package com.walktour.gui.singlestation.setting.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.setting.fragment.SettingInsideFragment;
import com.walktour.gui.singlestation.setting.presenter.SettingInsideFragmentPresenter;
import com.walktour.gui.singlestation.setting.service.SettingService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 室内基站视图模型
 * Created by luojun on 2017/7/6.
 */

@Module
@ActivityScope
public class SettingInsideFragmentModule {
    private SettingInsideFragment mFragment;

    public SettingInsideFragmentModule(SettingInsideFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    SettingInsideFragment provideFragment() {
        return this.mFragment;
    }

    @Provides
    @Singleton
    SettingService provideService() {
        return new SettingService(this.mFragment.getContext());
    }

    @Provides
    SettingInsideFragmentPresenter providePresenter(SettingInsideFragment fragment, SettingService service) {
        return new SettingInsideFragmentPresenter(fragment, service);
    }

}
