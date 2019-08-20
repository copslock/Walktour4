package com.walktour.base.gui.activity;

import com.walktour.base.gui.component.DaggerSimpleBaseActivityComponent;
import com.walktour.base.gui.module.SimpleBaseActivityModule;
import com.walktour.base.gui.presenter.BaseActivityPresenter;
import com.walktour.base.gui.presenter.SimpleBaseActivityPresenter;

import javax.inject.Inject;

/**
 * 简单视图界面基础类
 * Created by wangk on 2017/6/21.
 */

public abstract class SimpleBaseActivity extends BaseActivity {
    /**
     * 界面交互类
     */
    @Inject
    SimpleBaseActivityPresenter mPresenter;

    @Override
    protected void setupActivityComponent() {
        DaggerSimpleBaseActivityComponent.builder().simpleBaseActivityModule(new SimpleBaseActivityModule(this)).build().inject(this);
    }

    @Override
    protected BaseActivityPresenter getPresenter() {
        return this.mPresenter;
    }
}
