package com.walktour.gui.newmap.overlay.variable;

import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.Text;
import com.baidu.mapapi.model.LatLng;
import com.walktour.gui.R;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.layer.BaseMapLayer;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap.overlay.BaseMapOverlay.OverlayType;

import java.util.ArrayList;
import java.util.List;

/**
 * 测距连线图层
 * 
 * @author jianchao.wang
 *
 */
public class RangingLinkOverlay extends BaseVariableOverlay {

	/** 测距的经纬度点列表 */
	private List<MyLatLng> mRangingList = new ArrayList<MyLatLng>();
	/** 第一个点对象 */
	private Marker mPoint1;
	/** 第二个点对象 */
	private Marker mPoint2;
	/** 中间连线对象 */
	private Polyline mLine;
	/** 文字显示对象 */
	private Text mText;
	/** 是否打点有变化 */
	private boolean hasChange = false;

	public RangingLinkOverlay(BaseMapActivity activity, View parent, BaseMapLayer mapLayer) {
		super(activity, parent, mapLayer, "RangingLinkOverlay", OverlayType.RangingLink);
	}

	@Override
	protected boolean onClick(Point click) {
		if (super.factory.isRanging()) {
			this.addRangingPoint(super.convertPointToLatlng(click));
			this.hasChange = true;
		} else {
			if (!this.mRangingList.isEmpty()) {
				this.mRangingList.clear();
				this.hasChange = true;
			}
		}
		if (super.factory.isRanging())
			return true;
		return false;
	}

	@Override
	protected boolean onLongClick(Point click) {
		return false;
	}

	/**
	 * 添加测距的经纬度点
	 * 
	 * @param latlng
	 *          测距的屏幕点
	 */
	private void addRangingPoint(MyLatLng latlng) {
		if (this.mRangingList.size() == 2)
			this.mRangingList.remove(0);
		this.mRangingList.add(latlng);
	}

	@Override
	protected void drawCanvas(Canvas canvas) {
		if (!super.factory.isRanging()) {
			if (!this.mRangingList.isEmpty()) {
				this.mRangingList.clear();
				this.hasChange = true;
			}
		}
		if (!this.hasChange)
			return;
		List<Integer> rsIds = new ArrayList<Integer>();
		rsIds.add(R.drawable.iconmarker);
		// 绘制测距点
		if (this.mPoint1 == null) {
			this.mPoint1 = (Marker) super.mMapLayer.drawResourceMarker(this.mRangingList.get(0), rsIds, 0.5f, 1f);
		}
		if (this.mRangingList.size() == 2 && this.mPoint2 == null) {
			this.mPoint2 = (Marker) super.mMapLayer.drawResourceMarker(this.mRangingList.get(1), rsIds, 0.5f, 1f);
		}
		// 绘制测距连线
		if (this.mRangingList.size() == 2) {
			this.mPoint1.setPosition(new LatLng(this.mRangingList.get(0).latitude, this.mRangingList.get(0).longitude));
			this.mPoint1.setVisible(true);
			this.mPoint2.setPosition(new LatLng(this.mRangingList.get(1).latitude, this.mRangingList.get(1).longitude));
			this.mPoint2.setVisible(true);
			if (this.mLine == null) {
				this.mLine = (Polyline) super.mMapLayer.drawLine(this.mRangingList, (int) mPaint.getStrokeWidth(),
						mPaint.getColor());
			} else {
				List<LatLng> list = new ArrayList<LatLng>();
				for (MyLatLng latlng : this.mRangingList) {
					list.add(new LatLng(latlng.latitude, latlng.longitude));
				}
				this.mLine.setPoints(list);
				this.mLine.setVisible(true);
			}
			MyLatLng latlng = new MyLatLng(0, 0);
			latlng.latitude = (this.mRangingList.get(0).latitude + this.mRangingList.get(1).latitude) / 2;
			latlng.longitude = (this.mRangingList.get(0).longitude + this.mRangingList.get(1).longitude) / 2;
			double distance = super.calculateDistance(this.mRangingList.get(0), this.mRangingList.get(1));
			if (this.mText == null) {
				this.mText = (Text) super.mMapLayer.drawText(latlng, distanceConversion(distance), (int) (systemScale * 14),
						super.defaultColor);
			} else {
				this.mText.setText(distanceConversion(distance));
				this.mText.setPosition(new LatLng(latlng.latitude, latlng.longitude));
				this.mText.setVisible(true);
			}
		} else if (this.mRangingList.size() == 1) {
			this.mPoint1.setPosition(new LatLng(this.mRangingList.get(0).latitude, this.mRangingList.get(0).longitude));
			this.mPoint1.setVisible(true);
			if (this.mPoint2 != null)
				this.mPoint2.setVisible(false);
			if (this.mLine != null)
				this.mLine.setVisible(false);
			if (this.mText != null)
				this.mText.setVisible(false);
		} else {
			if (this.mPoint1 != null)
				this.mPoint1.setVisible(false);
			if (this.mPoint2 != null)
				this.mPoint2.setVisible(false);
			if (this.mLine != null)
				this.mLine.setVisible(false);
			if (this.mText != null)
				this.mText.setVisible(false);
		}
		this.hasChange = false;
	}

	@Override
	public void closeShowPopWindow() {
		// 无须实现

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
		return false;
	}

}
