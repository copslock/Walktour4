package com.dinglicom;

public class DiagSoLib{

	static{
		System.loadLibrary("ipcDevDiagSo");
	}

	public native static int startDLDiag(String jslibPath, String jsCode, String jsImei);
	public native static int stopDLDiag();
}



