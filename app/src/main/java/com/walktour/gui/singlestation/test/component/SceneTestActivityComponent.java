package com.walktour.gui.singlestation.test.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.test.activity.SceneTestActivity;
import com.walktour.gui.singlestation.test.module.SceneTestActivityModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 简单视图界面组件类
 * Created by wangk on 2017/4/6.
 */
@Singleton
@ActivityScope
@Component(modules = SceneTestActivityModule.class)
public interface SceneTestActivityComponent {

    SceneTestActivity inject(SceneTestActivity activity);

}