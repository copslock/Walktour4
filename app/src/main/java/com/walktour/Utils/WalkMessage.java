package com.walktour.Utils;

public class WalkMessage {

	public static String pushData = "com.walktour.pushData"; // 写用户自定义的事件或者数据
	public static String pushPointData = "com.walktour.pushPointData"; // 写用户自定义的采样点信息
	public static String startPlayback = "com.walktour.startplayback"; // 开始回放，需要提供端口，采样点，可以多次触发
	public static String stopPlayback = "com.walktour.endplayback"; // 停止回放，需要提供端口
	public static String getParamResponse = "com.walktour.getParamResponse"; // 获得参数反馈，提供端口，参数值组合
	public static String openFileforPlayback = "com.walktour.openfile"; // 打开回放文件，需要提供文件名，端口号
	public static String openFileforPlaybackResponse = "com.walktour.openfile.response"; // 打开回放文件结果

	public static String KEY_IS_FROM_REPLAY="com.walktour.gui.is.from.replay";//是否来自于回放选择窗口
	public static String KEY_IS_FROM_STARTDIALOG="com.walktour.gui.is.from.startdialog";//是否来自于开始测试窗口
	public static String KEY_IS_FROM_Intelligent_Analysis="com.walktour.gui.is.from.intelligentanalysis";//是否来自于智能分析窗口
	// 地图相关Action
	public static String ACTION_MAP_IMPORT_KML = "com.walktour.gui.googlemap.importkml";
	public static String KEY_MAP_KML_PATH = "com.walktour.gui.googlemap.kmlpath";

	public static final String ACTION_TRACE_GET_SIGNAL_TIME = "com.walktour.trace.getSignalTime"; // 获得信令时间
	public static final String ACTION_TRACE_SET_STARTDECODE = "com.walktour.trace.setstartDecode"; // 设置开始解码
	public static final String ACTION_TRACE_SET_STOPDECODE = "com.walktour.trace.setStopDecode"; // 设置停止解码
	public static final String ACTION_TRACE_SET_FREQUENCY_POINT = "com.walktour.trace.set.frequencyPoint"; // 设置锁定频点
	public static final String KEY_FRE_POINT = "point"; // 设置锁定频点

	public static final String ACTION_MAIN_INIT_SUCCESS = "com.walktour.main.init.success"; // 初始化设置

	public static final String ACTION_STARTDATASET_INIT_FINISH= "com.walktour.main.init.start.dataset.finish"; // 数据集启动完成

	/** Walktour运行过程中时间计时器更新消息，该消息每秒发送一次 */
	public static final String ACTION_WALKTOUR_TIMER_CHANGED = "com.walktour.timer.changed";
	/** Walktour冻屏状态改变 */
	public static final String ACTION_FREEZE_CHANGED = "com.walktour.freeze.changed";
	public static final String FreezeState = "FreezeState";

	public static final String ACTION_ADD_TAG = "com.walktour.add.tag";

	/** FTP业务过程中的时钟消息，每秒一次 */
	public static final String ACTION_WALKTOUR_TIMER_FTPCHANGED = "com.walktour.timer.ftpchanged";
	public static final String ACTION_WALKTOUR_SCANNERTIMER_CHANGED = "com.walktour.scannertimer.changed";
	/** 彩信自动下载的提示 */
	public static final String ACTION_MMS_AUTODOWNLOAD = "com.walktour.mms.autodownload";
	
	/**Info 界面从地图到事件窗口跳转*/
	public static final String ACTION_INFO_MAP2EVENT	= "com.walktour.info.map2event";
	/**回放加载完毕*/
	public static final String ACTION_REPLAY_FINISH	= "com.walktour.replayfloatview.finish";

	// 关于扫频仪测试
	public static final String ACTION_WALKTOUR_SCANNERTEST_START = "com.walktour.scannertest.start"; // 扫频仪开始测试
	public static final String ACTION_WALKTOUR_SCANNERTEST_CONNECTSCANNER = "com.walktour.scannertest.connectscanner"; // 连接扫频仪设备
	public static final String ACTION_WALKTOUR_SCANNERTEST_DISCONNECTSCANNER = "com.walktour.scannertest.disconnectscanner"; // 断开扫频仪设备
	public static final String ACTION_WALKTOUR_SCANNERTEST_CONNECTSUCCESS = "com.walktour.scannertest.connectsuccess"; // 连接成功提示
	public static final String ACTION_WALKTOUR_SCANNERTEST_CONNECTFAIL = "com.walktour.scannertest.connectfail"; // 连接成功提示
	public static final String ACTION_WALKTOUR_SCANNERTEST_DISCONNECTSUCCESS = "com.walktour.scannertest.disconnectsuccess"; // 连接成功提示
	public static final String ACTION_WALKTOUR_SCANNERTEST_STARTTEST = "com.walktour.scannertest.starttest"; // 开始测试
	public static final String ACTION_WALKTOUR_SCANNERTEST_STOPTEST = "com.walktour.scannertest.stoptest"; // 停止测试

