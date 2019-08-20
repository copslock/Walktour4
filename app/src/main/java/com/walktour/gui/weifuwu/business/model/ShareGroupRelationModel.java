package com.walktour.gui.weifuwu.business.model;
/***
 * 组信息
 * 
 * @author weirong.fan
 *
 */
public class ShareGroupRelationModel {
	/** 主键ID **/
	private int id;
	/** 组Code **/
	private String groupCode;
	/** 设备Code **/
	private String deviceCode;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getDeviceCode() {
		return deviceCode;
	}
	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}
}
