package com.walktour.gui.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ToastUtil;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicTabActivity;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.task.activity.phone.TaskGroupExpandListView;
import com.walktour.gui.task.activity.scannertsma.ui.ScanTSMATaskListActivity;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.weifuwu.ShareDialogActivity;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
import com.walktour.gui.weifuwu.sharepush.ShareTaskActivity;
import com.walktour.gui.weifuwu.view.BadgeView;

import java.util.List;

/**
 * 测试任务(包含Phone和Scanner)
 * 
 * @author weirong.fan
 *
 */
public class TaskAll extends BasicTabActivity implements OnClickListener, OnTabChangeListener {
	private Context mContext = TaskAll.this;
	/** tabhost对象 **/
	private TabHost tabhost;
	/** 是否从工单系统跳转过来的 */
	private boolean isFromWorkOrder = false;
	private CheckBox checkBoxBtu = null;
	private ServerManager mSerMgr = null;
	private boolean isBtuMode = false;
	/** 参数存储 */
//	private SharedPreferences preferences;
	/** 标题栏 **/
	private RelativeLayout layout;
	/** 分享接收按钮 **/
	private LinearLayout sharepushLayout;
	private Button nb;
	//按钮上显示文字
	private BadgeView badge1;
	private FileReceiver receiver = new FileReceiver();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.walktour.gui.R.layout.task_tabhost_all);
