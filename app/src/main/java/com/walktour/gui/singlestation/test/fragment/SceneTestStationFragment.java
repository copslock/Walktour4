package com.walktour.gui.singlestation.test.fragment;

import android.widget.ListView;
import android.widget.TextView;

import com.walktour.gui.R;
import com.walktour.gui.R2;
import com.walktour.gui.singlestation.test.component.DaggerSceneTestStationFragmentComponent;
import com.walktour.gui.singlestation.test.module.SceneTestStationFragmentModule;
import com.walktour.gui.singlestation.test.presenter.SceneTestStationFragmentPresenter;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * 场景测试基站视图类
 * Created by wangk on 2017/6/13.
 */

public class SceneTestStationFragment extends SceneTestBaseFragment {
    /**
     * 日志标识
     */
    private static final String TAG = "SceneTestStationFragment";
    /**
     * 结果列表
     */
    @BindView(R2.id.list_view)
    ListView mResultList;
    /**
     * 测试基站
     */
    @BindView(R2.id.test_site)
    TextView mTestStation;
    /**
     * 当前基站
     */
    @BindView(R2.id.current_site)
    TextView mCurrentStation;
    /**
     * 界面交互类
     */
    @Inject
    SceneTestStationFragmentPresenter mPresenter;

    public SceneTestStationFragment() {
        super(R.string.single_station_test, R.layout.fragment_single_station_test_station_list, R.layout.fragment_single_station_test_base_list_row);
    }

    /**
     * 显示测试基站
     *
     * @param eNodeBID 测试基站参数
     */
    public void showTestStation(int eNodeBID) {
        this.mTestStation.setText(String.valueOf(eNodeBID));
    }

    /**
     * 显示当前基站信息
     *
     * @param eNodeBID 当前基站参数
     * @param color    字体的颜色
     */
    public void showCurrentStation(int eNodeBID, int color) {
        if (eNodeBID > 0)
            this.mCurrentStation.setText(String.valueOf(eNodeBID));
        this.mCurrentStation.setTextColor(color);
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    @Override
    protected void setupFragmentComponent() {
        DaggerSceneTestStationFragmentComponent.builder().sceneTestStationFragmentModule(new SceneTestStationFragmentModule(this)).build().inject(this);

    }

    @Override
    public SceneTestStationFragmentPresenter getPresenter() {
        return this.mPresenter;
    }
}