	//任务组调度消息
	/**设定当前执行任务组序号*/
	public static final String TEST_GROUP_INDEX							= "test_group_index";
	/**设定的任务组指定测试时间到达*/
	public static final String ACTION_WALKTOUR_GROUP_TESTTIME_SETIN	= "com.walktour.group.testtime.setin";
	/**当前任务组执行的持续时间到达*/
	public static final String ACTION_WALKTOUR_GROUP_KEEPTIME_SETIN 	= "com.walktour.group.keeptime.setin";
	// 自动测试
	public static final String ACTION_WALKTOUR_START_TEST 			= "com.walktour.start.test"; 				// 自动开始测试
	public static final String NOTIFY_TESTJOBDONE 						= "com.walktour.notifytestjobdone"; 		// 测试完成
	public static final String NOTIFY_INTERRUPTJOBDONE 				= "com.walktour.INTERRUPTJOBDONE"; 			// 中断完成
	public static final String NOTIFY_GROUP_TESTJOBDONE 				= "com.walktour.group.notifytestjobdone"; 	// 组测试完成
	public static final String NOTIFY_GROUP_INTERRUPTJOBDONE 			= "com.walktour.group.INTERRUPTJOBDONE"; 	// 组中断完成
	public static final String NOTIFY_TEST_FILE_CREATED 				= "com.walktour.group.test.file.created"; 	//测试文件生成完成

	public static final String NOTIFY_TEST_IPACK_STOPED				= "com.walktour.ipack.test.stoped"; 	//通知ipack测试已经停止

	public static final String NOTIFY_STEP_TEST_JOB_PAUSE 			= "com.walktour.notifyStepTestJobPause"; // 分步测试暂停
	public static final String NOTIFY_TESTJOBDONE_PARANAME 			= "createRcuFilePath"; // 测试结束后消息中带有当前创建的RCU文件路径信息
	public static final String NOTIFY_TESTJOBDONE_RECORDID 			= "fileRecordId"; 		// 文件管理中的记录ID
	public static final String NOTIFY_TESTTING_WAITTRACEINITSUCC 	= "com.walktour.testingwaitTraceSucc"; // 测试开始的时候等待trace口初始成功
	public static final String Action_Walktour_Test_Interrupt 		= "com.walktour.TestInterrupt"; // 测试中断
	public static final String Action_Walktour_Test_Puase 			= "com.walktour.TestPuase"; // 测试暂停
	public static final String Action_Walktour_Test_Continue 			= "com.walktour.TestContinue"; // 测试继续
	public static final String Action_Walktour_Test_SMSTestStart 	= "com.walktour.SMSTestStart"; // 短信测试开始
	public static final String ACTION_ISAUTOTEST_PARA 	= "IsAutoTest"; // 是否自动测试参数
	public static final String ACTION_ISUMPCTEST_PARA 	= "IsUmpcTest"; // 是否来自UMPC的测试参数
	public static final String ACTION_ISUMPCTEST_INFO 	= "IsUmpcTestInfo"; // UMPC测试时的环境参数信息
	public static final String ACTION_LICENSE_CHANGE 	= "License_Change"; // 更新license
	public static final String TestInfoForPioneer 		= "ForPioneerStr"; 	// 业务测试过程中往Pioneer回传进度信息
	public static final String TESTFILENAME				= "TestFileName";	//当前测试文件名

	// 通知时间更改
	public static final String ACTION_TIME_CHANGE = "com.systemset.timechange";
	// 地图参数改变消息
	public static final String mapParaChanged = "com.walktour.mapParaChanged";
	// 地图颜色改消息
	public static final String mapColorChanged = "com.walktour.mapColorChanged";
	// 地图GPS轨迹颜色改变消息
	public static final String mapGpsColorChanged = "com.walktour.mapGpsColorChanged";
	// 图表曲线参数改变
	public static final String chartLineChanged = "com.walktour.chartLineChanged";
	// 图表表格参数改变
	public static final String chartTableChanged = "com.walktour.chartTableChanged";

