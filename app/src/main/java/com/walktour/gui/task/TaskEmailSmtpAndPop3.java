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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.walktour.Utils.WalkStruct;
import com.walktour.gui.R;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.email.receive.ReceiveConfig;
import com.walktour.gui.task.parsedata.model.task.email.sendreceive.TaskEmailSmtpPop3Model;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

import java.util.List;

/***
 * 邮件自发自收测试任务配置界面
 * 
 * @author weirong.fan
 *
 */
public class TaskEmailSmtpAndPop3 extends BaseTaskActivity {

	TaskListDispose taskd = null;
	TaskEmailSmtpPop3Model model = null;
	private int taskListId = -1;
	private boolean isNew = true;

	private EditText taskNameEditText;

	private EditText repeatEditText;

	private EditText sendTimeOutEditText;
	private EditText receiveTimeOutEditText;
	private EditText interValEditText;

	private EditText portEditText;

	private EditText emailServerEditText;

	private EditText accountEditText;

	private EditText passwordEditText;

	private EditText emailPop3ServerEditText;

	private EditText emailPop3PortEditText;

	private EditText subjectEditText;

	private EditText bodyEditText;

	private EditText adjunctEditText;

	private Spinner disConnectSpinner;
	/** 发送 服务器类型：POP3还是IMAP **/
	//private Spinner serverTypeSend;
	/** 接收 服务器类型：POP3还是IMAP **/
	private Spinner serverTypeReceive;
	//private Spinner smtpAuthenticationSpinner;
	private TaskRabModel taskRabModel;
	/*邮件发送使用 SSL*/
	private Spinner et_ssl_smtp;
	/*邮件接收使用 SSL*/
	private Spinner et_ssl_pop3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_emailsmtp_pop3);
		taskd = TaskListDispose.getInstance();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("taskListId")) {
			taskListId = bundle.getInt("taskListId");
			// 根据标记做并发编辑
			if (RABTAG.equals(super.getRabTag())) {
				for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
					if (taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())) {
						model = (TaskEmailSmtpPop3Model) ((TaskRabModel) (taskd.getCurrentTaskList().get(i)))
								.getTaskModel().get(taskListId);
						break;
					}
				}
			} else {
				model = (TaskEmailSmtpPop3Model) taskd.getTaskListArray().get(taskListId);
			}
			abstModel = model;
			isNew = false;
		}
		findView();
		IntentFilter filter = new IntentFilter();
		filter.addAction(FileExplorer.ACTION_LOAD_NORMAL_FILE);
		this.registerReceiver(mReceiver, filter);
	}

	/**
	 * 当接收邮件服务器类型或使用SSL加密变化时,修改邮件接收默认端口
	 */
	private void pop3PortChange(){
		if(serverTypeReceive.getSelectedItemPosition() == 0){		//POP3
			if(et_ssl_pop3.getSelectedItemPosition() == 0){	//POP3-NONE 110;
				emailPop3PortEditText.setText("110");
			}else{	//POP3-SSL 995
				emailPop3PortEditText.setText("995");
			}
		}else if(serverTypeReceive.getSelectedItemPosition() == 1){//IMAP
			if(et_ssl_pop3.getSelectedItemPosition() == 0){	//IMAP-NONE 143;
				emailPop3PortEditText.setText("143");
			}else{	//IMAP-SSL 993
				emailPop3PortEditText.setText("993");
			}
		}
	}
	
	private void findView() {
		(initTextView(R.id.title_txt)).setText(R.string.act_task_emailsmtp_pop3);// 设置标题
		(initImageView(R.id.pointer)).setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);
		Button btn_ok = initButton(R.id.btn_ok);
		Button btn_cencle = initButton(R.id.btn_cencle);
		btn_ok.setText(" " + getString(R.string.str_save) + " ");
		btn_cencle.setText(getString(R.string.str_cancle));

		taskNameEditText = initEditText(R.id.edit_taskname);
		repeatEditText = initEditText(R.id.edit_repeat);
		sendTimeOutEditText = initEditText(R.id.edit_timeOut_send);
		receiveTimeOutEditText = initEditText(R.id.edit_timeOut_receive);
		interValEditText = initEditText(R.id.edit_interVal);
		portEditText = initEditText(R.id.edit_port);
		emailServerEditText = initEditText(R.id.edit_emailServer);
		accountEditText = initEditText(R.id.edit_account);
		passwordEditText = initEditText(R.id.edit_password);

		Button btn_view = initButton(R.id.btn_view);

		emailPop3ServerEditText = initEditText(R.id.edit_email_pop3_server);
		emailPop3PortEditText = initEditText(R.id.edit_email_pop3_port);

		subjectEditText = initEditText(R.id.edit_subject);
		bodyEditText = initEditText(R.id.edit_body);
		adjunctEditText = initEditText(R.id.edit_adjunct);
		disConnectSpinner = initSpinner(R.id.edit_disConnect);
		//smtpAuthenticationSpinner = initSpinner(R.id.edit_smtpAuthentication);

		//serverTypeSend = initSpinner(R.id.edit_serverType_send);
		serverTypeReceive = initSpinner(R.id.edit_serverType_receive);
		/*// 服务器类型
		ArrayAdapter<String> serverTypeSendadapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.email_receive_popsmtp));
		serverTypeSendadapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		serverTypeSend.setAdapter(serverTypeSendadapter);*/
		// 服务器类型
		ArrayAdapter<String> serverTypeReceiveadapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.email_receive_popsmtp));
		serverTypeReceiveadapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		serverTypeReceive.setAdapter(serverTypeReceiveadapter);
		serverTypeReceive.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				pop3PortChange();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		et_ssl_smtp = initSpinner(R.id.edit_ssl_smtp);
		ArrayAdapter<String> smtpSslAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.email_sent_ssl));
		smtpSslAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		et_ssl_smtp.setAdapter(smtpSslAdapter);
		et_ssl_smtp.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                if(position == 1){			//SSL
                	portEditText.setText("465");
                }else if(position == 2){	//TLS
                	portEditText.setText("587");
                }else{
                	portEditText.setText("25");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
        });
		
		et_ssl_pop3 = initSpinner(R.id.edit_ssl_pop3);
		et_ssl_pop3.setOnItemSelectedListener(new OnItemSelectedListener() {
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
		ArrayAdapter<String> pop3SslAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.email_receive_ssl));
		pop3SslAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		et_ssl_pop3.setAdapter(pop3SslAdapter);
		
		/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_task_disconnect)); // public_yn
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		disConnectSpinner.setAdapter(adapter);
		smtpAuthenticationSpinner.setAdapter(adapter);*/

		btn_view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;
				intent = new Intent(TaskEmailSmtpAndPop3.this, FileExplorer.class);
				// 添加传递参数
				Bundle bundle = new Bundle();
				bundle.putBoolean(FileExplorer.KEY_NORMAL, true);
				bundle.putString(FileExplorer.KEY_ACTION, FileExplorer.ACTION_LOAD_NORMAL_FILE);// 文件浏览类型
				bundle.putString(FileExplorer.KEY_EXTRA, FileExplorer.KEY_FILE);
				bundle.putLong(FileExplorer.KEY_FILE_SIZE, 300 * 1000);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		if (model != null) {
			taskNameEditText.setText(model.getTaskName().toString().trim()
					.substring(model.getTaskName().indexOf("%") + 1, model.getTaskName().toString().trim().length()));
			repeatEditText.setText(String.valueOf(model.getRepeat()));
			sendTimeOutEditText.setText(String.valueOf(model.getEmailSelfTestConfig().getSendTimeout()));
			receiveTimeOutEditText.setText(String.valueOf(model.getEmailSelfTestConfig().getReceiveTimeout()));
			interValEditText.setText(String.valueOf(model.getInterVal()));
			disConnectSpinner.setSelection(model.getDisConnect());
			emailServerEditText.setText(String.valueOf(model.getSmtpServer()));
			portEditText.setText(String.valueOf(model.getSmtpPort()));
			accountEditText.setText(String.valueOf(model.getAccount()));
			passwordEditText.setText(String.valueOf(model.getPassword()));
			//smtpAuthenticationSpinner.setSelection(model.getSmtpAuthentication());

			emailPop3ServerEditText.setText(String.valueOf(model.getPop3Server()));
			emailPop3PortEditText.setText(String.valueOf(model.getPop3Port()));
			subjectEditText.setText(String.valueOf(model.getSubject()));
			bodyEditText.setText(String.valueOf(model.getBody()));
			adjunctEditText.setText(String.valueOf(model.getAdjunct()));

			et_ssl_smtp.setSelection(model.getUseSSLBySmtp());
			et_ssl_pop3.setSelection(model.getUseSSLByPop3());
			
			String[] serverTypes=getResources().getStringArray(R.array.email_receive_popsmtp);
			/*for(int i=0;i<serverTypes.length;i++){
				if(serverTypes[i].equals(model.getSendServerType())){
					serverTypeSend.setSelection(i);
				}
			}*/
			for(int i=0;i<serverTypes.length;i++){
				if(serverTypes[i].equals(model.getReceiveServerType())){
					serverTypeReceive.setSelection(i);
				}
			}
		} else {
			taskNameEditText.setText("Email Send_Recevice");
			subjectEditText.setText("Email Send");
			bodyEditText.setText("This is a Email");
			repeatEditText.setText("10");
			sendTimeOutEditText.setText("300");
			receiveTimeOutEditText.setText("300");
			interValEditText.setText("15");
			disConnectSpinner.setSelection(1);
			//serverTypeSend.setSelection(0);
			serverTypeReceive.setSelection(0);
			//smtpAuthenticationSpinner.setSelection(1);
			
			emailPop3PortEditText.setText("110");
			portEditText.setText("25");
			
			et_ssl_smtp.setSelection(0);
			et_ssl_pop3.setSelection(0);
			
		}
		btn_ok.setOnClickListener(this);
		btn_cencle.setOnClickListener(this);
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
			Toast.makeText(com.walktour.gui.task.TaskEmailSmtpAndPop3.this.getApplicationContext(),
					R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
			taskNameEditText.setError(getResources().getString(R.string.task_alert_nullName));
			return;
		} else if (repeatEditText.getText().toString().trim().equals("0")
				|| repeatEditText.getText().toString().trim().length() == 0) {
			repeatEditText.setError(getResources().getString(R.string.task_alert_nullRepeat));
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			return;
		} else if (emailServerEditText.getText().toString().trim().length() == 0) { // 邮箱服务器为空
			Toast.makeText(com.walktour.gui.task.TaskEmailSmtpAndPop3.this.getApplicationContext(),
					R.string.task_alert_nullEmailServer, Toast.LENGTH_SHORT).show();
			emailServerEditText.setError(getResources().getString(R.string.task_alert_nullEmailServer));
			return;
		} else if (accountEditText.getText().toString().trim().length() == 0) { // 帐号为空
			Toast.makeText(com.walktour.gui.task.TaskEmailSmtpAndPop3.this.getApplicationContext(),
					R.string.task_alert_nullAccount, Toast.LENGTH_SHORT).show();
			accountEditText.setError(getResources().getString(R.string.task_alert_nullAccount));
			return;
		} else if (emailPop3ServerEditText.getText().toString().trim().length() == 0) {// 收件人POP服务器为空
			Toast.makeText(com.walktour.gui.task.TaskEmailSmtpAndPop3.this.getApplicationContext(),
					R.string.task_alert_nullEmailServer, Toast.LENGTH_SHORT).show();
			emailPop3ServerEditText.setError(getResources().getString(R.string.task_alert_nullEmailServer));
			return;
		} else {}
		if (model == null){
			model = new TaskEmailSmtpPop3Model();
			taskd.setCurrentTaskIdAndSequence(model);
		}
		// 依据标记区分用户名的编辑
		model.setTaskName(taskNameEditText.getText().toString().trim());
		model.setTaskType(WalkStruct.TaskType.EmailSmtpAndPOP.name());
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(repeatEditText.getText().toString().trim().length() == 0 ? "10"
				: repeatEditText.getText().toString().trim()));
		model.getEmailSelfTestConfig()
				.setSendTimeout(Integer.parseInt(sendTimeOutEditText.getText().toString().trim().length() == 0 ? "300"
						: sendTimeOutEditText.getText().toString().trim()));
		model.getEmailSelfTestConfig()
				.setReceiveTimeout(Integer.parseInt(receiveTimeOutEditText.getText().toString().trim().length() == 0
						? "300" : receiveTimeOutEditText.getText().toString().trim()));
		model.setInterVal(Integer.parseInt(interValEditText.getText().toString().trim().length() == 0 ? "10"
				: interValEditText.getText().toString().trim()));
		model.setDisConnect(disConnectSpinner.getSelectedItemPosition());
		model.setSmtpServer(emailServerEditText.getText().toString().trim().length() == 0 ? "0"
				: emailServerEditText.getText().toString().trim());
		model.setSmtpPort(Integer.parseInt(portEditText.getText().toString().trim().length() == 0 ? "0"
				: portEditText.getText().toString().trim()));
		model.setAccount(accountEditText.getText().toString().trim().length() == 0 ? "0"
				: accountEditText.getText().toString().trim());
		model.setPassword(passwordEditText.getText().toString().trim().length() == 0 ? "0"
				: passwordEditText.getText().toString().trim());
		//model.setSmtpAuthentication(smtpAuthenticationSpinner.getSelectedItemPosition());
		
		model.setSubject(subjectEditText.getText().toString().trim().length() == 0 ? "0"
				: subjectEditText.getText().toString().trim());
		model.setBody(bodyEditText.getText().toString().trim().length() == 0 ? "0"
				: bodyEditText.getText().toString().trim());
		model.setAdjunct(adjunctEditText.getText().toString().trim().length() == 0 ? "0"
				: adjunctEditText.getText().toString().trim());

		model.setPop3Server(emailPop3ServerEditText.getText().toString().trim().length() == 0 ? "0"
				: emailPop3ServerEditText.getText().toString().trim());
		model.setPop3Port(Integer.parseInt(emailPop3PortEditText.getText().toString().trim().length() == 0 ? "0"
				: emailPop3PortEditText.getText().toString().trim()));
		//model.setSendServerType(serverTypeSend.getSelectedItemPosition()==0?ReceiveConfig.TYPE_POP3:ReceiveConfig.TYPE_IMAP);
		model.setReceiveServerType(serverTypeReceive.getSelectedItemPosition()==0?ReceiveConfig.TYPE_POP3:ReceiveConfig.TYPE_IMAP);
		
		model.setUseSSLBySmtp(et_ssl_smtp.getSelectedItemPosition());
		model.setUseSSLByPop3(et_ssl_pop3.getSelectedItemPosition());

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
		TaskEmailSmtpAndPop3.this.finish();
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(FileExplorer.ACTION_LOAD_NORMAL_FILE)) {
				String filePath = "";
				try {
					filePath = intent.getStringExtra(FileExplorer.KEY_FILE);
				} catch (Exception e) {

				}
				adjunctEditText.setText(filePath);
			}

		}
	};

	@Override
	protected void onDestroy() {
		this.unregisterReceiver(mReceiver);
		super.onDestroy();
	}

}
