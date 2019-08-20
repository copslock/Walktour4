package com.walktour.gui.applet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * License浏览器
 * */
public class LicenseExplorer {
	private Context context; /* 上下文 */
	private String[] file_filter; /* 文件过滤器 */

	private int actionType = LOADING_LICENSE;

	public static int LOADING_LICENSE = 1;

	public static int LOADING_BASE_DATA = 2;

	public static int LOADING_URL = 3;

	private String key_action;

	private String key_extra;
	/** 加载基站地图使用的地图类型 */
	private int mapType; 
	/**
	 * @param actionValue
	 *            接收广播的Action值,
	 * @param extraName
	 *            返回结果的包名
	 * */
	public LicenseExplorer(Context context, String[] filter) {
		this.context = context;
		this.file_filter = filter;
	}
 
	/**
	 * @param actionValue
	 *            接收广播的Action值,
	 * @param extraName
	 *            返回结果的包名
	 * @param mapType
	 *            地图类型
	 * */
	public LicenseExplorer(Context context, String[] filter, int actionType, int mapType) {
		this.context = context;
		this.file_filter = filter;
		this.actionType = actionType;
		this.mapType = mapType;
	}

	/**
	 * @param actionValue
	 *            接收广播的Action值,
	 * @param extraName
	 *            返回结果的包名
	 * */
	public LicenseExplorer(Context context, String[] filter, int actionType, String actionKey, String extraFile) {
		this.context = context;
		this.file_filter = filter;
		this.actionType = actionType;
		this.key_action = actionKey;
		this.key_extra = extraFile;

	}

	/**
	 * @param mContext
	 *            启动者的context
	 * */
	public void start() {
		Intent intent;
		intent = new Intent(context, FileExplorer.class);
		// 添加传递参数
		Bundle bundle = new Bundle();
		if (file_filter != null) {
			bundle.putStringArray(FileExplorer.KEY_FILE_FILTER, file_filter);// 根据构造函数修改文件过滤
		}
		
		bundle.putString(FileExplorer.KEY_ACTION, key_action);
		bundle.putString(FileExplorer.KEY_EXTRA, key_extra);
		if (this.actionType == LOADING_BASE_DATA) {
			bundle.putBoolean(FileExplorer.KEY_BASE_DATA, true);
			bundle.putInt(FileExplorer.KEY_MAP_TYPE, this.mapType);
		} else if (this.actionType == LOADING_URL) { // 浏览url文件
			bundle.putBoolean(FileExplorer.KEY_URL, true);
		} else {
			bundle.putBoolean(FileExplorer.KEY_LICENSE, true);
		}
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

}