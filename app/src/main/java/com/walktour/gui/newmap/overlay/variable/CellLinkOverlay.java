package com.walktour.gui.newmap.overlay.variable;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.WalktourConst.CellLink;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.layer.BaseMapLayer;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap.overlay.BaseMapOverlay.OverlayType;

import org.andnav.osm.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小区连线图层
 * 
 * @author jianchao.wang
 *
 */
public class CellLinkOverlay extends BaseVariableOverlay {

	/** 参数 */
	private SharedPreferences mPreferences;
	/** 基站距离映射<最近距离的基站参数值，基站对象> */
	private Map<String, BaseStationDetail> baseDetailMap = new HashMap<String, BaseStationDetail>();

	public CellLinkOverlay(BaseMapActivity activity, View parent, BaseMapLayer mapLayer) {
		super(activity, parent, mapLayer, "CellLinkOverlay", OverlayType.CellLink);
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this.mActivity);
	}

	@Override
	protected boolean onClick(Point click) {
		return false;
	}

	@Override
	protected boolean onLongClick(Point click) {
		return false;
	}

	@Override
	protected void drawCanvas(Canvas canvas) {
		GeoPoint point = GpsInfo.getInstance().getLastGeoPoint();
		if (point == null)
			return;
		MyLatLng adjustPoint = this.mMapLayer.adjustFromGPS(point.getLatitude(), point.getLongitude());
		this.calculatePointDistance(adjustPoint);
		this.drawCellLink(canvas, adjustPoint);
	}

	/**
	 * 计算当前采样点和关联基站的距离
	 * 
	 * @param latlng
	 *          当前采样点
	 */
	private void calculatePointDistance(MyLatLng latlng) {
		List<BaseStation> list = new ArrayList<BaseStation>(super.factory.getBaseStationList());
		if (latlng == null)
			return;
		for (int i = 0; i < list.size(); i++) {
			BaseStation base = list.get(i);
			// 计算当前点和所有基站的距离
			double distance = super.calculateDistance(latlng, new MyLatLng(base.latitude, base.longitude));
			switch (base.netType) {
			case BaseStation.NETTYPE_GSM:
				gsmBcchBsic(base, distance);
				break;
			case BaseStation.NETTYPE_WCDMA:
				wcdmaPscUarfcn(base, distance);
				break;
			case BaseStation.NETTYPE_CDMA:
				cdmaPNFreq(base, distance);
				break;
			case BaseStation.NETTYPE_TDSCDMA:
				tdscdmaCPIFreq(base, distance);
				break;
			case BaseStation.NETTYPE_LTE:
				ltePCIEafrcn(base, distance);
				break;
			}
		}
	}

	/**
	 * WCDMA相同频点基站集合
	 * 
	 * @param base
	 *          当前判断的基站
	 * @param distance
	 *          基站距离
	 */
	private void wcdmaPscUarfcn(BaseStation base, double distance) {
		String servingNeighbor[] = getParaValue(UnifyParaID.W_TUMTSCellInfoV2).split(";");
		for (int i = 0; servingNeighbor != null && i < servingNeighbor.length - 1; i++) {
			String[] neighbor = servingNeighbor[i + 1].split(",");
			for (BaseStationDetail detail : base.details) {
				if (detail.psc.equals(neighbor[2]) && detail.uarfcn.equals(neighbor[1])) {
					if (neighbor[0].equals("0")) {
						detail.setType = BaseStationDetail.SETTYPE_ACTIVESET;
					} else if (neighbor[0].equals("1")) {
						detail.setType = BaseStationDetail.SETTYPE_MONITORSET;
					} else /* if(neighbor[0].equals("2")) */ {
						detail.setType = BaseStationDetail.SETTYPE_NEIGHBORSET;
					}
					String key = detail.psc + "_" + detail.uarfcn;
					setBaseSatationDistance(key, detail, distance);
					return;
				}
			}
		}

	}

	/**
	 * GSM相同频点基站集合
	 * 
	 * @param base
	 *          当前判断的基站
	 * @param distance
	 *          距离
	 */
	private void gsmBcchBsic(BaseStation base, double distance) {
		String[] datas = new String[] { getParaValue(UnifyParaID.G_Ser_BCCH), getParaValue(UnifyParaID.G_NCell_N1_BCCH),
				getParaValue(UnifyParaID.G_NCell_N2_BCCH), getParaValue(UnifyParaID.G_NCell_N3_BCCH),
				getParaValue(UnifyParaID.G_NCell_N4_BCCH), getParaValue(UnifyParaID.G_NCell_N5_BCCH),
				getParaValue(UnifyParaID.G_NCell_N6_BCCH), getParaValue(UnifyParaID.G_Ser_BSIC),
				getParaValue(UnifyParaID.G_NCell_N1_BSIC), getParaValue(UnifyParaID.G_NCell_N2_BSIC),
				getParaValue(UnifyParaID.G_NCell_N3_BSIC), getParaValue(UnifyParaID.G_NCell_N4_BSIC),
				getParaValue(UnifyParaID.G_NCell_N5_BSIC), getParaValue(UnifyParaID.G_NCell_N6_BSIC), };
		for (int i = 0, j = 7; i < (datas.length / 2); i++, j++) {
			for (BaseStationDetail detail : base.details) {
				if (detail.bcch.equals(datas[i]) && detail.bsic.equals(datas[j])) {
					if (i == 0) {
						detail.setType = BaseStationDetail.SETTYPE_SERVINGSET;
					} else {
						detail.setType = BaseStationDetail.SETTYPE_NEIGHBORSET;
					}
					String key = detail.bcch + "_" + detail.bsic;
					setBaseSatationDistance(key, detail, distance);
					return;
				}
			}
		}
	}

	/**
	 * TDSCDMA相同频点基站集合
	 * 
	 * @param base
	 *          当前判断的基站
	 * @param distance
	 *          基站距离
	 */
	private void tdscdmaCPIFreq(BaseStation base, double distance) {
		String[] datas = new String[] { getParaValue(UnifyParaID.TD_Ser_UARFCN),
				getParaValue(UnifyParaID.T_NCell_N1_UARFCN), getParaValue(UnifyParaID.T_NCell_N2_UARFCN),
				getParaValue(UnifyParaID.T_NCell_N3_UARFCN), getParaValue(UnifyParaID.T_NCell_N4_UARFCN),
				getParaValue(UnifyParaID.T_NCell_N5_UARFCN), getParaValue(UnifyParaID.T_NCell_N6_UARFCN),
				getParaValue(UnifyParaID.TD_Ser_CPI), getParaValue(UnifyParaID.T_NCell_N1_CPI),
				getParaValue(UnifyParaID.T_NCell_N2_CPI), getParaValue(UnifyParaID.T_NCell_N3_CPI),
				getParaValue(UnifyParaID.T_NCell_N4_CPI), getParaValue(UnifyParaID.T_NCell_N5_CPI),
				getParaValue(UnifyParaID.T_NCell_N6_CPI), };
		for (int i = 0, j = 7; i < (datas.length / 2); i++, j++) {
			for (BaseStationDetail detail : base.details) {
				if (detail.cpi.equals(datas[j]) && detail.uarfcn.equals(datas[i])) {
					if (i == 0) {
						detail.setType = BaseStationDetail.SETTYPE_SERVINGSET;
					} else {
						detail.setType = BaseStationDetail.SETTYPE_NEIGHBORSET;
					}
					String key = detail.cpi + "_" + detail.uarfcn;
					setBaseSatationDistance(key, detail, distance);
					return;
				}
			}
		}
	}

	/**
	 * CDMA相同频点基站集合
	 * 
	 * @param base
	 *          当前判断的基站
	 * @param distance
	 *          基站距离
	 */
	private void cdmaPNFreq(BaseStation base, double distance) {
		String[] servingNeighbor = TraceInfoInterface.getParaValue(UnifyParaID.C_cdmaServingNeighbor).split(";");

		for (int i = 0; i < servingNeighbor.length - 1; i++) {
			String[] neighbor = servingNeighbor[i + 1].split(",");
			for (BaseStationDetail detail : base.details) {
				if (detail.pn.equals(neighbor[2]) && detail.frequency.equals(neighbor[1])) {
					if (neighbor[0].equals("0")) {
						detail.setType = BaseStationDetail.SETTYPE_ACTIVESET;
					} else if (neighbor[0].equals("1")) {
						detail.setType = BaseStationDetail.SETTYPE_MONITORSET;
					} else /* if(neighbor[0].equals("2")) */ {
						detail.setType = BaseStationDetail.SETTYPE_NEIGHBORSET;
					}
					String key = detail.pn + "_" + detail.frequency;
					setBaseSatationDistance(key, detail, distance);
					return;
				}
			}
		}
	}

	/**
	 * LTE相同频点基站集合
	 * 
	 * @param base
	 *          当前判断的基站
	 * @param distance
	 *          基站距离
	 */
	private void ltePCIEafrcn(BaseStation base, double distance) {
		String[] servingNeighbor = getParaValue(UnifyParaID.LTE_CELL_LIST).split(";");
		String sevPCI = getParaValue(UnifyParaID.L_SRV_PCI);
		String sevEARFCN = getParaValue(UnifyParaID.L_SRV_EARFCN);
		for (int i = 0; i < servingNeighbor.length - 1; i++) {
			String[] neighbor = servingNeighbor[i + 1].split(",");
			for (BaseStationDetail detail : base.details) {
				if (detail.pci.equals(neighbor[1]) && detail.earfcn.equals(neighbor[0])) {
					String key = detail.pci + "_" + detail.earfcn;
					detail.setType = BaseStationDetail.SETTYPE_NEIGHBORSET;
					setBaseSatationDistance(key, detail, distance);
					return;
				} else if (detail.pci.equals(sevPCI) && detail.earfcn.equals(sevEARFCN)) {
					String key = detail.pci + "_" + detail.earfcn;
					detail.setType = BaseStationDetail.SETTYPE_SERVINGSET;
					setBaseSatationDistance(key, detail, distance);
					return;
				}
			}
		}
	}

	/**
	 * 获得得参数队列中指定ID的值
	 * 
	 * @param paraId
	 *          参数ID
	 * @return
	 */
	private String getParaValue(int paraId) {
		return TraceInfoInterface.getParaValue(paraId);
	}

	/**
	 * 设置集合映射
	 * 
	 * @param key
	 *          关键字
	 * @param detail
	 *          基站明细对象
	 * @param distance
	 *          距离
	 * @return
	 */
	private boolean setBaseSatationDistance(String key, BaseStationDetail detail, double distance) {
		if (baseDetailMap.get(key) != null) {
			LogUtil.e(TAG, "baseDetailMap distance:" + baseDetailMap.get(key).distance + "____distance:" + distance);
			if (baseDetailMap.get(key).distance > distance) {
				detail.distance = distance;
				baseDetailMap.put(key, detail);
				return true;
			}
			return false;
		}
		detail.distance = distance;
		baseDetailMap.put(key, detail);
		return true;
	}

	/**
	 * 绘制小区连线
	 * 
	 * @param canvas
	 *          画布
	 * @param latlng
	 *          当前点
	 */
	private void drawCellLink(Canvas canvas, MyLatLng latlng) {
		if (latlng == null) {
			this.baseDetailMap.clear();
			return;
		}
		Point currentPoint = super.convertLatlngToPoint(latlng.latitude, latlng.longitude);
		for (String key : baseDetailMap.keySet()) {
			BaseStationDetail detail = baseDetailMap.get(key);
			int angle = detail.bearing - 90;
			Point basePoint = super.convertLatlngToPoint(detail.main.latitude, detail.main.longitude);
			int color = this.defaultColor;
			int strokeWidth = 2;
			switch (detail.setType) {
			case BaseStationDetail.SETTYPE_ACTIVESET:
				if (mPreferences.getBoolean(CellLink.ACTIVE_SET_ENABLE, true)) {
					color = mPreferences.getInt(CellLink.ACTIVE_SET_COLOR, defaultColor);
					strokeWidth = (int) (mPreferences.getInt(CellLink.ACTIVE_SET_WIDTH, 2) * super.mDensity);
				}
				break;
			case BaseStationDetail.SETTYPE_MONITORSET:
				if (mPreferences.getBoolean(CellLink.MONITOR_CANDIDATE_ENABLE, true)) {
					color = mPreferences.getInt(CellLink.MONITOR_CANDIDATE_COLOR, defaultColor);
					strokeWidth = (int) (mPreferences.getInt(CellLink.MONITOR_CANDIDATE_WIDTH, 2) * super.mDensity);
				}
				break;
			case BaseStationDetail.SETTYPE_NEIGHBORSET:
				if (mPreferences.getBoolean(CellLink.NEIGHBOR_ENABLE, true)) {
					color = mPreferences.getInt(CellLink.NEIGHBOR_COLOR, defaultColor);
					strokeWidth = (int) (mPreferences.getInt(CellLink.NEIGHBOR_WIDTH, 2) * super.mDensity);
				}
				break;
			case BaseStationDetail.SETTYPE_SERVINGSET:
				if (mPreferences.getBoolean(CellLink.SERVING_REFERENCE_ENABLE, true)) {
					color = mPreferences.getInt(CellLink.SERVING_REFERENCE_COLOR, defaultColor);
					strokeWidth = (int) (mPreferences.getInt(CellLink.SERVING_REFERENCE_WIDTH, 2) * super.mDensity);
				}
				break;

			default:
				break;
			}
			drawCellLink(canvas, detail, currentPoint, basePoint, color, strokeWidth, angle);

		}
		baseDetailMap.clear();
	}

	/**
	 * 绘制小区连线<BR>
	 * [功能详细描述]
	 * 
	 * @param canvas
	 *          画布
	 * @param detail
	 *          基站对象
	 * @param currentPoint
	 *          当前位置点
	 * @param basePoint
	 *          基站屏幕坐标
	 * @param color
	 *          颜色
	 * @param strokeWidth
	 *          线宽
	 * @param angle
	 *          角度
	 */
	private void drawCellLink(Canvas canvas, BaseStationDetail detail, Point currentPoint, Point basePoint, int color,
			int strokeWidth, int angle) {
		float overLayX = (float) (basePoint.x + overlayRadius * Math.cos(((angle)) * Math.PI / 180));
		float overLayY = (float) (basePoint.y + overlayRadius * Math.sin(((angle)) * Math.PI / 180));
		canvas.drawLine(currentPoint.x, currentPoint.y, overLayX, overLayY, mPaint);
		mPaint.setTextSize(systemScale * 14);
		mPaint.setColor(color);
		mPaint.setStrokeWidth(strokeWidth);
		// 计算两点之间的中间点 公式：x = (x1+x2)/2 y = (y1+y2)/2
		canvas.drawText(distanceConversion(detail.distance), (currentPoint.x + overLayX) / 2,
				(currentPoint.y + overLayY) / 2, mPaint);
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
