package com.walktour.gui.task.parsedata.model.task.email.send;

import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.email.AccountConfig;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class EmailServerSendConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -1623974328132088269L;

	private boolean isCheck = false;

	private String serverName;

	private SendConfig sendConfig = new SendConfig();

	private AccountConfig accountConfig = new AccountConfig();

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public SendConfig getSendConfig() {
		return sendConfig;
	}

	public AccountConfig getAccountConfig() {
		return accountConfig;
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
				if (tagName.equals("EmailServerConfig")) {
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						String attName = parser.getAttributeName(i);
						if (attName.equals("IsCheck")) {
							this.setCheck(stringToBool(parser.getAttributeValue(i)));
						}
					}
				} else if (tagName.equals("ServerName")) {
					this.setServerName(parser.nextText());
				} else if (tagName.equals("SendConfig")) {
					sendConfig.parseXml(parser);
				} else if (tagName.equals("AccountConfig")) {
					accountConfig.parseXml(parser);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("EmailServerConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "EmailServerConfig");
		this.writeAttribute(serializer, "IsCheck", this.isCheck);
		this.writeTag(serializer, "ServerName", this.serverName);

		if (null != sendConfig) {
			sendConfig.writeXml(serializer);
		}
		if (null != accountConfig) {
			accountConfig.writeXml(serializer);
		}
		serializer.endTag(null, "EmailServerConfig");
	}
}
