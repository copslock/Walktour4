package com.walktour.gui.newmap.overlay.constant;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.preference.PreferenceManager;

import com.walktour.Utils.TraceInfoInterface;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.layer.BaseMapLayer;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap.overlay.BaseMapOverlay.OverlayType;
import com.walktour.model.LocusParamInfo;
import com.walktour.model.MapEvent;
import com.walktour.model.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * 轨迹点显示图层
 * 
 * @author jianchao.wang
 * 
 */
public class LocasPointOverlay extends BaseConstantOverlay {

	/** 存储设置 */
	private SharedPreferences mPreferences;
	/** 参数设置 */
	private ParameterSetting mParameterSet;
	/** 参数列表 */
	private List<Parameter> parameterms;
	/** 最后一次检测网络参数时间 */
	private long mLastCheckParamsTime = 0;

	@SuppressLint("InflateParams")
	public LocasPointOverlay(BaseMapActivity activity, BaseMapLayer mapLayer) {
		super(activity, mapLayer, "LocasPointOverlay", OverlayType.LocasPoint);
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
		mParameterSet = ParameterSetting.getInstance();
		mParameterSet.initMapLocusShape(mActivity.getApplicationContext());
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeWidth(3);
	}

	/**
	 * 绘制轨迹点
	 * 
	 * @param canvas
	 *          画图
	 * @param mapEvent
	 *          轨迹点
	 * @param point
	 *          屏幕坐标点
	 * @param radius
	 *          半径
	 * @param isNavigation
	 *          是否为导航模式
	 */
	private void drawPoint(Canvas canvas, MapEvent mapEvent, Point point, float radius, boolean isNavigation) {
		BaseStation selectBaseStation = super.factory.getSelectBaseStation();
		// 如果是导航模式,或者当前选择了基站且当前点和基站没有关联
		if ((selectBaseStation != null && !this.isConnectStation(mapEvent)) || isNavigation) {
			mPaint.setColor(Color.GRAY);
			this.drawPointGraph(canvas, point.x, point.y, radius);
		} else {
			if (parameterms == null || parameterms.isEmpty()) {
				mPaint.setColor(this.mParameterSet.getGpsColor());
				this.drawPointGraph(canvas, point.x, point.y, radius);
			} else {
				for (int k = 0; k < parameterms.size(); k++) {
					LocusParamInfo info = mapEvent.getParamInfoMap().get(parameterms.get(k).getShowName());
					int color = (info == null ? Color.GRAY : info.color);
					mPaint.setColor(color);
					this.drawPointGraph(canvas, point.x + k * radius * 3, point.y, radius);
				}
			}
		}
	}

	/**
	 * 绘制指定轨迹点的图形
	 * 
	 * @param canvas
	 *          画布
	 * @param pointX
	 *          轨迹点X坐标
	 * @param pointY
	 *          轨迹点Y坐标
	 * @param radius
	 *          半径
	 */
	private void drawPointGraph(Canvas canvas, float pointX, float pointY, float radius) {
		if (mParameterSet.getLocusShape() == 0) {
			canvas.drawCircle(pointX, pointY, radius, mPaint);
		} else {
			canvas.drawRect(pointX - radius, pointY - radius, pointX + radius, pointY + radius, mPaint);
		}
	}

	/**
	 * 获取轨迹的半径大小<BR>
	 * [功能详细描述]
	 */
	private int getLocusRadius() {
		float radius = 8 * mDensity;
		switch (mParameterSet.getLocusSize()) {
		case 0:
			radius = 12 * mDensity;
			break;
		case 1:
			radius = 8 * mDensity;
			break;
		case 2:
			radius = 4 * mDensity;
			break;
		default:
			break;
		}
		return (int) radius;
	}

	/**
	 * 判断当前参数是否有更改
	 * 
	 * @return
	 */
	private void checkParamerChange() {
		if (this.mLastCheckParamsTime > 0 && System.currentTimeMillis() - this.mLastCheckParamsTime < 3000)
			return;
		List<Parameter> params = mParameterSet
				.getCheckedParamertersByNet(MyPhoneState.getInstance().getCurrentNetForParam(this.mActivity));
		if (params == null || params.size() == 0 || this.parameterms == null || this.parameterms.size() != params.size()) {
			this.parameterms = params;
			return;
		}
		int count = 0;
		for (Parameter param : params) {
			for (Parameter param1 : this.parameterms) {
				if (param.getId().equals(param1.getId())) {
					count++;
				}
			}
		}
		if (params.size() != count) {
			this.parameterms = params;
			return;
		}
		this.mLastCheckParamsTime = System.currentTimeMillis();
	}

