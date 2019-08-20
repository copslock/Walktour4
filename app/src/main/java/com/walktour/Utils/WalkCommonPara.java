package com.walktour.Utils;

/**
 * 存放应用中的常数信息
 * @author tangwq
 *
 */
public class WalkCommonPara {

	/*用户鉴权返回值*/
	public static final int POWER_LINCESE_SUCCESS		= 0;	//鉴权成功
	public static final int POWER_LICENSE_NULL			= 6;	//鉴权文件不存
	public static final int POWER_CONTENT_GROUP_FAILD	= 5;	//文件内容分组出错
	public static final int POWER_CONTENT_CHECK_FAILD	= 4;	//文件内容监权失败
	public static final int POWER_POWER_GROUP_FAILD		= 3;	//权限内容分组失败
	public static final int POWER_APPPOWER_FAILD		= 2;	//拥有应用权限无效
	public static final int POWER_INIT_USERPOWER_FAILD	= 1;	//初始化用户权限失败
	public static final int POWER_LICENSE_TIME_OUT		= 7;	//License过期
	public static final int POWER_CURRENT_STATE_CHECKIN = 8;	//当前为check in 状态
	public static final int POWER_FTP_CONNECTED_FAILD	= 9;	//ftp连接失败
	public static final int POWER_FTP_FILE_NOT_EXISTS	= 10;	//FTP文件不存在
	public static final int POWER_FTP_NET_DISCONNECTED	= 11;	//网络连接失败
	public static final int POWER_CHECK_POWER_OUTSERVICE= 12;	//检查权限时脱网
	public static final int POWER_FTP_FILE_EXISTS		= 13;	//权限文件存在
	public static final int POWER_LOCAL_INFO_BUILDFAILD	= 14;//本地检验信息生成失败
	public static final int POWER_LOCAL_INFO_CHACKFAILD	= 15;//本地检验信息服务端检测失败
	
	/*过滤频率设置模式*/
	public static final int Frequency_Disable			= 0;	//表示不过滤
	public static final int Frequency_CDMA_Normal		= 1;	//cdma一般状态
	public static final int Frequency_CDMA_Monitor		= 2;	//cdma监控状态
	public static final int Frequency_CDMA_JobTest		= 3;	//cdma业务测试状态
	public static final int Frequency_CDMA_FtpTest		= 4;	//cdmaftp数据业务状态
	public static final int Frequency_GSM_Normal		= 5;	//gsm一般状态
	public static final int Frequency_GSM_FtpUplaod		= 6;	//gms数据上传过滤反了
	public static final int Frequency_GSM_FtpDownload	= 7;	//gms数据下载过滤反了
	
	/*Logmask设置*/
	public static final int Logmask_Type_Invalid		= -1;	//通过后面设置的路径读取Logmask
	public static final int Logmask_Type_Normal			= 1; 	//普通精简模式
	public static final int Logmask_Type_DataNormal		= 2;	//数据业务精简模式
	public static final int Logmask_Type_Layout3		= 3;	//仅层3信令
	
	
	/*GPS设置*/			
	public static final int GPSCallTimeOpen 			= 0;	//通话时打开
	public static final int GPSAllTheTimeOpen			= 1; 	//始终打开
	public static final int GPSClose 					= 2; 	//关闭
	
	
	/*监控文件上传模式*/
	public static final int MonitorData_Upload_Auto		= 0;	//自动上传
	public static final int MonitorData_Upload_Manual	= 1;	//手动上传
	public static final int MonitorData_Upload_ClueOn	= 2;	//提示后上传
	
	
	/*存储设置*/
	public static final int DATA_STORSE_PHONE 			= 0;	//手机
	public static final int DATA_STORSE_SDCARD 			= 1;	//存储卡
	
	
	/*GPS开启类型*/
	public static final int OPEN_GPS_TYPE_JOBTEST		= 1;	//业务GPS测试
	public static final int OPEN_GPS_TYPE_MONITOR		= 2;	//监控服务GPS开启
	public static final int OPEN_GPS_TYPE_AUTOTEST		= 4;	//自动测试GPS开启
	public static final int OPEN_GPS_TYPE_INDOORTEST	= 8;	//室内专项GPS开启
	public static final int OPEN_GPS_TYPE_UMPC			= 16;	//UMPC GPS OPEN
	
