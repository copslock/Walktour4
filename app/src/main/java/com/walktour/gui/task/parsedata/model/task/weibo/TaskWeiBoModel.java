package com.walktour.gui.task.parsedata.model.task.weibo;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

/**
 * WeiBo模板参数对象
 * 
 * @author zhihui.lian
 *
 */
public class TaskWeiBoModel extends TaskModel {

	private static final long serialVersionUID = -4551229577314152562L;
	@SerializedName("weiboTestConfig")
	private WeiboTestConfig weiboTestConfig = new WeiboTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();

	public TaskWeiBoModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		setInterVal(15);
		setDisConnect(1);
		setTaskType(WalkStruct.TaskType.WeiBo.name());
	}

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());
		testTask.append("userName=").append(weiboTestConfig.getMyAccount().getUserName()).append("\r\n");
		testTask.append("password=").append(weiboTestConfig.getMyAccount().getPassword()).append("\r\n");
		testTask.append("fansName=").append(weiboTestConfig.getFansAccount().getUserName()).append("\r\n");
		testTask.append("fansPassword=").append(weiboTestConfig.getFansAccount().getPassword()).append("\r\n");
		testTask.append("picPath=").append(weiboTestConfig.getSendFile()).append("\r\n");
		testTask.append("loginTimeOut=").append(weiboTestConfig.getLoginTimeout()).append("\r\n");
		testTask.append("SendTimeout=").append(weiboTestConfig.getSendTimeout()).append("\r\n");

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

	public WeiboTestConfig getWeiboTestConfig() {
		return weiboTestConfig;
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
				if (tagName.equals("WeiboTestConfig")) {
					weiboTestConfig.parseXml(parser);
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
		if (null != weiboTestConfig)
			weiboTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}

	@Override
	public String getServerTaskType() {
		return WalkStruct.TaskType.WeiBo.getXmlTaskType();
	}

}
