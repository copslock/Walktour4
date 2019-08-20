package com.walktour.gui.setting.bluetoothmos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.setting.bluetoothmos.fragment.BluetoothMOCSummaryFragment;
import com.walktour.gui.setting.bluetoothmos.fragment.BluetoothMTCSummaryFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Yi.Lin on 2017/12/18.
 * <p>
 * 蓝牙控制tab界面
 */

public class BluetoothMosTabActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private static final String TAG = "BluetoothMosTabActivity";

    @BindView(R.id.frame_fragment_container)
    FrameLayout mFrameLayoutFragmentContainer;
    @BindView(R.id.title_txt)
    TextView titleView;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    /**
     * ButterKnife解绑器
     */
    private Unbinder mUnbinder;

    /**
     * 主叫tab页标志
     */
    private static final int TAB_MOC = 1;
    /**
     * 被叫tab页标志
     */
    private static final int TAB_MTC = 2;

    /**
     * 蓝牙主叫界面fragment
     */
    private BluetoothMOCSummaryFragment mMOCSummaryFragment;
    /**
     * 蓝牙被叫界面fragment
     */
    private BluetoothMTCSummaryFragment mMTCSummaryFragment;

    /**
     * 记录上一个显示的tab页fragment
     */
    private Fragment mPreFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e(TAG,"----onCreate----");
        setContentView(R.layout.activity_tab_bluetooth_mos);
        initTabFragments();
        initView();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this);
        titleView.setText(R.string.task_callMOSBluetooth);
        mTabLayout.addOnTabSelectedListener(this);
        TabLayout.Tab tabMoc = this.mTabLayout.newTab();
        tabMoc.setTag(TAB_MOC);
        TabLayout.Tab tabMtc = this.mTabLayout.newTab();
        tabMtc.setTag(TAB_MTC);
        this.mTabLayout.addTab(tabMoc.setText(R.string.csfb_faild_type_mo));
        this.mTabLayout.addTab(tabMtc.setText(R.string.csfb_faild_type_mt));
    }

    private void initTabFragments() {
        if (null == mMOCSummaryFragment) {
            mMOCSummaryFragment = new BluetoothMOCSummaryFragment();
        }
        if (null == mMTCSummaryFragment) {
            mMTCSummaryFragment = new BluetoothMTCSummaryFragment();
        }
    }

    @OnClick({R.id.pointer})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pointer:
                finish();
                break;
        }
    }


    /**
     * 切换tab页Fragment
     *
     * @param tabId tab页标志
     */
    private void switchTabFragment(int tabId) {
        Fragment curFragment = null;
        if (tabId == TAB_MOC) {
            curFragment = mMOCSummaryFragment;
        } else if (tabId == TAB_MTC) {
            curFragment = mMTCSummaryFragment;
        }
        if (mPreFragment != null && mPreFragment != curFragment && mPreFragment.isAdded()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.hide(mPreFragment).commit();
        }
        if (null != curFragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (curFragment.isAdded()) ft.show(curFragment);
            else ft.add(R.id.frame_fragment_container, curFragment, curFragment.getClass().getSimpleName());
            ft.commit();
            mPreFragment = curFragment;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int tag = (int) tab.getTag();
        switchTabFragment(tag);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
