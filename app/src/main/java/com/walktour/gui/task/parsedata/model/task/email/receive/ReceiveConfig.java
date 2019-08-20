package com.walktour.gui.task.parsedata.model.task.email.receive;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class ReceiveConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5177765217771045158L;
	/**服务器使用的加密协议**/
	public static final String PROTOCOL_NONE="None";
	public static final String PROTOCOL_SSL="SSL";
	/**服务器类型**/
	public static final String TYPE_POP3="POP3";
	public static final String TYPE_IMAP="IMAP";
	@SerializedName("serverAddress")
	private String serverAddress="";
	@SerializedName("serverPort")
	private int serverPort=0;
	/**加密协议  安全连接类型(0:None 1: SSL 2: STARTTLS）**/
	@SerializedName("securityProtocol")
	private String securityProtocol=PROTOCOL_SSL;
	/**服务器类型**/
	@SerializedName("serverType")
	private String serverType=TYPE_POP3;

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getSecurityProtocol() {
		return securityProtocol;
	}

	public void setSecurityProtocol(String securityProtocol) {
		this.securityProtocol = securityProtocol;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	
	public void parseXml(XmlPullParser parser) throws Exception {
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			String attName = parser.getAttributeName(i);
			if (attName.equals("ServerAddress")) {
				this.setServerAddress(parser.getAttributeValue(i));
			} else if (attName.equals("ServerPort")) {
				this.setServerPort(stringToInt(parser.getAttributeValue(i)));
			} else if (attName.equals("SecurityProtocol")) {
				this.setSecurityProtocol(parser.getAttributeValue(i));
			} else if (attName.equals("ServerType")) {
				this.setServerType(parser.getAttributeValue(i));
			}
		}
	}
	
	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "ReceiveConfig");

		this.writeAttribute(serializer, "ServerAddress", this.serverAddress);
		this.writeAttribute(serializer, "ServerPort", this.serverPort);
		this.writeAttribute(serializer, "SecurityProtocol", this.securityProtocol);
		this.writeAttribute(serializer, "ServerType", this.serverType);
		
		serializer.endTag(null, "ReceiveConfig");
	}
}
