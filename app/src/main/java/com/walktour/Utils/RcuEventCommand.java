package com.walktour.Utils;

import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.model.APNModel;

public class RcuEventCommand {
	public static int NullityRcuValue 	= -9999;	//无效的Rcu文件结果值
	// 脱网补偿信令
	// public final static String CS_QUALCOM =
	// "DD 00 00 01 00 00 00 00 00 01 00 7E";
	public final static String CS_QUALCOM = "DD 00 00 01 00 00 00 00 00 3E 65 7E";
	// 0xDD, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x3E, 0x65, 0x7E
	public final static String CS_T3G = "AA E2 2D 00 29 00 02 FF 91 00 00 00 94 "
			+ "00 0000 B0 00 91 00 9C 8F 09 00 C6 87 19 00 61 0A 00 00 43 01 00 00 "
			+ "C2 FE 1F 04 00 01 00 00 00 FF FF FF FF FF";
		
	/**脱网*/
	public final static int OUT_OF_SERVICE								= 0x00000074;
	
	/**
	 * TAG文本标识事件
	 */
	public final static int TAG_EVENT 									= 0x00000194;
	/**针对LTE切换请求，当出现LTE切换请求事件后，传入该事件的EventIndex，及以下事件属性ID，即可得到事件属性值*/
	public final static int LTE_SWITCH_REQUEST							= 0x30300;
	/**源小区频点*/
	public final static int LTE_SWITCH_SOURCE_FREQ						= 0x1FE;
	/**源小区扰码*/
	public final static int LTE_SWITCH_SOURCE_PCI						= 0x1FF;
	/**目标小区频点*/
	public final static int LTE_SWITCH_TARGE_FREQ						= 0x1F8;
	/**目前小区扰码*/
	public final static int LTE_SWITCH_TARGE_PCI						= 0x1F9;

    /**
     * 获取起呼属性值
     */
	public final static int PROPERTY_ID_VOICE_BASE                      = 200;
	
	
	//测试类型
	public final static int OutService_TestType_NONE					= 0;
	public final static int OutService_TestType_DIAL					= 1;
	public final static int OutService_TestType_FTPDL					= 2;
	public final static int OutService_TestType_FTPUL					= 3;
	public final static int OutService_TestType_PING					= 4;
	public final static int OutService_TestType_HTTP					= 5;
	public final static int OutService_TestType_WAP						= 6;
	public final static int OutService_TestType_EMAIL_DL				= 7;
	public final static int OutService_TestType_EMAIL_UL				= 8;
	public final static int OutService_TestType_VIDEOSTREAM				= 9;
	public final static int OutService_TestType_VIDEOTELEPHONE			= 10;
	public final static int OutService_TestType_MMS_SEND				= 11;
	public final static int OutService_TestType_MMS_RECEIVE				= 12;
	public final static int OutService_TestType_WAP_DL					= 13;
	public final static int OutService_TestType_PPP						= 14;
	public final static int OutService_TestType_PDP						= 15;
	public final static int OutService_TestType_ATTACH					= 16;
	public final static int OutService_TestType_U_IMAP_Send				= 17;
	public final static int OutService_TestType_U_IMAP_Receive			= 18;
	public final static int OutService_TestType_Micro_Blog_Caller		= 19;
	public final static int OutService_TestType_Micro_Blog_Called		= 20;
	public final static int OutService_TestType_gpsOne					= 21;
	public final static int OutService_TestType_QChat					= 22;
	public final static int OutService_TestType_HTTPfix					= 23;
	public final static int OutService_TestType_HTTPPage				= 24;
	public final static int OutService_TestType_Mult_Ftp_Upload			= 25;
	public final static int OutService_TestType_Mult_Ftp_Download		= 26;
	public final static int OutService_TestType_Passive_gpsOne			= 27;
	public final static int OutService_TestType_HTTP_Up					= 28;
	public final static int OutService_TestType_SpeedTest				= 29;
	public final static int OutService_TestType_VideoPlay				= 30;
	public final static int OutService_TestType_DNSLookUp				= 31;
	public final static int OutService_TestType_FaceBook				= 32;
	public final static int OutService_TestType_Iperf					= 33;
	public final static int OutService_TestType_VOIP					= 34;
	public final static int OutService_TestType_TraceRoute				= 35;
	
	public final static int DNS_TEST_TYPE_SELF							= 1;		//DNS Lookup(单独的DNS业务测试)
	public final static int DNS_TEST_TYPE_OTHER						= 2;		//other Data Service
	//数据拨号事件中，测试类型值
	public final static int TEST_TYPE_FTP_DOWNLOAD					= 1;		//FTP Download
	public final static int TEST_TYPE_FTP_UPLOAD						= 2;		//FTP Upload
	public final static int TEST_TYPE_Ping								= 3;		//Ping
	public final static int TEST_TYPE_HTTP								= 4;		//HTTP
	public final static int TEST_TYPE_WAP								= 5;		//WAP
	public final static int TEST_TYPE_Email_POP3						= 6;		//Email POP3 Download
	public final static int TEST_TYPE_Email_SMTP						= 7;		//Email SMTP upload
	public final static int TEST_TYPE_Connect							= 8;		//Connect
	public final static int TEST_TYPE_Awaken							= 9;		//Awaken
	public final static int TEST_TYPE_MMS_Send							= 10;		//MMS Send
	public final static int TEST_TYPE_MMS_Retrieval						= 11;		//MMS Retrieval
	public final static int TEST_TYPE_WAP_Download						= 12;		//WAP Download
	public final static int TEST_TYPE_Video_Streaming					= 13;		//Video Streaming
	public final static int TEST_TYPE_PDP								= 14;		//PDP
	public final static int TEST_TYPE_Attach							= 15;		//Attach
	public final static int TEST_TYPE_LBS								= 16;		//LBS
	public final static int TEST_TYPE_U_IMAP_Send						= 17;		//U-IMAP Send
	public final static int TEST_TYPE_U_IMAP_Receive					= 18;		//U-IMAP Receive
	public final static int TEST_TYPE_PPP_Test							= 19;		//PPP Test
	public final static int TEST_TYPE_Fetion							= 20;		//Fetion
	public final static int TEST_TYPE_Iperf								= 21;		//Iperf
	public final static int TEST_TYPE_TraceRoute						= 22;		//TraceRoute
	public final static int TEST_TYPE_HisiliconConnect					= 23;		//HisiliconConnect
	public final static int TEST_TYPE_MultFtpUpload						= 24;		//MultFtp(Upload)
	public final static int TEST_TYPE_MultFtpDownLoad					= 25;		//MultFtp(DownLoad)
	public final static int TEST_TYPE_Power								= 26;		//Power
	public final static int TEST_TYPE_MicroBlogCaller					= 27;		//MicroBlog Caller
	public final static int TEST_TYPE_MicroBlogCalled					= 28;		//MicroBlog Called
	public final static int TEST_TYPE_DelayTest							= 29;		//DelayTest
	public final static int TEST_TYPE_HttpFix							= 30;		//HttpFix
	public final static int TEST_TYPE_QQCaller							= 31;		//QQ Caller（QQ主叫）
	public final static int TEST_TYPE_QQCalled							= 32;		//QQ Called（QQ被叫）
	public final static int TEST_TYPE_HTTP_PAGE							= 33;		//HTTP_PAGE
	public final static int TEST_TYPE_PassivegpsOne						= 34;		//Passive gpsOne
	public final static int TEST_TYPE_SpeedTest							= 35;		//SpeedTest拨号事件
	public final static int TEST_TYPE_MultiDataService					= 38;		//并发业务拨号事件
	public final static int TEST_TYPE_DNSLookUp							= 42;		//DNSLookUp拨号事件
	public final static int TEST_TYPE_HTTPVS							= 40;		//Http VS (Video Play)
	public final static int TEST_TYPE_HTTP_UP							= 36;		//Http up
	public final static int TEST_TYPE_FACEBOOK							= 43;		//Facebook
	public final static int TEST_TYPE_PBM								= 44;		//PBM
	public final static int TEST_TYPE_WEIBO								= 45;		//WeiBo
	public final static int TEST_TYPE_WECHAT							= 46;		//微信
	public final static int TEST_TYPE_UDP                               = 58;       //UDP
	public final static int TEST_TYPE_OpenSignal                        = 59;       //OpenSignal
	public final static int TEST_TYPE_MultiHttpDownload                 = 55;       //MultiHttpDownload
	public final static int TEST_TYPE_REBOOT                            = 61;       //REBOOT
	public final static int TEST_TYPE_WeCallMoc                         = 50;       //WeCallMoc
	public final static int TEST_TYPE_WeCallMtc                         = 51;       //WeCallMtc
	public final static int TEST_TYPE_SkypeChat                         = 64;       //SkypeChat
	public final static int TEST_TYPE_SinaWeibo                         = 1009;       //SinaWeibo
	public final static int TEST_TYPE_QQ                                = 1008;       //QQ
	public final static int TEST_TYPE_WHATSAPP                          = 1011;       //WhatsApp
	public final static int TEST_TYPE_WHATSAPP_Moc                      = 1012;       //WhatsAppMoc
	public final static int TEST_TYPE_WHATSAPP_Mtc                      = 1013;       //WhatsAppMtc
	public final static int TEST_TYPE_FACKBOOK_OTT                      = 1014;       //Facebook_Ott
	public final static int TEST_TYPE_INSTAGRAM_OTT                     = 1015;       //Instagram_Ott
	public final static int TEST_TYPE_IDT                               = 1015;       //IDT


