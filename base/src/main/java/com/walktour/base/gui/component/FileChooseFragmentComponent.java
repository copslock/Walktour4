package com.walktour.base.gui.component;

import com.walktour.base.gui.ActivityScope;
import com.walktour.base.gui.activity.SimpleBaseActivity;
import com.walktour.base.gui.fragment.FileChooseFragment;
import com.walktour.base.gui.module.FileChooseFragmentModule;
import com.walktour.base.gui.module.SimpleBaseActivityModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 文件选择界面组件类
 * Created by wangk on 2017/4/6.
 */
@Singleton
@ActivityScope
@Component(modules = FileChooseFragmentModule.class)
public interface FileChooseFragmentComponent {

    FileChooseFragment inject(FileChooseFragment activity);

}