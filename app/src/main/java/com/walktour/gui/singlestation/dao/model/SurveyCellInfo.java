package com.walktour.gui.singlestation.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

/**
 * 勘查小区信息
 * Created by wangk on 2017/7/18.
 */

@Entity(nameInDb = "survey_cell")
public class SurveyCellInfo implements Parcelable {
    /**
     * 唯一主键，自增
     */
    @Id(autoincrement = true)
    private Long id;
    /**
     * 基站信息表
     */
    @Property(nameInDb = "survey_station_id")
    private Long surveyStationId;
    /**
     * 小区ID
     */
    @Property(nameInDb = "cell_id")
    private int cellId;
    /**
     * 小区信息ID
     */
    @Property(nameInDb = "cell_info_id")
    private Long cellInfoId;
    /**
     * 基站信息对象
     */
    @Transient
    private CellInfo mCellInfo;
    /**
     * 小区载波配置
     */
    @Property(nameInDb = "carrier_setup")
    private String carrierSetup = "";
    /**
     * PCI
     */
    @Property(nameInDb = "PCI")
    private int PCI;
    /**
     * 频段
     */
    @Property(nameInDb = "band")
    private String band = "";
    /**
     * 主频点
     */
    @Property(nameInDb = "frequency")
    private int frequency;
    /**
     * 小区带宽
     */
    @Property(nameInDb = "bandwidth")
    private int bandwidth;
    /**
     * 根序列
     */
    @Property(nameInDb = "root_sequence")
    private int rootSequence;
    /**
     * 子帧配比
     */
    @Property(nameInDb = "subframe_matching")
    private String subframeMatching = "";
    /**
     * 特殊子帧配比
     */
    @Property(nameInDb = "special_subframe_matching")
    private String specialSubframeMatching = "";
    /**
     * RsPower(dBm)
     */
    @Property(nameInDb = "RsPower")
    private int RsPower;
    /**
     * PDCCH符号数
     */
    @Property(nameInDb = "PDCCH")
    private int PDCCH;
    /**
     * PA
     */
    @Property(nameInDb = "PA")
    private int PA;
    /**
     * PB
     */
    @Property(nameInDb = "PB")
    private int PB;
    /**
     * 天线挂高(米)
     */
    @Property(nameInDb = "aerial_high")
    private float aerialHigh;
    /**
     * 方位角(度)
     */
    @Property(nameInDb = "azimuth")
    private float azimuth;
    /**
     * 总下倾角(度)
     */
    @Property(nameInDb = "down_angle")
    private float downAngle;
    /**
     * 预制电下倾（度）
     */
    @Property(nameInDb = "electric_down_angle")
    private float electricDownAngle;
    /**
     * 机械下倾角（度）
     */
    @Property(nameInDb = "machine_down_angle")
    private float machineDownAngle;
    /**
     * 垂直半功率角（度）
     */
    @Property(nameInDb = "vertical_falf_power_angle")
    private float verticalFalfPowerAngle;
    /**
     * 水平半功率角（度）
     */
    @Property(nameInDb = "horizontal_falf_power_angle")
    private float horizontalFalfPowerAngle;
    /**
     * 天线厂家
     */
    @Property(nameInDb = "aerial_vender")
    private String aerialVender = "";
    /**
     * 天线型号
     */
    @Property(nameInDb = "aerial_type")
    private String aerialType = "";