	public final static int TEST_PLAN_START								= 0x0000019C;	//当前测试任第一次开始时写入当前测试计划内容
	public final static int TEST_PLAN_STOP								= 0x0000019D;	//当前测试任务结束时写入当前测试结束标志
	/** 当前测试任第一次开始时写入当前测试信息 */
	public final static int TEST_PLAN_INFO = 0x0000019F;
	//拨号事件
	public final static int PPP_Dial_Start								= 0x00000070;	//拨号开始
	public final static int PPP_Dial_Finished							= 0x00000071;	//状态：成功Result＝1，失败Result＝0
	public final static int PPP_Hangup									= 0x00000072;	//断开网络
	public final static int PPP_DataTestUrl								= 0x00000076;	//记录数据业务测试URL信息					 
	//LTE网络下的相应事件
	public final static int Network_Connect_Start						= 0x00000601;	//PPP_Dial_Start
	public final static int Network_Connect_Success						= 0x00000602;	//PPP_Dial_Success
	public final static int Network_Connect_Failure						= 0x00000603;	//PPP_Dial_Failure
	public final static int Network_Disconnect							= 0x00000604;	//PPP_Hangup
	
	//Idle空业务事件
	public final static int Idle_Test_Start								= 0x000010C0;	//空测试开始
	public final static int Idle_Test_End								= 0x000010C1;	//空测试结束
	
	//FTP 下载事件
	public final static int FTP_DL_CtrlSockConnecting					= 0x00000080;	//连接FTP服务尝试
	public final static int FTP_TYPE_FTP								= 0;	
	public final static int FTP_TYPE_SFTP								= 1;	
	//public final static int FTP_DL_CtrlSock_Finished					= 0x00000088;	//连接尝试结果Result=1成功Result=0失败 分成下面成功两个事件
	public final static int FTP_DL_ConnectSockSucc						= 0x00000088;	//FTP下载控制， 连接FTP服务器成功
	public final static int FTP_DL_ConnectSockFailed					= 0x00000089;	//FTP下载控制， 连接FTP服务器失败。（握手失败）
	public final static int FTP_DL_LoginSuccess							= 0x0000008A;	//登录成功
	public final static int FTP_DL_LoginFailure							= 0x0000008B;	//登录失败
	public final static int FTP_DL_SendRetrCmd							= 0x00000081;	//获得下载文件大小，参数FileSize
	public final static int FTP_DL_FirstData							= 0x00000082;	//第一个数据包
	public final static int FTP_DL_LastData								= 0x00000083;	//最后一个数据包,参数ActualSize实际下载大小
	public final static int FTP_DL_Drop									= 0x00000084;	//下载掉线，参数TimeCount(实际上传时长)、ActualSize(实际上传大小)、Reason(掉线原因)
	public final static int FTP_DL_SocketDisconnected					= 0x00000085;	//断开服务器连接
	public final static int FTP_DL_Failure								= 0x0000008C;	//FTP业务失败
	public final static int FTP_DL_Tcpslow								= 0x0000008D;	//TCP启动慢结束
	public final static int FTP_DL_Support_REST							= 0x00000086;	//支持断点续传
	
	//FTP 上传事件
	public final static int FTP_UL_CtrlSockConnecting					= 0x00000090;	//FTP上传连接服务器尝试
	//public final static int FTP_UL_CtrlSock_Finished					= 0x00000098;	//连接尝试结果,Result（1－成功、0－失败）分成下面成功失败两个事件
	public final static int FTP_UL_ConnectSockSucc						= 0x00000098;	//FTP上传控制， 连接FTP服务器成功。（握手成功
	public final static int FTP_UL_ConnectSockFailed					= 0x00000099;	//FTP上传控制， 连接FTP服务器失败。（握手失败）
	public final static int FTP_UL_LoginSuccess							= 0x0000009A;	//FTP上传登录成功
	public final static int FTP_UL_LoginFailure							= 0x0000009B;	//FTP上传登录失败
	public final static int FTP_UL_Support_REST							= 0x00000096;	//FTP上传登录失败
	public final static int FTP_UL_SendStorCmd							= 0x00000091;	//FileSize(要上传文件大小)
	public final static int FTP_UL_FirstData							= 0x00000092;	//FTP上传第一个数据包
	public final static int FTP_UL_LastData								= 0x00000093;	//FTP上传最后一个数据包,ActualSize(实际传输大小)
	public final static int FTP_UL_Drop									= 0x00000094;	//FTP上传掉线,TimeCount(实际上传时长)、ActualSize(实际上传大小)、Reason(掉线原因)
	public final static int FTP_UL_Failure								= 0x0000009C;	//FTP上传掉线,TimeCount(实际上传时长)、ActualSize(实际上传大小)、Reason(掉线原因)
	public final static int FTP_UL_Tcpslow								= 0x0000009D;	//TCP启动慢结束
	public final static int FTP_UL_SocketDisconnected					= 0x00000095;	//FTP上传断开服务器连接
	
	//多路FTP下发
	public final static int Multi_FTP_DL_ConnectStart					= 0x00000250;
	public final static int Multi_FTP_DL_ConnectSuccess					= 0x00000251;
	public final static int Multi_FTP_DL_SupportReset					= 0x00000252;
	public final static int Multi_FTP_DL_ErrorHappend					= 0x00000253;
	public final static int Multi_FTP_DL_AllSendRetrCmd					= 0x00000254;
	public final static int Multi_FTP_DL_AllFirstData					= 0x00000255;
	public final static int Multi_FTP_DL_CountDownStart					= 0x00000256;
	public final static int Multi_FTP_DL_ReSendRetrCmd					= 0x00000257;
	public final static int Multi_FTP_DL_Drop							= 0x00000258;
	public final static int Multi_FTP_DL_LastData						= 0x00000259;
	public final static int Multi_FTP_DL_Disconnect						= 0x0000025A;
	public final static int Multi_FTP_DL_LoginSuccess					= 0x0000025B;
	public final static int Multi_FTP_DL_LoginFailure					= 0x0000025C;
	public final static int Multi_FTP_DL_Fail							= 0x0000025D;
	
	public final static int Multi_FTP_UL_ConnectStart 					= 0x00000260;
	public final static int Multi_FTP_UL_ConnectSuccess 				= 0x00000261;
	public final static int Multi_FTP_UL_SupportReset 					= 0x00000262;
	public final static int Multi_FTP_UL_ErrorHappend 					= 0x00000263;
	public final static int Multi_FTP_UL_AllSendStorCmd 				= 0x00000264;
	public final static int Multi_FTP_UL_AllFirstData 					= 0x00000265;
	public final static int Multi_FTP_UL_CountDownStart	 				= 0x00000266;
	public final static int Multi_FTP_UL_ReSendRetrCmd 					= 0x00000267;
	public final static int Multi_FTP_UL_Drop 							= 0x00000268;
	public final static int Multi_FTP_UL_LastData 						= 0x00000269;
	public final static int Multi_FTP_UL_Disconnect 					= 0x0000026A;
	public final static int Multi_FTP_UL_LoginSuccess 					= 0x0000026B;
	public final static int Multi_FTP_UL_LoginFailure 					= 0x0000026C;
	public final static int Multi_FTP_UL_Fail 							= 0x0000026D;
	
