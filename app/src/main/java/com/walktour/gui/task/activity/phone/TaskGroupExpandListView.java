package com.walktour.gui.task.activity.phone;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct;
import com.walktour.control.adapter.SchemeAdapter;
import com.walktour.control.config.ConfigAutoTest;
import com.walktour.control.config.ProjectManager;
import com.walktour.control.config.ServerManager;
import com.walktour.customView.ListViewForScrollView;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.applet.MySimpleAdapter;
import com.walktour.gui.fleet.Fleet;
import com.walktour.gui.listener.ServerStatus;
import com.walktour.gui.listener.ServerStatusListener;
import com.walktour.gui.setting.sysroutine.SysRoutineActivity;
import com.walktour.gui.task.Task;
import com.walktour.gui.task.TaskAll;
import com.walktour.gui.task.TaskHistory;
import com.walktour.gui.task.activity.phone.TaskGroupExpandAdapter.OnClickMyListener;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.TaskGroupConfig;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.xml.btu.TaskConverter;
import com.walktour.gui.task.parsedata.xml.btu.model.TestScheme;
import com.walktour.gui.task.parsedata.xml.common.TaskXmlTools;
import com.walktour.wifip2p.WiFiDirectActivity;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
/**
 * 任务组管理界面,提供展开、收缩等操作
 * 
 * @author zhihui.lian
 */
