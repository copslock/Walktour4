package com.walktour.gui.newmap2.overlay.gaode;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.walktour.Utils.NetUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.SafeHandler;
import com.walktour.framework.database.BaseStationDBHelper;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap.basestation.ImportBaseStationFilterManager;
import com.walktour.gui.newmap2.bean.MarkClickBean;
import com.walktour.gui.newmap2.bean.StationEvent;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.MapCache;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.ui.BaseStationDetailDialog;
import com.walktour.gui.newmap2.ui.MapProgressDialog;
import com.walktour.gui.newmap2.ui.SectorView;
import com.walktour.gui.newmap2.util.GaodeMapUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/6/7
 * @describe 高德基站管理
 */
public class GdStationOverlayManager extends BaseOverlayManager implements Handler.Callback {

    private static final String TAG = "GdStationOverlayManager";

    public static final int LOAD_STATION = 1; //加载基站
    public static final int STOP_LOAD = 2; // 退出地图，停止加载基站
    public static final int CLEAR_STATION = 3;//清除地图基站
    public static final int SHOW_PROGRESS_DIALOG = 4; //显示loading
    public static final int HIDE_PROGRESS_DIALOG = 5; //隐藏loading

    private boolean mIsDetroy;
    private boolean mIsRunnableExcete;//是否runnable执行标识

    private static final int MAX_COUNT = 500;//add max count
    private List<Marker> mScreenInsideMarker = new ArrayList<>();//屏幕上加载的点
    private ArrayList<MarkerOptions> needAddToScreenInsideMark = new ArrayList<>();//需要添加到地图的Maker
    private List<BaseStation> baseStationList;//从数据库查询的基站
    private BaseStationDetailDialog dialog;
    private Marker mSelectMk;
    private long mSelectStationId;
    private MapProgressDialog mPgDialog;