	/*多路测试当前状态*/
	public static final int UMPC_Connected				= 0x00;	//
	public static final int UMPC_Logined				= 0x01;	//
	public static final int UMPC_TimeSynced				= 0x02;	//
	public static final int UMPC_TestPlanSeted			= 0x03;	//
	public static final int UMPC_TestStarted			= 0x04;	//
	public static final int UMPC_TestEnded				= 0x05;	//

	public static final int eNC_Error			= 0;	//错误信息，pdata为char*，错误信息
	public static final int eNI_LoadLib			= 1;	//加载库成功或失败，pdata为int，1为成功，0为失败
	public static final int eNC_Connect			= 2;	//连接成功或失败，pdata为int，1为成功，0为失败
	public static final int eNC_Login			= 3;	//登陆成功或失败，pdata为int，1为成功，0为失败
	public static final int eNC_TimeSync		= 4;	//通知已完成时间同步，pdata为NULL
	public static final int eNC_TestPlan		= 5;	//通知收到测试计划，pdata为char*，测试计划所在的文件名
	public static final int eNC_TestStart		= 6;	//开始测试，pdata为StartInfo*
	public static final int eNC_TestSop			= 7;	//停止测试，pdata为NULL
	public static final int eNC_HungUp			= 8;	//请求挂机，pdata为NULL
	public static final int eNC_StartMos		= 9;	//请求播放MOS，pdata为NULL
	public static final int eNC_BrekCurTest	= 10;	//请求跳过当前测试，pdata为NULL
	public static final int eNC_QueryFileList	= 11;	//请求文件列表，pdata为NULL，在strRet中填文件列表字符串
	public static final int eNC_RequestUpload	= 12;	//请求文件列表，pdata为请求的文件名
	public static final int eNC_UploadFinish	= 13;
	public static final int eNC_RecordGPS		= 14;	//请求记录GPS，pdata为GPS原始数据
	public static final int eNC_StartGPS		= 15;	//请求启动GPS，pdata为NULL
	public static final int eNC_StopGPS			= 16;	//请求停止GPS，pdata为NULL
	public static final int eNC_SyncMessage	= 17;	//收到同步消息，pdata为发送的同步字符串，收发一致方可认为成功同步
	public static final int eNC_NormalMessage	= 18;	//收到组内其他手机的消息，pdata为消息字符串
	public static final int eNC_WriteMark		= 19;	//收到打点消息，pdata为MarkInfo*
	public static final int eNC_DelMark			= 20;	//收到删点消息，pdata为GpsPt*
	public static final int eNC_EncryptFinish	= 21;	//UMPC 文件加密结果，pdata结果0为失败，1为成功
	public static final int eNI_DeleteFile		= 22;	//删除文件，pdata为文件名
	public static final int eNC_StartTrace    	= 23;   //开始上传trace数据
    public static final int eNC_TransTrace     = 24;   //实时上传trace数据，这个不用处理
    public static final int eNC_StopTrace      = 25;   //停止上传trace数据
    public static final int eNI_FileStatUpload= 26;   //上传包含统计SQL的文件
    public static final int eNC_SyncAlias		= 27;	//更改设置别名,如B,C,D
    public static final int eNC_Suspend			= 28;	//暂停测试
    public static final int eNC_Resume			= 29;	//恢复测试
    public static final int eNC_Transparent	= 30;	//透传
    public static final int eNC_RTUploadTrans	= 31;	//开始传输31
    public static final int eNC_RTUploadEof	= 32;	//结束传输
    public static final int eNC_Alarm			= 33;	//告警响应
    public static final int eNC_MosInit			= 34;	//Mos初始化
    public static final int eNC_MosTest			= 35;	//Mos测试
    public static final int eNC_MosScore		= 36;	//Mos分值
    public static final int eNC_CtrlConn		= 37;	//控制通道连接状态通知 ："result=%d" ，1为连接，0为断开；

