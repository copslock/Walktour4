package com.walktour.gui.task.parsedata.model.task.ott;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

/**
 * Ott 模板参数对象
 * 
 * @author bin.li
 *
 */
public class TaskMultipleAppTestModel extends TaskModel {

	private static final long serialVersionUID = -3762844235931396752L;

	@SerializedName("multipleAppTestConfig")
	private MultipleAppTestConfig multipleAppTestConfig = new MultipleAppTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();

	public TaskMultipleAppTestModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
	}

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());
		testTask.append("taskName=").append(multipleAppTestConfig.getMultipleAppTestTaskName()).append("\r\n");
		testTask.append("contactName=").append(multipleAppTestConfig.getContactName()).append("\r\n");
		testTask.append("startAppMode=").append(multipleAppTestConfig.getStartAppMode()).append("\r\n");
		testTask.append("sendText=").append(multipleAppTestConfig.getSendText()).append("\r\n");
		testTask.append("voiceDuration=").append(multipleAppTestConfig.getVoiceDuration()).append("\r\n");
		testTask.append("sendPictureType=").append(multipleAppTestConfig.getSendPictureType()).append("\r\n");
		testTask.append("taskTimeout=").append(multipleAppTestConfig.getTaskTimeOut()).append("\r\n");
		testTask.append("audioCallMode=").append(multipleAppTestConfig.getAudioCallMode()).append("\r\n");
		testTask.append("audioCallSeconds=").append(multipleAppTestConfig.getAudioCallSeconds()).append("\r\n");
		testTask.append("dialMode=").append(multipleAppTestConfig.getDialMode()).append("\r\n");
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
//		return networkConnectionSetting.getDisConnect();
        return 0;
	}

//	public void setDisConnect(int disConnect) {
//		networkConnectionSetting.setDisConnect(disConnect);
//	}

	public MultipleAppTestConfig getMultipleAppTestConfig() {
		return multipleAppTestConfig;
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
				if (tagName.equals("MultipleAppTestConfig")) {
					multipleAppTestConfig.parseXml(parser);
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
		if (null != multipleAppTestConfig)
			multipleAppTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}

	@Override
	public String getServerTaskType() {
		return getTaskType();
	}

	public void setSendPictureType(int sendPictureType) {
		switch (sendPictureType) {
		case 0:
			multipleAppTestConfig.setSendPictureType(MultipleAppTestConfig.SEND_PICTURE_TYPE_1M);
			break;
		case 1:
			multipleAppTestConfig.setSendPictureType(MultipleAppTestConfig.SEND_PICTURE_TYPE_3M);
			break;
		}
	}
	public String getContactName() {
		return multipleAppTestConfig.getContactName();
	}

	public void setContactName(String contactName) {
		multipleAppTestConfig.setContactName(contactName);
	}


	public int getSendPictureType() {
		String picType = multipleAppTestConfig.getSendPictureType();
		if (picType.equals(MultipleAppTestConfig.SEND_PICTURE_TYPE_1M))
			return 0;
		else if (picType.equals(MultipleAppTestConfig.SEND_PICTURE_TYPE_3M))
			return 1;
		return 0;
	}

    public int getDialMode(){
        return this.multipleAppTestConfig.getDialMode();
    }

    public void setDialMode(int dialMode){
        this.multipleAppTestConfig.setDialMode(dialMode);
    }

	public int getAudioCallMode() {
		return this.multipleAppTestConfig.getAudioCallMode();
	}

	public void setAudioCallMode(int audioCallMode) {
		this.multipleAppTestConfig.setAudioCallMode(audioCallMode);
	}

	public int getAudioCallSeconds() {
		return this.multipleAppTestConfig.getAudioCallSeconds();
	}

	public void setAudioCallSeconds(int audioCallSeconds) {
		this.multipleAppTestConfig.setAudioCallSeconds(audioCallSeconds);
	}

	public String getSendText() {
		return this.multipleAppTestConfig.getSendText();
	}
	public void setSendText(String sendText) {
		this.multipleAppTestConfig.setSendText(sendText);
	}

	public int getVoiceDuration() {
		return this.multipleAppTestConfig.getVoiceDuration();
	}
	public void setVoiceDuration(int voiceDuration) { this.multipleAppTestConfig.setVoiceDuration(voiceDuration); }

	public int getTaskTimeout() {
		return this.multipleAppTestConfig.getTaskTimeOut() / 1000;
	}
	public void setTaskTimeout(int taskTimeout) {
		this.multipleAppTestConfig.setTaskTimeOut(taskTimeout * 1000);
	}

	public int getStartAppMode() {
		return this.multipleAppTestConfig.getStartAppMode();
	}
	public void setStartAppMode(int startappmode) {
		this.multipleAppTestConfig.setStartAppMode(startappmode);
	}
}
