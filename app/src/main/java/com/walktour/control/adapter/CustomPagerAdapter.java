package com.walktour.control.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CustomPagerAdapter  extends PagerAdapter{
	
	private List<View> mViewList = new ArrayList<View>();
	
	public CustomPagerAdapter(List<View> viewList) {
		this.mViewList.clear();
		this.mViewList.addAll(viewList);
	}
	@Override
	public int getCount() {
		return mViewList.size();
	}
     @Override
	 @SuppressWarnings("deprecation")
     public void destroyItem(View arg0, int arg1, Object arg2) {
             ((ViewPager) arg0).removeView(mViewList.get(arg1));
     }  
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		 ((ViewPager) container).addView(mViewList.get(position), 0);  
		return mViewList.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	public void notifyDataSetChanged(List<View> viewList) {
		this.mViewList.clear();
		this.mViewList.addAll(viewList);
		super.notifyDataSetChanged();
	}
	
}
