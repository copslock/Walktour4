package com.walktour.gui.task.parsedata.model.task.attach;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class AttachTestConfig extends TaskBase { 

	private static final long serialVersionUID = 9153604967984099899L;
	@SerializedName("keepTime")
	private int keepTime=10; // 持续时间
	@SerializedName("timeout")
	private int timeout=15;
	@SerializedName("port")
	private int port;
	@SerializedName("baudRate")
	private int baudRate=57600;

 

	public int getKeepTime() {
		return keepTime;
	}

	public void setKeepTime(int keepTime) {
		this.keepTime = keepTime;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getBaudRate() {
		return baudRate;
	}

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
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
				if (tagName.equals("Duration")) {
					this.setKeepTime(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("Timeout")) {
					this.setTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("Port")) {
					this.setPort(stringToInt(parser.nextText()));
				} else if (tagName.equals("BaudRate")) {
					this.setBaudRate(stringToInt(parser.nextText()));
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("AttachTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "AttachTestConfig");
		this.writeTag(serializer, "Duration", this.keepTime*1000);
		this.writeTag(serializer, "Timeout", this.timeout*1000);
		this.writeTag(serializer, "Port", this.port);
		this.writeTag(serializer, "BaudRate", this.baudRate);
		serializer.endTag(null, "AttachTestConfig");
	}

}
