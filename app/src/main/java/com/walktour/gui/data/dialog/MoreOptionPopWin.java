package com.walktour.gui.data.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.walktour.Utils.DensityUtil;
import com.walktour.gui.R;

@SuppressLint("InflateParams")
public class MoreOptionPopWin {

	public  PopupWindow pop;
	
	public View view;
	
	public Button mButtonOne;
	
	public Button mButtonTwo;
	
	public Button mButtonThree;
	
	public MoreOptionPopWin(Context context) {
		init(context);
	}
	
	public void showMenu(View v) {
		int xOffset = 0;
		int yOffset = 0;
		xOffset = v.getWidth() - pop.getWidth();
		yOffset = -((pop.getHeight() - v.getHeight())/2 + v.getHeight());
		pop.showAsDropDown(v, xOffset, yOffset);
	}
	
	public void closeMenu() {
		if (pop != null && pop.isShowing()) {
			pop.dismiss();
		}
	}
	
	public View getView() {
		return this.view;
	}
	
	public PopupWindow getPopupWindow() {
		return this.pop;
	}
	
	@SuppressWarnings("deprecation")
	private void init(Context context) {
		view = LayoutInflater.from(context).inflate(R.layout.data_manager_more_menu, null);
		mButtonOne = (Button)view.findViewById(R.id.button_1);
		mButtonTwo = (Button)view.findViewById(R.id.button_2);
		mButtonThree = (Button)view.findViewById(R.id.button_3);
		if (pop == null) {
			pop = new PopupWindow(view, DensityUtil.dip2px(context, 200),DensityUtil.dip2px(context, 60), true);
			pop.setOutsideTouchable(true);
			pop.setFocusable(true);
			pop.setTouchable(true);
			pop.setBackgroundDrawable(new BitmapDrawable());
			pop.setAnimationStyle(R.style.popwin_anim_style);
		}
	}
}
