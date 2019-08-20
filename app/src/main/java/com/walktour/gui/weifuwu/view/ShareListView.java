package com.walktour.gui.weifuwu.view;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
public class ShareListView extends ListView {
	private int mScreenWidth; // 屏幕宽度
	private int mDownX; // 按下点的x值
	private int mDownY; // 按下点的y值
	private int mDeleteBtnWidth;// 删除按钮的宽度
	private boolean isDeleteShown; // 删除按钮是否正在显示
	private ViewGroup mPointChild; // 当前处理的item
	private LinearLayout.LayoutParams mLayoutParams; // 当前处理的item的LayoutParams
	public ShareListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public ShareListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// 获取屏幕宽度
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			performActionDown(ev);
			break;
		case MotionEvent.ACTION_MOVE:
			return performActionMove(ev);
		case MotionEvent.ACTION_UP:
			performActionUp();
			break;
		}
		return super.onTouchEvent(ev);
	}
	// 处理action_down事件
	private void performActionDown(MotionEvent ev) {
		if (isDeleteShown) {
			turnToNormal();
		}
		mDownX = (int) ev.getX();
		mDownY = (int) ev.getY();
		// 获取当前点的item
		mPointChild = (ViewGroup) getChildAt(pointToPosition(mDownX, mDownY) - getFirstVisiblePosition());
		// 获取删除按钮的宽度
		mDeleteBtnWidth = mPointChild.getChildAt(1).getLayoutParams().width
				+ mPointChild.getChildAt(2).getLayoutParams().width;
		mLayoutParams = (LinearLayout.LayoutParams) mPointChild.getChildAt(0).getLayoutParams();
		// 为什么要重新设置layout_width 等于屏幕宽度
		// 因为match_parent时，不管你怎么滑，都不会显示删除按钮
		// why？ 因为match_parent时，ViewGroup就不去布局剩下的view
		mLayoutParams.width = mScreenWidth;
		mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
	}
	// 处理action_move事件
	private boolean performActionMove(MotionEvent ev) {
		int nowX = (int) ev.getX();
		int nowY = (int) ev.getY();
		if (Math.abs(nowX - mDownX) > Math.abs(nowY - mDownY)) {
			// 如果向左滑动
			if (nowX < mDownX) {
				// 计算要偏移的距离
				int scroll = (nowX - mDownX) / 2;
				// 如果大于了删除按钮的宽度， 则最大为删除按钮的宽度
				if (-scroll >= mDeleteBtnWidth) {
					scroll = -mDeleteBtnWidth;
				}
				// 重新设置leftMargin
				mLayoutParams.leftMargin = scroll;
				mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}
	// 处理action_up事件
	private void performActionUp() {
		// 偏移量大于button的一半，则显示button
		// 否则恢复默认
		if (null != mLayoutParams) {
			if (-mLayoutParams.leftMargin >= mDeleteBtnWidth / 2) {
				mLayoutParams.leftMargin = -mDeleteBtnWidth;
				isDeleteShown = true;
			} else {
				turnToNormal();
			}
			mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
		}
	}
	/**
	 * 变为正常状态
	 */
	public void turnToNormal() {
		try {
			mLayoutParams.leftMargin = 0;
			mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
			isDeleteShown = false;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * 当前是否可点击
	 * 
	 * @return 是否可点击
	 */
	public boolean canClick() {
		return !isDeleteShown;
	}
}
