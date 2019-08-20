package com.ylb.engineeringMode;

/**
 * 工程机SIM929锁网JNI调用库
 * @author XieJihong
 *
 */
public class DiagInterface{

	static{
		System.loadLibrary("EngineeringInterface");
	}

	native public void Diaginit();
	native public void Diagrelease();
	native public void DiagSwitchMode(int mode);
	
	native public Pkg DiagreadData();
	native public void DiagwriteData(byte[] data,int length);
	
	native public void SendAtCmd(String cmd);

	//native public int SimReadAtEcho(char[] buf,int count);
	native public int SimReadAtEcho(byte[] buf,int count);

	public class Pkg{
		public int len;
		public byte[] data;
	}
}
