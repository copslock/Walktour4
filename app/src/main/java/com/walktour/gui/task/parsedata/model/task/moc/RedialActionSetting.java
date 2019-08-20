package com.walktour.gui.task.parsedata.model.task.moc;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class RedialActionSetting extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -748980726547524944L;
	@SerializedName("isAvailable")
	private boolean isAvailable = false;
	@SerializedName("callDropAction")
	private int callDropAction = 0;
	@SerializedName("callDropWait")
	private int callDropWait = 0;
	@SerializedName("accessFailAction")
	private int accessFailAction = 0;
	@SerializedName("accessFailWait")
	private int accessFailWait = 0;

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public int getCallDropAction() {
		return callDropAction;
	}

	public void setCallDropAction(int callDropAction) {
		this.callDropAction = callDropAction;
	}

	public int getCallDropWait() {
		return callDropWait;
	}

	public void setCallDropWait(int callDropWait) {
		this.callDropWait = callDropWait;
	}

	public int getAccessFailAction() {
		return accessFailAction;
	}

	public void setAccessFailAction(int accessFailAction) {
		this.accessFailAction = accessFailAction;
	}

	public int getAccessFailWait() {
		return accessFailWait;
	}

	public void setAccessFailWait(int accessFailWait) {
		this.accessFailWait = accessFailWait;
	}

	public void parseXmlMocTest(XmlPullParser parser) throws Exception {
		int eventType = parser.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("CallDropAction")) {
					this.setCallDropWait(stringToInt(parser.nextText()));
				} else if (tagName.equals("CallDropWait")) {
					this.setCallDropWait(stringToInt(parser.nextText()));
				} else if (tagName.equals("AccessFailAction")) {
					this.setAccessFailAction(stringToInt(parser.nextText()));
				} else if (tagName.equals("AccessFailWait")) {
					this.setAccessFailWait(stringToInt(parser.nextText()));
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("RedialActionSetting")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "RedialActionSetting");
		this.writeAttribute(serializer, "IsAvailable", this.isAvailable);
		this.writeTag(serializer, "CallDropAction", this.callDropAction);
		this.writeTag(serializer, "CallDropWait", this.callDropWait);
		this.writeTag(serializer, "AccessFailAction", this.accessFailAction);
		this.writeTag(serializer, "AccessFailWait", this.accessFailWait);
		serializer.endTag(null, "RedialActionSetting");
	}
}
