package com.walktour.gui.task.parsedata.model.task.traceroute;

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
 * TraceRoute 模板参数对象
 * 
 * @author lianzh
 *
 */
public class TaskTraceRouteModel extends TaskModel {
	private static final long serialVersionUID = 1546402071181094880L;
	@SerializedName("tracertTestConfig")
	private TracertTestConfig tracertTestConfig = new TracertTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();
	public TaskTraceRouteModel() {
		setTypeProperty(WalkCommonPara.TypeProperty_Net);
		setInterVal(15);
		setDisConnect(1);
		setTaskType(WalkStruct.TaskType.TraceRoute.name());
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

	public String getHost() {
		return tracertTestConfig.getAddress();
	}

	public void setHost(String host) {
		tracertTestConfig.setAddress(host);
	}

	public int getIpPacket() {
		return tracertTestConfig.getPacketSize_B();
	}

	public void setIpPacket(int ipPacket) {
		tracertTestConfig.setPacketSize_B(ipPacket);
	}

	public long getHopTimeout() {
		return tracertTestConfig.getHopTimeout();
	}

	public void setHopTimeout(long hopTimeout) {
		tracertTestConfig.setHopTimeout((int)hopTimeout);
	}

	public long getHopInterval() {
		return tracertTestConfig.getHopInterval();
	}

	public void setHopInterval(long hopInterval) {
		tracertTestConfig.setHopInterval((int)hopInterval);
	}

	public int getHopProbeNum() {
		return tracertTestConfig.getHopProbeNumber();
	}

	public void setHopProbeNum(int hopProbeNum) {
		tracertTestConfig.setHopProbeNumber(hopProbeNum);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public TracertTestConfig getTracertTestConfig() {
		return tracertTestConfig;
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
				if (tagName.equals("TracertTestConfig")) { 
					tracertTestConfig.parseXml(parser);
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
		if (null != tracertTestConfig)
			tracertTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}
	
	@Override
	public String getServerTaskType() { 
		return WalkStruct.TaskType.TraceRoute.getXmlTaskType();
	}
}
