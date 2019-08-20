package com.walktour.gui.singlestation.setting.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.setting.fragment.SettingInsideFragment;
import com.walktour.gui.singlestation.setting.module.SettingInsideFragmentModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by luojun on 2017/7/6.
 */

@Singleton
@ActivityScope
@Component(modules = SettingInsideFragmentModule.class)
public interface SettingInsideFragmentComponent {
    SettingInsideFragment inject(SettingInsideFragment fragment);
}
