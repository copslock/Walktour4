package com.dingli.wlan.apscan;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WifiTools {
	
	public static final int FILTER_BY_SSID = 0;
	public static final int FILTER_BY_CHANNEL = 1;
	public static final int FILTER_BY_MAC = 2;
	public static final int FILTER_BY_FREQUENCY = 3;
	public static final int FILTER_BY_WALLS = 5;
	
	private static final String TAG = "WifiTools";
	// Constants used for different security types
	public static final String OPEN = "OPEN";
	public static final String WEP = "WEP";
	public static final String WPA = "WPA";
	public static final String WPA2 = "WPA2";
	// static final int SECURITY_EAP = 3;
	// For EAP Enterprise fields
	public static final String WPA_EAP = "WPA-EAP";
	public static final String IEEE8021X = "IEEE8021X";
	static final String[] SECURITY_MODES = { WEP, WPA, WPA_EAP, IEEE8021X,WPA2};

	public static final int WEP_PASSWORD_AUTO = 0;
	public static final int WEP_PASSWORD_ASCII = 1;
	public static final int WEP_PASSWORD_HEX = 2;
	public static int WALL_WEAKER = 0;//墙体覆盖的影响
	
	public static WifiManager wifiManager; //使用前必须先赋值
	//public static List<APInfoModel> WIFI_AP_LIST ; //这个变量从WifiScanner中获得值，然后进行更新
	//public static WifiScanner wifiScanner; //使用前在Main.java中对其进行赋值
	//public static List<APInfoModel> apDetailedInfoList;
	//public static ConcurrentHashMap<String,APInfoModel> WIFI_AP_MAP = new ConcurrentHashMap<String,APInfoModel>();
	
	//当前的服务AP
	//public static APInfoModel currentAP=new APInfoModel();
	//该APlist为UI使用
	//public static ArrayList<APInfoModel> WIFI_AP_LIST_CLONE = new ArrayList<APInfoModel>();//WIFI_AP_LIST副本
	
	//public static ArrayList<STAInfoModel> staInfolist;// = new ArrayList<STAInfoModel>() ;
	//public static HashMap<Integer,ChannelQualityParse> m_channelQualiyMap;
	//public static HashMap<String,Integer> m_apChannel = new HashMap<String,Integer>();  //AP对应的Channel,统计需要
	//public static HashMap<String,String> m_apBeacons = new HashMap<String,String>(); // ap ssid 和 beacons的对应关系
	//public static HashMap<String,STAInfoModel> m_staMap = new HashMap<String,STAInfoModel>();
	//public static HashMap<String,ArrayList<String>> m_apSta = new HashMap<String,ArrayList<String>>();
	public static HashMap<String, Long> m_firstDetectedTimes = new HashMap<String, Long>();//BSSID,对应时间
	public static HashMap<String, Long> m_lastDetectedTimes = new HashMap<String, Long>();//BSSID,对应时间
	
	//public static boolean mStreaming=false;

	// 用于TaskModelXmlChange中FileDB.getInstance(WifiTools.mContext)，mContext将会在Main.java中赋值，因为Main只会被隐藏，不会被finish掉
	public static Context mContext;


	
//	public static ArrayList<String> getAPNames(){
////		List<String> apList = new ArrayList<String>();
////		if(WIFI_AP_LIST == null){
////			return apList;
////		}else{
////			for (APInfoModel sr : WIFI_AP_LIST) {
////				String ssid = sr.SSID;
////				String bssid = sr.BSSID;
////				apList.add(ssid+"("+bssid+")");
////			}
////			return apList;
////		}
//		ArrayList<String> apList = new ArrayList<String>();
//		if(WIFI_AP_LIST == null){
//			return apList;
//		}else{
//			for (APInfoModel sr : WIFI_AP_LIST) {
//				String ssid = sr.ssid;
//				String bssid = sr.bssid;
//				apList.add(ssid+"("+bssid+")");
//			}
//			return apList;
//		}
//	} 
//	public static void registerScannerObsrver(ScannerObserver so) {
//		if (wifiScanner!=null)
//			wifiScanner.registerObserver(so);
//	}
//	public static void removeScannerObsrver(ScannerObserver so) {
//		if (wifiScanner!=null)
//			wifiScanner.removeObserver(so);
//	}
//	
//	public static int getWifiState() {
//		if (wifiScanner!=null)
//			return wifiScanner.getWifiState();
//		return -1;
//	}
	/**
	 * Configure a network, and connect to it.
	 * 
	 * @param wifiMgr
	 * @param APInfoModel
	 * @param password
	 *            Password for secure network or is ignored.
	 * @return
	 */
	public static boolean connectToNewNetwork(APInfoModel model,
			String password) {
		final String security = getScanResultSecurity(model.encryptionType);

		WifiConfiguration config = new WifiConfiguration();
		config.BSSID = model.bssid;
		config.SSID = convertToQuotedString(model.ssid);

		setupSecurity(config, security, password);

		config.networkId = wifiManager.addNetwork(config);

		wifiManager.updateNetwork(config);

		if (!wifiManager.enableNetwork(config.networkId, true)) {
			return false;
		}

		return true;

	}

	/**
	 * Connect to a configured network.
	 * 
	 * @param wifiManager
	 * @param config
	 * @param numOpenNetworksKept
	 *            Settings.Secure.WIFI_NUM_OPEN_NETWORKS_KEPT
	 * @return
	 */
//	public static boolean connectToConfiguredNetwork(WifiConfiguration config) {
//		if (!wifiManager.enableNetwork(config.networkId, true)) {
//			return false;
//		}
//		wifiManager.saveConfiguration();
//		return true;
//	}

	/**
	 * 获取
	 */
	public static String getScanResultSecurity(String  cap) {
			for (int i = SECURITY_MODES.length - 1; i >= 0; i--) {
			if (cap.contains(SECURITY_MODES[i])) {
				return SECURITY_MODES[i];
			}
		}
		return OPEN;
	}
	public static String getScanResultSecurity(ScanResult apinfo) {

		final String cap = apinfo.capabilities;
		for (int i = SECURITY_MODES.length - 1; i >= 0; i--) {
			if (cap.contains(SECURITY_MODES[i])) {
				return SECURITY_MODES[i];
			}
		}
		return OPEN;
	}
	public static String convertToQuotedString(String string) {
		if (TextUtils.isEmpty(string)) {
			return "";
		}

		final int lastPos = string.length() - 1;
		if (lastPos < 0
				|| (string.charAt(0) == '"' && string.charAt(lastPos) == '"')) {
			return string;
		}

		return "\"" + string + "\"";
	}

	public static String getIPAddress(int address) {
		StringBuilder sb = new StringBuilder();
		sb.append(address & 0x000000FF).append(".").append(
				(address & 0x0000FF00) >> 8).append(".").append(
				(address & 0x00FF0000) >> 16).append(".").append(
				(address & 0xFF000000L) >> 24);
		return sb.toString();
	}

	/**
	 * Fill in the security fields of WifiConfiguration config.
	 * 
	 * @param config
	 *            The object to fill.
	 * @param security
	 *            If is OPEN, password is ignored.
	 * @param password
	 *            Password of the network if security is not OPEN.
	 */
	private static void setupSecurity(WifiConfiguration config,
			String security, String password) {
		if (TextUtils.isEmpty(security)) {
			security = OPEN;
			Log.w(TAG, "Empty security, assuming open");
		}

		if (security.equals(WEP)) {
			int wepPasswordType = WEP_PASSWORD_AUTO;
			// If password is empty, it should be left untouched
			if (!TextUtils.isEmpty(password)) {
				if (wepPasswordType == WEP_PASSWORD_AUTO) {
					if (isHexWepKey(password)) {
						config.wepKeys[0] = password;
					} else {
						config.wepKeys[0] = convertToQuotedString(password);
					}
				} else {
					config.wepKeys[0] = wepPasswordType == WEP_PASSWORD_ASCII ? convertToQuotedString(password)
							: password;
				}
			}

			config.wepTxKeyIndex = 0;

			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);

			config.allowedKeyManagement.set(KeyMgmt.NONE);

			config.allowedGroupCiphers.set(GroupCipher.WEP40);
			config.allowedGroupCiphers.set(GroupCipher.WEP104);

		} else if (security.equals(WPA)) {

			config.allowedGroupCiphers.set(GroupCipher.TKIP);
			config.allowedGroupCiphers.set(GroupCipher.CCMP);

			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
//			config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
//			config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
			
			config.allowedPairwiseCiphers.set(PairwiseCipher.CCMP);
			config.allowedPairwiseCiphers.set(PairwiseCipher.TKIP);

			// config.allowedProtocols.set(security.equals(WPA2) ? Protocol.RSN
			// : Protocol.WPA);
			config.allowedProtocols.set(Protocol.WPA);//for wpa1
//			config.allowedProtocols.set(Protocol.RSN);//for wpa2
			
			// If password is empty, it should be left untouched
			if (!TextUtils.isEmpty(password)) {
				if (password.length() == 64 && isHex(password)) {
					// Goes unquoted as hex
					config.preSharedKey = password;
				} else {
					// Goes quoted as ASCII
					config.preSharedKey = convertToQuotedString(password);
				}
			}

		} else if (security.equals(OPEN)) {
			config.allowedKeyManagement.set(KeyMgmt.NONE);
		} else if (security.equals(WPA_EAP) || security.equals(IEEE8021X)) {
			config.allowedGroupCiphers.set(GroupCipher.TKIP);
			config.allowedGroupCiphers.set(GroupCipher.CCMP);
			if (security.equals(WPA_EAP)) {
				config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
			} else {
				config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
			}
			if (!TextUtils.isEmpty(password)) {
				config.preSharedKey = convertToQuotedString(password);
			}
		}
	}

	public static WifiConfiguration getWifiConfiguration(APInfoModel hotsopt) {
		final String ssid = convertToQuotedString(hotsopt.ssid);
		if (ssid.length() == 0) {
			return null;
		}

		final String bssid = hotsopt.bssid;
		if (bssid == null) {
			return null;
		}

		String hotspotSecurity = getScanResultSecurity(hotsopt.encryptionType);

		final List<WifiConfiguration> configurations = wifiManager
				.getConfiguredNetworks();

		for (final WifiConfiguration config : configurations) {
			if (config.SSID == null || !ssid.equals(config.SSID)) {
				continue;
			}
			if (config.BSSID == null || bssid.equals(config.BSSID)) {
				final String configSecurity = getWifiConfigurationSecurity(config);
				if (hotspotSecurity.equals(configSecurity)) {
					return config;
				}
			}
		}

		for (WifiConfiguration config : configurations) {
			Log.d("my", config.toString());
		}
		return null;
	}

	static public String getWifiConfigurationSecurity(
			WifiConfiguration wifiConfig) {

		if (wifiConfig.allowedKeyManagement.get(KeyMgmt.NONE)) {
			// If we never set group ciphers, wpa_supplicant puts all of them.
			// For open, we don't set group ciphers.
			// For WEP, we specifically only set WEP40 and WEP104, so CCMP
			// and TKIP should not be there.
			if (!wifiConfig.allowedGroupCiphers.get(GroupCipher.CCMP)
					&& (wifiConfig.allowedGroupCiphers.get(GroupCipher.WEP40) || wifiConfig.allowedGroupCiphers
							.get(GroupCipher.WEP104))) {
				return WEP;
			}
			return OPEN;
		} else if (wifiConfig.allowedProtocols.get(Protocol.RSN)
				|| wifiConfig.allowedProtocols.get(Protocol.WPA)) {
			return WPA;
		} else if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA_EAP)) {
			return WPA_EAP;
		} else if (wifiConfig.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
			return IEEE8021X;
		} else {
			Log.w(TAG, "Unknown security type from WifiConfiguration, falling back on open.");
			return OPEN;
		}
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
			if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
					&& c <= 'f')) {
				return false;
			}
		}

		return true;
	}

	//only for 2.4G
	public static int getFrequency(int channel) {
		int frequency = 0;
		if (channel <= 14) {
			frequency = 2412+(channel-1)*5;
		}
		return frequency;
	}
	/**
	 * 根据频率获得信道
	 * 
	 * @param frequency
	 * @return
	 */
	public static int getChannel(int frequency) {
		int channel = 0;
		switch (frequency) {
		case 2412:
			channel = 1;
			break;
		case 2417:
			channel = 2;
			break;
		case 2422:
			channel = 3;
			break;
		case 2427:
			channel = 4;
			break;
		case 2432:
			channel = 5;
			break;
		case 2437:
			channel = 6;
			break;
		case 2442:
			channel = 7;
			break;
		case 2447:
			channel = 8;
			break;
		case 2452:
			channel = 9;
			break;
		case 2457:
			channel = 10;
			break;
		case 2462:
			channel = 11;
			break;
		case 2467:
			channel = 12;
			break;
		case 2472:
			channel = 13;
			break;
		case 2484:
			channel = 14;
			break;
		//5G频段
		case 4915:
			channel = 183;
			break;
		case 4920:
			channel = 184;
			break;
		case 4925:
			channel = 185;
			break;
		case 4935:
			channel = 187;
			break;
		case 4940:
			channel = 188;
			break;
		case 4945:
			channel = 189;
			break;
		case 4960:
			channel = 192;
			break;
		case 4980:
			channel = 196;
			break;
		case 5035:
			channel = 7;
			break;
		default:
			if (frequency > 5035) { //other 5G
		       channel = 7+(frequency-5035)/5;
		    }
			break;
				
		}
		return channel;
	}
	/*
	 * 
	 * 是否是运营商网络
	 */
	public  static boolean isOperator(String apname) {
		return apname.equals("CMCC")||
		apname.equals("ChinaNet")||
		apname.equals("ChinaUnicom");
	}
	/**
	 *	连接到指定的AP，如果已经连接了，则直接返回
	 *  如果连接了别的AP或者没有连接任何AP，则会重新连接
	 *  lfj 2012/3/5
	 * 
	 * @param apnName
	 * @return
	 */
	public static boolean connectAP(String apname,String account,String password) {
		// 程序中经常产生一个apnname为""的AP，而且这种空的AP会连接成功，而name为空的AP显然没有意义。add by ZhengLei
		if("".equals(apname)) {
			return false;
		}
		
		if (wifiManager == null) {
			Log.d(TAG,"wifiManager is null");
			return false;
		}
		if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED)
			return false;
		
		wifiManager.startScan();
		WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
		//是否成功连接上AP的标志
		boolean connectResult = false;
		
		//已经连接上了指定的AP
		if(mWifiInfo.getSSID() != null && mWifiInfo.getSSID().equals(apname)) {
			Log.d(TAG,"AP has connected");
			return true;
		}

		//Log.d(TAG,"wifiManager cannot get connection info");
		//如果没有连接上，或者已经连接上的不是所选的，那么重新连接
		List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
		boolean networkInSupplicant = false;
		//重新连接指定AP
		for(WifiConfiguration w:list){
			//将指定AP 名字转化
			String str = WifiTools.convertToQuotedString(apname);
			if(w.SSID.equals(str)){
				connectResult = wifiManager.enableNetwork(w.networkId, true);
				wifiManager.saveConfiguration();
				networkInSupplicant = true;
				break;
			}
		}
		if(!networkInSupplicant) {
			Log.d(TAG,"connect specificAP....");
			connectResult = connectSpecificAP(apname,account,password);
			wifiManager.saveConfiguration();
		}
		mWifiInfo = wifiManager.getConnectionInfo();
