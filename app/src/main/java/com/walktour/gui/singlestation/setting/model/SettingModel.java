package com.walktour.gui.singlestation.setting.model;

import com.walktour.gui.singlestation.dao.SingleStationDaoManager;

/**
 * 阈值设置对象
 * Created by luojun on 2017/7/3.
 */

public class SettingModel {
    /**
     * 测试基站类型, 分室内基站与室外宏站
     */
    private int mStationType = SingleStationDaoManager.STATION_TYPE_INDOOR;
    /**
     * 测试场景类型
     */
    private int mSceneType = SingleStationDaoManager.SCENE_TYPE_COVERAGE;
    /**
     * 测试任务
     */
    private String mTestTask = "";
    /**
     * 阈值指标
     */
    private String mThresholdKey = "";
    /**
     * 阈值判断标准
     */
    private String mOperator = "";
    /**
     * 阈值指标值
     */
    private float mThresholdValue = 0.0f;
    /**
     * 阈值指标单位
     */
    private String mThresholdUnit = "";

    public float getThresholdValue() {
        return mThresholdValue;
    }

    public void setThresholdValue(float thresholdValue) {
        mThresholdValue = thresholdValue;
    }

    public String getThresholdUnit() {
        return mThresholdUnit;
    }

    public void setThresholdUnit(String thresholdUnit) {
        mThresholdUnit = thresholdUnit;
    }

    public String getTestTask() {
        return mTestTask;
    }

    public void setTestTask(String testTask) {
        mTestTask = testTask;
    }

    public String getThresholdKey() {
        return mThresholdKey;
    }

    public void setThresholdKey(String thresholdKey) {
        this.mThresholdKey = thresholdKey;
    }

    public String getOperator() {
        return mOperator;
    }

    public void setOperator(String operator) {
        mOperator = operator;
    }

    public int getStationType() {
        return mStationType;
    }

    public void setStationType(int stationType) {
        mStationType = stationType;
    }

    public int getSceneType() {
        return mSceneType;
    }

    public void setSceneType(int sceneType) {
        mSceneType = sceneType;
    }
}
