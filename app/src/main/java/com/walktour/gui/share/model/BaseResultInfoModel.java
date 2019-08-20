package com.walktour.gui.share.model;
/**
 * @author zhihui.lian 基础回调信息
 */
public class BaseResultInfoModel {
	//0--表示正常，其他表示失败
	public static final String REQ_SUCC="0";
	/**表示已经是好友了**/
	public static final String REQ_REGISTER_ADDED="17";
	public static final String REQ_REGISTER_FAILURE="19";
	private int reasonCode = 1; // 表示网络是否正常,原因码 1为正常 -1为失败
	private String reason="";
	private String device_code;
	private String result_code=REQ_SUCC;
	private String device_name;
	private String file_id=""; // 文件id
	private String group_code; // 群组ID
	private String group_name; //群组名称
	private String session_id="";
	
	public String getDevice_code() {
		return device_code;
	}
	public void setDevice_code(String device_code) {
		this.device_code = device_code;
	}
	public String getGroup_code() {
		return group_code;
	}
	public void setGroup_code(String group_code) {
		this.group_code = group_code;
	}
	public int getReasonCode() {
		return reasonCode;
	}
	public void setReasonCode(int reasonCode) {
		this.reasonCode = reasonCode;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getFile_id() {
		return file_id;
	}
	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}
	public String getResult_code() {
		return result_code;
	}
	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}
	public String getDevice_name() {
		return device_name;
	}
	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}
	public String getSession_id() {
		return session_id;
	}
	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	
}
