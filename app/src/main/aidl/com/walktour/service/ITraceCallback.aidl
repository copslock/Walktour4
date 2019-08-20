package com.walktour.service;   
interface ITraceCallback {
	void OnDataChanged(String dataList);
	void TraceInitSuccess(boolean initResult);
	void SetSignalTime(String signalTime);
	
	/**
	 * l3 decoder call back
	 * @param l3MsgInfo l3 detail message
	 */
	 void resolveL3msgInfoCallback(String l3MsgInfo);
}