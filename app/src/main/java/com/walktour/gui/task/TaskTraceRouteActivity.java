package com.walktour.gui.task;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.WalkStruct;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.traceroute.TaskTraceRouteModel;

import java.util.List;

/**
 * 
 * @author lianzh
 * 
 * TracdRoute 任务配置界面
 *
 */
public class TaskTraceRouteActivity extends BaseTaskActivity {
	TaskListDispose taskd = null;
	TaskTraceRouteModel model = null;
	private int taskListId = -1;
	private boolean isNew = true;

    private EditText taskNameEditText;
    private EditText repeatEditText;
    private EditText interValEditText;
    private Spinner disConnectEditText;
	private TaskRabModel taskRabModel;
//	private Spinner dataConnectType;//数据连接选择：PPP or WIFI
	private EditText hostEditText;
	private EditText ipPacketEditText;
	private EditText hopTimeOutEditText;
	private EditText hopIntervalEditText;
	private EditText hopProbeNumEditText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        
        taskd=TaskListDispose.getInstance();
        Bundle bundle = getIntent().getExtras(); 
        if(bundle != null && bundle.containsKey("taskListId")){
         	taskListId = bundle.getInt("taskListId");
         	if(RABTAG.equals(super.getRabTag())){
        		for(int i=0;i<taskd.getCurrentTaskList().size();i++){ 
        				if(taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())){
            				model =	(TaskTraceRouteModel)((TaskRabModel)(taskd.getCurrentTaskList().get(i))).getTaskModel().get(taskListId);
            				break;
            			}
        		}
        	}else{
         		model = (TaskTraceRouteModel)taskd.getTaskListArray().get(taskListId);
         	}
         	abstModel = model;
         	isNew=false;
         }
        findView();
        addEditTextWatcher();
	}
	private void findView(){
	    setContentView(R.layout.task_traceroute);
		(initTextView(R.id.title_txt)).setText(getString(R.string.traceroute_title));//设置标题
        (initImageView(R.id.pointer)).setOnClickListener(this);
        ((RelativeLayout)findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);
        
		TextView tv_taskName = initTextView(R.id.txt_taskname);
		TextView tv_repeat =initTextView(R.id.txt_repeat);
		TextView tv_interVal =initTextView(R.id.txt_interVal);
		TextView tv_disConnect =initTextView(R.id.txt_disConnect);
		Button btn_ok = ( Button ) findViewById(R.id.btn_ok);
        Button btn_cencle = ( Button ) findViewById(R.id.btn_cencle);
        
        tv_taskName.setText(getString(R.string.task_taskName));
        tv_repeat.setText(getString(R.string.task_repeat));
        tv_interVal.setText(getString(R.string.task_interVal));
        tv_disConnect.setText(getString(R.string.task_disConnect));
        btn_ok.setText(" "+getString(R.string.str_save)+" ");
        btn_cencle.setText(getString(R.string.str_cancle));
        
		interValEditText =initEditText(R.id.edit_interVal);
		repeatEditText = initEditText(R.id.edit_repeat);
		taskNameEditText = initEditText(R.id.edit_taskname);
		hostEditText = initEditText(R.id.edit_host);
		ipPacketEditText = initEditText(R.id.edit_IpPacket);
		hopTimeOutEditText = initEditText(R.id.edit_hopTimeOut);
		hopIntervalEditText = initEditText(R.id.edit_hopInterval);
		hopProbeNumEditText = initEditText(R.id.edit_HopProbeNum);

		disConnectEditText =(Spinner)findViewById(R.id.edit_disConnect);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_task_disconnect));
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        disConnectEditText.setAdapter(adapter);
        
        //wifi support 
