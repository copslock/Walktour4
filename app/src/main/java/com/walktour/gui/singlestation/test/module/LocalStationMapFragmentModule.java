package com.walktour.gui.singlestation.test.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.test.fragment.LocalStationMapFragment;
import com.walktour.gui.singlestation.test.presenter.LocalStationMapFragmentPresenter;
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
public class LocalStationMapFragmentModule {
    /**
     * 关联视图
     */
    private LocalStationMapFragment mFragment;

    public LocalStationMapFragmentModule(LocalStationMapFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    LocalStationMapFragment provideFragment() {
        return this.mFragment;
    }

    @Provides
    @Singleton
    LocalStationService provideService() {
        return new LocalStationService(this.mFragment.getContext());
    }

    @Provides
    LocalStationMapFragmentPresenter providePresenter(LocalStationMapFragment fragment, LocalStationService service) {
        return new LocalStationMapFragmentPresenter(fragment, service);
    }
}
