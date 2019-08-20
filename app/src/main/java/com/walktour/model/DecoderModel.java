package com.walktour.model;

public class DecoderModel {
	public int handle;				//句柄,初始化时传0,并从初始化接口获得句柄,并在获取数据时将该句柄传入.
	public int moduletype;			//Module_MC5210 1,Module_MC5218 2,目前默认传2;Huawei_ec360   3,目前XT800的模块为3
	public String modulename = "";  //模块名称，用于生成RCU头文件，如果不指定由库原来默认生成
	public String device;			//设置名称
	public int nettype;				//1:WCDMA 2:GSM 3:EVDO 4:CDMA
	public String decoderlibpath;	//解码库所在路径
	public String diagcfgpath;		//logmask配置文件路径
	public int setSimpleRcu	= 1;	//1，精简,2不精简
	public int TraceOffset	= 12;	//串口数据出来后android拼接的表示长度的无效数据长度，目前有4,8，12字节几种
}
