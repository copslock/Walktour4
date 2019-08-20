package com.walktour.gui.singlestation.setting.presenter;

import com.walktour.base.gui.model.TreeNode;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.setting.fragment.SettingOutdoorFragment;
import com.walktour.gui.singlestation.setting.model.SettingModelCallBack;
import com.walktour.gui.singlestation.setting.service.SettingService;

import java.util.List;

/**
 * 室外宏站阈值设置交互类
 * Created by luojun on 2017/7/23.
 */

public class SettingOutdoorFragmentPresenter extends SettingBaseFragmentPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "SettingOutdoorFragmentPresenter";

    public SettingOutdoorFragmentPresenter(SettingOutdoorFragment fragment, SettingService service) {
        super(fragment, service);
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    @Override
    public void loadData() {
        super.mService.getThresholdSettingModeList(SingleStationDaoManager.STATION_TYPE_OUTDOOR, new SettingModelCallBack() {
            @Override
            public void onSuccess(List<TreeNode> settingModeList) {
                mFragment.showFragment(settingModeList);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    /**
     * 加载默认的数据
     */
    public void LoadDefaultData() {
        super.mService.setThresholdDefaultSetting(SingleStationDaoManager.STATION_TYPE_OUTDOOR, new SettingModelCallBack() {
            @Override
            public void onSuccess(List<TreeNode> settingModeList) {
                mFragment.showFragment(settingModeList);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }
}
