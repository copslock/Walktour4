package com.walktour.gui.singlestation.test.service;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.AlertWakeLock;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.SurveyPhotoUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.gui.model.SimpleCallBack;
import com.walktour.base.gui.model.TreeNode;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.SceneInfo;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.SurveyPhoto;
import com.walktour.gui.singlestation.dao.model.TaskTestResult;
import com.walktour.gui.singlestation.dao.model.ThresholdTestResult;
import com.walktour.gui.singlestation.survey.model.SurveyPhotoCallBack;
import com.walktour.gui.singlestation.test.model.CurrentCellCallBack;
import com.walktour.gui.singlestation.test.model.CurrentStationCallBack;
import com.walktour.gui.singlestation.test.model.TaskTestResultTreeCallBack;
import com.walktour.gui.singlestation.test.presenter.SceneTestBaseFragmentPresenter;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.TaskGroupConfig;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景测试服务类
 */
public class SceneTestService {
    /**
     * 日志标识
     */
    private static final String TAG = "SceneTestService";
    /**
     * 数据库操作类
     */
    private SingleStationDaoManager mDaoManager;
    /**
     * 任务模板编辑类
     */
    private TaskListDispose mTaskListDispose;
    /**
     * 系统设置
     */
    private ApplicationModel mApplicationModel;

    public SceneTestService(Context context) {
        this.mDaoManager = SingleStationDaoManager.getInstance(context);
        this.mTaskListDispose = TaskListDispose.getInstance();
        this.mApplicationModel = ApplicationModel.getInstance();
    }

    /**
     * 开始场景测试
     *
     * @param context              上下文
     * @param sceneInfo            场景对象
     * @param testTaskResultIdList 任务结果ID列表
     * @param callBack             回调类
     */
    public void startTest(Context context, SceneInfo sceneInfo, List<Long> testTaskResultIdList, SimpleCallBack callBack) {
        if (this.mApplicationModel.isTestStoping()) {
            callBack.onFailure(context.getString(R.string.main_testStoping));
            return;
        }
        if (this.mApplicationModel.isTestJobIsRun()) {
            // 停止测试,关闭wifi
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
            Intent interruptIntent = new Intent(WalkMessage.Action_Walktour_Test_Interrupt);
            context.sendBroadcast(interruptIntent);
            callBack.onFailure(context.getString(R.string.main_testStoping));
        } else {
            if (this.mApplicationModel.getNetList().contains(WalkStruct.ShowInfoType.WOnePro)) {
                callBack.onFailure(context.getString(R.string.wone_start_toast));
                return;
            }
            // 当前是否需要检查自动同步时间是否打开并且非电信手机
            if (this.mApplicationModel.isTraceInitSucc() && !DatasetManager.isPlayback) {
                StationInfo stationInfo = this.mDaoManager.getStationInfo(sceneInfo.getStationId());
                this.setTaskChecked(context, sceneInfo, stationInfo, testTaskResultIdList);
                // 如果有自动测试权限并且自动测试已经开启，给提示
                if (!this.mTaskListDispose.hasEnabledTask() || (this.mApplicationModel.isScannerTest() && !this.mApplicationModel.isScannerTestTask())) {
                    callBack.onFailure(context.getString(R.string.main_testTaskEmpty));
                    return;
                }
                AlertWakeLock.acquire(context.getApplicationContext());
                callBack.onSuccess();
            } else {
                if (!this.mApplicationModel.isTraceInitSucc()) {
                    context.sendBroadcast(new Intent(WalkMessage.NOTIFY_TESTTING_WAITTRACEINITSUCC));
                    callBack.onFailure(context.getString(R.string.main_traceIniting));
                } else {
                    callBack.onFailure(context.getString(R.string.main_test_after_playback_closed));
                }
            }
        }
    }

