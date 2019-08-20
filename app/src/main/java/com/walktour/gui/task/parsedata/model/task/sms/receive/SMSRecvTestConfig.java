package com.walktour.gui.task.parsedata.model.task.sms.receive;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class SMSRecvTestConfig extends TaskBase{
	private static final long serialVersionUID = -4953411360637871830L;
	@SerializedName("testType")
	private String testType = "Receive Only";
	@SerializedName("recvTimeout")
	private int recvTimeout;

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	public int getRecvTimeout() {
		return recvTimeout;
	}

	public void setRecvTimeout(int recvTimeout) {
		this.recvTimeout = recvTimeout;
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
				if (tagName.equals("TestType")) {
					this.setTestType(parser.nextText());
				} else if (tagName.equals("RecvTimeout")) {
					this.setRecvTimeout(stringToInt(parser.nextText())/1000);
				} 

				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("SMSRecvTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}
	
	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "SMSRecvTestConfig");

		this.writeTag(serializer, "TestType", this.testType);
		this.writeTag(serializer, "RecvTimeout", this.recvTimeout*1000);

		serializer.endTag(null, "SMSRecvTestConfig");
	}
}