@SuppressLint({ "InflateParams", "HandlerLeak" })
public class TaskGroupExpandListView extends BasicActivity
		implements  ServerStatusListener, OnClickMyListener {
	private Context mContext = TaskGroupExpandListView.this;
	private ImageView pointer;
	private ListView listView;
	private List<TaskGroupConfig> groupLists = new ArrayList<TaskGroupConfig>();
	private ControlBar bar;
	private Button allBtn;
	private Button noBtn;
	private Button refreshBtn;
	private Button delBtn;
	private ApplicationModel appModel = ApplicationModel.getInstance();
	private Button btnMore;
	private LinearLayout deleteBar;
	private Button btnDelete;
	private Button btnCancle;
	private TaskGroupExpandAdapter taskGroupAdapter;
	private TaskListDispose mInstance;
	private SparseBooleanArray mapExist = new SparseBooleanArray();
	/** 更多PopWindow */
	private PopupWindow morePopupWindow;
	/** 全选与反选控制 */
	private boolean isSelectAll = true;
	/** 下载测试任务进度 **/
	private ProgressDialog progressDialog;
	/** 下载测试任务管理器 **/
	private ServerManager serverManager = null;
	private DisplayMetrics metric;
	/** 测试任务导出导入 */
	private ProjectManager proManager = null;
	/** 保存 */
	public static final int TASK_SAVE = 0;
	/** 加载task */
	public static final int TASK_LOAD = 1;
	/** 任务列表 */
	private TaskListDispose taskListDispose;
	/** 是否来至工单界面 */
//	private boolean isFromWorkOrder = false;
	// 导入任务的加载模式 0-覆盖 1-追加
	private int loadModel = TaskXmlTools.LOADMODEL_APPEND;
	private ServerManager mSerMgr = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSerMgr = ServerManager.getInstance(this);
		mSerMgr.setServerStatusChangeListener(this);
		initView();
	}
	@Override
	protected void onResume() {
		super.onResume();
		mSerMgr.setServerStatusChangeListener(this);
		refreshGroupData();
		mapExist.clear();
		for (int i = 0; i < groupLists.size(); i++) {
			if (groupLists.get(i).isCheck()) {
				mapExist.put(i, true);
			} else {
				mapExist.put(i, false);
			}
		}
		taskGroupAdapter.notifyDataSetChanged();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button01: // 新建
			createGroup();
			break;
		case R.id.Button02: // 删除
			TaskAll act = ((TaskAll) this.getParent());
			act.setSharepushLayouVisible(View.GONE);
			bar.setVisibility(View.GONE);
			deleteBar.setVisibility(View.VISIBLE);
			taskGroupAdapter.setDelMode(true);
			btnDelete.setText(getResources().getString(R.string.delete));
			break;
		case R.id.Button03:
			downloadTask(false);// 下载测试任务
			break;
		case R.id.Button04: // 全选/反选
			deSelectGropu();
			break;
		case R.id.Button05: // 更多
			showMorePopView();
			break;
		case R.id.Import_rv:
			// 先判断SD卡是否可用
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				ToastUtil.showToastShort(mContext, getString(R.string.sdcard_non));
			} else {
				showMyDialog(TASK_LOAD, getString(R.string.task_import_task));
			}
			morePopupWindow.dismiss();
			break;
		case R.id.Export_rv:
			// 先判断SD卡是否可用
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				ToastUtil.showToastShort(mContext, getString(R.string.sdcard_non));
			} else {
				showMyDialog(TASK_SAVE, getString(R.string.task_export_task));
			}
			morePopupWindow.dismiss();
			break;
		case R.id.History_rv:
			Bundle bundle = new Bundle();
			bundle.putBoolean("isGroup", true);
			this.jumpActivity(TaskHistory.class, bundle);
			morePopupWindow.dismiss();
			break;
		case R.id.Share_rv: {
			if (android.os.Build.VERSION.SDK_INT < 14) {
				ToastUtil.showToastShort(mContext, R.string.file_share_version_low);
			} else {
				String filename = "Task" + UtilsMethod.getSimpleDateFormat7(System.currentTimeMillis());
				proManager.saveTask(filename, true);
				Intent intent = new Intent(this, WiFiDirectActivity.class);
				intent.putExtra("filename", filename + ".xml");
				startActivity(intent);
			}
			morePopupWindow.dismiss();
		}
			break;
		case R.id.pointer:
			finish();
			break;
		case R.id.ButtonDelete: // 执行删除按钮
			deleGroup(taskGroupAdapter.getDeleteMap());
			taskGroupMode();
			break;
		case R.id.ButtonCancle: // 取消按钮
			taskGroupMode();
			break;
		default:
			break;
		}
	}
	/**
	 * 恢复正常模式
	 */
	private void taskGroupMode() {
		taskGroupAdapter.setDelMode(false);
		deleteBar.setVisibility(View.GONE);
		bar.setVisibility(View.VISIBLE);
		TaskAll act = ((TaskAll) this.getParent());
		act.setSharepushLayouVisible(View.VISIBLE);
	}
	/**
	 * 导出任务时从列表中获取要保存的列表名
	 */
	private String getTaskGroupListNames() {
		StringBuffer name = new StringBuffer();
		List<TaskGroupConfig> groups = taskListDispose.getCurrentGroups();
		if (null != groups && groups.size() > 0) {
			for (TaskGroupConfig g : groups) {
				if (!name.toString().contains(g.getGroupName() + "_")) {
					name.append(g.getGroupName() + "_");
				}
			}
			return name.toString().substring(0, name.toString().length() - 1);
		}
		return "";
	}
	private void showMyDialog(int id, String title) {
		LayoutInflater factory = LayoutInflater.from(this);
		switch (id) {
		// 导出测试任务
		case TASK_SAVE:
			final View textEntryView = factory.inflate(R.layout.alert_dialog_edittext1, null);
			final EditText taskName = (EditText) textEntryView.findViewById(R.id.alert_textEditText);
			taskName.setText(getTaskGroupListNames() + "");
			new BasicDialog.Builder(this).setView(textEntryView).setTitle(title)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 先判断SD卡是否可用
							if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
								ToastUtil.showToastShort(mContext, getString(R.string.sdcard_non));
								return;
							}
							// 如果名字为空
							if (taskName.getText().toString().trim().length() == 0) {
								ToastUtil.showToastShort(mContext, getString(R.string.task_export_name_null));
								showMyDialog(TASK_SAVE, getString(R.string.task_export_task));
							} else {
								proManager.saveTask(taskName.getText().toString(), true);
							}
						}
					}).setNegativeButton(R.string.str_cancle).show();
			break;
		// 导入测试任务
		case TASK_LOAD:
			final View viewLoad = factory.inflate(R.layout.alert_dialog_listview, null);
			RadioGroup radioGroup = (RadioGroup) viewLoad.findViewById(R.id.loadmodel);
			radioGroup.setVisibility(View.VISIBLE);
			RadioButton fugaiBtn = (RadioButton) viewLoad.findViewById(R.id.isfugai);
			fugaiBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked)
						loadModel = TaskXmlTools.LOADMODEL_REPLACE;
				}
			});
			RadioButton zhuJiaBtn = (RadioButton) viewLoad.findViewById(R.id.iszhuijia);
			zhuJiaBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked)
						loadModel = TaskXmlTools.LOADMODEL_APPEND;
				}
			});
			if (loadModel == TaskXmlTools.LOADMODEL_REPLACE) {
				fugaiBtn.setChecked(true);
				zhuJiaBtn.setChecked(false);
			} else {
				fugaiBtn.setChecked(false);
				zhuJiaBtn.setChecked(true);
			}
			// ListView
			ListViewForScrollView listView = (ListViewForScrollView) viewLoad.findViewById(R.id.ListView);
			ArrayList<HashMap<String, Object>> itemArrayList = new ArrayList<HashMap<String, Object>>();// checkListItemAdapter的参数
			final ArrayList<File> taskFileList = proManager.getTaskFileList(true);
			for (File f : taskFileList) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemTitle", f.getName().substring(0, f.getName().lastIndexOf(".xml")));// android:background="@drawable/background_btn_delete"
				map.put("ItemImage", R.drawable.background_btn_delete);
				map.put(MySimpleAdapter.KEY_DELETE, f);
				itemArrayList.add(map);
			}
			MySimpleAdapter checkListItemAdapter = new MySimpleAdapter(this, itemArrayList,
					R.layout.listview_item_style13, new String[] { "ItemTitle", "ItemImage" },
					new int[] { R.id.ItemTitle, R.id.ItemImage });
			listView.setAdapter(checkListItemAdapter);// listView用到的Adapter
			final BasicDialog alertDialog = new BasicDialog.Builder(this).setTitle(title).setView(viewLoad)
					.setPositiveButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).create();
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					// 先判断SD卡是否可用
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						int loadresult = proManager.loadTask(taskFileList.get(arg2), loadModel);
						if (loadresult == ProjectManager.LOAD_RESULT_VERSIONEROR) {
							ToastUtil.showToastShort(mContext, getString(R.string.task_import_task_error));
						} else
							ToastUtil.showToastShort(mContext, getString(R.string.task_import_task_success));
						initView();
					} else {
						ToastUtil.showToastShort(mContext, getString(R.string.sdcard_non));
					}
					alertDialog.dismiss();
				}
			});
			alertDialog.show();
			break;
		}
	}
	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		setContentView(R.layout.taskgroup_expandview);
		metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
