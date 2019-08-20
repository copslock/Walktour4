/**
 * com.dinglicom.server
 * BtuStatusListener.java
 * 类功能：
 * 2014-4-1-下午3:57:12
 * 2014鼎利-版权所有
 * @author qihang.li@dinglicom.com
 */
package com.walktour.gui.listener;

/***
 * 服务器状态监听器,ATU/Btu数据传输及任务下载.
 * 
 * @author weirong.fan
 *
 */
public interface ServerStatusListener {
	void onCompressProgress(String filename, int progress);

	void onStatusChange(ServerStatus status, String info);
}
