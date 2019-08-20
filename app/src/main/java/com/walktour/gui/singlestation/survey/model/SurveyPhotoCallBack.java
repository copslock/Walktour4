package com.walktour.gui.singlestation.survey.model;

import com.walktour.base.gui.model.BaseCallBack;
import com.walktour.gui.singlestation.dao.model.SurveyPhoto;

/**
 * 勘查照片回调类
 * Created by wangk on 2017/8/30.
 */

public interface SurveyPhotoCallBack extends BaseCallBack {
    /**
     * 执行成功
     *
     * @param surveyPhoto 勘查照片
     */
    void onSuccess(SurveyPhoto surveyPhoto);
}
