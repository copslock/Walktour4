package com.walktour.gui.mutilytester;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.control.bean.Verify;
import com.walktour.control.config.ConfigAPN;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicDialog.Builder;
import com.walktour.gui.R;
import com.walktour.gui.setting.SysAlarm;
import com.walktour.gui.setting.sysroutine.SysRoutineAPNActivity;
import com.walktour.gui.setting.sysroutine.SysRoutineAdvencedActivity;
import com.walktour.gui.setting.sysroutine.SysRoutineDataUploadActivity;
import com.walktour.gui.setting.sysroutine.SysRoutineLockMainActivity;
import com.walktour.gui.setting.sysroutine.dialog.SysRoutineDataFormatDialog;
import com.walktour.gui.setting.sysroutine.dialog.SysRoutineLockDialog;

/***
 * 多网测试设置界面
 * 
 * @author weirong.fan
 *
 */
public class MultilyTestSetting extends BasicActivity implements OnClickListener {

	/** 常规设置配置文件 */
	private ConfigRoutine configRoutine;
	private final int CHANGE_APN = 1;
	private final int CHANGE_ALARM = 2;
	private final int CHANGE_LOCKNET = 3;
	private final int CHANGE_CHANNEL = 4;
	private final int CHANGE_VREC = 5;
	private final int CHANGE_VPLAY = 6;
	private final int CHANGE_VFAZHI = 7;

	private boolean isApnPointChange = false; // 是否改变数据测试时APN接入点,如果是在onResume的时候刷新页面

	private RelativeLayout setting_apn;
	private RelativeLayout setting_channel;
	private RelativeLayout setting_vRec;
	private RelativeLayout setting_vPlay;
	private RelativeLayout setting_vfazhi;
	private RelativeLayout setting_alarm;