	//WAP Page事件
	public final static int WAP_PageStart								= 0x000000FB;	//WAP Page开始事件
	public final static int WAP_SockConnecting							= 0x000000F0;	//WAP测试Socket开始建立的时刻
	public final static int WAP_ConnectGatewayRequest					= 0x000000F1;	//连接WAP网关请求
	public final static int WAP_ConnectGatewayFinished					= 0x000000F2;	//连接WAP网关结束，该事件和发送GET命令几乎在同一时间，因此可以用它来取代发送GET命令的事件
	public final static int WAP_CONNECTGATEWAY_SUCCESS					= 1;			//测试结果：成功
	public final static int WAP_CONNECTGATEWAY_FAIL						= 0;			//测试结果：失败
	public final static int WAP_GetRequest								= 0x000000F6;	
	public final static int WAP_GetUrlFinished							= 0x000000FA;
	public final static int WAP_GetUrlFinished_Success					= 1;
	public final static int WAP_GetUrlFinished_Fail						= 0;
	public final static int WAP_FirstData								= 0x000000F3;	//WAP下载收到第一个数据包的时刻
	public final static int WAP_FirstDataEx								= 0x000000F7;	//WAP下载收到第一个数据包的时刻
	public final static int WAP_UrlRedirect								= 0x000000F8;	//WAP发生网址跳转时
	public final static int WAP_LastData								= 0x000000F4;	//WAP下载收到最后一个数据包的时刻
	public final static int WAP_Drop									= 0x000000F5;	//WAP下载掉线
	public final static int WAP_Result									= 0x000000F8;	//WAP PAGE下载结果
	//WAP Download事件
	public final static int WAPDL_SockConnecting						= 0x00000100;	//WAP Download Socket开始建立的时刻
	public final static int WAPDL_ConnectGatewayRequest					= 0x00000101;	//WAP Download连接WAP网关请求
	public final static int WAPDL_ConnectGatewayFinished				= 0x00000102;	//WAP Download连接WAP网关结束，该事件和发送GET命令几乎在同一时间，因此可以用它来取代发送GET命令的事件
	public final static int WAPDL_GetRequest							= 0x00000106;	//
	public final static int WAPDL_FirstData								= 0x00000103;	//WAP Download下载收到第一个数据包的时刻
	public final static int WAPDL_FirstDataEx							= 0x00000107;	//WAP Download下载收到第一个数据包的时刻
	public final static int WAPDL_UrlRedirect							= 0x00000108;	//WAP Download发生网址跳转时
	public final static int WAPDL_LastData								= 0x00000104;	//WAP Download收到最后一个数据包的时刻
	public final static int WAPDL_Drop									= 0x00000105;	//WAP Download掉线
	
	
	//彩信事件
	public final static int MMS_Send_SockConnecting						= 0x000000EA;	//发送连接网关请求
	public final static int MMS_Send_WSP_Connect_Request				= 0x000000E0;	//发送连接网关请求
	public final static int MMS_Send_WSP_Connect_Finished				= 0x000000E1;	//发送连接网关请求结束
	public final static int MMS_Send_Start 								= 0x000000E9;	//彩信发送开始事件							
	public final static int MMS_M_Send_Config							= 0x000000E2;	//MMS发送结果
	
	public final static int MMS_M_Notification_Ind						= 0x000000E4;	//收到PUSH
	public final static int MMS_Recv_SockConnecting						= 0x000000EB;	//发送连接网关请求
	public final static int MMS_Recv_WSP_Connect_Request				= 0x000000E5;	//MMS接收连接网关请求
	public final static int MMS_Recv_WSP_Connect_Finished				= 0x000000E6;	//MMS接收连接网关请求结果
	public final static int MMS_M_Notification_TimeOut					= 0x000000E7;	//PUSH超时
	public final static int MMS_M_Recv_FirstData						= 0x000000EC;	//
	public final static int MMS_M_NotifyResp_Ind						= 0x000000E8;	//MMS接收失败
	//彩信的原因码
	public final static int MMS_RESON_SUCCESS							= 1;			//成功
//	public final static int MMS_RESON_USERSTOP							= 101;			//用户停止	
//	public final static int MMS_RESON_TIMEOUT							= 102;			//超时
//	public final static int MMS_RESON_ERR_FAIL							= -1;			//其它错误
	//2013.4.11 所有drop相关的原因码已经统一
	
	//HttpDown事件
	public final static int HTTP_Down_Start								= 0x000000AB;		//HTTP下载业务开始
	public final static int HTTP_Down_SockConnecting					= 0x000000A0;		//HTTP下载连接服务器尝试(连接网关)
	public final static int HTTP_Down_ConnectSockSucc					= 0x000000A7;		//HTTP下载连接服务器尝试成功
	public final static int HTTP_Down_ConnectSockFailed					= 0x000000A8;		//HTTP下载连接服务器尝试失败
	public final static int HTTP_Down_UrlRedirect						= 0x000000A6;		//HTTP URL地址跳转
	public final static int HTTP_Down_SendGetCmd						= 0x000000A1;		//HTTP下载发送Get命令
	public final static int HTTP_Down_Failure							= 0x000000A9;		//HTTP业务失败事件
	public final static int HTTP_Down_FirstData							= 0x000000A2;		//HTTP下载第一个数据包
	public final static int HTTP_Down_LastData							= 0x000000A3;		//HTTP下载最后一个数据包
	public final static int HTTP_Down_Drop								= 0x000000A4;		//HTTP下载掉线
	public final static int HTTP_Down_SocketDisconnected				= 0x000000A5;		//HTTP下载断开服务器连接

	//MultiHttpDown
	public final static int Multi_HTTP_Down_Start=0x00001301;
	public final static int Multi_HTTP_Down_First_Data=0x00001302;
	public final static int Multi_HTTP_Down_Last_Data=0x00001303;
	public final static int Multi_HTTP_Down_Drop=0x00001304;
	public final static int Multi_HTTP_Down_Failure=0x00001305;
	
	//Http Page事件
	public final static int HTTP_PageStart								= 0x00000F0C;		//HTTP Page测试开始事件
	public final static int HTTP_SockConnecting							= 0x00000F01;		//HTTP下载连接服务器尝试(连接网关)
	public final static int HTTP_SockFailure							= 0x00000F08;		//HTTP下载连接服务器尝试(连接网关)
	public final static int HTTP_SockSuccess							= 0x00000F0A;		//HTTP下载连接服务器尝试(连接网关)
	public final static int HTTP_SendGetCmd								= 0x00000F02;		//HTTP下载发送Get命令
	public final static int HTTP_SendGetCmdFail							= 0x00000F09;		//HTTP下载发送Get命令失败
	public final static int HTTP_UrlRedirect							= 0x00000F07;		//网址跳转
	public final static int HTTP_FirstData								= 0x00000F03;		//HTTP下载第一个数据包
	public final static int HTTP_MainPageOk								= 0x00000F0D;		
	public final static int HTTP_LastData								= 0x00000F04;		//HTTP下载最后一个数据包
	public final static int HTTP_Drop									= 0x00000F05;		//HTTP下载掉线
	public final static int HTTP_Page_Fail								= 0x00000F0B;		//HTTP业务失败
	public final static int HTTP_SocketDisconnected						= 0x00000F06;		//HTTP下载断开服务器连接
	
	//业务失败的原因码(新的RCU文档里有描述)
	public static final int FAIL_NORMAL									= 1000;
	public static final int FAIL_NODATA									= 1001;
	public static final int FAIL_USER_STOP								= 1002;
	public static final int FAIL_CONNECT_TIMEOUT						= 1003;
	public static final int FAIL_FILE_NOT_EXIT							= 1010;
	public static final int FAIL_FILE_SIZE_ZERO							= 1011;
	public static final int FAIL_FILE_OPEN_FAILED						= 1012;
	public static final int FAIL_CMD_RESP_FAILED						= 1020;
	public static final int FAIL_EMAIL_STREAM_ERROR						= 1030;
	public static final int FAIL_EMAIL_NOT_EXIT							= 1031;
	public static final int FAIL_EMAIL_SIZE_ZERO						= 1032;
	public static final int FAIL_EMAIL_SELECT_ERROR						= 1033;
	public static final int FAIL_HOST_NOT_FOUND							= 1040;

	
	//语音事件
	public final static int MOC_START									= 0x0000000A;		//主叫起呼
	public final static int MTC_START									= 0x00000064;		//被叫起呼
	public final static int HANGUP_DIAL									= 0x0000000B;		//挂机
	public final static int CMServiceRequest							= 0x00000069;	
	public final static int Alerting									= 0x00000061;	
	public final static int Connect										= 0x00000062;	
	public final static int Disconnect									= 0x0000006c;	
	public final static int PagingResponse								= 0x0000006a;
	public final static int VoiceCallStart								= 0x0000006b;		//语音呼叫开始
	public final static int VoiceCallEnd								= 0x0000006c;		//主意呼叫结束
	public final static int PESQ_score									= 0x00000150;		//PESQ语音评估分数值
	public final static int POLQA_RESULT								= 0x00000154;		//POLAQ语音评估分数值
	public final static int RecordBeforeCall							= 0x00000151;		//通话前录音
	public final static int VIDEO_MOC_START							= 0x00000188;		//视频通话主叫开始
	public final static int VIDEO_MOC_HANGUP							= 0x00000189;		//视频通话主叫开始
	public final static int VIDEO_MTC_START							= 0x0000018A;		//视频通话被叫开始
	
	
	
