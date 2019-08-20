package com.walktour.service.automark.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.walktour.service.automark.AutoMarkManager;
import com.walktour.service.automark.kalman.Kalman;
import com.walktour.service.automark.kalman.KalmanUtil;

/**
 * 陀螺仪传感器监听类
 *
 * @author jianchao.wang
 */
public class GyroscopeListener extends BaseListener {
    /**
     * 陀螺仪传感器
     */
    private Sensor mGyroscopeSensor;
    /**
     * 卡尔曼滤波算法计算类
     */
    private Kalman mKalman;
    /**
     * 上一次获取传感器数据的时间
     */
    private long mLastSensorTime = 0;
    /**
     * 将纳秒转化为秒
     */
    private static final float NS2S = 1.0f / 1000000000;
    /**
     * 当前的陀螺仪Z轴旋转弧度
     */
    private double mNowZRadian;

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_GYROSCOPE:
                this.calculateGyroscope(event);
                break;
        }
    }

    /**
     * 根据当前的角速度计算当前的旋转角度
     *
     * @param event 传感器参数
     */
    private void calculateGyroscope(SensorEvent event) {
        if (!AutoMarkManager.getInstance().isRunTest())
            return;
        if (this.mLastSensorTime == 0) {
            this.mLastSensorTime = event.timestamp;
            this.mKalman = KalmanUtil.createGyroscopeKalman();
            return;
        }
        // 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒
        final float dT = (event.timestamp - this.mLastSensorTime) * NS2S;
        // 将手机在各个轴上的旋转角度相加，即可得到当前位置相对于初始位置的旋转弧度
        float radian = event.values[2] * dT;
        double calRadian = this.mKalman.calculate(radian);
        this.mNowZRadian += calRadian;
        double degree = Math.toDegrees(this.mNowZRadian);
        AutoMarkManager.getInstance().setDeflectionDegree(degree);
//		long time = System.currentTimeMillis();
//		AutoMarkManager.getInstance().saveValuesToFile("gyroscope", time,
//				new double[] { event.timestamp, radian, calRadian, this.mNowZRadian });
        this.mLastSensorTime = event.timestamp;
    }

    @Override
    public void registerListener(SensorManager manager) {
        if (this.isRegister)
            return;
        this.init();
        this.mGyroscopeSensor = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        manager.registerListener(this, this.mGyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);
        this.isRegister = true;
    }

    @Override
    public void unregisterListener(SensorManager manager) {
        if (!this.isRegister)
            return;
        manager.unregisterListener(this, this.mGyroscopeSensor);
        this.mGyroscopeSensor = null;
        this.isRegister = false;
    }

    @Override
    protected void init() {
        this.mKalman = null;
        this.mLastSensorTime = 0;
        this.mNowZRadian = 0;

    }

}
