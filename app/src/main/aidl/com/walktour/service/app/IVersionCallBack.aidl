package com.walktour.service.app;   

interface IVersionCallBack {
	void onResultChange(int result);
	void onProgressChange(int dlSize,int totalSize); 
	boolean isTesting();
}