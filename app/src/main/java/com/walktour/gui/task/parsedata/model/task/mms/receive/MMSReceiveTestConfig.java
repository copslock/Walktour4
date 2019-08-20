package com.walktour.gui.task.parsedata.model.task.mms.receive;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.base.GatewaySetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class MMSReceiveTestConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -296991462710755578L;
	@SerializedName("testType")
	private String testType = "Receive Only";
	@SerializedName("mmsc")
	private String mmsc;
	/**PUSH限时*/
	@SerializedName("pushTimeout")
	private int pushTimeout;
	@SerializedName("receiveTimeout")
	private int receiveTimeout;
	@SerializedName("gatewaySetting")
	private GatewaySetting gatewaySetting=new GatewaySetting();
	public String getTestType() {
		return testType;
	}
	public void setTestType(String testType) {
		this.testType = testType;
	}
	public String getMmsc() {
		return mmsc;
	}
	public void setMmsc(String mmsc) {
		this.mmsc = mmsc;
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
				if (tagName.equals("TestType")) {
					this.setTestType(parser.nextText());
				}else if (tagName.equals("MMSC")) {
					this.setMmsc(parser.nextText());
				}else if (tagName.equals("PUSHTimeout")) {
					this.setPushTimeout(stringToInt(parser.nextText())/1000);
				}else if (tagName.equals("ReceiveTimeout")) {
					this.setReceiveTimeout(stringToInt(parser.nextText())/1000);
				}else if (tagName.equals("GatewaySetting")) { 
					gatewaySetting.parseXml(parser);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("MMSReceiveTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}
	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "MMSReceiveTestConfig");
		this.writeTag(serializer, "TestType", this.testType);
		this.writeTag(serializer, "MMSC", this.mmsc);
		this.writeTag(serializer, "PUSHTimeout", this.pushTimeout*1000);
		this.writeTag(serializer, "ReceiveTimeout", this.receiveTimeout*1000);
		if(null!=gatewaySetting){
			gatewaySetting.writeXml(serializer);
		}
		serializer.endTag(null, "MMSReceiveTestConfig");
	}
}
