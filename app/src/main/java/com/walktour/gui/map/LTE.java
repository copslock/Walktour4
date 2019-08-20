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
import com.walktour.gui.map.celllist.LTECellListView;

public class LTE extends BasicActivity implements ViewSizeLinstener {

	/**
	 * 滑动Layout对象
	 */
	private ScrollLayout scollLayout;

	private ScrollLayout scollLayout2;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_lteview);

		scollLayout = (ScrollLayout) findViewById(R.id.srooll_layout);
		scollLayout2 = (ScrollLayout) findViewById(R.id.srooll_layout_2);
		scollLayout2.setVisibility(View.GONE);
		LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		LinearLayout layout2 = new LinearLayout(this);
		layout2.setOrientation(LinearLayout.VERTICAL);
		LTECellListView lteCell = new LTECellListView(this);
		layout2.addView(lteCell);

		LTEView lteView1 = new LTEView(this, 1, this);
		LTEView lteViewFor2 = new LTEView(this, 2);
		// LTEView lteView3 = new LTEView(this, 3);
		LTEView lteView4 = new LTEView(this, 4);
		// LTEView lteView5 = new LTEView(this, 5);

		scollLayout.addView(lteView1, layoutParams);
		scollLayout.addView(layout2, layoutParams);
		scollLayout.addView(lteViewFor2, layoutParams);
		scollLayout.addView(lteView4, layoutParams);
		// scollLayout2.addView(layout2,layoutParams);
		// scollLayout2.addView(lteView5,layoutParams);

		scollLayout.addChangeListener(new LayoutChangeListener() {
			@Override
			public void doChange(int lastIndex, int currentIndex) {
				(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_4)).setBackgroundResource(R.drawable.darkdot);
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
				default:
					(initImageView(R.id.switch_4)).setBackgroundResource(R.drawable.lightdot);
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
		// height = height + height * 1 / 3;
		// lteViewFor2.setViewHeight(height);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
	}
}
