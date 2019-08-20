package com.walktour.gui.task.parsedata.model.task.attach;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

public class TaskAttachModel extends TaskModel { 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5770105616608521933L;
	@SerializedName("attachTestConfig")
	private AttachTestConfig attachTestConfig = new AttachTestConfig();

	/**
	 * 构造器
	 */
	public TaskAttachModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_None);
		this.setRepeat(1);
		setTaskType(WalkStruct.TaskType.Attach.toString());
	}

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @author tangwq
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());
		testTask.append("KeepTime =" + attachTestConfig.getKeepTime() + "\r\n");
		return testTask.toString();
	}

	public AttachTestConfig getAttachTestConfig() {
		return attachTestConfig;
	}

	public void parseXml(XmlPullParser parser, List<TaskModel> tasks, Map<String, String> map) throws Exception {
		int eventType = parser.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("AttachTestConfig")) {
					attachTestConfig.parseXml(parser);
				} else {// 解析公共属性
					parsrXmlPublic(parser, map);
				}

				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("TaskConfig")) {
					tasks.add(this);
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		super.writeXml(serializer);
		if (null != attachTestConfig)
			attachTestConfig.writeXml(serializer);
	}

	@Override
	public String getServerTaskType() {
		return WalkStruct.TaskType.Attach.getXmlTaskType();
	}
}
