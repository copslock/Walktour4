package com.walktour.gui.singlestation.survey.activity;

import android.view.Menu;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.walktour.base.gui.activity.BaseTabHostActivity;
import com.walktour.base.gui.presenter.BaseActivityPresenter;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.singlestation.survey.component.DaggerSurveyActivityComponent;
import com.walktour.gui.singlestation.survey.fragment.SurveyHistoryFragment;
import com.walktour.gui.singlestation.survey.module.SurveyActivityModule;
import com.walktour.gui.singlestation.survey.presenter.SurveyActivityPresenter;
import com.walktour.gui.singlestation.test.fragment.LocalStationListFragment;
import com.walktour.gui.singlestation.test.fragment.LocalStationMapFragment;
import com.walktour.gui.singlestation.test.fragment.StationSearchFragment;

import javax.inject.Inject;

/**
 * 基站勘查主界面
 * Created by wangk on 2017/7/14.
 */

public class SurveyActivity extends BaseTabHostActivity {
    /**
     * 日志标识
     */
    private static final String TAG = "SurveyActivity";

    /**
     * 定位客户端
     */
    private LocationClient mLocClient;

    /**
     * 关联交互类
     */
    @Inject
    SurveyActivityPresenter mPresenter;


    @Override
    protected void onCreate() {
        super.setToolbarTitle(R.string.single_station_survey);
        SDKInitializer.initialize(this.getApplicationContext());
        this.initLocation();
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        this.mLocClient = new LocationClient(this.getApplicationContext());
        this.mLocClient.registerLocationListener(new SurveyActivity.MyLocationListenner());
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(LocationClientOption.MIN_SCAN_SPAN);
        this.mLocClient.setLocOption(option);
        this.mLocClient.start();
        // 发起POI查询请求。请求过程是异步的，定位结果在上面的监听函数onReceivePoi中获取。
        if (mLocClient != null && mLocClient.isStarted())
            mLocClient.requestLocation();

    }

    /**
     * 定位SDK监听函数
     */
    private class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不再处理新接收的位置
            if (location == null)
                return;
            MyLatLng latlng = new MyLatLng(location.getLatitude(), location.getLongitude());
            LogUtil.d(TAG, "--------onReceiveLocation-----locType:" + location.getLocType() + "-----lat:" + latlng.latitude
                    + "----lng:" + latlng.longitude);
            if (location.getLocType() != BDLocation.TypeNetWorkLocation || (latlng.latitude == 0 && latlng.longitude == 0)) {
                return;
            }
            // 此处设置开发者获取到的方向信息，顺时针0-360
            mPresenter.setLatitude(location.getLatitude());
            mPresenter.setLongitude(location.getLongitude());
            mPresenter.loadData();
            mLocClient.stop();
        }
    }

    @Override
    protected void setupActivityComponent() {
        DaggerSurveyActivityComponent.builder().surveyActivityModule(new SurveyActivityModule(this)).build().inject(this);
    }

    @Override
    public BaseActivityPresenter getPresenter() {
        return this.mPresenter;
    }


    @Override
    protected void initFragments() {
        LocalStationListFragment localStationListFragment = new LocalStationListFragment();
        LocalStationMapFragment localStationMapFragment = new LocalStationMapFragment();
        this.getIntent().putExtra("activity", this.getLogTAG());
        super.addFragment(localStationListFragment);
        super.addFragment(new StationSearchFragment());
        super.addFragment(localStationMapFragment, false);
        this.getIntent().putExtra("station_list_fragment_index", localStationListFragment.getIndex());
        this.getIntent().putExtra("station_map_fragment_index", localStationMapFragment.getIndex());
        SurveyHistoryFragment surveyHistoryFragment = new SurveyHistoryFragment();
        super.addFragment(surveyHistoryFragment);
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    /**
     * 生成顶部菜单栏
     *
     * @param menu 菜单对象
     * @return 是否有生成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu, R.menu.singlestation_survey_station_menu);
    }

}
