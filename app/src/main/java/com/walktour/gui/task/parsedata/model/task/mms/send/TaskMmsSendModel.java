package com.walktour.gui.task.parsedata.model.task.mms.send;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

public class TaskMmsSendModel extends TaskModel { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -2871954868925847269L;
	public TaskMmsSendModel(){
		setTypeProperty(WalkCommonPara.TypeProperty_Wap);
		setTaskType(WalkStruct.TaskType.MMSSend.toString());
	}
//	private String serverAddress ="000";	//彩信中心
//	private String gateway ="10.0.0.172";			//网关服务器地址
//	private int port = 0;					//端口
//	private String subject="MMS Test";	//主题
//	private int mediaFileSize=100;			//附件大小,单位KB
//	private int timeOut = 60;				//发送超时
	@SerializedName("reportTime")
	private int reportTime = 60;     		//发送状态报告超时
//	private String destination ="000";			//接收彩信号码
//	private String adjunct="000";			//如果有附件时，此处填附件的路径
//	private String content="Mms Test";			//发送文本内容
//	private int fileSource=1;         //选择文件上传方式  0为本地 1为自动
	@SerializedName("sendNum")
	private String sendNum="";         //选择文件上传方式  0为本地 1为自动

	@SerializedName("mmsSendTestConfig")
	private MMSSendTestConfig mmsSendTestConfig=new MMSSendTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();
	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * @author tangwq
	 * @return
	 */
	public String getTestPlanStr(){
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());
		
		testTask.append("serverAddress ="+ this.getServerAddress() +"\r\n");
		testTask.append("gateway ="+ this.getGateway() +"\r\n");
		testTask.append("port ="+ this.getPort() +"\r\n");
		testTask.append("subject ="+ this.getSubject() +"\r\n");
		testTask.append("mediaFileSize ="+ this.getMediaFileSize() +"\r\n");
		testTask.append("timeOut ="+ this.getTimeOut() +"\r\n");
		testTask.append("reportTime ="+ this.getReportTime() +"\r\n");
		testTask.append("destination ="+ this.getDestination() +"\r\n");
		testTask.append("adjunct ="+ this.getAdjunct() +"\r\n");
		testTask.append("content ="+ this.getContent() +"\r\n");
		return testTask.toString();
	}
	
	//保留，未知是否会用
