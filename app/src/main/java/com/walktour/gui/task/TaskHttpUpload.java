/**
 *
 */
package com.walktour.gui.task;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.framework.view.DateTimePickDialogUtil;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.http.upload.TaskHttpUploadModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

import java.util.List;

/**
 * @author Jihong Xie
 * Http Upload模板设置界面
 */
public class TaskHttpUpload extends BaseTaskActivity {

	private EditText taskName;
	private EditText repeat;
	private Spinner testMode;
	private EditText timeout;
	private Spinner serverType;
	private Spinner accountType;
	private EditText accountKey;
	private EditText secretKey;
	private EditText serverPath;
	private EditText username;
	private EditText password;
	private EditText url;
	private Spinner fileSource;
	private EditText filePath;

	private EditText interval;
	private EditText noDataTimeout;
	private Spinner disConnect;

	TaskListDispose taskd =null;
	TaskHttpUploadModel model =null;
	private int taskListId=-1;
	private boolean isNew=true;

	private Button save;//保存
	private Button cancel;//取消
	private ImageView advance;//高级
	private Button selectBtn;
	private TextView txtTimeout;
	private EditText fileSize;

	private TaskRabModel taskRabModel;

//	private Spinner dataConnectType;//数据连接选择：PPP or WIFI
	/**
	 * 文件选择接收器
	 */
	private MyBroadcastReceiver mEventReceiver = new MyBroadcastReceiver();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_http_upload);
		//设置标题名字
		(initTextView(R.id.title_txt)).setText(getResources().getString(R.string.act_task_httpupload));

		taskd=TaskListDispose.getInstance();
		Bundle bundle = getIntent().getExtras();
		if(bundle != null && bundle.containsKey("taskListId")){
			taskListId = bundle.getInt("taskListId");
			if(RABTAG.equals(super.getRabTag())){
				for(int i=0;i<taskd.getCurrentTaskList().size();i++){
					if(taskd.getCurrentTaskList().get(i).getTaskID().equals(super.getMultiRabName())){
						model =	(TaskHttpUploadModel)((TaskRabModel)(taskd.getCurrentTaskList().get(i))).getTaskModel().get(taskListId);
						break;
					}
				}
			}else{
				model = (TaskHttpUploadModel)taskd.getTaskListArray().get(taskListId);
			}
			abstModel = model;
			isNew=false;
		}

		showView();//控件获取
		setAdapter();//给Spinner设置Adapter
		setWidgetValue();//给控件赋值
		addEditTextWatcher();//给EditText添加内容改变监听
		regedit();
	}

	/**
	 * 注册广播接收器
	 */
	private void regedit() {
		IntentFilter filter = new IntentFilter();
		filter.addAction( FileExplorer.ACTION_LOAD_NORMAL_FILE);
		mEventReceiver = new MyBroadcastReceiver();
		this.registerReceiver(mEventReceiver, filter);
	}

	/**
	 * 给EditText添加内容改变监听
	 */
	private void addEditTextWatcher() {
		taskName.addTextChangedListener(etWatcher);
		repeat.addTextChangedListener(etWatcher);
		timeout.addTextChangedListener(etWatcher);
		username.addTextChangedListener(etWatcher);
		password.addTextChangedListener(etWatcher);
		url.addTextChangedListener(etWatcher);
		filePath.addTextChangedListener(etWatcher);
		interval.addTextChangedListener(etWatcher);
		noDataTimeout.addTextChangedListener(etWatcher);
	}

	/**
	 * EditTextWatcher具体实现
	 */
	private EditTextWatcher etWatcher = new EditTextWatcher(){
		@SuppressLint("StringFormatMatches")
		public void afterTextChanged(android.text.Editable s) {
			View view = getCurrentFocus();
			if(view == taskName && StringUtil.isEmpty(taskName.getText().toString())){
				taskName.setError(getString(R.string.task_alert_nullName));
			}else if(view == repeat && StringUtil.isLessThanZeroInteger(repeat.getText().toString())){
				repeat.setError(getString(R.string.task_alert_nullRepeat));
			}else if(view == timeout && StringUtil.isLessThanZeroInteger(repeat.getText().toString())){
				repeat.setError(getString(R.string.task_alert_nullTimeout));
			}else if(view == username && StringUtil.isEmpty(username.getText().toString())){
				username.setError(getString(R.string.task_alert_nullAccount));
			}else if(view == password && StringUtil.isEmpty(password.getText().toString())){
				password.setError(getString(R.string.task_alert_nullPassword));
			}else if(view == url && StringUtil.isEmpty(url.getText().toString())){
				url.setError(getString(R.string.task_alert_nullUrl));
			}else if(view == filePath && StringUtil.isEmpty(filePath.getText().toString())){
				filePath.setError(getString(R.string.task_alert_nullfilepath));
			}else if(view == interval && StringUtil.isLessThanZeroInteger(interval.getText().toString())){
				repeat.setError(getString(R.string.task_alert_nullInterval));
			}else if(view == noDataTimeout && StringUtil.isLessThanZeroInteger(noDataTimeout.getText().toString())){
				repeat.setError(getString(R.string.task_alert_nullnodatatimeout));
			}else if(view == noDataTimeout && !StringUtil.isWuToSAN(5, 120, noDataTimeout.getText().toString())){
				noDataTimeout.setError(getString(R.string.alert_input_toolong, 5,120));
			}
		}
	};
	private RelativeLayout rab_time_layout;
	private RelativeLayout rab_rule_time_layout;

	/**
	 * 给页面上的控件赋值
	 */
	private void setWidgetValue() {
		//如果是新建，则初始化一个模板
		if(model == null){
			model = new TaskHttpUploadModel();
			taskd.setCurrentTaskIdAndSequence(model);
		}
		if(!isNew){
			taskName.setText(model.getTaskName().toString().trim().substring(model.getTaskName().indexOf("%")+1, model.getTaskName().toString().trim().length()));
		}else{
			taskName.setText(model.getTaskName());
		}
		repeat.setText(String.valueOf(model.getRepeat()));
		testMode.setSelection(model.getTestMode());
		timeout.setText(String.valueOf(model.getTimeout()));
		serverType.setSelection(model.getServerType());
//        dataConnectType.setSelection(model.getTypeProperty()==4?1:0); //ppp 0 wifi 1
		//2013-04-10 沈清邮件要求：当服务器类型选择Youtube时，只能上传本地的视频文件，不允许自己创建文件
		serverType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				//如果是Youtube，不允许选择文件获取方式
				if(position == (TaskHttpUploadModel.SERVER_TYPE_HTTPS)){//选择0
					SpinnerAdapter adapter = fileSource.getAdapter();
					if(adapter != null && adapter.getCount() > 1){
						fileSource.setSelection(0);
						fileSource.setEnabled(Boolean.FALSE);
					}
					((LinearLayout)findViewById(R.id.task_count_layout)).setVisibility(View.VISIBLE);
					((LinearLayout)findViewById(R.id.task_count_layout_inner)).setVisibility(View.GONE);
					((LinearLayout)findViewById(R.id.task_server_baidu)).setVisibility(View.GONE);
//					url.setText(model.getUrl());
					url.setText("http://101.201.30.203:9013/DoUploadSave.aspx");
				}else if(position == (TaskHttpUploadModel.SERVER_TYPE_YOUTUBE)){//选择1
					SpinnerAdapter adapter = fileSource.getAdapter();
					if(adapter != null && adapter.getCount() > 1){
						fileSource.setSelection(0);
						fileSource.setEnabled(Boolean.FALSE);
					}
					((LinearLayout)findViewById(R.id.task_count_layout)).setVisibility(View.VISIBLE);
					((LinearLayout)findViewById(R.id.task_count_layout_inner)).setVisibility(View.VISIBLE);
					((LinearLayout)findViewById(R.id.task_server_baidu)).setVisibility(View.GONE);
				}else{//选择2
					fileSource.setEnabled(Boolean.TRUE);
					((LinearLayout)findViewById(R.id.task_count_layout)).setVisibility(View.GONE);
					((LinearLayout)findViewById(R.id.task_server_baidu)).setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		accountType.setSelection(model.getAccountType());
		accountType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				//如果是Youtube，不允许选择文件获取方式
				if(position == 0){
					((LinearLayout)findViewById(R.id.task_baidu_count_key)).setVisibility(View.VISIBLE);
				}else{
					((LinearLayout)findViewById(R.id.task_baidu_count_key)).setVisibility(View.GONE);
					serverPath.setText("/dingli-up");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		accountKey.setText(model.getAccountKey());
		secretKey.setText(model.getSecretKey());
		serverPath.setText(model.getServerPath());
		username.setText(model.getUsername());
		password.setText(model.getPassword());
		url.setText(model.getUrl());

		fileSource.setSelection(model.getFileSource());
		fileSize.setText(String.valueOf(model.getFileSize()));
		filePath.setText(model.getFilePath());

		interval.setText(String.valueOf(model.getInterVal()));
		noDataTimeout.setText(String.valueOf(model.getNoDataTimeout()));
		disConnect.setSelection(model.getDisConnect());
	}

	/**
	 * 设置Adapter内容
	 */
	private void setAdapter() {
		//断开网络配置
		ArrayAdapter<String> disconnectAdapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_task_disconnect));
		disconnectAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		disConnect.setAdapter(disconnectAdapter);

		//Test model
		ArrayAdapter<String> testModeAdapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_ftpdownload_testmode));
		testModeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		testMode.setAdapter(testModeAdapter);
		testMode.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				if(position == TaskHttpUploadModel.BY_FILE){
					txtTimeout.setText(getString(R.string.task_httpUp_timeout));
				}else{
					txtTimeout.setText(getString(R.string.task_httpUp_duration));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		//server type
		ArrayAdapter<String> serverTypeAdapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_httpupload_servertype));
		serverTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		serverType.setAdapter(serverTypeAdapter);

		ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_custom_layout,
				ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.BaiduSpecialCount) ?
						getResources().getStringArray(R.array.array_httpupload_accountType)
						: new String[]{getResources().getStringArray(R.array.array_httpupload_accountType)[0]}
		);
		accountAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		accountType.setAdapter(accountAdapter);

		//file source
		ArrayAdapter<String> fileSourceAdapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.array_httpupload_filesource));
		fileSourceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		fileSource.setAdapter(fileSourceAdapter);

		fileSource.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				if(position == TaskHttpUploadModel.CREATE_FILE){
					EditText viewEdit=initEditText(R.id.edit_filePath);
					viewEdit.setText("");
					findViewById(R.id.file_view).setVisibility(View.GONE);
					findViewById(R.id.filesize_vorg).setVisibility(View.VISIBLE);
					fileSize.setText(String.valueOf(model.getFileSize()));
				}else{
					findViewById(R.id.file_view).setVisibility(View.VISIBLE);
					findViewById(R.id.edit_filePath).setEnabled(false);
					findViewById(R.id.filesize_vorg).setVisibility(View.GONE);
					filePath.setText(model.getFilePath());
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	/**
	 * 界面展示
	 */
	private void showView(){
		fileSize = initEditText(R.id.edit_fileSize);
		txtTimeout = initTextView(R.id.txt_timeout);
		taskName = initEditText(R.id.edit_taskname);
		repeat = initEditText(R.id.edit_repeat);
		testMode = (Spinner)findViewById(R.id.edit_testMode);
		timeout = initEditText(R.id.edit_timeout);
		serverType = (Spinner)findViewById(R.id.edit_serverType);
		accountType= (Spinner)findViewById(R.id.edit_AccountType);
		accountKey = initEditText(R.id.edit_baidu_key);
		secretKey  = initEditText(R.id.edit_baidu_secret_key);
		serverPath  = initEditText(R.id.edit_baidu_server_path);
		username = initEditText(R.id.edit_username);
		password = initEditText(R.id.edit_password);
		url = initEditText(R.id.edit_url);
		fileSource = (Spinner)findViewById(R.id.edit_fileSource);
		filePath = initEditText(R.id.edit_filePath);
		selectBtn = initButton(R.id.btn_view);
		selectBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TaskHttpUpload.this,FileExplorer.class);
				//添加传递参数
				Bundle bundle = new Bundle();
				bundle.putBoolean(FileExplorer.KEY_NORMAL, true );
				bundle.putString(FileExplorer.KEY_ACTION, FileExplorer.ACTION_LOAD_NORMAL_FILE );//文件浏览类型
				bundle.putString(FileExplorer.KEY_EXTRA, FileExplorer.KEY_FILE );
				//bundle.putLong( FileExplorer.KEY_FILE_SIZE, 25*1000*1000 );
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		interval = initEditText(R.id.edit_interval);
		noDataTimeout = initEditText(R.id.edit_nodatatimeout);
		disConnect = (Spinner)findViewById(R.id.edit_disConnect);
		((RelativeLayout)findViewById(R.id.advanced_arrow_rel)).setOnClickListener(this);
		(initImageView(R.id.pointer)).setOnClickListener(this);
		save = initButton(R.id.btn_ok);
		cancel = initButton(R.id.btn_cencle);

		save.setOnClickListener(this);
		cancel.setOnClickListener(this);
		//wifi support
//		dataConnectType = (Spinner)findViewById(R.id.edit_data_connect_type);
//		super.setDataConnectTypeSP(dataConnectType);

		//并发相对时间
		rab_time_layout = (RelativeLayout)findViewById(R.id.rab_time_layout);
		rab_rule_time_layout = (RelativeLayout)findViewById(R.id.rab_time_rel_layout);
		super.setRabTime(rab_time_layout,rab_rule_time_layout);

		//并发专用
		if(model!=null){
			super.rabRelTimeEdt.setText(model.getRabRelTime());
			super.rabAblTimeEdt.setText(model.getRabRuelTime());
		}else{
			super.rabRelTimeEdt.setText("50");
			super.rabAblTimeEdt.setText("12:00");
		}

		super.rabAblTimeEdt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
						TaskHttpUpload.this, rabAblTimeEdt.getText().toString());
				dateTimePicKDialog.dateTimePicKDialog(rabAblTimeEdt);
			}
		});
	}

	/**
	 * 保存模板数据
	 */
	@SuppressLint("StringFormatMatches")
	@Override
	public void saveTestTask() {
		if(StringUtil.isEmpty(taskName.getText().toString())){	//任务名为空
			Toast.makeText(TaskHttpUpload.this, R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
			return;
		}else if(StringUtil.isEmpty(repeat.getText().toString()) || "0".equals(repeat.getText().toString().trim())){
			Toast.makeText(TaskHttpUpload.this, R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			return;
		}else if(StringUtil.isLessThanZeroInteger(timeout.getText().toString())){
			Toast.makeText(this, R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			return;
		}else if(StringUtil.isEmpty(username.getText().toString()) && serverType.getSelectedItemPosition() == 1){
			Toast.makeText(this, R.string.task_alert_nullAccount, Toast.LENGTH_SHORT).show();
			return;
		}else if(StringUtil.isEmpty(password.getText().toString()) && serverType.getSelectedItemPosition() == 1){
			Toast.makeText(this, R.string.task_alert_nullPassword, Toast.LENGTH_SHORT).show();
			return;
		}else if(StringUtil.isEmpty(url.getText().toString()) && (serverType.getSelectedItemPosition() == 1||serverType.getSelectedItemPosition() == 0)){
			Toast.makeText(TaskHttpUpload.this, R.string.task_alert_nullUrl, Toast.LENGTH_SHORT).show();
			return;
		}else if(fileSource.getSelectedItemPosition() == TaskHttpUploadModel.LOCAL_FILE){
			if(StringUtil.isEmpty(filePath.getText().toString())||
					(filePath.getText().toString().trim().startsWith("/")&&filePath.getText().toString().trim().length()==1)){
				Toast.makeText(TaskHttpUpload.this, R.string.task_alert_nullfilepath, Toast.LENGTH_SHORT).show();
				return;
			}
		}else if(noDataTimeout.getText().toString().trim().length() == 0||
				!StringUtil.isRange(Integer.parseInt(noDataTimeout.getText().toString()), 5, 120)){
			if(noDataTimeout.getText().toString().trim().length()==0){
				ToastUtil.showToastShort(this, R.string.task_alert_nullTimeout);
				noDataTimeout.setError(getString(R.string.task_alert_nullTimeout));
			}else{
				ToastUtil.showToastShort(this,getString(R.string.task_noAnswer)+","+String.format(getString(R.string.share_project_devices_release_relation_9), 5,120));
				noDataTimeout.setError(getString(R.string.task_noAnswer)+","+String.format(getString(R.string.share_project_devices_release_relation_9), 5,120));
			}
		}
		if(StringUtil.isLessThanZeroInteger(interval.getText().toString().trim())){
			Toast.makeText(TaskHttpUpload.this, R.string.task_alert_nullInterval, Toast.LENGTH_SHORT).show();
			return;
		}
		if(noDataTimeout.getText().toString().length()<1){
			Toast.makeText(TaskHttpUpload.this, R.string.task_alert_nullInut, Toast.LENGTH_SHORT).show();
			return;
		}
		else if(!StringUtil.isWuToSAN(5, 120, noDataTimeout.getText().toString())){
			Toast.makeText(this,getString(R.string.alert_input_toolong, 5,120), Toast.LENGTH_SHORT).show();
			return;
		}else if(serverType.getSelectedItemPosition() == 2
				&& accountType.getSelectedItemPosition() == 0
				&& StringUtil.isEmpty(accountKey.getText().toString())){
			Toast.makeText(this,getString(R.string.task_alert_nullKey), Toast.LENGTH_SHORT).show();
			return;
		}else if(serverType.getSelectedItemPosition() == 2
				&& accountType.getSelectedItemPosition() == 0
				&& StringUtil.isEmpty(secretKey.getText().toString())){
			Toast.makeText(this,getString(R.string.task_alert_nullSecretKey), Toast.LENGTH_SHORT).show();
			return;
		}else if(serverType.getSelectedItemPosition() == 2
				&& accountType.getSelectedItemPosition() == 0
				&& StringUtil.isEmpty(serverPath.getText().toString())){
			Toast.makeText(this,getString(R.string.task_alert_nullServerPath), Toast.LENGTH_SHORT).show();
			return;
		}

		model.setTaskName(taskName.getText().toString().trim());
		model.setEnable(1);
		model.setRepeat(Integer.parseInt(repeat.getText().toString()));
		model.setTestMode(testMode.getSelectedItemPosition());
		model.setTimeout(Integer.parseInt(timeout.getText().toString()));
		model.setServerType(serverType.getSelectedItemPosition());
		model.setAccountType(accountType.getSelectedItemPosition());
		model.setUrl(url.getText().toString());
		model.setAccountKey(accountKey.getText().toString());
		model.setSecretKey(secretKey.getText().toString());
		model.setServerPath(serverPath.getText().toString());
		model.setUsername(username.getText().toString());
		model.setPassword(password.getText().toString());
		model.setFileSource(fileSource.getSelectedItemPosition());
		model.setFilePath(filePath.getText().toString());
		String length = fileSize.getText().toString();
		model.setFileSize((length==null||"".equals(length))?0:Integer.parseInt(fileSize.getText().toString()));
		model.setInterVal(Integer.parseInt(interval.getText().toString()));
		model.setNoDataTimeout(Integer.parseInt(noDataTimeout.getText().toString()));
		model.setDisConnect(disConnect.getSelectedItemPosition());
		model.setRabRelTime(super.rabRelTimeEdt.getText().toString().trim().length() == 0 ? "50" : super.rabRelTimeEdt.getText().toString().trim());
		model.setRabRuelTime(super.rabAblTimeEdt.getText().toString().trim().length() == 0 ? "12:00" : super.rabAblTimeEdt.getText().toString().trim());
//		  if (((Long)dataConnectType.getSelectedItemId()).intValue()==1)  //只有是WIFI的才设置
//	  			model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);  //
//	  		else
//	  			model.setTypeProperty(WalkCommonPara.TypeProperty_Net);  //ppp
		List<TaskModel> array = taskd.getTaskListArray();
		if(RABTAG.equals(super.getRabTag())){//依标志区分并发与普通业务
			for (int i = 0; i < taskd.getCurrentTaskList().size(); i++) {
				if(super.getMultiRabName().equals(taskd.getCurrentTaskList().get(i).getTaskID())){
					taskRabModel=(TaskRabModel)taskd.getCurrentTaskList().get(i);
					break;
				}
			}
			if(isNew){
				taskRabModel.setTaskModelList(taskRabModel.addTaskList(model));
			}else{
				taskRabModel.getTaskModel().remove(taskListId);
				taskRabModel.getTaskModel().add(taskListId, model);
			}

		}else{
			if(isNew){
				array.add(array.size(), model);
			}else{
				array.remove(taskListId);
				array.add(taskListId, model);
			}
		}
		taskd.setTaskListArray(array);
		Toast.makeText(getApplicationContext(),isNew?R.string.task_alert_newSucess:R.string.task_alert_updateSucess,Toast.LENGTH_SHORT).show();
		TaskHttpUpload.this.finish();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mEventReceiver);
	}

	private class MyBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(FileExplorer.ACTION_LOAD_NORMAL_FILE)){
				String path ="";
				try{
					path = intent.getStringExtra(FileExplorer.KEY_FILE);
				}catch(Exception e){
					e.printStackTrace();
				}
				model.setFilePath(path);
				filePath.setText(path);
			}
		}
	}

	/**
	 * 页面加载完成后事件
	 * //2013-04-10 沈清邮件要求：当服务器类型选择Youtube时，只能上传本地的视频文件，不允许自己创建文件
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if(serverType.getSelectedItemPosition() == (TaskHttpUploadModel.SERVER_TYPE_HTTPS)||serverType.getSelectedItemPosition() == (TaskHttpUploadModel.SERVER_TYPE_YOUTUBE)){
			fileSource.setSelection(0);
			fileSource.setEnabled(Boolean.FALSE);
		}
	}

}
