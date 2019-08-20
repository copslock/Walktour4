package com.walktour.service.automark.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.walktour.service.automark.AutoMarkManager;

/**
 * 大气压传感器
 * 
 * @author jianchao.wang
 *
 */
public class PressureListener extends BaseListener {

	/** 大气压传感器 */
	private Sensor mPressureSensor;
	/** 计算间隔时间（纳秒） */
	private final static int INTERAL_TIME = 1000 * 1000 * 1000;

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_PRESSURE:
			if (this.mLastCalcTime == 0 || event.timestamp - this.mLastCalcTime > INTERAL_TIME) {
				this.calculateHeight(event.values[0]);
				this.mLastCalcTime = event.timestamp;
			}
			break;
		}
	}

	/**
	 * 计算当前的海拔高度
	 * 
	 * @param pressure
	 *          当前压强
	 */
	private void calculateHeight(float pressure) {
		// 每升高9米，大气压下降100pa
		AutoMarkManager.getInstance().setHeight((int) ((AutoMarkManager.standardAtm - pressure) / 11.2 * 100.0));
	}

	@Override
	public void registerListener(SensorManager manager) {
		if (this.isRegister)
			return;
		this.mPressureSensor = manager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		if (this.mPressureSensor != null)
			manager.registerListener(this, this.mPressureSensor, 1000);
		this.init();
		this.isRegister = true;
	}

	@Override
	public void unregisterListener(SensorManager manager) {
		if (!this.isRegister)
			return;
		if (this.mPressureSensor != null)
			manager.unregisterListener(this, this.mPressureSensor);
		this.mPressureSensor = null;
		this.isRegister = false;
	}

	@Override
	protected void init() {
		// 无须实现

	}

}
