package com.walktour.gui.applet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 选存储目录
 * */
public class ExplorerDirectory {
	private Context context; /* 上下文 */
	private String[] file_filter; /* 文件过滤器 */
	private String action; // 要返回结果的广播
	private String extra; // 广播包含的内容KEY

	/**
	 * @param actionValue
	 *          接收广播的Action值,
	 * @param extraName
	 *          返回结果的包名
	 * */
	public ExplorerDirectory(Context context, String[] filter, String action, String extra) {
		this.context = context;
		this.file_filter = filter;
		this.action = action;
		this.extra = extra;
	}

	/**
	 * @param mContext
	 *          启动者的context
	 * */
	public void start() {
		Intent intent;
		intent = new Intent(context, FileExplorer.class);
		// 添加传递参数
		Bundle bundle = new Bundle();
		if (file_filter != null) {
			// 根据构造函数修改文件过滤
			bundle.putStringArray(FileExplorer.KEY_FILE_FILTER, file_filter);
		}
		// 根据构造函数设定粘贴板内容
		bundle.putString(FileExplorer.KEY_ACTION, action);
		bundle.putString(FileExplorer.KEY_EXTRA, extra);
		bundle.putBoolean(FileExplorer.KEY_DIR, true);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

}