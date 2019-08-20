package com.walktour.gui.singlestation.test.activity;

import com.walktour.base.gui.activity.SimpleBaseActivity;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.test.fragment.StationResultFragment;

/**
 * 基站测试结果显示界面
 * Created by wangk on 2017/6/20.
 */

public class ResultActivity extends SimpleBaseActivity {
    /**
     * 日志标识
     */
    private static final String TAG = "ResultActivity";

    @Override
    protected void onCreate() {
        StationInfo stationInfo = this.getIntent().getParcelableExtra("station_info");
        super.setToolbarTitle(stationInfo.getName());
    }

    @Override
    protected void initFragments() {
        super.addFragment(new StationResultFragment());
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

}
