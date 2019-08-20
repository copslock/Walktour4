package com.walktour.gui.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.Verify;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.mms.send.TaskMmsSendModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

import java.io.File;
import java.util.List;

public class TaskMmsSend extends BaseTaskActivity {
	TaskListDispose taskd = null;

	TaskMmsSendModel model = null;

	private int taskListId = -1;

	private boolean isNew = true;

	private EditText et_adjunct = null;

	private EditText mediaFileSizeEditText = null;

	private MyBroadcastReceiver mEventReceiver = new MyBroadcastReceiver();

	private EditText taskNameEditText;

	private EditText repeatEditText;

	private EditText timeOutEditText;

	private EditText serverAddressEditText;

	private EditText gatewayEditText;

	private EditText portEditText;

	private EditText interValEditText;

	private EditText destinationEditText;

	private EditText contentEditText;

	private TaskRabModel taskRabModel;

	private Spinner fileSource;

	private Spinner disConnectSpinner;

	private LinearLayout unionTestLayout;
	/**
	 * 主叫联合
	 */
	private Spinner unionSpinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		taskd = TaskListDispose.getInstance();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("taskListId")) {
			taskListId = bundle.getInt("taskListId");
			// 根据标记做并发编辑
			if (RABTAG.equals(super.getRabTag())) {
				for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
					if (taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())) {
						model = (TaskMmsSendModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel()
								.get(taskListId);
						break;
					}
				}
			} else {
				model = (TaskMmsSendModel) taskd.getTaskListArray().get(taskListId);
			}
			abstModel = model;
			isNew = false;
		}
		findView();
		regedit();
		addEditTextWatcher();
	}

	private void findView() {
		// 绑定Layout里面的ListView
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.task_mmssend, null);
		((TextView) textEntryView.findViewById(R.id.title_txt)).setText(R.string.act_task_mmssend);// 设置标题
		((ImageView) textEntryView.findViewById(R.id.pointer)).setOnClickListener(this);
		((RelativeLayout) textEntryView.findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);

		TextView tv_taskName = (TextView) textEntryView.findViewById(R.id.txt_taskname);
		TextView tv_repeat = (TextView) textEntryView.findViewById(R.id.txt_repeat);
		TextView tv_timeOut = (TextView) textEntryView.findViewById(R.id.txt_timeOut);
		TextView tv_serverAddress = (TextView) textEntryView.findViewById(R.id.txt_serverAddress);
		TextView tv_gateway = (TextView) textEntryView.findViewById(R.id.txt_gateway);
		TextView tv_port = (TextView) textEntryView.findViewById(R.id.txt_port);
		TextView tv_interVal = (TextView) textEntryView.findViewById(R.id.txt_interVal);
		TextView tv_destination = (TextView) textEntryView.findViewById(R.id.txt_destination);
		// TextView tv_subject
		// =(TextView)textEntryView.findViewById(R.id.txt_subject);
		TextView tv_content = (TextView) textEntryView.findViewById(R.id.txt_content);
		TextView tv_adjunct = (TextView) textEntryView.findViewById(R.id.txt_adjunct);
		TextView tv_mediaFileSize = (TextView) textEntryView.findViewById(R.id.txt_mediaFileSize);
		Button btn_view = (Button) textEntryView.findViewById(R.id.btn_view);
		Button btn_ok = (Button) textEntryView.findViewById(R.id.btn_ok);
		Button btn_cencle = (Button) textEntryView.findViewById(R.id.btn_cencle);

		tv_taskName.setText(getString(R.string.task_taskName));
		tv_repeat.setText(getString(R.string.task_repeat));
		tv_timeOut.setText(getString(R.string.task_sendTimeOut));
		tv_serverAddress.setText(getString(R.string.task_serverAddress));
		tv_gateway.setText(getString(R.string.task_gateway));
		tv_port.setText(getString(R.string.task_port));
		tv_interVal.setText(getString(R.string.task_interVal));
		tv_destination.setText(getString(R.string.task_receive_number));
		// tv_subject.setText(getString(R.string.task_subject));
		tv_content.setText(getString(R.string.task_mmsContent));
		tv_adjunct.setText(getString(R.string.task_adjunct));
		tv_mediaFileSize.setText(getString(R.string.task_fileSize));
		btn_ok.setText(" " + getString(R.string.str_save) + " ");
		btn_cencle.setText(getString(R.string.str_cancle));

		taskNameEditText = (EditText) textEntryView.findViewById(R.id.edit_taskname);
		repeatEditText = (EditText) textEntryView.findViewById(R.id.edit_repeat);
		timeOutEditText = (EditText) textEntryView.findViewById(R.id.edit_timeOut);
		serverAddressEditText = (EditText) textEntryView.findViewById(R.id.edit_serverAddress);
		gatewayEditText = (EditText) textEntryView.findViewById(R.id.edit_gateway);
		portEditText = (EditText) textEntryView.findViewById(R.id.edit_port);
		interValEditText = (EditText) textEntryView.findViewById(R.id.edit_interVal);
		destinationEditText = (EditText) textEntryView.findViewById(R.id.edit_destination);
		// final EditText et_subject
		// =(EditText)textEntryView.findViewById(R.id.edit_subject);
		contentEditText = (EditText) textEntryView.findViewById(R.id.edit_content);
		et_adjunct = (EditText) textEntryView.findViewById(R.id.edit_adjunct);
		mediaFileSizeEditText = (EditText) textEntryView.findViewById(R.id.edit_mediaFileSize);
		fileSource = (Spinner) textEntryView.findViewById(R.id.edit_fileSource);
		// final Spinner et_Capture =
		// (Spinner)textEntryView.findViewById(R.id.edit_Capture);
		// final Spinner et_protocol =
		// (Spinner)textEntryView.findViewById(R.id.edit_Protocol);
		ArrayAdapter<String> fileAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_httpupload_filesource));
		fileAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		fileSource.setAdapter(fileAdapter);

		// 主被叫联合
		unionTestLayout = (LinearLayout) textEntryView.findViewById(R.id.task_uniontest_layout);
		if (ConfigRoutine.getInstance().isBluetoothSync(getApplicationContext())) {
			unionTestLayout.setVisibility(View.VISIBLE);
		}
		unionSpinner = (Spinner) textEntryView.findViewById(R.id.edit_unionTest);
		ArrayAdapter<String> unionApter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.public_yn));
		unionApter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		unionSpinner.setAdapter(unionApter);

		// 断开网络配置
		disConnectSpinner = (Spinner) textEntryView.findViewById(R.id.edit_disConnect);
		ArrayAdapter<String> disconnect = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_task_disconnect));
		disconnect.setDropDownViewResource(R.layout.spinner_dropdown_item);
		disConnectSpinner.setAdapter(disconnect);

		if (model != null) {
			taskNameEditText.setText(model.getTaskName().toString().trim()
					.substring(model.getTaskName().indexOf("%") + 1, model.getTaskName().toString().trim().length()));
			repeatEditText.setText(String.valueOf(model.getRepeat()));
			timeOutEditText.setText(String.valueOf(model.getTimeOut()));
			serverAddressEditText.setText(String.valueOf(model.getServerAddress()));
			gatewayEditText.setText(String.valueOf(model.getGateway()));
			portEditText.setText(String.valueOf(model.getPort()));
			interValEditText.setText(String.valueOf(model.getInterVal()));
			destinationEditText.setText(String.valueOf(model.getDestination()));
			// et_subject.setText( String.valueOf(model.getSubject()));
			contentEditText.setText(String.valueOf(model.getContent()));
			et_adjunct.setText(String.valueOf(model.getAdjunct()));
			mediaFileSizeEditText.setText(String.valueOf(model.getMediaFileSize()));
			fileSource.setSelection(model.getFileSource());
			disConnectSpinner.setSelection(model.getDisConnect());
			unionSpinner.setSelection(model.isUnitTest() ? 1 : 0);
		} else {
			taskNameEditText.setText("MMS Send");
			repeatEditText.setText("1");
			serverAddressEditText.setText("http://");
			gatewayEditText.setText("10.0.0.172");
			portEditText.setText("80");
			timeOutEditText.setText("60");
			interValEditText.setText("10");
			fileSource.setSelection(1);
			disConnectSpinner.setSelection(1);
			// if(ConfigRoutine.getInstance().isBluetoothSync(getApplicationContext())){
			unionSpinner.setSelection(1);
			// }
			// et_subject.setText("MMS Test");
		}

		/**
		 * 文件选择模式
		 */
		fileSource.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2 == 0) {
					textEntryView.findViewById(R.id.view_mms).setVisibility(View.VISIBLE);
					textEntryView.findViewById(R.id.edit_adjunct).setEnabled(false);
					textEntryView.findViewById(R.id.filesize_mms).setVisibility(View.GONE);
				} else {
					EditText viewEdit = (EditText) textEntryView.findViewById(R.id.edit_adjunct);
					viewEdit.setText("");
					textEntryView.findViewById(R.id.view_mms).setVisibility(View.GONE);
					textEntryView.findViewById(R.id.filesize_mms).setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});

		btn_view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;
				intent = new Intent(TaskMmsSend.this, FileExplorer.class);
				// 添加传递参数
				Bundle bundle = new Bundle();
				bundle.putBoolean(FileExplorer.KEY_NORMAL, true);
				bundle.putString(FileExplorer.KEY_ACTION, FileExplorer.ACTION_LOAD_NORMAL_FILE);// 文件浏览类型
				bundle.putString(FileExplorer.KEY_EXTRA, FileExplorer.KEY_FILE);
				bundle.putLong(FileExplorer.KEY_FILE_SIZE, 300 * 1000);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveTestTask();
			}
		});

		btn_cencle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TaskMmsSend.this.finish();
			}
		});
		textEntryView.setVerticalScrollBarEnabled(true);
		setContentView(textEntryView);
	}

	@Override
	public void onResume() {
		super.onResume();
		// showView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mEventReceiver);
	}

	/**
	 * 注册广播接收器
	 */
	private void regedit() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(FileExplorer.ACTION_LOAD_NORMAL_FILE);
		mEventReceiver = new MyBroadcastReceiver();
		this.registerReceiver(mEventReceiver, filter);
	}

	/**
	 * 广播接收器:接收来自Fleet.java的广播更新界面
	 * */
	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(FileExplorer.ACTION_LOAD_NORMAL_FILE)) {
				String filePath = "";
				try {
					filePath = intent.getStringExtra(FileExplorer.KEY_FILE);
				} catch (Exception e) {

				}
				et_adjunct.setText(filePath);
				LogUtil.i("TaskMmsSend", filePath);
				File file = new File(filePath);
				if (file.exists()) {
					mediaFileSizeEditText
							.setText(String.valueOf((file.length() / 1000 < 1) ? 1 : file.length() / 1000));
				}
			}
		}

	}// end inner class EventBroadcastReceiver

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @see com.walktour.gui.task.BaseTaskActivity#saveTestTask()
	 */

	@Override
	public void saveTestTask() {
		if (taskNameEditText.getText().toString().trim().length() == 0) { // 任务名为空
			Toast.makeText(com.walktour.gui.task.TaskMmsSend.this.getApplicationContext(),
					R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
			taskNameEditText.setError(getString(R.string.task_alert_nullName));
			return;
		} else if (repeatEditText.getText().toString().trim().equals("0")
				|| repeatEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
			return;
		} else if (serverAddressEditText.getText().toString().trim().length() == 0) {// 服务中心地址为空
			Toast.makeText(TaskMmsSend.this,
					getString(R.string.task_alert_input) + " " + getString(R.string.task_serverAddress),
					Toast.LENGTH_SHORT).show();
			serverAddressEditText.setError(getString(R.string.task_alert_input) + " "
					+ getString(R.string.task_serverAddress));
			return;
		} else if (!Verify.isIpOrUrl(serverAddressEditText.getText().toString().trim())) {
			Toast.makeText(TaskMmsSend.this,
					getString(R.string.task_alert_input) + " " + getString(R.string.task_serverAddress),
					Toast.LENGTH_SHORT).show();
			serverAddressEditText.setError(getString(R.string.task_alert_input) + " "
					+ getString(R.string.task_serverAddress));
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
		} else if (destinationEditText.getText().toString().trim().length() == 0) { // 任务名为空
			Toast.makeText(com.walktour.gui.task.TaskMmsSend.this.getApplicationContext(),
					R.string.task_alert_nulldestination, Toast.LENGTH_SHORT).show();
			destinationEditText.setError(getString(R.string.task_alert_nulldestination));
			return;
		} else if (mediaFileSizeEditText.getText().toString().trim().length() == 0) {// 大小为空
			mediaFileSizeEditText.setError(getString(R.string.task_alert_nullInut) + getString(R.string.task_fileSize));
			Toast.makeText(TaskMmsSend.this,
					getString(R.string.task_alert_nullInut) + getString(R.string.task_fileSize), Toast.LENGTH_SHORT)
					.show();
			return;
		} else if (Integer.parseInt(mediaFileSizeEditText.getText().toString().trim()) > 300
				|| Integer.parseInt(mediaFileSizeEditText.getText().toString().trim()) < 0) {
			mediaFileSizeEditText.setError(getString(R.string.task_alert_nullInut) + "0~300");
			Toast.makeText(TaskMmsSend.this, getString(R.string.task_alert_nullInut) + "0~300", Toast.LENGTH_SHORT)
					.show();
			return;
		} else if (gatewayEditText.getText().toString().trim().length() == 0) {// 网关为空
			Toast.makeText(TaskMmsSend.this,
					getString(R.string.task_alert_input) + " " + getString(R.string.task_gateway), Toast.LENGTH_SHORT)
					.show();
			gatewayEditText.setError(getString(R.string.task_alert_input) + " " + getString(R.string.task_gateway));
			return;
		} else if (portEditText.getText().toString().trim().length() == 0) {// 端口为空
			Toast.makeText(TaskMmsSend.this,
					getString(R.string.task_alert_input) + " " + getString(R.string.task_port), Toast.LENGTH_SHORT)
					.show();
			portEditText.setError(getString(R.string.task_alert_input) + " " + getString(R.string.task_port));
			return;
		} else if (!Verify.isIp(gatewayEditText.getText().toString())) {// 网关不正确
			Toast.makeText(TaskMmsSend.this,
					getString(R.string.task_alert_input) + " " + getString(R.string.task_gateway), Toast.LENGTH_SHORT)
					.show();
			gatewayEditText.setError(getString(R.string.task_alert_input) + " " + getString(R.string.task_gateway));
			return;
		} else if (!Verify.isPort(portEditText.getText().toString())) {// 端口不正确
			Toast.makeText(TaskMmsSend.this,
					getString(R.string.task_alert_input) + " " + getString(R.string.task_port), Toast.LENGTH_SHORT)
					.show();
			portEditText.setError(getString(R.string.task_alert_input) + " " + getString(R.string.task_port));
			return;
		} else {
		}

		if (model == null) {
			model = new TaskMmsSendModel();
			taskd.setCurrentTaskIdAndSequence(model);
		}
		model.setTaskName(taskNameEditText.getText().toString());
		model.setTaskType(WalkStruct.TaskType.MMSSend.name());
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(repeatEditText.getText().toString().trim().length() == 0 ? "1"
				: repeatEditText.getText().toString().trim()));
		model.setTimeOut(Integer.parseInt(timeOutEditText.getText().toString().trim().length() == 0 ? "60"
				: timeOutEditText.getText().toString().trim()));
		// 这个参数不会用到
		model.setReportTime(60);
		model.setServerAddress(serverAddressEditText.getText().toString().trim().length() == 0 ? ""
				: serverAddressEditText.getText().toString().trim());
		model.setGateway(gatewayEditText.getText().toString().trim().length() == 0 ? "" : gatewayEditText.getText()
				.toString().trim());
		model.setPort(Integer.parseInt(portEditText.getText().toString().trim().length() == 0 ? "80" : portEditText
				.getText().toString().trim()));
		model.setInterVal(Integer.parseInt(interValEditText.getText().toString().trim().length() == 0 ? "30"
				: interValEditText.getText().toString().trim()));
		model.setDestination(destinationEditText.getText().toString().trim().length() == 0 ? "0" : destinationEditText
				.getText().toString().trim());
		// model.setSubject(et_subject.getText().toString().trim().length()==0?"0":et_subject.getText().toString().trim());
		model.setContent(contentEditText.getText().toString().trim().length() == 0 ? "0" : contentEditText.getText()
				.toString().trim());
		model.setFileSource(fileSource.getSelectedItemPosition());
		model.setAdjunct(et_adjunct.getText().toString().trim().length() == 0 ? "" : et_adjunct.getText().toString()
				.trim());
		model.setMediaFileSize(Integer.parseInt(mediaFileSizeEditText.getText().toString().trim().length() == 0 ? "20"
				: mediaFileSizeEditText.getText().toString().trim()));
		model.setDisConnect(disConnectSpinner.getSelectedItemPosition());
		model.setUnitTest(unionSpinner.getSelectedItemPosition() == 1);

		List<TaskModel> array = taskd.getTaskListArray();
		if (RABTAG.equals(super.getRabTag())) {// 依标志区分并发与普通业务
			for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
				if ((super.getMultiRabName()).equals(taskd.getCurrentTaskList().get(i).getTaskID())) {
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
				array.add(array.size(), model);
			} else {
				array.remove(taskListId);
				array.add(taskListId, model);
			}
		}

		taskd.setTaskListArray(array);

		Toast.makeText(getApplicationContext(),
				isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess, Toast.LENGTH_SHORT).show();
		TaskMmsSend.this.finish();
	}

	/**
	 * 添加EditText输入监听限制<BR>
	 * [功能详细描述]
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

		serverAddressEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (serverAddressEditText.getText().toString().trim().length() == 0) {// 服务中心地址为空
					serverAddressEditText.setError(getString(R.string.task_alert_input) + " "
							+ getString(R.string.task_serverAddress));
				}
			}
		});

		gatewayEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (!Verify.isIp(gatewayEditText.getText().toString())) {// 网关不正确
					gatewayEditText.setError(getString(R.string.task_alert_input) + " "
							+ getString(R.string.task_gateway));
				}
			}
		});

		contentEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (contentEditText.getText().toString().trim().length() == 0) {
					contentEditText.setError(getString(R.string.task_alert_nullcontent));
				}
			}
		});

		portEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (portEditText.getText().toString().trim().length() == 0) {// 端口为空
					portEditText.setError(getString(R.string.task_alert_input) + " " + getString(R.string.task_port));
				}
			}
		});

	}

}
