package com.walktour.gui.task.parsedata.model.task.http;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class BaiduYunAccount extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -8151902261010495721L;
	@SerializedName("apiKey")
	private String apiKey;
	@SerializedName("secretKey")
	private String secretKey;
	@SerializedName("isDeveloperAccount")
	private boolean isDeveloperAccount = true;
	@SerializedName("uploadPath")
	private String uploadPath = "/ding-up";
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public boolean getIsDeveloperAccount() {
		return isDeveloperAccount;
	}

	public void setIsDeveloperAccount(boolean isDeveloperAccount) {
		this.isDeveloperAccount = isDeveloperAccount;
	}

	public String getUploadPath() {
		return uploadPath;
	}

	public void setUploadPath(String uploadPath) {
		this.uploadPath = uploadPath;
	}

	public void parseXml(XmlPullParser parser) throws Exception {
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			String attName = parser.getAttributeName(i);
			if (attName.equals("APIKey")) {
				this.setApiKey(parser.getAttributeValue(i));
			} else if (attName.equals("SecretKey")) {
				this.setSecretKey(parser.getAttributeValue(i));
			} else if (attName.equals("UploadPath")) {
				this.setUploadPath(parser.getAttributeValue(i));
			} else if (attName.equals("IsDeveloperAccount")) {
				this.setIsDeveloperAccount(stringToBool(parser.getAttributeValue(i)));
			}
		}
	}
	
	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "BaiduYunAccount");
		this.writeAttribute(serializer, "UploadPath", this.uploadPath);
		this.writeAttribute(serializer, "APIKey", this.apiKey);
		this.writeAttribute(serializer, "SecretKey", this.secretKey);
		this.writeAttribute(serializer, "IsDeveloperAccount", this.isDeveloperAccount);
		serializer.endTag(null, "BaiduYunAccount");

	}
}
