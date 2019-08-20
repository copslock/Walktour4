package com.walktour.gui.singlestation.test.module;

import com.walktour.Utils.ApplicationModel;
import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.test.fragment.SceneTestCellFragment;
import com.walktour.gui.singlestation.test.presenter.SceneTestCellFragmentPresenter;
import com.walktour.gui.singlestation.test.service.SceneTestService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 小区测试模型
 * Created by wangk on 2017/4/6.
 */
@Module
@ActivityScope
public class SceneTestCellFragmentModule {
    /**
     * 关联视图
     */
    private SceneTestCellFragment mFragment;

    public SceneTestCellFragmentModule(SceneTestCellFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    SceneTestCellFragment provideFragment() {
        return this.mFragment;
    }

    @Provides
    @Singleton
    SceneTestService provideService() {
        return new SceneTestService(this.mFragment.getContext());
    }

    @Provides
    SceneTestCellFragmentPresenter providePresenter(SceneTestCellFragment fragment, ApplicationModel applicationModel, SceneTestService service) {
        return new SceneTestCellFragmentPresenter(fragment, applicationModel, service);
    }
}
