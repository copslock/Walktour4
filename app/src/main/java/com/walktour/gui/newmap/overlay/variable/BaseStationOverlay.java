package com.walktour.gui.newmap.overlay.variable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import com.baidu.mapapi.map.Marker;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.basestation.BaseStationDetailPopWindow;
import com.walktour.gui.newmap.layer.BaseMapLayer;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap.overlay.BaseMapOverlay.OverlayType;

import java.util.ArrayList;
import java.util.List;

/**
 * 基站显示图层
 * 
 * @author jianchao.wang
 * 
 */
public class BaseStationOverlay extends BaseVariableOverlay {

	/** 是否在做选择基站扇区的操作 */
	private boolean isSelectStationSector = false;
	/** 基站扇区区域映射 */
	private SparseArray<Region> stationSectorArray = new SparseArray<Region>();
	/** 选择基站扇区缩放比例 */
	private final int stationPopScale = 3;
	/** 基站详情弹出框 */
	private BaseStationDetailPopWindow window;
	/** 基站方位颜色集合，方便区分不同的方向角 */
	private int[] mColorSets = { Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN };
	/** 选择基站的显示对象 */
	private Marker mMarker;
	/** 选择基站的图像 */
	private Bitmap mBitMap;
	/** 最后一次选择的基站 */
	private BaseStation mLastStation;
	/** 最后一次选择的扇区 */
	private int mLastDetailIndex;

	public BaseStationOverlay(BaseMapActivity activity, View parent, BaseMapLayer mapLayer) {
		super(activity, parent, mapLayer, "BaseStationOverlay", OverlayType.BaseStation);
	}

	@Override
	protected void drawCanvas(Canvas canvas) {
		BaseStation selectBaseStation = super.factory.getSelectBaseStation();
		if (selectBaseStation == null) {
			this.clearMarker();
			this.mLastStation = null;
			this.mLastDetailIndex = -1;
			return;
		}
		if (!isSelectStationSector && selectBaseStation.equals(this.mLastStation)
				&& selectBaseStation.detailIndex == this.mLastDetailIndex)
			return;
		this.clearMarker();
		List<BaseStation> list = new ArrayList<BaseStation>(super.factory.getBaseStationList());
		if (!list.contains(selectBaseStation))
			return;
		int[] bearings = selectBaseStation.getBearings();
		if (isSelectStationSector) {
			Point point = super.convertLatlngToPoint(selectBaseStation.latitude, selectBaseStation.longitude);
			this.drawSelectStationSector(canvas, point, bearings);
		} else {
			this.mBitMap = this.createBaseStationBitmap(bearings);
			Bundle bundle = new Bundle();
			bundle.putInt("type", super.mType.getId());
			bundle.putLong("id", selectBaseStation.id);
			this.mMarker = (Marker) super.mMapLayer.drawBitmapMarker(
					new MyLatLng(selectBaseStation.latitude, selectBaseStation.longitude), this.mBitMap, 0.5f, 0.5f, bundle);
			this.mLastStation = selectBaseStation;
			this.mLastDetailIndex = selectBaseStation.detailIndex;
		}
	}

	/**
	 * 绘制基站图片
	 * 
	 * @param bearings
	 *          方向角
	 * @return
	 */
	private Bitmap createBaseStationBitmap(int[] bearings) {
		if (bearings == null || bearings.length == 0)
			return null;
		int size = overlayRadius * 4;
		Bitmap bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		int centerX = size / 2;
		int centerY = centerX;
		mPaint.setColor(Color.parseColor("#FF7F00"));
		canvas.drawCircle(centerX, centerY, overlayRadius, mPaint);
		BaseStation selectBaseStation = super.factory.getSelectBaseStation();
		RectF oval = new RectF();
		oval.top = centerY - overlayRadius;
		oval.bottom = centerY + overlayRadius;
		oval.left = centerX - overlayRadius;
		oval.right = centerX + overlayRadius;
		RectF selectOval = new RectF();
		selectOval.top = 0;
		selectOval.bottom = size;
		selectOval.left = 0;
		selectOval.right = size;
		mPaint.setShadowLayer(1f, 1, 1f, Color.BLACK);
		for (int i = 0; i < bearings.length; i++) {
			float startAngle = bearings[i] - 90 - 30;
			mPaint.setColor(mColorSets[i % mColorSets.length]);
			if (selectBaseStation.detailIndex == i)
				canvas.drawArc(selectOval, startAngle, 60, true, mPaint);
			else
				canvas.drawArc(oval, startAngle, 60, true, mPaint);
		}
		return bitmap;
	}

