package com.walktour.gui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

import com.dingli.wlan.apscan.WifiScanner;
import com.dinglicom.dataset.EventManager;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ImageUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhone;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicTabActivity;
import com.walktour.gui.analysis.csfb.CsfbPieChartActivity;
import com.walktour.gui.eventbus.OnEventMenuSelectedEvent;
import com.walktour.gui.eventbus.OnL3MsgMenuSelectedEvent;
import com.walktour.gui.map.AlarmMsg;
import com.walktour.gui.map.InfoActivity2;
import com.walktour.gui.map.MapTabActivity;
import com.walktour.gui.map.ParamActivity2;
import com.walktour.gui.newmap2.NewInfoTabActivity;
import com.walktour.gui.replayfloatview.FloatWindowManager;
import com.walktour.gui.replayfloatview.OnReplayWindowListener;
import com.walktour.gui.setting.ParamsSettingActivity;
import com.walktour.gui.setting.SysFloorMap;
import com.walktour.gui.task.activity.scanner.ui.ScannerInfoActivity;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
import com.walktour.gui.weifuwu.sharepush.ShareSendActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * @Activity 查看信息基础页面
 * @History 2013.9.9 qihang.li 集成数据集时，把原来的回放去掉
 */
@SuppressLint("InflateParams")
public class InfoTabActivity extends BasicTabActivity implements OnClickListener, OnTabChangeListener, OnReplayWindowListener {
	private static final String tag = "InfoTabActivity";
	/** 信息类型名称 */
	public static final String INFO_TYPE_NAME = "infoType";
	/** tab集合 */
	protected InfoTabHost myTabhost;
	/** 是否回放 */
	private boolean isReplay;
	/**是否即时回放*/
	private boolean isReplayNow = false;
	/** 当前序号 */
	protected int currentNum = 0;
	/** 应用类 */
	private ApplicationModel appModel = null;
	/** 信息类型 */
	private int infoType = InfoTabHost.INFO_TYPE_NULL;
	/**更多菜单*/
	private PopupWindow popMoreMenu;

	private DisplayMetrics metric;
	/**是否从数据管理跳转过来*/
	private boolean isComeFromDataManager = true;


