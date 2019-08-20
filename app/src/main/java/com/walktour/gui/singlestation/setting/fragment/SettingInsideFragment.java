package com.walktour.gui.singlestation.setting.fragment;

import android.annotation.SuppressLint;

import com.walktour.base.gui.model.TreeNode;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.model.ThresholdSetting;
import com.walktour.gui.singlestation.setting.component.DaggerSettingInsideFragmentComponent;
import com.walktour.gui.singlestation.setting.module.SettingInsideFragmentModule;
import com.walktour.gui.singlestation.setting.presenter.SettingInsideFragmentPresenter;

import javax.inject.Inject;

import butterknife.OnItemClick;

/**
 * 室内基站阈值设置视图
 * Created by luojun on 2017/7/4.
 */

@SuppressLint("ValidFragment")
public class SettingInsideFragment extends SettingBaseFragment {
    /**
     * 日志标识
     */
    private static final String TAG = "SettingInsideFragment";

    /**
     * 界面交互类
     */
    @Inject
    SettingInsideFragmentPresenter mPresenter;

    public SettingInsideFragment() {
        super(R.string.single_station_inside_station, R.layout.fragment_single_station_setting_list_view, R.layout.fragment_single_station_setting_threshold_row);
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
        DaggerSettingInsideFragmentComponent.builder().settingInsideFragmentModule(new SettingInsideFragmentModule(this)).build().inject(this);
    }

    @Override
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
