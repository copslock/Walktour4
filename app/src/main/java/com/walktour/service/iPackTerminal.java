package com.walktour.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;

import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.TestRecord;
import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.EventManager;
import com.dinglicom.dataset.PageQueryParas;
import com.dinglicom.ipack.IpackControl;
import com.dinglicom.ipack.IpackControl.IpackFileType;
import com.dinglicom.ipack.IpackControl.TermType;
import com.walktour.Utils.APNOperate;
import com.walktour.Utils.AlertWakeLock;
import com.walktour.Utils.AppVersionControl;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ByteUitils;
import com.walktour.Utils.DataSetFileUtil;
import com.walktour.Utils.DataTypeChangeHelper;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.ForPioneerInfo;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.ShowInfo;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UmpcSwitchMethod;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.CurrentNetState;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.Utils.WalkStruct.ShowInfoType;
import com.walktour.Utils.WalkStruct.UMPCConnectStatus;
import com.walktour.Utils.WalkStruct.UMPCEventType;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhone;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigNetwork;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.config.ServerManager;
import com.walktour.control.instance.FileDB;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.data.model.DBManager;
import com.walktour.gui.data.model.DataModel;
import com.walktour.gui.locknet.ForceControler.Band;
import com.walktour.gui.locknet.ForceControler.ForceNet;
import com.walktour.gui.locknet.ForceManager;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.task.activity.scannertsma.model.TestSchemaType;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.TaskGroupConfig;
import com.walktour.gui.task.parsedata.xml.btu.TaskConverter;
import com.walktour.gui.task.parsedata.xml.btu.model.TestScheme;
import com.walktour.gui.task.parsedata.xml.common.TaskXmlTools;
import com.walktour.model.NetStateModel;
import com.walktour.model.StateInfoModel;
import com.walktour.model.UmpcTestInfo;
import com.walktour.model.YwDataModel;
import com.walktour.service.app.DataTransService;
import com.walktour.service.app.Killer;
import com.walktour.service.app.datatrans.model.UploadFileModel;
import com.walktour.service.phoneinfo.utils.MobileUtil;
import com.walktour.service.test.PBMTest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;


@SuppressLint("SdCardPath")
public class iPackTerminal extends Service implements RefreshEventListener {
    private static final String TAG = "iPackTerminal";

    private boolean umpcLogined = false;    //UMPC登陆成功
    private byte bDual = 1;                            //数据上传时是否双通道，0表示单通道(旧模式)，1表示双通道（用于大数据上传）
    private int atuPort = -1;        //AUT端口号
    private int controlerService = UmpcTestInfo.ControlForNone;    //控制端类型
    private PageQueryParas pageParas = null;
    //private UmpcEnvModel 		umpcEnvModel	= null;;
    private ApplicationModel appModel = null;
    private Deviceinfo deviceInfo = null;
    private MyPhone myPhone = null;
    private GpsInfo gpsInfo = null;
    private MyPhoneState phoneState = null;
    private ForPioneerInfo forIpadInfo = null;
    private DBManager dbManager = null;

    private String encryptCode = "";        //存放需要对测试结果进行加密的密钥
    private String encryptSource = "";        //存放加密源文件名，
    private String encryptTarget = "";        //存放加密目标文件路径	如果收到两次一样的目标源，不做处理
    private String controller = "";        //是否是往Pionner传输数据状态

    private int[] lockObj = new int[0];            //通知对象锁,当连接UMPC失败时，交互线程不再运行
    private ActionSwitchThread mActionSwitchThread;                    //收到消息与UMPC交互线程
    /***
     *连接小背包相关的方法控制类
     */
    private IpackControl ipackControl = IpackControl.getInstance();

    /**
     * 一次通话MOS的测试序号,即索引文件（每次通话均从1开始编号），举例：对于主叫手机，如：主叫第一次放音序号为1，则第二次录音序号为2。依次交替！
     **/
    public static short recordFileINDEX = 0;
    /**
     * Mos测试时间范围存储文件
     **/
    public static final String mosFileName = AppFilePathUtil.getInstance().getSDCardFile("timeframe.txt").getAbsolutePath();
    /**
     * ipack测试每次采集到的gps点的个数
     **/
    private int gpsCount = 0;
    /**
     * 当前电量
     **/
    private int currentLevel = 0;

    /**
     * 时钟
     */
    private AlarmManager alarmManager;
    /**
     * ID标记
     */
    public static final String PI_ID_EXTRA = "id";
    /**
     * 每小时新建个文件,按小时保存log数据
     **/
    private static final int PI_ID_TASK_LISTER_ONE = 1;
    /**
     * 每小时采集一次的定时器
     */
    private PendingIntent pi_task_one;

    /***
     * 需要上传的文件
     */
    List<UploadFileModel> uploadFiles = new ArrayList<UploadFileModel>();
    /**
     * 最后一次刷新参数
     */
    private long mLastTimeChanged;
    /**
     * 是否正在刷新参数
     */
    private boolean isTimeChanging = false;
    /**
     * 需要上传的文件的fileID
     */
    private String fileID = "";

    /**
     * 备份上传完成的ID
     */
    private String fileIDBack = "";

    /**
     * 是否已经注册接收获取PBMTest测试过程中参数广播
     */
    private boolean hasRegisteredPBMBroadcast = false;

	/**
	 * czc:电信小背包主被叫ipack下发的网络同步类型,默认值：NONE
	 */
	private String mDxSyncNetType = "NONE";
    private boolean isAddedFirstPoint=false;//是不是已经加第一个点

    /**接收数据,防止并发多建立几个**/
    private StringBuffer dataMsg1=new StringBuffer();
    private StringBuffer dataMsg2=new StringBuffer();
    private StringBuffer dataMsg3=new StringBuffer();
    private StringBuffer dataMsg4=new StringBuffer();
    /**
     * 获取 PendingIntent
     *
     * @param context 上下文
     * @param value   值
     * @return PendingIntent
     */
    private PendingIntent getPendingIntent(Context context, int value) {
        Intent intent = new Intent(context, iPackTerminal.class);
        intent.putExtra(PI_ID_EXTRA, value);
        return PendingIntent.getService(context, value, intent, 0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        registerPBMReceiver();
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onStart(Intent intent, int startid) {
        LogUtil.w(TAG, "--onstart isReBand:" + (intent == null));
        if (intent == null) {
            startService(new Intent(getApplicationContext(), Killer.class));
            return;
        }
        registerPBMReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        registerPBMReceiver();
        int id = intent.getIntExtra(PI_ID_EXTRA, 0);
        try {
            switch (id) {
                case PI_ID_TASK_LISTER_ONE:
                    LogUtil.w(TAG, "--onStartCommand By FiveMinutes--:" + (appModel.getUmpcStatus() == UMPCConnectStatus.TerminalLoginSucces));
                    uploadLevel();
                    doTaskFiveMinutes();
                    break;
            }
        } catch (Exception ex) {
            LogUtil.w(TAG, ex.getMessage());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 上传电量
     */
    private void uploadLevel() {
        if (appModel.getUmpcStatus() == UMPCConnectStatus.TerminalLoginSucces) {
            LogUtil.w(TAG, "upload mobile level=" + currentLevel);
            byte[] cmdByteArray = DataTypeChangeHelper.int2byte4(1004);
            TransparentNotifyResp(DataTypeChangeHelper.byteMerger(cmdByteArray, String.valueOf(currentLevel).getBytes()));
        }
    }

    /***
     * log采集：按每小时采集数据
     */
    private void doTaskFiveMinutes() {

        if (null == pi_task_one) {
            LogUtil.w(TAG, "doTaskOneHour 为空.");
            return;
        }
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5 * 60 * 1000, pi_task_one);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.w(TAG, "---onCreate---");
        AlertWakeLock.acquire(this);
        dbManager = DBManager.getInstance(getApplicationContext());

        RefreshEventManager.addRefreshListener(this);
        pageParas = new PageQueryParas();
        forIpadInfo = ForPioneerInfo.getInstance();

        initEnv();
        registerRecv();

        new Thread(new StartLibService()).start();
        if (mActionSwitchThread == null) {
            mActionSwitchThread = new ActionSwitchThread(lockObj);
            mActionSwitchThread.start();
        }
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        pi_task_one = this.getPendingIntent(this, PI_ID_TASK_LISTER_ONE);
        doTaskFiveMinutes();
        registReceiver();
        registerPBMReceiver();
    }

    /**
     * 注册接收PBMTest广播过来的PBM DL/UL BANDWIDTH数据
     */
    private void registerPBMReceiver() {
        if(!hasRegisteredPBMBroadcast){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(PBMTest.BROADCAST_PBM_UP_BANDWIDTH);
            intentFilter.addAction(PBMTest.BROADCAST_PBM_DOWN_BANDWIDTH);
            registerReceiver(mPBMBroadcastReceiver, intentFilter);
            hasRegisteredPBMBroadcast = true;
        }
    }

    /**
     * 接收PBMTest DL/UL BANDWIDTH广播
     */
    private BroadcastReceiver mPBMBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, android.content.Intent intent) {
            String action = intent.getAction();
            LogUtil.i(TAG,"---------onReceivePBMBroadcast:" + action + "----");
            YwDataModel ywData = ShowInfo.getInstance().getYwDataModel();
            dataMsg1.setLength(0);
            if (action.equals(PBMTest.BROADCAST_PBM_UP_BANDWIDTH)) {
                if(appModel.getCurrentTask() == WalkStruct.TaskType.PBM){
                    int up = intent.getIntExtra(PBMTest.EXTRA_PBM_UP_BANDWIDTH,-9999);

                    String PBMQosUpBandwidth = String.valueOf(up);// 上行带宽

                    dataMsg1.append("TestKind=51");
                    dataMsg1.append(",PBMDlSpeed=-9999");
                    dataMsg1.append(",PBMUlSpeed=" + (TextUtils.isEmpty(PBMQosUpBandwidth) ? "-9999" : PBMQosUpBandwidth));
                    dataMsg1.append( ",AliveThreads=" + ywData.getFtpActivityThread());
                    dataMsg1.append( ",Point_Time=" + System.currentTimeMillis());
                    WriteRealTimeParamP(WalkStruct.UMPCEventType.RealTimeData.getUMPCEvnetType(), dataMsg1.toString());
                }
            } else if (action.equals(PBMTest.BROADCAST_PBM_DOWN_BANDWIDTH)) {
                if(appModel.getCurrentTask() == WalkStruct.TaskType.PBM){
                    int down = intent.getIntExtra(PBMTest.EXTRA_PBM_DOWN_BANDWIDTH,-9999);
                    String PBMQosDownBandwidth = String.valueOf(down);// 下行带宽
                    dataMsg1.append("TestKind=51");
                    dataMsg1.append(",PBMDlSpeed="  + (TextUtils.isEmpty(PBMQosDownBandwidth) ? "-9999" : PBMQosDownBandwidth));
                    dataMsg1.append(",PBMUlSpeed=-9999");
                    dataMsg1.append(",AliveThreads=" + ywData.getFtpActivityThread());
                    dataMsg1.append(",Point_Time=" + System.currentTimeMillis());
                    WriteRealTimeParamP(WalkStruct.UMPCEventType.RealTimeData.getUMPCEvnetType(), dataMsg1.toString());
                }
            }
        }
    };

    /**
     * 电信巡检版本需要上传生成的文件信息给pad端，pad端上传给服务器
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewFileFinishEvent(TestFileDataEvent event){
        boolean isUmpcLogined = appModel.getUmpcStatus() == UMPCConnectStatus.TerminalLoginSucces;
        LogUtil.i(TAG,"---------onNewFileFinishEvent:" + event.getData() + ",isUmpcLogined:" + isUmpcLogined + "---------");
        if (isUmpcLogined) {
            byte[] cmdByteArray = DataTypeChangeHelper.int2byte4(1006);
            TransparentNotifyResp(DataTypeChangeHelper.byteMerger(cmdByteArray, event.getData().getBytes()));
        }
    }

    @Override
    public void onDestroy() {
        LogUtil.w(TAG, "---onDestroy---");
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        stopServer();

        RefreshEventManager.removeRefreshListener(this);
        AlertWakeLock.release();

        if (umpcEventChangeReceiver != null) {
            unregisterReceiver(umpcEventChangeReceiver);
            umpcEventChangeReceiver = null;
        }

        appModel.setUmpcRunning(false);
        appModel.setUmpcStatus(UMPCConnectStatus.Default);
        if (this.mActionSwitchThread != null) {
            this.mActionSwitchThread.stopThread();
            this.mActionSwitchThread = null;
        }

        umpcLogined = false;
        unRegisterReceiver();
    }

    /**
     * 初始化当前服务应用环境
     */
    private void initEnv() {
        appModel = ApplicationModel.getInstance();
        deviceInfo = Deviceinfo.getInstance();
        gpsInfo = GpsInfo.getInstance();
        myPhone = new MyPhone(this);
        //umpcEnvModel = ConfigUmpc.getInstance().getUmpcModel();
        phoneState = MyPhoneState.getInstance();
    }

    /**
     * 注册UMPC状态改变消息
     */
    private void registerRecv() {
        IntentFilter filterIntent = new IntentFilter();
        filterIntent.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filterIntent.addAction(Intent.ACTION_BATTERY_CHANGED);            //电池状态
        filterIntent.addAction(GpsInfo.gpsLocationChanged);
        filterIntent.addAction(WalkMessage.MutilyTester_ReDo_BandWifi);    //重新绑定WIFI动作
        filterIntent.addAction(WalkMessage.ACTION_WALKTOUR_SCANNERTIMER_CHANGED);
        filterIntent.addAction(WalkMessage.testDataUpdate);
        filterIntent.addAction(WalkMessage.FtpTest_End_Logmask);
        filterIntent.addAction(WalkMessage.UMPC_WriteRealTimeEvent);
        filterIntent.addAction(WalkMessage.NOTIFY_TESTJOBDONE);
        filterIntent.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE);
        filterIntent.addAction(WalkMessage.ACTION_UNIT_SYNC_START);    //同步消息
        filterIntent.addAction(WalkMessage.ACTION_UNIT_NORMAL_SEND);    //常规消息
        filterIntent.addAction(WalkMessage.ToEncryptRcuFile);            //开始加密RCU文件
        filterIntent.addAction(WalkMessage.umpcTestAutoUploadFile);
        filterIntent.addAction(WalkMessage.rcuEventSend2Pad);            //发送标准事件
        filterIntent.addAction(WalkMessage.traceL3MsgChanged);            //往IPAD发送层三信令
        filterIntent.addAction(WalkMessage.ACTION_SEND_STATIC2PAD);        //发送统计数据
        filterIntent.addAction(WalkMessage.MutilyTester_Send_Event);    //发送同步字符串
        filterIntent.addAction(WalkMessage.ACTION_UNIT_MOS_BOX_INIT);//MOS盒测试开始消息
        filterIntent.addAction(WalkMessage.ACTION_UNIT_MOS_BOX_TEST);//MOS盒测试开始消息
        this.registerReceiver(umpcEventChangeReceiver, filterIntent);
    }

