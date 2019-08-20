package com.walktour.service.metro;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import com.dingli.watcher.model.MetroGPS;
import com.walktour.Utils.EventBytes;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.gui.map.MapFactory;
import com.walktour.service.metro.model.MetroStation;

/**
 * 地铁线路测试服务类
 *
 * @author jianchao.wang
 */
public class MetroTestService extends Service implements SensorEventListener {
    /**
     * 日志标识
     */
    private static final String TAG = "MetroTestService";
    /**
     * 地铁线路GPS点更新广播类型
     */
    public static final String GPS_LOCATION_CHANGED = "com.walktour.service.metro.MetroTestService.gpsLocationChanged";
    /**
     * 获取加速度的间隔时间(毫秒)
     */
    private static final int ACCELERATION_INTERVAL_TIME = 500;
    /**
     * 地铁工厂类
     */
    private MetroFactory mFactory;
    /**
     * 感应器管理类
     */
    private SensorManager mSensorManager;
    /**
     * 模拟加速度数据的位置
     */
    private int mPost;
    /**
     * 当前任务是否自动打点
     */
    private boolean isAutoMark = false;
    /**
     * 写GPS线程
     */
    private WriteGPSThread mWriteGPSThread;
    /**
     * 传感器类
     */
    private Sensor mSensor;
    /**
     * 上次获取时间
     */
    private long mLastTime;

    /**
     * 设置当前的加速度
     *
     * @param acceleration 加速度
     */
    private void setAcceleration(float acceleration) {
        if (this.mLastTime > 0 && System.currentTimeMillis() - this.mLastTime < ACCELERATION_INTERVAL_TIME) {
            this.mPost++;
            return;
        }
        this.mLastTime = System.currentTimeMillis();
        if (this.mFactory.hasAutoTestData()) {
            acceleration = this.mFactory.getTestAcceleration(this.mPost++);
        } else {
            this.mFactory.saveAccelerationToFile(acceleration);
        }
        this.mFactory.setAcceleration(acceleration);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LogUtil.d(TAG, "-----onCreate-----");
        super.onCreate();
        this.mFactory = MetroFactory.getInstance(this);
        this.mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.mPost = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "-----onStartCommand-----");
        this.mPost = 0;
        this.mFactory.startTest();
        this.isAutoMark = MapFactory.getMapData().isAutoMark();
        if (this.isAutoMark && this.mSensor == null && !ConfigRoutine.getInstance().isHsExternalGPS(this)) {
            this.mSensor = this.mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            this.mSensorManager.registerListener(this, this.mSensor, 200 * 1000);
        }
        if (this.mWriteGPSThread == null) {
            this.mWriteGPSThread = new WriteGPSThread();
            this.mWriteGPSThread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "-----onDestroy-----");
        if (this.mSensor != null) {
            this.mSensorManager.unregisterListener(this, this.mSensor);
            this.mSensor = null;
        }
        if (this.mWriteGPSThread != null) {
            this.mWriteGPSThread.stopThread();
            this.mWriteGPSThread = null;
        }
        this.mFactory.stopTest();
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }
        switch (event.sensor.getType()) {
            case Sensor.TYPE_GRAVITY:
                this.setAcceleration(-event.values[0]);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 无须实现
    }

    /**
     * 写GPS数据线程
     *
     * @author jianchao.wang
     */
    private class WriteGPSThread extends Thread {
        /**
         * 是否停止线
         */
        private boolean isStop = false;
        /**
         * 获取当前经纬度的间隔时间
         */
        private static final int GPS_INTERVAL_TIME = 1000;
        /**
         * 最后一次获取的GPS
         */
        private MetroGPS mLastGPS = null;
        /**
         * 获取到的gps点数
         */
        private int count = 0;
        /**
         * 线程休眠时间
         */
        private int intervalTime = 0;

        @Override
        public void run() {
            while (!isStop) {
                if (count == 0) {
                    count = mFactory.getCurrentGPSCount();
                    LogUtil.d(TAG, "-----地铁count=" + count + "-----");
                    /**
                     *如果出现1个点以上的话，300毫秒写一次
                     */
                    if (count > 1)
                        intervalTime = 300;
                    else
                        intervalTime = GPS_INTERVAL_TIME;
                }
                if (count > 0) {

                    MetroGPS gps = mFactory.getCurrentGPS();
                    this.mLastGPS = gps;
                    Boolean isreach = this.checkIsStationGPS(gps);
                    this.write(gps);
                    count--;
                    if (this.mLastGPS != null) {
                        Intent intent = new Intent(GPS_LOCATION_CHANGED);
                        intent.putExtra("lon", this.mLastGPS.Lon);
                        intent.putExtra("lat", this.mLastGPS.Lat);
                        sendBroadcast(intent);
                    }
                }
                try {
                        Thread.sleep(intervalTime);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        /**
         * 判断是否是站点GPS，设置当前的下一个站点
         *
         * @param info gps信息
         */
        private boolean checkIsStationGPS(MetroGPS info) {
            if (info.Atype < 0)
                return false;
            if (info.Atype > 0) {
                MetroStation station = mFactory.getCurrentStation();
                if (station == null)
                    return false;
                if (info.Atype - 1 == station.getIndex()) {
                    info.Lon = station.getLatLng().longitude;
                    info.Lat = station.getLatLng().latitude;
                    mFactory.reachStation(false);
                    LogUtil.e(TAG,"已到站");
                    return true;
                }
            } else {// 如果当前收到第一个补点，且当前的站是已到站，则设置从当前站出发
                MetroStation station = mFactory.getCurrentStation();
                if (station.isReach()){
                    mFactory.startStation(false);
                    LogUtil.e(TAG,"已启动");
                }
            }
            return false;
        }

        /**
         * 写数据到文件
         *
         * @param gps 经纬度
         */
        private void write(MetroGPS gps) {
            if (gps.Atype < 0)
                return;
            LogUtil.d(TAG, "-----write-----lat:" + gps.Lat + "-----lon:" + gps.Lon+"------speed:"+gps.Speed);
            float altitude = 0;
            // 由于数据集的业务逻辑限制，带时间的的GPS点如果插入过密或者移动距离过大则会导致GPS点无效，当前的处理是使用了数据集的bug，以后根据需要再更改
            int secondTime = 0;
            int usecondTime = 0;
            int flag = 0x30002;
            EventBytes.Builder(getBaseContext()).addInteger(1).addInteger(secondTime).addInteger(usecondTime)
                    .addDouble(gps.Lat).addDouble(gps.Lon).addSingle(gps.Speed).addSingle(0).addSingle(altitude).addInteger(3)
                    .addSingle(0).writeGPSToRcu(flag);
//            MapEvent event = new MapEvent();
//            event.setLongitude(gps.Lon);
//            event.setLatitude(gps.Lat);
//            TraceInfoInterface.traceData.addGpsLocas(event);
            Intent intent = new Intent(GPS_LOCATION_CHANGED);
            intent.putExtra("lon", gps.Lon);
            intent.putExtra("lat", gps.Lat);
            sendBroadcast(intent);
        }

        /**
         * 停止测试
         */
        public void stopThread() {
            this.isStop = true;
            this.interrupt();
        }

    }

}
