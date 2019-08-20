package com.walktour.gui.task;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.WalkStruct;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.framework.view.DateTimePickDialogUtil;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.sms.sendreceive.TaskSmsSendReceiveModel;

import java.util.List;

/**
 * 短信自发自收测试的配置页面
 * */
public class TaskSmsSendReceive extends BaseTaskActivity {
    TaskListDispose taskd = null;
    
    TaskSmsSendReceiveModel model = null;
    
    private int taskListId = -1;
    
    private boolean isNew = true;
    
    private boolean isCDMA = false;
    
    private EditText taskNameEditText;
    
    private EditText repeatEditText;
    
    private EditText sendTimeOutEditText;
    
    private EditText receiveTimeOutEditText;
    
    private EditText serverCenterAddressEditText;
    
    private EditText destinationEditText;
    
    private EditText contentEditText;
    
    private EditText interValEditText;

	private TaskRabModel taskRabModel;

	private RelativeLayout rab_time_layout;

	private RelativeLayout rab_rule_time_layout;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //检查网络类型，CDMA则屏蔽短信中心号码
        int phoneType = MyPhoneState.getInstance()
                .getPhoneType(getApplicationContext());
        if (phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
            isCDMA = true;
        }
        
        taskd = TaskListDispose.getInstance();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("taskListId")) {
        	taskListId = bundle.getInt("taskListId");
        	//根据标记做并发编辑
        	if(RABTAG.equals(super.getRabTag())){
        		for(int i=0;i<taskd.getCurrentTaskList().size();i++){ 
        			if(taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())){
        				model =	(TaskSmsSendReceiveModel)((TaskRabModel)(taskd.getCurrentTaskList().get(i))).getTaskModel().get(taskListId);
        				break;
        			}
        		}
        	}else{
        		model = (TaskSmsSendReceiveModel)taskd.getTaskListArray().get(taskListId);
        	}
        	abstModel = model;
        	isNew=false;
        }
        findView();
        addEditTextWatcher();
    }
    
    private void findView() {
        
        //绑定Layout里面的ListView
//        LayoutInflater factory = LayoutInflater.from(this);
//        final View textEntryView = factory.inflate(R.layout.task_smssendreceive,null);
        setContentView(R.layout.task_smssendreceive);
        (initTextView(R.id.title_txt)).setText(R.string.act_task_smssendreceive);//设置标题
        (initImageView(R.id.pointer)).setOnClickListener(this);
        ((RelativeLayout)findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);
        
        TextView tv_taskName = initTextView(R.id.txt_taskname);
        TextView tv_repeat = initTextView(R.id.txt_repeat);
        TextView tv_sendTimeOut = initTextView(R.id.txt_sendTimeOut);
        TextView tv_receiveTimeOut = initTextView(R.id.txt_receiveTimeOut);
        TextView tv_serverCenterAddress = initTextView(R.id.txt_serverCenterAddress);
        TextView tv_destination = initTextView(R.id.txt_destination);
        TextView tv_content = initTextView(R.id.txt_content);
        TextView tv_interVal = initTextView(R.id.txt_interVal);
        Button btn_ok = initButton(R.id.btn_ok);
        Button btn_cencle = initButton(R.id.btn_cencle);
        
        tv_taskName.setText(getString(R.string.task_taskName));
        tv_repeat.setText(getString(R.string.task_repeat));
        tv_sendTimeOut.setText(getString(R.string.task_sendTimeOut));
        tv_receiveTimeOut.setText(getString(R.string.task_receiveTimeOut));
        tv_serverCenterAddress.setText(getString(R.string.task_serverCenterAddress));
        tv_destination.setText(getString(R.string.task_destination));
        tv_content.setText(getString(R.string.task_content));
        tv_interVal.setText(getString(R.string.task_interVal));
        btn_ok.setText(" " + getString(R.string.str_save));
        btn_cencle.setText(getString(R.string.str_cancle));
        
        taskNameEditText = initEditText(R.id.edit_taskname);
        repeatEditText = initEditText(R.id.edit_repeat);
        sendTimeOutEditText = initEditText(R.id.edit_sendTimeOut);
        receiveTimeOutEditText = initEditText(R.id.edit_receiveTimeOut);
        serverCenterAddressEditText = initEditText(R.id.edit_serverCenterAddress);
        destinationEditText = initEditText(R.id.edit_destination);
        contentEditText = initEditText(R.id.edit_content);
        interValEditText = initEditText(R.id.edit_interVal);
        
        ((RelativeLayout)findViewById(R.id.serverCenter_layout)).setVisibility(View.GONE);//isCDMA ? View.GONE : View.VISIBLE);
        
      //并发相对时间
	    rab_time_layout = (RelativeLayout)findViewById(R.id.rab_time_layout);
	    rab_rule_time_layout = (RelativeLayout)findViewById(R.id.rab_time_rel_layout);
	    super.setRabTime(rab_time_layout,rab_rule_time_layout);
	    
	    //并发专用
        if(model!=null){
        	super.rabRelTimeEdt.setText(model.getRabRelTime());
        	super.rabAblTimeEdt.setText(model.getRabRuelTime());
        }else{
        	super.rabRelTimeEdt.setText("50");
        	super.rabAblTimeEdt.setText("12:00");
        }
        super.rabAblTimeEdt.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
        				TaskSmsSendReceive.this, rabAblTimeEdt.getText().toString());
        		dateTimePicKDialog.dateTimePicKDialog(rabAblTimeEdt);
        	}
        });
        
        
        if (model != null) {
        	taskNameEditText.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%")+1, model.getTaskName().toString().trim().length()));
            repeatEditText.setText(String.valueOf(model.getRepeat()));
            sendTimeOutEditText.setText(String.valueOf(model.getSendTimeOut()));
            receiveTimeOutEditText.setText(String.valueOf(model.getReceiveTimeOut()));
            serverCenterAddressEditText.setText(String.valueOf(model.getServerNumber()));
            destinationEditText.setText(String.valueOf(model.getDesNumber()));
            contentEditText.setText(String.valueOf(model.getContent()));
            interValEditText.setText(String.valueOf(model.getInterVal()));
        } else {
        	taskNameEditText.setText("SMS  Send_Recevice");
        	contentEditText.setText("This is a SMS");
            repeatEditText.setText("10");
            sendTimeOutEditText.setText("30");
            receiveTimeOutEditText.setText("30");
            interValEditText.setText("15");
        }
        btn_ok.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                saveTestTask();
            }
        });
        
        btn_cencle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TaskSmsSendReceive.this.finish();
            }
        });
