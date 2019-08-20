/**
 *
 */
package com.walktour.gui.task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.opensignal.TaskOpenSignalModel;

import java.util.List;

/**
 * OpenSignal业务
 */
public class TaskOpenSignalActivity extends BaseTaskActivity {
    /**
     * 任务名称
     */
    private EditText taskName;
    /**
     * 重复次数
     */
    private EditText repeat;
    /**
     * 下行线程数
     */
    private EditText downThreads;

    /**
     * 上行线程数
     */
    private EditText upThreads;

    /**
     * 间隔时长
     */
    private EditText interval;

    /**
     * 国家名
     */
    private EditText country;
    /**
     * 城市名
     */
    private EditText city;
    /**
     * 服务器赞助商
     */
    private EditText sponsor;

    /**
     * 连接方式
     */
    private Spinner disConnect;

    TaskListDispose taskd = null;
    TaskOpenSignalModel model = null;
    private int taskListId = -1;
    private boolean isNew = true;

    private Button save;//保存
    private Button cancel;//取消
    private Spinner dataConnectType;// 数据连接选择：PPP or WIFI
    private TaskRabModel taskRabModel;
    private LinearLayout wifiTestLayout;
    private RelativeLayout userNameLayout;
    private Button wifiSSIDET;
    private EditText wifiUserET;
    private EditText wifiPasswordET;
    private LayoutInflater inflater;
    private Spinner disConnectEditText;
    private Context context =TaskOpenSignalActivity.this;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(this);
        setContentView(R.layout.task_opensignal);

        // 设置标题名字
        (initTextView(R.id.title_txt)).setText(getResources().getString(R.string.act_task_opensignal));

        taskd = TaskListDispose.getInstance();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("taskListId")) {  //加入普通业务与并发业务处理
            taskListId = bundle.getInt("taskListId");
            if (RABTAG.equals(super.getRabTag())) {
                for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
                    if (taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())) {
                        model = (TaskOpenSignalModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel().get(taskListId);
                        break;
                    }
                }
            } else {
                model = (TaskOpenSignalModel) taskd.getTaskListArray().get(taskListId);
            }
            abstModel = model;
            isNew = false;
        }

        showView();
    }


    /**
     * 界面控件展现设置
     */
    private void showView() {
        taskName = initEditText(R.id.edit_taskname);
        taskName.setText(null==model?getResources().getString(R.string.act_task_opensignal):model.getTaskName());
        repeat = initEditText(R.id.edit_repeat);
        downThreads = initEditText(R.id.download_threads);
        downThreads.setText(null==model?"4":model.getDownThreadNum());
        upThreads = initEditText(R.id.upload_threads);
        upThreads.setText(null==model?"1":model.getUpThreadNum());
        country=initEditText(R.id.et_country);
        country.setText(null==model?"":model.getCountry());
        city=initEditText(R.id.et_city);
        city.setText(null==model?"":model.getCity());
        sponsor=initEditText(R.id.et_sponsor);
        sponsor.setText(null==model?"":model.getSponsor());
        interval = initEditText(R.id.edit_interval);
        disConnect = (Spinner) findViewById(R.id.edit_disConnect);
        ((RelativeLayout)findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);
        (initImageView(R.id.pointer)).setOnClickListener(this);
        save = initButton(R.id.btn_ok);
        cancel = initButton(R.id.btn_cencle);
        disConnectEditText = (Spinner) findViewById(R.id.edit_disConnect);
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
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

        //如果是新建，则初始化一个模板
        if (model == null) {
            model = new TaskOpenSignalModel();
            taskd.setCurrentTaskIdAndSequence(model);
        }
        if (!isNew) {
            taskName.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%") + 1, model.getTaskName().toString().trim().length()));
        } else {
            taskName.setText(model.getTaskName());
        }
        //断开网络配置
        ArrayAdapter<String> disconnectAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_task_disconnect));
        disconnectAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        disConnect.setAdapter(disconnectAdapter);

        repeat.setText(String.valueOf(model.getRepeat()));
        interval.setText(String.valueOf(model.getInterVal()));
        disConnect.setSelection(model.getDisConnect());
        // 数据连接类型
        dataConnectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0||position == 2) {// PPP测试
                    wifiTestLayout.setVisibility(View.GONE);
                } else {// wifi测试
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
    /**
     * 保存方法
     */
    @Override
    public void saveTestTask() {
        if (StringUtil.isEmpty(taskName.getText().toString())) {
            Toast.makeText(TaskOpenSignalActivity.this, R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
            return;
        } else if (StringUtil.isEmpty(repeat.getText().toString()) || "0".equals(repeat.getText().toString().trim())) {
            Toast.makeText(TaskOpenSignalActivity.this, R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
            return;
        } else if (StringUtil.isEmpty(downThreads.getText().toString())) {
            Toast.makeText(TaskOpenSignalActivity.this, R.string.task_alert_nullOrzeroThread, Toast.LENGTH_SHORT).show();
            return;
        } else if (StringUtil.isEmpty(upThreads.getText().toString())) {
            Toast.makeText(TaskOpenSignalActivity.this, R.string.task_alert_nullOrzeroThread, Toast.LENGTH_SHORT).show();
            return;
        } else if (StringUtil.isLessThanZeroInteger(interval.getText().toString().trim())) {
            Toast.makeText(TaskOpenSignalActivity.this, R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
            return;
        } else {
        }
        model.setTaskName(taskName.getText().toString().trim());
        taskName.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%") + 1, model.getTaskName().toString().trim().length()));
        model.setEnable(1);
        model.setRepeat(Integer.parseInt(repeat.getText().toString().trim().length() == 0 ? "1" : repeat.getText().toString().trim()));
        model.setInterVal(Integer.parseInt(interval.getText().toString().trim()));
        model.setDisConnect(((Long) disConnect.getSelectedItemId()).intValue());

        model.setDownThreadNum(downThreads.getText()+"");
        model.setUpThreadNum(upThreads.getText()+"");
        model.setCountry(country.getText()+"");
        model.setCity(city.getText()+"");
        model.setSponsor(sponsor.getText()+"");
        List<TaskModel> array = taskd.getTaskListArray();
        if (RABTAG.equals(super.getRabTag())) {//依标志区分并发与普通业务
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
        Toast.makeText(getApplicationContext(), isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess, Toast.LENGTH_SHORT).show();
        this.finish();
    }

}
