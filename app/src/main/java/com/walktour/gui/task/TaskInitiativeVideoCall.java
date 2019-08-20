package com.walktour.gui.task;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.WalkStruct;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;

import java.util.List;

public class TaskInitiativeVideoCall extends BasicActivity {
	TaskListDispose taskd =null;
	TaskInitiativeCallModel model =null;
	private int taskListId=-1;
	private boolean isNew=true;
	private RelativeLayout rab_time_layout;
	private RelativeLayout rab_rule_time_layout;
	@Override
	public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        
        taskd=TaskListDispose.getInstance();
        Bundle bundle = getIntent().getExtras(); 
        if(bundle != null && bundle.containsKey("taskListId")){
        	taskListId = bundle.getInt("taskListId");
        	model = (TaskInitiativeCallModel)taskd.getTaskListArray().get(taskListId);
        	isNew=false;
        }
        showView();
	}
	private void showView(){
    	//绑定Layout里面的ListView
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.task_initiativecall, null); 
		((TextView)textEntryView.findViewById(R.id.title_txt)).setText(R.string.act_task_initiativevideocall);//设置标题
		TextView tv_taskName = (TextView)textEntryView.findViewById(R.id.txt_taskname);
		TextView tv_repeat =(TextView)textEntryView.findViewById(R.id.txt_repeat);
		TextView tv_callNumber =(TextView)textEntryView.findViewById(R.id.txt_callNumber);
		TextView tv_connectTime = (TextView)textEntryView.findViewById(R.id.txt_connectTime);
		TextView tv_keepTime = (TextView)textEntryView.findViewById(R.id.txt_keepTime);
		TextView tv_interVal = (TextView)textEntryView.findViewById(R.id.txt_interVal);
/*		TextView tv_callMOSServer =(TextView)textEntryView.findViewById(R.id.txt_callMOSServer);
		TextView tv_parallelData =(TextView)textEntryView.findViewById(R.id.txt_parallelData);*/
        Button btn_ok = ( Button ) textEntryView.findViewById(R.id.btn_ok);
        Button btn_cencle = ( Button ) textEntryView.findViewById(R.id.btn_cencle);
        
        tv_taskName.setText(getString(R.string.task_taskName));
        tv_repeat.setText(getString(R.string.task_repeat));
        tv_callNumber.setText(getString(R.string.task_callNumber));
        tv_connectTime.setText(getString(R.string.task_connectTime));
        tv_keepTime.setText(getString(R.string.task_keepTime));
        tv_interVal.setText(getString(R.string.task_interVal));
