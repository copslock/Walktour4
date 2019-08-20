package com.walktour.base.gui.presenter;

import android.content.Intent;

import com.walktour.base.gui.activity.BaseActivity;
import com.walktour.base.util.LogUtil;

/**
 * 界面交互基础类
 * Created by wangk on 2017/6/15.
 */

public abstract class BaseActivityPresenter {
    /**
     * 关联的界面
     */
    private BaseActivity mActivity;

    public BaseActivityPresenter(BaseActivity activity) {
        LogUtil.d(this.getLogTAG(), "-----onCreate----");
        this.mActivity = activity;
    }


    /**
     * 获取日志标识
     *
     * @return 日志标识
     */
    protected abstract String getLogTAG();

    /**
     * 获取所属Activity的Intent
     *
     * @return 所属Activity的Intent
     */
    protected Intent getIntent() {
        return this.mActivity.getIntent();
    }
}
