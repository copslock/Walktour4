package com.walktour.gui.newmap2.overlay.gaode;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.TextOptions;
import com.walktour.Utils.DensityUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.WalktourConst.CellLink;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.util.GaodeMapUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date on 2018/6/14
 * @describe 小区连线
 * @author jinfeng.xie
 * @version 1.0.0
 *
 */
public class GdCellLinkOverlayManager extends BaseOverlayManager {
    private static final String TAG = GdCellLinkOverlayManager.class.getSimpleName();
    /**
     * 参数
     */
    private SharedPreferences mPreferences;
    /**
     * 基站距离映射<最近距离的基站参数值，基站对象>
     */
    private Map<String, BaseStationDetail> baseDetailMap = new HashMap<>();
    /**
     * 默认颜色
     */
    protected int defaultColor = 0xFFFF0000;
    /**
     * 像素密度
     */
    protected float mDensity;
    /**
     * 系统缩放比例
     */
    protected float systemScale;

    public GdCellLinkOverlayManager(Context context) {
        super(context);

        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metric);

        this.mDensity = metric.density;
        this.systemScale = metric.densityDpi / 240.f;
        this.mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public OverlayType getOverlayType() {
        return OverlayType.CellLink;
    }

    @Override
    public boolean addOverlay(Object... obj) {
        if (obj.length==0||obj==null){
            return false;
        }
        MyLatLng latLng = (MyLatLng) obj[0];
        if (latLng != null) {
            LatLng ll = GaodeMapUtil.convertToLatLng(latLng);
            calculatePointDistance(ll);
            addCellLineOverlay(ll);
            return true;
        }
        return false;
    }

    private void addCellLineOverlay(LatLng currentLocation) {
        if (currentLocation == null) {
            this.baseDetailMap.clear();
            return;
        }

        for (String key : baseDetailMap.keySet()) {
            BaseStationDetail detail = baseDetailMap.get(key);
            int angle = detail.bearing - 90;
            LatLng stationLoaction = new LatLng(detail.main.latitude, detail.main.longitude);
            int color = this.defaultColor;
            int strokeWidth = 2;
            switch (detail.setType) {
                case BaseStationDetail.SETTYPE_ACTIVESET:
                    if (mPreferences.getBoolean(CellLink.ACTIVE_SET_ENABLE, true)) {
                        color = mPreferences.getInt(CellLink.ACTIVE_SET_COLOR, defaultColor);
                        strokeWidth = (int) (mPreferences.getInt(CellLink.ACTIVE_SET_WIDTH, 2) * mDensity);
                    }
                    break;
                case BaseStationDetail.SETTYPE_MONITORSET:
                    if (mPreferences.getBoolean(CellLink.MONITOR_CANDIDATE_ENABLE, true)) {
                        color = mPreferences.getInt(CellLink.MONITOR_CANDIDATE_COLOR, defaultColor);
                        strokeWidth = (int) (mPreferences.getInt(CellLink.MONITOR_CANDIDATE_WIDTH, 2) * mDensity);
                    }
                    break;
                case BaseStationDetail.SETTYPE_NEIGHBORSET:
                    if (mPreferences.getBoolean(CellLink.NEIGHBOR_ENABLE, true)) {
                        color = mPreferences.getInt(CellLink.NEIGHBOR_COLOR, defaultColor);
                        strokeWidth = (int) (mPreferences.getInt(CellLink.NEIGHBOR_WIDTH, 2) * mDensity);
                    }
                    break;
                case BaseStationDetail.SETTYPE_SERVINGSET:
                    if (mPreferences.getBoolean(CellLink.SERVING_REFERENCE_ENABLE, true)) {
                        color = mPreferences.getInt(CellLink.SERVING_REFERENCE_COLOR, defaultColor);
                        strokeWidth = (int) (mPreferences.getInt(CellLink.SERVING_REFERENCE_WIDTH, 2) * mDensity);
                    }
                    break;

                default:
                    break;
            }
            drawCellLink(detail, currentLocation, stationLoaction, color, strokeWidth, angle);

        }

        baseDetailMap.clear();
    }

    private void drawCellLink(BaseStationDetail detail, LatLng currentLocation, LatLng stationLoaction, int color, int strokeWidth, int angle) {
        // todo angle 连接到具体扇区？
        double lat = stationLoaction.latitude + 0.000001 * Math.sin(((angle)) * Math.PI / 180);
        double lng = stationLoaction.longitude + 0.000001 * Math.cos(((angle)) * Math.PI / 180);
        List<LatLng> lineList = new ArrayList<>();
        lineList.add(currentLocation);
        lineList.add(new LatLng(lat, lng));
        PolylineOptions ooPolyline = new PolylineOptions()
                .width(strokeWidth)
                .color(color)
                .zIndex(9);
        ooPolyline.setPoints(lineList);
        getMapControllor().addPolyline(ooPolyline);

        double centerLat = (currentLocation.latitude + stationLoaction.latitude) / 2;
        double centerLng = (currentLocation.longitude + stationLoaction.longitude) / 2;
        LatLng center = new LatLng(centerLat, centerLng);
        TextOptions textOption = new TextOptions()
                .text(distanceConversion(detail.distance))
                .fontColor(0xFFFF0000)
                .backgroundColor(Color.WHITE)
                .fontSize(DensityUtil.dip2px(mContext, 14))
                .zIndex(9)
                .position(center);

        getMapControllor().addText(textOption);
    }

