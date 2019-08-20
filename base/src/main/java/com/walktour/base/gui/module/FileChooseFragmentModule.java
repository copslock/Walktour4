package com.walktour.base.gui.module;

import android.app.Service;

import com.walktour.base.gui.ActivityScope;
import com.walktour.base.gui.fragment.FileChooseFragment;
import com.walktour.base.gui.presenter.FileChooseFragmentPresenter;
import com.walktour.base.gui.service.FileChooseService;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 文件选择模型
 * Created by wangk on 2017/4/6.
 */
@Module
@ActivityScope
public class FileChooseFragmentModule {
    /**
     * 关联视图
     */
    private FileChooseFragment mFragment;

    public FileChooseFragmentModule(FileChooseFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    @Singleton
    FileChooseService provideService() {
        return new FileChooseService();
    }

    @Provides
    FileChooseFragment provideFragment() {
        return this.mFragment;
    }

    @Provides
    FileChooseFragmentPresenter providePresenter(FileChooseFragment fragment, FileChooseService service) {
        return new FileChooseFragmentPresenter(fragment, service);
    }
}
