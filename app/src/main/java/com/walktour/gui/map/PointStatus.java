package com.walktour.gui.map;


import android.graphics.PointF;

import com.walktour.gui.newmap.model.MyLatLng;

/**
 * 采样点属性状态
 * 
 * @author jianchao.wang
 * 
 */
public class PointStatus {
	/** 点状态:预打点 */
	public static final int POINT_STATUS_PREVIOUS = -1;
	/** 点状态:实打点 */
	public static final int POINT_STATUS_EFFECTIVE = 0;
	/** 点状态:校准打点 */
	public static final int POINT_STATUS_CALIBRATION = 1;
	/** 点状态:预打点进行实打点之后的点 */
	public static final int POINT_STATUS_PREVIOUS_EFFECTIVE = 2;
	/** 点坐标 */
	private PointF point = new PointF();
	/** 点说明 */
	private String description;
	/** 点状态 */
	private int status = POINT_STATUS_CALIBRATION;
	/** 开始采样点 */
	private int beginPointIndex;
	/** 结束采样点 */
	private int endPointIndex;
	/** 点击时间 */
	private long pointTime;
	/** 经纬度 */
	private MyLatLng latLng;

	public MyLatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(MyLatLng latLng) {
		this.latLng = latLng;
	}

	public PointF getPoint() {
		return point;
	}

	public void setPoint(PointF point) {
		this.point = point;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getBeginPointIndex() {
		return beginPointIndex;
	}

	public void setBeginPointIndex(int beginPointIndex) {
		this.beginPointIndex = beginPointIndex;
	}

	public int getEndPointIndex() {
		return endPointIndex;
	}

	public void setEndPointIndex(int endPointIndex) {
		this.endPointIndex = endPointIndex;
	}

	public long getPointTime() {
		return pointTime;
	}

	public void setPointTime(long pointTime) {
		this.pointTime = pointTime;
	}

	@Override
	public String toString() {
		return "PointStatus{" +
				"point=" + point +
				", description='" + description + '\'' +
				", status=" + status +
				", beginPointIndex=" + beginPointIndex +
				", endPointIndex=" + endPointIndex +
				", pointTime=" + pointTime +
				", latLng=" + latLng +
				'}';
	}
}
