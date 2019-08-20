package com.walktour.gui.replayfloatview;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.walktour.gui.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * @author zhihui.lian 悬浮窗管理类
 */
public class FloatWindowManager {

	/**
	 * 拖动View的实例
	 */
	private static ReplayDragView dragView;

	/**
	 * 回放控制View的实例
	 */
	private static ReplayFloatView floatWindow;

	/**
	 * 拖动View的参数
	 */
	private static LayoutParams dragWindowParams;

	/**
	 * 回放控制View的参数
	 */
	private static LayoutParams floatWindowParams;

	/**
	 * 用于控制在屏幕上添加或移除悬浮窗
	 */
	private static WindowManager mWindowManager;

	/**
	 * 用于获取手机可用内存
	 */
	private static ActivityManager mActivityManager;

	/**
	 * 创建一个拖动。初始位置为屏幕的右部中间位置。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	@SuppressWarnings("deprecation")
	public static void createDragWindow(Context context) {
		WindowManager windowManager = getWindowManager(context);
		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();
		if (dragView == null) {
			dragView = new ReplayDragView(context);
			if (dragWindowParams == null) {
				dragWindowParams = new LayoutParams();
				dragWindowParams.type = LayoutParams.TYPE_PHONE;
				dragWindowParams.format = PixelFormat.RGBA_8888;
				dragWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
						| LayoutParams.FLAG_NOT_FOCUSABLE;
				dragWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				dragWindowParams.width = ReplayDragView.viewWidth;
				dragWindowParams.height = ReplayDragView.viewHeight;
				dragWindowParams.x = screenWidth;
				dragWindowParams.y = screenHeight / 2;
			}
			dragView.setParams(dragWindowParams);
			windowManager.addView(dragView, dragWindowParams);
		} else {
			dragView.setVisibility(View.VISIBLE);
			dragView.setParams(dragWindowParams);
			windowManager.updateViewLayout(dragView, dragWindowParams);
		}
	}

	/**
	 * 创建一个回放控制。位置为屏幕底部
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	@SuppressWarnings("deprecation")
	public static void createFloatWindow(Context context) {
		WindowManager windowManager = getWindowManager(context);
		int screenHeight = windowManager.getDefaultDisplay().getHeight();
		if (floatWindow == null) {
			floatWindow = new ReplayFloatView(context);
			if (floatWindowParams == null) {
				floatWindowParams = new LayoutParams(WindowManager.LayoutParams.TYPE_TOAST);
				floatWindowParams.x = 0;
				floatWindowParams.y = screenHeight - ReplayFloatView.viewHeight;
				floatWindowParams.type = LayoutParams.TYPE_PHONE;
				floatWindowParams.format = PixelFormat.RGBA_8888;
				floatWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
						| LayoutParams.FLAG_NOT_FOCUSABLE;
				floatWindowParams.gravity = Gravity.RIGHT | Gravity.TOP;
				floatWindowParams.width = ReplayFloatView.viewWidth;
				floatWindowParams.height = ReplayFloatView.viewHeight;
			}
			floatWindow.setParams(floatWindowParams);
			windowManager.addView(floatWindow, floatWindowParams);
		} else {
			floatWindow.setVisibility(View.VISIBLE);
			floatWindow.setParams(floatWindowParams);
			windowManager.updateViewLayout(floatWindow, floatWindowParams);
		}
	}

	/**
	 * 将回放控制在屏幕上隐藏。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void hiddenFloatWindow(Context context) {
		if (floatWindow != null) {
			floatWindow.setVisibility(View.GONE);
		}
	}

	/**
	 * 将回放控制在屏幕上显示。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void showFloatWindow(Context context) {
		if (floatWindow != null) { 
			floatWindow.setVisibility(View.VISIBLE);
		}
	}
	/**
	 * 将回放控制在屏幕上移除。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void removeFloatWindow(Context context) {
		if (floatWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(floatWindow);
			floatWindow = null;
			//
			if (mListener != null) {
				mListener.onWindowClose();
			}
		}
	}

	public static boolean isRemoveView() {
		if (floatWindow != null) {
			return floatWindow.removeView();
		}
		return false;
	}

	/**
	 * 将拖动从屏幕上移除。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void removeDragWindow(Context context) {
		if (dragView != null) {
			dragView.setVisibility(View.GONE);
		}
	}

	/**
	 * 更新拖动的TextView上的数据，显示内存使用的百分比。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 */
	public static void updateUsedPercent(Context context) {
		if (dragView != null) {
			TextView percentView = (TextView) dragView
					.findViewById(R.id.percent);
			percentView.setText(getUsedPercentValue(context));
		}
	}

	/**
	 * 是否有悬浮窗(包括拖动和回放控制)显示在屏幕上。
	 * 
	 * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
	 */
	public static boolean isWindowShowing() {
		return floatWindow != null;
	}

	/**
	 * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
	 */
	private static WindowManager getWindowManager(Context context) {
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
		}
		return mWindowManager;
	}

	/**
	 * 如果ActivityManager还未创建，则创建一个新的ActivityManager返回。否则返回当前已创建的ActivityManager。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return ActivityManager的实例，用于获取手机可用内存。
	 */
	private static ActivityManager getActivityManager(Context context) {
		if (mActivityManager == null) {
			mActivityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
		}
		return mActivityManager;
	}

	/**
	 * 计算已使用内存的百分比，并返回。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return 已使用内存的百分比，以字符串形式返回。
	 */
	public static String getUsedPercentValue(Context context) {
		String dir = "/proc/meminfo";
		try {
			FileReader fr = new FileReader(dir);
			BufferedReader br = new BufferedReader(fr, 2048);
			String memoryLine = br.readLine();
			String subMemoryLine = memoryLine.substring(memoryLine
					.indexOf("MemTotal:"));
			br.close();
			long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll(
					"\\D+", ""));
			long availableSize = getAvailableMemory(context) / 1000;
			int percent = (int) ((totalMemorySize - availableSize)
					/ (float) totalMemorySize * 100);
			return percent + "%";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Float";
	}

	/**
	 * 获取当前可用内存，返回数据以字节为单位。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return 当前可用内存。
	 */
	private static long getAvailableMemory(Context context) {
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		getActivityManager(context).getMemoryInfo(mi);
		return mi.availMem;
	}
	
	public static void runReplay(String filePath) {
		floatWindow.desDdibPath(filePath);
	}
	public static void runReplay(String filePath,int startIndex,int endIndex) {
		floatWindow.desDdibPath(filePath,startIndex,endIndex);
	}
	private static OnReplayWindowListener mListener;
	
	public static void setOnReplayWindowListener(OnReplayWindowListener onReplayWindowListener) {
		mListener = onReplayWindowListener;
	}
}
