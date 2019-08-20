package com.dinglicom.dataset.model;

/**
* 数据集的所有事件
* -----------------数据集事件---------------------------------------------------------
* 2013.9.11
* 以下所有事件取自CommonDataSetEventDefine.h,
* 以下所有事件定义都必须和CommonDataSetEventDefine.h中的名字一样，不能改名,不能混淆编译
* -----------------------------------------------------------------------------------
* @author qihang.li
*/
public class DataSetEvent {
	public static int NullityRcuValue = -9999; // 无效的Rcu文件结果值
	// 测试数据事件代码
	public static final int ET_FTP_UP_START = 1;
	public static final int ET_FTP_UP_END = 2;
	public static final int ET_FTP_DOWN_START = 3;
	public static final int ET_FTP_DOWN_END = 4;
	public static final int ET_FTP_UP_DROP = 5;
	public static final int ET_FTP_DOWN_DROP = 6;
	public static final int ET_WAP_FAIL = 7;
	public static final int ET_WAP_SUCC = 8;
	public static final int ET_WAP_LINK = 9;
	public static final int ET_DIAL_START = 10;
	public static final int ET_DIAL_END = 11;
	public static final int ET_ATTACH_SUCCESS = 12;
	public static final int ET_ATTACH_FAILURE = 13;
	public static final int ET_PDPACTIVE_SUCCESS = 14;
	public static final int ET_PDPACTIVE_FAILURE = 15;
	// pioneer
	public static final int ET_DIAL_START_P = 56;
	public static final int ET_DIAL_END_P = 57;
	// jerry lee add 2005.4.29
	// 短信事件
	public static final int ET_SMS_SEND = 26;
	public static final int ET_SMS_RECV = 27;
	public static final int ET_SMS_RETURN_RECEIPT = 28;
	public static final int ET_SMS_SEND_PHONE = 36;
	public static final int ET_SMS_RECV_PHONE = 37;
	public static final int ET_SMS_SEND_INDEX = 38;
	public static final int ET_SMS_RECV_INDEX = 39;

	// jerry lee add 2005.7.18
	public static final int ET_MMS_SEND = 29;
	public static final int ET_MMS_PUSH_RECV = 30;
	public static final int ET_MMS_TEXT_EXTRACT = 31;
	public static final int ET_KJAVA_DOWNLOAD = 33;

	// dingfu.chen add 2007.8.7
	public static final int ET_WAPGraph_DOWNLOAD = 35;

	public static final int ET_WAP_PAGE_TEST = 32;

	// EMail事件
	// public static final int ET_EMailDownStart = 22;
	// public static final int ET_EMailDownEnd = 23;
	// public static final int ET_EMailUpStart = 24;
	// public static final int ET_EMailUpEnd = 25;

	// HTTP事件
	// public static final int ET_HTTPStart = 16;
	// public static final int ET_HTTPEnd = 17;

	// public static final int ET_DeviceRestart = 43; // 设备意外重启
	//
	// // Stop Use
	// CIRCURASDial = 47;
	// CIRCURASError = 48;
	// CIRCURASHangup = 49;
	// CIRCUFTPConnect = 50;
	// CIRCUFTPDisconnect = 51;
	// CIRCUPOP3Connect = 52;
	// CIRCUPOP3Disconnect = 53;
	// CIRCUSMTPConnect = 54;
	// CIRCUSMTPDisconnect = 55;
	// //
	// CIRCUDialStart = 56; // 现在不用了，用CIRCUDialStartA
	// CIRCUHangupCall = 57; // 现在不用了，用CIRCUHangupCallA

	// ping icon_event_1
	// public static final int ET_Ping_SUCCESS = 44;
	// public static final int ET_Ping_FAILURE = 45;
	public static final int ET_Device_Lost = 46;

	// DQ Add 2006.04.10 for New version Event
	// CALL icon_event_1
	public static final int ET_Alerting = 0x0061;
	public static final int ET_Connect = 0x0062;
	public static final int ET_ConnectACK = 0x0063;
	public static final int ET_Dialed_Start = 0x0064; // Fleet数据有被叫，Pioneer也增加了，比Fleet多了GUID标识

	// 拨号事件
	public static final int ET_ATDStart = 0x0070;
	public static final int ET_ATDFinished = 0x0071;
	public static final int ET_ATDHangup = 0x0072;
	public static final int ET_ATDDrop = 0x0073;

	public static final int ET_OutOfService = 0x0074;

	public static final int ET_Data_Test_Url = 0x0076; // 2011-12-12 lsq
														// fleet定义的取http,web测试的url

	// FTP DOWNLOAD
	public static final int ET_FTP_DL_SockConnect = 0x0080;
	public static final int ET_FTP_DL_ConnectFinish = 0x0088; // FTP下载控制，登录FTP服务器成功。
	public static final int ET_FTP_DL_SendRetrCmd = 0x0081;
	public static final int ET_FTP_DL_FirstData = 0x0082;
	public static final int ET_FTP_DL_LastData = 0x0083;
	public static final int ET_FTP_DL_Drop = 0x0084;
	public static final int ET_FTP_DL_SockDisconnect = 0x0085;
	// 2011-09-25 liulieshan
	public static final int ET_FTP_DL_SupportReset = 0x0086; // FTP
																// Server支持断点续传功能
	public static final int ET_FTP_DL_ReSendRetrCmd = 0x0087; // FTP下载中断点续传
	public static final int ET_FTP_DL_ConnectSockFailed = 0x0089; // FTP下载控制，登录FTP服务器失败。
	public static final int ET_FTP_DL_LoginSuccess = 0x008A; //
	public static final int ET_FTP_DL_LoginFailure = 0x008B; //
	public static final int ET_FTP_DL_Failure = 0x008C;

	// FTP UPLOAD
	public static final int ET_FTP_UP_SockConnect = 0x0090;
	public static final int ET_FTP_UP_ConnectFinish = 0x0098;
	public static final int ET_FTP_UP_SendRetrCmd = 0x0091;
	public static final int ET_FTP_UP_FirstData = 0x0092;
	public static final int ET_FTP_UP_LastData = 0x0093;
	public static final int ET_FTP_UP_Droped = 0x0094;
	public static final int ET_FTP_UP_SockDisconnect = 0x0095;
	// 2011-09-25 liulieshan
	public static final int ET_FTP_UP_SupportReset = 0x0096; // FTP服务器支持断点续传功能
	public static final int ET_FTP_UP_ReSendStorCmd = 0x0097; // FTP上行测试中，规定的时间内没有流量，进行续传的命令
	public static final int ET_FTP_UP_ConnectSockFailed = 0x0099; // FTP上载控制，
																	// 登录FTP服务器失败。ConnectFinish
	public static final int ET_FTP_UP_LoginSuccess = 0x009A;
	public static final int ET_FTP_UP_LoginFailure = 0x009B;
	public static final int ET_FTP_UP_Failure = 0x009C;

	// HTTP
	public static final int ET_HTTP_SockConnect = 0x00A0;
	public static final int ET_HTTP_SendGetCmd = 0x00A1;
	public static final int ET_HTTP_FirstData = 0x00A2;
	public static final int ET_HTTP_LastData = 0x00A3;
	public static final int ET_HTTP_Drop = 0x00A4;
	public static final int ET_HTTP_SocketDisconnect = 0x00A5;
	// 2011-09-25 liulieshan
	public static final int ET_HTTP_UrlRedirect = 0x00A6;
	public static final int ET_HTTP_ConnectSuccess = 0x00A7;
	public static final int ET_HTTP_ConnectFailure = 0x00A8;
	public static final int ET_HTTP_Failure = 0x00A9;
	public static final int ET_HTTP_GetFileSize = 0x00AA;
	public static final int ET_HTTP_Start = 0x00AB;

	// 3.2.6. Email接收(POP3)/彩E接收事件
	public static final int ET_Email_POP3_Connect 			= 0x00B0;
	public static final int ET_Email_POP3_SendCmd 			= 0x00B1;
	public static final int ET_Email_POP3_FirstData 		= 0x00B2;
	public static final int ET_Email_POP3_LastData 			= 0x00B3;
	public static final int ET_Email_POP3_Drop 				= 0x00B4;
	public static final int ET_Email_POP3_RetrFinished 		= 0x00B5;
	public static final int ET_Email_POP3_Failure 			= 0x00B6;
	public static final int ET_Email_POP3_Start 			= 0x00B7;
	public static final int ET_Email_POP3_SockConnectSuccess= 0x00B8;
	public static final int ET_Email_POP3_SockConnectFailure= 0x00B9;
	public static final int ET_Email_POP3_LoginSuccess 		= 0x00BA;
	public static final int ET_Email_POP3_LoginFailure 		= 0x00BB;
	public static final int ET_Email_POP3_ContentOK			= 0x00BC;

	// 3.2.7. Email发送(SMTP)/彩E发送事件
	public static final int ET_Email_SMTP_Connect 			= 0x00C0;
	public static final int ET_Email_SMTP_SendCmd 			= 0x00C1;
	public static final int ET_Email_SMTP_FirstData 		= 0x00C2;
	public static final int ET_Email_SMTP_LastData 			= 0x00C3;
	public static final int ET_Email_SMTP_Drop 				= 0x00C4;
	public static final int ET_Email_SMTP_EOMACK 			= 0x00C5;
	public static final int ET_Email_SMTP_Failure 			= 0x00C6;
	public static final int ET_Email_SMTP_Start 			= 0x00C7;
	public static final int ET_Email_SMTP_SockConnectSuccess= 0x00C8;
	public static final int ET_Email_SMTP_SockConnectFailure= 0x00C9;
	public static final int ET_Email_SMTP_LoginSuccess 		= 0x00CA;
	public static final int ET_Email_SMTP_LoginFailure 		= 0x00CB;

	// 3.2.8. 短信(SMS)icon_event_1
	public static final int ET_SMS_Access_Request = 0x00D0;
	public static final int ET_SMS_Send_Finished = 0x00D1;
	public static final int ET_SMS_Status_Report = 0x00D2;
	public static final int ET_SMS_Received = 0x00D3;

	// 3.2.9. 彩信（MMS）icon_event_1
	public static final int ET_MMS_Send_SockConnect = 0x00EA;
	public static final int ET_MMS_WSPConnect_Request = 0x00E0;
	public static final int ET_MMS_WSPConnect_Finished = 0x00E1;
	public static final int ET_MMS_MSend_Config = 0x00E2;
	public static final int ET_MMS_MNotification_Ind = 0x00E4;
	public static final int ET_MMS_MNotification_Timeout = 0x00E7;

	public static final int ET_MMS_Recv_SockConnect = 0x00EB;
	public static final int ET_MMS_Recv_WSPConnect_Request = 0x00E5;
	public static final int ET_MMS_Recv_WSPConnect_Finished = 0x00E6;
	public static final int ET_MMS_MNotifyResp_Ind = 0x00E8;
	public static final int ET_MMS_Send_Start = 0x00E9;
	public static final int ET_MMS_Recv_FirstData = 0x00EC;

	// 3.2.10. WAP Page事件
	public static final int ET_WAP_SockConnect = 0x00F0;
	public static final int ET_WAP_ConnectGateway_Request = 0x00F1;
	public static final int ET_WAP_ConnectGateway_Finished = 0x00F2;
	public static final int ET_WAP_FirstData = 0x00F3; // 未使用
	public static final int ET_WAP_LastData = 0x00F4;
	public static final int ET_WAP_Drop = 0x00F5;
	public static final int ET_WAP_GetRequest = 0x00F6;
	public static final int ET_WAP_FirstDataEx = 0x00F7;
	public static final int ET_WAP_UrlRedirect = 0x00F8;
	// 2011-09-25 liulieshan
	public static final int ET_WAP_GetURLFinished = 0x00FA; // xiao091228
	public static final int ET_WAP_Start = 0x00FB;

	// 3.2.11. WAP Download事件
	public static final int ET_WAPDL_SockConnect = 0x0100;
	public static final int ET_WAPDL_ConnectGateway_Request = 0x0101;
	public static final int ET_WAPDL_ConnectGateway_Finished = 0x0102;
	public static final int ET_WAPDL_FirstData = 0x0103;
	public static final int ET_WAPDL_LastData = 0x0104;
	public static final int ET_WAPDL_Drop = 0x0105;
	public static final int ET_WAPDL_GetRequest = 0x0106;
	public static final int ET_WAPDL_FirstDataEx = 0x0107;
	// 2011-09-25 liulieshan
	public static final int ET_WAPDL_UrlRedirect = 0x0108; // WAP
															// Download发生网址跳转时。
	public static final int ET_WAPDL_GetURLFinished = 0x0109; // xiao091228
	public static final int ET_WAPDL_Start = 0x010A;

	// QQ
	// 主叫
	public static final int ET_QQCallerLoginStart = 0x0110;
	public static final int ET_QQCallerLoginSuccess = 0x0111;
	public static final int ET_QQCallerLoginFailed = 0x0112;

	public static final int ET_QQCallerSendMsg = 0x0113;
	public static final int ET_QQCallerRcvReplySuccess = 0x0114;
	public static final int ET_QQCallerRcvReplyFailed = 0x0115;

	public static final int ET_QQCallerDrop = 0x0116;

	public static final int ET_QQCallerLogoutStart = 0x0117;
	public static final int ET_QQCallerLogoutSuccess = 0x0118;
	public static final int ET_QQCallerLogoutFailed = 0x0119;

	// 被叫
	public static final int ET_QQCalledLoginStart = 0x011A;
	public static final int ET_QQCalledLoginSuccess = 0x011B;
	public static final int ET_QQCalledLoginFailed = 0x011C;

	public static final int ET_QQCalledRcvMsgSuccess = 0x011D;
	public static final int ET_QQCalledSendReply = 0x011E;

	public static final int ET_QQCalledDrop = 0x011F;

	public static final int ET_QQCalledLogoutStart = 0x0120;
	public static final int ET_QQCalledLogoutSuccess = 0x0121;
	public static final int ET_QQCalledLogoutFailed = 0x0122;

	public static final int ET_Attach_start = 0x0130;
	public static final int ET_PDP_start = 0x0140;
	// public static final int ET_PDP_Request = 0x0141;
	// public static final int ET_PDP_Complete = 0x0142;
	// public static final int ET_PDP_Accept = 0x0143;
	// public static final int ET_PDP_Reject = 0x0144;
	// 语音评估事件
	public static final int ET_PESQ_score = 0x0150;
	public static final int ET_RCURecordBeforeCall = 0x0151; // cyh090821
	public static final int ET_RCUMOSSingleMute = 0x0152; // cyh090821
	public static final int ET_RCUMOSWhistle = 0x0153;
	public static final int ET_POLQA_Result = 0x0154;

	// 强制事件记录
	public static final int ET_Force_Handover = 0x0160;
	public static final int ET_Forbid_Handover = 0x0161;
	public static final int ET_Force_Cell_Reselection = 0x0162;
	public static final int ET_Forbid_Cell_Reselection = 0x0163;
	public static final int ET_Force_Lock_Cell = 0x0164;
	public static final int ET_Force_Lock_Frequency = 0x0165;
	public static final int ET_Clear_All_Force = 0x0166;

	// 3.2.18. VideoStream事件
	public static final int ET_Stream_Request = 0x0170;
	public static final int ET_Stream_first_data_packet = 0x0171;
	public static final int ET_Stream_reproduction_starts = 0x0172;
	public static final int ET_Stream_End = 0x0173;
	public static final int ET_Stream_Drop = 0x0174;
	public static final int ET_Stream_reproduction_end = 0x0175;
	// 2011-09-25 liulieshan
	public static final int ET_Stream_Request_Success = 0x0176; // 请求流媒体成功
	public static final int ET_Stream_Request_Failed = 0x0177; // 请求流媒体失败
	public static final int ET_Stream_Receive_Overtime = 0x0178; //

	// 3.2.19. Video Telephony事件
	public static final int ET_Video_Telephony_Start = 0x0180;
	public static final int ET_Start_audio_data = 0x0181;
	public static final int ET_Start_video_data = 0x0182;
	public static final int ET_Video_Telephony_End = 0x0183;
	public static final int ET_Video_Telephony_Drop = 0x0184;
	public static final int ET_Video_Setup_TimeOut = 0x0185;
	public static final int ET_Video_H245_Setup = 0x0186;
	public static final int ET_Video_H245_Timeout = 0x0187;
	public static final int ET_Video_MOC_Start = 0x0188;
	public static final int ET_Video_MOC_Hangup = 0x0189;
	public static final int ET_Video_MTC_Start = 0x018A;

	public static final int ET_AVET_Finish_Tag = 0x018B; // cyh091022
	public static final int ET_PEVQ_Finish_Tag = 0x018C; // xiao100204
															// //cyh100302
	public static final int ET_AVET_Value = 0x018D; // cyh100303,
													// 保存PEVQ，AVET的实时运算的结果
	public static final int ET_PEVQ_Value = 0x018E; // cyh100303,
													// 保存PEVQ，AVET的实时运算的结果
	// 2011-09-25 liulieshan
	public static final int ET_Video_Telephony_Interrupt = 0x018F;

	// New version ping Event
	public static final int ET_Ping_Start = 0x0190;
	public static final int ET_Tag = 0x0191;
	public static final int ET_Ping_NSuccess = 0x0192;
	public static final int ET_Ping_NFail = 0x0193;
	// 2011-09-25 liulieshan
	public static final int ET_TagText = 0x0194; // 文本标记事件
	public static final int ET_TestError = 0x0195; // 测试错误事件
	public static final int ET_DeviceSamplePointLost = 0x0196; // 前端产品测试时，出现了一段时间内设备端口没有采样点的情况
	public static final int ET_DeviceIdentity = 0x0197; // 记录IMEI等信息的事件
	public static final int ET_ForceEvent = 0x01A7; // 强制事件

	// LBS
	public static final int ET_LBS_Socket_Connecting = 0x01B0;
	public static final int ET_LBS_Connect_GateWay_Request = 0x01B1;
	public static final int ET_LBS_Connect_GateWay_Finished = 0x01B2;
	public static final int ET_LBS_Send_Request = 0x01B3;
	public static final int ET_LBS_Receive_Response = 0x01B4;
	public static final int ET_LBS_Send_Request_Error = 0x01B5;
	public static final int ET_LBS_Receive_Response_Error = 0x01B6;
	public static final int ET_LBS_Receive_Paging = 0x01B7;

	// [2008-9-1]zpl: 彩E事件
	public static final int ET_CE_Send_ConnectRequest = 0x01E0;
	public static final int ET_CE_Send_ConnectSuccess = 0x01E1;
	public static final int ET_CE_Send_AuthSuccess = 0x01E2;
	public static final int ET_CE_Send_Success = 0x01E3;
	public static final int ET_CE_Send_LogoutSuccess = 0x01E4;
	public static final int ET_CE_Push_Received = 0x01E5;
	public static final int ET_CE_Receive_ConnectRequest = 0x01E6;
	public static final int ET_CE_Receive_ConnectSuccess = 0x01E7;
	public static final int ET_CE_Receive_AuthSuccess = 0x01E8;
	public static final int ET_CE_Receive_ContentDetail = 0x01E9;
	public static final int ET_CE_Receive_Success = 0x01EA;
	public static final int ET_CE_Receive_LogoutSuccess = 0x01EB;
	public static final int ET_CE_U_IMAP_Push_TimeOut = 0x01EC;

