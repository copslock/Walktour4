package com.walktour.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dingli.samsungvolte.SIPInfoModel;
import com.dingli.samsungvolte.VolteKeyModel;
import com.walktour.gui.task.activity.scannertsma.ScanTask5GOperateFactory;
import com.dinglicom.data.model.RecordTestInfo;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.AssetsWriter;
import com.walktour.control.bean.FileReader;
import com.walktour.control.config.ConfigNBModuleInfo;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.task.activity.scannertsma.ennnnum.WifiType;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.model.BuildingModel;
import com.walktour.model.FloorModel;
import com.walktour.model.UmpcTestInfo;
import com.walktour.service.GpsService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@SuppressLint("UseSparseArrays")
public class ApplicationModel {
    private static final String TAG = "ApplicationModel";
    private static ApplicationModel sInstance = new ApplicationModel();
    /**
     * 系统全局变量对象,存储测试状态是否启动;是否中断 如果当前任务状态为启动,则中断状态在任务进行中会用到.
     */
    public final static String TestTaskChanged = "com.walktour.TestTaskChanged";
    public final static String applicationDirectory = "walktour";

    public static int LockNetworkType = 7; // 锁定网络类型
    private int outLooptimes = 1; // 保存上次外循环次数
    private int outLoopInterval = 5; // 外循环的间隔时间
    private boolean outLoopDisconnetNetwork = true; // 外循环是否断开拨号
    private int screenWidth = 480; // 手机屏幕宽
    private int screenHeigth = 618; // 手机屏幕高
    private int encryFileResult = -1; // 文件加密结果
    private int controllerType = UmpcTestInfo.ControlForNone; // UMPC控制端类型

    private int activeTime = 0; // license有效天数
    private String activeDate; // license到期日期
    private boolean isPowerByNet = false; // 是否从网络中获取过权限
    private boolean autoTesting = false; // 是否自动测试的流程中(下载测试计划，测试，上传)
    private boolean testJobIsRun = false; // 测试任务是否运行中
    private boolean testStoping = false; // 测试停止中...
    private boolean testPause = false; // 暂停测试
    private boolean testStepPause = false; // 分步测试暂停
    private boolean testInterrupt = false; // 测试任务是否中断
    private boolean umpcRunning = false; // UMPC连接服务运行中
    private boolean isUmpcTest = false; // UMPC连接服务运行中

    private boolean traceInitSucc = false; // Trace设置初始化成功
    private boolean rcuFileCreated = false; // RCU文件创建成功
    private boolean isPioneer=false;//是否是与Pioneer测试
    private boolean monitorCalling = false; // 监控呼叫中
    private boolean monitorDataing = false; // 监控数据业务中

    private boolean environmentInit = false; // 应用环境已初始化
    private boolean freezeScreen = false; // 是否冻结屏幕

    private boolean isIndoorTest = false; // 是否室内测试
    private boolean isGyroTest = false; // 是否陀螺仪打点测试
    private boolean isGpsTest = false; // 是否gps测试
    private boolean isGerenalTest = false; // 是否普通测试
    private boolean isPreviouslyTest = false; // 是否预打点测试

    private boolean isWoneTest = false; // 是否Wone测试

    private boolean isShowActive = false; // 是否已经提示过到期

    private boolean isInit = false; // 是否在拷贝数据
    private boolean isBluetoothConnected = false; // 蓝牙同步服务是否已连接

    private boolean isWifiOpen = false; // 检测Wifi状态是否开启

    private String terminalSign = ""; // 连小背包时当前终端标识
    private WalkStruct.UMPCConnectStatus umpcStatus = WalkStruct.UMPCConnectStatus.Default; // umpc服务器连接状态

    private boolean isParmDragBack = false; // 是否动态参数拖动界面返回

    public boolean isBindXgSuccess = false;    //是否绑定信鸽成功
    private HashMap<Integer, Integer> currentTestNet = null;
    private HashMap<String, String> currentTestTaskList = null;

    /**
     * 用于蓝牙MOS测试录放音同步问题，该值为当前终端时间减去服务器获取到的时间的值
     */
    private long mServerTimeOffset = 0;
    /**
     * 当前是否寅时自动测试
     */
    private boolean isInnsmapTest = false;

    /**
     * 当前是否格纳微自动测试
     */
    private boolean isGlonavinTest = false;
    /**
     *  格纳微自动测试： 0：L1,L2    1:L3
     */
    private int glonavinType;

