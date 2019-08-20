package com.walktour.base.gui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.walktour.base.R2;
import com.walktour.base.gui.fragment.BaseFragment;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.StringUtil;

import butterknife.BindView;

/**
 * 带分页的界面基础类
 */
public abstract class BaseTabHostActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {
    /**
     * 页签栏
     */
    @BindView(R2.id.tabs)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mTabLayout.setVisibility(View.VISIBLE);
        if (super.mFragmentList.size() > 4)
            this.mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        else
            this.mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        this.mTabLayout.addOnTabSelectedListener(this);
    }

    /**
     * 新增界面包含的视图
     *
     * @param fragment 视图
     */
    protected int addFragment(BaseFragment fragment) {
        return this.addFragment(fragment, true);
    }

    /**
     * 新增界面包含的视图
     *
     * @param fragment        视图
     * @param isShowInTabHost 当前视图是否要显示在页签栏
     */
    protected int addFragment(BaseFragment fragment, boolean isShowInTabHost) {
        if (fragment == null)
            return -1;
        int index = super.addFragment(fragment);
        if (isShowInTabHost) {
            TabLayout.Tab tab = this.mTabLayout.newTab();
            tab.setTag(index);
            if (!StringUtil.isEmpty(fragment.getTitleName()))
                this.mTabLayout.addTab(tab.setText(fragment.getTitleName().trim()));
            else
                this.mTabLayout.addTab(tab.setText(fragment.getTitleId()));
        }
        return index;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        LogUtil.d(this.getLogTAG(), "----onTabSelected----tag:" + tab.getTag());
        super.showFragment((int) tab.getTag());
    }

    /**
     * 选择指定位置的页签
     *
     * @param position 指定位置
     */
    public void selectTab(int position) {
        if (position < this.mTabLayout.getTabCount())
            this.mTabLayout.getTabAt(position).select();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
