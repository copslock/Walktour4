package com.walktour.gui.singlestation.survey.service;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.SurveyPhotoUtil;
import com.walktour.Utils.ZipUtil;
import com.walktour.base.gui.model.SimpleCallBack;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.SDCardUtil;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.SurveyCellInfo;
import com.walktour.gui.singlestation.dao.model.SurveyPhoto;
import com.walktour.gui.singlestation.dao.model.SurveyStationInfo;
import com.walktour.gui.singlestation.net.SingleStationRetrofitManager;
import com.walktour.gui.singlestation.net.model.UploadSurveyStationResultCallback;
import com.walktour.gui.singlestation.net.model.survey.CellInfoUploadModel;
import com.walktour.gui.singlestation.net.model.survey.SiteInfoUploadModel;
import com.walktour.gui.singlestation.net.model.survey.SurveyResultUploadModel;
import com.walktour.gui.singlestation.net.model.survey.TestInfoUploadModel;
import com.walktour.gui.singlestation.survey.model.SurveyPhotoCallBack;
import com.walktour.gui.singlestation.survey.model.SurveySiteHistoryCallBack;
import com.walktour.gui.singlestation.survey.model.SurveyStationInfoCallback;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 勘查基站服务服务类
 */
public class SurveyService {
    /**
     * 日志标识
     */
    private static final String TAG = "SurveyService";

    /**
     * 数据库操作类
     */
    private SingleStationDaoManager mDaoManager;

    public SurveyService(Context context) {
        this.mDaoManager = SingleStationDaoManager.getInstance(context);
    }

    /**
     * 保存编辑的小区勘查信息
     *
     * @param cellInfo 勘查小区
     * @param callBack 回调类
     */
    public void saveSurveyCellEdit(SurveyCellInfo cellInfo, SimpleCallBack callBack) {
        this.mDaoManager.save(cellInfo);
        callBack.onSuccess();
    }

    /**
     * 保存编辑的基站勘查信息
     *
     * @param stationInfo 勘查的基站
     * @param callBack    回调类
     */
    public void saveSurveySiteEdit(SurveyStationInfo stationInfo, SimpleCallBack callBack) {
        this.mDaoManager.save(stationInfo);
        callBack.onSuccess();
    }


    /**
     * 获取勘察基站信息对象
     *
     * @param stationId 原始基站ID
     * @param callback  回调接口
     */
    public void getSurveyStationInfo(Long stationId, SurveyStationInfoCallback callback) {
        SurveyStationInfo surveyStationInfo = this.mDaoManager.getSurveyStationInfo(stationId);
        callback.onSuccess(surveyStationInfo);
    }

    /**
     * 勘查基站历史列表查询
     *
     * @param callBack 回调类
     */
    public void getEditingSurveyStationList(SurveySiteHistoryCallBack callBack) {
        LogUtil.d(TAG, "----getSurveyStationList----");
        List<SurveyStationInfo> list = this.mDaoManager.getEditingSurveyStationInfoList();
        callBack.onSuccess(list);
    }


    /**
     * 更新保存勘察基站数据
     * @param surveyStationInfo
     */
    public void saveSurveyStation(SurveyStationInfo surveyStationInfo){
        LogUtil.d(TAG, "----saveSurveyStation----");
        this.mDaoManager.save(surveyStationInfo);
    }

    /**
     * 勘查基站图片查询
     *
     * @param stationId 基站ID
     * @param photoType 图片类型
     * @param callBack  回调类
     */
    public void getSurveyPhoto(long stationId, int photoType, SurveyPhotoCallBack callBack) {
        SurveyPhoto surveyPhoto = this.mDaoManager.getSurveyPhoto(stationId, photoType);
        callBack.onSuccess(surveyPhoto);
    }

    /**
     * 保存勘查照片
     *
     * @param photo    照片对象
     * @param callBack 回调类
     */
    public void saveSurveyPhoto(SurveyPhoto photo, SimpleCallBack callBack) {
        String oldPath = this.mDaoManager.save(photo);
        SurveyPhotoUtil.deletePhoto(oldPath);
        callBack.onSuccess();
    }

