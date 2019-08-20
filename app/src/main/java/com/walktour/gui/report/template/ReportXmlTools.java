package com.walktour.gui.report.template;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * 报表模板样式解析工具类
 * 
 * @author weirong.fan
 *
 */
public class ReportXmlTools {

	private static final ReportXmlTools instance = new ReportXmlTools();
	/**当前报表模板的版本信息**/
	private String version="";
	/**当前报表模板样式信息**/
	private List<GroupTemplateModel> groups = new LinkedList<GroupTemplateModel>();

	private ReportXmlTools() {
		super();
	}

	public static ReportXmlTools getInstance() {
		return instance;
	}

	/**
	 * 获取当前报表模板的版本信息
	 * 
	 * @return
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * 解析样式模板文件
	 * 
	 * @param xmlString
	 *            字符串
	 * @return
	 */
	public List<GroupTemplateModel> parseXml(String xmlString) {
 
		groups.clear();
		XmlPullParser parser = Xml.newPullParser();
		if (null == xmlString || xmlString == "") {
			return groups;
		}
		try {
			String tagName = "";
			parser.setInput(new ByteArrayInputStream(xmlString.getBytes()), "UTF-8");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					tagName = parser.getName();
					break;
				case XmlPullParser.START_TAG:
					tagName = parser.getName();
					if (tagName.equalsIgnoreCase("version")) {
						 for(int i=0;i<parser.getAttributeCount();i++){
							String attName = parser.getAttributeName(i);
							String attValue = parser.getAttributeValue(i);
							if(attName.equalsIgnoreCase("value")){
								this.version=attValue;
							}
						 }
					}else if (tagName.equalsIgnoreCase("group")) {
						GroupTemplateModel group = new GroupTemplateModel();
						group.parseXml(parser);
						this.groups.add(group);
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			groups.clear();
		}
		return groups;
	}
}
