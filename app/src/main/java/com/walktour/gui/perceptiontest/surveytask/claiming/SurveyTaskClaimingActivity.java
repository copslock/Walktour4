package com.walktour.gui.perceptiontest.surveytask.claiming;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.walktour.base.util.ToastUtil;
import com.walktour.gui.R;
import com.walktour.gui.perceptiontest.surveytask.claiming.adapter.SurveyTaskClaimingPagerAdapter;
import com.walktour.gui.perceptiontest.surveytask.claiming.event.LocationEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yi.Lin on 2018/11/18.
 * 勘测任务认领界面，包括待领取、已领取
 */

public class SurveyTaskClaimingActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    //    @BindView(R.id.toolbar)
    //    Toolbar mToolbar;

    @BindView(R.id.toolbar_title)
    TextView mTvTitle;

    private LocationClient mLocClient;
    private ProgressDialog mProgressDialog;

    private SurveyTaskClaimingPagerAdapter mPagerAdapter;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<String> mTitles = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_task_claiming);
        ButterKnife.bind(this);
        //Must init Baidu map sdk before,when use Baidu map api !
        SDKInitializer.initialize(getApplicationContext());
        initViews();
        initLocation();
    }

    /**
     * 设置标题栏
     *
     * @param title 标题
     */
    protected void setToolbarTitle(String title) {
        //        ActionBar actionBar = getSupportActionBar();
        //        if (actionBar == null)
        //            return;
        //为标题栏设置标题，即给ActionBar设置标题。
        this.mTvTitle.setText(title);
        //        actionBar.setTitle("");
        //        //ActionBar加一个返回图标
        //        actionBar.setDisplayHomeAsUpEnabled(true);
        //        //不显示当前程序的图标。
        //        actionBar.setDisplayShowHomeEnabled(false);
    }

    @OnClick(R.id.ib_back)
    void back() {
        finish();
    }

    private void initViews() {
        //        setSupportActionBar(this.mToolbar);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("正在定位...");

        setToolbarTitle("任务管理");

        if (mFragments.isEmpty()) {
            mFragments.add(new UnclaimedSurveyTaskFragment());
            mFragments.add(new ClaimedSurveyTaskFragment());
        }
        if (mTitles.isEmpty()) {
            mTitles.add("待领取");
            mTitles.add("已领取");
        }

        mTabLayout.addTab(mTabLayout.newTab().setText(mTitles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitles.get(1)));

        mPagerAdapter = new SurveyTaskClaimingPagerAdapter(getSupportFragmentManager(), mFragments, mTitles);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    @OnClick(R.id.ib_location)
    void clickLocation() {
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        mLocClient.start();
        mLocClient.requestLocation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initLocation() {
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                mLocClient.stop();
                if (location == null) {
                    ToastUtil.showLong(SurveyTaskClaimingActivity.this, "定位失败");
                    return;
                }
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                if (latitude == 0
                        && longitude == 0) {
                    ToastUtil.showLong(SurveyTaskClaimingActivity.this, "定位失败");
                    return;
                }
                ToastUtil.showLong(SurveyTaskClaimingActivity.this, "定位成功");
                LocationEvent event = new LocationEvent();
                event.lat = latitude;
                event.lng = longitude;
                EventBus.getDefault().post(event);
            }
        });
        LocationClientOption locationOption = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
        mLocClient.setLocOption(locationOption);

    }
}
