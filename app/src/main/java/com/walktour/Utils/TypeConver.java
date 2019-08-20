package com.walktour.Utils;

import com.walktour.base.util.LogUtil;

/**
 * 封装C函数，通过JNI调用C中的类型转换
 * @author tangwq
 *
 */
public class TypeConver {
	static{
		try{
			System.loadLibrary("TypeConver");
		}catch (UnsatisfiedLinkError e){
			LogUtil.d("UtilsCMethod:","Library not found!");
		}
	}
	
	/**
	 * 将字符串转换成立double类型
	 */
	public static native double StringToDouble(String in);
	/**
	 * 将类型串以十进制转为整进
	 * @param in
	 * @return
	 */
	public static native int StringToInt(String in);
	/**
	 * 将字符串当成十六进制转换为整形
	 * @param in
	 * @return
	 */
	public static native int StringToHexInt(String in);
	
	/**
	 * 设置系统同步时间
	 * @param ymd 为修改时间格式为:yyyy-mm-dd hh:mm:ss
	 * @return
	 */
	public static native int SetSyncTime(String ymh);
	
	//public static native int testSIGSEGV();
	//public static native int testSIGABRT();
	
	public static native int 	 ksB67dHyili23(byte[] jdava,String buffer,int len);
	public static native boolean CheckPower(String filePath,String devId);
	public static native boolean tcBKlmm0u23f3(byte[] jdata,int length,String devId,int time);
	public static native String  GetPower(String filePath,String devId);
	public static native byte[]  AhBSdh80eK0x2(byte[] jdata,int length,int aub,String devId);
	public static native String  hiBe75ARwqOI(byte[] jdata,int length,String devId,int index);
	public static native String  PnBv0Y6nxz9uW1(int xbb,byte[] jdata,int length,String devId);
	/*
	 * 生成标志文件
	 * @param szDevid	szDevid  设备IMEI或MAC地址
	 * @return	失败：返回字串"ErrorFlagFile"，成功：返回设备码的MD5
	 */
	public static native String 	dIuVlic53R(String szDevid);
}
