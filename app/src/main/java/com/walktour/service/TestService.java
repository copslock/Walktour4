package com.walktour.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.dingli.samsungvolte.SIPInfoModel;
import com.dingli.samsungvolte.VolteKeyModel;
import com.dingli.seegull.SeeGullService;
import com.dinglicom.DataSetLib;
import com.dinglicom.UnicomInterface;
import com.dinglicom.autotesthandy.script.run.ScriptRun;
import com.dinglicom.autotesthandy.script.service.ScriptService;
import com.dinglicom.autotesthandy.script.util.ByteUtil;
import com.dinglicom.data.control.BuildTestRecord;
import com.dinglicom.data.control.DataTableStruct;
import com.dinglicom.data.control.DataTableStruct.RecordDetailEnum;
import com.dinglicom.data.control.DataTableStruct.RecordInfoKey;
import com.dinglicom.data.control.DataTableStruct.RecordNetTypeEnum;
import com.dinglicom.data.control.DataTableStruct.RecordTaskTypeEnum;
import com.dinglicom.data.control.DataTableStruct.TestRecordEnum;
import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.TestRecord;
import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.EventManager;
import com.dinglicom.dataset.TotalInterface;
import com.dinglicom.dataset.model.DataSetEvent;
import com.dinglicom.dataset.model.EventModel;
import com.dinglicom.wifi.WifiPortalManager;
import com.dinglicom.wifi.service.WifiScanService;
import com.walktour.Utils.APNOperate;
import com.walktour.Utils.AlertWakeLock;
import com.walktour.Utils.AppVersionControl;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.DataSetFileUtil;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.ForPioneerInfo;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.MobileInfoUtil;
import com.walktour.Utils.NetWorkAlive;
import com.walktour.Utils.PPPRule;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.ServerMessage;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.ShowInfo;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalDial;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UmpcSwitchMethod;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.UtilsMethodPara;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.Alarm;
import com.walktour.Utils.WalkStruct.CurrentNetState;
import com.walktour.Utils.WalkStruct.DataTaskValue;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.Utils.WalkStruct.ServerOperateType;
import com.walktour.Utils.WalkStruct.ShowInfoType;
import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.Utils.WalkStruct.TestType;
import com.walktour.Utils.WalkStruct.UMPCEventType;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.DateUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.VoiceAnalyse;
import com.walktour.control.bean.FileReader;
import com.walktour.control.bean.MyPhone;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigAPN;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.config.ParameterSetting;
import com.walktour.control.config.ServerManager;
import com.walktour.control.instance.AlertManager;
import com.walktour.control.instance.DataManagerFileList;
import com.walktour.control.netsniffer.Command;
import com.walktour.externalinterface.AidlTestControllor;
import com.walktour.externalinterface.event.AidlEvnet;
import com.walktour.gui.R;
import com.walktour.gui.WalkTour;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.highspeedrail.GService;
import com.walktour.gui.highspeedrail.HighSpeedGpsService;
import com.walktour.gui.indoor.TestInfoValue;
import com.walktour.gui.locknet.ForceControler.Band;
import com.walktour.gui.locknet.ForceControler.ForceNet;
import com.walktour.gui.locknet.ForceManager;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.map.MapView;
import com.walktour.gui.mutilytester.MutilyTester;
import com.walktour.gui.setting.KPIResulActivity;
import com.walktour.gui.setting.SysBuildingManager;
import com.walktour.gui.setting.customevent.CustomEventFactory;
import com.walktour.gui.setting.customevent.model.CustomEventParam;
import com.walktour.gui.setting.customevent.model.Param;
import com.walktour.gui.task.activity.scannertsma.ScanTask5GOperateFactory;
import com.walktour.gui.task.activity.scannertsma.model.ScanEventModel;
import com.walktour.gui.task.activity.scannertsma.model.ScanTaskModel;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.WifiConnectionInfo;
import com.walktour.gui.task.parsedata.model.task.ftp.TaskFtpModel;
import com.walktour.gui.task.parsedata.model.task.http.page.TaskHttpPageModel;
import com.walktour.gui.task.parsedata.model.task.http.upload.TaskHttpUploadModel;
import com.walktour.gui.task.parsedata.model.task.idle.TaskEmptyModel;
import com.walktour.gui.task.parsedata.model.task.moc.MOCTestConfig;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.gui.task.parsedata.model.task.mtc.MTCTestConfig;
import com.walktour.gui.task.parsedata.model.task.mtc.TaskPassivityCallModel;
import com.walktour.gui.task.parsedata.model.task.multirab.ParallelServiceTestConfig;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.ott.TaskWeCallModel;
import com.walktour.gui.task.parsedata.model.task.ping.TaskPingModel;
import com.walktour.gui.task.parsedata.model.task.videoplay.TaskVideoPlayModel;
import com.walktour.gui.task.parsedata.model.task.wlan.ap.TaskWlanApModel;
import com.walktour.gui.task.parsedata.model.task.wlan.eteauth.TaskWlanEteAuthModel;
import com.walktour.gui.task.parsedata.model.task.wlan.login.TaskWlanLoginModel;
import com.walktour.gui.task.parsedata.txt.TestPlan;
import com.walktour.model.AlarmModel;
import com.walktour.model.NetStateModel;
import com.walktour.model.TestDoneStateModel;
import com.walktour.model.TestObjectModel;
import com.walktour.model.TotalSpecialModel;
import com.walktour.model.UmpcTestInfo;
import com.walktour.model.UrlModel;
import com.walktour.netsniffer.NetSnifferService;
import com.walktour.netsniffer.NetSnifferServiceUtil;
import com.walktour.service.app.Killer;
import com.walktour.service.app.datatrans.model.UploadFileModel;
import com.walktour.service.automark.AutoMarkService;
import com.walktour.service.automark.constant.AutoMarkConstant;
import com.walktour.service.automark.constant.MarkScene;
import com.walktour.service.automark.glonavin.Glonavin3in1AutoMarkNotCommonService;
import com.walktour.service.automark.glonavin.Glonavin3in1AutoMarkService;
import com.walktour.service.automark.glonavin.Glonavin3in1GService;
import com.walktour.service.automark.glonavin.Glonavin3in1MetroService;
import com.walktour.service.automark.glonavin.GlonavinAutoMarkNotCommonService;
import com.walktour.service.automark.glonavin.GlonavinAutoMarkService;
import com.walktour.service.bluetoothmos.BluetoothMOSFactory;
import com.walktour.service.bluetoothmos.BluetoothMOSService;
import com.walktour.service.bluetoothmos.BluetoothMTCService;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;
import com.walktour.service.innsmap.InnsmapTestService;
import com.walktour.service.metro.MetroTestService;
import com.walktour.service.phoneinfo.utils.MobileUtil;
import com.walktour.service.test.MOCTest;
import com.walktour.service.test.TestTaskService;
import com.walktour.workorder.model.Loglabel;

import org.andnav.osm.util.GeoPoint;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestService extends Service {
    /**
     * 日志标识
     */
    private static final String TAG = "TestService";
    public static final String RESULT_SUCCESS = "1";
    public static final String RESULT_FAILD = "faild";
    public static final String RESULT_FAIL_REDIAL = "fail.redial";    //业务失败并且要重新拨号
    //    public static final String RESULT_FAIL_NEXT = "fail.next";        //业务失败并且直接跳到下一个业务
    public static final String RESULT_BUNDLE_NULL = "0";                //业务库崩溃后的服务自动启动

    private static final int SPECIALDATT_TYPE_ADBSIP = 1;
    //VOLTE密钥
    private static final int SPECIALDATT_TYPE_SIPKEY = 4;
    //VOLTE通话结束
    private static final int SPECIALDATT_TYPE_SIPEND = 5;

    //private IService 	mService; 										//远程服务
    ApplicationModel appModel = ApplicationModel.getInstance();    //全局对象,存储测试启动,中断状态
    private Deviceinfo deviceInfo = Deviceinfo.getInstance();            //
    TaskListDispose taskd = TaskListDispose.getInstance();    //获取系统测试任务列表信息
    ForPioneerInfo pioneerInfo = ForPioneerInfo.getInstance();     //获得往Pioneer的实时参数
    TaskTestObject taskMethod = null;                            //实例化测试任务用到的相关方法
    private ConfigRoutine routineSet = null;                            //系统设置环境信息
    private TaskModel currentTaskModel = null;
    private String telecomOrderStr = "";                        //电信工单最终命名
    /**
     * 并发业务指定相对事件准备完成
     */
    private boolean rabEventReady = false;
    private boolean doneCurrentTask = false;                    //当前任务测试完成状态
    private boolean taskListEmpty = false;                    //任务列表为空
    private boolean isAutoTest = false;                    //如果当前为自动测试，以此状态控制自动测试过程中GPS等相关状态
    private boolean isUmpcTest = false;                    //如果当前为UMPC测试，以此状态修改最终的RCU文件名
    private boolean isBluetoothSync = false;                    //蓝牙同步测试
    private boolean isNetsniffer = false;                    //是否抓包
    private boolean isDontSaveFile = false;                    //不保存测试文件设置
    private boolean isDontSwitchAirplane = false;                    //是否不切换飞行模式
    private static boolean isSyncCircle = false;                //UMPC测试时外循环需要同步状态
    private boolean hasCall = false;                    //有语音业务时该状态为真.创建文件时,如果有语音业务,设置为不判断起呼原因码
    private boolean hasReadyFromMOC = false;                    //被叫用：主叫是否准备好
    private boolean hasReadyFromMTC = false;                    //主叫用:被叫是否准备好
    private boolean hasHangupFromMOC = false;                    //被叫用：否收到的主叫的挂机指令
    private boolean hasHangupAckFromMTC = false;                    //主叫用：被叫收到挂机指令并响应
    private boolean hasMOCTaskFinish = false;                    //是否一个MOC任务的次数被终结
    private boolean hasMOCMOSTest = false;                    //当前次测试是否有主叫MOS算分业务
    //发送接收头对齐
    private boolean hasReadyFromSmsSender = false;                    //短信发送测试任务开始
    private boolean hasReadyFromSmsRecver = false;                    //短信接收测试任务开始
    //发送接收尾对齐
    private boolean hasStopFromSmsSender = false;                    //短信发送测试任务结束
    private boolean hasStopAckFromSmsRcver = false;                    //短信接收测试任务结束

    //发送接收头对齐
    private boolean hasReadyFromMmsSender = false;                    //彩信发送测试任务开始
    private boolean hasReadyFromMmsRecver = false;                    //彩信接收测试任务开始
    //发送接收尾对齐
    private boolean hasStopFromMmsSender = false;                    //彩信发送测试任务结束
    private boolean hasStopAckFromMmsRcver = false;                    //彩信接收测试任务结束

    private boolean isRecordCall = false;//是否起呼录制录音

    private UmpcTestInfo umpcTestinfo = null;                        //如果当前为UMPC测试，此处存储UMPC开始测试时的相关信息
    //private int 	encryptFileResult	= -1;						//文件加密结果0表示失败，1表示成功，其它为无值
    private DecimalFormat fileNameFormat = new DecimalFormat("000000");
    private ShowInfo showInfo = null;
    private Timer timer = new Timer();
    private String times = "";
    private String createRcuFilePath = "";    //全路径
    private String rcuFilePath = "";
    private String rcuFileName = "";
    private String doneJobName = "";
    //    private long startRcuTime = 0;    //开始记录RCU文件时间
//    private long stopRcuTime = 0;    //停止记录RCU文件时间
    private static final int testStopredoTraceInit = 1;    //测试停止并重启串口线程
    private static final int testTaskListEmpty = 2;    //测试任务列表为空
    private static final int testTaskNotPower = 3;    //没有当前任务测试权限
    private MyPhone myPhone;
    private MyPhoneState myPhoneState;                /*监听手机信号*/
    //private Intent	totalDetailService		= null;	//统计明细服务
//	private boolean hasUploadGpsDone        = false;
    private boolean isFirstDataJobByMulti = false;//是否并行业务中的第一个数据业务
    private String theFirstDataNameByMulti = "";   //并行业务第一个数据业务名称

    private NetSnifferService netSnifferService;//抓包服务
    /**
     * 电信招标需要,小背包测试时返回当前文件的序号 该序号从软件安装开始从0递增
     */
    private int currentFileNum = -1;
    /**
     * 单机加密文件路径信息
     */
    private final String entryptioneFilePath = Environment.getExternalStorageDirectory().getPath() + "/walktour/config/dingli.wskf";
    /**
     * 测试任务网络类型—VOLTE
     */
    private final String TASK_NET_TYPE_VOLTE = "VOLTE";
    /**
     * 测试任务网络类型—2G语音
     */
    private final String TASK_NET_TYPE_2G = "2G语音";

    /**
     * 当前手机版本号
     */
    private String mAppVersionName;
    /**
     * 一次循环不同业务间隔时长
     */
    private int mDifferentTaskInterval;

    @Override
    public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
        super.sendOrderedBroadcast(intent, receiverPermission);
    }

    /**
     * 密钥文件信息
     */
    private String entryptioneKey = null;
    //UMPC控制相关
    private int testGroupIndex = 0;    //测试任务组序号
    private int outCircleTimes = 1;    //外循环次数设置
    private int currentCircle = 0;    //当前外循环次数
    private int outCircleInterval = 5;    //外循环间隔时长
    private int currentTaskIndex = 0;    //当前任务所在测试任务列表的位置序号，目前仅用于外循环大于1时的时间控制用
    private List<String> testPlanWList = new ArrayList<>();    //已添文件列表,新创建文件时需要清空测试计划
    private boolean outCircleDisconnetNetwork = false;    //如当前拨号未断开，是否强制断开拨号
    private boolean needToSplieFile = false;    //当前测试过程串口重初始过，文件写入时需求分割标志
    private Double rcuFileSizeLimit = 0.0;    //RCU文件大小限制，当该值大于0时表示需要对当前测试次数进行文件分割
    private int rcuFileTimeLimit = 0;    //RCU文件大小按时间限制，当该值大于0时表示需要对当前测试进行分割,时间秒
    private String splitFileRcuName = "";   //用户记录除UMPC测试外，需要做文件分割时第一个RCU名字，当为“”时替换为当前文件名
    private long rcuFilelastTime = 0;    //上一次记录RCU文件时间
    private int rcuFileLimitNum = 1;    //RCU文件分割时序号，从1开始
    private boolean isAirplaneDone = false;//是否执行过飞行模式
    private String circleSyncMessage = "";    //循环同步等待,包括外循环同步,并发业务同步
    //测试内容记录
    private String metroRoute = "";       //地铁线路
    private String metroCity = "";       //地铁线路所属的城市
    private String highSpeedRail = "";       //高铁测试线路
    private String mSingleStationName = "";       //测试的单站
    private String tester = "";        //测试人员
    private String testAddress = "";        //测试地址
    private boolean isIndoorTest = false;    //是否室内测试
    private boolean isCQTChecked = false;   //是否勾选CQT测试，不使用isIndoorTest是因为isIndoorTest需要在startDialog选择室内图才为true
    private String testBuilding = "";        //测试的建筑物
    private String testFloor = "";        //测试的楼层
    private String testTagStr = "";        //测试TAG标题
    //    private StringBuffer testPlanBuffer = new StringBuffer();//要写入数据库的测试信息
    //private int 	testTotalCount		= 0;	//当前次数计划统计次数
    //private String 	testResult 	= RESULT_SUCCESS;	//测试结果计入统计次数 1 表示计入统计次数；其它表示不计入统计次数
    private int currentNetType = -1;    //用于存储当前的锁网类型，第一次锁网的时候都执行，后面只有不符合的网络才需要锁网
    /**
     * 通知对象锁,当收到暂停测时，测试任务不再继续，当收到继续测试消息时接着往下走
     */
    private static final int[] lockObj = new int[0];
    /**
     * 室内测试自动打点
     */
    private boolean isCQTAutoMark = false;
    /**
     * 蓝牙定位模块address
     */
    private String mSelectedGlonavinModuleAddress;
//    /**
//     * 保存当前是否DTLog测试状态
//     */
//    private boolean genDtLog = false;
    /**
     * 拨号规则实现类，每次任务开始前重新例化
     */
    private PPPRule pppRule = null;
    /**
     * 当前正执行测试服务Intent列表
     */
    private ConcurrentHashMap<String, TestObjectModel> execServiceList = new ConcurrentHashMap<>();
    /**
     * 当前任务对象完成状态
     */
    private HashMap<String, TestDoneStateModel> currModelDoneState = new HashMap<>();
    private TestObjectModel testObject = null;
    private TestDoneStateModel testDoneState = null;
    /**
     * 是否分步测试，既是在每次测试内循环结束后即等待，等待下次开始测试
     */
    private boolean isStepTest = false;
    //告警提示相关
    private NotificationManager mNotificationManager;//通知管理器

    private TelephonyManager telManager;
    private Context mContext;

    private DatasetManager mDatasetMgr = null;
    private ServerManager sManager = null;
    private EventManager mEventMgr = null;

    /**
     * 表示语音业务
     **/
    private int currentTaskTypeInfo = TaskModel.TYPE_PROPERTY_NOCALL;

    /**
     * 线程池管理
     */
    private ExecutorService mExecutorService = null;

    //构建测试信息对象,开始创建文件时实例化该对象
    private BuildTestRecord mTestRecord = null;
    //当前次测试的唯时间ID,用于多个文件分割时该值唯一分组
    private String task_no = UtilsMethod.sdfhmsss.format(System.currentTimeMillis());

    /**
     * 用于统计CSFB业务返回LTE的时延
     * Key:   RequestTime  RequestNetType   CallType
     * Value: 请求返回时间     请求时的网络类型     0是主叫1是被叫
     */
    private HashMap<String, Long> csfbHM = new HashMap<>();

    private Intent scannerIntent = null;
    private SeeGullService.ISeeGullService iService = null;
    private ServiceConnection mServiceConnection = null;
    /**
     * 是否是WLAN测试
     **/
    boolean isWlanTest = false;
    /**
     * wifi 库工具
     **/
    private WifiPortalManager wifiPortalManager = null;
    /**
     * wifi管理工具
     ***/
    private WifiManager wifiManager = null;
    /**
     * 当前是否在关闭文件中
     */
    private boolean isCloseFileing = false;

    private TaskModel mCurTaskModel;

    /**
     * 绑定了的服务的名字
     **/
    private List<String> isBindServiceName = new LinkedList<>();
    private List<TaskModel> allEnableList;
    /**
     * 是否是并发业务
     */
    private boolean isRabReady = false;

    private void sendIntent() {
        Intent intent = new Intent(WalkMessage.testDataUpdate);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return testServiceBinder;
    }

    private TestServiceBinder testServiceBinder = new TestServiceBinder();

    public class TestServiceBinder extends Binder {
        public TestService getService() {
            return TestService.this;
        }
    }

    public class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName sevice, IBinder iBinder) {
            LogUtil.i("SeeGullService", "--onServiceConnected() called.--");
            iService = (SeeGullService.ISeeGullService) iBinder;
            iService.setMsgHandler(mHandler);
            //修改 true调试回放模式        false 正常模式
            iService.init(false);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            LogUtil.w("SeeGullService", "--onServiceDisconnected--");
            //Log.i(TAG, "onServiceDisconnected() called.");
            iService = null;
        }
    }

    private final BroadcastReceiver testEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(WalkMessage.ACTION_EVENT)) {
                LogUtil.w(TAG, "---testAction:" + intent.getAction());
            }

            if (intent.getAction().equals(ServerMessage.ACTION_FLEET_SYNC_DONE)) {
                MyPhoneState.setSync(mContext, true);
//            } else if (intent.getAction().equals(WalkMessage.ACTION_FLEET_UPLOADGPS_DONE)) {
//				hasUploadGpsDone = true;
            } else if (intent.getAction().equals(WalkMessage.ACTION_UNIT_NORMAL_RECEIVE)) {
                String msg = intent.getExtras().getString(WalkMessage.KEY_UNIT_MSG);

                LogUtil.i(TAG, "receive normal msg:" + msg);

                //2013.1.23和iPhone互拨：
                if (msg != null) {
                    switch (msg) {
                        //主叫使用：被叫发过来的就绪消息
                        case TestTaskService.MSG_MTC_READY:
                            hasReadyFromMTC = true;
                            mEventMgr.addTagEvent(mContext, System.currentTimeMillis(), "MTC Service is Ready");
                            break;
                        //被叫使用：主叫准备就绪
                        case TestTaskService.MSG_MOC_READY:
//                        case TestTaskService.MSG_MOC_GETUUIDFROMMTC:
                            LogUtil.w(TAG, "msg:" + msg + ",hasReadyFromMOC=" + hasReadyFromMOC);
                            if (!hasReadyFromMOC) {
                                hasReadyFromMOC = true;
                                mEventMgr.addTagEvent(mContext, System.currentTimeMillis(), "MOC Service is Ready");
                            }
                            break;
                        //被叫使用：收到主叫挂机消息，马上响应，
                        //被叫进程里也接入此消息,收到后会退出当前被叫进程
                        case TestTaskService.MSG_UNIT_MOC_HANGUP:
                            LogUtil.w(TAG, "msg:" + msg);
                            if (!hasHangupFromMOC) {
                                mEventMgr.addTagEvent(mContext, System.currentTimeMillis(), "MOC Hangup");
                            }
                            hasHangupFromMOC = true;
                            hasReadyFromMOC = false;
                            sendNormalMessage(TestTaskService.MSG_UNIT_MOC_HANGUP_ACK);
                            break;
                        //主叫使用：收到被叫挂机响应
                        case TestTaskService.MSG_UNIT_MOC_HANGUP_ACK:
                            if (!hasHangupAckFromMTC) {
                                mEventMgr.addTagEvent(mContext, System.currentTimeMillis(), "MTC Hangup");
                            }
                            hasHangupAckFromMTC = true;
                            hasReadyFromMTC = false;
                            break;
                        //被叫使用
                        case TestTaskService.MSG_UNIT_NOT_MOC_TASK:
                            hasMOCTaskFinish = true;
                            break;
                        //接收方使用：短信彩信发送开始
                        case TestTaskService.MSG_UNIT_SMS_SEND_START:
                            hasReadyFromSmsSender = true;
                            mEventMgr.addTagEvent(mContext, System.currentTimeMillis(), "SMS Sender is Ready");
                            break;
                        //发送方使用：短信彩信接收方就绪
                        case TestTaskService.MSG_UNIT_SMS_RECV_START:
                            hasReadyFromSmsRecver = true;
                            mEventMgr.addTagEvent(mContext, System.currentTimeMillis(), "SMS Receiver is Ready");
                            break;
                        //接收方使用：收到发送方的结束，马上响应
                        case TestTaskService.MSG_UNIT_SMS_SEND_END:
                            hasStopFromSmsSender = true;
                            sendNormalMessage(TestTaskService.MSG_UNIT_SMS_RECV_EDN);
                            mEventMgr.addTagEvent(mContext, System.currentTimeMillis(), "SMS Sender End");
                            break;
                        //发送方使用：
                        case TestTaskService.MSG_UNIT_SMS_RECV_EDN:
                            hasStopAckFromSmsRcver = true;
                            mEventMgr.addTagEvent(mContext, System.currentTimeMillis(), "SMS Receiver End");
                            break;
                        //接收方使用：彩信发送开始
                        case TestTaskService.MSG_UNIT_MMS_SEND_START:
                            hasReadyFromMmsSender = true;
                            mEventMgr.addTagEvent(mContext, System.currentTimeMillis(), "MMS Sender is Ready");
                            break;
                        //发送方使用：彩信接收方就绪
                        case TestTaskService.MSG_UNIT_MMS_RECV_START:
                            hasReadyFromMmsRecver = true;
                            mEventMgr.addTagEvent(mContext, System.currentTimeMillis(), "MMS Receiver is Ready");
                            break;
                        //接收方使用：收到发送方的结束，马上响应
                        case TestTaskService.MSG_UNIT_MMS_SEND_END:
                            hasStopFromMmsSender = true;
                            sendNormalMessage(TestTaskService.MSG_UNIT_MMS_RECV_EDN);
                            mEventMgr.addTagEvent(mContext, System.currentTimeMillis(), "MMS Sender End");
                            break;
                        //发送方使用：
                        case TestTaskService.MSG_UNIT_MMS_RECV_EDN:
                            hasStopAckFromMmsRcver = true;
                            mEventMgr.addTagEvent(mContext, System.currentTimeMillis(), "MMS Receiver End");
                            break;
                    }
                }
            } else if (intent.getAction().equals(WalkMessage.EncryptFileResult)) {
                //encryptFileResult = intent.getIntExtra("EncryptFileResult", -1);
                LogUtil.w(TAG, "---getencryptFileResult:" + (intent.getIntExtra("EncryptFileResult", -1)));
            } else if (intent.getAction().equals(WalkMessage.testNotifyTestservice)) {
                String msg = intent.getStringExtra("eventValue");
                EventManager.getInstance().addEvent(mContext, msg);
            } else if (intent.getAction().equals(WalkMessage.ACTION_UNIT_SYNC_DONE)) {
                String syncMsg = intent.getExtras().getString(WalkMessage.KEY_UNIT_MSG);
                //iPhone的大循环从第2次开始,标志为2( 1+1 )
                //int circleNum = currentCircle+1;
                //if(syncMsg.equals(TestTaskService.MSG_UNIT_SYNC_Circle+circleNum )){
                //twq20140311,iphone循环同步从第一次开始,此处不再用当前次数+1(注第一次的序号是0),同步比对串由发关同步消息中的变量保存
                if (circleSyncMessage.equals(syncMsg)) {
                    isSyncCircle = true;
                    circleSyncMessage = "";
                }
                //LogUtil.w(tag,"---receive syncMsg:"+syncMsg);
            } else if (intent.getAction().equals(WalkMessage.Action_Walktour_Test_Puase)
                    || intent.getAction().equals(WalkMessage.PuaseTestAndRedoTraceInit)) {
                //收到暂停测试的消息时，中断当前测试
//				AlertManager.getInstance(mContext).speak("Interrupt Test");			//暂停测试
                AlertManager.getInstance(mContext)
                        .addDeviceAlarm(WalkStruct.Alarm.DEVICE_INTERRUPT_TEST, -1);
                appModel.setTestPause(true);

                if (!routineSet.isPuaseKeepTest(mContext)) {
                    puaseOrInterruptTest(true, RcuEventCommand.DROP_USERSTOP);

                    /*根据需要关闭当前RCU文件且添加到数据库中*/
                    if (intent.getBooleanExtra(WalkMessage.PuaseTestAndCloseRcuFile, false)) {
                        needToSplieFile = true;
                        sendCloseRcuFileAddToDB();
                    }
                }
            } else if (intent.getAction().equals(WalkMessage.Action_Walktour_Test_Continue)) {
                //收到继续测试的消息
                new ContinueTest(intent.getBooleanExtra(WalkMessage.ContinueAndCreateRcuFile, false)).start();
            }

            //Push数据或者事件，包括GPS数据
            else if (intent.getAction().equals(WalkMessage.pushData)) {
                LogUtil.d(TAG, "----push Data----start----isCloseFileing:" + isCloseFileing);
                if (!isCloseFileing) {
                    int port = intent.getIntExtra("port", 2);
                    int flag = intent.getIntExtra("flag", 'E');
                    long time = intent.getLongExtra("time", System.currentTimeMillis() * 1000);
                    byte[] buffer = intent.getByteArrayExtra("buffer");
                    int bufferSize = intent.getIntExtra("size", 0);
                    int type = intent.getIntExtra("type", 0);
                    mDatasetMgr.pushData(flag, type, port, time, buffer, bufferSize);
                }
                LogUtil.d(TAG, "----push Data----end----");
                // Push采样点数据
            } else if (intent.getAction().equals(WalkMessage.pushPointData)) {
                LogUtil.d(TAG, "----push Point Data----start----");
                int port = intent.getIntExtra("port", 2);
                long time = intent.getLongExtra("time", System.currentTimeMillis() * 1000);
                byte[] buffer = intent.getByteArrayExtra("buffer");
                int bufferSize = intent.getIntExtra("size", 0);
                mDatasetMgr.pushPointData(port, time, buffer, bufferSize);
                LogUtil.d(TAG, "----push Point Data----end----");
            } else if (intent.getAction().equals(WalkMessage.ACTION_EVENT)) {
                processEvent(intent);
            } else if (intent.getAction().equals(
                    WalkMessage.SERVICE_CHANGE_BY_TRACE)) {
                LogUtil.w(TAG, "--trace is Out of service to interrupt test--");
                // mServiceState = ServiceState.STATE_OUT_OF_SERVICE;
                new WaitSignToInterruptThread().start();
            } else if (intent.getAction().equals(
                    WalkMessage.ACTION_BLUE_CONNECT_STATE_CHANGE)) {
                if (appModel.isUmpcRunning() && umpcTestinfo != null) {
                    umpcTestinfo.reRTUploadStart();
                }
            } else if (intent.getAction().equals(
                    WalkMessage.TELECOM_SETTIMG_NETNOTMATCH)) {
                // 当前为数据业务的时候才启动电信网络监控
                if (!appModel.isTestInterrupt()
                        && currentTaskModel != null
                        && WalkStruct.TaskType.valueOf(
                        currentTaskModel.getTaskType()).isDataTest()) {
                    if (!isTelecomSetRun) {
                        new TelecomSetCheckThread().start();
                    }
                }/*
                //电信巡检现在语音起呼网络限制不在这里做处理
                else if(!appModel.isTestInterrupt()
                        && currentTaskModel != null
                        && WalkStruct.TaskType.valueOf(currentTaskModel.getTaskType()) == WalkStruct.TaskType.InitiativeCall
                        && ConfigRoutine.getInstance().getTelecomVoiceNetSet(mContext) != WalkStruct.TelecomSetting.Normal
                        ){
                    if (!isTelecomVoiceSetRun) {
                        new TelecomVoiceSetCheckThread().start();
                    }
                }*/
            }else if(intent.getAction().equals(
                    WalkMessage.ACTION_VALUE_BUSINESS_DIRECT_TYPE)){
                int directType= intent.getIntExtra("directType", 0);
                ShowInfo.getInstance().setType(directType);
            }
