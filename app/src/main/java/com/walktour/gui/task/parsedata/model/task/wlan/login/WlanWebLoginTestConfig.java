package com.walktour.gui.task.parsedata.model.task.wlan.login;

import com.walktour.gui.task.parsedata.model.task.wlan.WlanConfig;

import org.xmlpull.v1.XmlSerializer;

public class WlanWebLoginTestConfig extends WlanConfig{
	private static final long serialVersionUID = -4743282343448040281L;

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null,"WlanWebLoginTestConfig");
		this.writeTag(serializer, "Timeout", this.getTimeout()*1000);
		this.writeTag(serializer, "APName", this.getApName());
		if(null!=this.getWlanAccount()){
			this.getWlanAccount().writeXml(serializer);
		}
		serializer.endTag(null,"WlanWebLoginTestConfig");
	}
}
