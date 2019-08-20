package com.walktour.gui.task;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.framework.view.DateTimePickDialogUtil;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.dnslookup.TaskDNSLookUpModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

import java.util.List;

/***
 * DNS Lookup业务模板界面
 * 
 * @author weirong.fan
 * 
 */
public class TaskDNSLookup extends BaseTaskActivity {
	private EditText taskName;
	private EditText repeat;
	private EditText url;
	private EditText timeout;
	private EditText interval;
	private Spinner disConnect;
	TaskListDispose taskd = null;
	TaskDNSLookUpModel model = null;
	private int taskListId = -1;
	private boolean isNew = true;
	private Button save;// 保存
	private Button cancel;// 取消
	private ImageView advance;// 高级
	private TaskRabModel taskRabModel;

	// private Spinner dataConnectType;//数据连接选择：PPP or WIFI

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_dnslookup);
		// 设置标题名字
		(initTextView(R.id.title_txt)).setText(getResources().getString(R.string.act_task_dnslookup));
		(initImageView(R.id.pointer)).setOnClickListener(this);
		taskd = TaskListDispose.getInstance();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("taskListId")) { // 加入普通业务与并发业务处理
			taskListId = bundle.getInt("taskListId");
			if (RABTAG.equals(super.getRabTag())) {
				for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
					if (taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())) {
						model = (TaskDNSLookUpModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i)))
								.getTaskModel().get(taskListId);
						break;
					}
				}
			} else {
				model = (TaskDNSLookUpModel) taskd.getTaskListArray().get(taskListId);
			}
			isNew = false;
			abstModel = model;
		}

		taskName = initEditText(R.id.edit_taskname);
		repeat = initEditText(R.id.edit_repeat);
		url = initEditText(R.id.edit_url);
		timeout = initEditText(R.id.edit_timeout);

		interval = initEditText(R.id.edit_interval);
		disConnect = (Spinner) findViewById(R.id.edit_disConnect);
		((RelativeLayout) findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);
		save = initButton(R.id.btn_ok);
		cancel = initButton(R.id.btn_cencle);

		// wifi support
		// dataConnectType = (Spinner)findViewById(R.id.edit_data_connect_type);
		// super.setDataConnectTypeSP(dataConnectType);
		// wifi support end

		save.setOnClickListener(this);
		cancel.setOnClickListener(this);

		showView();

		addEditTextWatcher();
	}

	/**
	 * 添加编辑框内容改变监听
	 */
	private void addEditTextWatcher() {
		taskName.addTextChangedListener(etWatcher);
		repeat.addTextChangedListener(etWatcher);
		url.addTextChangedListener(etWatcher);
		timeout.addTextChangedListener(etWatcher);
		interval.addTextChangedListener(etWatcher);
	}

	/**
	 * EditTextWatcher具体实现
	 */
	private EditTextWatcher etWatcher = new EditTextWatcher() {
		public void afterTextChanged(android.text.Editable s) {
			View view = getCurrentFocus();
			if (view == taskName) {
				if (s.length() < 1)
					taskName.setError(getString(R.string.task_alert_nullName));
				else
					taskName.setError(null);
			} else if (view == repeat && StringUtil.isLessThanZeroInteger(repeat.getText().toString().trim())) {
				repeat.setError(getString(R.string.task_alert_nullRepeat));
			} else if (view == url) {
				if (s.length() < 1)
					url.setError(getString(R.string.task_alert_nullUrl));
				else
					url.setError(null);
			} else if (view == interval && StringUtil.isLessThanZeroInteger(interval.getText().toString().trim())) {
				interval.setError(getString(R.string.task_alert_nullInterval));
			} else if (view == timeout) {
				String txtTimeout = timeout.getText().toString().trim();
				int equalInt = "".equals(txtTimeout) ? 0 : Integer.valueOf(txtTimeout);
				if (equalInt == 0) {
					timeout.setError(getString(R.string.task_alert_zore));
				} else if (StringUtil.isEmpty(timeout.getText().toString().trim())) {
					timeout.setError(getString(R.string.task_alert_nulldnstimeout));
				}
			}
		};
	};
	private RelativeLayout rab_time_layout;
	private RelativeLayout rab_rule_time_layout;

	/**
	 * 界面显示初始化及赋值
	 */
	private void showView() {
		// 断开网络配置
		ArrayAdapter<String> disconnectAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_task_disconnect));
		disconnectAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		disConnect.setAdapter(disconnectAdapter);

		// 如果是新建，则初始化一个模板
		if (model == null) {
			model = new TaskDNSLookUpModel();
			taskd.setCurrentTaskIdAndSequence(model);
		}

		taskName.setText(model.getTaskName());
		if (!isNew) {
			taskName.setText(model.getTaskName().toString().trim()
					.substring(model.getTaskName().indexOf("%") + 1, model.getTaskName().toString().trim().length()));
		}
		repeat.setText(String.valueOf(model.getRepeat()) + "");
		url.setText(model.getDnsTestConfig().getUrl() + "");
		timeout.setText(model.getDnsTestConfig().getTimeout() + "");
		interval.setText(String.valueOf(model.getInterVal()) + "");
		disConnect.setSelection(model.getDisConnect());
		// dataConnectType.setSelection(model.getTypeProperty()==4?1:0); //ppp 0
		// wifi 1

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
				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(TaskDNSLookup.this,
						rabAblTimeEdt.getText().toString());
				dateTimePicKDialog.dateTimePicKDialog(rabAblTimeEdt);
			}
		});
	}

	@Override
	public void saveTestTask() {
		if (StringUtil.isEmpty(taskName.getText().toString())) { // 任务名为空
			Toast.makeText(TaskDNSLookup.this, R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
			return;
		} else if (StringUtil.isEmpty(repeat.getText().toString()) || "0".equals(repeat.getText().toString().trim())) {
			Toast.makeText(TaskDNSLookup.this, R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			return;
		} else if (StringUtil.isEmpty(url.getText().toString())) {
			Toast.makeText(TaskDNSLookup.this, R.string.task_alert_nullUrl, Toast.LENGTH_SHORT).show();
			return;
		} else if (StringUtil.isLessThanZeroInteger(interval.getText().toString().trim())) {
			Toast.makeText(TaskDNSLookup.this, R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			return;
		} else if (StringUtil.isEmpty(timeout.getText().toString())) {
			if (StringUtil.isLessThanZeroInteger(timeout.getText().toString())) {
				Toast.makeText(TaskDNSLookup.this, R.string.task_alert_zore, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(TaskDNSLookup.this, R.string.task_alert_nulldnstimeout, Toast.LENGTH_SHORT).show();
			}
			return;
		} /*
		 * else if(!url.getText().toString().startsWith("http://")){
		 * Toast.makeText(TaskDNSLookup.this, R.string.sys_setting_upload_url,
		 * Toast.LENGTH_SHORT).show(); return; }
		 */else {
		}

		// 依据标记区分用户名的编辑
		model.setTaskName(taskName.getText().toString().trim());
		model.setTaskType(WalkStruct.TaskType.DNSLookUp.name());
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(repeat.getText().toString().trim().length() == 0 ? "1" : repeat.getText()
				.toString().trim()));
		model.getDnsTestConfig().setUrl(url.getText().toString().trim());
		model.setInterVal(Integer.parseInt(interval.getText().toString().trim()));
		model.getDnsTestConfig().setTimeout(Integer.parseInt(timeout.getText().toString().trim()));
		model.setDisConnect(((Long) disConnect.getSelectedItemId()).intValue());
		model.setRabRelTime(super.rabRelTimeEdt.getText().toString().trim().length() == 0 ? "50" : super.rabRelTimeEdt
				.getText().toString().trim());
		model.setRabRuelTime(super.rabAblTimeEdt.getText().toString().trim().length() == 0 ? "12:00"
				: super.rabAblTimeEdt.getText().toString().trim());
		// if (((Long)dataConnectType.getSelectedItemId()).intValue()==1)
		// //只有是WIFI的才设置
		// model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan); //
		// else
		// model.setTypeProperty(WalkCommonPara.TypeProperty_Net); //ppp

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
		Toast.makeText(getApplicationContext(),
				isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess, Toast.LENGTH_SHORT).show();
		this.finish();
	}

}
