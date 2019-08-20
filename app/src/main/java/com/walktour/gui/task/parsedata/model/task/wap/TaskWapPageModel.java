package com.walktour.gui.task.parsedata.model.task.wap;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;
import com.walktour.gui.task.parsedata.model.task.base.URLInfo;
import com.walktour.gui.task.parsedata.model.task.wap.down.WAPDownTestConfig;
import com.walktour.gui.task.parsedata.model.task.wap.page.WAPPageTestConfig;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

public class TaskWapPageModel extends TaskModel {

	private static final long serialVersionUID = 3464869148406860029L;
	public TaskWapPageModel(String taskType) {
		setTypeProperty(WalkCommonPara.TypeProperty_Wap);
		this.setTaskType(taskType);
	}

//	private String gateway; // 网关服务器地址
	@SerializedName("showPage")
	private int showPage; // 是否显示页面 0：不显示1：显示
	@SerializedName("refurbish")
	private int refurbish; // 刷新层数,大于0的整数
	@SerializedName("refreshcount")
	private int refreshcount; // 刷新次数
	@SerializedName("wapType")
	private int wapType; // Wap模式 下载类型： 0,Picture/ 1,Ring/ 2,Kjava
	@SerializedName("wapPageTestConfig")
	private WAPPageTestConfig wapPageTestConfig = new WAPPageTestConfig();
	@SerializedName("wapDownTestConfig")
	private WAPDownTestConfig wapDownTestConfig = new WAPDownTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();

	private static final String WAPTYPE_PICTURE = "Picture";
	private static final String WAPTYPE_RING = "Ring";
	private static final String WAPTYPE_KJAVA = "KJava";
	
