package com.walktour.gui.setting.sysroutine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TabHost;

import com.dingli.droidwall.DroidWallActivity;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.ScroollTabActivity;
import com.walktour.gui.R;

/**
 * 常规设置主界面
 * 
 * @author jianchao.wang
 * 
 */
public class SysRoutineActivity extends ScroollTabActivity {
	/** 显示数据相关设置界面 */
	public final static String SHOW_DATA_TAB = "routine_show_data_tab";
	/** 显示主界面设置界面 */
	public final static String SHOW_MAIN_TAB = "routine_show_main_tab";
	/** 显示自定义窗口设置界面 */
	public final static String SHOW_CUSTOM_WINDOW_TAB = "routine_show_custom_window_tab";
	/** 显示OTS设置界面 */
	public final static String SHOW_OTS_TAB = "routine_show_ots_tab";
	/** 显示数据上传设置界面 */
	public final static String SHOW_DATA_UPLOAD_TAB = "routine_show_data_upload_tab";
	/** 显示数据过滤设置界面 */
	public final static String SHOW_DATA_FILTER_TAB = "routine_show_data_filter_tab";
	/** 显示高级选项设置界面 */
	public final static String SHOW_ADVENCED_TAB = "routine_show_advenced_tab";
	/** 显示调试模式设置界面 */
	public final static String SHOW_DEBUG_MODEL_TAB = "routine_show_debug_model_tab";
	/** 显示第三方应用联网权限设置界面 */
	public final static String SHOW_NETWORK_CONTROL_TAB = "routine_show_network_control_tab";
	/** 显示平台交互设置界面 */
	public final static String SHOW_PLATFORM_INTERACTIVE_TAB = "routine_show_platform_interactive_tab";
	/** 显示NB模块界面 */
	public final static String SHOW_NBMODULE_TAB = "routine_show_nbmodule_tab";
	/**强制网络主界面*/
	public final static String SHOW_LOCK_MAIN_TAB = "routine_show_lock_main_tab";
	/** 输出标签 */
	private final String TAG = "SysRoutineActivity";
	/** tab对象 */
	private TabHost tabHost;
	/** 监听类 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.w(TAG, "--BroadcastReceiver--" + intent.getAction());
			if (intent.getAction().equals(SysRoutineActivity.SHOW_DATA_TAB)) {
				tabHost.setCurrentTabByTag("data");
			} else if (intent.getAction().equals(SysRoutineActivity.SHOW_CUSTOM_WINDOW_TAB)) {
				tabHost.setCurrentTabByTag("customWindow");
			} else if (intent.getAction().equals(SysRoutineActivity.SHOW_OTS_TAB)) {
				tabHost.setCurrentTabByTag("ots");
			} else if (intent.getAction().equals(SysRoutineActivity.SHOW_DATA_UPLOAD_TAB)) {
				tabHost.setCurrentTabByTag("dataUpload");
			} else if (intent.getAction().equals(SysRoutineActivity.SHOW_DATA_FILTER_TAB)) {
				tabHost.setCurrentTabByTag("dataFilter");
			} else if (intent.getAction().equals(SysRoutineActivity.SHOW_ADVENCED_TAB)) {
				tabHost.setCurrentTabByTag("advenced");
			} else if (intent.getAction().equals(SysRoutineActivity.SHOW_DEBUG_MODEL_TAB)) {
				tabHost.setCurrentTabByTag("debugModel");
			} else if (intent.getAction().equals(SysRoutineActivity.SHOW_NETWORK_CONTROL_TAB)) {
				tabHost.setCurrentTabByTag("networkControl");
			} else if (intent.getAction().equals(SysRoutineActivity.SHOW_PLATFORM_INTERACTIVE_TAB)) {
				tabHost.setCurrentTabByTag("platformInteractive");
			}else if (intent.getAction().equals(SysRoutineActivity.SHOW_NBMODULE_TAB)) {
				tabHost.setCurrentTabByTag("nbmodule");
			}else if (intent.getAction().equals(SysRoutineActivity.SHOW_LOCK_MAIN_TAB)) {
				tabHost.setCurrentTabByTag("lockmain");
			}else {
				tabHost.setCurrentTabByTag("main");
			}
		}

	};

	/**
	 * 添加广播监听
	 */
	private void addBroadcastReceiver() {
		LogUtil.w(TAG, "--addBroadcastReceiver--");
		IntentFilter filter = new IntentFilter();
		filter.addAction(SHOW_DATA_TAB);
		filter.addAction(SHOW_MAIN_TAB);
		filter.addAction(SHOW_CUSTOM_WINDOW_TAB);
		filter.addAction(SHOW_OTS_TAB);
		filter.addAction(SHOW_DATA_UPLOAD_TAB);
		filter.addAction(SHOW_DATA_FILTER_TAB);
		filter.addAction(SHOW_ADVENCED_TAB);
		filter.addAction(SHOW_DEBUG_MODEL_TAB);
		filter.addAction(SHOW_NETWORK_CONTROL_TAB);
		filter.addAction(SHOW_PLATFORM_INTERACTIVE_TAB);
		filter.addAction(SHOW_NBMODULE_TAB);
		filter.addAction(SHOW_LOCK_MAIN_TAB);
		SysRoutineActivity.this.registerReceiver(this.mReceiver, filter);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void initView() {
		LogUtil.w(TAG, "--initView--");
		setContentView(R.layout.sys_routine_setting);
		// 获取TabHost对象
		tabHost = (TabHost) findViewById(R.id.tabhost);
		// 如果没有继承TabActivity时，通过该种方法加载启动tabHost
		tabHost.setup(this.getLocalActivityManager());
		tabHost.addTab(tabHost.newTabSpec("main").setIndicator("main").setContent(new Intent(SysRoutineActivity.this, SysRoutineMainActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("data").setIndicator("data").setContent(new Intent(SysRoutineActivity.this, SysRoutineDataActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("customWindow").setIndicator("customWindow").setContent(new Intent(SysRoutineActivity.this, SysRoutineCustomWindowsActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("ots").setIndicator("ots").setContent(new Intent(SysRoutineActivity.this, SysRoutineOTSActivity.class)));
		Intent intent = this.getIntent();
		Intent dataUpload = new Intent(SysRoutineActivity.this, SysRoutineDataUploadActivity.class);
		if (SHOW_DATA_UPLOAD_TAB.equals(intent.getAction())) {
			dataUpload.setAction(SHOW_DATA_UPLOAD_TAB);
		}
		tabHost.addTab(tabHost.newTabSpec("dataUpload").setIndicator("dataUpload").setContent(dataUpload));
		tabHost.addTab(tabHost.newTabSpec("dataFilter").setIndicator("dataFilter").setContent(new Intent(SysRoutineActivity.this, SysRoutineDataFilterActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("advenced").setIndicator("advenced").setContent(new Intent(SysRoutineActivity.this, SysRoutineAdvencedActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("debugModel").setIndicator("debugModel").setContent(new Intent(SysRoutineActivity.this, SysRoutineDebugModelActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("networkControl").setIndicator("networkControl").setContent(new Intent(SysRoutineActivity.this, DroidWallActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("platformInteractive").setIndicator("platformInteractive").setContent(new Intent(SysRoutineActivity.this, SysRoutinePlatformActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("nbmodule").setIndicator("nbmodule").setContent(new Intent(SysRoutineActivity.this, SysRoutineNBModuleActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("lockmain").setIndicator("lockmain").setContent(new Intent(SysRoutineActivity.this, SysRoutineLockMainActivity.class)));
		if (SHOW_DATA_UPLOAD_TAB.equals(intent.getAction()))
			tabHost.setCurrentTabByTag("dataUpload");
		else
			tabHost.setCurrentTabByTag("main");
		this.addBroadcastReceiver();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 56 && resultCode == RESULT_OK) {
			String remoteStr = data.getExtras().getString("path");
			if (remoteStr != null) {
				SysRoutineDataUploadActivity activity = (SysRoutineDataUploadActivity) this.getCurrentActivity();
				activity.setFtpPath(remoteStr);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.w(TAG, "--onDestroy--");
		unregisterReceiver(mReceiver);
	}
}
