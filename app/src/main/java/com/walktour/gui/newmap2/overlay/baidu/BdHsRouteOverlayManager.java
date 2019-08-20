package com.walktour.gui.newmap2.overlay.baidu;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.DensityUtil;
import com.walktour.gui.R;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap.hs.HsStationOperatePopWindow;
import com.walktour.gui.newmap.metro.MetroStationOperatePopWindow;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.ui.GpsLocasView;
import com.walktour.gui.newmap2.util.BaiduMapUtil;
import com.walktour.service.metro.HsFactory;
import com.walktour.service.metro.MetroFactory;
import com.walktour.service.metro.model.MetroCity;
import com.walktour.service.metro.model.MetroRoute;
import com.walktour.service.metro.model.MetroStation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 地铁
 *
 * @author zhicheng.chen
 * @date 2018/6/13
 */
public class BdHsRouteOverlayManager extends BaseOverlayManager {

    private static final String EXTRA_METRO_INFO = "EXTRA_METRO_INFO";

    /**
     * 地铁图层的工厂类
     */
    private HsFactory mFactory;
    /**
     * 当前选择的线路的所有站点
     */
    private List<MetroStation> mStations = new ArrayList<MetroStation>();

    private HsStationOperatePopWindow mWindow;
    private int textColor = 0xFF333333;
    private int lineColor = 0XFF00A3E6;
    private Marker mMarker;
    private List<Overlay> mAllOverlayList;
    private boolean mIsMapLoadFinish;//判断地图是不是加载完成
    private ExecutorService MetroThreadPool = Executors.newFixedThreadPool(3);

    public BdHsRouteOverlayManager(Context context) {
        super(context);
        this.mFactory = HsFactory.getInstance(mContext);
    }

    @Override
    public boolean onMapClick(Object... obj) {
        if (mWindow != null && mWindow.isShow()) {
            mWindow.closePopWindow();
        }
        return true;
    }


