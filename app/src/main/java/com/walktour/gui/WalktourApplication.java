package com.walktour.gui;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.support.multidex.MultiDex;

import com.tencent.android.tpush.XGNotifaction;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushNotifactionCallback;
import com.tencent.bugly.crashreport.CrashReport;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.GlobalExceptionHandler;
import com.walktour.Utils.ServerMessage;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.AssetsUtils;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.map.MapView;
import com.walktour.gui.perceptiontest.surveytask.data.DataProvider;
import com.walktour.gui.perceptiontest.surveytask.data.dao.DBManager;
import com.walktour.gui.setting.bluetoothmos.BluetoothPipeLine;

import org.xutils.x;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * 全局Application
 *
 * @author weirong.fan
 *
 */
public class WalktourApplication extends Application {
    private static final String TAG = "WalktourApplication";
    /**
     * 是否捕获全局异常
     */
    public static boolean isDebug = true;
    /**
     * 是否上传日志附件
     */
    public static boolean isReportAttachments = false;
    public static boolean isInit = false;
    public static String CURRENT_LOG_PATH = "";
    public static String deviceIMEI = "";
    private static Context mContext;
    private final String KEY_ENV_FILE_INIT = "env_file_init";
    private ExecutorService mExecutor = Executors.newCachedThreadPool();
    private List<File> assetsFiles=new LinkedList<>();
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (Build.VERSION.SDK_INT < 21) {
            MultiDex.install(this);
        }
    }

    /**
     *  czc
     *  注意：多进程会多次调用 Application 的 onCreate() 方法，
     *  有一些操作要确定是否需要多次初始化，应避免耗时操作
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
        AppFilePathUtil.getInstance().init(this);
        if (!filterProcess()) {
            x.Ext.init(this);
            Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHandler.get(this));
            if (UtilsMethod.isSystemApplication(this)) {
                //如果是系统级应用，才去做hookWebview操作
                hookWebView();
            }
            // 主进程设置
            if (isMainProcess()) {
                doInNewThread();
                initConfig(mContext);
                initXgPush();
            }
            //Bugly崩溃解决方案,配置仅在主进程上报崩溃异常，避免资源浪费
            CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(mContext);
            strategy.setUploadProcess(isMainProcess());
            CrashReport.initCrashReport(getApplicationContext(), strategy);
            CrashReport.setUserId(MyPhoneState.getInstance().getDeviceId(this));
        }
    }

    private void doInNewThread() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    resetFlags();
                    deviceIMEI = MyPhoneState.getInstance().getDeviceId(getApplicationContext());
                    refreshFile();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                Looper.loop();
            }
        });
    }

    private void initXgPush() {
        /**
         * 为保证弹出通知前一定调用本方法，需要在application的onCreate注册
         *收到通知时，会调用本回调函数。
         *相当于这个回调会拦截在信鸽的弹出通知之前被截取
         *一般上针对需要获取通知内容、标题，设置通知点击的跳转逻辑等等
         */
        XGPushManager.setNotifactionCallback(new XGPushNotifactionCallback() {
            @Override
            public void handleNotify(XGNotifaction xGNotifaction) {
                // 获取标签、内容、自定义内容
                // String title = xGNotifaction.getTitle();
                // 接收到的消息类型
                String messageType = xGNotifaction.getContent();
                // 广播发送出去,统一由广播接收器处理
                Intent intent = new Intent();
                intent.setAction(ServerMessage.ACTION_SHARE_SEND_CONTROL);
                intent.putExtra("messageType", messageType);
                mContext.sendOrderedBroadcast(intent, null);
            }
        });
    }


    public static Context getAppContext() {
        return mContext;
    }


    /**
     * 程序启动时重置一些需要重置的标志
     */
    private void resetFlags() {
        LogUtil.d(TAG,"resetFlags start");
        if(DBManager.getInstance(this).needReloadData()){
            DataProvider.getInstance().initFakeSurveyTaskData(this);
        }
        SharePreferencesUtil.getInstance(this).saveBoolean(MapView.SP_IS_LOAD_INDOOR_MAP,false);
        LogUtil.d(TAG,"resetFlags end");
    }

    /**
     * 解决打包成系统级应用时webview崩溃问题
     */
    public void hookWebView() {
        LogUtil.d(TAG, "-----hookWebView-----");
        int sdkInt = Build.VERSION.SDK_INT;
        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
                LogUtil.d(TAG, "sProviderInstance isn't null");
                return;
            }
            Method getProviderClassMethod;
            if (sdkInt > 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
                LogUtil.d(TAG, "Don't need to Hook WebView");
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> providerClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> providerConstructor = providerClass.getConstructor(delegateClass);
            if (providerConstructor != null) {
                providerConstructor.setAccessible(true);
                Constructor<?> declaredConstructor = delegateClass.getDeclaredConstructor();
                declaredConstructor.setAccessible(true);
                sProviderInstance = providerConstructor.newInstance(declaredConstructor.newInstance());
                LogUtil.d(TAG, sProviderInstance.toString());
                field.set("sProviderInstance", sProviderInstance);
            }
            LogUtil.d(TAG, "Hook done!");
        } catch (Throwable e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }


    /**
     * 判断是否为主进程
     *
     * @return
     */
    private boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 在以下进程不需要初始化资源
     *
     * @return
     */
    private boolean filterProcess() {
        String bdMapProcess = "com.walktour.gui:remote";
        String xgProcess = "com.walktour.gui:xg_service_v3";
        List<String> filterProcess = new ArrayList<>();
        filterProcess.add(xgProcess);
        filterProcess.add(bdMapProcess);
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        int myPid = android.os.Process.myPid();
        for (RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && filterProcess.contains(info.processName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void registerOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        super.registerOnProvideAssistDataListener(callback);
    }

    /***
     * 整个业务测试是否存在分组,默认为关
     *
     * @return 是否存在
     */
    public static boolean isExitGroup() {
        if (ApplicationModel.getInstance().getSelectScene() == WalkStruct.SceneType.SingleSite) {
            return true;
        }
        return SharePreferencesUtil.getInstance(mContext).getBoolean(WalktourConst.SYS_SETTING_taskgroup_control, false);
    }

    /**
     * 写入配置文件
     * 用Assets_Writer写配置文件到手机目录/data/data/com.walktour.gui/files/config/
     * 如果文件已经存在，Assets_Writer不会写入
     */
    private void initConfig(Context mContext) {
        LogUtil.d(TAG, "---InitConfigFile start--");
        AppFilePathUtil.getInstance().createSDCardBaseDirectory(mContext.getString(R.string.path_config));
        String appConfigPath = AppFilePathUtil.getInstance().getAppConfigDirectory();
        if (SharePreferencesUtil.getInstance(mContext).getBoolean(KEY_ENV_FILE_INIT, false)) {
            LogUtil.d(TAG, "---InitConfigFile finish--");
            updateSDCardFiles(appConfigPath);
            return;
        }

        /**将assets dataset目录下的文件全部拷贝到/data/data/com.walktour.gui/files/config/目录下**/
        AssetsUtils.copyFilesFromAssets(mContext, "dataset", appConfigPath);
        /**将assets tasksetting目录下的文件全部拷贝到/data/data/com.walktour.gui/files/config/目录下**/
        AssetsUtils.copyFilesFromAssets(mContext, "tasksetting", appConfigPath);
        /**将assets config目录下的文件全部拷贝到/data/data/com.walktour.gui/files/config/目录下**/
        AssetsUtils.copyFilesFromAssets(mContext, "config", appConfigPath);
        /**将assets picture目录下的文件全部拷贝到/data/data/com.walktour.gui/files/config/目录下**/
        AssetsUtils.copyFilesFromAssets(mContext, "picture", appConfigPath);
        //root手机赋权限
        UtilsMethod.runRootCommand("chmod 777 " + appConfigPath + "*");

        //这个文件比较特殊
        String fileName = "script.spp";
        File file = new File(AppFilePathUtil.getInstance().getAppConfigDirectory() + fileName);
        File fileBack = AppFilePathUtil.getInstance().getAppLibFile(fileName);
        File fileBack2 = AppFilePathUtil.getInstance().getAppFilesFile(fileName);
        UtilsMethod.writeRawResource(mContext, file, fileBack, false); //需要特殊处理才能写入
        UtilsMethod.writeRawResource(mContext, file, fileBack2, false);
        SharePreferencesUtil.getInstance(mContext).saveBoolean(KEY_ENV_FILE_INIT, true);
        LogUtil.d(TAG, "---InitConfigFile end--");
        updateSDCardFiles(appConfigPath);
    }


    /***
     * VIVO手机需要通过如下方法刷新文件,电脑端才能看得到文件
     */
    public static void refreshFile() {
        if (Deviceinfo.getInstance().isVivo()) {
            File fx = new File(AppFilePathUtil.getInstance().getAppFilesDirectory(mContext.getString(R.string.path_data), mContext.getString(R.string.path_task)));
            if (fx.exists()) {
                File[] files = fx.listFiles();
                if (null != files && files.length > 0) {
                    LogUtil.w(TAG, "vivo refreshFile=" + fx.getAbsolutePath());
                    String absolutePath = files[0].getAbsolutePath();
                    final Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.parse("file://" + absolutePath));
                    mContext.sendBroadcast(intent);
                }
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        BluetoothPipeLine.getInstance().close();
    }

    /**
     * 更新SD卡配置文件到手机安装目录下
     */
    private void updateSDCardFiles(String appConfigPath){
        File file=AppFilePathUtil.getInstance().getSDCardBaseFile("assets");
        assetsFiles.clear();
        func(file);
        if(assetsFiles.size()>0){
            try {
                for (File f : assetsFiles) {
                    File fx = new File(appConfigPath + f.getName());
                    if (fx.exists()) {
                        fx.delete();
                        FileUtil.copyFile(f, fx);
                    }
                }
                //root手机赋权限
                UtilsMethod.runRootCommand("chmod 777 " + appConfigPath + "*");
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    /***
     * 遍历文件夹下的所有文件
     * @param file
     */
    private void func(File file){
        File[] fs = file.listFiles();
        if(null!=fs&&fs.length>0) {
            for (File f : fs) {
                if (f.isDirectory())
                    func(f);
                if (f.isFile()) {
                    assetsFiles.add(f);
                }
            }
        }
    }
}

