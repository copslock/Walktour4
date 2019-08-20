package com.walktour.gui.newmap2.sdk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DistanceItem;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.EventManager;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.NetUtil;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhone;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.framework.database.BaseStationDBHelper;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap2.NewMapActivity;
import com.walktour.gui.newmap2.bean.MarkClickBean;
import com.walktour.gui.newmap2.bean.MarkLocasBean;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.NullOverlayManager;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.util.ErrorInfo;
import com.walktour.gui.newmap2.util.GaodeMapUtil;
import com.walktour.model.AlarmModel;
import com.walktour.model.LocusParamInfo;
import com.walktour.model.MapEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/6/18
 * @describe 提供了高德地图的API二次封装
 */
public class GaoDeNaviMapSdk implements IMapSdk,
        AMap.OnCameraChangeListener, AMap.OnMapClickListener,
        AMap.OnMapLongClickListener, AMap.OnMapLoadedListener,
        AMap.OnMarkerClickListener, LocationSource, AMapLocationListener
        , DistanceSearch.OnDistanceSearchListener, AMap.InfoWindowAdapter, AMapNaviListener, AMapNaviViewListener {
    private String ZOOM_SIZE_SHARE = "zoom_size_share";
    private static final String TAG = "GaoDeNaviMapSdk";
    /**
     * 地图View控件
     */
    AMapNaviView mMapView = null;
    /**
     * 操作地图
     */
    AMap aMap;
    /**
     * 操作导航的
     */
     AMapNavi mAMapNavi;
    /**
     * Activity的上下文
     */
    Context mContext;
    /**
     * 定位服务
     */
    AMapLocationClient mLocationClient;
    /**
     * 状态改变的监听
     */
    OnLocationChangedListener mListener;
    /**
     * 管理
     */
    private List<BaseOverlayManager> mOverlayManagers = new ArrayList<>();
    private SharedPreferences mPreferences;//存储设置
    private MapCallBack mMapCallBack;
    private String locationInfo = "";
    private MapCallBack<String> locationCallBack;
    private boolean isFocus = false;

    public static boolean isNavi=false;
    public GaoDeNaviMapSdk(Context context, Bundle savedInstanceState) {
        this.mContext = context;
        if (mContext instanceof MapCallBack) {
            mMapCallBack = (MapCallBack) mContext;
        }
        initMap(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    /**
     * 初始化地图
     */
    private void initMap(Bundle savedInstanceState) {
        if (mMapView == null) {
            mMapView = new AMapNaviView(mContext);
            mMapView.setAMapNaviViewListener(this);
            //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
            mMapView.onCreate(savedInstanceState);
            //初始化地图控制器对象c
            if (aMap == null) {
                aMap = mMapView.getMap();
                setUpMap();
                setMapListener();
            }
//            requestLocation(null);
            float zoomSize = SharePreferencesUtil.getInstance(mContext).getFloat(ZOOM_SIZE_SHARE, 12);
            setZoomLevel(zoomSize);
        }
        if (mAMapNavi==null){
            mAMapNavi = AMapNavi.getInstance(mContext);
            mAMapNavi.addAMapNaviListener(this);
            mAMapNavi.setUseInnerVoice(true);
            //设置模拟导航的行车速度
            mAMapNavi.setEmulatorNaviSpeed(60);
        }
    }

    @Override
    public String getMapName() {
        return mContext.getString(R.string.gaode_online);
    }

    /**
     * 设置监听
     */
    private void setMapListener() {
        aMap.setOnMapLoadedListener(this);
        aMap.setOnCameraChangeListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnMapClickListener(this);
        aMap.setOnMapLongClickListener(this);
        aMap.setOnMapLoadedListener(this);
        aMap.setInfoWindowAdapter(this);
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转3种
        //跟随：LOCATION_TYPE_MAP_FOLLOW
        //旋转：LOCATION_TYPE_MAP_ROTATE
        //定位：LOCATION_TYPE_LOCATE
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        aMap.getUiSettings().setGestureScaleByMapCenter(true);//开启以中心点进行手势操作的方法：
        aMap.getUiSettings().setMyLocationButtonEnabled(false);//去掉定位按钮

        //设置定位外面的圆形范围
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        myLocationStyle.showMyLocation(false);
        aMap.setMyLocationStyle(myLocationStyle);
        //去掉logo
        UiSettings uiSettings =  aMap.getUiSettings();
        uiSettings.setLogoBottomMargin(-50);//隐藏logo
        //设置布局完全不可见
        AMapNaviViewOptions viewOptions = mMapView.getViewOptions();
//主动隐藏蚯蚓线
    }

    /**
     * 在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
     */
    public void onDestroy() {
        release();
        isNavi=false;

    }

    /**
     * 释放资源，在oonDestory或者切换地图的时候调用
     */
    @Override
    public void release(){
        if (mMapView != null) {
            mMapView.onDestroy();
            mAMapNavi.stopNavi();
            mAMapNavi.destroy();
        }
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
            mLocationClient.disableBackgroundLocation(true);//不需要的时候就关闭后台定位
        }
    }
    @Override
    public void loadOfflineMap() {

    }

    @Override
    public String getLocationInfo() {
        return locationInfo;
    }

    @Override
    public List<BaseStation> getStationInBounds(Point lt, Point rb) {
/**
 *
 */
        Projection projection = aMap.getProjection();
        LatLng ltLL = projection.fromScreenLocation(lt);
        LatLng rbLL = projection.fromScreenLocation(rb);
        //read from db
        List<BaseStation>  baseStationList = BaseStationDBHelper.getInstance(mContext).queryBaseStation(
                ltLL.longitude, ltLL.latitude, rbLL.longitude, rbLL.latitude,
                NetUtil.getNetTypes(mContext), 0, BaseStation.MAPTYPE_OUTDOOR, -1);

        return baseStationList;
    }

    @Override
    public boolean doSthOnBackPressed() {
        return false;
    }
    /**
     * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
     *  @startList 开始的坐标点，可以多个
     *   @endList 结束的坐标点，可以多个
     * @mWayPointList 途经点的坐标点，可以多个
     * @congestion 躲避拥堵
     * @avoidhightspeed 不走高速
     * @cost 避免收费
     * @hightspeed 高速优先
     * @multipleroute 多路径
     *
     *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
     *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
     */
    @Override
    public boolean calculateDriveRoute(List<MyLatLng> startList, List<MyLatLng> endList, List<MyLatLng> mWayPointList, boolean congestion, boolean avoidhightspeed, boolean cost, boolean hightspeed, boolean multipleroute,boolean isGaodeLatlon) {
        int strategy = 0;
        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<NaviLatLng> sList=new ArrayList<>();
        List<NaviLatLng> eList=new ArrayList<>();
        List<NaviLatLng> wayList=new ArrayList<>();
        if (startList!=null){
            for (int i=0;i<startList.size();i++){
                MyLatLng latlng=startList.get(i);
                if (isGaodeLatlon){
                    sList.add(new NaviLatLng(latlng.latitude,latlng.longitude));
                }else {
                    sList.add(GaodeMapUtil.convertToGaodeNavi(mContext,latlng, CoordinateConverter.CoordType.GPS));
                }
            }
        }
        if (endList!=null){
            for (int i=0;i<endList.size();i++){
                MyLatLng latlng=endList.get(i);
                if (isGaodeLatlon){
                    eList.add(new NaviLatLng(latlng.latitude,latlng.longitude));
                }else {
                   eList.add(GaodeMapUtil.convertToGaodeNavi(mContext,latlng, CoordinateConverter.CoordType.GPS));
                }
            }
        }
        if (mWayPointList!=null){
            for (int i=0;i<mWayPointList.size();i++){
                MyLatLng latlng=mWayPointList.get(i);
                if (isGaodeLatlon){
                    wayList.add(new NaviLatLng(latlng.latitude,latlng.longitude));
                }else {
                    wayList.add(GaodeMapUtil.convertToGaodeNavi(mContext,latlng, CoordinateConverter.CoordType.GPS));
                }
            }
        }
        return mAMapNavi.calculateDriveRoute(sList, eList, wayList, strategy);
    }

    /**
     * 得到aMap对象
     */
    @Override
    public Object getMapControllor() {
        return aMap;
    }

    @Override
    public int getLayoutId() {
        return R.layout.map_gaode_navi_main_activity;
    }

    @Override
    public View getMapView() {
        return mMapView;
    }

    @Override
    public void switchMapByType(int type) {
        switch (type) {
            case NewMapFactory.MAP_TYPE_NORMAL_2D:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);//矢量地图模式
                break;
            case NewMapFactory.MAP_TYPE_NORMAL_3D:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);//矢量地图模式
                break;
            case NewMapFactory.MAP_TYPE_SATELLITE:
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);//卫星地图模式
                break;
            case NewMapFactory.MAP_TYPE_NONE:
                aMap.setMapType(AMap.MAP_TYPE_NAVI);//导航地图模式
                break;
        }


    }

    @Override
    public void requestLocation(MapCallBack<String> callBack) {

        if (mLocationClient != null) {
            mLocationClient.startLocation();
            this.locationCallBack = callBack;
//            LatLng currentLatLng = new LatLng(mLocationClient.getLastKnownLocation().getLatitude(), mLocationClient.getLastKnownLocation().getLongitude());
//            aMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        }
    }

    @Override
    public void setLocation(double latitude, double longitude) {
        boolean isFollow = mPreferences.getInt(NewMapActivity.AUTO_FOLLOW_MODE, 0) == 1;
        if (isFollow) {
            MyLatLng myLatLng = new MyLatLng(latitude, longitude);
            LatLng latLng = GaodeMapUtil.convertToGaode(mContext, GaodeMapUtil.convertToLatLng(myLatLng), CoordinateConverter.CoordType.GPS);
            aMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
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
        return new NullOverlayManager(mContext);
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
        aMap.clear();
        return false;
    }

    @Override
    public void getSnapShot(final MapCallBack callBack) {
        aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(Bitmap bitmap) {
                if (callBack != null) {
                    callBack.result(bitmap);
                }
            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int i) {
                if (null == bitmap) {
                    return;
                }
                if (callBack != null) {
                    callBack.result(bitmap);
                }
                if (i != 0) {
                    LogUtil.d(TAG, "地图渲染完成，截屏无网格");
                } else {
                    LogUtil.d(TAG, "地图未渲染完成，截屏有网格");
                }
            }
        });

    }

    @Override
    public double measureDistance(boolean isMeasure) {
        getOverlayManager(OverlayType.RangingLink).setEnable(isMeasure);
        return 0;
    }

    @Override
    public void showHeatMap(boolean isShow) {
        getOverlayManager(OverlayType.HeatMap).setEnable(isShow);
        getOverlayManager(OverlayType.BaseStation).setEnable(!isShow);
    }

    @Override
    public void setZoomLevel(float level) {
        if (aMap != null) {
            aMap.moveCamera(CameraUpdateFactory.zoomTo(level));
        }
    }

    @Override
    public float getZoomLevel() {
        CameraPosition camerPostion = aMap.getCameraPosition();
        float mapZoom = camerPostion.zoom;
        return mapZoom;
    }

    /**
     * 在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
     */
    public void onResume() {
        mMapView.onResume();
    }

    /**
     * 在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
     */
    public void onPause() {
        mMapView.onPause();
    }

    /**
     * 在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
     */
    public void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 当状态被改变时
     *
     * @param cameraPosition 状态
     */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        NewMapFactory.getInstance().setZoomLevelNow(cameraPosition.zoom);
        if (mMapCallBack != null) {
            mMapCallBack.result((int) cameraPosition.zoom + "");
        }
    }

    /**
     * 状态改变完成（松开手指）
     *
     * @param cameraPosition 状态
     */
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        Log.d(TAG, "onCameraChangeFinish:" + cameraPosition);
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.onMapStatusChangeFinish(cameraPosition);
        }
        SharePreferencesUtil.getInstance(mContext).putFloat(ZOOM_SIZE_SHARE, getZoomLevel());
    }


    /**
     * 地图被点击
     *
     * @param latLng 经纬度
     */
    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick:" + latLng);
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.onMapClick(latLng);
        }
    }


    /**
     * 地图加载完成
     */
    @Override
    public void onMapLoaded() {
        Log.d(TAG, "onMapLoaded:");
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.onMapLoaded();
        }
    }

    /**
     * 地图长按
     *
     * @param latLng 经纬度
     */
    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG, "onMapLongClick:latLng:" + latLng);
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.onMapLongClick(latLng);
        }
    }

    /**
     * 标签被点击的时候
     *
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClick:marker:" + marker);
        for (BaseOverlayManager manager : mOverlayManagers) {
            manager.onMarkerClick(marker);
        }
        return false;
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        Log.d(TAG, "activate()");
        Log.d(TAG, "onLocationChangedListener():" + onLocationChangedListener);

        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            //初始化定位
            mLocationClient = new AMapLocationClient(mContext);
            //初始化定位参数
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setOnceLocation(true);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            AMapLocationClientOption.GeoLanguage language= MyPhoneState.isZhForLanguage(mContext)? AMapLocationClientOption.GeoLanguage.ZH: AMapLocationClientOption.GeoLanguage.EN;
            mLocationOption.setGeoLanguage(language);
            //设置是否允许模拟位置,默认为true，允许模拟位置
            mLocationOption.setMockEnable(true);
            mLocationOption.setInterval(2000);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);

            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();//启动定位
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    private ProgressDialog progDialog = null;// 添加海量点时

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new ProgressDialog(mContext);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage(message);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 定位变化
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.d(TAG, "aMapLocation:" + aMapLocation);
        if (mListener != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {

                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息
                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                aMapLocation.getStreet();//街道信息
                aMapLocation.getStreetNum();//街道门牌号信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码
                aMapLocation.getAoiName();//获取当前定位点的AOI信息
                aMapLocation.getBuildingId();//获取当前室内定位的建筑物Id
                aMapLocation.getFloor();//获取当前室内定位的楼层
                aMapLocation.getGpsAccuracyStatus();//获取GPS的当前状态
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                locationInfo = "" + aMapLocation.getAddress();
                if (locationCallBack != null) {
                    locationCallBack.result((String) locationInfo);
                }
                MyLatLng latlng = new MyLatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                NewMapFactory.getInstance().setNowLatLng(latlng);
                LatLng currentLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                if (!isFocus) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                    isFocus = true;
                }
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    public void focuLastLatlng() {
        LatLng currentLatLng = new LatLng(mLocationClient.getLastKnownLocation().getLatitude(),
                mLocationClient.getLastKnownLocation().getLongitude());
        aMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
    }

    /**
     * 测量距离的回调
     *
     * @param distanceResult 测量结果
     * @param errorCode      错误码  1000是成功，其他是失败
     */
    @Override
    public void onDistanceSearched(DistanceResult distanceResult, int errorCode) {
        dissmissProgressDialog();
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            try {
                Log.i("amap", "onDistanceSearched " + distanceResult);
                List<DistanceItem> distanceItems = distanceResult.getDistanceResults();
                DistanceSearch.DistanceQuery distanceQuery = distanceResult.getDistanceQuery();
                List<LatLonPoint> origins = distanceQuery.getOrigins();
                LatLonPoint destLatlon = distanceQuery.getDestination();
                if (distanceItems == null) {
                    return;
                }
                int index = 1;
                for (DistanceItem item : distanceItems) {
                    StringBuffer stringBuffer = new StringBuffer();
                    //item.getOriginId() - 1 是因为 下标从1开始
                    stringBuffer.append("\n\torid: ").append(item.getOriginId()).append(" ").append(origins.get(item.getOriginId() - 1)).append("\n");
                    stringBuffer.append("\tdeid: ").append(item.getDestId()).append(" ").append(destLatlon).append("\n");
                    stringBuffer.append("\tdis: ").append(item.getDistance()).append(" , ");
                    stringBuffer.append("\tdur: ").append(item.getDuration());

                    if (item.getErrorInfo() != null) {
                        stringBuffer.append(" , ").append("err: ").append(item.getErrorCode()).append(" ").append(item.getErrorInfo());
                    }

                    stringBuffer.append("\n");
                    Log.i("amap", "onDistanceSearched " + index + " : " + stringBuffer.toString());
//                    mapDistance.setText(item.getDistance() + " 米 " + item.getDuration() + " 秒");
                    index++;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private View mPopLayout;
    private TextView mtitle;
    private TextView mDescr;
    private TextView mCoord;

    @Override
    public View getInfoWindow(Marker marker) {
        MarkClickBean bean = (MarkClickBean) marker.getObject();
        switch (bean.getOverlayType()) {
            case LocasPoint:
                if (bean.isCurrentPoint()) {//如果是当前定位，不需要信息窗
                    return null;
                }
                mPopLayout = LayoutInflater.from(mContext).inflate(R.layout.poi_descr, null);
                mtitle = (TextView) mPopLayout.findViewById(R.id.poi_title);
                mDescr = (TextView) mPopLayout.findViewById(R.id.descr);
                mCoord = (TextView) mPopLayout.findViewById(R.id.coord);
                if (!bean.isCurrentPoint() && bean.getObj() instanceof MarkLocasBean) {
                    MarkLocasBean markBean = (MarkLocasBean) bean.getObj();
                    MapEvent event = markBean.getMapEvent();
                    if (event != null) {
                        mDescr.setText(event.getMapPopInfo());
                        if (DatasetManager.isPlayback) {
                            DatasetManager.getInstance(mContext).getPlaybackManager().setSkipIndex(event.getBeginPointIndex());
                        }
                    }
                    LocusParamInfo locusParamInfo = markBean.getLocusParamInfo();
                    mtitle.setText(R.string.position);
                    if (locusParamInfo == null || locusParamInfo.paramName == null || locusParamInfo.value == -9999) {
                        mCoord.setVisibility(View.GONE);
                    } else {
                        mCoord.setVisibility(View.VISIBLE);
                        mCoord.setText(locusParamInfo.paramName + ":" + locusParamInfo.value);
                    }
                }
                return mPopLayout;
            case Alarm:
                AlarmModel alarm = (AlarmModel) bean.getObj();
                mPopLayout = LayoutInflater.from(mContext).inflate(R.layout.even_descr, null);
                mtitle = (TextView) mPopLayout.findViewById(R.id.event_title);
                mDescr = (TextView) mPopLayout.findViewById(R.id.even_content);
                mtitle.setText(alarm.getDescription(mContext));
                mDescr.setText(alarm.getMapPopInfo());
                return mPopLayout;
            case CellLink:
                return null;
            case MetroRoute:
                return null;
            case BaseStation:
                return null;
            case RangingLink:
                return null;
        }
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return getInfoWindow(marker);
    }
    @Override
    public void onInitNaviFailure() {
        Toast.makeText(mContext, "init navi Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInitNaviSuccess() {
        //初始化成功
        LogUtil.e(TAG,"初始化成功");
    }

    @Override
    public void onStartNavi(int type) {
        //开始导航回调
    }

    @Override
    public void onTrafficStatusUpdate() {
        //
        LogUtil.e(TAG,"onTrafficStatusUpdate:");
    }

    @Override
    public void onLocationChange(AMapNaviLocation location) {
        //当前位置回调
    }

    @Override
    public void onGetNavigationText(int type, String text) {
        //播报类型和播报文字回调/
        LogUtil.e(TAG,"类型"+type+",文本："+text);
    }

    @Override
    public void onGetNavigationText(String s) {
        LogUtil.e(TAG,",文本："+s);
    }

    @Override
    public void onEndEmulatorNavi() {
        //结束模拟导航
        LogUtil.e(TAG,"结束模拟导航:");
    }

    @Override
    public void onArriveDestination() {
        //到达目的地
        LogUtil.e(TAG,"到达目的地:");
    }

    @Override
    public void onCalculateRouteFailure(int errorInfo) {
        //路线计算失败
        LogUtil.e(TAG, "--------------------------------------------");
        LogUtil.i(TAG, "路线计算失败：错误码=" + errorInfo + ",Error Message= " + ErrorInfo.getError(errorInfo));
        LogUtil.i(TAG, "错误码详细链接见：http://lbs.amap.com/api/android-navi-sdk/guide/tools/errorcode/");
        LogUtil.e(TAG, "--------------------------------------------");
        Toast.makeText(mContext, "errorInfo：" + errorInfo + ",Message：" + ErrorInfo.getError(errorInfo), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReCalculateRouteForYaw() {
        //偏航后重新计算路线回调
        Toast.makeText(mContext,  "你已经偏航，重新测试", Toast.LENGTH_LONG).show();
        EventManager.getInstance().addTagEvent(mContext,System.currentTimeMillis(),"你已经偏航，重新测试");
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        //拥堵后重新计算路线回调
        Toast.makeText(mContext,  "前方拥堵，请耐心等待", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onArrivedWayPoint(int wayID) {
        //到达途径点
        LogUtil.e(TAG,"到达途径点:");
    }

    @Override
    public void onGpsOpenStatus(boolean enabled) {
        //GPS开关状态回调
        Toast.makeText(mContext,  "GPS开关"+enabled, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNaviSetting() {
        //底部导航设置点击回调
    }

    @Override
    public void onNaviMapMode(int isLock) {
        //地图的模式，锁屏或锁车
    }

    @Override
    public void onNaviCancel() {
        LogUtil.d(TAG,"onNaviCancel");

    }


    @Override
    public void onNaviTurnClick() {
        //转弯view的点击回调
    }

    @Override
    public void onNextRoadClick() {
        //下一个道路View点击回调
        LogUtil.e(TAG,"下一个道路View点击回调:");
    }


    @Override
    public void onScanViewButtonClick() {
        //全览按钮点击回调
    }

    @Deprecated
    @Override
    public void onNaviInfoUpdated(AMapNaviInfo naviInfo) {
        //过时
    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] amapServiceAreaInfos) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviinfo) {
        //导航过程中的信息更新，请看NaviInfo的具体说明
    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {
        //已过时
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {
        //已过时
    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
        //显示转弯回调
    }

    @Override
    public void hideCross() {
        //隐藏转弯回调
    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] laneInfos, byte[] laneBackgroundInfo, byte[] laneRecommendedInfo) {
        //显示车道信息

    }

    @Override
    public void hideLaneInfo() {
        //隐藏车道信息
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        //多路径算路成功回调

//        mAMapNavi.startNavi(NaviType.EMULATOR);
        mAMapNavi.startNavi(NaviType.GPS);
    }

    @Override
    public void notifyParallelRoad(int i) {
//        if (i == 0) {
//            Toast.makeText(mContext, "当前在主辅路过渡", Toast.LENGTH_SHORT).show();
//            LogUtil.d(TAG, "当前在主辅路过渡");
//            return;
//        }
//        if (i == 1) {
//            Toast.makeText(mContext, "当前在主路", Toast.LENGTH_SHORT).show();
//
//            LogUtil.d(TAG, "当前在主路");
//            return;
//        }
//        if (i == 2) {
//            Toast.makeText(mContext, "当前在辅路", Toast.LENGTH_SHORT).show();
//
//            LogUtil.d(TAG, "当前在辅路");
//        }
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
        //更新交通设施信息
        LogUtil.d(TAG, "aMapNaviTrafficFacilityInfos："+aMapNaviTrafficFacilityInfos);
    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
        //更新巡航模式的统计信息
    }


    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
        //更新巡航模式的拥堵信息
    }

    @Override
    public void onPlayRing(int i) {

    }


    @Override
    public void onLockMap(boolean isLock) {
        //锁地图状态发生变化时回调
    }

    @Override
    public void onNaviViewLoaded() {
        LogUtil.d(TAG, "导航页面加载成功");
        LogUtil.d(TAG, "请不要使用AMapNaviView.getMap().setOnMapLoadedListener();会overwrite导航SDK内部画线逻辑");
    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public void onNaviViewShowMode(int i) {

    }

    @Override
    public boolean onNaviBackClick() {
        LogUtil.d(TAG,"点击了地图的X按钮");
 tipNaviMapFinish();
        return true;
    }


    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        isNavi=true;
    }
    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {
        isNavi=false;
    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }
    void tipNaviMapFinish(){
        if(mContext instanceof Activity)

        new BasicDialog.Builder(((Activity)mContext).getParent()).setTitle(R.string.str_tip).setMessage(mContext.getString(R.string.exit_navi))
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity)mContext).finish();
                    }
                }).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //不作任何处理
            }
        }).show();
    }
}
