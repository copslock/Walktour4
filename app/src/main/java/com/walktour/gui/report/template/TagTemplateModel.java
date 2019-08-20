package com.walktour.gui.report.template;

import com.walktour.Utils.StringUtil;

import org.xmlpull.v1.XmlPullParser;

import java.util.LinkedList;
import java.util.List;

public class TagTemplateModel extends BaseTemplateModel {

	private String tagNameSum="";
	
	private String tagNameSumEN="";
	
	private String tagNamebylog="";
	
	private String tagNamebylogEN="";
	
	private int outputResultCount=0;

	private List<ResultTemplateModel> results=new LinkedList<ResultTemplateModel>();
	public TagTemplateModel() {
		super();
		level=4;
	}
	public String getShowTagNameSum() {
		if (StringUtil.getLanguage().equalsIgnoreCase("cn")) {
			return this.getTagNameSumCN();
		} else
			return this.getTagNameSumEN();
	}
	public String getShowTagNamebylog() {
		if (StringUtil.getLanguage().equalsIgnoreCase("cn")) {
			return this.getTagNamebylogCN();
		} else
			return this.getTagNamebylogEN();
	}
	private String getTagNameSumCN() {
		return tagNameSum;
	}

	private void setTagNameSumCN(String tagNameSum) {
		this.tagNameSum = tagNameSum;
	}

	private String getTagNameSumEN() {
		return tagNameSumEN;
	}

	private void setTagNameSumEN(String tagNameSumEN) {
		this.tagNameSumEN = tagNameSumEN;
	}

	private String getTagNamebylogCN() {
		return tagNamebylog;
	}

	private void setTagNamebylogCN(String tagNamebylog) {
		this.tagNamebylog = tagNamebylog;
	}

	private String getTagNamebylogEN() {
		return tagNamebylogEN;
	}

	private void setTagNamebylogEN(String tagNamebylogEN) {
		this.tagNamebylogEN = tagNamebylogEN;
	}

	public int getOutputResultCount() {
		return outputResultCount;
	}

	public void setOutputResultCount(int outputResultCount) {
		this.outputResultCount = outputResultCount;
	}

	public List<ResultTemplateModel> getResults() {
		return results;
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
				if (tagName.equalsIgnoreCase("tag")) {
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						String attName = parser.getAttributeName(i);
						String attValue = parser.getAttributeValue(i);
						if (attName.equalsIgnoreCase("level")) {
							this.setLevel(Integer.parseInt(attValue));
						} else if (attName.equalsIgnoreCase("code")) {
							this.setCode(attValue);
						} else if (attName.equalsIgnoreCase("TagNameSum")) {
							this.setTagNameSumCN(attValue);
						} else if (attName.equalsIgnoreCase("TagNameSumEN")) {
							this.setTagNameSumEN(attValue);
						} else if (attName.equalsIgnoreCase("TagNamebylog")) {
							this.setTagNamebylogCN(attValue);
						} else if (attName.equalsIgnoreCase("TagNamebylogEN")) {
							this.setTagNamebylogEN(attValue);
						} else if (attName.equalsIgnoreCase("OutputResultCount")) {
							this.setOutputResultCount(Integer.parseInt(attValue));
						}  
					}
				}else if(tagName.equalsIgnoreCase("result")){
					ResultTemplateModel resultModel=new ResultTemplateModel();
					resultModel.parseXml(parser);
					this.getResults().add(resultModel);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equalsIgnoreCase("tag")) {
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
		result = prime * result + outputResultCount;
		result = prime * result + ((results == null) ? 0 : results.hashCode());
		result = prime * result + ((tagNameSum == null) ? 0 : tagNameSum.hashCode());
		result = prime * result + ((tagNameSumEN == null) ? 0 : tagNameSumEN.hashCode());
		result = prime * result + ((tagNamebylog == null) ? 0 : tagNamebylog.hashCode());
		result = prime * result + ((tagNamebylogEN == null) ? 0 : tagNamebylogEN.hashCode());
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
		TagTemplateModel other = (TagTemplateModel) obj;
		if (outputResultCount != other.outputResultCount)
			return false;
		if (results == null) {
			if (other.results != null)
				return false;
		} else if (!results.equals(other.results))
			return false;
		if (tagNameSum == null) {
			if (other.tagNameSum != null)
				return false;
		} else if (!tagNameSum.equals(other.tagNameSum))
			return false;
		if (tagNameSumEN == null) {
			if (other.tagNameSumEN != null)
				return false;
		} else if (!tagNameSumEN.equals(other.tagNameSumEN))
			return false;
		if (tagNamebylog == null) {
			if (other.tagNamebylog != null)
				return false;
		} else if (!tagNamebylog.equals(other.tagNamebylog))
			return false;
		if (tagNamebylogEN == null) {
			if (other.tagNamebylogEN != null)
				return false;
		} else if (!tagNamebylogEN.equals(other.tagNamebylogEN))
			return false;
		return true;
	}
 
}
