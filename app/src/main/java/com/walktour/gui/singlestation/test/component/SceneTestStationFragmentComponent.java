package com.walktour.gui.singlestation.test.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.test.fragment.SceneTestStationFragment;
import com.walktour.gui.singlestation.test.module.SceneTestBaseFragmentModule;
import com.walktour.gui.singlestation.test.module.SceneTestStationFragmentModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 基站测试组件类
 * Created by wangk on 2017/4/6.
 */
@Singleton
@ActivityScope
@Component(modules = {SceneTestStationFragmentModule.class, SceneTestBaseFragmentModule.class})
public interface SceneTestStationFragmentComponent {

    SceneTestStationFragment inject(SceneTestStationFragment fragment);

}