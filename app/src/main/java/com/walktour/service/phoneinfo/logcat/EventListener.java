package com.walktour.service.phoneinfo.logcat;

/**
 * 事件监听类
 * 
 * @author jianchao.wang
 *
 */
public interface EventListener {
	void comeEvent(int eventCode, int causeCode, long timestamp);
}
