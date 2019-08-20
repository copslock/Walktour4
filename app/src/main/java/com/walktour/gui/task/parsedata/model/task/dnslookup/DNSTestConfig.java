package com.walktour.gui.task.parsedata.model.task.dnslookup;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class DNSTestConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 667217416847009309L;
	@SerializedName("url")
	private String url="";
	@SerializedName("timeout")
	private int timeout=10;
	/**品质数据测量间隔*/
	@SerializedName("iqosInv")
	private int iqosInv=0; 
 

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getIqosInv() {
		return iqosInv;
	}

	public void setIqosInv(int iqosInv) {
		this.iqosInv = iqosInv;
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
				if (tagName.equals("HostAddress")) {
					this.setUrl(parser.nextText());
				} else if (tagName.equals("Timeout")) {
					this.setTimeout(stringToInt(parser.nextText())/1000);
				}else if (tagName.equals("IqosInv")) {
					this.setIqosInv(stringToInt(parser.nextText())/1000);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("DNSTestConfig")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "DNSTestConfig");
		this.writeTag(serializer, "HostAddress", this.url);
		this.writeTag(serializer, "Timeout", this.timeout*1000);
		this.writeTag(serializer, "IqosInv", this.iqosInv*1000);
		serializer.endTag(null, "DNSTestConfig");
	}

}
