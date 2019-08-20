package com.walktour.gui.singlestation.net.model;

import com.walktour.base.gui.model.BaseCallBack;

/**
 * Created by yi.lin on 2017/9/5.
 * 上传勘察基站结果回调接口
 */

public interface UploadSurveyStationResultCallback extends BaseCallBack {

    void onSuccess(UploadSurveyStationResult result);

}
