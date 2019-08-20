/**
 * 
 */
package com.walktour.gui.task;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.walktour.Utils.StringUtil;
import com.walktour.framework.view.DateTimePickDialogUtil;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.speedtest.TaskSpeedTestModel;

import java.util.List;

/**
 * @author Jihong Xie
 * SpeedTest 界面类
 */
public class TaskSpeedTest extends BaseTaskActivity {
	/**任务名称*/
	private EditText taskName;
	/**重复次数*/
	private EditText repeat;
	/**URL*/
	private EditText url;
	/**间隔时长*/
	private EditText interval;
	/**连接方式*/
	private Spinner disConnect;
	private Button btnView;
	
	TaskListDispose taskd =null;
	TaskSpeedTestModel model =null;
	private int taskListId=-1;
	private boolean isNew=true;
	
	private Button save;//保存
	private Button cancel;//取消
	
	private TaskRabModel taskRabModel;
	
	private Spinner remoteFile;		//远程文件列表
//	private Spinner dataConnectType;//数据连接选择：PPP or WIFI
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_speedtest);
		
		// 设置标题名字
		(initTextView(R.id.title_txt)).setText(getResources()
				.getString(R.string.act_task_speedtest));

		taskd = TaskListDispose.getInstance();
		Bundle bundle = getIntent().getExtras();
		if(bundle != null && bundle.containsKey("taskListId")){  //加入普通业务与并发业务处理
         	taskListId = bundle.getInt("taskListId");
         	if(RABTAG.equals(super.getRabTag())){
        		for(int i=0;i<taskd.getCurrentTaskList().size();i++){ 
        				if(taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())){
            				model =	(TaskSpeedTestModel)((TaskRabModel)(taskd.getCurrentTaskList().get(i))).getTaskModel().get(taskListId);
            				break;
            			}
        		}
        	}else{
         		model = (TaskSpeedTestModel)taskd.getTaskListArray().get(taskListId);
         	}
         	abstModel = model;
         	isNew=false;
         }
		
		showView();
		addEditTextWatcher();
	}
	
	/**
	 * 文本编辑框改变监听
	 */
	private void addEditTextWatcher() {
		taskName.addTextChangedListener(etWatcher);
		repeat.addTextChangedListener(etWatcher);
		url.addTextChangedListener(etWatcher);
		interval.addTextChangedListener(etWatcher);
	}
	
	/**
     * EditTextWatcher具体实现
     */
    private EditTextWatcher etWatcher = new EditTextWatcher(){
    	public void afterTextChanged(android.text.Editable s) {
    		View view = getCurrentFocus();
    		if(view == taskName){
    			if(s.length() < 1)
    				taskName.setError(getString(R.string.task_alert_nullName));
    			else
    				taskName.setError(null);
			}else if(view == repeat && StringUtil.isLessThanZeroInteger(
										repeat.getText().toString().trim())){
				repeat.setError(getString(R.string.task_alert_nullRepeat));
			}else if(view == url){
				if(s.length() < 1)
					url.setError(getString(R.string.task_alert_nullUrl));
				else
					url.setError(null);
			}else if(view == interval && StringUtil.isLessThanZeroInteger(
					interval.getText().toString().trim())){
				interval.setError(getString(R.string.task_alert_nullInterval));
			}
    	};
    };
	private RelativeLayout rab_time_layout;
	private RelativeLayout rab_rule_time_layout;

	/**
	 * 界面控件展现设置
	 */
	private void showView() {
		taskName = initEditText(R.id.edit_taskname);
    	repeat = initEditText(R.id.edit_repeat);
    	url = initEditText(R.id.edit_url);
    	
    	interval = initEditText(R.id.edit_interval);
    	disConnect = (Spinner)findViewById(R.id.edit_disConnect);
    	((RelativeLayout)findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);
    	(initImageView(R.id.pointer)).setOnClickListener(this);
    	
		save = initButton(R.id.btn_ok);
		cancel = initButton(R.id.btn_cencle);
		btnView = initButton(R.id.btn_view);
		
		btnView.setOnClickListener(clickListener);
		save.setOnClickListener(this);
		cancel.setOnClickListener(this);
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
        				TaskSpeedTest.this, rabAblTimeEdt.getText().toString());
        		dateTimePicKDialog.dateTimePicKDialog(rabAblTimeEdt);
        	}
        });
		
		//远程文件
		remoteFile = (Spinner)findViewById(R.id.edit_remoteFile);
		ArrayAdapter<RemoteFile> fileAdapter = new ArrayAdapter<RemoteFile>(this,
	                R.layout.simple_spinner_custom_layout,fileArray );
		fileAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		remoteFile.setAdapter(fileAdapter);
		
		 //wifi support 
