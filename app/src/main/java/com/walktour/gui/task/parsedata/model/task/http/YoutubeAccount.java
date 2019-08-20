package com.walktour.gui.task.parsedata.model.task.http;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class YoutubeAccount extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 6147812033240079750L;

	@SerializedName("userName")
	private String userName = "dinglicom.com1@gmail.com";
	@SerializedName("password")
	private String password = "dinglicom";

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
	
	public void parseXml(XmlPullParser parser) throws Exception {
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			String attName = parser.getAttributeName(i);
			if (attName.equals("Password")) {
				this.setPassword(parser.getAttributeValue(i));
			} else if (attName.equals("Username")) {
				this.setUserName(parser.getAttributeValue(i));
			}
		}
	}
	
	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "YoutubeAccount");
		this.writeAttribute(serializer, "Password", this.password);
		this.writeAttribute(serializer, "Username", this.userName);
		serializer.endTag(null, "YoutubeAccount");

	}
}