	private TextView text_apnname;
	/** 通道号 **/
	private TextView text_mosbox_channel;
	/** Mos录音音量 */
	private TextView text_vRec;
	/** Mos放音音量 */
	private TextView text_vPlay;
	/** Mos阈值 */
	private TextView text_vFazhi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terminal_setting);
		configRoutine = ConfigRoutine.getInstance();
		setting_apn = (RelativeLayout) findViewById(R.id.layout_setting_apn);
		setting_channel = (RelativeLayout) findViewById(R.id.layout_setting_mosbox_channel);
		setting_vRec = (RelativeLayout) findViewById(R.id.layout_setting_vRec_value);
		setting_vPlay = (RelativeLayout) findViewById(R.id.layout_setting_vPlay_value);
		setting_vfazhi = (RelativeLayout) findViewById(R.id.layout_setting_vFazhi_value);
		setting_alarm = (RelativeLayout) findViewById(R.id.layout_setting_alarm);
 
		if (!Deviceinfo.getInstance().getApnList()) {
			setting_apn.setVisibility(View.GONE);
		}

		text_apnname = (TextView) findViewById(R.id.text_apn_netname); 
		text_mosbox_channel = (TextView) findViewById(R.id.text_setting_mosbox_channel);

		text_vRec = (TextView) findViewById(R.id.txt_mutilytester_mos_vRec);
		text_vPlay = (TextView) findViewById(R.id.txt_mutilytester_mos_vPlay);
		text_vFazhi = (TextView) findViewById(R.id.txt_mutilytester_mos_vfazhi);
		setting_vRec.setVisibility(View.VISIBLE);
		setting_vPlay.setVisibility(View.VISIBLE);
		setting_vfazhi.setVisibility(View.VISIBLE);
		setting_apn.setOnClickListener(this);
		setting_channel.setOnClickListener(this);
		setting_alarm.setOnClickListener(this); 
		setting_vRec.setOnClickListener(this);
		setting_vPlay.setOnClickListener(this);
		setting_vfazhi.setOnClickListener(this);

		((RelativeLayout) findViewById(R.id.relative_dataupload)).setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.relative_datasave)).setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.relative_functions)).setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.relative_advances)).setOnClickListener(this); 
		findView();
	}
 

	@Override
	protected void onResume() { 
		super.onResume();
		if (isApnPointChange) {
			isApnPointChange = false;
			findView();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy(); 
	}

	private void findView() {
		findViewById(R.id.pointer).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		text_apnname.setText(ConfigAPN.getInstance().getDataAPN());
		text_mosbox_channel.setText(ConfigRoutine.getInstance().getMosBoxChannel(getApplicationContext()) > 0
				? String.valueOf(ConfigRoutine.getInstance().getMosBoxChannel(getApplicationContext()))
				: getString(R.string.none));
		text_vRec.setText(String.valueOf(ConfigRoutine.getInstance().getMosBoxVRec(getApplicationContext())));
		text_vPlay.setText(String.valueOf(ConfigRoutine.getInstance().getMosBoxVPlay(getApplicationContext())));
		text_vFazhi.setText(String.valueOf(ConfigRoutine.getInstance().getCallMosBoxLowMos(getApplicationContext())));

	}

	@Override
	@SuppressWarnings("deprecation")
	public void onClick(View v) {
		Bundle bundle = new Bundle();
		switch (v.getId()) {
		case R.id.layout_setting_apn:
			mHandler.obtainMessage(CHANGE_APN).sendToTarget();
			break;
		case R.id.layout_setting_alarm:
			mHandler.obtainMessage(CHANGE_ALARM).sendToTarget();
			break;
		case R.id.layout_setting_mosbox_channel:
			mHandler.obtainMessage(CHANGE_CHANNEL).sendToTarget();
			break;
		case R.id.layout_setting_vRec_value:
			mHandler.obtainMessage(CHANGE_VREC).sendToTarget();
			break;
		case R.id.layout_setting_vPlay_value:
			mHandler.obtainMessage(CHANGE_VPLAY).sendToTarget();
			break;
		case R.id.layout_setting_vFazhi_value:
			mHandler.obtainMessage(CHANGE_VFAZHI).sendToTarget();
			break;

		case R.id.relative_dataupload:
			bundle.putBoolean("isFromMultitest", true);
			jumpActivity(SysRoutineDataUploadActivity.class, bundle);
			break;

		case R.id.relative_datasave:
			showDialog(R.id.relative_datasave);
			break;

		case R.id.relative_functions:
			bundle.putBoolean("isFromMultitest", true);
			jumpActivity(SysRoutineLockMainActivity.class, bundle);
			break;
		case R.id.relative_advances:
			bundle.putBoolean("isFromMultitest", true);
			jumpActivity(SysRoutineAdvencedActivity.class, bundle);
			break;
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {
		BasicDialog.Builder builder = new BasicDialog.Builder(this);
		switch (id) {
		case R.id.relative_datasave:
			new SysRoutineDataFormatDialog(this, builder, configRoutine);
			break;
		}
		return builder.create();
	}
 

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case CHANGE_APN:
					isApnPointChange = true;
					Intent intent = new Intent(getApplicationContext(), SysRoutineAPNActivity.class);
					startActivity(intent);
					break;
				case CHANGE_ALARM:
					Intent alarm = new Intent(getApplicationContext(), SysAlarm.class);
					startActivity(alarm);
					break;
				case CHANGE_LOCKNET:
					BasicDialog bd = buildLockNetDialog();
					bd.show();
					break;
				case CHANGE_CHANNEL:
					BasicDialog ch = buildBosChannelDialog();
					ch.show();
					break;
				case CHANGE_VREC:
					buildMosBoxVRecDialog().show();
					break;
				case CHANGE_VPLAY:
					buildMosBoxVPlayDialog().show();
					break;
				case CHANGE_VFAZHI:
					buildMosBoxVFazhiDialog().show();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 创建锁定网络对话框
	 * 
	 * @return
	 */
	private BasicDialog buildLockNetDialog() {
		return new BasicDialog.Builder(this).setTitle(R.string.str_lock_script)
				.setSingleChoiceItems(R.array.public_switch, ConfigRoutine.getInstance().canRunScript() ? 0 : 1,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ConfigRoutine.getInstance().setCanRunScript(which == 0);
								findView();
								dialog.dismiss();
							}
						})
				.setNeutralButton(R.string.str_lock, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						new SysRoutineLockDialog(getParent()).show();
					}
				}).create();
	}

	/**
	 * 创建锁定网络对话框
	 * 
	 * @return
	 */
	private BasicDialog buildBosChannelDialog() {
		return new BasicDialog.Builder(this).setTitle(R.string.mutilytester_mos_box_channel)
				.setSingleChoiceItems(R.array.mutily_mosbox_channel,
						ConfigRoutine.getInstance().getMosBoxChannel(getApplicationContext()),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ConfigRoutine.getInstance().setMosBoxChannel(getApplicationContext(), which);
								findView();
								dialog.dismiss();
							}
						})
				.create();
	}

	private BasicDialog buildMosBoxVRecDialog() {
		Builder builder = new Builder(this);

		LayoutInflater factory = LayoutInflater.from(this);
		final View view = factory.inflate(R.layout.alert_dialog_edittext, null);
		EditText alert_EditText = (EditText) view.findViewById(R.id.alert_textEditText);
		final ConfigRoutine configRoutine = ConfigRoutine.getInstance();
		alert_EditText.setText(String.valueOf(configRoutine.getMosBoxVRec(getApplicationContext())));
		alert_EditText.setSelectAllOnFocus(true);
		alert_EditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.mutilytester_mos_vRec_value).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						EditText alert_EditText = (EditText) view.findViewById(R.id.alert_textEditText);
						String tag = alert_EditText.getText().toString().trim();
						if (Verify.checknum(tag)) {
							configRoutine.setMosBoxVRec(getApplicationContext(), Integer.parseInt(tag));
						} else {
							alert_EditText.setText(configRoutine.getDeviceTag());
							Toast.makeText(getParent(), getString(R.string.monitor_inputPosition), Toast.LENGTH_LONG)
									.show();
						}

						findView();
					}
				}).setNeutralButton(R.string.str_default_reset, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						configRoutine.setMosBoxVRec(getApplicationContext(), Deviceinfo.getInstance().getMosBoxVRec());
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.str_cancle);

		return builder.create();
	}

	private BasicDialog buildMosBoxVPlayDialog() {
		Builder builder = new Builder(this);

		LayoutInflater factory = LayoutInflater.from(this);
		final View view = factory.inflate(R.layout.alert_dialog_edittext, null);
		EditText alert_EditText = (EditText) view.findViewById(R.id.alert_textEditText);
		final ConfigRoutine configRoutine = ConfigRoutine.getInstance();
		alert_EditText.setText(String.valueOf(configRoutine.getMosBoxVPlay(getApplicationContext())));
		alert_EditText.setSelectAllOnFocus(true);
		alert_EditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.mutilytester_mos_vPlay_value).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						EditText alert_EditText = (EditText) view.findViewById(R.id.alert_textEditText);
						String tag = alert_EditText.getText().toString().trim();
						if (Verify.checknum(tag)) {
							configRoutine.setMosBoxVPlay(getApplicationContext(), Integer.parseInt(tag));
						} else {
							alert_EditText.setText(configRoutine.getDeviceTag());
							Toast.makeText(getParent(), getString(R.string.monitor_inputPosition), Toast.LENGTH_LONG)
									.show();
						}
						findView();
					}
				}).setNeutralButton(R.string.str_default_reset, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						configRoutine.setMosBoxVPlay(getApplicationContext(),
								Deviceinfo.getInstance().getMosBoxVPlay());
						findView();
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.str_cancle);

		return builder.create();
	}

	private BasicDialog buildMosBoxVFazhiDialog() {
		Builder builder = new Builder(this);

		LayoutInflater factory = LayoutInflater.from(this);
		final View view = factory.inflate(R.layout.alert_dialog_edittext, null);
		EditText alert_EditText = (EditText) view.findViewById(R.id.alert_textEditText);
		final ConfigRoutine configRoutine = ConfigRoutine.getInstance();
		alert_EditText.setText(String.valueOf(configRoutine.getCallMosBoxLowMos(getApplicationContext())));
		alert_EditText.setSelectAllOnFocus(true);
		alert_EditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.mutilytester_mos_vFazhi_value).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						EditText alert_EditText = (EditText) view.findViewById(R.id.alert_textEditText);
						String tag = alert_EditText.getText().toString().trim();
						if (Verify.checknum(tag)) {
							configRoutine.setCallMosBoxLowMos(getApplicationContext(), Float.parseFloat(tag));
						} else {
							alert_EditText.setText(configRoutine.getDeviceTag());
							Toast.makeText(getParent(), getString(R.string.monitor_inputPosition), Toast.LENGTH_LONG)
									.show();
						}
						findView();
					}
				}).setNeutralButton(R.string.str_default_reset, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						configRoutine.setCallMosBoxLowMos(getApplicationContext(),
								Deviceinfo.getInstance().getCallMosBoxLowMos());
						findView();
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.str_cancle);

		return builder.create();
	}
}