	/** 重新初始化串口 */
	public static final String redoTraceInit = "com.walktour.redoTraceInit";
	/** 停止当前测试，重新初始化串口 */
	public static final String InteruptTestAndRedoTraceInit 	= "com.walktour.InteruptTestAndRedoTraceInit";
	/** 停止当前测试，重启手机 */
	public static final String InteruptTestAndRebootDevice 	= "com.walktour.InteruptTestAndRebootDevice";
	/** 暂停当前测试，重新初始化串口服务 */
	public static final String PuaseTestAndRedoTraceInit 		= "com.walktour.PuaseTestAndRedoTraceInit";
	/** 暂停测试且关闭RCU文件状态 */
	public static final String PuaseTestAndCloseRcuFile 		= "PuaseTestAndCloseRcuFile";
	/** 继续测前创建RCU文件 */
	public static final String ContinueAndCreateRcuFile 		= "ContinueAndCreateRcuFile";
	/** 测试数据窗口更新 */
	public static final String testDataUpdate = "com.walktour.testDataUpdate";

	// 在算分完成后通知TestService，由TestService通知事件显示界面刷新
	public static final String testNotifyTestservice 			= "com.walktour.notifytestservice";
	/** TRACE 口初始化状态写入事件窗口 */
	public static final String traceInitEventStates 			= "com.dingli.walktour.traceInitEventStates";
	public static final String traceInitStatStr 				= "com.dingli.walktour.traceInitStatStr";
	// L3信令更改事件
	public static final String traceL3MsgChanged 				= "com.walktour.traceL3MsgChanged";
	public static final String traceL3MsgInfo 					= "traceL3MsgChanged";
	public static final String traceIndexPoint 					= "traceIndexPoint";						//采样点		
	/** 告警事件发生变化 */
	public static final String ACTION_ALARM_LIST 				= "com.walktour.alarm.list";
	/** 事件过滤发生改变 */
	public static final String eventFilterChanged 				= "com.walktour.eventFilterChanged";
	/**在线状态改变通过*/
	public static final String SERVICE_CHANGE_BY_TRACE 		= "com.walktour.servicestate_change_bytrace";
	/**电信设置中,当前网络与设置的网络不一致时,发送此消息*/
	public static final String TELECOM_SETTIMG_NETNOTMATCH	= "com.walktour.telecom_set_netnotmatch";
	
	/**
	 * 解析详细层三信令
	 */
	public static final String ACTION_TRACE_RESOLVE_L3MSG_DETAIL = "com.walktour.traceResolveL3msgInfo";

	/**
	 * 相信层三信令返回字符Action
	 */
	public static final String ACTION_TRACE_RESOLVE_L3MSG_DETAIL_CALLBACK = "com.walktour.traceResolveL3msg_detail_callback";

	public static final String ToEncryptRcuFile = "com.walktour.toEncryptRucFile"; // 对生成的RCU目标文件进行加密
	public static final String EncryptFileResult = "com.walktour.empcSendEncryptFileResult";// 等待RCU加密结束
	public static final String umpcTestAutoUploadFile = "com.walktour.umpcTestAutoUploadFile";// UMPC测试自动上传每次生成的测试文件
	public static final String rcuFileSelfDefineString = "com.walktour.rcuSelfDefineString";// 往RCU文件中写入GPS信息、测试计划内容等原始信息
	public static final String rcuFileSelfDefineString_flag = "flag";
	public static final String rcuFileSelfDefineString_string = "string";
	public static final String rcuGpsPointWrite = "com.walktour.rcuGpsPointWrite"; // 往RCU文件中写入GPS点信息
	public static final String rcuGpsSourceWrite = "com.walktour.rcuGpsSourceWrite"; // 往RCU文件中写入GPS点信息，内容为处理过的GPS源始数据
	public static final String rcuFileIndoorPoint_mark = "mark";
	public static final String rcuEventWrite = "com.walktour.rcuEventWrite"; // 往RCU文件中写入事件信息
	public static final String rcuEventSend2Pad = "com.walktour.rcuEventSend2Pad"; // 往pad发送事件信息
	public static final String rcuFilePath = "rcuFilePath"; // RCU文件存储路径
	public static final String rcuIsIndoor = "rcuIsIndoor"; // RCU文件存储路径
	public static final String rcuFileNum = "rcuFileNum";
	public static final String rcuDogId = "rcuDogId";
	public static final String wifiDevice = "wifiDevice"; // wifi设备名称，这是新增加的，为了保持wifi的数据2013.7.4
	public static final String rcuFileNameTip = "rcuFileNameTip"; // rcu文件名前缀,通过系统设置获得
	public static final String rcuFileNameJobs = "rcuFileNameJbos"; // rcu文件后缀，显示测试任务名称
	public static final String rcuFileWriteFlag = "com.walktour.rcuFileWriteFlag";// 往RCU文件中与入异常监控FLAG
	public static final String rcuFileUpToPinner = "com.walktour.rcuFileUpToPinner"; // 将RCU原始数据上传到Pinner
	public static final String rcuFitlToPinnerFlag = "rcuFitlToPinnerFlag"; // 上传状态;1开始上传
																																					// 0停止上传