	// 2011-09-26 liulieshan
	public static final int ET_23G_TestStart = 0x01F0; // 标记起始位
	public static final int ET_23G_TestArriveArea = 0x01F1; // 到达折返点
	public static final int ET_23G_TestStop = 0x01F2; // 标记终点位

	// 2011-10-12 liulieshan
	public static final int ET_PushMail_SendConnStart = 0x0200;
	public static final int ET_PushMail_SendConnFinish = 0x0201;
	public static final int ET_PushMail_SendAuthFinish = 0x0202;
	public static final int ET_PushMail_SendFinish = 0x0203;
	public static final int ET_PushMail_SendLogoutFinish = 0x0204;
	public static final int ET_PushMail_PushReceived = 0x0205;
	public static final int ET_PushMail_PushFail = 0x020C;

	public static final int ET_Stop_Logging = 0x0210;
	public static final int ET_Logging_Pause = 0x0211;
	public static final int ET_Stop_Test = 0x0212;
	public static final int ET_Mos_ValueLow = 0x0213;
	// 2011-09-26 liulieshan
	public static final int ET_GPS_Lost = 0x0214;

	// [2009-11-10]zpl: RCU增加模块重启事件
	public static final int ET_RCU_ModuleRestart = 0x0215;// [2009-11-10]zpl:
															// RCU模块重启或关闭
	public static final int ET_RCU_Restart = 0x0216;// [2009-11-10]zpl: RCU重启或关机
	public static final int ET_RCU_InitializeComplete = 0x0217;// [2009-11-10]zpl:
																// 模块初始化完成
	public static final int ET_BuildingGPSInfo = 0x0218;// 获取建筑物GPS信息

	// [2009-12-12]zpl: 增加飞信事件
	public static final int ET_Fetion_LoginStart = 0x0230;
	public static final int ET_Fetion_LoginSuccess = 0x0231;
	public static final int ET_Fetion_LoginFailed = 0x0232;
	public static final int ET_Fetion_LogoutStart = 0x0233;
	public static final int ET_Fetion_LogoutSuccess = 0x0234;
	public static final int ET_Fetion_LogoutFailed = 0x0235;
	public static final int ET_Fetion_QueryStatusStart = 0x0236;
	public static final int ET_Fetion_QueryStatusSuccess = 0x0237;
	public static final int ET_Fetion_QueryStatusFailed = 0x0238;
	public static final int ET_Fetion_CallStart = 0x0239;
	public static final int ET_Fetion_CallSuccess = 0x023A;
	public static final int ET_Fetion_CallFailed = 0x023B;
	public static final int ET_Fetion_SendStart = 0x023C;
	public static final int ET_Fetion_SendSuccess = 0x023D;
	public static final int ET_Fetion_SendFailed = 0x023E;
	public static final int ET_Fetion_RecvSuccess = 0x023F;
	public static final int ET_Fetion_RecvFailed = 0x0240;
	public static final int ET_Fetion_Offline = 0x0241;

	// 并发FTP
	// 多路下载
	public static final int ET_MultiFTPDlConnectStart = 0x0250;
	public static final int ET_MultiFTPDlConnectSuccess = 0x0251;
	public static final int ET_MultiFTPDlSupportReset = 0x0252;
	public static final int ET_MultiFTPDlErrorHappened = 0x0253;
	public static final int ET_MultiFTPDlAllSendStorCmd = 0x0254;
	public static final int ET_MultiFTPDlAllFirstData = 0x0255;
	public static final int ET_MultiFTPDlCountDownStart = 0x0256;
	public static final int ET_MultiFTPDlReSendStorCmd = 0x0257;
	public static final int ET_MultiFTPDlDrop = 0x0258;
	public static final int ET_MultiFTPDlLastData = 0x0259;
	public static final int ET_MultiFTPDlDisconnect = 0x025A;
	public static final int ET_MultiFTPDlLoginSuccess = 0x025B;
	public static final int ET_MultiFTPDlLoginFailure = 0x025C;
	public static final int ET_MultiFTPDlFailure = 0x025D;

	// 多路上传
	public static final int ET_MultiFTPUlConnectStart = 0x0260;
	public static final int ET_MultiFTPUlConnectSuccess = 0x0261;
	public static final int ET_MultiFTPUlSupportReset = 0x0262;
	public static final int ET_MultiFTPUlErrorHappened = 0x0263;
	public static final int ET_MultiFTPUlAllSendStorCmd = 0x0264;
	public static final int ET_MultiFTPUlAllFirstData = 0x0265;
	public static final int ET_MultiFTPUlCountDownStart = 0x0266;
	public static final int ET_MultiFTPUlReSendStorCmd = 0x0267;
	public static final int ET_MultiFTPUlDrop = 0x0268;
	public static final int ET_MultiFTPUlLastData = 0x0269;
	public static final int ET_MultiFTPUlDisconnect = 0x026A;
	public static final int ET_MultiFTPUlLoginSuccess = 0x026B;
	public static final int ET_MultiFTPUlLoginFailure = 0x026C;
	public static final int ET_MultiFTPUlFailure = 0x026D;

	// CMMB 测试事件
	public static final int ET_CMMB_ScanChannelStart = 0x0270;
	public static final int ET_CMMB_ScanChannelFinished = 0x0271;
	public static final int ET_CMMB_ConnectChannelStart = 0x0272;
	public static final int ET_CMMB_ConnectChannelSuccess = 0x0273;
	public static final int ET_CMMB_ConnectChannelFailed = 0x0274;
	public static final int ET_CMMB_ChannelDrop = 0x0275;
	public static final int ET_CMMB_ChannelFinished = 0x0276;
	public static final int ET_CMMB_SwitchChannel = 0x0277;
	public static final int ET_CMMB_DumpStart = 0x0278;
	public static final int ET_CMMB_DumpFinished = 0x0279;

	// WiFi 权限验证
	public static final int ET_WiFi_LoginStart = 0x0280;
	public static final int ET_WiFi_LoginSuccess = 0x0281;
	public static final int ET_WiFi_LoginFailed = 0x0282;
	public static final int ET_WiFi_LogoutStart = 0x0283;
	public static final int ET_WiFi_LogoutSuccess = 0x0284;
	public static final int ET_WiFi_LogoutFailed = 0x0285;
	// 2011-09-25 liulieshan
	public static final int ET_WiFi_AuthMainPageStart = 0x0286;
	public static final int ET_WiFi_AuthMainPageFirstData = 0x0287;
	public static final int ET_WiFi_AuthMainPageSuccess = 0x0288;
	public static final int ET_WiFi_AuthMainPageFailed = 0x0289;

//	public static final int ET_WiFi_AP_Added = 0x050D;
//	public static final int ET_WiFi_AP_Connected = 0x050E;
//	public static final int ET_WiFi_AP_Scan_Info = 0x050F;
	
	// 2011-09-25 liulieshan
	public static final int ET_TraceRoute_Start = 0x0290;
	public static final int ET_TraceRoute_PointReply = 0x0291;
	public static final int ET_TraceRoute_PointTimeOut = 0x0292;
	public static final int ET_TraceRoute_Success = 0x0293;
	public static final int ET_TraceRoute_Fail = 0x0294;
	public static final int ET_TraceRoute_ReslvError = 0x0295;

//	public static final int ET_WiFi_Start = 0x019C;
//	public static final int ET_WiFi_End = 0x019D;
	
	// WiMax 测试事件
	public static final int ET_WiFi_DHCPStart = 0x0300;
	public static final int ET_WiFi_DHCPSuccess = 0x0301;
	public static final int ET_WiFi_DHCPFail = 0x0302;
	public static final int ET_WiFi_DHCPReleaseStart = 0x0303; // 2012-03-16
																// liulieshan
	public static final int ET_WiFi_DHCPReleaseSuccess = 0x0304;

	// 老的ping
	public static final int ET_PING = 0x03E9; // ET_USER + 1 = 1001;

	public static final int ET_WiMax_SearchStart = 0x0401;
	public static final int ET_WiMax_SearchDone = 0x0402;
	public static final int ET_WiMax_ConnectStart = 0x0403;
	public static final int ET_WiMax_ConnectSuccess = 0x0404;
	public static final int ET_WiMax_ConnectFailed = 0x0405;
	public static final int ET_WiMax_Disconnected = 0x0406;
	public static final int ET_WiMax_NetworkDropped = 0x0407;
	public static final int ET_WiMax_GainIpAddress = 0x0408;

	public static final int ET_WiFi_SearchStart = 0x0501;
	public static final int ET_WiFi_SearchDone = 0x0502;
	public static final int ET_WiFi_ConnectStart = 0x0503;
	public static final int ET_WiFi_ConnectSuccess = 0x0504;
	public static final int ET_WiFi_ConnectFailed = 0x0505;
	public static final int ET_WiFi_Disconnected = 0x0506;
	public static final int ET_WiFi_Switch = 0x0507;
	public static final int ET_WiFi_Disconnect = 0x0508;
	public static final int ET_WiFi_Roam = 0x0509;

	// LTE
	public static final int ET_TDDLTEConnectStart = 0x0601; // 开始连接基站
	public static final int ET_TDDLTEConnectSuccess = 0x0602; // 连接基站成功
	public static final int ET_TDDLTEConnectFailed = 0x0603; // 连接基站失败
	public static final int ET_TDDLTEDisconnected = 0x0604; // 与基站连接断开
	public static final int ET_TDDLTENetworkDropped = 0x0605; // LTE脱网事件
	public static final int ET_TDDLTEAttachStart = 0x0606;
	public static final int ET_TDDLTEAttachSuccess = 0x0607;
	public static final int ET_TDDLTEAttachFailed = 0x0608;
	public static final int ET_TDDLTEDetachStart = 0x0609;
	public static final int ET_TDDLTEDetachSuccess = 0x0610;
	public static final int ET_TDDLTEDetachFailed = 0x0611;

	// public static final int ET_TDDLTEPowerRequest = 0x061A;
	public static final int ET_TDDLTEPowerSuccess = 0x0612;
	// public static final int ET_TDDLTEPowerFailure = 0x061B;
	public static final int ET_TDDLTEPowerOff = 0x0613;

	public static final int ET_TDDLTEDelayTestStart = 0x0614;
	public static final int ET_TDDLTEDelayTestSuccess = 0x0615;
	public static final int ET_TDDLTEDelayTestFailed = 0x0616;

	public static final int ET_TDDLTEDisconnectStart = 0x0617; // 开始连接基站
	public static final int ET_TDDLTEDisconnectSuccess = 0x0618; // 连接基站成功
	public static final int ET_TDDLTEDisconnectFailed = 0x0619; // 连接基站失败

	public static final int ET_TDDLTEPowerRequest = 0x061A;
	public static final int ET_TDDLTEPowerFailure = 0x061B;

	// gpsOne事件
	public static final int ET_gpsOne_Request = 0x0701;
	public static final int ET_gpsOne_Response = 0x0702;
	public static final int ET_gpsOne_Success = 0x0703;
	public static final int ET_gpsOne_Failure = 0x0704;
	public static final int ET_gpsOne_DeviationOutOfThreshold = 0x0705;
	public static final int ET_gpsOne_DelayOutOfThreshold = 0x0706;
	// 2012-02-06 liulieshan
	public static final int ET_gpsOne_PassiveRequest = 0x0707;
	public static final int ET_gpsOne_PassiveSuccess = 0x0708;
	public static final int ET_gpsOne_PassiveFailure = 0x0709;
	public static final int ET_gpsOne_PassiveDeviationOutOfThreshold = 0x070A;
	public static final int ET_gpsOne_PassiveDelayOutOfThreshold = 0x070B;

	public static final int ET_IPhoneMOAttempt = 0x0801;
	public static final int ET_IPhoneMOEstablished = 0x0802;
	public static final int ET_IPhoneMOBlock = 0x0803;
	public static final int ET_IPhoneMODrop = 0x0804;
	public static final int ET_IPhoneMOCallEnd = 0x0805;

	public static final int ET_IPhoneMTAttempt = 0x0806;
	public static final int ET_IPhoneMTEstablished = 0x0807;
	public static final int ET_IPhoneMTBlock = 0x0808;
	public static final int ET_IPhoneMTDrop = 0x0809;
	public static final int ET_IPhoneMTCallEnd = 0x0810;

	public static final int ET_IPhoneMOSetup = 0x080A; // 主叫振铃
	public static final int ET_IPhoneMTSetup = 0x080B; // 被叫振铃

	public static final int ET_IPhoneMOStart = 0x080C;
	public static final int ET_IPhoneMTStart = 0x080D;

	// QChat
	public static final int ET_PTTStart = 0x0901;
	public static final int ET_PTTEnd = 0x0902;
	public static final int ET_PTTInitialAttempt = 0x0903;
	public static final int ET_PTTInitialSuccess = 0x0904;
	public static final int ET_PTTInitialFailure = 0x0905;
	public static final int ET_PTTInCallAttempt = 0x0906;
	public static final int ET_PTTInCallSuccess = 0x0907;
	public static final int ET_PTTInCallFailure = 0x0908;
	public static final int ET_PTTTerminationSuccess = 0x0909;
	public static final int ET_PTTTerminationFailure = 0x090A;
	public static final int ET_PTTMediaDelay = 0x090B;

	public static final int ET_PTTStarted = 0x090C;
	public static final int ET_PTTEnded = 0x090D;
	// 2011-09-26 liulieshan
	public static final int ET_PTTRegStart = 0x090E;
	public static final int ET_PTTRegSuccess = 0x090F;
	public static final int ET_PTTRegFailed = 0x0910;
	public static final int ET_PTTUnRegStart = 0x0911;
	public static final int ET_PTTUnRegSuccess = 0x0912;
	public static final int ET_PTTUnRegFailed = 0x0913;

	// 2011-09-26 liulieshan
	// Add_WebDisconnect
	public static final int ET_WebDisconnectStart = 0x0A01;
	public static final int ET_WebDisconnectEnd = 0x0A02;
	public static final int ET_WebDisconnectRefreshStart = 0x0A03;
	public static final int ET_WebDisconnectRefreshFinish = 0x0A04;
	public static final int ET_WebDisconnectRefreshDrop = 0x0A05;

	// 2011-09-26 liulieshan
	// Add_WLanAPCoverage
	public static final int ET_WiFi_APCoverageSamplingStart = 0x0B01;
	public static final int ET_WiFi_APCoverageSamplingData = 0x0B02;

	// SpeedTest
	public static final int ET_SpeedTest_SockConnecting = 0x0C20; // Speedtest
																	// Socket开始建立的时刻
	public static final int ET_SpeedTest_ConnectSockSuccess = 0x0C21; // Speedtest连接speedtest服务器成功
	public static final int ET_SpeedTest_ConnectSockFail = 0x0C22; // 连接speedtest服务器失败
	public static final int ET_SpeedTest_Ping_Start = 0x0C23; // 开始ping
																// speedtest服务器
	public static final int ET_SpeedTest_Ping_Success = 0x0C24; // ping
																// speedtest服务器成功
	// public static final int ET_SpeedTest_Ping_Fail = 0x0C25; //连接ping
	// speedtest服务器失败
	public static final int ET_SpeedTest_Download_Start = 0x0C26; // 开始download
																	// speedtest服务器
	public static final int ET_SpeedTest_Download_Success = 0x0C27; // download
																	// speedtest服务器成功
	// public static final int ET_SpeedTest_Download_Fail = 0x0C28; //连接download
	// speedtest服务器失败
	// public static final int ET_SpeedTest_Download_Drop = 0x0C29; //download
	// speedtest服务器掉线
	public static final int ET_SpeedTest_Upload_Start = 0x0C2A; // 开始upload
																// speedtest服务器
	public static final int ET_SpeedTest_Upload_Success = 0x0C2B; // upload
																	// speedtest服务器成功
	// public static final int ET_SpeedTest_Upload_Fail = 0x0C2C; //连接upload
	// speedtest服务器失败
	// public static final int ET_SpeedTest_Upload_Drop = 0x0C2D; //upload
	// speedtest服务器掉线
	public static final int ET_SpeedTest_Finish = 0x0C2E; // speedtest测试成功
	public static final int ET_SpeedTest_Fail = 0x0C2F; // speedtest测试失败（连接speedtest、ping、download、upload服务器失败）
	// public static final int ET_SpeedTest_Drop = 0x0C30;
	// //download、upload服务器过程中drop导致speedtest测试drop
	public static final int ET_SpeedTest_SocketDisconnected = 0x0C31; // Socket断开，表示一次speedtest完成

	// MicroBlog
	// 主叫
	public static final int ET_MicroBlogCallerTestStart = 0x0D01;

	public static final int ET_MicroBlogCallerLoginStart = 0x0D02;
	public static final int ET_MicroBlogCallerLoginSuccess = 0x0D03;
	public static final int ET_MicroBlogCallerLoginFailed = 0x0D04;

	public static final int ET_MicroBlogCallerSendMsgStart = 0x0D05;
	public static final int ET_MicroBlogCallerSendMsgSuccess = 0x0D06;
	public static final int ET_MicroBlogCallerSendMsgFailed = 0x0D07;
	public static final int ET_MicroBlogCallerRcvCommentSuccess = 0x0D08;
	public static final int ET_MicroBlogCallerRcvCommentFailed = 0x0D09;

	public static final int ET_MicroBlogCallerLogoutStart = 0x0D0A;
	public static final int ET_MicroBlogCallerLogoutSuccess = 0x0D0B;
	public static final int ET_MicroBlogCallerLogoutFailed = 0x0D0C;

	public static final int ET_MicroBlogCallerDrop = 0x0D0D;
	public static final int ET_MicroBlogCallerFinish = 0x0D0E;

	// 被叫
	public static final int ET_MicroBlogCalledTestStart = 0x0D0F;

	public static final int ET_MicroBlogCalledLoginStart = 0x0D10;
	public static final int ET_MicroBlogCalledLoginSuccess = 0x0D11;
	public static final int ET_MicroBlogCalledLoginFailed = 0x0D12;

	public static final int ET_MicroBlogCalledRcvMsgSuccess = 0x0D13;
	public static final int ET_MicroBlogCalledRcvMsgFailed = 0x0D14;

	public static final int ET_MicroBlogCalledRepostMsgStart = 0x0D15;
	public static final int ET_MicroBlogCalledRepostMsgSuccess = 0x0D16;
	public static final int ET_MicroBlogCalledRepostMsgFailed = 0x0D17;

