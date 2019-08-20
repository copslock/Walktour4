/**
 * 
 */
package com.walktour.gui.task.parsedata.model.task.videostreaming;

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

/***
 * 流媒体测试参数对象类
 * 
 * @author weirong.fan
 *
 */
public class TaskStreamModel extends TaskModel{

	private static final long serialVersionUID = -4010867048368225976L;

	public TaskStreamModel() {
		super();
		setTaskName("Video Streaming");
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		setInterVal(15);
		setRepeat(10);
		setDisConnect(1);
		setTaskType(WalkStruct.TaskType.Stream.toString());
	}

//	// 播放模式，是否开启 PSCAll
//	private boolean psCall = true;
//
//	// 播放时长
//	private String mPlayTime = "300";

	// 流媒体URL
	@SerializedName("mURL")
	private String mURL = "rtsp://116.6.50.82/xx.3gp"; // "http://211.136.165.53/wap/neirjd/rcyw/zbpd/dfws/index.jsp"

//	// 是否使用TCP连接
//	private boolean mUseTCP = false;
//
//	// 接收超时
//	private String mNodataTimeout = "30";
	@SerializedName("videoType")
	private int videoType;
	@SerializedName("videoQuality")
	private int videoQuality;

	/** 保存媒体数据 */
	@SerializedName("isSaveVideo")
	private boolean isSaveVideo = false;

//	/** 是否显示视频 */
//	private boolean isShowVideo = false;

	/** 保存通信过程中的协议信息 */
	@SerializedName("isSaveprotol")
	private boolean isSaveprotol = false;

//	private String bufferTime = "60";
//
//	private String bufferPlay = "5";
	@SerializedName("cacheSize")
	private String cacheSize;
	@SerializedName("videoStreamingTestConfig")
	private VideoStreamingTestConfig videoStreamingTestConfig = new VideoStreamingTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();
	
	
	private final static String CALLMODE_FILE = "By File"; 
	
	private final static String CALLMODE_TIME = "By Time"; 

	private final static String PROTOCOL_TCP = "TCP"; 
	
