package com.walktour.gui.report.template;

import com.walktour.Utils.StringUtil;

import org.xmlpull.v1.XmlPullParser;

import java.util.LinkedList;
import java.util.List;

public class StyleTemplateModel extends BaseTemplateModel {

	private String styleName = "";

	private String styleNameEN = "";

	private String styleFileName = "";

	private String styleFileNameEN = "";

	private List<TagTemplateModel> tags=new LinkedList<TagTemplateModel>();

	public StyleTemplateModel() {
		super();
		level=3;
	}

	public String getShowStyleName() {
		if (StringUtil.getLanguage().equalsIgnoreCase("cn")) {
			return this.getStyleNameCN();
		} else
			return this.getStyleNameEN();
	}

	public String getStyleFileName() {
		if (StringUtil.getLanguage().equalsIgnoreCase("cn")) {
			return this.getStyleFileNameCN();
		} else
			return this.getStyleFileNameEN();
	}
	private String getStyleNameCN() {
		return styleName;
	}

	private void setStyleNameCN(String styleName) {
		this.styleName = styleName;
	}

	private String getStyleNameEN() {
		return styleNameEN;
	}

	private void setStyleNameEN(String styleNameEN) {
		this.styleNameEN = styleNameEN;
	}

	private String getStyleFileNameCN() {
		return styleFileName;
	}

	private void setStyleFileNameCN(String styleFileName) {
		this.styleFileName = styleFileName;
	}

	private String getStyleFileNameEN() {
		return styleFileNameEN;
	}

	private void setStyleFileNameEN(String styleFileNameEN) {
		this.styleFileNameEN = styleFileNameEN;
	}
	
	public List<TagTemplateModel> getTags() {
		return tags;
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
				if (tagName.equalsIgnoreCase("style")) {
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						String attName = parser.getAttributeName(i);
						String attValue = parser.getAttributeValue(i);
						if (attName.equalsIgnoreCase("level")) {
							this.setLevel(Integer.parseInt(attValue));
						} else if (attName.equalsIgnoreCase("code")) {
							this.setCode(attValue);
						} else if (attName.equalsIgnoreCase("stylename")) {
							this.setStyleNameCN(attValue);
						} else if (attName.equalsIgnoreCase("stylenameen")) {
							this.setStyleNameEN(attValue);
						} else if (attName.equalsIgnoreCase("stylefilename")) {
							this.setStyleFileNameCN(attValue);
						} else if (attName.equalsIgnoreCase("stylefilenameen")) {
							this.setStyleFileNameEN(attValue);
						}
					}
				}else if(tagName.equalsIgnoreCase("tag")){
					TagTemplateModel tagModel=new TagTemplateModel();
					tagModel.parseXml(parser);
					this.getTags().add(tagModel);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equalsIgnoreCase("style")) {
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
		result = prime * result + ((styleFileName == null) ? 0 : styleFileName.hashCode());
		result = prime * result + ((styleFileNameEN == null) ? 0 : styleFileNameEN.hashCode());
		result = prime * result + ((styleName == null) ? 0 : styleName.hashCode());
		result = prime * result + ((styleNameEN == null) ? 0 : styleNameEN.hashCode());
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
		StyleTemplateModel other = (StyleTemplateModel) obj;
		if (styleFileName == null) {
			if (other.styleFileName != null)
				return false;
		} else if (!styleFileName.equals(other.styleFileName))
			return false;
		if (styleFileNameEN == null) {
			if (other.styleFileNameEN != null)
				return false;
		} else if (!styleFileNameEN.equals(other.styleFileNameEN))
			return false;
		if (styleName == null) {
			if (other.styleName != null)
				return false;
		} else if (!styleName.equals(other.styleName))
			return false;
		if (styleNameEN == null) {
			if (other.styleNameEN != null)
				return false;
		} else if (!styleNameEN.equals(other.styleNameEN))
			return false;
		return true;
	}
	 
}
