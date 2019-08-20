package com.walktour.gui.singlestation.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * 基站关联的小区信息
 * Created by wangk on 2017/6/23.
 */
@Entity(nameInDb = "cell_info")
public class CellInfo implements Parcelable {
    /**
     * 唯一主键，自增
     */
    @Id(autoincrement = true)
    private Long id;
    /**
     * 基站信息表ID
     */
    @Property(nameInDb = "station_id")
    private Long stationId;
    /**
     * 小区ID
     */
    @Property(nameInDb = "cell_id")
    private int cellId;
    /**
     * 小区名称
     */
    @Property(nameInDb = "cell_name")
    private String cellName = "";
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


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStationId() {
        return this.stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
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

    public CellInfo() {
    }



    public String getCellName() {
        return this.cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.stationId);
        dest.writeInt(this.cellId);
        dest.writeString(this.cellName);
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

    protected CellInfo(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.stationId = (Long) in.readValue(Long.class.getClassLoader());
        this.cellId = in.readInt();
        this.cellName = in.readString();
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

    @Generated(hash = 930086539)
    public CellInfo(Long id, Long stationId, int cellId, String cellName,
            String carrierSetup, int PCI, String band, int frequency, int bandwidth,
            int rootSequence, String subframeMatching,
            String specialSubframeMatching, int RsPower, int PDCCH, int PA, int PB,
            float aerialHigh, float azimuth, float downAngle,
            float electricDownAngle, float machineDownAngle,
            float verticalFalfPowerAngle, float horizontalFalfPowerAngle,
            String aerialVender, String aerialType) {
        this.id = id;
        this.stationId = stationId;
        this.cellId = cellId;
        this.cellName = cellName;
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

    public static final Creator<CellInfo> CREATOR = new Creator<CellInfo>() {
        @Override
        public CellInfo createFromParcel(Parcel source) {
            return new CellInfo(source);
        }

        @Override
        public CellInfo[] newArray(int size) {
            return new CellInfo[size];
        }
    };
}
