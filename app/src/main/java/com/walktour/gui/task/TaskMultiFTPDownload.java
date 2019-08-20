/*
 * 文件名: TaskMutilFTPDownload.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-6-3
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.gui.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.setting.SysFTPGroup;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multiftp.download.TaskMultiftpDownloadModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.model.FTPGroupModel;

import java.util.ArrayList;
import java.util.List;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-6-3] 
 */
public class TaskMultiFTPDownload extends BaseTaskActivity{
    private static final String TAG = "TaskMultiFTPDownload";
    private boolean isNew  = true;

    private TaskRabModel taskRabModel;
    
    private TaskListDispose taskd;
    
    private TaskMultiftpDownloadModel multilModel;

    private int taskListId = -1;
    
    public static final String SERVER_GROUP_KEY = "server_group_key";
    
    public static final int SYSFTPGROUP_REQUEST_CODE = 1001;
    
    
    /**
     * 任务名称
     */
    private EditText tasknameET;
    
    /**
     * 重复次数
     */
    private EditText repeatET;
    
    /**
     * 时长
     */
    private EditText durationET;
    
    /**
     * 间隔时间
     */
    private EditText intervalET;
    
    /**
     * 无流量超时
     */
    private EditText nodataET;
    
    /**
     * 等待时长
     */
    private EditText waittimeET;
    
    /**
     * 断开网络
     */
    private BasicSpinner disconnectSP;
    
    /**
     * 测试模式
     */
    private BasicSpinner testmodeSP;
    /**
     * 结束条件 
     */
    private BasicSpinner endConditionSP;
    private Spinner ed_thread_mode;
    private EditText threadNumberEditText;
    private  ArrayList<FTPGroupModel> ftpServers;
    
//    private Spinner dataConnectType;
    private EditText sendBufferText;
    private EditText receiveBufferText;
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param savedInstanceState
     * @see com.walktour.gui.task.BaseTaskActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_multiftp_download);
        taskd=TaskListDispose.getInstance();
        Bundle bundle = getIntent().getExtras(); 
        if(bundle != null && bundle.containsKey("taskListId")){
            taskListId  = bundle.getInt("taskListId");
            if(RABTAG.equals(super.getRabTag())){
                for(int i=0;i<taskd.getCurrentTaskList().size();i++){ 
                        if(taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())){
                            multilModel = (TaskMultiftpDownloadModel)((TaskRabModel)(taskd.getCurrentTaskList().get(i))).getTaskModel().get(taskListId);
                            break;
                        }
                }
            }else{
                multilModel = (TaskMultiftpDownloadModel)taskd.getTaskListArray().get(taskListId);
            }
            abstModel = multilModel;
            isNew=false;
         }
        findView();
        addEditTextWatcher();
    }
    
    
    /**
     * 初始化化所有View对象<BR>
     * [功能详细描述]
     */
    private void findView(){
        (initTextView(R.id.title_txt)).setText(R.string.act_task_multiftp_download);
        (initImageView(R.id.pointer)).setOnClickListener(this);
        ((RelativeLayout)findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);
        (initTextView(R.id.ftpServer_text)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(TaskMultiFTPDownload.this,SysFTPGroup.class);
                    intent.putExtra(SysFTPGroup.TASKTYPE_KEY, TaskType.MultiftpDownload);
                    intent.putParcelableArrayListExtra(SERVER_GROUP_KEY, multilModel == null ? new ArrayList<FTPGroupModel>():ftpServers);
                    startActivityForResult(intent, SYSFTPGROUP_REQUEST_CODE);
            }
        });
        TextView tv_threadNumber =initTextView(R.id.txt_threadNumber);
        tv_threadNumber.setText(getString(R.string.task_threadNumber));
        sendBufferText=initEditText(R.id.edit_sendbuffer);
        receiveBufferText=initEditText(R.id.edit_receivebuffer);
        tasknameET = initEditText(R.id.edit_taskname);
        repeatET = initEditText(R.id.edit_repeat);
        durationET = initEditText(R.id.keeptime_edit);
        intervalET = initEditText(R.id.edit_interVal);
        nodataET = initEditText(R.id.edit_noAnswer);
        waittimeET = initEditText(R.id.waittime_edit);
        disconnectSP = (BasicSpinner) findViewById(R.id.disconnect_sp);
        testmodeSP = (BasicSpinner) findViewById(R.id.testmode_sp);
        endConditionSP = (BasicSpinner) findViewById(R.id.endcondition_sp);
        ed_thread_mode=initSpinner(R.id.spn_thread_mode);
        threadNumberEditText =initEditText(R.id.edit_threadNumber);
