package com.walktour.gui.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.WalkStruct;
import com.walktour.gui.R;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.email.send.TaskEmailSmtpModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

import java.io.File;
import java.util.List;

/**
 * 邮箱发送测试编辑界面
 * 
 * @author jianchao.wang
 *
 */
public class TaskEmailSmtp extends BaseTaskActivity {
	TaskListDispose taskd = null;
	TaskEmailSmtpModel model = null;
	private int taskListId = -1;
	private boolean isNew = true;
	private MyBroadcastReceiver mEventReceiver = new MyBroadcastReceiver();

	private EditText et_taskName;
	private EditText et_repeat;
	private EditText et_timeOut;
	private EditText et_interVal;
	private EditText et_port;
	private EditText et_emailServer;
	private EditText et_account;
	private EditText et_password;
	private EditText et_to;
	private EditText et_subject;
	private EditText et_body;
	private EditText et_adjunct;
	private EditText et_fileSize;
	private Spinner et_disConnect;
	private Spinner et_ssl;
	/** 服务器类型：POP3还是IMAP **/
	//private Spinner serverType;
	private TaskRabModel taskRabModel;
	private Spinner fileSource;

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
						model = (TaskEmailSmtpModel) ((TaskRabModel) (taskd.getCurrentTaskList().get(i))).getTaskModel()
								.get(taskListId);
						break;
					}
				}
			} else {
				model = (TaskEmailSmtpModel) taskd.getTaskListArray().get(taskListId);
			}
			abstModel = model;
			isNew = false;
		}
		regedit();
		showView();
	}

	private void showView() {
		setContentView(R.layout.task_emailsmtp);
		(initTextView(R.id.title_txt)).setText(R.string.act_task_emailsmtp);// 设置标题
		(initImageView(R.id.pointer)).setOnClickListener(this);
		(initRelativeLayout(R.id.advanced_arrow_rel)).setOnClickListener(this);

		TextView tv_taskName = initTextView(R.id.txt_taskname);
		TextView tv_repeat = initTextView(R.id.txt_repeat);
		TextView tv_timeOut = initTextView(R.id.txt_timeOut);
		TextView tv_interVal = initTextView(R.id.txt_interVal);
		TextView tv_disConnect = initTextView(R.id.txt_disConnect);
		TextView tv_emailServer = initTextView(R.id.txt_emailServer);
		TextView tv_port = initTextView(R.id.txt_port);
		TextView tv_account = initTextView(R.id.txt_account);
		TextView tv_password = initTextView(R.id.txt_password);
		TextView tv_to = initTextView(R.id.txt_to);
		TextView tv_subject = initTextView(R.id.txt_subject);
		TextView tv_body = initTextView(R.id.txt_body);
		TextView tv_adjunct = initTextView(R.id.txt_adjunct);
		TextView tv_fileSize = initTextView(R.id.txt_fileSize);
		TextView tv_ssl = initTextView(R.id.txt_ssl);
		Button btn_ok = initButton(R.id.btn_ok);
		Button btn_cencle = initButton(R.id.btn_cencle);
		Button btn_view = initButton(R.id.btn_view);

		tv_taskName.setText(getString(R.string.task_taskName));
		tv_repeat.setText(getString(R.string.task_repeat));
		tv_timeOut.setText(getString(R.string.task_sendTimeOut));
		tv_emailServer.setText(getString(R.string.task_email_smtp_server));
		tv_account.setText(getString(R.string.task_send_email));
		tv_port.setText(getString(R.string.task_port));
		tv_password.setText(getString(R.string.task_password));
		tv_to.setText(getString(R.string.task_receive_email));
		tv_subject.setText(getString(R.string.task_subject));
		tv_body.setText(getString(R.string.task_body));
		tv_adjunct.setText(getString(R.string.task_adjunct));
		tv_fileSize.setText(getString(R.string.task_fileSize));
		tv_interVal.setText(getString(R.string.task_interVal));
		tv_disConnect.setText(getString(R.string.task_disConnect));
		tv_ssl.setText(getString(R.string.task_email_ssl));
		btn_ok.setText(" " + getString(R.string.str_save) + " ");
		btn_cencle.setText(getString(R.string.str_cancle));
		//initTextView(R.id.txt_serverType).setText(R.string.task_email_server_type_send);
		
		et_taskName = initEditText(R.id.edit_taskname);
		et_repeat = initEditText(R.id.edit_repeat);
		et_timeOut = initEditText(R.id.edit_timeOut);
		et_interVal = initEditText(R.id.edit_interVal);
		et_port = initEditText(R.id.edit_port);
		et_emailServer = initEditText(R.id.edit_emailServer);
		et_account = initEditText(R.id.edit_account);
		et_password = initEditText(R.id.edit_password);
		et_to = initEditText(R.id.edit_to);
		et_subject = initEditText(R.id.edit_subject);
		et_body = initEditText(R.id.edit_body);
		et_adjunct = initEditText(R.id.edit_adjunct);
		et_fileSize = initEditText(R.id.edit_fileSize);
		et_adjunct = initEditText(R.id.edit_adjunct);
		et_fileSize = initEditText(R.id.edit_fileSize);
		et_disConnect = initSpinner(R.id.edit_disConnect);
		fileSource = initSpinner(R.id.edit_fileSource);
		ArrayAdapter<String> fileAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_httpupload_filesource));
		fileAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		fileSource.setAdapter(fileAdapter);
		et_ssl = initSpinner(R.id.edit_ssl);
		/*serverType = initSpinner(R.id.edit_serverType);

		// 服务器类型
		ArrayAdapter<String> serverTypeadapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.email_receive_popsmtp));
		serverTypeadapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		serverType.setAdapter(serverTypeadapter);*/

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.email_sent_ssl));
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		et_ssl.setAdapter(adapter);
		et_ssl.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
            	
            	if(model != null 
            			&& et_ssl.getSelectedItemId() == model.getUseSSL()){
            		et_port.setText(String.valueOf(model.getPort()));
            	}else  if(position == 1){			//SSL
                	et_port.setText("465");
                }else if(position == 2){	//TLS
                	et_port.setText("587");
                }else{
                	et_port.setText("25");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
        });
		
		// 断开网络配置
		ArrayAdapter<String> disconnect = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_task_disconnect));
		disconnect.setDropDownViewResource(R.layout.spinner_dropdown_item);
		et_disConnect.setAdapter(disconnect);

		/**
		 * 文件选择模式
		 */
		fileSource.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2 == 0) {
					findViewById(R.id.view_rel).setVisibility(View.VISIBLE);
					findViewById(R.id.edit_adjunct).setEnabled(false);
					findViewById(R.id.filesize_rel).setVisibility(View.GONE);
				} else {
					EditText viewEdit = initEditText(R.id.edit_adjunct);
					viewEdit.setText("");
					findViewById(R.id.view_rel).setVisibility(View.GONE);
					findViewById(R.id.filesize_rel).setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});

		if (model != null) {
			et_taskName.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%") + 1,
					model.getTaskName().toString().trim().length()));
			et_repeat.setText(String.valueOf(model.getRepeat()));
			et_timeOut.setText(String.valueOf(model.getTimeOut()));
			et_interVal.setText(String.valueOf(model.getInterVal()));
			et_disConnect.setSelection(model.getDisConnect());
			et_emailServer.setText(String.valueOf(model.getEmailServer()));
			et_port.setText(String.valueOf(model.getPort()));
			et_account.setText(String.valueOf(model.getAccount()));
			et_password.setText(String.valueOf(model.getPassword()));
			et_to.setText(String.valueOf(model.getTo()));
			et_subject.setText(String.valueOf(model.getSubject()));
			et_body.setText(String.valueOf(model.getBody()));
			et_adjunct.setText(String.valueOf(model.getAdjunct()));
			et_fileSize.setText(String.valueOf(model.getFileSize()));
			et_ssl.setSelection(model.getUseSSL());
			fileSource.setSelection(model.getFileSource());


			/*String[] serverTypes = getResources().getStringArray(R.array.email_receive_popsmtp);
			for (int i = 0; i < serverTypes.length; i++) {
				if (serverTypes[i].equals(model.getServerType())) {
					serverType.setSelection(i);
				}
			}*/

		} else {
			et_taskName.setText("Email Send");
			et_subject.setText("Email Send");
			et_body.setText("This is a Email");
			et_repeat.setText("10");
			et_timeOut.setText("300");
			et_interVal.setText("15");
			et_port.setText("25");
			et_fileSize.setText("100");
			et_disConnect.setSelection(1);
			et_ssl.setSelection(0);
			fileSource.setSelection(1);
			//serverType.setSelection(0);
		}
		btn_ok.setOnClickListener(this);

		btn_cencle.setOnClickListener(this);
		btn_view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;
				intent = new Intent(TaskEmailSmtp.this, FileExplorer.class);
				// 添加传递参数
				Bundle bundle = new Bundle();
				bundle.putBoolean(FileExplorer.KEY_NORMAL, true);
				bundle.putString(FileExplorer.KEY_ACTION, FileExplorer.ACTION_LOAD_NORMAL_FILE);// 文件浏览类型
				bundle.putString(FileExplorer.KEY_EXTRA, FileExplorer.KEY_FILE);
				bundle.putLong(FileExplorer.KEY_FILE_SIZE, 15 * 1000 * 1000);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mEventReceiver);
	}

	/**
	 * 注册广播接收器
	 */
	private void regedit() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(FileExplorer.ACTION_LOAD_NORMAL_FILE);
		mEventReceiver = new MyBroadcastReceiver();
		this.registerReceiver(mEventReceiver, filter);
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
			et_taskName.setError(getResources().getString(R.string.task_alert_nullName));
			Toast.makeText(com.walktour.gui.task.TaskEmailSmtp.this.getApplicationContext(),
					R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
			return;

		} else if (et_repeat.getText().toString().trim().equals("0")
				|| et_repeat.getText().toString().trim().length() == 0) {
			et_repeat.setError(getResources().getString(R.string.task_alert_nullRepeat));
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			return;
		} else if (et_timeOut.getText().toString().trim().equals("0")
				|| et_timeOut.getText().toString().trim().length() == 0) {
			et_timeOut.setError(getResources().getString(R.string.task_alert_nullTimeout));
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			return;
		} else if (et_interVal.getText().toString().trim().equals("0")
				|| et_interVal.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			et_interVal.setError(getResources().getString(R.string.task_alert_nullInterval));
			return;
		} else if (et_emailServer.getText().toString().trim().length() == 0) { // 邮箱服务器为空
			et_emailServer.setError(getResources().getString(R.string.task_alert_nullEmailServer));
			Toast.makeText(com.walktour.gui.task.TaskEmailSmtp.this.getApplicationContext(),
					R.string.task_alert_nullEmailServer, Toast.LENGTH_SHORT).show();
			return;
		} else if (et_fileSize.getText().toString().length() == 0) {
			et_fileSize.setError(getResources().getString(R.string.alert_inputagain));
			Toast.makeText(com.walktour.gui.task.TaskEmailSmtp.this.getApplicationContext(), R.string.alert_inputagain,
					Toast.LENGTH_SHORT).show();
			return;
		} else if (Integer.valueOf(et_fileSize.getText().toString()) > TaskEmailSmtpModel.FILE_SIZE_LIMIT / 1000
				|| Integer.valueOf(et_fileSize.getText().toString()) < 1) {
			et_fileSize.setError(getResources().getString(R.string.task_alert_nullInut) + "1~11000");
			return;
		} else if (et_account.getText().toString().trim().length() == 0) { // 帐号为空
			et_account.setError(getResources().getString(R.string.task_alert_nullAccount));
			Toast.makeText(com.walktour.gui.task.TaskEmailSmtp.this.getApplicationContext(),
					R.string.task_alert_nullAccount, Toast.LENGTH_SHORT).show();
			return;
		} else if (et_to.getText().toString().trim().length() == 0) { // 帐号为空
			et_to.setError(getResources().getString(R.string.task_alert_nullemailto));
			Toast.makeText(com.walktour.gui.task.TaskEmailSmtp.this.getApplicationContext(),
					R.string.task_alert_nullemailto, Toast.LENGTH_SHORT).show();
			return;
		} else {}
		if (model == null){
			model = new TaskEmailSmtpModel();
			taskd.setCurrentTaskIdAndSequence(model);
		}
		// 依据标记区分用户名的编辑
		model.setTaskName(et_taskName.getText().toString().trim());
		model.setTaskType(WalkStruct.TaskType.EmailSmtp.name());
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(
				et_repeat.getText().toString().trim().length() == 0 ? "10" : et_repeat.getText().toString().trim()));
		model.setTimeOut(Integer.parseInt(
				et_timeOut.getText().toString().trim().length() == 0 ? "300" : et_timeOut.getText().toString().trim()));
		model.setInterVal(Integer.parseInt(et_interVal.getText().toString().trim().length() == 0 ? "15"
				: et_interVal.getText().toString().trim()));
		model.setDisConnect(et_disConnect.getSelectedItemPosition());
		model.setEmailServer(et_emailServer.getText().toString().trim().length() == 0 ? "0"
				: et_emailServer.getText().toString().trim());
		model.setPort(Integer.parseInt(
				et_port.getText().toString().trim().length() == 0 ? "0" : et_port.getText().toString().trim()));
		model.setAccount(
				et_account.getText().toString().trim().length() == 0 ? "0" : et_account.getText().toString().trim());
		model.setPassword(
				et_password.getText().toString().trim().length() == 0 ? "0" : et_password.getText().toString().trim());
		model.setUseSSL(et_ssl.getSelectedItemPosition());
		model.setTo(et_to.getText().toString().trim().length() == 0 ? "0" : et_to.getText().toString().trim());
		model.setSubject(
				et_subject.getText().toString().trim().length() == 0 ? "0" : et_subject.getText().toString().trim());
		model.setBody(et_body.getText().toString().trim().length() == 0 ? "0" : et_body.getText().toString().trim());
		model.setFileSource(fileSource.getSelectedItemPosition());
		model.setAdjunct(
				et_adjunct.getText().toString().trim().length() == 0 ? "0" : et_adjunct.getText().toString().trim());
		model.setFileSize(Integer.parseInt(
				et_fileSize.getText().toString().trim().length() == 0 ? "0" : et_fileSize.getText().toString().trim()));
		/*model.setServerType(
				serverType.getSelectedItemPosition() == 0 ? ReceiveConfig.TYPE_POP3 : ReceiveConfig.TYPE_IMAP);*/

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
		TaskEmailSmtp.this.finish();
	}

	/**
	 * 广播接收器:接收来自Fleet.java的广播更新界面
	 */
	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(FileExplorer.ACTION_LOAD_NORMAL_FILE)) {
				String filePath = "";
				try {
					filePath = intent.getStringExtra(FileExplorer.KEY_FILE);
				} catch (Exception e) {

				}
				et_adjunct.setText(filePath);
				File file = new File(filePath);
				if (file.exists()) {
					et_fileSize.setText(String.valueOf((file.length() / 1000 < 1) ? 1 : file.length() / 1000));
				}
			}
		}

	}// end inner class EventBroadcastReceiver
}
