package com.walktour.gui.singlestation.test.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.test.fragment.StationResultFragment;
import com.walktour.gui.singlestation.test.presenter.StationResultFragmentPresenter;
import com.walktour.gui.singlestation.test.service.LocalStationService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 基站测试本地基站模型
 * Created by wangk on 2017/4/6.
 */
@Module
@ActivityScope
public class StationResultFragmentModule {
    /**
     * 关联视图
     */
    private StationResultFragment mFragment;

    public StationResultFragmentModule(StationResultFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    StationResultFragment provideFragment() {
        return this.mFragment;
    }

    @Provides
    @Singleton
    LocalStationService provideService() {
        return new LocalStationService(this.mFragment.getContext());
    }

    @Provides
    StationResultFragmentPresenter providePresenter(StationResultFragment fragment, LocalStationService service) {
        return new StationResultFragmentPresenter(fragment, service);
    }
}
