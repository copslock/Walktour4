package com.walktour.gui.task;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.WalkStruct;
import com.walktour.control.bean.Verify;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.mms.receive.TaskMmsReceiveModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

import java.util.List;

public class TaskMmsReceive extends BaseTaskActivity {
	TaskListDispose taskd =null;
	TaskMmsReceiveModel model =null;
	private int taskListId=-1;
	private boolean isNew=true;
	
    private EditText taskNameEditText;
    private EditText repeatEditText;
    private EditText timeOutEditText;
    private EditText pushTimeOutEditText;
    private EditText serverAddressEditText;
    private EditText gatewayEditText;
    private EditText portEditText;
    private EditText interValEditText;
	private TaskRabModel taskRabModel;
	private Spinner disConnectSpinner;
	private LinearLayout unionTestLayout;
	/**
	 * 主叫联合
	 */
	private Spinner unionSpinner;
    
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
        				model =	(TaskMmsReceiveModel)((TaskRabModel)(taskd.getCurrentTaskList().get(i))).getTaskModel().get(taskListId);
        				break;
        			}
        		}
        	}else{
        		model = (TaskMmsReceiveModel)taskd.getTaskListArray().get(taskListId);
        	}
        	abstModel = model;
        	isNew=false;
        }
        findView();
        addEditTextWatcher();
	}
	private void findView(){
    	//绑定Layout里面的ListView
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.task_mmsincept, null); 
		((TextView)textEntryView.findViewById(R.id.title_txt)).setText(R.string.act_task_mmsincept);//设置标题
        ((ImageView)textEntryView.findViewById(R.id.pointer)).setOnClickListener(this);
        ((RelativeLayout)textEntryView.findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);
        
		TextView tv_taskName = (TextView)textEntryView.findViewById(R.id.txt_taskname);
		TextView tv_repeat =(TextView)textEntryView.findViewById(R.id.txt_repeat);
		TextView tv_timeOut =(TextView)textEntryView.findViewById(R.id.txt_timeOut);
		//TextView tv_connectTimeOut =(TextView)textEntryView.findViewById(R.id.txt_connectTimeOut);
		TextView tv_pushTimeOut =(TextView)textEntryView.findViewById(R.id.txt_pushTimeOut);
		TextView tv_serverAddress =(TextView)textEntryView.findViewById(R.id.txt_serverAddress);
		TextView tv_gateway =(TextView)textEntryView.findViewById(R.id.txt_gateway);
		TextView tv_port =(TextView)textEntryView.findViewById(R.id.txt_port);
		TextView tv_interVal =(TextView)textEntryView.findViewById(R.id.txt_interVal);
		Button btn_ok = ( Button ) textEntryView.findViewById(R.id.btn_ok);
        Button btn_cencle = ( Button ) textEntryView.findViewById(R.id.btn_cencle);
        
        tv_taskName.setText(getString(R.string.task_taskName));
        tv_repeat.setText(getString(R.string.task_repeat));
        tv_timeOut.setText( getString(R.string.task_receiveTimeOut) );
        //tv_connectTimeOut.setText(getString(R.string.task_connectTimeOut));
        tv_pushTimeOut.setText(getString(R.string.task_pushTimeOut));
        tv_serverAddress.setText(getString(R.string.task_serverAddress));
        tv_gateway.setText(getString(R.string.task_gateway));
        tv_port.setText(getString(R.string.task_port));
        tv_interVal.setText(getString(R.string.task_interVal));
        btn_ok.setText(" "+getString(R.string.str_save)+" ");
        btn_cencle.setText(getString(R.string.str_cancle));
        
		taskNameEditText = (EditText)textEntryView.findViewById(R.id.edit_taskname);
		repeatEditText =(EditText)textEntryView.findViewById(R.id.edit_repeat);
		timeOutEditText =(EditText)textEntryView.findViewById(R.id.edit_timeOut);
		//final EditText et_connectTimeOut =(EditText)textEntryView.findViewById(R.id.edit_connectTimeOut);
		pushTimeOutEditText =(EditText)textEntryView.findViewById(R.id.edit_pushTimeOut);
		serverAddressEditText =(EditText)textEntryView.findViewById(R.id.edit_serverAddress);
		gatewayEditText =(EditText)textEntryView.findViewById(R.id.edit_gateway);
		portEditText =(EditText)textEntryView.findViewById(R.id.edit_port);
		interValEditText =(EditText)textEntryView.findViewById(R.id.edit_interVal);
		
		//断开网络配置
		disConnectSpinner =(Spinner)textEntryView.findViewById(R.id.edit_disConnect);
        ArrayAdapter<String> disconnect = new ArrayAdapter<String>(this,
        		R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_task_disconnect));
        disconnect.setDropDownViewResource(R.layout.spinner_dropdown_item);
        disConnectSpinner.setAdapter(disconnect);
        
        //主被叫联合
        unionTestLayout = (LinearLayout)textEntryView.findViewById(R.id.task_uniontest_layout);
        if(ConfigRoutine.getInstance().isBluetoothSync(getApplicationContext())){
        	unionTestLayout.setVisibility(View.VISIBLE);
        }
        unionSpinner =(Spinner)textEntryView.findViewById(R.id.edit_unionTest);
        ArrayAdapter<String> unionApter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout,getResources().getStringArray(R.array.public_yn));
        unionApter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        unionSpinner.setAdapter(unionApter);
		
        if(model!=null){
        	taskNameEditText.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%")+1, model.getTaskName().toString().trim().length()));
	        repeatEditText.setText( String.valueOf(model.getRepeat()));
	        timeOutEditText.setText( String.valueOf(model.getTimeOut()));
	        //et_connectTimeOut.setText( String.valueOf(model.getConnectTimeOut()));
	        pushTimeOutEditText.setText( String.valueOf(model.getPushTimeOut()));
	        serverAddressEditText.setText( String.valueOf(model.getServerAddress()));
	        gatewayEditText.setText( String.valueOf(model.getGateway()));
	        portEditText.setText( String.valueOf(model.getPort()));
	        interValEditText.setText(String.valueOf(model.getInterVal()));
			disConnectSpinner.setSelection(model.getDisConnect());
			unionSpinner.setSelection( model.isUnitTest()?1:0 );
        }else{
        	taskNameEditText.setText("MMS Recevice");
	        repeatEditText.setText("1");
	        serverAddressEditText.setText("http://");
	        gatewayEditText.setText("10.0.0.172");
	        portEditText.setText("80");
	        pushTimeOutEditText.setText("120");
	        timeOutEditText.setText("60");
	        interValEditText.setText("3");
	        pushTimeOutEditText.setText("120");
            disConnectSpinner.setSelection(1);
            //if(ConfigRoutine.getInstance().isBluetoothSync(getApplicationContext())){
            	unionSpinner.setSelection(1);
            //}
	        //et_connectTimeOut.setText("60");
        }
        btn_ok.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        	    saveTestTask();
        	}
        });
        
        btn_cencle.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		TaskMmsReceive.this.finish();
        	}
        });
        textEntryView.setVerticalScrollBarEnabled(true);
        setContentView(textEntryView);
        tv_taskName.requestFocus();
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
            Toast.makeText(com.walktour.gui.task.TaskMmsReceive.this.getApplicationContext(), R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
            taskNameEditText.setError(getString(R.string.task_alert_nullName));
            return;
        }else if( repeatEditText.getText().toString().trim().equals("0") 
				||  repeatEditText.getText().toString().trim().length()==0){
			Toast.makeText( getApplicationContext(),
					R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			taskNameEditText.setError(getString(R.string.task_alert_nullRepeat));
			return;
		}else if( serverAddressEditText.getText().toString().trim().length()==0 ){//服务中心地址为空
            Toast.makeText( TaskMmsReceive.this , 
                    getString( R.string.task_alert_input)+" "+getString(R.string.task_serverAddress)
                    ,Toast.LENGTH_SHORT).show();
            serverAddressEditText.setError(getString( R.string.task_alert_input)+" "+getString(R.string.task_serverAddress));
            return;
        }else if(serverAddressEditText.getText().toString().trim().equalsIgnoreCase("http://")){
        	Toast.makeText(TaskMmsReceive.this,
                    getString(R.string.task_alert_input) + " "
                            + getString(R.string.task_serverAddress),
                    Toast.LENGTH_SHORT).show();
        	  serverAddressEditText.setError(getString(R.string.task_alert_input) + " "
                      + getString(R.string.task_serverAddress));
        	return;
        }else if( timeOutEditText.getText().toString().trim().equals("0") 
				|| timeOutEditText.getText().toString().trim().length()==0 
				|| pushTimeOutEditText.getText().toString().trim().equals("0") 
				|| pushTimeOutEditText.getText().toString().trim().length()==0 ){
			Toast.makeText(getApplicationContext(),
					R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			taskNameEditText.setError(getString(R.string.task_alert_nullTimeout));
			return;
		}else if( gatewayEditText.getText().toString().trim().length()==0 ){//网关为空
            Toast.makeText( TaskMmsReceive.this , 
                    getString( R.string.task_alert_input)+" "+getString(R.string.task_gateway)
                    ,Toast.LENGTH_SHORT).show();
            taskNameEditText.setError(getString( R.string.task_alert_input)+" "+getString(R.string.task_gateway));
            return;
        }else if( portEditText.getText().toString().trim().length()==0   ){//端口为空
            Toast.makeText( TaskMmsReceive.this , 
                    getString( R.string.task_alert_input)+" "+getString(R.string.task_port)
                    ,Toast.LENGTH_SHORT).show();
            taskNameEditText.setError(getString( R.string.task_alert_input)+" "+getString(R.string.task_port));
            return;
        }else{
            if( ! Verify.isIp( gatewayEditText.getText().toString() ) ){//网关不正确
                Toast.makeText( TaskMmsReceive.this , 
                        getString( R.string.task_alert_input)+" "+getString(R.string.task_gateway)
                        ,Toast.LENGTH_SHORT).show();
                taskNameEditText.setError(getString( R.string.task_alert_input)+" "+getString(R.string.task_gateway));
                return;
            }else if( ! Verify.isPort( portEditText.getText().toString())  ){//端口不正确
                Toast.makeText( TaskMmsReceive.this , 
                        getString( R.string.task_alert_input)+" "+getString(R.string.task_port)
                        ,Toast.LENGTH_SHORT).show();
                taskNameEditText.setError(getString(R.string.task_alert_input));
                return;
            }
        }
        if(model==null){
            model = new TaskMmsReceiveModel();
            taskd.setCurrentTaskIdAndSequence(model);
        }
        //依据标记区分用户名的编辑
        model.setTaskName(taskNameEditText.getText().toString().trim());
        model.setTaskType(WalkStruct.TaskType.MMSIncept.name());
        model.setEnable(1);
        model.setRepeat(Integer.parseInt( repeatEditText.getText().toString().trim().length()==0?"1":repeatEditText.getText().toString().trim()));
        model.setTimeOut(Integer.parseInt( timeOutEditText.getText().toString().trim().length()==0?"60":timeOutEditText.getText().toString().trim()));
        //model.setConnectTimeOut(Integer.parseInt( et_connectTimeOut.getText().toString().trim().length()==0?"60":et_connectTimeOut.getText().toString().trim()));
        model.setConnectTimeOut( 60 );
        model.setPushTimeOut(Integer.parseInt( pushTimeOutEditText.getText().toString().trim().length()==0?"120":pushTimeOutEditText.getText().toString().trim()));
        model.setServerAddress(serverAddressEditText.getText().toString().trim().length()==0?"http://":serverAddressEditText.getText().toString().trim());
        model.setGateway(gatewayEditText.getText().toString().trim().length()==0?"0.0.0.0":gatewayEditText.getText().toString().trim());
        model.setPort(Integer.parseInt( portEditText.getText().toString().trim().length()==0?"80":portEditText.getText().toString().trim()));
//        model.setInterVal(Integer.parseInt( interValEditText.getText().toString().trim().length()==0?"50":interValEditText.getText().toString().trim()));
        model.setDisConnect(disConnectSpinner.getSelectedItemPosition());

        model.setUnitTest( unionSpinner.getSelectedItemPosition()==1 ); 

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
                array.add(array.size(), model);
        	}else{
        		array.remove(taskListId);
        		array.add(taskListId, model);
        	}
        }
        taskd.setTaskListArray(array);
        
        Toast.makeText(getApplicationContext(),isNew?R.string.task_alert_newSucess:R.string.task_alert_updateSucess,Toast.LENGTH_SHORT).show();
        TaskMmsReceive.this.finish();
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
 
        interValEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if( interValEditText.getText().toString().trim().equals("0")
                        || interValEditText.getText().toString().trim().length()==0 ){
                    interValEditText.setError(getString(R.string.task_alert_nullInterval));
                }
            }
        });
        
        serverAddressEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if( serverAddressEditText.getText().toString().trim().length()==0 ){//服务中心地址为空
                    serverAddressEditText.setError(getString( R.string.task_alert_input)+" "+getString(R.string.task_serverAddress));
                }
            }
        });
        
        gatewayEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if( ! Verify.isIp( gatewayEditText.getText().toString() ) ){//网关不正确
                    gatewayEditText.setError(getString( R.string.task_alert_input)+" "+getString(R.string.task_gateway));
                }
            }
        });
        
        portEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (portEditText.getText().toString().trim().length() == 0) {//端口为空
                    portEditText.setError(getString(R.string.task_alert_input) + " "
                            + getString(R.string.task_port));
                }
            }
        });

    }
}