/*	private String subject="MMS Test";	//主题
	private int connectTimeOut;			//连接网关限时
*/	
	/***
	 * 数据业务复写这两个方法，以适配历史的业务
	 */
	@Override
	public int getDisConnect() {
		return networkConnectionSetting.getDisConnect();
	}

	public void setDisConnect(int disConnect) {
		networkConnectionSetting.setDisConnect(disConnect);
	}
	@Override
	public int getTypeProperty() {
		if(networkConnectionSetting.getConnectionProtocol().equals(NetworkConnectionSetting.ConnectionProtocol_WLAN))
			return WalkCommonPara.TypeProperty_Wlan;
		else if(networkConnectionSetting.getConnectionProtocol().equals(NetworkConnectionSetting.ConnectionProtocol_PPPNB)){
			return WalkCommonPara.TypeProperty_Ppp;
		}
		return WalkCommonPara.TypeProperty_Net;
	}



	@Override
	public void setTypeProperty(int typeProperty) {
		if(typeProperty==WalkCommonPara.TypeProperty_Wlan){
			networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_WLAN);
		} else if(typeProperty==WalkCommonPara.TypeProperty_Ppp){
			networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_PPPNB);
		}else{
			networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_PPP);
		}
	}
	public NetworkConnectionSetting getNetworkConnectionSetting() {
		return networkConnectionSetting;
	}
	
	
	public int getTimeOut() {
		return mmsSendTestConfig.getSendTimeout();
	}
	public void setTimeOut(int timeOut) { 
		mmsSendTestConfig.setSendTimeout(timeOut);
	}
	public String getServerAddress() {
		return mmsSendTestConfig.getMmsc();
	}
	public void setServerAddress(String serverAddress) {
		if( serverAddress.trim().length()==0 ){
			return ;
		}
		mmsSendTestConfig.setMmsc(serverAddress); 
		
	}
	public String getGateway() {
		return mmsSendTestConfig.getGatewaySetting().getGateWayIp();
	}
	public void setGateway(String gateway) {
		if( gateway.trim().length()==0 ){
			return ;
		} 
		mmsSendTestConfig.getGatewaySetting().setGateWayIp(gateway);
	}
	public int getPort() {
		return mmsSendTestConfig.getGatewaySetting().getGateWayPort();
	}
	public void setPort(int port) { 
		mmsSendTestConfig.getGatewaySetting().setGateWayPort(port);
	}
	public String getDestination() {
		return mmsSendTestConfig.getReceiveNumber();
	}
	public void setDestination(String destination) {
		if( destination.trim().length()==0 ){
			return ;
		} 
		mmsSendTestConfig.setReceiveNumber(destination);
	}
	public int getMediaFileSize() {
		return mmsSendTestConfig.getFileSize();
	}
	public void setMediaFileSize(int mediaFileSize) { 
		mmsSendTestConfig.setFileSize(mediaFileSize);
	}
	public int getReportTime() {
		return reportTime;
	}
	public void setReportTime(int reportTime) {
		this.reportTime = reportTime;
	}
	public  String getSubject() {
		return mmsSendTestConfig.getSubject();
	}
	public  void setSubject(String subject) {
		if( subject.trim().length()==0 ){
			return ;
		} 
		mmsSendTestConfig.setSubject(subject);
	}
	public String getAdjunct() {
		
		return mmsSendTestConfig.getLocalFile();
	}
	public void setAdjunct(String adjunct) {
		if( adjunct.trim().length()==0 ){
			return ;
		} 
		mmsSendTestConfig.setLocalFile(adjunct);
	}
	public String getContent() {
		return mmsSendTestConfig.getContent();
	}
	public void setContent(String content) {
		if( content.trim().length()==0 ){
			return ;
		} 
		mmsSendTestConfig.setContent(content);
	}
	
	/**
	 * @return the fileSource
	 */
	public int getFileSource() {
		if (mmsSendTestConfig.getFileSource().equalsIgnoreCase(MMSSendTestConfig.FILESOURCE_LOCAL)) {
			return 0;
		}
		return 1;
	}

	/**
	 * @param fileSource the fileSource to set
	 */
	public void setFileSource(int fileSource) { 
		switch (fileSource) {
		case 0:
			mmsSendTestConfig.setFileSource(MMSSendTestConfig.FILESOURCE_LOCAL);
			break;
		case 1:
			mmsSendTestConfig.setFileSource(MMSSendTestConfig.fileSource_Creat);
			break;
		default:
			break;
		}
	}

	public String getSendNum() {
		return sendNum;
	}

	public void setSendNum(String sendNum) {
		this.sendNum = sendNum;
	}

	public MMSSendTestConfig getMmsSendTestConfig() {
		return mmsSendTestConfig;
	}
 
	
	
	public void parseXml(XmlPullParser parser, List<TaskModel> tasks,Map<String,String> map) throws Exception {
		int eventType = parser.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("MMSSendTestConfig")) { 
					mmsSendTestConfig.parseXml(parser);
				} else if (tagName.equals("NetworkConnectionSetting")) {
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						String attName = parser.getAttributeName(i);
						String attValue = parser.getAttributeValue(i);
						if (attName.equals("IsAvailable")) {
							networkConnectionSetting.setAvailable(stringToBool(attValue));
						}
					}
					networkConnectionSetting.parseXml(parser);
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

	public void writeXml(XmlSerializer serializer) throws Exception {
		super.writeXml(serializer);
		if (null != mmsSendTestConfig)
			mmsSendTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
		
	}
	
	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.MMSSend.getXmlTaskType();
	}

}
