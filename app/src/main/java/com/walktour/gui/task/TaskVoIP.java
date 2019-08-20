/**
 * 
 */
package com.walktour.gui.task;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.TaskVoIPModel;

import java.util.List;

/**
 * @author Jihong Xie
 * VoIP任务
 */
public class TaskVoIP extends BasicActivity implements View.OnClickListener {
	
	private EditText taskname;
	private Spinner callType;
	private EditText duration;
	private EditText repeat;
	private EditText interval;
	private EditText timeout;
	private EditText nodataTimeout;
	
	private Spinner registerType;
	private EditText username;
	private EditText password;
	private EditText serverIP;
	private EditText serverPort;
	private EditText imsInfo;
	private EditText imsServiceIP;
	private EditText dialUserName;
	
	
	private Spinner voipType;
	private Spinner useSample;
	private EditText audioFile;
	private EditText videoFile;
	private Spinner calMOS;
	private Spinner doSave;
	private EditText savePath;
	
	TaskListDispose taskd =null;
	TaskVoIPModel model =null;
	private int taskListId=-1;
	private boolean isNew=true;
	
	private Button save;
	private Button canel;
	//private Spinner dataConnectType;//数据连接选择：PPP or WIFI

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.task_voip);
		
		taskd=TaskListDispose.getInstance();
        Bundle bundle = getIntent().getExtras(); 
        if(bundle != null && bundle.containsKey("taskListId")){
        	taskListId = bundle.getInt("taskListId");
        	model = (TaskVoIPModel)taskd.getTaskListArray().get(taskListId);
        	isNew=false;
        }
		
		//设置标题
		(initTextView(R.id.title_txt)).setText(getResources().getString(R.string.act_task_voip));
		(initImageView(R.id.pointer)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TaskVoIP.this.finish();
			}
		});
		taskname = initEditText(R.id.edit_taskname);
		callType = (Spinner)findViewById(R.id.edit_tasktype);
		duration = initEditText(R.id.edit_duration);
		repeat = initEditText(R.id.edit_repeat);
		interval = initEditText(R.id.edit_interval);
		timeout = initEditText(R.id.edit_timeout);
		nodataTimeout = initEditText(R.id.edit_nodate_timeout);
		
		registerType = (Spinner)findViewById(R.id.edit_registertype);
		username = initEditText(R.id.edit_username);
		password = initEditText(R.id.edit_password);
		serverIP = initEditText(R.id.edit_serverip);
		serverPort = initEditText(R.id.edit_serverport);
		imsInfo = initEditText(R.id.edit_imsinfo);
		imsServiceIP = initEditText(R.id.edit_imsip);
		dialUserName = initEditText(R.id.edit_dialuser);
		
		voipType = (Spinner)findViewById(R.id.edit_voiptype);
		useSample = (Spinner)findViewById(R.id.edit_usesample);
		audioFile = initEditText(R.id.edit_audiofile);
		videoFile = initEditText(R.id.edit_vediofile);
		calMOS = (Spinner)findViewById(R.id.edit_moscal);
		doSave = (Spinner)findViewById(R.id.edit_dosave);
		savePath = initEditText(R.id.edit_savepath);
		
		save = initButton(R.id.btn_ok);
		canel = initButton(R.id.btn_cencle);
		