	// Fleet操作相关
	public static final String ACTION_FLEET_UPLOAD_TASK_INDOOR = "com.walktour.fleet.upload.indoorfile";// 室内文件
	public static final String ACTION_FLEET_RECEIVE_SMS = "com.walktour.fleet.receive.sms";// 下载测试计划
	public static final String ACTION_FLEET_SYNC = "com.walktour.fleet.sync";// 同步时间
	public static final String ACTION_FLEET_UPLOADGPS_ONECE = "com.walktour.fleet.uploadgpsonece";// 上传GPS信息(仅一次)
	public static final String ACTION_FLEET_UPLOADGPS_ONECE_STOP = "com.walktour.fleet.uploadgpsonece.stop";// 上传GPS信息(仅一次)
	public static final String ACTION_FLEET_UPLOADGPS_DONE = "com.walktour.fleet.uploadgps.done";// 完成上传GPS信息
	public static final String ACTION_FLEET_UPLOAD_END = "com.walktour.fleet.uploadend";// 上传结束

	// /*Attach测试相关*/
	// public static final String ACTION_ATTACH_REQUEST =
	// "walkour.attach.request"; //Attach请求
	// public static final String ACTION_ATTACH_ACCEPT = "walkour.attach.accept";
	// //Attach请求成功
	// public static final String ACTION_ATTACH_COMPLETE =
	// "walkour.attach.complete"; //Attach完成
	// public static final String ACTION_ATTACH_REJECT = "walkour.attach.reject";
	// //Attach拒绝
	// public static final String ACTION_DETACH_REQUEST =
	// "walkour.detach.request"; //Detach请求
	// public static final String ACTION_DETACH_ACCEPT = "walkour.detach.accept";
	// //Detach请求成功

	// /*PDP务相关的信令*/
	// public static final String ACTION_PDP_DEACT_REQUEST =
	// "Walktour.pdp.deact.request"; //pdp测试的Deactive请求
	// public static final String ACTION_PDP_DEACT_ACCEPT =
	// "Walktour.pdp.deact.accept"; //pdp测试的Deactive请求成功
	// public static final String ACTION_PDP_ACT_REQUEST =
	// "Walktour.pdp.act.request"; //pdp测试的Active请求
	// public static final String ACTION_PDP_ACT_ACCEPT =
	// "Walktour.pdp.act.accept"; //pdp测试的Active请求成功

	/* 业务事件 */
	public static final String ACTION_PROPERTY = "Walktour.ACTION_PROPERTY"; // 事件属性action
    public static final String KEY_PROPERTY = "property"; //属性

	public static final String ACTION_EVENT = "Walktour.EVENT"; // 呼叫解码信令
	public static final String KEY_EVENT_RCUID = "rcuid"; // 呼叫过程解码令信类型
	public static final String KEY_EVENT_TIME = "time"; // 时间
	public static final String KEY_EVENT_STRING = "event_str"; // 时间
	//public static final String ACTION_CALL_ATTEMPT = "Walktour.InitiativeCallTest.CALL_ATTEMPT";// 主叫起呼信令
	//public static final String ACTION_CALL_ESTABLISHED = "Walktour.InitiativeCallTest.CALL_ESTABLISHED";// 通话连接建立信令
	public static final String ACTION_CALL_PsCallStart = "Walktour.PsCALL.Start";// //被叫测试开始
	public static final String ACTION_CALL_BEFER_START = "walktour.call.ACTION_CALL_BEFER_START"; // 呼叫开始前重置呼叫事件信息
	public static final String ACTION_MT_END_EVENT = "walktour.call.ACTION_MT_END_EVENT"; // 被叫结束事件信息

	public static final String KEY_IPACK_MONITOR_NET_TYPE = "monitor_net_type"; //呼叫监听实时网络类型的值
	/* 通过串口拨打 */
	public static final String ACTION_CALL_BY_TRACE = "com.walktour.phone.call.bytrace";
	public static final String ACTION_HANGUP_BY_TRACE = "com.walktour.phone.hangup.bytrace";
	public static final String KEY_CALL_NUMBER = "number";

	public static final String ACTION_SDCARD_STATUS = "com.walktour.sdcardstatus";// SD卡状态变化
	public static final String ACTION_IMPORT_ZIP = "com.walktour.gui.setting.SysIndoor.build";// 导入建筑zip文件
	public static final String ACTION_MMS_PUSH_RECEIVE = "com.walktour.mms.push.receive";// 收到PUSH消息