    @Override
    public boolean onMarkerClick(Object... obj) {
        if (mFactory.isRuning()) {
            Marker marker = (Marker) obj[0];
            if (marker != null) {
                Bundle bundle = marker.getExtraInfo();
                if (bundle != null
                        && bundle.containsKey(EXTRA_METRO_INFO)) {
                    MetroStation currentStation = this.mFactory.getCurrentStation();
                    if (currentStation != null) {
                        if (this.mWindow == null) {
                            this.mWindow = new HsStationOperatePopWindow(mMapSdk.getMapView(), mContext, currentStation);
                        }
                        this.mWindow.setStation(currentStation);
                        if (!this.mWindow.isShow()) {
                            this.mWindow.showPopWindow();
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public OverlayType getOverlayType() {
        return OverlayType.HsRoute;
    }

    @Override
    public boolean addOverlay(Object... obj) {
        if (!mFactory.isRuning()) {
            clearOverlay();
            return false;
        }
        MetroThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (mFactory) {
                    MetroStation currentStation = mFactory.getCurrentStation();
                    if (currentStation != null && mIsMapLoadFinish) {
                        MyLatLng baiduLatLng = currentStation.getBaiduLatLng();
                        if (baiduLatLng != null) {
                            LatLng bdLL = new LatLng(baiduLatLng.latitude, baiduLatLng.longitude);
                            if (mMarker == null) {
                                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.iconmarker2);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(EXTRA_METRO_INFO, currentStation);
                                OverlayOptions option = new MarkerOptions()
                                        .position(bdLL)
                                        .icon(icon)
                                        .extraInfo(bundle)
                                        .zIndex(10);
                                if (getMapControllor() != null) {
                                    mMarker = (Marker) getMapControllor().addOverlay(option);
                                }
                            } else {
                                mMarker.setPosition(bdLL);
                            }
                        }
                    }
                }
            }
        });
        return true;
    }

    @Override
    public void onMapLoaded() {
        mIsMapLoadFinish = true;
        if (!this.mFactory.isRuning() && !DatasetManager.isPlayback) {
            return;
        }
        MetroThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    mFactory.init(mContext);
                    updateData();
                    addMetroRountOverlay();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onMapStatusChangeFinish(Object... obj) {
        MetroThreadPool.execute(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    /**
     * 添加地铁线路和站点名称
     */
    private void addMetroRountOverlay() {

        List<OverlayOptions> optionList = new ArrayList<>();
        GpsLocasView view = new GpsLocasView(mContext);
        view.setColor(lineColor);
        view.setStrokeColor(lineColor);
        view.setRadius(DensityUtil.dip2px(mContext, 8));
        BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);

        for (MetroStation station : mStations) {
            if (station.getState() != MetroStation.STATE_CANT_SELECT){
                MyLatLng baiduLatLng = station.getBaiduLatLng();
                LatLng ll = new LatLng(baiduLatLng.latitude, baiduLatLng.longitude);
                Bundle bundle = new Bundle();
                bundle.putSerializable(EXTRA_METRO_INFO, station);
                OverlayOptions markerOpt = new MarkerOptions()
                        .icon(icon)
                        .extraInfo(bundle)
                        .position(ll);
                optionList.add(markerOpt);


                OverlayOptions textOpt = new TextOptions()
                        .text(station.getName())
                        .fontColor(textColor)
                        .bgColor(Color.WHITE)
                        .align(TextOptions.ALIGN_CENTER_HORIZONTAL, TextOptions.ALIGN_TOP)
                        .fontSize(DensityUtil.dip2px(mContext, 9))
                        .zIndex(9)
                        .position(ll);
                optionList.add(textOpt);
            }
        }
        if (getMapControllor() != null) {
            mAllOverlayList = getMapControllor().addOverlays(optionList);
        }
    }

    /**
     * 更新数据
     */
    private void updateData() {
        MetroRoute currentRoute = this.mFactory.getCurrentRoute(DatasetManager.isPlayback);
        this.mStations.clear();
        if (currentRoute != null) {
            this.adjustLatLng(currentRoute);
            for (int i = 0; i < currentRoute.getStations().size(); i++) {
                MetroStation station = currentRoute.getStations().get(i);
                if (station.getBaiduLatLng() == null) {
                    LatLng ll = BaiduMapUtil.convert(station.getLatLng().latitude, station.getLatLng().longitude);
                    station.setBaiduLatLng(new MyLatLng(ll.latitude, ll.longitude));
                }
            }
            this.mStations.addAll(currentRoute.getStations());
        }
    }

    /**
     * 校正地铁路线的经纬度坐标
     *
     * @param route 地铁路线
     */
    private void adjustLatLng(MetroRoute route) {
        if (route.getBaiduKml().isEmpty() && !route.getKml().isEmpty()) {
            List<MyLatLng> list = new ArrayList<MyLatLng>();
            for (MyLatLng latlng : route.getKml()) {
                LatLng ll = BaiduMapUtil.convert(latlng.latitude, latlng.longitude);
                list.add(new MyLatLng(ll.latitude, ll.longitude));
            }
            route.setBaiduKml(list);
        }
    }


    @Override
    public boolean clearOverlay() {
        if (mMarker != null) {
            mMarker.cancelAnimation();
            mMarker.remove();
            mMarker = null;
        }
        if (mAllOverlayList != null) {
            for (Overlay overlay : mAllOverlayList) {
                overlay.remove();
            }
            mAllOverlayList.clear();
        }
        return true;
    }

    private BaiduMap getMapControllor() {
        return (BaiduMap) mMapSdk.getMapControllor();
    }

    @Override
    public void onDestory() {
        super.onDestory();
        if (mWindow != null) {
            mWindow.closePopWindow();
        }

        if (!MetroThreadPool.isShutdown()) {
            MetroThreadPool.shutdown();
        }
    }
}
