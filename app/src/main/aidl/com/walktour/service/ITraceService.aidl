package com.walktour.service;   
import com.walktour.service.ITraceCallback;   
  
interface ITraceService {
	void startWriteRcuFile();
	void stopWriteRcuFile();
    void registerCallback(ITraceCallback cb);   
    void unregisterCallback(ITraceCallback cb);   
    /**
	 * resolveL3msg
	 * 
	 * @param num signal tag
	 */
    String resolveL3msg(int num);
    
}