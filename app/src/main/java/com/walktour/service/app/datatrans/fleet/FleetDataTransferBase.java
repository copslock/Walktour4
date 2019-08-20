package com.walktour.service.app.datatrans.fleet;

import com.dinglicom.btu.comlib;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.R;
import com.walktour.gui.listener.ServerStatus;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.txt.TestPlan;
import com.walktour.gui.task.parsedata.txt.TestPlan.TimeRange;
import com.walktour.service.app.DataTransService;
import com.walktour.service.app.datatrans.BaseDataTransfer;
import com.walktour.service.app.datatrans.model.UploadFileModel.UploadState;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Fleet服务器上传数据基础类
 *
 * @author jianchao.wang
 */
public abstract class FleetDataTransferBase extends BaseDataTransfer implements comlib.OnCallbackListener {
    /**
     * 上传库
     */
    protected comlib mLib;
    /**
     * 旧日期格式
     */
    private SimpleDateFormat mOldDF = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault());
    /**
     * 新日期格式
     */
    private SimpleDateFormat mNewDF = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());

    FleetDataTransferBase(String tag, DataTransService service) {
        super(ServerManager.SERVER_FLEET, tag, service, "fleet_upload.log");
        mLib = comlib.getInstance();
        mLib.setOnCallbackListener(this);
    }

    @Override
    public boolean init() {
        LogUtil.d(TAG, "--------init-----------");
        mServerMgr.sendTipBroadcast(mService.getString(R.string.server_connect_stat) + mServerMgr.getUploadFleetIp() + ":"
                + mServerMgr.getUploadFleetPort());
        String serverIp = mServerMgr.getUploadFleetIp();
        int serverPort = mServerMgr.getUploadFleetPort();
        super.mServerDescribe = serverIp + "_" + serverPort;
        String password = mServerMgr.getFleetPassword();
        String userId = "{" + MyPhoneState.getInstance().getGUID(mService) + "}";
        int cVer = 0;
        String sVer = "0";
        int syncTime = 0;
        int timeout = 6 * 1000 * 10;
        int result = mLib.initclient("", serverIp, password, userId, this.mLogPath, timeout,comlib.LOGIN_TYPE_FLEET, serverPort,
                cVer, sVer, syncTime);
        return result == comlib.DL_RET_OK;
    }

    @Override
    protected void uploadCurrentFileType() {
        LogUtil.d(TAG, "---------uploadCurrentFileType:" + this.mCurrentFileType.getFileTypeName() + "---------");
        UploadRcuParams params = this.createParams();
        if (params == null) {
            super.setFileTypeUploadState(UploadState.FILE_NO_FOUND);
            return;
        }
        // 等待网络可用
        while (!isStopTransfer && !MyPhoneState.getInstance().isNetworkAvirable(mService)) {
            mServerMgr.sendTipBroadcast(mService.getString(R.string.fleet_netIsOff));
            LogUtil.e(TAG, "---wait for network");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            int result = this.mLib.uploaddatafleet(params.path, params.serverFileName, params.tag, comlib.ENCODING_UTF8);
            switch (result) {
                case comlib.DL_RET_OK:
                case comlib.DL_RET_UPLOAD_FILE_HADUPLOAD:
                    mServerMgr.sendTipBroadcast(mService.getString(R.string.server_upload_success));
                    super.setFileTypeUploadState(UploadState.SUCCESS);
                    break;
                case comlib.DL_RET_UPLOAD_INTERRUPT:
                    super.setFileTypeUploadState(UploadState.INTERRUPT);
                    break;
                default:
                    mServerMgr.sendTipBroadcast(mService.getString(R.string.server_upload_fail));
                    super.setFileTypeUploadState(UploadState.FAILURE);
                    break;
            }
        } catch (Exception ex) {
            LogUtil.w(TAG,ex.getMessage());
        }
    }

    /**
     * 生成参数对象
     */
    protected UploadRcuParams createParams() {
        File file = super.mCurrentFile.getFile(super.mCurrentFileType);
        if (file == null)
            return null;
        UploadRcuParams params = new UploadRcuParams();
        params.path = file.getAbsolutePath();
        params.tag = this.getTag();
        params.serverFileName = this.formatFileName() + super.mCurrentFileType.getExtendName();
        if(super.mCurrentFileType== WalkStruct.FileType.ECTI){
            params.serverFileName =file.getName();
        }
        LogUtil.d(TAG,"UploadRcuParams="+params.toString());
        return params;

    }

    @Override
    public boolean uninit() {
        LogUtil.d(TAG, "-----uninit------");
        mLib.stopupload();
        mLib.destroyclient();
        return true;
    }

	@Override
	public boolean downloadTestTask(boolean force) {
		int flag = this.mLib.getconfig(0, AppFilePathUtil.getInstance().getAppConfigFile("test_task.txt").getAbsolutePath());
		if (flag == comlib.DL_RET_OK) {
			this.disposeTask();
			return true;
		}
		return false;
	}

    /**
     * 处理下载的任务
     */
    private void disposeTask() {
        boolean effect = TestPlan.getInstance().getTestPlanFromFile();
        if (effect) {
            // 每个时间段都有一组任务
            ArrayList<TimeRange> rangeList = TestPlan.getInstance().getTimeRangeList();
            TaskListDispose.getInstance().getTestPlanConfigFromFleet(rangeList);
            // ArrayList<TaskModel> taskList = new ArrayList<TaskModel>();
            // for (TimeRange r : rangeList) {
            // ArrayList<TaskModel> modelList = r.getTaskList();
            // for (TaskModel t : modelList) {
            // taskList.add(t);
            // }
            // }
            // if (taskList.size() > 0) {
            // // 添加测试任务模型列表到任务列表中
            // TaskListDispose taskListDispose = TaskListDispose.getInstance();
            // taskListDispose.replaceTaskList(taskList);
            // TaskListDispose.getInstance();
            // }
            mServerMgr.OnServerStatusChange(ServerStatus.configUpdateSuccess, "");
        } else {
            mServerMgr.OnServerStatusChange(ServerStatus.configUpdateFail, "");
        }
    }

    @Override
    public void onCallback(String data, int type) {

        LogUtil.d(TAG, "type="+type + ",data=" + data);

        switch (type) {
            case comlib.CMD_ClientConnect:
                boolean connectSuccess = data.equals("1");
                mServerMgr.sendTipBroadcast(connectSuccess ? mService.getString(R.string.server_connect_success)
                        : mService.getString(R.string.server_connect_fail));
                if (!connectSuccess) {
                    super.setFileTypeUploadState(UploadState.FAILURE);
                }
                break;

            case comlib.CMD_ClientLogin:
                boolean loginSuccess = data.equals("1");
                mServerMgr.sendTipBroadcast(loginSuccess ? mService.getString(R.string.server_login_success)
                        : (mService.getString(R.string.server_login_fail) + ","
                        + mService.getString(R.string.server_login_fail_reason)));
                if (!loginSuccess) {
                    super.setFileTypeUploadState(UploadState.FAILURE);
                }
                break;
            case comlib.CMD_DATA_UploadStart:
                break;
            case comlib.CMD_DATA_UPLOAD_TransInfo:
                long currentSize = getResponseResult(data, "curlen");
                long totalSize = getResponseResult(data, "totallen");
                super.setUploadProgress((int) (currentSize * 100 / totalSize));
                break;
            default:
                break;
        }

    }

    /**
     * 格式化服务器端需要的文件名
     *
     * @return 不带后缀名的文件名称
     */
    protected String formatFileName() {
        String fileName = this.mCurrentFile.getName();
        System.out.println(TAG + " fileName:" + fileName);
        String data;
        boolean isCQT = false;
        int start;
        if (fileName.contains("IN")) {
            isCQT = true;
            start = fileName.indexOf("IN") + 2;
            data = fileName.substring(start, start + 15);
        } else if (fileName.contains("OUT")) {
            start = fileName.indexOf("OUT") + 3;
            data = fileName.substring(start, start + 15);
        } else {
            return fileName;
        }
        String taskType;
        if (fileName.indexOf("_Port") > 0) {
            taskType = fileName.substring(fileName.lastIndexOf("_Port") + 1);
        } else {
            if (isCQT) {
                taskType = fileName.substring(fileName.indexOf("IN") + 18);
            } else {
                taskType = fileName.substring(fileName.indexOf("OUT") + 19);
            }
        }
        //文件名前缀，一般用来放设备名或工单测试的调用名
        String fileNamePrefix = "";
        if (fileName.indexOf("IN") > 0) {
            fileNamePrefix = fileName.substring(0, fileName.indexOf("-IN"));
        } else if (fileName.indexOf("OUT") > 0) {
            fileNamePrefix = fileName.substring(0, fileName.indexOf("-OUT"));
        }
        StringBuilder name = new StringBuilder();
        if (fileNamePrefix.length() > 0)
            name.append(fileNamePrefix).append("-");
        name.append(taskType).append("-").append(isCQT ? "IN" : "OUT").append("-");
        name.append(this.formatDateString(data));
        LogUtil.d(TAG, " formatFileName:" + name.toString());
        return name.toString();
    }

    /**
     * 转换日期格式
     *
     * @param dateStr 旧日期格式
     * @return 新日期格式
     */
    String formatDateString(String dateStr) {
        try {
            return this.mNewDF.format(this.mOldDF.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr;
        }
    }

    /**
     * 获得要上传的tag
     */
    protected abstract String getTag();

    /**
     * 获得返回结果
     *
     * @param respMsg 应答消息
     * @param key     键值
     * @return 结果值
     */
    private long getResponseResult(String respMsg, String key) {
        String[] lines = respMsg.split("\n");
        long result = 0;
        try {
            for (String s : lines) {
                if (s.toLowerCase(Locale.getDefault()).startsWith(key.toLowerCase(Locale.getDefault()) + "=")) {
                    result = Long.parseLong(s.split("=")[1].trim());
                    break;
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        return result;
    }

    @Override
    public boolean sendEvent() {
        return false;
    }

    @Override
    public boolean syncTime() {
        return false;
    }

    /**
     * 上传的接口参数对象
     *
     * @author jianchao.wang
     */
    protected class UploadRcuParams {
        /**
         * 标识
         */
        public String tag;
        /**
         * 上传的文件路径
         */
        public String path;
        /**
         * 服务器端名字
         */
        String serverFileName;

        @Override
        public String toString() {
            return "UploadRcuParams{" +
                    "tag='" + tag + '\'' +
                    ", path='" + path + '\'' +
                    ", serverFileName='" + serverFileName + '\'' +
                    '}';
        }
    }

    @Override
    protected void interruptUploading() {
        this.mLib.stopupload();
    }

    @Override
    protected boolean uploadParamsReport(String msg) {
        LogUtil.d(TAG, "----uploadParamsReport:" + msg + "----");
        int ack = this.mLib.uploadrealtimemsg(msg, 1);
        return (ack == comlib.DL_RET_OK);
    }

}
