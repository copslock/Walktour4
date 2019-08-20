package com.walktour.gui.task;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.model.TaskSetModel;

public class TaskSet extends BasicActivity {
	TaskListDispose taskd =null;
	TaskSetModel model =null;
	@Override
	public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        taskd=TaskListDispose.getInstance();
    	model = (TaskSetModel)taskd.getTaskSetModel();
        showView();
	}
	
	private void showView(){
    	//绑定Layout里面的ListView
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.task_set, null); 
		
		TextView tv_outCycleTimes = (TextView)textEntryView.findViewById(R.id.txt_outCycleTimes);
		tv_outCycleTimes.setSelectAllOnFocus(true);
		TextView tv_timeEnable =(TextView)textEntryView.findViewById(R.id.txt_timeEnable);
		TextView tv_executiveDate =(TextView)textEntryView.findViewById(R.id.txt_executiveDate);
		TextView tv_startTime =(TextView)textEntryView.findViewById(R.id.txt_startTime);
		TextView tv_endTime =(TextView)textEntryView.findViewById(R.id.txt_endTime);
		Button btn_ok = ( Button ) textEntryView.findViewById(R.id.btn_ok);
        Button btn_cencle = ( Button ) textEntryView.findViewById(R.id.btn_cencle);
        
        tv_outCycleTimes.setText(getString(R.string.task_outCycleTimes));
        tv_executiveDate.setText(getString(R.string.task_executiveDate));
        tv_startTime.setText(getString(R.string.task_startTime));
        tv_endTime.setText(getString(R.string.task_endTime));
        tv_timeEnable.setText(getString(R.string.task_timeEnable));
        btn_ok.setText(" "+getString(R.string.str_save)+" ");
        btn_cencle.setText(getString(R.string.str_cancle));
        
		final EditText et_outCycleTimes = (EditText)textEntryView.findViewById(R.id.edit_outCycleTimes);
		final EditText et_executiveDate =(EditText)textEntryView.findViewById(R.id.edit_executiveDate);
		final EditText et_startTime =(EditText)textEntryView.findViewById(R.id.edit_startTime);
		final EditText et_endTime =(EditText)textEntryView.findViewById(R.id.edit_endTime);
		final Spinner et_timeEnable =(Spinner)textEntryView.findViewById(R.id.edit_timeEnable);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.public_yn));
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        et_timeEnable.setAdapter(adapter);
        
        et_outCycleTimes.setText(String.valueOf(model.getOutCycleTimes()));
        et_executiveDate.setText(model.getExecutiveDate());
        et_startTime.setText(model.getStartTime());
        et_endTime.setText(model.getEndTime());
        et_timeEnable.setSelection(model.getTimeEnable());
   
        btn_ok.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		model.setOutCycleTimes(Integer.parseInt( et_outCycleTimes.getText().toString().trim().length()==0?"1":et_outCycleTimes.getText().toString().trim()));
        		model.setExecutiveDate(et_executiveDate.getText().toString().trim().length()==0?"":et_executiveDate.getText().toString().trim());
        		model.setStartTime(et_startTime.getText().toString().trim().length()==0?"":et_startTime.getText().toString().trim());
        		model.setEndTime(et_endTime.getText().toString().trim().length()==0?"":et_endTime.getText().toString().trim());
        		model.setTimeEnable(et_timeEnable.getSelectedItemPosition());
        		
        		taskd.setTaskSetModel(model);
        		Toast.makeText(getApplicationContext(),"Done!",Toast.LENGTH_SHORT).show();
        		finish();
        	}
        });
        
        btn_cencle.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		finish();
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
			finish();
			return false;
		}
		return true;
	}
	/*private void turnToTaskList(){
		com.walktour.gui.task.TaskSet.this.finish();
	}*/	
}