    /**
     * 设置任务的勾选状态
     *
     * @param context              上下文
     * @param sceneInfo            场景信息
     * @param stationInfo          基站对象
     * @param testTaskResultIdList 任务结果ID列表
     */
    private void setTaskChecked(Context context, SceneInfo sceneInfo, StationInfo stationInfo, List<Long> testTaskResultIdList) {
        String groupName;
        if(stationInfo.getFromType() == StationInfo.FROM_TYPE_IMPORT){
            groupName = stationInfo.getName() + "_" + sceneInfo.getName();
        }else{
            groupName = stationInfo.getName() + "_" + StationSearchService.getDisplaySceneName(sceneInfo.getSceneName());
        }
        TaskGroupConfig taskGroupConfig = this.mTaskListDispose.getGroup(groupName);
        if (taskGroupConfig == null)
            return;
        this.mTaskListDispose.uncheckedAllGroups();
        List<TaskTestResult> results = new ArrayList<>();
        for (long id : testTaskResultIdList) {
            results.add(this.mDaoManager.getTaskTestResult(id));
        }
        //把当前的任务组和组里的场景任务设置成勾选状态
        taskGroupConfig.setCheck(true);
        String taskNameTag = "";
        if (stationInfo.getFromType() == StationInfo.FROM_TYPE_IMPORT && sceneInfo.getSceneType() == SingleStationDaoManager.SCENE_TYPE_HANDOVER) {
            taskNameTag = sceneInfo.getSceneName().toUpperCase().replace("HANDOVER_", "") + "_";
        }
       /* switch (sceneInfo.getSceneType()) {
            case SingleStationDaoManager.SCENE_TYPE_COVERAGE:
                taskNameTag = context.getString(R.string.single_station_scene_coverage);
                break;
            case SingleStationDaoManager.SCENE_TYPE_HANDOVER:
                taskNameTag = sceneInfo.getSceneName().toUpperCase().replace("HANDOVER_","")+"_";
                break;
           case SingleStationDaoManager.SCENE_TYPE_PERFORMANCE:
                taskNameTag = context.getString(R.string.single_station_scene_performance);
                break;
            case SingleStationDaoManager.SCENE_TYPE_SIGNAL_LEAKAGE:
                taskNameTag = context.getString(R.string.single_station_scene_signal_leakage);
                break;
        }
        taskNameTag += "_";*/
        if (stationInfo.getFromType() == StationInfo.FROM_TYPE_IMPORT && sceneInfo.getSceneType() == SingleStationDaoManager.SCENE_TYPE_HANDOVER) {
            for (TaskModel task : taskGroupConfig.getTasks()) {
                if (task.getTaskName().startsWith(taskNameTag)) {
                    for (TaskTestResult result : results) {
                        if (task.getTaskName().endsWith(result.getTaskType())) {
                            task.setCheck(true);
                            break;
                        }
                    }
                }
            }
            this.mTaskListDispose.writeXml();
        } else {
            for (TaskModel task : taskGroupConfig.getTasks()) {
                for (TaskTestResult result : results) {
                    if (task.getTaskName().endsWith(result.getTaskType())) {
                        task.setCheck(true);
                        break;
                    }
                }
            }
            this.mTaskListDispose.writeXml();
        }

    }

