package com.walktour.gui.newmap.overlay.constant;

import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.layer.BaseMapLayer;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap.overlay.BaseMapOverlay;

import java.util.ArrayList;
import java.util.List;

/**
 * 不变的图层，即不随着屏幕的操作而改变的图层，包括基站、轨迹点、告警信息
 * 
 * @author jianchao.wang
 *
 */
public class ConstantMapOverlay extends BaseMapOverlay {
	/** 覆盖物图层对象列表 */
	private List<BaseConstantOverlay> overlayList = new ArrayList<BaseConstantOverlay>();

	public ConstantMapOverlay(BaseMapActivity activity, BaseMapLayer mapLayer) {
		super(activity, mapLayer, "ConstantMapOverlay");
		this.fillOverlays();
	}

	/**
	 * 填充覆盖物图层
	 */
	private void fillOverlays() {
		SceneType scene = ApplicationModel.getInstance().getSelectScene();
		if (scene != null && scene == SceneType.Metro)
			this.overlayList.add(new MetroRouteOverlay(mActivity, mapLayer));
		this.overlayList.add(new BaseStationOverlay(mActivity, mapLayer));
		this.overlayList.add(new LocasPointOverlay(mActivity, mapLayer));
		this.overlayList.add(new AlarmPointOverlay(mActivity, mapLayer));
	}

	@Override
	protected void drawCanvas(Canvas canvas) {
		for (BaseConstantOverlay overlay : this.overlayList) {
			overlay.drawCanvas(canvas);
		}
	}

	@Override
	protected void updateToNewLocation(MyLatLng latlng) {
		if(this.valueLeftTop == null || this.valueRightBottom == null){
			return;
		}
		for (BaseConstantOverlay overlay : this.overlayList) {
			switch (overlay.getType()) {
			case LocasPoint:
			case MetroRoute:
				overlay.getOverlayItems(this.valueLeftTop, this.valueRightBottom);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public boolean onClick(Point click) {
		return false;
	}

	@Override
	public boolean onLongClick(Point click) {
		return false;
	}

	@Override
	protected void getOverlayItems() {
		LogUtil.d(TAG, "---------getOverlayItems------------");
		isGetOverlayItems = true;
		for (BaseConstantOverlay overlay : this.overlayList) {
			overlay.getOverlayItems(this.valueLeftTop, this.valueRightBottom);
		}
		isGetOverlayItems = false;
	}

	@Override
	public void closeShowPopWindow() {
		// 无需实现

	}

	@Override
	public void onDestroy() {
		LogUtil.d(TAG, "-----onDestroy-----");
		for (BaseConstantOverlay overlay : this.overlayList) {
			overlay.onDestroy();
		}
	}

	@Override
	public void onResume() {
		LogUtil.d(TAG, "-----onResume-----");
		for (BaseConstantOverlay overlay : this.overlayList) {
			overlay.onResume();
		}
	}

	@Override
	public boolean onMarkerClick(Bundle bundle) {
		return false;
	}

}
