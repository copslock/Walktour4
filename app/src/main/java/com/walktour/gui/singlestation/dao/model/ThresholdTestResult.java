package com.walktour.gui.singlestation.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * 阈值测试结果
 * Created by wangk on 2017/6/20.
 */
@Entity(nameInDb = "threshold_test_result")
public class ThresholdTestResult implements Parcelable {
    /**
     * 测试状态:未测试
     */
    public static final int TEST_STATUS_INIT = 0;
    /**
     * 测试状态:通过
     */
    public static final int TEST_STATUS_PASS = 1;
    /**
     * 测试状态:未通过
     */
    public static final int TEST_STATUS_FAULT = 2;
    /**
     * 唯一主键，自增
     */
    @Id(autoincrement = true)
    private Long id;
    /**
     * 基站信息表ID
     */
    @Property(nameInDb = "task_test_result_id")
    private Long taskTestResultId;
    /**
     * 阈值key
     */
    @Property(nameInDb = "threshold_key")
    private String thresholdKey;
    /**
     * 阈值名称
     */
    @Property(nameInDb = "threshold_name")
    private String thresholdName;
    /**
     * 判断阈值
     */
    @Property(nameInDb = "threshold_value")
    private float thresholdValue;
    /**
     * 阈值单位
     */
    @Property(nameInDb = "threshold_unit")
    private String thresholdUnit;
    /**
     * 判断公式
     */
    @Property(nameInDb = "operator")
    private String operator = "";
    /**
     * 实际测试值
     */
    @Property(nameInDb = "real_value")
    private float realValue;
    /**
     * 测试状态（0、未测试,1、通过,2、未通过)
     */
    @Property(nameInDb = "test_status")
    private int testStatus = TEST_STATUS_INIT;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskTestResultId() {
        return this.taskTestResultId;
    }

    public void setTaskTestResultId(Long taskTestResultId) {
        this.taskTestResultId = taskTestResultId;
    }

    public String getThresholdKey() {
        return this.thresholdKey;
    }

    public void setThresholdKey(String thresholdKey) {
        this.thresholdKey = thresholdKey;
    }

    public String getThresholdName() {
        return this.thresholdName;
    }

    public void setThresholdName(String thresholdName) {
        this.thresholdName = thresholdName;
    }

    public float getThresholdValue() {
        return this.thresholdValue;
    }

    public void setThresholdValue(float thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public String getThresholdUnit() {
        return this.thresholdUnit;
    }

    public void setThresholdUnit(String thresholdUnit) {
        this.thresholdUnit = thresholdUnit;
    }

    public String getOperator() {
        return this.operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public float getRealValue() {
        return this.realValue;
    }

    public void setRealValue(float realValue) {
        this.realValue = realValue;
    }

    public int getTestStatus() {
        return this.testStatus;
    }

    public void setTestStatus(int testStatus) {
        this.testStatus = testStatus;
    }

    public ThresholdTestResult() {
    }

    @Generated(hash = 812779028)
    public ThresholdTestResult(Long id, Long taskTestResultId, String thresholdKey,
            String thresholdName, float thresholdValue, String thresholdUnit, String operator,
            float realValue, int testStatus) {
        this.id = id;
        this.taskTestResultId = taskTestResultId;
        this.thresholdKey = thresholdKey;
        this.thresholdName = thresholdName;
        this.thresholdValue = thresholdValue;
        this.thresholdUnit = thresholdUnit;
        this.operator = operator;
        this.realValue = realValue;
        this.testStatus = testStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.taskTestResultId);
        dest.writeString(this.thresholdKey);
        dest.writeString(this.thresholdName);
        dest.writeFloat(this.thresholdValue);
        dest.writeString(this.thresholdUnit);
        dest.writeString(this.operator);
        dest.writeFloat(this.realValue);
        dest.writeInt(this.testStatus);
    }

    protected ThresholdTestResult(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.taskTestResultId = (Long) in.readValue(Long.class.getClassLoader());
        this.thresholdKey = in.readString();
        this.thresholdName = in.readString();
        this.thresholdValue = in.readFloat();
        this.thresholdUnit = in.readString();
        this.operator = in.readString();
        this.realValue = in.readFloat();
        this.testStatus = in.readInt();
    }

    public static final Creator<ThresholdTestResult> CREATOR = new Creator<ThresholdTestResult>() {
        @Override
        public ThresholdTestResult createFromParcel(Parcel source) {
            return new ThresholdTestResult(source);
        }

        @Override
        public ThresholdTestResult[] newArray(int size) {
            return new ThresholdTestResult[size];
        }
    };
}
