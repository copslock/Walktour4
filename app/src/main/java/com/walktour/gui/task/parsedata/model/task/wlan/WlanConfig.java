package com.walktour.gui.task.parsedata.model.task.wlan;

import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;

public abstract class WlanConfig extends TaskBase {
	private static final long serialVersionUID = -7727780236501587505L;
	private int timeout=30;
	private String apName;

	private WlanAccount wlanAccount=new WlanAccount();

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public WlanAccount getWlanAccount() {
		return wlanAccount;
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
				if (tagName.equals("Timeout")) {
					this.setTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("APName")) {
					this.setApName(parser.nextText());
				} else if (tagName.equals("WlanAccount")) { 
					wlanAccount.parseXml(parser);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("WlanAPRelationTestConfig")) {
					return;
				} else if (tagName.equals("WlanWebLoginTestConfig")) {
					return;
				} else if (tagName.equals("WlanETEAuthTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}
}