	/**
	 * 绘制要选择扇区的基站
	 * 
	 * @param canvas
	 *          画布
	 * @param point
	 *          坐标点
	 * @param bearings
	 *          朝向
	 */
	private void drawSelectStationSector(Canvas canvas, Point point, int bearings[]) {
		if (bearings == null || bearings.length == 0)
			return;
		mPaint.setColor(Color.parseColor("#FF7F00"));
		canvas.drawCircle(point.x, point.y, overlayRadius, mPaint);
		this.stationSectorArray.clear();
		RectF oval = new RectF();
		oval.top = point.y - overlayRadius * this.stationPopScale;
		oval.bottom = point.y + overlayRadius * this.stationPopScale;
		oval.left = point.x - overlayRadius * this.stationPopScale;
		oval.right = point.x + overlayRadius * this.stationPopScale;
		mPaint.setShadowLayer(1f, 1, 1f, Color.BLACK);
		Region base = new Region();
		base.set((int) oval.left, (int) oval.top, (int) oval.right, (int) oval.bottom);
		Point pathPoint;
		int startAngle;
		int r = overlayRadius * this.stationPopScale;
		for (int i = 0; i < bearings.length; i++) {
			startAngle = bearings[i] - 90 - 30;
			mPaint.setColor(mColorSets[i % mColorSets.length]);
			canvas.drawArc(oval, startAngle, 60, true, mPaint);
			Region region = new Region();
			Path path = new Path();
			path.moveTo(point.x, point.y);
			pathPoint = this.calculateCriclePoint(point.x, point.y, r, startAngle);
			path.lineTo(pathPoint.x, pathPoint.y);
			pathPoint = this.calculateCriclePoint(point.x, point.y, r, startAngle + 60);
			path.lineTo(pathPoint.x, pathPoint.y);
			path.close();
			region.setPath(path, base);
			this.stationSectorArray.put(i, region);
		}
	}

	/**
	 * 计算指定半径的园上的指定角度的坐标
	 * 
	 * @param centerX
	 *          中心点X坐标
	 * @param centerY
	 *          中心点Y坐标
	 * @param r
	 *          园半径
	 * @param angle
	 *          指定角度
	 * @return 坐标
	 */
	private Point calculateCriclePoint(int centerX, int centerY, int r, int angle) {
		Point point = new Point();
		while (angle < 0)
			angle += 360;
		if (angle >= 360)
			angle = angle % 360;
		int x = 0;
		int y = 0;
		if (angle == 0) {
			x = r;
		} else if (angle == 90) {
			y = r;
		} else if (angle == 180) {
			x = -r;
		} else if (angle == 270) {
			y = -r;
		} else if (angle > 0 && angle < 90) {
			y = (int) (Math.sin(Math.toRadians(angle)) * r);
			x = (int) (Math.cos(Math.toRadians(angle)) * r);
		} else if (angle > 90 && angle < 180) {
			x -= (int) (Math.sin(Math.toRadians(angle - 90)) * r);
			y = (int) (Math.cos(Math.toRadians(angle - 90)) * r);
		} else if (angle > 180 && angle < 270) {
			y -= (int) (Math.sin(Math.toRadians(angle - 180)) * r);
			x -= (int) (Math.cos(Math.toRadians(angle - 180)) * r);
		} else {
			x = (int) (Math.sin(Math.toRadians(angle - 270)) * r);
			y -= (int) (Math.cos(Math.toRadians(angle - 270)) * r);
		}
		point.x = centerX + x;
		point.y = centerY + y;
		return point;
	}