	//SMS事件 
	public final static int SMS_Access_Request							= 0x000000D0;		//SMS发送开始
	public final static int SMS_Send_Finished							= 0x000000D1;		//SMS发送事件
	public final static int SMS_Received								= 0x000000D3;		//SMS接收事件
	
	
	//停止测试
	public final static int Stop_Logging								= 0x00000210;   	//停止写入RUC文件时插入该事件
	public final static int Logging_Pause								= 0x00000211;   	//
	public final static int Stop_Test									= 0x00000212;		//所有前台测试产品,如果是手工强制/(正常)停止当前测试业务,则必须在软件强制停止测试时,插入StopTest事件
	public final static int DeviceLost									= 0x00000214;		//设备丢失
	public final static int DeviceReboot								= 0x00000215;		//设备重启
	public final static int ParallelStart								= 0x0000019A;		//并发业务开始
	public final static int ParallelFinish								= 0x0000019B;		//并发业务结束
	public final static int DataServiceMsg								= 0x00000199;		//测试时，记录业务过程中的相关信息（如告警等）
	
	//ping测试事件
	public final static int Ping_Start									= 0x00000190;		//开始ping测试
	public final static int Ping_Success								= 0x00000192;		//ping成功
	public final static int Ping_Failure								= 0x00000193;		//ping失败
	
	//Attach事件 
	public final static int Attach_Start								= 0x00000130;		//Attach测试开始
	public final static int Attach_Success								= 0x0000000C;		//Attach成功
	public final static int Attach_Fail									= 0x0000000D;		//Attach失败
	public final static int Attach_LTE_Start							= 0x00000606;		//LTE Attach测试开始
	public final static int Attach_LTE_Success							= 0x00000607;		//LTE Attach成功
	public final static int Attach_LTE_Fail								= 0x00000608;		//LTE Attach失败

	public final static int DETACH_REQUEST                              = 0x00000139;
	public final static int DETACH_SUCCESS                              = 0x0000013A;
	public final static int DETACH_FAILURE                              = 0x0000013B;
	//PDP事件
	public final static int PDP_Start									= 0x00000140;		//PDP测试开始
	public final static int PDP_Request									= 0x00000141;		//PDP激活请求
	public final static int PDP_Activate_Success						= 14;				//PDP激活请求成功
	public final static int PDP_Activate_Fail							= 15;				//PDP激活请求失败
	//SMTP业务相关事件
	public final static int EMAIL_SMTP_SOCK_CONNECTING					= 0x000000C0;		//SMTP邮件服务器连接尝试
	public final static int EMAIL_SMTP_SEND_MIAL_CMD					= 0x000000C1;		//Email发送Get命令
	public final static int EMAIL_SMTP_FIRST_DATA						= 0x000000C2;		//Email发送第一个数据包
	public final static int EMAIL_SMTP_LAST_DATA						= 0x000000C3;		//Email发送最后一个数据包
	public final static int EMAIL_SMTP_DROP								= 0x000000C4;		//Email发送掉线
	public final static int EMAIL_SMTP_EOM_ACK							= 0x000000C5;		//SMTP邮件服务器断开连接
	public final static int EMAIL_SMTP_FAILURE								= 0x000000C6;	//SMTP业务失败
	public final static int EMAIL_SMTP_START							= 0x000000C7;		//开始连接
	public final static int EMAIL_SMTP_SOCK_CONNECT_SUCCESS				= 0x000000C8;		//连接成功
	public final static int EMAIL_SMTP_SOCK_CONNECT_FAILURE				= 0x000000C9;		//连接失败
	public final static int EMAIL_SMTP_LOGIN_SUCCESS					= 0x000000CA;		//登录成功
	public final static int EMAIL_SMTP_LOGIN_FAILURE					= 0x000000CB;		//登录失败
	public final static short EMAIL_TYPE_NORMAL							= 0;				//普通email
	public final static short EMAIL_TYPE_COLORE							= 1;				//彩E
	//POP业务相关事件
	public final static int EMAIL_POP3_SOCK_CONNECTING					= 0x000000B0;	 	//pop邮件服务器连接尝试
	public final static int EMAIL_POP3_SEND_RETR_CMD					= 0x000000B1;		//Email发送RETR命令
	public final static int EMAIL_POP3_FIRST_DATA						= 0x000000B2;		//Emai下载第一个数据包
	public final static int EMAIL_POP3_LAST_DATA						= 0x000000B3;		//Email下载最后一个数据包
	public final static int EMAIL_POP3_DROP								= 0x000000B4;		//Email接收掉线
	public final static int EMAIL_POP3_RETR_FINISHED					= 0x000000B5;				//接收收到结束标志数据包的时刻
	public final static int EMAIL_POP3_FAILURE							= 0x000000B6;				//接收失败
	public final static int EMAIL_POP3_START							= 0x000000B7;				//接收开始
	public final static int EMAIL_POP3_SOCK_CONNECT_SUCCESS				= 0x000000B8;				//连接成功
	public final static int EMAIL_POP3_SOCK_CONNECT_FAILURE				= 0x000000B9;				//连接失败
	public final static int EMAIL_POP3_LOGIN_SUCCESS					= 0x000000BA;					//登录成功
	public final static int EMAIL_POP3_LOGIN_FAILURE					= 0x000000BB;					//登录失败
	
	//Drop的原因码
	public static final int DROP_NORMAL            						= 0;
	public static final int DROP_TIMEOUT          	 					= 1;
	public static final int RESTART                						= 2;
	public static final int DROP_NOREPONSE         						= 3;
	public static final int DROP_USERSTOP          						= -101;
	public static final int DROP_PPPDROP           						= -102;
	public static final int DROP_NODATA            						= -103;
	public static final int DROP_OUT_OF_SERVICE    					= -104;
	public static final int DROP_DEVICE_LOST       					= -105;
	public static final int DROP_CLIENT_EXIT       					= -106;
	public static final int DROP_NETWORK_NO_MATCH						= -107;
	public static final int DROP_UNKNOWN       							= 1000;
		
	//Wlan AP关联测试
	public final static int WLAN_WIFI_SEARCH_START						= 0x00000501;			//开始连接Wifi
	public final static int WLAN_WIFI_SEARCH_DONE						= 0x00000502;			//开始连接Wifi
	public final static int WLAN_WIFI_CONNECT_START						= 0x00000503;			//开始连接Wifi
	public final static int WLAN_WIFI_CONNECT_SUCCESS					= 0x00000504;			//连接wifi成功
	public final static int WLAN_WIFI_CONNECT_FAILURE					= 0x00000505;			//连接wifi失败
	
	public final static int WLAN_OPEN_PORTAL_PAGE_START					= 0x00000286;		//打开登录主页
	public final static int WLAN_OPEN_PORTAL_PAGE_FIRST_DATA			= 0x00000287;	//登录主页第一个数据包到达
	public final static int WLAN_OPEN_PORTAL_PAGE_SUCCESS				= 0x00000288;	//打开登录主页成功
	public final static int WLAN_OPEN_PORTAL_PAGE_FAILURE				= 0x00000289;	//打开登录主页失败
	//WLAN Web登陆认证测试
	public final static int WLAN_LOGIN_START							= 0x00000280;	//开始登录
	public final static int WLAN_LOGIN_SUCCESS							= 0x00000281;	//登录成功
	public final static int WLAN_LOGIN_FAILURE							= 0x00000282;	//登录失败
	//WLAN Web登出认证测试
	public final static int WLAN_LOGOUT_START							= 0x00000283;	//注销开始
	public final static int WLAN_LOGOUT_SUCCESS							= 0x00000284;	//注销成功
	public final static int WLAN_LOGOUT_FAILURE							= 0x00000285;	//注销成功
	
