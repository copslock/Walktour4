package com.walktour.gui.singlestation.test.presenter;

import com.walktour.base.gui.model.TreeNode;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.test.fragment.StationResultFragment;
import com.walktour.gui.singlestation.test.model.StationTestResultCallBack;
import com.walktour.gui.singlestation.test.service.LocalStationService;

import java.util.List;

/**
 * 本地基站测试情况列表交互类
 * Created by wangk on 2017/6/15.
 */

public class StationResultFragmentPresenter extends BaseFragmentPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "StationResultFragmentPresenter";
    /**
     * 关联视图
     */
    private StationResultFragment mFragment;
    /**
     * 关联业务类
     */
    private LocalStationService mService;

    public StationResultFragmentPresenter(StationResultFragment fragment, LocalStationService service) {
        super(fragment);
        this.mFragment = fragment;
        this.mService = service;
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    @Override
    public void loadData() {
        StationInfo stationInfo = super.getIntent().getParcelableExtra("station_info");
        this.mService.getStationTestResultList(this.mFragment.getContext(), stationInfo.getId(), new StationTestResultCallBack() {
            @Override
            public void onSuccess(List<TreeNode> resultList) {
                mFragment.showFragment(resultList);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }
}
