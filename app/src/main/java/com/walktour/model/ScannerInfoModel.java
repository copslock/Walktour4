package com.walktour.model;

import com.walktour.Utils.WalkStruct.NetType;

import java.util.ArrayList;

public class ScannerInfoModel {
	private String deviceName 	= "";
	private ArrayList<NetType> netTypes = new ArrayList<NetType>();
	private String modulName 	= "";
	private int chipvendor	 	= 0;
	private String expend1 		= "";
	private String expend2 		= "";
	private String expend3 		= "";
	
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("deviceName:");
		sb.append(deviceName);
		sb.append(";netTypes:");
		for(NetType netType : netTypes){
			sb.append(netType.name());
			sb.append(",");
		}
		sb.append(";modulName:");
		sb.append(modulName);
		sb.append(";chipvendor:");
		sb.append(chipvendor);
		return sb.toString();
	}
	
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public ArrayList<NetType> getNetTypes() {
		return netTypes;
	}
	public void setNetTypes(ArrayList<NetType> netTypes) {
		this.netTypes = netTypes;
	}
	public void addNetTypes(NetType netType){
		this.netTypes.add(netType);
	}
	public String getModulName() {
		return modulName;
	}
	public void setModulName(String modulName) {
		this.modulName = modulName;
	}
	public int getChipvendor() {
		return chipvendor;
	}
	public void setChipvendor(int chipvendor) {
		this.chipvendor = chipvendor;
	}
	public String getExpend1() {
		return expend1;
	}
	public void setExpend1(String expend1) {
		this.expend1 = expend1;
	}
	public String getExpend2() {
		return expend2;
	}
	public void setExpend2(String expend2) {
		this.expend2 = expend2;
	}
	public String getExpend3() {
		return expend3;
	}
	public void setExpend3(String expend3) {
		this.expend3 = expend3;
	}
}
