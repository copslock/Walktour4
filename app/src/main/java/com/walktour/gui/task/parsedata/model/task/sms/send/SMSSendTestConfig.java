package com.walktour.gui.task.parsedata.model.task.sms.send;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class SMSSendTestConfig extends TaskBase {
	private static final long serialVersionUID = 756946807036776292L;
	@SerializedName("smsc")
	private String smsc;
	@SerializedName("sendPort")
	private int sendPort;
	@SerializedName("sendBaudRate")
	private int sendBaudRate = 57600;
	@SerializedName("smsFormat")
	private String smsFormat;
	@SerializedName("isSMSSP")
	private boolean isSMSSP = false;
	@SerializedName("smsText")
	private String smsText;
	@SerializedName("sendTimeout")
	private int sendTimeout;
	@SerializedName("receiverNum")
	private String receiverNum;
	@SerializedName("testType")
	private String testType = "Send Only";
	public String getSmsc() {
		return smsc;
	}

	public void setSmsc(String smsc) {
		this.smsc = smsc;
	}

	public int getSendPort() {
		return sendPort;
	}

	public void setSendPort(int sendPort) {
		this.sendPort = sendPort;
	}

	public int getSendBaudRate() {
		return sendBaudRate;
	}

	public void setSendBaudRate(int sendBaudRate) {
		this.sendBaudRate = sendBaudRate;
	}

	public String getSmsFormat() {
		return smsFormat;
	}

	public void setSmsFormat(String smsFormat) {
		this.smsFormat = smsFormat;
	}

	public boolean isSMSSP() {
		return isSMSSP;
	}

	public void setSMSSP(boolean isSMSSP) {
		this.isSMSSP = isSMSSP;
	}

	public String getSmsText() {
		return smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	public int getSendTimeout() {
		return sendTimeout;
	}

	public void setSendTimeout(int sendTimeout) {
		this.sendTimeout = sendTimeout;
	}
	
	
	public String getReceiverNum() {
		return receiverNum;
	}

	public void setReceiverNum(String receiverNum) {
		this.receiverNum = receiverNum;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
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
				tagName = parser.getName();if (tagName.equals("SMSC")) {
					this.setSmsc(parser.nextText());
				} else if (tagName.equals("SendPort")) {
					this.setSendPort(stringToInt(parser.nextText()));
				} else if (tagName.equals("SendBaudRate")) {
					this.setSendBaudRate(stringToInt(parser.nextText()));
				} else if (tagName.equals("SMSFormat")) {
					this.setSmsFormat(parser.nextText());
				} else if (tagName.equals("IsSMSSP")) {
					this.setSMSSP(stringToBool(parser.nextText()));
				} else if (tagName.equals("SMSText")) {
					this.setSmsText(parser.nextText());
				} else if (tagName.equals("ReceiveNumber")) {
					this.setReceiverNum(parser.nextText());
				} else if (tagName.equals("TestType")) {
					this.setTestType(parser.nextText());
				} else if (tagName.equals("SendTimeout")) {
					this.setSendTimeout(stringToInt(parser.nextText())/1000);
				} 

				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("SMSSendTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}
	
	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "SMSSendTestConfig");

		this.writeTag(serializer, "SMSC", this.smsc);
		this.writeTag(serializer, "SendPort", this.sendPort);
		this.writeTag(serializer, "SendBaudRate", this.sendBaudRate);
		this.writeTag(serializer, "SMSFormat", this.smsFormat);
		this.writeTag(serializer, "IsSMSSP", this.isSMSSP);
		this.writeTag(serializer, "ReceiveNumber", this.receiverNum);
		this.writeTag(serializer, "SMSText", this.smsText);
		this.writeTag(serializer, "SendTimeout", this.sendTimeout*1000);
		this.writeTag(serializer, "TestType", this.testType);
		serializer.endTag(null, "SMSSendTestConfig");
	}
}
