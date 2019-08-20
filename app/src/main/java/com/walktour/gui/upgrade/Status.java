package com.walktour.gui.upgrade;

/**
 * 版本更新状态
 * 
 * @author jianchao.wang
 * 
 */
public enum Status {
	/** 空闲 */
	IDLE,
	/** 检查网络 */
	NETWORK_CHECK,
	/** 网络不可用 */
	NETWORK_UNAVIRABLE,
	/** 测试正在进行 */
	TEST_RUNNING,
	/** 测试正在停止 */
	TEST_STOPPING,
	/** 正在检查新版本 */
	CHECKING_VERSION,
	/** 本地已经下载最新版本 */
	VERSION_LOCAL_INCLUDE,
	/** 需要升级 */
	VERSION_NEED_UPGRADE,
	/** 当前版本最新 */
	VERSION_CURRENT_LATEST,
	/** 无版本信息 */
	VERSION_NO_INFO,
	/** 正在连接FTP */
	CONNECTING_FTP,
	/** 连接FTP失败 */
	CONNECT_FTP_FAIL,
	/** 下载进行中 */
	DOWNLOAD_DOING,
	/** 开始下载 */
	DOWNLOAD_STARTED,
	/** 下载成功 */
	DOWNLOAD_SUCCESS,
	/** 目标文件不存在 */
	REMOTE_NOT_EXSIT,
	/** 下载失败 */
	DOWNLOAD_FAIL,
	/** 下载失败(无可用SD卡) */
	DOWNLOAD_FAIL_NOSDCARD,
	/** 下载停止 */
	DOWNLOAD_STOP,
}
