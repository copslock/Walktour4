package com.walktour.gui.task;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.wechat.TaskWeChatModel;

import java.util.List;

/**
 * @deprecated
 *
 * 微信任务配置界面
 * @author jianchao.wang
 *
 */
public class TaskWeChatActivity extends BaseTaskActivity implements OnItemSelectedListener {
	/** 任务解析类 */
	TaskListDispose mTaskDispose = null;
	/** 对象模型 */
	TaskWeChatModel mModel = null;
	/** 任务列表ID */
	private int mTaskListId = -1;
	/** 是否新建任务 */
	private boolean isNew = true;
	/** 任务名称 */
	private EditText mTaskNameEditText;
	/** 测试次数 */
	private EditText mRepeatEditText;
	/** 朋友昵称 */
	private EditText mFriendNameEditText;
	/** 发送文本编辑行 */
	private RelativeLayout mSendTextLayout;
	/** 发送文本 */
	private EditText mSendTextEditText;
	/** 发送语音时长编辑行 */
	private RelativeLayout mVoiceDurationLayout;
	/** 发送语音时长 */
	private EditText mVoiceDurationText;
	/** 测试模式 */
	private Spinner mOperationTypeSpinner;
	/** 发送图片 */
	private Spinner mSendPictureTypeSpinner;
	/** 间隔时长(s) */
	private EditText mInterValEditText;
	/** 发送超时(s) */
	private EditText mSendTimeoutEditText;
	/** 拨号规则 */
	private Spinner mDisConnectSpinner;
	/** 任务对象 */
	private TaskRabModel mTaskRabModel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTaskDispose = TaskListDispose.getInstance();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("taskListId")) {
			mTaskListId = bundle.getInt("taskListId");
			if (RABTAG.equals(super.getRabTag())) {
				for (int i = 0; i < mTaskDispose.getCurrentTaskList().size(); i++) {
					if (mTaskDispose.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())) {
						mModel = (TaskWeChatModel) ((TaskRabModel) (mTaskDispose.getCurrentTaskList().get(i))).getTaskModel()
								.get(mTaskListId);
						break;
					}
				}
			} else {
				mModel = (TaskWeChatModel) mTaskDispose.getTaskListArray().get(mTaskListId);
			}
			abstModel = mModel;
			isNew = false;
		}
		findView();
		this.addEditTextWatcher(this.mTaskNameEditText, R.string.task_alert_nullName, false);
		this.addEditTextWatcher(this.mRepeatEditText, R.string.task_alert_nullRepeat, true);
		this.addEditTextWatcher(this.mInterValEditText, R.string.task_alert_nullInterval, true);
		this.addEditTextWatcher(this.mVoiceDurationText, R.string.wechat_null_send_voice_duration, true);
	}

	private void findView() {
		setContentView(R.layout.task_wechat);
		(initTextView(R.id.title_txt)).setText(R.string.wechat_title);// 设置标题
		(initImageView(R.id.pointer)).setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);

		this.mTaskNameEditText = initEditText(R.id.edit_taskname);
		this.mRepeatEditText = initEditText(R.id.edit_repeat);
		this.mFriendNameEditText = initEditText(R.id.edit_friend_name);
		this.mSendTextEditText = initEditText(R.id.edit_send_text);
		this.mSendTextLayout = (RelativeLayout) findViewById(R.id.edit_send_text_layout);
		this.mSendPictureTypeSpinner = (Spinner) findViewById(R.id.edit_send_picture_type);
		this.mVoiceDurationText = initEditText(R.id.edit_send_voice_duration);
		this.mVoiceDurationLayout = (RelativeLayout) findViewById(R.id.edit_send_voice_duration_layout);
		this.mInterValEditText = initEditText(R.id.edit_interVal);
		this.mSendTimeoutEditText = initEditText(R.id.edit_sendTimeout);
		this.mOperationTypeSpinner = (Spinner) findViewById(R.id.edit_operation_type);
		ArrayAdapter<String> operationTypeAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.wechat_operation_type));
		operationTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		this.mOperationTypeSpinner.setAdapter(operationTypeAdapter);
		this.mOperationTypeSpinner.setOnItemSelectedListener(this);
		this.mDisConnectSpinner = (Spinner) findViewById(R.id.edit_disConnect);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_task_disconnect));
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		this.mDisConnectSpinner.setAdapter(adapter);

		ArrayAdapter<String> pictureTypeAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				new String[] { "1M", "3M", "5M", "10M" });
		pictureTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		this.mSendPictureTypeSpinner.setAdapter(pictureTypeAdapter);

		if (mModel != null) {
			this.mTaskNameEditText.setText(mModel.getTaskName().toString().trim()
					.substring(mModel.getTaskName().indexOf("%") + 1, mModel.getTaskName().toString().trim().length()));
			this.mRepeatEditText.setText(String.valueOf(mModel.getRepeat()));
			this.mFriendNameEditText.setText(mModel.getFriendName());
			this.mSendTextEditText.setText(mModel.getSendText());
			this.mVoiceDurationText.setText(String.valueOf(mModel.getVoiceDuration()));
			this.mSendTimeoutEditText.setText(String.valueOf(mModel.getSendTimeout()));
			this.mSendPictureTypeSpinner.setSelection(mModel.getSendPictureType());
			this.mOperationTypeSpinner.setSelection(mModel.getOperationType());
			this.mInterValEditText.setText(String.valueOf(mModel.getInterVal()));
			this.mDisConnectSpinner.setSelection(mModel.getDisConnect());
		} else {
			this.mTaskNameEditText.setText(this.getString(R.string.wechat_title));
			this.mRepeatEditText.setText("10");
			this.mInterValEditText.setText("15");
			this.mVoiceDurationText.setText("2");
			this.mSendTextEditText.setText("How are you?");
			this.mSendTimeoutEditText.setText("20");
			this.mDisConnectSpinner.setSelection(1);
			this.mSendPictureTypeSpinner.setSelection(3);
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
				TaskWeChatActivity.this.finish();
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
	}

	@Override
	public void saveTestTask() {
		if (mTaskNameEditText.getText().toString().trim().length() == 0) { // 任务名为空
			Toast.makeText(com.walktour.gui.task.TaskWeChatActivity.this.getApplicationContext(),
					R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
			mTaskNameEditText.setError(getString(R.string.task_alert_nullName));
			return;
		} else if (mRepeatEditText.getText().toString().trim().equals("0")
				|| mRepeatEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			mRepeatEditText.setError(getString(R.string.task_alert_nullRepeat));
			return;
		} else if (this.mFriendNameEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(com.walktour.gui.task.TaskWeChatActivity.this.getApplicationContext(),
					R.string.wechat_null_friend_name, Toast.LENGTH_SHORT).show();
			this.mFriendNameEditText.setError(getString(R.string.wechat_null_friend_name));
			return;
		} else if (this.mSendTextEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(com.walktour.gui.task.TaskWeChatActivity.this.getApplicationContext(),
					R.string.wechat_null_send_text, Toast.LENGTH_SHORT).show();
			mSendTextEditText.setError(getString(R.string.wechat_null_send_text));
			return;
		} else if (this.mVoiceDurationText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.wechat_null_send_voice_duration, Toast.LENGTH_SHORT).show();
			mVoiceDurationText.setError(getString(R.string.wechat_null_send_voice_duration));
			return;
		} else if (mInterValEditText.getText().toString().trim().equals("0")
				|| mInterValEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			mInterValEditText.setError(getString(R.string.task_alert_nullInterval));
			return;
		} else if (this.mSendTimeoutEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.wechat_null_send_time_out, Toast.LENGTH_SHORT).show();
			mSendTimeoutEditText.setError(getString(R.string.wechat_null_send_time_out));
			return;
		}
		if (mModel == null) {
			mModel = new TaskWeChatModel();
			mTaskDispose.setCurrentTaskIdAndSequence(mModel);
		}
		// 依据标记区分用户名的编辑
		mModel.setTaskName(mTaskNameEditText.getText().toString().trim());
		mModel.setTaskType(WalkStruct.TaskType.WeChat.name());
		mModel.setEnable(1);
		mModel.setRepeat(Integer.parseInt(mRepeatEditText.getText().toString().trim().length() == 0 ? "10"
				: mRepeatEditText.getText().toString().trim()));
		mModel.setInterVal(Integer.parseInt(mInterValEditText.getText().toString().trim().length() == 0 ? "15"
				: mInterValEditText.getText().toString().trim()));
		mModel.setDisConnect(mDisConnectSpinner.getSelectedItemPosition());
		mModel.setFriendName(this.mFriendNameEditText.getText().toString());
		mModel.setSendText(this.mSendTextEditText.getText().toString());
		mModel.setVoiceDuration(Integer.parseInt(this.mVoiceDurationText.getText().toString().trim().length() == 0 ? "1"
				: mVoiceDurationText.getText().toString().trim()));
		mModel.setSendTimeout(Integer.parseInt(this.mSendTimeoutEditText.getText().toString().trim().length() == 0 ? "20"
				: mSendTimeoutEditText.getText().toString().trim()));
		mModel.setSendPictureType(mSendPictureTypeSpinner.getSelectedItemPosition());
		mModel.setOperationType(this.mOperationTypeSpinner.getSelectedItemPosition());
		int enableLength = mTaskDispose.getTaskNames(1).length;
		List<TaskModel> array = mTaskDispose.getTaskListArray();
		if (RABTAG.equals(super.getRabTag())) {// 依标志区分并发与普通业务
			for (int i = 0; i < mTaskDispose.getCurrentTaskList().size(); i++) {
				if (super.getMultiRabName().equals(mTaskDispose.getCurrentTaskList().get(i).getTaskID())) {
					mTaskRabModel = (TaskRabModel) mTaskDispose.getCurrentTaskList().get(i);
					break;
				}
			}
			if (isNew) {
				mTaskRabModel.setTaskModelList(mTaskRabModel.addTaskList(mModel));
			} else {
				mTaskRabModel.getTaskModel().remove(mTaskListId);
				mTaskRabModel.getTaskModel().add(mTaskListId, mModel);
			}
		} else {
			if (isNew) {
				array.add(array.size(),mModel);
			} else {
				array.remove(mTaskListId);
				array.add(mTaskListId, mModel);
			}

		}
		mTaskDispose.setTaskListArray(array);

		Toast.makeText(getApplicationContext(), isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess,
				Toast.LENGTH_SHORT).show();
		TaskWeChatActivity.this.finish();
	}

	/**
	 * 添加编辑框的监听器
	 * 
	 * @param editText
	 *          编辑框
	 * @param strId
	 *          提示信息Id
	 * @param isNumber
	 *          编辑内容是否文本
	 */
	public void addEditTextWatcher(final EditText editText, final int strId, final boolean isNumber) {

		editText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				String text = editText.getText().toString().trim();
				if (StringUtil.isNullOrEmpty(text) || (isNumber && text.equals("0")))
					editText.setError(getString(strId));
			}
		});
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.edit_operation_type) {
			if (position == 0) {
				this.mVoiceDurationLayout.setVisibility(View.VISIBLE);
				this.mSendTextLayout.setVisibility(View.VISIBLE);
			} else {
				this.mVoiceDurationLayout.setVisibility(View.GONE);
				this.mSendTextLayout.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

}
