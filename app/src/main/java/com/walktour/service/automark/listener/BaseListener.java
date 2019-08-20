package com.walktour.service.automark.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 传感器监听基础类
 *
 * @author jianchao.wang
 */
public abstract class BaseListener implements SensorEventListener {

    /**
     * 上一次的计算时间
     */
    long mLastCalcTime;
    /**
     * 是否当前监听已经注册
     */
    boolean isRegister = false;

    @Override
    public abstract void onSensorChanged(SensorEvent event);

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 无须实现

    }

    /**
     * 注册监听器
     *
     * @param manager 管理类
     */
    public abstract void registerListener(SensorManager manager);

    /**
     * 注销监听器
     *
     * @param manager 管理类
     */
    public abstract void unregisterListener(SensorManager manager);

    /**
     * 初始化值
     */
    protected abstract void init();

}
