package com.walktour.gui.singlestation.setting.presenter;

import android.os.Bundle;

import com.walktour.base.gui.model.SimpleCallBack;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.gui.singlestation.dao.model.ThresholdSetting;
import com.walktour.gui.singlestation.setting.fragment.SettingBaseFragment;
import com.walktour.gui.singlestation.setting.fragment.ThresholdEditDialogFragment;
import com.walktour.gui.singlestation.setting.service.SettingService;

/**
 * 阈值设置基础交互类
 * Created by luojun on 2017/7/23.
 */

public abstract class SettingBaseFragmentPresenter extends BaseFragmentPresenter {
    /**
     * 关联视图
     */
    protected SettingBaseFragment mFragment;
    /**
     * 关联的业务类
     */
    protected SettingService mService;

    SettingBaseFragmentPresenter(SettingBaseFragment fragment, SettingService service) {
        super(fragment);
        this.mFragment = fragment;
        this.mService = service;
    }

    /**
     * 显示阈值编辑对话框
     */
    public void showThresholdEditDialog(ThresholdSetting thresholdSetting) {
        ThresholdEditDialogFragment fragment = new ThresholdEditDialogFragment();
        fragment.putBundle("threshold_setting", thresholdSetting);
        this.getActivity().showDialog(fragment);
    }

    @Override
    public void dealDialogCallBackValues(Bundle bundle) {
        ThresholdSetting thresholdSetting = bundle.getParcelable("threshold_setting");
        this.mService.editThresholdSetting(thresholdSetting, new SimpleCallBack() {
            @Override
            public void onSuccess() {
                loadData();
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }
}
