package com.walktour.gui.singlestation.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.walktour.base.util.LogUtil;
import com.walktour.greendao.CellInfoDao;
import com.walktour.greendao.DaoSession;
import com.walktour.greendao.SceneInfoDao;
import com.walktour.greendao.StationInfoDao;
import com.walktour.greendao.StationInfoReportDao;
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
 * 基站信息表
 * Created by wangk on 2017/6/15.
 */
@Entity(nameInDb = "station_info")
public class StationInfo implements Parcelable, Comparable<StationInfo> {
    /**
     * 基站来源：本地导入
     */
    public static final int FROM_TYPE_IMPORT = 0;
    /**
     * 基站来源：平台下载
     */
    public static final int FROM_TYPE_PLATFORM = 1;
    /**
     * 测试状态：未测试
     */
    public static final int TEST_STATUS_INIT = 0;
    /**
     * 测试状态：测试中
     */
    public static final int TEST_STATUS_TESTING = 1;
    /**
     * 测试状态：处理中
     */
    public static final int TEST_STATUS_DEAL = 2;
    /**
     * 测试状态：测试失败
     */
    public static final int TEST_STATUS_FAULT = 3;
    /**
     * 测试状态：测试成功
     */
    public static final int TEST_STATUS_SUCCESS = 4;
    /**
     * 是否已经导出测试报告,1为是
     */
    public static final int EXPORTED_REPORT_YES = 1;
    /**
     * 是否已经导出测试报告,0为否
     */
    public static final int EXPORTED_REPORT_NO = 0;
    /**
     * 唯一主键，自增
     */
    @Id(autoincrement = true)
    private Long id;
    /**
     * 平台获取的基站ID
     */
    @Property(nameInDb = "site_id")
    private int siteId;
    /**
     * 基站来源(0、本地导入，1、平台下载)
     */
    @Property(nameInDb = "from_type")
    private int fromType = FROM_TYPE_IMPORT;
    /**
     * 站名
     */
    @Property(nameInDb = "name")
    private String name = "";
    /**
     * 站号
     */
    @Property(nameInDb = "code")
    private String code = "";
    /**
     * 站型(0、室内，1、室外)
     */
    @Property(nameInDb = "type")
    private int type = SingleStationDaoManager.STATION_TYPE_INDOOR;
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
     * 测试状态(0、未测试，1、测试未通过，2、已测试未上传，3、测试通过)
     */
    @Property(nameInDb = "test_status")
    private int testStatus = TEST_STATUS_INIT;

    /***
     * 是否已导出测试报告 0为否,1为是
     */
    @Property(nameInDb = "is_exported_report")
    private int isExportedReport = EXPORTED_REPORT_NO;

    /**
     * 基站和当前位置的距离
     */
    @Transient
    private String mDistance = "";
    /**
     * 基站关联的小区列表
     */
    @ToMany(referencedJoinProperty = "stationId")
    private List<CellInfo> cellInfoList;
    /**
     * 基站关联的场景列表
     */
    @ToMany(referencedJoinProperty = "stationId")
    private List<SceneInfo> sceneInfoList;

    /**
     * 基站关联的报告列表,一个基站最多2个报告,一个远程报告,一个本地报告
     */
    @ToMany(referencedJoinProperty = "stationId")
    private List<StationInfoReport> reportList;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getTestStatus() {
        return this.testStatus;
    }

