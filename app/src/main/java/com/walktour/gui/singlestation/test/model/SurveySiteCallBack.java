package com.walktour.gui.singlestation.test.model;

import com.walktour.base.gui.model.BaseCallBack;
import com.walktour.gui.singlestation.dao.model.SurveyStationInfo;

/**
 * 勘查基站信息回调接口
 * Created by wangk on 2017/8/30.
 */

public interface SurveySiteCallBack extends BaseCallBack {
    /**
     * 执行成功
     *
     * @param surveyStationInfo 勘查基站信息
     */
    void onSuccess(SurveyStationInfo surveyStationInfo);
}
