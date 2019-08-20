package com.walktour.gui.task.parsedata.model.task.base;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class FansAccount extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 8931520379658520059L;
	@SerializedName("password")
	private String password;
	@SerializedName("nickName")
	private String nickName;
	@SerializedName("userName")
	private String userName;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void parseXml(XmlPullParser parser) {
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			String attName = parser.getAttributeName(i);
			if (attName.equals("Password")) {
				this.setPassword(parser.getAttributeValue(i));
			} else if (attName.equals("Nickname")) {
				this.setNickName(parser.getAttributeValue(i));
			} else if (attName.equals("Username")) {
				this.setUserName(parser.getAttributeValue(i));
			}

		}
	}
	
	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null,"FansAccount");
		this.writeAttribute(serializer, "Password", this.password);
		this.writeAttribute(serializer, "Nickname", this.nickName);
		this.writeAttribute(serializer, "Username", this.userName);
		serializer.endTag(null,"FansAccount");
		
	}
}
