package com.walktour.gui.task.parsedata.model.task.mms.receive;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

public class TaskMmsReceiveModel extends TaskModel { 

	/**
	 * 
	 */
	private static final long serialVersionUID = -1114882595444922200L;

	public TaskMmsReceiveModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_Wap);
		setInterVal(3); // 固定间隔时长为"3"
		setTaskType(WalkStruct.TaskType.MMSIncept.toString());
	}

	@SerializedName("connectTimeOut")
	private int connectTimeOut = 60; // 连接网关限时 
	@SerializedName("mmsReceiveTestConfig")
	private MMSReceiveTestConfig mmsReceiveTestConfig = new MMSReceiveTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @author tangwq
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());
		testTask.append("timeOut =" + mmsReceiveTestConfig.getReceiveTimeout() + "\r\n");
		testTask.append("connectTimeOut =" + connectTimeOut + "\r\n");
		testTask.append("pushTimeOut =" + mmsReceiveTestConfig.getPushTimeout() + "\r\n");
		testTask.append("serverAddress =" + getServerAddress() + "\r\n");
		testTask.append("gateway =" + mmsReceiveTestConfig.getGatewaySetting().getGateWayIp() + "\r\n");
		testTask.append("port =" + mmsReceiveTestConfig.getGatewaySetting().getGateWayPort() + "\r\n");
		return testTask.toString();
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

	public NetworkConnectionSetting getNetworkConnectionSetting() {
		return networkConnectionSetting;
	}

	public int getTimeOut() {
		return mmsReceiveTestConfig.getReceiveTimeout();
	}

	public void setTimeOut(int timeOut) {
		mmsReceiveTestConfig.setReceiveTimeout(timeOut);
	}

	public int getConnectTimeOut() {
		return connectTimeOut;
	}

	public void setConnectTimeOut(int connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}

	public int getPushTimeOut() {
		return mmsReceiveTestConfig.getPushTimeout();
	}

	public void setPushTimeOut(int pushTimeOut) {
		mmsReceiveTestConfig.setPushTimeout(pushTimeOut);
	}

	public String getServerAddress() {
		return mmsReceiveTestConfig.getMmsc();
	}

	public void setServerAddress(String serverAddress) {
		if (serverAddress.trim().length() == 0) {
			return;
		}
		mmsReceiveTestConfig.setMmsc(serverAddress);
	}

	public String getGateway() {
		return mmsReceiveTestConfig.getGatewaySetting().getGateWayIp();
	}

	public void setGateway(String gateway) {
		if (gateway.trim().length() == 0) {
			return;
		}
		mmsReceiveTestConfig.getGatewaySetting().setGateWayIp(gateway);
	}

	public int getPort() {
		return mmsReceiveTestConfig.getGatewaySetting().getGateWayPort();
	}

	public void setPort(int port) {
		mmsReceiveTestConfig.getGatewaySetting().setGateWayPort(port);
		mmsReceiveTestConfig.getGatewaySetting().setWapVersion("2.0");
	}

	public MMSReceiveTestConfig getMmsReceiveTestConfig() {
		return mmsReceiveTestConfig;
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
				if (tagName.equals("MMSReceiveTestConfig")) {
					mmsReceiveTestConfig.parseXml(parser);
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
		if (null != mmsReceiveTestConfig)
			mmsReceiveTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}
	
	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.MMSIncept.getXmlTaskType();
	}

}
