package com.walktour.gui.singlestation.test.model;

import com.walktour.base.gui.model.BaseCallBack;
import com.walktour.gui.singlestation.dao.model.StationInfo;

import java.util.List;

/**
 * 本地基站列表回调接口
 * Created by wangk on 2017/8/30.
 */

public interface LocalStationCallBack extends BaseCallBack {
    /**
     * 执行成功
     *
     * @param stationList 基站列表
     */
    void onSuccess(List<StationInfo> stationList);
}
