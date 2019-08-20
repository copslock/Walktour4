package com.walktour.gui.map;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.LayoutChangeListener;
import com.walktour.framework.view.ScrollLayout;
import com.walktour.framework.view.ViewSizeLinstener;
import com.walktour.gui.R;
import com.walktour.gui.map.celllist.CDMACellListView;

public class Cdma extends BasicActivity implements ViewSizeLinstener {

	/**
	 * 滑动Layout对象
	 */
	private ScrollLayout scollLayout;

	private ScrollLayout scollLayout2;

	private CdmaView cdmaViewFor4;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_cdmaview);

		scollLayout = (ScrollLayout) findViewById(R.id.srooll_layout);
		scollLayout2 = (ScrollLayout) findViewById(R.id.srooll_layout_2);
		LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		LinearLayout layout2 = new LinearLayout(this);
		layout2.setOrientation(LinearLayout.VERTICAL);
		CDMACellListView cdmaCell = new CDMACellListView(this);
		layout2.addView(cdmaCell);

		View cdmaView1 = new CdmaView(this, 1, this);
//		View cdmaView2 = new CdmaView(this, 2);
//		View cdmaView3 = new CdmaView(this, 3);
		cdmaViewFor4 = new CdmaView(this, 4);
		scollLayout.addView(cdmaView1, layoutParams);
		scollLayout.addView(layout2, layoutParams);
		//scollLayout.addView(cdmaViewFor4, layoutParams);
//		scollLayout2.addView(cdmaView2, layoutParams);
//		scollLayout2.addView(cdmaView3, layoutParams);

		scollLayout.addChangeListener(new LayoutChangeListener() {
			@Override
			public void doChange(int lastIndex, int currentIndex) {
				(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.darkdot);
				//(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.darkdot);
				switch (currentIndex) {
				case 0:
					(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.lightdot);
					scollLayout2.setVisibility(View.VISIBLE);
					break;
				case 1:
					scollLayout2.setVisibility(View.GONE);
					(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.lightdot);
					break;
				default:
					scollLayout2.setVisibility(View.GONE);
					(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.lightdot);
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
		height = height + height * 1 / 3;
		cdmaViewFor4.setViewHeight(height);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
	}
}
