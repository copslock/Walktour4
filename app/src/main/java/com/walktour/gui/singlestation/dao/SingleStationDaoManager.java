package com.walktour.gui.singlestation.dao;
import android.content.Context;

import com.walktour.base.util.LogUtil;
import com.walktour.greendao.CellInfoDao;
import com.walktour.greendao.DaoMaster;
import com.walktour.greendao.DaoSession;
import com.walktour.greendao.SceneInfoDao;
import com.walktour.greendao.StationInfoDao;
import com.walktour.greendao.StationInfoReportDao;
import com.walktour.greendao.SurveyCellInfoDao;
import com.walktour.greendao.SurveyPhotoDao;
import com.walktour.greendao.SurveyStationInfoDao;
import com.walktour.greendao.TaskTestResultDao;
import com.walktour.greendao.ThresholdSettingDao;
import com.walktour.greendao.ThresholdTestResultDao;
import com.walktour.gui.singlestation.dao.model.CellInfo;
import com.walktour.gui.singlestation.dao.model.SceneInfo;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.StationInfoReport;
import com.walktour.gui.singlestation.dao.model.SurveyCellInfo;
import com.walktour.gui.singlestation.dao.model.SurveyPhoto;
import com.walktour.gui.singlestation.dao.model.SurveyStationInfo;
import com.walktour.gui.singlestation.dao.model.TaskTestResult;
import com.walktour.gui.singlestation.dao.model.ThresholdSetting;
import com.walktour.gui.singlestation.dao.model.ThresholdTestResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 单站验证数据库操作类
 * Created by wangk on 2017/6/15.
 */

public class SingleStationDaoManager {
    /**
     * 日志标识
     */
    private static final String TAG = "SingleStationDaoManager";
    /**
     * 站型：室内基站
     */
    public static final int STATION_TYPE_INDOOR = 0;
    /**
     * 站型：室外宏站
     */
    public static final int STATION_TYPE_OUTDOOR = 1;
    /**
     * 场景类型:覆盖测试
     */
    public static final int SCENE_TYPE_COVERAGE = 0;
    /**
     * 场景类型:切换测试
     */
    public static final int SCENE_TYPE_HANDOVER = 1;
    /**
     * 场景类型:外泄测试
     */
    public static final int SCENE_TYPE_SIGNAL_LEAKAGE = 2;
    /**
     * 场景类型:性能测试
     */
    public static final int SCENE_TYPE_PERFORMANCE = 3;
    /**
     * 场景类型:停车场测试
     */
//    public static final int SCENE_TYPE_PARK = 4;
    /**
     * 唯一实例
     */
    private static SingleStationDaoManager sInstance;
    /**
     * 数据库名称
     */
    private static final String DB_NAME = "singlestation.db";
    /**
     * 数据操作事务类
     */
    private DaoSession mDaoSession;

