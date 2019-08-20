package com.walktour.gui.singlestation.survey.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.walktour.Utils.SurveyPhotoUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.base.gui.model.SimpleCallBack;
import com.walktour.base.util.DateUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.model.SurveyPhoto;
import com.walktour.gui.singlestation.dao.model.SurveyStationInfo;
import com.walktour.gui.singlestation.survey.activity.SurveyEditActivity;
import com.walktour.gui.singlestation.survey.fragment.SurveySiteEditFragment;
import com.walktour.gui.singlestation.survey.model.SurveyPhotoCallBack;
import com.walktour.gui.singlestation.survey.service.SurveyService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 基站勘查基站参数编辑交互类
 * Created by wangk on 2017/7/17.
 */

public class SurveySiteEditFragmentPresenter extends SurveyEditBaseFragmentPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "SurveySiteEditFragmentPresenter";
    /**
     * 关联视图
     */
    private SurveySiteEditFragment mFragment;

    /**
     * 当前勘查的基站对象
     */
    private SurveyStationInfo mSurveyStationInfo;

    /**
     * 拍照临时存储图片路径
     */
    private String mTempFilePath;

    public SurveySiteEditFragmentPresenter(SurveySiteEditFragment fragment, SurveyService service) {
        super(fragment, service);
        this.mFragment = fragment;
    }


    @Override
    protected String getLogTAG() {
        return TAG;
    }

    /**
     * 保存编辑数据
     */
    @Override
    public void saveEditingData() {
        LogUtil.i(getLogTAG(), "---saveEditingData---");
        String tac = TraceInfoInterface.getParaValue(0x7F060018);
        String eNodeBId = TraceInfoInterface.getParaValue(0x0A0050A2);
        //TAC从参数界面取
        this.mSurveyStationInfo.setTAC(TextUtils.isEmpty(tac) ? 0 : Integer.parseInt(tac));
        //eNodeBID从参数界面取
        this.mSurveyStationInfo.setENodeBID(TextUtils.isEmpty(eNodeBId) ? 0 : Integer.parseInt(eNodeBId));

        this.mSurveyStationInfo.setDistrict(mFragment.getEditSiteDistrict());
        this.mSurveyStationInfo.setCity(mFragment.getEditSiteCity());
        this.mSurveyStationInfo.setAddress(mFragment.getEditSiteAddress());
        this.mSurveyStationInfo.setConfigure(mFragment.getEditSiteConfiguration());
        this.mSurveyStationInfo.setTester(mFragment.getEditSiteTester());
        this.mSurveyStationInfo.setTestPhone(mFragment.getEditSiteTestPhone());
        this.mSurveyStationInfo.setTestPlatform(mFragment.getEditTestPlatform());
        this.mSurveyStationInfo.setTestDeviceModel(mFragment.getEditSiteTestDeviceModel());
        this.mSurveyStationInfo.setIsActualSiteHigherOK(mFragment.getEditSiteActualSiteHigher());
        this.mSurveyStationInfo.setIsActualSiteLowerOK(mFragment.getEditSiteActualSiteLower());
        this.mSurveyStationInfo.setIsAntennaInstallationHeightOK(mFragment.getEditSiteAntennaInstallationHeight());
        this.mSurveyStationInfo.setIsSpacingOfSitesOK(mFragment.getEditSiteSpacingOfSites());
        this.mSurveyStationInfo.setType(mFragment.getEditSiteType());
        this.mSurveyStationInfo.setDeviceType(mFragment.getEditSiteDeviceType());
        this.mSurveyStationInfo.setTestDeviceModel(mFragment.getEditSiteTestDeviceModel());
        updateTestDate(false);
        this.mService.saveSurveySiteEdit(this.mSurveyStationInfo, new SimpleCallBack() {
            @Override
            public void onSuccess() {
                showToast(R.string.single_station_survey_save_success);
            }

            @Override
            public void onFailure(String message) {
                showToast(R.string.single_station_survey_save_failure);
            }
        });
    }

    @Override
    public void loadData() {
        this.mSurveyStationInfo = super.getIntent().getParcelableExtra("survey_station_info");
        mService.getSurveyPhoto(this.mSurveyStationInfo.getStationId(), SurveyPhoto.PHOTO_TYPE_NORMAL, new SurveyPhotoCallBack() {
            @Override
            public void onSuccess(SurveyPhoto surveyPhoto) {
                if (surveyPhoto != null)
                    mFragment.showCaptureImageView(Uri.parse(surveyPhoto.getPhotoPath()));
            }

            @Override
            public void onFailure(String message) {

            }
        });
        this.mFragment.show(this.mSurveyStationInfo);
    }

    /**
     * 更新文本以及保存到数据库
     *
     * @param needSendQuery 是否需要构建查询参数
     */
    private void updateTestDate(boolean needSendQuery) {
        String testDate = DateUtil.formatDate(DateUtil.FORMAT_DATE_TIME, new Date());
        //更新界面
        mFragment.updateTestDate(testDate);
        //更新数据库
        this.mSurveyStationInfo.setTestDate(testDate);
        if (needSendQuery) {
            this.mService.saveSurveySiteEdit(this.mSurveyStationInfo, new SimpleCallBack() {
                @Override
                public void onSuccess() {
                    showToast(R.string.single_station_survey_save_success);
                }

                @Override
                public void onFailure(String message) {
                    showToast(R.string.single_station_survey_save_failure);
                }
            });
        }
    }


    public void toCaptureSurveyPhoto() {
        String photoName = "temp" + System.currentTimeMillis();
        mTempFilePath = SurveyPhotoUtil.getPhotoFileName(mSurveyStationInfo.getStationInfo().getName(), photoName);
        File file = new File(mTempFilePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mFragment.toCaptureSurveyPhoto(file);
    }

    public double getLongitude(){
        return ((SurveyEditActivityPresenter)(((SurveyEditActivity)getActivity()).getPresenter())).getLongitude();
    }
    public double getLatitude(){
        return ((SurveyEditActivityPresenter)(((SurveyEditActivity)getActivity()).getPresenter())).getLatitude();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == SurveySiteEditFragment.REQUEST_CAPTURE_IMAGE) {
            String photoName = "survey_" + SurveyPhoto.PHOTO_TYPE_NORMAL + "_" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());//图片名称：survey_photoType_时间戳
            String time = DateUtil.formatDate(DateUtil.FORMAT_DATE_TIME, new Date());
            SurveyPhotoUtil.Watermark watermarkTime = new SurveyPhotoUtil.Watermark(SurveyPhotoUtil.WatermarkPosition.RIGHT_BOTTOM, time);
            String location = getLongitude() + "  " + getLatitude();
            SurveyPhotoUtil.Watermark watermarkLocation = new SurveyPhotoUtil.Watermark(SurveyPhotoUtil.WatermarkPosition.LEFT_TOP, location);
            Uri uri = Uri.parse(SurveyPhotoUtil.fixRotateAndWatermarkDate2Photo(mSurveyStationInfo.getStationInfo().getName(), photoName, mTempFilePath, watermarkTime, watermarkLocation));
            mFragment.showCaptureImageView(uri);
            Long surveyStationId = mSurveyStationInfo.getStationId();
            SurveyPhoto surveyPhoto = new SurveyPhoto();
            surveyPhoto.setPhotoPath(String.valueOf(uri));
            surveyPhoto.setPhotoType(SurveyPhoto.PHOTO_TYPE_NORMAL);
            surveyPhoto.setSurveyStationId(surveyStationId);
            updateTestDate(true);
            this.mService.saveSurveyPhoto(surveyPhoto, new SimpleCallBack() {
                @Override
                public void onSuccess() {
                    SurveyPhotoUtil.deletePhoto(mTempFilePath);
                    showToast(R.string.single_station_survey_save_success);
                }

                @Override
                public void onFailure(String message) {
                    showToast(R.string.single_station_survey_save_failure);
                }
            });
        }
    }


}