//		 //wifi support 
//		dataConnectType = (Spinner)findViewById(R.id.edit_data_connect_type);
//	    ArrayAdapter<String> dataConnectTypeAdapter = new ArrayAdapter<String>(this,
//	          R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_data_connect_type));
//	    dataConnectTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
//	    dataConnectType.setAdapter(dataConnectTypeAdapter);
    
		//给下拉框项设置数据源
		setAdapter();
		
		//给UI设置界面数据及隐藏显示
		findView();
		addEditTextWatcher();
	}

	/**
	 * 编辑时事件监听
	 */
	private void addEditTextWatcher() {
		taskname.addTextChangedListener(watcher);
	}
	
	private EditTextWatcher watcher = new EditTextWatcher(){
		@Override
		public void afterTextChanged(Editable s) {
			View current = getCurrentFocus();
			if(current != null && current instanceof EditText){
				EditText edit = (EditText)current;
				String message = null;
				if(edit.getText().toString().trim().length() == 0){
					if(edit == taskname) message = getString(R.string.task_alert_nullName);
					edit.setError(message);
				}
			}
		}
	};

	/**
	 * 给UI设置界面数据及隐藏显示
	 */
	private void findView() {
		if(model == null) {
			model = new TaskVoIPModel();
		}
		
		taskname.setText(model.getTaskName());
		callType.setSelection(model.callType);
		repeat.setText(String.valueOf(model.getRepeat()));
		duration.setText(model.duration);
		interval.setText(String.valueOf(model.getInterVal()));
		timeout.setText(model.timeout);
		nodataTimeout.setText(model.nodataTimeout);
		
		registerType.setSelection(model.registerType);
		username.setText(model.username);
		password.setText(model.password);
		serverIP.setText(model.serverIP);
		serverPort.setText(model.serverPort);
		imsInfo.setText(model.imsInfo);
		imsServiceIP.setText(model.imsServiceIP);
		dialUserName.setText(model.dialUser);
		
		voipType.setSelection(model.voipType);
		useSample.setSelection(model.useSample);
		audioFile.setText(model.audioFile);
		videoFile.setText(model.videoFile);
		calMOS.setSelection(model.calMOS);
		doSave.setSelection(model.doSave);
		savePath.setText(model.savePath);
		//dataConnectType.setSelection(model.getTypeProperty()==4?1:0); //ppp 0 wifi 1
	}

	/**
	 * 给下拉框项设置数据源
	 */
	private void setAdapter() {
		//呼叫类型adapter
		ArrayAdapter<String> callTypeAdapter = new ArrayAdapter<String>(this,
		        		R.layout.simple_spinner_custom_layout, 
		        		getResources().getStringArray(R.array.array_voip_calltype) );
		callTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		callType.setAdapter(callTypeAdapter);
		
		//注册类型
		ArrayAdapter<String> registerTypeAdapter = new ArrayAdapter<String>(this,
        		R.layout.simple_spinner_custom_layout, 
        		getResources().getStringArray(R.array.array_voip_registertype) );
		registerTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		registerType.setAdapter(registerTypeAdapter);
		//选择事件
		registerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			LinearLayout imsInfoView = (LinearLayout)findViewById(R.id.imsinfo_layout);
			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,
					int index, long position) {
				if(index != 1){
					imsInfoView.setVisibility(View.GONE);
				}else{
					imsInfoView.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		//voipType
		ArrayAdapter<String> voipTypeAdapter = new ArrayAdapter<String>(this,
		        		R.layout.simple_spinner_custom_layout, 
		        		getResources().getStringArray(R.array.array_voip_voiptype) );
		voipTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		voipType.setAdapter(voipTypeAdapter);
		
		//useSample
		ArrayAdapter<String> useSampleAdapter = new ArrayAdapter<String>(this,
        		R.layout.simple_spinner_custom_layout, 
        		getResources().getStringArray(R.array.array_voip_usesample) );
		useSampleAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		useSample.setAdapter(useSampleAdapter);

		//是否项选择adapter
		ArrayAdapter<String> adpAP = new ArrayAdapter<String>(this,
        		R.layout.simple_spinner_custom_layout, 
        		getResources().getStringArray(R.array.public_yn) );
        adpAP.setDropDownViewResource(R.layout.spinner_dropdown_item);
		
		calMOS.setAdapter(adpAP);
		doSave.setAdapter(adpAP);
		
		save.setOnClickListener(this);
		canel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v == save){
			saveTestTask();
		}else if(v == canel){
			TaskVoIP.this.finish();
		}
	}

	/**
	 * 保存数据方法
	 */
	private void saveTestTask() {
	        List<TaskModel> array = taskd.getTaskListArray();
//	        if (((Long)dataConnectType.getSelectedItemId()).intValue()==1)  //只有是WIFI的才设置
//	  			model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);  //
//	  		else 
//	  			model.setTypeProperty(WalkCommonPara.TypeProperty_Net);  //ppp

	        if(isNew){
				array.add(array.size(),model);
	        }else{
	            array.remove(taskListId);
	            array.add(taskListId, model);
	        }
	        taskd.setTaskListArray(array);
	        
	        Toast.makeText(getApplicationContext(),isNew?R.string.task_alert_newSucess:R.string.task_alert_updateSucess,Toast.LENGTH_SHORT).show();
	        TaskVoIP.this.finish();
	}
}
