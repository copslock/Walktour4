package com.walktour.gui.task.activity.scannertsma.model;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Scanner任务配置父model
 * 
 * @author zhihui.lian
 */

public abstract class ScanTaskModel {

	private int enable = 0; 			// 0 为未勾选 1为勾选
	private String taskName;
	private String taskType; 			// 自定义模板测试类型,区分各个网络同任务模板
	/**
	 * 0x0001 GSM 
	 * 0x0003 IS-95 CDMA 
	 * 0x0004 3GPP WCDMA 
	 * 0x0005 IS-2000 CDMA
	 * 0x0006 IS-856 EVDO 
	 * 0x0008 TDSCDMA
	 * 0x000A LTE
	 * 0x000B TD-LTE
	 * 0x000C WiFi
	 */
	private int protocolCode;   		//协议Code
	private int scanType;
	private boolean isUpload;			//是否上下行链路
	private int style;					//相当于界面上带宽			
	private int scanMode;				//默认为0
	private String groupName;			//组名，显示用
	
	public ArrayList<Channel> channelLists = new ArrayList<Channel>();
	
	public ArrayList<Channel> getChannelLists() {
		return channelLists;
	}
	public void setChannelLists(ArrayList<Channel> channelLists) {
		this.channelLists = channelLists;
	}
	public int getEnable() {
		return enable;
	}
	public void setEnable(int enable) {
		this.enable = enable;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public int getProtocolCode() {
		return protocolCode;
	}
	public void setProtocolCode(int protocolCode) {
		this.protocolCode = protocolCode;
	}
	public int getScanType() {
		return scanType;
	}
	public void setScanType(int scanType) {
		this.scanType = scanType;
	}
	public int getStyle() {
		return style;
	}
	public void setStyle(int style) {
		this.style = style;
	}
	public boolean isUpload() {
		return isUpload;
	}
	public void setUpload(boolean isUpload) {
		this.isUpload = isUpload;
	}
	public int getScanMode() {
		return scanMode;
	}
	public void setScanMode(int scanMode) {
		this.scanMode = scanMode;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * 解析子节点
	 *
	 * @param nodeName
	 * @param nodeValue
	 */
	public void NodeValue(XmlSerializer serializer, String nodeName, Object nodeValue) {

		try {
			serializer.startTag("", nodeName);
			serializer.text(String.valueOf(nodeValue));
			serializer.endTag("", nodeName);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public abstract void writeToXml(XmlSerializer serializer, TestSchemaType taskType) throws IOException;

	public abstract void parserXml(XmlPullParser parser, List<ScanTaskModel> testModelList) throws Exception;

	@Override
	public String toString() {
		return "ScanTaskModel [enable=" + enable + ", taskName=" + taskName
				+ ", taskType=" + taskType + ", protocolCode=" + protocolCode
				+ ", scanType=" + scanType + ", isUlorDl=" + isUpload
				+ ", style=" + style + ", scanMode=" + scanMode + "]";
	}
	
	
}
