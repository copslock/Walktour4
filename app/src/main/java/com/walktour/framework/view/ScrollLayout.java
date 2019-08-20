/*
 * 文件名: ScrollLayout.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2012-6-20
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.Vector;

public class ScrollLayout extends ViewGroup {

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	/**
	 * 当前屏幕
	 */
	private int mCurScreen;
	/**
	 * 初始默认屏幕
	 */
	private int mDefaultScreen = 0;
	/**
	 * 点击状态
	 */
	private static final int TOUCH_STATE_REST = 0;
	/**
	 * 点击滑动状态
	 */
	private static final int TOUCH_STATE_SCROLLING = 1;
	private static final int SNAP_VELOCITY = 500;
	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop;
	private float mLastMotionX;
	private int sensitivity = 30;
	private boolean spring;

	private Vector<LayoutChangeListener> listeners;

	public ScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(context);
		mCurScreen = mDefaultScreen;
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		listeners = new Vector<LayoutChangeListener>();
	}
    public ScrollLayout(Context context) {
        super(context);
        mScroller = new Scroller(context);
        mCurScreen = mDefaultScreen;
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        listeners = new Vector<LayoutChangeListener>();
     }

	/**
	 * 添加Layout改变接口<BR>
	 * [功能详细描述]
	 * @param listener
	 */
	public void addChangeListener(LayoutChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childLeft = 0;
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View childView = getChildAt(i);
			if (childView.getVisibility() != View.GONE) {
				final int childWidth = childView.getMeasuredWidth();
				childView.layout(childLeft, 0, childLeft + childWidth,
						childView.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only canmCurScreen run at EXACTLY mode!");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only can run at EXACTLY mode!");
		}

		// The children are given the same width and height as the scrollLayout
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(mCurScreen * width, 0);
	}

	public void snapToDestination() {
		final int screenWidth = getWidth();
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}
	
	/**
	 * 跳转屏幕<BR>
	 * [功能详细描述]
	 * @param whichScreen 屏幕index
	 */
	public void snapToScreen(int whichScreen) {
		// get the valid layout page
		int lastIndex = mCurScreen;
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {

			final int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta) * 2);
			mCurScreen = whichScreen;
			invalidate(); // Redraw the layout
		}
		for (LayoutChangeListener listener : listeners) {
			listener.doChange(lastIndex, whichScreen);
		}
	}
	
	public void setToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		mCurScreen = whichScreen;
		scrollTo(whichScreen * getWidth(), 0);
	}

	/**
	 * 获得当前所在屏幕<BR>
	 * [功能详细描述]
	 * @return
	 */
	public int getCurScreen() {
		return mCurScreen;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	public boolean isSpring() {
		return spring;
	}

	public void setSpring(boolean spring) {
		this.spring = spring;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mVelocityTracker == null)
			mVelocityTracker = VelocityTracker.obtain();
		mVelocityTracker.addMovement(event);
		final int action = event.getAction();
		final float x = event.getX();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished())
				mScroller.abortAnimation();
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);
			if (Math.abs(deltaX) > sensitivity) {
				if (spring) {
					scrollBy(deltaX, 0);
					mLastMotionX = x;
				} else {
					final int childCount = getChildCount();
					boolean max = mCurScreen < childCount - 1;
					boolean min = mCurScreen > 0;
					boolean canMove = deltaX > 0 ? (max ? true : false)
							: (min ? true : false);
					if (canMove) {
						scrollBy(deltaX, 0);
						mLastMotionX = x;
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityX = (int) velocityTracker.getXVelocity();
			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				// Fling enough to move left
				snapToScreen(mCurScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY
					&& mCurScreen < getChildCount() - 1) {
				// Fling enough to move right
				snapToScreen(mCurScreen + 1);
			} else {
				snapToDestination();
			}
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST))
			return true;
		final float x = ev.getX();
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(mLastMotionX - x);
			if (xDiff > mTouchSlop)
				mTouchState = TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}

}