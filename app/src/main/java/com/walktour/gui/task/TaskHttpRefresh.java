package com.walktour.gui.task;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.http.page.TaskHttpPageModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

import java.util.List;

public class TaskHttpRefresh extends BaseTaskActivity {
	TaskListDispose taskd =null;
	TaskHttpPageModel model =null;
	private int taskListId=-1;
	private boolean isNew=true;
	
    private EditText et_taskName;
    private EditText et_repeat;
    private EditText et_timeOut;
    private EditText et_noAnswer;
    private EditText et_url;
    private EditText et_interVal;
    private EditText et_refreshDeep;
    private Spinner et_showWeb;
    private Spinner sp_ap;
    private Spinner  et_disConnect;
	private TaskRabModel taskRabModel;
	private Spinner dataConnectType;//数据连接选择：PPP or WIFI
	private LinearLayout wifiTestLayout ;
	private RelativeLayout userNameLayout;
	private Button wifiSSIDET ;
	private EditText wifiUserET ;
	private EditText wifiPasswordET ; 
	private Context context=TaskHttpRefresh.this;
	private LayoutInflater inflater;
	@Override
	public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
    	inflater = LayoutInflater.from(this);
        taskd=TaskListDispose.getInstance();
        Bundle bundle = getIntent().getExtras(); 
        if(bundle != null && bundle.containsKey("taskListId")){
         	taskListId = bundle.getInt("taskListId");
         	if(RABTAG.equals(super.getRabTag())){
        		for(int i=0;i<taskd.getCurrentTaskList().size();i++){ 
        				if(taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())){
            				model =	(TaskHttpPageModel)((TaskRabModel)(taskd.getCurrentTaskList().get(i))).getTaskModel().get(taskListId);
            				break;
            			}
        		}
        	}else{
         		model = (TaskHttpPageModel)taskd.getTaskListArray().get(taskListId);
         	}
         	abstModel = model;
         	isNew=false;
         }
        showView();
	}
	private void showView(){
	    setContentView(R.layout.task_http_refresh);
		(initTextView(R.id.title_txt)).setText(R.string.act_task_httpFresh);//设置标题
        (initImageView(R.id.pointer)).setOnClickListener(this);
        (initRelativeLayout(R.id.advanced_arrow_rel)).setOnClickListener(this);
        
		TextView tv_taskName = initTextView(R.id.txt_taskname);
		TextView tv_repeat =initTextView(R.id.txt_repeat);
		TextView tv_timeOut =initTextView(R.id.txt_timeOut);
		TextView tv_noAnswer =initTextView(R.id.txt_noAnswer);
		TextView tv_url =initTextView(R.id.txt_url);
		TextView tv_interVal =initTextView(R.id.txt_interVal);
		TextView tv_refreshDeep =initTextView(R.id.txt_refreshDeep);
		TextView tv_showweb =initTextView(R.id.txt_showWeb);
		TextView tv_disConnect =initTextView(R.id.txt_disConnect);
		Button btn_ok = initButton(R.id.btn_ok);
        Button btn_cencle = initButton(R.id.btn_cencle);
        
        tv_taskName.setText(getString(R.string.task_taskName));
        tv_repeat.setText(getString(R.string.task_repeat));
        tv_timeOut.setText(getString(R.string.task_timeOut));
        tv_noAnswer.setText(getString(R.string.task_noAnswer));
        tv_url.setText(getString(R.string.task_url));
        tv_interVal.setText(getString(R.string.task_interVal));
        tv_disConnect.setText(getString(R.string.task_disConnect));
        tv_refreshDeep.setText( getString(R.string.task_refurbish) );
		tv_showweb.setText(getString(R.string.task_webshow));
        btn_ok.setText(" "+getString(R.string.str_save)+" ");
        btn_cencle.setText(getString(R.string.str_cancle));
        
		et_taskName = initEditText(R.id.edit_taskname);
		et_repeat =initEditText(R.id.edit_repeat);
		et_timeOut =initEditText(R.id.edit_timeOut);
		et_noAnswer =initEditText(R.id.edit_noAnswer);
		et_url =initEditText(R.id.edit_url);
		et_interVal =initEditText(R.id.edit_interVal);
		et_refreshDeep =initEditText(R.id.edit_refreshDeep );
		et_showWeb =initSpinner(R.id.edit_showWeb);
		et_disConnect =initSpinner(R.id.edit_disConnect);

		//final Spinner sp_proxy =initSpinner(R.id.SpinnerProxy);
		//final Spinner sp_downpic =initSpinner(R.id.SpinnerDownPicture);
		//final EditText et_address =initEditText(R.id.EditTextAdd);
		//final EditText et_port =initEditText(R.id.EditTextPort);
		//final EditText et_user =initEditText(R.id.EditTextUser);
		//final EditText et_pass =initEditText(R.id.EditTextPass);
		
        //添加Net接入点和Wifi接入点
        TextView tv_ap =initTextView(R.id.txt_ap);
        tv_ap.setText(getString(R.string.task_accepoint));
        sp_ap =initSpinner(R.id.spiner_ap );
        ArrayAdapter<String> adpAP = new ArrayAdapter<String>(this,
        		R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.task_ap) );
        adpAP.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_ap.setAdapter( adpAP );
        ArrayAdapter<String> showApter = new ArrayAdapter<String>(this,
        		R.layout.simple_spinner_custom_layout,getResources().getStringArray(R.array.public_yn));
        showApter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        et_showWeb.setAdapter(showApter);
        //sp_proxy.setAdapter(showApter);
        //sp_downpic.setAdapter(showApter);
        
		//断开网络配置
        ArrayAdapter<String> disconnect = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_task_disconnect));
        disconnect.setDropDownViewResource(R.layout.spinner_dropdown_item);
        et_disConnect.setAdapter(disconnect);
        //wifi support 
      	dataConnectType = initSpinner(R.id.edit_data_connect_type);
      	setDataConnectTypeSP(dataConnectType);
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
        	et_taskName.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%")+1, model.getTaskName().toString().trim().length()));
	        et_repeat.setText( String.valueOf(model.getRepeat()));
	        et_timeOut.setText(String.valueOf(model.getTimeOut()));
	        et_noAnswer.setText(String.valueOf(model.getReponse()));
	        et_url.setText( String.valueOf(model.getUrl()));
	        et_interVal.setText(String.valueOf(model.getInterVal()));
	        sp_ap.setSelection( model.getAccessPoint() );
	        et_showWeb.setSelection(model.isShowWeb() ? 1 : 0);
			et_disConnect.setSelection(model.getDisConnect());
			et_refreshDeep.setText( String.valueOf( model.getRefreshDeep() ) );
			dataConnectType.setSelection(model.getTypeProperty()==4?1:0); //ppp 0 wifi 1
        }else{
        	et_taskName.setText("HTTP Refresh");
        	et_repeat.setText("10");
	        et_timeOut.setText("15");
	        et_noAnswer.setText("20");
	        et_url.setText("http://");
	        et_interVal.setText("15");
	        sp_ap.setSelection( 0 );
	        et_disConnect.setSelection(1);
	        et_refreshDeep.setText( "3" );
        }
        btn_ok.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        	    saveTestTask();
        	}
        });
        
        btn_cencle.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        	    TaskHttpRefresh.this.finish();
        	}
        });
        //数据连接类型
        dataConnectType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { 
				if(position==0||position == 2){//PPP测试
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
	public void onResume(){
		super.onResume();
//		showView();
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
    
    @SuppressLint("StringFormatMatches")
	@Override
    public void saveTestTask() {
        if(et_taskName.getText().toString().trim().length()==0){    //任务名为空
            Toast.makeText(com.walktour.gui.task.TaskHttpRefresh.this.getApplicationContext(), R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
            return;
        }else if( et_repeat.getText().toString().trim().equals("0") 
				||  et_repeat.getText().toString().trim().length()==0){
			Toast.makeText( getApplicationContext(),
					R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			return;
		}else if (et_timeOut.getText().toString().trim().length()==0
				|| !StringUtil.isRange(Integer.parseInt(et_timeOut.getText().toString()), 5,300)) {
			
			if(et_timeOut.getText().toString().trim().length()==0){
				ToastUtil.showToastShort(context, R.string.task_alert_nullTimeout);
				et_timeOut.setError(getString(R.string.task_alert_nullTimeout));
			}else{
				ToastUtil.showToastShort(context,getString(R.string.task_refreshTimeOut)+","+String.format(getString(R.string.share_project_devices_release_relation_9), 5,300));
				et_timeOut.setError(getString(R.string.task_refreshTimeOut)+","+String.format(getString(R.string.share_project_devices_release_relation_9), 5,300));
			}
			return;
		}else if( et_interVal.getText().toString().trim().equals("0")
				|| et_interVal.getText().toString().trim().length()==0 ){
			Toast.makeText(getApplicationContext(),
					R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			return;
		}else if(et_url.getText().toString().trim().length()==0){   //URL为空
            Toast.makeText(com.walktour.gui.task.TaskHttpRefresh.this.getApplicationContext(), R.string.task_alert_nullUrl, Toast.LENGTH_SHORT).show();
            return;
        }else if( et_refreshDeep.getText().toString().trim().length()==0 
        		|| et_refreshDeep.getText().toString().trim().equals("0") ){
        	Toast.makeText( TaskHttpRefresh.this, 
        			getString(R.string.task_alert_input)+" " + getString( R.string.task_refurbish ),
        			Toast.LENGTH_SHORT ).show();
        	return;
        }else{}
        if(model==null){
            model = new TaskHttpPageModel(WalkStruct.TaskType.HttpRefurbish.name());
			taskd.setCurrentTaskIdAndSequence(model);
        }
        	model.setTaskName(et_taskName.getText().toString().trim());
        model.setTaskType(WalkStruct.TaskType.HttpRefurbish.name() );
        model.setEnable(1);
        model.setRepeat(Integer.parseInt( et_repeat.getText().toString().trim().length()==0?"10":et_repeat.getText().toString().trim()));
        model.setTimeOut(Integer.parseInt( et_timeOut.getText().toString().trim().length()==0?"15":et_timeOut.getText().toString().trim()));
        model.setReponse( Integer.parseInt( et_noAnswer.getText().toString().trim().length()==0?"20":et_noAnswer.getText().toString().trim()));
        model.setUrl(et_url.getText().toString().trim().length()==0?"http://":et_url.getText().toString().trim());
        model.setInterVal(Integer.parseInt( et_interVal.getText().toString().trim().length()==0?"15":et_interVal.getText().toString().trim()));
        model.setAccessPoint( sp_ap.getSelectedItemPosition()  );
        model.setDisConnect(et_disConnect.getSelectedItemPosition());
		model.setShowWeb(et_showWeb.getSelectedItemPosition()==1 );
		model.setRefreshDeep( Integer.parseInt( et_refreshDeep.getText().toString().trim().length()==0?"1" : et_refreshDeep.getText().toString().trim() ) );

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
        }else{//普通业务保存入口
        	if(isNew){
				array.add(array.size(), model);
        	}else{
        		array.remove(taskListId);
        		array.add(taskListId, model);
        	}
        }
        taskd.setTaskListArray(array);
        
        Toast.makeText(getApplicationContext(),isNew?R.string.task_alert_newSucess:R.string.task_alert_updateSucess,Toast.LENGTH_SHORT).show();
        TaskHttpRefresh.this.finish();
    }	
}
