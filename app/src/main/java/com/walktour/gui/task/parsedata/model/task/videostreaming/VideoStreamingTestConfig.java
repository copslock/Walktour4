package com.walktour.gui.task.parsedata.model.task.videostreaming;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.base.URLInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.LinkedList;
import java.util.List;

public class VideoStreamingTestConfig extends TaskBase {
	private static final long serialVersionUID = 5297586340498712516L;
	@SerializedName("playVideo")
	private boolean playVideo;
	@SerializedName("saveDirectory")
	private String saveDirectory;
	@SerializedName("websiteType")
	private String websiteType;
	@SerializedName("mediaQuality")
	private String mediaQuality;
	@SerializedName("bufferTotalTime")
	private int bufferTotalTime;
	@SerializedName("playThreshold")
	private int playThreshold;
	@SerializedName("isSaveVideo")
	private boolean isSaveVideo;
	@SerializedName("noDataTimeout")
	private int noDataTimeout;
	@SerializedName("protocol")
	private String protocol = "";
	@SerializedName("playTimeout")
	private int playTimeout;
	@SerializedName("playDuration")
	private int playDuration;
	@SerializedName("psCallMode")
	private String psCallMode = "";

	@SerializedName("urlList")
	private List<URLInfo> urlList = new LinkedList<URLInfo>();

	public boolean isPlayVideo() {
		return playVideo;
	}

	public void setPlayVideo(boolean playVideo) {
		this.playVideo = playVideo;
	}

	public String getSaveDirectory() {
		return saveDirectory;
	}

	public void setSaveDirectory(String saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	public String getWebsiteType() {
		return websiteType;
	}

	public void setWebsiteType(String websiteType) {
		this.websiteType = websiteType;
	}

	public String getMediaQuality() {
		return mediaQuality;
	}

	public void setMediaQuality(String mediaQuality) {
		this.mediaQuality = mediaQuality;
	}

	public int getBufferTotalTime() {
		return bufferTotalTime;
	}

	public void setBufferTotalTime(int bufferTotalTime) {
		this.bufferTotalTime = bufferTotalTime;
	}

	public int getPlayThreshold() {
		return playThreshold;
	}

	public void setPlayThreshold(int playThreshold) {
		this.playThreshold = playThreshold;
	}

	public boolean isSaveVideo() {
		return isSaveVideo;
	}

	public void setSaveVideo(boolean isSaveVideo) {
		this.isSaveVideo = isSaveVideo;
	}

	public int getNoDataTimeout() {
		return noDataTimeout;
	}

	public void setNoDataTimeout(int noDataTimeout) {
		this.noDataTimeout = noDataTimeout;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public int getPlayTimeout() {
		return playTimeout;
	}

	public void setPlayTimeout(int playTimeout) {
		this.playTimeout = playTimeout;
	}

	public int getPlayDuration() {
		return playDuration;
	}

	public void setPlayDuration(int playDuration) {
		this.playDuration = playDuration;
	}

	public String getPsCallMode() {
		return psCallMode;
	}

	public void setPsCallMode(String psCallMode) {
		this.psCallMode = psCallMode;
	}

	public List<URLInfo> getUrlList() {
		return urlList;
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
				if (tagName.equals("PlayVideo")) {
					this.setPlayVideo(stringToBool(parser.nextText()));
				} else if (tagName.equals("SaveDirectory")) {
					this.setSaveDirectory(parser.nextText());
				} else if (tagName.equals("PSCallMode")) {
					this.setPsCallMode(parser.nextText());
				} else if (tagName.equals("PlayDuration")) {
					this.setPlayDuration(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("BufferTotalTime")) {
					this.setBufferTotalTime(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("PlayThreshold")) {
					this.setPlayThreshold(stringToInt(parser.nextText()));
				} else if (tagName.equals("IsSaveVideo")) {
					this.setSaveVideo(stringToBool(parser.nextText()));
				} else if (tagName.equals("PlayTimeout")) {
					this.setPlayTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("NoDataTimeout")) {
					this.setNoDataTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("WebsiteType")) {
					this.setWebsiteType(parser.nextText());
				} else if (tagName.equals("MediaQuality")) {
					this.setMediaQuality(parser.nextText());
				} else if (tagName.equals("Protocol")) {
					this.setProtocol(parser.nextText());
				} else if (tagName.equals("URLInfo")) {
					URLInfo urlInfo = new URLInfo();
					urlInfo.parseXml(parser);
					this.getUrlList().add(urlInfo);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("VideoStreamingTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "VideoStreamingTestConfig");
		this.writeTag(serializer, "PlayVideo", this.playVideo);
		this.writeTag(serializer, "SaveDirectory", this.saveDirectory);
		this.writeTag(serializer, "WebsiteType", this.websiteType);
		this.writeTag(serializer, "MediaQuality", this.mediaQuality);
		this.writeTag(serializer, "BufferTotalTime", this.bufferTotalTime*1000);
		this.writeTag(serializer, "PlayThreshold", this.playThreshold);
		this.writeTag(serializer, "IsSaveVideo", this.isSaveVideo);
		this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout*1000);
		this.writeTag(serializer, "Protocol", this.protocol);
		this.writeTag(serializer, "PlayTimeout", this.playTimeout*1000);
		this.writeTag(serializer, "PlayDuration", this.playDuration*1000);
		this.writeTag(serializer, "PSCallMode", this.psCallMode);

		serializer.startTag(null, "URLList");
		for (URLInfo urlInfo : urlList) {
			urlInfo.writeXml(serializer);
		}
		serializer.endTag(null, "URLList");
		serializer.endTag(null, "VideoStreamingTestConfig");
	}

}
