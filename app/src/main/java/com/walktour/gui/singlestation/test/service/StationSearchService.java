package com.walktour.gui.singlestation.test.service;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.walktour.Utils.TotalStruct;
import com.walktour.base.gui.model.SimpleCallBack;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.StringUtil;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.gui.R;
import com.walktour.gui.newmap.basestation.util.BaseStationImportFactory;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.CellInfo;
import com.walktour.gui.singlestation.dao.model.SceneInfo;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.TaskTestResult;
import com.walktour.gui.singlestation.dao.model.ThresholdSetting;
import com.walktour.gui.singlestation.dao.model.ThresholdTestResult;
import com.walktour.gui.singlestation.net.SingleStationRetrofitManager;
import com.walktour.gui.singlestation.net.model.StationPlatformInfo;
import com.walktour.gui.singlestation.net.model.StationPlatformInfoCallBack;
import com.walktour.gui.singlestation.net.model.StationSearchCallBack;
import com.walktour.gui.singlestation.net.model.testplan.AttachTestPlan;
import com.walktour.gui.singlestation.net.model.testplan.BaseTestPlan;
import com.walktour.gui.singlestation.net.model.testplan.IdleTestPlan;
import com.walktour.gui.singlestation.net.model.testplan.ftp.FTPDownloadTestPlan;
import com.walktour.gui.singlestation.net.model.testplan.ftp.FTPUploadTestPlan;
import com.walktour.gui.singlestation.net.model.testplan.moc.MocCSFBTestPlan;
import com.walktour.gui.singlestation.net.model.testplan.moc.MocVolteTestPlan;
import com.walktour.gui.singlestation.setting.model.SettingModel;
import com.walktour.gui.singlestation.setting.service.ConfigSettingManager;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.TaskGroupConfig;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.attach.TaskAttachModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;
import com.walktour.gui.task.parsedata.model.task.ftp.TaskFtpModel;
import com.walktour.gui.task.parsedata.model.task.idle.TaskEmptyModel;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.gui.task.parsedata.xml.common.TaskXmlTools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 基站查询服务类
 * Created by wangk on 2017/6/19.
 */

public class StationSearchService {
    /**
     * 日志标识
     */
    private static final String TAG = "StationSearchService";
    /**
     * 数据库操作类
     */
    private SingleStationDaoManager mDaoManager;
    /**
     * 任务模板编辑类
     */
    private TaskListDispose mTaskListDispose;
    /**
     * 存放本地测试任务模板的文件路径，用于本地导入基站生成测试任务
     */
    private File mTaskListFile;
    /**
     * XML操作类, 可返回读取到的列表信息
     */
    private ConfigSettingManager mConfigSetting = null;


    /**
     * 上下文对象
     */
    private Context mContext;

    public StationSearchService(Context context) {
        this.mDaoManager = SingleStationDaoManager.getInstance(context.getApplicationContext());
        this.mTaskListDispose = TaskListDispose.getInstance();
        this.mTaskListFile = AppFilePathUtil.getInstance().getAppConfigFile("single_station_task_list.xml");
        this.mConfigSetting = ConfigSettingManager.getInstance(context);
        mContext = context;
    }

    /**
     * 登录服务器
     *
     * @param serverIP      平台地址
     * @param serverPort    平台端口
     * @param loginUser     登录账号
     * @param loginPassword 登录密码
     * @param callBack      回调类
     */
    public void loginServer(Context context, String serverIP, int serverPort, String loginUser, String loginPassword, SimpleCallBack callBack) {
        SingleStationRetrofitManager retrofitManager = SingleStationRetrofitManager.getInstance(serverIP, serverPort);
        retrofitManager.login(context, loginUser, loginPassword, callBack);
    }

    /**
     * 导入基站数据
     *
     * @param context  上下文
     * @param filePath 数据文件路径
     * @param callBack 回调类
     */
    public void importStation(Context context, String filePath, SimpleCallBack callBack) {
        LogUtil.d(TAG, "----importStation----filePath:" + filePath);
        try {
            List<BaseStation> baseStationList = BaseStationImportFactory.getInstance().importFile(filePath);
            if (baseStationList != null && !baseStationList.isEmpty()) {
                for (BaseStation baseStation : baseStationList) {
                    if (baseStation.netType == BaseStation.NETTYPE_LTE) {
                        if (!this.mDaoManager.checkStationInfoExist(baseStation.enodebId)) {
                            this.initImportStationInfo(context, baseStation);
                        }
                    }
                }
            }
            callBack.onSuccess();
        } catch (Exception e) {
            LogUtil.d(TAG, e.getMessage(), e);
            callBack.onFailure(e.getMessage());
        }
    }

    /**
     * 初始化导入基站列表测试数据
     *
     * @param context     上下文
     * @param baseStation 基站信息
     */
    private void initImportStationInfo(Context context, BaseStation baseStation) {
        StationInfo stationInfo = new StationInfo();
        stationInfo.setType(baseStation.mapType == BaseStation.MAPTYPE_INDOOR ? SingleStationDaoManager.STATION_TYPE_INDOOR : SingleStationDaoManager.STATION_TYPE_OUTDOOR);
        stationInfo.setCode(baseStation.enodebId);
        stationInfo.setName(baseStation.name);
        stationInfo.setLatitude(baseStation.latitude);
        stationInfo.setLongitude(baseStation.longitude);
        stationInfo.setENodeBID(this.getIntValue(baseStation.enodebId));
        this.mDaoManager.save(stationInfo);
        Long surveyStationInfoId = this.mDaoManager.createSurveyStationInfo(stationInfo);
        for (BaseStationDetail detail : baseStation.details) {
            CellInfo cellInfo = new CellInfo();
            cellInfo.setStationId(stationInfo.getId());
            cellInfo.setCellId(this.getIntValue(detail.sectorId));
            cellInfo.setCellName(detail.cellName);
            cellInfo.setAerialHigh(detail.antennaHeight);
            cellInfo.setPCI(this.getIntValue(detail.pci));
            cellInfo.setFrequency(this.getIntValue(detail.uarfcn));
            this.mDaoManager.save(cellInfo);
            this.mDaoManager.createSurveyCellInfo(surveyStationInfoId, cellInfo);
        }
        this.initStationTestResultList(context, stationInfo);
        this.initTestTask(context, stationInfo);
    }

