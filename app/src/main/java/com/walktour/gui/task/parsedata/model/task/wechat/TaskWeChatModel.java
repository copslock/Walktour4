package com.walktour.gui.task.parsedata.model.task.wechat;

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
 * 微信模板参数对象
 * 
 * @author jianchao.wang
 *
 */
public class TaskWeChatModel extends TaskModel {

	private static final long serialVersionUID = 1674172176080882059L;
	@SerializedName("wechatTestConfig")
	private WeChatTestConfig wechatTestConfig = new WeChatTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();

	public TaskWeChatModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		setInterVal(15);
		setDisConnect(1);
		setTaskType(WalkStruct.TaskType.WeChat.name());
	}

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());
		testTask.append("operationType=").append(wechatTestConfig.getOperationType()).append("\r\n");
		testTask.append("friendName=").append(wechatTestConfig.getFriendName()).append("\r\n");
		testTask.append("sendText=").append(wechatTestConfig.getSendText()).append("\r\n");
		testTask.append("voiceDuration=").append(wechatTestConfig.getVoiceDuration()).append("\r\n");
		testTask.append("sendPictureType=").append(wechatTestConfig.getSendPictureType()).append("\r\n");
		testTask.append("sendTimeout=").append(wechatTestConfig.getSendTimeout()).append("\r\n");
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

	public WeChatTestConfig getWeChatTestConfig() {
		return wechatTestConfig;
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
				if (tagName.equals("WeChatTestConfig")) {
					wechatTestConfig.parseXml(parser);
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
		if (null != wechatTestConfig)
			wechatTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}

	@Override
	public String getServerTaskType() {
		return WalkStruct.TaskType.WeChat.getXmlTaskType();
	}

	public void setSendPictureType(int sendPictureType) {
		switch (sendPictureType) {
		case 0:
			wechatTestConfig.setSendPictureType(WeChatTestConfig.SEND_PICTURE_TYPE_1M);
			break;
		case 1:
			wechatTestConfig.setSendPictureType(WeChatTestConfig.SEND_PICTURE_TYPE_3M);
			break;
		case 2:
			wechatTestConfig.setSendPictureType(WeChatTestConfig.SEND_PICTURE_TYPE_5M);
			break;
		case 3:
			wechatTestConfig.setSendPictureType(WeChatTestConfig.SEND_PICTURE_TYPE_10M);
			break;
		}
	}

	public int getSendPictureType() {
		String picType = wechatTestConfig.getSendPictureType();
		if (picType.equals(WeChatTestConfig.SEND_PICTURE_TYPE_1M))
			return 0;
		else if (picType.equals(WeChatTestConfig.SEND_PICTURE_TYPE_3M))
			return 1;
		else if (picType.equals(WeChatTestConfig.SEND_PICTURE_TYPE_5M))
			return 2;
		return 3;
	}

	public String getFriendName() {
		return this.wechatTestConfig.getFriendName();
	}

	public void setFriendName(String friendName) {
		this.wechatTestConfig.setFriendName(friendName);
	}

	public String getSendText() {
		return this.wechatTestConfig.getSendText();
	}

	public void setSendText(String sendText) {
		this.wechatTestConfig.setSendText(sendText);
	}

	public int getVoiceDuration() {
		return this.wechatTestConfig.getVoiceDuration();
	}

	public void setVoiceDuration(int voiceDuration) {
		this.wechatTestConfig.setVoiceDuration(voiceDuration);
	}

	public int getSendTimeout() {
		return this.wechatTestConfig.getSendTimeout() / 1000;
	}

	public void setSendTimeout(int sendTimeout) {
		this.wechatTestConfig.setSendTimeout(sendTimeout * 1000);
	}

	public int getOperationType() {
		String operationType = this.wechatTestConfig.getOperationType();
		if (operationType.equals(WeChatTestConfig.OPERATION_TYPE_DEFAULT))
			return 0;
		return 1;
	}

	public void setOperationType(int operationType) {
		switch (operationType) {
		case 0:
			this.wechatTestConfig.setOperationType(WeChatTestConfig.OPERATION_TYPE_DEFAULT);
			break;
		case 1:
			this.wechatTestConfig.setOperationType(WeChatTestConfig.OPERATION_TYPE_PICTURE);
			break;
		}
	}

}