    private class StartLibService implements Runnable {
        //与iPack通信的默认端口
        private int CmdPort=22222;
        private int DataPort=33333;
        private int CtrlPort=55555;
        //文件名
        private String fileName="iPackTerminal_port_control.txt";

        /***
         * 构造器
         */
        public StartLibService()
        {
            try {
                //默认是55555,但是配置了的话,就是用配置的
                CtrlPort = Integer.parseInt(Deviceinfo.getInstance().getIpackCtrlPort());
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        /***
         * 初始化端口
         */
        private void initPort()
        {

            try {
                //打开文件
                File file = AppFilePathUtil.getInstance().getSDCardBaseFile("setting",fileName);
                if (!file.exists()||file.isDirectory()) {
                    LogUtil.d(TAG, "iPackTerminal_port_control.txt is not exist,use default value.");
                    return;
                }

                StringBuffer stringBuffer = new StringBuffer();
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                    instream.close();
                    instream=null;


                    JSONObject jsonObject=new JSONObject(stringBuffer.toString());

                    CmdPort=jsonObject.getInt("CmdPort");
                    DataPort=jsonObject.getInt("DataPort");
                    CtrlPort=jsonObject.getInt("CtrlPort");
                    jsonObject=null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            SetPath(getFilesDir().getParentFile().getAbsolutePath(), AppFilePathUtil.getInstance().getSDCardBaseDirectory() + "data");
            InitService(getFilesDir().getParentFile().getAbsolutePath() + File.separator + "lib" + File.separator);
            SetLogPath(AppFilePathUtil.getInstance().getSDCardBaseDirectory("liblog"));
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String devId = MyPhoneState.getInstance().getMyDeviceId(getApplicationContext());
            Deviceinfo devInfo = Deviceinfo.getInstance();
            LogUtil.w(TAG, "devId=" + devId + "devInfo.getModuleName()=" + devInfo.getModuleName());
            initPort();
            LogUtil.i(TAG,"---------config port:CmdPort=" + CmdPort+",DataPort="+DataPort+",CtrlPort="+CtrlPort + "-------");
            String jstrParam = String.format(ipackControl.getBeginInitFormat(), devId, devInfo.getModuleName(), ""+CmdPort,
                    //CmdPort 默认22222
                    ""+DataPort,    //DataPort 默认33333
                    ""+CtrlPort,    //CtrlPort  默认55555
                    MyPhoneState.getInstance().getNetworkOperateName(getApplicationContext()),
                    ConfigNetwork.getInstance().getNetworkType(MyPhoneState.getInstance().getNetworkOperate(getApplicationContext())),
                    devInfo.getWifiDevice(), (ConfigRoutine.getInstance().isWifiModel(getApplicationContext()) ? "12306" : "0"),//"12306",
                    appModel.isScannerTest() ? TermType.Scanner.getTermTypeId() : TermType.Android.getTermTypeId());
            LogUtil.d(TAG,"----send jstrParam to iPack:" + jstrParam + "------");
            BeginService(jstrParam);    //如果当前为USB方式的话,该值为0 WIFI 12306

            appModel.setUmpcStatus(UMPCConnectStatus.ServerCreated);
        }
    }

    private void stopServer() {
        EndService();
        UnInitService();
    }

    /**
     * 当收到系统消息时，通过线程处理与UMPC的交互，避免频繁与UMPC交互而占用主线程
     *
     * @author tangwq
     */
    private class ActionSwitchThread extends Thread {
        Object lock;
        private boolean isStop = false;
        /**
         * 往UMCP交互事件队列
         */
        private Queue<Intent> actionList = new LinkedBlockingQueue<>();
        /**
         * 必须及时处理ACTION队列
         */
        private Queue<Intent> realAList = new LinkedBlockingQueue<>();

        public ActionSwitchThread(Object obj) {
            lock = obj;
        }

        public void run() {
            while (!isStop) {
                //当消息交互列表为空时等等50ms
                if (actionList.isEmpty() && realAList.isEmpty()) {
                    try {
                        if (umpcLogined) {    //如果当前是连接状态，队列无数据的时候等待100毫秒
                            Thread.sleep(100);
                        } else {    //当前无处理信息时，未连接等待1秒
                            Thread.sleep(1000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                //优先处理实时消息队列
                Intent intent = (realAList.isEmpty() ? actionList.remove() : realAList.remove());
                actionSwitchDo(intent);
            }
        }

        public void addRealIndent(Intent intent) {
            this.realAList.add(intent);
        }

        public void addIndent(Intent intent) {
            this.actionList.add(intent);
        }

        public void stopThread() {
            this.isStop = true;
            this.interrupt();
        }

    }

    /**
     * 具体实现每个消息与UMPC的交互
     *
     * @param intent
     * @author tangwq
     */
    private void actionSwitchDo(Intent intent) {
        LogUtil.d(TAG, "----actionSwitchDo----start----action:" + intent.getAction());
        try {
            if (intent.getAction().equals(GpsInfo.gpsLocationChanged) && gpsInfo.getLocation() != null && umpcLogined) {
                Location location = gpsInfo.getLocation();
                /**
                 * 当GPS状态为打开时,获取GPS消息回传到UMPC
                 */
                if (gpsInfo.isUmpcGpsOpen()) {
                    WriteRealTimeParamP('F', "Latitude=" + location.getLatitude() + ",Longitude=" + location
                            .getLongitude());
                    doStopQueryGps();
                }
            } else if (intent.getAction().equals(WalkMessage.MutilyTester_Send_Event) && umpcLogined) {//&& appModel
                // .isTestJobIsRun()
                //回传测试过程中的相关事件信息 只有测试过程中产生的消息，不再加是否正在测试中判断
                String eventMsg = String.format("%s %s", UtilsMethod.sdFormat.format(intent.getLongExtra
                        ("eventTimes", System.currentTimeMillis())), intent.getStringExtra("eventValue"));

                LogUtil.w(TAG, "--sentEvent:" + eventMsg);
                WriteRealTimeParamP(WalkStruct.UMPCEventType.Event.getUMPCEvnetType(), eventMsg);
            } else if (intent.getAction().equals(WalkMessage.ACTION_WALKTOUR_TIMER_CHANGED) && umpcLogined) {
                if (!isTimeChanging) {
                    isTimeChanging = true;
                    //回传解码过程中的相关参数信息&& appModel.isTestJobIsRun()
                    sendRealParaToUMPC();
                    sendRealStateInfoToIpad();
                    isTimeChanging = false;
                }
            } else if (intent.getAction().equals(WalkMessage.ACTION_WALKTOUR_SCANNERTIMER_CHANGED) && umpcLogined) {
                sendRealParaToUMPC();
            } else if (WalkMessage.testDataUpdate.equals(intent.getAction()) && umpcLogined && appModel
                    .isTestJobIsRun() && appModel.getControllerType() != UmpcTestInfo.ControlForPioneer) {
                dataMsg2.setLength(0);
                YwDataModel ywData = ShowInfo.getInstance().getYwDataModel();
                if (appModel.getCurrentTask() == WalkStruct.TaskType.FTPDownload) {
                    dataMsg2.append("TestKind=5,");
                    dataMsg2.append("FTPDlProgress=" + ywData.getFtpDlProgress() + ",FTPDlSpeed=" + (ywData
                            .getFtpDlMeanRate().trim().equals("") ? "" : String.valueOf(Float.parseFloat(ywData
                            .getFtpDlThrput()) * 1000)) + ",FTPUlProgress=-9999,FTPUlSpeed=-9999" + ",AliveThreads="
                            + ywData.getFtpActivityThread() + ",Point_Time=" + System.currentTimeMillis());
                    WriteRealTimeParamP(WalkStruct.UMPCEventType.RealTimeData.getUMPCEvnetType(), dataMsg2.toString());
                } else if (appModel.getCurrentTask() == WalkStruct.TaskType.FTPUpload) {
                    dataMsg2.append("TestKind=6,");
                    dataMsg2.append("FTPDlProgress=-9999,FTPDlSpeed=-9999," + "FTPUlProgress=" + ywData.getFtpUlProgress()
                            + ",FTPUlSpeed=" + (ywData.getFtpUlMeanRate().trim().equals("") ? "" : String.valueOf
                            (Float.parseFloat(ywData.getFtpUlThrput()) * 1000)) + ",AliveThreads=" + ywData
                            .getFtpActivityThread() + ",Point_Time=" + System.currentTimeMillis());
                    WriteRealTimeParamP(WalkStruct.UMPCEventType.RealTimeData.getUMPCEvnetType(), dataMsg2.toString());
                }
                dataMsg2.setLength(0);
            } else if (WalkMessage.FtpTest_End_Logmask.equals(intent.getAction())) {
                dataMsg3.setLength(0);
                dataMsg3.append("FTPDlProgress=-9999,FTPDlSpeed=-9999," + "FTPUlProgress=-9999,FTPUlSpeed=-9999");
                WriteRealTimeParamP(WalkStruct.UMPCEventType.RealTimeData.getUMPCEvnetType(), dataMsg3.toString());
                dataMsg3.setLength(0);
            } else if (intent.getAction().equals(WalkMessage.ACTION_UNIT_SYNC_START)) {
                //同步开始消息
                String msg = intent.getExtras().getString(WalkMessage.KEY_UNIT_MSG);
                /*向服务端发送同步消息
                 *如果主叫和被叫方都发送了相同内容的同步消息后，
                 *回调函数中会返回WalkCommonPara.eNC_SyncMessage,回调的pdata是这里发送的msg*/
                int syncModel = intent.getExtras().getInt(WalkMessage.KEY_UNIT_SYNCMODEL, 0);
                if (syncModel == 0) {
                    SendSyncData(msg, msg.length());
                } else {
                    SendSyncAll(msg, msg.length());
                }
                LogUtil.i(TAG, "send sync message:" + msg + "---model:" + syncModel);
            } else if (intent.getAction().equals(WalkMessage.UMPC_WriteRealTimeEvent)) {
                //接收往UMPC回传实时信息的消息
                char type = intent.getCharExtra(WalkMessage.UMPC_WriteRealTimeType, UMPCEventType.Event
                        .getUMPCEvnetType());
                String info = intent.getStringExtra(WalkMessage.UMPC_WriteRealTimeInfo);
                LogUtil.w(TAG, "--Logined:" + umpcLogined + "--type:" + (String.valueOf(type)) + "--info:" + info);
                if (!info.equals("")) {
                    WriteRealTimeParamP(type, info);
                }
            } else if (intent.getAction().equals(WalkMessage.ACTION_UNIT_NORMAL_SEND)) {
                //要发送常规消息到服务器
                String msg = intent.getExtras().getString(WalkMessage.KEY_UNIT_MSG);
                SendNormalData(msg, msg.length());
                LogUtil.i(TAG, "send normal message:" + msg + "---->");
            } else if (intent.getAction().equals(WalkMessage.NOTIFY_TESTJOBDONE)    //当测试结束时，发送结束状态通知UMPC
                    || intent.getAction().equals(WalkMessage.NOTIFY_INTERRUPTJOBDONE)) {
                LogUtil.w(TAG, "-----send test stop ----");
                SendStopNotify();
            } else if (intent.getAction().equals(WalkMessage.ToEncryptRcuFile)) {
                if (encryptSource.equals(intent.getExtras().getString("EncryptSourceFile")) && encryptTarget.equals
                        (intent.getExtras().getString("EncryptTargetFile"))) {
                    LogUtil.w(TAG, "---reEncrypt Not Dont File:" + encryptTarget + "--source:" + encryptSource);
                } else {
                    encryptSource = intent.getExtras().getString("EncryptSourceFile");
                    encryptTarget = intent.getExtras().getString("EncryptTargetFile");
                    LogUtil.w(TAG, "---encrypt:" + encryptTarget);
                    EncryptRcuFile(encryptSource, encryptTarget);
                }
            } else if (intent.getAction().equals(WalkMessage.umpcTestAutoUploadFile)) {
                String uploadFile = intent.getExtras().getString("AutoUploadFileName");
                LogUtil.w(TAG, "---11doUploadFile:" + uploadFile);
                SendFile(uploadFile);
            } else if (intent.getAction().equals(WalkMessage.traceL3MsgChanged)) {    //当层三信息发生改变时，当成信令回传到UMPC服务器中
                if (intent.getExtras().getString(WalkMessage.traceL3MsgInfo) != null) {
                    String l3Msg = intent.getExtras().getString(WalkMessage.traceL3MsgInfo);
                    String pointIndex = intent.getExtras().getString(WalkMessage.traceIndexPoint);
                    LogUtil.w(TAG, "----write----pointIndex:" + pointIndex + "----l3Msg:" + l3Msg);
                    WriteRealTimeParamP(WalkStruct.UMPCEventType.Single.getUMPCEvnetType(), "[" + pointIndex + "]" + l3Msg);
                }
            } else if (intent.getAction().equals(WalkMessage.MutilyTester_ReDo_BandWifi)) {
                LogUtil.w(TAG, "--redo band wifi--");
            } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                LogUtil.w(TAG, "--wifiNet:" + info.getState());
                //当前WIFI状态为断开，且等待重连线程状态为未运行，启动重连线程
            } else if (intent.getAction().equals(WalkMessage.ACTION_UNIT_MOS_BOX_INIT)) {
                String callType = intent.getStringExtra(WalkMessage.KEY_MOS_BOX_TEST_TYPE);
                int state = intent.getIntExtra(WalkMessage.KEY_MOS_BOX_TEST_STATE, 0);
                int channel = ConfigRoutine.getInstance().getMosBoxChannel(getApplicationContext());
                String mosType = intent.getStringExtra(WalkMessage.KEY_MOS_BOX_TEST_MOSTYPE);
                int sample = intent.getIntExtra(WalkMessage.KEY_MOS_BOX_TEST_SAMPLE, 8000);
                LogUtil.w(TAG, "--MosBoxTest Call:" + callType + "--State:" + state + "--Channel:" + channel +
                        "--Mos:" + mosType + "--Sample:" + sample + "--VRec:" + ConfigRoutine.getInstance()
                        .getMosBoxVRec(getApplicationContext()) + "--VPlay:" + ConfigRoutine.getInstance()
                        .getMosBoxVPlay(getApplicationContext()));

                MosInit(mosType, sample, channel, ConfigRoutine.getInstance().getMosBoxVRec(getApplicationContext()),
                        ConfigRoutine.getInstance().getMosBoxVPlay(getApplicationContext()), ConfigRoutine
                                .getInstance().getCallMosBoxLowMos(getApplicationContext()), MyPhoneState.getInstance
                                ().getDeviceId(getApplicationContext()));
            } else if (intent.getAction().equals(WalkMessage.ACTION_UNIT_MOS_BOX_TEST)) {
                String callType = intent.getStringExtra(WalkMessage.KEY_MOS_BOX_TEST_TYPE);
                int state = intent.getIntExtra(WalkMessage.KEY_MOS_BOX_TEST_STATE, 0);
                int channel = ConfigRoutine.getInstance().getMosBoxChannel(getApplicationContext());
                String mosType = intent.getStringExtra(WalkMessage.KEY_MOS_BOX_TEST_MOSTYPE);
                int sample = intent.getIntExtra(WalkMessage.KEY_MOS_BOX_TEST_SAMPLE, 8000);
                LogUtil.w(TAG, "--MosBoxTest Call:" + callType + "--State:" + state + "--Channel:" + channel +
                        "--Mos:" + mosType + "--Sample:" + sample);

                MosTest(callType, channel, state);
            }
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                //获取当前电量
                int clevel = intent.getIntExtra("level", 0);

                LogUtil.w(TAG, "currentLevel=" + currentLevel);
                if (clevel != currentLevel) {
                    currentLevel = clevel;
                    uploadLevel();
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----actionSwitchDo----end----action:" + intent.getAction());
    }

    private static int realParaTimes = 0;    //用于监控UMPC实时

    private void WriteRealTimeParamP(char chr, String info) {
        //LogUtil.w("Para"+TAG,"--umpcLogined:"+umpcLogined+"--type:"+(int)chr+"--"+String.valueOf(chr)+"--"+info);
        if (umpcLogined) {    //只有登陆成功后才能回传UMPC信息
            realParaTimes++;
            WriteRealTimeParam(chr, info);
            info=null;
        }
    }

    int i = 0;

    /**
     * 发送实时消息到UMPC端
     *
     * @author tangwq
     */
    private void sendRealParaToUMPC() {
        /**
         * 暂时根据权限判断回传数据
         * scanner往ipad回传界面显示参数
         */
        if (appModel.isScannerTest()) {

            String cwCallBackStr = UmpcSwitchMethod.getScannerCallBackParm(TestSchemaType.GSMCW);
            if (cwCallBackStr.length() != 0) {
                WriteRealTimeParamP(UMPCEventType.Scanner.getUMPCEvnetType(), cwCallBackStr);
            }
            String colorCallBackStr = UmpcSwitchMethod.getScannerCallBackParm(TestSchemaType.GSMCOLORCODE);
            if (colorCallBackStr.length() != 0) {
                WriteRealTimeParamP(UMPCEventType.Scanner.getUMPCEvnetType(), colorCallBackStr);
            }
            String cPilotCallBackStr = UmpcSwitchMethod.getScannerCallBackParm(TestSchemaType.CDMAPILOT);
            if (cPilotCallBackStr.length() != 0) {
                WriteRealTimeParamP(UMPCEventType.Scanner.getUMPCEvnetType(), cPilotCallBackStr);
            }
            String wPilotCallBackStr = UmpcSwitchMethod.getScannerCallBackParm(TestSchemaType.WCDMAPILOT);
            if (wPilotCallBackStr.length() != 0) {
                WriteRealTimeParamP(UMPCEventType.Scanner.getUMPCEvnetType(), wPilotCallBackStr);
            }
            String tPilotCallBackStr = UmpcSwitchMethod.getScannerCallBackParm(TestSchemaType.TDSCDMAPILOT);
            if (tPilotCallBackStr.length() != 0) {
                WriteRealTimeParamP(UMPCEventType.Scanner.getUMPCEvnetType(), tPilotCallBackStr);
            }
            String lPilotCallBackStr = UmpcSwitchMethod.getScannerCallBackParm(TestSchemaType.LTEPILOT);
            if (lPilotCallBackStr.length() != 0) {
                WriteRealTimeParamP(UMPCEventType.Scanner.getUMPCEvnetType(), lPilotCallBackStr);
            }
        }

        //int networtType = phoneState.getNetWorkType(this) ;
        //当前不在参数查看页,才需要重新查询参数(如果强制切换到其实网络页,当前参数不实时)
        boolean notShowParamPage = TraceInfoInterface.currentShowChildTab == ShowInfoType.Default;
        CurrentNetState netState;
        if (notShowParamPage) {
            netState = NetStateModel.getInstance().getCurrentNetTypeSync();
        } else {
            netState = NetStateModel.getInstance().getCurrentNetByHistory();
        }

        switch (netState) {
            case LTE:
                WriteRealTimeParamP(UMPCEventType.NetLTE.getUMPCEvnetType(), UmpcSwitchMethod.getCallBackPara(NetType.LTE.getNetType()));

                //twq20160713 LTE下需要判断是否有CDMA/EVDO网络,如果有需要传相关参数
                if (!TraceInfoInterface.getParaValue(UnifyParaID.CURRENT_STATE_CDMA).equals("") ||
                        !TraceInfoInterface.getParaValue(UnifyParaID.CURRENT_STATE_EVDO).equals("")) {
                    WriteRealTimeParamP(UMPCEventType.NetEVDO.getUMPCEvnetType(), UmpcSwitchMethod.getCallBackPara
                            (NetType.EVDO.getNetType()));
                    WriteRealTimeParamP(UMPCEventType.NetCDMA.getUMPCEvnetType(), UmpcSwitchMethod.getCallBackPara
                            (NetType.CDMA.getNetType()));
                }
                ;
                break;
            case WCDMA:
                WriteRealTimeParamP(UMPCEventType.NetWCDMA.getUMPCEvnetType(), UmpcSwitchMethod.getCallBackPara
                        (NetType.WCDMA.getNetType()));
                break;
            case TDSCDMA:
                WriteRealTimeParamP(UMPCEventType.NetTDSCDMA.getUMPCEvnetType(), UmpcSwitchMethod.getCallBackPara
                        (NetType.TDSCDMA.getNetType()));
                break;
            case GSM:
                WriteRealTimeParamP(UMPCEventType.NetGSM.getUMPCEvnetType(), UmpcSwitchMethod.getCallBackPara(NetType
                        .GSM.getNetType()));
                break;
            case CDMA:
                WriteRealTimeParamP(UMPCEventType.NetEVDO.getUMPCEvnetType(), UmpcSwitchMethod.getCallBackPara
                        (NetType.EVDO.getNetType()));
                WriteRealTimeParamP(UMPCEventType.NetCDMA.getUMPCEvnetType(), UmpcSwitchMethod.getCallBackPara
                        (NetType.CDMA.getNetType()));
                break;
            default:
                LogUtil.w(TAG, "---networtType:" + netState.getCurrentNetId() + "--netType:" + deviceInfo.getNettype());
                break;
        }
    }

    /**
     * 测试过程中往IPAD端发端实时统计信息
     */
    private void sendRealStateInfoToIpad() {
        if (appModel.isTestJobIsRun()) {
            WriteRealTimeParamP(UMPCEventType.RealTimeData.getUMPCEvnetType(), forIpadInfo.getCurrentLoopStr());
        }
    }

    /**
     * 接收应该程序运行过程中的相关消息
     */
    private BroadcastReceiver umpcEventChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            //LogUtil.w(TAG,"--allevt:" + intent.getAction());

            /**
             * UMPC登陆成功或者开始测试后的所有收到的消息存入线程队列中，等线程处理
             * 可以保证测试过程中因为UMPC断开而丢显示事件
             */
            //对于文件加密及重新绑定WIFI动作，不判断当前是否连接
            if (intent.getAction().equals(WalkMessage.ToEncryptRcuFile) || intent.getAction().equals(WalkMessage
                    .MutilyTester_ReDo_BandWifi) || intent.getAction().equals(WifiManager
                    .NETWORK_STATE_CHANGED_ACTION) || intent.getAction().equals(WalkMessage.ACTION_UNIT_MOS_BOX_INIT)
                    || intent.getAction().equals(WalkMessage.ACTION_UNIT_MOS_BOX_TEST)) {
                LogUtil.w(TAG, "---realtime disponse event:" + intent.getAction());
                if (mActionSwitchThread != null)
                    mActionSwitchThread.addRealIndent(intent);
            } else if (intent.getAction().equals(WalkMessage.rcuEventSend2Pad)) {
                LogUtil.w(TAG, "---" + intent.getAction());
                byte[] eventBytes = intent.getByteArrayExtra("bytes");
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < eventBytes.length; i++) {
                    String hex = Integer.toHexString(eventBytes[i] & 0xFF);
                    if (hex.length() == 1) {
                        hex += "0";
                    }
                    sb.append(hex + " ");
                }
                LogUtil.i(TAG, sb.toString());
                WriteRealTimeParamBytes(UMPCEventType.Event.getUMPCEvnetType(), eventBytes, eventBytes.length);

            } else if (intent.getAction().equals(WalkMessage.ACTION_SEND_STATIC2PAD)) {
                final String fileName = intent.getStringExtra("file");
                new Thread() {
                    public void run() {
                        //生成统计SQL语句文件
                        String fileFullPath = FileDB.getInstance(getApplicationContext()).getTotalSQL(fileName,
                                MyPhoneState.getInstance().getDeviceId(getApplicationContext())).getAbsolutePath();
                        SendFileStat(fileFullPath);
                    }
                }.start();
            } else if (mActionSwitchThread != null && umpcLogined) {    //如果当前断开再记录的话，会导致内存溢出及关键消息因时延问题而错乱
                if (intent.getAction().equals(WalkMessage.MutilyTester_Send_Event) || intent.getAction().equals
                        (WalkMessage.ACTION_UNIT_SYNC_START) || intent.getAction().equals(WalkMessage
                        .ACTION_UNIT_NORMAL_SEND) || intent.getAction().equals(WalkMessage.umpcTestAutoUploadFile)) {
                    LogUtil.w(TAG, "---realtime disponse event:" + intent.getAction());
                    mActionSwitchThread.addRealIndent(intent);
                } else {
                    mActionSwitchThread.addIndent(intent);
                }
            } else {
                //LogUtil.w(TAG, "--lost event:" + intent.getAction() + "--connecte:" + umpcLogined);
            }
        }
    };

    static {
        System.loadLibrary("iPackTerminal");
    }

    public String m_strResult = "OK";        //JNI回调函数的处理结果，在回调函数之前赋为无效值 -1
    public byte[] m_szbuff;                //收到数据透传时,通过此变量下发变化值
    public byte[] m_mosvalue;            //MOS盒测试时,ipack端会将算分拼好的结果透传
    public byte[] m_szGyroGps;            //MOS盒测试时,ipack端会将算分拼好的结果透传

    /**
     * UMPC服务端回调功能
     *
     * @param notifycode 回调事件类型
     * @param pdata      回调相关内容
     * @return
     */
    public void evtcallback(int notifycode, String pdata) {
        LogUtil.w(TAG, "----notifycode:" + notifycode + "----str:" + pdata);
        try {
            m_strResult = "OK";
            switch (notifycode) {
                case WalkCommonPara.eNI_LoadLib:            //加载库失败
                    LogUtil.w(TAG, "--load lib faild--");
                    break;
                case WalkCommonPara.eNC_Error:                //错误信息，pdata为char*，错误信息
                    break;
                case WalkCommonPara.eNC_Connect:            //连接成功或失败，pdata为int，1为成功，0为失败
                    if (UtilsMethod.getEqualsValue(pdata).equals("1")) {
                        appModel.setUmpcStatus(UMPCConnectStatus.TerminalConnected);
                    } else {
                        umpcLogined = false;
                        if (umpcEventChangeReceiver != null) {
                            appModel.setUmpcStatus(UMPCConnectStatus.TerminalConnectFaild);
                        } else {
                            appModel.setUmpcStatus(UMPCConnectStatus.Default);
                        }
                    }
                    appModel.setUmpcRunning(umpcLogined);
                    appModel.setUmpcTest(true);
                    sendBroadcast(new Intent(WalkMessage.ACTION_BLUE_CONNECT_STATE_CHANGE));
                    break;
                case WalkCommonPara.eNC_Login:                //登陆成功或失败，pdata为int，1为成功，0为失败
                    String[] result = pdata.split(",");
                    if (UtilsMethod.getEqualsValue(result[0]).equals("1")) {
                        appModel.setUmpcStatus(UMPCConnectStatus.TerminalLoginSucces);
                        umpcLogined = true;
                        uploadLevel();
                        LogUtil.w(pdata, "iPackTerminal is Login Success.");
                    } else {
                        umpcLogined = false;
                        appModel.setUmpcStatus(UMPCConnectStatus.TerminalLoginFaild);
                        LogUtil.w(pdata, "iPackTerminal is Login Failure.");
                    }
                    appModel.setUmpcTest(true);
                    appModel.setUmpcRunning(umpcLogined);
                    //如果返回结果中带有多个参数，需作其它处理
                    if (result.length > 1) {
                        for (int i = 1; i < result.length; i++) {
                            if (result[i].startsWith("key=")) {
                                encryptCode = result[i].substring(4);
                                LogUtil.w(TAG, "---login call back key:" + encryptCode);
                                //encryptCode =
                                // "DLHK-FSJVR-FTOF4-ORNCF-YP43X-6TKKW-VTAZI-2VWZK-CJTHA-5IRDB-IBQEY-2CELW-OHAXC-5NGQF
                                // -Q6KMP-GMETH-ORZBE-GCWDE-ORAGA-TNIJR-EFKTN-EOROW-AUNIJ-WS5LY-CQJV4-7CDLJ-MU6MK
                                // -KDYIF-G3A5O-YAFML-KUGE2-SIGCF-BRCEC-RCDDU-OUYRS-GBEOU-SQQED-BWBAZ-3ROQL-BCVY2";
                            } else if (result[i].startsWith("controller=")) {
                                controller = result[i].substring(11);

                                LogUtil.w(TAG, "--controller:" + controller);
                            } else if (result[i].startsWith("alias=")) {
                                appModel.setTerminalSign(result[i].substring(6));
                            }
                        }
                    }
                    sendBroadcast(new Intent(WalkMessage.ACTION_BLUE_CONNECT_STATE_CHANGE));
                    break;
                case WalkCommonPara.eNC_TimeSync:            //通知已完成时间同步，pdata为NULL
                    umpcLogined = true;
                    LogUtil.w(TAG, "--syncTime:" + pdata);
                    String mccmnc = MobileUtil.getSIM_MCCMNC(getApplicationContext());
                    if (mccmnc.equals("46003") || mccmnc.equals("46011") || mccmnc.equals("46005")) {
                        //do nothing
                    } else {
                        Date syncdate = UtilsMethod.sdFormatss.parse(pdata);
                        UtilsMethod.setTime(syncdate.getTime(), getApplicationContext());
                    }
                    appModel.setUmpcRunning(umpcLogined);
                    TimeSyncNotify(1);
                    break;
                case WalkCommonPara.eNC_TestPlan:            //通知收到测试计划，pdata为char*，测试计划所在的文件名
                    doSetTestPlan(pdata);
                    break;
                case WalkCommonPara.eNC_TestStart:            //开始测试，pdata为StartInfo*
                    gpsCount = 0;
                    doStartTest(pdata);
                    UmpcTestInfo umpcTestInfo=new UmpcTestInfo(pdata);
                    LogUtil.d(TAG,"测试是否室内:"+"CQT".equals(umpcTestInfo.getTestmode()));
                    if ("CQT".equals(umpcTestInfo.getTestmode())){
                        if (umpcTestInfo.getLatitude()==-9999||umpcTestInfo.getLongitude()==-9999){
                            GpsInfo.getInstance().setIpackLatLng(new MyLatLng(0,0));
                        }else {
                            GpsInfo.getInstance().setIpackLatLng(new MyLatLng(umpcTestInfo.getLatitude(),umpcTestInfo.getLongitude()));
                        }
                    }
                    isAddedFirstPoint=false;
                    break;
                case WalkCommonPara.eNC_TestSop:            //停止测试，pdata为NULL
                    doStopTest();
                    break;
                case WalkCommonPara.eNC_HungUp:                //请求挂机，pdata为NULL
                    recordFileINDEX = 0;//每次挂机记录清0
                    //挂机动作
                    myPhone.endCall();
                    break;
                case WalkCommonPara.eNC_StartMos:            //请求播放MOS，pdata为NULL
                    break;
                case WalkCommonPara.eNC_BrekCurTest:        //请求跳过当前测试，pdata为NULL
                    break;
                case WalkCommonPara.eNC_QueryFileList:        //请求文件列表，pdata为NULL，在strRet中填文件列表字符串
                    //twq20150310此方法移到 eNC_RequestUpload 的type=11中执行
                    doRequestUpLoad(pdata);
                    break;
                case WalkCommonPara.eNC_RequestUpload:        //请求文件列表，pdata为请求的文件名
                    LogUtil.w(TAG, "FILEXX=" + pdata);
                    doRequestUpLoad(pdata);
                    break;
                case WalkCommonPara.eNC_UploadFinish:        //上传完成，pdata为char*，请求的文件名
                    doUploadFinish(pdata);
                    break;
                case WalkCommonPara.eNC_RecordGPS:            //请求记录GPS，pdata为GPS原始数据
                    try {
                        String[] args = pdata.split(",");
                        if ((args[2].equals("") && args[4].equals("")) || (Double.parseDouble(args[2]) == 0 && Double.parseDouble(args[4]) == 0)) {

//                            LogUtil.w(TAG, "iPackTerminal RecordGPS --eNC_RecordGPS receive--");
                            break;
                        }
                        int flag = 0x30002;
                        if (!args[1].equals("0")) {
                            double latitude = this.changeGPS(args[2]);
                            double longitude = this.changeGPS(args[4]);
                            long time = System.currentTimeMillis();
                            int secondTime = (int) (time / 1000);

                            gpsCount += 1;
                            LogUtil.w(TAG, "iPackTerminal RecordGPS count=" + gpsCount);
                            EventBytes.Builder(getBaseContext()).addInteger(1).addInteger(secondTime).addInteger((int) (time - secondTime * 1000) * 1000).addDouble(latitude).addDouble(longitude).addSingle(0).addSingle(0).addSingle(0).addInteger(3).addSingle(0).writeGPSToRcu(flag);
                        } else {//等于0时,时间写为0

                            double latitude = this.changeGPS(args[2]);
                            double longitude = this.changeGPS(args[4]);
                            gpsCount += 1;
                            LogUtil.w(TAG, "iPackTerminal RecordGPS count=" + gpsCount);
                            EventBytes.Builder(getBaseContext()).addInteger(1).addInteger(0).addInteger(0).addDouble(latitude).addDouble(longitude).addSingle(0).addSingle(0).addSingle(0).addInteger(3).addSingle(0).writeGPSToRcu(flag);
                        }
                    } catch (Exception e) {
                        LogUtil.w(TAG, "iPackTerminal RecordGPS exception eNC_RecordGPS", e);
                    }
                    break;
                case WalkCommonPara.eNC_StartGPS:            //请求启动GPS，pdata为NULL
                    doQueryGps();
                    break;
                case WalkCommonPara.eNC_StopGPS:            //请求停止GPS，pdata为NULL
                    doStopQueryGps();
                    break;
                case WalkCommonPara.eNC_SyncMessage:        //收到同步消息，pdata为发送的同步字符串，收发一致方可认为成功同步
                    //发送同步完成消息给要同步的双方
                    LogUtil.w(TAG, "--->receive sync message:" + pdata);
                    Intent intent = new Intent(WalkMessage.ACTION_UNIT_SYNC_DONE);
                    intent.putExtra(WalkMessage.KEY_UNIT_MSG, pdata);
                    sendBroadcast(intent);
                    break;
                case WalkCommonPara.eNC_NormalMessage:        //收到组内其他手机的消息，pdata为消息字符串
                    LogUtil.w(TAG, "--->receive normal message:" + pdata);
                    Intent i = new Intent(WalkMessage.ACTION_UNIT_NORMAL_RECEIVE);
                    i.putExtra(WalkMessage.KEY_UNIT_MSG, pdata);
                    sendBroadcast(i);
                    break;
                case WalkCommonPara.eNC_WriteMark:            //收到打点消息，pdata为MarkInfo*
                    //String mark = "$MARK,"+ p.x + ","+(mbmpTest.getHeight()-p.y)+",0";
                    //如果出来的字符串已拼好，可以直接写入RCU中
                    LogUtil.w(TAG, "----WriteMark:" + pdata);
                    String[] addMak = pdata.split(",");
                    String ax = addMak[0].substring(2);
                    String ay = addMak[1].substring(2);
                    if(!isAddedFirstPoint){
                        writeGPSBeforeFirstPoint(UtilsMethod.MARKSTATE_ADD,true);
                        isAddedFirstPoint=true;
                    }
                    //新RCU结构体
                    long time = System.currentTimeMillis();
                    int secondTime = (int) (time / 1000);
                    int flag = 0x30007;
                    float northShift = Float.parseFloat(ay);
                    float eastShift = Float.parseFloat(ax);
                    MyLatLng latlog = GpsInfo.getInstance().getIpackLatLng();
                    LogUtil.d(TAG,"写入点为"+latlog);
                    EventBytes.Builder(this).addInteger(2)//int GPSPointType; 0：陀螺仪；2：手工打点；3：取消手工打点
                            .addInteger(secondTime)//unsigned int Second;
                            .addInteger((int) (time - secondTime * 1000) * 1000)//unsigned int uSecond;
                            .addDouble(latlog==null?0:latlog.longitude)//double dLon;  定点经度（建筑物等）
                            .addDouble(latlog==null?0:latlog.latitude)//double dLat;  定点纬度（建筑物等）
                            .addSingle(0)//float Altitude;	定点海拔（建筑物等）
                            .addSingle(northShift)//float Altitude;	float NorthShift; 南北偏移距离（单位：米）
                            .addSingle(eastShift)//float Altitude;	float EastShift;  东西偏移距离（单位：米）
                            .addSingle(0)//float Altitude;  float HeightShift; 上下偏移距离（单位：米）
                            .addSingle(0)//float Altitude;	float Angle;  角度
                            .writeToRcu(flag);
                    break;
                case WalkCommonPara.eNC_DelMark:            //收到删点消息，pdata为GpsPt*
                    LogUtil.w(TAG, "----DelMark:" + pdata);
                    String[] delMak = pdata.split(",");
                    String dx = delMak[0].substring(2);
                    String dy = delMak[1].substring(2);

                    /*StringBuffer delMark = UtilsMethod.buildMarkStr(UtilsMethod.MARKSTATE_DEL, Double.parseDouble(dx)
                            , Double.parseDouble(dy));
                    DatasetManager.getInstance(getApplicationContext()).pushData(WalkCommonPara.MsgDataFlag_D, -1, 0,
                            System.currentTimeMillis() * 1000, delMark.toString().getBytes(), delMark.length());*/
                    //新RCU结构体
                    long timeDel = System.currentTimeMillis();
                    int secondTimeDel = (int) (timeDel / 1000);
                    int flagDel = 0x30007;
                    float northShiftDel = Float.parseFloat(dy);
                    float eastShiftDel = Float.parseFloat(dx);

                    EventBytes.Builder(this).addInteger(3)//int GPSPointType; 0：陀螺仪；2：手工打点；3：取消手工打点
                            .addInteger(secondTimeDel)//unsigned int Second;
                            .addInteger((int) (timeDel - secondTimeDel * 1000) * 1000)//unsigned int uSecond;
                            .addDouble(0)//double dLon;  定点经度（建筑物等）
                            .addDouble(0)//double dLat;  定点纬度（建筑物等）
                            .addSingle(0)//float Altitude;	定点海拔（建筑物等）
                            .addSingle(northShiftDel)//float Altitude;	float NorthShift; 南北偏移距离（单位：米）
                            .addSingle(eastShiftDel)//float Altitude;	float EastShift;  东西偏移距离（单位：米）
                            .addSingle(0)//float Altitude;  float HeightShift; 上下偏移距离（单位：米）
                            .addSingle(0)//float Altitude;	float Angle;  角度
                            .writeToRcu(flagDel);
                    break;
                case WalkCommonPara.eNC_EncryptFinish:        //文件加密结果回传
                    appModel.setEncryFileResult(Integer.parseInt(UtilsMethod.getEqualsValue(pdata)));
        		/*Intent encryResult = new Intent(WalkMessage.EncryptFileResult);
        		encryResult.putExtra("EncryptFileResult",Integer.parseInt(UtilsMethod.getEqualsValue(pdata)));
        		sendBroadcast(encryResult);*/
                    LogUtil.w(TAG, "----send EncryResult end----");
                    break;
                case WalkCommonPara.eNI_DeleteFile:
                    doDeleteFile(pdata);
                    break;
                case WalkCommonPara.eNC_StartTrace:
                    Intent startToPinner = new Intent(WalkMessage.rcuFileUpToPinner);
                    startToPinner.putExtra(WalkMessage.rcuFitlToPinnerFlag, WalkCommonPara.RcuFileToPinner_Start);
                    sendBroadcast(startToPinner);
                    break;
                case WalkCommonPara.eNC_TransTrace:
                    break;
                case WalkCommonPara.eNC_StopTrace:
                    Intent stopToPinner = new Intent(WalkMessage.rcuFileUpToPinner);
                    stopToPinner.putExtra(WalkMessage.rcuFitlToPinnerFlag, WalkCommonPara.RcuFileToPinner_Stop);
                    sendBroadcast(stopToPinner);
                    break;
                case WalkCommonPara.eNI_FileStatUpload:
                    LogUtil.i(TAG, "--stat File:" + pdata);
                    String fullPath = FileDB.getInstance(getApplicationContext()).getTotalSQL(pdata, MyPhoneState
                            .getInstance().getDeviceId(getApplicationContext())).getAbsolutePath();
                    SendFileStat(fullPath);
                    break;
                case WalkCommonPara.eNC_SyncAlias:
                    appModel.setTerminalSign(pdata);
                    sendBroadcast(new Intent(WalkMessage.ACTION_BLUE_CONNECT_STATE_CHANGE));
                    break;
                case WalkCommonPara.eNC_Suspend:        //暂停测试
                    appModel.setTestPause(true);
                    sendBroadcast(new Intent(WalkMessage.Action_Walktour_Test_Puase));
                    TestSuspendNotify(1);
                    break;
                case WalkCommonPara.eNC_Resume:            //继续测试
                    appModel.setTestPause(false);
                    sendBroadcast(new Intent(WalkMessage.Action_Walktour_Test_Continue));
                    TestResumeNotify(1);
                    break;
                case WalkCommonPara.eNC_Transparent:    //透传接口
                    ipadTransparent();
                    break;
                case WalkCommonPara.eNC_MosInit:
                    break;
                case WalkCommonPara.eNC_MosTest:

                    break;
                case WalkCommonPara.eNC_MosScore:
                    //MOS盒算分结果写入语音事件中
                    writeMosValue();
                    break;
                case WalkCommonPara.eNC_CtrlConn:    //控制通道连接状态通知 ："result=%d" ，1为连接，0为断开；
                    if (pdata.equals("result=1")) {
                        sendBroadcast(new Intent(WalkMessage.ACTION_UNIT_MOS_RESET_BOX));
                    }
                    break;
                case WalkCommonPara.eNC_ULFleet://
                    enableData();
                    String commx = new String(m_szbuff);
                    LogUtil.w(TAG, "valuexx=command=" + commx + ",length=" + commx.length());
                    if (null != commx && commx.trim().startsWith("Command=PlatformInfo")) {//fleet ip及端口信息
                        parseFleetInfo(commx);
                    } else if (null != commx && commx.trim().startsWith("Command=UploadToPlatform")) {//fleet 上传文件
                        for (int w = 0; w < 10; w++) {
                            Thread.sleep(1000);
                        }
                        parseFleetUploadFile(commx);
                    } else if (null != commx && commx.trim().startsWith("Command=CancelUploadToPlatform")) {// fleet 取消文件上传
                        ServerManager server = ServerManager.getInstance(this);
                        server.uploadFile(this, WalkStruct.ServerOperateType.stopUpload, uploadFiles);
                        uploadFiles.clear();
                        ULFleetResp("Command=CancelUploadToPlatform\r\nError=Cancel\r\n");
                    }
                    break;
                case WalkCommonPara.eNC_RecordBegin://记录文件开始42
                    if (appModel.isPioneer()) {//特殊处理，当出现异常情况时，原来已经创建文件，又发创建文件的情形.
                        DataSetFileUtil.getInstance().sendCloseRcuFileAddToDB(this); //先关闭文件
                    }
                    appModel.setPioneer(true);
                    DataSetFileUtil.getInstance().createFile(this, SceneType.MultiTest.getSceneTypeId(), pdata, true);
                    this.SendBeginRecordNotify();
                    break;
                case WalkCommonPara.eNC_RecordEnd://记录文件结束43
                    if (appModel.isPioneer()) {
                        DataSetFileUtil.getInstance().sendCloseRcuFileAddToDB(this);
                        appModel.setPioneer(false);
                    }
                    this.SendEndRecordNotify();
                    break;
                case WalkCommonPara.eNC_GyroGps:
                    parseGryoGPS();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 新结构的插点第一个点必须插入一个X,Y,Z坐标都为0的点
     * @param status
     * @param isAddNewMark
     */
    private void writeGPSBeforeFirstPoint(int status, boolean isAddNewMark) {
        if(status == UtilsMethod.MARKSTATE_ADD && isAddNewMark ){

            //新结构的插电第一个点必须插入一个X,Y,Z坐标都为0的点
            long time = System.currentTimeMillis();
            int secondTime = (int) (time / 1000);
            int flag = 0x30007;
            MyLatLng location = GpsInfo.getInstance().getIpackLatLng();
            LogUtil.d(TAG,"开始插入第一个点:"+location);
            EventBytes.Builder(this).addInteger(2)//int GPSPointType; 0：陀螺仪；2：手工打点；3：取消手工打点
                    .addInteger(secondTime)//unsigned int Second;
                    .addInteger((int) (time - secondTime * 1000) * 1000)//unsigned int uSecond;
                    .addDouble(null == location ? 0 : location.getLongitude())//double dLon;  定点经度（建筑物等）
                    .addDouble(null == location ? 0 : location.getLatitude())//double dLat;  定点纬度（建筑物等）
                    .addSingle(0)//float Altitude;	定点海拔（建筑物等）
                    .addSingle(0)//float NorthShift; 南北偏移距离（单位：米）
                    .addSingle(0)//float EastShift;  东西偏移距离（单位：米）
                    .addSingle(0)//float HeightShift; 上下偏移距离（单位：米）
                    .addSingle(0)//float Angle;  角度
                    .writeToRcu(flag);
            MapFactory.getMapData().setIndoorTestFirstPoint(false);
        }
    }
    /**
     * 解析陀螺仪GPS
     */
    private void parseGryoGPS() {
        int length = 8;
        int second=ByteUitils.getInt(ByteUitils.getSubBytes(m_szGyroGps,4,4),0);
        int usecond=ByteUitils.getInt(ByteUitils.getSubBytes(m_szGyroGps,8,4),0);
        double speed = ByteUitils.getDouble(ByteUitils.getSubBytes(m_szGyroGps, m_szGyroGps.length - length, 8));//速度
        double longitude = ByteUitils.getDouble(ByteUitils.getSubBytes(m_szGyroGps, m_szGyroGps.length - (length + 4 + 4 + 4 + 4 + 4 + 8), 8));//经度
        double latitude = ByteUitils.getDouble(ByteUitils.getSubBytes(m_szGyroGps, m_szGyroGps.length - (length + 4 + 4 + 4 + 4 + 4 + 8 + 8), 8));//纬度
        if (latitude == 0 && longitude == 0)
            return;
        if (latitude < -90 || latitude > 90)
            return;
        if (longitude < -180 || longitude > 180)
            return;
        LogUtil.d(TAG, "second："+second+"usecond"+usecond+"speed=" + speed + "，latitude=" + latitude + "，longitude=" + longitude);
        //写GPS数据
        EventBytes.Builder(getBaseContext()).addInteger(1).addInteger(second).addInteger(usecond).addDouble(latitude).addDouble(longitude).addSingle((float) speed).addSingle(0).addSingle(0).addInteger(3).addSingle(0).writeGPSToRcu(0x30002);

    }

    private void enableData() {
        if (!APNOperate.getInstance(this).checkNetWorkIsConnected()) {
            LogUtil.w(TAG, "setenable true");
            APNOperate.getInstance(this).setMobileDataEnabled(true, "", true, 1000 * 15);
        } else {
            LogUtil.w(TAG, "data is enable");
        }
    }

    /**
     * 解析从平板下发的FLEET信息
     */
    private void parseFleetInfo(String commx) {
        String[] infos = commx.split("\r\n");
        if (infos.length >= 4) {//解析成功
            String fleetType = infos[1];
            String fleetIP = infos[2];
            String fleetPORT = infos[3];
            LogUtil.w(TAG, "fleetType=" + fleetType);
            ServerManager server = ServerManager.getInstance(this);
            if (fleetType.split("=")[1].toLowerCase().trim().equals("atu")) {
                server.setUploadServer(ServerManager.SERVER_ATU);
                server.setDTLogPort(Integer.parseInt(fleetPORT.split("=")[1]));
            } else if (fleetType.split("=")[1].toLowerCase().trim().equals("btu")) {
                server.setUploadServer(ServerManager.SERVER_BTU);
                server.setDTLogPort(Integer.parseInt(fleetPORT.split("=")[1]));
            } else {
                server.setUploadServer(ServerManager.SERVER_FLEET);
                server.setUploadFleetPort(Integer.parseInt(fleetPORT.split("=")[1]));
            }
            server.setUploadFleetIp(fleetIP.split("=")[1]);


            if (infos.length >= 5) {
                String boxID = infos[4];
                LogUtil.w(TAG, "boxID=" + boxID);
                server.setDTLogBoxId(boxID.split("=")[1]);
            }
            ULFleetResp("Command=PlatformInfo\r\nResulte=OK\r\n");
        } else {
            ULFleetResp("Command=PlatformInfo\r\nResulte=ERROR\r\n");
        }
    }

    private void parseFleetUploadFile(String fileIDS) {
        uploadFiles.clear();
        String[] infos = fileIDS.split("\r\n");
        if (infos.length >= 2) {//解析成功

            ArrayList<DataModel> modelList = new ArrayList<DataModel>();
            DBManager mDbManager = DBManager.getInstance(this);
            ArrayList<DataModel> tmpList = mDbManager.getAllFiles(SceneType.MultiTest);
            for (DataModel d : tmpList) {
                for (DataModel dataLevel2 : d.getChild()) {
                    if (dataLevel2.isFolder) {
                        modelList.addAll(dataLevel2.getChild());
                    } else {
                        modelList.add(dataLevel2);
                    }
                }
            }
//            for (int index=1;index<infos.length;index++) {//组装上传文件，一般只有一个文件
            fileID = infos[1].split("=")[1];
            String groupInfo = infos[2].split("=")[1];
            //要实际处理下
            for (int i = 0; i < modelList.size(); i++) {
                Set<FileType> fileTypes = new HashSet<FileType>();
                DataModel model = modelList.get(i);
                UploadFileModel file = new UploadFileModel(model.testRecord.record_id, model.testRecord.test_type);
                file.setName(model.testRecord.file_name);
                boolean isExist = false;
                for (int j = 0; j < model.testRecord.getRecordDetails().size(); j++) {

                    RecordDetail detail = model.testRecord.getRecordDetails().get(j);
                    if (!isExist) {
                        if (detail.file_guid.equals(fileID)) {
                            isExist = true;
                        }
                    }
                    if (
//                            detail.file_type == FileType.RCU.getFileTypeId()
//                            ||
                    detail.file_type == FileType.DDIB.getFileTypeId()
                            || detail.file_type == FileType.DTLOG.getFileTypeId()
                            || detail.file_type == FileType.DCF.getFileTypeId()
                            || detail.file_type == FileType.ORGRCU.getFileTypeId()
                            || detail.file_type == FileType.ECTI.getFileTypeId()) {

                        file.setParentPath(detail.file_path);
                    }
                    if (detail.file_guid.equals(fileID)) {
                        for (FileType fileType : FileType.values()) {
                            if (detail.file_name.toLowerCase().endsWith(fileType.getFileTypeName())) {
                                fileTypes.add(fileType);
                            }
                        }
                    }
                }
                if (isExist) {
                    file.setFileTypes(fileTypes);
                    file.addExtraParam("GroupID", groupInfo + "");
                    uploadFiles.add(file);
                }
            }

            if (uploadFiles.size() > 0) {
                FleetUpBegin("FILEID=" + fileID + "\r\n");//上传开始
                ServerManager server = ServerManager.getInstance(this);
                for (UploadFileModel upf : uploadFiles) {
                    LogUtil.w(TAG, "upload test record id is:" + upf.getTestRecordId());
                    for (File ff : upf.getUploadFiles()) {
                        LogUtil.w(TAG, "upload test record id is:==" + ff.getName());
                    }
                }
                server.uploadFile(this, WalkStruct.ServerOperateType.uploadTestFile, uploadFiles);
            } else {
                LogUtil.w(TAG, "uploadFiles.size is null");
            }
        }
    }

    private void registReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DataTransService.EXTRA_DATA_TRANS_FILE_PROGRESS);
        intentFilter.addAction(DataTransService.EXTRA_DATA_TRANS_FILE_END);
        intentFilter.addAction(DataTransService.EXTRA_DATA_TRANS_END);
        intentFilter.addAction(WalkMessage.NOTIFY_TEST_IPACK_STOPED);
        registerReceiver(mUploadStateReceiver, intentFilter);
    }

    protected void unRegisterReceiver() {
        try {
            if (null != mUploadStateReceiver) {
                unregisterReceiver(mUploadStateReceiver);
                mUploadStateReceiver = null;
            }
            if(null != mPBMBroadcastReceiver){
                unregisterReceiver(mPBMBroadcastReceiver);
                mPBMBroadcastReceiver = null;
                hasRegisteredPBMBroadcast = false;
            }
        } catch (Exception e) {
            LogUtil.w(TAG, e.getMessage());
        }
    }

    private BroadcastReceiver mUploadStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, android.content.Intent intent) {

            String action = intent.getAction();
            System.out.println("fragmentBase 接收到更新上传进度广播action:" + action);
            ServerManager server = ServerManager.getInstance(context);
            UploadFileModel item = intent.getParcelableExtra(DataTransService.EXTRA_DATA_TRANS_FILE_MODEL);
            if (action.equals(DataTransService.EXTRA_DATA_TRANS_FILE_PROGRESS)) {
                LogUtil.w(TAG, "upload finish,FILEID=" + fileID + ",progress is=" + item.getProgress());
                // 更新界面上传进度
                FleetUpTrans("FILEID=" + fileID + "\r\nUploadProgress=" + item.getProgress() + "\r\n");
                fileIDBack = fileID;
            } else if (action.equals(DataTransService.EXTRA_DATA_TRANS_FILE_END) || action.equals(DataTransService.EXTRA_DATA_TRANS_END)) {
                LogUtil.w(TAG, "fileIndex" + fileIDBack + ",fileID=" + fileID);
                if (null != fileID && null != fileIDBack && fileIDBack.equals(fileID)) {
                    uploadFiles.clear();
                    if (item.getProgress() < 100) {//失败
                        FleetUpEnd("FILEID=" + fileID + "\r\nError=upload progress <100.\\r\\n");
                        LogUtil.w(TAG, "upload finish,FILEID=" + fileID + ",failure!");
                    } else {//成功
                        FleetUpEnd("FILEID=" + fileID + "\r\n");
                        LogUtil.w(TAG, "upload finish,FILEID=" + fileID + ",success!");
                    }
                    fileID = null;
                } else {
                    LogUtil.w(TAG, "upload finish,FILEID=2is:" + fileID);
                }

            } else if (action.equals(WalkMessage.NOTIFY_TEST_IPACK_STOPED)) {//通知手机端已经停止测试
                SendStopNotify();
            }
        }

        ;
    };

    /**
     * 把GPS的格式从度分格式转换成度格式
     *
     * @param gps 度分格式
     * @return 度格式
     */
    private double changeGPS(String gps) {
        int d = Integer.parseInt(gps.substring(0, gps.indexOf(".") - 2));
        double m = Float.parseFloat(gps.substring(gps.indexOf(".") - 2)) / 60;
        return Double.parseDouble(String.format(Locale.getDefault(), "%.5f", d + m));
    }

    /**
     * IPAD透传处理方法
     */
    private void ipadTransparent() {
        if (m_szbuff.length > 4) {
            byte[] command = DataTypeChangeHelper.getArrayBySource(m_szbuff, 0, 4, true);
            byte[] values = DataTypeChangeHelper.getArrayBySource(m_szbuff, 4, m_szbuff.length - 4, false);
            int cmd = (int) DataTypeChangeHelper.unsigned4BytesToInt(command, 0);
            LogUtil.w(TAG, "cmd=" + cmd);
            switch (cmd) {
                case 1000:    //写入标签ETransRCUTag    = 1000,
                    String eventStr = new String(values);
                    EventManager.getInstance().addTagEvent(getApplicationContext(), System.currentTimeMillis(),
                            eventStr);
                    break;
                case 1001://ETransSetting

                    break;
                case 1002://ETransLockNet锁网
                    doLockNet(new String(values));
                    break;

                case 1003: //透传信令采样点，根据请求采样点查找信令详细解码ETransMsgDetail,
                    String pointIndexStr = new String(values);
                    LogUtil.i(TAG, "L3MSG PointIndex--->" + pointIndexStr);
                    String l3MsgDetailStr = getL3Detail(Integer.parseInt(pointIndexStr));
                    byte[] cmdByteArray = DataTypeChangeHelper.int2byte4(1003);
                    TransparentNotifyResp(DataTypeChangeHelper.byteMerger(cmdByteArray, l3MsgDetailStr.getBytes()));
                    break;
                case 1004://电量传输ETransBattery(这个是ipad请求，自动返回)
                    cmdByteArray = DataTypeChangeHelper.int2byte4(1004);
                    TransparentNotifyResp(DataTypeChangeHelper.byteMerger(cmdByteArray, String.valueOf(currentLevel)
                            .getBytes()));
                    break;
                case 1005: //ETransSyncNet 主被叫同步网络
                    String value = new String(values);
                    if (value.contains("=")) {
						mDxSyncNetType = value.split("=")[1];
                        mDxSyncNetType = mDxSyncNetType.replace("\n", "").replace("\r", "");
                    }
                    break;
                case 1006://电信巡检版本手机端上传文件信息给pad，pad回传上传成功的信息回来
                    String file = new String(values);
                    LogUtil.i(TAG,"----1006 file:" + file + "----");
                    break;
                case 1007://1007 MOS 默认通道号    传送内容：MosChannel=1~14
                    String mosValue = new String(values);  //MosChannel=1~14
                    LogUtil.i(TAG,"----1007 file:" + mosValue + "----");
                    if (mosValue.contains("=")){
                     int which  = Integer.parseInt(mosValue.split("=")[1]);
                        ConfigRoutine.getInstance().setMosBoxChannel(getApplicationContext(), which);
                        LogUtil.i(TAG,"----which:" + which + "----");
                    }
                    break;
            }
            TransparentNotify(1);
        } else {
            TransparentNotify(0);
        }
    }

    /**
     * MOS盒测试时,通过盒子算分的结果通过此接口传入
     * ipack将结果值拼成码流存入m_mosvalue,此接口直接将码流写入文件即可
     */
    private void writeMosValue() {
        long startTime = 0;
        long endTime = 0;
        int valx = 0;
        try {
            if (null != m_mosvalue) {
                valx = ByteUitils.getInt(m_mosvalue, 0);
                if (valx == 336 || valx == 340) {// PESQ_score 0x00000150事件处理或POLQA_Result 0x00000154事件的处理
                    recordFileINDEX += 1;
                    m_mosvalue[m_mosvalue.length - 21] = (byte) (recordFileINDEX >> 8);
                    m_mosvalue[m_mosvalue.length - 22] = (byte) (recordFileINDEX >> 0);
                    //开始时间
                    startTime = ByteUitils.getLong(m_mosvalue, m_mosvalue.length - 20);
                    //结束时间
                    endTime = ByteUitils.getLong(m_mosvalue, m_mosvalue.length - 12);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        LogUtil.w(TAG,"---writeMosValue---startTime:" + startTime + ",endTime:" + endTime);
        if (isTimeFrame(startTime, endTime)) {
            EventBytes.Builder(getApplicationContext(), ByteUitils.getSubBytes(m_mosvalue, 0, m_mosvalue.length - 4)).writeToRcu(System.currentTimeMillis() * 1000);
            sendMOSScore2iPack(valx,m_mosvalue);
        }
    }

    /**
     * 发送算分结果到pad端
     * @param mosType mos算分类型，336 PESQ_score 0x00000150 | 340 POLQA_Result 0x00000154
     * @param mosValue
     */
    private void sendMOSScore2iPack(int mosType, byte[] mosValue) {
        LogUtil.i(TAG,"-----mosType:" + mosType + "------");
        int mosScore = 0;
        if(mosType == 336 && null != mosValue && mosValue.length > 7){
            //PESQ分值在4-7位,实际分数乘以1000以后的整数值
            mosScore = ByteUitils.getInt(mosValue,4);
        }else if(mosType == 340 && null != mosValue && mosValue.length > 23){
            //POLQA分值在20-23位,实际分数乘以1000以后的整数值
            mosScore = ByteUitils.getInt(mosValue,20);
        }
        LogUtil.i(TAG,"-----mosScore:" + mosScore + "------");
        dataMsg4.setLength(0);
        dataMsg4.append("TestKind=0,MosValue=" + mosScore  + ",Point_Time=" + System.currentTimeMillis());
        WriteRealTimeParamP(WalkStruct.UMPCEventType.RealTimeData.getUMPCEvnetType(), dataMsg4.toString());
        dataMsg4.setLength(0);
    }

    /***
     * Mos测试是否在时间范围内
     *
     * @return
     */
    private boolean isTimeFrame(long startTime, long endTime) {
        LogUtil.w(TAG, "ipack fileName is:" + mosFileName);
        File file = new File(mosFileName);
        if (!file.exists()) {
            LogUtil.w(TAG, "ipack file is not exist!");
        } else {
            LogUtil.w(TAG, "ipack file is exist!");
        }
        String valx = FileUtil.getStringFromFile(file);
        LogUtil.w(TAG, "ipack mos values:startTime=" + startTime + ",endTime=" + endTime);
        LogUtil.w(TAG, "test values:" + valx);
        StateInfoModel stateInfo = TraceInfoInterface.traceData.getStateInfoNoQuery();
        boolean flag = false;//默认是不插入事件
        if (stateInfo.getCurrentJon().equals(WalkStruct.TaskType.InitiativeCall.name())||stateInfo.getCurrentJon().equals("MOC")) {//主叫
            if (null != valx && valx.trim().length() > 0) {
                String[] arrays = valx.split("~");
                if (null != arrays && arrays.length > 0) {
                    for (String str : arrays) {
                        String[] times = str.split("-");
                        if (times.length == 1) {//只有开始时间
                            if(TextUtils.isEmpty(times[0])){
                                times[0] = "0";
                            }
                            if (Long.parseLong(times[0]) <= startTime) {
                                flag = true;
                                break;
                            }
                        } else if (times.length == 2) {//有开始时间和结束时间
                            if(TextUtils.isEmpty(times[0])){
                                times[0] = "0";
                            }
                            if(TextUtils.isEmpty(times[1])){
                                times[1] = "0";
                            }
                            if (Long.parseLong(times[0]) <= startTime && Long.parseLong(times[1]) >= endTime) {
                                flag = true;
                                break;
                            }
                        }

                    }
                }
            }
        } else if (stateInfo.getCurrentJon().equals(WalkStruct.TaskType.PassivityCall.name())||stateInfo.getCurrentJon().equals("MTC")) {//被叫
            if (null != valx && valx.trim().length() > 0) {
                String[] arrays = valx.split("~");
                if (null != arrays && arrays.length > 0) {
                    for (String str : arrays) {
                        String[] times = str.split("-");
                        if (times.length == 1) {//只有开始时间
                            if(TextUtils.isEmpty(times[0])){
                                times[0] = "0";
                            }
                            if (Long.parseLong(times[0]) <= startTime) {
                                flag = true;
                                break;
                            }
                        } else if (times.length == 2) {//有开始时间和结束时间
                            if(TextUtils.isEmpty(times[0])){
                                times[0] = "0";
                            }
                            if(TextUtils.isEmpty(times[1])){
                                times[1] = "0";
                            }
                            if (Long.parseLong(times[0]) <= startTime && Long.parseLong(times[1]) >= endTime) {
                                flag = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        LogUtil.w(TAG,"----isTimeFrame result:" + flag + "----");
        return flag;
    }

    /**
     * 查询层三详细解码
     *
     * @param pointIndex
     * @return
     */
    private String getL3Detail(int pointIndex) {
        try {
            if (pointIndex != -1) {
                String l3DetailStr = DatasetManager.getInstance(getApplicationContext()).queryL3Detail(pointIndex);
                LogUtil.i(TAG, "pointIndex" + pointIndex);
                if (l3DetailStr != null && !l3DetailStr.trim().equals("")) {
                    return l3DetailStr;
                }
                return "The message can't  be decoded";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NA";
    }


    /**
     * 执行锁定指定网强获者指定频段
     *
     * @param locknetStr
     */
    private void doLockNet(String locknetStr) {
        try {
            ForceManager fm = ForceManager.getInstance();
            fm.init();
            String lockValue = locknetStr.substring(2, locknetStr.length());

            if (locknetStr.startsWith("1:")) {
                ForceNet forceNet = ForceNet.valueOf(lockValue);

                if (forceNet != null) {
                    if (Deviceinfo.getInstance().getDevicemodel().equals("ZTENX569J"))
                        fm.lockNetwork(this, forceNet);
                    else fm.lockNetwork(forceNet);
                }
            } else if (locknetStr.startsWith("2:")) {
                String netStr = lockValue.substring(0, lockValue.indexOf("["));
                String[] bandStrs = lockValue.substring(lockValue.indexOf("[") + 1, lockValue.indexOf("]")).split(",");

                ForceNet forceNet = ForceNet.getForceNetByNet(netStr);
                if (forceNet != null && bandStrs.length > 0) {
                    Band[] bands = new Band[bandStrs.length];
                    for (int i = 0; i < bandStrs.length; i++) {
                        bands[i] = Band.valueOf(bandStrs[i]);
                    }
                    if (Deviceinfo.getInstance().getDevicemodel().equals("ZTENX569J"))
                        fm.lockBand(this, forceNet, bands);
                    else fm.lockBand(forceNet, bands);
                }
            }
        } catch (Exception e) {
            LogUtil.w(TAG, "doLockNet", e);
        }
    }


    /*处理测试计划下载*/
    private void doSetTestPlan(String planTmp) {
        //根据文件生成测试任务模型列表taskList
        LogUtil.w(TAG, "--start-doSetTestPlan--" + planTmp);
        int type = Integer.parseInt(ipackControl.getValueByKey(planTmp, "PlanType", "0"));
        String planPath = ipackControl.getValueByKey(planTmp, "FileName");
        TermType termType = TermType.getTermTypeById(type);

        LogUtil.w(TAG, "--Umpc TestPlan:" + planPath + ",termType:" + termType);
        UtilsMethod.runRootCommand("chmod 777 " + planPath);

        switch (termType) {
//            case Android:
//                TestPlanUmpc testPlan = new TestPlanUmpc(planPath);
//                ArrayList<TaskModel> modelList = testPlan.getTaskList();
//                TaskListDispose.getInstance().getTestPlanConfigFromIpad(modelList);
//                break;
            case IPhone:
                try {
                    Thread.sleep(1000);
                    TaskListDispose taskListDispose = TaskListDispose.getInstance();
                    UtilsMethod.copyFile(new File(planPath), new File(taskListDispose.getFileName()));
                    LogUtil.d(TAG,"-----doSetTestPlan file txt:" + UtilsMethod.readFile(planPath));
                    taskListDispose.reloadFromXML();
                    if (taskListDispose.getCurrentGroups().size() > 0) {//从iPad下发
                        taskListDispose.getCurrentGroups().get(0).setGroupName(TaskXmlTools.sDefaultGroupName);
                        taskListDispose.getCurrentGroups().get(0).setGroupID(taskListDispose.getDefaultGroupId());
                        taskListDispose.getCurrentGroups().get(0).setGroupStatus(TaskGroupConfig.GROUPSTATUS_1);
                        taskListDispose.writeXml();//写入xml,在重新加载
                        taskListDispose.reloadFromXML();
                    }
                } catch (Exception e) {
                    LogUtil.w(TAG, "doSetTestPlan" + e.getMessage());
                }
                break;
            case Scanner:
                try {
                    UtilsMethod.copyFile(new File(planPath), new File(Environment.getExternalStorageDirectory() +
                            "/scantasklist.xml"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PlanATU:
                analyzeAtuPlan(planPath);
                break;
            default:
                break;
        }
        if(AppVersionControl.getInstance().isTelecomInspection()){
            //如果是电信巡检版本，删除测试计划文件
            FileUtil.deleteFile(planPath);
        }
        LogUtil.w(TAG, "--end-doSetTestPlan--");
    }

    private void analyzeAtuPlan(String filePath) {
        TaskConverter converter = new TaskConverter(getApplicationContext(), new File(filePath), true);
        int taskVersion = converter.getTaskVersion();
        if (taskVersion > 0) {
            ServerManager.getInstance(getApplicationContext()).setDTLogCVersion(converter.getTaskVersion());
            ServerManager.getInstance(getApplicationContext()).setDTLogScheme(0);

            // 默认先取第一个测试方案
            ArrayList<TestScheme> schemeList = converter.convertTestScheme();
            if (schemeList.size() > 0) {
                TestScheme scheme = schemeList.get(0);
                atuPort = scheme.getMoudleNum();
                ServerManager.getInstance(getApplicationContext()).setDTLogMoudleNum(atuPort);
//				TaskListDispose.getInstance().replaceTaskList(scheme.getCommandList());

                TaskListDispose.getInstance().getTestPlanConfigFromAtu(scheme.getCommandList());
            }
        }
    }

    /*处理文件上传请求*/
    private void doRequestUpLoad(String pdata) {
        enableData();
        String upType = ipackControl.getValueByKey(pdata, "FileType");
        String upName = ipackControl.getValueByKey(pdata, "FileName");
        String groupInfo = ipackControl.getValueByKey(pdata, "GroupInfo");
        IpackFileType fileType = IpackFileType.getFileTypeByID(upType);

        String upFilePath = null;
        LogUtil.w(TAG, "--doRequestUpLoad:upType=" + upType + ",upName" + upName + ",groupInfo=" + groupInfo + ",fileType=" + fileType);
        switch (fileType) {
            case FILELIST:
                upFilePath = ipackControl.getFileListPath(getApplicationContext(), dbManager.getMultiRecordList(),
                        upName);
                break;
            case RCU:
            case DTLOG:
            case DDIB:
            case PCAP:
            case DCF:
                upFilePath = ipackControl.getUploadFilesPath(getApplicationContext(), dbManager.getMultiRecordList(),
                        upName, fileType);
                break;
            case STAT:
                break;
            case TESTPLAN:
                break;
            case FLEETFILELIST://fleet上传文件列表
                LogUtil.w(TAG, "--FLEETFILELIST:");
                upFilePath = ipackControl.getFileListPathDXXJ(getApplicationContext(), dbManager.getMultiRecordList(),
                        upName, groupInfo);
                break;
            default:
                break;
        }

        LogUtil.w(TAG, "--doRequestUpLoad:" + upFilePath);
        if (upFilePath != null) {
            SendFile(upFilePath);
        }
    }


    /*文件上传结束*/
    private void doUploadFinish(String fileName) {
        ArrayList<TestRecord> fileList = dbManager.getMultiRecordList();
        String simpleName = fileName.substring(0, fileName.indexOf("."));
        String extendName = fileName.substring(fileName.indexOf(".") + 1);
        FileType fileType = FileType.getFileTypeByName(extendName);

        for (TestRecord file : fileList) {
            if (file.file_name.equals(simpleName)) {
                // 将设置结果固化到数据库中 此处要根扰上传到指定服务器填写
                dbManager.uploadStateChange(file.record_id, fileType, 100, "ipack");
                LogUtil.w(TAG, "--upload finish:" + fileName);
                break;
            }
        }
    }

    /**
     * 文件删除，通知删除结果
     *
     * @param fileName
     * @author tangwq
     */
    private void doDeleteFile(String fileName) {
        int delResult = 0;
        if (fileName.indexOf(".rcu") > 0 || fileName.indexOf(".ddib") > 0) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        ArrayList<TestRecord> fileList = dbManager.getMultiRecordList();
        for (TestRecord file : fileList) {
            if (file.file_name.equals(fileName)) {
                //将设置结果固化到数据库中
                dbManager.deleteFile(file);
                delResult = 1;
                break;
            }
        }
        LogUtil.w(TAG, "--delete file:" + fileName + "--delResult:" + delResult);
        //通知文件删除成功
        DeleteFileNotify(fileName, delResult);
    }

    /*响应开始测试,未处理建筑物相关情况*/
    private void doStartTest(String testInfo) {
        //数据格式:
        //indoor=1,catchcap=0,testmode=CQT,taskno=20170428_142924,testgroupinfo=CQT_20170428_142924,
        //testername=Longitude=-9999.000000,longitude=-9999.000000,latitude=-9999.000000,valsamplecdma=0,
        //valsampletd=0,valsamplewcdma=0,datasize=3600,guid=2082134046,repeats=1,autoupload=0,syncmode=0,
        //autosync=1,syncfile=dcf;rcu;dtlog,genfiletype=7

        //LogUtil.w(TAG,"---start test:"+tesInfo);
        /**    indoor=0,catchcap=1,testmode=CQT,taskno=20141208121211,
         * 	testgroupinfo=20141208121211_CQT_WCDMA,testername=BQ,
         * longitude=0.000000,latitude=0.000000,valsamplecdma=0,valsampletd=0,
         * valsamplewcdma=0,datasize=0,guid=2099848150,repeats=100,autoupload=1,
         * syncmode=0,autosync=1,syncfile=rcu;pcap;dcf;ddib;dtlog,
         * genfiletype=10
         *
         * indoor=0			1代表CQT，0 代表DT测试
         * catchcap=0
         * testlocale=		测试结果文件名
         * testername=		测试人员姓名
         * longitude=0.000000经度
         * latitude=0.000000	纬度
         * valsamplecdma=0	CDMA采样频率（单位：毫秒）
         * valsampletd=0		TD采样频率（单位：毫秒）
         * valsamplewcdma=0	WCDMA采样频率（单位：毫秒）
         * datasize=0		自定义文件大小（单位：M）
         * guid=1258261422	UMPC生成一串唯一码用于后台匹配RCU文件，写入RCU文件头的RCUID区
         * repeats=0			外循环次数
         * autoupload		是否自动上传数据，0不上传，1 当前文件关闭时上传
         * encryptCode		密钥内容
         * syncmode			0代表组内同步，1代表全手机同步
         * genfiletype		IPACK往外报时以10进制输出.0x1 dcf,0x2 org.rcu,0x4 dtlog,0x8 cu
         */
        testInfo += ",encryptCode=" + encryptCode + ",phonename=" + MyPhoneState.getInstance().getDeviceId
                (getApplicationContext()) + ",Controller=" + controller + ",ATUPort=" + atuPort+",DxSyncNetType="+mDxSyncNetType;
        if (!appModel.isTestJobIsRun()) {
            //发送开始自动测试消息
            LogUtil.w(TAG, "--testInfo--" + testInfo);
            Intent startTest = new Intent(WalkMessage.ACTION_WALKTOUR_START_TEST);
            startTest.putExtra(WalkMessage.ACTION_ISUMPCTEST_PARA, true);
            startTest.putExtra(WalkMessage.ACTION_ISUMPCTEST_INFO, testInfo);
            startTest.putExtra(WalkMessage.KEY_FROM_SCENE, SceneType.MultiTest.getSceneTypeId());
            sendBroadcast(startTest);
            appModel.setRuningScene(SceneType.MultiTest);
        }
        UmpcTestInfo umpcTestInfo=new UmpcTestInfo(testInfo);
        if (umpcTestInfo.getSceneType()==1||umpcTestInfo.getSceneType()==2){
            DatasetManager.getInstance(getApplicationContext()).ConfigPropertyKeyIsCheckGPSDrift(false);
        }else {
            DatasetManager.getInstance(getApplicationContext()).ConfigPropertyKeyIsCheckGPSDrift(true);
        }
    }

    /* 中断测测试 */
    private void doStopTest() {
        if (appModel.isTestStoping()) {
            return;
        }

        if (appModel.isTestJobIsRun()) {
            Intent interruptIntent = new Intent(WalkMessage.Action_Walktour_Test_Interrupt);
            sendBroadcast(interruptIntent);
        } else {
            SendStopNotify();
        }
    }


    /*开启GPS搜索*/
    private void doQueryGps() {
        startService(new Intent(this, GpsService.class));
        gpsInfo.setUmpcGpsOpen(true);
    }

    /*停止GPS搜索*/
    private void doStopQueryGps() {
        LogUtil.w(TAG, "--doStopQueryGps--" + gpsInfo.isUmpcGpsOpen());
        gpsInfo.releaseGps(getApplicationContext(), WalkCommonPara.OPEN_GPS_TYPE_UMPC);
    }


    /**
     * 初始化ADB服务
     *
     * @param jstrlibpath 为动态库路径，如“/data/data/com.walktour.gui/lib/”
     * @return
     */
    public native int InitService(String jstrlibpath);

    /**
     * 解除ADB连接服务
     *
     * @return
     */
    public native int UnInitService();

    /**
     * ADB服务创建
     *
     * @param jstrParam 为启动服务参数，
     *                  请按以下格式组串 "DevID=%s\r\nDevModel=%s\r\nCmdPort=%s\r\nDataPort=%s\r\nTelComName=%s\r\nifrName=%s
     *                  \r\nbcport=%s\r\n"
     *                  DevID：		设备唯一码，可以是IMEI；
     *                  DevModel：	设备型号，如“htc 9228”；
     *                  CmdPort：	命令端口，大于1000小于65536；
     *                  DataPort：	数据端口，大于1000小于65536；
     *                  TelComName：运营商名字，如china unicom、china mobile...；
     *                  ifrName：	网卡名，如“en0”,"wlan0"....；
     *                  bcport：		广播端口，大于1000小于65536；
     * @return
     */
    public native int BeginService(String jstrParam);

    public native int EndService();

    /**
     * 名称：LoadConfigFile
     * 描述：从配置文件读取客户端信息
     * 原型：bool LoadConfigFile(String pcsFileName)
     * 返回：读取成功返回true，否则返回false
     * 参数：
     *
     * @param String pcsFileName	配置文件文件绝对路径
     *               [out]
     *               备注：如果已经初始化客户端信息，则文件配置无效
     *               修改：
     */
    public native byte LoadConfigFile(String pcsFileName);

    /**
     * 名称：SetWorkPath
     * 描述：设置工作路径
     * 原型：bool SetWorkPath(String strpath)
     * 返回：设置成功返回true，否则返回false
     * 参数：
     *
     * @param String strpath	工作目录绝对路径，末尾不带 '/'
     * @param String strdatapath	数据存储路径，末尾不带'/'
     *               [out]
     *               备注：工作路径必须存在，否则上传文件功能不能直接使用
     *               修改：
     */
    public native byte SetPath(String strworkpath, String strdatapath);


    /**
     * Log日志路径
     *
     * @param logPath
     * @return
     */
    public native byte SetLogPath(String logPath);

    /**
     * 名称：WriteRealTimeParam
     * 描述：写实时参数，包括事件、信令、参数、数据业务速率等等
     * 原型：int WriteRealTimeParam(char msgType,String strMsg)
     * 返回：实际发送到网络的数据长度，0或者-1为失败
     * 参数：
     * [in]	char msgType 消息类型
     * String strMsg 消息内容
     * [out]
     * 备注：参数说明，如有变化以协议文档为准
     * DataType：这个标志表示Message的内容。
     * 'E':事件
     * 'A':告警
     * 'M':信令
     * 'R':数据业务实时参数
     * 'G':GSM参数
     * 'T':TD参数
     * 'W':WCDMA参数
     * 'C':CDMA2000参数
     * 'L':LocaltionGPS经纬度 Latitude=2211.3344,Longitude=23.334455
     * 'V':EVDO参数
     * 'S':SCANNER参数
     * <p>
     * 语音业务Message（测试参数）格式如下：
     * GSM语音：
     * Message:RxLevFull=12,RxLevSub=-20,RxQualSub=5,TA=100,Txpower=20,BCCH=10,BSIC=2, CellID=876
     * CDMA语音：
     * Message:RxAGC=-50,TxAGC=-20,TotalEc/Io=-10,FFER=70,Freq=20,PN=83
     * 数据业务:
     * TD-SCDMA数据业务:
     * Message:PCCPCHRSCP=-50,PCCPCHC/I=10,TxPower=-20,BLER=-50,FTPDLAllSize=1000,FTPULAllSize=1000,FTPDLCurSize=50,
     * FTPULCurSize=60,Freq=50,CPI=40,CellID=60
     * EVDO数据业务:
     * Message:Evdo_RxAGC=-50,Evdo_TotalC/I=10,Evdo_TxAGC=-20,Evdo_DRCRate=3250000,EVSectorUserServed=10,
     * FTPDLAllSize=1000,FTPULAllSize=1000,FTPDLCurSize=50,FTPULCurSize=60, Evdo_Freq=50, Evdo_PN=283
     * WCDMA数据业务:
     * Message:TotalRSCP=-50,TotalEc/IO=-10,TxPower=20,BLER=50,FTPDLAllSize=1000,FTPULAllSize=1000,FTPDLCurSize=50,
     * FTPULCurSize=60,Freq=50,PSC=283, CellID=60
     * 修改：
     */
    public native int WriteRealTimeParam(char msgType, String strMsg);

    /**
     * 名称：SendStopNotify
     * 描述：发送停止通知
     * 原型：int SendStopNotify()
     * 返回：实际发送到网络的数据长度
     * 参数：
     * [in]
     * [out]
     * 备注：当手工停止或者测试结束都要发送
     * 修改：
     */
    public native int SendStopNotify();

    /**
     * 名称：SendBreakCurTest
     * 描述：要求对方手机跳过本次测试
     * 原型：int SendBreakCurTest()
     * 返回：实际发送到网络的数据长度
     * 参数：
     * [in]
     * [out]
     * 备注：本次测试发生异常时使用
     * 修改：
     */
    public native int SendBreakCurTest();

    /**
     * 名称：SendNormalData
     * 描述：向对方发送普通数据
     * 原型：int SendNormalData(unsigned String buf,int len)
     * 返回：实际发送到网络的数据长度
     * 参数：
     * [in]		unsigned String buf 要发给对方的的数据内容
     *
     * @param int len			   数据长度
     *            [out]
     *            备注：当有数据需要转发给对方时使用
     *            修改：
     */
    public native int SendNormalData(String buf, int len);


    /**
     * 名称：SendSyncData
     * 描述：向服务器发送同步数据
     * 原型：int SendSyncData(unsigned String buf,int len)
     * 返回：实际发送到网络的数据长度
     * 参数：
     * [in]		unsigned String buf 要发给服务器的同步数据
     *
     * @param int len			   数据长度
     *            [out]
     *            备注：当需要服务器做同步响应时候使用
     *            修改：
     */
    public native int SendSyncData(String buf, int len);

    /**
     * 名称：SendFile
     * 描述：向服务器发送文件
     * 原型：bool SendFile(const String strFileName)
     * 返回：请求发送成功返回true，否则返回false
     * 参数：
     *         [in]		const String strFileName 要发送文件名，绝对路径
     *         [out]
     * 备注：当需要服务器做同步响应时候使用
     * 修改：
     */

    /**
     * iPack要求上传上传文件时填充当前串，调晓军库上传
     * GUID=%s\r\nDevID=%s\r\nFilePath=%s\r\nFileName=%s\r\nFileType=%s\r\nTransMode=%s\r\nGroupInfo=%s\r\nTestMode=%s
     * \r\n
     */
    public static native byte SendFile(String strFileName);

    /**
     * 当连小背包测试开始时，如果为实时上传指定文件，按要求填充当前串并调JNI接口进行上传
     * GUID=%s\r\nFilePath=%s\r\nFileName=%s\r\nFileType=%d\r\nTransMode=%d\r\nGroupInfo=%s\r\nTestMode=%s\r\n
     */
    public static native int SendRTFile(String jstrParam);

    /**
     * 实时上传过程中实时更新当前文件的大小,填充当前串
     * GUID=%s\r\nWriteEof=%d\r\nCurrSize=%d\r\n
     */
    public static native int SetRTFileInfo(String jstrParam);

    /**
     * 测试结束后，如果生成文件的名字发生变化时，调用当方法更新
     * GUID=%s\r\nDevID=%s\r\nFileName=%s\r\n
     */
    public static native int SendRTFileUpdate(String jstrParam);

    /**
     * "AlarmType=%s\r\nContent=%s\r\n"
     */
    public static native int SendAlarmEvent(String jstrParam);

    /**
     * 名称：SendStopTheOther
     * 描述：发送请求停止同组对方测试
     * 原型：int SendStopTheOther()
     * 返回：实际发送到网络的数据长度
     * 参数：
     * [in]
     * [out]
     * 备注：当需要服务器做同步响应时候使用
     * 修改：
     */
    public native int SendStopTheOther();

    /**
     * 名称：SendStopAll
     * 描述：发送请求所有手机停止测试
     * 原型：int SendStopAll()
     * 返回：实际发送到网络的数据长度
     * 参数：
     * [in]
     * [out]
     * 备注：有必要时再使用
     * 修改：
     */
    public native int SendStopAll();

    /**
     * 用于对生成的文件按照联通的规范进行加密
     *
     * @param jstrkey      密钥
     * @param jstrSrcFile  加密文件绝对路径
     * @param jstrDestFile 加密后文件绝对路径
     * @return
     */
    //public native int EncryptRcuFile(String jstrkey, String jstrSrcFile, String jstrDestFile);
    public native int EncryptRcuFile(String jstrSrcFile, String jstrDestFile);

    /**
     * 处理结果通知IPAD端
     *
     * @param fileName 删除文件名不带路径
     * @param result   0失败，1成功
     * @return
     * @author tangwq
     */
    public native int DeleteFileNotify(String fileName, int result);

    /**
     * 外循环全同步接口
     * 当外循环条件为需要全部同步时调用此接口
     *
     * @param buf
     * @param len
     * @return
     * @author tangwq
     */
    public native int SendSyncAll(String buf, int len);

    /**
     * 发送消息通知所有终端
     *
     * @param buf
     * @param len
     * @return
     * @author tangwq
     */
    public native int SendBroadcast(String buf, int len);

    /**
     * 上传统计数据的文件，该文件包含了统计数据的SQL语句
     *
     * @param fileFullPath
     * @return
     */
    public native int SendFileStat(String fileFullPath);

    /**
     * 发送RCU标准结构事件
     *
     * @param buf
     * @param len
     * @return
     */
    public native int WriteRealTimeParamBytes(char type, byte[] buf, int len);

    /**
     * 时间同步处理结果通知
     *
     * @param result 1:同步成功,其它失败
     * @return
     */
    public native int TimeSyncNotify(int result);

    /**
     * 暂停测试结果通知
     */
    public native int TestSuspendNotify(int result);

    /**
     * 继续测试结果通知
     */
    public native int TestResumeNotify(int result);

    /**
     * 透传命令执行结果通知
     */
    public native int TransparentNotify(int result);

    /**
     * 透传命令执行,回传层三信令详细解码
     */
    public native int TransparentNotifyResp(byte[] resultDate);


    /******MOS盒相关调用流程******
     * 初始化MOS,接通后通知道调用成功
     * 传入IMEI等配置信息
     * 通话过程中不需要控制中间间隔录音情况
     * 通话结束时通知当前次通话结束
     */
    /**
     * mos初始化，与盒子交互
     *
     * @param mosType MosType mos类型（POLQA/PESQ）
     * @param sample  Sample 采样率(8000/16000/48000)
     * @param chnnl   Chnnl 通道号(1~8)
     * @param vRec    VRec 录音音量(0~255)
     * @param vPlay   VPlay 放音音量(0~255)
     * @param lowMos  LowMos Mos阈值，多少分值以下才上报(浮点，4.0)
     * @param recFile RecFile 文件命名
     * @return 成功返回>0，否则返回<=0
     */
    public native int MosInit(String mosType, int sample, int chnnl, int vRec, int vPlay, float lowMos, String recFile);

    /**
     * MOS开始结束控制
     *
     * @param MOMT       MOMT 主叫被叫（MO/MT）
     * @param Chnnl      Chnnl 通道号(1~8)
     * @param CallStatus CallStatus 接通状态（0：挂机；1：接通）
     * @return 成功返回>0，否则返回<=0
     */
    public native int MosTest(String MOMT, int Chnnl, int CallStatus);


    /***
     * 向FLeet服务器上传数据开始，用于通知ipack
     * @param param
     * @return
     */
    public native int FleetUpBegin(String param);

    /***
     * 向FLeet服务器上传数据的进度，用于通知ipack
     * @param param
     * @return
     */
    public native int FleetUpTrans(String param);

    /***
     * 向FLeet服务器上传数据结束，用于通知ipack
     * @param param
     * @return
     */
    public native int FleetUpEnd(String param);

    /***
     * 向FLeet服务器上传数据结束，用于通知ipack
     * @param param
     * @return
     */
    public native int ULFleetResp(String param);


    /**
     * 开始记录文件通知
     *
     * @return
     */
    public native int SendBeginRecordNotify();

    /**
     * 停止记录文件通知
     *
     * @return
     */
    public native int SendEndRecordNotify();

    @Override
    public void onRefreshed(RefreshType refreshType, Object object) {
        switch (refreshType) {
            case ACTION_WALKTOUR_TIMER_CHANGED:
                //LogUtil.e(TAG,"--onRefreshed:" + umpcLogined);
                if (mActionSwitchThread != null && umpcLogined) {    //如果当前断开再记录的话，会导致内存溢出及关键消息因时延问题而错乱
                    if (System.currentTimeMillis() - mLastTimeChanged > 1000) {
                        mLastTimeChanged = System.currentTimeMillis();
                        mActionSwitchThread.addIndent(new Intent(WalkMessage.ACTION_WALKTOUR_TIMER_CHANGED));
                    }
                }
                break;
            default:
                break;
        }
    }
}
