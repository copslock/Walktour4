package com.walktour.base.gui.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.base.gui.activity.SimpleTabHostActivity;
import com.walktour.base.gui.module.SimpleTabHostActivityModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 简单视图界面组件类
 * Created by wangk on 2017/4/6.
 */
@Singleton
@ActivityScope
@Component(modules = SimpleTabHostActivityModule.class)
public interface SimpleTabHostActivityComponent {

    SimpleTabHostActivity inject(SimpleTabHostActivity activity);

}