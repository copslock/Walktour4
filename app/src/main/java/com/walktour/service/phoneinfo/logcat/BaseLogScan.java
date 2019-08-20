package com.walktour.service.phoneinfo.logcat;

/**
 * 日志扫描基础类
 * 
 * @author jianchao.wang
 *
 */
public abstract class BaseLogScan {

	public LogFilter filter;
	public Logger logger;

	public BaseLogScan() {
		filter = new LogFilter();
		logger = new Logger(getClass());
	}
}
