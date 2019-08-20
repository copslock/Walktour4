package com.walktour.gui.setting.sysroutine;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.walktour.Utils.HttpServer;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.applet.MyKeyListener;
import com.walktour.service.OtsHttpUploadService;

import java.io.File;

/**
 * 常规设置的OTS设置
 * 
 * @author jianchao.wang
 * 
 */

public class SysRoutineOTSActivity extends BasicActivity implements OnClickListener {
	/** 参数存储 */
	private SharedPreferences preferences;
	/** OTS Test */
	private CheckBox otsBox;
	/** 分割启用 */
	private CheckBox divisionEnableBox;
	/** 采用间隔 */
	private TextView sampleIntervalTextView;
	/** 分割大小KB */
	private TextView divisionSizeTextView;
	/** 数据存放目录 */
	private TextView datastroageTextView;
	/** 端口信息 */
	private TextView portTextView;
	/** 上报间隔 */
	private TextView reportIntervalTextView;
	/** URL设备信息 */
	private TextView urlDeviceTextView;
	/** URL参数信息 */
	private TextView urlParameterTextView;
	/** 开始测试 */
	private CheckBox testActiveCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sys_routine_setting_ots);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		findView();
	}

	/**
	 * 初始化视图
	 */
	private void findView() {
		otsBox = (CheckBox) findViewById(R.id.ots_test_ck);
		testActiveCheck = (CheckBox) findViewById(R.id.test_active_ck);
		divisionEnableBox = (CheckBox) findViewById(R.id.division_enable_ck);
		sampleIntervalTextView = initTextView(R.id.sample_interval_text);
		divisionSizeTextView = initTextView(R.id.division_size_text);
		datastroageTextView = initTextView(R.id.dataStorage_text);
		portTextView = initTextView(R.id.port_text);
		reportIntervalTextView = initTextView(R.id.report_interval_text);
		urlDeviceTextView = initTextView(R.id.url_device_text);
		urlParameterTextView = initTextView(R.id.url_parameter_text);

		findViewById(R.id.sample_interval_layout).setOnClickListener(this);
		findViewById(R.id.division_size_layout).setOnClickListener(this);
		findViewById(R.id.dataStorage_layout).setOnClickListener(this);
		findViewById(R.id.port_layout).setOnClickListener(this);
		findViewById(R.id.report_interval_layout).setOnClickListener(this);
		findViewById(R.id.url_device_layout).setOnClickListener(this);
		findViewById(R.id.url_parameter_layout).setOnClickListener(this);

		otsBox.setChecked(preferences.getBoolean(WalktourConst.SYS_SETTING_OTS_TEST, false));
		divisionEnableBox.setChecked(preferences.getBoolean(WalktourConst.SYS_SETTING_OTS_DIVISION_ENABLE, false));

		testActiveCheck.setChecked(preferences.getBoolean(WalktourConst.SYS_SETTING_OTS_TEST_ACTIVE, false));

		divisionSizeTextView.setText(String.valueOf(preferences.getInt(WalktourConst.SYS_SETTING_OTS_DIVISION_SIZE, 512)));
		datastroageTextView.setText(preferences.getString(WalktourConst.SYS_SETTING_OTS_DATASTORAGE,
				AppFilePathUtil.getInstance().getSDCardBaseDirectory()));
		portTextView.setText(String.valueOf(preferences.getInt(WalktourConst.SYS_SETTING_OTS_PORT, HttpServer.HttpServerPort)));
		reportIntervalTextView.setText(String.valueOf(preferences.getInt(WalktourConst.SYS_SETTING_OTS_REPORT_INTERVAL, 3000)));
		urlDeviceTextView.setText(preferences.getString(WalktourConst.SYS_SETTING_OTS_URL_DEVICE, "http://192.168.1.1:80/device"));
		urlParameterTextView.setText(preferences.getString(WalktourConst.SYS_SETTING_OTS_URL_PARAMETER,
				"http://192.168.1.1:80/air_interface/params"));
		sampleIntervalTextView.setText(String.valueOf(preferences.getInt(WalktourConst.SYS_SETTING_OTS_SAMPLE_INTERVAL, 500)));

		otsBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.edit().putBoolean(WalktourConst.SYS_SETTING_OTS_TEST, isChecked).apply();
				// 此处需要相应开关HTTP服务
				if (isChecked) {
					HttpServer.getInstance(SysRoutineOTSActivity.this).startHttpServer(
							PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(
									WalktourConst.SYS_SETTING_OTS_PORT, HttpServer.HttpServerPort));
//					startService(new Intent(SysRoutineOTSActivity.this, OtsSocketUploadService.class));
				} else {
					HttpServer.getInstance(SysRoutineOTSActivity.this).stopHttpServer();
//					stopService(new Intent(SysRoutineOTSActivity.this, OtsSocketUploadService.class));
				}
			}
		});

		divisionEnableBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.edit().putBoolean(WalktourConst.SYS_SETTING_OTS_DIVISION_ENABLE, isChecked).apply();
			}
		});

		testActiveCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.edit().putBoolean(WalktourConst.SYS_SETTING_OTS_TEST_ACTIVE, isChecked).apply();

				if (isChecked) {
					startService(new Intent(SysRoutineOTSActivity.this, OtsHttpUploadService.class));
				} else {
					stopService(new Intent(SysRoutineOTSActivity.this, OtsHttpUploadService.class));
				}
			}
		});
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointer:
			SysRoutineOTSActivity.this.finish();
			break;
		case R.id.sample_interval_layout:
			showDialog(R.id.sample_interval_layout);
			break;
		case R.id.division_size_layout:
			showDialog(R.id.division_size_layout);
			break;
		case R.id.dataStorage_layout:
			showDialog(R.id.dataStorage_layout);
			break;
		case R.id.port_layout:
			showDialog(R.id.port_layout);
			break;
		case R.id.report_interval_layout:
			showDialog(R.id.report_interval_layout);
			break;
		case R.id.url_device_layout:
			showDialog(R.id.url_device_layout);
			break;
		case R.id.url_parameter_layout:
			showDialog(R.id.url_parameter_layout);
			break;
		default:
			break;
		}

	}

	@SuppressLint("InflateParams")
	@Override
	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {
		BasicDialog.Builder builder = new BasicDialog.Builder(this.getParent());
		LayoutInflater factory = LayoutInflater.from(this.getParent());
		final View view = factory.inflate(R.layout.alert_dialog_edittext, null);
		final EditText editText = (EditText) view.findViewById(R.id.alert_textEditText);
		switch (id) {
		case R.id.sample_interval_layout:
			editText.setSelectAllOnFocus(true);
			editText.setText(String.valueOf(preferences.getInt(WalktourConst.SYS_SETTING_OTS_SAMPLE_INTERVAL, 500)));
			editText.setKeyListener(new MyKeyListener().getNumberKeyListener());
			// editText.setInputType(android.text.InputType.TYPE_NUMBER_VARIATION_NORMAL);
			builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.sys_setting_sample_interval).setView(view)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (!StringUtil.isNullOrEmpty(editText.getText().toString())) {
								preferences
										.edit()
										.putInt(WalktourConst.SYS_SETTING_OTS_SAMPLE_INTERVAL,
												Integer.valueOf(editText.getText().toString())).apply();
								sampleIntervalTextView.setText(editText.getText().toString());
							}
						}
					}).setNegativeButton(R.string.str_cancle);
			break;
		case R.id.division_size_layout:
			editText.setText(String.valueOf(preferences.getInt(WalktourConst.SYS_SETTING_OTS_DIVISION_SIZE, 512)));
			editText.setKeyListener(new MyKeyListener().getNumberKeyListener());
			// editText.setInputType(android.text.InputType.TYPE_NUMBER_VARIATION_NORMAL);
			editText.setSelectAllOnFocus(true);
			builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.sys_setting_division_size).setView(view)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (!StringUtil.isNullOrEmpty(editText.getText().toString())) {
								preferences
										.edit()
										.putInt(WalktourConst.SYS_SETTING_OTS_DIVISION_SIZE, Integer.valueOf(editText.getText().toString()))
										.apply();
								divisionSizeTextView.setText(editText.getText().toString());
							}
						}
					}).setNegativeButton(R.string.str_cancle);
			break;
		case R.id.dataStorage_layout:
			editText.setSelectAllOnFocus(true);
			editText.setText(preferences.getString(WalktourConst.SYS_SETTING_OTS_DATASTORAGE, AppFilePathUtil.getInstance().getSDCardBaseDirectory()));
			// editText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_NORMAL);
			builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.sys_setting_data_storage).setView(view)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (!StringUtil.isNullOrEmpty(editText.getText().toString())) {
								preferences
										.edit()
										.putString(
												WalktourConst.SYS_SETTING_OTS_DATASTORAGE,
												editText.getText().toString().endsWith("/") ? editText.getText().toString() : editText
														.getText().toString() + File.separator).apply();
								datastroageTextView.setText(editText.getText().toString());

								File fileDir = new File(editText.getText().toString());
								if (!fileDir.exists()) {
									fileDir.mkdirs();
								}
							}
						}
					}).setNegativeButton(R.string.str_cancle);
			break;
		case R.id.port_layout:
			editText.setSelectAllOnFocus(true);
			editText.setText(String.valueOf(preferences.getInt(WalktourConst.SYS_SETTING_OTS_PORT, HttpServer.HttpServerPort)));
			// editText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_NORMAL);
			builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.fleet_set_port).setView(view)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (!StringUtil.isNullOrEmpty(editText.getText().toString())) {
								preferences.edit()
										.putInt(WalktourConst.SYS_SETTING_OTS_PORT, Integer.valueOf(editText.getText().toString()))
										.apply();
								portTextView.setText(editText.getText().toString());
							}
						}
					}).setNegativeButton(R.string.str_cancle);
			break;
		case R.id.report_interval_layout:
			editText.setSelectAllOnFocus(true);
			editText.setText(String.valueOf(preferences.getInt(WalktourConst.SYS_SETTING_OTS_REPORT_INTERVAL, 3000)));
			// editText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_NORMAL);
			builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.sys_ots_report_interval_str).setView(view)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (!StringUtil.isNullOrEmpty(editText.getText().toString())) {
								preferences
										.edit()
										.putInt(WalktourConst.SYS_SETTING_OTS_REPORT_INTERVAL,
												Integer.valueOf(editText.getText().toString())).apply();
								reportIntervalTextView.setText(editText.getText().toString());
							}
						}
					}).setNegativeButton(R.string.str_cancle);
			break;
		case R.id.url_device_layout:
			editText.setSelectAllOnFocus(true);
			editText.setText(preferences.getString(WalktourConst.SYS_SETTING_OTS_URL_DEVICE, "http://192.168.1.1:80/device"));
			// editText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_NORMAL);
			builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.sys_ots_url_device_str).setView(view)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (!StringUtil.isNullOrEmpty(editText.getText().toString())) {
								preferences.edit().putString(WalktourConst.SYS_SETTING_OTS_URL_DEVICE, editText.getText().toString())
										.apply();
								urlDeviceTextView.setText(editText.getText().toString());
							}
						}
					}).setNegativeButton(R.string.str_cancle);
			break;

		case R.id.url_parameter_layout:
			editText.setSelectAllOnFocus(true);
			editText.setText(preferences.getString(WalktourConst.SYS_SETTING_OTS_URL_PARAMETER,
					"http://192.168.1.1:80/air_interface/params"));
			// editText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_NORMAL);
			builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.sys_ots_url_para_str).setView(view)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (!StringUtil.isNullOrEmpty(editText.getText().toString())) {
								preferences.edit().putString(WalktourConst.SYS_SETTING_OTS_URL_PARAMETER, editText.getText().toString())
										.apply();
								urlParameterTextView.setText(editText.getText().toString());
							}
						}
					}).setNegativeButton(R.string.str_cancle);
			break;

		default:
			break;
		}
		return builder.create();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(SysRoutineActivity.SHOW_MAIN_TAB);
			this.sendBroadcast(intent);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

}