	public static final String MODE_REFRESH = "Refresh";
	public static final String MODE_LOGIN = "Login";
	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @author tangwq
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());

		testTask.append("timeOut =" + getTimeOut() + "\r\n");
		testTask.append("gateway =" + getGateway() + "\r\n");
		testTask.append("port =" + getPort() + "\r\n");
		testTask.append("url =" + getUrl() + "\r\n");
		testTask.append("showPage =" + showPage + "\r\n");
		testTask.append("refurbish =" + getRefreshDepth() + "\r\n");
		testTask.append("refreshcount =" + refreshcount + "\r\n");
		testTask.append("wapType =" + getWapType() + "\r\n");
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
	/**
	 * 刷新层数，是指进入子页面的深度。 例如设定为3层时，打开wap.163.com为第1层，wap.163.com/sport为第2层，
	 * wap.163.com/sport/nba为第3层. 仅仅针对wap刷新测试
	 */
	public int getRefreshDepth() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case WapRefurbish:
			String refreshURLLayer = wapPageTestConfig.getRefreshURLLayer();
			if (refreshURLLayer != null && refreshURLLayer.trim().length() != 0){
				this.refurbish = Integer.valueOf(wapPageTestConfig.getRefreshURLLayer());
			}
			break;
		default:
			break;
		}
		return refurbish;
	}

	/**
	 * 刷新层数，是指进入子页面的深度。 例如设定为3层时，打开wap.163.com为第1层，wap.163.com/sport为第2层，
	 * wap.163.com/sport/nba为第3层. 仅仅针对wap刷新测试
	 */
	public void setRefreshDepth(int refurbish) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case WapRefurbish:
			wapPageTestConfig.setRefreshURLLayer(String.valueOf(refurbish));
			break;
		default:
			break;
		}
		this.refurbish = refurbish;
	}

	/**
	 * 刷新次数，页面要刷新的总次数，此属性在fleet5下载的测试计划中没有对应的属性。 仅仅针对wap刷新测试
	 */
	public int getRefreshcount() {
		return refreshcount;
	}

	/**
	 * 刷新次数，页面要刷新的总次数，此属性在fleet5下载的测试计划中没有对应的属性。 仅仅针对wap刷新测试
	 */
	public void setRefreshcount(int refreshcount) {
		this.refreshcount = refreshcount;
	}

	public int getWapType() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case WapDownload:
			String fileType = wapDownTestConfig.getDownloadFileType();
			if(fileType.equals(WAPTYPE_PICTURE)){
				return 0;
			}else if(fileType.equals(WAPTYPE_RING)){
				return 1;
			}else {
				return 2;
			}
		default:
			break;
		}
		return wapType;
	}

	public void setWapType(int wapType) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case WapDownload:
			switch (wapType) {
			case 0:
				wapDownTestConfig.setDownloadFileType(WAPTYPE_PICTURE);
				break;
			case 1:
				wapDownTestConfig.setDownloadFileType(WAPTYPE_RING);
				break;
			case 2:
				wapDownTestConfig.setDownloadFileType(WAPTYPE_KJAVA);
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}

	public int getTimeOut() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case WapDownload:
			return wapDownTestConfig.getDownloadTimeout();
		default:
			break;
		}
		return wapPageTestConfig.getPageTimeout();
		
	}

	public void setTimeOut(int timeOut) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case WapDownload:
			wapDownTestConfig.setDownloadTimeout(timeOut);
		default:
			wapPageTestConfig.setPageTimeout(timeOut);
			break;
		}
	}

	public String getGateway() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case WapDownload:
			return wapDownTestConfig.getGatewaySetting().getGateWayIp();
		default:
			break;
		}
		return wapPageTestConfig.getGatewaySetting().getGateWayIp();
	}

	public void setGateway(String gateway) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case WapDownload:
			wapDownTestConfig.getGatewaySetting().setGateWayIp(gateway);
			break;
		default:
			wapPageTestConfig.getGatewaySetting().setGateWayIp(gateway);
			break;
		}
	}

	public int getPort() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case WapDownload:
			return wapDownTestConfig.getGatewaySetting().getGateWayPort();
		default:
			break;
		}
		return wapPageTestConfig.getGatewaySetting().getGateWayPort();
		
	}

	public void setPort(int port) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case WapDownload:
			wapDownTestConfig.getGatewaySetting().setGateWayPort(port);
			wapDownTestConfig.getGatewaySetting().setWapVersion("2.0");
			break;
		default:
			wapPageTestConfig.getGatewaySetting().setGateWayPort(port);
			wapPageTestConfig.getGatewaySetting().setWapVersion("2.0");
			break;
		}
		
	}

	public String getUrl() {
		List<URLInfo> urlInfos;
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case WapDownload:
			urlInfos = wapDownTestConfig.getUrlList();
			if (urlInfos != null && urlInfos.size() > 0) {
				for (int i = 0; i < urlInfos.size(); i++) {
					if (urlInfos.get(i).isCheck()) {
						return urlInfos.get(i).getUrl();
					}
				}
			}
			break;
		default:
			urlInfos = wapPageTestConfig.getUrlList();
			if (urlInfos != null && urlInfos.size() > 0) {
				for (int i = 0; i < urlInfos.size(); i++) {
					if (urlInfos.get(i).isCheck()) {
						return urlInfos.get(i).getUrl();
					}
				}
			}
			break;
		}
		return "";
	}

	public void setUrl(String url) {
		URLInfo urlInfo;
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case WapDownload:
			wapDownTestConfig.getUrlList().clear();
			urlInfo = new URLInfo();
			urlInfo.setCheck(true);
			urlInfo.setUrl(url);
			wapDownTestConfig.getUrlList().add(urlInfo);
			break;
		default:
			wapPageTestConfig.getUrlList().clear();
			urlInfo = new URLInfo();
			urlInfo.setCheck(true);
			urlInfo.setUrl(url);
			wapPageTestConfig.getUrlList().add(urlInfo);
			break;
		}
	}

	public int getShowPage() {
		return showPage;
	}

	public void setShowPage(int showPage) {
		this.showPage = showPage;
	}

	public WAPPageTestConfig getWapPageTestConfig() {
		return wapPageTestConfig;
	}

	public NetworkConnectionSetting getNetworkConnectionSetting() {
		return networkConnectionSetting;
	}

	public WAPDownTestConfig getWapDownTestConfig() {
		return wapDownTestConfig;
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
				if (tagName.equals("WAPPageTestConfig")) {
					wapPageTestConfig.parseXml(parser);
				} else if (tagName.equals("WAPDownTestConfig")) {
					wapDownTestConfig.parseXml(parser);
				} else if (tagName.equals("NetworkConnectionSetting")) {
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						String attName = parser.getAttributeName(i);
						String attValue = parser.getAttributeValue(i);
						if (attName.equals("IsAvailable")) {
							networkConnectionSetting.setAvailable(stringToBool(attValue));
						}
					}
					networkConnectionSetting.parseXml(parser);

				} else {// 解析公共属性
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

	public void writeXml(XmlSerializer serializer) throws Exception {
		super.writeXml(serializer);
		if (this.getTaskType().equals(WalkStruct.TaskType.WapDownload.name())) {
			if (null != wapDownTestConfig)
				wapDownTestConfig.writeXml(serializer);
		} else {
			if (null != wapPageTestConfig)
				wapPageTestConfig.writeXml(serializer);
		}
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}

	@Override
	public String getServerTaskType() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case WapRefurbish:
			wapPageTestConfig.setMode(MODE_REFRESH);
			wapPageTestConfig.setRefreshType("HomePage Refresh");
			break;
		case WapLogin:
			wapPageTestConfig.setMode(MODE_LOGIN);
			wapPageTestConfig.setRefreshType("HomePage Refresh");
			break;

		default:
			break;
		}
		return WalkStruct.TaskType.valueOf(this.getTaskType()).getXmlTaskType();
	}
}
