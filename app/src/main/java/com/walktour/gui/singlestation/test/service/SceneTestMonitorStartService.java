package com.walktour.gui.singlestation.test.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.gui.model.ServiceMessage;
import com.walktour.base.gui.service.BaseStartService;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.SceneInfo;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.TaskTestResult;
import com.walktour.gui.singlestation.dao.model.ThresholdTestResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 场景测试监听类，用于判断测试是否通过
 * Created by wangk on 2017/6/29.
 */

public class SceneTestMonitorStartService extends BaseStartService {
    /**
     * 日志标识
     */
    private static final String TAG = "SceneTestMonitorStartService";
    /**
     * 广播信息，测试结束，用于场景测试界面刷新
     */
    public static final String MESSAGE_TEST_FINISH = "com.dinglicom.scene.test.finish";
    /**
     * 数据库操作类
     */
    private SingleStationDaoManager mDaoManager;
    /**
     * 场景要测试的结果列表
     */
    private List<TaskTestResult> mTestResultList = new ArrayList<>();
    /**
     * 广播接收
     */
    private MyReceiver mReceiver;
    /**
     * 外泄测试获取到
     */
    private List<Float> mRSRPList = new ArrayList<>();
    /**
     * 测试过程中的RSRP的汇总值
     */
    private double mRsrpSum = 0;
    /**
     * 测试过程中的SINR的汇总值
     */
    private double mSinrSum = 0;
    /**
     * 记录RSRP的次数
     */
    private int mRsrpTimeCount = 0;
    /**
     * 记录SINR次数
     */
    private int mSinrTimeCount = 0;
    /**
     * 上一次记录的时间，每秒记录一次
     */
    private long mLastTime = 0;
    /**
     * 当前测试的场景
     */
    private SceneInfo mSceneInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mDaoManager = SingleStationDaoManager.getInstance(this.getApplicationContext());
        this.mReceiver = new MyReceiver();
        this.registerReceiver();
    }

    /**
     * 注册广播接听器
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.NOTIFY_TEST_FILE_CREATED);
        filter.addAction(TotalDataByGSM.TotalParaDataChanged);
        this.registerReceiver(mReceiver, filter);
    }

    /**
     * 注册广播接听器
     */
    private void unregisterReceiver() {
        this.unregisterReceiver(mReceiver);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d(getLogTAG(), "action = " + intent.getAction());
            if (intent.getAction() == null)
                return;
            if (intent.getAction().equals(WalkMessage.NOTIFY_TEST_FILE_CREATED)) {
                String recordId = intent.getStringExtra("record_id");
                checkTaskTestIsPass(recordId);
            } else if (intent.getAction().equals(TotalDataByGSM.TotalParaDataChanged)) {
                if (System.currentTimeMillis() - mLastTime > 1000) {
                    mLastTime = System.currentTimeMillis();
                    String earfcn = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_EARFCN);
                    String pci = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_PCI);
                    String cellList = TraceInfoInterface.getParaValue(UnifyParaID.LTE_CELL_LIST);
                    LogUtil.d(TAG, "----earfcn:" + earfcn + "---");
                    LogUtil.d(TAG, "----pci:" + pci + "---");
                    LogUtil.d(TAG, "----cellList:" + cellList + "---");
                    String[] ltes = cellList.split(";");
                    for (String s : ltes) {
                        String[] ltefs = s.split(",");
                        if (ltefs.length > 3) {
                            if (earfcn.equals(ltefs[0]) && pci.equals(ltefs[1])) {
                                if (StringUtil.isFloat(ltefs[2])) {
                                    mRSRPList.add(Float.parseFloat(ltefs[2]));
                                }
                            }
                        }
                    }
                    String rsrp = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_RSRP);
                    String sinr = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_SINR);
                    if (StringUtil.isDouble(rsrp)) {
                        mRsrpSum += Double.parseDouble(rsrp);
                        mRsrpTimeCount++;
                    }
                    if (StringUtil.isDouble(sinr)) {
                        mSinrSum += Double.parseDouble(sinr);
                        mSinrTimeCount++;
                    }

                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(null, flags, startId);
        }
        this.mSceneInfo = intent.getParcelableExtra("scene_info");
        this.mTestResultList.clear();
        String resultIds = intent.getStringExtra("result_ids");
        if (!StringUtil.isNullOrEmpty(resultIds)) {
            String[] ids = resultIds.split(",");
            for (String id : ids) {
                this.mTestResultList.add(this.mDaoManager.getTaskTestResult(Long.parseLong(id)));
            }
            this.mRSRPList.clear();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(this.getLogTAG(), "----onDestroy----");
        this.unregisterReceiver();
    }

    /**
     * 检测业务测试是否通过
     *
     * @param recordId 生成的测试文件的数据管理记录ID
     */
    private void checkTaskTestIsPass(String recordId) {
        LogUtil.d(this.getLogTAG(), "----checkTaskTestIsPass----recordId:" + recordId + " , sceneName = " + mSceneInfo.getSceneName()/*+ "----taskType:" + taskType + "----taskName:" + taskName*/);
        StationInfo stationInfo = this.mDaoManager.getStationInfo(this.mSceneInfo.getStationId());
        String recordTaskNames = "";
        for (TaskTestResult testTask : mTestResultList) {
            recordTaskNames = recordTaskNames + (TextUtils.isEmpty(recordTaskNames) ? "" : ",") + testTask.getTaskType();
        }
        for (TaskTestResult taskTestResult : this.mTestResultList) {
            if (!taskTestResult.getThresholdTestResultList().isEmpty()) {
                boolean isCheck;
                if (stationInfo.getType() == SingleStationDaoManager.STATION_TYPE_INDOOR) {
                    isCheck = checkIndoorTaskTestIsPass(taskTestResult);
                } else {
                    isCheck = checkOutdoorTaskTestIsPass(taskTestResult);
                }
                if (isCheck) {
                    mSceneInfo.setRecordTaskNames(recordTaskNames);
                    mSceneInfo.setRecordId(recordId);
                    mDaoManager.save(mSceneInfo);
                    this.setTaskTestResultStatus(taskTestResult);
                    this.setStationTestStatus();
                }
            }
        }
        if (this.mTestResultList.isEmpty()) {
            ServiceMessage message = new ServiceMessage(MESSAGE_TEST_FINISH);
            super.sendMessageToActivity(message);
            this.stopSelf();
        }
    }

    /**
     * 设置业务测试状态
     *
     * @param taskTestResult 业务测试结果对象
     */
    private void setTaskTestResultStatus(TaskTestResult taskTestResult) {
        boolean isPass = true;
        for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList()) {
            if (thresholdTestResult.getTestStatus() != ThresholdTestResult.TEST_STATUS_PASS) {
                isPass = false;
                break;
            }
        }
        taskTestResult.setTestStatus(isPass ? TaskTestResult.TEST_STATUS_PASS : TaskTestResult.TEST_STATUS_FAULT);
        if (this.mRsrpTimeCount > 0) {
            taskTestResult.setRsrpAverage((int) (this.mRsrpSum / this.mRsrpTimeCount));
            this.mRsrpSum = 0;
            this.mRsrpTimeCount = 0;
        }
        if (this.mSinrTimeCount > 0) {
            taskTestResult.setSinrAverage((int) (this.mSinrSum / this.mSinrTimeCount));
            this.mSinrSum = 0;
            this.mSinrTimeCount = 0;
        }
        this.mDaoManager.save(taskTestResult);
    }

    /**
     * 设置当前基站的测试状态
     */
    private void setStationTestStatus() {
        StationInfo stationInfo = this.mDaoManager.getStationInfo(this.mSceneInfo.getStationId());
        List<SceneInfo> sceneInfoList = stationInfo.getSceneInfoList();
        int passCount = 0;//测试通过统计
        int faultCount = 0;//测试失败统计
        int initCount = 0;//未测试统计
        int totalCount = 0;//总测试业务数
        for (SceneInfo sceneInfo : sceneInfoList) {
            for (TaskTestResult taskTestResult : sceneInfo.getTaskTestResultList()) {
                totalCount++;
                switch (taskTestResult.getTestStatus()) {
                    case TaskTestResult.TEST_STATUS_PASS:
                        passCount++;
                        break;
                    case TaskTestResult.TEST_STATUS_FAULT:
                        faultCount++;
                        break;
                    default:
                        initCount++;
                        break;
                }
            }
        }
        if (passCount == totalCount) {
            stationInfo.setTestStatus(StationInfo.TEST_STATUS_SUCCESS);
        } else if (initCount == totalCount) {
            stationInfo.setTestStatus(StationInfo.TEST_STATUS_INIT);
        } else if (initCount > 0) {
            stationInfo.setTestStatus(StationInfo.TEST_STATUS_TESTING);
        } else if (initCount == 0 && faultCount > 0) {
            stationInfo.setTestStatus(StationInfo.TEST_STATUS_FAULT);
        }
        this.mDaoManager.save(stationInfo);
    }

    /**
     * 检查室外宏站测试是否通过
     *
     * @param result 测试结果对象
     * @return 是否有结果需要检查
     */
    private boolean checkOutdoorTaskTestIsPass(TaskTestResult result) {
        LogUtil.d(this.getLogTAG(), "----checkOutdoorTaskTestIsPass----taskType:" + result.getTaskTypeName());
        boolean isCheck = false;
        switch (this.mSceneInfo.getSceneType()) {
            case SingleStationDaoManager.SCENE_TYPE_COVERAGE:
                if (result.getTaskType().equals("FTP_Download")) {
                    this.checkOutdoorCoverageFTPDownload(result.getThresholdTestResultList());
                    isCheck = true;
                }
                break;
            case SingleStationDaoManager.SCENE_TYPE_PERFORMANCE:
                if (result.getTaskType().equals("Attach")) {
                    this.checkPerformanceAttach(result.getThresholdTestResultList());
                    isCheck = true;
                } else if (result.getTaskType().equals("FTP_Upload")) {
                    this.checkOutdoorPerformanceFTPUpload(result.getThresholdTestResultList());
                    isCheck = true;
                } else if (result.getTaskType().equals("FTP_Download")) {
                    this.checkOutdoorPerformanceFTPDownload(result.getThresholdTestResultList());
                    isCheck = true;
                } else if (result.getTaskType().startsWith("MOC")) {
                    this.checkOutdoorPerformanceMOC(result.getThresholdTestResultList());
                    isCheck = true;
                }
                break;
            default:
        }
        return isCheck;
    }

    /**
     * 检查室内基站测试是否通过
     *
     * @param result 测试结果对象
     * @return 是否有结果需要检查
     */

    private boolean checkIndoorTaskTestIsPass(TaskTestResult result) {
        LogUtil.d(this.getLogTAG(), "----checkIndoorTaskTestIsPass----taskType:" + result.getTaskTypeName());
        boolean isCheck = false;
        switch (this.mSceneInfo.getSceneType()) {
            case SingleStationDaoManager.SCENE_TYPE_COVERAGE:
                if (result.getTaskType().equals("FTP_Download")) {
                    this.checkIndoorCoverageFTPDownload(result.getThresholdTestResultList());
                    isCheck = true;
                }
                break;
//            case SingleStationDaoManager.SCENE_TYPE_PARK:
            case SingleStationDaoManager.SCENE_TYPE_HANDOVER:
                if (result.getTaskType().equals("FTP_Download")) {
                    this.checkIndoorHandoverFTPDownload(result.getThresholdTestResultList());
                    isCheck = true;
                } else if (result.getTaskType().startsWith("MOC")) {
                    this.checkIndoorHandoverMOC(result.getThresholdTestResultList());
                    isCheck = true;
                }
                break;
            case SingleStationDaoManager.SCENE_TYPE_SIGNAL_LEAKAGE:
                if (result.getTaskType().equals("Idle")) {
                    this.checkIndoorSignalLeakageIdle(result.getThresholdTestResultList());
                    isCheck = true;
                }
                break;
            case SingleStationDaoManager.SCENE_TYPE_PERFORMANCE:
                if (result.getTaskType().equals("Attach")) {
                    this.checkPerformanceAttach(result.getThresholdTestResultList());
                    isCheck = true;
                } else if (result.getTaskType().equals("FTP_Upload")) {
                    this.checkIndoorPerformanceFTPUpload(result.getThresholdTestResultList());
                    isCheck = true;
                } else if (result.getTaskType().equals("FTP_Download")) {
                    this.checkIndoorPerformanceFTPDownload(result.getThresholdTestResultList());
                    isCheck = true;
                } else if (result.getTaskType().startsWith("MOC")) {
                    this.checkIndoorPerformanceMOC(result.getThresholdTestResultList());
                    isCheck = true;
                }
                break;
            default:
        }
        return isCheck;
    }

    /**
     * 判断室内基站切换测试的主叫业务
     *
     * @param resultList 判断阈值列表
     */
    private void checkIndoorHandoverMOC(List<ThresholdTestResult> resultList) {
        LogUtil.d(this.getLogTAG(), "----checkIndoorHandoverMOC----size:" + resultList.size());
        for (ThresholdTestResult result : resultList) {
            if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._FTPAttemptHandoverTimes.name())) {
                HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getEvent();
                String valueStr = TotalDataByGSM.getHashMapValue(hMap, TotalStruct.TotalEvent._lteHandOverReq.name());
                float value = 0;
                if (!TextUtils.isEmpty(valueStr)) {
                    try {
                        value = Float.valueOf(valueStr);
                    } catch (Exception e) {
                        LogUtil.e(getLogTAG(), e.getMessage());
                    }
                }
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            } else if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._FTPHandoverSuccessRate.name())) {
                HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getEvent();
                String valueStr = TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalEvent._lteHandOverSucss.name(),
                        TotalStruct.TotalEvent._lteHandOverReq.name(), 100, "%");
                float value = 0;
                if (!TextUtils.isEmpty(valueStr)) {
                    try {
                        String valueStrReal = valueStr.substring(0, valueStr.length() - 1);//去除字符串最后面的%号
                        value = Float.valueOf(valueStrReal);
                    } catch (Exception e) {
                        LogUtil.e(getLogTAG(), e.getMessage());
                    }
                }
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            }
        }
    }

    /**
     * 判断宏站覆盖测试的FTP下载业务
     *
     * @param resultList 判断阈值列表
     */
    private void checkOutdoorCoverageFTPDownload(List<ThresholdTestResult> resultList) {
        LogUtil.d(this.getLogTAG(), "----checkOutdoorCoverageFTPDownload----size:" + resultList.size());
        for (ThresholdTestResult result : resultList) {
            if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._FTPCoverMileage.name())) {
                float count = this.getTotalValue(TotalStruct.TotalDial._LTErsrp.name());
                float total = this.getTotalValue(TotalStruct.TotalDial._LTErsrpCount.name());
                float value = count / total * 100;
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            } else if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._SpeedAverage.name())) {
                float downSize = this.getTotalValue(TotalStruct.TotalFtp._downCurrentSize.name());
                float downTime = this.getTotalValue(TotalStruct.TotalFtp._downCurrentTimes.name());
                float value = downSize / downTime / 1024;
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            }
        }
    }

    /**
     * 判断宏站性能测试的Attach业务
     *
     * @param resultList 判断阈值列表
     */
    private void checkPerformanceAttach(List<ThresholdTestResult> resultList) {
        LogUtil.d(this.getLogTAG(), "----checkPerformanceAttach----size:" + resultList.size());
        for (ThresholdTestResult result : resultList) {
            if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._tryTimes.name())) {
                float value = this.getTotalValue(TotalStruct.TotalAttach._attachRequest.name());
                value += this.getTotalValue(TotalStruct.TotalAttach._lteAttachRequest.name());
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            } else if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._successRate.name())) {
                float tryTimes = this.getTotalValue(TotalStruct.TotalAttach._attachRequest.name());
                tryTimes += this.getTotalValue(TotalStruct.TotalAttach._lteAttachRequest.name());
                float successTimes = this.getTotalValue(TotalStruct.TotalAttach._attachSuccess.name());
                successTimes += this.getTotalValue(TotalStruct.TotalAttach._lteAttachSuccess.name());
                float value = 0;
                if (tryTimes > 0) {
                    value = successTimes / tryTimes * 100;
                }
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            }
        }
    }

    /**
     * 获取实时统计参数值
     *
     * @param name 参数名称
     * @return 参数值
     */
    private float getTotalValue(String name) {
        HashMap<String, Long> paras = TotalDataByGSM.getInstance().getUnifyTimes();
        float value = 0;
        try {
            value = Float.parseFloat(TotalDataByGSM.getHashMapValue(paras, name));
        } catch (Exception e) {
            //
        }
        return value;
    }

    /**
     * 判断宏站性能测试的FTP上传业务
     *
     * @param resultList 判断阈值列表
     */
    private void checkOutdoorPerformanceFTPUpload(List<ThresholdTestResult> resultList) {
        LogUtil.d(this.getLogTAG(), "----checkOutdoorPerformanceFTPUpload----size:" + resultList.size());
        for (ThresholdTestResult result : resultList) {
            if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._SpeedAverage.name())) {
                float upSize = this.getTotalValue(TotalStruct.TotalFtp._upCurrentSize.name());
                float upTime = this.getTotalValue(TotalStruct.TotalFtp._upCurrentTimes.name());
                float value = upSize / upTime / 1024;
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            }
        }
    }

    /**
     * 判断宏站性能测试的FTP下载业务
     *
     * @param resultList 判断阈值列表
     */
    private void checkOutdoorPerformanceFTPDownload(List<ThresholdTestResult> resultList) {
        LogUtil.d(this.getLogTAG(), "----checkOutdoorPerformanceFTPDownload----size:" + resultList.size());
        for (ThresholdTestResult result : resultList) {
            if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._SpeedAverage.name())) {
                float downSize = this.getTotalValue(TotalStruct.TotalFtp._downCurrentSize.name());
                float downTime = this.getTotalValue(TotalStruct.TotalFtp._downCurrentTimes.name());
                float value = downSize / downTime / 1024;
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            }
        }
    }

    /**
     * 判断宏站性能测试的语音主叫业务
     *
     * @param resultList 判断阈值列表
     */
    private void checkOutdoorPerformanceMOC(List<ThresholdTestResult> resultList) {
        LogUtil.d(this.getLogTAG(), "----checkOutdoorPerformanceMOC----size:" + resultList.size());
        for (ThresholdTestResult result : resultList) {
            if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._csfb_tryTimes.name())) {
                float value = this.getTotalValue(TotalStruct.TotalDial._csfb_mo_request.name());
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            } else if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._csfb_successRate.name())) {
                float tryTimes = this.getTotalValue(TotalStruct.TotalDial._csfb_mo_request.name());
                float successTimes = this.getTotalValue(TotalStruct.TotalDial._csfb_mo_CallEnd.name());
                float value = successTimes / tryTimes * 100;
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            } else if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._csfb_establishedRate.name())) {
                float tryTimes = this.getTotalValue(TotalStruct.TotalDial._csfb_mo_request.name());
                float connectTimes = this.getTotalValue(TotalStruct.TotalDial._csfb_mo_Established.name());
                float value = connectTimes / tryTimes * 100;
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            } else if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._volte_successRate.name())) {
                float tryTimes = this.getTotalValue(TotalStruct.TotalDial._volte_moTrys.name());
                float connectTimes = this.getTotalValue(TotalStruct.TotalDial._volte_moConnects.name());
                float value = tryTimes == 0 ? 0 : connectTimes / tryTimes * 100;
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            } else if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._volte_eSRVCC.name())) {
                HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getEvent();
                String valueStr = TotalDataByGSM.getHashMapValue(hMap, TotalStruct.TotalEvent._lteTogsmHandoverSuccess.name());
                float value = 0;
                if (!TextUtils.isEmpty(valueStr)) {
                    try {
                        value = Float.valueOf(valueStr);
                    } catch (Exception e) {
                        LogUtil.e(getLogTAG(), e.getMessage());
                    }
                }
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            }
        }
    }

    /**
     * 判断室内基站性能测试的语音主叫业务
     *
     * @param resultList 判断阈值列表
     */
    private void checkIndoorPerformanceMOC(List<ThresholdTestResult> resultList) {
        LogUtil.d(this.getLogTAG(), "----checkIndoorPerformanceMOC----size:" + resultList.size());
        for (ThresholdTestResult result : resultList) {
            if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._csfb_tryTimes.name())) {
                float value = this.getTotalValue(TotalStruct.TotalDial._csfb_mo_request.name());
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            } else if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._csfb_successRate.name())) {
                float tryTimes = this.getTotalValue(TotalStruct.TotalDial._csfb_mo_request.name());
                float connectTimes = this.getTotalValue(TotalStruct.TotalDial._csfb_mo_Established.name());
                float value = connectTimes / tryTimes * 100;
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            } else if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._csfb_returnDelay.name())) {
                //返回4G时延
                HashMap<String, Long> unifyTimes = TotalDataByGSM.getInstance().getUnifyTimes();
                String valueStr2G = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalStruct.TotalDial._csfb_mo_2G_ReturnLTE_Delay.name(),
                        TotalStruct.TotalDial._csfb_mo_2G_ReturnLTE.name(), 0.001f, "");
                String valueStr3G = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalStruct.TotalDial._csfb_mo_3G_ReturnLTE_Delay.name(),
                        TotalStruct.TotalDial._csfb_mo_3G_ReturnLTE.name(), 0.001f, "");
                float value;
                float value2G = 0;
                float value3G = 0;
                if (!TextUtils.isEmpty(valueStr2G)) {
                    try {
                        value2G = Float.valueOf(valueStr2G);
                    } catch (Exception e) {
                        LogUtil.e(getLogTAG(), e.getMessage());
                    }
                }

                if (!TextUtils.isEmpty(valueStr3G)) {
                    try {
                        value3G = Float.valueOf(valueStr3G);
                    } catch (Exception e) {
                        LogUtil.e(getLogTAG(), e.getMessage());
                    }
                }
                value = value2G == 0 || value3G == 0 ? value2G + value3G : (value2G + value3G) / 2;
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            } else if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._csfb_connectDelay.name())) {
                //CSFB接通时延
                HashMap<String, Long> unifyTimes = TotalDataByGSM.getInstance().getUnifyTimes();
                String valueStr = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalStruct.TotalDial._csfb_mo_SuccessDelay.name(),
                        TotalStruct.TotalDial._csfb_mo_Established.name(), 0.001f, "");
                float value = 0;
                if (!TextUtils.isEmpty(valueStr)) {
                    try {
                        value = Float.valueOf(valueStr);
                    } catch (Exception e) {
                        LogUtil.e(getLogTAG(), e.getMessage());
                    }
                }
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            } else if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._csfb_returnSuccessRate.name())) {
                //返回4G成功率
                HashMap<String, Long> unifyTimes = TotalDataByGSM.getInstance().getUnifyTimes();
                String valueStr = TotalDataByGSM.getHashMapMultiple(unifyTimes, TotalStruct.TotalDial._csfb_mo_CallEnd.name(),
                        TotalStruct.TotalDial._csfb_mo_request.name(), 100, "%");
                float value = 0;
                if (!TextUtils.isEmpty(valueStr)) {
                    try {
                        String valueReal = valueStr.substring(0, valueStr.length() - 1);
                        value = Float.valueOf(valueReal);
                    } catch (Exception e) {
                        LogUtil.e(getLogTAG(), e.getMessage());
                    }
                }
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            } else if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._volte_successRate.name())) {
                float tryTimes = this.getTotalValue(TotalStruct.TotalDial._volte_moTrys.name());
                float connectTimes = this.getTotalValue(TotalStruct.TotalDial._volte_moConnects.name());
                float value = tryTimes == 0 ? 0 : connectTimes / tryTimes * 100;
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            }
        }
    }

    /**
     * 判断室内基站性能测试的FTP下载业务
     *
     * @param resultList 判断阈值列表
     */
    private void checkIndoorPerformanceFTPDownload(List<ThresholdTestResult> resultList) {
        LogUtil.d(this.getLogTAG(), "----checkIndoorPerformanceFTPDownload----size:" + resultList.size());
        for (ThresholdTestResult result : resultList) {
            if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._SpeedAverage.name())) {
                float downSize = this.getTotalValue(TotalStruct.TotalFtp._downCurrentSize.name());
                float downTime = this.getTotalValue(TotalStruct.TotalFtp._downCurrentTimes.name());
                float value = downSize / downTime / 1024;
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            }
        }
    }

    /**
     * 判断室内基站性能测试的FTP上传业务
     *
     * @param resultList 判断阈值列表
     */
    private void checkIndoorPerformanceFTPUpload(List<ThresholdTestResult> resultList) {
        LogUtil.d(this.getLogTAG(), "----checkIndoorPerformanceFTPUpload----size:" + resultList.size());
        for (ThresholdTestResult result : resultList) {
            if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._SpeedAverage.name())) {
                float upSize = this.getTotalValue(TotalStruct.TotalFtp._upCurrentSize.name());
                float upTime = this.getTotalValue(TotalStruct.TotalFtp._upCurrentTimes.name());
                float value = upSize / upTime / 1024;
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            }
        }
    }

    /**
     * 判断室内基站外泄测试的Idle业务
     *
     * @param resultList 判断阈值列表
     */
    private void checkIndoorSignalLeakageIdle(List<ThresholdTestResult> resultList) {
        LogUtil.d(this.getLogTAG(), "----checkIndoorSignalLeakageIdle----size:" + resultList.size());
        int count = 0;
        for (ThresholdTestResult result : resultList) {
            if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._RSRPAverage.name())) {
                if (this.mRSRPList.isEmpty()) {
                    this.saveResult(result, false, -9999);
                } else {
                    for (float rsrp : this.mRSRPList) {
                        if (this.checkResultIsPass(result, rsrp)) {
                            count++;
                        }
                    }
                    this.saveResult(result, true, result.getThresholdValue());
                }
                break;
            }
        }
        for (ThresholdTestResult result : resultList) {
            if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._PCISample.name())) {
                if (this.mRSRPList.isEmpty()) {
                    this.saveResult(result, false, -9999);
                } else {
                    float value = count / this.mRSRPList.size() * 100;
                    boolean isPass = this.checkResultIsPass(result, value);
                    this.saveResult(result, isPass, value);
                }
                break;
            }
        }
    }

    /**
     * 判断室内基站切换测试的FTP下载业务
     *
     * @param resultList 判断阈值列表
     */
    private void checkIndoorHandoverFTPDownload(List<ThresholdTestResult> resultList) {
        LogUtil.d(this.getLogTAG(), "----checkIndoorHandoverFTPDownload----size:" + resultList.size());
        for (ThresholdTestResult result : resultList) {
            if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._tryTimes.name())) {
                float value = this.getTotalValue(TotalStruct.TotalFtp._downtrys.name());
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            } else if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._successRate.name())) {
                float value = this.getTotalValue(TotalStruct.TotalFtp._downSuccs.name());
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            } else if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._FTPAttemptHandoverTimes.name())) {
                HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getEvent();
                String valueStr = TotalDataByGSM.getHashMapValue(hMap, TotalStruct.TotalEvent._lteHandOverReq.name());
                float value = 0;
                if (!TextUtils.isEmpty(valueStr)) {
                    try {
                        value = Float.valueOf(valueStr);
                    } catch (Exception e) {
                        LogUtil.e(getLogTAG(), e.getMessage());
                    }
                }
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            } else if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._FTPHandoverSuccessRate.name())) {
                HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getEvent();
                String valueStr = TotalDataByGSM.getHashMapMultiple(hMap, TotalStruct.TotalEvent._lteHandOverSucss.name(),
                        TotalStruct.TotalEvent._lteHandOverReq.name(), 100, "%");
                float value = 0;
                if (!TextUtils.isEmpty(valueStr)) {
                    try {
                        String valueStrReal = valueStr.substring(0, valueStr.length() - 1);//去除字符串最后面的%号
                        value = Float.valueOf(valueStrReal);
                    } catch (Exception e) {
                        LogUtil.e(getLogTAG(), e.getMessage());
                    }
                }
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
            }
        }
    }

    /**
     * 保存测试结果到数据库
     *
     * @param result 测试结果对象
     * @param isPass 是否通过
     * @param value  测试值
     */
    private void saveResult(ThresholdTestResult result, boolean isPass, float value) {
        result.setTestStatus(isPass ? ThresholdTestResult.TEST_STATUS_PASS : ThresholdTestResult.TEST_STATUS_FAULT);
        result.setRealValue(value);
        this.mDaoManager.save(result);
    }

    /**
     * 判断室内基站覆盖测试的FTP下载业务
     *
     * @param resultList 判断阈值列表
     */
    private void checkIndoorCoverageFTPDownload(List<ThresholdTestResult> resultList) {
        LogUtil.d(this.getLogTAG(), "----checkIndoorCoverageFTPDownload----size:" + resultList.size());
        for (ThresholdTestResult result : resultList) {
            if (result.getThresholdKey().equals(TotalStruct.TotalSingleStation._RSCoverMileage.name())) {
                HashMap<String, Long> paras = TotalDataByGSM.getInstance().getPara();
                String valueStr = TotalDataByGSM.getHashMapMultiple(paras, TotalStruct.TotalDial._LTErsrp.name(), TotalStruct.TotalDial._LTErsrpCount.name(), 100, "%");
                String valueStrReal = valueStr.substring(0, valueStr.length() - 1);//去除字符串最后面的%号
                float value = 0;
                try {
                    value = Float.valueOf(valueStrReal);
                } catch (Exception e) {
                    LogUtil.e(getLogTAG(), e.getMessage());
                }
                boolean isPass = this.checkResultIsPass(result, value);
                this.saveResult(result, isPass, value);
                break;
            }
        }
    }

    /**
     * 判断测试结果是否通过测试
     *
     * @param result 测试结果 对象
     * @param value  实际值
     */
    private boolean checkResultIsPass(ThresholdTestResult result, float value) {
        if (value == -9999) {
            return false;
        }
        boolean flag = false;
        switch (result.getOperator()) {
            case "=":
                flag = (value == result.getThresholdValue());
                break;
            case ">=":
                flag = (value >= result.getThresholdValue());
                break;
            case ">":
                flag = (value > result.getThresholdValue());
                break;
            case "<=":
                flag = (value <= result.getThresholdValue());
                break;
            case "<":
                flag = (value < result.getThresholdValue());
                break;
            default:
        }
        return flag;
    }
}
