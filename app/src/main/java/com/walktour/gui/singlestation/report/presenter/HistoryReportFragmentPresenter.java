package com.walktour.gui.singlestation.report.presenter;

import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.gui.singlestation.report.fragment.HistoryReportFragment;
import com.walktour.gui.singlestation.report.model.StationInfoAndReport;
import com.walktour.gui.singlestation.report.model.StationInfoAndReportCallBack;
import com.walktour.gui.singlestation.report.service.HistoryReportService;

import java.util.LinkedList;
import java.util.List;

public class HistoryReportFragmentPresenter extends BaseFragmentPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "HistoryReportFragmentPresenter";
    private HistoryReportFragment mFragment;
    private HistoryReportService mService;
    /**
     * 界面已选择的基站信息
     */
    private List<StationInfoAndReport> selectedStationInfo = new LinkedList<>();

    public HistoryReportFragmentPresenter(HistoryReportFragment mFragment, HistoryReportService service) {
        super(mFragment);
        this.mFragment = mFragment;
        this.mService = service;
        selectedStationInfo.clear();
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    @Override
    public void loadData() {
        this.mService.fetchTestedStationInfo(new StationInfoAndReportCallBack() {
            @Override
            public void onSuccess(List<StationInfoAndReport> stationInfoAndReportList) {
                mFragment.showFragment(stationInfoAndReportList);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    /**
     * 是否存在此基站
     *
     * @param data
     * @return
     */
    public boolean existStationInfo(StationInfoAndReport data) {
        if (selectedStationInfo.contains(data)) {
            return true;
        } else {
            return false;
        }

    }

    /***
     * 选择基站
     * @param data
     */
    public void addStationInfo(StationInfoAndReport data) {
        selectedStationInfo.add(data);
    }

    /***
     * 取消选择基站
     * @param data
     */
    public void removetationInfo(StationInfoAndReport data) {
        selectedStationInfo.remove(data);
    }

    /**
     * 批量删除报告
     */
    public void deleteReport() {
        this.mService.deleteReport(selectedStationInfo, new StationInfoAndReportCallBack() {
            @Override
            public void onSuccess(List<StationInfoAndReport> stationInfoAndReportList) {
                mFragment.showFragment(stationInfoAndReportList);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }
}