    private HashMap<String, RecordTestInfo> extendInfo = new HashMap<String, RecordTestInfo>();
    /**
     * 当前选择的场景
     */
    private SceneType mSelectScene = SceneType.Manual;
    /**
     * 当前执行的场景
     */
    private SceneType mRuningScene;
    /** volte密钥*/
    private VolteKeyModel mVolteKeyModel = null;
    /** sip信息*/
    private SIPInfoModel mSIPInfoModel = null;
    /** dcf 加密文件**/
    private String dcfEncryptName="causer.der";
    /**资源库句柄:参数*/
    private Integer handler_param=new Integer(-1);
    /**资源库句柄:事件*/
    private Integer handler_event=new Integer(-1);
    /**资源库句柄:业务*/
    private Integer handler_business=new Integer(-1);


    private ApplicationModel() {
    }


    public void addExtendInfo(String key, RecordTestInfo testInfo) {
        extendInfo.put(key, testInfo);
    }

    /**
     * 获得指定关键字扩展信息内容
     *
     * @param key
     * @return
     */
    public String getExtendInfoStr(String key) {
        if (extendInfo.containsKey(key)) {
            return extendInfo.get(key).key_value;
        }
        return "";
    }

    public HashMap<String, RecordTestInfo> getExtendInfo() {
        return extendInfo;
    }

    public boolean isGyroTest() {
        return isGyroTest;
    }

    public void setGyroTest(boolean isGyroTest) {
        this.isGyroTest = isGyroTest;
    }

    private boolean isScannerJobTest = false; // 是否扫频仪测试

    private WalkStruct.ScannerConnectStatus scannerStatus = WalkStruct.ScannerConnectStatus.Default; // 默认状态

    /**
     * @return the scannerStatus
     */
    public WalkStruct.ScannerConnectStatus getScannerStatus() {
        return scannerStatus;
    }

    /**
     * @param scannerStatus the scannerStatus to set
     */
    public void setScannerStatus(WalkStruct.ScannerConnectStatus scannerStatus) {
        this.scannerStatus = scannerStatus;
    }

    /**
     * @return the isScannerJobTest
     */
    public boolean isScannerJobTest() {
        return isScannerJobTest;
    }

    /**
     * @param isScannerJobTest the isScannerJobTest to set
     */
    public void setScannerJobTest(boolean isScannerJobTest) {
        this.isScannerJobTest = isScannerJobTest;
    }

    public boolean isPreviouslyTest() {
        return isPreviouslyTest;
    }

    public void setPreviouslyTest(boolean isPreviouslyTest) {
        this.isPreviouslyTest = isPreviouslyTest;
    }

    private boolean hasReceiveSMS = false; // 是否刚刚收到了短信

    public synchronized boolean isGpsTest() {
        return isGpsTest;
    }

    public synchronized void setGpsTest(boolean isGpsTest) {
        LogUtil.w("AppModel", "----set gps=" + isGpsTest);
        this.isGpsTest = isGpsTest;
    }

    public synchronized boolean isGerenalTest() {
        return isGerenalTest;
    }

    public synchronized void setGerenalTest(boolean isGerenalTest) {
        this.isGerenalTest = isGerenalTest;
    }

    private String copyMap = ""; // 室内测试要复制的地图

    private FloorModel floorModel;

    private BuildingModel buildModel;

    private String testMapPath = "";

    /**
     * add msi
     */
    private String buildNodeId = "";
    private String floorNodeId = "";



    public String getTestMapPath() {
        return testMapPath;
    }

    public void setTestMapPath(String testMapPath) {
        this.testMapPath = testMapPath;
    }

    public BuildingModel getBuildModel() {
        return buildModel;
    }

    public void setBuildModel(BuildingModel buildModel) {
        this.buildModel = buildModel;
    }

    public String getBuildNodeId() {
        return buildNodeId;
    }

    public void setBuildNodeId(String buildNodeId) {
        this.buildNodeId = buildNodeId;
    }

    public String getFloorNodeId() {
        return floorNodeId;
    }

    public void setFloorNodeId(String floorNodeId) {
        this.floorNodeId = floorNodeId;
    }

    private boolean needToCleanMap = false;// 开始测试后清除地图的标志

    private Date timeLimit = null; // 信令时间表