	public final static int	WLAN_DISCONNECT								= 0x00000506;
	public final static int WLAN_ROAM_DISCONNECT_AP						= 0x00000508;
	public final static int WLAN_ROAM_AP								= 0x00000509;
	
	public final static int WLAN_AUTHENTICATE_START						= 0x0000050A;
	public final static int WLAN_AUTHENTICATE_SUCCESS					= 0x0000050B;
	public final static int WLAN_AUTHENTICATE_FAILURE					= 0x0000050C;
	public final static int WLAN_WIFI_AP_ADDED							= 0x0000050D; //周期性扫描,每找到一个新的AP,写入一次
	public final static int WLAN_WIFI_AP_CONNECTED						= 0x0000050E; //连接上某个AP时,写入
	public final static int WLAN_WIFI_AP_SCANNED_INFO					= 0x0000050F; //周期性扫描wifi信息
	
	
	//WIFI AP关联成功后,获取ip时写入,DHCP事件 
	public final static int WLAN_DHCP_START								= 0x00000300;
	public final static int WLAN_DHCP_SUCCESS							= 0x00000301;
	public final static int WLAN_DHCP_FAILED							= 0x00000302;
	public final static int WLAN_DHCP_RELEASESTART						= 0x00000303;
	public final static int WLAN_DHCP_RELEASESUCCESS					= 0x00000304;
	
	/**
	 * 用户感知系统，CDMA监控事件定义
	 */
	public final static int Monitor_CallType_MO							= 1;			//主叫
	public final static int Monitor_CallType_MT							= 2;			//被叫
	public final static int Monitor_FailType_SingalLayer				= 0x00;
	public final static int Monitor_MOBlockCallCause					= 0x00;
	public final static int Monitor_MOBlockCall							= 0x0000;		//主叫接入失败
	public final static int Monitor_MTBlockCallCause					= 0x01;
	public final static int Monitor_MTBlockCall							= 0x0001;		//被叫接入失败
	public final static int Monitor_MOCallDelayTooLongCause				= 0x02;
	public final static int Monitor_MOCallDelayTooLong					= 0x0002;		//主叫接入时延长
	public final static int Monitor_MTCallDelayTooLongCause				= 0x03;
	public final static int Monitor_MTCallDelayTooLong					= 0x0003;		//被叫接入时延长
	public final static int Monitor_MultiAccessAttempt					= 0x0004;		//多次接入尝试
	public final static int Monitor_DataAccessFailure					= 0x0005;		//数据接入失败
	public final static int Monitor_MOCDropCallCause					= 0x06;
	public final static int Monitor_MOCDropCall							= 0x0006;		//主叫掉话
	public final static int Monitor_MTCDropCallCause					= 0x07;
	public final static int Monitor_MTCDropCall							= 0x0007;		//被叫掉话
	public final static int Monitor_RegistrationFailure					= 0x0008;		//登记失败
	public final static int Monitor_OutOfServiceCause					= 0x09;        		//手机脱网
  public final static int Monitor_OutOfService						= 0x0009;		//手机脱网
  public final static int Monitor_SoftHandoffFailure					= 0x0010;		//软切换失败
	public final static int Monitor_HardHandoffFailure					= 0x0011;		//硬切换失败
	public final static int Monitor_PoorCoverageCause					= 0x12;
	public final static int Monitor_PoorCoverage						= 0x0012;		//弱覆盖
	public final static int Monitor_BadCoverageCause					= 0x13;			
	public final static int Monitor_BadCoverage							= 0x0013;		//链路质量差
	public final static int Monitor_EdgeCoverageCause					= 0x14;
	public final static int Monitor_EdgeCoverage						= 0x0014;		//边缘覆盖
	public final static int Monitor_ForwardLinkCause					= 0x15;
	public final static int Monitor_ForwardLinkInterference				= 0x0015;		//前向链路干扰
	public final static int Monitor_PNPollution							= 0x0016;		//导频污染
	public final static int Monitor_PPPFailure							= 0x0017;		//EVDO Connection掉线
	/**
	 * 用户感知GSM事件定义
	 */
	public final static int Monitor_GSM_FailType_SingalLayer			= 0x06;			//信令异常类型 
	public final static int Monitor_GSM_DropCallCause					= 0x00;
	public final static int Monitor_GSM_DropCall						= 0x0600;		//掉话
	public final static int Monitor_GSM_BlockCallCause					= 0x01;
	public final static int Monitor_GSM_BlockCall						= 0x0601;		//未接受通
	
	/**
	 * 关键点记录事件
	 */
	public final static int Monitor_FailType_KeyFlag					= 0x08;
	public final static int Monitor_CallAttemptCause					= 0x00;
	public final static int Monitor_CallAttempt							= 0x0800;		//起呼
	public final static int Monitor_ConnectCause						= 0x01;
	public final static int Monitor_Connect								= 0x0801;		//接通
	public final static int Monitor_DisconnectCause						= 0x02;
	public final static int Monitor_Disconnect							= 0x0802;		//通话结束
	
	/**通用版专用主叫事件 */
	public static final int ET_IPhoneMOAttempt = 0x0801;
	public static final int ET_IPhoneMOEstablished = 0x0802;
	public static final int ET_IPhoneMOBlock = 0x0803;
	public static final int ET_IPhoneMODrop = 0x0804;
	public static final int ET_IPhoneMOCallEnd = 0x0805;
	public static final int ET_IPhoneMOSetup = 0x080A; // 主叫振铃
	public static final int ET_IPhoneMOStart = 0x080C;

	/**通用版专用被叫事件 */
	public static final int ET_IPhoneMTAttempt = 0x0806;
	public static final int ET_IPhoneMTEstablished = 0x0807;
	public static final int ET_IPhoneMTBlock = 0x0808;
	public static final int ET_IPhoneMTDrop = 0x0809;
	public static final int ET_IPhoneMTCallEnd = 0x0810;
	public static final int ET_IPhoneMTSetup = 0x080B; // 被叫振铃
	public static final int ET_IPhoneMTStart = 0x080D;

	/**
	 * 流媒体事件定义
	 */
	public final static int STREAM_REQUEST								= 0x00000170;	//Video Stream Start
	public final static int RECEPTION_OF_FIRST_DATA_PACKET				= 0x00000171;    //Video Stream First Data
	public final static int STREAM_REPRODUCTION_START					= 0x00000172;    //Video Stream Reproduction Start
	public final static int STREAM_END									= 0x00000173;	//Video Stream Finished
	public final static int STREAM_DROP									= 0x00000174;	//Video Stream Drop
	public final static int STREAM_REPRODUCTION_END						= 0x00000175;	//Video Stream Reproduction End
	public final static int STREAM_REQUEST_SUCCESS						= 0x00000176;    //Video Stream Request Success
	public final static int STREAM_REQUEST_FAILURE						= 0x00000177;    //Video Stream Request Failure
	public final static int STREAM_RECEIVE_OVERTIME						= 0x00000178;	//Stream Receive OverTime
	
	public static final int Streaming_Request							= 0x00001030;
	public static final int Streaming_First_Data						= 0x00001031;
	public static final int Streaming_Request_Failure					= 0x00001032;
	public static final int Streaming_Reproduction_Start				= 0x00001033;
	public static final int Streaming_Reproduction_Start_Failure		= 0x00001034;
	public static final int Streaming_Last_Data							= 0x00001035;
	public static final int Streaming_Drop								= 0x00001036; 
	public static final int Streaming_Play_Finished						= 0x00001037; 
	public static final int Streaming_Rebuffering_Start					= 0x00001038;
	public static final int Streaming_Rebuffering_End					= 0x00001039;
	
	//DNS LookUp事件定义
	/**
	 * DNS LookUp Start
	 */
	public final static int DNS_LOOKUP_START							= 0x00001020;
	/**DNS LookUp Success*/
	public final static int DNS_LOOKUP_SUCCESS							= 0x00001021;
	/**DNS lookup failure*/
	public final static int DNS_LOOKUP_FAILURE							= 0x00001022;
	
