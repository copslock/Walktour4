package com.walktour.gui.task.parsedata.model.task.wlan.eteauth;

import com.walktour.gui.task.parsedata.model.task.wlan.WlanConfig;

import org.xmlpull.v1.XmlSerializer;

public class WlanETEAuthTestConfig extends WlanConfig{
	private static final long serialVersionUID = 9116397738488190847L;

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null,"WlanETEAuthTestConfig");
		this.writeTag(serializer, "Timeout", this.getTimeout()*1000);
		this.writeTag(serializer, "APName", this.getApName());
		if(null!=this.getWlanAccount()){
			this.getWlanAccount().writeXml(serializer);
		}
		serializer.endTag(null,"WlanETEAuthTestConfig");
	}
}
