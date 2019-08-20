package com.walktour.service.app;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.gui.R;
import com.walktour.gui.WalktourApplication;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * 日志服务，日志默认会存储在SDcar里如果没有SDcard会存储在内存中的安装目录下面。 1.本服务默认在SDcard中每天生成一个日志文件,
 * 2.如果有SDCard的话会将之前内存中的文件拷贝到SDCard中 3.如果没有SDCard，在安装目录下只保存当前在写日志
 * 4.SDcard的装载卸载动作会在步骤2,3中切换 5.SDcard中的日志文件只保存7天
 *
 * @author Administrator
 */
public class LogService extends Service {
    /**
     * 日志标识
     */
    private static final String TAG = "LogService";
    /**
     * 内存中日志文件最大值，10M
     */
    private static final int MEMORY_LOG_FILE_MAX_SIZE = 10 * 1000 * 1000;
    /**
     * 内存中的日志文件大小监控时间间隔，10分钟
     */
    private static final int MEMORY_LOG_FILE_MONITOR_INTERVAL = 10 * 60 * 1000;
    /**
     * sd卡中日志文件的最多保存天数
     */
    private static final int SDCARD_LOG_FILE_SAVE_DAYS = 7; //
    /**
     * 一天毫秒数
     */
    private static final long SDCARD_ONEDATE_MISTIME = 1000 * 60 * 60 * 24;
    /**
     * 日志文件在内存中的路径(日志文件在安装目录中的路径)
     */
    private String LOG_PATH_MEMORY_DIR;
    /**
     * 日志文件在sdcard中的路径
     */
    private String LOG_PATH_SDCARD_DIR;
    /**
     * 当前的日志记录类型:存储在SD卡下面
     */
    private static final int SDCARD_TYPE = 0;
    /**
     * 当前的日志记录类型:为存储在内存中
     */
    private static final int MEMORY_TYPE = 1;
    /**
     * 当前的日志记录类型
     */
    private int CURR_LOG_TYPE = SDCARD_TYPE;
    /**
     * 如果当前的日志写在内存中，记录当前的日志文件名称
     */
    private String CURR_INSTALL_LOG_NAME;
    /**
     * 本服务输出的日志文件名称
     */
    private static final String LOG_SERVICE_LOG_NAME = "log_service.log";
    /**
     * 日志日期格式
     */
    private SimpleDateFormat mLogFormat = new SimpleDateFormat("yy-MM-dd-HH:mm:ss", Locale.getDefault());
    /**
     * 本服务写入
     */
    private OutputStreamWriter mLogWriter;
    /**
     * 日志名称格式
     */
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.getDefault());
    /**
     * 日志输出进程
     */
    private Process mLogcatProcess;
    /**
     * 锁屏操作
     */
    private WakeLock mWakeLock;
    /**
     * SDcard状态监测
     */
    private SDCardStateMonitorReceiver mSDCardStateReceiver;
    /**
     * 日志任务监听
     */
    private LogTaskReceiver mLogTaskReceiver;
    /*
     * 是否正在监测日志文件大小； 如果当前日志记录在SDcard中则为false 如果当前日志记录在内存中则为true
     */
    private boolean isLogSizeMoniting = false;
    /**
     * 应用服务类
     */
    private ApplicationModel appModel;
    /**
     * 日志文件监测action
     */
    private static String MONITOR_LOG_SIZE_ACTION = "com.walktour.gui.MONITOR_LOG_SIZE";
    /**
     * 切换日志文件action
     */
    public static String SWITCH_LOG_FILE_ACTION = "com.walktour.gui.SWITCH_LOG_FILE_ACTION";
    /**
     * 时钟
     */
    private AlarmManager mAlarmManager;
    /**
     * ID标记
     */
    public static final String PI_ID_EXTRA = "id";
    /**
     * 每小时新建个文件,按小时保存log数据
     **/
    private static final int PI_ID_TASK_LISTER_ONE_HOUR = 1;
    /**
     * 每小时采集一次的定时器
     */
    private PendingIntent mOneHourTaskPandingIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.w(TAG, "--onCreate--");
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        ConfigRoutine routineSet = ConfigRoutine.getInstance();
        appModel = ApplicationModel.getInstance();
        try {
            mLogWriter = new OutputStreamWriter(new FileOutputStream(AppFilePathUtil.getInstance().createSDCardBaseFile("log", LOG_SERVICE_LOG_NAME), true));
        } catch (FileNotFoundException e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.ONLYAPPLOG = !routineSet.isSaveAllLog(getApplicationContext());
        recordLogServiceLog("---onCreate---");
        init();
        register();
        new LogCollectorThread(true).start();
    }

    /**
     * 初始化
     */
    @SuppressLint("InvalidWakeLockTag")
    private void init() {
        // APP应用路径
        LOG_PATH_MEMORY_DIR = AppFilePathUtil.getInstance().getAppFilesDirectory("log");
        LogUtil.i(TAG, "LOG_PATH_MEMORY_DIR IS " + LOG_PATH_MEMORY_DIR);
        // SD卡路径
        LOG_PATH_SDCARD_DIR = AppFilePathUtil.getInstance().getSDCardBaseDirectory(getString(R.string.path_log));
        LogUtil.i(TAG, "LOG_PATH_SDCARD_DIR IS " + LOG_PATH_SDCARD_DIR);
        // 创建保存日志路径
        createLogDir();

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        CURR_LOG_TYPE = getCurrLogType();
        mOneHourTaskPandingIntent = this.getPendingIntent(this.getApplicationContext(), PI_ID_TASK_LISTER_ONE_HOUR);
        doTaskOneHour();
    }

    /**
     * 获取 PendingIntent
     *
     * @param context 上下文
     * @param value   值
     * @return PendingIntent
     */
    private PendingIntent getPendingIntent(Context context, int value) {
        Intent intent = new Intent(context, LogService.class);
        intent.putExtra(PI_ID_EXTRA, value);
        return PendingIntent.getService(context, value, intent, 0);
    }

    /***
     * log采集：按每小时采集数据
     */
    private void doTaskOneHour() {
        if (null == mOneHourTaskPandingIntent) {
            LogUtil.w(TAG, "doTaskOneHour is null.");
            return;
        }
        long systemTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(systemTime);
        if(calendar.get(Calendar.HOUR_OF_DAY) == 23){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY,0);
        }else {
            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 选择的每天定时时间
        long selectTime = calendar.getTimeInMillis();
        LogUtil.d(TAG,"----doTaskOneHour----nextTime:"+mDateFormat.format(new Date(selectTime)));
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, selectTime, mOneHourTaskPandingIntent);
    }

    /**
     * 注册广播监听器
     */
    private void register() {
        IntentFilter sdCarMonitorFilter = new IntentFilter();
        sdCarMonitorFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        sdCarMonitorFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        sdCarMonitorFilter.addDataScheme("file");
        mSDCardStateReceiver = new SDCardStateMonitorReceiver();
        registerReceiver(mSDCardStateReceiver, sdCarMonitorFilter);

        IntentFilter logTaskFilter = new IntentFilter();
        logTaskFilter.addAction(MONITOR_LOG_SIZE_ACTION);
        logTaskFilter.addAction(SWITCH_LOG_FILE_ACTION);
        mLogTaskReceiver = new LogTaskReceiver();
        registerReceiver(mLogTaskReceiver, logTaskFilter);
    }

    /**
     * 获取当前应存储在内存中还是存储在SDCard中
     *
     * @return 当前的存储模式
     */
    public int getCurrLogType() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return MEMORY_TYPE;
        } else {
            return SDCARD_TYPE;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(null, flags, startId);
        }
        int id = intent.getIntExtra(PI_ID_EXTRA, 0);
        try {
            switch (id) {
                case PI_ID_TASK_LISTER_ONE_HOUR:
                    LogUtil.w(TAG, "--LogCollectorThread By OneHour--");
                    new LogCollectorThread(false).start();
                    doTaskOneHour();
                    break;
            }
        } catch (Exception ex) {
            LogUtil.w(TAG, ex.getMessage());
        }
        return super.onStartCommand(intent, flags, startId);
    }

   /**
     * 日志收集 1.清除日志缓存 2.杀死应用程序已开启的Logcat进程防止多个进程写入一个日志文件 3.开启日志收集进程 4.处理日志文件 移动
     * OR 删除
     */
    class LogCollectorThread extends Thread {

        private boolean isFirst;

        LogCollectorThread(boolean isFirst) {
            super("LogCollectorThread");
            this.isFirst = isFirst;
            LogUtil.d(TAG, "LogCollectorThread is create");
        }

        @Override
        @SuppressLint("Wakelock")
        public void run() {
            try {
                recordLogServiceLog("LogCollectorThread start");
                mWakeLock.acquire(5000); // 唤醒手机
                if (isFirst)
                    clearLogcat();
                createLogCollector();
                handleLog();
                mWakeLock.setReferenceCounted(false);
                mWakeLock.release(); // 释放
                recordLogServiceLog("LogCollectorThread end");
            } catch (Exception e) {
                e.printStackTrace();
                recordLogServiceLog(Log.getStackTraceString(e));
            }
        }

        public void stopThread() {

        }
    }

    /**
     * 清除日志信息
     */
    private void clearLogcat() {
        this.runCommand("logcat -c", false);
    }

    /**
     * 执行非root命令
     *
     * @param command  命令
     * @param isLogcat 是否开启logcat的命令
     */
    private void runCommand(String command, boolean isLogcat) {
        try {
            LogUtil.w(TAG, command);
            if (isLogcat) {
                this.mLogcatProcess = Runtime.getRuntime().exec(command);
                this.mLogcatProcess.waitFor();
            } else {
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
            }
        } catch (Exception e) {
            LogUtil.e("LogService-runCommand()", e.getMessage(), e);
        }
    }

    /**
     * 开始收集日志信息
     */
    public void createLogCollector() {
        String logPath = getLogPath();
        try {
            String cmd;
            if (LogUtil.ONLYAPPLOG) {
                cmd = "logcat -f " + logPath + " -v time -s DEBUG:V " + LogUtil.APPTAG + ":V&";
            } else {
                cmd = "logcat -f " + logPath + " -b main -b system -v time -s *:V&";
            }
            recordLogServiceLog("start collecting the log:" + logPath);
            LogUtil.w(TAG, "---createLogCollector---DeviceModel:" + android.os.Build.MODEL + "--Version:" + UtilsMethod.getCurrentVersionName(getApplicationContext()));
            this.closeLogcatProcess();
            runCommand(cmd, true);
        } catch (Exception e) {
            LogUtil.w(TAG, "CollectorThread == >" + e.getMessage(), e);
            recordLogServiceLog("CollectorThread == >" + e.getMessage());
        }
    }

    /**
     * 根据当前的存储位置得到日志的绝对存储路径
     *
     * @return 日志文件绝对路径
     */
    public String getLogPath() {
        createLogDir();
        String logFileName = mDateFormat.format(new Date()) + ".log";// 日志文件名称
        if (CURR_LOG_TYPE == MEMORY_TYPE) {
            CURR_INSTALL_LOG_NAME = logFileName;
            LogUtil.d(TAG, "Log stored in memory, the path is:" + LOG_PATH_MEMORY_DIR + logFileName);
            WalktourApplication.CURRENT_LOG_PATH = LOG_PATH_MEMORY_DIR + logFileName;
        } else {
            CURR_INSTALL_LOG_NAME = null;
            LogUtil.d(TAG, "Log stored in SDcard, the path is:" + LOG_PATH_SDCARD_DIR + logFileName);
            WalktourApplication.CURRENT_LOG_PATH = LOG_PATH_SDCARD_DIR + logFileName;
        }
        return WalktourApplication.CURRENT_LOG_PATH;
    }

    /**
     * 处理日志文件 1.如果日志文件存储位置切换到内存中，删除除了正在写的日志文件 并且部署日志大小监控任务，控制日志大小不超过规定值
     * 2.如果日志文件存储位置切换到SDCard中，删除7天之前的日志，移 动所有存储在内存中的日志到SDCard中，并将之前部署的日志大小 监控取消
     */
    public void handleLog() {
        if (CURR_LOG_TYPE == MEMORY_TYPE) {
            deployLogSizeMonitorTask();
            deleteMemoryExpiredLog();
        } else {
            moveLogfile();
            cancelLogSizeMonitorTask();
            deleteSDcardExpiredLog();
        }
    }

    /**
     * 部署日志大小监控任务
     */
    private void deployLogSizeMonitorTask() {
        if (isLogSizeMoniting) { // 如果当前正在监控着，则不需要继续部署
            return;
        }
        isLogSizeMoniting = true;
        Intent intent = new Intent(MONITOR_LOG_SIZE_ACTION);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), MEMORY_LOG_FILE_MONITOR_INTERVAL, sender);
        LogUtil.d(TAG, "deployLogSizeMonitorTask() succ !");
    }

    /**
     * 取消部署日志大小监控任务
     */
    private void cancelLogSizeMonitorTask() {
        isLogSizeMoniting = false;
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(MONITOR_LOG_SIZE_ACTION);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        am.cancel(sender);

        LogUtil.d(TAG, "canelLogSizeMonitorTask() succ");
    }

    /**
     * 检查日志文件大小是否超过了规定大小 如果超过了重新开启一个日志收集进程
     */
    private void checkLogSize() {
        if (CURR_INSTALL_LOG_NAME != null && !"".equals(CURR_INSTALL_LOG_NAME)) {
            String path = LOG_PATH_MEMORY_DIR + File.separator + CURR_INSTALL_LOG_NAME;
            File file = new File(path);
            if (!file.exists()) {
                return;
            }
            LogUtil.d(TAG, "checkLog() ==> The size of the log is too big?");
            if (file.length() >= MEMORY_LOG_FILE_MAX_SIZE) {
                LogUtil.d(TAG, "The log's size is too big!");
                new LogCollectorThread(false).start();
            }
        }
    }

    /**
     * 创建日志目录
     */
    private void createLogDir() {
        File file = new File(LOG_PATH_MEMORY_DIR);
        if (!file.isDirectory()) {
            if (!file.mkdirs())
                recordLogServiceLog("move file failed,dir is not created succ");
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = new File(LOG_PATH_SDCARD_DIR);
            if (!file.isDirectory()) {
                if (!file.mkdirs()) {
                    recordLogServiceLog("move file failed,dir is not created succ");
                }
            }
        }
    }

    /**
     * 将日志文件转移到SD卡下面
     */
    private void moveLogfile() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            recordLogServiceLog("move file failed, sd card does not mount");
            return;
        }
        File file = new File(LOG_PATH_SDCARD_DIR);
        if (!file.isDirectory()) {
            boolean mkOk = file.mkdirs();
            if (!mkOk) {
                recordLogServiceLog("move file failed,dir is not created succ");
                return;
            }
        }

        file = new File(LOG_PATH_MEMORY_DIR);
        if (file.isDirectory()) {
            File[] allFiles = file.listFiles();
            for (File logFile : allFiles) {
                String fileName = logFile.getName();
                if (LOG_SERVICE_LOG_NAME.equals(fileName)) {
                    continue;
                }
                // String createDateInfo = getFileNameWithoutExtension(fileName);
                boolean isSucc = copy(logFile, new File(LOG_PATH_SDCARD_DIR + File.separator + fileName));
                if (isSucc) {
                    if (logFile.delete())
                        LogUtil.d(TAG, "move file success,log name is:" + fileName);
                }
            }
        }
    }

    /**
     * 删除内存下过期的日志
     */
    private void deleteSDcardExpiredLog() {
        File file = new File(LOG_PATH_SDCARD_DIR);
        if (file.isDirectory()) {
            File[] allFiles = file.listFiles();
            for (File logFile : allFiles) {
                String fileName = logFile.getName();
                if (LOG_SERVICE_LOG_NAME.equals(fileName)) {
                    continue;
                }
                if (canDeleteSDLogByLastDate(logFile.lastModified())) {
                    if (logFile.delete())
                        LogUtil.d(TAG, "delete expired log success,the log path is:" + logFile.getAbsolutePath());

                }
            }
        }
    }

    /**
     * [当前日志文件是否可被删除]
     * [根据当前文件最后修改时间，判断当前文件是否需要删除]
     *
     * @param lastTime 最后修改时间
     * @return 是否可删除
     */

    public boolean canDeleteSDLogByLastDate(long lastTime) {
        return ((System.currentTimeMillis() - lastTime) > (SDCARD_ONEDATE_MISTIME * SDCARD_LOG_FILE_SAVE_DAYS));
    }

    /**
     * 删除内存中的过期日志，删除规则： 除了当前的日志和离当前时间最近的日志保存其他的都删除
     */
    private void deleteMemoryExpiredLog() {
        File directory = new File(LOG_PATH_MEMORY_DIR);
        if (directory.isDirectory()) {
            File[] allFiles = directory.listFiles();
            Arrays.sort(allFiles, new FileComparator());
            for (int i = 0; i < allFiles.length - 2; i++) { // "-2"保存最近的两个日志文件
                File file = allFiles[i];
                if (LOG_SERVICE_LOG_NAME.equals(file.getName()) || file.getName().equals(CURR_INSTALL_LOG_NAME)) {
                    continue;
                }
                if (file.delete())
                    LogUtil.d(TAG, "delete expired log success,the log path is:" + file.getAbsolutePath());

            }
        }
    }

    /**
     * 拷贝文件
     *
     * @param source 源文件
     * @param target 目标文件
     * @return 执行成功
     */
    private boolean copy(File source, File target) {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            if (!target.exists()) {
                boolean createSucc = target.createNewFile();
                if (!createSucc) {
                    return false;
                }
            }
            in = new FileInputStream(source);
            out = new FileOutputStream(target);
            byte[] buffer = new byte[8 * 1000];
            int count;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.w(TAG, e.getMessage(), e);
            recordLogServiceLog("copy file fail");
            return false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.w(TAG, e.getMessage(), e);
                recordLogServiceLog("copy file fail");
            }
        }

    }

    /**
     * 记录日志服务的基本信息 防止日志服务有错，在LogCat日志中无法查找 此日志名称为Log.log
     *
     * @param msg 日志信息
     */
    private void recordLogServiceLog(String msg) {
        LogUtil.d(TAG, "---recordLogServiceLog---mLogWriter:" + (mLogWriter != null) + "---msg:" + msg);
        if (mLogWriter != null) {
            try {
                Date time = new Date();
                mLogWriter.write(mLogFormat.format(time) + " : " + msg);
                mLogWriter.write("\n");
                mLogWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.w(TAG, e.getMessage(), e);
            }
        }
    }

    /**
     * 去除文件的扩展类型（.log）
     *
     * @param fileName 文件名
     * @return 文件名
     */
    private String getFileNameWithoutExtension(String fileName) {
        return fileName.substring(0, fileName.indexOf("."));
    }

    /**
     * 监控SD卡状态
     */
    private class SDCardStateMonitorReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {

            if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) { // 存储卡被卸载
                if (CURR_LOG_TYPE == SDCARD_TYPE) {
                    LogUtil.d(TAG, "SDcard is UNMOUNTED");
                    CURR_LOG_TYPE = MEMORY_TYPE;
                    new LogCollectorThread(false).start();
                }
            } else { // 存储卡被挂载
                if (CURR_LOG_TYPE == MEMORY_TYPE) {
                    LogUtil.d(TAG, "SDcard is MOUNTED");
                    CURR_LOG_TYPE = SDCARD_TYPE;
                    new LogCollectorThread(false).start();

                }
            }
        }
    }

    /**
     * 日志任务接收 切换日志，监控日志大小
     *
     * @author Administrator
     */
    private class LogTaskReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (SWITCH_LOG_FILE_ACTION.equals(action)) {
                LogUtil.w(TAG, "--LogCollectorThread:" + SWITCH_LOG_FILE_ACTION);
                new LogCollectorThread(false).start();
            } else if (MONITOR_LOG_SIZE_ACTION.equals(action)) {
                checkLogSize();
            }
        }
    }

    private class FileComparator implements Comparator<File> {
        public int compare(File file1, File file2) {
            if (LOG_SERVICE_LOG_NAME.equals(file1.getName())) {
                return -1;
            } else if (LOG_SERVICE_LOG_NAME.equals(file2.getName())) {
                return 1;
            }

            String createInfo1 = getFileNameWithoutExtension(file1.getName());
            String createInfo2 = getFileNameWithoutExtension(file2.getName());

            try {
                Date create1 = mDateFormat.parse(createInfo1);
                Date create2 = mDateFormat.parse(createInfo2);
                if (create1.before(create2)) {
                    return -1;
                } else {
                    return 1;
                }
            } catch (ParseException e) {
                return 0;
            }
        }
    }

    /**
     * 退出日志保存进程
     */
    public void closeLogcatProcess() {
        if (this.mLogcatProcess != null) {
            recordLogServiceLog("---closeLogcatProcess---");
            try {
                DataOutputStream os = new DataOutputStream(this.mLogcatProcess.getOutputStream());
                os.writeBytes("^C");
                os.flush();
                os.close();
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage(), e);
            }
            this.mLogcatProcess.destroy();
            this.mLogcatProcess = null;
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "---onDestroy---");
        this.closeLogcatProcess();
        if (null != mAlarmManager) {
            mAlarmManager.cancel(mOneHourTaskPandingIntent);
        }
        unregisterReceiver(mSDCardStateReceiver);
        unregisterReceiver(mLogTaskReceiver);
        LogUtil.d(TAG, "---onDestroy---end----");
        recordLogServiceLog("---onDestroy---");
        if (mLogWriter != null) {
            try {
                mLogWriter.close();
                mLogWriter = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
