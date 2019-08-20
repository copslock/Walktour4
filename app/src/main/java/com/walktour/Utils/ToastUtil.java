package com.walktour.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 手机提示工具类
 * 
 * @author weirong.fan
 * @see com.walktour.base.util.ToastUtil
 */
public class ToastUtil {

	/**
	 * 防止外部构造此对象
	 */
	private ToastUtil() {
		super();
	}

	/**
	 * 显示信息
	 * 
	 * @param context
	 *            上下文
	 * @param msg
	 *            信息
	 * @return void 无
	 */
	public static void showToastShort(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示信息
	 * 
	 * @param context
	 *            上下文
	 * @param id
	 * @return void 无
	 */
	public static void showToastShort(Context context, int id) {
		Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示信息
	 * 
	 * @param context
	 *            上下文
	 * @param msg
	 *            信息
	 * @return void 无
	 */
	public static void showToastLong(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 显示信息
	 * 
	 * @param context
	 *            上下文
	 * @param id
	 *            信息
	 * @return void 无
	 */
	public static void showToastLong(Context context, int id) {
		Toast.makeText(context, id, Toast.LENGTH_LONG).show();
	}
}