    @Override
    public String toString() {
        return "SurveyCellInfo{" +
                "id=" + id +
                ", surveyStationId=" + surveyStationId +
                ", cellId=" + cellId +
                ", cellInfoId=" + cellInfoId +
                ", mCellInfo=" + mCellInfo +
                ", carrierSetup='" + carrierSetup + '\'' +
                ", PCI=" + PCI +
                ", band='" + band + '\'' +
                ", frequency=" + frequency +
                ", bandwidth=" + bandwidth +
                ", rootSequence=" + rootSequence +
                ", subframeMatching='" + subframeMatching + '\'' +
                ", specialSubframeMatching='" + specialSubframeMatching + '\'' +
                ", RsPower=" + RsPower +
                ", PDCCH=" + PDCCH +
                ", PA=" + PA +
                ", PB=" + PB +
                ", aerialHigh=" + aerialHigh +
                ", azimuth=" + azimuth +
                ", downAngle=" + downAngle +
                ", electricDownAngle=" + electricDownAngle +
                ", machineDownAngle=" + machineDownAngle +
                ", verticalFalfPowerAngle=" + verticalFalfPowerAngle +
                ", horizontalFalfPowerAngle=" + horizontalFalfPowerAngle +
                ", aerialVender='" + aerialVender + '\'' +
                ", aerialType='" + aerialType + '\'' +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSurveyStationId() {
        return this.surveyStationId;
    }

    public void setSurveyStationId(Long surveyStationId) {
        this.surveyStationId = surveyStationId;
    }

    public int getCellId() {
        return this.cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    public String getCarrierSetup() {
        return this.carrierSetup;
    }

    public void setCarrierSetup(String carrierSetup) {
        this.carrierSetup = carrierSetup;
    }

    public int getPCI() {
        return this.PCI;
    }

    public void setPCI(int PCI) {
        this.PCI = PCI;
    }

    public int getBandwidth() {
        return this.bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public int getRootSequence() {
        return this.rootSequence;
    }

    public void setRootSequence(int rootSequence) {
        this.rootSequence = rootSequence;
    }

    public String getSubframeMatching() {
        return this.subframeMatching;
    }

    public void setSubframeMatching(String subframeMatching) {
        this.subframeMatching = subframeMatching;
    }

    public String getSpecialSubframeMatching() {
        return this.specialSubframeMatching;
    }

    public void setSpecialSubframeMatching(String specialSubframeMatching) {
        this.specialSubframeMatching = specialSubframeMatching;
    }

    public int getRsPower() {
        return this.RsPower;
    }

    public void setRsPower(int RsPower) {
        this.RsPower = RsPower;
    }

    public int getPDCCH() {
        return this.PDCCH;
    }

    public void setPDCCH(int PDCCH) {
        this.PDCCH = PDCCH;
    }

    public int getPA() {
        return this.PA;
    }

    public void setPA(int PA) {
        this.PA = PA;
    }

    public int getPB() {
        return this.PB;
    }

    public void setPB(int PB) {
        this.PB = PB;
    }

    public float getAerialHigh() {
        return this.aerialHigh;
    }

    public void setAerialHigh(float aerialHigh) {
        this.aerialHigh = aerialHigh;
    }

    public float getAzimuth() {
        return this.azimuth;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    public float getDownAngle() {
        return this.downAngle;
    }

    public void setDownAngle(float downAngle) {
        this.downAngle = downAngle;
    }

    public float getElectricDownAngle() {
        return this.electricDownAngle;
    }

    public void setElectricDownAngle(float electricDownAngle) {
        this.electricDownAngle = electricDownAngle;
    }

    public float getMachineDownAngle() {
        return this.machineDownAngle;
    }

    public void setMachineDownAngle(float machineDownAngle) {
        this.machineDownAngle = machineDownAngle;
    }

    public float getVerticalFalfPowerAngle() {
        return this.verticalFalfPowerAngle;
    }

    public void setVerticalFalfPowerAngle(float verticalFalfPowerAngle) {
        this.verticalFalfPowerAngle = verticalFalfPowerAngle;
    }

    public float getHorizontalFalfPowerAngle() {
        return this.horizontalFalfPowerAngle;
    }

    public void setHorizontalFalfPowerAngle(float horizontalFalfPowerAngle) {
        this.horizontalFalfPowerAngle = horizontalFalfPowerAngle;
    }

    public String getAerialVender() {
        return this.aerialVender;
    }

    public void setAerialVender(String aerialVender) {
        this.aerialVender = aerialVender;
    }

    public String getAerialType() {
        return this.aerialType;
    }

    public void setAerialType(String aerialType) {
        this.aerialType = aerialType;
    }

    public Long getCellInfoId() {
        return this.cellInfoId;
    }

    public void setCellInfoId(Long cellInfoId) {
        this.cellInfoId = cellInfoId;
    }

    public SurveyCellInfo() {
    }


    public String getBand() {
        return this.band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public CellInfo getCellInfo() {
        return mCellInfo;
    }

    public void setCellInfo(CellInfo cellInfo) {
        mCellInfo = cellInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.surveyStationId);
        dest.writeInt(this.cellId);
        dest.writeValue(this.cellInfoId);
        dest.writeParcelable(this.mCellInfo, flags);
        dest.writeString(this.carrierSetup);
        dest.writeInt(this.PCI);
        dest.writeString(this.band);
        dest.writeInt(this.frequency);
        dest.writeInt(this.bandwidth);
        dest.writeInt(this.rootSequence);
        dest.writeString(this.subframeMatching);
        dest.writeString(this.specialSubframeMatching);
        dest.writeInt(this.RsPower);
        dest.writeInt(this.PDCCH);
        dest.writeInt(this.PA);
        dest.writeInt(this.PB);
        dest.writeFloat(this.aerialHigh);
        dest.writeFloat(this.azimuth);
        dest.writeFloat(this.downAngle);
        dest.writeFloat(this.electricDownAngle);
        dest.writeFloat(this.machineDownAngle);
        dest.writeFloat(this.verticalFalfPowerAngle);
        dest.writeFloat(this.horizontalFalfPowerAngle);
        dest.writeString(this.aerialVender);
        dest.writeString(this.aerialType);
    }

    protected SurveyCellInfo(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.surveyStationId = (Long) in.readValue(Long.class.getClassLoader());
        this.cellId = in.readInt();
        this.cellInfoId = (Long) in.readValue(Long.class.getClassLoader());
        this.mCellInfo = in.readParcelable(CellInfo.class.getClassLoader());
        this.carrierSetup = in.readString();
        this.PCI = in.readInt();
        this.band = in.readString();
        this.frequency = in.readInt();
        this.bandwidth = in.readInt();
        this.rootSequence = in.readInt();
        this.subframeMatching = in.readString();
        this.specialSubframeMatching = in.readString();
        this.RsPower = in.readInt();
        this.PDCCH = in.readInt();
        this.PA = in.readInt();
        this.PB = in.readInt();
        this.aerialHigh = in.readFloat();
        this.azimuth = in.readFloat();
        this.downAngle = in.readFloat();
        this.electricDownAngle = in.readFloat();
        this.machineDownAngle = in.readFloat();
        this.verticalFalfPowerAngle = in.readFloat();
        this.horizontalFalfPowerAngle = in.readFloat();
        this.aerialVender = in.readString();
        this.aerialType = in.readString();
    }

    @Generated(hash = 865303965)
    public SurveyCellInfo(Long id, Long surveyStationId, int cellId, Long cellInfoId,
            String carrierSetup, int PCI, String band, int frequency, int bandwidth,
            int rootSequence, String subframeMatching, String specialSubframeMatching,
            int RsPower, int PDCCH, int PA, int PB, float aerialHigh, float azimuth,
            float downAngle, float electricDownAngle, float machineDownAngle,
            float verticalFalfPowerAngle, float horizontalFalfPowerAngle, String aerialVender,
            String aerialType) {
        this.id = id;
        this.surveyStationId = surveyStationId;
        this.cellId = cellId;
        this.cellInfoId = cellInfoId;
        this.carrierSetup = carrierSetup;
        this.PCI = PCI;
        this.band = band;
        this.frequency = frequency;
        this.bandwidth = bandwidth;
        this.rootSequence = rootSequence;
        this.subframeMatching = subframeMatching;
        this.specialSubframeMatching = specialSubframeMatching;
        this.RsPower = RsPower;
        this.PDCCH = PDCCH;
        this.PA = PA;
        this.PB = PB;
        this.aerialHigh = aerialHigh;
        this.azimuth = azimuth;
        this.downAngle = downAngle;
        this.electricDownAngle = electricDownAngle;
        this.machineDownAngle = machineDownAngle;
        this.verticalFalfPowerAngle = verticalFalfPowerAngle;
        this.horizontalFalfPowerAngle = horizontalFalfPowerAngle;
        this.aerialVender = aerialVender;
        this.aerialType = aerialType;
    }

    public static final Creator<SurveyCellInfo> CREATOR = new Creator<SurveyCellInfo>() {
        @Override
        public SurveyCellInfo createFromParcel(Parcel source) {
            return new SurveyCellInfo(source);
        }

        @Override
        public SurveyCellInfo[] newArray(int size) {
            return new SurveyCellInfo[size];
        }
    };
}
