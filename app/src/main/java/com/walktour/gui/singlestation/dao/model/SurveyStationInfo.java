package com.walktour.gui.singlestation.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.walktour.Utils.StringUtil;
import com.walktour.greendao.DaoSession;
import com.walktour.greendao.SurveyCellInfoDao;
import com.walktour.greendao.SurveyStationInfoDao;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Transient;

import java.util.List;

/**
 * 勘查基站信息
 * Created by wangk on 2017/7/14.
 */
@Entity(nameInDb = "survey_station")
public class SurveyStationInfo implements Parcelable {
    /**
     * 唯一主键，自增
     */
    @Id(autoincrement = true)
    private Long id;

    /**
     * 是否勘察中
     */
    @Property(nameInDb = "is_editing")
    private boolean isEditing;

    /**
     * 基站信息ID
     */
    @Property(nameInDb = "station_id")
    private Long stationId;
    /**
     * 基站信息对象
     */
    @Transient
    private StationInfo mStationInfo;
    /**
     * 站型(0、室内，1、室外)
     */
    @Property(nameInDb = "type")
    private int type = SingleStationDaoManager.STATION_TYPE_INDOOR;
    /**
     * 区县
     */
    @Property(nameInDb = "district")
    private String district;
    /**
     * 城市
     */
    @Property(nameInDb = "city")
    private String city;
    /**
     * 地址
     */
    @Property(nameInDb = "address")
    private String address = "";
    /**
     * 设备类型
     */
    @Property(nameInDb = "device_type")
    private String deviceType = "";
    /**
     * 配置
     */
    @Property(nameInDb = "configure")
    private String configure = "";
    /**
     * 经度
     */
    @Property(nameInDb = "longitude")
    private double longitude;
    /**
     * 纬度
     */
    @Property(nameInDb = "latitude")
    private double latitude;
    /**
     * TAC
     */
    @Property(nameInDb = "TAC")
    private int TAC;
    /**
     * eNodeBID
     */
    @Property(nameInDb = "eNodeBID")
    private int eNodeBID;
    /**
     * 测试人员
     */
    @Property(nameInDb = "tester")
    private String tester;
    /**
     * 勘查日期
     */
    @Property(nameInDb = "test_date")
    private String testDate;
    /**
     * 测试设备型号
     */
    @Property(nameInDb = "test_device_model")
    private String testDeviceModel;
    /**
     * 测试手机号码
     */
    @Property(nameInDb = "test_phone")
    private String testPhone;
    /**
     * 测试平台
     */
    @Property(nameInDb = "test_platform")
    private String testPlatform;
    /**
     * 实际站高超过理想站高（使用实际站间距查询上表得到）1.5倍
     */
    @Property(nameInDb = "actual_site_higher")
    private boolean isActualSiteHigherOK = true;
    /**
     * 实际站高在理想站高范围内，但若实际下倾小于理想下倾（使用实际站间距查询上表得到） 3度以上，则可与LTE共址（天面），但必须新建独立天线。
     */
    @Property(nameInDb = "actual_site_lower")
    private boolean isActualSiteLowerOK = true;
    /**
     * 天线挂高是否大于50米
     */
    @Property(nameInDb = "antenna_installation_height")
    private boolean isAntennaInstallationHeightOK = true;
    /**
     * 基站间距是否小于100米
     */
    @Property(nameInDb = "spacing_of_sites")
    private boolean isSpacingOfSitesOK = true;

    /**
     * 基站关联的小区列表
     */
    @ToMany(referencedJoinProperty = "surveyStationId")
    private List<SurveyCellInfo> cellInfoList;

