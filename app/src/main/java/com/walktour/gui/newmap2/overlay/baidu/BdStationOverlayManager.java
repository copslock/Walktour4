package com.walktour.gui.newmap2.overlay.baidu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.walktour.Utils.NetUtil;
import com.walktour.base.util.SafeHandler;
import com.walktour.framework.database.BaseStationDBHelper;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap.basestation.ImportBaseStationFilterManager;
import com.walktour.gui.newmap2.bean.StationEvent;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.MapCache;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.ui.BaseStationDetailDialog;
import com.walktour.gui.newmap2.ui.MapProgressDialog;
import com.walktour.gui.newmap2.ui.SectorView;
import com.walktour.gui.newmap2.util.BaiduMapUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 百度地图基站信息
 *
 * @author zhicheng.chen
 * @date 2018/6/5
 */
public class BdStationOverlayManager extends BaseOverlayManager implements Handler.Callback {

    private static final String TAG = BdStationOverlayManager.class.getSimpleName();

    private boolean mIsDetroy;
    private boolean mIsRunnableExcete;//是否runnable执行标识

    public static final String EXTRA_STATION_INFO = "EXTRA_STATION_INFO";
    public static final String EXTRA_OVERLAY_TYPE = "EXTRA_OVERLAY_TYPE";//marker 类型

    public static final int LOAD_STATION = 1; //加载基站
    public static final int STOP_LOAD = 2; // 退出地图，停止加载基站
    public static final int CLEAR_STATION = 3;//清除地图基站
    public static final int SHOW_PROGRESS_DIALOG = 4; //显示loading
    public static final int HIDE_PROGRESS_DIALOG = 5; //隐藏loading

    /**
     * 基站详情弹出框
     */
    private BaseStationDetailDialog dialog;
    private List<Overlay> mScreenInsideOverlays = Collections.synchronizedList(new ArrayList<Overlay>());

    private MapProgressDialog mPgDialog;
    private Marker mSelectMk;
    private long mSelectStationId;
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

    public BdStationOverlayManager(Context context) {
        super(context);
        mPgDialog = new MapProgressDialog(((Activity) mContext).getParent());
        mPgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        RefreshEventManager.addRefreshListener(mRefreshListener);

        HandlerThread thread = new HandlerThread("station_load_thread");
        thread.start();
        mHd = new SafeHandler(thread.getLooper(), this);

        EventBus.getDefault().register(this);
    }

    /**
     * filter overlay for map
     */
    private List<OverlayOptions> filterOverlay(List<BaseStation> list) {
        List<OverlayOptions> overlayOptions = new ArrayList<>();

        for (BaseStation station : list) {
            //            if (station.details.isEmpty()) {
            //                continue;
            //            }
            LatLng ll = new LatLng(station.latitude, station.longitude);
            Bundle extraInfo = new Bundle();
            extraInfo.putInt(EXTRA_OVERLAY_TYPE, getOverlayType().getId());
            extraInfo.putSerializable(EXTRA_STATION_INFO, station);
            SectorView view = new SectorView(mContext);
            view.setAngle(station.getBearings());
            view.setSelected(station.id == mSelectStationId);
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(view);
            MarkerOptions option = new MarkerOptions()
                    .position(ll)
                    .zIndex(station.id == mSelectStationId ? 9 : 0)
                    .extraInfo(extraInfo)
                    .icon(bitmap);
            boolean needAdd = true;
            for (Overlay overlay : mScreenInsideOverlays) {
                if (overlay.getExtraInfo() != null) {
                    BaseStation bs = (BaseStation) overlay.getExtraInfo().getSerializable(EXTRA_STATION_INFO);
                    boolean isEq = bs.latitude == station.latitude
                            && bs.latitude == station.latitude
                            && bs.name.equals(station.name)
                            && bs.getBearings() == station.getBearings();
                    if (bs.id == station.id || isEq) {
                        needAdd = false;
                        break;
                    }
                }
            }
            if (needAdd) {
                // add overlay not in screen
                overlayOptions.add(option);
            }
        }
        return overlayOptions;
    }