    // 当前执行任务类型
    private WalkStruct.TaskType currentTask = WalkStruct.TaskType.Default;
    // twq20131028当前事件类型存储，当前值目前可用于统计使用，如果指定业务，为数据业务时，据当当前值判断目前是否在firstData状态，非当前状态都不参与统计
    private boolean isFirstData = false;

    private boolean checkPowerSuccess = false; // 鉴权成功
    private boolean checkPowerRunning = false; // 鉴权进行中

    private ArrayList<WalkStruct.AppType> appList = new ArrayList<WalkStruct.AppType>(); // 用有的应用权限

    private ArrayList<WalkStruct.ShowInfoType> netList = new ArrayList<WalkStruct.ShowInfoType>(); // 拥有查看网络权限

    private ArrayList<WalkStruct.TaskType> taskList = new ArrayList<WalkStruct.TaskType>(); // 拥有业务测试权限

    private List<Integer> licenseKeyIDS=new LinkedList<Integer>();//存储所有权限的KEY ID
    public static ApplicationModel getInstance() {
        return sInstance;
    }

    /**
     * 收到TRACE服务回传的文件创建信息时，表示当前文件创建成功 测试开始及创建文件开始时，将该状态置为false
     *
     * @return the rcuFileCreated
     */
    public boolean isRcuFileCreated() {
        return rcuFileCreated;
    }

    /**
     * 收到TRACE服务回传的文件创建信息时，表示当前文件创建成功 测试开始及创建文件开始时，将该状态置为false
     *
     * @param rcuFileCreated the rcuFileCreated to set
     */
    public void setRcuFileCreated(boolean rcuFileCreated) {
        this.rcuFileCreated = rcuFileCreated;
    }

    public boolean isPioneer() {
        return isPioneer;
    }

    public void setPioneer(boolean pioneer) {
        isPioneer = pioneer;
    }

    public boolean isTestJobIsRun() {
        return testJobIsRun;
    }

    public void setTestJobIsRun(boolean testJobIsRun) {
        this.testJobIsRun = testJobIsRun;
    }

    /**
     * @return the testPause
     */
    public boolean isTestPause() {
        return testPause;
    }

    /**
     * @param testPause the testPause to set
     */
    public void setTestPause(boolean testPause) {
        this.testPause = testPause;
    }

    public boolean isTestInterrupt() {
        return testInterrupt;
    }

    public void setTestInterrupt(boolean testInterrupt) {
        this.testInterrupt = testInterrupt;
    }

    /**
     * 当前测试任务名称 当前测试后，该值为Default
     *
     * @return
     */
    public WalkStruct.TaskType getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(WalkStruct.TaskType currentTask) {
        this.currentTask = currentTask;
    }

    public boolean isFirstData() {
        return isFirstData;
    }

    public void setFirstData(boolean isFirstData) {
        this.isFirstData = isFirstData;
    }

    public synchronized boolean isTestStoping() {
        return testStoping;
    }

    public synchronized void setTestStoping(boolean testStoping) {
        this.testStoping = testStoping;
    }

    public synchronized boolean isTraceInitSucc() {
        if (this.isGeneralMode())
            return true;
        return traceInitSucc;
    }

    public synchronized void setTraceInitSucc(boolean traceInitSucc) {
        this.traceInitSucc = traceInitSucc;
    }

    public synchronized boolean isMonitorCalling() {
        return monitorCalling;
    }

    public synchronized void setMonitorCalling(boolean monitorCalling) {
        this.monitorCalling = monitorCalling;
    }

    public synchronized boolean isMonitorDataing() {
        return monitorDataing;
    }

    public synchronized void setMonitorDataing(boolean monitorDataing) {
        this.monitorDataing = monitorDataing;
    }

    public synchronized boolean isEnvironmentInit() {
        return environmentInit;
    }

    public synchronized void setEnvironmentInit(boolean environmentInit) {
        this.environmentInit = environmentInit;
    }

    public synchronized boolean isFreezeScreen() {
        return freezeScreen;
    }

    public synchronized void setFreezeScreen(boolean freezeScreen) {
        this.freezeScreen = freezeScreen;
    }

    public synchronized void cleanApp() {
        appList.clear();
    }

    public synchronized void addApp(WalkStruct.AppType app) {
        appList.add(app);
    }

    public synchronized void addAppList(String[] appListStr) {
        appList.clear();
        for (int i = 0; i < appListStr.length; i++) {
            try {
                this.addApp(WalkStruct.AppType.valueOf(appListStr[i]));
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
            }
        }
    }

