package com.walktour.gui.task;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.walktour.gui.setting.SysURL;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.http.page.TaskHttpPageModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.model.UrlModel;

import java.util.ArrayList;
import java.util.List;

public class TaskHttpPage extends BaseTaskActivity {
	TaskListDispose taskd = null;
	TaskHttpPageModel model = null;
	private int taskListId = -1;
	private boolean isNew = true;
	private EditText et_taskName;
	private EditText et_repeat;
	private EditText et_timeOut;
	private EditText et_noAnswer;
	private EditText et_url;
	private EditText et_interVal;
	private Spinner et_showWeb; // 是否显示web
	/**
	 * 刷新类型和刷新深度布局容器
	 */
	private LinearLayout mLlRefreshSetting;
	/**
	 * http页面刷新类型下拉框，刷新类型	 1:刷新主页 2:深度刷新（打开子链接）
	 */
	private Spinner mSpinnerRefreshType;
	/**
	 * http页面刷新深度布局容器
	 */
	private RelativeLayout mRlRefreshDepth;
	/**
	 * http页面刷新深度，限制深度刷新页面进入的深度输入框
	 */
	private EditText mEtRefreshDepth;
	private Spinner sp_ap;
	private Spinner et_disConnect;
	private Spinner et_httpTestMode;
	private EditText parallel_timeout;
	private TextView tv_timeOut;
	private TextView tv_not_url;
	private TextView tv_taskName;
	public ArrayList<UrlModel> urlList; // 存储url地址
	private TaskRabModel taskRabModel;
	private final static int IS_LOGIN_REFRESH = 0; // 区分登录与刷新 0为登录 非0为刷新
	private Spinner dataConnectType;// 数据连接选择：PPP or WIFI
	private RelativeLayout rab_time_layout;
	private RelativeLayout rab_rule_time_layout;
	private LinearLayout wifiTestLayout;
	private RelativeLayout userNameLayout;
	private Button wifiSSIDET;
	private EditText wifiUserET;
	private EditText wifiPasswordET;
	private Context context = TaskHttpPage.this;
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
		setContentView(R.layout.task_http_page);
		(initRelativeLayout(R.id.advanced_arrow_rel)).setOnClickListener(this);
		RelativeLayout ra_http_url = (initRelativeLayout(R.id.ra_http_utl));
		(initImageView(R.id.pointer)).setOnClickListener(this);
		(initTextView(R.id.title_txt)).setText(R.string.act_task_httppage);// 设置标题
		tv_taskName = initTextView(R.id.txt_taskname);
		TextView tv_repeat = initTextView(R.id.txt_repeat);
		tv_timeOut = initTextView(R.id.txt_timeOut);
		TextView tv_httpTestMode = initTextView(R.id.txt_httpTestMode);
		TextView tv_noAnswer = initTextView(R.id.txt_noAnswer);
		TextView tv_url = initTextView(R.id.txt_url);
		tv_not_url = initTextView(R.id.txt_not_url);
		TextView tv_interVal = initTextView(R.id.txt_interVal);
		TextView tv_showweb = initTextView(R.id.txt_showWeb);
		TextView tv_disConnect = initTextView(R.id.txt_disConnect);
		Button btn_ok = initButton(R.id.btn_ok);
		Button btn_cencle = initButton(R.id.btn_cencle);
		parallel_timeout = initEditText(R.id.http_parallel_timeout_id);
		tv_taskName.setText(getString(R.string.task_taskName));
		tv_repeat.setText(getString(R.string.task_repeat));
		tv_timeOut.setText(getString(R.string.task_loginTimeOut));
		tv_noAnswer.setText(getString(R.string.task_noAnswer));
		tv_httpTestMode.setText(getString(R.string.task_http_testmode));
		tv_url.setText(getString(R.string.task_http_url));
		mLlRefreshSetting = initLinearLayout(R.id.ll_http_refresh_setting);
		mSpinnerRefreshType = initSpinner(R.id.spinner_http_refresh_type);
		mRlRefreshDepth = initRelativeLayout(R.id.rl_http_refresh_depth);
		mEtRefreshDepth = initEditText(R.id.et_http_refresh_depth);
		if (!isNew) {
			if (model.getUrlModelList().size() != 0) {
				tv_not_url.setText(getString(R.string.task_http_selected_url));
				tv_not_url.setTextColor(getResources().getColor(R.color.app_main_text_color));
			} else {
				tv_not_url.setText(getString(R.string.task_http_not_url));
				tv_not_url.setTextColor(getResources().getColor(R.color.red));
			}
		} else {
			tv_not_url.setText(getString(R.string.task_http_not_url));
			tv_not_url.setTextColor(getResources().getColor(R.color.red));
		}
		tv_interVal.setText(getString(R.string.task_interVal));
		tv_disConnect.setText(getString(R.string.task_disConnect));
		tv_showweb.setText(getString(R.string.task_webshow));
		btn_ok.setText(" " + getString(R.string.str_save) + " ");
		btn_cencle.setText(getString(R.string.str_cancle));
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
				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(TaskHttpPage.this,
						rabAblTimeEdt.getText().toString());
				dateTimePicKDialog.dateTimePicKDialog(rabAblTimeEdt);
			}
		});
		et_taskName = initEditText(R.id.edit_taskname);
		et_repeat = initEditText(R.id.edit_repeat);
		et_timeOut = initEditText(R.id.edit_timeOut);
		et_noAnswer = initEditText(R.id.edit_noAnswer);
		// et_url =initEditText(R.id.edit_url);
		et_interVal = initEditText(R.id.edit_interVal);
		et_showWeb = initSpinner(R.id.edit_showWeb);
		et_disConnect = initSpinner(R.id.edit_disConnect);
		et_httpTestMode = initSpinner(R.id.edit_httpTestMode);
		// final Spinner sp_proxy =initSpinner(R.id.SpinnerProxy);
		// final Spinner sp_downpic =initSpinner(R.id.SpinnerDownPicture);
		// final EditText et_address =initEditText(R.id.EditTextAdd);
		// final EditText et_port =initEditText(R.id.EditTextPort);
		// final EditText et_user =initEditText(R.id.EditTextUser);
		// final EditText et_pass =initEditText(R.id.EditTextPass);
		ArrayAdapter<String> showApter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.public_yn));
		showApter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		et_showWeb.setAdapter(showApter);
		// sp_proxy.setAdapter(showApter);
		// sp_downpic.setAdapter(showApter);
		// 断开网络配置
		ArrayAdapter<String> disconnect = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_task_disconnect));
		disconnect.setDropDownViewResource(R.layout.spinner_dropdown_item);
		et_disConnect.setAdapter(disconnect);
		// http测试模式
		ArrayAdapter<String> httpTestMode = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_http_testmode));
		httpTestMode.setDropDownViewResource(R.layout.spinner_dropdown_item);
		et_httpTestMode.setAdapter(httpTestMode);
		/**
		 * 处理点击切换登录/刷新时修改登录超时与刷新超时的text
		 */
		et_httpTestMode.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				//当测试模式选择刷新时，高级设置里刷新类型刷新深度才可见
				mLlRefreshSetting.setVisibility(arg2 == TaskHttpPageModel.LOGIN ? View.GONE : View.VISIBLE);
				if (arg2 == TaskHttpPageModel.LOGIN) {
					tv_timeOut.setText(getString(R.string.task_loginTimeOut)); // 登录超时
					if (!isNew && model.getTaskType().equals("Http")) {
						et_taskName.setText(model.getTaskName().toString().trim().substring(
								model.getTaskName().indexOf("%") + 1, model.getTaskName().toString().trim().length()));
					} else {
						et_taskName.setText("HTTP Logon");
					}
				} else {
					tv_timeOut.setText(getString(R.string.task_refreshTimeOut)); // 刷新超时
					if (!isNew && model.getTaskType().equals("HttpRefurbish")) {
						et_taskName.setText(model.getTaskName().toString().trim().substring(
								model.getTaskName().indexOf("%") + 1, model.getTaskName().toString().trim().length()));
					} else {
						et_taskName.setText("HTTP Refresh");
					}
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		//http刷新类型 1:刷新主页 2:深度刷新（打开子链接）
		ArrayAdapter<String> refreshTypeApter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_http_refresh_type));
		refreshTypeApter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		mSpinnerRefreshType.setAdapter(refreshTypeApter);
		//刷新类型下拉框选择监听器
		mSpinnerRefreshType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
				mRlRefreshDepth.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				//no-op
			}
		});
		// wifi support
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
						ApSelectAdapter adapter = new ApSelectAdapter(list);
						openDialog(adapter, list, -1, wifiSSIDET);
					} else {
						ToastUtil.showToastShort(context, getString(R.string.sys_wifi_aplist) + "");
					}
				}
			}
		});
		if (model != null) {
			et_taskName.setText(model.getTaskName() + "");
			et_repeat.setText(String.valueOf(model.getRepeat()));
			et_timeOut.setText(String.valueOf(model.getTimeOut()));
			et_noAnswer.setText(String.valueOf(model.getReponse()));
			et_interVal.setText(String.valueOf(model.getInterVal()));
			et_showWeb.setSelection(model.isShowWeb() ? 1 : 0);
			et_disConnect.setSelection(model.getDisConnect());
			et_httpTestMode.setSelection(model.getTaskType().equals(WalkStruct.TaskType.HttpRefurbish.name()) ? 1 : 0);
			mSpinnerRefreshType.setSelection(model.getHttpRefreshType() == TaskHttpPageModel.REFRESH_TYPE_DEEPLY ? 1:0 );
			mLlRefreshSetting.setVisibility(model.getHttpTestMode() == TaskHttpPageModel.REFRESH ? View.VISIBLE : View.GONE);
			mRlRefreshDepth.setVisibility(model.getHttpRefreshType() == TaskHttpPageModel.REFRESH_TYPE_DEEPLY ? View.VISIBLE : View.GONE);
			mEtRefreshDepth.setText(String.valueOf(model.getHttpRefreshDepth()));
			dataConnectType.setSelection(model.getTypeProperty() == 4 ? 1 : 0); // ppp
																				// 0
																				// wifi
																				// 1
			parallel_timeout.setText(String.valueOf(model.getParallelTimeout()));
		} else {
			et_taskName.setText("HTTP Refresh");
			et_repeat.setText("10");
			et_timeOut.setText("60");
			et_noAnswer.setText("20");
			et_interVal.setText("15");
			et_disConnect.setSelection(1);
			parallel_timeout.setText("0");
		}
		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveTestTask();
			}
		});
		btn_cencle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TaskHttpPage.this.finish();
			}
		});
		tv_not_url.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TaskHttpPage.this, SysURL.class); // 跳转到url列表
				intent.putExtra("taskname", et_taskName.getText().toString().trim());
				if (!isNew) {
					if (urlList == null) {
						intent.putExtra("urlModel", model.getUrlModelList());
					} else {
						intent.putExtra("urlModel", urlList);
					}
				} else {
					if (urlList == null) {
						urlList = new ArrayList<UrlModel>();
					}
					intent.putExtra("urlModel", urlList);
				}
				TaskHttpPage.this.startActivityForResult(intent, 8);
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}
		});
		// 数据连接类型
		dataConnectType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0||position == 2) {// PPP测试
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
		if (null != model) {
			if (model.getTypeProperty() == WalkCommonPara.TypeProperty_Wlan
					|| model.getNetworkConnectionSetting().isConnectionUseWifi()) {
				dataConnectType.setSelection(1);
				wifiTestLayout.setVisibility(View.VISIBLE);
				String[] params = model.getNetworkConnectionSetting().getWifiParam();
				String apName = params[0];
				wifiSSIDET.setText(apName);
				if (apName.equals("ChinaNet") || apName.equals("ChinaUnicom") || apName.contains("CMCC-WEB")
						|| apName.contains("CMCC")) {
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
	@Override
	public void onResume() {
		super.onResume();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	/*
	 * <p>Title: onActivityResult</p> <p>Description: </p>
	 *
	 * @param requestCode
	 *
	 * @param resultCode
	 *
	 * @param data
	 *
	 * @see com.walktour.framework.ui.BasicActivity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 8) {
			urlList = (ArrayList<UrlModel>) data.getExtras().get("backUrlModelList");
			if (urlList.size() == 0) {
				tv_not_url.setText(getString(R.string.task_http_not_url));
				tv_not_url.setTextColor(getResources().getColor(R.color.red));
			} else {
				tv_not_url.setText(getString(R.string.task_http_selected_url));
				tv_not_url.setTextColor(getResources().getColor(R.color.app_main_text_color));
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
				if (sr.SSID.equals("ChinaNet") || sr.SSID.equals("ChinaUnicom") || sr.SSID.contains("CMCC-WEB")
						|| sr.SSID.contains("CMCC")) {
					userNameLayout.setVisibility(View.VISIBLE);
				} else {
					userNameLayout.setVisibility(View.GONE);
				}
				dialog.dismiss();
			}
		});
		builder.show();
	}
	@SuppressLint("StringFormatMatches")
	@Override
	public void saveTestTask() {
		if (et_taskName.getText().toString().trim().length() == 0) {
			ToastUtil.showToastShort(context, R.string.task_alert_nullName);
			return;
		} else if (et_repeat.getText().toString().trim().length() == 0) {
			ToastUtil.showToastShort(context, R.string.task_alert_nullRepeat);
			return;
		} else if (et_timeOut.getText().toString().trim().length()==0
				|| !StringUtil.isRange(Integer.parseInt(et_timeOut.getText().toString()), 5,300)) {
			if(et_timeOut.getText().toString().trim().length()==0){
				ToastUtil.showToastShort(context, R.string.task_alert_nullTimeout);
				et_timeOut.setError(getString(R.string.task_alert_nullTimeout));
			}else{
				ToastUtil.showToastShort(context,getString(R.string.task_loginTimeOut)+","+String.format(getString(R.string.share_project_devices_release_relation_9), 5,300));
				et_timeOut.setError(getString(R.string.task_loginTimeOut)+","+String.format(getString(R.string.share_project_devices_release_relation_9), 5,300));
			}
			return;
		} else if (parallel_timeout.getText().toString().trim().length() == 0) {
			ToastUtil.showToastShort(context, R.string.task_alert_nullTimeout);
			return;
		} else if (et_interVal.getText().toString().trim().equals("0")
				|| et_interVal.getText().toString().trim().length() == 0) {
			ToastUtil.showToastShort(context, R.string.task_alert_nullInterval);
			return;
		} else if (!isNew && (model.getUrlModelList().size() == 0 || model.getUrlModelList() == null)) {
			ToastUtil.showToastShort(context, R.string.task_http_ref_url);
			return;
		} else if (urlList != null ? urlList.size() == 0 : false) {
			ToastUtil.showToastShort(context, R.string.task_http_ref_url);
			return;
		} else if (isNew && urlList == null) {
			ToastUtil.showToastShort(context, R.string.task_http_ref_url);
			return;
		}
		if (model == null) {
			model = new TaskHttpPageModel(WalkStruct.TaskType.Http.name());
			taskd.setCurrentTaskIdAndSequence(model);
		}
		model.setTaskName(et_taskName.getText().toString().trim());
		if (et_httpTestMode.getSelectedItemPosition() == IS_LOGIN_REFRESH) { // 区分测试类型
			model.setTaskType(WalkStruct.TaskType.Http.name());
		} else {
			model.setTaskType(WalkStruct.TaskType.HttpRefurbish.name());
		}
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(
				et_repeat.getText().toString().trim().length() == 0 ? "10" : et_repeat.getText().toString().trim()));
		model.setTimeOut(Integer.parseInt(
				et_timeOut.getText().toString().trim().length() == 0 ? "60" : et_timeOut.getText().toString().trim()));
		model.setReponse(Integer.parseInt(et_noAnswer.getText().toString().trim().length() == 0 ? "20"
				: et_noAnswer.getText().toString().trim()));
		// model.setUrl(et_url.getText().toString().trim().length()==0?"http://":et_url.getText().toString().trim());
		model.setInterVal(Integer.parseInt(et_interVal.getText().toString().trim().length() == 0 ? "15"
				: et_interVal.getText().toString().trim()));
		model.setDisConnect(et_disConnect.getSelectedItemPosition());
		model.setShowWeb(et_showWeb.getSelectedItemPosition() == 1);
		model.setHttpTestMode(et_httpTestMode.getSelectedItemPosition());
		model.setHttpRefreshType(mSpinnerRefreshType.getSelectedItemPosition() == 0 ? TaskHttpPageModel.REFRESH_TYPE_HOME_PAGE : TaskHttpPageModel.REFRESH_TYPE_DEEPLY);
		model.setHttpRefreshDepth(TextUtils.isEmpty(mEtRefreshDepth.getText().toString().trim()) ? 1 : Integer.parseInt(mEtRefreshDepth.getText().toString().trim()));
		model.setParallelTimeout(Integer.valueOf(parallel_timeout.getText().toString().trim()));
		model.setRabRelTime(super.rabRelTimeEdt.getText().toString().trim().length() == 0 ? "50"
				: super.rabRelTimeEdt.getText().toString().trim());
		model.setRabRuelTime(super.rabAblTimeEdt.getText().toString().trim().length() == 0 ? "12:00"
				: super.rabAblTimeEdt.getText().toString().trim());
		if (((Long) dataConnectType.getSelectedItemId()).intValue() == 1) {
			model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
			model.getNetworkConnectionSetting().updateWifiParam(wifiSSIDET.getText().toString() + "",
					wifiUserET.getText().toString() + "", wifiPasswordET.getText().toString() + "");
		} else if(((Long) dataConnectType.getSelectedItemId()).intValue() == 2){//NBPPP
			model.setTypeProperty(WalkCommonPara.TypeProperty_Ppp);
			model.getNetworkConnectionSetting().setConnectionUseWifi(false);
		} else{//PPP
			model.setTypeProperty(WalkCommonPara.TypeProperty_Net);
			model.getNetworkConnectionSetting().setConnectionUseWifi(false);
		}
		if (isNew) {
			model.setUrlModelList(urlList);
		} else {
			if (urlList == null) { // 判断设置的url对象有没变化。
				if (model.getUrlModelList().size() == 0 || model.getUrlModelList() == null) {
					ToastUtil.showToastShort(context, R.string.task_http_ref_url);
					return;
				}
				model.setUrlModelList(model.getUrlModelList());
			} else {
				model.setUrlModelList(urlList);
			}
		}

		/*
		 * model.setHasProxy( sp_proxy.getSelectedItemPosition()==1 );
		 * model.setDownPicture(sp_downpic.getSelectedItemPosition() == 1);
		 * model.setAddress(
		 * et_address.getText().toString().trim().length()==0?"http://":
		 * et_address.getText().toString().trim() ); model.setPort(
		 * et_port.getText().toString().trim().length()==0? 80:Integer.parseInt(
		 * et_port.getText().toString().trim() ) ); model.setUser(
		 * et_user.getText().toString() ); model.setPass(
		 * et_pass.getText().toString() );
		 */
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
		ToastUtil.showToastShort(context, isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess);
		TaskHttpPage.this.finish();
	}
}
