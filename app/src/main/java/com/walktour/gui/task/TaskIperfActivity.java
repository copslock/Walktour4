package com.walktour.gui.task;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkStruct;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.iperf.TaskIperfModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

import java.util.List;

public class TaskIperfActivity  extends BaseTaskActivity {
		TaskListDispose taskd =null;
		TaskIperfModel model =null;
		private int taskListId=-1;
		private boolean isNew=true;
		
		private EditText repeatEditText;
		private EditText taskNameEditText;
		private EditText remoteAddressText;
		private Spinner disConnectSpinner;
		private Spinner  spApSpinner;
		private Spinner  sp_protocol;  //udp or tcp
		private Spinner  sp_direction; //up,down,both
		private EditText telnetAddressText; 
		private EditText telnetPortText;
		private EditText telnetUserText;
		private EditText telnetPwdText;
		private EditText iperfPathText;
		private EditText durationText;
		private EditText bandwidthText; //for udp
		private EditText bufferSizeText; // for udp
		private EditText packetSizeText; //for udp;
		private EditText interValEditText;
//		private Spinner dataConnectType;//数据连接选择：PPP or WIFI
		private LinearLayout udpProperty;
		private TaskRabModel taskRabModel;
		//private boolean isFtpList = false;
		//private int serverPos;
		//private FtpListUtilModel ftpUtil;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {  
	        super.onCreate(savedInstanceState);
	        taskd=TaskListDispose.getInstance();
	       // ftpUtil = FtpListUtilModel.getInstance();
	        Bundle bundle = getIntent().getExtras(); 
	        if(bundle != null && bundle.containsKey("taskListId")){
	        	taskListId = bundle.getInt("taskListId");
	        	//根据标记做并发编辑
	        	if(RABTAG.equals(super.getRabTag())){
	        		for(int i=0;i<taskd.getCurrentTaskList().size();i++){ 
	        				if(taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())){
	            				model =	(TaskIperfModel)((TaskRabModel)(taskd.getCurrentTaskList().get(i))).getTaskModel().get(taskListId);
	            				break;
	            			}
	        		}
	        	}else{
	        		model = (TaskIperfModel)taskd.getTaskListArray().get(taskListId);
	        	}
	        	abstModel = model;
	        	isNew=false;
	        
	        }
	        //this.config_ftp = new ConfigFtp();
	        findView();
	        addEditTextWatcher();
	        
		}
		
		@Override
		protected void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
		}
		
		private void findView(){
		    setContentView(R.layout.task_iperf);
			(initTextView(R.id.title_txt)).setText(R.string.act_task_iperf);//设置标题
			
	        (initImageView(R.id.pointer)).setOnClickListener(this);
	        (initRelativeLayout(R.id.advanced_arrow_rel)).setOnClickListener(this);
			
			TextView tv_taskName = initTextView(R.id.txt_taskname);
			TextView tv_repeat =initTextView(R.id.txt_repeat);
			//final TextView tv_timeOut =initTextView(R.id.txt_timeOut);
//			TextView tv_remoteAddress =initTextView(R.id.txt_remote_address);
//			TextView tv_interVal =initTextView(R.id.txt_interVal);
//			TextView tv_disConnect =initTextView(R.id.txt_disConnect);
//			TextView tv_protocol =initTextView(R.id.txt_protocol);
//			TextView tv_direction =initTextView(R.id.txt_direction);
//			TextView tv_duration =initTextView(R.id.txt_duration);
			
			Button btn_ok = initButton(R.id.btn_ok);
	        Button btn_cencle = initButton(R.id.btn_cencle);
	        
	        tv_taskName.setText(getString(R.string.task_taskName));
	        tv_repeat.setText(getString(R.string.task_repeat));
//	        tv_timeOut.setText(getString(R.string.task_ftpdownload_Duration));
//	        tv_noAnswer.setText(getString(R.string.task_noAnswer));
//	        tv_ftpServer.setText(getString(R.string.task_ftpServer));
//	        tv_remoteFile.setText(getString(R.string.task_remoteFileDown));
//	        tv_threadNumber.setText(getString(R.string.task_threadNumber));
//	        tv_psCall.setText(getString(R.string.task_psCall_download));
//	        tv_interVal.setText(getString(R.string.task_interVal));
//	        tv_disConnect.setText(getString(R.string.task_disConnect));
	        btn_ok.setText(" "+getString(R.string.str_save)+" ");
	        btn_cencle.setText(getString(R.string.str_cancle));
	        
//	    	private EditText remoteAddressText;
//			private Spinner  sp_protocol;  //udp or tcp
//			private Spinner  sp_direction; //up,down,both
//			private EditText telnetAddressText; 
//			private EditText telnetPortText;
//			private EditText telnetUserText;
//			private EditText telnetPwdText;
//			private EditText iperfPathText;
//			private EditText durationText;
//			private EditText bandwidthText; //for udp
//			private EditText bufferSizeText; // for udp
//			private EditText packetSizeText; //for udp;
//			
//	    	private EditText remoteAddressText;
//			private Spinner  sp_protocol;  //udp or tcp
//			private Spinner  sp_direction; //up,down,both
//			private EditText telnetAddressText; 
//			private EditText telnetPortText;
//			private EditText telnetUserText;
//			private EditText telnetPwdText;
//			private EditText iperfPathText;
//			private EditText durationText;
//			private EditText bandwidthText; //for udp
//			private EditText bufferSizeText; // for udp
//			private EditText packetSizeText; //for udp;

			
			taskNameEditText = initEditText(R.id.edit_taskname);
			repeatEditText =initEditText(R.id.edit_repeat);
			remoteAddressText =initEditText(R.id.edit_remote_address);
			telnetAddressText = initEditText(R.id.edit_telnet_address);
			telnetPortText = initEditText(R.id.edit_telnet_port);
			telnetUserText = initEditText(R.id.edit_telnet_user);
			telnetPwdText =  initEditText(R.id.edit_telnet_password);
			iperfPathText =  initEditText(R.id.edit_iperf_path);
			durationText =  initEditText(R.id.edit_duration);
			bandwidthText = initEditText(R.id.edit_bandwidth);
			bufferSizeText = initEditText(R.id.edit_buffer_size);
			packetSizeText = initEditText(R.id.edit_packet_size);
			interValEditText= initEditText(R.id.edit_interval);
			udpProperty = initLinearLayout(R.id.udp_property);
			//final EditText et_cache = initEditText(R.id.edit_cache);
			sp_protocol =initSpinner(R.id.spiner_protocol);
			sp_direction = initSpinner(R.id.spiner_direction);
			disConnectSpinner =initSpinner(R.id.edit_disConnect);
			//final Spinner  et_Capture = initSpinner(R.id.edit_Capture);
			//final Spinner  et_protocol = initSpinner(R.id.edit_Protocol);
			ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
	                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_iperf_protocol));
			modeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
	        sp_protocol.setAdapter(modeAdapter);
			
	        //添加监听事件，若选中项为配置，则跳转到ftp配置页面
	        sp_protocol.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					if(position == 1){ //UDP
						udpProperty.setVisibility(View.VISIBLE);
					}else if (position == 0) { //TCP
						udpProperty.setVisibility(View.GONE);
					}
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}
			});
	        
	        ArrayAdapter<String> directionAdapter = new ArrayAdapter<String>(this,
	                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_iperf_direction));
	        directionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
	        sp_direction.setAdapter(directionAdapter);
			
	        //et_disConnect.setAdapter(adapter);
	        //et_Capture.setAdapter(adapter);

	        //断开网络配置
	        ArrayAdapter<String> disconnect = new ArrayAdapter<String>(this,
	                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_task_disconnect));
	        disconnect.setDropDownViewResource(R.layout.spinner_dropdown_item);
	        disConnectSpinner.setAdapter(disconnect);
	        
	        //连接协议下拉框
	        //ArrayAdapter<String> adpProt = new ArrayAdapter<String>(this,
	        //      android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.array_task_ftp_protocol));
	        //adpProt.setDropDownViewResource(R.layout.spinner_dropdown_item);
	        //et_protocol.setAdapter(adpProt);
	        
	        //添加Net接入点和Wifi接入点
	        TextView tv_ap =initTextView(R.id.txt_ap);
	        tv_ap.setText(getString(R.string.task_accepoint));
	        spApSpinner =initSpinner(R.id.spiner_ap );
	        ArrayAdapter<String> adpAP = new ArrayAdapter<String>(this,
	                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.task_ap) );
	        adpAP.setDropDownViewResource(R.layout.spinner_dropdown_item);
	        spApSpinner.setAdapter( adpAP );
	                      
