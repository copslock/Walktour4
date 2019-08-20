package com.walktour.gui.singlestation.net.model;

import com.walktour.base.gui.model.BaseCallBack;

import java.util.List;

/**
 * 平台返回的查询基站信息回调接口
 * Created by wangk on 2017/8/30.
 */

public interface StationSearchCallBack extends BaseCallBack {
    /**
     * 执行成功
     *
     * @param stationList 基站列表
     */
    void onSuccess(List<StationSearch> stationList);
}
