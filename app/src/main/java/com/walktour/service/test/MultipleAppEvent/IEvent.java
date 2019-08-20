package com.walktour.service.test.MultipleAppEvent;

import java.util.Map;

/**
 * @author zhicheng.chen
 * @date 2018/8/16
 */
public interface IEvent {
	void handleStartEvent();
	void handleProcessEvent(Map<String, String> datasMap);
	void handleEndEvent();
}
