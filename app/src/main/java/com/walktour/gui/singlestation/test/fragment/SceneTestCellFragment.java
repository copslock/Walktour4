package com.walktour.gui.singlestation.test.fragment;

import android.widget.TextView;

import com.walktour.gui.R;
import com.walktour.gui.R2;
import com.walktour.gui.singlestation.dao.model.CellInfo;
import com.walktour.gui.singlestation.test.component.DaggerSceneTestCellFragmentComponent;
import com.walktour.gui.singlestation.test.module.SceneTestCellFragmentModule;
import com.walktour.gui.singlestation.test.presenter.SceneTestCellFragmentPresenter;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * 场景小区测试视图
 * Created by wangk on 2017/6/13.
 */

public class SceneTestCellFragment extends SceneTestBaseFragment {
    /**
     * 日志标识
     */
    private static final String TAG = "SceneTestCellFragment";
    /**
     * 测试小区
     */
    @BindView(R2.id.test_cell)
    TextView mTestCell;
    /**
     * 当前小区
     */
    @BindView(R2.id.current_cell)
    TextView mCurrentCell;
    /**
     * 界面交互类
     */
    @Inject
    SceneTestCellFragmentPresenter mPresenter;
    /**
     * 显示的小区对象
     */
    private CellInfo mCellInfo;

    public SceneTestCellFragment() {
        super(R.string.single_station_scene_performance, R.layout.fragment_single_station_test_cell_list, R.layout.fragment_single_station_test_base_list_row);
    }

    @Override
    public SceneTestCellFragmentPresenter getPresenter() {
        return this.mPresenter;
    }

    /**
     * 显示测试小区
     *
     * @param eNodeBID 测试基站参数
     * @param cellID   小区ID
     */
    public void showTestCell(int eNodeBID, int cellID) {
        String value = eNodeBID + " " + cellID;
        this.mTestCell.setText(value);
    }

    /**
     * 显示当前小区信息
     *
     * @param eNodeBID 当前基站参数
     * @param cellID   小区ID
     * @param color    字体的颜色
     */
    public void showCurrentCell(int eNodeBID, int cellID, int color) {
        if (eNodeBID > 0 && cellID > 0) {
            String value = eNodeBID + " " + cellID;
            this.mCurrentCell.setText(value);
        }
        this.mCurrentCell.setTextColor(this.getResources().getColor(color));
    }

    public CellInfo getCellInfo() {
        return mCellInfo;
    }

    public void setCellInfo(CellInfo cellInfo) {
        mCellInfo = cellInfo;
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    @Override
    protected void setupFragmentComponent() {
        DaggerSceneTestCellFragmentComponent.builder().sceneTestCellFragmentModule(new SceneTestCellFragmentModule(this)).build().inject(this);
    }

}
