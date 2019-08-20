package com.walktour.service.test;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.ril.com.datangb2b.MethodManager;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

import com.dinglicom.dataset.EventManager;
import com.dinglicom.dataset.model.DataSetEvent;
import com.vivo.api.netcustom.interfaces.DeviceAssistant;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.MyAudioPlayer;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.TotalStruct.TotalDial;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.audios.AudioManagerFactory;
import com.walktour.Utils.audios.IAudioManager;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhone;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.instance.AlertManager;
import com.walktour.gui.R;
import com.walktour.gui.mos.CaculateModeFacade;
import com.walktour.gui.mos.TaskModelWrapper;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.gui.task.parsedata.model.task.mtc.TaskPassivityCallModel;
import com.walktour.service.ICallback;
import com.walktour.service.IService;
import com.walktour.service.TestService;
import com.walktour.service.bluetoothmos.BluetoothMOSService;
import com.walktour.service.bluetoothmos.IBluetoothMOSServiceBinder;
import com.walktour.service.bluetoothmos.command.BaseCommand.FileType;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;
import com.walktour.service.iPackTerminal;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @Serivice 语音被叫测试
 * @descrition 业务流程参考文档《语音业务测试行为规范》
 */
public class MTCTest extends TestTaskService {
    public static final String EXTRA_HAS_POLQA_TYPE = "has_polqa_type";
    public static final String TAG = "MTCTest";

    private long delay = 0;

    // 记录接收到的信令
    private long timeAttempt;
    /**
     * 被叫起呼的接通时间,需要和MOS返回的时间进行比较
     **/
    public static long timeAlerting;
    private long timeConnected;
    /**
     * 被叫起呼的挂机时间,需要和MOS返回的时间进行比较
     **/
    public static long callendTime = 0;
    private boolean hasCallAttempt = false;
    private boolean hasEstablishedFromCommand = false;
    private boolean isTesting = true;
    private boolean isIdle = true;
    private String strUUID = ""; // 写入RCU事件时的配对信息，当前为主被叫联合时，获得UUID值，并发送给主叫方同时在开始事件中写入
    // 测试模型
    private TaskPassivityCallModel callModel;
    private boolean mHasPolqaType = false;

    // 手机状态，用于检测是否有信号
    private TelephonyManager telManager;
    private MyPhone mPhone;
    private boolean isServiceOn = false;
    private boolean hasIncoming = false;
    private boolean hasAlerting = false;
    private boolean hasDrop = false;
    private boolean hasHangup = false;

    private long csfbRequest = 0;
    private long csfbProceeding = 0;
    private int csfbProceedingNetType = 0;// CSFB回落网络类型
    private long csfbRrcRelease = 0;// RRC Release时延,从CSFB Request到Originating
    // CSFB RRC Release 时延

    /**
     * 主被叫同步信号，WAIT为等待，EXEC为可执行
     */
    public static final String WAIT = "wait";
    public static final String EXEC = "exec";
    public static final String STOP = "stop";
    public static final String RESQ = "resq";
    private boolean mRecMocResponse;//判断是否收到主叫响应
    private boolean mConnectFlag; // 是否接通标识
    private String mNetSyncSignal = WAIT;
    //如果为 NONE，则不需要处理主被叫网络同步
    private static final String DEFAULT_NET_TYPE = "NONE";

    private boolean isVoLTE = false;

    // 主被叫联合测试
    private boolean hasFinish = false; // 是否结束正要退出本进程
    private boolean isMosBoxTest = false; // 当前是否小背包的MOS盒子测试

    /**
     * 统计
     */
    private HashMap<String, Integer> map = new HashMap<String, Integer>();
    /**
     * 向主叫发送MOS开始的消息后，是否收到了主叫的响应
     */
    private boolean hasMosStartAck = false;
    /**
     * 是否在在进行MOS的同步(确保只有一个MOS同步线程在执行)
     */
    private boolean isMOSSynchronizing = false;

    // RCU原因码
    private final int NORMAL_CALL = 0; /* 正常通话结束挂机标志 */
    private final int BLOCK_CALL = 1; /* 未接通挂机标志 */
    private final int DROP_CALL = 2; /* 掉话挂机标志 */
    private final int INTERRUPT = 6; /* 手工停测试 */
    private final int OUT_OF_SERVER = RcuEventCommand.DROP_OUT_OF_SERVICE;/* 脱网 */
    // 其它不会在界面显示事件的挂机类型
    private final int BLOCK_FROM_MOC = -1; /* 主叫BLOCK后通知的挂机 */
    // private final int NO_CAMMAND = -2; /*没有信令的判断*/
    private DecimalFormat df = new DecimalFormat("##0.000");
    /**
     * 是否已注册广播接收器
     */
    private boolean hasRegisteredBroadcast = false;
    /**
     * 是否初始化放音设置
     */
    private boolean isInitMosPlayer = true;
    /**
     * 当前关联的设备
     */
    private BluetoothMOSDevice mCurrDevice;
    /**
     * 用于蓝牙MOS测试录放音同步问题，该值为当前终端时间减去服务器获取到的时间的值
     */
    private long mServerTimeOffset = 0;
    /**
     * 是否已连接蓝牙MOS服务
     */
    private boolean hasMOSConnected = false;
    /**
     * 是否已获取蓝牙MOS的电量
     */
    private boolean hasMOSPower = false;
    /**
     * 当前是否是通用版
     */
    private boolean isGeneralMode = ApplicationModel.getInstance().isGeneralMode();
    /**
     * 蓝牙MOS头服务
     */
    private IBluetoothMOSServiceBinder mMOSService = null;
    /**
     * 是否已经接听电话,防止在通话中，又有人拨打进来
     **/
    private boolean isAccept = false;

    private String rcuFileName = "";
    private CaculateModeFacade mCaculateModeFacade;

    //使用内置polqa算分文件操作过程需要保存手机原始SeriaNo
    private String originalSerialNo;

    private Handler mHandler = new MyHandler(this);

    // 注册回调接口
    private boolean hasCallbackRegisted = false;
    private WaitForIncomingCallThread mWaitForIncomingCallThread = null;
    private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();
    private IService.Stub mBinder = new IService.Stub() {

        public void unregisterCallback(ICallback cb) {
            if (cb != null) {
                LogUtil.w(tag, "--unregisterCallback--");
                mCallbacks.unregister(cb);
                hasCallbackRegisted = false;
            }
        }

        public void registerCallback(ICallback cb) {
            if (cb != null) {
                mCallbacks.register(cb);
                hasCallbackRegisted = true;
            }
        }

        public void stopTask(boolean isTestInterrupt, int dropReason) throws RemoteException {
            LogUtil.i(tag, String.format(Locale.getDefault(), "===stopTask,isInterrupt:%b,reason:%d", isTestInterrupt, dropReason));
            if (isTestInterrupt) {
                hangupDial(INTERRUPT);
            } else if (dropReason == OUT_OF_SERVER) {
                showEvent(getDropReasonString(OUT_OF_SERVER));
                hangupDial(OUT_OF_SERVER);
            }
            new Thread(new MonitorOnDestroy()).start();
            clearTimePlayer();
        }

        /**
         * 返回是否执行startCommand状态, 如果改状态需要在业务中出现某种情况时才为真, 可以在继承的业务中改写该状态
         */
        public boolean getRunState() {
            return startCommondRun;
        }
    };
    private String mNetType = DEFAULT_NET_TYPE;

    /**
     * 清除定时播放线程
     */
    private void clearTimePlayer() {
        isInitMosPlayer = true;
        this.muteSystemOrNotification(false);
    }


