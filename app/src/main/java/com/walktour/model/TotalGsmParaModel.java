package com.walktour.model;

public class TotalGsmParaModel {
	private int id;					//明细主键
	private int mainId;				//统计主表主键
	private int testType;			//测试类型	1,普通测试，2，室内测试	
	private String filePath="";		//文件路径           
	private int rxLevFullMin = -9999;
	private int rxLevFullMax = -9999;
	private int rxLevFullSum = -9999;
	private int rxLevFullCount;
	private int rxLevSubMin = -9999;
	private int rxLevSubMax = -9999;
	private int rxLevSubSum = -9999;
	private int rxLevSubCount;
	private int rxQualFullMin = -9999;
	private int rxQualFullMax = -9999;
	private int rxQualFullSum = -9999;
	private int rxQualFullCount;
	private int rxQualSubMin = -9999;
	private int rxQualSubMax = -9999;
	private int rxQualSubSum = -9999;
	private int rxQualSubCount;
	private int txPowerMin = -9999;
	private int txPowerMax = -9999;
	private int txPowerSum = -9999;
	private int txPowerCount;
	private int tAMin = -9999;
	private int tAMax = -9999;
	private int tASum = -9999;
	private int tACount;

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
	public synchronized int getRxLevFullMin() {
		return rxLevFullMin;
	}
	public synchronized void setRxLevFullMin(int rxLevFullMin) {
		this.rxLevFullMin = rxLevFullMin;
	}
	public synchronized int getRxLevFullMax() {
		return rxLevFullMax;
	}
	public synchronized void setRxLevFullMax(int rxLevFullMax) {
		this.rxLevFullMax = rxLevFullMax;
	}
	public synchronized int getRxLevFullSum() {
		return rxLevFullSum;
	}
	public synchronized void setRxLevFullSum(int rxLevFullSum) {
		this.rxLevFullSum = rxLevFullSum;
	}
	public synchronized int getRxLevFullCount() {
		return rxLevFullCount;
	}
	public synchronized void setRxLevFullCount(int rxLevFullCount) {
		this.rxLevFullCount = rxLevFullCount;
	}
	public synchronized int getRxLevSubMin() {
		return rxLevSubMin;
	}
	public synchronized void setRxLevSubMin(int rxLevSubMin) {
		this.rxLevSubMin = rxLevSubMin;
	}
	public synchronized int getRxLevSubMax() {
		return rxLevSubMax;
	}
	public synchronized void setRxLevSubMax(int rxLevSubMax) {
		this.rxLevSubMax = rxLevSubMax;
	}
	public synchronized int getRxLevSubSum() {
		return rxLevSubSum;
	}
	public synchronized void setRxLevSubSum(int rxLevSubSum) {
		this.rxLevSubSum = rxLevSubSum;
	}
	public synchronized int getRxLevSubCount() {
		return rxLevSubCount;
	}
	public synchronized void setRxLevSubCount(int rxLevSubCount) {
		this.rxLevSubCount = rxLevSubCount;
	}
	public synchronized int getRxQualFullMin() {
		return rxQualFullMin;
	}
	public synchronized void setRxQualFullMin(int rxQualFullMin) {
		this.rxQualFullMin = rxQualFullMin;
	}
	public synchronized int getRxQualFullMax() {
		return rxQualFullMax;
	}
	public synchronized void setRxQualFullMax(int rxQualFullMax) {
		this.rxQualFullMax = rxQualFullMax;
	}
	public synchronized int getRxQualFullSum() {
		return rxQualFullSum;
	}
	public synchronized void setRxQualFullSum(int rxQualFullSum) {
		this.rxQualFullSum = rxQualFullSum;
	}
	public synchronized int getRxQualFullCount() {
		return rxQualFullCount;
	}
	public synchronized void setRxQualFullCount(int rxQualFullCount) {
		this.rxQualFullCount = rxQualFullCount;
	}
	public synchronized int getRxQualSubMin() {
		return rxQualSubMin;
	}
	public synchronized void setRxQualSubMin(int rxQualSubMin) {
		this.rxQualSubMin = rxQualSubMin;
	}
	public synchronized int getRxQualSubMax() {
		return rxQualSubMax;
	}
	public synchronized void setRxQualSubMax(int rxQualSubMax) {
		this.rxQualSubMax = rxQualSubMax;
	}
	public synchronized int getRxQualSubSum() {
		return rxQualSubSum;
	}
	public synchronized void setRxQualSubSum(int rxQualSubSum) {
		this.rxQualSubSum = rxQualSubSum;
	}
	public synchronized int getRxQualSubCount() {
		return rxQualSubCount;
	}
	public synchronized void setRxQualSubCount(int rxQualSubCount) {
		this.rxQualSubCount = rxQualSubCount;
	}
	public synchronized int getTxPowerMin() {
		return txPowerMin;
	}
	public synchronized void setTxPowerMin(int txPowerMin) {
		this.txPowerMin = txPowerMin;
	}
	public synchronized int getTxPowerMax() {
		return txPowerMax;
	}
	public synchronized void setTxPowerMax(int txPowerMax) {
		this.txPowerMax = txPowerMax;
	}
	public synchronized int getTxPowerSum() {
		return txPowerSum;
	}
	public synchronized void setTxPowerSum(int txPowerSum) {
		this.txPowerSum = txPowerSum;
	}
	public synchronized int getTxPowerCount() {
		return txPowerCount;
	}
	public synchronized void setTxPowerCount(int txPowerCount) {
		this.txPowerCount = txPowerCount;
	}
	public synchronized int gettAMin() {
		return tAMin;
	}
	public synchronized void settAMin(int tAMin) {
		this.tAMin = tAMin;
	}
	public synchronized int gettAMax() {
		return tAMax;
	}
	public synchronized void settAMax(int tAMax) {
		this.tAMax = tAMax;
	}
	public synchronized int gettASum() {
		return tASum;
	}
	public synchronized void settASum(int tASum) {
		this.tASum = tASum;
	}
	public synchronized int gettACount() {
		return tACount;
	}
	public synchronized void settACount(int tACount) {
		this.tACount = tACount;
	}
	
}
