package com.walktour.service.test;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.ril.com.datangb2b.MethodManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.dinglicom.dataset.EventManager;
import com.dinglicom.dataset.model.DataSetEvent;
import com.vivo.api.netcustom.interfaces.DeviceAssistant;
import com.voix.RVoixSrv;
import com.voix.VoiceRecorder;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.TotalStruct.TotalDial;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.DateUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhone;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.R;
import com.walktour.gui.mos.CaculateModeFacade;
import com.walktour.gui.mos.TaskModelWrapper;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.service.ICallback;
import com.walktour.service.IService;
import com.walktour.service.TestService;
import com.walktour.service.bluetoothmos.BluetoothMOSService;
import com.walktour.service.bluetoothmos.BluetoothMTCService;
import com.walktour.service.bluetoothmos.IBluetoothMOSServiceBinder;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;
import com.walktour.service.iPackTerminal;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.opticom.utils.PolqaCalculator;

/**
 * @Serivice 语音主叫测试
 * @descrition 业务流程参考文档《语音业务测试行为规范》 实现拨打的功能请查看com.walktour.phone包
 * PhoneService接收到此广播后会进行拨打动作
 * PhoneService是后台程序com.walktour.phone中的服务,
 * com.walktour.phone需要单独编译和安装
 */
public class MOCTest extends TestTaskService {
    public static final String EXTRA_HAS_POLQA_TYPE = "has_polqa_type";
    private boolean mHasPolqaType = false;
    private static final String TAG = "MOCTest";

    // 挂机的原因码，请参考RCU事件存储结构旧事件中的语音挂机
    private final int NORMAL_CALL = 0; /* 正常通话结束挂机标志 */
    private final int BLOCK_CALL = 1; /* 未接通挂机标志 */
    private final int DROP_CALL = 2; /* 掉话挂机标志 */
    private final int INTERRUPT = 6; /* 手工停测试 */
    private boolean isCallbackRegister = false; /* 与测试服务绑定成功状态 */
    private boolean isPhoneStateCallback = false; /* 与测试服务绑定成功状态 */
    private long delay = 0;

    /**
     * FLAG ,用于接收信令
     */
    public static final String KEY_CALL_DELAY = "delay";// 主叫接通时延

    private int mCallTime;
    private DecimalFormat df = new DecimalFormat("##0.000");
    private static final int EDN_CALL = 100;

    // 记录接收到的信令
    private long callAttemptTime = 0;
    private long alertTime = 0;
    private long connectedTime = 0;
    private long callendTime = 0;
    // private boolean isOffHook = false; //是否拿起了电话(API中没有正在通话的状态)
    private boolean isIdle = false; // 手机是否空闲状态,
    private boolean hasEstablishedFromCommand = false;
    private boolean hasFinish = false;    // 是否已经挂机或着掉话,用区分判断手工在通话屏幕挂机
    private boolean hasHanupEvt = false;    //是否已写Hangup事件
    private boolean timeout = false;
    private boolean hasDrop = false;
    private boolean hasUUIDByUnitTest = false; // 主被叫联合测试时，已获得被叫端的UUID
    private String unitTestUuidByMTC = ""; // 主被叫联合时的被叫端的UUID

    private long csfbRequest = 0;
    private long csfbProceeding = 0;
    private int csfbProceedingNetType = 0;// CSFB回落网络类型
    // CSFB RRC Release 时延
    private long csfbRrcRelease = 0;// RRC Release时延,从CSFB Request到Originating

    private boolean isVoLTE = false;
    // 测试模型
    private TaskInitiativeCallModel callModel;

    private TelephonyManager telManager;
    private MyPhone mPhone;

    private boolean isTestInterrupt = false;

    // 计时器
    private Timer timer;
    private TimerTask timerTask;

    /**
     * 主被叫同步信号，WAIT为等待，EXEC为可执行
     */
    public static final String WAIT = "wait";
    public static final String EXEC = "exec";
    public static final String STOP = "stop";
    public static final String RESQ = "resq";
    private boolean mRecMtcResponse;//判断是否收到被叫响应
    private boolean mConnectFlag; // 是否接通标识
    private String mNetSyncSignal = WAIT;
    //如果为 NONE，则不需要处理主被叫网络同步
    private static final String DEFAULT_NET_TYPE = "NONE";

    // POLQA 算分
    private PolqaCalculator polqaCalculator;

    private String rcuFileName = "";

    /**
     * 是否正在做MOS的流程(确保一个时候只能有一个录音线程)
     */
    private boolean isMosRunning = false;
    /**
     * 主叫算分完成后发送被叫，被叫是否收到并响应
     */
    private boolean hasMosStopAck = false;
    /**
     * 当前是否小背包的MOS盒子测试
     */
    private boolean isMosBoxTest = false;

    /**
     * 统计
     */
    private HashMap<String, Integer> map = new HashMap<>();

    /**
     * 是否已注册mEventReceiver广播接收者
     */
    private boolean hasRegisteredEventBroadcast = false;

    private Context mContext;

    /**
     * 当前关联的主叫设备
     */
    private BluetoothMOSDevice mCurrMOCDevice;
    /**
     * 当前关联的被叫设备
     */
    private BluetoothMOSDevice mCurrMTCDevice;
    /**
     * 是否起呼录音（iPack下发）
     */
    private boolean mIsRecordCall = false;
    /**
     * 用于蓝牙MOS测试录放音同步问题，该值为当前终端时间减去服务器获取到的时间的值
     */
    private long mServerTimeOffset = 0;
    /**
     * 是否已连接主叫蓝牙MOS服务
     */
    private boolean hasMOCConnected = false;
    /**
     * 是否已获取主叫蓝牙MOS的电量
     */
    private boolean hasMOCPower = false;
    /**
     * 主叫蓝牙MOS头服务
     */
    private IBluetoothMOSServiceBinder mMOCService = null;

    /**
     * 是否已连接被叫蓝牙MOS服务
     */
    private boolean hasMTCConnected = false;
    /**
     * 是否已获取被叫蓝牙MOS的电量
     */
    private boolean hasMTCPower = false;
    /**
     * 被叫蓝牙MOS头服务
     */
    private IBluetoothMOSServiceBinder mMTCService = null;
    private StartToTestThread mStartToTestThread = null;
    /**
     * 是否初始化录音设置
     */
    private boolean isInitMosRecorder = true;
    /**
     * 当前是否是通用版
     */
    private boolean isGeneralMode = ApplicationModel.getInstance().isGeneralMode();

    //使用内置polqa算分文件操作过程需要保存手机原始SeriaNo
    private String originalSerialNo;
    /**
     * 起呼开始录制音频的录音器(vivo Y79A需求：起呼就开始录音，超过30秒未接通，保存录音文件)
     */
    private VoiceRecorder mVoiceRecorder;
    /**
     * 起呼录制音频文件地址(vivo Y79A需求：起呼就开始录音，超过30秒未接通，保存录音文件)
     */
    private String recordCallFilePath;
    private String mNetType = DEFAULT_NET_TYPE;
    private CaculateModeFacade mCaculateModeFacade;

