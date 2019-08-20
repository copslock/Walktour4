package com.walktour.gui.singlestation.setting.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.setting.fragment.SettingOutdoorFragment;
import com.walktour.gui.singlestation.setting.module.SettingOutdoorFragmentModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by luojun on 2017/7/23.
 */

@Singleton
@ActivityScope
@Component(modules = SettingOutdoorFragmentModule.class)
public interface SettingOutdoorFragmentComponent {
    SettingOutdoorFragment inject(SettingOutdoorFragment fragment);
}
