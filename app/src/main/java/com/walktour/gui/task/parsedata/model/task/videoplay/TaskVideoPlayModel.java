package com.walktour.gui.task.parsedata.model.task.videoplay;

import android.annotation.SuppressLint;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;
import com.walktour.gui.task.parsedata.model.task.base.URLInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

/**
 * 视频播放实体模型
 *
 * @author Xie Jihong
 */
@SuppressLint("DefaultLocale")
public class TaskVideoPlayModel extends TaskModel {
	private static final long serialVersionUID = 8755929809543663822L;
	/** 播放模式：文件 */
	public static final int PLAY_TYPE_FILE = 0;
	/** 播放模式：时间 */
	public static final int PLAY_TYPE_TIME = 1;
	/** 播放器类型：鼎利 */
	public static final int PLAYER_TYPE_DINGLI = 1;
	/** 播放器类型：youtube */
	public static final int PLAYER_TYPE_YOUTUBE = 2;
	/** 播放器类型：vitamio */
	public static final int PLAYER_TYPE_VITAMIO = 3;
	/** 视频类型：youtube */
	public static final int VIDEO_TYPE_YOUTUBE = 0;
	/** 视频类型：youku */
	public static final int VIDEO_TYPE_YOUKU = 1;
	/** 视频类型：sohu */
	public static final int VIDEO_TYPE_SOHU = 2;
	/** 视频类型：iqiyi */
	public static final int VIDEO_TYPE_IQIYI = 3;
	/** 视频类型：other */
	public static final int VIDEO_TYPE_OTHER = 4;
	// /** 播放器类型 */
	// private int playerType = PLAYER_TYPE_DINGLI;
	// /** 视频类型 */
	// @Deprecated
	// private int videoType;
	// /** 视频质量 */
	// private int videoQuality = 1;
	// /** 视频地址 */
	// private String url = "";//
	// "http://v.youku.com/v_show/id_XMjU1ODQ0Nzk2.html";//"http://www.youtube.com/watch?v=d0xLhU_79Jk";
	// /** 播放模式 */
	// private int playType = PLAY_TYPE_FILE;
	// /** 当按时间时，需要可选择播放记时方式 */
	// private int playTimerMode;
	// /** 最大缓冲次数 */
	// private int maxBufCounts = 5;
	// /** 缓冲记时方式 */
	// private int bufTimerMode;
	// /** 最大缓冲时间 */
	// private int maxBufTime = 600;
	// /** 接收超时或播放时长 */
	// private int playTimeout = 300;
	// /** 无流量超时 */
	// private int noDataTimeout = 60;
	// /** 缓冲区总时长 */
	// private int bufTime = 600;
	// /** 缓冲播放门限 */
	// private int bufThred = 2;
	// /** 超时时长 */
	//	private int timeout = 0;
	// /**
	// * 是否保存视频
	// */
	// private boolean save;
	@SerializedName("videoPlayTestConfig")
	private VideoPlayTestConfig videoPlayTestConfig = new VideoPlayTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();

