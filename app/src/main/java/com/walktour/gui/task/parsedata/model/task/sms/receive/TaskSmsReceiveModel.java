package com.walktour.gui.task.parsedata.model.task.sms.receive;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

public class TaskSmsReceiveModel extends TaskModel{
	private static final long serialVersionUID = 2498417208017018938L;

	public TaskSmsReceiveModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_None);
		setTaskType(WalkStruct.TaskType.SMSIncept.toString());
	}

//	private int saveSms; // 保存短信 0：不保存1：保存
	@SerializedName("smsRecvTestConfig")
	private SMSRecvTestConfig smsRecvTestConfig=new SMSRecvTestConfig();

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @author tangwq
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());

		testTask.append("timeOut =" + smsRecvTestConfig.getRecvTimeout() + "\r\n");
//		testTask.append("saveSms =" + saveSms + "\r\n");
		return testTask.toString();
	}

	public int getTimeOut() {
		return smsRecvTestConfig.getRecvTimeout();
	}

	public void setTimeOut(int timeOut) {
		smsRecvTestConfig.setRecvTimeout(timeOut);
	}

//	public int getSaveSms() {
//		return saveSms;
//	}
//
//	public void setSaveSms(int saveSms) {
//		this.saveSms = saveSms;
//	}

	public SMSRecvTestConfig getSmsRecvTestConfig() {
		return smsRecvTestConfig;
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
				if (tagName.equals("SMSRecvTestConfig")) { 
					smsRecvTestConfig.parseXml(parser);
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
		if(null!=smsRecvTestConfig)
			smsRecvTestConfig.writeXml(serializer);
		
	}
	
	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.SMSIncept.getXmlTaskType();
	}
}
