package com.walktour.gui.newmap2.manager;


import android.content.Context;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.walktour.gui.newmap2.NewMapActivity;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.MapCache;
import com.walktour.gui.newmap2.overlay.baidu.BdAlarmOverlayManager;
import com.walktour.gui.newmap2.overlay.baidu.BdCellLinkOverlayManager;
import com.walktour.gui.newmap2.overlay.baidu.BdGisStationOverlayManager;
import com.walktour.gui.newmap2.overlay.baidu.BdHeatMapManager;
import com.walktour.gui.newmap2.overlay.baidu.BdHsRouteOverlayManager;
import com.walktour.gui.newmap2.overlay.baidu.BdLocasOverlayManager;
import com.walktour.gui.newmap2.overlay.baidu.BdMIFMapOverlayManager;
import com.walktour.gui.newmap2.overlay.baidu.BdMeasureOverlayManager;
import com.walktour.gui.newmap2.overlay.baidu.BdMetroRouteOverlayManager;
import com.walktour.gui.newmap2.overlay.baidu.BdStationOverlayManager;
import com.walktour.gui.newmap2.overlay.gaode.GdAlarmOverlayManager;
import com.walktour.gui.newmap2.overlay.gaode.GdCellLinkOverlayManager;
import com.walktour.gui.newmap2.overlay.gaode.GdHeatMapManager;
import com.walktour.gui.newmap2.overlay.gaode.GdHsRouteOverlayManager;
import com.walktour.gui.newmap2.overlay.gaode.GdLocasOverlayManager;
import com.walktour.gui.newmap2.overlay.gaode.GdMIFMapOverlayManager;
import com.walktour.gui.newmap2.overlay.gaode.GdMeasureOverlayManager;
import com.walktour.gui.newmap2.overlay.gaode.GdMetroRouteOverlayManager;
import com.walktour.gui.newmap2.overlay.gaode.GdStationOverlayManager;
import com.walktour.gui.newmap2.sdk.BaiduMapSdk;
import com.walktour.gui.newmap2.sdk.BingMapSdk;
import com.walktour.gui.newmap2.sdk.GaoDeMapSdk;
import com.walktour.gui.newmap2.sdk.GaoDeNaviMapSdk;
import com.walktour.gui.newmap2.sdk.IMapSdk;

/**
 * 地图管理者
 *
 * @author zhicheng.chen
 * @date 2018/6/5
 */
public class MapManager {

    private IMapSdk mCurrentMap;
    /**
     * 当前地图类型，默认是百度
     */
    private int mCurrentMapType = BAIDU;

    /**
     * 百度地图
     */
    public static final int BAIDU = 0;
    /**
     * 高德地图
     */
    public static final int GAODE = 1;
    /**
     * 谷歌地图
     */
    public static final int GOOGLE = 2;
    /**
     * 必应地图
     */
    public static final int Bing = 3;
    /**
     * 高德导航地图
     */
    public static final int GdNav = 4;

    private BaseOverlayManager stationMgr;// 基站图层管理者
    private BaseOverlayManager gisMgr;// gis基站图层管理者
    private BaseOverlayManager measureMgr;// 测量图层管理者
    private BaseOverlayManager locasMgr;// gps 轨迹打点图层管理者
    private BaseOverlayManager cellLinkMgr;// 小区连线图层管理者
    private BaseOverlayManager metroMgr;// 地铁图层管理者
    private BaseOverlayManager alarmMgr;// 告警图层管理者
    private BaseOverlayManager mifMgr;// MIF地图图层管理者
    private BaseOverlayManager heatMgr;// 热力图图层管理者
    private BaseOverlayManager hsMgr;// 高铁图层管理者


    public void initSdk(Context applicationContext, int type) {
        mCurrentMapType = type;
        switch (type) {
            case BAIDU:
                // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
                // 注意该方法要再setContentView方法之前实现
                SDKInitializer.initialize(applicationContext);
                break;
            case GAODE:
                //高德地图不需要这一步操作
                break;
            case GdNav:
                //高德地图不需要这一步操作
                break;
            case Bing:
                // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
                // 注意该方法要再setContentView方法之前实现
                SDKInitializer.initialize(applicationContext);
                break;
            default:
        }
    }

