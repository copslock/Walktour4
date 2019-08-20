package com.walktour.gui.singlestation.survey.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.survey.fragment.SurveyCellEditFragment;
import com.walktour.gui.singlestation.survey.module.SurveyCellEditFragmentModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 基站勘查小区参数编辑组件类
 * Created by wangk on 2017/4/6.
 */
@Singleton
@ActivityScope
@Component(modules = SurveyCellEditFragmentModule.class)
public interface SurveyCellEditFragmentComponent {

    SurveyCellEditFragment inject(SurveyCellEditFragment fragment);

}