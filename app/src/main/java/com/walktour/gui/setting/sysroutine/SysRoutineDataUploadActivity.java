package com.walktour.gui.setting.sysroutine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.Verify;
import com.walktour.control.config.ConfigFtp;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicDialog.Builder;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.applet.MyKeyListener;
import com.walktour.gui.setting.SysFtp;
import com.walktour.gui.setting.sysroutine.dialog.SysRoutineUploadLogDialog;
import com.walktour.gui.task.FtpListActivity;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.model.FtpListUtilModel;
import com.walktour.workorder.bll.InitServer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 系统常规设置中的数据上传相关设置
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("InflateParams")
public class SysRoutineDataUploadActivity extends BasicActivity
		implements OnClickListener, OnCheckedChangeListener, OnItemSelectedListener {
	private final String TAG = "SysRoutineDataUploadActivity";
	/** 服务器管理类 */
	private ServerManager mServer;
	/** 数据上传网络显示 */
	private TextView uploadNetWorkTextView;
	/** 数据服务器显示 */
	private TextView uploadServerTextView;
	/** 上下文 */
	private SysRoutineDataUploadActivity mContext;
	/** 上传Fleet服务器布局 */
	private LinearLayout layoutFleetServer;
	/** 数据Fleet服务器IP */
	private RelativeLayout uploadFleetServerIpLayout;
	/** 数据Fleet服务器IP显示 */
	private TextView uploadFleetServerIpTextView;
	/** 数据Fleet服务器端口 */
	private RelativeLayout uploadFleetServerPortLayout;
	/** 数据Fleet服务器端口显示 */
	private TextView uploadFleetServerPortTextView;
	/** 下载Fleet服务器IP */
	private RelativeLayout downloadFleetServerIpLayout;
	/** 下载Fleet服务器IP显示 */
	private TextView downloadFleetServerIpTextView;
	/** 下载Fleet服务器端口 */
	private RelativeLayout downloadFleetServerPortLayout;
	/** 下载Fleet服务器端口显示 */
	private TextView downloadFleetServerPortTextView;
	/** 上传Fleet服务器账号 */
	private RelativeLayout uploadFleetServerAccountLayout;
	/** 上传Fleet服务器账号显示 */
	private TextView uploadFleetServerAccountTextView;
	/** 上传Fleet服务器密码 */
	private RelativeLayout uploadFleetServerPasswordLayout;
	/** 上传Fleet服务器密码显示 */
	private TextView uploadFleetServerPasswordTextView;
	/** 上传Fleet服务器事件 */
	// private RelativeLayout uploadFleetServerEventLayout;
	/** 上传Fleet服务器事件勾选 */
	// private CheckBox uploadFleetServerEventCheckBox;
	/** https上传设置 */
	private RelativeLayout layoutHttps;
	/** https上传设置显示 */
	private TextView textViewHttps;
	/** ftp上传设置 */
	private LinearLayout layoutFtp;
	/** ftp上传设置 */
	private BasicSpinner spinnerFtp;
	/** httpURL编辑框 */
	private View httpUrlLayout;
	/** DTlog日志 */
	private RelativeLayout layoutDtlog;
	/** DTlog日志显示 */
	private TextView textViewDTLog;
	/** 联通统一平台 */
	private LinearLayout layoutUnicomServer;
	/** 是否开启分布式服务器 */
	private CheckBox fleetServerType;
	/** 是否自动上传 */
	private CheckBox autoUpload;
	/** 是否邮件通知 */
	private CheckBox toggleEmail;
	/** Ftp配置 */
	private ConfigFtp configFTP = new ConfigFtp();
	/** 联通统一平台IP */
	private TextView uploadUnicomServerIpTextView;
	/** 联通统一平台Port */
	private TextView uploadUnicomServerPortTextView;
	/** 联通统一平台帐号 */
	private TextView uploadUnicomServerAccountTextView;
	/** 联通统一平台密码 */
	private TextView uploadUnicomServerPasswordTextView;
	/**
	 * 寅时服务器设置
	 */
	private LinearLayout layoutInnsServer;
	/**
	 * 寅时服务器ip地址
	 */
	private TextView tvInnsServerIp;
	/**
	 * 寅时服务器UserId
	 */
	private TextView tvInnsUserId;
	/** 数据上传路径对话框 */
	private View fileBrowseDialog;
	private ApplicationModel appModel = null;
	/** 是否同步上传MOS文件 */
	private CheckBox uploadMOSFile;
	/** 是否同步上传标注文件 */
	private CheckBox uploadTAGGINGFile;
	/** 是否从多网测试过来的界面**/
	private boolean isFromMultitest = false;
	private Activity activity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.w(TAG, "--onCreate--");
		setContentView(R.layout.sys_routine_setting_data_upload);
		this.mContext = this;
		activity=this.getParent();
		Intent intent=this.getIntent();
		if(null!=intent){
			Bundle bundle = intent.getExtras();
			if(null!=bundle){
				isFromMultitest = bundle.getBoolean("isFromMultitest");
				if(isFromMultitest){
					mServer = ServerManager.getInstance(this);
					activity=this;
				}else{
					activity=this.getParent();
					mServer = ServerManager.getInstance(activity);
				}
			}else{
				activity=this.getParent();
				mServer = ServerManager.getInstance(activity);
			}
		}


		findView();
		initData();
	}

	/**
	 * 初始化控件对象<BR>
	 * [功能详细描述]
	 */
	private void findView() {
		appModel = ApplicationModel.getInstance();
		fleetServerType = (CheckBox) this.findViewById(R.id.setting_fleet_server_type_checkbox);
		fleetServerType.setOnCheckedChangeListener(this);
		autoUpload = (CheckBox) this.findViewById(R.id.auto_upload_checkbox);
		autoUpload.setOnCheckedChangeListener(this);
		findViewById(R.id.setting_upload_network_layout).setOnClickListener(this);
		this.uploadNetWorkTextView = initTextView(R.id.setting_upload_network_text);
		findViewById(R.id.setting_upload_server_layout).setOnClickListener(this);
		findViewById(R.id.setting_upload_file_type_layout).setOnClickListener(this);
		uploadServerTextView = initTextView(R.id.setting_upload_server_text);
		layoutFleetServer = initLinearLayout(R.id.fleet_server_layout);
		uploadFleetServerIpLayout = initRelativeLayout(R.id.setting_upload_fleet_server_ip_layout);
		uploadFleetServerIpTextView = initTextView(R.id.setting_upload_fleet_server_ip_text);
		uploadFleetServerPortLayout = initRelativeLayout(R.id.setting_upload_fleet_server_port_layout);
		uploadFleetServerPortTextView = initTextView(R.id.setting_upload_fleet_server_port_text);
		downloadFleetServerIpLayout = initRelativeLayout(R.id.setting_download_fleet_server_ip_layout);
		downloadFleetServerIpTextView = initTextView(R.id.setting_download_fleet_server_ip_text);
		downloadFleetServerPortLayout = initRelativeLayout(R.id.setting_download_fleet_server_port_layout);
		downloadFleetServerPortTextView = initTextView(R.id.setting_download_fleet_server_port_text);
		uploadFleetServerAccountLayout = initRelativeLayout(R.id.setting_upload_fleet_server_account_layout);
		uploadFleetServerAccountTextView = initTextView(R.id.setting_upload_fleet_server_account_text);
		uploadFleetServerPasswordLayout = initRelativeLayout(R.id.setting_upload_fleet_server_password_layout);
		uploadFleetServerPasswordTextView = initTextView(R.id.setting_upload_fleet_server_password_text);
//		if (!ApplicationModel.getInstance().isAnHuiTest() && !ApplicationModel.getInstance().isHuaWeiTest()
//				&& !ApplicationModel.getInstance().isSingleStationTest()) {
//			uploadFleetServerAccountLayout.setVisibility(View.GONE);
//			uploadFleetServerPasswordLayout.setVisibility(View.GONE);
//		}
//		if (ApplicationModel.getInstance().isFuJianTest()) {
//			uploadFleetServerAccountLayout.setVisibility(View.VISIBLE);
//			uploadFleetServerPasswordLayout.setVisibility(View.VISIBLE);
//		}
		// uploadFleetServerEventLayout = (RelativeLayout)
		// findViewById(R.id.setting_upload_fleet_server_event_layout);
		// uploadFleetServerEventCheckBox = (CheckBox)
		// findViewById(R.id.fleet_event_checkbox);
		layoutHttps = initRelativeLayout(R.id.setting_upload_http_layout);
		textViewHttps = initTextView(R.id.setting_upload_http_text);
		layoutUnicomServer = initLinearLayout(R.id.unicom_layout);
		layoutInnsServer = initLinearLayout(R.id.inns_layout);
		tvInnsServerIp = initTextView(R.id.setting_upload_inns_server_ip_text);
		tvInnsUserId = initTextView(R.id.setting_upload_inns_server_user_id_text);
		layoutFtp = initLinearLayout(R.id.setting_upload_ftp_layout);
		spinnerFtp = (BasicSpinner) findViewById(R.id.setting_upload_ftp_spinner);
		layoutDtlog = initRelativeLayout(R.id.setting_upload_dtlog_layout);
		textViewDTLog = initTextView(R.id.setting_upload_dtlog_text);
		layoutDtlog.setOnClickListener(this);
		layoutHttps.setOnClickListener(this);
		uploadFleetServerIpLayout.setOnClickListener(this);
		uploadFleetServerPortLayout.setOnClickListener(this);
		downloadFleetServerIpLayout.setOnClickListener(this);
		downloadFleetServerPortLayout.setOnClickListener(this);
		uploadFleetServerAccountLayout.setOnClickListener(this);
		uploadFleetServerPasswordLayout.setOnClickListener(this);
		Button uploadLog = initButton(R.id.upload_log);
		uploadLog.setOnClickListener(this);
		// uploadFleetServerEventLayout.setOnClickListener(this);
		findViewById(R.id.setting_upload_ftp_path_layout).setOnClickListener(this);
		findViewById(R.id.email_send_address_layout).setOnClickListener(this);
		findViewById(R.id.email_receive_address_layout).setOnClickListener(this);
		findViewById(R.id.setting_upload_inns_server_ip_layout).setOnClickListener(this);
		findViewById(R.id.setting_upload_inns_server_user_id_layout).setOnClickListener(this);
		httpUrlLayout = LayoutInflater.from(activity).inflate(R.layout.alert_dialog_https, null);
		spinnerFtp.setOnItemSelectedListener(this);
		toggleEmail = (CheckBox) findViewById(R.id.toggle_email_checkbox);
		toggleEmail.setOnCheckedChangeListener(this);
		/** wone服务器布局 */
		findViewById(R.id.setting_upload_unicom_server_ip_layout).setOnClickListener(this);
		findViewById(R.id.setting_upload_unicom_server_port_layout).setOnClickListener(this);
		findViewById(R.id.setting_upload_unicom_server_account_layout).setOnClickListener(this);
		findViewById(R.id.setting_upload_unicom_server_password_layout).setOnClickListener(this);
		uploadUnicomServerIpTextView = initTextView(R.id.setting_upload_unicom_server_ip_text);
		uploadUnicomServerPortTextView = initTextView(R.id.setting_upload_unicom_server_port_text);
		uploadUnicomServerAccountTextView = initTextView(R.id.setting_upload_unicom_server_account_text);
		uploadUnicomServerPasswordTextView = initTextView(R.id.setting_upload_unicom_server_password_text);
		this.uploadMOSFile = (CheckBox) this.findViewById(R.id.setting_upload_mos_file_checkbox);
		this.uploadMOSFile.setOnCheckedChangeListener(this);

		this.uploadTAGGINGFile = (CheckBox) this.findViewById(R.id.setting_upload_tagging_file_checkbox);
		this.uploadTAGGINGFile.setOnCheckedChangeListener(this);
	}

	/**
	 * 初始化控件原始数据<BR>
	 * 读取配置初始化控件值
	 */
	private void initData() {
		LogUtil.w(TAG, "--initData--");

		initUploadServer();
		fleetServerType.setChecked(mServer.getFleetServerType());
		this.setFleetServerType(mServer.getFleetServerType());
		autoUpload.setChecked(mServer.isAutoUpload());
		toggleEmail.setChecked(mServer.isEmailNotifyToggle());
		(initTextView(R.id.email_send_address_text)).setText(mServer.getEmailSendAddress());
		(initTextView(R.id.email_receive_address_text)).setText(mServer.getEmailReciverAddress());
		(initTextView(R.id.setting_upload_ftp_path_txt)).setText(mServer.getFtpPath());
		ArrayAdapter<String> ftpadapter = new ArrayAdapter<String>(activity, R.layout.simple_spinner_custom_layout,
				configFTP.getAllFtpNamesFirstEmpty(activity));
		ftpadapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spinnerFtp.setAdapter(ftpadapter);

		String ftpName = mServer.getFtpName();
		if (ftpName.equals("")) {
			spinnerFtp.setSelection(0);
		} else {
			spinnerFtp.setSelection(configFTP.getPositonFirstEmpty(ftpName));
		}
		this.uploadMOSFile.setChecked(mServer.isUploadMOSFile());
		this.uploadTAGGINGFile.setChecked(mServer.isUploadTaggingFile());
		this.showFileTypes();
	}

	/**
	 * 设置选择分布式服务器的界面显示
	 *
	 * @param isChecked
	 */
	private void setFleetServerType(boolean isChecked) {
		if (isChecked) {
			downloadFleetServerIpLayout.setVisibility(View.VISIBLE);
			downloadFleetServerPortLayout.setVisibility(View.VISIBLE);
		} else {
			downloadFleetServerIpLayout.setVisibility(View.GONE);
			downloadFleetServerPortLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 显示文件类型
	 */
	private void showFileTypes() {

		StringBuilder fileTypes = new StringBuilder();

		Set<Entry<String, Boolean>> set = mServer.getUploadFileTypes(this).entrySet();
		Iterator<?> iterator = set.iterator();

		int i = 0;
		while (iterator.hasNext()) {
			@SuppressWarnings("unchecked")
			Entry<String, Boolean> entry = (Entry<String, Boolean>) iterator.next();
			if (entry.getValue()) {
				String key = entry.getKey();
				/*
				 * if (key.equals(DataModel.FILE_LTE_DGZ)) key =
				 * FileType.DTLOG.getFileTypeName();
				 */
				fileTypes.append((i > 0 ? "," : "") + key);
			}
			i++;
		}

		TextView text = initTextView(R.id.setting_upload_file_type_text);
		if (fileTypes.length() > 0 && fileTypes.toString().startsWith(",")) {
			fileTypes.replace(0, 1, "");
		}
		if (fileTypes.length() > 0)
			text.setText(fileTypes);
		else
			text.setText("");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.setting_upload_network_layout:
				showDialog(R.id.setting_upload_network_layout);
				break;
			case R.id.setting_upload_server_layout:
				showDialog(R.id.setting_upload_server_layout);
				break;
			case R.id.setting_upload_http_layout:
				showDialog(R.id.setting_upload_http_layout);
				break;
			// 发送邮件地址设置
			case R.id.email_send_address_layout:
				showDialog(R.id.email_send_address_layout);
				break;
			// 接收邮件地址设置
			case R.id.email_receive_address_layout:
				showDialog(R.id.email_receive_address_layout);
				break;
			case R.id.setting_upload_dtlog_layout:
				showDialog(R.id.setting_upload_dtlog_layout);
				break;
			case R.id.setting_upload_ftp_path_layout:
				showDialog(R.id.setting_upload_ftp_path_layout);
				break;
			case R.id.setting_upload_fleet_server_ip_layout:
				showDialog(R.id.setting_upload_fleet_server_ip_layout);
				break;
			case R.id.setting_upload_fleet_server_port_layout:
				showDialog(R.id.setting_upload_fleet_server_port_layout);
				break;
			case R.id.setting_download_fleet_server_ip_layout:
				showDialog(R.id.setting_download_fleet_server_ip_layout);
				break;
			case R.id.setting_download_fleet_server_port_layout:
				showDialog(R.id.setting_download_fleet_server_port_layout);
				break;
			case R.id.setting_upload_fleet_server_account_layout:
				showDialog(R.id.setting_upload_fleet_server_account_layout);
				break;
			case R.id.setting_upload_fleet_server_password_layout:
				showDialog(R.id.setting_upload_fleet_server_password_layout);
				break;
			case R.id.setting_upload_file_type_layout:
				showFileTypeDialog();
				break;
			case R.id.setting_upload_unicom_server_ip_layout:
				showDialog(R.id.setting_upload_unicom_server_ip_layout);
				break;
			case R.id.setting_upload_unicom_server_port_layout:
				showDialog(R.id.setting_upload_unicom_server_port_layout);
				break;
			case R.id.setting_upload_unicom_server_account_layout:
				showDialog(R.id.setting_upload_unicom_server_account_layout);
				break;
			case R.id.setting_upload_unicom_server_password_layout:
				showDialog(R.id.setting_upload_unicom_server_password_layout);
				break;
			case R.id.upload_log:
				showDialog(R.id.upload_log);
				break;
			case R.id.setting_upload_inns_server_ip_layout:
				showDialog(R.id.setting_upload_inns_server_ip_layout);
				break;
			case R.id.setting_upload_inns_server_user_id_layout:
				showDialog(R.id.setting_upload_inns_server_user_id_layout);
				break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isFromMultitest) {
				this.finish();
			} else {
				Intent intent = this.getIntent();
				if (SysRoutineActivity.SHOW_DATA_UPLOAD_TAB.equals(intent.getAction())) {
					this.finish();
				} else {
					intent = new Intent(SysRoutineActivity.SHOW_DATA_TAB);
					this.sendBroadcast(intent);
				}
			}
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.auto_upload_checkbox:
				mServer.setAutoUpload(isChecked);
				break;
			case R.id.toggle_email_checkbox:
				mServer.setEmailNotifyToggle(isChecked);
				break;
			case R.id.setting_upload_mos_file_checkbox:
				mServer.setUploadMOSFile(isChecked);
				break;
			case R.id.setting_upload_tagging_file_checkbox:
				mServer.setUploadTaggingFile(isChecked);
				break;

			case R.id.setting_fleet_server_type_checkbox:
				mServer.setFleetServerType(isChecked);
				this.setFleetServerType(isChecked);
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {
		BasicDialog.Builder builder = new BasicDialog.Builder(activity);
		switch (id) {
			case R.id.setting_upload_network_layout:
				createNetworkDialog(builder);
				break;
			case R.id.setting_upload_server_layout:
				createServerDialog(builder);
				break;
			case R.id.setting_upload_http_layout:
				createHttpsDialog(builder);
				break;
			case R.id.email_send_address_layout:
				createEmailSendAddressDialog(builder);
				break;
			case R.id.email_receive_address_layout:
				createEmailReceiveDialog(builder);
				break;
			case R.id.setting_upload_dtlog_layout:
				createDTLogDialog(builder);
				break;
			case R.id.setting_upload_ftp_path_layout:
				createFtpPathDialog(builder);
				break;
			case R.id.setting_upload_fleet_server_ip_layout:
				createUploadFleetServerIpDialog(builder);
				break;
			case R.id.setting_upload_fleet_server_port_layout:
				createUploadFleetServerPortDialog(builder);
				break;
			case R.id.setting_download_fleet_server_ip_layout:
				createDownloadFleetServerIpDialog(builder);
				break;
			case R.id.setting_download_fleet_server_port_layout:
				createDownloadFleetServerPortDialog(builder);
				break;
			case R.id.setting_upload_fleet_server_account_layout:
				createFleetServerAccountDialog(builder);
				break;
			case R.id.setting_upload_fleet_server_password_layout:
				createFleetServerPasswordDialog(builder);
				break;
			case R.id.setting_upload_unicom_server_ip_layout:
				createUnicomServerIpDialog(builder);
				break;
			case R.id.setting_upload_unicom_server_port_layout:
				createUnicomServerPortDialog(builder);
				break;
			case R.id.setting_upload_unicom_server_account_layout:
				createUnicomServerAccountDialog(builder);
				break;
			case R.id.setting_upload_unicom_server_password_layout:
				createUnicomServerPasswordDialog(builder);
				break;
			case R.id.upload_log:
				new SysRoutineUploadLogDialog(activity, builder);
				break;
			case R.id.setting_upload_inns_server_ip_layout:
				createInnsServerIpDialog(builder);
				break;
			case R.id.setting_upload_inns_server_user_id_layout:
				createInnsServerUserIdDialog(builder);
				break;
		}

		return builder.create();
	}


	/**
	 * 创建寅时服务器输入IP对话框
	 * @param builder
	 */
	private void createInnsServerIpDialog(Builder builder) {
		View view = LayoutInflater.from(activity).inflate(R.layout.alert_dialog_edittext, null);
		final EditText text = (EditText) view.findViewById(R.id.alert_textEditText);
		text.setText(this.tvInnsServerIp.getText());
		text.setSelectAllOnFocus(true);
		builder.setTitle(R.string.sys_setting_upload_inns_ip).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String ip = text.getText().toString().trim();
						if (!TextUtils.isEmpty(ip)) {
							mServer.setInnsServerIp(ip);
							tvInnsServerIp.setText(ip);
						} else {
							Toast.makeText(getParent(), getResources().getString(R.string.sys_ftp_alert_nullIP), Toast.LENGTH_SHORT)
									.show();
						}
					}
				}).setNegativeButton(R.string.str_cancle);
	}

	/**
	 * 创建寅时服务器输入UserId对话框
	 *
	 * @param builder
	 */
	private void createInnsServerUserIdDialog(Builder builder) {
		View view = LayoutInflater.from(activity).inflate(R.layout.alert_dialog_edittext, null);
		final EditText text = (EditText) view.findViewById(R.id.alert_textEditText);
		text.setText(this.tvInnsUserId.getText());
		text.setSelectAllOnFocus(true);
		builder.setTitle(R.string.sys_setting_upload_inns_user_id).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						String userId = text.getText().toString().trim();
						if (userId != null && !"".equals(userId)) {
							mServer.setInnsServerUserId(userId);
							tvInnsUserId.setText(userId);
						} else {
							Toast.makeText(getParent(), getResources().getString(R.string.sys_ftp_alert_nullAccout),
									Toast.LENGTH_SHORT).show();
						}
					}
				}).setNegativeButton(R.string.str_cancle);
	}

	/**
	 * 创建Fleet平台密码对话框
	 *
	 * @param builder
	 */
	private void createFleetServerPasswordDialog(Builder builder) {
		View view = LayoutInflater.from(activity).inflate(R.layout.alert_dialog_edittext, null);
		final EditText text = (EditText) view.findViewById(R.id.alert_textEditText);
		text.setText(this.uploadFleetServerPasswordTextView.getText());
		text.setSelectAllOnFocus(true);
		builder.setTitle(R.string.sys_setting_upload_fleet_password).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						String password = text.getText().toString().trim();
						if (password != null && !"".equals(password)) {
							mServer.setFleetPassword(password);
							uploadFleetServerPasswordTextView.setText(password);
						} else {
							Toast.makeText(getParent(), getResources().getString(R.string.sys_ftp_alert_nullPassword),
									Toast.LENGTH_SHORT).show();
						}
					}
				}).setNegativeButton(R.string.str_cancle);
	}

	/**
	 * 创建Fleet平台账户对话框
	 *
	 * @param builder
	 */
	private void createFleetServerAccountDialog(Builder builder) {
		View view = LayoutInflater.from(activity).inflate(R.layout.alert_dialog_edittext, null);
		final EditText text = (EditText) view.findViewById(R.id.alert_textEditText);
		text.setText(this.uploadFleetServerAccountTextView.getText());
		text.setSelectAllOnFocus(true);
		builder.setTitle(R.string.sys_setting_upload_fleet_account).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						String account = text.getText().toString().trim();
						if (account != null && !"".equals(account)) {
							mServer.setFleetAccount(account);
							uploadFleetServerAccountTextView.setText(account);
						} else {
							Toast.makeText(getParent(), getResources().getString(R.string.sys_ftp_alert_nullAccout),
									Toast.LENGTH_SHORT).show();
						}
					}
				}).setNegativeButton(R.string.str_cancle);
	}

	/**
	 * 创建联通统一平台密码对话框
	 *
	 * @param builder
	 */
	private void createUnicomServerPasswordDialog(Builder builder) {
		View view = LayoutInflater.from(activity).inflate(R.layout.alert_dialog_edittext, null);
		final EditText text = (EditText) view.findViewById(R.id.alert_textEditText);
		text.setText(this.uploadUnicomServerPasswordTextView.getText());
		text.setSelectAllOnFocus(true);
		builder.setTitle(R.string.sys_setting_upload_unicom_password).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						String password = text.getText().toString().trim();
						if (password != null && !"".equals(password)) {
							mServer.setUnicomPassword(password);
							uploadUnicomServerPasswordTextView.setText(password);
						} else {
							Toast.makeText(getParent(), getResources().getString(R.string.sys_ftp_alert_nullPassword),
									Toast.LENGTH_SHORT).show();
						}
					}
				}).setNegativeButton(R.string.str_cancle);
	}

	/**
	 * 创建联通统一平台帐号对话框
	 *
	 * @param builder
	 */
	private void createUnicomServerAccountDialog(Builder builder) {
		View view = LayoutInflater.from(activity).inflate(R.layout.alert_dialog_edittext, null);
		final EditText text = (EditText) view.findViewById(R.id.alert_textEditText);
		text.setText(this.uploadUnicomServerAccountTextView.getText());
		text.setSelectAllOnFocus(true);
		builder.setTitle(R.string.sys_setting_upload_unicom_account).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						String account = text.getText().toString().trim();
						if (account != null && !"".equals(account)) {
							mServer.setUnicomAccount(account);
							uploadUnicomServerAccountTextView.setText(account);
							// 切换用户后，设置“是否已经初始化服务器”为false
							InitServer.setInit(false);
						} else {
							Toast.makeText(getParent(), getResources().getString(R.string.sys_ftp_alert_nullAccout),
									Toast.LENGTH_SHORT).show();
						}
					}
				}).setNegativeButton(R.string.str_cancle);
	}

	/**
	 * 创建联通统一平台Port对话框
	 *
	 * @param builder
	 */
	private void createUnicomServerPortDialog(Builder builder) {
		View view = LayoutInflater.from(activity).inflate(R.layout.alert_dialog_edittext, null);
		final EditText text = (EditText) view.findViewById(R.id.alert_textEditText);
		text.setText(this.uploadUnicomServerPortTextView.getText());
		text.setSelectAllOnFocus(true);
		text.setKeyListener(new MyKeyListener().getNumberKeyListener());
		builder.setTitle(R.string.sys_setting_upload_unicom_port).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						String port = text.getText().toString().trim();
						if (Verify.isPort(port)) {
							mServer.setUnicomPort(Integer.parseInt(port));
							uploadUnicomServerPortTextView.setText(String.valueOf(port));
						} else {
							Toast.makeText(getParent(), getResources().getString(R.string.sys_ftp_alert_nullPort), Toast.LENGTH_SHORT)
									.show();
						}
					}
				}).setNegativeButton(R.string.str_cancle);
	}

	/**
	 * 创建联通统一平台IP对话框
	 *
	 * @param builder
	 */
	private void createUnicomServerIpDialog(Builder builder) {
		View view = LayoutInflater.from(activity).inflate(R.layout.alert_dialog_edittext, null);
		final EditText text = (EditText) view.findViewById(R.id.alert_textEditText);
		text.setText(this.uploadUnicomServerIpTextView.getText());
		text.setSelectAllOnFocus(true);
		text.setKeyListener(new MyKeyListener().getIpKeyListener());
		builder.setTitle(R.string.sys_setting_upload_unicom_ip).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String ip = text.getText().toString().trim();
						if (Verify.isIp(ip)) {
							mServer.setUnicomIp(ip);
							uploadUnicomServerIpTextView.setText(ip);
						} else {
							Toast.makeText(getParent(), getResources().getString(R.string.sys_ftp_alert_nullIP), Toast.LENGTH_SHORT)
									.show();
						}
					}
				}).setNegativeButton(R.string.str_cancle);
	}

	/**
	 * 创建上传文件类型对话框
	 */
	private void showFileTypeDialog() {

		Set<Entry<String, Boolean>> set = mServer.getUploadFileTypes(this).entrySet();
		Iterator<?> iterator = set.iterator();

		final String[] strArray = new String[set.size()];
		final boolean[] checkedItems = new boolean[set.size()];

		int i = 0;
		while (iterator.hasNext()) {
			@SuppressWarnings("unchecked")
			Entry<String, Boolean> entry = (Entry<String, Boolean>) iterator.next();
			strArray[i] = entry.getKey();
			checkedItems[i] = entry.getValue();
			i++;
		}

		new BasicDialog.Builder(getParent()).setTitle(R.string.sys_setting_data_upload_file_type)
				.setMultiChoiceItems(strArray, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						String key = strArray[which];
						mServer.getUploadFileTypes(getParent()).put(key, isChecked);
						mServer.saveUploadFileTypes();
						showFileTypes();
					}
				}).show();
	}

	/**
	 * 创建数据fleet服务器端口对话框
	 *
	 * @param builder
	 */
	private void createUploadFleetServerPortDialog(BasicDialog.Builder builder) {
		View view = LayoutInflater.from(activity).inflate(R.layout.alert_dialog_edittext, null);
		final EditText text = (EditText) view.findViewById(R.id.alert_textEditText);
		text.setText(uploadFleetServerPortTextView.getText());
		text.setSelectAllOnFocus(true);
		text.setKeyListener(new MyKeyListener().getNumberKeyListener());
		builder.setTitle(R.string.sys_setting_upload_fleet_port).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						String port = text.getText().toString().trim();
						if (Verify.isPort(port)) {
							mServer.setUploadFleetPort(Integer.parseInt(port));
							uploadFleetServerPortTextView.setText(String.valueOf(port));
						} else {
							Toast.makeText(getParent(), getResources().getString(R.string.sys_ftp_alert_nullPort), Toast.LENGTH_SHORT)
									.show();
						}
					}
				}).setNegativeButton(R.string.str_cancle);
	}

	/**
	 * 创建下载fleet服务器端口对话框
	 *
	 * @param builder
	 */
	private void createDownloadFleetServerPortDialog(BasicDialog.Builder builder) {
		View view = LayoutInflater.from(activity).inflate(R.layout.alert_dialog_edittext, null);
		final EditText text = (EditText) view.findViewById(R.id.alert_textEditText);
		text.setText(downloadFleetServerPortTextView.getText());
		text.setSelectAllOnFocus(true);
		text.setKeyListener(new MyKeyListener().getNumberKeyListener());
		builder.setTitle(R.string.sys_setting_download_fleet_port).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						String port = text.getText().toString().trim();
						if (Verify.isPort(port)) {
							mServer.setDownloadFleetPort(Integer.parseInt(port));
							downloadFleetServerPortTextView.setText(String.valueOf(port));
						} else {
							Toast.makeText(getParent(), getResources().getString(R.string.sys_ftp_alert_nullPort), Toast.LENGTH_SHORT)
									.show();
						}
					}
				}).setNegativeButton(R.string.str_cancle);
	}

	/**
	 * 创建数据Fleet服务器IP对话框
	 *
	 * @param builder
	 */
	private void createUploadFleetServerIpDialog(BasicDialog.Builder builder) {
		View view = LayoutInflater.from(activity).inflate(R.layout.alert_dialog_edittext, null);
		final EditText text = (EditText) view.findViewById(R.id.alert_textEditText);
		text.setText(uploadFleetServerIpTextView.getText());
		text.setSelectAllOnFocus(true);
		text.setKeyListener(new MyKeyListener().getIpKeyListener());
		builder.setTitle(R.string.sys_setting_upload_fleet_ip).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String ip = text.getText().toString().trim();
						if (Verify.isIp(ip)) {
							mServer.setUploadFleetIp(ip);
							uploadFleetServerIpTextView.setText(ip);
						} else {
							Toast.makeText(getParent(), getResources().getString(R.string.sys_ftp_alert_nullIP), Toast.LENGTH_SHORT)
									.show();
						}
					}
				}).setNegativeButton(R.string.str_cancle);
	}

	/**
	 * 创建下载Fleet服务器IP对话框
	 *
	 * @param builder
	 */
	private void createDownloadFleetServerIpDialog(BasicDialog.Builder builder) {
		View view = LayoutInflater.from(activity).inflate(R.layout.alert_dialog_edittext, null);
		final EditText text = (EditText) view.findViewById(R.id.alert_textEditText);
		text.setText(downloadFleetServerIpTextView.getText());
		text.setSelectAllOnFocus(true);
		text.setKeyListener(new MyKeyListener().getIpKeyListener());
		builder.setTitle(R.string.sys_setting_download_fleet_ip).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String ip = text.getText().toString().trim();
						if (Verify.isIp(ip)) {
							mServer.setDownloadFleetIp(ip);
							downloadFleetServerIpTextView.setText(ip);
						} else {
							Toast.makeText(getParent(), getResources().getString(R.string.sys_ftp_alert_nullIP), Toast.LENGTH_SHORT)
									.show();
						}
					}
				}).setNegativeButton(R.string.str_cancle);
	}

	/**
	 * 设置显示的文件上传路径
	 *
	 * @param remoteStr
	 *          路径
	 */
	public void setFtpPath(String remoteStr) {
		if (remoteStr != null) {
			((EditText) fileBrowseDialog.findViewById(R.id.setting_upload_ftp_path_edit)).setText(remoteStr);
		}

	}

	/**
	 * 浏览FTP的目录
	 */
	private void browseFTPList() {
		Intent intent = new Intent(SysRoutineDataUploadActivity.this, FtpListActivity.class);
		FtpListUtilModel.getInstance().setServerPosition(spinnerFtp.getSelectedItemPosition());
		FtpListUtilModel.getInstance().setDlOrUl(2); // 设置为1代表是ftpUpload
		activity.startActivityForResult(intent, 56);
	}

	/**
	 * 生成FTP路径设置对话框
	 *
	 * @param builder
	 */
	private void createFtpPathDialog(BasicDialog.Builder builder) {
		fileBrowseDialog = LayoutInflater.from(activity).inflate(R.layout.file_browse_dialog, null);
		fileBrowseDialog.findViewById(R.id.btn_view).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				browseFTPList();
			}
		});
		((EditText) fileBrowseDialog.findViewById(R.id.setting_upload_ftp_path_edit)).setText(mServer.getFtpPath());
		builder.setTitle(R.string.task_upload_file_path).setView(fileBrowseDialog)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						boolean isshow = true;
						String ftpPath = ((EditText) fileBrowseDialog.findViewById(R.id.setting_upload_ftp_path_edit)).getText()
								.toString();
						if (!ftpPath.startsWith("/") || !ftpPath.endsWith("/")) {
							isshow = false;
						}
						try {
							Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, isshow);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (isshow) {
							mServer.setFtpPath(ftpPath);
							(initTextView(R.id.setting_upload_ftp_path_txt)).setText(ftpPath);
						} else {
							Toast.makeText(getParent(), R.string.task_alert_nullfilepath, Toast.LENGTH_LONG).show();
						}

					}
				}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((EditText) fileBrowseDialog.findViewById(R.id.setting_upload_ftp_path_edit)).setText(mServer.getFtpPath());
				try {
					Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 生成DTlog对话框
	 *
	 * @param builder
	 */
	private void createDTLogDialog(BasicDialog.Builder builder) {
		View dtlogView = LayoutInflater.from(activity).inflate(R.layout.alert_dialog_dtlog, null);
		final EditText editIp = (EditText) dtlogView.findViewById(R.id.edit_ip);
		final EditText editPort = (EditText) dtlogView.findViewById(R.id.edit_port);
		final EditText editBoxId = (EditText) dtlogView.findViewById(R.id.edit_boxId);
		final EditText editPwd = (EditText) dtlogView.findViewById(R.id.edit_pass);
		editIp.setText(mServer.getDTLogIp());
		editPort.setText(String.valueOf(mServer.getDTLogPort()));
		editBoxId.setText(mServer.getDTLogBoxId());
		editPwd.setText(mServer.getDTLogNewPwd());
		final RelativeLayout passwordLayout = (RelativeLayout) dtlogView.findViewById(R.id.passwordlayout);
		if (ServerManager.getInstance(mContext).getUploadServer() == ServerManager.SERVER_ATU
				|| ServerManager.getInstance(mContext).getUploadServer() == ServerManager.SERVER_BTU)
			passwordLayout.setVisibility(View.GONE);
		else
			passwordLayout.setVisibility(View.VISIBLE);
		builder.setView(dtlogView).setNeutralButton(R.string.str_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String ip = editIp.getText().toString();
				String port = editPort.getText().toString();
				String boxid = editBoxId.getText().toString().trim();
				if (!Verify.isIp(ip)) {
					editIp.setError(getString(R.string.str_input) + getString(R.string.ip));
				} else if (!Verify.isPort(port)) {
					editPort.setError(getString(R.string.str_input) + getString(R.string.port));
				} else if (boxid.trim().length() == 0) {
					editBoxId.setError(getString(R.string.str_input) + "BoxId");
				} else {
					mServer.setDTLogIp(ip);
					mServer.setDTLogPort(Integer.parseInt(port));
					mServer.setDTLogBoxId(boxid);

					if (ApplicationModel.getInstance().isAtu() || ApplicationModel.getInstance().isBtu()) {
						String pwds = com.walktour.base.util.StringUtil.getHEX(boxid);
						editPwd.setText(pwds + "");
						String passwords = mServer.getDTLogNewPwd();
						if (null != passwords && passwords.length() > 0) {
							String[] passes = passwords.split("\r\n");
							if (passes.length < 3) {
								mServer.setDTLogNewPwd(pwds);
							}
						} else {
							mServer.setDTLogNewPwd(pwds);
						}
					}
					if (editPwd.getText().toString().equals(mServer.getDTLogNewPwd())) {
						// 只有密码字段发生变化时，才需要重写密码文件，比如换boxid时，是不能重填密码文个的，除知道更换的boxid对应的密码文件
						mServer.setDTLogNewPwd(editPwd.getText().toString());
					}
					dialog.dismiss();
					initData();
				}
			}
		}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
	}

	/**
	 * 生成邮件接收地址编辑对话框
	 *
	 * @param builder
	 */
	private void createEmailReceiveDialog(BasicDialog.Builder builder) {
		View view = LayoutInflater.from(activity).inflate(R.layout.alert_dialog_edittext, null);
		final EditText receiveAddressEditText = (EditText) view.findViewById(R.id.alert_textEditText);
		receiveAddressEditText.setText(mServer.getEmailReciverAddress());
		builder.setTitle(R.string.email_receive_address).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (Verify.isEmail(receiveAddressEditText.getText().toString())) {
							(initTextView(R.id.email_receive_address_text)).setText(receiveAddressEditText.getText().toString());
							mServer.setEmailReciverAddress(receiveAddressEditText.getText().toString());
						} else {
							receiveAddressEditText.setText(mServer.getEmailReciverAddress());
							Toast.makeText(getApplicationContext(), getString(R.string.alert_inputagain), Toast.LENGTH_LONG).show();
						}

					}
				}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				receiveAddressEditText.setText(mServer.getEmailReciverAddress());
				dialog.dismiss();
			}
		});
	}

	/**
	 * 生成邮件发送地址对话框
	 *
	 * @param builder
	 */
	private void createEmailSendAddressDialog(BasicDialog.Builder builder) {
		View view = LayoutInflater.from(activity).inflate(R.layout.email_notify_alert_dialog, null);
		final CheckBox emailDefaultCK = (CheckBox) view.findViewById(R.id.email_defualt_ck);
		final EditText sendAddressEditText = (EditText) view.findViewById(R.id.email_send_address_text);
		final EditText sendPassET = (EditText) view.findViewById(R.id.email_send_password_text);
		final EditText sendServerET = (EditText) view.findViewById(R.id.email_send_server_text);
		final EditText sendPort = (EditText) view.findViewById(R.id.email_send_port_text);

		sendAddressEditText.setText(mServer.getEmailSendAddress());
		sendPassET.setText(mServer.getEmailSendPassoword());
		sendServerET.setText(mServer.getEmailSendServer());
		sendPort.setText(mServer.getEmailSendPort());
		emailDefaultCK.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					sendAddressEditText.setText("dinglicom.email@gmail.com");
					sendPassET.setText("dinglicom");
					sendPort.setText("465");
					sendServerET.setText("smtp.gmail.com");
					sendAddressEditText.setEnabled(false);
					sendPassET.setEnabled(false);
					sendServerET.setEnabled(false);
					sendPort.setEnabled(false);
				} else {
					sendAddressEditText.setText(mServer.getEmailSendAddress());
					sendPassET.setText(mServer.getEmailSendPassoword());
					sendServerET.setText(mServer.getEmailSendServer());
					sendPort.setText(mServer.getEmailSendPort());
					sendAddressEditText.setEnabled(true);
					sendPassET.setEnabled(true);
					sendServerET.setEnabled(true);
					sendPort.setEnabled(true);
				}
			}
		});

		sendAddressEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				super.onTextChanged(s, start, before, count);
				int star = s.toString().indexOf("@");
				if (star != -1) {
					String smtpstr = "smtp." + s.toString().substring(star + 1);
					sendServerET.setText(smtpstr);
					int port = 25;
					if (s.toString().endsWith("@gmail.com")) {
						port = 465;
					}
					sendPort.setText(String.valueOf(port));
				}
			}
		});

		if (mServer.getEmailSendAddress().equals("dinglicom.email@gmail.com")) {
			emailDefaultCK.setChecked(true);
		} else {
			emailDefaultCK.setChecked(false);
		}

		if (emailDefaultCK.isChecked()) {
			sendAddressEditText.setText("dinglicom.email@gmail.com");
			sendPassET.setText("dinglicom");
			sendPort.setText("465");
			sendServerET.setText("smtp.gmail.com");
			sendAddressEditText.setEnabled(false);
			sendPassET.setEnabled(false);
			sendServerET.setEnabled(false);
			sendPort.setEnabled(false);
		} else {
			sendAddressEditText.setEnabled(true);
			sendPassET.setEnabled(true);
			sendServerET.setEnabled(true);
			sendPort.setEnabled(true);
		}

		builder.setTitle(R.string.email_send_address).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						boolean isshow = true;
						if (!Verify.isEmail(sendAddressEditText.getText().toString())) {
							isshow = false;
							sendAddressEditText.setError(getString(R.string.alert_inputagain));
						}

						if (StringUtil.isNullOrEmpty(sendPassET.getText().toString())) {
							isshow = false;
							sendPassET.setError(getString(R.string.alert_inputagain));
						}

						// if
						// (!CheckInput.isSmtpAddress(sendServerET.getText().toString()))
						// {
						// isshow = false;
						// sendServerET.setError(getString(R.string.alert_inputagain));
						// }

						if (!Verify.isPort(sendPort.getText().toString())) {
							isshow = false;
							sendPort.setError(getString(R.string.alert_inputagain));
						}

						try {
							Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, isshow);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (isshow) {
							(initTextView(R.id.email_send_address_text)).setText(sendAddressEditText.getText().toString());
							mServer.setEmailSendAddress(sendAddressEditText.getText().toString());
							mServer.setEmailSendPassoword(sendPassET.getText().toString());
							mServer.setEmailSendServer(sendServerET.getText().toString());
							mServer.setEmailSendPort(sendPort.getText().toString());
						} else {
							Toast.makeText(getApplicationContext(), getString(R.string.alert_inputagain), Toast.LENGTH_LONG).show();
						}
					}
				}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				try {
					Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				sendAddressEditText.setText(mServer.getEmailSendAddress());
				sendPassET.setText(mServer.getEmailSendPassoword());
				sendServerET.setText(mServer.getEmailSendServer());
				sendPort.setText(mServer.getEmailSendPort());
				dialog.dismiss();
			}
		});
	}

	/**
	 * 生成上传https的设置对话框
	 *
	 * @param builder
	 */
	private void createHttpsDialog(BasicDialog.Builder builder) {
		// 服务器类型
		final Spinner spiServerType = (Spinner) httpUrlLayout.findViewById(R.id.spinner_serverType);
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(activity, R.layout.simple_spinner_custom_layout,
				new String[] { "AT&T" });
		modeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spiServerType.setAdapter(modeAdapter);
		spiServerType.setEnabled(false);

		// URL
		final EditText httpUrlEditText = (EditText) httpUrlLayout.findViewById(R.id.edit_url);
		httpUrlEditText.setText(
				mServer.getHttpsUrl().length() == 0 ? "https://gng.wireless.att.com/HandheldUpload" : mServer.getHttpsUrl());
		// Username
		final EditText editUser = (EditText) httpUrlLayout.findViewById(R.id.edit_username);
		editUser.setText(mServer.getHttpsUsername());
		// Password
		final EditText editPass = (EditText) httpUrlLayout.findViewById(R.id.edit_password);
		editPass.setText(mServer.getHttpsPass());
		// Drive Source
		final EditText editDrive = (EditText) httpUrlLayout.findViewById(R.id.edit_drive);
		editDrive.setText(mServer.getHttpsDrive());
		// Market
		final Spinner spiMarket = (Spinner) httpUrlLayout.findViewById(R.id.spinner_market);
		ArrayAdapter<String> marketAdapter = new ArrayAdapter<String>(activity,
				R.layout.simple_spinner_custom_layout, ServerManager.ATT_MARKET);
		marketAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spiMarket.setAdapter(marketAdapter);
		spiMarket.setSelection(mServer.getHttpsMarketPosition());
		// Scope
		final Spinner spiScope = (Spinner) httpUrlLayout.findViewById(R.id.spinner_scope);
		ArrayAdapter<String> adapterScope = new ArrayAdapter<String>(activity,
				R.layout.simple_spinner_custom_layout, ServerManager.ATT_SCOPE);
		adapterScope.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spiScope.setAdapter(adapterScope);
		spiScope.setSelection(mServer.getHttpsScopePosition());
		// Description
		final EditText editDesciption = (EditText) httpUrlLayout.findViewById(R.id.edit_descrition);
		editDesciption.setText(mServer.getHttpsDescription());
		// Event
		final EditText editEvent = (EditText) httpUrlLayout.findViewById(R.id.edit_event);
		editEvent.setText(mServer.getHttpsEvent());

		builder.setTitle(R.string.sys_setting_upload_https).setView(httpUrlLayout)
				.setNeutralButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String url = httpUrlEditText.getText().toString().trim();
						String user = editUser.getText().toString().trim();
						String pass = editPass.getText().toString();
						String drive = editDrive.getText().toString().trim();
						String descrition = editDesciption.getText().toString().trim();
						String event = editEvent.getText().toString().trim();
						String market = ServerManager.ATT_MARKET[spiMarket.getSelectedItemPosition()];
						String scope = ServerManager.ATT_SCOPE[spiScope.getSelectedItemPosition()];
						if (!Verify.isUrl(url)) {
							httpUrlEditText.setError("Input URL");
							httpUrlEditText.findFocus();
						} else if (user.length() == 0) {
							editUser.setError("Input Username");
							editUser.findFocus();
						} else if (pass.length() == 0) {
							editPass.setError("Input password");
							editPass.findFocus();
						} else if (drive.length() == 0) {
							editDrive.setError("Input drive source");
							editDrive.findFocus();
						} else {
							mServer.setHttpsDescription(descrition);
							mServer.setHttpsDrive(drive);
							mServer.setHttpsEvent(event);
							mServer.setHttpsMarket(market);
							mServer.setHttpsPass(pass);
							mServer.setHttpsScope(scope);
							mServer.setHttpsUrl(url);
							mServer.setHttpsUsername(user);
							dialog.dismiss();
							initData();
						}
					}
				}).setNegativeButton(R.string.str_cancle);
	}

	/**
	 * 生成上传服务器对话框
	 *
	 * @param builder
	 */
	private void createServerDialog(BasicDialog.Builder builder) {
		int select = mServer.getUploadServer();
		List<String> serverNameList = new ArrayList<>();
		String[] uploadServers = mServer.getUploadServers();
		serverNameList.add(uploadServers[0]);
		serverNameList.add(uploadServers[1]);
		serverNameList.add(uploadServers[2]);
		if (ApplicationModel.getInstance().isBtu())
			serverNameList.add(uploadServers[3]);
		if (ApplicationModel.getInstance().isAtu())
			serverNameList.add(uploadServers[4]);
		serverNameList.add(uploadServers[5]);
		if(ApplicationModel.getInstance().hasInnsmapTest())
			serverNameList.add(uploadServers[6]);
		final String[] serverNames = new String[serverNameList.size()];
		for (int i = 0; i < serverNames.length; i++) {
			serverNames[i] = serverNameList.get(i);
		}
		builder.setTitle(R.string.sys_setting_upload_server).setSingleChoiceItems(serverNames, select,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String[] servers = mServer.getUploadServers();
						int selectServer = 0;
						for (int i = 0; i < servers.length; i++) {
							if (servers[i].equals(serverNames[which])) {
								selectServer = i;
								break;
							}
						}
						int oldServer = mServer.getUploadServer();
						if (oldServer == selectServer)
							return;
						showChangeServerDialog(selectServer);
					}
				});
	}

	/**
	 * 生成上传网络设置对话框
	 *
	 * @param builder
	 */
	private void createNetworkDialog(BasicDialog.Builder builder) {
		builder.setTitle(R.string.sys_setting_uploadnetwork).setSingleChoiceItems(mServer.getUploadNetWorks(),
				mServer.getUploadNetWork(), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mServer.setUploadNetWork(which);
						uploadNetWorkTextView.setText(mServer.getUploadNetWorks()[which]);
						dialog.dismiss();
					}
				});
	}

	/**
	 * 设置新的数据服务器类型
	 *
	 * @param selectServer
	 *          服务器序号
	 */
	private void setNewServer(int selectServer) {
		mServer.setHistoryServer(selectServer);
		mServer.setUploadServer(selectServer);
		mServer.initUploadFileType(mContext, selectServer);
		uploadServerTextView.setText(mServer.getUploadServerName());
		initUploadServer();
		showFileTypes();
	}

	/**
	 * 显示是否更改服务器确定对话框
	 *
	 * @param selectServer
	 */
	private void showChangeServerDialog(final int selectServer) {
		new Builder(activity).setMessage(R.string.sys_setting_upload_clear)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setNewServer(selectServer);
						// 切换测试平台时清空测试计划
						new Thread() {
							@Override
							public void run() {
								TaskListDispose.getInstance().deleteAllGroup();
							}
						}.start();
					}
				}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setNewServer(selectServer);
			}
		}).show();
	}

	/**
	 * 初始化上传服务器设置项<BR>
	 * [功能详细描述]
	 */
	private void initUploadServer() {
		uploadFleetServerIpTextView.setText(mServer.getUploadFleetIp());
		uploadFleetServerPortTextView.setText(String.valueOf(mServer.getUploadFleetPort()));
		downloadFleetServerIpTextView.setText(mServer.getDownloadFleetIp());
		downloadFleetServerPortTextView.setText(String.valueOf(mServer.getDownloadFleetPort()));
		uploadFleetServerAccountTextView.setText(String.valueOf(mServer.getFleetAccount()));
		uploadFleetServerPasswordTextView.setText(String.valueOf(mServer.getFleetPassword()));
		// uploadFleetServerEventCheckBox.setChecked(mServer.hasFleetEvent(mContext));
		// uploadFleetServerEventCheckBox.setOnCheckedChangeListener(new
		// OnCheckedChangeListener() {
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean
		// isChecked) {
		// mServer.setFleetEvent(mContext, isChecked);
		// }
		// });
		this.uploadUnicomServerIpTextView.setText(mServer.getUnicomIp());
		this.uploadUnicomServerPortTextView.setText(String.valueOf(mServer.getUnicomPort()));
		this.uploadUnicomServerAccountTextView.setText(mServer.getUnicomAccount());
		this.uploadUnicomServerPasswordTextView.setText(mServer.getUnicomPassword());
		this.tvInnsServerIp.setText(mServer.getInnsServerIp());
		this.tvInnsUserId.setText(mServer.getInnsServerUserId());
		uploadServerTextView.setText(mServer.getUploadServerName());
		uploadNetWorkTextView.setText(mServer.getUploadNetWorkName());
		textViewHttps.setText(mServer.getHttpsUrl());
		textViewDTLog.setText(mServer.getDTLogIp() + ":" + mServer.getDTLogPort() + "\n" + mServer.getDTLogBoxId());
		switch (mServer.getUploadServer()) {
			case ServerManager.SERVER_FLEET:
				layoutFleetServer.setVisibility(View.VISIBLE);
				layoutHttps.setVisibility(View.GONE);
				layoutFtp.setVisibility(View.GONE);
				layoutDtlog.setVisibility(View.GONE);
				layoutUnicomServer.setVisibility(View.GONE);
				layoutInnsServer.setVisibility(View.GONE);
				break;
			case ServerManager.SERVER_HTTPS:
				layoutHttps.setVisibility(View.VISIBLE);
				layoutFleetServer.setVisibility(View.GONE);
				layoutFtp.setVisibility(View.GONE);
				layoutDtlog.setVisibility(View.GONE);
				layoutUnicomServer.setVisibility(View.GONE);
				layoutInnsServer.setVisibility(View.GONE);
				break;
			case ServerManager.SERVER_FTP:
				layoutFtp.setVisibility(View.VISIBLE);
				layoutHttps.setVisibility(View.GONE);
				layoutFleetServer.setVisibility(View.GONE);
				layoutDtlog.setVisibility(View.GONE);
				layoutUnicomServer.setVisibility(View.GONE);
				layoutInnsServer.setVisibility(View.GONE);
				break;
			case ServerManager.SERVER_BTU:
			case ServerManager.SERVER_ATU:
				layoutDtlog.setVisibility(View.VISIBLE);
				layoutFtp.setVisibility(View.GONE);
				layoutHttps.setVisibility(View.GONE);
				layoutFleetServer.setVisibility(View.GONE);
				layoutUnicomServer.setVisibility(View.GONE);
				layoutInnsServer.setVisibility(View.GONE);
				break;
			case ServerManager.SERVER_UNICOM:
				layoutUnicomServer.setVisibility(View.VISIBLE);
				layoutDtlog.setVisibility(View.GONE);
				layoutFtp.setVisibility(View.GONE);
				layoutHttps.setVisibility(View.GONE);
				layoutFleetServer.setVisibility(View.GONE);
				layoutInnsServer.setVisibility(View.GONE);
				break;
			case ServerManager.SERVER_INNS:
				layoutInnsServer.setVisibility(View.VISIBLE);
				layoutUnicomServer.setVisibility(View.GONE);
				layoutDtlog.setVisibility(View.GONE);
				layoutFtp.setVisibility(View.GONE);
				layoutHttps.setVisibility(View.GONE);
				layoutFleetServer.setVisibility(View.GONE);
				break;
			default:
				break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.w(TAG, "--onResume--");
		this.configFTP = null;
		this.configFTP = new ConfigFtp();
		initData();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (position == parent.getCount() - 1) {
			// 配置
			Intent intent = new Intent(getParent(), SysFtp.class);
			startActivity(intent);
		} else {
			// 保存Ftp服务器名
			mServer.setFtpName(configFTP.getNameFirstEmpty(position, getParent()));
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

}
