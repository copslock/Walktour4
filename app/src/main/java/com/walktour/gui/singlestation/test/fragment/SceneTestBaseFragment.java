package com.walktour.gui.singlestation.test.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.walktour.Utils.WalkMessage;
import com.walktour.base.gui.fragment.BaseHolder;
import com.walktour.base.gui.fragment.BaseListFragment;
import com.walktour.base.gui.model.ServiceMessage;
import com.walktour.base.gui.model.TreeNode;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.model.SceneInfo;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.TaskTestResult;
import com.walktour.gui.singlestation.dao.model.ThresholdTestResult;
import com.walktour.gui.singlestation.test.presenter.SceneTestBaseFragmentPresenter;
import com.walktour.gui.singlestation.test.service.SceneTestMonitorStartService;
import com.walktour.service.app.DataTransService;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 场景测试视图基础类
 * Created by wangk on 2017/6/13.
 */

public abstract class SceneTestBaseFragment extends BaseListFragment<TreeNode> {
    /**
     * 结果列表
     */
    @BindView(R.id.list_view)
    ListView mResultList;
    /**
     * 场景对象
     */
    private SceneInfo mSceneInfo;

    /**
     * @param titleId          标题ID
     * @param fragmentLayoutId 视图关联的布局资源ID
     * @param rowLayoutId      行布局资源ID
     */
    public SceneTestBaseFragment(int titleId, int fragmentLayoutId, int rowLayoutId) {
        super(titleId, fragmentLayoutId, rowLayoutId);
    }

