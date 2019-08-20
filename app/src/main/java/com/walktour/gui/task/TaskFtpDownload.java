package com.walktour.gui.task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.control.config.ConfigFtp;
import com.walktour.framework.view.DateTimePickDialogUtil;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.setting.Sys;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.ftp.TaskFtpModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.model.FtpListUtilModel;

import java.util.ArrayList;
import java.util.List;

 
public class TaskFtpDownload extends BaseTaskActivity {
	TaskListDispose taskd =null;
	TaskFtpModel model =null;
	private int taskListId=-1;
	private boolean isNew=true;
	private ConfigFtp config_ftp;
	private ArrayAdapter<String> ftpadapter;
	private Spinner  et_ftpServer;
	private Spinner  ed_transfer_protocal;
	private Spinner  ed_thread_mode;
    private EditText taskNameEditText;
    private EditText repeatEditText;
    private EditText timeOutEditText;
    private EditText noAnswerEditText;
    private EditText interValEditText;
    private EditText threadNumberEditText;
    private EditText ppptimesEditText;
    private EditText pppintervalEditText;
    private EditText logintimesEditText;
    private EditText loginintervalEditText;
    private EditText loginTimeoutEditText;
    private EditText sendBufferText;
    private EditText receiveBufferText;
    private Spinner  psCallSpinner;
    private Spinner  disConnectSpinner;
    private Button btn_view;  //浏览目录
    private Spinner spApSpinner;
	private TaskRabModel taskRabModel;
	private Spinner dataConnectType;//数据连接选择：PPP or WIFI
	private boolean isFtpList = false;
	private int serverPos;
	private FtpListUtilModel ftpUtil;
    private CustomAutoCompleteTextView remoteFileEditText;
	/**
	 * 保存历史记录
	 */
	private SaveHistoryShare historyShare;   
	
	/**
	 * 历史记录集合
	 */
	private ArrayList<String> mOriginalValues = new ArrayList<String>();