    private SingleStationDaoManager(Context context) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        this.mDaoSession = daoMaster.newSession();
    }

    /**
     * 返回唯一实例
     *
     * @param context 上下文
     * @return 唯一实例
     */
    public static SingleStationDaoManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SingleStationDaoManager(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * 获得小区对象
     *
     * @param cellId 小区ID
     * @return 基站对象
     */
    private CellInfo getCellInfo(Long cellId) {
        LogUtil.d(TAG, "----getCellInfo----start----");
        CellInfo cellInfo = null;
        try {
            CellInfoDao dao = this.mDaoSession.getCellInfoDao();
            cellInfo = dao.load(cellId);
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----getCellInfo----end----isNull:" + (cellInfo == null));
        return cellInfo;
    }

    /**
     * 获得基站对象
     *
     * @param stationId 基站ID
     * @return 基站对象
     */
    public StationInfo getStationInfo(Long stationId) {
        LogUtil.d(TAG, "----getStationInfo----start----");
        StationInfo stationInfo = null;
        try {
            StationInfoDao dao = this.mDaoSession.getStationInfoDao();
            stationInfo = dao.load(stationId);
            stationInfo.getCellInfoList();
            stationInfo.getSceneInfoList();
            stationInfo.getReportList();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----getStationInfo----end----isNull:" + (stationInfo == null));
        return stationInfo;
    }

    /**
     * 生成勘查基站对象
     *
     * @param surveyStationId 勘查基站ID
     * @param cellInfo        小区信息
     */
    public void createSurveyCellInfo(Long surveyStationId, CellInfo cellInfo) {
        LogUtil.d(TAG, "----createSurveyCellInfo----start----");
        SurveyCellInfo surveyCellInfo = new SurveyCellInfo();
        surveyCellInfo.setSurveyStationId(surveyStationId);
        surveyCellInfo.setHorizontalFalfPowerAngle(cellInfo.getHorizontalFalfPowerAngle());
        surveyCellInfo.setBand(cellInfo.getBand());
        surveyCellInfo.setFrequency(cellInfo.getFrequency());
        surveyCellInfo.setElectricDownAngle(cellInfo.getElectricDownAngle());
        surveyCellInfo.setDownAngle(cellInfo.getDownAngle());
        surveyCellInfo.setCellId(cellInfo.getCellId());
        surveyCellInfo.setCellInfoId(cellInfo.getId());
        surveyCellInfo.setCarrierSetup(cellInfo.getCarrierSetup());
        surveyCellInfo.setBandwidth(cellInfo.getBandwidth());
        surveyCellInfo.setAzimuth(cellInfo.getAzimuth());
        surveyCellInfo.setAerialVender(cellInfo.getAerialVender());
        surveyCellInfo.setAerialType(cellInfo.getAerialType());
        surveyCellInfo.setAerialHigh(cellInfo.getAerialHigh());
        surveyCellInfo.setMachineDownAngle(cellInfo.getMachineDownAngle());
        surveyCellInfo.setPA(cellInfo.getPA());
        surveyCellInfo.setPB(cellInfo.getPB());
        surveyCellInfo.setPCI(cellInfo.getPCI());
        surveyCellInfo.setPDCCH(cellInfo.getPDCCH());
        surveyCellInfo.setRootSequence(cellInfo.getRootSequence());
        surveyCellInfo.setRsPower(cellInfo.getRsPower());
        surveyCellInfo.setSpecialSubframeMatching(cellInfo.getSpecialSubframeMatching());
        surveyCellInfo.setSubframeMatching(cellInfo.getSubframeMatching());
        surveyCellInfo.setVerticalFalfPowerAngle(cellInfo.getVerticalFalfPowerAngle());
        try {
            this.save(surveyCellInfo);
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----createSurveyCellInfo----end----");
    }

    /**
     * 生成勘查基站对象
     *
     * @param stationInfo 基站对象
     * @return 生成勘查基站对象ID
     */
    public Long createSurveyStationInfo(StationInfo stationInfo) {
        LogUtil.d(TAG, "----createSurveyStationInfo----start----");
        SurveyStationInfo surveyStationInfo = new SurveyStationInfo();
        surveyStationInfo.setStationId(stationInfo.getId());
        surveyStationInfo.setENodeBID(stationInfo.getENodeBID());
        surveyStationInfo.setConfigure(stationInfo.getConfigure());
        surveyStationInfo.setAddress(stationInfo.getAddress());
        surveyStationInfo.setLongitude(stationInfo.getLongitude());
        surveyStationInfo.setLatitude(stationInfo.getLatitude());
        surveyStationInfo.setTAC(stationInfo.getTAC());
        surveyStationInfo.setType(stationInfo.getType());
        try {
            this.save(surveyStationInfo);
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----createSurveyStationInfo----end----");
        return surveyStationInfo.getId();
    }

    /**
     * 获得勘查基站对象
     *
     * @param stationId 原始基站ID
     * @return 基站对象
     */
    public SurveyStationInfo getSurveyStationInfo(Long stationId) {
        LogUtil.d(TAG, "----getSurveyStationInfo----start----");
        SurveyStationInfo surveyStationInfo = null;
        try {
            SurveyStationInfoDao dao = this.mDaoSession.getSurveyStationInfoDao();
            List<SurveyStationInfo> list = dao.queryBuilder().where(SurveyStationInfoDao.Properties.StationId.eq(stationId)).build().list();
            if (list != null && !list.isEmpty()) {
                surveyStationInfo = list.get(0);
                surveyStationInfo.getCellInfoList();
                if (surveyStationInfo.getCellInfoList() != null) {
                    for (SurveyCellInfo surveyCellInfo : surveyStationInfo.getCellInfoList()) {
                        surveyCellInfo.setCellInfo(this.getCellInfo(surveyCellInfo.getCellInfoId()));
                    }
                }
                surveyStationInfo.setStationInfo(this.getStationInfo(stationId));
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----getSurveyStationInfo----end----isNull:" + (surveyStationInfo == null));
        return surveyStationInfo;
    }

    /**
     * 判断相应的基站标识是否已经存在
     *
     * @return enodeBID 基站标识
     */
    public boolean checkStationInfoExist(String enodeBID) {
        LogUtil.d(TAG, "----checkStationInfoExist----start----");
        boolean isExist = false;
        try {
            StationInfoDao dao = this.mDaoSession.getStationInfoDao();
            long count = dao.queryBuilder().where(StationInfoDao.Properties.ENodeBID.eq(enodeBID)).count();
            if (count > 0)
                isExist = true;
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----checkStationInfoExist----end----isExist:" + isExist);
        return isExist;
    }

    /**
     * 获得本地基站列表
     *
     * @return 本地基站列表
     */
    public List<StationInfo> getStationInfoList() {
        LogUtil.d(TAG, "----getStationInfoList----start----");
        List<StationInfo> list = new ArrayList<>();
        try {
            StationInfoDao dao = this.mDaoSession.getStationInfoDao();
            list = dao.loadAll();
            //因为StationInfo用了Parcelable方式，如果在传输前前没有调用下面的方法，则会导致传输后调用异常
            for (StationInfo station : list) {
                station.getCellInfoList();
                for (SceneInfo sceneInfo : station.getSceneInfoList()) {
                    for (TaskTestResult taskTestResult : sceneInfo.getTaskTestResultList()) {
                        taskTestResult.getThresholdTestResultList();
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----getStationInfoList----end----size:" + list.size());
        return list;
    }
    /**
     * 获得基站列表
     *
     * @param fromType 本地基站和服务器基站
     * @return
     */
    public List<StationInfo> getStationInfoList(int fromType) {
        LogUtil.d(TAG, "----getStationInfoList----start----");
        List<StationInfo> list = new ArrayList<>();
        try {
            StationInfoDao dao = this.mDaoSession.getStationInfoDao();
            list = dao.loadAll();

            Iterator<StationInfo> it = list.iterator();
            while(it.hasNext()){
                StationInfo stationInfo = it.next();
                if(stationInfo.getFromType()!=fromType){
                    it.remove();
                }
            }

            //因为StationInfo用了Parcelable方式，如果在传输前前没有调用下面的方法，则会导致传输后调用异常
            for (StationInfo station : list) {
                station.getCellInfoList();
                for (SceneInfo sceneInfo : station.getSceneInfoList()) {
                    for (TaskTestResult taskTestResult : sceneInfo.getTaskTestResultList()) {
                        taskTestResult.getThresholdTestResultList();
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----getStationInfoList----end----size:" + list.size());
        return list;
    }

    /**
     * 获得勘查基站历史列表
     *
     * @return 勘查基站历史列表
     */
    public List<SurveyStationInfo> getEditingSurveyStationInfoList() {
        LogUtil.d(TAG, "----getSurveyStationInfoList----start----");
        List<SurveyStationInfo> list = new ArrayList<>();
        try {
            SurveyStationInfoDao dao = this.mDaoSession.getSurveyStationInfoDao();
            list = dao.queryBuilder().where(SurveyStationInfoDao.Properties.IsEditing.eq(true)).list();
            //因为StationInfo用了Parcelable方式，如果在传输前前没有调用下面的方法，则会导致传输后调用异常
            for (SurveyStationInfo station : list) {
                station.setStationInfo(this.getStationInfo(station.getStationId()));
                station.getCellInfoList();
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----getSurveyStationInfoList----end----size:" + list.size());
        return list;
    }

    /**
     * 保存基站信息
     *
     * @param stationInfo 基站对象
     */
    public void save(StationInfo stationInfo) {
        LogUtil.d(TAG, "----save----StationInfo----");
        try {
            StationInfoDao dao = this.mDaoSession.getStationInfoDao();
            dao.save(stationInfo);
            this.mDaoSession.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 保存勘查基站信息
     *
     * @param stationInfo 勘查基站对象
     */
    public void save(SurveyStationInfo stationInfo) {
        LogUtil.d(TAG, "----save----SurveyStationInfo----");
        try {
            SurveyStationInfoDao dao = this.mDaoSession.getSurveyStationInfoDao();
            dao.save(stationInfo);
            SurveyCellInfoDao cellInfoDao = this.mDaoSession.getSurveyCellInfoDao();
            if (stationInfo.getCellInfoList() != null) {
                for (SurveyCellInfo cellInfo : stationInfo.getCellInfoList()) {
                    cellInfoDao.save(cellInfo);
                }
            }
            this.mDaoSession.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 保存勘查基站信息
     *
     * @param cellInfo 勘查小区对象
     */
    public void save(SurveyCellInfo cellInfo) {
        LogUtil.d(TAG, "----save----SurveyCellInfo----");
        try {
            SurveyCellInfoDao dao = this.mDaoSession.getSurveyCellInfoDao();
            dao.save(cellInfo);
            this.mDaoSession.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 保存小区信息
     *
     * @param cellInfo 小区对象
     */
    public void save(CellInfo cellInfo) {
        LogUtil.d(TAG, "----save----CellInfo----");
        try {
            CellInfoDao dao = this.mDaoSession.getCellInfoDao();
            dao.save(cellInfo);
            this.mDaoSession.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 保存基站报告
     *
     * @param stationInfoReport 基站报告
     */
    public void save(StationInfoReport stationInfoReport) {
        LogUtil.d(TAG, "----save----StationInfoReport----");
        try {
            StationInfoReportDao dao = this.mDaoSession.getStationInfoReportDao();
            dao.save(stationInfoReport);
            this.mDaoSession.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 保存阈值设置
     *
     * @param thresholdSetting 阈值设置
     */
    public void save(ThresholdSetting thresholdSetting) {
        LogUtil.d(TAG, "----save----ThresholdSetting----");
        try {
            ThresholdSettingDao dao = this.mDaoSession.getThresholdSettingDao();
            dao.save(thresholdSetting);
            this.mDaoSession.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 保存拍照图片、删除之前存储图片并返回路径
     *
     * @param sp 勘查图片
     * @return 之前保存过的老的图片路径
     */
    public String save(SurveyPhoto sp) {
        LogUtil.d(TAG, "----save----SurveyPhoto----");
        String oldPhotoPath = "";//之前保存的图片地址
        try {
            SurveyPhotoDao dao = this.mDaoSession.getSurveyPhotoDao();
            SurveyPhoto surveyPhoto = getSurveyPhoto(sp.getSurveyStationId(), sp.getPhotoType());
            if (surveyPhoto != null) {
                oldPhotoPath = surveyPhoto.getPhotoPath();
                dao.delete(surveyPhoto);
            }
            dao.save(sp);
            this.mDaoSession.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        return oldPhotoPath;
    }

    /**
     * 获取当前基站对应图片
     *
     * @param surveyStationId 基站id
     * @param photoType       图片类型
     * @return 基站图片
     */
    public SurveyPhoto getSurveyPhoto(Long surveyStationId, int photoType) {
        SurveyPhotoDao dao = this.mDaoSession.getSurveyPhotoDao();
        SurveyPhoto surveyPhoto = dao.queryBuilder()
                .where(SurveyPhotoDao.Properties.SurveyStationId.eq(surveyStationId),
                        SurveyPhotoDao.Properties.PhotoType.eq(photoType))
                .build()
                .unique();
        this.mDaoSession.clear();
        return surveyPhoto;
    }


    /**
     * 删除基站报告
     *
     * @param stationInfoReport 基站报告
     */
    public void delete(StationInfoReport stationInfoReport) {
        LogUtil.d(TAG, "----delete----stationInfoReport----");
        try {
            StationInfoReportDao dao = this.mDaoSession.getStationInfoReportDao();
            dao.delete(stationInfoReport);
            this.mDaoSession.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 保存场景信息
     *
     * @param sceneInfo 场景对象
     */
    public void save(SceneInfo sceneInfo) {
        LogUtil.d(TAG, "----save----SceneInfo----");
        try {
            SceneInfoDao dao = this.mDaoSession.getSceneInfoDao();
            dao.save(sceneInfo);
            this.mDaoSession.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 保存业务测试结果信息
     *
     * @param taskTestResult 业务测试结果对象
     */
    public void save(TaskTestResult taskTestResult) {
        LogUtil.d(TAG, "----save----SceneTaskTestResult----");
        try {
            TaskTestResultDao dao = this.mDaoSession.getTaskTestResultDao();
            dao.save(taskTestResult);
            this.mDaoSession.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 更新业务测试结果信息
     *
     * @param taskTestResult 业务测试结果对象
     */
    public void update(TaskTestResult taskTestResult) {
        LogUtil.d(TAG, "----update----SceneTaskTestResult----");
        try {
            TaskTestResultDao dao = this.mDaoSession.getTaskTestResultDao();
            dao.update(taskTestResult);
            this.mDaoSession.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 保存阈值测试结果信息
     *
     * @param thresholdTestResult 阈值测试结果对象
     */
    public void save(ThresholdTestResult thresholdTestResult) {
        LogUtil.d(TAG, "----save----ThresholdTestResult----");
        try {
            ThresholdTestResultDao dao = this.mDaoSession.getThresholdTestResultDao();
            dao.save(thresholdTestResult);
            this.mDaoSession.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 根据基站ID获得场景列表
     *
     * @param stationId 基站ID
     * @return 场景列表
     */
    public List<SceneInfo> getSceneInfoList(Long stationId) {
        LogUtil.d(TAG, "----getSceneInfoList----start----stationId:" + stationId);
        List<SceneInfo> list = new ArrayList<>();
        try {
            SceneInfoDao dao = this.mDaoSession.getSceneInfoDao();
            list = dao.queryBuilder().where(SceneInfoDao.Properties.StationId.eq(stationId)).orderAsc(SceneInfoDao.Properties.SceneType).build().list();
            for (SceneInfo sceneInfo : list) {
                for (TaskTestResult taskTestResult : sceneInfo.getTaskTestResultList()) {
                    taskTestResult.getThresholdTestResultList();
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----getSceneList----end----size:" + list.size());
        return list;
    }


    /**
     * 根据recordId获取场景SceneInfo对象
     * @param recordId
     * @return
     */
    public SceneInfo getSceneInfo(String recordId) {
        SceneInfo sceneInfo = null;
        try {
            SceneInfoDao dao = this.mDaoSession.getSceneInfoDao();
            sceneInfo = dao.queryBuilder().where(SceneInfoDao.Properties.RecordId.eq(recordId)).unique();
            sceneInfo.getTaskTestResultList();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        return sceneInfo;
    }

    /**
     * 根据基站ID、场景类型获得场景列表
     *
     * @param stationId 基站ID
     * @param sceneType 场景类型
     * @return 场景列表
     */
    public List<SceneInfo> getSceneInfoList(Long stationId, int sceneType) {
        LogUtil.d(TAG, "----getSceneInfoList----start----stationId:" + stationId + "----sceneType:" + sceneType);
        List<SceneInfo> list = new ArrayList<>();
        try {
            SceneInfoDao dao = this.mDaoSession.getSceneInfoDao();
            list = dao.queryBuilder().where(SceneInfoDao.Properties.StationId.eq(stationId), SceneInfoDao.Properties.SceneType.eq(sceneType)).orderAsc(SceneInfoDao.Properties.Id).build().list();
            for (SceneInfo sceneInfo : list) {
                for (TaskTestResult taskTestResult : sceneInfo.getTaskTestResultList()) {
                    taskTestResult.getThresholdTestResultList();
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----getSceneList----end----size:" + list.size());
        return list;
    }

    /**
     * 根据基站ID、场景类型、小区id获得场景列表
     *
     * @param stationId 基站ID
     * @param sceneType 场景类型
     * @param cellId    小区id
     * @return 场景列表
     */
    public List<SceneInfo> getSceneInfoList(Long stationId, int sceneType, int cellId) {
        LogUtil.d(TAG, "----getSceneInfoList----start----stationId:" + stationId + "----sceneType:" + sceneType);
        List<SceneInfo> list = new ArrayList<>();
        try {
            SceneInfoDao dao = this.mDaoSession.getSceneInfoDao();
            list = dao.queryBuilder().where(SceneInfoDao.Properties.StationId.eq(stationId),
                    SceneInfoDao.Properties.SceneType.eq(sceneType), SceneInfoDao.Properties.CellId.eq(cellId))
                    .orderAsc(SceneInfoDao.Properties.Id).build().list();
            for (SceneInfo sceneInfo : list) {
                for (TaskTestResult taskTestResult : sceneInfo.getTaskTestResultList()) {
                    taskTestResult.getThresholdTestResultList();
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----getSceneList----end----size:" + list.size());
        return list;
    }

    /**
     * 获得业务测试结果对象
     *
     * @param resultId 业务测试结果ID
     * @return 业务测试结果信息
     */
    public TaskTestResult getTaskTestResult(Long resultId) {
        LogUtil.d(TAG, "----getTaskTestResult----start----resultId:" + resultId);
        TaskTestResult result = null;
        try {
            TaskTestResultDao dao = this.mDaoSession.getTaskTestResultDao();
            result = dao.load(resultId);
            if (result != null)
                result.getThresholdTestResultList();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----getTaskTestResult----end----");
        return result;
    }


    /**
     * 根据基站ID和场景类型获得测试结果列表
     *
     * @param stationId 基站ID
     * @param sceneType 场景类型
     * @return 测试结果信息
     */
    public List<TaskTestResult> getTaskTestResultList(Long stationId, int sceneType) {
        LogUtil.d(TAG, "----getTaskTestResultList----start----stationId:" + stationId + "----sceneType:" + sceneType);
        List<TaskTestResult> list = new ArrayList<>();
        try {
            TaskTestResultDao dao = this.mDaoSession.getTaskTestResultDao();
            List<SceneInfo> sceneInfoList = this.getSceneInfoList(stationId, sceneType);
            for (SceneInfo sceneInfo : sceneInfoList) {
                list.addAll(this.getTaskTestResultList(sceneInfo.getId()));
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----getTaskTestResultList----end----size:" + list.size());
        return list;
    }

    /**
     * 根据场景ID获得测试结果列表
     *
     * @param sceneId 场景ID
     * @return 测试结果信息
     */
    public List<TaskTestResult> getTaskTestResultList(Long sceneId) {
        LogUtil.d(TAG, "----getTaskTestResultList----start----sceneId:" + sceneId);
        List<TaskTestResult> list = new ArrayList<>();
        try {
            TaskTestResultDao dao = this.mDaoSession.getTaskTestResultDao();
            list = dao.queryBuilder().where(TaskTestResultDao.Properties.SceneId.eq(sceneId)).orderAsc(TaskTestResultDao.Properties.TaskType).build().list();
            for (TaskTestResult taskTestResult : list) {
                taskTestResult.getThresholdTestResultList();
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----getTaskTestResultList----end----size:" + list.size());
        return list;
    }

    /**
     * 根据基站类型获取阈值设置模板列表
     *
     * @param stationType 基站类型
     * @return 阈值设置模板列表
     */
    public List<ThresholdSetting> getThresholdSettingList(int stationType) {
        LogUtil.d(TAG, "----getThresholdSettingList----start----stationType:" + stationType);
        List<ThresholdSetting> list = new ArrayList<>();
        try {
            ThresholdSettingDao dao = this.mDaoSession.getThresholdSettingDao();
            list = dao.queryBuilder().where(ThresholdSettingDao.Properties.StationType.eq(stationType)).orderAsc(ThresholdSettingDao.Properties.Id).build().list();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----getThresholdSettingList----end----size:" + list.size());
        return list;
    }

    /**
     * 根据基站类型和场景类型获取阈值设置模板列表
     *
     * @param stationType 基站类型
     * @param sceneType   场景类型
     * @return 阈值设置模板列表
     */
    public List<ThresholdSetting> getThresholdSettingList(int stationType, int sceneType) {
        LogUtil.d(TAG, "----getThresholdSettingList----start----stationType:" + stationType + "----sceneType:" + sceneType);
        List<ThresholdSetting> list = new ArrayList<>();
        try {
            ThresholdSettingDao dao = this.mDaoSession.getThresholdSettingDao();
            list = dao.queryBuilder().where(ThresholdSettingDao.Properties.StationType.eq(stationType), ThresholdSettingDao.Properties.SceneType.eq(sceneType)).orderAsc(ThresholdSettingDao.Properties.Id).build().list();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        LogUtil.d(TAG, "----getThresholdSettingList----end----size:" + list.size());
        return list;
    }

}
