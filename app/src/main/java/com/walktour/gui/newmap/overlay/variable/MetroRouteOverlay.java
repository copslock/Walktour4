package com.walktour.gui.newmap.overlay.variable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.layer.BaseMapLayer;
import com.walktour.gui.newmap.metro.MetroStationOperatePopWindow;
import com.walktour.gui.newmap.overlay.BaseMapOverlay.OverlayType;
import com.walktour.service.metro.MetroFactory;
import com.walktour.service.metro.model.MetroStation;

/**
 * 地铁线路显示图层
 *
 * @author jianchao.wang
 *
 */
public class MetroRouteOverlay extends BaseVariableOverlay {

	/** 地铁图层的工厂类 */
	private MetroFactory mFactory;
	/** 基站详情弹出框 */
	private MetroStationOperatePopWindow mWindow;
	/** 站点园大小 */
	private int[] mRediuses = new int[6];
	/** 当前位置 */
	private int mPos = 0;
	/** 画笔 */
	private Paint mPaint;
	/** 当前站点的显示对象 */
	private Marker mMarker;
	/** 站点图像 */
	private Bitmap mBitMap;

	public MetroRouteOverlay(BaseMapActivity activity, View parent, BaseMapLayer mapLayer) {
		super(activity, parent, mapLayer, "MetroRouteOverlay", OverlayType.MetroRoute);
		mFactory = MetroFactory.getInstance(activity);
		this.mWindow = null;
		for (int i = 0; i < 4; i++) {
			int radius = (int) ((6 + i * 2) * mDensity);
			this.mRediuses[i] = radius;
		}
		this.mRediuses[4] = this.mRediuses[2];
		this.mRediuses[5] = this.mRediuses[1];
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setTypeface(null);
		mPaint.setStrokeWidth(1);
		mPaint.setColor(super.mActivity.getResources().getColor(R.color.light_blue));
	}

	/**
	 * 生成站点显示图
	 */
	private void createStationBitmap() {
		if (this.mBitMap != null)
			return;
		LogUtil.d(TAG, "-----createStationBitmap-----");
		int size = (int) (12 * mDensity);
		int radius = (int) (6 * mDensity);
		this.mBitMap = Bitmap.createBitmap(size * 2, size * 2, Config.ARGB_8888);
		Canvas canvas = new Canvas(this.mBitMap);
		canvas.drawCircle(size, size, radius, mPaint);
	}

	@Override
	protected void drawCanvas(Canvas canvas) {
		if (!this.mFactory.isRuning()) {
			this.clearMarker();
			return;
		}
		MetroStation currentStation = this.mFactory.getCurrentStation();
		if (this.createMarker()) {
			if (currentStation == null)
				return;
			this.mMarker
					.setPosition(new LatLng(currentStation.getBaiduLatLng().latitude, currentStation.getBaiduLatLng().longitude));
			this.mMarker.setVisible(true);
		}
		Point point = super.convertLatlngToPoint(currentStation.getBaiduLatLng().latitude,
				currentStation.getBaiduLatLng().longitude);
		int radius = this.mRediuses[this.mPos];
		canvas.drawCircle(point.x, point.y, radius, mPaint);
		this.mPos++;
		if (this.mPos >= this.mRediuses.length)
			this.mPos = 0;

	}

	/**
	 * 清除覆盖物
	 */
	private void clearMarker() {
		if (this.mMarker == null)
			return;
		this.mMarker.remove();
		this.mMarker = null;
		if (this.mBitMap != null) {
			this.mBitMap.recycle();
			this.mBitMap = null;
		}
	}

	@Override
	protected boolean onClick(Point click) {
		if (this.mFactory.getCurrentStation() == null)
			return false;
		this.closeShowPopWindow();
		return false;
	}

	@Override
	protected boolean onLongClick(Point click) {
		return false;
	}

	/**
	 * 显示选中的操作对话框
	 *
	 */
	protected void showStationOperate() {
		if (this.mWindow != null && this.mWindow.isShow())
			return;
		MetroStation currentStation = this.mFactory.getCurrentStation();
		LogUtil.d(TAG, "----showStationOperate----" + currentStation.getName() + currentStation.isReach());
		this.mWindow = new MetroStationOperatePopWindow(this.parent, this.mActivity, currentStation);
		this.mWindow.showPopWindow();
		this.mActivity.setShowPopWindow(true);
	}

	@Override
	public void closeShowPopWindow() {
		if (this.mWindow != null) {
			this.mWindow.closePopWindow();
			this.mWindow = null;
			this.mActivity.setShowPopWindow(false);
		}
	}

	/**
	 * 生成覆盖物对象
	 *
	 * @return 是否生成成功
	 */
	private boolean createMarker() {
		MetroStation currentStation = this.mFactory.getCurrentStation();
		if (this.mFactory.isLastStation(currentStation) && currentStation.isReach()) {
			this.clearMarker();
			return false;
		}
		if (this.mMarker != null) {
			if (currentStation != null) {
				Bundle bundle = new Bundle();
				bundle.putInt("type", super.mType.getId());
				bundle.putLong("id", currentStation.getId());
				this.mMarker.setExtraInfo(bundle);
			}
			return true;
		}
		if (currentStation == null)
			return false;
		LogUtil.d(TAG, "-----createMarker-----");
		if (currentStation.getBaiduLatLng() == null)
			currentStation.setBaiduLatLng(
					super.mMapLayer.adjustFromGPS(currentStation.getLatLng().latitude, currentStation.getLatLng().longitude));
		this.createStationBitmap();
		Bundle bundle = new Bundle();
		bundle.putInt("type", super.mType.getId());
		bundle.putLong("id", currentStation.getId());
		this.mMarker = (Marker) super.mMapLayer.drawBitmapMarker(currentStation.getBaiduLatLng(), this.mBitMap, 0.5f, 0.5f,
				bundle);
		this.mMarker.setVisible(false);
		return true;
	}

	@Override
	public void changeMapType() {
		this.closeShowPopWindow();
		this.clearMarker();
		this.createMarker();
	}

	@Override
	public void onDestroy() {
		LogUtil.d(TAG, "-----onDestroy-----");
		this.clearMarker();
		this.closeShowPopWindow();
	}

	@Override
	public void onResume() {
		LogUtil.d(TAG, "-----onResume-----");
		this.createMarker();
	}

	@Override
	public boolean onMarkerClick(Bundle bundle) {
		int typeId = bundle.getInt("type", -1);
		if (typeId == -1 || !OverlayType.get(typeId).equals(super.mType)) {
			return false;
		}
		if (bundle.getLong("id") == this.mFactory.getCurrentStation().getId()) {
			this.showStationOperate();
			return true;
		}
		this.closeShowPopWindow();
		return false;
	}

}
