package com.walktour.gui.singlestation.test.presenter;

import android.content.Intent;
import android.view.MenuItem;

import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.SurveyStationInfo;
import com.walktour.gui.singlestation.survey.activity.SurveyEditActivity;
import com.walktour.gui.singlestation.test.activity.SceneTestActivity;
import com.walktour.gui.singlestation.test.activity.StationActivity;
import com.walktour.gui.singlestation.test.fragment.LocalStationMapFragment;
import com.walktour.gui.singlestation.test.model.LocalStationCallBack;
import com.walktour.gui.singlestation.test.model.SurveySiteCallBack;
import com.walktour.gui.singlestation.test.service.LocalStationService;

import java.util.List;

/**
 * 本地基站地图交互类
 * Created by wangk on 2017/6/15.
 */

public class LocalStationMapFragmentPresenter extends BaseFragmentPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "LocalStationMapFragmentPresenter";
    /**
     * 关联视图
     */
    private LocalStationMapFragment mFragment;
    /**
     * 关联业务类
     */
    private LocalStationService mService;

    public LocalStationMapFragmentPresenter(LocalStationMapFragment fragment, LocalStationService service) {
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
        if (item.getItemId() == R.id.menu_singlestation_show_list) {
            this.getActivity().showFragment(super.getIntent().getIntExtra("station_list_fragment_index", -1));
        }
    }

    @Override
    public void loadData() {
        double latitude = super.getIntent().getDoubleExtra("latitude", -9999);
        double longitude = super.getIntent().getDoubleExtra("longitude", -9999);
        if (latitude != -9999) {
            this.mFragment.setLocation(latitude, longitude);
        }
        this.mService.getLocalStationList(latitude, longitude, new LocalStationCallBack() {
            @Override
            public void onSuccess(List<StationInfo> stationList) {
                mFragment.showFragment(stationList);
            }

            @Override
            public void onFailure(String message) {
                showToast(message);
            }
        });
    }

    /**
     * 点击基站跳转到下个页面
     *
     * @param stationInfo 基站对象
     */
    public void jumpToNextActivity(StationInfo stationInfo) {
        if (this.getActivity() instanceof StationActivity)
            this.jumpToSceneTestActivity(stationInfo);
        else
            this.jumpToSurveyEditActivity(stationInfo);
    }

    /**
     * 跳转到场景测试界面
     *
     * @param stationInfo 基站对象
     */
    private void jumpToSceneTestActivity(StationInfo stationInfo) {
        Intent intent = new Intent(this.getActivity(), SceneTestActivity.class);
        intent.putExtra("station_info", stationInfo);
        this.mFragment.startActivity(intent);
    }

    /**
     * 跳转到基站勘查编辑界面
     *
     * @param stationInfo 基站对象
     */
    private void jumpToSurveyEditActivity(StationInfo stationInfo) {
        this.mService.getSurveyStation(stationInfo.getId(), new SurveySiteCallBack() {
            @Override
            public void onSuccess(SurveyStationInfo surveyStationInfo) {
                Intent intent = new Intent(mFragment.getContext(), SurveyEditActivity.class);
                intent.putExtra("survey_station_info", surveyStationInfo);
                mFragment.startActivity(intent);
            }

            @Override
            public void onFailure(String message) {
                showToast(message);
            }
        });
    }

}
