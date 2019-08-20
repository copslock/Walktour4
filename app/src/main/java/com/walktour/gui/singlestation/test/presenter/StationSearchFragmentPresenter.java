package com.walktour.gui.singlestation.test.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;

import com.walktour.Utils.StringUtil;
import com.walktour.base.gui.activity.BaseTabHostActivity;
import com.walktour.base.gui.activity.FileChooseActivity;
import com.walktour.base.gui.model.SimpleCallBack;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.Verify;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.R;
import com.walktour.gui.setting.Sys;
import com.walktour.gui.singlestation.net.model.StationSearch;
import com.walktour.gui.singlestation.net.model.StationSearchCallBack;
import com.walktour.gui.singlestation.test.fragment.LoginServerDialogFragment;
import com.walktour.gui.singlestation.test.fragment.StationSearchFragment;
import com.walktour.gui.singlestation.test.service.StationSearchService;

import java.util.List;

/**
 * 基站查询列表交互类
 * Created by wangk on 2017/6/15.
 */

public class StationSearchFragmentPresenter extends BaseFragmentPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "StationSearchFragmentPresenter";
    /**
     * 文件选择请求代码
     */
    private static final int FILE_CHOOSE_REQUEST_CODE = 1001;
    /**
     * 关联视图
     */
    private StationSearchFragment mFragment;
    /**
     * 关联业务类
     */
    private StationSearchService mService;
    /**
     * 服务管理类
     */
    private ServerManager mServerManager;

    public StationSearchFragmentPresenter(StationSearchFragment fragment, StationSearchService service) {
        super(fragment);
        this.mFragment = fragment;
        this.mService = service;
        this.mServerManager = ServerManager.getInstance(this.getActivity());
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    @Override
    public void onOptionsItemSelected(MenuItem item) {
        /*if (item.getItemId() == R.id.menu_singlestation_import_station) {
            this.selectStationFile();
        } else */if (item.getItemId() == R.id.menu_singlestation_login) {
            this.showLoginServerDialog();
        }
    }

    @Override
    public void dealDialogCallBackValues(Bundle bundle) {
        String loginUser = bundle.getString("login_user");
        String loginPassword = bundle.getString("login_password");
        String ip = this.mServerManager.getDownloadFleetIp();
        int port = this.mServerManager.getDownloadFleetPort();
        this.mServerManager.setFleetAccount(loginUser);
        this.mServerManager.setFleetPassword(loginPassword);
        this.mService.loginServer(this.mFragment.getContext(), ip, port, loginUser, loginPassword, new SimpleCallBack() {
            @Override
            public void onSuccess() {
                showToast(R.string.single_station_login_success);
            }

            @Override
            public void onFailure(String message) {
                showToast(R.string.single_station_login_fail);
            }
        });
    }

    /**
     * 显示登录服务器对话框
     */
    private void showLoginServerDialog() {
        String ip = this.mServerManager.getDownloadFleetIp();
        if (!this.mServerManager.getFleetServerType() || StringUtil.isNullOrEmpty(ip)) {
            super.showToast(R.string.work_order_fleet_ip_null);
            jump2SettingActivity();
            return;
        }
        int port = this.mServerManager.getDownloadFleetPort();
        if (!this.mServerManager.getFleetServerType() || !Verify.isPort(String.valueOf(port))) {
            super.showToast(R.string.work_order_fleet_port_invalid);
            jump2SettingActivity();
            return;
        }
        LoginServerDialogFragment fragment = new LoginServerDialogFragment();
        String loginUser = this.mServerManager.getFleetAccount();
        String loginPassword = this.mServerManager.getFleetPassword();
        fragment.putBundle("login_user", loginUser);
        fragment.putBundle("login_password", loginPassword);
        this.getActivity().showDialog(fragment);
    }

    /**
     * 跳转到服务器设置界面
     */
    private void jump2SettingActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt(Sys.CURRENTTAB, 0);
        Intent intent = new Intent(getActivity(),Sys.class);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }

    /**
     * 选择要导入的基站数据文件
     */
    private void selectStationFile() {
        LogUtil.d(TAG, "----selectStationFile----");
        Intent intent = new Intent(this.getActivity(), FileChooseActivity.class);
        intent.putExtra("file_path", Environment.getExternalStorageDirectory().getPath());
        intent.putExtra("filter_types", "txt,xls,kml,mif");
        super.startActivityForResult(intent, FILE_CHOOSE_REQUEST_CODE);
    }

    @Override
    public void loadData() {
    }

    /**
     * 从平台查询指定的基站信息
     *
     * @param distance 查询距离
     */
    public void searchStationFromPlatform(int distance) {
        String ip = this.mServerManager.getDownloadFleetIp();
        if (!this.mServerManager.getFleetServerType() || StringUtil.isNullOrEmpty(ip)) {
            super.showToast(R.string.work_order_fleet_ip_null);
            jump2SettingActivity();
            return;
        }
        int port = this.mServerManager.getDownloadFleetPort();
        if (!this.mServerManager.getFleetServerType() || !Verify.isPort(String.valueOf(port))) {
            super.showToast(R.string.work_order_fleet_port_invalid);
            jump2SettingActivity();
            return;
        }
        double latitude = super.getIntent().getDoubleExtra("latitude", -9999);
        double longitude = super.getIntent().getDoubleExtra("longitude", -9999);
        if (latitude == -9999)
            return;
        String loginUser = this.mServerManager.getFleetAccount();
        this.mService.getStationSearchList(this.mFragment.getContext(), ip, port, loginUser, latitude, longitude, distance, new StationSearchCallBack() {
            @Override
            public void onSuccess(List<StationSearch> stationList) {
                mFragment.showFragment(stationList);
            }

            @Override
            public void onFailure(String message) {
                showToast(message);
            }
        });
    }

    /**
     * 从平台查询指定的基站信息
     *
     * @param keyword 查询关键字
     */
    public void searchStationFromPlatform(String keyword) {
        if (StringUtil.isNullOrEmpty(keyword))
            return;
        String ip = this.mServerManager.getDownloadFleetIp();
        if (!this.mServerManager.getFleetServerType() || StringUtil.isNullOrEmpty(ip)) {
            super.showToast(R.string.work_order_fleet_ip_null);
            jump2SettingActivity();
            return;
        }
        int port = this.mServerManager.getDownloadFleetPort();
        if (!this.mServerManager.getFleetServerType() || !Verify.isPort(String.valueOf(port))) {
            super.showToast(R.string.work_order_fleet_port_invalid);
            jump2SettingActivity();
            return;
        }
        String loginUser = this.mServerManager.getFleetAccount();
        this.mService.getStationSearchList(this.mFragment.getContext(), ip, port, loginUser, keyword, new StationSearchCallBack() {
            @Override
            public void onSuccess(List<StationSearch> stationList) {
                mFragment.showFragment(stationList);
            }

            @Override
            public void onFailure(String message) {
                showToast(message);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CHOOSE_REQUEST_CODE && resultCode == FileChooseActivity.FILE_CHOOSE_RESULT_CODE) {
            String filePath = data.getStringExtra("file_path");
            LogUtil.d(TAG, "----onActivityResult----filePath:" + filePath);
            this.mService.importStation(this.mFragment.getContext(), filePath, new SimpleCallBack() {
                @Override
                public void onSuccess() {
                    dismissProgressDialog();
                    BaseTabHostActivity activity = (BaseTabHostActivity) getActivity();
                    activity.selectTab(0);
                }

                @Override
                public void onFailure(String message) {
                    showToast(message);
                }
            });
        }
    }

    /**
     * 新增平台基站
     *
     * @param stationId 平台基站ID
     */
    public void addStation(int stationId) {
        String ip = this.mServerManager.getDownloadFleetIp();
        if (!this.mServerManager.getFleetServerType() || StringUtil.isNullOrEmpty(ip)) {
            super.showToast(R.string.work_order_fleet_ip_null);
            return;
        }
        int port = this.mServerManager.getDownloadFleetPort();
//        String loginUser = this.mServerManager.getFleetAccount();
        showProgressDialog("", getActivity().getString(R.string.single_station_importing_platform_station), true, false);
        this.mService.addStationFromPlatform(getActivity(), ip, port, stationId, new SimpleCallBack() {
            @Override
            public void onSuccess() {
                dismissProgressDialog();
                BaseTabHostActivity activity = (BaseTabHostActivity) getActivity();
                activity.selectTab(0);
            }

            @Override
            public void onFailure(String message) {
                dismissProgressDialog();
                showToast(message);
            }
        });
    }
}
