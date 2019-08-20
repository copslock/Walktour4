package com.walktour.gui.task;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.control.bean.Verify;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.view.DateTimePickDialogUtil;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.ping.TaskPingModel;

import java.util.List;

public class TaskPing extends BaseTaskActivity {
    TaskListDispose taskd = null;
    TaskPingModel model = null;
    private int taskListId = -1;
    private boolean isNew = true;
    private EditText taskNameEditText;
    private EditText repeatEditText;
    private EditText timeOutEditText;
    private EditText ipEditText;
    private EditText sizeEditText;
    private EditText interValEditText;
    private EditText editTTL;
    private Spinner disConnectEditText;
    private Spinner mSpinnerUEState;
    private CheckBox mCbATPing;
    private CheckBox mCbCMDPing;
    private TaskRabModel taskRabModel;
    private Spinner dataConnectType;// 数据连接选择：PPP or WIFI
    private CheckBox unlimited_chk;
    private RelativeLayout rab_time_layout;
    private RelativeLayout rab_rule_time_layout;
    private LinearLayout wifiTestLayout;
    private RelativeLayout userNameLayout;
    private Button wifiSSIDET;
    private EditText wifiUserET;
    private EditText wifiPasswordET;
    private Context context = TaskPing.this;
    private LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(this);
        taskd = TaskListDispose.getInstance();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("taskListId")) {
            taskListId = bundle.getInt("taskListId");
            if (RABTAG.equals(super.getRabTag())) {
                for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
                    if (taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())) {
                        model = (TaskPingModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel()
                                .get(taskListId);
                        break;
                    }
                }
            } else {
                model = (TaskPingModel) taskd.getTaskListArray().get(taskListId);
            }
            abstModel = model;
            isNew = false;
        }
        findView();
        addEditTextWatcher();
    }

    private void findView() {
        // 绑定Layout里面的ListView
        setContentView(R.layout.task_ping);
        (initTextView(R.id.title_txt)).setText(R.string.act_task_ping);// 设置标题
        (initImageView(R.id.pointer)).setOnClickListener(this);
        ((RelativeLayout) findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);
        TextView tv_taskName = initTextView(R.id.txt_taskname);
        TextView tv_repeat = initTextView(R.id.txt_repeat);
        TextView tv_timeOut = initTextView(R.id.txt_timeOut);
        TextView tv_ip = initTextView(R.id.txt_ip);
        TextView tv_size = initTextView(R.id.txt_size);
        TextView tv_interVal = initTextView(R.id.txt_interVal);
        TextView tv_disConnect = initTextView(R.id.txt_disConnect);
        TextView tvUEState = initTextView(R.id.tv_ue_state);
        Button btn_ok = (Button) findViewById(R.id.btn_ok);
        Button btn_cencle = (Button) findViewById(R.id.btn_cencle);
        tv_taskName.setText(getString(R.string.task_taskName));
        tv_repeat.setText(getString(R.string.task_repeat));
        tv_timeOut.setText(getString(R.string.task_ping_timeout));
        tv_ip.setText(getString(R.string.task_ping_address));
        tv_size.setText(getString(R.string.task_size));
        tv_interVal.setText(getString(R.string.task_interVal));
        tv_disConnect.setText(getString(R.string.task_disConnect));
        tvUEState.setText(getString(R.string.task_ue_state));
        btn_ok.setText(" " + getString(R.string.str_save) + " ");
        btn_cencle.setText(getString(R.string.str_cancle));
        taskNameEditText = initEditText(R.id.edit_taskname);
        repeatEditText = initEditText(R.id.edit_ping_repeat);
        timeOutEditText = initEditText(R.id.edit_timeOut);
        ipEditText = initEditText(R.id.edit_ip);
        sizeEditText = initEditText(R.id.edit_size);
        interValEditText = initEditText(R.id.edit_interVal);
        editTTL = initEditText(R.id.edit_ttl);
        //终端状态
        mSpinnerUEState = (Spinner) findViewById(R.id.spinner_ue_state);
        ArrayAdapter<String> ueSpinnerAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_custom_layout,
                getResources().getStringArray(R.array.array_task_ue_state));
        ueSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSpinnerUEState.setAdapter(ueSpinnerAdapter);
        //AT+Ping
        mCbATPing = (CheckBox) findViewById(R.id.cb_at_ping);
        mCbCMDPing = (CheckBox) findViewById(R.id.cb_cmd_ping);
        mCbCMDPing.setChecked(Deviceinfo.getInstance().isIscmdping());
        mCbATPing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            mCbCMDPing.setChecked(false);
                        }
            }
        });

        RelativeLayout layoutAtPing=initRelativeLayout(R.id.layout_at_ping);
        if(Deviceinfo.getInstance().isS8()){
            layoutAtPing.setVisibility(View.VISIBLE);
        }else{
            layoutAtPing.setVisibility(View.GONE);
        }
        mCbCMDPing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mCbATPing.setChecked(false);
                }
            }
        });
        disConnectEditText = (Spinner) findViewById(R.id.edit_disConnect);
        unlimited_chk = (CheckBox) findViewById(R.id.unlimited_chk);
        unlimited_chk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
                    repeatEditText.setEnabled(false);
                } else {
                    repeatEditText.setEnabled(true);
                }
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
                getResources().getStringArray(R.array.array_task_disconnect));
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        disConnectEditText.setAdapter(adapter);
        // wifi support
        dataConnectType = (Spinner) findViewById(R.id.edit_data_connect_type);
        setDataConnectTypeSP(dataConnectType);
        ArrayAdapter<String> dataConnectTypeAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, ApplicationModel.getInstance().getConnectType());
        dataConnectTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        dataConnectType.setAdapter(dataConnectTypeAdapter);
        // 并发相对时间
        rab_time_layout = (RelativeLayout) findViewById(R.id.rab_time_layout);
        rab_rule_time_layout = (RelativeLayout) findViewById(R.id.rab_time_rel_layout);
        super.setRabTime(rab_time_layout, rab_rule_time_layout);
        // 并发专用
        if (model != null) {
            super.rabRelTimeEdt.setText(model.getRabRelTime());
            super.rabAblTimeEdt.setText(model.getRabRuelTime());
        } else {
            super.rabRelTimeEdt.setText("50");
            super.rabAblTimeEdt.setText("12:00");
        }
        super.rabAblTimeEdt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(TaskPing.this,
                        rabAblTimeEdt.getText().toString());
                dateTimePicKDialog.dateTimePicKDialog(rabAblTimeEdt);
            }
        });
        // 添加Wifi信息,如果所有业务都有wifi测试,那么这段代码可以放入父类中
        wifiTestLayout = initLinearLayout(R.id.task_wifitest_layout);
        userNameLayout = initRelativeLayout(R.id.usernamelayout);
        wifiSSIDET = initButton(R.id.wifitestssid);
        wifiUserET = initEditText(R.id.wifitestuser);
        wifiPasswordET = initEditText(R.id.wifitestpassword);
        wifiSSIDET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (null != wifiManager) {
                    List<ScanResult> list = wifiManager.getScanResults();
                    if (null != list && list.size() > 0) {
                        ApSelectAdapter adapter = new ApSelectAdapter(list);
                        openDialog(adapter, list, -1, wifiSSIDET);
                    } else {
                        ToastUtil.showToastShort(context, getString(R.string.sys_wifi_aplist) + "");
                    }
                }
            }
        });
        if (model != null) {
            taskNameEditText.setText(model.getTaskName() .trim()
                    .substring(model.getTaskName().indexOf("%") + 1, model.getTaskName() .trim().length()));
            repeatEditText.setText(String.valueOf(model.getRepeat()));
            timeOutEditText.setText(String.valueOf(model.getTimeOut()));
            ipEditText.setText(String.valueOf(model.getIp()));
            sizeEditText.setText(String.valueOf(model.getSize()));
            interValEditText.setText(String.valueOf(model.getInterVal()));
            disConnectEditText.setSelection(model.getDisConnect());
            dataConnectType.setSelection(model.getTypeProperty() == 4 ? 1 : 0); // ppp
            // 0
            // wifi
            // 1
            unlimited_chk.setChecked(model.isInfinite());
            editTTL.setText(String.valueOf(model.getTtl()));
            mCbATPing.setChecked(model.isATPing());
            mCbCMDPing.setChecked(model.getPingTestConfig().isCMDPing());
            mSpinnerUEState.setSelection(TextUtils.isEmpty(model.getUEState())
                    || model.getUEState().equals((getResources().getStringArray(R.array.array_task_ue_state))[0]) ? 0 : 1);
            repeatEditText.setEnabled(!model.isInfinite());
        } else {
            taskNameEditText.setText("Ping");
            sizeEditText.setText("64");
            repeatEditText.setText("10");
            timeOutEditText.setText("5");
            interValEditText.setText("3");
            disConnectEditText.setSelection(1);
            editTTL.setText("64");
        }
        btn_ok.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                saveTestTask();
            }
        });
        btn_cencle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        // 数据连接类型
        dataConnectType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0||position == 2) {// PPP测试
                    wifiTestLayout.setVisibility(View.GONE);
                }else {// wifi测试
                    wifiTestLayout.setVisibility(View.VISIBLE);
                    if (null != model) {
                        String[] params = model.getNetworkConnectionSetting().getWifiParam();
                        String apName = params[0];
                        wifiSSIDET.setText(apName);
                        if (apName.equals("ChinaNet") || apName.equals("ChinaUnicom") || apName.contains("CMCC-WEB")
                                || apName.contains("CMCC")) {
                            userNameLayout.setVisibility(View.VISIBLE);
                        }
                        wifiUserET.setText(params[1]);
                        wifiPasswordET.setText(params[2]);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (null != model) {
            if (model.getTypeProperty() == WalkCommonPara.TypeProperty_Wlan
                    || model.getNetworkConnectionSetting().isConnectionUseWifi()) {
                dataConnectType.setSelection(1);
                wifiTestLayout.setVisibility(View.VISIBLE);
                String[] params = model.getNetworkConnectionSetting().getWifiParam();
                String apName = params[0];
                wifiSSIDET.setText(apName);
                if (apName.equals("ChinaNet") || apName.equals("ChinaUnicom") || apName.contains("CMCC-WEB")
                        || apName.contains("CMCC")) {
                    userNameLayout.setVisibility(View.VISIBLE);
                }
                wifiUserET.setText(params[1]);
                wifiPasswordET.setText(params[2]);
            } else if(model.getTypeProperty() == WalkCommonPara.TypeProperty_Ppp){
                dataConnectType.setSelection(2);
                wifiTestLayout.setVisibility(View.GONE);
            }else {
                dataConnectType.setSelection(0);
                wifiTestLayout.setVisibility(View.GONE);
            }
        }
    }

    private void setDataConnectTypeSP(Spinner dataConnectTypeSP) {
        if (dataConnectTypeSP != null) {
            if (showInfoList.contains(WalkStruct.ShowInfoType.WLAN)) {
                ArrayAdapter<String> dataConnectTypeAdapter = new ArrayAdapter<String>(this,
                        R.layout.simple_spinner_custom_layout,
                        ApplicationModel.getInstance().getConnectType());
                dataConnectTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                dataConnectTypeSP.setAdapter(dataConnectTypeAdapter);
            } else {
                findViewById(R.id.task_wifi_app_choice).setVisibility(View.GONE);
                findViewById(R.id.task_wifi_test_choice).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // findView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private final class ApItem {
        public TextView wifiAP;
        public TextView wifiStrength;
    }

    private class ApSelectAdapter extends BaseAdapter {
        private List<ScanResult> listSR;

        public ApSelectAdapter(List<ScanResult> listSR) {
            super();
            this.listSR = listSR;
        }

        @Override
        public int getCount() {
            return listSR == null ? 0 : listSR.size();
        }

        @Override
        public Object getItem(int position) {
            return listSR.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ScanResult model = listSR.get(position);
            ApItem itemView = null;
            if (convertView == null) {
                itemView = new ApItem();
                convertView = inflater.inflate(R.layout.task_ap_select_item, parent, false);
                itemView.wifiAP = (TextView) convertView.findViewById(R.id.wlanapname);
                itemView.wifiStrength = (TextView) convertView.findViewById(R.id.wlanapsinglestrength);
                convertView.setTag(itemView);
            } else {
                itemView = (ApItem) convertView.getTag();
            }
            itemView.wifiAP.setText(model.SSID);
            itemView.wifiStrength.setText(model.level + "dbm");
            return convertView;
        }
    }

    private void openDialog(final BaseAdapter adapter, final List<ScanResult> listSR, final int checkedItem,
                            final Button button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.sys_wifi_selectap) + "");
        builder.setSingleChoiceItems(adapter, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ScanResult sr = listSR.get(which);
                button.setText(sr.SSID);
                if (sr.SSID.equals("ChinaNet") || sr.SSID.equals("ChinaUnicom") || sr.SSID.contains("CMCC-WEB")
                        || sr.SSID.contains("CMCC")) {
                    userNameLayout.setVisibility(View.VISIBLE);
                } else {
                    userNameLayout.setVisibility(View.GONE);
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void saveTestTask() {
        if (taskNameEditText.getText().toString().trim().length() == 0) { // 任务名为空
            ToastUtil.showToastShort(context, R.string.task_alert_nullName);
            taskNameEditText.setError(getString(R.string.task_alert_nullName));
            return;
        } else if (repeatEditText.getText().toString().trim().equals("0")
                || repeatEditText.getText().toString().trim().length() == 0) {
            ToastUtil.showToastShort(context, R.string.task_alert_nullRepeat);
            repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
            return;
        } else if (timeOutEditText.getText().toString().trim().length() == 0
                || !StringUtil.isRange(Integer.parseInt(timeOutEditText.getText().toString()), 1, 5000)) {
            if (timeOutEditText.getText().toString().trim().length() == 0) {
                ToastUtil.showToastShort(context, R.string.task_alert_nullTimeout);
                timeOutEditText.setError(getString(R.string.task_alert_nullTimeout));
            } else {
                ToastUtil.showToastShort(context, getString(R.string.total_ping) + "," + String.format(getString(R.string.share_project_devices_release_relation_9), 1, 5000));
                timeOutEditText.setError(getString(R.string.total_ping) + "," + String.format(getString(R.string.share_project_devices_release_relation_9), 1, 5000));
            }
            return;
        } else if (interValEditText.getText().toString().trim().equals("0")
                || interValEditText.getText().toString().trim().length() == 0) {
            ToastUtil.showToastShort(context, R.string.task_alert_nullInterval);
            interValEditText.setError(getString(R.string.task_alert_nullInterval));
            return;
        } else if (!Verify.isIpOrUrl(ipEditText.getText().toString().trim())) { // 任务名为空
            ToastUtil.showToastShort(context, R.string.sys_ping_alert_nullIP);
            ipEditText.setError(getString(R.string.sys_ping_alert_nullIP));
            return;
        } else if (sizeEditText.getText().toString().trim().length() == 0
                || !StringUtil.isRange(Integer.parseInt(sizeEditText.getText().toString()), 1, 1500)) {
            if (sizeEditText.getText().toString().trim().length() == 0) {
                ToastUtil.showToastShort(context, R.string.task_alert_nullSize);
                sizeEditText.setError(getString(R.string.task_alert_nullSize));
            } else {
                ToastUtil.showToastShort(context, getString(R.string.task_size) + "," + String.format(getString(R.string.share_project_devices_release_relation_9), 1, 1500));
                sizeEditText.setError(getString(R.string.task_size) + "," + String.format(getString(R.string.share_project_devices_release_relation_9), 1, 1500));
            }
            return;
        } else if (editTTL.getText().toString().trim().length() == 0
                || !StringUtil.isRange(Integer.parseInt(editTTL.getText().toString()), 1, 255)) {
            if (editTTL.getText().toString().trim().length() == 0) {
                ToastUtil.showToastShort(context, R.string.task_alert_nullTtl);
                editTTL.setError(getString(R.string.task_alert_nullTtl));
            } else {
                ToastUtil.showToastShort(context, getString(R.string.task_ping_ttl) + "," + String.format(getString(R.string.share_project_devices_release_relation_9), 1, 255));
                editTTL.setError(getString(R.string.task_ping_ttl) + "," + String.format(getString(R.string.share_project_devices_release_relation_9), 1, 255));
            }
            return;
        }
        if (model == null) {
            model = new TaskPingModel();
            taskd.setCurrentTaskIdAndSequence(model);
        }
        // 依据标记区分用户名的编辑
        model.setTaskName(taskNameEditText.getText().toString().trim());
        model.setTaskType(WalkStruct.TaskType.Ping.name());
        model.setEnable(1);
        model.setRepeat(Integer.parseInt(repeatEditText.getText().toString().trim().length() == 0 ? "10"
                : repeatEditText.getText().toString().trim()));
        model.setTimeOut(Integer.parseInt(timeOutEditText.getText().toString().trim().length() == 0 ? "2"
                : timeOutEditText.getText().toString().trim()));
        model.setIp(ipEditText.getText().toString().trim().length() == 0 ? "" : ipEditText.getText().toString().trim());
        model.setSize(Integer.parseInt(sizeEditText.getText().toString().trim().length() == 0 ? "256"
                : sizeEditText.getText().toString().trim()));
        model.setInterVal(Integer.parseInt(interValEditText.getText().toString().trim().length() == 0 ? "3"
                : interValEditText.getText().toString().trim()));
        model.setTtl(Integer.parseInt(
                editTTL.getText().toString().trim().length() == 0 ? "7" : editTTL.getText().toString().trim()));
        model.setUEState((String) mSpinnerUEState.getSelectedItem());
        model.setIsATPing(mCbATPing.isChecked());
        model.getPingTestConfig().setCMDPing(mCbCMDPing.isChecked());
        model.setDisConnect(disConnectEditText.getSelectedItemPosition());
        model.setInfinite(unlimited_chk.isChecked());
        model.setRabRelTime(super.rabRelTimeEdt.getText().toString().trim().length() == 0 ? "50"
                : super.rabRelTimeEdt.getText().toString().trim());
        model.setRabRuelTime(super.rabAblTimeEdt.getText().toString().trim().length() == 0 ? "12:00"
                : super.rabAblTimeEdt.getText().toString().trim());
        if (((Long) dataConnectType.getSelectedItemId()).intValue() == 1) { // WIFI
            model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
            model.getNetworkConnectionSetting().updateWifiParam(wifiSSIDET.getText().toString() + "",
                    wifiUserET.getText().toString() + "", wifiPasswordET.getText().toString() + "");
        } else if(((Long) dataConnectType.getSelectedItemId()).intValue() == 2){//NBPPP
            model.setTypeProperty(WalkCommonPara.TypeProperty_Ppp);
            model.getNetworkConnectionSetting().setConnectionUseWifi(false);
        } else{//PPP
            model.setTypeProperty(WalkCommonPara.TypeProperty_Net);
            model.getNetworkConnectionSetting().setConnectionUseWifi(false);
        }
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
        ToastUtil.showToastShort(context, isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess);
        TaskPing.this.finish();
    }

    /**
     * 添加EditText输入监听限制<BR>
     * [功能详细描述]
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
        timeOutEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (timeOutEditText.getText().toString().trim().equals("0")) {
                    timeOutEditText.setError(getString(R.string.task_alert_nullTimeout));
                }
            }
        });
        editTTL.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!Verify.isInteger(ipEditText.getText().toString().trim())) {
                        editTTL.setError(getString(R.string.alert_inputagain));
                    }
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
        interValEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (interValEditText.getText().toString().trim().equals("0")
                        || interValEditText.getText().toString().trim().length() == 0) {
                    interValEditText.setError(getString(R.string.task_alert_nullInterval));
                }
            }
        });
        ipEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!Verify.isIpOrUrl(ipEditText.getText().toString().trim())) {
                        ipEditText.setError(getString(R.string.sys_ftp_alert_nullIP));
                    }
                }
            }
        });
    }
}
