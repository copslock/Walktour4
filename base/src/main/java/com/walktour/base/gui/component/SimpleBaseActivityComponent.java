package com.walktour.base.gui.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.base.gui.activity.SimpleBaseActivity;
import com.walktour.base.gui.module.SimpleBaseActivityModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 简单视图界面组件类
 * Created by wangk on 2017/4/6.
 */
@Singleton
@ActivityScope
@Component(modules = SimpleBaseActivityModule.class)
public interface SimpleBaseActivityComponent {

    SimpleBaseActivity inject(SimpleBaseActivity activity);

}