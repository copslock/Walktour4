package com.walktour.service.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import com.walktour.base.util.LogUtil;
import com.walktour.gui.map.MapView;
import com.walktour.gui.map.PointStatus;

import java.util.Stack;

/***
 * 记录运动轨迹服务类
 * ***/
public class RecordTraceService extends Service implements SensorEventListener {

    private SensorManager sm;
    public static String ACTION_TRACE_POINT_UPDATE = "ACTION_TRACE_POINT_UPDATE";//通知服务调用方取点的广播
    //	private static final String tag = "RecordTraceService";
    private float azimuth = 0;//手机Y方向与地理正北方向的夹角
    private float baseAzimuth = 0;//第一次探测的方位
    private float diffAzimuth = 0;//本次测得的角度与上次的差值
    private boolean hasInitialized = false;//第一次角度时候已经初始化
    private static float distancePerFoot = 0.25f;//行走一步大概的距离估算
    public static Stack<Point> allPoint;//记录位置的栈
    public static float startAzimuth = 0;//打点确定的方向与X正方向的夹角
    private static boolean hasAdjusted = false;//是否已经校准

    //	private static float realWidth = 10;
//	private static float screenWidth = 480;
    private static float rate = 5;//每走一步按5个像素点来算

    //设置是否校准，如果校准后系统在传感器值改变后将正式计算行走路线，并通知上层接受数据，未校准，则不会有动作
    public static void setAdjust(boolean result) {
        hasAdjusted = result;
    }

    public static boolean isHasAdjusted() {
        return hasAdjusted;
    }

    public static void setStartDirection(float angle) {
        startAzimuth = angle;
    }

    public static Stack<PointStatus> getPointstatus() {
        Stack<PointStatus> psStack = new Stack<PointStatus>();
        for (Point p : allPoint) {
            PointStatus ps = new PointStatus();
            //利用起始点加偏移位置计算出该点在屏幕上的位置
            ps.getPoint().x = MapView.getFirstPoint().getPoint().x - (int) (p.x * rate);
            ps.getPoint().y = MapView.getFirstPoint().getPoint().y - (int) (p.y * rate);
            ps.setStatus(PointStatus.POINT_STATUS_EFFECTIVE);
            psStack.add(ps);
        }
        return psStack;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /***
     * 服务第一次建立时调用
     * ***/
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {
        super.onCreate();
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                , SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION)
                , SensorManager.SENSOR_DELAY_NORMAL);
        if (allPoint == null) {
            allPoint = new Stack<Point>();
        }
        LogUtil.w("Service", "onCreate");
    }

    /***
     * 服务销毁时调用
     * ***/
    @Override
    public void onDestroy() {
        super.onDestroy();
        sm.unregisterListener(this);
        allPoint.clear();
//		hasAdjusted = false;
        hasInitialized = false;
    }

    /***
     * 传感器精度变化时调用
     * ***/
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /***
     * 传感器值变化时调用
     * ***/
    @SuppressWarnings("deprecation")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!hasAdjusted) {
            return;
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float accZ = event.values[2];
            //Z方向加速度达到一定数值时，认为震动一次
            if (accZ > 11.0) {
                if (allPoint == null) {
                    allPoint = new Stack<Point>();
                }
                //取最近一次的点的位置
                Point prePoint;
                if (allPoint.size() > 0) {
                    prePoint = allPoint.get(allPoint.size() - 1);
                } else {
                    prePoint = new Point();
                    prePoint.x = 0;
                    prePoint.y = 0;
                }
                //根据上一点的位置和现有角度，构造新的位置
                Point curPoint = new Point();
                curPoint.x = (int) (prePoint.x + 20 * distancePerFoot * Math.cos(Math.toRadians(diffAzimuth)));
                curPoint.y = (int) (prePoint.y + 20 * distancePerFoot * Math.sin(Math.toRadians(diffAzimuth)));
                //讲新点加入栈中
                allPoint.push(curPoint);
                //发送广播通知调用方取点栈
                Intent intent = new Intent();
                intent.setAction(ACTION_TRACE_POINT_UPDATE);
                sendBroadcast(intent);
//				LogUtil.w(tag, "-----curPoint.x="+curPoint.x+" curPoint.y="+curPoint.y);
            }
        }
        //或得Y轴与正北的方向角
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            azimuth = event.values[0];
            if (hasInitialized) {
                diffAzimuth = azimuth - baseAzimuth - startAzimuth;
            } else {
                hasInitialized = true;
                baseAzimuth = azimuth;
                diffAzimuth = 0 - startAzimuth;
            }
//			LogUtil.w(tag, "----diffAzimuth="+diffAzimuth);
        }
    }

}
