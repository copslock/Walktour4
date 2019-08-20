package com.walktour.gui.task.parsedata.model.task.mms.sendreceive;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.base.GatewaySetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class MMSSelfTestConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -8223075880983676116L;
	@SerializedName("receiveNumber")
	private String receiveNumber;
	@SerializedName("ccNumber")
	private String ccNumber;
	@SerializedName("subject")
	private String subject;
	@SerializedName("content")
	private String content;
	@SerializedName("priority")
	private String priority = "High";
	@SerializedName("fileSource")
	private String fileSource;
	@SerializedName("fileSize")
	private int fileSize;
	@SerializedName("localFile")
	private String localFile;
	@SerializedName("mmsc")
	private String mmsc;
	@SerializedName("sendTimeout")
	private int sendTimeout;
	@SerializedName("pushTimeout")
	private int pushTimeout;
	@SerializedName("receiveTimeout")
	private int receiveTimeout;
	@SerializedName("gatewaySetting")
	private GatewaySetting gatewaySetting = new GatewaySetting();

	public String getReceiveNumber() {
		return receiveNumber;
	}

	public void setReceiveNumber(String receiveNumber) {
		this.receiveNumber = receiveNumber;
	}

	public String getCcNumber() {
		return ccNumber;
	}

	public void setCcNumber(String ccNumber) {
		this.ccNumber = ccNumber;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getFileSource() {
		return fileSource;
	}

	public void setFileSource(String fileSource) {
		this.fileSource = fileSource;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getLocalFile() {
		return localFile;
	}

	public void setLocalFile(String localFile) {
		this.localFile = localFile;
	}

	public String getMmsc() {
		return mmsc;
	}

	public void setMmsc(String mmsc) {
		this.mmsc = mmsc;
	}

	public int getSendTimeout() {
		return sendTimeout;
	}

	public void setSendTimeout(int sendTimeout) {
		this.sendTimeout = sendTimeout;
	}

	public int getPushTimeout() {
		return pushTimeout;
	}

	public void setPushTimeout(int pushTimeout) {
		this.pushTimeout = pushTimeout;
	}

	public int getReceiveTimeout() {
		return receiveTimeout;
	}

	public void setReceiveTimeout(int receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
	}

	public GatewaySetting getGatewaySetting() {
		return gatewaySetting;
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
				if (tagName.equals("ReceiveNumber")) {
					this.setReceiveNumber(parser.nextText());
				} else if (tagName.equals("CCNumber")) {
					this.setCcNumber(parser.nextText());
				} else if (tagName.equals("Subject")) {
					this.setSubject(parser.nextText());
				} else if (tagName.equals("Content")) {
					this.setContent(parser.nextText());
				} else if (tagName.equals("Priority")) {
					this.setPriority(parser.nextText());
				} else if (tagName.equals("FileSource")) {
					this.setFileSource(parser.nextText());
				} else if (tagName.equals("LocalFile")) {
					this.setLocalFile(parser.nextText());
				} else if (tagName.equals("MMSC")) {
					this.setMmsc(parser.nextText());
				} else if (tagName.equals("FileSize")) {
					this.setFileSize(stringToInt(parser.nextText()));
				} else if (tagName.equals("SendTimeout")) {
					this.setSendTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("PUSHTimeout")) {
					this.setPushTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("ReceiveTimeout")) {
					this.setReceiveTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("GatewaySetting")) {
					gatewaySetting.parseXml(parser);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("MMSSelfTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "MMSSelfTestConfig");
		this.writeTag(serializer, "ReceiveNumber", this.receiveNumber);
		this.writeTag(serializer, "CCNumber", this.ccNumber);
		this.writeTag(serializer, "Subject", this.subject);
		this.writeTag(serializer, "Content", this.content);
		this.writeTag(serializer, "Priority", this.priority);
		this.writeTag(serializer, "FileSource", this.fileSource);
		this.writeTag(serializer, "FileSize", this.fileSize);
		this.writeTag(serializer, "LocalFile", this.localFile);
		this.writeTag(serializer, "MMSC", this.mmsc);
		this.writeTag(serializer, "SendTimeout", this.sendTimeout*1000);
		this.writeTag(serializer, "PUSHTimeout", this.pushTimeout*1000);
		this.writeTag(serializer, "ReceiveTimeout", this.receiveTimeout*1000);
		if (null != gatewaySetting) {
			gatewaySetting.writeXml(serializer);
		}
		serializer.endTag(null, "MMSSelfTestConfig");
	}

}
