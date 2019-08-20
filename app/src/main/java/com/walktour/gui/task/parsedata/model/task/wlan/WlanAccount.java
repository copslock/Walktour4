package com.walktour.gui.task.parsedata.model.task.wlan;

import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class WlanAccount extends TaskBase {
	private static final long serialVersionUID = -3844376114660266585L;
	/*** 登录Wlanr的用户名 */
	private String password;
	/*** 登录Wlan的密码 */
	private String username;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 解析 WlanAccount结点
	 * 
	 * @param parser
	 * @throws Exception
	 */
	public void parseXml(XmlPullParser parser) throws Exception {
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			String attName = parser.getAttributeName(i);
			String attValue = parser.getAttributeValue(i);
			if (attName.equals("Password")) {
				this.setPassword(attValue);
			} else if (attName.equals("Username")) {
				this.setUsername(attValue);
			}
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "WlanAccount");
		this.writeAttribute(serializer, "Password", this.password);
		this.writeAttribute(serializer, "Username", this.username);
		serializer.endTag(null, "WlanAccount");
	}
}
