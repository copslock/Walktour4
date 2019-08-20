/**
 * 
 */
package com.walktour.gui.task.parsedata.model.task.speedtest;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;
public class TaskSpeedTestModel extends TaskModel {

	private static final long serialVersionUID = 5111846978865124309L;

	public TaskSpeedTestModel() {
		setTaskName("Speed Test");
		setRepeat(10);
		setInterVal(15);
		setDisConnect(1);
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		setTaskType(WalkStruct.TaskType.SpeedTest.name());
	}
 

	@SerializedName("speedTestConfig")
	private SpeedTestConfig speedTestConfig = new SpeedTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();

	public String getUrl() {
		return speedTestConfig.getUrl();
	}

	public void setUrl(String url) {
		speedTestConfig.setUrl(url);
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

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());

		testTask.append("URL =" + speedTestConfig.getUrl() + "\r\n");
		testTask.append("Country=" + speedTestConfig.getCountry() + "\r\n");
		testTask.append("Name=" + speedTestConfig.getCity() + "\r\t");
		testTask.append("Sponsor=" + getSponsor() + "\r\t");
		return testTask.toString();
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

	public String getCountry() {
		return speedTestConfig.getCountry();
	}

	public void setCountry(String country) {
		speedTestConfig.setCountry(country);
	}

	public String getName() {
		return speedTestConfig.getCity();
	}

	public void setName(String name) {
		speedTestConfig.setCity(name);
	}

	public String getSponsor() {
		return speedTestConfig.getSponsor();
	}

	public void setSponsor(String sponsor) {
		speedTestConfig.setSponsor(sponsor);
	}

	public String getRemoteFile() {
		return speedTestConfig.getDownloadFile();
	}

	public void setRemoteFile(String remoteFile) {
		/*if (remoteFile.equalsIgnoreCase("random350x350.jpg")) {
			speedTestConfig.setDownloadFile("350*350");
		} else if (remoteFile.equalsIgnoreCase("random500x500.jpg")) {
			speedTestConfig.setDownloadFile("500*500");
		} else if (remoteFile.equalsIgnoreCase("random500x500.jpg")) {
			speedTestConfig.setDownloadFile("500*500");
		} else if (remoteFile.equalsIgnoreCase("random750x750.jpg")) {
			speedTestConfig.setDownloadFile("750*750");
		} else if (remoteFile.equalsIgnoreCase("random1000x1000.jpg")) {
			speedTestConfig.setDownloadFile("1000*1000");
		} else if (remoteFile.equalsIgnoreCase("random1500x1500.jpg")) {
			speedTestConfig.setDownloadFile("1500*1500");
		} else if (remoteFile.equalsIgnoreCase("random2000x2000.jpg")) {
			speedTestConfig.setDownloadFile("2000*2000");
		} else if (remoteFile.equalsIgnoreCase("random2500x2500.jpg")) {
			speedTestConfig.setDownloadFile("2500*2500");
		} else if (remoteFile.equalsIgnoreCase("random3000x3000.jpg")) {
			speedTestConfig.setDownloadFile("3000*3000");
		} else if (remoteFile.equalsIgnoreCase("random3500x3500.jpg")) {
			speedTestConfig.setDownloadFile("3500*3500");
		} else if (remoteFile.equalsIgnoreCase("random4000x4000.jpg")) {
			speedTestConfig.setDownloadFile("4000*4000");
		}*/
		speedTestConfig.setDownloadFile(remoteFile);
	}

	
	
	
	public SpeedTestConfig getSpeedTestConfig() {
		return speedTestConfig;
	}

	public NetworkConnectionSetting getNetworkConnectionSetting() {
		return networkConnectionSetting;
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
				if (tagName.equals("SpeedTestConfig")) {
					speedTestConfig.parseXml(parser);
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
		if (null != speedTestConfig)
			speedTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}
	
	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.SpeedTest.getXmlTaskType();
	}
}
