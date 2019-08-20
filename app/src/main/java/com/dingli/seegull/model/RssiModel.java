package com.dingli.seegull.model;

import java.util.ArrayList;

/**
 * Rssi配置model
 * @author zhihui.lian
 */
public class RssiModel extends ScanTaskModel implements Cloneable{
	
	public RssiModel(){
		setScanType(0);
	}
	

	private ArrayList<ChannelModel> channelList = new ArrayList<ChannelModel>();
	
	
	
	
	public ArrayList<ChannelModel> getChannelList() {
		return channelList;
	}

	public void setChannelList(ArrayList<ChannelModel> channelList) {
		this.channelList = channelList;
	}
	
	/**
	 * 对象浅克隆
	 */
	@Override
	public RssiModel clone(){
		RssiModel rssiModel = null;
		try {
			rssiModel = (RssiModel) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return rssiModel;
	}

	@Override
	public String toString() {
		return "RssiModel [channelList=" + channelList + "]" +  "ScanTaskModel [enable=" + getEnable() + ", taskName=" + getTaskName()
				+ ", taskType=" + getTaskType() + ", protocolCode=" + getProtocolCode()
				+ ", scanType=" + getScanType() + ", isUlorDl=" + isUpload()
				+ ", style=" + getStyle() + ", scanMode=" + getScanMode()+ "]";
	}

	@Override
	public void initChannelList(ArrayList<ChannelModel> channelLists) {
		super.channelLists = channelLists;
	}
	
	
}
