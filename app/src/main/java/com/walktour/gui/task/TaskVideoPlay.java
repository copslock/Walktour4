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
import android.widget.CheckBox;
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
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.videoplay.StringSpecialInit;
import com.walktour.gui.task.parsedata.model.task.videoplay.TaskVideoPlayModel;

import java.util.List;

/***
 * Video Play业务模板 (HTTP VS)
 *
 * @author weirong.fan
 *
 */
public class TaskVideoPlay extends BaseTaskActivity implements OnItemSelectedListener {
	TaskListDispose taskd = null;
	TaskVideoPlayModel model = null;
	private int taskListId = -1;
	private boolean isNew = true;

	private EditText taskName;
	private EditText repeat;
	private Spinner playerType;
	/** 视频源类型 */
	private Spinner streamType;
	private Spinner videoQuality;
	private EditText url;
	private Spinner playType;
	private LinearLayout show1;
	private LinearLayout show2;

	private Spinner playTimerMode;
	private EditText playTimeout;// 需要随着选项的变动，内容变动
	private EditText maxBufCounts;
	private Spinner bufTimerMode;
	private EditText maxBufTime;

	private EditText interval;
	private EditText noDataTimeout;
	private EditText bufTime;
	private EditText bufThred;
	private CheckBox isSave;
	private CheckBox isShow;
	private Spinner disConnect;
	private TextView txtPlayTimeout;
	private TextView txtMaxBufTime;
	private ArrayAdapter<String> videoQualityAdapter;

