package com.walktour.gui.newmap2.overlay;

import android.content.Context;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.newmap2.filter.FilterMap;
import com.walktour.gui.newmap2.manager.MapManager;
import com.walktour.gui.newmap2.sdk.IMapSdk;

import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

/**
 * 地图操作相关缓存
 *
 * @author zhicheng.chen
 * @date 2019/3/14
 */
public class MapCache {

    private static final String RECT_VIEW_LT_LATLNG = "rect_view_lt_latlng";
    private static final String RECT_VIEW_RB_LATLNG = "rect_view_rb_latlng";

    private static final String GAODE_RECT_VIEW_LT_LATLNG = "gaode_rect_view_lt_latlng";
    private static final String GAODE_RECT_VIEW_RB_LATLNG = "gaode_rect_view_rb_latlng";

    private int currentMapType = MapManager.BAIDU;

    private MapCache() {

    }

    public static MapCache getInstance() {
        return MapCacheManager.SINGLETON;
    }

    public static class MapCacheManager {
        private static final MapCache SINGLETON = new MapCache();
    }

    public void setCurrentMapType(int currentMapType) {
        this.currentMapType = currentMapType;
    }

    public int getCurrentMapType() {
        return this.currentMapType;
    }

    /**
     * 参数过滤
     */
    private static FilterMap<String, Integer> MAP_PARAM_CACHE = new FilterMap<>();

    /**
     * 框选框过滤
     */
    private static FilterMap<String, String> MAP_RECT_CACHE = new FilterMap<>();

    /**
     * 保存地图框选框的左上角和右下角的经纬度
     *
     * @param p
     */
    public static <T> void saveRectLatLng(T[] p) {
        T lt = p[0];
        T rb = p[1];

        if (MapCache.getInstance().getCurrentMapType() == MapManager.BAIDU) {
            MAP_RECT_CACHE.put(RECT_VIEW_LT_LATLNG, new Gson().toJson(lt));
            MAP_RECT_CACHE.put(RECT_VIEW_RB_LATLNG, new Gson().toJson(rb));
        } else if (MapCache.getInstance().getCurrentMapType() == MapManager.GAODE) {
            MAP_RECT_CACHE.put(GAODE_RECT_VIEW_LT_LATLNG, new Gson().toJson(lt));
            MAP_RECT_CACHE.put(GAODE_RECT_VIEW_RB_LATLNG, new Gson().toJson(rb));
        }
    }

    /**
     * 读取地图框选框的左上角和右下角的经纬度
     *
     * @return
     */
    public static <T> T[] readRectLatLng(T[] t, Class<T> clazz) {
        String ltJson = "", rbJson = "";
        if (MapCache.getInstance().getCurrentMapType() == MapManager.BAIDU) {
            ltJson = MAP_RECT_CACHE.getOrDefault(RECT_VIEW_LT_LATLNG, "");
            rbJson = MAP_RECT_CACHE.getOrDefault(RECT_VIEW_RB_LATLNG, "");
        } else if (MapCache.getInstance().getCurrentMapType() == MapManager.GAODE) {
            ltJson = MAP_RECT_CACHE.getOrDefault(GAODE_RECT_VIEW_LT_LATLNG, "");
            rbJson = MAP_RECT_CACHE.getOrDefault(GAODE_RECT_VIEW_RB_LATLNG, "");
        }

        t[0] = new Gson().fromJson(ltJson, clazz);
        t[1] = new Gson().fromJson(rbJson, clazz);

        return t;
    }


    /**
     * 清除地图框选框的左上角和右下角经纬度
     */
    public static void clearRectLatLng() {
        MAP_RECT_CACHE.clear();
    }

    /**
     * 存储参数
     *
     * @param key
     * @param value
     */
    public static void saveParams(String key, int value) {
        MAP_PARAM_CACHE.put(key, value);
    }

    /**
     * 读取参数如果没有相关key值，返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getParams(String key, int defaultValue) {
        return MAP_PARAM_CACHE.getOrDefault(key, defaultValue);
    }

    public static void clearParamsCache() {
        MAP_PARAM_CACHE.clear();
    }

}
