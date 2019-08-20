package com.walktour.gui.newmap2.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.gui.R;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap2.NewMapActivity;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.overlay.baidu.BdStationOverlayManager;
import com.walktour.gui.newmap2.util.BaiduMapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 百度地图
 *
 * @author zhicheng.chen
 * @date 2018/6/5
 */
public class BingMapSdk implements IMapSdk,
        BaiduMap.OnMapStatusChangeListener,
        BaiduMap.OnMarkerClickListener,
        BaiduMap.OnMapLoadedCallback,
        BaiduMap.OnMapClickListener,
        BaiduMap.OnMapLongClickListener, MKOfflineMapListener {

    private static final String TAG = BingMapSdk.class.getSimpleName();
    private Context mCt;
    private MapView mMapView;
    private BaiduMap mMap;
    private MapCallBack mMapCallBack;

    //地图缩放比例存储key值
    public static final String EXTRA_BD_MAP_ZOOM_LEVEL = "EXTRA_BD_MAP_ZOOM_LEVEL";
    //地图纬度存储key值
    public static final String EXTRA_BD_MAP_LAT = "EXTRA_BD_MAP_LAT";
    //地图经度存储key值
    public static final String EXTRA_BD_MAP_LNG = "EXTRA_BD_MAP_LNG";
    /**
     * 存放所有的overlay管理者
     */
    private List<BaseOverlayManager> mOverlayManagers = new ArrayList<>();
    /**
     * 地图界面是否初始化完成
     */
    protected boolean isInit = false;
    /**
     * 定位客户端
     */
    private LocationClient mLocClient;
    private SharedPreferences mSpf;
    private SharedPreferences mPreferences;//存储设置
    private MKOfflineMap mkOfflineMap;
    private String locationInfo="";
    private MapCallBack<String> locationCallBack;
    private boolean isFocus=false;

    public BingMapSdk(Context ct) {
        mCt = ct;
        if (mCt instanceof MapCallBack) {
            mMapCallBack = (MapCallBack) mCt;
        }
        mSpf = PreferenceManager.getDefaultSharedPreferences(mCt);
        initMap();
        setMapListener();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mCt);
    }

    private void initMap() {
        BaiduMapOptions option = new BaiduMapOptions().zoomControlsEnabled(true)
                .overlookingGesturesEnabled(false)
                .rotateGesturesEnabled(false);
        this.mMapView = new MapView(mCt, option);
        this.mMapView.setClickable(true);
        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){
            child.setVisibility(View.INVISIBLE);
        }
        // 不显示地图上比例尺
        mMapView.showScaleControl(false);

        // 不显示地图缩放控件（按钮控制栏）
        mMapView.showZoomControls(false);
        this.mMap = mMapView.getMap();
        this.mMap.setMapType(NewMapFactory.getInstance().getMapType());
        this.mMap.setMaxAndMinZoomLevel(NewMapFactory.getInstance().getZoomLevelMax(), NewMapFactory.getInstance().getZoomLevelMin());

        int zoom = mSpf.getInt(EXTRA_BD_MAP_ZOOM_LEVEL, 12);
        NewMapFactory.getInstance().setZoomLevelNow(zoom);
        double lat = Double.parseDouble(mSpf.getString(EXTRA_BD_MAP_LAT, "0.0"));
        double lng = Double.parseDouble(mSpf.getString(EXTRA_BD_MAP_LNG, "0.0"));
        this.mMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));

        NewMapFactory.getInstance().setMapType(getMapType());

        initLocation();
    }

    private void setMapListener() {
        this.mMap.setOnMapLongClickListener(this);
        this.mMap.setOnMapLoadedCallback(this);
        this.mMap.setOnMarkerClickListener(this);
        this.mMap.setOnMapStatusChangeListener(this);
        this.mMap.setOnMapClickListener(this);
    }

    @Override
    public Object getMapControllor() {
        return mMap;
    }

    @Override
    public int getLayoutId() {
        return R.layout.map_bind_main_activity;
    }

    @Override
    public View getMapView() {
        return mMapView;
    }

    @Override
    public String getMapName() {
        return mCt.getString(R.string.bind_online);
    }

    @Override
    public void switchMapByType(int type) {
        switch (type) {
            case NewMapFactory.MAP_TYPE_NORMAL_2D:
                setMapTypeAndOverlook(BaiduMap.MAP_TYPE_NORMAL, 0);
                break;
            case NewMapFactory.MAP_TYPE_NORMAL_3D:
                setMapTypeAndOverlook(BaiduMap.MAP_TYPE_NORMAL, -30);
                break;
            case NewMapFactory.MAP_TYPE_SATELLITE:
                setMapTypeAndOverlook(BaiduMap.MAP_TYPE_SATELLITE, 0);
                break;
            case NewMapFactory.MAP_TYPE_NONE:
                setMapTypeAndOverlook(BaiduMap.MAP_TYPE_NONE, 0);
                break;
        }
    }

    @Override
    public void requestLocation(MapCallBack<String> callBack) {
        if (mLocClient != null ){
            LogUtil.d(TAG,"請求地址");
            this.locationCallBack=callBack;
            mLocClient.start();
            mLocClient.requestLocation();
        }
    }

    private void initLocation() {
        // 定位初始化
        mLocClient = new LocationClient(mCt.getApplicationContext());
        mLocClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null)
                    return;
                if (location.getLatitude() == 0
                        && location.getLongitude() == 0) {
                    return;
                }
                //设置地理位置信息；
                locationInfo=location.getAddrStr();
                LogUtil.d(TAG,"当前地址为"+locationInfo);
                if (locationCallBack!=null){
                    locationCallBack.result((String)locationInfo);
                }
                // 此处设置开发者获取到的方向信息，顺时针0-360
                MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100)
                        .latitude(location.getLatitude()).longitude(location.getLongitude()).build();
                mMap.setMyLocationData(locData);
                MyLatLng latlng = new MyLatLng(location.getLatitude(), location.getLongitude());
                NewMapFactory.getInstance().setNowLatLng(latlng);
                if (!isFocus){
                    float zoomLevelNow = NewMapFactory.getInstance().getZoomLevelNow();
                    mMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(latlng.latitude, latlng.longitude), zoomLevelNow));
                    isFocus=true;
                }
