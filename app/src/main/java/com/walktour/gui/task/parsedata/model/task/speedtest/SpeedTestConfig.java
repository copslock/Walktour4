package com.walktour.gui.task.parsedata.model.task.speedtest;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class SpeedTestConfig extends TaskBase {
	private static final long serialVersionUID = -317279995639792252L;
	@SerializedName("downloadThreadCount")
	private int downloadThreadCount;
	@SerializedName("uploadThreadCount")
	private int uploadThreadCount;
	@SerializedName("downloadFile")
	private String downloadFile;
	@SerializedName("url")
	private String url ="";
	@SerializedName("country")
	private String country = "";
	@SerializedName("city")
	private String city ="";
	@SerializedName("sponsor")
	private String sponsor="";
	
	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	public int getDownloadThreadCount() {
		return downloadThreadCount;
	}

	public void setDownloadThreadCount(int downloadThreadCount) {
		this.downloadThreadCount = downloadThreadCount;
	}

	public int getUploadThreadCount() {
		return uploadThreadCount;
	}

	public void setUploadThreadCount(int uploadThreadCount) {
		this.uploadThreadCount = uploadThreadCount;
	}

	public String getDownloadFile() {
		return downloadFile;
	}

	public void setDownloadFile(String downloadFile) {
		this.downloadFile = downloadFile;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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
				if (tagName.equals("DownloadThreadCount")) {
					this.setDownloadThreadCount(stringToInt(parser.nextText()));
				} else if (tagName.equals("UploadThreadCount")) {
					this.setUploadThreadCount(stringToInt(parser.nextText()));
				} else if (tagName.equals("DownloadFile")) {
					this.setDownloadFile(parser.nextText());
				} else if (tagName.equals("URL")) {
					this.setUrl(parser.nextText());
				} else if (tagName.equals("Country")) {
					this.setCountry(parser.nextText());
				} else if (tagName.equals("City")) {
					this.setCity(parser.nextText());
				}else if (tagName.equals("Sponsor")) {
					this.setSponsor(parser.nextText());
				}

				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("SpeedTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "SpeedTestConfig");
		this.writeTag(serializer, "DownloadThreadCount", this.downloadThreadCount);
		this.writeTag(serializer, "UploadThreadCount", this.uploadThreadCount);
		this.writeTag(serializer, "DownloadFile", this.downloadFile);
		this.writeTag(serializer, "URL", this.url);
		this.writeTag(serializer, "Country", this.country);
		this.writeTag(serializer, "City", this.city);
		this.writeTag(serializer, "Sponsor", this.sponsor);
		serializer.endTag(null, "SpeedTestConfig");
	}
}
