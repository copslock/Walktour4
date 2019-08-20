package com.walktour.gui.singlestation.setting.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.setting.fragment.SettingOutdoorFragment;
import com.walktour.gui.singlestation.setting.presenter.SettingOutdoorFragmentPresenter;
import com.walktour.gui.singlestation.setting.service.SettingService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 室外宏站模型
 * Created by luojun on 2017/7/23.
 */

@Module
@ActivityScope
public class SettingOutdoorFragmentModule {
    private SettingOutdoorFragment mFragment;

    public SettingOutdoorFragmentModule(SettingOutdoorFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    SettingOutdoorFragment provideFragment() {
        return this.mFragment;
    }

    @Provides
    @Singleton
    SettingService provideService() {
        return new SettingService(this.mFragment.getContext());
    }

    @Provides
    SettingOutdoorFragmentPresenter providePresenter(SettingOutdoorFragment fragment, SettingService service) {
        return new SettingOutdoorFragmentPresenter(fragment, service);
    }
}
