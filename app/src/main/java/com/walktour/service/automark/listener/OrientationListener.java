package com.walktour.service.automark.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.walktour.service.automark.AutoMarkManager;

/**
 * 方向传感器监听类
 *
 * @author jianchao.wang
 */
public class OrientationListener extends BaseListener {
    /**
     * 重力加速度值
     */
    private float[] mGravityValues;
    /**
     * 磁力值
     */
    private float[] mMagneticValues;
    /**
     * 重力传感器
     */
    private Sensor mGravitySensor;
    /**
     * 磁力传感器
     */
    private Sensor mMagneticSensor;
    /**
     * 计算间隔时间（纳秒）
     */
    private final static int INTERAL_TIME = 20 * 1000 * 1000;

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                this.mMagneticValues = event.values;
                if (this.mLastCalcTime == 0 || event.timestamp - this.mLastCalcTime > INTERAL_TIME) {
                    this.calculateOrientation();
                    this.mLastCalcTime = event.timestamp;
                }
                break;
            case Sensor.TYPE_GRAVITY:
                this.mGravityValues = event.values;
                if (this.mLastCalcTime == 0 || event.timestamp - this.mLastCalcTime > INTERAL_TIME) {
                    this.calculateOrientation();
                    this.mLastCalcTime = event.timestamp;
                }
                break;
        }
    }

    @Override
    public void registerListener(SensorManager manager) {
        if (this.isRegister)
            return;
        this.init();
        this.mGravitySensor = manager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        this.mMagneticSensor = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        manager.registerListener(this, this.mGravitySensor, 1000);
        manager.registerListener(this, this.mMagneticSensor, 1000);
        this.isRegister = true;
    }

    @Override
    public void unregisterListener(SensorManager manager) {
        if (!this.isRegister)
            return;
        manager.unregisterListener(this, this.mGravitySensor);
        manager.unregisterListener(this, this.mMagneticSensor);
        this.mGravitySensor = null;
        this.mMagneticSensor = null;
        this.isRegister = false;
    }

    /**
     * 计算最开始的方向度
     */
    private void calculateOrientation() {
        if (this.mGravityValues == null || this.mMagneticValues == null)
            return;
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, mGravityValues, mMagneticValues);
        SensorManager.getOrientation(R, values);
        // 要经过一次数据格式的转换，转换为度
        double orientation = Math.toDegrees(values[0]);
        if (orientation < 0)
            orientation += 360;
        AutoMarkManager.getInstance().setFirstOrientation(orientation);
    }

    @Override
    protected void init() {
        this.mGravityValues = null;
        this.mMagneticValues = null;
    }

}
