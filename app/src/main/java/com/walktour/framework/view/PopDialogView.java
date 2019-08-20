package com.walktour.framework.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.DensityUtil;
import com.walktour.gui.R;

import java.lang.reflect.Field;

@SuppressLint("InflateParams")
public class PopDialogView {

	private PopupWindow pop;
	private View mView = null;
	private Context mContext;
	private int statusBarHeight = 0;
	private int screenWidth = 0;
	private int screenHeight = 0;
	private View mScreenView;
	private TextView tvTitle;

	public PopDialogView(Context context) {
		this.mContext = context;
		initView();
		getStatusBarHeight();
	}

	public PopDialogView(Context context, View view) {
		this.mContext = context;
		this.mView = view;
		initView();
		setView(view);
		getStatusBarHeight();
	}

	public PopupWindow getPopupWindow() {
		return this.pop;
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		mScreenView = LayoutInflater.from(mContext).inflate(R.layout.pop_dialog_view, null);
		// if (mView != null) {
		// ((LinearLayout)mScreenView.findViewById(R.id.screening_view)).addView(this.mView);
		// }

		if (pop == null) {
			pop = new PopupWindow(mScreenView, 0, 0, true);
			pop.setOutsideTouchable(true);
			pop.setFocusable(true);
			pop.setTouchable(true);
			pop.setBackgroundDrawable(new BitmapDrawable());
			pop.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					if (onDismissListener != null) {
						onDismissListener.onDismiss();
					}
				}
			});
			// pop.setAnimationStyle(R.style.PopupAnimStyle);
		}

	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	public void setView(View view) {
		this.mView = view;
		((LinearLayout) mScreenView.findViewById(R.id.screening_view)).addView(this.mView);
	}

	private void getStatusBarHeight() {

		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;

		Class<?> c = null;

		Object obj = null;

		Field field = null;

		int x = 0;

		try {

			c = Class.forName("com.android.internal.R$dimen");

			obj = c.newInstance();

			field = c.getField("status_bar_height");

			x = Integer.parseInt(field.get(obj).toString());

			statusBarHeight = mContext.getResources().getDimensionPixelSize(x);

		} catch (Exception e1) {

			e1.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void showAtLocation() {
		if (mView == null) {
			Toast.makeText(mContext, "Please set view", Toast.LENGTH_SHORT).show();
			return;
		}
		if (pop == null) {
			pop = new PopupWindow(mScreenView, screenWidth, screenHeight - statusBarHeight, true);
			pop.setOutsideTouchable(true);
			pop.setFocusable(false);
			pop.setTouchable(true);
			pop.setBackgroundDrawable(new BitmapDrawable());
			pop.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					if (onDismissListener != null) {
						onDismissListener.onDismiss();
					}
				}
			});
			pop.showAtLocation(mScreenView, Gravity.CENTER, 0, statusBarHeight);
		} else {
			if (pop.isShowing()) {
				pop.dismiss();
			} else {
				pop.showAtLocation(mScreenView, Gravity.CENTER, 0, statusBarHeight);
			}
		}

	}

	public void showAsDropDown(View view, int width, int height) {
		if (mView == null) {
			Toast.makeText(mContext, "please set view", Toast.LENGTH_SHORT).show();
			return;
		}
		width = width == 0 ? screenWidth : width;
		DensityUtil.measureView(this.mView);
		height = height == 0 ? this.mView.getMeasuredHeight() : height;
		pop.setWidth(width);
		pop.setHeight(height);
		pop.showAsDropDown(view);

	}

	public void closeView() {
		if (pop != null && pop.isShowing()) {
			pop.dismiss();
		}
	}

	private onDismissListener onDismissListener = null;

	public void setOnDismissListener(onDismissListener onDismissListener) {
		this.onDismissListener = onDismissListener;
	}

	public interface onDismissListener {
		public void onDismiss();
	}
}
