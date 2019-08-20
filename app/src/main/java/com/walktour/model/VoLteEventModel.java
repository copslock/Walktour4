package com.walktour.model;

/**
 * VoLTE事件Model封装类
 * 
 * @author zhihui.lian
 * 
 */
public class VoLteEventModel {
	private long time;				//时间
	private int pointIndex;			//采样点
	private int eventId;			//事件ID
	
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getPointIndex() {
		return pointIndex;
	}

	public void setPointIndex(int pointIndex) {
		this.pointIndex = pointIndex;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

}
