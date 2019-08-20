package com.walktour.gui.task.parsedata.model.task.iperf;

import com.google.gson.annotations.SerializedName;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.NetworkConnectionSetting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;
import java.util.Map;

public class TaskIperfModel extends TaskModel{  
	/**
	 * 
	 */
	private static final long serialVersionUID = 6799035551726613872L;
	@SerializedName("iPerfTestConfig")
	private IPerfTestConfig iPerfTestConfig = new IPerfTestConfig();
	@SerializedName("networkConnectionSetting")
	private NetworkConnectionSetting networkConnectionSetting = new NetworkConnectionSetting();
	private final static String DIRECTION_SEND = "Send";
	private final static String DIRECTION_RECEIVE = "Receive";
	private final static String DIRECTION_FULL = "Full Duplex";
	private final static String PROTOCOL_TCP = "TCP";
	private final static String PROTOCOL_UDP = "UDP";

	public TaskIperfModel() {
		setTaskType(WalkStruct.TaskType.Iperf.toString());
	}

	/**
	 * 获得当前测试计划写入RCU文件的字符串
	 * 
	 * @author tangwq
	 * @return
	 */
	public String getTestPlanStr() {
		StringBuffer testTask = new StringBuffer();
		testTask.append(getBaseModelStr());

		testTask.append("remoteAddr =" + iPerfTestConfig.getServerIP() + "\r\n");
		testTask.append("remote_port =" + iPerfTestConfig.getServerPort() + "\r\n");
		testTask.append("protocol =" + iPerfTestConfig.getProtocol() + "\r\n");
		testTask.append("direction =" + getDirection() + "\r\n");
		testTask.append("duration =" + iPerfTestConfig.getDuration() + "\r\n");
		testTask.append("udp_band_width =" + iPerfTestConfig.getUpBandwidth() + "\r\n");
		testTask.append("udp_buff_size =" + iPerfTestConfig.getUpBufferSize() + "\r\n");
		testTask.append("udp_packect_size =" + iPerfTestConfig.getUpPacketSize_B() + "\r\n"); 
		testTask.append("telnet_addr =" + iPerfTestConfig.getTelnetSetting().getAddress() + "\r\n");
		testTask.append("telnet_port =" + iPerfTestConfig.getTelnetSetting().getPort() + "\r\n");
		testTask.append("telnet_user_name =" + iPerfTestConfig.getTelnetSetting().getUserName() + "\r\n");
		testTask.append("telnet_password =" + iPerfTestConfig.getTelnetSetting().getPassword() + "\r\n");
		testTask.append("iperf_path =" + iPerfTestConfig.getTelnetSetting().getiPerfPath() + "\r\n");

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

	public NetworkConnectionSetting getNetworkConnectionSetting() {
		return networkConnectionSetting;
	}

	public void setTelnetAddress(String telnet_addr) {
		iPerfTestConfig.getTelnetSetting().setAddress(telnet_addr);
	}

	public void setTelnetPort(int port) {
		iPerfTestConfig.getTelnetSetting().setPort(port);
	}

	public void setUserName(String name) {
		iPerfTestConfig.getTelnetSetting().setUserName(name);
	}

	public void setPassword(String pwd) {
		iPerfTestConfig.getTelnetSetting().setPassword(pwd);
	}

	public void setIperfPath(String path) {
		iPerfTestConfig.getTelnetSetting().setiPerfPath(path);
	}

	public void setRemotePort(int port) {
		iPerfTestConfig.setServerPort(port);
	}

	public void setRemoteAddress(String address) {
		iPerfTestConfig.setServerIP(address);
	}

	public void setProtocol(int protocol) {
		switch (protocol) {
		case 0:
			iPerfTestConfig.setProtocol(PROTOCOL_TCP);
			break;
		case 1:
			iPerfTestConfig.setProtocol(PROTOCOL_UDP);
			break;
		default:
			break;
		}
	}

	public void setDirection(int direction) {
		switch (direction) {
		case 0:
			iPerfTestConfig.setTestMode(DIRECTION_SEND);
			break;
		case 1:
			iPerfTestConfig.setTestMode(DIRECTION_RECEIVE);
			break;
		case 2:
			iPerfTestConfig.setTestMode(DIRECTION_FULL);
			break;
		default:
			break;
		}
	}

	public void setDuration(int duration) {
		iPerfTestConfig.setDuration(duration);
	}

	public void setUdpBandWidth(int width) {
		iPerfTestConfig.setUpBandwidth(width);
	}

	public void setUdpBuffSize(int size) {
		iPerfTestConfig.setUpBufferSize(size);
	}

	public void setUdpPacketSize(int size) {
		iPerfTestConfig.setUpPacketSize_B(size);
	}

	public String getTelnetAddr() {
		return iPerfTestConfig.getTelnetSetting().getAddress();
	}

	public int getTelnetPort() {
		return iPerfTestConfig.getTelnetSetting().getPort();
	}

	public String getUserName() {
		return iPerfTestConfig.getTelnetSetting().getUserName();
	}

	public String getPassword() {
		return iPerfTestConfig.getTelnetSetting().getPassword();
	}

	public String getIperfPath() {
		return iPerfTestConfig.getTelnetSetting().getiPerfPath();
	}

	public int getRemotePort() {
		return iPerfTestConfig.getServerPort();
	}

	public String getRemoteAddr() {
		return iPerfTestConfig.getServerIP();
	}

	public int getDirection() {
		if (iPerfTestConfig.getTestMode().equalsIgnoreCase(DIRECTION_SEND)) {
			return 0;
		} else if (iPerfTestConfig.getTestMode().equalsIgnoreCase(DIRECTION_RECEIVE)) {
			return 1;
		} else {
			return 2;
		}
	}

	public int getDuration() {
		return iPerfTestConfig.getDuration();
	}

	public int getUdpBandWidth() {
		return iPerfTestConfig.getUpBandwidth();
	}

	// tcp or udp
	public int getProtocol() {
		if (iPerfTestConfig.getProtocol().equalsIgnoreCase(PROTOCOL_TCP)) {
			return 0;
		}
		return 1;
	}

	public int getUdpBuffSize() {
		return iPerfTestConfig.getUpBufferSize();
	}

	public int getUdpPacketSize() {
		return iPerfTestConfig.getUpPacketSize_B();
	}
 

	public IPerfTestConfig getiPerfTestConfig() {
		return iPerfTestConfig;
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
				if (tagName.equals("iPerfTestConfig")) {
					iPerfTestConfig.parseXml(parser);
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
		if (null != iPerfTestConfig)
			iPerfTestConfig.writeXml(serializer);
		if (null != networkConnectionSetting)
			networkConnectionSetting.writeXml(serializer);
	}

	@Override
	public String getServerTaskType() {
		return WalkStruct.TaskType.Iperf.getXmlTaskType();
	}
}
