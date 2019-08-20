package com.walktour.gui.singlestation.survey.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.survey.fragment.SurveyHistoryFragment;
import com.walktour.gui.singlestation.survey.presenter.SurveyHistoryFragmentPresenter;
import com.walktour.gui.singlestation.survey.service.SurveyService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 基站勘查历史列表模型
 * Created by wangk on 2017/4/6.
 */
@Module
@ActivityScope
public class SurveyHistoryFragmentModule {
    /**
     * 关联视图
     */
    private SurveyHistoryFragment mFragment;

    public SurveyHistoryFragmentModule(SurveyHistoryFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    SurveyHistoryFragment provideFragment() {
        return this.mFragment;
    }

    @Provides
    @Singleton
    SurveyService provideService() {
        return new SurveyService(this.mFragment.getContext());
    }


    @Provides
    SurveyHistoryFragmentPresenter providePresenter(SurveyHistoryFragment fragment, SurveyService service) {
        return new SurveyHistoryFragmentPresenter(fragment, service);
    }
}
