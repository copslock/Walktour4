package com.walktour.gui.singlestation.dao.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by yi.lin on 2017/8/24.
 * 现场拍照
 */
@Entity(nameInDb = "survey_photo")
public class SurveyPhoto {

    public static final int PHOTO_TYPE_NORMAL = 0;
    public static final int PHOTO_TYPE_ABNORMAL_REPORT = 1;

    /**
     * 唯一主键，自增
     */
    @Id(autoincrement = true)
    private Long id;

    /**
     * 勘查基站信息表ID
     */
    @Property(nameInDb = "survey_station_id")
    private Long surveyStationId;


    /**
     * 图片描述
     */
    @Property(nameInDb = "photo_desc")
    private String photoDesc;

    /**
     * 图片存放路径
     */
    @Property(nameInDb = "photo_path")
    private String photoPath;

    /**
     * 图片类型
     */
    @Property(nameInDb = "photo_type")
    private int photoType;

    @Generated(hash = 1868667686)
    public SurveyPhoto(Long id, Long surveyStationId, String photoDesc,
            String photoPath, int photoType) {
        this.id = id;
        this.surveyStationId = surveyStationId;
        this.photoDesc = photoDesc;
        this.photoPath = photoPath;
        this.photoType = photoType;
    }

    @Generated(hash = 676724417)
    public SurveyPhoto() {
    }

    public String getPhotoDesc() {
        return photoDesc;
    }

    public void setPhotoDesc(String photoDesc) {
        this.photoDesc = photoDesc;
    }

    @Override
    public String toString() {
        return "SurveyPhoto{" +
                "id=" + id +
                ", surveyStationId=" + surveyStationId +
                ", photoDesc='" + photoDesc + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", photoType=" + photoType +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSurveyStationId() {
        return surveyStationId;
    }

    public void setSurveyStationId(Long surveyStationId) {
        this.surveyStationId = surveyStationId;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getPhotoType() {
        return photoType;
    }

    public void setPhotoType(int photoType) {
        this.photoType = photoType;
    }
}
