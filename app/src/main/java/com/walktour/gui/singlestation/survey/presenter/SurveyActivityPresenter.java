package com.walktour.gui.singlestation.survey.presenter;

import com.walktour.base.gui.presenter.BaseActivityPresenter;
import com.walktour.gui.singlestation.survey.activity.SurveyActivity;

/**
 * 基站勘察基站列表界面交互类
 */
public class SurveyActivityPresenter extends BaseActivityPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "SurveyActivityPresenter";
    /**
     * 关联界面
     */
    private SurveyActivity mActivity;

    /**
     * 经度
     */
    private double mLongitude;
    /**
     * 纬度
     */
    private double mLatitude;

    public SurveyActivityPresenter(SurveyActivity activity) {
        super(activity);
        this.mActivity = activity;
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
        this.getIntent().putExtra("longitude", this.mLongitude);
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
        this.getIntent().putExtra("latitude", this.mLatitude);
    }

    /**
     * 重新加载当前视图数据
     */
    public void loadData() {
        this.mActivity.loadCurrentFragmentData();
    }

}
