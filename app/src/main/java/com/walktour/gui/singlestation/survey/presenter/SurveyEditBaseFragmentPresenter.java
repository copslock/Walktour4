package com.walktour.gui.singlestation.survey.presenter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.walktour.Utils.StringUtil;
import com.walktour.base.gui.fragment.BaseFragment;
import com.walktour.base.gui.model.SimpleCallBack;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.Verify;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.model.SurveyStationInfo;
import com.walktour.gui.singlestation.net.model.UploadSurveyStationResult;
import com.walktour.gui.singlestation.net.model.UploadSurveyStationResultCallback;
import com.walktour.gui.singlestation.survey.activity.SurveyEditActivity;
import com.walktour.gui.singlestation.survey.service.SurveyService;
import com.walktour.gui.singlestation.test.fragment.LoginServerDialogFragment;
import com.walktour.gui.singlestation.test.service.StationSearchService;

import java.util.HashMap;


/**
 * Created by yi.lin on 2017/9/4.
 * <p>
 * 基站勘察编辑presenter基类
 */

public abstract class SurveyEditBaseFragmentPresenter extends BaseFragmentPresenter {
    public static final String TAG = "SurveyEditBaseFragmentPresenter";

    /**
     * 子帧配比键值对{0,1,2,3,4,5,6},{"1:3","2:2","3:1","6:3","7:2","8:1","3:5"}
     * 如：<"0"=>"1:3">
     */
    protected static final HashMap<String, String> subframeMatchingMap = new HashMap<>();

    /**
     * 特殊子帧配比键值对{0,1,2,3,4,5,6,7,8},{"3:10:1","9:4:1","10:3:1","11:2:1","12:1:1","3:9:2","9:3:2","10:2:2","11:1:2"}
     * 如：<"0"=>"3:10:1">
     */
    protected static final HashMap<String, String> specialSubframeMatchingMap = new HashMap<>();

    /**
     * 服务管理类
     */
    private ServerManager mServerManager;
    /**
     * 关联业务类
     */
    protected SurveyService mService;

    public SurveyEditBaseFragmentPresenter(BaseFragment fragment, SurveyService service) {
        super(fragment);
        this.mService = service;
        mServerManager = ServerManager.getInstance(this.getActivity());
        initData();
    }

    /**
     * 获取子帧配比值
     * @param key
     * @return
     */
    protected String getSubframeMatchValue(String key){
        if(TextUtils.isEmpty(key) || !subframeMatchingMap.containsKey(key)){
            return "";
        }else{
            return subframeMatchingMap.get(key);
        }
    }

    /**
     * 获取特殊子帧配比值
     * @param key
     * @return
     */
    protected String getSpecialSubframeMatchValue(String key){
        if(TextUtils.isEmpty(key) || !specialSubframeMatchingMap.containsKey(key)){
            return "";
        }else{
            return specialSubframeMatchingMap.get(key);
        }
    }

    private void initData() {
        //子帧配比键值对{0,1,2,3,4,5,6},{"1:3","2:2","3:1","6:3","7:2","8:1","3:5"}
        if (subframeMatchingMap.isEmpty()) {
            subframeMatchingMap.put("0", "1:3");
            subframeMatchingMap.put("1", "2:2");
            subframeMatchingMap.put("2", "3:1");
            subframeMatchingMap.put("3", "6:3");
            subframeMatchingMap.put("4", "7:2");
            subframeMatchingMap.put("5", "8:1");
            subframeMatchingMap.put("6", "3:5");
        }
        //特殊子帧配比键值对{0,1,2,3,4,5,6,7,8},{"3:10:1","9:4:1","10:3:1","11:2:1","12:1:1","3:9:2","9:3:2","10:2:2","11:1:2"}
        if (specialSubframeMatchingMap.isEmpty()) {
            specialSubframeMatchingMap.put("0", "3:10:1");
            specialSubframeMatchingMap.put("1", "9:4:1");
            specialSubframeMatchingMap.put("2", "10:3:1");
            specialSubframeMatchingMap.put("3", "11:2:1");
            specialSubframeMatchingMap.put("4", "12:1:1");
            specialSubframeMatchingMap.put("5", "3:9:2");
            specialSubframeMatchingMap.put("6", "9:3:2");
            specialSubframeMatchingMap.put("7", "10:2:2");
            specialSubframeMatchingMap.put("8", "11:1:2");
        }
    }

