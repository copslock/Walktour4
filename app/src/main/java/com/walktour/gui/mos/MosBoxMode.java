package com.walktour.gui.mos;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.bluetooth.IBluetoothCommunication;
import com.walktour.gui.setting.bluetoothmos.BluetoothPipeLineService;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.service.bluetoothmos.BluetoothMOSService;
import com.walktour.service.bluetoothmos.IBluetoothMOSServiceBinder;
import com.walktour.service.bluetoothmos.command.BaseCommand;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 通过 Mos 盒录放音，得出算分文件并算分
 * <p>
 * 可以编排多个语料一起进行顺序播放，并调整语料顺序、间隔、循环次数；
 *
 * @author zhicheng.chen
 * @date 2019/4/1
 */
public class MosBoxMode implements Handler.Callback, ICaculateMode {
    private static final String TAG = "czc";

    // Handler消息标识
    private static final int FLAG_PLAY_VOICE = 0x0010;
    private static final int FLAG_RECORD_VOICE = 0x0011;
    private static final int FLAG_SEND_DATA = 0x0012;
    private static final int FLAG_SYNC_ALIGN = 0x0013;

    // 蓝牙通信命令
    private static final String BEGIN_COMMAND = "begin";
    private static final String STOP_COMMAND = "stop";
    private static final String READY_COMMAND = "ready";
    private static final String PLAY_COMMAND = "play";
    private static final String RECORD_COMMAND = "record";
    private static final String ALIGN_COMMAND = "align";

    // 流程标志
    private boolean mIsPhoneConnected;
    private boolean mIsMosConnected;
    private boolean mIsStart;
    private boolean mIsReady;
    private boolean mIsStop;
    //是否对齐操作（因为蓝牙mos头存在时延，导致录放音不是同时进行）
    private boolean mNeedSyncAlign;

    /**
     * 低分标准
     */
    private static final double VOICE_LOW_STAND = 3.5;

    /**
     * 10s的循环时间
     */
    private static final int TIME_INTERVAL = 10 * 1000;

    /**
     * 算分类型：MOS_PESQ、MOS_POLQA
     */
    private int mCaculateType;

    private ExecutorService mExecutor = Executors.newCachedThreadPool();
    private List<String> mCommandList = Collections.synchronizedList(new ArrayList<String>());

    private Context mContext;


    private long mMosConnectTime;
    private String mRcuFileName;
    private TaskModelWrapper mTaskModelWrapper;

    private Timer mTimer;
    private final Object mLock = new Object();
    private int mDuration;
    private Handler mHandler;
    private BaseCommand.FileType mFileType;
    private IBluetoothCommunication mPhoneBinder;
    private IBluetoothMOSServiceBinder mMosBinder;
    private BluetoothMOSDevice mDevice;

    /**
     * 定时器循环执行的次数
     */
    private int mCurrentTimerIndex;

