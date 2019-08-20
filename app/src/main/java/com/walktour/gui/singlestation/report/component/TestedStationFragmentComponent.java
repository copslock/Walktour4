package com.walktour.gui.singlestation.report.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.report.fragment.TestedStationFragment;
import com.walktour.gui.singlestation.report.module.TestedStationFragmentModule;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@ActivityScope
@Component(modules = TestedStationFragmentModule.class)
public interface TestedStationFragmentComponent {
    TestedStationFragment inject(TestedStationFragment fragment);
}
