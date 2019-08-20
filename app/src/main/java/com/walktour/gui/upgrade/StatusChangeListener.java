package com.walktour.gui.upgrade;

/**
 * 状态监听接口
 * 
 * @author jianchao.wang
 * 
 */
public interface StatusChangeListener {
	/**
	 * 状态改变
	 * 
	 * @param status
	 */
	public void onStatusChange(Status status);

	/**
	 * 进度改变
	 * 
	 * @param localSize
	 * @param remoteSize
	 */
	public void onProgressChange(long localSize, long remoteSize);

	/**
	 * 设置更新的信息
	 * 
	 * @param message
	 */
	public void setUpgradeMessage(String message);

	/** 设置服务端的版本号 */
	public void setRemoteVersion(String version);
}
