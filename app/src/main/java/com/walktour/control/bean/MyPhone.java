package com.walktour.control.bean;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.RemoteException;
import android.ril.com.datangb2b.MethodManager;
import android.support.annotation.RequiresApi;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;

import com.android.internal.telephony.ITelephony;
import com.dingli.ott.util.OttUtil;
import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ImageUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.gps.GpsSetDialog;
import com.walktour.gui.locknet.ForceManager;
import com.walktour.gui.locknet.VivoControler;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
import com.walktour.service.SamsungService;
import com.walktour.service.XiaoMi8CustomService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyPhone {
    ITelephony iTelePhony;

    private static final String TAG = "MyPhone";

    private ActivityManager activityManager;
    private Context mContext;

    public MyPhone(Context context) {
        this.mContext = context;
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<TelephonyManager> clazz = TelephonyManager.class;
            Method getITelephonyMethod = null;
            getITelephonyMethod = clazz.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            this.iTelePhony = (ITelephony) getITelephonyMethod.invoke(tManager, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 管理窗口
        this.activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
    }

    /**
     * 拨打电话 此方法需要共享系统进程号，共享后需要针对手机签名才能使用
     */
    public void call(String number) {

        Deviceinfo info = Deviceinfo.getInstance();
        boolean isGeneralMode = ApplicationModel.getInstance().isGeneralMode();
        LogUtil.d(TAG, "--dial way:" + info.getDialWay() + "--number:" + number);
        LogUtil.d(TAG, "isOppoCustom():" + Deviceinfo.getInstance().isOppoCustom());
        // 部分机器使用trace拨打可能引起死机
        if (!isGeneralMode && info.getDialWay() == Deviceinfo.DIAL_WITH_TRACE) {
            Intent intent = new Intent();
            intent.setAction(WalkMessage.ACTION_CALL_BY_TRACE);
            intent.putExtra(WalkMessage.KEY_CALL_NUMBER, number);
            LogUtil.i(TAG, "--->call by trace,number:" + number);
            mContext.sendBroadcast(intent);
        } else if (Deviceinfo.getInstance().isOppoCustom()) {
            String serviceCall = "service call phone 1 s16 \"" + number + "\"";
            UtilsMethod.runRootCommand(serviceCall);
            UtilsMethod.runRootCommand("input tap 575 1811");
        } else if (!isGeneralMode && info.getDialWay() == Deviceinfo.DIAL_WITH_API) {
            try {
                LogUtil.i(TAG, "--->call by api,number:" + number);
                iTelePhony.call(number);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (!isGeneralMode && info.getDialWay() == Deviceinfo.DIAL_WITH_SYSTEM_APP) {
            Intent intent = new Intent(WalktourConst.WALKTOUR_SYS_CALL);
            intent.putExtra(WalktourConst.KEY_NUMBER, number);
            mContext.sendBroadcast(intent);
        } else if (!isGeneralMode && info.getDialWay() == Deviceinfo.DIAL_WITH_RADIOOPTION) {
            // 通过radiooption 拨打电话
            UtilsMethod.runRootCommand(mContext.getFilesDir().getParent() + "/radiooptions 8 " + number);
        } else if (!isGeneralMode && info.getDialWay() == Deviceinfo.DIAL_WITH_SYSTEM_AM) {
            String cmdstr = "am start -a android.intent.action.CALL -d tel:" + number;
            UtilsMethod.runRootCommand(cmdstr);
        } else if (info.getDialWay() == Deviceinfo.DIAL_WITH_SERVICE_CALL) {
            String serviceCall = "service call phone 1 s16 \"" + number + "\"";
            UtilsMethod.runRootCommand(serviceCall);
            UtilsMethod.runRootCommand("input keyevent 66");
        } else if (info.getDialWay() == Deviceinfo.DIAL_WITH_SERVICE_CALL2) {
            String serviceCall = "service call phone 2 s16 \"" + number + "\"";
            UtilsMethod.runRootCommand(serviceCall);
            UtilsMethod.runRootCommand("input keyevent 66");
        } else if (info.getDialWay() == Deviceinfo.DIAL_WITH_SYSTEM_API_4) {
            callBySystemApi4(number);
        } else if (info.getDialWay() == Deviceinfo.DIAL_WITH_SYSTEM_API_5) {
            callBySystemApi5(number);
        } else if (isGeneralMode) {
            this.callBySystemApi6(number);
        }
    }

    private void callBySystemApi6(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + number));
        mContext.startActivity(intent);
    }

    private void callBySystemApi4(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);

        intent.setPackage("com.android.phone");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + number));
        mContext.startActivity(intent);
    }

    private void callBySystemApi5(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setPackage("com.android.server.telecom");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + number));
        mContext.startActivity(intent);
    }


    public void makeVideoCall(Context context, String number) {
        Deviceinfo info = Deviceinfo.getInstance();
        if (info.getDevicemodel().equals("SM-G9250")
                || info.getDevicemodel().equals("SM-G9200")
                || info.getDevicemodel().equals("SM-G9300")
                || info.getDevicemodel().equals("SM-G9350")
                ) {
            UtilsMethod.runRootCommand("service call phone 2 s16 " + number);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                LogUtil.w(TAG, "makeVideoCall", e);
            }
            UtilsMethod.runRootCommand("input tap 265 2400");
        } else if (Deviceinfo.getInstance().isSamsungCustomRom()) {
            if(Deviceinfo.getInstance().isCustomS9()){
                UtilsMethod.runRootCommand("service call phone 1 s16 " + number);
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    LogUtil.w(TAG, "makeVideoCall", e);
                }
                UtilsMethod.runRootCommand("input tap 206 1775");
            }else if(Deviceinfo.getInstance().isA60Custom()){
                UtilsMethod.runRootCommand("service call phone 1 s16 " + number);
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    LogUtil.w(TAG, "makeVideoCall", e);
                }
                UtilsMethod.runRootCommand("input tap 230 1930");
            }else {
                MethodManager mMethodManager;
                mMethodManager = MethodManager.from(context);
                mMethodManager.b2bVideoCall(Uri.parse("tel:" + number));
            }
        } else if (info.getDevicemodel().equals("SM-G9500")
                || info.getDevicemodel().equals("SM-G9600")) {
            UtilsMethod.runRootCommand("service call phone 1 s16 " + number);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                LogUtil.w(TAG, "makeVideoCall", e);
            }
            UtilsMethod.runRootCommand("input tap 200 1980");
        } else if (info.getDevicemodel().equals("SM-G9550")) {
            UtilsMethod.runRootCommand("service call phone 1 s16 " + number);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                LogUtil.w(TAG, "makeVideoCall", e);
            }
            UtilsMethod.runRootCommand("input tap 200 1900");
        } else if (info.getDevicemodel().equals("ZTENX529J") || Deviceinfo.getInstance().isOppoCustom()) {
            UtilsMethod.runRootCommand("service call phone 1 s16 " + number);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                LogUtil.w(TAG, "makeVideoCall", e);
            }
            UtilsMethod.runRootCommand("input tap 575 1811");
        } else if (Deviceinfo.getInstance().isVivo()) {
            ForceManager forceManager = ForceManager.getInstance();
            forceManager.init();
            forceManager.makeVideoCall(context, number);
        } else if (Deviceinfo.getInstance().isXiaomi()){
            UtilsMethod.runRootCommand("service call phone 1 s16 " + number);
        } else {
            Intent callIntent = new Intent("com.android.phone.videocall");
            callIntent.putExtra("videocall", true);
            callIntent.setData(Uri.parse("tel:" + number));
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(callIntent);
        }
    }

    /*
    vivo的接听指令
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void answerRingingCallEx() throws Exception {
        TelecomManager tcm = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
        Class c = Class.forName(tcm.getClass().getName());
        Method m = c.getMethod("acceptRingingCall", (Class[]) null);
        m.invoke(tcm, (Object[]) null);
    }

    /*
        vivo的挂机指令
         */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void endCallEx() throws Exception {
        TelecomManager tcm = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
        Class c = Class.forName(tcm.getClass().getName());
        Method m = c.getMethod("endCall", (Class[]) null);
        m.invoke(tcm, (Object[]) null);
    }

    /**
     * 挂机
     */
    public void endCall() {
        Deviceinfo info = Deviceinfo.getInstance();
        boolean isGeneralMode = ApplicationModel.getInstance().isGeneralMode();
        LogUtil.w(TAG, "---endCall()");
        if (Deviceinfo.getInstance().isSamsungCustomRom()) {
            if(Deviceinfo.getInstance().isCustomS9()){
                boolean endC=SamsungService.getInStance(mContext).getService().endCall();
                LogUtil.w(TAG,"endCall=x="+endC);
//                ToastUtil.showToastShort(mContext,"endCall="+endC);
            }else  if (Deviceinfo.getInstance().isA60Custom()){
                String cmdstr = "input keyevent KEYCODE_ENDCALL";
                UtilsMethod.runRootCommand(cmdstr);
            } else {
                endCallS8(mContext);
            }
        } else if (info.getDevicemodel().startsWith("vivo") ) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    endCallEx();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Deviceinfo.getInstance().isXiaomi()) {
            String cmdstr = "input keyevent KEYCODE_ENDCALL";
            UtilsMethod.runRootCommand(cmdstr);
            LogUtil.w(TAG, "---end call by input keyevent-");
        } else if (!isGeneralMode && info.getDialWay() == Deviceinfo.DIAL_WITH_TRACE) {
            Intent intent = new Intent();
            intent.setAction(WalkMessage.ACTION_HANGUP_BY_TRACE);
            LogUtil.w(TAG, "---hang up by trace");
            mContext.sendBroadcast(intent);
        } else if (!isGeneralMode && info.getDialWay() == Deviceinfo.DIAL_WITH_SYSTEM_AM
                && !info.getDevicemodel().equals("HTC M10u")) {
            String cmdstr = "input keyevent KEYCODE_ENDCALL";
            UtilsMethod.runRootCommand(cmdstr);
            LogUtil.w(TAG, "---end call by input keyevent-");
        } else {
            try {
                LogUtil.w(TAG, "---hang up by API");
                iTelePhony.endCall();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 接听来电
     */
    public void aceeptCall() {
        Deviceinfo info = Deviceinfo.getInstance();
        int sdk = Build.VERSION.SDK_INT;
        LogUtil.i(TAG, "--aceeptCall--sdk:" + sdk + "--acceptWay:" + info.getAcceptWay());
        if (Deviceinfo.getInstance().isVivo() || Deviceinfo.getInstance().isSamsungCustomRom() || Deviceinfo.getInstance().isXiaomi()) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    /**
                     * 小米8的视频通话中映射变为普通通话，需要用模拟按键来点击视频通话
                     * 小米8坐标为960,250  小米MIX 2坐标做970 120
                     */
                    if (Deviceinfo.getInstance().isXiaomi()){
                        if (Deviceinfo.getInstance().getDevicemodel().equals("MIX 2")){
                            UtilsMethod.runRootCommand("input tap 970 120");
                        }else if(Deviceinfo.getInstance().getDevicemodel().equals("MI 8")){
                            UtilsMethod.runRootCommand("input tap 960 250");
                        }else {
                            answerRingingCallEx();
                        }

                    }else {
                        answerRingingCallEx();
                    }
                }else{
                    acceptCallByWay(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 出现异常，走普通接听方法
                acceptCallByWay(info);
            }
        } else {
            // 4.2及以下的版本都可以直接用am接听电话,有发现不行的再说
            if (sdk < 15) {
                // 通过ActivityManager的命令来接听
                acceptCallByAM();
            } else {
                acceptCallByWay(info);
            }
        }
        closeCallScreen(500);
    }

    private void acceptCallByWay(Deviceinfo info) {
        if (info.getAcceptWay() == Deviceinfo.DIAL_WITH_TRACE) {
            acceptCallByAPI();
        } else if (info.getAcceptWay() == Deviceinfo.DIAL_WITH_API) {
            acceptCallByAPI();
        } else if (info.getAcceptWay() == Deviceinfo.DIAL_WITH_SYSTEM_APP) {
            acceptCallByWalkSetting();
        } else if (info.getAcceptWay() == Deviceinfo.DIAL_WITH_RADIOOPTION) {
            acceptCallByRadiooption();
        } else if (info.getAcceptWay() == Deviceinfo.DIAL_WITH_SYSTEM_AM) {
            acceptCallByAM();
        } else if (info.getAcceptWay() == Deviceinfo.CALL_BY_INPUT_KEYEVENT) {
            acceptCallByInputKeyevent();
        } else if (info.getAcceptWay() == Deviceinfo.CALL_BY_MICRO_PHONE) {
            acceptCallByMicroPhone();
        }
    }

    private void acceptCallByAPI() {
        try {
            iTelePhony.answerRingingCall();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void acceptCallByWalkSetting() {
        Intent intent = new Intent(WalktourConst.WALKTOUR_SYS_SETTING_ACCEPTCALL);
        mContext.sendBroadcast(intent);
    }

    private void acceptCallByRadiooption() {
        // 通过/system/bin/下的rediooptions实现接听功能
        // if(WalktourApplication.isRootSystem()){
        UtilsMethod.runRootCommand(mContext.getFilesDir().getParent() + "/radiooptions 9");
    }

    private void acceptCallByAM() {
        // 通过ActivityManager的命令来接听
        // if(WalktourApplication.isRootSystem()){
        UtilsMethod.runRootCommand("am start -n com.android.phone/.InCallScreen -a android.intent.action.ANSWER");
    }

    private void acceptCallByInputKeyevent() {
        String cmdstr = "input keyevent 79";
        if (Deviceinfo.getInstance().getDevicemodel().startsWith("vivo")) {
            LogUtil.w(TAG, "acceptCallByInputKeyevent is vivo");
            UtilsMethod.runShCommand(cmdstr);
        } else {
            UtilsMethod.runRootCommand(cmdstr);
        }
    }

    /**
     * 通过调用back按键退出当前的拨打界面
     */
    private void acceptCallBackKey() {
        LogUtil.w(TAG, "----acceptCallBackKey=----");
        UtilsMethod.runRootCommand("input keyevent 4");
    }

    /**
     * Shell指令执行回退键
     */
    private void performBackByShell() {
        LogUtil.w(TAG, "----performBackByShell----");
        UtilsMethod.runRootCMD("input keyevent 4");
    }

    private void acceptCallByMicroPhone() {
        Intent intent = new Intent(Intent.ACTION_HEADSET_PLUG);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("state", 1);
        intent.putExtra("microphone", 1);
        intent.putExtra("name", "Headset");
        this.mContext.sendOrderedBroadcast(intent, "android.permission.CALL_PRIVILEGED");

        Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
        KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
        localIntent2.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent1);
        this.mContext.sendOrderedBroadcast(localIntent2, "android.permission.CALL_PRIVILEGED");

        Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
        KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
        localIntent3.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent2);
        this.mContext.sendOrderedBroadcast(localIntent3, "android.permission.CALL_PRIVILEGED");

        Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
        localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        localIntent4.putExtra("state", 0);
        localIntent4.putExtra("microphone", 1);
        localIntent4.putExtra("name", "Headset");
        this.mContext.sendOrderedBroadcast(localIntent4, "android.permission.CALL_PRIVILEGED");
    }

    /**
     * 关闭手机的数据服务
     */
    public void disableDataConnectivity() {
        try {
            int sdk = UtilsMethod.getSDKVersionNumber();
            LogUtil.w(TAG, "---disableDataServie -- sdk version = " + sdk);
            if (sdk > 9) {
                if (sdk < 15) {
                    final ConnectivityManager conman = (ConnectivityManager) mContext
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                    final Field iConnectivityManagerField = conman.getClass().getDeclaredField("mService");
                    iConnectivityManagerField.setAccessible(true);
                    final Object iConnectivityManager = iConnectivityManagerField.get(conman);
                    final Method setMobileDataEnabledMethod = iConnectivityManager.getClass()
                            .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
                    setMobileDataEnabledMethod.setAccessible(true);
                    setMobileDataEnabledMethod.invoke(iConnectivityManager, false);
                } else {
                    Intent intent = new Intent(WalktourConst.WALKTOUR_SYS_SETTING_DATA);
                    intent.putExtra(WalktourConst.IS_ENABLE, false);
                    this.mContext.sendBroadcast(intent);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                iTelePhony.disableDataConnectivity();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启手机的数据服务
     */
    public void enableDataConnectivity() {
        try {
            int sdk = UtilsMethod.getSDKVersionNumber();
            LogUtil.w(TAG, "---enableDataServie  -- sdk version = " + sdk);
            if (sdk > 9) {
                if (sdk < 15) {
                    final ConnectivityManager conman = (ConnectivityManager) mContext
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                    final Field iConnectivityManagerField = conman.getClass().getDeclaredField("mService");
                    iConnectivityManagerField.setAccessible(true);
                    final Object iConnectivityManager = iConnectivityManagerField.get(conman);
                    final Method setMobileDataEnabledMethod = iConnectivityManager.getClass()
                            .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
                    setMobileDataEnabledMethod.setAccessible(true);
                    setMobileDataEnabledMethod.invoke(iConnectivityManager, true);
                } else {
                    Intent intent = new Intent(WalktourConst.WALKTOUR_SYS_SETTING_DATA);
                    intent.putExtra(WalktourConst.IS_ENABLE, true);
                    this.mContext.sendBroadcast(intent);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                iTelePhony.enableDataConnectivity();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测当前数据接入点是否有效
     *
     * @return
     * @author tangwq
     */
    public boolean isDataConnectivityPossible() {
        try {
            return iTelePhony.isDataConnectivityPossible();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 无线是否打开
     */
    public boolean isRadioOn() {
        try {
            return iTelePhony.isRadioOn();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 开关无线
     */
    public void setRadioPower(boolean enable) {
        try {
            int sdk = UtilsMethod.getSDKVersionNumber();
            LogUtil.w(TAG, "---set radio on  -- sdk version = " + sdk);
            if (Deviceinfo.getInstance().getDevicemodel().equals("HuaweiMT7")) {
                DatasetManager.getInstance(mContext).devWritePortExt(enable ? 3 : 4, "".getBytes());
            } else if (sdk < 15) {
                iTelePhony.setRadio(enable);
            } else {
                // 2013.10.28 目前WalktourSetting里只能切换，不能指定是开或关
                Intent intent = new Intent(WalktourConst.WALKTOUR_SYS_SETTING_RADIO);
                intent.putExtra(WalktourConst.IS_ENABLE, enable);
                this.mContext.sendBroadcast(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开wifi
     */
    public void openWifi() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled() && wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
            wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 检查GPS服务是否可用,根据配置文件来决定GPS打开的方式，手工打开或者提示用户跳到设置页面打开
     *
     * @param isOpen 是否打开操作
     */
    public void checkGpsProvider(boolean isOpen) {
        Deviceinfo info = Deviceinfo.getInstance();
        LogUtil.v(TAG, "-checkGpsProvider--" + info.getOpenGpsWay());
        int openGpsWay = info.getOpenGpsWay();
        int sdk = UtilsMethod.getSDKVersionNumber();
        if (sdk > 17) {
            openGpsWay = Deviceinfo.GPS_PROVIDER_MANUAL;
        }
        switch (openGpsWay) {
            case Deviceinfo.GPS_PROVIDER_OFF:
                // do nothing
                break;

            case Deviceinfo.GPS_PROVIDER_MANUAL:
                // 弹出窗口提示GPS设备没有打开，是否转到打开GPS设备页面
                LocationManager alm = (LocationManager) this.mContext.getSystemService(Context.LOCATION_SERVICE);
                if (!isOpen || alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER))
                    return;
                Intent gpsDialog = new Intent(mContext, GpsSetDialog.class);
                gpsDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(gpsDialog);
                break;

            case Deviceinfo.GPS_PROVIDER_PROGRAMING:
                // 强制打开GPS
                openGps();
                break;
        }

    }

    /**
     * 打开GPS(当GPS不可用时切换开关)
     */
    public void openGps() {
        tootleGpsProvider();
    }

    /**
     * GPS开关，注意这里仅仅是一个开关，当GPS不可用时设为可用，反之亦然
     */
    private void tootleGpsProvider() {
        int sdk = UtilsMethod.getSDKVersionNumber();
        LogUtil.v(TAG, "--start to open or close gps -- sdk version = " + sdk);
        try {
            if (sdk < 15) {
                Intent gpsIntent = new Intent();
                gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
                gpsIntent.setData(Uri.parse("custom:3"));
                PendingIntent.getBroadcast(mContext, 0, gpsIntent, 0).send();
            } else {
                Intent intent = new Intent(WalktourConst.WALKTOUR_SYS_SETTING_GPS);
                this.mContext.sendBroadcast(intent);
            }
        } catch (Exception e) {
            LogUtil.w(TAG, "--open gps failure--");
            e.printStackTrace();
        }
    }

    /**
     * 关闭通话显示屏
     */
    public void closeCallScreen(final int timeout) {
        LogUtil.d(TAG, "----closeCallScreen----");
        if (Deviceinfo.getInstance().isS8()){
            new Thread(moveHomeRunnable).start();
        }else {
            Thread closer = new Thread(new IncallScreenCloser(mContext, timeout));
            closer.start();
        }
    }

    /**
     * 回到Walktour界面
     */
    public void moveToHome() {
        LogUtil.d(TAG, "----closeCallScreen----");
        new Thread(moveHomeRunnable).start();
    }


    private Lock mLock = new ReentrantLock();
    private Runnable moveHomeRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLock.tryLock()) {
                try {
                    int times = 0;
                    while (times < 5) {
                        // 5 secends is enough
                        Thread.sleep(1000);
                        OttUtil.moveToHome(mContext);
                        times++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mLock.unlock();
                }
            }else {
                // do nothing
            }
        }
    };

    public static String getApnName(Context context) {
        try {
            ConnectivityManager conManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = conManager.getActiveNetworkInfo();
            //获取网络接入点，这里一般为cmwap和cmnet
            return ni.getExtraInfo();
        } catch (Exception ex) {
            LogUtil.w(TAG, ex.getMessage());
        }
        return "";
    }

    /**
     * 转到Walktour的页面
     */
    @SuppressWarnings("deprecation")
    public void switchToWalktour() {
        List<RunningTaskInfo> taskList = activityManager.getRunningTasks(10);
        // 如果Walktour的Activity在最顶端，则不处理
        RunningTaskInfo firstTask = taskList.get(0);
        if (firstTask.topActivity.getPackageName().equals(mContext.getPackageName())) {
            // do nothing ;
            LogUtil.w(TAG, "top activity:" + firstTask.topActivity.getPackageName());
        } else {
            for (int i = 1; i < taskList.size(); i++) {
                RunningTaskInfo info = taskList.get(i);
                LogUtil.i(TAG, "task" + i + "'s topActivity:" + info.topActivity.getPackageName());
                if (info.topActivity.getPackageName().equals(mContext.getPackageName())) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    ComponentName componentName = new ComponentName(info.topActivity.getPackageName(),
                            info.topActivity.getClassName());
                    intent.setComponent(componentName);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 清除
                    mContext.startActivity(intent);
                    break;
                }
            }
        }
    }

    /**
     * 关闭InCallScreen的线程
     */
    private class IncallScreenCloser implements Runnable {
        private Context context;
        private int timeout;

        /**
         * @param context
         * @param timeout 超时后结束线程
         */
        public IncallScreenCloser(Context context, int timeout) {
            this.context = context;
            this.timeout = timeout;
        }

        @Override
        @SuppressWarnings("deprecation")
        public void run() {
            int t = 0;
            while (t < timeout) {
                t++;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int max = 2;
                List<RunningTaskInfo> list = activityManager.getRunningTasks(max);
                RunningTaskInfo firstTask = list.size() > 0 ? list.get(0) : null;// 第1个Task
                RunningTaskInfo secondTask = list.size() > 1 ? list.get(1) : null;// 第2个Task(被通话屏幕覆盖的)
                ComponentName actOfFirstTask = (firstTask != null ? firstTask.topActivity : null);// 第1个Task的Top
                // Activity
                ComponentName actOfSecondTask = (secondTask != null ? secondTask.topActivity : null);// 第2个Task的Top
                // Activity
                LogUtil.w(TAG, "----actOfFirstTask----" + (actOfFirstTask != null ? actOfFirstTask.getClassName() : "null"));
                LogUtil.w(TAG, "----actOfSecondTask----" + (actOfSecondTask != null ? actOfSecondTask.getClassName() : "null"));

                /**
                 * 2013.7.26 lqh 各款手机的通话界面都不一样com.android.phone.SomcInCallScreen/
                 * 三星g9009d:com.android.incallui.InCallActivity
                 */
                if (actOfFirstTask != null) {
                    if (Deviceinfo.getInstance().isOppoCustom()) {
                        //OPPO定制机（非root）
                        LogUtil.i(TAG, "------OppoCustom------");
                        List<RunningTaskInfo> runningTasks = activityManager.getRunningTasks(4);
                        for (RunningTaskInfo runningTask : runningTasks) {
                            if (runningTask != null) {
                                ComponentName topActivity = runningTask.topActivity;
                                if (null != topActivity && topActivity.getClassName().startsWith("com.walktour")) {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_MAIN);
                                        ComponentName componentName = new ComponentName(topActivity.getPackageName(), topActivity.getClassName());
                                        intent.setComponent(componentName);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    } catch (Exception e) {
                                        LogUtil.e(TAG, "---OPPO R11s Custom failed to go back to Activity:" + topActivity.getClassName());
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    } else if (actOfFirstTask.getClassName().startsWith("com.android.phone")
                            || actOfFirstTask.getClassName().startsWith("com.android.incallui")
                            || actOfFirstTask.getClassName().contains("InCall")) {
                        // if(
                        // actOfFirstTask.getClassName().equals("com.android.phone.InCallScreen")
                        // ){
                        // 显示3秒后关闭
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // 关闭InCallScreen
                        if (actOfSecondTask != null && !actOfSecondTask.getClassName().equals("com.htc.launcher.Launcher")) {
                            // ---can not back to Activity:com.htc.launcher.Launcher

                            // 先获取当前的最顶端的task
                            RunningTaskInfo currentTask = activityManager.getRunningTasks(max).get(0);
                            // 如果当前task就是通话屏幕打开前的task,就不再需要跳转了
                            if (currentTask.id == secondTask.id) {
                                return;
                            }
                            LogUtil.w(TAG, "---back to Activity:" + actOfSecondTask.getClassName());
                            try {
                                // 返回IncallScreen之前的页面
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                ComponentName componentName = new ComponentName(actOfSecondTask.getPackageName(),
                                        actOfSecondTask.getClassName());
                                intent.setComponent(componentName);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                if (!actOfSecondTask.equals("com.walktour.gui.Info")
                                        || !actOfSecondTask.equals("com.walktour.gui.data.FileManager")) {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 清除
                                }

                                context.startActivity(intent);
                            } catch (Exception e2) {
                                LogUtil.w(TAG, "---fail to go back to Activity:" + actOfSecondTask.getClassName());
                                e2.printStackTrace();

                            }
                        }
                        break;
                    } else if (actOfFirstTask.getClassName().startsWith("com.walktour")) {// 三星S6
                        //对于OPPO R11S手机特殊处理
                        if (actOfSecondTask.getClassName().equals("com.oppo.launcher.Launcher")) {
                            acceptCallBackKey();
                        } else {
                            // 关闭拨号界面
                            try {
                                // 返回IncallScreen之前的页面
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                ComponentName componentName = new ComponentName(actOfFirstTask.getPackageName(),
                                        actOfFirstTask.getClassName());
                                intent.setComponent(componentName);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            } catch (Exception e2) {
                                LogUtil.w(TAG, "---fail to go back to actOfSecondTask!");
                                e2.printStackTrace();
                            }
                        }
                        break;
                    }
                }
            } // end while
        }// end method run
    }// end inner class

    /**
     * 保存当前屏幕
     *
     * @param activity 当前Activty
     * @param dirPath  文件保存的目录
     */
    public void getScreen(Activity activity, String dirPath, ImageUtil.FileType fileType) {
        Thread thread = new Thread(new Snaper(activity, dirPath, fileType));
        thread.start();
    }

    /**
     * 截屏,由于是I/O操作所以启动线程
     */
    private class Snaper implements Runnable {
        // private Activity activity;
        private String desFilePath;
        private View view;
        private Bitmap bm;
        private ImageUtil.FileType fileType;

        public Snaper(Activity activity, String desFilePath, ImageUtil.FileType fileType) {
            // this.activity = activity;
            this.desFilePath = desFilePath;
            // 获取Bitmap
            view = activity.getWindow().getDecorView();
            view.setDrawingCacheEnabled(true);
            bm = view.getDrawingCache();
            this.fileType = fileType;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (bm == null) {
                LogUtil.w(TAG, "---null");
            } else {
                LogUtil.w(TAG, "---not null");
            }

            // 当前时间
            SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()); // 格式化当前系统日期
            String time = dateFm.format(new Date(System.currentTimeMillis()));
            // 创建新文件
            switch (fileType) {
                case BMP:
                    ShareCommons.CURRENT_SCREEN_SHOT_PIC_NAME = desFilePath + "/" + time + ".bmp";
                    break;
                case PNG:
                    ShareCommons.CURRENT_SCREEN_SHOT_PIC_NAME = desFilePath + "/" + time + ".png";
                    break;
                case JPEG:
                    ShareCommons.CURRENT_SCREEN_SHOT_PIC_NAME = desFilePath + "/" + time + ".jpeg";
                    break;
                default:
                    ShareCommons.CURRENT_SCREEN_SHOT_PIC_NAME = desFilePath + "/" + time + ".png";
                    break;
            }

            File outFile = new File(ShareCommons.CURRENT_SCREEN_SHOT_PIC_NAME);
            try {
                File path = new File(desFilePath);
                if (!path.exists())
                    path.mkdirs();
                outFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 输出流和新文件关联
            FileOutputStream fOutStream = null;
            try {
                fOutStream = new FileOutputStream(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            switch (fileType) {
                case BMP:
                    bm.compress(Bitmap.CompressFormat.WEBP, 100, fOutStream);
                    break;
                case PNG:
                    bm.compress(Bitmap.CompressFormat.PNG, 100, fOutStream);
                    break;
                case JPEG:
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, fOutStream);
                    break;
                default:
                    bm.compress(Bitmap.CompressFormat.PNG, 100, fOutStream);
                    break;
            }
            try {
                if (fOutStream != null)
                    fOutStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fOutStream != null)
                    fOutStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取手机屏幕大小 1,低分辨率 2,中分辨率 3,高分辨率
     */
    public int getScreenSize(Activity activity) {
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);

        return 0;
    }

    /**
     * 启用三星自动接听电话
     */
    public static void startS8AutoAnswer(Context context) {
        Intent i = new Intent("com.android.CUSTOMER_REQUEST_ACTION");
        i.putExtra("customer_action", "AutoAnswer");//自动接听动作
        i.putExtra("action_enable", true); //true 代表开启自动接听；false 关闭自动接听
        context.sendBroadcast(i);
    }

    /**
     * 关闭三星自动接听电话
     */
    public static void stopS8AutoAnswer(Context context) {
        Intent i = new Intent("com.android.CUSTOMER_REQUEST_ACTION");
        i.putExtra("customer_action", "AutoAnswer");//自动接听动作
        i.putExtra("action_enable", false);
        context.sendBroadcast(i);
    }

    /**
     * 三星自动挂断
     */
    public static void endCallS8(Context context) {
        //自动挂断
        Intent i = new Intent("com.android.CUSTOMER_REQUEST_ACTION");
        i.putExtra("customer_action", "AutoCallEnd");//自动挂断动作
        i.putExtra("action_time", 0); //time 是int 类型，表示time秒后，自动挂断
        context.sendBroadcast(i);
    }

    /**
     * 获取rom简短版本号
     *
     * @return G9208ZMU2COJA
     */
    public static String getSimpleROMVersionName() {
        return android.os.Build.VERSION.INCREMENTAL;
    }

    /**
     * 使用adb shell获取rom简短版本号
     *
     * @return
     */
    public static String getSimpleROMVersionNameByShell() {
        return getSystemPropertyByShell("ro.bootloader");
    }

    /**
     * 获取rom版本号
     *
     * @return LMY47X.G9208ZMU2COJA
     */
    public static String getROMVersionNameByShell() {
        return getSystemPropertyByShell("ro.build.display.id");
    }

    public static String getSerialNo() {
        return getSystemPropertyByShell("ro.serialno");
    }

    /**
     * 通过adb shell指令获取系统属性信息
     *
     * @param propName
     * @return
     */
    public static String getSystemPropertyByShell(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            LogUtil.e(TAG, "Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    /**
     * 是否插入耳机头
     *
     * @param context
     * @return
     */
    public static boolean isHeadsetOn(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean isHeadsetOn = audioManager.isWiredHeadsetOn();
        LogUtil.i(TAG, "isHeadsetOn = " + isHeadsetOn);
        return isHeadsetOn;
    }
}