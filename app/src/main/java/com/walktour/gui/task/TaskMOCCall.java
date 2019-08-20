package com.walktour.gui.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import android.widget.Toast;

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
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.service.bluetoothmos.BluetoothMOSFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 语音主叫业务测试
 *
 * @author jianchao.wang
 */
public class TaskMOCCall extends BaseTaskActivity {

    TaskListDispose taskd = null;
    TaskInitiativeCallModel model = null;
    private int taskListId = -1;
    private boolean isNew = true;

    /**
     * 任务名称
     */
    @BindView(R.id.edit_taskname)
    EditText taskNameEditText;
    /**
     * 重复次数
     */
    @BindView(R.id.edit_repeat)
    EditText repeatEditText;
    /**
     * 呼叫号码
     */
    @BindView(R.id.edit_callNumber)
    CustomAutoCompleteTextView callNumberEditText;
    /**
     * 连接时间
     */
    @BindView(R.id.edit_connectTime)
    EditText connectTimeEditText;
    /**
     * 持续时间
     */
    @BindView(R.id.edit_keepTime)
    EditText keepTimeEditText;
    /**
     * 每次间隔
     */
    @BindView(R.id.edit_interVal)
    EditText interValEditText;
    /**
     * 视频通话
     */
    @BindView(R.id.spinner_callMode)
    BasicSpinner callModeSpinner;
    /**
     * 是否长呼
     */
    @BindView(R.id.spinner_callLength)
    BasicSpinner callLength;
    /**
     * 语音评测
     */
    @BindView(R.id.edit_callMOSServer)
    Spinner callMOSServerSpinner;
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
     * 双向交替
     */
    @BindView(R.id.spinner_mos_way)
    BasicSpinner mCallMOSAlternateSpinner;
    /**
     * 匹配手机
     */
    @BindView(R.id.spinner_match_phone)
    BasicSpinner mCallMatchPhoneSpinner;

    @BindView(R.id.task_uniontest_layout)
    LinearLayout unionTestLayout;
    /**
     * 主叫联合
     */
    @BindView(R.id.edit_unionTest)
    Spinner unionSpinner;
    /**
     * MOS测试类型
     */
    @BindView(R.id.edit_callTestType)
    BasicSpinner callMOSTestTypeSpinner;
    /**
     * 实时算分
     **/
    @BindView(R.id.mos_calc)
    BasicSpinner callMosCalcSpinner;

    private TaskRabModel taskRabModel;
    /**
     * 保存历史记录
     */
    private SaveHistoryShare historyShare;

    private ArrayList<String> mOriginalValues = new ArrayList<String>();
    /**
     * 是否来自工单界面
     */
    private boolean isFromWorkOrder = false;
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
        setContentView(R.layout.task_initiativecall);
        ButterKnife.bind(this);

