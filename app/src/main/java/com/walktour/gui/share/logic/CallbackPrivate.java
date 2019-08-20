package com.walktour.gui.share.logic;

import org.xutils.common.Callback;

/**
 * http内部回调处理接口
 * zhihui.lian
 */
public class CallbackPrivate implements
        Callback.CommonCallback<String>,
        Callback.ProgressCallback<String>,
        Callback.Cancelable {

    private boolean cancelled = false;
    private Cancelable cancelable;
    
    HttpCallBackI httpCallBackI;

    public CallbackPrivate(HttpCallBackI httpCallBackI) {
    	this.httpCallBackI = httpCallBackI;
    }



    public void setCancelable(Cancelable cancelable) {
        this.cancelable = cancelable;
    }


    @Override
    public void onWaiting() {
    	synchronized (CallbackPrivate.class) {
    		System.out.println("onWaiting==");
    		httpCallBackI.onWaiting();
		}
    }

    @Override
    public void onStarted() {
    	synchronized (CallbackPrivate.class) {
    		System.out.println("onStarted==");
    	}
    }

    @Override
    public void onLoading(long total, long current, boolean isDownloading) {
    	synchronized (CallbackPrivate.class) {
    		System.out.println("onLoading==");
    		httpCallBackI.onLoading(total, current, isDownloading);
    	}
    }


    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
    	synchronized (CallbackPrivate.class) {
    		System.out.println("onError==");
    		httpCallBackI.onError(ex,isOnCallback);
    	}
    }

    @Override
    public void onCancelled(CancelledException cex) {
    	synchronized (CallbackPrivate.class) {
    		System.out.println("onCancelled==");
    		httpCallBackI.onCancelled(cex);
    	}
    }

    @Override
    public void onFinished() {
    	synchronized (CallbackPrivate.class) {
    		System.out.println("onFinished==");
    		cancelled = false;
    	}
    }


    @Override
    public void cancel() {
    	synchronized (CallbackPrivate.class) {
    		System.out.println("cancel==");
    		cancelled = true;
    		if (cancelable != null) {
    			cancelable.cancel();
    		}
    	}
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

	@Override
	public void onSuccess(String result) {
		synchronized (CallbackPrivate.class) {
			System.out.println("onSuccess==");
			httpCallBackI.onSuccess(result);
		}
	}
	
	
	
}
