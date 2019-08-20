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
import com.walktour.gui.map.celllist.GSMCellListView;

public class Gsm extends BasicActivity implements ViewSizeLinstener {

	/**
	 * 滑动Layout对象
	 */
	private ScrollLayout scollLayout;

	// private ScrollLayout scollLayout2;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_gsmview);
		scollLayout = (ScrollLayout) findViewById(R.id.srooll_layout);
		// scollLayout2 = (ScrollLayout)findViewById(R.id.srooll_layout_2);
		LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		LinearLayout layout2 = new LinearLayout(this);
		layout2.setOrientation(LinearLayout.VERTICAL);
		GSMCellListView gsmCell = new GSMCellListView(this);
		layout2.addView(gsmCell);

		View gsmView1 = new GsmView(this, 1, this);
		// View gsmView2 = new GsmView(this, 2,this);
		// View gsmView3 = new GsmView(this, 3,this);
		GsmView gsmViewFor4 = new GsmView(this, 4, this);

		scollLayout.addView(gsmView1, layoutParams);
		// twq20130227屏蔽GSM第二页的参数信息，相关的参数未解，需同时隐藏map_gsmview.xml中的imageview多出来的项
		scollLayout.addView(layout2, layoutParams);
		scollLayout.addView(gsmViewFor4, layoutParams);

		// scollLayout2.addView(gsmView2, layoutParams);
		// scollLayout2.addView(gsmView3, layoutParams);

		scollLayout.addChangeListener(new LayoutChangeListener() {
			@Override
			public void doChange(int lastIndex, int currentIndex) {
				(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.darkdot);
				switch (currentIndex) {
				case 0:
					(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.lightdot);
					break;
				case 1:
					(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.lightdot);
					break;
				default:
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
		// height = height + height * 3 /5;
		// gsmViewFor4.setViewHeight(height);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
	}

}
