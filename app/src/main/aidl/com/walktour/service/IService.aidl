package com.walktour.service;   
  
import com.walktour.service.ICallback;
  
interface IService {
	void stopTask(boolean isTestInterrupt,int dropReason) ;
    void registerCallback(ICallback cb);
    void unregisterCallback(ICallback cb);
    boolean getRunState();
}