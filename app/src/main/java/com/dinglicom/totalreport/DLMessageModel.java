package com.dinglicom.totalreport;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求场景model类
 * @author zhihui.lian
 */
public class DLMessageModel {
	
	private String device;
	private String Tag;
	private String SendTime;
	private List<SubDlMessageItem> subDlMessageItems = new ArrayList<SubDlMessageItem>();   //文件属性列表
	private String business;
	
	
	public String getBusiness() {
		return business;
	}
	public void setBusiness(String business) {
		this.business = business;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getTag() {
		return Tag;
	}
	public void setTag(String tag) {
		Tag = tag;
	}
	public String getSendTime() {
		return SendTime;
	}
	public void setSendTime(String sendTime) {
		SendTime = sendTime;
	}
	public List<SubDlMessageItem> getSubDlMessageItems() {
		return subDlMessageItems;
	}
	public void setSubDlMessageItems(List<SubDlMessageItem> subDlMessageItems) {
		this.subDlMessageItems = subDlMessageItems;
	}
	
	
	
}
