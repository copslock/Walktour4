package com.walktour.gui.singlestation.net.model.survey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi.lin on 2017/10/20.
 * 上传基站勘察结果数据中的基站信息model
 */

public class SiteInfoUploadModel {

    @SerializedName("City")
    private String city;//"珠海"
    @SerializedName("District")
    private String district;//"香洲区"
    @SerializedName("Address")
    private String address;//"珠海定湾七路"
    @SerializedName("SiteName")
    private String siteName;//"站名"
    @SerializedName("SiteConfigure")
    private String siteConfigure;//"基站配置",
    @SerializedName("SiteType")
    private String siteType;//"室外",
    @SerializedName("DeviceType")
    private String deviceType;//"设备类型"
    @SerializedName("TAC")
    private int TAC;//10442
    @SerializedName("eNodeBID")
    private int eNodeBID;//546245,
    @SerializedName("Latitude")
    private double latitude;//22.03886, 纬度
    @SerializedName("Longitude")
    private double longitude;//113.31444 经度

    @Override
    public String toString() {
        return "SiteInfoUploadModel{" +
                "city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", address='" + address + '\'' +
                ", siteName='" + siteName + '\'' +
                ", siteConfigure='" + siteConfigure + '\'' +
                ", siteType='" + siteType + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", TAC=" + TAC +
                ", eNodeBID=" + eNodeBID +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteConfigure() {
        return siteConfigure;
    }

    public void setSiteConfigure(String siteConfigure) {
        this.siteConfigure = siteConfigure;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getTAC() {
        return TAC;
    }

    public void setTAC(int TAC) {
        this.TAC = TAC;
    }

    public int geteNodeBID() {
        return eNodeBID;
    }

    public void seteNodeBID(int eNodeBID) {
        this.eNodeBID = eNodeBID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
