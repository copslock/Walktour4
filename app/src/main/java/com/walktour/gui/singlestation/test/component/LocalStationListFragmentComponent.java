package com.walktour.gui.singlestation.test.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.test.fragment.LocalStationListFragment;
import com.walktour.gui.singlestation.test.module.LocalStationListFragmentModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 本地基站列表组件类
 * Created by wangk on 2017/4/6.
 */
@Singleton
@ActivityScope
@Component(modules = LocalStationListFragmentModule.class)
public interface LocalStationListFragmentComponent {

    LocalStationListFragment inject(LocalStationListFragment fragment);

}