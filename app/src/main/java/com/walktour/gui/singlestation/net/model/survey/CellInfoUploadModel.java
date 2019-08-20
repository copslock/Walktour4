package com.walktour.gui.singlestation.net.model.survey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi.lin on 2017/10/20.
 * 上传基站勘察结果数据中的小区信息model
 */

public class CellInfoUploadModel {

    @SerializedName("PA")
    private int PA;// 0
    @SerializedName("PB")
    private int PB;// 0
    @SerializedName("PCI")
    private int PCI;//326,
    @SerializedName("PDCCHSymbols")
    private int PDCCHSymbols;//0
    @SerializedName("RsPower")
    private int RsPower;// 37,
    @SerializedName("AerialHigh")
    private float aerialHigh; //35,
    @SerializedName("AerialType")
    private String aerialType;//"ODS-090R15NT03",
    @SerializedName("AerialVender")
    private String aerialVender;//"京信",
    @SerializedName("Azimuth")
    private float azimuth;//210.0,
    @SerializedName("Band")
    private String band;//"F",
    @SerializedName("Bandwidth")
    private int bandwidth;// 20,
    @SerializedName("CarrierConfig")
    private String carrierConfig;//"1",
    @SerializedName("CellId")
    private int cellId;//6764123,
    @SerializedName("CellName")
    private String cellName;//"珠海定湾七路F-ZLH-1",
    @SerializedName("DownAngle")
    private float downAngle;//6,
    @SerializedName("ElectricDownAngle")
    private float electricDownAngle;// 3,
    @SerializedName("Frequency")
    private int frequency;//38400,
    @SerializedName("HorizontalFalfPowerAngle")
    private float horizontalHalfPowerAngle;//65.0,
    @SerializedName("id")
    private Long id;// 1,
    @SerializedName("MachineDownAngle")
    private float machineDownAngle;//3,
    @SerializedName("RootSequence")
    private int rootSequence;//216,
    @SerializedName("SpecialSubframeMatching")
    private String specialSubframeMatching;//"10:2:2",
    @SerializedName("stationId")
    private Long stationId;// 1,
    @SerializedName("SubframeMatching")
    private String subframeMatching;//"3:1",
    @SerializedName("VerticalFalfPowerAngle")
    private float verticalHalfPowerAngle;// 45,
    @SerializedName("Latitude")
    private double latitude;//22.03886, 纬度
    @SerializedName("Longitude")
    private double longitude;//113.31444 经度

    @Override
    public String toString() {
        return "CellInfoUploadModel{" +
                "PA=" + PA +
                ", PB=" + PB +
                ", PCI=" + PCI +
                ", PDCCHSymbols=" + PDCCHSymbols +
                ", RsPower=" + RsPower +
                ", aerialHigh=" + aerialHigh +
                ", aerialType='" + aerialType + '\'' +
                ", aerialVender='" + aerialVender + '\'' +
                ", azimuth=" + azimuth +
                ", band='" + band + '\'' +
                ", bandwidth=" + bandwidth +
                ", carrierConfig=" + carrierConfig +
                ", cellId=" + cellId +
                ", cellName='" + cellName + '\'' +
                ", downAngle=" + downAngle +
                ", electricDownAngle=" + electricDownAngle +
                ", frequency=" + frequency +
                ", horizontalHalfPowerAngle=" + horizontalHalfPowerAngle +
                ", id=" + id +
                ", machineDownAngle=" + machineDownAngle +
                ", rootSequence=" + rootSequence +
                ", specialSubframeMatching='" + specialSubframeMatching + '\'' +
                ", stationId=" + stationId +
                ", subframeMatching='" + subframeMatching + '\'' +
                ", verticalHalfPowerAngle=" + verticalHalfPowerAngle +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public int getPA() {
        return PA;
    }

    public void setPA(int PA) {
        this.PA = PA;
    }

    public int getPB() {
        return PB;
    }

    public void setPB(int PB) {
        this.PB = PB;
    }

    public int getPCI() {
        return PCI;
    }

    public void setPCI(int PCI) {
        this.PCI = PCI;
    }

    public int getPDCCHSymbols() {
        return PDCCHSymbols;
    }

    public void setPDCCHSymbols(int PDCCHSymbols) {
        this.PDCCHSymbols = PDCCHSymbols;
    }

    public int getRsPower() {
        return RsPower;
    }

    public void setRsPower(int rsPower) {
        RsPower = rsPower;
    }

    public float getAerialHigh() {
        return aerialHigh;
    }

    public void setAerialHigh(float aerialHigh) {
        this.aerialHigh = aerialHigh;
    }

    public String getAerialType() {
        return aerialType;
    }

    public void setAerialType(String aerialType) {
        this.aerialType = aerialType;
    }

    public String getAerialVender() {
        return aerialVender;
    }

    public void setAerialVender(String aerialVender) {
        this.aerialVender = aerialVender;
    }

    public float getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getCarrierConfig() {
        return carrierConfig;
    }

    public void setCarrierConfig(String carrierConfig) {
        this.carrierConfig = carrierConfig;
    }

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    public float getDownAngle() {
        return downAngle;
    }

    public void setDownAngle(float downAngle) {
        this.downAngle = downAngle;
    }

    public float getElectricDownAngle() {
        return electricDownAngle;
    }

    public void setElectricDownAngle(float electricDownAngle) {
        this.electricDownAngle = electricDownAngle;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public float getHorizontalHalfPowerAngle() {
        return horizontalHalfPowerAngle;
    }

    public void setHorizontalHalfPowerAngle(float horizontalHalfPowerAngle) {
        this.horizontalHalfPowerAngle = horizontalHalfPowerAngle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getMachineDownAngle() {
        return machineDownAngle;
    }

    public void setMachineDownAngle(float machineDownAngle) {
        this.machineDownAngle = machineDownAngle;
    }

    public int getRootSequence() {
        return rootSequence;
    }

    public void setRootSequence(int rootSequence) {
        this.rootSequence = rootSequence;
    }

    public String getSpecialSubframeMatching() {
        return specialSubframeMatching;
    }

    public void setSpecialSubframeMatching(String specialSubframeMatching) {
        this.specialSubframeMatching = specialSubframeMatching;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public String getSubframeMatching() {
        return subframeMatching;
    }

    public void setSubframeMatching(String subframeMatching) {
        this.subframeMatching = subframeMatching;
    }

    public float getVerticalHalfPowerAngle() {
        return verticalHalfPowerAngle;
    }

    public void setVerticalHalfPowerAngle(float verticalHalfPowerAngle) {
        this.verticalHalfPowerAngle = verticalHalfPowerAngle;
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
