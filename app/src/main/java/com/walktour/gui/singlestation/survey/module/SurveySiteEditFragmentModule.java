package com.walktour.gui.singlestation.survey.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.survey.fragment.SurveySiteEditFragment;
import com.walktour.gui.singlestation.survey.presenter.SurveySiteEditFragmentPresenter;
import com.walktour.gui.singlestation.survey.service.SurveyService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 基站勘查基站参数编辑模型
 * Created by wangk on 2017/4/6.
 */
@Module
@ActivityScope
public class SurveySiteEditFragmentModule {
    /**
     * 关联视图
     */
    private SurveySiteEditFragment mFragment;

    public SurveySiteEditFragmentModule(SurveySiteEditFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    SurveySiteEditFragment provideFragment() {
        return this.mFragment;
    }

    @Provides
    @Singleton
    SurveyService provideService() {
        return new SurveyService(this.mFragment.getContext());
    }

    @Provides
    SurveySiteEditFragmentPresenter providePresenter(SurveySiteEditFragment fragment, SurveyService service) {
        return new SurveySiteEditFragmentPresenter(fragment, service);
    }
}
