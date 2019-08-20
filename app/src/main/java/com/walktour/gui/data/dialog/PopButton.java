package com.walktour.gui.data.dialog;

//import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.walktour.Utils.DensityUtil;
import com.walktour.gui.R;

@SuppressLint("InflateParams")
public class PopButton {
	private View mView = null;
	private Context mContext;
	private View mButtonView;
	private Button mButtonOK;
	private Button mButtonCancel;
	private PopupWindow pop;
//	private int screenWidth = 0;
//	private int screenHeight = 0;
//	private int statusBarHeight = 0;
	private int mButtonViewHeight = 0;
//	private int navigationBarHeight = 0;
;	
	public PopButton(Context context, View view) {
		this.mContext = context;
		this.mView = view;
		initView();
		getStatusBarHeight();
	}
	
	private void initView() {
		mButtonView = LayoutInflater.from(mContext).inflate(R.layout.pop_button, null);
		if (mView != null) {
			((LinearLayout)mButtonView.findViewById(R.id.button_view)).addView(this.mView);
			mButtonView.findViewById(R.id.default_layout).setVisibility(View.GONE);
		}
		mButtonOK = (Button)mButtonView.findViewById(R.id.btn_ok);
		mButtonCancel = (Button)mButtonView.findViewById(R.id.btn_cancel);
		
	}
	
	public PopButton setOKButtonText(String text) {
		mButtonOK.setText(text);
		return this;
	}
	
	public PopButton setCancelButtonText(String text) {
		mButtonCancel.setText(text);
		return this;
	}
	
	public PopButton setOKButtonListener(final ClickListener clickListener) {
		mButtonOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				clickListener.onClick();
				close();
			}
		});

		return this;
	}
	
	public PopButton setCancelButtonListener(final ClickListener clickListener) {
		mButtonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				clickListener.onClick();
				close();
			}
		});

		return this;
	}
	
	@SuppressWarnings("deprecation")
	public void show() {

		if (pop == null) {
			pop = new PopupWindow(mButtonView, LayoutParams.FILL_PARENT, mButtonViewHeight, true);
			pop.setOutsideTouchable(true);
			pop.setFocusable(false);
			pop.setTouchable(true);
			pop.setAnimationStyle(R.style.popwin_anim_up_in_style);
			pop.showAtLocation(mButtonView, Gravity.BOTTOM, 0, 0);//
		} else {
			if (pop.isShowing()) { 
				pop.dismiss();
			} else {
				pop.showAtLocation(mButtonView, Gravity.BOTTOM, 0, 0);//
			}
		}
	}
	
	public void close() {
		if (pop != null && pop.isShowing()) {
			pop.dismiss();
		}
	}

	public PopButton setButtonListener(int buttonId, final ClickListener clickListener, final boolean close) {
			this.mView.findViewById(buttonId).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					clickListener.onClick();
					if (close) close();
				}
			});

		return this;
	}
	
	public void setButtonClickable(int buttonId, boolean clickable) {
		if (clickable) {
			((Button)this.mButtonView.findViewById(buttonId)).setTextColor(mContext.getResources().getColor(R.color.app_main_text_color));
		} else {
			((Button)this.mButtonView.findViewById(buttonId)).setTextColor(mContext.getResources().getColor(R.color.gray));
		}
		this.mButtonView.findViewById(buttonId).setClickable(clickable);
	}
	
	public void setButtonText(int buttonId, String text) {
		((Button)this.mButtonView.findViewById(buttonId)).setText(text);
	}
	
	private void getStatusBarHeight() {

		DisplayMetrics  dm = new DisplayMetrics();
		((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		mButtonViewHeight = DensityUtil.dip2px(mContext, 52);
//		screenWidth = dm.widthPixels;
//		screenHeight = dm.heightPixels;
		
//		Class<?> c = null;
//		 
//	      Object obj = null;
//	 
//	      Field field = null;
//	 
//	      int x = 0, sbar = 0;
//		 
//		  Field field2 = null;
//		      
//		  int x2 = 0;
//	 
//	try {
//	 
//	        c = Class.forName("com.android.internal.R$dimen");
//	 
//	        obj = c.newInstance();
//	 
//	        field = c.getField("status_bar_height");
//	 
//	        x = Integer.parseInt(field.get(obj).toString());
//	 
//	        statusBarHeight = mContext.getResources().getDimensionPixelSize(x);
//	        //-----------------------------------------------------
//	        field2 = c.getField("navigation_bar_height");
//	        x2 = Integer.parseInt(field2.get(obj).toString());
//	        navigationBarHeight = mContext.getResources().getDimensionPixelSize(x2);
//	 
//	} catch (Exception e1) {
//	        e1.printStackTrace();
//	}
	}
	
	public interface ClickListener {
		void onClick();
	}
}
