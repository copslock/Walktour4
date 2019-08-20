package com.walktour.gui.task;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dingli.ott.task.service.WalktourAutoService;
import com.dingli.ott.util.OttUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.ToastUtil;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.ott.TaskMultipleAppTestModel;
import com.walktour.service.bluetoothmos.BluetoothMOSFactory;

import java.util.List;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * WhatsApp 拨打语音自动化点击测试
 *
 * @author czc
 */
public class TaskOttWhatsAppMocActivity extends BaseTaskActivity {

    @BindArray(R.array.mos_counted)
    String[] mosCountArr;

    @BindArray(R.array.mos_testtype)
    String[] mosTestTypeArr;

    @BindString(R.string.task_callMOS_no_device)
    String noDevice;

    @BindString(R.string.task_callMOS_choose_device)
    String chooseDevice;

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
     * 语音拨打时长(s)
     */
    @BindView(R.id.edit_audiocallsecond)
    EditText mAudioCallSecondsText;
    /**
     * 业务超时(s)
     */
    @BindView(R.id.edit_taskTimeout)
    EditText mTaskTimeoutEditText;
    /**
     * 被叫好友名称
     */
    @BindView(R.id.edit_call_friend)
    EditText mContactNameEditText;

    /**
     * 开启辅助权限
     */
    @BindView(R.id.btn_open_permission)
    Button mBtnOpenPermission;

    /**
     * 视频通话
     */
    @BindView(R.id.spinner_callMode)
    BasicSpinner callModeSpinner;

    /**
     * 任务对象
     */
    private TaskRabModel mTaskRabModel;


    /**
     * ott 测试子任务的类型
     */
    private WalkStruct.TaskType mCurrentTaskType = WalkStruct.TaskType.WhatsAppMoc;

    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getIntentParams();
        initView();
        initData();
        register();
        setListner();
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
        setContentView(R.layout.task_whatsapp_moc);
        ButterKnife.bind(this);

        // 设置标题
        mTaskTitle.setText(mCurrentTaskType.getTypeName());

        findViewById(R.id.pointer).setOnClickListener(this);
        findViewById(R.id.advanced_arrow_rel).setOnClickListener(this);

        addEditTextWatcher(mRepeatEditText, R.string.task_alert_nullRepeat, true);
        addEditTextWatcher(mContactNameEditText, R.string.task_export_name_null, false);


        setSpinnerAdapter(callModeSpinner, R.array.public_yn);


        if (mModel != null) {
            this.mContactNameEditText.setText(mModel.getContactName());
            this.mRepeatEditText.setText(String.valueOf(mModel.getRepeat()));
            this.mTaskTimeoutEditText.setText(String.valueOf(mModel.getTaskTimeout()));
            this.mAudioCallSecondsText.setText(String.valueOf(mModel.getAudioCallSeconds()));
            this.callModeSpinner.setSelection(mModel.getDialMode() == 1 ? 1 : 0);
        } else {
            this.mRepeatEditText.setText("1");
            this.mTaskTimeoutEditText.setText("60");
            this.mAudioCallSecondsText.setText("8");
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

    private void setListner() {

    }

    @OnClick(R.id.btn_ok)
    void clickSaveBtn() {
        saveTestTask();
    }

    @OnClick(R.id.btn_cencle)
    void clickCancleBtn() {
        finish();
    }

    @Override
    public void saveTestTask() {

        if (!OttUtil.hasServicePermission(this, WalktourAutoService.class)) {
            ToastUtil.showShort(this, getString(R.string.open_walktour_wx_auto_service));
            return;
        }

        if (StringUtil.isEmpty(mContactNameEditText.getText().toString().trim())) {
            ToastUtil.showShort(this, getString(R.string.task_export_name_null));
            return;
        }
        if (mModel == null) {
            mModel = new TaskMultipleAppTestModel();
            mTaskDispose.setCurrentTaskIdAndSequence(mModel);
        }
        // 依据标记区分用户名的编辑
        mModel.setTaskName(mCurrentTaskType.getTypeName());
        mModel.setContactName(mContactNameEditText.getText().toString().trim());
        mModel.setTaskType(mCurrentTaskType.getXmlTaskType());
        mModel.setEnable(1);
        mModel.setRepeat(Integer.parseInt(mRepeatEditText.getText().toString().trim().length() == 0 ? "1"
                : mRepeatEditText.getText().toString().trim()));
        mModel.setTaskTimeout(Integer.parseInt(this.mTaskTimeoutEditText.getText().toString().trim()));
        mModel.setAudioCallMode(0);
        mModel.setDialMode(callModeSpinner.getSelectedItemPosition() == 1 ? 1 : 2);
        mModel.setAudioCallSeconds(Integer.parseInt(this.mAudioCallSecondsText.getText().toString().trim()));

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
                if (StringUtil.isNullOrEmpty(text) || (isNumber && text.equals("0"))) {
                    editText.setError(getString(strId));
                }
            }
        });
    }


    private void setSpinnerAdapter(BasicSpinner spinner, int arraryId) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_custom_layout, getResources().getStringArray(arraryId));
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