/*        tv_callMOSServer.setText(getString(R.string.task_callMOSServer));
        tv_parallelData.setText(getString(R.string.task_parallelData));*/
        btn_ok.setText(" "+getString(R.string.str_save)+" ");
        btn_cencle.setText(getString(R.string.str_cancle));
        
		final EditText et_taskName = (EditText)textEntryView.findViewById(R.id.edit_taskname);
		final EditText et_repeat =(EditText)textEntryView.findViewById(R.id.edit_repeat);
		final EditText et_callNumber =(EditText)textEntryView.findViewById(R.id.edit_callNumber);
		final EditText et_connectTime = (EditText)textEntryView.findViewById(R.id.edit_connectTime);
		final EditText et_keepTime = (EditText)textEntryView.findViewById(R.id.edit_keepTime);
		final EditText et_interVal = (EditText)textEntryView.findViewById(R.id.edit_interVal);
		/*final Spinner et_callMOSServer =(Spinner)textEntryView.findViewById(R.id.edit_callMOSServer);
		final Spinner et_parallelData =(Spinner)textEntryView.findViewById(R.id.edit_parallelData);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.public_yn));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        et_callMOSServer.setAdapter(adapter);
        ArrayAdapter<String> paadapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.task_call_parallel));
        paadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        et_parallelData.setAdapter(paadapter);
        et_callMOSServer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){ 
        	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3){
        		if(arg2==1)
        			Toast.makeText(getApplicationContext(), R.string.task_alert_callMOSServer, Toast.LENGTH_SHORT).show();
        	}
        	public void onNothingSelected(AdapterView<?> arg0){
        		System.out.println("========No Change.========");
        	}
        });*/
		
        
        if(model!=null){
	        et_taskName.setText( model.getTaskName());
	        et_repeat.setText(String.valueOf(model.getRepeat()));
	        et_callNumber.setText( model.getCallNumber());
	        et_connectTime.setText( String.valueOf(model.getConnectTime()));
	        et_keepTime.setText( String.valueOf(model.getKeepTime()));
	        et_interVal.setText( String.valueOf(model.getInterVal()));
	        /*et_callMOSServer.setSelection(model.getCallMOSServer());
	        et_parallelData.setSelection( model.getParallelData());*/
        }else{
	        et_repeat.setText("1");
	        et_connectTime.setText("15");
	        et_keepTime.setText("45");
	        et_interVal.setText("15");
        }
        btn_ok.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		if(et_taskName.getText().toString().trim().length()==0){	//任务名为空
        			Toast.makeText(com.walktour.gui.task.TaskInitiativeVideoCall.this.getApplicationContext(), R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
        			return;
        		}else if( et_repeat.getText().toString().trim().equals("0") 
        				||  et_repeat.getText().toString().trim().length()==0){
        			Toast.makeText( getApplicationContext(),
        					R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
        			return;
        		}
        		else if( et_connectTime.getText().toString().trim().equals("0") 
        				||  et_connectTime.getText().toString().trim().length()==0 ){
        			Toast.makeText(getApplicationContext(),getString( R.string.task_alert_input)+" "+
        					getString( R.string.task_connectTime ), Toast.LENGTH_SHORT).show();
        			return;
        		}else if( et_keepTime.getText().toString().trim().equals("0") 
        				|| et_keepTime.getText().toString().trim().length()==0 ){
        			Toast.makeText(getApplicationContext(),getString( R.string.task_alert_input)+" "+
        					getString( R.string.task_keepTime ), Toast.LENGTH_SHORT).show();
        			return;
        		}
        		else if( et_interVal.getText().toString().trim().equals("0")
        				|| et_interVal.getText().toString().trim().length()==0 ){
        			Toast.makeText(getApplicationContext(),
        					R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
        			return;
        		}else if(et_callNumber.getText().toString().trim().length()==0){	//拨打号码为空
        			Toast.makeText(getApplicationContext(), R.string.task_alert_nullcallNumber,Toast.LENGTH_SHORT).show();
        			return;
        		}else{
	        		String[] taskNames = taskd.getTaskNames(3);
	        		for(int i=0;i<taskNames.length;i++){
	        			if((isNew && taskNames[i].equals(et_taskName.getText().toString().trim()))	//判断任务名是否重复
	        					|| (!isNew && taskListId !=i && taskNames[i].equals(et_taskName.getText().toString().trim())))
	        			{
	        				Toast.makeText(getApplicationContext(),R.string.task_alert_existsName,Toast.LENGTH_SHORT).show();
	    					return;
	        			}
	        		}
        		}
        		if(model==null){
        			model = new TaskInitiativeCallModel();
					taskd.setCurrentTaskIdAndSequence(model);
        		}
        		model.setTaskName(et_taskName.getText().toString().trim());
        		model.setTaskType(WalkStruct.TaskType.InitiativeVideoCall.name());
        		model.setEnable(1);
        		model.setRepeat(Integer.parseInt( et_repeat.getText().toString().trim().length()==0?"0":et_repeat.getText().toString().trim()));
        		model.setCallNumber(et_callNumber.getText().toString().trim());
        		model.setConnectTime(Integer.parseInt( et_connectTime.getText().toString().trim().length()==0?"0":et_connectTime.getText().toString().trim()));
        		model.setKeepTime(Integer.parseInt( et_keepTime.getText().toString().trim().length()==0?"0":et_keepTime.getText().toString().trim()));
        		model.setInterVal(Integer.parseInt( et_interVal.getText().toString().trim().length()==0?"0":et_interVal.getText().toString().trim()));
        		model.setMosTest( 0 );
        		List<TaskModel> array = taskd.getTaskListArray();
        		if(isNew){
					array.add(array.size(), model);
        		}else{
        			array.remove(taskListId);
        			array.add(taskListId, model);
        		}
        		taskd.setTaskListArray(array);
        		
        		Toast.makeText(getApplicationContext(),isNew?R.string.task_alert_newSucess:R.string.task_alert_updateSucess,Toast.LENGTH_SHORT).show();
        		turnToTaskList();
        	}
        });
        
        btn_cencle.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		turnToTaskList();
        	}
        });
        textEntryView.setVerticalScrollBarEnabled(true);
        setContentView(textEntryView);
	}
	@Override 
	public void onResume(){
		super.onResume();
		showView();
	}
	
	@Override 
	public void onDestroy(){
		 super.onDestroy(); 
	}
	public boolean onKeyDown(int keyCode,KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
			turnToTaskList();
			return false;
		}
		return true;
	}
	private void turnToTaskList(){
		Intent intent = new Intent(com.walktour.gui.task.TaskInitiativeVideoCall.this ,Task.class);
		startActivity(intent);
		com.walktour.gui.task.TaskInitiativeVideoCall.this.finish();
	}	
}
