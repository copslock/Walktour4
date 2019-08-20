package com.walktour.gui.task.parsedata.model.task.http;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class ProxySetting extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1654928376273604712L;
	/**代理服务器地址**/
	@SerializedName("proxyIP")
	private String proxyIP;
	/**代理服务器密码**/
	@SerializedName("password")
	private String password;
	@SerializedName("proxyType")
	private String proxyType;
	/**代理服务器用户名**/
	@SerializedName("userName")
	private String userName;
	/**代理服务器端口**/
	@SerializedName("proxyPort")
	private int proxyPort;

	public String getProxyIP() {
		return proxyIP;
	}

	public void setProxyIP(String proxyIP) {
		this.proxyIP = proxyIP;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProxyType() {
		return proxyType;
	}

	public void setProxyType(String proxyType) {
		this.proxyType = proxyType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public void parseXml(XmlPullParser parser) throws Exception {
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			String attName = parser.getAttributeName(i);
			if (attName.equals("ProxyIP")) {
				this.setProxyIP(parser.getAttributeValue(i));
			} else if (attName.equals("Password")) {
				this.setPassword(parser.getAttributeValue(i));
			} else if (attName.equals("ProxyType")) {
				this.setProxyType(parser.getAttributeValue(i));
			} else if (attName.equals("Username")) {
				this.setUserName(parser.getAttributeValue(i));
			} else if (attName.equals("ProxyPort")) {
				this.setProxyPort(stringToInt(parser.getAttributeValue(i)));
			}
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "ProxySetting");
		this.writeAttribute(serializer, "ProxyIP", this.proxyIP);
		this.writeAttribute(serializer, "Password", this.password);
		this.writeAttribute(serializer, "ProxyType", this.proxyType);
		this.writeAttribute(serializer, "Username", this.userName);
		this.writeAttribute(serializer, "ProxyPort", this.proxyPort);
		serializer.endTag(null, "ProxySetting");

	}
}
