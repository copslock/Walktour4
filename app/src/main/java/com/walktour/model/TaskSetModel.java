package com.walktour.model;

public class TaskSetModel {
	private int outCycleTimes;		//外循环次数
	private int timeEnable;			//时间有效
	private String executiveDate;	//执行时间
	private String startTime;		//开始时间
	private String endTime;			//结果时间
	private int taskModelVersion=1;	//测试模版版本号
	
	public int getOutCycleTimes() {
		return outCycleTimes;
	}
	public void setOutCycleTimes(int outCycleTimes) {
		this.outCycleTimes = outCycleTimes;
	}
	public int getTimeEnable() {
		return timeEnable;
	}
	public void setTimeEnable(int timeEnable) {
		this.timeEnable = timeEnable;
	}
	public String getExecutiveDate() {
		return executiveDate;
	}
	public void setExecutiveDate(String executiveDate) {
		this.executiveDate = executiveDate;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getTaskModelVersion() {
		return taskModelVersion;
	}
	public void setTaskModelVersion(int taskModelVersion) {
		this.taskModelVersion = taskModelVersion;
	}
}
