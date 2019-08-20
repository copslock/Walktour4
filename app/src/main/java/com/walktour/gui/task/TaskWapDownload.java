package com.walktour.gui.task;

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
import com.walktour.control.bean.Verify;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.wap.TaskWapPageModel;

import java.util.List;

public class TaskWapDownload extends BaseTaskActivity {

	TaskListDispose taskd = null;

	TaskWapPageModel model = null;

	private int taskListId = -1;

	private boolean isNew = true;

	private EditText taskNameEditText;

	private EditText repeatEditText;

	private EditText timeOutEditText;

	private Spinner wapTypeSpinner;

	private EditText urlEditText;

	private EditText gatewayEditText;

	private EditText portEditText;

	private EditText interValEditText;

	private Spinner disConnectSpinner;

	private TaskRabModel taskRabModel;

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
					if (taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getModifyBefRabName())) {
						model = (TaskWapPageModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel()
								.get(taskListId);
						break;
					}
				}
			} else {
				model = (TaskWapPageModel) taskd.getTaskListArray().get(taskListId);
			}
			abstModel = model;
			isNew = false;

		}
		findView();
		addEditTextWatcher();
	}

	private void findView() {
		// 绑定Layout里面的ListView
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.task_wapdownload, null);
		((TextView) textEntryView.findViewById(R.id.title_txt)).setText(R.string.act_task_wapdownload);// 设置标题
		((ImageView) textEntryView.findViewById(R.id.pointer)).setOnClickListener(this);
		((RelativeLayout) textEntryView.findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);

		TextView tv_taskName = (TextView) textEntryView.findViewById(R.id.txt_taskname);
		TextView tv_repeat = (TextView) textEntryView.findViewById(R.id.txt_repeat);
		TextView tv_timeOut = (TextView) textEntryView.findViewById(R.id.txt_timeOut);
		TextView tv_wapType = (TextView) textEntryView.findViewById(R.id.txt_wapType);
		TextView tv_url = (TextView) textEntryView.findViewById(R.id.txt_url);
		TextView tv_gateway = (TextView) textEntryView.findViewById(R.id.txt_gateway);
		TextView tv_port = (TextView) textEntryView.findViewById(R.id.txt_port);
		TextView tv_interVal = (TextView) textEntryView.findViewById(R.id.txt_interVal);
		Button btn_ok = (Button) textEntryView.findViewById(R.id.btn_ok);
		Button btn_cencle = (Button) textEntryView.findViewById(R.id.btn_cencle);

		tv_taskName.setText(getString(R.string.task_taskName));
		tv_repeat.setText(getString(R.string.task_repeat));
		tv_timeOut.setText(getString(R.string.task_ftpdownload_timeout));
		tv_wapType.setText(getString(R.string.task_wapType));
		tv_url.setText(getString(R.string.task_url));
		tv_gateway.setText(getString(R.string.task_gateway));
		tv_port.setText(getString(R.string.task_port));
		tv_interVal.setText(getString(R.string.task_interVal));
		btn_ok.setText(" " + getString(R.string.str_save) + " ");
		btn_cencle.setText(getString(R.string.str_cancle));

		taskNameEditText = (EditText) textEntryView.findViewById(R.id.edit_taskname);
		repeatEditText = (EditText) textEntryView.findViewById(R.id.edit_repeat);
		timeOutEditText = (EditText) textEntryView.findViewById(R.id.edit_timeOut);
		wapTypeSpinner = (Spinner) textEntryView.findViewById(R.id.edit_wapType);
		urlEditText = (EditText) textEntryView.findViewById(R.id.edit_url);
		gatewayEditText = (EditText) textEntryView.findViewById(R.id.edit_gateway);
		portEditText = (EditText) textEntryView.findViewById(R.id.edit_port);
		interValEditText = (EditText) textEntryView.findViewById(R.id.edit_interVal);
		disConnectSpinner = (Spinner) textEntryView.findViewById(R.id.edit_disConnect);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.task_wap_downloadType));
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		wapTypeSpinner.setAdapter(adapter);

		// 断开网络配置
		ArrayAdapter<String> disconnect = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_task_disconnect));
		disconnect.setDropDownViewResource(R.layout.spinner_dropdown_item);
		disConnectSpinner.setAdapter(disconnect);

		if (model != null) {
			taskNameEditText.setText(model.getTaskName().toString().trim()
					.substring(model.getTaskName().indexOf("%") + 1, model.getTaskName().toString().trim().length()));
			repeatEditText.setText(String.valueOf(model.getRepeat()));
			timeOutEditText.setText(String.valueOf(model.getTimeOut()));
			wapTypeSpinner.setSelection(model.getWapType());
			urlEditText.setText(String.valueOf(model.getUrl()));
			gatewayEditText.setText(String.valueOf(model.getGateway()));
			portEditText.setText(String.valueOf(model.getPort()));
			interValEditText.setText(String.valueOf(model.getInterVal()));
			disConnectSpinner.setSelection(model.getDisConnect());
		} else {
			taskNameEditText.setText("WAP Download");
			repeatEditText.setText("10");
			gatewayEditText.setText("10.0.0.172");
			urlEditText.setText("http://wap.monternet.com");
			portEditText.setText("80");
			timeOutEditText.setText("30");
			interValEditText.setText("15");
			disConnectSpinner.setSelection(1);
		}

		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveTestTask();
			}
		});

		btn_cencle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TaskWapDownload.this.finish();
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
		super.onDestroy();
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
			Toast.makeText(com.walktour.gui.task.TaskWapDownload.this.getApplicationContext(),
					R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
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
		} else if (gatewayEditText.getText().toString().trim().length() == 0
				|| !Verify.isIp(gatewayEditText.getText().toString())) {// 网关
			Toast.makeText(com.walktour.gui.task.TaskWapDownload.this.getApplicationContext(),
					R.string.task_alert_nullGateway, Toast.LENGTH_SHORT).show();
			gatewayEditText.setError(getString(R.string.task_alert_nullGateway));
			return;
		} else if (portEditText.getText().toString().trim().length() == 0) {// 端口
			Toast.makeText(com.walktour.gui.task.TaskWapDownload.this.getApplicationContext(),
					R.string.task_alert_nullPort, Toast.LENGTH_SHORT).show();
			return;
		} else if (urlEditText.getText().toString().trim().length() == 0
				|| !(Verify.isUrl(urlEditText.getText().toString()))) {// URL地址
			Toast.makeText(com.walktour.gui.task.TaskWapDownload.this.getApplicationContext(),
					R.string.task_alert_nullUrl, Toast.LENGTH_SHORT).show();
			return;
		} else if (timeOutEditText.getText().toString().trim().length() == 0
				|| Integer.valueOf(timeOutEditText.getText().toString().trim()) <= 0) {// 超时
			Toast.makeText(com.walktour.gui.task.TaskWapDownload.this.getApplicationContext(),
					R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			repeatEditText.setError(getString(R.string.task_alert_nullTimeout));
			return;
		} else if (Integer.valueOf(repeatEditText.getText().toString().trim()) <= 0) {// 重复次数必须大于1
			Toast.makeText(getApplicationContext(),
					getString(R.string.task_repeat) + getString(R.string.task_alert_zore), Toast.LENGTH_SHORT).show();
			repeatEditText.setError(getString(R.string.task_alert_nullName));
			return;
		} else {
			String[] taskNames = taskd.getTaskNames(3);
			for (int i = 0; i < taskNames.length; i++) {
				if ((isNew && taskNames[i].equals(taskNameEditText.getText().toString().trim())) // 判断任务名是否重复
						|| (!isNew && taskListId != i && taskNames[i].equals(taskNameEditText.getText().toString()
								.trim()))) {
					// Toast.makeText(getApplicationContext(),
					// R.string.task_alert_existsName,
					// Toast.LENGTH_SHORT).show();
					// taskNameEditText.setError(getString(R.string.task_alert_existsName));
					// return;
				}
			}
		}

		if (model == null) {
			model = new TaskWapPageModel(WalkStruct.TaskType.WapDownload.name());
			taskd.setCurrentTaskIdAndSequence(model);
		}
		// 依据标记区分用户名的编辑
		model.setTaskName(taskNameEditText.getText().toString().trim());
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(repeatEditText.getText().toString().trim().length() == 0 ? "1"
				: repeatEditText.getText().toString().trim()));
		model.setTimeOut(Integer.parseInt(timeOutEditText.getText().toString().trim().length() == 0 ? "60"
				: timeOutEditText.getText().toString().trim()));
		model.setWapType(wapTypeSpinner.getSelectedItemPosition());
		model.setUrl(urlEditText.getText().toString().trim().length() == 0 ? "0" : urlEditText.getText().toString()
				.trim());
		model.setGateway(gatewayEditText.getText().toString().trim().length() == 0 ? "0" : gatewayEditText.getText()
				.toString().trim());
		model.setPort(Integer.parseInt(portEditText.getText().toString().trim().length() == 0 ? "0" : portEditText
				.getText().toString().trim()));
		model.setInterVal(Integer.parseInt(interValEditText.getText().toString().trim().length() == 0 ? "5"
				: interValEditText.getText().toString().trim()));
		model.setDisConnect(disConnectSpinner.getSelectedItemPosition());

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
		TaskWapDownload.this.finish();

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

		gatewayEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (!Verify.isIp(gatewayEditText.getText().toString())) {// 网关不正确
					gatewayEditText.setError(getString(R.string.task_alert_input) + " "
							+ getString(R.string.task_gateway));
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

		urlEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (urlEditText.getText().toString().trim().length() == 0
						|| !(Verify.isUrl(urlEditText.getText().toString().trim()))) {// URL地址
					urlEditText.setError(getString(R.string.task_alert_nullUrl));
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

		timeOutEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (timeOutEditText.getText().toString().trim().equals("0")) {
					timeOutEditText.setError(getString(R.string.task_alert_nullTimeout));
				}
			}
		});

	}
}
