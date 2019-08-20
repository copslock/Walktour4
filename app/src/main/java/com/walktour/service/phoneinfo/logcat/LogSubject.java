package com.walktour.service.phoneinfo.logcat;

import java.util.ArrayList;

/**
 * 日志项目
 * 
 * @author jianchao.wang
 *
 */
public class LogSubject {

	private ArrayList<LogObserver> observers = new ArrayList<LogObserver>();

	public void attach(LogObserver observer) {
		observers.add(observer);
	}

	public void detach(LogObserver observer) {
		observers.remove(observer);
	}

	public synchronized void notifyObservers(LogcatBean bean) {
		for (LogObserver server : observers) {
			server.update(this, bean);
		}
	}

	public void init() {

	};
}
