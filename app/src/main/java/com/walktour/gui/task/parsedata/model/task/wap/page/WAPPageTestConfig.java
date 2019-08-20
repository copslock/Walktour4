package com.walktour.gui.task.parsedata.model.task.wap.page;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.base.GatewaySetting;
import com.walktour.gui.task.parsedata.model.task.base.URLInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.LinkedList;
import java.util.List;

public class WAPPageTestConfig extends TaskBase {
	private static final long serialVersionUID = -2376699283598652354L;
	@SerializedName("saveDirectory")
	private String saveDirectory = "";
	@SerializedName("refreshType")
	private String refreshType;
	@SerializedName("userAgent")
	private String userAgent;
	@SerializedName("refreshURLLayer")
	private String refreshURLLayer;
	@SerializedName("noDataTimeout")
	private int noDataTimeout;
	@SerializedName("mode")
	private String mode = "";
	@SerializedName("loadImage")
	private String loadImage;
	@SerializedName("clearCache")
	private boolean clearCache = false;
	@SerializedName("useGateWay")
	private boolean useGateWay;
	@SerializedName("pageTimeout")
	private int pageTimeout;
	@SerializedName("urlList")
	private List<URLInfo> urlList = new LinkedList<URLInfo>();
	@SerializedName("gatewaySetting")
	private GatewaySetting gatewaySetting=new GatewaySetting();

	public String getSaveDirectory() {
		return saveDirectory;
	}

	public void setSaveDirectory(String saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	public String getRefreshType() {
		return refreshType;
	}

	public void setRefreshType(String refreshType) {
		this.refreshType = refreshType;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getRefreshURLLayer() {
		return refreshURLLayer;
	}

	public void setRefreshURLLayer(String refreshURLLayer) {
		this.refreshURLLayer = refreshURLLayer;
	}

	public int getNoDataTimeout() {
		return noDataTimeout;
	}

	public void setNoDataTimeout(int noDataTimeout) {
		this.noDataTimeout = noDataTimeout;
	}
 

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getLoadImage() {
		return loadImage;
	}

	public void setLoadImage(String loadImage) {
		this.loadImage = loadImage;
	}

	public boolean isClearCache() {
		return clearCache;
	}

	public void setClearCache(boolean clearCache) {
		this.clearCache = clearCache;
	}
 

	public boolean isUseGateWay() {
		return useGateWay;
	}

	public void setUseGateWay(boolean useGateWay) {
		this.useGateWay = useGateWay;
	}

	public int getPageTimeout() {
		return pageTimeout;
	}

	public void setPageTimeout(int pageTimeout) {
		this.pageTimeout = pageTimeout;
	}

	public GatewaySetting getGatewaySetting() {
		return gatewaySetting;
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
				if (tagName.equals("RefreshType")) {
					this.setRefreshType(parser.nextText());
				} else if (tagName.equals("SaveDirectory")) {
					this.setSaveDirectory(parser.nextText());
				} else if (tagName.equals("NoDataTimeout")) {
					this.setNoDataTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("Mode")) {
					this.setMode(parser.nextText());
				} else if (tagName.equals("RefreshURLLayer")) {
					this.setRefreshURLLayer(parser.nextText());
				} else if (tagName.equals("UserAgent")) {
					this.setUserAgent(parser.nextText());
				} else if (tagName.equals("LoadImage")) {
					this.setLoadImage(parser.nextText());
				} else if (tagName.equals("ClearCache")) {
					this.setClearCache(stringToBool(parser.nextText()));
				} else if (tagName.equals("UseGateway")) {
					this.setUseGateWay(stringToBool(parser.nextText()));
				} else if (tagName.equals("PageTimeout")) {
					this.setPageTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("URLList")) {
				} else if (tagName.equals("URLInfo")) {
					URLInfo urlInfo = new URLInfo();
					urlInfo.parseXml(parser);
					this.getUrlList().add(urlInfo);
				} else if (tagName.equals("GatewaySetting")) { 
					gatewaySetting.parseXml(parser);
				}

				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("WAPPageTestConfig")) { 
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "WAPPageTestConfig");

		this.writeTag(serializer, "SaveDirectory", this.saveDirectory);
		this.writeTag(serializer, "RefreshType", this.refreshType);
		this.writeTag(serializer, "UserAgent", this.userAgent);
		this.writeTag(serializer, "RefreshURLLayer", this.refreshURLLayer);
		this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout*1000);
		this.writeTag(serializer, "Mode", this.mode);
		this.writeTag(serializer, "LoadImage", this.loadImage);
		this.writeTag(serializer, "ClearCache", this.clearCache);
		this.writeTag(serializer, "UseGateway", this.useGateWay);
		this.writeTag(serializer, "PageTimeout", this.pageTimeout*1000);
		this.writeTag(serializer, "RefreshType", this.refreshType);

		serializer.startTag(null, "URLList");
		for (URLInfo urlInfo : urlList) {
			if (null != urlInfo)
				urlInfo.writeXml(serializer);
		}
		serializer.endTag(null, "URLList");
		if (null != this.gatewaySetting) {
			this.gatewaySetting.writeXml(serializer);
		}
		serializer.endTag(null, "WAPPageTestConfig");
	}
}
