package com.walktour.gui.upgrade;

public interface UpgradeBinder {
	
	/**
	 * Service到Acitivity的状态回调
	 * @param statusChangeListener
	 */
	void setStatusChangeListener(StatusChangeListener statusChangeListener);
	
	/**
	 * 停止测试
	 */
	void stopTest();
	
	/**
	 * 服务器是否有新版本
	 */
	void checkNewVersion();
	/**
	 *从服务器下载
	 */
	void download();
	/**
	 * 停止下载
	 */
	void stopDownload();
	/**
	 * 安装新版本
	 */
	boolean install();
}
