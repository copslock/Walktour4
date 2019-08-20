package com.walktour.gui.singlestation.test.presenter;

import com.walktour.Utils.ApplicationModel;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.model.CellInfo;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.test.fragment.SceneTestCellFragment;
import com.walktour.gui.singlestation.test.model.CurrentCellCallBack;
import com.walktour.gui.singlestation.test.service.SceneTestService;

/**
 * 小区测试交互类
 * Created by wangk on 2017/6/15.
 */

public class SceneTestCellFragmentPresenter extends SceneTestBaseFragmentPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "SceneTestCellFragmentPresenter";
    /**
     * 关联视图
     */
    private SceneTestCellFragment mFragment;

    public SceneTestCellFragmentPresenter(SceneTestCellFragment fragment, ApplicationModel applicationModel, SceneTestService service) {
        super(fragment, applicationModel, service);
        this.mFragment = fragment;
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    @Override
    public void loadData() {
        this.showTestCell();
        super.loadSceneData();
        this.getCurrentCell();
        super.showStartButton();
    }

    /**
     * 显示测试的小区信息
     */
    private void showTestCell() {
        StationInfo stationInfo = super.getIntent().getParcelableExtra("station_info");
        this.mFragment.showTestCell(stationInfo.getENodeBID(), this.mFragment.getCellInfo().getCellId());
    }

    /**
     * 获取当前的小区
     */
    private void getCurrentCell() {
        super.mService.getCurrentCell(new CurrentCellCallBack() {
            @Override
            public void onSuccess(int eNodeBID, int cellID) {
                CellInfo cellInfo = mFragment.getCellInfo();
                StationInfo stationInfo = getIntent().getParcelableExtra("station_info");
                int color = (eNodeBID == stationInfo.getENodeBID() && cellID == cellInfo.getCellId()) ? R.color.green : R.color.red;
                mFragment.showCurrentCell(eNodeBID, cellID, color);
            }

            @Override
            public void onFailure(String message) {
                showToast(message);
            }
        });
    }

}
