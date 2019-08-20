package com.walktour.service.phoneinfo.logcat;

public interface LogObserver {
	void update(LogSubject subject, LogcatBean bean);
}
