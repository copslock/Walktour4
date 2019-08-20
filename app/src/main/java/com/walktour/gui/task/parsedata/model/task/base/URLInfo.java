package com.walktour.gui.task.parsedata.model.task.base;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class URLInfo extends TaskBase implements Cloneable { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -6404528989403256518L;
	@SerializedName("url")
	private String url;
	@SerializedName("isCheck")
	private boolean isCheck = true;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public void parseXml(XmlPullParser parser) {
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			String attName = parser.getAttributeName(i);
			if (attName.equals("URL")) {
				this.setUrl(parser.getAttributeValue(i));
			} else if (attName.equals("IsCheck")) {
				this.setCheck(stringToBool(parser.getAttributeValue(i)));
			}

		}
	}
	

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "URLInfo");
		this.writeAttribute(serializer, "URL", this.url);
		this.writeAttribute(serializer, "IsCheck", this.isCheck);
		serializer.endTag(null, "URLInfo");

	}

	@Override
	protected URLInfo clone() {
		URLInfo urlInfo = null;
		try {
			urlInfo = (URLInfo) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return urlInfo;
	}

	@Override
	public String toString() {
		return "URLInfo{" +
				"url='" + url + '\'' +
				", isCheck=" + isCheck +
				'}';
	}
}