	/* 主被叫联合测试 */
	public static final String ACTION_UNIT_SYNC_START 	= "com.walktour.unit.sync.start"; 		// 同步开始,由主叫或者被叫向服务器发送
	public static final String ACTION_UNIT_SYNC_DONE 	= "com.walktour.unit.sync.done"; 			// 同步完成,由服务器回调后发送
	public static final String ACTION_UNIT_NORMAL_SEND= "com.walktour.unit.normal.send"; 		// 发送常规消息到服务器
	public static final String ACTION_UNIT_NORMAL_RECEIVE = "com.walktour.unit.normal.receive";	// 接收到服务器下发的常规消息
	public static final String KEY_UNIT_MSG = "message";
	public static final String KEY_UNIT_SYNCMODEL = "syncmodel"; // 0代表组内同步，1代表全手机同步
	public static final String ACTION_UNIT_MOS_BOX_INIT	= "com.walktour.unit.mosbox.init";	//小背包测试中的MOS盒测试初始化
	public static final String ACTION_UNIT_MOS_BOX_TEST	= "com.walktour.unit.mosbox.test";	//小背包测试中的MOS盒测试
	public static final String ACTION_UNIT_MOS_RESET_BOX	= "com.walktour.unit_reset_mos";	//当MOS盒重新连接时,需要重置MOS盒初始信息,并发送重新录放音的动作
	public static final String ACTION_SYNC_NET_TYPE	= "com.walktour.action_sync_net_type";	//主被叫同步网络
	/**OS 盒测试类型:"MO/MT"*/
	public static final String KEY_MOS_BOX_TEST_TYPE		= "mosboxtype";
	/**MOS 盒测试状态:0：挂机；1：接通*/
	public static final String KEY_MOS_BOX_TEST_STATE		= "mosboxstate";
	/**MosType mos类型（POLQA/PESQ）*/
	public static final String KEY_MOS_BOX_TEST_MOSTYPE	= "mosboxmostype";
	/**Sample 采样率(8000/16000/48000)*/
	public static final String KEY_MOS_BOX_TEST_SAMPLE		= "mosboxsmaple";
	
	/*
	 * 以下４个静态值必须和WalktourPhone中的PhoneService一致. 否则无法发送广播到PhoneService
	 * 注：PhoneService是WalktourPhone进程中的服务，WalktourPhone是一个单独编译
	 * 　的程序，独立于Walkour,WalktourPhone在Walktour后自动启动
	 */
	/**
	 * 广播的Action名,后台程序WalktourPhone接收此广播锁定优先网络类型（GSM／WCDMA/CDMA/EVDO）
	 */
	public final static String SET_NETWORKTYPE_ACTION = "com.walktour.phone.setnetworktype";
	public final static String QUERY_NETWORKTYPE_ACTION = "com.walktour.phone.querynetworktype";
	/**
	 * 发送广播ACTION_NT的包名，传递的参数为int,对应不同的网络，如下
	 * RIL_REQUEST_SET_PREFERRED_NETWORK_TYPE ((int *)data)[0] is == 0 for
	 * GSM/WCDMA (WCDMA preferred) ((int *)data)[0] is == 1 for GSM only ((int
	 * *)data)[0] is == 2 for WCDMA only ((int *)data)[0] is == 3 for GSM/WCDMA
	 * (auto mode, according to PRL) ((int *)data)[0] is == 4 for CDMA and EvDo
	 * (auto mode, according to PRL) ((int *)data)[0] is == 5 for CDMA only ((int
	 * *)data)[0] is == 6 for EvDo only ((int *)data)[0] is == 7 for GSM/WCDMA,
	 * CDMA, and EvDo (auto mode, according to PRL)
	 * 源码hardware/ril/include/telephony/ril.h
	 * */
	public final static String NETWORKTYPE_KEY = "networktype";

