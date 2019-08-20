package com.walktour.gui.task;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.WalkStruct;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.framework.view.DateTimePickDialogUtil;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.sms.receive.TaskSmsReceiveModel;

import java.util.List;

public class TaskSmsIncept extends BaseTaskActivity {
	TaskListDispose taskd =null;
	TaskSmsReceiveModel model =null;
	private int taskListId=-1;
	private boolean isNew=true;
	
    private EditText taskNameEditText;
    private EditText repeatEditText;
    private EditText timeOutEditText;
	private TaskRabModel taskRabModel;
	private LinearLayout unionTestLayout;
	/**
	 * 主叫联合
	 */
	private Spinner unionSpinner;
	private RelativeLayout rab_time_layout;
	private RelativeLayout rab_rule_time_layout;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        
        taskd=TaskListDispose.getInstance();
        Bundle bundle = getIntent().getExtras(); 
        if(bundle != null && bundle.containsKey("taskListId")){
        	taskListId = bundle.getInt("taskListId");
        	//根据标记做并发编辑
        	if(RABTAG.equals(super.getRabTag())){
        		for(int i=0;i<taskd.getCurrentTaskList().size();i++){ 
        			if(taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())){
        				model =	(TaskSmsReceiveModel)((TaskRabModel)(taskd.getCurrentTaskList().get(i))).getTaskModel().get(taskListId);
        				break;
        			}
        		}
        	}else{
        		model = (TaskSmsReceiveModel)taskd.getTaskListArray().get(taskListId);
        	}
        	abstModel = model;
        	isNew=false;
        }
        findView();
        addEditTextWatcher();
	}
	private void findView(){
    	//绑定Layout里面的ListView
//		LayoutInflater factory = LayoutInflater.from(this);
//		final View textEntryView = factory.inflate(R.layout.task_smsincept, null); 
		setContentView(R.layout.task_smsincept);
		(initTextView(R.id.title_txt)).setText(R.string.act_task_smsincept);//设置标题
        (initImageView(R.id.pointer)).setOnClickListener(this);
        ((RelativeLayout)findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);
        
		TextView tv_taskName = initTextView(R.id.txt_taskname);
		TextView tv_repeat =initTextView(R.id.txt_repeat);
		TextView tv_timeOut =initTextView(R.id.txt_timeOut);
		//TextView tv_interVal =(TextView)textEntryView.findViewById(R.id.txt_interVal);
		//TextView tv_saveSms =(TextView)textEntryView.findViewById(R.id.txt_saveSms);
		Button btn_ok = ( Button ) findViewById(R.id.btn_ok);
        Button btn_cencle = ( Button ) findViewById(R.id.btn_cencle);
        
        tv_taskName.setText(getString(R.string.task_taskName));
        tv_repeat.setText(getString(R.string.task_repeat));
        tv_timeOut.setText(getString(R.string.task_receiveTimeOut));
        //tv_interVal.setText(getString(R.string.task_interVal));
        //tv_saveSms.setText(getString(R.string.task_saveSms));
        btn_ok.setText(" "+getString(R.string.str_save)+" ");
        btn_cencle.setText(getString(R.string.str_cancle));
        
		taskNameEditText = initEditText(R.id.edit_taskname);
		repeatEditText =initEditText(R.id.edit_repeat);
		timeOutEditText =initEditText(R.id.edit_timeOut);
		//final EditText et_interVal =(EditText)textEntryView.findViewById(R.id.edit_interVal);
		//final Spinner et_saveSms =(Spinner)textEntryView.findViewById(R.id.edit_saveSms);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.public_yn));
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        //et_saveSms.setAdapter(adapter);
        
        //主被叫联合
        unionTestLayout = (LinearLayout)findViewById(R.id.task_uniontest_layout);
        if(ConfigRoutine.getInstance().isBluetoothSync(getApplicationContext())){
        	unionTestLayout.setVisibility(View.VISIBLE);
        }
        unionSpinner =(Spinner)findViewById(R.id.edit_unionTest);
        ArrayAdapter<String> unionApter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout,getResources().getStringArray(R.array.public_yn));
        unionApter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        unionSpinner.setAdapter(unionApter);
        
    	
		// 并发相对时间
		rab_time_layout = initRelativeLayout(R.id.rab_time_layout);
		rab_rule_time_layout = initRelativeLayout(R.id.rab_time_rel_layout);
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
	        		DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
	        				TaskSmsIncept.this, rabAblTimeEdt.getText().toString());
	        		dateTimePicKDialog.dateTimePicKDialog(rabAblTimeEdt);
	        	}
	        });
        
        if(model!=null){
        	taskNameEditText.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%")+1, model.getTaskName().toString().trim().length()));
	        repeatEditText.setText( String.valueOf(model.getRepeat()));
	        timeOutEditText.setText( String.valueOf(model.getTimeOut()));
	        unionSpinner.setSelection( model.isUnitTest()?1:0 );
	       // et_interVal.setText(String.valueOf(model.getInterVal()));
	       // et_saveSms.setSelection(model.getSaveSms());
        }else{
        	taskNameEditText.setText("SMS Receive");
	        repeatEditText.setText("10");
	        timeOutEditText.setText("60");
	        //if(ConfigRoutine.getInstance().isBluetoothSync(getApplicationContext())){
            	unionSpinner.setSelection(1);
            //}
	       // et_interVal.setText("50");
	       // et_saveSms.setSelection(1);
        }
        btn_ok.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        	    saveTestTask();
        	}
        });
        
        btn_cencle.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		TaskSmsIncept.this.finish();
        	}
        });
	}
	@Override 
	public void onResume(){
		super.onResume();
	}
	
	@Override 
	public void onDestroy(){
		 super.onDestroy(); 
	}	

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see com.walktour.gui.task.BaseTaskActivity#saveTestTask()
     */
    @Override
    public void saveTestTask() {
        if(taskNameEditText.getText().toString().trim().length()==0){    //任务名为空
            Toast.makeText(com.walktour.gui.task.TaskSmsIncept.this.getApplicationContext(), R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
            taskNameEditText.setError(getString(R.string.task_alert_nullName));
            return;
        }else if( repeatEditText.getText().toString().trim().equals("0") 
				||  repeatEditText.getText().toString().trim().length()==0){
			Toast.makeText( getApplicationContext(),
					R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
			return;
		}else if( timeOutEditText.getText().toString().trim().equals("0") 
				||  timeOutEditText.getText().toString().trim().length()==0 ){
			Toast.makeText(getApplicationContext(),
					R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			timeOutEditText.setError(getString(R.string.task_alert_nullTimeout));
			return;
		}else{}
        if(model==null){
            model = new TaskSmsReceiveModel();
			taskd.setCurrentTaskIdAndSequence(model);
        }
        //依据标记区分用户名的编辑
        model.setTaskName(taskNameEditText.getText().toString().trim());
        model.setTaskType(WalkStruct.TaskType.SMSIncept.name());
        model.setEnable(1);
        model.setRepeat(Integer.parseInt( repeatEditText.getText().toString().trim().length()==0?"1":repeatEditText.getText().toString().trim()));
        model.setTimeOut(Integer.parseInt( timeOutEditText.getText().toString().trim().length()==0?"60":timeOutEditText.getText().toString().trim()));
        model.setInterVal(0);
//        model.setSaveSms(1);
        model.setUnitTest( unionSpinner.getSelectedItemPosition()==1 ); 
        //model.setInterVal(Integer.parseInt( et_interVal.getText().toString().trim().length()==0?"0":et_interVal.getText().toString().trim()));
        //model.setSaveSms(et_saveSms.getSelectedItemPosition());
        
        model.setRabRelTime(super.rabRelTimeEdt.getText().toString().trim().length() == 0 ? "50" : super.rabRelTimeEdt.getText().toString().trim());
        model.setRabRuelTime(super.rabAblTimeEdt.getText().toString().trim().length() == 0 ? "12:00" : super.rabAblTimeEdt.getText().toString().trim());

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
        
        Toast.makeText(getApplicationContext(),isNew?R.string.task_alert_newSucess:R.string.task_alert_updateSucess,Toast.LENGTH_SHORT).show();
        TaskSmsIncept.this.finish();
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
        
        timeOutEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(timeOutEditText.getText().toString().trim().equals("0")){
                    timeOutEditText.setError(getString(R.string.task_alert_nullTimeout));
                }
            }
        });
        
        repeatEditText.addTextChangedListener(new EditTextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {
                if( repeatEditText.getText().toString().trim().equals("0") 
                        ||  repeatEditText.getText().toString().trim().length()==0){
                    repeatEditText.setError(getString(R.string.task_alert_nullName));
                }
            }
        });
    }
}
