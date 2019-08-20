package com.walktour.gui.task.parsedata.model.task.pbm;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * PBM测试模型
 * 
 * @author jianchao.wang
 * 
 */
public class TaskPBMModel extends TaskModel {
	private static final long serialVersionUID = -3769421681343459579L;
	/** 方向 :上行 */
	public static final int DIRECTION_UP = 0;
	/** 方向 :下行 */
	public static final int DIRECTION_DOWN = 1;

	/** 采样间隔(秒) */
	@SerializedName("sampleInterval")
	private int sampleInterval = 1;
	/** 方向 ：上行 0 下行 1 */
	@SerializedName("direction")
	private int direction = DIRECTION_DOWN;
	/** 测试模式 0:上行 1:下行 2:上下行。注：必须设2，界面不应公开此选项。 */
	@SerializedName("testMode")
	private int testMode = 2;

	@SerializedName("pbmTestConfig")
	private PBMTestConfig pbmTestConfig = new PBMTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();

	public TaskPBMModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		setTaskType(WalkStruct.TaskType.PBM.toString());
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
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
	 * @author jianchao.wang
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());

		testTask.append("duration =" + pbmTestConfig.getDuration() + "\r\n");
		testTask.append("sampleInterval =" + this.sampleInterval + "\r\n");
		if(pbmTestConfig.getServerList().size()>0){
		testTask.append("server_ip =" + pbmTestConfig.getServerList().get(0).getServerIP() + "\r\n");
		testTask.append("server_port =" + pbmTestConfig.getServerList().get(0).getServerPort() + "\r\n");
		}else{
			testTask.append("server_ip =\r\n");
			testTask.append("server_port =0\r\n");
		}
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

	public int getDuration() {
		return pbmTestConfig.getDuration();
	}

	public void setDuration(int duration) {
		pbmTestConfig.setDuration(duration);
	}

	public int getSampleInterval() {
		return sampleInterval;
	}

	public void setSampleInterval(int sampleInterval) {
		this.sampleInterval = sampleInterval;
	}

	public int getNodataTimeout() {
		return pbmTestConfig.getNoDataTimeout();
	}

	public void setNodataTimeout(int nodataTimeout) {
		pbmTestConfig.setNoDataTimeout(nodataTimeout);
	}

	public String getServerIP() {
		Iterator<Server> iter = pbmTestConfig.getServerList().iterator();
		while (iter.hasNext()) {
			Server server = iter.next();
			// 存在的话,直接取第一个返回即可
			return server.getServerIP();

		}
		return "";
	}

	public void setServerIP(String serverIP) {
		boolean isUpdate = false;
		Iterator<Server> iter = pbmTestConfig.getServerList().iterator();
		while (iter.hasNext()) {
			Server server = iter.next();
			isUpdate = true;
			server.setServerIP(serverIP);
			break;
		}
		if (!isUpdate) {
			Server server = new Server();
			server.setServerIP(serverIP);
			pbmTestConfig.getServerList().add(server);
		}
	}

	public int getServerPort() {
		Iterator<Server> iter = pbmTestConfig.getServerList().iterator();
		while (iter.hasNext()) {
			Server server = iter.next();
			// 存在的话,直接取第一个返回即可
			return server.getServerPort();

		}
		return 0;
	}

	public void setServerPort(int serverPort) {
		boolean isUpdate = false;
		Iterator<Server> iter = pbmTestConfig.getServerList().iterator();
		while (iter.hasNext()) {
			Server server = iter.next();
			isUpdate = true;
			server.setServerPort(serverPort);
			break;
		}
		if (!isUpdate) {
			Server server = new Server();
			server.setServerPort(serverPort);
			pbmTestConfig.getServerList().add(server);
		}
	}

	public int getTestMode() {
		return testMode;
	}

	public void setTestMode(int testMode) {
		this.testMode = testMode;
	}

	public int getUpSampleRatio() {
		return pbmTestConfig.getUpSampleRatio();
	}

	public void setUpSampleRatio(int upSampleRatio) {
		pbmTestConfig.setUpSampleRatio(upSampleRatio);
	}

	public int getDownSampleRatio() {
		return pbmTestConfig.getDownSampleRatio();
	}

	public void setDownSampleRatio(int downSampleRatio) {
		pbmTestConfig.setDownSampleRatio(downSampleRatio);
	}

	public PBMTestConfig getPbmTestConfig() {
		return pbmTestConfig;
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
				if (tagName.equals("PBMTestConfig")) {
					pbmTestConfig.parseXml(parser);
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
		if (null != pbmTestConfig)
			pbmTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}
	
	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.PBM.getXmlTaskType();
	}
}
