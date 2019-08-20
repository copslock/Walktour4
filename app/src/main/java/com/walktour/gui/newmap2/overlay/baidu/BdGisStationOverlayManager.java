package com.walktour.gui.newmap2.overlay.baidu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

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
import com.walktour.base.util.NetRequest;
import com.walktour.base.util.ScreenUtils;
import com.walktour.base.util.ToastUtil;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap2.bean.StationEvent;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.ui.BaseStationDetailDialog;
import com.walktour.gui.newmap2.ui.MapFrameView;
import com.walktour.gui.newmap2.ui.MapProgressDialog;
import com.walktour.gui.newmap2.ui.SectorView;
import com.walktour.gui.newmap2.util.BaiduMapUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 百度地图基站信息
 *
 * @author zhicheng.chen
 * @date 2018/6/5
 */
public class BdGisStationOverlayManager extends BaseOverlayManager {

    private static final String IP = "http://172.16.23.253";
    private static final String URL = IP + "/Services/AppService.svc/GetBTS?";

    public static final int LOAD_STATION = 1; //加载基站
    public static final int STOP_LOAD = 2; // 退出地图，停止加载基站
    public static final int SHOW_STATION = 3; // 显示基站
    public static final int CLEAR_STATION = 4;//清除地图基站
    public static final int SHOW_LOADING = 5;//显示加载框
    public static final int HIDE_LOADING = 6;//隐藏加载框

    private static final String TAG = BdGisStationOverlayManager.class.getSimpleName();
    private boolean mIsDetroy;
    private boolean mIsRunnableExcete;//是否runnable执行标识

    private Object mLock = new Object();
    private final String EXTRA_STATION_INFO = "EXTRA_STATION_INFO";
    private final String EXTRA_OVERLAY_TYPE = "EXTRA_OVERLAY_TYPE";//marker 类型
    private final String EXTRA_FLAG_HIGH_LIGHT = "EXTRA_FLAG_HIGH_LIGHT"; // marker 高亮标识

    //基站详情弹出框
    private BaseStationDetailDialog dialog;
    private List<Overlay> mScreenInsideOverlays = Collections.synchronizedList(new ArrayList<Overlay>());
    private List<BaseStation> mStationList = new ArrayList<>();
    private MapProgressDialog mPgDialog;

    //记录上次请求的屏幕左上角与右下角的经纬度
    private LatLng[] mLastReqLl = new LatLng[2];