    public synchronized ArrayList<WalkStruct.AppType> getAppList() {
        return appList;
    }

    public synchronized void cleanNet() {
        netList.clear();
    }

    public synchronized void addNet(WalkStruct.ShowInfoType net) {
        netList.add(net);
    }

    public synchronized void addNetList(String[] netListStr) {
        netList.clear();
        for (int i = 0; i < netListStr.length; i++) {
            try {
                this.addNet(WalkStruct.ShowInfoType.valueOf(netListStr[i]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized ArrayList<WalkStruct.ShowInfoType> getNetList() {
        return netList;
    }

    private synchronized void addTask(WalkStruct.TaskType task) {
        taskList.add(task);
    }

    public synchronized void addTaskList(String[] taskListStr) {
        taskList.clear();
        if (taskListStr != null) {
            for (int i = 0; i < taskListStr.length; i++) {
                try {
                    Log.d("task", taskListStr[i]);
                    for (WalkStruct.TaskType type : WalkStruct.TaskType.values()) {
                        if (type.name().equals(taskListStr[i])) {
                            this.addTask(type);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /***
     * 所有权限的ID的集合,对应PowerList中的serialId
     * @return
     */
    public List<Integer> getLicenseKeyIDS() {
        return licenseKeyIDS;
    }
    public synchronized ArrayList<WalkStruct.TaskType> getTaskList() {
        return taskList;
    }

    public synchronized boolean isCheckPowerSuccess() {
        return checkPowerSuccess;
    }

    public synchronized void setCheckPowerSuccess(boolean checkPowerSuccess) {
        this.checkPowerSuccess = checkPowerSuccess;
    }

    public boolean isCheckPowerRunning() {
        return checkPowerRunning;
    }

    public void setCheckPowerRunning(boolean checkPowerRunning) {
        this.checkPowerRunning = checkPowerRunning;
    }

    public synchronized int getScreenWidth() {
        return screenWidth;
    }

    public synchronized void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public synchronized int getScreenHeight() {
        return screenHeigth;
    }

    public synchronized void setScreenHeigth(int screenHeigth) {
        this.screenHeigth = screenHeigth;
    }

    public synchronized Date getTimeLimit() {
        return timeLimit;
    }

    public synchronized FloorModel getFloorModel() {
        return floorModel;
    }

    public synchronized void setFloorModel(FloorModel floorModel) {
        this.floorModel = floorModel;
    }

    public synchronized void setTimeLimit(Date timeLimit) {
        this.timeLimit = timeLimit;
    }

    public synchronized boolean isIndoorTest() {
        return isIndoorTest;
    }

    public synchronized void setIndoorTest(boolean isIndoorTest) {
        this.isIndoorTest = isIndoorTest;
    }

    public synchronized String getCopyMap() {
        return copyMap;
    }

    public synchronized void setCopyMap(String mapPath) {
        this.copyMap = mapPath;
    }

    public synchronized boolean isNeedToCleanMap() {
        return needToCleanMap;
    }

    public synchronized void setNeedToCleanMap(boolean needToCleanMap) {
        this.needToCleanMap = needToCleanMap;
    }

    /**
     * 是否执行过网络获取权限动作
     */
    public boolean isPowerByNet() {
        return isPowerByNet;
    }

    /**
     * 网络获取权限成功状态
     */
    public void setPowerByNet(boolean isPowerByNet) {
        this.isPowerByNet = isPowerByNet;
    }

    /**
     * 是否自动测试的流程中(下载测试计划，测试，上传)
     */
    public synchronized boolean isTesting() {
        return autoTesting;
    }

    /**
     * 设置当前是否自动测试的流程中(下载测试计划，测试，上传)
     */
    public synchronized void setAutoTesting(boolean autoTesting) {
        this.autoTesting = autoTesting;
    }

    /**
     * @return the umpcRunning
     */
    public synchronized boolean isUmpcRunning() {
        return umpcRunning;
    }

    /**
     * @param umpcRunning the umpcRunning to set
     */
    public synchronized void setUmpcRunning(boolean umpcRunning) {
        this.umpcRunning = umpcRunning;
    }

    public boolean isUmpcTest() {
        return isUmpcTest;
    }

    public void setUmpcTest(boolean isUmpcTest) {
        this.isUmpcTest = isUmpcTest;
    }

    public synchronized boolean isHasReceiveSMS() {
        boolean result = hasReceiveSMS;
        hasReceiveSMS = false;
        return result;
    }

    public synchronized void setHasReceiveSMS(boolean hasReceiveSMS) {
        this.hasReceiveSMS = hasReceiveSMS;
    }

    public synchronized WalkStruct.UMPCConnectStatus getUmpcStatus() {
        return umpcStatus;
    }

    public synchronized void setUmpcStatus(WalkStruct.UMPCConnectStatus umpcStatus) {
        LogUtil.w("ApplicationModel", "----setUmpcStatus----" + umpcStatus.name());
        this.umpcStatus = umpcStatus;
    }

    /**
     * 获得小背包连接后,IPAD端下发通知的当前终端标识
     *
     * @return
     */
    public String getTerminalSign() {
        return terminalSign;
    }

    /**
     * 设置小背包连接后,IPAD端下发通知的当前终端标识
     *
     * @param terminalSign
     */
    public void setTerminalSign(String terminalSign) {
        this.terminalSign = terminalSign;
    }

    /**
     * @return the outLooptimes
     */
    public int getOutLooptimes() {
        return outLooptimes;
    }

    /**
     * @param outLooptimes the outLooptimes to set
     */
    public void setOutLooptimes(int outLooptimes) {
        this.outLooptimes = outLooptimes;
    }

    /**
     * @return the encryFileResult
     */
    public int getEncryFileResult() {
        return encryFileResult;
    }

    /**
     * @param encryFileResult the encryFileResult to set
     */
    public void setEncryFileResult(int encryFileResult) {
        this.encryFileResult = encryFileResult;
    }

    /**
     * 返回当前UMPC连接类型 0 IPAD,1 Pioneer,2 AndroidPad
     */
    public int getControllerType() {
        return controllerType;
    }

    public void setControllerType(int controllerType) {
        this.controllerType = controllerType;
    }

    public int getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(int activeTime) {
        this.activeTime = activeTime;
    }

    public String getActiveDate(){
        return activeDate;
    }

    public void setActiveDate(String date){
        this.activeDate = date;
    }

    public boolean isShowActive() {
        return isShowActive;
    }

    public void setShowActive(boolean isShowActive) {
        this.isShowActive = isShowActive;
    }

    public synchronized boolean isInit() {
        return isInit;
    }

    public synchronized void setInit(boolean isInit) {
        this.isInit = isInit;
    }

    /**
     * 蓝牙同步服服务是否已连接成功
     *
     * @return
     */
    public boolean isBluetoothConnected() {
        return isBluetoothConnected;
    }

    public void setBluetoothConnected(boolean isBluetoothConnected) {
        this.isBluetoothConnected = isBluetoothConnected;
    }

    public synchronized boolean isWoneTest() {
        return isWoneTest;
    }

    public synchronized void setWoneTest(boolean isWoneTest) {
        this.isWoneTest = isWoneTest;
    }

    /**
     * 是否华为工单项目
     *
     * @return
     */
    public boolean isHuaWeiTest() {
        return this.netList.contains(WalkStruct.ShowInfoType.HWWorkOrder)&&AppVersionControl.getInstance().isHuaWeiTest();
    }

    /**
     * 是否北京测试项目
     *
     * @return
     */
    public boolean isBeiJingTest() {
        return false;
    }

    /**
     * 是否福建投诉工单项目
     *
     * @return
     */
    public boolean isFuJianTest() {
        return this.netList.contains(WalkStruct.ShowInfoType.FJWorkOrder);
    }

    /**
     * 是否安徽工单项目
     *
     * @return
     */
    public boolean isAnHuiTest() {
        return this.netList.contains(WalkStruct.ShowInfoType.AHWorkOrder);
    }

    /**
     * 是否是单站验证
     *
     * @return
     */
    public boolean isSingleStationTest() {
//        return true;
        return this.netList.contains(WalkStruct.ShowInfoType.SingleStation);
    }


    /**
     * 是否有Btu权限
     *
     * @return
     */
    public boolean isBtu() {
        return this.netList.contains(WalkStruct.ShowInfoType.Btu);
    }

    /**
     * 是否具有NB模块权限,仅仅判断是否有权限
     * @return
     */
    public boolean isNB(){
        return this.netList.contains(WalkStruct.ShowInfoType.NBIoT);
    }

    /***
     * 是否是NB测试，包括NB USB测试或者NB WIFI测试
     * @return
     */
    public boolean isNBTest(){
        return isNB()&&(Deviceinfo.getInstance().isHasNBUsbTestModel()||ConfigNBModuleInfo.getInstance(WalktourApplication.getAppContext()).isHasNBWifiTestModel());
    }
    /**
     * 是否有Atu权限
     *
     * @return
     */
    public boolean isAtu() {
        return this.netList.contains(WalkStruct.ShowInfoType.Atu);
    }

    /**
     * 是否有dcf权限
     * @return
     */
    public boolean hasDcf(){
        return this.netList.contains(WalkStruct.ShowInfoType.DCF);
    }
    /**
     * 是否有Org RCU权限
     * @return
     */
    public boolean hasOrgRcu(){
        return this.netList.contains(WalkStruct.ShowInfoType.CreateBin);
    }

    /**
     * 是否有DTLog权限
     * @return
     */
    public boolean hasDTLog(){
        return isAtu() || isBtu();
    }
  /**
     * 是否有PCap权限
     * @return
     */
    public boolean hasPCap(){
        return this.netList.contains(WalkStruct.ShowInfoType.TcpIpPcap);
    }

    /**
     * 当明是否扫频测试
     */
    public boolean isScannerTest() {
        return hasScannerTest() && ConfigRoutine.getInstance().isUseScanner(WalktourApplication.getAppContext());
//        return true;
    }

    /**
     * 是否有扫频仪权限
     * @return
     */
    public boolean hasScannerTest(){
        return getAppList().contains(WalkStruct.AppType.ScannerTest);
//        return true;
    }

    /**
     * 当明是否扫频测试
     */
    public boolean isScannerTSMATest() {
        return hasScannerTSMATest() && ConfigRoutine.getInstance().isUseTSMAScanner(WalktourApplication.getAppContext());
    }
    /**
     * 是否有ScannerTSMA测试权限
     *
     * @return
     */
    public boolean hasScannerTSMATest(){

       return false;

    }


    public WifiType getWifiType(Context context){
        int wifiType=SharePreferencesUtil.getInstance(context).getInteger(WalktourConst.WifiType.WIFI_TYPE);
      return   WifiType.getWifiTypeByCode(wifiType);
    }
    /**
     * 判断扫频仪列表是否为空
     */
    public boolean isScannerTestTask() {
        return ScanTask5GOperateFactory.getInstance().hasEnableTask();

    }

    /***
     * 是否具有LTE Data分析权限,独立出来,方便使用
     * @return
     */
    public boolean isCSFB() {
        if (getNetList().contains(WalkStruct.ShowInfoType.CSFBAnalysis)) {
            return true;
        }
        return false;
    }

    /***
     * 是否具有LTE Data分析权限,独立出来,方便使用
     * @return
     */
    public boolean isVoLTE() {
        if (getNetList().contains(WalkStruct.ShowInfoType.VoLTEAnalyse)) {
            return true;
        }
        return false;
    }

    /***
     * 是否具有LTE Data分析权限,独立出来,方便使用
     * @return
     */
    public boolean isLTEData() {
//		if(getNetList().contains(WalkStruct.ShowInfoType.LTEDataAnalysis)) {
        return true;
//		}
//		 return false;
    }

    /***
     * 是否有智能分析按钮，取决于是否存在一个权限：csfb,volte,ltedata,只要存在一个权限，此按钮即显示,否则隐藏.
     * @return
     */
    public boolean isIntelligentAnalysis() {
        if (isGeneralMode())//通用版本不显示此按钮
            return false;
        if (isCSFB()) {
            return true;
        } else if (isVoLTE()) {
            return true;
        } else if (isLTEData()) {
            return true;
        }
        return false;
    }

    public boolean isTestStepPause() {
        return testStepPause;
    }

    public void setTestStepPause(boolean testStepPause) {
        this.testStepPause = testStepPause;
    }

    public int getOutLoopInterval() {
        return outLoopInterval;
    }

    public void setOutLoopInterval(int outLoopInterval) {
        this.outLoopInterval = outLoopInterval;
    }

    public boolean isOutLoopDisconnetNetwork() {
        return outLoopDisconnetNetwork;
    }

    public void setOutLoopDisconnetNetwork(boolean outLoopDisconnetNetwork) {
        this.outLoopDisconnetNetwork = outLoopDisconnetNetwork;
    }

    /**
     * 是否在测试结束时保存当前的地图轨迹
     *
     * @return
     */
    public boolean isSaveMapLocas() {
        return false;
    }

    public boolean isParmDragBack() {
        return isParmDragBack;
    }

    public void setParmDragBack(boolean isParmDragBack) {
        this.isParmDragBack = isParmDragBack;
    }

    /**
     * 是否通用版本不带root <BR>
     * 通用版只支持的DCF的文件格式
     */
    public boolean isGeneralMode() {
        // if (!WalktourApplication.isRootSystem())
        // return true;
        return false;
    }

    /**
     * 开始测试前,初始化测试记录的业务,网络信息,或清楚上次网络信息
     */
    public void initCurrentTestInfo() {
        currentTestNet = new HashMap<Integer, Integer>();
        currentTestTaskList = new HashMap<String, String>();
    }

    public HashMap<Integer, Integer> getCurrentTestNet() {
        return currentTestNet;
    }

    public void addCurrentTestNet(Integer key, Integer value) {
        if (currentTestNet == null) {
            currentTestNet = new HashMap<Integer, Integer>();
        }
        if (!currentTestNet.containsKey(key)) {
            this.currentTestNet.put(key, value);
        }
    }

    public HashMap<String, String> getCurrentTestTaskList() {
        return currentTestTaskList;
    }

    public void addCurrentTestTaskList(String key, String value) {
        if (currentTestTaskList == null) {
            currentTestTaskList = new HashMap<String, String>();
        }
        if (!currentTestTaskList.containsKey(key)) {
            this.currentTestTaskList.put(key, value);
        }
    }

    public boolean isWifiOpen() {
        return isWifiOpen;
    }

    public void setWifiOpen(boolean isWifiOpen) {
        this.isWifiOpen = isWifiOpen;
    }

    public SceneType getSelectScene() {
        return mSelectScene;
    }
    /***
     * 设置场景,更新文件名,重新加载xml测试任务
     * @param selectScene
     */
    public void setSelectScene(SceneType selectScene) {
        mSelectScene = selectScene;
        TaskListDispose.getInstance().updateCurrentFileName(selectScene);
        TaskListDispose.getInstance().reloadFromXML();
    }

    public SceneType getRuningScene() {
        return mRuningScene;
    }

    public void setRuningScene(SceneType runingScene) {
        mRuningScene = runingScene;
    }

    /***
     * 获取业务测试所包含的所有场景信息
     * @return
     */
    public List<SceneType> getBusinessTestScenes() {
        List<SceneType> scenes = new LinkedList<SceneType>();
        //业务测试默认加入Manual的场景
        scenes.add(SceneType.Manual);
        if (isSingleStationTest())
            scenes.add(SceneType.SingleSite);
        if(AppVersionControl.getInstance().isPerceptionTest()){
            scenes.add(SceneType.Perception);
        }
        if (this.isAnHuiTest())
            scenes.add(SceneType.Anhui);
        if (this.isHuaWeiTest())
            scenes.add(SceneType.Huawei);
        if (this.isFuJianTest())
            scenes.add(SceneType.Fujian);
        if (this.isAtu()) {
            scenes.add(SceneType.ATU);
            scenes.add(SceneType.MultiATU);
        }
        if (this.isBtu())
            scenes.add(SceneType.BTU);
        if (this.isScannerTest())
            scenes.add(SceneType.Scanner);
        if (this.isWoneTest())
            scenes.add(SceneType.Wone);
        return scenes;
    }

    /***
     * 获取数据连接类型，PPP,WIFI,NBPPP
     *
     * @return
     */
    public List<String> getConnectType(){
        List<String> dataConnType = new ArrayList<String>();
        dataConnType.add("PPP");
        if (getNetList().contains(WalkStruct.ShowInfoType.WLAN)) {
            dataConnType.add("WIFI");
        }
        if(getNetList().contains(WalkStruct.ShowInfoType.NBIoT)){
            dataConnType.add("NBPPP");
        }
        return dataConnType;
    }

    /***
     * 控制是否切Logmask
     * @return
     */
    public boolean hasSamsungVoLTE() {
//        return netList.contains(WalkStruct.ShowInfoType.SAMSUNGVoLTE)
//                && Deviceinfo.getInstance().isVoLTENet();
//                && Deviceinfo.getInstance().isSAMSUNG();//三星手机的ModelName必须SAMSUNG开头，作为和别的机型区别开来

        if(Deviceinfo.getInstance().isVoLTENet()){//当voltenet设置为1时,一定切logmask.
            return true;
        }else{
            if(netList.contains(WalkStruct.ShowInfoType.SAMSUNGVoLTE)){//当有三星Volte权限时,一定切logmask.
                return true;
            }
            //否则不切
            return false;
        }
    }

    /**
     * 是否有寅时测试权限
     *
     * @return
     */
    public boolean hasInnsmapTest() {
        return appList.contains(WalkStruct.AppType.InnsMap);
    }

    /**
     * 是否有使用内置polqa文件算分权限
     * @return
     */
    public boolean hasPolqaType(){
        return appList.contains(WalkStruct.AppType.PolqaType);
    }

    //是否具有CU权限
    public boolean showInfoTypeCu() {
        return getNetList().contains(WalkStruct.ShowInfoType.CU);
    }

    //是否具有eCTI权限
    public boolean showInfoTypeEcti() {
        return getNetList().contains(WalkStruct.ShowInfoType.eCTI);
    }
    public boolean showInfoTypeOTS(){
        return getNetList().contains(WalkStruct.ShowInfoType.OTS);
    }
    public boolean isInnsmapTest() {
        return this.isInnsmapTest;
    }

    public void setInnsmapTest(boolean isInnsmapTest) {
        this.isInnsmapTest = isInnsmapTest;
    }

    public boolean isGlonavinTest(){
        return this.isGlonavinTest;
    }

    public void setGlonavinTest(boolean isGlonavinTest){
        this.isGlonavinTest = isGlonavinTest;
    }
    public int getGlonavinType(){
        return this.glonavinType;
    }

    public void setGlonavinType(int glonavinType){
        this. glonavinType = glonavinType;
    }

    public Integer getHandler_param()
    {
        return handler_param;
    }

    public Integer getHandler_event()
    {
        return handler_event;
    }

    public Integer getHandler_business()
    {
        return handler_business;
    }

    public VolteKeyModel getVolteKeyModel() {
        return mVolteKeyModel;
    }
    public void setVolteKeyModel(VolteKeyModel volteKeyModel) {
        mVolteKeyModel = volteKeyModel;
    }
    public SIPInfoModel getSIPInfoModel() {
        return mSIPInfoModel;
    }
    public void setSIPInfoModel(SIPInfoModel sIPInfoModel) {
        mSIPInfoModel = sIPInfoModel;
    }

    public long getServerTimeOffset() {
        return mServerTimeOffset;
    }

    public void setServerTimeOffset(long serverTimeOffset) {
        mServerTimeOffset = serverTimeOffset;
    }

    /**
     * 获取dcf加密文件,请不要将causer.der放在asserts目录下,否则设置中会隐藏文件格式设置
     * @return
     */
    public byte[] getDcfEncryptKey(Context mContext){
        byte[] dcfEncryptKey = new AssetsWriter(mContext, dcfEncryptName).getFileBytes();
        if(dcfEncryptKey.length == 0){
            dcfEncryptKey = new FileReader().getFileBytes(AppFilePathUtil.getInstance().getSDCardBaseFile("config",dcfEncryptName).getAbsolutePath());
        }
        return dcfEncryptKey;
    }

    public boolean isInOutSwitchMode() {
        return isInOutSwitchMode;
    }

    public void setInOutSwitchMode(boolean inOutSwitchMode) {
        isInOutSwitchMode = inOutSwitchMode;
    }
    /*
     * 是否为室内，室外切换模式*/
    boolean isInOutSwitchMode=false;

    public void in2out(Context context){
        if (isTestJobIsRun()&&isInOutSwitchMode){
            setGpsTest(true);
//            setIndoorTest(false);
            setNeedToCleanMap(false);
            MapFactory.setLoadIndoor(false);
            context.startService(new Intent(context,GpsService.class));
        }
    }
    public void out2in(Context context){
        if (isTestJobIsRun()&&isInOutSwitchMode) {
            setGpsTest(false);
//            setIndoorTest(true);
            setNeedToCleanMap(false);
            MapFactory.setLoadIndoor(true);
            context.stopService(new Intent(context, GpsService.class));
        }
    }

}
