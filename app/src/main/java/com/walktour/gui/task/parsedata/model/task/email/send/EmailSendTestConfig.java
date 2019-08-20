package com.walktour.gui.task.parsedata.model.task.email.send;

import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.LinkedList;
import java.util.List;

public class EmailSendTestConfig extends TaskBase { 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7629937616477541600L;
	public static final String PSCALLMODEL_BYTIME="By Time";
	public static final String PSCALLMODEL_BYFILE="By File";
	
	public static final String FILESOURCE_LOCAL="Local File";
	public static final String FILESOURCE_CREATE="Creat File";
	private String psCallMode=PSCALLMODEL_BYFILE;
	private int sendTimeout=1200;
	/**选择文件上传方式 0为本地 1为自动创建**/
	private String fileSource=FILESOURCE_CREATE;
	/**上传附件时，前端随机生成的上传文件的大小,单位Bytes**/
	private int fileSize;
	/**如果有附件时，此处填附件的路径**/
	private String localFile;
	private String emailSubject;
	private String emailBody;
	private List<EmailServerSendConfig> emailServerList = new LinkedList<EmailServerSendConfig>();
	private int noDataTimeout=60;
	private String receiverAddress;
	/**身份验证方式:0：None 1：Simple Login**/
	private int isAuthentcation=0;
	/**附件文件名**/
	private String fileName;
	/**发件人**/
	private String fromUser;
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getFileSource() {
		return fileSource;
	}

	public int getIsAuthentcation() {
		return isAuthentcation;
	}

	public void setIsAuthentcation(int isAuthentcation) {
		this.isAuthentcation = isAuthentcation;
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

	public int getNoDataTimeout() {
		return noDataTimeout;
	}

	public void setNoDataTimeout(int noDataTimeout) {
		this.noDataTimeout = noDataTimeout;
	}

	public String getReceiverAddress() {
		return receiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
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

	public List<EmailServerSendConfig> getEmailServerList() {
		return emailServerList;
	}

	public void setEmailServerList(List<EmailServerSendConfig> emailServerList) {
		this.emailServerList = emailServerList;
	}

	public int getSendTimeout() {
		return sendTimeout;
	}

	public void setSendTimeout(int sendTimeout) {
		this.sendTimeout = sendTimeout;
	}

	public String getPsCallMode() {
		return psCallMode;
	}

	public void setPsCallMode(String psCallMode) {
		this.psCallMode = psCallMode;
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
					this.setNoDataTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("ReceiverAddress")) {
					this.setReceiverAddress(parser.nextText());
				} else if (tagName.equals("FileSize")) {
					this.setFileSize(stringToInt(parser.nextText()));
				} else if (tagName.equals("EmailSubject")) {
					this.setEmailSubject(parser.nextText());
				} else if (tagName.equals("EmailBody")) {
					this.setEmailBody(parser.nextText());
				} else if (tagName.equals("SendTimeout")) {
					this.setSendTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("IsAuthentcation")) {
					this.setIsAuthentcation(stringToInt(parser.nextText()));
				}else if (tagName.equals("PSCallMode")) {
					this.setPsCallMode(parser.nextText());
				}else if (tagName.equals("FileName")) {
					this.setFileName(parser.nextText());
				}else if (tagName.equals("FromUser")) {
					this.setFromUser(parser.nextText());
				} else if (tagName.equals("EmailServerList")) {
				} else if (tagName.equals("EmailServerConfig")) {
					EmailServerSendConfig emailServerConfig = new EmailServerSendConfig();
					emailServerConfig.parseXml(parser);
					this.getEmailServerList().add(emailServerConfig);

				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("EmailSendTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "EmailSendTestConfig");

		this.writeTag(serializer, "FileSource", this.fileSource);
		this.writeTag(serializer, "LocalFile", this.localFile);
		this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout*1000);
		this.writeTag(serializer, "ReceiverAddress", this.receiverAddress);
		this.writeTag(serializer, "FileSize", this.fileSize);
		this.writeTag(serializer, "EmailSubject", this.emailSubject);
		this.writeTag(serializer, "EmailBody", this.emailBody);
		this.writeTag(serializer, "SendTimeout", this.sendTimeout*1000);
		this.writeTag(serializer, "PSCallMode", this.psCallMode);
		this.writeTag(serializer, "FileName", this.fileName);
		this.writeTag(serializer, "FromUser", this.fromUser);
		
		this.writeTag(serializer, "IsAuthentcation", this.isAuthentcation);
		if (null != emailServerList && emailServerList.size() > 0) {
			serializer.startTag(null, "EmailServerList");
			for (EmailServerSendConfig emailServerSendConfig : emailServerList) {
				if (null != emailServerSendConfig) {
					emailServerSendConfig.writeXml(serializer);
				}
			}
			serializer.endTag(null, "EmailServerList");
		} 
		serializer.endTag(null, "EmailSendTestConfig");

	}
}