//            else if (intent.getAction().equals(WalkMessage.ACTION_MT_END_EVENT)) {
//                hasHangupFromMOC = true;
//            }
        }
    };

    /**
     * 继续测试时如果当前需要创建RCU文件，发送创建命令过后等待5S
     */
    class ContinueTest extends Thread {
        boolean needCreateFile = false;

        private ContinueTest(boolean needCreate) {
            needCreateFile = needCreate;
        }

        public void run() {
            if (needCreateFile && appModel.isTestPause()) {
                if (!appModel.isRcuFileCreated()) {
                    sendCreateRcuFileCommand();
                    try {
                        Thread.sleep(1000 * 5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            appModel.setTestPause(false);
            notifyThread();
        }
    }

    /**
     * 运行前台服务
     * [一句话功能简述]<BR>
     * [功能详细描述]
     */
    @SuppressWarnings("deprecation")
    private void runForegroundService() {
        Notification.Builder notification = new Notification.Builder(this);
        notification.setTicker(getString(R.string.app_name));
        notification.setSmallIcon(R.drawable.walktour38);
        notification.setWhen(System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, WalkTour.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setContentIntent(pendingIntent);
        notification.setContentTitle(getString(R.string.main_testing));
        notification.setContentText(getString(R.string.main_tostoptest));
        //如果用户权限包含业务测试，则开启前台业务显示图标，否则不显示
        int showForegroundFlag = (appModel.getAppList().contains(WalkStruct.AppType.OperationTest) ? 1 : 0);
        //使用 startForeground ，如果 id 为 0 ，那么 notification 将不会显示
        startForeground(showForegroundFlag, notification.build());
    }

    /**
     * 手机状态监听器
     */
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);
            LogUtil.w(TAG, "--service state current:" + myPhoneState.isServiceAlive() + "--change:" + serviceState.getState()
                    + "--checkOF:" + ConfigRoutine.getInstance().checkOutOfService());
            if (ConfigRoutine.getInstance().checkOutOfService()) {
                myPhoneState.setServiceAlive(serviceState.getState() == ServiceState.STATE_IN_SERVICE);

                if (serviceState.getState() != ServiceState.STATE_IN_SERVICE) {
                    // 更改当前信号状态为off
                    new WaitSignToInterruptThread().start();
                }
            }
        }

        @Override
        public void onDataConnectionStateChanged(int state) {
            super.onDataConnectionStateChanged(state);
            dataState = state;
            LogUtil.w(TAG, "--data state change:" + dataState
                    + "--taskType:" + (currentTaskModel != null ? currentTaskModel.getTaskType() : ""));
            try {
                int networkType = myPhoneState.getNetWorkType(getApplicationContext());
                pioneerInfo.setCurrentNet(WalkStruct.NetworkTypeShow.getNetWorkByNetID(networkType).getNetForIpadTag());

                if (state == TelephonyManager.DATA_DISCONNECTED && currentTaskModel != null
                        && execServiceList.get(currentTaskModel.getTaskName()) != null
                        && execServiceList.get(currentTaskModel.getTaskName()).getmService() != null) {

                    WalkStruct.TaskType t = WalkStruct.TaskType.valueOf(currentTaskModel.getTaskType());
                    if (t.isDataTest() && t != TaskType.MMSSendReceive) {
                        new WaitSignToInterruptThread().start();
                    }
                }
            } catch (Exception e) {
                LogUtil.w(TAG, "", e);
            }
        }
    };

    private boolean waitSignThrRun = false;
    private boolean isTelecomSetRun = false;
    private boolean isTelecomVoiceSetRun = false;
    //private int mServiceState = ServiceState.STATE_IN_SERVICE;
    private int dataState = TelephonyManager.DATA_CONNECTED;
    /**
     * 任务来源
     */
    private int fromType = TaskModel.FROM_TYPE_SELF;
    private int fromScene = SceneType.Manual.getSceneTypeId();

    /**
     * 电信设置检测线程
     * <p>
     * 如果连续五秒钟当前取得的网络类型与设置的不一致,则调用中断当前执行中的任务
     *
     * @author Tangwq
     */
    class TelecomSetCheckThread extends Thread {
        public void run() {
            LogUtil.d(TAG, "------TelecomDataSetCheckThread run...-----");
            if (!isTelecomSetRun) {
                isTelecomSetRun = true;
                int noMatchTimes = 0;
                try {
                    while (!appModel.isTestInterrupt() &&
                            routineSet.getTelecomDataNetSet(mContext).getNetWorkGeneral() !=
                                    NetStateModel.getInstance().getCurrentNetType().getGeneral()) {
                        Thread.sleep(1000);
                        if (noMatchTimes++ >= 5) {
                            puaseOrInterruptTest(false, RcuEventCommand.DROP_NETWORK_NO_MATCH);
                            break;
                        }
                    }
                } catch (Exception e) {
                    LogUtil.w(TAG, "TelecomSetCheckThread", e);
                }
                isTelecomSetRun = false;
            }
        }
    }

    /**
     * 电信设置检测语音发起网络线程
     * <p>
     * 如果连续五秒钟当前取得的网络类型与设置的不一致,则调用中断当前执行中的任务
     *
     * @author Tangwq
     */
    class TelecomVoiceSetCheckThread extends Thread {
        public void run() {
            LogUtil.d(TAG, "------TelecomVoiceSetCheckThread run...-----");
            if (!isTelecomVoiceSetRun) {
                isTelecomVoiceSetRun = true;
                int noMatchTimes = 0;
                try {
                    while (!appModel.isTestInterrupt() &&
                            routineSet.getTelecomVoiceNetSet(mContext).getNetWorkGeneral() !=
                                    NetStateModel.getInstance().getCurrentNetType().getGeneral()) {
                        Thread.sleep(1000);
                        if (noMatchTimes++ >= 5) {
                            puaseOrInterruptTest(false, RcuEventCommand.DROP_NETWORK_NO_MATCH);
                            break;
                        }
                    }
                } catch (Exception e) {
                    LogUtil.w(TAG, "TelecomSetCheckThread", e);
                }
                isTelecomSetRun = false;
            }
        }
    }

    class WaitSignToInterruptThread extends Thread {
        public void run() {

            if (!waitSignThrRun) {
                waitSignThrRun = true;
                boolean needToInterruptTest = true;
                for (int i = 0; i < 5; i++) {
                    LogUtil.w(TAG, "--WaitSignToInterruptThread run--" + i);
                    if (!myPhoneState.isServiceAlive() || dataState == TelephonyManager.DATA_DISCONNECTED) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        needToInterruptTest = false;
                        break;
                    }
                }

                if (needToInterruptTest) {
                    sendDrop();
                }

                waitSignThrRun = false;
            }
            LogUtil.w(TAG, "--WaitSignToInterruptThread exit--");
        }

        private void sendDrop() {
            //2013.10.28 Attach和PDP业务忽略
            if (currentTaskModel != null) {
                WalkStruct.TaskType taskType = WalkStruct.TaskType.valueOf(currentTaskModel.getTaskType());
                if (taskType != TaskType.Attach && taskType != TaskType.PDP) {
                    //twq20130605如果当前不在服务状态，都当成脱离网状态处理，包括仅紧急呼叫，关机等
                    if (!myPhoneState.isServiceAlive() && ConfigRoutine.getInstance().checkOutOfService()) {
                        puaseOrInterruptTest(false, RcuEventCommand.DROP_OUT_OF_SERVICE);
                        //AlertManager.getInstance(mContext).speak("Interrupt Test");
                        AlertManager.getInstance(mContext)
                                .addDeviceAlarm(WalkStruct.Alarm.DEVICE_INTERRUPT_TEST, -1);
                    } else if (dataState == TelephonyManager.DATA_DISCONNECTED) {
                        if (taskType != TaskType.SMSIncept && taskType != TaskType.SMSSend
                                && taskType != TaskType.SMSSendReceive) {
                            if (taskType.isDataTest() && taskType != TaskType.MMSSendReceive) {
                                puaseOrInterruptTest(false, RcuEventCommand.DROP_PPPDROP);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 开始监听，PhoneState实例化后必须运行此方法才能获取手机信号状态变化
     * 监听手机信号的Activity或者Service
     */
    public void listenPhoneState() {
        telManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(mPhoneStateListener,
                PhoneStateListener.LISTEN_SERVICE_STATE
                        | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
    }

    public void onCreate() {
        LogUtil.w(TAG, "onCreate");
        mContext = this;

        wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        isWlanTest = taskd.isWlanTest();
        runForegroundService();
        routineSet = ConfigRoutine.getInstance();

        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(WalktourConst.SYS_SETTING_platform_control, false)) {
            startService(new Intent(mContext, PlatformControlService.class));
        }


        if (appModel.isInnsmapTest()) {
            startService(new Intent(mContext, InnsmapTestService.class));
        }
        //设置当前的运行场景
        appModel.setRuningScene(appModel.getSelectScene());

        if (isWlanTest) {//如果是Wifi测试,启动定时采集wifi数据的服务
            startService(new Intent(mContext, WifiScanService.class));
        }

        if (routineSet.checkOutOfService()) {
            listenPhoneState();
        }

        AlertWakeLock.acquire(mContext);
        taskMethod = new TaskTestObject(getApplicationContext());
        pppRule = new PPPRule(mContext);
//		pcapFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + getApplication().getString(R.string.path_approot) + "/pcap/";
        mDatasetMgr = DatasetManager.getInstance(this);
        sManager = ServerManager.getInstance(this);
        mExecutorService = Executors.newFixedThreadPool(3);

        //初始化锁网执行脚本环境
        initRunScriptEnv();

        // 只有设备类型为1,2,5即 WCDMA，GSM,TDWCDMA的设备才需要启动统计
//        if (deviceInfo.getNettype() == NetType.WCDMA.getNetType()
//                || deviceInfo.getNettype() == NetType.TDSCDMA.getNetType()
//                || deviceInfo.getNettype() == NetType.LTETDD.getNetType()
//                || deviceInfo.getNettype() == NetType.GSM.getNetType()
//                || deviceInfo.getNettype() == NetType.LTE.getNetType()) {
        //开始测试时打开统计明细服务
            /*totalDetailService = new Intent(this,TotalDetailByGSM.class);
            startService(totalDetailService);*/
//        } else if (deviceInfo.getNettype() == NetType.CDMA.getNetType()
//                || deviceInfo.getNettype() == NetType.EVDO.getNetType()) {
        //开始测试时打开统计明细服务
            /*totalDetailService = new Intent(this,TotalDetailByCDMA.class);
            startService(totalDetailService);*/
//        }

        //手机信号监听
        this.myPhoneState = MyPhoneState.getInstance();
        this.myPhone = new MyPhone(mContext);

        //生成通知管理器
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        showInfo = ShowInfo.getInstance();

        //开始测试时设置告警音量
        AlertManager.getInstance(mContext).setVolume();
        AlertManager.getInstance(mContext).clearAlarms(true);
        mEventMgr = EventManager.getInstance();
        mEventMgr.clearEvents();
        appModel.initCurrentTestInfo();

        timer.schedule(alarmTimerTask, 0, 1000);

        registFilter();

        if (appModel.isScannerTest()) {
            bindScannerEnv();
        }
    }


    /**
     * 绑定扫频仪服务
     */
    private void bindScannerEnv() {
        scannerIntent = new Intent(getApplicationContext(), SeeGullService.class);
        mServiceConnection = new MyServiceConnection();
        bindService(scannerIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 注册消息接受
     *
     * @author tangwq
     */
    private void registFilter() {
        IntentFilter filter = new IntentFilter();
        //filter.addAction(WalkMessage.rcuFilePathInfo);
        filter.addAction(WalkMessage.ACTION_EVENT);
        filter.addAction(WalkMessage.EncryptFileResult);
        filter.addAction(WalkMessage.ACTION_FLEET_UPLOADGPS_DONE);
        filter.addAction(WalkMessage.ACTION_UNIT_NORMAL_RECEIVE);
        filter.addAction(WalkMessage.testNotifyTestservice);
        filter.addAction(WalkMessage.ACTION_UNIT_SYNC_DONE);
        filter.addAction(WalkMessage.Action_Walktour_Test_Puase);
        filter.addAction(WalkMessage.Action_Walktour_Test_Continue);
        filter.addAction(WalkMessage.PuaseTestAndRedoTraceInit);
        filter.addAction(WalkMessage.rcuEventSend2Pad);
        filter.addAction(ServerMessage.ACTION_FLEET_SYNC_DONE);
        filter.addAction(WalkMessage.pushData);
        filter.addAction(WalkMessage.pushPointData);
        filter.addAction(WalkMessage.SERVICE_CHANGE_BY_TRACE);
        filter.addAction(WalkMessage.TELECOM_SETTIMG_NETNOTMATCH);
        filter.addAction(WalkMessage.ACTION_BLUE_CONNECT_STATE_CHANGE);
//        filter.addAction(WalkMessage.ACTION_MT_END_EVENT);
        this.registerReceiver(testEventReceiver, filter);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            isBindServiceName.clear();
            if (intent == null) {
                LogUtil.w(TAG, "--TestService onStartCommand Null---");
                Intent killIntent = new Intent(getApplicationContext(), Killer.class);
                startService(killIntent);
                return super.onStartCommand(intent, flags, startId);
            }
            mAppVersionName = UtilsMethod.getCurrentVersionName(WalktourApplication.getAppContext());
            entryptioneKey = new FileReader().getFileText(entryptioneFilePath);
            isAutoTest = intent.getBooleanExtra(WalkMessage.ACTION_ISAUTOTEST_PARA, false);
            testGroupIndex = intent.getIntExtra(WalkMessage.TEST_GROUP_INDEX, 0);
            LogUtil.i(TAG, "--is auto test:" + isAutoTest + "--testGroupIndex:" + testGroupIndex);
            isUmpcTest = intent.getBooleanExtra(WalkMessage.ACTION_ISUMPCTEST_PARA, false);
            isNetsniffer = intent.getBooleanExtra(WalkMessage.ISNETSNIFFER, false);
            if (ApplicationModel.getInstance().isBeiJingTest())
                isNetsniffer = true;
            isCQTAutoMark = intent.getBooleanExtra(WalkMessage.ISCQTAUTOMARK, false);
            mSelectedGlonavinModuleAddress = intent.getStringExtra(WalkMessage.CQT_AUTO_MARK_SELECTED_GLONAVIN_MODULE);
            isDontSaveFile = intent.getBooleanExtra(WalkMessage.KEY_TEST_DONTSAVEDATA, false);
            isDontSwitchAirplane = intent.getBooleanExtra(WalkMessage.KEY_TEST_FLIGHT_MODE, false);
            isBluetoothSync = routineSet.isBluetoothSync(getApplicationContext());
            telecomOrderStr = (intent.getStringExtra(WalkMessage.WORK_ORDER_AH_RCU_FILE_NAME)
                    != null ? intent.getStringExtra(WalkMessage.WORK_ORDER_AH_RCU_FILE_NAME) : "");
            testTagStr = (intent.getStringExtra(WalkMessage.KEY_TEST_TAG) != null
                    ? intent.getStringExtra(WalkMessage.KEY_TEST_TAG) :
                    routineSet.getDeviceTag().equals("") ? "" : routineSet.getDeviceTag() + "-");

            this.fromType = intent.getIntExtra(WalkMessage.KEY_FROM_TYPE, TaskModel.FROM_TYPE_SELF);
            this.fromScene = intent.getIntExtra(WalkMessage.KEY_FROM_SCENE, SceneType.Manual.getSceneTypeId());
            if (isUmpcTest) {
                umpcTestinfo = new UmpcTestInfo(intent.getStringExtra(WalkMessage.ACTION_ISUMPCTEST_INFO));
                //响应UMPC下发下来是否生成抓包文件
                task_no = umpcTestinfo.getTaskno();
                isNetsniffer = umpcTestinfo.isCatchcap();
                appModel.setControllerType(umpcTestinfo.getController());
                outCircleTimes = umpcTestinfo.getRepeats();
                rcuFileSizeLimit = (double) umpcTestinfo.getDatasize();
                rcuFileTimeLimit = umpcTestinfo.getLimitTime();
                isIndoorTest = umpcTestinfo.getTestmode().equals("CQT");
                if (outCircleTimes < 1) {
                    outCircleTimes = 1;
                }
                updateFileUploadTypes(umpcTestinfo.getGenFileTypes());
                isRecordCall = umpcTestinfo.isRecordCall();//是否起呼录音
                LogUtil.w(TAG, "----------isRecordCall:" + isRecordCall + "---------");
//                ConfigRoutine.getInstance().setSMSInfo(mContext, umpcTestinfo.isSendSMS());
                //外循环间隔时长
                outCircleInterval = umpcTestinfo.getRepeatInterval();
                //一次循环不同业务间隔时长
                mDifferentTaskInterval = umpcTestinfo.getTaskInterval();
            } else {
                this.isStepTest = intent.getBooleanExtra(WalkMessage.IS_STEP_TEST, false);
                outCircleTimes = intent.getIntExtra(WalkMessage.Outlooptimes, 1);
                //外循环间隔时长
                outCircleInterval = intent.getIntExtra(WalkMessage.OutloopInterval, 5);
                //一次循环不同业务间隔时长
                mDifferentTaskInterval = intent.getIntExtra(WalkMessage.DIFFERENT_TASK_INTERVAL, 10);
                LogUtil.w(TAG, "---outCircleInterval:" + outCircleInterval + ",differentTaskInterval:" + mDifferentTaskInterval + "----");
                outCircleDisconnetNetwork = intent.getBooleanExtra(WalkMessage.OutloopDisconnetNetwork, false);
                if (intent.getIntExtra(WalkMessage.RcuFileLimitType, 0) == 0) {
                    rcuFileSizeLimit = TextUtils.isEmpty(intent.getStringExtra(WalkMessage.RucFileSizeLimit)) ?
                            0 : Double.valueOf(intent.getStringExtra(WalkMessage.RucFileSizeLimit));
                } else {
                    rcuFileTimeLimit = TextUtils.isEmpty(intent.getStringExtra(WalkMessage.RucFileSizeLimit)) ?
                            0 : Integer.valueOf(intent.getStringExtra(WalkMessage.RucFileSizeLimit));
                }
                tester = intent.getExtras().getString(WalkMessage.KEY_TESTER);
                metroCity = intent.getExtras().getString(WalkMessage.KEY_TEST_CITY);
                metroRoute = intent.getExtras().getString(WalkMessage.KEY_TEST_METRO);
                highSpeedRail = intent.getExtras().getString(WalkMessage.KEY_TEST_HIGHT_SPEED_RAIL);
                mSingleStationName = intent.getExtras().getString(WalkMessage.KEY_TEST_SINGLE_STATION);
                testAddress = intent.getExtras().getString(WalkMessage.KEY_TEST_ADDRESS);
                LogUtil.i(TAG, "---address:" + testAddress);
                isIndoorTest = intent.getExtras().getBoolean(WalkMessage.KEY_TEST_INDOOR, false);
                isCQTChecked = intent.getExtras().getBoolean(WalkMessage.KEY_TEST_CQT_CHECK, false);
                LogUtil.i(TAG, "---isIndoorTest:" + isIndoorTest);
                if (isIndoorTest) {
                    testBuilding = intent.getExtras().getString(WalkMessage.KEY_TEST_BUILDING);
                    testFloor = intent.getExtras().getString(WalkMessage.KEY_TEST_FLOOR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            umpcTestinfo = null;
            outCircleTimes = 1;
            rcuFileSizeLimit = 0.0;
            rcuFileTimeLimit = 0;
        }

        Thread thread = new Thread(new TestThread(lockObj));
        thread.start();
        //如果当前是地铁测试模式则启动地铁测试服务
        if (appModel.getSelectScene() == SceneType.Metro) {
            if (appModel.isGlonavinTest()&&appModel.getGlonavinType()==1){
                Intent glonavin3in1Metro=new Intent(mContext,Glonavin3in1MetroService.class);
                glonavin3in1Metro.putExtra(WalkMessage.CQT_AUTO_MARK_SELECTED_GLONAVIN_MODULE,mSelectedGlonavinModuleAddress);
                startService(glonavin3in1Metro);
            }else {
                startService(new Intent(mContext, MetroTestService.class));
            }
        } else if (appModel.getSelectScene() == SceneType.HighSpeedRail) {
            //启动高铁测试服务
            startService(new Intent(mContext, HighSpeedGpsService.class));
        }
        //启动自动打点服务类
        if (isIndoorTest && isCQTAutoMark && !appModel.isInnsmapTest() && !appModel.isGlonavinTest()) {
            Intent service = new Intent(this, AutoMarkService.class);
            this.startService(service);
        } else if (appModel.getSelectScene()!=SceneType.Metro&&appModel.getSelectScene()!=SceneType.HighSpeedRail&&isCQTAutoMark && appModel.isGlonavinTest()) {
            //蓝牙定位模块自动打点方案服务类
            if (AutoMarkConstant.markScene != MarkScene.COMMON && appModel.getGlonavinType() == 0) {//楼梯，电梯并且是L1，L2的
                Intent glonavinIntent = new Intent(this, GlonavinAutoMarkNotCommonService.class);
                glonavinIntent.putExtra(WalkMessage.CQT_AUTO_MARK_SELECTED_GLONAVIN_MODULE, mSelectedGlonavinModuleAddress);
                startService(glonavinIntent);
            } else if (AutoMarkConstant.markScene == MarkScene.COMMON && appModel.getGlonavinType() == 0) {//普通室内打点,并且是L1，L2的
                Intent glonavinIntent = new Intent(this, GlonavinAutoMarkService.class);
                glonavinIntent.putExtra(WalkMessage.CQT_AUTO_MARK_SELECTED_GLONAVIN_MODULE, mSelectedGlonavinModuleAddress);
                startService(glonavinIntent);
            } else if (AutoMarkConstant.markScene != MarkScene.COMMON && appModel.getGlonavinType() == 1) {
                Intent glonavinIntent = new Intent(this, Glonavin3in1AutoMarkNotCommonService.class);
                glonavinIntent.putExtra(WalkMessage.CQT_AUTO_MARK_SELECTED_GLONAVIN_MODULE, mSelectedGlonavinModuleAddress);
                startService(glonavinIntent);
            } else if (AutoMarkConstant.markScene == MarkScene.COMMON && appModel.getGlonavinType() == 1) {
                Intent glonavinIntent = new Intent(this, Glonavin3in1AutoMarkService.class);
                glonavinIntent.putExtra(WalkMessage.CQT_AUTO_MARK_SELECTED_GLONAVIN_MODULE, mSelectedGlonavinModuleAddress);
                startService(glonavinIntent);
            }

        }

        //地铁获取外置陀螺仪服务
        if (appModel.getSelectScene()==SceneType.Metro||appModel.getSelectScene()==SceneType.HighSpeedRail
                &&ConfigRoutine.getInstance().isHsExternalGPS(this)){
            if (appModel.getGlonavinType()==0){
                Intent gService = new Intent(this, GService.class);
                gService.putExtra(WalkMessage.CQT_AUTO_MARK_SELECTED_GLONAVIN_MODULE, mSelectedGlonavinModuleAddress);
                startService(gService);
            }else if (appModel.getGlonavinType()==1&&appModel.getSelectScene()==SceneType.HighSpeedRail){//高鐵才需要這個GService，地鐵是MetroService
                Intent gService=new Intent(this, Glonavin3in1GService.class);
                gService.putExtra(WalkMessage.CQT_AUTO_MARK_SELECTED_GLONAVIN_MODULE, mSelectedGlonavinModuleAddress);
                startService(gService);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新iPack下发测试任务,上传的日志的格式
     *
     * @param types 类型,每一位代表一个权限
     */
    private void updateFileUploadTypes(char[] types) {

        if (null != types && types.length >= 0) {
            ConfigRoutine rount = ConfigRoutine.getInstance();
            rount.resetFileUploadTypes(this);
            if (types.length >= 1 && types[types.length - 1] == '1') {
                rount.setGenDCF(this, true);
            }
            if (types.length >= 2 && types[types.length - 2] == '1') {
                rount.setGenOrgRcu(this, true);
            }
            if (types.length >= 3 && types[types.length - 3] == '1') {
                rount.setGenDtLog(this, true);
            }
            if (types.length >= 4 && types[types.length - 4] == '1') {
                rount.setGenCU(this, true);
            }
            if (types.length >= 5 && types[types.length - 5] == '1') {
                rount.setGenRCU(this, true);
            }
            if (types.length >= 6 && types[types.length - 6] == '1') {
                rount.setGenECTI(this, true);
            }
        }
    }

    private TimerTask alarmTimerTask = new TimerTask() {
        private int time = 0;
        private final int STORGE_INTERVAL = 30;
        private final int GPS_INTERVAL = 5;            //文档说1秒检测一次，没必要
        /**
         * GPS无效持续时间
         */
        private final int GPS_INVALID_TIME = 30;
        /**
         * 脱网的持续时间
         */
        private final int OUT_OF_SERVICE = 15;

        private int invalidTime = 0;
        private Location lasLocation = null;
        /**
         * 脱网持续时间
         */
        private int outOfService = 0;

        private List<CustomEventParam> paraDefList = CustomEventFactory.getInstance()
                .getCustomEventParamList();

        @Override
        public void run() {
            final boolean isGpsTest = ApplicationModel.getInstance().isGpsTest();
            //是否为地铁扩展蓝牙陀螺仪测试
            final boolean isHighSpeedExpreTest = (ApplicationModel.getInstance().getSelectScene() == SceneType.HighSpeedRail
                    || ApplicationModel.getInstance().getSelectScene() == SceneType.Metro) && ConfigRoutine.getInstance().isHsExternalGPS(mContext);
            sendIntent();
            if (isGpsTest && !isHighSpeedExpreTest) {
                if (time % GPS_INTERVAL == 0) {
                    checkGps();
                }
            }
            if (time % STORGE_INTERVAL == 0) {
                AlertManager.getInstance(mContext).checkStorge();
            }

            checkService();

            checkParamEvent();

            time++;
        }

        private void checkService() {
            if (myPhoneState.isServiceAlive()) {
                outOfService = 0;
            } else {
                outOfService++;
            }

            if (outOfService % OUT_OF_SERVICE == 1) {
                AlarmModel model = new AlarmModel(System.currentTimeMillis(),
                        Alarm.NET_OUT_OF_SERVICE);
                model.setMsgIndex(mDatasetMgr.getCurrentIndex());
                AlertManager.getInstance(mContext).addAlarm(model);
            }
        }

        /**
         * 函数功能：监测自定义参数事件
         */
        private void checkParamEvent() {
//            LogUtil.d(TAG,"----checkParamEvent----start----");
            //读取自定义参数事件的所有ID
            List<Integer> queryList = new ArrayList<>();
            for (CustomEventParam define : paraDefList) {
                Param[] pArray = define.getParams();
                for (Param p : pArray) {
                    if (p != null) {
                        int id = Integer.parseInt(p.id, 16);
                        queryList.add(id);
                    }
                }
            }
            int[] queryArray = new int[queryList.size()];
            for (int i = 0; i < queryList.size(); i++) {
                queryArray[i] = queryList.get(i);
            }

            //查询参数
            mDatasetMgr.queryParamsSync(queryArray);

            //获取参数值
            for (CustomEventParam define : paraDefList) {
                Param[] pArray = define.getParams();
                int paramCount = 0;//参数个数
                int condition = 0;//满足条件的参数个数
                for (Param p : pArray) {
                    if (p != null) {
                        paramCount++;
                        boolean b = queryParam(p, define.getDuration());
                        condition = b ? (condition + 1) : condition;
                    }
                }

                //所有参数都满足条件
                if (paramCount == condition) {

                    createEvent(define);

                    //重置所有参数的持续时间
                    for (Param p : pArray) {
                        if (p != null) {
                            p.duration = 0;
                        }
                    }
                }
            }
//            LogUtil.d(TAG,"----checkParamEvent----end----");
        }

        /**
         *检测GPS
         */
        private synchronized void checkGps() {
            Location location = GpsInfo.getInstance().getLocation();
            if (location == null) {
                invalidTime += GPS_INTERVAL;
            } else if (location.getLongitude() == 0 && location.getLatitude() == 0) {
                invalidTime += GPS_INTERVAL;
            } else {
                if (null != lasLocation) {
                    if (lasLocation.getLatitude() == location.getLatitude() && lasLocation.getLongitude() == location.getLongitude()) {//经纬度没有任何变化时为无效(正常情况下静止不动也会有变化)
                        invalidTime += GPS_INTERVAL;
                    } else {
                        invalidTime = 0;
                        lasLocation = location;
                    }
                } else {
                    invalidTime = 0;
                    lasLocation = location;
                }
            }

            LogUtil.i(TAG, "location=null?" + (location == null)
                    + ",invalidTime:" + invalidTime);

            if (invalidTime >= GPS_INVALID_TIME) {
                AlarmModel model = new AlarmModel(System.currentTimeMillis(),
                        Alarm.DEVICE_GPS_UNAVAILABLE);
                model.setMsgIndex(mDatasetMgr.getCurrentIndex());
                AlertManager.getInstance(mContext).addAlarm(model);
                invalidTime = 0;
            }
        }

        /**
         * 函数功能：生成自定义事件
         * @param custom 自定义事件
         */
        private void createEvent(CustomEventParam custom) {

            StringBuilder paramStr = new StringBuilder();
            for (Param param : custom.getParams()) {
                if (param != null) {
                    String name = ParameterSetting.getInstance().getParamShortName(param.id);
                    if (StringUtil.isNullOrEmpty(name))
                        continue;
                    paramStr.append(String.format(Locale.getDefault(), "%s%s%.2f\n", name,
                            param.getComapreStr(),
                            name.toLowerCase(Locale.getDefault()).endsWith("(k)") ? param.value / 1000 : param.value));
                }
            }
            String eventStr = String.format(Locale.getDefault(), "%s\n%sDuration:%d(s)",
                    custom.getName(), paramStr, custom.getDuration());

            EventModel model = new EventModel(System.currentTimeMillis(), eventStr,
                    EventModel.TYPE_DEFINE);
            model.setCustomEventName(custom.getName());
            model.setPointIndex(mDatasetMgr.getCurrentIndex());
            model.setAlarm(custom.isShowAlarm());
            model.setShowOnChart(custom.isShowChart());
            model.setShowOnMap(custom.isShowMap());
            model.setShowOnTotal(custom.isShowTotal());
            model.setIconDrawablePath(custom.getIconFilePath());

            //生成事件
            mEventMgr.addEvent(mContext, model, true);//回放时需要回溯按时间排序

            //事件转换告警
            AlertManager.getInstance(mContext).addAlarmFromEvent(model);

            //测试时统计自定义事件
            if (model.isShowOnTotal()) {
                GeoPoint geoPoint = GpsInfo.getInstance().getLastGeoPoint();
                mEventMgr.addCutstomEventTotal(mContext, model, geoPoint);
            }
        }

        /**
         * 函数功能：读取一个参数的值
         * @param p 一个参数
         * @param duration 事件的持续时间
         * @return 此参数值比较是否满足持续时间
         */
        private boolean queryParam(Param p, int duration) {
            int id = Integer.parseInt(p.id, 16);
            String value = TraceInfoInterface.getParaValue(id);

            LogUtil.i(TAG, "id:" + id + ",value:" + value);

            if (value.trim().length() > 0) {
                try {
                    float f = Float.parseFloat(value);
                    p.refreshValue(f);

                    //满足持续时间
                    if (p.duration >= duration) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return false;
        }
    };
    private String newRcuFileName;
//	private String createScanRcuFilePath;

    /**
     * 服务结束后，通知线程继续执行
     *
     * @author tangwq
     */
    private void notifyThread() {
        appModel.setTestPause(false);
        synchronized (lockObj) {
            lockObj.notifyAll();
        }
    }

    /**
     * 退出服务清系统资源
     */
    public void onDestroy() {
        LogUtil.w(TAG, "-------onDestroy---------");
        super.onDestroy();
        if (AppVersionControl.getInstance().isTelecomInspection()) {
            startOperateTestFileThread();
        }
        try {
            stopForeground(true);

            if (telManager != null) {
                telManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
            }

            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(WalktourConst.SYS_SETTING_platform_control, false)) {
                stopService(new Intent(mContext, PlatformControlService.class));
            }

            //如果当前是地铁测试模式则启动地铁测试服务
            if (appModel.getRuningScene() == SceneType.Metro) {
                if (appModel.isGlonavinTest()&&appModel.getGlonavinType()==1){
                    Intent glonavinService = new Intent(mContext, Glonavin3in1MetroService.class);
                    new StopGlonavin3in1Jni(glonavinService).start();
                }else {
                    stopService(new Intent(mContext, MetroTestService.class));
                }
            } else if (appModel.getRuningScene() == SceneType.HighSpeedRail) {
                stopService(new Intent(mContext, HighSpeedGpsService.class));
            }
            if (appModel.isInnsmapTest()) {
                stopService(new Intent(mContext, InnsmapTestService.class));
            }
            //设置当前的运行场景
            appModel.setRuningScene(null);

            if (isWlanTest) {//如果是Wifi测试,停止定时采集wifi数据的服务
                stopService(new Intent(mContext, WifiScanService.class));
            }
            appModel.setTestPause(false);
            AlertWakeLock.release();

            //AlertManager.getInstance(mContext).clearAlarms( false );

            LogUtil.e(TAG, "-----testservice onDestroy");
            this.unregisterReceiver(testEventReceiver);
            showInfo.ClearQueueInfo(ShowInfo.defaultChatName);

            if (alarmTimerTask != null) {
                alarmTimerTask.cancel();
                alarmTimerTask = null;
            }
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            csfbHM.clear();
            if (isCQTAutoMark) {
                Intent service = new Intent(this, AutoMarkService.class);
                this.stopService(service);
                if (appModel.isGlonavinTest()) {
                    //蓝牙定位模块自动打点方案服务类
                    if (AutoMarkConstant.markScene != MarkScene.COMMON && appModel.getGlonavinType() == 0) {//楼梯，电梯并且是L1，L2的
                        Intent glonavinService = new Intent(this, GlonavinAutoMarkNotCommonService.class);
                        ApplicationModel.getInstance().setGlonavinTest(false);
                        this.stopService(glonavinService);
                    } else if (AutoMarkConstant.markScene == MarkScene.COMMON && appModel.getGlonavinType() == 0) {//普通室内打点,并且是L1，L2的
                        Intent glonavinService = new Intent(this, GlonavinAutoMarkService.class);
                        ApplicationModel.getInstance().setGlonavinTest(false);
                        this.stopService(glonavinService);
                    } else if (AutoMarkConstant.markScene != MarkScene.COMMON && appModel.getGlonavinType() == 1) {
                        Intent glonavinService = new Intent(this, Glonavin3in1AutoMarkNotCommonService.class);
                        new StopGlonavin3in1Jni(glonavinService).start();
                        ApplicationModel.getInstance().setGlonavinTest(false);
                    } else if (AutoMarkConstant.markScene == MarkScene.COMMON && appModel.getGlonavinType() == 1) {
                        Intent glonavinService = new Intent(this, Glonavin3in1AutoMarkService.class);
                        new StopGlonavin3in1Jni(glonavinService).start();
                        ApplicationModel.getInstance().setGlonavinTest(false);
                    }
                }
            }
            //地铁获取外置陀螺仪服务
            if (appModel.getSelectScene() == SceneType.Metro || appModel.getSelectScene() == SceneType.HighSpeedRail
                    &&ConfigRoutine.getInstance().isHsExternalGPS(this)) {
                if (appModel.getGlonavinType()==0){
                    Intent gService = new Intent(this, GService.class);
                    stopService(gService);
                }else if (appModel.getGlonavinType()==1&&appModel.getSelectScene()==SceneType.HighSpeedRail){
                    Intent gService=new Intent(this, Glonavin3in1GService.class);
                    new StopGlonavin3in1Jni(gService).start();
//                    stopService(gService);
                }
            }

            if (appModel.isScannerTest()) {
                if (iService != null) {
                    iService.releaseSeeGull();
                    iService = null;
                }

                if (mServiceConnection != null) {
                    unbindService(mServiceConnection);
                }
                if (scannerIntent != null) {
                    stopService(scannerIntent);
                }
            }
            if (appModel.isInOutSwitchMode()) {
                appModel.setInOutSwitchMode(false);
            }
        } catch (Exception e) {
            LogUtil.w(TAG, "onDestory", e);
        }
    }
    private class StopGlonavin3in1Jni extends Thread{
        Intent glonavinService;
        public StopGlonavin3in1Jni(Intent glonavinService) {
            this.glonavinService=glonavinService;
        }

        @Override
        public void run() {
            try {
                Intent stopIntent = new Intent(WalkMessage.AUTOMARK_STOP_MARK);
                sendBroadcast(stopIntent);
                Thread.sleep(3000);
                stopService(glonavinService);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            super.run();
        }
    }

    /**
     * 电信巡检版本需要删除和修改某些文件
     */
    private void startOperateTestFileThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "----startOperateTestFileThread run...----");
                if (!TextUtils.isEmpty(createRcuFilePath)) {
                    try {
//                        String prefix = createRcuFilePath.substring(0,createRcuFilePath.lastIndexOf("("));
//                        LogUtil.w(TAG,"prefix:" + prefix);
                        List<String> listFiles = FileUtil.getListFiles(rcuFilePath, "", false);
                        for (String listFile : listFiles) {
                            LogUtil.w(TAG, "listFile:" + listFile);
                            if (/*listFile.startsWith(prefix) &&*/ listFile.endsWith(".ddib")) {
                                LogUtil.w(TAG, "----delete ddib----");
                                FileUtil.deleteFile(listFile);
                            } else if (/*listFile.startsWith(prefix) &&*/ listFile.endsWith(".org.rcu")) {
                                LogUtil.w(TAG, "----rename org.rcu----");
                                File originFile = new File(listFile);
                                File newFile = new File(listFile.replace(".org.rcu", ""));
                                originFile.renameTo(newFile);
                            }
                        }
                    } catch (Exception e) {
                        LogUtil.e(TAG, e.getMessage());
                    }
                }
            }
        }).start();
    }


    /**
     * 业务测试启动线程
     *
     * @author tangwq
     */
    private class TestThread implements Runnable {
        final Object lock;

        private TestThread(Object obj) {
            lock = obj;
        }

        public void run() {

            EventBus.getDefault().post(new AidlEvnet(AidlEvnet.NOTIFY_START_TEST));

            //自动测试默认打开GPS,被禁用时不打开
            if (isAutoTest) {
                LogUtil.i(TAG, "auto test");
                if (!TestPlan.getInstance().isSuspendGps()) {
                    LogUtil.i(TAG, "gps is open");
                    //先强制打开GPS
                    myPhone.openGps();
                    //启动GPS服务
                    GpsInfo.getInstance().openGps(mContext,
                            WalkCommonPara.OPEN_GPS_TYPE_AUTOTEST);
                }
            } else {
                LogUtil.i(TAG, "normal test");
            }

            //等待Trace口初始化成功时间，如果60秒内未成功则不再继续执行，返回并成功初始化串口
            int waitTraceInitTime = 0;
            LogUtil.w(TAG, "--->>appModel.isTraceInitSucc():" + appModel.isTraceInitSucc());
            while (!appModel.isTraceInitSucc() && waitTraceInitTime < 100 && !appModel.isTestInterrupt()) {
                LogUtil.w(TAG, "--waitTraceInitTime:" + waitTraceInitTime + "--cIndex:" + mDatasetMgr.getCurrentIndex());

                if (waitTraceInitTime == 1) {
                    LogUtil.w(TAG, "--test start and trace if faild;start trace monitor--");
                    sendBroadcast(new Intent(WalkMessage.NOTIFY_TESTTING_WAITTRACEINITSUCC));
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                waitTraceInitTime++;
                if (waitTraceInitTime % 25 == 0) {    //20S重新初始化一次Trace 口
                    Intent reStartTraceInit = new Intent(WalkMessage.redoTraceInit);
                    sendBroadcast(reStartTraceInit);
                }
            }

            //业务开始测试之前清空上次数据分布结果
            TraceInfoInterface.traceData.cleanDistributionData();

            if (!appModel.isTraceInitSucc()) {//如果此处未初始化成功，发送重新初始化Trace消息，并停业当前测试
                Intent reStartTraceInit = new Intent(WalkMessage.redoTraceInit);
                sendBroadcast(reStartTraceInit);

                Message msg = mHandler.obtainMessage(testStopredoTraceInit);
                mHandler.sendMessage(msg);
            } else {
                sendStartCommand();
                //测试
                callAllTest();

                sendStopCommand();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //测试正常/中断停止后写入测试停止事件
            EventBytes.Builder(mContext, RcuEventCommand.Stop_Test).writeToRcu(System.currentTimeMillis() * 1000);

            //显示测试停止事件
            if (appModel.isTestInterrupt()) {
                showEvent(WalkStruct.DataTaskEvent.Task_Stop.toString());
                //被手工停止时切换回数据业务的APN
                switchToDataApn();
            } else {
                showEvent(WalkStruct.DataTaskEvent.Test_Finished.toString());
            }

            //如果任务列表不为空，最后一个任务测试完成后固定暂停10秒
            if (!taskListEmpty && appModel.isTraceInitSucc()) {
                LogUtil.w(TAG, "---wait to finish Job----");
                try {
                    Thread.sleep(1000 * 4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //停止写入RCU信令时插入停止写入事件
            EventBytes.Builder(mContext, RcuEventCommand.Stop_Logging).writeToRcu(System.currentTimeMillis() * 1000);

            //显示停止写入事件
            showEvent(WalkStruct.DataTaskEvent.Stop_Logging.toString());
            //显示间隔
            sendSplitLine();

            if (appModel.isScannerTest() && iService != null) {
                iService.stopScan(-1);
            }

            //测试结束时获得下测试时，用于更新测试时长记录
            TraceInfoInterface.traceData.getTestTimeLength();

            if (isAutoTest) {
                //关闭GPS
                GpsInfo.getInstance().releaseGps(mContext,
                        WalkCommonPara.OPEN_GPS_TYPE_AUTOTEST);
            }


            //2014.1.15  完全测试完毕(结束后还要转换文件等动作)再修改 测试状态标志
            //appModel.setTestJobIsRun(false);
            appModel.setTestStoping(true);
            LogUtil.w(TAG, "--test finish file created:" + appModel.isRcuFileCreated());
            if (!taskListEmpty && appModel.isTraceInitSucc() && appModel.isRcuFileCreated()) {
                //2014.1.15 下面这个函数是阻塞的
                //测试任务列表不为空的时候才需要关闭写RCU文件及将文件信息插入到数据库中
                sendCloseRcuFileAddToDB();
            }
            appModel.setTestStoping(false);
            appModel.setWoneTest(false);                        //设置回wone测试状态
            //测试完成后上传（根据设置--自动上传）
            boolean upload = sManager.isAutoUpload();
            LogUtil.i(TAG, "-----Is Auto Upload:" + upload + "------");
            //yi.lin 2018/10/10 之前勾选自动上传后测试结束后自动上传提示登录失败，是因为做完业务后移动数据开关被关闭，
            //因此自动上传失败，此处加上打开数据网络操作。
            if (upload) {
                boolean networkEnable = MyPhoneState.getInstance().isNetworkAvirable(WalktourApplication.getAppContext());
                if (!networkEnable) {
                    boolean isMobileDataEnabled = APNOperate.getInstance(WalktourApplication.getAppContext()).setMobileDataEnabled(true, "", true, 5 * 1000);
                    if (isMobileDataEnabled) {
                        uploadFile();
                    }
                } else {
                    uploadFile();
                }
            }

            if (!appModel.isTestInterrupt()) {
                Intent doneIntent = new Intent(WalkMessage.NOTIFY_GROUP_TESTJOBDONE);
                doneIntent.putExtra(WalkMessage.NOTIFY_TESTJOBDONE_PARANAME,
                        createRcuFilePath);
                doneIntent.putExtra(WalkMessage.NOTIFY_TESTJOBDONE_RECORDID, mTestRecord == null ? "" : mTestRecord.getTestRecord().record_id);
                sendBroadcast(doneIntent);
            } else {
                Intent doneIntent = new Intent(WalkMessage.NOTIFY_GROUP_INTERRUPTJOBDONE);
                doneIntent.putExtra(WalkMessage.NOTIFY_TESTJOBDONE_PARANAME,
                        createRcuFilePath);
                doneIntent.putExtra(WalkMessage.NOTIFY_TESTJOBDONE_RECORDID, mTestRecord == null ? "" : mTestRecord.getTestRecord().record_id);
                sendBroadcast(doneIntent);
            }

            EventBus.getDefault().post(new AidlEvnet(AidlEvnet.NOTIFY_STOP_TEST));

            stopSelf();
            //添加停止测试声音告警
            AlertManager.getInstance(mContext)
                    .addDeviceAlarm(WalkStruct.Alarm.DEVICE_STOP_TEST, -1);
            if (!ApplicationModel.getInstance().isGeneralMode())
                TotalInterface.getInstance(getApplicationContext()).setAutoStatistic(true);
            LogUtil.d(TAG, "----TestService stop sucess-----");
        }

        /**
         * 上传文件
         */
        private void uploadFile() {
            LogUtil.d(TAG, "-------uploadFile-----------");
            sManager.sendEvent(mContext);
            if (mTestRecord != null) {
                //设置里面上传文件类型集合
                Set<Entry<String, Boolean>> uploadFileTypeSet = ServerManager.getInstance(WalktourApplication.getAppContext()).getUploadFileTypes(mContext).entrySet();
                //提取设置里面勾选的上传文件类型集合
                List<String> needUploadTypes = new ArrayList<>();
                for (Entry<String, Boolean> entry : uploadFileTypeSet) {
                    if (entry.getValue()) {
                        needUploadTypes.add(entry.getKey());
                    }
                }
                //从生成的文件类型中取出需要上传的文件类型添加到上传列表中
                TestRecord record = mTestRecord.getTestRecord();
                UploadFileModel file = new UploadFileModel(record.record_id, record.test_type);
                file.setName(record.file_name);
                Set<FileType> realUploadFileTypes = new HashSet<>();//存储需要上传的文件类型集合
                for (int i = 0; i < record.getRecordDetails().size(); i++) {
                    RecordDetail detail = record.getRecordDetails().get(i);
                    FileType tempFileType = FileType.getFileType(detail.file_type);
                    if (needUploadTypes.contains(tempFileType.getFileTypeName())) {
                        file.setParentPath(detail.file_path);
                        realUploadFileTypes.add(tempFileType);
                    }
                }
                LogUtil.d(TAG, "realUploadFileTypes size:" + realUploadFileTypes.size());
                //如果不为空，开始上传
                if (!realUploadFileTypes.isEmpty()) {
                    file.setFileTypes(realUploadFileTypes);
                    List<UploadFileModel> uploadFiles = new ArrayList<>();
                    uploadFiles.add(file);
                    sManager.uploadFile(mContext,
                            appModel.isIndoorTest() ? ServerOperateType.uploadIndoorFile : ServerOperateType.uploadTestFile,
                            uploadFiles);
                }

            }
        }

        /**
         * 等待大循环同步完成
         */
        private void waitForSyncDone(String circleMsg) {
            //发送MTC消息给主被叫控制服务端(UMPC/iPad)
            isSyncCircle = false;
            circleSyncMessage = circleMsg;
            Intent intent = new Intent(WalkMessage.ACTION_UNIT_SYNC_START);
            intent.putExtra(WalkMessage.KEY_UNIT_SYNCMODEL,
                    (umpcTestinfo != null ? umpcTestinfo.getSyncModel() : 1));
            intent.putExtra(WalkMessage.KEY_UNIT_MSG, circleSyncMessage);

            int t = 0;
            do {
                //如果1分钟未收到同步成功重发同步消息
                if (t % 30 == 0) {
                    LogUtil.i(TAG, "--send " + WalkMessage.ACTION_UNIT_SYNC_START + ":" + circleSyncMessage);
                    sendBroadcast(intent);
                }
                LogUtil.i(TAG, "---wait for synchronize circle");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                t++;
            } while (t < 60 * 30 && !isSyncCircle && !appModel.isTestInterrupt());
            //如果30分钟仍未外循环同步成功继续
        }

        /**
         * 执行测试任务第一次时，需要将测试任务的相关测试信息写入RCU文件中
         *
         * @param model 业务对象
         * @author tangwq
         */
        private void writeTestPlanToRcu(TaskModel model) {
            String testPlan = TaskTestObject.getTestPlanInfo(model);
//            testPlanBuffer.append(testPlan).append("\r\n");
            LogUtil.w(TAG, "--testPlan:" + testPlan);
            //当写入RCU文件的测试内容不为空时发送命令将内容写入文件中
            if (!testPlan.equals("")) {
                EventBytes.Builder(getApplicationContext())
                        .addCharArray(testPlan)
                        .writeToRcu(WalkCommonPara.MsgDataFlag_Z);

				/*Intent intent = new Intent( WalkMessage.rcuFileSelfDefineString );
                intent.putExtra(WalkMessage.rcuFileSelfDefineString_flag, 'Z');
				intent.putExtra(WalkMessage.rcuFileSelfDefineString_string, testPlan);
				sendBroadcast( intent );*/
            }
        }

        /**
         * 线程等待指定时间，支持实时中断
         */
        private void threadWaitCanInterrupt(int seccond, String tips) {
            try {

                for (int i = 0; i < Math.abs(seccond) && !appModel.isTestInterrupt() && !currentMultiRabTimeOut; i++) {
                    LogUtil.w(TAG, "--wait " + tips + ":" + seccond + "--by:" + i);
                    Thread.sleep(1000);
                    /*if(i % 10 == 0){	//防止休眠
                        AlertWakeLock.acquire( mContext );
					}*/
                }

                if (tips.equals("idle")) {
                    EventBytes.Builder(TestService.this, RcuEventCommand.Idle_Test_End)
                            .writeToRcu(System.currentTimeMillis() * 1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        /**
         * 执行单个任务模型
         * 如果当前类型是并发任务，获得并发任务子列表，调用任务循环执行线程处理相应的任务动作
         *
         * @return 控制<任务次数>的j值，这里的<任务次数>是指调用远程测试服务的次数
         * @author tangwq
         */
        private int execAnTaskModel(TaskModel model, int j, int pdpDelay, String pioneerStr) {

            LogUtil.w(TAG, "--do an job and control J:" + j + ",taskName=" + model.getTaskName() + ",pioneerStr=" + pioneerStr);

            if (isWlanTest) {
                model.setDatadevice(deviceInfo.getWifiDevice());
            } else {
                model.setDatadevice(deviceInfo.getPppName());
            }
            mCurTaskModel = model;
            Intent startTest = taskMethod.getTestTaskIntent(model, j, pdpDelay);
            startTest.putExtra(WalkCommonPara.testUmpcTest, isUmpcTest);
            startTest.putExtra(WalkMessage.TestInfoForPioneer, pioneerStr);
            startTest.putExtra(WalkMessage.TESTFILENAME, newRcuFileName.substring(0, newRcuFileName.lastIndexOf(".")));
            startTest.putExtra(WalkMessage.IS_RECORD_CALL, isRecordCall);
            startTest.putExtra(BluetoothMOSService.EXTRA_KEY_SERVER_TIME_OFFSET, appModel.getServerTimeOffset());
            startTest.putExtra(MOCTest.EXTRA_HAS_POLQA_TYPE, ApplicationModel.getInstance().hasPolqaType());
            startTest.putExtra(WalkMessage.ACTION_SYNC_NET_TYPE, umpcTestinfo == null ? "NONE" : umpcTestinfo.getDxSyncNetType());
            if (!isUmpcTest) {
                if (BluetoothMOSFactory.get().getCurrMOCDevice() != null) {
                    startTest.putExtra(BluetoothMOSService.EXTRA_KEY_BLUETOOTH_MOS, BluetoothMOSFactory.get().getCurrMOCDevice());
                }
                if (BluetoothMOSFactory.get().getCurrMTCDevice() != null) {
                    startTest.putExtra(BluetoothMTCService.EXTRA_KEY_BLUETOOTH_MTC, BluetoothMOSFactory.get().getCurrMTCDevice());
                }
                startTest.setExtrasClassLoader(BluetoothMOSDevice.class.getClassLoader());
            }
            TestDoneStateModel doneState = (currModelDoneState.get(model.getTaskName())
                    == null ? new TestDoneStateModel(1) : currModelDoneState.get(model.getTaskName()));
            doneState.setDoneStateNum(1);

            currModelDoneState.put(model.getTaskName(), doneState);

            testObject = new TestObjectModel(startTest);
            testObject.setServiceConnection(getServiceConnection(model.getTaskName()));
            try {
                testObject.setTaskType(WalkStruct.TaskType.valueOf(model.getTaskType()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            execServiceList.put(model.getTaskName(), testObject);

            //启动服务
            ComponentName cN = startService(startTest);
            boolean isBind = bindService(startTest, testObject.getServiceConnection(), Context.BIND_AUTO_CREATE);
            LogUtil.w(TAG, "--do an job to return:" + j + "--start :" + (cN == null) + "--bind:" + isBind);

            if (cN == null || !isBind) {
                interruptTestService(false, model.getTaskName(), RcuEventCommand.DROP_NORMAL);
                execServiceList.remove(model.getTaskName());
            } else {
                LogUtil.w(TAG, "--do an job to monitor service binder--");
                isBindServiceName.add(model.getTaskName());
                mExecutorService.execute(new MonitorServiceBinder(model.getTaskName()));
            }

            return startTest.getIntExtra(TaskTestObject.ControlCircleJ, j) - (isBind ? 0 : 1);
        }

        /**
         * 执行包含URL列表的Http登陆或HTTP刷新
         *
         * @param model    测试模板
         * @param j        当前第几次测试
         * @param pdpDelay pdp拨号的时延
         * @return 控制<任务次数>的j值，这里的<任务次数>是指调用远程测试服务的次数
         */
        private int execUrlListModel(TaskModel model, int j, int pdpDelay, boolean isMultiDerive, String testStartInfo) {
            TaskHttpPageModel httpModel = (TaskHttpPageModel) model;
            List<UrlModel> urlList = httpModel.getUrlModelList();
            int t = j;
            if (urlList != null) {
                boolean doAgain;
                for (int i = 0; i < urlList.size() && !appModel.isTestInterrupt()
                        && !currentMultiRabTimeOut && !currentHttpTimeOut; i++) {
                    httpModel.setUrl(urlList.get(i).getName());

                    //2013.12.20做完一个url,必须等待适当时间，否则执行execAnTaskModel后会出现绑定业务失败(之前的业务进程还在)
                    if (i != 0) {
                        try {
                            Thread.sleep(3 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (appModel.getNetList().contains(WalkStruct.ShowInfoType.TelecomSwitch)
                            && !NetWorkAlive.checkTelecomSet(mContext, mEventMgr)) {
                        //如果当前为电信设置开关打开状态,后面的方法检查当前网络是否匹配,如果返回false的话,表示不匹配返回,直接break退出
                        break;
                    }

                    t = execAnTaskModel(model, j, pdpDelay, testStartInfo);

                    //如果当前任务没有完成,且不是中断状态,则在此处循环休眠等待,
                    //任务完成是在具体测试任务回调中产生最后一个数据包表示任务完成
                    while (currModelDoneState.get(model.getTaskName()).getDoneStateNum() > 0
                            && !appModel.isTestInterrupt()) {
                        try {
                            Thread.sleep(900);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //如果当前url失败重新做
                    doAgain = needDoAgain(model.getTaskName());
                    if (doAgain) {
                        i--;
                    }

                    //分隔空行
                    if ((!isMultiDerive)) {
                        if (i != urlList.size() - 1 || doAgain) {//最后一个url不显示空行
                            sendSplitLine();
                        }
                    }
                }
                currentHttpDone = true;

            }

            return t;
        }

        /**
         * 监控测试服务是否绑定成功,
         * 如果超过最大时间未绑定成功,结束该次绑定测试
         *
         * @author Tangwq
         */
        class MonitorServiceBinder implements Runnable {
            final int BINDER_TIMEOUT = 60000;//绑定服务超时时间
            String monitorTask = "";    //绑定服务名称

            private MonitorServiceBinder(String taskName) {
                monitorTask = taskName;
            }

            @Override
            public void run() {
                LogUtil.w(TAG, "--MonitorServiceBinder " + monitorTask + " run--");
                int binderTimes = 0; // 绑定计时
                try {
                    UtilsMethod.ThreadSleep(10);
                    while (execServiceList.get(monitorTask) != null
                            && !execServiceList.get(monitorTask).isServiceHasBind()
                            && binderTimes < BINDER_TIMEOUT) {
                        binderTimes += 100;
                        UtilsMethod.ThreadSleep(100);

                        LogUtil.w(TAG, "--MonitorServiceBinder " + monitorTask + ":" + binderTimes + "," + monitorTask + " isServiceHasBind=" + execServiceList.get(monitorTask).isServiceHasBind());
                    }

                    while (binderTimes < BINDER_TIMEOUT
                            && execServiceList.get(monitorTask) != null
                            && execServiceList.get(monitorTask).getmService() != null
                            && !execServiceList.get(monitorTask).getmService().getRunState()
                            ) {
                        binderTimes += 100;
                        UtilsMethod.ThreadSleep(100);

                        LogUtil.w(TAG, "--MonitorServiceStart " + monitorTask + ":" + binderTimes);
                    }

                    //如果上面监控时间内绑定服务值未成功,此处中断该处测试
                    if (execServiceList.get(monitorTask) != null &&
                            (!execServiceList.get(monitorTask).isServiceHasBind()
                                    || execServiceList.get(monitorTask).getmService() == null
                                    || !execServiceList.get(monitorTask).getmService().getRunState())) {
                        LogUtil.w(TAG, "--MonitorServiceBinder Is TimeOue To Interrupt Servie:" + monitorTask);

                        EventManager.getInstance().addTagEvent(getApplicationContext(), System.currentTimeMillis(), monitorTask + " Service binder failure.");

                        interruptTestService(true, monitorTask, RcuEventCommand.DROP_NORMAL);
                        UtilsMethod.killProcessByPname(execServiceList.get(monitorTask).getTestIntent().getComponent().getClassName(), false);
                        execServiceList.remove(monitorTask);
                    } else {
                        LogUtil.w(TAG, "--MonitorServiceBinder Success Break--" + monitorTask);
                    }
                } catch (Exception e) {
                    LogUtil.w(TAG, "MonitorServiceBinder", e);
                }
            }
        }

        boolean currentMultiRabDone = false;
        boolean currentMultiRabTimeOut = false;
        boolean currentHttpDone = false;
        boolean currentHttpTimeOut = false;
        //http业务需作总超时控制时，PPP拨号使用时间
        int httpPPPUsTimeOut = 0;

        /**
         * 执行并发任务
         *
         * @param model 业务
         * @author tangwq
         */
        private void execMultiRabModel(TaskModel model, int j) {
            TaskRabModel rabModel = (TaskRabModel) model;

            //启动并发timeout监控线程
            currentMultiRabDone = false;
            currentMultiRabTimeOut = false;
            rabEventReady = false;

            if (rabModel.getSingleParallelTimeout() > 0) {
                new MultiRabTimeOutInterrup(rabModel.getSingleParallelTimeout()).start();
            }

            List<TaskModel> multiTasks = rabModel.getTaskModel();
            currModelDoneState.put(rabModel.getTaskName(), new TestDoneStateModel(multiTasks.size()));

            //twq20150929并发业务执行流程按新流程执行
            //1.执行并发业务的拨号断开测试,暂不处理HTTP的拨号起时情况,后续需要再加
            doPPPRuleByModel(model, TaskType.MultiRAB, j, -9999);
            //2.根据设定的延时时间启动相应的并发业务
            if (rabModel.getParallelServiceTestConfig().getRabStartMode() == ParallelServiceTestConfig.RAB_STATE_MODEL_EVENT_STATE) {
                /*
                 * 如果当前为指定业务指定事件并发
                 * (1)启动指定事件监控线程,传入监控事件ID
                 * (2)执行指定业务
                 * (3)等待指定事件为真,执行除指定业务外的其它所有业务,按指定的延时时延启动
                 */
                //启动指定事件监控线程
                new MultiRabEventMonitor(rabModel.getParallelServiceTestConfig().getStartState(), System.currentTimeMillis()).start();
                //启动被引用业务
                for (int i = 0; i < multiTasks.size(); i++) {
                    if (multiTasks.get(i).getTaskType().equals(rabModel.getParallelServiceTestConfig().getReferenceService())) {
                        new TaskModelCircleDoThread(multiTasks.get(i), true, rabModel.getTaskName()).start();
                        break;
                    }
                }
                //等待指定事件就绪
                while (!rabEventReady) {
                    threadWaitCanInterrupt(1, "rabEventReady");
                }
                //按业务中配置的时间启动非指定业务
                for (int i = 0; i < multiTasks.size(); i++) {
                    if (!multiTasks.get(i).getTaskType().equals(rabModel.getParallelServiceTestConfig().getReferenceService())) {
                        //相对业务事件后的业务启动为直接开始,不做时延
                        int delay = 0; //getDelayByType(rabModel.getRabStartMode(),multiTasks.get(i).getRabDelayTimes(rabModel.getRabStartMode()));
                        new MultiRabJobCallByDelay(rabModel.getTaskName(), multiTasks.get(i), delay).start();
                        try {
                            Thread.sleep(3000);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            } else {
                /*
                 * 如果当前为非指定业务启动
                 * 1.根据当前指定类型,无,按绝对时间,按相对时间,把具体业务中指定的延时转换为相对延时启动时间
                 * 2.启动并发业务
                 */
                for (int i = 0; i < multiTasks.size(); i++) {
                    int delay = getDelayByType(rabModel.getParallelServiceTestConfig().getRabStartMode(), multiTasks.get(i).getRabDelayTimes(rabModel.getParallelServiceTestConfig().getRabStartMode()));
                    new MultiRabJobCallByDelay(rabModel.getTaskName(), multiTasks.get(i), delay).start();
                    try {
                        Thread.sleep(3000);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        /**
         * 根据传入的并发业务时间启动模式,将子业务中的时间转换成相对延时启动时间
         *
         * @param startModel 启动模式
         * @param time       业务时间
         * @return 相对延时启动时间
         */
        private int getDelayByType(int startModel, int time) {
            if (startModel == ParallelServiceTestConfig.RAB_STATE_MODEL_NORMAL) {
                //无时直接返回0
                return 0;
            } else if (startModel == ParallelServiceTestConfig.RAB_STATE_MODEL_ABSOLUTELY_TIME) {
                //绝对时间时要把指定时间与当前时间进行比较,转换成相对时间,如果指定时间小于当前时间,返回0立即执行

                return time - UtilsMethod.convertHHmmToSecond(UtilsMethod.sdfhms.format(System.currentTimeMillis()));
            } else {
                //其它情况为相对时间,直接用指定值即可
                return time;
            }
        }

        /**
         * 监控指定的事件ID是否在当前时间点之后存在于事件列表之中
         *
         * @author Tangwq
         */
        class MultiRabEventMonitor extends Thread {
            private int eventId;
            private long startEventTime;

            private MultiRabEventMonitor(int eventId, long startTime) {
                this.eventId = eventId;
                this.startEventTime = startTime;
            }

            public void run() {
                try {
                    LogUtil.w(TAG, "--MultiRabEventMonitor start--");
                    while (!EventManager.getInstance().EventContainsRCUID(eventId, startEventTime)) {
                        sleep(500);
                    }
                    LogUtil.w(TAG, "--MultiRabEventMonitor Success--");
                    rabEventReady = true;
                } catch (Exception e) {
                    LogUtil.w(TAG, "", e);
                }
            }
        }

        /**
         * 并发业务中每个子业务单独调
         *
         * @author Tangwq
         */
        class MultiRabJobCallByDelay extends Thread {
            int jobDelay = 0;
            String rabName = "";
            TaskModel rabModel = null;

            /**
             * @param jobName 并发业务名称
             * @param model   当前要执行的并发子业务
             * @param delay   当前并发子业务延时多久启动
             */
            private MultiRabJobCallByDelay(String jobName, TaskModel model, int delay) {
                this.jobDelay = delay;
                this.rabName = jobName;
                this.rabModel = model;
            }

            public void run() {
                LogUtil.w(TAG, "--MultiRabJobCallByDelay " + rabName + "--subName:" + rabModel.getTaskName() + "--delay:" + jobDelay);
                //如果延时时间大于0,按指定时间启动
                if (jobDelay > 0) {
                    threadWaitCanInterrupt(jobDelay, "callRabDelay");
                }

                LogUtil.w(TAG, "--MultiRabJobCallByDelay " + rabName + "--subName:" + rabModel.getTaskName() + "-- run-");
                new TaskModelCircleDoThread(rabModel, true, rabName).start();
            }
        }

        /**
         * 监控并发任务监控流程
         */
        class MultiRabTimeOutInterrup extends Thread {
            int timeOut = 0;

            private MultiRabTimeOutInterrup(int timeout) {
                this.timeOut = timeout;
            }

            public void run() {
                if (timeOut > 0) {
                    for (int i = 0; i < timeOut && !currentMultiRabDone; i++) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        LogUtil.w(TAG, "--rabtimeout:" + timeOut + "-to:" + i);
                    }

                    LogUtil.w(TAG, "--rabtimeout result:" + currentMultiRabDone);
                    //执行到此处说明是超时退出，需要执行中断当前任务动作
                    if (!currentMultiRabDone) {
                        currentMultiRabTimeOut = true;
                        puaseOrInterruptTest(true, RcuEventCommand.DROP_TIMEOUT);
                    }
                }
            }
        }

        /**
         * 监控HTTP任务超时流程
         */
        class HttpTimeOutInterrup extends Thread {
            TaskHttpPageModel httpModel = null;
            int timeOut = 0;

            private HttpTimeOutInterrup(TaskHttpPageModel model) {
                httpModel = model;
                this.timeOut = model.getParallelTimeout();
            }

            public void run() {
                if (timeOut > 0) {
                    for (int i = httpPPPUsTimeOut; i < timeOut && (!currentMultiRabDone || !currentHttpDone); i++) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        LogUtil.w(TAG, "--httptimeout:" + timeOut + "-to:" + i);
                    }

                    LogUtil.w(TAG, "--httpimeout result:" + currentHttpDone);
                    //执行到此处说明是超时退出，需要执行中断当前任务动作
                    if (!currentHttpDone && !currentMultiRabTimeOut) {
                        currentHttpTimeOut = true;
                        puaseOrInterruptTest(true, RcuEventCommand.DROP_USERSTOP, TaskType.valueOf(httpModel.getTaskType()));
                    }
                }
            }
        }

        /**
         * [设置当前业务为第一个数据业务状态及名称]
         * [当前第一个数据业务状态为假，且当前业务为数据业务时
         * 将当前第一个数据业务状态置为直且获得第一个数据业务名称]
         *
         * @param model 测试业务
         */
        private void setFirstDataInfo(TaskModel model) {
            if (!isFirstDataJobByMulti
                    && (model.getTypeProperty() == WalkCommonPara.TypeProperty_Net
                    || model.getTypeProperty() == WalkCommonPara.TypeProperty_Wap
                    || model.getTypeProperty() == WalkCommonPara.TypeProperty_Wlan)
                    && !model.getTaskType().equals(WalkStruct.TaskType.MultiRAB.name())) {
                isFirstDataJobByMulti = true;
                theFirstDataNameByMulti = model.getTaskName();
                LogUtil.w(TAG, "---setFirstDataInfo--" + theFirstDataNameByMulti + "--" + isFirstDataJobByMulti
                        + "--" + model.getTaskType());
            }
        }

        /**
         * 当前测试第几个线程
         */
        private int testThreadNum = 0;
        /**
         * twq20120619是否第一个数据业务
         */
        boolean isFirstDataJob = true;

        /**
         * 当前任务循环控制执行线程
         *
         * @author tangwq
         */
        class TaskModelCircleDoThread extends Thread {
            TaskModel model = null;            //当前可执行任务名称
            boolean isMultiDerive = false;    //是否并发派生任务执行线程，false表示主线程启动
            String multiName = "";            //任务名称
            private int threadNum = 0;
            private WalkStruct.TaskType taskType;
            private int pdpDelay = 1000;

            final int TASK_EXCUTE = 0;    //继续执行任务
            final int TASK_CONTINUE = 1;    //跳到下一次
            final int TASK_BREAK = 2;    //直接跳到下一个任务,

            /**
             * 拨号结果，如果拨号成功，任务结果时的结果才写PPP HANGUP
             */
            int pppResult = 0;

            private TaskModelCircleDoThread(TaskModel taskModel, boolean isMultiDerive, String multiName) {
                this.model = taskModel;
                this.isMultiDerive = isMultiDerive;
                this.multiName = multiName;
                threadNum = testThreadNum;
                testThreadNum++;
                taskType = WalkStruct.TaskType.valueOf(model.getTaskType());
            }
            //2013.1.24 lqh增加iPhone互拨打时把主被叫同步流程放到TestService里控制

            /**
             * 主被叫联合测试的头部对齐
             *
             * @return 是否成功同步
             */
            private boolean unitTestSyncHead(int j) {

                long startTime = System.currentTimeMillis();

                boolean hasReady = false;
                LogUtil.w(TAG, "unitTestSyncHead=" + j + ",taskType=" + taskType);

                //主叫等待被叫准备好(被叫启动Service后)
                if (taskType == TaskType.InitiativeCall) {
                    //hasReadyFromMTC = false;

                    startTime = 0;
                    LogUtil.w(TAG, "-----Waiting for MTC Service-----start-----hasReadyFromMTC=" + hasReadyFromMTC);
                    while (!hasReadyFromMTC && !appModel.isTestInterrupt()) {
                        if (startTime / 100 % 300 == 0) {
                            mEventMgr.addTagEvent(mContext, System.currentTimeMillis(), "Waiting for MTC Service");
                            sendNormalMessage(TestTaskService.MSG_MOC_READY);
                        }
                        try {
                            Thread.sleep(100);
                            startTime += 100;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    LogUtil.w(TAG, "-----Waiting for MTC Service-----end----------hasReadyFromMTC=" + hasReadyFromMTC);
                    hasReady = hasReadyFromMTC;
                }

                //被叫等待主叫准备好才启动Service
                else if (taskType == TaskType.PassivityCall) {
                    startTime = 0;
                    LogUtil.w(TAG, "-----Waiting for MOC Service-----start-----hasReadyFromMOC=" + hasReadyFromMOC + ",hasMOCTaskFinish=" + hasMOCTaskFinish);
                    while (!hasReadyFromMOC && !hasMOCTaskFinish
                            && !appModel.isTestInterrupt()) {
                        if (startTime / 100 % 300 == 0) {
                            mEventMgr.addTagEvent(mContext, System.currentTimeMillis(), "Waiting for MOC Service");
                        }
                        try {
                            startTime += 100;
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    LogUtil.w(TAG, "-----Waiting for MOC Service-----end-----hasReadyFromMOC=" + hasReadyFromMOC + ",hasMOCTaskFinish=" + hasMOCTaskFinish);
                    hasReady = hasReadyFromMOC && !hasMOCTaskFinish;
                    hasReadyFromMOC = false;
                }

                //短信发送方等待接收方准备好
                if (taskType == TaskType.SMSSend) {

                    if (j == 0) {
                        hasReadyFromSmsRecver = false;
                        while (!hasReadyFromSmsRecver && !appModel.isTestInterrupt()) {
                            LogUtil.i(TAG, "Waiting for Sms Receiver Service");
                            if ((System.currentTimeMillis() - startTime) / 1000 % 15 == 0) {
                                sendNormalMessage(TestTaskService.MSG_UNIT_SMS_SEND_START);
                                mEventMgr.addTagEvent(mContext, System.currentTimeMillis(),
                                        "Waiting for Sms Receiver Service");
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        hasReady = hasReadyFromSmsRecver;
                    } else {
                        hasReady = true;
                    }
                }

                //短信接收方等待发送方准备好才启动Service( 短信接收服务只启动一次接收多条短信 )
                else if (taskType == TaskType.SMSIncept) {

                    if (j == 0) {
                        //清空状态
                        hasReadyFromSmsSender = false;
                        while (!hasReadyFromSmsSender && !hasStopFromSmsSender
                                && !appModel.isTestInterrupt()) {
                            LogUtil.i(TAG, "Waiting for SMS Sender Service");
                            if ((System.currentTimeMillis() - startTime) / 1000 % 15 == 0) {
                                mEventMgr.addTagEvent(mContext, System.currentTimeMillis(),
                                        "Waiting for SMS Sender Service");
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        hasReady = hasReadyFromSmsSender && !hasStopFromSmsSender;
                    }
                }

                //彩信发送方等待接收方准备好
                else if (taskType == TaskType.MMSSend) {

                    hasReadyFromMmsRecver = false;

                    while (!hasReadyFromMmsRecver && !appModel.isTestInterrupt()) {
                        LogUtil.i(TAG, "Waiting for MMS Receiver Service");
                        if ((System.currentTimeMillis() - startTime) / 1000 % 15 == 0) {
                            sendNormalMessage(TestTaskService.MSG_UNIT_MMS_SEND_START);
                            mEventMgr.addTagEvent(mContext, System.currentTimeMillis(),
                                    "Waiting for MMS Receiver Service");
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    hasReady = hasReadyFromMmsRecver;
                }

                //彩信接收方等待发送方准备好才启动Service
                else if (taskType == TaskType.MMSIncept) {
                    //清空状态
                    hasReadyFromMmsSender = false;
                    while (!hasReadyFromMmsSender && !hasStopFromMmsSender
                            && !appModel.isTestInterrupt()) {
                        LogUtil.i(TAG, "Waiting for MMS Sender Service");
                        if ((System.currentTimeMillis() - startTime) / 1000 % 15 == 0) {
                            mEventMgr.addTagEvent(mContext, System.currentTimeMillis(),
                                    "Waiting for MMS Sender Service");
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    hasReady = hasReadyFromMmsSender && !hasStopFromMmsSender;
                }

                //主叫告诉被叫手机：当前任务不是MOC测试
                //twq20140311加当前非并发派生的业务才需要通知道状态
                if (taskType != WalkStruct.TaskType.InitiativeCall && !isMultiDerive) {
                    sendNormalMessage(TestTaskService.MSG_UNIT_NOT_MOC_TASK);
                }

                LogUtil.i(TAG, "excute " + taskType.name() + ":" + hasReady);

                return hasReady;
            }

            /**
             * 主被叫联合测试的尾巴对齐: 主被叫联合测试的同步
             *
             * @param postion 业务所在位置
             */
            private void unitTestSyncTail(int postion) {
                long startTime = System.currentTimeMillis();
                //语音主叫的尾部对齐(主叫结束后向被叫发送挂机指令)
                if (taskType == TaskType.InitiativeCall) {

                    hasHangupAckFromMTC = false;
                    startTime = 0;
                    LogUtil.w(TAG, "-----Waiting for MTC hangup-----start-----hasHangupAckFromMTC=" + hasHangupAckFromMTC);
                    while (!hasHangupAckFromMTC && !appModel.isTestInterrupt()) {
//                        LogUtil.w(TAG, "Waiting for MTC hangup");
                        if (startTime / 100 % 300 == 0) {
                            mEventMgr.addTagEvent(mContext, System.currentTimeMillis(),
                                    "Waiting for MTC hangup");
                            //语音任务停止的消息
                            sendNormalMessage(TestTaskService.MSG_UNIT_MOC_HANGUP);
                        }

                        try {
                            startTime += 100;
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                    LogUtil.w(TAG, "-----Waiting for MTC hangup-----end-----hasHangupAckFromMTC=" + hasHangupAckFromMTC);

                    //如果是主叫任务的最后一次,告诉被叫停止当前任务
                    if (postion == model.getRepeat() - 1) {
                        sendNormalMessage(TestTaskService.MSG_UNIT_NOT_MOC_TASK);
                    }
                }

                //语音被叫的尾部对齐(被叫完成一次任务后，等待到主叫的挂机指令)
                else if (taskType == TaskType.PassivityCall) {
                    /*正常情况不会进入此循环,当被叫DropCall或其它原因先退出进程来到这里，
                     *而这时主叫还没有发送挂机指令 MSG_UNIT_MOC_HANGUP
                     */
                    startTime = 0;
                    LogUtil.w(TAG, "-----Waiting for MOC hangup-----start-----hasHangupFromMOC=" + hasHangupFromMOC + ",hasMOCTaskFinish=" + hasMOCTaskFinish);
                    while (!hasHangupFromMOC && !hasMOCTaskFinish
                            && !appModel.isTestInterrupt()) {
                        if (startTime / 100 % 300 == 0) {
                            mEventMgr.addTagEvent(mContext, System.currentTimeMillis(), "Waiting for MOC hangup");
                        }

                        try {
                            startTime += 100;
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    LogUtil.w(TAG, "-----Waiting for MOC hangup-----end-----");
                    //复位此标志
                    hasHangupFromMOC = false;
                }

                //发送方最后一次时进行尾同步
                if (postion == model.getRepeat() - 1) {
                    if (taskType == TaskType.SMSSend) {
                        //先清空状态
                        hasStopAckFromSmsRcver = false;
                        do {
                            //语音任务停止的消息
                            sendNormalMessage(TestTaskService.MSG_UNIT_SMS_SEND_END);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            LogUtil.i(TAG, "Waiting for SMS Receiver's End");
                            if ((System.currentTimeMillis() - startTime) / 1000 % 15 == 0) {
                                mEventMgr.addTagEvent(mContext, System.currentTimeMillis(),
                                        "Waiting for SMS Receiver's End");
                            }
                        } while (!hasStopAckFromSmsRcver && !appModel.isTestInterrupt());

                    } else if (taskType == TaskType.MMSSend) {
                        //先清空状态
                        hasStopAckFromMmsRcver = false;
                        do {
                            //语音任务停止的消息
                            sendNormalMessage(TestTaskService.MSG_UNIT_MMS_SEND_END);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            LogUtil.i(TAG, "Waiting for MMS Receiver's End");
                            if ((System.currentTimeMillis() - startTime) / 1000 % 15 == 0) {
                                mEventMgr.addTagEvent(mContext, System.currentTimeMillis(),
                                        "Waiting for MMS Receiver's End");
                            }
                        } while (!hasStopAckFromMmsRcver && !appModel.isTestInterrupt());
                    }
                }

                //接收方就不用尾部同步了，hasStopFromSender为true的时候，下一次接入任务的服务都不会启动
            }

            /**
             * 处理界面显示
             */
            private void displayEvent(int j) {
                //twq20161019如果当前任务未写入测试计划且非并发派生，将测试内容写入RCU文件中
                if (!testPlanWList.contains(model.getTaskID()) && !isMultiDerive) {
                    showEvent("Start Task:" + model.getTaskID());

                    testPlanWList.add(model.getTaskID());
                    writeTestPlanToRcu(model);
                }

                //第二次以上的任务或上一次失败不计入统计的显示分隔符
                if ((j > 0 || (currModelDoneState.get(model.getTaskName()) != null
                        && currModelDoneState.get(model.getTaskName()).getTestTotalCount() > 0)
                ) && !isMultiDerive) {
                    sendSplitLine();
                }

                //设置当前任务执行次数，用于关于中的显示
                if (!isMultiDerive) {
                    TraceInfoInterface.traceData.setTestTimes(model.getTaskType(),
                            String.valueOf(model.getRepeat()), String.valueOf(j + 1), String.valueOf(outCircleTimes), String.valueOf(currentCircle + 1));
                }

                //放循环前面显示
                showInfo.SetChartProperty();
            }

            /**
             * 处理并发总超时状态
             *
             * @return 是否要跳到下一个业务
             */
            private boolean processRabTest() {
                //twq20131210如果当前任务为并发派生子线程，且当前并发限制超时状态为直，那么并发中子任务不再继续
                if (isMultiDerive && currentMultiRabTimeOut
                        || ((taskType == TaskType.Http || taskType == TaskType.HttpRefurbish) && currentHttpTimeOut)) {
                    LogUtil.w(TAG, "--Rab:" + model.getTaskName() + "--RabTimeOut--");
                    return true;
                }

                return false;
            }

            @Override
            public synchronized void start() {
                super.start();
            }

            /**
             * 处理小背包测试
             *
             * @return 小背包和pionner相关的信息
             */
            private String processPionnerTest(int j) {
                String testStartInfo = "";

                if (isUmpcTest) {
                    // 此处要注意并行业务的小循环处理
                    pioneerInfo.setCurrentLoop(j + 1);
                    pioneerInfo.setMutil(isMultiDerive);
                    pioneerInfo.setTestKind(taskType.getTestKind());
                    pioneerInfo.setLoop(model.getRepeat());

                    testStartInfo = (umpcTestinfo.getController() == UmpcTestInfo.ControlForPioneer
                            ? pioneerInfo.getCurrentLoopStr() : "");
                    UmpcSwitchMethod.sendEventToController(getApplicationContext(),
                            UMPCEventType.RealTimeData.getUMPCEvnetType(),
                            pioneerInfo.getCurrentLoopStr(), umpcTestinfo.getController());
                }

                return testStartInfo;
            }

            /***
             * 是否可以继续执行Wlan测试
             *
             * @param times
             *            任务循环执行到第几次
             * @return 是否继续
             */
            private boolean processWifiTest(int times) {
                boolean flag = false;
                int connectTimes = 20;
                if (taskType != TaskType.WlanAP && taskType != TaskType.WlanLogin && taskType != TaskType.WlanEteAuth) {
                    if (model.getDisConnect() == PPPRule.pppHangupNone
                            || model.getDisConnect() == PPPRule.pppHangupJobDone) {
                        if (times == 0) {
                            writeStartOrStopEventToRcu(RcuEventCommand.TEST_PLAN_START);
                            while (connectTimes > 0 && !ApplicationModel.getInstance().isTestInterrupt()) {// 登陆三次,如果失败,则不做业务
                                // 直接关闭数据网络即可
                                pppRule.setDataEnabled(isWlanTest, false);
                                // 0-不断开
                                // 2-任务完成后断开在wifi模式下处理一直,每次任务结束后都断开,及先登录完成任务后断开

                                getWifiPortalManager(mContext, model);
                                try {
                                    flag = wifiPortalManager.loginAP(true, true, wifiManager, 60, 0);
                                    if (flag)
                                        break;
                                    // 登陆失败,需要执行一次logout,确保释放资源
                                    wifiPortalManager.logoutAP();
                                    wifiPortalManager.disconnectAP(true, wifiManager);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                wifiPortalManager = null;

                                connectTimes -= 1;
                            }
                        }
                    } else if (model.getDisConnect() == PPPRule.pppHangupEvery) {
                        writeStartOrStopEventToRcu(RcuEventCommand.TEST_PLAN_START);
                        while (connectTimes > 0 && !ApplicationModel.getInstance().isTestInterrupt()) {// 登陆三次,如果失败,则不做业务
                            pppRule.setDataEnabled(isWlanTest, false);
                            // 0-不断开
                            // 2-任务完成后断开在wifi模式下处理一直,每次任务结束后都断开,及先登录完成任务后断开
                            getWifiPortalManager(mContext, model);

                            try {
                                flag = wifiPortalManager.loginAP(true, true, wifiManager, 60, 0);
                                if (flag)
                                    break;
                                // 登陆失败,需要执行一次logout,确保释放资源
                                wifiPortalManager.logoutAP();
                                wifiPortalManager.disconnectAP(true, wifiManager);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            wifiPortalManager = null;

                            connectTimes -= 1;
                        }
                    }
                    return flag;
                }
                return true;
            }

            /***
             * 需要做wifi测试业务的初始化对象
             * @param context 上下文
             * @param model 测试业务
             */
            private void getWifiPortalManager(Context context, TaskModel model) {
                wifiPortalManager = null;
                String apName;
                String userName;
                String password;

                if (model instanceof TaskFtpModel) {
                    TaskFtpModel ftp = (TaskFtpModel) model;
                    List<WifiConnectionInfo> wifilist = ftp.getNetworkConnectionSetting().getWifiList();
                    for (WifiConnectionInfo wi : wifilist) {
                        if (wi.isCheck()) {
                            apName = wi.getApName();
                            password = wi.getPassword();
                            userName = wi.getUserName();
                            wifiPortalManager = new WifiPortalManager(isLib(apName), context, apName, userName,
                                    password);
                            break;
                        }
                    }
                } else if (model instanceof TaskPingModel) {
                    TaskPingModel ftp = (TaskPingModel) model;
                    List<WifiConnectionInfo> wifilist = ftp.getNetworkConnectionSetting().getWifiList();
                    for (WifiConnectionInfo wi : wifilist) {
                        if (wi.isCheck()) {
                            apName = wi.getApName();
                            password = wi.getPassword();
                            userName = wi.getUserName();
                            wifiPortalManager = new WifiPortalManager(isLib(apName), context, apName, userName,
                                    password);
                            break;
                        }
                    }
                } else if (model instanceof TaskVideoPlayModel) {
                    TaskVideoPlayModel ftp = (TaskVideoPlayModel) model;
                    List<WifiConnectionInfo> wifilist = ftp.getNetworkConnectionSetting().getWifiList();
                    for (WifiConnectionInfo wi : wifilist) {
                        if (wi.isCheck()) {
                            apName = wi.getApName();
                            password = wi.getPassword();
                            userName = wi.getUserName();
                            wifiPortalManager = new WifiPortalManager(isLib(apName), context, apName, userName,
                                    password);
                            break;
                        }
                    }
                } else if (model instanceof TaskHttpPageModel) {
                    TaskHttpPageModel ftp = (TaskHttpPageModel) model;
                    List<WifiConnectionInfo> wifilist = ftp.getNetworkConnectionSetting().getWifiList();
                    for (WifiConnectionInfo wi : wifilist) {
                        if (wi.isCheck()) {
                            apName = wi.getApName();
                            password = wi.getPassword();
                            userName = wi.getUserName();
                            wifiPortalManager = new WifiPortalManager(isLib(apName), context, apName, userName,
                                    password);
                            break;
                        }
                    }
                } else if (model instanceof TaskHttpUploadModel) {
                    TaskHttpUploadModel ftp = (TaskHttpUploadModel) model;
                    List<WifiConnectionInfo> wifilist = ftp.getNetworkConnectionSetting().getWifiList();
                    for (WifiConnectionInfo wi : wifilist) {
                        if (wi.isCheck()) {
                            apName = wi.getApName();
                            password = wi.getPassword();
                            userName = wi.getUserName();
                            wifiPortalManager = new WifiPortalManager(isLib(apName), context, apName, userName,
                                    password);
                            break;
                        }
                    }
                }
            }

            /***
             * 是否需要使用库登陆
             * @param apName ap名称
             * @return 是否使用
             */
            private boolean isLib(String apName) {
                return (apName.equals("ChinaNet") || apName.equals("ChinaUnicom") || apName.equals("CMCC")
                        || apName.equals("CMCC-WEB") || apName.equals("CMCC-AUTO"));
            }

            /**
             * 处理各种状态，
             *
             * @return 是否要跳出当前任务
             */
            private boolean processStatus() {

                //脱 网时插入循环等待并插入事件,语音主叫按在进程里单独处理
                if (routineSet.checkOutOfService()) {
                    waitForSignal(taskType);
                }

                //twq20120403进入测试任务前，判断当前是否在暂停测试状态，如果是则锁定线程，等待继续测试唤醒
                if (appModel.isTestPause() && !routineSet.isPuaseKeepTest(mContext)) {
                    LogUtil.w(TAG, "---to pause the test thread---");
                    try {
                        synchronized (lock) {
                            lock.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //如果测试被终止，直接跳出for循环停止测试
                if (appModel.isTestInterrupt()) {
                    LogUtil.w(TAG, "--Interrupt by for--");
                    return true;
                }

                return false;
            }

            /**
             * 函数功能：处理拨号策略
             *
             * @param postion 循环次数
             * @return 后继处理
             */
            private int processPPPDial(int postion) {

//                if (Deviceinfo.getInstance().isHasNBModule()) {//如果是NB模块,不需要拨号
//                    //如果测试被终止，直接跳出for循环停止测试
//                    if (appModel.isTestInterrupt()) {
//                        return TASK_BREAK;
//                    } else if (isMultiDerive && currentMultiRabTimeOut) {
//                        return TASK_BREAK;
//                    }
//
//                    return TASK_EXCUTE;
//
//                } else {
//                 如果当前是并发派生线程，不需要支持下拨号的相关动作,
//                 如果当前是并发操作，且语音时延小于0，即语音提前的，拨号在语音时延后执行拨号
//                 在语音时延后拨号不成功时会有BUG，该任务只能做语音
                if (!isMultiDerive && taskType != TaskType.MultiRAB || (taskType == TaskType.MultiRAB && ((TaskRabModel) model).getVoiceDelay() >= 0)) {

                    pppResult = doPPPRuleByModel(model, taskType, postion);
                    if (pppResult == PPPRule.PPP_RESULT_FAIL) {
                        return TASK_CONTINUE;
                    } else if (pppResult == PPPRule.PPP_RESULT_ApnNull) {
                        return TASK_BREAK;
                    }
                }

                int noConnectTimes = 0;
                //如果当前为并发派生，且为数据任务PPPDrop后直重新打开拨号流程	//接退出当前循环
                //twq20140417当前为并发派生数据业务,当前网络状态为非连接时等待,且死等,中断,并发总超时,有恢复网络是退出当前循环
                if (isMultiDerive && taskType.isDataTest() && !isWlanTest && myPhoneState.getDataState() != TelephonyManager.DATA_CONNECTED) {
                    while (myPhoneState.getDataState() != TelephonyManager.DATA_CONNECTED && !appModel.isTestInterrupt() && !currentMultiRabTimeOut) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (noConnectTimes % 10 == 0) {
                            LogUtil.w(TAG, "--do dataJob wait connected:" + noConnectTimes);
                            EventBytes.Builder(mContext, RcuEventCommand.DataServiceMsg).addInteger(taskType.getTestType()).addInteger(2)            //1,消息;2,告警;3,错误
                                    .addInteger(-9999)        //无效值
                                    .addStringBuffer("Internet connection is unavailable")    //描述
                                    .addStringBuffer("")    //上下文
                                    .writeToRcu(System.currentTimeMillis() * 1000);
                        }
                        noConnectTimes++;
                    }
                }

                //如果测试被终止，直接跳出for循环停止测试
                if (appModel.isTestInterrupt()) {
                    LogUtil.w(TAG, "--Interrupt by for--");
                    return TASK_BREAK;
                } else if (isMultiDerive && currentMultiRabTimeOut) {
                    return TASK_BREAK;
                }

                return TASK_EXCUTE;
//                }
            }

            /**
             * 处理语音测试相关功能
             */
            private void processCallTest() {
                AlertManager.getInstance(mContext).setTestingMos(false);
                if (taskType == TaskType.InitiativeCall) {
                    csfbHM.put("CallType", 0L);
                    TaskInitiativeCallModel mocModel = (TaskInitiativeCallModel) model;
                    //MOS测试时关闭语音告警
                    AlertManager.getInstance(mContext).setTestingMos(
                            mocModel.getMosTest() == TaskInitiativeCallModel.MOS_ON);

                    if (mocModel.getTestConfig().isMosTest()) {
                        hasMOCMOSTest = true;
                    }
                } else if (taskType == WalkStruct.TaskType.PassivityCall) {
                    csfbHM.put("CallType", 1L);
                    TaskPassivityCallModel mtcModel = (TaskPassivityCallModel) model;
                    AlertManager.getInstance(mContext).setTestingMos
                            (mtcModel.getCallMOSServer() == TaskPassivityCallModel.MOS_ON);
                } else if (taskType == TaskType.WeCallMoc || taskType == TaskType.WeCallMtc) {
                    TaskWeCallModel mocModel = (TaskWeCallModel) model;
                    //MOS测试时关闭语音告警
                    AlertManager.getInstance(mContext).setTestingMos(
                            mocModel.getMosTest() == TaskInitiativeCallModel.MOS_ON);
                    if (mocModel.getTestConfig().isMosTest()) {
                        hasMOCMOSTest = true;
                    }
                }
            }

            /**
             * 函数功能：启动一次业务之前的准备（同步，脱网等待，拨号等）
             *
             * @return 当前任务的跳动，TASK_*
             */
            private int prepareTask(int j) {
                //并发派生的第一次任务不调时间等待
                if (!isMultiDerive || j > 0) {
                    waitNextTimeSplitFile(model, j);//下次任务启动之前的等待，如需要执行RCU文件分割等待
                }

                if (processRabTest()) {
                    return TASK_BREAK;
                }
                if (isWlanTest) {
                    if (processWifiTest(j)) {
                        return TASK_EXCUTE;
                    }
                    return TASK_BREAK;
                }
                if (processStatus()) {
                    return TASK_BREAK;
                }

                if (model.isUnitTest()) {
                    boolean syncSuccess = unitTestSyncHead(j);
                    if (!syncSuccess) {
                        return TASK_BREAK;
                    }
                }


                if (ApplicationModel.getInstance().isNBTest() && model.getTypeProperty() != WalkCommonPara.TypeProperty_Ppp) {//如果是NB模块,不需要拨号
                    //如果测试被终止，直接跳出for循环停止测试
                    if (appModel.isTestInterrupt()) {
                        return TASK_BREAK;
                    } else if (isMultiDerive && currentMultiRabTimeOut) {
                        return TASK_BREAK;
                    }

                    return TASK_EXCUTE;

                }

                return processPPPDial(j);
            }

            /**
             * 函数功能：根据不同业务启动不同的执行方法
             *
             * @param j 循环次数
             * @return 控制次数的j值
             */
            private int excuteOneTask(int j) throws Exception {
                displayEvent(j);
                processCallTest();
                String pionnerInfo = processPionnerTest(j);
                addNetTypeInfo();

                LogUtil.w(TAG, "--multiName:" + multiName + "--j:" + j + "--Type:" + taskType.name()
                        + "--threadNum:" + threadNum);
                // 测试任务开始调度
                if (taskType == TaskType.MultiRAB) {    //如果当前是并发任务
                    //写并发任务开始时事件
                    LogUtil.w(TAG, "--write ParallelStart Event--");
                    if (isUmpcTest || isBluetoothSync) {
                        waitForSyncDone(TestTaskService.MSG_UNIT_SYNC_Parallel + (j + 1));
                    }

                    wirteParallelEvent(model, RcuEventCommand.ParallelStart);
                    execMultiRabModel(model, j);
                } else if (taskType == TaskType.EmptyTask) {
                    LogUtil.w(TAG, "--Do EmptyTask--");
                    currModelDoneState.put(model.getTaskName(), new TestDoneStateModel(0));  //空任务不需要等待结束

                    EventBytes.Builder(TestService.this, RcuEventCommand.Idle_Test_Start)
                            .writeToRcu(System.currentTimeMillis() * 1000);

                    threadWaitCanInterrupt(((TaskEmptyModel) model).getIdleTestConfig().getKeepTime(), "idle");
                } else if (taskType == TaskType.Http || taskType == TaskType.HttpRefurbish) {
                    currentHttpDone = false;
                    currentHttpTimeOut = false;
                    TaskHttpPageModel httpModel = ((TaskHttpPageModel) model);
                    if (httpModel.getParallelTimeout() > 0) {
                        //如果此时PPP超时已超过http本身的超时，不再启http超时线程，直接设置http超时状态
                        if (httpPPPUsTimeOut >= httpModel.getParallelTimeout()) {
                            currentHttpTimeOut = true;
                        } else {
                            new HttpTimeOutInterrup((TaskHttpPageModel) model).start();
                        }
                    }
                    j = execUrlListModel(model, j, pdpDelay, isMultiDerive, pionnerInfo);


                } else if (taskType == TaskType.WlanAP || taskType == TaskType.WlanLogin || taskType == TaskType.WlanEteAuth) {


                    // 如果是WIFI测试中的这三个业务,那么区别对待
                    boolean isLib;
                    if (taskType == TaskType.WlanAP) {// 如果是WLAN AP测试
                        if (model instanceof TaskWlanApModel) {
                            TaskWlanApModel taskWlanApModel = (TaskWlanApModel) model;
                            writeStartOrStopEventToRcu(RcuEventCommand.TEST_PLAN_START);
                            String apName = taskWlanApModel.getWlanAPRelationTestConfig().getApName();
                            String userName = taskWlanApModel.getWlanAPRelationTestConfig().getWlanAccount().getUsername();
                            String password = taskWlanApModel.getWlanAPRelationTestConfig().getWlanAccount().getPassword();
                            isLib = isLib(apName);
                            wifiPortalManager = new WifiPortalManager(isLib, mContext, apName, userName, password);
                            wifiPortalManager.connectAp(true, true, wifiManager, taskWlanApModel.getTimeOut(), taskWlanApModel.getHoldTime());
                            wifiPortalManager.disconnectAP(true, wifiManager);
                            writeStartOrStopEventToRcu(RcuEventCommand.TEST_PLAN_STOP);
                        }
                    } else if (taskType == TaskType.WlanLogin) {// 如果是WLAN LOGIN测试
                        if (model instanceof TaskWlanLoginModel) {
                            TaskWlanLoginModel taskWlanLoginModel = (TaskWlanLoginModel) model;
                            writeStartOrStopEventToRcu(RcuEventCommand.TEST_PLAN_START);
                            String apName = taskWlanLoginModel.getWlanWebLoginTestConfig().getApName();
                            String userName = taskWlanLoginModel.getWlanWebLoginTestConfig().getWlanAccount().getUsername();
                            String password = taskWlanLoginModel.getWlanWebLoginTestConfig().getWlanAccount().getPassword();
                            isLib = isLib(apName);
                            wifiPortalManager = new WifiPortalManager(isLib, mContext, apName, userName, password);
                            boolean isL = wifiPortalManager.loginAP(true, false, wifiManager, taskWlanLoginModel.getTimeOut(), 0);
                            if (isL) {
                                wifiPortalManager.logoutAP();
                            }
                            wifiPortalManager.disconnectAP(false, wifiManager);
                            writeStartOrStopEventToRcu(RcuEventCommand.TEST_PLAN_STOP);
                        }
                    } else if (taskType == TaskType.WlanEteAuth) {// 如果是WLAN ETE AUTH测试
                        if (model instanceof TaskWlanEteAuthModel) {
                            TaskWlanEteAuthModel taskWlanEteAuthModel = (TaskWlanEteAuthModel) model;
                            writeStartOrStopEventToRcu(RcuEventCommand.TEST_PLAN_START);
                            String apName = taskWlanEteAuthModel.getWlanETEAuthTestConfig().getApName();
                            String userName = taskWlanEteAuthModel.getWlanETEAuthTestConfig().getWlanAccount().getUsername();
                            String password = taskWlanEteAuthModel.getWlanETEAuthTestConfig().getWlanAccount().getPassword();
                            isLib = isLib(apName);
                            wifiPortalManager = new WifiPortalManager(isLib, mContext, apName, userName, password);
                            boolean isL = wifiPortalManager.loginAP(true, true, wifiManager, taskWlanEteAuthModel.getTimeOut(), 0);
                            if (isL) {
                                wifiPortalManager.logoutAP();
                            }
                            wifiPortalManager.disconnectAP(true, wifiManager);
                            writeStartOrStopEventToRcu(RcuEventCommand.TEST_PLAN_STOP);
                        }
                    }
                    wifiPortalManager = null;
                } else {
                    j = execAnTaskModel(model, j, pdpDelay, pionnerInfo);
                }

                //如果当前任务没有完成,且不是中断状态,则在此处循环休眠等待,
                //任务完成是在具体测试任务回调中产生最后一个数据包表示任务完成
                if (currModelDoneState.get(model.getTaskName()) != null) {
                    while (currModelDoneState.get(model.getTaskName()).getDoneStateNum() > 0
                            && !appModel.isTestInterrupt()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        LogUtil.w(TAG, "--addNetTypeInfo5:taskName=" + model.getTaskName() + ",DoneStateNum=" + currModelDoneState.get(model.getTaskName()).getDoneStateNum());
                        addNetTypeInfo();
                    }
                }

                return j;
            }

            /**
             * 在等待任务结果过程中,查询当前网络信息,并将网络信息入库
             */
            private int queryInterval = 0;

            private void addNetTypeInfo() {

                queryInterval++;
                if (queryInterval % 5 == 0) {
                    int netTypeId = NetStateModel.getInstance().getCurrentNetType().getNetTypeId();
                    LogUtil.w(TAG, "--addNetTypeInfo5:" + netTypeId);

                    mTestRecord.setRecordNetTypeMsg(RecordNetTypeEnum.net_type.name(), netTypeId);
                    appModel.addCurrentTestNet(netTypeId, netTypeId);
                }
            }

            private void finishOneTask(int j) {

                LogUtil.w(TAG, "--write Parallel done:" + taskType.name());
                if (taskType == TaskType.MultiRAB) {    //如果当前是并发任务
                    //写并发任务结束时事件
                    LogUtil.w(TAG, "--write ParallelFinish Event--");
                    currentMultiRabDone = true;
                    wirteParallelEvent(model, RcuEventCommand.ParallelFinish);
                }

                AlertManager.getInstance(mContext).setTestingMos(false);

                /*
                 * 同步联合测试尾部对齐:为了让主被叫的第N次对应上 ,防止错位.
                 */
                if (model.isUnitTest()) {
                    unitTestSyncTail(j);
                }

                //如果当前为数据业务，结束时需按设置断开规则处理断开动作
                if ((model.getTypeProperty() == WalkCommonPara.TypeProperty_Net
                        || model.getTypeProperty() == WalkCommonPara.TypeProperty_Wap)
                        && !isMultiDerive) {
                    //数据业务结束时清空数据业务相关折线图，仪表盘数据
                    showInfo.ClearQueueInfo(model.getTaskName());
                    pppRule.pppHangup(model.getDisConnect(), (j >= (model.getRepeat() - 1)), pppResult);
                } else if (isWlanTest) {//如果当前为WLAN测试
                    if (taskType != TaskType.WlanAP && taskType != TaskType.WlanLogin && taskType != TaskType.WlanEteAuth) {
                        if (model.getDisConnect() == PPPRule.pppHangupEvery) {
                            //每次都断开
                            try {
                                wifiPortalManager.logoutAP();
                                wifiPortalManager.disconnectAP(true, wifiManager);
                                wifiPortalManager = null;
                                writeStartOrStopEventToRcu(RcuEventCommand.TEST_PLAN_STOP);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else if (model.getDisConnect() == PPPRule.pppHangupNone || model.getDisConnect() == PPPRule.pppHangupJobDone) {
                            // 0-不断开 2-任务完成后断开在wifi模式下处理一直,每次任务结束后都断开,及先登录完成任务后断开
                            try {
                                if (j >= (model.getRepeat() - 1)) {//
                                    wifiPortalManager.logoutAP();
                                    wifiPortalManager.disconnectAP(true, wifiManager);
                                    wifiPortalManager = null;
                                    writeStartOrStopEventToRcu(RcuEventCommand.TEST_PLAN_STOP);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } else if (taskType == TaskType.InitiativeCall || taskType == TaskType.PassivityCall) {
                    if (appModel.hasSamsungVoLTE()) {
                        LogUtil.w(TAG, "--VoLTE Call End--");

                        long currentTime = System.currentTimeMillis();
                        EventBytes.Builder(mContext)
                                .addInteger(SPECIALDATT_TYPE_SIPEND)    //Special Data Type
                                .addInteger(0)                            //Version 版本号，当版本为0
                                .addInteger((int) currentTime / 1000)
                                .addInteger((int) currentTime % 1000)
                                .addByte((byte) 0xff)
                                .addInteger(100)
                                .writeToRcu(WalkCommonPara.MsgDataFlag_C);
                    }
                }
            }

            /*
             * 根据业务类型切换logmask
             */
            private void changeLogmaskByJob() {
                if (isRabReady) {//并发测试不在这里控制
                    LogUtil.w(TAG, "-----change logmask to isRabReady=true-----");
                    return;
                } else {
                    LogUtil.w(TAG, "-----change logmask to isRabReady=false-----");
                }
                if (currentTaskTypeInfo == TaskModel.TYPE_PROPERTY_CALL) {
                    mDatasetMgr.changeToVoLTELogMask();
                } else {
                    mDatasetMgr.changeToNormalLogMask();
                }

            }

            public void run() {
                //twq20140312当前为主被叫,且没有开启蓝牙步,非小背包测试,将模版中的联合测试状态置为false
                //因为有可能蓝牙开关关闭后业务中的联合状态为真,或者执行的是小背包下发的模板,故此处被主被叫联合置成false;
                if (!isBluetoothSync && !isUmpcTest) {
                    model.setUnitTest(false);
                }

                LogUtil.w(TAG, "---taskType:" + taskType + "--isDataJob:" + model.getTypeProperty());
                //默认开去REBOOT权限
                if (!appModel.getTaskList().contains(taskType) && !taskType.equals(TaskType.REBOOT) && !taskType.equals(TaskType.OpenSignal)) {
                    //当任务列表中的任务类型不存在于用户授权限表中时提示当前任务没有执行权限，并继续下个测试任务
                    Message msg = mHandler.obtainMessage(testTaskNotPower, taskType.getShowName(mContext));
                    mHandler.sendMessage(msg);
                    doneCurrentTask = true;
                    EventManager.getInstance().addTagEvent(getApplicationContext(), System.currentTimeMillis(), "Walktour have no testing authority:" + taskType);
                    LogUtil.w(TAG, "---You have no current operational testing authority:" + taskType + "---");
                } else {
                    setFirstDataInfo(model);
                    appModel.setCurrentTask(taskType);

                    //开始一个测试任务(一个任务循环)

                    hasStopFromSmsSender = false;
                    hasStopFromMmsSender = false;


                    if (isCallTest(model)) {
                        currentTaskTypeInfo = TaskModel.TYPE_PROPERTY_CALL;
                    } else {
                        currentTaskTypeInfo = TaskModel.TYPE_PROPERTY_NOCALL;
                    }
                    changeLogmaskByJob();
                    for (int j = 0; j < model.getRepeat() && !appModel.isTestInterrupt(); j++) {

                        hasMOCTaskFinish = false;
                        int what = prepareTask(j);
                        LogUtil.w(TAG, "what=" + what);
                        if (what == TASK_BREAK) {
                            break;
                        } else if (what == TASK_EXCUTE && !appModel.isTestInterrupt()) {
                            mEventMgr.addTagEvent(mContext, System.currentTimeMillis(), "Start Task:" + model.getTaskName() + "-" + (j + 1));

                            if (j == 0 && !isMultiDerive) {
                                mTestRecord.setRecordTaskTypeMsg(model.getTaskName(), RecordTaskTypeEnum.task_info_id.name(), model.getTaskName());
                                mTestRecord.setRecordTaskTypeMsg(model.getTaskName(), RecordTaskTypeEnum.task_type.name(), taskType.getTestTypeId());
                                mTestRecord.setRecordTaskTypeMsg(model.getTaskName(), RecordTaskTypeEnum.test_plan.name(), TaskTestObject.getTestPlanInfo(model));
                                mTestRecord.setRecordTaskTypeMsg(model.getTaskName(), RecordTaskTypeEnum.excute_time.name(), System.currentTimeMillis());

                            }
                            appModel.addCurrentTestTaskList(model.getTaskType(), model.getTaskType());
                            //录果当前不是并发派生任务且当前为数据业务,则调用激活网络动作
                            if (!isMultiDerive && (model.getTypeProperty() == 2 || model.getTypeProperty() == 3)) {
                                if (appModel.getNetList().contains(WalkStruct.ShowInfoType.TelecomSwitch)
                                        && !NetWorkAlive.checkTelecomSet(mContext, mEventMgr)) {
                                    //如果当前为电信设置开关打开状态,后面的方法检查当前网络是否匹配,如果返回false的话,表示不匹配返回,直接break退出
                                    break;
                                }
                                NetWorkAlive.doNetWorkAlive(mContext, System.currentTimeMillis());
                            } else if (!isMultiDerive && model.getTypeProperty() == 1) {
                                //语音业务
                                if (appModel.getNetList().contains(WalkStruct.ShowInfoType.TelecomSwitch)
                                        && !NetWorkAlive.checkTelecomVoiceSet(mContext, mEventMgr)) {
                                    //如果当前为电信设置开关打开状态,后面的方法检查当前网络是否匹配,如果返回false的话,表示不匹配返回,直接break退出
                                    break;
                                }
                            }

                            try {
                                j = excuteOneTask(j);
                            } catch (Exception e) {
                                e.printStackTrace();
                                LogUtil.w(TAG, e.getMessage() + "");
                            }

                            //此次决定当前次任务是否需要重做，如果是则计算出来的值减1
                            boolean doAgain = needDoAgain(model.getTaskName());
                            //htt登录和刷新的失败重做控制在execUrlListModel里
                            if (doAgain && taskType != TaskType.Http
                                    && taskType != TaskType.HttpRefurbish) {
                                j--;
                            }


                            finishOneTask(j);


                        }

                    }

                    //子任务结束次数结束后根据当前是否并发派生进行不同处理
                    if (isMultiDerive) {
                        taskDoneStateChange(multiName);
                    } else {
                        doneCurrentTask = true;
                        currentMultiRabTimeOut = false;
                    }

                    //LogUtil.w(tag,"---set default---");
                    showInfo.ClearQueueInfo(model.getTaskName());
                    appModel.setCurrentTask(TaskType.Default);
                    TraceInfoInterface.traceData.setTestTimes("-", "-", "-", "-", "-");
                }
            }
        }

        /**
         * 任务调度中的调用PPP中断
         *
         * @param model    测试业务
         * @param taskType 业务类型
         * @param j        循环次数
         * @return 结果
         */
        private int doPPPRuleByModel(TaskModel model, TaskType taskType, int j) {
            return doPPPRuleByModel(model, taskType, j, -9999);
        }

        /**
         * 任务调度中的调用PPP中断，添加超时控制
         *
         * @param model    测试业务
         * @param taskType 任务类型
         * @param j        循环次数
         * @param timeOut  超时时长
         * @return 结果
         */
        private int doPPPRuleByModel(TaskModel model, TaskType taskType, int j, int timeOut) {
            int pppResult = PPPRule.PPP_RESULT_SUCCESS;
            LogUtil.w(TAG, "----doPPPRuleByModel----start----");

            //如果当前为数据业务，需要执行拨号，等拨号结束过后才真正进入数据业务
            if (model.getTypeProperty() == WalkCommonPara.TypeProperty_Net
                    || model.getTypeProperty() == WalkCommonPara.TypeProperty_Wap
                    || model.getTypeProperty() == WalkCommonPara.TypeProperty_Ppp) {
                //数据业务开始前处理，如果当前为每次断开网络，或者任务完成断开且当前为任务第一次
                //且当前接入点有效，执行断开网络处理
                if ((model.getDisConnect() == 1
                        || model.getDisConnect() == 2 && j == 0)
                        && APNOperate.getInstance(mContext).checkNetWorkIsConnected()) {
                    pppRule.pppHangup(model.getDisConnect(), (j >= (model.getRepeat() - 1)), PPPRule.pppFaildOther);
                }
                if (!ApplicationModel.getInstance().isGeneralMode()) {
                    pppResult = pppRule.pppDial(isFirstDataJob, taskType, model.getTypeProperty(), timeOut);
                    LogUtil.w(TAG, "--isFirstDataJob:" + isFirstDataJob + "--pppResult:" + pppResult + "--" + model.getTypeProperty());
                }

                //去掉第一个数据任务状态
                if (isFirstDataJob) {
                    isFirstDataJob = false;
                }

                if (pppResult == PPPRule.PPP_RESULT_FAIL) {
                    //如果当前拨号失败，不管当前是断开策略是什么，都做断开网络动作让下一次业务重新拨号
                    pppRule.setDataEnabled(isWlanTest, false);
                    //如果当前为数据业务，且拨号失败，跳过当前次数据业务，进入下一次
                    //continue;
                } else if (pppResult == PPPRule.PPP_RESULT_ApnNull) {
                    //如果当前APN设置无效，测试中断
                    Intent interruptIntent = new Intent(WalkMessage.Action_Walktour_Test_Interrupt);
                    sendBroadcast(interruptIntent);
                    //break;
                }

            } else {

                //如果Mobile To Land测试，先进行时间同步
                if (taskType == WalkStruct.TaskType.InitiativeCall) {
                    TaskInitiativeCallModel mocModel = (TaskInitiativeCallModel) model;
                    if (mocModel.getMosTest() == TaskInitiativeCallModel.MOS_ON
                            && mocModel.getCallMOSTestType() == TaskInitiativeCallModel.MOS_M2L
                            && j == 0) {
                        //先拨号成功
                        pppRule.setDataEnabled(isWlanTest, true);
                        waitForSyncTime();
                    }
                }


                //如果当前不是数据业务，且接入点开着时，需要关闭
                // 2018.01.26 Yi.Lin 此处控制非数据业务关闭数据连接操作
                pppRule.setDataEnabled(isWlanTest, false);
            }
            LogUtil.w(TAG, "----doPPPRuleByModel----end----");
            return pppResult;
        }


        private synchronized void callAllTest() {
            if (appModel.isScannerTest()) {
                //测试开始调用串口服务器的创建RCU文件功能
                sendCreateRcuFileCommand();
            }

            doTest();

            //当果当前手机任务，执行到此处表示当前任务完成或中断完成，当前值为true，不进入等待退出业务
            //如果当前没有手机业务，则为扫频业务，此处的状态为false，一直等到手工中断测试结束
//            try{
//                while(!appModel.isTestInterrupt() && !taskd.hasEnabledTask()){
//					Thread.sleep(900);
//				}
//			}catch(Exception e){
//				LogUtil.w(TAG,"",e);
//			}
        }

        /**
         * 如果测试任务包含数据业务,发送查询流量短信
         *
         * @param enableList
         */
        private void sendSMS(List<TaskModel> enableList) {
            if (!ConfigRoutine.getInstance().isSMSInfo(mContext)) {
                return;
            }
            boolean isContainDataTest = false;//判断是否有数据业务测试
            //判断是否有流量业务
            if (null != enableList) {
                for (TaskModel model : enableList) {
                    if (isContainDataTest)
                        break;
                    WalkStruct.TaskType taskType = WalkStruct.TaskType.valueOf(model.getTaskType());
                    switch (taskType) {
                        case EmptyTask:
                            isContainDataTest = false;
                            break;
                        case InitiativeCall:
                            isContainDataTest = false;
                            break;
                        case PassivityCall:
                            isContainDataTest = false;
                            break;
                        case Ping:
                            isContainDataTest = true;
                            break;
                        case Attach:
                            isContainDataTest = false;
                            break;
                        case PDP:
                            isContainDataTest = true;
                            break;
                        case FTPUpload:
                            isContainDataTest = true;
                            break;
                        case FTPDownload:
                            isContainDataTest = true;
                            break;
                        case Http:
                            isContainDataTest = true;
                            break;
                        case HttpRefurbish:
                            isContainDataTest = true;
                            break;
                        case HttpDownload:
                            isContainDataTest = true;
                            break;
                        case EmailPop3:
                            isContainDataTest = true;
                            break;
                        case EmailSmtp:
                            isContainDataTest = true;
                            break;
                        case EmailSmtpAndPOP:
                            isContainDataTest = true;
                            break;
                        case SMSIncept:
                            isContainDataTest = false;
                            break;
                        case SMSSend:
                            isContainDataTest = false;
                            break;
                        case SMSSendReceive:
                            isContainDataTest = false;
                            break;
                        case MMSIncept:
                            isContainDataTest = true;
                            break;
                        case MMSSend:
                            isContainDataTest = true;
                            break;
                        case MMSSendReceive:
                            isContainDataTest = true;
                            break;
                        case WapLogin:
                            isContainDataTest = true;
                            break;
                        case WapRefurbish:
                            isContainDataTest = true;
                            break;
                        case WapDownload:
                            isContainDataTest = true;
                            break;
                        case WlanLogin:
                            break;
                        case Stream:
                            isContainDataTest = true;
                            break;
                        case DNSLookUp:
                            isContainDataTest = true;
                            break;
                        case SpeedTest:
                            isContainDataTest = true;
                            break;
                        case VOIP:
                            isContainDataTest = true;
                            break;
                        case HttpUpload:
                            isContainDataTest = true;
                            break;
                        case MultiRAB:
                            isContainDataTest = true;
                            break;
                        case HTTPVS:
                            isContainDataTest = true;
                            break;
                        case MultiftpUpload:
                            isContainDataTest = true;
                            break;
                        case MultiftpDownload:
                            isContainDataTest = true;
                            break;
                        case Facebook:
                            isContainDataTest = true;
                            break;
                        case TraceRoute:
                            isContainDataTest = true;
                            break;
                        case Iperf:
                            isContainDataTest = true;
                            break;
                        case PBM:
                            isContainDataTest = true;
                            break;
                        case WeiBo:
                            isContainDataTest = true;
                            break;
                        case WeChat:
                        case WeCallMoc:
                        case WeCallMtc:
                        case SkypeChat:
                        case QQ:
                        case WhatsAppChat:
                        case WhatsAppMoc:
                        case WhatsAppMtc:
                        case SinaWeibo:
                        case Facebook_Ott:
                        case Instagram_Ott:
                            isContainDataTest = false;
                            break;
                        case UDP:
                            isContainDataTest = true;
                            break;
                        case OpenSignal:
                            isContainDataTest = true;
                            break;
                        case MultiHttpDownload:
                            isContainDataTest = true;
                            break;
                        default:
                            break;
                    }
                }
            }

            if (isContainDataTest) {//发送短信查询流量
                if (MobileUtil.isChinaMobile(mContext)) {
                    sendSMS("10086", "cxll");
                }
                if (MobileUtil.isChinaUnicom(mContext)) {
                    sendSMS("10010", "cxll");
                }
                if (MobileUtil.isChinaTelecom(mContext)) {
                    sendSMS("10001", "108");
                }

            }
        }

        /**
         * 直接调用短信接口发短信
         *
         * @param phoneNumber
         * @param message
         */
        public void sendSMS(String phoneNumber, String message) {
            // 获取短信管理器
            android.telephony.SmsManager smsManager = android.telephony.SmsManager
                    .getDefault();
            // 拆分短信内容（手机短信长度限制）
            List<String> divideContents = smsManager.divideMessage(message);
            for (String text : divideContents) {
                smsManager.sendTextMessage(phoneNumber, null, text, null,
                        null);
            }
        }

        /**
         * 开始测试,获取任务列表,循环取得测试任务进行执行,在每个任务可多次执行,注意间隔及是否停止测试
         * 注意启动服务的Intent对象就specificTestJob对象,服务结束的时候是针对该Intent进行停止中断服务的.
         */
        private synchronized void doTest() {
            //check is wifi test?
            isWlanTest = taskd.isWlanTest();
            if (isWlanTest) {
                showEvent("WiFi Test");
            }

            List<TaskModel> enableList = taskd.getAllSelectedTask(testGroupIndex);
            for (int i = 0; i < enableList.size(); i++) {
                if (enableList.get(i).getFromType() == fromType) {
                    if (enableList.get(i).getTaskType().equals(TaskType.InitiativeCall.name())
                            || enableList.get(i).getTaskType().equals(TaskType.PassivityCall.name())) {
                        hasCall = true;
                    }
                }
            }

            sendSMS(enableList);

            doneJobName = taskMethod.getTestPlanFile(enableList);
            allEnableList = enableList;

            //当前有语音业务,有三星VoLTE权限,且为9250型号,开启写入SIP信令流程
//            if (hasCall && appModel.hasSamsungVoLTE()) {
//                splitVoLTEKey = SplitVoLTEKey.getInstance(mHandler, true, android.os.Build.MODEL, android.os.Build.TAGS);
//
//                //设置是否生成日志文件
//                //splitVoLTEKey.createSIPLog(true);
//                //设置文件路径
//                splitVoLTEKey.startSIPInfo(getFilesDir().getParent() + "/lib/whatsnew",1);
//            }

            if (enableList.size() <= 0) {    //|| !taskd.hasEnabledTask()
                taskListEmpty = true;
                Message msg = mHandler.obtainMessage(testTaskListEmpty);
                mHandler.sendMessage(msg);
            } else {
                //if(!appModel.isGeneralMode() && (isUmpcTest || appModel.getNetList().contains(WalkStruct.ShowInfoType.TelecomSwitch))){
                //twq20151216测试序号改为跟加密统一控制,如果/sdcard/walktour/config/dingli.swkf密码文件存在则加密,并生成序号
                //twq20160218加密顺序测试序号一次测试生成一个序号,不再每个文件生成一个序号
                if (isEncrypt()) {
                    //系统顺序分配测试记录序号
                    currentFileNum = routineSet.getFileNum();
                    routineSet.setFileNum(currentFileNum + 1);
                }

                sendCreateRcuFileCommand();
                new WriteDeviceInfoThread().start();
                int num = 0;
                try {
                    //IPDA测试时设置当前网络信息
                    if (isUmpcTest) {
                        int networkType = myPhoneState.getNetWorkType(getApplicationContext());
                        pioneerInfo.setCurrentNet(WalkStruct.NetworkTypeShow.getNetWorkByNetID(networkType).getNetForIpadTag());
                        pioneerInfo.setTestStatus(ForPioneerInfo.TESTSTATUS_TESTTING);
                    }
                    initMosTestInfo();
                    //添加外循环处理 twq20120321添加判断当前是否正在测试中判断，处理异常中断后测试还在进行的情况
                    LogUtil.w(TAG, "outCircleTimes=" + outCircleTimes);
                    for (currentCircle = 0;
                         currentCircle < outCircleTimes && !appModel.isTestInterrupt() && appModel.isTestJobIsRun();
                         currentCircle++) {

                        //显示Start Test
                        showEvent("Start Test" + (outCircleTimes > 1 ? "" + (currentCircle + 1) : ""));
                        pioneerInfo.setCurrentOuterLoop(currentCircle + 1);
                        pioneerInfo.setOuterLoop(outCircleTimes);
                        if (isUmpcTest) {
                            UmpcSwitchMethod.sendEventToController(getApplicationContext(), UMPCEventType.RealTimeData.getUMPCEvnetType(),
                                    pioneerInfo.getOuterLoopStr(), umpcTestinfo.getController());
                        }

                        //如果是UMPC测试，进入大循环前需要等待同步
                        //2013.01.23 和iPhone互拨，第2次大循环才进行同步 twq20140305蓝牙同步亦需要做外循环同步
                        //if((isUmpcTest || isBluetoothSync) && currentCircle>0 ){
                        if (isUmpcTest || isBluetoothSync) {
                            //iPhone的大循环是从第2次开始同步，符号是2
                            //twq20140311IPHONE的循环同步从0开始,即第一次都同步
                            //waitForSyncDone( currentCircle+1 );
                            waitForSyncDone(TestTaskService.MSG_UNIT_SYNC_Circle + (currentCircle + 1));
                        } else {
                            //进入下一次大循环前暂停5秒
                            if (currentCircle > 0) {
                                //如果是分步式测试，则中断测试等待
                                if (isStepTest && !appModel.isTestInterrupt()) {
                                    Intent doneIntent = new Intent(WalkMessage.NOTIFY_STEP_TEST_JOB_PAUSE);
                                    sendBroadcast(doneIntent);
                                    appModel.setTestStepPause(true);
                                    while (true) {
                                        try {
                                            Thread.sleep(1000);
                                            if (!appModel.isTestStepPause() || appModel.isTestInterrupt())
                                                break;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                        if (currentCircle > 0) {
                            LogUtil.w(TAG, "---wait to next outcircle--" + (currentCircle + 1));
                            try {
                                threadWaitCanInterrupt(outCircleInterval - 1, "进入下一次大循环前暂停[" + outCircleInterval + "]秒split3");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        /*每次外循环开始时都重置第一个数据状态*/
                        isFirstDataJob = (currentCircle == 0);
                        //遍历测试任务列表(内循环) twq20120321添加判断当前是否正在测试中判断，处理异常中断后测试还在进行的情况
                        for (int i = 0; i < enableList.size() && !appModel.isTestInterrupt() && appModel.isTestJobIsRun(); i++) {
                            //测试业务默认为下行，5G业务使用
                            ShowInfo.getInstance().clearData();
                            currentTaskIndex = i;
                            //Mos测试文件记录索引清零
                            iPackTerminal.recordFileINDEX = 0;
                            if (isUmpcTest) {
                                pioneerInfo.setTemplateIndex(i + 1);
                                UmpcSwitchMethod.sendEventToController(getApplicationContext(), UMPCEventType.RealTimeData.getUMPCEvnetType(),
                                        pioneerInfo.getTemplateIndexStr(), umpcTestinfo.getController());
                            }

                            num++;
                            currentTaskModel = (TaskModel) enableList.get(i).deepClone();
                            if (ApplicationModel.getInstance().isNBTest()) {
                                if (currentTaskModel.getTypeProperty() == WalkCommonPara.TypeProperty_Ppp) {//NBPPP拨号,防止切换数据网络导致dns还原
                                    LogUtil.w(TAG, Deviceinfo.CMDNB);
                                    UtilsMethod.execRootCmdx(Deviceinfo.CMDNB);
                                }
                            }
                            if (TaskType.valueOf(currentTaskModel.getTaskType()) == TaskType.Ping) {   //增加拼无限次
                                TaskPingModel pingModel = (TaskPingModel) currentTaskModel;
                                currentTaskModel.setRepeat(pingModel.isInfinite() ? 999999999 : pingModel.getRepeat());
                            }
                            if (num > 1) {
                                //任务间显示分隔符
                                sendSplitLine();
                            }

                            if (AppVersionControl.getInstance().isTelecomInspection() && Deviceinfo.getInstance().isOppoCustom()) {
                                boolean isVolte = MobileInfoUtil.isVolte(mContext);
                                LogUtil.w(TAG, "isVolte:" + isVolte);
                                String volteOr2G = getModelNetType(mCurTaskModel);
                                LogUtil.w(TAG, "volteOr2G:" + volteOr2G);
                                try {
                                    if (volteOr2G.equals(TASK_NET_TYPE_VOLTE)) {//VOLTE
                                        if (!isVolte) {
                                            MobileInfoUtil.setVolteEnable(mContext, true);
                                            isVolte = MobileInfoUtil.isVolte(mContext);
                                            LogUtil.w(TAG, "isVolte:" + isVolte);
                                            if (!isVolte) {
                                                MobileInfoUtil.SetVolteEnable2(mContext, true);
                                            }
                                        }
                                    } else if (volteOr2G.equals(TASK_NET_TYPE_2G)) {//2G
                                        if (isVolte) {
                                            MobileInfoUtil.setVolteEnable(mContext, false);
                                            isVolte = MobileInfoUtil.isVolte(mContext);
                                            LogUtil.w(TAG, "isVolte:" + isVolte);
                                            if (isVolte) {
                                                MobileInfoUtil.SetVolteEnable2(mContext, false);
                                            }
                                        }
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    LogUtil.w(TAG, ex.getMessage() + "");
                                }
                            }

                            // 开始任务列表中一个任务的测度，启动任务执行线程
                            doneCurrentTask = false;
                            isFirstDataJobByMulti = false;
                            theFirstDataNameByMulti = "";
                            testThreadNum = 0;
                            if (WalkStruct.TaskType.valueOf(currentTaskModel.getTaskType()) == TaskType.MultiRAB && appModel.hasSamsungVoLTE()) {//并发业务测试，并且含三星VoLTE权限
                                isRabReady = true;
                                TaskRabModel rabModel = (TaskRabModel) currentTaskModel;
                                List<TaskModel> multiTasks = rabModel.getTaskModel();
                                for (TaskModel m : multiTasks) {
                                    if (isCallTest(m)) {
                                        mDatasetMgr.changeToVoLTELogMask();
                                        break;
                                    }
                                }
                            } else {
                                isRabReady = false;
                                mDatasetMgr.changeToNormalLogMask();
                            }
                            new TaskModelCircleDoThread(currentTaskModel, false, "").start();

//                              如果当前任务没有完成,且不是中断状态,则在此处循环休眠等待,
//                              任务完成是在具体测试任务回调中产生最后一个数据包表示任务完成
//                              && !appModel.isTestInterrupt()暂不判断中断，在任务执行线程中如果中断将doneCurrentTask置为真
                            while (!doneCurrentTask) {
                                Thread.sleep(1000);
                            }

                            //显示Start Finished
                            showEvent("Task Finished:" + currentTaskModel.getTaskName());
//                            currentTaskModel = null;
                        }

                        if (currentCircle + 1 < outCircleTimes) {    //当外循环大于1且当前大循环不是最后一次,显示分隔符
                            sendSplitLine();
                        }

                        if (!appModel.isGeneralMode() && outCircleDisconnetNetwork
                                && APNOperate.getInstance(mContext).checkNetWorkIsConnected()) {
                            pppRule.pppHangup(PPPRule.pppHangupJobDone, true);
                        }
                    }

                    pioneerInfo.setTestStatus(ForPioneerInfo.TESTSTATUS_TEST_STOP);
                    if (!appModel.isGeneralMode() && outCircleDisconnetNetwork
                            && APNOperate.getInstance(mContext).checkNetWorkIsConnected()) {
                        //测试结束
                        pppRule.pppHangup(PPPRule.pppHangupJobDone, true);
                        //测试结束后打开接入点
                        pppRule.setDataEnabled(isWlanTest, true);
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "---testService call job error---", e);
                    e.printStackTrace();
                }
            }
        }

        /***
         * 删除存储MOS测试时间范围信息
         */
        private void initMosTestInfo() {
            File file = new File(iPackTerminal.mosFileName);
            if (file.exists()) {
                if (!file.delete())
                    LogUtil.e(TAG, "----initMosTestInfo-----delete----false");
                try {
                    if (!file.createNewFile())
                        LogUtil.e(TAG, "----initMosTestInfo-----create----false");
                    Command.exec(iPackTerminal.mosFileName, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 测试结束后，根据testTotalInterrupt结果决定当前次任务是否需要重做
         *
         * @return 是否需要重做
         * @author twq
         */
        private boolean needDoAgain(String taskName) {

            //关于失败时的循环次数
            int testTotalInterrupt = 1;    //当前测试如果超过N次仍不计入统计时，或进入下一次测试，或者中断当前测试
            boolean result = false;// 是否需要重新测试(当次次数不算)
            // 此处控制如果业务测试不成功，不计入次数统计时的处理

            testDoneState = currModelDoneState.get(taskName);

            if (testDoneState != null) {

                LogUtil.w(TAG, "--testTotalResult:" + testDoneState.getTestResult());
                if (testDoneState.getTestResult() != null && !testDoneState.getTestResult().equals(RESULT_SUCCESS)) {
                    testDoneState.addTestTotalCount(1);
                    if (testDoneState.getTestTotalCount() < testTotalInterrupt) {
                        //失败3次以内要重做
                        result = true;
                    } else {
                        testDoneState.setTestTotalCount(0);

                        //  不计入统计次数如果超过定义次数可在此发送中断测试或进入下一次
                        /*if (appModel.isTesting() || isUmpcTest) { // 如果当前为自动测试或UMPC测试则进入下一次，否则中断
                            // testTotalCount = 0;
							UmpcSwitchMethod.sendEventToUmpc(
									getApplicationContext(),
									UMPCEventType.Alarm.getUMPCEvnetType(),
									getString(R.string.main_pppfaild_tointerrupt),
									true);
						} else {
							// twq20120419业务测试时如果出现三次失败进入下一次，此处应有一提示是否中断窗口，后加
							Intent dialIntent = new Intent(mContext,ServiceDialog.class);
							dialIntent.putExtra(ServiceDialog.DIALOG_ID,ServiceDialog.PPPFaildToInterrupt);
							dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(dialIntent);
						}*/
                        LogUtil.w(TAG, "--here to stop test by testTotalCount:"
                                + testDoneState.getTestTotalCount() + "--isAutoTest:"
                                + appModel.isTesting());
                    }
                } else {
                    testDoneState.setTestTotalCount(0);
                }

                currModelDoneState.put(taskName, testDoneState);

            }

            LogUtil.w(TAG, "needDoAgain,taskName=" + taskName + ",result=" + result);
            return result;
        }

        private void waitNextTimeSplitFile(TaskModel model, int j) {
            try {
                LogUtil.w(TAG, "--wait for next getInterVal:" + model.getTaskName() + "--InterVal:" + model.getInterVal());
                doLockNet(model.getNettype());    //锁网动作，按任务中设置的类型

                //上一次测试结束，进入下一次时，有分割文件的动作的，分割之前先等待5秒 (i!=0 || j!=0) &&
                boolean needSplitFile = needToSplitFile();
                if (needSplitFile) {
                    threadWaitCanInterrupt(5, "split1" + model.getTaskName());
                }

                if (!appModel.isTestInterrupt() && needSplitFile) {
                    doSplitRcuFile();
                }

                if (j == 0) {    //i==0 && 每次任务开始都默认等10秒
                    //当前为任务列表的第一个任务，第一次外循环不等，第二次外循环起由于等了外循环间隔，所以此处第一个任务不做等操作
                    if (currentTaskIndex != 0) {
                        int taskInter = /*model.getInterTaskInterval()*/mDifferentTaskInterval - 1 - (needSplitFile ? 5 : 0);
                        threadWaitCanInterrupt(taskInter < 0 ? 0 : taskInter, "split2" + model.getTaskName());
                    } else {
//                        threadWaitCanInterrupt(5, "split5" + model.getTaskName());
                    }
                } else {
                    int weetTimes = model.getInterVal() - 1 - (needSplitFile ? 5 : 0);
                    if (weetTimes < 0) {
                        LogUtil.w(TAG, "--wait 5000ms the interVal is :" + weetTimes);
                        UtilsMethod.ThreadSleep(5000);
                    } else {
                        threadWaitCanInterrupt(weetTimes, "split3" + model.getTaskName());
                    }
                }
                LogUtil.w(TAG, "to do next getInterVal,J:" + j);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 当前是否需要进行数据分割判断
         *
         * @return 是否需要分割文件
         */
        private boolean needToSplitFile() {
            if (appModel.isHuaWeiTest())
                return false;
            if (rcuFileSizeLimit > 0 || rcuFileTimeLimit > 0) {
                if (rcuFileSizeLimit > 0) {
                    /*String ddibFilePath = mDatasetMgr.getDecodedIndexFileName();
                    File rcuFile = new File(ddibFilePath);*/
                    if (DataSetLib.currentFileLength >= rcuFileSizeLimit * 1000 * 1000) {
                        LogUtil.w(TAG, "--split file fileSize:" + DataSetLib.currentFileLength);
                        return true;
                    }

                    return false;
                }
                if (rcuFilelastTime <= 0) {
                    rcuFilelastTime = System.currentTimeMillis();
                    return false;
                } else if ((System.currentTimeMillis() - rcuFilelastTime) / 1000 > rcuFileTimeLimit) {
                    LogUtil.w(TAG, "--split file lastTime:" + rcuFilelastTime + "--currT:" + System.currentTimeMillis());
                    rcuFilelastTime = System.currentTimeMillis();
                    return true;
                }

                return false;
            }

            return false;
        }

        /**
         * 尝试切换成数据业务的APN
         */
        private void switchToDataApn() {
            APNOperate apnOperate = APNOperate.getInstance(mContext);
            ConfigAPN configApn = ConfigAPN.getInstance();
            //如果系统设置的数据业务接入点的APN不存在时,通知用户
            if (configApn.getDataAPN() == null || !apnOperate.anpIsExists(configApn.getDataAPN())) {
                if (configApn.getDataAPN() == null) {
                    LogUtil.w(TAG, "apn is null");
                } else {
                    LogUtil.w(TAG, " APN " + configApn.getDataAPN() + " is not existed");
                }
                //APN告警
                //showNotification( getString(R.string.Sys_Intent_APN_data_Null), SysSettingAPN.class );
            } else {
                //选择数据业务的APN
                LogUtil.w(TAG, "switch apn to be " + configApn.getDataAPN());
                apnOperate.setSelectApn(configApn.getDataAPN());
            }
        }

        /**
         * 脱网情况下等待恢复网络动作
         */
        private void waitForSignal(TaskType taskType) {
            //先判断是否脱网(电信规范中为了统计脱网下拨打的次数，参考文档<脱 网-前台脱 网事件算法文档>)
            int waitServiceTime = 0;
            while (!MyPhoneState.getInstance().isServiceAlive() && !appModel.isTestInterrupt()) {
                LogUtil.w(TAG, "---waitServiceTime:" + waitServiceTime + "--mode:" + (waitServiceTime % 15));
                //先检测15秒直到有信号
                if (waitServiceTime % 15 == 0) {
                    showEvent("Out Of Service");
                    if (waitServiceTime != 0) {
                        writeRcuEvent(RcuEventCommand.OUT_OF_SERVICE, RcuEventCommand.getTaskType(taskType), System.currentTimeMillis());
                    }
                }
                try {
                    Thread.sleep(1000);
                    if (MyPhoneState.getInstance().isServiceAlive()) {
                        //如果当前恢复信号，则再等待3秒，再根据外循环决定是否退出流程
                        sendSplitLine();
                        Thread.sleep(1000 * 3);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                waitServiceTime++;
            }
        }

        /**
         * 等待Mobile To Land MOS测试与服务器的时间同步
         */
        private void waitForSyncTime() {

            MyPhoneState.setSync(mContext, false);

            //开始通知FleetService同步
            sendBroadcast(new Intent(ServerMessage.ACTION_FLEET_SYNC));

            showEvent("Synchronize Time");

            //等待1分钟超时
            for (int i = 0; i < 45 && !MyPhoneState.hasSync(mContext); i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (MyPhoneState.hasSync(mContext)) {
                showEvent("Synchronize Time Success");
//                return true;
            }
            showEvent("Synchronize Time Failure");
//            return false;
        }

        /**
         * 向服务器发送开始测试消息
         */
        private void sendStartCommand() {
            ServerManager server = ServerManager.getInstance(mContext);
            if (server.hasFleetEvent(mContext) && server.hasSetFleetServer()) {
                boolean pppConnected = setDataNetwork();
                if (pppConnected) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    showEvent(getString(R.string.test_event_start));
                    ServerManager.getInstance(mContext).addStartTestEvent(mContext);
                    sendSplitLine();
                }
            }
        }

        /**
         * 向服务器发送停止测试消息
         */
        private void sendStopCommand() {
            ServerManager server = ServerManager.getInstance(mContext);
            if (server.hasFleetEvent(mContext) && server.hasSetFleetServer()) {
                sendSplitLine();
                boolean pppConnected = setDataNetwork();
                if (pppConnected) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    showEvent(getString(R.string.test_event_end));
                    ServerManager.getInstance(mContext).addStopTestEvent(mContext);
                    sendSplitLine();
                }
            }
        }

        /**
         * 尝试切换成数据业务的APN
         */
        private boolean setDataNetwork() {
            int result = PPPRule.PPP_RESULT_SUCCESS;
            if (!ApplicationModel.getInstance().isGeneralMode()) {
                result = pppRule.pppDial(false, WalkStruct.TaskType.FleetConnect, WalkCommonPara.TypeProperty_Net, -9999);
            }
            return result == PPPRule.PPP_RESULT_SUCCESS || result == PPPRule.pppNullSuccess;
        }

    }

    /**
     * 写Log标签，用于W-One Pro联通版本
     */
    private void writeLogLable() {
        Loglabel label = Loglabel.newInstance();
        label.product_name = getString(R.string.app_name); // 产品名称
        label.prodcut_version = UtilsMethod.getCurrentVersionName(TestService.this); // 程序版本名
        label.fileformat_version = "1.0"; // 日志格式的版本
        label.device_name = android.os.Build.MODEL; // 设备名称
        label.number_of_supported_systems = "2"; // 支持的网络数目
        label.supported_systems = "1,5"; // 支持的网络编号
        label.device_id = MyPhoneState.getInstance().getDeviceId(TestService.this); // 系统ID，即IMEI
        label.service_type = "normal"; // 业务类型
//		label.scene = "indoor"; // 应用场景
        // 文档中接下来的部分标签的值在WorkOrderDetailActivity中获得
        label.terminal_os = "Android" + MyPhoneState.getInstance().getAndroidVersion(); // 系统版本
        label.stop_time = UtilsMethod.sdFormatss.format(new Date()); // 结束时间
        label.stop_time_millseconds = UtilsMethod.sdFormat.format(new Date()); // 结束时间，带毫秒

        // 调用jni接口函数创建Log文件
//		String logPath = Environment.getExternalStorageDirectory() + "/Walktour/data/taskdcf/";

        String logName = newRcuFileName.substring(0, newRcuFileName.lastIndexOf("."));
        String fullName = rcuFilePath + logName + ".txt";
        LogUtil.i(TAG, "log file name:" + fullName);
        UnicomInterface.createLoglabelFile(fullName, label.format());
    }

    /**
     * 处理RCU文件分割
     * 发送关闭当前RCU文件的命令
     * 将当前的RCU文件信息写入数据库中
     * 重新创建RCU文件
     * 数据分割时目前会把当前的统计写入相应的RCU文件中重新创建时会把统计清空
     */
    private void doSplitRcuFile() {
        LogUtil.w(TAG, "--todo split rcu file--isInterrupt:" + appModel.isTestInterrupt());
        sendCloseRcuFileAddToDB();

        if (!appModel.isTestInterrupt()) {  //当前非测试中断状态下才开始创建下一个RCU文件
            sendCreateRcuFileCommand();
        }
    }

    /**
     * 在联通招标中根据网络类型获得文件名的类型标识
     *
     * @return 类型标识
     */
    private String getUmpcDeviceName() {

        /*String name = MobileUtil.getSIM_MCCMNC(getApplicationContext());
        try {
            int iMmcMnc = Integer.parseInt(name);
            //如果当前MNC非国内的,直接文件名中加MNC信息
            switch (iMmcMnc) {
                case 46000:
                case 46002:
                case 46007:
                case 46004:
                case 46020:
                    name = getString(R.string.str_chinamobile);
                    break;
                case 46001:
                case 46006:
                case 46009:
                    name = getString(R.string.str_chinaunicom);
                    break;
                case 46003:
                case 46005:
                case 46011:
                    name = getString(R.string.str_chinatelecom);
                    break;
            }
        } catch (Exception e) {
            LogUtil.w(TAG, "getUmpcDeviceName", e);
        }

        return name;*/
        return MyPhoneState.getInstance().getNetworkOperateName(WalktourApplication.getAppContext());
    }

    /**
     * 将当前记录的RCU文件信息写入数据库中
     */
    private void insertFileInfoToDB() {
        try {
            if (createRcuFilePath != null && !createRcuFilePath.equals("")) {
                //构建最终要重命名的文件名,存于类全局的newRcuFileName变量中
                newRcuFileName = newRcuFileName.replaceAll(appModel.isScannerTest() ? DatasetManager.Port3Name : DatasetManager.Port2Name, "");
                String initName = newRcuFileName.replace(".org", "").replace(".rcu", "").replace(".org.rcu", "");

                long stopRcuTime = System.currentTimeMillis();
                mTestRecord.setTestRecordMsg(TestRecordEnum.time_end.name(), stopRcuTime);
                mTestRecord.setTestRecordMsg(TestRecordEnum.go_or_nogo.name(), TotalDataByGSM.getInstance().getGoOrNogoReport(mContext) ? 1 : 0);

                mTestRecord.setRecordTestInfoMsg(appModel.getExtendInfo());
                String ddibFilePath = mTestRecord.moveRelativeFiles(isDontSaveFile, rcuFilePath, initName);

                if (hasMOCMOSTest) {    //如果当前有语音且开启算分业务
                    mTestRecord.setRecordDetailMsg(FileType.MOSZIP.name(), RecordDetailEnum.file_type.name(), FileType.MOSZIP.getFileTypeId());
                    mTestRecord.setRecordDetailMsg(FileType.MOSZIP.name(), RecordDetailEnum.file_path.name(), BluetoothMOSFactory.getStorgePath(mContext));
                    mTestRecord.setRecordDetailMsg(FileType.MOSZIP.name(), RecordDetailEnum.file_name.name(), newRcuFileName.substring(0, newRcuFileName.lastIndexOf(".")));
                    mTestRecord.setRecordDetailMsg(FileType.MOSZIP.name(), RecordDetailEnum.file_guid.name(), UtilsMethod.getUUID());
                }

                if (umpcTestinfo != null ?
                        umpcTestinfo.getGenFileTypes() != null && umpcTestinfo.getGenFileTypes()[umpcTestinfo.GEN_FILE_ECTI] == '1'
                        : ConfigRoutine.getInstance().isGenECTI(mContext)) {
                    mTestRecord.setRecordDetailMsg(FileType.ECTI.getFileTypeName(), RecordDetailEnum.file_type.name(), FileType.ECTI.getFileTypeId());
                    mTestRecord.setRecordDetailMsg(FileType.ECTI.getFileTypeName(), RecordDetailEnum.file_path.name(), ddibFilePath.substring(0, ddibFilePath.lastIndexOf("/") + 1));
                    mTestRecord.setRecordDetailMsg(FileType.ECTI.getFileTypeName(), RecordDetailEnum.file_name.name(), DataSetLib.currentFileName);
                    mTestRecord.setRecordDetailMsg(FileType.ECTI.getFileTypeName(), RecordDetailEnum.file_guid.name(), UtilsMethod.getUUID());
                    if (null != DataSetLib.currentFileName && DataSetLib.currentFileName.length() > 0) {
                        File fx = new File(ddibFilePath.substring(0, ddibFilePath.lastIndexOf("/") + 1) + DataSetLib.currentFileName);
                        if (fx.exists()) {
                            mTestRecord.setRecordDetailMsg(FileType.ECTI.getFileTypeName(), RecordDetailEnum.file_size.name(), fx.length());
                        }
                    }
                }

                if (!appModel.isGeneralMode())
                    TotalInterface.getInstance(mContext).addStatisticDDIBFile(ddibFilePath);


                if (umpcTestinfo != null) {
                    umpcTestinfo.filesRename(newRcuFileName, MyPhoneState.getInstance().getDeviceId(TestService.this), mTestRecord.getTestRecord());
                }

                if (isDontSaveFile) {
                    return;
                }

                int mainId = (int) DataManagerFileList.getInstance(getApplicationContext()).insertFile(mTestRecord.getTestRecord());

                if (isUmpcTest && AppVersionControl.getInstance().isTelecomInspection()) {
                    if (null != mTestRecord && null != mTestRecord.getTestRecord()) {
                        ArrayList<RecordDetail> recordDetails = mTestRecord.getTestRecord().getRecordDetails();
                        if (null != recordDetails) {
                            for (RecordDetail detail : recordDetails) {
                                LogUtil.i(TAG, "file_name:" + detail.file_name + ",fileSize:" + detail.file_size);
                                String fileDetail = detail.file_name + ","
                                        + detail.file_size + ","
                                        + System.currentTimeMillis() + ","
                                        + mAppVersionName + ","
                                        + "\r\n";
                                EventBus.getDefault().post(new TestFileDataEvent(fileDetail));
                            }
                        }
                    }
                }
                VoiceAnalyse.setTotalFileId(String.valueOf(mainId));
//                if(appModel.isSingleStationTest()) {
                //生成文件广播
                Intent i = new Intent(WalkMessage.NOTIFY_TEST_FILE_CREATED);
                i.putExtra("record_id", this.mTestRecord.getTestRecord().record_id);
                this.sendBroadcast(i);

                //通知 Aidl 管理者，文件生成
                Bundle bundle = new Bundle();
                bundle.putInt(AidlTestControllor.EXTRA_FILE_COUNT, mTestRecord.getTestRecord().getRecordDetails().size());
                EventBus.getDefault().post(new AidlEvnet(AidlEvnet.NOTIFY_FILE_CREATE, bundle));
//                }
                //写入统计数据
                LogUtil.w(TAG, "---insert to DB Id:" + mainId + "--" + createRcuFilePath);
                TotalDataByGSM.getInstance().InsertTotalDetailToDB(getApplicationContext(), mainId, appModel.isIndoorTest() ? 2 : 1);

                //发送统计文件
                if (isUmpcTest) {
                    if (umpcTestinfo != null) {
                        if (umpcTestinfo.isAutoupload()) {
                            Intent intent = new Intent(WalkMessage.ACTION_SEND_STATIC2PAD);
                            intent.putExtra("file", initName);
                            sendBroadcast(intent);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.w(TAG, "insertFileInfoToDB", e);
        }
    }

    private String getUmpcTagDeviceName(String localName) {
        if (localName != null && !localName.equals("")) {
            return localName;
        } else if (routineSet.getDeviceTag() != null && !routineSet.getDeviceTag().equals("")) {
            return routineSet.getDeviceTag();
        } else {
            return UtilsMethod.sdfyMdhms.format(System.currentTimeMillis());
        }
    }

    /**
     * 获得需要重命名的名字（电信巡检版本）
     */
    private void buildNewFileName(String volteOr2G) {
        //如果当前为UMPC测试，则根据UMPC测试时传过来的名称替换现在的RCU文件名
        newRcuFileName = "";
        if (!appModel.isGeneralMode() && isUmpcTest) {
            if (umpcTestinfo != null /*&& !umpcTestinfo.getTestlocale().equals("")*/) {
                LogUtil.d(TAG, "------Testgroupinfo:" + umpcTestinfo.getTestgroupinfo() + "------");
                String testgroupinfo = getUmpcTagDeviceName(umpcTestinfo.getTestgroupinfo());
                newRcuFileName = (testgroupinfo.equals("") ? DateUtil.formatDate("yyyyMMdd_HHmmss", new Date()) : testgroupinfo) + "-"
                        + (TextUtils.isEmpty(volteOr2G) ? "" : volteOr2G + "-")
                        + getUmpcDeviceName() + "-"
                        + (doneJobName.equals("") ? "" : doneJobName + "-")
                        + myPhoneState.getMyDeviceId(mContext)
                        + genSplitFileIndex()
                        + FileType.ORGRCU.getExtendName();

                mTestRecord.setTestRecordMsg(TestRecordEnum.port_id.name(), String.valueOf(umpcTestinfo.getAtuPort()));
                mTestRecord.setTestRecordMsg(TestRecordEnum.group_info.name(), umpcTestinfo.getTestgroupinfo());
                mTestRecord.setTestRecordMsg(TestRecordEnum.test_index.name(), currentFileNum);
            } else {
                newRcuFileName = testTagStr + addDonenNameToRcuName(rcuFileName, doneJobName);
            }

        } else if (!telecomOrderStr.equals("")) {
            newRcuFileName = String.format("%s%s.%s", telecomOrderStr, genSplitFileIndex(), FileType.ORGRCU.getFileTypeName());
        } else if (routineSet.isGenCU(mContext) && ApplicationModel.getInstance().showInfoTypeCu()) {

            newRcuFileName = rcuFileName;
        } else {
            //根据当前已测试的任务修改最终的文件名，并将文件名写入数据库中
            String fleetName = "";
            if (isAutoTest) {
                fleetName = TestPlan.getInstance().getFleetRcuName();
            }

            if (rcuFileSizeLimit > 0 || rcuFileTimeLimit > 0 || needToSplieFile) {
                if (splitFileRcuName.equals("")) {
                    splitFileRcuName = testTagStr + addDonenNameToRcuName(rcuFileName, doneJobName);
                    splitFileRcuName = splitFileRcuName.substring(0, splitFileRcuName.length() - 4);
                }
                newRcuFileName = String.format("%s(%s).%s", splitFileRcuName, (rcuFileLimitNum++), FileType.ORGRCU.getFileTypeName());
            } else if (appModel.getRuningScene() == SceneType.SingleSite) {
                newRcuFileName = testTagStr + addDonenNameToRcuName(rcuFileName, this.currentTaskModel.getTaskType());
            } else {
                newRcuFileName = testTagStr + addDonenNameToRcuName(rcuFileName, doneJobName);
            }

            if (!fleetName.equals("")) {
                newRcuFileName = String.format("%s-%s", fleetName, newRcuFileName);
            }

            newRcuFileName = newRcuFileName.replaceAll(DatasetManager.Port2Name, "");

            if (appModel.isWoneTest()) {
                newRcuFileName = testTagStr + Loglabel.newInstance().work_order_id + "_"
                        + newRcuFileName.substring(newRcuFileName.indexOf("-") + 1, newRcuFileName.length());
            } else if (appModel.isHuaWeiTest()) {
                newRcuFileName = this.formatHuaweiFileName(newRcuFileName);
            }
        }

        mTestRecord.setTestRecordMsg(TestRecordEnum.task_no.name(), task_no);
        mTestRecord.setTestRecordMsg(TestRecordEnum.file_split_id.name(), (rcuFileLimitNum - 1));
        mTestRecord.setTestRecordMsg(TestRecordEnum.node_id.name(), testFloor);

        if (appModel.getRuningScene() == SceneType.HighSpeedRail) {
            mTestRecord.setTestRecordMsg(TestRecordEnum.node_id.name(), SysBuildingManager.getInstance(this.getApplicationContext()).getNodeId(highSpeedRail, "0"));
        } else if (appModel.getRuningScene() == SceneType.Metro) {
            mTestRecord.setTestRecordMsg(TestRecordEnum.node_id.name(), SysBuildingManager.getInstance(this.getApplicationContext()).getNodeId(metroRoute, "0"));
        } else if (appModel.getRuningScene() == SceneType.SingleSite) {
            mTestRecord.setTestRecordMsg(TestRecordEnum.node_id.name(), SysBuildingManager.getInstance(this.getApplicationContext()).getNodeId(mSingleStationName, "0"));
        }
    }

    /**
     * 获得需要重命名的名字
     */
    private void buildNewFileName() {
        //如果当前为UMPC测试，则根据UMPC测试时传过来的名称替换现在的RCU文件名
        newRcuFileName = "";

        if (!appModel.isGeneralMode() && isUmpcTest) {
            if (umpcTestinfo != null /*&& !umpcTestinfo.getTestlocale().equals("")*/) {

                newRcuFileName = getUmpcTagDeviceName(umpcTestinfo.getTestgroupinfo())        //Ipack下发的文件名串
                        + "@"                                                                    //以@分隔之前的内容紧跟着自增序号000001..
                        + (currentFileNum > -1 ? fileNameFormat.format(currentFileNum) + "_" : "")    //如果加密序号大于-1则添加加密序号
                        + getUmpcDeviceName() + "_"                                                //加上“移动”“联通”“电信”分组;如果非这些运行商显示MMC号
                        + (doneJobName.equals("") ? "" : doneJobName + "_")                        //业务名称(最多三项)
                        + myPhoneState.getMyDeviceId(mContext)                                    //IMEI
                        + genSplitFileIndex()
                        + FileType.ORGRCU.getExtendName();

                mTestRecord.setTestRecordMsg(TestRecordEnum.port_id.name(), String.valueOf(umpcTestinfo.getAtuPort()));
                mTestRecord.setTestRecordMsg(TestRecordEnum.group_info.name(), umpcTestinfo.getTestgroupinfo());
                mTestRecord.setTestRecordMsg(TestRecordEnum.test_index.name(), currentFileNum);
            } else {
                newRcuFileName = testTagStr + addDonenNameToRcuName(rcuFileName, doneJobName);
            }

        } else if (!telecomOrderStr.equals("")) {
            newRcuFileName = String.format("%s%s.%s", telecomOrderStr, genSplitFileIndex(), FileType.ORGRCU.getFileTypeName());
        } else if (routineSet.isGenCU(mContext) && ApplicationModel.getInstance().showInfoTypeCu()) {

            newRcuFileName = rcuFileName;
         } else {
            //根据当前已测试的任务修改最终的文件名，并将文件名写入数据库中
            String fleetName = "";
            if (isAutoTest) {
                fleetName = TestPlan.getInstance().getFleetRcuName();
            }

            if (rcuFileSizeLimit > 0 || rcuFileTimeLimit > 0 || needToSplieFile) {
                if (splitFileRcuName.equals("")) {
                    splitFileRcuName = testTagStr + addDonenNameToRcuName(rcuFileName, doneJobName);
                    splitFileRcuName = splitFileRcuName.substring(0, splitFileRcuName.length() - 4);
                }
                newRcuFileName = String.format("%s(%s).%s", splitFileRcuName, (rcuFileLimitNum++), FileType.ORGRCU.getFileTypeName());
            } else if (appModel.getRuningScene() == SceneType.SingleSite) {
                newRcuFileName = testTagStr + addDonenNameToRcuName(rcuFileName, this.currentTaskModel.getTaskType());
            } else {
                newRcuFileName = testTagStr + addDonenNameToRcuName(rcuFileName, doneJobName);
            }

            if (!fleetName.equals("")) {
                newRcuFileName = String.format("%s-%s", fleetName, newRcuFileName);
            }

            newRcuFileName = newRcuFileName.replaceAll(DatasetManager.Port2Name, "");

            if (appModel.isWoneTest()) {
                newRcuFileName = testTagStr + Loglabel.newInstance().work_order_id + "_"
                        + newRcuFileName.substring(newRcuFileName.indexOf("-") + 1, newRcuFileName.length());
            } else if (appModel.isHuaWeiTest()) {
                newRcuFileName = this.formatHuaweiFileName(newRcuFileName);
            }
        }
        mTestRecord.setTestRecordMsg(TestRecordEnum.task_no.name(), task_no);
        mTestRecord.setTestRecordMsg(TestRecordEnum.file_split_id.name(), (rcuFileLimitNum - 1));
        mTestRecord.setTestRecordMsg(TestRecordEnum.node_id.name(), testFloor);

        if (appModel.getRuningScene() == SceneType.HighSpeedRail) {
            mTestRecord.setTestRecordMsg(TestRecordEnum.node_id.name(), SysBuildingManager.getInstance(this.getApplicationContext()).getNodeId(highSpeedRail, "0"));
        } else if (appModel.getRuningScene() == SceneType.Metro) {
            mTestRecord.setTestRecordMsg(TestRecordEnum.node_id.name(), SysBuildingManager.getInstance(this.getApplicationContext()).getNodeId(metroRoute, "0"));
        } else if (appModel.getRuningScene() == SceneType.SingleSite) {
            mTestRecord.setTestRecordMsg(TestRecordEnum.node_id.name(), SysBuildingManager.getInstance(this.getApplicationContext()).getNodeId(mSingleStationName, "0"));
        }
    }

    /**
     * 获取当前测试任务的网络类型
     *
     * @param mCurTaskModel
     * @return
     */
    private String getModelNetType(TaskModel mCurTaskModel) {
        String volteOr2G = "";
        if (mCurTaskModel instanceof TaskInitiativeCallModel) {
            String mosAlgorithm = ((TaskInitiativeCallModel) mCurTaskModel).getMocTestConfig().getMosAlgorithm();
            volteOr2G = MOCTestConfig.MOSAlgorithm_POLQA.equals(mosAlgorithm) ? TASK_NET_TYPE_VOLTE : TASK_NET_TYPE_2G;
        } else if (mCurTaskModel instanceof TaskPassivityCallModel) {
            String mosAlgorithm = ((TaskPassivityCallModel) mCurTaskModel).getMtcTestConfig().getMosAlgorithm();
            volteOr2G = MTCTestConfig.MOSALGORITHM_POLQA.equals(mosAlgorithm) ? TASK_NET_TYPE_VOLTE : TASK_NET_TYPE_2G;
        }
        return volteOr2G;
    }

    /**
     * 格式化服务器端需要的文件名 华为工单专用
     *
     * @param fileName 文件全路径
     * @return 文件名
     */
    private String formatHuaweiFileName(String fileName) {
        System.out.println(TAG + " fileName:" + fileName);
        if (fileName.indexOf("_Port") > 0) {
            return fileName;
        }
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        String prefix = "";
        if (fileName.indexOf("-IN") > 0) {
            prefix = fileName.substring(0, fileName.indexOf("-IN"));
        } else if (fileName.indexOf("-OUT") > 0) {
            prefix = fileName.substring(0, fileName.indexOf("-OUT"));
        }
        String data = "";
        int start;
        if (fileName.contains("-IN")) {
            start = fileName.indexOf("-IN") + 3;
            data = fileName.substring(start, start + 15);
        } else if (fileName.contains("-OUT")) {
            start = fileName.indexOf("-OUT") + 4;
            data = fileName.substring(start, start + 15);
        }
        StringBuilder name = new StringBuilder();
        if (prefix.length() > 0)
            name.append(prefix).append("_");
        name.append(data.substring(0, 4)).append("-");
        name.append(data.substring(4, 6)).append("-");
        name.append(data.substring(6, 8)).append("-");
        name.append(data.substring(9, 11)).append("-");
        name.append(data.substring(11, 13)).append("-");
        name.append(data.substring(13, 15));
        name.append(suffix);
        System.out.println(TAG + " formatFileName:" + name.toString());
        return name.toString();
    }

    /**
     * 获得当前是否需要添加文件分割序号
     *
     * @return 是否需要添加文件分割序号
     */
    private String genSplitFileIndex() {
        return (rcuFileSizeLimit > 0 || rcuFileTimeLimit > 0 ? "(" + (rcuFileLimitNum++) + ")" : "");
    }

    /**
     * 当前测试是否需要加密
     */
    private boolean isEncrypt() {
        return (umpcTestinfo != null && !umpcTestinfo.getEncryptCode().equals("")
                || entryptioneKey != null && !entryptioneKey.equals(""));
    }

    /**
     * 发送创建RCU文件命令
     */
    private void sendCreateRcuFileCommand() {
        String volteOr2G = "";
        for (TaskModel tm : allEnableList) {
            if (tm instanceof TaskInitiativeCallModel) {
                volteOr2G = MOCTestConfig.MOSAlgorithm_POLQA.equals(((TaskInitiativeCallModel) tm).getMocTestConfig().getMosAlgorithm()) ? TASK_NET_TYPE_VOLTE : TASK_NET_TYPE_2G;
            } else if (tm instanceof TaskPassivityCallModel) {
                volteOr2G = MTCTestConfig.MOSALGORITHM_POLQA.equals(((TaskPassivityCallModel) tm).getMtcTestConfig().getMosAlgorithm()) ? TASK_NET_TYPE_VOLTE : TASK_NET_TYPE_2G;
            }
        }
        if (appModel.isPioneer()) {
            LogUtil.w(TAG, "sendCreateRcuFileCommand()===file is created.");
            mTestRecord = DataSetFileUtil.getInstance().getmTestRecord();
            return;
        }
        LogUtil.w(TAG, "---create rcu file size:" + rcuFileSizeLimit + "--time:" + rcuFileTimeLimit + "--num:" + rcuFileLimitNum);
        testPlanWList.clear();
        appModel.setRcuFileCreated(false);

        mTestRecord = new BuildTestRecord();
        mTestRecord.setTestRecordMsg(TestRecordEnum.record_id.name(), UtilsMethod.getUUID());
        mTestRecord.setTestRecordMsg(TestRecordEnum.type_scene.name(), fromScene);
        int testTypeId = -1;
        if (isUmpcTest) {
            if (isIndoorTest) {
                testTypeId = TestType.CQT.getTestTypeId();
            } else {
                testTypeId = TestType.DT.getTestTypeId();
            }
        } else {
            testTypeId = (GpsInfo.getInstance().isJobTestGpsOpen() || GpsInfo.getInstance().isAutoTestGpsOpen()) ? TestType.DT.getTestTypeId() : TestType.CQT.getTestTypeId();
        }
        mTestRecord.setTestRecordMsg(TestRecordEnum.test_type.name(), testTypeId);
        long startRcuTime = System.currentTimeMillis();
        mTestRecord.setTestRecordMsg(TestRecordEnum.time_create.name(), startRcuTime);
        //2018/10/31 Yi.lin FixBug:ATU/BTU上传数据没有端口号
        ServerManager serverManager = ServerManager.getInstance(mContext);
        if (serverManager.getUploadServer() == ServerManager.SERVER_ATU || serverManager.getUploadServer() == ServerManager.SERVER_BTU) {
            int dtLogMoudleNum = serverManager.getDTLogMoudleNum();
            LogUtil.i(TAG, "------dtLogMoudleNum: " + dtLogMoudleNum + "----------");
            mTestRecord.setTestRecordMsg(TestRecordEnum.port_id.name(), String.valueOf(dtLogMoudleNum));
        }
        String nameTag;
        //当前是否开启测试的GPS，如果开启，则认为是室内测试加OUT标志，否则默认为室内测试。如果是地铁高铁测试，虽然用不上GPS，也是out
        if (GpsInfo.getInstance().isJobTestGpsOpen() || GpsInfo.getInstance().isAutoTestGpsOpen() ||
                appModel.getSelectScene() == SceneType.HighSpeedRail || appModel.getSelectScene() == SceneType.Metro) {
            nameTag = getString(R.string.path_outindoortip);
        } else {
            //默认为室内测试时，看当前是否有室内转项的权限，如果有室内测试为室内专项的室内测试，不改头标志名
            nameTag = appModel.getAppList().contains(WalkStruct.AppType.IndoorTest) && appModel.isIndoorTest() ?
                    (appModel.getFloorModel().getBuildingName() + "_" + appModel.getFloorModel().getName())
                    : getString(R.string.path_indoortip);
        }
        rcuFilePath = routineSet.getStorgePathTask();
        times = UtilsMethod.getSimpleDateFormat7(startRcuTime);
        if (routineSet.isGenCU(mContext)
                && ApplicationModel.getInstance().showInfoTypeCu()) {

            rcuFileName = String.format(DataTableStruct.CU_FILE_NAME_FORMAT,
                    GpsInfo.getInstance().isJobTestGpsOpen() ? "DT" : "CQT",        //测试方式:DT/CQT
                    appModel.getExtendInfoStr(RecordInfoKey.city.name()),            //城市名称
                    appModel.getExtendInfoStr(RecordInfoKey.cu_Scope.name()),        //测试范围
                    (AppVersionControl.getInstance().isPerceptionTest() ? mContext.getString(R.string.device_Factory_perception) : mContext.getString(R.string.device_Factory)),                            //仪表厂家
                    appModel.getExtendInfoStr(RecordInfoKey.cu_Company.name()),    //测试厂家
                    appModel.getExtendInfoStr(RecordInfoKey.tester.name()),            //测试人员
                    UtilsMethod.sdfyMdhms.format(System.currentTimeMillis()),        //测试时间
                    appModel.getExtendInfoStr(RecordInfoKey.extendsInfo.name()),    //扩展信息
                    DatasetManager.PORT_2,
                    FileType.ORGRCU.getFileTypeName()
            );
        } else {
            rcuFileName = String.format("%s%s%s." + FileType.ORGRCU.getFileTypeName(), nameTag, times,
                    appModel.getExtendInfoStr(RecordInfoKey.tester.name()));
        }


        createRcuFilePath = String.format("%s%s", rcuFilePath, rcuFileName);
        if (AppVersionControl.getInstance().isTelecomInspection() || AppVersionControl.getInstance().isUnicomGroup()) {
            buildNewFileName(volteOr2G);
        } else {
            buildNewFileName();
        }
        createRcuFilePath = String.format("%s%s", rcuFilePath, newRcuFileName);
        mTestRecord.setTestRecordMsg(TestRecordEnum.file_name.name(), newRcuFileName.substring(0, newRcuFileName.lastIndexOf(".")).replaceAll(".org","").replaceAll(".org.rcu","").replaceAll(".rcu",""));
        mDatasetMgr.configDecodeProperty(false);   //配置采样配置周期  false为不是OTS采集

        boolean genDtLog = routineSet.isGenDTLog(mContext)                                //当前打开DTLog开关
                || (umpcTestinfo != null && umpcTestinfo.getAtuPort() != -1)    //当前为小背包测试且ATU端口设置的有效值
                || ServerManager.getInstance(mContext).getDTLogCVersion() > 0;    //当前测试计划为ATU关联测试

        String deviceId =  /*genDtLog && !(umpcTestinfo != null && umpcTestinfo.getAtuPort() != -1) ? sManager.getDTLogBoxId() :*/
                ("{" + myPhoneState.getGUID(mContext, isEncrypt()) + "}");

        mDatasetMgr.createFile(createRcuFilePath, isDontSaveFile, genDtLog, deviceId,
                sManager.getDTLogCVersion(), umpcTestinfo, mTestRecord, currentFileNum, entryptioneKey, hasCall);
        createRcuFilePath = createRcuFilePath.substring(0, createRcuFilePath.lastIndexOf("."))
                + DatasetManager.Port2Name + FileType.ORGRCU.getExtendName();
        rcuFileName = createRcuFilePath.substring(createRcuFilePath.lastIndexOf("/") + 1);

        if (isNetsniffer) {
            String pcapFileName = createRcuFilePath.replace(FileType.ORGRCU.getExtendName(), FileType.PCAP.getExtendName());
            NetSnifferServiceUtil.getInstance().bindService(this, pcapFileName);

            mTestRecord.setRecordDetailMsg(FileType.PCAP.name(), RecordDetailEnum.file_type.name(), FileType.PCAP.getFileTypeId());
            mTestRecord.setRecordDetailMsg(FileType.PCAP.name(), RecordDetailEnum.file_path.name(), ConfigRoutine.getInstance().getStorgePathTask());
            mTestRecord.setRecordDetailMsg(FileType.PCAP.name(), RecordDetailEnum.file_name.name(), pcapFileName.substring(pcapFileName.lastIndexOf("/") + 1));
            mTestRecord.setRecordDetailMsg(FileType.PCAP.name(), RecordDetailEnum.file_guid.name(), UtilsMethod.getUUID());
        }

        if (appModel.getFloorModel() != null && !appModel.getFloorModel().getTestMapPath().equals("")) {
            mTestRecord.setRecordDetailMsg(FileType.FloorPlan.name(), RecordDetailEnum.file_type.name(), FileType.FloorPlan.getFileTypeId());
            mTestRecord.setRecordDetailMsg(FileType.FloorPlan.name(), RecordDetailEnum.file_path.name(),
                    appModel.getFloorModel().getTestMapPath().substring(0, appModel.getFloorModel().getTestMapPath().lastIndexOf("/") + 1));
            mTestRecord.setRecordDetailMsg(FileType.FloorPlan.name(), RecordDetailEnum.file_name.name(),
                    appModel.getFloorModel().getTestMapPath().substring(appModel.getFloorModel().getTestMapPath().lastIndexOf("/") + 1));
            mTestRecord.setRecordDetailMsg(FileType.FloorPlan.name(), RecordDetailEnum.file_guid.name(), UtilsMethod.getUUID());
        }

        //twq20150618文件分割时容易挂住,只有在第一次创建文件时需要做飞行模式,并且如果开始测试时没有指定不切换飞行模式
        if (!isAirplaneDone && routineSet.isSwitchAirplan(mContext) && !routineSet.isWifiModel(mContext) && !isDontSwitchAirplane) {
            isAirplaneDone = true;
            resetAirplane();
        }

        new WriteTestInfoThread().start();

        if (umpcTestinfo != null) {
            umpcTestinfo.setRTUploadStart(mTestRecord.getRecordDetails());
        }

        TraceInfoInterface.traceData.setTestLogFile(createRcuFilePath);
    }

    /**
     * 重置飞行模式
     * 开启飞行模式,三秒后关闭飞行模式
     */
    private void resetAirplane() {
        if (ApplicationModel.getInstance().isNBTest()) {
            //如果是NB模块,则不用开取飞行模式
            return;
        }
        if (isBluetoothSync)
            return;
        LogUtil.w(TAG, "----start resetAirplane----");
        myPhoneState.startAirplane(mContext);
        threadWaitCanInterrupt(3, "resetAirplane");
        myPhoneState.closeAirplane(mContext);
        threadWaitCanInterrupt(5, "resetAirplane");
        LogUtil.w(TAG, "----end resetAirplane----");

        if (appModel.getNetList().contains(ShowInfoType.LockFrequency)) {
            ForceManager fm = ForceManager.getInstance();
            fm.init();
            ForceNet netType = fm.getLockNet(mContext);
            Band[] bands = fm.getLockBandArray(mContext);
            if (bands.length > 0) {
                if (Deviceinfo.getInstance().getDevicemodel().equals("ZTENX569J"))
                    fm.lockBand(mContext, netType, bands);
                else
                    fm.lockBand(netType, bands);
            } else if (netType != ForceNet.NET_AUTO) {
                if (Deviceinfo.getInstance().getDevicemodel().equals("ZTENX569J"))
                    fm.lockNetwork(mContext, netType);
                else
                    fm.lockNetwork(netType);
            }
            threadWaitCanInterrupt(5, "lock bands");
        }
    }


    /**
     * 线程等待指定时间，支持实时中断
     *
     * @param seccond 等待时间
     * @param tips    执行事件
     */
    private void threadWaitCanInterrupt(int seccond, String tips) {
        try {
            for (int i = 0; i < Math.abs(seccond) && !appModel.isTestInterrupt(); i++) {
                LogUtil.w(TAG, "--wait " + tips + ":" + seccond + "--by:" + i);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 等待RCU文件创建完成后，写入测试人员的相关信息
     */
    private class WriteTestInfoThread extends Thread {
        public void run() {
            try {
                Thread.sleep(200);
                appModel.setRcuFileCreated(true);

                String testInfo = "Device Model=" + deviceInfo.getDevicemodel() + "\r\n"
                        + "Version=" + UtilsMethod.getCurrentVersionName(getApplicationContext());
                if (!appModel.isGeneralMode() && isUmpcTest) {
                    //如果为UMPC开始的测试，调用写入测试人员相关信息的RCU事件
                    testInfo += "\r\nCeShiRenYua="
                            + (umpcTestinfo != null && !umpcTestinfo.getTestername().equals("") ? umpcTestinfo.getTestername() : "")
                            + "\r\nCeShiShengFen=\r\nCeShiChengShi=\r\nCeShiCheLiang="
                            + "\r\nHuJiaoXuHao=\r\nXiangXiXinXi=\r\nCeShiDian=\r\nCaiYangDian=";
                    //2013.5.17增加
                    testInfo += "\r\n" + String.format("LogNumber=%s", currentFileNum);
                    testInfo += "\r\n" + String.format("LogName=%s", umpcTestinfo.getTestlocale());
                } else {
                    testInfo += "\r\nTester=" + tester;
                    if (!StringUtil.isNullOrEmpty(highSpeedRail)) {
                        testInfo += "\r\nhigh_speed_rail=" + highSpeedRail;
                    } else if (!StringUtil.isNullOrEmpty(metroCity) && !StringUtil.isNullOrEmpty(metroRoute)) {
                        testInfo += "\r\ncity=" + metroCity;
                        testInfo += "\r\nmetro_line=" + metroRoute;
                    } else if (StringUtil.isNullOrEmpty(mSingleStationName)) {
                        testInfo += "\r\nsingle_station=" + mSingleStationName;
                    }
                    testInfo += "\r\nAddress=" + testAddress;
                    testInfo += "\r\n" + "Indoor Test="
                            + (isIndoorTest ? "Yes" : "No");
                    if (isIndoorTest) {
                        testInfo += "\r\n" + "Building=" + testBuilding;
                        testInfo += "\r\n" + "Floor=" + testFloor;
                    }
                }
                testInfo += "\r\nOuterLoop=" + outCircleTimes;
                if (!appModel.isGeneralMode() && appModel.getNetList().contains(WalkStruct.ShowInfoType.TelecomSwitch)) {
                    testInfo += "\r\nSpecialInfoData=" + currentFileNum;
                }

                LogUtil.w(TAG, "--Test Information:" + testInfo);

                EventBytes.Builder(mContext)
                        .addCharArray(testInfo)
                        .writeToRcu(WalkCommonPara.MsgDataFlag_I);
                writeTestInfoEventToRcu();
                //写入VOLTE 密钥
                writeVolteKeyToRcu();

                if (appModel.isIndoorTest()) {
                    if (appModel.isWoneTest()) {     //如果是Wone插入gpsinfo
                        Location location = GpsInfo.getInstance().getLocation();
                        if (location != null) {
                            EventBytes.Builder(mContext, RcuEventCommand.IndoorTestGpsFlag)
                                    .addDouble(location.getLongitude())
                                    .addDouble(location.getLatitude())
                                    .writeToRcu(System.currentTimeMillis());
                        }
                    } else {
                        indoorTestGpsFlag();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void indoorTestGpsFlag() {
        EventBytes.Builder(mContext, RcuEventCommand.IndoorTestGpsFlag)
                .addDouble(TestInfoValue.latitude)
                .addDouble(TestInfoValue.longtitude)
                .writeToRcu(System.currentTimeMillis() * 1000);
    }

    /**
     * 将测试过程中的任务名称添加到已存在的RCU名称后面
     *
     * @param rcuName  rcu文件
     * @param doneName 任务名称
     * @return 生成的名称
     */
    private String addDonenNameToRcuName(String rcuName, String doneName) {
        if (doneName == null || doneName.equals("")) {
            return rcuName;
        }
        return rcuName.substring(0, rcuName.lastIndexOf(".")) + "-" + doneName + FileType.ORGRCU.getExtendName();
    }


    /**
     * 发送关闭RCU文件命令并将当前关闭文件的记录写入数据库中
     */
    private void sendCloseRcuFileAddToDB() {
        if (appModel.isPioneer()) {
            LogUtil.w(TAG, "sendCloseRcuFileAddToDB===is pioneer");
            return;
        }
        appModel.setRcuFileCreated(false);
        isCloseFileing = true;
        LogUtil.w(TAG, "--befer toDB the rcuname:" + createRcuFilePath);
        //测试结束设置不显示当前文件信息
        TraceInfoInterface.traceData.setTestLogFile(null);

//        String ddibFilePath = mDatasetMgr.getDecodedIndexFileName(DatasetManager.PORT_2);
        int cResult = mDatasetMgr.closeFile();//此函数阻塞

        LogUtil.w(TAG, "--befer toDB close file result:" + cResult);
        isCloseFileing = false;
        MapFactory.getMapData().getHistoryList().clear();

        if (umpcTestinfo != null) {
            umpcTestinfo.setRTUploadEnd();

            LogUtil.w(TAG, "--wait upload start");
            while (!umpcTestinfo.isUploadEnd()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LogUtil.w(TAG, "--wait upload end");
        }

        // 测试结束时停止抓包,需要兼容所有测试，包括IPAD下发 && !appModel.isTestJobIsRun()
        if (isNetsniffer) {
//            if(Deviceinfo.getInstance().isVivo()){
            NetSnifferServiceUtil.getInstance().unbindService(this);
//            }else{
//                NetSniffer.getInstance().stop();
//            }

        }

        insertFileInfoToDB();

        if (appModel.isWoneTest()) {
            writeLogLable();
        }
        testReportGoOrNogo();
    }

    /**
     * 测试结束时，判断是否需要生成测试报告
     * 此处仅start报告结果的activity
     */
    private void testReportGoOrNogo() {
        if (ServerManager.getInstance(getApplicationContext()).getAutoTip()) {
            Intent goOrNOIntent = new Intent(this, KPIResulActivity.class);
            goOrNOIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            goOrNOIntent.putExtra("TASK_NAME", createRcuFilePath.substring(createRcuFilePath.lastIndexOf("/") + 1));
            goOrNOIntent.putExtra("TEST_TIME", times);
            goOrNOIntent.putExtra("TEST_RESULT", "NO-Go");
            goOrNOIntent.putExtra("IS_GO", false);
            startActivity(goOrNOIntent);
        }
    }


    //显示通知
    @SuppressWarnings("deprecation")
    private void showNotification(String tickerText) {
        // notification //通知图标, 状态栏显示的通知文本提示,通知产生的时间
        Notification.Builder notification = new Notification.Builder(this);
        notification.setTicker(tickerText);
        notification.setSmallIcon(R.mipmap.walktour);
        notification.setWhen(System.currentTimeMillis());
        //Intent 点击该通知后要跳转的Activity
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, TestService.class), 0);
        // must set this for content view, or will throw a exception
        //如果想要更新一个通知，只需要在设置好notification之后，再次调用 setLatestEventInfo(),然后重新发送一次通知即可，即再次调用notify()。
        notification.setContentIntent(contentIntent);
        notification.setContentTitle(getString(R.string.sys_alarm));
        notification.setContentText(tickerText);
        mNotificationManager.notify(R.string.service_started, notification.build());
    }

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<TestService> reference;

        public MyHandler(TestService service) {
            this.reference = new WeakReference<>(service);
        }

        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            TestService service = this.reference.get();
            try {
                switch (msg.what) {
                    case testStopredoTraceInit:
                        Toast.makeText(service, R.string.main_testStopredoTraceInit, Toast.LENGTH_LONG).show();
                        break;
                    case testTaskListEmpty:
                        Toast.makeText(service, R.string.main_testTaskEmpty, Toast.LENGTH_LONG).show();
                        break;
                    case testTaskNotPower:
                        String tickerText = service.getResources().getString(R.string.main_license_currentTask_faild).replaceAll("当前",
                                msg.obj.toString());
                        service.showNotification(tickerText);
                        break;
                    case SeeGullService.MSG_SHOWLOG:
                        String filterMsg = getMessage(msg.obj.toString());
                        if (filterMsg.length() != 0) {
                            TraceInfoInterface.traceData.scanEventList
                                    .add(new ScanEventModel(UtilsMethod.getSimpleDateFormat1(System.currentTimeMillis()), filterMsg));
                        }
                        while (TraceInfoInterface.traceData.scanEventList.size() > 100) {
                            TraceInfoInterface.traceData.scanEventList.remove(0);
                        }
                        LogUtil.w(TAG, "--showlog:" + msg.obj.toString());
                        break;
                    case SeeGullService.MSG_UPDATE_DEVICE_INFO:
                        Map<String, Object> state = (Map<String, Object>) msg.obj;
                        LogUtil.w(TAG, "--devinfo:" + state.get("DEVICE_STATE"));
                        if (state.get("DEVICE_STATE").equals("CONNECTED")) {
                            service.doScannerTest();
                        }
                        break;
                    case SeeGullService.MSG_TEST_DATA_RECEIVED:
                        if (service.appModel.isRcuFileCreated()) {
                            LogUtil.w(TAG, "--pushScannerData:" + "---come from TestService---");
                            DatasetManager.getInstance(service.getApplicationContext()).pushExternalTraceData((byte[]) msg.obj);
                        }
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 如果volteKeyModel对象不为空
     * 将volteKeyModel中的密钥信息写入RCU中
     */
    private void writeVolteKeyToRcu() {
        VolteKeyModel volteKeyModel = ApplicationModel.getInstance().getVolteKeyModel();
        LogUtil.w(TAG, "--volteKeyWrite:" + (volteKeyModel != null ? volteKeyModel.getAuthenticationKey() + ":" + volteKeyModel.getEncryptionKey() : "key isnull"));

        if (volteKeyModel != null) {
            //LogUtil.w(TAG, "--EncryptionKey:" + volteKeyModel.getEncryptionKey()
            //		+ "--AuthenticationKey:" + volteKeyModel.getAuthenticationKey()
            //		+ "--isEnable:" + volteKeyModel.isEnable());

            long currentTime = System.currentTimeMillis();
            EventBytes.Builder(this.mContext)
                    .addInteger(SPECIALDATT_TYPE_SIPKEY)    //Special Data Type
                    .addInteger(1)                            //Version 版本号，当版本为0
                    .addInteger((int) currentTime / 1000)
                    .addInteger((int) currentTime % 1000)
                    .addByte((byte) 0xff)
                    .addInteger(volteKeyModel.getEncryptionAlgo())            //加密算法	如果原始值中百11,12,那么当前值为22
                    .addInteger(volteKeyModel.getAuthenticationAlgo())        //鉴权算法	如果原始值为非2,那么当前值为0
                    .addInteger(volteKeyModel.getEncryptionAlgoOrg())        //加密算法原始值
                    .addInteger(volteKeyModel.getAuthenticationAlgoOrg())    //鉴权算法原始值
                    .addStringBuffer(volteKeyModel.getEncryptionKey())
                    .addStringBuffer(volteKeyModel.getAuthenticationKey())
                    .writeToRcu(WalkCommonPara.MsgDataFlag_C);

			/*byte[] bt = sipEvent.getByteArray();
            String s1 = "";
			String s0 = "";
			for (int i = 0; i < bt.length; i++)
	        {
				s0 = s0 + bt[i] + " ";

	            String tempStr = Integer.toHexString(bt[i]);
	            if (tempStr.length() > 2)
	                tempStr = tempStr.substring(tempStr.length() - 2);
	            s1 = s1 + (tempStr.length() == 1 ? "0" : "") + tempStr;
	        }

			LogUtil.w(TAG,"--TestInfo volteKeyByte:" + s0);
			LogUtil.w(TAG,"--TestInfo volteKeyEvent:" + s1);*/

            //BuildRCUSignal.buildVoLTEKeySignal(mContext, keyModel)
            //.writeToRcu(WalkCommonPara.MsgDataFlag_D);
        }
        SIPInfoModel sipModel = ApplicationModel.getInstance().getSIPInfoModel();
        LogUtil.w(TAG, "--SIPInfoWrite:" + (sipModel != null
                ? sipModel.getName() + ":" + sipModel.getContent() : "SIPInfo isnull"));
        if (sipModel != null) {
            EventBytes.Builder(mContext).addInteger(SPECIALDATT_TYPE_ADBSIP) // Special
                    // Data
                    // Type
                    .addInteger(0) // Version 版本号，当版本为0
                    .addInteger((int) sipModel.getTime()).addInteger(sipModel.getuTime())
                    .addByte((byte) sipModel.getDirection()).addStringBuffer(sipModel.getName())
                    .addStringBuffer(sipModel.getContent()).writeToRcu(WalkCommonPara.MsgDataFlag_C);
        }
    }

    /**
     * 字符串处理方法
     *
     * @param message 消息对象
     */
    public static String getMessage(String message) {
        final String[] keys = new String[]{"SCAN_ID", "SCAN_TYPE", "REQUEST_TYPE", "STATUS_MESSAGE", "RESPONSE_TYPE"};
        StringBuilder result = new StringBuilder();
        for (String key : keys) {
            if (message.contains(key)) {
                int length = key.length();
                String tmp = message.substring(message.indexOf(key) + length + 2);
                if (tmp.contains(",")) {
                    tmp = tmp.substring(0, tmp.indexOf(","));
                }
                if (tmp.contains("\"")) {
                    tmp = tmp.substring(1, tmp.lastIndexOf("\""));
                }
                result.append(key).append(":").append(tmp).append(" ");
            }
        }
        return result.toString();
    }


    private boolean isScannerTestRun = false;

    private synchronized void doScannerTest() {
        //当前有扫频测试计划，执行扫频任务
        if (appModel.isScannerTest() && !isScannerTestRun) {
            isScannerTestRun = true;

            List<ScanTaskModel> scanTasks = ScanTask5GOperateFactory.getInstance().enableTasks();
            LogUtil.w(TAG, "--start scanTask:" + scanTasks.size());
            ScanTask5GOperateFactory.getInstance().addDefault();
            iService.startScan(scanTasks);
        }

    }

    /**
     * 响应暂停或中断所有测试任务
     *
     * @param isInterrupt 是否中断
     * @param dropReasion 中断原因
     */
    public synchronized void puaseOrInterruptTest(boolean isInterrupt, int dropReasion) {
        puaseOrInterruptTest(isInterrupt, dropReasion, TaskType.Default);
    }

    /**
     * 响应暂停或中断测试指定类型的任务
     * 如果不指定，默认为所有任务
     *
     * @param isInterrupt 是否中断
     * @param dropReasion 中断原因，用户中断，PPP DROP中断
     * @param targetType  是定类型任务
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public synchronized void puaseOrInterruptTest(boolean isInterrupt, int dropReasion, TaskType targetType) {
        LogUtil.w(TAG, "--puaseOrInterruptTest:" + isInterrupt + "--size:"
                + execServiceList.size() + "--reasion:" + dropReasion + "--tasktyp:" + targetType.name());
        //PPPDROP 时非数据任务个数计数
        int notDataCount = 0;
        if (execServiceList.size() > 0) {
            Iterator it = execServiceList.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, TestObjectModel> key = (Entry<String, TestObjectModel>) it.next();

                //如果不是指定类型的中断测试走原来的流程
                if (targetType == TaskType.Default) {
                    if (dropReasion != RcuEventCommand.DROP_PPPDROP || key.getValue().getTaskType().isDataTest()) {
                        interruptTestService(isInterrupt, key.getKey(), dropReasion);
                        execServiceList.remove(key.getKey());
                        it = execServiceList.entrySet().iterator();
                    } else {
                        notDataCount++;
                        LogUtil.w(TAG, "--Current Type:" + key.getValue().getTaskType().name() + "--count:" + notDataCount);
                    }

                    //当非任务计数大于等于当前任务列表数时，表示当前已结束完成
                    if (notDataCount >= execServiceList.size()) {
                        break;
                    }
                } else {
                    if (targetType == key.getValue().getTaskType()) {
                        interruptTestService(isInterrupt, key.getKey(), dropReasion);
                        execServiceList.remove(key.getKey());
                        break;
                    }
                }
            }
            LogUtil.w(TAG, "--exit puaseOrInterruptTest--" + execServiceList.size());
            //execServiceList.clear();
        }

        //twq20120404只有系统中的状态为中断时才需要执行唤醒动作
        if (appModel.isTestInterrupt()) {
            LogUtil.w(TAG, "---test interrupt notifyThread---");
            notifyThread();
        }
    }

    /**
     * 停止测试服务,当启动的specificTestJob服务不为空时,表示当前有服务绑定并运行中,需取消绑定,停止服务
     *
     * @param isInterrupt 是否手工中断测试服务
     */
    public synchronized void interruptTestService(boolean isInterrupt, String stopTaskName, int dropReason) {
        TestObjectModel testObjectm = execServiceList.get(stopTaskName);
        LogUtil.w(TAG, "--stopName:" + stopTaskName + "--stopobj isnull:" + (testObjectm == null)
                + (testObjectm == null ? "" : "--SevNull:" + (testObjectm.getmService() == null)));
        if (testObjectm != null) {
            try {
                if (testObjectm.getmService() != null) {
                    testObjectm.getmService().stopTask(isInterrupt, dropReason);
                    LogUtil.w(TAG, "--test call stopTask end--");
                }
                if (testObjectm.getTestIntent() != null) {
                    stopService(testObjectm.getTestIntent());
                }
                if (testObjectm.isServiceHasBind()) {
                    if (null != testObjectm.getServiceConnection() && isBindServiceName.contains(stopTaskName)) {
                        LogUtil.w(TAG, "stopTaskName=" + stopTaskName);
                        unbindService(testObjectm.getServiceConnection());
                        testObjectm.setServiceHasBind(false);
                        testObjectm.setServiceConnection(null);
                        isBindServiceName.remove(stopTaskName);
                    }
                }

                if (isUmpcTest) {
                    testDoneState = currModelDoneState.get(stopTaskName);
                    //将测试结果返回到Pioneer中
                    pioneerInfo.setSuccess(testDoneState.getTestResult().equals(RESULT_SUCCESS));
                    UmpcSwitchMethod.sendEventToController(getApplicationContext(), UMPCEventType.RealTimeData.getUMPCEvnetType(),
                            pioneerInfo.getTestSuccessStr(testObjectm.getTestIntent().getStringExtra(WalkMessage.TestInfoForPioneer))
                            , umpcTestinfo.getController());
                }

                taskDoneStateChange(stopTaskName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获结束后，设用些方法，将当前任务HASH MAP表中相应的状态值减1并重新存入该键值,需要处理同步情况
     *
     * @param taskName 任务名称
     * @author tangwq
     */
    private void taskDoneStateChange(String taskName) {
        TestDoneStateModel doneState = currModelDoneState.get(taskName);
        doneState.subDoneStateNum(1);
        currModelDoneState.put(taskName, doneState);
    }

    //private boolean isAbnormalStop = false;

    /**
     * 注册connection
     * 绑定远程服务,远程服务为独立进程,如FtpTest服务)
     */
    private ServiceConnection getServiceConnection(final String taskNames) {
        return new ServiceConnection() {
            public void onServiceDisconnected(ComponentName name)
            {
                String keyName = null;
                if (null != execServiceList && execServiceList.size() > 0) {
                    try {
                        Iterator<Map.Entry<String, TestObjectModel>> it = execServiceList.entrySet().iterator();
                        while(it.hasNext()) {
                            Map.Entry<String, TestObjectModel> entry = (Map.Entry<String, TestObjectModel>) it.next();
                            TestObjectModel mm=entry.getValue();
                            if (mm.getTestIntent().getComponent().getClassName().equals(name.getClassName())) {
                                keyName = entry.getKey();
                                break;
                            }
                        }

//                        for (String key : execServiceList.keySet()) {
//                            TestObjectModel mm = execServiceList.get(key);
//                            if (mm.getTestIntent().getComponent().getClassName().equals(name.getClassName())) {
//                                keyName = key;
//                                break;
//                            }
//                        }

                        LogUtil.w(TAG,
                                "--onServiceDisconnected,onServiceConnected-:keyName=" + keyName + "," +
                                        "taskNames=" + taskNames);
                        if (execServiceList.get(keyName) != null && null != keyName) {
                            execServiceList.get(keyName).setAbnormalStop(true);
                        } else {
                            LogUtil.w(TAG,
                                    "--onServiceDisconnected,onServiceConnected-:keyName=" + keyName);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            public void onServiceConnected(ComponentName name, IBinder service) {
                IService mService = IService.Stub.asInterface(service);
                try {
                    mService.registerCallback(mCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                LogUtil.w(TAG, "--onServiceConnected-:name=" + name);
                String keyName = "";
                for (String key : execServiceList.keySet()) {
                    TestObjectModel mm = execServiceList.get(key);
                    LogUtil.w(TAG, "--onServiceConnected-:mm=" + mm.getTestIntent().getComponent().getClassName() + ",name=" + name.getClassName());
                    if (mm.getTestIntent().getComponent().getClassName().equals(name.getClassName())) {
                        keyName = key;
                        break;
                    }
                }
                LogUtil.w(TAG, "--onServiceConnected-:keyName=" + keyName + ",taskNames=" + taskNames + ",execServiceList.size=" + execServiceList.size());
                if (null != keyName) {
                    testObject = execServiceList.get(keyName);
                    LogUtil.w(TAG, "--onServiceConnected-:--testObject != null=" + (testObject != null));

                    if (testObject != null) {
                        testObject.setServiceHasBind(true);
                        testObject.setmService(mService);
                        execServiceList.put(keyName, testObject);
                        //如果之前出现注册服务异常结束，此处注册成功后调用中断结束任务动作
                        if (testObject.isAbnormalStop()) {
                            testDoneState = currModelDoneState.get(keyName);
                            testObject.setAbnormalStop(false);
                            testDoneState.setTestResult("AbnormalStop");

                            currModelDoneState.put(keyName, testDoneState);
                            // 并发业务过程中如果测试服务异常结束，此处需另处理
                            interruptTestService(false, keyName, RcuEventCommand.DROP_NORMAL);
                            execServiceList.remove(keyName);
                            LogUtil.w(TAG, "--isAbnormalStop--execList:" + execServiceList.size());
                        }
                    }
                } else {
                    LogUtil.w(TAG, "--onServiceConnected-:keyName=" + keyName);
                }
            }
        };
    }

    /**
     * 远程服务(测试独立进程)的回调函数
     */
    private ICallback mCallback = new ICallback.Stub() {
        private long curTime = System.currentTimeMillis();

        /**
         * 当后台测试有事件变化时触发此方法
         */
        public void OnEventChange(String event) throws RemoteException {
            showEvent(event);
        }

        /**
         * 此事件为统计数据有改事时触发
         */
        @SuppressWarnings("rawtypes")
        public void onChartDataChanged(Map chartList) throws RemoteException {
            TotalDataByGSM.getInstance().updateTotalUnifyTimes(mContext, (HashMap<?, ?>) chartList);
        }

        /**
         * 当后台数据有更新时,触发此方法
         */
        @SuppressWarnings({"rawtypes", "unchecked"})
        public void OnDataChanged(Map dataList) throws RemoteException {
            if (System.currentTimeMillis() - curTime < 1000) {//控制1秒内刷1次,防止刷新太快
                return;
            }
            curTime = System.currentTimeMillis();
            //当数据业务回传的信息是当前第一个业务业务的状态，则显示到仪表盘，其它不显示,只有并行业务存在该问题
            //twq20151007并发中所有瞬时速率都记录,实现多仪表盘显示
            /*if(dataList.get(TaskTestObject.stopResultName) == null
                    || theFirstDataNameByMulti.equals(dataList.remove(TaskTestObject.stopResultName).toString())){*/
            String taskName = dataList.get(TaskTestObject.stopResultName) == null ? ShowInfo.defaultChatName :
                    dataList.remove(TaskTestObject.stopResultName).toString();
            String lteCA = TraceInfoInterface.getParaValue(UnifyParaID.LTECA_Capacity_Packet_Capability);
            LogUtil.d(TAG, "----OnDataChanged----taskName:" + taskName + "----lteCA:" + lteCA);
            String infx = getString(R.string.info_data_currentnet) + "@" + UtilsMethodPara.netWorkCaType(lteCA);
            dataList.put(DataTaskValue.BordRightTile.name(), infx);
            dataList.put(WalkStruct.DataTaskValue.IsWlanTest.name(), isWlanTest);
            Iterator itData = dataList.entrySet().iterator();
            showInfo.SetChartData(taskName, itData);
            infx = null;
        }

        /**
         * 当后台测试业务执行到结束状态时,调用此方法
         */
        @SuppressWarnings("rawtypes")
        public void onCallTestStop(Map stopResult) throws RemoteException {
            String testName = stopResult.get(TaskTestObject.stopResultName).toString();
            String result = (String) stopResult.get(TaskTestObject.stopResultState);
            TestDoneStateModel doneState = currModelDoneState.get(testName);
            //默认是成功
            doneState.setTestResult(RESULT_SUCCESS);
            LogUtil.w(TAG, "--result:" + (result == null ? "null" : result));
            if (result != null) {
                doneState.setTestResult(result);
            }
            doneState.setDoneStateNum(0);
            currModelDoneState.put(testName, doneState);

            LogUtil.w(TAG, "--TestStop:" + testName + "-Result:" + doneState.getTestResult()
                    + "--execList:" + execServiceList.size() + "--isInterrupt:" + appModel.isTestInterrupt());

            //如果当前是中断测试状态，不重新执行中断测试的动作
            if (!appModel.isTestInterrupt()) {
                interruptTestService(appModel.isTestInterrupt(), testName
                        , RcuEventCommand.DROP_NORMAL);
                execServiceList.remove(stopResult.get(TaskTestObject.stopResultName).toString());
                LogUtil.w(TAG, "--StopEnd execList:" + execServiceList.size());
            }

        }

        /**
         * 访问主进程的信息
         * [功能详细描述]
         * @param callType 调用类型
         * @return 调用信息
         * @throws RemoteException 远程调用异常
         * @see com.walktour.service.ICallback#callMainProcess(int)
         */
        @Override
        public Map<String, TotalSpecialModel> callMainProcess(int callType) throws RemoteException {
            if (callType == WalkCommonPara.CallMainType_GetNetStat_ByPingStart) {
                TotalSpecialModel startModel = MyPhoneState.getNetworkNameForTotal(
                        myPhoneState.getNetWorkType(getApplicationContext()), deviceInfo.getNettype());
                HashMap<String, TotalSpecialModel> totalMap = new HashMap<>();
                totalMap.put(WalkCommonPara.CallMainResultKey, startModel);
                LogUtil.w(TAG, "--callMainProcess mk1" + startModel.getMainKey1() + "--mk2:" + startModel.getMainKey2());
                return totalMap;
            } else if (callType == WalkCommonPara.CallMainType_Do_Attach) {
                if (ApplicationModel.getInstance().isNBTest()) {
                    LogUtil.w(TAG, "WalkCommonPara.CallMainType_Do_Attach is NBIot");
                    Intent intentThree = new Intent(mContext, NBHandlerService.class);
//                    intentThree.putExtra("select",NBHandlerService.COMMAND_UP);
                    intentThree.putExtra("select", NBHandlerService.COMMAND_MODEL_ATTACH);
                    mContext.startService(intentThree);
                } else {
                    myPhoneState.closeAirplane(mContext);
                }
            } else if (callType == WalkCommonPara.CallMainType_Do_Detach) {
                if (ApplicationModel.getInstance().isNBTest()) {
                    LogUtil.w(TAG, "WalkCommonPara.CallMainType_Do_Detach is NBIot");
                    Intent intentThree = new Intent(mContext, NBHandlerService.class);
//                    intentThree.putExtra("select",NBHandlerService.COMMAND_DOWN);
                    intentThree.putExtra("select", NBHandlerService.COMMAND_MODEL_DETTACH);
                    mContext.startService(intentThree);
                } else {
                    myPhoneState.startAirplane(mContext);
                }
            }
            return null;
        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public void onParaChanged(int callType, Map paraValue) throws RemoteException {
            try {

                if (callType == WalkCommonPara.CALL_BACK_VIDEO_PLAY_REAL_PARA ||
                        callType == WalkCommonPara.CALL_BACK_VIDEO_STREAM_REAL_PARA) {
                    Map values = TraceInfoInterface.traceData.getVideoRealPrar();
                    for (Object key : paraValue.keySet()) {
                        values.put(key, paraValue.get(key));
                    }
                    //RefreshEventManager.notifyRefreshEvent(RefreshType.REFRESH_STREAM, values);
                    sendBroadcast(new Intent(WalkCommonPara.VIDEO_REAL_PARA_CHANGE));
                } else if (callType == WalkCommonPara.CALL_BACK_SET_FIRSTDATA_STATE) {
                    LogUtil.w(TAG, "--firstData:" + paraValue.get(WalkCommonPara.CALL_BACK_FIRSTDATE_STATE_KEY));
                    if (paraValue.get(WalkCommonPara.CALL_BACK_FIRSTDATE_STATE_KEY) != null) {
                        appModel.setFirstData((Boolean) paraValue.get(WalkCommonPara.CALL_BACK_FIRSTDATE_STATE_KEY));
                    }
                } else if (callType == WalkCommonPara.CALL_BACK_SET_TESTTIMES) {
                    if (paraValue.get(WalkCommonPara.CALL_BACK_SET_TEST_TIMES_KEY) != null) {
                        TraceInfoInterface.traceData.setTestTimes(mCurTaskModel, paraValue.get(WalkCommonPara.CALL_BACK_SET_TEST_TIMES_KEY).toString());
                    }
                }
            } catch (Exception e) {
                LogUtil.w(TAG, "onParaChanged", e);
            }
        }

        /**
         * 从测试进程访问主进程的网络类型
         * @param isPBM 是否PBM业务
         */
        @Override
        public int getNetWorkType(boolean isPBM) {
            NetStateModel netState = NetStateModel.getInstance();
            if (isPBM) {
                int netType = netState.getCurrentNetTypeForPBM();
                if (netType > 0)
                    return netType;
            } else {
                //2014.7.22 异步查询网络类型
                CurrentNetState state = netState.getCurrentNetTypeSync();

                long g = state.getGeneral();
                csfbHM.put("RequestNetType", g);
                LogUtil.i(TAG, "RequestNetType:" + g + "G," + state.name());

                return state.getCurrentNetId();
            }
            return 0;
        }

    };

    private void showEvent(String event) {
        LogUtil.d(TAG, "----showEvent----" + event);
        //2013.11.21 暂时屏蔽从业务过来的旧规范事件
        //EventManager.getInstance().addEvent(mContext,event);
    }

    /**
     * 发送常规消息到服务端（ipad/umpc）
     */
    protected void sendNormalMessage(String message) {
        Intent intent = new Intent();
        intent.setAction(WalkMessage.ACTION_UNIT_NORMAL_SEND);
        intent.putExtra(WalkMessage.KEY_UNIT_MSG, message);
        sendBroadcast(intent);
        LogUtil.i(TAG, "send normal msg:" + message);
    }

    /**
     * 事件页面的空行分隔
     */
    private void sendSplitLine() {
        EventManager.getInstance().addSplitLine(mContext);
    }

    /**
     * 写入RCU事件
     *
     * @param eventFlag 事件标识，如主叫起呼为10,请参考RCU相关文档
     * @param flag      原因码
     * @param time      事件时间
     */
    protected void writeRcuEvent(int eventFlag, int flag, long time) {
        //UtilsMethod.sendWriteRcuEvent(mContext,eventFlag,flag);
        EventBytes.Builder(mContext, eventFlag)
                .addInteger(flag)
                .writeToRcu(time);
    }

    /**
     * 写并发开始结束事件
     *
     * @param model     测试业务
     * @param eventFlag 事件标识
     */
    protected void wirteParallelEvent(TaskModel model, int eventFlag) {
        //写并发任务开始时事件
        TaskRabModel rabModel = (TaskRabModel) model;
        EventBytes.Builder(mContext, eventFlag)
                .addInteger(rabModel.getVoiceDelay() > 0 ? 1
                        : (rabModel.getVoiceDelay() < 0 ? 2 : 0))
                .addStringBuffer(eventFlag == RcuEventCommand.ParallelStart ? rabModel.getStartLable() :
                        rabModel.getEndLable())
                .writeToRcu(System.currentTimeMillis());
    }

    /**
     * 周期写入设备IMEI信息线程
     * 当前为测试状态时定期写入,否则退出线程
     * 当前文件件创建成功状态写事件
     *
     * @author Tangwq
     */
    @SuppressLint("HardwareIds")
    private class WriteDeviceInfoThread extends Thread {

        String imsiStr = "0";
        long imsi = RcuEventCommand.NullityRcuValue;
        long imei = RcuEventCommand.NullityRcuValue;
        long meid = RcuEventCommand.NullityRcuValue;

        @Override
        public void run() {

            if (telManager == null) {
                telManager = (TelephonyManager) getApplicationContext()
                        .getSystemService(Context.TELEPHONY_SERVICE);
            }

            while (ApplicationModel.getInstance().isTestJobIsRun()
                    || ApplicationModel.getInstance().isTestPause()) {

                String newIMSI = telManager.getSubscriberId();

                if (newIMSI != null && appModel.isRcuFileCreated()) {
                    if (!newIMSI.equals(imsiStr)) {
                        LogUtil.i(TAG, "-Change IMSI:" + newIMSI);
                        imsiStr = newIMSI;
                        writeDeviceInfoToRcu();
                    }
                } else {
                    LogUtil.i(TAG, "IMSI null or File Created:" + appModel.isRcuFileCreated());
                }

                try {
                    for (int i = 0; i < 10; i++) {//循环10次，每次1秒，停止测试
                        if (ApplicationModel.getInstance().isTestJobIsRun() || ApplicationModel.getInstance().isTestPause()) {
                            Thread.sleep(1 * 1000);
                        } else {
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

        /**
         * 往RCU文件中写入设备信息
         * 2013.8.23 张勇说没有的填无效值 -9999
         */
        private void writeDeviceInfoToRcu() {

            try {
                imsi = Long.parseLong(telManager.getSubscriberId());

                if (telManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                    imei = Long.parseLong(telManager.getDeviceId());
                }
                if (telManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                    meid = Long.parseLong(telManager.getDeviceId(), 16);
                }

            } catch (Exception e) {
                LogUtil.e(TAG, "writeDeviceInfoToRcu:" + e.toString());
            }

            //Log.i(tag,String.format("IMSI:%d,IMEI:%d,MEID:%d", imsi,imei,meid ) );

            EventBytes.Builder(mContext, RcuEventCommand.DeviceIdentity)
                    .addInt64(imsi)
                    .addInt64(imei)
                    .addInt64(meid)
                    .writeToRcu(System.currentTimeMillis());

        }
    }


    /**
     * 做锁网动作d
     * 0:FREE,1:GSM,2:TD_SCDMA,3:CDMA2000,4:WCDMA,5:EVDO,6:LTE
     *
     * @param netType 网络类型
     */
    private void doLockNet(int netType) {
        if (routineSet != null && routineSet.canRunScript()
                && netType >= 0 && netType != currentNetType) {
            currentNetType = netType;
            String netScript = "";
            if (deviceInfo.getDevicemodel().toUpperCase(Locale.getDefault()).equals("ME860")) {
                if (netType == 1) {
                    netScript = "Lock_ME860_GSMonly.dls";
                } else if (netType == 4) {
                    netScript = "Lock_ME860_WCDMAonly.dls";
                } else if (netType == 0) {
                    netScript = "Lock_ME860_WCDMApreferred.dls";
                }
            }
            LogUtil.w(TAG, "---lock net:" + currentNetType + "---script:" + netScript);
            if (!netScript.equals("")) {
                Intent intent = new Intent();
                intent.setClass(mContext, MutilyTester.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                byte[] xml = ByteUtil.getXML(getAssets(), netScript);
                ScriptRun.run(TestService.this, getAssets(), intent, xml);
            }
        }
    }

    /**
     * 初始化执行锁网脚本的运行环境
     */
    private void initRunScriptEnv() {
        if (deviceInfo.getDevicemodel().equalsIgnoreCase("ME860")
                && routineSet != null && routineSet.canRunScript() && !WalktourApplication.isInit) {
            ScriptRun.initApkPackage("com.walktour.gui");
            ScriptService service = ScriptService.getInstance();
            boolean flag = service.startServiceAndClient(getAssets(), ScriptService.port);
            LogUtil.w(TAG, "----ScriptService init:" + flag);
            if (!flag) {
                Toast.makeText(this, "启动服务失败，请重启应用...", Toast.LENGTH_SHORT).show();
                //this.finish();
            }
            WalktourApplication.isInit = true;
        }
    }

    /**
     * 函数功能：处理数据集事件
     *
     * @param intent 调用参数
     */
    private void processEvent(Intent intent) {
        int rcuId = intent.getIntExtra(WalkMessage.KEY_EVENT_RCUID, 0);
        final long time = intent.getLongExtra(WalkMessage.KEY_EVENT_TIME,
                System.currentTimeMillis());

        switch (rcuId) {

            case DataSetEvent.ET_ReturnToLTE_Request:
                LogUtil.i(TAG, "ET_ReturnToLTE_Request");
                csfbHM.put("RequestTime", time);
                //2014.7.22 不能在这里查询ReturnToLte之前的网络，这里查出来会是4G
                break;

            case DataSetEvent.ET_ReturnToLTE_Complete:
                LogUtil.i(TAG, "ET_ReturnToLTE_Complete");
                if (csfbHM.get("RequestTime") != null && csfbHM.get("RequestNetType") != null
                        && csfbHM.get("CallType") != null) {
                    long requestTime = csfbHM.get("RequestTime");
                    long general = csfbHM.get("RequestNetType");
                    boolean moc = csfbHM.get("CallType") == 0;
                    int delay = (int) (time - requestTime);
                    if (delay > 0) {
                        LogUtil.i(TAG, "delay:" + delay + "gen:" + general);
                        HashMap<String, Integer> map = new HashMap<>();
                        if (general == 2) {//2G返回LTE统计
                            map.put(moc ? TotalDial._csfb_mo_2G_ReturnLTE.name()
                                    : TotalDial._csfb_mt_2G_ReturnLTE.name(), 1);
                            map.put(moc ? TotalDial._csfb_mo_2G_ReturnLTE_Delay.name()
                                    : TotalDial._csfb_mt_2G_ReturnLTE_Delay.name(), delay);
                        } else if (general == 3) {//3G返回LTE统计
                            map.put(moc ? TotalDial._csfb_mo_3G_ReturnLTE.name()
                                    : TotalDial._csfb_mt_3G_ReturnLTE.name(), 1);
                            map.put(moc ? TotalDial._csfb_mo_3G_ReturnLTE_Delay.name()
                                    : TotalDial._csfb_mt_3G_ReturnLTE_Delay.name(), delay);
                        }
                        if (!map.isEmpty()) {
                            TotalDataByGSM.getInstance().updateTotalUnifyTimes(mContext, map);
                            csfbHM.clear();
                        }
                    }
                }
                break;

            case DataSetEvent.ET_ReturnToLTE_Failure:
                csfbHM.clear();
                break;

//		case DataSetEvent.ET_HttpPageLastData:
//			Intent i = new Intent(mContext,WebViewActivity.class);
////			i.putExtra("path","/sdcard/Walktour/temp/test_down/index.htm" );
////			i.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
////			startActivity( i );
//			break;
        }
    }

    /**
     * 业务测试开始和结束事件写入
     *
     * @param eventID 事件ID
     */
    private void writeStartOrStopEventToRcu(int eventID) {
        EventBytes eventBytes = EventBytes.Builder(mContext, eventID);
        eventBytes.addInteger(1);
        eventBytes.addStringBuffer("1003");
        eventBytes.writeToRcu(System.currentTimeMillis() * 1000);
    }

    /**
     * 填写测试信息事件到每一个分隔文件中
     */
    private void writeTestInfoEventToRcu() {
        StringBuilder sb = new StringBuilder();
        sb.append("<TestInfo>");
        sb.append("<DeviceID>{").append(myPhoneState.getGUID(mContext)).append("}</DeviceID>");
        sb.append("<Vendor>0</Vendor>");
        sb.append("<DeviceType>2</DeviceType>");
        sb.append("<DeviceModel>").append(deviceInfo.getDevicemodel()).append("</DeviceModel>");
        if (this.umpcTestinfo == null) {
            sb.append("<TestPlanID>-9999</TestPlanID>");
            sb.append("<TestPlanName>Null</TestPlanName >");
        } else {
            sb.append("<TestPlanID>").append(this.umpcTestinfo.getTaskno()).append("</TestPlanID>");
            sb.append("<TestPlanName>").append(this.umpcTestinfo.getTestername()).append("</TestPlanName>");
        }
        if (GpsInfo.getInstance().isJobTestGpsOpen()) {
            sb.append("<TestType>0</TestType>");
        } else {
            sb.append("<TestType>1</TestType>");
        }
        sb.append("<TestLevel>-9999</TestLevel>");
        sb.append("<Testscenario>-9999</Testscenario>");
        if (StringUtil.isNullOrEmpty(testAddress))
            sb.append("<Address>Null</Address>");
        else
            sb.append("<Address>").append(testAddress).append("</Address>");
        sb.append("<SecondAddress>Null</SecondAddress>");
        if (isUmpcTest) {//小背包测试写入无效值
            sb.append("<Longitude>-9999</Longitude>");
            sb.append("<Latitude>-9999</Latitude>");
        } else {
            Location location = GpsInfo.getInstance().getLocation();
            if (location != null) {
                sb.append("<Longitude>").append(location.getLongitude()).append("</Longitude>");
                sb.append("<Latitude>").append(location.getLatitude()).append("</Latitude>");
            } else {
                sb.append("<Longitude>-9999</Longitude>");
                sb.append("<Latitude>-9999</Latitude>");
            }
        }
        sb.append("<TestPoint>0</TestPoint>");
        if (StringUtil.isNullOrEmpty(tester))
            sb.append("<Tester>Null</Tester>");
        else
            sb.append("<Tester>").append(tester).append("</Tester>");
        if (this.hasMOCMOSTest) {
            sb.append("<MOT>").append(this.csfbHM.get("CallType")).append("</MOT>");
            sb.append("<MOT_GUID>{").append(myPhoneState.getGUID(mContext)).append("}</MOT_GUID>");
        } else {
            sb.append("<MOT>-9999</MOT>");
            sb.append("<MOT_GUID>Null</MOT_GUID>");
        }
        sb.append("</TestInfo>");
        LogUtil.d(TAG, sb.toString());
        EventBytes eventBytes = EventBytes.Builder(mContext, RcuEventCommand.TEST_PLAN_INFO);
        eventBytes.addInteger(0);
        eventBytes.addStringBuffer(sb.toString());
        eventBytes.writeToRcu(System.currentTimeMillis() * 1000);
        if (umpcTestinfo != null) {
            //写入CU数据格式
            EventBytes eventBytes2 = EventBytes.Builder(mContext);
            eventBytes2.addInteger(1);
            eventBytes2.addSingle(umpcTestinfo.getCuScale());
            eventBytes2.addInteger(umpcTestinfo.getCuPicType());
            eventBytes2.addStringBuffer(umpcTestinfo.getCuPicName());
            eventBytes2.addInteger(0);
            eventBytes2.addCharArray("");
            eventBytes2.writeToRcu(WalkCommonPara.MsgDataFlag_N);
        } else if (isIndoorTest || isCQTChecked) {
            //如果是室内测试，写入比例尺
            int picType = 0;
            SharePreferencesUtil preferencesUtil = SharePreferencesUtil.getInstance(WalktourApplication.getAppContext());
            boolean isLoadIndoorMap = preferencesUtil.getBoolean(MapView.SP_IS_LOAD_INDOOR_MAP);
            LogUtil.i(TAG, "---isLoadIndoorMap:" + isLoadIndoorMap + "----");
//            if(isLoadIndoorMap){
            String mapPath = preferencesUtil.getString(MapView.SP_INDOOR_MAP_PATH);
            if (!TextUtils.isEmpty(mapPath)) {
                //图片类型,0-BMP,1-JPG,2-PNG
                String picTypeSuffix = mapPath.substring(mapPath.lastIndexOf("."), mapPath.length());
                if (picTypeSuffix.equalsIgnoreCase(".BMP")) {
                    picType = 0;
                } else if (picTypeSuffix.equalsIgnoreCase(".JPG")) {
                    picType = 1;
                } else if (picTypeSuffix.equalsIgnoreCase(".PNG")) {
                    picType = 2;
                }
                LogUtil.d(TAG, "picTypeSuffix: " + picTypeSuffix + ",picType:" + picType);
            }
            float plottingScale = preferencesUtil.getFloat(mapPath, 1.0f);
            LogUtil.i(TAG, "---------室内测试，写入比例尺:" + plottingScale + "------");
            /**
             * @anthor max
             * @deprecated 文档说明要求是米/象素，而我们使用的plottingScale是象素/米.所以得用1/plottingScale
             *
             *
             */
            EventBytes eventBytes2 = EventBytes.Builder(mContext);
            eventBytes2.addInteger(1);
            eventBytes2.addSingle(plottingScale);
            eventBytes2.addInteger(picType);
            eventBytes2.addStringBuffer(mapPath);
            eventBytes2.addInteger(0);
            eventBytes2.addCharArray("");
            eventBytes2.writeToRcu(WalkCommonPara.MsgDataFlag_N);
//            }
        }
    }

    /***
     * 是否是语音测试
     * @param model
     * @return
     */
    private boolean isCallTest(TaskModel model) {
        return WalkStruct.TaskType.valueOf(model.getTaskType()) == TaskType.InitiativeCall || WalkStruct.TaskType.valueOf(model.getTaskType()) == TaskType.PassivityCall;
    }

}
