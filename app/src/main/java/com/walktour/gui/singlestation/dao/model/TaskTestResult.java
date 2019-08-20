package com.walktour.gui.singlestation.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.walktour.greendao.DaoSession;
import com.walktour.greendao.TaskTestResultDao;
import com.walktour.greendao.ThresholdTestResultDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Transient;

import java.util.List;

/**
 * 业务测试结果表
 * Created by wangk on 2017/9/9.
 */
@Entity(nameInDb = "task_test_result")
public class TaskTestResult implements Parcelable {
    /**
     * 测试状态:未测试
     */
    public static final int TEST_STATUS_INIT = 0;
    /**
     * 测试状态:通过
     */
    public static final int TEST_STATUS_PASS = 1;
    /**
     * 测试状态:未通过
     */
    public static final int TEST_STATUS_FAULT = 2;
    /**
     * 唯一主键，自增
     */
    @Id(autoincrement = true)
    private Long id;
    /**
     * 业务类型
     */
    @Property(nameInDb = "task_type")
    private String taskType;
    /**
     * 场景信息表ID
     */
    @Property(nameInDb = "scene_id")
    private Long sceneId;
    /**
     * 平台测试项ID
     */
    @Property(nameInDb = "platform_item_id")
    private int platformItemId;
    /**
     * RSRP平均值
     */
    @Property(nameInDb = "rsrp_avg")
    private int rsrpAverage;
    /**
     * SINR平均值
     */
    @Property(nameInDb = "sinr_avg")
    private int sinrAverage;


    /**
     * 测试状态（0、未测试,1、测试通过,2、测试失败)
     */
    @Property(nameInDb = "test_status")
    private int testStatus = TEST_STATUS_INIT;
    /**
     * 业务测试关联的阈值列表
     */
    @ToMany(referencedJoinProperty = "taskTestResultId")
    private List<ThresholdTestResult> thresholdTestResultList;

    /**
     * 是否勾选
     */
    @Transient
    private boolean isCheck = false;
    /**
     * 测试的业务类型名称
     */
    @Transient
    private String mTaskTypeName;

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

    public Long getSceneId() {
        return this.sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public int getPlatformItemId() {
        return this.platformItemId;
    }

    public void setPlatformItemId(int platformItemId) {
        this.platformItemId = platformItemId;
    }

    public int getRsrpAverage() {
        return this.rsrpAverage;
    }

    public void setRsrpAverage(int rsrpAverage) {
        this.rsrpAverage = rsrpAverage;
    }

    public int getSinrAverage() {
        return this.sinrAverage;
    }

    public void setSinrAverage(int sinrAverage) {
        this.sinrAverage = sinrAverage;
    }

    public String getTaskTypeName() {
        return mTaskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        mTaskTypeName = taskTypeName;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 116689342)
    public List<ThresholdTestResult> getThresholdTestResultList() {
        if (thresholdTestResultList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ThresholdTestResultDao targetDao = daoSession.getThresholdTestResultDao();
            List<ThresholdTestResult> thresholdTestResultListNew = targetDao
                    ._queryTaskTestResult_ThresholdTestResultList(id);
            synchronized (this) {
                if (thresholdTestResultList == null) {
                    thresholdTestResultList = thresholdTestResultListNew;
                }
            }
        }
        return thresholdTestResultList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1899181150)
    public synchronized void resetThresholdTestResultList() {
        thresholdTestResultList = null;
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
    @Generated(hash = 767015432)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTaskTestResultDao() : null;
    }

    public int getTestStatus() {
        return this.testStatus;
    }

    public void setTestStatus(int testStatus) {
        this.testStatus = testStatus;
    }

    public TaskTestResult() {
    }


    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1303126645)
    private transient TaskTestResultDao myDao;




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.taskType);
        dest.writeValue(this.sceneId);
        dest.writeInt(this.platformItemId);
        dest.writeInt(this.rsrpAverage);
        dest.writeInt(this.sinrAverage);
        dest.writeInt(this.testStatus);
        dest.writeTypedList(this.thresholdTestResultList);
        dest.writeByte(this.isCheck ? (byte) 1 : (byte) 0);
        dest.writeString(this.mTaskTypeName);
    }


    protected TaskTestResult(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.taskType = in.readString();
        this.sceneId = (Long) in.readValue(Long.class.getClassLoader());
        this.platformItemId = in.readInt();
        this.rsrpAverage = in.readInt();
        this.sinrAverage = in.readInt();
        this.testStatus = in.readInt();
        this.thresholdTestResultList = in.createTypedArrayList(ThresholdTestResult.CREATOR);
        this.isCheck = in.readByte() != 0;
        this.mTaskTypeName = in.readString();
    }

    @Generated(hash = 206139860)
    public TaskTestResult(Long id, String taskType, Long sceneId, int platformItemId,
            int rsrpAverage, int sinrAverage, int testStatus) {
        this.id = id;
        this.taskType = taskType;
        this.sceneId = sceneId;
        this.platformItemId = platformItemId;
        this.rsrpAverage = rsrpAverage;
        this.sinrAverage = sinrAverage;
        this.testStatus = testStatus;
    }


    public static final Creator<TaskTestResult> CREATOR = new Creator<TaskTestResult>() {
        @Override
        public TaskTestResult createFromParcel(Parcel source) {
            return new TaskTestResult(source);
        }

        @Override
        public TaskTestResult[] newArray(int size) {
            return new TaskTestResult[size];
        }
    };
}
