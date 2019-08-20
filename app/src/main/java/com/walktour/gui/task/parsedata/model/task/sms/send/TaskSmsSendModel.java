package com.walktour.gui.task.parsedata.model.task.sms.send;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

public class TaskSmsSendModel extends TaskModel{
	private static final long serialVersionUID = -2187834076935817009L;

	public TaskSmsSendModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_None);
		setTaskType(WalkStruct.TaskType.SMSSend.toString());
	}
	@SerializedName("smsSendTestConfig")
	private SMSSendTestConfig smsSendTestConfig=new SMSSendTestConfig();

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @author tangwq
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());

		testTask.append("timeOut =" + smsSendTestConfig.getSendTimeout() + "\r\n");
		testTask.append("serverNumber =" + getServerNumber() + "\r\n");
		testTask.append("desNumber =" + smsSendTestConfig.getReceiverNum() + "\r\n");
		testTask.append("content =" + smsSendTestConfig.getSmsText() + "\r\n");
		return testTask.toString();
	}

	public int getTimeOut() {
		return smsSendTestConfig.getSendTimeout();
	}

	public void setTimeOut(int timeOut) {
		smsSendTestConfig.setSendTimeout(timeOut);
	}

	public String getContent() {
		return smsSendTestConfig.getSmsText();
	}

	public void setContent(String content) {
		smsSendTestConfig.setSmsText(content);
	}

	public String getServerNumber() {
		return smsSendTestConfig.getSmsc();
	}

	public void setServerNumber(String serverNumber) {
		if (serverNumber.length() <= 1) {
			serverNumber = "000";
		}
		smsSendTestConfig.setSmsc(serverNumber);
	}

	public String getDesNumber() {
		return smsSendTestConfig.getReceiverNum();
	}

	public void setDesNumber(String desNumber) {
		if (desNumber.trim().length() == 0) {
			smsSendTestConfig.setReceiverNum("000");
		} else {
			smsSendTestConfig.setReceiverNum(desNumber);
		}
	}

	public SMSSendTestConfig getSmsSendTestConfig() {
		return smsSendTestConfig;
	}

 
	public void parseXml(XmlPullParser parser,List<TaskModel> tasks,Map<String,String> map) throws Exception {

		int eventType = parser.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("SMSSendTestConfig")) { 
					smsSendTestConfig.parseXml(parser);
				} else {// 解析公共属性
					parsrXmlPublic(parser,map);
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
	
	public void writeXml(XmlSerializer serializer)  throws Exception {
		super.writeXml(serializer);
		if(null!=smsSendTestConfig)
			smsSendTestConfig.writeXml(serializer);
		
	}
	
	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.SMSSend.getXmlTaskType();
	}
}
