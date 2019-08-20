package com.walktour.gui.singlestation.survey.fragment;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.walktour.Utils.TraceInfoInterface;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.SurveyStationInfo;
import com.walktour.gui.singlestation.survey.component.DaggerSurveySiteEditFragmentComponent;
import com.walktour.gui.singlestation.survey.module.SurveySiteEditFragmentModule;
import com.walktour.gui.singlestation.survey.presenter.SurveySiteEditFragmentPresenter;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 基站勘查基站参数编辑视图
 * Created by wangk on 2017/7/17.
 */

public class SurveySiteEditFragment extends SurveyEditBaseFragment {
    /**
     * 日志标识
     */
    private static final String TAG = "SurveySiteEditFragment";

    /**
     * 基站名
     */
    @BindView(R.id.site_name)
    TextView mSiteName;
    /**
     * 基站号
     */
    @BindView(R.id.site_index)
    TextView mSiteIndex;
    /**
     * 站型(工参)
     */
    @BindView(R.id.site_type_old)
    TextView mSiteTypeOld;
    /**
     * 站型
     */
    @BindView(R.id.site_type)
    Spinner mSiteType;
    /**
     * 勘查日期
     */
    @BindView(R.id.site_date)
    TextView mSiteDate;
    /**
     * 区县
     */
    @BindView(R.id.site_district)
    EditText mSiteDistrict;
    /**
     * 城市
     */
    @BindView(R.id.site_city)
    EditText mSiteCity;
    /**
     * 地址(工参)
     */
    @BindView(R.id.site_address_old)
    TextView mSiteAddressOld;
    /**
     * 地址
     */
    @BindView(R.id.site_address)
    EditText mSiteAddress;
    /**
     * 设备类型(工参)
     */
    @BindView(R.id.site_device_type_old)
    TextView mSiteDeviceTypeOld;
    /**
     * 设备类型
     */
    @BindView(R.id.site_device_type)
    TextView mSiteDeviceType;
    /**
     * 配置(工参)
     */
    @BindView(R.id.site_configuration_old)
    TextView mSiteConfigurationOld;
    /**
     * 配置
     */
    @BindView(R.id.site_configuration)
    TextView mSiteConfiguration;
    /**
     * 天线所在建筑物的外观照
     */
    @BindView(R.id.site_photo)
    ImageButton mSitePhoto;


    /**
     * 拍照显示的图片
     */
    @BindView(R.id.iv_survey_image)
    ImageView mIvSurveyImage;

    /**
     * 经度(工参)
     */
    @BindView(R.id.site_longitude_old)
    TextView mSiteLongitudeOld;
    /**
     * 经度
     */
    @BindView(R.id.site_longitude)
    TextView mSiteLongitude;
    /**
     * 纬度(工参)
     */
    @BindView(R.id.site_latitude_old)
    TextView mSiteLatitudeOld;
    /**
     * 纬度
     */
    @BindView(R.id.site_latitude)
    TextView mSiteLatitude;
    /**
     * TAC(工参)
     */
    @BindView(R.id.site_tac_old)
    TextView mSiteTacOld;
    /**
     * TAC
     */
    @BindView(R.id.site_tac)
    EditText mSiteTac;
    /**
     * eNodeBID(工参)
     */
    @BindView(R.id.site_enodebid_old)
    TextView mSiteEnodebidOld;
    /**
     * eNodeBID
     */
    @BindView(R.id.site_enodebid)
    EditText mSiteEnodebid;
    /**
     * 测试人员
     */
    @BindView(R.id.site_tester)
    EditText mSiteTester;
    /**
     * 测试设备型号
     */
    @BindView(R.id.site_test_device_model)
    EditText mSiteTestDeviceModel;
    /**
     * 测试手机号码
     */
    @BindView(R.id.site_test_phone)
    EditText mSiteTestPhone;
    /**
     * 测试平台
     */
    @BindView(R.id.site_test_platform)
    EditText mSiteTestPlatform;
    /**
     * 实际站高超过理想站高（使用实际站间距查询上表得到）1.5倍
     */
    @BindView(R.id.site_actual_site_higher)
    Spinner mSiteActualSiteHigher;
    /**
     * 实际站高在理想站高范围内，但若实际下倾小于理想下倾（使用实际站间距查询上表得到） 3度以上，则可与LTE共址（天面），但必须新建独立天线。
     */
    @BindView(R.id.site_actual_site_lower)
    Spinner mSiteActualSiteLower;
    /**
     * 天线挂高是否大于50米
     */
    @BindView(R.id.site_antenna_installation_height)
    Spinner mSiteAntennaInstallationHeight;
    /**
     * 基站间距是否小于100米
     */
    @BindView(R.id.site_spacing_of_sites)
    Spinner mSiteSpacingOfSites;
    /**
     * 界面交互类
     */
    @Inject
    SurveySiteEditFragmentPresenter mPresenter;