    private MyHandler mHandler = new MyHandler(this);

    // 注册回调接口
    private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<>();
    private IService.Stub mBinder = new IService.Stub() {

        public void unregisterCallback(ICallback cb) {
            if (cb != null) {
                mCallbacks.unregister(cb);
                cb = null;
                isCallbackRegister=false;
            }
        }

        public void registerCallback(ICallback cb) {
            if (cb != null) {
                LogUtil.w(TAG,"mBinder registerCallback.");
                mCallbacks.register(cb);
                isCallbackRegister = true;
            }
        }

        public synchronized void stopTask(boolean isTestInterrupt, int dropReason) throws
                RemoteException {
            LogUtil.i(TAG, String.format(Locale.getDefault(), "===stopTask,isInterrupt:%b," +
                    "reason:%d", isTestInterrupt, dropReason));
            MOCTest.this.isTestInterrupt = isTestInterrupt;

            if (isTestInterrupt) {
                hangupDial(INTERRUPT);
            } else if (dropReason == RcuEventCommand.DROP_OUT_OF_SERVICE) {

                LogUtil.e(TAG, "out of service");

                showEvent(getDropReasonString(dropReason));

                /*
                 * 2013.3.20 脱网后大部分情况无信令(恢复网络后才会有DROP_CALL的信令)这里会和DROP_CALL同样处理
                 */
                hangupDial(RcuEventCommand.DROP_OUT_OF_SERVICE);

                LogUtil.e(TAG, "stopTask end");
            }

            new Thread(new MonitorOnDestroy()).start();
        }

        /**
         * 返回是否执行startCommand状态, 如果改状态需要在业务中出现某种情况时才为真, 可以在继承的业务中改写该状态
         */
        public boolean getRunState() {
            return startCommondRun;
        }
    };

    private static class MyHandler extends Handler {
        private WeakReference<MOCTest> reference;

        public MyHandler(MOCTest test) {
            this.reference = new WeakReference<MOCTest>(test);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MOCTest test = this.reference.get();
            if (msg.what == EDN_CALL) {
                test.mPhone.endCall();
            } else {
                resultCallBack(msg);
            }

        }

        @SuppressWarnings("rawtypes")
        private synchronized void resultCallBack(Message msg) {
            MOCTest test = this.reference.get();
            int N = test.mCallbacks.beginBroadcast();
            LogUtil.w(TAG, "--msg what:" + msg.what + "--N:" + N);
            try {
                for (int i = 0; i < N; i++) {
                    switch (msg.what) {
                        case EVENT_CHANGE:
                            test.mCallbacks.getBroadcastItem(i).OnEventChange(test.repeatTimes +
                                    "-" + msg.obj.toString());
                            break;
                        case CHART_CHANGE:
                            test.mCallbacks.getBroadcastItem(i).onChartDataChanged((Map) msg.obj);
                            break;
                        case DATA_CHANGE:
                            test.mCallbacks.getBroadcastItem(i).OnDataChanged((Map) msg.obj);
                            break;
                        case TEST_STOP:
                            LogUtil.w(TAG, "-----TEST_STOP----");
                            Map<String, String> resultMap = TaskTestObject.getStopResultMap(test.callModel);
                            resultMap.put(TaskTestObject.stopResultState, (String) msg.obj);
                            test.mCallbacks.getBroadcastItem(i).onCallTestStop(resultMap);
                            break;
                        case GET_NETWORK_TYPE:
                            test.csfbProceedingNetType = test.mCallbacks.getBroadcastItem(i).getNetWorkType(false);
                            break;
                    }
                }
            } catch (RemoteException e) {
                LogUtil.w(TAG, "---", e);
            }
            test.mCallbacks.finishBroadcast();
        }

    }

