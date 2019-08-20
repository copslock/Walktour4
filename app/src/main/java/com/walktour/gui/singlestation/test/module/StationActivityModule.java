package com.walktour.gui.singlestation.test.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.test.activity.StationActivity;
import com.walktour.gui.singlestation.test.presenter.StationActivityPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * 基站测试基站列表界面模型
 * Created by wangk on 2017/4/6.
 */
@Module
@ActivityScope
public class StationActivityModule {
    /**
     * 关联视图
     */
    private StationActivity mActivity;

    public StationActivityModule(StationActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    StationActivity provideActivity() {
        return this.mActivity;
    }

    @Provides
    StationActivityPresenter providePresenter(StationActivity activity) {
        return new StationActivityPresenter(activity);
    }
}