    /**
     * 基站勘察的数据上传
     *
     * @param context   上下文对象
     * @param stationId 基站id
     * @param callback  回调
     */
    public void uploadSurveyStationInfo(final Context context, String loginAccount, String serverIP, int serverPort, int siteId,Long stationId, final UploadSurveyStationResultCallback callback) {
        //按照服务器要求json格式构建勘察数据对象
        SurveyResultUploadModel resultUploadModel = buildSurveyResultUploadModel(this.mDaoManager.getSurveyStationInfo(stationId));
        String jsonStr = new Gson().toJson(resultUploadModel);
        SurveyPhoto surveyPhoto = this.mDaoManager.getSurveyPhoto(stationId, SurveyPhoto.PHOTO_TYPE_NORMAL);
        try {
            Set<File> originFiles = new HashSet<>();
            File file = new File(SDCardUtil.getSDCardPath() + "Walktour/singlestation/survey/files/");
            if (!file.exists()) {
                file.mkdirs();
            }
            //json文件转file
            File originInfoFile = FileUtil.getFileFromBytes(jsonStr.getBytes("UTF-8"), String.valueOf(file) + "/" + System.currentTimeMillis() + ".json");
            originFiles.add(originInfoFile);
            if (null != surveyPhoto && !TextUtils.isEmpty(surveyPhoto.getPhotoPath())) {
                //图片文件转file
                File surveyPhotoFile = new File(surveyPhoto.getPhotoPath());
                if (!surveyPhotoFile.exists()) {
                    try {
                        surveyPhotoFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                originFiles.add(surveyPhotoFile);
            }
            File zipFile = new File(String.valueOf(file) + "/" + System.currentTimeMillis() + ".zip");
            if (!zipFile.exists()) {
                try {
                    zipFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //json和图片一起压缩
            ZipUtil.zip(originFiles, zipFile);
            //网络上传请求
            SingleStationRetrofitManager retrofitManager = SingleStationRetrofitManager.getInstance(serverIP, serverPort);
            if (!retrofitManager.isLogin()) {
                callback.onFailure(context.getString(R.string.single_station_no_login));
                return;
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), zipFile);
            retrofitManager.uploadSurveyStationInfo(context, loginAccount, siteId, requestFile, callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            callback.onFailure(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(e.getMessage());
        }

    }

    /**
     * 构建SurveyResultUploadModel对象，将SurveyStationInfo对象转SurveyResultUploadModel对象，主要是过滤掉冗余参数
     *
     * @param ssi
     * @return
     */
    private SurveyResultUploadModel buildSurveyResultUploadModel(SurveyStationInfo ssi) {
        SurveyResultUploadModel uploadModel = new SurveyResultUploadModel();

        //测试信息model
        TestInfoUploadModel testInfoUploadModel = new TestInfoUploadModel();
        testInfoUploadModel.setTestDate(ssi.getTestDate());
        testInfoUploadModel.setTestDeviceModel(ssi.getTestDeviceModel());
        testInfoUploadModel.setTester(ssi.getTester());
        testInfoUploadModel.setTestPhoneNum(ssi.getTestPhone());
        testInfoUploadModel.setTestPlatform(ssi.getTestPlatform());
        uploadModel.setTestInfo(testInfoUploadModel);
        //基站信息model
        SiteInfoUploadModel siteInfoUploadModel = new SiteInfoUploadModel();
        siteInfoUploadModel.setCity(ssi.getCity());
        siteInfoUploadModel.setDistrict(ssi.getDistrict());
        siteInfoUploadModel.setAddress(ssi.getAddress());
        siteInfoUploadModel.setSiteName(ssi.getStationInfo().getName());
        siteInfoUploadModel.setSiteConfigure(ssi.getConfigure());
        siteInfoUploadModel.setSiteType(ssi.getType() == SingleStationDaoManager.STATION_TYPE_INDOOR ? "室内" : "室外");
        siteInfoUploadModel.setDeviceType(ssi.getDeviceType());
        siteInfoUploadModel.setTAC(ssi.getTAC());
        siteInfoUploadModel.seteNodeBID(ssi.getENodeBID());
        siteInfoUploadModel.setLatitude(ssi.getLatitude());
        siteInfoUploadModel.setLongitude(ssi.getLongitude());
        uploadModel.setSiteInfo(siteInfoUploadModel);
        //小区勘察信息model列表
        if (ssi.getCellInfoList() != null && !ssi.getCellInfoList().isEmpty()) {
            List<CellInfoUploadModel> cellInfoUploadModels = new ArrayList<>();
            CellInfoUploadModel cellInfoUploadModel;
            for (SurveyCellInfo info : ssi.getCellInfoList()) {
                cellInfoUploadModel = new CellInfoUploadModel();
                cellInfoUploadModel.setPA(info.getPA());
                cellInfoUploadModel.setPB(info.getPB());
                cellInfoUploadModel.setPCI(info.getPCI());
                cellInfoUploadModel.setPDCCHSymbols(info.getPDCCH());
                cellInfoUploadModel.setRsPower(info.getRsPower());
                cellInfoUploadModel.setAerialHigh(info.getAerialHigh());
                cellInfoUploadModel.setAerialType(info.getAerialType());
                cellInfoUploadModel.setAerialVender(info.getAerialVender());
                cellInfoUploadModel.setAzimuth(info.getAzimuth());
                cellInfoUploadModel.setBand(info.getBand());
                cellInfoUploadModel.setBandwidth(info.getBandwidth());
                cellInfoUploadModel.setCarrierConfig(info.getCarrierSetup());
                cellInfoUploadModel.setCellId(info.getCellId());
                cellInfoUploadModel.setCellName(info.getCellInfo().getCellName());
                cellInfoUploadModel.setDownAngle(info.getDownAngle());
                cellInfoUploadModel.setElectricDownAngle(info.getElectricDownAngle());
                cellInfoUploadModel.setFrequency(info.getFrequency());
                cellInfoUploadModel.setHorizontalHalfPowerAngle(info.getHorizontalFalfPowerAngle());
                cellInfoUploadModel.setId(info.getId());
                cellInfoUploadModel.setMachineDownAngle(info.getMachineDownAngle());
                cellInfoUploadModel.setRootSequence(info.getRootSequence());
                cellInfoUploadModel.setSpecialSubframeMatching(info.getSpecialSubframeMatching());
                cellInfoUploadModel.setStationId(ssi.getStationId());
                cellInfoUploadModel.setSubframeMatching(info.getSubframeMatching());
                cellInfoUploadModel.setVerticalHalfPowerAngle(info.getVerticalFalfPowerAngle());
                cellInfoUploadModel.setLatitude(ssi.getLatitude());
                cellInfoUploadModel.setLongitude(ssi.getLongitude());
                cellInfoUploadModels.add(cellInfoUploadModel);
            }
            uploadModel.setCellInfoList(cellInfoUploadModels);
        }

        return uploadModel;
    }

}
