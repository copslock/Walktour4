package com.walktour.gui.newmap.overlay.constant;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.util.DisplayMetrics;

import com.baidu.mapapi.map.Marker;
import com.walktour.Utils.WalkStruct.Alarm;
import com.walktour.base.util.LogUtil;
import com.walktour.control.instance.AlertManager;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.layer.BaseMapLayer;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap.overlay.BaseMapOverlay.OverlayType;
import com.walktour.model.AlarmModel;
import com.walktour.model.MapEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 告警事件显示图层
 * 
 * @author jianchao.wang
 * 
 */
public class AlarmPointOverlay extends BaseConstantOverlay {
	/** 像素密度 */
	private float mDensity;
	/** 是否已绘制图层 */
	private boolean isDraw = false;
	/** 告警图片映射<告警类型，图片对象> */
	private Map<Alarm, Bitmap> mBitmapMap = new HashMap<Alarm, Bitmap>();
	/** 覆盖物映射<对象Id，覆盖物> */
	private LongSparseArray<Marker> mMarkerArray = new LongSparseArray<Marker>();

	public AlarmPointOverlay(BaseMapActivity activity, BaseMapLayer mapLayer) {
		super(activity, mapLayer, "AlarmPointOverlay", OverlayType.Alarm);
		DisplayMetrics metrics = new DisplayMetrics();
		this.mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mDensity = metrics.density;
	}

	/**
	 * 清除所有的告警覆盖物
	 */
	private void clearAllMarkers() {
		for (Alarm alarm : this.mBitmapMap.keySet()) {
			this.mBitmapMap.get(alarm).recycle();
		}
		this.mBitmapMap.clear();
		for (int i = 0; i < this.mMarkerArray.size(); i++) {
			this.mMarkerArray.valueAt(i).remove();
		}
		this.mMarkerArray.clear();
	}

	/**
	 * 生成告警图片
	 * 
	 * @param alarmModel
	 *          告警事件
	 */
	private Bitmap createAlarmBitmap(AlarmModel alarmModel) {
		if (this.mBitmapMap.containsKey(alarmModel.getAlarm()))
			return this.mBitmapMap.get(alarmModel.getAlarm());
		Drawable eventDrawable = alarmModel.getIconDrawable(this.mActivity);
		if (eventDrawable != null) {
			int size = (int) (6 * mDensity);
			Bitmap bitmap = Bitmap.createBitmap(size *5, size * 5, Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			int left = 0;
			int right = 5 * size;
			int top = 0;
			int bottom = 5 * size;
			eventDrawable.setBounds(left, top, right, bottom);
			eventDrawable.draw(canvas);
			this.mBitmapMap.put(alarmModel.getAlarm(), bitmap);
			return bitmap;
		}
		return null;
	}

	@Override
	protected void getOverlayItems(MyLatLng valueLeftTop, MyLatLng valueRightBottom) {
		super.factory.getAlarmList().clear();
		List<AlarmModel> list = new ArrayList<AlarmModel>();
		list.addAll(AlertManager.getInstance(this.mActivity).getMapAlarmList());
		for (int i = 0; i < list.size(); i++) {
			AlarmModel alarm = list.get(i);
			if (alarm == null || alarm.getMapEvent() == null)
				continue;
			MapEvent mapEvent = alarm.getMapEvent();
			if (mapEvent.getAdjustLatitude() == 0 && mapEvent.getAdjustLongitude() == 0) {
				MyLatLng latlng = this.mMapLayer.adjustFromGPS(mapEvent.getLatitude(), mapEvent.getLongitude());
				mapEvent.setAdjustLatitude(latlng.latitude);
				mapEvent.setAdjustLongitude(latlng.longitude);
			}
			if (mapEvent.getAdjustLatitude() > valueLeftTop.latitude
					|| mapEvent.getAdjustLatitude() < valueRightBottom.latitude) {
				list.remove(i);
				i--;
				continue;
			}
			if (mapEvent.getAdjustLongitude() < valueLeftTop.longitude
					|| mapEvent.getAdjustLongitude() > valueRightBottom.longitude) {
				list.remove(i);
				i--;
				continue;
			}
			Point point = super.convertLatlngToPoint(mapEvent.getAdjustLatitude(), mapEvent.getAdjustLongitude());
			mapEvent.setX(point.x);
			mapEvent.setY(point.y);
		}
		LogUtil.d(TAG, "------getOverlayItems----alarmList.size():" + list.size() + "----");
		super.factory.getAlarmList().addAll(list);
		this.isDraw = false;
	}

	@Override
	protected void drawCanvas(Canvas canvas) {
		if (this.isDraw)
			return;
		List<AlarmModel> list = new ArrayList<AlarmModel>(super.factory.getAlarmList());
		if (list.isEmpty()) {
			this.clearAllMarkers();
			return;
		}
		// 绘制告警事件
		LongSparseArray<Marker> markerArray = new LongSparseArray<Marker>();
		for (int i = list.size() - 1; i >= 0; i--) {
			AlarmModel alarm = list.get(i);
			MapEvent mapEvent = alarm.getMapEvent();
			if (mapEvent == null)
				continue;
			long id = mapEvent.getId();
			if (this.mMarkerArray.get(id) != null) {
				markerArray.put(id, this.mMarkerArray.get(id));
				this.mMarkerArray.remove(id);
				continue;
			}
			Bitmap bitmap = this.createAlarmBitmap(alarm);
			if (bitmap != null) {
				Bundle bundle = new Bundle();
				bundle.putInt("type", super.mType.getId());
				bundle.putLong("id", id);
				Marker marker = (Marker) super.mMapLayer.drawBitmapMarker(
						new MyLatLng(mapEvent.getAdjustLatitude(), mapEvent.getAdjustLongitude()), bitmap, 0.5f, 0.5f, bundle);
				markerArray.put(id, marker);
			}
		}
		for (int i = 0; i < this.mMarkerArray.size(); i++) {
			this.mMarkerArray.valueAt(i).remove();
		}
		this.mMarkerArray.clear();
		this.mMarkerArray = markerArray;
		list.clear();
		list = null;
		this.isDraw = true;
	}

	@Override
	public void onDestroy() {
		this.clearAllMarkers();
	}

	@Override
	public void onResume() {
		this.isDraw = false;
	}
}
