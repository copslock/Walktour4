package com.walktour.gui.singlestation.test.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.test.activity.StationActivity;
import com.walktour.gui.singlestation.test.module.StationActivityModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 基站测试基站列表界面组件类
 * Created by wangk on 2017/4/6.
 */
@Singleton
@ActivityScope
@Component(modules = StationActivityModule.class)
public interface StationActivityComponent {

    StationActivity inject(StationActivity activity);

}