package com.walktour.model;

public class StateInfoModel {
	/** 当前网络 */
	private String currentNet = "";
	/** 网络状态 */
	private String netState = "";
	/** 当前业务 */
	private String currentJon = "";
	/** 测试次数 */
	private String testTimes = "";
	/** 成功次数 */
	private String successTimes = "";
	/** 成功率 */
	private String successRate = "";
	/** 时延 */
	private String delay = "";
	/** 平均速率 */
	private String avgThrRate = "";
	/** 存储卡剩余空间 */
	private long surplusSize = 0L;
	/** 手机温度 */
	private String temperature = "";
	/** 手机CPU状态 */
	private String cpuState = "";
	/** 存储路径 */
	private String storagePath = "";
	/** 当前任务测试次数 */
	private String curTestTime = "";
	/** 当前任务总次数 */
	private String curAllTimes = "";
	/** 运行总时长 **/
	private long runTime = 0;
	/** log记录状态大小 **/
	private String logRecordSize = ""; // 这里直接显示可能需要转换单位。再确定看看
	/** log名字 **/
	private String logName = "";
	/** 当前任务的外循环总次数 */
	private String curAllCircles = "";
	/** 当前任务的外循环测试次数 */
	private String curTestCircles = "";

	public String getLogRecordSize() {
		return logRecordSize;
	}

	public void setLogRecordSize(String logRecordSize) {
		this.logRecordSize = logRecordSize;
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public long getRunTime() {
		return runTime;
	}

	public void setRunTime(long runTime) {
		this.runTime = runTime;
	}

	public String getCurrentNet() {
		return currentNet;
	}

	public void setCurrentNet(String currentNet) {
		this.currentNet = currentNet;
	}

	public String getNetState() {
		return netState;
	}

	public void setNetState(String netState) {
		this.netState = netState;
	}

	public String getCurrentJon() {
		if(currentJon.equals("EmptyTask"))
			return "IdleTask";
		else if(currentJon.equals("InitiativeCall"))
			return "MOC";
		else if(currentJon.equals("PassivityCall"))
			return "MTC";
		return currentJon;
//		return currentJon.equals("EmptyTask") ? "IdleTask" : currentJon;
	}

	public void setCurrentJon(String currentJon) {
		this.currentJon = currentJon;
	}

	public String getTestTimes() {
		return testTimes;
	}

	public void setTestTimes(String testTimes) {
		this.testTimes = testTimes;
	}

	public String getSuccessTimes() {
		return successTimes;
	}

	public void setSuccessTimes(String successTimes) {
		this.successTimes = successTimes;
	}

	public String getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(String successRate) {
		this.successRate = successRate;
	}

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	public String getAvgThrRate() {
		return avgThrRate;
	}

	public void setAvgThrRate(String avgThrRate) {
		this.avgThrRate = avgThrRate;
	}

	public long getSurplusSize() {
		return surplusSize;
	}

	public void setSurplusSize(long surplusSize) {
		this.surplusSize = surplusSize;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getCpuState() {
		return cpuState;
	}

	public void setCpuState(String cpuState) {
		this.cpuState = cpuState;
	}

	public String getStoragePath() {
		return storagePath;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	public String getCurTestTime() {
		return curTestTime;
	}

	public void setCurTestTime(String curTestTime) {
		this.curTestTime = curTestTime;
	}

	public String getCurAllTimes() {
		return curAllTimes;
	}

	public void setCurAllTimes(String curAllTimes) {
		this.curAllTimes = curAllTimes;
	}

	public String getCurAllCircles() {
		return curAllCircles;
	}

	public void setCurAllCircles(String curAllCircles) {
		this.curAllCircles = curAllCircles;
	}

	public String getCurTestCircles() {
		return curTestCircles;
	}

	public void setCurTestCircles(String curTestCircles) {
		this.curTestCircles = curTestCircles;
	}

}
