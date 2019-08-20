/**
 * com.dinglicom.server
 * BtuStatus.java
 * 类功能：
 * 2014-4-1-下午3:55:48
 * 2014鼎利-版权所有
 * @author qihang.li@dinglicom.com
 */
package com.walktour.gui.listener;

/**
 * 数据传输服务器的状态信息
 * 
 * @author weirong.fan
 *
 */
public enum ServerStatus {
	offline, loginStart, loginSuccess, passwordChange, loginFail, processFile,

	/** 下载测试计划 */
	config,
	/** 下载测试计划失败 */
	configDLFail,
	/** 下载测试计划成功 */
	configDLSuccess,
	/** 当前已经是最新的测试计划 */
	configNoNeed,
	/** 成功替换测试计划 */
	configUpdateSuccess,
	/** 替换测试计划失败 */
	configUpdateFail,

	uploadRequest, uploadReject, uploadEofReuest, uploadEofSuccess, uploadEofFail,

}
