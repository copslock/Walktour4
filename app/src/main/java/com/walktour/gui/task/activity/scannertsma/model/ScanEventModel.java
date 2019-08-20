package com.walktour.gui.task.activity.scannertsma.model;

/**
 * 事件model类
 * @author zhihui.lian
 *
 */
public class ScanEventModel {
	
	private String  eventTime;
	
	private String eventInfo;

	public ScanEventModel(String eventTime, String eventInfo) {
		this.eventTime = eventTime;
		this.eventInfo = eventInfo;
	}
	
	
	public String getEventTime() {
		return eventTime;
	}

	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}

	public String getEventInfo() {
		return eventInfo;
	}

	public void setEventInfo(String eventInfo) {
		this.eventInfo = eventInfo;
	}

	
	
}