    private AMap getMapControllor() {
        return (AMap) mMapSdk.getMapControllor();
    }

    protected String distanceConversion(double distance) {
        String ret;
        if (distance > 1000) {
            ret = String.format("%.2fKM", (float) distance / 1000);
        } else {
            ret = String.format("%.2fM", distance);
        }
        return ret;
    }

    @Override
    public boolean clearOverlay() {
        return false;
    }

    /**
     * 计算当前采样点和关联基站的距离
     *
     * @param latlng 当前采样点
     */
    private void calculatePointDistance(LatLng latlng) {
        List<BaseStation> list = new ArrayList<BaseStation>(super.factory.getBaseStationList());
        if (latlng == null)
            return;
        for (int i = 0; i < list.size(); i++) {
            BaseStation base = list.get(i);
            // 计算当前点和所有基站的距离
//            double distance = super.calculateDistance(latlng, new MyLatLng(base.latitude, base.longitude));
            LatLng latlng2 = new LatLng(base.latitude, base.longitude);
            double distance = AMapUtils.calculateLineDistance(latlng,latlng2);
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
     * @param base     当前判断的基站
     * @param distance 基站距离
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
     * @param base     当前判断的基站
     * @param distance 距离
     */
    private void gsmBcchBsic(BaseStation base, double distance) {
        String[] datas = new String[]{getParaValue(UnifyParaID.G_Ser_BCCH), getParaValue(UnifyParaID.G_NCell_N1_BCCH),
                getParaValue(UnifyParaID.G_NCell_N2_BCCH), getParaValue(UnifyParaID.G_NCell_N3_BCCH),
                getParaValue(UnifyParaID.G_NCell_N4_BCCH), getParaValue(UnifyParaID.G_NCell_N5_BCCH),
                getParaValue(UnifyParaID.G_NCell_N6_BCCH), getParaValue(UnifyParaID.G_Ser_BSIC),
                getParaValue(UnifyParaID.G_NCell_N1_BSIC), getParaValue(UnifyParaID.G_NCell_N2_BSIC),
                getParaValue(UnifyParaID.G_NCell_N3_BSIC), getParaValue(UnifyParaID.G_NCell_N4_BSIC),
                getParaValue(UnifyParaID.G_NCell_N5_BSIC), getParaValue(UnifyParaID.G_NCell_N6_BSIC),};
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
     * @param base     当前判断的基站
     * @param distance 基站距离
     */
    private void tdscdmaCPIFreq(BaseStation base, double distance) {
        String[] datas = new String[]{getParaValue(UnifyParaID.TD_Ser_UARFCN),
                getParaValue(UnifyParaID.T_NCell_N1_UARFCN), getParaValue(UnifyParaID.T_NCell_N2_UARFCN),
                getParaValue(UnifyParaID.T_NCell_N3_UARFCN), getParaValue(UnifyParaID.T_NCell_N4_UARFCN),
                getParaValue(UnifyParaID.T_NCell_N5_UARFCN), getParaValue(UnifyParaID.T_NCell_N6_UARFCN),
                getParaValue(UnifyParaID.TD_Ser_CPI), getParaValue(UnifyParaID.T_NCell_N1_CPI),
                getParaValue(UnifyParaID.T_NCell_N2_CPI), getParaValue(UnifyParaID.T_NCell_N3_CPI),
                getParaValue(UnifyParaID.T_NCell_N4_CPI), getParaValue(UnifyParaID.T_NCell_N5_CPI),
                getParaValue(UnifyParaID.T_NCell_N6_CPI),};
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
     * @param base     当前判断的基站
     * @param distance 基站距离
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
     * @param base     当前判断的基站
     * @param distance 基站距离
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
     * @param paraId 参数ID
     * @return
     */
    private String getParaValue(int paraId) {
        return TraceInfoInterface.getParaValue(paraId);
    }

    /**
     * 设置集合映射
     *
     * @param key      关键字
     * @param detail   基站明细对象
     * @param distance 距离
     * @return
     */
    private boolean setBaseSatationDistance(String key, BaseStationDetail detail, double distance) {
        if (baseDetailMap.get(key) != null) {
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
}
