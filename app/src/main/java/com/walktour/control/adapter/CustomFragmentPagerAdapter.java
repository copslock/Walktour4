package com.walktour.control.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.walktour.Utils.ApplicationModel;
import com.walktour.gui.map.DynamicParamterOtherFragment;

import java.util.ArrayList;

/**
 * 根据缓存刷新动态参数界面,解决刷新异常问题
 * 
 * @author zhihui.lian
 */
public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<Fragment> fragmentsList;

	public CustomFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public CustomFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
		super(fm);
		this.fragmentsList = fragments;
	}

	public ArrayList<Fragment> getFragments() {
		return this.fragmentsList;
	}

	@Override
	public int getCount() {
		return fragmentsList.size();
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragmentsList.get(arg0);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (ApplicationModel.getInstance().isTestJobIsRun() && position == 0) {
			Fragment fragment = (Fragment) super.instantiateItem(container, position);
			return fragment;
		}
		if (position > 1) {
			DynamicParamterOtherFragment parm = (DynamicParamterOtherFragment) super.instantiateItem(container, position);
			parm.setPage(((DynamicParamterOtherFragment) fragmentsList.get(position)).getPage());
			return parm;
		}
		Fragment fragment = (Fragment) super.instantiateItem(container, position);
		return fragment;
	}
}
