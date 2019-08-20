package com.walktour.model;

public class TotalDialModel {
	private int id;
	private int mainId;
	private int testType;		//测试类型1，普通测试；2室内测试
	private String filePath;	//文件绝对路径 当主键使用
	private int motrys;			//主叫尝试次数
	private int moconnects;		//主叫接通次数
	private int modropcalls;	//主叫掉话次数
	private int moblockcalls;	//主叫未接通次数
	private int modelaytimes;	//主叫时间统计次数
	private int mocalldelay;	//主叫时延
	private int morxlev1s;		//覆盖率1>=-90采样点数
	private int morxlev2s;		//覆盖率2>=-94采样点数
	private int mototalrxlevs;	//覆盖率总的采样点数
	private int morxqual1s;		//通话质量一级采样点	Rxqual中值为0,1,2
	private int morxqual2s;		//通话质量二级采样点 Rxqual中值为3,4,5
	private int mototalrxquals;	//通话质量总采样点 上面两种相加或者为Rxqual中值为0,1,2,3,4,5,6,7所有的值
	private int mttrys;			//被叫尝试次数
	private int mtconnects;		//被叫接通次数
	private int mtdropcalls;	//被叫掉话次数
	private int mtblockcalls;	//被叫未接通次数
	private int mtdelaytimes;	//被叫时间统计次娄
	private int mtcalldelay;	//被叫时延
	private int mtrxlev1s;		//覆盖率1>=-90采样点数
	private int mtrxlev2s;		//覆盖率2>=-94采样点数
	private int mttotalrxlevs;	//覆盖率总的采样点数
	private int mtrxqual1s;		//通话质量一级采样点	Rxqual中值为0,1,2
	private int mtrxqual2s;		//通话质量二级采样点 Rxqual中值为3,4,5
	private int mttotalrxquals;	//通话质量总采样点 上面两种相加或者为Rxqual中值为0,1,2,3,4,5,6,7所有的值
	
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
	
	public synchronized String getFilePath() {
		return filePath;
	}
	public synchronized void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public synchronized void setTestType(int testType) {
		this.testType = testType;
	}
	public synchronized int getMotrys() {
		return motrys;
	}
	public synchronized void setMotrys(int motrys) {
		this.motrys = motrys;
	}
	public synchronized int getMoconnects() {
		return moconnects;
	}
	public synchronized void setMoconnects(int moconnects) {
		this.moconnects = moconnects;
	}
	public synchronized int getModropcalls() {
		return modropcalls;
	}
	public synchronized void setModropcalls(int modropcalls) {
		this.modropcalls = modropcalls;
	}
	public synchronized int getMoblockcalls() {
		return moblockcalls;
	}
	public synchronized void setMoblockcalls(int moblockcalls) {
		this.moblockcalls = moblockcalls;
	}
	public synchronized int getMocalldelay() {
		return mocalldelay;
	}
	public synchronized void setMocalldelay(int mocalldelay) {
		this.mocalldelay = mocalldelay;
	}
	public synchronized int getMorxlev1s() {
		return morxlev1s;
	}
	public synchronized void setMorxlev1s(int morxlev1s) {
		this.morxlev1s = morxlev1s;
	}
	public synchronized int getMorxlev2s() {
		return morxlev2s;
	}
	public synchronized void setMorxlev2s(int morxlev2s) {
		this.morxlev2s = morxlev2s;
	}
	public synchronized int getMototalrxlevs() {
		return mototalrxlevs;
	}
	public synchronized void setMototalrxlevs(int mototalrxlevs) {
		this.mototalrxlevs = mototalrxlevs;
	}
	public synchronized int getMorxqual1s() {
		return morxqual1s;
	}
	public synchronized void setMorxqual1s(int morxqual1s) {
		this.morxqual1s = morxqual1s;
	}
	public synchronized int getMorxqual2s() {
		return morxqual2s;
	}
	public synchronized void setMorxqual2s(int morxqual2s) {
		this.morxqual2s = morxqual2s;
	}
	public synchronized int getMototalrxquals() {
		return mototalrxquals;
	}
	public synchronized void setMototalrxquals(int mototalrxquals) {
		this.mototalrxquals = mototalrxquals;
	}
	public synchronized int getMttrys() {
		return mttrys;
	}
	public synchronized void setMttrys(int mttrys) {
		this.mttrys = mttrys;
	}
	public synchronized int getMtconnects() {
		return mtconnects;
	}
	public synchronized void setMtconnects(int mtconnects) {
		this.mtconnects = mtconnects;
	}
	public synchronized int getMtdropcalls() {
		return mtdropcalls;
	}
	public synchronized void setMtdropcalls(int mtdropcalls) {
		this.mtdropcalls = mtdropcalls;
	}
	public synchronized int getMtblockcalls() {
		return mtblockcalls;
	}
	public synchronized void setMtblockcalls(int mtblockcalls) {
		this.mtblockcalls = mtblockcalls;
	}
	public synchronized int getMtcalldelay() {
		return mtcalldelay;
	}
	public synchronized void setMtcalldelay(int mtcalldelay) {
		this.mtcalldelay = mtcalldelay;
	}
	public synchronized int getMtrxlev1s() {
		return mtrxlev1s;
	}
	public synchronized void setMtrxlev1s(int mtrxlev1s) {
		this.mtrxlev1s = mtrxlev1s;
	}
	public synchronized int getMtrxlev2s() {
		return mtrxlev2s;
	}
	public synchronized void setMtrxlev2s(int mtrxlev2s) {
		this.mtrxlev2s = mtrxlev2s;
	}
	public synchronized int getMttotalrxlevs() {
		return mttotalrxlevs;
	}
	public synchronized void setMttotalrxlevs(int mttotalrxlevs) {
		this.mttotalrxlevs = mttotalrxlevs;
	}
	public synchronized int getMtrxqual1s() {
		return mtrxqual1s;
	}
	public synchronized void setMtrxqual1s(int mtrxqual1s) {
		this.mtrxqual1s = mtrxqual1s;
	}
	public synchronized int getMtrxqual2s() {
		return mtrxqual2s;
	}
	public synchronized void setMtrxqual2s(int mtrxqual2s) {
		this.mtrxqual2s = mtrxqual2s;
	}
	public synchronized int getMttotalrxquals() {
		return mttotalrxquals;
	}
	public synchronized void setMttotalrxquals(int mttotalrxquals) {
		this.mttotalrxquals = mttotalrxquals;
	}
	public synchronized int getModelaytimes() {
		return modelaytimes;
	}
	public synchronized void setModelaytimes(int modelaytimes) {
		this.modelaytimes = modelaytimes;
	}
	public synchronized int getMtdelaytimes() {
		return mtdelaytimes;
	}
	public synchronized void setMtdelaytimes(int mtdelaytimes) {
		this.mtdelaytimes = mtdelaytimes;
	}
}