        taskd = TaskListDispose.getInstance();
        this.mFactory = BluetoothMOSFactory.get();
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            this.isFromWorkOrder = bundle.getBoolean("fromWorkOrder");
        }
        if (bundle != null && bundle.containsKey("taskListId")) {
            taskListId = bundle.getInt("taskListId");
            if (RABTAG.equals(super.getRabTag())) {
                for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
                    if (taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())) {
                        model = (TaskInitiativeCallModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel()
                                .get(taskListId);
                        break;
                    }
                }
            } else {
                model = (TaskInitiativeCallModel) taskd.getTaskListArray().get(taskListId);
            }
            abstModel = model;
            isNew = false;
        }

        initData();
        setListener();
    }


    private void initData() {
        // 是否有手机－固定端权限，如果没有，只显示数组中的第一项
        ArrayList<WalkStruct.TaskType> taskList = ApplicationModel.getInstance().getTaskList();
        mosType = taskList.contains(WalkStruct.TaskType.Phone2Fixed)
                ? getArrary(R.array.mos_testtype)
                : new String[]{getArrary(R.array.mos_testtype)[0]};

        // 是否有Polqa权限，如果没有，仅显示PESQ权限
        mosCounted = taskList.contains(WalkStruct.TaskType.POLQA)
                && taskList.contains(WalkStruct.TaskType.MOS)
                ? getArrary(R.array.mos_counted)
                : taskList.contains(WalkStruct.TaskType.POLQA)
                ? new String[]{getArrary(R.array.mos_counted)[1]}
                : new String[]{getArrary(R.array.mos_counted)[0]};

        polqaSample = getArrary(R.array.mos_polqa_sample);
        polqaCalc = getArrary(R.array.mos_polqa_calc);
        ynArray = getArrary(R.array.public_yn);

        // 设置标题
        mTvTitle.setText(R.string.act_task_initiativecall);
        mBtnOk.setText(" " + getString(R.string.str_save) + " ");
        mBtnCancle.setText(getString(R.string.str_cancle));

        buildSpinner(callMOSServerSpinner, ynArray);
        buildSpinner(callModeSpinner, ynArray);
        buildSpinner(callLength, ynArray);
        buildSpinner(mCallMOSAlternateSpinner, ynArray);
        buildSpinner(unionSpinner, ynArray);

        buildGraySpinner(callMOSCountdSpinner, mosCounted, (model == null ? 0 : adapetByPesqSub(model.getCallMOSCount())));
        buildGraySpinner(callMosCalcSpinner, ynArray, (model == null ? 1 : model.getMocTestConfig().isRealtimeCalculation() ? 1 : 0));
        buildGraySpinner(callMOSTestTypeSpinner, mosType, (model == null ? 0 : model.getCallMOSTestType()));
        buildGraySpinner(callMOSPOLQASampleSpinner, polqaSample, (model == null ? 0 : model.getPolqaSample()));
        buildGraySpinner(callMOSPOLQACalcSpinner, polqaCalc, (model == null ? 0 : model.getPolqaCalc()));

        if (model != null) {
            String taskName = model.getTaskName();
            taskNameEditText.setText(taskName.toString().trim().substring(taskName.indexOf("%") + 1,
                    taskName.toString().trim().length()));
            repeatEditText.setText(String.valueOf(model.getRepeat()));
            callNumberEditText.setText(model.getCallNumber());
            connectTimeEditText.setText(String.valueOf(model.getConnectTime()));
            keepTimeEditText.setText(String.valueOf(model.getKeepTime()));
            interValEditText.setText(String.valueOf(model.getInterVal()));
            callModeSpinner.setSelection(model.getCallMode());
            callLength.setSelection(model.getCallLength());
            callMOSServerSpinner.setSelection(model.getMosTest());
            callMOSCountdSpinner.setSelection(adapetByPesqSub(model.getCallMOSCount()));
            callMOSPOLQASampleSpinner.setSelection(model.getPolqaSample());
            callMOSPOLQACalcSpinner.setSelection(model.getPolqaCalc());
            callMosCalcSpinner.setSelection(model.getMocTestConfig().isRealtimeCalculation() ? 1 : 0);
            callMOSTestTypeSpinner.setSelection(model.getCallMOSTestType());
            unionSpinner.setSelection(model.isUnitTest() ? 1 : 0);
            mCallMOSAlternateSpinner.setSelection(model.isAlternateTest() ? 1 : 0);

            mTvMultiCycleData.setText(model.getMultiCycleDataName());
            mEtCycleInterval.setText(model.getCycleInterval() + "");
            mEtCycleTimes.setText((model.getCycleTimes() == 0 ? 1 : model.getCycleTimes()) + "");
            mCbMultiSample.setChecked(model.isMultiTest());
        } else {
            taskNameEditText.setText("MOC");
            repeatEditText.setText("10");
            connectTimeEditText.setText("30");
            keepTimeEditText.setText("60");
            interValEditText.setText("15");
            // if(ConfigRoutine.getInstance().isBluetoothSync(getApplicationContext())){
            // 不管是否开启蓝牙,该主被叫联合值都为真,开始测试时,如果不是蓝牙或小背包测试,处理该值不起效
            unionSpinner.setSelection(1);
            // }
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

        // 通过权限控制是否显示MOC,使用原来的飞信权限
        if (taskList.contains(WalkStruct.TaskType.MOS)
                || taskList.contains(WalkStruct.TaskType.POLQA)) {
            (initRelativeLayout(R.id.layout_mos_calc)).setVisibility(View.VISIBLE);
            (initRelativeLayout(R.id.layout_mos_bluetooth)).setVisibility(View.VISIBLE);
            (initRelativeLayout(R.id.callMosLayout)).setVisibility(View.VISIBLE);
            (initRelativeLayout(R.id.layout_mos_type)).setVisibility(View.VISIBLE);
            (initRelativeLayout(R.id.layout_mos_count)).setVisibility(View.VISIBLE);

            if (taskList.contains(WalkStruct.TaskType.POLQA)) {
                mRlMosPolqaSimple.setVisibility(View.VISIBLE);
                (initRelativeLayout(R.id.layout_mos_polqa_calc)).setVisibility(View.VISIBLE);
            }
        } else {
            callMOSServerSpinner.setSelection(0);
        }

        if (model != null && model.isMultiTest()) {
            mLlMultiDataLayout.setVisibility(View.VISIBLE);
            mRlMosPolqaSimple.setVisibility(View.GONE);
        } else {
            mLlMultiDataLayout.setVisibility(View.GONE);
            mRlMosPolqaSimple.setVisibility(View.VISIBLE);
        }

        historyShare = new SaveHistoryShare(getApplicationContext());
        historyShare.getHistoryDataFromSP(this.getPackageName(), "PhoneNumber", mOriginalValues);
        historyShare.initAutoComplete(callNumberEditText);

    }

    private void setListener() {
        mIvBack.setOnClickListener(this);
        mRlAdvancedArrow.setOnClickListener(this);

        mBtnOk.setOnClickListener(this);
        mBtnCancle.setOnClickListener(this);

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

        callNumberEditText.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == arg0.getCount() - 1) {
                    historyShare.clearData();
                    callNumberEditText.setText("");
                }
            }
        });
        if (ConfigRoutine.getInstance().isBluetoothSync(getApplicationContext())) {
            unionTestLayout.setVisibility(View.VISIBLE);
        }

        this.mCallMOSBluetoothSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(MosMatchActivity.EXTRA_FRAGMENT_TYPE, MosMatchActivity.EXTRA_MACTCH_MOC_MOSBOX);
                    jumpActivity(MosMatchActivity.class, bundle);
                }
            }
        });

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
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == 0) {

                    mCbMultiSample.setChecked(false);

                    View[] views = new View[]{callMOSCountdSpinner, callMOSTestTypeSpinner, callMosCalcSpinner, mCallMOSBluetoothSpinner
                            , callMOSPOLQASampleSpinner, mCallMOSAlternateSpinner, mCallMatchPhoneSpinner, callMOSPOLQACalcSpinner, mCbMultiSample};
                    multiViewAble(views, false);
                } else {

                    View[] views = new View[]{callMOSCountdSpinner, callMosCalcSpinner, mCallMOSBluetoothSpinner, callMOSTestTypeSpinner
                            , mCallMOSAlternateSpinner, mCallMatchPhoneSpinner, mCbMultiSample};
                    multiViewAble(views, true);

                    if (adapetByPesqAdd(callMOSCountdSpinner.getSelectedItemPosition()) == 1) {
                        callMOSPOLQASampleSpinner.setEnabled(true);
                        callMOSPOLQACalcSpinner.setEnabled(true);
                    }
                    if (!Deviceinfo.getInstance().isUseRoot() && !Deviceinfo.getInstance().isXiaomi()) {
                        //vivo、小米手机、S8mos测试不接mos头所以不用弹出该提示
                        Toast.makeText(getApplicationContext(), R.string.task_alert_callMOSServer, Toast.LENGTH_SHORT).show();
                    }
                }
                buildGraySpinner(mCallMOSBluetoothSpinner, mBluetoothDevices, 0);
                buildGraySpinner(callMOSCountdSpinner, mosCounted,
                        (model == null ? 0 : adapetByPesqSub(model.getCallMOSCount()))); // 置灰spinner颜色字体特殊处理
                buildGraySpinner(callMosCalcSpinner, ynArray, (model == null ? 1 : model.getMocTestConfig().isRealtimeCalculation() ? 1 : 0));
                buildGraySpinner(callMOSTestTypeSpinner, mosType, (model == null ? 0 : model.getCallMOSTestType()));
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

        super.rabAblTimeEdt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        TaskMOCCall.this, rabAblTimeEdt.getText().toString());
                dateTimePicKDialog.dateTimePicKDialog(rabAblTimeEdt);
            }
        });

        addEditTextWatcher();
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
        spinner.setAdapter(new GraySpinnerAdapter(TaskMOCCall.this, arrayStr));
        ((GraySpinnerAdapter) spinner.getAdapter()).setEnabled(spinner.isEnabled());
        spinner.setSelection(position);
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

    @Override
    public void saveTestTask() {
        if (isFromWorkOrder) {// 来自工单不允许修改,只可查看
            ToastUtil.showShort(this, "工单任务只可以查看,不允许修改.");
            this.finish();
            return;
        }

        String taskName = taskNameEditText.getText().toString().trim();
        String repeat = repeatEditText.getText().toString().trim();
        String connectTime = connectTimeEditText.getText().toString().trim();
        String keepTime = keepTimeEditText.getText().toString().trim();
        String interVal = interValEditText.getText().toString().trim();
        String callNumber = callNumberEditText.getText().toString().trim();
        String cycleTimes = mEtCycleTimes.getText().toString().trim();
        String cycleInterval = mEtCycleInterval.getText().toString().trim();

        if (taskName.length() == 0) { // 任务名为空
            taskNameEditText.setError(getString(R.string.task_alert_nullName));
            ToastUtil.showShort(this, R.string.task_alert_nullName);
            return;
        } else if (repeat.equals("0") || repeat.length() == 0) {
            repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
            ToastUtil.showShort(this, R.string.task_alert_nullRepeat);
            return;
        } else if (connectTime.equals("0") || connectTime.length() == 0) {
            connectTimeEditText.setError(getString(R.string.task_alert_input) + " " + getString(R.string.task_connectTime));
            ToastUtil.showShort(this, getString(R.string.task_alert_input) + " " + getString(R.string.task_connectTime));
            return;
        } else if (keepTime.equals("0") || keepTime.length() == 0) {
            keepTimeEditText.setError(getString(R.string.task_alert_input) + " " + getString(R.string.task_keepTime));
            ToastUtil.showShort(this, getString(R.string.task_alert_input) + " " + getString(R.string.task_keepTime));
            return;
        } else if (interVal.length() == 0) {
            interValEditText.setError(getString(R.string.task_alert_nullInterval));
            ToastUtil.showShort(this, R.string.task_alert_nullInterval);
            return;
        } else if (callNumber.length() == 0) { // 拨打号码为空
            ToastUtil.showShort(this, R.string.task_alert_nullcallNumber);
            return;
        } else if (mCbMultiSample.isChecked() && (cycleTimes.length() == 0 || Integer.parseInt(cycleTimes) < 1)) {
            ToastUtil.showShort(this, getString(R.string.task_alert_cycletime));
            return;
        }

        if (model == null) {
            model = new TaskInitiativeCallModel();
            taskd.setCurrentTaskIdAndSequence(model);
        }

        model.setTaskName(taskName);
        model.setTaskType(WalkStruct.TaskType.InitiativeCall.name());
        model.setEnable(1);
        model.setRepeat(Integer.parseInt(repeat.length() == 0 ? "10" : repeat));
        model.setCallNumber(callNumber);
        model.setConnectTime(Integer.parseInt(connectTime.length() == 0 ? "30" : connectTime));
        model.setKeepTime(Integer.parseInt(keepTime.length() == 0 ? "60" : keepTime));
        model.setInterVal(Integer.parseInt(interVal.length() == 0 ? "15" : interVal));
        model.setCallMode(callModeSpinner.getSelectedItemPosition());
        model.setCallLength(callLength.getSelectedItemPosition());
        model.setMosTest(callMOSServerSpinner.getSelectedItemPosition());
        model.setCallMOSCount(adapetByPesqAdd(callMOSCountdSpinner.getSelectedItemPosition()));
        model.getMocTestConfig().setRealtimeCalculation(callMosCalcSpinner.getSelectedItemPosition() == 0 ? false : true);
        model.setCallMOSTestType(callMOSTestTypeSpinner.getSelectedItemPosition());
        model.setPolqaSample(callMOSPOLQASampleSpinner.getSelectedItemPosition());
        model.setPolqaCalc(callMOSPOLQACalcSpinner.getSelectedItemPosition());
        model.setUnitTest(unionSpinner.getSelectedItemPosition() == 1);
        model.setRabRelTime(super.rabRelTimeEdt.getText().toString().trim().length() == 0 ? "50" : super.rabRelTimeEdt.getText().toString().trim());
        model.setRabRuelTime(super.rabAblTimeEdt.getText().toString().trim().length() == 0 ? "12:00" : super.rabAblTimeEdt.getText().toString().trim());
        model.setAlternateTest(mCallMOSAlternateSpinner.getSelectedItemPosition() == 1);
        model.setMultiTest(mCbMultiSample.isChecked());
        model.setMultiCycleDataName(mTvMultiCycleData.getText().toString().trim());
        model.setCycleTimes(Integer.parseInt(cycleTimes.length() == 0 ? "1" : cycleTimes));
        model.setCycleInterval(Integer.parseInt(cycleInterval.length() == 0 ? "0" : cycleInterval));
        List<TaskModel> array = taskd.getTaskListArray();
        if (RABTAG.equals(super.getRabTag())) {// 依标志区分并发与普通业务
            for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
                if ((super.getMultiRabName()).equals(taskd.getCurrentTaskList().get(i).getTaskID())) {
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
        historyShare.saveHistory(callNumberEditText);
        taskd.setTaskListArray(array);

        ToastUtil.showShort(this, isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess);
        TaskMOCCall.this.finish();
    }

    private int adapetByPesqSub(int sort) {
        return ApplicationModel.getInstance().getTaskList().contains(WalkStruct.TaskType.MOS)
                && ApplicationModel.getInstance().getTaskList().contains(WalkStruct.TaskType.POLQA) ? sort : 0;
    }

    private int adapetByPesqAdd(int sort) {
        return ApplicationModel.getInstance().getTaskList().contains(WalkStruct.TaskType.MOS) ? sort : sort + 1;
    }

    /**
     * 添加EditText输入监听限制
     */
    public void addEditTextWatcher() {
        taskNameEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (taskNameEditText.getText().toString().trim().length() == 0) { // 任务名为空
                    taskNameEditText.setError(getString(R.string.task_alert_nullName));
                }

            }
        });
        repeatEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (repeatEditText.getText().toString().trim().equals("0")
                        || repeatEditText.getText().toString().trim().length() == 0) {
                    repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
                }
            }
        });

        connectTimeEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (connectTimeEditText.getText().toString().trim().equals("0")
                        || connectTimeEditText.getText().toString().trim().length() == 0) {
                    connectTimeEditText.setError(getString(R.string.task_alert_input) + " " + getString(R.string.task_connectTime));
                }

            }
        });

        keepTimeEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (keepTimeEditText.getText().toString().trim().equals("0")
                        || keepTimeEditText.getText().toString().trim().length() == 0) {
                    keepTimeEditText.setError(getString(R.string.task_alert_input) + " " + getString(R.string.task_keepTime));
                }
            }
        });

        interValEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (interValEditText.getText().toString().trim().length() == 0) {
                    interValEditText.setError(getString(R.string.task_alert_nullInterval));
                }
            }
        });

        callNumberEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (callNumberEditText.getText().toString().trim().length() == 0) { // 拨打号码为空
                    callNumberEditText.setError(getString(R.string.task_alert_nullcallNumber));
                }
            }
        });

    }

    private void initAdapetArrayList() {
        String noDevice = this.getResources().getString(R.string.task_callMOS_no_device);
        String chooseDevice = this.getResources().getString(R.string.task_callMOS_choose_device);
        mBluetoothDevices = new String[]{noDevice, chooseDevice};
        if (this.mFactory.getCurrMOCDevice() != null) {
            mBluetoothDevices[0] = this.mFactory.getCurrMOCDevice().getAddress();
        }
        buildSpinner(mCallMOSBluetoothSpinner, mBluetoothDevices);

        String[] phoneDevices = new String[]{noDevice, chooseDevice};
        if (BluetoothPipeLine.getInstance().getConnectDevice() != null) {
            phoneDevices[0] = BluetoothPipeLine.getInstance().getConnectDevice().getAddress();
        }
        buildSpinner(mCallMatchPhoneSpinner, phoneDevices);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initAdapetArrayList();

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
}
