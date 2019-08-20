package com.dingli.service.test;

import android.util.Log;

public class filetransjni {
	
	public int fd_stopflag = 0;
	public String fd_error;
	
	int item_list_len = 0;
	
	String down_content;
	int down_content_len = 0;
	
	private int ret = 0;
	
	// C functions we call
	private native int file_trans_init(String param_class, String item_class, String jni_class, fd_init_params f_para);
	private native void file_trans_uninit();
	private native int file_trans_list(String parentPath, fd_file_item f_item, int item_list_len, int stopFlag, String error);
	private native int file_trans_upload(String local_filename, String serv_filename, Integer stopFlag, String error);
	private native int file_trans_upload_content(String upload_content, String serv_filename, Integer stopFlag, String error);
	private native int file_trans_download_content(String down_content, int down_content_len, String serv_filename, int stopFlag, String error);
	private native int file_trans_upload_progress();
	private native void file_trans_cancel();
	
	static {
		Log.i("file_trans", "file_transjni");
		System.loadLibrary("crystax_shared");
		System.loadLibrary("gnustl_shared");
		System.loadLibrary("miniSDL");		
		System.loadLibrary("mydns");
		System.loadLibrary("mysock");
		System.loadLibrary("curl");
		System.loadLibrary("file_trans");
		System.loadLibrary("file_transjni");
	}
	
	public boolean init_server(fd_init_params f_para)
	{
		ret = file_trans_init("com/dingli/service/test/fd_init_params", "com/dingli/service/test/fd_file_item", "com/dingli/service/test/filetransjni", f_para);
		
		if (ret == -1)
			return false;
		
		return true;
	}
	
	public void uninit_server()
	{
		file_trans_uninit();
	}
	
	public boolean trans_list(String parentPath, fd_file_item f_item, int item_list_len, int stopFlag, String error)
	{
		ret = file_trans_list(parentPath, f_item, item_list_len, stopFlag, error);
		
		if (ret == -1)
			return false;
		
		return true;
	}
	
	public boolean upload_file(String local_filename, String serv_filename, Integer stopFlag, String error)
	{
		ret = file_trans_upload(local_filename, serv_filename, stopFlag, error);
		
		if (ret == -1)
			return false;
		
		Log.i("filetransjni", "uplaod file ok");
		return true;
	}
	
	public boolean upload_content(String upload_content, String serv_filename, Integer stopFlag, String error)
	{
		ret = file_trans_upload_content(upload_content, serv_filename, stopFlag, error);
		
		if (ret == -1)
			return false;
		
		return true;
	}
	
	public boolean download_content(String down_content, int down_content_len, String serv_filename, int stopFlag, String error)
	{
		ret = file_trans_download_content(down_content, down_content_len, serv_filename, stopFlag, error);
		
		if (ret == -1)
			return false;
		
		return true;
	}
	
	public int upload_progress()
	{
		return file_trans_upload_progress();
	}
	
	public void trans_cancel()
	{
		file_trans_cancel();
	}

}
