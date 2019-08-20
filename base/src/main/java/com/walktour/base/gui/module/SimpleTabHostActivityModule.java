package com.walktour.base.gui.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.base.gui.activity.SimpleTabHostActivity;
import com.walktour.base.gui.presenter.SimpleTabHostActivityPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * 简单页签视图界面界面模型
 * Created by wangk on 2017/4/6.
 */
@Module
@ActivityScope
public class SimpleTabHostActivityModule {
    /**
     * 关联视图
     */
    private SimpleTabHostActivity mActivity;

    public SimpleTabHostActivityModule(SimpleTabHostActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    SimpleTabHostActivity provideActivity() {
        return this.mActivity;
    }

    @Provides
    SimpleTabHostActivityPresenter providePresenter(SimpleTabHostActivity activity) {
        return new SimpleTabHostActivityPresenter(activity);
    }
}
