package com.walktour.gui.newmap2.overlay.gaode;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.animation.CycleInterpolator;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.DensityUtil;
import com.walktour.gui.R;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap.hs.HsStationOperatePopWindow;
import com.walktour.gui.newmap.metro.MetroStationOperatePopWindow;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap2.bean.MarkClickBean;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.ui.GpsLocasView;
import com.walktour.gui.newmap2.util.GaodeMapUtil;
import com.walktour.service.metro.HsFactory;
import com.walktour.service.metro.MetroFactory;
import com.walktour.service.metro.model.MetroCity;
import com.walktour.service.metro.model.MetroRoute;
import com.walktour.service.metro.model.MetroStation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/6/12
 * @describe 地铁的覆盖物
 */
public class GdHsRouteOverlayManager extends BaseOverlayManager {
    private static final String TAG = "GdHsRouteOverlayManager";

    /**
     * 地铁图层的工厂类
     */
    private HsFactory mFactory;

    /**
     * 基站详情弹出框
     */
    private HsStationOperatePopWindow mWindow = null;
    /**
     * 当前站点的显示对象
     */
    private Marker mMarker;
    /**
     * 当前选择的线路的所有站点
     */
    private List<MetroStation> mStations = new ArrayList<>();
    private List<Marker> markers = new ArrayList<>();
    /**
     * 像素密度
     */
    protected float mDensity;
    private final MarkerOptions markerOptions;
    private final PolylineOptions mPolylineOptions;
    private TextOptions mTextOptions;
    private final PolylineOptions mCitylineOptions;
    private ScaleAnimation mAnimation;
    private boolean animatiionIsShow=false;//正在展示动画

    public GdHsRouteOverlayManager(Context context) {
        super(context);
        mFactory = HsFactory.getInstance(mContext);
        GpsLocasView customView=new GpsLocasView(mContext);
        customView.setColor(0xFF00A3E6);
        customView.setStrokeColor(0xFF00A3E6);
        customView.setRadius(DensityUtil.dip2px(mContext, 8));
        markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromView(customView));
        mPolylineOptions = new PolylineOptions().color(mContext.getResources().getColor(R.color.light_blue));
        mCitylineOptions = new PolylineOptions().color(mContext.getResources().getColor(R.color.app_grey_color));
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metric);
        this.mDensity = metric.density;

    }

    private void showCurrentMetro(MyLatLng  mlatLng) {

        if (mFactory.getCurrentStation()==null)
            return;
        MyLatLng currentStation = mFactory.getCurrentStation().getLatLng();
        LatLng latLng = GaodeMapUtil.convertToGaode(mContext, GaodeMapUtil.convertToLatLng(currentStation), CoordinateConverter.CoordType.GPS);
        if (mMarker==null){
            GpsLocasView customView=new GpsLocasView(mContext);
            customView.setColor(Color.BLUE);
            customView.setStrokeColor(Color.BLUE);
            customView.setRadius(DensityUtil.dip2px(mContext, 10));
            MarkerOptions options = new MarkerOptions();
            options.position(latLng);
            options.anchor(0.5f, 0.5f);
            options.period(10);
            options.icon(BitmapDescriptorFactory.fromView(customView));
            mMarker = getMapControllor().addMarker(options);
            mAnimation = new ScaleAnimation(0, 1, 0, 1);
            mAnimation.setDuration(500000l);
            mAnimation.setInterpolator(new CycleInterpolator(300));
            mAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart() {
                    animatiionIsShow=true;
                }

                @Override
                public void onAnimationEnd() {
                    animatiionIsShow=false;
                }
            });
        }else {
            mMarker.setPosition(latLng);
        }
        if (!animatiionIsShow){
            mMarker.setAnimation(mAnimation);
            mMarker.startAnimation();
        }
    }



    /**
     * 获取选择的路线的覆盖物
     */
    private void getCurrentRoutesOverlayItems() {
        MetroRoute currentRoute = this.mFactory.getCurrentRoute(DatasetManager.isPlayback);
        this.mStations.clear();
        if (currentRoute != null) {
            this.mStations.addAll(currentRoute.getStations());
        }
    }

    /**
     * 绘制当前测试路线
     */
    private void drawCurrentRoute() {
        //绘制点
        for (int i = 0; i < this.mStations.size(); i++) {
            MetroStation station = this.mStations.get(i);
            if (station.getState() != MetroStation.STATE_CANT_SELECT){
            MarkClickBean bean = new MarkClickBean();
            bean.setObj(station);
            bean.setOverlayType(OverlayType.HsRoute);
            //將GPS坐标点转成高德地图
            LatLng latLng = GaodeMapUtil.convertToGaode(mContext, GaodeMapUtil.convertToLatLng(station.getLatLng()), CoordinateConverter.CoordType.GPS);
            // 绘制站点
            Marker marker = getMapControllor().addMarker(markerOptions.position(latLng));
            marker.setObject(bean);
            markers.add(marker);
            // 绘制站点名称
            mTextOptions = new TextOptions().text("cal distance ...").
                    fontSize(DensityUtil.dip2px(mContext, 10));
            Text stationText = getMapControllor().addText(mTextOptions.position(latLng));
            stationText.setText(station.getName());
        }
        }
    }


    @Override
    public boolean onMarkerClick(Object... obj) {
        Marker marker = (Marker) obj[0];
        MarkClickBean bean = (MarkClickBean) marker.getObject();
        if (bean != null) {
            if (bean.getOverlayType() == OverlayType.HsRoute) {
                this.showStationOperate();
            }
        }
        return false;
    }


    /**
     * 显示选中的操作对话框
     */
    protected void showStationOperate() {
        if (this.mWindow != null && this.mWindow.isShow())
            return;
        MetroStation currentStation = this.mFactory.getCurrentStation();
        if (this.mWindow==null){
            this.mWindow = new HsStationOperatePopWindow(mMapSdk.getMapView(), (Activity) mContext, currentStation);
        }
        this.mWindow.setStation(currentStation);
        this.mWindow.showPopWindow();
    }

    public void closeShowPopWindow() {
        if (this.mWindow != null) {
            this.mWindow.closePopWindow();
            this.mWindow = null;
        }
    }


    @Override
    public OverlayType getOverlayType() {
        return OverlayType.HsRoute;
    }

    @Override
    public void onDestory() {
        this.clearOverlay();
        this.closeShowPopWindow();
        super.onDestory();
    }

    @Override
    public void onMapLoaded() {
        mFactory.init(mContext);
        this.getCurrentRoutesOverlayItems();
        if (!this.mFactory.isRuning() && !DatasetManager.isPlayback)
            return;
        this.drawCurrentRoute();
    }

    private AMap getMapControllor() {
        return (AMap) mMapSdk.getMapControllor();
    }

    @Override
    public boolean addOverlay(Object... obj) {
        if (obj==null||obj.length==0){
            return false;
        }
        MyLatLng myLatLng= (MyLatLng) obj[0];
        showCurrentMetro(myLatLng);
        return false;
    }

    @Override
    public boolean clearOverlay() {
        if (markers != null) {
            for (Marker marker : markers) {
                marker.remove();
            }
        }
        return false;
    }
}
