/**
 *
 */
package com.walktour.gui.task;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.gui.R;
import com.walktour.gui.setting.SysURL;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.base.URLInfo;
import com.walktour.gui.task.parsedata.model.task.multihttp.download.TaskMultiHttpDownModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.model.UrlModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * MultiHttpDownload业务
 */
public class TaskMultiHttpDownActivity extends BaseTaskActivity {
    TaskListDispose taskd = null;
    TaskMultiHttpDownModel model = null;
    private int taskListId = -1;
    private boolean isNew = true;
    private EditText et_taskName;
    private EditText et_repeat;
    private EditText et_timeOut;
    private EditText et_thrNum;
    private EditText et_noAnswer;
    private EditText et_url;
    private EditText et_interVal;
    private Spinner sp_ap;
    /**
     * 结束条件
     */
    private BasicSpinner endConditionSP;

    private Spinner et_disConnect;
    private TaskRabModel taskRabModel;
    private Spinner dataConnectType;
    private LinearLayout wifiTestLayout;
    private RelativeLayout userNameLayout;
    private Button wifiSSIDET;
    private EditText wifiUserET;
    private EditText wifiPasswordET;
    private Context context = this;
    private LayoutInflater inflater;
    public ArrayList<UrlModel> urlList=new ArrayList<>(); // 存储url地址
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
                        model = (TaskMultiHttpDownModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel()
                                .get(taskListId);
                        break;
                    }
                }
            } else {
                model = (TaskMultiHttpDownModel) taskd.getTaskListArray().get(taskListId);
            }
            abstModel = model;
            isNew = false;
        }
        showView();
    }

    private void showView() {
        setContentView(R.layout.task_multi_http_download);
        (initTextView(R.id.title_txt)).setText(R.string.act_task_multihttpDownload);// 设置标题
        (initImageView(R.id.pointer)).setOnClickListener(this);
        (initRelativeLayout(R.id.advanced_arrow_rel)).setOnClickListener(this);

        TextView tv_taskName = initTextView(R.id.txt_taskname);
        TextView tv_repeat = initTextView(R.id.txt_repeat);
        final TextView tv_timeOut = initTextView(R.id.txt_timeOut);
        TextView tv_noAnswer = initTextView(R.id.txt_noAnswer);
        TextView tv_url = initTextView(R.id.txt_url);
        TextView tv_interVal = initTextView(R.id.txt_interVal);
        TextView tv_showweb = initTextView(R.id.txt_showWeb);
        TextView tv_disConnect = initTextView(R.id.txt_disConnect);
        endConditionSP = (BasicSpinner) findViewById(R.id.endcondition_sp);
        Button btn_ok = initButton(R.id.btn_ok);
        Button btn_cencle = initButton(R.id.btn_cencle);
        tv_taskName.setText(getString(R.string.task_taskName));
        tv_repeat.setText(getString(R.string.task_repeat));
        tv_timeOut.setText(getString(R.string.task_ftpdownload_timeout));
        tv_noAnswer.setText(getString(R.string.task_noAnswer));
        tv_url.setText(getString(R.string.task_http_url));
        tv_interVal.setText(getString(R.string.task_interVal));
        tv_disConnect.setText(getString(R.string.task_disConnect));
        tv_showweb.setText(getString(R.string.task_webshow));
        btn_ok.setText(" " + getString(R.string.str_save) + " ");
        btn_cencle.setText(getString(R.string.str_cancle));

        et_taskName = initEditText(R.id.edit_taskname);
        et_repeat = initEditText(R.id.edit_repeat);
        et_timeOut = initEditText(R.id.edit_timeOut);
        et_thrNum = initEditText(R.id.edit_thrNum);
        et_noAnswer = initEditText(R.id.edit_noAnswer);
        et_url = initEditText(R.id.edit_url);
        et_interVal = initEditText(R.id.edit_interVal);
        et_disConnect = initSpinner(R.id.edit_disConnect);
        // 添加Net接入点和Wifi接入点
        TextView tv_ap = initTextView(R.id.txt_ap);
        tv_ap.setText(getString(R.string.task_accepoint));
        sp_ap = initSpinner(R.id.spiner_ap);
        ArrayAdapter<String> adpAP = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
                getResources().getStringArray(R.array.task_ap));
        adpAP.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_ap.setAdapter(adpAP);

        ArrayAdapter<String> endAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_mftp_end));
        endAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        endConditionSP.setAdapter(endAdapter);

        // wifi support
        dataConnectType = (Spinner) findViewById(R.id.edit_data_connect_type);
        setDataConnectTypeSP(dataConnectType);
        ArrayAdapter<String> dataConnectTypeAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, ApplicationModel.getInstance().getConnectType());
        dataConnectTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        dataConnectType.setAdapter(dataConnectTypeAdapter);

        // 断开网络配置
        ArrayAdapter<String> disconnect = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
                getResources().getStringArray(R.array.array_task_disconnect));
        disconnect.setDropDownViewResource(R.layout.spinner_dropdown_item);
        et_disConnect.setAdapter(disconnect);
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

        if (!isNew) {
            if (model.getUrlList().size() != 0) {
                et_url.setText(getString(R.string.task_http_selected_url));
                et_url.setTextColor(getResources().getColor(R.color.app_main_text_color));
            } else {
                et_url.setText(getString(R.string.task_http_not_url));
                et_url.setTextColor(getResources().getColor(R.color.red));
            }
        } else {
            et_url.setText(getString(R.string.task_http_not_url));
            et_url.setTextColor(getResources().getColor(R.color.red));
        }

        if (model != null) {
            et_taskName.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%") + 1,
                    model.getTaskName().toString().trim().length()));
            et_repeat.setText(String.valueOf(model.getRepeat()));
            et_timeOut.setText(String.valueOf(model.getDownloadTimeout()));
            et_thrNum.setText(String.valueOf(model.getThreadCount()));
            et_interVal.setText(String.valueOf(model.getInterVal()));
            et_disConnect.setSelection(model.getDisConnect());
            dataConnectType.setSelection(model.getTypeProperty() == 4 ? 1 : 0);
