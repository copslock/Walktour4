package com.walktour.gui.singlestation.report.model;

import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.StationInfoReport;


/***
 * 基站对应的报告信息
 *
 */
public class StationInfoAndReport {
    /**
     * 基站信息
     */
    private StationInfo stationInfo ;

    /**
     * 基站报告信息
     */
    private StationInfoReport stationInfoReport;

    public StationInfo getStationInfo() {
        return stationInfo;
    }

    public void setStationInfo(StationInfo stationInfo) {
        this.stationInfo = stationInfo;
    }

    public StationInfoReport getStationInfoReport() {
        return stationInfoReport;
    }

    public void setStationInfoReport(StationInfoReport stationInfoReport) {
        this.stationInfoReport = stationInfoReport;
    }
}
