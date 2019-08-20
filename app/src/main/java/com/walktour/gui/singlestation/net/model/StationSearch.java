package com.walktour.gui.singlestation.net.model;

import com.walktour.base.gui.model.BaseNetModel;

/**
 * 平台返回的查询基站信息
 * Created by wangk on 2017/8/18.
 */

public class StationSearch implements BaseNetModel {
    /**
     * 基站类型：宏站
     */
    public static final int SITE_TYPE_OUTDOOR = 1;
    /**
     * 基站类型：室分
     */
    public static final int SITE_TYPE_INDOOR = 2;
    /**
     * 设备类型
     */
    private String DeviceType;
    /**
     * ENodeBID
     */
    private int ENodeBID;
    /**
     * 纬度
     */
    private double Lat;
    /**
     * 精度
     */
    private double Lon;
    /**
     * 基站地址
     */
    private String SiteAddress;
    /**
     * 基站ID
     */
    private int SiteId;
    /**
     * 基站名称
     */
    private String SiteName;
    /**
     * 基站类型 1：宏站，2：室分
     */
    private int SiteType = SITE_TYPE_OUTDOOR;

    public String getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(String DeviceType) {
        this.DeviceType = DeviceType;
    }

    public int getENodeBID() {
        return ENodeBID;
    }

    public void setENodeBID(int ENodeBID) {
        this.ENodeBID = ENodeBID;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double Lat) {
        this.Lat = Lat;
    }

    public double getLon() {
        return Lon;
    }

    public void setLon(double Lon) {
        this.Lon = Lon;
    }

    public String getSiteAddress() {
        return SiteAddress;
    }

    public void setSiteAddress(String SiteAddress) {
        this.SiteAddress = SiteAddress;
    }

    public int getSiteId() {
        return SiteId;
    }

    public void setSiteId(int SiteId) {
        this.SiteId = SiteId;
    }

    public String getSiteName() {
        return SiteName;
    }

    public void setSiteName(String SiteName) {
        this.SiteName = SiteName;
    }

    public int getSiteType() {
        return SiteType;
    }

    public void setSiteType(int SiteType) {
        this.SiteType = SiteType;
    }
}
