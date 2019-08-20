package com.walktour.gui.task.parsedata.model.task.base;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class GatewaySetting extends TaskBase{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 6605491837815494815L;

	/**网关服务器地址*/
	@SerializedName("gateWayIp")
	private String gateWayIp="";
	@SerializedName("wapVersion")
	private String wapVersion="2.0";
	/**网关服务器端口*/
	@SerializedName("gateWayPort")
	private int gateWayPort=0;

	public String getGateWayIp() {
		return gateWayIp;
	}

	public void setGateWayIp(String gateWayIp) {
		this.gateWayIp = gateWayIp;
	}

	public String getWapVersion() {
		return wapVersion;
	}

	public void setWapVersion(String wapVersion) {
		this.wapVersion = wapVersion;
	}

	public int getGateWayPort() {
		return gateWayPort;
	}

	public void setGateWayPort(int gateWayPort) {
		this.gateWayPort = gateWayPort;
	}
	
	
	public void parseXml(XmlPullParser parser) throws Exception {
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			String attName = parser.getAttributeName(i);
			if (attName.equals("GatewayIP")) {
				this.setGateWayIp(parser.getAttributeValue(i));
			} else if (attName.equals("WAPVersion")) {
				this.setWapVersion(parser.getAttributeValue(i));
			} else if (attName.equals("GatewayPort")) {
				this.setGateWayPort(stringToInt(parser.getAttributeValue(i)));
			}
		}
	}
	
	public void writeXml(XmlSerializer serializer)  throws Exception {
		serializer.startTag(null,"GatewaySetting");
		
		this.writeAttribute(serializer, "GatewayIP", this.getGateWayIp());
		this.writeAttribute(serializer, "WAPVersion", this.wapVersion);
		this.writeAttribute(serializer, "GatewayPort", this.getGateWayPort());
		serializer.endTag(null,"GatewaySetting");
	}
}