    /**
     * 获取地图
     *
     * @param type
     * @return
     */
    public IMapSdk getMapByType(Context context, int type, Bundle bundle) {
        mCurrentMapType = type;

        //save current map type in cache
        MapCache.getInstance().setCurrentMapType(mCurrentMapType);

        //是否是 感知测试——>>Gis分析地图
        boolean isFromGis = false;
        if (bundle != null) {
            String from = bundle.getString(NewMapActivity.EXTRA_FROM);
            isFromGis = NewMapActivity.FROM_GIS_ANALYSIS.equals(from);
        }
        switch (type) {
            case BAIDU:
                //1、初始化百度sdk
                mCurrentMap = new BaiduMapSdk(context);

                //2、初始化图层管理
                stationMgr = new BdStationOverlayManager(context);
                measureMgr = new BdMeasureOverlayManager(context);
                locasMgr = new BdLocasOverlayManager(context);
                cellLinkMgr = new BdCellLinkOverlayManager(context);
                metroMgr = new BdMetroRouteOverlayManager(context);
                alarmMgr = new BdAlarmOverlayManager(context);
                mifMgr = new BdMIFMapOverlayManager(context);
                heatMgr = new BdHeatMapManager(context);
                hsMgr=new BdHsRouteOverlayManager(context);
                if (isFromGis) {
                    gisMgr = new BdGisStationOverlayManager(context);
                }

                break;
            case GAODE:
                //1、初始化高德sdk
                mCurrentMap = new GaoDeMapSdk(context, bundle);

                //2、初始化图层管理
                stationMgr = new GdStationOverlayManager(context);
                measureMgr = new GdMeasureOverlayManager(context);
                locasMgr = new GdLocasOverlayManager(context);
                metroMgr = new GdMetroRouteOverlayManager(context);
                cellLinkMgr = new GdCellLinkOverlayManager(context);
                alarmMgr = new GdAlarmOverlayManager(context);
                mifMgr = new GdMIFMapOverlayManager(context);
                heatMgr = new GdHeatMapManager(context);
                hsMgr=new GdHsRouteOverlayManager(context);
                break;
            case GdNav:
                mCurrentMap = new GaoDeNaviMapSdk(context, bundle);

                stationMgr = new GdStationOverlayManager(context);
                measureMgr = new GdMeasureOverlayManager(context);
                locasMgr = new GdLocasOverlayManager(context);
                metroMgr = new GdMetroRouteOverlayManager(context);
                cellLinkMgr = new GdCellLinkOverlayManager(context);
                alarmMgr = new GdAlarmOverlayManager(context);
                mifMgr = new GdMIFMapOverlayManager(context);
                heatMgr = new GdHeatMapManager(context);
                hsMgr=new GdHsRouteOverlayManager(context);
                break;
            case GOOGLE:

                break;
            case Bing:
                mCurrentMap = new BingMapSdk(context);

                stationMgr = new BdStationOverlayManager(context);
                measureMgr = new BdMeasureOverlayManager(context);
                locasMgr = new BdLocasOverlayManager(context);
                cellLinkMgr = new BdCellLinkOverlayManager(context);
                metroMgr = new BdMetroRouteOverlayManager(context);
                alarmMgr = new BdAlarmOverlayManager(context);
                mifMgr = new BdMIFMapOverlayManager(context);
                heatMgr = new BdHeatMapManager(context);
                hsMgr=new BdHsRouteOverlayManager(context);
                break;
            default:
                mCurrentMap = new BaiduMapSdk(context);
        }

        //3、添加图层管理者
        mCurrentMap.addOverlayManager(stationMgr);
        mCurrentMap.addOverlayManager(measureMgr);
        mCurrentMap.addOverlayManager(locasMgr);
        mCurrentMap.addOverlayManager(cellLinkMgr);
        mCurrentMap.addOverlayManager(metroMgr);
        mCurrentMap.addOverlayManager(alarmMgr);
        mCurrentMap.addOverlayManager(mifMgr);
        mCurrentMap.addOverlayManager(heatMgr);
        mCurrentMap.addOverlayManager(hsMgr);

        if (gisMgr != null) {
            mCurrentMap.addOverlayManager(gisMgr);
        }
        return mCurrentMap;
    }

    /**
     * 获取当前地图sdk类型
     *
     * @return
     * @see #GAODE
     * @see #BAIDU
     */
    public int getCurrentMapType() {
        return mCurrentMapType;
    }

    /**
     * 释放资源
     */
    public void destroy() {
        if (mCurrentMap != null) {
            mCurrentMap.onDestroy();
        }
    }
}
