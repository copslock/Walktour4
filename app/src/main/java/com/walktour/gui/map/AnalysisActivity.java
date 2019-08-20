package com.walktour.gui.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.walktour.control.adapter.CustomPagerAdapter;
import com.walktour.framework.view.TotalScrollTabActivity;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.List;

public class AnalysisActivity extends TotalScrollTabActivity {

	private Context mContext;
	private ViewPager mPager;
	private CustomPagerAdapter adapter;
	private List<View> views = new ArrayList<View>();
	private LinearLayout switchLayout;
	private OnCustomPageChangeListener onCustomPageChangeListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.analysis_activity);
		mContext = this;
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		mPager = (ViewPager)findViewById(R.id.viewPager);
		switchLayout = (LinearLayout)findViewById(R.id.switch_layout);
		
		//pdfView
		Intent pdfIntent = new Intent(this, ParmPdfActivity.class);
		View pdfView = getLocalActivityManager().startActivity("", pdfIntent).getDecorView();
		views.add(pdfView);
		//TODO 增加其他界面
		
		adapter = new CustomPagerAdapter(views);
		mPager.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		onCustomPageChangeListener = new OnCustomPageChangeListener();
		mPager.setOnPageChangeListener(onCustomPageChangeListener);
		mPager.setCurrentItem(0);
		onCustomPageChangeListener.changeOtherSwitchImage(0);
	}
	
public class OnCustomPageChangeListener implements OnPageChangeListener {
		
		private List<ImageView> switchImages = new ArrayList<ImageView>();
		
		public OnCustomPageChangeListener() {

			switchImages.clear();
			switchLayout.removeAllViews();
			for (int i = 0; i < views.size(); i++) {
				ImageView img = new ImageView(mContext);
				img.setImageResource(R.drawable.darkdot);
				switchImages.add(img);
				switchLayout.addView(img);
				if (i < views.size() - 1) {
					ImageView imgDivider = new ImageView(mContext);
					imgDivider.setImageResource(R.drawable.img_switch_divider);
					switchLayout.addView(imgDivider);
				}
			}
		}
		
		

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			changeOtherSwitchImage(position);
			switchImages.get(position).setImageResource(R.drawable.lightdot);
			
		}
		
		/**
		 * 改变除当前页外的其他的图片的状态
		 * @param current
		 */
		private void changeOtherSwitchImage(int current) {
			for (int i = 0; i < views.size(); i++) {
				if (i != current) {
					switchImages.get(i).setImageResource(R.drawable.darkdot);
				} else {
					switchImages.get(i).setImageResource(R.drawable.lightdot);
				}
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	}
}
