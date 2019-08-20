package com.walktour.gui.task.parsedata.model.task.sms.sendreceive;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;


/**
 * 短信自发自收测试
 * 
 * */
public class TaskSmsSendReceiveModel extends TaskModel {
	private static final long serialVersionUID = -4726179818719423834L;
	public TaskSmsSendReceiveModel(){
		setTypeProperty(WalkCommonPara.TypeProperty_None);
		setTaskType(WalkStruct.TaskType.SMSSendReceive.toString());
	}
	@SerializedName("serverNumber")
	private String serverNumber="000";			//服务中心号码
	@SerializedName("smsSelfTestConfig")
	private SMSSelfTestConfig smsSelfTestConfig=new SMSSelfTestConfig();
	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * @author tangwq
	 * @return
	 */
	public String getTestPlanStr(){
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());
		
		testTask.append("sendTimeOut ="+ smsSelfTestConfig.getSendTimeout() +"\r\n");
		testTask.append("receiveTimeOut ="+ smsSelfTestConfig.getRecvTimeout() +"\r\n");
		testTask.append("serverNumber ="+ getServerNumber() +"\r\n");
		testTask.append("desNumber ="+ smsSelfTestConfig.getReceiverNum() +"\r\n");
		testTask.append("content ="+ smsSelfTestConfig.getSmsText() +"\r\n");
		return testTask.toString();
	}
	
	public String getContent() {
		return smsSelfTestConfig.getSmsText();
	}
	public void setContent(String content) {
		smsSelfTestConfig.setSmsText(content);
	}
	public String getServerNumber() {
		return smsSelfTestConfig.getSmsc();
	}

	public void setServerNumber(String serverNumber) {
		if (serverNumber.length() <= 1) {
			this.serverNumber = "000";
		} else {
			this.serverNumber = serverNumber;
		}
		smsSelfTestConfig.setSmsc(this.serverNumber);
	}
	public String getDesNumber() {
		return smsSelfTestConfig.getReceiverNum();
	}
	public void setDesNumber(String desNumber) {
		if( desNumber.trim().length()==0 ){
			smsSelfTestConfig.setReceiverNum("000");
		}else{
			smsSelfTestConfig.setReceiverNum(desNumber);
		}
	}
	public int getSendTimeOut() {
		return smsSelfTestConfig.getSendTimeout();
	}
	public void setSendTimeOut(int sendTimeOut) {
		smsSelfTestConfig.setSendTimeout(sendTimeOut);
	}
	public int getReceiveTimeOut() {
		return smsSelfTestConfig.getRecvTimeout();
	}
	public void setReceiveTimeOut(int receiveTimeOut) {
		smsSelfTestConfig.setRecvTimeout(receiveTimeOut);
	}
	
	public SMSSelfTestConfig getSmsSelfTestConfig() {
		return smsSelfTestConfig;
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
				if (tagName.equals("SMSSelfTestConfig")) { 
					smsSelfTestConfig.parseXml(parser);
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
		if(null!=smsSelfTestConfig)
			smsSelfTestConfig.writeXml(serializer);
	
	}
	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.SMSSendReceive.getXmlTaskType();
	}
}