    /**
     * 获取字符串整型值
     *
     * @param value 字符串
     * @return 整型值
     */
    private int getIntValue(String value) {
        if (StringUtil.isInteger(value)) {
            return Integer.parseInt(value);
        }
        return 0;
    }

    /**
     * 初始化场景的测试任务
     *
     * @param context     上下文
     * @param stationInfo 基站信息
     */
    private void initTestTask(Context context, StationInfo stationInfo) {
        if (this.mTaskListDispose.existGroup(stationInfo.getName())) {
            return;
        }
        TaskXmlTools taskXmlTools = new TaskXmlTools();
        HashSet<String> groupNameSet = new HashSet<>();
        for (SceneInfo sceneInfo : stationInfo.getSceneInfoList()) {
            taskXmlTools.parseXml(this.mTaskListFile.getAbsolutePath(), TaskXmlTools.LOADMODEL_REPLACE);
            for (TaskGroupConfig group : taskXmlTools.getCurrenGroups()) {
                String groupName = stationInfo.getName() + "_" + sceneInfo.getName();
                if(groupNameSet.contains(groupName)){
                    continue;
                }
                group.setGroupName(groupName);
                List<TaskModel> taskModels = new ArrayList<>();
                for (TaskModel model : group.getTasks()) {
                    if (stationInfo.getType() == SingleStationDaoManager.STATION_TYPE_OUTDOOR) {
                        //当基站类型为室外时，只保留覆盖和性能，其余的过滤掉
                        if (sceneInfo.getSceneType() == SingleStationDaoManager.SCENE_TYPE_PERFORMANCE) {
                            if (model.getTaskName().startsWith("性能")) {
                                taskModels.add(model);
                                model.setTaskName(model.getTaskName().substring(3, model.getTaskName().length()));
                            }
                        } else if (sceneInfo.getSceneType() == SingleStationDaoManager.SCENE_TYPE_COVERAGE) {
                            if (model.getTaskName().startsWith("覆盖")) {
                                taskModels.add(model);
                                model.setTaskName(model.getTaskName().substring(3, model.getTaskName().length()));
                            }
                        }
                    } else {
                        //室内基站只按场景过滤
                        if (sceneInfo.getSceneType() == SingleStationDaoManager.SCENE_TYPE_COVERAGE) {
                            //覆盖
                            if (model.getTaskName().startsWith("覆盖")) {
                                taskModels.add(model);
                                model.setTaskName(model.getTaskName().substring(3, model.getTaskName().length()));
                            }
                        } else if (sceneInfo.getSceneType() == SingleStationDaoManager.SCENE_TYPE_PERFORMANCE) {
                            if (model.getTaskName().startsWith("性能")) {
                                taskModels.add(model);
                                model.setTaskName(model.getTaskName().substring(3, model.getTaskName().length()));
                            }
                        } else if (sceneInfo.getSceneType() == SingleStationDaoManager.SCENE_TYPE_SIGNAL_LEAKAGE) {
                            if (model.getTaskName().startsWith("外泄")) {
                                taskModels.add(model);
                                model.setTaskName(model.getTaskName().substring(3, model.getTaskName().length()));
                            }
                        } else if (sceneInfo.getSceneType() == SingleStationDaoManager.SCENE_TYPE_HANDOVER) {
                            // TODO: 2017/10/31
                        }
                    }
                }
                //清除掉原有的taskModel列表
                group.getTasks().clear();
                //将保留覆盖和性能的列表设置给 group.getTasks()
                group.getTasks().addAll(taskModels);
                groupNameSet.add(groupName);
                this.mTaskListDispose.addGroup(group);
            }
        }
    }