	public static final int ET_MicroBlogCalledSendCommentStart = 0x0D18;
	public static final int ET_MicroBlogCalledSendCommentSuccess = 0x0D19;
	public static final int ET_MicroBlogCalledSendCommentFailed = 0x0D1A;

	public static final int ET_MicroBlogCalledLogoutStart = 0x0D1B;
	public static final int ET_MicroBlogCalledLogoutSuccess = 0x0D1C;
	public static final int ET_MicroBlogCalledLogoutFailed = 0x0D1D;

	public static final int ET_MicroBlogCalledDrop = 0x0D1E;
	public static final int ET_MicroBlogCalledFinish = 0x0D1F;

	// 2011-09-26 liulieshan
	// Iperf
	public static final int ET_IperfInited = 0x0E01; // Iperf初始化
	public static final int ET_IperfConnectStart = 0x0E02; // Iperf连接开始
	public static final int ET_IperfConnectSuccess = 0x0E03; // Iperf连接成功
	public static final int ET_IperfConnectFailed = 0x0E04; // Iperf连接失败
	public static final int ET_IperfDropped = 0x0E05; // Iperf业务中断
	public static final int ET_IperfFinished = 0x0E06; // Iperf业务完成
	public static final int ET_IperfQuit = 0x0E07; // Iperf退出

	// HTTP Page
	public static final int ET_HttpPageSocketConnecting = 0x0F01;
	public static final int ET_HttpPageSocketFailed = 0x0F08;
	public static final int ET_HttpPageSendGetCmd = 0x0F02;
	public static final int ET_HttpPageFirstData = 0x0F03;
	public static final int ET_HttpPageLastData = 0x0F04;
	public static final int ET_HttpPageDrop = 0x0F05;
	public static final int ET_HttpPageSocketDisconnect = 0x0F06;
	public static final int ET_HttpPageUrlRedirect = 0x0F07;
	// 2011-09-26 liulieshan
	public static final int ET_HttpPageSendGetCmdFailer = 0x0F09;
	public static final int ET_HttpPageSocketSuccess = 0x0F0A;
	public static final int ET_HttpPageFailure = 0x0F0B;
	public static final int ET_HttpPageStart = 0x0F0C;
	public static final int ET_HttpPageMainPageOK = 0x0F0D;

	// Voip
	public static final int ET_Voip_OutgoingStart = 0x0F10;
	public static final int ET_Voip_IncomingStart = 0x0F11;
	public static final int ET_Voip_OutgoingLoginStart = 0x0F12;
	public static final int ET_Voip_IncomingLoginStart = 0x0F13;
	public static final int ET_Voip_OutgoingLoginSuccess = 0x0F14;
	public static final int ET_Voip_IncomingLoginSuccess = 0x0F15;
	public static final int ET_Voip_OutgoingLoginFailure = 0x0F16;
	public static final int ET_Voip_IncomingLoginFailure = 0x0F17;
	public static final int ET_Voip_OutgoingCallRequest = 0x0F18;
	public static final int ET_Voip_IncomingCallRequest = 0x0F19;
	public static final int ET_Voip_OutgoingCallRinging = 0x0F1A;
	public static final int ET_Voip_IncomingCallRinging = 0x0F1B;
	public static final int ET_Voip_OutgoingCallSuccess = 0x0F1C;
	public static final int ET_Voip_IncomingCallSuccess = 0x0F1D;
	public static final int ET_Voip_OutgoingCallFailure = 0x0F1E;
	public static final int ET_Voip_IncomingCallFailure = 0x0F1F;
	public static final int ET_Voip_ReceiveAudioFirstPackage = 0x0F20;
	public static final int ET_Voip_ReceiveVideoFirstPackage = 0x0F21;
	public static final int ET_Voip_SendAudioStart = 0x0F22;
	public static final int ET_Voip_SendVideoStart = 0x0F23;
	public static final int ET_Voip_Finish = 0x0F24;
	public static final int ET_Voip_Drop = 0x0F25;

	// Http Upload
	public static final int ET_HttpUpload_Start = 0x1000;
	public static final int ET_HttpUpload_LoginStart = 0x1001;
	public static final int ET_HttpUpload_LoginSuccess = 0x1002;
	public static final int ET_HttpUpload_LoginFailed = 0x1003;
	public static final int ET_HttpUpload_SendStart = 0x1004;
	public static final int ET_HttpUpload_FirstData = 0x1005;
	public static final int ET_HttpUpload_Drop = 0x1006;
	public static final int ET_HttpUpload_LastData = 0x1007;
	public static final int ET_HttpUpload_Failure = 0x1008;

	// Http Video测试事件
	public static final int ET_HttpVideo_Request = 0x1010;
	public static final int ET_HttpVideo_FirstData = 0x1011;
	public static final int ET_HttpVideo_RequestFailure = 0x1012;
	public static final int ET_HttpVideo_ReproductionStart = 0x1013;
	public static final int ET_HttpVideo_ReproductionStartFailure = 0x1014;
	public static final int ET_HttpVideo_LastData = 0x1015;
	public static final int ET_HttpVideo_Drop = 0x1016;
	public static final int ET_HttpVideo_Finished = 0x1017;
	public static final int ET_HttpVideo_RebufferStart = 0x1018;
	public static final int ET_HttpVideo_RebufferEnd = 0x1019;
	public static final int ET_HttpVideo_ConnectStart = 0x101A;
	public static final int ET_HttpVideo_ConnectSuccess=0x101B;
	public static final int ET_HttpVideo_ConnectFailure =0x101C;
	public static final int ET_HttpVideo_SendGetCommand	=0x101D;
	public static final int ET_HttpVideo_URLParseSuccess =0x101F;
	public static final int ET_HttpVideo_SegmentReport	=0x102E;
	public static final int ET_HttpVideo_VMosReport	=0x102F;

	// DNS Lookup
	public static final int ET_DNSLookup_Start = 0x1020;
	public static final int ET_DNSLookup_Success = 0x1021;
	public static final int ET_DNSLookup_Failure = 0x1022;

	// 新版本VideoStream
	public static final int ET_NewVideoStream_Request = 0x1030;
	public static final int ET_NewVideoStream_FirstData = 0x1031;
	public static final int ET_NewVideoStream_RequestFailure = 0x1032;
	public static final int ET_NewVideoStream_ReproductionStart = 0x1033;
	public static final int ET_NewVideoStream_ReproductionStartFailure = 0x1034;
	public static final int ET_NewVideoStream_LastData = 0x1035;
	public static final int ET_NewVideoStream_Drop = 0x1036;
	public static final int ET_NewVideoStream_Finished = 0x1037;
	public static final int ET_NewVideoStream_RebufferStart = 0x1038;
	public static final int ET_NewVideoStream_RebufferEnd = 0x1039;

	// Facebook测试事件
	public static final int ET_App_Facebook_TestStart = 0x1110;
	public static final int ET_App_Facebook_ActionStart = 0x1111;
	public static final int ET_App_Facebook_ActionSuccess = 0x1112;
	public static final int ET_App_Facebook_ActionFailure = 0x1113;
	public static final int ET_App_Facebook_TestSuccess = 0x1114;
	public static final int ET_App_Facebook_TestFailure = 0x1115;

	// 微信测试事件
	public static final int ET_App_WeChat_TestStart = 0x1100;
	public static final int ET_App_WeChat_ActionStart = 0x1101;
	public static final int ET_App_WeChat_ActionSuccess = 0x1102;
	public static final int ET_App_WeChat_ActionFailure = 0x1103;
	public static final int ET_App_WeChat_TestSuccess = 0x1104;
	public static final int ET_App_WeChat_TestFailure = 0x1105;

	// *********************************扩展事件*************************************
	public static final int ET_Extended_Base = 0x10000;

	// [2010-5-26]zpl: 由 ET_ATDFinished 分解
	public static final int ET_ATD_Success = ET_Extended_Base + 0x1;
	public static final int ET_ATD_Failure = ET_Extended_Base + 0x2;

	// [2010-5-26]zpl: 由 ET_SMS_Send_Finished 分解
	public static final int ET_SMS_Send_Success = ET_Extended_Base + 0x3;
	public static final int ET_SMS_Send_Failure = ET_Extended_Base + 0x4;

	// [2010-5-26]zpl: 由 ET_SMS_Received 分解
	public static final int ET_SMS_Recv_Success = ET_Extended_Base + 0x5;
	public static final int ET_SMS_Recv_Failure = ET_Extended_Base + 0x6;

	public static final int ET_Message_Lost = ET_Extended_Base + 0x10;
	public static final int ET_Voice_Whistle = ET_Extended_Base + 0x11;
	public static final int ET_Voice_Double_Mute = ET_Extended_Base + 0x13;

	// [2010-5-26]zpl: 由ET_MMS_WSPConnect_Finished分解
	public static final int ET_MMS_Send_WSP_Connect_Success = ET_Extended_Base + 0x20;
	public static final int ET_MMS_Send_WSP_Connect_Failure = ET_Extended_Base + 0x21;

	// [2010-5-26]zpl: 由ET_MMS_MSend_Config分解
	public static final int ET_MMS_Send_Success = ET_Extended_Base + 0x22;
	public static final int ET_MMS_Send_Failure = ET_Extended_Base + 0x23;

	// [2010-5-26]zpl: 由ET_MMS_Recv_WSPConnect_Finished分解
	public static final int ET_MMS_Recv_WSP_Connect_Success = ET_Extended_Base + 0x24;
	public static final int ET_MMS_Recv_WSP_Connect_Failure = ET_Extended_Base + 0x25;

	// [2010-5-26]zpl: 由ET_MMS_MNotifyResp_Ind
	public static final int ET_MMS_Recv_Success = ET_Extended_Base + 0x26;
	public static final int ET_MMS_Recv_Failure = ET_Extended_Base + 0x27;

	// [2010-5-26]zpl: 由ET_WAP_ConnectGateway_Finished分解
	public static final int ET_Wap_WSP_Connect_Success = ET_Extended_Base + 0x28;
	public static final int ET_Wap_WSP_Connect_Failure = ET_Extended_Base + 0x29;

	// [2010-5-26]zpl: 由ET_WAPDL_ConnectGateway_Finished
	public static final int ET_WapDown_WSP_Connect_Success = ET_Extended_Base + 0x2A;
	public static final int ET_WapDown_WSP_Connect_Failure = ET_Extended_Base + 0x2B;

	// 2010-08-04 lsq 由ET_CE_Send_ConnectSuccess分解
	public static final int ET_CE_Send_Connect_Success = ET_Extended_Base + 0x2C;
	public static final int ET_CE_Send_Connect_Failure = ET_Extended_Base + 0x2D;

	// 2010-08-04 lsq 由ET_CE_Send_AuthSuccess分解
	public static final int ET_CE_Send_Auth_Success = ET_Extended_Base + 0x2E;
	public static final int ET_CE_Send_Auth_Failure = ET_Extended_Base + 0x2F;

	// 2010-08-04 lsq 由ET_CE_Send_Success分解
	public static final int ET_CE_Send_Success_Success = ET_Extended_Base + 0x30;
	public static final int ET_CE_Send_Success_Failure = ET_Extended_Base + 0x31;

	// 2010-08-04 lsq 由ET_CE_Send_LogoutSuccess分解
	public static final int ET_CE_Send_Logout_Success = ET_Extended_Base + 0x32;
	public static final int ET_CE_Send_Logout_Failure = ET_Extended_Base + 0x33;

	// 2010-08-04 lsq 由ET_CE_Receive_ConnectSuccess分解
	public static final int ET_CE_Receive_Connect_Success = ET_Extended_Base + 0x34;
	public static final int ET_CE_Receive_Connect_Failure = ET_Extended_Base + 0x35;

	// 2010-08-04 lsq 由ET_CE_Receive_AuthSuccess分解
	public static final int ET_CE_Receive_Auth_Success = ET_Extended_Base + 0x36;
	public static final int ET_CE_Receive_Auth_Failure = ET_Extended_Base + 0x37;

	// 2010-08-04 lsq 由ET_CE_Receive_ContentDetail分解
	public static final int ET_CE_Receive_ContentDetail_Success = ET_Extended_Base + 0x38;
	public static final int ET_CE_Receive_ContentDetail_Failure = ET_Extended_Base + 0x39;

	// 2010-08-04 lsq 由ET_CE_Receive_Success分解
	public static final int ET_CE_Receive_Success_Success = ET_Extended_Base + 0x3A;
	public static final int ET_CE_Receive_Success_Failure = ET_Extended_Base + 0x3B;

	// 2010-08-04 lsq 由ET_CE_Receive_LogoutSuccess分解
	public static final int ET_CE_Receive_Logout_Success = ET_Extended_Base + 0x3C;
	public static final int ET_CE_Receive_Logout_Failure = ET_Extended_Base + 0x3D;

	// 由ET_MicroBlogCallerRcvCommentSuccess分解
	public static final int ET_MicroBlogCallerRcvNormalComment = ET_Extended_Base + 0x3E;
	public static final int ET_MicroBlogCallerRcvUnNormalComment = ET_Extended_Base + 0x3F;

	public static final int ET_FMTEvent = ET_Extended_Base + 0x40;

	public static final int ET_Extended_End = ET_FMTEvent;

	// DTLog begin1----------------------------------------------
	public static final int ET_DTLog_Event_Base = ET_Extended_Base + 0x41; // 0x10000
	// PPP
	public static final int ET_DTLog_PPPDialStart = ET_DTLog_Event_Base + 0x00;
	public static final int ET_DTLog_PPPDialSuccess = ET_DTLog_Event_Base + 0x01;
	public static final int ET_DTLog_PPPDialFailure = ET_DTLog_Event_Base + 0x02;
	public static final int ET_DTLog_PPPHangup = ET_DTLog_Event_Base + 0x03;
	// FTP
	public static final int ET_DTLog_FTPServerLogonSuccess = ET_DTLog_Event_Base + 0x04;
	public static final int ET_DTLog_FTPServerLogonFail = ET_DTLog_Event_Base + 0x05; // 当前归类到FTPUp业务(没有归类到FTPDown业务)
	public static final int ET_DTLog_FTPDownAttempt = ET_DTLog_Event_Base + 0x06;
	public static final int ET_DTLog_FTPDownSuccess = ET_DTLog_Event_Base + 0x07;
	public static final int ET_DTLog_FTPDownFailure = ET_DTLog_Event_Base + 0x08; // 相当于rcu事件中的FTPDLFailure
	public static final int ET_DTLog_FTPDownServiceDrop = ET_DTLog_Event_Base + 0x09;
	public static final int ET_DTLog_FTPUpAttempt = ET_DTLog_Event_Base + 0x0A;
	public static final int ET_DTLog_FTPUpSuccess = ET_DTLog_Event_Base + 0x0B;
	public static final int ET_DTLog_FTPUpFailure = ET_DTLog_Event_Base + 0x0C; // 相当于rcu事件中的FTPULFailure
	public static final int ET_DTLog_FTPUpServiceDrop = ET_DTLog_Event_Base + 0x0D;
	// PING
	public static final int ET_DTLog_PingAttempt = ET_DTLog_Event_Base + 0x0E;
	public static final int ET_DTLog_PingSuccess = ET_DTLog_Event_Base + 0x0F;
	public static final int ET_DTLog_PingFailure = ET_DTLog_Event_Base + 0x10;
	// WAP
	public static final int ET_DTLog_WAPGatewayConnected = ET_DTLog_Event_Base + 0x11; // 当前归类到WAPLoginAttempt(由于GSM无WAPLoginAttempt事件)
	public static final int ET_DTLog_WAPLoginAttempt = ET_DTLog_Event_Base + 0x12; // CDMA有(GSM没有)
	public static final int ET_DTLog_WAPLoginSuccess = ET_DTLog_Event_Base + 0x13;
	public static final int ET_DTLog_WAPLoginFailure = ET_DTLog_Event_Base + 0x14;
	public static final int ET_DTLog_WAPRefreshAttempt = ET_DTLog_Event_Base + 0x15;
	public static final int ET_DTLog_WAPRefreshSuccess = ET_DTLog_Event_Base + 0x16;
	public static final int ET_DTLog_WAPRefreshFailure = ET_DTLog_Event_Base + 0x17;
	public static final int ET_DTLog_WAPDownloadStart = ET_DTLog_Event_Base + 0x18;
	public static final int ET_DTLog_WAPDownloadSuccess = ET_DTLog_Event_Base + 0x19;
	public static final int ET_DTLog_WAPDownloadFailure = ET_DTLog_Event_Base + 0x1A; // 导入DTLog共用,导出DTLog(CDMA用)
	public static final int ET_DTLog_WAPRedirection = ET_DTLog_Event_Base + 0x1B;
	// VP
	public static final int ET_DTLog_VPOutCallAttempt = ET_DTLog_Event_Base + 0x1C;
	public static final int ET_DTLog_VPOutCallAlerting = ET_DTLog_Event_Base + 0x1D;
	public static final int ET_DTLog_VPOutCallFailure = ET_DTLog_Event_Base + 0x1E;
	public static final int ET_DTLog_VPOutCallSetup = ET_DTLog_Event_Base + 0x1F;
	public static final int ET_DTLog_VPOutCallConnect = ET_DTLog_Event_Base + 0x20;
	public static final int ET_DTLog_VPOutCallComplete = ET_DTLog_Event_Base + 0x21;
	public static final int ET_DTLog_VPOutCallDrop = ET_DTLog_Event_Base + 0x22;
	public static final int ET_DTLog_VPInCallAttempt = ET_DTLog_Event_Base + 0x23;
	public static final int ET_DTLog_VPInCallAlerting = ET_DTLog_Event_Base + 0x24;
	public static final int ET_DTLog_VPInCallFailure = ET_DTLog_Event_Base + 0x25;
	public static final int ET_DTLog_VPInCallSetup = ET_DTLog_Event_Base + 0x26;
	public static final int ET_DTLog_VPInCallConnect = ET_DTLog_Event_Base + 0x27;
	public static final int ET_DTLog_VPInCallComplete = ET_DTLog_Event_Base + 0x28;
	public static final int ET_DTLog_VPInCallDrop = ET_DTLog_Event_Base + 0x29;
	public static final int ET_DTLog_VPOutCallH245Setup = ET_DTLog_Event_Base + 0x2A;
	public static final int ET_DTLog_VPInCallH245Setup = ET_DTLog_Event_Base + 0x2B;
	public static final int ET_DTLog_VPFirstVideoFrameArrived = ET_DTLog_Event_Base + 0x2C;
	public static final int ET_DTLog_VPFirstAudioFrameArrived = ET_DTLog_Event_Base + 0x2D;
	// FTPDown\FTPUp, WapDown补充
	public static final int ET_DTLog_WAPKJavaDownLoadFail = ET_DTLog_Event_Base + 0x30; // 导出DTLog(GSM用)
	public static final int ET_DTLog_WAPPicRingDownLoadFail = ET_DTLog_Event_Base + 0x31; // 导出DTLog(GSM用)
	public static final int ET_DTLog_FTPDownDisconnect = ET_DTLog_Event_Base + 0x32; // LTE用
	public static final int ET_DTLog_FTPUpDisconnect = ET_DTLog_Event_Base + 0x33; // LTE用
	// 语音事件-导出时需要(即RCU导出为DTLog),导入时不需要(即读DTLog文件)-2G上报,3G不上报
	public static final int ET_DTLog_Outgoing_Attempt = ET_DTLog_Event_Base + 0x40; // 主叫发起
	public static final int ET_DTLog_Outgoing_Alerting = ET_DTLog_Event_Base + 0x41; // 主叫振铃
	public static final int ET_DTLog_Outgoing_Connected = ET_DTLog_Event_Base + 0x42; // 主叫接通
	public static final int ET_DTLog_Outgoing_Failure = ET_DTLog_Event_Base + 0x43; // 主叫失败
	public static final int ET_DTLog_Incoming_Attempt = ET_DTLog_Event_Base + 0x44; // 被叫响应
	public static final int ET_DTLog_Incoming_Alerting = ET_DTLog_Event_Base + 0x45; // 被叫振铃
	public static final int ET_DTLog_Incoming_Connected = ET_DTLog_Event_Base + 0x46; // 被叫接通
	public static final int ET_DTLog_Incoming_Failure = ET_DTLog_Event_Base + 0x47; // 被叫失败
	public static final int ET_DTLog_Call_Complete = ET_DTLog_Event_Base + 0x48; // 呼叫结束
	public static final int ET_DTLog_Drop_Call = ET_DTLog_Event_Base + 0x49; // 掉话
	// 信令可判事件(导出DTLog用)-2G上报,3G不上报
	public static final int ET_DTLog_Soft_Handoff_Attempt = ET_DTLog_Event_Base + 0x4A; // 软切换发起
																						// Add
	public static final int ET_DTLog_Soft_Handoff_Success = ET_DTLog_Event_Base + 0x4B; // 软切换成功
	public static final int ET_DTLog_Soft_Handoff_Failure = ET_DTLog_Event_Base + 0x4C; // 软切换失败
	public static final int ET_DTLog_Idle_Mode = ET_DTLog_Event_Base + 0x4D; // 空闲模式:转换态
	public static final int ET_DTLog_Dedicated_Mode = ET_DTLog_Event_Base + 0x4E; // 专有模式
	public static final int ET_DTLog_PagingZone_Update = ET_DTLog_Event_Base + 0x4F; // 位置更新请求
	public static final int ET_DTLog_PagingZone_Update_Failure = ET_DTLog_Event_Base + 0x50; // 位置更新失败