//      	dataConnectType = (Spinner)findViewById(R.id.edit_data_connect_type);
//        super.setDataConnectTypeSP(dataConnectType);

        if(model!=null){
        	taskNameEditText.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%")+1, model.getTaskName().toString().trim().length()));
	        repeatEditText.setText( String.valueOf(model.getRepeat()));
	        interValEditText.setText(String.valueOf(model.getInterVal()));
	        disConnectEditText.setSelection(model.getDisConnect());
//	        dataConnectType.setSelection(model.getTypeProperty()==4?1:0); //ppp 0 wifi 1
	        hostEditText.setText(model.getHost());
	        ipPacketEditText.setText(String.valueOf(model.getIpPacket()));
	        hopTimeOutEditText.setText(String.valueOf(model.getHopTimeout()));
	        hopIntervalEditText.setText(String.valueOf(model.getHopInterval()));
	        hopProbeNumEditText.setText(String.valueOf(model.getHopProbeNum()));
	        
        }else{
        	taskNameEditText.setText("Trace Route");
        	repeatEditText.setText("10");
 	        interValEditText.setText("10");
 	        ipPacketEditText.setText("64");
 	        hopTimeOutEditText.setText("5000");
 	        hopIntervalEditText.setText("1000");
 	        hopProbeNumEditText.setText("1");
 	        disConnectEditText.setSelection(1);
 	        
        }
        btn_ok.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        	    saveTestTask();
        	}
        });
        
        btn_cencle.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		TaskTraceRouteActivity.this.finish();
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
            Toast.makeText(getApplicationContext(), R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
            taskNameEditText.setError(getString(R.string.task_alert_nullName));
            return;
        }else if( repeatEditText.getText().toString().trim().equals("0") 
				||  repeatEditText.getText().toString().trim().length()==0){
			Toast.makeText( getApplicationContext(),
					R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
			return;
		}else if( interValEditText.getText().toString().trim().equals("0")
				|| interValEditText.getText().toString().trim().length()==0 ){
			Toast.makeText(getApplicationContext(),
					R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			interValEditText.setError(getString(R.string.task_alert_nullInterval));
			return;
		}else if(hostEditText.getText().toString().trim().length() == 0){
			Toast.makeText(getApplicationContext(), getString(R.string.sys_ping_alert_nullIP), Toast.LENGTH_SHORT).show();
			return;
		}else if(ipPacketEditText.getText().toString().trim().length() == 0){
			Toast.makeText(getApplicationContext(), getString(R.string.task_alert_nullSize), Toast.LENGTH_SHORT).show();
			return;
		}else if(Integer.valueOf(ipPacketEditText.getText().toString().trim()) < 0 || 
				Integer.valueOf(ipPacketEditText.getText().toString().trim()) >1460){
			Toast.makeText(getApplicationContext(), getString(R.string.task_alert_nullSize), Toast.LENGTH_SHORT).show();
			return;
		}else if(hopTimeOutEditText.getText().toString().trim().length() == 0){
			Toast.makeText(getApplicationContext(), getString(R.string.task_alert_nullInut),Toast.LENGTH_SHORT).show();
			return;
		}else if(hopIntervalEditText.getText().toString().trim().length() == 0){
			Toast.makeText(getApplicationContext(), getString(R.string.task_alert_nullInut),Toast.LENGTH_SHORT).show();
			return;
		}else if(hopProbeNumEditText.getText().toString().trim().length() == 0){
			Toast.makeText(getApplicationContext(), getString(R.string.task_alert_nullInut),Toast.LENGTH_SHORT).show();
			return;
		}
        else{}
        if(model==null){
            model = new TaskTraceRouteModel();
			taskd.setCurrentTaskIdAndSequence(model);
        }
        //依据标记区分用户名的编辑
        	model.setTaskName(taskNameEditText.getText().toString().trim());
        model.setTaskType(WalkStruct.TaskType.TraceRoute.name());
        model.setEnable(1);
        model.setRepeat(Integer.parseInt( repeatEditText.getText().toString().trim().length()==0?"10":repeatEditText.getText().toString().trim()));
        model.setInterVal(Integer.parseInt( interValEditText.getText().toString().trim().length()==0?"3":interValEditText.getText().toString().trim()));
        model.setDisConnect(disConnectEditText.getSelectedItemPosition());
        model.setHost(hostEditText.getText().toString());
        model.setIpPacket(Integer.parseInt(ipPacketEditText.getText().toString()));
        model.setHopTimeout(Long.parseLong(hopTimeOutEditText.getText().toString()));
        model.setHopInterval(Long.parseLong(hopIntervalEditText.getText().toString()));
        model.setHopProbeNum(Integer.parseInt(hopProbeNumEditText.getText().toString()));
        
//        if (((Long)dataConnectType.getSelectedItemId()).intValue()==1)  //只有是WIFI的才设置
//  			model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);  //
//  		else 
//  			model.setTypeProperty(WalkCommonPara.TypeProperty_Net);  //ppp

        List<TaskModel> array = taskd.getTaskListArray();
        if(RABTAG.equals(super.getRabTag())){//依标志区分并发与普通业务
        	for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
        		if(super.getMultiRabName().equals(taskd.getCurrentTaskList().get(i).getTaskID())){
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
			if (isNew) {
				array.add(array.size(),model);
			} else {
				array.remove(taskListId);
				array.add(taskListId, model);
			}
			
		}
        taskd.setTaskListArray(array);
        
        Toast.makeText(getApplicationContext(),isNew?R.string.task_alert_newSucess:R.string.task_alert_updateSucess,Toast.LENGTH_SHORT).show();
        TaskTraceRouteActivity.this.finish();
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
    }
    
}
