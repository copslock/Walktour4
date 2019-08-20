package com.walktour.gui.perceptiontest.surveytask.claiming.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Yi.Lin on 2018/11/18.
 * 勘测任务实体类
 */
@Entity(nameInDb = "survey_task")
public class SurveyTask implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    /**
     * STATE_UNCLAIMED:未领取
     * STATE_CLAIMED:已领取（已领取后就是未完成）
     * STATE_UNFINISHED:未完成
     * STATE_FINISHED:已完成（易完成后就是未上传）
     * STATE_UNUPLOADED:未上传
     * STATE_UPLOADED:已上传
     */
    @Transient
    public static final int STATE_UNCLAIMED = 1, STATE_CLAIMED = 2,
            STATE_UNFINISHED = 2, STATE_FINISHED = 3,
            STATE_UNUPLOADED = 3, STATE_UPLOADED = 4;

    /**
     * 是否为展开状态
     */
    @Transient
    private boolean isExpanded;
    /**
     * 是否勾选
     */
    @Transient
    private boolean isChecked;

    /**
     * 勘测任务当前状态
     */
    private int state;

    /**
     * id
     */
    @Id(autoincrement = true)
    private Long id;
    /**
     * 任务类型
     */
    @Property(nameInDb = "task_type")
    private String taskType;

    /**
     * 基站名称
     */
    @Property(nameInDb = "station_name")
    private String stationName;
    /**
     * 任务名称
     */
    @Property(nameInDb = "task_name")
    private String taskName;
    /**
     * 时间限制
     */
    @Property(nameInDb = "time_limit")
    private String timeLimit;
    /**
     * 距离
     */
    private String distance;
    /**
     * 任务编号
     */
    @Property(nameInDb = "task_no")
    private String taskNo;
    /**
     * 任务地点描述
     */
    private String address;
    /**
     * 经度
     */
    private double longitude;
    /**
     * 纬度
     */
    private double latitude;

    /**
     * 方位角
     */
    @Property
    private float azimuth;

    /**
     * 下倾角
     */
    @Property
    private float downtilt;

    /**
     * 近景图
     */
    @Property(nameInDb = "close_shot_img_path")
    private String closeShotImgPath;
    /**
     * 远景图
     */
    @Property(nameInDb = "far_shot_img_path")
    private String farShotImgPath;
    /**
     * 天线背板图
     */
    @Property(nameInDb = "antenna_back_img_path")
    private String antennaBackImgPath;
    /**
     * 覆盖方向
     */
    @Property(nameInDb = "cover_direction_img_path")
    private String coverDirectionImgPath;

    @Override
    public String toString() {
        return "SurveyTask{" +
                "state=" + state +
                ", id=" + id +
                ", taskType='" + taskType + '\'' +
                ", stationName='" + stationName + '\'' +
                ", taskName='" + taskName + '\'' +
                ", timeLimit='" + timeLimit + '\'' +
                ", distance='" + distance + '\'' +
                ", taskNo='" + taskNo + '\'' +
                ", address='" + address + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", azimuth=" + azimuth +
                ", downtilt=" + downtilt +
                ", closeShotImgPath='" + closeShotImgPath + '\'' +
                ", farShotImgPath='" + farShotImgPath + '\'' +
                ", antennaBackImgPath='" + antennaBackImgPath + '\'' +
                ", coverDirectionImgPath='" + coverDirectionImgPath + '\'' +
                '}';
    }


    @Generated(hash = 1016764909)
    public SurveyTask(int state, Long id, String taskType, String stationName, String taskName,
            String timeLimit, String distance, String taskNo, String address, double longitude,
            double latitude, float azimuth, float downtilt, String closeShotImgPath,
            String farShotImgPath, String antennaBackImgPath, String coverDirectionImgPath) {
        this.state = state;
        this.id = id;
        this.taskType = taskType;
        this.stationName = stationName;
        this.taskName = taskName;
        this.timeLimit = timeLimit;
        this.distance = distance;
        this.taskNo = taskNo;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.azimuth = azimuth;
        this.downtilt = downtilt;
        this.closeShotImgPath = closeShotImgPath;
        this.farShotImgPath = farShotImgPath;
        this.antennaBackImgPath = antennaBackImgPath;
        this.coverDirectionImgPath = coverDirectionImgPath;
    }
    @Generated(hash = 462055814)
    public SurveyTask() {
    }
    public int getState() {
        return this.state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTaskType() {
        return this.taskType;
    }
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
    public String getStationName() {
        return this.stationName;
    }
    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
    public String getTaskName() {
        return this.taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public String getTimeLimit() {
        return this.timeLimit;
    }
    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }
    public String getDistance() {
        return this.distance;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }
    public String getTaskNo() {
        return this.taskNo;
    }
    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public double getLongitude() {
        return this.longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return this.latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
    public float getAzimuth() {
        return this.azimuth;
    }
    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }
    public float getDowntilt() {
        return this.downtilt;
    }
    public void setDowntilt(float downtilt) {
        this.downtilt = downtilt;
    }
    public String getCloseShotImgPath() {
        return this.closeShotImgPath;
    }
    public void setCloseShotImgPath(String closeShotImgPath) {
        this.closeShotImgPath = closeShotImgPath;
    }
    public String getFarShotImgPath() {
        return this.farShotImgPath;
    }
    public void setFarShotImgPath(String farShotImgPath) {
        this.farShotImgPath = farShotImgPath;
    }
    public String getAntennaBackImgPath() {
        return this.antennaBackImgPath;
    }
    public void setAntennaBackImgPath(String antennaBackImgPath) {
        this.antennaBackImgPath = antennaBackImgPath;
    }
    public String getCoverDirectionImgPath() {
        return this.coverDirectionImgPath;
    }
    public void setCoverDirectionImgPath(String coverDirectionImgPath) {
        this.coverDirectionImgPath = coverDirectionImgPath;
    }

}


