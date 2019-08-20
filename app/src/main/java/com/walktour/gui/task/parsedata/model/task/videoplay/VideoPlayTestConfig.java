package com.walktour.gui.task.parsedata.model.task.videoplay;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.base.URLInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.LinkedList;
import java.util.List;

public class VideoPlayTestConfig extends TaskBase {
	private static final long serialVersionUID = -8386686748670805433L;
	@SerializedName("playVideo")
	private boolean playVideo;
	@SerializedName("saveDirectory")
	private String saveDirectory;
	@SerializedName("psCallMode")
	private String psCallMode = PSCALLMODE_FILE;
	@SerializedName("playDuration")
	private int playDuration;
	@SerializedName("playPercentage")
	private int playPercentage;
	@SerializedName("maxBufferCounts")
	private int maxBufferCounts = 5;
	@SerializedName("bufferTimeBy")
	private String bufferTimeBy = BUFFERTIMEBY_TIME;
	@SerializedName("maxBufferTimeout")
	private int maxBufferTimeout = 600;
	@SerializedName("maxBufferPercentage")
	private int maxBufferPercentage;
	@SerializedName("bufferTotalTime")
	private int bufferTotalTime = 600;
	@SerializedName("playThreshold")
	private int playThreshold = 4;
	@SerializedName("isSaveVideo")
	private boolean isSaveVideo;
	@SerializedName("playTimeout")
	private int playTimeout = 300;
	@SerializedName("noDataTimeout")
	private int noDataTimeout = 60;
	@SerializedName("websiteType")
	private String websiteType = VIDEOTYPE_YOUTUBE;
	@SerializedName("mediaQuality")
	private String mediaQuality = "1080P";
	@SerializedName("protocol")
	private String protocol = "TCP";
	@SerializedName("playTimeBy")
	private String playTimeBy = PLAYTIMEBY_DURATION;
	@SerializedName("playerType")
	private String playerType =  PLAYER_TYPE_DINGLI;
	/**
	 * 参数用于选择HPD地址和HLS地址。1:HPD，2:HLS。默认为1
	 */
	@SerializedName("streamType")
	private int streamType = 1;
	@SerializedName("urlList")
	private List<URLInfo> urlList = new LinkedList<URLInfo>();
	
	/** 播放器类型：鼎利 */
	public static final String PLAYER_TYPE_DINGLI = "DingliPlayer";
	/** 播放器类型：vitamio */
	public static final String PLAYER_TYPE_VITAMIO = "Vitamio";
	/** 播放器类型：youtube */
	public static final String PLAYER_TYPE_YOUTUBE = "YouTuBeSDK";
	public final static String VIDEOTYPE_YOUTUBE = "Youtube";
	public final static String VIDEOTYPE_YOUKU = "Youku";
	public final static String VIDEOTYPE_FACEBOOK = "Facebook";
	public final static String VIDEOTYPE_IFENG = "ifeng";
	public final static String VIDEOTYPE_SOHU = "Sohu";
	public final static String VIDEOTYPE_IQIYI = "iQIYI";
	public final static String VIDEOTYPE_OTHER = "Other";
	
	
	public final static String PSCALLMODE_FILE = "By File";

	public final static String PSCALLMODE_TIME = "By Time";
	
	public final static String  PLAYTIMEBY_DURATION = "Duration";
	
	public final static String  PLAYTIMEBY_PERCENTAGE = "Percentage";
	
	public final static String  BUFFERTIMEBY_TIME = "Time";
	
	public final static String  BUFFERTIMEBY_PERCENT = "Percent";
	

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

	public String getPsCallMode() {
		return psCallMode;
	}

	public void setPsCallMode(String psCallMode) {
		this.psCallMode = psCallMode;
	}

	public int getPlayDuration() {
		return playDuration;
	}

	public void setPlayDuration(int playDuration) {
		this.playDuration = playDuration;
	}

	public int getPlayPercentage() {
		return playPercentage;
	}

	public void setPlayPercentage(int playPercentage) {
		this.playPercentage = playPercentage;
	}

	public int getMaxBufferCounts() {
		return maxBufferCounts;
	}

	public void setMaxBufferCounts(int maxBufferCounts) {
		this.maxBufferCounts = maxBufferCounts;
	}

	public String getBufferTimeBy() {
		return bufferTimeBy;
	}

	public void setBufferTimeBy(String bufferTimeBy) {
		this.bufferTimeBy = bufferTimeBy;
	}

	public int getMaxBufferTimeout() {
		return maxBufferTimeout;
	}

