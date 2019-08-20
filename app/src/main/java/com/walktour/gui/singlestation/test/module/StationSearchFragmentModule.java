package com.walktour.gui.singlestation.test.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.test.fragment.StationSearchFragment;
import com.walktour.gui.singlestation.test.presenter.StationSearchFragmentPresenter;
import com.walktour.gui.singlestation.test.service.StationSearchService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 基站测试基站查询模型
 * Created by wangk on 2017/4/6.
 */
@Module
@ActivityScope
public class StationSearchFragmentModule {
    /**
     * 关联视图
     */
    private StationSearchFragment mFragment;

    public StationSearchFragmentModule(StationSearchFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    StationSearchFragment provideFragment() {
        return this.mFragment;
    }

    @Provides
    @Singleton
    StationSearchService provideService() {
        return new StationSearchService(this.mFragment.getContext());
    }

    @Provides
    StationSearchFragmentPresenter providePresenter(StationSearchFragment fragment, StationSearchService service) {
        return new StationSearchFragmentPresenter(fragment, service);
    }
}
