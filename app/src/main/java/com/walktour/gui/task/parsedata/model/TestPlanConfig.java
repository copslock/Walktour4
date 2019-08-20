package com.walktour.gui.task.parsedata.model;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.Serializable;

/***
 * 测试计划配置
 * 
 * @author weirong.fan
 *
 */
public class TestPlanConfig  implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = -2993978993278253846L;
	/** 测试计划信息 */
	private TestPlanInfo testPlanInfo = new TestPlanInfo();
	/** 测试计划结构 */
	private TestSchemas testSchemas = new TestSchemas();

	public TestPlanInfo getTestPlanInfo() {
		return testPlanInfo;
	}

	 

	public TestSchemas getTestSchemas() {
		return testSchemas;
	}
 

	/**
	 * 解析XML数据,返回当前XML任务列表
	 * 
	 * @return
	 */
	public void parseXml(XmlPullParser parser) throws Exception {
		String tagName = "";
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("TestPlanInfo")) { 
					testPlanInfo.parseXml(parser);
				} else if (tagName.equals("TestSchemas")) { 
					testSchemas.parseXml(parser);
				} 
				break;
			case XmlPullParser.END_TAG:
				break;
			}
			eventType = parser.next();

		}

	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		if (null != testPlanInfo) {
			testPlanInfo.writeXml(serializer);
		}

		if (null != testSchemas) {
			testSchemas.writeXml(serializer);
		}
	}
}