    /**
     * 获得当前的小区信息
     *
     * @param callBack 回调类
     */
    public void getCurrentCell(CurrentCellCallBack callBack) {
        LogUtil.d(TAG, "----getCurrentCell----");
        String eNodeBIDStr = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_eNodeBID);
        String cellIDStr = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_CellID);
        if (!StringUtil.isNullOrEmpty(eNodeBIDStr) && !StringUtil.isNullOrEmpty(cellIDStr))
            cellIDStr = cellIDStr.substring(eNodeBIDStr.length());
        int eNodeBID = 0;
        int cellID = 0;
        if (StringUtil.isInteger(eNodeBIDStr))
            eNodeBID = Integer.parseInt(eNodeBIDStr);
        if (StringUtil.isInteger(cellIDStr))
            cellID = Integer.parseInt(cellIDStr);
        callBack.onSuccess(eNodeBID, cellID);
    }

    /**
     * 获得当前的基站信息
     *
     * @param callBack 回调类
     */
    public void getCurrentStation(CurrentStationCallBack callBack) {
        String eNodeBID = TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_eNodeBID);
        if (StringUtil.isInteger(eNodeBID))
            callBack.onSuccess(Integer.parseInt(eNodeBID));
    }

    /**
     * 获取指定基站指定场景的测试结果
     *
     * @param context  上下文
     * @param sceneId  场景ID
     * @param callBack 回调类
     * @deprecated 已废弃，由{@link #buildSceneTestResultList(Context, SceneInfo, TaskTestResultTreeCallBack)}代替
     */
    @Deprecated
    public void getSceneTestResultList(Context context, long sceneId, TaskTestResultTreeCallBack callBack) {
        List<TaskTestResult> resultList = this.mDaoManager.getTaskTestResultList(sceneId);
        List<TreeNode> list = new ArrayList<>();
        for (TaskTestResult taskTestResult : resultList) {
            String taskTypeName = "";
            switch (taskTestResult.getTaskType()) {
                case "FTP_Download":
                    taskTypeName = context.getString(R.string.act_task_ftpdownload);
                    break;
                case "Idle":
                    taskTypeName = context.getString(R.string.act_task_empty);
                    break;
                case "Attach":
                    taskTypeName = context.getString(R.string.act_task_attach);
                    break;
                case "FTP_Upload":
                    taskTypeName = context.getString(R.string.act_task_ftpupload);
                    break;
                case "MOC_CSFB":
                case "MOC_VOLTE":
                    taskTypeName = context.getString(R.string.act_task_initiativecall);
                    break;
            }
            TreeNode node = new TreeNode();
            node.setLevel(0);
            taskTestResult.setTaskTypeName(taskTypeName);
            node.setObject(taskTestResult);
            list.add(node);
            for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList()) {
                node = new TreeNode();
                node.setLevel(1);
                node.setObject(thresholdTestResult);
                list.add(node);
            }
        }
        callBack.onSuccess(list);
    }


    /**
     * 构建指定基站的特定场景类型下的测试结果数据，用于界面列表展示
     *
     * @param context  上下文对象
     * @param si       场景对象
     * @param callBack 回调接口
     */
    public void buildSceneTestResultList(Context context, SceneInfo si, TaskTestResultTreeCallBack callBack) {
        Long stationId = si.getStationId();
        int sceneType = si.getSceneType();
        int cellId = si.getCellId();
        LogUtil.d(TAG, "stationId = " + stationId + " ,sceneType = " + sceneType + " ,cellId = " + cellId);
        List<SceneInfo> sceneInfoList = this.mDaoManager.getSceneInfoList(stationId, sceneType, cellId);
        List<TreeNode> treeNodeList = new ArrayList<>();
        TreeNode treeNode;
        if (null != sceneInfoList && !sceneInfoList.isEmpty()) {
            boolean first = true;
            for (SceneInfo sceneInfo : sceneInfoList) {
                //构建第一层级数据
                TreeNode treeNodeP = new TreeNode();
                treeNodeP.setLevel(SceneTestBaseFragmentPresenter.TREE_LEVEL_SCENE_NAME);
                treeNodeP.setObject(sceneInfo);
                treeNodeList.add(treeNodeP);
                treeNodeP.setExpanded(first);
                first = false;

                //构建第二层级数据
                List<TaskTestResult> taskTestResults = sceneInfo.getTaskTestResultList();
                if (null != taskTestResults && !taskTestResults.isEmpty()) {
                    for (TaskTestResult taskTestResult : taskTestResults) {
                        treeNode = new TreeNode();
                        treeNode.setLevel(SceneTestBaseFragmentPresenter.TREE_LEVEL_TEST_TASK);
                        taskTestResult.setTaskTypeName(getTaskTypeName(context, taskTestResult.getTaskType()));
                        treeNode.setObject(taskTestResult);
                        treeNode.setParent(treeNodeP);
                        treeNodeList.add(treeNode);
                        //构建第三层级数据
                        List<ThresholdTestResult> thresholdTestResults = taskTestResult.getThresholdTestResultList();
                        if (null != thresholdTestResults && !thresholdTestResults.isEmpty()) {
                            for (ThresholdTestResult thresholdTestResult : thresholdTestResults) {
                                treeNode = new TreeNode();
                                treeNode.setLevel(SceneTestBaseFragmentPresenter.TREE_LEVEL_KPI);
                                treeNode.setObject(thresholdTestResult);
                                treeNode.setParent(treeNodeP);
                                treeNodeList.add(treeNode);
                            }
                        }
                    }
                }
            }
        }
        callBack.onSuccess(treeNodeList);
    }

    /**
     * 根据taskType获取对应的任务类型名称
     *
     * @param taskType
     * @return
     */
    private String getTaskTypeName(Context context, String taskType) {
        switch (taskType) {
            case "FTP_Download":
                return context.getString(R.string.act_task_ftpdownload);
            case "Idle":
                return context.getString(R.string.act_task_empty);
            case "Attach":
                return context.getString(R.string.act_task_attach);
            case "FTP_Upload":
                return context.getString(R.string.act_task_ftpupload);
            case "MOC_CSFB":
            case "MOC_VOLTE":
                return context.getString(R.string.act_task_initiativecall);
            default:
                return "";
        }
    }

    /**
     * 勘查基站图片查询
     *
     * @param stationId 基站ID
     * @param photoType 图片类型
     * @param callBack  回调类
     */
    public void getSurveyPhoto(long stationId, int photoType, SurveyPhotoCallBack callBack) {
        SurveyPhoto surveyPhoto = this.mDaoManager.getSurveyPhoto(stationId, photoType);
        callBack.onSuccess(surveyPhoto);
    }


    /**
     * 更新数据库中的SceneInfo
     * @param sceneInfo
     */
    public void updateSceneInfo(SceneInfo sceneInfo){
        mDaoManager.save(sceneInfo);
    }

    /**
     * 保存勘查照片
     *
     * @param photo    照片对象
     * @param callBack 回调类
     */
    public void saveSurveyPhoto(SurveyPhoto photo, SimpleCallBack callBack) {
        String oldPath = this.mDaoManager.save(photo);
        SurveyPhotoUtil.deletePhoto(oldPath);
        callBack.onSuccess();
    }

    public String getStationName(Long stationId) {
        return mDaoManager.getStationInfo(stationId).getName();
    }
}