    /**
     * 注册广播接收器
     */
    private void regedit() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.ACTION_EVENT);
        filter.addAction(WalkMessage.ACTION_PROPERTY);
        filter.addAction(WalkMessage.ACTION_UNIT_NORMAL_RECEIVE);
        filter.addAction(WalkMessage.ACTION_UNIT_MOS_RESET_BOX);
        filter.addAction(WalkMessage.ACTION_SYNC_NET_TYPE);
        this.registerReceiver(mEventReceiver, filter);
        hasRegisteredEventBroadcast = true;
    }

    /**
     * 监听通话状态
     */
    private void listenPhoneState(Context context) {
        telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE |
                PhoneStateListener.LISTEN_SERVICE_STATE);
    }

    /**
     * 通话状态监听器
     */
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                // 不是接通 ， 而是提起手机
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    LogUtil.w(TAG, "-->OFFHOOK");
                    isIdle = false;
                    break;

                case TelephonyManager.CALL_STATE_RINGING:
                    isIdle = false;
                    LogUtil.w(TAG, "-->RINGING");
                    break;

                case TelephonyManager.CALL_STATE_IDLE: /* 空闲 */
                    isIdle = true;
                    LogUtil.w(TAG, "---Delay IDLE hasFinish:" + hasFinish + "," +
                            "hasEstablishedFromCommand:" + hasEstablishedFromCommand + "---");

                    if (hasEstablishedFromCommand && !hasFinish) {
                        new DelayToDecideIdle().start();
                    }

                    /*
                     * 如果标志位hasCallEstablished为true，即已经通话连接后未发出挂机命令，通话状态就变为空闲，
                     * 这时判断为掉话，(还有一种情况是被叫方的主动断开连接这里未判断出来，需要用手机信令判断)
                     */
                    /*
                     * if ( hasCallEstablished ){ if( isCallComplete ){
                     * //通话已经连接,并且是被叫方断开的,当成是正常通话结束挂机 hangupDial( NORMAL_CALL ); }else{
                     * //通话已经连接,并且不是被叫挂断的,当成是掉话挂机 hangupDial( DROP_CALL ); } }
                     */
                    // 已经改成全部用信令判断，遗留问题：当屏蔽门关闭后，不会吐出信令，导致无法判断掉话
                    break;

                default:
                    isIdle = false;
                    break;
            }


            isPhoneStateCallback = true;
        }

    };

    /**
     * 通话过程中未正常结束直接进入idle状态,延时2S判断掉话
     *
     * @author tangwq
     */
    class DelayToDecideIdle extends Thread {
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 2013.12.8 数据集的DropCall和CallEnd不准
            if (hasEstablishedFromCommand && !hasFinish) {
                if (!timeout) {
                    hangupDial(DROP_CALL);
                }
            }
        }
    }

    /**
     * 广播接收器:接收通信过程中的信令
     */
    private BroadcastReceiver mEventReceiver = new BroadcastReceiver() {
        private boolean isHangUpAfterAttempt;

        @Override
        public void onReceive(Context context, Intent intent) {
            long currTime = System.currentTimeMillis() * 1000;
            if (intent.getAction().equals(WalkMessage.ACTION_PROPERTY)) {
                int rcuId = intent.getIntExtra(WalkMessage.KEY_EVENT_RCUID, 0);
                if (rcuId == DataSetEvent.ET_MO_Attempt) {
                    long propertyValue = intent.getLongExtra(WalkMessage.KEY_PROPERTY, 0);
                    if (callModel.isUnitTest()) {
                        if (propertyValue < 2 && !mNetType.equals(DEFAULT_NET_TYPE) &&
                                callAttemptTime != 0) {
                            EventManager.getInstance().addTagEvent(MOCTest.this, System
                                    .currentTimeMillis(), "MOC is not VOLTE");
                            sendNormalMessage(String.valueOf(STOP));
                            hangupDial(NORMAL_CALL);
                            sendCallBackStop(TestService.RESULT_SUCCESS);
                        } else if (propertyValue < 2 && !mNetType.equals(DEFAULT_NET_TYPE) &&
                                callAttemptTime == 0) {
                            isHangUpAfterAttempt = true;
                        }
                        LogUtil.i(TAG, "MOC propertyvalue=" + propertyValue);
                    }
                }
            }
            // 接收来自解码库的信令
            if (intent.getAction().equals(WalkMessage.ACTION_EVENT)) {
                int rcuId = intent.getIntExtra(WalkMessage.KEY_EVENT_RCUID, 0);
                long time = intent.getLongExtra(WalkMessage.KEY_EVENT_TIME, System
                        .currentTimeMillis());
                String str = intent.getStringExtra(WalkMessage.KEY_EVENT_STRING);
                LogUtil.i(TAG, "--onReceiveEvent:" + str + ",rcuId:0x" + Integer.toHexString
                        (rcuId) + "-----");

                switch (rcuId) {

                    case DataSetEvent.ET_MO_CSFB_Proceeding:
                        csfbProceeding = time;
                        mHandler.obtainMessage(GET_NETWORK_TYPE).sendToTarget();
                        break;

                    // RRC Release时延：从CSFB Request到Originating CSFB RRC Release 时延
                    case DataSetEvent.ET_MO_CSFB_RRCRelease:
                        csfbRrcRelease = time;
                        break;

                    // 2014.4.20 CSFB建立时延是CSFB_Request到接通，不用ET_MT_CSFB_Success
                    // case DataSetEvent.ET_MO_CSFB_Success:
                    // csfbSuccess = time;
                    // break;

                    // 2013.10.15 CSFB事件只有以下3个引用数据集的事件,还有一个在TestService里接收，其它的组合判断
                    // 2013.10.17 香港后发现CSFB事件出来次序不对，得按时间排序，MOC这里只参与统计
                    case DataSetEvent.ET_MO_CSFB_Request:
                        csfbRequest = time;
                        // twq20150528当收到 csfb request事件时,当成call attempt处理,走该流程
                        // break;
                    case DataSetEvent.ET_MO_Attempt:// 信令:主叫起呼
                    case DataSetEvent.ET_MO_Attempt_Retry:// 信令:主叫起呼
                    case DataSetEvent.ET_ImmidiateAssignment:
                        if (str.toLowerCase(Locale.getDefault()).indexOf("volte") > 0) {
                            isVoLTE = true;
                        }

                        if (callAttemptTime == 0) {
                            callAttemptTime = System.currentTimeMillis();

                            if (isGeneralMode)
                                writeRcuEvent(RcuEventCommand.ET_IPhoneMOAttempt, currTime, 1);

                            // 显示: 主叫起呼事件
                            showEvent("Outgoing Call Attempt");
                            LogUtil.w(TAG, "---Outgoing Call Attempt");

                            // 关闭通话显示屏
                            mPhone.closeCallScreen(callModel.getConnectTime());
                        }

                        if (isHangUpAfterAttempt) {
                            EventManager.getInstance().addTagEvent(MOCTest.this, System
                                    .currentTimeMillis(), "moc is not volte");
                            sendNormalMessage(String.valueOf(STOP));
                            hangupDial(NORMAL_CALL);
                            sendCallBackStop(TestService.RESULT_SUCCESS);
                        }

                        break;

                    case DataSetEvent.ET_MO_Alerting:// 信令:响铃
                        if (isGeneralMode)
                            writeRcuEvent(RcuEventCommand.ET_IPhoneMOSetup, currTime);
                        else
                            // TODO存储事件 1.1.1.1. Alerting (0x00000061)
                            writeRcuEvent(RcuEventCommand.Alerting, currTime);
                        // 接通时延
                        alertTime = System.currentTimeMillis();
                        delay = (callAttemptTime != 0 ? alertTime - callAttemptTime : 0);

                        // 显示和统计: 振铃事件
                        if (callAttemptTime != 0) {
                            showEvent("Outgoing Call Setup:" + df.format(Float.parseFloat(Long
                                    .toString(delay)) / 1000) + "s");
                        }

                        LogUtil.w(TAG, "---Outgoing Call Setup");

                        break;

                    // 2014.5.21 不用数据集的Block_Call,直接从L3信令增加Connect事件的判断
                    // case DataSetEvent.ET_MO_Block:
                    // //2013.9.22 数据集会出来错误的BLOCK_CALL，这里要处理
                    // if( callAttemptTime!=0 && !hasEstablishedFromCommand ){
                    // LogUtil.w(TAG, "---Outgoing_Call_Failure");
                    // //未接通挂机
                    // hangupDial(BLOCK_CALL);
                    // }else{
                    // LogUtil.w(TAG,"---block call without call attempt");
                    // }
                    // break;

                    // 2014.5.21 增加从信令生成此事件(所以此事件会重复来)
                    case DataSetEvent.ET_MO_Connect:// 信令:接通呼叫
                        //三星S8定制rom在进行视频通话测试中接通呼叫后会自动跳回到视频界面，此处再做回到测试界面操作
                        //                    LogUtil.i(TAG,"接通...15s后通知停止网络监控");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.i(TAG, "通知停止监控网络");
                                mConnectFlag = true;
                            }
                        }, 3000);
                        boolean isVideo = callModel.getCallMode() == 1;
                        if (Deviceinfo.getInstance().isS8() && isVideo) {
                            mPhone.closeCallScreen(200);
                        }
                        //停止录制并删除文件(vivo Y79A需求：起呼就开始录音，超过30秒未接通，保存录音文件)
                        if (mIsRecordCall && null != mVoiceRecorder) {
                            mVoiceRecorder.stopRecord();
                            mVoiceRecorder = null;
                            FileUtil.deleteFile(recordCallFilePath);
                        }
                        if (!hasEstablishedFromCommand && callAttemptTime != 0) {
                            hasEstablishedFromCommand = true;
                        } else {
                            return;
                        }
                        addStartTime(UtilsMethod.getLocalTimeByUTCTime() * 1000);
                        connectedTime = System.currentTimeMillis();
                        callendTime = 0;

                        if (isGeneralMode)
                            writeRcuEvent(RcuEventCommand.ET_IPhoneMOEstablished, currTime);
                        else
                            // 存储事件 1.1.1.2. Connect (0x00000062)
                            writeRcuEvent(RcuEventCommand.Connect, currTime);

                        // 显示:主叫接通事件
                        if (callAttemptTime != 0) {
                            showEvent("Outgoing Call Established"
                                    + (alertTime != 0 ? "" : (":" + df.format(Float.parseFloat
                                    (Long.toString(delay)) / 1000) + "s")));
                            LogUtil.w(TAG, "---call has hasCallEstablished");
                        }

                        // 往Pioneer回传通话状态
                        sendMsgToPioneer(",DialState=1,ConnectTime=" + delay);
                        startPioneerTimer();

                        // 开始通话时长计时
                        // 到达通话时长,正常挂机
                        cleanCallTimer();
                        timer = new Timer();
                        timerTask = new TimerTask() {
                            @Override
                            public void run() {

                                if (hasEstablishedFromCommand) {
                                    // 通话时长到正常挂机
                                    LogUtil.w(TAG, "---call time's up");

                                    // 2012.10.24时间到只执行挂机动作，收到Call_Complete后会关闭此测试进程
                                    // hangup();

                                    // 2012.11.17 改回原来的hangupDial( int
                                    // reason),预防没有Call_Complete信令而不停止
                                    // hangupDial( NORMAL_CALL );

                                    writeRcuEvent(RcuEventCommand.HANGUP_DIAL, System
                                            .currentTimeMillis() * 1000, NORMAL_CALL);
                                    hasHanupEvt = true;
                                    sleepBySecond(1);
                                    // 2014.7.4 重新再改回只挂机动作，因为要统计Call_end次数
                                    hangup();

                                    timeout = true;

                                    // 等待Call_end
                                    long start = System.currentTimeMillis();
                                    while (!hasFinish) {
                                        try {
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        if (System.currentTimeMillis() - start > 6 * 1000) {
                                            hangupDial(NORMAL_CALL);
                                            break;
                                        }
                                    }
                                }
                            }
                        };
                        timer.schedule(timerTask, (mCallTime) * 1000);
                        // MOS测试
                        startMos();
                        // 接通却事前没有收到主叫起呼信令(丢信令,解码库没有给出)，现在这样处理：
                        // 挂机并停止语音测试
                        if (callAttemptTime == 0) {
                            // 停止语音测试服务
                            LogUtil.w(TAG, "---no call attempt before Connected");
                            // 起呼时间标记
                            callAttemptTime = System.currentTimeMillis();
                        }
                        break;

                    // 2014.3.20 新总说：把这个也开放出来先，预防会挂住
                    case DataSetEvent.ET_MO_End:// 信令:通话结束
                        isIdle = true;
                        callendTime = System.currentTimeMillis();
                        // 存储事件 1.1.1.11. Disconnect (0x0000006c) location -1 cause :16
                        if (isGeneralMode)
                            writeRcuEvent(RcuEventCommand.ET_IPhoneMOCallEnd, currTime, -1, 16);
                        else
                            writeRcuEvent(RcuEventCommand.Disconnect, currTime, -1, 16);

                        LogUtil.w(TAG, "Call complete!");

                        // 如果是手工挂断（没有发过挂机命令并且没有掉话，则认为是手工挂断，也就是用API拨打时在通话屏幕中按挂机 ）,
                        // 当成是正常挂机
                        LogUtil.w(TAG, "has hangup ");
                        hangupDial(NORMAL_CALL);
                        break;


                    default:
                        break;
                }
            }
            // 来自服务端的常规消息
            else if (intent.getAction().equals(WalkMessage.ACTION_UNIT_NORMAL_RECEIVE)) {
                String msg = intent.getExtras().getString(WalkMessage.KEY_UNIT_MSG);
                LogUtil.i(TAG, "receive normal msg:" + msg);

                // 同步MOS
                if (msg.equals(MSG_UNIT_MOS_START)) {

                    // 向被叫发送响应消息
                    sendNormalMessage(MSG_UNIT_MOS_START_ACK);

                    // 如果是蓝牙MOS盒测试,收到被叫准备好MOS测试消息之后,回得被叫的同步消息,同时发消息给消息盒子执行录放音动作
                    if (isMosBoxTest) {
                        sendMosBoxTest(WalkMessage.ACTION_UNIT_MOS_BOX_TEST, "MO", 1, callModel
                                        .getCallMosCountStr(),
                                callModel.getSampleRate());
                    } else {
                        if (mCaculateModeFacade != null) {
                            mCaculateModeFacade.setRcuFile(rcuFileName);
                            mCaculateModeFacade.setModel(new TaskModelWrapper(callModel));
                            mCaculateModeFacade.start();
                        }

                    }
                }

                // 来自被叫的响应
                else if (msg.equals(MSG_UNIT_MOS_STOP_ACK)) {
                    hasMosStopAck = true;
                } else if (msg.startsWith(MSG_MTC_SENDUUID2MOC)) {
                    unitTestUuidByMTC = msg.substring(MSG_MTC_SENDUUID2MOC.length());
                    hasUUIDByUnitTest = true;
                } else if (msg.equals(String.valueOf(WAIT))) {
                    LogUtil.i(TAG, "mtc need wait");
                    mNetSyncSignal = WAIT;
                    sendNormalMessage(RESQ);
                } else if (msg.equals(String.valueOf(EXEC))) {
                    LogUtil.i(TAG, "mtc can execute");
                    mNetSyncSignal = EXEC;
                    sendNormalMessage(RESQ);
                } else if (msg.equals(String.valueOf(STOP))) {
                    LogUtil.i(TAG, "mtc stop");
                } else if (msg.equals(String.valueOf(RESQ))) {
                    LogUtil.i(TAG, "收到对方响应");
                    mRecMtcResponse = true;
                }
            } else if (intent.getAction().equals(WalkMessage.ACTION_UNIT_MOS_RESET_BOX)) {
                LogUtil.w(TAG, "----MOC isMosBoxTest:" + isMosBoxTest + ",getMosTest:" +
                        callModel.getMosTest());
                // 如果当前为MOS盒测试,收到重置MOS盒消息后重
                if (isMosBoxTest && callModel.getMosTest() == TaskInitiativeCallModel.MOS_ON) {
                    // 主叫收到重置MOS盒消息后,只需要发送配置重连信息就好,具体的录放音命令在同步成功后执行
                    //2018/9/21：小背包底座已经做了缓存，在重连时无须调用BOX_INIT,调用了反而会出现重连后打分1分问题。因此注释掉下面代码。
					/*sendMosBoxTest(WalkMessage.ACTION_UNIT_MOS_BOX_INIT, "MO", 1, callModel
					.getCallMosCountStr(),
							callModel.getSampleRate());*/
                }
            }
        }

    };

    /**
     * 添加开始时间
     */
    private void addStartTime(long startTime) {
        LogUtil.w(TAG, "----MOC addStartTime:" + startTime);
        File fileP = new File(iPackTerminal.mosFileName);
        String valx = FileUtil.getStringFromFile(fileP);
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
        LogUtil.w(TAG, "----MOC addEndTime:" + endTime);
        File fileP = new File(iPackTerminal.mosFileName);
        String valx = FileUtil.getStringFromFile(fileP);
        if (null == valx || valx.trim().length() == 0) {
            FileUtil.writeToFile(fileP, "-" + endTime);
        } else {
            FileUtil.writeToFile(fileP, valx + "-" + endTime);
        }
    }

    /**
     * 清除接通计时器
     */
    private void cleanCallTimer() {
        if (timer != null) {
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.w(TAG, "onBind");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tag = "MOCTest";
        LogUtil.i(TAG, "-----onCreate-----");
        mContext = this;
        mPhone = new MyPhone(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.w(TAG, "-----onStartCommand-----");
        this.mNetType = intent.getStringExtra(WalkMessage.ACTION_SYNC_NET_TYPE);
        this.mServerTimeOffset = intent.getLongExtra(BluetoothMOSService
                .EXTRA_KEY_SERVER_TIME_OFFSET, 0);
        this.mCurrMOCDevice = intent.getParcelableExtra(BluetoothMOSService
                .EXTRA_KEY_BLUETOOTH_MOS);
        this.mCurrMTCDevice = intent.getParcelableExtra(BluetoothMTCService
                .EXTRA_KEY_BLUETOOTH_MTC);
        this.mIsRecordCall = intent.getBooleanExtra(WalkMessage.IS_RECORD_CALL, false);
        LogUtil.i(TAG, "---------------isRecordCall:" + mIsRecordCall + "---------------");
        this.mHasPolqaType = intent.getBooleanExtra(EXTRA_HAS_POLQA_TYPE, false);
        this.mServerTimeOffset = intent.getLongExtra(BluetoothMOSService
                .EXTRA_KEY_SERVER_TIME_OFFSET, 0);
        LogUtil.w(TAG, "ServerTimeOffset: " + mServerTimeOffset + "; mHasPolqaType: " + this
                .mHasPolqaType);
        int startFlag = super.onStartCommand(intent, flags, startId);
        rcuFileName = intent.getStringExtra(WalkMessage.TESTFILENAME);
        callModel = (TaskInitiativeCallModel) super.taskModel;

        int channel = ConfigRoutine.getInstance().getMosBoxChannel(getApplicationContext());
        LogUtil.i(tag, "----channel:" + channel + "----isUnitTest:" + callModel.isUnitTest());
        // 只有MOS盒通道号大于0且当前为主被叫测试,才认为当前是MOS盒测试
        isMosBoxTest = (channel > 0) && callModel.isUnitTest();
        LogUtil.w(TAG, "---isMosBoxTest:" + isMosBoxTest + "--mosTest:" + callModel.getMosTest()
                + "--repeatTimes:" + repeatTimes);
        if (repeatTimes == 1 && isMosBoxTest && callModel.getMosTest() == TaskInitiativeCallModel
                .MOS_ON) {
            sendMosBoxTest(WalkMessage.ACTION_UNIT_MOS_BOX_INIT, "MO", 1, callModel
                            .getCallMosCountStr(),
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

        if (callModel.getMosTest() == TaskInitiativeCallModel.MOS_ON) {
            mCaculateModeFacade = new CaculateModeFacade(this);
            mCaculateModeFacade.setDevice(mCurrMOCDevice);
        }

        listenPhoneState(this);

        // 运行测试线程
        if (this.mStartToTestThread == null) {
            mCallTime = callModel.getCallLength() == 1 ? 99999 : callModel.getKeepTime();
            this.mStartToTestThread = new StartToTestThread(callModel.getCallNumber(), callModel
                    .getConnectTime());
            this.mStartToTestThread.start();
        } else {
            LogUtil.w(TAG, "mStartToTestThread is not null.");
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

        LogUtil.w(TAG, "-----onDestroy----");

        //当机型为vivo并且做polqa算分时(现在加权限控制，不判断vivo机型了，根据权限)，在测试结束时删除复制过来的polqa文件
        if (mHasPolqaType && callModel.getCallMOSCount() == TaskModel.MOS_POLQA) {
            if (!originalSerialNo.isEmpty()) {
                setCustomSerialNo(originalSerialNo);
            }
            FileUtil.deleteFile(AppFilePathUtil.getInstance().getAppFilesFile("PolqaLicenseFile" +
                    ".txt").getAbsolutePath());
        }

        hasOnDestroy = true;

        if (telManager != null) {
            telManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (this.mStartToTestThread != null) {
            this.mStartToTestThread.stopThread();
            this.mStartToTestThread = null;
        }
        this.cleanCallTimer();

        mHandler.removeMessages(0);

        try {
            if (this.hasRegisteredEventBroadcast) {
                this.unregisterReceiver(mEventReceiver);
                this.hasRegisteredEventBroadcast = false;
            }
        } catch (Exception e) {
            LogUtil.i(TAG, e.toString());
        }

        mCallbacks.kill();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    /**
     * 线程：进行通话测试
     */
    private class StartToTestThread extends Thread {
        private String mNumber;// 拨打号码
        private int mAttemptTime = 15 * 1000;// startDial到收到主叫起呼信令之间的时间(秒)
        private int mConnectTime;// 接通时间，收到主叫起呼信令到接通之间的时间
        private boolean isStop = false;

        /**
         * @param number      拨打的号码
         * @param connectTime 接通时间
         */
        StartToTestThread(String number, int connectTime) {
            this.mNumber = number;
            this.mConnectTime = connectTime * 1000;
            LogUtil.w(TAG, "this.mConnectTime=" + this.mConnectTime);
        }

        public void stopThread() {
            this.isStop = true;
            this.interrupt();
        }

        @Override
        public void run() {
            // 只有业务与测试服务绑定成功后，再继续往下做业务
            LogUtil.w(TAG, "----waiting for callback registed----start----");
            int sleepTime = 200;
            while (!isStop && (!isCallbackRegister && !isPhoneStateCallback)) {
                try {
                    Thread.sleep(sleepTime);
                    LogUtil.w(TAG, "----waiting for callback registeing------");
                } catch (InterruptedException e) {
                    break;
                }
            }
            LogUtil.w(TAG, "----waiting for callback registed----end----");
            LogUtil.w(TAG, "isStop:"+isStop+",isIdle:"+isIdle+",hasRegisteredEventBroadcast:"+hasRegisteredEventBroadcast);
            if (isStop)
                return;
            // 先判断当前是否不在Idle状态则先挂机
            if (!isIdle) {
                LogUtil.w(TAG, "----current state is not idle, hangup----");
                hangup();
                sleepBySecond(1);
            }
            // 确定挂机后再注册
            if (!hasRegisteredEventBroadcast)
                regedit();

            if (callModel.isUnitTest()) {
                //czc:通话前检测网络类型是否与ipack下发网络类型一致——>>针对电信多网测试
                waitIfNetUnavailable();
            }

            sleepBySecond(1);
            // 发送消息通知TRACE事件处理类呼叫开始，重置呼叫事件信息
            sendBroadcast(new Intent(WalkMessage.ACTION_CALL_BEFER_START));


            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!isTestInterrupt) {
                // 显示: MOC Start事件
                showEvent("MOC Start");
                startDial();
            }
        }

        private void waitIfNetUnavailable() {
            boolean isMonitorNetType = !mNetType.equals(DEFAULT_NET_TYPE);
            if (isMonitorNetType) {
                String currentNetType = MyPhoneState.getInstance().parseNetType(MOCTest.this,
                        mNetType);
                EventManager.getInstance().addTagEvent(MOCTest.this, System.currentTimeMillis(),
                        "current net type:" + currentNetType);

                int time = 0;
                while (!mNetType.equals(currentNetType)) {
                    try {
                        sendNormalMessage(String.valueOf(WAIT));
                        currentNetType = MyPhoneState.getInstance().parseNetType(MOCTest.this,
                                mNetType);
                        if (time % 60 == 0) {
                            EventManager.getInstance().addTagEvent(MOCTest.this, System
                                    .currentTimeMillis(), "current net type is " + currentNetType + ",it " +
                                    "does't match " + mNetType);
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
                } while (!mRecMtcResponse);

                time = 0;
                while (mNetSyncSignal.equals(WAIT)) {
                    try {
                        if (time % 60 == 0) {
                            EventManager.getInstance().addTagEvent(MOCTest.this, System
                                    .currentTimeMillis(), "mtc net type does't match,waiting···");
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
                                String currentNetType = MyPhoneState.getInstance().parseNetType
                                        (MOCTest.this, mNetType);
                                if (!mNetType.equals(currentNetType) && !mConnectFlag) {
                                    String str = String.format("moc current net type %s does't " +
                                            "match %s", currentNetType, mNetType);
                                    LogUtil.i(TAG, str);
                                    EventManager.getInstance().addTagEvent(MOCTest.this, System
                                            .currentTimeMillis(), str);
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
         * start Dial 拨打
         */
        private void startDial() {

            // 存储：MOC Start事件
            if (callModel.isUnitTest()) {
                waitForUUIDByMtc();
            } else {
                unitTestUuidByMTC = genGUIDDefault();
            }
            LogUtil.w(TAG, "--uuid:" + unitTestUuidByMTC + "--phone:" + getSimNumber());

            long time = System.currentTimeMillis() * 1000;
            LogUtil.w(TAG, "---write event to RCU file:RcuEventCommand.MOC_START time:" + time);


            EventBytes.Builder(mContext, RcuEventCommand.MOC_START).addInteger(callModel
                    .getKeepTime())
                    .addTguid(unitTestUuidByMTC).addStringBuffer(callModel.getCallNumber())
                    .addStringBuffer(getSimNumber())
                    .writeToRcu(time);

            sleepBySecond(1);

            // 复位主叫起呼信令标志为false
            callAttemptTime = 0;
            hasEstablishedFromCommand = false;

            // 拨打动作
            call();

            // 等待起呼信令
            waitForCallAttemptCode();
        }

        /**
         * 发送拨打命令
         */
        private void call() {
            if (!isTestInterrupt) {
                if (callModel.getCallMode() == 0) {
                    mPhone.call(mNumber);
                    //起呼开始录制(vivo Y79A需求：起呼就开始录音，超过30秒未接通，保存录音文件)
                    if (mIsRecordCall) {
                        recordCall();
                    }
                } else {
                    mPhone.makeVideoCall(mContext, mNumber);
                }
            }
        }

        /**
         * 起呼开始录制(vivo Y79A需求：起呼就开始录音，超过30秒未接通，保存录音文件)
         */
        private void recordCall() {
            LogUtil.i(TAG, "-----recordCall--------");
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Service
                    .AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            // 关闭系统音和通知音
            audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
            audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            mVoiceRecorder = new VoiceRecorder();
            String filePath = AppFilePathUtil.getInstance().createSDCardBaseDirectory
                    ("call_voice", UtilsMethod.getSimpleDateFormat6(System.currentTimeMillis()),
                            (rcuFileName.equals("") ? "" : rcuFileName));
            String fileName = "voice_" + DateUtil.formatDate(DateUtil.FORMAT_DATE_TIME2, new Date
                    (System.currentTimeMillis())) + ".wav";
            recordCallFilePath = filePath + fileName;
            int audioSource = Deviceinfo.getInstance().isVivo() ? MediaRecorder.AudioSource
                    .VOICE_CALL : MediaRecorder.AudioSource.DEFAULT;
            LogUtil.i(TAG, "---------audioSource:" + audioSource + "---------");
            mVoiceRecorder.init(new File(recordCallFilePath), RVoixSrv.MODE_RECORD_WAV, 48000,
                    audioSource, 1,
                    16, true);
            mVoiceRecorder.startRecord();
        }

        /**
         * 主被叫联合时等待获得被叫联合的UUID
         */
        private void waitForUUIDByMtc() {

            hasUUIDByUnitTest = false;

            long sendTime = System.currentTimeMillis();
            sendNormalMessage(MSG_MOC_GETUUIDFROMMTC);

            int i = 0;
            while (!isStop) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (i++ % 5 == 0) {
                    LogUtil.w(TAG, "---wait for get uuid from mtc--");
                }

                if (!hasUUIDByUnitTest && System.currentTimeMillis() - sendTime > 10 * 1000) {
                    sendNormalMessage(MSG_MOC_GETUUIDFROMMTC);
                    sendTime = System.currentTimeMillis();
                }

                if (hasFinish || hasUUIDByUnitTest) {
                    break;
                }
            }
        }

        /**
         * 等待主叫起呼信令 插入Start Dial事件之后,等待主叫起呼信令或者接通指示（解码库给出） 如在15s内未收到主叫起呼信令，
         * 则认为该次呼叫尝试不成功，不计入通话次数， 重新发送拨打命令并插入Start Dial事件。
         */
        private void waitForCallAttemptCode() {
            int useTime = 0;
            int sleepTime = 100;
            LogUtil.w(TAG, "----waitForCallAttemptCode----start----");
            while (callAttemptTime == 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    break;
                }
                useTime += sleepTime;
                if (useTime >= mAttemptTime) {
                    break;
                }
            }
            LogUtil.w(TAG, "----waitForCallAttemptCode----end----");
            // 如果已经收到起呼信令
            if (callAttemptTime != 0) {
                // 收到Call Attempt才算是一次呼叫
                waitForCallEstablished();
            } else {
                LogUtil.w(TAG, "---call attempt lost,hasEstablishedFromCommand:" +
                        hasEstablishedFromCommand + "---");
                // 当通话已经建立但是没有收到Call_Attempt(不排除解码库丢信令的可能)
                if (hasEstablishedFromCommand) {
                    // 关闭通话显示屏
                    mPhone.closeCallScreen(callModel.getConnectTime());
                } else {
                    // 2013.3.29 S3手机脱网后，未及时获取到脱网消息，再拨打就会无Call_Attempt,来到这里。

                    /* 存储事件：Hungup Dial事件 */
                    writeRcuEvent(RcuEventCommand.HANGUP_DIAL, System.currentTimeMillis() * 1000,
                            NORMAL_CALL);

                    // 挂机动作
                    hangup();

                    sleepBySecond(15);

                    // 开始拨打
                    startDial();
                }
            }

        }

        /**
         * 等待接通 出现主叫起呼信令开始计时，到Connection时长之后， 主叫手机仍未收到接通信令或接通指示（解码库给出），
         * 则认为该次主叫起呼未接通，发挂机指令，插入Hungup Dial事件，
         */
        private void waitForCallEstablished() {
            int useTime = 0;// 接通时长
            int sleepTime = 100;
            LogUtil.w(TAG, "----waiting for CallEstablished----start----");
            while (!isStop && !hasEstablishedFromCommand) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    break;
                }
                useTime += sleepTime;
                if (useTime > mConnectTime) {
                    break;
                }
            }
            LogUtil.w(TAG, "----waiting for CallEstablished----end----hasEstablishedFromCommand:"
                    + hasEstablishedFromCommand);
            // 如果未收到接通信令
            if (!hasEstablishedFromCommand) {
                if (!mNetType.equals(DEFAULT_NET_TYPE)) {
                    // 电信主被叫同步，设为 Normal 挂机
                    hangupDial(NORMAL_CALL);
                } else {
                    // 未接通挂机
                    hangupDial(BLOCK_CALL);
                }
            }

        }

    }

    /**
     * 休眠
     */
    private void sleepBySecond(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 此处的MOS录音仅处理单机情况, 如果是主被叫联合,不做录音动作 主被叫的录音动作在收到被叫的录音开始消息之后进行
     */
    private void startMos() {
        // 如果是MOS测试
        if (callModel.getMosTest() == TaskInitiativeCallModel.MOS_ON) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                if (callModel.isUnitTest()) {
                    // 如果是主被叫联合测试时, do nothing
                } else {
                    if (callModel.getCallMOSTestType() == TaskInitiativeCallModel.MOS_M2M) {
                        // 单机的普通MOS
                        if (mCaculateModeFacade != null) {
                            mCaculateModeFacade.setRcuFile(rcuFileName);
                            mCaculateModeFacade.setModel(new TaskModelWrapper(callModel));
                            mCaculateModeFacade.start();
                        }

                    } else if (callModel.getCallMOSTestType() == TaskInitiativeCallModel.MOS_M2L
                            && MyPhoneState.hasSync(mContext)) {
                        // do nothing
                    }
                }
            } else {
                // 通知栏目提示用户SD卡不可用
                showNotification(getString(R.string.test_mos_sdcard_unmount), WalkMessage.ACTION_TEST_MOS_SDCARD_UNMOUNTED);
            }
        }
    }

    /**
     * 发送挂机命令
     */
    private synchronized void hangup() {
        // 2014.6.16 部分手机在idle状态挂机会关屏
        LogUtil.w(TAG, "hangup(),isIdle=" + isIdle);
        if (!isIdle) {
            mPhone.endCall();
        }
    }

    /**
     * hangup dial 挂断 挂机分加３种:未接通挂机，正常挂机，异常挂机 此方法是未接通挂机、正常挂机时调用,挂机时需要存储hangup
     * dial事件 请参考和语音业务相关的文档<>
     *
     * @param flag 挂机原因: 0,正常挂机 1,接入失败挂机 2,掉话挂机 3,手工停止
     */
    private synchronized void hangupDial(int flag) {
        //当flag为接入失败挂机时，保存起呼录制通话的录音文件(vivo Y79A需求：起呼就开始录音，超过30秒未接通，保存录音文件)
        if (mIsRecordCall && mVoiceRecorder != null) {
            mVoiceRecorder.stopRecord();
            mVoiceRecorder = null;
            if (flag != BLOCK_CALL) {
                FileUtil.deleteFile(recordCallFilePath);
            }
        }
        LogUtil.d(TAG, "----hangupDial flag:" + flag + ",hasFinish:" + hasFinish + "----");
        // 防止重复调用
        if (hasFinish) {
            return;
        }
        addEndTime(UtilsMethod.getLocalTimeByUTCTime() * 1000);
        hasFinish = true;

        if (isMosBoxTest && callModel.getMosTest() == TaskInitiativeCallModel.MOS_ON) {
            sendMosBoxTest(WalkMessage.ACTION_UNIT_MOS_BOX_TEST, "MO", 0, callModel.getCallMosCountStr(),
                    callModel.getSampleRate());
        }

        /* 存储事件：Hungup Dial事件 */
        if (!hasHanupEvt) {
            hasHanupEvt = true;
            LogUtil.e(TAG, "---write event to RCU file:RcuEventCommand.HANGUP_DIAL:" + flag);
            writeRcuEvent(RcuEventCommand.HANGUP_DIAL, System.currentTimeMillis() * 1000, flag);
        }

        /* 发送广播通知PhoneService挂机 */
        hangup();

        // 往Pioneer回传通话状态
        stopPioneerTimer();
        sendMsgToPioneer(",DialState=0");

        /* 显示事件 */
        switch (flag) {
            case NORMAL_CALL:
                // 显示:正常结束
                LogUtil.w(TAG, "---call end,hang up now!");
                if (!isTestInterrupt) {
                    // 显示Hangup事件
                    showEvent("Hangup Dial");

                    if (callAttemptTime != 0) {
                        showEvent("Outgoing Call End");
                    }
                }
                break;

            case BLOCK_CALL:
                // 如果是主被叫联合测试发送消息给被叫
                if (callModel.isUnitTest()) {
                    sendNormalMessage(MSG_UNIT_CALL_BLOCK);
                }

                // Message msg;
                if (callAttemptTime != 0) {
                    // 显示:主叫未接通
                    LogUtil.w(TAG, "---block call,hang up now!");
                    showEvent("Outgoing Blocked Call");
                }

                // 显示Hangup事件
                showEvent("Hangup Dial");

                break;

            // 掉话
            case DROP_CALL:
            case RcuEventCommand.DROP_OUT_OF_SERVICE:

                if (hasEstablishedFromCommand) {
                    /*
                     * 2012.03.19 如果是主被叫联合测试发送消息给被叫针对这样的异常，主被叫都进入屏蔽房后，主叫收到Drop_Call信令，但被叫一直无
                     * Call_Complete或者Drop_Call信令(屏蔽后无信令)，这时被叫收到主叫的MSG_UNIT_CALL_FINISH消息
                     * 就直接退出进程了，导致 被叫没有插入HANGUP_DIAL事件
                     */
                    if (callModel.isUnitTest() && callAttemptTime != 0) {
                        sendNormalMessage(MSG_UNIT_CALL_DROP);
                    }
                    // 显示:主叫结束(呼叫掉话)
                    if (callAttemptTime != 0) {
                        // 2013.8.30 Drop_Call可能发生在挂机之后，所以在Testservice里写
                        // 数据集后单独处理语音事件的显示
                        showEvent("Outgoing Drop Call");
                        hasDrop = true;
                    }
                }
                // 显示Hangup事件
                showEvent("Hangup Dial");
                break;

            case INTERRUPT:

                // 存储事件：Hungup Dial事件
                // writeRcuEvent( RcuEventCommand.HANGUP_DIAL ,INTERRUPT );

                // 显示Hangup事件
                showEvent("Hungup Dial");

                if (hasEstablishedFromCommand) {
                    showEvent("Outgoing Call End");
                }

                break;
        }

        if (mCaculateModeFacade != null) {
            mCaculateModeFacade.stop();
        }

        /* 修改状态位 */
        hasEstablishedFromCommand = false;


        // 发送统计数据
        sendChartData();

        /* 计入一次通话 */
        sendCallBackStop(TestService.RESULT_SUCCESS);

    }

    protected synchronized void showEvent(String event) {
        Message msg = mHandler.obtainMessage(EVENT_CHANGE, event);
        msg.sendToTarget();
    }

    /**
     * 增加统计数据
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
            addChartData(TotalDial._csfb_mo_request, 1);
            addChartData(TotalDial._moTrys, 1);
            if (csfbRrcRelease > 0) {
                int rrcReleaseDelay = (int) (csfbRrcRelease - csfbRequest);
                addChartData(TotalDial._csfb_mo_RrcRelease, 1);
                addChartData(TotalDial._csfb_mo_RrcReleaseDelay, rrcReleaseDelay);
                // 回落时延
                if (csfbProceeding > 0) {
                    int delayProceeding = (int) (csfbProceeding - csfbRrcRelease);
                    switch (csfbProceedingNetType) {
                        case UnifyParaID.NET_GSM:
                        case UnifyParaID.NET_CDMA_EVDO:
                            addChartData(TotalDial._csfb_mo_proceeding_2G, 1);
                            addChartData(TotalDial._csfb_mo_proceedingDelay_2G, delayProceeding);
                            break;
                        case UnifyParaID.NET_TDSCDMA:
                        case UnifyParaID.NET_WCDMA:
                            addChartData(TotalDial._csfb_mo_proceeding_3G, 1);
                            addChartData(TotalDial._csfb_mo_proceedingDelay_3G, delayProceeding);
                            break;
                    }

                    if (callendTime > 0) {
                        addChartData(TotalDial._csfb_mo_CallEnd, 1);
                    }
                }
            }

            if (alertTime > 0) {
                addChartData(TotalDial._csfb_mo_alerting, 1);

                // twq20160421 只有alertTime存在的时候,才根据是否有接通去计时延及接通次数,否则抛掉当前时延
                if (connectedTime > 0) {
                    int delaySuccess = (int) (alertTime - csfbRequest);
                    addChartData(TotalDial._csfb_mo_SuccessDelay, delaySuccess);
                    addChartData(TotalDial._csfb_mo_Established, 1);
                    addChartData(TotalDial._moConnects, 1);
                    addChartData(TotalDial._moCalldelay, delaySuccess);
                    addChartData(TotalDial._moDelaytimes, 1);
                }
            }

            if (hasDrop) {
                addChartData(TotalDial._csfb_mo_drop, 1);
                addChartData(TotalDial._moDropcalls, 1);
            }
        }

        // 普通2G/3G统计
        else if (callAttemptTime > 0) {

            boolean isVideo = callModel.getCallMode() == 1;

            // 统计:拨打次数
            if (isVideo) {
                addChartData(TotalDial._video_moTrys, 1);
            } else {
                addChartData(TotalDial._moTrys, 1);
                if (isVoLTE) {
                    addChartData(TotalDial._volte_moTrys, 1);
                }
            }

            if (connectedTime > 0 || alertTime > 0) {
                if (isVideo) {
                    addChartData(TotalDial._video_moConnects, 1);
                } else {
                    addChartData(TotalDial._moConnects, 1);
                    if (isVoLTE) {
                        addChartData(TotalDial._volte_moConnects, 1);
                    }
                }
                int delayConnect = (int) ((alertTime > 0 ? alertTime : connectedTime) - callAttemptTime);
                if (isVideo) {
                    addChartData(TotalDial._video_moCalldelay, delayConnect);
                    addChartData(TotalDial._video_moDelaytimes, 1);
                } else {
                    addChartData(TotalDial._moCalldelay, delayConnect);
                    addChartData(TotalDial._moDelaytimes, 1);
                    if (isVoLTE) {
                        addChartData(TotalDial._volte_moCalldelay, delayConnect);
                        addChartData(TotalDial._volte_moDelaytimes, 1);
                    }
                }
            }

            if (hasDrop) {
                if (isVideo) {
                    addChartData(TotalDial._video_moDropcalls, 1);
                } else {
                    addChartData(TotalDial._moDropcalls, 1);
                    if (isVoLTE) {
                        addChartData(TotalDial._volte_moDropcalls, 1);
                    }
                }
            }
        }

        if (!map.isEmpty()) {
            Message msg = mHandler.obtainMessage(CHART_CHANGE, map);
            msg.sendToTarget();
        }
    }

    /**
     * 添加一次MOS分值的统计
     */
    private void sendChartMos(double pesqScore, double pesqMos) {
        // 2013.10.23这里暂时不考虑总值超出int范围
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put(TotalDial._pesq_score.name(), (int) pesqScore);
        map.put(TotalDial._pesq_scoreCount.name(), 1);
        map.put(TotalDial._pesq_mos.name(), (int) pesqMos);
        map.put(TotalDial._pesq_mosCount.name(), 1);
        Message msg = mHandler.obtainMessage(CHART_CHANGE, map);
        msg.sendToTarget();
    }

    /**
     * [调用停止当前业务接口]<BR>
     * [如果当前为手工停止状态不能调用当前停止接口]
     *
     * @param msg
     */
    private synchronized void sendCallBackStop(String msg) {
        if (!isInterrupted) {
            Message StopMsg = mHandler.obtainMessage(TEST_STOP, msg);
            StopMsg.sendToTarget();
        }
    }

}