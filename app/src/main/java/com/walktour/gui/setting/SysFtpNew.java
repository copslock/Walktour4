package com.walktour.gui.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.control.bean.Verify;
import com.walktour.control.config.ConfigFtp;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.task.CustomAutoCompleteTextView;
import com.walktour.gui.task.SaveHistoryShare;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.ftp.TaskFtpModel;
import com.walktour.model.FtpServerModel;

import java.util.ArrayList;
import java.util.List;

public class SysFtpNew extends BasicActivity {
	private ConfigFtp config;
	private String ftpName;
	private Boolean isEdit = false;
	private int position;
	private TaskListDispose taskd; // 获取系统测试任务列表信息
	private SaveHistoryShare historyShare;

	/**
	 * 历史记录集合
	 */
	private ArrayList<String> mOriginalValues = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		taskd = TaskListDispose.getInstance();
		this.config = new ConfigFtp();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			this.ftpName = bundle.getString("server");// 读出数据
			this.isEdit = bundle.getBoolean("isEdit");
			this.position = bundle.getInt("position");
		}
		historyShare = new SaveHistoryShare(getApplicationContext());
		historyShare.getHistoryDataFromSP(this.getPackageName(), "SysFtpIp", mOriginalValues);
		this.findView();
	}

	private void findView() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.sys_ftp_new, null);
		((TextView) textEntryView.findViewById(R.id.title_txt))
				.setText(isEdit ? R.string.sys_ftp_edit : R.string.sys_ftp_new); // 设置标题
		textEntryView.findViewById(R.id.pointer).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SysFtpNew.this.finish();
			}
		});

		Button btn_ok = (Button) textEntryView.findViewById(R.id.btn_ok);
		Button btn_cencle = (Button) textEntryView.findViewById(R.id.btn_cencle);

		final EditText editText_name = (EditText) textEntryView.findViewById(R.id.edit_name);
		final EditText editText_port = (EditText) textEntryView.findViewById(R.id.edit_port);
		final CustomAutoCompleteTextView editText_ip = (CustomAutoCompleteTextView) textEntryView
				.findViewById(R.id.edit_ip);
		editText_ip.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2 == arg0.getCount() - 1) {
					historyShare.clearData();
					editText_ip.setText("");
				}
			}
		});
		historyShare.initAutoComplete(editText_ip);
		final EditText editText_user = (EditText) textEntryView.findViewById(R.id.edit_user);
		final EditText editText_pass = (EditText) textEntryView.findViewById(R.id.edit_pass);
		final CheckBox checkBox = (CheckBox) textEntryView.findViewById(R.id.checkbox);
		final Spinner passiveSpinner = (Spinner) textEntryView.findViewById(R.id.spinner_passive);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.connect_mode));
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		passiveSpinner.setAdapter(adapter);

		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					editText_user.setText("");
					editText_pass.setText("");
					editText_user.setEnabled(false);
					editText_pass.setEnabled(false);
				} else {
					editText_user.setEnabled(true);
					editText_pass.setEnabled(true);
				}
			}
		});

		if (isEdit) {
			FtpServerModel serverModel = config.getFtpServerModel(this.ftpName);
			editText_name.setText(serverModel.getName());
			editText_ip.setText(serverModel.getIp());
			editText_port.setText(serverModel.getPort());
			editText_user.setText(serverModel.getLoginUser());
			editText_pass.setText(serverModel.getLoginPassword());
			checkBox.setChecked(serverModel.isAnonymous());
			passiveSpinner.setSelection(serverModel.getConnect_mode() == FtpServerModel.CONNECT_MODE_PASSIVE ? 0 : 1);
		}

		// 添加"确定"Button事件
		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				boolean isDataCorrect = true;

				// 验证必填项
				if (editText_name.getText().toString().trim().length() == 0) {
					ToastUtil.showToastShort(getApplicationContext(), R.string.sys_ftp_alert_nullName);
					editText_name.setError(getString(R.string.sys_ftp_alert_nullName));
					isDataCorrect = false;
				} else if (editText_ip.getText().toString().trim().length() == 0) {
					ToastUtil.showToastShort(getApplicationContext(), R.string.sys_ftp_alert_nullIP);
					isDataCorrect = false;
				} else if (editText_port.getText().toString().trim().length() == 0) {
					ToastUtil.showToastShort(getApplicationContext(), R.string.sys_ftp_alert_nullPort);
					editText_port.setError(getString(R.string.sys_ftp_alert_nullPort));
					isDataCorrect = false;
				} else if (editText_user.getText().toString().trim().equals("") && !checkBox.isChecked()) {
					ToastUtil.showToastShort(SysFtpNew.this, R.string.task_alert_nullAccount);
					editText_user.setError(getString(R.string.task_alert_nullAccount));
					isDataCorrect = false;
				} else if (editText_pass.getText().toString().trim().equals("") && !checkBox.isChecked()) {
					ToastUtil.showToastShort(SysFtpNew.this, R.string.task_alert_nullPassword);
					editText_pass.setError(getString(R.string.task_alert_nullPassword));
					isDataCorrect = false;
				}

				// 验证数据正确性
				else {
					if (!Verify.isIpOrUrl(editText_ip.getText().toString().trim())) {
						ToastUtil.showToastShort(getApplicationContext(), R.string.sys_ftp_alert_nullIP);
						isDataCorrect = false;
					}
					if (!Verify.isPort(editText_port.getText().toString().trim())) {
						ToastUtil.showToastShort(getApplicationContext(), R.string.sys_ftp_alert_nullPort);
						isDataCorrect = false;
					}
				} // end else

				if (isDataCorrect && !isEdit) {
					if (config.contains(editText_name.getText().toString().trim())) {
						ToastUtil.showToastShort(getApplicationContext(), "\"" + editText_name.getText().toString().trim() + "\""
								+ getString(R.string.sys_ftp_alert_existName));
						return;
					}
					FtpServerModel model = new FtpServerModel();
					model.setName(editText_name.getText().toString().trim());
					model.setIp(editText_ip.getText().toString().trim());
					model.setPort(editText_port.getText().toString());
					model.setLoginUser(editText_user.getText().toString().length() == 0 ? " "
							: editText_user.getText().toString().trim());
					model.setLoginPassword(editText_pass.getText().toString().length() == 0 ? " "
							: editText_pass.getText().toString());
					model.setAnonymous(checkBox.isChecked());
					model.setConnect_mode(passiveSpinner.getSelectedItemPosition() == 0
							? FtpServerModel.CONNECT_MODE_PASSIVE : FtpServerModel.CONNECT_MODE_PORT);
					config.addFtp(model);
					historyShare.saveHistory(editText_ip);
					ToastUtil.showToastShort(getApplicationContext(), R.string.sys_ftp_newSuccess);
					com.walktour.gui.setting.SysFtpNew.this.finish();
				} else if (isDataCorrect && isEdit) {
					if (config.contains(editText_name.getText().toString().trim())
							&& !editText_name.getText().toString().trim().equals(ftpName)) {
						ToastUtil.showToastShort(getApplicationContext(), "\"" + editText_name.getText().toString().trim() + "\""
								+ getString(R.string.sys_ftp_alert_existName));
						return;
					}
					FtpServerModel ftpModel = new FtpServerModel();
					if (!isUseByFtpServer(ftpName)) {
						ftpModel.setName(editText_name.getText().toString().trim());
					} else {
						if (!ftpName.equals(editText_name.getText().toString().trim())) {
							ToastUtil.showToastShort(SysFtpNew.this, ftpName + ":" + getString(R.string.sys_setting_modify_isuse));
							return;
						}
						ftpModel.setName(ftpName);
					}

					ftpModel.setPort(editText_port.getText().toString());
					ftpModel.setIp(editText_ip.getText().toString().trim());
					ftpModel.setLoginUser(editText_user.getText().toString().length() == 0 ? " "
							: editText_user.getText().toString().trim());
					ftpModel.setLoginPassword(editText_pass.getText().toString().length() == 0 ? " "
							: editText_pass.getText().toString());
					ftpModel.setAnonymous(checkBox.isChecked());
					ftpModel.setConnect_mode(passiveSpinner.getSelectedItemPosition() == 0
							? FtpServerModel.CONNECT_MODE_PASSIVE : FtpServerModel.CONNECT_MODE_PORT);
					historyShare.saveHistory(editText_ip);
					config.editFtp(ftpModel, position);
					ToastUtil.showToastShort(getApplicationContext(), R.string.task_alert_updateSucess);
					SysFtpNew.this.finish();
				}
			}
		});

		// 添加"取消"Button事件
		btn_cencle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				com.walktour.gui.setting.SysFtpNew.this.finish();
			}
		});

		setContentView(textEntryView);

	}// end method findView

	/**
	 * 根据传进来的FTP服务器名称判断，当前服务器是否被使用
	 * 
	 * @param ftpServerName
	 * @return
	 */
	private boolean isUseByFtpServer(String ftpServerName) {
		boolean isUser = false;
		List<TaskModel> list = taskd.getTaskListArray();
		for (Object task : list) {
			if (((TaskModel) task).getTaskType().equals(WalkStruct.TaskType.FTPDownload.name())
					|| ((TaskModel) task).getTaskType().equals(WalkStruct.TaskType.FTPUpload.name())) {
				if (((TaskFtpModel) task).getFtpServerName().equals(ftpServerName)) {
					isUser = true;
					break;
				}
			}
		}
		return isUser;
	}

	@Override // 添加菜单
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mInflater = getMenuInflater();
		mInflater.inflate(R.menu.ftp, menu);
		return false;
	}

	@Override // 菜单点击事件
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == R.id.menu_newftp) {

		} else {

		}
		return false;
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, R.anim.slide_in_down);
	}

}