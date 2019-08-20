package com.walktour.gui.singlestation.setting.fragment;

import android.annotation.SuppressLint;

import com.walktour.base.gui.model.TreeNode;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.model.ThresholdSetting;
import com.walktour.gui.singlestation.setting.component.DaggerSettingOutdoorFragmentComponent;
import com.walktour.gui.singlestation.setting.module.SettingOutdoorFragmentModule;
import com.walktour.gui.singlestation.setting.presenter.SettingOutdoorFragmentPresenter;

import javax.inject.Inject;

import butterknife.OnItemClick;

/**
 * 室外宏站阈值设置视图
 * Created by luojun on 2017/7/23.
 */

@SuppressLint("ValidFragment")
public class SettingOutdoorFragment extends SettingBaseFragment {
    /**
     * 日志标识
     */
    private static final String TAG = "SettingOutdoorFragment";

    /**
     * 界面交互类
     */
    @Inject
    SettingOutdoorFragmentPresenter mPresenter;

    public SettingOutdoorFragment() {
        super(R.string.single_station_outdoor_station, R.layout.fragment_single_station_setting_list_view, R.layout.fragment_single_station_setting_threshold_row);
    }

    @Override
    public String getLogTAG() {
        return TAG;
    }

    @Override
    public BaseFragmentPresenter getPresenter() {
        return this.mPresenter;
    }

    @Override
    protected void setupFragmentComponent() {
        DaggerSettingOutdoorFragmentComponent.builder().settingOutdoorFragmentModule(new SettingOutdoorFragmentModule(this)).build().inject(this);
    }

    //@Override
    protected void loadDefaultSetting() {
        this.mPresenter.LoadDefaultData();
    }

    /**
     * 弹出阈值编辑对话框
     *
     * @param position 行位置
     */
    @OnItemClick(R.id.list_view)
    public void showThresholdEditDialog(int position) {
        TreeNode treeNode = this.getItem(position);
        if (treeNode.getLevel() == 0)
            return;
        ThresholdSetting thresholdSetting = (ThresholdSetting) treeNode.getObject();
        this.mPresenter.showThresholdEditDialog(thresholdSetting);
    }

}
