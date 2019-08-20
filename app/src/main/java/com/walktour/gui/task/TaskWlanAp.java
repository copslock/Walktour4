package com.walktour.gui.task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
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
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.wlan.ap.TaskWlanApModel;

import java.util.List;

/**
 * Ping 测试
 * 
 * @author weirong.fan
 * 
 */
public class TaskWlanAp extends BaseTaskActivity {
	TaskListDispose taskd = null;
	TaskWlanApModel model = null;
	private int taskListId = -1;
	private boolean isNew = true;
	private EditText taskNameEditText;
	private EditText repeatEditText;
	private EditText timeOutEditText;
	private EditText interValEditText;
	private Spinner disConnectEditText;
	private TaskRabModel taskRabModel;
	private Spinner dataConnectType;// 数据连接选择：PPP or WIFI
	private LinearLayout wifiTestLayout;
	private RelativeLayout userNameLayout;
	private Button wifiSSIDET;
	private EditText wifiUserET;
	private EditText wifiPasswordET;
	private Context context=TaskWlanAp.this;
	private LayoutInflater inflater;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = LayoutInflater.from(this);
		taskd = TaskListDispose.getInstance();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("taskListId")) {
			taskListId = bundle.getInt("taskListId");
			if (RABTAG.equals(super.getRabTag())) {
				for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
					if (taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())) {
						model = (TaskWlanApModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel()
								.get(taskListId);
						break;
					}
				}
			} else {
				model = (TaskWlanApModel) taskd.getTaskListArray().get(taskListId);
			}
			abstModel = model;
			isNew = false;
		}

		findView();
		addEditTextWatcher();
	}

	private void findView() {
		setContentView(R.layout.task_wlanap);
		(initTextView(R.id.title_txt)).setText(R.string.act_task_wlanap);// 设置标题
		(initImageView(R.id.pointer)).setOnClickListener(this);
		(initRelativeLayout(R.id.advanced_arrow_rel)).setOnClickListener(this);
		TextView tv_taskName = initTextView(R.id.txt_taskname);
		TextView tv_repeat = initTextView(R.id.txt_repeat);
		TextView tv_timeOut = initTextView(R.id.txt_timeOut);
		TextView tv_interVal = initTextView(R.id.txt_interVal);
		TextView tv_disConnect = initTextView(R.id.txt_disConnect);
		Button btn_ok = initButton(R.id.btn_ok);
		Button btn_cencle = initButton(R.id.btn_cencle);
		tv_taskName.setText(getString(R.string.task_taskName));
		tv_repeat.setText(getString(R.string.task_repeat));
		tv_timeOut.setText(getString(R.string.task_wlanap_timeout));
		tv_interVal.setText(getString(R.string.task_interVal));
		tv_disConnect.setText(getString(R.string.task_disConnect));
		btn_ok.setText(" " + getString(R.string.str_save) + " ");
		btn_cencle.setText(getString(R.string.str_cancle));

		taskNameEditText = initEditText(R.id.edit_taskname);
		repeatEditText = initEditText(R.id.edit_ping_repeat);
		timeOutEditText = initEditText(R.id.edit_timeOut);
		interValEditText = initEditText(R.id.edit_interVal);
		disConnectEditText = initSpinner(R.id.edit_disConnect);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_task_disconnect));
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		disConnectEditText.setAdapter(adapter);
		// wifi support
		dataConnectType = initSpinner(R.id.edit_data_connect_type);
		setDataConnectTypeSP(dataConnectType);

		ArrayAdapter<String> dataConnectTypeAdapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_custom_layout, ApplicationModel.getInstance().getConnectType());
		dataConnectTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		dataConnectType.setAdapter(dataConnectTypeAdapter);
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
		if (model != null) {
			taskNameEditText.setText(model.getTaskName().toString().trim()
					.substring(model.getTaskName().indexOf("%") + 1, model.getTaskName().toString().trim().length()));
			repeatEditText.setText(String.valueOf(model.getRepeat()));
			timeOutEditText.setText(String.valueOf(model.getTimeOut()));
			// ipEditText.setText(String.valueOf(model.getIp()));
			interValEditText.setText(String.valueOf(model.getInterVal()));
			disConnectEditText.setSelection(model.getDisConnect());
			dataConnectType.setSelection(model.getTypeProperty() == 4 ? 1 : 0);
		} else {
			taskNameEditText.setText(getString(R.string.act_task_wlanap)+"");
			repeatEditText.setText("10");
			timeOutEditText.setText("60");
			interValEditText.setText("30");
			disConnectEditText.setSelection(1);
			dataConnectType.setSelection(1);
			dataConnectType.setEnabled(false);
			dataConnectType.setClickable(false);
		}
		initWifiInfo();
		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveTestTask();
			}
		});

		btn_cencle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TaskWlanAp.this.finish();
			}
		});
		// 数据连接类型
		dataConnectType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {// PPP测试
					wifiTestLayout.setVisibility(View.GONE);
				} else {// wifi测试
					wifiTestLayout.setVisibility(View.VISIBLE);
					
					initWifiInfo();
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
				initWifiInfo();
			} else {
				dataConnectType.setSelection(0);
				wifiTestLayout.setVisibility(View.GONE);
			}
		}
	}
	
	private void initWifiInfo(){
		if (null != model) {
			String apName = model.getWlanAPRelationTestConfig().getApName();
			wifiSSIDET.setText(apName);
			if (apName.equals("ChinaNet") || apName.equals("ChinaUnicom") || apName.contains("CMCC-WEB")
					|| apName.contains("CMCC")) {
				userNameLayout.setVisibility(View.VISIBLE);
			}
			wifiUserET.setText(model.getWlanAPRelationTestConfig().getWlanAccount().getUsername()+"");
			wifiPasswordET.setText(model.getWlanAPRelationTestConfig().getWlanAccount().getPassword()+"");
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
	@Override
	public void saveTestTask() {
		if (taskNameEditText.getText().toString().trim().length() == 0) { // 任务名为空
			Toast.makeText(com.walktour.gui.task.TaskWlanAp.this.getApplicationContext(), R.string.task_alert_nullName,
					Toast.LENGTH_SHORT).show();
			taskNameEditText.setError(getString(R.string.task_alert_nullName));
			return;
		} else if (repeatEditText.getText().toString().trim().equals("0")
				|| repeatEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
			return;
		} else if (timeOutEditText.getText().toString().trim().equals("0")
				|| timeOutEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			timeOutEditText.setError(getString(R.string.task_alert_nullTimeout));
			return;
		} else if (interValEditText.getText().toString().trim().equals("0")
				|| interValEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			interValEditText.setError(getString(R.string.task_alert_nullInterval));
			return;
		} else {}
		if (model == null){
			model = new TaskWlanApModel();
			taskd.setCurrentTaskIdAndSequence(model);
		}
		// 依据标记区分用户名的编辑
			model.setTaskName(taskNameEditText.getText().toString().trim());
		model.setTaskType(WalkStruct.TaskType.WlanAP.name());
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(repeatEditText.getText().toString().trim().length() == 0 ? "10"
				: repeatEditText.getText().toString().trim()));
		model.setTimeOut(Integer.parseInt(timeOutEditText.getText().toString().trim().length() == 0 ? "2"
				: timeOutEditText.getText().toString().trim()));
		model.setInterVal(Integer.parseInt(interValEditText.getText().toString().trim().length() == 0 ? "3"
				: interValEditText.getText().toString().trim()));
		model.setDisConnect(disConnectEditText.getSelectedItemPosition());
		if (((Long) dataConnectType.getSelectedItemId()).intValue() == 1) // 只有是WIFI的才设置
			model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan); //
		else
			model.setTypeProperty(WalkCommonPara.TypeProperty_Net); // ppp
		
		  if(((Long)dataConnectType.getSelectedItemId()).intValue()==1){
	        	model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
	        	model.getWlanAPRelationTestConfig().setApName(wifiSSIDET.getText().toString()+"");
	        	model.getWlanAPRelationTestConfig().getWlanAccount().setUsername(wifiUserET.getText().toString()+"");
	        	model.getWlanAPRelationTestConfig().getWlanAccount().setPassword(wifiPasswordET.getText().toString()+"");
	        }
		int enableLength = taskd.getTaskNames(1).length;
		List<TaskModel> array = taskd.getTaskListArray();
		if (RABTAG.equals(super.getRabTag())) {// 依标志区分并发与普通业务
			for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
				if (super.getMultiRabName().equals(taskd.getCurrentTaskList().get(i).getTaskID())) {
					taskRabModel = (TaskRabModel) taskd.getTaskListArray().get(i);
					break;
				}
			}
			if (isNew) {
				taskRabModel.setTaskModelList(taskRabModel.addTaskList(model));
			} else {
				taskRabModel.getTaskModel().remove(taskListId);
				taskRabModel.getTaskModel().add(taskListId, model);
			}
		} else {
			if (isNew) {
				array.add(array.size(),model);
			} else {
				array.remove(taskListId);
				array.add(taskListId, model);
			}

		}
		taskd.setTaskListArray(array);

		Toast.makeText(getApplicationContext(),
				isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess, Toast.LENGTH_SHORT).show();
		TaskWlanAp.this.finish();
	}

	/**
	 * 添加EditText输入监听限制
	 */
	public void addEditTextWatcher() {

		taskNameEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (taskNameEditText.getText().toString().trim().length() == 0) { // 任务名为空
					taskNameEditText.setError(getString(R.string.task_alert_nullName));
				}

			}
		});

		timeOutEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (timeOutEditText.getText().toString().trim().equals("0")) {
					timeOutEditText.setError(getString(R.string.task_alert_nullTimeout));
				}
			}
		});

		repeatEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (repeatEditText.getText().toString().trim().equals("0")
						|| repeatEditText.getText().toString().trim().length() == 0) {
					repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
				}
			}
		});

		interValEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (interValEditText.getText().toString().trim().equals("0")
						|| interValEditText.getText().toString().trim().length() == 0) {
					interValEditText.setError(getString(R.string.task_alert_nullInterval));
				}
			}
		});

	}
}