//		String ssid = mWifiInfo.getSSID();
		int ip = mWifiInfo.getIpAddress();
		int count = 20;
		//切换过程中需要等待20S
		while (ip == 0 && count > 0) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			count--; 
			mWifiInfo = wifiManager.getConnectionInfo();
			ip = mWifiInfo.getIpAddress();
		}
		if(ip == 0) {
			connectResult = false;
		}
		return connectResult;
	
	}
	
	public static boolean resetWifi() {
		wifiManager.setWifiEnabled(false);
		boolean setEnabled=false;
		while(true) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
			if ( wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
    		
				Log.d("Scan", " Wifi Status is Disable");
				if (!setEnabled) {
					wifiManager.setWifiEnabled(true);
					setEnabled=true;
				}
			}
			if (setEnabled) {
				//是命令触发的打开动作
				if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
					break;
			}
		}
		return true;
	}
	 /**
     * 连接设置里指定的wifi,配置列表里面没有
     * ***/
    public static boolean connectSpecificAP(String apname,String account,String password) {
    	// 程序中经常产生一个apname为""的AP，而且这种空的AP会连接成功，而name为空的AP显然没有意义。add by ZhengLei
		if("".equals(apname)) {
			return false;
		}
//		WifiManager wi;
    	wifiManager.startScan();
    	List<ScanResult> results = wifiManager.getScanResults();
    	String authMode = OPEN;
    	// AP是否在扫描结果中
    	boolean isApInScanResult = false;
    	for(int i = 0;i < results.size();i++) {
    		if(results.get(i).SSID.equals(apname)) {
    			isApInScanResult = true;
    			authMode = getScanResultSecurity(results.get(i));
    			break;
    		}
    	}
    	// 如果AP不在扫描结果中，直接返回连接失败。add by ZhengLei,2012/08/16
    	if(!isApInScanResult) {
    		return false;
    	}
    	WifiConfiguration wc = new WifiConfiguration();
    	wc.SSID = WifiTools.convertToQuotedString(apname);
    	boolean isOpen = false;
    	if(password.equals("")) {
    		isOpen = true;
    		wc.preSharedKey = null;
    	}else {
    		//如果WLAN账户不为空，说明apnPassword是WLAN的登录密码，不是WIFI密码
    		if(account.equals("")) {
    			wc.status = WifiConfiguration.Status.ENABLED;
    			if(authMode.equals(WEP)) {
    				wc.hiddenSSID = false;
    				wc.wepKeys[0] = WifiTools.convertToQuotedString(password);
    				wc.wepTxKeyIndex = 0;
    			}else {
            		wc.preSharedKey = WifiTools.convertToQuotedString(password);
    			}
    		}else {
    			isOpen = true;
    		}

    	}
    	if(isOpen) {
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
    	}else {
    		if(authMode.equals(WEP)) {
                wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
    		} else {
                wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
    		}

    	}
    	int netId = wifiManager.addNetwork(wc);
    	return wifiManager.enableNetwork(netId, true);
    }
	public static int mfilterConditions;
	public static ArrayList<String> mfilterParams;
	
	/*
	 * 
	 * true是添加上去的意思
	 */
	public static boolean  needFiltered(APInfoModel model ) {
		if (mfilterParams ==null) {
			return true;
		}
		if ( mfilterConditions == FILTER_BY_SSID) {
				return mfilterParams.contains(model.ssid);
		}
		else if ( mfilterConditions == FILTER_BY_CHANNEL) {
			for (int i=0;i<mfilterParams.size();i++) {
				
				Log.e("wifitools",mfilterParams.get(i));
			}
			String channel = String.valueOf(getChannel(model.frequency));
			return mfilterParams.contains(channel);
		}
		else if ( mfilterConditions == FILTER_BY_MAC) {
			return mfilterParams.contains(model.bssid);
		}
		else if ( mfilterConditions == FILTER_BY_FREQUENCY) {
			return mfilterParams.contains(""+model.frequency);
		}
		else if ( mfilterConditions == FILTER_BY_WALLS ) {
			int minusNumber = Integer.parseInt(mfilterParams.get(0));
			model.rssi -= minusNumber;//衰减值为正值
			if (model.rssi<-100) {
				model.rssi=-110;
			}
			return true;
		}
		return false;
		
	}
