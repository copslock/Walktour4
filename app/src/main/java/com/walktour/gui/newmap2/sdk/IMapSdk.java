package com.walktour.gui.newmap2.sdk;

import android.graphics.Point;
import android.view.View;

import com.amap.api.navi.model.NaviLatLng;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.gui.newmap2.overlay.BaseOverlayManager;
import com.walktour.gui.newmap2.overlay.OverlayType;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图sdk统一接口
 *
 * @author zhicheng.chen
 * @date 2018/6/5
 */
public interface IMapSdk {

    /**
     * 获取地图管理者
     *
     * @return
     */
    Object getMapControllor();

    /**
     * 获取地图布局id
     *
     * @return
     */
    int getLayoutId();

    /**
     * 获取地图view
     *
     * @return
     */
    View getMapView();

    /**
     * 获取地图名称
     *
     * @return
     */
    String getMapName();

    /**
     * 切换地图类型：2d、3d、卫星，取值：
     */
    void switchMapByType(int type);

    /**
     * 请求定位
     */
    void requestLocation(MapCallBack<String> callBack);

    /**
     * 设置地图当前中心(传入GPS的经纬度)
     */
    void setLocation(double latitude, double longitude);

    /**
     * 添加图层管理者
     *
     * @param overlay
     */
    void addOverlayManager(BaseOverlayManager overlay);


    /**
     * 移除图层管理者
     *
     * @param overlay
     */
    void removeOverlayManager(BaseOverlayManager overlay);


    /**
     * 获取指定图层管理者
     *
     * @param overlayType
     */
    BaseOverlayManager getOverlayManager(OverlayType overlayType);

    /**
     * 移除图层
     *
     * @param overlayType
     * @return
     */
    boolean removeOverlay(OverlayType overlayType);

    /**
     * 清除全部图层
     */
    boolean clearOverlay();

    /**
     * 截图
     *
     * @return
     */
    void getSnapShot(MapCallBack callBack);

    /**
     * 测距
     *
     * @return
     */
    double measureDistance(boolean isMeaseureMode);

    /**
     * 显示热力图
     *
     * @param isShow
     */
    void showHeatMap(boolean isShow);

    void setZoomLevel(float level);

    float getZoomLevel();

    void onResume();

    void onPause();

    void onDestroy();
    void release();
    /**
     * 加载离线地图
     */
    void loadOfflineMap();

    String getLocationInfo();

    /**
     * 获取指定区域的基站
     *
     * @param lt 左上角的屏幕点
     * @param rb 右下角的屏幕点
     * @return
     */
    List<BaseStation> getStationInBounds(Point lt, Point rb);

    /**
     * 聚焦最后定位的点
     */
    void focuLastLatlng();
    /**
     * 地图回调方法
     *
     * @param <T>
     */
    interface MapCallBack<T> {

        void result(T... t);
    }

    boolean doSthOnBackPressed();
    /**
     * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
     *  @startList 开始的坐标点，可以多个
     *   @endList 结束的坐标点，可以多个
     * @mWayPointList 途经点的坐标点，可以多个
     * @congestion 躲避拥堵
     * @avoidhightspeed 不走高速
     * @cost 避免收费
     * @hightspeed 高速优先
     * @multipleroute 多路径
     *
     *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
     *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
     */
    boolean calculateDriveRoute(List<MyLatLng> startList, List<MyLatLng> endList, List<MyLatLng> mWayPointList,
                                boolean congestion,boolean avoidhightspeed,boolean cost,boolean hightspeed,boolean multipleroute,boolean isGaodeLatlon);
}
