package com.walktour.gui.task.parsedata.model.task.email.sendreceive;

import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.LinkedList;
import java.util.List;

public class EmailSelfTestConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 6281364760347502690L;
	private String psCallMode;
	private int sendTimeout=1200;
	private int receiveTimeout=300;
	private String fileSource;
	private int fileSize;
	/**如果有附件时，此处填附件的路径**/
	private String localFile;
	private String emailSubject;
	private String emailBody;
	
	private String saveDirectory;
	private String receiverAddress;
	
	private int noDataTimeout;
	
	
	
	private int receiveEmailIndex;
	private boolean isSaveFile;
	private List<EmailServerConfig> emailServerList = new LinkedList<EmailServerConfig>();

	public String getFileSource() {
		return fileSource;
	}

	public void setFileSource(String fileSource) {
		this.fileSource = fileSource;
	}

	public String getPsCallMode() {
		return psCallMode;
	}

	public void setPsCallMode(String psCallMode) {
		this.psCallMode = psCallMode;
	}

	public String getSaveDirectory() {
		return saveDirectory;
	}

	public void setSaveDirectory(String saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	public int getNoDataTimeout() {
		return noDataTimeout;
	}

	public void setNoDataTimeout(int noDataTimeout) {
		this.noDataTimeout = noDataTimeout;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailBody() {
		return emailBody;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public int getSendTimeout() {
		return sendTimeout;
	}

	public void setSendTimeout(int sendTimeout) {
		this.sendTimeout = sendTimeout;
	}

	public int getReceiveTimeout() {
		return receiveTimeout;
	}

	public void setReceiveTimeout(int receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
	}

	public int getReceiveEmailIndex() {
		return receiveEmailIndex;
	}

	public void setReceiveEmailIndex(int receiveEmailIndex) {
		this.receiveEmailIndex = receiveEmailIndex;
	}

	public boolean isSaveFile() {
		return isSaveFile;
	}

	public void setSaveFile(boolean isSaveFile) {
		this.isSaveFile = isSaveFile;
	}

	public List<EmailServerConfig> getEmailServerList() {
		return emailServerList;
	}

	public String getReceiverAddress() {
		return receiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}

	public String getLocalFile() {
		return localFile;
	}

	public void setLocalFile(String localFile) {
		this.localFile = localFile;
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
				} else if (tagName.equals("NoDataTimeout")) {
					this.setNoDataTimeout(stringToInt(parser.nextText()));
				} else if (tagName.equals("ReceiverAddress")) {
					this.setReceiverAddress(parser.nextText());
				} else if (tagName.equals("FileSize")) {
					this.setFileSize(stringToInt(parser.nextText()));
				} else if (tagName.equals("EmailSubject")) {
					this.setEmailSubject(parser.nextText());
				} else if (tagName.equals("EmailBody")) {
					this.setEmailBody(parser.nextText());
				} else if (tagName.equals("PSCallMode")) {
					this.setPsCallMode(parser.nextText());
				} else if (tagName.equals("SaveDirectory")) {
					this.setSaveDirectory(parser.nextText());
				} else if (tagName.equals("SendTimeout")) {
					this.setSendTimeout(stringToInt(parser.nextText()));
				} else if (tagName.equals("ReceiveTimeout")) {
					this.setReceiveTimeout(stringToInt(parser.nextText()));
				} else if (tagName.equals("ReceiveEmailIndex")) {
					this.setReceiveEmailIndex(stringToInt(parser.nextText()));
				} else if (tagName.equals("IsSaveFile")) {
					this.setSaveFile(stringToBool(parser.nextText()));
				} else if (tagName.equals("EmailServerList")) {
				} else if (tagName.equals("EmailServerConfig")) {
					EmailServerConfig emailServerConfig = new EmailServerConfig();
					emailServerConfig.parseXml(parser);
					this.getEmailServerList().add(emailServerConfig);

				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("EmailSelfTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "EmailSelfTestConfig");

		this.writeTag(serializer, "FileSource", this.fileSource);
		this.writeTag(serializer, "PSCallMode", this.psCallMode);
		this.writeTag(serializer, "SaveDirectory", this.saveDirectory);
		this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout);
		this.writeTag(serializer, "FileSize", this.fileSize);
		this.writeTag(serializer, "EmailSubject", this.emailSubject);
		this.writeTag(serializer, "EmailBody", this.emailBody);
		this.writeTag(serializer, "SendTimeout", this.sendTimeout);
		this.writeTag(serializer, "ReceiveTimeout", this.receiveTimeout);
		this.writeTag(serializer, "ReceiveEmailIndex", this.receiveEmailIndex);
		this.writeTag(serializer, "IsSaveFile", this.isSaveFile);
		if (null != emailServerList && emailServerList.size() > 0) {
			serializer.startTag(null, "EmailServerList");
			for (EmailServerConfig emailServerConfig : emailServerList) {
				emailServerConfig.writeXml(serializer);
			}
			serializer.endTag(null, "EmailServerList");
		}
		
		serializer.endTag(null, "EmailSelfTestConfig");
	}
}
