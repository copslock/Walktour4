package com.walktour.gui.newmap2.overlay.gaode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.LruCache;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.walktour.Utils.DensityUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.control.instance.AlertManager;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap2.bean.MarkClickBean;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.util.GaodeMapUtil;
import com.walktour.model.AlarmModel;
import com.walktour.model.MapEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/6/18
 * @describe 告警图层
 */
public class GdAlarmOverlayManager extends BaseOverlayManager  {

    private  ExecutorService AlarmThreadPool = Executors.newFixedThreadPool(2);
    private LruCache<WalkStruct.Alarm, Bitmap> mCache = new LruCache<>(5 * 1024);// 5M

    public GdAlarmOverlayManager(Context context) {
        super(context);
    }

    @Override
    public void onMapLoaded() {
        super.onMapLoaded();
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
    public void onDestory() {
        super.onDestory();
        if (AlarmThreadPool!=null){
            AlarmThreadPool.shutdown();
        }
    }

    @Override
    public boolean onMarkerClick(Object... obj) {
        return super.onMarkerClick(obj);
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
                        com.amap.api.maps.model.LatLng latlng = GaodeMapUtil.convertToGaode(mContext, GaodeMapUtil.convertToLatLng(new MyLatLng(mapEvent.getLatitude(), mapEvent.getLongitude())), CoordinateConverter.CoordType.GPS);
                        mapEvent.setAdjustLatitude(latlng.latitude);
                        mapEvent.setAdjustLongitude(latlng.longitude);
                    }
                    //移除不在屏幕内的
                    LatLng ll = new LatLng(mapEvent.getAdjustLatitude(), mapEvent.getAdjustLongitude());
                    if (!GaodeMapUtil.checkPointInScreenBound(getMapControllor(), ll)) {
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
        for (AlarmModel alarm : list) {
            MapEvent mapEvent = alarm.getMapEvent();
            Bitmap bitmap = createAlarmBitmap(alarm);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
            LatLng position = new LatLng(mapEvent.getAdjustLatitude(), mapEvent.getAdjustLongitude());
            MarkClickBean bean = new MarkClickBean();
            bean.setObj(alarm);
            bean.setOverlayType(OverlayType.Alarm);
            MarkerOptions options = new MarkerOptions()
                    .position(position)
                    .icon(bitmapDescriptor);
            Marker marker=getMapControllor().addMarker(options);
            marker.setObject(bean);
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
            int size = DensityUtil.dip2px(mContext, 6);
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

    private AMap getMapControllor() {
        return (AMap) mMapSdk.getMapControllor();
    }

    @Override
    public boolean clearOverlay() {
        return false;
    }
}