//		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		mSerMgr = ServerManager.getInstance(this);
		isFromWorkOrder = this.getIntent().getBooleanExtra("fromWorkOrder", false);
		layout = initRelativeLayout(com.walktour.gui.R.id.title_layout);
		initTextView(com.walktour.gui.R.id.title_txt).setText(getString(com.walktour.gui.R.string.act_task_group));
		findViewById(com.walktour.gui.R.id.pointer).setOnClickListener(this);
		isBtuMode = mSerMgr.getUploadServer() == ServerManager.SERVER_BTU
				|| mSerMgr.getUploadServer() == ServerManager.SERVER_ATU;
		sharepushLayout = (LinearLayout) this.findViewById(com.walktour.gui.R.id.sharepush);
		sharepushLayout.setVisibility(View.VISIBLE);
		sharepushLayout.findViewById(com.walktour.gui.R.id.share).setOnClickListener(this);
		sharepushLayout.findViewById(com.walktour.gui.R.id.push).setOnClickListener(this);
		nb = (Button) sharepushLayout.findViewById(com.walktour.gui.R.id.push);
		nb.setOnClickListener(this);
		badge1 = new BadgeView(this, nb);
	    badge1.setText("1");
	    badge1.setTextColor(getResources().getColor(com.walktour.gui.R.color.white));
	    badge1.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
	    badge1.hide();
		initButtonNum();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ShareCommons.SHARE_ACTION_2);
		this.registerReceiver(receiver, filter);
		if (isBtuMode)
			initBtn();
		initTabHost();
	}
	@Override
	protected void onDestroy() {
		getLocalActivityManager().destroyActivity("Routine",true);
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}
	@SuppressWarnings("deprecation")
	private void initBtn() {
		checkBoxBtu = (CheckBox) findViewById(com.walktour.gui.R.id.CheckBoxBtu);
		checkBoxBtu.setVisibility(View.VISIBLE);
		checkBoxBtu.setChecked(mSerMgr.getDTLogCVersion() > 0);
		checkBoxBtu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int messageID = -1;
				if (checkBoxBtu.isChecked()) {
					if (mSerMgr.getUploadServer() == ServerManager.SERVER_BTU) {
						messageID = com.walktour.gui.R.string.act_task_btu_check;
					} else {
						messageID = com.walktour.gui.R.string.act_task_atu_check;
					}
					new AlertDialog.Builder(mContext).setTitle(com.walktour.gui.R.string.str_tip).setMessage(messageID)
							.setCancelable(false)
							.setPositiveButton(com.walktour.gui.R.string.str_return, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							checkBoxBtu.setChecked(false);
							Activity act = getCurrentActivity();
							if (act instanceof TaskGroupExpandListView) {
								TaskGroupExpandListView act1 = (TaskGroupExpandListView) act;
								act1.genToolBar();
							}
						}
					}).show();
				} else {
					if (mSerMgr.getUploadServer() == ServerManager.SERVER_BTU) {
						messageID = com.walktour.gui.R.string.act_task_btu_edit;
					} else {
						messageID = com.walktour.gui.R.string.act_task_atu_edit;
					}
					new AlertDialog.Builder(mContext).setTitle(com.walktour.gui.R.string.str_tip).setMessage(messageID)
							.setCancelable(false)
							.setPositiveButton(com.walktour.gui.R.string.delete, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 重置测试计划版本号
							mSerMgr.setDTLogCVersion(0);
							// 清除所有业务
							TaskListDispose.getInstance().getTestPlanConfig().getTestSchemas().getTestSchemaConfig()
									.getTaskGroups().clear();
							Activity activity = getCurrentActivity();
							if (activity instanceof TaskGroupExpandListView) {
								TaskGroupExpandListView act1 = (TaskGroupExpandListView) activity;
								act1.genToolBar();
								act1.refreshGroupData();
							}
						}
					}).setNegativeButton(com.walktour.gui.R.string.str_cancle, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							checkBoxBtu.setChecked(true);
						}
					}).show();
				}
			}
		});
	}
	@SuppressWarnings("deprecation")
	public void initTabHost() {
		tabhost = getTabHost();
		tabhost.setOnTabChangedListener(this);
		Button paramTabBtn = new Button(this);
		paramTabBtn.setBackgroundDrawable(getResources().getDrawable(com.walktour.gui.R.drawable.tab_btn_top_bg));
		paramTabBtn.setPadding(3, 5, 3, 5);
		paramTabBtn.setText("Phone");
		paramTabBtn.setTextColor(getResources().getColor(com.walktour.gui.R.color.white));
		Intent intent = null;
		if (WalktourApplication.isExitGroup()) {
			layout.setVisibility(View.VISIBLE);
			// intent = new Intent(this, TaskGroup.class);
			intent = new Intent(this, TaskGroupExpandListView.class);
		} else {
			layout.setVisibility(View.GONE);
			TaskListDispose.getInstance().createDefaultGroup();
			intent = new Intent(this, Task.class);
		}
		intent.putExtra("fromWorkOrder", isFromWorkOrder);
		tabhost.addTab(tabhost.newTabSpec("Routine").setIndicator(paramTabBtn).setContent(intent));
		if (ApplicationModel.getInstance().hasScannerTSMATest()) {
			Button mocTabBtn = new Button(this);
			mocTabBtn.setBackgroundDrawable(getResources().getDrawable(com.walktour.gui.R.drawable.tab_btn_top_bg));
			mocTabBtn.setPadding(3, 5, 3, 5);
			mocTabBtn.setText("Scanner");
			mocTabBtn.setTextColor(getResources().getColor(com.walktour.gui.R.color.white));
			tabhost.addTab(tabhost.newTabSpec("Scanner").setIndicator(mocTabBtn)
					.setContent(new Intent(this, ScanTSMATaskListActivity.class)));
		} else {
			getTabWidget().setVisibility(View.GONE);
		}
	}
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		updateTab(tabhost);
	}
	/**
	 * 更新字体颜色
	 */
	private void updateTab(TabHost tabHost) {
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			Button button = (Button) tabHost.getTabWidget().getChildTabViewAt(i);
			if (tabHost.getCurrentTab() == i) {
				button.setTextColor(getResources().getColor(com.walktour.gui.R.color.info_param_color));
			} else {
				button.setTextColor(Color.WHITE);
			}
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case com.walktour.gui.R.id.pointer:
			finish();
			break;
		case com.walktour.gui.R.id.share:// 分享
			Bundle bundle = new Bundle();
			bundle.putInt("from", 1);
			this.jumpActivity(ShareTaskActivity.class, bundle);
			break;
		case com.walktour.gui.R.id.push:// 接收
			try {
				//无新的共享信息,给出提示
				List<ShareFileModel> lists = ShareDataBase.getInstance(mContext).fetchAllFilesByFileStatusAndFileType(
						ShareFileModel.FILETYPE_GROUP, new int[]{ShareFileModel.FILE_STATUS_INIT, ShareFileModel.FILE_STATUS_START, ShareFileModel.FILE_STATUS_ONGOING});
				if (lists.size() <= 0) {
					ToastUtil.showToastShort(TaskAll.this, com.walktour.gui.R.string.share_project_share_info_obj_receive);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			badge1.hide();
			// 跳转到接收diaolog
			Bundle bundle2 = new Bundle();
			bundle2.putInt("fileType", ShareFileModel.FILETYPE_GROUP);
			this.jumpActivity(ShareDialogActivity.class, bundle2);
			break;
		default:
			break;
		}
	}
	@Override
	public void onTabChanged(String tabId) {
		updateTab(tabhost);
	}
	public void setSharepushLayouVisible(int visible) {
		sharepushLayout.setVisibility(visible);
	}
	public void setCheckBox(boolean flag) {
		checkBoxBtu.setChecked(flag);
	}
	/***
	 * 广播接收器，更新接收按钮上的数组
	 * 
	 * @author weirong.fan
	 *
	 */
	private class FileReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			initButtonNum();
		}
	}
	/**
	 * 接收到广播后
	 */
	private void initButtonNum() {
		try {
			List<ShareFileModel> lists = ShareDataBase.getInstance(mContext).fetchAllFilesByFileStatusAndFileType(
					ShareFileModel.FILETYPE_GROUP, new int[]{ShareFileModel.FILE_STATUS_INIT});
			if (lists.size() > 0){
				badge1.setText(lists.size()+"");
				badge1.show(true);
			}else{
				badge1.hide(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
