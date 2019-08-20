package com.walktour.service.app.datatrans;

import android.text.TextUtils;

import com.dinglicom.btu.comlib;
import com.dinglicom.data.model.TestRecord;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.R;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.data.model.DBManager;
import com.walktour.gui.listener.ServerStatus;
import com.walktour.gui.listener.ServerStatusListener;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.xml.btu.TaskConverter;
import com.walktour.gui.task.parsedata.xml.btu.model.TestScheme;
import com.walktour.service.app.DataTransService;
import com.walktour.service.app.datatrans.model.UploadFileModel.UploadState;

import java.io.File;
import java.util.ArrayList;

/***
 * Btu下载测试任务.
 * 
 * @author weirong.fan
 *
 */
public class AtuDataTransfer extends BaseDataTransfer implements ServerStatusListener, comlib.OnCallbackListener {
	private static final String TAG = "AtuDataTransfer";
	/** 服务器IP */
	private String mServerIp;
	/** 服务器端口 */
	private int mPort;
	/** 服务器用户ID */
	private String mUserId;
	/** 服务器密码 */
	private String mPassword;
	/** 上传库 */
	private comlib mLib = null;
	/** 进度监听线程 */
	private ProgressThread mThread;
	/** 默认是上传文件操作 **/
	private boolean isUploadFile = false;
	/**标识是否上传完文件*/
	private boolean isUploadFinish=false;
	/** 默认是没有销毁 **/
	private boolean isDestroy = false;

	public AtuDataTransfer(DataTransService service) {
		super(ServerManager.SERVER_ATU, TAG, service, "atu_upload.log");
		mLib = comlib.getInstance();
		mLib.setOnCallbackListener(this);
	}

	@Override
	protected boolean uploadParamsReport(String msg) {
		return false;
	}

