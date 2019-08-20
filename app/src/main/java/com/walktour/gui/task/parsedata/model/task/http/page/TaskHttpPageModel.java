package com.walktour.gui.task.parsedata.model.task.http.page;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;
import com.walktour.gui.task.parsedata.model.task.base.URLInfo;
import com.walktour.model.UrlModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Http测试模型
 */
public class TaskHttpPageModel extends TaskModel { 
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1504441203377175685L;
	private final static String SERVERTYPE_NORMAL = "Normal";
	private final static String SERVERTYPE_BAIDU = "BaiduYun";

	public TaskHttpPageModel(String taskType) {
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		this.setTaskType(taskType);
	}

	public static final int LOGIN = 0; // 0为登录
	public static final int REFRESH = 1; // 1为刷新`


	/**
	 * 刷新类型	 1:刷新主页 2:深度刷新（打开子链接）
	 */
	public static final int REFRESH_TYPE_HOME_PAGE = 1,REFRESH_TYPE_DEEPLY = 2;

	@SerializedName("refreshDeep")
	private int refreshDeep = 0; // 刷新层次,刷新业务时用到
	@SerializedName("url")
	private String url = "";
	@SerializedName("xmlUrl")
	private String xmlUrl = "";
	@SerializedName("accessPoint")
	private int accessPoint; // 接入点 0,网络 1,wifi
	@SerializedName("logonCount")
	private int logonCount = 1;
	@SerializedName("hasProxy")
	private boolean hasProxy = false; // 是否用代理服务器
	@SerializedName("downPicture")
	private boolean downPicture = false;// 是否下载图片
	@SerializedName("parallelTimeout")
	private int parallelTimeout = 0; // 并发超时
	@SerializedName("httpDownTestConfig")
	private HTTPDownTestConfig httpDownTestConfig = new HTTPDownTestConfig();
	@SerializedName("httpPageTestConfig")
	private HTTPPageTestConfig httpPageTestConfig = new HTTPPageTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @author tangwq
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());
		ArrayList<UrlModel> urlList = getUrlModelList();
		if (urlList != null && urlList.size() > 0) {
			for (UrlModel urlModel : urlList) {
				if (urlModel.getEnable().equalsIgnoreCase("True")) {
					testTask.append("url =" + urlModel.getName() + "\r\n");
				}
			}
		} else if (xmlUrl != null && !xmlUrl.trim().equals("")) {
			testTask.append("url =" + xmlUrl + "\r\n");
		}

		testTask.append("reponse =" + getReponse() + "\r\n");
		testTask.append("timeOut =" + getTimeOut() + "\r\n");
		testTask.append("showWeb =" + this.isShowWeb() + "\r\n");
		testTask.append("accessPoint =" +  accessPoint + "\r\n");
		testTask.append("logonCount =" + logonCount + "\r\n");
		testTask.append("hasProxy =" + hasProxy + "\r\n");
		testTask.append("downPicture =" + downPicture + "\r\n");
		testTask.append("address =" + this.getAddress() + "\r\n");
		testTask.append("port =" + this.getPort() + "\r\n");
		testTask.append("user =" + this.getUser() + "\r\n");
		testTask.append("pass =" + this.getPass() + "\r\n");
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
	
	public String getXmlUrl() {

		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case HttpDownload:
			List<URLInfo>  urlInfos = httpDownTestConfig.getUrlList();
			if(urlInfos != null && urlInfos.size() > 0 ){
				for (int i = 0; i < urlInfos.size(); i++) {
					if(urlInfos.get(i).isCheck()){
						this.xmlUrl = urlInfos.get(i).getUrl();
						break;
					}
				}
			}
			break;
		default:
			break;
		}
		return xmlUrl;
	}

	public void setXmlUrl(String xmlUrl) {
		this.xmlUrl = xmlUrl;
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case HttpDownload:
			List<URLInfo>  urlInfos = httpDownTestConfig.getUrlList();
			urlInfos.clear();
			URLInfo urlInfo = new URLInfo();
			urlInfo.setCheck(true);
			urlInfo.setUrl(xmlUrl);
			this.xmlUrl = xmlUrl;
			urlInfos.add(urlInfo);
			break;
		default:
			break;
		}
	}

	public int getRefreshDeep() {
		return refreshDeep;
	}

	public void setRefreshDeep(int refreshDeep) {
		this.refreshDeep = refreshDeep;
	}

	public boolean isHasProxy() {
		return hasProxy;
	}

	public void setHasProxy(boolean hasProxy) {
		this.hasProxy = hasProxy;
	}

	public String getAddress() {
		return httpPageTestConfig.getProxySetting().getProxyIP();
	}

	public void setAddress(String address) {
		httpPageTestConfig.getProxySetting().setProxyIP(address);
	}

	public int getPort() {
		return httpPageTestConfig.getProxySetting().getProxyPort();
	}

	public void setPort(int port) {
		httpPageTestConfig.getProxySetting().setProxyPort(port);
	}

	public String getUser() {
		return httpPageTestConfig.getProxySetting().getUserName();

	}

	public void setUser(String user) {
		httpPageTestConfig.getProxySetting().setUserName(user);
	}

	public String getPass() {
		return httpPageTestConfig.getProxySetting().getPassword();
	}

	public void setPass(String pass) {
		httpPageTestConfig.getProxySetting().setPassword(pass);
	}

	public int getLogonCount() {
		return logonCount;
	}

	public void setLogonCount(int logonCount) {
		this.logonCount = logonCount;
	}

	public int getAccessPoint() {
		return accessPoint;
	}

	public void setAccessPoint(int accessPoint) {
		this.accessPoint = accessPoint;
	}
	

	public synchronized String getUrl() {
		return url;
	}

	public synchronized void setUrl(String url) {
		this.url = url;
	}

	public synchronized int getReponse() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case HttpDownload:
			return httpDownTestConfig.getNoDataTimeout();
		default:
			break;
		}
		return httpPageTestConfig.getNoDataTimeout();
	}

	public synchronized void setReponse(int reponse) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case HttpDownload:
			httpDownTestConfig.setNoDataTimeout(reponse);
			break;
		default:
			httpPageTestConfig.setNoDataTimeout(reponse);
			break;
		}
	}

	public synchronized int getTimeOut() {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case HttpDownload:
			if(this.getTestMode()==0) {
				return httpDownTestConfig.getDownloadTimeout();
			}else{
				return httpDownTestConfig.getDownloadDuration();
			}

		default:
			break;
		}
		return httpPageTestConfig.getPageTimeout();
	}

	public synchronized void setTimeOut(int timeOut) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case HttpDownload:
			if(this.getTestMode()==0){//区分是FILE还是时间
				httpDownTestConfig.setDownloadTimeout(timeOut);
			}else{
				httpDownTestConfig.setDownloadDuration(timeOut);
			}

			break;
		default:
			httpPageTestConfig.setPageTimeout(timeOut);
			break;
		}
	}
	
	public synchronized int getThreadCount(){
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case HttpDownload:
			return httpDownTestConfig.getThreadCount();
			default:
				break;
		}
		
		return httpPageTestConfig.getThreadCount(); 
	}
	
	public synchronized void setThreadCount(int thrCount) {
		switch (WalkStruct.TaskType.valueOf(this.getTaskType())) {
		case HttpDownload:
			httpDownTestConfig.setThreadCount(thrCount);
			break;
		default:
			httpPageTestConfig.setThreadCount(thrCount);
			break;
		}
	}

	public boolean isShowWeb() {
		return httpPageTestConfig.isShowBrowser();
	}

	public void setShowWeb(boolean showWeb) {
		httpPageTestConfig.setShowBrowser(showWeb);
	}

	public boolean isDownPicture() {
		return downPicture;
	}

	public void setDownPicture(boolean downPicture) {
		this.downPicture = downPicture;
	}

	public ArrayList<UrlModel> getUrlModelList() {
		ArrayList<UrlModel> listM = new ArrayList<UrlModel>();
		List<URLInfo> list = httpPageTestConfig.getUrlList();
		for (URLInfo url : list) {
			UrlModel um = new UrlModel();
			um.setName(url.getUrl());
			um.setEnable(boolToText(url.isCheck()));
			listM.add(um);
		}
		return listM;
	}

	public void setUrlModelList(ArrayList<UrlModel> urlModelList) {
//		httpPageTestConfig.getUrlList().clear();
//		List<URLInfo> list = httpPageTestConfig.getUrlList();
		List<URLInfo> list = new ArrayList<URLInfo>();
		if (null != urlModelList) {
			for (UrlModel um : urlModelList) {
				URLInfo ui = new URLInfo();
				ui.setUrl(um.getName());
				ui.setCheck(stringToBool(um.getEnable()));
				list.add(ui);
			}
			httpPageTestConfig.setUrlList(list);
		}
	}

	public int getHttpTestMode() {
		String mode = httpPageTestConfig.getMode();
		if (mode != null && mode.length() != 0) {
			return mode.equals("Login") ? LOGIN : REFRESH;
		}
		return LOGIN;
	}

	public void setHttpTestMode(int httpTestMode) {
		httpPageTestConfig.setMode(httpTestMode == LOGIN ? "Login" : "Refresh");
	}

	public int getParallelTimeout() {
		return parallelTimeout;
	}

	public void setParallelTimeout(int parallelTimeout) {
		this.parallelTimeout = parallelTimeout;
	}

	public int getHttpRefreshDepth() {
		return httpPageTestConfig.getHttpRefreshDepth();
	}

	public void setHttpRefreshDepth(int httpRefreshDepth) {
		httpPageTestConfig.setHttpRefreshDepth(httpRefreshDepth);
	}

	public int getHttpRefreshType() {
		return httpPageTestConfig.getHttpRefreshType();
	}

	public void setHttpRefreshType(int httpRefreshType) {
		httpPageTestConfig.setHttpRefreshType(httpRefreshType);
	}

	public int getServerType() {
		if(httpDownTestConfig.getWebsiteType().equals(SERVERTYPE_NORMAL)){
			return 0;
		}else if(httpDownTestConfig.getWebsiteType().equals(SERVERTYPE_BAIDU)){
			return 1;
		}
		return 0;
	}

	public void setServerType(int serverType) {
		switch (serverType) {
		case 0:
			httpDownTestConfig.setWebsiteType(SERVERTYPE_NORMAL);
			break;
		case 1:
			httpDownTestConfig.setWebsiteType(SERVERTYPE_BAIDU);
			break;

		default:
			break;
		}
	}
	public int getTestMode() {
		if(httpDownTestConfig.getPsCallMode().equalsIgnoreCase(HTTPDownTestConfig.PSCALL_BY_FILE)){
			return 0;
		}
		return 1;
	}

	public void setTestMode(int testMode) {
		switch (testMode) {
		case 0:
			httpDownTestConfig.setPsCallMode(HTTPDownTestConfig.PSCALL_BY_FILE);
			break;
		case 1:
			httpDownTestConfig.setPsCallMode(HTTPDownTestConfig.PSCALL_BY_TIME);
			break;
		default:
			break;
		}
	}
	public int getAccountType() {
		if(httpDownTestConfig.getBaiduYunAccount().getIsDeveloperAccount()){
			return 0;
		}
		return 1;
	}

	public void setAccountType(int accountType) {
		switch (accountType) {
		case 0:
			httpDownTestConfig.getBaiduYunAccount().setIsDeveloperAccount(true);
			break;
		case 1:
			httpDownTestConfig.getBaiduYunAccount().setIsDeveloperAccount(false);
			break;
		default:
			break;
		}
	}

	public String getAccountKey() {
		return httpDownTestConfig.getBaiduYunAccount().getApiKey();
	}

	public void setAccountKey(String accountKey) {
		httpDownTestConfig.getBaiduYunAccount().setApiKey(accountKey);
	}

	public String getSecretKey() {
		return httpDownTestConfig.getBaiduYunAccount().getSecretKey();
	}

	public void setSecretKey(String secretKey) {
		httpDownTestConfig.getBaiduYunAccount().setSecretKey(secretKey);
	}

	public HTTPPageTestConfig getHttpPageTestConfig() {
		return httpPageTestConfig;
	}

	public HTTPDownTestConfig getHttpDownTestConfig() {
		return httpDownTestConfig;
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
				if (tagName.equals("HTTPPageTestConfig")) {
					httpPageTestConfig.parseXml(parser);
				} else if (tagName.equals("HTTPDownTestConfig")) {
					httpDownTestConfig.parseXml(parser);
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

		if (this.getTaskType().equals(WalkStruct.TaskType.HttpDownload.name())) {
			if (null != httpDownTestConfig)
				httpDownTestConfig.writeXml(serializer);
		} else {
			if (null != httpPageTestConfig)
				httpPageTestConfig.writeXml(serializer);
		}
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}

	@Override
	public String getServerTaskType() {
		return WalkStruct.TaskType.valueOf(this.getTaskType()).getXmlTaskType();
	}
}
