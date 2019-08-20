package com.walktour.service.app;   

import com.walktour.service.app.IVersionCallBack;   

interface IVersionService {
	void startDownload(); 
	void stopDownload();
	void stopTest();
	void installApk();
	void registerCallback(IVersionCallBack cb);   
    void unregisterCallback(IVersionCallBack cb); 
}