    /**
     * 是否已经完成输入所有生成报告必需的参数
     * @return true 完成 false 未完成
     */
    public boolean isCompleteNecessaryParams(){
        return !StringUtil.isNullOrEmpty(getStationInfo().getName())
                && !StringUtil.isNullOrEmpty(String.valueOf(getStationId()))
                && !StringUtil.isNullOrEmpty(getTestDate())
                && !StringUtil.isNullOrEmpty(getTestDeviceModel())
                && !StringUtil.isNullOrEmpty(getTestPhone())
                && !StringUtil.isNullOrEmpty(getTestPlatform());
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDistrict() {
        return this.district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getConfigure() {
        return this.configure;
    }

    public void setConfigure(String configure) {
        this.configure = configure;
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

    public int getTAC() {
        return this.TAC;
    }

    public void setTAC(int TAC) {
        this.TAC = TAC;
    }

    public int getENodeBID() {
        return this.eNodeBID;
    }

    public void setENodeBID(int eNodeBID) {
        this.eNodeBID = eNodeBID;
    }

    public String getTester() {
        return this.tester;
    }

    public void setTester(String tester) {
        this.tester = tester;
    }

    public String getTestDate() {
        return this.testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public String getTestDeviceModel() {
        return this.testDeviceModel;
    }

    public void setTestDeviceModel(String testDeviceModel) {
        this.testDeviceModel = testDeviceModel;
    }

    public String getTestPhone() {
        return this.testPhone;
    }

    public void setTestPhone(String testPhone) {
        this.testPhone = testPhone;
    }

    public String getTestPlatform() {
        return this.testPlatform;
    }

    public void setTestPlatform(String testPlatform) {
        this.testPlatform = testPlatform;
    }

    public StationInfo getStationInfo() {
        return mStationInfo;
    }

    public void setStationInfo(StationInfo stationInfo) {
        mStationInfo = stationInfo;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeByte(this.isEditing ? (byte) 1 : (byte) 0);
        dest.writeValue(this.stationId);
        dest.writeParcelable(this.mStationInfo, flags);
        dest.writeInt(this.type);
        dest.writeString(this.district);
        dest.writeString(this.city);
        dest.writeString(this.address);
        dest.writeString(this.deviceType);
        dest.writeString(this.configure);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeInt(this.TAC);
        dest.writeInt(this.eNodeBID);
        dest.writeString(this.tester);
        dest.writeString(this.testDate);
        dest.writeString(this.testDeviceModel);
        dest.writeString(this.testPhone);
        dest.writeString(this.testPlatform);
        dest.writeByte(this.isActualSiteHigherOK ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isActualSiteLowerOK ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAntennaInstallationHeightOK ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSpacingOfSitesOK ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.cellInfoList);
    }

    public Long getStationId() {
        return this.stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public boolean getIsActualSiteHigherOK() {
        return this.isActualSiteHigherOK;
    }

    public void setIsActualSiteHigherOK(boolean isActualSiteHigherOK) {
        this.isActualSiteHigherOK = isActualSiteHigherOK;
    }

    public boolean getIsActualSiteLowerOK() {
        return this.isActualSiteLowerOK;
    }

    public void setIsActualSiteLowerOK(boolean isActualSiteLowerOK) {
        this.isActualSiteLowerOK = isActualSiteLowerOK;
    }

    public boolean getIsAntennaInstallationHeightOK() {
        return this.isAntennaInstallationHeightOK;
    }

    public void setIsAntennaInstallationHeightOK(boolean isAntennaInstallationHeightOK) {
        this.isAntennaInstallationHeightOK = isAntennaInstallationHeightOK;
    }

    public boolean getIsSpacingOfSitesOK() {
        return this.isSpacingOfSitesOK;
    }

    public void setIsSpacingOfSitesOK(boolean isSpacingOfSitesOK) {
        this.isSpacingOfSitesOK = isSpacingOfSitesOK;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 99960066)
    public List<SurveyCellInfo> getCellInfoList() {
        if (cellInfoList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SurveyCellInfoDao targetDao = daoSession.getSurveyCellInfoDao();
            List<SurveyCellInfo> cellInfoListNew = targetDao._querySurveyStationInfo_CellInfoList(id);
            synchronized (this) {
                if (cellInfoList == null) {
                    cellInfoList = cellInfoListNew;
                }
            }
        }
        return cellInfoList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 923763622)
    public synchronized void resetCellInfoList() {
        cellInfoList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 740026083)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSurveyStationInfoDao() : null;
    }

    public boolean getIsEditing() {
        return this.isEditing;
    }

    public void setIsEditing(boolean isEditing) {
        this.isEditing = isEditing;
    }

    public SurveyStationInfo() {
    }

    protected SurveyStationInfo(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.isEditing = in.readByte() != 0;
        this.stationId = (Long) in.readValue(Long.class.getClassLoader());
        this.mStationInfo = in.readParcelable(StationInfo.class.getClassLoader());
        this.type = in.readInt();
        this.district = in.readString();
        this.city = in.readString();
        this.address = in.readString();
        this.deviceType = in.readString();
        this.configure = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.TAC = in.readInt();
        this.eNodeBID = in.readInt();
        this.tester = in.readString();
        this.testDate = in.readString();
        this.testDeviceModel = in.readString();
        this.testPhone = in.readString();
        this.testPlatform = in.readString();
        this.isActualSiteHigherOK = in.readByte() != 0;
        this.isActualSiteLowerOK = in.readByte() != 0;
        this.isAntennaInstallationHeightOK = in.readByte() != 0;
        this.isSpacingOfSitesOK = in.readByte() != 0;
        this.cellInfoList = in.createTypedArrayList(SurveyCellInfo.CREATOR);
    }

    @Generated(hash = 471851850)
    public SurveyStationInfo(Long id, boolean isEditing, Long stationId, int type, String district, String city,
            String address, String deviceType, String configure, double longitude, double latitude, int TAC, int eNodeBID,
            String tester, String testDate, String testDeviceModel, String testPhone, String testPlatform,
            boolean isActualSiteHigherOK, boolean isActualSiteLowerOK, boolean isAntennaInstallationHeightOK,
            boolean isSpacingOfSitesOK) {
        this.id = id;
        this.isEditing = isEditing;
        this.stationId = stationId;
        this.type = type;
        this.district = district;
        this.city = city;
        this.address = address;
        this.deviceType = deviceType;
        this.configure = configure;
        this.longitude = longitude;
        this.latitude = latitude;
        this.TAC = TAC;
        this.eNodeBID = eNodeBID;
        this.tester = tester;
        this.testDate = testDate;
        this.testDeviceModel = testDeviceModel;
        this.testPhone = testPhone;
        this.testPlatform = testPlatform;
        this.isActualSiteHigherOK = isActualSiteHigherOK;
        this.isActualSiteLowerOK = isActualSiteLowerOK;
        this.isAntennaInstallationHeightOK = isAntennaInstallationHeightOK;
        this.isSpacingOfSitesOK = isSpacingOfSitesOK;
    }

    public static final Creator<SurveyStationInfo> CREATOR = new Creator<SurveyStationInfo>() {
        @Override
        public SurveyStationInfo createFromParcel(Parcel source) {
            return new SurveyStationInfo(source);
        }

        @Override
        public SurveyStationInfo[] newArray(int size) {
            return new SurveyStationInfo[size];
        }
    };
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 2129960390)
    private transient SurveyStationInfoDao myDao;
}