	public static final int ET_DTLog_GPRS_Attach_Attempt = ET_DTLog_Event_Base + 0x51; // GPRS附着尝试
	public static final int ET_DTLog_GPRS_Attach_Success = ET_DTLog_Event_Base + 0x52; // GPRS附着成功
	public static final int ET_DTLog_GPRS_Attach_Failure = ET_DTLog_Event_Base + 0x53; // GPRS附着失败
	public static final int ET_DTLog_PDPContext_Activate_Attempt = ET_DTLog_Event_Base + 0x54; // PDP激活尝试
	public static final int ET_DTLog_PDPContext_Activate_Success = ET_DTLog_Event_Base + 0x55; // PDP激活成功
	public static final int ET_DTLog_PDPContext_Activate_Failure = ET_DTLog_Event_Base + 0x56; // PDP激活失败
	public static final int ET_DTLog_Routing_Area_Update = ET_DTLog_Event_Base + 0x57; // 路由区更新成功(只记录成功事件)
	public static final int ET_DTLog_Location_Area_Update = ET_DTLog_Event_Base + 0x58; // 位置区更新成功(只记录成功事件)
	public static final int ET_DTLog_PDPContext_Deactivate_Attempt = ET_DTLog_Event_Base + 0x59; // PDP去激活尝试
	public static final int ET_DTLog_PDPContext_Deactivate_Success = ET_DTLog_Event_Base + 0x5A; // PDP去激活成功
	public static final int ET_DTLog_PDPContext_Deactivate_Failure = ET_DTLog_Event_Base + 0x5B; // PDP去激活失败
	public static final int ET_DTLog_Incoming_CallComplete = ET_DTLog_Event_Base + 0x5C; // 呼叫结束(被叫)-WCDMA,TD
	public static final int ET_DTLog_Incoming_DropCall = ET_DTLog_Event_Base + 0x5D; // 掉话(被叫)-WCDMA,TD

	public static final int ET_DTLog_Event_End = ET_DTLog_Event_Base + 0xBE; //
	// DTLog end1----------------------------------------------

	public static final int ET_Extended_Sec_Base = ET_Extended_Base + 0x100;// 0x10100;
	// 2011-09-29 liulieshan 由 ET_SMS_Status_Report 分解
	public static final int ET_SMS_Status_Report_Success = ET_Extended_Sec_Base + 0x01;
	public static final int ET_SMS_Status_Report_Failure = ET_Extended_Sec_Base + 0x02;

	// 2011-09-29 liulieshan 由 ET_WAP_GetURLFinished 分解
	public static final int ET_WAP_GetURL_Success = ET_Extended_Sec_Base + 0x03;
	public static final int ET_WAP_GetURL_Failure = ET_Extended_Sec_Base + 0x04;

	// 2011-09-29 liulieshan 由 ET_WAPDL_GetURLFinished 分解
	public static final int ET_WAPDL_GetURL_Success = ET_Extended_Sec_Base + 0x05;
	public static final int ET_WAPDL_GetURL_Failure = ET_Extended_Sec_Base + 0x06;

	// 2011-10-12 liulieshan 由 ET_PushMail_SendConnFinish 分解
	public static final int ET_PushMail_SendConnSuccess = ET_Extended_Sec_Base + 0x07;
	public static final int ET_PushMail_SendConnFailure = ET_Extended_Sec_Base + 0x08;

	// 2011-10-12 liulieshan 由 ET_PushMail_SendAuthFinish 分解
	public static final int ET_PushMail_SendAuthSuccess = ET_Extended_Sec_Base + 0x09;
	public static final int ET_PushMail_SendAuthFailure = ET_Extended_Sec_Base + 0x0A;

	// 2011-10-12 liulieshan 由 ET_PushMail_SendFinish 分解
	public static final int ET_PushMail_SendSuccess = ET_Extended_Sec_Base + 0x0B;
	public static final int ET_PushMail_SendFailure = ET_Extended_Sec_Base + 0x0C;

	// 2011-10-12 liulieshan 由 ET_PushMail_SendLogoutFinish 分解
	public static final int ET_PushMail_SendLogoutSuccess = ET_Extended_Sec_Base + 0x0D;
	public static final int ET_PushMail_SendLogoutFailure = ET_Extended_Sec_Base + 0x0E;

	// 2011-10-12 liulieshan 由 ET_CMMB_ScanChannelFinished 分解
	public static final int ET_CMMB_ScanChannelSuccess = ET_Extended_Sec_Base + 0x0F;
	public static final int ET_CMMB_ScanChannelFailed = ET_Extended_Sec_Base + 0x10;

	// 2011-10-14 liulieshan 由 ET_MicroBlogCalledSendCommentSuccess 分解
	public static final int ET_MicroBlogCalledSendCommentSuccessNormal = ET_Extended_Sec_Base + 0x11;
	public static final int ET_MicroBlogCalledSendCommentSuccessUnNormal = ET_Extended_Sec_Base + 0x12;

	public static final int ET_Extended_Sec_End = ET_MicroBlogCalledSendCommentSuccessUnNormal;

	// ************************完全由对应的信令生成的事件****************************
	public static final int ET_MSG_Event_Base = 0x20000;

	// RRC
	public static final int ET_RRC_Connection_Request = ET_MSG_Event_Base + 0x1;
	public static final int ET_RRC_Connection_Setup = ET_MSG_Event_Base + 0x2;
	public static final int ET_RRC_Connection_Setup_Complete = ET_MSG_Event_Base + 0x3;
	public static final int ET_RRC_Connection_Reject = ET_MSG_Event_Base + 0x4;

	public static final int ET_RRC_Release = ET_MSG_Event_Base + 0x5;
	public static final int ET_RRC_Release2 = ET_MSG_Event_Base + 0x6;

	public static final int ET_RRC_Connection_ReestablishmentRequest = ET_MSG_Event_Base + 0x7;
	public static final int ET_RRC_Connection_Reestablishment = ET_MSG_Event_Base + 0x8;
	public static final int ET_RRC_Connection_ReestablishmentComplete = ET_MSG_Event_Base + 0x9;
	public static final int ET_RRC_Connection_ReestablishmentReject = ET_MSG_Event_Base + 0xA;
	// RAB
	public static final int ET_RAB_Setup = ET_MSG_Event_Base + 0x10;
	public static final int ET_RAB_Setup_Complete = ET_MSG_Event_Base + 0x11;
	public static final int ET_RAB_Setup_Failure = ET_MSG_Event_Base + 0x12;
	// ActiveSetUpdate
	public static final int ET_ActiveSet_Update = ET_MSG_Event_Base + 0x20;
	public static final int ET_ActiveSet_Update_Complete = ET_MSG_Event_Base + 0x21;
	public static final int ET_ActiveSet_Update_Failure = ET_MSG_Event_Base + 0x22;
	// PDPActive
	public static final int ET_PDPActive_Request = ET_MSG_Event_Base + 0x30;
	public static final int ET_PDPActive_Accept = ET_MSG_Event_Base + 0x31;
	public static final int ET_PDPActive_Reject = ET_MSG_Event_Base + 0x32;
	public static final int ET_PDPDeActive_Request = ET_MSG_Event_Base + 0x33;
	public static final int ET_PDPDeActive_Accept = ET_MSG_Event_Base + 0x34;

	// PhysicalChannelReconfig
	public static final int ET_PhysicalChannelReconfig = ET_MSG_Event_Base + 0x40;
	public static final int ET_PhysicalChannelReconfig_Complete = ET_MSG_Event_Base + 0x41;
	public static final int ET_PhysicalChannelReconfig_Failure = ET_MSG_Event_Base + 0x42;
	// RABReconfig
	public static final int ET_RABReconfig = ET_MSG_Event_Base + 0x50;
	public static final int ET_RABReconfig_Complete = ET_MSG_Event_Base + 0x51;
	public static final int ET_RABReconfig_Failure = ET_MSG_Event_Base + 0x52;
	// TransportChannelReconfig
	public static final int ET_TransportChannelReconfig = ET_MSG_Event_Base + 0x60;
	public static final int ET_TransportChannelReconfig_Complete = ET_MSG_Event_Base + 0x61;
	public static final int ET_TransportChannelReconfig_Failure = ET_MSG_Event_Base + 0x62;

	// 鉴权
	public static final int ET_AuthenticationRequest = ET_MSG_Event_Base + 0x70;
	public static final int ET_AuthenticationResponse = ET_MSG_Event_Base + 0x71;
	public static final int ET_AuthenticationReject = ET_MSG_Event_Base + 0x72;
	public static final int ET_AuthenticationFailure = ET_MSG_Event_Base + 0x73;

	public static final int ET_AuthenticationCipheringRequest = ET_MSG_Event_Base + 0x74;
	public static final int ET_AuthenticationCipheringResponse = ET_MSG_Event_Base + 0x75;
	public static final int ET_AuthenticationCipheringReject = ET_MSG_Event_Base + 0x76;
	public static final int ET_AuthenticationCipheringFailure = ET_MSG_Event_Base + 0x77;

	public static final int ET_AttachRequestMsg = ET_MSG_Event_Base + 0x78;
	public static final int ET_AttachAcceptMsg = ET_MSG_Event_Base + 0x79;
	public static final int ET_AttachRejectMsg = ET_MSG_Event_Base + 0x7A;
	public static final int ET_AttachCompleteMsg = ET_MSG_Event_Base + 0x7B;

	public static final int ET_DetachRequestMsg = ET_MSG_Event_Base + 0x7C;
	public static final int ET_DetachAcceptMsg = ET_MSG_Event_Base + 0x7D;

	public static final int ET_PTMSIReallocationCommand = ET_MSG_Event_Base + 0x7E;
	public static final int ET_PTMSIReallocationComplete = ET_MSG_Event_Base + 0x7F;

	// QChat
	public static final int ET_QChatStream1Request = ET_MSG_Event_Base + 0x80;
	public static final int ET_QChatStream1Accept = ET_MSG_Event_Base + 0x81;
	public static final int ET_QChatStream1Reject = ET_MSG_Event_Base + 0x82;

	public static final int ET_QChatStream2Request = ET_MSG_Event_Base + 0x83;
	public static final int ET_QChatStream2Accept = ET_MSG_Event_Base + 0x84;
	public static final int ET_QChatStream2Reject = ET_MSG_Event_Base + 0x85;

	public static final int ET_QChatStream3Request = ET_MSG_Event_Base + 0x86;
	public static final int ET_QChatStream3Accept = ET_MSG_Event_Base + 0x87;
	public static final int ET_QChatStream3Reject = ET_MSG_Event_Base + 0x88;
	// 算QChat时延
	public static final int ET_QChat_MO_PageRequest = ET_MSG_Event_Base + 0x89;
	public static final int ET_QChat_MO_ConnectRequest = ET_MSG_Event_Base + 0x8A;
	public static final int ET_QChat_MO_AC_Ack = ET_MSG_Event_Base + 0x8B;
	public static final int ET_QChat_MO_TrafficChannelAssign = ET_MSG_Event_Base + 0x8C;
	public static final int ET_QChat_MO_RTC_Ack = ET_MSG_Event_Base + 0x8D;
	public static final int ET_QChat_MT_Page = ET_MSG_Event_Base + 0x8E;
	public static final int ET_QChat_MT_AC_Ack = ET_MSG_Event_Base + 0x8F;
	public static final int ET_QChat_MT_TrafficChannelAssign = ET_MSG_Event_Base + 0x90;
	public static final int ET_QChat_MT_RTC_Ack = ET_MSG_Event_Base + 0x91;

	public static final int ET_ImmidiateAssignment = ET_MSG_Event_Base + 0x92;
	public static final int ET_CipheringModeCommand = ET_MSG_Event_Base + 0x93;
	public static final int ET_CipheringModeComplete = ET_MSG_Event_Base + 0x94;
	public static final int ET_AssignmentCommand = ET_MSG_Event_Base + 0x95;
	public static final int ET_AssignmentComplete = ET_MSG_Event_Base + 0x96;

	// GSMHandover
	public static final int ET_HO_Success = ET_MSG_Event_Base + 0xD0;
	public static final int ET_HO_Failure = ET_MSG_Event_Base + 0xD1;
	public static final int ET_CellChangeOrderFromUTRAN = ET_MSG_Event_Base + 0xD2;
	public static final int ET_CellChangeOrderFromUTRAN_Failure = ET_MSG_Event_Base + 0xD3;
	public static final int ET_HandoverFromUTRAN = ET_MSG_Event_Base + 0xD4;
	public static final int ET_HandoverFromUTRAN_Failure = ET_MSG_Event_Base + 0xD5;
	public static final int ET_HandoverToUTRANComplete = ET_MSG_Event_Base + 0xD6;
	// LocationUpdate
	public static final int ET_LocaltionUpdate_Request = ET_MSG_Event_Base + 0xE0;
	public static final int ET_LocaltionUpdate_Accept = ET_MSG_Event_Base + 0xE1;
	public static final int ET_LocaltionUpdate_Reject = ET_MSG_Event_Base + 0xE2;
	public static final int ET_RouteUpdate_Request = ET_MSG_Event_Base + 0xE3;
	public static final int ET_RouteUpdate_Accept = ET_MSG_Event_Base + 0xE4;
	public static final int ET_RouteUpdate_Reject = ET_MSG_Event_Base + 0xE5;
	public static final int ET_RouteUpdate_Complete = ET_MSG_Event_Base + 0xE6;
	// sysinfo
	public static final int ET_SysInfoBcch_Bch = ET_MSG_Event_Base + 0xF0;
	public static final int ET_SysInfo1 = ET_MSG_Event_Base + 0xF1;
	public static final int ET_SysInfo2 = ET_MSG_Event_Base + 0xF2;
	public static final int ET_RRChannelRequest = ET_MSG_Event_Base + 0xF3;
	public static final int ET_IMSIDetachIndication = ET_MSG_Event_Base + 0xF4;
	// CellUpdate
	public static final int ET_CellUpdate = ET_MSG_Event_Base + 0x100;
	public static final int ET_CellUpdate_Confirm = ET_MSG_Event_Base + 0x101;
	// TDHandover
	public static final int ET_RRInterSysTemtOutRanHandOver = ET_MSG_Event_Base + 0x110;
	public static final int ET_RRHandoverCommand = ET_MSG_Event_Base + 0x111;

	public static final int ET_WcdmaRachChannelStatus = ET_MSG_Event_Base + 0x112;
	public static final int ET_WcdmaRachStatus = ET_MSG_Event_Base + 0x113;

	// CDMA
	public static final int ET_PCGeneralPageMsg = ET_MSG_Event_Base + 0x126;
	public static final int ET_CCBInitStateMsg = ET_MSG_Event_Base + 0x127;
	public static final int ET_ACPageResponseMsg = ET_MSG_Event_Base + 0x128;
	public static final int ET_RTCOrderConnect = ET_MSG_Event_Base + 0x12A;
	public static final int ET_RTCServiceConnectComp = ET_MSG_Event_Base + 0x135;
	public static final int ET_ACOrigination = ET_MSG_Event_Base + 0x136;
	public static final int ET_FTCServiceConnect = ET_MSG_Event_Base + 0x137;
	public static final int ET_LocationNotification = ET_MSG_Event_Base + 0x138;
	public static final int ET_LocationComplete = ET_MSG_Event_Base + 0x139;
	public static final int ET_CDMAAccessProbeInfo = ET_MSG_Event_Base + 0x13B;
	public static final int ET_CDMAAlertInfo = ET_MSG_Event_Base + 0x13C;
	public static final int ET_RTCOrderRelease = ET_MSG_Event_Base + 0x13D;
	public static final int ET_FTCOrderRelease = ET_MSG_Event_Base + 0x13E;
	public static final int ET_CDMASyncMessage = ET_MSG_Event_Base + 0x13F;
	// EvdoSession
	public static final int ET_EvdoSessionRequest = ET_MSG_Event_Base + 0x140;
	public static final int ET_EvdoSessionSetup = ET_MSG_Event_Base + 0x141;

	public static final int ET_CDMARegistrationRequest = ET_MSG_Event_Base + 0x142;
	public static final int ET_MCTrafficChannelAssignment = ET_MSG_Event_Base + 0x143;
	public static final int ET_MCTrafficChannelComplete = ET_MSG_Event_Base + 0x144;
	// 注册
	public static final int ET_CDMARegistrationAccept = ET_MSG_Event_Base + 0x147;
	public static final int ET_CDMARegistrationReject = ET_MSG_Event_Base + 0x148;

