package com.walktour.gui.perceptiontest.surveytask.claiming.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Yi.Lin on 2018/11/18.
 */

public class SurveyTaskClaimingPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;
    private List<String> mTitles;

    public SurveyTaskClaimingPagerAdapter(FragmentManager fm, List<Fragment> fragments,List<String> titles) {
        super(fm);
        this.mFragmentList = fragments;
        this.mTitles = titles;
    }


    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return null == mFragmentList ? 0 : mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