//                mLocClient.stop();
            }
        });
        LocationClientOption locationOption = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("gcj02");
//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
//可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
//可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
//可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
//可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
//可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
//开始定位
        mLocClient.setLocOption(locationOption);
//        locationClient.start();

//        requestLocation(null);
    }

    @Override
    public void setLocation(double latitude, double longitude) {
        boolean isFollow =mPreferences.getInt(NewMapActivity.AUTO_FOLLOW_MODE, 0) == 1;
        if (isFollow) {
            LatLng ll = BaiduMapUtil.convert(latitude, longitude);
            mMap.setMapStatus((MapStatusUpdateFactory.newLatLng(ll)));
        }
    }

    @Override
    public void addOverlayManager(BaseOverlayManager overlay) {
        if (overlay != null) {
            if (!mOverlayManagers.contains(overlay)) {
                overlay.bindSdk(this);
                mOverlayManagers.add(overlay);
            }
        }
    }

    @Override
    public void removeOverlayManager(BaseOverlayManager overlay) {
        if (overlay != null) {
            if (mOverlayManagers.contains(overlay)) {
                mOverlayManagers.remove(overlay);
            }
        }
    }

    @Override
    public BaseOverlayManager getOverlayManager(OverlayType overlayType) {
        for (BaseOverlayManager manager : mOverlayManagers) {
            if (manager.getOverlayType() == overlayType) {
                return manager;
            }
        }
        return null;
    }


    @Override
    public boolean removeOverlay(OverlayType overlayType) {
        for (BaseOverlayManager manager : mOverlayManagers) {
            if (manager.getOverlayType() == overlayType) {
                manager.clearOverlay();
            }
        }
        return false;
    }

    @Override
    public boolean clearOverlay() {
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.clearOverlay();
        }
        mMap.clear();
        return true;
    }

    @Override
    public void getSnapShot(final MapCallBack callBack) {
        mMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                if (callBack != null) {
                    callBack.result(bitmap);
                }
            }
        });
    }

    @Override
    public double measureDistance(boolean isMeaseureMode) {
        getOverlayManager(OverlayType.RangingLink).setEnable(isMeaseureMode);
        return 0;
    }

    @Override
    public void showHeatMap(boolean isShow) {
        getOverlayManager(OverlayType.HeatMap).setEnable(isShow);
        getOverlayManager(OverlayType.BaseStation).setEnable(!isShow);
    }

    @Override
    public void setZoomLevel(float level) {
        this.mMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(level));
    }

    @Override
    public float getZoomLevel() {
        MapStatus mapStatus = mMap.getMapStatus();
        float zoom = mapStatus.zoom;
        return zoom;
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.onResume();
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.onDestory();
        }
        mMapView.onDestroy();
        mMapView = null;
        mMap = null;
    }
    /**
     * 释放资源，在oonDestory或者切换地图的时候调用
     */
    @Override
    public void release(){

    }
    @Override
    public void loadOfflineMap() {
        mkOfflineMap=new MKOfflineMap();
        mkOfflineMap.init(this);
    }

    @Override
    public String getLocationInfo() {
        return locationInfo;
    }
    @Override
    public void focuLastLatlng() {
        mMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(mLocClient.getLastKnownLocation().getLatitude()
                , mLocClient.getLastKnownLocation().getLongitude())));
    }

    @Override
    public List<BaseStation> getStationInBounds(Point lt, Point rb) {
        List<BaseStation> list = new ArrayList<>();
        Projection pro = mMap.getProjection();
        if (pro != null) {
            LatLng ltLL = pro.fromScreenLocation(lt);
            LatLng rbLL = pro.fromScreenLocation(rb);
            LatLngBounds bounds = new LatLngBounds.Builder().include(ltLL).include(rbLL).build();
            List<Marker> markers = mMap.getMarkersInBounds(bounds);
            if (markers != null) {
                for (Marker marker : markers) {
                    Bundle extraInfo = marker.getExtraInfo();
                    BaseStation bs = (BaseStation) extraInfo.getSerializable(BdStationOverlayManager.EXTRA_STATION_INFO);
                    list.add(bs);
                }
            }
        }
        return list;
    }

    @Override
    public boolean doSthOnBackPressed() {
        return getOverlayManager(OverlayType.BaseStation).doSthOnBackPressed();
    }

    @Override
    public boolean calculateDriveRoute(List<MyLatLng> startList, List<MyLatLng> endList, List<MyLatLng> mWayPointList,
                                       boolean congestion, boolean avoidhightspeed, boolean cost, boolean hightspeed,
                                       boolean multipleroute,boolean isGaodeLatlon) {
        return false;
    }

    @Override
    public void onMapLoaded() {
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.onMapLoaded();
        }
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.onMapStatusChangeStart(mapStatus);
        }
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int reason) {
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.onMapStatusChangeStart(mapStatus, reason);
        }
    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {
        NewMapFactory.getInstance().setZoomLevelNow(mapStatus.zoom);
        if (mMapCallBack != null) {
            mMapCallBack.result((int) mapStatus.zoom + "");
        }
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.onMapStatusChange(mapStatus);
        }
    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        mSpf.edit().putInt(EXTRA_BD_MAP_ZOOM_LEVEL, (int) mapStatus.zoom)
                .putString(EXTRA_BD_MAP_LAT, mapStatus.target.latitude + "")
                .putString(EXTRA_BD_MAP_LNG, mapStatus.target.longitude + "")
                .apply();
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.onMapStatusChangeFinish(mapStatus);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.onMarkerClick(marker);
        }
        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.onMapLongClick(latLng);
        }

    }

    @Override
    public void onMapClick(LatLng latLng) {
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.onMapClick(latLng);
        }
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.onMapClick(mapPoi.getPosition());
        }
        return false;
    }

    private void setMapTypeAndOverlook(int mapType, int overlook) {
        mMap.setMapType(mapType);
        MapStatus mapStatus = mMap.getMapStatus();
        MapStatus newStatus = new MapStatus.Builder(mapStatus).overlook(overlook).build();
        mMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(newStatus));
    }

    private int getMapType() {
        int mapType = this.mMap.getMapType();
        int type = NewMapFactory.MAP_TYPE_NORMAL_2D;
        switch (mapType) {
            case BaiduMap.MAP_TYPE_NONE:
                type = NewMapFactory.MAP_TYPE_NONE;
                break;
            case BaiduMap.MAP_TYPE_NORMAL:
                float overlook = mMap.getMapStatus().overlook;
                if (overlook < 0) {
                    type = NewMapFactory.MAP_TYPE_NORMAL_3D;
                } else {
                    type = NewMapFactory.MAP_TYPE_NORMAL_2D;
                }
                break;
            case BaiduMap.MAP_TYPE_SATELLITE:
                type = NewMapFactory.MAP_TYPE_SATELLITE;
                break;
        }
        return type;
    }

    @Override
    public void onGetOfflineMapState(int i, int i1) {

    }
}