//		dataConnectType = (Spinner)findViewById(R.id.edit_data_connect_type);
//		super.setDataConnectTypeSP(dataConnectType);
	    
		//如果是新建，则初始化一个模板
        if(model == null){
        	model = new TaskSpeedTestModel();
			taskd.setCurrentTaskIdAndSequence(model);
        }
        if(!isNew){
        	taskName.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%")+1, model.getTaskName().toString().trim().length()));
        }else{
        	taskName.setText(model.getTaskName());
        }
		//断开网络配置
        ArrayAdapter<String> disconnectAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_task_disconnect));
        disconnectAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        disConnect.setAdapter(disconnectAdapter);
        
        remoteFile.setSelection( getSelecttion(model.getRemoteFile()));
        repeat.setText(String.valueOf(model.getRepeat()));
        url.setText(model.getUrl());
        interval.setText(String.valueOf(model.getInterVal()));
        disConnect.setSelection(model.getDisConnect());
//        dataConnectType.setSelection(model.getTypeProperty()==4?1:0); //ppp 0 wifi 1
	}

	/**
	 * 保存方法
	 */
	@Override
	public void saveTestTask() {
		if(StringUtil.isEmpty(taskName.getText().toString())){	//任务名为空
			Toast.makeText(TaskSpeedTest.this, R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
			return;
		}else if(StringUtil.isEmpty(repeat.getText().toString()) || "0".equals(repeat.getText().toString().trim())){
			Toast.makeText(TaskSpeedTest.this, R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			return;
		}else if(StringUtil.isEmpty(url.getText().toString())){
			Toast.makeText(TaskSpeedTest.this, R.string.task_alert_nullUrl, Toast.LENGTH_SHORT).show();
			return;
		}else if(StringUtil.isLessThanZeroInteger(interval.getText().toString().trim())){
			Toast.makeText(TaskSpeedTest.this, R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			return;
		}
		else{}
        model.setTaskName(taskName.getText().toString().trim());
		taskName.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%")+1, model.getTaskName().toString().trim().length()));
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(repeat.getText().toString().trim().length()==0?"1":repeat.getText().toString().trim()));
		//model.setUrl(url.getText().toString().trim());
		model.setInterVal(Integer.parseInt(interval.getText().toString().trim()));
		model.setRemoteFile( fileArray[remoteFile.getSelectedItemPosition()].fileName );
		model.setDisConnect(((Long)disConnect.getSelectedItemId()).intValue());
		 model.setRabRelTime(super.rabRelTimeEdt.getText().toString().trim().length() == 0 ? "50" : super.rabRelTimeEdt.getText().toString().trim());
	        model.setRabRuelTime(super.rabAblTimeEdt.getText().toString().trim().length() == 0 ? "12:00" : super.rabAblTimeEdt.getText().toString().trim());
//		if (((Long)dataConnectType.getSelectedItemId()).intValue()==1)  //只有是WIFI的才设置
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
        	if(isNew){
				array.add(array.size(),model);
        	}else{
        		array.remove(taskListId);
        		array.add(taskListId, model);
        	}
        }
		taskd.setTaskListArray(array);
		Toast.makeText(getApplicationContext(),isNew?R.string.task_alert_newSucess:R.string.task_alert_updateSucess,Toast.LENGTH_SHORT).show();
		TaskSpeedTest.this.finish();
	}
	
	
	/**
	 * 点击弹出事件
	 */
	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(TaskSpeedTest.this, TaskSpeedTestServer.class);
			intent.putExtra("Country", model.getCountry());
			intent.putExtra("URL", url.getText().toString());
			intent.putExtra("City", model.getName());
			startActivityForResult(intent, 9999);
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==9999 && resultCode == RESULT_OK){
			String url = data.getStringExtra("URL");
			String country = data.getStringExtra("Country");
			String city = data.getStringExtra("City");
			model.setCountry(country);
			model.setName(city);
			model.setUrl(url);
			this.url.setText(url);
		}
	};
	
	private int getSelecttion(String fileName){
		int result = 9;
		if (fileName != null){
			fileName = fileName.replaceAll("\\*", "x");
			for(int i=0;i<fileArray.length;i++){
				if (fileArray[i].fileName.indexOf(fileName) != -1){
					result = i;
					break;
				}
			}
		}
		return result;
	}
	
	private RemoteFile[] fileArray = new RemoteFile[]{
			new RemoteFile("random350x350.jpg",240),
			new RemoteFile("random500x500.jpg",494),
			new RemoteFile("random750x750.jpg",1092),
			new RemoteFile("random1000x1000.jpg",1940),
			new RemoteFile("random1500x1500.jpg",4364),
			new RemoteFile("random2000x2000.jpg",7723),
			new RemoteFile("random2500x2500.jpg",12118),
			new RemoteFile("random3000x3000.jpg",17400),
			new RemoteFile("random3500x3500.jpg",23694),
			new RemoteFile("random4000x4000.jpg",30885),
	};
	
	private class RemoteFile{
		/**
		 * 文件名
		 */
		private String fileName ;
		/**
		 * 文件大小(KByte)
		 */
		private long fileSize ;
		
		public RemoteFile(String fileName,long fileSize){
			this.fileName = fileName;
			this.fileSize = fileSize;			
		}
		
		@Override
		public String toString(){
			if( fileSize<2048 ){
				return String.format("%s\t(%dKB)", fileName,fileSize);
			}else{
				return String.format("%s\t(%dMB)", fileName,fileSize/1000);
			}
		}
	}
}