	public TaskVideoPlayModel() {
		super();
		setTaskName("Video Play");
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		setRepeat(10); // 重复次数
		setInterVal(15); // 每次间隔时间
		setDisConnect(1);// 拨号规则，默认每次断开
		setTaskType(WalkStruct.TaskType.HTTPVS.toString());
	}

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 *
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());
		switch (this.getPlayerType()) {
		case PLAYER_TYPE_DINGLI:
			testTask.append("PlayerType=Dingli\r\n");
			break;
		case PLAYER_TYPE_VITAMIO:
			testTask.append("PlayerType=Vitamio\r\n");
			break;
		case PLAYER_TYPE_YOUTUBE:
			testTask.append("PlayerType=Youtube\r\n");
			break;
		default:
			testTask.append("PlayerType=Unknown\r\n");
			break;
		}
		testTask.append("VideoType=").append(getVideoType()).append("\r\n");
		testTask.append("VideoQuality=").append(videoPlayTestConfig.getMediaQuality()).append("\r\n");
		testTask.append("Url=").append(getUrl()).append("\r\n");
		testTask.append("PlayType=").append(getPlayType()).append("\r\n");
		testTask.append("PlayTimerMode=").append(getPlayTimerMode()).append("\r\n");
		testTask.append("MaxBufCounts=").append(getMaxBufCounts()).append("\r\n");
		testTask.append("BufTimerMode=").append(getBufTimerMode()).append("\r\n");
		testTask.append("MaxBufferPercentage=").append(this.getMaxBufferPercentage()).append("\r\n");
		testTask.append("MaxBufferTimeout=").append(this.getMaxBufferTimeout()).append("\r\n");
		testTask.append("PlayTimeout=").append(getPlayTimeout()).append("\r\n");
		testTask.append("NoDataTimeout=").append(getNoDataTimeout()).append("\r\n");
		testTask.append("BufTime=").append(getBufTime()).append("\r\n");
		testTask.append("BufThred=").append(getBufThred()).append("\r\n");
		testTask.append("StreamType=").append(getStreamType()).append("\r\n");
		testTask.append("Save=").append(isSave()).append("\r\n");

		return testTask.toString();
	}

	@Override
	public int getTypeProperty() {
		if(networkConnectionSetting.getConnectionProtocol().equals(NetworkConnectionSetting.ConnectionProtocol_WLAN))
			return WalkCommonPara.TypeProperty_Wlan;
		else if(networkConnectionSetting.getConnectionProtocol().equals(NetworkConnectionSetting.ConnectionProtocol_PPPNB)){
			return WalkCommonPara.TypeProperty_Ppp;
		}
		return WalkCommonPara.TypeProperty_Net;
	}



	@Override
	public void setTypeProperty(int typeProperty) {
		if(typeProperty==WalkCommonPara.TypeProperty_Wlan){
			networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_WLAN);
		} else if(typeProperty==WalkCommonPara.TypeProperty_Ppp){
			networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_PPPNB);
		}else{
			networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_PPP);
		}
	}

	/***
	 * 数据业务复写这两个方法，以适配历史的业务
	 */
	@Override
	public int getDisConnect() {
		return networkConnectionSetting.getDisConnect();
	}

	public void setDisConnect(int disConnect) {
		networkConnectionSetting.setDisConnect(disConnect);
	}

	@Deprecated
	public int getVideoType() {
		String videoType = videoPlayTestConfig.getWebsiteType();
		if (videoType.equalsIgnoreCase(VideoPlayTestConfig.VIDEOTYPE_YOUTUBE)) {
			return 0;
		} else if (videoType.equalsIgnoreCase(VideoPlayTestConfig.VIDEOTYPE_YOUKU)) {
			return 1;
		} else if (videoType.equalsIgnoreCase(VideoPlayTestConfig.VIDEOTYPE_SOHU)) {
			return 2;
		} else if (videoType.equalsIgnoreCase(VideoPlayTestConfig.VIDEOTYPE_IQIYI)) {
			return 3;
		} else {
			return 4;
		}
	}

	@Deprecated
	public void setVideoType(int videoType) {
		switch (videoType) {
		case 0:
			videoPlayTestConfig.setWebsiteType(VideoPlayTestConfig.VIDEOTYPE_YOUTUBE);
			break;
		case 1:
			videoPlayTestConfig.setWebsiteType(VideoPlayTestConfig.VIDEOTYPE_YOUKU);
			break;
		case 2:
			videoPlayTestConfig.setWebsiteType(VideoPlayTestConfig.VIDEOTYPE_SOHU);
			break;
		case 3:
			videoPlayTestConfig.setWebsiteType(VideoPlayTestConfig.VIDEOTYPE_IQIYI);
			break;
		default:
			videoPlayTestConfig.setWebsiteType(VideoPlayTestConfig.VIDEOTYPE_OTHER);
			break;
		}
	}

	public int getVideoQuality() {
		String[] qualityArray = StringSpecialInit.getInstance().getVideoQualityArray();
		int index = 0;
		for (String quality : qualityArray) {
			if (quality.equalsIgnoreCase(videoPlayTestConfig.getMediaQuality())) {
				break;
			}
			index++;
		}

		return index;
	}

	public void setVideoQuality(int videoQuality) {
		String[] qualityArray = StringSpecialInit.getInstance().getVideoQualityArray();
		videoPlayTestConfig.setMediaQuality(qualityArray[videoQuality]);
	}

	public String getUrl() {
		String mURL = "";
		List<URLInfo> urlInfos = videoPlayTestConfig.getUrlList();
		if (urlInfos != null && urlInfos.size() != 0) {
			for (int i = 0; i < urlInfos.size(); i++) {
				if (urlInfos.get(i).isCheck()) {
					mURL = urlInfos.get(i).getUrl();
					return mURL;
				}
			}
		}
		return mURL;
	}

	public void setUrl(String mURL) {
		List<URLInfo> urlInfos = videoPlayTestConfig.getUrlList();
		boolean isExist = false;
		if (urlInfos != null && urlInfos.size() != 0) {
			for (URLInfo urlInfo : urlInfos) {
				urlInfo.setCheck(false);
				if (urlInfo.getUrl().equalsIgnoreCase(mURL)) {
					isExist = true;
					urlInfo.setCheck(true);
				}
			}
		}
		if (urlInfos != null && !isExist) {
			URLInfo urlInfo = new URLInfo();
			urlInfo.setCheck(true);
			urlInfo.setUrl(mURL);
			urlInfos.add(urlInfo);
		}
	}

	public int getPlayType() {
		if (videoPlayTestConfig.getPsCallMode().equalsIgnoreCase(VideoPlayTestConfig.PSCALLMODE_FILE)) {
			return 0;
		}
		return 1;
	}

	public void setPlayType(int playType) {
		switch (playType) {
		case 0:
			videoPlayTestConfig.setPsCallMode(VideoPlayTestConfig.PSCALLMODE_FILE);
			break;
		default:
			videoPlayTestConfig.setPsCallMode(VideoPlayTestConfig.PSCALLMODE_TIME);
			break;
		}
	}

	public int getPlayTimerMode() {
		if (videoPlayTestConfig.getPlayTimeBy().equalsIgnoreCase(VideoPlayTestConfig.PLAYTIMEBY_DURATION)) {
			return 0;
		}
		return 1;
	}

	public void setPlayTimerMode(int playTimerMode) {
		switch (playTimerMode) {
		case 0:
			videoPlayTestConfig.setPlayTimeBy(VideoPlayTestConfig.PLAYTIMEBY_DURATION);
			break;
		default:
			videoPlayTestConfig.setPlayTimeBy(VideoPlayTestConfig.PLAYTIMEBY_PERCENTAGE);
			break;
		}
	}

	public int getMaxBufCounts() {
		return videoPlayTestConfig.getMaxBufferCounts();
	}

	public void setMaxBufCounts(int maxBufCounts) {
		videoPlayTestConfig.setMaxBufferCounts(maxBufCounts);
	}

	public int getBufTimerMode() {
		if (videoPlayTestConfig.getBufferTimeBy().equalsIgnoreCase(VideoPlayTestConfig.BUFFERTIMEBY_TIME)) {
			return 0;
		}
		return 1;
	}

	public void setBufTimerMode(int bufTimerMode) {
		switch (bufTimerMode) {
		case 0:
			videoPlayTestConfig.setBufferTimeBy(VideoPlayTestConfig.BUFFERTIMEBY_TIME);
			break;
		default:
			videoPlayTestConfig.setBufferTimeBy(VideoPlayTestConfig.BUFFERTIMEBY_PERCENT);
			break;
		}
	}

	public int getMaxBufferPercentage(){
		return videoPlayTestConfig.getMaxBufferPercentage();
	}

	public int getMaxBufferTimeout(){
		return videoPlayTestConfig.getMaxBufferTimeout();
	}

	public void setMaxBufferPercentage(int maxBufferPercentage){
		videoPlayTestConfig.setMaxBufferPercentage(maxBufferPercentage);
	}

	public void setMaxBufferTimeout(int maxBufferTimeout){
		videoPlayTestConfig.setMaxBufferTimeout(maxBufferTimeout);
	}

	public int getPlayTimeout() {
		return videoPlayTestConfig.getPlayTimeout();
	}

	public void setPlayTimeout(int playTimeout){
		videoPlayTestConfig.setPlayTimeout(playTimeout);
	}

	public void setPlayDuration(int playDuration){
		videoPlayTestConfig.setPlayDuration(playDuration);
	}

	public void setPlayPercentage(int playPercentage){
		videoPlayTestConfig.setPlayPercentage(playPercentage);
	}

	public int getPlayDuration(){
		return videoPlayTestConfig.getPlayDuration();
	}

	public int getPlayPercentage(){
		return videoPlayTestConfig.getPlayPercentage();
	}

	public int getNoDataTimeout() {
		return videoPlayTestConfig.getNoDataTimeout();
	}

	public void setNoDataTimeout(int noDataTimeout) {
		videoPlayTestConfig.setNoDataTimeout(noDataTimeout);
	}

	public int getBufTime() {
		return videoPlayTestConfig.getBufferTotalTime();
	}

	public void setBufTime(int bufTime) {
		videoPlayTestConfig.setBufferTotalTime(bufTime);
	}

	public int getBufThred() {
		return videoPlayTestConfig.getPlayThreshold();
	}

	public void setBufThred(int bufThred) {
		videoPlayTestConfig.setPlayThreshold(bufThred);
	}

	public boolean isSave() {
		return videoPlayTestConfig.isSaveVideo();
	}

	public void setSave(boolean save) {
		videoPlayTestConfig.setSaveVideo(save);
	}

	public boolean isVideoShow() {
		return videoPlayTestConfig.isPlayVideo();
	}

	public void setVideoShow(boolean videoShow) {
		videoPlayTestConfig.setPlayVideo(videoShow);
	}

	public int getStreamType() {
		return videoPlayTestConfig.getStreamType();
	}

	public void setStreamType(int streamType) {
		videoPlayTestConfig.setStreamType(streamType);
	}

	public NetworkConnectionSetting getNetworkConnectionSetting() {
		return networkConnectionSetting;
	}

	public void parseXml(XmlPullParser parser, List<TaskModel> tasks, Map<String, String> map) throws Exception {
		int eventType = parser.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("VideoPlayTestConfig")) {
					videoPlayTestConfig.parseXml(parser);
				} else if (tagName.equals("NetworkConnectionSetting")) {
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						String attName = parser.getAttributeName(i);
						String attValue = parser.getAttributeValue(i);
						if (attName.equals("IsAvailable")) {
							networkConnectionSetting.setAvailable(stringToBool(attValue));
						}
					}
					networkConnectionSetting.parseXml(parser);

				}

				else {// 解析公共属性
					parsrXmlPublic(parser, map);
				}
				break;
			case XmlPullParser.END_TAG:

				tagName = parser.getName();
				if (tagName.equals("TaskConfig")) {
					tasks.add(this);
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	@Override
	public void writeXml(XmlSerializer serializer) throws Exception {
		super.writeXml(serializer);
		if (null != videoPlayTestConfig)
			videoPlayTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}

	@Override
	public String getServerTaskType() {
		return WalkStruct.TaskType.HTTPVS.getXmlTaskType();
	}

	public int getPlayerType() {
		if (videoPlayTestConfig.getPlayerType().equals(VideoPlayTestConfig.PLAYER_TYPE_DINGLI)) {
			return PLAYER_TYPE_DINGLI;
		} else if (videoPlayTestConfig.getPlayerType().equals(VideoPlayTestConfig.PLAYER_TYPE_YOUTUBE)) {
			return PLAYER_TYPE_YOUTUBE;
		} else {
			return PLAYER_TYPE_VITAMIO;
		}
	}

	public void setPlayerType(int playerType) {
		switch (playerType) {
		case PLAYER_TYPE_DINGLI:
			videoPlayTestConfig.setPlayerType(VideoPlayTestConfig.PLAYER_TYPE_DINGLI);
			break;
		case PLAYER_TYPE_YOUTUBE:
			videoPlayTestConfig.setPlayerType(VideoPlayTestConfig.PLAYER_TYPE_YOUTUBE);
			break;
		default:
			videoPlayTestConfig.setPlayerType(VideoPlayTestConfig.PLAYER_TYPE_VITAMIO);
			break;
		}
	}
}
