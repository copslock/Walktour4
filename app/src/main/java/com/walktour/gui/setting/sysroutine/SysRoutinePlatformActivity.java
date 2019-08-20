package com.walktour.gui.setting.sysroutine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.walktour.Utils.ServerMessage;
import com.walktour.Utils.WalktourConst;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

/***
 * 平台交互
 * 
 * @author weirong.fan
 *
 */
public class SysRoutinePlatformActivity extends BasicActivity implements OnClickListener {
	/** 测试计划扫描 */
	private CheckBox cb1;
	/** 平台监控 */
	private CheckBox cb2;
	/** 参数存储 */
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		findView();
	}

	/**
	 * 初始化视图
	 */
	private void findView() {
		setContentView(R.layout.sys_routine_setting_platform);
		cb1 = (CheckBox) findViewById(R.id.platform_testtsk_control_ck);
		cb2 = (CheckBox) findViewById(R.id.platform_control_ck);
		cb1.setChecked(preferences.getBoolean(WalktourConst.SYS_SETTING_platform_test, false));
		cb2.setChecked(preferences.getBoolean(WalktourConst.SYS_SETTING_platform_control, false));
		cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.edit().putBoolean(WalktourConst.SYS_SETTING_platform_test, isChecked).commit();
				//开关定时器,为true时系统启动时同时启动
				if(isChecked){
					Intent intent = new Intent();
					intent.setAction(ServerMessage.ACTION_PLATFORM_CONTROL_TESTPLAN_START);
					sendOrderedBroadcast(intent,null);
				}else{
					Intent intent = new Intent();
					intent.setAction(ServerMessage.ACTION_PLATFORM_CONTROL_TESTPLAN_STOP);
					sendOrderedBroadcast(intent,null);
				}
			}
		});

		cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.edit().putBoolean(WalktourConst.SYS_SETTING_platform_control, isChecked).commit();
			}
		});
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
