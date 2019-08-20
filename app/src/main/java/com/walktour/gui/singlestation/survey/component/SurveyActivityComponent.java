package com.walktour.gui.singlestation.survey.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.survey.activity.SurveyActivity;
import com.walktour.gui.singlestation.survey.module.SurveyActivityModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 基站测试基站列表界面组件类
 * Created by yi.lin on 2017/9/29
 */
@Singleton
@ActivityScope
@Component(modules = SurveyActivityModule.class)
public interface SurveyActivityComponent {

    SurveyActivity inject(SurveyActivity activity);

}