    //循环次数
    private int mCycleTimes = 2;
    //循环间隔
    private long mCycleInterval;
    //循环语料
    private List<BaseCommand.FileType> mCycleDatas = new ArrayList<>();

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_BLUE_TOOTH".equals(intent.getAction())) {
                String command = intent.getStringExtra("EXTRA_BLUE_TOOTH_READ");
                if (READY_COMMAND.equals(command)) {
                    synchronized (mLock) {
                        mIsReady = true;
                        mLock.notifyAll();
                    }
                } else if (BEGIN_COMMAND.equals(command)) {
                    scheduleTask();
                } else if (ALIGN_COMMAND.equals(command)) {
                    if (!mNeedSyncAlign) {
                        if (!mCommandList.contains(ALIGN_COMMAND)) {
                            mCommandList.add(0, ALIGN_COMMAND);
                        }
                    }
                } else if (STOP_COMMAND.equals(command)) {
                    stop();
                }
            }
        }
    };

    private ServiceConnection mPhoneConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPhoneBinder = IBluetoothCommunication.Stub.asInterface(service);
            mIsPhoneConnected = true;
            Log.i(TAG, "Phone onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPhoneBinder = null;
            mIsPhoneConnected = false;
            Log.i(TAG, "Phone onServiceDisconnected");
            stop();
        }
    };

    private ServiceConnection mMOSConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMosConnectTime = System.currentTimeMillis();
            mMosBinder = IBluetoothMOSServiceBinder.Stub.asInterface(service);
            mIsMosConnected = true;
            Log.i(TAG, "Mos onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsMosConnected = false;
            Log.i(TAG, "Mos onServiceDisconnected");
            stop();
        }
    };

    public MosBoxMode(Context context) {
        mContext = context;

        HandlerThread handlerThread = new HandlerThread("bluetooth_mos_thread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper(), this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_BLUE_TOOTH");
        mContext.registerReceiver(mReceiver, filter);


        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                final long day = 24 * 60 * 60 * 1000;
                String voiceDir = AppFilePathUtil.getInstance().getSDCardBaseDirectory("voice");
                UtilsMethod.removeFiles(voiceDir, 7 * day);
                String lowDir = AppFilePathUtil.getInstance().getSDCardBaseDirectory("Voice-Low");
                UtilsMethod.removeFiles(lowDir, 10 * day);
            }
        });

    }

    public void setRcuFile(String rcuFileName) {
        mRcuFileName = rcuFileName;
    }

    public void setModel(TaskModelWrapper wrapper) {
        this.mTaskModelWrapper = wrapper;
        mCaculateType = wrapper.callMosCount;
        mDuration = wrapper.keepTime;
        mCycleInterval = wrapper.cycleInterval;
        mCycleTimes = wrapper.cycleTimes;
        if (wrapper.cycleDatas != null) {
            mCycleDatas.addAll(wrapper.cycleDatas);
            if (!mCycleDatas.isEmpty()) {
                mFileType = mCycleDatas.get(0);
            }
        }
    }


    public void start() {
        //        if (mDevice != null) {
        //            Log.e(TAG, "Bluetooth Mos Device is Null");
        //            return;
        //        }
        if (mCycleDatas.isEmpty()) {
            Log.e(TAG, "CycleDatas isEmpty!!!");
            return;
        }
        boolean isMocTest = mTaskModelWrapper.isMocTest;
        boolean isAlternateTest = mTaskModelWrapper.isAlternaterTest;
        if (isAlternateTest) {
            executeTwoWay(isMocTest);
        } else {
            executeOneWay(isMocTest);
        }
    }


    /**
     * 执行单向命令：主叫录音，被叫放音
     *
     * @param isMocTest 是否主叫
     */
    private void executeOneWay(final boolean isMocTest) {

        MosCaculator.setVoice(mContext);


        int testTime = 10;
        int times = mDuration % testTime == 0 ? mDuration / testTime : mDuration / testTime + 1;
        for (int i = 0; i < times; i++) {
            if (isMocTest) {
                mCommandList.add(RECORD_COMMAND);
            } else {
                mCommandList.add(PLAY_COMMAND);
            }
        }

        submitTask(isMocTest);
    }

    /**
     * 执行双向交替命令：主被叫交替执行录放音命令
     *
     * @param isMocTest 是否主叫
     */
    private void executeTwoWay(final boolean isMocTest) {

        MosCaculator.setVoice(mContext);

        //init command
        int testTime = 10;
        int times = mDuration % testTime == 0 ? mDuration / testTime : mDuration / testTime + 1;
        for (int i = 0; i < times; i++) {
            if (isMocTest) {
                mCommandList.add(i % 2 == 0 ? RECORD_COMMAND : PLAY_COMMAND);
            } else {
                mCommandList.add(i % 2 == 0 ? PLAY_COMMAND : RECORD_COMMAND);
            }
        }

        submitTask(isMocTest);

    }

    private void submitTask(final boolean isMocTest) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mIsStart = true;
                makeSureServiceConnected();
                if (isMocTest) {
                    makeSureTimeAble();
                    mHandler.obtainMessage(FLAG_SEND_DATA, BEGIN_COMMAND).sendToTarget();
                    scheduleTask();
                }
            }
        });
    }

    private void makeSureServiceConnected() {
        while (!mIsMosConnected || !mIsPhoneConnected) {
            if (isStop()) {
                return;
            }
            if (!mIsMosConnected) {
                //EventBus.getDefault().post("mos service is unconnected...");
                Log.i(TAG, "mos service is unconnected, waiting...");
            }
            if (!mIsPhoneConnected) {
                //EventBus.getDefault().post("bluetooth service is unconnected...");
                Log.i(TAG, "bluetooth service is unconnected, waiting...");
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "Service is ready...");
        //EventBus.getDefault().post("Service is ready...");
        //告诉对方，我已经准备好了，可以执行下一步操作
        mHandler.obtainMessage(FLAG_SEND_DATA, READY_COMMAND).sendToTarget();
        synchronized (mLock) {
            while (!mIsReady) {
                Log.i(TAG, "Opposite side is not ready,waiting...");
                try {
                    mLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.i(TAG, "Opposite side is ready...");
        //EventBus.getDefault().post("Opposite side is ready...");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void makeSureTimeAble() {
        //保证连接与执行任务前有2s的间隔
        long interval = System.currentTimeMillis() - mMosConnectTime;
        try {
            int time = 2000;
            long tSleep = time - interval > 0 ? time - interval : 0;
            if (tSleep != 0) {
                Thread.sleep(tSleep);
                Log.w(TAG, "sleep time:" + tSleep);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void scheduleTask() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!mCommandList.isEmpty()) {
                        String command = mCommandList.remove(0);
                        monitorCycleTimes(command);
                        if (PLAY_COMMAND.equals(command)) {
                            mHandler.sendEmptyMessage(FLAG_PLAY_VOICE);
                        } else if (RECORD_COMMAND.equals(command)) {
                            mHandler.sendEmptyMessage(FLAG_RECORD_VOICE);
                        } else if (ALIGN_COMMAND.equals(command)) {
                            mHandler.sendEmptyMessage(FLAG_SYNC_ALIGN);
                        }
                    } else {
                        Log.w(TAG, "command list is empty,timer doen‘t work");
                    }
                }
            }, 0, TIME_INTERVAL + mCycleInterval * 1000);
        }
    }

    /**
     * 监视循环次数，到达指定循环次数之后，执行停止操作
     *
     * @param command
     */
    private void monitorCycleTimes(String command) {
        if (!ALIGN_COMMAND.equals(command) && mCycleDatas.size() > 0) {

            /*
             *两种情况：
             * 1、单向操作：
             * 主叫录音，被叫放音，仅需操作一次
             * 2、双向操作：
             * 每个语料播放和录音操作，才算完成一次小循环
             *
             * 多个语料都执行完，才是完成一次大循环
             */
            final int t = mTaskModelWrapper.isAlternaterTest ? 2 : 1;

            mFileType = mCycleDatas.get(mCurrentTimerIndex / t % mCycleDatas.size());
            mCurrentTimerIndex++;
            if (mCurrentTimerIndex % (mCycleDatas.size() * t) == 0) {

                mCycleTimes--;
                if (mCycleTimes <= 0) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //这里要延时10s间隔停止，录放音执行需要10s时间
                            stop();
                        }
                    }, TIME_INTERVAL);
                }
            }
        }
    }

    public void setMosDevice(BluetoothMOSDevice device) {
        bindPhone();
        bindMos(device);
    }

    private void unBind() {
        if (mIsPhoneConnected) {
            unBindPhone();
        }
        if (mIsMosConnected) {
            unBindMos();
        }
    }

    public boolean isStop() {
        return mIsStop;
    }

    public synchronized void stop() {
        if (!mIsStop) {
            Log.i(TAG, "stop");
            mIsStop = true;
            //EventBus.getDefault().post("stop");
            if (mIsStart) {
                mHandler.obtainMessage(FLAG_SEND_DATA, STOP_COMMAND).sendToTarget();
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }
            }
            mContext.unregisterReceiver(mReceiver);
            unBind();
        }
    }


    /**
     * 绑定手机
     */
    private void bindPhone() {
        Intent intent = new Intent(mContext, BluetoothPipeLineService.class);
        mContext.bindService(intent, mPhoneConn, Context.BIND_AUTO_CREATE);
    }


    /**
     * 绑定mos头
     */
    private void bindMos(BluetoothMOSDevice device) {
        mDevice = device;
        if (device != null) {
            Intent intent = new Intent(mContext, BluetoothMOSService.class);
            intent.putExtra(BluetoothMOSService.EXTRA_KEY_BLUETOOTH_MOS, device);
            intent.setExtrasClassLoader(BluetoothMOSDevice.class.getClassLoader());
            mContext.bindService(intent, mMOSConn, Context.BIND_AUTO_CREATE);
        }
    }

    private void unBindPhone() {
        mContext.unbindService(mPhoneConn);
    }

    private void unBindMos() {
        mContext.unbindService(mMOSConn);
    }


    @Override
    public boolean handleMessage(Message msg) {
        try {
            switch (msg.what) {
                case FLAG_PLAY_VOICE:
                    if (mMosBinder != null) {
                        Log.i(TAG, "play");
                        MosCaculator.setMicrophoneMute(false);

                        boolean ret = mMosBinder.runPlayback(mFileType.getName());
                        if (ret) {
                            //EventBus.getDefault().post("play:" + mFileType.getName());
                        } else {
                            Log.i(TAG, "play failed");
                            stop();
                        }
                    }
                    break;
                case FLAG_RECORD_VOICE:
                    if (mMosBinder != null) {
                        Log.i(TAG, "record");
                        MosCaculator.setMicrophoneMute(true);

                        //                        if (!BuildConfig.DEBUG) {
                        mMosBinder.setFileNameExtends(mRcuFileName, mTaskModelWrapper.getFileExtend());
                        //                        }
                        boolean ret = mMosBinder.runRecord(mFileType.getName());
                        if (ret) {
                            //EventBus.getDefault().post("record:" + mFileType.getName());
                            caculateResult();
                        } else {
                            Log.i(TAG, "record failed");
                            stop();
                        }
                    }
                    break;
                case FLAG_SEND_DATA:
                    if (mPhoneBinder != null) {
                        mPhoneBinder.sendMessage((String) msg.obj);
                    }
                    break;
                case FLAG_SYNC_ALIGN:
                    //EventBus.getDefault().post("同步对齐 10 s");
                    Log.w(TAG, "sync aligin 10 s");
                    try {
                        Thread.sleep(TIME_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mNeedSyncAlign = false;
                    break;
                default:
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return true;
    }


    private void caculateResult() {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (mLock) {
                        if (!mIsStop) {
                            String recordFile = mMosBinder.getRecordFile();

                            int t = 0;
                            long start = System.currentTimeMillis();
                            while (recordFile == null && t < 30) {
                                Thread.sleep(500);
                                recordFile = mMosBinder.getRecordFile();
                                t++;
                            }

                            Log.i(TAG, "recordFile=" + recordFile + ",wait time=" + (System.currentTimeMillis() - start));
                            if (!mIsStop) {
                                double result = 0;
                                if (mCaculateType == TaskModel.MOS_POLQA) {
                                    result = MosCaculator.caculate(mFileType, recordFile, mTaskModelWrapper.isSwb, mTaskModelWrapper.isRealTimeCalculation);
                                } else if (mCaculateType == TaskModel.MOS_PESQ) {
                                    result = MosCaculator.pesqCalculate(mMosBinder, recordFile);
                                }
                                if (result < VOICE_LOW_STAND && result > 0.0) {
                                    MosCaculator.copyLowValueFile(recordFile, result);
                                }
                                if (result <= 1.5 && result > 0.0 && !mNeedSyncAlign) {
                                    Log.w(TAG, "value is low");
                                    mNeedSyncAlign = true;
                                    // 告诉对方，需要对齐
                                    mHandler.obtainMessage(FLAG_SEND_DATA, ALIGN_COMMAND).sendToTarget();
                                    // 插入同步对齐命令,对齐命令保证唯一性
                                    if (!mCommandList.contains(ALIGN_COMMAND)) {
                                        mCommandList.add(0, ALIGN_COMMAND);
                                    }
                                }
                            }
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
