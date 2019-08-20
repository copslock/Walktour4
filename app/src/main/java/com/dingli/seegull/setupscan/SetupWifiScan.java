package com.dingli.seegull.setupscan;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;


public class SetupWifiScan extends SetupScanBase {
	
	private String mServerIpAddress = "192.168.1.2";
	private String mRouterSSID = "AP";
	
	public SetupWifiScan(int scanId, int scanMode, int protocolCode, int bandCode) {
		super(scanId, scanMode, protocolCode, bandCode);
		
	}

	
	
	
	/**
	 * SCAN_ID	int	Scan Id unique to individual scan.
	 * 		Value: 0 - 254
	 * SCAN_MODE	int	Scan mode value.
	 * 		Value:
	 * 		0 - Auto
	 * 		3 - Auto with SD recording
	 * ROUTER_SSID	String	Wireless Access Point SSID
	 * WIRELESS_KEY	String	Optional.
	 * Wireless Access Point Security Key.
	 * SERVER_IP_ADDRESS	String	Server IP Address or Host Name.
	 * SERVER_PORT	int	Optional. Server Port Number.
	 * NUM_BYTES_TO_XMIT	int	Optional. Number of bytes to transmit.
	 * BANDWIDTH	long	Optional. UDP bandwidth to send in bits/sec.
	 * DATA_MODE_LIST	JSON Array (type of int)	Optional, default to both uplink and downlink. Array of data modes
	 * 		1 - Uplink Throughput
	 * 		2 - Downlink Throughput

	 * @return JSONObject
	 */
	
	@Override
	public JSONObject genScanRequestBody() throws JSONException {
		JSONObject jsonObject = new JSONObject();	
		
		try {
			jsonObject.put("SCAN_ID", mScanId);
			jsonObject.put("SCAN_MODE", mScanMode); 		// 0 auto
			jsonObject.put("ROUTER_SSID", mRouterSSID);	//"AP";
			jsonObject.put("SERVER_IP_ADDRESS", mServerIpAddress);//"192.168.1.2";
		} catch (JSONException e){		
			e.printStackTrace();
		}	 
		return jsonObject;
	}
	
	
	public void setServerIp(String ip) {
		if (isIPAdress(ip))
			mServerIpAddress = ip;
	}
	public void setRouterSSID(String ssid) {
		mRouterSSID = ssid;
	}
	
	private boolean isIPAdress(String str) {   
		 Pattern pattern = Pattern.compile( "^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$" );   
		 return (str != null && pattern.matcher( str ).matches());
		 
		 //String regex = "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";
		 //str.matches(regex)
	}  	
}
