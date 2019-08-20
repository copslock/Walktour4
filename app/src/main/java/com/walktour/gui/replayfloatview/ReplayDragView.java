package com.walktour.gui.replayfloatview;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.walktour.gui.R;

import java.lang.reflect.Field;

/**
 * 
 * @author zhihui.lian
 *
 *	全局回放拖动按钮View，支持贴边处理
 */
public class ReplayDragView extends LinearLayout {

	/**
	 * 记录悬浮窗的宽度
	 */
	public static int viewWidth;

	/**
	 * 记录悬浮窗的高度
	 */
	public static int viewHeight;

	/**
	 * 记录系统状态栏的高度
	 */
	 private static int statusBarHeight;

	/**
	 * 用于更新悬浮窗的位置
	 */
	private WindowManager windowManager;

	/**
	 * 悬浮窗的参数
	 */
	private WindowManager.LayoutParams mParams;

	/**
	 * 记录当前手指位置在屏幕上的横坐标值
	 */
	private float xInScreen;

	/**
	 * 记录当前手指位置在屏幕上的纵坐标值
	 */
	private float yInScreen;

	/**
	 * 记录手指按下时在屏幕上的横坐标的值
	 */
	private float xDownInScreen;

	/**
	 * 记录手指按下时在屏幕上的纵坐标的值
	 */
	private float yDownInScreen;

	/**
	 * 记录手指按下时在悬浮窗的View上的横坐标的值
	 */
	private float xInView;

	/**
	 * 记录手指按下时在悬浮窗的View上的纵坐标的值
	 */
	private float yInView;
	
	/**
	 * 记录点下的时间
	 */
	private long ACTION_DOWN_TIME = 0;
	/**
	 * 记录点起来时间
	 */
	private long ACTION_UP_TIME = 0;
	
	private Context context;
	
	public ReplayDragView(Context context) {
		super(context);
		this.context = context;
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		LayoutInflater.from(context).inflate(R.layout.float_window_drag, this);
		View view = findViewById(R.id.small_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		TextView percentView = (TextView) findViewById(R.id.percent);
		percentView.setText(FloatWindowManager.getUsedPercentValue(context));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//记录点下时间
			ACTION_DOWN_TIME = System.currentTimeMillis(); 
			// 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
			xInView = event.getX();
			yInView = event.getY();
			xDownInScreen = event.getRawX();
			yDownInScreen = event.getRawY() - getStatusBarHeight();
			xInScreen = event.getRawX();
			yInScreen = event.getRawY() - getStatusBarHeight();
			break;
		case MotionEvent.ACTION_MOVE:
			xInScreen = event.getRawX();
			yInScreen = event.getRawY() - getStatusBarHeight();
			// 手指移动的时候更新悬浮窗的位置
			updateViewPosition();
			break;
		case MotionEvent.ACTION_UP:
			
			ACTION_UP_TIME = System.currentTimeMillis();
			long time = ACTION_UP_TIME - ACTION_DOWN_TIME;
			System.out.println("time == " + time);
				//把down和up时间差小于120 当作点击事件
			if (time < 120) {
				openFloatWindow();
			}else{    //将按钮贴回屏幕边缘
				aSideViewPosition();
			}
			break;
		default:
			break;
		}
		return true;
	}
	
	
	
	
	
	
	

	/**
	 * 将悬浮窗的参数传入，用于更新悬浮窗的位置。
	 * 
	 * @param params
	 *            悬浮窗的参数
	 */
	public void setParams(WindowManager.LayoutParams params) {
		mParams = params;
	}

	/**
	 * 更新悬浮窗在屏幕中的位置。
	 */
	private void updateViewPosition() {
		mParams.x = (int) (xInScreen - xInView);
		mParams.y = (int) (yInScreen - yInView);
		windowManager.updateViewLayout(this, mParams);
	}

	/**
	 * 打开悬浮窗，同时关闭悬浮窗。
	 */
	private void openFloatWindow() {
		FloatWindowManager.createFloatWindow(context);
		FloatWindowManager.removeDragWindow(context);
	}
	
	
	/**
	 * 判断靠近屏幕然后自动贴近桌面
	 */
	private void aSideViewPosition() {
		if (mParams.x + this.getWidth() / 2 > getScreenSize()[0] / 2) {
			mParams.x = getScreenSize()[0] - this.getWidth();
		} else {
			mParams.x = 0;
		}
		windowManager.updateViewLayout(this, mParams);
	}
	
	
	
	/**
	 * 获取屏幕的尺寸
	 * @param mContext
	 * @return
	 */
	public int[] getScreenSize() {

		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		int[] size = { dm.widthPixels, dm.heightPixels };
		return size;
	}
	
	

	/**
	 * 用于获取状态栏的高度。
	 * 
	 * @return 返回状态栏高度的像素值。
	 */
	private int getStatusBarHeight() {
		if (statusBarHeight == 0) {
			try {
				Class<?> c = Class.forName("com.android.internal.R$dimen");
				Object o = c.newInstance();
				Field field = c.getField("status_bar_height");
				int x = (Integer) field.get(o);
				statusBarHeight = getResources().getDimensionPixelSize(x);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusBarHeight;
	}

}
