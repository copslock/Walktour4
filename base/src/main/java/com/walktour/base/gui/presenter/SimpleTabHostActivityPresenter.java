package com.walktour.base.gui.presenter;

import com.walktour.base.gui.activity.BaseActivity;

/**
 * 简单页签视图界面交互类
 */
public class SimpleTabHostActivityPresenter extends BaseActivityPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "SimpleTabHostActivityPresenter";

    public SimpleTabHostActivityPresenter(BaseActivity activity) {
        super(activity);
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }
}
