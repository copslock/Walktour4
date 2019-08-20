package com.walktour.gui.singlestation.survey.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.survey.activity.SurveyEditActivity;
import com.walktour.gui.singlestation.survey.presenter.SurveyEditActivityPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * 基站勘查编辑模型
 * Created by wangk on 2017/4/6.
 */
@Module
@ActivityScope
public class SurveyEditActivityModule {
    /**
     * 关联视图
     */
    private SurveyEditActivity mActivity;

    public SurveyEditActivityModule(SurveyEditActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    SurveyEditActivity provideActivity() {
        return this.mActivity;
    }

    @Provides
    SurveyEditActivityPresenter providePresenter(SurveyEditActivity activity) {
        return new SurveyEditActivityPresenter(activity);
    }
}
