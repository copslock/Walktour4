package com.walktour.gui.perceptiontest.surveytask.claiming.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Yi.Lin on 2018/11/19.
 * 勘测任务扇区信息实体类
 */
@Entity(nameInDb = "survey_task_cell")
public class SurveyCell implements Serializable {
    @Transient
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private Long id;

    @Property
    private double longitude;
    @Property
    private double latitude;
    @NotNull
    private Long surveyTaskId;

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

    @Generated(hash = 447446217)
    public SurveyCell(Long id, double longitude, double latitude,
            @NotNull Long surveyTaskId, float azimuth, float downtilt,
            String closeShotImgPath, String farShotImgPath,
            String antennaBackImgPath, String coverDirectionImgPath) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.surveyTaskId = surveyTaskId;
        this.azimuth = azimuth;
        this.downtilt = downtilt;
        this.closeShotImgPath = closeShotImgPath;
        this.farShotImgPath = farShotImgPath;
        this.antennaBackImgPath = antennaBackImgPath;
        this.coverDirectionImgPath = coverDirectionImgPath;
    }

    @Generated(hash = 1799781410)
    public SurveyCell() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getSurveyTaskId() {
        return this.surveyTaskId;
    }

    public void setSurveyTaskId(Long surveyTaskId) {
        this.surveyTaskId = surveyTaskId;
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

    @Override
    public String toString() {
        return "SurveyCell{" +
                "id=" + id +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", surveyTaskId=" + surveyTaskId +
                ", azimuth=" + azimuth +
                ", downtilt=" + downtilt +
                ", closeShotImgPath='" + closeShotImgPath + '\'' +
                ", farShotImgPath='" + farShotImgPath + '\'' +
                ", antennaBackImgPath='" + antennaBackImgPath + '\'' +
                ", coverDirectionImgPath='" + coverDirectionImgPath + '\'' +
                '}';
    }
}
