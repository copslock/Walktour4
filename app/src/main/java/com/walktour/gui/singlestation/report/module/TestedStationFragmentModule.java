package com.walktour.gui.singlestation.report.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.report.fragment.TestedStationFragment;
import com.walktour.gui.singlestation.report.presenter.TestedStationFragmentPresenter;
import com.walktour.gui.singlestation.report.service.TestedStationService;

import dagger.Module;
import dagger.Provides;

/***
 * 已测试基站模型
 */
@Module
@ActivityScope
public class TestedStationFragmentModule {
    private TestedStationFragment mFragment;

    public TestedStationFragmentModule(TestedStationFragment mFragment) {
        this.mFragment = mFragment;
    }

    @Provides
    TestedStationFragment provideFragment() {
        return this.mFragment;
    }

    @Provides
    TestedStationService provideService() {
        return new TestedStationService(this.mFragment.getContext());
    }

    @Provides
    TestedStationFragmentPresenter providePresenter(TestedStationFragment fragment, TestedStationService service) {
        return new TestedStationFragmentPresenter(fragment, service);
    }
}
