package com.walktour.gui.singlestation.survey.presenter;

import android.content.Intent;
import android.view.MenuItem;

import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.SurveyStationInfo;
import com.walktour.gui.singlestation.survey.activity.SurveyActivity;
import com.walktour.gui.singlestation.survey.activity.SurveyEditActivity;
import com.walktour.gui.singlestation.survey.fragment.SurveyHistoryFragment;
import com.walktour.gui.singlestation.survey.model.SurveySiteHistoryCallBack;
import com.walktour.gui.singlestation.survey.model.SurveyStationInfoCallback;
import com.walktour.gui.singlestation.survey.service.SurveyService;
import com.walktour.gui.singlestation.test.activity.ResultActivity;

import java.util.List;

/**
 * 基站勘查历史列表交互类
 * Created by wangk on 2017/6/15.
 */

public class SurveyHistoryFragmentPresenter extends BaseFragmentPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "SurveyHistoryFragmentPresenter";
    /**
     * 关联视图
     */
    private SurveyHistoryFragment mFragment;
    /**
     * 关联业务类
     */
    private SurveyService mService;

    public SurveyHistoryFragmentPresenter(SurveyHistoryFragment fragment, SurveyService service) {
        super(fragment);
        this.mFragment = fragment;
        this.mService = service;
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    @Override
    public void onOptionsItemSelected(MenuItem item) {
        LogUtil.d(TAG, "----onOptionsItemSelected----");
        if (item.getItemId() == R.id.menu_singlestation_show_map) {
            this.getActivity().showFragment(this.getIntent().getIntExtra("station_list_fragment_index", -1));
        }
    }

    @Override
    public void loadData() {
        this.mService.getEditingSurveyStationList(new SurveySiteHistoryCallBack() {
            @Override
            public void onSuccess(List<SurveyStationInfo> stationList) {
                mFragment.showFragment(stationList);
            }

            @Override
            public void onFailure(String message) {
                showToast(message);
            }
        });
    }

    /**
     * 跳转到测试情况界面
     *
     * @param stationInfo 基站对象
     */
    public void jumpToResultActivity(StationInfo stationInfo) {
        Intent intent = new Intent(this.getActivity(), ResultActivity.class);
        intent.putExtra("station_info", stationInfo);
        this.mFragment.startActivity(intent);
    }

    /**
     * 跳转到基站勘查编辑界面
     *
     * @param stationInfo 基站对象
     */
    public void jumpToSurveyEditActivity(SurveyStationInfo stationInfo) {
        double longitude = ((SurveyActivityPresenter) (((SurveyActivity) getActivity()).getPresenter())).getLongitude();
        double latitude = ((SurveyActivityPresenter) (((SurveyActivity) getActivity()).getPresenter())).getLatitude();
        final Intent intent = new Intent(this.getActivity(), SurveyEditActivity.class);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        mService.getSurveyStationInfo(stationInfo.getStationId(), new SurveyStationInfoCallback() {
            @Override
            public void onSuccess(SurveyStationInfo surveyStationInfo) {
                intent.putExtra("survey_station_info", surveyStationInfo);
                mFragment.startActivity(intent);
            }

            @Override
            public void onFailure(String message) {
                //no-op
            }
        });

    }
}
