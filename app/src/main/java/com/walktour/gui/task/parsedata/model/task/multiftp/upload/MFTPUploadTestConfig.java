package com.walktour.gui.task.parsedata.model.task.multiftp.upload;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.LinkedList;
import java.util.List;

public class MFTPUploadTestConfig extends TaskBase { 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -768542086209274356L;
	public static final String PSCALLMODE_TIME="By Time";
	public static final String PSCALLMODE_FILE="By File";
	
	public static final String ENDCONDITION_ONE="One Session End";
	public static final String ENDCONDITION_ALL="All Session End";
	/**文件大小加线程数**/
	public static final String ThreadModeType_0="0";
	/**文件大小乘线程数**/
	public static final String ThreadModeType_1="1";
	/**结束条件: 0为One 1为All**/
	@SerializedName("endCondition")
	private String endCondition=ENDCONDITION_ONE;

	@SerializedName("uploadTimeout")
	private int uploadTimeout;
	/**无数据流量超时**/
	@SerializedName("noDataTimeout")
	private int noDataTimeout=60;
	@SerializedName("threadCount")
	private int threadCount=30;
	@SerializedName("threadMode")
	private String threadMode=ThreadModeType_0;
    /**映射：测试模式  0为 按文件  1为按时间***/
	@SerializedName("psCallMode")
	private String psCallMode=PSCALLMODE_FILE;
	/**Firstdata后间隔该时延插入计算速率起点事件,等待时长**/

	@SerializedName("waitTime")
	private int waitTime=3;
	@SerializedName("uploadDuration")
    private int uploadDuration;
	//发送缓存
	@SerializedName("sendBuffer")
	private String sendBuffer="2097152";
	//接收缓存
	@SerializedName("receBuffer")
	private String receBuffer="524288";
	@SerializedName("mftpUpList")
    private List<MFTPUpConfig> mftpUpList=new LinkedList<MFTPUpConfig>();

	public String getEndCondition() {
		return endCondition;
	}

	public void setEndCondition(String endCondition) {
		this.endCondition = endCondition;
	}

	public int getNoDataTimeout() {
		return noDataTimeout;
	}

	public void setNoDataTimeout(int noDataTimeout) {
		this.noDataTimeout = noDataTimeout;
	}

	public String getPsCallMode() {
		return psCallMode;
	}

	public void setPsCallMode(String psCallMode) {
		this.psCallMode = psCallMode;
	}

	public int getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	public List<MFTPUpConfig> getMftpUpList() {
		return mftpUpList;
	}
    
 
	public int getUploadTimeout() {
		return uploadTimeout;
	}

	public void setUploadTimeout(int uploadTimeout) {
		this.uploadTimeout = uploadTimeout;
	}

	public int getUploadDuration() {
		return uploadDuration;
	}

	public void setUploadDuration(int uploadDuration) {
		this.uploadDuration = uploadDuration;
	}
	public String getSendBuffer()
	{
		return sendBuffer;
	}

	public void setSendBuffer(String sendBuffer)
	{
		this.sendBuffer = sendBuffer;
	}

	public String getReceBuffer()
	{
		return receBuffer;
	}

	public int getThreadCount()
	{
		return threadCount;
	}

	public void setThreadCount(int threadCount)
	{
		this.threadCount = threadCount;
	}

	public String getThreadMode()
	{
		return threadMode;
	}

	public void setThreadMode(String threadMode)
	{
		this.threadMode = threadMode;
	}

	public void setReceBuffer(String receBuffer)
	{
		this.receBuffer = receBuffer;
	}
	public void parseXml(XmlPullParser parser) throws Exception {
		int eventType = parser.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("EndCondition")) {
					this.setEndCondition(parser.nextText());
				} else if (tagName.equals("NoDataTimeout")) {
					this.setNoDataTimeout(stringToInt(parser.nextText())/1000);
				}else if (tagName.equals("UploadTimeout")) {
					this.setUploadTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("UploadDuration")) {
					this.setUploadDuration(stringToInt(parser.nextText()));
				} else if (tagName.equals("PSCallMode")) {
					this.setPsCallMode(parser.nextText());
				} else if (tagName.equals("EndCondition")) {
					this.setEndCondition(parser.nextText());
				} else if (tagName.equals("WaitTime")) {
					this.setWaitTime(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("SendBuffer")){
					this.setSendBuffer(parser.nextText());
				}else if (tagName.equals("ReceiveBuffer")){
					this.setReceBuffer(parser.nextText());
				}else if (tagName.equals("ThreadCount")) {
					this.setThreadCount(stringToInt(parser.nextText()));
				} else if (tagName.equals("ThreadMode")){
					this.setThreadMode(parser.nextText());
				}else if (tagName.equals("MFTPUpConfig")) {
					MFTPUpConfig mftpUpConfig=new MFTPUpConfig();
					for(int i=0;i<parser.getAttributeCount();i++){
						String attName=parser.getAttributeName(i);
						String attValue=parser.getAttributeValue(i);
						
						if(attName.equals("IsCheck")){
							mftpUpConfig.setCheck(stringToBool(attValue));
						}
					}
					mftpUpConfig.parseXml(parser);
					this.getMftpUpList().add(mftpUpConfig);
				}  
				break;
			case XmlPullParser.END_TAG: 
				tagName = parser.getName();
				if (tagName.equals("MFTPUploadTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}
	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "MFTPUploadTestConfig");

		this.writeTag(serializer, "EndCondition", this.endCondition);
		this.writeTag(serializer, "UploadTimeout", this.uploadTimeout*1000);
		this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout*1000);
		this.writeTag(serializer, "PSCallMode", this.psCallMode);
		this.writeTag(serializer, "EndCondition", this.endCondition);
		this.writeTag(serializer, "WaitTime", this.waitTime*1000);
		this.writeTag(serializer, "UploadDuration", this.uploadDuration);
		this.writeTag(serializer, "SendBuffer", this.sendBuffer);
		this.writeTag(serializer, "ReceiveBuffer", this.receBuffer);
		this.writeTag(serializer, "ThreadCount", this.threadCount);
		this.writeTag(serializer, "ThreadMode", this.threadMode);
		serializer.startTag(null, "MFTPUpList");
		for(MFTPUpConfig mftpUpConfig:mftpUpList){
			if(null!=mftpUpConfig){
				mftpUpConfig.writeXml(serializer);
			}
		}
		serializer.endTag(null, "MFTPUpList");
		serializer.endTag(null, "MFTPUploadTestConfig");
	}
}
