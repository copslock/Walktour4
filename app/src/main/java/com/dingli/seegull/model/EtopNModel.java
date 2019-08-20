package com.dingli.seegull.model;

import java.util.ArrayList;

/**
 * EtopNModel任务配置model
 * @author zhihui.lian
 */
public class EtopNModel extends ScanTaskModel implements Cloneable{

	//style默认为6 对应带宽
	
	private ArrayList<ChannelModel> channelList = new ArrayList<ChannelModel>();
	
	private double carrierRssiThreshold;
	private int numberOfSignals;
	private int numberOfTxAntennaPorts;
	private int numberOfRxAntennaPorts;   //isMIMO  是为2  否为1
	private int cyclicPrefix;
	
	private double measurementThreshold;
	private int operationalMode;
	private int wideBand;				//对应界面上的数据模式DATA_MODE				
	
	private double syncMeasurementThreshold;
	private int syncOperationalMode;
	private int syncWideBand;					//13有层三SCH DB  14无层三SCH_MIB_SIB DB
	
	private double refMeasurementThreshold;
	private int refOperationalMode;
	private int refWideBand;					//界面上需要呈现    要根据protocol来判断 TDD25起 FDD18起
	
	private int subBandStart;					//0
	private int numberOfSubBands;				//4
	private int subBandSize;					//4	
 
	private boolean isPscOrPn;				//界面用参数
	
	private String pscStr;					//界面用参数
	
	private int fddORTdd;					//0 FDD  1 TDD
	

	public ArrayList<ChannelModel> getChannelList() {
		return channelList;
	}

	public void setChannelList(ArrayList<ChannelModel> channelList) {
		this.channelList = channelList;
	}

	public double getCarrierRssiThreshold() {
		return carrierRssiThreshold;
	}

	public void setCarrierRssiThreshold(double carrierRssiThreshold) {
		this.carrierRssiThreshold = carrierRssiThreshold;
	}

	public int getNumberOfSignals() {
		return numberOfSignals;
	}

	public void setNumberOfSignals(int numberOfSignals) {
		this.numberOfSignals = numberOfSignals;
	}

	public int getNumberOfTxAntennaPorts() {
		return numberOfTxAntennaPorts;
	}

	public void setNumberOfTxAntennaPorts(int numberOfTxAntennaPorts) {
		this.numberOfTxAntennaPorts = numberOfTxAntennaPorts;
	}

	public int getNumberOfRxAntennaPorts() {
		return numberOfRxAntennaPorts;
	}

	public void setNumberOfRxAntennaPorts(int numberOfRxAntennaPorts) {
		this.numberOfRxAntennaPorts = numberOfRxAntennaPorts;
	}

	public int getCyclicPrefix() {
		return cyclicPrefix;
	}

	public void setCyclicPrefix(int cyclicPrefix) {
		this.cyclicPrefix = cyclicPrefix;
	}

	public double getMeasurementThreshold() {
		return measurementThreshold;
	}

	public void setMeasurementThreshold(double measurementThreshold) {
		this.measurementThreshold = measurementThreshold;
	}

	public int getOperationalMode() {
		return operationalMode;
	}

	public void setOperationalMode(int operationalMode) {
		this.operationalMode = operationalMode;
	}

	public int getWideBand() {
		return wideBand;
	}

	public void setWideBand(int wideBand) {
		this.wideBand = wideBand;
	}

	public double getSyncMeasurementThreshold() {
		return syncMeasurementThreshold;
	}

	public void setSyncMeasurementThreshold(double syncMeasurementThreshold) {
		this.syncMeasurementThreshold = syncMeasurementThreshold;
	}

	public int getSyncOperationalMode() {
		return syncOperationalMode;
	}

	public void setSyncOperationalMode(int syncOperationalMode) {
		this.syncOperationalMode = syncOperationalMode;
	}

	public int getSyncWideBand() {
		return syncWideBand;
	}

	public void setSyncWideBand(int syncWideBand) {
		this.syncWideBand = syncWideBand;
	}

	public double getRefMeasurementThreshold() {
		return refMeasurementThreshold;
	}

	public void setRefMeasurementThreshold(double refMeasurementThreshold) {
		this.refMeasurementThreshold = refMeasurementThreshold;
	}

	public int getRefOperationalMode() {
		return refOperationalMode;
	}

	public void setRefOperationalMode(int refOperationalMode) {
		this.refOperationalMode = refOperationalMode;
	}

	public int getRefWideBand() {
		return refWideBand;
	}

	public void setRefWideBand(int refWideBand) {
		this.refWideBand = refWideBand;
	}

	public int getSubBandStart() {
		return subBandStart;
	}

	public void setSubBandStart(int subBandStart) {
		this.subBandStart = subBandStart;
	}

	public int getNumberOfSubBands() {
		return numberOfSubBands;
	}

	public void setNumberOfSubBands(int numberOfSubBands) {
		this.numberOfSubBands = numberOfSubBands;
	}

	public int getSubBandSize() {
		return subBandSize;
	}

	public void setSubBandSize(int subBandSize) {
		this.subBandSize = subBandSize;
	}

	public boolean isPscOrPn() {
		return isPscOrPn;
	}

	public void setPscOrPn(boolean isPscOrPn) {
		this.isPscOrPn = isPscOrPn;
	}

	public String getPscStr() {
		return pscStr;
	}

	public void setPscStr(String pscStr) {
		this.pscStr = pscStr;
	}
	
	public int getFddORTdd() {
		return fddORTdd;
	}

	public void setFddORTdd(int fddORTdd) {
		this.fddORTdd = fddORTdd;
	}
	
	/**
	 * 对象浅克隆
	 */
	@Override
	public EtopNModel clone(){
		EtopNModel eTopNModel = null;
		try {
			eTopNModel = (EtopNModel) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return eTopNModel;
	}
	
	@Override
	public void initChannelList(ArrayList<ChannelModel> channelLists) {
		super.channelLists = channelLists;
	}
}
