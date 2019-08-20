package com.walktour.model;

public class WifiStaticDataModel {
	private static String SSID;
	private String BSSID;
	private String RSSI;
	private String TxPower;
	private String AuthMode;
	private String NetworkType;
	private String BeaconPeriod;
	private String TransmittedFragmentCount;
	private String FailedCount;
	private String RetryCount;
	private String MultipleRetryCount;
	private String RTSSuccessCount;
	private String RTSFailureCount;
	private String ACKFailureCount;
	private String FCSErrorCount;
	private String FrameDuplicateCount;
	private String ReceivedFragmentCount;
	
	public WifiStaticDataModel() {
		SSID = "";
		BSSID = "";
		RSSI = "";
		TxPower = "";
		AuthMode = "";
		NetworkType = "";
		BeaconPeriod = "";
		TransmittedFragmentCount = "";
		FailedCount = "";
		RetryCount = "";
		MultipleRetryCount = "";
		RTSSuccessCount = "";
		RTSFailureCount = "";
		ACKFailureCount = "";
		FCSErrorCount = "";
		FrameDuplicateCount = "";
		ReceivedFragmentCount = "";
	}
	
	public void initialized() {
		SSID = "";
		BSSID = "";
		RSSI = "";
		TxPower = "";
		AuthMode = "";
		NetworkType = "";
		BeaconPeriod = "";
		TransmittedFragmentCount = "";
		FailedCount = "";
		RetryCount = "";
		MultipleRetryCount = "";
		RTSSuccessCount = "";
		RTSFailureCount = "";
		ACKFailureCount = "";
		FCSErrorCount = "";
		FrameDuplicateCount = "";
		ReceivedFragmentCount = "";
	}
	public  String getSSID() {
		return SSID;
	}
	public  void setSSID(String data) {
		SSID = data;
	}
	
	public  String getBSSID() {
		return BSSID;
	}
	public  void setBSSID(String data) {
		BSSID = data;
	}
	
	public  String getRSSI() {
		return RSSI;
	}
	public  void setRSSI(String data) {
		RSSI = data;
	}
	
	public  String getTxPower() {
		return TxPower;
	}
	public  void setTxPower(String data) {
		TxPower = data;
	}
	
	public  String getAuthMode() {
		return AuthMode;
	}
	public  void setAuthMode(String data) {
		AuthMode = data;
	}
	
	public  String getNetworkType() {
		return NetworkType;
	}
	public  void setNetworkType(String data) {
		NetworkType = data;
	}
	
	
	public  String getBeaconPeriod() {
		return BeaconPeriod;
	}
	public  void setBeaconPeriod(String data) {
		BeaconPeriod = data;
	}
	
	public  String getTransmittedFragmentCount() {
		return TransmittedFragmentCount;
	}
	public  void setTransmittedFragmentCount(String data) {
		TransmittedFragmentCount = data;
	}
	
	public  String getFailedCount() {
		return FailedCount;
	}
	public  void setFailedCount(String data) {
		FailedCount = data;
	}
	
	public  String getRetryCount() {
		return RetryCount;
	}
	public  void setRetryCount(String data) {
		RetryCount = data;
	}
	
	public  String getMultipleRetryCount() {
		return MultipleRetryCount;
	}
	public  void setMultipleRetryCount(String data) {
		MultipleRetryCount = data;
	}
	
	public  String getRTSSuccessCount() {
		return RTSSuccessCount;
	}
	public  void setRTSSuccessCount(String data) {
		RTSSuccessCount = data;
	}
	
	public  String getRTSFailureCount() {
		return RTSFailureCount;
	}
	public  void setRTSFailureCount(String data) {
		RTSFailureCount = data;
	}
	
	public  String getACKFailureCount() {
		return ACKFailureCount;
	}
	public  void setACKFailureCount(String data) {
		ACKFailureCount = data;
	}
	
	public  String getFCSErrorCount() {
		return FCSErrorCount;
	}
	public  void setFCSErrorCount(String data) {
		FCSErrorCount = data;
	}
	
	public  String getFrameDuplicateCount() {
		return FrameDuplicateCount;
	}
	public  void setFrameDuplicateCount(String data) {
		FrameDuplicateCount = data;
	}
	
	public  String getReceivedFragmentCount() {
		return ReceivedFragmentCount;
	}
	public  void setReceivedFragmentCount(String data) {
		ReceivedFragmentCount = data;
	}
}
