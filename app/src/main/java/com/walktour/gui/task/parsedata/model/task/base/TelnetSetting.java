package com.walktour.gui.task.parsedata.model.task.base;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class TelnetSetting extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 8840506863883947247L;

	@SerializedName("address")
	private String address;
	@SerializedName("port")
	private int port;
	@SerializedName("userName")
	private String userName;
	@SerializedName("password")
	private String password;
	@SerializedName("iPerfPath")
	private String iPerfPath;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getiPerfPath() {
		return iPerfPath;
	}

	public void setiPerfPath(String iPerfPath) {
		this.iPerfPath = iPerfPath;
	}

	// <TelnetSetting Address="1.1.1.1" Port="22" Username="dl" Password="dl"
	// iPerfPath="/Data/Tool/iPerf/iPerf.exe"/>
	public void parseXml(XmlPullParser parser) {
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			String attName = parser.getAttributeName(i);
			if (attName.equals("Address")) {
				this.setAddress(parser.getAttributeValue(i));
			} else if (attName.equals("Port")) {
				this.setPort(stringToInt(parser.getAttributeValue(i)));
			} else if (attName.equals("Username")) {
				this.setUserName(parser.getAttributeValue(i));
			} else if (attName.equals("Password")) {
				this.setPassword(parser.getAttributeValue(i));
			} else if (attName.equals("iPerfPath")) {
				this.setiPerfPath(parser.getAttributeValue(i));
			}

		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "TelnetSetting");
		this.writeAttribute(serializer, "Address", this.address);
		this.writeAttribute(serializer, "Port", this.port);
		this.writeAttribute(serializer, "Username", this.userName);
		this.writeAttribute(serializer, "Password", this.password);
		this.writeAttribute(serializer, "iPerfPath", this.iPerfPath);
		serializer.endTag(null, "TelnetSetting");

	}

}
