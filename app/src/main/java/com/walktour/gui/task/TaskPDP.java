package com.walktour.gui.task;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.APNOperate;
import com.walktour.Utils.WalkStruct;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.pdpactive.TaskPdpModel;

import java.util.List;

public class TaskPDP extends BaseTaskActivity {
	Uri uri = Uri.parse("content://telephony/carriers");
	TaskListDispose taskd = null;
	TaskPdpModel model = null;
	APNOperate apnOperate = null;
	private int taskListId = -1;
	private boolean isNew = true;
	private String tag = "TaskPDP";
	private EditText taskNameEditText;
	private EditText repeatEditText;
	private EditText keepTimeEditText;
	private EditText interValEditText;
	private EditText ulRateText;
	private EditText dlRateText;
	private TaskRabModel taskRabModel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		taskd = TaskListDispose.getInstance();
		apnOperate = APNOperate.getInstance(this);
		Bundle bundle = getIntent().getExtras();
		// LogUtil.i(tag, super.getRabTag());
		// LogUtil.i(tag, super.getRabTaskName());
		if (bundle != null && bundle.containsKey("taskListId")) {
			taskListId = bundle.getInt("taskListId");
			// 根据标记做并发编辑
			if (RABTAG.equals(super.getRabTag())) {
				for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
					if (taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getModifyBefRabName())) {
						model = (TaskPdpModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel().get(
								taskListId);
						break;
					}
				}
			} else {
				model = (TaskPdpModel) taskd.getTaskListArray().get(taskListId);
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
		final View textEntryView = factory.inflate(R.layout.task_pdp, null);
		((TextView) textEntryView.findViewById(R.id.title_txt)).setText(R.string.act_task_pdp);// 设置标题
		((ImageView) textEntryView.findViewById(R.id.pointer)).setOnClickListener(this);
		((RelativeLayout) textEntryView.findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);

		TextView tv_taskName = (TextView) textEntryView.findViewById(R.id.txt_taskname);
		TextView tv_repeat = (TextView) textEntryView.findViewById(R.id.txt_repeat);
		TextView tv_keepTime = (TextView) textEntryView.findViewById(R.id.txt_keepTime);
		// TextView tv_apn =(TextView)textEntryView.findViewById(R.id.txt_apn);
		TextView tv_interVal = (TextView) textEntryView.findViewById(R.id.txt_interVal);
		Button btn_ok = (Button) textEntryView.findViewById(R.id.btn_ok);
		Button btn_cencle = (Button) textEntryView.findViewById(R.id.btn_cencle);

		tv_taskName.setText(getString(R.string.task_taskName));
		tv_repeat.setText(getString(R.string.task_repeat));
		tv_keepTime.setText(getString(R.string.task_timeOut));
		// tv_apn.setText(getString(R.string.task_apn)+"*");
		tv_interVal.setText(getString(R.string.task_interVal));
		btn_ok.setText(" " + getString(R.string.str_save) + " ");
		btn_cencle.setText(getString(R.string.str_cancle));

		taskNameEditText = (EditText) textEntryView.findViewById(R.id.edit_taskname);
		repeatEditText = (EditText) textEntryView.findViewById(R.id.edit_repeat);
		keepTimeEditText = (EditText) textEntryView.findViewById(R.id.edit_keepTime);
		// final Spinner et_apn
		// =(Spinner)textEntryView.findViewById(R.id.edit_apn);
		interValEditText = (EditText) textEntryView.findViewById(R.id.edit_interVal);
		ulRateText = (EditText) textEntryView.findViewById(R.id.edit_ulRate);
		dlRateText = (EditText) textEntryView.findViewById(R.id.edit_dlRate);
		// 此处为获得手机的网络连接列表
		/*
		 * String[] apnNames =
		 * apnOperate.getAPNNameListByFirstEmpty(TaskPDP.this);
		 * ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		 * android.R.layout.simple_spinner_item, apnNames);
		 * adapter.setDropDownViewResource
		 * (android.R.layout.simple_spinner_dropdown_item);
		 * et_apn.setAdapter(adapter);
		 */

		if (model != null) {
			taskNameEditText.setText(model.getTaskName().toString().trim()
					.substring(model.getTaskName().indexOf("%") + 1, model.getTaskName().toString().trim().length()));
			repeatEditText.setText(String.valueOf(model.getRepeat()));
			keepTimeEditText.setText(String.valueOf(model.getKeepTime()));
			// et_apn.setSelection(apnOperate.getPositonFirstEmpty(model.getApn()));
			interValEditText.setText(String.valueOf(model.getInterVal()));
			ulRateText.setText(String.valueOf(model.getRateUL()));
			dlRateText.setText(String.valueOf(model.getRateDL()));
		} else {
			taskNameEditText.setText("PDP");
			repeatEditText.setText("10");
			keepTimeEditText.setText("15");
			interValEditText.setText("15");
			ulRateText.setText("64");
			dlRateText.setText("384");
		}
		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveTestTask();
			}
		});

		btn_cencle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TaskPDP.this.finish();
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
			Toast.makeText(com.walktour.gui.task.TaskPDP.this.getApplicationContext(), R.string.task_alert_nullName,
					Toast.LENGTH_SHORT).show();
			taskNameEditText.setError(getString(R.string.task_alert_nullName));
			return;
		} else if (repeatEditText.getText().toString().trim().equals("0")
				|| repeatEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
			return;
		} else if (keepTimeEditText.getText().toString().trim().equals("0")
				|| keepTimeEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			keepTimeEditText.setError(getString(R.string.task_alert_input) + " " + getString(R.string.task_timeOut));
			return;
		} else if (interValEditText.getText().toString().trim().equals("0")
				|| interValEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			interValEditText.setError(getString(R.string.task_alert_nullInterval));
			return;
		} else if (ulRateText.getText().toString().trim().length() == 0
				|| dlRateText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullRateOfPdp, Toast.LENGTH_SHORT).show();
			return;
		} else {
			String[] taskNames = taskd.getTaskNames(3);
			for (int i = 0; i < taskNames.length; i++) {
				if ((isNew && taskNames[i].equals(taskNameEditText.getText().toString().trim())) // 判断任务名是否重复
						|| (!isNew && taskListId != i && taskNames[i].equals(taskNameEditText.getText().toString()
								.trim()))) {
					// Toast.makeText(getApplicationContext(),R.string.task_alert_existsName,Toast.LENGTH_SHORT).show();
					// taskNameEditText.setError(getString(R.string.task_alert_existsName));
					// return;
				}
			}
		}
		if (model == null) {
			model = new TaskPdpModel();
			taskd.setCurrentTaskIdAndSequence(model);
		}
		// 依据标记区分用户名的编辑
		model.setTaskName(taskNameEditText.getText().toString().trim());
		model.setTaskType(WalkStruct.TaskType.PDP.name());
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(repeatEditText.getText().toString().trim().length() == 0 ? "0"
				: repeatEditText.getText().toString().trim()));
		model.setKeepTime(Integer.parseInt(keepTimeEditText.getText().toString().trim().length() == 0 ? "0"
				: keepTimeEditText.getText().toString().trim()));
		// model.setApn(apnOperate.getNameFirstEmpty(et_apn.getSelectedItemPosition(),TaskPDP.this));
		model.setInterVal(Integer.parseInt(interValEditText.getText().toString().trim().length() == 0 ? "0"
				: interValEditText.getText().toString().trim()));
		model.setRateDL(Integer.parseInt(dlRateText.getText().toString().trim()));
		model.setRateUL(Integer.parseInt(ulRateText.getText().toString().trim()));
		List<TaskModel> array = taskd.getTaskListArray();
		if (RABTAG.equals(super.getRabTag())) {// 依标志区分并发与普通业务
			for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
				if ((isNew ? super.getMultiRabName() : super.getModifyBefRabName()).equals(taskd.getCurrentTaskList().get(i).getTaskID())) {
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
				array.add(array.size(),model);
			} else {
				array.remove(taskListId);
				array.add(taskListId, model);
			}
		}
		taskd.setTaskListArray(array);

		Toast.makeText(getApplicationContext(),
				isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess, Toast.LENGTH_SHORT).show();
		TaskPDP.this.finish();
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

		keepTimeEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (keepTimeEditText.getText().toString().trim().equals("0")
						|| keepTimeEditText.getText().toString().trim().length() == 0) {
					keepTimeEditText.setError(getString(R.string.task_alert_input) + " "
							+ getString(R.string.task_timeOut));
					return;
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