    private Marker mSelectMk;
    private long mSelectStationId;
    private Handler mExecHd;
    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case SHOW_STATION:
                    synchronized (mLock) {
                        if (!mIsDetroy) {
                            removeMarkerScreenOutSize();
                            BaiduMap baiduMap = getMapControllor();
                            if (baiduMap != null) {
                                //only add screen inside
                                List<OverlayOptions> overlayOptions = filterOverlay(mStationList);
                                addMarkerToMap(overlayOptions);
                                mIsRunnableExcete = false;
                                mUiHd.sendEmptyMessage(HIDE_LOADING);
                                // 通知框选view触发回调
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(MapFrameView.ACTION_MAP_FINISH));
                            }
                            mLock.notify();
                        }
                    }
                    break;
                case CLEAR_STATION:
                    for (Overlay overlay : mScreenInsideOverlays) {
                        overlay.remove();
                    }
                    mScreenInsideOverlays.clear();
                    break;
            }
            return true;
        }
    };

    private Handler mUiHd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == LOAD_STATION) {
                if (getMapControllor().getMapStatus().zoom <= 12) {
                    mExecHd.sendEmptyMessage(CLEAR_STATION);
                    return;
                }
                if (!mIsRunnableExcete
                        && isEnable()
                        && getMapControllor().getMapStatus().zoom > 13) {
                    LatLng[] ll = getMapScreenLatLng();
                    if (checkInBound(ll)) {
                        getStationFromNet(ll);
                    }
                }
            } else if (what == STOP_LOAD) {
                if (mPgDialog != null && mPgDialog.isShowing()) {
                    mPgDialog.dismiss();
                }
                ((Activity) mContext).getParent().finish();
            } else if (what == SHOW_LOADING) {
                if (mPgDialog != null) {
                    mPgDialog.show();
                }
            } else if (what == HIDE_LOADING) {
                if (mPgDialog != null && mPgDialog.isShowing()) {
                    mPgDialog.dismiss();
                }
            }
        }
    };

    private LatLng[] getMapScreenLatLng() {
        LatLng[] ll = new LatLng[2];
        BaiduMap baiduMap = getMapControllor();
        if (baiduMap != null) {
            Projection projection = baiduMap.getProjection();
            if (projection != null) {
                View mapView = mMapSdk.getMapView();
                int width = mapView.getWidth();
                int height = mapView.getHeight();

                Point lt = new Point(0, 0);
                Point rb = new Point(width, height);

                LatLng ltLL = projection.fromScreenLocation(lt);
                LatLng rbLL = projection.fromScreenLocation(rb);

                ll[0] = ltLL;
                ll[1] = rbLL;
            }
        }
        return ll;
    }

    private boolean checkInBound(LatLng[] ll) {
        if (mLastReqLl[0] != null && mLastReqLl[1] != null) {
            LatLngBounds bounds = new LatLngBounds.Builder().include(mLastReqLl[0]).include(mLastReqLl[1]).build();
            if (ll[0] != null
                    && ll[1] != null) {
                Log.w("@@@",bounds.contains(ll[0])+" lt?");
                Log.w("@@@",bounds.contains(ll[1])+" rb?");
                boolean include = !(bounds.contains(ll[0]) && bounds.contains(ll[1]));
                return include;
            }
        }
        mLastReqLl[0] = ll[0];
        mLastReqLl[1] = ll[1];
        return true;
    }


    private void getStationFromNet(LatLng[] ll) {
        mIsRunnableExcete = true;

        mUiHd.sendEmptyMessage(SHOW_LOADING);

        double[] gpsLt = BaiduMapUtil.baidu2Gaode(ll[0]);
        double[] gpsrb = BaiduMapUtil.baidu2Gaode(ll[1]);

        Map<String, String> params = new HashMap<>();
        params.put("Network", "4G");
        params.put("Latitude1", gpsLt[0] + "");
        params.put("Longitude1", gpsLt[1] + "");
        params.put("Latitude2", gpsrb[0] + "");
        params.put("Longitude2", gpsrb[1] + "");

        NetRequest.getFormRequest(URL, params, new NetRequest.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                mStationList.clear();
                JSONArray ja = new JSONArray(result);
                for (int i = 0; i < ja.length(); i++) {
                    BaseStation bs = new BaseStation();
                    JSONObject jo = ja.optJSONObject(i);
                    bs.id = jo.optLong("BTSID");
                    bs.name = jo.optString("BTSName");
                    LatLng ll = BaiduMapUtil.convert(jo.optDouble("Latitude"), jo.optDouble("Longitude"));
                    bs.latitude = ll.latitude;
                    bs.longitude = ll.longitude;
                    mStationList.add(bs);
                }
                Log.i("@@@", mStationList.size() + "~");
                mExecHd.sendEmptyMessage(SHOW_STATION);
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                mPgDialog.dismiss();
                ToastUtil.showLong(mContext,"加载出错");
                mIsRunnableExcete = false;
            }
        });
    }

    /**
     * filter overlay for map
     *
     * @param list
     * @return
     */
    private List<OverlayOptions> filterOverlay(List<BaseStation> list) {
        List<OverlayOptions> overlayOptions = new ArrayList<>();

        for (BaseStation station : list) {
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
            Log.w("@@@", "addMarkerToMap");
            BaiduMap baiduMap = getMapControllor();
            if (baiduMap != null) {
                List<Overlay> overlays = baiduMap.addOverlays(overlayOptions);
                mScreenInsideOverlays.addAll(overlays);
            }
        }
    }

    /**
     * remove overlay in  screen outsize
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

    public BdGisStationOverlayManager(Context context) {
        super(context);

        mPgDialog = new MapProgressDialog(((Activity) mContext).getParent());
        mPgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        RefreshEventManager.addRefreshListener(mRefreshListener);
        EventBus.getDefault().register(this);

        HandlerThread thread = new HandlerThread("station_load_thread");
        thread.start();
        mExecHd = new Handler(thread.getLooper(), mCallback);
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
        Log.w("@@@", "onMapClick");
        if (dialog != null) {
            dialog.dismiss();
        }
        return super.onMapClick(obj);
    }

    @Override
    public void onMapStatusChangeStart(Object... obj) {
        Log.w("@@@", "onMapStatusChangeStart");
    }

    @Override
    public void onMapStatusChange(Object... obj) {
        //        Log.w("@@@", "onMapStatusChange");
    }

    @Override
    public void onMapStatusChangeFinish(Object... obj) {
        Log.w("@@@", "onMapStatusChangeFinish");
        mUiHd.sendEmptyMessageDelayed(LOAD_STATION, 500);
    }

    @Override
    public OverlayType getOverlayType() {
        return OverlayType.GisStation;
    }

    @Override
    public boolean addOverlay(Object... obj) {
        return false;
    }

    @Override
    public boolean clearOverlay() {
        if (!mIsRunnableExcete) {
            for (Overlay overlay : mScreenInsideOverlays) {
                overlay.remove();
            }
            mScreenInsideOverlays.clear();
        } else {
            mPgDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        synchronized (mLock) {
                            // double check
                            if (mIsRunnableExcete) {
                                mLock.wait();
                            }
                            mUiHd.sendEmptyMessage(CLEAR_STATION);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        return false;
    }

    /**
     * check point is in Screen Bound
     *
     * @param p
     * @return
     */
    private boolean checkPointInScreenBound(LatLng p) {
        // pre-load data half of screen width outsize
        int offset = ScreenUtils.getScreenWidth(mContext) / 2;

        Point lt = new Point();
        lt.x = -offset;
        lt.y = -offset;

        Point rb = new Point();
        rb.x = ScreenUtils.getScreenWidth(mContext) + offset;
        rb.y = ScreenUtils.getScreenHeight(mContext) + offset;

        if (getMapControllor() != null) {
            Projection projection = getMapControllor().getProjection();
            LatLng ltLL = projection.fromScreenLocation(lt);
            LatLng rbLL = projection.fromScreenLocation(rb);

            return p.longitude >= ltLL.longitude
                    && p.latitude <= ltLL.latitude
                    && p.longitude <= rbLL.longitude
                    && p.latitude >= rbLL.latitude;
        }
        return false;

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
    public void setEnable(boolean isEnable) {
        if (!isEnable) {
            clearOverlay();
        } else {
            mUiHd.sendEmptyMessageDelayed(LOAD_STATION, 500);
        }
        super.setEnable(isEnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUiHd.sendEmptyMessageDelayed(LOAD_STATION, 1000);
    }

    @Override
    public void onDestory() {
        super.onDestory();
        Log.w("@@@", "onDestory");
        mIsDetroy = true;
        mUiHd.removeMessages(LOAD_STATION);
        RefreshEventManager.removeRefreshListener(mRefreshListener);
        EventBus.getDefault().unregister(this);
    }

    // 框选框里面的基站高亮显示
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void highLightStation(StationEvent event) {

        Point lt = event.points[0];
        Point rb = event.points[1];
        Projection pro = getMapControllor().getProjection();
        if (pro != null) {
            LatLng ltLL = pro.fromScreenLocation(lt);
            LatLng rbLL = pro.fromScreenLocation(rb);
            LatLngBounds bounds = new LatLngBounds.Builder().include(ltLL).include(rbLL).build();
            if (!mIsDetroy && !mIsRunnableExcete) {
                List<Marker> markers = getMapControllor().getMarkersInBounds(bounds);
                if (markers != null && markers.size() > 0) {
                    Log.w("@@@", "highLight");
                    if (event.type == StationEvent.SELECT) {
                        // 重置所有marker为非高亮的
                        List<Overlay> screenInsideOverlay = new ArrayList<>();
                        screenInsideOverlay.addAll(mScreenInsideOverlays);
                        screenInsideOverlay.removeAll(markers);
                        for (Overlay overlay : screenInsideOverlay) {
                            Marker marker = (Marker) overlay;
                            boolean isHighLight = marker.getExtraInfo().getBoolean(EXTRA_FLAG_HIGH_LIGHT, false);
                            if (marker != mSelectMk && isHighLight) {
                                setMarkerHighLight(marker, false);
                            }
                        }
                        // 设置框选框marker为高亮
                        for (Marker marker : markers) {
                            setMarkerHighLight(marker, true);
                        }
                    } else {
                        // 设置框选框marker设为默认
                        for (Marker marker : markers) {
                            setMarkerHighLight(marker, false);
                        }
                    }
                }
            }
        }
    }


    /**
     * 设置 marker 是否高亮显示
     *
     * @param marker
     * @param isHighLight
     */
    private void setMarkerHighLight(Marker marker, boolean isHighLight) {
        BaseStation base = (BaseStation) marker.getExtraInfo().getSerializable(EXTRA_STATION_INFO);
        if (base != null) {
            SectorView view = new SectorView(mContext);
            view.setAngle(base.getBearings());
            view.setHighLight(isHighLight);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromView(view);
            marker.setIcon(icon);
            marker.getExtraInfo().putBoolean(EXTRA_FLAG_HIGH_LIGHT, isHighLight);
        }
    }


    @Override
    public boolean doSthOnBackPressed() {
        if (mIsRunnableExcete) {
            mPgDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (mLock) {
                        try {
                            // double check
                            if (mIsRunnableExcete) {
                                //                                Log.e("@@@","wait");
                                mLock.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mUiHd.sendEmptyMessage(STOP_LOAD);
                    }
                }
            }).start();
        } else {
            mUiHd.sendEmptyMessage(STOP_LOAD);
        }
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

                    mUiHd.sendEmptyMessageDelayed(LOAD_STATION, 300);

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
}
