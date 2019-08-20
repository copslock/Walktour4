package com.walktour.Utils;

public interface OperaType {
	//此处为Item值
	public static int Type_IPC_CONFIG=1;	//2-0
	public static int Type_FETION_ITEM=2;	//2-1
	public static int Type_WAP_10=4;		//2-2
	public static int Type_WAP_20=8;		//2-3
	public static int Type_HTTP=16;			//2-4
	public static int Type_UIMAP=32;		//2-5
	public static int Type_FTP=64;			//2-6
	public static int Type_VIDEOSTREAM=128;	//2-7
	public static int TASK_END=16384;		//2-14
	public static int SHUTDOWN=32768;		//2-15
	
	//此处为Command
	public static int Command_LOGIN=1;			//2-0
	public static int Command_REFRESH=2;		//2-1
	public static int Command_DOWNLOAD=4;		//2-2
	public static int Command_UPLOAD=8;		//2-3
	public static int Command_MMS_SEND=16;		//2-4
	public static int Command_MMS_RECV=32;		//2-5
	public static int Command_UIMAP_SEND=64;	//2-6
	public static int Command_UIMAP_RECV=128;	//2-7
	
	//ftp base
	public static int FTP_CONNECT_BEGIN=1;		//begin connect server
	public static int FTP_CONNECT_SUCCESS=2;	//connect server success
	public static int FTP_CONNECT_FAILED=3;		//connect server fail
	
	public static int FTP_LOGIN_SUCCESS=4;		//login server success
	public static int FTP_LOGIN_FAILED=5;		//login server fail
	
	public static int FTP_FIRST_DATA=6;			//receive or send first
	public static int FTP_CUR_DATA=7;			//receiving or sending data
	public static int FTP_LAST_DATA=8;			//receive or send last data
	
	public static int FTP_DROP=9;				//connect drop
	public static int FTP_FILE_SIZE=10;			//get down file size
	public static int FTP_FILE_NOT_FOUND=11;	// upload or down file error
	public static int FTP_RETR_OR_STOR=12;		//send retr or stor command
	public static int FTP_PARSE_URL_ERROR=13;	//input url msg error
	public static int FTP_REQUEST=14;			//client send msg 
	public static int FTP_RESPONSE=15;			//server response msg
	
	public static int FTP_MKD_ERROR=16;			//client mkdir in server error
	public static int FTP_CWD_ERROR=17;			//cwd server dir
	public static int FTP_MALLOC_NULL=18;		//malloc is NULL
	public static int FTP_SERVER_NORESPONSE=19;	//server no response 连接失败，登陆失败，下载失败
	public static int FTP_FILE_SIZE_ZERO=20;	//upload or down file error
	//wap base
	public static int CONNECT_GATEWAY_BEGIN=1;
	public static int CONNECT_GATEWAY_SUCCESS=2;
	public static int CONNECT_GATEWAY_FAILED=3;
	public static int SEND_REQUEST_BEGIN = 4;
	public static int SEND_CUR_DATA = 5;
	public static int SEND_REQUEST_SUCCESS=6;
	public static int SEND_REQUEST_FAILED=7;
	public static int RECV_FIRST_DATA=8;
	public static int RECV_CUR_DATA=9;
	public static int RECV_LAST_DATA=10;
	public static int RECV_FAILED=11;
	public static int RECV_DATA=12;
	public static int URL_REDIRECT=13;
	public static int HYPERLINK_URL=14;
	public static int IMG_URL=15;
	public static int HTTP_REDIRECT=16;
	public static int REDIRECT_END=17;
	public static int WIFI_LOGIN_BEGIN=18;
	public static int WIFI_LOGIN_SUCCESS=19;
	public static int WIFI_LOGIN_FAILED=20;
	public static int WIFI_LOGOUT_BEGIN=21;
	public static int WIFI_LOGOUT_SUCCESS=22;
	public static int WIFI_LOGOUT_FAILED=23;
}
