package com.walktour.gui.share.download;

import org.xutils.common.Callback;

import java.io.File;

/**
 * 进度界面ui回调专用类
 * @author zhihui.lian
 */
public interface UICallBack {
	
	public void onWaiting();

	public void onStarted();

	public void onLoading(long total, long current);

	public void onSuccess(File result);				//下载专用
	
	public void onSuccess(String result);			//上传专用

	public void onError(Throwable ex);

	public void onCancelled(Callback.CancelledException cex);
}