	public static final int eNC_ULFleet         = 41;   //开始向FLEET上传文件
	public static final int eNC_RecordBegin         = 42;   //记录文件开始42
	public static final int eNC_RecordEnd         = 43;   //记录文件结束43
	public static final int eNC_GyroGps       = 44;   //新版陀螺仪/GPS，带速度   44

	public static final int RcuFileToPinner_Start = 1;  //开始传RCU文件流到Pinner
    public static final int RcuFileToPinner_Stop  = 0;  //停止传RCU文件流到Pinner

	public static final int Base_None			= 0;	//基站地图画高度
	public static final int Base_Height			= 1;	//基站地图画高度
	public static final int Base_PN				= 2;	//基站地图画PN值
	public static final int Base_Azimuth		= 3;	//基站地图画方位角
	
	public static final int TypeProperty_None	= 0;	//非数据类型
	public static final int TypeProperty_Voice	= 1;	//语音类型
	public static final int TypeProperty_Net	= 2;	//Net 类型
	public static final int TypeProperty_Wap	= 3;	//
	public static final int TypeProperty_Wlan	= 4;	//WIFI
	public static final int TypeProperty_Ppp	= 5;	//NBPPP拨号
	
	public static final String testModelKey		= "testModel_DataObject";	//测试任务调度模型对象传递
	public static final String testModelJsonKey		= "testModel_DataObject_json";	//测试任务调度模型对象json
	public static final String testModelJsonTypeKey		= "testModel_DataObject_type";	//测试任务调度模型对象json类型
	public static final String testRepeatTimes  = "repeatTimes";              //测试当前次数传递
	public static final String testUmpcTest  	= "isumpctest";              //测试当前次数传递
	
