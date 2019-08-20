package com.walktour.gui.task.parsedata.model;

import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/***
 * 测试计划结构
 * 
 * @author weirong.fan
 *
 */
public class TestSchemas extends TaskBase{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 6916377378381369414L;
	/** 测试计划配置 */
	private TestSchemaConfig testSchemaConfig=new TestSchemaConfig();

	public TestSchemaConfig getTestSchemaConfig() {
		return testSchemaConfig;
	}

 

	/**
	 * 解析TestSchemas
	 * 
	 * @param parser
	 * @return
	 * @throws Exception
	 */
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
				if (tagName.equals("TestSchemaConfig")) { 
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						if (parser.getAttributeName(i).equals("IsCheck")) {
							testSchemaConfig.setChecked(stringToBool(parser.getAttributeValue(i)));
						}
					}
					testSchemaConfig.parseXml(parser);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("TestSchemas")) {
					return;
				} 
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "TestSchemas");
		if (null != testSchemaConfig) {
			testSchemaConfig.writeXml(serializer);
		}
		serializer.endTag(null, "TestSchemas");
	}

}
