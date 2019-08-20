package com.walktour.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;

import com.dingli.service.test.ipc2jni;
import com.dingli.service.test.ipc2msg;
import com.dinglicom.dataset.model.DataSetEvent;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigNBModuleInfo;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.NbPowerOnDialog;
import com.walktour.model.NetStateModel;

import static com.walktour.Utils.WalkStruct.CurrentNetState.CatM;
import static com.walktour.Utils.WalkStruct.CurrentNetState.NBIoT;


/***
 * NB module上电处理方式
 */
public class NBHandlerService extends Service {
    private final String TAG = "NBHandlerService";
    /**上下文**/
    private Context mContext=null;
    private final int TEST_ITEM = 8001;
    private final int TEST_INITED = 1;
    private ipc2jni aIpc2Jni;
    private boolean initSuccess = false;

    private ConfigNBModuleInfo configNBModuleInfo;

    private final int COMMAND_80011 = 80011;//初始化
    private final int COMMAND_80012 = 80012;//上电命令
    private final int COMMAND_80013 = 80013;//下电命令

    //注意,目前利尔达设备(海思)和高通设备dettach和attach命令不一样
    //    #define DEV_DEATCH	80014  //模块deatch
    //    #define DEV_ATTACH	80015  //模块attach
    private int COMMAND_80014 = 80014;//模块dettach
    private int COMMAND_80015 = 80015;//模块attach

    private final int COMMAND_1006 = 1006;//停止

    /***
     * 下电是否成功
     */
    private boolean hasDetachAccept = false;

    /**
     * 上电是否成功
     */
    private boolean hasAttachAccept = false;

    /**
     * 上下电操作
     */
    public static final int COMMAND_UPDOWN = 1;
    /***
     * 下电操作
     */
    public static final int COMMAND_DOWN = 2;
    /***
     * 上电操作
     */
    public static final int COMMAND_UP = 3;

    /**
     * 独立的下电过程
     */
    public static final int COMMAND_DOWNX = 4;

    public static final int COMMAND_MODEL_ATTACH = 5;
    public static final int COMMAND_MODEL_DETTACH = 6;

    /**
     * 操作
     */
    private int select = COMMAND_UPDOWN;

    /**
     * 释放IPC
     */
    private boolean isFreeIpc = false;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.w(TAG, "onCreate.");
        mContext=this;
        regedit();
        configNBModuleInfo = ConfigNBModuleInfo.getInstance(this);
        //防止程序已经退出，但库没完全退出再次执行的问题
        if (configNBModuleInfo.getNbAtPort() == null || configNBModuleInfo.getNbAtPort().equals("") ||
                configNBModuleInfo.getNbAtPort().equals("null")) {
            stopSelf();
            return;
        }
        aIpc2Jni = new ipc2jni(mEventHandler);
        initSuccess = aIpc2Jni.initServer(getLibLogPath());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (null != intent) {
            select = intent.getIntExtra("select", select);
        }
        if(configNBModuleInfo.exterNBModuleName().startsWith("QualcomLiteprobe")){//高通的设备特殊处理
            COMMAND_80014 = 80013;
            COMMAND_80015 = 80012;
        }
        LogUtil.w(TAG, "onStartCommand - startId = " + startId + ", Thread ID = " + Thread.currentThread().getId());
        try {
            this.startJni("-m command -z " + AppFilePathUtil.getInstance().getAppFilesDirectory("config"));
        }catch (Exception ex){//防止出现异常
            LogUtil.w(TAG,ex.getMessage());
            ex.printStackTrace();
            if(null==aIpc2Jni){
                freeIPC();//先释放,再启动
                aIpc2Jni = new ipc2jni(mEventHandler);
                initSuccess = aIpc2Jni.initServer(getLibLogPath());
                try {
                    this.startJni("-m command -z " + AppFilePathUtil.getInstance().getAppFilesDirectory("config"));
                }catch (Exception ex1){
                    LogUtil.w(TAG,ex1.getMessage());
                }
            }
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.w(TAG, "onBind - Thread ID = " + Thread.currentThread().getId());
        return null;
    }