//        textEntryView.setVerticalScrollBarEnabled(true);
//        setContentView(textEntryView);
    }
    
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see com.walktour.gui.task.BaseTaskActivity#saveTestTask()
     */
    @Override
    public void saveTestTask() {
        //验证任务名为空
        if (taskNameEditText.getText().toString().trim().length() == 0) {
            Toast.makeText(com.walktour.gui.task.TaskSmsSendReceive.this.getApplicationContext(),
                    R.string.task_alert_nullName,
                    Toast.LENGTH_SHORT)
                    .show();
            taskNameEditText.setError(getString(R.string.task_alert_nullName));
            return;
        }else if( repeatEditText.getText().toString().trim().equals("0") 
				||  repeatEditText.getText().toString().trim().length()==0){
			Toast.makeText( getApplicationContext(),
					R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
			return;
		}
		else if( sendTimeOutEditText.getText().toString().trim().equals("0") 
				||  sendTimeOutEditText.getText().toString().trim().length()==0 ){
			Toast.makeText(getApplicationContext(),
					R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			sendTimeOutEditText.setError(getString(R.string.task_alert_nullTimeout));
			return;
		}else if( receiveTimeOutEditText.getText().toString().trim().equals("0") 
				||  receiveTimeOutEditText.getText().toString().trim().length()==0 ){
			Toast.makeText(getApplicationContext(),
					R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			receiveTimeOutEditText.setError(getString(R.string.task_alert_nullTimeout));
			return;
		}else if( interValEditText.getText().toString().trim().equals("0")
				|| interValEditText.getText().toString().trim().length()==0 ){
			Toast.makeText(getApplicationContext(),
					R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			interValEditText.setError(getString(R.string.task_alert_nullInterval));
			return;
		}

        //验证短信内容为空
        if (contentEditText.getText().toString().trim().length() == 0) {
            Toast.makeText(com.walktour.gui.task.TaskSmsSendReceive.this.getApplicationContext(),
                    R.string.task_alert_nullcontent,
                    Toast.LENGTH_SHORT)
                    .show();
            contentEditText.setError(getString(R.string.task_alert_nullcontent));
            return;
        }
        //验证接收号码不正确
        if (destinationEditText.getText().toString().trim().length() == 0) {
            Toast.makeText(com.walktour.gui.task.TaskSmsSendReceive.this.getApplicationContext(),
                    R.string.task_alert_nullDesNumber,
                    Toast.LENGTH_SHORT)
                    .show();
            destinationEditText.setError(getString(R.string.task_alert_nullDesNumber));
            return;
        }
        
        //验证短信中心为空
        /*if (serverCenterAddressEditText.getText().toString().trim().length() == 0) {
            if (!isCDMA) {
                Toast.makeText(com.walktour.gui.task.TaskSmsSendReceive.this.getApplicationContext(),
                        R.string.task_alert_nullserverCenterAddress,
                        Toast.LENGTH_SHORT)
                        .show();
                serverCenterAddressEditText.setError(getString(R.string.task_alert_nullserverCenterAddress));
                return;
            }
        }*/
       
        if (model == null){
            model = new TaskSmsSendReceiveModel();
            taskd.setCurrentTaskIdAndSequence(model);
        }
        	model.setTaskName(taskNameEditText.getText().toString().trim());
        model.setTaskType(WalkStruct.TaskType.SMSSendReceive.name());
        model.setEnable(1);
        model.setRepeat(Integer.parseInt(repeatEditText.getText()
                .toString()
                .trim()
                .length() == 0 ? "1" : repeatEditText.getText().toString().trim()));
        model.setSendTimeOut(Integer.parseInt(sendTimeOutEditText.getText()
                .toString()
                .trim()
                .length() == 0 ? "30" : sendTimeOutEditText.getText()
                .toString()
                .trim()));
        model.setReceiveTimeOut(Integer.parseInt(receiveTimeOutEditText.getText()
                .toString()
                .trim()
                .length() == 0 ? "60" : receiveTimeOutEditText.getText()
                .toString()
                .trim()));
        
        model.setRabRelTime(super.rabRelTimeEdt.getText().toString().trim().length() == 0 ? "50" : super.rabRelTimeEdt.getText().toString().trim());
        model.setRabRuelTime(super.rabAblTimeEdt.getText().toString().trim().length() == 0 ? "12:00" : super.rabAblTimeEdt.getText().toString().trim());
        if (isCDMA) {
            model.setServerNumber("");
        } else {
            model.setServerNumber(serverCenterAddressEditText.getText()
                    .toString()
                    .trim()
                    .length() == 0 ? "0" : serverCenterAddressEditText.getText()
                    .toString()
                    .trim());
        }
        model.setDesNumber(destinationEditText.getText().toString().trim().length() == 0 ? "0"
                : destinationEditText.getText().toString().trim());
        model.setContent(contentEditText.getText().toString().trim().length() == 0 ? "0"
                : contentEditText.getText().toString().trim());
        model.setInterVal(Integer.parseInt(interValEditText.getText()
                .toString()
                .trim()
                .length() == 0 ? "50" : interValEditText.getText().toString().trim()));

        List<TaskModel> array = taskd.getTaskListArray();
        if(RABTAG.equals(super.getRabTag())){//依标志区分并发与普通业务
        	for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
        		if((super.getMultiRabName()).equals(taskd.getCurrentTaskList().get(i).getTaskID())){
        			taskRabModel=(TaskRabModel)taskd.getCurrentTaskList().get(i);
        			break;
        		}
        	} 
        	if(isNew){
        		taskRabModel.setTaskModelList(taskRabModel.addTaskList(model));
        	}else{
        		taskRabModel.getTaskModel().remove(taskListId);
        		taskRabModel.getTaskModel().add(taskListId, model);
        	}
        }else{
        	if(isNew){
                array.add(array.size(),model);
        	}else{
        		array.remove(taskListId);
        		array.add(taskListId, model);
        	}
        }
        taskd.setTaskListArray(array);
        
        Toast.makeText(getApplicationContext(),
                isNew ? R.string.task_alert_newSucess
                        : R.string.task_alert_updateSucess,
                Toast.LENGTH_SHORT).show();
        TaskSmsSendReceive.this.finish();
    }
    
    
    /**
     * 添加EditText输入监听限制<BR>
     * [功能详细描述]
     */
    public void addEditTextWatcher(){
        
        taskNameEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(taskNameEditText.getText().toString().trim().length()==0){    //任务名为空
                    taskNameEditText.setError(getString(R.string.task_alert_nullName));
                }
                
            }
        });
        
        repeatEditText.addTextChangedListener(new EditTextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {
                if( repeatEditText.getText().toString().trim().equals("0") 
                        ||  repeatEditText.getText().toString().trim().length()==0){
                    repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
                }
            }
        });
 
        interValEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if( interValEditText.getText().toString().trim().equals("0")
                        || interValEditText.getText().toString().trim().length()==0 ){
                    interValEditText.setError(getString(R.string.task_alert_nullInterval));
                }
            }
        });
        
        contentEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (contentEditText.getText().toString().trim().length() == 0 ) {
                    contentEditText.setError(getString(R.string.task_alert_nullcontent));
                }
            }
        });
    }
}
