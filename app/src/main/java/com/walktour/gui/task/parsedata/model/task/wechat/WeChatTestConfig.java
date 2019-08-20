package com.walktour.gui.task.parsedata.model.task.wechat;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/**
 * 微信测试业务对象
 * 
 * @author jianchao.wang
 *
 */
public class WeChatTestConfig extends TaskBase {

	private static final long serialVersionUID = 2256874169901139703L;
	/** 测试模式:文本、图片、语音 */
	public static final String OPERATION_TYPE_DEFAULT = "All Types";
	/** 测试模式:图片 */
	public static final String OPERATION_TYPE_PICTURE = "Only Send Picture";
	/** 发送的图片大小:1M */
	public static final String SEND_PICTURE_TYPE_1M = "1M";
	/** 发送的图片大小:3M */
	public static final String SEND_PICTURE_TYPE_3M = "3M";
	/** 发送的图片大小:5M */
	public static final String SEND_PICTURE_TYPE_5M = "5M";
	/** 发送的图片大小:10M */
	public static final String SEND_PICTURE_TYPE_10M = "10M";
	/** 朋友昵称 */
	@SerializedName("mFriendName")
	private String mFriendName;
	/** 发送文本 */
	@SerializedName("mSendText")
	private String mSendText;
	/** 发送语音时长(ms) */
	@SerializedName("mVoiceDuration")
	private int mVoiceDuration;
	/** 发送的图片大小等级 */
	@SerializedName("mSendPictureType")
	private String mSendPictureType = SEND_PICTURE_TYPE_10M;
	/** 发送语音超时(ms) */
	@SerializedName("mSendTimeout")
	private int mSendTimeout;
	/** 测试模式 */
	@SerializedName("mOperationType")
	private String mOperationType = OPERATION_TYPE_DEFAULT;

	public String getFriendName() {
		return mFriendName;
	}

	public void setFriendName(String friendName) {
		mFriendName = friendName;
	}

	public String getSendText() {
		return mSendText;
	}

	public void setSendText(String sendText) {
		mSendText = sendText;
	}

	public int getVoiceDuration() {
		return mVoiceDuration;
	}

	public void setVoiceDuration(int voiceDuration) {
		mVoiceDuration = voiceDuration;
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
				if (tagName.equals("FriendName")) {
					this.setFriendName(parser.nextText());
				} else if (tagName.equals("SendText")) {
					this.setSendText(parser.nextText());
				} else if (tagName.equals("SendPictureType")) {
					this.setSendPictureType(parser.nextText());
				} else if (tagName.equals("VoiceDuration")) {
					this.setVoiceDuration(stringToInt(parser.nextText()));
				} else if (tagName.equals("SendTimeout")) {
					this.setSendTimeout(stringToInt(parser.nextText()));
				} else if (tagName.equals("OperationType")) {
					this.setOperationType(parser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("WeChatTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "WeChatTestConfig");
		this.writeTag(serializer, "OperationType", this.mOperationType);
		this.writeTag(serializer, "FriendName", this.mFriendName);
		this.writeTag(serializer, "SendText", this.mSendText);
		this.writeTag(serializer, "SendPictureType", this.mSendPictureType);
		this.writeTag(serializer, "VoiceDuration", this.mVoiceDuration);
		this.writeTag(serializer, "SendTimeout", this.mSendTimeout);
		serializer.endTag(null, "WeChatTestConfig");
	}

	public int getSendTimeout() {
		return mSendTimeout;
	}

	public void setSendTimeout(int sendTimeout) {
		mSendTimeout = sendTimeout;
	}

	public String getSendPictureType() {
		return mSendPictureType;
	}

	public void setSendPictureType(String sendPictureType) {
		mSendPictureType = sendPictureType;
	}

	public String getOperationType() {
		return mOperationType;
	}

	public void setOperationType(String operationType) {
		mOperationType = operationType;
	}

}