    private SafeHandler mUiHd = new SafeHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == SHOW_PROGRESS_DIALOG) {
                if (!mPgDialog.isShowing()) {
                    mPgDialog.show();
                }
            } else if (msg.what == HIDE_PROGRESS_DIALOG) {
                if (mPgDialog.isShowing()) {
                    mPgDialog.dismiss();
                }
            }
            return true;
        }
    });

    private SafeHandler mHd;

    public GdStationOverlayManager(Context context) {
        super(context);
        mPgDialog = new MapProgressDialog(((Activity) mContext).getParent());
        mPgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        RefreshEventManager.addRefreshListener(mRefreshListener);

        HandlerThread thread = new HandlerThread("station_load_thread");
        thread.start();
        mHd = new SafeHandler(thread.getLooper(), this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void setEnable(boolean isEnable) {
        if (!isEnable) {
            mHd.sendEmptyMessage(CLEAR_STATION);
        } else {
            mHd.sendEmptyMessageDelayed(LOAD_STATION, 300);
        }
        super.setEnable(isEnable);
    }


    @Override
    public boolean onMarkerClick(Object... obj) {
        Marker marker = (Marker) obj[0];
        MarkClickBean bean = (MarkClickBean) marker.getObject();
        if (bean != null) {
            if (bean.getOverlayType() == OverlayType.BaseStation) {
                marker.setInfoWindowEnable(false);
                BaseStation baseStation = (BaseStation) bean.getObj();
                showBaseStationDetail(marker, baseStation);
            }
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void filterByStationRectView(StationEvent event) {
        if (event.type == StationEvent.RECT_FILTER) {
            Point[] points = event.points;
            Projection projection = getAMap().getProjection();
            if (projection != null) {
                LatLng ltLl = projection.fromScreenLocation(points[0]);
                LatLng rbLl = projection.fromScreenLocation(points[1]);
                MapCache.saveRectLatLng(new LatLng[]{ltLl, rbLl});
                mHd.sendEmptyMessage(CLEAR_STATION);
                mHd.sendEmptyMessage(LOAD_STATION);
            }
        } else if (event.type == StationEvent.PARAM_FILTER) {
            mHd.sendEmptyMessage(CLEAR_STATION);
            mHd.sendEmptyMessage(LOAD_STATION);
        }
    }

    protected void showBaseStationDetail(final Marker marker, final BaseStation base) {
        if (dialog == null) {
            dialog = new BaseStationDetailDialog(((Activity) mContext).getParent());
        }
        dialog.setOnCheckListener(new BaseStationDetailDialog.OnCheckListener() {
            @Override
            public void onCheckStation(boolean isCheck) {

                if (mSelectMk != null && mSelectMk != marker) {
                    NewMapFactory.getInstance().setSelectBaseStation(null);
                    SectorView view = new SectorView(mContext);
                    view.setAngle(base.getBearings());
                    view.setSelected(false);
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
                    mSelectMk.setIcon(icon);
                    mSelectMk.setZIndex(0);
                }
                SectorView view = new SectorView(mContext);
                view.setAngle(base.getBearings());
                view.setSelected(isCheck);
                BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
                if (isCheck) {
                    NewMapFactory.getInstance().setSelectBaseStation(base);
                    marker.setIcon(icon);
                    marker.setZIndex(9);
                    mSelectMk = marker;
                    mSelectStationId = base.id;
                } else {
                    NewMapFactory.getInstance().setSelectBaseStation(null);
                    marker.setIcon(icon);
                    marker.setZIndex(0);
                    mSelectMk = null;
                    mSelectStationId = -1;
                }
            }
        });
        dialog.setBaseStation(base);
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mHd.sendEmptyMessageDelayed(LOAD_STATION, 300);
    }

    @Override
    public void onDestory() {
        super.onDestory();
        mIsDetroy = true;
        mUiHd.removeCallbacksAndMessages(null);
        mHd.removeCallbacksAndMessages(null);
        RefreshEventManager.removeRefreshListener(mRefreshListener);
        EventBus.getDefault().unregister(this);
    }

    /**
     * 查看这些基站是否有加载过，有则过滤，无则添加
     *
     * @param list
     * @return
     */
    private void filterOverlay(List<BaseStation> list) {
        needAddToScreenInsideMark.clear();
        for (BaseStation baseStation : list) {
            boolean needAdd = true;
            for (Marker marker : mScreenInsideMarker) {
                if (marker.getSnippet().contains(String.valueOf(baseStation.id))) {
                    needAdd = false;
                    break;
                }
            }
            if (needAdd) {
                // add
                LatLng ll = new LatLng(baseStation.latitude, baseStation.longitude);
                ll = GaodeMapUtil.convertToGaode(mContext, ll, CoordinateConverter.CoordType.GPS);
                SectorView view = new SectorView(mContext);
                view.setAngle(baseStation.getBearings());
                view.setSelected(baseStation.id == mSelectStationId);
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(view);
                MarkerOptions option = new MarkerOptions()
                        .position(ll)
                        .zIndex(baseStation.id == mSelectStationId ? 9 : 0)
                        .snippet("" + baseStation.id)
                        .icon(bitmap);
                needAddToScreenInsideMark.add(option);
            }
        }
    }

    private void addMarkerToMap() {
        int size = needAddToScreenInsideMark.size();
        //        LogUtil.d(TAG, "需要加载基站的数量：" + size);
        /**
         * 1.取出需要加载的基站，
         * 2.如果marker大于500，则分几组加载，否则一次性加载
         *
         */
        if (needAddToScreenInsideMark.size() < MAX_COUNT) {
            addMarkers(needAddToScreenInsideMark);
        } else {
            List<List<MarkerOptions>> markersList = new ArrayList<>();
            int count = needAddToScreenInsideMark.size() / MAX_COUNT;
            for (int i = 0; i < count; i++) {
                List<MarkerOptions> countMarkers = needAddToScreenInsideMark.subList(i * MAX_COUNT, (i + 1) * MAX_COUNT);
                markersList.add(countMarkers);
            }
            List<MarkerOptions> counMarkers = needAddToScreenInsideMark.subList(count * MAX_COUNT, needAddToScreenInsideMark.size());
            markersList.add(counMarkers);
            for (List<MarkerOptions> markerOptions : markersList) {
                ArrayList<MarkerOptions> markerOptionsArrayList = new ArrayList<>(markerOptions);
                addMarkers(markerOptionsArrayList);
            }
        }
        //        LogUtil.d(TAG, "基站的数量：" + mScreenInsideMarker.size());
    }

    private void addMarkers(ArrayList<MarkerOptions> list) {
        ArrayList<Marker> markers = getAMap().addMarkers(list, false);
        if (markers == null || markers.size() == 0) {
            return;
        }
        for (Marker marker : markers) {
            LogUtil.d(TAG, "marker.getSnippet()>>>>" + marker.getSnippet());
            MarkClickBean bean = new MarkClickBean();
            BaseStation baseStation = null;
            for (BaseStation baseStationBean : baseStationList) {//从查询的基站中取出基站信息
                if (marker.getSnippet().contains(String.valueOf(baseStationBean.id))) {
                    baseStation = baseStationBean;
                }
            }
            bean.setObj(baseStation);
            bean.setOverlayType(OverlayType.BaseStation);
            LogUtil.d(TAG, "baseStation：" + baseStation);
            marker.setObject(bean);
            mScreenInsideMarker.add(marker);
        }
    }

    /**
     * 移除屏幕外面的基站
     */
    private void removeMarkerScreenOutSize() {
        Iterator<Marker> it = mScreenInsideMarker.iterator();
        while (it.hasNext()) {
            Marker marker = it.next();
            LatLng p = marker.getPosition();
            if (!GaodeMapUtil.checkPointInScreenBound(getAMap(), p)) {
                marker.remove();
                it.remove();
                mScreenInsideMarker.remove(marker);
            }
        }
    }


    @Override
    public void onMapLoaded() {
        mHd.sendEmptyMessageDelayed(LOAD_STATION, 300);
    }

    @Override
    public void onMapStatusChangeFinish(Object... obj) {
        if (!mIsRunnableExcete) {
            //            LogUtil.d(TAG, "onMapStatusChangeFinish:>>>>>>>>>>");
            mHd.sendEmptyMessage(LOAD_STATION);
        }
    }

    @Override
    public OverlayType getOverlayType() {
        return OverlayType.BaseStation;
    }

    private AMap getAMap() {
        if (mMapSdk != null ) {
            return (AMap) mMapSdk.getMapControllor();
        } else {
            return null;
        }
    }

    @Override
    public boolean addOverlay(Object... obj) {
        return false;
    }

    @Override
    public boolean clearOverlay() {
        mHd.sendEmptyMessage(CLEAR_STATION);
        return false;
    }

    //基站查询的时候，点击其中一个基站，移动地图到选中基站位置，并设置基站选中状态
    RefreshEventManager.RefreshEventListener mRefreshListener = new RefreshEventManager.RefreshEventListener() {
        @Override
        public void onRefreshed(RefreshEventManager.RefreshType refreshType, Object object) {
            if (refreshType == RefreshEventManager.RefreshType.REFRSH_GOOGLEMAP_BASEDATA) {
                if (object != null) {

                    BaseStation station = (BaseStation) object;
                    getAMap().animateCamera(CameraUpdateFactory.newLatLng(GaodeMapUtil.convertToGaode(mContext, new LatLng(station.latitude, station.longitude), CoordinateConverter.CoordType.GPS)));

                    mSelectStationId = station.id;
                    NewMapFactory.getInstance().setSelectBaseStation(station);

                    mHd.sendEmptyMessageDelayed(LOAD_STATION, 300);

                    if (mSelectMk != null) {
                        MarkClickBean bean = (MarkClickBean) mSelectMk.getObject();
                        BaseStation base = (BaseStation) bean.getObj();
                        SectorView view = new SectorView(mContext);
                        view.setAngle(base.getBearings());
                        view.setSelected(false);
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
                        mSelectMk.setIcon(icon);
                        mSelectMk.setZIndex(0);
                    }

                    for (Marker marker : mScreenInsideMarker) {
                        SectorView view = new SectorView(mContext);
                        view.setAngle(station.getBearings());
                        view.setSelected(true);
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
                        MarkClickBean bean = (MarkClickBean) marker.getObject();
                        BaseStation base = (BaseStation) bean.getObj();
                        if (base.id == station.id) {
                            marker.setIcon(icon);
                            mSelectMk = marker;
                            break;
                        }
                    }
                }
            }
        }
    };

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case LOAD_STATION:
                    loadStation();
                break;
            case CLEAR_STATION:
                if (!mIsRunnableExcete) {
                    mUiHd.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
                    for (Marker marker : mScreenInsideMarker) {
                        marker.remove();
                    }
                    mScreenInsideMarker.clear();
                    mUiHd.sendEmptyMessage(HIDE_PROGRESS_DIALOG);
                }
                break;
            case STOP_LOAD:
                ((Activity) mContext).getParent().finish();
                break;
        }
        return true;
    }

    private void loadStation() {
        mIsRunnableExcete = true;
        if (!mIsDetroy) {
            if (isEnable()) {
                /**
                 * 1.先取出二个点，左上角和右下角
                 * 2.清除屏幕外的点
                 * 3.过滤已经加载过的点
                 * 4.加载到地图上
                 */

                removeMarkerScreenOutSize();
                LatLngBounds bound = getAMap().getProjection().getVisibleRegion().latLngBounds;
                LatLng lbLL = bound.southwest;
                LatLng rtLL = bound.northeast;

                // 如果地图筛选框是要筛选的，使用框选框的经纬度查询
                LatLng[] p = MapCache.readRectLatLng(new LatLng[2], LatLng.class);
                if (p != null && p.length == 2) {
                    LatLng lt = p[0];
                    LatLng rb = p[1];

                    boolean notNull = lt != null && rb != null;

                    if (notNull && !(lt.latitude == 0 && lt.longitude == 0 && rb.latitude == 0 && rb.longitude == 0)) {
                        // 重新赋值经纬度
                        lbLL = new LatLng(rb.latitude, lt.longitude);
                        rtLL = new LatLng(lt.latitude, rb.longitude);
                    }
                }
                //read from db
                baseStationList = BaseStationDBHelper.getInstance(mContext).queryBaseStation(
                        lbLL.longitude, rtLL.latitude, rtLL.longitude, lbLL.latitude,
                        NetUtil.getNetTypes(mContext), 0, BaseStation.MAPTYPE_OUTDOOR, -1);

                if (!baseStationList.isEmpty()) {

                    if (ImportBaseStationFilterManager.getInstance().isFilter()) {
                        // 通过参数筛选过滤
                        for (BaseStation station : baseStationList) {
                            ImportBaseStationFilterManager.getInstance().getFilterStrategy().filter(station);
                        }
                    }

                    //only add screen inside
                    filterOverlay(baseStationList);
                    addMarkerToMap();
                }

            } else {
                for (Marker marker : mScreenInsideMarker) {
                    marker.remove();
                }
                mScreenInsideMarker.clear();
            }
        }
        mIsRunnableExcete = false;
    }
}
