package com.walktour.gui.singlestation.report.service;

import android.content.Context;

import com.walktour.Utils.FileUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.StationInfoReport;
import com.walktour.gui.singlestation.report.model.StationInfoAndReport;
import com.walktour.gui.singlestation.report.model.StationInfoAndReportCallBack;

import java.util.LinkedList;
import java.util.List;

/**
 * 报告历史业务类
 * Created by andy-fan on 2017-07-17.
 */

public class HistoryReportService {
    /**
     * 日志标识
     */
    private static final String TAG = "HistoryReportService";
    /**
     * 数据库操作类
     */
    private SingleStationDaoManager mDaoManager;

    public HistoryReportService(Context context) {
        this.mDaoManager = SingleStationDaoManager.getInstance(context);
    }

    /***
     * 获取已存在测试报告的基站信息
     * @param callBack 回调类
     */
    public void fetchTestedStationInfo(StationInfoAndReportCallBack callBack) {
        List<StationInfo> list = this.mDaoManager.getStationInfoList();
        List<StationInfoAndReport> reportList = new LinkedList<>();
        try {
            for (StationInfo stationInfo : list) {
                for (StationInfoReport stationInfoReport : stationInfo.getReportList()) {
                    StationInfoAndReport report = new StationInfoAndReport();
                    report.setStationInfo(stationInfo);
                    report.setStationInfoReport(stationInfoReport);
                    reportList.add(report);
                }
            }
            callBack.onSuccess(reportList);
        } catch (Exception ex) {
            ex.printStackTrace();
            callBack.onFailure(ex.getMessage());
            LogUtil.e(TAG, ex.getMessage());
        }
    }

    /**
     * 删除测试报告
     *
     * @param reportList 删除的报告列表
     * @param callBack   回调类
     */
    public void deleteReport(List<StationInfoAndReport> reportList, StationInfoAndReportCallBack callBack) {
        try {
            for (StationInfoAndReport report : reportList) {
                //删除报告
                this.mDaoManager.delete(report.getStationInfoReport());
                //同时删除文件
                FileUtil.deleteFile(report.getStationInfoReport().getReportPath());
                //如果基站所有报告都清除,将基站改为没生成报告
                StationInfo stationInfo = this.mDaoManager.getStationInfo(report.getStationInfo().getId());
                List<StationInfoReport> stationInfoReports = stationInfo.getReportList();
                if (null == stationInfoReports || stationInfoReports.size() <= 0) {
                    stationInfo.setIsExportedReport(StationInfo.EXPORTED_REPORT_NO);
                    this.mDaoManager.save(stationInfo);
                }
            }
            this.fetchTestedStationInfo(callBack);
        } catch (Exception ex) {
            ex.printStackTrace();
            LogUtil.e(TAG, ex.getMessage());
            callBack.onFailure(ex.getMessage());
        }
    }

}
