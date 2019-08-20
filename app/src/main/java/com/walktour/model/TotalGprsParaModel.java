package com.walktour.model;

public class TotalGprsParaModel {
	private int id;					//明细主键
	private int mainId;				//统计主表主键
	private int testType;			//测试类型	1,普通测试，2，室内测试	
	private String filePath="";		//文件路径           
	private int ulRLCThrMin;
	private int ulRLCThrMax;
	private int ulRLCThrSum;
	private int ulRLCThrCount;
	private int dlRLCThrMin;
	private int dlRLCThrMax;
	private int dlRLCThrSum;
	private int dlRLCThrCount;
	private int ulRLCRTXMin;
	private int ulRLCRTXMax;
	private int ulRLCRTXSum;
	private int ulRLCRTXCount;
	private int dlRLCRTXMin;
	private int dlRLCRTXMax;
	private int dlRLCRTXSum;
	private int dlRLCRTXCount;
	private int gprsBLERMin;
	private int gprsBLERMax;
	private int gprsBLERSum;
	private int gprsBLERCount;
	private int bepGMSKMin;
	private int bepGMSKMax;
	private int bepGMSKSum;
	private int bepGMSKCount;
	private int cvBepGMSKMin;
	private int cvBepGMSKMax;
	private int cvBepGMSKSum;
	private int cvBepGMSKCount;
	private int bep8PSKMin;
	private int bep8PSKMax;
	private int bep8PSKSum;
	private int bep8PSKCount;
	private int cvBep8PSKMin;
	private int cvBep8PSKMax;
	private int cvBep8PSKSum;
	private int cvBep8PSKCount;
	private int cvalueMin;
	private int cvalueMax;
	private int cvalueSum;
	private int cvalueCount;
	
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
	public synchronized int getUlRLCThrMin() {
		return ulRLCThrMin;
	}
	public synchronized void setUlRLCThrMin(int ulRLCThrMin) {
		this.ulRLCThrMin = ulRLCThrMin;
	}
	public synchronized int getUlRLCThrMax() {
		return ulRLCThrMax;
	}
	public synchronized void setUlRLCThrMax(int ulRLCThrMax) {
		this.ulRLCThrMax = ulRLCThrMax;
	}
	public synchronized int getUlRLCThrSum() {
		return ulRLCThrSum;
	}
	public synchronized void setUlRLCThrSum(int ulRLCThrSum) {
		this.ulRLCThrSum = ulRLCThrSum;
	}
	public synchronized int getUlRLCThrCount() {
		return ulRLCThrCount;
	}
	public synchronized void setUlRLCThrCount(int ulRLCThrCount) {
		this.ulRLCThrCount = ulRLCThrCount;
	}
	public synchronized int getDlRLCThrMin() {
		return dlRLCThrMin;
	}
	public synchronized void setDlRLCThrMin(int dlRLCThrMin) {
		this.dlRLCThrMin = dlRLCThrMin;
	}
	public synchronized int getDlRLCThrMax() {
		return dlRLCThrMax;
	}
	public synchronized void setDlRLCThrMax(int dlRLCThrMax) {
		this.dlRLCThrMax = dlRLCThrMax;
	}
	public synchronized int getDlRLCThrSum() {
		return dlRLCThrSum;
	}
	public synchronized void setDlRLCThrSum(int dlRLCThrSum) {
		this.dlRLCThrSum = dlRLCThrSum;
	}
	public synchronized int getDlRLCThrCount() {
		return dlRLCThrCount;
	}
	public synchronized void setDlRLCThrCount(int dlRLCThrCount) {
		this.dlRLCThrCount = dlRLCThrCount;
	}
	public synchronized int getUlRLCRTXMin() {
		return ulRLCRTXMin;
	}
	public synchronized void setUlRLCRTXMin(int ulRLCRTXMin) {
		this.ulRLCRTXMin = ulRLCRTXMin;
	}
	public synchronized int getUlRLCRTXMax() {
		return ulRLCRTXMax;
	}
	public synchronized void setUlRLCRTXMax(int ulRLCRTXMax) {
		this.ulRLCRTXMax = ulRLCRTXMax;
	}
	public synchronized int getUlRLCRTXSum() {
		return ulRLCRTXSum;
	}
	public synchronized void setUlRLCRTXSum(int ulRLCRTXSum) {
		this.ulRLCRTXSum = ulRLCRTXSum;
	}
	public synchronized int getUlRLCRTXCount() {
		return ulRLCRTXCount;
	}
	public synchronized void setUlRLCRTXCount(int ulRLCRTXCount) {
		this.ulRLCRTXCount = ulRLCRTXCount;
	}
	public synchronized int getDlRLCRTXMin() {
		return dlRLCRTXMin;
	}
	public synchronized void setDlRLCRTXMin(int dlRLCRTXMin) {
		this.dlRLCRTXMin = dlRLCRTXMin;
	}
	public synchronized int getDlRLCRTXMax() {
		return dlRLCRTXMax;
	}
	public synchronized void setDlRLCRTXMax(int dlRLCRTXMax) {
		this.dlRLCRTXMax = dlRLCRTXMax;
	}
	public synchronized int getDlRLCRTXSum() {
		return dlRLCRTXSum;
	}
	public synchronized void setDlRLCRTXSum(int dlRLCRTXSum) {
		this.dlRLCRTXSum = dlRLCRTXSum;
	}
	public synchronized int getDlRLCRTXCount() {
		return dlRLCRTXCount;
	}
	public synchronized void setDlRLCRTXCount(int dlRLCRTXCount) {
		this.dlRLCRTXCount = dlRLCRTXCount;
	}
	public synchronized int getGprsBLERMin() {
		return gprsBLERMin;
	}
	public synchronized void setGprsBLERMin(int gprsBLERMin) {
		this.gprsBLERMin = gprsBLERMin;
	}
	public synchronized int getGprsBLERMax() {
		return gprsBLERMax;
	}
	public synchronized void setGprsBLERMax(int gprsBLERMax) {
		this.gprsBLERMax = gprsBLERMax;
	}
	public synchronized int getGprsBLERSum() {
		return gprsBLERSum;
	}
	public synchronized void setGprsBLERSum(int gprsBLERSum) {
		this.gprsBLERSum = gprsBLERSum;
	}
	public synchronized int getGprsBLERCount() {
		return gprsBLERCount;
	}
	public synchronized void setGprsBLERCount(int gprsBLERCount) {
		this.gprsBLERCount = gprsBLERCount;
	}
	public synchronized int getBepGMSKMin() {
		return bepGMSKMin;
	}
	public synchronized void setBepGMSKMin(int bepGMSKMin) {
		this.bepGMSKMin = bepGMSKMin;
	}
	public synchronized int getBepGMSKMax() {
		return bepGMSKMax;
	}
	public synchronized void setBepGMSKMax(int bepGMSKMax) {
		this.bepGMSKMax = bepGMSKMax;
	}
	public synchronized int getBepGMSKSum() {
		return bepGMSKSum;
	}
	public synchronized void setBepGMSKSum(int bepGMSKSum) {
		this.bepGMSKSum = bepGMSKSum;
	}
	public synchronized int getBepGMSKCount() {
		return bepGMSKCount;
	}
	public synchronized void setBepGMSKCount(int bepGMSKCount) {
		this.bepGMSKCount = bepGMSKCount;
	}
	public synchronized int getCvBepGMSKMin() {
		return cvBepGMSKMin;
	}
	public synchronized void setCvBepGMSKMin(int cvBepGMSKMin) {
		this.cvBepGMSKMin = cvBepGMSKMin;
	}
	public synchronized int getCvBepGMSKMax() {
		return cvBepGMSKMax;
	}
	public synchronized void setCvBepGMSKMax(int cvBepGMSKMax) {
		this.cvBepGMSKMax = cvBepGMSKMax;
	}
	public synchronized int getCvBepGMSKSum() {
		return cvBepGMSKSum;
	}
	public synchronized void setCvBepGMSKSum(int cvBepGMSKSum) {
		this.cvBepGMSKSum = cvBepGMSKSum;
	}
	public synchronized int getCvBepGMSKCount() {
		return cvBepGMSKCount;
	}
	public synchronized void setCvBepGMSKCount(int cvBepGMSKCount) {
		this.cvBepGMSKCount = cvBepGMSKCount;
	}
	public synchronized int getBep8PSKMin() {
		return bep8PSKMin;
	}
	public synchronized void setBep8PSKMin(int bep8pskMin) {
		bep8PSKMin = bep8pskMin;
	}
	public synchronized int getBep8PSKMax() {
		return bep8PSKMax;
	}
	public synchronized void setBep8PSKMax(int bep8pskMax) {
		bep8PSKMax = bep8pskMax;
	}
	public synchronized int getBep8PSKSum() {
		return bep8PSKSum;
	}
	public synchronized void setBep8PSKSum(int bep8pskSum) {
		bep8PSKSum = bep8pskSum;
	}
	public synchronized int getBep8PSKCount() {
		return bep8PSKCount;
	}
	public synchronized void setBep8PSKCount(int bep8pskCount) {
		bep8PSKCount = bep8pskCount;
	}
	public synchronized int getCvBep8PSKMin() {
		return cvBep8PSKMin;
	}
	public synchronized void setCvBep8PSKMin(int cvBep8PSKMin) {
		this.cvBep8PSKMin = cvBep8PSKMin;
	}
	public synchronized int getCvBep8PSKMax() {
		return cvBep8PSKMax;
	}
	public synchronized void setCvBep8PSKMax(int cvBep8PSKMax) {
		this.cvBep8PSKMax = cvBep8PSKMax;
	}
	public synchronized int getCvBep8PSKSum() {
		return cvBep8PSKSum;
	}
	public synchronized void setCvBep8PSKSum(int cvBep8PSKSum) {
		this.cvBep8PSKSum = cvBep8PSKSum;
	}
	public synchronized int getCvBep8PSKCount() {
		return cvBep8PSKCount;
	}
	public synchronized void setCvBep8PSKCount(int cvBep8PSKCount) {
		this.cvBep8PSKCount = cvBep8PSKCount;
	}
	public synchronized int getCvalueMin() {
		return cvalueMin;
	}
	public synchronized void setCvalueMin(int cvalueMin) {
		this.cvalueMin = cvalueMin;
	}
	public synchronized int getCvalueMax() {
		return cvalueMax;
	}
	public synchronized void setCvalueMax(int cvalueMax) {
		this.cvalueMax = cvalueMax;
	}
	public synchronized int getCvalueSum() {
		return cvalueSum;
	}
	public synchronized void setCvalueSum(int cvalueSum) {
		this.cvalueSum = cvalueSum;
	}
	public synchronized int getCvalueCount() {
		return cvalueCount;
	}
	public synchronized void setCvalueCount(int cvalueCount) {
		this.cvalueCount = cvalueCount;
	}
	
}
