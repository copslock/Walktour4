package com.walktour.base.gui.model;

/**
 * 基础执行回调接口
 * Created by wangk on 2017/8/30.
 */

public interface BaseCallBack {
    /**
     * 执行失败
     *
     * @param message 失败信息
     */
    void onFailure(String message);

}