    /**
     * mHandler: 调用回调函数
     */
    private static class MyHandler extends Handler {
        private WeakReference<MTCTest> reference;

        public MyHandler(MTCTest test) {
            this.reference = new WeakReference<>(test);
        }

        @Override
        public void handleMessage(Message msg) {
            MTCTest test = this.reference.get();
            while (!test.hasCallbackRegisted)
                ;

            if (msg.what == EVENT_CHANGE) {
                LogUtil.w(tag, "===" + msg.obj.toString());
            }

            resultCallBack(msg);
            super.handleMessage(msg);
        }

        @SuppressWarnings("rawtypes")
        private void resultCallBack(Message msg) {
            MTCTest test = this.reference.get();
            int N = test.mCallbacks.beginBroadcast();
            try {
                // 只有注册成功后，才能回传相关参数信息
                for (int i = 0; i < N; i++) {
                    LogUtil.w(tag, "---hasCallbackRegisted:" + test.hasCallbackRegisted + "--what:" + msg.what);
                    switch (msg.what) {
                        case EVENT_CHANGE:
                            test.mCallbacks.getBroadcastItem(i).OnEventChange(test.repeatTimes + "-" + msg.obj.toString());
                            break;
                        case CHART_CHANGE:
                            test.mCallbacks.getBroadcastItem(i).onChartDataChanged((Map) msg.obj);
                            break;
                        case DATA_CHANGE:
                            test.mCallbacks.getBroadcastItem(i).OnDataChanged((Map) msg.obj);
                            break;
                        case TEST_STOP:
                            LogUtil.w(tag,
                                    "---is mCallbacks.getBroadcastItem(i) null ?" + (test.mCallbacks.getBroadcastItem(i) == null));
                            Map<String, String> resultMap = TaskTestObject.getStopResultMap(test.callModel);
                            resultMap.put(TaskTestObject.stopResultState, (String) msg.obj);
                            test.mCallbacks.getBroadcastItem(i).onCallTestStop(resultMap);
                            break;
                        case GET_NETWORK_TYPE:
                            // 此查询是阻塞的
                            test.csfbProceedingNetType = test.mCallbacks.getBroadcastItem(i).getNetWorkType(false);
                            break;
                    }
                }
            } catch (RemoteException e) {
                LogUtil.w(tag, "", e);
            }
            test.mCallbacks.finishBroadcast();
        }
    }

    /**
     * [调用停止当前业务接口]<BR>
     * [如果当前为手工停止状态不能调用当前停止接口]
     *
     * @param msg
     */
    private void sendCallBackStop(String msg) {
        if (!isInterrupted) {
            Message StopMsg = mHandler.obtainMessage(TEST_STOP, msg);
            StopMsg.sendToTarget();
        }
    }


    /**
     * 发送被叫结束事件给TestService
     */
    private void sendMTEndEvent2TestService() {
        sendBroadcast(new Intent(WalkMessage.ACTION_MT_END_EVENT));
    }


