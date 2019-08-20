package com.walktour.gui.task.parsedata.model.task.base;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.task.parsedata.model.base.TaskBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.LinkedList;
import java.util.List;

public class NetworkConnectionSetting extends TaskBase {
	private static final long serialVersionUID = 7993254291892003129L;
	/**连接协议 wlan**/
	public static final String ConnectionProtocol_WLAN="WLAN";
	/**连接协议 ppp**/
	public static final String ConnectionProtocol_PPP="PPP";
	/**连接协议 nbppp**/
	public static final String ConnectionProtocol_PPPNB="NBPPP";
	/** 从不断开,映射到disConnect=0 **/
	public static final String connectionDisconnectStrategy_0 = "Never";
	/** 每次断开,映射到disConnect=1 **/
	public static final String connectionDisconnectStrategy_1 = "Each Repeat Finish";
	/** 任务结束后断开,映射到disConnect=2 **/
	public static final String connectionDisconnectStrategy_2 = "Single Task Finish";

	/**是否使用该设置**/
	@SerializedName("isAvailable")
	private boolean isAvailable;
	/**拨号连接的协议类型*/
	@SerializedName("connectionProtocol")
	private String connectionProtocol=ConnectionProtocol_PPP;
	/**Detach策略**/
	@SerializedName("detachStrategy")
	private String detachStrategy;
	@SerializedName("singleTimeout")
	private int singleTimeout;
	@SerializedName("reconnectCount")
	private int reconnectCount;
	/**拨号连接的创建方式*/
	@SerializedName("connectionType")
	private String connectionType;
	@SerializedName("totalTimeout")
	private int totalTimeout;
	@SerializedName("connectionDisconnectStrategy")
	private String connectionDisconnectStrategy;
	/**是否使用wifi网格*/
	@SerializedName("connectionUseWifi")
	private boolean connectionUseWifi;
	@SerializedName("wifiList")
	private List<WifiConnectionInfo> wifiList = new LinkedList<WifiConnectionInfo>();

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public String getConnectionProtocol() {
		return connectionProtocol;
	}

	public void setConnectionProtocol(String connectionProtocol) {
		this.connectionProtocol = connectionProtocol;
	}

	public String getDetachStrategy() {
		return detachStrategy;
	}

	public void setDetachStrategy(String detachStrategy) {
		this.detachStrategy = detachStrategy;
	}

	public int getSingleTimeout() {
		return singleTimeout;
	}

	public void setSingleTimeout(int singleTimeout) {
		this.singleTimeout = singleTimeout;
	}

	public int getReconnectCount() {
		return reconnectCount;
	}

	public void setReconnectCount(int reconnectCount) {
		this.reconnectCount = reconnectCount;
	}

	public String getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public int getTotalTimeout() {
		return totalTimeout;
	}

	public void setTotalTimeout(int totalTimeout) {
		this.totalTimeout = totalTimeout;
	}

	public String getConnectionDisconnectStrategy() {
		return connectionDisconnectStrategy==null?"":connectionDisconnectStrategy;
	}

	public void setConnectionDisconnectStrategy(String connectionDisconnectStrategy) {
		this.connectionDisconnectStrategy = connectionDisconnectStrategy;
	}

	public boolean isConnectionUseWifi() {
		return connectionUseWifi;
	}

	public void setConnectionUseWifi(boolean connectionUseWifi) {
		this.connectionUseWifi = connectionUseWifi;
		if(connectionUseWifi){
			this.setConnectionProtocol(ConnectionProtocol_WLAN);
		}else{
//			if(this.getConnectionProtocol()==NetworkConnectionSetting.ConnectionProtocol_PPPNB){
//				this.setConnectionProtocol(ConnectionProtocol_PPPNB);
//			}else {
//				this.setConnectionProtocol(ConnectionProtocol_PPP);
//			}
		}
	}

	public List<WifiConnectionInfo> getWifiList() {
		return wifiList;
	}

	/***
	 * 数据业务复写这两个方法，以适配历史的业务
	 */
	public int getDisConnect() {
		if (getConnectionDisconnectStrategy().equals(NetworkConnectionSetting.connectionDisconnectStrategy_0))
			return 0;
		else if (getConnectionDisconnectStrategy().equals(NetworkConnectionSetting.connectionDisconnectStrategy_1))
			return 1;
		else if (getConnectionDisconnectStrategy().equals(NetworkConnectionSetting.connectionDisconnectStrategy_2))
			return 2;
		return -1;
	}

