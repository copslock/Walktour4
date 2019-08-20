package com.walktour.gui.task.parsedata.model;

import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/***
 * 测试计划配置
 * 
 * @author weirong.fan
 *
 */
public class TestSchemaConfig extends TaskBase { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5398661113535194814L;
	/** 是否选择 */
	private boolean isChecked = true;
	/**测试计划ID*/
	private String schemaID="";
	/**测试计划描述，用于界面描述**/
	private String schemaName="";
	/**Module端口号**/
	private int portNumber=0;
	/**模块是否打开*/
	private boolean enable=false;
	/** 测试任务组 */
	private List<TaskGroupConfig> taskGroups = new LinkedList<TaskGroupConfig>();

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getSchemaID() {
		return schemaID;
	}

	public void setSchemaID(String schemaID) {
		this.schemaID = schemaID;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public List<TaskGroupConfig> getTaskGroups() {
		TaskGroupConfig comparator= new TaskGroupConfig();
		Collections.sort(taskGroups,comparator);
		comparator=null;
		return taskGroups;
	}

	/***
	 * 解析 TestSchemaConfig
	 * 
	 * @param parser
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
				if (tagName.equals("SchemaID")) {
					this.setSchemaID(parser.nextText());
				} else if (tagName.equals("SchemaName")) {
					this.setSchemaName(parser.nextText());
				} else if (tagName.equals("PortNumber")) {
					this.setPortNumber(stringToInt(parser.nextText()));
				} else if (tagName.equals("Enable")) {
					this.setEnable(stringToBool(parser.nextText()));
				} else if (tagName.equals("TaskGroupConfig")) {
					TaskGroupConfig taskGroupConfig = new TaskGroupConfig();
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						if (parser.getAttributeName(i).equals("IsCheck")) {
							taskGroupConfig.setCheck(stringToBool(parser.getAttributeValue(i)));
						}
					}
					taskGroupConfig.parseXml(parser);
					taskGroups.add(taskGroupConfig);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("TestSchemaConfig")) {
					return;
				}

				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "TestSchemaConfig");
		this.writeAttribute(serializer, "IsCheck", isChecked);
		this.writeTag(serializer, "SchemaID", this.schemaID);
		this.writeTag(serializer, "SchemaName", this.schemaName);
		this.writeTag(serializer, "PortNumber", this.portNumber);
		this.writeTag(serializer, "Enable", boolToText(this.enable));
		serializer.startTag(null, "TaskGroups");
		for (TaskGroupConfig taskGroupConfig : taskGroups) {
			if (null != taskGroupConfig) {
				serializer.startTag(null, "TaskGroupConfig");
				this.writeAttribute(serializer, "IsCheck", taskGroupConfig.isCheck());
				taskGroupConfig.writeXml(serializer);
				serializer.endTag(null, "TaskGroupConfig");
			}
		}
		serializer.endTag(null, "TaskGroups");
		serializer.endTag(null, "TestSchemaConfig");

	}
}
