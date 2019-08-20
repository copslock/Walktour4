package com.walktour.base.gui.model;

/**
 * 简单的回调类，用于传递执行失败
 * Created by wangk on 2017/8/30.或成功的信息
 */

public interface SimpleCallBack extends BaseCallBack {
    /**
     * 执行成功
     */
    void onSuccess();

}
