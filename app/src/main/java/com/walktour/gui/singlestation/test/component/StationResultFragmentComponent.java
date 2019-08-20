package com.walktour.gui.singlestation.test.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.test.fragment.StationResultFragment;
import com.walktour.gui.singlestation.test.module.StationResultFragmentModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 本地基站列表组件类
 * Created by wangk on 2017/4/6.
 */
@Singleton
@ActivityScope
@Component(modules = StationResultFragmentModule.class)
public interface StationResultFragmentComponent {

    StationResultFragment inject(StationResultFragment fragment);

}