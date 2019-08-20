package com.walktour.gui.task.parsedata.model.task.wlan.ap;

import com.walktour.gui.task.parsedata.model.task.wlan.WlanConfig;

import org.xmlpull.v1.XmlSerializer;

public class WlanAPRelationTestConfig extends WlanConfig{
	private static final long serialVersionUID = 7975056350869036630L;

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null,"WlanAPRelationTestConfig");
		this.writeTag(serializer, "Timeout", this.getTimeout()*1000);
		this.writeTag(serializer, "APName", this.getApName());
		if(null!=this.getWlanAccount()){
			this.getWlanAccount().writeXml(serializer);
		}
		serializer.endTag(null,"WlanAPRelationTestConfig");
	}
}
