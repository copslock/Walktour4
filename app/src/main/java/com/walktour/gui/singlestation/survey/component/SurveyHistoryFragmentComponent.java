package com.walktour.gui.singlestation.survey.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.survey.fragment.SurveyHistoryFragment;
import com.walktour.gui.singlestation.survey.module.SurveyHistoryFragmentModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 基站勘查历史列表组件类
 * Created by wangk on 2017/4/6.
 */
@Singleton
@ActivityScope
@Component(modules = SurveyHistoryFragmentModule.class)
public interface SurveyHistoryFragmentComponent {

    SurveyHistoryFragment inject(SurveyHistoryFragment fragment);

}