package com.walktour.gui.task.parsedata.model.task.http.page;

import com.google.gson.annotations.SerializedName;
import com.walktour.control.config.ConfigUrl;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.base.URLInfo;
import com.walktour.gui.task.parsedata.model.task.http.ProxySetting;
import com.walktour.model.UrlModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.LinkedList;
import java.util.List;

public class HTTPPageTestConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 4409985634876472034L;
	public static final String PSCALL_BY_TIME="By Time";
	public static final String PSCALL_BY_FILE="By File";
	/**是否PScall mode**/
	@SerializedName("psCallMode")
	private String psCallMode=PSCALL_BY_FILE;
	/**0-登陆,1-刷新*/
	@SerializedName("mode")
	private String mode;
	/**刷新的URL**/
	@SerializedName("urlList")
	private List<URLInfo> urlList = new LinkedList<URLInfo>();
	/**页面登陆/刷新超时**/
	@SerializedName("pageTimeout")
	private int pageTimeout;
	/**无数据超时时间**/
	@SerializedName("noDataTimeout")
	private int noDataTimeout;
	/**间隔时长**/
	@SerializedName("innerInterval")
	private int innerInterval;
	/**浏览器类型**/
	@SerializedName("userAgent")
	private String userAgent;
	/**是否加载图片**/
	@SerializedName("loadImage")
	private boolean loadImage=false;
	/**是否清除缓存**/
	@SerializedName("clearCache")
	private boolean clearCache=false;
	/**是否保存文件**/
	@SerializedName("isSaveFile")
	private boolean isSaveFile;
	/**保存目录**/
	@SerializedName("saveDirectory")
	private String saveDirectory;
	/**是否使用代理*/
	@SerializedName("useProxy")
	private boolean useProxy=false;
	/**代理设置**/
	@SerializedName("proxySetting")
	private ProxySetting proxySetting=new ProxySetting();
	/**连接超时**/
	@SerializedName("connectTimeout")
	private int connectTimeout=20000;
	/**是否显示浏览器界面**/
	@SerializedName("showBrowser")
	private boolean showBrowser=false;
	@SerializedName("threadCount")
	private int threadCount = 1;
	@SerializedName("httpRefreshType")
	private int httpRefreshType;//刷新类型1:刷新主页 2:深度刷新（打开子链接）
	@SerializedName("httpRefreshDepth")
	private int httpRefreshDepth;//刷新深度层数
	
	public String getPsCallMode() {
		return psCallMode;
	}

	public void setPsCallMode(String psCallMode) {
		this.psCallMode = psCallMode;
	}

	public boolean isUseProxy() {
		return useProxy;
	}

	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public boolean isShowBrowser() {
		return showBrowser;
	}

	public void setShowBrowser(boolean showBrowser) {
		this.showBrowser = showBrowser;
	}

	public int getHttpRefreshDepth() {
		return httpRefreshDepth == 0 ? 1 : httpRefreshDepth;
	}

	public void setHttpRefreshDepth(int httpRefreshDepth) {
		this.httpRefreshDepth = httpRefreshDepth;
	}

	public int getHttpRefreshType() {
		return httpRefreshType;
	}

	public void setHttpRefreshType(int httpRefreshType) {
		this.httpRefreshType = httpRefreshType;
	}

	public int getThreadCount(){
		return threadCount;
	}
	
	public void setThreadCount(int thrCount){
		this.threadCount = thrCount;
	}

	public void setProxySetting(ProxySetting proxySetting) {
		this.proxySetting = proxySetting;
	}

	public int getNoDataTimeout() {
		return noDataTimeout;
	}

	public void setNoDataTimeout(int noDataTimeout) {
		this.noDataTimeout = noDataTimeout;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public boolean isSaveFile() {
		return isSaveFile;
	}

	public void setSaveFile(boolean isSaveFile) {
		this.isSaveFile = isSaveFile;
	}

	public String getSaveDirectory() {
		return saveDirectory;
	}

	public void setSaveDirectory(String saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public boolean isLoadImage() {
		return loadImage;
	}

	public void setLoadImage(boolean loadImage) {
		this.loadImage = loadImage;
	}

	public boolean isClearCache() {
		return clearCache;
	}

	public void setClearCache(boolean clearCache) {
		this.clearCache = clearCache;
	}

	public int getInnerInterval() {
		return innerInterval;
	}

	public void setInnerInterval(int innerInterval) {
		this.innerInterval = innerInterval;
	}

	public int getPageTimeout() {
		return pageTimeout;
	}

	public void setPageTimeout(int pageTimeout) {
		this.pageTimeout = pageTimeout;
	}

	public List<URLInfo> getUrlList() {
		return urlList;
	}

	public void setUrlList(List<URLInfo> urlList) {
		System.out.println("urllist----" + urlList.size());
		this.urlList  = urlList;
	}

	public ProxySetting getProxySetting() {
		return proxySetting;
	}
 

	public void parseXml(XmlPullParser parser) throws Exception {
		int eventType = parser.getEventType();
		String tagName = "";
		ConfigUrl config=new ConfigUrl();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("UseProxy")) {
					this.setUseProxy(stringToBool(parser.nextText()));
				} else if (tagName.equals("NoDataTimeout")) {
					this.setNoDataTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("UserAgent")) {
					this.setUserAgent(parser.nextText());
				} else if (tagName.equals("IsSaveFile")) {
					this.setSaveFile(stringToBool(parser.nextText()));
				} else if (tagName.equals("PSCallMode")) {
					this.setPsCallMode(parser.nextText());
				} else if (tagName.equals("SaveDirectory")) {
					this.setSaveDirectory(parser.nextText());
				} else if (tagName.equals("Mode")) {
					this.setMode(parser.nextText());
				} else if (tagName.equals("LoadImage")) {
					this.setLoadImage(stringToBool(parser.nextText()));
				} else if (tagName.equals("ClearCache")) {
					this.setClearCache(stringToBool(parser.nextText()));
				} else if (tagName.equals("InnerInterval")) {
					this.setInnerInterval(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("PageTimeout")) {
					this.setPageTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("URLList")) {
					// this.setPageTimeout(stringToInt(parser.nextText()));
				} else if (tagName.equals("URLInfo")) {
					URLInfo urlInfo = new URLInfo();
					urlInfo.parseXml(parser);
					//载入到配置里面去
					UrlModel url=new UrlModel();
					url.setName(urlInfo.getUrl());
					url.setEnable(urlInfo.isCheck()?"1":"0");
					config.addUrl(url);
					
					this.getUrlList().add(urlInfo);
				} else if (tagName.equals("ProxySetting")) { 
					proxySetting.parseXml(parser);
				}  else if (tagName.equals("ConnectTimeout")) {
					this.setConnectTimeout(stringToInt(parser.nextText())/1000);
				}else if (tagName.equals("ShowBrowser")) {
					this.setShowBrowser(stringToBool(parser.nextText()));
				} else if (tagName.equals("ThreadCount")) {
					this.setThreadCount(stringToInt(parser.nextText()));
				} else if(tagName.equals("HttpRefreshType")){
					this.setHttpRefreshType(stringToInt(parser.nextText()));
				}else if(tagName.equals("HttpRefreshDepth")){
					this.setHttpRefreshDepth(stringToInt(parser.nextText()));
				}

				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("HTTPPageTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {

		serializer.startTag(null, "HTTPPageTestConfig");
		
		this.writeTag(serializer, "PSCallMode", this.psCallMode);
		this.writeTag(serializer, "Mode", this.mode);
		serializer.startTag(null, "URLList");
		for (URLInfo urlInfo : urlList) {
			if (null != urlInfo)
				urlInfo.writeXml(serializer);
		}
		serializer.endTag(null, "URLList");
		this.writeTag(serializer, "PageTimeout", this.pageTimeout*1000);
		this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout*1000);
		this.writeTag(serializer, "InnerInterval", this.innerInterval*1000);
		this.writeTag(serializer, "UserAgent", this.userAgent);
		this.writeTag(serializer, "LoadImage", this.loadImage);
		this.writeTag(serializer, "ClearCache", this.clearCache);
		this.writeTag(serializer, "IsSaveFile", this.isSaveFile);
		this.writeTag(serializer, "SaveDirectory", this.saveDirectory);
		this.writeTag(serializer, "UseProxy", this.useProxy);
		if(null!=proxySetting)
			proxySetting.writeXml(serializer);
		this.writeTag(serializer, "ConnectTimeout", this.connectTimeout*1000);
		this.writeTag(serializer, "ShowBrowser", this.showBrowser);
		this.writeTag(serializer, "ThreadCount", this.threadCount);
		this.writeTag(serializer,"HttpRefreshType",this.httpRefreshType);
		this.writeTag(serializer,"HttpRefreshDepth",this.httpRefreshDepth);
		serializer.endTag(null, "HTTPPageTestConfig");
	}
}