//        dataConnectType = (Spinner)findViewById(R.id.edit_data_connect_type);
//        super.setDataConnectTypeSP(dataConnectType);
            
        //断开网络配置
        ArrayAdapter<String> disconnectAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_task_disconnect));
        disconnectAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        disconnectSP.setAdapter(disconnectAdapter);
        disconnectSP.setSelection(1);
        
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_ftpdownload_testmode));
        modeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        testmodeSP.setAdapter(modeAdapter);
        ArrayAdapter<String> threadModeAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.thread_mode_select));
        threadModeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        ed_thread_mode.setAdapter(threadModeAdapter);
        ArrayAdapter<String> endAdapter = new ArrayAdapter<String>(this,
        		R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_mftp_end));
        endAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        endConditionSP.setAdapter(endAdapter);
        
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.btn_cencle).setOnClickListener(this);
        
        if(multilModel != null){
            tasknameET.setText(multilModel.getTaskName());
            repeatET.setText(String.valueOf(multilModel.getRepeat()));
            durationET.setText(String.valueOf(multilModel.getKeepTime()));
            intervalET.setText(String.valueOf(multilModel.getInterVal()));
            nodataET.setText(String.valueOf(multilModel.getNoData()));
            waittimeET.setText(String.valueOf(multilModel.getWaitTime()));
            testmodeSP.setSelection( multilModel.getTestMode() );
            endConditionSP.setSelection( multilModel.getEndCodition() );
            disconnectSP.setSelection(multilModel.getDisConnect());
            ftpServers = multilModel.getFtpServers();
            sendBufferText.setText(multilModel.getMftpDownloadTestConfig().getSendBuffer()+"");
            receiveBufferText.setText(multilModel.getMftpDownloadTestConfig().getReceBuffer()+"");
//            dataConnectType.setSelection(multilModel.getTypeProperty()==4?1:0); //ppp 0 wifi 1
            threadNumberEditText.setText( String.valueOf(multilModel.getThreadCount()));
            ed_thread_mode.setSelection(Integer.parseInt(multilModel.getThreadMode()));
        }else {
            testmodeSP.setSelection(1);
            endConditionSP.setSelection( 0 );
            threadNumberEditText.setText("30");
        }
        
        testmodeSP.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                if(arg2==0){
                    (initTextView(R.id.keeptime_txt)).setText(R.string.task_ftpdownload_timeout);
                }else{
                    (initTextView(R.id.keeptime_txt)).setText(R.string.task_ftpdownload_Duration);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                
            }
        });
        
        if(testmodeSP.getSelectedItemPosition() == 0){
            (initTextView(R.id.keeptime_txt)).setText(R.string.task_ftpdownload_timeout);
        }else{
            (initTextView(R.id.keeptime_txt)).setText(R.string.task_ftpdownload_Duration);
        }
        
        (initTextView(R.id.ftpServer_text)).setText(R.string.task_http_not_url);
        if(ftpServers != null && ftpServers.size() > 0){
            for(FTPGroupModel ftpModel : ftpServers){
                if(ftpModel.getEnable() == 1){
                    (initTextView(R.id.ftpServer_text)).setText(R.string.task_http_selected_url);
                    break;
                }
            }
            
        }
    }
    
    /**
     * 添加EditText输入监听限制<BR>
     * [功能详细描述]
     */
    public void addEditTextWatcher() {
        
        tasknameET.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (tasknameET.getText().toString().trim().length() == 0) { //任务名为空
                    tasknameET.setError(getString(R.string.task_alert_nullName));
                }
            }
        });
        
        durationET.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (durationET.getText().toString().trim().equals("0")|| durationET.getText().toString().trim().length() == 0) {
                    durationET.setError(getString(R.string.task_alert_nullTimeout));
                }
            }
        });
        
        repeatET.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (repeatET.getText().toString().trim().equals("0")
                        || repeatET.getText().toString().trim().length() == 0) {
                    repeatET.setError(getString(R.string.task_alert_nullRepeat));
                    
                }
            }
        });
        
        intervalET.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (intervalET.getText().toString().trim().equals("0")
                        || intervalET.getText().toString().trim().length() == 0) {
                    intervalET.setError(getString(R.string.task_alert_nullInterval));
                }
            }
        });
        
        nodataET.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (nodataET.getText().toString().trim().equals("0")|| nodataET.getText().toString().trim().length() == 0) {
                    nodataET.setError(getString(R.string.task_alert_input)
                            + " " + getString(R.string.task_noAnswer));
                    return;
                }
            }
        });
        
        waittimeET.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (waittimeET.getText().toString().trim().equals("0")
                        || waittimeET.getText().toString().trim().length() == 0) {
                    waittimeET.setError(getString(R.string.task_alert_nullInterval));
                }
            }
        });
        
    }
    

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see com.walktour.gui.task.BaseTaskActivity#saveTestTask()
     */
    @Override
    public void saveTestTask() {
        if (tasknameET.getText().toString().trim().length() == 0) {
            Toast.makeText(this,
                    R.string.task_alert_nullName,
                    Toast.LENGTH_SHORT).show();
            tasknameET.setError(getString(R.string.task_alert_nullName));
            return;
        } else if (repeatET.getText().toString().trim().length() == 0|| repeatET.getText().toString().trim().length() == 0) {
            Toast.makeText(this,
                    R.string.task_alert_nullRepeat,
                    Toast.LENGTH_SHORT).show();
            repeatET.setError(getString(R.string.task_alert_nullRepeat));
            return;
        } else if (durationET.getText().toString().trim().equals("0")
                || durationET.getText().toString().trim().length() == 0) {
            Toast.makeText(this,
                    R.string.task_alert_nullTimeout,
                    Toast.LENGTH_SHORT).show();
            durationET.setError(getString(R.string.task_alert_nullTimeout));
            return;
        } else if (intervalET.getText().toString().trim().equals("0")
                || intervalET.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(),
                    R.string.task_alert_nullInterval,
                    Toast.LENGTH_SHORT).show();
            intervalET.setError(getString(R.string.task_alert_nullInterval));
            return;
        } else if (waittimeET.getText().toString().trim().equals("0")
                || waittimeET.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(),
                    R.string.task_alert_nullInterval,
                    Toast.LENGTH_SHORT).show();
            waittimeET.setError(getString(R.string.task_alert_nullInterval));
            return;
        }
        if (multilModel == null){
            multilModel = new TaskMultiftpDownloadModel();
            taskd.setCurrentTaskIdAndSequence(multilModel);
        }
        
        multilModel.setTaskName(tasknameET.getText().toString().trim());
        multilModel.setTaskType(WalkStruct.TaskType.MultiftpDownload.name());
        multilModel.setEnable(1);
        multilModel.setTaskName(tasknameET.getText().toString());
        multilModel.setRepeat(Integer.valueOf(repeatET.getText().toString()));
        multilModel.setTestMode(testmodeSP.getSelectedItemPosition());
        multilModel.setEndCodition( endConditionSP.getSelectedItemPosition() );
        Log.e(TAG,"上傳時長:"+Integer.valueOf(durationET.getText().toString()));
        multilModel.setKeepTime(Integer.valueOf(durationET.getText().toString()));
        multilModel.setInterVal(Integer.valueOf(intervalET.getText().toString()));
        multilModel.setNoData(Integer.valueOf(nodataET.getText().toString()));
        multilModel.setWaitTime(Integer.valueOf(waittimeET.getText().toString()));
        multilModel.setDisConnect(disconnectSP.getSelectedItemPosition());
        multilModel.setFtpServers(ftpServers);
        multilModel.getMftpDownloadTestConfig().setSendBuffer(sendBufferText.getText().toString()+"");
        multilModel.getMftpDownloadTestConfig().setReceBuffer(receiveBufferText.getText().toString()+"");
        multilModel.getMftpDownloadTestConfig().setThreadCount(Integer.parseInt(threadNumberEditText.getText().toString()));
        multilModel.getMftpDownloadTestConfig().setThreadMode(ed_thread_mode.getSelectedItemPosition()+"");
        boolean selectFTP  = false;
        if(ftpServers != null){
            for (int i = 0; i < ftpServers.size(); i++) {
                if(ftpServers.get(i).getEnable()  == 1){
                    selectFTP = true;
                    break;
                }
            }
        }
        if(!selectFTP){
            Toast.makeText(this, R.string.ftp_server_not_select, Toast.LENGTH_LONG).show();
            return ;
        }
