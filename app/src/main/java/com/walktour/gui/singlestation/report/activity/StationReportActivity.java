package com.walktour.gui.singlestation.report.activity;

import com.walktour.base.gui.activity.SimpleTabHostActivity;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.report.fragment.HistoryReportFragment;
import com.walktour.gui.singlestation.report.fragment.TestedStationFragment;

/**
 * 测试报告列表界面
 */
public class StationReportActivity extends SimpleTabHostActivity {
    /**
     * 日志标识
     */
    private static final String TAG = "StationReportActivity";

    @Override
    protected void onCreate() {
        super.setToolbarTitle(R.string.single_station_report);
    }

    @Override
    protected void initFragments() {
        //已测试基站
        super.addFragment(new TestedStationFragment());
        //历史报告
        super.addFragment(new HistoryReportFragment());
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

}