	/**
	 * 广播的Action名，后台程序WalktourPhone接收此广播锁定频率
	 * */
	public final static String BANDMODE_ACTION = "com.walktour.phone.bandmode";
	/**
	 * 广播ACTION_BM的包名，传递参数为int,对应不同的频率，如下 ((int *)data)[0] is == 0 for
	 * "unspecified" (selected by baseband automatically) ((int *)data)[0] is == 1
	 * for "EURO band" (GSM-900 / DCS-1800 / WCDMA-IMT-2000) ((int *)data)[0] is
	 * == 2 for "US band" (GSM-850 / PCS-1900 / WCDMA-850 / WCDMA-PCS-1900) ((int
	 * *)data)[0] is == 3 for "JPN band" (WCDMA-800 / WCDMA-IMT-2000) ((int
	 * *)data)[0] is == 4 for "AUS band" (GSM-900 / DCS-1800 / WCDMA-850 /
	 * WCDMA-IMT-2000) ((int *)data)[0] is == 5 for "AUS band 2" (GSM-900 /
	 * DCS-1800 / WCDMA-850) ((int *)data)[0] is == 6 for
	 * "Cellular (800-MHz Band)" ((int *)data)[0] is == 7 for
	 * "PCS (1900-MHz Band)" ((int *)data)[0] is == 8 for
	 * "Band Class 3 (JTACS Band)" ((int *)data)[0] is == 9 for
	 * "Band Class 4 (Korean PCS Band)" ((int *)data)[0] is == 10 for
	 * "Band Class 5 (450-MHz Band)" ((int *)data)[0] is == 11 for
	 * "Band Class 6 (2-GMHz IMT2000 Band)" ((int *)data)[0] is == 12 for
	 * "Band Class 7 (Upper 700-MHz Band)" ((int *)data)[0] is == 13 for
	 * "Band Class 8 (1800-MHz Band)" ((int *)data)[0] is == 14 for
	 * "Band Class 9 (900-MHz Band)" ((int *)data)[0] is == 15 for
	 * "Band Class 10 (Secondary 800-MHz Band)" ((int *)data)[0] is == 16 for
	 * "Band Class 11 (400-MHz European PAMR Band)" ((int *)data)[0] is == 17 for
	 * "Band Class 15 (AWS Band)" ((int *)data)[0] is == 18 for
	 * "Band Class 16 (US 2.5-GHz Band)" 源码：hardware/ril/include/telephony/ril.h
	 * 更多关于频率请参考 http://wireless.agilent.com/rfcomms/refdocs/1xevdo/1
	 * xevdo_gen_bse_cell_band.php
	 */
	public final static String BANDMODE_KEY = "bandmode";

	public static final String ClueOnUploadRcuEvent = "com.walktour.ClueOnUploadRcuEvent"; // 提示后上传RCU文件事件
	public static final String monitorParaTransfers = "com.walktour.monitorParaTransfers"; // 监控参数传输
	public static final String monitorParaValues = "monitorParaValues"; // 监控参数传输参数名
	public static final String monitorDataTransfers = "com.walktour.monitorDataTransfers"; // 监控数据传输变化
	public static final String monitorDataFileReport = "com.walktour.monitorDataFileReport"; // 关闭当前数据监控文件并且上报
	public static final String monitorDataStart = "com.walktour.monitorDataStart"; // 开始访问网络
	public static final String monitorDataStop = "com.walktour.monitorDataStop"; // 停止访问网络
	public static final String telephonySMSReceived = "android.provider.Telephony.SMS_RECEIVED"; // 接受短信

	/** FTP测试开始时设置Trace口为特定Logmask，结束后重置为通用的LogMask */
	public static final String Test_Start_FrequencyChanged = "Test_Start_FrequencyChanged";// 测试开始时修改过滤频率
	public static final String Test_End_FrequencyChanged = "Test_End_FrequencyChanged"; // 测试结束时修改过滤频率
	public static final String FtpTest_Upload_Start_Logmask = "FtpTestUploadStartLogmask"; // Ftp
																																													// 上载开始时Logmask
	public static final String FtpTest_Download_Start_Logmask = "FtpTestDownloadStartLogmask";// Ftp
																																														// 下传开始时Logmask
	public static final String FtpTest_End_Logmask = "FtpTestEdnLogmask"; // Ftp
																																				// 上结束时Logmask
	public static final String FtpTest_Times_Rate = "FTPTestTimesRate"; // FTP
																																			// 测试过程瞬时速率
	public static final String FtpTest_RETR_OR_STOR = "FtpTest_RETR_OR_STOR"; // 获得传输文件大小
	public static final String FtpTest_Get_FirstData = "FtpTest_Get_FirstData"; // 收到第一个数据包消息，统计时用到此值

	public static final String Monitor_StartMonitor = "com.walktour.monitorstart"; // 开启监控
	public static final String Monitor_StopMonitor = "com.walktour.monitorstop"; // 停止监控
	public static final String Monitor_FileOperate = "com.walktour.monitorfileoperate"; // 删除三天前文件

	/** 统计明细参数改变 */
	public static final String TotalDetailParaChange = "com.walktour.totalDetailParaChange"; // 需参与统计的参数处理
	public static final String TotalParaValue = "TotalParaValue"; // 参与统计参数值
	public static final String TotalByFtpIsFull = "com.walktour.totalByFtpIsFull"; // FTP流程完整过程参数可参与统计
	public static final String TotalParaSelect = "com.walktour.totalparaselect"; // FTP流程完整过程参数可参与统计
	public static final String TotalByPBMIsFull = "com.walktour.totalByPBMIsFull"; // PBM流程完整过程参数可参与统计
	public static final String TotalByWeiBoIsFull = "com.walktour.totalByWeiBoIsFull"; // WeiBo流程完整过程参数可参与统计

