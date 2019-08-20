package com.walktour.gui.singlestation.test.model;

import com.walktour.base.gui.model.BaseCallBack;

/**
 * 当前基站回调接口
 * Created by wangk on 2017/8/30.
 */

public interface CurrentStationCallBack extends BaseCallBack {
    /**
     * 执行成功
     *
     * @param eNodeBID eNodeBID
     */
    void onSuccess(int eNodeBID);
}
