package com.walktour.gui.task.parsedata.model.task.iperf;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.base.TelnetSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class IPerfTestConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -3947113743107786212L;
	@SerializedName("testMode")
	private String testMode;
	@SerializedName("protocol")
	private String protocol;
	@SerializedName("serverIP")
	private String serverIP;
	@SerializedName("serverPort")
	private int serverPort;
	@SerializedName("duration")
	private int duration;
	@SerializedName("upBandwidth")
	private int upBandwidth;
	@SerializedName("upBufferSize")
	private int upBufferSize;
	@SerializedName("upPacketSize_B")
	private int upPacketSize_B;
	@SerializedName("telnetSetting")
	private TelnetSetting telnetSetting=new TelnetSetting();
	public String getTestMode() {
		return testMode;
	}
	public void setTestMode(String testMode) {
		this.testMode = testMode;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getServerIP() {
		return serverIP;
	}
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	public int getServerPort() {
		return serverPort;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
 
	public int getUpBandwidth() {
		return upBandwidth;
	}
	public void setUpBandwidth(int upBandwidth) {
		this.upBandwidth = upBandwidth;
	}
	public int getUpBufferSize() {
		return upBufferSize;
	}
	public void setUpBufferSize(int upBufferSize) {
		this.upBufferSize = upBufferSize;
	}
	public int getUpPacketSize_B() {
		return upPacketSize_B;
	}
	public void setUpPacketSize_B(int upPacketSize_B) {
		this.upPacketSize_B = upPacketSize_B;
	}
	public TelnetSetting getTelnetSetting() {
		return telnetSetting;
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
				if (tagName.equals("TestMode")) {
					this.setTestMode(parser.nextText());
				}else if (tagName.equals("Protocol")) {
					this.setProtocol(parser.nextText());
				}else if (tagName.equals("ServerIP")) {
					this.setServerIP(parser.nextText());
				}else if (tagName.equals("ServerPort")) {
					this.setServerPort(stringToInt(parser.nextText()));
				}else if (tagName.equals("Duration")) {
					this.setDuration(stringToInt(parser.nextText())/1000);
				}else if (tagName.equals("UDPBandwidth")) {
					this.setUpBandwidth(stringToInt(parser.nextText()));
				}else if (tagName.equals("UDPBufferSize")) {
					this.setUpBufferSize(stringToInt(parser.nextText()));
				}else if (tagName.equals("UDPPacketSize_B")) {
					this.setUpPacketSize_B(stringToInt(parser.nextText()));
				}else if (tagName.equals("TelnetSetting")) { 
					telnetSetting.parseXml(parser);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("iPerfTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception { 
		serializer.startTag(null, "iPerfTestConfig");
		this.writeTag(serializer, "TestMode", this.testMode);
		this.writeTag(serializer, "Protocol", this.protocol);
		this.writeTag(serializer, "ServerIP", this.serverIP);
		this.writeTag(serializer, "ServerPort", this.serverPort);
		this.writeTag(serializer, "Duration", this.duration*1000);
		this.writeTag(serializer, "UDPBandwidth", this.upBandwidth);
		this.writeTag(serializer, "UDPBufferSize", this.upBufferSize);
		this.writeTag(serializer, "UDPPacketSize_B", this.upPacketSize_B);
		if(null!=telnetSetting){
			telnetSetting.writeXml(serializer);
		}
		
		serializer.endTag(null, "iPerfTestConfig");
	}
}
