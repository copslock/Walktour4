package com.walktour.gui.singlestation.test.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.test.fragment.StationSearchFragment;
import com.walktour.gui.singlestation.test.module.StationSearchFragmentModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 基站查询列表组件类
 * Created by wangk on 2017/4/6.
 */
@Singleton
@ActivityScope
@Component(modules = StationSearchFragmentModule.class)
public interface StationSearchFragmentComponent {

    StationSearchFragment inject(StationSearchFragment fragment);

}