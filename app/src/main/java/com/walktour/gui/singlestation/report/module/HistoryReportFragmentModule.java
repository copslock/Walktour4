package com.walktour.gui.singlestation.report.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.report.fragment.HistoryReportFragment;
import com.walktour.gui.singlestation.report.presenter.HistoryReportFragmentPresenter;
import com.walktour.gui.singlestation.report.service.HistoryReportService;

import dagger.Module;
import dagger.Provides;

/***
 * 已测试基站模型
 */
@Module
@ActivityScope
public class HistoryReportFragmentModule {
    private HistoryReportFragment mFragment;

    public HistoryReportFragmentModule(HistoryReportFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    HistoryReportFragment provideFragment() {
        return this.mFragment;
    }

    @Provides
    HistoryReportService provideService() {
        return new HistoryReportService(this.mFragment.getContext());
    }

    @Provides
    HistoryReportFragmentPresenter providePresenter(HistoryReportFragment fragment,HistoryReportService service) {
        return new HistoryReportFragmentPresenter(fragment,service);
    }
}
