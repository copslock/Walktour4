package com.walktour.model;

public class TcpModel {
	public String tcpPath;		//[256]Tcp服务器端口信息,如:tcp://:8201
	public String device;		//[32] 不填
	public String ftpMsg;		//[512]Ftp信息,格式为:ftp://用户名:密码@ftp服务器IP[:端口]/文件名;上传时文件名为上传到服务器上时的文件名,下载时为服务器上已有的文件名; 如:ftp://egprs:egprs123ftp@211.136.93.245/8M2.rar
	public int item;			//区分业务如ftp,http..
	public int command;			//区分item下的具体任务如ftp上传,下载..
	public String ftptestPath;	//ftp_test的存放路径,格式如: /data/data/com.walktour.gui/ftp_test
	public String stfile;		//上传时ftp断点续传文件路径,如/data/data/com.walktour.gui/ftp_test
	public int mode;			//模式,一般传0,pass模式,其它值为purt模式
	public int pth_num;			//线程数,一般为1,最大支持32
	public int connect_timeout;	//连接超时时间,一般为60,单位为秒
	public int nodata_timeout;	//无数据响应时间,20-180,一般为30.
	public String localfile;	//本地文件,上传时为本地文件的地址,如果为空则系统自动生成文件,下载时为断点续传文件的存放路径如:/data/data/com.walktour.gui/ftp_test
	public long upload_size=0;	//上传大小
	public int connect_count=3;	//登陆失败次数
	public int connect_inteval=15;	//重新登陆间隔
	
	/*wap测试时添加下面3参数,及上面的tcpPath,ftptestPath,command,item共4个参数即可*/
	public int port;			//wap端口,默认80
	public String gateway;		//wap网关 移动为:127.0.0.1
	public String wapurl;		//wapURL
	//wap刷新时添加下面两参数
	public int refreshcount;	//刷新次数
	public int deep;			//刷新层次
	
	//MMS测试添加下面6个参数，及上面的tcppath,ftptestPath,command,item,port,gateway,
	public int tid;
	public String mmscenter;	//彩信中心
	public String mmsagent;		//
	public String mmsnum;		//号码
	public String mmsitem;		//标题
	public String mmstext;		//文本
	public String mmsfile;		//文件
	
	//http测试添加以下参数，及上面的tcppath,ftptestPath,item,command,device,port
	public String httpurl;
	public String httpip;
}