	public void setDisConnect(int disConnect) {
		switch (disConnect) {
		case 0:
			setConnectionDisconnectStrategy(NetworkConnectionSetting.connectionDisconnectStrategy_0);
			break;
		case 1:
			setConnectionDisconnectStrategy(NetworkConnectionSetting.connectionDisconnectStrategy_1);
			break;
		case 2:
			setConnectionDisconnectStrategy(NetworkConnectionSetting.connectionDisconnectStrategy_2);
			break;
		default:
			break;
		}
	}

	/**
	 * 获取wifi的参数信息
	 * 
	 * @param setting
	 *            wifi设置信息
	 * @return [0]--apname [1]--username [2]--password
	 */
	public String[] getWifiParam() {
		// 使用数组在程序中不用判断对象是否为空
		String[] params = new String[] { "", "", "" };
		for (WifiConnectionInfo wifiI : wifiList) {
			if (wifiI.isCheck()) {
				params[0] = wifiI.getApName() + "";
				params[1] = wifiI.getUserName() + "";
				params[2] = wifiI.getPassword() + "";
			}
		}
		return params;
	}

	/***
	 * 更新wifi参数信息
	 * 
	 * @param setting
	 */
	public void updateWifiParam(String wifiAp, String wifiUserName, String wifiPassword) {
		boolean isUpdate = false;
		for (WifiConnectionInfo wifiI : wifiList) {
			if (wifiI.getApName().equals(wifiAp)) {
				wifiI.setUserName(wifiUserName);
				wifiI.setPassword(wifiPassword);
				wifiI.setCheck(true);
				isUpdate = true;
				continue;
			}
			wifiI.setCheck(false);
		}
		if (!isUpdate) {
			WifiConnectionInfo wifioI = new WifiConnectionInfo();
			wifioI.setCheck(true);
			wifioI.setApName(wifiAp);
			wifioI.setUserName(wifiUserName);
			wifioI.setPassword(wifiPassword);
			wifiList.add(wifioI);
		}
		this.setConnectionProtocol(ConnectionProtocol_WLAN);
		//标识使用的是wifi
		setConnectionUseWifi(true);
	}

	public void parseXml(XmlPullParser parser) throws Exception {
		int eventType = parser.getEventType();
		String tagName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				tagName = parser.getName();
				break;
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if (tagName.equals("ConnectionProtocol")) {
					this.setConnectionProtocol(parser.nextText());
				} else if (tagName.equals("DetachStrategy")) {
					this.setDetachStrategy(parser.nextText());
				} else if (tagName.equals("SingleTimeout")) {
					this.setSingleTimeout(stringToInt(parser.nextText()));
				} else if (tagName.equals("ReconnectCount")) {
					this.setReconnectCount(stringToInt(parser.nextText()));
				} else if (tagName.equals("ConnectionType")) {
					this.setConnectionType(parser.nextText());
				} else if (tagName.equals("TotalTimeout")) {
					this.setTotalTimeout(stringToInt(parser.nextText()));
				} else if (tagName.equals("ConnectionDisconnectStrategy")) {
					this.setConnectionDisconnectStrategy(parser.nextText());
				} else if (tagName.equals("ConnectionUseWifi")) {
					this.setConnectionUseWifi(stringToBool(parser.nextText()));
				} else if (tagName.equals("WifiConnectionInfo")) {
					WifiConnectionInfo wifiConnectionInfo = new WifiConnectionInfo();
					wifiConnectionInfo.parseXml(parser);
					this.wifiList.add(wifiConnectionInfo);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if (tagName.equals("NetworkConnectionSetting")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	public void writeXml(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "NetworkConnectionSetting");
		this.writeAttribute(serializer, "IsAvailable", this.isAvailable);
		this.writeTag(serializer, "ConnectionProtocol", this.connectionProtocol);
		this.writeTag(serializer, "DetachStrategy", this.detachStrategy);
		this.writeTag(serializer, "SingleTimeout", this.singleTimeout);
		this.writeTag(serializer, "ReconnectCount", this.reconnectCount);
		this.writeTag(serializer, "ConnectionType", this.connectionType);
		this.writeTag(serializer, "TotalTimeout", this.totalTimeout);
		this.writeTag(serializer, "ConnectionDisconnectStrategy", this.connectionDisconnectStrategy);
		this.writeTag(serializer, "ConnectionUseWifi", this.connectionUseWifi);
		serializer.startTag(null, "WifiConnectionList");
		for (WifiConnectionInfo wifiConnectionInfo : wifiList) {
			if (null != wifiConnectionInfo) {
				wifiConnectionInfo.writeXml(serializer);
			}
		}
		serializer.endTag(null, "WifiConnectionList");
		serializer.endTag(null, "NetworkConnectionSetting");

	}
}
