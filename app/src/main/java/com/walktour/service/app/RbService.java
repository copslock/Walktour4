package com.walktour.service.app;

import com.walktour.base.util.LogUtil;

public class RbService {
	private static String tag = "RbService";
	public native int runcmd(String cmd,int su_flag);
	static{
		System.loadLibrary("decoder");
		LogUtil.w(tag, "---load success");
	}
}
