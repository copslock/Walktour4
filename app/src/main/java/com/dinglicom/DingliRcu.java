package com.dinglicom;



/*******************************************************************************
File name     : DingliRcu_JNI.h 
Author        : yan.zhu
Version       : 1.0
Date          : 2013/11/28
Description   : RCU文件操作模块
Others        :
Function List : 
Histroy       :
1.
<author>         <time>         <vertion>         <desc>
y.z              2013/11/28     2.0               创建

*******************************************************************************/

public class DingliRcu {
	
	/************************************************************************/
	//合并RCU调用流程：
	// 1.initMergeOrDivideFile 
	// 2.addMergeOrDivideFile 
	// 3. mergeRcus
	/************************************************************************/
	//按时间分割RCU调用流程:
	// 1.initMergeOrDivideFile 
	// 2.addDivideRcuByTime 
	// 3.divideRcus
	
	static{
		System.loadLibrary("DingliRcu");
	}
	
	//***************************************************************************************************
	//函数功能： 获取版本信息
	//返回值  ： 返回字符串，如“Compile Date: Mar 15 2013, Time=11:14:48”
	//参数    ： 无
	//说明    ： 无
	public native static String getVersion();
	
	//***************************************************************************************************
	//函数功能： 合并分割RCU初始化
	//返回值  ： >=0 success; <0 error
	//说明    ： 调用者按时间顺序传入RCU文件.
	public native static int initMergeOrDivideFile();
	
	
	//***************************************************************************************************
	//函数功能： 添加合并分割文件
	//返回值  ： >=0 success; <0 error
	//说明    ： 调用者按时间顺序添加RCU文件.
	
	public native static int addMergeOrDivideFile(String szRcuFile);
	
	//***************************************************************************************************
	//函数功能： 合并RCU文件
	//返回值  ： >=0 success; <0 error
	//参数    ： 
	//           szDestRcuPath 合并后的RCU文件存放目录
	//说明    ： 调用者按时间顺序传入RCU文件.线程中调用
	public native static int mergeRcus(String szDestRcuPath);
	
	
	//***************************************************************************************************
	//函数功能： 添加分割时间段
	//返回值  ： >=0 success; <0 error
	public native static int addDivideRcuByTime(long startTime,long endTime);
	
	//***************************************************************************************************
	//函数功能： 按时间段截取RCU文件
	//返回值  ： >=0 success; <0 error
	//参数    ： szSrcRcuFile 需要截取的原始RCU全文件名
	//           szDestRcuPath 截取后RCU文件存放目录
	//说明    ： 线程中调用
	public native static int divideRcus(String szSrcRcuFile,String szDestRcuPath);
	
	
	
}
