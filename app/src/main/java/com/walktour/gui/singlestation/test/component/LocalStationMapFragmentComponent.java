package com.walktour.gui.singlestation.test.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.test.fragment.LocalStationMapFragment;
import com.walktour.gui.singlestation.test.module.LocalStationMapFragmentModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 本地基站列表组件类
 * Created by wangk on 2017/4/6.
 */
@Singleton
@ActivityScope
@Component(modules = LocalStationMapFragmentModule.class)
public interface LocalStationMapFragmentComponent {

    LocalStationMapFragment inject(LocalStationMapFragment fragment);

}