	@Override
	protected boolean onClick(Point click) {
		List<BaseStation> list = new ArrayList<BaseStation>(super.factory.getBaseStationList());
		if (list.isEmpty()) {
			this.clearMarker();
			return false;
		}
		BaseStation selectBaseStation = super.factory.getSelectBaseStation();
		if (this.isSelectStationSector) {
			for (int i = 0; i < this.stationSectorArray.size(); i++) {
				Region region = this.stationSectorArray.valueAt(i);
				if (region.contains(click.x, click.y)) {
					this.isSelectStationSector = false;
					selectBaseStation.detailIndex = this.stationSectorArray.keyAt(i);
					return true;
				}
			}
			this.isSelectStationSector = false;
		}
		this.closeShowPopWindow();
		if (selectBaseStation != null) {
			this.isSelectStationSector = false;
			selectBaseStation.detailIndex = -1;
			super.factory.setSelectBaseStation(null);
			return true;
		}
		this.clearMarker();
		return false;
	}

	/**
	 * 清除覆盖物
	 */
	private void clearMarker() {
		if (this.mMarker != null) {
			this.mMarker.remove();
			this.mMarker = null;
			if (this.mBitMap != null) {
				this.mBitMap.recycle();
				this.mBitMap = null;
			}
		}
	}

	@Override
	protected boolean onLongClick(Point click) {
		this.isSelectStationSector = false;
		List<BaseStation> list = new ArrayList<BaseStation>(super.factory.getBaseStationList());
		if (list.isEmpty())
			return false;
		BaseStation selectBaseStation = super.factory.getSelectBaseStation();
		final Rect rect = new Rect();
		int left, right, top, bottom;
		for (int i = 0; i < list.size(); i++) {
			BaseStation base = list.get(i);
			Point point = super.convertLatlngToPoint(base.latitude, base.longitude);
			if (point.x < 0 || point.y < 0)
				continue;
			left = point.x - overlayRadius;
			top = point.y - overlayRadius;
			right = point.x + overlayRadius;
			bottom = point.y + overlayRadius;
			rect.set(left, top, right, bottom);
			if (rect.contains(click.x, click.y)) {
				super.factory.setSelectBaseStation(base);
				this.isSelectStationSector = true;
				return true;
			}
		}
		if (selectBaseStation != null) {
			this.isSelectStationSector = false;
			selectBaseStation.detailIndex = -1;
			super.factory.setSelectBaseStation(null);
			return true;
		}
		return false;
	}

	/**
	 * 显示选中的基站信息
	 * 
	 * @param base
	 *          基站
	 */
	protected void showBaseStationDetail(BaseStation base) {
		if (window!=null){
			this.window.closePopWindow();
		}
		this.window = new BaseStationDetailPopWindow(this.parent, this.mActivity, base);
		this.window.setOnDeleteListener(new BaseStationDetailPopWindow.OnDeleteListener() {
			@Override
			public void hasDelete() {
				BaseStation selectBaseStation = factory.getSelectBaseStation();
				if (selectBaseStation != null) {
					isSelectStationSector = false;
					selectBaseStation.detailIndex = -1;
					factory.setSelectBaseStation(null);
				}
			}
		});
//		if (this.window.)
		this.window.showPopWindow();
		this.mActivity.setShowPopWindow(true);
	}

	@Override
	public void closeShowPopWindow() {
		if (this.window != null) {
			this.window.closePopWindow();
			this.window = null;
			this.mActivity.setShowPopWindow(false);
		}
	}

	@Override
	public void changeMapType() {
		// 无须实现

	}

	@Override
	public void onDestroy() {
		// 无须实现

	}

	@Override
	public void onResume() {
		// 无须实现

	}

	@Override
	public boolean onMarkerClick(Bundle bundle) {
		int typeId = bundle.getInt("type", -1);
		if (typeId == -1 || !OverlayType.get(typeId).equals(super.mType)) {
			return false;
		}
		List<BaseStation> list = new ArrayList<BaseStation>(super.factory.getBaseStationList());
		if (list.isEmpty()) {
			this.clearMarker();
			return false;
		}
		long id = bundle.getLong("id");
		for (int i = 0; i < list.size(); i++) {
			BaseStation base = list.get(i);
			if (base.id == id) {
				super.factory.setSelectBaseStation(base);
				this.showBaseStationDetail(base);
				return true;
			}
		}
		return false;
	}

}
