package com.walktour.gui.singlestation.test.model;

import com.walktour.base.gui.model.BaseCallBack;
import com.walktour.gui.singlestation.dao.model.TaskTestResult;

import java.util.ArrayList;

/**
 * 业务测试结果回调接口
 * Created by wangk on 2017/8/30.
 */

public interface TaskTestResultCallBack extends BaseCallBack {
    /**
     * 执行成功
     *
     * @param resultList 测试结果列表
     */
    void onSuccess(ArrayList<TaskTestResult> resultList);
}
