package com.walktour.gui.singlestation.survey.model;

import com.walktour.base.gui.model.BaseCallBack;
import com.walktour.gui.singlestation.dao.model.SurveyStationInfo;

/**
 * Created by yi.lin on 2017/9/4.
 *
 * 查询勘查基站回调接口
 */

public interface SurveyStationInfoCallback extends BaseCallBack {
    /**
     * 执行成功
     *
     * @param surveyStationInfo 勘查基站
     */
    void onSuccess(SurveyStationInfo surveyStationInfo);
}
