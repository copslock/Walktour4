package com.walktour.gui.singlestation.survey.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.survey.fragment.SurveySiteEditFragment;
import com.walktour.gui.singlestation.survey.module.SurveySiteEditFragmentModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 基站勘查基站参数编辑组件类
 * Created by wangk on 2017/4/6.
 */
@Singleton
@ActivityScope
@Component(modules = SurveySiteEditFragmentModule.class)
public interface SurveySiteEditFragmentComponent {

    SurveySiteEditFragment inject(SurveySiteEditFragment fragment);

}