    /**
     * 广播接收器:接收通信过程中的信令
     */
    private BroadcastReceiver mEventReceiver = new BroadcastReceiver() {

        private boolean isHangUpAfterAttempt;

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.w(TAG, "onReceive:" + intent.getAction());
            if (intent.getAction().equals(WalkMessage.ACTION_PROPERTY)) {
                int rcuId = intent.getIntExtra(WalkMessage.KEY_EVENT_RCUID, 0);
                if (rcuId == DataSetEvent.ET_MT_Attempt) {
                    long propertyValue = intent.getLongExtra(WalkMessage.KEY_PROPERTY, 0);
                    if (callModel.isUnitTest()) {
                        if (propertyValue < 2 && !mNetType.equals(DEFAULT_NET_TYPE) && hasCallAttempt) {
                            EventManager.getInstance().addTagEvent(MTCTest.this, System.currentTimeMillis(), "MTC is not VOLTE");
                            sendNormalMessage(String.valueOf(STOP));
                            hangupDial(NORMAL_CALL);
                            sendCallBackStop(TestService.RESULT_SUCCESS);
                        } else if (propertyValue < 2 && !mNetType.equals(DEFAULT_NET_TYPE) && !hasCallAttempt) {
                            isHangUpAfterAttempt = true;
                        }
                        LogUtil.i(TAG, "mtc propertyvalue=" + propertyValue);
                    }
                }
            }

            // 接收来自解码库的信令
            if (intent.getAction().equals(WalkMessage.ACTION_EVENT)) {
                int rcuId = intent.getIntExtra(WalkMessage.KEY_EVENT_RCUID, 0);
                long time = intent.getLongExtra(WalkMessage.KEY_EVENT_TIME, System.currentTimeMillis());
                String str = intent.getStringExtra(WalkMessage.KEY_EVENT_STRING);
                LogUtil.w(tag, "--receive:" + str + ",rcuId:" + rcuId);

                switch (rcuId) {

                    case DataSetEvent.ET_MT_CSFB_Proceeding:
                        csfbProceeding = time;
                        mHandler.obtainMessage(GET_NETWORK_TYPE).sendToTarget();
                        break;

                    // RRC Release时延：从CSFB Request到Originating CSFB RRC Release 时延
                    case DataSetEvent.ET_MT_CSFB_RRCRelease:
                        csfbRrcRelease = time;
                        break;

                    // 2013.10.17 香港后发现CSFB事件出来次序不对，得按时间排序，这里只参与统计
                    case DataSetEvent.ET_MT_CSFB_Request:
                        csfbRequest = time;
                        // twq20150528当收到 csfb request事件时,当成call attempt处理,走该流程
                        // break;
                        // 被叫响应
                    case DataSetEvent.ET_MT_Attempt:
                    case DataSetEvent.ET_AuthenticationRequest:
                    case DataSetEvent.ET_CMCallConfirm:
                        if (str.toLowerCase(Locale.getDefault()).indexOf("volte") > 0) {
                            isVoLTE = true;
                        }
                        if (!hasCallAttempt) {
                            hasCallAttempt = true;
                            timeAttempt = System.currentTimeMillis();
                            if (isGeneralMode)
                                writeRcuEvent(RcuEventCommand.ET_IPhoneMOAttempt, System.currentTimeMillis() * 1000);
                            else
                                // 存储事件1.1.1.9. Paging Response (0x0000006a)
                                writeRcuEvent(RcuEventCommand.PagingResponse, System.currentTimeMillis() * 1000);
                            LogUtil.w(tag, "---Incoming_Call_Attempt");
                            // 显示：被叫起呼
                            showEvent("Incoming Call Attempt");
                        }

                        if (isHangUpAfterAttempt) {
                            EventManager.getInstance().addTagEvent(MTCTest.this, System.currentTimeMillis(), "mtc is not volte");
                            sendNormalMessage(String.valueOf(STOP));
                            hangupDial(NORMAL_CALL);
                            sendCallBackStop(TestService.RESULT_SUCCESS);
                        }
                        break;

                    // 被叫振铃
                    case DataSetEvent.ET_MT_Alerting:
                        hasAlerting = true;
                        // 统计时延
                        timeAlerting = System.currentTimeMillis();
                        delay = (timeAttempt != 0 ? timeAlerting - timeAttempt : 0);
                        if (isGeneralMode)
                            writeRcuEvent(RcuEventCommand.ET_IPhoneMOSetup, System.currentTimeMillis() * 1000);
                        else
                            // 存事件 1.1.1.1. Alerting (0x00000061)
                            writeRcuEvent(RcuEventCommand.Alerting, System.currentTimeMillis() * 1000);
                        // 在这里无法接听,接听动作在mCallStateListener中
                        LogUtil.w(tag, "---Incoming_Call_Alerting");
                        // 显示: 振铃事件
                        if (hasCallAttempt) {
                            showEvent("Incoming Call Setup:" + df.format(Float.parseFloat(Long.toString(delay)) / 1000) + "s");
                        }

                        // 2014.3.6 预防某些手机的API可能没有振铃，通过信令事件来接听
                        new Thread() {
                            @Override
                            public void run() {
                                // 等5秒
                                long begin = System.currentTimeMillis();
                                while (!hasIncoming && !hasHangup && System.currentTimeMillis() - begin < 5 * 1000) {
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                // API没有振铃就接听
                                if (!hasIncoming) {
                                    LogUtil.w(tag, "---Incoming Call accept");
                                    sendAcceptCallBroadcast();
                                }
                            }
                        }.start();

                        break;

                    // 被叫接通 2014.5.21 增加从信令生成此事件(所以此事件会重复来)
                    case DataSetEvent.ET_MT_Connect:
                        //三星S8定制rom在进行视频通话测试中接通呼叫后会自动跳回到视频界面，此处再做回到测试界面操作
                        //2018.05.28:测试发现三星S8自定义rom被叫语音通话接通后也会出现系统自动跳转到通话界面，所以不用判断是否是视频通话
                        //2018.06.06:VivoY79A被叫有时未能正常回到walktour界面
                        LogUtil.i(TAG, "通知停止监控网络");
                        mConnectFlag = true;
                        if (Deviceinfo.getInstance().isS8() || Deviceinfo.getInstance().isVivo()) {
                            mPhone.closeCallScreen(200);
                        }
                        if (hasEstablishedFromCommand) {
                            return;
                        }
                        hasEstablishedFromCommand = true;
                        timeConnected = System.currentTimeMillis();
                        addStartTime(UtilsMethod.getLocalTimeByUTCTime() * 1000);
                        callendTime = 0;

                        if (isGeneralMode)
                            writeRcuEvent(RcuEventCommand.ET_IPhoneMTEstablished, System.currentTimeMillis() * 1000);
                        else
                            // TODO存储RCU事件 1.1.1.2. Connect (0x00000062)
                            writeRcuEvent(RcuEventCommand.Connect, System.currentTimeMillis() * 1000);

                        // twq20110908呼叫时延为起呼到振铃，连接，接通之最先到状态之间的时差
                        if (!hasAlerting) {
                            // 统计:接通时延
                            timeConnected = new Date().getTime();
                            delay = (timeAttempt != 0 ? timeConnected - timeAttempt : 0);
                        }

                        // 被叫接通
                        if (hasCallAttempt) {
                            showEvent("Incoming Call Established"
                                    + (hasAlerting ? "" : ":" + df.format(Float.parseFloat(Long.toString(delay)) / 1000) + "s"));
                            startPioneerTimer();
                            sendMsgToPioneer(String.format(Locale.getDefault(), ",DialState=%d,ConnectTime=%d", 1, delay));
                        }
                        // MOS测试
                        if (callModel.getCallMOSServer() == TaskPassivityCallModel.MOS_ON) {
                            // 2013.1.29向主叫发送
                            if (callModel.isUnitTest()) {
                                // 如果是MOS测试
                                new Thread(new MosSynchronizer()).start();
                            } else {
                                if (mCaculateModeFacade != null) {
                                    mCaculateModeFacade.setRcuFile(rcuFileName);
                                    mCaculateModeFacade.setModel(new TaskModelWrapper(callModel));
                                    mCaculateModeFacade.start();
                                }
                            }
                        }
                        break;

                    // 被叫失败
                    case DataSetEvent.ET_MT_Block:
                        isIdle = true;
                        if (hasCallAttempt && !hasEstablishedFromCommand) {
                            hangupDial(BLOCK_CALL);
                        } else {
                            LogUtil.w(tag, "---block call without call attempt");
                        }
                        break;

                    // 2014.3.20 新总说：把这个也开放出来先，预防会挂住
                    // 呼叫结束
                    case DataSetEvent.ET_MT_End:
                        isIdle = true;
                        callendTime = System.currentTimeMillis();
                        hangupDial(NORMAL_CALL);
                        sendMTEndEvent2TestService();
                        break;

                    // 2013.12.8暂时屏蔽数据集的事件(CallEnd和DropCall都不准，直接用API的IDLE判断)
                    // //掉话
                    // case DataSetEvent.ET_MT_Drop:
                    // if( !hasCallAttempt ){
                    // LogUtil.w(tag,"---drop call without call attempt");
                    // return ;
                    // }else{
                    // hangupDial(DROP_CALL);
                    // }
                    // break;

                }
            }

            // 同步完成消息
            // else if( intent.getAction().equals(WalkMessage.ACTION_UNIT_SYNC_DONE)
            // ){
            // String syncType = intent.getExtras().getString(
            // WalkMessage.KEY_UNIT_MSG );
            // LogUtil.i(tag,"---receive ACTION_UNIT_SYNC_DONE:"+syncType );
            // //拨打同步
            // if( syncType.equals( TestTaskService.MSG_UNIT_CALL ) ) {
            // hasSync = true;
            // }
            // }
            // 2012.10.09 主被叫同步改为使用Normal_Message

            // 来自服务端的常规消息
            else if (intent.getAction().equals(WalkMessage.ACTION_UNIT_NORMAL_RECEIVE)) {
                String msg = intent.getExtras().getString(WalkMessage.KEY_UNIT_MSG);
                LogUtil.i(tag, "receive normal msg:" + msg);
                // 同步MOS
                if (msg.equals(MSG_UNIT_MOS_START_ACK)) {
                    // 主叫响应MOS开始
                    hasMosStartAck = true;
                } else if (msg.equals(MSG_UNIT_MOS_STOP)) {
                    sendNormalMessage(MSG_UNIT_MOS_STOP_ACK);
                    // 开始下一次放音
                    new Thread(new MosSynchronizer()).start();
                } else if (msg.equals(MSG_UNIT_CALL_BLOCK)) {
                    // 收到主叫BLOCK_CALL的消息
                    hangupDial(BLOCK_FROM_MOC);
                } else if (msg.equals(MSG_UNIT_CALL_DROP)) {
                    // 收到主叫Drop_Call的消息,这时有可能被叫没有收到Call_Complete或Drop_Call信令（脱网无信令）
                    // 主叫DROP_CALL时，被叫插入NORMAL_CALL挂机事件(如果被叫这时已经返回DROP_CALL的信令，这里将不再做插入)
                    if (hasCallAttempt) {// 前提是已经有起呼信令，因为这个消息可能是来自主叫最上一次发过来的Drop
                        LogUtil.w(tag, "---drop call from moc ,but current MTC has no call attempt yet");
                        hangupDial(NORMAL_CALL);
                    }
                }

                // 主叫准备就绪
                else if (msg.equals(MSG_MOC_READY)) {
                    if (!hasIncoming) {
                        sendNormalMessage(MSG_MTC_READY);
                    } else {
                        // 这里是异常点，本次被叫由于异常未结束，主叫已经开始下一次
                        new Thread(new Terminator()).start();
                    }
                }

                // 主叫挂机
                else if (msg.equals(MSG_UNIT_MOC_HANGUP)) {
                    new Thread(new Terminator()).start();
                }

                // 主叫手机的MOC任务已经结束
                else if (msg.equals(MSG_UNIT_NOT_MOC_TASK)) {
                    new Thread(new Terminator()).start();
                }
                // 当收到主叫端请求获得UUID时，将当前的UUID返回给主叫
                else if (msg.equals(MSG_MOC_GETUUIDFROMMTC)) {
                    sendNormalMessage(MSG_MTC_SENDUUID2MOC + strUUID);
                } else if (msg.equals(String.valueOf(WAIT))) {
                    LogUtil.i(TAG, "moc need wait");
                    mNetSyncSignal = WAIT;
                    sendNormalMessage(RESQ);
                } else if (msg.equals(String.valueOf(EXEC))) {
                    LogUtil.i(TAG, "moc can execute");
                    mNetSyncSignal = EXEC;
                    sendNormalMessage(RESQ);
                } else if (msg.equals(String.valueOf(STOP))) {
                    LogUtil.i(TAG, "moc stop");
                    //                    hangupDial(NORMAL_CALL);
                    //                    sendCallBackStop(TestService.RESULT_SUCCESS);
                } else if (msg.equals(String.valueOf(RESQ))) {
                    LogUtil.i(TAG, "收到对方响应");
                    mRecMocResponse = true;
                }
            } else if (intent.getAction().equals(WalkMessage.ACTION_UNIT_MOS_RESET_BOX)) {
                // 如果当前为MOS盒测试,收到重置MOS盒消息后重
                LogUtil.w(TAG, "----MTC isMosBoxTest:" + isMosBoxTest + ",getMosTest:" + callModel.getCallMOSServer());
                if (isMosBoxTest && callModel.getCallMOSServer() == TaskInitiativeCallModel.MOS_ON) {
                    //2018/9/21：小背包底座已经做了缓存，在重连时无须调用BOX_INIT,调用了反而会出现重连后打分1分问题。因此注释掉下面代码。
					/*sendMosBoxTest(WalkMessage.ACTION_UNIT_MOS_BOX_INIT, "MT", 1, callModel.getCallMosCountStr(),
							callModel.getSampleRate());*/

                    // 等待同步成功,发送开始录放音指令
                    new Thread(new MosSynchronizer()).start();
                }
            }
        }
    };

