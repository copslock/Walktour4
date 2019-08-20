package com.walktour.gui.singlestation.survey.model;

import com.walktour.base.gui.model.BaseCallBack;
import com.walktour.gui.singlestation.dao.model.SurveyStationInfo;

import java.util.List;

/**
 * 勘查基站回调类
 * Created by wangk on 2017/8/30.
 */

public interface SurveySiteHistoryCallBack extends BaseCallBack {
    /**
     * 执行成功
     *
     * @param stationList 勘查基站列表
     */
    void onSuccess(List<SurveyStationInfo> stationList);
}
