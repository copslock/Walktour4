package com.walktour.service.test;

import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.AppFilePathUtil;

public class EmailSend extends EmailTest {
	
	@Override
	public void onCreate(){
		super.onCreate();
	}
	
	@Override
    public void onDestroy(){
    	super.onDestroy();
    	//因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
    	UtilsMethod.killProcessByPname( "com.walktour.service.test.EmailSend", false );
    	UtilsMethod.killProcessByPname( AppFilePathUtil.getInstance().getAppLibFile("datatests_android").getAbsolutePath(), false );
    }
}
