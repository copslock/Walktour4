package com.walktour.gui.task.parsedata.model.task.pdpactive;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

public class TaskPdpModel extends TaskModel {
	private static final long serialVersionUID = -1308480276327556947L;

	@SerializedName("pdpActiveTestConfig")
	private PDPActiveTestConfig pdpActiveTestConfig = new PDPActiveTestConfig();
	public TaskPdpModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_None);
		setTaskType(WalkStruct.TaskType.PDP.toString());
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

		testTask.append("keepTime =" + pdpActiveTestConfig.getDuration() + "\r\n");
		return testTask.toString();
	}

	public int getKeepTime() {
		return pdpActiveTestConfig.getDuration();
	}

	public void setKeepTime(int keepTime) {
		pdpActiveTestConfig.setDuration(keepTime);
	}

	public int getRateUL() {
		return pdpActiveTestConfig.getQoSRequest().getUlueRate();
	}

	public void setRateUL(int rateUL) {
		pdpActiveTestConfig.getQoSRequest().setUlueRate(rateUL);
	}

	public int getRateDL() {
		return pdpActiveTestConfig.getQoSRequest().getDlueRate();
	}

	public void setRateDL(int rateDL) {
		pdpActiveTestConfig.getQoSRequest().setDlueRate(rateDL);
	}

	public PDPActiveTestConfig getPdpActiveTestConfig() {
		return pdpActiveTestConfig;
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
				if (tagName.equals("PDPActiveTestConfig")) { 
					pdpActiveTestConfig.parseXml(parser);
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
		if (null != pdpActiveTestConfig)
			pdpActiveTestConfig.writeXml(serializer);
	}

	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.PDP.getXmlTaskType();
	}
}
