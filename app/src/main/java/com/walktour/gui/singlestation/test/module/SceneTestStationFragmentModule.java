package com.walktour.gui.singlestation.test.module;

import com.walktour.Utils.ApplicationModel;
import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.test.fragment.SceneTestStationFragment;
import com.walktour.gui.singlestation.test.presenter.SceneTestStationFragmentPresenter;
import com.walktour.gui.singlestation.test.service.SceneTestService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 基站测试模型
 * Created by wangk on 2017/4/6.
 */
@Module
@ActivityScope
public class SceneTestStationFragmentModule {
    /**
     * 关联视图
     */
    private SceneTestStationFragment mFragment;

    public SceneTestStationFragmentModule(SceneTestStationFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    SceneTestStationFragment provideFragment() {
        return this.mFragment;
    }

    @Provides
    @Singleton
    SceneTestService provideService() {
        return new SceneTestService(this.mFragment.getContext());
    }

    @Provides
    SceneTestStationFragmentPresenter providePresenter(SceneTestStationFragment fragment, ApplicationModel applicationModel, SceneTestService service) {
        return new SceneTestStationFragmentPresenter(fragment, applicationModel, service);
    }
}
