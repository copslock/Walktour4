
package com.walktour.service.app;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.speech.tts.TextToSpeech;
import android.text.format.Formatter;
import android.widget.Toast;

import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigAlarm;
import com.walktour.gui.R;

import java.io.File;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class Alarm {
    //Logcat标签
    private final static String TAG = "WalktourAlarm";

    //告警类型
    private final static String VOICE = "voice";
    private final static String STORGE = "storge";
    private final static String SDCARDLOW = "sdcardlow";
    private final static String SDCARDNON = "sdcardnon";
    private final static String GPSFAIL = "gpsfail";
    private final static String GPSLOST = "gpslost";
    private final static String NET_ERR = "net_err";
    private final static String DATA_STORGE_ERR = "data_storge_err";
    private final static String CONNECT_FAIL = "connect_fail";
    private final static String CALL_DIS = "call_dis";
    private final static String SWICTH_FAIL = "swicth_fail";

    //空间存储告警相关
    private final static long STORGE_UNIT = 1000 * 1000;//单位为M
    private final static long STORGE_ALARM_VALUE = 20;//空间告阀值:

    //GPS告警相关
    private LocationManager mLocationManager;

    //告警提示相关
    private NotificationManager mNotificationManager;//通知管理器 
    private final static int ALARM = 1;//Message.what
    private final static int NOALARM = 0;
    private static boolean hasAlarm = false;
    //TextToSpeech
    private static TextToSpeech mTts;

    private Context context;
    private ActivityManager activityManager;

    public Alarm(Context context) {
        this.context = context;

        mNotificationManager =
                (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);

        //生成TextToSpeech对象
        if (mTts == null) {
            mTts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    //TTS Engine初始化完成
                    if (status == TextToSpeech.SUCCESS) {
                        int result = mTts.setLanguage(Locale.US);
                        //设置发音语言
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                        //判断语言是否可用
                        {
                            LogUtil.v(TAG, "Language is not available");
                        } else {

                        }

                    }
                }

            });
        }//end if

        this.activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    //显示通知
    private void showNotification(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();


        List<RunningTaskInfo> taskList = activityManager.getRunningTasks(10);
        //如果Walktour的Activity在最顶端，则不处理
        RunningTaskInfo firstTask = taskList.get(0);
        Notification.Builder notification = new Notification.Builder(context);
        notification.setSmallIcon(R.mipmap.walktour);
        notification.setWhen(System.currentTimeMillis());
        if (firstTask.topActivity.getPackageName().equals(context.getPackageName())) {
            PendingIntent contentIntent = PendingIntent.getActivity(
                    context, 0, new Intent("nothing"), 0);
            notification.setContentIntent(contentIntent);
            notification.setContentTitle("Walktour");
            notification.setContentText(text);
        } else {
            for (int i = 0; i < taskList.size(); i++) {
                RunningTaskInfo info = taskList.get(i);
                if (info.topActivity.getPackageName().equals(context.getPackageName())) {
                    ComponentName componentName = new ComponentName(
                            info.topActivity.getPackageName(),
                            info.topActivity.getClassName()
                    );
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(componentName);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//清除
                    PendingIntent contentIntent = PendingIntent.getActivity(
                            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notification.setContentIntent(contentIntent);
                    notification.setContentTitle("Walktour");
                    notification.setContentText(text);
                    break;
                }
            }
        }
        mNotificationManager.notify(text, 0, notification.build());
    }


    /**
     * 检查手机空间
     */
    public boolean checkStorge() {
        hasAlarm = false;
        if (ConfigAlarm.getInstance().getChoiced()[1]) {
            //计算手机内部存储
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();//块大小
            long totalBlocks = stat.getBlockCount();//总块数
            long availableBlocks = stat.getAvailableBlocks();//可用块数
            if ((availableBlocks * blockSize / STORGE_UNIT) < STORGE_ALARM_VALUE) {
                hasAlarm = true;
                String content =
                        context.getString(R.string.sys_alarm_storge_mobilelow) + ":\n" +
                                context.getString(R.string.sys_alarm_storge_available) +
                                Formatter.formatFileSize(context, availableBlocks * blockSize) + "\t" +
                                context.getString(R.string.sys_alarm_storge_free) +
                                Formatter.formatFileSize(context, totalBlocks * blockSize);

                if (ConfigAlarm.getInstance().getChoiced()[0]) {
                    mTts.speak(context.getString(R.string.sys_alarm_speech_mobile_low),
                            TextToSpeech.QUEUE_ADD, null);
                }
                showNotification(content);
            }
        }
        return hasAlarm;
    }//end method checkStorge

    /**
     * 检查Sdcard空间大小
     */
    public boolean checkSDcardLow() {
        hasAlarm = false;
        if (ConfigAlarm.getInstance().getChoiced()[2]) {
            String status = Environment.getExternalStorageState();
            if (status.equals(Environment.MEDIA_MOUNTED)) {
                try {
                    File path = Environment.getExternalStorageDirectory();
                    StatFs stat = new StatFs(path.getPath());
                    long blockSize = stat.getBlockSize();
                    long totalBlocks = stat.getBlockCount();
                    long availableBlocks = stat.getAvailableBlocks();

                    if ((blockSize * availableBlocks) / STORGE_UNIT < STORGE_ALARM_VALUE) {
                        hasAlarm = true;
                        String content =
                                context.getString(R.string.sys_alarm_storge_sdcardlow) + ":\n" +
                                        context.getString(R.string.sys_alarm_storge_available) +
                                        Formatter.formatFileSize(context, availableBlocks * blockSize) + "\t" +
                                        context.getString(R.string.sys_alarm_storge_free) +
                                        Formatter.formatFileSize(context, totalBlocks * blockSize);

                        if (ConfigAlarm.getInstance().getChoiced()[0]) {
                            mTts.speak(context.getString(R.string.sys_alarm_speech_SDCard_low),
                                    TextToSpeech.QUEUE_ADD, null);
                        }
                        showNotification(content);
                    }

                } catch (IllegalArgumentException e) {
                    LogUtil.w(TAG, "SDcard is unmounted");
                }
            }

        }
        return hasAlarm;
    }//end method checkSDCard


    /**
     * 检查Sdcard是否可用
     */
    public boolean checkSDcardNon() {
        hasAlarm = false;
        if (ConfigAlarm.getInstance().getChoiced()[3]) {
            String status = Environment.getExternalStorageState();
            if (!status.equals(Environment.MEDIA_MOUNTED)) {
                hasAlarm = true;
                String content = context.getString(R.string.sys_alarm_storge_sdcardnon);

                if (ConfigAlarm.getInstance().getChoiced()[0]) {

                    mTts.speak(context.getString(
                            R.string.sys_alarm_speech_SDCard_unavailable),
                            TextToSpeech.QUEUE_ADD, null);
                }
                showNotification(content);
            }
        }
        return hasAlarm;
    }//end method checkSDCard

    /**
     * 检查GPS是否可用
     */
    @SuppressWarnings("deprecation")
    public boolean checkGPSOff() {
        hasAlarm = false;
        if (ConfigAlarm.getInstance().getChoiced()[4]) {
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                hasAlarm = true;
                String content =
                        context.getString(R.string.sys_alarm_gpsoff);
                if (ConfigAlarm.getInstance().getChoiced()[0]) {
                    mTts.speak(context.getString(R.string.sys_alarm_speech_gpsunavailable),
                            TextToSpeech.QUEUE_ADD, null);
                }
                showNotification(content);
            }
        }
        return hasAlarm;
    }//end method checkGPSFail

    /**
     * 检查网络是否可用
     */
    @SuppressWarnings("deprecation")
    public boolean checkNet() {
        hasAlarm = false;
        if (ConfigAlarm.getInstance().getChoiced()[6]) {

            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService
                            (Context.CONNECTIVITY_SERVICE);
            NetworkInfo net_info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (net_info.getState() == NetworkInfo.State.DISCONNECTED
                    || net_info.getState() == NetworkInfo.State.UNKNOWN) {
                hasAlarm = true;
                LogUtil.w(TAG, "Network error");
                LogUtil.w(TAG, String.valueOf(ConfigAlarm.getInstance().getChoiced()[0]));

                if (ConfigAlarm.getInstance().getChoiced()[0]) {
                    mTts.speak(context.getString(R.string.sys_alarm_speech_neterr),
                            TextToSpeech.QUEUE_ADD, null);
                }
                showNotification(context.getString(R.string.sys_alarm_neterr));
            }
        }
        return hasAlarm;
    }//end method checkNet

}