package com.walktour.gui.newmap.overlay.variable;

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
 * 可变的图层，包括基站的点击显示、轨迹点的点击显示、测距显示、基站连线显示
 * 
 * @author jianchao.wang
 *
 */
public class VariableMapOverlay extends BaseMapOverlay {
	/** 覆盖物图层对象列表 */
	private List<BaseVariableOverlay> overlayList = new ArrayList<BaseVariableOverlay>();

	public VariableMapOverlay(BaseMapActivity activity, BaseMapLayer mapLayer) {
		super(activity, mapLayer, "VariableMapOverlay");
		this.fillOverlays();
	}

	/**
	 * 填充覆盖物图层
	 */
	private void fillOverlays() {
		SceneType scene = ApplicationModel.getInstance().getSelectScene();
		if (scene != null && scene == SceneType.Metro)
			this.overlayList.add(new MetroRouteOverlay(mActivity, this, mapLayer));
		this.overlayList.add(new BaseStationOverlay(mActivity, this, mapLayer));
		this.overlayList.add(new LocasPointOverlay(mActivity, this, mapLayer));
		this.overlayList.add(new AlarmPointOverlay(mActivity, this, mapLayer));
		if (!ApplicationModel.getInstance().isGeneralMode())
			this.overlayList.add(new CellLinkOverlay(mActivity, this, mapLayer));
		this.overlayList.add(new RangingLinkOverlay(mActivity, this, mapLayer));
	}

	@Override
	public boolean onClick(Point click) {
		for (int i = this.overlayList.size() - 1; i >= 0; i--) {
			if (this.overlayList.get(i).onClick(click))
				break;
		}
		return true;
	}

	@Override
	public boolean onLongClick(Point click) {
		for (int i = this.overlayList.size() - 1; i >= 0; i--) {
			if (this.overlayList.get(i).onLongClick(click))
				break;
		}
		return true;
	}

	@Override
	protected void drawCanvas(Canvas canvas) {
		for (BaseVariableOverlay overlay : this.overlayList) {
			overlay.drawCanvas(canvas);
		}
	}

	@Override
	protected void updateToNewLocation(MyLatLng latlng) {
		// 无须实现
	}

	@Override
	public void closeShowPopWindow() {
		for (BaseVariableOverlay overlay : this.overlayList) {
			overlay.closeShowPopWindow();
		}
	}

	@Override
	protected void getOverlayItems() {
		// 无需实现

	}

	@Override
	public void onDestroy() {
		LogUtil.d(TAG, "-----onDestroy-----");
		for (BaseVariableOverlay overlay : this.overlayList) {
			overlay.onDestroy();
		}
	}

	@Override
	public void onResume() {
		LogUtil.d(TAG, "-----onResume-----");
		for (BaseVariableOverlay overlay : this.overlayList) {
			overlay.onResume();
		}
	}

	@Override
	public boolean onMarkerClick(Bundle bundle) {
		if (bundle == null)
			return false;
		for (int i = this.overlayList.size() - 1; i >= 0; i--) {
			if (this.overlayList.get(i).onMarkerClick(bundle))
				break;
		}
		return true;
	}

}
