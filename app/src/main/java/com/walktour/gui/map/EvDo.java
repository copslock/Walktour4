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
import com.walktour.gui.map.celllist.EVDOCellListView;

public class EvDo extends BasicActivity implements ViewSizeLinstener {

	/**
	 * 滑动Layout对象
	 */
	private ScrollLayout scollLayout;

	private ScrollLayout scollLayout2;

	private EvDoView evDoViewFor2;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_evdoview);

		scollLayout = (ScrollLayout) findViewById(R.id.srooll_layout);
		scollLayout2 = (ScrollLayout) findViewById(R.id.srooll_layout_2);
		LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		LinearLayout layout2 = new LinearLayout(this);
		layout2.setOrientation(LinearLayout.VERTICAL);
		EVDOCellListView evdoCell = new EVDOCellListView(this);
		layout2.addView(evdoCell);

		View evdoView1 = new EvDoView(this, 1, this);
		evDoViewFor2 = new EvDoView(this, 2);
//		View evdoView3 = new EvDoView(this, 3);
//		View evdoView4 = new EvDoView(this, 4);
//		View evdoView5 = new EvDoView(this, 5);

		scollLayout.addView(evdoView1, layoutParams);
		scollLayout.addView(layout2, layoutParams);
		// TODO 由于木有参，暂时屏蔽
		scollLayout.addView(evDoViewFor2, layoutParams);
		//scollLayout.addView(evdoView4, layoutParams);
//		scollLayout2.addView(evdoView3);
//		scollLayout2.addView(evdoView5);
		scollLayout.addChangeListener(new LayoutChangeListener() {
			@Override
			public void doChange(int lastIndex, int currentIndex) {
				(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.darkdot);
				//(initImageView(R.id.switch_4)).setBackgroundResource(R.drawable.darkdot);
				switch (currentIndex) {
				case 0:
					(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.lightdot);
					scollLayout2.setVisibility(View.VISIBLE);
				case 1:
					scollLayout2.setVisibility(View.GONE);
					(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.lightdot);
					break;
				case 2:
					scollLayout2.setVisibility(View.GONE);
					(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.lightdot);
				default:
					scollLayout2.setVisibility(View.GONE);
					(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.lightdot);
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
		height = height * 2;
		evDoViewFor2.setViewHeight(height);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
	}

}
