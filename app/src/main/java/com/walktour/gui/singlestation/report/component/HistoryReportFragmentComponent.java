package com.walktour.gui.singlestation.report.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.report.fragment.HistoryReportFragment;
import com.walktour.gui.singlestation.report.module.HistoryReportFragmentModule;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@ActivityScope
@Component(modules = HistoryReportFragmentModule.class)
public interface HistoryReportFragmentComponent {
    HistoryReportFragment inject(HistoryReportFragment fragment);
}