    /**
     * 初始化平台导入的场景的测试任务。<br/>
     * 此处策略是获取本地测试模版里面的列表，存到一个HashMap里面，然后清空TaskGroupConfig.getTasks()，
     * 然后从平台获取下发的测试任务，如果名字一致，
     * 则将该获取到的测试任务的values设置给HashMap里面对应的TaskModel，
     * 然后将该TaskModel添加到TaskGroupConfig.getTasks中。
     *
     * @param context     上下文
     * @param stationInfo 基站信息
     */
    private void initTestTaskFromPlatform(Context context, StationPlatformInfo stationInfo) {
        if (this.mTaskListDispose.existGroup(stationInfo.getSiteName())) {
            return;
        }
        TaskXmlTools taskXmlTools = new TaskXmlTools();

        ArrayList<StationPlatformInfo.TestScene> testScenes = stationInfo.getTestScenes();
        if (null != testScenes && !testScenes.isEmpty()) {
            for (StationPlatformInfo.TestScene testScene : testScenes) {
                taskXmlTools.parseXml(this.mTaskListFile.getAbsolutePath(), TaskXmlTools.LOADMODEL_REPLACE);
                for (TaskGroupConfig group : taskXmlTools.getCurrenGroups()) {
                    group.setGroupName(stationInfo.getSiteName() + "_" + getDisplaySceneName(testScene.getSceneName()) );
                    HashMap<String, TaskModel> map = new HashMap<>();//将group.getTasks数据结构转化成HashMap<名字，对应的TaskModel>，方便后续操作数据
                    for (TaskModel model : group.getTasks()) {
                        map.put(model.getTaskName(), model);
                    }
                    group.getTasks().clear();//清空原来的group的TaskModel列表
                    StationPlatformInfo.TestGroup testGroup = testScene.getTestGroup();
                    if (null != testGroup) {
                        group.setGroupRepeatCount(testGroup.getRepeatCount());
                        if (null != testGroup.getTests() && !testGroup.getTests().isEmpty()) {
                            for (StationPlatformInfo.TestTask testTask : testScene.getTestGroup().getTests()) {
                                //先读取下发的测试任务的值
                                String plan = testTask.getTestPlan();
                                BaseTestPlan testPlan = null;//用于存放解析下发测试任务的值
                                Gson gson = new Gson();//json解析器
                                if (!TextUtils.isEmpty(plan)) {
                                    switch (testTask.getTaskType()) {
                                        case "Attach":
                                            testPlan = gson.fromJson(plan, AttachTestPlan.class);
                                            break;
                                        case "FTP_Upload":
                                            testPlan = gson.fromJson(plan, FTPUploadTestPlan.class);
                                            break;
                                        case "FTP_Download":
                                            testPlan = gson.fromJson(plan, FTPDownloadTestPlan.class);
                                            break;
                                        case "MOC_CSFB":
                                            testPlan = gson.fromJson(plan, MocCSFBTestPlan.class);
                                            break;
                                        case "MOC_VOLTE":
                                            testPlan = gson.fromJson(plan, MocVolteTestPlan.class);
                                            break;
                                        case "Idle":
                                            testPlan = gson.fromJson(plan, IdleTestPlan.class);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                //将下发的测试任务的值设置给测试模版
                                String name = getNamePrefix(testScene) + "_" + testTask.getTaskType();
                                if (map.containsKey(name)) {
                                    TaskModel tm = map.get(name);
                                    tm.setTaskName(name.substring(3, name.length()));
                                    //将设置下发的值的测试任务添加到group.getTasks()中
                                    group.getTasks().add(processTaskModel(testTask.getTaskType(),testTask.getRepeatCount(), tm, testPlan));
                                }
                            }
                        }
                    }
                    this.mTaskListDispose.addGroup(group);
                }
            }
        }
    }

    /**
     * 加工TaskModel，为TaskModel设置解析得到的下发下来的值
     *
     * @param taskType                 测试类型
     * @param repeatCount              重复次数
     * @param targetTaskModel          需要加工的TaskModel
     * @param baseTestPlanFromPlatform 解析从平台下发的测试任务json获取的测试任务对象
     * @return 加工后的TaskModel
     */
    private TaskModel processTaskModel(String taskType,int repeatCount, TaskModel targetTaskModel, BaseTestPlan baseTestPlanFromPlatform) {
        if (null == baseTestPlanFromPlatform) {
            return targetTaskModel;
        }
        switch (taskType) {
            case "Attach":
                AttachTestPlan attachTestPlan = (AttachTestPlan) baseTestPlanFromPlatform;
                TaskAttachModel attachModel = (TaskAttachModel) targetTaskModel;
                attachModel.setRepeat(repeatCount);
                attachModel.getAttachTestConfig().setBaudRate(attachTestPlan.getBaudRate());
                attachModel.getAttachTestConfig().setKeepTime(attachTestPlan.getDuration() / 1000);
                attachModel.setInterVal(attachTestPlan.getInterval() / 1000);
                attachModel.getAttachTestConfig().setKeepTime(attachTestPlan.getTimeout() / 1000);
                return attachModel;
            case "FTP_Upload":
                FTPUploadTestPlan ftpUploadTestPlan = (FTPUploadTestPlan) baseTestPlanFromPlatform;
                TaskFtpModel ftpUploadModel = (TaskFtpModel) targetTaskModel;
                ftpUploadModel.setRepeat(repeatCount);
                String uploadConnectDisconnectStrategy = NetworkConnectionSetting.connectionDisconnectStrategy_0;
                if (ftpUploadTestPlan.isDisconnectEveryTime()) {
                    uploadConnectDisconnectStrategy = NetworkConnectionSetting.connectionDisconnectStrategy_2;
                } else if (ftpUploadTestPlan.isDetachEveryTime()) {
                    uploadConnectDisconnectStrategy = NetworkConnectionSetting.connectionDisconnectStrategy_1;
                }
                ftpUploadModel.getNetworkConnectionSetting().setConnectionDisconnectStrategy(uploadConnectDisconnectStrategy);
                ftpUploadModel.getFtpUploadTestConfig().setUploadDuration(ftpUploadTestPlan.getDuration() / 1000);
                ftpUploadModel.setFileSize(ftpUploadTestPlan.getFileSize());
                ftpUploadModel.setInterVal(ftpUploadTestPlan.getInterval() / 1000);
//                ftpUploadModel.getFtpUploadTestConfig().setPsCallMode(ftpUploadTestPlan.getMode());
                ftpUploadModel.getFtpUploadTestConfig().setNoDataTimeout(ftpUploadTestPlan.getNoDataTimeout() / 1000);
                ftpUploadModel.getFtpUploadTestConfig().setRemoteDirectory(ftpUploadTestPlan.getRemotePath());
                ftpUploadModel.setThreadNumber(ftpUploadTestPlan.getThreadCount());
                //FTPHostSetting
                ftpUploadModel.getFtpUploadTestConfig().getFtpHostSetting().setAddress(ftpUploadTestPlan.getFTPHostSetting().getHost());
                ftpUploadModel.getFtpUploadTestConfig().getFtpHostSetting().setPassword(ftpUploadTestPlan.getFTPHostSetting().getPassword());
                ftpUploadModel.getFtpUploadTestConfig().getFtpHostSetting().setPort(ftpUploadTestPlan.getFTPHostSetting().getPort());
                ftpUploadModel.getFtpUploadTestConfig().getFtpHostSetting().setUserName(ftpUploadTestPlan.getFTPHostSetting().getUser());

                return ftpUploadModel;
            case "FTP_Download":
                FTPDownloadTestPlan ftpDownloadTestPlan = (FTPDownloadTestPlan) baseTestPlanFromPlatform;
                TaskFtpModel ftpDownloadModel = (TaskFtpModel) targetTaskModel;
                ftpDownloadModel.setRepeat(repeatCount);
                String downloadConnectDisconnectStrategy = NetworkConnectionSetting.connectionDisconnectStrategy_0;
                if (ftpDownloadTestPlan.isDisconnectEveryTime()) {
                    downloadConnectDisconnectStrategy = NetworkConnectionSetting.connectionDisconnectStrategy_2;
                } else if (ftpDownloadTestPlan.isDetachEveryTime()) {
                    downloadConnectDisconnectStrategy = NetworkConnectionSetting.connectionDisconnectStrategy_1;
                }
                ftpDownloadModel.getNetworkConnectionSetting().setConnectionDisconnectStrategy(downloadConnectDisconnectStrategy);
                ftpDownloadModel.getFtpDownloadTestConfig().setDownloadFile(ftpDownloadTestPlan.getDownloadFile());
                ftpDownloadModel.getFtpDownloadTestConfig().setDownloadDuration(ftpDownloadTestPlan.getDuration() / 1000);
                ftpDownloadModel.setInterVal(ftpDownloadTestPlan.getInterval() / 1000);
//                ftpDownloadModel.getFtpDownloadTestConfig().setPsCallMode(ftpDownloadTestPlan.getMode());
                ftpDownloadModel.getFtpDownloadTestConfig().setNoDataTimeout(ftpDownloadTestPlan.getNoDataTimeout() / 1000);
                ftpDownloadModel.setThreadNumber(ftpDownloadTestPlan.getThreadCount());
                //FTPHostSetting
                ftpDownloadModel.getFtpDownloadTestConfig().getFtpHostSetting().setAddress(ftpDownloadTestPlan.getFTPHostSetting().getHost());
                ftpDownloadModel.getFtpDownloadTestConfig().getFtpHostSetting().setPassword(ftpDownloadTestPlan.getFTPHostSetting().getPassword());
                ftpDownloadModel.getFtpDownloadTestConfig().getFtpHostSetting().setPort(ftpDownloadTestPlan.getFTPHostSetting().getPort());
                ftpDownloadModel.getFtpDownloadTestConfig().getFtpHostSetting().setUserName(ftpDownloadTestPlan.getFTPHostSetting().getUser());

                return ftpDownloadModel;
            case "MOC_CSFB":
                MocCSFBTestPlan mocCSFBTestPlan = (MocCSFBTestPlan) baseTestPlanFromPlatform;
                TaskInitiativeCallModel mocCSFBModel = (TaskInitiativeCallModel) targetTaskModel;
                mocCSFBModel.setRepeat(repeatCount);
                mocCSFBModel.setConnectTime(mocCSFBTestPlan.getConnectionTime() / 1000);
                mocCSFBModel.setCallNumber(mocCSFBTestPlan.getDialNumber());
                mocCSFBModel.setKeepTime(mocCSFBTestPlan.getDuration() / 1000);
                mocCSFBModel.setInterVal(mocCSFBTestPlan.getInterval() / 1000);
                return mocCSFBModel;
            case "MOC_VOLTE":
                MocVolteTestPlan mocVolteTestPlan = (MocVolteTestPlan) baseTestPlanFromPlatform;
                TaskInitiativeCallModel mocVolteModel = (TaskInitiativeCallModel) targetTaskModel;
                mocVolteModel.setRepeat(repeatCount);
                mocVolteModel.setConnectTime(mocVolteTestPlan.getConnectionTime() / 1000);
                mocVolteModel.setCallNumber(mocVolteTestPlan.getDialNumber());
                mocVolteModel.setKeepTime(mocVolteTestPlan.getDuration() / 1000);
                mocVolteModel.setInterVal(mocVolteTestPlan.getInterval() / 1000);
                return mocVolteModel;
            case "Idle":
                IdleTestPlan idleTestPlan = (IdleTestPlan) baseTestPlanFromPlatform;
                TaskEmptyModel idleModel = (TaskEmptyModel) targetTaskModel;
                idleModel.setRepeat(repeatCount);
                idleModel.getIdleTestConfig().setCollectData(idleTestPlan.isCollectData());
                idleModel.getIdleTestConfig().setKeepTime(idleTestPlan.getDuration() / 1000);
                return idleModel;
            default:
                return targetTaskModel;
        }
    }

    /**
     * 根据sceneType获取测试任务名称前缀
     *
     * @param testScene 场景
     * @return 测试任务名称前缀
     */
    private String getNamePrefix(StationPlatformInfo.TestScene testScene) {
        switch (testScene.getSceneType()) {
            case SingleStationDaoManager.SCENE_TYPE_COVERAGE:
                return "覆盖";
            case SingleStationDaoManager.SCENE_TYPE_HANDOVER:
                String sceneName = testScene.getSceneName().toUpperCase();
                if (TextUtils.isEmpty(sceneName) && sceneName.contains("HANDOVER")) {
                    return "切换";
                } else {
                    return "切换" + sceneName.replace("HANDOVER", "");
                }
            case SingleStationDaoManager.SCENE_TYPE_PERFORMANCE:
                return "性能";
            case SingleStationDaoManager.SCENE_TYPE_SIGNAL_LEAKAGE:
                return "外泄";
            default:
                return "";
        }
    }

    public static String getDisplaySceneName(String sceneName) {
        if (TextUtils.isEmpty(sceneName)) {
            return "";
        }
        switch (sceneName) {
            case "coverage":
                return "覆盖测试";
            case "handover_gate":
                return "大门口切换测试";
            case "handover_indoor":
                return "室内切换测试";
            case "handover_park":
                return "停车场切换测试";
            case "leakage":
                return "外泄测试";
            case "performance":
                return "性能测试";
            default:
                return "";
        }
    }

    /**
     * 设置基站阈值为默认值
     *
     * @param stationType 基站类型
     */
    private void setThresholdDefaultSetting(int stationType) {
        List<SettingModel> settingModelList = this.mConfigSetting.getSettingModelList();
        List<ThresholdSetting> list = this.mDaoManager.getThresholdSettingList(stationType);
        for (SettingModel settingModel : settingModelList) {
            if (settingModel.getStationType() != stationType) {
                continue;
            }
            boolean isFind = false;
            for (ThresholdSetting thresholdSetting : list) {
                if (thresholdSetting.getSceneType() == settingModel.getSceneType()
                        && thresholdSetting.getTestTask().equals(settingModel.getTestTask())
                        && thresholdSetting.getThresholdKey().equals(settingModel.getThresholdKey())) {
                    isFind = true;
                    thresholdSetting.setOperator(settingModel.getOperator());
                    thresholdSetting.setThresholdValue(settingModel.getThresholdValue());
                    this.mDaoManager.save(thresholdSetting);
                    break;
                }
            }
            if (!isFind) {
                ThresholdSetting thresholdSetting = new ThresholdSetting();
                thresholdSetting.setThresholdValue(settingModel.getThresholdValue());
                thresholdSetting.setOperator(settingModel.getOperator());
                thresholdSetting.setSceneType(settingModel.getSceneType());
                thresholdSetting.setStationType(settingModel.getStationType());
                thresholdSetting.setTestTask(settingModel.getTestTask());
                thresholdSetting.setThresholdKey(settingModel.getThresholdKey());
                thresholdSetting.setThresholdUnit(settingModel.getThresholdUnit());
                this.mDaoManager.save(thresholdSetting);
            }
        }
    }

    /**
     * 初始化基站测试结果测试数据
     *
     * @param stationInfo 基站对象
     */
    private void initStationTestResultList(Context context, StationInfo stationInfo) {
        List<ThresholdSetting> list = this.mDaoManager.getThresholdSettingList(stationInfo.getType());
        if (list == null || list.isEmpty()) {
            this.setThresholdDefaultSetting(stationInfo.getType());
        }
        this.createSceneInfo(context, stationInfo, 0, SingleStationDaoManager.SCENE_TYPE_COVERAGE);
        if (stationInfo.getType() == SingleStationDaoManager.STATION_TYPE_INDOOR) {
            this.createSceneInfo(context, stationInfo, 0, SingleStationDaoManager.SCENE_TYPE_HANDOVER);
            this.createSceneInfo(context, stationInfo, 0, SingleStationDaoManager.SCENE_TYPE_SIGNAL_LEAKAGE);
            this.createSceneInfo(context, stationInfo, 0, SingleStationDaoManager.SCENE_TYPE_PERFORMANCE);
        } else {
            for (CellInfo cellInfo : stationInfo.getCellInfoList()) {
                this.createSceneInfo(context, stationInfo, cellInfo.getCellId(), SingleStationDaoManager.SCENE_TYPE_PERFORMANCE);
            }
        }
    }

    /**
     * 生成基站关联的场景
     *
     * @param context     上下文
     * @param stationInfo 基站信息
     * @param cellId      小区ID
     * @param sceneType   场景类型
     */
    private void createSceneInfo(Context context, StationInfo stationInfo, int cellId, int sceneType) {
        List<ThresholdSetting> list = this.mDaoManager.getThresholdSettingList(stationInfo.getType(), sceneType);
        if (list == null || list.isEmpty()) {
            return;
        }
        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setENodeBID(stationInfo.getENodeBID());
        sceneInfo.setCellId(cellId);
        sceneInfo.setName(getName(sceneType));
        sceneInfo.setSceneType(sceneType);
        sceneInfo.setStationId(stationInfo.getId());
        this.mDaoManager.save(sceneInfo);
        Map<String, TaskTestResult> taskTestResultMap = new HashMap<>();
        for (ThresholdSetting thresholdSetting : list) {
            TaskTestResult taskTestResult;
            if (taskTestResultMap.containsKey(thresholdSetting.getTestTask())) {
                taskTestResult = taskTestResultMap.get(thresholdSetting.getTestTask());
            } else {
                taskTestResult = this.createTaskTestResult(sceneInfo.getId(), thresholdSetting);
                taskTestResultMap.put(thresholdSetting.getTestTask(), taskTestResult);
            }
            this.createThresholdTestResult(context, taskTestResult.getId(), thresholdSetting);
        }
    }

    /**
     * 根据场景类型获取名称
     *
     * @param sceneType
     * @return
     */
    private String getName(int sceneType) {
        switch (sceneType) {
            case SingleStationDaoManager.SCENE_TYPE_COVERAGE:
                return mContext.getString(R.string.single_station_scene_coverage_test);
            case SingleStationDaoManager.SCENE_TYPE_SIGNAL_LEAKAGE:
                return mContext.getString(R.string.single_station_scene_signal_leakage_test);
            case SingleStationDaoManager.SCENE_TYPE_HANDOVER:
                return mContext.getString(R.string.single_station_scene_handover_test);
            case SingleStationDaoManager.SCENE_TYPE_PERFORMANCE:
                return mContext.getString(R.string.single_station_scene_performance_test);
            default:
                return "";
        }
    }

    /**
     * 生成从平台导入基站关联的场景
     *
     * @param context      上下文
     * @param stationId    基站id
     * @param platformInfo 平台导入的基站对象
     * @param cellId       小区ID
     * @param testScene    测试场景
     */
    private void createSceneInfoFromPlatform(Context context, long stationId, StationPlatformInfo platformInfo, int cellId, StationPlatformInfo.TestScene testScene) {
        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setENodeBID(platformInfo.getENodeBID());
        sceneInfo.setCellId(cellId);
        sceneInfo.setSceneType(testScene.getSceneType());
        sceneInfo.setSceneName(testScene.getSceneName());
        sceneInfo.setName(testScene.getName());
        sceneInfo.setPlatformSceneId(testScene.getSceneId());
        sceneInfo.setStationId(stationId);
        this.mDaoManager.save(sceneInfo);
        if (null != testScene.getTestGroup().getTests()) {
            for (StationPlatformInfo.TestTask testTask : testScene.getTestGroup().getTests()) {
                ArrayList<StationPlatformInfo.TestTask.Condition> conditions = testTask.getConditions();
                Map<String, TaskTestResult> taskTestResultMap = new HashMap<>();
                for (StationPlatformInfo.TestTask.Condition condition : conditions) {
                    TaskTestResult taskTestResult;
                    if (taskTestResultMap.containsKey(condition.getTask())) {
                        taskTestResult = taskTestResultMap.get(condition.getTask());
                    } else {
                        taskTestResult = this.createTaskTestResult(sceneInfo.getId(), condition.getTask(), testTask.getItemId());
                        taskTestResultMap.put(condition.getTask(), taskTestResult);
                    }
                    this.createThresholdTestResult(context, taskTestResult.getId(), condition);
                }
            }
        }
    }

    /**
     * 生成阈值测试结果数据
     *
     * @param taskTestResultId 业务测试结果对象ID
     * @param condition        阈值设置条件对象
     */
    private void createThresholdTestResult(Context context, long taskTestResultId, StationPlatformInfo.TestTask.Condition condition) {
        ThresholdTestResult thresholdTestResult = new ThresholdTestResult();
        thresholdTestResult.setTaskTestResultId(taskTestResultId);
        thresholdTestResult.setOperator(condition.getOperator());
        thresholdTestResult.setThresholdKey(getThresholdKey(condition.getKPI()));
        thresholdTestResult.setThresholdName(context.getString(this.getThresholdKeyResId(thresholdTestResult.getThresholdKey())));
        thresholdTestResult.setThresholdValue(condition.getValue());
        thresholdTestResult.setThresholdUnit(condition.getUnit());
        this.mDaoManager.save(thresholdTestResult);
    }

    /**
     * 初始化平台基站测试结果测试数据
     *
     * @param stationId           基站对象ID
     * @param stationPlatformInfo 平台获取的基站信息
     */
    private void initSceneTestResultList(Context context, Long stationId, StationPlatformInfo stationPlatformInfo) {
        StationInfo stationInfo = this.mDaoManager.getStationInfo(stationId);
        int cellId = 0;
        for (CellInfo cellInfo : stationInfo.getCellInfoList()) {
            cellId = cellInfo.getCellId();
            break;
        }
        if (stationInfo.getType() == SingleStationDaoManager.STATION_TYPE_OUTDOOR) {
            for (StationPlatformInfo.TestScene testScene : stationPlatformInfo.getTestScenes()) {
                if (testScene.getSceneType() != SingleStationDaoManager.SCENE_TYPE_PERFORMANCE) {
                    this.createSceneInfoFromPlatform(context, stationId, stationPlatformInfo, cellId, testScene);
                } else {
                    for (CellInfo cellInfo : stationInfo.getCellInfoList()) {
                        this.createSceneInfoFromPlatform(context, stationId, stationPlatformInfo, cellInfo.getCellId(), testScene);
                    }
                }
            }

        } else {
            if (null != stationPlatformInfo.getTestScenes()) {
                for (StationPlatformInfo.TestScene testScene : stationPlatformInfo.getTestScenes()) {
                    this.createSceneInfoFromPlatform(context, stationId, stationPlatformInfo, cellId, testScene);
                }
            }
        }
    }

    /**
     * 生成平台场景测试结果对象
     *
     * @param sceneId  场景id
     * @param taskType 任务类型
     * @return TaskTestResult对象
     */
    private TaskTestResult createTaskTestResult(long sceneId, String taskType, int itemId) {
        TaskTestResult taskTestResult = new TaskTestResult();
        taskTestResult.setPlatformItemId(itemId);
        taskTestResult.setSceneId(sceneId);
        taskTestResult.setTaskType(taskType);
        this.mDaoManager.save(taskTestResult);
        return taskTestResult;
    }

    /**
     * 生成场景测试结果对象
     *
     * @param sceneId          场景ID
     * @param thresholdSetting 阈值设置对象
     * @return 场景测试结果对象
     */
    private TaskTestResult createTaskTestResult(long sceneId, ThresholdSetting thresholdSetting) {
        TaskTestResult taskTestResult = new TaskTestResult();
        taskTestResult.setSceneId(sceneId);
        taskTestResult.setTaskType(thresholdSetting.getTestTask());
        this.mDaoManager.save(taskTestResult);
        return taskTestResult;
    }

    /**
     * 生成阈值测试结果数据
     *
     * @param taskTestResultId 业务测试结果对象ID
     * @param thresholdSetting 阈值设置对象
     */
    private void createThresholdTestResult(Context context, long taskTestResultId, ThresholdSetting thresholdSetting) {
        ThresholdTestResult thresholdTestResult = new ThresholdTestResult();
        thresholdTestResult.setTaskTestResultId(taskTestResultId);
        thresholdTestResult.setOperator(thresholdSetting.getOperator());
        thresholdTestResult.setThresholdKey(thresholdSetting.getThresholdKey());
        thresholdTestResult.setThresholdName(context.getString(this.getThresholdKeyResId(thresholdSetting.getThresholdKey())));
        thresholdTestResult.setThresholdValue(thresholdSetting.getThresholdValue());
        thresholdTestResult.setThresholdUnit(thresholdSetting.getThresholdUnit());
        this.mDaoManager.save(thresholdTestResult);
    }

    /**
     * 获得要显示的阈值名称资源ID
     *
     * @param thresholdKey 阈值Key
     * @return 显示的阈值名称资源ID
     */
    private int getThresholdKeyResId(String thresholdKey) {
        if (thresholdKey.equals(TotalStruct.TotalSingleStation._RSCoverMileage.name())) {
            return R.string.single_station_threshold_rs_coverage_ratio;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._FTPAttemptHandoverTimes.name())) {
            return R.string.single_station_threshold_attempt_handover_times;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._FTPHandoverSuccessRate.name())) {
            return R.string.single_station_threshold_handover_succ_ratio;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._PCISample.name())) {
            return R.string.single_station_threshold_pci_sample;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._RSRPAverage.name())) {
            return R.string.single_station_threshold_rsrp;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._tryTimes.name())) {
            return R.string.single_station_threshold_attempt_times;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._successRate.name())) {
            return R.string.single_station_threshold_succ_ratio;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._SpeedAverage.name())) {
            return R.string.single_station_threshold_data_average_rate;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_tryTimes.name())) {
            return R.string.single_station_threshold_attampt_call_times_csfb;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_establishedRate.name())) {
            return R.string.single_station_threshold_establised_succ_ratio_csfb;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_successRate.name())) {
            return R.string.single_station_threshold_call_succ_ratio_csfb;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._volte_successRate.name())) {
            return R.string.single_station_threshold_call_succ_ratio_volte;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._volte_eSRVCC.name())) {
            return R.string.single_station_threshold_handover_succ_times_esrvcc;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_returnDelay.name())) {
            return R.string.single_station_threshold_csfb_return_delay;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_connectDelay.name())) {
            return R.string.single_station_threshold_csfb_connect_delay;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_returnSuccessRate.name())) {
            return R.string.single_station_threshold_csfb_return_success_rate;
        }

        return R.string.single_station_validation;
    }

    /**
     * 获取实际的阈值key
     *
     * @param threshold 配置文件的阈值key
     * @return 实际的阈值key
     */
    private String getThresholdKey(String threshold) {
        String thresholdKey;
        switch (threshold) {
            case "RS Coverage Ratio":
                thresholdKey = TotalStruct.TotalSingleStation._RSCoverMileage.name();
                break;
            case "Attempt Handover Times":
                thresholdKey = TotalStruct.TotalSingleStation._FTPAttemptHandoverTimes.name();
                break;
            case "Handover Success Ratio":
                thresholdKey = TotalStruct.TotalSingleStation._FTPHandoverSuccessRate.name();
                break;
            case "PCI Sample":
                thresholdKey = TotalStruct.TotalSingleStation._PCISample.name();
                break;
            case "RSRP":
                thresholdKey = TotalStruct.TotalSingleStation._RSRPAverage.name();
                break;
            case "Attempt Times":
                thresholdKey = TotalStruct.TotalSingleStation._tryTimes.name();
                break;
            case "Success Ratio":
                thresholdKey = TotalStruct.TotalSingleStation._successRate.name();
                break;
            case "Average Rate":
                thresholdKey = TotalStruct.TotalSingleStation._SpeedAverage.name();
                break;
            case "Attampt Call Times(CSFB)":
                thresholdKey = TotalStruct.TotalSingleStation._csfb_tryTimes.name();
                break;
            case "Call Sucess Ratio(CSFB)":
                thresholdKey = TotalStruct.TotalSingleStation._csfb_successRate.name();
                break;
            case "Handover Success Ratio(eSRVCC)":
                thresholdKey = TotalStruct.TotalSingleStation._volte_eSRVCC.name();
                break;
            case "Call Sucess Ratio(Volte)":
                thresholdKey = TotalStruct.TotalSingleStation._volte_successRate.name();
                break;
            case "Call Established Sucess Ratio(CSFB)":
                thresholdKey = TotalStruct.TotalSingleStation._csfb_establishedRate.name();
                break;
            case "Handover Success Times(eSRVCC)":
                thresholdKey = TotalStruct.TotalSingleStation._volte_eSRVCC.name();
                break;
            case "Call Return Delay(CSFB)":
                thresholdKey = TotalStruct.TotalSingleStation._csfb_returnDelay.name();
                break;
            case "Call Connect Delay(CSFB)":
                thresholdKey = TotalStruct.TotalSingleStation._csfb_connectDelay.name();
                break;
            case "Call Return Success Ratio(CSFB)":
                thresholdKey = TotalStruct.TotalSingleStation._csfb_returnSuccessRate.name();
                break;
            default:
                thresholdKey = "";
                break;
        }
        return thresholdKey;
    }

    /**
     * 根据关键字（基站号、基站名称、基站距离）从平台查询相关的基站列表
     *
     * @param serverIP   平台地址
     * @param serverPort 平台端口
     * @param loginUser  登录账号
     * @param keyword    关键字（基站号、基站名称)
     * @param callBack   回调类
     */
    public void getStationSearchList(Context context, String serverIP, int serverPort, String loginUser, String keyword, StationSearchCallBack callBack) {
        LogUtil.d(TAG, "----getStationSearchList----keyword:" + keyword);
        SingleStationRetrofitManager retrofitManager = SingleStationRetrofitManager.getInstance(serverIP, serverPort);
        if (!retrofitManager.isLogin()) {
            callBack.onFailure(context.getString(R.string.single_station_no_login));
            return;
        }
        retrofitManager.searchStationList(context, loginUser, keyword, callBack);
    }

    /**
     * 根据关键字（基站号、基站名称、基站距离）从平台查询相关的基站列表
     *
     * @param serverIP   平台地址
     * @param serverPort 平台端口
     * @param loginUser  登录账号
     * @param latitude   纬度
     * @param longitude  经度
     * @param distance   基站距离
     * @param callBack   回调类
     */
    public void getStationSearchList(Context context, String serverIP, int serverPort, String loginUser, double latitude, double longitude, int distance, StationSearchCallBack callBack) {
        LogUtil.d(TAG, "----getStationSearchList----");
        SingleStationRetrofitManager retrofitManager = SingleStationRetrofitManager.getInstance(serverIP, serverPort);
        if (!retrofitManager.isLogin()) {
            callBack.onFailure(context.getString(R.string.single_station_no_login));
            return;
        }
        retrofitManager.searchStationList(context, loginUser, latitude, longitude, distance, callBack);
    }

    /**
     * 新增平台基站配置
     *
     * @param stationId 平台基站ID
     * @param callBack  回调类
     */
    public void addStationFromPlatform(final Context context, String serverIP, int serverPort, int stationId, final SimpleCallBack callBack) {
        List<StationInfo> existedStationList = mDaoManager.getStationInfoList();
        if (null != existedStationList && !existedStationList.isEmpty()) {
            for (StationInfo station : existedStationList) {
                if (station.getSiteId() == stationId) {
                    callBack.onFailure(context.getString(R.string.single_station_platform_station_has_imported));
                    return;
                }
            }
        }
        SingleStationRetrofitManager retrofitManager = SingleStationRetrofitManager.getInstance(serverIP, serverPort);
        if (!retrofitManager.isLogin()) {
            callBack.onFailure(context.getString(R.string.single_station_no_login));
            return;
        }
        retrofitManager.getStationInfo(context, stationId, new StationPlatformInfoCallBack() {
            @Override
            public void onSuccess(StationPlatformInfo stationPlatformInfo) {
                if (null != stationPlatformInfo) {
                    initPlatformStationInfo(context, stationPlatformInfo);
                    callBack.onSuccess();
                } else {
                    callBack.onFailure(context.getString(R.string.single_station_toast_import_platform_station_failed));
                }
            }

            @Override
            public void onFailure(String message) {
                callBack.onFailure(message);
            }
        });
    }

    /**
     * 初始化平台基站列表测试数据
     *
     * @param context             上下文
     * @param stationPlatformInfo 平台获取的基站信息
     */
    private void initPlatformStationInfo(Context context, StationPlatformInfo stationPlatformInfo) {
        StationInfo stationInfo = new StationInfo();
        stationInfo.setFromType(StationInfo.FROM_TYPE_PLATFORM);
        stationInfo.setType(stationPlatformInfo.getSiteType() == StationPlatformInfo.MAPTYPE_INDOOR ? SingleStationDaoManager.STATION_TYPE_INDOOR : SingleStationDaoManager.STATION_TYPE_OUTDOOR);
        stationInfo.setCode(String.valueOf(stationPlatformInfo.getENodeBID()));
        stationInfo.setName(stationPlatformInfo.getSiteName());
        stationInfo.setLatitude(stationPlatformInfo.getLat());
        stationInfo.setLongitude(stationPlatformInfo.getLon());
        stationInfo.setENodeBID(stationPlatformInfo.getENodeBID());
        stationInfo.setAddress(stationPlatformInfo.getSiteAddress());
        stationInfo.setSiteId(stationPlatformInfo.getSiteId());
        stationInfo.setDeviceType(stationPlatformInfo.getDeviceType());
        this.mDaoManager.save(stationInfo);
        Long surveyStationInfoId = this.mDaoManager.createSurveyStationInfo(stationInfo);
        for (int i = 0;i < stationPlatformInfo.getCells().size();i++) {
            StationPlatformInfo.Cell cell = stationPlatformInfo.getCells().get(i);
            CellInfo cellInfo = new CellInfo();
            cellInfo.setAzimuth(cell.getAzimuth());
            cellInfo.setCellName(cell.getCellName());
            cellInfo.setCellId(cell.getECI());
            cellInfo.setHorizontalFalfPowerAngle(cell.getAntBeamWidth());
            cellInfo.setPA(cell.getPA());
            cellInfo.setPB(cell.getPB());
            cellInfo.setPCI(cell.getPCI());
            cellInfo.setRsPower(cell.getRsPower());
            cellInfo.setStationId(stationInfo.getId());
            this.mDaoManager.save(cellInfo);
            this.mDaoManager.createSurveyCellInfo(surveyStationInfoId, cellInfo);
        }
        this.initSceneTestResultList(context, stationInfo.getId(), stationPlatformInfo);
        this.initTestTaskFromPlatform(context, stationPlatformInfo);
    }
}
