package com.dingli.wlan.model;

public interface WlanDataObserver {
	public void onGetData(byte[] data,int datalen);
}
