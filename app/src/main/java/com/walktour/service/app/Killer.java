package com.walktour.service.app;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import com.dingli.wlan.apscan.WifiScanner;
import com.dinglicom.QMIServerFactory;
import com.dinglicom.ResourceCategory;
import com.tencent.android.tpush.service.XGDaemonService;
import com.tencent.android.tpush.service.XGPushServiceV3;
import com.tencent.bugly.crashreport.CrashReport;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.excel.manger.CountTimeManger;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.instance.AlertManager;
import com.walktour.gui.share.UpDownService;
import com.walktour.service.ApplicationInitService;
import com.walktour.service.BlueToothControlService;
import com.walktour.service.CheckLicenseService;
import com.walktour.service.DataFeelUtils;
import com.walktour.service.DatasetRecordService;
import com.walktour.service.FleetService;
import com.walktour.service.OtsSocketUploadService;
import com.walktour.service.SamsungService;
import com.walktour.service.StartDatasetService;
import com.walktour.service.TestInterfaceService;
import com.walktour.service.TestService;
import com.walktour.service.TraceService;
import com.walktour.service.iPackTerminal;
import com.walktour.service.phoneinfo.TelephonyManagerService;

/**
 * Walktour终结者,停止所有Walktour相关的服务和进程,退出app
 */
public class Killer extends Service {
    private String TAG = "WalktourKiller";

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.w(TAG, "--OnCreate--");
        CountTimeManger.getInstance(this).onDestroy();
        //注册广播过滤:中断测试完成
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE);
        this.registerReceiver(mReceiver, filter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "--onStartCommand--");
        //如果当前测试正在进行
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.w(TAG, "--onDestroy--");
        closeResourceCategory(ApplicationModel.getInstance().getHandler_param());
        closeResourceCategory(ApplicationModel.getInstance().getHandler_event());
        closeResourceCategory(ApplicationModel.getInstance().getHandler_business());
        if (ConfigRoutine.getInstance().getNetTimes(getApplicationContext()) < System.currentTimeMillis()) {
            ConfigRoutine.getInstance().setNetTimes(getApplicationContext(), System.currentTimeMillis());
        }
        if (ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.WLAN)) {
            WifiScanner.instance(getApplicationContext()).quitScan();
        }

        this.unregisterReceiver(mReceiver);
        try {

            //首先退出数据集
            stopAppService(DatasetRecordService.class);

            stopAppService(iPackTerminal.class);
            stopAppService(BlueToothControlService.class);
            stopAppService(TelephonyManagerService.class);

            stopAppService(FleetService.class);
            if(!Deviceinfo.getInstance().isUseRoot()||Deviceinfo.getInstance().isRunStartDatasetService()) {
                if((!Deviceinfo.getInstance().isVivo() && !Deviceinfo.getInstance().isOppoCustom())||Deviceinfo.getInstance().isRunStartDatasetService()) {
                    stopAppService(StartDatasetService.class);
                }
            }
            stopAppService(TraceService.class);
            stopAppService(DataService.class);
            stopAppService(StateService.class);
            stopAppService(TestInterfaceService.class);
            stopAppService(AutoTestService.class);
            stopAppService(ApplicationInitService.class);
            stopAppService(DataFeelUtils.class);

            stopAppService(DataTransService.class);
            stopAppService(OtsSocketUploadService.class);
            stopAppService(CheckLicenseService.class);
            stopAppService(TestService.class);
            stopAppService(UpDownService.class);
            stopAppService(XGPushServiceV3.class);
            stopAppService(XGDaemonService.class);
            stopAppService(com.baidu.location.f.class);

            //S9机型解除绑定服务
            if(Deviceinfo.getInstance().isCustomS9()){
                SamsungService.getInStance(this).release();
            }
            //关闭告警语音引擎
            TextToSpeech tts = AlertManager.getInstance(getApplicationContext()).getTTS();
            if (tts != null) {
                tts.shutdown();
            }
            stopAppService(LogService.class);
            ApplicationModel.getInstance().setEnvironmentInit(false);
//            sendBroadcast(new Intent(WalkMessage.KILL_WALKTOUR));
            LogUtil.w(TAG, "version:" + android.os.Build.VERSION.RELEASE);
            if (android.os.Build.VERSION.RELEASE.startsWith("2.1")) {
                ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                manager.restartPackage(getPackageName());
            } else if (!ApplicationModel.getInstance().isGeneralMode()) {
                QMIServerFactory.getInstance().freeControlHandle();
                new KillThread().start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 停止App服务
     *
     * @param cls
     */
    private void stopAppService(Class cls) {
        LogUtil.d(TAG,"stopAppService start:"+cls.getSimpleName());
        Intent it = new Intent(getApplicationContext(), cls);
        stopService(it);
        it = null;
        LogUtil.d(TAG,"stopAppService end:"+cls.getSimpleName());
    }

    /**
     * 启动一个线程执行kill
     *
     * @author Administrator
     */
    private class KillThread extends Thread {
        @Override
        public void run() {
            try {
                CrashReport.closeBugly();
                UtilsMethod.killProcessByPname("logcat", true);
                //等待底层库释放资源
                Thread.sleep(1000);
                executeKillCommand();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行kill方法
     */
    private void executeKillCommand() {
        com.walktour.framework.ui.ActivityManager.finishAll();

        ActivityManager mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            //先杀子进程
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
                if (appProcess.processName.startsWith(getPackageName() + ":")) {
                    LogUtil.w(TAG, "Kill:" + appProcess.processName);
                    android.os.Process.sendSignal(Integer.valueOf(appProcess.pid), android.os.Process.SIGNAL_KILL);
                }
            }
            //再杀主进程
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
                if (appProcess.processName.startsWith(getPackageName())) {
                    LogUtil.w(TAG, "Kill:" + appProcess.processName);
                    android.os.Process.sendSignal(Integer.valueOf(appProcess.pid), android.os.Process.SIGNAL_KILL);
                }
            }
            //保守做法
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }

    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WalkMessage.NOTIFY_INTERRUPTJOBDONE)) {
                stopSelf();
            }
        }
    };
    /***
     * 关闭资源句柄,释放资源
     * @param handler
     */
    private void closeResourceCategory(int handler){
        ResourceCategory.getInstance().Close(handler);
        ResourceCategory.getInstance().FreeResourceHandle(handler);
    }
}