package com.walktour.gui.singlestation.test.model;

import com.walktour.base.gui.model.BaseCallBack;

/**
 * 当前小区回调接口
 * Created by wangk on 2017/8/30.
 */

public interface CurrentCellCallBack extends BaseCallBack {
    /**
     * 执行成功
     *
     * @param eNodeBID eNodeBID
     * @param cellID   小区ID
     */
    void onSuccess(int eNodeBID, int cellID);
}
