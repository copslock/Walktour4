package com.walktour.gui.task.parsedata.model.task.email.send;

import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class SendConfig extends TaskBase{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 6624509273937260031L;
	/**服务器使用的加密协议**/
	public static final String PROTOCOL_NONE="None";
	public static final String PROTOCOL_SSL="SSL";
	public static final String PROTOCOL_TLS="TLS";
//	/**服务器类型**/
//	public static final String TYPE_POP3="POP3";
//	public static final String TYPE_IMAP="IMAP";
	
	private int serverPort;
	/**加密协议  安全连接类型(0:None 1: SSL 2: STARTTLS）**/
	private String securityProtocol=PROTOCOL_SSL;

	private String serverAddress="";
	/**服务器类型**/
//	private String serverType=TYPE_POP3;
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

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

//	public String getServerType() {
//		return serverType;
//	}
//
//	public void setServerType(String serverType) {
//		this.serverType = serverType;
//	}

	public void parseXml(XmlPullParser parser) throws Exception {
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			String attName = parser.getAttributeName(i);
			if (attName.equals("ServerAddress")) {
				this.setServerAddress(parser.getAttributeValue(i));
			} else if (attName.equals("ServerPort")) {
				this.setServerPort(stringToInt(parser.getAttributeValue(i)));
			} else if (attName.equals("SecurityProtocol")) {
				this.setSecurityProtocol(parser.getAttributeValue(i));
			}
//			else if (attName.equals("ServerType")) {
//				this.setServerType(parser.getAttributeValue(i));
//			}
		}
	}
	
	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "SendConfig");

		this.writeAttribute(serializer, "ServerPort", this.serverPort);
		this.writeAttribute(serializer, "SecurityProtocol", this.securityProtocol);
		this.writeAttribute(serializer, "ServerAddress", this.serverAddress);
//		this.writeAttribute(serializer, "ServerType", this.serverType);
		
		serializer.endTag(null, "SendConfig");
	}
}
