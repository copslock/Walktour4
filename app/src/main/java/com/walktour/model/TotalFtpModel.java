package com.walktour.model;

public class TotalFtpModel {
	private int id;					//明细主键
	private int mainId;				//统计主表主键
	private int testType;			//测试类型	1,普通测试，2，室内测试	
	private String filePath="";			//文件路径           
	private int downtrys;			//下载偿试次数       
	private int downSuccs;			//下载成功次数       
	private int downDrops;			//下载掉线次数       
	private float downAverageThr;		//应用层平均下载速率 	
	private int downRLCThrs;		//RLC瞬时速率累加    	
	private int downRLCCount;		//RLC瞬时速率采样点数	
	private float downRLCBlers;		//下载时间内的总误块率	
	private int downRLCBlerClunt;	//时间内误块率采样点 	
	private int downAllTimes;		//下载总时长         	
	private float downTBFOpenTimes;	//下载时间内 TBFOpen总时长
	private float downTSCounts;		//时间内时隙总数1，2，3个时隙的累加
	private int downTSAllCounts;	//时间内时隙总数1，2，3，4，5，6，7，8个时隙的累加
	public synchronized int getDownTSAllCounts() {
		return downTSAllCounts;
	}
	public synchronized void setDownTSAllCounts(int downTSAllCounts) {
		this.downTSAllCounts = downTSAllCounts;
	}
	private int downMCCounts;		//下载时间内总包数   MC1*1+..MC9*9	
	private int downMCAllCount;		//总包数量           	
	private int uptrys;				//上传偿试次数       
	private int upSuccs;			//上传成功次数       
	private int upDrops;			//上传掉线次数       
	private float upAverageThr;		//应用层平均上传速率 	
	private int upRLCThrs;			//RLC瞬时速率累加    
	private int upRLCCount;			//RLC瞬时速率采样点数
	private int upAllTimes;			//下载总时长         
	private float upTBFOpenTimes;		//下载时间内 TBFOpen总时长
	private int upTSCounts;			//时间内时隙总数    1，2，3个时隙的累加 
	private int upTSAllCounts;		//时间内时隙总时长1，2，3，4，5，6，7，8
	private int upMCCounts;			//下载时间内总包数   MC1*1+..MC9*9
	private int upMCAllCount;		//总包数量    
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
	public synchronized int getDowntrys() {
		return downtrys;
	}
	public synchronized void setDowntrys(int downtrys) {
		this.downtrys = downtrys;
	}
	public synchronized int getDownSuccs() {
		return downSuccs;
	}
	public synchronized void setDownSuccs(int downSuccs) {
		this.downSuccs = downSuccs;
	}
	public synchronized int getDownDrops() {
		return downDrops;
	}
	public synchronized void setDownDrops(int downDrops) {
		this.downDrops = downDrops;
	}
	public synchronized float getDownAverageThr() {
		return downAverageThr;
	}
	public synchronized void setDownAverageThr(float downAverageThr) {
		this.downAverageThr = downAverageThr;
	}
	public synchronized int getDownRLCThrs() {
		return downRLCThrs;
	}
	public synchronized void setDownRLCThrs(int downRLCThrs) {
		this.downRLCThrs = downRLCThrs;
	}
	public synchronized int getDownRLCCount() {
		return downRLCCount;
	}
	public synchronized void setDownRLCCount(int downRLCCount) {
		this.downRLCCount = downRLCCount;
	}
	public synchronized float getDownRLCBlers() {
		return downRLCBlers;
	}
	public synchronized void setDownRLCBlers(float downRLCBlers) {
		this.downRLCBlers = downRLCBlers;
	}
	public synchronized int getDownRLCBlerClunt() {
		return downRLCBlerClunt;
	}
	public synchronized void setDownRLCBlerClunt(int downRLCBlerClunt) {
		this.downRLCBlerClunt = downRLCBlerClunt;
	}
	public synchronized int getDownAllTimes() {
		return downAllTimes;
	}
	public synchronized void setDownAllTimes(int downAllTimes) {
		this.downAllTimes = downAllTimes;
	}
	public synchronized float getDownTBFOpenTimes() {
		return downTBFOpenTimes;
	}
	public synchronized void setDownTBFOpenTimes(float downTBFOpenTimes) {
		this.downTBFOpenTimes = downTBFOpenTimes;
	}
	public synchronized float getDownTSCounts() {
		return downTSCounts;
	}
	public synchronized void setDownTSCounts(float downTSCounts) {
		this.downTSCounts = downTSCounts;
	}
	public synchronized int getDownMCCounts() {
		return downMCCounts;
	}
	public synchronized void setDownMCCounts(int downMCCounts) {
		this.downMCCounts = downMCCounts;
	}
	public synchronized int getDownMCAllCount() {
		return downMCAllCount;
	}
	public synchronized void setDownMCAllCount(int downMCAllCount) {
		this.downMCAllCount = downMCAllCount;
	}
	public synchronized int getUptrys() {
		return uptrys;
	}
	public synchronized void setUptrys(int uptrys) {
		this.uptrys = uptrys;
	}
	public synchronized int getUpSuccs() {
		return upSuccs;
	}
	public synchronized void setUpSuccs(int upSuccs) {
		this.upSuccs = upSuccs;
	}
	public synchronized int getUpDrops() {
		return upDrops;
	}
	public synchronized void setUpDrops(int upDrops) {
		this.upDrops = upDrops;
	}
	public synchronized float getUpAverageThr() {
		return upAverageThr;
	}
	public synchronized void setUpAverageThr(float upAverageThr) {
		this.upAverageThr = upAverageThr;
	}
	public synchronized int getUpRLCThrs() {
		return upRLCThrs;
	}
	public synchronized void setUpRLCThrs(int upRLCThrs) {
		this.upRLCThrs = upRLCThrs;
	}
	public synchronized int getUpRLCCount() {
		return upRLCCount;
	}
	public synchronized void setUpRLCCount(int upRLCCount) {
		this.upRLCCount = upRLCCount;
	}
	public synchronized int getUpAllTimes() {
		return upAllTimes;
	}
	public synchronized void setUpAllTimes(int upAllTimes) {
		this.upAllTimes = upAllTimes;
	}
	public synchronized float getUpTBFOpenTimes() {
		return upTBFOpenTimes;
	}
	public synchronized void setUpTBFOpenTimes(float upTBFOpenTimes) {
		this.upTBFOpenTimes = upTBFOpenTimes;
	}
	public synchronized int getUpTSCounts() {
		return upTSCounts;
	}
	public synchronized void setUpTSCounts(int upTSCounts) {
		this.upTSCounts = upTSCounts;
	}
	public synchronized int getUpMCCounts() {
		return upMCCounts;
	}
	public synchronized void setUpMCCounts(int upMCCounts) {
		this.upMCCounts = upMCCounts;
	}
	public synchronized int getUpMCAllCount() {
		return upMCAllCount;
	}
	public synchronized void setUpMCAllCount(int upMCAllCount) {
		this.upMCAllCount = upMCAllCount;
	}
	public synchronized int getUpTSAllCounts() {
		return upTSAllCounts;
	}
	public synchronized void setUpTSAllCounts(int upTSAllCounts) {
		this.upTSAllCounts = upTSAllCounts;
	}

}