	// 语音
	// GSM
	public static final int ET_CMServiceRequest = ET_MSG_Event_Base + 0x150;
	public static final int ET_CMServiceAcpt = ET_MSG_Event_Base + 0x151;
	public static final int ET_CMServiceRejct = ET_MSG_Event_Base + 0x152;
	public static final int ET_CMServiceAbort = ET_MSG_Event_Base + 0x153;

	public static final int ET_CMConnect = ET_MSG_Event_Base + 0x160;
	public static final int ET_CMConnectAck = ET_MSG_Event_Base + 0x161;
	public static final int ET_CMDisconnect = ET_MSG_Event_Base + 0x162;
	public static final int ET_CMCallConfirm = ET_MSG_Event_Base + 0x163;
	public static final int ET_CMProceeding = ET_MSG_Event_Base + 0x164;
	public static final int ET_CMSetup = ET_MSG_Event_Base + 0x165;
	public static final int ET_CPData = ET_MSG_Event_Base + 0x166;

	public static final int ET_CMAlerting = ET_MSG_Event_Base + 0x170;
	public static final int ET_ChannelRelease = ET_MSG_Event_Base + 0x171;
	public static final int ET_RRRelease = ET_MSG_Event_Base + 0x172;
	public static final int ET_PagintRequestType1 = ET_MSG_Event_Base + 0x174;
	public static final int ET_PagintResp = ET_MSG_Event_Base + 0x177;

	public static final int ET_GSMSysInfo1 = ET_MSG_Event_Base + 0x180;
	public static final int ET_GSMSysInfo4 = ET_MSG_Event_Base + 0x183;
	public static final int ET_GSMSysInfo5 = ET_MSG_Event_Base + 0x184;
	public static final int ET_GSMSysInfo6 = ET_MSG_Event_Base + 0x185;
	public static final int ET_GSMSysInfo13 = ET_MSG_Event_Base + 0x186;

	public static final int ET_TerminalMsgEvent_Base = ET_MSG_Event_Base + 0x200;

	public static final int ET_DaTangBase = ET_TerminalMsgEvent_Base;

	public static final int ET_DaTang_Intra_TDD_Reselection_Start = ET_DaTangBase;
	public static final int ET_DaTang_Intra_TDD_Reselection_Success = ET_DaTangBase + 0x1;
	public static final int ET_DaTang_Intra_TDD_Reselection_Failure = ET_DaTangBase + 0x2;

	public static final int ET_DaTang_Handover_To_GSM_Start = ET_DaTangBase + 0x3;
	public static final int ET_DaTang_Handover_To_GSM_Success = ET_DaTangBase + 0x4;
	public static final int ET_DaTang_Handover_To_GSM_Failure = ET_DaTangBase + 0x5;

	public static final int ET_DaTang_Inter_Reselection_To_GSM_Start = ET_DaTangBase + 0x6;
	public static final int ET_DaTang_Inter_Reselection_To_GSM_Success = ET_DaTangBase + 0x7;
	public static final int ET_DaTang_Inter_Reselection_To_GSM_Failure = ET_DaTangBase + 0x8;

	public static final int ET_DaTang_GSM_Handover_Start = ET_DaTangBase + 0x9;
	public static final int ET_DaTang_GSM_Handover_Success = ET_DaTangBase + 0xA;
	public static final int ET_DaTang_GSM_Handover_Failure = ET_DaTangBase + 0xB;

	public static final int ET_DaTang_Intra_GSM_Reselection_Start = ET_DaTangBase + 0xC;
	public static final int ET_DaTang_Intra_GSM_Reselection_Success = ET_DaTangBase + 0xD;
	public static final int ET_DaTang_Intra_GSM_Reselection_Failure = ET_DaTangBase + 0xE;

	public static final int ET_DaTang_RAT_Measure_Start = ET_DaTangBase + 0xF;
	public static final int ET_DaTang_RAT_Measure_Stop = ET_DaTangBase + 0x10;

	public static final int ET_DaTang_Inter_Reselection_To_TDD_Start = ET_DaTangBase + 0x11;
	public static final int ET_DaTang_Inter_Reselection_To_TDD_Success = ET_DaTangBase + 0x12;
	public static final int ET_DaTang_Inter_Reselection_To_TDD_Failure = ET_DaTangBase + 0x13;

	public static final int ET_DaTang_1gEventMeasurementReport = ET_DaTangBase + 0x14;
	public static final int ET_DaTang_2aEventMeasurementReport = ET_DaTangBase + 0x15;
	public static final int ET_DaTang_5aEventMeasurementReport = ET_DaTangBase + 0x16;

	public static final int ET_DingXing = ET_MSG_Event_Base + 0x280;
	public static final int ET_DingXing_Inter_Reselection_To_TDD_Start = ET_DingXing + 0x1;
	public static final int ET_DingXing_Inter_Reselection_To_GSM_Start = ET_DingXing + 0x2;
	public static final int ET_DingXing_Inter_Reselection_23G_Success = ET_DingXing + 0x3;
	// public static final int
	// ET_DingXing_Inter_Reselection_To_GSM_Success=ET_DingXing+0x4;
	public static final int ET_TerminalMsgEvent_End = ET_DingXing_Inter_Reselection_23G_Success;

	// LTE
	public static final int ET_LTEActiveDefaultEPSRequest = ET_MSG_Event_Base + 0x400;
	public static final int ET_LTEActiveDefaultEPSAccept = ET_MSG_Event_Base + 0x401;
	public static final int ET_LTEActiveDefaultEPSReject = ET_MSG_Event_Base + 0x402;
	
	public static final int ET_LTEActiveDedicatedEPSRequest = ET_MSG_Event_Base + 0x41F;
	public static final int ET_LTEActiveDedicatedEPSAccept = ET_MSG_Event_Base + 0x420;
	public static final int ET_LTEActiveDedicatedEPSReject = ET_MSG_Event_Base + 0x421;
	

	public static final int ET_LTETrackingAreaUpdateRequest = ET_MSG_Event_Base + 0x403;
	public static final int ET_LTETrackingAreaUpdateAccept = ET_MSG_Event_Base + 0x404;
	public static final int ET_LTETrackingAreaUpdateReject = ET_MSG_Event_Base + 0x405;

	public static final int ET_LTERRCReconfigRequest = ET_MSG_Event_Base + 0x406;
	public static final int ET_LTERRCReconfigComplete = ET_MSG_Event_Base + 0x407;

	public static final int ET_LTECellReselectStart = ET_MSG_Event_Base + 0x408;
	public static final int ET_LTECellReselectSuccess = ET_MSG_Event_Base + 0x409;
	public static final int ET_LTECellReselectFailure = ET_MSG_Event_Base + 0x40A;

	public static final int ET_LTEPRACHStart = ET_MSG_Event_Base + 0x40B;
	public static final int ET_LTEPRACHSuccess = ET_MSG_Event_Base + 0x40C;
	public static final int ET_LTEPRACHFailure = ET_MSG_Event_Base + 0x40D;

	public static final int ET_LTECellSearchStart = ET_MSG_Event_Base + 0x40E;
	public static final int ET_LTECellCampOn = ET_MSG_Event_Base + 0x40F;

	public static final int ET_LTEPowerOn = ET_MSG_Event_Base + 0x410;
	public static final int ET_LTEServiceRequest = ET_MSG_Event_Base + 0x411;

	public static final int ET_LTEMsgAttachRequest = ET_MSG_Event_Base + 0x412;
	public static final int ET_LTEMsgAttachAccept =ET_MSG_Event_Base+0x413;
	public static final int ET_LTEMsgAttachComplete = ET_MSG_Event_Base + 0x414;
	public static final int ET_LTEMsgAttachReject = ET_MSG_Event_Base + 0x415;

	public static final int ET_LTEMIB = ET_MSG_Event_Base + 0x416;

	public static final int ET_LTEMsgDetachRequest = ET_MSG_Event_Base + 0x417;
	public static final int ET_LTEMsgDetachAccept = ET_MSG_Event_Base + 0x418;

	public static final int ET_LTETrackingAreaUpdateComplete = ET_MSG_Event_Base + 0x419;

	public static final int ET_LTERandomAccessMessage0 = ET_MSG_Event_Base + 0x41A;
	public static final int ET_LTERandomAccessMessage1 = ET_MSG_Event_Base + 0x41B;
	public static final int ET_LTERandomAccessMessage2 = ET_MSG_Event_Base + 0x41C;
	public static final int ET_LTERandomAccessMessage3 = ET_MSG_Event_Base + 0x41D;
	public static final int ET_LTERandomAccessMessage4 = ET_MSG_Event_Base + 0x41E;
	public static final int ET_Redirection_CellOutoffSync			= ET_MSG_Event_Base + 0x427;
	public static final int ET_CELL_UPDATE_CONFIRM			= ET_MSG_Event_Base + 0x101;

	public static final int ET_RR_Connetion_Abnormal_Release			= ET_MSG_Event_Base + 0x17;
	public static final int ET_RR_Connetion_Normal_Release			= ET_MSG_Event_Base + 0x16;
	public static final int ET_RR_Connection_Request					= ET_MSG_Event_Base + 0x13;
	public static final int ET_RR_Connection_Setup_Failure				= ET_MSG_Event_Base + 0x15;
	public static final int ET_RR_Connection_Setup_Success				= ET_MSG_Event_Base + 0x14;

	// *********************由信令事件和一定的规则生成的事件*************************
	public static final int ET_Custom_Event_Base = 0x30000;

	// GSMHardHandover
	public static final int ET_GSM_Hard_Handover_Request = ET_Custom_Event_Base + 0x0;
	public static final int ET_GSM_Hard_Handover_Success = ET_Custom_Event_Base + 0x1;
	public static final int ET_GSM_Hard_Handover_Failure = ET_Custom_Event_Base + 0x2;

	public static final int ET_GSM_IntraCell_Handover_Request = ET_Custom_Event_Base + 0x3;
	public static final int ET_GSM_IntraCell_Handover_Success = ET_Custom_Event_Base + 0x4;
	public static final int ET_GSM_IntraCell_Handover_Failure = ET_Custom_Event_Base + 0x5;
	// CDMAHardHandover
	public static final int ET_CDMA_Hard_Handover_Request = ET_Custom_Event_Base + 0x10;
	public static final int ET_CDMA_Hard_Handover_Success = ET_Custom_Event_Base + 0x11;
	public static final int ET_CDMA_Hard_Handover_Failure = ET_Custom_Event_Base + 0x12;
	// SoftHandover
	public static final int ET_CDMA_Soft_Handover_Request = ET_Custom_Event_Base + 0x30;
	public static final int ET_CDMA_Soft_Handover_Success = ET_Custom_Event_Base + 0x31;
	public static final int ET_CDMA_Soft_Handover_Failure = ET_Custom_Event_Base + 0x32;

	public static final int ET_WCDMA_Soft_Handover_Request = ET_Custom_Event_Base + 0x33;
	public static final int ET_WCDMA_Soft_Handover_Success = ET_Custom_Event_Base + 0x34;
	public static final int ET_WCDMA_Soft_Handover_Failure = ET_Custom_Event_Base + 0x35;
	// HardHandover
	public static final int ET_WCDMA_Hard_Handover_Request = ET_Custom_Event_Base + 0x70;
	public static final int ET_WCDMA_Hard_Handover_Success = ET_Custom_Event_Base + 0x71;
	public static final int ET_WCDMA_Hard_Handover_Failure = ET_Custom_Event_Base + 0x72;

	public static final int ET_TD_Hard_Handover_Request = ET_Custom_Event_Base + 0x73;
	public static final int ET_TD_Hard_Handover_Success = ET_Custom_Event_Base + 0x74;
	public static final int ET_TD_Hard_Handover_Failure = ET_Custom_Event_Base + 0x75;
	// HandoverToUMTS
	public static final int ET_Handover_To_WCDMA_Request = ET_Custom_Event_Base + 0x80;
	public static final int ET_Handover_To_WCDMA_Success = ET_Custom_Event_Base + 0x81;
	public static final int ET_Handover_To_WCDMA_Failure = ET_Custom_Event_Base + 0x82;

	public static final int ET_Handover_To_TD_Request = ET_Custom_Event_Base + 0x83;
	public static final int ET_Handover_To_TD_Success = ET_Custom_Event_Base + 0x84;
	public static final int ET_Handover_To_TD_Failure = ET_Custom_Event_Base + 0x85;

	// CellReselect
	public static final int ET_Intra_WCDMA_CellReselect = ET_Custom_Event_Base + 0x90;
	public static final int ET_Inter_CellReselect_WCDMA_To_GSM_Request = ET_Custom_Event_Base + 0x91;
	public static final int ET_Inter_CellReselect_WCDMA_To_GSM_Complete = ET_Custom_Event_Base + 0x92;

	public static final int ET_Inter_CellReselect_GSM_To_WCDMA_Request = ET_Custom_Event_Base + 0x93;
	public static final int ET_Inter_CellReselect_GSM_To_WCDMA_Complete = ET_Custom_Event_Base + 0x94;
	public static final int ET_Intra_GSM_CellReselect = ET_Custom_Event_Base + 0x95;
	public static final int ET_Intra_CDMA_CellReselect = ET_Custom_Event_Base + 0x96;
	public static final int ET_Intra_Evdo_CellReselect = ET_Custom_Event_Base + 0x97;

	public static final int ET_Inter_CellReselect_TD_To_GSM_Request = ET_Custom_Event_Base + 0x98;
	public static final int ET_Inter_CellReselect_TD_To_GSM_Complete = ET_Custom_Event_Base + 0x99;

	public static final int ET_Intra_TD_CellReselect = ET_Custom_Event_Base + 0x9A;

	public static final int ET_Inter_CellReselect_GSM_To_TD_Request = ET_Custom_Event_Base + 0x9B;
	public static final int ET_Inter_CellReselect_GSM_To_TD_Complete = ET_Custom_Event_Base + 0x9C;

	public static final int ET_Intra_TDDLTE_CellReselect = ET_Custom_Event_Base + 0x9D;
	// HSPAReselect
	public static final int ET_HSPA_Reselect_Request = ET_Custom_Event_Base + 0xA0;
	public static final int ET_HSPA_Reselect_Success = ET_Custom_Event_Base + 0xA1;
	public static final int ET_HSPA_Reselect_Failure = ET_Custom_Event_Base + 0xA2;

	// 3,4G重选
	public static final int ET_Inter_CellReselect_LTE_To_WCDMA_Request = ET_Custom_Event_Base + 0xA3;
	public static final int ET_Inter_CellReselect_LTE_To_WCDMA_Complete = ET_Custom_Event_Base + 0xA4;

	public static final int ET_Inter_CellReselect_WCDMA_To_LTE_Request = ET_Custom_Event_Base + 0xA5;
	public static final int ET_Inter_CellReselect_WCDMA_To_LTE_Complete = ET_Custom_Event_Base + 0xA6;

	public static final int ET_Inter_CellReselect_LTE_To_TD_Request = ET_Custom_Event_Base + 0xA7;
	public static final int ET_Inter_CellReselect_LTE_To_TD_Complete = ET_Custom_Event_Base + 0xA8;

	public static final int ET_Inter_CellReselect_TD_To_LTE_Request = ET_Custom_Event_Base + 0xA9;
	public static final int ET_Inter_CellReselect_TD_To_LTE_Complete = ET_Custom_Event_Base + 0xAA;

	public static final int ET_Inter_CellReselect_LTE_To_TD_Failure = ET_Custom_Event_Base + 0xAB;
	public static final int ET_Inter_CellReselect_TD_To_LTE_Failure = ET_Custom_Event_Base + 0xAC;
	public static final int ET_Inter_CellReselect_LTE_To_WCDMA_Failure = ET_Custom_Event_Base + 0xAD;
	public static final int ET_Inter_CellReselect_WCDMA_To_LTE_Failure = ET_Custom_Event_Base + 0xAE;

	// HandoverFromUMTS
	public static final int ET_Handover_From_WCDMA_Request = ET_Custom_Event_Base + 0xB0;
	public static final int ET_Handover_From_WCDMA_Success = ET_Custom_Event_Base + 0xB1;
	public static final int ET_Handover_From_WCDMA_Failure = ET_Custom_Event_Base + 0xB2;

	public static final int ET_Handover_From_TD_Request = ET_Custom_Event_Base + 0xB3;
	public static final int ET_Handover_From_TD_Success = ET_Custom_Event_Base + 0xB4;
	public static final int ET_Handover_From_TD_Failure = ET_Custom_Event_Base + 0xB5;
	// PS HandoverFromUMTS
	public static final int ET_PS_Handover_From_WCDMA_Request = ET_Custom_Event_Base + 0xC0;
	public static final int ET_PS_Handover_From_WCDMA_Success = ET_Custom_Event_Base + 0xC1;
	public static final int ET_PS_Handover_From_WCDMA_Failure = ET_Custom_Event_Base + 0xC2;

	public static final int ET_PS_Handover_From_TD_Request = ET_Custom_Event_Base + 0xC3;
	public static final int ET_PS_Handover_From_TD_Success = ET_Custom_Event_Base + 0xC4;
	public static final int ET_PS_Handover_From_TD_Failure = ET_Custom_Event_Base + 0xC5;
	// PS HandoverToUMTS
	public static final int ET_PS_Handover_To_WCDMA_Request = ET_Custom_Event_Base + 0xD0;
	public static final int ET_PS_Handover_To_WCDMA_Success = ET_Custom_Event_Base + 0xD1;
	public static final int ET_PS_Handover_To_WCDMA_Failure = ET_Custom_Event_Base + 0xD2;

	public static final int ET_PS_Handover_To_TD_Request = ET_Custom_Event_Base + 0xD3;
	public static final int ET_PS_Handover_To_TD_Success = ET_Custom_Event_Base + 0xD4;
	public static final int ET_PS_Handover_To_TD_Failure = ET_Custom_Event_Base + 0xD5;

	// 3,4G重搜
	public static final int ET_Inter_Research_LTE_To_WCDMA_Request = ET_Custom_Event_Base + 0xF0;
	public static final int ET_Inter_Research_LTE_To_WCDMA_Complete = ET_Custom_Event_Base + 0xF1;

	public static final int ET_Inter_Research_WCDMA_To_LTE_Request = ET_Custom_Event_Base + 0xF2;
	public static final int ET_Inter_Research_WCDMA_To_LTE_Complete = ET_Custom_Event_Base + 0xF3;

	public static final int ET_Inter_Research_LTE_To_TD_Request = ET_Custom_Event_Base + 0xF4;
	public static final int ET_Inter_Research_LTE_To_TD_Complete = ET_Custom_Event_Base + 0xF5;

