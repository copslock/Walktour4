package com.walktour.gui.task.parsedata.model.task.facebook;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.base.MyAccount;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class FacebookTestConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 6997189131055647518L;
	/** 发送的图片大小等级:小(0.5M) */
	public static final String SEND_PIC_SIZE_LEVEL_SMALL = "Small";
	/** 发送的图片大小等级:中(1.5M) */
	public static final String SEND_PIC_SIZE_LEVEL_MIDDLE = "Middle";
	/** 发送的图片大小等级:大(3M) */
	public static final String SEND_PIC_SIZE_LEVEL_LARGE = "Large";
	/** appID */
	@SerializedName("appId")
	private String appId;
	/** app密钥 */
	@SerializedName("appSecret")
	private String appSecret;
	/** 发送的文本内容 */
	@SerializedName("sendContent")
	private String sendContent;
	/** 发送的图片大小等级 */
	@SerializedName("sendPicSizeLevel")
	private String sendPicSizeLevel = SEND_PIC_SIZE_LEVEL_SMALL;
	/** 用户账号 */
	@SerializedName("myAccount")
	private MyAccount myAccount = new MyAccount();

	public MyAccount getMyAccount() {
		return myAccount;
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
				if (tagName.equals("AppId")) {
					this.setAppId(parser.nextText());
				} else if (tagName.equals("AppSecret")) {
					this.setAppSecret(parser.nextText());
				} else if (tagName.equals("SendContent")) {
					this.setSendContent(parser.nextText());
				} else if (tagName.equals("SendPicSizeLevel")) {
					this.setSendPicSizeLevel(parser.nextText());
				} else if (tagName.equals("MyAccount")) {
					myAccount.parseXml(parser);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("FacebookTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "FacebookTestConfig");

		this.writeTag(serializer, "AppId", this.appId);
		this.writeTag(serializer, "AppSecret", this.appSecret);
		this.writeTag(serializer, "SendContent", this.sendContent);
		this.writeTag(serializer, "SendPicSizeLevel", this.sendPicSizeLevel);
		if (null != myAccount) {
			myAccount.writeXml(serializer);
		}
		serializer.endTag(null, "FacebookTestConfig");
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getSendContent() {
		return sendContent;
	}

	public void setSendContent(String sendContent) {
		this.sendContent = sendContent;
	}

	public void setMyAccount(MyAccount myAccount) {
		this.myAccount = myAccount;
	}

	public String getSendPicSizeLevel() {
		return sendPicSizeLevel;
	}

	public void setSendPicSizeLevel(String sendPicSizeLevel) {
		this.sendPicSizeLevel = sendPicSizeLevel;
	}

}