    public void setTestStatus(int testStatus) {
        this.testStatus = testStatus;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    public int getIsExportedReport() {
        return this.isExportedReport;
    }

    public void setIsExportedReport(int isExportedReport) {
        this.isExportedReport = isExportedReport;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 2137324302)
    public List<CellInfo> getCellInfoList() {
        if (cellInfoList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CellInfoDao targetDao = daoSession.getCellInfoDao();
            List<CellInfo> cellInfoListNew = targetDao
                    ._queryStationInfo_CellInfoList(id);
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
    @Generated(hash = 1996410038)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getStationInfoDao() : null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1384912580)
    public List<StationInfoReport> getReportList() {
        if (reportList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StationInfoReportDao targetDao = daoSession.getStationInfoReportDao();
            List<StationInfoReport> reportListNew = targetDao._queryStationInfo_ReportList(id);
            synchronized (this) {
                if (reportList == null) {
                    reportList = reportListNew;
                }
            }
        }
        return reportList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 542575969)
    public synchronized void resetReportList() {
        reportList = null;
    }

    public StationInfo() {
    }

    @Generated(hash = 1701018272)
    public StationInfo(Long id, int siteId, int fromType, String name, String code, int type,
                       String address, String deviceType, String configure, double longitude, double latitude,
                       int TAC, int eNodeBID, int testStatus, int isExportedReport) {
        this.id = id;
        this.siteId = siteId;
        this.fromType = fromType;
        this.name = name;
        this.code = code;
        this.type = type;
        this.address = address;
        this.deviceType = deviceType;
        this.configure = configure;
        this.longitude = longitude;
        this.latitude = latitude;
        this.TAC = TAC;
        this.eNodeBID = eNodeBID;
        this.testStatus = testStatus;
        this.isExportedReport = isExportedReport;
    }

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1830684024)
    private transient StationInfoDao myDao;

    public int getSiteId() {
        return this.siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getFromType() {
        return this.fromType;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 614235812)
    public List<SceneInfo> getSceneInfoList() {
        if (sceneInfoList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SceneInfoDao targetDao = daoSession.getSceneInfoDao();
            List<SceneInfo> sceneInfoListNew = targetDao._queryStationInfo_SceneInfoList(id);
            synchronized (this) {
                if (sceneInfoList == null) {
                    sceneInfoList = sceneInfoListNew;
                }
            }
        }
        return sceneInfoList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1502043231)
    public synchronized void resetSceneInfoList() {
        sceneInfoList = null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeInt(this.siteId);
        dest.writeInt(this.fromType);
        dest.writeString(this.name);
        dest.writeString(this.code);
        dest.writeInt(this.type);
        dest.writeString(this.address);
        dest.writeString(this.deviceType);
        dest.writeString(this.configure);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeInt(this.TAC);
        dest.writeInt(this.eNodeBID);
        dest.writeInt(this.testStatus);
        dest.writeInt(this.isExportedReport);
        dest.writeString(this.mDistance);
        dest.writeTypedList(this.cellInfoList);
        dest.writeTypedList(this.sceneInfoList);
        dest.writeTypedList(this.reportList);
    }

    protected StationInfo(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.siteId = in.readInt();
        this.fromType = in.readInt();
        this.name = in.readString();
        this.code = in.readString();
        this.type = in.readInt();
        this.address = in.readString();
        this.deviceType = in.readString();
        this.configure = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.TAC = in.readInt();
        this.eNodeBID = in.readInt();
        this.testStatus = in.readInt();
        this.isExportedReport = in.readInt();
        this.mDistance = in.readString();
        this.cellInfoList = in.createTypedArrayList(CellInfo.CREATOR);
        this.sceneInfoList = in.createTypedArrayList(SceneInfo.CREATOR);
        this.reportList = in.createTypedArrayList(StationInfoReport.CREATOR);
    }

    public static final Creator<StationInfo> CREATOR = new Creator<StationInfo>() {
        @Override
        public StationInfo createFromParcel(Parcel source) {
            return new StationInfo(source);
        }

        @Override
        public StationInfo[] newArray(int size) {
            return new StationInfo[size];
        }
    };

    @Override
    public int compareTo(@NonNull StationInfo stationInfo) {
        String dis1 = stationInfo.getDistance();
        String dis2 = this.getDistance();
        String dis1Pro = "";
        String dis2Pro = "";
        if (!TextUtils.isEmpty(dis1) && dis1.contains("m")) {
            dis1Pro = dis1.replace("m", "");
        }
        if (!TextUtils.isEmpty(dis2) && dis1.contains("m")) {
            dis2Pro = dis2.replace("m", "");
        }
        try {
            return Float.parseFloat(dis1Pro) > Float.parseFloat(dis2Pro) ? -1 : 1;
        } catch (Exception e) {
            LogUtil.e("StationInfo", e.getMessage());
            return 1;
        }
    }
}
