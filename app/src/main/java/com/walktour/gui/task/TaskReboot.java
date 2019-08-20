package com.walktour.gui.task;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.WalkStruct;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.reboot.TaskRebootModel;

import java.util.List;

/**
 * 重新开机测试任务
 */
public class TaskReboot extends BaseTaskActivity {
	TaskListDispose taskd = null;
	TaskRebootModel model = null;
	private String tag = "TaskReboot";
	private int taskListId = -1;
	private boolean isNew = true;
	private EditText taskNameEditText;
	private EditText keepTimeEditText;
	private String rabTag = "";
	private String multiRabName;
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
						model = (TaskRebootModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel()
								.get(taskListId);
						break;
					}
				}
			} else {
				model = (TaskRebootModel) taskd.getTaskListArray().get(taskListId);
			}
			abstModel = model;
			isNew = false;
		}
		showView();
		rabTag = (getIntent() == null ? "" : getIntent().getStringExtra("RAB"));
		multiRabName = (getIntent() == null ? "" : getIntent().getStringExtra("multiRabName"));
	}

	private void showView() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.task_reboot, null);
		((TextView) textEntryView.findViewById(R.id.title_txt)).setText(R.string.act_task_reboot);
		((ImageView) textEntryView.findViewById(R.id.pointer)).setOnClickListener(this);
		TextView tv_taskName = (TextView) textEntryView.findViewById(R.id.txt_taskname);
		TextView tv_keepTime = (TextView) textEntryView.findViewById(R.id.txt_keepTime);
		Button btn_ok = (Button) textEntryView.findViewById(R.id.btn_ok);
		Button btn_cencle = (Button) textEntryView.findViewById(R.id.btn_cencle);
		tv_taskName.setText(getString(R.string.task_taskName));
		tv_keepTime.setText(getString(R.string.task_rebootTime));
		btn_ok.setText(" " + getString(R.string.str_save) + " ");
		btn_cencle.setText(getString(R.string.str_cancle));

		taskNameEditText = (EditText) textEntryView.findViewById(R.id.edit_taskname);
		keepTimeEditText = (EditText) textEntryView.findViewById(R.id.edit_keepTime);

		if (model != null) {
			taskNameEditText.setText(model.getTaskName().toString().trim()
					.substring(model.getTaskName().indexOf("%") + 1, model.getTaskName().toString().trim().length()));
			keepTimeEditText.setText(model.getRebootTestConfig().getRebootTime()+"");
		}else{
			taskNameEditText.setText(R.string.act_task_reboot);
		}
		// 保存任务
		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveTestTask();
			}
		});

		// 取消
		btn_cencle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
				// turnToTaskList();
			}
		});
		textEntryView.setVerticalScrollBarEnabled(true);
		setContentView(textEntryView);
	}

	/**
	 * 保存任务<BR>
	 * [功能详细描述]
	 */
	public void saveTestTask() {
		if (taskNameEditText.getText().toString().trim().length() == 0) { // 任务名为空
			Toast.makeText(this.getApplicationContext(), R.string.task_alert_nullName,
					Toast.LENGTH_SHORT).show();
			taskNameEditText.setError(getString(R.string.task_alert_nullName));
			return;
		} else {
		}
		if (model == null) {
			model = new TaskRebootModel();
			taskd.setCurrentTaskIdAndSequence(model);
		}
		// 依据标记区分用户名的编辑
		model.setTaskName(taskNameEditText.getText().toString());
		model.setTypeProperty(0);
		model.setTaskType(WalkStruct.TaskType.REBOOT.name());
		model.setEnable(1);
		if (keepTimeEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nulKeepTime, Toast.LENGTH_SHORT).show();
			return;
		} else if (keepTimeEditText.getText().toString().trim().equals("0")
				|| keepTimeEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			keepTimeEditText.setError(getString(R.string.task_alert_nullTimeout));
			return;
		} else {
			model.getRebootTestConfig().setRebootTime(keepTimeEditText.getText().toString().trim());
		}

		List<TaskModel> array = taskd.getTaskListArray();

		if (RABTAG.equals(this.rabTag)) {// 依标志区分并发与普通业务
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
		Toast.makeText(getApplicationContext(),
				isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess, Toast.LENGTH_SHORT).show();
		this.finish();
	}

	public void addToFirstEnab(TaskModel model) {

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

		keepTimeEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (keepTimeEditText.getText().toString().trim().equals("0")
						|| keepTimeEditText.getText().toString().trim().length() == 0) {
					keepTimeEditText.setError(getString(R.string.task_alert_input) + " "
							+ getString(R.string.task_keepTime));
					return;
				}
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
	}

}
