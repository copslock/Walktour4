package com.walktour.gui.task.parsedata.model.task.base;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class WifiConnectionInfo extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -6817387594426659139L;

	@SerializedName("password")
	private String password;
	@SerializedName("userName")
	private String userName;
	@SerializedName("isCheck")
	private boolean isCheck;
	@SerializedName("apName")
	private String apName;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public void parseXml(XmlPullParser parser) throws Exception {
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			String attName = parser.getAttributeName(i);
			if (attName.equals("Password")) {
				this.setPassword(parser.getAttributeValue(i));
			} else if (attName.equals("Username")) {
				this.setUserName(parser.getAttributeValue(i));
			} else if (attName.equals("APName")) {
				this.setApName(parser.getAttributeValue(i));
			} else if (attName.equals("IsCheck")) {
				this.setCheck(stringToBool(parser.getAttributeValue(i)));
			}

		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "WifiConnectionInfo");

		this.writeAttribute(serializer, "Password", this.password);
		this.writeAttribute(serializer, "Username", this.userName);
		this.writeAttribute(serializer, "IsCheck", this.isCheck);
		this.writeAttribute(serializer, "APName", this.apName);

		serializer.endTag(null, "WifiConnectionInfo");
	}
}