	/** 多网同步测试消息 */
	public static final String MutilyTester_Start_UmpcServer = "com.walktour.MutilyTester.StartUmpcServer"; // 开启UMPC服务
	public static final String MutilyTester_Close_UmpcServer = "com.walktour.MutilyTester.CloseUmpcServer"; // 关闭UMPC服务
	public static final String MutilyTester_ReDo_ConnectServer = "com.walktour.MutilyTester.ReConnectServer"; // 重新连接UMPC服务器
	public static final String MutilyTester_ReDo_ConnectPara = "Walktour_RedoWifiBand"; // 重做WIFI绑定参数
	public static final String MutilyTester_Wifi_ConnectFaild = "com.walktour.MutilyTester.WifiConnectFaild";// Wifi连接失败
	public static final String MutilyTester_ReDo_BandWifi = "com.walktour.MutilyTester.reDoBandWifi"; // 不管当前什么状态，做重绑WIFI动作
	public static final String MutilyTester_Send_Event = "com.walktour.MutilyTester.sendEvent"; // 发送事件
	public static final String UMPC_WriteRealTimeEvent = "com.walktour.umpc.WriteRealTimeEvent"; // 发送往UMPC传实时参数信息
	public static final String UMPC_WriteRealTimeType = "com.walktour.umpc.WriteRealTimeType"; // 回传实时消息类型
																																															// 'A','E','R'...
	public static final String UMPC_WriteRealTimeInfo = "com.walktour.umpc.WriteRealTimeInfo"; // 回传实时消息内容

	/** 业务测试相关 */
	public static final String ACTION_TEST_MOS_SDCARD_UNMOUNTED = "walktour.test.mos.sdcard.unmount"; // MOS测试时无SD卡
	public static final String ACTION_TEST_DATA_APN_NULL = "walktour.test.data.apn.null"; // 数据业务的APN不存在

	/** 外循环次数 如果当前存在任务分组,任务调度时将循环次数设置为分组次数*/
	public static final String Outlooptimes = "walktour.test.outlooptimes";
	/** 外循环间隔时长 */
	public static final String OutloopInterval = "walktour.test.outloopInterval";
	/**
	 * 一次大循环不同业务间间隔时长
	 */
	public static final String DIFFERENT_TASK_INTERVAL = "walktour.test.differentTaskInterval";
	/** 外循环断开拨号 */
	public static final String OutloopDisconnetNetwork = "walktour.test.outloopDisconnetNetwork";
	/** 外循环次数 ****/
	public static final String IS_STEP_TEST = "walktour.test.isStepTest";

	/**
	 * 是否起呼录音
	 */
	public static final String IS_RECORD_CALL = "walktour.test.is_record_call";

	/** 是否CQT测试自动打点 */
	public final static String ISCQTAUTOMARK = "walktour.test.CQTAutoMark";

	public final static String CQT_AUTO_MARK_SELECTED_GLONAVIN_MODULE = "walktour.test.cqtAutoMarkSelectedGlonavinModule";

	/** 是否抓包 */
	public final static String ISNETSNIFFER = "walktour.test.netsniffer";
	/** RUC文件大小分割类型 */
	public static final String RcuFileLimitType = "walktour.test.rcufilelimittype";
	/** RCU文件分割大小 */
	public static final String RucFileSizeLimit = "walktour.test.rcufilesizelimit";
	/** 切换出地图窗口 */
	public static final String ACTION_EXIT_MAPACTIVITY = "walktour.exit.mapactivity";
	/** 测试人员 */
	public static final String KEY_TESTER = "walktour.test.tester";
	/** 测试的地铁测试的城市 */
	public static final String KEY_TEST_CITY = "walktour.test.city";
	/** 测试的地铁测试的线路 */
	public static final String KEY_TEST_METRO = "walktour.test.metro";
	/** 测试的高铁线路 */
	public static final String KEY_TEST_HIGHT_SPEED_RAIL = "walktour.test.highspeedrail";
	/** 测试的单站 */
	public static final String KEY_TEST_SINGLE_STATION = "walktour.test.singlestation";
	/** 测试地址 */
	public static final String KEY_TEST_ADDRESS = "walktour.test.address";
	/** 测试是否室内测试 */
	public static final String KEY_TEST_INDOOR = "walktour.test.indoor";
	/** 是否勾选CQT */
	public static final String KEY_TEST_CQT_CHECK = "walktour.test.cqt_checked";
	/** 测试建筑名 */
	public static final String KEY_TEST_BUILDING = "walktour.test.building";
	/** 测试楼层名 */
	public static final String KEY_TEST_FLOOR = "walktour.test.floor";
	/** 不保存测试数据 */
	public static final String KEY_TEST_DONTSAVEDATA = "walktour.test.dontsavedata";
	/** 开始测试的tag标识 */
	public static final String KEY_TEST_TAG = "walktour.test.tag";
	/** 测试任务来源*/
	public static final String KEY_FROM_TYPE = "walktour.test.fromType";
	/**是否需要执行飞行模式,默认是false,需要执行飞行模式.当为true将忽略执行飞行模式***/
	public static final String KEY_TEST_FLIGHT_MODE="walktour.test.flight.mode";
	/**测试场景来来源,手动测试,自动测试,各工单测试,取值为 DataTableStruct.SceneType枚举值*/
	public static final String KEY_FROM_SCENE= "walktour_test_from_scene";

