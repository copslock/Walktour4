package com.walktour.gui.singlestation.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.walktour.gui.singlestation.dao.SingleStationDaoManager;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * 阈值设置对象
 * Created by wangk on 2017/7/3.
 */
@Entity(nameInDb = "threshold_setting")
public class ThresholdSetting implements Parcelable {
    /**
     * 唯一主键，自增
     */
    @Id(autoincrement = true)
    private Long id;
    /**
     * 测试基站类型, 分室内基站与室外宏站
     */
    @Property(nameInDb = "station_type")
    private int stationType = SingleStationDaoManager.STATION_TYPE_INDOOR;
    /**
     * 测试场景类型
     */
    @Property(nameInDb = "scene_type")
    private int sceneType = SingleStationDaoManager.SCENE_TYPE_COVERAGE;
    /**
     * 测试任务
     */
    @Property(nameInDb = "test_task")
    private String testTask = "";
    /**
     * 阈值key
     */
    @Property(nameInDb = "threshold_key")
    private String thresholdKey;
    /**
     * 判断公式
     */
    @Property(nameInDb = "operator")
    private String operator = "";
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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStationType() {
        return this.stationType;
    }

    public void setStationType(int stationType) {
        this.stationType = stationType;
    }

    public int getSceneType() {
        return this.sceneType;
    }

    public void setSceneType(int sceneType) {
        this.sceneType = sceneType;
    }

    public String getTestTask() {
        return this.testTask;
    }

    public void setTestTask(String testTask) {
        this.testTask = testTask;
    }

    public String getThresholdKey() {
        return this.thresholdKey;
    }

    public void setThresholdKey(String thresholdKey) {
        this.thresholdKey = thresholdKey;
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

    public ThresholdSetting() {
    }

    @Generated(hash = 244388275)
    public ThresholdSetting(Long id, int stationType, int sceneType, String testTask,
                            String thresholdKey, String operator, float thresholdValue,
                            String thresholdUnit) {
        this.id = id;
        this.stationType = stationType;
        this.sceneType = sceneType;
        this.testTask = testTask;
        this.thresholdKey = thresholdKey;
        this.operator = operator;
        this.thresholdValue = thresholdValue;
        this.thresholdUnit = thresholdUnit;
    }

    public String getOperator() {
        return this.operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeInt(this.stationType);
        dest.writeInt(this.sceneType);
        dest.writeString(this.testTask);
        dest.writeString(this.thresholdKey);
        dest.writeString(this.operator);
        dest.writeFloat(this.thresholdValue);
        dest.writeString(this.thresholdUnit);
    }

    protected ThresholdSetting(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.stationType = in.readInt();
        this.sceneType = in.readInt();
        this.testTask = in.readString();
        this.thresholdKey = in.readString();
        this.operator = in.readString();
        this.thresholdValue = in.readFloat();
        this.thresholdUnit = in.readString();
    }

    public static final Creator<ThresholdSetting> CREATOR = new Creator<ThresholdSetting>() {
        @Override
        public ThresholdSetting createFromParcel(Parcel source) {
            return new ThresholdSetting(source);
        }

        @Override
        public ThresholdSetting[] newArray(int size) {
            return new ThresholdSetting[size];
        }
    };
}
