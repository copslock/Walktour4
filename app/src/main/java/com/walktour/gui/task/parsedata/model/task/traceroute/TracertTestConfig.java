package com.walktour.gui.task.parsedata.model.task.traceroute;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class TracertTestConfig  extends TaskBase {
	private static final long serialVersionUID = 3117722009136107696L;
	/***主机地址*/
	@SerializedName("Address")
	private String Address="";
	@SerializedName("PacketSize_B")
	private int PacketSize_B=64;
	@SerializedName("HopProbeNumber")
	private int HopProbeNumber=1;
	@SerializedName("HopTimeout")
	private int HopTimeout=5000;
	@SerializedName("HopInterval")
	private int HopInterval=1000;

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public int getPacketSize_B() {
		return PacketSize_B;
	}

	public void setPacketSize_B(int packetSize_B) {
		PacketSize_B = packetSize_B;
	}

	public int getHopProbeNumber() {
		return HopProbeNumber;
	}

	public void setHopProbeNumber(int hopProbeNumber) {
		HopProbeNumber = hopProbeNumber;
	}

	public int getHopTimeout() {
		return HopTimeout;
	}

	public void setHopTimeout(int hopTimeout) {
		HopTimeout = hopTimeout;
	}

	public int getHopInterval() {
		return HopInterval;
	}

	public void setHopInterval(int hopInterval) {
		HopInterval = hopInterval;
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
				if (tagName.equals("Address")) {
					this.setAddress(parser.nextText());
				} else if (tagName.equals("PacketSize_B")) {
					this.setPacketSize_B(stringToInt(parser.nextText()));
				} else if (tagName.equals("HopProbeNumber")) {
					this.setHopProbeNumber(stringToInt(parser.nextText()));
				} else if (tagName.equals("HopTimeout")) {
					this.setHopTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("HopInterval")) {
					this.setHopInterval(stringToInt(parser.nextText())/1000);
				}  

				break;
			case XmlPullParser.END_TAG: 
				tagName = parser.getName();
				if (tagName.equals("TracertTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}
	
	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "TracertTestConfig");
		
		this.writeTag(serializer, "Address", this.Address);
		this.writeTag(serializer, "PacketSize_B", this.PacketSize_B);
		this.writeTag(serializer, "HopProbeNumber", this.HopProbeNumber);
		this.writeTag(serializer, "HopTimeout", this.HopTimeout*1000);
		this.writeTag(serializer, "HopInterval", this.HopInterval*1000);

		serializer.endTag(null, "TracertTestConfig");
	}
	
	
}
