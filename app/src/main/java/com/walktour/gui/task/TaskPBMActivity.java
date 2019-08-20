package com.walktour.gui.task;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.walktour.Utils.WalkStruct;
import com.walktour.control.bean.Verify;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.pbm.TaskPBMModel;

import java.util.List;

/**
 * 测试PBM业务
 * 
 * @author jianchao.wang
 * 
 */
public class TaskPBMActivity extends BaseTaskActivity {
	private TaskListDispose taskd = null;
	/** 任务对象 */
	private TaskPBMModel model = null;
	private int taskListId = -1;
	/** 是否新建任务 */
	private boolean isNew = true;
	/** 任务名称 */
	private EditText taskNameEditText;
	/** 测试次数 */
	private EditText repeatEditText;
	/** 业务时长 */
	private EditText durationEditText;
	/** 无数据超时 */
	private EditText nodataTimeoutEditText;
	/** 服务端IP */
	private EditText serverIPEditText;
	/** 服务端Port */
	private EditText serverPortEditText;
	/** 间隔时长 */
	private EditText interValEditText;
	/** 上行带宽采样占比 */
	private EditText upSampleRatioText;
	/** 下行带宽采样占比 */
	private EditText downSampleRatioText;
	/** 数据连接规则 */
	private Spinner disConnectSpinner;
	/** 数据连接类型 PPP or WIFI */
	// private Spinner dataConnectTypeSpinner;
	/** 并发测试 */
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
						model = (TaskPBMModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel().get(
								taskListId);
						break;
					}
				}
			} else {
				model = (TaskPBMModel) taskd.getTaskListArray().get(taskListId);
			}
			abstModel = model;
			isNew = false;
		}
		findView();
		addEditTextWatcher();
	}

	/**
	 * 查找控件
	 */
	private void findView() {
		setContentView(R.layout.task_pbm);
		(initTextView(R.id.title_txt)).setText(R.string.act_task_pbm);// 设置标题
		(initImageView(R.id.pointer)).setOnClickListener(this);
		(initRelativeLayout(R.id.advanced_arrow_rel)).setOnClickListener(this);

		this.taskNameEditText = initEditText(R.id.edit_taskname);
		this.repeatEditText = initEditText(R.id.edit_repeat);
		this.durationEditText = initEditText(R.id.edit_pbm_duration);
		this.nodataTimeoutEditText = initEditText(R.id.edit_pbm_nodataTimeout);
		this.serverIPEditText = initEditText(R.id.edit_pbm_serverIp);
		this.serverPortEditText = initEditText(R.id.edit_pbm_serverPort);
		this.interValEditText = initEditText(R.id.edit_interVal);
		this.upSampleRatioText = initEditText(R.id.edit_pbm_upSampleRatio);
		this.downSampleRatioText = initEditText(R.id.edit_pbm_downSampleRatio);
		this.disConnectSpinner = initSpinner(R.id.edit_disConnect);
		ArrayAdapter<String> disConnectAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_task_disconnect));
		disConnectAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		this.disConnectSpinner.setAdapter(disConnectAdapter);
		// this.dataConnectTypeSpinner =
		// initSpinner(R.id.edit_data_connect_type);
		// super.setDataConnectTypeSP(this.dataConnectTypeSpinner);
		Button btn_ok = initButton(R.id.btn_ok);
		Button btn_cencle = initButton(R.id.btn_cencle);
		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveTestTask();
			}
		});

		btn_cencle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TaskPBMActivity.this.finish();
			}
		});

		this.initValue();

	}

	/**
	 * 添加EditText输入监听限制
	 */
	public void addEditTextWatcher() {

		this.taskNameEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (taskNameEditText.getText().toString().trim().length() == 0) { // 任务名为空
					taskNameEditText.setError(getString(R.string.task_alert_nullName));
				}
			}
		});

		this.repeatEditText.addTextChangedListener(new EditTextWatcher() {
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
	 * 初始化数据
	 */
	private void initValue() {
		if (this.model != null) {
			this.taskNameEditText.setText(this.model
					.getTaskName()
					.toString()
					.trim()
					.substring(this.model.getTaskName().indexOf("%") + 1,
							model.getTaskName().toString().trim().length()));
			this.repeatEditText.setText(String.valueOf(model.getRepeat()));
			this.durationEditText.setText(String.valueOf(model.getDuration()));
			this.nodataTimeoutEditText.setText(String.valueOf(model.getNodataTimeout()));
			this.serverIPEditText.setText(model.getServerIP());
			this.serverPortEditText.setText(String.valueOf(model.getServerPort()));
			this.interValEditText.setText(String.valueOf(model.getInterVal()));
			this.upSampleRatioText.setText(String.valueOf(model.getUpSampleRatio()));
			this.downSampleRatioText.setText(String.valueOf(model.getDownSampleRatio()));
			disConnectSpinner.setSelection(model.getDisConnect());
			// this.dataConnectTypeSpinner.setSelection(model.getTypeProperty()
			// == 4 ? 1 : 0); // ppp:0;wifi:1
		} else {
			this.taskNameEditText.setText("PBM");
			this.repeatEditText.setText("10");
			this.durationEditText.setText("60");
			this.nodataTimeoutEditText.setText("10");
			this.interValEditText.setText("15");
			this.upSampleRatioText.setText("8");
			this.downSampleRatioText.setText("6");
			this.disConnectSpinner.setSelection(1);
			// this.dataConnectTypeSpinner.setSelection(0);
		}

	}

	/**
	 * 判断编辑框的值是否为空
	 * 
	 * @param text
	 *            编辑框
	 * @return
	 */
	private boolean isEmpty(EditText text) {
		return this.getValue(text).length() == 0;
	}

	/**
	 * 判断编辑框的值是否为空或0
	 * 
	 * @param text
	 *            编辑框
	 * @return
	 */
	private boolean isZero(EditText text) {
		if (isEmpty(text))
			return true;
		else if (this.getValue(text).equals("0"))
			return true;
		return false;
	}

	/**
	 * 获得编辑框的值
	 * 
	 * @param text
	 *            编辑框
	 * @return
	 */
	private String getValue(EditText text) {
		return text.getText().toString().trim();
	}

	/**
	 * 显示错误提示
	 * 
	 * @param text
	 *            编辑的对象
	 * @param resId
	 *            提示信息ID
	 */
	private void showError(EditText text, int resId) {
		Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
		text.setError(getString(resId));
	}

	/**
	 * 判断IP地址是否合法
	 * 
	 * @return 非法true 合法 false
	 */
	private boolean verifyIP() {
		if (!Verify.isIp(this.getValue(this.serverIPEditText))) {
			this.showError(this.serverIPEditText, R.string.task_alert_nullIP);
			return true;
		}
		return false;
	}

	/**
	 * 判断端口是否合法
	 * 
	 * @return 非法true 合法 false
	 */
	private boolean verifyPort() {
		if (!Verify.isPort(this.getValue(this.serverPortEditText))) {
			this.showError(this.serverPortEditText, R.string.task_alert_nullPort);
			return true;
		}
		return false;
	}

	/**
	 * 判断比率是否合法
	 * 
	 * @param text
	 *            输入值
	 * @return 非法true 合法false
	 */
	private boolean verifyRatio(EditText text) {
		int ratio = Integer.parseInt(this.getValue(text));
		if (ratio < 1 || ratio > 100) {
			this.showError(text, R.string.task_pbm_alert_illegal_ratio);
			return true;
		}
		return false;
	}

	@Override
	public void saveTestTask() {
		if (this.isEmpty(this.taskNameEditText)) {
			this.showError(this.taskNameEditText, R.string.task_alert_nullName);
			return;
		} else if (this.isZero(this.repeatEditText)) {
			this.showError(this.repeatEditText, R.string.task_alert_nullRepeat);
			return;
		} else if (this.isZero(this.durationEditText)) {
			this.showError(this.durationEditText, R.string.task_pbm_alert_nullDuration);
			return;
		} else if (this.isZero(this.nodataTimeoutEditText)) {
			this.showError(this.nodataTimeoutEditText, R.string.task_pbm_alert_nullNodataTimeout);
			return;
		} else if (this.verifyRatio(this.upSampleRatioText)) {
			return;
		} else if (this.verifyRatio(this.downSampleRatioText)) {
			return;
		} else if (this.verifyIP() || this.verifyPort()) {
			return;
		} else if (this.isZero(this.interValEditText)) {
			this.showError(this.interValEditText, R.string.task_alert_nullInterval);
			return;
		}
		if (model == null) {
			model = new TaskPBMModel();
			taskd.setCurrentTaskIdAndSequence(model);
		}
		model.setTaskName(this.getValue(taskNameEditText));
		model.setTaskType(WalkStruct.TaskType.PBM.name());
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(this.getValue(this.repeatEditText)));
		model.setDuration(Integer.parseInt(this.getValue(this.durationEditText)));
		model.setNodataTimeout(Integer.parseInt(this.getValue(this.nodataTimeoutEditText)));
		if (model.getNodataTimeout() >= model.getDuration()) {
			this.showError(this.nodataTimeoutEditText, R.string.task_pbm_alert_nodataTimeout_is_more);
			return;
		}
		model.setServerIP(this.getValue(this.serverIPEditText));
		model.setServerPort(Integer.parseInt(this.getValue(this.serverPortEditText)));
		model.setInterVal(Integer.parseInt(this.getValue(this.interValEditText)));
		model.setDisConnect(disConnectSpinner.getSelectedItemPosition());
		model.setUpSampleRatio(Integer.parseInt(this.getValue(this.upSampleRatioText)));
		model.setDownSampleRatio(Integer.parseInt(this.getValue(this.downSampleRatioText)));
		// if (((Long)
		// this.dataConnectTypeSpinner.getSelectedItemId()).intValue() == 1) //
		// 只有是WIFI的才设置
		// model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan); //
		// else
		// model.setTypeProperty(WalkCommonPara.TypeProperty_Net); // ppp

		if (RABTAG.equals(super.getRabTag())) {// 依标志区分并发与普通业务
			for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
				if (super.getMultiRabName().equals(taskd.getCurrentTaskList().get(i).getTaskID())) {
					taskRabModel = (TaskRabModel) taskd.getCurrentTaskList().get(i);
					break;
				}
			}
			if (isNew) {
				taskRabModel.addTaskList(model);
			} else {
				taskRabModel.getTaskModel().set(taskListId, model);
			}
		} else {
			List<TaskModel> array = taskd.getTaskListArray();
			if (isNew) {
				array.add(array.size(),model);
			} else {
				array.set(taskListId, model);
			}
			taskd.setTaskListArray(array);
		}

		Toast.makeText(getApplicationContext(),isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess, Toast.LENGTH_SHORT).show();
		this.finish();
	}
}
