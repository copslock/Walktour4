package com.walktour.gui.singlestation.test.module;

import com.walktour.base.gui.ActivityScope;
import com.walktour.gui.singlestation.test.activity.SceneTestActivity;
import com.walktour.gui.singlestation.test.presenter.SceneTestActivityPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * 场景测试界面模型
 * Created by wangk on 2017/4/6.
 */
@Module
@ActivityScope
public class SceneTestActivityModule {
    /**
     * 关联视图
     */
    private SceneTestActivity mActivity;

    public SceneTestActivityModule(SceneTestActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    SceneTestActivity provideActivity() {
        return this.mActivity;
    }

    @Provides
    SceneTestActivityPresenter providePresenter(SceneTestActivity activity) {
        return new SceneTestActivityPresenter(activity);
    }
}