//	        dataConnectType = initSpinner(R.id.edit_data_connect_type);
//	        super.setDataConnectTypeSP(dataConnectType);
			
	        if(model!=null){
	        	taskNameEditText.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%")+1, model.getTaskName().toString().trim().length()));
		        repeatEditText.setText( String.valueOf(model.getRepeat()));
		        interValEditText.setText(String.valueOf(model.getInterVal()));
		        remoteAddressText.setText( String.valueOf(model.getRemoteAddr()));
		        sp_protocol.setSelection( model.getProtocol() );
		        sp_direction.setSelection( model.getDirection() );
		        telnetAddressText.setText( String.valueOf(model.getTelnetAddr()));
		        telnetPortText.setText( String.valueOf(model.getTelnetPort()));
		        telnetUserText.setText( String.valueOf(model.getUserName()));
		        telnetPwdText.setText( String.valueOf(model.getPassword()));
		        iperfPathText.setText( String.valueOf(model.getIperfPath()));
		        durationText.setText( String.valueOf(model.getDuration()));
		        bandwidthText.setText( String.valueOf(model.getUdpBandWidth()));
		        bufferSizeText.setText( String.valueOf(model.getUdpBuffSize()));
		        packetSizeText.setText( String.valueOf(model.getUdpPacketSize()));
		        disConnectSpinner.setSelection(model.getDisConnect());
//		        dataConnectType.setSelection(model.getTypeProperty()==4?1:0); //ppp 0 wifi 1
		        //et_cache.setText(String.valueOf(model.getCache()));
	        }else{
	        	taskNameEditText.setText("iPerf");
		        repeatEditText.setText("10");
		        remoteAddressText.setText("116.6.50.82");
		        sp_protocol.setSelection(0);
		        sp_direction.setSelection(0);
		        telnetAddressText.setText("116.6.50.82");
		        telnetPortText.setText("23");
		        telnetUserText.setText("root");
		        telnetPwdText.setText("dinglicom");
		        iperfPathText.setText("/usr/bin");
		        durationText.setText("10");
		        bandwidthText.setText("1");
		        bufferSizeText.setText("512");
		        packetSizeText.setText("1400");
		        interValEditText.setText("10");
		        spApSpinner.setSelection( 0 );
		        disConnectSpinner.setSelection(1); //依据新规范，修改拨号规则为每次断开
		       
	        }
	           btn_ok.setOnClickListener(new OnClickListener(){
	        	public void onClick(View v) {
	        	    saveTestTask();
	        	}
	        });
	        
	        btn_cencle.setOnClickListener(new OnClickListener(){
	        	public void onClick(View v){
	        	    finish();
	        	}
	        });

			if(ApplicationModel.getInstance().isNBTest()){//NB测试,隐藏数据连接规则和数据连接类型
				findViewById(R.id.pppconnect_select).setVisibility(View.GONE);
			}
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
	            Toast.makeText(com.walktour.gui.task.TaskIperfActivity.this.getApplicationContext(), R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
	            taskNameEditText.setError(getString(R.string.task_alert_nullName));
	            return;
	        }else if( repeatEditText.getText().toString().trim().equals("0") 
					||  repeatEditText.getText().toString().trim().length()==0){
				Toast.makeText( getApplicationContext(),
						R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
				repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
				return;
			}else if( remoteAddressText.getText().toString().trim().equals("0") ){
				Toast.makeText(TaskIperfActivity.this,
						R.string.task_alert_nullIP, Toast.LENGTH_SHORT).show();
				remoteAddressText.setError(getString(R.string.task_alert_nullIP));
				return;
			}else if( interValEditText.getText().toString().trim().equals("0") ){
				Toast.makeText(TaskIperfActivity.this,
						R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
				interValEditText.setError(getString(R.string.task_alert_nullInterval));
				return;
			}else if( telnetAddressText.getText().toString().trim().equals("0") ){
				Toast.makeText(TaskIperfActivity.this,getString(R.string.task_alert_input)+" "+
						getString( R.string.task_alert_nullIP ), Toast.LENGTH_SHORT).show();
				telnetAddressText.setError(getString(R.string.task_alert_input)+" "+
	                    getString( R.string.task_alert_nullIP ));
				return;
			
	        }
	        if(model==null){
	            model = new TaskIperfModel();
				taskd.setCurrentTaskIdAndSequence(model);
	        }
	        model.setTaskName(taskNameEditText.getText().toString().trim());
	        model.setTaskType(WalkStruct.TaskType.Iperf.name());
	        model.setEnable(1);
	        model.setRepeat(Integer.parseInt( repeatEditText.getText().toString().trim().length()==0?"1":repeatEditText.getText().toString().trim()));
	        model.setTelnetAddress(telnetAddressText.getText().toString().trim().length()==0?"116.6.50.82":telnetAddressText.getText().toString().trim());
	        model.setRemoteAddress(remoteAddressText.getText().toString().trim().length()==0?"116.6.50.82":remoteAddressText.getText().toString().trim());
	        model.setProtocol(sp_protocol.getSelectedItemPosition());
	        model.setDirection(sp_direction.getSelectedItemPosition());
	        model.setDuration(Integer.parseInt( durationText.getText().toString().trim().length()==0?"10":durationText.getText().toString().trim()));
	        model.setTelnetPort(Integer.parseInt( telnetPortText.getText().toString().trim().length()==0?"23":telnetPortText.getText().toString().trim()));
	        model.setUserName(telnetUserText.getText().toString().trim().length()==0?"":telnetUserText.getText().toString().trim());
	        model.setPassword(telnetPwdText.getText().toString().trim().length()==0?"":telnetPwdText.getText().toString().trim());
	        model.setIperfPath(iperfPathText.getText().toString().trim().length()==0?"":iperfPathText.getText().toString().trim());
	        model.setUdpBandWidth(Integer.parseInt( bandwidthText.getText().toString().trim().length()==0?"1":bandwidthText.getText().toString().trim()));
	        model.setUdpBuffSize(Integer.parseInt( bufferSizeText.getText().toString().trim().length()==0?"512":bufferSizeText.getText().toString().trim()));
	        model.setUdpPacketSize(Integer.parseInt( packetSizeText.getText().toString().trim().length()==0?"1400":packetSizeText.getText().toString().trim()));
	        
	      //  model.setTimeOut(Integer.parseInt( timeOutEditText.getText().toString().trim().length()==0?"90":timeOutEditText.getText().toString().trim()));
	      //  model.setNoAnswer(Integer.parseInt( noAnswerEditText.getText().toString().trim().length()==0?"20":noAnswerEditText.getText().toString().trim()));
	        model.setInterVal(Integer.parseInt( interValEditText.getText().toString().trim().length()==0?"10":interValEditText.getText().toString().trim()));
	      //  model.setFtpServer(config_ftp.getNameFirstEmpty( et_ftpServer.getSelectedItemPosition() ,TaskIperfActivity.this) );
	      //  model.setRemoteFile(remoteFileEditText.getText().toString().trim().length()==0?"":remoteFileEditText.getText().toString().trim());
	       // model.setThreadNumber(Integer.parseInt(threadNumberEditText.getText().toString().trim().length()==0?"1":threadNumberEditText.getText().toString().trim()));
	        model.setDisConnect(disConnectSpinner.getSelectedItemPosition());
	       // model.setPsCall(psCallSpinner.getSelectedItemPosition());
	      //  model.setAccessPoint( spApSpinner.getSelectedItemPosition()  );
	       // model.setPppfaildtimes(Integer.parseInt(ppptimesEditText.getText().toString().trim().length() == 0 ? "3" : ppptimesEditText.getText().toString().trim()));
	       // model.setPppInterval(Integer.parseInt(pppintervalEditText.getText().toString().trim().length() == 0 ? "3" : pppintervalEditText.getText().toString().trim()));
	       // model.setLoginTimes(Integer.parseInt(logintimesEditText.getText().toString().trim().length() == 0 ? "3" : logintimesEditText.getText().toString().trim()));
	       // model.setLoginInterval(Integer.parseInt(loginintervalEditText.getText().toString().trim().length() == 0 ? "3" : loginintervalEditText.getText().toString().trim()));
	       // model.setLoginTimeOut(Integer.parseInt(loginTimeoutEditText.getText().toString().trim().length() == 0 ? "60" : loginTimeoutEditText.getText().toString().trim()));
	        /*model.setTcpipCapture(et_Capture.getSelectedItemPosition());
	        model.setProtocol(et_protocol.getSelectedItemPosition());
	        model.setCache(Integer.parseInt(et_cache.getText().toString().trim().length() == 0 ? "1000" : et_cache.getText().toString().trim()));
	        */
//	        if (((Long)dataConnectType.getSelectedItemId()).intValue()==1)  //只有是WIFI的才设置
//				model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);  //
//			else 
//				model.setTypeProperty(WalkCommonPara.TypeProperty_Net);  //ppp

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
					array.add(array.size(), model);
				} else {
					array.remove(taskListId);
					array.add(taskListId, model);
				}
			}
	        taskd.setTaskListArray(array);
	        
	        Toast.makeText(getApplicationContext(),isNew?R.string.task_alert_newSucess:R.string.task_alert_updateSucess,Toast.LENGTH_SHORT).show();
	        TaskIperfActivity.this.finish();
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
	        
	        remoteAddressText.addTextChangedListener(new EditTextWatcher() {
	            @Override
	            public void afterTextChanged(Editable s) {
	                if(remoteAddressText.getText().toString().trim().length() == 0){
	                	remoteAddressText.setError(getString(R.string.task_alert_nullIP));
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
	        
	        telnetAddressText.addTextChangedListener(new EditTextWatcher() {
	            @Override
	            public void afterTextChanged(Editable s) {
	                if( telnetAddressText.getText().toString().trim().length() == 0 ){
	                	remoteAddressText.setError(getString(R.string.task_alert_nullIP));
	                    return;
	                }
	            }
	        });
	        
	   
	    }
//
//		@Override
//		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//			// TODO Auto-generated method stub
//			super.onActivityResult(requestCode, resultCode, data);
//			
//			if(requestCode == 55 && resultCode == RESULT_OK){
//					Log.i("ftplist", "返回OK");
//					serverPos = data.getExtras().getInt("POS");
//					isFtpList = true;
//					String remoteStr = data.getExtras().getString("path");
//					if( remoteStr != null){
//						remoteFileEditText.setText(remoteStr);
//					}
//			}
//				
//		}
//	    
	    
	    
//	}



}
