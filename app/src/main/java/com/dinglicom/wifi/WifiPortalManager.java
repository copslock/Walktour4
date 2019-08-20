package com.dinglicom.wifi;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;

import com.dingli.service.test.ipc2jni;
import com.dingli.service.test.ipc2msg;
import com.dingli.wlan.apscan.WifiTools;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.base.util.LogUtil;

import java.util.List;
/***
 * 调用so库登陆wifi portal
 *
 * @author weirong.fan
 *
 */
@SuppressLint("SdCardPath")
public class WifiPortalManager {
	private final String TAG = "WifiPortalManager";
	/** 上下文 **/
	private Context context;
	/** WiFi ap connect test type FTPUPLOAD **/
//	private int AP_TESTTYPE_FTPUPLOAD = 0;
	/** WiFi ap connect test type FTPDOWNLOAD **/
//	private int AP_TESTTYPE_FTPDOWNLOAD = 1;
	/** WiFi ap connect test type PING **/
//	private int AP_TESTTYPE_PING = 2;
	/** WiFi ap connect test type HTTPPAGE **/
//	private int AP_TESTTYPE_HTTPPAGE = 3;
	/** WiFi ap connect test type HTTPDOWNLOAD **/
//	private int AP_TESTTYPE_HTTPDOWNLOAD = 4;
	/** WiFi ap connect test type VIDEOSTREAM **/
//	private int AP_TESTTYPE_VIDEOSTREAM = 5;
	/** WiFi ap connect test type WEB LOGIN **/
//	private int AP_TESTTYPE_WEBLOGIN1 = 6;
	/** WiFi ap connect test type WEB LOGIN(目前暂时忽略) **/
//	private int AP_TESTTYPE_WEBLOGIN2 = 7;
	/** WiFi ap connect test type APTEST **/
	private int AP_TESTTYPE_APTEST = 8;
	private static final int WIFI_AUTH_TEST = 76;
	private static final int WIFI_INITED = 1;
	private static final int WIFI_MAIN_PAGE_START = 2;
	private static final int WIFI_MAIN_PAGE_FIRSTDATA = 3;
	private static final int WIFI_MAIN_PAGE_SUCCESS = 4;
	private static final int WIFI_MAIN_PAGE_FAILED = 5;
	private static final int WIFI_LOGIN_START = 6;
	private static final int WIFI_LOGIN_SUCCESS = 7;
	private static final int WIFI_LOGIN_FAILED = 8;
	// 登陆失败的原因:1:打开超时(open timeout) 2:已经登录(more logon) 3:没有响应(no response)
	// 4.解析失败(dns-resolv failed) 5.DNS文件不存(dnsfile not exist) 10:其他(other)
//	private static final String WIFI_LOGIN_FAILED_REASON_1 = "1";
	private static final String WIFI_LOGIN_FAILED_REASON_2 = "2";
	//	private static final String WIFI_LOGIN_FAILED_REASON_3 = "3";
//	private static final String WIFI_LOGIN_FAILED_REASON_4 = "4";
//	private static final String WIFI_LOGIN_FAILED_REASON_5 = "5";
//	private static final String WIFI_LOGIN_FAILED_REASON_10 = "10";
	private static final int WIFI_LOGOUT_START = 9;
	private static final int WIFI_LOGOUT_SUCCESS = 10;
	private static final int WIFI_LOGOUT_FAILED = 11;
	private static final int WIFI_QUIT = 12;
	private static final int WIFI_START_TEST = 1001;
	private static final int WIFI_START_LOGIN = 1002;
	private static final int WIFI_START_LOGOUT = 1003;
	//	private static final int WIFI_STOP_TEST = 1006;
	private static final String PSK = "PSK";
	private static final String WEP = "WEP";
	private static final String OPEN = "Open";
	private ScanReceiver scanReceiver;
	/** 定时器 **/
//	private final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
	/*** 远程回调 **/
//	private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();
//	private ScheduledFuture taskHandler;
	private ConnectionReceiver connectionReceiver;
	/** 表示WIFI AP信息是否连接成功 **/
	private boolean isWifiConnected = false;
	/** 是否已经完成了连接 **/
	private boolean isFinishConnect = false;
	/** 是否需要web wifi测试等待 **/
	private boolean webwifi_waiting = false;
	/** 登陆开始时间 **/
	private long startTime = 0;
	/** 登陆结束时间 **/
	private long endTime = 0;
	/** 需要连接的wifi SSID **/
	private String wifiSSID = "";
	/** 需要连接的wifi USER **/
	private String wifiUser = "";
	/** 需要连接的wifi PASSWORD **/
	private String wifiPassword = "";
	/*** WIFI SO库 */
	private ipc2jni aIpc2Jni;
	/** Wifi SO库配置文件路径 */
	private String args = "-m web_wifi -z /data/data/com.walktour.gui/files/config/";
	/** Wifi SO库路径 */
	private String client_path = "/data/data/com.walktour.gui/lib/libdatatests_so.so";
	/** SO库事件处理机 **/
	private Handler mEventHandler;
	/** 记录是否登陆成功 **/
	private boolean isLogin = false;
	/** 是否使用库 **/
	private boolean isLib = false;
	/** 使用api登陆 **/
	private WifiAutoConnectManager wifiAutoConnectManager = null;
	public WifiPortalManager(boolean isLib, Context context, String wifiSSID, String wifiUser, String wifiPassword) {
		super();
		this.context = context;
		this.wifiSSID = wifiSSID;
		this.wifiUser = wifiUser;
		this.wifiPassword = wifiPassword;
		isLogin = false;
		this.isLib = isLib;
		if (this.isLib) {
			// new initIni().start();
		} else {
			WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			wifiAutoConnectManager = new WifiAutoConnectManager(wm, context);
		}
	}
	@SuppressLint("HandlerLeak")
	private class initThread extends Thread {
		public void run() {
			LogUtil.w(TAG, "initIni:" + wifiSSID + "," + wifiUser + "," + wifiPassword);
			Looper.prepare();
			mEventHandler = new Handler() {
				public void handleMessage(android.os.Message msg) {
					try {
						ipc2msg aMsg = (ipc2msg) msg.obj;
						if (aMsg.test_item != WIFI_AUTH_TEST) {
							return;
						}
						String message = aMsg.data;
						if (null != message) {
							if (message.contains("reason::") && message.length() > 8) {
								message = message.substring(8);
							}
						}
						switch (aMsg.event_id) {
							case WIFI_INITED:
								LogUtil.w(TAG,"recv WIFI_INITED");
								LogUtil.w(TAG,aMsg.data + "");
								StringBuffer event_data = new StringBuffer();
								event_data.append("local_if::" + "" + "\n");
								event_data.append("user_name::" + wifiUser + "\n");
								event_data.append("password::" + wifiPassword + "\n");
								event_data.append("mainpage_timeout_ms::" + 30000 + "\n");
								event_data.append("login_timeout_ms::" + 30000 + "\n");
								event_data.append("logout_timeout_ms::" + 30000 + "\n");
								event_data.append("net_name::" + wifiSSID + "\n");
								event_data.append("area_code::" + "GUD");
								LogUtil.w(TAG, event_data.toString());
								aIpc2Jni.send_command(WIFI_AUTH_TEST, WIFI_START_TEST, event_data.toString(),
										event_data.length());
								event_data.setLength(0);
								event_data = null;
								break;
							case WIFI_MAIN_PAGE_START:
								LogUtil.w(TAG,"recv WIFI_MAIN_PAGE_START");
								startTime = System.currentTimeMillis();
								writeEventToRcu(true, RcuEventCommand.WLAN_OPEN_PORTAL_PAGE_START);
								break;
							case WIFI_MAIN_PAGE_FIRSTDATA:
								LogUtil.w(TAG, "recv WIFI_MAIN_PAGE_FIRSTDATA");
								endTime = System.currentTimeMillis();
								writeEventToRcu(true, RcuEventCommand.WLAN_OPEN_PORTAL_PAGE_FIRST_DATA,
										(int) (endTime - startTime));
								break;
							case WIFI_MAIN_PAGE_SUCCESS:
								LogUtil.w(TAG, "recv WIFI_MAIN_PAGE_SUCCESS");
								endTime = System.currentTimeMillis();
								writeEventToRcu(true, RcuEventCommand.WLAN_OPEN_PORTAL_PAGE_SUCCESS,
										(int) (endTime - startTime));
								aIpc2Jni.send_command(WIFI_AUTH_TEST, WIFI_START_LOGIN, "", 0);
								break;
							case WIFI_MAIN_PAGE_FAILED:
								if (null != message && message.equals(WIFI_LOGIN_FAILED_REASON_2)) {// 表示已经登陆
									writeEventToRcu(true, RcuEventCommand.WLAN_OPEN_PORTAL_PAGE_SUCCESS);
									endTime = System.currentTimeMillis();
									LogUtil.w(TAG, "recv WIFI_LOGIN_SUCCESS");
									writeEventToRcu(true, RcuEventCommand.WLAN_OPEN_PORTAL_PAGE_SUCCESS,
											(int) (endTime - startTime));
									webwifi_waiting = false;
									isLogin = true;
								} else {
									LogUtil.w(TAG, "recv WIFI_MAIN_PAGE_FAILED");
									LogUtil.w(TAG, aMsg.data + "");
									if (null != message && aMsg.data_len > 0) {
										writeEventToRcu(true, RcuEventCommand.WLAN_OPEN_PORTAL_PAGE_FAILURE,
												Integer.parseInt(message));
									} else {
										writeEventToRcu(true, RcuEventCommand.WLAN_OPEN_PORTAL_PAGE_FAILURE);
									}
									webwifi_waiting = false;
									isLogin = false;
								}
								break;
							case WIFI_LOGIN_START:
								startTime = System.currentTimeMillis();
								LogUtil.w(TAG, "recv WIFI_LOGIN_START");
								writeEventToRcu(true, RcuEventCommand.WLAN_LOGIN_START);
								break;
							case WIFI_LOGIN_SUCCESS:
								endTime = System.currentTimeMillis();
								LogUtil.w(TAG, "recv WIFI_LOGIN_SUCCESS");
								writeEventToRcu(true, RcuEventCommand.WLAN_LOGIN_SUCCESS, (int) (endTime - startTime));
								webwifi_waiting = false;
								isLogin = true;
								break;
							case WIFI_LOGIN_FAILED:
								if (null != message && message.equals(WIFI_LOGIN_FAILED_REASON_2)) {// 表示已经登陆
									endTime = System.currentTimeMillis();
									LogUtil.w(TAG, "recv WIFI_LOGIN_SUCCESS");
									if (aMsg.data_len > 0) {
										writeEventToRcu(true, RcuEventCommand.WLAN_LOGIN_FAILURE,
												Integer.parseInt(message));
									} else {
										writeEventToRcu(true, RcuEventCommand.WLAN_LOGIN_FAILURE);
									}
									webwifi_waiting = false;
									isLogin = true;
								} else {
									LogUtil.w(TAG, "recv WIFI_LOGIN_FAILED");
									LogUtil.w(TAG, aMsg.data + "");
									if (null != message && aMsg.data_len > 0) {
										writeEventToRcu(true, RcuEventCommand.WLAN_LOGIN_FAILURE,
												Integer.parseInt(message));
									} else {
										writeEventToRcu(true, RcuEventCommand.WLAN_LOGIN_FAILURE);
									}
									webwifi_waiting = false;
									isLogin = false;
								}
								break;
							case WIFI_LOGOUT_START:
								LogUtil.w(TAG, "recv WIFI_LOGOUT_START");
								writeEventToRcu(true, RcuEventCommand.WLAN_LOGOUT_START);
								break;
							case WIFI_LOGOUT_SUCCESS:
								LogUtil.w(TAG, "recv WIFI_LOGOUT_SUCCESS");
								LogUtil.w(TAG, aMsg.data + "");
								writeEventToRcu(true, RcuEventCommand.WLAN_LOGOUT_SUCCESS);
								webwifi_waiting = false;
								break;
							case WIFI_LOGOUT_FAILED:
								LogUtil.w(TAG, "recv WIFI_LOGOUT_FAILED");
								LogUtil.w(TAG, aMsg.data + "");
								if (null != message && aMsg.data_len > 0) {
									writeEventToRcu(true, RcuEventCommand.WLAN_LOGOUT_FAILURE, Integer.parseInt(message));
								} else {
									writeEventToRcu(true, RcuEventCommand.WLAN_LOGOUT_FAILURE);
								}
								webwifi_waiting = false;
								break;
							case WIFI_QUIT:
								LogUtil.w(TAG, "recv WIFI_QUIT");
								webwifi_waiting = false;
								break;
						}
						message = null;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			};
			aIpc2Jni = new ipc2jni(mEventHandler);
			boolean flag = false;
			while (!flag) {
				flag = aIpc2Jni.initServer(getLibLogPath());
			}
			flag = false;
			while (!flag) {
				flag = aIpc2Jni.run_client(client_path, args);
			}
			Looper.loop();
		}
	}
	/***
	 * 搜索wifi
	 *
	 * @return
	 */
	private boolean serarchWifi(boolean isPrint, int timeOut) throws Exception {
		LogUtil.w(TAG, "serarchWifi");
		writeEventToRcu(isPrint, RcuEventCommand.WLAN_WIFI_SEARCH_START);
		LogUtil.w(TAG, "Wifi AP startScan");
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {// 没有开启wifi
			boolean wifiStatus = wifiManager.setWifiEnabled(true);
			int timeOutx = timeOut;
			while (!wifiStatus && timeOutx >= 0 && !ApplicationModel.getInstance().isTestInterrupt()) {
				wifiStatus = wifiManager.setWifiEnabled(true);
				Thread.sleep(3000);
				timeOutx -= 1;
			}
//			if (!wifiStatus) {// 在指定时间内wifi没有开启成功,直接返回失败.
//				writeEventToRcu(isPrint, RcuEventCommand.WLAN_WIFI_CONNECT_FAILURE);
//				return false;
//			}
		}
		wifiManager.startScan();
		// 删除当前已经连接的AP
		boolean isRemove = false;
		while (true) {
			WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
			if (null != mWifiInfo) {
				int curNetworkId = mWifiInfo.getNetworkId();
				if (curNetworkId != -1) {
					wifiManager.disableNetwork(curNetworkId);
					wifiManager.removeNetwork(curNetworkId);
					isRemove = true;
				} else {
					break;
				}
			} else {
				break;
			}
		}
		if (!isRemove) {
			LogUtil.w(TAG, "Wifi AP list");
			// 如果系统存储了要连接AP的信息，删除
			List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
			// 默认等待5秒
			String str = null;
			if (null != list && list.size() > 0) {
				for (WifiConfiguration w : list) {
					str = null;
					str = w.SSID.replace("\"", "");
					if (str.equals(wifiSSID)) {
						wifiManager.disableNetwork(w.networkId);
						wifiManager.removeNetwork(w.networkId);
						break;
					}
				}
			}
		}
		writeEventToRcu(isPrint, RcuEventCommand.WLAN_WIFI_SEARCH_DONE);
		return true;
	}
	/**
	 * 连接WifiAP
	 *
	 * @param isPrintWifi
	 *            是否打印搜寻wifi的信息
	 * @param isPrintAP
	 *            是否打印搜寻ap的信息
	 * @param wifiManager
	 * @param timeOut
	 * @param holdTime
	 * @return
	 * @throws Exception
	 */
	public boolean connectAp(boolean isPrintWifi, boolean isPrintAP, WifiManager wifiManager, int timeOut, int holdTime)
			throws Exception {
		if (!this.serarchWifi(isPrintWifi, timeOut)) {
			return false;
		}
		LogUtil.w(TAG, "connectAp");
		// WIFI AP尝试开始
		writeEventToRcu(isPrintAP, RcuEventCommand.WLAN_WIFI_CONNECT_START, AP_TESTTYPE_APTEST);
		LogUtil.w(TAG, "Wifi AP start connect:" + wifiSSID + "," + wifiPassword);
		startTime = System.currentTimeMillis();
		// 固定时间打开AP信息后cancle掉所有处理器
		scanReceiver = new ScanReceiver();
		context.registerReceiver(scanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		wifiManager.startScan();
		while (!isFinishConnect && !ApplicationModel.getInstance().isTestInterrupt()) {// 没有连接上,并且人工中断
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifi.getConnectionInfo();
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (networkInfo.isConnected()) {// 表明wifi ap连接成功
				if (wifiInfo.getSSID().replace("\"", "").equals(wifiSSID)) {
					isFinishConnect = true;
					isWifiConnected = true;
					unregisterReceiverConnectionReceiver();
//					unregisterReceiverScanReceiver();
					LogUtil.w(TAG, "wifi is equals.");
				}
				LogUtil.w(TAG, "wifi is connect.");
			} else {
				LogUtil.w(TAG, "wifi is not connect.");
			}

			Thread.sleep(2000);
			continue;
		}
		if (isWifiConnected) {// 已经连接上,则持续固定的时间
			LogUtil.w(TAG, "Wifi AP connect success");
			LogUtil.w(TAG, "Wifi AP catch DHCP Start");
			writeEventToRcu(isPrintAP, RcuEventCommand.WLAN_DHCP_START);
			WifiInfo wi = wifiManager.getConnectionInfo();
			int ip = wi.getIpAddress();
			int timeOutx = timeOut;
			while (ip == 0 && timeOutx > 0 && !ApplicationModel.getInstance().isTestInterrupt()) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				wi = wifiManager.getConnectionInfo();
				ip = wi.getIpAddress();
				timeOutx--;
				LogUtil.w(TAG, "Wifi AP catch ip:" + ip);
			}
			if (ip != 0) {
				endTime = System.currentTimeMillis();
				// 从开始到成功的连接时延
				int delay = (int) (endTime - startTime);
				LogUtil.w(TAG, "Wifi AP catch DHCP End.");
				if (holdTime > 0) {
					LogUtil.w(TAG, "Wifi AP wait:[" + holdTime + "]s");
					Thread.sleep(holdTime * 1000);
				}
				writeEventToRcu(isPrintAP, RcuEventCommand.WLAN_DHCP_SUCCESS,
						(WifiTools.getIPAddress(ip) + "\0").toCharArray(), 48, delay, holdTime * 1000);

//				EventBytes eventBytes = EventBytes.Builder(context, eventID);
//				eventBytes.writeToRcu(System.currentTimeMillis() * 1000);
//				eventBytes = null;

				writeEventToRcu(isPrintAP, RcuEventCommand.WLAN_WIFI_CONNECT_SUCCESS);
				return true;
			}
			LogUtil.w(TAG, "Wifi AP catch DHCP fail.");
			LogUtil.w(TAG, "Wifi AP connect fail.");
			writeEventToRcu(isPrintAP, RcuEventCommand.WLAN_DHCP_FAILED, 0);
			writeEventToRcu(isPrintAP, RcuEventCommand.WLAN_WIFI_CONNECT_FAILURE);
		} else {
			LogUtil.w(TAG, "Wifi AP connect fail.");
			writeEventToRcu(isPrintAP, RcuEventCommand.WLAN_WIFI_CONNECT_FAILURE);
		}
		return false;
	}
	private void unregisterReceiverConnectionReceiver() {
		if (null != connectionReceiver) {
			context.unregisterReceiver(connectionReceiver);
			connectionReceiver = null;
		}
	}

	private void unregisterReceiverScanReceiver() {
		if (null != scanReceiver) {
			context.unregisterReceiver(scanReceiver);
			scanReceiver = null;
		}
	}
	/***
	 * 断开Wifi AP
	 *
	 * @return
	 */
	public boolean disconnectAP(boolean isPrint, WifiManager wifiManager) throws Exception {
		LogUtil.w(TAG,"disconnectAP");
		writeEventToRcu(isPrint, RcuEventCommand.WLAN_DHCP_RELEASESTART);
		WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
		if (null != mWifiInfo) {
			int curNetworkId = mWifiInfo.getNetworkId();
			if (curNetworkId != -1) {// 存在wlan连接
				wifiManager.disableNetwork(curNetworkId);
				wifiManager.removeNetwork(curNetworkId);
				writeEventToRcu(isPrint, RcuEventCommand.WLAN_DHCP_RELEASESUCCESS);
			}
		} else {// 不存在连接
			// 如果系统存储了要连接AP的信息，删除
			List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
			for (WifiConfiguration w : list) {
				String str = WifiTools.convertToQuotedString(wifiSSID);
				if (w.SSID.equals(str)) {
					wifiManager.disableNetwork(w.networkId);
					wifiManager.removeNetwork(w.networkId);
					break;
				}
			}
		}
		writeEventToRcu(isPrint, RcuEventCommand.WLAN_DISCONNECT);
		return true;
	}
	/**
	 * 获取库的日志路径
	 *
	 * @return
	 */
	private String getLibLogPath() {
		return "/mnt/sdcard/Walktour/liblog/";
	}
	/***
	 * 通过SO库登陆指定的ap
	 *
	 * @param isPrintWifi
	 *            是否打印搜寻wifi的信息
	 *
	 * @param isPrintAP
	 *            是否打印搜寻ap的信息
	 * @return
	 */
	public boolean loginAP(boolean isPrintWifi, boolean isPrintAP, WifiManager wifiManager, int timeOut, int holdTime)
			throws Exception {
		if (this.connectAp(isPrintWifi, isPrintAP, wifiManager, timeOut, holdTime)) {
			int times = 10;
			LogUtil.w(TAG, "loginAP");
			if (this.isLib) {
				new initThread().start();
				webwifi_waiting = true;
				while (webwifi_waiting && times > 0) {
					if (ApplicationModel.getInstance().isTestInterrupt()) {
						logoutAP();
						return false;
					}
					for (int i = 0; i < 6; i++) {
						Thread.sleep(1000);
						if (!webwifi_waiting) {
							break;
						}
						if (ApplicationModel.getInstance().isTestInterrupt()) {
							logoutAP();
							return false;
						}
					}
					times -= 1;
				}
				return isLogin;
			}
			wifiAutoConnectManager.connect(this.wifiSSID, this.wifiPassword,
					WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA);
			webwifi_waiting = true;
			while (webwifi_waiting && times > 0) {
				boolean isConnect = wifiAutoConnectManager.getWifiConnectStatus(context);
				if(isConnect){
					isConnect=isWiFiActive(context);
				}
				if (isConnect) {
					isLogin = true;
					webwifi_waiting = false;
					break;
				}
				if (ApplicationModel.getInstance().isTestInterrupt()) {
					logoutAP();
					writeEventToRcu(true, RcuEventCommand.WLAN_LOGIN_FAILURE);
					return false;
				}
				for (int i = 0; i < 5; i++) {
					Thread.sleep(1000);
					if (!webwifi_waiting) {
						break;
					}
					if (ApplicationModel.getInstance().isTestInterrupt()) {
						logoutAP();
						writeEventToRcu(true, RcuEventCommand.WLAN_LOGIN_FAILURE);
						return false;
					}
				}
				times -= 1;
			}
			if (isLogin) {
				endTime = System.currentTimeMillis();
				LogUtil.w(TAG, "recv WIFI_LOGIN_SUCCESS");
				writeEventToRcu(true, RcuEventCommand.WLAN_LOGIN_SUCCESS, (int) (endTime - startTime));
			} else {
				writeEventToRcu(true, RcuEventCommand.WLAN_LOGIN_FAILURE);
			}
			return isLogin;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	private boolean isWiFiActive(Context inContext) {
		Context context = inContext.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * 通过SO库登出指定的ap
	 *
	 * @return
	 */
	public void logoutAP() throws Exception {
		LogUtil.w(TAG, "logoutAP");
		// 登陆成功之后立即退出
		webwifi_waiting = true;
		if (this.isLib) {
			int i = 0;
			if (!isLogin){
				//没有登陆,无需登录,直接销毁服务即可
				webwifi_waiting=false;
			}
			while (webwifi_waiting) {
				aIpc2Jni.send_command(WIFI_AUTH_TEST, WIFI_START_LOGOUT, "", 0);
				for (i = 0; i < 90; i++) {
					Thread.sleep(1000);
					if (!webwifi_waiting) {
						break;
					}
					if (ApplicationModel.getInstance().isTestInterrupt()) {
						aIpc2Jni.uninit_server();
						aIpc2Jni = null;
						mEventHandler = null;
						return;
					}
				}
				if (i >= 90) {
					webwifi_waiting = false;
				}
			}
			aIpc2Jni.uninit_server();
			mEventHandler = null;
			aIpc2Jni = null;
		} else {// API登陆时不存在登出
			LogUtil.w(TAG, "recv WIFI_LOGOUT_START");
			writeEventToRcu(true, RcuEventCommand.WLAN_LOGOUT_START);
			writeEventToRcu(true, RcuEventCommand.WLAN_LOGOUT_SUCCESS);
		}
	}

	/**
	 * Wifi连接广播接收器
	 *
	 * @author weirong.fan
	 *
	 */
	@SuppressWarnings("deprecation")
	private class ConnectionReceiver extends BroadcastReceiver {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			WifiInfo wifiInfo = wifi.getConnectionInfo();
			if (networkInfo.isConnected()) {// 表明wifi ap连接成功
				if (wifiInfo.getSSID().replace("\"", "").equals(wifiSSID)) {
					isWifiConnected = true;
					isFinishConnect = true;
					context.unregisterReceiver(this);
					connectionReceiver=null;
				}
			}
		}
	}
	/***
	 * 扫描接收机制
	 *
	 * @author weirong.fan
	 *
	 */
	private class ScanReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			List<ScanResult> scanResultList = wifi.getScanResults();
			boolean found = false;
			String security = null;
			for (ScanResult scanResult : scanResultList) {
				if (scanResult.SSID.replace("\"", "").equals(wifiSSID)) {
					security = getScanResultSecurity(scanResult);
					found = true;
				}
			}
			LogUtil.w(TAG,"ScanReceiver is:"+found);
			if (found){
				final WifiConfiguration conf = new WifiConfiguration();
				conf.SSID = "\"" + wifiSSID + "\"";
				if(security != null){
					if (security.equals(WEP)) {
						conf.wepKeys[0] = "\"" + wifiPassword + "\"";
						conf.wepTxKeyIndex = 0;
						conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
						conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
					} else if (security.equals(PSK)) {
						conf.preSharedKey = "\"" + wifiPassword + "\"";
					} else if (security.equals(OPEN)) {
						conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
					}
				}
				if(connectionReceiver==null) {
					connectionReceiver = new ConnectionReceiver();
					IntentFilter intentFilter = new IntentFilter();
					intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
					intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
					intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
					context.registerReceiver(connectionReceiver, intentFilter);
					int netId = wifi.addNetwork(conf);
					wifi.disconnect();
					wifi.enableNetwork(netId, true);
					wifi.reconnect();
				}
			}
			context.unregisterReceiver(this);
		}
	}
	/**
	 * 获取结果的安全机制
	 *
	 * @param scanResult
	 * @return
	 */
	private String getScanResultSecurity(ScanResult scanResult) {
		final String cap = scanResult.capabilities;
		final String[] securityModes = { WEP, PSK };
		for (int i = securityModes.length - 1; i >= 0; i--) {
			if (cap.contains(securityModes[i])) {
				return securityModes[i];
			}
		}
		return OPEN;
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
	/***
	 * 将事件写入RCU
	 *
	 * @param eventID
	 *            事件ID
	 * @param value
	 *            事件整数值
	 *
	 */
	private void writeEventToRcu(boolean isPrint, int eventID, int value) {
		if (!isPrint)
			return;
		EventBytes eventBytes = EventBytes.Builder(context, eventID);
		eventBytes.addInteger(value);
		eventBytes.writeToRcu(System.currentTimeMillis() * 1000);
		eventBytes = null;
	}
	/**
	 * 将事件写入RCU
	 *
	 * @param isPrint
	 *            事件ID
	 * @param eventID
	 *            事件ID
	 * @param arg
	 *            字符数组
	 * @param maxLength
	 *            字符数组最大长度
	 * @param values
	 *            整数数组值
	 */
	private void writeEventToRcu(boolean isPrint, int eventID, char[] arg, int maxLength, int... values) {
		if (!isPrint)
			return;
		EventBytes eventBytes = EventBytes.Builder(context, eventID);
		eventBytes.addCharArray(arg, maxLength);
		for (int value : values) {
			eventBytes.addInteger(value);
		}
		eventBytes.writeToRcu(System.currentTimeMillis() * 1000);
		eventBytes = null;
	}
}
