/**
 * 
 */
package com.walktour.gui.task;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.videostreaming.TaskStreamModel;

import java.util.List;

/**
 * @author jone
 * 流媒体
 */
public class TaskStreaming  extends BaseTaskActivity{
	TaskListDispose taskd =null;
	TaskStreamModel model =null;
	private int taskListId=-1;
	private boolean isNew=true;
	
	private EditText taskName;   //任务名称
	private EditText repeat;    //重复次数
	private Spinner videoType;
	private Spinner videoQuality;
	private EditText url;       //URL
	private Spinner playMode; //播放模式
	private EditText playTime; //播放时长
	private TextView txtPlayTime;
	private Spinner proType;    //连接协议
	
	//高级属性部分
	private EditText interval;//间隔时长
	private EditText noDataTimeout;//无流量超时
	private EditText bufferTotal;//缓存大小
	private EditText bufferPlay;
	private CheckBox saveVideo;
	private Spinner disConnect;//拨号规则
//	private Spinner dataConnectType;//数据连接选择：PPP or WIFI
	private Button save;
	private Button canel;
	
	private TaskRabModel taskRabModel;  //并发对象
	private CheckBox showVideo;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.task_streaming);
		(initTextView(R.id.title_txt)).setText(getResources().getString(R.string.act_task_stream));
		(initImageView(R.id.pointer)).setOnClickListener(this);
		taskd=TaskListDispose.getInstance();
        Bundle bundle = getIntent().getExtras(); 
        if(bundle != null && bundle.containsKey("taskListId")){
         	taskListId = bundle.getInt("taskListId");
         	if(RABTAG.equals(super.getRabTag())){
        		for(int i=0;i<taskd.getCurrentTaskList().size();i++){ 
        				if(taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())){
            				model =	(TaskStreamModel)((TaskRabModel)(taskd.getCurrentTaskList().get(i))).getTaskModel().get(taskListId);
            				break;
            			}
        		}
        	}else{
         		model = (TaskStreamModel)taskd.getTaskListArray().get(taskListId);
         	}
         	abstModel = model;
         	isNew=false;
         }
        
        showView();
        
        //可编辑框内容改变监听
        addEditTextWatcher();
	}
	
	/**
	 * 显示界面的一些设置
	 */
	private void showView(){
		save = initButton(R.id.btn_ok);
		canel = initButton(R.id.btn_cencle);
		
		((RelativeLayout)findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);
		save.setOnClickListener(this);
		canel.setOnClickListener(this);
		
		taskName = initEditText(R.id.edit_taskname);
		repeat = initEditText(R.id.edit_repeat);
		videoType = (Spinner)findViewById(R.id.edit_video_type);
		ArrayAdapter<String> videoTypeAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.stream_video_type));
		videoTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        videoType.setAdapter(videoTypeAdapter);
		
		
		videoQuality = (Spinner)findViewById(R.id.edit_video_quality);
		ArrayAdapter<String> videoQualityAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.stream_video_quality));
		videoQualityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		videoQuality.setAdapter(videoQualityAdapter);
		
		
		playMode = (Spinner)findViewById(R.id.edit_play_mode);
		ArrayAdapter<String> playModeAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_stream_play_model));
		playModeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		playMode.setAdapter(playModeAdapter);
		
		
		playTime = initEditText(R.id.edit_play_time);
		url = initEditText(R.id.edit_url);
		proType = (Spinner)findViewById(R.id.edit_play_protocol);
		ArrayAdapter<String> proTypeAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_stream_protocol));
		proTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		proType.setAdapter(proTypeAdapter);
		
		
		interval = initEditText(R.id.edit_interval);
		noDataTimeout = initEditText(R.id.edit_no_data_timeout);
		bufferTotal = initEditText(R.id.edit_buffertime);
		bufferPlay = initEditText(R.id.edit_play_thres);
		saveVideo = (CheckBox)findViewById(R.id.edit_save_video);
		showVideo = (CheckBox)findViewById(R.id.edit_show_video);
