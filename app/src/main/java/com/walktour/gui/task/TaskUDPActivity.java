package com.walktour.gui.task;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.control.bean.Verify;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.udp.TaskUDPModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by yi.lin on 2017/8/11.
 * <p>
 * UDP测试业务
 */

public class TaskUDPActivity extends BaseTaskActivity {

    @BindView(R.id.title_txt)
    TextView mTitleTxt;
    @BindView(R.id.et_task_name)
    EditText mEtTaskName;
    @BindView(R.id.et_task_times)
    EditText mEtTaskTimes;
    @BindView(R.id.cb_unlimited)
    CheckBox mCbUnlimited;
    @BindView(R.id.et_server_ip)
    EditText mEtServerIp;
    @BindView(R.id.et_server_port)
    EditText mEtServerPort;
    @BindView(R.id.et_packet_size)
    EditText mEtPacketSize;
    @BindView(R.id.et_send_packet_interval)
    EditText mEtSendPacketInterval;
    @BindView(R.id.et_send_packet_duration)
    EditText mEtSendPacketDuration;
    @BindView(R.id.et_test_duration)
    EditText mEtTestDuration;
    @BindView(R.id.task_sort_title_txt)
    TextView mTaskSortTitleTxt;
    @BindView(R.id.advanced_arrow)
    ImageView mAdvancedArrow;
    @BindView(R.id.advanced_arrow_rel)
    RelativeLayout mAdvancedArrowRel;
    @BindView(R.id.et_interval)
    EditText mEtInterval;
    @BindView(R.id.rl_interval)
    RelativeLayout mRlInterval;
    @BindView(R.id.et_no_data_timeout)
    EditText mEtNoDataTimeout;
    @BindView(R.id.rl_no_data_timeout)
    RelativeLayout mRlNoDataTimeout;
    @BindView(R.id.task_advanced_layout)
    LinearLayout mTaskAdvancedLayout;
    @BindView(R.id.sv_container)
    ScrollView mSvContainer;
    @BindView(R.id.spinner_test_mode)
    BasicSpinner mSpinnerTestMode;


