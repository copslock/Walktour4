package com.walktour.gui.newmap2.overlay.baidu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.walktour.Utils.DensityUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.control.config.ParameterSetting;
import com.walktour.control.instance.AlertManager;
import com.walktour.gui.R;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.util.BaiduMapUtil;
import com.walktour.model.AlarmModel;
import com.walktour.model.MapEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 告警图层
 *
 * @author zhicheng.chen
 * @date 2018/6/15
 */
public class BdAlarmOverlayManager extends BaseOverlayManager {

    private final String EXTRA_ALARM_INFO = "EXTRA_ALARM_INFO";
    private final String EXTRA_OVERLAY_TYPE = "EXTRA_OVERLAY_TYPE";

    private final int zIndex = 10;
    private ParameterSetting mParameterSet;//参数设置
    private LruCache<WalkStruct.Alarm, Bitmap> mCache = new LruCache<>(5 * 1024);// 5M
    private ExecutorService AlarmThreadPool = Executors.newFixedThreadPool(2);
    private View mPopLayout;
    private TextView mTitle;
    private TextView mContent;

    public BdAlarmOverlayManager(Context context) {
        super(context);
        mParameterSet = ParameterSetting.getInstance();
    }

    @Override
    public OverlayType getOverlayType() {
        return OverlayType.Alarm;
    }

    @Override
    public boolean addOverlay(Object... obj) {
        AlarmThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                updateData();
                addAlarmOverlay();
            }
        });
        return true;
    }

    @Override
    public boolean onMapClick(Object... obj) {
        if (mPopLayout != null) {
            MapView mapView = (MapView) mMapSdk.getMapView();
            mapView.removeView(mPopLayout);
        }
        return super.onMapClick(obj);
    }

    @Override
    public boolean onMarkerClick(Object... obj) {
        Marker marker = (Marker) obj[0];
        Bundle bundle = marker.getExtraInfo();
        if (bundle != null) {
            if (bundle.getInt(EXTRA_OVERLAY_TYPE) == getOverlayType().getId()
                    && bundle.containsKey(EXTRA_ALARM_INFO)) {
                showInfoPop(marker);
            }
        }
        return super.onMarkerClick(obj);
    }

    private void showInfoPop(Marker marker) {
        Bundle bundle = marker.getExtraInfo();
        AlarmModel alarm = (AlarmModel) bundle.getSerializable(EXTRA_ALARM_INFO);
        if (alarm != null) {
            if (mPopLayout == null) {
                mPopLayout = LayoutInflater.from(mContext).inflate(R.layout.even_descr, null);
                mTitle = (TextView) mPopLayout.findViewById(R.id.event_title);
                mContent = (TextView) mPopLayout.findViewById(R.id.even_content);
            }
            mTitle.setText(alarm.getDescription(mContext));
            mContent.setText(alarm.getMapPopInfo());

            MapView mapView = (MapView) mMapSdk.getMapView();
            mapView.removeView(mPopLayout);
            ViewGroup.LayoutParams params = new MapViewLayoutParams.Builder()
                    .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)// 按照经纬度设置位置
                    .position(marker.getPosition())
                    .width(MapViewLayoutParams.WRAP_CONTENT)
                    .height(MapViewLayoutParams.WRAP_CONTENT)
                    .yOffset(-25)
                    .build();
            mapView.addView(mPopLayout, params);
        }
    }

    /**
     * 更新数据
     */
    private void updateData() {
        super.factory.getAlarmList().clear();
        List<AlarmModel> list = new ArrayList<AlarmModel>();
        list.addAll(AlertManager.getInstance(mContext).getMapAlarmList());
        Iterator<AlarmModel> it = list.iterator();
        while (it.hasNext()) {
            AlarmModel alarm = it.next();
            if (alarm == null) {
                it.remove();
            } else {
                MapEvent mapEvent = alarm.getMapEvent();
                if (mapEvent == null) {
                    it.remove();
                } else {
                    if (mapEvent.getAdjustLatitude() == 0 && mapEvent.getAdjustLongitude() == 0) {
                        LatLng latlng = BaiduMapUtil.convert(mapEvent.getLatitude(), mapEvent.getLongitude());
                        mapEvent.setAdjustLatitude(latlng.latitude);
                        mapEvent.setAdjustLongitude(latlng.longitude);
                    }
                    //移除不在屏幕内的
                    LatLng ll = new LatLng(mapEvent.getAdjustLatitude(), mapEvent.getAdjustLongitude());
                    if (!BaiduMapUtil.checkPointInScreenBound(getMapControllor(), ll)) {
                        it.remove();
                    }
                }
            }
        }
        super.factory.getAlarmList().addAll(list);
    }

    /**
     * 添加alarm图层
     */
    private void addAlarmOverlay() {
        List<AlarmModel> list = new ArrayList<>(super.factory.getAlarmList());
        List<OverlayOptions> optionsList = new ArrayList<>();
        for (AlarmModel alarm : list) {
            MapEvent mapEvent = alarm.getMapEvent();
            Bitmap bitmap = createAlarmBitmap(alarm);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
            LatLng position = new LatLng(mapEvent.getAdjustLatitude(), mapEvent.getAdjustLongitude());
            Bundle extraInfo = new Bundle();
            extraInfo.putInt(EXTRA_OVERLAY_TYPE, getOverlayType().getId());
            extraInfo.putSerializable(EXTRA_ALARM_INFO, alarm);
            OverlayOptions options = new MarkerOptions()
                    .position(position)
                    .extraInfo(extraInfo)
                    .zIndex(zIndex)
                    .icon(bitmapDescriptor);
            optionsList.add(options);
        }
        if (getMapControllor() != null) {
            getMapControllor().addOverlays(optionsList);
        }
    }

    private Bitmap createAlarmBitmap(AlarmModel alarmModel) {
        if (alarmModel == null) {
            return null;
        }
        if (mCache.get(alarmModel.getAlarm()) != null) {
            return mCache.get(alarmModel.getAlarm());
        }
        Drawable eventDrawable = alarmModel.getIconDrawable(mContext);
        if (eventDrawable != null) {
            int size = getLocusRadius();
            Bitmap bitmap = Bitmap.createBitmap(size * 2, size * 2, Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(bitmap);
            int left = 0;
            int right = 2 * size;
            int top = 0;
            int bottom = 2 * size;
            eventDrawable.setBounds(left, top, right, bottom);
            eventDrawable.draw(canvas);
            mCache.put(alarmModel.getAlarm(), bitmap);
            return bitmap;
        }
        return null;
    }

    private int getLocusRadius() {
        float radius = DensityUtil.dip2px(mContext, 10);
        switch (mParameterSet.getLocusSize()) {
            case 0:
                radius = DensityUtil.dip2px(mContext, 12);
                break;
            case 1:
                radius = DensityUtil.dip2px(mContext, 10);
                break;
            case 2:
                radius = DensityUtil.dip2px(mContext, 8);
                break;
            default:
                break;
        }
        return (int) radius;
    }

    private BaiduMap getMapControllor() {
        return (BaiduMap) mMapSdk.getMapControllor();
    }

    @Override
    public boolean clearOverlay() {
        return false;
    }

    @Override
    public void onDestory() {
        super.onDestory();
        if (!AlarmThreadPool.isShutdown()) {
            AlarmThreadPool.shutdown();
        }
    }
}
