package com.walktour.model;

public class TotalWapModel {
	private int id;					//明细主键
	private int mainId;				//统计主表主键
	private int testType;			//测试类型	1,普通测试，2，室内测试	
	private String filePath="";		//文件路径           
	private int loginTrys;
	private int logingSuccs;
	private int loginDelay;
	private int refreshTrys;
	private int refreshSuccs;
	private int refreshDelay;
	private int downTrys;
	private int downSuccs;
	private float downSpeed;
	public synchronized int getId() {
		return id;
	}
	public synchronized void setId(int id) {
		this.id = id;
	}
	public synchronized int getMainId() {
		return mainId;
	}
	public synchronized void setMainId(int mainId) {
		this.mainId = mainId;
	}
	public synchronized int getTestType() {
		return testType;
	}
	public synchronized void setTestType(int testType) {
		this.testType = testType;
	}
	public synchronized String getFilePath() {
		return filePath;
	}
	public synchronized void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public synchronized int getLoginTrys() {
		return loginTrys;
	}
	public synchronized void setLoginTrys(int loginTrys) {
		this.loginTrys = loginTrys;
	}
	public synchronized int getLogingSuccs() {
		return logingSuccs;
	}
	public synchronized void setLogingSuccs(int logingSuccs) {
		this.logingSuccs = logingSuccs;
	}
	public synchronized int getLoginDelay() {
		return loginDelay;
	}
	public synchronized void setLoginDelay(int loginDelay) {
		this.loginDelay = loginDelay;
	}
	public synchronized int getRefreshTrys() {
		return refreshTrys;
	}
	public synchronized void setRefreshTrys(int refreshTrys) {
		this.refreshTrys = refreshTrys;
	}
	public synchronized int getRefreshSuccs() {
		return refreshSuccs;
	}
	public synchronized void setRefreshSuccs(int refreshSuccs) {
		this.refreshSuccs = refreshSuccs;
	}
	public synchronized int getRefreshDelay() {
		return refreshDelay;
	}
	public synchronized void setRefreshDelay(int refreshDelay) {
		this.refreshDelay = refreshDelay;
	}
	public synchronized int getDownTrys() {
		return downTrys;
	}
	public synchronized void setDownTrys(int downTrys) {
		this.downTrys = downTrys;
	}
	public synchronized int getDownSuccs() {
		return downSuccs;
	}
	public synchronized void setDownSuccs(int downSuccs) {
		this.downSuccs = downSuccs;
	}
	public synchronized float getDownSpeed() {
		return downSpeed;
	}
	public synchronized void setDownSpeed(float downSpeed) {
		this.downSpeed = downSpeed;
	}
	
	
}
