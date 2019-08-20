package com.walktour.gui.singlestation.survey.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.survey.activity.SurveyEditActivity;
import com.walktour.gui.singlestation.survey.module.SurveyEditActivityModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 基站勘查基站参数编辑组件类
 * Created by wangk on 2017/4/6.
 */
@Singleton
@ActivityScope
@Component(modules = SurveyEditActivityModule.class)
public interface SurveyEditActivityComponent {

    SurveyEditActivity inject(SurveyEditActivity activity);

}