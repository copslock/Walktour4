package com.walktour.gui.applet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.walktour.gui.R;

/***
 * 底部工具栏，包含7个Button,默认7个Button为View.GONE
 * 
 * @author weirong.fan
 *
 */
public class ControlBar extends FrameLayout {
	private Context context;
	private Button[] buttons;

	public ControlBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findView();
	}

	@SuppressLint("InflateParams")
	private void findView() {
		LayoutInflater factory = LayoutInflater.from(context);
		View view = factory.inflate(R.layout.controlbar, null);
		//
		this.addView(view);

		buttons = new Button[7];
		buttons[0] = (Button) view.findViewById(R.id.Button01);
		buttons[1] = (Button) view.findViewById(R.id.Button02);
		buttons[2] = (Button) view.findViewById(R.id.Button03);
		buttons[3] = (Button) view.findViewById(R.id.Button04);
		buttons[4] = (Button) view.findViewById(R.id.Button05);
		buttons[5] = (Button) view.findViewById(R.id.Button06);
		buttons[6] = (Button) view.findViewById(R.id.Button07);
	}

	public Button getButton(int position, int color) {
		if (position < 0 || position > 6) {
			return null;
		}
		buttons[position].setVisibility(View.VISIBLE);
		buttons[position].setTextColor(color);
		return buttons[position];
	}

	/**
	 * 从工具栏目得到Button对象：0-6
	 */
	public Button getButton(int position) {
		if (position < 0 || position > 6) {
			return null;
		}
		buttons[position].setVisibility(View.VISIBLE);
		return buttons[position];
	}

	/**
	 * 获取所有按钮
	 * 
	 * @return
	 */
	public Button[] getAllButtons() {
		for (int i = 0; i <= 6; i++) {
			buttons[i].setVisibility(View.VISIBLE);
		}
		return buttons;
	}

	public void setButtonsListener(OnClickListener listener) {
		for (Button x : buttons) {
			x.setOnClickListener(listener);
		}
	}

}