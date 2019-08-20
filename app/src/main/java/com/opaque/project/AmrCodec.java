package com.opaque.project;

/***
 * 暂时没看到有什么地方用到
 *
 */
@Deprecated
public class AmrCodec
{
	static {
		System.loadLibrary("AmrCodec");
	}

	public native static int nbInit();
	public native static int nbDecode(int handle, byte[] inBuf, int inLen, byte[] outBut, int outLen);
	public native static void nbDestroy(int handle);

	public native static int wbInit();
	public native static int wbDecode(int handle, byte[] intBuf, int inLen, byte[] outBut, int outLen);
	public native static void wbDestroy(int handle);
}