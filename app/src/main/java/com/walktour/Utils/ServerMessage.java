package com.walktour.Utils;


/**
 * 和服务器操作相关的消息
 * */
public class ServerMessage {
	
//	//用户操作的事件、结果标志
	public  static final String ACTION_FLEET_RESULT = "walktour.event.result";
	
	public  static final String ACTION_FLEET_LOG = "walktour.event.log";
	public  static final String ACTION_FLEET_REFRESH = "walktour.event.refresh";
	public static final String ACTION_FLEET_MAKEPLAN = "walktour.fleet.makeplan";
	public static final String ACTION_FLEET_DOWNLOAD_AUTOTEST = "walktour.fleet.download.autotest";
	public static final String ACTION_FLEET_DOWNLOAD_MANUAL = "walktour.fleet.download.manual";
	public static final String ACTION_FLEET_DOWNLOAD_STOP ="com.walktour.fleet.download_stop";//停止下载测试计划
	public static final String ACTION_FLEET_DOWNLOAD_DONE ="com.walktour.fleet.download_done";//下载测试计划完成
	public static final String ACTION_FLEET_TIP = "walktour.fleet.tip";
	public static final String ACTION_FLEET_PROGRESS = "walktour.fleet.progress";
	public static final String ACTION_FLEET_SYNC = "walktour.fleet.sync";
	public static final String ACTION_FLEET_SYNC_DONE = "walktour.fleet.sync.done";
	public static final String ACTION_FLEET_STOPUPLD = "walktour.fleet.stop";
	public static final String ACTION_FLEET_LOGOUT = "walktour.fleet.logout";
	public static final String ACTION_FLEET_APN = "walktour.fleet.apn";
	public static final String ACTION_FLEET_SERVER_NOTSET = "walktour.fleet.dataServer.notSet";
	public static final String ACTION_FLEET_SEND_MESSAGE = "walktour.fleet.send.message";//向平台发送消息
	public static final String KEY_STOPTYPE = "Fleet.STOPTYPE";
	public static final String ACTION_FLEET_STATUS = "walktour.fleet.status";
	public static final String ACTION_FLEET_SWITCH ="walktour.switch.event";	//跳到事件页面
	public  static final String KEY_EVENT = "Fleet.Event";
//	public static final String KEY_RESULT = "Fleet.Result"; 
//	public static final String KEY_TYPE = "Fleet.Type"; 
	
	public static final String KEY_MSG_MYFILEMODEL = "Fleet.MSG.MYFILEMODEL"; 
	public static final String KEY_MSG_FILENAME = "Fleet.MSG.FILENAME";
	public static final String KEY_MSG_PROGRESS = "Fleet.MSG.PROGRESS"; 
	public static final String KEY_MSG = "Fleet.MSG"; 
	public static final String KEY_POSITION = "Fleet.position";
	public static final String KEY_PROGRESS = "Fleet.Progress";
	public static final String KEY_PATH = "Fleet.Path";
	public static final String KEY_TIME = "time";//上传文件
	public  static final int KEY_EXCEPTION =-1;//下载测试计划失败
	public  static final int KEY_TASK_DLFAIL =0;//下载测试计划失败
	public  static final int KEY_TASK_DLSUCCESS = 1;//下载测试计划成功
	public  static final int KEY_RCU_ULFAIL =2;//上传RCU文件失败 
	public static final int KEY_RCU_ULSUCCESS=3;//上传RCU文件成功
	public static final int KEY_SYNC_FAIL = 4;//时间同步失败
	public static final int KEY_SYNC_SUCCESS = 5;//时间 同步 成功
	public static final int KEY_ERR_CONNCT = 6;//连接服务器错误
	public static final int KEY_ERR_LOGIN = 7;//登录服务器错误
	public static final int KEY_CONNCTTING = 8;//正在连接服务器
	public static final int KEY_LOGIN = 100;//成功登录
	public static final int KEY_ADD_TASKLIST_SUCCESS=9;//转换测试任务列表成功
	
//	public static final int KEY_UL_INITFAIL=10;//初始化上传失败
//	public static final int KEY_UL_INITSUCCESS = 11;//初始化上传成功
//	public static final int KEY_UL_FAIL = 12;//文件上传失败
//	public static final int KEY_UL_ERROR = 125;//文件上传出现错误
//	public static final int KEY_UL_SUCCESS = 13;//文件上传成功
//	public static final int KEY_UL_SUCCESS_DELETE = 14;//文件上传成功后删除
//	public static final int KEY_UL_FINISH = 15;//上传动作完成,不管是否成功
	
	public static final int KEY_INTERUPTED = 16;//上传中断
	public static final int KEY_RECONNECT = 17;//重新连接
	public static final int KEY_STOPPING = 18;//重新连接
	public static final int KEY_STATUS = 19;//FleetService的状态
	public static final int KEY_EXIST = 20;//FleetService的状态
	
	public static final String ACTION_PLATFORM_CONTROL_TESTPLAN_START = "com.walktour.gui.platform.control.testplan.start";//平台交互控制,测试计划扫描
	public static final String ACTION_PLATFORM_CONTROL_TESTPLAN_STOP = "com.walktour.gui.platform.control.testplan.stop";//平台交互控制,测试计划扫描
	
	
//	public static final String ACTION_WIFI_CONTROL_START = "com.walktour.gui.platform.control.testplan.stop";//wifi定期扫描开始
//	public static final String ACTION_WIFI_CONTROL_STOP = "com.walktour.gui.platform.control.testplan.stop";//wifi定期扫描结束
	
	//微服务部分 
	public static final String ACTION_SHARE_SEND_CONTROL="com.walktour.gui.weifuwu.sharereceiver";
	public static final String ACTION_SHARE_SEND_CONTROL_DATA="com.walktour.gui.weifuwu.sharereceiver.data";

	//重启串口
	public static final String ACTION_REBOOT_TRACE="com.walktour.gui.reboot.trace.server.data";
}
