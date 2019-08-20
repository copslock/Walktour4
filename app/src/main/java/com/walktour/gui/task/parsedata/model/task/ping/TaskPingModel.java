package com.walktour.gui.task.parsedata.model.task.ping;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

public class TaskPingModel extends TaskModel{

	private static final long serialVersionUID = -6493358854761704282L;

	public TaskPingModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		setTaskType(WalkStruct.TaskType.Ping.toString());
	}

	@SerializedName("isUnlimited")
	private boolean isUnlimited = false;
	@SerializedName("pingTestConfig")
	private PingTestConfig pingTestConfig=new PingTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting=new NetworkConnectionSetting();

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @author tangwq
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());
		testTask.append("Repeat =" + (isUnlimited ? 9999 : getRepeat()) + "\r\n");
		testTask.append("timeOut =" + pingTestConfig.getTimeout() + "\r\n");
		testTask.append("ttl =" + pingTestConfig.getTtl() + "\r\n");
		testTask.append("ip =" + pingTestConfig.getPingAddress() + "\r\n");
		testTask.append("size =" + pingTestConfig.getPacketSize_B() + "\r\n");
		testTask.append("accessPoint =" + networkConnectionSetting.getConnectionProtocol() + "\r\n");
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
 
	public int getTimeOut() {
		return pingTestConfig.getTimeout();
	}

	public void setTimeOut(int timeOut) {
		pingTestConfig.setTimeout(timeOut);
	}

	public String getIp() {
		return pingTestConfig.getPingAddress();
	}

	public void setIp(String ip) {
		pingTestConfig.setPingAddress(ip);
	}

	public int getSize() {
		return pingTestConfig.getPacketSize_B();
	}

	public void setSize(int size) {
		pingTestConfig.setPacketSize_B(size);
	}

	public int getTtl() {
		return pingTestConfig.getTtl();
	}

	public String getUEState(){
        return pingTestConfig.getUeState();
    }
    public void setUEState(String ueState){
        pingTestConfig.setUeState(ueState);
    }

    public boolean isATPing(){
        return pingTestConfig.isATPing();
    }
    public void setIsATPing(boolean isATPing){
        pingTestConfig.setATPing(isATPing);
    }

	public void setTtl(int ttl) {
		pingTestConfig.setTtl(ttl);
	}

	public PingTestConfig getPingTestConfig() {
		return pingTestConfig;
	}
 

	public NetworkConnectionSetting getNetworkConnectionSetting() {
		return networkConnectionSetting;
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
				if (tagName.equals("PingTestConfig")) { 
					pingTestConfig.parseXml(parser);
				}else if (tagName.equals("NetworkConnectionSetting")) { 
					for(int i=0;i<parser.getAttributeCount();i++){
						String attName=parser.getAttributeName(i);
						String attValue=parser.getAttributeValue(i);
						if(attName.equals("IsAvailable")){
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
	
	@Override
	public void writeXml(XmlSerializer serializer)  throws Exception {
		super.writeXml(serializer);
		
		if(null!=pingTestConfig)
			pingTestConfig.writeXml(serializer);
		if(null!=networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
		
	}
	
	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.Ping.getXmlTaskType();
	}
}
