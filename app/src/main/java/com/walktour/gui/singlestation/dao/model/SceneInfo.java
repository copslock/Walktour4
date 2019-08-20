package com.walktour.gui.singlestation.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.walktour.greendao.DaoSession;
import com.walktour.greendao.SceneInfoDao;
import com.walktour.greendao.TaskTestResultDao;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * 基站测试场景信息表
 * Created by wangk on 2017/9/9.
 */
@Entity(nameInDb = "scene_info")
public class SceneInfo implements Parcelable, Comparable<SceneInfo> {
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
     * eNodeBID
     */
    @Property(nameInDb = "eNodeBID")
    private int eNodeBID;
    /**
     * 小区ID,当场景为小区测试时设置
     */
    @Property(nameInDb = "cell_id")
    private int cellId;
   /**
     * 场景类型
     */
    @Property(nameInDb = "scene_type")
    private int sceneType = SingleStationDaoManager.SCENE_TYPE_COVERAGE;

    /**
     * 场景名称
     */
    @Property(nameInDb = "scene_name")
    private String sceneName;


    /**
     * 名称
     */
    @Property(nameInDb = "name")
    private String name;

    /**
     * 平台场景ID
     */
    @Property(nameInDb = "platform_scene_id")
    private int platformSceneId;
    /**
     * 数据管理的记录ID
     */
    @Property(nameInDb = "record_id")
    private String recordId;


    /**
     * 是否已上传
     */
    @Property(nameInDb = "is_uploaded")
    private boolean uploaded = false;

    @Property(nameInDb = "record_task_names")
    private String recordTaskNames;

    /**
     * 场景关联的业务测试列表
     */
    @ToMany(referencedJoinProperty = "sceneId")
    private List<TaskTestResult> taskTestResultList;

    @Override
    public String toString() {
        return "SceneInfo{" +
                "id=" + id +
                ", stationId=" + stationId +
                ", eNodeBID=" + eNodeBID +
                ", cellId=" + cellId +
                ", sceneType=" + sceneType +
                ", sceneName='" + sceneName + '\'' +
                ", name='" + name + '\'' +
                ", platformSceneId=" + platformSceneId +
                ", recordId='" + recordId + '\'' +
                ", uploaded=" + uploaded +
                ", recordTaskNames='" + recordTaskNames + '\'' +
                ", taskTestResultList=" + taskTestResultList +
                ", daoSession=" + daoSession +
                ", myDao=" + myDao +
                '}';
    }

    public String getRecordTaskNames() {
        return recordTaskNames;
    }

    public void setRecordTaskNames(String recordTaskNames) {
        this.recordTaskNames = recordTaskNames;
    }



    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

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

    public int getENodeBID() {
        return this.eNodeBID;
    }

    public void setENodeBID(int eNodeBID) {
        this.eNodeBID = eNodeBID;
    }

    public int getCellId() {
        return this.cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }
    public int getSceneType() {
        return this.sceneType;
    }

    public void setSceneType(int sceneType) {
        this.sceneType = sceneType;
    }

    public int getPlatformSceneId() {
        return this.platformSceneId;
    }

    public void setPlatformSceneId(int platformSceneId) {
        this.platformSceneId = platformSceneId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    } public boolean getUploaded() {
        return this.uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1186093684)
    public List<TaskTestResult> getTaskTestResultList() {
        if (taskTestResultList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TaskTestResultDao targetDao = daoSession.getTaskTestResultDao();
            List<TaskTestResult> taskTestResultListNew = targetDao
                    ._querySceneInfo_TaskTestResultList(id);
            synchronized (this) {
                if (taskTestResultList == null) {
                    taskTestResultList = taskTestResultListNew;
                }
            }
        }
        return taskTestResultList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1040972663)
    public synchronized void resetTaskTestResultList() {
        taskTestResultList = null;
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
    @Generated(hash = 468514860)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSceneInfoDao() : null;
    }

    public SceneInfo() {
    }


    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1779384509)
    private transient SceneInfoDao myDao;

    @Override
    public int compareTo(@NonNull SceneInfo sceneInfo) {
        int result = sceneInfo.getSceneType() - this.getSceneType();//先按照场景类型排序
        if (result == 0) {
            return sceneInfo.getId() - this.getId() > 0 ? -1 : 1;//如果场景类型相等了再用id进行排序
        } else if (result > 0) {
            return -1;
        } else {
            return 1;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.stationId);
        dest.writeInt(this.eNodeBID);
        dest.writeInt(this.cellId);
        dest.writeInt(this.sceneType);
        dest.writeString(this.sceneName);
        dest.writeString(this.name);
        dest.writeInt(this.platformSceneId);
        dest.writeString(this.recordId);
        dest.writeByte(this.uploaded ? (byte) 1 : (byte) 0);
        dest.writeString(this.recordTaskNames);
        dest.writeTypedList(this.taskTestResultList);
    }

    protected SceneInfo(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.stationId = (Long) in.readValue(Long.class.getClassLoader());
        this.eNodeBID = in.readInt();
        this.cellId = in.readInt();
        this.sceneType = in.readInt();
        this.sceneName = in.readString();
        this.name = in.readString();
        this.platformSceneId = in.readInt();
        this.recordId = in.readString();
        this.uploaded = in.readByte() != 0;
        this.recordTaskNames = in.readString();
        this.taskTestResultList = in.createTypedArrayList(TaskTestResult.CREATOR);
    }

    @Generated(hash = 312610809)
    public SceneInfo(Long id, Long stationId, int eNodeBID, int cellId, int sceneType,
            String sceneName, String name, int platformSceneId, String recordId,
            boolean uploaded, String recordTaskNames) {
        this.id = id;
        this.stationId = stationId;
        this.eNodeBID = eNodeBID;
        this.cellId = cellId;
        this.sceneType = sceneType;
        this.sceneName = sceneName;
        this.name = name;
        this.platformSceneId = platformSceneId;
        this.recordId = recordId;
        this.uploaded = uploaded;
        this.recordTaskNames = recordTaskNames;
    }

    public static final Creator<SceneInfo> CREATOR = new Creator<SceneInfo>() {
        @Override
        public SceneInfo createFromParcel(Parcel source) {
            return new SceneInfo(source);
        }

        @Override
        public SceneInfo[] newArray(int size) {
            return new SceneInfo[size];
        }
    };
}
