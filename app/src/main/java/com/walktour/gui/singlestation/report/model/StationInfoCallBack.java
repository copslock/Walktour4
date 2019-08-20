package com.walktour.gui.singlestation.report.model;

import com.walktour.base.gui.model.BaseCallBack;
import com.walktour.gui.singlestation.dao.model.StationInfo;

import java.util.List;

/**
 * 查询获取到的基站信息回调类
 * Created by wangk on 2017/8/30.
 */

public interface StationInfoCallBack extends BaseCallBack {

    /**
     * 执行成功
     *
     * @param stationInfoList 查询获取到的基站信息
     */
    void onSuccess(List<StationInfo> stationInfoList);

}
