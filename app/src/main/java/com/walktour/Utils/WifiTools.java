package com.walktour.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Wifi 工具类
 * 
 * @author weirong.fan
 *
 */
public class WifiTools {
	public static String getIPAddress(int address) {
		StringBuilder sb = new StringBuilder();
		sb.append(address & 0x000000FF).append(".").append((address & 0x0000FF00) >> 8).append(".")
				.append((address & 0x00FF0000) >> 16).append(".").append((address & 0xFF000000L) >> 24);
		return sb.toString();
	}

	/**
	 * 根据频率获得信道
	 * 
	 * @param frequency
	 * @return
	 */
	public static String getChannelByFrequency(int frequency) {
		int channel = -1;
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
			case 5745:
				channel = 149;
				break;
			case 5765:
				channel = 153;
				break;
			case 5785:
				channel = 157;
				break;
			case 5805:
				channel = 161;
				break;
			case 5825:
				channel = 165;
				break;
		}
		return channel+"";
	}
	
	/***
	 * 判断wifi是否可用
	 * 
	 * @param inContext
	 * @return
	 */
	public static boolean isWiFiActive(Context inContext) {   
        Context context = inContext.getApplicationContext();   
        ConnectivityManager connectivity = (ConnectivityManager) context  .getSystemService(Context.CONNECTIVITY_SERVICE);   
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
}
