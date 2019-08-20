package com.walktour.gui.singlestation.report.presenter;
import android.text.TextUtils;

import com.walktour.Utils.StringUtil;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.report.fragment.TestedStationFragment;
import com.walktour.gui.singlestation.report.model.StationInfoCallBack;
import com.walktour.gui.singlestation.report.model.StationInfoReportDownCallBack;
import com.walktour.gui.singlestation.report.service.TestedStationService;

import java.util.LinkedList;
import java.util.List;
public class TestedStationFragmentPresenter extends BaseFragmentPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "TestedStationFragmentPresenter";
    private TestedStationFragment mFragment;
    private TestedStationService mService;
    /**
     * 界面已选择的基站信息
     */
    private List<StationInfo> selectedStationInfo = new LinkedList<>();
    /**
     * 服务管理类
     */
    private ServerManager mServerManager;
    public TestedStationFragmentPresenter(TestedStationFragment mFragment, TestedStationService service) {
        super(mFragment);
        selectedStationInfo.clear();
        this.mFragment = mFragment;
        this.mService = service;
        mServerManager = ServerManager.getInstance(this.getActivity());
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    @Override
    public void loadData() {
        this.mService.fetchTestedStationInfo(new StationInfoCallBack() {
            @Override
            public void onSuccess(List<StationInfo> stationInfoList) {
                mFragment.showFragment(stationInfoList);
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
    public boolean existStationInfo(StationInfo data) {
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
    public void addStationInfo(StationInfo data) {
        selectedStationInfo.add(data);
    }

    /***
     * 取消选择基站
     * @param data
     */
    public void removetationInfo(StationInfo data) {
        selectedStationInfo.remove(data);
    }

    /**
     * 获取选择的数据
     *
     * @return
     */
    public List<StationInfo> getSelectedStationInfo() {
        return selectedStationInfo;
    }

    /***
     *  生成远程报告
     *
     */
    public void fetchRemoteReport() {
        String ip = this.mServerManager.getDownloadFleetIp();
        if (!this.mServerManager.getFleetServerType() || StringUtil.isNullOrEmpty(ip)) {
            super.showToast(R.string.work_order_fleet_ip_null);
            return;
        }
        final int port= this.mServerManager.getDownloadFleetPort();
        showProgressDialog("", this.getActivity().getString(R.string.single_station_fetching_platform_report), true, false);
        this.mService.exportRemoteReport(this.mFragment.getContext(),ip,port, this.selectedStationInfo, new StationInfoReportDownCallBack() {
            int count=selectedStationInfo.size();
            @Override
            public void onFinish(List<StationInfo> stationInfoList) {
                    dismissProgressDialog();
                    mFragment.showFragment(stationInfoList);
                    showToast(R.string.download_file_finish);
            }
            @Override
            public void onSuccess(String message) {
                count-=1;
                if(count<=0){
                    finish();
                }
            }
            @Override
            public void onFailure(String message) {
                count-=1;
                if(count<=0) {
                    finish();
                }else {
                    showToast(!TextUtils.isEmpty(message) ? message : getActivity().getString(R.string.single_station_fetch_platform_report_failure));

                }
            }
            /**
             * 下载完成
             */
            private void finish(){
                mService.fetchTestedStationInfo(new StationInfoCallBack(){
                    @Override
                    public void onSuccess(List<StationInfo> stationInfoList) {
                        onFinish(stationInfoList);
                    }
                    @Override
                    public void onFailure(String message) {
                    }
                });
            }
        });
    }

    /***
     *  生成远程报告
     *
     */
    public void fetchLocalReport() {
        this.mService.exportLocalReport(this.mFragment.getContext(), this.selectedStationInfo, new StationInfoCallBack() {
            @Override
            public void onSuccess(List<StationInfo> stationInfoList) {
                mFragment.showFragment(stationInfoList);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

}
