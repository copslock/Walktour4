package com.walktour.gui.task.parsedata.model.task.weibo;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.base.FansAccount;
import com.walktour.gui.task.parsedata.model.task.base.MyAccount;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class WeiboTestConfig extends TaskBase {
	private static final long serialVersionUID = 1648111439391301951L;
	@SerializedName("testType")
	private String testType;
	/** 自己账户 **/
	@SerializedName("myAccount")
	private MyAccount myAccount = new MyAccount();
	/** 粉丝账户 **/
	@SerializedName("fansAccount")
	private FansAccount fansAccount = new FansAccount();
	/** 图片路径 */
	@SerializedName("sendFile")
	private String sendFile;
	/** 登录时间 **/
	@SerializedName("loginTimeout")
	private int loginTimeout;
	/** 发送时间 **/
	@SerializedName("sendTimeout")
	private int sendTimeout;

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	public MyAccount getMyAccount() {
		return myAccount;
	}

	public FansAccount getFansAccount() {
		return fansAccount;
	}

	public String getSendFile() {
		return sendFile;
	}

	public void setSendFile(String sendFile) {
		this.sendFile = sendFile;
	}

	public int getLoginTimeout() {
		return loginTimeout;
	}

	public void setLoginTimeout(int loginTimeout) {
		this.loginTimeout = loginTimeout;
	}

	public int getSendTimeout() {
		return sendTimeout;
	}

	public void setSendTimeout(int sendTimeout) {
		this.sendTimeout = sendTimeout;
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
				if (tagName.equals("TestType")) {
					this.setTestType(parser.nextText());
				} else if (tagName.equals("MyAccount")) {
					myAccount.parseXml(parser);
				} else if (tagName.equals("FansAccount")) {
					fansAccount.parseXml(parser);
				} else if (tagName.equals("SendFile")) {
					this.setSendFile(parser.nextText());
				} else if (tagName.equals("LogINTimeout")) {
					this.setLoginTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("SendTimeout")) {
					this.setSendTimeout(stringToInt(parser.nextText())/1000);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("WeiboTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "WeiboTestConfig");
		this.writeTag(serializer, "TestType", this.testType);
		if (null != myAccount) {
			myAccount.writeXml(serializer);
		}
		if (null != fansAccount) {
			fansAccount.writeXml(serializer);
		}
		this.writeTag(serializer, "SendFile", this.sendFile);
		this.writeTag(serializer, "LogINTimeout", this.loginTimeout*1000);
		this.writeTag(serializer, "SendTimeout", this.sendTimeout*1000);
		serializer.endTag(null, "WeiboTestConfig");
	}
}
