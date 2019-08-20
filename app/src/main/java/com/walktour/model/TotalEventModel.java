package com.walktour.model;

public class TotalEventModel {
	private int id;					//明细主键
	private int mainId;				//统计主表主键
	private int testType;			//测试类型	1,普通测试，2，室内测试	
	private String filePath="";		//文件路径           
	private int switchTimes;
	private int switchSuccs;
	private int lacTrys;
	private int lacSuccs;
	private int rauTrys;
	private int rauSuccs;
	private int rauIntervalSum;
	private int sectionReChooses;
	private int sectionReChoIntervalSum;
	
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
	public synchronized int getSwitchTimes() {
		return switchTimes;
	}
	public synchronized void setSwitchTimes(int switchTimes) {
		this.switchTimes = switchTimes;
	}
	public synchronized int getSwitchSuccs() {
		return switchSuccs;
	}
	public synchronized void setSwitchSuccs(int switchSuccs) {
		this.switchSuccs = switchSuccs;
	}
	public synchronized int getLacTrys() {
		return lacTrys;
	}
	public synchronized void setLacTrys(int lacTrys) {
		this.lacTrys = lacTrys;
	}
	public synchronized int getLacSuccs() {
		return lacSuccs;
	}
	public synchronized void setLacSuccs(int lacSuccs) {
		this.lacSuccs = lacSuccs;
	}
	public synchronized int getRauTrys() {
		return rauTrys;
	}
	public synchronized void setRauTrys(int rauTrys) {
		this.rauTrys = rauTrys;
	}
	public synchronized int getRauSuccs() {
		return rauSuccs;
	}
	public synchronized void setRauSuccs(int rauSuccs) {
		this.rauSuccs = rauSuccs;
	}
	public synchronized int getRauIntervalSum() {
		return rauIntervalSum;
	}
	public synchronized void setRauIntervalSum(int rauIntervalSum) {
		this.rauIntervalSum = rauIntervalSum;
	}
	public synchronized int getSectionReChooses() {
		return sectionReChooses;
	}
	public synchronized void setSectionReChooses(int sectionReChooses) {
		this.sectionReChooses = sectionReChooses;
	}
	public synchronized int getSectionReChoIntervalSum() {
		return sectionReChoIntervalSum;
	}
	public synchronized void setSectionReChoIntervalSum(int sectionReChoIntervalSum) {
		this.sectionReChoIntervalSum = sectionReChoIntervalSum;
	}
	
}
