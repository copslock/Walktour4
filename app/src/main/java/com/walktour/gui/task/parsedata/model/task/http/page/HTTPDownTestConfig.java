package com.walktour.gui.task.parsedata.model.task.http.page;

import com.google.gson.annotations.SerializedName;
import com.walktour.control.config.ConfigUrl;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.base.URLInfo;
import com.walktour.gui.task.parsedata.model.task.http.BaiduYunAccount;
import com.walktour.gui.task.parsedata.model.task.http.ProxySetting;
import com.walktour.model.UrlModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.LinkedList;
import java.util.List;

public class HTTPDownTestConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -297176498368836047L;
	public static final String PSCALL_BY_TIME="By Time";
	public static final String PSCALL_BY_FILE="By File";
	/**是否PScall mode**/
	@SerializedName("psCallMode")
	private String psCallMode=PSCALL_BY_FILE;
	@SerializedName("saveDirectory")
	private String saveDirectory;
	@SerializedName("useProxy")
	private boolean useProxy;
	@SerializedName("isSaveFile")
	private boolean isSaveFile;
	@SerializedName("downloadTimeout")
	private int downloadTimeout;
	@SerializedName("downloadDuration")
	private int downloadDuration;
	@SerializedName("websiteType")
	private String websiteType="Normal";
	@SerializedName("threadCount")
	private int threadCount;
	@SerializedName("noDataTimeout")
	private int noDataTimeout;

	@SerializedName("urlList")
	private List<URLInfo> urlList = new LinkedList<URLInfo>();
	@SerializedName("proxySetting")
	private ProxySetting proxySetting;
	@SerializedName("baiduYunAccount")
	private BaiduYunAccount baiduYunAccount = new BaiduYunAccount();
	
	public String getSaveDirectory() {
		return saveDirectory;
	}

	public void setSaveDirectory(String saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	public boolean isUseProxy() {
		return useProxy;
	}

	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}

	public boolean isSaveFile() {
		return isSaveFile;
	}

	public void setSaveFile(boolean isSaveFile) {
		this.isSaveFile = isSaveFile;
	}

	public String getPsCallMode() {
		return psCallMode;
	}

	public void setPsCallMode(String psCallMode) {
		this.psCallMode = psCallMode;
	}

	public int getDownloadTimeout() {
		return downloadTimeout;
	}

	public void setDownloadTimeout(int downloadTimeout) {
		this.downloadTimeout = downloadTimeout;
	}

	public int getDownloadDuration() {
		return downloadDuration;
	}

	public void setDownloadDuration(int downloadDuration) {
		this.downloadDuration = downloadDuration;
	}

	public String getWebsiteType() {
		return websiteType;
	}

	public void setWebsiteType(String websiteType) {
		this.websiteType = websiteType;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public int getNoDataTimeout() {
		return noDataTimeout;
	}

	public void setNoDataTimeout(int noDataTimeout) {
		this.noDataTimeout = noDataTimeout;
	}

	public ProxySetting getProxySetting() {
		return proxySetting;
	}

	public void setProxySetting(ProxySetting proxySetting) {
		this.proxySetting = proxySetting;
	}

	public BaiduYunAccount getBaiduYunAccount() {
		return baiduYunAccount;
	}

	public void setBaiduYunAccount(BaiduYunAccount baiduYunAccount) {
		this.baiduYunAccount = baiduYunAccount;
	}

	public List<URLInfo> getUrlList() {
		return urlList;
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
				} else if (tagName.equals("SaveDirectory")) {
					this.setSaveDirectory(parser.nextText());
				} else if (tagName.equals("IsSaveFile")) {
					this.setSaveFile(stringToBool(parser.nextText()));
				} else if (tagName.equals("PSCallMode")) {
					this.setPsCallMode(parser.nextText());
				} else if (tagName.equals("WebsiteType")) {
					this.setWebsiteType(parser.nextText());
				} else if (tagName.equals("DownloadTimeout")) {
					this.setDownloadTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("DownloadDuration")) {
					this.setDownloadDuration(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("ThreadCount")) {
					this.setThreadCount(stringToInt(parser.nextText()));
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
					proxySetting = new ProxySetting();
					proxySetting.parseXml(parser);
				} else if (tagName.equals("BaiduYunAccount")) {
					baiduYunAccount = new BaiduYunAccount();
					baiduYunAccount.parseXml(parser);
				}

				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("HTTPDownTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {

		serializer.startTag(null, "HTTPDownTestConfig");

		this.writeTag(serializer, "SaveDirectory", this.saveDirectory);
		this.writeTag(serializer, "UseProxy", this.useProxy);
		this.writeTag(serializer, "IsSaveFile", this.isSaveFile);
		this.writeTag(serializer, "PSCallMode", this.psCallMode);
		this.writeTag(serializer, "DownloadTimeout", this.downloadTimeout*1000);
		this.writeTag(serializer, "DownloadDuration", this.downloadDuration*1000);
		this.writeTag(serializer, "WebsiteType", this.websiteType);
		this.writeTag(serializer, "ThreadCount", this.threadCount);
		this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout*1000);
		serializer.startTag(null, "URLList");
		for (URLInfo urlInfo : urlList) {
			if (null != urlInfo) {
				urlInfo.writeXml(serializer);
			}
		}
		serializer.endTag(null, "URLList");
		if (null != proxySetting) {
			proxySetting.writeXml(serializer);
		}

		if (null != baiduYunAccount) {
			baiduYunAccount.writeXml(serializer);
		}
		serializer.endTag(null, "HTTPDownTestConfig");

	}
}
