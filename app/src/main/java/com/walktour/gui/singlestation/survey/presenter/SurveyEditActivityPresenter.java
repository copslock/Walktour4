package com.walktour.gui.singlestation.survey.presenter;

import com.walktour.base.gui.presenter.BaseActivityPresenter;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.SurveyCellInfo;
import com.walktour.gui.singlestation.dao.model.SurveyStationInfo;
import com.walktour.gui.singlestation.survey.activity.SurveyEditActivity;
import com.walktour.gui.singlestation.survey.fragment.SurveyCellEditFragment;
import com.walktour.gui.singlestation.survey.fragment.SurveyEditBaseFragment;
import com.walktour.gui.singlestation.survey.fragment.SurveySiteEditFragment;
import com.walktour.gui.singlestation.survey.service.SurveyService;

import java.util.ArrayList;
import java.util.List;

/**
 * 勘查编辑界面交互类
 */
public class SurveyEditActivityPresenter extends BaseActivityPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "SurveyEditActivityPresenter";
    /**
     * 关联界面
     */
    private SurveyEditActivity mActivity;
    private SurveyService mSurveyService;

    public SurveyEditActivityPresenter(SurveyEditActivity activity) {
        super(activity);
        this.mActivity = activity;
        mSurveyService = new SurveyService(activity);
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    private List<SurveyEditBaseFragment> mFragmentList;


    /**
     * 获取初始化的视图列表
     *
     * @return 视图列表
     */
    public List<SurveyEditBaseFragment> getInitFragments() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new SurveySiteEditFragment());
        SurveyStationInfo stationInfo = this.getIntent().getParcelableExtra("survey_station_info");
        //小区参数
        for (int i = 0; i < stationInfo.getCellInfoList().size(); i++) {
            SurveyCellInfo cellInfo = stationInfo.getCellInfoList().get(i);
            if (cellInfo.getCellInfo() == null)
                continue;
            SurveyCellEditFragment fragment = new SurveyCellEditFragment();
            fragment.setSurveyCellInfo(cellInfo);
            fragment.setTitleName(this.mActivity.getString(R.string.single_station_survey_cell_parameters) + " " + (i + 1));
            mFragmentList.add(fragment);
            //室内基站只呈现一个小区参数页签
            if (stationInfo.getType() == SingleStationDaoManager.STATION_TYPE_INDOOR)
                break;
        }
        return mFragmentList;
    }

    /**
     * 编辑主界面退出时，保存已编辑数据
     */
    public void saveEditingData() {
        LogUtil.i(getLogTAG(), "---saveEditingData---");
        if (null != mFragmentList && !mFragmentList.isEmpty()) {
            for (SurveyEditBaseFragment fragment : mFragmentList) {
                if (fragment.getPresenter() != null) {
                    ((SurveyEditBaseFragmentPresenter) fragment.getPresenter()).saveEditingData();
                }
            }
        }
    }

    /**
     * 将当前勘察基站设置为勘察中
     */
    public void setSurveyStationEditing() {
        SurveyStationInfo surveyStationInfo = this.getIntent().getParcelableExtra("survey_station_info");
        if (surveyStationInfo != null && !surveyStationInfo.isEditing()) {
            surveyStationInfo.setIsEditing(true);
            mSurveyService.saveSurveyStation(surveyStationInfo);
        }
    }

    /**
     * 保存指定界面正在编辑的数据
     *
     * @param position 第几个界面
     */
    public void saveEditingData(int position) {
        LogUtil.i(getLogTAG(), "---saveEditingData---");
        if (null != mFragmentList && !mFragmentList.isEmpty() && mFragmentList.size() > position) {
            SurveyEditBaseFragment fragment = mFragmentList.get(position);
            ((SurveyEditBaseFragmentPresenter) fragment.getPresenter()).saveEditingData();
        }
    }

    public double getLongitude() {
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        return longitude;
    }

    public double getLatitude() {
        double latitude = getIntent().getDoubleExtra("latitude", 0);
        return latitude;
    }


}
