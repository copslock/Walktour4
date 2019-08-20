package com.walktour.gui.task.activity.scannertsma.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 自定义ScrollView
 * 目的是重新计算高度，避免高度不准问题
 * * @author jinfeng.xie
 */
public class CustomScrollView extends ScrollView{
	
	private int itemCount;
	private int itemHeight;

	public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CustomScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CustomScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
		super.onMeasure(widthMeasureSpec, measureHeight(heightMeasureSpec));
	}
	private int measureWidth(int measureSpec) {  
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
    }  
	private int measureHeight(int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int result = heightMeasureSpec;
		int tmp = itemCount * itemHeight;
		int height = getResources().getDisplayMetrics().heightPixels;
		int[] screenLocation = new int[2];
		this.getLocationOnScreen(screenLocation);
		if (tmp < height - screenLocation[1])
			result = height - screenLocation[1];
		else 
			result = heightMeasureSpec;
		return result;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public int getItemHeight() {
		return itemHeight;
	}

	public void setItemHeight(int itemHeight) {
		this.itemHeight = itemHeight;
	}
	
}