//		dataConnectType = (Spinner)findViewById(R.id.edit_data_connect_type);
//		super.setDataConnectTypeSP(dataConnectType);
		disConnect = (Spinner)findViewById(R.id.edit_disConnect);
        
        
		ArrayAdapter<String> disconnectAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_task_disconnect));
        disconnectAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        disConnect.setAdapter(disconnectAdapter);
		
		txtPlayTime = initTextView(R.id.txt_play_time);
		
		//如果是对象编辑
		if(model == null){
			model = new TaskStreamModel();
			taskd.setCurrentTaskIdAndSequence(model);
		}
		
		if(!isNew){
			taskName.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%")+1, model.getTaskName().toString().trim().length()));
		}else{
			taskName.setText(model.getTaskName());
		}
		
		repeat.setText(String.valueOf(model.getRepeat()));
		
		playMode.setOnItemSelectedListener(selectChangeListener);
		
		//如果是 ps class模式则选中按时间，否则按大小
		playMode.setSelection(model.isPsCall() ? 1 : 0);
		
		videoType.setSelection(model.getVideoType());
		videoQuality.setSelection(model.getVideoQuality());
		url.setText(model.getmURL());
		playTime.setText(model.getmPlayTime());
		proType.setSelection(model.ismUseTCP() ? 0 : 1);
		
		interval.setText(String.valueOf(model.getInterVal()));
		noDataTimeout.setText(model.getmNodataTimeout());
		bufferTotal.setText(model.getBufferTime());
		bufferPlay.setText(model.getBufferPlay());
		saveVideo.setChecked(model.isSaveVideo());
		showVideo.setChecked(model.isShowVideo());
		disConnect.setSelection(model.getDisConnect());
//		dataConnectType.setSelection(model.getTypeProperty()==4?1:0); //ppp 0 wifi 1
		 /**
         * 文件选择模式
         */
//		dataConnectType.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				// TODO Auto-generated method stub
////				if(arg2==0){ //
////					findViewById(R.id.wifi_ap_id).setVisibility(View.GONE);
////					findViewById(R.id.wifi_ap_name).setVisibility(View.GONE);
////				
////				}else{
////					findViewById(R.id.wifi_ap_id).setVisibility(View.VISIBLE);
////					findViewById(R.id.wifi_ap_name).setVisibility(View.VISIBLE);
////				}
//			}

