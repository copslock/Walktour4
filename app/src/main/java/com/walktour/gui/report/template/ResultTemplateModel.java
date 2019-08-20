package com.walktour.gui.report.template;

import com.walktour.Utils.StringUtil;

import org.xmlpull.v1.XmlPullParser;

/***
 * 结果
 * 
 * @author weirong.fan
 *
 */
public class ResultTemplateModel {

	private String excelTitle = "";
	private String excelTitleEN = "";
	private String descripton = "";
	private String descriptonEN = "";

	/**
	 * 依据语言自动获取Excel标题
	 * 
	 * @return
	 */
	public String getShowExcelTitle() {
		if (StringUtil.getLanguage().equalsIgnoreCase("cn")) {
			return this.getExcelTitleCN();
		} else
			return this.getExcelTitleEN();
	}

	/**
	 * 依据语言自动获取Excel描述
	 * 
	 * @return
	 */
	public String getShowDescripton() {
		if (StringUtil.getLanguage().equalsIgnoreCase("cn")) {
			return this.getDescriptonCN();
		} else
			return this.getDescriptonEN();
	}

	private String getExcelTitleCN() {
		return excelTitle;
	}

	private void setExcelTitleCN(String excelTitle) {
		this.excelTitle = excelTitle;
	}

	private String getExcelTitleEN() {
		return excelTitleEN;
	}

	private void setExcelTitleEN(String excelTitleEN) {
		this.excelTitleEN = excelTitleEN;
	}

	private String getDescriptonCN() {
		return descripton;
	}

	private void setDescriptonCN(String descripton) {
		this.descripton = descripton;
	}

	private String getDescriptonEN() {
		return descriptonEN;
	}

	private void setDescriptonEN(String descriptonEN) {
		this.descriptonEN = descriptonEN;
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
				if (tagName.equalsIgnoreCase("result")) {
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						String attName = parser.getAttributeName(i);
						String attValue = parser.getAttributeValue(i);
						if (attName.equalsIgnoreCase("ExcelTitle")) {
							this.setExcelTitleCN(attValue);
						} else if (attName.equalsIgnoreCase("ExcelTitleEN")) {
							this.setExcelTitleEN(attValue);
						} else if (attName.equalsIgnoreCase("Descripton")) {
							this.setDescriptonCN(attValue);
						} else if (attName.equalsIgnoreCase("DescriptonEN")) {
							this.setDescriptonEN(attValue);
						}
					}
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equalsIgnoreCase("result")) {
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
		int result = 1;
		result = prime * result + ((descripton == null) ? 0 : descripton.hashCode());
		result = prime * result + ((descriptonEN == null) ? 0 : descriptonEN.hashCode());
		result = prime * result + ((excelTitle == null) ? 0 : excelTitle.hashCode());
		result = prime * result + ((excelTitleEN == null) ? 0 : excelTitleEN.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultTemplateModel other = (ResultTemplateModel) obj;
		if (descripton == null) {
			if (other.descripton != null)
				return false;
		} else if (!descripton.equals(other.descripton))
			return false;
		if (descriptonEN == null) {
			if (other.descriptonEN != null)
				return false;
		} else if (!descriptonEN.equals(other.descriptonEN))
			return false;
		if (excelTitle == null) {
			if (other.excelTitle != null)
				return false;
		} else if (!excelTitle.equals(other.excelTitle))
			return false;
		if (excelTitleEN == null) {
			if (other.excelTitleEN != null)
				return false;
		} else if (!excelTitleEN.equals(other.excelTitleEN))
			return false;
		return true;
	}
 
}
