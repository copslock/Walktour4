package com.walktour.gui.newmap.overlay.constant;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap.layer.BaseMapLayer;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap.overlay.BaseMapOverlay.OverlayType;
import com.walktour.service.metro.MetroFactory;
import com.walktour.service.metro.model.MetroCity;
import com.walktour.service.metro.model.MetroRoute;
import com.walktour.service.metro.model.MetroStation;

import java.util.ArrayList;
import java.util.List;

/**
 * 地铁线路图层
 * 
 * @author jianchao.wang
 * 
 */
public class MetroRouteOverlay extends BaseConstantOverlay {
	/** 地铁图层的工厂类 */
	private MetroFactory mFactory;
	/** 当前选择的城市的线路总图，不包括当前线路 */
	private List<List<MyLatLng>> mCityRoutes = new ArrayList<List<MyLatLng>>();
	/** 当前选择的路线 */
	private List<MyLatLng> mRouteKML = new ArrayList<MyLatLng>();
	/** 当前选择的线路的所有站点 */
	private List<MetroStation> mStations = new ArrayList<MetroStation>();
	/** 字体画笔 */
	private Paint mTxtPaint;
	/** 线条画笔 */
	private Paint mLinePaint;

	public MetroRouteOverlay(BaseMapActivity activity, BaseMapLayer mapLayer) {
		super(activity, mapLayer, "MetroRouteOverlay", OverlayType.MetroRoute);
		this.mFactory = MetroFactory.getInstance(activity);
		this.mTxtPaint = new Paint();
		this.mTxtPaint.setAntiAlias(true);
		this.mTxtPaint.setStyle(Paint.Style.FILL);
		this.mTxtPaint.setTypeface(null);
		this.mTxtPaint.setStrokeWidth(0);
		this.mTxtPaint.setTextSize(12 * super.mDensity);
		this.mTxtPaint.setColor(super.mActivity.getResources().getColor(R.color.app_main_text_color));
		super.mPaint = new Paint();
		super.mPaint.setStyle(Paint.Style.FILL);
		super.mPaint.setAntiAlias(true);
		super.mPaint.setColor(super.mActivity.getResources().getColor(R.color.light_blue));
		this.mLinePaint = new Paint();
		this.mLinePaint.setStyle(Paint.Style.STROKE);
		this.mLinePaint.setAntiAlias(true);
		this.mLinePaint.setStrokeWidth(3 * super.mDensity);
		this.mLinePaint.setColor(super.mActivity.getResources().getColor(R.color.light_blue));
	}

	@Override
	protected void getOverlayItems(MyLatLng valueLeftTop, MyLatLng valueRightBottom) {
		if (NewMapFactory.getInstance().getMapType() == NewMapFactory.MAP_TYPE_NONE) {
			this.getCityRoutesOverlayItems();
		}
		this.getCurrentRoutesOverlayItems();
	}

	/**
	 * 获取选择的路线的覆盖物
	 */
	private void getCurrentRoutesOverlayItems() {
		MetroRoute currentRoute = this.mFactory.getCurrentRoute(DatasetManager.isPlayback);
		this.mRouteKML.clear();
		this.mStations.clear();
		if (currentRoute != null) {
			this.adjustLatLng(currentRoute);
			this.mRouteKML.addAll(currentRoute.getBaiduKml());
			for (int i = 0; i < currentRoute.getStations().size(); i++) {
				MetroStation station = currentRoute.getStations().get(i);
				if (station.getBaiduLatLng() == null)
					station.setBaiduLatLng(
							super.mMapLayer.adjustFromGPS(station.getLatLng().latitude, station.getLatLng().longitude));
			}
			this.mStations.addAll(currentRoute.getStations());
		}
		LogUtil.d(TAG, "------getCurrentRoutesOverlayItems----mRouteKML.size():" + this.mRouteKML.size() + "----");
	}

