package com.walktour.gui.singlestation.report.model;

import com.walktour.base.gui.model.BaseCallBack;

import java.util.List;

/**
 * 获取基站生成的报告信息回调函数
 * Created by wangk on 2017/8/30.
 */

public interface StationInfoAndReportCallBack extends BaseCallBack {
    /**
     * 执行成功
     *
     * @param stationInfoAndReportList 报告信息列表
     */
    void onSuccess(List<StationInfoAndReport> stationInfoAndReportList);
}
