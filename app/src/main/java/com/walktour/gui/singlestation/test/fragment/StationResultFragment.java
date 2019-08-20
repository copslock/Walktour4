package com.walktour.gui.singlestation.test.fragment;

import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.base.gui.fragment.BaseHolder;
import com.walktour.base.gui.fragment.BaseListFragment;
import com.walktour.base.gui.model.TreeNode;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.gui.R;
import com.walktour.gui.R2;
import com.walktour.gui.singlestation.dao.model.ThresholdTestResult;
import com.walktour.gui.singlestation.test.component.DaggerStationResultFragmentComponent;
import com.walktour.gui.singlestation.test.module.StationResultFragmentModule;
import com.walktour.gui.singlestation.test.presenter.StationResultFragmentPresenter;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * 基站测试测试情况列表界面
 * Created by wangk on 2017/6/13.
 */

public class StationResultFragment extends BaseListFragment<TreeNode> {
    /**
     * 日志标识
     */
    private static final String TAG = "StationResultFragment";
    /**
     * 界面交互类
     */
    @Inject
    StationResultFragmentPresenter mPresenter;
    /**
     * 结果列表
     */
    @BindView(R2.id.list_view)
    ListView mResultList;

    public StationResultFragment() {
        super(R.string.single_station_local_station, R.layout.fragment_list_base, R.layout.fragment_single_station_test_result_list_row);
    }

    @Override
    public String getLogTAG() {
        return TAG;
    }

    @Override
    public BaseFragmentPresenter getPresenter() {
        return this.mPresenter;
    }

    @Override
    protected void setupFragmentComponent() {
        DaggerStationResultFragmentComponent.builder().stationResultFragmentModule(new StationResultFragmentModule(this)).build().inject(this);
    }

    @Override
    protected void onCreateView() {

    }

    @Override
    protected AbsListView getListView() {
        return this.mResultList;
    }

    @Override
    protected BaseHolder createViewHolder() {
        return new ViewHolder();
    }

    /**
     * 存放控件 的ViewHolder
     */
    public class ViewHolder extends BaseHolder<TreeNode> {
        /**
         * 测试场景
         */
        @BindView(R2.id.test_scene_type)
        TextView mSceneType;
        /**
         * 阈值名称
         */
        @BindView(R2.id.threshold_name)
        TextView mThresholdName;
        /**
         * 判断值
         */
        @BindView(R2.id.threshold_value)
        TextView mThresholdValue;
        /**
         * 测试结果
         */
        @BindView(R2.id.test_result)
        TextView mTestResult;
        /**
         * 结果行
         */
        @BindView(R2.id.test_result_layout)
        LinearLayout mResultLayout;
        /**
         * 场景行
         */
        @BindView(R2.id.test_scene_type_layout)
        LinearLayout mSceneTypeLayout;

        @Override
        public void setData(int position, TreeNode data) {
            switch (data.getLevel()) {
                case 0:
                    showSceneTypeRow(data);
                    break;
                case 1:
                    showThresholdResultRow(data);
                    break;
            }
        }

        /**
         * 显示结果行
         *
         * @param data 数据
         */
        private void showThresholdResultRow(TreeNode data) {
            this.mSceneTypeLayout.setVisibility(View.GONE);
            this.mResultLayout.setVisibility(View.VISIBLE);
            ThresholdTestResult result = (ThresholdTestResult) data.getObject();
            this.mThresholdName.setText(result.getThresholdName());
            String value = result.getOperator();
            value += (int) result.getThresholdValue() + result.getThresholdUnit();
            this.mThresholdValue.setText(value);
            String testResult = String.valueOf((int) result.getRealValue()) + result.getThresholdUnit();
            this.mTestResult.setText(testResult);
            switch (result.getTestStatus()) {
                case ThresholdTestResult.TEST_STATUS_PASS:
                    this.mTestResult.setTextColor(getResources().getColor(R.color.green));
                    break;
                case ThresholdTestResult.TEST_STATUS_FAULT:
                    this.mTestResult.setTextColor(getResources().getColor(R.color.red_light));
                    break;
                default:
                    this.mTestResult.setTextColor(getResources().getColor(R.color.app_main_text_color));
                    break;
            }
        }

        /**
         * 显示场景行
         *
         * @param data 数据
         */
        private void showSceneTypeRow(TreeNode data) {
            this.mSceneTypeLayout.setVisibility(View.VISIBLE);
            this.mResultLayout.setVisibility(View.GONE);
            this.mSceneType.setText((String) data.getObject());
        }

    }
}
