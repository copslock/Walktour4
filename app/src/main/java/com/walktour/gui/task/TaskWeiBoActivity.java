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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.WalkStruct;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.weibo.TaskWeiBoModel;

import java.io.File;
import java.util.List;

/**
 * 
 * @author zhihui.lian
 * 
 *         weibo任务配置界面
 * 
 */
public class TaskWeiBoActivity extends BaseTaskActivity {
	TaskListDispose taskd = null;
	TaskWeiBoModel model = null;
	private int taskListId = -1;
	private boolean isNew = true;

	private EditText taskNameEditText;
	private EditText repeatEditText;
	private EditText interValEditText;
	private Spinner disConnectEditText;
	private TaskRabModel taskRabModel;
	private EditText userEditText;
	private EditText passEditText;
	private Button btnSelectView;
	private EditText sendpicPath;
	// private Spinner dataConnectType;// 数据连接选择：PPP or WIFI
	private EditText fansnameEditText;
	private EditText fanspasswordEditText;
	private EditText longinTimeoutEditText;
	private EditText infosendtimeoutEditText;

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
						model = (TaskWeiBoModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel()
								.get(taskListId);
						break;
					}
				}
			} else {
				model = (TaskWeiBoModel) taskd.getTaskListArray().get(taskListId);
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
		setContentView(R.layout.task_weibo);
		(initTextView(R.id.title_txt)).setText("WeiBo");// 设置标题
		(initImageView(R.id.pointer)).setOnClickListener(this);
		(initRelativeLayout(R.id.advanced_arrow_rel)).setOnClickListener(this);

		TextView tv_taskName = initTextView(R.id.txt_taskname);
		TextView tv_repeat = initTextView(R.id.txt_repeat);
		TextView tv_interVal = initTextView(R.id.txt_interVal);
		TextView tv_disConnect = initTextView(R.id.txt_disConnect);
		Button btn_ok = initButton(R.id.btn_ok);
		Button btn_cencle = initButton(R.id.btn_cencle);

		btnSelectView = initButton(R.id.btn_view);
		btnSelectView.setOnClickListener(clickListener);
		sendpicPath = initEditText(R.id.edit_sendpic);

		tv_taskName.setText(getString(R.string.task_taskName));
		tv_repeat.setText(getString(R.string.task_repeat));
		tv_interVal.setText(getString(R.string.task_interVal));
		tv_disConnect.setText(getString(R.string.task_disConnect));
		btn_ok.setText(" " + getString(R.string.str_save) + " ");
		btn_cencle.setText(getString(R.string.str_cancle));

		taskNameEditText = initEditText(R.id.edit_taskname);
		repeatEditText = initEditText(R.id.edit_repeat);
		userEditText = initEditText(R.id.edit_myname);
		passEditText = initEditText(R.id.edit_mypassword);
		interValEditText = initEditText(R.id.edit_interVal);
		fansnameEditText = initEditText(R.id.edit_fansname);
		fanspasswordEditText = initEditText(R.id.edit_fanspassword);
		longinTimeoutEditText = initEditText(R.id.edit_LonginTimeout);
		infosendtimeoutEditText = initEditText(R.id.edit_infosendtimeout);

		disConnectEditText = initSpinner(R.id.edit_disConnect);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_task_disconnect));
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		disConnectEditText.setAdapter(adapter);

		// wifi support
		// dataConnectType = initSpinner(R.id.edit_data_connect_type);
		// super.setDataConnectTypeSP(dataConnectType);

		if (model != null) {
			taskNameEditText.setText(model.getTaskName().toString().trim()
					.substring(model.getTaskName().indexOf("%") + 1, model.getTaskName().toString().trim().length()));
			repeatEditText.setText(String.valueOf(model.getRepeat()));
			userEditText.setText(model.getWeiboTestConfig().getMyAccount().getUserName());
			passEditText.setText(model.getWeiboTestConfig().getMyAccount().getPassword());
			interValEditText.setText(String.valueOf(model.getInterVal()));
			disConnectEditText.setSelection(model.getDisConnect());
			sendpicPath.setText(model.getWeiboTestConfig().getSendFile());
			// dataConnectType.setSelection(model.getTypeProperty() == 4 ? 1 :
			// 0);
			fansnameEditText.setText(model.getWeiboTestConfig().getFansAccount().getUserName());
			fanspasswordEditText.setText(model.getWeiboTestConfig().getFansAccount().getPassword());
			longinTimeoutEditText.setText(String.valueOf(model.getWeiboTestConfig().getLoginTimeout()));
			infosendtimeoutEditText.setText(String.valueOf(model.getWeiboTestConfig().getSendTimeout()));
		} else {
			taskNameEditText.setText("WeiBo");
			repeatEditText.setText("10");
			interValEditText.setText("15");
			longinTimeoutEditText.setText("20");
			infosendtimeoutEditText.setText("20");
			sendpicPath.setText("/");
			disConnectEditText.setSelection(1);
		}
		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveTestTask();
			}
		});

		btn_cencle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TaskWeiBoActivity.this.finish();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mReceiver);
	}

	@Override
	public void saveTestTask() {
		if (taskNameEditText.getText().toString().trim().length() == 0) { // 任务名为空
			Toast.makeText(com.walktour.gui.task.TaskWeiBoActivity.this.getApplicationContext(),
					R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
			taskNameEditText.setError(getString(R.string.task_alert_nullName));
			return;
		} else if (repeatEditText.getText().toString().trim().equals("0")
				|| repeatEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
			return;
		} else if (userEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(com.walktour.gui.task.TaskWeiBoActivity.this.getApplicationContext(),
					R.string.task_alert_nullAccount, Toast.LENGTH_SHORT).show();
			userEditText.setError(getString(R.string.task_alert_nullAccount));
			return;
		} else if (passEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(com.walktour.gui.task.TaskWeiBoActivity.this.getApplicationContext(),
					R.string.task_alert_nullPassword, Toast.LENGTH_SHORT).show();
			passEditText.setError(getString(R.string.task_alert_nullPassword));
			return;
		} else if (fansnameEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(com.walktour.gui.task.TaskWeiBoActivity.this.getApplicationContext(),
					R.string.task_alert_nullAccount, Toast.LENGTH_SHORT).show();
			fansnameEditText.setError(getString(R.string.task_alert_nullAccount));
			return;
		} else if (fanspasswordEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(com.walktour.gui.task.TaskWeiBoActivity.this.getApplicationContext(),
					R.string.task_alert_nullPassword, Toast.LENGTH_SHORT).show();
			fanspasswordEditText.setError(getString(R.string.task_alert_nullPassword));
			return;
		} else if (longinTimeoutEditText.getText().toString().trim().equals("0")
				|| longinTimeoutEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			longinTimeoutEditText.setError(getString(R.string.task_alert_nullTimeout));
			return;
		} else if (interValEditText.getText().toString().trim().equals("0")
				|| interValEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			interValEditText.setError(getString(R.string.task_alert_nullInterval));
			return;
		} else if (infosendtimeoutEditText.getText().toString().trim().equals("0")
				|| infosendtimeoutEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			infosendtimeoutEditText.setError(getString(R.string.task_alert_nullTimeout));
			return;
		} else if (sendpicPath.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullfilepath, Toast.LENGTH_SHORT).show();
			sendpicPath.setError(getString(R.string.task_alert_nullfilepath));
			return;
		} else if (sendpicPath.getText().toString().trim().length() > 0) {
			String picPath = sendpicPath.getText().toString().trim();
			File file = new File(picPath);
			if (!file.exists() || !file.isFile()) {
				Toast.makeText(getApplicationContext(), R.string.server_file_notfound, Toast.LENGTH_SHORT).show();
				sendpicPath.setError(getString(R.string.server_file_notfound));
				return;
			}
		} else {}
		if (model == null) {
			model = new TaskWeiBoModel();
			taskd.setCurrentTaskIdAndSequence(model);
		}
		// 依据标记区分用户名的编辑
		model.setTaskName(taskNameEditText.getText().toString().trim());
		model.setTaskType(WalkStruct.TaskType.WeiBo.name());
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(repeatEditText.getText().toString().trim().length() == 0 ? "10"
				: repeatEditText.getText().toString().trim()));
		model.setInterVal(Integer.parseInt(interValEditText.getText().toString().trim().length() == 0 ? "3"
				: interValEditText.getText().toString().trim()));
		model.setDisConnect(disConnectEditText.getSelectedItemPosition());
		model.getWeiboTestConfig().getMyAccount().setUserName(userEditText.getText().toString());
		model.getWeiboTestConfig().getMyAccount().setPassword(passEditText.getText().toString());
		model.getWeiboTestConfig().getFansAccount().setUserName(fansnameEditText.getText().toString());
		model.getWeiboTestConfig().getFansAccount().setPassword(fanspasswordEditText.getText().toString());
		model.getWeiboTestConfig().setLoginTimeout(Integer.parseInt(longinTimeoutEditText.getText().toString()));
		model.getWeiboTestConfig().setSendTimeout(Integer.parseInt(infosendtimeoutEditText.getText().toString()));
		model.getWeiboTestConfig().setSendFile(sendpicPath.getText().toString());
		// if (((Long) dataConnectType.getSelectedItemId()).intValue() == 1) //
		// 只有是WIFI的才设置
		// model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan); //
		// else
		// model.setTypeProperty(WalkCommonPara.TypeProperty_Net); // ppp

		int enableLength = taskd.getTaskNames(1).length;
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

		Toast.makeText(getApplicationContext(),
				isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess, Toast.LENGTH_SHORT).show();
		TaskWeiBoActivity.this.finish();
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

		infosendtimeoutEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (infosendtimeoutEditText.getText().toString().trim().equals("0")) {
					infosendtimeoutEditText.setError(getString(R.string.task_alert_nullTimeout));
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

	/**
	 * 点击弹出事件
	 */
	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(TaskWeiBoActivity.this, FileExplorer.class);
			// 添加传递参数
			Bundle bundle = new Bundle();
			bundle.putBoolean(FileExplorer.KEY_NORMAL, true);
			bundle.putString(FileExplorer.KEY_ACTION, FileExplorer.ACTION_LOAD_NORMAL_FILE);// 文件浏览类型
			bundle.putString(FileExplorer.KEY_EXTRA, FileExplorer.KEY_FILE);
			bundle.putLong(FileExplorer.KEY_FILE_SIZE, 1000 * 1000);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(FileExplorer.ACTION_LOAD_NORMAL_FILE)) {
				String filePath = "";
				try {
					filePath = intent.getStringExtra(FileExplorer.KEY_FILE);
				} catch (Exception e) {
				}
				sendpicPath.setText(filePath);
			}
		}
	};
}
