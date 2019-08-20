package com.walktour.gui.task.parsedata.model.task.wlan.ap;

import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

/**
 * WLAN AP关联测试
 * 
 * @author weirong.fan
 *
 */
public class TaskWlanApModel extends TaskModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3284291244406126386L;
	private WlanAPRelationTestConfig wlanAPRelationTestConfig=new WlanAPRelationTestConfig();
	private NetworkConnectionSetting networkConnectionSetting=new NetworkConnectionSetting();
	/**
	 * 构造器
	 */
	public TaskWlanApModel() { 
		setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
		setTaskType(WalkStruct.TaskType.WlanAP.name());
	}
	/**链接AP后的持续时间**/
	private int holdTime=30;
	public int getTimeOut() {
		return wlanAPRelationTestConfig.getTimeout();
	}
	public void setTimeOut(int timeOut) {
		wlanAPRelationTestConfig.setTimeout(timeOut);
	}
	public int getHoldTime() {
		return holdTime;
	}
	public void setHoldTime(int holdTime) {
		this.holdTime = holdTime;
	}
	@Override
	public int getTypeProperty() { 
		if(networkConnectionSetting.getConnectionProtocol().equals(NetworkConnectionSetting.ConnectionProtocol_WLAN))
			return WalkCommonPara.TypeProperty_Wlan;
		return WalkCommonPara.TypeProperty_Net;
	}



	@Override
	public void setTypeProperty(int typeProperty) {
		if(typeProperty==WalkCommonPara.TypeProperty_Wlan){
			networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_WLAN);
			networkConnectionSetting.setConnectionUseWifi(true);
		}else{
			networkConnectionSetting.setConnectionUseWifi(false);
			networkConnectionSetting.setConnectionProtocol(NetworkConnectionSetting.ConnectionProtocol_PPP);
		} 
	}
	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @return
	 */
	public String getTestPlanStr(){
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());
		testTask.append("holdTime ="+ holdTime +"\r\n"); 
		testTask.append("timeOut ="+ wlanAPRelationTestConfig.getTimeout() +"\r\n");
		return testTask.toString();
	}

	public WlanAPRelationTestConfig getWlanAPRelationTestConfig() {
		return wlanAPRelationTestConfig;
	}

	public NetworkConnectionSetting getNetworkConnectionSetting() {
		return networkConnectionSetting;
	}
	/***
	 * 数据业务复写这两个方法，以适配历史的业务
	 */
	@Override
	public int getDisConnect() {
		if(null==networkConnectionSetting)
			return 0;
		return networkConnectionSetting.getDisConnect();
	}

	public void setDisConnect(int disConnect) {
		networkConnectionSetting.setDisConnect(disConnect);
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
				if (tagName.equals("WlanAPRelationTestConfig")) { 
					wlanAPRelationTestConfig.parseXml(parser);
				}else if (tagName.equals("NetworkConnectionSetting")) { 
					for(int i=0;i<parser.getAttributeCount();i++){
						String attName=parser.getAttributeName(i);
						String attValue=parser.getAttributeValue(i);
						if(attName.equals("IsAvailable")){
							networkConnectionSetting.setAvailable(stringToBool(attValue));
						}
					}
					networkConnectionSetting.parseXml(parser);
					
				}else {// 解析公共属性
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
		if (null != wlanAPRelationTestConfig)
			wlanAPRelationTestConfig.writeXml(serializer);
		if(null!=networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}
	
	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.WlanAP.getXmlTaskType();
	}
}
