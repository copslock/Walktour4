package com.walktour.base.gui.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.base.gui.activity.SimpleBaseActivity;
import com.walktour.base.gui.presenter.SimpleBaseActivityPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * 简单视图界面界面模型
 * Created by wangk on 2017/4/6.
 */
@Module
@ActivityScope
public class SimpleBaseActivityModule {
    /**
     * 关联视图
     */
    private SimpleBaseActivity mActivity;

    public SimpleBaseActivityModule(SimpleBaseActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    SimpleBaseActivity provideActivity() {
        return this.mActivity;
    }

    @Provides
    SimpleBaseActivityPresenter providePresenter(SimpleBaseActivity activity) {
        return new SimpleBaseActivityPresenter(activity);
    }
}