    /**
     * 拍照请求码
     */
    public static final int REQUEST_CAPTURE_IMAGE = 201;

    public SurveySiteEditFragment() {
        super(R.string.single_station_survey_site_parameters, R.layout.fragment_single_station_survey_station_edit);
    }

    @Override
    public String getLogTAG() {
        return TAG;
    }

    /**
     * 获取输入站型
     *
     * @return
     */
    public int getEditSiteType() {
        int siteType = mSiteType.getSelectedItemPosition() == 0 ? SingleStationDaoManager.STATION_TYPE_INDOOR : SingleStationDaoManager.STATION_TYPE_OUTDOOR;
        return siteType;
    }

    /**
     * @return 最后编辑时间
     */
    public String getEditSiteDate() {
        return getTextContent(mSiteDate);
    }

    /**
     * @return 地市
     */
    public String getEditSiteCity() {
        return getTextContent(mSiteCity);
    }

    /**
     * @return 区县
     */
    public String getEditSiteDistrict() {
        return getTextContent(mSiteDistrict);
    }

    /**
     * @return 地址
     */
    public String getEditSiteAddress() {
        return getTextContent(mSiteAddress);
    }

    /**
     * @return 设备类型
     */
    public String getEditSiteDeviceType() {
        return getTextContent(mSiteDeviceType);
    }

    /**
     * @return 配置
     */
    public String getEditSiteConfiguration() {
        return getTextContent(mSiteConfiguration);
    }

    /**
     * @return TAC
     */
    public String getEditSiteTAC() {
        return getTextContent(mSiteTac);
    }

    /**
     * @return eNodeBID
     */
    public String getEditSiteENodeBID() {
        return getTextContent(mSiteEnodebid);
    }

    /**
     * @return siteActualSiteHigher
     */
    public boolean getEditSiteActualSiteHigher() {
        boolean siteActualSiteHigher = this.mSiteActualSiteHigher.getSelectedItemPosition() == 0;
        return siteActualSiteHigher;
    }

    /**
     * @return siteActualSiteLower
     */
    public boolean getEditSiteActualSiteLower() {
        boolean siteActualSiteLower = this.mSiteActualSiteLower.getSelectedItemPosition() == 0;
        return siteActualSiteLower;
    }

    /**
     * @return siteAntennaInstallationHeight
     */
    public boolean getEditSiteAntennaInstallationHeight() {
        boolean siteAntennaInstallationHeight = this.mSiteAntennaInstallationHeight.getSelectedItemPosition() == 0;
        return siteAntennaInstallationHeight;
    }

    /**
     * @return siteSpacingOfSites
     */
    public boolean getEditSiteSpacingOfSites() {
        boolean siteSpacingOfSites = this.mSiteSpacingOfSites.getSelectedItemPosition() == 0;
        return siteSpacingOfSites;
    }

    /**
     * @return 测试人员
     */
    public String getEditSiteTester() {
        return getTextContent(mSiteTester);
    }

    /**
     * @return 测试设备型号
     */
    public String getEditSiteTestDeviceModel() {
        return getTextContent(mSiteTestDeviceModel);
    }

    /**
     * @return 测试手机号码
     */
    public String getEditSiteTestPhone() {
        return getTextContent(mSiteTestPhone);
    }

    /**
     * @return 测试平台
     */
    public String getEditTestPlatform() {
        return getTextContent(mSiteTestPlatform);
    }