	private Button save;// 保存
	private Button cancel;// 取消
	private TaskRabModel taskRabModel; // 并发对象
	private Spinner dataConnectType;// 数据连接选择：PPP or WIFI
	private LinearLayout wifiTestLayout;
	private RelativeLayout userNameLayout;
	private Button wifiSSIDET;
	private EditText wifiUserET;
	private EditText wifiPasswordET;
	private RelativeLayout rab_time_layout;
	private RelativeLayout rab_rule_time_layout;
	private Context context = TaskVideoPlay.this;
	private LayoutInflater inflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_videoplay);
		StringSpecialInit.getInstance().setmContext(this);
		// 设置标题名字
		(initTextView(R.id.title_txt)).setText(getResources().getString(R.string.act_task_videoplay));
		(initImageView(R.id.pointer)).setOnClickListener(this);
		inflater = LayoutInflater.from(this);
		taskd = TaskListDispose.getInstance();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("taskListId")) {
			taskListId = bundle.getInt("taskListId");
			if (RABTAG.equals(super.getRabTag())) {
				for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
					if (taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())) {
						model = (TaskVideoPlayModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel()
								.get(taskListId);
						break;
					}
				}
			} else {
				model = (TaskVideoPlayModel) taskd.getTaskListArray().get(taskListId);
			}
			abstModel = model;
			isNew = false;
		}
		showViews();
		setValue();

		(initRelativeLayout(R.id.advanced_arrow_rel)).setOnClickListener(this);
		save = initButton(R.id.btn_ok);
		cancel = initButton(R.id.btn_cencle);

		save.setOnClickListener(this);
		cancel.setOnClickListener(this);
		// 可编辑框内容改变监听
		addEditTextWatcher();
	}

	/**
	 * 添加EditText输入监听限制<BR>
	 */
	public void addEditTextWatcher() {
		taskName.addTextChangedListener(etWatcher);
		repeat.addTextChangedListener(etWatcher);
		url.addTextChangedListener(etWatcher);
		playTimeout.addTextChangedListener(etWatcher);
		maxBufCounts.addTextChangedListener(etWatcher);
		maxBufTime.addTextChangedListener(etWatcher);

		interval.addTextChangedListener(etWatcher);
		noDataTimeout.addTextChangedListener(etWatcher);
		bufTime.addTextChangedListener(etWatcher);
		bufThred.addTextChangedListener(etWatcher);
	}

	/**
	 * EditTextWatcher具体实现
	 */
	private EditTextWatcher etWatcher = new EditTextWatcher() {
		@SuppressLint("StringFormatMatches")
		public void afterTextChanged(android.text.Editable s) {
			View view = getCurrentFocus();
			if (view == taskName) {
				if (s.length() < 1)
					taskName.setError(getString(R.string.task_alert_nullName));
				else
					taskName.setError(null);
			} else if (view == repeat && StringUtil.isLessThanZeroInteger(repeat.getText().toString().trim())) {
				repeat.setError(getString(R.string.task_alert_nullInut));
			} else if (view == url) {
				if (s.length() < 1)
					url.setError(getString(R.string.task_alert_nullUrl));
				else
					url.setError(null);
			} else if (view == interval && StringUtil.isLessThanZeroInteger(interval.getText().toString().trim())) {
				interval.setError(getString(R.string.task_alert_nullInterval));
			} else if (view == noDataTimeout) {
				if (StringUtil.isEmpty(noDataTimeout.getText().toString())) {
					noDataTimeout.setError(getString(R.string.task_alert_nullnodatatimeout));
				} else if (parseInt(noDataTimeout) < 5 || parseInt(noDataTimeout) > 120) {
					noDataTimeout.setError(String.format(getString(R.string.alert_input_toolong, 5, 120)));
				}
			} else if (view == playTimeout) {
				if (StringUtil.isEmpty(playTimeout.getText().toString())) {
					playTimeout.setError(getString(R.string.task_alert_nullInut));
				} else if (playTypeValue == 0 && (parseInt(playTimeout) < 5 || parseInt(playTimeout) > 99999)) {
					playTimeout.setError(String.format(getString(R.string.alert_input_toolong, 5, 99999)));
				} else if (playTypeValue == 1) {// 按时长方式
					if (playTimerMode.getSelectedItemPosition() == 1
							&& (parseInt(playTimeout) < 5 || parseInt(playTimeout) > 100))
						playTimeout.setError(String.format(getString(R.string.alert_input_toolong, 5, 100)));
					else if (parseInt(playTimeout) < 5 || parseInt(playTimeout) > 99999)
						playTimeout.setError(String.format(getString(R.string.alert_input_toolong, 5, 99999)));
				}
			} else if (view == maxBufCounts && StringUtil.isEmpty(maxBufCounts.getText().toString())) {// 最大缓冲次数
				maxBufCounts.setError(getString(R.string.task_alert_nullInut));
			} else if (view == maxBufCounts && (parseInt(maxBufCounts) < 1 || parseInt(maxBufCounts) > 99999)) {
				maxBufCounts.setError(String.format(getString(R.string.alert_input_toolong, 1, 99999)));
			} else if (view == maxBufTime) {
				if (StringUtil.isEmpty(maxBufTime.getText().toString())) {
					maxBufTime.setError(getString(R.string.task_alert_nullInut));
				} else if (bufTimerModeValue == 0 && (parseInt(maxBufTime) < 5 || parseInt(maxBufTime) > 99999)) {
					maxBufTime.setError(String.format(getString(R.string.alert_input_toolong, 5, 99999)));
				} else if (bufTimerModeValue == 1 && (parseInt(maxBufTime) < 5 || parseInt(maxBufTime) > 100)) {
					maxBufTime.setError(String.format(getString(R.string.alert_input_toolong, 5, 100)));
				}
			} else if (view == bufTime && s.length() < 1) {
				bufTime.setError(getString(R.string.task_alert_nullInut));
			} else if (view == bufThred) {
				if (s.length() < 1) {
					bufThred.setError(getString(R.string.task_alert_nullInut));
				} else if (parseInt(bufTime) < parseInt(bufThred)) {
					bufThred.setError(getString(R.string.task_alert_bufThredmorethanbuftime));
				}
			}
		};
	};

	private void showViews() {
		playerType = initSpinner(R.id.edit_playertype);
		ArrayAdapter<String> playerTypeAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_player_type));
		playerTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		playerType.setAdapter(playerTypeAdapter);
		playerType.setOnItemSelectedListener(this);

		streamType = initSpinner(R.id.edit_streamtype);
		ArrayAdapter<String> streamTypeAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_stream_type));
		streamTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		streamType.setAdapter(streamTypeAdapter);

		videoQuality = initSpinner(R.id.edit_videoquality);
		videoQualityAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_video_quality));

		videoQualityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		videoQuality.setAdapter(videoQualityAdapter);

		playType = initSpinner(R.id.edit_playtype);
		ArrayAdapter<String> playTypeAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_ftpdownload_testmode));
		playTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		playType.setAdapter(playTypeAdapter);

		txtPlayTimeout = initTextView(R.id.txt_playtimeout);
		show1 = initLinearLayout(R.id.bytime_1);
		show2 = initLinearLayout(R.id.bytime2);

		taskName = initEditText(R.id.edit_taskname);
		repeat = initEditText(R.id.edit_repeat);

		url = initEditText(R.id.edit_url);
		playTimerMode = initSpinner(R.id.edit_playtimermode);
		ArrayAdapter<String> playTimerModeAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_timer_mode));
		playTimerModeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		playTimerMode.setAdapter(playTimerModeAdapter);
		playTimerMode.setOnItemSelectedListener(this);

		playTimeout = initEditText(R.id.edit_playtimeout);
		maxBufCounts = initEditText(R.id.edit_maxbufcounts);
		txtMaxBufTime = initTextView(R.id.txt_maxbuftime);
		bufTimerMode = initSpinner(R.id.edit_buftimermode);
		ArrayAdapter<String> bufTimerModeAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_timer_mode));
		bufTimerModeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		bufTimerMode.setAdapter(bufTimerModeAdapter);

		maxBufTime = initEditText(R.id.edit_maxbuftime);

		interval = initEditText(R.id.edit_interval);
		noDataTimeout = initEditText(R.id.edit_nodatatimeout);
		bufTime = initEditText(R.id.edit_bufTime);
		bufThred = initEditText(R.id.edit_bufThred);
		isSave = (CheckBox) findViewById(R.id.edit_save);
		isShow = (CheckBox) findViewById(R.id.edit_show);
		disConnect = initSpinner(R.id.edit_disConnect);
		ArrayAdapter<String> disconnectAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_task_disconnect));
		disconnectAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		disConnect.setAdapter(disconnectAdapter);

		// wifi support
		dataConnectType = initSpinner(R.id.edit_data_connect_type);
		setDataConnectTypeSP(dataConnectType);

		ArrayAdapter<String> dataConnectTypeAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				ApplicationModel.getInstance().getConnectType());
		dataConnectTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		dataConnectType.setAdapter(dataConnectTypeAdapter);
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

		playType.setOnItemSelectedListener(this);
		bufTimerMode.setOnItemSelectedListener(this);
		// wifi support
		// dataConnectType = initSpinner(R.id.edit_data_connect_type);
		// super.setDataConnectTypeSP(dataConnectType);
		// 并发相对时间
		rab_time_layout = initRelativeLayout(R.id.rab_time_layout);
		rab_rule_time_layout = initRelativeLayout(R.id.rab_time_rel_layout);
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
				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(TaskVideoPlay.this,
						rabAblTimeEdt.getText().toString());
				dateTimePicKDialog.dateTimePicKDialog(rabAblTimeEdt);
			}
		});

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

	int playTimerModeValue;// 0按时长，1为按比例
	int playTypeValue;// 0为按时间，1为按文件
	int bufTimerModeValue;// 缓冲记时方式

	private void setDataConnectTypeSP(Spinner dataConnectTypeSP) {
		if (dataConnectTypeSP != null) {
			if (showInfoList.contains(WalkStruct.ShowInfoType.WLAN)) {
				ArrayAdapter<String> dataConnectTypeAdapter = new ArrayAdapter<String>(this,
						R.layout.simple_spinner_custom_layout, ApplicationModel.getInstance().getConnectType());
				dataConnectTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
				dataConnectTypeSP.setAdapter(dataConnectTypeAdapter);
			} else {
				findViewById(R.id.task_wifi_app_choice).setVisibility(View.GONE);

				findViewById(R.id.task_wifi_test_choice).setVisibility(View.GONE);
			}

		}
	}

	@Override
	public void saveTestTask() {
		if (StringUtil.isEmpty(taskName.getText().toString())) { // 任务名为空
			showToast(R.string.task_alert_nullName);
			return;
		} else if (StringUtil.isEmpty(repeat.getText().toString()) || "0".equals(repeat.getText().toString().trim())) {
			showToast(R.string.task_alert_nullRepeat);
			return;
		} else if (StringUtil.isEmpty(url.getText().toString())) {
			showToast(R.string.task_alert_nullUrl);
			return;
		} else if (StringUtil.isLessThanZeroInteger(interval.getText().toString().trim())) {
			showToast(R.string.task_alert_nullInterval);
			return;
		} else if (parseInt(noDataTimeout) < 5 || parseInt(noDataTimeout) > 120) {
			showToast(R.string.task_alert_nodatatimeout_between);
			return;
		} else if (StringUtil.isEmpty(bufTime.getText().toString())) {
			showToast(R.string.task_alert_nullbufTime);
			return;
		} else if (StringUtil.isEmpty(bufThred.getText().toString())) {
			showToast(R.string.task_alert_nullbufThred);
			return;
		} else if (parseInt(bufTime) < parseInt(bufThred)) {
			showToast(R.string.task_alert_bufThredmorethanbuftime);
			return;
		} else {
		}

		if (playTypeValue == 0) {// 按文件
			if (StringUtil.isEmpty(playTimeout.getText().toString())) {
				showToast(R.string.task_alert_nullrecvtimeout);
				return;
			} else if (parseInt(playTimeout) < 5) {// 按文件
				showToast(R.string.task_alert_recvtimeout_error);
				return;
			}
		} else {// 按时间
			if (StringUtil.isEmpty(playTimeout.getText().toString())) {// 播放时长空验证
				showToast(playTimerModeValue == 0 ? R.string.task_alert_nullplaytime : R.string.task_alert_nullplayscale);
				return;
			} else if (parseInt(playTimeout) < 5) {// 播放时长或播放比例小于5验证
				showToast(playTimerModeValue == 0 ? R.string.task_alert_playtime_error : R.string.task_alert_playscale_error);
				return;
			} else if (playTimerModeValue == 1 && parseInt(playTimeout) > 100) {// 播放计时方式按比例
				showToast(R.string.task_alert_playscalemax_error);
				return;
			} else if (StringUtil.isEmpty(maxBufCounts.getText().toString())) {// 最大缓冲次数
				showToast(R.string.task_alert_nullmaxbufcounts);
				return;
			} else if (parseInt(maxBufCounts) < 1) {// 最大缓冲次数
				showToast(R.string.task_alert_nullmaxbufcounts_error);
				return;
			} else if (StringUtil.isEmpty(maxBufTime.getText().toString())) {// 最大缓冲时长或比例
				showToast(bufTimerModeValue == 0 ? R.string.task_alert_nullmaxbuftime : R.string.task_alert_nullmaxbufscale);
				return;
			} else if (parseInt(maxBufTime) < 5) {
				showToast(playTimerModeValue == 0 ? R.string.task_alert_nullmaxbuftime_error
						: R.string.task_alert_nullmaxbufscale_error);
				return;
			} else if (bufTimerModeValue == 1 && parseInt(maxBufTime) > 100) {
				showToast(R.string.task_alert_nullmaxbufscalemax_error);
				return;
			}
		}

		setValueForModel();
		model.setEnable(1);
		model.setTaskType(WalkStruct.TaskType.HTTPVS.name());
		int enableLength = taskd.getTaskNames(1).length;
		model.setRabRelTime(super.rabRelTimeEdt.getText().toString().trim().length() == 0 ? "50"
				: super.rabRelTimeEdt.getText().toString().trim());
		model.setRabRuelTime(super.rabAblTimeEdt.getText().toString().trim().length() == 0 ? "12:00"
				: super.rabAblTimeEdt.getText().toString().trim());
		if (((Long) dataConnectType.getSelectedItemId()).intValue() == 1) // 只有是WIFI的才设置
			model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan); //
		else
			model.setTypeProperty(WalkCommonPara.TypeProperty_Net); // ppp

		if (((Long) dataConnectType.getSelectedItemId()).intValue() == 1) {
			model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
			model.getNetworkConnectionSetting().updateWifiParam(wifiSSIDET.getText().toString() + "",
					wifiUserET.getText().toString() + "", wifiPasswordET.getText().toString() + "");
		} else {
			model.getNetworkConnectionSetting().setConnectionUseWifi(false);
			model.setTypeProperty(WalkCommonPara.TypeProperty_Net); // ppp
		}
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
		} else {
			if (isNew) {
				array.add(array.size(),model);
			} else {
				array.remove(taskListId);
				array.add(taskListId, model);
			}
		}
		taskd.setTaskListArray(array);
		ToastUtil.showToastShort(getApplicationContext(),
				isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess);
		TaskVideoPlay.this.finish();
	}

	/**
	 * 将Activity中的值设置到Model中
	 */
	private void setValueForModel() {

		model.setTaskName(taskName.getText().toString().trim());
		taskName.setText(model.getTaskName());
		model.setRepeat(parseInt(repeat));
		model.setPlayerType(playerType.getSelectedItemPosition() + 1);
		model.setStreamType(streamType.getSelectedItemPosition() + 1);
		model.setVideoQuality(videoQuality.getSelectedItemPosition());
		model.setUrl(url.getText().toString());
		model.setPlayType(playType.getSelectedItemPosition());
		model.setPlayTimeout(parseInt(playTimeout));

		model.setPlayTimerMode(playTimerModeValue);
		model.setMaxBufCounts(parseInt(maxBufCounts));
		model.setBufTimerMode(bufTimerModeValue);
		if(model.getBufTimerMode() == 0)
			model.setMaxBufferTimeout(parseInt(maxBufTime));
		else
			model.setMaxBufferPercentage(parseInt(maxBufTime));

		model.setInterVal(parseInt(interval));
		model.setNoDataTimeout(parseInt(noDataTimeout));
		model.setBufTime(parseInt(bufTime));
		model.setBufThred(parseInt(bufThred));
		model.setSave(isSave.isChecked());
		model.setVideoShow(isShow.isChecked());
		model.setDisConnect(disConnect.getSelectedItemPosition());
		this.setPlayTimeout(parseInt(playTimeout));
	}

	/**
	 * 设置播放超时
	 * @param playTimeout 播放超时
	 */
	private void setPlayTimeout(int playTimeout) {
		switch (model.getPlayType()) {
			case 0:
				model.setPlayTimeout(playTimeout);
				break;
			case 1:
				switch (model.getPlayTimerMode()) {
					case 0:
						model.setPlayDuration(playTimeout);
						break;
					case 1:
						model.setPlayPercentage(playTimeout);
						break;

					default:
						break;
				}
				break;
			default:
				break;
		}
	}

	/**
	 * 显示Toast提示
	 *
	 * @param strId
	 */
	private void showToast(int strId) {
		ToastUtil.showToastShort(TaskVideoPlay.this, strId);
	}

	private void setValue() {
		if (model == null) {
			model = new TaskVideoPlayModel();
			taskd.setCurrentTaskIdAndSequence(model);
		}

		if (!isNew) {
			taskName.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%") + 1,
					model.getTaskName().toString().trim().length()));
		} else {
			taskName.setText(model.getTaskName());
		}

		repeat.setText(String.valueOf(model.getRepeat()));
		playerType.setSelection(model.getPlayerType() - 1);
		streamType.setSelection(model.getStreamType() - 1);
		videoQuality.setSelection(model.getVideoQuality());
		url.setText(model.getUrl());
		playType.setSelection(model.getPlayType());
		playTimerMode.setSelection(model.getPlayTimerMode());
		playTimeout.setText(String.valueOf(this.getPlayTimeout()));
		maxBufCounts.setText(String.valueOf(model.getMaxBufCounts()));
		bufTimerMode.setSelection(model.getBufTimerMode());
		maxBufTime.setText(String.valueOf(model.getBufTimerMode() == 0 ? model.getMaxBufferTimeout() : model.getMaxBufferPercentage()));
		interval.setText(String.valueOf(model.getInterVal()));
		noDataTimeout.setText(String.valueOf(model.getNoDataTimeout()));
		bufTime.setText(String.valueOf(model.getBufTime()));
		bufThred.setText(String.valueOf(model.getBufThred()));
		isSave.setChecked(model.isSave());
		isShow.setChecked(model.isVideoShow());
		disConnect.setSelection(model.getDisConnect());

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
			} else {
				dataConnectType.setSelection(0);
				wifiTestLayout.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 获取播放超时设置
	 * @return 播放超时设置
	 */
	private int getPlayTimeout() {
		int playTime = 0;
		switch (model.getPlayType()) {
			case 0:
				return model.getPlayTimeout();
			case 1:
				switch (model.getPlayTimerMode()) {
					case 0:
						return model.getPlayDuration();
					case 1:
						return model.getPlayPercentage();
					default:
						break;
				}
				break;
			default:
				break;
		}
		return playTime;
	}

	/**
	 * EditText中输入的字符转int
	 *
	 * @param value
	 * @return
	 */
	private int parseInt(EditText value) {
		try {
			return Integer.parseInt(value.getText().toString());
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	private static final int BY_TYPE = 1;

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (parent == playType) {// 播放模式选择操作
			url.clearFocus();
			playTypeValue = position;
			model.setPlayType(position);
			if (position != BY_TYPE) {// 按文件
				show1.setVisibility(View.GONE);
				show2.setVisibility(View.GONE);
				txtPlayTimeout.setText(R.string.task_playTimeOut);
				playTimeout.setText(String.valueOf(model.getPlayTimeout()));
			} else {
				show1.setVisibility(View.VISIBLE);
				show2.setVisibility(View.VISIBLE);
				playTimeout.setText(getPlayTimeout()+"");//String.valueOf(model.getPlayTimeout())
				if (isNew) {
					maxBufCounts.setText(null);
					maxBufTime.setText(null);
					playTimeout.setText(null);
				}

				if (playTimerMode != null && playTimerMode.getSelectedItemPosition() != 0) {
					txtPlayTimeout.setText(R.string.playscale);
				} else {
					txtPlayTimeout.setText(R.string.playtime);
				}
			}

		}else if (parent == playTimerMode) {
			playTimerModeValue = position;
			model.setPlayTimerMode(position);
			if (position == 0) {
				txtPlayTimeout.setText(R.string.playtime);
			} else {
				txtPlayTimeout.setText(R.string.playscale);
			}
		} else if (parent == bufTimerMode) {
			bufTimerModeValue = position;
			if (position == 0) {
				txtMaxBufTime.setText(R.string.maxbuftime);
			} else {
				txtMaxBufTime.setText(R.string.maxbufscale);
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> view) {
	}

}
