package com.walktour.service.paramsreport.model;

/**
 * 上传参数对象
 * 
 * @author jianchao.wang
 *
 */
public class ParamsReportModel {
	/** 经度 */
	private double mLongitude;
	/** 纬度 */
	private double mLatitude;
	/** 采样时间 */
	private long mTime;
	/** 海拔高度 */
	private double mAltitude;
	/** 采样端口 */
	private int mPort;
	/** 要采集的参数ID数组 */
	private int[] mParamKeys;
	/** 解码出来的继承值[参数Key,解码出来的未经过缩放的值] */
	private double[] mInheritValues;
	/** 解码真实值[参数Key,解码出来的未经过缩放的值] */
	private double[] mRealValues;

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double longitude) {
		mLongitude = longitude;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double latitude) {
		mLatitude = latitude;
	}

	public long getTime() {
		return mTime;
	}

	public void setTime(long time) {
		mTime = time;
	}

	public double getAltitude() {
		return mAltitude;
	}

	public void setAltitude(double altitude) {
		mAltitude = altitude;
	}

	public int getPort() {
		return mPort;
	}

	public void setPort(int port) {
		mPort = port;
	}

	public int[] getParamKeys() {
		return mParamKeys;
	}

	public void setParamKeys(int[] paramKeys) {
		mParamKeys = paramKeys;
	}

	public double[] getInheritValues() {
		return mInheritValues;
	}

	public void setInheritValues(double[] inheritValues) {
		mInheritValues = inheritValues;
	}

	public double[] getRealValues() {
		return mRealValues;
	}

	public void setRealValues(double[] realValues) {
		mRealValues = realValues;
	}

}