    @Nullable
    private FileType getFileType() {
        FileType fileType = null;
        if (callModel.getCallMOSCount() == TaskModel.MOS_PESQ) {
            fileType = FileType.pesq_8k;
        } else if (callModel.getCallMOSCount() == TaskModel.MOS_POLQA) {
            if (callModel.getPolqaSample() == TaskModel.POLQA_16K) {
                fileType = FileType.polqa_16k;
            } else if (callModel.getPolqaSample() == TaskModel.POLQA_48K) {
                fileType = FileType.polqa_48k;
            } else {
                fileType = FileType.polqa_8k;
            }
        }
        return fileType;
    }

    /**
     * 添加开始时间
     */
    private void addStartTime(long startTime) {
        LogUtil.w(TAG, "----MTC addStartTime:" + startTime);
        File fileP = new File(iPackTerminal.mosFileName);
        String valx = null;
        if (fileP.exists())
            valx = FileUtil.getStringFromFile(fileP);
        if (null == valx || valx.trim().length() == 0) {
            FileUtil.writeToFile(fileP, startTime + "");
        } else {
            FileUtil.writeToFile(fileP, valx + "~" + startTime + "");
        }
    }

    /**
     * 添加结束时间
     */
    private void addEndTime(long endTime) {
        LogUtil.w(TAG, "----MTC addEndTime:" + endTime);
        File fileP = new File(iPackTerminal.mosFileName);
        String valx = null;
        if (fileP.exists())
            valx = FileUtil.getStringFromFile(fileP);
        if (null == valx || valx.trim().length() == 0) {
            FileUtil.writeToFile(fileP, "-" + endTime);
        } else {
            FileUtil.writeToFile(fileP, valx + "-" + endTime);
        }
    }

    /**
     * 手机通话状态监听
     */
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            // 响铃
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                isIdle = false;
                LogUtil.w(tag, "Call Number:" + incomingNumber);
                hasIncoming = true;

                // 发送接听命令到PhoneService
                // sendAcceptCallBroadcast();

