package com.dingli.service.test;

import android.annotation.SuppressLint;
import android.os.Handler;

import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;

public class ipc2jni {
	
	private String mServAddr = "";
	private int mIpcServerHandle = 0;
	private ipc2msg mCurmsg;
	private Handler mEventHandler = null;
	// C functions we call
	public native int ipc_init_server(String serv_path, String callback_func, String ini_path, String exe_path,String log_path);
	public native void ipc_uninit_server(int handle);
	public native int ipc_run_test(int handle, String filename, String dev, String args);
	public native void ipc_send_command(int handle, String dev, int test_item, int event_id, String data, int data_len);
	/**
	 * 以root方式启动bin格式的业务库时需要设定的root相关文件
	 * @param handle
	 * @param su_file_path "/system/xbin/su -c",如果写成"su -c"则自动根据环境变量启动
	 * @param su_pre_cmd "export LD_LIBRARY_PATH=调用的业务库绝对路径"
	 */
	public native void ipc_set_su_file_path(int handle, String su_file_path, String su_pre_cmd);
	
	static {
		LogUtil.i("ipc2", "ipc2jni");
		//加载基础库
		System.loadLibrary("crystax_shared");
		System.loadLibrary("gnustl_shared");
		System.loadLibrary("miniSDL");		
		System.loadLibrary("ipc2");
		System.loadLibrary("iconv");
		System.loadLibrary("myiconv");
		System.loadLibrary("ipc2jni");
		//以下在非root权限时必须添加
//		System.loadLibrary("v8");
		System.loadLibrary("mysock");
		System.loadLibrary("mydns");
		System.loadLibrary("curl");		
		System.loadLibrary("dthelper");
//		System.loadLibrary("etpan");
	}	
	
	public ipc2jni(Handler eventHandler)
	{
		mEventHandler = eventHandler;
		mCurmsg = new ipc2msg();
	}
	
	public String getServerAddress()
	{
		return mServAddr;
	}
	
	@SuppressLint("SdCardPath")
	public boolean initServer(String logPath)
	{
		mServAddr = "";
		
		//start ipc_init_server
		for(int i=0; i<100; i++)
		{
			String serv_addr = "tcp://:" + (8201 + i);
			LogUtil.i("ipc_init_server", serv_addr);
			mIpcServerHandle = ipc_init_server(serv_addr, 
					"msg_callback", 						   //回调函数
					AppFilePathUtil.getInstance().getAppConfigDirectory(), //配置文件路径
					AppFilePathUtil.getInstance().getAppLibDirectory(),logPath);//动态库路径
		    if (mIpcServerHandle != 0) {
		    	mServAddr = serv_addr;
		    	break;
		    }
		}
		
	    if (mIpcServerHandle == 0) {
	    	LogUtil.w("ipc2", "init server failed");
	    	return false;
	    }
	    
		return true;
	}
	
	public void uninit_server()
	{
		if(mIpcServerHandle != 0)
		{
			ipc_uninit_server(mIpcServerHandle);
			LogUtil.i("ipc2", "ipc_uninit_server ok");
			mIpcServerHandle = 0;
		}
	}
	
	public boolean run_client(String client_path, String args)
	{
		//run vs_client
        LogUtil.i("ipc2", "ipc_run_test");
       
        int success = ipc_run_test(mIpcServerHandle, client_path, "DEV_127.0.0.1", args);
        if (success == 0) 
        {
        	LogUtil.w("ipc2", "run test failed");
        	LogUtil.w("ipc2", "client_path=" + client_path);
        	LogUtil.w("ipc2", "args=" + args);
        	return false;
        }
        
		return true;
	}

	public boolean run_clientCMD(String client_path, String args,String cmdTAG)
	{
		//run vs_client
		LogUtil.i("ipc2", "ipc_run_test");

		int success = ipc_run_test(mIpcServerHandle, client_path, cmdTAG, args);
		if (success == 0)
		{
			LogUtil.w("ipc2", "run test failed");
			LogUtil.w("ipc2", "client_path=" + client_path);
			LogUtil.w("ipc2", "args=" + args);
			return false;
		}

		return true;
	}
	public void set_su_file_path(String su_file_path, String su_pre_cmd)
	{
		ipc_set_su_file_path(mIpcServerHandle, su_file_path, su_pre_cmd);
	}
	
	public boolean send_command( int test_item, int event_id, String data, int data_len)
	{
		ipc_send_command(mIpcServerHandle, "DEV_127.0.0.1", test_item, event_id, data, data_len);
		return true;
	}
	public boolean send_commandTAG( int test_item, int event_id, String data, int data_len,String cmdTAG)
	{
		ipc_send_command(mIpcServerHandle, cmdTAG, test_item, event_id, data, data_len);
		return true;
	}
    public void msg_callback(int test_item, int event_id, String dev, 
    		int tv_sec, int tv_usec, String data, int data_len) 
    {    
    	ipc2msg curMsg = new ipc2msg();
    	curMsg.test_item = test_item;
    	curMsg.event_id = event_id;
    	curMsg.tv_sec = tv_sec;
    	curMsg.tv_usec = tv_usec;
    	curMsg.data = data;
    	curMsg.data_len = data_len;
    	curMsg.dev = dev;

    	if (null != mEventHandler)
    		mEventHandler.obtainMessage(0, curMsg).sendToTarget();
    	else
    		LogUtil.w("ipc2", "un-send message");
    }
    
    /**
	 * 执行命令行操作，可拥有ROOT权限
	 * @param command
	 * @return
	 */
	public static boolean runCommand(String command) {
	    return UtilsMethod.runRootCommand(command);
	}
}