	@Override
	protected void getOverlayItems(MyLatLng valueLeftTop, MyLatLng valueRightBottom) {
		super.factory.getLocasList().clear();
		List<MapEvent> locasList = new ArrayList<MapEvent>();
		locasList.addAll(TraceInfoInterface.traceData.getGpsLocas());
		LogUtil.d(TAG, "------getOverlayItems----locasList.size():" + locasList.size() + "----");
		for (int i = 0; i < locasList.size(); i++) {
			MapEvent mapEvent = locasList.get(i);
			if (mapEvent.getAdjustLatitude() == 0 && mapEvent.getAdjustLongitude() == 0) {
				MyLatLng latlng = this.mMapLayer.adjustFromGPS(mapEvent.getLatitude(), mapEvent.getLongitude());
				mapEvent.setAdjustLatitude(latlng.latitude);
				mapEvent.setAdjustLongitude(latlng.longitude);
			}
			if (mapEvent.getAdjustLatitude() > valueLeftTop.latitude
					|| mapEvent.getAdjustLatitude() < valueRightBottom.latitude) {
				locasList.remove(i);
				i--;
				continue;
			}
			if (mapEvent.getAdjustLongitude() < valueLeftTop.longitude
					|| mapEvent.getAdjustLongitude() > valueRightBottom.longitude) {
				locasList.remove(i);
				i--;
				continue;
			}
		}
		LogUtil.d(TAG, "------getOverlayItems----locasList.size():" + locasList.size() + "----");
		super.factory.getLocasList().addAll(locasList);
	}

	@Override
	protected void drawCanvas(Canvas canvas) {
		int radius = getLocusRadius();
		// 绘制轨迹点
		this.checkParamerChange();
		boolean isNavigation = mPreferences.getInt(BaseMapActivity.AUTO_FOLLOW_MODE, 0) == 2;
		List<MapEvent> list = new ArrayList<MapEvent>(super.factory.getLocasList());
		for (int i = 0; i < list.size(); i++) {
			MapEvent mapEvent = list.get(i);
			Point point = super.convertLatlngToPoint(mapEvent.getAdjustLatitude(), mapEvent.getAdjustLongitude());
			mapEvent.setX(point.x);
			mapEvent.setY(point.y);
			if (point.x >= 0 && point.y >= 0)
				drawPoint(canvas, mapEvent, point, radius, isNavigation);
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

	/**
	 * 判断当前的轨迹点是否和选择的基站相关联
	 * 
	 * @param mapEvent
	 *          轨迹点
	 * @return 是否相连
	 */
	private boolean isConnectStation(MapEvent mapEvent) {
		BaseStation selectBaseStation = super.factory.getSelectBaseStation();
		if (selectBaseStation == null)
			return false;
		int index = selectBaseStation.detailIndex;
		if (index == -1)
			return false;
		BaseStationDetail detail = selectBaseStation.details.get(index);
		switch (selectBaseStation.netType) {
		case BaseStation.NETTYPE_GSM:
			if (mapEvent.getStationParamMap().containsKey("BS_BCCH")
					&& mapEvent.getStationParamMap().containsKey("BS_BSIC")) {
				String bcch = mapEvent.getStationParamMap().get("BS_BCCH");
				String bsic = mapEvent.getStationParamMap().get("BS_BSIC");
				if (detail.bcch.equals(bcch) && detail.bsic.equals(bsic))
					return true;
			}
			break;
		case BaseStation.NETTYPE_WCDMA:
			if (mapEvent.getStationParamMap().containsKey("BS_PSC")
					&& mapEvent.getStationParamMap().containsKey("BS_UARFCN")) {
				String psc = mapEvent.getStationParamMap().get("BS_PSC");
				String uarfcn = mapEvent.getStationParamMap().get("BS_UARFCN");
				if (detail.psc.equals(psc) && detail.uarfcn.equals(uarfcn))
					return true;
			}
			break;
		case BaseStation.NETTYPE_CDMA:
			if (mapEvent.getStationParamMap().containsKey("BS_PN") && mapEvent.getStationParamMap().containsKey("BS_FREQ")) {
				String pn = mapEvent.getStationParamMap().get("BS_PN");
				String freq = mapEvent.getStationParamMap().get("BS_FREQ");
				if (detail.pn.equals(pn) && detail.frequency.equals(freq))
					return true;
			}
			break;
		case BaseStation.NETTYPE_TDSCDMA:
			if (mapEvent.getStationParamMap().containsKey("BS_UARFCN")
					&& mapEvent.getStationParamMap().containsKey("BS_CPI")) {
				String uarfcn = mapEvent.getStationParamMap().get("BS_UARFCN");
				String cpi = mapEvent.getStationParamMap().get("BS_CPI");
				if (detail.uarfcn.equals(uarfcn) && detail.cpi.equals(cpi))
					return true;
			}
			break;
		case BaseStation.NETTYPE_LTE:
			if (mapEvent.getStationParamMap().containsKey("BS_PCI")
					&& mapEvent.getStationParamMap().containsKey("BS_EARFCN")) {
				String pci = mapEvent.getStationParamMap().get("BS_PCI");
				String earfcn = mapEvent.getStationParamMap().get("BS_EARFCN");
				if (detail.pci.equals(pci) && detail.earfcn.equals(earfcn))
					return true;
			}
			break;
		}
		return false;
	}

}