                new Thread(new DelayAcceptCall()).start();
            }

            // 已经修改 成由信令来判断结束通话:BlockCall,NormalCall,Drop_Call
            if (state == TelephonyManager.CALL_STATE_IDLE) {
                isIdle = true;
                LogUtil.w(tag, "---CALL_STATE_IDLE");

                // //2013.12.8暂时屏蔽数据集的事件(不准，直接用API的IDLE判断)
                // if( hasEstablishedFromCommand ){
                // hangupDial( NORMAL_CALL );
                // }

                // 2012.11.17预防没有Call_Complete信令，
                if (hasIncoming && !hasCallAttempt) {
                    new Thread(new ThreadWaitHungup()).start();
                }
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                LogUtil.w(tag, "---CALL_STATE_OFFHOOK");
                isIdle = false;
            }
        }

        /**
         * 针对MIX 2手机有时候未接听的现象，
         * 将MIX的延时0.5改成延时1秒
         */
        class DelayAcceptCall implements Runnable {
            @Override
            public void run() {
                if (Deviceinfo.getInstance().getDevicemodel().equals("MIX 2")) {
                    UtilsMethod.ThreadSleep(1000);
                } else {
                    UtilsMethod.ThreadSleep(500);
                }
                sendAcceptCallBroadcast();
            }
        }

        @Override
        /** 手机信号改变回调函数 */
        public void onServiceStateChanged(ServiceState serviceState) {
            if (serviceState.getState() == ServiceState.STATE_IN_SERVICE) {
                // 更改当前信号状态为on
                isServiceOn = true;
                LogUtil.w(tag, "Service is on");
            } else {
                // 更改当前信号状态为off
                isServiceOn = !ConfigRoutine.getInstance().checkOutOfService();
                LogUtil.w(tag, "Service is off");
            }
        }

    };

    private class ThreadWaitHungup implements Runnable {
        @Override
        public void run() {

            long idleTime = System.currentTimeMillis();

            // 等待5秒
            while (isIdle) {
                // 如果已经结束或者应挂机，这里不再等待
                if (hasHangup || hasFinish) {
                    LogUtil.i(tag, "has hangup");
                    break;
                }

                long currentTime = System.currentTimeMillis();
                if (currentTime - idleTime > 5 * 1000) {
                    break;
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 如果未挂机(因为信令不正常收不到CALL_COMPLETE或DROP_CALL)
            if (!hasHangup) {
                LogUtil.d(tag, "----ThreadWaitHungup----hangupDial----");
                if (isServiceOn) {
                    // if( hasEstablishedFromCommand ){
                    // hangupDial( DROP_CALL );
                    // }else{
                    // hangupDial( BLOCK_CALL );
                    // }
                    // 2013.4.29信令不正常的情况下，不进行判断是DROP_CALL还是BLOCK_CALL
                    hangupDial(NORMAL_CALL);
                } else {
                    // 如果是脱网
                    hangupDial(OUT_OF_SERVER);
                }
            }

        }

    }

    // 注册广播接收器
    private void regedit() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.ACTION_EVENT);
        filter.addAction(WalkMessage.ACTION_PROPERTY);
        filter.addAction(WalkMessage.ACTION_UNIT_NORMAL_RECEIVE);
        filter.addAction(WalkMessage.ACTION_UNIT_MOS_RESET_BOX);
        filter.addAction(WalkMessage.ACTION_SYNC_NET_TYPE);
        this.registerReceiver(mEventReceiver, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(tag, "onBind");
        regedit();
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tag = "MTCTest";
        LogUtil.d(tag, "-----onCreate-----");
        mPhone = new MyPhone(this);
        regedit();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(tag, "-----onStartCommand-----");
        this.mNetType = intent.getStringExtra(WalkMessage.ACTION_SYNC_NET_TYPE);
        this.mCurrDevice = intent.getParcelableExtra(BluetoothMOSService.EXTRA_KEY_BLUETOOTH_MOS);
        this.mServerTimeOffset = intent.getLongExtra(BluetoothMOSService.EXTRA_KEY_SERVER_TIME_OFFSET, 0);
        rcuFileName = intent.getStringExtra(WalkMessage.TESTFILENAME);
        this.mHasPolqaType = intent.getBooleanExtra(EXTRA_HAS_POLQA_TYPE, false);
        if (!hasRegisteredBroadcast) {
            regedit();
            hasRegisteredBroadcast = true;
        }
        //当手机为三星定制rom手机时，开始测试时打开自动接听
        if (Deviceinfo.getInstance().isSamsungCustomRom()) {
            MyPhone.startS8AutoAnswer(this);
        }
        this.mServerTimeOffset = intent.getLongExtra(BluetoothMOSService.EXTRA_KEY_SERVER_TIME_OFFSET, 0);
        int startFlag = super.onStartCommand(intent, flags, startId);
        callModel = (TaskPassivityCallModel) super.taskModel;
        int channel = ConfigRoutine.getInstance().getMosBoxChannel(getApplicationContext());
        LogUtil.i(tag, "----channel:" + channel + "----isUnitTest:" + callModel.isUnitTest());
        // 只有MOS盒通道号大于0且当前为主被叫测试,才认为当前是MOS盒测试
        isMosBoxTest = (channel > 0) && callModel.isUnitTest();
        if (repeatTimes == 1 && isMosBoxTest && callModel.getCallMOSServer() == TaskInitiativeCallModel.MOS_ON) {
            sendMosBoxTest(WalkMessage.ACTION_UNIT_MOS_BOX_INIT, "MT", 1, callModel.getCallMosCountStr(),
                    callModel.getSampleRate());
        }
        //当机型为vivo并且做polqa算分时(现在加权限控制，不判断vivo机型了，根据权限)，将raw里加密的polqa文件复制到files目录下，算完分再删除
        if (mHasPolqaType && callModel.getCallMOSCount() == TaskModel.MOS_POLQA) {
            File file = AppFilePathUtil.getInstance().getAppFilesFile("PolqaLicenseFile.txt");
            UtilsMethod.writeRawResourceWithDecodeBase64(getApplicationContext(), R.raw.plf,
                    file, true);
            originalSerialNo = MyPhone.getSystemPropertyByShell("ro.serialno");
            LogUtil.d(TAG, "-----originalSerialNo:" + originalSerialNo + "-----");

            setCustomSerialNo("2970015a");
        }

        //		this.bindMOSService();
        // 发送广播通知TraceInfo服务：设定被叫测试开始标志位
        // sendBroadcast( new Intent( WalkMessage.ACTION_CALL_PsCallStart) );
        // 发送消息通知TRACE事件处理类呼叫开始，重置呼叫事件信息
        sendBroadcast(new Intent(WalkMessage.ACTION_CALL_BEFER_START));
        // 显示：MTC Start事件
        showEvent("MTC Start");
        // if (callModel.getCallMode() == 0) {
        // EventBytes.Builder(mContext, RcuEventCommand.MTC_START
        // ).writeToRcu(System.currentTimeMillis() * 1000);
        if (callModel.isUnitTest()) {
            strUUID = genGUID();
        } else {
            strUUID = genGUIDDefault();
        }

        EventBytes.Builder(mContext, RcuEventCommand.MTC_START).addInteger(-9999).addTguid(strUUID)
                .addStringBuffer(getSimNumber()).writeToRcu(System.currentTimeMillis() * 1000);


        mCaculateModeFacade = new CaculateModeFacade(MTCTest.this);
        mCaculateModeFacade.setDevice(mCurrDevice);

        LogUtil.d(tag, "-------add mPhoneStateListener-------------");
        // 开始测试:监听通话状态
        telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE | PhoneStateListener.LISTEN_SERVICE_STATE);
        if (this.mWaitForIncomingCallThread == null) {
            this.mWaitForIncomingCallThread = new WaitForIncomingCallThread();
            this.mWaitForIncomingCallThread.start();
        }
        return startFlag;
    }

    private void setCustomSerialNo(String value) {
        if (Deviceinfo.getInstance().isSamsungCustomRom()) {
            MethodManager mMethodManager;
            mMethodManager = MethodManager.from(this);
            mMethodManager.setRoProperty(value);
        } else if (Deviceinfo.getInstance().isVivo()) {
            new DeviceAssistant().setSerialNo(value);
        } else if (Deviceinfo.getInstance().isXiaomi()) {
            UtilsMethod.runRootCommand("setprop ro.serialno " + value);
        }
    }

    @Override
    public void onDestroy() {
        if (mCaculateModeFacade != null) {
            mCaculateModeFacade.stop();
        }
        hasOnDestroy = true;
        //如果手机时S8定制rom，在任务结束时关闭自动接听操作
        if (Deviceinfo.getInstance().isSamsungCustomRom()) {
            MyPhone.stopS8AutoAnswer(this);
        }

        //当机型为vivo并且做polqa算分时(现在加权限控制，不判断vivo机型了，根据权限)，在测试结束时删除复制过来的polqa文件
        if (mHasPolqaType && callModel.getCallMOSCount() == TaskModel.MOS_POLQA) {
            if (!originalSerialNo.isEmpty()) {
                setCustomSerialNo(originalSerialNo);
            }
            FileUtil.deleteFile(AppFilePathUtil.getInstance().getAppFilesFile("PolqaLicenseFile" +
                    ".txt").getAbsolutePath());
        }


        LogUtil.d(tag, "-----onDestroy-----");
        if (telManager != null) {
            telManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (this.mWaitForIncomingCallThread != null) {
            this.mWaitForIncomingCallThread.stopThread();
            this.mWaitForIncomingCallThread = null;
        }

        hasFinish = true;
        mHandler.removeMessages(0);
        if (hasRegisteredBroadcast) {
            LogUtil.w(TAG, "-------MTC UnRegisterBroadcast-----");
            this.unregisterReceiver(mEventReceiver);
            hasRegisteredBroadcast = false;
        }

        mCallbacks.kill();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    /**
     * 开始被叫测试：等待来电
     */
    private class WaitForIncomingCallThread extends Thread {

        private boolean isStop = false;

        @Override
        public void run() {
            int sleepTime = 200;
            // 等待注册完成
            LogUtil.w(tag, "---waiting for callback registed----start----");
            while (!isStop && !hasCallbackRegisted) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    break;
                }
            }
            LogUtil.w(tag, "---waiting for callback registed----end----");
            if (!hasCallbackRegisted) {
                return;
            }
            // 如果是主被叫联合测试,发送被叫就绪消息
            if (callModel.isUnitTest()) {
                sendNormalMessage(MSG_MTC_READY);
            }

            if (callModel.isUnitTest()) {
                //czc:通话前检测网络类型是否与ipack下发网络类型一致——>>针对电信多网测试
                waitIfNetUnavailable();
            }

            // Log等待CallAttempt
            int useTime = 0;
            int maxTime = 1000 * 60 * 30;
            LogUtil.w(tag, "---waiting for incoming call----start----");
            while (!isStop && isTesting && !hasCallAttempt && !hasIncoming && !hasAlerting) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    break;
                }
                useTime += sleepTime;
                if (useTime >= maxTime) {
                    if (!hasCallAttempt) {
                        LogUtil.w(tag, "----time out and stop test----");
                        // 结束本次通话，
                        sendCallBackStop(TestService.RESULT_SUCCESS);
                    }
                    break;
                }
            }
            LogUtil.w(tag, "---waiting for incoming call----end----hasCallAttempt:" + hasCallAttempt + "----hasIncoming:" + hasIncoming + "----hasAlerting:" + hasAlerting);
            if (isStop)
                return;
            useTime = 0;
            if (callModel.isUnitTest()) {
                maxTime = 1000 * 60 * 10;// 10 mins
            } else {
                maxTime = 10 * 1000;
            }
            LogUtil.w(tag, "----waiting for Established--start----");
            // 如果当前有振铃,监控是否有接通,如果没有接通.五秒后结束当前测试
            while (!isStop && (hasCallAttempt || hasIncoming || hasAlerting) && !hasEstablishedFromCommand) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                useTime += sleepTime;
                // 如果5S内未收到接通事件,结束当前测试
                if (useTime >= maxTime) {
                    LogUtil.w(tag, "----waiting for Established----time out----");
                    hangupDial(INTERRUPT);
                    sendCallBackStop(TestService.RESULT_SUCCESS);
                    break;
                }
            }
            LogUtil.w(tag, "---waiting for Established--end----time:" + useTime);
        }

        /**
         * 停止线程
         */
        public void stopThread() {
            this.isStop = true;
            this.interrupt();
        }

        //		/** 等待同步完成 */
        // private void waitForSyncDone(){
        // // //发送MTC消息给主被叫控制服务端(UMPC/iPad)
        // // Intent intent = new Intent ( WalkMessage.ACTION_UNIT_SYNC_START );
        // // intent.putExtra( WalkMessage.KEY_UNIT_MSG,
        // TestTaskService.MSG_UNIT_CALL );
        // // sendBroadcast( intent );
        // //
        // LogUtil.i(tag,"---send
        // "+WalkMessage.ACTION_UNIT_SYNC_START.toString()+":"
        // // +TestTaskService.MSG_UNIT_CALL );
        //
        // for(int i=0;i< 60*30 && !hasSync ;i++){
        // LogUtil.i(tag, "---wait for moc ready");
        // try {
        // Thread.sleep( 1*1000 );
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
        // }

    }

    private void waitIfNetUnavailable() {
        boolean isMonitorNetType = !mNetType.equals(DEFAULT_NET_TYPE);
        if (isMonitorNetType && !isInterrupted) {
            String currentNetType = MyPhoneState.getInstance().parseNetType(MTCTest.this, mNetType);
            EventManager.getInstance().addTagEvent(MTCTest.this, System.currentTimeMillis(), "current net type:" + currentNetType);
            int time = 0;
            while (!mNetType.equals(currentNetType)) {
                try {
                    sendNormalMessage(String.valueOf(WAIT));
                    currentNetType = MyPhoneState.getInstance().parseNetType(MTCTest.this, mNetType);
                    if (time % 60 == 0) {
                        EventManager.getInstance().addTagEvent(MTCTest.this, System.currentTimeMillis(), "current net type is " + currentNetType + ",it does't match " + mNetType);
                    }
                    time++;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            do {
                sendNormalMessage(String.valueOf(EXEC));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!mRecMocResponse && !isInterrupted);

            time = 0;
            while (mNetSyncSignal.equals(WAIT) && !isInterrupted) {
                try {
                    if (time % 60 == 0) {
                        EventManager.getInstance().addTagEvent(MTCTest.this, System.currentTimeMillis(), "moc net type does't match,waiting···");
                    }
                    Thread.sleep(1000);
                    time++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!isInterrupted && !hasOnDestroy && !mConnectFlag) {
                        try {
                            String currentNetType = MyPhoneState.getInstance().parseNetType(MTCTest.this, mNetType);
                            if (!mNetType.equals(currentNetType) && !mConnectFlag) {
                                String str = String.format("mtc current net type does't match : %s", mNetType);
                                EventManager.getInstance().addTagEvent(MTCTest.this, System.currentTimeMillis(), str);
                                LogUtil.i(TAG, str);
                                sendNormalMessage(String.valueOf(STOP));
                                hangupDial(NORMAL_CALL);
                                sendCallBackStop(TestService.RESULT_SUCCESS);
                                break;
                            }
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    /**
     * 初始化音量管理
     *
     * @param isBluetoothMOS 当前是否蓝牙MOS测试
     */
    private void setVoice(boolean isBluetoothMOS) {
        // 音频管理器
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        // 调节媒体播放器的音量至5(实验证明此时音质最好)
        int mediaVolume = 0;
        int voiceVolume = 0;
        if (isBluetoothMOS) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            int mediaMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int voiceMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
            mediaVolume = Deviceinfo.getInstance().getMicroMosMediaVolume();
            voiceVolume = Deviceinfo.getInstance().getMicroMosVoiceVolume();
            if (mediaMaxVolume < mediaVolume)
                mediaVolume = mediaMaxVolume;
            if (voiceMaxVolume < voiceVolume)
                voiceVolume = voiceMaxVolume;
        } else {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mediaVolume = Deviceinfo.getInstance().getMediaVoice();
            voiceVolume = mediaVolume;
        }
        LogUtil.d(tag, "mediaVolume = " + mediaVolume + ";voiceVolume=" + voiceVolume);
        audioManager.setSpeakerphoneOn(false);
        audioManager.setMicrophoneMute(false);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mediaVolume, AudioManager.FLAG_SHOW_UI);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, voiceVolume, AudioManager.FLAG_SHOW_UI);
        muteSystemOrNotification(true);
    }

    /**
     * 关闭或开启系统音或提示音
     *
     * @param isMute 是否关闭
     */
    private void muteSystemOrNotification(boolean isMute) {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, isMute);
        audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, isMute);
        if (!isMute)
            audioManager.setMode(AudioManager.MODE_NORMAL);
    }

    /**
     * 初始化MOS头播放设置
     *
     * @param syncTime 是否同步整点时间
     */
    private void initMosPlayer(boolean syncTime) {
        LogUtil.i(tag, "-----initMosPlayer-----syncTime:" + syncTime);
        if (isInitMosPlayer) {
            if (mCurrDevice != null && hasMOSConnected) {
                setVoice(true);
            } else {
                setVoice(false);
            }
            isInitMosPlayer = false;
            //非MOS蓝牙设备测试时做同步操作
            if (syncTime && !MyPhone.isHeadsetOn(mContext))
                this.waitNextTenSecond();
        }
    }

    /**
     * 等待下一个10秒执行当前业务，通过这个方法来减少录放音不同步的问题，该处理方法默认两台手机的时间是同步的
     */
    private void waitNextTenSecond() {
        Calendar date = Calendar.getInstance();
        long serverTime = System.currentTimeMillis() - this.mServerTimeOffset;
        LogUtil.d(tag, "----waitNextTenSecond----serverTime:" + UtilsMethod.sdFormat.format(new Date(serverTime)));
        date.setTimeInMillis(serverTime);
        int second = date.get(Calendar.SECOND);
        int mis = date.get(Calendar.MILLISECOND);
        long waitTime = 10 - (second % 10);
        waitTime = waitTime * 1000 - mis;
        LogUtil.i(tag, "-----waitNextTenSecond-----waitTime:" + waitTime);
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * MOS放音线程,一个线程只放音一次
     */
    private class MosPlayer extends Thread {
        private final int TIME_PLAY = 8;// 播放时长

        @Override
        public void run() {
            LogUtil.i(tag, "-----start mos player-----");

            //如果被叫手机插着mos头（此处检测到耳机口插入状态则认为是mos头），被叫手机本身不做放音操作，
            //放音动作在主叫手机端控制插在被叫手机mos头放音。
            if (MyPhone.isHeadsetOn(mContext))
                return;

            MyAudioPlayer player = null;
            IAudioManager iAudioManager;
            AudioManagerFactory.AudioManagerType audioManagerType = null;
            if (Deviceinfo.getInstance().isS8CustomRom()) {
                audioManagerType = AudioManagerFactory.AudioManagerType.SAMSUNG;
            } else if (Deviceinfo.getInstance().isVivo()) {
                audioManagerType = AudioManagerFactory.AudioManagerType.VIVO;
            } else if (Deviceinfo.getInstance().isXiaomi()) {
                audioManagerType = AudioManagerFactory.AudioManagerType.XIAOMI;
            }
            AudioManagerFactory factory = new AudioManagerFactory();
            iAudioManager = factory.getAudioManager(MTCTest.this, audioManagerType);
            if (mCurrDevice == null || !hasMOSConnected) {
                int rawId = 0;
                if (callModel.getCallMOSCount() == TaskModel.MOS_PESQ) {
                    rawId = R.raw.pesqvoice;
                } else if (callModel.getCallMOSCount() == TaskModel.MOS_POLQA) {
                    // rawId = R.raw.polqavoice_nb;
                    if (callModel.getPolqaSample() == TaskModel.POLQA_16K) {
                        rawId = R.raw.sample_wb_16k;
                    } else if (callModel.getPolqaSample() == TaskModel.POLQA_48K) {
                        rawId = R.raw.sample_swb_48k;
                    } else {
                        rawId = R.raw.sample_nb_8k;
                    }
                }
                if (iAudioManager != null) {
                    iAudioManager.startPlaying(rawId);
                } else {
                    player = new MyAudioPlayer(MTCTest.this, rawId);
                    // 播放
                    player.startPlayer();
                }
            } else {
                FileType fileType = getFileType();
                if (fileType != null) {
                    try {
                        if (!hasMOSPower && mMOSService.getDevicePower() < AlertManager.MOS_POWER_LOW) {
                            LogUtil.d(tag, "-----mos power low-----");
                            hasMOSPower = true;
                            AlertManager.getInstance(mContext).addDeviceAlarm(WalkStruct.Alarm.MT_MOS_POWER_LOW, -1);
                        }
                        if (!mMOSService.runPlayback(fileType.getName())) {
                            LogUtil.i(tag, "-----mos player error-----");
                            return;
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            // 播放计时
            try {
                //VIVO方案录音为保证录音文件时长8秒，此处当设备时vivo手机时，让线程休眠8.5秒
                Thread.sleep(TIME_PLAY * 1000 + (Deviceinfo.getInstance().isVivo() ? 500 : 0));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 停止播放
            LogUtil.i(tag, "stop play audio file");
            if (player != null)
                player.stopPlayer();
            if (iAudioManager != null) {
                iAudioManager.stopPlaying();
            }
        }

    }

    /**
     * 主被叫联合同步测试的MOS测试的同步器 仅当是主被叫联合测试时才使用这个同步器 如果是普通的主被叫测试不能使用此同步器
     */
    private class MosSynchronizer implements Runnable {

        @Override
        public void run() {

            if (!isMOSSynchronizing) {
                waitForSync();
            }

            isMOSSynchronizing = false;
        }

        private void waitForSync() {
            hasMosStartAck = false;

            long sendTime = System.currentTimeMillis();// 发送的时间
            sendNormalMessage(TestTaskService.MSG_UNIT_MOS_START);
            LogUtil.i(tag,
                    "---send " + WalkMessage.ACTION_UNIT_NORMAL_SEND.toString() + ":" + TestTaskService.MSG_UNIT_MOS_START);
            // 等待主叫响应
            int i = 0;
            while (!hasMosStartAck && !hasFinish) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (i++ % 5 == 0) {
                    LogUtil.w(tag, "---wait for mos start ack");
                }
                if (!hasMosStartAck && System.currentTimeMillis() - sendTime > 10 * 1000) {
                    sendNormalMessage(TestTaskService.MSG_UNIT_MOS_START);
                    LogUtil.i(tag,
                            "---send " + WalkMessage.ACTION_UNIT_NORMAL_SEND.toString() + ":" + TestTaskService.MSG_UNIT_MOS_START);
                    sendTime = System.currentTimeMillis();
                }
            }

            if (hasMosStartAck && !hasFinish) {
                // 收到主叫录音应答之后,如果当前是小背包的MOS盒子测试,不初始化播放器,只通知道盒子
                // 正常放音之后收到主叫的录音结束消息之后,停止放音,进入等待下次放音同步等待,MOS盒主叫不会发送录音停止消息
                if (isMosBoxTest) {
                    sendMosBoxTest(WalkMessage.ACTION_UNIT_MOS_BOX_TEST, "MT", 1, callModel.getCallMosCountStr(),
                            callModel.getSampleRate());
                } else {
                    initMosPlayer(false);
                    new MosPlayer().start();
                }
            }

            hasMosStartAck = false;// 重置条件
        }
    }

    /**
     * 终结者进程 收到主叫进程退出的消息时，被叫进程等待一段时间(因为有可能是主叫挂机后被叫收到CALL_END再挂机)，
     * 如果本进程仍然未退出，说明这时是主叫接通后挂机但被叫并未接通(GSM下),这时按正常挂机结束本次测试。
     */
    private class Terminator implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 6 && !hasFinish; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LogUtil.w(tag, "---wait for finish");
            }

            // 如果等待6秒后仍未退出，结果此进程
            if (!hasFinish) {
                hangupDial(NORMAL_CALL);
            }
        }
    }

    /**
     * 插入HANGUP_DIAL事件到文件、页面显示、作挂机动作
     *
     * @param reason 挂机的原因
     */
    private synchronized void hangupDial(int reason) {
        // 防止重复挂机
        if (!hasHangup) {
            hasHangup = true;
            addEndTime(UtilsMethod.getLocalTimeByUTCTime() * 1000);
            if (isMosBoxTest && callModel.getCallMOSServer() == TaskInitiativeCallModel.MOS_ON) {
                sendMosBoxTest(WalkMessage.ACTION_UNIT_MOS_BOX_TEST, "MT", 0, callModel.getCallMosCountStr(),
                        callModel.getSampleRate());
            }

            // 发送消息到Pioneer
            stopPioneerTimer();
            sendMsgToPioneer(String.format(",DialState=%d", 0));
            long time = System.currentTimeMillis() * 1000;
            switch (reason) {
                case NORMAL_CALL: // 正常通话结束
                    // 存储RCU事件 1.1.1.11. Disconnect (0x0000006c) location -1 cause :16
                    if (isGeneralMode)
                        writeRcuEvent(RcuEventCommand.ET_IPhoneMOCallEnd, time, -1, 16);
                    else
                        writeRcuEvent(RcuEventCommand.Disconnect, time, -1, 16);
                    LogUtil.w(tag, "---Call_Complete");

                    // 显示:主叫结束
                    if (hasCallAttempt) {
                        LogUtil.w(tag, "Incoming Call End");
                        showEvent("Incoming Call End");
                    }

                    // 存储RCU事件：正常挂机
                    LogUtil.w(tag, "---write event to RCU file:RcuEventCommand.HANGUP_DIAL");
                    writeRcuEvent(RcuEventCommand.HANGUP_DIAL, time, NORMAL_CALL);
                    break;

                case BLOCK_CALL: // 未接通
                    // 显示：被叫失败
                    if (hasCallAttempt) {
                        LogUtil.w(tag, "---Incoming_Call_Failure");
                        showEvent("Incoming Blocked Call");
                    }
                    // 存储RCU事件：未接通挂机
                    LogUtil.w(tag, "---write event to RCU file:RcuEventCommand.HANGUP_DIAL:BLOCK_CALL");
                    writeRcuEvent(RcuEventCommand.HANGUP_DIAL, time, BLOCK_CALL);
                    break;

                case OUT_OF_SERVER:// 脱网
                case DROP_CALL: // 掉话
                    LogUtil.w(tag, "---drop:" + reason);
                    if (hasCallAttempt) {
                        if (hasEstablishedFromCommand) {
                            // 存储RCU事件：掉话等异常挂机事件
                            LogUtil.w(tag, "---event:HANGUP_DIAL:DROP_CALL");
                            writeRcuEvent(RcuEventCommand.HANGUP_DIAL, time, reason);
                            // 显示:主叫结束(呼叫掉话)
                            showEvent("Incoming Drop Call");

                            hasDrop = true;
                        }
                    }
                    break;

                case INTERRUPT: // 手工停止
                    // 存储RCU事件：掉话等异常挂机事件
                    LogUtil.w(tag, "---write event:RcuEventCommand.HANGUP_DIAL:INTERRUPT");
                    writeRcuEvent(RcuEventCommand.HANGUP_DIAL, time, INTERRUPT);
                    break;

                case BLOCK_FROM_MOC:// 主叫通知的挂机
                    // 存储RCU事件：正常挂机
                    LogUtil.w(tag, "---write event to RCU file:RcuEventCommand.HANGUP_DIAL");
                    writeRcuEvent(RcuEventCommand.HANGUP_DIAL, time, NORMAL_CALL);
                    break;

            }

            // 发送统计数据
            sendChartData();

            LogUtil.w(tag, "---call back call end release success---");
            // 显示Hangup事件
            showEvent("Hangup Dial");

            // 2012.3.16防止不正常的情况出现,被叫也进行挂机
            hangup();
            clearTimePlayer();
            // 手工停止时不能使用TEST_STOP回调，否则会导致这个进程退出有问题
            if (reason != INTERRUPT) {
                // 计入一次通话，
                sendCallBackStop(TestService.RESULT_SUCCESS);
            }

            if (mCaculateModeFacade != null) {
                mCaculateModeFacade.stop();
            }


            // 等待消息发送成功
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 发送挂机命令
     */
    private synchronized void hangup() {
        // 2014.6.16 部分手机在idle状态挂机会关屏
        LogUtil.w(tag, "hangup(),isIdle=" + isIdle);
        if (!isIdle) {
            mPhone.endCall();
            isAccept = false;
        }
    }

    /**
     * 发送接听命令
     */
    private void sendAcceptCallBroadcast() {
        if (!isAccept) {
            mPhone.aceeptCall();
            isAccept = true;
        }

    }

    protected synchronized void showEvent(String event) {
        Message msg = mHandler.obtainMessage(EVENT_CHANGE, event);
        msg.sendToTarget();
    }

    /**
     * 增加统计数据
     *
     * @param key
     * @param value
     */
    private void addChartData(TotalDial key, int value) {
        map.put(key.name(), value);
    }

    /**
     * 发送统计数据
     */
    private void sendChartData() {

        // CSFB统计
        if (csfbRequest > 0) {
            addChartData(TotalDial._csfb_mt_request, 1);
            addChartData(TotalDial._mtTrys, 1);

            if (csfbRrcRelease > 0) {
                int rrcReleaseDelay = (int) (csfbRrcRelease - csfbRequest);
                addChartData(TotalDial._csfb_mt_RrcRelease, 1);
                addChartData(TotalDial._csfb_mt_RrcReleaseDelay, rrcReleaseDelay);
                // 回落时延
                if (csfbProceeding > 0) {
                    int delayProceeding = (int) (csfbProceeding - csfbRrcRelease);
                    switch (csfbProceedingNetType) {
                        case UnifyParaID.NET_GSM:
                        case UnifyParaID.NET_CDMA_EVDO:
                            addChartData(TotalDial._csfb_mt_proceeding_2G, 1);
                            addChartData(TotalDial._csfb_mt_proceedingDelay_2G, delayProceeding);
                            break;
                        case UnifyParaID.NET_TDSCDMA:
                        case UnifyParaID.NET_WCDMA:
                            addChartData(TotalDial._csfb_mt_proceeding_3G, 1);
                            addChartData(TotalDial._csfb_mt_proceedingDelay_3G, delayProceeding);
                            break;
                    }

                    if (callendTime > 0) {
                        addChartData(TotalDial._csfb_mt_CallEnd, 1);
                    }
                }
            }

            if (timeAlerting > 0) {
                addChartData(TotalDial._csfb_mt_alerting, 1);
                // twq20160421 只有alertTime存在的时候,才根据是否有接通去计时延及接通次数,否则抛掉当前时延
                if (timeConnected > 0) {
                    int delaySuccess = (int) (timeAlerting - csfbRequest);
                    addChartData(TotalDial._csfb_mt_SuccessDelay, delaySuccess);
                    addChartData(TotalDial._csfb_mt_Established, 1);
                    addChartData(TotalDial._mtConnects, 1);
                    addChartData(TotalDial._mtCalldelay, delaySuccess);
                    addChartData(TotalDial._mtDelaytimes, 1);
                }
            }

            if (hasDrop) {
                addChartData(TotalDial._csfb_mt_drop, 1);
                addChartData(TotalDial._mtDropcalls, 1);
            }
        }

        // 普通2G/3G统计
        else if (timeAttempt > 0) {
            boolean isVideo = callModel.getCallMode() == 1;

            // 统计:拨打次数
            if (isVideo) {
                addChartData(TotalDial._video_mtTrys, 1);
            } else {
                addChartData(TotalDial._mtTrys, 1);
                if (isVoLTE) {
                    addChartData(TotalDial._volte_mtTrys, 1);
                }
            }
            if (timeConnected > 0) {
                if (isVideo) {
                    addChartData(TotalDial._video_mtConnects, 1);
                } else {
                    addChartData(TotalDial._mtConnects, 1);
                    if (isVoLTE) {
                        addChartData(TotalDial._volte_mtConnects, 1);
                    }
                }
                int delayConnect = (int) ((timeAlerting > 0 ? timeAlerting : timeConnected) - timeAttempt);
                if (isVideo) {
                    addChartData(TotalDial._video_mtCalldelay, delayConnect);
                    addChartData(TotalDial._video_mtDelaytimes, 1);
                } else {
                    addChartData(TotalDial._mtCalldelay, delayConnect);
                    addChartData(TotalDial._mtDelaytimes, 1);
                    if (isVoLTE) {
                        addChartData(TotalDial._volte_mtCalldelay, delayConnect);
                        addChartData(TotalDial._volte_mtDelaytimes, 1);
                    }
                }
            }

            if (hasDrop) {
                if (isVideo) {
                    addChartData(TotalDial._video_mtDropcalls, 1);
                } else {
                    addChartData(TotalDial._mtDropcalls, 1);
                    if (isVoLTE) {
                        addChartData(TotalDial._volte_mtDropcalls, 1);
                    }
                }
            }
        }

        if (!map.isEmpty()) {
            Message msg = mHandler.obtainMessage(CHART_CHANGE, map);
            msg.sendToTarget();
        }
    }


}