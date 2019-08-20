package com.walktour.gui.task;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dingli.ott.task.service.WalktourAutoService;
import com.dingli.ott.util.OttUtil;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.ToastUtil;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.mos.MosMatchActivity;
import com.walktour.gui.setting.bluetoothmos.BluetoothMosTabActivity;
import com.walktour.gui.setting.bluetoothmos.BluetoothPipeLine;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.ott.TaskWeCallModel;
import com.walktour.service.bluetoothmos.BluetoothMOSFactory;

import java.util.List;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * WeChat 主叫自动化点击测试
 *
 * @author czc
 */
public class TaskOttWxMocActivity extends BaseTaskActivity {

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
    TaskWeCallModel mModel = null;
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
     * 间隔时长(s)
     */
    @BindView(R.id.edit_interVal)
    EditText mInterValEditText;

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
    TextView mContactNameEditText;

    /**
     * 开启辅助权限
     */
    @BindView(R.id.btn_open_permission)
    Button mBtnOpenPermission;

    /**
     * Mos
     */
    @BindView(R.id.edit_callMOSServer)
    BasicSpinner callMOSServerSpinner;
    /**
     * POLQA算分
     */
    @BindView(R.id.edit_callMOSCountd)
    BasicSpinner callMOSCountdSpinner;
    /**
     * POLQA 样本文件列表
     */
    @BindView(R.id.edit_callMOS_PolqaSimple)
    BasicSpinner callMOSPOLQASampleSpinner;
    /**
     * POLQA 算分算法
     */
    @BindView(R.id.edit_callMOS_PolqaCalc)
    BasicSpinner callMOSPOLQACalcSpinner;
    /**
     * 蓝牙MOS选择
     */
    @BindView(R.id.edit_callMOSBluetooth)
    BasicSpinner mCallMOSBluetoothSpinner;
    /**
     * 实时算分
     **/
    @BindView(R.id.mos_calc)
    BasicSpinner callMosCalcSpinner;

    /**
     * 匹配手机
     */
    @BindView(R.id.spinner_match_phone)
    BasicSpinner mCallMatchPhoneSpinner;
    /**
     * 蓝牙设备选择
     */
    private String[] mBluetoothDevices;

    /**
     * 任务对象
     */
    private TaskRabModel mTaskRabModel;

    /**
     * 蓝牙MOS头工厂类
     */
    private BluetoothMOSFactory mFactory;

