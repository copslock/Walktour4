package com.walktour.gui.singlestation.survey.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.survey.activity.SurveyActivity;
import com.walktour.gui.singlestation.survey.presenter.SurveyActivityPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * 基站勘察基站列表界面模型
 * Created by yi.lin on 2017/9/29.
 */
@Module
@ActivityScope
public class SurveyActivityModule {
    /**
     * 关联视图
     */
    private SurveyActivity mActivity;

    public SurveyActivityModule(SurveyActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    SurveyActivity provideActivity() {
        return this.mActivity;
    }

    @Provides
    SurveyActivityPresenter providePresenter(SurveyActivity activity) {
        return new SurveyActivityPresenter(activity);
    }
}
