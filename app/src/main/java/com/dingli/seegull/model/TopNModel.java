package com.dingli.seegull.model;

import java.util.ArrayList;

/**
 * TopN配置model
 * 
 * @author zhihui.lian
 */
public class TopNModel extends ScanTaskModel {

	private ArrayList<ChannelModel> channelList = new ArrayList<ChannelModel>();

	private int numberOfPilots;
	
	private int pilotMode;
	
	private double pilotThreshold;
	
	private int syncDLEcIo;
	
	private int bchLayer3Message;
	
	private boolean isPscOrPn;				//界面用参数
	
	private String pscStr = "";					//界面用参数
	/**
	 * scan type dataMode    值0或1
	 */
	private int ecioEnable;							//0x0001
	private int timeOffsetEnable;					//0x0004
	private int sirEnable; 							//0x0200
	private int ecEnable;							//0x0400		
	private int epsIoEnable;						//0x0020
	private int bchLayer3MessageDecodingEnable;		//0x4000
	private int pilotDelayEnable;					//0x0004
	private int aggregateEcIoEnable;				//0x0008	
	private int delaySpreadEnable;					//0x0010
    private int rakeFingerCountEnable;				//0x0100
    private int essIoEnable;						//0x0040


	public ArrayList<ChannelModel> getChannelList() {
		return channelList;
	}

	public void setChannelList(ArrayList<ChannelModel> channelList) {
		this.channelList = channelList;
	}

	public int getNumberOfPilots() {
		return numberOfPilots;
	}

	public void setNumberOfPilots(int numberOfPilots) {
		this.numberOfPilots = numberOfPilots;
	}

	public int getPilotMode() {
		return pilotMode;
	}

	public void setPilotMode(int pilotMode) {
		this.pilotMode = pilotMode;
	}

	public double getPilotThreshold() {
		return pilotThreshold;
	}

	public void setPilotThreshold(double pilotThreshold) {
		this.pilotThreshold = pilotThreshold;
	}

	public int getSyncDLEcIo() {
		return syncDLEcIo;
	}

	public void setSyncDLEcIo(int syncDLEcIo) {
		this.syncDLEcIo = syncDLEcIo;
	}

	public int getBchLayer3Message() {
		return bchLayer3Message;
	}

	public void setBchLayer3Message(int bchLayer3Message) {
		this.bchLayer3Message = bchLayer3Message;
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

	public int getEcioEnable() {
		return ecioEnable;
	}

	public void setEcioEnable(int ecioEnable) {
		this.ecioEnable = ecioEnable;
	}

	public int getTimeOffsetEnable() {
		return timeOffsetEnable;
	}

	public void setTimeOffsetEnable(int timeOffsetEnable) {
		this.timeOffsetEnable = timeOffsetEnable;
	}

	public int getSirEnable() {
		return sirEnable;
	}

	public void setSirEnable(int sirEnable) {
		this.sirEnable = sirEnable;
	}

	public int getEcEnable() {
		return ecEnable;
	}

	public void setEcEnable(int ecEnable) {
		this.ecEnable = ecEnable;
	}

	public int getEpsIoEnable() {
		return epsIoEnable;
	}

	public void setEpsIoEnable(int epsIoEnable) {
		this.epsIoEnable = epsIoEnable;
	}

	public int getBchLayer3MessageDecodingEnable() {
		return bchLayer3MessageDecodingEnable;
	}

	public void setBchLayer3MessageDecodingEnable(int bchLayer3MessageDecodingEnable) {
		this.bchLayer3MessageDecodingEnable = bchLayer3MessageDecodingEnable;
	}

	public int getPilotDelayEnable() {
		return pilotDelayEnable;
	}

	public void setPilotDelayEnable(int pilotDelayEnable) {
		this.pilotDelayEnable = pilotDelayEnable;
	}

	public int getAggregateEcIoEnable() {
		return aggregateEcIoEnable;
	}

	public void setAggregateEcIoEnable(int aggregateEcIoEnable) {
		this.aggregateEcIoEnable = aggregateEcIoEnable;
	}

	public int getDelaySpreadEnable() {
		return delaySpreadEnable;
	}

	public void setDelaySpreadEnable(int delaySpreadEnable) {
		this.delaySpreadEnable = delaySpreadEnable;
	}

	public int getRakeFingerCountEnable() {
		return rakeFingerCountEnable;
	}

	public void setRakeFingerCountEnable(int rakeFingerCountEnable) {
		this.rakeFingerCountEnable = rakeFingerCountEnable;
	}

	public int getEssIoEnable() {
		return essIoEnable;
	}

	public void setEssIoEnable(int essIoEnable) {
		this.essIoEnable = essIoEnable;
	}

	@Override
	public String toString() {
		return "TopNModel [channelList=" + channelList + ", numberOfPilots="
				+ numberOfPilots + ", pilotMode=" + pilotMode
				+ ", pilotThreshold=" + pilotThreshold + ", syncDLEcIo="
				+ syncDLEcIo + ", bchLayer3Message=" + bchLayer3Message
				+ ", isPscOrPn=" + isPscOrPn + ", pscStr=" + pscStr
				+ ", ecioEnable=" + ecioEnable + ", timeOffsetEnable="
				+ timeOffsetEnable + ", sirEnable=" + sirEnable + ", ecEnable="
				+ ecEnable + ", epsIoEnable=" + epsIoEnable
				+ ", bchLayer3MessageDecodingEnable="
				+ bchLayer3MessageDecodingEnable + ", pilotDelayEnable="
				+ pilotDelayEnable + ", aggregateEcIoEnable="
				+ aggregateEcIoEnable + ", delaySpreadEnable="
				+ delaySpreadEnable + ", rakeFingerCountEnable="
				+ rakeFingerCountEnable + ", essIoEnable=" + essIoEnable + "]"
				+  "ScanTaskModel [enable=" + getEnable() + ", taskName=" + getTaskName()
				+ ", taskType=" + getTaskType() + ", protocolCode=" + getProtocolCode()
				+ ", scanType=" + getScanType() + ", isUlorDl=" + isUpload()
				+ ", style=" + getStyle() + ", scanMode=" + getScanMode()+ "]"
				;
	}
	
	@Override
	public void initChannelList(ArrayList<ChannelModel> channelLists) {
		super.channelLists = channelLists;
	}
}
