package com.walktour.gui.report.template;

import com.walktour.Utils.StringUtil;

import org.xmlpull.v1.XmlPullParser;

import java.util.LinkedList;
import java.util.List;

/***
 * 业务模板组描述文件-一级
 * 
 * @author weirong.fan
 *
 */
public class GroupTemplateModel extends BaseTemplateModel {

	private String groupName = "";

	private String groupNameEN = "";

	private List<BusinessTemplateModel> businesses = new LinkedList<BusinessTemplateModel>();

	public GroupTemplateModel() {
		super();
		level = 1;
	}

	public String getShowGroupName() {
		if (StringUtil.getLanguage().equalsIgnoreCase("cn")) {
			return this.getGroupNameCN();
		} else
			return this.getGroupNameEN();
	}

	public String getGroupNameCN() {
		return groupName;
	}

	public void setGroupNameCN(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupNameEN() {
		return groupNameEN;
	}

	public void setGroupNameEN(String groupNameEN) {
		this.groupNameEN = groupNameEN;
	}

	public List<BusinessTemplateModel> getBusinesses() {
		return businesses;
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
				if (tagName.equalsIgnoreCase("group")) {
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						String attName = parser.getAttributeName(i);
						String attValue = parser.getAttributeValue(i);
						if (attName.equalsIgnoreCase("level")) {
							this.setLevel(Integer.parseInt(attValue));
						} else if (attName.equalsIgnoreCase("code")) {
							this.setCode(attValue);
						} else if (attName.equalsIgnoreCase("GroupName")) {
							this.setGroupNameCN(attValue);
						} else if (attName.equalsIgnoreCase("GroupNameEN")) {
							this.setGroupNameEN(attValue);
						}
					}
				} else if (tagName.equalsIgnoreCase("business")) {
					BusinessTemplateModel buninessModel = new BusinessTemplateModel();
					buninessModel.parseXml(parser);
					this.getBusinesses().add(buninessModel);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equalsIgnoreCase("group")) {
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
		result = prime * result + ((businesses == null) ? 0 : businesses.hashCode());
		result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
		result = prime * result + ((groupNameEN == null) ? 0 : groupNameEN.hashCode());
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
		GroupTemplateModel other = (GroupTemplateModel) obj;
		if (businesses == null) {
			if (other.businesses != null)
				return false;
		} else if (!businesses.equals(other.businesses))
			return false;
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		if (groupNameEN == null) {
			if (other.groupNameEN != null)
				return false;
		} else if (!groupNameEN.equals(other.groupNameEN))
			return false;
		return true;
	}
}
