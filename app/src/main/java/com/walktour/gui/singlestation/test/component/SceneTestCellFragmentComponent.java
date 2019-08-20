package com.walktour.gui.singlestation.test.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.test.fragment.SceneTestCellFragment;
import com.walktour.gui.singlestation.test.module.SceneTestBaseFragmentModule;
import com.walktour.gui.singlestation.test.module.SceneTestCellFragmentModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 小区测试组件类
 * Created by wangk on 2017/4/6.
 */
@Singleton
@ActivityScope
@Component(modules = {SceneTestCellFragmentModule.class, SceneTestBaseFragmentModule.class})
public interface SceneTestCellFragmentComponent {

    SceneTestCellFragment inject(SceneTestCellFragment fragment);

}