	private final static String PROTOCOL_UDP = "UDP"; 

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());

		testTask.append("psCall =" + isPsCall() + "\r\n");
		testTask.append("PlayTime =" + getmPlayTime() + "\r\n");
		testTask.append("URL =" + getmURL() + "\r\n");
		testTask.append("UseTCP =" + ismUseTCP() + "\r\n");

		testTask.append("NodataTimeout =" + getmNodataTimeout() + "\r\n");
		testTask.append("VideoType =" + videoType + "\r\n");
		testTask.append("VideoQuality =" + videoQuality + "\r\n");
		testTask.append("SaveVideo =" + isSaveVideo + "\r\n");
		testTask.append("BufferPlay =" + getBufferPlay() + "\r\n");
		testTask.append("BufferTime =" + getBufferTime() + "\r\n");
		return testTask.toString();
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

	public String getmURL() {
		List<URLInfo> urlInfos = videoStreamingTestConfig.getUrlList();
		if (urlInfos != null && urlInfos.size() != 0){
			for (int i = 0; i < urlInfos.size(); i++) {
				if(urlInfos.get(i).isCheck()){
					mURL = urlInfos.get(i).getUrl();
					return mURL;
				}
			}
		}
		return mURL;
	}

	public void setmURL(String mURL) {
		List<URLInfo> urlInfos = videoStreamingTestConfig.getUrlList();
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
	@Override
	public int getTypeProperty() { 
		if(networkConnectionSetting.getConnectionProtocol().equals(NetworkConnectionSetting.ConnectionProtocol_WLAN))
			return WalkCommonPara.TypeProperty_Wlan;
		return WalkCommonPara.TypeProperty_Net;
	}



	@Override
	public void setTypeProperty(int typeProperty) {
		if(typeProperty==WalkCommonPara.TypeProperty_Wlan){
			networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_WLAN);
		}else{
			networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_PPP);
		} 
	}
	public boolean ismUseTCP() {
		if(videoStreamingTestConfig.getProtocol().equalsIgnoreCase(PROTOCOL_TCP)){
			return true;
		}
		return false;
	}

	public void setmUseTCP(boolean mUseTCP) {
		if(mUseTCP){
			videoStreamingTestConfig.setProtocol(PROTOCOL_TCP);
		}else{
			videoStreamingTestConfig.setProtocol(PROTOCOL_UDP);
		}
	}

	public String getmPlayTime() {
		return videoStreamingTestConfig.getPlayTimeout()+"";
	}

	public void setmPlayTime(String mPlayTime) {
		try {
			videoStreamingTestConfig.setPlayTimeout(Integer.valueOf(mPlayTime.trim()));
		} catch (Exception e) {
			e.printStackTrace();
			videoStreamingTestConfig.setPlayTimeout(300);
		}
	}

	public String getmNodataTimeout() {
		return videoStreamingTestConfig.getNoDataTimeout() +"";
	}

	public void setmNodataTimeout(String mNodataTimeout) {
		try {
			videoStreamingTestConfig.setNoDataTimeout(Integer.valueOf(mNodataTimeout.trim()));
		} catch (Exception e) {
			e.printStackTrace();
			videoStreamingTestConfig.setNoDataTimeout(15);
		}
	}

	public boolean isPsCall() {
		if(videoStreamingTestConfig.getPsCallMode().equalsIgnoreCase(CALLMODE_FILE)){
			return true;
		}
		return false;
	}

	public void setPsCall(boolean psCall) {
		if(psCall){
			videoStreamingTestConfig.setPsCallMode(CALLMODE_FILE);
		}else{
			videoStreamingTestConfig.setPsCallMode(CALLMODE_TIME);
		}
	}

	public boolean isSaveprotol() {
		return isSaveprotol;
	}

	public void setSaveprotol(boolean isSaveprotol) {
		this.isSaveprotol = isSaveprotol;
	}

	/**
	 * @return the videoType
	 */
	public int getVideoType() {
		return videoType;
	}

	/**
	 * @param videoType
	 *            the videoType to set
	 */
	public void setVideoType(int videoType) {
		this.videoType = videoType;
	}

	/**
	 * @return the videoQuality
	 */
	public int getVideoQuality() {
		return videoQuality;
	}

	/**
	 * @param videoQuality
	 *            the videoQuality to set
	 */
	public void setVideoQuality(int videoQuality) {
		this.videoQuality = videoQuality;
	}

	/**
	 * @return the bufferTime
	 */
	public String getBufferTime() {
		return videoStreamingTestConfig.getBufferTotalTime()+"";
	}

	/**
	 * @param bufferTime
	 *            the bufferTime to set
	 */
	public void setBufferTime(String bufferTime) {
		try {
			videoStreamingTestConfig.setBufferTotalTime(Integer.valueOf(bufferTime));
		} catch (Exception e) {
			e.printStackTrace();
			videoStreamingTestConfig.setBufferTotalTime(60);
		}
	}

	/**
	 * @return the bufferPlay
	 */
	public String getBufferPlay() {
		
		return videoStreamingTestConfig.getPlayThreshold()+"";
	}

	/**
	 * @param bufferPlay
	 *            the bufferPlay to set
	 */
	public void setBufferPlay(String bufferPlay) {
		try {
			videoStreamingTestConfig.setPlayThreshold(Integer.valueOf(bufferPlay));
		} catch (Exception e) {
			e.printStackTrace();
			videoStreamingTestConfig.setPlayThreshold(5);
		}
	}

	/**
	 * @return the isSaveVideo
	 */
	public boolean isSaveVideo() {
		return videoStreamingTestConfig.isSaveVideo();
	}

	/**
	 * @param isSaveVideo
	 *            the isSaveVideo to set
	 */
	public void setSaveVideo(boolean isSaveVideo) {
		videoStreamingTestConfig.setSaveVideo(isSaveVideo);
	}

	public String getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(String cacheSize) {
		this.cacheSize = cacheSize;
	}

	public boolean isShowVideo() {
		return videoStreamingTestConfig.isSaveVideo();
	}

	public void setShowVideo(boolean isShowVideo) {
		videoStreamingTestConfig.setPlayVideo(isShowVideo);
	}

	public VideoStreamingTestConfig getVideoStreamingTestConfig() {
		return videoStreamingTestConfig;
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
				if (tagName.equals("VideoStreamingTestConfig")) {
					videoStreamingTestConfig.parseXml(parser);
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
		if (null != videoStreamingTestConfig)
			videoStreamingTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}
	
	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.Stream.getXmlTaskType();
	}
}
