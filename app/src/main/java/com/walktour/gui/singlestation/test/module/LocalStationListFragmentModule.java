package com.walktour.gui.singlestation.test.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.test.fragment.LocalStationListFragment;
import com.walktour.gui.singlestation.test.presenter.LocalStationListFragmentPresenter;
import com.walktour.gui.singlestation.test.service.LocalStationService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 基站测试本地基站列表模型
 * Created by wangk on 2017/4/6.
 */
@Module
@ActivityScope
public class LocalStationListFragmentModule {
    /**
     * 关联视图
     */
    private LocalStationListFragment mFragment;

    public LocalStationListFragmentModule(LocalStationListFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    LocalStationListFragment provideFragment() {
        return this.mFragment;
    }

    @Provides
    @Singleton
    LocalStationService provideService() {
        return new LocalStationService(this.mFragment.getContext());
    }

    @Provides
    LocalStationListFragmentPresenter providePresenter(LocalStationListFragment fragment, LocalStationService service) {
        return new LocalStationListFragmentPresenter(fragment, service);
    }
}