    /**
     * ott 测试子任务的类型
     */
    private WalkStruct.TaskType mCurrentTaskType = WalkStruct.TaskType.WeCallMoc;

    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mFactory = BluetoothMOSFactory.get();

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
                        mModel = (TaskWeCallModel) ((TaskRabModel) (mTaskDispose.getCurrentTaskList().get(i))).getTaskModel()
                                .get(mTaskListId);
                        break;
                    }
                }
            } else {
                mModel = (TaskWeCallModel) mTaskDispose.getTaskListArray().get(mTaskListId);
            }
            abstModel = mModel;
            isNew = false;
        }
    }

    private void initView() {
        setContentView(R.layout.task_wx_moc);
        ButterKnife.bind(this);

        // 设置标题
        mTaskTitle.setText(mCurrentTaskType.getTypeName());

        findViewById(R.id.pointer).setOnClickListener(this);
        findViewById(R.id.advanced_arrow_rel).setOnClickListener(this);

        addEditTextWatcher(mRepeatEditText, R.string.task_alert_nullRepeat, true);
        addEditTextWatcher(mInterValEditText, R.string.task_alert_nullInterval, true);
//        addEditTextWatcher(mContactNameEditText, R.string.multipleapptest_null_mtc_name, false);


        boolean hasPolqa = hasTaskAuth(WalkStruct.TaskType.POLQA);
        boolean hasPolqaAndMos = hasPolqa && hasTaskAuth(WalkStruct.TaskType.MOS);
        String[] mosCounted = hasPolqaAndMos ? mosCountArr : hasPolqa ? new String[]{mosCountArr[1]} : new String[]{mosCountArr[0]};

        String[] mBluetoothDevices = new String[]{noDevice, chooseDevice};
        if (this.mFactory.getCurrMOCDevice() != null) {
            mBluetoothDevices[0] = this.mFactory.getCurrMOCDevice().getAddress();
        }

        setSpinnerAdapter(callMOSServerSpinner, R.array.public_yn);
        setSpinnerAdapter(callMosCalcSpinner, R.array.public_yn);
        setSpinnerAdapter(callMOSPOLQACalcSpinner, R.array.mos_polqa_calc);
        setSpinnerAdapter(callMOSPOLQASampleSpinner, R.array.mos_polqa_sample);
        setSpinnerAdapter(callMOSCountdSpinner, mosCounted);
        setSpinnerAdapter(mCallMOSBluetoothSpinner, mBluetoothDevices);


        if (mModel != null) {
//            this.mContactNameEditText.setText(mModel.getContactName());
            this.mRepeatEditText.setText(String.valueOf(mModel.getRepeat()));
            this.mTaskTimeoutEditText.setText(String.valueOf(mModel.getConnectTime()));
            this.mInterValEditText.setText(String.valueOf(mModel.getInterVal()));
            this.mAudioCallSecondsText.setText(String.valueOf(mModel.getKeepTime()));

            callMOSServerSpinner.setSelection(mModel.getMosTest());
            callMOSCountdSpinner.setSelection(mModel.getCallMOSCount());
            callMOSPOLQASampleSpinner.setSelection(mModel.getPolqaSample());
            callMOSPOLQACalcSpinner.setSelection(mModel.getPolqaCalc());
            callMosCalcSpinner.setSelection(mModel.getTestConfig().isRealtimeCalculation() ? 1 : 0);
        } else {
            this.mRepeatEditText.setText("1");
            this.mInterValEditText.setText("10");
            this.mTaskTimeoutEditText.setText("60");
            this.mAudioCallSecondsText.setText("8");
        }
    }

    private boolean hasTaskAuth(WalkStruct.TaskType type) {
        return ApplicationModel.getInstance().getTaskList().contains(type);
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

        this.mCallMatchPhoneSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(MosMatchActivity.EXTRA_FRAGMENT_TYPE, MosMatchActivity.EXTRA_MACTCH_PHONE);
                    jumpActivity(MosMatchActivity.class, bundle);
                }
            }
        });

        callMOSServerSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isMos = position == 1;
                boolean isPolqa = callMOSCountdSpinner.getSelectedItemPosition() == 1;
                callMosCalcSpinner.setEnabled(isMos);
                callMOSPOLQACalcSpinner.setEnabled(isMos && isPolqa);
                callMOSPOLQASampleSpinner.setEnabled(isMos && isPolqa);
                callMOSCountdSpinner.setEnabled(isMos);
                mCallMOSBluetoothSpinner.setEnabled(isMos);
                mCallMatchPhoneSpinner.setEnabled(isMos);
            }
        });

        callMOSCountdSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isPolqa = position == 1;
                callMOSPOLQASampleSpinner.setEnabled(isPolqa);
                callMOSPOLQACalcSpinner.setEnabled(isPolqa);
            }
        });

        mCallMOSBluetoothSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isSeleceDevices = position == 1;
                if (isSeleceDevices) {
                    jumpActivity(BluetoothMosTabActivity.class);
                }
            }
        });
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
            ToastUtil.showShort(this, getString(R.string.multipleapptest_null_mtc_name));
            return;
        }
        if (mModel == null) {
            mModel = new TaskWeCallModel();
            mTaskDispose.setCurrentTaskIdAndSequence(mModel);
        }
        // 依据标记区分用户名的编辑
        mModel.setTaskName(mCurrentTaskType.getTypeName());
        mModel.setContactName("friend");
        mModel.setTaskType(mCurrentTaskType.getXmlTaskType());
        mModel.setEnable(1);
        mModel.setRepeat(Integer.parseInt(mRepeatEditText.getText().toString().trim().length() == 0 ? "1"
                : mRepeatEditText.getText().toString().trim()));
        mModel.setInterVal(Integer.parseInt(mInterValEditText.getText().toString().trim().length() == 0 ? "10"
                : mInterValEditText.getText().toString().trim()));
        mModel.setConnectTime(Integer.parseInt(this.mTaskTimeoutEditText.getText().toString().trim()));
        mModel.setKeepTime(Integer.parseInt(this.mAudioCallSecondsText.getText().toString().trim()));

        mModel.setMosTest(callMOSServerSpinner.getSelectedItemPosition());
        mModel.setCallMOSCount(callMOSCountdSpinner.getSelectedItemPosition());
        mModel.getTestConfig().setRealtimeCalculation(callMosCalcSpinner.getSelectedItemPosition() == 0 ? false : true);
        mModel.setPolqaSample(callMOSPOLQASampleSpinner.getSelectedItemPosition());
        mModel.setPolqaCalc(callMOSPOLQACalcSpinner.getSelectedItemPosition());

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

    private void setSpinnerAdapter(BasicSpinner spinner, String[] arrary) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_custom_layout, arrary);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    private void initAdapetArrayList() {
        String noDevice = this.getResources().getString(R.string.task_callMOS_no_device);
        String chooseDevice = this.getResources().getString(R.string.task_callMOS_choose_device);
        mBluetoothDevices = new String[]{noDevice, chooseDevice};
        if (this.mFactory.getCurrMOCDevice() != null) {
            mBluetoothDevices[0] = this.mFactory.getCurrMOCDevice().getAddress();
        }
        setSpinnerAdapter(mCallMOSBluetoothSpinner, mBluetoothDevices);

        String[] phoneDevices = new String[]{noDevice, chooseDevice};
        if (BluetoothPipeLine.getInstance().getConnectDevice() != null) {
            phoneDevices[0] = BluetoothPipeLine.getInstance().getConnectDevice().getAddress();
        }
        setSpinnerAdapter(mCallMatchPhoneSpinner, phoneDevices);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initAdapetArrayList();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