	//Speed Test事件定义
	/**开始连接服务器*/
	public static final int SPEEDTEST_SOCKCONNECTING					= 0x00000C20;
	/**连接服务器成功*/
	public static final int SPEEDTEST_CONNECTSOCKSUCC					= 0x00000C21;
	/**连接服务器失败*/
	public static final int SPEEDTEST_CONNECTSOCKFAILED					= 0x00000C22;
	/**开始发送服务器请求*/
	public static final int SPEEDTEST_PING_STRART						= 0x00000C23;
	/**收到服务器响应*/
	public static final int SPEEDTEST_PING_SUC							= 0x00000C24;

	/**开始下载文件*/
	public static final int SPEEDTEST_DL_STRART							= 0x00000C26;
	/**下载完成*/
	public static final int SPEEDTEST_DL_SUC							= 0x00000C27;
	
	/**开始上传*/
	public static final int SPEEDTEST_UL_START							= 0x00000C2A;
	/**上传成功*/
	public static final int SPEEDTEST_UL_SUC							= 0x00000C2B;
	
	public static final int SPEEDTEST_FINISH							= 0x00000C2E;
	public static final int SPEEDTEST_FAIL								= 0x00000C2F;
	/**断开服务器连接*/
	public static final int SPEEDTEST_SOCKETDISCONNECTED				= 0x00000C31;
	//OpenSignal Test事件定义
	/**开始连接服务器*/
	public static final int OPENSIGNAL_SOCKCONNECTING					= 0x00001320;
	/**连接服务器成功*/
	public static final int OPENSIGNAL_CONNECTSOCKSUCC					= 0x00001321;
	/**连接服务器失败*/
	public static final int OPENSIGNAL_CONNECTSOCKFAILED					= 0x00001322;
	/**开始发送服务器请求*/
	public static final int OPENSIGNAL_PING_STRART						= 0x00001323;
	/**收到服务器响应*/
	public static final int OPENSIGNAL_PING_SUC							= 0x00001324;

	/**开始下载文件*/
	public static final int OPENSIGNAL_DL_STRART							= 0x00001326;
	/**下载完成*/
	public static final int OPENSIGNAL_DL_SUC							= 0x00001327;

	/**开始上传*/
	public static final int OPENSIGNAL_UL_START							= 0x0000132A;
	/**上传成功*/
	public static final int OPENSIGNAL_UL_SUC							= 0x0000132B;

	public static final int OPENSIGNAL_FINISH							= 0x0000132E;
	public static final int OPENSIGNAL_FAIL								= 0x0000132F;
	/**断开服务器连接*/
	public static final int OPENSIGNAL_SOCKETDISCONNECTED				= 0x00001331;
	
	//Http Up事件
	public static final int HTTP_UP_START								= 0x00001000;
	public static final int HTTP_UP_CONNECT_START	    				= 0x00001001;
	public static final int HTTP_UP_CONNECT_SUCCESS						= 0x00001002;
	public static final int HTTP_UP_CONNECT_FAILED						= 0x00001003;
	public static final int HTTP_UP_SEND_POST_COMMAND					= 0x00001004;
	public static final int HTTP_UP_FIRST_DATA							= 0x00001005;
	public static final int HTTP_UP_DROP								= 0x00001006;
	public static final int HTTP_UP_LAST_DATA							= 0x00001007;
	public static final int HTTP_UP_FAILURE								= 0x00001008;
	
	//Video Play事件
	public static final int VIDEOPLAY_REQUEST							= 0x00001010;
	public static final int VIDEOPLAY_FIRST_DATA						= 0x00001011;
	public static final int VIDEOPLAY_URLPARSE_SUCCESS				= 0x0000101F;
	public static final int VIDEOPLAY_KPIS_REPORT						= 0x0000102F;
	public static final int VIDEOPLAY_REQUEST_FAILURE					= 0x00001012;
	public static final int VIDEOPLAY_REPRODUCTION_START				= 0x00001013;
	public static final int VIDEOPLAY_REPRODUCTION_START_FAILURE		= 0x00001014;
	public static final int VIDEOPLAY_LAST_DATA							= 0x00001015;
	public static final int VIDEOPLAY_DROP								= 0x00001016;
	public static final int VIDEOPLAY_PLAY_FINISHED						= 0x00001017;
	public static final int VIDEOPLAY_REBUFFERING_START					= 0x00001018;
	public static final int VIDEOPLAY_REBUFFERING_END					= 0x00001019;
	public static final int VIDEOPLAY_CONNECT_START						= 0x0000101A;
	public static final int VIDEOPLAY_CONNECT_SUCCESS						= 0x0000101B;
	public static final int VIDEOPLAY_CONNECT_FAILED						= 0x0000101C;
	public static final int VIDEOPLAY_SEND_GET_CMD							= 0x0000101D;
	public static final int VIDEOPLAY_SEND_GET_CMD_FAILED			= 0x0000101E;
	public static final int VIDEOPLAY_VMOS_REPORT						= 0x0000101F;
	public static final int VIDEOPLAY_SEGMENT_REPORT						= 0x0000102E;
	
	//Facebook
	public static final int Facebook_Test_Start						= 0x00001110;
	public static final int Facebook_Action_Start						= 0x00001111;
	public static final int Facebook_Action_Success					= 0x00001112;
	public static final int Facebook_Action_Failure					= 0x00001113;
	public static final int Facebook_Test_Success					= 0x00001114;
	public static final int Facebook_Test_Failure				= 0x00001115;
	
	//微信
	public static final int WeChat_Test_Start						= 0x00001100;
	public static final int WeChat_Action_Start						= 0x00001101;
	public static final int WeChat_Action_Success					= 0x00001102;
	public static final int WeChat_Action_Failure					= 0x00001103;
	public static final int WeChat_Test_Success					= 0x00001104;
	public static final int WeChat_Test_Failure				= 0x00001105;

	//WeChat
	public static final int Multiple_WeChat_Test_Start				= 0x000010001;
	public static final int Multiple_WeChat_Action_Start			= 0x000010002;
	public static final int Multiple_WeChat_Action_Success			= 0x000010003;
	public static final int Multiple_WeChat_Action_Failure			= 0x000010004;
	public static final int Multiple_WeChat_Test_END				= 0x000010005;

	//微信主叫
	public static final int WECALL_MO_DIAL                          = 0x000010D0;
	public static final int WECALL_MO_ATTEMPT                       = 0x000010D1;
	public static final int WECALL_MO_SETUP                         = 0x000010D2;
	public static final int WECALL_MO_ESTABLISH                     = 0x000010D3;
	public static final int WECALL_MO_BLOCK                         = 0x000010D4;
	public static final int WECALL_MO_END                           = 0x000010D5;
	public static final int WECALL_MO_DROP                          = 0x000010D6;
	public static final int WECALL_MO_HANGUP                        = 0x000010D7;

	//微信被叫
	public static final int WECALL_MT_DIAL                          = 0x000010D8;
	public static final int WECALL_MT_ATTEMPT                       = 0x000010D9;
	public static final int WECALL_MT_SETUP                         = 0x000010DA;
	public static final int WECALL_MT_ESTABLISH                     = 0x000010DB;
	public static final int WECALL_MT_BLOCK                         = 0x000010DC;
	public static final int WECALL_MT_END                           = 0x000010DD;
	public static final int WECALL_MT_DROP                          = 0x000010DE;
	public static final int WECALL_MT_HANGUP                        = 0x000010DF;

	//QQ
	public static final int Multiple_App_QQ_Start                   = 0x00001380;
	public static final int Multiple_App_QQ_Action_Start            = 0x00001381;
	public static final int Multiple_App_QQ_Action_Success          = 0x00001382;
	public static final int Multiple_App_QQ_Action_Failure          = 0x00001383;
	public static final int Multiple_App_QQ_END                     = 0x00001384;

	//TaBao
	public static final int Multiple_TaoBao_Test_Start				= 0x000012001;
	public static final int Multiple_TaoBao_Action_Start			= 0x000012002;
	public static final int Multiple_TaoBao_Action_Success			= 0x000012003;
	public static final int Multiple_TaoBao_Action_Failure			= 0x000012004;
	public static final int Multiple_TaoBao_Test_END				= 0x000012005;

	//SinaWeBo
	public static final int Multiple_App_SinaWeBo_Test_Start		= 0x000013090;
	public static final int Multiple_App_SinaWeBo_Action_Start		= 0x000013091;
	public static final int Multiple_App_SinaWeBo_Action_Success	= 0x000013092;
	public static final int Multiple_App_SinaWeBo_Action_Failure	= 0x000013093;
	public static final int Multiple_App_SinaWeBo_Test_END			= 0x000013094;

