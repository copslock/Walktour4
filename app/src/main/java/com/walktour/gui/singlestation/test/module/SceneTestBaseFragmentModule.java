package com.walktour.gui.singlestation.test.module;

import com.walktour.Utils.ApplicationModel;
import com.walktour.base.gui.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * 场景测试模型
 * Created by wangk on 2017/4/6.
 */
@Module
@ActivityScope
public class SceneTestBaseFragmentModule {

    @Provides
    public ApplicationModel provideApplicationModel() {
        return ApplicationModel.getInstance();
    }

}
