package com.walktour.gui.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.walktour.framework.ui.BasicActivityGroup;
import com.walktour.framework.view.LayoutChangeListener;
import com.walktour.framework.view.ScrollLayout;
import com.walktour.gui.R;

/**
 * Wlan相关信息页面
 * Author: ZhengLei
 *   Date: 2013-6-25 上午11:35:39
 */
public class Wlan extends BasicActivityGroup {
	private ScrollLayout scrollLayout;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.map_wlan_view);
		scrollLayout = (ScrollLayout) findViewById(R.id.srooll_layout);
		
		Intent apDetailIntent = new Intent(this, APListDetailActivity.class);
			apDetailIntent.putExtra(APListDetailActivity.EXTRA_IS_CONNECTED_AP_INFO, true);
		Intent apListIntent = new Intent(this, APListActivity.class);
		Intent apCurveIntent = new Intent(this, APCurveActivity.class);
		Intent apChannelIntent = new Intent(this, ChannelParamActivity.class);
		
		View v1 = getLocalActivityManager().startActivity("", apDetailIntent).getDecorView();
		View v2 = getLocalActivityManager().startActivity("", apListIntent).getDecorView();
		View v3 = getLocalActivityManager().startActivity("", apCurveIntent).getDecorView();
		View v4 = getLocalActivityManager().startActivity("", apChannelIntent).getDecorView();

		scrollLayout.addView(v1, 0);
		scrollLayout.addView(v2, 1);
		scrollLayout.addView(v3, 2);
		scrollLayout.addView(v4, 3);
		
		scrollLayout.addChangeListener(new LayoutChangeListener() {
			@Override
			public void doChange(int lastIndex, int currentIndex) {
				if (currentIndex == 0) {
					(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.lightdot);
					(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.darkdot);
					(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.darkdot);
					(initImageView(R.id.switch_4)).setBackgroundResource(R.drawable.darkdot);
				} else if (currentIndex == 1) {
					(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.darkdot);
					(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.lightdot);
					(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.darkdot);
					(initImageView(R.id.switch_4)).setBackgroundResource(R.drawable.darkdot);
				}else if (currentIndex == 2) {
					(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.darkdot);
					(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.darkdot);
					(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.lightdot);
					(initImageView(R.id.switch_4)).setBackgroundResource(R.drawable.darkdot);
				}else if (currentIndex == 3) {
					(initImageView(R.id.switch_1)).setBackgroundResource(R.drawable.darkdot);
					(initImageView(R.id.switch_2)).setBackgroundResource(R.drawable.darkdot);
					(initImageView(R.id.switch_3)).setBackgroundResource(R.drawable.darkdot);
					(initImageView(R.id.switch_4)).setBackgroundResource(R.drawable.lightdot);
				}

			}
		});
	}

}