    //Facebook
    public static final int Multiple_App_Facebook_Test_Start		= 0x000013E0;
    public static final int Multiple_App_Facebook_Action_Start		= 0x000013E1;
    public static final int Multiple_App_Facebook_Action_Success	= 0x000013E2;
    public static final int Multiple_App_Facebook_Action_Failed	    = 0x000013E3;
    public static final int Multiple_App_Facebook_Test_END			= 0x000013E4;

    //Instagram
    public static final int Multiple_App_Instagram_Test_Start		= 0x000013F0;
    public static final int Multiple_App_Instagram_Action_Start		= 0x000013F1;
    public static final int Multiple_App_Instagram_Action_Success	= 0x000013F2;
    public static final int Multiple_App_Instagram_Action_Failed	= 0x000013F3;
    public static final int Multiple_App_Instagram_Test_END			= 0x000013F4;

	//WangYiNew
	public static final int Multiple_WangYiNew_Test_Start			= 0x000014001;
	public static final int Multiple_WangYiNew_Action_Start			= 0x000014002;
	public static final int Multiple_WangYiNew_Action_Success		= 0x000014003;
	public static final int Multiple_WangYiNew_Action_Failure		= 0x000014004;
	public static final int Multiple_WangYiNew_Test_END				= 0x000014005;

	//iQiYi
	public static final int Multiple_iQiYi_Test_Start				= 0x000015001;
	public static final int Multiple_iQiYi_Action_Start				= 0x000015002;
	public static final int Multiple_iQiYi_Action_Success			= 0x000015003;
	public static final int Multiple_iQiYi_Action_Failure			= 0x000015004;
	public static final int Multiple_iQiYi_Test_END					= 0x000015005;

	//TencentVideo
	public static final int Multiple_TencentVideo_Test_Start		= 0x000016001;
	public static final int Multiple_TencentVideo_Action_Start		= 0x000016002;
	public static final int Multiple_TencentVideo_Action_Success	= 0x000016003;
	public static final int Multiple_TencentVideo_Action_Failure	= 0x000016004;
	public static final int Multiple_TencentVideo_Test_END			= 0x000016005;

	//YouKu
	public static final int Multiple_YouKu_Test_Start				= 0x000017001;
	public static final int Multiple_YouKu_Action_Start				= 0x000017002;
	public static final int Multiple_YouKu_Action_Success			= 0x000017003;
	public static final int Multiple_YouKu_Action_Failure			= 0x000017004;
	public static final int Multiple_YouKu_Test_END					= 0x000017005;

	//MiGu
	public static final int Multiple_MiGu_Test_Start				= 0x000018001;
	public static final int Multiple_MiGu_Action_Start				= 0x000018002;
	public static final int Multiple_MiGu_Action_Success			= 0x000018003;
	public static final int Multiple_MiGu_Action_Failure			= 0x000018004;
	public static final int Multiple_MiGu_Test_END					= 0x000018005;

	//DouYin
	public static final int Multiple_DouYin_Test_Start				= 0x000019001;
	public static final int Multiple_DouYin_Action_Start			= 0x000019002;
	public static final int Multiple_DouYin_Action_Success			= 0x000019003;
	public static final int Multiple_DouYin_Action_Failure			= 0x000019004;
	public static final int Multiple_DouYin_Test_END				= 0x000019005;

	//Skype
	public static final int Multiple_Skype_Test_Start				= 0x00001360;
	public static final int Multiple_Skype_Action_Start				= 0x00001361;
	public static final int Multiple_Skype_Action_Success			= 0x00001362;
	public static final int Multiple_Skype_Action_Failure			= 0x00001363;
	public static final int Multiple_Skype_Test_Success			    = 0x00001364;
	public static final int Multiple_Skype_Test_Failure				= 0x00001365;

    //WhatsApp
    public static final int WhatsApp_Test_Start				        = 0x000013B0;
    public static final int WhatsApp_Action_Start				    = 0x000013B1;
    public static final int WhatsApp_Action_Success			        = 0x000013B2;
    public static final int WhatsApp_Action_Failed			        = 0x000013B3;
    public static final int WhatsApp_Test_End			            = 0x000013B4;
    public static final int WhatsApp_Test_Success			        = 0x00010304;
    public static final int WhatsApp_Test_Failure			        = 0x00010305;

    //WhatsApp主叫
    public static final int WhatsApp_MO_Dial                        = 0x000013C0;
    public static final int WhatsApp_MO_Attempt                     = 0x000013C1;
    public static final int WhatsApp_MO_Setup                       = 0x000013C2;
    public static final int WhatsApp_MO_Establish                   = 0x000013C3;
    public static final int WhatsApp_MO_Block                       = 0x000013C4;
    public static final int WhatsApp_MO_End                         = 0x000013C5;
    public static final int WhatsApp_MO_Drop                        = 0x000013C6;
    public static final int WhatsApp_MO_Hangup                      = 0x000013C7;

    //WhatsApp被叫
    public static final int WhatsApp_MT_Dial                        = 0x000013C8;
    public static final int WhatsApp_MT_Attempt                     = 0x000013C9;
    public static final int WhatsApp_MT_Setup                       = 0x000013CA;
    public static final int WhatsApp_MT_Establish                   = 0x000013CB;
    public static final int WhatsApp_MT_Block                       = 0x000013CC;
    public static final int WhatsApp_MT_End                         = 0x000013CD;
    public static final int WhatsApp_MT_Drop                        = 0x000013CE;
    public static final int WhatsApp_MT_Hangup                      = 0x000013CF;

	// 自定义的WALKTOUR事件<参考RCU自定义事件存结构，2012.12.19何承忠>
	public static final int WALKTOUR_EVENT								= 0x00110003;
	public static final int EVENT_START_TEST							= 1;
	public static final int EVENT_STOP_TEST								= 2;
	public static final int EVENT_LOG_OUT								= 3;
	public static final int EVENT_REASON_NONE							= 0;
	
	public static final int Route_Start 								= 0x00000290;
	public static final int Route_PointReply 							= 0x00000291;
	public static final int Route_PointTimeOut 							= 0x00000292;
	public static final int Route_Success 								= 0x00000293;
	public static final int Route_Fail 									= 0x00000294;
	public static final int Route_ReslvError 							= 0x00000295;
	
	public static final int MessageLost									= 0x00000196;
	public static final int DeviceIdentity 								= 0x00000197;
	public static final int IndoorTestGpsFlag							= 0x00000218;
	
	
	//Iperf
	public static final int Iperf_Initialized                         	= 0x00000E01;
	public static final int Iperf_Connect_Start                       	= 0x00000E02;
	public static final int Iperf_Connect_Success                    	= 0x00000E03;
	public static final int Iperf_Connect_Failure                     	= 0x00000E04;
	public static final int Iperf_Drop                                	= 0x00000E05;
	public static final int Iperf_Finished                            	= 0x00000E06;
	public static final int Iperf_Quit                                	= 0x00000E07;
	
	//PBM
	public static final int PBM_Start                         		  	= 0x00001060;
	public static final int PBM_Finished                       		  	= 0x00001061;
	public static final int PBM_Failure                     		 	= 0x00001062;

	//UDP
	public static final int UDP_CONNECT_Start                		  	= 0x00001310;
	public static final int UDP_CONNECT_Success               		  	= 0x00001311;
	public static final int UDP_CONNECT_Failure              		 	= 0x00001312;
	public static final int UDP_Start                         		  	= 0x00001313;
	public static final int UDP_End                          		  	= 0x00001314;
	public static final int UDP_Finish                      		  	= 0x00001316;
	public static final int UDP_Disconnect                  		  	= 0x00001318;

	//WeiBo
	public static final int WeiBo_Start 								= 0x00001079;
	public static final int WeiBo_Action_Start 							= 0x00001073;
	public static final int WeiBo_Action_Success 						= 0x00001074;
	public static final int WeiBo_Action_Failure 						= 0x00001075;
	public static final int WeiBo_Finished                       		= 0x0000107A;

	//Reboot
	public static final int Reboot_Start 								= 0x00001179;
	public static final int Reboot_End 				     	     		= 0x00001173;