	public void setMaxBufferTimeout(int maxBufferTimeout) {
		this.maxBufferTimeout = maxBufferTimeout;
	}

	public int getMaxBufferPercentage() {
		return maxBufferPercentage;
	}

	public void setMaxBufferPercentage(int maxBufferPercentage) {
		this.maxBufferPercentage = maxBufferPercentage;
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

	public int getPlayTimeout() {
		return playTimeout;
	}

	public void setPlayTimeout(int playTimeout) {
		this.playTimeout = playTimeout;
	}

	public int getNoDataTimeout() {
		return noDataTimeout;
	}

	public void setNoDataTimeout(int noDataTimeout) {
		this.noDataTimeout = noDataTimeout;
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

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getPlayTimeBy() {
		return playTimeBy;
	}

	public void setPlayTimeBy(String playTimeBy) {
		this.playTimeBy = playTimeBy;
	}

	public List<URLInfo> getUrlList() {
		return urlList;
	}

	public void setUrlList(List<URLInfo> urlList) {
		this.urlList = urlList;
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
				} else if (tagName.equals("PlayPercentage")) {
					this.setPlayPercentage(stringToInt(parser.nextText()));
				} else if (tagName.equals("MaxBufferCounts")) {
					this.setMaxBufferCounts(stringToInt(parser.nextText()));
				} else if (tagName.equals("BufferTimeBy")) {
					this.setBufferTimeBy(parser.nextText());
				} else if (tagName.equals("MaxBufferTimeout")) {
					this.setMaxBufferTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("MaxBufferPercentage")) {
					this.setMaxBufferPercentage(stringToInt(parser.nextText()));
				} else if (tagName.equals("BufferTotalTime")) {
					this.setBufferTotalTime(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("PlayThreshold")) {
					this.setPlayThreshold(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("IsSaveVideo")) {
					this.setSaveVideo(stringToBool(parser.nextText()));
				} else if (tagName.equals("PlayerType")) {
					this.setPlayerType(parser.nextText());
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
				} else if (tagName.equals("PlayTimeBy")) {
					this.setPlayTimeBy(parser.nextText());
				} else if (tagName.equals("URLInfo")) {
					URLInfo urlInfo = new URLInfo();
					urlInfo.parseXml(parser);
					this.getUrlList().add(urlInfo);
				} else if(tagName.equals("StreamType")){
					this.setStreamType(stringToInt(parser.nextText()));
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("VideoPlayTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "VideoPlayTestConfig");
		this.writeTag(serializer, "PlayVideo", this.playVideo);
		this.writeTag(serializer, "SaveDirectory", this.saveDirectory);
		this.writeTag(serializer, "PSCallMode", this.psCallMode);
		this.writeTag(serializer, "PlayDuration", this.playDuration*1000);
		this.writeTag(serializer, "PlayPercentage", this.playPercentage);

		this.writeTag(serializer, "MaxBufferCounts", this.maxBufferCounts);
		this.writeTag(serializer, "BufferTimeBy", this.bufferTimeBy);
		this.writeTag(serializer, "MaxBufferTimeout", this.maxBufferTimeout*1000);
		this.writeTag(serializer, "MaxBufferPercentage", this.maxBufferPercentage);
		this.writeTag(serializer, "BufferTotalTime", this.bufferTotalTime*1000);
		this.writeTag(serializer, "PlayThreshold", this.playThreshold*1000);
		this.writeTag(serializer, "IsSaveVideo", this.isSaveVideo);
		this.writeTag(serializer, "PlayerType", this.playerType);
		this.writeTag(serializer, "PlayTimeout", this.playTimeout*1000);
		this.writeTag(serializer, "StreamType", this.streamType);

		this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout*1000);
		this.writeTag(serializer, "WebsiteType", this.websiteType);
		this.writeTag(serializer, "MediaQuality", this.mediaQuality);
		this.writeTag(serializer, "Protocol", this.protocol);
		this.writeTag(serializer, "PlayTimeBy", this.playTimeBy);

		serializer.startTag(null, "URLList");
		for (URLInfo urlInfo : urlList) {
			urlInfo.writeXml(serializer);
		}
		serializer.endTag(null, "URLList");
		serializer.endTag(null, "VideoPlayTestConfig");
	}

	public String getPlayerType() {
		return playerType;
	}

	public void setPlayerType(String playerType) {
		this.playerType = playerType;
	}

	public int getStreamType() {
		return streamType;
	}

	public void setStreamType(int streamType) {
		this.streamType = streamType;
	}

}