    private void addMarkerToMap(List<OverlayOptions> overlayOptions) {
        if (!mIsDetroy) {
            //            LogUtil.w(TAG, "addMarkerToMap");
            BaiduMap baiduMap = getMapControllor();
            if (baiduMap != null) {
                List<Overlay> overlays = baiduMap.addOverlays(overlayOptions);
                mScreenInsideOverlays.addAll(overlays);
            }
        }
    }

    /**
     * remove overlay out of screen
     */
    private void removeMarkerScreenOutSize() {
        Iterator<Overlay> it = mScreenInsideOverlays.iterator();
        while (it.hasNext()) {
            Overlay overlay = it.next();
            Marker marker = (Marker) overlay;
            LatLng p = marker.getPosition();
            if (!checkPointInScreenBound(p)) {
                marker.remove();
                it.remove();
            }
        }
    }

    private BaiduMap getMapControllor() {
        return (BaiduMap) mMapSdk.getMapControllor();
    }

    @Override
    public boolean onMarkerClick(Object... obj) {
        Marker marker = (Marker) obj[0];
        Bundle extra = marker.getExtraInfo();
        if (extra != null) {
            int type = extra.getInt(EXTRA_OVERLAY_TYPE);
            if (type == getOverlayType().getId()) {
                BaseStation station = (BaseStation) extra.getSerializable(EXTRA_STATION_INFO);
                showBaseStationDetail(marker, station);
            }
        }
        return true;
    }

    @Override
    public boolean onMapClick(Object... obj) {
        if (dialog != null) {
            dialog.dismiss();
        }
        return super.onMapClick(obj);
    }

    @Override
    public void onMapStatusChangeFinish(Object... obj) {
        if (!mIsRunnableExcete) {
            mHd.sendEmptyMessage(LOAD_STATION);
        }
    }

