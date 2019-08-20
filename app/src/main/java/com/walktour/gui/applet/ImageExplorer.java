package com.walktour.gui.applet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.walktour.gui.R;


/**
 * 文件浏览器,选定文件后会自动返回,并发送广播到启动者(Activity或者Service)
 * 广播内容是文件路径,广播的Action值和内容包名都用启动者来指定
 * */
public class ImageExplorer {
	private Context context;
	private String key_action;
	private String key_extra;
	private String[] file_filter;
	private String init_path;
	
	/**
	 * @param actionValue 接收广播的Action值, 
	 * @param extraName 返回结果的包名
	 * */
	public ImageExplorer(Context context,String actionValue,String extraName){
		this.context = context;
		this.key_action = actionValue;
		this.key_extra = extraName;
	}
	
	/**
	 * @param actionValue 接收广播的Action值, 
	 * @param extraName 返回结果的包名
	 * @param file_filter 所有要过滤的文件后缀名,
	 * */
	public ImageExplorer(Context context,String actionValue,String extraName,String [] file_filter){
		this.context = context;
		this.key_action = actionValue;
		this.key_extra = extraName;
		this.file_filter = file_filter;
	}
	
	/**
	 * @param actionValue 接收广播的Action值, 
	 * @param extraName 返回结果的包名
	 * @param file_filter 所有要过滤的文件后缀名,
	 * @param init_path 初始化路径
	 * */
	public ImageExplorer(Context context,String actionValue,String extraName,String [] file_filter,String init_path){
		this.context = context;
		this.key_action = actionValue;
		this.key_extra = extraName;
		this.file_filter = file_filter;
	}
	
	
	/**
	 * @param mContext 启动者的context
	 * */
	public void start(){
		Intent intent;
        intent = new Intent(context,FileExplorer.class);
        
      //添加传递参数
        Bundle bundle = new Bundle(); 
      //文件类型过滤
        bundle.putStringArray(
        		FileExplorer.KEY_FILE_FILTER, 
        		context.getResources().getStringArray(R.array.maptype_picture)//默认文件过滤
        		);intent.putExtras(bundle); 
        bundle.putString(FileExplorer.KEY_ACTION, key_action);
        bundle.putString(FileExplorer.KEY_EXTRA, key_extra); 
        if(file_filter!=null)
        {
        	bundle.putStringArray(FileExplorer.KEY_FILE_FILTER, file_filter);//根据构造函数修改文件过滤
        }
        if(init_path!=null)
        {
        	bundle.putString(FileExplorer.KEY_INIT_DIR, init_path);
        }
        intent.putExtras(bundle); 
        context.startActivity(intent);
	}
	
}