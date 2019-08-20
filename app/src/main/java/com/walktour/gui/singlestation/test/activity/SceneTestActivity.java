package com.walktour.gui.singlestation.test.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;

import com.walktour.base.gui.activity.BaseTabHostActivity;
import com.walktour.base.gui.fragment.BaseFragment;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.test.component.DaggerSceneTestActivityComponent;
import com.walktour.gui.singlestation.test.module.SceneTestActivityModule;
import com.walktour.gui.singlestation.test.presenter.SceneTestActivityPresenter;
import com.walktour.gui.singlestation.test.service.SceneTestMonitorStartService;

import java.util.List;

import javax.inject.Inject;

/**
 * 基站场景测试界面
 */
public class SceneTestActivity extends BaseTabHostActivity {
    /**
     * 日志标识
     */
    private static final String TAG = "SceneTestActivity";
    /**
     * 关联交互类
     */
    @Inject
    SceneTestActivityPresenter mPresenter;
    /**
     * 广播接收
     */
    private MyReceiver mReceiver;

    @Override
    protected void onCreate() {
        StationInfo stationInfo = this.getIntent().getParcelableExtra("station_info");
        super.setToolbarTitle(stationInfo.getName());
        this.mReceiver = new MyReceiver();
        this.registerReceiver();
    }

    @Override
    protected void initFragments() {
        List<BaseFragment> fragmentList = this.mPresenter.getInitFragments();
        for (BaseFragment fragment : fragmentList) {
            super.addFragment(fragment);
        }
    }

    /**
     * 注册广播接听器
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SceneTestMonitorStartService.MESSAGE_TEST_FINISH);
        this.registerReceiver(mReceiver, filter);
    }

    /**
     * 注册广播接听器
     */
    private void unregisterReceiver() {
        this.unregisterReceiver(mReceiver);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SceneTestMonitorStartService.MESSAGE_TEST_FINISH)) {
                loadCurrentFragmentData();
            }
        }
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    @Override
    protected SceneTestActivityPresenter getPresenter() {
        return this.mPresenter;
    }

    @Override
    protected void setupActivityComponent() {
        DaggerSceneTestActivityComponent.builder().sceneTestActivityModule(new SceneTestActivityModule(this)).build().inject(this);
    }

    /**
     * 生成顶部菜单栏
     *
     * @param menu 菜单对象
     * @return 是否有生成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu, R.menu.singlestation_scene_test_menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver();
    }
}
