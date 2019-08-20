package com.walktour.base.gui.activity;

import com.walktour.base.gui.component.DaggerSimpleTabHostActivityComponent;
import com.walktour.base.gui.module.SimpleTabHostActivityModule;
import com.walktour.base.gui.presenter.BaseActivityPresenter;
import com.walktour.base.gui.presenter.SimpleTabHostActivityPresenter;

import javax.inject.Inject;

/**
 * 简单页签视图界面基础类
 * Created by wangk on 2017/6/21.
 */

public abstract class SimpleTabHostActivity extends BaseTabHostActivity {
    /**
     * 界面交互类
     */
    @Inject
    SimpleTabHostActivityPresenter mPresenter;

    @Override
    protected void setupActivityComponent() {
        DaggerSimpleTabHostActivityComponent.builder().simpleTabHostActivityModule(new SimpleTabHostActivityModule(this)).build().inject(this);
    }

    @Override
    protected BaseActivityPresenter getPresenter() {
        return this.mPresenter;
    }
}