    private int mTaskListId = -1;
    private boolean isNew = true;
    private TaskListDispose mTaskListDispose = TaskListDispose.getInstance();
    private TaskUDPModel mModel = null;
    private TaskRabModel mTaskRabModel;
    private ArrayAdapter<String> mTestModeAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_udp);
        ButterKnife.bind(this);
        getBundleData();
        initView();
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("taskListId")) {
            mTaskListId = bundle.getInt("taskListId");
            //根据标记做并发编辑
            if (RABTAG.equals(super.getRabTag())) {
                for (int i = 0; i < mTaskListDispose.getCurrentTaskList().size(); i++) {
                    if (mTaskListDispose.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())) {
                        mModel = (TaskUDPModel) ((TaskRabModel) (mTaskListDispose.getCurrentTaskList().get(i))).getTaskModel().get(mTaskListId);
                        break;
                    }
                }
            } else {
                mModel = (TaskUDPModel) mTaskListDispose.getTaskListArray().get(mTaskListId);
            }
            abstModel = mModel;
            isNew = false;
        }
    }

    private void initView() {
        mTitleTxt.setText(getString(R.string.act_task_udp));
        mTestModeAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_custom_layout,
                getResources().getStringArray(R.array.array_task_udp_test_mode));
        mTestModeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSpinnerTestMode.setAdapter(mTestModeAdapter);
        inflateModelData2View();
    }

    private void inflateModelData2View() {
        if (null != mModel) {
            mEtTaskName.setText(mModel.getTaskName().trim().substring(mModel.getTaskName().indexOf("%") + 1, mModel.getTaskName().trim().length()));
            mEtTaskTimes.setText(String.valueOf(mModel.getRepeat()));
            mEtServerIp.setText(mModel.getServerIP());
            mEtServerPort.setText(mModel.getServerPort());
            mCbUnlimited.setChecked(mModel.isInfinite());
            mEtTaskTimes.post(new Runnable() {
                @Override
                public void run() {
                    mEtTaskTimes.setEnabled(!mModel.isInfinite());
                }
            });
            mSpinnerTestMode.setSelection(TextUtils.isEmpty(mModel.getTestMode()) ? 0 : Integer.parseInt(mModel.getTestMode()));
            mEtPacketSize.setText(mModel.getPacketSize());
            mEtSendPacketInterval.setText(mModel.getSendPacketInterval());
            mEtSendPacketDuration.setText(mModel.getSendPacketDuration());
            mEtTestDuration.setText(mModel.getTestDuration());
            mEtInterval.setText(String.valueOf(mModel.getInterVal()));
            mEtNoDataTimeout.setText(mModel.getNoDataTimeout());
        }
    }

    @Override
    public void saveTestTask() {
        if (verifyInputContent()) {
            //输入内容检测合法之后做保存操作
            doSaveTestTask();
        }
    }

    /**
     * 保存测试任务
     */
    private void doSaveTestTask() {
        if (null == mModel) {
            mModel = new TaskUDPModel();
            mTaskListDispose.setCurrentTaskIdAndSequence(mModel);
        }
        mModel.setTaskName(mEtTaskName.getText().toString().trim());
        mModel.setTaskType(WalkStruct.TaskType.UDP.name());
        mModel.setEnable(1);
        mModel.setRepeat(Integer.parseInt(mEtTaskTimes.getText().toString().trim().length() == 0 ? "1" : mEtTaskTimes.getText().toString().trim()));
        mModel.setInfinite(mCbUnlimited.isChecked());
        mModel.setServerIP(mEtServerIp.getText().toString().trim());
        mModel.setServerPort(mEtServerPort.getText().toString().trim());
        mModel.setTestMode(String.valueOf(mSpinnerTestMode.getSelectedItemPosition()));
        mModel.setPacketSize(mEtPacketSize.getText().toString().trim());
        mModel.setSendPacketInterval(mEtSendPacketInterval.getText().toString().trim());
        mModel.setSendPacketDuration(mEtSendPacketDuration.getText().toString().trim());
        mModel.setTestDuration(mEtTestDuration.getText().toString().trim());
        mModel.setInterVal(Integer.parseInt(mEtInterval.getText().toString().trim().length() == 0 ? "15" : mEtInterval.getText().toString().trim()));
        mModel.setNoDataTimeout(mEtNoDataTimeout.getText().toString().trim().length() == 0 ? "30" : mEtNoDataTimeout.getText().toString().trim());

        List<TaskModel> array = mTaskListDispose.getTaskListArray();
        if (RABTAG.equals(super.getRabTag())) {//依标志区分并发与普通业务
            for (int i = 0; i < mTaskListDispose.getCurrentTaskList().size(); i++) {
                if (super.getMultiRabName().equals(mTaskListDispose.getCurrentTaskList().get(i).getTaskID())) {
                    mTaskRabModel = (TaskRabModel) mTaskListDispose.getCurrentTaskList().get(i);
                    break;
                }
            }
            if (isNew) {
                mTaskRabModel.setTaskModelList(mTaskRabModel.addTaskList(mModel));
            } else {
                mTaskRabModel.getTaskModel().remove(mTaskListId);
                mTaskRabModel.getTaskModel().add(mTaskListId, mModel);
            }
        } else {
            if (isNew) {
                array.add(array.size(), mModel);
            } else {
                array.remove(mTaskListId);
                array.add(mTaskListId, mModel);
            }
        }
        mTaskListDispose.setTaskListArray(array);

        Toast.makeText(getApplicationContext(), isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess, Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * 检测输入内容是否合法
     *
     * @return
     */
    private boolean verifyInputContent() {
        if (TextUtils.isEmpty(mEtTaskName.getText().toString().trim())) {
            //任务名为空
            ToastUtil.showToastShort(this, R.string.task_alert_nullName);
            mEtTaskName.setError(getString(R.string.task_alert_nullName));
            return false;
        } else if (TextUtils.isEmpty(mEtTaskTimes.getText().toString().trim()) || mEtTaskTimes.getText().toString().trim().equals("0")) {
            //测试次数不合法
            ToastUtil.showToastShort(this, R.string.task_alert_nullRepeat);
            mEtTaskTimes.setError(getString(R.string.task_alert_nullRepeat));
            return false;
        } else if (TextUtils.isEmpty(mEtServerIp.getText().toString().trim()) || !Verify.isIpOrUrl(mEtServerIp.getText().toString().trim())) {
            //服务器IP不合法
            ToastUtil.showToastShort(this, R.string.task_alert_nullIP);
            mEtServerIp.setError(getString(R.string.task_alert_nullIP));
            return false;
        } else if (TextUtils.isEmpty(mEtServerPort.getText().toString().trim()) || !Verify.isPort(mEtServerPort.getText().toString().trim())) {
            //服务器端口不合法
            ToastUtil.showToastShort(this, R.string.task_alert_nullPort);
            mEtServerPort.setError(getString(R.string.task_alert_nullPort));
            return false;
        } else if (TextUtils.isEmpty(mEtPacketSize.getText().toString().trim()) || !StringUtil.isRange(Integer.parseInt(mEtPacketSize.getText().toString().trim()), 0, 3000)) {
            //包大小不合法
            ToastUtil.showToastShort(this, R.string.task_udp_alert_invalid_packet_size);
            mEtPacketSize.setError(getString(R.string.task_udp_alert_invalid_packet_size));
            return false;
        } else if (TextUtils.isEmpty(mEtSendPacketInterval.getText().toString().trim()) || !StringUtil.isRange(Integer.parseInt(mEtSendPacketInterval.getText().toString().trim()), 1, 3000)) {
            //发包间隔不合法
            ToastUtil.showToastShort(this, R.string.task_udp_alert_invalid_send_packet_interval);
            mEtSendPacketInterval.setError(getString(R.string.task_udp_alert_invalid_send_packet_interval));
            return false;
        } else if (TextUtils.isEmpty(mEtSendPacketDuration.getText().toString().trim()) || !StringUtil.isRange(Integer.parseInt(mEtSendPacketDuration.getText().toString().trim()), 1, 3000)) {
            //持续发包时长不合法
            ToastUtil.showToastShort(this, R.string.task_udp_alert_invalid_send_packet_duration);
            mEtSendPacketDuration.setError(getString(R.string.task_udp_alert_invalid_send_packet_duration));
            return false;
        } else if (TextUtils.isEmpty(mEtTestDuration.getText().toString().trim()) || !StringUtil.isRange(Integer.parseInt(mEtTestDuration.getText().toString().trim()), 1, 3000)) {
            //测试时长不合法
            ToastUtil.showToastShort(this, R.string.task_udp_alert_invalid_test_duration);
            mEtTestDuration.setError(getString(R.string.task_udp_alert_invalid_test_duration));
            return false;
        }
        return true;
    }

    @OnTextChanged(value = R.id.et_task_name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTaskNameChanged(Editable editable) {
        if (mEtTaskName.getText().toString().trim().length() == 0) {
            //任务名为空
            mEtTaskName.setError(getString(R.string.task_alert_nullName));
        }
    }

    @OnTextChanged(value = R.id.et_task_times, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTaskTimesChanged(Editable editable) {
        if (TextUtils.isEmpty(mEtTaskTimes.getText().toString().trim()) || mEtTaskTimes.getText().toString().trim().equals("0")) {
            //测试次数不合法
//            ToastUtil.showToastShort(this, R.string.task_alert_nullRepeat);
            mEtTaskTimes.setError(getString(R.string.task_alert_nullRepeat));
        }
    }

    @OnTextChanged(value = R.id.et_server_ip, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onServerIPChanged(Editable editable) {
        if (TextUtils.isEmpty(mEtServerIp.getText().toString().trim()) || !Verify.isIpOrUrl(mEtServerIp.getText().toString().trim())) {
            //服务器IP不合法
//            ToastUtil.showToastShort(this, R.string.task_alert_nullIP);
            mEtServerIp.setError(getString(R.string.task_alert_nullIP));
        }
    }

    @OnTextChanged(value = R.id.et_server_port, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onServerPortChanged(Editable editable) {
        if (TextUtils.isEmpty(mEtServerPort.getText().toString().trim()) || !Verify.isPort(mEtServerPort.getText().toString().trim())) {
            //服务器端口不合法
//            ToastUtil.showToastShort(this, R.string.task_alert_nullPort);
            mEtServerPort.setError(getString(R.string.task_alert_nullPort));
        }
    }

    @OnTextChanged(value = R.id.et_packet_size, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onPacketSizeChanged(Editable editable) {
        if (TextUtils.isEmpty(mEtPacketSize.getText().toString().trim()) || !StringUtil.isRange(Integer.parseInt(mEtPacketSize.getText().toString().trim()), 0, 3000)) {
            //包大小不合法
//            ToastUtil.showToastShort(this, R.string.task_udp_alert_invalid_packet_size);
            mEtPacketSize.setError(getString(R.string.task_udp_alert_invalid_packet_size));
        }
    }

    @OnTextChanged(value = R.id.et_send_packet_interval, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onSendPacketIntervalChanged(Editable editable) {
        if (TextUtils.isEmpty(mEtSendPacketInterval.getText().toString().trim()) || !StringUtil.isRange(Integer.parseInt(mEtSendPacketInterval.getText().toString().trim()), 1, 3000)) {
            //发包间隔不合法
//            ToastUtil.showToastShort(this, R.string.task_udp_alert_invalid_send_packet_interval);
            mEtSendPacketInterval.setError(getString(R.string.task_udp_alert_invalid_send_packet_interval));
        }
    }

    @OnTextChanged(value = R.id.et_send_packet_duration, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onSendPacketDurationChanged(Editable editable) {
        if (TextUtils.isEmpty(mEtSendPacketDuration.getText().toString().trim()) || !StringUtil.isRange(Integer.parseInt(mEtSendPacketDuration.getText().toString().trim()), 1, 3000)) {
            //持续发包时长不合法
//            ToastUtil.showToastShort(this, R.string.task_udp_alert_invalid_send_packet_duration);
            mEtSendPacketDuration.setError(getString(R.string.task_udp_alert_invalid_send_packet_duration));
        }
    }

    @OnTextChanged(value = R.id.et_test_duration, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTestDurationChanged(Editable editable) {
        if (TextUtils.isEmpty(mEtTestDuration.getText().toString().trim()) || !StringUtil.isRange(Integer.parseInt(mEtTestDuration.getText().toString().trim()), 1, 3000)) {
            //测试时长不合法
//            ToastUtil.showToastShort(this, R.string.task_udp_alert_invalid_test_duration);
            mEtTestDuration.setError(getString(R.string.task_udp_alert_invalid_test_duration));
        }
    }


    @OnClick({R.id.pointer, R.id.advanced_arrow_rel, R.id.btn_cencle, R.id.btn_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.pointer:
                this.finish();
                break;
            case R.id.advanced_arrow_rel:
                super.onClick(view);
                mSvContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        mSvContainer.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
                break;
            case R.id.btn_cencle:
                finish();
                break;
            case R.id.btn_ok:
                saveTestTask();
                break;
        }
    }

    @OnCheckedChanged(R.id.cb_unlimited)
    void onUnlimitedCheckBoxChanged(CheckBox cb, boolean isChecked) {
        mEtTaskTimes.setEnabled(!isChecked);
    }
}
