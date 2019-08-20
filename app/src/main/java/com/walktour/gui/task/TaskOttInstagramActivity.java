package com.walktour.gui.task;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dingli.ott.task.service.WalktourAutoService;
import com.dingli.ott.util.OttUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.ToastUtil;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.ott.TaskMultipleAppTestModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Instagram 自动化点击测试
 *
 * @author czc
 */
public class TaskOttInstagramActivity extends BaseTaskActivity implements OnItemSelectedListener {

    /**
     * 任务解析类
     */
    TaskListDispose mTaskDispose = null;
    /**
     * 对象模型
     */
    TaskMultipleAppTestModel mModel = null;
    /**
     * 任务列表ID
     */
    private int mTaskListId = -1;
    /**
     * 是否新建任务
     */
    private boolean isNew = true;

    @BindView(R.id.title_txt)
    TextView mTaskTitle;
    /**
     * 测试次数
     */
    @BindView(R.id.edit_repeat)
    EditText mRepeatEditText;
    /**
     * 发送文本编辑行
     */
    @BindView(R.id.edit_send_text_layout)
    RelativeLayout mSendTextLayout;
    /**
     * 发送文本
     */
    @BindView(R.id.edit_search_tag)
    EditText mSearchTagEditText;

    /**
     * 业务超时(s)
     */
    @BindView(R.id.edit_taskTimeout)
    EditText mTaskTimeoutEditText;

    //开启辅助权限
    @BindView(R.id.btn_open_permission)
    Button mBtnOpenPermission;
    /**
     * 任务对象
     */
    private TaskRabModel mTaskRabModel;

    /**
     * ott 测试子任务的类型
     */
    private WalkStruct.TaskType mCurrentTaskType = WalkStruct.TaskType.Instagram_Ott;

    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentParams();
        initView();
        initData();
        register();
    }

    private void getIntentParams() {
        mTaskDispose = TaskListDispose.getInstance();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("taskListId")) {
            mTaskListId = bundle.getInt("taskListId");
            if (RABTAG.equals(super.getRabTag())) {
                for (int i = 0; i < mTaskDispose.getCurrentTaskList().size(); i++) {
                    if (mTaskDispose.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())) {
                        mModel = (TaskMultipleAppTestModel) ((TaskRabModel) (mTaskDispose.getCurrentTaskList().get(i))).getTaskModel()
                                .get(mTaskListId);
                        break;
                    }
                }
            } else {
                mModel = (TaskMultipleAppTestModel) mTaskDispose.getTaskListArray().get(mTaskListId);
            }
            abstModel = mModel;
            isNew = false;
        }
    }

    private void initView() {
        setContentView(R.layout.task_instagram);
        ButterKnife.bind(this);

        // 设置标题
        mTaskTitle.setText(mCurrentTaskType.getXmlTaskType());

        findViewById(R.id.pointer).setOnClickListener(this);
        findViewById(R.id.advanced_arrow_rel).setOnClickListener(this);

        addEditTextWatcher(mRepeatEditText, R.string.task_alert_nullRepeat, true);

        if (mModel != null) {
            this.mRepeatEditText.setText(String.valueOf(mModel.getRepeat()));
            this.mSearchTagEditText.setText(mModel.getSendText());
            this.mTaskTimeoutEditText.setText(String.valueOf(mModel.getTaskTimeout()));
        } else {
            this.mRepeatEditText.setText("1");
            this.mSearchTagEditText.setText("good");
            this.mTaskTimeoutEditText.setText("60");
        }
    }

    private void initData() {
        mReceiver = new OttPermissionReceiver(this, mBtnOpenPermission);
    }

    private void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalktourAutoService.ACTION_SERVICE_CONNECTED);
        filter.addAction(WalktourAutoService.ACTION_SERVICE_UNCONNECTED);
        registerReceiver(mReceiver, filter);
    }

    @OnClick(R.id.btn_ok)
    void clickSaveBtn() {
        saveTestTask();
    }

    @OnClick(R.id.btn_cencle)
    void clickCancleBtn() {
        finish();
    }

    @OnClick(R.id.btn_open_permission)
    void clickOpenPermisson() {
        OttUtil.openServicePermissonCompat(this, WalktourAutoService.class);
    }

    @Override
    public void saveTestTask() {

        if (!OttUtil.hasServicePermission(this, WalktourAutoService.class)) {
            ToastUtil.showShort(this, getString(R.string.open_auto_test_pemission_dialog_tips));
            return;
        }

        if (mModel == null) {
            mModel = new TaskMultipleAppTestModel();
            mTaskDispose.setCurrentTaskIdAndSequence(mModel);
        }
        // 依据标记区分用户名的编辑
        mModel.setTaskName(mCurrentTaskType.getXmlTaskType());
        mModel.setTaskType(mCurrentTaskType.name());
        mModel.setEnable(1);
        mModel.setRepeat(Integer.parseInt(mRepeatEditText.getText().toString().trim().length() == 0 ? "1"
                : mRepeatEditText.getText().toString().trim()));
        mModel.setSendText(this.mSearchTagEditText.getText().toString());
        mModel.setTaskTimeout(Integer.parseInt(this.mTaskTimeoutEditText.getText().toString().trim()));

        List<TaskModel> array = mTaskDispose.getTaskListArray();
        if (RABTAG.equals(super.getRabTag())) {// 依标志区分并发与普通业务
            for (int i = 0; i < mTaskDispose.getCurrentTaskList().size(); i++) {
                if (super.getMultiRabName().equals(mTaskDispose.getCurrentTaskList().get(i).getTaskID())) {
                    mTaskRabModel = (TaskRabModel) mTaskDispose.getCurrentTaskList().get(i);
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
        mTaskDispose.setTaskListArray(array);


        ToastUtil.showLong(this, isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess);
        finish();
    }


    public void addEditTextWatcher(final EditText editText, final int strId, final boolean isNumber) {

        editText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String text = editText.getText().toString().trim();
                if (StringUtil.isNullOrEmpty(text) || (isNumber && text.equals("0")))
                    editText.setError(getString(strId));
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.edit_operation_type) {
            if (position == 0) {
                this.mSendTextLayout.setVisibility(View.VISIBLE);
            } else {
                this.mSendTextLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
