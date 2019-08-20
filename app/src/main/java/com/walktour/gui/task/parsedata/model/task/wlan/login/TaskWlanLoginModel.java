package com.walktour.gui.task.parsedata.model.task.wlan.login;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

/***
 * Wlan登录模型
 * 
 * @author weirong.fan
 *
 */
public class TaskWlanLoginModel extends TaskModel {

	private static final long serialVersionUID = -3657402683996806616L;
	public TaskWlanLoginModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_Wap);
		setTaskType(WalkStruct.TaskType.WlanLogin.name());
	}

	@SerializedName("wlanWebLoginTestConfig")
	private WlanWebLoginTestConfig wlanWebLoginTestConfig = new WlanWebLoginTestConfig();
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

		testTask.append("user =" + wlanWebLoginTestConfig.getWlanAccount().getUsername() + "\r\n");
		testTask.append("pass =" + wlanWebLoginTestConfig.getWlanAccount().getPassword() + "\r\n");
		testTask.append("timeOut =" + wlanWebLoginTestConfig.getTimeout() + "\r\n");
		return testTask.toString();
	}
	@Override
	public int getTypeProperty() { 
		if(networkConnectionSetting.getConnectionProtocol().equals(NetworkConnectionSetting.ConnectionProtocol_WLAN))
			return WalkCommonPara.TypeProperty_Wlan;
		return WalkCommonPara.TypeProperty_Wap;
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
	public String getUser() {
		return wlanWebLoginTestConfig.getWlanAccount().getUsername();
	}

	public String getPass() {
		return wlanWebLoginTestConfig.getWlanAccount().getPassword();
	}

	public int getTimeOut() {
		return wlanWebLoginTestConfig.getTimeout();
	}

	public void setUser(String user) {
		wlanWebLoginTestConfig.getWlanAccount().setUsername(user);
	}

	public void setPass(String pass) {
		wlanWebLoginTestConfig.getWlanAccount().setPassword(pass);
	}

	public void setTimeOut(int timeOut) {
		wlanWebLoginTestConfig.setTimeout(timeOut);
	}

	public WlanWebLoginTestConfig getWlanWebLoginTestConfig() {
		return wlanWebLoginTestConfig;
	}
	public NetworkConnectionSetting getNetworkConnectionSetting() {
		return networkConnectionSetting;
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
				if (tagName.equals("WlanWebLoginTestConfig")) {
					wlanWebLoginTestConfig.parseXml(parser);
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
		if (null != wlanWebLoginTestConfig)
			wlanWebLoginTestConfig.writeXml(serializer);
		if(null!=networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}
	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.WlanLogin.getXmlTaskType();
	}
}
