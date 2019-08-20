package com.walktour.gui.task.parsedata.model.task.http.upload;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;
import com.walktour.gui.task.parsedata.model.task.http.BaiduYunAccount;
import com.walktour.gui.task.parsedata.model.task.http.ProxySetting;
import com.walktour.gui.task.parsedata.model.task.http.YoutubeAccount;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class HTTPUpTestConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5540842649862677965L;
	@SerializedName("psCallMode")
	private String psCallMode = PSCallMode_File;
	@SerializedName("websiteType")
	private String websiteType = WEBSITETYPE_YOUTUBE;
	@SerializedName("localFile")
	private String localFile = "";
	@SerializedName("url")
	private String url = "http://uploads.gdata.youtube.com/feeds/api/users/default/uploads";
	@SerializedName("fileSize")
	private int fileSize;
	@SerializedName("uploadTimeout")
	private int uploadTimeout;
	@SerializedName("uploadDuration")
	private int uploadDuration;
	@SerializedName("noDataTimeout")
	private int noDataTimeout;
	@SerializedName("fileSource")
	private String fileSource = "";
	@SerializedName("proxySetting")
	private ProxySetting proxySetting = new ProxySetting();
	@SerializedName("baiduYunAccount")
	private BaiduYunAccount baiduYunAccount = new BaiduYunAccount();
	@SerializedName("youtubeAccount")
	private YoutubeAccount youtubeAccount = new YoutubeAccount();
	
	
	public final static String WEBSITETYPE_YOUTUBE = "Youtube";
	public final static String WEBSITETYPE_BAIDU = "Baidu";
	public final static String WEBSITETYPE_HTTPS = "https";

	public final static String PSCallMode_Time = "By Time";
	public final static String PSCallMode_File = "By File";

	public final static String FileSource_Local  = "Local File";
	public final static String FileSource_Creat  = "Creat File";

	
	
	
	public String getPsCallMode() {
		return psCallMode;
	}

	public void setPsCallMode(String psCallMode) {
		this.psCallMode = psCallMode;
	}

	public String getWebsiteType() {
		return websiteType;
	}

	public void setWebsiteType(String websiteType) {
		this.websiteType = websiteType;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLocalFile() {
		return localFile;
	}

	public void setLocalFile(String localFile) {
		this.localFile = localFile;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getUploadTimeout() {
		return uploadTimeout;
	}

	public void setUploadTimeout(int uploadTimeout) {
		this.uploadTimeout = uploadTimeout;
	}

	public int getUploadDuration() {
		return uploadDuration;
	}

	public void setUploadDuration(int uploadDuration) {
		this.uploadDuration = uploadDuration;
	}

	public int getNoDataTimeout() {
		return noDataTimeout;
	}

	public void setNoDataTimeout(int noDataTimeout) {
		this.noDataTimeout = noDataTimeout;
	}

	public String getFileSource() {
		return fileSource;
	}

	public void setFileSource(String fileSource) {
		this.fileSource = fileSource;
	}

	public ProxySetting getProxySetting() {
		return proxySetting;
	}

	public BaiduYunAccount getBaiduYunAccount() {
		return baiduYunAccount;
	}

	public YoutubeAccount getYoutubeAccount() {
		return youtubeAccount;
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
				if (tagName.equals("WebsiteType")) {
					this.setWebsiteType(parser.nextText());
				} else if (tagName.equals("LocalFile")) {
					this.setLocalFile(parser.nextText());
				} else if (tagName.equals("FileSize")) {
					this.setFileSize(stringToInt(parser.nextText()));
				}else if (tagName.equals("URL")) {
					this.setUrl(parser.nextText());
				} else if (tagName.equals("PSCallMode")) {
					this.setPsCallMode(parser.nextText());
				} else if (tagName.equals("UploadTimeout")) {
					this.setUploadTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("UploadDuration")) {
					this.setUploadDuration(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("NoDataTimeout")) {
					this.setNoDataTimeout(stringToInt(parser.nextText())/1000);
				} else if (tagName.equals("FileSource")) {
					this.setFileSource(parser.nextText());
				} else if (tagName.equals("YoutubeAccount")) {
					youtubeAccount.parseXml(parser);
				} else if (tagName.equals("BaiduYunAccount")) {
					baiduYunAccount.parseXml(parser);
				} else if (tagName.equals("ProxySetting")) {
					proxySetting.parseXml(parser);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("HTTPUpTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {

		serializer.startTag(null, "HTTPUpTestConfig");

		this.writeTag(serializer, "PSCallMode", this.psCallMode);
		this.writeTag(serializer, "WebsiteType", this.websiteType);
		this.writeTag(serializer, "LocalFile", this.localFile);
		this.writeTag(serializer, "URL", this.url);
		this.writeTag(serializer, "FileSize", this.fileSize);
		this.writeTag(serializer, "UploadTimeout", this.uploadTimeout*1000);
		this.writeTag(serializer, "UploadDuration", this.uploadDuration*1000);
		this.writeTag(serializer, "NoDataTimeout", this.noDataTimeout*1000);
		this.writeTag(serializer, "FileSource", this.fileSource);

		if (null != proxySetting) {
			proxySetting.writeXml(serializer);
		}

		if (null != youtubeAccount) {
			youtubeAccount.writeXml(serializer);
		}

		if (null != baiduYunAccount) {
			baiduYunAccount.writeXml(serializer);
		}

		serializer.endTag(null, "HTTPUpTestConfig");

	}
}
