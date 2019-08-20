package com.walktour.gui.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.ToastUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.framework.ui.GraySpinnerAdapter;
import com.walktour.framework.view.DateTimePickDialogUtil;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.mos.MosMatchActivity;
import com.walktour.gui.setting.bluetoothmos.BluetoothPipeLine;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.mtc.TaskPassivityCallModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.service.bluetoothmos.BluetoothMOSFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 语音被叫业务测试
 *
 * @author jianchao.wang
 */
public class TaskMTCCall extends BaseTaskActivity {

    private static final String TAG = "TaskMTCCall";

    TaskListDispose taskd = null;
    TaskPassivityCallModel model = null;
    private int taskListId = -1;
    private boolean isNew = true;
    /**
     * 任务名称
     */
    @BindView(R.id.edit_taskname)
    EditText taskNameEditText;
    /**
     * 测试次数
     */
    @BindView(R.id.edit_repeat)
    EditText repeatEditText;
    /**
     * 是否视频通话
     */
    @BindView(R.id.spinner_callMode)
    Spinner callModeSpinner;
    /**
     * 是否MOS测试
     */
    @BindView(R.id.edit_callMOSServer)
    Spinner callMOSServerSpinner;
    /**
     * 主被叫联合测试
     */
    @BindView(R.id.task_uniontest_layout)
    LinearLayout unionTestLayout;
    /**
     * 主被叫联合测试选项
     */
    @BindView(R.id.edit_unionTest)
    Spinner unionSpinner;
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
     * 蓝牙MOS选择
     */
    @BindView(R.id.edit_callMOSBluetooth)
    BasicSpinner mCallMOSBluetoothSpinner;
    /**
     * 双向交替
     */
    @BindView(R.id.spinner_mos_way)
    BasicSpinner mCallMOSAlternateSpinner;
    /**
     * 匹配手机
     */
    @BindView(R.id.spinner_match_phone)
    BasicSpinner mCallMatchPhoneSpinner;
    /**
     * POLQA 算分算法
     */
    @BindView(R.id.edit_callMOS_PolqaCalc)
    BasicSpinner callMOSPOLQACalcSpinner;
    /**
     * 实时算分
     **/
    @BindView(R.id.mos_calc)
    BasicSpinner callMosCalcSpinner;

    private TaskRabModel taskRabModel;
    /**
     * 蓝牙MOS头工厂类
     */
    private BluetoothMOSFactory mFactory;
    /**
     * 蓝牙设备选择
     */
    private String[] mBluetoothDevices;

    @BindView(R.id.rab_time_layout)
    RelativeLayout rab_time_layout;

    @BindView(R.id.rab_time_rel_layout)
    RelativeLayout rab_rule_time_layout;

    @BindView(R.id.btn_ok)
    Button mBtnOk;

    @BindView(R.id.btn_cencle)
    Button mBtnCancle;

    @BindView(R.id.title_txt)
    TextView mTvTitle;

    @BindView(R.id.cb_multi_sample)
    CheckBox mCbMultiSample;

    @BindView(R.id.ll_multi_data)
    LinearLayout mLlMultiDataLayout;

    @BindView(R.id.tv_multi_cycle_data)
    TextView mTvMultiCycleData;

    @BindView(R.id.edit_cycle_interval)
    EditText mEtCycleInterval;

    @BindView(R.id.edit_cycle_times)
    EditText mEtCycleTimes;

    @BindView(R.id.layout_mos_polqa_simple)
    RelativeLayout mRlMosPolqaSimple;

    @BindView(R.id.pointer)
    ImageView mIvBack;

    @BindView(R.id.advanced_arrow_rel)
    RelativeLayout mRlAdvancedArrow;

