package com.dinglicom;

/**
 * 李方杰提供，只把native方法都改为static了
 *
 */
public class UnicomInterface {

	static
	{
		System.loadLibrary("UnicomInterface");
	}
	//reasone code
	public final static int     NO_ERROR	=				  0x00;
	public final static int     INVALID_ACCOUNT=			  0x1000;
	public final static int     SOCKET_DISCONNECT=		  0x1001;
	public final static int     ACCOUNT_HAS_FORBID=		  0x1002;
	public final static int     CONNECT_SERVER_FAILED=	  0x1003;
	public final static int     GET_RESOUCE_SERVER_FAILED=  0x1004;
	
	// 服务器类型
	public final static int     TASK_SERVER = 1;
	public final static int     FTP_UPLOAD_SERVER = 2;
	public final static int     FTP_DOWNLOAD_SERVER = 3;
	public final static int     HTTP_UPLOAD_SERVER = 4;
	public final static int     HTTP_DOWNLOAD_SERVER = 5;

	/***************************************************************************************************
	//函数功能： 获取资源服务器的配置
	//返回值  ： 资源服务器配置描述，xml格式
	//参数    ：
	//            server公开的ip地址，port公开的端口，type类型，定义如下：
					1	业务交互服务器
					2	Ftp上传服务器
					3	Ftp下载服务器
					4	Http上传服务器
					5	Http下载服务器

    //				 
	//说明    ： 该函数可以单独使用。如果没有业务交互服务器的地址，需要首先调用
	***************************************************************************************************/
	public native static String getResourceServer(String server, int port, int type);
	

	/***************************************************************************************************
	//函数功能： 初始化交互服务器信息
	//返回值  ： 成功或者失败，boolean类型
	//参数    ：
	//            server交互服务器地址，port交互服务器端口，username，password登录的账号

    //				 
	//说明    ： 在和业务服务器交互的时候，需要第一个被调用
	***************************************************************************************************/
	
	public native static boolean initTaskServer( String server,int port, String username,String password);
	/***************************************************************************************************
	//函数功能： 登录
	//返回值  ： 成功或者失败，boolean类型
	//参数    ：
	//            

    //				 
	//说明    ：该函数是可选的
	***************************************************************************************************/

	public native static boolean login();
	/***************************************************************************************************
	//函数功能： 获取工单字典
	//返回值  ： 工单字典描述，xml格式
	//参数    ：
	//            

    //				 
	//说明    ：需要从该xml获取codeid，传给工单列表的输入参数
	***************************************************************************************************/

	public native static String getWorkTypeDict();

/***************************************************************************************************
	//函数功能： 获取工单字典2
	//返回值  ： 工单字典描述，xml格式,描述里面包含工单个数
	//参数    ：
	//            

    //				 
	//说明    ：需要从该xml获取codeid，传给工单列表的输入参数
	***************************************************************************************************/

	public native static String getWorkTypeDict2();


	/***************************************************************************************************
	//函数功能： 获取工单详情
	//返回值  ： 工单详情描述，xml格式
	//参数    ：
	//            

    //				 
	//说明    ：工单详情里面包括执行任务的详细描述，输入参数需要从工单列表获得
	***************************************************************************************************/

	public native static String getWorkOrderDetail(int workid);
	/***************************************************************************************************
	//函数功能： 获取工单列表
	//返回值  ：工单列表描述，xml格式
	//参数    ：
	//            

    //				 
	//说明    ：输入参数需要从工单字典获得
	***************************************************************************************************/

	public native static String getWorkOrderList(int workType);
	
	/***************************************************************************************************
	//函数功能： 获取原因码
	//返回值  ：返回最近的原因码
	//参数    ：
	//            

    //				 
	//说明    ：
	***************************************************************************************************/

	public native static int getReasoneCode();

	/***************************************************************************************************
	//函数功能： 创建Log标签文件
	//返回值  ： 成功或者失败
	//参数    ：
	//            

    //				 
	//说明    ：任务开始的时候就可以记录了
	***************************************************************************************************/
	public native static boolean  createLoglabelFile(String filename,String content);
	
//	
//	/***************************************************************************************************
//	//函数功能： 写Log标签内容，参考接口文档中的Log标签部分的格式
//	//返回值  ： 成功或者失败
//	//参数    ：
//	//            
//
//    //				 
//	//说明    ：任务开始的时候就可以记录了
//	***************************************************************************************************/
//	public native static boolean  writeLoglabel(String filename);
//
//	/***************************************************************************************************
//	//函数功能： 关闭写Log标签
//	//返回值  ： 无
//	//参数    ：
//	//            
//
//    //				 
//	//说明    ：任务开始的时候就可以记录了
//	***************************************************************************************************/
//	public native static void  closeLogFile();
//
//	/***************************************************************************************************
//	//函数功能： 获得文件的MD5码
//	//返回值  ： MD5码（32位）
//	//参数    ：
//	//            
//
//    //				 
//	//说明    ：在写log标签的时候，需要将室内地图文件的MD5写到文件中
//	***************************************************************************************************/
	public native static String  getMD5(String filename);

	/***************************************************************************************************
	//函数功能： 释放与服务器的连接
	//返回值  ： 无
	//参数    ：
	//            

    //				 
	//说明    ：程序退出或者在OnDestroy的时候执行
	***************************************************************************************************/

	public native static void free();

	/***************************************************************************************************
	//函数功能： 主动断开与服务器的连接
	//返回值  ： 无
	//参数    ：
	//            

    //				 
	//说明    ：如果长时间得不到响应，比如连接，读数据，写数据的时候执行
	***************************************************************************************************/

	public native static void disconnect();
	
}
