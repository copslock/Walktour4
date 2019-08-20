package com.walktour.gui.task.parsedata.model.task.pdpactive;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class QoSRequest extends TaskBase {
	private static final long serialVersionUID = 4976414803414406083L;
	@SerializedName("dlueRate")
	private int dlueRate = 2048;
	@SerializedName("ulueRate")
	private int ulueRate = 384;
	@SerializedName("trafficClass")
	private String trafficClass = "Default";
	@SerializedName("apn")
	private String apn ="cmnet";
	public int getDlueRate() {
		return dlueRate;
	}
	public void setDlueRate(int dlueRate) {
		this.dlueRate = dlueRate;
	}
	public int getUlueRate() {
		return ulueRate;
	}
	public void setUlueRate(int ulueRate) {
		this.ulueRate = ulueRate;
	}
	public String getTrafficClass() {
		return trafficClass;
	}
	public void setTrafficClass(String trafficClass) {
		this.trafficClass = trafficClass;
	}
	public String getApn() {
		return apn;
	}
	public void setApn(String apn) {
		this.apn = apn;
	}
	
	public void parseXml(XmlPullParser parser) {
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			String attName = parser.getAttributeName(i);
			if (attName.equals("DLUERate")) {
				this.setDlueRate(stringToInt(parser.getAttributeValue(i)));
			} else if (attName.equals("ULUERate")) {
				this.setUlueRate(stringToInt(parser.getAttributeValue(i)));
			} else if (attName.equals("TrafficClass")) {
				this.setTrafficClass(parser.getAttributeValue(i));
			}else if (attName.equals("APN")) {
				this.setApn(parser.getAttributeValue(i));
			}
		}
	}
	
	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null,"QoSRequest");
		this.writeAttribute(serializer, "DLUERate", this.dlueRate);
		this.writeAttribute(serializer, "ULUERate", this.ulueRate);
		this.writeAttribute(serializer, "TrafficClass", this.trafficClass);
		this.writeAttribute(serializer, "APN", this.apn);
		serializer.endTag(null,"QoSRequest");
		
	}
}