	public static final char MsgDataFlag_C		= 'C';	//测试模块的原始Log信息，同时室内Mark的位置信息也保存在此，具体的Mark信息的格式见
	public static final char MsgDataFlag_D		= 'D';	//测试模块的原始Log信息，同时室内Mark的位置信息也保存在此，具体的Mark信息的格式见
	public static final char MsgDataFlag_E		= 'E';	//事件结构信息，具体的事件结构见《RCU事件存储结构.doc》
	public static final char MsgDataFlag_F		= 'F';	//FTP测试中间状态信息,见结构：FTP Data格式：
	public static final char MsgDataFlag_G		= 'G';	//多任务FTP测试中间状态信息,见结构：FTP Data格式：
	public static final char MsgDataFlag_H		= 'H';	//HTTP Down测试中间状态信息,见结构：FTP Data格式：
	public static final char MsgDataFlag_X		= 'X';	//HTTP Page测试中间状态信息,见结构：FTP Data格式：
	public static final char MsgDataFlag_L		= 'L';	//Email测试中间状态信息,见结构： FTP Data格式：
	public static final char MsgDataFlag_M		= 'M';	//彩E测试的中间状态信息，见结构： FTP Data格式：
	public static final char MsgDataFlag_N		= 'N';	//彩E测试的中间状态信息，见结构： CU Data格式：
	public static final char MsgDataFlag_U		= 'U';	//彩信测试中间状态信息,见结构：FTP Data格式：
	public static final char MsgDataFlag_B		= 'B';	//Wap页面测试中间状态信息,见结构：FTP Data格式：
	public static final char MsgDataFlag_W		= 'W';	//Wap下载测试中间状态信息,见结构：FTP Data格式：
	public static final char MsgDataFlag_T		= 'T';	//TFTP测试中间状态信息,见结构：FTP Data格式：
	public static final char MsgDataFlag_P		= 'P';	//Ping测试结果信息，见结构Ping Data格式
	public static final char MsgDataFlag_V		= 'V';	//MOS测试的结果信息，见结构Voice Data 格式；现在最新的MOS测试的结果信息作为事件保存，具体的结构见《RCU事件存储结构.doc》
	public static final char MsgDataFlag_J		= 'J';	//Video Streaming测试的中间状态信息，见结构：Video Streaming Data格式
	public static final char MsgDataFlag_K		= 'K';	//Video Telephony 测试的中间状态信息，见结构：Video Telephony Data 格式
	public static final char MsgDataFlag_Z		= 'Z';	//测试任务内容，见结构：TestTask格式：
	public static final char MsgDataFlag_I		= 'I';	//测试信息内容，见结构：Test Information格式：
	public static final char MsgDataFlag_R		= 'R';	//测试信息内容，见结构：Iperf  Data 格式
	public static final char MsgDataFlag_Q		= 'Q';	//TCPIP监控汇总信息，见结构：TCPIP Monitor Data 格式
	public static final char MsgDataFlag_1		= '1';	//#1数据业务测试中物理层中间状态信息,见结构：见结构：FTP Data格式：
	public static final char MsgDataFlag_2		= '2';	//数据业务测试中网络层中间状态信息,见结构：见结构：FTP Data格式：
	public static final char MsgDataFlag_3		= '3';	//数据业务测试中传输层中间状态信息,见结构：见结构：FTP Data格式：
	public static final char MsgDataFlag_4		= '4';	//数据业务测试中TCP重传中间状态信息,见结构：见结构：FTP Data格式：
	public static final char MsgDataFlag_5		= '5';	//TD智能终端网络异常上报包含的信息，见结构：TD智能终端网络异常上报Data格式
	public static final char MsgDataFlag_6		= '6';	//Pioneer联通评估版的需求，把当前测试的DLlog文件的内容写入rcu里。
	public static final char MsgDataFlag_7		= '7';	//Pioneer 电信评估版的需要，要求rcu校验时检查是否锁3G。在开始业务前取得每个终端的网络状态，并写入到rcu。见结构：TRCUDeviceNetType格式
	public static final char MsgDataFlag_8		= '8';	//MicroBlog 测试的中间状态信息，见结构FTP Data格式：
	public static final char MsgDataFlag_O		= 'O';	//新版Video Telephony 测试的中间状态信息，见结构New Video Telephony Data 格式
	public static final char MsgDataFlag_S		= 'S';	//Voip流品质数据，见结构Voip Stream Quality Data 格式
	public static final char MsgDataFlag_Y		= 'Y';	//业务测试中，Iperf测试状态信息详情，见结构: Iperf Status Message Report Data 格式
	public static final char MsgDataFlag_A		= 'A';	//数据业务过程的品质数据，见结构DataTestQosData 格式, 以结构中的QosType来标识品质结构的类型
	public static final int SpeedTestDownloadQos= 1;
	public static final int SpeedTestUploadQos 	= 2;
	public static final int VideoPlayQos 		= 3;
	public static final int VideoStream 		= 4;
	public static final int PBMQosDown 		= 8; 				//PBM下行品质结构
	public static final int PBMQosUp 		= 9; 					//PBM上行品质结构
	public static final int WhatsApp 		= 16; 					//PBM上行品质结构

	/**PING业务开始前获得当前网络存储状态对象*/
	public static final int CallMainType_GetNetStat_ByPingStart 	= 1;
	/**子进程中设用主进程的attach动作*/
	public static final int CallMainType_Do_Attach					= 2;
	/**子进程中调用主进程的detach动作*/
	public static final int CallMainType_Do_Detach					= 3;
	
	/**子进程回调返回值类型*/
	public static final String CALL_BACK_TYPE_KEY					= "callBackType";
	public static final int CALL_BACK_SET_FIRSTDATA_STATE			= 10;
	/**子进程回调当前数据业务FirstData状态关键字*/
	public static final String CALL_BACK_FIRSTDATE_STATE_KEY		= "firstDataKey";
	public static final int CALL_BACK_VIDEO_PLAY_REAL_PARA 			= 1024;
	public static final String VIDEO_REAL_PARA_CHANGE 				= "com.walktour.video.parachange";	//视频播放参数改变
	public static final int CALL_BACK_VIDEO_STREAM_REAL_PARA 		= 128;
	/**调用主进程结果关键字*/
	public static final String CallMainResultKey 					= "CallMainResultKey";
	/**PING,SMS,等启动一次业务,做多次测试时的测试次数修改*/
	public static int	CALL_BACK_SET_TESTTIMES						= 11;
	public static final String CALL_BACK_SET_TEST_TIMES_KEY				= "CallBackSetTestTimes";
}
