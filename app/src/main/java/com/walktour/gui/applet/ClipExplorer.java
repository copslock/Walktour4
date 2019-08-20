package com.walktour.gui.applet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 文件复制或移动浏览器
 * */
public class ClipExplorer {
	private Context context;		/*上下文*/
	private String[] file_filter;	/*文件过滤器*/
	private String[] filePaths;		/*粘贴板内容，即要复制或移动的文件绝对路径*/
	private boolean remove = false;
	
	/**
	 * @param actionValue 接收广播的Action值, 
	 * @param extraName 返回结果的包名
	 * */
	public ClipExplorer(Context context,String[] filter,String[] filePaths,boolean remove){
		this.context = context;
		this.file_filter = filter;
		this.filePaths = filePaths; 
		this.remove = remove;
	}
		
	/**
	 * @param mContext 启动者的context
	 * */
	public void start(){
		Intent intent;
        intent = new Intent(context,FileExplorer.class);
        //添加传递参数
        Bundle bundle = new Bundle(); 
        if(file_filter!=null)
        {
        	//根据构造函数修改文件过滤
        	bundle.putStringArray(FileExplorer.KEY_FILE_FILTER, file_filter);
        }
        if(filePaths!=null)
        {
        	//根据构造函数设定粘贴板内容
        	bundle.putStringArray(FileExplorer.KEY_CLIP, filePaths);
        	bundle.putBoolean( FileExplorer.KEY_REMOVE, remove);
        }
        intent.putExtras(bundle); 
        context.startActivity(intent);
	}
	
}