package com.walktour.gui.singlestation.net.model;

import com.walktour.base.gui.model.BaseCallBack;

/**
 * 平台获取到的基站测试配置回调接口
 * Created by wangk on 2017/8/30.
 */

public interface StationPlatformInfoCallBack extends BaseCallBack {
    /**
     * 执行成功
     *
     * @param stationPlatformInfo 平台获取到的基站测试配置
     */
    void onSuccess(StationPlatformInfo stationPlatformInfo);
}
