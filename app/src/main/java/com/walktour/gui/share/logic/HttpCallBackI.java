package com.walktour.gui.share.logic;

import org.xutils.common.Callback.CancelledException;

/**
 * http通用回调接口
 * @author zhihui.lian
 */

public interface HttpCallBackI {
	
	void onWaiting();
	
	void onSuccess(String result);

	void onError(Throwable ex, boolean isOnCallback);

	void onCancelled(CancelledException cex);
	
	void onLoading(long total, long current, boolean isDownloading);
	
	
	
	public abstract class BaseCallBackI implements HttpCallBackI {
		@Override
		public void onWaiting() {

		}

		@Override
		public void onCancelled(CancelledException cex) {

		}
		
		@Override
		public void onLoading(long total, long current, boolean isDownloading) {
			
		}

	}

}
