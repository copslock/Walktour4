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

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.framework.view.DateTimePickDialogUtil;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.http.page.TaskHttpPageModel;
import com.walktour.gui.task.parsedata.model.task.http.upload.TaskHttpUploadModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

import java.util.List;

public class TaskHttpDownload extends BaseTaskActivity {
	TaskListDispose taskd = null;
	TaskHttpPageModel model = null;
	private int taskListId = -1;
	private boolean isNew = true;

	private EditText et_taskName;
	private EditText et_repeat;
	private Spinner testMode;
	private EditText et_timeOut;
	private EditText et_thrNum;
	private EditText et_noAnswer;
	private EditText et_url;
	private EditText et_interVal;
	private Spinner et_showWeb;
	private Spinner sp_ap;
	private Spinner et_disConnect;
	private TaskRabModel taskRabModel;
	private Spinner dataConnectType;// 数据连接选择：PPP or WIFI

	private Spinner serverType;
	private Spinner accountType;
	private EditText accountKey;
	private EditText secretKey;
	private RelativeLayout rab_time_layout;
	private RelativeLayout rab_rule_time_layout;
	private LinearLayout wifiTestLayout;
	private RelativeLayout userNameLayout;
	private Button wifiSSIDET;
	private EditText wifiUserET;
	private EditText wifiPasswordET;
	private LayoutInflater inflater;
	private Context context = TaskHttpDownload.this;

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
						model = (TaskHttpPageModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel()
								.get(taskListId);
						break;
					}
				}
			} else {
				model = (TaskHttpPageModel) taskd.getTaskListArray().get(taskListId);
			}
			abstModel = model;
			isNew = false;
		}
		showView();
	}

	private void showView() {
		setContentView(R.layout.task_http_download);
		(initTextView(R.id.title_txt)).setText(R.string.act_task_httpDownload);// 设置标题
		(initImageView(R.id.pointer)).setOnClickListener(this);
		(initRelativeLayout(R.id.advanced_arrow_rel)).setOnClickListener(this);

		TextView tv_taskName = initTextView(R.id.txt_taskname);
		TextView tv_repeat = initTextView(R.id.txt_repeat);
		final TextView tv_timeOut = initTextView(R.id.txt_timeOut);
		TextView tv_noAnswer = initTextView(R.id.txt_noAnswer);
		TextView tv_url = initTextView(R.id.txt_url);
		TextView tv_interVal = initTextView(R.id.txt_interVal);
		TextView tv_showweb = initTextView(R.id.txt_showWeb);
		TextView tv_disConnect = initTextView(R.id.txt_disConnect);
		Button btn_ok = initButton(R.id.btn_ok);
		Button btn_cencle = initButton(R.id.btn_cencle);
		testMode = (Spinner)findViewById(R.id.edit_testMode);
		tv_taskName.setText(getString(R.string.task_taskName));
		tv_repeat.setText(getString(R.string.task_repeat));
		tv_timeOut.setText(getString(R.string.task_ftpdownload_timeout));
		tv_noAnswer.setText(getString(R.string.task_noAnswer));
		tv_url.setText(getString(R.string.task_url));
		tv_interVal.setText(getString(R.string.task_interVal));
		tv_disConnect.setText(getString(R.string.task_disConnect));
		tv_showweb.setText(getString(R.string.task_webshow));
		btn_ok.setText(" " + getString(R.string.str_save) + " ");
		btn_cencle.setText(getString(R.string.str_cancle));

		et_taskName = initEditText(R.id.edit_taskname);
		et_repeat = initEditText(R.id.edit_repeat);
		et_timeOut = initEditText(R.id.edit_timeOut);
		et_thrNum  = initEditText(R.id.edit_thrNum);
		et_noAnswer = initEditText(R.id.edit_noAnswer);
		et_url = initEditText(R.id.edit_url);
		et_interVal = initEditText(R.id.edit_interVal);
		et_showWeb = initSpinner(R.id.edit_showWeb);
		et_disConnect = initSpinner(R.id.edit_disConnect);
		serverType = (Spinner) findViewById(R.id.edit_serverType);
		accountType = (Spinner) findViewById(R.id.edit_AccountType);
		accountKey = initEditText(R.id.edit_baidu_key);
		secretKey = initEditText(R.id.edit_baidu_secret_key);
		// 添加Net接入点和Wifi接入点
		TextView tv_ap = initTextView(R.id.txt_ap);
		tv_ap.setText(getString(R.string.task_accepoint));
		sp_ap = initSpinner(R.id.spiner_ap);
		ArrayAdapter<String> adpAP = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.task_ap));
		adpAP.setDropDownViewResource(R.layout.spinner_dropdown_item);
		sp_ap.setAdapter(adpAP);
		ArrayAdapter<String> showApter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.public_yn));
		showApter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		et_showWeb.setAdapter(showApter);

		
		 //Test model
        ArrayAdapter<String> testModeAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_ftpdownload_testmode));
        testModeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        testMode.setAdapter(testModeAdapter);
        testMode.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == TaskHttpUploadModel.BY_FILE){
					tv_timeOut.setText(getString(R.string.task_ftpdownload_timeout));
				}else{
					tv_timeOut.setText(getString(R.string.task_ftpdownload_Duration));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		}); 
		// wifi support
		dataConnectType = (Spinner) findViewById(R.id.edit_data_connect_type);
		setDataConnectTypeSP(dataConnectType);

		ArrayAdapter<String> dataConnectTypeAdapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_custom_layout, ApplicationModel.getInstance().getConnectType());
		dataConnectTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		dataConnectType.setAdapter(dataConnectTypeAdapter);
		// 数据连接类型
		dataConnectType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {// PPP测试
					wifiTestLayout.setVisibility(View.GONE);
				} else {// wifi测试
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

		ArrayAdapter<String> serverTypeAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_httpdownload_servertype));
		serverTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		serverType.setAdapter(serverTypeAdapter);

		ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.BaiduSpecialCount)
						? getResources().getStringArray(R.array.array_httpupload_accountType)
						: new String[] { getResources().getStringArray(R.array.array_httpupload_accountType)[0] });
		accountAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		accountType.setAdapter(accountAdapter);

		serverType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					((LinearLayout) findViewById(R.id.task_server_baidu)).setVisibility(View.GONE);
				} else {
					((LinearLayout) findViewById(R.id.task_server_baidu)).setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		accountType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// 如果是Youtube，不允许选择文件获取方式
				if (position == 0) {
					((LinearLayout) findViewById(R.id.task_baidu_count_key)).setVisibility(View.VISIBLE);
				} else {
					((LinearLayout) findViewById(R.id.task_baidu_count_key)).setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// 断开网络配置
		ArrayAdapter<String> disconnect = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_task_disconnect));
		disconnect.setDropDownViewResource(R.layout.spinner_dropdown_item);
		et_disConnect.setAdapter(disconnect);
		// wifi support
		// dataConnectType = initSpinner(R.id.edit_data_connect_type);
		// super.setDataConnectTypeSP(dataConnectType);

		// 并发相对时间
		rab_time_layout = (RelativeLayout) findViewById(R.id.rab_time_layout);
		rab_rule_time_layout = (RelativeLayout) findViewById(R.id.rab_time_rel_layout);
		super.setRabTime(rab_time_layout, rab_rule_time_layout);

		// 并发专用
		if (model != null) {
			super.rabRelTimeEdt.setText(model.getRabRelTime());
			super.rabAblTimeEdt.setText(model.getRabRuelTime());
		} else {
			super.rabRelTimeEdt.setText("50");
			super.rabAblTimeEdt.setText("12:00");
		}

		super.rabAblTimeEdt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(TaskHttpDownload.this,
						rabAblTimeEdt.getText().toString());
				dateTimePicKDialog.dateTimePicKDialog(rabAblTimeEdt);
			}
		});
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
						ApSelectAdapter adapter = new ApSelectAdapter(list);
						openDialog(adapter, list, -1, wifiSSIDET);
					} else {
						ToastUtil.showToastShort(context, getString(R.string.sys_wifi_aplist) + "");
					}
				}

			}
		});
		if (model != null) {
			et_taskName.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%") + 1,
					model.getTaskName().toString().trim().length()));
			et_repeat.setText(String.valueOf(model.getRepeat()));
			et_timeOut.setText(String.valueOf(model.getTimeOut()));
			et_thrNum.setText(String.valueOf(model.getThreadCount()));
			et_noAnswer.setText(String.valueOf(model.getReponse()));
			et_url.setText(String.valueOf(model.getXmlUrl()));
			et_interVal.setText(String.valueOf(model.getInterVal()));
			sp_ap.setSelection(model.getAccessPoint());
			et_showWeb.setSelection(model.isShowWeb() ? 1 : 0);
			et_disConnect.setSelection(model.getDisConnect());
			dataConnectType.setSelection(model.getTypeProperty() == 4 ? 1 : 0);

			serverType.setSelection(model.getServerType());
			accountType.setSelection(model.getAccountType());
			accountKey.setText(model.getAccountKey());
			secretKey.setText(model.getSecretKey());
			testMode.setSelection(model.getTestMode());
		} else {
			et_taskName.setText("HTTP Download");
			et_repeat.setText("10");
			et_timeOut.setText("300");
			et_thrNum.setText("1");
			et_noAnswer.setText("30");
			et_url.setText("http://");
			et_interVal.setText("15");
			sp_ap.setSelection(0);
			et_disConnect.setSelection(1);
		}

		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveTestTask();
			}
		});

		btn_cencle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TaskHttpDownload.this.finish();
			}
		});
		
		if (null != model) {
			if (model.getTypeProperty() == WalkCommonPara.TypeProperty_Wlan || model.getNetworkConnectionSetting().isConnectionUseWifi()) {
				dataConnectType.setSelection(1);
				wifiTestLayout.setVisibility(View.VISIBLE);
				String apName=model.getNetworkConnectionSetting().getWifiParam()[0]+"";
				wifiSSIDET.setText(apName);
				wifiUserET.setText(model.getNetworkConnectionSetting().getWifiParam()[1]);
				wifiPasswordET.setText(model.getNetworkConnectionSetting().getWifiParam()[2]);
				if(apName.equals("ChinaNet")||apName.equals("ChinaUnicom")||apName.contains("CMCC-WEB")||apName.contains("CMCC")){
					userNameLayout.setVisibility(View.VISIBLE);
				}
			} else if(model.getTypeProperty() == WalkCommonPara.TypeProperty_Ppp){
				dataConnectType.setSelection(2);
				wifiTestLayout.setVisibility(View.GONE);
			}else {
				dataConnectType.setSelection(0);
				wifiTestLayout.setVisibility(View.GONE);
			}
		}
	}

	private final class ApItem {
		public TextView wifiAP;
		public TextView wifiStrength;
	}

	private class ApSelectAdapter extends BaseAdapter {
		private List<ScanResult> listSR;

		public ApSelectAdapter(List<ScanResult> listSR) {
			super();
			this.listSR = listSR;
		}

		@Override
		public int getCount() {
			return listSR == null ? 0 : listSR.size();
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
			itemView.wifiStrength.setText(model.level + "dbm");
			return convertView;
		}
	}

	private void openDialog(final BaseAdapter adapter, final List<ScanResult> listSR, final int checkedItem,
			final Button button) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.sys_wifi_selectap) + "");
		builder.setSingleChoiceItems(adapter, checkedItem, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ScanResult sr = listSR.get(which);
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

	private void setDataConnectTypeSP(Spinner dataConnectTypeSP) {
		if (dataConnectTypeSP != null) {
			if (showInfoList.contains(WalkStruct.ShowInfoType.WLAN)) {
				ArrayAdapter<String> dataConnectTypeAdapter = new ArrayAdapter<String>(this,
						R.layout.simple_spinner_custom_layout,
						ApplicationModel.getInstance().getConnectType());
				dataConnectTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
				dataConnectTypeSP.setAdapter(dataConnectTypeAdapter);
			} else {
				findViewById(R.id.task_wifi_app_choice).setVisibility(View.GONE);
				
				findViewById(R.id.task_wifi_test_choice).setVisibility(View.GONE);
			}

		}
	}
 
	@SuppressLint("StringFormatMatches")
	@Override
	public void saveTestTask() {
		if (et_taskName.getText().toString().trim().length() == 0) { // 任务名为空
			ToastUtil.showToastShort(com.walktour.gui.task.TaskHttpDownload.this.getApplicationContext(),
					R.string.task_alert_nullName);
			return;
		} else if (et_repeat.getText().toString().trim().equals("0")
				|| et_repeat.getText().toString().trim().length() == 0) {
			ToastUtil.showToastShort(getApplicationContext(), R.string.task_alert_nullRepeat);
			return;
		} else if (et_timeOut.getText().toString().trim().equals("0")
				|| et_timeOut.getText().toString().trim().length() == 0) {
			ToastUtil.showToastShort(getApplicationContext(), R.string.task_alert_nullTimeout);
			return;
		} else if (et_thrNum.getText().toString().trim().length() == 0
				|| Integer.parseInt(et_thrNum.getText().toString().trim()) < 1
				|| Integer.parseInt(et_thrNum.getText().toString().trim()) > 30) {
			ToastUtil.showToastShort(getApplicationContext(),getString(R.string.task_threadNumber)+","+String.format(getString(R.string.alert_inputt_interregional),1,30));
			return;
		}else if (et_interVal.getText().toString().trim().equals("0")
				|| et_interVal.getText().toString().trim().length() == 0) {
			ToastUtil.showToastShort(getApplicationContext(), R.string.task_alert_nullOrzeroThread);
			return;
		} else if (et_url.getText().toString().trim().length() == 0) { // URL为空
			ToastUtil.showToastShort(com.walktour.gui.task.TaskHttpDownload.this.getApplicationContext(),
					R.string.task_alert_nullUrl);
			return;
		} else if (serverType.getSelectedItemPosition() == 1 && accountType.getSelectedItemPosition() == 0
				&& StringUtil.isEmpty(accountKey.getText().toString())) {
			ToastUtil.showToastShort(this, getString(R.string.task_alert_nullKey));
			return;
		} else if (serverType.getSelectedItemPosition() == 1 && accountType.getSelectedItemPosition() == 0
				&& StringUtil.isEmpty(secretKey.getText().toString())) {
			ToastUtil.showToastShort(this, getString(R.string.task_alert_nullSecretKey));
			return;
		} else if(et_noAnswer.getText().toString().trim().length() == 0||
				!StringUtil.isRange(Integer.parseInt(et_noAnswer.getText().toString()), 5, 120)){
			ToastUtil.showToastShort(context,getString(R.string.task_noAnswer)+","+String.format(getString(R.string.alert_inputt_interregional), 5,120));
			et_noAnswer.setError(getString(R.string.task_noAnswer)+","+String.format(getString(R.string.alert_inputt_interregional), 5,120));
			return;
		}
		if (model == null) {
			model = new TaskHttpPageModel(WalkStruct.TaskType.HttpDownload.name());
			taskd.setCurrentTaskIdAndSequence(model);
		}
		model.setTestMode(testMode.getSelectedItemPosition());
		model.setTaskName(et_taskName.getText().toString().trim());
		model.setTaskType(WalkStruct.TaskType.HttpDownload.name());
		model.setEnable(1);
		model.setUrlModelList(null);
		model.setRepeat(Integer.parseInt(
				et_repeat.getText().toString().trim().length() == 0 ? "15" : et_repeat.getText().toString().trim()));
		model.setTimeOut(Integer.parseInt(
				et_timeOut.getText().toString().trim().length() == 0 ? "300" : et_timeOut.getText().toString().trim()));
		
		model.setThreadCount(Integer.parseInt(
				et_thrNum.getText().toString().trim().length() == 0 ? "1" : et_thrNum.getText().toString().trim()));
		model.setReponse(Integer.parseInt(et_noAnswer.getText().toString().trim().length() == 0 ? "30"
				: et_noAnswer.getText().toString().trim()));
		model.setXmlUrl(
				et_url.getText().toString().trim().length() == 0 ? "http://" : et_url.getText().toString().trim());
		model.setInterVal(Integer.parseInt(et_interVal.getText().toString().trim().length() == 0 ? "15"
				: et_interVal.getText().toString().trim()));
		model.setAccessPoint(sp_ap.getSelectedItemPosition());
		model.setTypeProperty(2);

		model.setDisConnect(et_disConnect.getSelectedItemPosition());
		model.setShowWeb(et_showWeb.getSelectedItemPosition() == 1);

		if (((Long) dataConnectType.getSelectedItemId()).intValue() == 1) {
			model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
			model.getNetworkConnectionSetting().updateWifiParam(wifiSSIDET.getText().toString() + "", wifiUserET.getText().toString() + "", wifiPasswordET.getText().toString() + "");
		}else if(((Long) dataConnectType.getSelectedItemId()).intValue() == 2){//NBPPP
			model.setTypeProperty(WalkCommonPara.TypeProperty_Ppp);
			model.getNetworkConnectionSetting().setConnectionUseWifi(false);
		} else{//PPP
			model.setTypeProperty(WalkCommonPara.TypeProperty_Net);
			model.getNetworkConnectionSetting().setConnectionUseWifi(false);
		}
		model.setServerType(serverType.getSelectedItemPosition());
		model.setAccountType(accountType.getSelectedItemPosition());
		model.setAccountKey(accountKey.getText().toString());
		model.setSecretKey(secretKey.getText().toString());
		model.setRabRelTime(super.rabRelTimeEdt.getText().toString().trim().length() == 0 ? "50"
				: super.rabRelTimeEdt.getText().toString().trim());
		model.setRabRuelTime(super.rabAblTimeEdt.getText().toString().trim().length() == 0 ? "12:00"
				: super.rabAblTimeEdt.getText().toString().trim());

		List<TaskModel> array = taskd.getTaskListArray();
		if (RABTAG.equals(super.getRabTag())) {// 依标志区分并发与普通业务
			for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
				if (super.getMultiRabName().equals(taskd.getCurrentTaskList().get(i).getTaskID())) {
					taskRabModel = (TaskRabModel) taskd.getCurrentTaskList().get(i);
					break;
				}
			}
			if (isNew) {
				taskRabModel.setTaskModelList(taskRabModel.addTaskList(model));
			} else {
				taskRabModel.getTaskModel().remove(taskListId);
				taskRabModel.getTaskModel().add(taskListId, model);
			}

		} else {// 普通业务保存入口
			if (isNew) {
				array.add(array.size(), model);
			} else {
				array.remove(taskListId);
				array.add(taskListId, model);
			}
		}
		taskd.setTaskListArray(array);

		ToastUtil.showToastShort(getApplicationContext(),
				isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess);
		TaskHttpDownload.this.finish();
	}
}