//            et_url.setText(model.getUrlList().get(0).getUrl() + "");
            et_noAnswer.setText(model.getNoDataTimeOut()+"");
            endConditionSP.setSelection( model.getEndCodition() );
            for(URLInfo ui:model.getUrlList()){
                UrlModel m=new UrlModel();
                m.setName(ui.getUrl()+"");
                m.setEnable(ui.isCheck()?"True":"Flase");
                urlList.add(m);
            }

        } else {
            et_taskName.setText(R.string.act_task_multihttpDownload);
            et_repeat.setText("10");
            et_timeOut.setText("300");
            et_thrNum.setText("1");
            et_noAnswer.setText("30");
//            et_url.setText("http://www.sinaimg.cn/qc/photo_auto/photo/36/39/5243639/5243639_950.jpg||http://101.201.30.203:9013/res/0.png||http://bigpc.didiwl.com/pc/vs2013_sqb.zip");
            et_interVal.setText("15");
            sp_ap.setSelection(0);
            et_disConnect.setSelection(1);
            urlList.clear();
            endConditionSP.setSelection( 0 );
        }

        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTestTask();
            }
        });

        btn_cencle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        et_url.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskMultiHttpDownActivity.this, SysURL.class); // 跳转到url列表
                intent.putExtra("taskname", et_taskName.getText().toString().trim());
                intent.putExtra("urlModel", urlList);
                startActivityForResult(intent, 8);
                overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
            }
        });
    }

    /**
     * 选择返回的数据
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 8) {
            urlList = (ArrayList<UrlModel>) data.getExtras().get("backUrlModelList");
            if (urlList.size() == 0) {
                et_url.setText(getString(R.string.task_http_not_url));
                et_url.setTextColor(getResources().getColor(R.color.red));
            } else {
                et_url.setText(getString(R.string.task_http_selected_url));
                et_url.setTextColor(getResources().getColor(R.color.app_main_text_color));
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
        if (et_taskName.getText().toString().trim().length() == 0) { // 任务名为空
            ToastUtil.showToastShort(this.getApplicationContext(),
                    R.string.task_alert_nullName);
            return;
        } else if (et_repeat.getText().toString().trim().equals("0")
                || et_repeat.getText().toString().trim().length() == 0) {
            ToastUtil.showToastShort(getApplicationContext(), R.string.task_alert_nullRepeat);
            return;
        } else if (et_timeOut.getText().toString().trim().equals("0")
                || et_timeOut.getText().toString().trim().length() == 0) {
            ToastUtil.showToastShort(getApplicationContext(), R.string.task_alert_nullTimeout);
            return;
        } else if (et_thrNum.getText().toString().trim().length() == 0
                || Integer.parseInt(et_thrNum.getText().toString().trim()) < 1
                || Integer.parseInt(et_thrNum.getText().toString().trim()) > 30) {
            ToastUtil.showToastShort(getApplicationContext(), getString(R.string.task_threadNumber) + "," + String.format(getString(R.string.alert_inputt_interregional), 1, 30));
            return;
        } else if (et_interVal.getText().toString().trim().equals("0")
                || et_interVal.getText().toString().trim().length() == 0) {
            ToastUtil.showToastShort(getApplicationContext(), R.string.task_alert_nullOrzeroThread);
            return;
        } else if (et_url.getText().toString().trim().length() == 0) { // URL为空
            ToastUtil.showToastShort(this.getApplicationContext(),
                    R.string.task_alert_nullUrl);
            return;
        } else if (et_noAnswer.getText().toString().trim().length() == 0 ||
                !StringUtil.isRange(Integer.parseInt(et_noAnswer.getText().toString()), 5, 120)) {
            ToastUtil.showToastShort(context, getString(R.string.task_noAnswer) + "," + String.format(getString(R.string.alert_inputt_interregional), 5, 120));
            et_noAnswer.setError(getString(R.string.task_noAnswer) + "," + String.format(getString(R.string.alert_inputt_interregional), 5, 120));
            return;
        }else if (urlList != null ? urlList.size() == 0 : false) {
            ToastUtil.showToastShort(context, R.string.task_http_ref_url);
            return;
        } else if (isNew && urlList == null) {
            ToastUtil.showToastShort(context, R.string.task_http_ref_url);
            return;
        }

        if (model == null) {
            model = new TaskMultiHttpDownModel();
            taskd.setCurrentTaskIdAndSequence(model);
        }
        if(urlList.size()>0){//有勾选的数据
            model.getUrlList().clear();
            for(UrlModel um:urlList){
                URLInfo ui=new URLInfo();
                ui.setUrl(um.getName());
                ui.setCheck(um.getEnable().equalsIgnoreCase("true")?true:false);
                model.getUrlList().add(ui);
            }
        }
        model.setTaskName(et_taskName.getText().toString().trim());
        model.setEnable(1);
        model.setRepeat(Integer.parseInt(
                et_repeat.getText().toString().trim().length() == 0 ? "15" : et_repeat.getText().toString().trim()));
        model.setDownloadTimeout(Integer.parseInt(
                et_timeOut.getText().toString().trim().length() == 0 ? "300" : et_timeOut.getText().toString().trim()));
        model.setNoDataTimeOut(Integer.parseInt(et_noAnswer.getText() + ""));
        model.setThreadCount(Integer.parseInt(
                et_thrNum.getText().toString().trim().length() == 0 ? "1" : et_thrNum.getText().toString().trim()));
        model.setInterVal(Integer.parseInt(et_interVal.getText().toString().trim().length() == 0 ? "15"
                : et_interVal.getText().toString().trim()));
        model.setTypeProperty(2);
        model.setEndCodition( endConditionSP.getSelectedItemPosition() );
//        URLInfo url = new URLInfo();
//        url.setUrl(et_url.getText() + "");
//        model.getUrlList().clear();
//        model.getUrlList().add(url);
        model.setDisConnect(et_disConnect.getSelectedItemPosition());
        if (((Long) dataConnectType.getSelectedItemId()).intValue() == 1) { // 只有是WIFI的才设置
            model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan); //
        } else {
            model.setTypeProperty(WalkCommonPara.TypeProperty_Net); // ppp
        }
        if (((Long) dataConnectType.getSelectedItemId()).intValue() == 1) {
            model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
        } else {
            model.getNetworkConnectionSetting().setConnectionUseWifi(false);
            model.setTypeProperty(WalkCommonPara.TypeProperty_Net); // ppp
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

        } else {// 普通业务保存入口
            if (isNew) {
                array.add(array.size(), model);
            } else {
                array.remove(taskListId);
                array.add(taskListId, model);
            }
        }
        taskd.setTaskListArray(array);

        ToastUtil.showToastShort(getApplicationContext(),
                isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess);
        this.finish();
    }
}
