package com.walktour.gui.singlestation.survey.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.survey.fragment.SurveyCellEditFragment;
import com.walktour.gui.singlestation.survey.presenter.SurveyCellEditFragmentPresenter;
import com.walktour.gui.singlestation.survey.service.SurveyService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 基站勘查小区参数编辑模型
 * Created by wangk on 2017/4/6.
 */
@Module
@ActivityScope
public class SurveyCellEditFragmentModule {
    /**
     * 关联视图
     */
    private SurveyCellEditFragment mFragment;

    public SurveyCellEditFragmentModule(SurveyCellEditFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    SurveyCellEditFragment provideFragment() {
        return this.mFragment;
    }

    @Provides
    @Singleton
    SurveyService provideService() {
        return new SurveyService(this.mFragment.getContext());
    }

    @Provides
    SurveyCellEditFragmentPresenter providePresenter(SurveyCellEditFragment fragment, SurveyService service) {
        return new SurveyCellEditFragmentPresenter(fragment, service);
    }
}
