package com.walktour.gui.map;

import android.graphics.Bitmap;

import com.walktour.model.AlarmModel;
import com.walktour.model.HistoryPoint;
import com.walktour.service.automark.glonavin.bean.GlonavinPoint;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 地图的相关数据
 *
 * @author jianchao.wang
 */
public class SavedMapData {
    /**
     * 地图Bitmap对象
     */
    private Bitmap map = null;
    /**
     * 打点坐标堆
     */
    private Stack<PointStatus> pointStatusStack = new Stack<PointStatus>();
    /**
     * 楼梯打点坐标堆
     */
    private Stack<GlonavinPoint> glonavinPointStack = new Stack<GlonavinPoint>();
    /**
     * 事件告警堆
     */
    private Stack<Queue<AlarmModel>> queueStack = new Stack<Queue<AlarmModel>>();
    /**
     * 事件告警队列
     */
    private Queue<AlarmModel> eventQueue = new LinkedBlockingQueue<AlarmModel>();
    /**
     * 历史点队列
     */
    public LinkedList<HistoryPoint> historyList = new LinkedList<HistoryPoint>();
    /**
     * 初始解析图片的缩放比例
     */
    private int mSampleSize = 1;
    /**
     * 地图缩放比例
     */
    private float mScale = 1;
    /**
     * 上次的地图缩放比例
     */
    private float mLastScale = 1;
    /**
     * 缩放等级
     */
    private int mZoomGrade = 20;
    /**
     * 是否处于打点操作
     */
    private boolean isSetPoint = false;
    /**
     * 是否自动打点
     */
    private boolean isAutoMark = false;
    /**
     * 地图的原始宽度
     */
    private int mMapWidth = 0;
    /**
     * 地图的原始高度
     */
    private int mMapHeight = 0;
    /**
     * 地图显示的左上角X坐标
     */
    private float mMapShowX = 0;
    /**
     * 地图显示的左上角Y坐标
     */
    private float mMapShowY = 0;

    /**
     * 比例尺
     */
    private float mPlottingScale = 1;

    /**
     * 是否室内测试打的第一个点
     */
    private boolean mIsIndoorTestFirstPoint;

    public boolean isIndoorTestFirstPoint() {
        return mIsIndoorTestFirstPoint;
    }

    public void setIndoorTestFirstPoint(boolean indoorTestFirstPoint) {
        mIsIndoorTestFirstPoint = indoorTestFirstPoint;
    }

    public Bitmap getMap() {
        return map;
    }

    public void setMap(Bitmap map) {
        if (map == null && this.map != null && !this.map.isRecycled()) {
            this.map.recycle();
            this.map = null;
            System.gc();
        }
        this.map = map;
        if (map == null) {
            this.mMapWidth = 0;
            this.mMapHeight = 0;
        } else {
            this.mMapWidth = map.getWidth();
            this.mMapHeight = map.getHeight();
        }
    }

    public Stack<PointStatus> getPointStatusStack() {
        return pointStatusStack;
    }

    public Stack<GlonavinPoint> getGlonavinPointStack() {
        return glonavinPointStack;
    }

    public void setGlonavinPointStack(Stack<GlonavinPoint> glonavinPointStack) {
        this.glonavinPointStack = glonavinPointStack;
    }

    public Stack<Queue<AlarmModel>> getQueueStack() {
        return queueStack;
    }

    public Queue<AlarmModel> getEventQueue() {
        return eventQueue;
    }

    public float getScale() {
        return mScale;
    }

    public void setScale(float scale) {
        this.mLastScale = this.mScale;
        this.mScale = scale;
    }

    public int getZoomGrade() {
        return mZoomGrade;
    }

    public void setZoomGrade(int zoomGrade) {
        this.mZoomGrade = zoomGrade;
    }

    public LinkedList<HistoryPoint> getHistoryList() {
        return this.historyList;
    }

    public boolean isSetPoint() {
        return isSetPoint;
    }

    public void setSetPoint(boolean isSetPoint) {
        this.isSetPoint = isSetPoint;
    }

    public boolean isAutoMark() {
        return isAutoMark;
    }

    public void setAutoMark(boolean isAutoMark) {
        this.isAutoMark = isAutoMark;
    }

    public float getLastScale() {
        return mLastScale;
    }

    public int getMapWidth() {
        return mMapWidth;
    }

    public void setMapWidth(int mapWidth) {
        mMapWidth = mapWidth;
    }

    public int getMapHeight() {
        return mMapHeight;
    }

    public void setMapHeight(int mapHeight) {
        mMapHeight = mapHeight;
    }

    public float getMapShowX() {
        return mMapShowX;
    }

    public void setMapShowX(float mapShowX) {
        mMapShowX = mapShowX;
    }

    public float getMapShowY() {
        return mMapShowY;
    }

    public void setMapShowY(float mapShowY) {
        mMapShowY = mapShowY;
    }

    public int getSampleSize() {
        return mSampleSize;
    }

    public void setSampleSize(int sampleSize) {
        mSampleSize = sampleSize;
    }

    public float getPlottingScale() {
        return mPlottingScale;
    }

    public void setPlottingScale(float plottingScale) {
        mPlottingScale = plottingScale;
    }

    @Override
    public String toString() {
        return "SavedMapData{" +
                "map=" + map +
                ", pointStatusStack=" + pointStatusStack +
                ", glonavinPointStack=" + glonavinPointStack +
                ", queueStack=" + queueStack +
                ", eventQueue=" + eventQueue +
                ", historyList=" + historyList +
                ", mSampleSize=" + mSampleSize +
                ", mScale=" + mScale +
                ", mLastScale=" + mLastScale +
                ", mZoomGrade=" + mZoomGrade +
                ", isSetPoint=" + isSetPoint +
                ", isAutoMark=" + isAutoMark +
                ", mMapWidth=" + mMapWidth +
                ", mMapHeight=" + mMapHeight +
                ", mMapShowX=" + mMapShowX +
                ", mMapShowY=" + mMapShowY +
                ", mPlottingScale=" + mPlottingScale +
                '}';
    }
}
