package com.walktour.gui.task.parsedata.model.task.ott;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/**
 * MultipleAppTest测试业务对象
 *
 * @author bin.li
 *
 */
public class MultipleAppTestConfig extends TaskBase {

	private static final long serialVersionUID = 7562116665155365075L;

	/** 发送的图片大小:1M */
	public static final String SEND_PICTURE_TYPE_1M = "0";
	/** 发送的图片大小:3M */
	public static final String SEND_PICTURE_TYPE_3M = "1";

	/**业务名称*/
	@SerializedName("mMultipleAppTestTaskName")
	private String mMultipleAppTestTaskName;

	/** 启动APP */
	@SerializedName("mStartAppMode")
	private int mStartAppMode;

	/** 发送文本 */
	@SerializedName("mSendText")
	private String mSendText;

	/** 发送语音时长(ms) */
	@SerializedName("mVoiceDuration")
	private int mVoiceDuration;

	/** 语音主被叫拨打模式 0:主叫 1：被叫*/
	@SerializedName("mAudioCallMode")
	private int mAudioCallMode;

	/** 语音拨打时长 s*/
	@SerializedName("mAudioCallSeconds")
	private int mAudioCallSeconds;

	/** 发送的图片大小等级 */
	@SerializedName("mSendPictureType")
	private String mSendPictureType = SEND_PICTURE_TYPE_1M;

	/** 业务超时(ms) */
	@SerializedName("mTaskTimeout")
	private int mTaskTimeout;

	/** 被叫好友名称*/
	@SerializedName("contactName")
	private String contactName;

	/** 通话模式：1:视频	2:音频*/
	@SerializedName("dialMode")
    private int dialMode;

	public int getDialMode(){
	    return dialMode;
    }

    public void setDialMode(int dialMode){
	    this.dialMode = dialMode;
    }

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}


	public String getMultipleAppTestTaskName() {
		return mMultipleAppTestTaskName;
	}
	public void setMultipleAppTestTaskName(String taskName) {
		mMultipleAppTestTaskName = taskName;
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

	public int getAudioCallMode() { return mAudioCallMode; }
	public void setAudioCallMode(int audioCallMode) { mAudioCallMode = audioCallMode; }

	public int getAudioCallSeconds() {
		return mAudioCallSeconds;
	}
	public void setAudioCallSeconds(int audioCallSeconds) { mAudioCallSeconds = audioCallSeconds; }

	public int getStartAppMode(){return mStartAppMode;}
	public void setStartAppMode(int start_app_mode){ mStartAppMode = start_app_mode;}

	public String getSendPictureType() { return mSendPictureType; }
	public void setSendPictureType(String sendPictureType) { mSendPictureType = sendPictureType; }

	public int getTaskTimeOut(){return mTaskTimeout;}
	public void setTaskTimeOut(int task_timeout){ mTaskTimeout = task_timeout;}

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
				if (tagName.equals("TaskName")) {
					this.setMultipleAppTestTaskName(parser.nextText());
				} else if (tagName.equals("ContactName")) {
					this.setContactName(parser.nextText());
				} else if (tagName.equals("StartAppMode")) {
					this.setStartAppMode(stringToInt(parser.nextText()));
				} else if (tagName.equals("SendText")) {
					this.setSendText(parser.nextText());
				} else if (tagName.equals("SendPictureType")) {
					this.setSendPictureType(parser.nextText());
				} else if (tagName.equals("VoiceDuration")) {
					this.setVoiceDuration(stringToInt(parser.nextText()));
				} else if (tagName.equals("TaskTimeout")) {
					this.setTaskTimeOut(stringToInt(parser.nextText()));
				}else if (tagName.equals("AudioCallMode")) {
					this.setAudioCallMode(stringToInt(parser.nextText()));
				}else if (tagName.equals("AudioCallSeconds")) {
					this.setAudioCallSeconds(stringToInt(parser.nextText()));
				}else if (tagName.equals("DialMode")) {
					this.setDialMode(stringToInt(parser.nextText()));
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("MultipleAppTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "MultipleAppTestConfig");
		this.writeTag(serializer, "TaskName", this.mMultipleAppTestTaskName);
		this.writeTag(serializer, "ContactName", this.contactName);
		this.writeTag(serializer, "StartAppMode", this.mStartAppMode);
		this.writeTag(serializer, "SendText", this.mSendText);
		this.writeTag(serializer, "SendPictureType", this.mSendPictureType);
		this.writeTag(serializer, "VoiceDuration", this.mVoiceDuration);
		this.writeTag(serializer, "TaskTimeout", this.mTaskTimeout);
		this.writeTag(serializer, "AudioCallMode", this.mAudioCallMode);
		this.writeTag(serializer, "AudioCallSeconds", this.mAudioCallSeconds);
		this.writeTag(serializer, "DialMode", this.dialMode);
		serializer.endTag(null, "MultipleAppTestConfig");
	}

}