//        if (((Long)dataConnectType.getSelectedItemId()).intValue()==1)  //只有是WIFI的才设置
//            multilModel.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);  //
//        else 
//            multilModel.setTypeProperty(WalkCommonPara.TypeProperty_Net);  //ppp
        List<TaskModel> array = taskd.getTaskListArray();
        if(RABTAG.equals(super.getRabTag())){//依标志区分并发与普通业务
            for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
                if(super.getMultiRabName().equals(taskd.getCurrentTaskList().get(i).getTaskID())){
                    taskRabModel=(TaskRabModel)taskd.getCurrentTaskList().get(i);
                    break;
                }
            }
            if(isNew){
                taskRabModel.setTaskModelList(taskRabModel.addTaskList(multilModel));
            }else{
                taskRabModel.getTaskModel().remove(taskListId);
                taskRabModel.getTaskModel().add(taskListId, multilModel);
            }
        }else{
            if (isNew) {
                array.add(array.size(), multilModel);
            } else {
                array.remove(taskListId);
                array.add(taskListId, multilModel);
            }
            
        }
        taskd.setTaskListArray(array);
        
        Toast.makeText(getApplicationContext(),isNew?R.string.task_alert_newSucess:R.string.task_alert_updateSucess,Toast.LENGTH_SHORT).show();
        TaskMultiFTPDownload.this.finish();
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param requestCode
     * @param resultCode
     * @param data
     * @see com.walktour.framework.ui.BasicActivity#onActivityResult(int, int, android.content.Intent)
     */
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SYSFTPGROUP_REQUEST_CODE:
                ftpServers = data.getParcelableArrayListExtra(SERVER_GROUP_KEY);
                if(multilModel == null){
                    multilModel = new TaskMultiftpDownloadModel();
                    taskd.setCurrentTaskIdAndSequence(multilModel);
                }
                (initTextView(R.id.ftpServer_text)).setText(R.string.task_http_not_url);
                if(ftpServers != null && ftpServers.size() > 0){
                    for(FTPGroupModel ftpModel : ftpServers){
                        if(ftpModel.getEnable() == 1){
                            (initTextView(R.id.ftpServer_text)).setText(R.string.task_http_selected_url);
                            break;
                        }
                    }
                    
                }
                break;
            
            default:
                break;
        }
    }
    
}
