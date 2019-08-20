package com.walktour.framework.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * 自定义Spinner控件<BR>
 * 改变Spinner原有控件效果
 * 
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-8-17]
 */
@SuppressLint("ClickableViewAccessibility")
public class BasicSpinner extends Spinner {

	public BasicSpinner(Context context) {
		super(context);
	}

	public BasicSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BasicSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
}
