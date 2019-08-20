package com.walktour.gui.task.parsedata.model.task.multiftp.download;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.LinkedList;
import java.util.List;

public class MFTPDownloadTestConfig extends TaskBase { 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6529897293450769473L;
	public static final String PSCALLMODE_TIME="By Time";
	public static final String PSCALLMODE_FILE="By File";
	
	public static final String ENDCONDITION_ONE="One Session End";
	public static final String ENDCONDITION_ALL="All Session End";
	/**文件大小加线程数**/
	public static final String ThreadModeType_0="0";
	/**文件大小乘线程数**/
	public static final String ThreadModeType_1="1";
	@SerializedName("saveDirectory")
	private String saveDirectory;
	/**结束条件: 0为One 1为All**/
	@SerializedName("endCondition")
	private String endCondition=ENDCONDITION_ONE;
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
	@SerializedName("downloadTimeout")
	private int downloadTimeout=3;
	@SerializedName("downloadDuration")
	private int downloadDuration=3;
	@SerializedName("mftpDownList")
	private List<MFTPDownConfig> mftpDownList = new LinkedList<MFTPDownConfig>();
	//发送缓存
	@SerializedName("sendBuffer")
	private String sendBuffer="524288";
	//接收缓存
	@SerializedName("receBuffer")
	private String receBuffer="4194304";
	public String getSaveDirectory() {
		return saveDirectory;
	}

	public void setSaveDirectory(String saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

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

	public List<MFTPDownConfig> getMftpDownList() {
		return mftpDownList;
	}
	public int getDownloadTimeout() {
		return downloadTimeout;
	}

	public void setDownloadTimeout(int downloadTimeout) {
		this.downloadTimeout = downloadTimeout;
	}

	public int getDownloadDuration() {
		return downloadDuration;
	}

	public void setDownloadDuration(int downloadDuration) {
		this.downloadDuration = downloadDuration;
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

	public void setReceBuffer(String receBuffer)
	{
		this.receBuffer = receBuffer;
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
				if (tagName.equals("SaveDirectory")) {
					this.setSaveDirectory(parser.nextText());
				} else if (tagName.equals("NoDataTimeout")) {
					this.setNoDataTimeout(stringToInt(parser.nextText()));
				} else if (tagName.equals("PSCallMode")) {
					this.setPsCallMode(parser.nextText());
				} else if (tagName.equals("EndCondition")) {
					this.setEndCondition(parser.nextText());
				} else if (tagName.equals("WaitTime")) {
					this.setWaitTime(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("DownloadTimeout")) {
					this.setDownloadTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("DownloadDuration")) {
					this.setDownloadDuration(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("SendBuffer")){
					this.setSendBuffer(parser.nextText());
				}else if (tagName.equals("ReceiveBuffer")){
					this.setReceBuffer(parser.nextText());
				}else if (tagName.equals("ThreadCount")) {
					this.setThreadCount(stringToInt(parser.nextText()));
				} else if (tagName.equals("ThreadMode")){
					this.setThreadMode(parser.nextText());
				}else if (tagName.equals("MFTPDownConfig")) {
					MFTPDownConfig mftpDownConfig=new MFTPDownConfig();
					for(int i=0;i<parser.getAttributeCount();i++){
						String attName=parser.getAttributeName(i);
						String attValue=parser.getAttributeValue(i);
						
						if(attName.equals("IsCheck")){
							mftpDownConfig.setCheck(stringToBool(attValue));
						}
					}
					mftpDownConfig.parseXml(parser);
					this.getMftpDownList().add(mftpDownConfig);
				}  

				break;
			case XmlPullParser.END_TAG: 
				tagName = parser.getName();
				if (tagName.equals("MFTPDownloadTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}
	
	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "MFTPDownloadTestConfig");
		this.writeTag(serializer, "SaveDirectory", this.saveDirectory);
		this.writeTag(serializer, "EndCondition", this.endCondition);
		this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout);
		this.writeTag(serializer, "PSCallMode", this.psCallMode);
		this.writeTag(serializer, "WaitTime", this.waitTime*1000);
		this.writeTag(serializer, "DownloadTimeout", this.downloadTimeout*1000);
		this.writeTag(serializer, "DownloadDuration", this.downloadDuration*1000);
		this.writeTag(serializer, "SendBuffer", this.sendBuffer);
		this.writeTag(serializer, "ReceiveBuffer", this.receBuffer);
		this.writeTag(serializer, "ThreadCount", this.threadCount);
		this.writeTag(serializer, "ThreadMode", this.threadMode);
		serializer.startTag(null, "MFTPDownList");
		for(MFTPDownConfig mftpDownConfig:mftpDownList){
			if(null!=mftpDownConfig){
				mftpDownConfig.writeXml(serializer);
			}
		}
		serializer.endTag(null, "MFTPDownList");
		serializer.endTag(null, "MFTPDownloadTestConfig");
	}
	
}
