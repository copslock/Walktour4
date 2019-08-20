package com.walktour.service.automark.glonavin;


import com.walktour.service.automark.glonavin.bean.GlonavinPoint;

/**
 * Created by Yi.Lin on 2018/4/23.
 * <p>
 * 格纳微测试数据存储的单例对象
 */

public class GlonavinDataManager {

    /**
     * 单例对象
     */
    private static GlonavinDataManager sInstance;

    /**
     * 自动打点开始前在地图上选的起始点
     */
    private GlonavinPoint mStartPoint;
    /**
     * 自动打点开始前在地图上选的方向点（与起始点连接成的方向）
     */
    private GlonavinPoint mEndPoint;

    /**
     * 自动打点开始前在地图上选的方向弧度
     */
    private float mInitialAngle;

    /**
     * 记录是否已经开始吐合法点
     */
    private boolean hasStartedBubblingValidPoint = false;

    /**
     * 是否已经在地图界面设置过起始点和方向
     */
    private boolean hasDirectionSet = false;

    /**
     * 是否已经开始打出第一个点了(该变量是为了在开始打点时将地图上的方向线隐藏)
     */
    private boolean hasPointDrew = false;


    public static GlonavinDataManager getInstance() {
        if (null == sInstance) {
            synchronized (GlonavinDataManager.class) {
                if (null == sInstance) {
                    sInstance = new GlonavinDataManager();
                }
            }
        }
        return sInstance;
    }


    @Override
    public String toString() {
        return "GlonavinDataManager{" +
                "mStartPoint=" + mStartPoint +
                ", mEndPoint=" + mEndPoint +
                ", mInitialAngle=" + mInitialAngle +
                ", hasStartedBubblingValidPoint=" + hasStartedBubblingValidPoint +
                ", hasDirectionSet=" + hasDirectionSet +
                ", hasPointDrew=" + hasPointDrew +
                '}';
    }

    public GlonavinPoint getStartPoint() {
        return mStartPoint;
    }

    public void setStartPoint(GlonavinPoint startPoint) {
        mStartPoint = startPoint;
    }

    public GlonavinPoint getEndPoint() {
        return mEndPoint;
    }

    public void setEndPoint(GlonavinPoint endPoint) {
        mEndPoint = endPoint;
    }

    public boolean isHasStartedBubblingValidPoint() {
        return hasStartedBubblingValidPoint;
    }

    public void setHasStartedBubblingValidPoint(boolean hasStartedBubblingValidPoint) {
        this.hasStartedBubblingValidPoint = hasStartedBubblingValidPoint;
    }

    public boolean isHasDirectionSet() {
        return hasDirectionSet;
    }

    public void setHasDirectionSet(boolean hasDirectionSet) {
        this.hasDirectionSet = hasDirectionSet;
    }

    public boolean isHasPointDrew() {
        return hasPointDrew;
    }

    public void setHasPointDrew(boolean hasPointDrew) {
        this.hasPointDrew = hasPointDrew;
    }

    /**
     * 计算画出的线方向弧度
     * @return
     */
    public float getInitialAngle() {
        if (null == getStartPoint() || null == getEndPoint()) {
            return mInitialAngle;
        }
        float dx = getEndPoint().getX() - getStartPoint().getX();
        float dy = getEndPoint().getY() - getStartPoint().getY();
        double result = Math.atan(dy / dx);
        if (dx > 0 && dy > 0) {
            //Ⅳ
            result = result - Math.PI;
        } else if (dx < 0 && dy > 0) {
            //Ⅲ
            result = result;
        } else if (dx > 0 && dy < 0) {
            //Ⅰ
            result = Math.PI + result;
        } else {
            //Ⅱ
            result = 1 / 2 * Math.PI + result;
        }
        mInitialAngle = (float) result;
        return mInitialAngle;
    }


    /**
     * 清除起始
     */
    public void clearPoint() {
        this.mStartPoint = null;
        this.mEndPoint = null;
    }

    /**
     * 还原对象数据
     */
    public void reset() {
        this.clearPoint();
        this.mInitialAngle = 0;
        this.hasStartedBubblingValidPoint = false;
        this.hasDirectionSet = false;
        this.hasPointDrew = false;
    }
}
