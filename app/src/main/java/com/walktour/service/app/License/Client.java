package com.walktour.service.app.License;


/**
 * 
 * @author zhihui.lian
 * 
 *	LicenseServer 客户端获取服务端函数类  
 */

public class Client {
		
		
	static {
		// 加载基础库
		System.loadLibrary("crystax_shared");
		System.loadLibrary("gnustl_shared");
		System.loadLibrary("miniSDL");
		System.loadLibrary("ipc2");
		System.loadLibrary("iconv");
		System.loadLibrary("myiconv");
		System.loadLibrary("mysock");
		System.loadLibrary("mydns");
		System.loadLibrary("curl");
		System.loadLibrary("dthelper");
		System.loadLibrary("file_trans");
		System.loadLibrary("license_client");

	}
	
	/*
	  函数功能:  	初始化库
	      入参:		服务器地址，log文件（带路径），请求超时秒
	    返回值: 	0成功，其他失败
	 */
	public native static int Init(String server, String logfile, int timeout);
	
	
	/*
	  函数功能:       登录LicenseServer
	      入参:			用户名user
	      			密码password
					输出XML文件（带路径）
	    返回值:       1	操作成功
					-1	操作失败
					-10	无效API 或不存在
					-2	账户信息错误
					-3	调用API与参数不符或参数格式错误
					-21	没有归还权限
					-22	没有可用license数
	 */
	public native static int Login(String user, String password, String xmlfile);
	
	
	/*
	 函数功能:    查询本机License状态
	 入参:		keyword
	            xmlfile
	 返回值:      1	操作成功
	            -1	操作失败
	            -10	无效API 或不存在
	            -2	账户信息错误
	            -3	调用API与参数不符或参数格式错误
	            -21	没有归还权限
	            -22	没有可用license数
	 */
	public native static int Status(String keyword,String xmlfile);
	
	
	
	/*
	  函数功能:  	获取License
    	入参:      （安卓过长的参数列表会找不到该接口）
                  contract_detail包含：product_code, contract_code, keyword, license识别号，安卓为IMEI， 过期日期(例：2013-10-28 18:00:00)。注意顺序，并用“\r\n”分割
                  输出license文件
	    返回值: 	1	操作成功
					-1	操作失败
					-10	无效API 或不存在
					-2	账户信息错误
					-3	调用API与参数不符或参数格式错误
					-21	没有归还权限
					-22	没有可用license数
	*/				
	public native static int Checkout(String contract_detail,String licensefile);
	
	
	
	/*
	  函数功能:       归还License
	      入参:		keyword,
	    返回值:      1	操作成功
					-1	操作失败
					-10	无效API 或不存在
					-2	账户信息错误
					-3	调用API与参数不符或参数格式错误
					-21	没有归还权限
					-22	没有可用license数
	 */
	public native static int Checkin(String keyword);
	
	
		
}