//    private static void filterResultBySSID(ArrayList<String> params) {
//    	ArrayList<APInfoModel> result = new ArrayList<APInfoModel>();
//    	if(WIFI_AP_LIST_CLONE != null && params != null) {
//    		if( (!WIFI_AP_LIST_CLONE.isEmpty()) && (!params.isEmpty())) {
//    			for(int i = 0;i < WIFI_AP_LIST_CLONE.size();i++) {
//    				//候选的APInfoModel的SSID包含在过滤参数中，加入result
//    				if(params.contains(WIFI_AP_LIST_CLONE.get(i).ssid)) {
//    					result.add(WIFI_AP_LIST_CLONE.get(i));
//    				}
//    			}
//    	    	//将过滤后获得的结果赋值给原列表
//    			WIFI_AP_LIST_CLONE = result;
//    		}
//    	}
//
//    }
//    private static void filterResultByChannel(ArrayList<String> params) {
//    	ArrayList<APInfoModel> result = new ArrayList<APInfoModel>();
//    	if(WIFI_AP_LIST_CLONE != null && params != null) {
//    		if( (!WIFI_AP_LIST_CLONE.isEmpty()) && (!params.isEmpty())) {
//    			for(int i = 0;i < WIFI_AP_LIST_CLONE.size();i++) {
//    				//候选的APInfoModel的channel包含在过滤参数中，加入result
//    				if(params.contains(String.valueOf(getChannel(WIFI_AP_LIST_CLONE.get(i).frequency)))) {
//    					result.add(WIFI_AP_LIST_CLONE.get(i));
//    				}
//    			}
//    	    	//将过滤后获得的结果赋值给原列表
//    			WIFI_AP_LIST_CLONE = result;
//    		}
//    	}
//
//    }
//    private static void filterResultByMacAddress(ArrayList<String> params) {
//    	ArrayList<APInfoModel> result = new ArrayList<APInfoModel>();
//    	if(WIFI_AP_LIST_CLONE != null && params != null) {
//    		if( (!WIFI_AP_LIST_CLONE.isEmpty()) && (!params.isEmpty())) {
//    			for(int i = 0;i < WIFI_AP_LIST_CLONE.size();i++) {
//    				//候选的mac address的channel包含在过滤参数中，加入result
//    				if(params.contains(WIFI_AP_LIST_CLONE.get(i).bssid)) {
//    					result.add(WIFI_AP_LIST_CLONE.get(i));
//    				}
//    			}
//    	    	//将过滤后获得的结果赋值给原列表
//    			WIFI_AP_LIST_CLONE = result;
//    		}
//    	}
//
//    }
//    private static void filterResultByFrequency(ArrayList<String> params) {
//    	ArrayList<APInfoModel> result = new ArrayList<APInfoModel>();
//    	if(WIFI_AP_LIST_CLONE != null && params != null) {
//    		if( (!WIFI_AP_LIST_CLONE.isEmpty()) && (!params.isEmpty())) {
//    			for(int i = 0;i < WIFI_AP_LIST_CLONE.size();i++) {
//    				//候选的frequency的channel包含在过滤参数中，加入result
//    				if(params.contains(String.valueOf(WIFI_AP_LIST_CLONE.get(i).frequency))) {
//    					result.add(WIFI_AP_LIST_CLONE.get(i));
//    				}
//    			}
//    	    	//将过滤后获得的结果赋值给原列表
//    			WIFI_AP_LIST_CLONE = result;
//    		}
//    	}
//    }
//    //加衰减参数
//    private static void filterResultByWalls(ArrayList<String> params) {
//    	if(WIFI_AP_LIST_CLONE != null && params != null) {
//    		if( (!WIFI_AP_LIST_CLONE.isEmpty()) && (!params.isEmpty())) {
//				int minusNumber = Integer.parseInt(params.get(0));
//    			for(int i = 0;i < WIFI_AP_LIST_CLONE.size();i++) {
//    				WIFI_AP_LIST_CLONE.get(i).rssi -= minusNumber;//衰减值为正值
//    				//Log.e("WifiTools", "minusNumber=" + minusNumber);
//    				if(WIFI_AP_LIST_CLONE.get(i).rssi < -110) {
//    					WIFI_AP_LIST_CLONE.get(i).rssi = -110;
//    				}
//    			}
//    		}
//    	}
//    }
//    //根据参数过滤结果
//	public static void filterResultByParam(int filter,ArrayList<String> params) {
//		
//		switch (filter) {
//		case FILTER_BY_SSID:
//			filterResultBySSID(params);
//			break;
//		case FILTER_BY_CHANNEL:
//			filterResultByChannel(params);
//			break;
//		case FILTER_BY_MAC:
//			filterResultByMacAddress(params);
//			break;
//		case FILTER_BY_FREQUENCY:
//			filterResultByFrequency(params);
//			break;
//		case FILTER_BY_WALLS:
//			filterResultByWalls(params);
//			break;
//		}
//	}
//	public static ArrayList<String> getAPNamesClone(){
//		ArrayList<String> apList = new ArrayList<String>();
//		if(WIFI_AP_LIST_CLONE == null){
//			return apList;
//		}else{
//			for (APInfoModel sr : WIFI_AP_LIST_CLONE) {
//				String ssid = sr.ssid;
//				String bssid = sr.bssid;
//				apList.add(ssid+"("+bssid+")");
//			}
//			return apList;
//		}
//	} 
	//获取SSID列表
	public static ArrayList<String> getAllSSID(List<APInfoModel> params) {
		ArrayList<String> result = new ArrayList<String>();
		if(params != null) {
			for(int i = 0;i < params.size();i++) {
				String ssid = params.get(i).ssid;
				if (!result.contains(ssid)) {
					result.add(ssid);
				}
			}
		}
		return result;
	}
	//获取所有Mac地址
	public static ArrayList<String> getAllMacAddress(List<APInfoModel> params) {
		ArrayList<String> result = new ArrayList<String>();
		if(params != null) {
			for(int i = 0;i < params.size();i++) {
				String macadress = params.get(i).bssid;
				if (!result.contains(macadress)) {
					result.add(macadress);
				}
			}
		}
		return result;
	}
	
	//如果是9002则只抓包，不做业务
	public static boolean is9002() {
		return android.os.Build.MODEL.equalsIgnoreCase("dkb");
	}
	//写入RCU的DHCP成功事件时使用
	public static String getCurrentIPString() {
		String result = "";
		WifiInfo wi = wifiManager.getConnectionInfo();
		int ip = wi.getIpAddress();
		if(ip != 0) {
			int ip1 = ip&0xFF;
			ip = ip>>8;
            int ip2 = ip&0xFF;
            ip = ip>>8;
            int ip3 = ip&0xFF;
            ip = ip>>8;
            int ip4 = ip&0xFF;
            result = ip1 + "." + ip2 + "." + ip3 + "." + ip4;
		} else {
			result = "0.0.0.0";
		}
		while (result.length() < 16) {
			result = result + '\0';
		}
		return result;
	}
	
	public static void insertFirstAndLastTime(String bssid, long time) {
		if(m_firstDetectedTimes == null || m_lastDetectedTimes == null) {
			return;
		}
		Long firstTime = m_firstDetectedTimes.get(bssid);
		if(firstTime == null) {
			m_firstDetectedTimes.put(bssid, time);
			Log.d("WifiTools", "m_firstDetectedTimes:" + bssid + time);
		}
		m_lastDetectedTimes.put(bssid, time);
	}
	
	/**
	 * 获取当前连接的AP的bssid
	 * @return bssid值
	 */
	public static String getCurrentBssid() {
		if(mContext != null) {
			WifiManager wifi = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifi.getConnectionInfo();
			if(wifiInfo != null) {
				return wifiInfo.getBSSID();
			}
		}
		return null;
	}
	
	/*
	 * 判断当前WIFI是否已经连接
	 * 
	 */
	public static boolean  isWifiConnected() {
		
		 boolean status=false;
	    try{
		    if (mContext==null)
		    		return false;
		        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		        NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		        if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
		            status= true;
		        }
		    }catch(Exception e){
		        e.printStackTrace();  
		        return false;
		    }
		    return status;

	    }
	
	public static boolean isWifiOpen() {
		return wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
	}
	
	/*
	 * 
	 * 
	 */
	public static byte[] macAddrToBytes(String macAddr) {
		try {
			String[] macAddressParts = macAddr.split(":");
			byte[] macAddressBytes = new byte[6];
			for(int i=0; i<6; i++){
			    Integer hex = Integer.parseInt(macAddressParts[i], 16);
			    macAddressBytes[i] = hex.byteValue();
			}
			return macAddressBytes;
		}catch(Exception e) {
			return new byte[6];
		}
	}
	
	public static String macAddrToString (byte[] mac) {
		
		  String value = "";
		  for(int i = 0;i < mac.length; i++){
		   String sTemp = Integer.toHexString(0xFF &  mac[i]);
		   value = value+sTemp+":";
		  }
		     
		  value = value.substring(0,value.lastIndexOf(":"));
		  return value;
	}
	public static String ipAddrToString(byte[] ip) {
		 String value = "";
		  for(int i = 0;i < ip.length; i++){
		   String sTemp = Integer.toString(0xFF &  ip[i]);
		   value = value+sTemp+":";
		  }
		     
		  value = value.substring(0,value.lastIndexOf(":"));
		  return value;
	}
	public static byte[] ipAddrToBytes(int ip) {
		
		byte[] buffer=new byte[4];
		if(ip != 0) {
			int ip1 = ip&0xFF;
			ip = ip>>8;
            int ip2 = ip&0xFF;
            ip = ip>>8;
            int ip3 = ip&0xFF;
            ip = ip>>8;
            int ip4 = ip&0xFF;
            buffer[0]= (byte)ip1;
            buffer[1]= (byte)ip2;
            buffer[2]= (byte)ip3;
            buffer[3]= (byte)ip4;
		}
		return buffer;
	// convert hex string to byte values
	}
}
