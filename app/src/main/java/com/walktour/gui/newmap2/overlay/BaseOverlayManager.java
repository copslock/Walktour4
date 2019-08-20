package com.walktour.gui.newmap2.overlay;

import android.content.Context;

import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.newmap2.sdk.IMapSdk;

/**
 * 覆盖层基类
 * 必须绑定 sdk 使用
 *
 * @author zhicheng.chen
 * @date 2018/6/5
 * @see #bindSdk(IMapSdk)
 */
public abstract class BaseOverlayManager {

    protected IMapSdk mMapSdk;
    protected Context mContext;
    protected boolean mIsOverlayEnable;
    protected NewMapFactory factory;//地图关联工厂类

    public BaseOverlayManager(Context context) {
        this.mContext = context;
        this.mIsOverlayEnable = true;
        this.factory = NewMapFactory.getInstance();
    }

    /**
     * 绑定sdk
     *
     * @param sdk
     */
    public void bindSdk(IMapSdk sdk) {
        this.mMapSdk = sdk;
    }

    public void onMapStatusChangeStart(Object... obj) {
    }

    public void onMapStatusChange(Object... obj) {
    }

    public void onMapStatusChangeFinish(Object... obj) {
    }

    public void onMapLoaded() {
    }

    /**
     * marker点击
     *
     * @param obj
     * @return
     */
    public boolean onMarkerClick(Object... obj) {
        return false;
    }

    /**
     * 地图点击
     *
     * @param obj
     * @return
     */
    public boolean onMapClick(Object... obj) {
        return false;
    }

    /**
     * 地图长按
     *
     * @param obj
     * @return
     */
    public boolean onMapLongClick(Object... obj) {
        return false;
    }

    /**
     * 获取覆盖物图层类型
     *
     * @return
     */
    public abstract OverlayType getOverlayType();

    public abstract boolean addOverlay(Object... obj);


    public abstract boolean clearOverlay();

    public boolean isEnable() {
        return mIsOverlayEnable;
    }

    public void setEnable(boolean isEnable) {
        this.mIsOverlayEnable = isEnable;
    }

    public void onResume(){

    }


    public void onDestory() {

    }

    public boolean doSthOnBackPressed() {
        return false;
    }
}