    @Override
    public void onDestroy() {
        LogUtil.w(TAG, "onDestroy start.");
        super.onDestroy();
        this.unregisterReceiver(mEventReceiver);
        if (null != aIpc2Jni) {
            try {
                LogUtil.w(TAG, "onDestroy send 1006.");
                freeIPC();
            } catch (Exception e) {
                e.printStackTrace();
            }

            LogUtil.w(TAG, "onDestroy finish.");
        }
    }

    private Handler mEventHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            LogUtil.w(TAG, "handleMessage");
            if (select == COMMAND_UPDOWN) {//上下电时

                if (checkNBStatus()) {//
                    return;
                }
            }

            ipc2msg aMsg = (ipc2msg) msg.obj;
            LogUtil.w(TAG, "aMsg.test_item=" + aMsg.test_item + ",aMsg.event_id=" + aMsg.event_id);
            if (aMsg.test_item != TEST_ITEM) {
                LogUtil.w(TAG, "Error");
                return;
            }

            switch (aMsg.event_id) {
                case TEST_INITED:
                    //防止程序已经退出，但库没完全退出再次执行的问题
                    if (configNBModuleInfo.getNbAtPort() == null || configNBModuleInfo.getNbAtPort().equals("") ||
                            configNBModuleInfo.getNbAtPort().equals("null")) {
                        stopSelf();
                        return;
                    }
                    LogUtil.w(TAG, "TEST_INITED Start");
                    StringBuffer params=new StringBuffer();

                    params.append("DevName::" + configNBModuleInfo.exterNBModuleName() + "\n");
                    params.append("port::" + configNBModuleInfo.exterNBATPort() + "\n");
                    params.append("Baudrate::115200\n");

//                    if(ConfigNBModuleInfo.getInstance(mContext).isHasNBWifiTestModel()){
                        params.append("IP::"+ConfigNBModuleInfo.getInstance(mContext).exterNBSelectWifiIP()+"\n");
                        params.append("IPPort::8888\n");
                        params.append("SocketType::1\n");
//                    }
//                    String dev_params = "DevName::" + configNBModuleInfo.exterNBModuleName() + "\nport::" + configNBModuleInfo.exterNBATPort()) + "\nBaudrate::115200\n";

                    LogUtil.w(TAG, "TEST_ITEM=" + TEST_ITEM + ",80011,dev_params=" + params.toString());
                    LogUtil.w(TAG, "configNBModuleInfo.exterNBATPort())=" + configNBModuleInfo.exterNBATPort());

                    aIpc2Jni.send_command(TEST_ITEM, COMMAND_80011, params.toString(), params.toString().length());
                    try {
                        Thread.currentThread();
                        Thread.sleep(1 * 1000);
                    } catch (Exception e) {
                        LogUtil.w(TAG, "Exception1 =" + e.getMessage());
                    }
                    if (select == COMMAND_UPDOWN) {//先下电再上电
                        Deviceinfo.getInstance().setNbPowerOnStaus(Deviceinfo.POWN_ON_ING);
                    } else if (select == COMMAND_UP) {
                        LogUtil.w(TAG, " COMMAND_80012-----1");
                        aIpc2Jni.send_command(TEST_ITEM, COMMAND_80012, "", 0);
                    } else if (select == COMMAND_DOWNX) {
                        LogUtil.w(TAG, "COMMAND_80013----x");
                        aIpc2Jni.send_command(TEST_ITEM, COMMAND_80013, "", 0);
                        Deviceinfo.getInstance().setNbPowerOnStaus(Deviceinfo.POWN_ON_FAILURE);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        stopSelf();
                    } else if (select == COMMAND_MODEL_ATTACH) {//模块ATTACH
                        LogUtil.w(TAG, " COMMAND_80015-----1");
                        aIpc2Jni.send_command(TEST_ITEM, COMMAND_80015, "", 0);
                    } else if (select == COMMAND_MODEL_DETTACH) {//模块DEATTACH
                        LogUtil.w(TAG, " COMMAND_80014-----1");
                        aIpc2Jni.send_command(TEST_ITEM, COMMAND_80014, "", 0);
                    } else {
                        aIpc2Jni.send_command(TEST_ITEM, COMMAND_80013, "", 0);
                    }
                    if (select != COMMAND_DOWNX) {
                        try {
                            Thread.currentThread();
                            //启动线程循环检测
                            Runnable1 r = new Runnable1();
                            new Thread(r).start();
                        } catch (Exception e) {
                            LogUtil.w(TAG, "Exception2 =" + e.getMessage());
                        }
                    }
                    LogUtil.w(TAG, "TEST_INITED End:");
                    break;
            }
        }
    };

    private void startJni(String args) {
        LogUtil.i(TAG, "initSuccess = " + initSuccess);
        if (initSuccess) {
            if (configNBModuleInfo.exterNBATPort() == null || configNBModuleInfo.exterNBATPort().equals("") ||
                    configNBModuleInfo.exterNBATPort().equals("null")) {
                stopSelf();
                return;
            }
            Deviceinfo.getInstance().setATModel(true);
            String client_path;
            if (Deviceinfo.getInstance().isUseRoot()) {
                client_path = AppFilePathUtil.getInstance().getAppLibDirectory() + "datatests_android_devmodem";
            } else {
                client_path = AppFilePathUtil.getInstance().getAppLibDirectory() + "libdatatests_so_devmodem.so";
            }
            aIpc2Jni.set_su_file_path(Deviceinfo.getInstance().getSuOrShCommand() + " -c", "export LD_LIBRARY_PATH=" + AppFilePathUtil.getInstance()
                    .getAppLibDirectory());
            LogUtil.i(TAG, "client_path = " + client_path);
            LogUtil.i(TAG, "args = " + args);
            boolean isSuccess = aIpc2Jni.run_client(client_path, args);
            if (isSuccess) {
                LogUtil.w(TAG, "is Success.");
            } else {
                LogUtil.w(TAG, "is failure.");
            }
        }
    }

    /**
     * 获取库的日志路径
     *
     * @return
     */
    private String getLibLogPath() {
        return AppFilePathUtil.getInstance().getSDCardBaseDirectory("liblog");
    }


    /**
     * 检测参数是否已经出来
     */
    private class Runnable1 implements Runnable {
        public void run() {
            try {
                if (select == COMMAND_DOWN || select == COMMAND_UP || select == COMMAND_MODEL_ATTACH || select == COMMAND_MODEL_DETTACH) {//如果是上电或者是下电，等待返回信令
                    for (int i = 0; i < 10; i++) {
                        if (hasDetachAccept && select == COMMAND_DOWN) {
                            break;
                        } else if (hasAttachAccept && select == COMMAND_UP) {
                            break;
                        } else if (hasAttachAccept && select == COMMAND_MODEL_ATTACH) {
                            break;
                        } else if (hasDetachAccept && select == COMMAND_MODEL_DETTACH) {
                            break;
                        }
                        Thread.sleep(500);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            int times = 2;
            boolean isStop = false;
            while (true && !isStop) {
                if (times <= 0) {
                    stopSelf();
                    break;
                }
                if (hasDetachAccept && select == COMMAND_DOWN) {
                    stopSelf();
                    break;
                } else if (hasAttachAccept && select == COMMAND_UP) {
                    stopSelf();
                    break;
                } else if (hasAttachAccept && select == COMMAND_MODEL_ATTACH) {
                    stopSelf();
                    break;
                } else if (hasDetachAccept && select == COMMAND_MODEL_DETTACH) {
                    stopSelf();
                    break;
                }

                try {
                    if (select == COMMAND_UPDOWN) {
                        long startTime = System.currentTimeMillis();
                        //先下电，再上电
                        LogUtil.w(TAG, "COMMAND_80013--1..");
                        aIpc2Jni.send_command(TEST_ITEM, COMMAND_80013, "", 0);
                        for (int i = 0; i < 10; i++) {
                            Thread.sleep(300);
                        }
                        LogUtil.w(TAG, "COMMAND_80012--2..");
                        aIpc2Jni.send_command(TEST_ITEM, COMMAND_80012, "", 0);


                        if (checkNBStatus()) {
                            stopSelf();
                            break;
                        }
                        long seepTime = Integer.parseInt(SharePreferencesUtil.getInstance(NBHandlerService.this).getString(WalktourConst.SYS_SETTING_nbmodule_powerondelay, "60"));
                        LogUtil.w(TAG, "seepTime=" + seepTime);
                        //上一次电就不管了
                        while (!isStop) {
                            Thread.sleep(2000);
                            freeIPC();
                            if (checkNBStatus()) {
                                isStop = true;
                            } else {

                                if (System.currentTimeMillis() - startTime > seepTime * 1000 && Deviceinfo.getInstance().getNbPowerOnStaus() != Deviceinfo.POWN_ON_SUCCESS) {//上电没成功且超过1分钟都判为失败
                                    LogUtil.w(TAG, "the time is over...");
                                    Deviceinfo.getInstance().setNbPowerOnStaus(Deviceinfo.POWN_ON_FAILURE);
                                    isStop = true;
                                    //跳转到确定窗口
                                    Intent intent = new Intent();
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setClass(NBHandlerService.this, NbPowerOnDialog.class);
                                    startActivity(intent);
                                }
                            }
                        }
                        if (isStop) {
                            stopSelf();
                            break;
                        }


                    } else if (select == COMMAND_UP) {
                        for (int i = 0; i < 10; i++) {
                            if (hasAttachAccept) {
                                break;
                            }
                            Thread.sleep(1000);
                        }
                        if (!hasAttachAccept) {
                            aIpc2Jni.send_command(TEST_ITEM, COMMAND_80012, "", 0);
                        }
                        times -= 1;
                    } else if (select == COMMAND_DOWN) {
                        for (int i = 0; i < 10; i++) {
                            if (hasDetachAccept) {
                                break;
                            }
                            Thread.sleep(1000);
                        }
                        if (!hasDetachAccept) {
                            aIpc2Jni.send_command(TEST_ITEM, COMMAND_80013, "", 0);
                        }
                        times -= 1;
                    } else if (select == COMMAND_MODEL_ATTACH) {//等待7秒再做一次，一共做三次
                        for (int i = 0; i < 10; i++) {
                            if (hasAttachAccept) {
                                break;
                            }
                            Thread.sleep(1000);
                        }
                        if (!hasAttachAccept) {
                            aIpc2Jni.send_command(TEST_ITEM, COMMAND_80015, "", 0);
                            LogUtil.w(TAG, "COMMAND_80015..");
                        }

                        times -= 1;
                    } else if (select == COMMAND_MODEL_DETTACH) {//COMMAND_MODEL_DETTACH 只做一次
                        for (int i = 0; i < 10; i++) {
                            if (hasDetachAccept) {
                                break;
                            }
                            Thread.sleep(1000);
                        }
                        times -= 1;
                    }
                    LogUtil.i(TAG, "nb send command...");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 检查NB上电状态
     */
    private boolean checkNBStatus() {
        NetStateModel state = NetStateModel.getInstance();
        WalkStruct.CurrentNetState status = state.getCurrentNetTypeSync();
        if (status == NBIoT || status == CatM) {
            LogUtil.i(TAG, "netType is NBIoT");
            Deviceinfo.getInstance().setNbPowerOnStaus(Deviceinfo.POWN_ON_SUCCESS);
            try {//上完电等待5秒，适配利尔达上完点后自动附着
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(500);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    /**
     * 广播接收器:接收通信过程中的信令
     */
    private BroadcastReceiver mEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WalkMessage.ACTION_EVENT)) {
                int rcuId = intent.getIntExtra(WalkMessage.KEY_EVENT_RCUID, 0);
                LogUtil.w(TAG, "RCUID=" + Integer.toHexString(rcuId));
                switch (rcuId) {
                    case DataSetEvent.ET_LTEMsgDetachRequest://detach操作
                        if (select == COMMAND_DOWN || select == COMMAND_MODEL_DETTACH) {
                            hasDetachAccept = true;
                        }
                        break;
                    case DataSetEvent.ET_LTEMsgAttachComplete://attach操作
                        if (select == COMMAND_UP || select == COMMAND_MODEL_ATTACH) {
                            hasAttachAccept = true;
                        }

                        break;
                }
                LogUtil.w(TAG,"hasDetachAccept="+hasDetachAccept+",hasAttachAccept="+hasAttachAccept);
            }
        }
    };

    //注册广播接收器
    private void regedit() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.ACTION_EVENT);
        this.registerReceiver(mEventReceiver, filter);
    }

    private void freeIPC() {
        if (!isFreeIpc) {
            isFreeIpc = true;
            if(null!=aIpc2Jni) {
                aIpc2Jni.send_command(TEST_ITEM, COMMAND_1006, "", 0);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                aIpc2Jni.uninit_server();
            }
            aIpc2Jni = null;
            Deviceinfo.getInstance().setATModel(false);
        }

    }
}