	public static final int ET_Inter_Research_TD_To_LTE_Request = ET_Custom_Event_Base + 0xF6;
	public static final int ET_Inter_Research_TD_To_LTE_Complete = ET_Custom_Event_Base + 0xF7;

	// 3,4G切换
	public static final int ET_LTE_Handover_To_WCDMA_Request = ET_Custom_Event_Base + 0x100;
	public static final int ET_LTE_Handover_To_WCDMA_Success = ET_Custom_Event_Base + 0x101;
	public static final int ET_LTE_Handover_To_WCDMA_Failure = ET_Custom_Event_Base + 0x102;

	public static final int ET_LTE_Handover_To_TD_Request = ET_Custom_Event_Base + 0x103;
	public static final int ET_LTE_Handover_To_TD_Success = ET_Custom_Event_Base + 0x104;
	public static final int ET_LTE_Handover_To_TD_Failure = ET_Custom_Event_Base + 0x105;

	// Wcdma Rach
	public static final int ET_WCDMARach_Request = ET_Custom_Event_Base + 0x110;
	public static final int ET_WCDMARach_Success = ET_Custom_Event_Base + 0x111;
	public static final int ET_WCDMARach_Failure = ET_Custom_Event_Base + 0x112;

	// WCDMA Paging
	public static final int ET_WCDMA_PagingRequest = ET_Custom_Event_Base + 0x120;
	public static final int ET_WCDMA_PagingSuccess = ET_Custom_Event_Base + 0x121;
	public static final int ET_WCDMA_PagingFailure = ET_Custom_Event_Base + 0x122;

	// BatonHandover
	public static final int ET_Baton_Handover_Request = ET_Custom_Event_Base + 0x130;
	public static final int ET_Baton_Handover_Success = ET_Custom_Event_Base + 0x131;
	public static final int ET_Baton_Handover_Failure = ET_Custom_Event_Base + 0x132;

	// CDMA
	// 反向软切换
	public static final int ET_EvdoSoftHandover_Request = ET_Custom_Event_Base + 0x140;
	public static final int ET_EvdoSoftHandover_Success = ET_Custom_Event_Base + 0x141;

	public static final int ET_EvdoConnection_Request = ET_Custom_Event_Base + 0x142;
	public static final int ET_EvdoConnection_Success = ET_Custom_Event_Base + 0x143;
	public static final int ET_EvdoConnection_Close = ET_Custom_Event_Base + 0x144;
	public static final int ET_EvdoConnection_Drop = ET_Custom_Event_Base + 0x145;
	public static final int ET_EvdoConnection_Failure = ET_Custom_Event_Base + 0x146;
	public static final int ET_EvdoConnection_DropForOtherReason = ET_Custom_Event_Base + 0x147;

	public static final int ET_SID_Area_Update = ET_Custom_Event_Base + 0x148; // 专为CTI设计
	// Do和1x之间的切换
	public static final int ET_EvdoTo1xActiveHandover_Request = ET_Custom_Event_Base + 0x150;
	public static final int ET_EvdoTo1xActiveHandover_Success = ET_Custom_Event_Base + 0x151;
	public static final int ET_EvdoTo1xSleepHandover_Request = ET_Custom_Event_Base + 0x153;
	public static final int ET_EvdoTo1xSleepHandover_Success = ET_Custom_Event_Base + 0x154;
	public static final int ET_1xToEvdoSleepHandover_Request = ET_Custom_Event_Base + 0x156;
	public static final int ET_1xToEvdoSleepHandover_Success = ET_Custom_Event_Base + 0x157;

	public static final int ET_EvdoVirtualSoftHandover_Request = ET_Custom_Event_Base + 0x160;
	public static final int ET_EvdoVirtualSoftHandover_Success = ET_Custom_Event_Base + 0x161;
	public static final int ET_EvdoVirtualSoftHandover_Failure = ET_Custom_Event_Base + 0x162;

	// 语音事件
	public static final int ET_MO_Attempt = ET_Custom_Event_Base + 0x170; // 主叫
	public static final int ET_MO_Alerting = ET_Custom_Event_Base + 0x171;
	public static final int ET_MO_Connect = ET_Custom_Event_Base + 0x172;
	public static final int ET_MO_End = ET_Custom_Event_Base + 0x173;
	public static final int ET_MO_Drop = ET_Custom_Event_Base + 0x174;
	public static final int ET_MO_Attempt_Retry = ET_Custom_Event_Base + 0x175;
	public static final int ET_MO_Block = ET_Custom_Event_Base + 0x176;

	public static final int ET_MT_Attempt = ET_Custom_Event_Base + 0x180; // 被叫
	public static final int ET_MT_Alerting = ET_Custom_Event_Base + 0x181;
	public static final int ET_MT_Connect = ET_Custom_Event_Base + 0x182;
	public static final int ET_MT_End = ET_Custom_Event_Base + 0x183;
	public static final int ET_MT_Drop = ET_Custom_Event_Base + 0x184;
	public static final int ET_MT_Block = ET_Custom_Event_Base + 0x185;

	// 多载波切换事件
	public static final int ET_MultiCarrierHandoverRequest = ET_Custom_Event_Base + 0x186;
	public static final int ET_MultiCarrierHandoverSuccess = ET_Custom_Event_Base + 0x187;
	public static final int ET_MultiCarrierHandoverFailure = ET_Custom_Event_Base + 0x188;

	public static final int ET_PassivegpsOneRequestFromMPC = ET_Custom_Event_Base + 0x189;

	// QChat
	public static final int ET_QChatInitAttempt = ET_Custom_Event_Base + 0x200;
	public static final int ET_QChatInitSuccess = ET_Custom_Event_Base + 0x201;
	public static final int ET_QChatInitFailure = ET_Custom_Event_Base + 0x202;

	public static final int ET_QChatInCallAttempt = ET_Custom_Event_Base + 0x210;
	public static final int ET_QChatInCallSuccess = ET_Custom_Event_Base + 0x211;
	public static final int ET_QChatInCallFailure = ET_Custom_Event_Base + 0x212;
	// LTE
	public static final int ET_LTEHandoverRequest = ET_Custom_Event_Base + 0x300;
	public static final int ET_LTEHandoverSuccess = ET_Custom_Event_Base + 0x301;
	public static final int ET_LTEHandoverFailure = ET_Custom_Event_Base + 0x302;
	public static final int ET_PSToCS_Handover_To_GSM_Request = ET_Custom_Event_Base + 0xB6;
	public static final int ET_PSToCS_Handover_To_GSM_Success = ET_Custom_Event_Base + 0xB7;
	public static final int ET_PSToCS_Handover_To_GSM_Failure = ET_Custom_Event_Base + 0xB8;

	public static final int ET_LTEPagingRequest = ET_Custom_Event_Base + 0x306;
	public static final int ET_LTEPagingSuccess = ET_Custom_Event_Base + 0x307;
	public static final int ET_LTEPagingFailure = ET_Custom_Event_Base + 0x308;

	public static final int ET_LTEAccessRequest = ET_Custom_Event_Base + 0x309;
	public static final int ET_LTEAccessSuccess = ET_Custom_Event_Base + 0x30A;
	public static final int ET_LTEAccessFailure = ET_Custom_Event_Base + 0x30B;

	public static final int ET_LTEServiceDrop = ET_Custom_Event_Base + 0x30C;
	public static final int ET_LTERRCConnectionDrop = ET_Custom_Event_Base + 0x30D;

	public static final int ET_LTERRCRadionLinkFailure = ET_Custom_Event_Base + 0x414;

	public static final int ET_LTEControlPlaneDelay = ET_Custom_Event_Base + 0x30E;
	public static final int ET_LTEDragnetDelay = ET_Custom_Event_Base + 0x30F;
	public static final int ET_LTE_ERAB_AbnormalRelease = ET_Custom_Event_Base + 0x310; // ERAB
																						// Abnormal
																						// Release
	public static final int ET_LTE_ERAB_Request			= ET_Custom_Event_Base + 0x326;
	public static final int ET_LTE_ERAB_Success			= ET_Custom_Event_Base + 0x327;
	public static final int ET_LTE_ERAB_Failure			= ET_Custom_Event_Base + 0x328;

	// LTE数据业务信令连接事件（由Attach，RRCRelease，RRCReeastablish及Detach信令判出）
	public static final int ET_LTEDataServiceConnectRequest = ET_Custom_Event_Base + 0x311;
	public static final int ET_LTEDataServiceConnectSuccess = ET_Custom_Event_Base + 0x312;
	public static final int ET_LTEDataServiceConnectFailure = ET_Custom_Event_Base + 0x313;
	public static final int ET_LTEDataServiceDrop = ET_Custom_Event_Base + 0x314;
	public static final int ET_LTEDataServiceFinished = ET_Custom_Event_Base + 0x315;
	public static final int ET_LTE_L2_MCE_ACTIVE_SCell		= ET_Custom_Event_Base + 0x415;
	public static final int ET_LTE_L2_MCE_DEACTIVE_SCell		= ET_Custom_Event_Base + 0x416;
	public static final int ET_LTE_SCell_Configuration_Request	= ET_Custom_Event_Base + 0x303;
	public static final int ET_LTE_SCell_Configuration_Success	= ET_Custom_Event_Base + 0x304;
	public static final int ET_LTE_SCell_Configuration_Failure	= ET_Custom_Event_Base + 0x305;

	public static final int ET_LTE_ServiceRequest = ET_Custom_Event_Base + 0x316;
	public static final int ET_LTE_ServiceSuccess = ET_Custom_Event_Base + 0x317;

	// CSFB事件（隶属语音业务过程）
	public static final int ET_MO_CSFB_Request = ET_Custom_Event_Base + 0x318; // CSFB主叫请求事件
	public static final int ET_MO_CSFB_Success = ET_Custom_Event_Base + 0x319; // CSFB主叫建立成功事件
	public static final int ET_MO_CSFB_Failure = ET_Custom_Event_Base + 0x31A; // CSFB主叫建立失败事件
	public static final int ET_MO_CSFB_Proceeding = ET_Custom_Event_Base + 0x31B; // CSFB主叫proceeding事件
	public static final int ET_MO_CSFB_RRCRelease = ET_Custom_Event_Base + 0x31C; // CSFB主叫RRC Release事件
	public static final int ET_MO_CSFB_Coverage = ET_Custom_Event_Base + 0x31D; // CSFB主叫coverage事件
	public static final int ET_MO_CSFB_Abnormal = ET_Custom_Event_Base + 0x337;
	
	public static final int ET_MT_CSFB_Request = ET_Custom_Event_Base + 0x31E; 		// CSFB被叫请求事件
	public static final int ET_MT_CSFB_Success = ET_Custom_Event_Base + 0x31F;	 	// CSFB被叫建立成功事件
	public static final int ET_MT_CSFB_Failure = ET_Custom_Event_Base + 0x320; 		// CSFB被叫建立失败事件
	public static final int ET_MT_CSFB_Proceeding = ET_Custom_Event_Base + 0x321; 	// CSFB被叫proceeding事件
	public static final int ET_MT_CSFB_RRCRelease = ET_Custom_Event_Base + 0x322; 	// CSFB被叫RRC Release事件
	public static final int ET_MT_CSFB_Coverage = ET_Custom_Event_Base + 0x323; 	// CSFB被叫coverage事件
	public static final int ET_MT_CSFB_Abnormal = ET_Custom_Event_Base + 0x338;		//

	// CSFB Return to LTE
	public static final int ET_ReturnToLTE_Request = ET_Custom_Event_Base + 0x324; // 对应信令Channel
																					// release或WCDMA->rrcconnectionrelease或TDS->rrcconnectionrelease
	public static final int ET_ReturnToLTE_Complete = ET_Custom_Event_Base + 0x325; // 对应信令LTE
																					// NAS-->Tracking
																					// area
																					// update
																					// complete
	public static final int ET_ReturnToLTE_Failure	= ET_Custom_Event_Base + 0x32C;	//
	
	
	//twq20140902新加事件
	public static final int ET_Inter_CellReselect_WCDMA_To_GSM_Failure	= ET_Custom_Event_Base + 0x9E;
	public static final int ET_Inter_CellReselect_GSM_To_WCDMA_Failure	= ET_Custom_Event_Base + 0x9F;
	public static final int ET_Redirection_LTEToWCDMA_Request			= ET_Custom_Event_Base + 0x340;
	public static final int ET_Redirection_LTEToWCDMA_Success			= ET_Custom_Event_Base + 0x341;
	public static final int ET_Redirection_LTEToWCDMA_Failure			= ET_Custom_Event_Base + 0x342;
	public static final int ET_Redirection_LTEToGSM_Request				= ET_Custom_Event_Base + 0x343;
	public static final int ET_Redirection_LTEToGSM_Success				= ET_Custom_Event_Base + 0x344;
	public static final int	ET_Redirection_LTEToGSM_Failure				= ET_Custom_Event_Base + 0x345;
	public static final int ET_Redirection_GSMToWCDMA_Request			= ET_Custom_Event_Base + 0x346;
	public static final int ET_Redirection_GSMToWCDMA_Success			= ET_Custom_Event_Base + 0x347;
	public static final int ET_Redirection_GSMToWCDMA_Failure			= ET_Custom_Event_Base + 0x348;
	public static final int ET_Redirection_GSMToLTE_Request				= ET_Custom_Event_Base + 0x349;
	public static final int ET_Redirection_GSMToLTE_Success				= ET_Custom_Event_Base + 0x34A;
	public static final int ET_Redirection_GSMToLTE_Failure				= ET_Custom_Event_Base + 0x34B;
	public static final int ET_Redirection_WCDMAToGSM_Request			= ET_Custom_Event_Base + 0x34C;
	public static final int ET_Redirection_WCDMAToGSM_Success			= ET_Custom_Event_Base + 0x34D;
	public static final int ET_Redirection_WCDMAToGSM_Failure			= ET_Custom_Event_Base + 0x34E;
	public static final int ET_Redirection_WCDMAToLTE_Request			= ET_Custom_Event_Base + 0x34F;
	public static final int ET_Redirection_WCDMAToLTE_Success			= ET_Custom_Event_Base + 0x350;
	public static final int ET_Redirection_WCDMAToLTE_Failure			= ET_Custom_Event_Base + 0x351;

	public static final int ET_Intra_NBIot_CellReselect_Start			= ET_Custom_Event_Base + 0xBA;
	public static final int ET_Intra_NBIot_CellReselect_Failure			= ET_Custom_Event_Base + 0xBB;
	public static final int ET_Intra_NBIot_CellReselect_Complete		= ET_Custom_Event_Base + 0xB9;

    //NR的
	public static final int ET_NR_CELL_PRACH_REQUEST		= ET_Custom_Event_Base + 0x100F;
	public static final int ET_NR_CELL_PRACH_SUCCESS		= ET_Custom_Event_Base + 0x1010;
	public static final int ET_NR_CELL_PRACH_FAILURE		= ET_Custom_Event_Base + 0x1011;
	public static final int ET_NR_PRACH_MSG1		= ET_Custom_Event_Base + 0x1012;
	public static final int ET_NR_PRACH_MSG2		= ET_Custom_Event_Base + 0x1013;
	public static final int ET_NR_PRACH_MSG3		= ET_Custom_Event_Base + 0x1014;
	public static final int ET_NR_PRACH_MSG4		= ET_Custom_Event_Base + 0x1015;
	public static final int ET_REGISTRATION_ACCEPT		= ET_Custom_Event_Base + 0x1001;
	public static final int ET_REGISTRATION_REJECT		= ET_Custom_Event_Base + 0x1002;
	public static final int ET_RRC_RESUME_REQUEST		= ET_Custom_Event_Base + 0x1003;
	public static final int ET_RRC_RESUME_ACCEPT		= ET_Custom_Event_Base + 0x1004;
	public static final int ET_RRC_RESUME_REJECT		= ET_Custom_Event_Base + 0x1005;
	public static final int ET_PDU_SESSION_ESTABLISHMENT_REQUEST		= ET_Custom_Event_Base + 0x1006;
	public static final int ET_PDU_SESSION_ESTABLISHMENT_ACCEPT		= ET_Custom_Event_Base + 0x1007;
	public static final int ET_PDU_SESSION_ESTABLISHMENT_REJECT		= ET_Custom_Event_Base + 0x1008;
	public static final int ET_NR_HANDOVER_REQUEST		= ET_Custom_Event_Base + 0x1009;
	public static final int ET_NR_REGISTRATION_REQUEST		= ET_Custom_Event_Base + 0x1000;
	public static final int ET_NR_HANDOVER_SUCCESS		= ET_Custom_Event_Base + 0x100A;
	public static final int ET_NR_HANDOVER_FAILED		= ET_Custom_Event_Base + 0x100B;
	public static final int ET_NR_CELL_ADD		= ET_Custom_Event_Base + 0x100C;
	public static final int ET_NR_CELL_FAILURE		= ET_Custom_Event_Base + 0x100D;
	public static final int ET_NR_CELL_RELEASE		= ET_Custom_Event_Base + 0x100E;
	public static final int ET_NR_ENDC_HANDOVER_REQUEST		= ET_Custom_Event_Base + 0x1006;
	public static final int ET_NR_ENDC_HANDOVER_SUCCESS		= ET_Custom_Event_Base + 0x1017;
	public static final int ET_NR_ENDC_HANDOVER_FAILURE		= ET_Custom_Event_Base + 0x1018;
	public static final int ET_NR_SNCHANGE_REQUEST		= ET_Custom_Event_Base + 0x1019;
	public static final int ET_NR_SNCHANGE_SUCCESS		= ET_Custom_Event_Base + 0x101A;
	public static final int ET_NR_SNCHANGE_FAILURE		= ET_Custom_Event_Base + 0x101B;
	public static final int ET_NR_EVENT_A1		= ET_Custom_Event_Base + 0x101C;
	public static final int ET_NR_EVENT_A2		= ET_Custom_Event_Base + 0x101D;
	public static final int ET_NR_EVENT_A3		= ET_Custom_Event_Base + 0x101E;
	public static final int ET_NR_EVENT_A4		= ET_Custom_Event_Base + 0x101F;
	public static final int ET_NR_EVENT_A5		= ET_Custom_Event_Base + 0x1020;
	public static final int ET_NR_EVENT_A6		= ET_Custom_Event_Base + 0x1021;
	public static final int ET_NR_BEAM_CHANGE_COMPLETE		= ET_Custom_Event_Base + 0x1025;
	public static final int ET_NR_CELL_ADDCOMPLETE		= ET_Custom_Event_Base + 0x1026;



	// *********************虚拟事件，不存储*************************
	public static final int ET_Virtual_Event_Base = 0x40000;

	/*
	 * public static final int
	 * ET_QChatMediaPacketOutBound=ET_Virtual_Event_Base; public static final
	 * int ET_QChatMediaPacketInBound=ET_Virtual_Event_Base+0x1;
	 */

