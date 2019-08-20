package com.walktour.gui.singlestation.test.model;

import com.walktour.base.gui.model.BaseCallBack;
import com.walktour.base.gui.model.TreeNode;

import java.util.List;

/**
 * 基站测试结果回调接口
 * Created by wangk on 2017/8/30.
 */

public interface StationTestResultCallBack extends BaseCallBack {
    /**
     * 执行成功
     *
     * @param resultList 测试结果树形列表
     */
    void onSuccess(List<TreeNode> resultList);
}
