package com.walktour.gui.task.parsedata.model.task.wap.down;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.base.GatewaySetting;
import com.walktour.gui.task.parsedata.model.task.base.URLInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.LinkedList;
import java.util.List;

public class WAPDownTestConfig extends TaskBase {
	private static final long serialVersionUID = 209655885100427944L;
	@SerializedName("isSaveFile")
	private boolean isSaveFile = false;
	@SerializedName("saveDirectory")
	private String saveDirectory;
	@SerializedName("downloadFileType")
	private String downloadFileType;
	@SerializedName("useGateway")
	private String useGateway;
	@SerializedName("downloadTimeout")
	private int downloadTimeout;
	@SerializedName("threadCount")
	private int threadCount;
	@SerializedName("noDataTimeout")
	private int noDataTimeout;
	@SerializedName("urlList")
	private List<URLInfo> urlList = new LinkedList<URLInfo>();
	@SerializedName("gatewaySetting")
	private GatewaySetting gatewaySetting = new GatewaySetting();

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

	public String getDownloadFileType() {
		return downloadFileType;
	}

	public void setDownloadFileType(String downloadFileType) {
		this.downloadFileType = downloadFileType;
	}

	public String getUseGateway() {
		return useGateway;
	}

	public void setUseGateway(String useGateway) {
		this.useGateway = useGateway;
	}

	public int getDownloadTimeout() {
		return downloadTimeout;
	}

	public void setDownloadTimeout(int downloadTimeout) {
		this.downloadTimeout = downloadTimeout;
	}

	public GatewaySetting getGatewaySetting() {
		return gatewaySetting;
	}

	public void setGatewaySetting(GatewaySetting gatewaySetting) {
		this.gatewaySetting = gatewaySetting;
	}

	public List<URLInfo> getUrlList() {
		return urlList;
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
				if (tagName.equals("IsSaveFile")) {
					this.setSaveFile(stringToBool(parser.nextText()));
				} else if (tagName.equals("SaveDirectory")) {
					this.setSaveDirectory(parser.nextText());
				} else if (tagName.equals("DownloadFileType")) {
					this.setDownloadFileType(parser.nextText());
				} else if (tagName.equals("UseGateway")) {
					this.setUseGateway(parser.nextText());
				} else if (tagName.equals("DownloadTimeout")) {
					this.setDownloadTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("ThreadCount")) {
					this.setThreadCount(stringToInt(parser.nextText()));
				} else if (tagName.equals("NoDataTimeout")) {
					this.setNoDataTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("URLInfo")) {
					URLInfo urlInfo = new URLInfo();
					urlInfo.parseXml(parser);
					this.getUrlList().add(urlInfo);
				} else if (tagName.equals("GatewaySetting")) {
					gatewaySetting = new GatewaySetting();
					gatewaySetting.parseXml(parser);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("WAPDownTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "WAPDownTestConfig");

		this.writeTag(serializer, "IsSaveFile", this.isSaveFile);
		this.writeTag(serializer, "SaveDirectory", this.saveDirectory);
		this.writeTag(serializer, "DownloadFileType", this.downloadFileType);
		this.writeTag(serializer, "UseGateway", this.useGateway);
		this.writeTag(serializer, "DownloadTimeout", this.downloadTimeout*1000);
		this.writeTag(serializer, "ThreadCount", this.threadCount);
		this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout*1000);

		serializer.startTag(null, "URLList");
		for (URLInfo urlInfo : urlList) {
			if (null != urlInfo)
				urlInfo.writeXml(serializer);
		}
		serializer.endTag(null, "URLList");
		if (null != this.gatewaySetting) {
			this.gatewaySetting.writeXml(serializer);
		}

		serializer.endTag(null, "WAPDownTestConfig");
	}
}