	/**
	 * 获取城市路线图的覆盖物
	 */
	private void getCityRoutesOverlayItems() {
		MetroCity currentCity = this.mFactory.getCurrentCity(DatasetManager.isPlayback);
		MetroRoute currentRoute = this.mFactory.getCurrentRoute(DatasetManager.isPlayback);
		this.mCityRoutes.clear();
		if (currentCity != null) {
			for (MetroRoute route : currentCity.getRoutes()) {
				if (route.equals(currentRoute))
					continue;
				this.adjustLatLng(route);
				this.mCityRoutes.add(route.getBaiduKml());
			}
		}
		LogUtil.d(TAG, "------getCityRoutesOverlayItems----mCityRoutes.size():" + this.mCityRoutes.size() + "----");
	}

	/**
	 * 校正地铁路线的经纬度坐标
	 * 
	 * @param route
	 *          地铁路线
	 */
	private void adjustLatLng(MetroRoute route) {
		if (route.getBaiduKml().isEmpty() && !route.getKml().isEmpty()) {
			List<MyLatLng> list = new ArrayList<MyLatLng>();
			for (MyLatLng latlng : route.getKml()) {
				list.add(super.mMapLayer.adjustFromGPS(latlng.latitude, latlng.longitude));
			}
			route.setBaiduKml(list);
		}
	}

	@Override
	protected void drawCanvas(Canvas canvas) {
		if (!this.mFactory.isRuning() && !DatasetManager.isPlayback)
			return;
		if (NewMapFactory.getInstance().getMapType() == NewMapFactory.MAP_TYPE_NONE) {
			this.drawCityRoutes(canvas);
		}
		this.drawCurrentRoute(canvas);
	}

	/**
	 * 绘制当前测试路线
	 * 
	 * @param canvas
	 *          画布
	 */
	private void drawCurrentRoute(Canvas canvas) {
		if (this.mRouteKML.isEmpty())
			return;
		List<MyLatLng> list = new ArrayList<MyLatLng>(this.mRouteKML);
		// 绘制路线
		Path path = new Path();
		for (int i = 0; i < list.size(); i++) {
			MyLatLng latlng = list.get(i);
			Point point = super.convertLatlngToPoint(latlng.latitude, latlng.longitude);
			if (i == 0)
				path.moveTo(point.x, point.y);
			else
				path.lineTo(point.x, point.y);
		}
		this.mLinePaint.setColor(super.mActivity.getResources().getColor(R.color.light_blue));
		this.mLinePaint.setStrokeWidth(3 * super.mDensity);
		canvas.drawPath(path, this.mLinePaint);
		this.mPaint.setColor(super.mActivity.getResources().getColor(R.color.light_blue));
		for (int i = 0; i < this.mStations.size(); i++) {
			MetroStation station = this.mStations.get(i);
			Point point = super.convertLatlngToPoint(station.getBaiduLatLng().latitude, station.getBaiduLatLng().longitude);
			// 绘制站点
			int radius = (int) (6 * mDensity);
			canvas.drawCircle(point.x, point.y, radius, mPaint);
			// 绘制站点名称
			int width = (int) mTxtPaint.measureText(station.getName());
			canvas.drawText(station.getName(), point.x - width - radius, point.y, mTxtPaint);
		}
		list.clear();
		list = null;
	}

	/**
	 * 绘制城市线路背景图
	 * 
	 * @param canvas
	 *          画布
	 */
	private void drawCityRoutes(Canvas canvas) {
		if (this.mCityRoutes.isEmpty())
			return;
		List<List<MyLatLng>> list = new ArrayList<List<MyLatLng>>(this.mCityRoutes);
		this.mLinePaint.setColor(super.mActivity.getResources().getColor(R.color.app_grey_color));
		this.mLinePaint.setStrokeWidth(1 * super.mDensity);
		for (int i = 0; i < list.size(); i++) {
			List<MyLatLng> points = list.get(i);
			Path path = new Path();
			for (int j = 0; j < points.size(); j++) {
				Point point = super.convertLatlngToPoint(points.get(j).latitude, points.get(j).longitude);
				if (j == 0)
					path.moveTo(point.x, point.y);
				else
					path.lineTo(point.x, point.y);
			}

			canvas.drawPath(path, this.mLinePaint);
		}
		list.clear();
		list = null;
	}

	@Override
	public void onDestroy() {
		// 无须实现
	}

	@Override
	public void onResume() {
		// 无须实现
	}

}
