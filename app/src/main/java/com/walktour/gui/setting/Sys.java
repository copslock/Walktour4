package com.walktour.gui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.dingli.droidwall.DroidWallUtil;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TabHostUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicTabActivity;
import com.walktour.gui.R;
import com.walktour.gui.setting.sysroutine.SysRoutineActivity;

/**
 * 系统设置 界面
 * 
 * @author weirong.fan
 *
 */
public class Sys extends BasicTabActivity implements OnClickListener {

	public static final String CURRENTTAB = "currenttab";
	/** 常规设置页 */
	public static final int TAB_ROUTINE = 0;
	/** 图表设置页 */
	public static final int TAB_CHART = 1;
	/** 地图设置页 */
	public static final int TAB_MAP = 2;
	/** CQT设置页 */
	public static final int TAB_CQT = 3;
	/** FTP设置页 */
	public static final int TAB_FTP = 4;
	/** 告警设置页 */
	public static final int TAB_ALARM = 5;
	private TabHost tabHost;
	private final String tag = "Sys";
	private static int CURRENT_TAB = 0;// 选中第５个标签

	private View conventionalTabButton;

	private View chartTabButton;

	private View mapTabButton;

	private View indoorTabButton;

	private View ftpTabButton;

	private View warningTabButton;
	// 判断有没有执行过第三方应用联网控制操作，用于是否需要执行禁网操作；
	public static boolean isExecuteNetControlSetting = false;
	private ApplicationModel appModel = ApplicationModel.getInstance();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.w(tag, "--onCreate--");
		isExecuteNetControlSetting = false;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sys);
		CURRENT_TAB = getIntent().getIntExtra(CURRENTTAB, 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		findView();
		initTabHost();
	}

	public void findView() {
		(initTextView(R.id.title_txt)).setText(R.string.setting);
		findViewById(R.id.pointer).setOnClickListener(this);

		conventionalTabButton = createTab("tab1", R.string.sys_setting_routine);
		chartTabButton = createTab("tab2", R.string.sys_tab_chart);
		mapTabButton = createTab("tab3", R.string.sys_tab_map);
		indoorTabButton = createTab("tab4", R.string.str_cqt);
		ftpTabButton = createTab("tab5", R.string.sys_tab_ftp);
		warningTabButton = createTab("tab6", R.string.sys_tab_alert);
	}

	public void initTabHost() {
		if (tabHost == null) {
			tabHost = getTabHost();
			// 添加第1个标签页 原 SysSetting.class
			tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(conventionalTabButton)
					.setContent(new Intent(this, SysRoutineActivity.class)));

			// 添加第2个标签页 SysChart
			tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(chartTabButton)
					.setContent(new Intent(this, SysChart.class)));

			// 添加第3个标签页
			tabHost.addTab(
					tabHost.newTabSpec("tab3").setIndicator(mapTabButton).setContent(new Intent(this, SysMap.class)));

			// 添加第4个标签页
			if (appModel.getSelectScene() == SceneType.HighSpeedRail || appModel.getSelectScene() == SceneType.Metro) {
				// 高铁和地铁默认隐藏CQT设置
			} else {
				Intent intentalarm = new Intent(this, SysIndoor.class);
				intentalarm.putExtra("AlarmType", WalkStruct.AppType.OperationTest.name());
				tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator(indoorTabButton).setContent(intentalarm));
			}
			// 添加第5个标签页
			tabHost.addTab(
					tabHost.newTabSpec("tab5").setIndicator(ftpTabButton).setContent(new Intent(this, SysFtp.class)));

			// 添加第6个标签页
			tabHost.addTab(tabHost.newTabSpec("tab6").setIndicator(warningTabButton)
					.setContent(new Intent(this, SysAlarm.class)));
		}

		tabHost.setCurrentTab(CURRENT_TAB);
		updateTab(tabHost);
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				if (tabId.equals("tab1")) {
					CURRENT_TAB = TAB_ROUTINE;
				}
				if (tabId.equals("tab2")) {
					CURRENT_TAB = TAB_CHART;
				}
				if (tabId.equals("tab3")) {
					CURRENT_TAB = TAB_MAP;
				}
				if (tabId.equals("tab4")) {
					CURRENT_TAB = TAB_CQT;
				}
				if (tabId.equals("tab5")) {
					CURRENT_TAB = TAB_FTP;
				}
				if (tabId.equals("tab6")) {
					CURRENT_TAB = TAB_ALARM;
				}
				updateTab(tabHost);
			}

		});
	}

	/**
	 * 生成tab页面
	 * 
	 * @param tabTag
	 *            tab标识
	 * @param textId
	 *            文本ID
	 *            内容
	 */
	protected View createTab(String tabTag, int textId) {
		View view = LayoutInflater.from(this).inflate(R.layout.tabmini, null);
		TextView tvTab = (TextView) view.findViewById(R.id.tv_title);
		tvTab.setText(textId);
		return view;
	}

	/**
	 * 更新字体颜色
	 */
	private void updateTab(TabHost tabHost) {
		TabHostUtil.updateTab(this,tabHost);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointer:
			if (isExecuteNetControlSetting) {
				DroidWallUtil.enable(Sys.this);
			}
			Sys.this.finish();
			break;

		default:
			break;
		}
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLocalActivityManager().destroyActivity("tab1",true);
        getLocalActivityManager().destroyActivity("tab2",true);
        getLocalActivityManager().destroyActivity("tab3",true);
        getLocalActivityManager().destroyActivity("tab4",true);
        getLocalActivityManager().destroyActivity("tab5",true);
        getLocalActivityManager().destroyActivity("tab6",true);
    }

    @Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

} // end class Sys