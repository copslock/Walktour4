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
import android.widget.Spinner;

import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.email.receive.ReceiveConfig;
import com.walktour.gui.task.parsedata.model.task.email.receive.TaskEmailPop3Model;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

import java.util.List;

/**
 * 邮箱接收测试编辑页面
 * 
 * @author jianchao.wang
 * 
 */
public class TaskEmailPop3 extends BaseTaskActivity {
	TaskListDispose taskd = null;
	TaskEmailPop3Model model = null;
	private int taskListId = -1;
	private boolean isNew = true;

	private EditText taskNameEditText;
	private EditText repeatEditText;
	private EditText timeOutEditText;
	private EditText interValEditText;
	private EditText emailServerEditText;
	private EditText accountEditText;
	private EditText passwordEditText;
	private EditText portEditText;
	private Spinner disConnectSpinner;
	private Spinner sslSpinner;
	/** 服务器类型：POP3还是IMAP **/
	private Spinner serverType;
	private TaskRabModel taskRabModel;// 并发model

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
						model = (TaskEmailPop3Model) ((TaskRabModel) (taskd.getCurrentTaskList().get(i)))
								.getTaskModel().get(taskListId);
						break;
					}
				}
			} else {
				model = (TaskEmailPop3Model) taskd.getTaskListArray().get(taskListId);
			}
			abstModel = model;
			isNew = false;
		}
		findView();
		addEditTextWatcher();
	}

	/**
	 * 当接收邮件服务器类型或使用SSL加密变化时,修改邮件接收默认端口
	 */
	private void pop3PortChange(){
		if(null != model 
				&& serverType.getSelectedItemPosition() == getSpinnerIndexByModelStr(R.array.email_receive_popsmtp, model.getServerType())
				&& sslSpinner.getSelectedItemPosition() == model.getUseSSL()){
			portEditText.setText(String.valueOf(model.getPort()));
		}else if(serverType.getSelectedItemPosition() == 0){		//POP3
			if(sslSpinner.getSelectedItemPosition() == 0){	//POP3-NONE 110;
				portEditText.setText("110");
			}else{	//POP3-SSL 995
				portEditText.setText("995");
			}
		}else if(serverType.getSelectedItemPosition() == 1){//IMAP
			if(sslSpinner.getSelectedItemPosition() == 0){	//IMAP-NONE 143;
				portEditText.setText("143");
			}else{	//IMAP-SSL 993
				portEditText.setText("993");
			}
		}
		
	}
	
	private void findView() {
		setContentView(R.layout.task_emailpop3);
		initTextView(R.id.title_txt).setText(R.string.act_task_emailpop3);// 设置标题
		initImageView(R.id.pointer).setOnClickListener(this);
		initRelativeLayout(R.id.advanced_arrow_rel).setOnClickListener(this);
		Button btn_ok = initButton(R.id.btn_ok);
		Button btn_cencle = initButton(R.id.btn_cencle);
		initTextView(R.id.txt_taskname).setText(getString(R.string.task_taskName));
		initTextView(R.id.txt_repeat).setText(getString(R.string.task_repeat));
		initTextView(R.id.txt_timeOut).setText(getString(R.string.task_receiveTimeOut));
		initTextView(R.id.txt_emailServer).setText(getString(R.string.task_email_pop3_server));
		initTextView(R.id.txt_serverType).setText(getString(R.string.task_email_server_type_receive));
		initTextView(R.id.txt_account).setText(getString(R.string.task_receive_email));
		initTextView(R.id.txt_password).setText(getString(R.string.task_password));
		initTextView(R.id.txt_port).setText(getString(R.string.task_port));
		initTextView(R.id.txt_interVal).setText(getString(R.string.task_interVal));
		initTextView(R.id.txt_disConnect).setText(getString(R.string.task_disConnect));
		initTextView(R.id.txt_ssl).setText(getString(R.string.task_email_ssl));
		btn_ok.setText(" " + getString(R.string.str_save) + " ");
		btn_cencle.setText(getString(R.string.str_cancle));

		taskNameEditText = initEditText(R.id.edit_taskname);
		repeatEditText = initEditText(R.id.edit_repeat);
		timeOutEditText = initEditText(R.id.edit_timeOut);
		interValEditText = initEditText(R.id.edit_interVal);
		emailServerEditText = initEditText(R.id.edit_emailServer);
		accountEditText = initEditText(R.id.edit_account);
		passwordEditText = initEditText(R.id.edit_password);
		portEditText = initEditText(R.id.edit_port);
		disConnectSpinner = initSpinner(R.id.edit_disConnect);
		serverType = initSpinner(R.id.edit_serverType);
		sslSpinner = initSpinner(R.id.edit_ssl);

		// 服务器类型
		ArrayAdapter<String> serverTypeadapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.email_receive_popsmtp));
		serverTypeadapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		serverType.setAdapter(serverTypeadapter);
		serverType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
            	pop3PortChange();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
        });
		
		// 是否使用SSL
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.email_receive_ssl));
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		sslSpinner.setAdapter(adapter);
		sslSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
            	pop3PortChange();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
        });
		
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
			interValEditText.setText(String.valueOf(model.getInterVal()));
			disConnectSpinner.setSelection(model.getDisConnect());
			sslSpinner.setSelection(model.getUseSSL());
			emailServerEditText.setText(String.valueOf(model.getEmailServer()));
			accountEditText.setText(String.valueOf(model.getAccount()));
			passwordEditText.setText(String.valueOf(model.getPassword()));
			
			serverType.setSelection(getSpinnerIndexByModelStr(R.array.email_receive_popsmtp, model.getServerType()));
			/*String[] serverTypes = getResources().getStringArray(R.array.email_receive_popsmtp);
			for (int i = 0; i < serverTypes.length; i++) {
				if (serverTypes[i].equals(model.getServerType())) {
					serverType.setSelection(i);
				}
			}*/
			
			
		} else {
			taskNameEditText.setText("Email Recevice");
			repeatEditText.setText("10");
			timeOutEditText.setText("300");
			interValEditText.setText("15");
			disConnectSpinner.setSelection(1);
			sslSpinner.setSelection(0);
			serverType.setSelection(0);
			portEditText.setText("110");
		}
		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveTestTask();
			}
		});

		btn_cencle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TaskEmailPop3.this.finish();
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void saveTestTask() {
		if (taskNameEditText.getText().toString().trim().length() == 0) { // 任务名为空
			ToastUtil.showToastShort(getApplicationContext(), R.string.task_alert_nullName);
			taskNameEditText.setError(getString(R.string.task_alert_nullName));
			return;
		} else if (repeatEditText.getText().toString().trim().equals("0")
				|| repeatEditText.getText().toString().trim().length() == 0) {
			ToastUtil.showToastShort(getApplicationContext(), R.string.task_alert_nullRepeat);
			repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
			return;
		} else if (timeOutEditText.getText().toString().trim().equals("0")
				|| timeOutEditText.getText().toString().trim().length() == 0) {
			ToastUtil.showToastShort(getApplicationContext(), R.string.task_alert_nullTimeout);
			timeOutEditText.setError(getString(R.string.task_alert_nullTimeout));
			return;
		} else if (interValEditText.getText().toString().trim().equals("0")
				|| interValEditText.getText().toString().trim().length() == 0) {
			ToastUtil.showToastShort(getApplicationContext(), R.string.task_alert_nullInterval);
			interValEditText.setError(getString(R.string.task_alert_nullInterval));
			return;
		} else if (emailServerEditText.getText().toString().trim().length() == 0) { // 邮箱服务器为空
			ToastUtil.showToastShort(getApplicationContext(), R.string.task_alert_nullEmailServer);
			emailServerEditText.setError(getString(R.string.task_alert_nullEmailServer));
			return;
		} else if (accountEditText.getText().toString().trim().length() == 0) { // 帐号为空
			ToastUtil.showToastShort(getApplicationContext(), R.string.task_alert_nullAccount);
			accountEditText.setError(getString(R.string.task_alert_nullAccount));
			return;
		} else {
		}
		if (model == null) {
			model = new TaskEmailPop3Model();
			taskd.setCurrentTaskIdAndSequence(model);
		}
		// 依据标记区分用户名的编辑
		model.setTaskName(taskNameEditText.getText().toString().trim());
		model.setTaskType(WalkStruct.TaskType.EmailPop3.name());
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(repeatEditText.getText().toString().trim().length() == 0 ? "1"
				: repeatEditText.getText().toString().trim()));
		model.setTimeOut(Integer.parseInt(timeOutEditText.getText().toString().trim().length() == 0 ? "900"
				: timeOutEditText.getText().toString().trim()));
		model.setInterVal(Integer.parseInt(interValEditText.getText().toString().trim().length() == 0 ? "60"
				: interValEditText.getText().toString().trim()));
		model.setDisConnect(disConnectSpinner.getSelectedItemPosition());
		model.setEmailServer(emailServerEditText.getText().toString().trim().length() == 0 ? "0" : emailServerEditText
				.getText().toString().trim());
		model.setAccount(accountEditText.getText().toString().trim().length() == 0 ? "0" : accountEditText.getText()
				.toString().trim());
		model.setPassword(passwordEditText.getText().toString().trim().length() == 0 ? "0" : passwordEditText.getText()
				.toString().trim());
		model.setPort(Integer.parseInt(portEditText.getText().toString().trim().length() == 0 ? "0" : portEditText
				.getText().toString().trim()));
		model.setUseSSL(sslSpinner.getSelectedItemPosition());
		model.setServerType(serverType.getSelectedItemPosition() == 0 ? ReceiveConfig.TYPE_POP3
				: ReceiveConfig.TYPE_IMAP);
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

		ToastUtil.showToastShort(getApplicationContext(), isNew ? R.string.task_alert_newSucess
				: R.string.task_alert_updateSucess);
		TaskEmailPop3.this.finish();
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
					repeatEditText.setError(getString(R.string.task_alert_nullName));
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
