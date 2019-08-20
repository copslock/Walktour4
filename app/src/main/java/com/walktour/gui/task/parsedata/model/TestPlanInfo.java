package com.walktour.gui.task.parsedata.model;

import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/***
 * 测试计划信息
 * 
 * @author weirong.fan
 *
 */
public class TestPlanInfo extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 3732137642272928971L;
	/** 测试计划ID **/
	private String testPlanID = "";
	/** 同一测试计划进行修改后的版本号 **/
	private String testPlanVersion = "";
	/** 测试计划格式版本号 */
	private String testPlanFormatVersion = "";
	/** 测试计划创建的时间 **/
	private String createTime = "";
	/** 测试计划最后一次修改的时间 **/
	private String lastUpdateTime = "";

	public String getTestPlanFormatVersion() {
		return testPlanFormatVersion;
	}

	public void setTestPlanFormatVersion(String testPlanFormatVersion) {
		this.testPlanFormatVersion = testPlanFormatVersion;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getTestPlanID() {
		return testPlanID;
	}

	public void setTestPlanID(String testPlanID) {
		this.testPlanID = testPlanID;
	}

	public String getTestPlanVersion() {
		return this.testPlanVersion;
	}

	public void setTestPlanVersion(String testPlanVersion) {
		this.testPlanVersion = testPlanVersion;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	/**
	 * 解析TestPlanInfo
	 * 
	 * @param parser
	 * @return
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
				if (tagName.equals("TestPlanFormatVersion")) {
					this.setTestPlanFormatVersion(parser.nextText());
				} else if (tagName.equals("LastUpdateTime")) {
					this.setLastUpdateTime(parser.nextText());
				} else if (tagName.equals("TestPlanID")) {
					this.setTestPlanID(parser.nextText());
				} else if (tagName.equals("TestPlanVersion")) {
					this.setTestPlanVersion(parser.nextText());
				} else if (tagName.equals("CreateTime")) {
					this.setCreateTime(parser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("TestPlanInfo"))
					return; 
				break;
			}
			eventType = parser.next();
		}
	}

	/***
	 * 生成TestPlanInfo
	 * 
	 * @param serializer
	 * @throws Exception
	 */
	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "TestPlanInfo");
		this.writeTag(serializer, "TestPlanID", this.testPlanID);
		if (null==this.testPlanVersion||this.testPlanVersion.equals(""))
			this.writeTag(serializer, "TestPlanVersion", TaskListDispose.TASK_MODEL_VERSION);
		else
			this.writeTag(serializer, "TestPlanVersion", this.testPlanVersion);
		this.writeTag(serializer, "TestPlanFormatVersion", this.testPlanFormatVersion);
		this.writeTag(serializer, "CreateTime", this.createTime);
		this.writeTag(serializer, "LastUpdateTime", this.lastUpdateTime);
		serializer.endTag(null, "TestPlanInfo");
	}
}
