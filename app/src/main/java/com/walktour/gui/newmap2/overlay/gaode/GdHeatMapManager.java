package com.walktour.gui.newmap2.overlay.gaode;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.HeatmapTileProvider;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.TileOverlayOptions;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalktourConst;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.framework.database.BaseStationDBHelper;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.OverlayType;
import com.walktour.gui.newmap2.util.GaodeMapUtil;
import com.walktour.gui.setting.SysMap;

import java.util.ArrayList;
import java.util.List;

/**
 * 热力图
 *
 * @author jinfeng.xie
 * @date 2018/11/18
 */
public class GdHeatMapManager extends BaseOverlayManager {

    private static final int ADD_HEAT_MAP = 1;
    private static final int REMOVE_HEAT_MAP = 2;
    private HeatmapTileProvider heatmap;
    TileOverlayOptions tileOverlayOptions;
    private Handler mH = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ADD_HEAT_MAP) {
                if (heatmap != null) {
                    if (tileOverlayOptions == null) {
                        tileOverlayOptions = new TileOverlayOptions();
                    }
                    tileOverlayOptions.tileProvider(heatmap); // 设置瓦片图层的提供者
                    // 向地图上添加 TileOverlayOptions 类对象
                    getMapControllor().addTileOverlay(tileOverlayOptions);

                }
            } else if (msg.what == REMOVE_HEAT_MAP) {
                clearOverlay();
            }
        }
    };
    private ArrayList<LatLng> datas;

    public GdHeatMapManager(Context context) {
        super(context);
        // set disable
        mIsOverlayEnable = false;
    }

    @Override
    public OverlayType getOverlayType() {
        return OverlayType.HeatMap;
    }

    // if isEnable add heatmap,otherwise hide heatmap ~
    @Override
    public void setEnable(boolean isEnable) {
        if (isEnable) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // get station info from db
                    //read from db
                    List<BaseStation> list = BaseStationDBHelper
                            .getInstance(mContext)
                            .queryBaseStation(getNetTypes(), 0, BaseStation.MAPTYPE_OUTDOOR);

                    datas = new ArrayList<LatLng>();
                    for (BaseStation station : list) {
                        datas.add(GaodeMapUtil.convertToGaode(mContext,
                                new LatLng(station.latitude,station.longitude), CoordinateConverter.CoordType.GPS));
                    }

                    heatmap = new HeatmapTileProvider.Builder().data(datas).build();
                    mH.sendEmptyMessage(ADD_HEAT_MAP);
                }
            }).start();
        } else {
            mH.sendEmptyMessage(REMOVE_HEAT_MAP);
        }
        super.setEnable(isEnable);
    }

    @Override
    public boolean addOverlay(Object... obj) {
        return false;
    }


    @Override
    public boolean clearOverlay() {
        if (tileOverlayOptions != null) {
            datas.clear();
            tileOverlayOptions.notify();
            return true;
        }
        return false;
    }

    private AMap getMapControllor() {
        return (AMap) mMapSdk.getMapControllor();
    }

    /**
     * 获取当前显示的基站的网络类型
     *
     * @return
     */
    private String getNetTypes() {
        StringBuffer netTypeSB = new StringBuffer();
        SharePreferencesUtil util = SharePreferencesUtil.getInstance(mContext.getApplicationContext());
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
        WalkStruct.NetType nettype = state.getCurrentNetType(mContext.getApplicationContext());
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

}
