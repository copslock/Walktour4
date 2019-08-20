package com.walktour.gui.task.parsedata.model.task.email.receive;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.LinkedList;
import java.util.List;

public class EmailReceiveTestConfig extends TaskBase {
	/**
	 *
	 */
	private static final long serialVersionUID = -3798178960720888955L;
	public static final String PSCALLMODEL_BYTIME="By Time";
	public static final String PSCALLMODEL_BYFILE="By File";
	/**是否以PSCall模式进行测试**/
	@SerializedName("psCallMode")
	private String psCallMode=PSCALLMODEL_BYFILE;
	@SerializedName("receiveTimeout")
	private int receiveTimeout=1200;
	@SerializedName("noDataTimeout")
	private int noDataTimeout=60;
	@SerializedName("receiveEmailIndex")
	private String receiveEmailIndex;
	@SerializedName("isSaveFile")
	private boolean isSaveFile;
	@SerializedName("saveDirectory")
	private String saveDirectory;
	@SerializedName("emailServerList")
	private List<EmailServerReceivConfig> emailServerList = new LinkedList<EmailServerReceivConfig>();

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

	public int getReceiveTimeout() {
		return receiveTimeout;
	}

	public void setReceiveTimeout(int receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
	}

	public String getReceiveEmailIndex() {
		return receiveEmailIndex;
	}

	public void setReceiveEmailIndex(String receiveEmailIndex) {
		this.receiveEmailIndex = receiveEmailIndex;
	}

	public boolean getIsSaveFile() {
		return isSaveFile;
	}

	public void setIsSaveFile(boolean isSaveFile) {
		this.isSaveFile = isSaveFile;
	}

	public List<EmailServerReceivConfig> getEmailServerList() {
		return emailServerList;
	}

	public void setEmailServerList(List<EmailServerReceivConfig> emailServerList) {
		this.emailServerList = emailServerList;
	}

	public String getPsCallMode() {
		return psCallMode;
	}

	public void setPsCallMode(String psCallMode) {
		this.psCallMode = psCallMode;
	}

	public void setSaveFile(boolean isSaveFile) {
		this.isSaveFile = isSaveFile;
	}

	public void parseXml(XmlPullParser parser) throws Exception {
		EmailServerReceivConfig emailServerReceiveConfig = null;
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
					this.setNoDataTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("ReceiveTimeout")) {
					this.setReceiveTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("ReceiveEmailIndex")) {
					this.setReceiveEmailIndex(parser.nextText());
				} else if (tagName.equals("IsSaveFile")) {
					this.setIsSaveFile(stringToBool(parser.nextText()));
				} else if (tagName.equals("PSCallMode")) {
					this.setPsCallMode(parser.nextText());
				} else if (tagName.equals("EmailServerList")) {
				} else if (tagName.equals("EmailServerConfig")) {
					emailServerReceiveConfig = new EmailServerReceivConfig();
					emailServerReceiveConfig.parseXml(parser);
					emailServerList.add(emailServerReceiveConfig);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("EmailReceiveTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {

		serializer.startTag(null, "EmailReceiveTestConfig");

		this.writeTag(serializer, "SaveDirectory", this.saveDirectory);
		this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout*1000);
		this.writeTag(serializer, "ReceiveTimeout", this.receiveTimeout*1000);
		this.writeTag(serializer, "ReceiveEmailIndex", this.receiveEmailIndex);
		this.writeTag(serializer, "IsSaveFile", this.isSaveFile);
		this.writeTag(serializer, "PSCallMode", this.psCallMode);
		if (null != emailServerList && emailServerList.size() > 0) {
			serializer.startTag(null, "EmailServerList");
			for (EmailServerReceivConfig emailServerReceivConfig : emailServerList) {
				if (null != emailServerReceivConfig) {
					emailServerReceivConfig.writeXml(serializer);
				}
			}
			serializer.endTag(null, "EmailServerList");
		}

		serializer.endTag(null, "EmailReceiveTestConfig");
	}
}
