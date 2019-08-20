package com.walktour.gui.report.template;

import com.walktour.Utils.StringUtil;

import org.xmlpull.v1.XmlPullParser;

import java.util.LinkedList;
import java.util.List;

public class BusinessTemplateModel extends BaseTemplateModel {

	private String businessName="";
	
	private String businessNameEN="";

	private List<StyleTemplateModel> styles=new LinkedList<StyleTemplateModel>();
	
	public BusinessTemplateModel() {
		super();
		level=2;
	}

	public String getShowBusinessName() {
		if (StringUtil.getLanguage().equalsIgnoreCase("cn")) {
			return this.getBusinessNameCN();
		} else
			return this.getBusinessNameEN();
	}
	
	public String getBusinessNameCN() {
		return businessName;
	}

	public void setBusinessNameCN(String businessName) {
		this.businessName = businessName;
	}

	public String getBusinessNameEN() {
		return businessNameEN;
	}

	public void setBusinessNameEN(String businessNameEN) {
		this.businessNameEN = businessNameEN;
	}
	
	 
	public List<StyleTemplateModel> getStyles() {
		return styles;
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
				if (tagName.equalsIgnoreCase("business")) {
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						String attName = parser.getAttributeName(i);
						String attValue = parser.getAttributeValue(i);
						if (attName.equalsIgnoreCase("level")) {
							this.setLevel(Integer.parseInt(attValue));
						} else if (attName.equalsIgnoreCase("code")) {
							this.setCode(attValue);
						} else if (attName.equalsIgnoreCase("businessName")) {
							this.setBusinessNameCN(attValue);
						} else if (attName.equalsIgnoreCase("businessNameEN")) {
							this.setBusinessNameEN(attValue);
						}
					}
				}else if(tagName.equalsIgnoreCase("style")){
					StyleTemplateModel styleModel=new StyleTemplateModel();
					styleModel.parseXml(parser);
					this.getStyles().add(styleModel);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equalsIgnoreCase("business")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((businessName == null) ? 0 : businessName.hashCode());
		result = prime * result + ((businessNameEN == null) ? 0 : businessNameEN.hashCode());
		result = prime * result + ((styles == null) ? 0 : styles.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BusinessTemplateModel other = (BusinessTemplateModel) obj;
		if (businessName == null) {
			if (other.businessName != null)
				return false;
		} else if (!businessName.equals(other.businessName))
			return false;
		if (businessNameEN == null) {
			if (other.businessNameEN != null)
				return false;
		} else if (!businessNameEN.equals(other.businessNameEN))
			return false;
		if (styles == null) {
			if (other.styles != null)
				return false;
		} else if (!styles.equals(other.styles))
			return false;
		return true;
	}
	
}
