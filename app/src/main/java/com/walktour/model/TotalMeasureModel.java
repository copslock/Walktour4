package com.walktour.model;

/**
 * 统计测试参数对存储对象
 * @author tangwq
 *
 */
public class TotalMeasureModel {
	private long maxValue = -9999;
	private long minValue = -9999;
	private long keySum   = -9999;
	private long keyCounts= -9999;
	public TotalMeasureModel(){
		
	}
	public TotalMeasureModel(long value){
		maxValue = value;
		minValue = value;
		keySum = value;
		keyCounts = 1;
	}
	public long getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(long maxValue) {
		this.maxValue = maxValue;
	}
	public long getMinValue() {
		return minValue;
	}
	public void setMinValue(long minValue) {
		this.minValue = minValue;
	}
	public long getKeySum() {
		return keySum;
	}
	public void setKeySum(long keySum) {
		this.keySum = keySum;
	}
	public long getKeyCounts() {
		return keyCounts;
	}
	public void setKeyCounts(long keyCounts) {
		this.keyCounts = keyCounts;
	}

	@Override
	public String toString() {
		return "TotalMeasureModel{" +
				"maxValue=" + maxValue +
				", minValue=" + minValue +
				", keySum=" + keySum +
				", keyCounts=" + keyCounts +
				'}';
	}
}