    private String[] mosType;
    private String[] mosCounted;
    private String[] polqaSample;
    private String[] polqaCalc;
    private String[] ynArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_passivitycall);
        ButterKnife.bind(this);
        this.mFactory = BluetoothMOSFactory.get();
        taskd = TaskListDispose.getInstance();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("taskListId")) {
            taskListId = bundle.getInt("taskListId");
            // 根据标记做并发编辑
            if (RABTAG.equals(super.getRabTag())) {
                for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
                    if (taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())) {
                        model = (TaskPassivityCallModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel()
                                .get(taskListId);
                        break;
                    }
                }
            } else {
                model = (TaskPassivityCallModel) taskd.getTaskListArray().get(taskListId);
            }
            abstModel = model;
            isNew = false;
        }
        initData();
        setListener();
    }


    private void initData() {
        mTvTitle.setText(R.string.act_task_passivitycall);// 设置标题
        mBtnOk.setText(" " + getString(R.string.str_save) + " ");
        mBtnCancle.setText(getString(R.string.str_cancle));

        if (ConfigRoutine.getInstance().isBluetoothSync(getApplicationContext())) {
            unionTestLayout.setVisibility(View.VISIBLE);
        }

        //并发相对时间
        super.setRabTime(rab_time_layout, rab_rule_time_layout);

        //并发专用
        if (model != null) {
            super.rabRelTimeEdt.setText(model.getRabRelTime());
            super.rabAblTimeEdt.setText(model.getRabRuelTime());
        } else {
            super.rabRelTimeEdt.setText("50");
            super.rabAblTimeEdt.setText("12:00");
        }

        ArrayList<WalkStruct.TaskType> taskList = ApplicationModel.getInstance().getTaskList();

        // 是否有Polqa权限，如果没有，公显示PESQ权限
        mosCounted = taskList.contains(WalkStruct.TaskType.POLQA)
                && taskList.contains(WalkStruct.TaskType.MOS)
                ? getArrary(R.array.mos_counted)
                : taskList.contains(WalkStruct.TaskType.POLQA)
                ? new String[]{getArrary(R.array.mos_counted)[1]}
                : new String[]{getArrary(R.array.mos_counted)[0]};

        polqaSample = getArrary(R.array.mos_polqa_sample);
        polqaCalc = getArrary(R.array.mos_polqa_calc);
        ynArray = getArrary(R.array.public_yn);

        buildSpinner(callMOSServerSpinner, ynArray);
        buildSpinner(mCallMOSAlternateSpinner, ynArray);
        buildSpinner(callModeSpinner, ynArray);
        buildSpinner(unionSpinner, ynArray);
        buildSpinner(callMOSPOLQASampleSpinner, polqaSample);
        buildSpinner(callMOSCountdSpinner, mosCounted);

        buildGraySpinner(callMOSPOLQACalcSpinner, polqaCalc, (model == null ? 0 : model.getPolqaCalc()));
        buildGraySpinner(callMosCalcSpinner, ynArray, (model == null ? 1 : model.getMtcTestConfig().isRealtimeCalculation() ? 1 : 0));

        if (model != null) {
            String taskName = model.getTaskName().toString().trim();
            taskNameEditText.setText(taskName.substring(model.getTaskName().indexOf("%") + 1, taskName.length()));
            repeatEditText.setText(String.valueOf(model.getRepeat()));
            callMOSServerSpinner.setSelection(model.getCallMOSServer());
            callModeSpinner.setSelection(model.getCallMode());
            callMOSCountdSpinner.setSelection(adapetByPesqSub(model.getCallMOSCount()));
            callMOSPOLQACalcSpinner.setSelection(model.getPolqaCalc());
            callMOSPOLQASampleSpinner.setSelection(model.getPolqaSample());
            callMosCalcSpinner.setSelection(model.isRealtimeCalculation() ? 1 : 0);
            mCallMOSAlternateSpinner.setSelection(model.isAlternateTest() ? 1 : 0);
            unionSpinner.setSelection(model.isUnitTest() ? 1 : 0);

            mTvMultiCycleData.setText(model.getMultiCycleDataName());
            mEtCycleInterval.setText(model.getCycleInterval() + "");
            mEtCycleTimes.setText((model.getCycleTimes() == 0 ? 1  : model.getCycleTimes())+"");
            mCbMultiSample.setChecked(model.isMultiTest());
        } else {
            repeatEditText.setText("10");
            taskNameEditText.setText("MTC");
            unionSpinner.setSelection(1);
        }
        // 通过权限控制是否显示MOC,使用原来的飞信权限
        if (taskList.contains(WalkStruct.TaskType.MOS)
                || taskList.contains(WalkStruct.TaskType.POLQA)) {
            (initRelativeLayout(R.id.layout_advance_L)).setVisibility(View.VISIBLE);
            (initRelativeLayout(R.id.callMosLayout)).setVisibility(View.VISIBLE);

            if (taskList.contains(WalkStruct.TaskType.POLQA)) {
                mRlMosPolqaSimple.setVisibility(View.VISIBLE);
            }
        } else {
            (initRelativeLayout(R.id.layout_advance_L)).setVisibility(View.GONE);
            callMOSServerSpinner.setSelection(0);
        }

        if (model != null && model.isMultiTest()) {
            mLlMultiDataLayout.setVisibility(View.VISIBLE);
            mRlMosPolqaSimple.setVisibility(View.GONE);
        } else {
            mLlMultiDataLayout.setVisibility(View.GONE);
            mRlMosPolqaSimple.setVisibility(View.VISIBLE);
        }

    }


    private void setListener() {
        mIvBack.setOnClickListener(this);
        mRlAdvancedArrow.setOnClickListener(this);

        mTvMultiCycleData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(TaskSelSampleActivity.SELECT_SAMPLE_DATA, mTvMultiCycleData.getText().toString().trim());
                jumpActivityForResult(TaskSelSampleActivity.class, bundle, 2001);
            }
        });

        mCbMultiSample.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mLlMultiDataLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                mRlMosPolqaSimple.setVisibility(!isChecked ? View.VISIBLE : View.GONE);
            }
        });

        super.rabAblTimeEdt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(TaskMTCCall.this, rabAblTimeEdt.getText().toString());
                dateTimePicKDialog.dateTimePicKDialog(rabAblTimeEdt);
            }
        });

        mCallMOSBluetoothSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(MosMatchActivity.EXTRA_FRAGMENT_TYPE, MosMatchActivity.EXTRA_MACTCH_MOC_MOSBOX);
                    jumpActivity(MosMatchActivity.class, bundle);
                }
            }
        });
        mCallMatchPhoneSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {

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
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == 0) {

                    mCbMultiSample.setChecked(false);

                    View[] views = new View[]{callMOSCountdSpinner, callMosCalcSpinner, mCallMOSBluetoothSpinner
                            , callMOSPOLQASampleSpinner, mCallMOSAlternateSpinner, mCallMatchPhoneSpinner, callMOSPOLQACalcSpinner, mCbMultiSample};
                    multiViewAble(views, false);
                } else {
                    View[] views = new View[]{callMOSCountdSpinner, callMosCalcSpinner, mCallMOSBluetoothSpinner
                            , mCallMOSAlternateSpinner, mCallMatchPhoneSpinner, mCbMultiSample};
                    multiViewAble(views, true);

                    if (adapetByPesqAdd(callMOSCountdSpinner.getSelectedItemPosition()) == 1) {
                        callMOSPOLQASampleSpinner.setEnabled(true);
                        callMOSPOLQACalcSpinner.setEnabled(true);
                    }
                    if (!Deviceinfo.getInstance().isUseRoot() && !Deviceinfo.getInstance().isXiaomi()) {
                        //vivo、小米手机、S8mos测试不接mos头所以不用弹出该提示
                        ToastUtil.showShort(TaskMTCCall.this, R.string.task_alert_callMOSServer);
                    }
                }
                buildGraySpinner(callMosCalcSpinner, ynArray, (model == null ? 1 : model.getMtcTestConfig().isRealtimeCalculation() ? 1 : 0));
                buildGraySpinner(mCallMOSBluetoothSpinner, mBluetoothDevices, 0);
                buildGraySpinner(callMOSCountdSpinner, mosCounted,
                        (model == null ? 0 : adapetByPesqSub(model.getCallMOSCount()))); // 置灰spinner颜色字体特殊处理
                buildGraySpinner(callMOSPOLQASampleSpinner, polqaSample, (model == null ? 0 : model.getPolqaSample()));
                buildGraySpinner(callMOSPOLQACalcSpinner, polqaCalc, (model == null ? 0 : model.getPolqaCalc()));
            }
        });

        callMOSCountdSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (adapetByPesqAdd(arg2) == 0) {
                    callMOSPOLQASampleSpinner.setEnabled(false);
                    callMOSPOLQACalcSpinner.setEnabled(false);
                    mCbMultiSample.setEnabled(false);
                    mCbMultiSample.setChecked(false);
                } else if (callMOSCountdSpinner.isEnabled()) {
                    callMOSPOLQASampleSpinner.setEnabled(true);
                    callMOSPOLQACalcSpinner.setEnabled(true);
                    mCbMultiSample.setEnabled(true);
                }
                buildGraySpinner(callMOSPOLQASampleSpinner, polqaSample, (model == null ? 0 : model.getPolqaSample()));
                buildGraySpinner(callMOSPOLQACalcSpinner, polqaCalc, (model == null ? 0 : model.getPolqaCalc()));
            }
        });

        mBtnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                saveTestTask();
            }
        });

        mBtnCancle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        addEditTextWatcher();
    }


    /**
     * 设置多个 view 的 enable or disable
     *
     * @param views
     * @param enabled
     */
    private void multiViewAble(View[] views, boolean enabled) {
        if (views != null) {
            for (View view : views) {
                if (view != null) {
                    view.setEnabled(enabled);
                }
            }
        }
    }

    private void buildSpinner(Spinner spinner, String[] array) {
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_custom_layout, array);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * 特殊处理spinner颜色字体，重写适配器
     */
    private void buildGraySpinner(BasicSpinner spinner, String[] arrayStr, int position) {
        GraySpinnerAdapter adapter = new GraySpinnerAdapter(TaskMTCCall.this, arrayStr);
        spinner.setAdapter(adapter);
        adapter.setEnabled(spinner.isEnabled());
        spinner.setSelection(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        initAdapetArrayList();
    }

    private void initAdapetArrayList() {
        String noDevice = this.getResources().getString(R.string.task_callMOS_no_device);
        String chooseDevice = this.getResources().getString(R.string.task_callMOS_choose_device);
        mBluetoothDevices = new String[]{noDevice, chooseDevice};
        if (this.mFactory.getCurrMOCDevice() != null)
            mBluetoothDevices[0] = this.mFactory.getCurrMOCDevice().getAddress();

        buildSpinner(mCallMOSBluetoothSpinner, mBluetoothDevices);

        String[] phoneDevices = new String[]{noDevice, chooseDevice};
        if (BluetoothPipeLine.getInstance().getConnectDevice() != null)
            phoneDevices[0] = BluetoothPipeLine.getInstance().getConnectDevice().getAddress();

        buildSpinner(mCallMatchPhoneSpinner, phoneDevices);
    }

    @Override
    public void saveTestTask() {
        String repeat = repeatEditText.getText().toString().trim();
        String taskName = taskNameEditText.getText().toString().trim();
        String cycleTimes = mEtCycleTimes.getText().toString().trim();
        String cycleInterval = mEtCycleInterval.getText().toString().trim();

        // 任务名为空
        if (taskName.length() == 0) {
            ToastUtil.showShort(this, R.string.task_alert_nullName);
            taskNameEditText.setError(getString(R.string.task_alert_nullName));
            return;
        } else if (repeat.equals("0") || repeat.length() == 0) {
            ToastUtil.showShort(this, R.string.task_alert_nullRepeat);
            repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
            return;
        } else if (mCbMultiSample.isChecked() && (cycleTimes.length() == 0 || Integer.parseInt(cycleTimes) < 1)) {
            ToastUtil.showShort(this, getString(R.string.task_alert_cycletime));
            return;
        }

        if (model == null) {
            model = new TaskPassivityCallModel();
            taskd.setCurrentTaskIdAndSequence(model);
        }

        String rabRelTime = super.rabRelTimeEdt.getText().toString().trim();
        String rabAblTime = super.rabAblTimeEdt.getText().toString().trim();

        model.setTaskName(taskName);
        model.setRepeat(Integer.parseInt(repeat.length() == 0 ? "10" : repeat));
        model.setTaskType(WalkStruct.TaskType.PassivityCall.name());
        model.setEnable(1);
        model.setCallMode(callModeSpinner.getSelectedItemPosition());
        model.setCallMOSServer(callMOSServerSpinner.getSelectedItemPosition());
        model.setUnitTest(unionSpinner.getSelectedItemPosition() == 1);
        model.setCallMOSCount(adapetByPesqAdd(callMOSCountdSpinner.getSelectedItemPosition()));
        model.setPolqaSample(callMOSPOLQASampleSpinner.getSelectedItemPosition());
        model.setPolqaCalc(callMOSPOLQACalcSpinner.getSelectedItemPosition());
        model.setTypeProperty(1);
        model.setParallelData(0);
        model.setInterVal(3);
        model.setRealtimeCalculation(callMosCalcSpinner.getSelectedItemPosition() == 0 ? false : true);
        model.setRabRelTime(rabRelTime.length() == 0 ? "50" : rabRelTime);
        model.setRabRuelTime(rabAblTime.length() == 0 ? "12:00" : rabAblTime);
        model.setAlternateTest(mCallMOSAlternateSpinner.getSelectedItemPosition() == 1);
        model.setMultiTest(mCbMultiSample.isChecked());
        model.setMultiCycleDataName(mTvMultiCycleData.getText().toString().trim());
        model.setCycleTimes(Integer.parseInt(cycleTimes.length() == 0 ? "1" : cycleTimes));
        model.setCycleInterval(Integer.parseInt(cycleInterval.length() == 0 ? "0" : cycleInterval));
        List<TaskModel> array = taskd.getTaskListArray();
        if (RABTAG.equals(super.getRabTag())) {// 依标志区分并发与普通业务
            for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
                if (super.getMultiRabName().equals(taskd.getCurrentTaskList().get(i).getTaskID())) {
                    taskRabModel = (TaskRabModel) taskd.getCurrentTaskList().get(i);
                    break;
                }
            }
            if (isNew) {
                taskRabModel.setTaskModelList(taskRabModel.addTaskList(model));
            } else {
                taskRabModel.getTaskModel().remove(taskListId);
                taskRabModel.getTaskModel().add(taskListId, model);
            }
        } else {
            if (isNew) {
                array.add(array.size(), model);
            } else {
                array.remove(taskListId);
                array.add(taskListId, model);
            }
        }
        taskd.setTaskListArray(array);

        ToastUtil.showShort(this, isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess);
        TaskMTCCall.this.finish();
    }

    private int adapetByPesqSub(int sort) {
        ArrayList<WalkStruct.TaskType> taskList = ApplicationModel.getInstance().getTaskList();
        return taskList.contains(WalkStruct.TaskType.MOS)
                && taskList.contains(WalkStruct.TaskType.POLQA) ? sort : 0;
    }

    private int adapetByPesqAdd(int sort) {
        return ApplicationModel.getInstance().getTaskList().contains(WalkStruct.TaskType.MOS) ? sort : sort + 1;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2001 && resultCode == RESULT_OK) {
            String selectSampleData = data.getStringExtra(TaskSelSampleActivity.SELECT_SAMPLE_DATA);
            List<String> sampleNameList = new Gson().fromJson(selectSampleData, new TypeToken<List<String>>() {
            }.getType());
            if (sampleNameList != null && sampleNameList.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (String name : sampleNameList) {
                    sb.append(name + ",");
                }
                mTvMultiCycleData.setText(sb.substring(0, sb.length() - 1));
            } else {
                mTvMultiCycleData.setText("");
            }
        }
    }

    /**
     * 添加EditText输入监听限制
     */
    public void addEditTextWatcher() {

        taskNameEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (taskNameEditText.getText().toString().trim().length() == 0) {
                    taskNameEditText.setError(getString(R.string.task_alert_nullName));
                }

            }
        });
        repeatEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String repeat = repeatEditText.getText().toString().trim();
                if (repeat.equals("0") || repeat.length() == 0) {
                    repeatEditText.setError(getString(R.string.task_alert_nullName));
                }
            }
        });

    }
}
