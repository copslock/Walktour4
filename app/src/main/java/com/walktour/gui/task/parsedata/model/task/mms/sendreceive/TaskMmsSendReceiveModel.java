package com.walktour.gui.task.parsedata.model.task.mms.sendreceive;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

public class TaskMmsSendReceiveModel extends TaskModel { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 7706558232589635003L;

	public TaskMmsSendReceiveModel(){
		setTypeProperty(WalkCommonPara.TypeProperty_Wap);
		setTaskType(WalkStruct.TaskType.MMSSendReceive.toString());
	}
	
//	private String serverAddress= UNKNOWN_VALUE;		//彩信中心
//	private String gateway=UNKNOWN_VALUE;			//网关服务器地址
//	private int port;					//端口
//	private String destination=UNKNOWN_VALUE ;		//目标号码
//	private String subject="MMS Test";	//主题
//	private String adjunct=UNKNOWN_VALUE;			//如果有附件时，此处填附件的路径
//	private int fileSize;				//附件大小 (KByte)
//	private int connectTimeout;			//连接网关超时
//	private String content="MMS Test";			//发送文本内容
//	private int sendTimeout;			//发送超时
	@SerializedName("reportTimeout")
	private int reportTimeout;			//报告超时
//	private int pushTimeout;			//push超时
//	private int receiveTimeout;			//接收超时
	@SerializedName("mmsSelfTestConfig")
	private MMSSelfTestConfig mmsSelfTestConfig=new MMSSelfTestConfig();
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
		testTask.append("destination ="+ this.getDestination() +"\r\n");
		testTask.append("subject ="+ this.getSubject() +"\r\n");
		testTask.append("adjunct ="+ this.getAdjunct() +"\r\n");
		testTask.append("fileSize ="+ this.getFileSize() +"\r\n");
		testTask.append("connectTimeout ="+ this.getConnectTimeout() +"\r\n");
		testTask.append("content ="+ this.getContent() +"\r\n");
		testTask.append("sendTimeout ="+ this.getSendTimeout() +"\r\n");
		testTask.append("reportTimeout ="+ this.getReportTimeout() +"\r\n");
		testTask.append("pushTimeout ="+ this.getPushTimeout() +"\r\n");
		testTask.append("receiveTimeout ="+ this.getReceiveTimeout() +"\r\n");
		return testTask.toString();
	}
	/***
	 * 数据业务复写这两个方法，以适配历史的业务
	 */
	@Override
	public int getDisConnect() {
		return networkConnectionSetting.getDisConnect();
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
	public void setDisConnect(int disConnect) {
		networkConnectionSetting.setDisConnect(disConnect);
	}

	public NetworkConnectionSetting getNetworkConnectionSetting() {
		return networkConnectionSetting;
	}
	
	public int getConnectTimeout() {
		return mmsSelfTestConfig.getReceiveTimeout();
	}
	public void setConnectTimeout(int connectTimeout) {
		mmsSelfTestConfig.setReceiveTimeout(connectTimeout);
	}
	public synchronized String getAdjunct() {
		return mmsSelfTestConfig.getLocalFile();
	}
	public synchronized void setAdjunct(String adjunct) {
		if( adjunct.trim().length()==0 ){
			return;
		}
		mmsSelfTestConfig.setLocalFile(adjunct);
	}
	public String getDestination() {
		return mmsSelfTestConfig.getReceiveNumber();
	}
	public void setDestination(String destination) {
		if( destination.trim().length()== 0 ){
			return;
		}
		mmsSelfTestConfig.setReceiveNumber(destination);
	}
	public String getSubject() {
		return mmsSelfTestConfig.getSubject();
	}
	public void setSubject(String subject) {
		if( subject.trim().length()==0 ){
			return;
		}
		mmsSelfTestConfig.setSubject(subject);
	}
	public int getFileSize() {
		return mmsSelfTestConfig.getFileSize();
	}
	public void setFileSize(int fileSize) {
		mmsSelfTestConfig.setFileSize(fileSize);
	}
	public int getSendTimeout() {
		return mmsSelfTestConfig.getSendTimeout();
	}
	public void setSendTimeout(int sendTimeout) {
		mmsSelfTestConfig.setSendTimeout(sendTimeout);
	}
	public int getReportTimeout() {
		return reportTimeout;
	}
	public void setReportTimeout(int reportTimeout) {
		this.reportTimeout = reportTimeout;
	}
	public int getPushTimeout() {
		return mmsSelfTestConfig.getPushTimeout();
	}
	public void setPushTimeout(int pushTimeout) {
		mmsSelfTestConfig.setPushTimeout(pushTimeout);
	}
	public int getReceiveTimeout() {
		return mmsSelfTestConfig.getReceiveTimeout();
	}
	public void setReceiveTimeout(int receiveTimeout) {
		mmsSelfTestConfig.setReceiveTimeout(receiveTimeout);
	}
	public String getServerAddress() {
		return mmsSelfTestConfig.getMmsc();
	}
	public void setServerAddress(String serverAddress) {
		if( serverAddress.trim().length()==0 ){
			return;
		}
		mmsSelfTestConfig.setMmsc(serverAddress);
	}
	public String getGateway() {
		return  mmsSelfTestConfig.getGatewaySetting().getGateWayIp();
	}
	public void setGateway(String gateway) {
		if( gateway.trim().length()==0 ){
			return;
		}
		mmsSelfTestConfig.getGatewaySetting().setGateWayIp(gateway);
	}
	public int getPort() {
		return mmsSelfTestConfig.getGatewaySetting().getGateWayPort();
	}
	public void setPort(int port) {
		mmsSelfTestConfig.getGatewaySetting().setGateWayPort(port);
	}
	public String getContent() {
		return mmsSelfTestConfig.getContent();
	}
	public void setContent(String content) {
		if( content.trim().length() == 0 ){
			return;
		}
		mmsSelfTestConfig.setContent(content);
	}

	public MMSSelfTestConfig getMmsSelfTestConfig() {
		return mmsSelfTestConfig;
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
				if (tagName.equals("MMSSelfTestConfig")) { 
					mmsSelfTestConfig.parseXml(parser);
				}  else if (tagName.equals("NetworkConnectionSetting")) {
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
		if (null != mmsSelfTestConfig)
			mmsSelfTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}
	
	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.MMSSendReceive.getXmlTaskType();
	}

}
