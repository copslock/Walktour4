/**
 * com.dinglicom.btu
 * BtuEvent.java
 * 类功能：
 * 2014-3-24-下午4:17:59
 * 2014鼎利-版权所有
 * @author qihang.li@dinglicom.com
 */
package com.walktour.gui.task.parsedata.xml.btu.model;

import com.dinglicom.dataset.model.DataSetEvent;


/**
 * BtuEvent
 * RCU事件到BTU事件的转换,
 * 部分析事件是根据各自的ID号就能对应，
 * 部分BTU事件要区分网络类型，
 * 部分，多个BTU事件是根据一个RCU事件结构里的属性不同区分
 * 2014-3-24 下午4:17:59
 * 
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
public enum BtuEventCode {
	
	//TD语音
	TD_Outcoming_Call_Attempt(0x2901,DataSetEvent.ET_MO_Attempt ),
	TD_Outgoing_Call_Setup(0x2902),
	TD_RRC_Connection_Completed(0x2903),
	TD_RAB_Setup_Failure(0x2904),
	TD_RAB_Setup(0x2905),
	TD_RAB_Setup_Completed(0x2906),
	TD_Outcoming_Call_Alerting(0x2907,DataSetEvent.ET_MO_Alerting),
	TD_Outcoming_Call_Connected(0x2908,DataSetEvent.ET_MO_Connect),
	TD_Outcoming_Call_Failure(0x2909,DataSetEvent.ET_MO_Block),
	TD_Outcoming_Call_End(0x290A,DataSetEvent.ET_MO_End),
	TD_Outcoming_Dropped_Call(0x290B,DataSetEvent.ET_MO_Drop),
	TD_Incoming_Call_Attempt(0x290C,DataSetEvent.ET_MT_Attempt),
	TD_Incoming_Call_Alerting(0x290D,DataSetEvent.ET_MT_Alerting),
	TD_Incoming_Call_Connected(0x290E,DataSetEvent.ET_MT_Connect),
	TD_Incoming_Call_End(0x290F,DataSetEvent.ET_MT_End),
	TD_Incoming_Call_Failure(0x2910,DataSetEvent.ET_MT_Block),
	TD_Incoming_Dropped_Call(0x2911,DataSetEvent.ET_MT_Drop),
	
	//彩信
	Mms_Send_Attempt(0x2201,DataSetEvent.ET_MMS_Send_Start),
	Mms_Send_Success(0x2202,DataSetEvent.ET_MMS_Send_Success),
	Mms_Send_Fail(0x2203,DataSetEvent.ET_MMS_Send_Failure),
	Mms_Receive_Attempt(0x2204,DataSetEvent.ET_MMS_MNotification_Ind),
	Mms_Receive_Success(0x2205,DataSetEvent.ET_MMS_MNotifyResp_Ind),
	Mms_Receive_Fail(0x2206),
	Mms_Download_Attempt(0x2207),
	Mms_Download_Success(0x2208),
	Mms_Download_Fail(0x2209),
	MMS_push_msg_received(0x220A),
	MMS_push_msg_fail(0x220B),
	MMS_P2P_success(0x220C),
	MMS_P2P_fail(0x220D),
	MMS_SP_success(0x220E),
	MMS_SP_fail(0x220F),

	//Email
	Mail_Send_Attempt(0x2601,DataSetEvent.ET_Email_SMTP_SendCmd),
	Mail_Send_Success(0x2602,DataSetEvent.ET_Email_SMTP_LastData),
	Mail_Send_Fail(0x2603,DataSetEvent.ET_Email_SMTP_Failure),
	Mail_Send_Drop(0x2610,DataSetEvent.ET_Email_SMTP_Drop),
	
	Pop3_Login_Attempt(0x2607,DataSetEvent.ET_Email_POP3_Connect),
	Pop3_Login_Success(0x2608,DataSetEvent.ET_Email_POP3_LoginSuccess),
	Pop3_Login_Fail(0x2609,DataSetEvent.ET_Email_POP3_LoginFailure),
	
	Mail_Recv_Attempt(0x2604,DataSetEvent.ET_Email_POP3_SendCmd),
	Mail_Recv_Success(0x2605,DataSetEvent.ET_Email_POP3_LastData),
	Mail_Recv_Fail(0x2606,DataSetEvent.ET_Email_POP3_Failure),
	Mail_Recv_Drop(0x2611,DataSetEvent.ET_Email_POP3_Drop),
	
	//Http下载
	Http_Download_Attempt(0x2701,DataSetEvent.ET_HTTP_SendGetCmd),
	Http_Download_Fail(0x2702,DataSetEvent.ET_HTTP_Failure),
	Http_Download_Success(0x2703,DataSetEvent.ET_HTTP_LastData),
	Http_Download_Drop(0x2704,DataSetEvent.ET_HTTP_Drop),
	
	//LTE_FTP
	FTP_server_logon_success(0x4100,
			new int[]{DataSetEvent.ET_FTP_DL_LoginSuccess, DataSetEvent.ET_FTP_UP_LoginSuccess} ),
	FTP_server_logon_fail(0x4101,
			new int[]{DataSetEvent.ET_FTP_DL_LoginFailure, DataSetEvent.ET_FTP_UP_LoginFailure} ),
	Ftp_Download_Attempt(0x4102,DataSetEvent.ET_FTP_DL_SendRetrCmd),
	Ftp_Download_Fail(0x4103,DataSetEvent.ET_FTP_DL_Failure),
	Ftp_Download_Success(0x4104,DataSetEvent.ET_FTP_DL_LastData),
	Ftp_Upload_Attempt(0x4105,DataSetEvent.ET_FTP_UP_SendRetrCmd),
	Ftp_Upload_Fail(0x4106,DataSetEvent.ET_FTP_UP_Failure),
	Ftp_Upload_Success(0x4107,DataSetEvent.ET_FTP_UP_LastData),
	FTP_Download_Drop(0x4108,DataSetEvent.ET_FTP_DL_Drop),
	FTP_Download_Disconnet(0x4109,DataSetEvent.ET_FTP_DL_SockDisconnect),
	FTP_Upload_Drop(0x410A,DataSetEvent.ET_FTP_UP_DROP),
	FTP_Upload_Disconnect(0x410B,DataSetEvent.ET_FTP_UP_SockDisconnect),
	
	//Video_Stream
	Video_Stream_Start(0x4400),
	Video_Stream_First_Data(0x4401),
	Video_Stream_Reproduction_Start(0x4402),
	Video_Stream_Finished(0x4403),
	Video_Stream_Drop(0x4404),
	Video_Timer_Out_Happened(0x4405),
	Video_Telephony_Start(0x4406),
	Video_Telephony_Hungup(0x4407),
	Video_Stream_Reproduction_End(0x4408),//5155898
	
	//Ping
	Ping_Attempt(0x4409,DataSetEvent.ET_Ping_Start),
	Ping_Fail(0x440A,DataSetEvent.ET_Ping_NFail),
	Ping_Success(0x440B,DataSetEvent.ET_Ping_NSuccess),
	
	//Http浏览
	Http_Browse_Attempt(0x4510,DataSetEvent.ET_HttpPageSendGetCmd),
	Http_Browse_Fail(0x4511,DataSetEvent.ET_HttpPageFailure),
	Http_Browse_Success(0x4512,DataSetEvent.ET_HttpPageLastData),
	Http_Browse_Drop(0x4513,DataSetEvent.ET_HttpPageDrop),
	
	//并发
	Data_Recovery_Success(0x4514),
	Data_Recovery_Failure(0x4515);

	private final int btuCode;
	private final int rcuCodes[];
	
	
	private BtuEventCode(int btuCode){
		this.btuCode = btuCode;
		this.rcuCodes = new int[0];
	}
	
	/**
	 * 一对一BTU事件
	 * 一个BTU事件ID对应一个RCU事件ID
	 * @param btuCode
	 * @param rcuCode
	 */
	private BtuEventCode(int btuCode,int rcuCode){
		this.btuCode = btuCode;
		this.rcuCodes = new int[]{rcuCode};
	}
	
	/**
	 * 一对多BTU事件
	 * 一个BTU事件ID对应多个RCU事件ID
	 * @param btuCode
	 * @param rcuCode
	 */
	private BtuEventCode(int btuCode,int[] rcuCode){
		this.btuCode = btuCode;
		this.rcuCodes = rcuCode;
	}
	
	public int BTUCODE(){
		return btuCode;
	}
	public int RcuCode(){
		return rcuCodes[0];
	}
	public int[] RcuCodes(){
		return rcuCodes;
	}
}