	@Override
	protected void interruptUploading() {
		try {
			if (null != this.mLib) {
				this.mLib.stopupload();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected boolean init() {
		LogUtil.d(TAG, "--------init-----------");
		onStatusChange(ServerStatus.loginStart, "");
		this.mServerIp = mServerMgr.getDTLogIp();
		this.mPort = mServerMgr.getDTLogPort();
		this.mUserId = mServerMgr.getDTLogBoxId();
		this.mPassword = mServerMgr.getDTLogNewPwd();
		super.mServerDescribe = this.mServerIp + "_" + this.mPort;
		return this.loginServer();
	}

	/**
	 * 登陆服务器
	 * 
	 * @return
	 */
	private boolean loginServer() {
		String pwPath = String.format(ServerManager.FILE_ATU_PWD, mUserId);
		mServerMgr.sendTipBroadcast(mService.getString(R.string.server_start_login));
		// 初始密码为空，但库不接受空密码
		mPassword = mPassword.length() == 0 ? "" : mPassword;
		int result = comlib.DL_RET_ERR;
		try {
			LogUtil.w(TAG, "start mlib initclient");
			result = mLib.initclient(pwPath, mServerIp, "", mUserId, mLogPath,60000, comlib.LOGIN_TYPE_ATU, mPort, 0, "0", 0);
			LogUtil.w(TAG, "end mlib initclient");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// 密码错误时尝试使用旧密码重新登录
		if (result == comlib.DL_RET_LOGIN_ERR_PW) {
			try {
				LogUtil.w(TAG, "start mlib initclient");
				result = mLib.initclient(pwPath, mServerIp, "", mUserId, mLogPath,60000, comlib.LOGIN_TYPE_ATU, mPort, 0, "0",
						0);
				LogUtil.w(TAG, "end mlib initclient");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		LogUtil.i(TAG, "---login result:" + result);

		if (result == comlib.DL_RET_OK) {
			mServerMgr.sendTipBroadcast(mService.getString(R.string.server_login_success));
			onStatusChange(ServerStatus.loginSuccess, "");
			return true;
		}
		onStatusChange(ServerStatus.loginFail, "");
		String reason = mService.getString(R.string.server_login_fail);
		if (result == comlib.DL_RET_LOGIN_ERR_PW) {
			reason += ":" + mService.getString(R.string.server_login_fail_wrong_pass);
		} else {
			reason += ":" + mService.getString(R.string.server_login_fail_reason);
		}
		mServerMgr.sendTipBroadcast(reason);

		if (result == comlib.DL_RET_LOGIN_ERR_ALREADY) {

			int interval = (int) (System.currentTimeMillis() - super.getLastDisconnectTime()) / 1000;
			interval = (interval > 0 && interval < 150) ? interval : 0;
			// 平台提示已经登录时，等待超时时间后(150秒)， 再重新登录
			for (int i = 150 - interval; i > 0 && !isStopTransfer; i--) {
				LogUtil.e(TAG, "Wait For Server's timeout:" + i);
				String msg = String.format(mService.getString(R.string.server_file_wait_timeout), i);
				mServerMgr.sendTipBroadcast(msg);
				try {
					Thread.sleep(1 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return false;
		}

		return false;
	}

	@Override
	protected void uploadCurrentFileType() {
		isUploadFile = true;
		int result = -1;
		File file = super.mCurrentFile.getFile(super.mCurrentFileType);
		TestRecord testRecord = DBManager.getInstance(WalktourApplication.getAppContext()).getTestRecord(mCurrentFile.getTestRecordId());
		int portId = mServerMgr.getDTLogMoudleNum();
		if(null != testRecord && !TextUtils.isEmpty(testRecord.port_id)){
			try {
				portId = Integer.parseInt(testRecord.port_id);
			}catch (Exception e){
				LogUtil.e(TAG,e.getMessage());
			}
		}
		String remoteFileName = this.getRemoteFileName(mServerMgr.getDTLogBoxId(), portId,
				file);
		this.mThread = new ProgressThread(file.length());
		this.mThread.start();
		LogUtil.w(TAG, "start mlib uploaddata");
		// result = mLib.uploaddata(file.getAbsolutePath(), remoteFileName, 1);
		result = mLib.uploadzipdata(file.getAbsolutePath(), remoteFileName, 0, 1);
		LogUtil.w(TAG, "end mlib uploaddata");
		LogUtil.d(TAG, "---upload result:" + result);
		this.mThread.stopThread();
		this.mThread = null;
		switch (result) {
		case comlib.DL_RET_OK:
		case comlib.DL_RET_UPLOAD_FILE_HADUPLOAD:
			super.setFileTypeUploadState(UploadState.SUCCESS);
			break;
		case comlib.DL_RET_UPLOAD_INTERRUPT:
			super.setFileTypeUploadState(UploadState.INTERRUPT);
			break;
		default:
			super.setFileTypeUploadState(UploadState.FAILURE);
			break;
		}
	}

	/**
	 * 上传进度监听线程
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class ProgressThread extends Thread {
		/** 文件大小 */
		private long mTotalSize;
		/** 是否停止当前线程 */
		private boolean isStop = false;

		public ProgressThread(long totalSize) {
			this.mTotalSize = totalSize;
		}

		@Override
		public void run() {

			while (!isStop) {
				long uploadSize = mLib.getuploadedsize();
				LogUtil.d(TAG, String.format("upload %d/%d", uploadSize, mTotalSize));
				int process = (int) (uploadSize * 100 / mTotalSize);
				if(process>=100)
					isUploadFinish=true;
				else
					isUploadFinish=false;
				setUploadProgress(process >= 100 ? 100 : process);
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * 停止线程
		 */
		public void stopThread() {
			this.isStop = true;
			try {
				this.interrupt();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	@Override
	protected boolean syncTime() {
		return false;
	}

	@Override
	public boolean downloadTestTask(boolean forceReplace) {
		ArrayList<TaskModel> taskList = null;
		File file = new File(ServerManager.FILE_ATU_TASK);
		LogUtil.w(TAG, "start mlib getconfig");
		isUploadFile = false;
		int result = this.mLib.getconfig(forceReplace ? 0 : mServerMgr.getDTLogCVersion(), ServerManager.FILE_ATU_TASK);
		LogUtil.w(TAG, "end mlib getconfig");
		if (result == comlib.DL_RET_OK) {
			TaskConverter converter = new TaskConverter(mService, file);
			int taskVersion = converter.getTaskVersion();
			if (taskVersion > 0) {
				mServerMgr.setDTLogCVersion(converter.getTaskVersion());
				mServerMgr.setDTLogScheme(0);
				// 默认先取第一个测试方案
				ArrayList<TestScheme> schemeList = converter.convertTestScheme();
				if (schemeList.size() > 0) {
					TestScheme scheme = schemeList.get(0);
					taskList = scheme.getCommandList();
					mServerMgr.setDTLogMoudleNum(scheme.getMoudleNum());
				}
				// 替换测试任务模型列表到任务列表中
				if(null!=taskList&&taskList.size()>0){
					TaskListDispose.getInstance().getTestPlanConfigFromAtu(taskList);
				}
				onStatusChange(ServerStatus.configUpdateSuccess, "");
			} else {
				onStatusChange(ServerStatus.configUpdateFail, "");
			}
			// 下载完成后同步时间
			super.finishDownloadTestTask();
		}

		return result == comlib.DL_RET_OK ? true : false;
	}

	@Override
	protected boolean sendEvent() {
		return false;
	}

	@Override
	protected boolean uninit() {
		LogUtil.d(TAG, "-----uninit------");
		try {
			if (null != mLib) {
				if (isUploadFile&&!isUploadFinish) {//是上传文件操作,并且没有传完.
					LogUtil.w(TAG, "start mlib stopupload");
					mLib.stopupload();
					LogUtil.w(TAG, "end mlib stopupload");
				}
				LogUtil.w(TAG, "start mlib destroyclient");
				if (!isDestroy) {
					mLib.destroyclient();
					isDestroy = true;
				}
				LogUtil.w(TAG, "end mlib destroyclient");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}

	@Override
	protected void initCurrentFileTypes() {
	}

	@Override
	public void onCompressProgress(String filename, int progress) {

	}

	@Override
	public void onStatusChange(ServerStatus status, String info) {
		if (null != mServerMgr)
			mServerMgr.OnServerStatusChange(status, info);
	}

	@Override
	public void onCallback(String data, int type) {
		LogUtil.d(TAG, "onCallback:" + type + "," + data);
		try {
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

			default:
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
