package com.walktour.gui.task.parsedata.model.task.pdpactive;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class PDPActiveTestConfig extends TaskBase {
	private static final long serialVersionUID = -6662731983091798114L;
	@SerializedName("duration")
	private int duration;
	@SerializedName("timeout")
	private int timeout;
	@SerializedName("port")
	private int port = 2;
	@SerializedName("baudRate")
	private int baudRate = 57600;

	@SerializedName("qoSRequest")
	private QoSRequest qoSRequest=new QoSRequest();

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
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

	public QoSRequest getQoSRequest() {
		return qoSRequest;
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
					this.setDuration(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("Timeout")) {
					this.setTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("Port")) {
					this.setPort(stringToInt(parser.nextText()));
				} else if (tagName.equals("BaudRate")) {
					this.setBaudRate(stringToInt(parser.nextText()));
				} else if (tagName.equals("QoSRequest")) { 
					qoSRequest.parseXml(parser);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("PDPActiveTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "PDPActiveTestConfig");
		this.writeTag(serializer, "Duration", this.duration*1000);
		this.writeTag(serializer, "Timeout", this.timeout*1000);
		this.writeTag(serializer, "Port", this.port);
		this.writeTag(serializer, "BaudRate", this.baudRate);
		if (null != qoSRequest) {
			qoSRequest.writeXml(serializer);
		}
		serializer.endTag(null, "PDPActiveTestConfig");
	}
}
