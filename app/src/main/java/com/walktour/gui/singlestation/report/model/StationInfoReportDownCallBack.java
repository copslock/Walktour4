package com.walktour.gui.singlestation.report.model;
import com.walktour.base.gui.model.BaseCallBack;
import com.walktour.gui.singlestation.dao.model.StationInfo;

import java.util.List;
/***
 * 文件下载回调
 */
public interface StationInfoReportDownCallBack extends BaseCallBack {
    /**
     * 所有文件下载完成
     *
     * @param stationInfoList 查询获取到的基站信息
     */
    void onFinish(List<StationInfo> stationInfoList);
    /**
     * 单个文件下载成功
     * @param message
     */
    void onSuccess(String message);
}