    @Override
    protected void onCreateView() {
        mResultList.setDivider(null);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.NOTIFY_TESTJOBDONE);
        filter.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE);
        filter.addAction(DataTransService.EXTRA_DATA_TRANS_FILE_START);
        filter.addAction(DataTransService.EXTRA_DATA_TRANS_FILE_END);
        filter.addAction(DataTransService.EXTRA_DATA_TRANS_FILE_PROGRESS);
        filter.addAction(DataTransService.EXTRA_DATA_TRANS_MESSAGE);
        getActivity().registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public int[] showActivityMenuItemIds() {
        return new int[]{R.id.single_station_scene_test_info, R.id.single_station_scene_to_survy, R.id.single_station_scene_error_report};
    }

    @Override
    public void dealMessageFromService(ServiceMessage message) {
        if (message.getAction().equals(SceneTestMonitorStartService.MESSAGE_TEST_FINISH)) {
            this.getPresenter().loadData();
        }
    }

    public SceneInfo getSceneInfo() {
        return mSceneInfo;
    }

    public void setSceneInfo(SceneInfo sceneInfo) {
        mSceneInfo = sceneInfo;
    }

    boolean isRunningJob = false;

    /**
     * 显示开始执行按钮
     *
     * @param isRunning 当前是否执行中
     */
    public void showStartButton(boolean isRunning) {
        isRunningJob = isRunning;
        notifyDataSetChanged();
    }

    public abstract SceneTestBaseFragmentPresenter getPresenter();

    @Override
    protected AbsListView getListView() {
        return this.mResultList;
    }

    @Override
    protected BaseHolder createViewHolder() {
        return new ViewHolder();
    }

    /**
     * 记录同一场景类型下不同场景名字的开关是否打开的集合
     */
    private SparseBooleanArray mSceneSwitchArray = new SparseBooleanArray();

    /**
     * 存放控件 的ViewHolder
     */
    public class ViewHolder extends BaseHolder<TreeNode> {
        /**
         * 测试任务勾选
         */
        @BindView(R.id.task_check)
        ImageButton mTaskCheck;
        /**
         * 任务名称
         */
        @BindView(R.id.task_name)
        TextView mTaskName;
        /**
         * 指标名称
         */
        @BindView(R.id.threshold_name)
        TextView mThresholdName;
        /**
         * 指标名称
         */
        @BindView(R.id.threshold_value)
        TextView mThresholdValue;
        /**
         * 实测值
         */
        @BindView(R.id.real_value)
        TextView mRealValue;
        /**
         * 结果
         */
        @BindView(R.id.test_result)
        TextView mTestResult;
        /**
         * 结果行
         */
        @BindView(R.id.test_result_layout)
        LinearLayout mResultLayout;
        /**
         * 测试业务行
         */
        @BindView(R.id.test_task_layout)
        LinearLayout mTestTaskLayout;


        /**
         * 控制显隐开关容器
         */
        @BindView(R.id.switch_layout)
        LinearLayout mSwitchLayout;

        /**
         * 场景名称
         */
        @BindView(R.id.tv_scene_name)
        TextView mTvSceneName;

        /**
         * 控制显隐开关
         */
        @BindView(R.id.switch_controller)
        Switch mSwitchController;

        @BindView(R.id.ib_upload)
        ImageButton mIbUpload;

        @BindView(R.id.ib_start_test)
        ImageButton mIbStartTest;

        /**
         * 关联的场景测试
         */
        private TaskTestResult mTaskTestResult;


        /**
         * 是否第一次进入标志
         */
        private boolean isFirstEnter = true;

//        private TaskGroupConfig taskGroupConfig;

        public ViewHolder() {
//            taskGroupConfig = TaskListDispose.getInstance().getGroup(getPresenter().getStationName(mSceneInfo.getStationId()));
        }

        @Override
        public void setData(int position, TreeNode data) {
            switch (data.getLevel()) {
                //显示第一层级:场景名数据
                case SceneTestBaseFragmentPresenter.TREE_LEVEL_SCENE_NAME:
                    showSceneNameRow(position, data);
                    break;
                //显示第二层级:测试任务数据
                case SceneTestBaseFragmentPresenter.TREE_LEVEL_TEST_TASK:
                    showTaskTestRow(data);
                    break;
                //显示第三层级:任务指标数据
                case SceneTestBaseFragmentPresenter.TREE_LEVEL_KPI:
                    showThresholdResultRow(data);
                    break;
                default:
            }
        }


        /**
         * 显示场景名行
         *
         * @param data 数据对象
         */
        private void showSceneNameRow(final int position, final TreeNode data) {
            this.mSwitchLayout.setVisibility(View.VISIBLE);
            this.mTestTaskLayout.setVisibility(View.GONE);
            this.mResultLayout.setVisibility(View.GONE);
            this.mIbStartTest.setImageResource(isRunningJob ? R.drawable.singlestation_test_stop : R.drawable.singlestation_test_start);
            final SceneInfo sceneInfo = (SceneInfo) data.getObject();
            //上传测试结果按钮
            mIbUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPresenter().uploadTestResult(sceneInfo);
                }
            });

            //开始测试按钮
            mIbStartTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(sceneInfo.getRecordId()) && !sceneInfo.getUploaded()) {
                        //如果已有测试记录并且记录还未上传，弹出提示框
                        Sure2RetestDlgFragment dlgFragment = new Sure2RetestDlgFragment();
                        dlgFragment.setOnClickListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getPresenter().startOrStopTest(sceneInfo);
                            }
                        });
                        dlgFragment.show(getFragmentManager(), dlgFragment.getClass().getSimpleName());
                    } else {
                        getPresenter().startOrStopTest(sceneInfo);
                    }
                }
            });
            mTvSceneName.setText(getPresenter().getSceneDisplayNameResId(sceneInfo));
            mSwitchController.setChecked((position == 0 && isFirstEnter) || mSceneSwitchArray.get(position));
            data.setExpanded((position == 0 && isFirstEnter) || mSceneSwitchArray.get(position));
            if (position == 0 && isFirstEnter) {
                mSceneSwitchArray.put(0, true);
            }
            Intent intent = getActivity().getIntent();
            StationInfo stationInfo = null;
            if (intent.hasExtra("station_info")) {
                stationInfo = intent.getParcelableExtra("station_info");
            }
            isFirstEnter = false;
            mIbUpload.setVisibility(stationInfo != null && stationInfo.getFromType() == StationInfo.FROM_TYPE_PLATFORM && mSwitchController.isChecked() ? View.VISIBLE : View.GONE);
            mIbStartTest.setVisibility(mSwitchController.isChecked() ? View.VISIBLE : View.GONE);
            notifyDataSetChanged();
            mSwitchController.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mSceneSwitchArray.put(position, isChecked);
                    data.setExpanded(isChecked);
                    notifyDataSetChanged();
                }
            });

        }

        /**
         * 显示阈值测试行
         *
         * @param data 数据
         */
        private void showThresholdResultRow(TreeNode data) {
            this.mTestTaskLayout.setVisibility(View.GONE);
            this.mSwitchLayout.setVisibility(View.GONE);
            this.mResultLayout.setVisibility(data.getParent().isExpanded() ? View.VISIBLE : View.GONE);
            ThresholdTestResult result = (ThresholdTestResult) data.getObject();
            this.mThresholdName.setText(getString(getPresenter().getThresholdKeyResId(result.getThresholdKey())));
            String value = result.getOperator();
            value += (int) result.getThresholdValue() + result.getThresholdUnit();
            this.mThresholdValue.setText(value);
            if (result.getTestStatus() != ThresholdTestResult.TEST_STATUS_INIT) {
                int tempValue = (int) result.getRealValue();//保存强转为int类型的值
                String realValue;
                if (tempValue == result.getRealValue()) {
                    //整数,直接用该整数拼接单位
                    realValue = tempValue + result.getThresholdUnit();
                } else {
                    //非整数，保留小数点后一位再拼接单位
                    realValue = new DecimalFormat("#.#").format(result.getRealValue()) + result.getThresholdUnit();
                }
                this.mRealValue.setText(realValue);
            } else {
                this.mRealValue.setText("");
            }
            switch (result.getTestStatus()) {
                case ThresholdTestResult.TEST_STATUS_PASS:
                    this.mTestResult.setText(R.string.single_station_test_result_success);
                    this.mTestResult.setTextColor(Color.parseColor("#39B54A"));
                    break;
                case ThresholdTestResult.TEST_STATUS_FAULT:
                    this.mTestResult.setText(R.string.single_station_test_result_fault);
                    this.mTestResult.setTextColor(Color.parseColor("#FF0000"));
                    break;
                default:
                    this.mTestResult.setText("");
                    break;
            }
        }

        /**
         * 点击业务勾选框
         */
        @OnClick(R.id.task_check)
        void clickTaskCheck() {
            this.mTaskTestResult.setCheck(!this.mTaskTestResult.isCheck());
            if (this.mTaskTestResult.isCheck())
                this.mTaskCheck.setImageResource(R.drawable.btn_check_on);
            else
                this.mTaskCheck.setImageResource(R.drawable.btn_check_off);
        }

        /**
         * 显示测试任务行
         *
         * @param data 数据
         */
        private void showTaskTestRow(final TreeNode data) {
            this.mTestTaskLayout.setVisibility(data.getParent().isExpanded() ? View.VISIBLE : View.GONE);
            mTestTaskLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    getPresenter().jump2EditTask((SceneInfo) data.getParent().getObject(), (TaskTestResult) data.getObject(), taskGroupConfig);
                }
            });
            this.mResultLayout.setVisibility(View.GONE);
            this.mSwitchLayout.setVisibility(View.GONE);
            this.mTaskTestResult = (TaskTestResult) data.getObject();
            this.mTaskName.setText(this.mTaskTestResult.getTaskTypeName());
            if (this.mTaskTestResult.isCheck())
                this.mTaskCheck.setImageResource(R.drawable.btn_check_on);
            else
                this.mTaskCheck.setImageResource(R.drawable.btn_check_off);
        }

    }

    /**
     * 广播监听器
     */
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getPresenter().onReceiveUploadTestResultBroadcast(intent);
        }
    };
}