	// 信令事件类型定义
	public static final int ET_Wimax_Msg_Event_Base = 0x20500;
	public static final int ET_Wimax_RNG_REQ = ET_Wimax_Msg_Event_Base + 1;
	public static final int ET_Wimax_RNG_RSP = ET_Wimax_Msg_Event_Base + 2;
	public static final int ET_Wimax_SBC_REQ = ET_Wimax_Msg_Event_Base + 3;
	public static final int ET_Wimax_SBC_RSP = ET_Wimax_Msg_Event_Base + 4;
	public static final int ET_Wimax_PKMv2_EAP_Transfer_REQ = ET_Wimax_Msg_Event_Base + 5;
	public static final int ET_Wimax_PKMv2_SA_TEK_Response = ET_Wimax_Msg_Event_Base + 6;
	public static final int ET_Wimax_REG_REQ = ET_Wimax_Msg_Event_Base + 7;
	public static final int ET_Wimax_REG_RSP = ET_Wimax_Msg_Event_Base + 8;
	public static final int ET_Wimax_DSA_REQ = ET_Wimax_Msg_Event_Base + 9;
	public static final int ET_Wimax_DSA_ACK = ET_Wimax_Msg_Event_Base + 10;
	public static final int ET_Wimax_DREG_REQ = ET_Wimax_Msg_Event_Base + 11;
	public static final int ET_Wimax_DREG_CMD = ET_Wimax_Msg_Event_Base + 12;
	public static final int ET_Wimax_MOB_MSHO_REQ = ET_Wimax_Msg_Event_Base + 13;
	public static final int ET_Wimax_MOB_MSHO_RSP = ET_Wimax_Msg_Event_Base + 14;
	public static final int ET_Wimax_MOB_HO_IND = ET_Wimax_Msg_Event_Base + 15;
	// 自定义事件类型定义
	public static final int ET_Wimax_Custom_Event_Base = 0x30500;
	public static final int ET_Wimax_InitialAccessAttempt = ET_Wimax_Custom_Event_Base + 1;
	public static final int ET_Wimax_RangingRequest = ET_Wimax_Custom_Event_Base + 2;
	public static final int ET_Wimax_RangingSuccess = ET_Wimax_Custom_Event_Base + 3;
	public static final int ET_Wimax_RangingFailure = ET_Wimax_Custom_Event_Base + 4;
	public static final int ET_Wimax_SBC_Request = ET_Wimax_Custom_Event_Base + 5;
	public static final int ET_Wimax_SBC_Success = ET_Wimax_Custom_Event_Base + 6;
	public static final int ET_Wimax_SBC_Failure = ET_Wimax_Custom_Event_Base + 7;
	public static final int ET_Wimax_AuthenticationRequst = ET_Wimax_Custom_Event_Base + 8;
	public static final int ET_Wimax_AuthenticationSuccess = ET_Wimax_Custom_Event_Base + 9;
	public static final int ET_Wimax_AuthenticationFailure = ET_Wimax_Custom_Event_Base + 10;
	public static final int ET_Wimax_RegisterRequest = ET_Wimax_Custom_Event_Base + 11;
	public static final int ET_Wimax_RegisterSuccess = ET_Wimax_Custom_Event_Base + 12;
	public static final int ET_Wimax_RegisterFailure = ET_Wimax_Custom_Event_Base + 13;
	public static final int ET_Wimax_FlowRequest = ET_Wimax_Custom_Event_Base + 14;
	public static final int ET_Wimax_FlowSuccess = ET_Wimax_Custom_Event_Base + 15;
	public static final int ET_Wimax_FlowFailure = ET_Wimax_Custom_Event_Base + 16;
	public static final int ET_Wimax_InitialAccessSuccess = ET_Wimax_Custom_Event_Base + 17;
	public static final int ET_Wimax_InitialAccessFailure = ET_Wimax_Custom_Event_Base + 18;
	public static final int ET_Wimax_AccessSuccess = ET_Wimax_Custom_Event_Base + 19;
	public static final int ET_Wimax_AccessFailure = ET_Wimax_Custom_Event_Base + 20;
	public static final int ET_Wimax_HORequest = ET_Wimax_Custom_Event_Base + 21;
	public static final int ET_Wimax_HOResponse = ET_Wimax_Custom_Event_Base + 22;
	public static final int ET_Wimax_HOReject = ET_Wimax_Custom_Event_Base + 23;
	public static final int ET_Wimax_MDHOAndFBSSResponse = ET_Wimax_Custom_Event_Base + 24;
	public static final int ET_Wimax_HONormalIndicator = ET_Wimax_Custom_Event_Base + 25;
	public static final int ET_Wimax_HOCancelIndicator = ET_Wimax_Custom_Event_Base + 26;
	public static final int ET_Wimax_HORejectIndicator = ET_Wimax_Custom_Event_Base + 27;
	public static final int ET_Wimax_TargetBSRangingRequest = ET_Wimax_Custom_Event_Base + 28;
	public static final int ET_Wimax_TargetBSRangingSuccess = ET_Wimax_Custom_Event_Base + 29;
	public static final int ET_Wimax_MSNormalRelease = ET_Wimax_Custom_Event_Base + 30;
	public static final int ET_Wimax_MSAbnormalRelease = ET_Wimax_Custom_Event_Base + 31;
	public static final int ET_Wimax_BSNormalRelease = ET_Wimax_Custom_Event_Base + 32;
	public static final int ET_Wimax_BSAbnormalRelease = ET_Wimax_Custom_Event_Base + 33;

	// *********************************************第三方格式的事件Flag定义
	// CTI格式文件事件
	public static final int ET_CTI_Base = 0x50000;

	// 无线事件(CDMA/1xEVDO)
	public static final int ET_CTI_Soft_Handoff_Attempt = ET_CTI_Base + 0x1060;
	public static final int ET_CTI_Soft_Handoff_Success = ET_CTI_Base + 0x1061;
	public static final int ET_CTI_Soft_Handoff_Fail = ET_CTI_Base + 0x1062;
	public static final int ET_CTI_Idle_Mode = ET_CTI_Base + 0x1063;
	public static final int ET_CTI_Dedicated_Mode = ET_CTI_Base + 0x1064;
	public static final int ET_CTI_Cell_Reselect = ET_CTI_Base + 0x1065;
	public static final int ET_CTI_PagingZone_Update = ET_CTI_Base + 0x1066;
	public static final int ET_CTI_PagingZone_Update_Failure = ET_CTI_Base + 0x1067;
	public static final int ET_CTI_SID_Area_Update = ET_CTI_Base + 0x1068;
	public static final int ET_CTI_SID_Area_Update_Fail = ET_CTI_Base + 0x1069;
	public static final int ET_CTI_EVDO_Connection_Attempt = ET_CTI_Base + 0x106A;
	public static final int ET_CTI_EVDO_Connection_Fail = ET_CTI_Base + 0x106B;
	public static final int ET_CTI_EVDO_Connection_Success = ET_CTI_Base + 0x106C;
	public static final int ET_CTI_EVDO_Connection_Close = ET_CTI_Base + 0x106D;
	public static final int ET_CTI_EVDO_Connection_Drop = ET_CTI_Base + 0x106E; // DO连接中断
	public static final int ET_CTI_EVDO_Reverse_Soft_Handoff_Request = ET_CTI_Base + 0x106F;
	public static final int ET_CTI_EVDO_Reverse_Soft_Handoff_Success = ET_CTI_Base + 0x1070;
	public static final int ET_CTI_Virtual_Soft_Handoff_Request = ET_CTI_Base + 0x1071;
	public static final int ET_CTI_Virtual_Soft_Handoff_Success = ET_CTI_Base + 0x1072;
	public static final int ET_CTI_Virtual_Soft_Handoff_Failure = ET_CTI_Base + 0x1073;
	public static final int ET_CTI_DOto1X_Handoff_Request_Active = ET_CTI_Base + 0x1074;
	public static final int ET_CTI_DOto1X_Handoff_Success_Active = ET_CTI_Base + 0x1075;
	public static final int ET_CTI_DOto1X_Handoff_Request_Dormant = ET_CTI_Base + 0x1076;
	public static final int ET_CTI_DOto1X_Handoff_Success_Dormant = ET_CTI_Base + 0x1077;
	public static final int ET_CTI_1XtoDO_Handoff_Request_Dormant = ET_CTI_Base + 0x1078;
	public static final int ET_CTI_1XtoDO_Handoff_Success_Dormant = ET_CTI_Base + 0x1079;
	public static final int ET_CTI_OutofService = ET_CTI_Base + 0x107A;

	// 语音
	public static final int ET_CTI_MO_Dial = ET_CTI_Base + 0x1000;
	public static final int ET_CTI_MO_Attempt = ET_CTI_Base + 0x1001;
	public static final int ET_CTI_MO_Alerting = ET_CTI_Base + 0x1002;
	public static final int ET_CTI_MO_Connected = ET_CTI_Base + 0x1003;
	public static final int ET_CTI_MO_Failure = ET_CTI_Base + 0x1004; // 未接通
	public static final int ET_CTI_MO_Hangup = ET_CTI_Base + 0x1005;
	public static final int ET_CTI_MO_Complete = ET_CTI_Base + 0x1006; // CallEnd
	public static final int ET_CTI_MO_Droped = ET_CTI_Base + 0x1007;

	public static final int ET_CTI_MT_Attempt = ET_CTI_Base + 0x1010;
	public static final int ET_CTI_MT_Alerting = ET_CTI_Base + 0x1011;
	public static final int ET_CTI_MT_Connected = ET_CTI_Base + 0x1012;
	public static final int ET_CTI_MT_Failure = ET_CTI_Base + 0x1013; // 未接通
	public static final int ET_CTI_MT_Complete = ET_CTI_Base + 0x1014; // CallEnd
	public static final int ET_CTI_MT_Droped = ET_CTI_Base + 0x1015;

	public static final int ET_CTI_MOS_ValueTooLow = ET_CTI_Base + 0x1020;
	public static final int ET_CTI_RecordBeforeCall = ET_CTI_Base + 0x1021;
	public static final int ET_CTI_MOS_SingleMute = ET_CTI_Base + 0x1022;
	public static final int ET_CTI_MOS_Whistle = ET_CTI_Base + 0x1023;

	public static final int ET_CTI_PPPDial_Start = ET_CTI_Base + 0x1301;
	public static final int ET_CTI_PPPDial_Success = ET_CTI_Base + 0x1302;
	public static final int ET_CTI_PPPDial_Fail = ET_CTI_Base + 0x1303;
	public static final int ET_CTI_PPPDial_Hangup = ET_CTI_Base + 0x1304;
	public static final int ET_CTI_PPPDial_Drop = ET_CTI_Base + 0x1305;

	public static final int ET_CTI_WAPLogin_GatewayRequest = ET_CTI_Base + 0x1310;
	public static final int ET_CTI_WAPLogin_GatewaySuccess = ET_CTI_Base + 0x1311;
	public static final int ET_CTI_WAPLogin_GatewayFailure = ET_CTI_Base + 0x1312;
	public static final int ET_CTI_WAPLogin_Attempt = ET_CTI_Base + 0x1313;
	public static final int ET_CTI_WAPLogin_Success = ET_CTI_Base + 0x1314;
	public static final int ET_CTI_WAPLogin_Fail = ET_CTI_Base + 0x1315;
	public static final int ET_CTI_WAPRefresh_Attempt = ET_CTI_Base + 0x1316;
	public static final int ET_CTI_WAPRefresh_Success = ET_CTI_Base + 0x1317;
	public static final int ET_CTI_WAPRefresh_Fail = ET_CTI_Base + 0x1318;
	public static final int ET_CTI_WAPDown_GatewayRequest = ET_CTI_Base + 0x1319;
	public static final int ET_CTI_WAPDown_GatewaySuccess = ET_CTI_Base + 0x131A;
	public static final int ET_CTI_WAPDown_GatewayFailure = ET_CTI_Base + 0x131B;
	public static final int ET_CTI_WAPDown_Attempt = ET_CTI_Base + 0x131C;
	public static final int ET_CTI_WAPDown_Success = ET_CTI_Base + 0x131D;
	public static final int ET_CTI_WAPDown_Fail = ET_CTI_Base + 0x131E;

	public static final int ET_CTI_FTPDown_ConStart = ET_CTI_Base + 0x1320;
	public static final int ET_CTI_FTPDown_ConSuccess = ET_CTI_Base + 0x1321;
	public static final int ET_CTI_FTPDown_ConFailure = ET_CTI_Base + 0x1322;
	public static final int ET_CTI_FTPDown_SerLoginAttempt = ET_CTI_Base + 0x1323;
	public static final int ET_CTI_FTPDown_SerLoginSuccess = ET_CTI_Base + 0x1324;
	public static final int ET_CTI_FTPDown_SerLoginFailure = ET_CTI_Base + 0x1325;
	public static final int ET_CTI_FTPDown_Start = ET_CTI_Base + 0x1326;
	public static final int ET_CTI_FTPDown_Success = ET_CTI_Base + 0x1327;
	public static final int ET_CTI_FTPDown_Drop = ET_CTI_Base + 0x1328;

	public static final int ET_CTI_FTPUp_ConStart = ET_CTI_Base + 0x1330;
	public static final int ET_CTI_FTPUp_ConSuccess = ET_CTI_Base + 0x1331;
	public static final int ET_CTI_FTPUp_ConFailure = ET_CTI_Base + 0x1332;
	public static final int ET_CTI_FTPUp_SerLoginAttempt = ET_CTI_Base + 0x1333;
	public static final int ET_CTI_FTPUp_SerLoginSuccess = ET_CTI_Base + 0x1334;
	public static final int ET_CTI_FTPUp_SerLoginFailure = ET_CTI_Base + 0x1335;
	public static final int ET_CTI_FTPUp_Start = ET_CTI_Base + 0x1336;
	public static final int ET_CTI_FTPUp_Success = ET_CTI_Base + 0x1337;
	public static final int ET_CTI_FTPUp_Drop = ET_CTI_Base + 0x1338;

	public static final int ET_CTI_Ping_Start = ET_CTI_Base + 0x1370;
	public static final int ET_CTI_Ping_Success = ET_CTI_Base + 0x1371;
	public static final int ET_CTI_Ping_Failure = ET_CTI_Base + 0x1372;

	public static final int ET_CTI_HTTPPage_Start = ET_CTI_Base + 0x13B0;
	public static final int ET_CTI_HTTPPage_Success = ET_CTI_Base + 0x13B1;
	public static final int ET_CTI_HTTPPage_Failure = ET_CTI_Base + 0x13B2;
	public static final int ET_CTI_HTTPPage_UrlRedirect = ET_CTI_Base + 0x13B3;

	public static final int ET_CTI_HTTPDown_Start = ET_CTI_Base + 0x1340;
	public static final int ET_CTI_HTTPDown_Success = ET_CTI_Base + 0x1341;
	public static final int ET_CTI_HTTPDown_Drop = ET_CTI_Base + 0x1342;
	public static final int ET_CTI_HTTPDown_UrlRedirect = ET_CTI_Base + 0x1343;

	public static final int ET_CTI_EMAILPOP3_Start = ET_CTI_Base + 0x1350;
	public static final int ET_CTI_EMAILPOP3_Success = ET_CTI_Base + 0x1351;
	public static final int ET_CTI_EMAILPOP3_Drop = ET_CTI_Base + 0x1352;

	public static final int ET_CTI_EMAILSMTP_Start = ET_CTI_Base + 0x1360;
	public static final int ET_CTI_EMAILSMTP_Success = ET_CTI_Base + 0x1361;
	public static final int ET_CTI_EMAILSMTP_Drop = ET_CTI_Base + 0x1362;

	public static final int ET_CTI_SMSSend_Requst = ET_CTI_Base + 0x1380;
	public static final int ET_CTI_SMSSend_Success = ET_CTI_Base + 0x1381;
	public static final int ET_CTI_SMSSend_Failure = ET_CTI_Base + 0x1382;
	public static final int ET_CTI_SMSReport_Success = ET_CTI_Base + 0x1383;
	public static final int ET_CTI_SMSReport_Failure = ET_CTI_Base + 0x1384;
	public static final int ET_CTI_SMSReceive_Success = ET_CTI_Base + 0x1385;
	public static final int ET_CTI_SMSReceive_Failure = ET_CTI_Base + 0x1386;

	public static final int ET_CTI_MMSSend_WSPRequest = ET_CTI_Base + 0x1390;
	public static final int ET_CTI_MMSSend_WSPSuccess = ET_CTI_Base + 0x1391;
	public static final int ET_CTI_MMSSend_WSPFailure = ET_CTI_Base + 0x1392;
	public static final int ET_CTI_MMSSend_Request = ET_CTI_Base + 0x1393;
	public static final int ET_CTI_MMSSend_Success = ET_CTI_Base + 0x1394;
	public static final int ET_CTI_MMSSend_Failure = ET_CTI_Base + 0x1395;
	public static final int ET_CTI_MMSPush_Received = ET_CTI_Base + 0x1396;
	public static final int ET_CTI_MMSPush_Timeout = ET_CTI_Base + 0x1397;
	public static final int ET_CTI_MMSReceive_WSPRequest = ET_CTI_Base + 0x1398;
	public static final int ET_CTI_MMSReceive_WSPSuccess = ET_CTI_Base + 0x1399;
	public static final int ET_CTI_MMSReceive_WSPFailure = ET_CTI_Base + 0x139A;
	public static final int ET_CTI_MMSReceive_Request = ET_CTI_Base + 0x139B;
	public static final int ET_CTI_MMSReceive_Success = ET_CTI_Base + 0x139C;
	public static final int ET_CTI_MMSReceive_Failure = ET_CTI_Base + 0x139D;

	public static final int ET_CTI_VideoStream_Start = ET_CTI_Base + 0x13C0;
	public static final int ET_CTI_VideoStream_FirData = ET_CTI_Base + 0x13C1;
	public static final int ET_CTI_VideoStream_RepStart = ET_CTI_Base + 0x13C2;
	public static final int ET_CTI_VideoStream_RepEnd = ET_CTI_Base + 0x13C3;
	public static final int ET_CTI_VideoStream_ReqSuccess = ET_CTI_Base + 0x13C4;
	public static final int ET_CTI_VideoStream_ReqFailure = ET_CTI_Base + 0x13C5;
	public static final int ET_CTI_VideoStream_Finished = ET_CTI_Base + 0x13C6;
	public static final int ET_CTI_VideoStream_Drop = ET_CTI_Base + 0x13C7;

	public static final int ET_CTI_TraceRoute_Start = ET_CTI_Base + 0x13D0;
	public static final int ET_CTI_TraceRoute_Reply = ET_CTI_Base + 0x13D1;
	public static final int ET_CTI_TraceRoute_Timeout = ET_CTI_Base + 0x13D2;
	public static final int ET_CTI_TraceRoute_Success = ET_CTI_Base + 0x13D3;
	public static final int ET_CTI_TraceRoute_Failure = ET_CTI_Base + 0x13D4;
	public static final int ET_CTI_TraceRoute_ReslvError = ET_CTI_Base + 0x13D5;