	/**
	 * 失败原因
	 * @author jone
	 *
	 */
	public static enum FailReason {
		UNKNOWN					(1000, "Unknown"), 
		NODATA_TIMEOUT			(1001, "Nodata Timeout"), 
		USER_STOP				(1002, "User Stop"), 
		CONNECT_TIMEOUT			(1003, "Connect Timeout"), 
		FILE_NOT_EXIST			(1010, "File Not Exist"), 
		FILE_SIZE_ZERO			(1011, "File Size Zero"), 
		FILE_OPEN_FAILED		(1012, "File Open Failed"), 
		NOT_THE_TARGET_PAGE		(1014, "Not the target page"), //(不是目标页面)
		AUTHENTICATION_FAILURE	(1015, "Authentication Failure"),// (认证失败)
		TOKEN_IS_INVALID		(1016, "Token is invalid"),// (令牌无效)
		TOKEN_EXPIRES			(1017, "Token expires"),// (令牌到期)
		FTP_CMD_RESP_FAILED		(1020,"Ftp cmd resp failed"), 
		EMAIL_STREAM_ERROR		(1030,"Email Stream Error"), 
		EMAIL_NOT_EXIST			(1031, "Email Not Exist"), 
		EMAIL_SIZE_ZERO			(1032, "Email Size Zero"), 
		EMAIL_SELECT_ERROR		(1033,"Email Select Error"), 
		HOST_NO_FOUND			(1040, "Host no found"),
		FRIENDS_DOES_NOT_EXIST	(1050, "Friends Does Not Exist"),// (好友不存在)
		PICTURE_UPLOADSIZE_ERROR(1051, "Picture Uploadsize Error"),//(图片上传指定大小小于实际大小)
		NETWORK_NO_MATCH			(1200, "Network no match"),
		
		//以下是FTP的连接失败和登录失败原因码 ,但不是RCU事件的业务失败原因码
		CONNECT_NETWORK_UNAVAILABLE(-1,"Network Unavailable"),//-1:网络不可用(network unavailable)
		CONNECT_HOST_UNRECHABLE	(-2,"Host Unreachable"),//-2:主机不可达(host unreachable)
		CONNECT_CONNECT_TIMEOUT	(-3,"Connection Timeout"),//-3:连接超时(connection timeout)
		CONNECT_REFUSED			(-4,"Connection Refused"),//-4:连接被拒绝(connection refused)
		CONNECT_BREAK			(-5,"Connect Break"),//-5:连接被断开(connection break)
		LOGIN_FAIL_INVALID		(1,"Invalid Username or Password"),//1:invalid username or password 
		LOGIN_FAIL_USER_TIMEOUT	(2,"Username Response Timeout"),//2:user response timeout
		LOGIN_FAIL_PASS_TIMEOUT	(3,"Password Response Timeout"),//3:pass response timeout
		LOGIN_THREAD_LIMIT		(4,"Thread Limit"),//4:thread limit
		OTHER					(-15,"Other");//-15:其他(other)
		
		private final int reason;
		private final String reasonStr;

		private FailReason(int reason, String reasonStr) {
			this.reason = reason;
			this.reasonStr = reasonStr;
		}

		public int getReasonCode() {
			return this.reason;
		}

		public String getResonStr() {
			return this.reasonStr;
		}

		/**
		 * 判断一个码是否属于业务失败原因码
		 * @param code
		 * @return
		 */
		public static boolean isFailReason( int code ){
			FailReason[] resons = values();
			for (int i = 0; i < resons.length; i++) {
				if (resons[i].reason == code) {
					return true;
				}
			}
			return false;
		}
		
		public static FailReason getFailReason(int code) {
			FailReason[] resons = values();
			for (int i = 0; i < resons.length; i++) {
				if (resons[i].reason == code) {
					return resons[i];
				}
			}
			return UNKNOWN;
		}
	}
	
	// DROP的原因码
	public static enum DropReason {
		NORMAL								(0, "Unknown"), 
		TIMEOUT								(1, "Timeout"), 
		RESTART								(2, "Restart"), 
		NORESPONSE							(3, "No Response"), 
		BUFFER_TIMEOUT						(4, "Buffer Timeout"), 						// （缓存等待时间超时）
		EXCEED_THE_PERCENTAGE_OF_BUFFERING	(5,"Exceed the percentage of buffering"), 	// （超出缓存等待百分数）
		EXCEED_THE_NUMBER_OF_BUFFERING		(6, "Exceed the number of buffering"), 		// （超出缓存等待次数）
		USER_STOP							(-101, "User Stop"), 
		PPP_DROP							(-102, "PPP Drop"), 
		NO_DATA								(-103,"No Data"), 
		OutOfService						(-104, "Out Of Service"), 
		DeviceLost							(-105, "Device Lost"), 
		ClientExit							(-106, "Client Exit");

		private final int reasonCode;
		private final String resonStr;

		private DropReason(int code, String str) {
			this.reasonCode = code;
			this.resonStr = str;
		}

		public int getReasonCode() {
			return this.reasonCode;
		}

		public String getResonStr() {
			return this.resonStr;
		}

		public static DropReason getDropReason(int code) {
			DropReason[] resons = values();
			for (int i = 0; i < resons.length; i++) {
				if (resons[i].reasonCode == code) {
					return resons[i];
				}
			}
			return NORMAL;
		}
	}

	/**
	 * Out Of Service时写RCU脱网事件0x00000074时，
	 * 需要写当前任务的类型,下面为定义的脱网任务类型
	 */
	
	public static int getTaskType(TaskType taskType) {
		if (taskType == TaskType.InitiativeCall
				|| taskType == TaskType.PassivityCall) {
			return OutService_TestType_DIAL;
		} else if (taskType == TaskType.FTPDownload) {
			return OutService_TestType_FTPDL;
		} else if (taskType == TaskType.FTPUpload) {
			return OutService_TestType_FTPUL;
		} else if (taskType == TaskType.Ping) {
			return OutService_TestType_PING;
		} else if (taskType == TaskType.Http) {
			return OutService_TestType_HTTP;
		} else if (taskType == TaskType.WapLogin
				|| taskType == TaskType.WapRefurbish) {
			return OutService_TestType_HTTP;
		} else if (taskType == TaskType.WapDownload) {
			return OutService_TestType_WAP_DL;
		} else if (taskType == TaskType.EmailPop3) {
			return OutService_TestType_EMAIL_DL;
		} else if (taskType == TaskType.EmailSmtp) {
			return OutService_TestType_EMAIL_UL;
		} else if (taskType == TaskType.MMSSend) {
			return OutService_TestType_MMS_SEND;
		} else if (taskType == TaskType.MMSIncept) {
			return OutService_TestType_MMS_RECEIVE;
		} else if (taskType == TaskType.PDP) {
			return OutService_TestType_PPP;
		} else if (taskType == TaskType.Attach) {
			return OutService_TestType_ATTACH;
		} else if (taskType == TaskType.Stream) {
			return OutService_TestType_VIDEOSTREAM;
		}else if(taskType == TaskType.SpeedTest){
			return OutService_TestType_SpeedTest;
		}else if(taskType == TaskType.HTTPVS){
			return OutService_TestType_VideoPlay;
		}else if(taskType == TaskType.DNSLookUp){
			return OutService_TestType_DNSLookUp;
		}else if(taskType == TaskType.Facebook){
			return OutService_TestType_FaceBook;
		}else if(taskType == TaskType.VOIP){
			return OutService_TestType_VOIP;
		}else if(taskType == TaskType.TraceRoute){
			return OutService_TestType_TraceRoute;
		}/*else if(taskType == TaskType.Iperf){
			return OutService_TestType_Iperf;
		}*/
		return OutService_TestType_NONE;
	}
	
	/** 根据APN返回flag */
	public static int getNetFlag(APNModel apn) {
		if (apn != null) {
			try {
				if ((apn.getApn() != null && apn.getApn().equalsIgnoreCase(
						"ctwap"))
						|| (apn.getUser() != null && apn.getUser()
								.equalsIgnoreCase("ctwap@mycdma.cn"))) {
					return 1;
				} else if ((apn.getApn() != null && apn.getApn()
						.equalsIgnoreCase("ctnet"))
						|| (apn.getUser() != null && apn.getUser()
								.equalsIgnoreCase("ctnet@mycdma.cn"))) {
					return 2;
				} else if (apn.getApn() != null
						&& apn.getApn().equalsIgnoreCase("cmwap")) {
					return 3;
				} else if (apn.getApn() != null
						&& apn.getApn().equalsIgnoreCase("cmnet")) {
					return 4;
				} else if (apn.getApn() != null
						&& apn.getApn().equalsIgnoreCase("3gwap")) {
					return 5;
				} else if (apn.getApn() != null
						&& apn.getApn().equalsIgnoreCase("3gnet")) {
					return 6;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -9999;
	}
}