    @Override
    public OverlayType getOverlayType() {
        return OverlayType.BaseStation;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void filterByStationRectView(StationEvent event) {
        if (event.type == StationEvent.RECT_FILTER) {
            Point[] points = event.points;
            Projection projection = getMapControllor().getProjection();
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

    /**
     * check point is in Screen Bound
     */
    private boolean checkPointInScreenBound(LatLng p) {
        return BaiduMapUtil.checkPointInScreenBound(getMapControllor(), p);
    }

    protected void showBaseStationDetail(final Marker marker, final BaseStation base) {
        if (dialog == null) {
            dialog = new BaseStationDetailDialog(((Activity) mContext).getParent());
        }
        //        dialog.setOnCheckListener(new BaseStationDetailDialog.OnCheckListener() {
        //            @Override
        //            public void onCheckStation(boolean isCheck) {
        //
        //                if (mSelectMk != null && mSelectMk != marker) {
        //                    NewMapFactory.getInstance().setSelectBaseStation(null);
        //                    SectorView view = new SectorView(mContext);
        //                    view.setAngle(base.getBearings());
        //                    view.setSelected(false);
        //                    BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
        //                    mSelectMk.setIcon(icon);
        //                    mSelectMk.setZIndex(0);
        //                }
        //                SectorView view = new SectorView(mContext);
        //                view.setAngle(base.getBearings());
        //                view.setSelected(isCheck);
        //                BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
        //                if (isCheck) {
        //                    NewMapFactory.getInstance().setSelectBaseStation(base);
        //                    marker.setIcon(icon);
        //                    marker.setZIndex(9);
        //                    mSelectMk = marker;
        //                    mSelectStationId = base.id;
        //                } else {
        //                    NewMapFactory.getInstance().setSelectBaseStation(null);
        //                    marker.setIcon(icon);
        //                    marker.setZIndex(0);
        //                    mSelectMk = null;
        //                    mSelectStationId = -1;
        //                }
        //            }
        //        });
        dialog.setBaseStation(base);
        dialog.show();
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


    @Override
    public boolean doSthOnBackPressed() {
        mHd.removeMessages(LOAD_STATION);
        mUiHd.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
        mHd.sendEmptyMessage(STOP_LOAD);
        return mIsRunnableExcete;
    }


    //基站查询的时候，点击其中一个基站，移动地图到选中基站位置，并设置基站选中状态
    RefreshEventManager.RefreshEventListener mRefreshListener = new RefreshEventManager.RefreshEventListener() {
        @Override
        public void onRefreshed(RefreshEventManager.RefreshType refreshType, Object object) {
            if (refreshType == RefreshEventManager.RefreshType.REFRSH_GOOGLEMAP_BASEDATA) {
                if (object != null) {
                    BaseStation station = (BaseStation) object;
                    getMapControllor().setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(station.latitude, station.longitude)));

                    mSelectStationId = station.id;
                    NewMapFactory.getInstance().setSelectBaseStation(station);

                    mHd.sendEmptyMessageDelayed(LOAD_STATION, 300);

                    if (mSelectMk != null) {
                        BaseStation base = (BaseStation) mSelectMk.getExtraInfo().getSerializable(EXTRA_STATION_INFO);
                        SectorView view = new SectorView(mContext);
                        view.setAngle(base.getBearings());
                        view.setSelected(false);
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
                        mSelectMk.setIcon(icon);
                        mSelectMk.setZIndex(0);
                    }

                    for (Overlay overlay : mScreenInsideOverlays) {
                        SectorView view = new SectorView(mContext);
                        view.setAngle(station.getBearings());
                        view.setSelected(true);
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
                        if (overlay instanceof Marker) {
                            BaseStation base = (BaseStation) overlay.getExtraInfo().getSerializable(EXTRA_STATION_INFO);
                            if (base.id == station.id) {
                                ((Marker) overlay).setIcon(icon);
                                mSelectMk = (Marker) overlay;
                                break;
                            }
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
                    for (Overlay overlay : mScreenInsideOverlays) {
                        overlay.remove();
                    }
                    mScreenInsideOverlays.clear();
                    mUiHd.sendEmptyMessage(HIDE_PROGRESS_DIALOG);
                }
                break;
            case STOP_LOAD:
                mUiHd.sendEmptyMessage(HIDE_PROGRESS_DIALOG);
                ((Activity) mContext).getParent().finish();
                break;
        }
        return true;
    }

    private void loadStation() {
        mIsRunnableExcete = true;
        try {
            if (!mIsDetroy) {

                if (isEnable()) {

                    removeMarkerScreenOutSize();

                    BaiduMap baiduMap = getMapControllor();
                    if (baiduMap != null) {

                        LatLngBounds bound = baiduMap.getMapStatus().bound;
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
                        List<BaseStation> list = BaseStationDBHelper.getInstance(mContext).queryBaseStation(
                                lbLL.longitude, rtLL.latitude, rtLL.longitude, lbLL.latitude,
                                NetUtil.getNetTypes(mContext), 2, BaseStation.MAPTYPE_OUTDOOR, -1);
                        if (!list.isEmpty()) {

                            if (ImportBaseStationFilterManager.getInstance().isFilter()) {
                                // 通过参数筛选过滤
                                for (BaseStation station : list) {
                                    ImportBaseStationFilterManager.getInstance().getFilterStrategy().filter(station);
                                }
                            }
                            factory.getBaseStationList().clear();
                            factory.getBaseStationList().addAll(list);

                            //only add screen inside
                            List<OverlayOptions> overlayOptions = filterOverlay(list);
                            addMarkerToMap(overlayOptions);
                        }
                    }
                } else {
                    for (Overlay overlay : mScreenInsideOverlays) {
                        overlay.remove();
                    }
                    mScreenInsideOverlays.clear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mIsRunnableExcete = false;
        }

    }

}
