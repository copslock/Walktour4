package com.walktour.gui.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.WalkStruct;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.attach.TaskAttachModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

import java.util.List;

public class TaskAttach extends BaseTaskActivity {
	TaskListDispose taskd = null;
	TaskAttachModel model = null;
	private int taskListId = -1;
	private boolean isNew = true;

	private Button btn_ok;
	private Button btn_cencle;

	private EditText et_taskName;
	private EditText et_repeat;
	private EditText et_keepTime;
	private EditText et_interVal;
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
					if (taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())) {
						model = (TaskAttachModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel()
								.get(taskListId);
						break;
					}
				}
			} else {
				model = (TaskAttachModel) taskd.getTaskListArray().get(taskListId);

			}
			abstModel = model;
			isNew = false;
		}

		showView();
	}

	private void showView() {
		// 绑定Layout里面的ListView
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.task_attach, null);
		((TextView) textEntryView.findViewById(R.id.title_txt)).setText(R.string.act_task_attach); // 设置标题
		((ImageView) textEntryView.findViewById(R.id.pointer)).setOnClickListener(this);
		((RelativeLayout) textEntryView.findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);

		TextView tv_taskName = (TextView) textEntryView.findViewById(R.id.txt_taskname);
		TextView tv_repeat = (TextView) textEntryView.findViewById(R.id.txt_repeat);
		TextView tv_keepTime = (TextView) textEntryView.findViewById(R.id.txt_keepTime);
		TextView tv_interVal = (TextView) textEntryView.findViewById(R.id.txt_interVal);
		btn_ok = (Button) textEntryView.findViewById(R.id.btn_ok);
		btn_cencle = (Button) textEntryView.findViewById(R.id.btn_cencle);

		tv_taskName.setText(getString(R.string.task_taskName));
		tv_repeat.setText(getString(R.string.task_repeat));
		tv_keepTime.setText(getString(R.string.task_simplekeepTime));
		tv_interVal.setText(getString(R.string.task_interVal));
		btn_ok.setText(" " + getString(R.string.str_save) + " ");
		btn_cencle.setText(getString(R.string.str_cancle));

		et_taskName = (EditText) textEntryView.findViewById(R.id.edit_taskname);
		et_repeat = (EditText) textEntryView.findViewById(R.id.edit_repeat);
		et_keepTime = (EditText) textEntryView.findViewById(R.id.edit_keepTime);
		et_interVal = (EditText) textEntryView.findViewById(R.id.edit_interVal);

		if (model != null) {
			et_taskName.setText(model.getTaskName().toString().trim()
					.substring(model.getTaskName().indexOf("%") + 1, model.getTaskName().toString().trim().length()));
			et_repeat.setText(String.valueOf(model.getRepeat()));
			et_keepTime.setText(String.valueOf(model.getAttachTestConfig().getKeepTime()));
			et_interVal.setText(String.valueOf(model.getInterVal()));
		} else {
			et_taskName.setText("Attach");
			et_repeat.setText("3");
			et_keepTime.setText("15");
			et_interVal.setText("5");
		}
		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveTestTask();
			}
		});

		btn_cencle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TaskAttach.this.finish();
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
		if (et_taskName.getText().toString().trim().length() == 0) { // 任务名为空
			Toast.makeText(com.walktour.gui.task.TaskAttach.this.getApplicationContext(), R.string.task_alert_nullName,
					Toast.LENGTH_SHORT).show();
			return;
		} else if (et_repeat.getText().toString().trim().equals("0")
				|| et_repeat.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			return;
		} else if (et_keepTime.getText().toString().trim().equals("0")
				|| et_keepTime.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			return;
		} else if (et_interVal.getText().toString().trim().equals("0")
				|| et_interVal.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			return;
		}
		if (model == null) {
			model = new TaskAttachModel();
			taskd.setCurrentTaskIdAndSequence(model);
		}
		// 依据标记区分用户名的编辑
		model.setTaskName(et_taskName.getText().toString().trim());
		model.setTaskType(WalkStruct.TaskType.Attach.name());
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(et_repeat.getText().toString().trim().length() == 0 ? "1" : et_repeat
				.getText().toString().trim()));
		model.getAttachTestConfig().setKeepTime(
				Integer.parseInt(et_keepTime.getText().toString().trim().length() == 0 ? "60" : et_keepTime.getText()
						.toString().trim()));
		model.setInterVal(Integer.parseInt(et_interVal.getText().toString().trim().length() == 0 ? "5" : et_interVal
				.getText().toString().trim()));

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
		TaskAttach.this.finish();
	}
}
