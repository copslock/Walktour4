package com.walktour.gui.singlestation.setting.model;

import com.walktour.base.gui.model.BaseCallBack;
import com.walktour.base.gui.model.TreeNode;

import java.util.List;

/**
 * 阈值设置列表回调类
 * Created by wangk on 2017/8/30.
 */

public interface SettingModelCallBack extends BaseCallBack {
    /**
     * 执行成功
     *
     * @param settingModeList 阈值设置树列表
     */
    void onSuccess(List<TreeNode> settingModeList);
}