//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		
//        });
	}
	
	/**
	 * PS Call选择改变事件
	 */
	private OnItemSelectedListener selectChangeListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> adapter, View view, int position,
				long index) {
			if(position==0){
				txtPlayTime.setText(getResources().getString(R.string.stream_play_time));
			}else{
				txtPlayTime.setText(getResources().getString(R.string.stream_play_timeout));
			}
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};
	
	@SuppressLint("StringFormatMatches")
	@Override
	public void saveTestTask() {
		if(StringUtil.isEmpty(taskName.getText().toString())){	//任务名为空
			Toast.makeText(TaskStreaming.this.getApplicationContext(), R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
			return;
		}else if(StringUtil.isEmpty(repeat.getText().toString()) ||
					"0".equals(repeat.getText().toString().trim())){
			Toast.makeText( getApplicationContext(),
					R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			return;
		}else if(StringUtil.isLessThanZeroInteger(playTime.getText().toString().trim())){
			Toast.makeText( getApplicationContext(),
					playMode.getSelectedItemId()==0?R.string.alert_stream_play_timeout_null:R.string.alert_stream_play_time_null, 
					Toast.LENGTH_SHORT).show();
			return;
		}else if(StringUtil.isEmpty(url.getText().toString())){		//FTP服务器为空
			Toast.makeText(TaskStreaming.this.getApplicationContext(), R.string.task_alert_nullUrl, Toast.LENGTH_SHORT).show();
			return;
		}else if(StringUtil.isLessThanZeroInteger(interval.getText().toString().trim())){
			Toast.makeText(TaskStreaming.this.getApplicationContext(), R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			return;
		}else if(!StringUtil.isNumeric(noDataTimeout.getText().toString())){
			Toast.makeText(TaskStreaming.this.getApplicationContext(), 
					R.string.task_alert_nullnodatatimeout, Toast.LENGTH_SHORT).show();
			return;
		}else if(Integer.parseInt(noDataTimeout.getText().toString())< 5 || Integer.parseInt(noDataTimeout.getText().toString()) > 9999){
			Toast.makeText(TaskStreaming.this.getApplicationContext(), 
					String.format(getString(R.string.field_alert_input_range), getString(R.string.stream_no_data_timeout), 5, 9999), 
					Toast.LENGTH_SHORT).show();
			return;
		}else{}
		
		 //依据标记区分用户名的编辑
        model.setTaskName(taskName.getText().toString().trim());
		model.setTaskType(WalkStruct.TaskType.Stream.name());
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(repeat.getText().toString().trim().length()==0?"1":repeat.getText().toString().trim()));
		model.setVideoType(videoType.getSelectedItemPosition());
		model.setVideoQuality(videoQuality.getSelectedItemPosition());
		model.setmURL(url.getText().toString().trim());
		model.setPsCall(playMode.getSelectedItemId()==1);
		model.setmPlayTime(playTime.getText().toString().trim());
		model.setmUseTCP(proType.getSelectedItemId()==0);
		
		
		model.setInterVal(Integer.parseInt(interval.getText().toString().trim()));
		model.setmNodataTimeout(noDataTimeout.getText().toString().trim());
		model.setBufferTime(bufferTotal.getText().toString());
		model.setBufferPlay(bufferPlay.getText().toString());
		model.setSaveVideo(saveVideo.isChecked());
		model.setShowVideo(showVideo.isChecked());
		model.setDisConnect(((Long)disConnect.getSelectedItemId()).intValue());
//		if (((Long)dataConnectType.getSelectedItemId()).intValue()==1)  //只有是WIFI的才设置
//			model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);  //
//		else 
//			model.setTypeProperty(WalkCommonPara.TypeProperty_Net);  //ppp

		List<TaskModel> array = taskd.getTaskListArray();
		if(RABTAG.equals(super.getRabTag())){//依标志区分并发与普通业务
        	for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
        		if(super.getMultiRabName().equals(taskd.getCurrentTaskList().get(i).getTaskName())){
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
		TaskStreaming.this.finish();
	}
	
	/**
     * 添加EditText输入监听限制<BR>
     */
    public void addEditTextWatcher(){
    	taskName.addTextChangedListener(etWatcher);
    	repeat.addTextChangedListener(etWatcher);
    	playTime.addTextChangedListener(etWatcher);
    	url.addTextChangedListener(etWatcher);
    	interval.addTextChangedListener(etWatcher);
    	noDataTimeout.addTextChangedListener(etWatcher);
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
			}else if(view == playTime && 
					StringUtil.isLessThanZeroInteger(
							playTime.getText().toString().trim())){
				playTime.setError(getString(playMode.getSelectedItemId()==0?
						R.string.alert_stream_play_timeout_null:R.string.alert_stream_play_time_null));
			}else if(view == url){
				if(s.length() < 1)
					url.setError(getString(R.string.task_alert_nullUrl));
				else
					url.setError(null);
			}else if(view == interval && StringUtil.isLessThanZeroInteger(
					interval.getText().toString().trim())){
				interval.setError(getString(R.string.task_alert_nullInterval));
			}else if(view == noDataTimeout && StringUtil.isLessThanZeroInteger(
					noDataTimeout.getText().toString().trim())){
				noDataTimeout.setError(getString(R.string.task_alert_nullnodatatimeout));
			}
    	};
    };

}
