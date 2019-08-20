package com.walktour.gui.newmap.overlay.constant;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;

import com.baidu.mapapi.map.Marker;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.framework.database.BaseStationDBHelper;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.layer.BaseMapLayer;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap.overlay.BaseMapOverlay.OverlayType;
import com.walktour.gui.setting.SysMap;

import java.util.ArrayList;
import java.util.List;

/**
 * 基站显示图层
 * 
 * @author jianchao.wang
 * 
 */
public class BaseStationOverlay extends BaseConstantOverlay {

	/** 基站方位颜色集合，方便区分不同的方向角 */
	private int[] mColorSets = { Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN };
	/** 是否已绘制图层 */
	private boolean isDraw = false;
	/** 基站图片集合 */
	private LongSparseArray<Bitmap> mBitmapArray = new LongSparseArray<Bitmap>();
	/** 基站覆盖物集合 */
	private LongSparseArray<Marker> mMarkerArray = new LongSparseArray<Marker>();

	public BaseStationOverlay(BaseMapActivity activity, BaseMapLayer mapLayer) {
		super(activity, mapLayer, "BaseStationOverlay", OverlayType.BaseStation);
	}

	/**
	 * 绘制基站图片
	 * 
	 * @param bearings
	 *          方向角
	 * @return
	 */
	private Bitmap createBaseStation(int[] bearings) {
		Bitmap bitmap = Bitmap.createBitmap(overlayRadius * 2, overlayRadius * 2, Config.ARGB_4444);
		Canvas canvas = new Canvas(bitmap);
		int centerX = overlayRadius;
		int centerY = overlayRadius;
		RectF oval = new RectF();
		oval.top = 2;
		oval.bottom = centerY + overlayRadius - 2;
		oval.left = 2;
		oval.right = centerX + overlayRadius - 2;
		mPaint.setShadowLayer(1f, 1, 1f, Color.BLACK);
		for (int i = 0; i < bearings.length; i++) {
			float startAngle = bearings[i] - 90 - 30;
			mPaint.setColor(mColorSets[i % mColorSets.length]);
			canvas.drawArc(oval, startAngle, 60, true, mPaint);
		}
		return bitmap;
	}

	/**
	 * 清除所有的基站覆盖物
	 */
	private void clearAllMarkers() {
		for (int i = 0; i < this.mBitmapArray.size(); i++) {
			this.mBitmapArray.valueAt(i).recycle();
		}
		this.mBitmapArray.clear();
		for (int i = 0; i < this.mMarkerArray.size(); i++) {
			this.mMarkerArray.valueAt(i).remove();
		}
		this.mMarkerArray.clear();
	}

	/**
	 * 获取当前显示的基站的网络类型
	 * 
	 * @return
	 */
	private String getNetTypes() {
		StringBuffer netTypeSB = new StringBuffer();
		SharePreferencesUtil util = SharePreferencesUtil.getInstance(mActivity.getApplicationContext());
		if (util.getInteger(SysMap.BASE_DISPLAY_TYPE, 0) == 1) {
			if (util.getBoolean(SysMap.BASE_GSM, false)) {
				netTypeSB.append(WalktourConst.NetWork.GSM + ",");
			}
			if (util.getBoolean(SysMap.BASE_WCDMA, false)) {
				netTypeSB.append(WalktourConst.NetWork.WCDMA + ",");
			}
			if (util.getBoolean(SysMap.BASE_CDMA, false)) {
				netTypeSB.append(WalktourConst.NetWork.CDMA + ",");
			}
			if (util.getBoolean(SysMap.BASE_TDSCDMA, false)) {
				netTypeSB.append(WalktourConst.NetWork.TDSDCDMA + ",");
			}
			if (util.getBoolean(SysMap.BASE_LTE, false)) {
				netTypeSB.append(WalktourConst.NetWork.LTE + ",");
			}
			if (util.getBoolean(SysMap.BASE_NB_IoT, false)) {
				netTypeSB.append(WalktourConst.NetWork.NB_IoT + ",");
			}
			if (!StringUtil.isNullOrEmpty(netTypeSB.toString())) {
				return netTypeSB.toString().substring(0, netTypeSB.toString().length() - 1);
			}
			return "";
		}
		MyPhoneState state = MyPhoneState.getInstance();
		NetType nettype = state.getCurrentNetType(mActivity);
		LogUtil.d(TAG, "nettype =" + nettype.toString());
		switch (nettype) {
		case GSM:
			netTypeSB.append(WalktourConst.NetWork.GSM);
			break;
		case WCDMA:
			netTypeSB.append(WalktourConst.NetWork.WCDMA);
			break;
		case EVDO:
		case CDMA:
			netTypeSB.append(WalktourConst.NetWork.CDMA);
			break;
		case TDSCDMA:
			netTypeSB.append(WalktourConst.NetWork.TDSDCDMA);
			break;
		case LTE:
			netTypeSB.append(WalktourConst.NetWork.LTE);
			break;
            case NBIoT:
			netTypeSB.append(WalktourConst.NetWork.NB_IoT);
			break;
		default:
			break;
		}
		return netTypeSB.toString();
	}

	@Override
	protected void getOverlayItems(MyLatLng valueLeftTop, MyLatLng valueRightBottom) {
		super.factory.getBaseStationList().clear();
		List<BaseStation> list = BaseStationDBHelper.getInstance(mActivity.getApplicationContext()).queryBaseStation(
				valueLeftTop.longitude, valueLeftTop.latitude, valueRightBottom.longitude, valueRightBottom.latitude,
				getNetTypes(), (mActivity.getMapType() == BaseMapActivity.MAP_TYPE_BAIDU || mActivity.getMapType() == BaseMapActivity.MAP_TYPE_BAIDU) ? 2 : 1, BaseStation.MAPTYPE_OUTDOOR,
				50 * 3);
//		List<BaseStation> list = BaseStationDBHelper.getInstance(mActivity.getApplicationContext()).queryAllBaseStation();
		LogUtil.d(TAG, "------getOverlayItems----baseStationList.size():" + list.size() + "----");
		super.factory.getBaseStationList().addAll(list);
		this.isDraw = false;
	}

	@Override
	protected void drawCanvas(Canvas canvas) {
		if (this.isDraw)
			return;
		this.clearAllMarkers();
		List<BaseStation> list = new ArrayList<BaseStation>(super.factory.getBaseStationList());
		LongSparseArray<Bitmap> bitmapArray = new LongSparseArray<Bitmap>();
		LongSparseArray<Marker> markerArray = new LongSparseArray<Marker>();
		for (int i = 0; i < list.size(); i++) {
			BaseStation base = list.get(i);
			if (this.mMarkerArray.get(base.id) != null) {
				bitmapArray.put(base.id, this.mBitmapArray.get(base.id));
				markerArray.put(base.id, this.mMarkerArray.get(base.id));
				this.mBitmapArray.remove(base.id);
				this.mMarkerArray.remove(base.id);
				continue;
			}
			int[] bearings = base.getBearings();
			Bitmap bitmap = this.createBaseStation(bearings);
			bitmapArray.put(base.id, bitmap);
			Bundle bundle = new Bundle();
			bundle.putInt("type", super.mType.getId());
			bundle.putLong("id", base.id);
			Marker marker = (Marker) super.mMapLayer.drawBitmapMarker(new MyLatLng(base.latitude, base.longitude), bitmap,
					0.5f, 0.5f, bundle);
			markerArray.put(base.id, marker);
		}
		this.clearAllMarkers();
		this.mBitmapArray = bitmapArray;
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
