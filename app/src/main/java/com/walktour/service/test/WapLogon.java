package com.walktour.service.test;

import android.util.Log;

import com.walktour.Utils.UtilsMethod;

public class WapLogon extends WebPage {
	@Override
	public void onCreate(){
		super.onCreate();
	}
		
	 @Override
	 public void onDestroy(){
		 super.onDestroy();
		 Log.e(tag, "onDestroy");
		//因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
	    UtilsMethod.killProcessByPname( "com.walktour.service.test.WapLogon", false );
	 }
}