    @Override
    public void onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_singlestation_upload) {
            doUploadSurveyResult();
        } else if (item.getItemId() == R.id.menu_singlestation_login) {
            showLoginServerDialog();
        }
    }

    /**
     * 显示登录服务器对话框
     */
    private void showLoginServerDialog() {
        String ip = this.mServerManager.getDownloadFleetIp();
        if (!this.mServerManager.getFleetServerType() || StringUtil.isNullOrEmpty(ip)) {
            super.showToast(R.string.work_order_fleet_ip_null);
            return;
        }
        int port = this.mServerManager.getDownloadFleetPort();
        if (!this.mServerManager.getFleetServerType() || !Verify.isPort(String.valueOf(port))) {
            super.showToast(R.string.work_order_fleet_port_invalid);
            return;
        }
        LoginServerDialogFragment fragment = new LoginServerDialogFragment();
        String loginUser = this.mServerManager.getFleetAccount();
        String loginPassword = this.mServerManager.getFleetPassword();
        fragment.putBundle("login_user", loginUser);
        fragment.putBundle("login_password", loginPassword);
        this.getActivity().showDialog(fragment);
    }

    @Override
    public void dealDialogCallBackValues(Bundle bundle) {
        String loginUser = bundle.getString("login_user");
        String loginPassword = bundle.getString("login_password");
        String ip = this.mServerManager.getDownloadFleetIp();
        int port = this.mServerManager.getDownloadFleetPort();
        this.mServerManager.setFleetAccount(loginUser);
        this.mServerManager.setFleetPassword(loginPassword);
        new StationSearchService(this.getActivity()).loginServer(this.getActivity(), ip, port, loginUser, loginPassword, new SimpleCallBack() {
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
     * 上传勘察结果数据
     */
    private void doUploadSurveyResult() {
        //先保存正在编辑的数据
        ((SurveyEditActivityPresenter) (((SurveyEditActivity) getActivity()).getPresenter())).saveEditingData();
        SurveyStationInfo surveyStationInfo = super.getIntent().getParcelableExtra("survey_station_info");
        String ip = ServerManager.getInstance(this.getActivity()).getDownloadFleetIp();
        if (!ServerManager.getInstance(this.getActivity()).getFleetServerType() || StringUtil.isNullOrEmpty(ip)) {
            super.showToast(R.string.work_order_fleet_ip_null);
            return;
        }
        int port = ServerManager.getInstance(this.getActivity()).getDownloadFleetPort();
        String loginUser = ServerManager.getInstance(this.getActivity()).getFleetAccount();
        showProgressDialog("", getActivity().getString(R.string.network_request_data_uploading), true, false);
        mService.uploadSurveyStationInfo(getActivity(), loginUser, ip, port, surveyStationInfo.getStationInfo().getSiteId(),surveyStationInfo.getStationId(), new UploadSurveyStationResultCallback() {
            @Override
            public void onSuccess(UploadSurveyStationResult result) {
                dismissProgressDialog();
                showToast(result.isSuccess() ? getActivity().getString(R.string.network_request_data_upload_success) : getActivity().getString(R.string.network_request_data_upload_failed));
            }

            @Override
            public void onFailure(String message) {
                dismissProgressDialog();
                showToast(!TextUtils.isEmpty(message) ? message : getActivity().getString(R.string.network_request_data_upload_failed));
                LogUtil.i(getLogTAG(), message);
            }
        });
    }


    @Override
    protected String getLogTAG() {
        return TAG;
    }

    /**
     * 保存编辑数据
     */
    public abstract void saveEditingData();
}
