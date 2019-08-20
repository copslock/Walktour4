package com.walktour.gui.singlestation.test.presenter;

import android.graphics.Color;

import com.walktour.Utils.ApplicationModel;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.test.fragment.SceneTestStationFragment;
import com.walktour.gui.singlestation.test.model.CurrentStationCallBack;
import com.walktour.gui.singlestation.test.service.SceneTestService;

/**
 * 基站测试交互类，包括（覆盖、切换、外泄、停车场）
 * Created by wangk on 2017/6/15.
 */

public class SceneTestStationFragmentPresenter extends SceneTestBaseFragmentPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "SceneTestStationFragmentPresenter";
    /**
     * 基站测试视图
     */
    private SceneTestStationFragment mFragment;

    public SceneTestStationFragmentPresenter(SceneTestStationFragment fragment, ApplicationModel applicationModel, SceneTestService service) {
        super(fragment, applicationModel, service);
        this.mFragment = fragment;
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    /**
     * 显示测试基站信息
     */
    private void showTestSation() {
        StationInfo stationInfo = super.getIntent().getParcelableExtra("station_info");
        this.mFragment.showTestStation(stationInfo.getENodeBID());
    }

    @Override
    public void loadData() {
        this.showTestSation();
        super.loadSceneData();
        this.getCurrentStation();
        super.showStartButton();
    }

    /**
     * 获取当前的基站
     */
    private void getCurrentStation() {
        this.mService.getCurrentStation(new CurrentStationCallBack() {
            @Override
            public void onSuccess(int eNodeBID) {
                StationInfo stationInfo = getIntent().getParcelableExtra("station_info");
                int color = eNodeBID == stationInfo.getENodeBID() ? Color.parseColor("#00A3E6") : Color.parseColor("#FF0000");
                mFragment.showCurrentStation(eNodeBID, color);
            }

            @Override
            public void onFailure(String message) {
                showToast(message);
            }
        });
    }

}
