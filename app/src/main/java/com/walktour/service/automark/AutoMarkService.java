package com.walktour.service.automark;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.map.PointStatus;
import com.walktour.service.automark.listener.AccelerationListener;
import com.walktour.service.automark.listener.GyroscopeListener;
import com.walktour.service.automark.listener.OrientationListener;
import com.walktour.service.automark.listener.PressureListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 自动打点服务类
 *
 * @author jianchao.wang 2014年8月8日
 */
@SuppressLint("InlinedApi")
public class AutoMarkService extends Service {
    /**
     * 日志标识
     */
    private static final String TAG = "AutoMarkService";
    /**
     * 应用实例
     */
    private static ApplicationModel appModel = ApplicationModel.getInstance();
    /**
     * 数据管理对象
     */
    private DatasetManager mDatasetManager;
    /**
     * 传感器管理类
     */
    private SensorManager mManager;
    /**
     * 陀螺仪传感器监听类
     */
    private GyroscopeListener mGyroscopeListener;
    /**
     * 方向角传感器监听类
     */
    private OrientationListener mOrientationListener;
    /**
     * 加速度传感器监听类
     */
    private AccelerationListener mAccelerationListener;
    /**
     * 大气压传感器监听类
     */
    private PressureListener mPressureListener;
    /**
     * 写入打点线程
     */
    private WriteMarkThread mWriteMarkThread;
    /**
     * 上次获取的步数
     */
    private int mLastSteps;
    /**
     * 监控是否启动
     */
    private boolean isMonitorRun = false;
    /**
     * 加速度值文件的存放路径
     */
    private static String sPATH = "";
    /**
     * 写加速度值文件
     */
    private WriteFileThread mWriteFileThread;
    /**
     * 自动打点管理类
     */
    private AutoMarkManager mAutoMarkManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 广播监听类
     */
    public BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case WalkMessage.AUTOMARK_START_MARK:
                    startMonitor();
                    break;
                case WalkMessage.AUTOMARK_STOP_MARK:
                    stopMonitor();
                    break;
                case WalkMessage.AUTOMARK_FIRST_POINT:
                    Bundle bundle = intent.getExtras();
                    int x = bundle.getInt("x");
                    int y = bundle.getInt("y");
                    setFirstPoint(new Point(x, y));
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        this.stopMonitor();
        if (this.mAccelerationListener != null) {
            this.mAccelerationListener.unregisterListener(this.mManager);
            this.mAccelerationListener = null;
        }
        if (this.mGyroscopeListener != null) {
            this.mGyroscopeListener.unregisterListener(this.mManager);
            this.mGyroscopeListener = null;
        }
        if (this.mOrientationListener != null) {
            this.mOrientationListener.unregisterListener(this.mManager);
            this.mOrientationListener = null;
        }
        if (this.mPressureListener != null) {
            this.mPressureListener.unregisterListener(this.mManager);
            this.mPressureListener = null;
        }
        this.unregisterReceiver(myReceiver);
        if (this.mWriteFileThread != null) {
            this.mWriteFileThread.stopThread();
            this.mWriteFileThread = null;
        }
        super.onDestroy();
    }

    /**
     * 界面显示当前海拔高度
     */
    private void showBuildingHeight() {
        Intent intent = new Intent(WalkMessage.AUTOMARK_BUILDER_HEIGHT);
        intent.putExtra("buildingHeight", this.mAutoMarkManager.getHeight());
        sendBroadcast(intent);
    }

    /**
     * 设置起始点
     *
     * @param point 起始点坐标
     */
    public void setFirstPoint(Point point) {
        this.startMonitor();
        this.mAutoMarkManager.setFirstPoint(point);
    }

    /**
     * 启动自动打点监听线程
     */
    private void startMonitor() {
        if (this.isMonitorRun)
            return;
        LogUtil.d(TAG, "----startMonitor----");
        this.isMonitorRun = true;
        if (this.mOrientationListener != null) {
            this.mOrientationListener.unregisterListener(this.mManager);
            this.mOrientationListener = null;
        }
        if (this.mPressureListener != null) {
            this.mPressureListener.unregisterListener(this.mManager);
            this.mPressureListener = null;
        }
        this.mAutoMarkManager.init();
        if (this.mAccelerationListener != null)
            this.mAccelerationListener.registerListener(this.mManager);
        if (this.mGyroscopeListener != null)
            this.mGyroscopeListener.registerListener(this.mManager);
        this.mAutoMarkManager.setRunTest(true);
        this.mWriteMarkThread = new WriteMarkThread();
        this.mWriteMarkThread.start();
    }

    /**
     * 暂停监听
     */
    private void stopMonitor() {
        if (!this.isMonitorRun)
            return;
        LogUtil.d(TAG, "----stopMonitor----");
        if (this.mWriteMarkThread != null) {
            this.mWriteMarkThread.stopThread();
            this.mWriteMarkThread = null;
        }
        if (this.mAccelerationListener != null) {
            this.mAccelerationListener.unregisterListener(this.mManager);
        }
        if (this.mGyroscopeListener != null) {
            this.mGyroscopeListener.unregisterListener(this.mManager);
        }
        this.mAutoMarkManager.setRunTest(false);
        MapFactory.getMapData().setAutoMark(false);
        this.isMonitorRun = false;
    }

    /**
     * 写入打点数据的线程
     *
     * @author jianchao.wang
     */
    private class WriteMarkThread extends Thread {

        /**
         * 间隔时间
         */
        private static final int INTEVER_TIME = 1000;
        /**
         * 线程是否停止
         */
        private boolean isStop = false;

        @Override
        public void run() {
            while (!isStop && mAutoMarkManager.isRunTest()) {
                try {
                    sleep(INTEVER_TIME);
                    showOrientation();
                    showTotalSteps();
                    showBuildingHeight();
                    int moveSteps = mAutoMarkManager.getSteps() - mLastSteps;
                    if (moveSteps > AutoMarkManager.minMoveSteps) {
                        writeMarkPoint();
                        mLastSteps = mAutoMarkManager.getSteps();
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        /**
         * 中断线程
         */
        public void stopThread() {
            this.isStop = true;
            this.interrupt();
        }
    }

    /**
     * 界面显示当前的总步数
     */
    private void showTotalSteps() {
        Intent intent = new Intent(WalkMessage.AUTOMARK_TOTAL_STEPS);
        intent.putExtra("steps", this.mAutoMarkManager.getSteps());
        sendBroadcast(intent);
    }

    /**
     * 界面显示当前方向角
     */
    private void showOrientation() {
        Intent intent = new Intent(WalkMessage.AUTOMARK_ORIENTATION);
        intent.putExtra("orientation", (int) this.mAutoMarkManager.getCurrentOrientation());
        sendBroadcast(intent);
    }

    /**
     * 写入室内Mark点到RCU文件中
     */
    private void writeMarkPoint() {
        // 如果当前不在文件创建成功状态，不执行删点的动作
        if (!appModel.isRcuFileCreated()) {
            return;
        }
        PointStatus ps = new PointStatus();
        ps.setPoint(this.mAutoMarkManager.getShowPoint());
        ps.setStatus(PointStatus.POINT_STATUS_EFFECTIVE);
        MapFactory.getMapData().getPointStatusStack().push(ps);
        Bitmap map = MapFactory.getMapData().getMap();
        int height = (map == null ? this.getResources().getDisplayMetrics().heightPixels : map.getHeight());
        float scale = MapFactory.getMapData().getScale();
        Point lastPoint = this.mAutoMarkManager.getLastPoint();
        LogUtil.w(TAG, "---writeMarkPoint---height:" + height + "---scale:" + scale);
        StringBuffer mark = UtilsMethod.buildMarkStr(1, lastPoint.x / scale, (int) (height / scale) - lastPoint.y);
        LogUtil.w(TAG, "---write mark status:Add ---mark:" + mark);
        mDatasetManager.pushData(WalkCommonPara.MsgDataFlag_D, 0, 0, System.currentTimeMillis() * 1000,
                mark.toString().getBytes(), mark.length());
    }

    /**
     * 写加速度文件线程
     *
     * @author jianchao.wang
     */
    private class WriteFileThread extends Thread {
        /**
         * 是否停止执行
         */
        private boolean isStop = false;
        /**
         * 间隔时间
         */
        private static final int INTEVER_TIME = 1000;
        /**
         * 日期格式
         */
        private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());

        @Override
        public void run() {
            File file = new File(sPATH + File.separator + "acceleration");
            if (!file.exists()) {
                if (!file.mkdirs())
                    LogUtil.e(TAG, "----mkdirs error----");
            }
            file = new File(file.getAbsolutePath() + File.separator + mFormat.format(new Date()) + ".csv");
            if (file.exists()) {
                if (!file.delete())
                    LogUtil.e(TAG, "----delete file error----");
            }
            try {
                if (!file.createNewFile())
                    LogUtil.e(TAG, "----createNewFile error----");
            } catch (IOException e) {
                LogUtil.e(TAG, e.getMessage(), e);
            }
            BufferedWriter writer = null;
            FileWriter fileWriter = null;

            try {
                fileWriter = new FileWriter(file, true);
                writer = new BufferedWriter(fileWriter);
                while (!isStop) {
                    try {
                        Thread.sleep(INTEVER_TIME);
                    } catch (InterruptedException e) {
                        break;
                    }
                    while (!mAutoMarkManager.getWriteList().isEmpty()) {
                        writer.write(mAutoMarkManager.getWriteList().remove(0));
                        writer.newLine();
                        writer.flush();
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    if (fileWriter != null) {
                        fileWriter.close();
                    }
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                    LogUtil.e(TAG, e.getMessage(), e);
                }
            }
            super.run();
        }

        /**
         * 停止线程
         */
        public void stopThread() {
            this.isStop = true;
            this.interrupt();
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        sPATH = AppFilePathUtil.getInstance().getSDCardBaseDirectory();
        this.mDatasetManager = DatasetManager.getInstance(this.getApplicationContext());
        this.mManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.mAccelerationListener = new AccelerationListener();
        this.mGyroscopeListener = new GyroscopeListener();
        this.mPressureListener = new PressureListener();
        this.mAutoMarkManager = AutoMarkManager.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "----onStartCommand----");
        this.mPressureListener.registerListener(this.mManager);
        this.mOrientationListener = new OrientationListener();
        this.mOrientationListener.registerListener(this.mManager);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.AUTOMARK_START_MARK);
        filter.addAction(WalkMessage.AUTOMARK_STOP_MARK);
        filter.addAction(WalkMessage.AUTOMARK_FIRST_POINT);
        this.registerReceiver(myReceiver, filter);
        if (this.mWriteFileThread == null) {
            this.mWriteFileThread = new WriteFileThread();
            this.mWriteFileThread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

}