	public static final int ET_CTI_gpsOne_Request = ET_CTI_Base + 0x13E0;
	public static final int ET_CTI_gpsOne_Response = ET_CTI_Base + 0x13E1;
	public static final int ET_CTI_gpsOne_Success = ET_CTI_Base + 0x13E2;
	public static final int ET_CTI_gpsOne_Failure = ET_CTI_Base + 0x13E3;
	public static final int ET_CTI_gpsOne_Deviation_OutOfThreshold = ET_CTI_Base + 0x13E4;
	public static final int ET_CTI_gpsOne_Delay_OutOfThreshold = ET_CTI_Base + 0x13E5;
	public static final int ET_CTI_gpsOne_PassiveRequest = ET_CTI_Base + 0x13E6;
	public static final int ET_CTI_gpsOne_PassiveSuccess = ET_CTI_Base + 0x13E7;
	public static final int ET_CTI_gpsOne_PassiveFailure = ET_CTI_Base + 0x13E8;
	public static final int ET_CTI_gpsOne_Success_Deviation = ET_CTI_Base + 0x13E9;
	public static final int ET_CTI_gpsOne_Success_Delay = ET_CTI_Base + 0x13EA;

	public static final int ET_CTI_PTTStart = ET_CTI_Base + 0x13F0;
	public static final int ET_CTI_PTTEnd = ET_CTI_Base + 0x13F1;
	public static final int ET_CTI_PTTInitialAttempt = ET_CTI_Base + 0x13F2;
	public static final int ET_CTI_PTTInitialSuccess = ET_CTI_Base + 0x13F3;
	public static final int ET_CTI_PTTInitialFailure = ET_CTI_Base + 0x13F4;
	public static final int ET_CTI_PTTInCall_Attempt = ET_CTI_Base + 0x13F5;
	public static final int ET_CTI_PTTInCall_Success = ET_CTI_Base + 0x13F6;
	public static final int ET_CTI_PTTInCall_Failure = ET_CTI_Base + 0x13F7;
	public static final int ET_CTI_PTTTermin_Success = ET_CTI_Base + 0x13F8;
	public static final int ET_CTI_PTTTermin_Failure = ET_CTI_Base + 0x13F9;
	public static final int ET_CTI_PTTMedia_Delay = ET_CTI_Base + 0x13FA;

	public static final int ET_CTI_QChatRegStart = ET_CTI_Base + 0x1400;
	public static final int ET_CTI_QChatRegSuccess = ET_CTI_Base + 0x1401;
	public static final int ET_CTI_QChatRegFailure = ET_CTI_Base + 0x1402;
	public static final int ET_CTI_QChatDeregStart = ET_CTI_Base + 0x1403;
	public static final int ET_CTI_QChatDeregSuccess = ET_CTI_Base + 0x1404;
	public static final int ET_CTI_QChatDeregFailure = ET_CTI_Base + 0x1405;
	// 2013-02-18 liulieshan 信令事件使用我们自己定义的 ------------------
	public static final int ET_CTI_QChatStream1Request = ET_CTI_Base + 0x1406;
	public static final int ET_CTI_QChatStream1Accept = ET_CTI_Base + 0x1407;
	public static final int ET_CTI_QChatStream1Reject = ET_CTI_Base + 0x1408;
	public static final int ET_CTI_QChatStream2Request = ET_CTI_Base + 0x1409;
	public static final int ET_CTI_QChatStream2Accept = ET_CTI_Base + 0x140A;
	public static final int ET_CTI_QChatStream2Reject = ET_CTI_Base + 0x140B;
	public static final int ET_CTI_QChatStream3Request = ET_CTI_Base + 0x140C;
	public static final int ET_CTI_QChatStream3Accept = ET_CTI_Base + 0x140D;
	public static final int ET_CTI_QChatStream3Reject = ET_CTI_Base + 0x140E;
	// -------------------------------------------------------------------
	public static final int ET_CTI_QChatInitAttempt = ET_CTI_Base + 0x140F;
	public static final int ET_CTI_QChatInitSuccess = ET_CTI_Base + 0x1410;
	public static final int ET_CTI_QChatInitFailure = ET_CTI_Base + 0x1411;
	public static final int ET_CTI_QChatInCallAttempt = ET_CTI_Base + 0x1412;
	public static final int ET_CTI_QChatInCallSuccess = ET_CTI_Base + 0x1413;
	public static final int ET_CTI_QChatInCallFailure = ET_CTI_Base + 0x1414;
	public static final int ET_CTI_End = 0x5FFFF;

	// DTLog数据 begin2---------------
	public static final int ET_DTLog_Base = 0x60000;
	// Connect(LTE特有)
	public static final int ET_DTLog_LTEConnectStart = ET_DTLog_Base + 0x01;
	public static final int ET_DTLog_LTEConnectSuccess = ET_DTLog_Base + 0x02;
	public static final int ET_DTLog_LTEConnectFailure = ET_DTLog_Base + 0x03;
	public static final int ET_DTLog_LTEDisConnected = ET_DTLog_Base + 0x04;
	// HTTP
	public static final int ET_DTLog_HttpAttempt = ET_DTLog_Base + 0x09;
	public static final int ET_DTLog_HttpSuccess = ET_DTLog_Base + 0x0A;
	public static final int ET_DTLog_HttpFailure = ET_DTLog_Base + 0x0B;
	// SMS
	public static final int ET_DTLog_SMSSendAttempt = ET_DTLog_Base + 0x10;
	public static final int ET_DTLog_SMSSendSuccess = ET_DTLog_Base + 0x11;
	public static final int ET_DTLog_SMSSendFail = ET_DTLog_Base + 0x12;
	public static final int ET_DTLog_SMSReceiveAttempt = ET_DTLog_Base + 0x13;
	public static final int ET_DTLog_SMSReceiveSuccess = ET_DTLog_Base + 0x14;
	public static final int ET_DTLog_SMSReceiveFail = ET_DTLog_Base + 0x15;
	public static final int ET_DTLog_SMSP2PSuccess = ET_DTLog_Base + 0x16;
	public static final int ET_DTLog_SMSP2PFail = ET_DTLog_Base + 0x17;
	public static final int ET_DTLog_SMSSPSuccess = ET_DTLog_Base + 0x18; // 当前无对应RCU事件
	public static final int ET_DTLog_SMSSPFail = ET_DTLog_Base + 0x19; // 当前无对应RCU事件
	// MMS
	public static final int ET_DTLog_MMSSendAttempt = ET_DTLog_Base + 0x20;
	public static final int ET_DTLog_MMSSendSuccess = ET_DTLog_Base + 0x21;
	public static final int ET_DTLog_MMSSendFail = ET_DTLog_Base + 0x22;
	public static final int ET_DTLog_MMSReceiveAttempt = ET_DTLog_Base + 0x23;
	public static final int ET_DTLog_MMSReceiveSuccess = ET_DTLog_Base + 0x24;
	public static final int ET_DTLog_MMSReceiveFail = ET_DTLog_Base + 0x25;
	public static final int ET_DTLog_MMSDownloadAttempt = ET_DTLog_Base + 0x26; // 当前无对应RCU事件
	public static final int ET_DTLog_MMSDownloadSuccess = ET_DTLog_Base + 0x27; // 当前无对应RCU事件
	public static final int ET_DTLog_MMSDownloadFail = ET_DTLog_Base + 0x28; // 当前无对应RCU事件
	public static final int ET_DTLog_MMSPushMsgReceived = ET_DTLog_Base + 0x29;
	public static final int ET_DTLog_MMSPushMsgFail = ET_DTLog_Base + 0x2A;
	public static final int ET_DTLog_MMSP2PSuccess = ET_DTLog_Base + 0x2B; // 当前无对应RCU事件
	public static final int ET_DTLog_MMSP2PFail = ET_DTLog_Base + 0x2C; // 当前无对应RCU事件
	public static final int ET_DTLog_MMSSPSuccess = ET_DTLog_Base + 0x2D; // 当前无对应RCU事件
	public static final int ET_DTLog_MMSSPFail = ET_DTLog_Base + 0x2E; // 当前无对应RCU事件
	// 邮件
	public static final int ET_DTLog_MailSendAttempt = ET_DTLog_Base + 0x30; // SMTP
	public static final int ET_DTLog_MailSendSuccess = ET_DTLog_Base + 0x31;
	public static final int ET_DTLog_MailSendFailure = ET_DTLog_Base + 0x32;
	public static final int ET_DTLog_MailRecvAttempt = ET_DTLog_Base + 0x33; // POP3
	public static final int ET_DTLog_MailRecvSuccess = ET_DTLog_Base + 0x34;
	public static final int ET_DTLog_MailRecvFailure = ET_DTLog_Base + 0x35;
	public static final int ET_DTLog_Pop3LoginAttempt = ET_DTLog_Base + 0x36; // SockConnecting
	public static final int ET_DTLog_Pop3LoginSuccess = ET_DTLog_Base + 0x37;
	public static final int ET_DTLog_Pop3LoginFailure = ET_DTLog_Base + 0x38;
	// 飞信
	public static final int ET_DTLog_FetionLogonSuccess = ET_DTLog_Base + 0x40;
	public static final int ET_DTLog_FetionLogonFailure = ET_DTLog_Base + 0x41;
	public static final int ET_DTLog_FetionMsgFromSMSSuccess = ET_DTLog_Base + 0x42;
	public static final int ET_DTLog_FetionMsgFromSMSFailure = ET_DTLog_Base + 0x43;
	public static final int ET_DTLog_FetionSMSReceieved = ET_DTLog_Base + 0x44;
	public static final int ET_DTLog_FetionMsgToSMSSuccess = ET_DTLog_Base + 0x45;
	public static final int ET_DTLog_FetionMsgToSMSFailure = ET_DTLog_Base + 0x46;
	// 流媒体(VideoPlay视频播放)
	public static final int ET_DTLog_StreamingPlayAttempt = ET_DTLog_Base + 0x50;
	public static final int ET_DTLog_StreamingPlaySuccess = ET_DTLog_Base + 0x51;
	public static final int ET_DTLog_StreamingPlayFailure = ET_DTLog_Base + 0x52;
	

	public static final int ET_DTLog_End = 0x6FFFF;

	// PBM
	public static final int ET_PBM_Start = 0x1060;
	public static final int ET_PBM_Finish = 0x1061;
	public static final int ET_PBM_Failure = 0x1062;

	//UDP
	public static final int ET_UDP_Connect_Start = 0x1310;
	public static final int ET_UDP_Connect_Success = 0x1311;
	public static final int ET_UDP_Connect_Failure = 0x1312;
	public static final int ET_UDP_Start = 0x1313;
	public static final int ET_UDP_End = 0x1314;
	public static final int ET_UDP_Finish = 0x1316;
	public static final int ET_UDP_Failure = 0x1001F;
	public static final int ET_UDP_Disconnect = 0x1318;

	//OpenSignal业务
	public static final int ET_OpenSignal_SockConnecting = 0x1320;
	public static final int ET_OpenSignal_ConnectSockSucc = 0x1321;
	public static final int ET_OpenSignal_ConnectSockFaile = 0x1322;
	public static final int ET_OpenSignal_Ping_Start = 0x1323;
	public static final int ET_OpenSignal_Ping_Suc = 0x1324;
	public static final int ET_OpenSignal_DL_Start = 0x1326;
	public static final int ET_OpenSignal_DL_Suc = 0x1327;
	public static final int ET_OpenSignal_UL_Start = 0x132A;
	public static final int ET_OpenSignal_UL_Suc = 0x132B;
	public static final int ET_OpenSignal_Finish = 0x132E;
	public static final int ET_OpenSignal_Fail = 0x132F;
	public static final int ET_OpenSignal_SocketDisconnected  = 0x1331;


	//MultiHttpDownload
	public static final int ET_Multi_HTTP_Down_Start  = 0x1301;
	public static final int ET_Multi_HTTP_Down_First_Data  = 0x1302;
	public static final int ET_Multi_HTTP_Down_Last_Data  = 0x1303;
	public static final int ET_Multi_HTTP_Down_Drop  = 0x1304;
	public static final int ET_Multi_HTTP_Down_Failure  = 0x1305;


	//DETACH
	public static final int ET_LTE_Detach_Request = 0x0139;
	public static final int ET_LTE_Detach_Accept  = 0x013A;
	public static final int ET_LTE_Detach_Failure  = 0x013B;
	// Weibo
	public static final int ET_WeiBo_Start = 0x1079;
	public static final int ET_WeiBo_ActionStart = 0x1073;
	public static final int ET_WeiBo_ActionSuccess = 0x1074;
	public static final int ET_WeiBo_ActionFailure = 0x1075;
	public static final int ET_WeiBo_Finish = 0x107A;
	
	//idle
	public static final int ET_Idle_Test_Start					= 0x10C0;
	public static final int ET_Idle_Test_End					= 0x10C1;
	
	//用于显示,当前如果为并发,数据业务起来前,如果当前网络无效,则写入该信息10S一次
	public static final int ET_DataService_Msg	= 0x00000199;

	public static final int ET_WECALL_MO_DIAL 						   = 0x10D0;
	public static final int ET_WECALL_MO_ATTEMPT                       = 0x10D1;
	public static final int ET_WECALL_MO_SETUP                         = 0x10D2;
	public static final int ET_WECALL_MO_ESTABLISH                     = 0x10D3;
	public static final int ET_WECALL_MO_BLOCK                         = 0x10D4;
	public static final int ET_WECALL_MO_END                           = 0x10D5;
	public static final int ET_WECALL_MO_DROP                          = 0x10D6;
	public static final int ET_WECALL_MO_HANGUP                        = 0x10D7;

	public static final int ET_WECALL_MT_DIAL                          = 0x10D8;
	public static final int ET_WECALL_MT_ATTEMPT                       = 0x10D9;
	public static final int ET_WECALL_MT_SETUP                         = 0x10DA;
	public static final int ET_WECALL_MT_ESTABLISH                     = 0x10DB;
	public static final int ET_WECALL_MT_BLOCK                         = 0x10DC;
	public static final int ET_WECALL_MT_END                           = 0x10DD;
	public static final int ET_WECALL_MT_DROP                          = 0x10DE;
	public static final int ET_WECALL_MT_HANGUP                        = 0x10DF;

	public static final int ET_Multiple_Skype_Test_Start				= 0x1360;
	public static final int ET_Multiple_Skype_Action_Start				= 0x1361;
	public static final int ET_Multiple_Skype_Action_Success			= 0x1362;
	public static final int ET_Multiple_Skype_Action_Failure			= 0x1363;
	public static final int ET_Multiple_Skype_Test_Success				= 0x1364;
	public static final int ET_Multiple_Skype_Test_Failure				= 0x1365;

    public static final int ET_Multiple_App_QQ_Start				    = 0x1380;
    public static final int ET_Multiple_App_QQ_Action_Start				= 0x1381;
    public static final int ET_Multiple_App_QQ_Action_Success			= 0x1382;
    public static final int ET_Multiple_App_QQ_Action_Failure			= 0x1383;
    public static final int ET_Multiple_App_QQ_End				        = 0x1384;

    public static final int ET_Multiple_App_Sina_Weibo_Start		    = 0x1390;
    public static final int ET_Multiple_App_Sina_Weibo_Action_Start	    = 0x1391;
    public static final int ET_Multiple_App_Sina_Weibo_Action_Success   = 0x1392;
    public static final int ET_Multiple_App_Sina_Weibo_Action_Failure   = 0x1393;
    public static final int ET_Multiple_App_Sina_Weibo_End				= 0x1394;

    public static final int ET_Multiple_App_Facebook_Start		        = 0x13E0;
    public static final int ET_Multiple_App_Facebook_Action_Start	    = 0x13E1;
    public static final int ET_Multiple_App_Facebook_Action_Success     = 0x13E2;
    public static final int ET_Multiple_App_Facebook_Action_Failed      = 0x13E3;
    public static final int ET_Multiple_App_Facebook_End				= 0x13E4;

    public static final int ET_Multiple_App_Instagram_Start		        = 0x13F0;
    public static final int ET_Multiple_App_Instagram_Action_Start	    = 0x13F1;
    public static final int ET_Multiple_App_Instagram_Action_Success    = 0x13F2;
    public static final int ET_Multiple_App_Instagram_Action_Failed     = 0x13F3;
    public static final int ET_Multiple_App_Instagram_End				= 0x13F4;

    public static final int ET_WhatsApp_Test_Start		                = 0x13B0;
    public static final int ET_WhatsApp_Action_Start	                = 0x13B1;
    public static final int ET_WhatsApp_Action_Success                  = 0x13B2;
    public static final int ET_WhatsApp_Action_Failed                   = 0x13B3;
    public static final int ET_WhatsApp_Test_End				        = 0x13B4;
    public static final int ET_WhatsApp_Test_Success			        = 0x10304;
    public static final int ET_WhatsApp_Test_Failure			        = 0x10305;

    public static final int ET_WhatsApp_MO_Dial 						 = 0x13C0;
    public static final int ET_WhatsApp_MO_Attempt                       = 0x13C1;
    public static final int ET_WhatsApp_MO_Setup                         = 0x13C2;
    public static final int ET_WhatsApp_MO_Establish                     = 0x13C3;
    public static final int ET_WhatsApp_MO_Block                         = 0x13C4;
    public static final int ET_WhatsApp_MO_End                           = 0x13C5;
    public static final int ET_WhatsApp_MO_Drop                          = 0x13C6;
    public static final int ET_WhatsApp_MO_Hangup                        = 0x13C7;

    public static final int ET_WhatsApp_MT_Dial                          = 0x13C8;
    public static final int ET_WhatsApp_MT_Attempt                       = 0x13C9;
    public static final int ET_WhatsApp_MT_Setup                         = 0x13CA;
    public static final int ET_WhatsApp_MT_Establish                     = 0x13CB;
    public static final int ET_WhatsApp_MT_Block                         = 0x13CC;
    public static final int ET_WhatsApp_MT_End                           = 0x13CD;
    public static final int ET_WhatsApp_MT_Drop                          = 0x13CE;
    public static final int ET_WhatsApp_MT_Hangup                        = 0x13CF;

	
	// DTLog end2---------------

	// 是否为自定义事件码
	public static boolean IsCustomEventFlag(int AEventFlag) {
		// 高16位，0x0010至0x00FF之间，划定为各个产品的自定义事件
		return ((AEventFlag >= 0x00100000) && (AEventFlag <= 0x00FFFFFF));
	}

	// 新RCU事件，目前还没有2012-10-17
	public static boolean IsNewRCUEventFlag(int AEventFlag) {
		// 高16位，0x0010至0x00FF之间，划定为各个产品的自定义事件
		return ((AEventFlag >= 0x03000000) && (AEventFlag <= 0x7FFFFFFF));
	}
}
