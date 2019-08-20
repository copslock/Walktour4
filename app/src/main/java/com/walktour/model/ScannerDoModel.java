package com.walktour.model;

import java.util.ArrayList;

/**
 * 
 * @author lzh
 * 解释回调model
 *
 */
public class ScannerDoModel {
	
	private ArrayList<String> channels;
	private ArrayList<String> rssi_Levels;
	private ArrayList<String> bsic;
	
	
	/**
	 * 初始化空构造
	 */
	public ScannerDoModel() {
		channels=new ArrayList<String>();
		rssi_Levels=new ArrayList<String>();
		bsic=new ArrayList<String>();
	}
	/**
	 * @return the channels
	 */
	public ArrayList<String> getChannels() {
		return channels;
	}
	/**
	 * @param channels the channels to set
	 */
	public void setChannels(ArrayList<String> channels) {
		this.channels = channels;
	}
	/**
	 * @return the bsic
	 */
	public ArrayList<String> getBsic() {
		return bsic;
	}
	/**
	 * @param bsic the bsic to set
	 */
	public void setBsic(ArrayList<String> bsic) {
		this.bsic = bsic;
	}
	/**
	 * @return the rssi_Levels
	 */
	public ArrayList<String> getRssi_Levels() {
		return rssi_Levels;
	}
	/**
	 * @param rssi_Levels the rssi_Levels to set
	 */
	public void setRssi_Levels(ArrayList<String> rssi_Levels) {
		this.rssi_Levels = rssi_Levels;
	}

	
	
	
	
	
}