    /**
     * 显示勘查的基站参数
     *
     * @param surveyStationInfo 勘查的基站参数
     */
    public void show(SurveyStationInfo surveyStationInfo) {
        StationInfo stationInfo = surveyStationInfo.getStationInfo();
        this.mSiteName.setText(stationInfo.getName());
        this.mSiteIndex.setText(stationInfo.getCode());
        this.mSiteTypeOld.setText(this.getOldValueShow(stationInfo.getType() == SingleStationDaoManager.STATION_TYPE_INDOOR ? getString(R.string.single_station_type_indoor) : getString(R.string.single_station_type_outdoor)));
        this.mSiteType.setSelection(surveyStationInfo.getType() == SingleStationDaoManager.STATION_TYPE_INDOOR ? 0 : 1);
        this.mSiteAddressOld.setText(this.getOldValueShow(stationInfo.getAddress()));
        this.mSiteConfigurationOld.setText(this.getOldValueShow(stationInfo.getConfigure()));
        this.mSiteDeviceTypeOld.setText(this.getOldValueShow(stationInfo.getDeviceType()));
        this.mSiteEnodebidOld.setText(this.getOldValueShow(String.valueOf(stationInfo.getENodeBID())));
        this.mSiteLatitudeOld.setText(this.getOldValueShow(String.valueOf(stationInfo.getLatitude())));
        this.mSiteLongitudeOld.setText(this.getOldValueShow(String.valueOf(stationInfo.getLongitude())));
        this.mSiteTacOld.setText(this.getOldValueShow(String.valueOf(stationInfo.getTAC())));
        this.mSiteAddress.setText(surveyStationInfo.getAddress());
        this.mSiteCity.setText(surveyStationInfo.getCity());
        this.mSiteConfiguration.setText(surveyStationInfo.getConfigure());
        this.mSiteDeviceType.setText(surveyStationInfo.getDeviceType());
        this.mSiteDate.setText(surveyStationInfo.getTestDate());
        this.mSiteDistrict.setText(surveyStationInfo.getDistrict());
        this.mSiteEnodebid.setText(TraceInfoInterface.getParaValue(0x0A0050A2));
        this.mSiteLatitude.setText(String.valueOf(mPresenter.getLatitude()));
        this.mSiteLongitude.setText(String.valueOf(mPresenter.getLongitude()));
        this.mSiteTac.setText(TraceInfoInterface.getParaValue(0x7F060018));
        this.mSiteTestDeviceModel.setText(surveyStationInfo.getTestDeviceModel());
        this.mSiteTester.setText(surveyStationInfo.getTester());
        this.mSiteTestPhone.setText(surveyStationInfo.getTestPhone());
        this.mSiteTestPlatform.setText(surveyStationInfo.getTestPlatform());
        this.mSiteActualSiteHigher.setSelection(surveyStationInfo.getIsActualSiteHigherOK() ? 0 : 1);
        this.mSiteActualSiteLower.setSelection(surveyStationInfo.getIsActualSiteLowerOK() ? 0 : 1);
        this.mSiteAntennaInstallationHeight.setSelection(surveyStationInfo.getIsAntennaInstallationHeightOK() ? 0 : 1);
        this.mSiteSpacingOfSites.setSelection(surveyStationInfo.getIsSpacingOfSitesOK() ? 0 : 1);
    }


    /**
     * 更新日期文本内容
     *
     * @param date
     */
    public void updateTestDate(String date) {
        mSiteDate.setText(date);
    }


    @OnClick(R.id.site_photo)
    public void onCaptureSurveyImage() {
        //拍照
        mPresenter.toCaptureSurveyPhoto();
    }


    /**
     * 拍照图片显示
     */
    public void showCaptureImageView(Uri uri) {
        mIvSurveyImage.setVisibility(View.VISIBLE);
        mIvSurveyImage.setImageURI(uri);
    }


    /**
     * 跳转到系统拍照界面
     *
     * @param file
     */
    public void toCaptureSurveyPhoto(File file) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        getActivity().startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
    }

    @Override
    public BaseFragmentPresenter getPresenter() {
        return this.mPresenter;
    }

    @Override
    protected void setupFragmentComponent() {
        DaggerSurveySiteEditFragmentComponent.builder().surveySiteEditFragmentModule(new SurveySiteEditFragmentModule(this)).build().inject(this);
    }

    @Override
    protected void onCreateView() {

    }

}
