package com.dinglicom.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.base.util.LogUtil;

import java.util.List;

/***
 * Wifi 自动连接管理工具
 * 
 * @author weirong.fan
 * 
 */
public class WifiAutoConnectManager {
	private static final String TAG = "WifiAutoConnectManager";
	WifiManager wifiManager;
	WifiInfo mWifiInfo;
	private NetworkInfo wifiInfo;
	private Context context;
	private List<ScanResult> mWifiList = null;
	public String ipaddr;
	/**wifi连接状态**/
	private boolean connectStatus=false;
	/** 登陆开始时间 **/
//	private long startTime = 0;
	/** 登陆结束时间 **/
//	private long endTime = 0;
	// 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
	public enum WifiCipherType {
		WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
	}

	// 构造函数
	public WifiAutoConnectManager(WifiManager wifiManager, Context context) {
		this.wifiManager = wifiManager;
		this.context = context;
	}

	// 打开wifi功能
	public boolean openWifi() {
		LogUtil.w(TAG, "openWifi...");
		boolean bRet = true;
		if (!wifiManager.isWifiEnabled()) {
			bRet = wifiManager.setWifiEnabled(true);
		}
		return bRet;
	}


	// 提供一个外部接口，传入要连接的无线网
	public void connect(String ssid, String password, WifiCipherType type) {
		try {
//			startTime = System.currentTimeMillis();
			LogUtil.w(TAG, "recv WIFI_LOGIN_START");
			writeEventToRcu(true, RcuEventCommand.WLAN_LOGIN_START);
			
//			Thread thread = new Thread(new ConnectRunnable(ssid, password, type));
//			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private WifiConfiguration createWifiInfo(String SSID, String Password, WifiCipherType Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		// nopass
		if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		// wep
		if (Type == WifiCipherType.WIFICIPHER_WEP) {
			if (!TextUtils.isEmpty(Password)) {
				if (isHexWepKey(Password)) {
					config.wepKeys[0] = Password;
				} else {
					config.wepKeys[0] = "\"" + Password + "\"";
				}
			}
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		// wpa
		if (Type == WifiCipherType.WIFICIPHER_WPA) {
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			// 此处需要修改否则不能自动重联
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}

	private class ConnectRunnable implements Runnable {
		private String ssid;
		private String password;
		private WifiCipherType type;

		public ConnectRunnable(String ssid, String password, WifiCipherType type) {
			this.ssid = ssid;
			this.password = password;
			this.type = type;
			LogUtil.w(TAG, "ssid:" + ssid + "password" + password);
		}

		@Override
		public void run() {
			// 打开wifi
			openWifi();
			// 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
			// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
			while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
				try {
					// 为了避免程序一直while循环，让它睡个100毫秒检测……
					Thread.sleep(100);
				} catch (Exception ie) {
					ie.printStackTrace();
				}
			}

			WifiConfiguration wifiConfig = createWifiInfo(ssid, password, type);
			//
			if (wifiConfig == null) {
				LogUtil.w(TAG, "wifiConfig is null!");
				return;
			}

			WifiConfiguration tempConfig = null;
			int loops = 0;
			while (loops <= 15) {// 等待15秒
				List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
				if (null != existingConfigs) {
					for (WifiConfiguration existingConfig : existingConfigs) {
						if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
							tempConfig = existingConfig;
							break;
						}
					}
					break;
				}
				loops += 1;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (tempConfig != null) {
				wifiManager.removeNetwork(tempConfig.networkId);
			}

			int netID = wifiManager.addNetwork(wifiConfig);
			boolean enabled = wifiManager.enableNetwork(netID, true);
			LogUtil.w(TAG, "enableNetwork status enable=" + enabled);
			connectStatus = wifiManager.reconnect();
			LogUtil.w(TAG, "enableNetwork connected=" + connectStatus);
//			boolean isLogin=getWifiConnectStatus(context);
			

		}
	}

	public WifiInfo get_WifiInfo() {
		this.mWifiInfo = this.wifiManager.getConnectionInfo();
		LogUtil.w(TAG, "get_WifiInfo==" + mWifiInfo);
		this.ipaddr = GetIPAddress();
		LogUtil.w(TAG, "ipaddr" + this.ipaddr);
		return this.mWifiInfo;
	}

	@SuppressWarnings("deprecation")
	public boolean getWifiConnectStatus(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm == null) {
				LogUtil.w(TAG, "ConnectivityManager cm = null");
				return false;
			}
			wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiInfo != null && wifiInfo.getState() == NetworkInfo.State.CONNECTED) {
				LogUtil.w(TAG, "WIFI connected");
			} else {
				LogUtil.w(TAG, "No WIFI connected");
				int loops = 0;
				while (wifiInfo.getState() != NetworkInfo.State.CONNECTED && loops < 15) {
					wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					Thread.sleep(1000);
					loops += 1;
					
					if (ApplicationModel.getInstance().isTestInterrupt()) {
						return false;
					}
				}
				LogUtil.w(TAG, "WIFI connected"); 
//				get_WifiInfo();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public String GetIPAddress() {
		if (this.mWifiInfo == null)
			;
		// 本机在WIFI状态下路由分配给的IP地址
		int ipAddress = mWifiInfo.getIpAddress();
		String ipString = "";

		// 获得IP地址的打印格式
		if (ipAddress != 0) {
			ipString = ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "." + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
		}

		// if(MainActivity.DEBUG)LogUtil.w(TAG, "ipAddress"+ipAddress);
		return ipString;
	}

	public static boolean checkNetworkConnection(Context paramContext) {
		return (!(((ConnectivityManager) paramContext.getSystemService("connectivity")).getNetworkInfo(1).isAvailable()));
	}

	public String GetBSSID() {
		if (this.mWifiInfo == null)
			return "NULL";
		return this.mWifiInfo.getBSSID();
	}

	public int GetLinkSpeed() {
		if (this.mWifiInfo == null)
			return 0;
		return this.mWifiInfo.getLinkSpeed();
	}

	public String GetMacAddress() {
		if (this.mWifiInfo == null)
			return "NULL";
		return this.mWifiInfo.getMacAddress();
	}

	public int GetNetworkId() {
		if (this.mWifiInfo == null)
			return 0;
		return this.mWifiInfo.getNetworkId();
	}

	public int GetRSSI() {
		if (this.mWifiInfo == null)
			return 0;
		return this.mWifiInfo.getRssi();
	}

	public String GetSSID() {
		if (this.mWifiInfo == null)
			return "NULL";
		return this.mWifiInfo.getSSID();
	}

	public int getwifilevel(String paramString) {
		for (int i = 0;; ++i) {
			if (i >= this.mWifiList.size())
				return 0;
			if (paramString.equalsIgnoreCase(this.mWifiList.get(i).SSID))
				return this.mWifiList.get(i).level;
		}
	}

	public boolean StartScan(int paramInt) {

		LogUtil.w(TAG, "===StartScan===");
		int i = (int) System.currentTimeMillis();
		if (this.mWifiList != null) {
			this.mWifiList.clear();
			this.mWifiList = null;
		}
		while (true) {
			this.wifiManager.startScan();
			this.mWifiList = this.wifiManager.getScanResults();
			if (paramInt <= (int) System.currentTimeMillis() - i)
				break;
			if (this.mWifiList != null)
				return true;
		}
		return false;
	}

	public List<ScanResult> GetWifiList() {
		return this.mWifiList;
	}

	public void Scanresults() {
		this.mWifiList = this.wifiManager.getScanResults();
	}

	private static boolean isHexWepKey(String wepKey) {
		final int len = wepKey.length();

		// WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
		if (len != 10 && len != 26 && len != 58) {
			return false;
		}
		return isHex(wepKey);
	}

	private static boolean isHex(String key) {
		for (int i = key.length() - 1; i >= 0; i--) {
			final char c = key.charAt(i);
			if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f')) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 将事件写入RCU
	 * 
	 * @param eventID
	 *            事件ID
	 */
	private void writeEventToRcu(boolean isPrint, int eventID) {
		if (!isPrint)
			return;
		EventBytes eventBytes = EventBytes.Builder(context, eventID);
		eventBytes.writeToRcu(System.currentTimeMillis() * 1000);
		eventBytes = null;
	}
 
}
