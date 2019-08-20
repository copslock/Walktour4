package com.walktour.gui.map;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.walktour.control.config.ConfigRoutine;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.LayoutChangeListener;
import com.walktour.framework.view.ScrollLayout;
import com.walktour.framework.view.ViewSizeLinstener;
import com.walktour.gui.R;
import com.walktour.gui.map.celllist.WCDMACellListView;

public class Umts extends BasicActivity implements ViewSizeLinstener {

	/** 滑动layout */
	private ScrollLayout scrollLayout;
	// private ScrollLayout scrollLayout2;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_umtsview);
		scrollLayout = (ScrollLayout) findViewById(R.id.srooll_layout);
		// scrollLayout2 = (ScrollLayout) findViewById(R.id.srooll_layout_2);
		LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		LinearLayout layout2 = new LinearLayout(this);
		layout2.setOrientation(LinearLayout.VERTICAL);
		WCDMACellListView wcdmaCell = new WCDMACellListView(this);
		layout2.addView(wcdmaCell);

		View wcdmaView1 = new WcdmaView(this, 1, this);
		// View wcdmaView2 = new WcdmaView(this, 2);
		// View wcdmaView3 = new WcdmaView(this, 3);
		WcdmaView wcdmaViewFor4 = new WcdmaView(this, 4);
		scrollLayout.addView(wcdmaView1, layoutParams);
		scrollLayout.addView(layout2, layoutParams);
		scrollLayout.addView(wcdmaViewFor4, layoutParams);

		if (ConfigRoutine.getInstance().showWcdmaExtendParam()) {
			View wcdmaView5 = new WcdmaView(this, 5);
			View wcdmaView6 = new WcdmaView(this, 6);
			View wcdmaView7 = new WcdmaView(this, 7);

			scrollLayout.addView(wcdmaView5, layoutParams);
			scrollLayout.addView(wcdmaView6, layoutParams);
			scrollLayout.addView(wcdmaView7, layoutParams);

			(initImageView(R.id.switch_4)).setVisibility(View.VISIBLE);
			(initImageView(R.id.switch_5)).setVisibility(View.VISIBLE);
			(initImageView(R.id.switch_6)).setVisibility(View.VISIBLE);
		}
		// scrollLayout2.addView(wcdmaView2, layoutParams);
		// scrollLayout2.addView(wcdmaView3, layoutParams);

		scrollLayout.addChangeListener(new LayoutChangeListener() {
			@Override
			public void doChange(int lastIndex, int currentIndex) {
				(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.darkdot);
				if (ConfigRoutine.getInstance().showWcdmaExtendParam()) {
					(initImageView(R.id.switch_4)).setBackgroundResource(R.drawable.darkdot);
					(initImageView(R.id.switch_5)).setBackgroundResource(R.drawable.darkdot);
					(initImageView(R.id.switch_6)).setBackgroundResource(R.drawable.darkdot);
				}
				switch (currentIndex) {
				case 0:
					(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.lightdot);
					break;
				case 1:
					(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.lightdot);
					break;
				case 2:
					(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.lightdot);
					break;
				case 3:
					(initImageView(R.id.switch_4)).setBackgroundResource(R.drawable.lightdot);
					break;
				case 4:
					(initImageView(R.id.switch_5)).setBackgroundResource(R.drawable.lightdot);
					break;
				default:
					(initImageView(R.id.switch_6)).setBackgroundResource(R.drawable.lightdot);
					break;
				}

			}
		});
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param height
	 * @param weidth
	 * @see com.walktour.framework.view.ViewSizeLinstener#onViewSizeChange(int,
	 *      int)
	 */
	@Override
	public void onViewSizeChange(int height, int weidth) {
		// height = height * 2;
		// wcdmaViewFor4.setViewHeight(height);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
	}
}
