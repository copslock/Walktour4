package com.walktour.gui.task.parsedata.model.task.ftp.download;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.base.FTPHostSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class FTPDownloadTestConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -4537727553742041708L;
	/**文件大小加线程数**/
	public static final String ThreadModeType_0="0";
	/**文件大小乘线程数**/
	public static final String ThreadModeType_1="1";
	@SerializedName("saveDirectory")
	private String saveDirectory;
	@SerializedName("downloadFile")
	private String downloadFile;
	@SerializedName("downloadTimeout")
	private int downloadTimeout;
	@SerializedName("noDataTimeout")
	private int noDataTimeout;
	@SerializedName("threadCount")
	private int threadCount;
	@SerializedName("threadMode")
	private String threadMode=ThreadModeType_0;
	@SerializedName("isSaveFile")
	private boolean isSaveFile;
	@SerializedName("psCallMode")
	private String psCallMode;
	@SerializedName("downloadDuration")
	private int downloadDuration;
	//传输协议：FTP和SFTP
	@SerializedName("transportTrotocol")
	private String transportProtocol="FTP";
	//发送缓存
	@SerializedName("sendBuffer")
	private String sendBuffer="524288";
	//接收缓存
	@SerializedName("receBuffer")
	private String receBuffer="4194304";
	@SerializedName("ftpHostSetting")
	private FTPHostSetting ftpHostSetting=new FTPHostSetting();

	public String getSaveDirectory() {
		return saveDirectory;
	}

	public void setSaveDirectory(String saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	public String getDownloadFile() {
		return downloadFile;
	}

	public void setDownloadFile(String downloadFile) {
		this.downloadFile = downloadFile;
	}

	public int getDownloadTimeout() {
		return downloadTimeout;
	}

	public void setDownloadTimeout(int downloadTimeout) {
		this.downloadTimeout = downloadTimeout;
	}

	public int getNoDataTimeout() {
		return noDataTimeout;
	}

	public void setNoDataTimeout(int noDataTimeout) {
		this.noDataTimeout = noDataTimeout;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public boolean isSaveFile() {
		return isSaveFile;
	}

	public void setSaveFile(boolean isSaveFile) {
		this.isSaveFile = isSaveFile;
	}

	public String getPsCallMode() {
		return psCallMode;
	}

	public void setPsCallMode(String psCallMode) {
		this.psCallMode = psCallMode;
	}

	public int getDownloadDuration() {
		return downloadDuration;
	}

	public void setDownloadDuration(int downloadDuration) {
		this.downloadDuration = downloadDuration;
	}

	public FTPHostSetting getFtpHostSetting() {
		return ftpHostSetting;
	}

	public String getTransportProtocol()
	{
		return transportProtocol;
	}

	public void setTransportProtocol(String transportProtocol)
	{
		this.transportProtocol = transportProtocol;
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
				} else if (tagName.equals("DownloadFile")) {
					this.setDownloadFile(parser.nextText());
				} else if (tagName.equals("DownloadTimeout")) {
					this.setDownloadTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("NoDataTimeout")) {
					this.setNoDataTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("ThreadCount")) {
					this.setThreadCount(stringToInt(parser.nextText()));
				} else if (tagName.equals("IsSaveFile")) {
					this.setSaveFile(stringToBool(parser.nextText()));
				} else if (tagName.equals("PSCallMode")) {
					this.setPsCallMode(parser.nextText());
				} else if (tagName.equals("DownloadDuration")) {
					this.setDownloadDuration(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("FTPHostSetting")) { 
					ftpHostSetting.parseXml(parser);
				}else if (tagName.equals("TransferProtocol")) {
					this.setTransportProtocol(parser.nextText()+"");
				}else if (tagName.equals("SendBuffer")){
					this.setSendBuffer(parser.nextText());
				}else if (tagName.equals("ReceiveBuffer")){
					this.setReceBuffer(parser.nextText());
				}else if (tagName.equals("ThreadMode")){
					this.setThreadMode(parser.nextText());
				}

				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("FTPDownloadTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "FTPDownloadTestConfig");

		this.writeTag(serializer, "SaveDirectory", this.saveDirectory);
		this.writeTag(serializer, "DownloadFile", this.downloadFile);
		this.writeTag(serializer, "DownloadTimeout", this.downloadTimeout*1000);
		this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout*1000);
		this.writeTag(serializer, "ThreadCount", this.threadCount);
		this.writeTag(serializer, "IsSaveFile", this.isSaveFile);
		this.writeTag(serializer, "PSCallMode", this.psCallMode);
		this.writeTag(serializer, "DownloadDuration", this.downloadDuration*1000);
		this.writeTag(serializer, "TransferProtocol", this.transportProtocol);
		this.writeTag(serializer, "SendBuffer", this.sendBuffer);
		this.writeTag(serializer, "ReceiveBuffer", this.receBuffer);
		this.writeTag(serializer, "ThreadMode", this.threadMode);
		if (null != ftpHostSetting) {
			ftpHostSetting.writeXml(serializer);
		}

		serializer.endTag(null, "FTPDownloadTestConfig");
	}
}
