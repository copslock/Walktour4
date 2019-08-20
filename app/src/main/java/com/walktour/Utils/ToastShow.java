package com.walktour.Utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Toast自定义工具类,显示Toast就不会出现时间叠加
 * 
 * @author zhihui.lian
 */
public class ToastShow {

	private static Toast mToast;
	private static Handler mHandler = new Handler();
	
	
	private static Runnable runnable = new Runnable() {
		public void run() {
			mToast.cancel();
		}
	};

	/**
	 * 自定义时间类，text直接显示
	 * 
	 * @param mContext
	 * @param text
	 * @param duration
	 */
	public static void showToast(Context mContext, String text, int duration) {

		mHandler.removeCallbacks(runnable);
		if (mToast != null){
			mToast.setText(text);
		}
		else{
			mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
		}
		mHandler.postDelayed(runnable, duration);

		mToast.show();
	}

	/**
	 * text通过资源文件获取
	 * 
	 * @param mContext
	 * @param resId
	 * @param duration
	 */
	public static void showToast(Context mContext, int resId, int duration) {
		showToast(mContext, mContext.getResources().getString(resId), duration);
	}
}
