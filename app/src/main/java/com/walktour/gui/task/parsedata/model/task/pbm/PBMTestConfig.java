package com.walktour.gui.task.parsedata.model.task.pbm;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.LinkedList;
import java.util.List;

public class PBMTestConfig extends TaskBase {
	private static final long serialVersionUID = -5757694252328698526L;
	/** 业务时长(秒) */
	@SerializedName("duration")
	private int duration;
	/** 无数据超时时间(秒) */
	@SerializedName("noDataTimeout")
	private int noDataTimeout = 10;
	/** 上行带宽采样占比，范围[1, 100]%，默认值8 */
	@SerializedName("upSampleRatio")
	private int upSampleRatio = 8;
	/** 下行带宽采样占比，范围[1, 100]%，默认值6 */
	@SerializedName("downSampleRatio")
	private int downSampleRatio = 6;

	@SerializedName("serverList")
	private List<Server> serverList = new LinkedList<Server>();

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getNoDataTimeout() {
		return noDataTimeout;
	}

	public void setNoDataTimeout(int noDataTimeout) {
		this.noDataTimeout = noDataTimeout;
	}

	public int getDownSampleRatio() {
		return downSampleRatio;
	}

	public void setDownSampleRatio(int downSampleRatio) {
		this.downSampleRatio = downSampleRatio;
	}

	public int getUpSampleRatio() {
		return upSampleRatio;
	}

	public void setUpSampleRatio(int upSampleRatio) {
		this.upSampleRatio = upSampleRatio;
	}

	public List<Server> getServerList() {
		return serverList;
	}

	public void setServerList(List<Server> serverList) {
		this.serverList = serverList;
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
				} else if (tagName.equals("NoDataTimeout")) {
					this.setNoDataTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("DownSampleRatio")) {
					this.setDownSampleRatio(stringToInt(parser.nextText()));
				} else if (tagName.equals("UpSampleRatio")) {
					this.setUpSampleRatio(stringToInt(parser.nextText()));
				} else if (tagName.equals("Server")) {
					Server server = new Server();
					server.parseXml(parser);
					this.getServerList().add(server);
				}

				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("PBMTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "PBMTestConfig");
		this.writeTag(serializer, "Duration", this.duration*1000);
		this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout*1000);
		this.writeTag(serializer, "DownSampleRatio", this.downSampleRatio);
		this.writeTag(serializer, "UpSampleRatio", this.upSampleRatio);
		serializer.startTag(null, "ServerList");
		for (Server server : serverList) {
			server.writeXml(serializer);
		}
		serializer.endTag(null, "ServerList");
		serializer.endTag(null, "PBMTestConfig");
	}
}
