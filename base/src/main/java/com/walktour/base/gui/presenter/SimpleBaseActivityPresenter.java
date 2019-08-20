package com.walktour.base.gui.presenter;

import com.walktour.base.gui.activity.BaseActivity;

/**
 * 简单视图界面交互类
 */
public class SimpleBaseActivityPresenter extends BaseActivityPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "SimpleBaseActivityPresenter";

    public SimpleBaseActivityPresenter(BaseActivity activity) {
        super(activity);
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }
}
