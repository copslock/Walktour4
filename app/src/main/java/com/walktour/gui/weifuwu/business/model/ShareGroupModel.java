package com.walktour.gui.weifuwu.business.model;
/***
 * 组信息
 * @author weirong.fan
 *
 */
public class ShareGroupModel {
	/** 主键ID **/
	private int id;
	/** 组Code **/
	private String groupCode;
	/** 组Name **/
	private String groupName;
	/** 创建组的ID **/
	private String createDeviceCode;
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
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getCreateDeviceCode() {
		return createDeviceCode;
	}
	public void setCreateDeviceCode(String createDeviceCode) {
		this.createDeviceCode = createDeviceCode;
	}
}
