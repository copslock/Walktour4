package com.walktour.gui.weifuwu.business.model;
/***
 * 共享终端信息表
 * 
 * @author weirong.fan
 *
 */
public class ShareDeviceModel {
	/** android系统 **/
	public static final int OS_ANDROID = 0;
	/** ios系统 **/
	public static final int OS_IOS = 1;
	/**-1--表示由组自动加入**/
	public static final int STATUS_DEFUALT=-1;
	/**0--表示由设备加入，请求中的非好友**/
	public static final int STATUS_NEW=0;
	/**0--表示由设备加入，请求中的需要确认的好友**/
	public static final int STATUS_CONFIRM=1;
	/**2--表示设备加入，新增的好友**/
	public static final int STATUS_ADDED=2;
	/**3--表示设备加入，已拒绝的好友**/
	public static final int STATUS_REFUSED=3; 
	/**4--表示设备加入，已解绑删除的好友**/
	public static final int STATUS_DELETED=4; 
	/** 主键ID */
	private int id;
	/** 终端CODE,6位 **/
	private String deviceCode="";
	/** 终端备注名称 **/
	private String deviceName;
	/** 终端操作系统:0-android,1-ios **/
	private int deviceOS = OS_ANDROID;
	/** 终端类型(htc,iphone5s等) **/
	private String deviceType;
	/**请求信息**/
	private String deviceMessage="";
	/**设备状态**/
	private int deviceStatus=STATUS_DEFUALT;
	/**创建时间***/
	private String createTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDeviceCode() {
		return deviceCode;
	}
	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public int getDeviceOS() {
		return deviceOS;
	}
	public void setDeviceOS(int deviceOS) {
		this.deviceOS = deviceOS;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public int getDeviceStatus() {
		return deviceStatus;
	}
	public void setDeviceStatus(int deviceStatus) {
		this.deviceStatus = deviceStatus;
	}
	public String getDeviceMessage() {
		return deviceMessage;
	}
	public void setDeviceMessage(String deviceMessage) {
		this.deviceMessage = deviceMessage;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	
}
