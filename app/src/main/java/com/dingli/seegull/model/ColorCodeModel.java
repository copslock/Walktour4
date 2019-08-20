package com.dingli.seegull.model;

import java.util.ArrayList;

/**
 * ColorCode任务配置model
 * 
 * @author zhihui.lian
 */

public class ColorCodeModel extends ScanTaskModel {

	private ArrayList<ChannelModel> channelList = new ArrayList<ChannelModel>();

	private double rssiThreshold;

	private boolean multipleColorCode;

	private boolean isColorCode;

	private boolean isCI;

	private boolean isL3Msg;
	
	

	public double getRssiThreshold() {
		return rssiThreshold;
	}

	public void setRssiThreshold(double rssiThreshold) {
		this.rssiThreshold = rssiThreshold;
	}

	public boolean isMultipleColorCode() {
		return multipleColorCode;
	}

	public void setMultipleColorCode(boolean multipleColorCode) {
		this.multipleColorCode = multipleColorCode;
	}

	public boolean isColorCode() {
		return isColorCode;
	}

	public void setColorCode(boolean isColorCode) {
		this.isColorCode = isColorCode;
	}

	public boolean isCI() {
		return isCI;
	}

	public void setCI(boolean isCI) {
		this.isCI = isCI;
	}

	public boolean isL3Msg() {
		return isL3Msg;
	}

	public void setL3Msg(boolean isL3Msg) {
		this.isL3Msg = isL3Msg;
	}

	public ArrayList<ChannelModel> getChannelList() {
		return channelList;
	}

	public void setChannelList(ArrayList<ChannelModel> channelList) {
		this.channelList = channelList;
	}

	@Override
	public String toString() {
		return "ColorCodeModel [channelList=" + channelList
				+ ", rssiThreshold=" + rssiThreshold + ", multipleColorCode="
				+ multipleColorCode + ", isColorCode=" + isColorCode
				+ ", isCI=" + isCI + ", isL3Msg=" + isL3Msg + "]" +  "ScanTaskModel [enable=" + getEnable() + ", taskName=" + getTaskName()
				+ ", taskType=" + getTaskType() + ", protocolCode=" + getProtocolCode()
				+ ", scanType=" + getScanType() + ", isUlorDl=" + isUpload()
				+ ", style=" + getStyle() + ", scanMode=" + getScanMode()+ "]";
	}

	@Override
	public void initChannelList(ArrayList<ChannelModel> channelLists) {
		super.channelLists = channelLists;
	}
}
