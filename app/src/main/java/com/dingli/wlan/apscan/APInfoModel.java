package com.dingli.wlan.apscan;


/*
AP Information Detailed
这些信息从Airodump中得到或者冲解码中获得
*/

public class APInfoModel {
	public String bssid = "";
	public String ssid = "";
	public int rssi;
	public String encryptionType = "";
	public String snr = "";//信噪比
	public String supportedProtocol = "";//支持协议abgn
	public String noise = "";//噪声
	public String beaconPeriod = "";
	public String data = "";
	public String mode = ""; //Infrastruacture or ad-hoc
	public String maxRate = "";
	public String auth = "";
	public boolean isSTA;
	public int signalSchwankung; //signal 波动
	public String channel = "";
	public int frequency; //witiTools.getChannel
	//public ArrayList<String> supportedRates = new ArrayList<String>();//支持速率
	public String rates;
	WifiFrameStatistics framesStatis;
	public boolean isBirdge;
}
