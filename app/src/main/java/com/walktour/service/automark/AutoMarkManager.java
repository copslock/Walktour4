package com.walktour.service.automark;

import android.graphics.Point;
import android.graphics.PointF;

import com.walktour.gui.map.MapFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 自动打点参数类
 *
 * @author jianchao.wang 2014年8月11日
 */
public class AutoMarkManager {
    /**
     * 建筑物方向角 (正北为0度)
     */
    public static int buildingOrientation = 0;
    /**
     * 建筑物比例尺 (地图1像数：实际多少米)
     */
    public static double buildingScale = 0.01;
    /**
     * 标准大气压 mbar
     */
    public static double standardAtm = 1013.25;
    /**
     * 最小移动步数
     */
    static int minMoveSteps = 1;
    /**
     * 是否显示移动距离
     */
    public static boolean isShowDistance = false;
    /**
     * 日期格式
     */
    private SimpleDateFormat mFormat = new SimpleDateFormat("HHmmssSSS", Locale.getDefault());
    /**
     * 唯一实例
     */
    private static AutoMarkManager sInstance;
    /**
     * 当前是否在执行测试
     */
    private boolean isRunTest = false;
    /**
     * 起始方向角
     */
    private double mFirstOrientation = -1;
    /**
     * 当前相对于起始方向角的偏移角度
     */
    private double mDeflectionDegree;
    /**
     * 当前行走步数
     */
    private int mSteps;
    /**
     * 当前步伐长度
     */
    private double mStepLength;
    /**
     * 当前移动速度
     */
    private double mSpeed;
    /**
     * 上一步所在的实际点的位置(单位米)
     */
    private PointF mLastRealPoint;
    /**
     * 上一次所在的像素点的位置
     */
    private Point mLastPoint;
    /**
     * 当前所在高度
     */
    private int mHeight;
    /**
     * 保存文本的实时采集的加速度值
     */
    private List<String> mWriteList = new ArrayList<>();

    private AutoMarkManager() {

    }

    /**
     * 返回唯一实例
     *
     * @return 唯一实例
     */
    public static AutoMarkManager getInstance() {
        if (sInstance == null) {
            sInstance = new AutoMarkManager();
        }
        return sInstance;
    }

    /**
     * 初始化所有值
     */
    public void init() {
        this.isRunTest = false;
        this.mDeflectionDegree = 0;
        this.mFirstOrientation = 0;
        this.mHeight = 0;
        this.mLastPoint = null;
        this.mLastRealPoint = null;
        this.mSpeed = 0;
        this.mStepLength = 0;
        this.mSteps = 0;
    }

//    public double getFirstOrientation() {
//        return mFirstOrientation;
//    }

    public void setFirstOrientation(double firstOrientation) {
        if (this.isRunTest)
            return;
        mFirstOrientation = firstOrientation;
    }

    public boolean isRunTest() {
        return isRunTest;
    }

    void setRunTest(boolean isRunTest) {
        this.isRunTest = isRunTest;
    }

//    public double getDeflectionDegree() {
//        return mDeflectionDegree;
//    }

    public void setDeflectionDegree(double deflectionDegree) {
        if (!this.isRunTest)
            return;
        mDeflectionDegree = deflectionDegree;
    }

    /**
     * 获取当前方向角
     *
     * @return 方向角
     */
    public double getCurrentOrientation() {
        double degree = this.mFirstOrientation - this.mDeflectionDegree;
        if (degree < 0)
            degree += 360;
        else if (degree > 360)
            degree -= 360;
        return degree;
    }

    /**
     * 新增一步
     *
     * @param stepLength 步伐距离
     */
    public void addNewStep(double stepLength) {
        this.mSteps++;
        this.mStepLength = stepLength;
        this.calculateCoordinate();
    }

    /**
     * 设置起始点
     *
     * @param point 起始点坐标
     */
    void setFirstPoint(Point point) {
        float scale = MapFactory.getMapData().getScale();
        this.mLastPoint = new Point((int) (point.x / scale), (int) (point.y / scale));
        this.mLastRealPoint = new PointF(this.mLastPoint.x * (float) AutoMarkManager.buildingScale,
                this.mLastPoint.y * (float) AutoMarkManager.buildingScale);
    }

    /**
     * 获取界面要显示的点
     *
     * @return 显示的点坐标
     */
     PointF getShowPoint() {
        float scale = MapFactory.getMapData().getScale();
        PointF point = new PointF();
        point.x =  (this.mLastPoint.x * scale);
        point.y =  (this.mLastPoint.y * scale);
        return point;
    }

    /**
     * 计算当前点坐标
     */
    private void calculateCoordinate() {
        double relativeAngle = this.getCurrentOrientation() - AutoMarkManager.buildingOrientation;
        if (relativeAngle < 0)
            relativeAngle += 360;
        this.mLastRealPoint = this.calculateCriclePoint(this.mLastRealPoint, this.mStepLength, relativeAngle);
        int x = (int) (this.mLastRealPoint.x / AutoMarkManager.buildingScale);
        int y = (int) (this.mLastRealPoint.y / AutoMarkManager.buildingScale);
        this.mLastPoint = new Point(x, y);
    }

    /**
     * 计算指定半径的园上的指定角度的坐标
     *
     * @param center 中心点坐标
     * @param r      圆半径
     * @param angle  指定角度
     * @return 坐标
     */
    private PointF calculateCriclePoint(PointF center, double r, double angle) {
        PointF point = new PointF();
        float x = 0;
        float y = 0;
        if (angle > 0 && angle <= 90) {
            x = (float) (Math.sin(Math.toRadians(angle)) * r);
            y -= (float) (Math.cos(Math.toRadians(angle)) * r);
        } else if (angle > 90 && angle <= 180) {
            x = (float) (Math.cos(Math.toRadians(angle - 90)) * r);
            y = (float) (Math.sin(Math.toRadians(angle - 90)) * r);
        } else if (angle > 180 && angle <= 270) {
            x -= (float) (Math.sin(Math.toRadians(angle - 180)) * r);
            y = (float) (Math.cos(Math.toRadians(angle - 180)) * r);
        } else {
            x -= (float) (Math.cos(Math.toRadians(angle - 270)) * r);
            y -= (float) (Math.sin(Math.toRadians(angle - 270)) * r);
        }
        point.x = center.x + x;
        point.y = center.y + y;
        return point;
    }

    public double getSpeed() {
        return mSpeed;
    }

    public void setSpeed(double speed) {
        mSpeed = speed;
    }

    int getSteps() {
        return mSteps;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    Point getLastPoint() {
        return mLastPoint;
    }

    /**
     * 保存采集到的值到文件中
     *
     * @param time   采集时间
     * @param values 采集到的值
     */
    public void saveValuesToFile(long time, double[] values) {
        StringBuilder sb = new StringBuilder();
        sb.append("acceleration").append(",");
        sb.append(this.mFormat.format(new Date(time)));
        for (double value : values) {
            sb.append(",").append(value);
        }
        this.mWriteList.add(sb.toString());
    }

    List<String> getWriteList() {
        return mWriteList;
    }

    public double getStepLength() {
        return mStepLength;
    }

}
