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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.Verify;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.mms.sendreceive.TaskMmsSendReceiveModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

import java.io.File;
import java.util.List;

public class TaskMmsSendReceive extends BaseTaskActivity {
	TaskListDispose taskd = null;

	TaskMmsSendReceiveModel model = null;

	private EditText et_adjunct = null;

	private EditText mediaFileSizeEditText = null;

	private int taskListId = -1;

	private boolean isNew = true;

	private MyBroadcastReceiver mEventReceiver = new MyBroadcastReceiver();

	private EditText taskNameEditText;

	private EditText repeatEditText;

	private EditText serverAddressEditText;

	private EditText gatewayEditText;

	private EditText portEditText;

	private EditText destinationEditText;

	private EditText contentEditText;

	private EditText sendTimeoutEditText;

	// 2011.7.01暂时不需要此参数
	// final EditText et_reportTimeout
	// =(EditText)textEntryView.findViewById(R.id.edit_reportTimeOut);
	private EditText pushTimeoutEditText;

	private EditText receiveTimeoutEditText;

	private EditText interValEditText;

	private TaskRabModel taskRabModel;
	private Spinner disConnectSpinner;

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
						model = (TaskMmsSendReceiveModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i)))
								.getTaskModel().get(taskListId);
						break;
					}
				}
			} else {
				model = (TaskMmsSendReceiveModel) taskd.getTaskListArray().get(taskListId);
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
		final View textEntryView = factory.inflate(R.layout.task_mmssendreceive, null);
		((TextView) textEntryView.findViewById(R.id.title_txt)).setText(R.string.act_task_mmssendreceive);// 设置标题
		((ImageView) textEntryView.findViewById(R.id.pointer)).setOnClickListener(this);
		((RelativeLayout) textEntryView.findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);

		Button btn_view = (Button) textEntryView.findViewById(R.id.btn_view);
		Button btn_ok = (Button) textEntryView.findViewById(R.id.btn_ok);
		Button btn_cencle = (Button) textEntryView.findViewById(R.id.btn_cencle);

		btn_ok.setText(" " + getString(R.string.str_save) + " ");
		btn_cencle.setText(getString(R.string.str_cancle));

		taskNameEditText = (EditText) textEntryView.findViewById(R.id.edit_taskname);
		repeatEditText = (EditText) textEntryView.findViewById(R.id.edit_repeat);
		serverAddressEditText = (EditText) textEntryView.findViewById(R.id.edit_serverAddress);
		gatewayEditText = (EditText) textEntryView.findViewById(R.id.edit_gateway);
		portEditText = (EditText) textEntryView.findViewById(R.id.edit_port);
		destinationEditText = (EditText) textEntryView.findViewById(R.id.edit_destination);
		contentEditText = (EditText) textEntryView.findViewById(R.id.edit_content);
		et_adjunct = (EditText) textEntryView.findViewById(R.id.edit_adjunct);
		mediaFileSizeEditText = (EditText) textEntryView.findViewById(R.id.edit_filesize);
		sendTimeoutEditText = (EditText) textEntryView.findViewById(R.id.edit_sendTimeOut);
		// 2011.7.01暂时不需要此参数
		// final EditText et_reportTimeout
		// =(EditText)textEntryView.findViewById(R.id.edit_reportTimeOut);
		pushTimeoutEditText = (EditText) textEntryView.findViewById(R.id.edit_pushTimeOut);
		receiveTimeoutEditText = (EditText) textEntryView.findViewById(R.id.edit_receiveTimeOut);
		interValEditText = (EditText) textEntryView.findViewById(R.id.edit_interVal);

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
			serverAddressEditText.setText(String.valueOf(model.getServerAddress()));
			gatewayEditText.setText(String.valueOf(model.getGateway()));
			portEditText.setText(String.valueOf(model.getPort()));
			destinationEditText.setText(model.getDestination());
			contentEditText.setText(String.valueOf(model.getContent()));
			et_adjunct.setText(String.valueOf(model.getAdjunct()));
			mediaFileSizeEditText.setText(String.valueOf(model.getFileSize()));
			sendTimeoutEditText.setText(String.valueOf(model.getSendTimeout()));
			// et_reportTimeout.setText( String.valueOf(
			// model.getReportTimeout()));
			pushTimeoutEditText.setText(String.valueOf(model.getPushTimeout()));
			receiveTimeoutEditText.setText(String.valueOf(model.getReceiveTimeout()));
			interValEditText.setText(String.valueOf(model.getInterVal()));
			disConnectSpinner.setSelection(model.getDisConnect());

		} else {
			taskNameEditText.setText("MMS  Send_Recevice");
			repeatEditText.setText("1");
			serverAddressEditText.setText("http://");
			gatewayEditText.setText("10.0.0.172");
			portEditText.setText("80");
			destinationEditText.setText("");
			mediaFileSizeEditText.setText("30");
			sendTimeoutEditText.setText("30");
			// et_reportTimeout.setText( "60" );
			pushTimeoutEditText.setText("120");
			receiveTimeoutEditText.setText("60");
			interValEditText.setText("30");
			disConnectSpinner.setSelection(1);

		}
		btn_view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;
				intent = new Intent(TaskMmsSendReceive.this, FileExplorer.class);
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
				TaskMmsSendReceive.this.finish();
			}
		});
		textEntryView.setVerticalScrollBarEnabled(true);
		setContentView(textEntryView);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mEventReceiver);
		super.onDestroy();
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
			Toast.makeText(TaskMmsSendReceive.this.getApplicationContext(), R.string.task_alert_nullName,
					Toast.LENGTH_SHORT).show();
			taskNameEditText.setError(getString(R.string.task_alert_nullName));
			return;
		} else if (repeatEditText.getText().toString().trim().equals("0")
				|| repeatEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
			return;
		} else if (interValEditText.getText().toString().trim().equals("0")
				|| interValEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			interValEditText.setError(getString(R.string.task_alert_nullInterval));
			return;
		} else if (destinationEditText.getText().toString().trim().length() == 0) { // 任务名为空
			Toast.makeText(TaskMmsSendReceive.this.getApplicationContext(), R.string.task_alert_nulldestination,
					Toast.LENGTH_SHORT).show();
			destinationEditText.setError(getString(R.string.task_alert_nulldestination));
			return;
		} else if (!Verify.isIpOrUrl(serverAddressEditText.getText().toString().trim())) {
			Toast.makeText(TaskMmsSendReceive.this,
					getString(R.string.task_alert_input) + " " + getString(R.string.task_serverAddress),
					Toast.LENGTH_SHORT).show();
			serverAddressEditText.setError(getString(R.string.task_alert_input) + " "
					+ getString(R.string.task_serverAddress));
			return;
		} else if (gatewayEditText.getText().toString().trim().length() == 0) {// 网关为空
			Toast.makeText(TaskMmsSendReceive.this,
					getString(R.string.task_alert_input) + " " + getString(R.string.task_gateway), Toast.LENGTH_SHORT)
					.show();
			gatewayEditText.setError(getString(R.string.task_alert_input) + " " + getString(R.string.task_gateway));
			return;
		} else if (portEditText.getText().toString().trim().length() == 0) {// 端口为空
			Toast.makeText(TaskMmsSendReceive.this,
					getString(R.string.task_alert_input) + " " + getString(R.string.task_port), Toast.LENGTH_SHORT)
					.show();
			portEditText.setError(getString(R.string.task_alert_input) + " " + getString(R.string.task_port));
			return;
		} else if (mediaFileSizeEditText.getText().toString().trim().length() == 0) {// 大小为空
			mediaFileSizeEditText.setError(getString(R.string.task_alert_nullInut) + getString(R.string.task_fileSize));
			Toast.makeText(TaskMmsSendReceive.this,
					getString(R.string.task_alert_nullInut) + getString(R.string.task_fileSize), Toast.LENGTH_SHORT)
					.show();
			return;
		} else if (Integer.parseInt(mediaFileSizeEditText.getText().toString().trim()) > 300
				|| Integer.parseInt(mediaFileSizeEditText.getText().toString().trim()) < 0) {
			mediaFileSizeEditText.setError(getString(R.string.task_alert_nullInut) + "0~300");
			Toast.makeText(TaskMmsSendReceive.this, getString(R.string.task_alert_nullInut) + "0~300",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (sendTimeoutEditText.getText().toString().trim().equals("0")
				|| sendTimeoutEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			sendTimeoutEditText.setError(getString(R.string.task_alert_nullTimeout));
			return;
		} else if (pushTimeoutEditText.getText().toString().trim().equals("0")
				|| pushTimeoutEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			pushTimeoutEditText.setError(getString(R.string.task_alert_nullTimeout));
			return;
		} else if (receiveTimeoutEditText.getText().toString().trim().equals("0")
				|| receiveTimeoutEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			receiveTimeoutEditText.setError(getString(R.string.task_alert_nullTimeout));
			return;
		} else if (interValEditText.getText().toString().trim().equals("0")
				|| interValEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			interValEditText.setError(getString(R.string.task_alert_nullInterval));
			return;
		}
		if (model == null) {
			model = new TaskMmsSendReceiveModel();
			taskd.setCurrentTaskIdAndSequence(model);
		}
		model.setTaskName(taskNameEditText.getText().toString().trim());
		model.setTaskType(WalkStruct.TaskType.MMSSendReceive.name());
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(repeatEditText.getText().toString().trim().length() == 0 ? "1"
				: repeatEditText.getText().toString().trim()));
		model.setServerAddress(serverAddressEditText.getText().toString().trim().length() == 0 ? "http://mmsc.monternet.com"
				: serverAddressEditText.getText().toString().trim());
		model.setGateway(gatewayEditText.getText().toString().trim().length() == 0 ? "10.0.0.172" : gatewayEditText
				.getText().toString().trim());
		model.setPort(Integer.parseInt(portEditText.getText().toString().trim().length() == 0 ? "80" : portEditText
				.getText().toString().trim()));
		model.setDestination(destinationEditText.getText().toString().trim().length() == 0 ? "" : destinationEditText
				.getText().toString().trim());
		model.setContent(contentEditText.getText().toString().trim().length() == 0 ? "0" : contentEditText.getText()
				.toString().trim());
		model.setAdjunct(et_adjunct.getText().toString().trim().length() == 0 ? "" : et_adjunct.getText().toString()
				.trim());
		model.setFileSize(Integer.parseInt(mediaFileSizeEditText.getText().toString().trim().length() == 0 ? "20"
				: mediaFileSizeEditText.getText().toString().trim()));
		model.setSendTimeout(Integer.parseInt(sendTimeoutEditText.getText().toString().trim().length() == 0 ? "15"
				: sendTimeoutEditText.getText().toString().trim()));
		// model.setReportTimeout(Integer.parseInt(
		// et_reportTimeout.getText().toString().trim().length()==0?"60":et_reportTimeout.getText().toString().trim()));
		model.setReportTimeout(60);
		model.setPushTimeout(Integer.parseInt(pushTimeoutEditText.getText().toString().trim().length() == 0 ? "120"
				: pushTimeoutEditText.getText().toString().trim()));
		model.setReceiveTimeout(Integer
				.parseInt(receiveTimeoutEditText.getText().toString().trim().length() == 0 ? "160"
						: receiveTimeoutEditText.getText().toString().trim()));
		model.setInterVal(Integer.parseInt(interValEditText.getText().toString().trim().length() == 0 ? "30"
				: interValEditText.getText().toString().trim()));
		model.setDisConnect(disConnectSpinner.getSelectedItemPosition());

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
		TaskMmsSendReceive.this.finish();
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

		/*
		 * mediaFileSizeEditText.addTextChangedListener(new EditTextWatcher() {
		 * 
		 * @Override public void afterTextChanged(Editable s) { if
		 * (mediaFileSizeEditText.getText().toString().trim().length() == 0 ||
		 * Integer.parseInt(mediaFileSizeEditText.getText().toString() .trim())
		 * > 300) {// 端口为空
		 * mediaFileSizeEditText.setError(getString(R.string.task_mediafilezize_max
		 * )); } } });
		 */
	}
}