//		this.isFromWorkOrder = this.getIntent().getBooleanExtra("fromWorkOrder", false);
		taskListDispose = TaskListDispose.getInstance();
		proManager = new ProjectManager(this);
		mInstance = TaskListDispose.getInstance(); // 初始化实例
		bar = (ControlBar) findViewById(R.id.ControlBar);
		deleteBar = initLinearLayout(R.id.DeleteBar);
		btnDelete = initButton(R.id.ButtonDelete);
		btnDelete.setOnClickListener(this);
		btnCancle = initButton(R.id.ButtonCancle);
		btnCancle.setOnClickListener(this);
		pointer = initImageView(R.id.pointer);
		pointer.setOnClickListener(this);
		listView = (ListView) findViewById(R.id.report_path_listview_id);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				TaskListDispose.getInstance().setGroupID(groupLists.get(arg2).getGroupID());
				TaskGroupExpandListView.this.jumpActivity(Task.class);
			}
		});
		refreshGroupData();
		taskGroupAdapter = new TaskGroupExpandAdapter(mContext, groupLists, mapExist);
		taskGroupAdapter.setOnClickListener(this);
		listView.setAdapter(taskGroupAdapter);
		taskGroupAdapter.notifyDataSetChanged();
		serverManager = ServerManager.getInstance(this);
		serverManager.setServerStatusChangeListener(this);
	}
	public void genToolBar4Btu() {
		bar.setVisibility(appModel.isTestJobIsRun() ? View.GONE : View.VISIBLE);
		Button btnDown = bar.getButton(0);
		btnDown.setText(R.string.download);
		btnDown.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.controlbar_download), null, null);
		btnDown.setOnClickListener(btuBtnListener);
		Button btnScheme = bar.getButton(1);
		btnScheme.setText(R.string.act_task_scheme);
		btnScheme.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_muilt),
				null, null);
		btnScheme.setOnClickListener(btuBtnListener);
		bar.getButton(2).setVisibility(View.GONE);
		bar.getButton(3).setVisibility(View.GONE);
		bar.getButton(4).setVisibility(View.GONE);
		bar.getButton(5).setVisibility(View.GONE);
	}
	private OnClickListener btuBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.Button01:// 下载
				downloadTask(false);
				break;
			case R.id.Button02:// 转换测试方案
				new ConvertTask().execute(mSerMgr.getDTLogScheme(), ConvertTask.MODE_CONVERT);
				break;
			}
		}
	};
	public void genToolBar() {
		allBtn = bar.getButton(0);
		allBtn.setText(getString(R.string.act_task_new));
		allBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_addto),
				null, null);
		allBtn.setOnClickListener(this);
		noBtn = bar.getButton(1);
		noBtn.setText(getString(R.string.delete));
		noBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_delete),
				null, null);
		noBtn.setOnClickListener(this);
		delBtn = bar.getButton(2);
		delBtn.setText(getString(R.string.download));
		delBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_download),
				null, null);
		delBtn.setOnClickListener(this);
		refreshBtn = bar.getButton(3);
		refreshBtn.setText(getString(R.string.str_checkall));
		refreshBtn.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.controlbar_selectall), null, null);
		refreshBtn.setOnClickListener(this);
		btnMore = bar.getButton(4);
		btnMore.setVisibility(View.VISIBLE);
		btnMore.setText(R.string.str_more);
		btnMore.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_more),
				null, null);
		btnMore.setOnClickListener(this);
	}
	/**
	 * 刷新测试任务组数据
	 */
	public void refreshGroupData() {
		if (mSerMgr.getDTLogCVersion() > 0) {
			TaskAll act = ((TaskAll) this.getParent());
			act.setCheckBox(true);
			genToolBar4Btu();
		} else {
			genToolBar();
		}
		groupLists.clear();
		List<TaskGroupConfig> groups = mInstance.getCurrentGroups();
		for (TaskGroupConfig group : groups) {
			if (group.getGroupStatus() != TaskGroupConfig.GROUPSTATUS_0)
				groupLists.add(group);
		}
		mapExist.clear();
		for (int i = 0; i < groupLists.size(); i++) {
			mapExist.put(i, groupLists.get(i).isCheck());
		}
	}
	/***
	 * 下载测试任务
	 * 
	 * @param force
	 */
	private void downloadTask(boolean force) {
		// 如果有自动测试权限并且自动测试已经开启，给提示
		if (ApplicationModel.getInstance().getAppList().contains(WalkStruct.AppType.AutomatismTest)
				&& new ConfigAutoTest().isAutoTestOn()) {
			showAutoTestOnDialog();
		} else {
			if (serverManager.hasDownloadServerSet()) {
				openDialog("", true);
				serverManager.downloadTestTask(force);
			} else {
				new BasicDialog.Builder(this).setTitle(R.string.task_alertSetServer)
						.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent(TaskGroupExpandListView.this, SysRoutineActivity.class);
								intent.setAction(SysRoutineActivity.SHOW_DATA_UPLOAD_TAB);
								startActivity(intent);
							}
						}).show();
			}
		}
	}
	/**
	 * 打开对话框
	 * 
	 * @param message
	 * @param cancleable
	 */
	private void openDialog(String message, boolean cancleable) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(cancleable);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}
	/**
	 * 关闭对话框
	 */
	private void closeDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	private void showAutoTestOnDialog() {
		new BasicDialog.Builder(this).setTitle(R.string.str_tip).setMessage(R.string.main_autoison)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(TaskGroupExpandListView.this, Fleet.class));
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}
	/**
	 * 新建组
	 */
	private void createGroup() {
		BasicDialog.Builder builder = new BasicDialog.Builder(this);
		final View viewDialog = LayoutInflater.from(this).inflate(R.layout.alert_dialog_edittext, null);
		final EditText editT = (EditText) viewDialog.findViewById(R.id.alert_textEditText);
		builder.setTitle(getString(R.string.act_task_group_input));
		builder.setIcon(R.drawable.icon_info);
		builder.setView(viewDialog);
		builder.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					String txt = editT.getText().toString();
					if (null == txt || txt.equals("") || txt.length() <= 0) {
						ToastUtil.showToastShort(mContext, getString(R.string.act_task_group_input));
						Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, false);
					} else {
						// 判断是否有相同的组名,并给出相应的提示
						boolean flag = mInstance.existGroup(txt);
						if (flag) {
							ToastUtil.showToastShort(mContext, getString(R.string.act_task_group_same));
							return;
						}
						TaskGroupConfig taskGroupConfig = new TaskGroupConfig();
						taskGroupConfig.setGroupName(txt);
						mInstance.addGroup(taskGroupConfig);
						refreshGroupData();
						taskGroupAdapter.notifyDataSetChanged();
						Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, true);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).setNegativeButton(R.string.str_cancle).show();
	}
	/***
	 * 删除组
	 */
	private void deleGroup(SparseBooleanArray delMap) {
		if (delMap.size() <= 0) {
			ToastUtil.showToastShort(mContext, getString(R.string.main_group_select));
			return;
		}
		List<TaskGroupConfig> deleteGroups = new ArrayList<TaskGroupConfig>();
		for (int i = 0;i < delMap.size();i++) {
			int key = delMap.keyAt(i);
			if (delMap.get(key)) {
				mInstance.deleteGroup(groupLists.get(key).getGroupID());
				deleteGroups.add(groupLists.get(key));
			}
		}
		if (delMap.size() != 0) {
			groupLists.removeAll(deleteGroups);
			delMap.clear();
			taskGroupAdapter.notifyDataSetChanged();
		}
	}
	/***
	 * 全选反选组
	 *
	 */
	private void deSelectGropu() {
		if (refreshBtn.getText().equals(getString(R.string.str_checkall))) {
			refreshBtn.setText(getString(R.string.str_checknon));
			refreshBtn.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.controlbar_unallcheck), null, null);
			for (int i = 0; i < groupLists.size(); i++) {
				mapExist.put(i, true);
			}
		} else {
			refreshBtn.setText(getString(R.string.str_checkall));
			refreshBtn.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.controlbar_selectall), null, null);
			for (int i = 0; i < groupLists.size(); i++) {
				if (mapExist.get(i)) {
					mapExist.put(i, false);
				} else {
					mapExist.put(i, true);
				}
			}
		}
		List<TaskModel> lists = new LinkedList<TaskModel>();
		for (TaskGroupConfig group : groupLists) {
			lists.addAll(group.getTasks());
		}
		for (int i = 0;i < mapExist.size();i++) {
			int position = mapExist.keyAt(i);
            groupLists.get(position).setCheck(mapExist.get(position));
			for (TaskModel m : lists) {
				m.setCheck(mapExist.get(position));
			}
		}
		TaskListDispose.getInstance().writeXml();
		taskGroupAdapter.notifyDataSetChanged();
		// 取反
		isSelectAll = !isSelectAll;
	}
	@SuppressWarnings("deprecation")
	public void showMorePopView() {
		/*
		 * LinearLayout morePopView =
		 * (LinearLayout)findViewById(R.id.more_popup);
		 */
		if (morePopupWindow == null) {
			View morePopView = LayoutInflater.from(this).inflate(R.layout.task_more_pop, null);
			morePopupWindow = new PopupWindow(morePopView, (int) (150 * metric.density), LayoutParams.WRAP_CONTENT,
					true);
			RelativeLayout importLayout = (RelativeLayout) morePopView.findViewById(R.id.Import_rv);
			RelativeLayout exportLayout = (RelativeLayout) morePopView.findViewById(R.id.Export_rv);
			RelativeLayout historyLayout = (RelativeLayout) morePopView.findViewById(R.id.History_rv);
			RelativeLayout shareLayout = (RelativeLayout) morePopView.findViewById(R.id.Share_rv);
			importLayout.setOnClickListener(this);
			exportLayout.setOnClickListener(this);
			historyLayout.setOnClickListener(this);
			shareLayout.setOnClickListener(this);
			morePopupWindow.setFocusable(true);
			morePopupWindow.setTouchable(true);
			morePopupWindow.setBackgroundDrawable(new BitmapDrawable());
			// morePopupWindow.setAnimationStyle(R.style.popwin_anim_up_in_style);
			morePopupWindow.showAsDropDown(btnMore, -30, 10);
		} else {
			morePopupWindow.showAsDropDown(btnMore, -30, 10);
		}
	}
	@Override
	public void onStatusChange(ServerStatus status, String info) {
		Holder holder = new Holder();
		holder.status = status;
		holder.info = info;
		btuHandler.obtainMessage(10, holder).sendToTarget();
		holder = null;
	}
	private class Holder {
		ServerStatus status;
		String info;
	}
	private Handler btuHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Holder holder = (Holder) msg.obj;
			ServerStatus status = holder.status;
			String info = holder.info;
			switch (status) {
			case loginStart:
				showDialogMessage(getString(R.string.server_start_login));
				break;
			case loginSuccess:
				showDialogMessage(getString(R.string.server_login_success));
				break;
			case loginFail:
				ToastUtil.showToastShort(mContext, getString(R.string.server_login_fail) + ":" + info);
				closeDialog();
				break;
			case config:
				showDialogMessage(getString(R.string.server_config));
				break;
			case configNoNeed:
				closeDialog();
				showForceDialog();
				break;
			case configDLFail:
				ToastUtil.showToastShort(mContext, R.string.server_config_dl_fail);
				closeDialog();
				break;
			case configDLSuccess:
				showDialogMessage(getString(R.string.server_config_dl_success));
				refreshGroupData();
				taskGroupAdapter.notifyDataSetChanged();
				break;
			case configUpdateSuccess:
				ToastUtil.showToastShort(mContext, R.string.server_config_success);
				closeDialog();
				refreshGroupData();
				taskGroupAdapter.notifyDataSetChanged();
				break;
			case configUpdateFail:
				ToastUtil.showToastShort(mContext, R.string.server_config_fail);
				closeDialog();
				break;
			default:
				break;
			}
		}
		private void showDialogMessage(String msg) {
			if (progressDialog != null) {
				progressDialog.setMessage(msg);
			}
		}
		private void showForceDialog() {
			new BasicDialog.Builder(TaskGroupExpandListView.this).setTitle(R.string.str_tip)
					.setMessage(R.string.server_config_noNeed)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					downloadTask(true);
				}
			}).setNegativeButton(R.string.str_cancle).show();
		}
	};
	@Override
	public void onCompressProgress(String filename, int progress) {
	}
	@Override
	public void onItemClick(View v, int position) {
		if (!taskGroupAdapter.isDelModel()) {
			taskListDispose.setGroupID(groupLists.get(position).getGroupID());
			List<TaskGroupConfig> listGroups=taskListDispose.getTestPlanConfig().getTestSchemas().getTestSchemaConfig().getTaskGroups();
			for(TaskGroupConfig group:listGroups){
				if(group.getGroupID().equals(groupLists.get(position).getGroupID())){
					List<TaskModel> tasks=group.getTasks();
					ArrayList<TaskModel> taskList =new ArrayList<TaskModel>();
					taskList.addAll(tasks);
					taskListDispose.setTaskListArray(taskList);
					break;
				}
			}
			TaskGroupExpandListView.this.jumpActivity(Task.class);
		} else {
			SparseBooleanArray deleteMap = taskGroupAdapter.getDeleteMap();
			int count = 0;
			for (int i = 0;i < deleteMap.size();i++) {
				if (deleteMap.valueAt(i)) {
					count++;
				}
			}
			btnDelete.setEnabled(count > 0 ? true : false);
			btnDelete.setText(getResources().getString(R.string.delete) + "(" + count + ")");
		}
	}
	private class ConvertTask extends AsyncTask<Integer, Integer, Integer> {
		public static final int MODE_CONVERT = 0;
		public static final int MODE_SELECT = 1;
		private ArrayList<TestScheme> schemeList = null;// 测试方案列表
		BasicDialog dialog = null;
		int position;
		@Override
		protected void onPreExecute() {
			openDialog("", false);
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			position = params[0];
			int mode = params[1];
			if (schemeList == null) {
				if (mSerMgr.getUploadServer() == ServerManager.SERVER_BTU) {
					File file = new File(ServerManager.FILE_BTU_TASK);
					TaskConverter converter = new TaskConverter(mContext, file);
					schemeList = converter.convertTestScheme();
				} else {
					File file = new File(ServerManager.FILE_ATU_TASK);
					TaskConverter converter = new TaskConverter(mContext, file);
					schemeList = converter.convertTestScheme();
				}
			}
			if (mode == MODE_SELECT) {
				if (schemeList.size() > 0 && position >= 0 && position < schemeList.size()) {
					ArrayList<TaskModel> taskList = schemeList.get(position).getCommandList();
					// 替换测试任务模型列表到任务列表中
					TaskListDispose.getInstance().replaceTaskList(taskList);
					mSerMgr.setDTLogScheme(position);
				}
			}
			for (int i = 0; i < schemeList.size(); i++) {
				TestScheme scheme = schemeList.get(i);
				scheme.setUsing(i == position);
			}
			return mode;
		};
		@Override
		protected void onPostExecute(Integer mode) {
			closeDialog();
			if (mode == MODE_CONVERT) {
				showListDialog();
			} else if (mode == MODE_SELECT) {
				initView();
			}
		}
		private void showListDialog() {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View view = inflater.inflate(R.layout.listview, null);
			ListView listView = (ListView) view.findViewById(R.id.ListView0btu);
			SchemeAdapter adapter = new SchemeAdapter(schemeList, mContext);
			listView.setAdapter(adapter);
			listView.setSelection(position);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					dialog.dismiss();
					new ConvertTask().execute(position, MODE_SELECT);
				}
			});
			if (dialog == null) {
				dialog = new BasicDialog.Builder(mContext).setTitle(R.string.act_task_scheme).setView(view).create();
			}
			if (!dialog.isShowing()) {
				dialog.show();
			}
		}
	}
}
