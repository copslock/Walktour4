package com.walktour.gui.task.parsedata.model.task.ftp.upload;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.base.FTPHostSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class FTPUploadTestConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -8186920343492559517L;
	/**文件大小加线程数**/
	public static final String ThreadModeType_0="0";
	/**文件大小乘线程数**/
	public static final String ThreadModeType_1="1";
	@SerializedName("fileSource")
	private String fileSource;
	@SerializedName("localFile")
	private String localFile;
	@SerializedName("fileSize")
	private int fileSize;
	@SerializedName("remoteDirectory")
	private String remoteDirectory;
	@SerializedName("uploadTimeout")
	private int uploadTimeout;
	@SerializedName("noDataTimeout")
	private int noDataTimeout;
	@SerializedName("threadCount")
	private int threadCount;
	@SerializedName("threadMode")
	private String threadMode=ThreadModeType_0;
	@SerializedName("psCallMode")
	private String psCallMode;
	@SerializedName("uploadDuration")
	private int uploadDuration;
	//传输协议：FTP和SFTP
	@SerializedName("transportTrotocol")
	private String transportProtocol="FTP";
	//发送缓存
	@SerializedName("sendBuffer")
	private String sendBuffer="2097152";
	//接收缓存
	@SerializedName("receBuffer")
	private String receBuffer="524288";
	@SerializedName("ftpHostSetting")
	private FTPHostSetting ftpHostSetting = new FTPHostSetting();

	public String getFileSource() {
		return fileSource;
	}

	public void setFileSource(String fileSource) {
		this.fileSource = fileSource;
	}

	public String getLocalFile() {
		return localFile;
	}

	public void setLocalFile(String localFile) {
		this.localFile = localFile;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getRemoteDirectory() {
		return remoteDirectory;
	}

	public void setRemoteDirectory(String remoteDirectory) {
		this.remoteDirectory = remoteDirectory;
	}

	public int getUploadTimeout() {
		return uploadTimeout;
	}

	public void setUploadTimeout(int uploadTimeout) {
		this.uploadTimeout = uploadTimeout;
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

	public String getPsCallMode() {
		return psCallMode;
	}

	public void setPsCallMode(String psCallMode) {
		this.psCallMode = psCallMode;
	}

	public int getUploadDuration() {
		return uploadDuration;
	}

	public void setUploadDuration(int uploadDuration) {
		this.uploadDuration = uploadDuration;
	}

	public FTPHostSetting getFtpHostSetting() {
		return ftpHostSetting;
	}
	public String getTransportProtocol()
	{
		return transportProtocol;
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

	public void setTransportProtocol(String transportProtocol)
	{
		this.transportProtocol = transportProtocol;
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
				if (tagName.equals("FileSource")) {
					this.setFileSource(parser.nextText());
				} else if (tagName.equals("LocalFile")) {
					this.setLocalFile(parser.nextText());
				} else if (tagName.equals("FileSize")) {
					this.setFileSize(stringToInt(parser.nextText()));
				} else if (tagName.equals("RemoteDirectory")) {
					this.setRemoteDirectory(parser.nextText());
				} else if (tagName.equals("UploadTimeout")) {
					this.setUploadTimeout(stringToInt(parser.nextText()) / 1000);
				} else if (tagName.equals("NoDataTimeout")) {
					this.setNoDataTimeout(stringToInt(parser.nextText()) / 1000);
				} else if (tagName.equals("ThreadCount")) {
					this.setThreadCount(stringToInt(parser.nextText()));
				} else if (tagName.equals("PSCallMode")) {
					this.setPsCallMode(parser.nextText());
				} else if (tagName.equals("UploadDuration")) {
					this.setUploadDuration(stringToInt(parser.nextText()) / 1000);
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
				if (tagName.equals("FTPUploadTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "FTPUploadTestConfig");
		this.writeTag(serializer, "FileSource", this.fileSource);
		this.writeTag(serializer, "LocalFile", this.localFile);
		this.writeTag(serializer, "FileSize", this.fileSize);
		this.writeTag(serializer, "RemoteDirectory", this.remoteDirectory);
		this.writeTag(serializer, "UploadTimeout", this.uploadTimeout * 1000);
		this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout * 1000);
		this.writeTag(serializer, "ThreadCount", this.threadCount);
		this.writeTag(serializer, "PSCallMode", this.psCallMode);
		this.writeTag(serializer, "UploadDuration", this.uploadDuration * 1000);
		this.writeTag(serializer, "TransferProtocol", this.transportProtocol);
		this.writeTag(serializer, "SendBuffer", this.sendBuffer);
		this.writeTag(serializer, "ReceiveBuffer", this.receBuffer);
		this.writeTag(serializer, "ThreadMode", this.threadMode);
		if (null != this.ftpHostSetting) {
			ftpHostSetting.writeXml(serializer);
		}

		serializer.endTag(null, "FTPUploadTestConfig");
	}

}
