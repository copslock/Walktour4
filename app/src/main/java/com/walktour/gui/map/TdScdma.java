package com.walktour.gui.map;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.LayoutChangeListener;
import com.walktour.framework.view.ScrollLayout;
import com.walktour.framework.view.ViewSizeLinstener;
import com.walktour.gui.R;
import com.walktour.gui.map.celllist.TDCellListView;

/**
 * TD-SCDMA解码过程参数显示界面
 * 
 * @author tangwq
 * @version 1.0
 */
public class TdScdma extends BasicActivity implements ViewSizeLinstener {

	/**
	 * 滑动Layout对象
	 */
	private ScrollLayout scollLayout;

	// private ScrollLayout scollLayout2;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_tdscdmaview);

		scollLayout = (ScrollLayout) findViewById(R.id.srooll_layout);
		// scollLayout2 = (ScrollLayout) findViewById(R.id.srooll_layout_2);
		LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		LinearLayout layout2 = new LinearLayout(this);
		layout2.setOrientation(LinearLayout.VERTICAL);
		TDCellListView tdCell = new TDCellListView(this);
		layout2.addView(tdCell);

		TdScdmaView tdScdmaView1 = new TdScdmaView(this, 1, this);
		// TdScdmaView tdScdmaView2 = new TdScdmaView(this, 2);
		TdScdmaView tdsdmaViewFor3 = new TdScdmaView(this, 3);
		// TdScdmaView tdScdmaView4 = new TdScdmaView(this, 4);
		TdScdmaView tdScdmaView5 = new TdScdmaView(this, 5);
		TdScdmaView tdScdmaView6 = new TdScdmaView(this, 6);

		scollLayout.addView(tdScdmaView1, layoutParams);
		scollLayout.addView(layout2, layoutParams);
		scollLayout.addView(tdsdmaViewFor3, layoutParams);
		scollLayout.addView(tdScdmaView5, layoutParams);
		scollLayout.addView(tdScdmaView6, layoutParams);

		// scollLayout2.addView(tdScdmaView2,layoutParams);
		// scollLayout2.addView(tdScdmaView4,layoutParams);

		scollLayout.addChangeListener(new LayoutChangeListener() {
			@Override
			public void doChange(int lastIndex, int currentIndex) {
				(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_4)).setBackgroundResource(R.drawable.darkdot);
				(initImageView(R.id.switch_5)).setBackgroundResource(R.drawable.darkdot);
				switch (currentIndex) {
				case 0:
					(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.lightdot);
					// scollLayout2.setVisibility(View.VISIBLE);
				case 1:
					// scollLayout2.setVisibility(View.GONE);
					(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.lightdot);
					break;
				case 2:
					// scollLayout2.setVisibility(View.GONE);
					(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.lightdot);
					break;
				case 3:
					// scollLayout2.setVisibility(View.GONE);
					(initImageView(R.id.switch_4)).setBackgroundResource(R.drawable.lightdot);
					break;
				default:
					// scollLayout2.setVisibility(View.GONE);
					(initImageView(R.id.switch_5)).setBackgroundResource(R.drawable.lightdot);
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
		// tdsdmaViewFor3.setViewHeight(height);
	}
}
