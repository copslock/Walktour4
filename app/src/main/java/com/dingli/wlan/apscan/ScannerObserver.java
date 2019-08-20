package com.dingli.wlan.apscan;

public interface ScannerObserver {
	abstract public void onGetScanResult();
	abstract public void onGetWifiStatus(int status);
}
