package com.walktour.gui.singlestation.test.presenter;

import android.content.Intent;
import android.view.MenuItem;

import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.SurveyStationInfo;
import com.walktour.gui.singlestation.survey.activity.SurveyActivity;
import com.walktour.gui.singlestation.survey.activity.SurveyEditActivity;
import com.walktour.gui.singlestation.survey.presenter.SurveyActivityPresenter;
import com.walktour.gui.singlestation.test.activity.ResultActivity;
import com.walktour.gui.singlestation.test.activity.SceneTestActivity;
import com.walktour.gui.singlestation.test.activity.StationActivity;
import com.walktour.gui.singlestation.test.fragment.LocalStationListFragment;
import com.walktour.gui.singlestation.test.model.LocalStationCallBack;
import com.walktour.gui.singlestation.test.model.SurveySiteCallBack;
import com.walktour.gui.singlestation.test.service.LocalStationService;

import java.util.Collections;
import java.util.List;

/**
 * 本地基站列表交互类
 * Created by wangk on 2017/6/15.
 */

public class LocalStationListFragmentPresenter extends BaseFragmentPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "LocalStationListFragmentPresenter";
    /**
     * 关联视图
     */
    private LocalStationListFragment mFragment;
    /**
     * 关联业务类
     */
    private LocalStationService mService;

    public LocalStationListFragmentPresenter(LocalStationListFragment fragment, LocalStationService service) {
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
            this.getActivity().showFragment(this.getIntent().getIntExtra("station_map_fragment_index", -1));
        }
    }

    @Override
    public void loadData() {
        double latitude = super.getIntent().getDoubleExtra("latitude", -9999);
        double longitude = super.getIntent().getDoubleExtra("longitude", -9999);
        this.mService.getLocalStationList(latitude, longitude, new LocalStationCallBack() {
            @Override
            public void onSuccess(List<StationInfo> stationList) {
                if(null != stationList && !stationList.isEmpty()){
                    //如果数据不为空，先按距离由近到远排序
                    Collections.sort(stationList);
                }
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
     * 点击基站跳转到下个页面
     *
     * @param stationInfo 基站对象
     */
    public void jumpToNextActivity(StationInfo stationInfo) {
        if (this.getActivity() instanceof StationActivity){
            this.jumpToSceneTestActivity(stationInfo);
        }
        else{
            double longitude = ((SurveyActivityPresenter)(((SurveyActivity)getActivity()).getPresenter())).getLongitude();
            double latitude = ((SurveyActivityPresenter)(((SurveyActivity)getActivity()).getPresenter())).getLatitude();
            this.jumpToSurveyEditActivity(stationInfo,longitude,latitude);
        }
    }

    /**
     * 跳转到场景测试界面
     *
     * @param stationInfo 基站对象
     */
    private void jumpToSceneTestActivity(final StationInfo stationInfo) {
        Intent intent = new Intent(getActivity(), SceneTestActivity.class);
        intent.putExtra("station_info", stationInfo);
        this.mFragment.startActivity(intent);
    }

    /**
     * 跳转到基站勘查编辑界面
     *
     * @param stationInfo 基站对象
     */
    private void jumpToSurveyEditActivity(StationInfo stationInfo, final double longitude, final double latitude) {
        this.mService.getSurveyStation(stationInfo.getId(), new SurveySiteCallBack() {
            @Override
            public void onSuccess(SurveyStationInfo surveyStationInfo) {
                Intent intent = new Intent(mFragment.getContext(), SurveyEditActivity.class);
                intent.putExtra("longitude",longitude);
                intent.putExtra("latitude",latitude);
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
