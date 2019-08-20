package com.walktour.gui.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.walktour.Utils.WalkStruct;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.facebook.TaskFaceBookModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

import java.util.List;

/**
 * Facebook任务配置界面
 * 
 * @author jianchao.wang
 *
 */
public class TaskFacebookActivity extends BaseTaskActivity {
	TaskListDispose taskd = null;
	TaskFaceBookModel model = null;
	private int taskListId = -1;
	private boolean isNew = true;
	/** 任务名称 */
	private EditText taskNameEditText;
	/** 测试次数 */
	private EditText repeatEditText;
	/** APP ID */
	private EditText appIdEditText;
	/** APP Secret */
	private EditText appSecretEditText;
	/** 发送图片 */
	private Spinner sendPicSizeLevelSpinner;
	/** 间隔时长(s) */
	private EditText interValEditText;
	/** 拨号规则 */
	private Spinner disConnectSpinner;
	/** 任务对象 */
	private TaskRabModel taskRabModel;
	/** 用户 */
	private EditText userEditText;
	/** 密码 */
	private EditText passEditText;
	private EditText fileURL;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		taskd = TaskListDispose.getInstance();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("taskListId")) {
			taskListId = bundle.getInt("taskListId");
			if (RABTAG.equals(super.getRabTag())) {
				for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
					if (taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())) {
						model = (TaskFaceBookModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel()
								.get(taskListId);
						break;
					}
				}
			} else {
				model = (TaskFaceBookModel) taskd.getTaskListArray().get(taskListId);
			}
			abstModel = model;
			isNew = false;
		}
		findView();
		addEditTextWatcher();
		IntentFilter filter = new IntentFilter();
		filter.addAction(FileExplorer.ACTION_LOAD_NORMAL_FILE);
		this.registerReceiver(mReceiver, filter);
	}

	private void findView() {
		setContentView(R.layout.task_facebook);
		(initTextView(R.id.title_txt)).setText(R.string.facebook_title);// 设置标题
		(initImageView(R.id.pointer)).setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);

		taskNameEditText = initEditText(R.id.edit_taskname);
		repeatEditText = initEditText(R.id.edit_repeat);
		userEditText = initEditText(R.id.edit_user);
		passEditText = initEditText(R.id.edit_pass);
		sendPicSizeLevelSpinner = (Spinner) findViewById(R.id.edit_send_pic);
		appIdEditText = initEditText(R.id.edit_app_id);
		appSecretEditText = initEditText(R.id.edit_app_secret);
		interValEditText = initEditText(R.id.edit_interVal);

		disConnectSpinner = (Spinner) findViewById(R.id.edit_disConnect);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_task_disconnect));
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		disConnectSpinner.setAdapter(adapter);

		ArrayAdapter<String> picSizeAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.send_pic_size_level));
		picSizeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		sendPicSizeLevelSpinner.setAdapter(picSizeAdapter);

		if (model != null) {
			taskNameEditText.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%") + 1,
					model.getTaskName().toString().trim().length()));
			repeatEditText.setText(String.valueOf(model.getRepeat()));
			userEditText.setText(model.getUser());
			passEditText.setText(model.getPassword());
			appIdEditText.setText(model.getAppId());
			appSecretEditText.setText(model.getAppSecret());
			sendPicSizeLevelSpinner.setSelection(model.getSendPicSizeLevel());
			interValEditText.setText(String.valueOf(model.getInterVal()));
			disConnectSpinner.setSelection(model.getDisConnect());
		} else {
			taskNameEditText.setText("Facebook");
			repeatEditText.setText("10");
			interValEditText.setText("15");
			disConnectSpinner.setSelection(1);
		}
		Button btn_ok = initButton(R.id.btn_ok);
		Button btn_cencle = initButton(R.id.btn_cencle);
		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveTestTask();
			}
		});

		btn_cencle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TaskFacebookActivity.this.finish();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		// findView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mReceiver);
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @see com.walktour.gui.task.BaseTaskActivity#saveTestTask()
	 */

	@Override
	public void saveTestTask() {
		if (taskNameEditText.getText().toString().trim().length() == 0) { // 任务名为空
			Toast.makeText(com.walktour.gui.task.TaskFacebookActivity.this.getApplicationContext(),
					R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
			taskNameEditText.setError(getString(R.string.task_alert_nullName));
			return;
		} else if (repeatEditText.getText().toString().trim().equals("0")
				|| repeatEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
			return;
		} else if (userEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(com.walktour.gui.task.TaskFacebookActivity.this.getApplicationContext(),
					R.string.task_alert_nullAccount, Toast.LENGTH_SHORT).show();
			userEditText.setError(getString(R.string.task_alert_nullAccount));
			return;
		} else if (passEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(com.walktour.gui.task.TaskFacebookActivity.this.getApplicationContext(),
					R.string.task_alert_nullPassword, Toast.LENGTH_SHORT).show();
			passEditText.setError(getString(R.string.task_alert_nullPassword));
			return;
		} else if (appIdEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullAppId, Toast.LENGTH_SHORT).show();
			appIdEditText.setError(getString(R.string.task_alert_nullAppId));
			return;
		} else if (interValEditText.getText().toString().trim().equals("0")
				|| interValEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			interValEditText.setError(getString(R.string.task_alert_nullInterval));
			return;
		} else if (appSecretEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullAppSecret, Toast.LENGTH_SHORT).show();
			appSecretEditText.setError(getString(R.string.task_alert_nullAppSecret));
			return;
		}
		if (model == null) {
			model = new TaskFaceBookModel();
			taskd.setCurrentTaskIdAndSequence(model);
		}
		// 依据标记区分用户名的编辑
		model.setTaskName(taskNameEditText.getText().toString().trim());
		model.setTaskType(WalkStruct.TaskType.Facebook.name());
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(
				repeatEditText.getText().toString().trim().length() == 0 ? "10" : repeatEditText.getText().toString().trim()));
		model.setInterVal(Integer.parseInt(interValEditText.getText().toString().trim().length() == 0 ? "3"
				: interValEditText.getText().toString().trim()));
		model.setDisConnect(disConnectSpinner.getSelectedItemPosition());
		model.setUser(userEditText.getText().toString());
		model.setPassword(passEditText.getText().toString());
		model.setAppId(appIdEditText.getText().toString());
		model.setAppSecret(appSecretEditText.getText().toString());
		model.setSendContent("");
		model.setSendPicSizeLevel(sendPicSizeLevelSpinner.getSelectedItemPosition());

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
				array.add(array.size(), model);
			} else {
				array.remove(taskListId);
				array.add(taskListId, model);
			}

		}
		taskd.setTaskListArray(array);

		Toast.makeText(getApplicationContext(), isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess,
				Toast.LENGTH_SHORT).show();
		TaskFacebookActivity.this.finish();
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
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(FileExplorer.ACTION_LOAD_NORMAL_FILE)) {
				String filePath = "";
				try {
					filePath = intent.getStringExtra(FileExplorer.KEY_FILE);
				} catch (Exception e) {
				}
				fileURL.setText(filePath);
			}
		}
	};
}