	/**HTTP业务显示网页退出*/
	public static final String KEY_HTTP_SHOWWEB_QUID	= "walktour.http.showweb.quit";
	
	/** 执行测试 */
	public static final String ACTION_EXECUTE_TASK = "com.walktour.execute_test"; // 执行测试

	/* 当前测试的工单ID */
	public static String KEY_TEST_WORK_ORDER_ID = "walktour.test.work_order_id";

	/**** UYou 与walktour共存相关广播***** ********************************************************/
	/** UYou中发送，Walktour接收 **/
	public static final String START_UYOU = "com.dingli.uyou.start"; // 启动UYou
	public static final String KILL_UYOU = "com.dingli.uyou.kill"; // 停止UYou
	public static final String READY_UYOU = "com.dingli.uyou.ready"; // 准备启动UYou
	public static final String UYOU_HAS_START = "com.dingli.uyou.hastart"; // UYou启动完成
	/** Walktour中发送，UYou接收 **/
	public static final String KILL_WALKTOUR = "com.dingli.walktour.kill"; // 停止Walktour
	public static final String START_WALKTOUR = "com.dingli.walktour.start"; // 启动Walktour
	public static final String WALKTOUR_STATUS = "com.dingli.walktour.status"; // Walktour测试状态
	public static final String ACTION_GPS_STATE_LOST = "com.walktour.gpsstate.lost"; // GPS丢失

	public static final String ACTION_SEND_STATIC2PAD = "com.walktour.send.static2pad"; // 发送统计到pad

	public static final String ACTION_BLUETOOTH_SOCKET_CHANGE = "com.walktour.bluetooth.socketchange"; // 蓝牙连接状态改变
	public static final String ACTION_BLUE_CONNECT_STATE_CHANGE = "com.walktour.bluetooth.statechnage"; // 小背包连接状态改变

	public static final String WORK_ORDER_AH_RCU_FILE_NAME = "com.dingli.walktour.workorderah.filename"; // 安徽电信工单测试结果文件名
	/** 自动打点：建筑物高度 */
	public static final String AUTOMARK_BUILDER_HEIGHT = "com.walktour.service.automark.builderHeight";
	/** 自动打点：当前方向角 */
	public static final String AUTOMARK_ORIENTATION = "com.walktour.service.automark.orientation";
	/** 自动打点：起始点 */
	public static final String AUTOMARK_FIRST_POINT = "com.walktour.service.automark.firstPoint";
	/** 自动打点：开始监听 */
	public static final String AUTOMARK_START_MARK = "com.walktour.service.automark.startMark";
	public static final String GLONAVIN_AUTOMARK_START_MARK = "com.walktour.service.glonavin.automark.startMark";

	/** 自动打点：停止监听 */
	public static final String AUTOMARK_STOP_MARK = "com.walktour.service.automark.stopMark";
	/** 自动打点：总步数 */
	public static final String AUTOMARK_TOTAL_STEPS = "com.walktour.service.automark.totalSteps";
	/** 回放操作中清理所有数据广播*/
	public static final String REPLAY_CLEAR_ALL_DATA = "com.walktour.gui.replayfloatview.clearAllData";
	
	/** 获取源或目标小区频点数据*/
	public static final String LTE_SWITCH_BROADCAST = "com.dingli.walktour.lte.switch.broadcast";
	/**高铁线路的版本号**/
	public static final String KEY_HIGHSPEEDRAIL_VERSION = "com.dingli.walktour.gui.highspeedrail.verion";
	/**当前选择的高铁线路-中文模式**/
	public static final String KEY_HIGHSPEEDRAIL_CURRENT_RAIL_ZH = "com.dingli.walktour.gui.highspeedrail.curren.trail.zh";
	/**当前选择的高铁线路-英文模式**/
	public static final String KEY_HIGHSPEEDRAIL_CURRENT_RAIL_EN = "com.dingli.walktour.gui.highspeedrail.curren.trail.en";
	/**当用户切换了DT默认地图的时候**/
	public static final String CHANCE_DT_MAP_DEFAULT = "com.dingli.walktour.chance_map_default";
	/**ENDC 网络测试时标识是上行还是下行,**/
	public static final String ACTION_VALUE_BUSINESS_DIRECT_TYPE="com.dingli.walktour.endc.lte.throughput.broadcast";
}
