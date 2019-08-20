package com.walktour.gui.task.parsedata.model.task.moc;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/***
 * 使用随机事件测试
 * 
 * @author weirong.fan
 *
 */
public class UseRandomTimeDial extends TaskBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2659511006321691932L;
	@SerializedName("isAvailable")
	private boolean isAvailable = false;
	@SerializedName("maxDuration")
	private int maxDuration = 0;
	@SerializedName("minDuration")
	private int minDuration = 0;

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public int getMaxDuration() {
		return maxDuration;
	}

	public void setMaxDuration(int maxDuration) {
		this.maxDuration = maxDuration;
	}

	public int getMinDuration() {
		return minDuration;
	}

	public void setMinDuration(int minDuration) {
		this.minDuration = minDuration;
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
				if (tagName.equals("MaxDuration")) {
					this.setMaxDuration(stringToInt(parser.nextText()));
				} else if (tagName.equals("MinDuration")) {
					this.setMinDuration(stringToInt(parser.nextText()));
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("UseRandomTimeDial")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "UseRandomTimeDial");
		this.writeAttribute(serializer, "IsAvailable", this.isAvailable);
		this.writeTag(serializer, "MaxDuration", maxDuration);
		this.writeTag(serializer, "MinDuration", minDuration);
		serializer.endTag(null, "UseRandomTimeDial");
	}
}