	private Spinner spnMaxThr;
	/**是否来自工单界面*/
	private boolean isFromWorkOrder=false;
	/**上下文*/
	private Context context=TaskFtpDownload.this;
	private RelativeLayout rab_time_layout;
	private RelativeLayout rab_rule_time_layout;
	private LinearLayout wifiTestLayout ;
	private RelativeLayout userNameLayout;
	private Button wifiSSIDET ;
	private EditText wifiUserET ;
	private EditText wifiPasswordET ; 
	private LayoutInflater inflater;
	@Override
	public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(this);
        this.config_ftp = new ConfigFtp(); 
        taskd=TaskListDispose.getInstance();
        ftpUtil = FtpListUtilModel.getInstance();
        Bundle bundle = getIntent().getExtras(); 
        if(null!=bundle){
        	this.isFromWorkOrder=bundle.getBoolean("fromWorkOrder");
        }
        if(bundle != null && bundle.containsKey("taskListId")){
        	taskListId = bundle.getInt("taskListId");
        	//根据标记做并发编辑
        	if(RABTAG.equals(super.getRabTag())){
        		for(int i=0;i<taskd.getCurrentTaskList().size();i++){ 
        				if(taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())){
            				model =	(TaskFtpModel)((TaskRabModel)(taskd.getCurrentTaskList().get(i))).getTaskModel().get(taskListId);
            				break;
            			}
        		}
        	}else{
        		model = (TaskFtpModel)taskd.getTaskListArray().get(taskListId);
        	}
        	abstModel = model;
        	isNew=false;
        
        }
        //this.config_ftp = new ConfigFtp();
        findView();
        historyShare=new SaveHistoryShare(getApplicationContext());
        historyShare.getHistoryDataFromSP(this.getPackageName(), "FtpDPath", mOriginalValues);
        historyShare.initAutoComplete(remoteFileEditText);
        addEditTextWatcher();
        
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		this.config_ftp = new ConfigFtp();
		ftpadapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_custom_layout, config_ftp.getAllFtpNamesFirstEmpty(TaskFtpDownload.this));
		ftpadapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		et_ftpServer.setAdapter(ftpadapter);
		
		/**
		 * TODO 此处可以再进一步处理为如果点配置页面返回的，带回最后一次修改或增加的FTP服务器
		 */
		if(!isFtpList){
			if(model != null){
				et_ftpServer.setSelection(config_ftp.getPositonFirstEmpty(model.getFtpServerName()));
			}else if(ftpadapter.getCount()>2){
				et_ftpServer.setSelection(1);
			}
		}else{
			et_ftpServer.setSelection(serverPos);
		}
        //添加监听事件，若选中项为配置，则跳转到ftp配置页面
        et_ftpServer.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == parent.getCount()-1){
					Intent it = new Intent(TaskFtpDownload.this, Sys.class);
					it.putExtra(Sys.CURRENTTAB, 4);
					startActivity(it);	
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	
	private void findView(){
	    setContentView(R.layout.task_ftpdownload);
		(initTextView(R.id.title_txt)).setText(R.string.act_task_ftpdownload);//设置标题
		
        (initImageView(R.id.pointer)).setOnClickListener(this);
        (initRelativeLayout(R.id.advanced_arrow_rel)).setOnClickListener(this);
		
		TextView tv_taskName = initTextView(R.id.txt_taskname);
		TextView tv_repeat =initTextView(R.id.txt_repeat);
		final TextView tv_timeOut =initTextView(R.id.txt_timeOut);
		TextView tv_noAnswer =initTextView(R.id.txt_noAnswer);
		TextView tv_interVal =initTextView(R.id.txt_interVal);
		TextView tv_disConnect =initTextView(R.id.txt_disConnect);
		TextView tv_ftpServer =initTextView(R.id.txt_ftpServer);
		TextView tv_remoteFile =initTextView(R.id.txt_remoteFile);
		TextView tv_threadNumber =initTextView(R.id.txt_threadNumber);
		TextView tv_psCall =initTextView(R.id.txt_psCall);
		
		Button btn_ok = initButton(R.id.btn_ok);
        Button btn_cencle = initButton(R.id.btn_cencle);
        
        tv_taskName.setText(getString(R.string.task_taskName));
        tv_repeat.setText(getString(R.string.task_repeat));
        tv_timeOut.setText(getString(R.string.task_ftpdownload_Duration));
        tv_noAnswer.setText(getString(R.string.task_noAnswer));
        tv_ftpServer.setText(getString(R.string.task_ftpServer));
        tv_remoteFile.setText(getString(R.string.task_remoteFileDown));
        tv_threadNumber.setText(getString(R.string.task_threadNumber));
        tv_psCall.setText(getString(R.string.task_psCall_download));
        tv_interVal.setText(getString(R.string.task_interVal));
        tv_disConnect.setText(getString(R.string.task_disConnect));
        btn_ok.setText(" "+getString(R.string.str_save)+" ");
        btn_cencle.setText(getString(R.string.str_cancle));
        sendBufferText=initEditText(R.id.edit_sendbuffer);
        receiveBufferText=initEditText(R.id.edit_receivebuffer);
		taskNameEditText = initEditText(R.id.edit_taskname);
		repeatEditText =initEditText(R.id.edit_repeat);
		timeOutEditText =initEditText(R.id.edit_timeOut);
		noAnswerEditText =initEditText(R.id.edit_noAnswer);
		interValEditText =initEditText(R.id.edit_interVal);
		et_ftpServer =initSpinner(R.id.edit_ftpServer);
		ed_transfer_protocal=initSpinner(R.id.edit_transfer_protocal);
		ed_thread_mode=initSpinner(R.id.spn_thread_mode);
		remoteFileEditText =(CustomAutoCompleteTextView) findViewById(R.id.edit_remoteFile);
		remoteFileEditText.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == arg0.getCount() - 1){
					historyShare.clearData();
					remoteFileEditText.setText("");
				}
			}
		});
		btn_view =initButton(R.id.btn_view);
		btn_view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int ftpPos=et_ftpServer.getSelectedItemPosition();
				if(ftpPos!=0 ){
					if(ed_transfer_protocal.getSelectedItemPosition()==0) {
						Intent intent = new Intent(getApplicationContext(), FtpListActivity.class);
						ftpUtil.setServerPosition(ftpPos);
						ftpUtil.setDlOrUl(1);  //设置为1代表是ftpDownload
//					intent.putExtra("POSITION", ftpPos);
						startActivityForResult(intent, 55);
					}else{
						Intent intent = new Intent(getApplicationContext(), SFtpListActivity.class);
						ftpUtil.setServerPosition(ftpPos);
						ftpUtil.setDlOrUl(1);  //设置为1代表是ftpDownload
//					intent.putExtra("POSITION", ftpPos);
						startActivityForResult(intent, 55);
					}
				}else {
					Toast.makeText(getApplicationContext(), getString(R.string.task_ftp_select_non), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
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
        				TaskFtpDownload.this, rabAblTimeEdt.getText().toString());
        		dateTimePicKDialog.dateTimePicKDialog(rabAblTimeEdt);
        	}
        });
		threadNumberEditText =initEditText(R.id.edit_threadNumber);
		ppptimesEditText = initEditText(R.id.edit_pppFaildTimes);
		pppintervalEditText = initEditText(R.id.edit_pppInterval);
		logintimesEditText = initEditText(R.id.edit_loginTimes);
		loginintervalEditText = initEditText(R.id.edit_loginInterval);
		loginTimeoutEditText	= initEditText(R.id.edit_loginTimeOut);
		//final EditText et_cache = initEditText(R.id.edit_cache);
		psCallSpinner =initSpinner(R.id.edit_psCall);
		disConnectSpinner =initSpinner(R.id.edit_disConnect);
		//final Spinner  et_Capture = initSpinner(R.id.edit_Capture);
		//final Spinner  et_protocol = initSpinner(R.id.edit_Protocol);
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_ftpdownload_testmode));
		modeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        
        psCallSpinner.setAdapter(modeAdapter);
        //et_disConnect.setAdapter(adapter);
        //et_Capture.setAdapter(adapter);
		ArrayAdapter<String> protocalAdapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_ftp_transfer_protocal));
		protocalAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		ed_transfer_protocal.setAdapter(protocalAdapter);


		ArrayAdapter<String> threadModeAdapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.thread_mode_select));
		threadModeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		ed_thread_mode.setAdapter(threadModeAdapter);
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
                      
        dataConnectType = initSpinner(R.id.edit_data_connect_type);
        setDataConnectTypeSP(dataConnectType);
        
        spnMaxThr = initSpinner(R.id.spn_max_thr);
        ArrayAdapter<String> spnMaxThrArray = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.max_up_down_array) );
        spnMaxThrArray.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spnMaxThr.setAdapter(spnMaxThrArray);
		
        // 添加Wifi信息,如果所有业务都有wifi测试,那么这段代码可以放入父类中
 		wifiTestLayout = initLinearLayout(R.id.task_wifitest_layout); 
 		userNameLayout = initRelativeLayout(R.id.usernamelayout);
 		wifiSSIDET = initButton(R.id.wifitestssid);
 		wifiUserET = initEditText(R.id.wifitestuser);
 		wifiPasswordET = initEditText(R.id.wifitestpassword);
 		wifiSSIDET.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				if (null != wifiManager) {
					List<ScanResult> list = wifiManager.getScanResults();
					if (null != list && list.size() > 0) {
						ApSelectAdapter adapter=new ApSelectAdapter(list);
						openDialog(adapter,list,-1, wifiSSIDET);
					}else{
						ToastUtil.showToastShort(context,getString(R.string.sys_wifi_aplist)+"");
					}
				}
				
			}
		}); 
        if(model!=null){
        	taskNameEditText.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%")+1, model.getTaskName().toString().trim().length()));
	        repeatEditText.setText( String.valueOf(model.getRepeat()));
	        timeOutEditText.setText(String.valueOf(model.getTimeOut()));
	        noAnswerEditText.setText(String.valueOf(model.getNoAnswer()));
	        interValEditText.setText(String.valueOf(model.getInterVal()));
	        spApSpinner.setSelection( model.getAccessPoint() );
	        et_ftpServer.setSelection(config_ftp.getPositonFirstEmpty( model.getFtpServerName() ));
	        remoteFileEditText.setText( String.valueOf(model.getRemoteFile()));
	        threadNumberEditText.setText( String.valueOf(model.getThreadNumber()));
			ed_transfer_protocal.setSelection(model.getTransportProtocal());
	        disConnectSpinner.setSelection(model.getDisConnect());
	        psCallSpinner.setSelection(model.getPsCall());
	        //et_Capture.setSelection(model.getTcpipCapture());
	       // et_protocol.setSelection(model.getProtocol());
	        ppptimesEditText.setText(String.valueOf(model.getPppfaildtimes()));
	        pppintervalEditText.setText(String.valueOf(model.getPppInterval()));
	        logintimesEditText.setText(String.valueOf(model.getLoginTimes()));
	        loginintervalEditText.setText(String.valueOf(model.getLoginInterval()));
	        loginTimeoutEditText.setText(String.valueOf(model.getLoginTimeOut()));
	        dataConnectType.setSelection(model.getTypeProperty()==4?1:0); //ppp 0 wifi 1
	        spnMaxThr.setSelection(model.getMaxThr());
	        sendBufferText.setText(model.getSendBuffer()+"");
	        receiveBufferText.setText(model.getReceiveBuffer()+"");
			ed_thread_mode.setSelection(Integer.parseInt(model.getThreadMode()));
        }else{
        	taskNameEditText.setText("FTP Download");
	        repeatEditText.setText("10");
	        timeOutEditText.setText("300");
	        noAnswerEditText.setText("60");
	        interValEditText.setText("15");
	        threadNumberEditText.setText("3");
	        spApSpinner.setSelection( 0 );
	        psCallSpinner.setSelection(1);
	        disConnectSpinner.setSelection(1); //依据新规范，修改拨号规则为每次断开
	        /*et_Capture.setSelection(0);
	        et_protocol.setSelection(0);
	        et_cache.setText("1000");*/
	        ppptimesEditText.setText("3");
	        pppintervalEditText.setText("15");
	        logintimesEditText.setText("3");
	        loginintervalEditText.setText("15");
	        loginTimeoutEditText.setText("60");
			ed_transfer_protocal.setSelection(0);
			ed_thread_mode.setSelection(0);
        }
        psCallSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(arg2==0){
					tv_timeOut.setText(getResources().getString(R.string.task_ftpdownload_timeout));
				}else{
					tv_timeOut.setText(getResources().getString(R.string.task_ftpdownload_Duration));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
        //数据连接类型
        dataConnectType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position==0||position==2){//PPP测试
					wifiTestLayout.setVisibility(View.GONE);
				}else{//wifi测试
					wifiTestLayout.setVisibility(View.VISIBLE);
					if (null != model) {
						String[] params = model.getNetworkConnectionSetting().getWifiParam();
						String apName = params[0];
						wifiSSIDET.setText(apName);
						if (apName.equals("ChinaNet") || apName.equals("ChinaUnicom") || apName.contains("CMCC-WEB")
								|| apName.contains("CMCC")) {
							userNameLayout.setVisibility(View.VISIBLE);
						}
						wifiUserET.setText(params[1]);
						wifiPasswordET.setText(params[2]);
					}
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        btn_ok.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		if(isFromWorkOrder){//来自工单不允许修改,只可查看
        			ToastUtil.showToastShort(context, "工单任务只可以查看,不允许修改.");
        			TaskFtpDownload.this.finish();
        			return;
        		}
        	    saveTestTask();
        	}
        });
        
        btn_cencle.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        	    TaskFtpDownload.this.finish();
        	}
        });
        
       
        if (null != model) {
 			if (model.getTypeProperty() == WalkCommonPara.TypeProperty_Wlan || model.getNetworkConnectionSetting().isConnectionUseWifi()) { 
 				dataConnectType.setSelection(1);
 				wifiTestLayout.setVisibility(View.VISIBLE);
 				String[] params=model.getNetworkConnectionSetting().getWifiParam();
 				String apName=params[0];
 				wifiSSIDET.setText(apName);
 				if(apName.equals("ChinaNet")||apName.equals("ChinaUnicom")||apName.contains("CMCC-WEB")||apName.contains("CMCC")){
					userNameLayout.setVisibility(View.VISIBLE);
				}
				wifiUserET.setText(params[1]);
				wifiPasswordET.setText(params[2]);
 			} else if(model.getTypeProperty() == WalkCommonPara.TypeProperty_Ppp){
				dataConnectType.setSelection(2);
				wifiTestLayout.setVisibility(View.GONE);
			}else {
				dataConnectType.setSelection(0);
				wifiTestLayout.setVisibility(View.GONE);
			}
 		}
	}
    private void setDataConnectTypeSP(Spinner dataConnectTypeSP){
        if(dataConnectTypeSP != null){
            if(showInfoList.contains(WalkStruct.ShowInfoType.WLAN)){
                ArrayAdapter<String> dataConnectTypeAdapter = new ArrayAdapter<String>(this,
                        R.layout.simple_spinner_custom_layout,
						ApplicationModel.getInstance().getConnectType());
                dataConnectTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                dataConnectTypeSP.setAdapter(dataConnectTypeAdapter);
            }else {
                findViewById(R.id.task_wifi_app_choice).setVisibility(View.GONE);
                
                findViewById(R.id.task_wifi_test_choice).setVisibility(View.GONE);
            }
            

        }
	}
	@Override 
	public void onDestroy(){
		 super.onDestroy(); 
	}
	private final class ApItem{
		public TextView wifiAP;
		public TextView wifiStrength;
	}
	private class ApSelectAdapter extends BaseAdapter{
		private List<ScanResult> listSR;
		
		public ApSelectAdapter(List<ScanResult> listSR) {
			super();
			this.listSR = listSR;
		}

		@Override
		public int getCount() { 
			return listSR==null?0:listSR.size();
		}

		@Override
		public Object getItem(int position) {
			return listSR.get(position);
		}

		@Override
		public long getItemId(int position) { 
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ScanResult model = listSR.get(position);
			ApItem itemView = null;
			if (convertView == null) {
				itemView = new ApItem();
				convertView = inflater.inflate(R.layout.task_ap_select_item, parent, false);
				itemView.wifiAP = (TextView) convertView.findViewById(R.id.wlanapname);
				itemView.wifiStrength = (TextView) convertView.findViewById(R.id.wlanapsinglestrength);
				convertView.setTag(itemView);
			} else {
				itemView = (ApItem) convertView.getTag();
			}
			itemView.wifiAP.setText(model.SSID);
			itemView.wifiStrength.setText(model.level+"dbm");
			return convertView;
		}
	}
	private void openDialog(final BaseAdapter adapter,final List<ScanResult> listSR,final int checkedItem, final Button button) {
 		AlertDialog.Builder builder = new AlertDialog.Builder(this);
 		builder.setTitle(getString(R.string.sys_wifi_selectap)+""); 
		builder.setSingleChoiceItems(adapter, checkedItem, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ScanResult sr=listSR.get(which);
				button.setText(sr.SSID);
				if(sr.SSID.equals("ChinaNet")||sr.SSID.equals("ChinaUnicom")||sr.SSID.contains("CMCC-WEB")||sr.SSID.contains("CMCC")){
					userNameLayout.setVisibility(View.VISIBLE);
				}else{
					userNameLayout.setVisibility(View.GONE);
				}
				dialog.dismiss();
			}
		});
	 
		builder.show();

	}
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see com.walktour.gui.task.BaseTaskActivity#saveTestTask()
     */
    
    @Override
    public void saveTestTask() {
    	if(isFromWorkOrder){//来自工单不允许修改,只可查看
			ToastUtil.showToastShort(context, "工单任务只可以查看,不允许修改.");
			TaskFtpDownload.this.finish();
			return;
		}
        if(taskNameEditText.getText().toString().trim().length()==0){    //任务名为空
            Toast.makeText(com.walktour.gui.task.TaskFtpDownload.this.getApplicationContext(), R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
            taskNameEditText.setError(getString(R.string.task_alert_nullName));
            return;
        }else if( repeatEditText.getText().toString().trim().equals("0") 
				||  repeatEditText.getText().toString().trim().length()==0){
			Toast.makeText( getApplicationContext(),
					R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
			return;
		}else if( timeOutEditText.getText().toString().trim().equals("0") ){
			Toast.makeText(TaskFtpDownload.this,
					R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			timeOutEditText.setError(getString(R.string.task_alert_nullTimeout));
			return;
		}else if( interValEditText.getText().toString().trim().equals("0") ){
			Toast.makeText(TaskFtpDownload.this,
					R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			interValEditText.setError(getString(R.string.task_alert_nullInterval));
			return;
		}else if( noAnswerEditText.getText().toString().trim().equals("0") ){
			Toast.makeText(TaskFtpDownload.this,getString(R.string.task_alert_input)+" "+
					getString( R.string.task_noAnswer ), Toast.LENGTH_SHORT).show();
            noAnswerEditText.setError(getString(R.string.task_alert_input)+" "+
                    getString( R.string.task_noAnswer ));
			return;
		}else if(et_ftpServer.getSelectedItemPosition()==0){        //FTP服务器为空
            Toast.makeText(com.walktour.gui.task.TaskFtpDownload.this.getApplicationContext(), R.string.task_alert_nullFtp, Toast.LENGTH_SHORT).show();
            return;
        }else if(remoteFileEditText.getText().toString().trim().length()==0 //远程文件名为空
                || remoteFileEditText.getText().toString().endsWith("/")
                || remoteFileEditText.getText().toString().endsWith("\\")){  
            Toast.makeText(com.walktour.gui.task.TaskFtpDownload.this.getApplicationContext(), R.string.task_alert_nullremoteFileDown, Toast.LENGTH_SHORT).show();
            remoteFileEditText.setError(getString(R.string.task_alert_nullremoteFileDown));
            return;
        }else if(Integer.parseInt(threadNumberEditText.getText().toString()) <= 0
                || Integer.parseInt(threadNumberEditText.getText().toString().trim()) >30){     //FTP服务器为空
            Toast.makeText(com.walktour.gui.task.TaskFtpDownload.this.getApplicationContext(), R.string.task_alert_nullOrzeroThread, Toast.LENGTH_SHORT).show();
            threadNumberEditText.setError(getString(R.string.task_alert_nullOrzeroThread));
            return;
        }else{}
        if(model==null){
            model = new TaskFtpModel(WalkStruct.TaskType.FTPDownload.name());
            taskd.setCurrentTaskIdAndSequence(model);
        }
        model.setTaskName(taskNameEditText.getText().toString().trim());
        model.setTaskType(WalkStruct.TaskType.FTPDownload.name());
        model.setEnable(1);
        model.setRepeat(Integer.parseInt( repeatEditText.getText().toString().trim().length()==0?"1":repeatEditText.getText().toString().trim()));
        model.setTimeOut(Integer.parseInt( timeOutEditText.getText().toString().trim().length()==0?"90":timeOutEditText.getText().toString().trim()));
        model.setNoAnswer(Integer.parseInt( noAnswerEditText.getText().toString().trim().length()==0?"20":noAnswerEditText.getText().toString().trim()));
        model.setInterVal(Integer.parseInt( interValEditText.getText().toString().trim().length()==0?"10":interValEditText.getText().toString().trim()));
        model.setFtpServer(config_ftp.getNameFirstEmpty( et_ftpServer.getSelectedItemPosition() ,TaskFtpDownload.this) );
        model.setRemoteFile(remoteFileEditText.getText().toString().trim().length()==0?"":remoteFileEditText.getText().toString().trim());
        model.setThreadNumber(Integer.parseInt(threadNumberEditText.getText().toString().trim().length()==0?"1":threadNumberEditText.getText().toString().trim()));
        model.setDisConnect(disConnectSpinner.getSelectedItemPosition());
        model.setPsCall(psCallSpinner.getSelectedItemPosition());
        model.setAccessPoint( spApSpinner.getSelectedItemPosition()  );
		model.setTransportProtocal(ed_transfer_protocal.getSelectedItemPosition());
		model.setThreadMode(ed_thread_mode.getSelectedItemPosition()+"");
        model.setPppfaildtimes(Integer.parseInt(ppptimesEditText.getText().toString().trim().length() == 0 ? "3" : ppptimesEditText.getText().toString().trim()));
        model.setPppInterval(Integer.parseInt(pppintervalEditText.getText().toString().trim().length() == 0 ? "3" : pppintervalEditText.getText().toString().trim()));
        model.setLoginTimes(Integer.parseInt(logintimesEditText.getText().toString().trim().length() == 0 ? "3" : logintimesEditText.getText().toString().trim()));
        model.setLoginInterval(Integer.parseInt(loginintervalEditText.getText().toString().trim().length() == 0 ? "3" : loginintervalEditText.getText().toString().trim()));
        model.setLoginTimeOut(Integer.parseInt(loginTimeoutEditText.getText().toString().trim().length() == 0 ? "60" : loginTimeoutEditText.getText().toString().trim()));
        model.setMaxThr(spnMaxThr.getSelectedItemPosition());
        model.setSendBuffer(sendBufferText.getText().toString()+"");
        model.setReceiveBuffer(receiveBufferText.getText().toString()+"");
        /*model.setTcpipCapture(et_Capture.getSelectedItemPosition());
        model.setProtocol(et_protocol.getSelectedItemPosition());
        model.setCache(Integer.parseInt(et_cache.getText().toString().trim().length() == 0 ? "1000" : et_cache.getText().toString().trim()));
        */
        if(((Long)dataConnectType.getSelectedItemId()).intValue()==1){
        	model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
        	model.getNetworkConnectionSetting().updateWifiParam(wifiSSIDET.getText().toString() + "", wifiUserET.getText().toString() + "", wifiPasswordET.getText().toString() + "");
        }else if(((Long) dataConnectType.getSelectedItemId()).intValue() == 2){//NBPPP
			model.setTypeProperty(WalkCommonPara.TypeProperty_Ppp);
			model.getNetworkConnectionSetting().setConnectionUseWifi(false);
		} else{//PPP
			model.setTypeProperty(WalkCommonPara.TypeProperty_Net);
			model.getNetworkConnectionSetting().setConnectionUseWifi(false);
		}
        
        model.setRabRelTime(super.rabRelTimeEdt.getText().toString().trim().length() == 0 ? "50" : super.rabRelTimeEdt.getText().toString().trim());
        model.setRabRuelTime(super.rabAblTimeEdt.getText().toString().trim().length() == 0 ? "12:00" : super.rabAblTimeEdt.getText().toString().trim());


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
        historyShare.saveHistory(remoteFileEditText);
        taskd.setTaskListArray(array);
        
        Toast.makeText(getApplicationContext(),isNew?R.string.task_alert_newSucess:R.string.task_alert_updateSucess,Toast.LENGTH_SHORT).show();
        TaskFtpDownload.this.finish();
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
                if(interValEditText.getText().toString().trim().equals("0")){
                    timeOutEditText.setError(getString(R.string.task_alert_nullTimeout));
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
        
        noAnswerEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if( noAnswerEditText.getText().toString().trim().equals("0") ){
                    noAnswerEditText.setError(getString(R.string.task_alert_input)+" "+
                            getString( R.string.task_noAnswer ));
                    return;
                }
            }
        });
        
        remoteFileEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(remoteFileEditText.getText().toString().trim().length()==0 //远程文件名为空
                        || remoteFileEditText.getText().toString().endsWith("/")
                        || remoteFileEditText.getText().toString().endsWith("\\")){  
                    remoteFileEditText.setError(getString(R.string.task_alert_nullremoteFileDown));
                }
            }
        });
        
        threadNumberEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            	int equalInt="".equals(threadNumberEditText.getText().toString())?0:Integer.valueOf(threadNumberEditText.getText().toString());
                if(equalInt <= 0
                        || equalInt >30){     //FTP服务器为空
                    threadNumberEditText.setError(getString(R.string.task_alert_nullOrzeroThread));
                }
            }
        });
   
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == 55 && resultCode == RESULT_OK){
				Log.i("ftplist", "返回OK");
				serverPos = data.getExtras().getInt("POS");
				isFtpList = true;
				String remoteStr = data.getExtras().getString("path");
				if( remoteStr != null){
					remoteFileEditText.setText(remoteStr);
				}
		}
			
	}
    
    
    
}
