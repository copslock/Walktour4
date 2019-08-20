package com.walktour.gui.task.parsedata.model.task.facebook;

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
 * FaceBook模板参数对象
 * 
 * @author zhihui.lian
 *
 */
public class TaskFaceBookModel extends TaskModel { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -9062281785817662929L;
	@SerializedName("facebookTestConfig")
	private FacebookTestConfig facebookTestConfig = new FacebookTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();

	public TaskFaceBookModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		setInterVal(15);
		setDisConnect(1);
		setTaskType(WalkStruct.TaskType.Facebook.name());
	}

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());
		testTask.append("user=").append(facebookTestConfig.getMyAccount().getUserName()).append("\r\n");
		testTask.append("password=").append(facebookTestConfig.getMyAccount().getPassword()).append("\r\n");
		testTask.append("appId=").append(getAppId()).append("\r\n");
		testTask.append("appSecret=").append(getAppSecret()).append("\r\n");
		testTask.append("sendContent=").append(getSendContent()).append("\r\n");
		testTask.append("sendPicSizeLevel=").append(getSendPicSizeLevel()).append("\r\n");
		return testTask.toString();
	}

	public void setAppId(String appId) {
		facebookTestConfig.setAppId(appId);
	}

	public String getAppId() {
		return facebookTestConfig.getAppId();
	}

	public void setAppSecret(String appSecret) {
		facebookTestConfig.setAppSecret(appSecret);
	}

	public String getAppSecret() {
		return facebookTestConfig.getAppSecret();
	}

	public void setSendContent(String sendContent) {
		facebookTestConfig.setSendContent(sendContent);
	}

	public String getSendContent() {
		return facebookTestConfig.getSendContent();
	}

	public void setSendPicSizeLevel(int sendPicSizeLevel) {
		switch (sendPicSizeLevel) {
		case 1:
			facebookTestConfig.setSendPicSizeLevel(FacebookTestConfig.SEND_PIC_SIZE_LEVEL_MIDDLE);
			break;
		case 2:
			facebookTestConfig.setSendPicSizeLevel(FacebookTestConfig.SEND_PIC_SIZE_LEVEL_LARGE);
			break;
		default:
			facebookTestConfig.setSendPicSizeLevel(FacebookTestConfig.SEND_PIC_SIZE_LEVEL_SMALL);
			break;
		}
	}

	public int getSendPicSizeLevel() {
		String level = facebookTestConfig.getSendPicSizeLevel();
		if (FacebookTestConfig.SEND_PIC_SIZE_LEVEL_MIDDLE.equals(level))
			return 1;
		else if (FacebookTestConfig.SEND_PIC_SIZE_LEVEL_LARGE.equals(level))
			return 2;
		else
			return 0;
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

	public String getUser() {
		return facebookTestConfig.getMyAccount().getUserName();
	}

	public void setUser(String user) {
		facebookTestConfig.getMyAccount().setUserName(user);
	}

	public String getPassword() {
		return facebookTestConfig.getMyAccount().getPassword();
	}

	public void setPassword(String password) {
		facebookTestConfig.getMyAccount().setPassword(password);
	}

	public FacebookTestConfig getFacebookTestConfig() {
		return facebookTestConfig;
	}

	public void setFacebookTestConfig(FacebookTestConfig facebookTestConfig) {
		this.facebookTestConfig = facebookTestConfig;
	}

	public NetworkConnectionSetting getNetworkConnectionSetting() {
		return networkConnectionSetting;
	}

	public void setNetworkConnectionSetting(NetworkConnectionSetting networkConnectionSetting) {
		this.networkConnectionSetting = networkConnectionSetting;
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
				if (tagName.equals("FacebookTestConfig")) {
					facebookTestConfig.parseXml(parser);
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

	@Override
	public void writeXml(XmlSerializer serializer) throws Exception {
		super.writeXml(serializer);
		if (null != facebookTestConfig)
			facebookTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}

	@Override
	public String getServerTaskType() {
		return WalkStruct.TaskType.Facebook.getXmlTaskType();
	}
}