	/**popMoreMenu的事件相关列表项容器**/
	View llEventContainer = null;
	/**popMoreMenu的信令相关列表项容器**/
	View llL3MsgContainer = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.infoType = this.getIntent().getIntExtra(INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_OTHER);
		appModel = ApplicationModel.getInstance();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.info_toplist);
		FloatWindowManager.setOnReplayWindowListener(this);
		metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		currentNum++;
		findView();
		registBroadcast();
		// 设置是否回放 ,并且启动回放悬浮框
		if (getIntent().getExtras() != null) {
			isReplay = getIntent().getBooleanExtra("isReplay", false);
			if (isReplay) {
				isReplayNow =  getIntent().getBooleanExtra("isReplayNow", false);

				if (!FloatWindowManager.isWindowShowing()) {
					FloatWindowManager.createFloatWindow(getApplicationContext());
					if (isReplayNow) {
						String filePath = getIntent().getStringExtra("filePath");
						LogUtil.i(tag,"即时回放filePath:" + filePath);
						int startIndex =  getIntent().getIntExtra("startIndex", -1);
						int endIndex =  getIntent().getIntExtra("endIndex", -1);
						if(startIndex!=-1&&endIndex!=-1){
							FloatWindowManager.runReplay(filePath,startIndex,endIndex);
						}else
							FloatWindowManager.runReplay(filePath);
					}
				}
			}
		}
    }

	/**
	 * 注册广播
	 */
	private void registBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(WalkMessage.NOTIFY_TESTJOBDONE); // 测试完成
		filter.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE); // 中断测试完成
		filter.addAction(WalkMessage.ACTION_FREEZE_CHANGED); // 冻屏状态更改
		filter.addAction(WalkMessage.ACTION_INFO_MAP2EVENT); // 接受从地图到事件页面跳转
		registerReceiver(myReceiver, filter);
	}

	/**
	 * 广播接收器
	 */
	private BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			String action = intent.getAction();
			if (action.equals(WalkMessage.NOTIFY_TESTJOBDONE) || action.equals(WalkMessage.NOTIFY_INTERRUPTJOBDONE)) {
				final boolean isCsfb = ServerManager.getInstance(getApplicationContext()).getCsfbAnalysis();
				if(isCsfb){
					BasicDialog.Builder bdialog = new BasicDialog.Builder(InfoTabActivity.this)
							.setTitle(R.string.str_tip)
							.setMessage(
									getString(R.string.main_notify_testdone)
											+ (isCsfb ? getString(R.string.csfb_testfinish_toanalysis) : ""))
							.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									// 测试结束后根据系统设置决定是否跳到CSFB异常分析界面
									if (isCsfb) {
										Intent csfbAnalysis = new Intent(getApplicationContext(), CsfbPieChartActivity.class);
										startActivity(csfbAnalysis);
										// 2015移动招标要求在数据管理里可以查看文件轨迹，特做跳转到地图界面对地图做导出处理
									} else if(!appModel.isFuJianTest() && appModel.isSaveMapLocas()){
										TraceInfoInterface.isSaveFileLocus = true;
										TraceInfoInterface.saveFileLocusPath = intent.getStringExtra(WalkMessage.NOTIFY_TESTJOBDONE_PARANAME);
										TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Map;
										Intent mapIntent = new Intent(getApplicationContext(), NewInfoTabActivity.class);
										mapIntent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_MAP);
										startActivity(mapIntent);
									}
								}
							});
					if (isCsfb) {
						bdialog.setNegativeButton(R.string.str_cancle);
					}

					bdialog.show();
				}else{
					Toast.makeText(InfoTabActivity.this, getString(R.string.main_notify_testdone), Toast.LENGTH_LONG).show();
				}
			} else if (intent.getAction().equals(WalkMessage.ACTION_FREEZE_CHANGED)) {
				if (appModel.isFreezeScreen()) {
					(initImageView(R.id.freeze_btn)).setImageResource(R.drawable.navi_lock);
				} else {
					(initImageView(R.id.freeze_btn)).setImageResource(R.drawable.navi_unlock);
				}
			} else if (intent.getAction().equals(WalkMessage.ACTION_INFO_MAP2EVENT)) {
				myTabhost.setCurrentTabByTag("info");
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.w(tag, "---exit replay");
		try {
			unregisterReceiver(myReceiver);
		} catch (Exception e) {
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		LogUtil.w(tag, "----onStart for result");
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		if (appModel.isFreezeScreen()) {
			(initImageView(R.id.freeze_btn)).setImageResource(R.drawable.navi_lock);
		} else {
			(initImageView(R.id.freeze_btn)).setImageResource(R.drawable.navi_unlock);
		}
		if (!TraceInfoInterface.saveEndShowChildTab.equals(WalkStruct.ShowInfoType.Default)) {
			TraceInfoInterface.currentShowChildTab = TraceInfoInterface.saveEndShowChildTab;
			TraceInfoInterface.saveEndShowChildTab = WalkStruct.ShowInfoType.Default;
		}else{
			//fix bug:# 211 qt测试更换室内地图偶尔无效
			if(!SysFloorMap.isFromIndoor){
				TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Param;
				TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Event;
			}
		}

		if (TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.Map)) {
			myTabhost.setCurrentTabByTag("map");
		} else if (TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.Info)) {
			myTabhost.setCurrentTabByTag("info");
		} else if (TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.Param)) {
			myTabhost.setCurrentTabByTag("param");
		} else if (TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.AlarmMsg)) {
			myTabhost.setCurrentTabByTag("alarmmsg");
		} else if (TraceInfoInterface.currentShowTab.equals(WalkStruct.ShowInfoType.Scanner)) {
			myTabhost.setCurrentTabByTag("scanner");
		}
		updateTab(myTabhost);
	}

	/**
	 * 生成界面
	 */
	private void findView() {
		findViewById(R.id.pointer).setOnClickListener(this);
		findViewById(R.id.freeze_btn).setOnClickListener(this);
		findViewById(R.id.capture_btn).setOnClickListener(this);
		findViewById(R.id.tag_btn).setOnClickListener(this);
		myTabhost = (InfoTabHost) findViewById(android.R.id.tabhost);
		myTabhost.setInfoType(this.infoType);
		myTabhost.setActivity(this);
		myTabhost.setInit(true);
		genContentView();
		myTabhost.setInit(false);
		myTabhost.setOnTabChangedListener(this);

	}

	/**
	 * 生成主界面
	 * */
	private void genContentView() {
		if (this.infoType == InfoTabHost.INFO_TYPE_MAP) {
			this.createTab("map", R.string.info_map, new Intent(this, MapTabActivity.class));
			this.createTab("info", R.string.info_info, new Intent(this, InfoBlankActivity.class));
			this.createTab("param", R.string.info_parameter, new Intent(this, InfoBlankActivity.class));
			if (!appModel.isBeiJingTest())
				this.createTab("alarmmsg", R.string.info_alarmmsg, new Intent(this, InfoBlankActivity.class));
			 if (appModel.isScannerTest()) {
			this.createTab("scanner", R.string.sc_main_main, new Intent(this, InfoBlankActivity.class));
			 }
		} else {
			this.createTab("map", R.string.info_map, new Intent(this, InfoBlankActivity.class));
			this.createTab("info", R.string.info_info, new Intent(this, InfoActivity2.class));
			this.createTab("param", R.string.info_parameter, new Intent(this, ParamActivity2.class));
			if (!appModel.isBeiJingTest())
				this.createTab("alarmmsg", R.string.info_alarmmsg, new Intent(this, AlarmMsg.class));
			if (appModel.isScannerTest()) {
				this.createTab("scanner", R.string.sc_main_main, new Intent(this, ScannerInfoActivity.class));
			}
			//			 this.createTab("param2", R.string.work_order_fj_voice_test_param, new Intent(this, ParamActivity.class));//test 正式需要删除
		}
	}

	/**
	 * 生成tab页面
	 *
	 * @param tabTag
	 *          tab标识
	 * @param textId
	 *          文本ID
	 * @param content
	 *          内容
	 */
	protected Button createTab(String tabTag, int textId, Intent content) {
		Button button = new Button(this);
		button.setBackgroundResource(R.drawable.tab_btn_top_bg);
		button.setPadding(3, 5, 3, 5);
		button.setText(textId);
		button.setId(textId);
		button.setTextSize(16);
		button.setTextColor(Color.WHITE);
		if (content == null)
			myTabhost.addTab(myTabhost.newTabSpec(tabTag).setIndicator(button));
		else
			myTabhost.addTab(myTabhost.newTabSpec(tabTag).setIndicator(button).setContent(content));

		return button;
	}


	/**
	 *更新字体颜色
	 */
	private void updateTab(TabHost tabHost){
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			Button button = (Button)tabHost.getTabWidget().getChildTabViewAt(i);
			if(tabHost.getCurrentTab() == i){
				button.setTextColor(getResources().getColor(R.color.info_param_color));
			}else{
				button.setTextColor(Color.WHITE);
			}
		}

	}


	/**
	 * 生成tab页面
	 *
	 * @param tabTag
	 *          tab标识
	 * @param textId
	 *          文本ID
	 */
	protected void createTab(String tabTag, int textId) {
		this.createTab(tabTag, textId, null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointer:
			this.finish();
			break;
		case R.id.freeze_btn:
			if (!appModel.isFreezeScreen()) {
				appModel.setFreezeScreen(true);
				TraceInfoInterface.freezeBakupResult();
				EventManager.getInstance().setFreezeEvent();
				TraceInfoInterface.decodeFreezeNetType = TraceInfoInterface.currentNetType;
			} else {
				appModel.setFreezeScreen(false);
				TraceInfoInterface.unFreezeReductionResult();
				EventManager.getInstance().setUnFreezeEvent();
			}
			Intent freezedIntent = new Intent(WalkMessage.ACTION_FREEZE_CHANGED);
			freezedIntent.putExtra(WalkMessage.FreezeState, appModel.isFreezeScreen());
			sendBroadcast(freezedIntent);
			break;
		case R.id.capture_btn:
			captureScreen();
			break;
		case R.id.tag_btn:
			showMoreMenu();
			break;
		case R.id.txt_params_setting:
			startActivity(new Intent(InfoTabActivity.this, ParamsSettingActivity.class));
			colseMenu();
			break;
		case R.id.txt_tab:
			showAddTagDialog();
			colseMenu();
			break;
		case R.id.txt_search:
			EventBus.getDefault().post(new OnEventMenuSelectedEvent(OnEventMenuSelectedEvent.TYPE_SEARCH));
			colseMenu();
			break;
		case R.id.txt_clear_text:
			EventBus.getDefault().post(new OnEventMenuSelectedEvent(OnEventMenuSelectedEvent.TYPE_CLEAR_TEXT));
			colseMenu();
			break;
		case R.id.txt_save:
			EventBus.getDefault().post(new OnEventMenuSelectedEvent(OnEventMenuSelectedEvent.TYPE_SAVE));
			colseMenu();
			break;
		case R.id.txt_add_label:
			EventBus.getDefault().post(new OnEventMenuSelectedEvent(OnEventMenuSelectedEvent.TYPE_ADD_LABEL));
			colseMenu();
			break;
		case R.id.txt_fleet_complain:
			EventBus.getDefault().post(new OnEventMenuSelectedEvent(OnEventMenuSelectedEvent.TYPE_FLEET_COMPLAIN));
			colseMenu();
			break;
		case R.id.txt_setting:
			EventBus.getDefault().post(new OnEventMenuSelectedEvent(OnEventMenuSelectedEvent.TYPE_SETTING));
			colseMenu();
			break;
		case R.id.tv_msg_search:
			EventBus.getDefault().post(new OnL3MsgMenuSelectedEvent(OnL3MsgMenuSelectedEvent.TYPE_SEARCH));
			colseMenu();
			break;
		case R.id.tv_msg_setting:
			EventBus.getDefault().post(new OnL3MsgMenuSelectedEvent(OnL3MsgMenuSelectedEvent.TYPE_SETTING));
			colseMenu();
			break;
		case R.id.tv_refresh_setting:
			EventBus.getDefault().post(new OnL3MsgMenuSelectedEvent(OnL3MsgMenuSelectedEvent.TYPE_REFRESH_SETTING));
			colseMenu();
			break;
		case R.id.tv_save_msg_list:
			EventBus.getDefault().post(new OnL3MsgMenuSelectedEvent(OnL3MsgMenuSelectedEvent.TYPE_SAVE_MSG_LIST));
			colseMenu();
			break;


		default:

			break;
		}

	}

	@SuppressWarnings("deprecation")
	private void showMoreMenu() {
		if (popMoreMenu == null) {
			View view = LayoutInflater.from(this).inflate(R.layout.menu_info_more, null);
			view.findViewById(R.id.txt_params_setting).setOnClickListener(this);
			view.findViewById(R.id.txt_tab).setOnClickListener(this);
			llEventContainer = view.findViewById(R.id.ll_event_container);
			llL3MsgContainer = view.findViewById(R.id.ll_l3msg_container);
			//当处于PARAM界面下的事件界面时，点击更多才显示事件menu列表项
			llEventContainer.setVisibility(TraceInfoInterface.sIsOnEvent ? View.VISIBLE : View.GONE);
			//当处于PARAM界面下的信令界面时，点击更多才显示信令menu列表项
			llL3MsgContainer.setVisibility(TraceInfoInterface.sIsOnL3Msg ? View.VISIBLE : View.GONE);
			view.findViewById(R.id.txt_search).setOnClickListener(this);
			view.findViewById(R.id.txt_clear_text).setOnClickListener(this);
			view.findViewById(R.id.txt_save).setOnClickListener(this);
			view.findViewById(R.id.txt_add_label).setOnClickListener(this);
			view.findViewById(R.id.txt_fleet_complain).setOnClickListener(this);
			view.findViewById(R.id.txt_setting).setOnClickListener(this);
			view.findViewById(R.id.tv_msg_search).setOnClickListener(this);
			view.findViewById(R.id.tv_msg_setting).setOnClickListener(this);
			view.findViewById(R.id.tv_refresh_setting).setOnClickListener(this);
			view.findViewById(R.id.tv_save_msg_list).setOnClickListener(this);
			popMoreMenu = new PopupWindow(view, LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, true);//
			popMoreMenu.setOutsideTouchable(true);
			popMoreMenu.setFocusable(true);
			popMoreMenu.setTouchable(true);
			popMoreMenu.setBackgroundDrawable(new BitmapDrawable());
//			popMoreMenu.setAnimationStyle(R.style.popwin_anim_down_in_style);
			popMoreMenu.showAsDropDown(findViewById(R.id.title_layout), 0, 10);
		} else {
			if (popMoreMenu.isShowing()) {
				popMoreMenu.dismiss();
			} else {
				if(null != llEventContainer && null != llL3MsgContainer){
					//当处于PARAM界面下的事件界面时，点击更多才显示事件menu列表项
					llEventContainer.setVisibility(TraceInfoInterface.sIsOnEvent ? View.VISIBLE : View.GONE);
					//当处于PARAM界面下的信令界面时，点击更多才显示信令menu列表项
					llL3MsgContainer.setVisibility(TraceInfoInterface.sIsOnL3Msg ? View.VISIBLE : View.GONE);
				}
				popMoreMenu.showAsDropDown(findViewById(R.id.title_layout), 0, 10);
			}
		}
	}

	private void colseMenu() {
		if (popMoreMenu != null) {
			if (popMoreMenu.isShowing()) {
				popMoreMenu.dismiss();
			}
		}
	}

	/**
	 * 显示增加事件标注
	 */
	private void showAddTagDialog() {
		LayoutInflater fac = LayoutInflater.from(getApplicationContext());
		View view = fac.inflate(R.layout.alert_dialog_edittext12, null);
		final EditText edittext = (EditText) view.findViewById(R.id.alert_textEditText);
		final long lableTime = System.currentTimeMillis();
		BasicDialog alert = new BasicDialog.Builder(InfoTabActivity.this).setIcon(android.R.drawable.ic_menu_edit)
				.setTitle(R.string.info_add_label).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String eventStr = edittext.getText().toString().trim();
						if (eventStr == null || eventStr.equals("")) {

						} else {
							EventManager.getInstance().addTagEvent(InfoTabActivity.this, lableTime, eventStr);
						}
					}
				}).setNegativeButton(R.string.str_cancle).create();
		alert.show();
	}

	/**
	 * 截屏操作
	 */
	private void captureScreen() {
		// 如果Sdcard不可写，则写到手机
		String desFileDir = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
				AppFilePathUtil.getInstance().getSDCardBaseDirectory(getString(R.string.path_snapshot))
				: AppFilePathUtil.getInstance().getAppFilesDirectory(getString(R.string.path_snapshot));
		File snapDir = new File(desFileDir);
		if (!snapDir.isDirectory()) {
			snapDir.mkdirs();
		}

		new BasicDialog.Builder(InfoTabActivity.this).setIcon(android.R.drawable.ic_menu_camera)
				.setTitle(R.string.str_snapshot).setMessage(getString(R.string.str_snapshot_save) + desFileDir)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						String desFileDir = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
								AppFilePathUtil.getInstance().getSDCardBaseDirectory(getString(R.string.path_snapshot))
								: AppFilePathUtil.getInstance().getAppFilesDirectory(getString(R.string.path_snapshot));
						File snapDir = new File(desFileDir);
						if (!snapDir.isDirectory()) {
							snapDir.mkdirs();
						}
						MyPhone phone = new MyPhone(InfoTabActivity.this);
						phone.getScreen(InfoTabActivity.this, desFileDir, ImageUtil.FileType.PNG);
					}
				}).setNeutralButton(R.string.share_project_share, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						//先截屏
						String desFileDir = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
								AppFilePathUtil.getInstance().getSDCardBaseDirectory(getString(R.string.path_snapshot))
								: AppFilePathUtil.getInstance().getAppFilesDirectory(getString(R.string.path_snapshot));
						File snapDir = new File(desFileDir);
						if (!snapDir.isDirectory()) {
							snapDir.mkdirs();
						}
						MyPhone phone = new MyPhone(InfoTabActivity.this);
						phone.getScreen(InfoTabActivity.this, desFileDir,ImageUtil.FileType.PNG);
						//再分享
						Bundle bundle=new Bundle();
						bundle.putInt(ShareCommons.SHARE_FROM_KEY,ShareCommons.SHARE_FROM_SCREENSHOT_PIC);
						jumpActivity(ShareSendActivity.class, bundle);
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}

	@Override
	public void onTabChanged(String tabId) {
		currentNum--;
		updateTab(myTabhost);
		if (tabId.equals("map")) {
			TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Map;
			TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Default;
		} else if (tabId.equals("info")) {
			TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Info;
		} else if (tabId.equals("param")) {
			TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Param;
		} else if (tabId.equals("alarmmsg")) {
			TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.AlarmMsg;
		} else if (tabId.equals("scanner")) {
			TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Scanner;
			TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Default;
		} else if (tabId.equals("param2")) {
			TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Param;
		}
		/**
		 * 处理数据集切换界面监听，通过获取子View的Tag
		 */
		TabHost tabHost = (TabHost) myTabhost.getCurrentView().findViewById(R.id.child_tabhost);
		if (tabHost != null && currentNum != 0) {
			String tabTag = tabHost.getCurrentTabTag();
				if (tabTag == null) {
					tabTag = "";
				}
				if (tabTag.equals("Event")) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Event;
				} else if (tabTag.equals("Chart")) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Chart;
				} else if (tabTag.equals("Data")) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Data;
				} else if (tabTag.equals("L3Msg")) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.L3Msg;
				} else if (tabTag.equals("VideoPlay")) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.VideoPlay;
				} else if (tabTag.equals(getResources().getString(R.string.info_gsm))) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Gsm;
				} else if (tabTag.equals(getResources().getString(R.string.info_umts))) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Umts;
				} else if (tabTag.equals(getResources().getString(R.string.info_hspa))) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Hspa;
				} else if (tabTag.equals(getResources().getString(R.string.info_hspa_plus))) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.HspaPlus;
				} else if (tabTag.equals(getResources().getString(R.string.info_lte))) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.LTE;
				} else if (tabTag.equals(getResources().getString(R.string.info_cdma))) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Cdma;
				} else if (tabTag.equals(getResources().getString(R.string.info_evdo))) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.EvDo;
				} else if (tabTag.equals(getResources().getString(R.string.info_edge))) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Edge;
				} else if (tabTag.equals(getResources().getString(R.string.info_tdscdma))) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.TDSCDMA;
				} else if (tabTag.equals(getResources().getString(R.string.info_tdhspa))) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.TDHspaPlus;
				} else if (tabTag.equals(getResources().getString(R.string.info_tcpip))) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.TcpIpPcap;
				} else if(tabTag.equals(getResources().getString(R.string.info_ca))){
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.LTECA;
				}else if(tabTag.equals(getResources().getString(R.string.info_4t4r))){
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.LTE4T4R;
				}
			}

		Log.i(tag, "currentShowTab:" + TraceInfoInterface.currentShowChildTab);
		// 这里把static对象放在WifiScanner里，不放在Info页面中
		if (WifiScanner.isScannerWifi() && !tabId.equals("param")) {
			WifiScanner.setScannerWifi(false);
			WifiScanner.instance(this).stopScan();
		}
	}

	@Override
	public void finish() {
		super.finish();
		if (TraceInfoInterface.currentShowChildTab.equals(WalkStruct.ShowInfoType.Default)) {
			TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Default;
		} else {
			TraceInfoInterface.saveEndShowChildTab = TraceInfoInterface.currentShowChildTab;
			TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Default;
		}
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		try {
			return super.dispatchKeyEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void onWindowClose() {
		if (isComeFromDataManager) {
			finish();
		}
	}
}
