package com.walktour.gui.singlestation.test.fragment;

import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.walktour.base.gui.fragment.BaseFragment;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.R2;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.test.component.DaggerLocalStationMapFragmentComponent;
import com.walktour.gui.singlestation.test.module.LocalStationMapFragmentModule;
import com.walktour.gui.singlestation.test.presenter.LocalStationMapFragmentPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * 基站测试本地基站地图界面
 * Created by wangk on 2017/6/13.
 */

public class LocalStationMapFragment extends BaseFragment implements BaiduMap.OnMarkerClickListener {
    /**
     * 日志标识
     */
    private static final String TAG = "LocalStationMapFragment";
    /**
     * 界面交互类
     */
    @Inject
    LocalStationMapFragmentPresenter mPresenter;
    /**
     * 基站列表
     */
    @BindView(R2.id.map_view)
    MapView mMapView;
    /**
     * 百度地图
     */
    private BaiduMap mMap;

    public LocalStationMapFragment() {
        super(R.string.single_station_local_station, R.layout.fragment_single_station_test_local_map);
    }

    @Override
    public String getLogTAG() {
        return TAG;
    }

    @Override
    public BaseFragmentPresenter getPresenter() {
        return this.mPresenter;
    }

    @Override
    protected void setupFragmentComponent() {
        DaggerLocalStationMapFragmentComponent.builder().localStationMapFragmentModule(new LocalStationMapFragmentModule(this)).build().inject(this);
    }

    @Override
    protected void onCreateView() {
        SDKInitializer.initialize(this.getActivity().getApplicationContext());
        if (this.mMapView != null) {
            this.mMap = this.mMapView.getMap();
            this.mMap.setMyLocationEnabled(true);
            this.mMap.setOnMarkerClickListener(this);
        }
    }

    @Override
    public int[] showActivityMenuItemIds() {
        return new int[]{R.id.menu_singlestation_show_list};
    }

    /**
     * 设置当前的位置
     *
     * @param latitude  经度
     * @param longitude 纬度
     */
    public void setLocation(double latitude, double longitude) {
        LogUtil.d(TAG, "--------setLocation-------------");
        // 此处设置开发者获取到的方向信息，顺时针0-360
        MyLocationData locData = new MyLocationData.Builder().latitude(latitude).longitude(longitude).build();
        mMap.setMyLocationData(locData);
        mMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17));
        mMap.setMyLocationEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.mMapView != null)
            this.mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mMapView != null)
            this.mMapView.onDestroy();
        this.mMap = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.mMapView != null)
            this.mMapView.onPause();
    }

    /**
     * 显示基站数据
     *
     * @param stationList 基站列表
     */
    public void showFragment(List<StationInfo> stationList) {
        this.mMap.clear();
        for (StationInfo stationInfo : stationList) {
            //定义Maker坐标点
            LatLng point = new LatLng(stationInfo.getLatitude(), stationInfo.getLongitude());
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(this.getStationImage(stationInfo));
            //构建MarkerOption，用于在地图上添加Marker
            MarkerOptions option = new MarkerOptions().position(point).icon(bitmap).title(stationInfo.getCode());
            Bundle bundle = new Bundle();
            bundle.putParcelable("station_info", stationInfo);
            option.extraInfo(bundle);
            //在地图上添加Marker，并显示
            mMap.addOverlay(option);
        }
    }

    /**
     * 获取基站显示图片
     *
     * @param stationInfo 基站信息
     * @return 图片
     */
    private int getStationImage(StationInfo stationInfo) {
        if (stationInfo.getType() == SingleStationDaoManager.STATION_TYPE_INDOOR) {
            if (stationInfo.getTestStatus() == StationInfo.TEST_STATUS_INIT) {
                return R.drawable.singlestation_indoor_untest;
            } else {
                return R.drawable.singlestation_indoor_tested;
            }
        } else {
            if (stationInfo.getTestStatus() == StationInfo.TEST_STATUS_INIT) {
                return R.drawable.singlestation_outdoor_untest;
            } else {
                return R.drawable.singlestation_outdoor_tested;
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        StationInfo stationInfo = marker.getExtraInfo().getParcelable("station_info");
        this.mPresenter.jumpToNextActivity(stationInfo);
        return true;
    }
}
