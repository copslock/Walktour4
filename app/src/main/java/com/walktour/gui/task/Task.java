package com.walktour.gui.task;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.ShowInfoType;
import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.control.adapter.SchemeAdapter;
import com.walktour.control.adapter.TaskAdapter;
import com.walktour.control.config.ConfigAutoTest;
import com.walktour.control.config.PageManager;
import com.walktour.control.config.ProjectManager;
import com.walktour.control.config.ServerManager;
import com.walktour.customView.ListViewForScrollView;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.dragsortlistview.DragSortListView;
import com.walktour.gui.R;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.applet.MySimpleAdapter;
import com.walktour.gui.fleet.Fleet;
import com.walktour.gui.listener.ServerStatus;
import com.walktour.gui.listener.ServerStatusListener;
import com.walktour.gui.setting.sysroutine.SysRoutineActivity;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.TaskGroupConfig;
import com.walktour.gui.task.parsedata.model.TestPlanConfig;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.xml.btu.TaskConverter;
import com.walktour.gui.task.parsedata.xml.btu.model.TestScheme;
import com.walktour.gui.task.parsedata.xml.common.TaskXmlTools;
import com.walktour.gui.weifuwu.ShareDialogActivity;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
import com.walktour.gui.weifuwu.sharepush.ShareTaskActivity;
import com.walktour.gui.weifuwu.view.BadgeView;
import com.walktour.wifip2p.WiFiDirectActivity;



import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
/**
 * 测试任务列表 1.显示所有测试任务 2.对任务的操作：编辑，排序,删除 3.菜单：历史任务,任务设置,下载测试任务
 */
@SuppressLint({ "InflateParams", "HandlerLeak" })
public class Task extends BasicActivity implements ServerStatusListener {
	private static final String TAG = "Task";
	private Context mContext = null;
	private ApplicationModel appModel = ApplicationModel.getInstance();
	/** 保存 */
	public static final int TASK_SAVE = 0;
	/** 加载task */
	public static final int TASK_LOAD = 1;
	/** 任务列表 */
	private TaskListDispose taskListDispose;
	/** 任务列表listview */
	private DragSortListView taskListView;
	private TaskAdapter taskListAdapter;
	/** 新建任务的树形菜单 */
	private ListView list01;
	/** 底部工具栏 */
	private ControlBar ctrlBar;
	private Button btnNew;
	private Button btnSort;
	private Button btnSelectAll;
	private Button btnRemove;
	private Button btnMore;
	private Button btnDownload;
	private ProgressDialog progressDialog;
	/** 删除确定栏 */
	private LinearLayout deleteBar;
	private Button btnDelete;
	private Button btnCancle;
	/** 是否可选模式 */
	private boolean isCheckModel = false;
	private TextView txtAll;
	private CheckBox checkBox;
	private List<TaskModel> taskModelList;
	private List<Integer> taskDelList = new ArrayList<Integer>();
	/** 测试任务导出导入 */
	private ProjectManager proManager = null;
	/** 更多PopWindow */
	private PopupWindow morePopupWindow;
	/** 新建任务Popwindow */
	private PopupWindow treePopupWindow;
	private DisplayMetrics metric;
	// BTU相关
	private boolean isBtuMode = false;
	private ServerManager mSerMgr = null;
	private CheckBox checkBoxBtu = null;
	/** 是否来至工单界面 */
	private boolean isFromWorkOrder = false;
	private boolean isDropMode = false;
	public static final String PREFERENCE_NAME = "info";
	// 导入任务的加载模式 0-覆盖 1-追加
	private int loadModel = TaskXmlTools.LOADMODEL_APPEND;
	/** 分享接收按钮 **/
	private LinearLayout sharepushLayou;
	private Button nb;
	//按钮上显示文字
	private BadgeView badge1;
	private FileReceiver receiver=new FileReceiver();
	/** 参数存储 */
//	private SharedPreferences preferences;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
//		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		setContentView(R.layout.listview_with_treemenu);
		sharepushLayou = (LinearLayout) this.findViewById(R.id.sharepush);
		sharepushLayou.setVisibility(View.VISIBLE);
		sharepushLayou.findViewById(R.id.share).setOnClickListener(this);
		nb=(Button)sharepushLayou.findViewById(R.id.push);
		nb.setOnClickListener(this);
		badge1 = new BadgeView(this, nb);
	    badge1.setText("1");
	    badge1.setTextColor(getResources().getColor(R.color.white));
	    badge1.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
	    badge1.hide();
		initButtonNum();
		IntentFilter filter=new IntentFilter();
		filter.addAction(ShareCommons.SHARE_ACTION_3);
		this.registerReceiver(receiver, filter);
		sharepushLayou.setVisibility(View.VISIBLE);
		proManager = new ProjectManager(Task.this);
		mSerMgr = ServerManager.getInstance(this);
		mSerMgr.setServerStatusChangeListener(this);
		regeditBroadcast();
		metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		Intent intent = this.getIntent();
		this.isFromWorkOrder = intent.getBooleanExtra("fromWorkOrder", false);
		isBtuMode = mSerMgr.getUploadServer() == ServerManager.SERVER_BTU
				|| mSerMgr.getUploadServer() == ServerManager.SERVER_ATU;
	}
	@Override
	public void onResume() {
		super.onResume();
		this.findView();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			taskListDispose.getmTaskXmlTools().fetchGroup(taskListDispose.getGroupID()).getTasks().clear();
			taskListDispose.getmTaskXmlTools().fetchGroup(taskListDispose.getGroupID()).getTasks().addAll(taskListDispose.getTaskListArray());
			this.finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
		unregisterReceiver(mBroadcastReceiver);// 反注册事件监听
        mSerMgr.setServerStatusChangeListener(null);
	}


	private void findView() {
		// 设置列表
		taskListDispose = TaskListDispose.getInstance();
		findViewById(R.id.pointer).setOnClickListener(this);
		if (taskListView == null) {
			taskListView = (DragSortListView) findViewById(R.id.param_listview);
			taskListView.setDropListener(onDrop);
		}
		genTaskList();
		// 设置BTU标题
		int verSion = mSerMgr.getDTLogCVersion();
		if (isBtuMode) {
			genTitle4Btu(verSion);
		}
		if (verSion > 0) {
			genToolBar4Btu();
		} else {
			genToolBar();
		}
	}
	@SuppressWarnings("deprecation")
	private void setOperatorMode(boolean isDropMode) {
		if (isDropMode) {
			btnSort.setBackgroundColor(getResources().getColor(R.color.base_list_item_bg_select));
		} else {
			btnSort.setBackgroundDrawable(getResources().getDrawable(R.drawable.base_list_toolbar_bg));
		}
		taskListView.setDragEnabled(isDropMode);
		taskListAdapter.setDropMode(isDropMode);

//		if(!isDropMode){//表示排序完了
//			if(taskListView.getAdapter().getCount()>0){
//				List<TaskModel> taskList = new LinkedList<TaskModel>();
//				for(int i=0;i<taskListView.getAdapter().getCount();i++){
//					TaskModel model=(TaskModel)taskListView.getAdapter().getItem(i);
//					model.setTaskSequence(i);
//					taskList.add(model);
//				}
//				if(taskList.size()>0){
//					List<TaskGroupConfig> groups = TaskListDispose.getInstance().getTestPlanConfig().getTestSchemas().getTestSchemaConfig().getTaskGroups();
//					for (TaskGroupConfig group : groups) {
//						if (group.getGroupID().equals(TaskListDispose.getInstance().getGroupID())) {
//							group.getTasks().clear();
//							group.getTasks().addAll(taskList);
//						}
//					}
//					TaskListDispose.getInstance().writeXml();
//				}
//			}
//		}
	}
	/** 生成底部工具栏 */
	private void genToolBar() {
		ctrlBar = (ControlBar) findViewById(R.id.ControlBar);
		ctrlBar.setVisibility(appModel.isTestJobIsRun() ? View.GONE : View.VISIBLE);
		if (this.isFromWorkOrder)
			ctrlBar.setVisibility(View.GONE);
		txtAll = initTextView(R.id.TextViewAll);
		checkBox = (CheckBox) findViewById(R.id.CheckBoxAll);
		deleteBar = initLinearLayout(R.id.DeleteBar);
		btnDelete = initButton(R.id.ButtonDelete);
		btnCancle = initButton(R.id.ButtonCancle);
		// get button from bar
		btnNew = ctrlBar.getButton(0);
		btnSort = ctrlBar.getButton(1);
		btnDownload = ctrlBar.getButton(2);
		btnDownload.setWidth(400);
		btnSelectAll = ctrlBar.getButton(3);
		btnSelectAll.setVisibility(View.VISIBLE);
		btnRemove = ctrlBar.getButton(4);
		btnRemove.setVisibility(View.VISIBLE);
		btnMore = ctrlBar.getButton(5);
		btnMore.setVisibility(View.VISIBLE);
		// set text
		btnNew.setText(R.string.act_task_new);
		/*
		 * btnSave.setText( R.string.task_export); btnLoad.setText(
		 * R.string.task_import);
		 */
		btnSort.setText(R.string.monitor_data_order);
		btnSelectAll.setText(R.string.str_checkall);
		btnRemove.setText(R.string.delete);
		btnDownload.setText(R.string.download);
		btnMore.setText(R.string.str_more);
		btnNew.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_new),
				null, null);
		btnSort.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_muilt),
				null, null);
		btnSelectAll.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.controlbar_allcheck), null, null);
		btnRemove.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_clear),
				null, null);
		btnMore.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_more),
				null, null);
		btnDownload.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.controlbar_import), null, null);
		btnNew.setOnClickListener(btnListener);
		btnSort.setOnClickListener(btnListener);
		btnSelectAll.setOnClickListener(btnListener);
		btnRemove.setOnClickListener(btnListener);
		btnMore.setOnClickListener(btnListener);
		btnDownload.setOnClickListener(btnListener);
		btnDelete.setOnClickListener(btnListener);
		btnCancle.setOnClickListener(btnListener);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					checkAll();
				} else {
					checkNon();
				}
			}
		});
	}
	private void genToolBar4Btu() {
		ctrlBar = (ControlBar) findViewById(R.id.ControlBar);
		ctrlBar.setVisibility(appModel.isTestJobIsRun() ? View.GONE : View.VISIBLE);
		Button btnDown = ctrlBar.getButton(0);
		btnDown.setText(R.string.download);
		btnDown.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.controlbar_download), null, null);
		btnDown.setOnClickListener(btuBtnListener);
		Button btnScheme = ctrlBar.getButton(1);
		btnScheme.setText(R.string.act_task_scheme);
		btnScheme.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_muilt),
				null, null);
		btnScheme.setOnClickListener(btuBtnListener);
		ctrlBar.getButton(2).setVisibility(View.GONE);
		ctrlBar.getButton(3).setVisibility(View.GONE);
		ctrlBar.getButton(4).setVisibility(View.GONE);
		ctrlBar.getButton(5).setVisibility(View.GONE);
	}
	private void genTitle4Btu(int cVersion) {
		checkBoxBtu = (CheckBox) findViewById(R.id.CheckBoxBtu);
		if (WalktourApplication.isExitGroup()) {
			// 分组时此按钮隐藏，组界面会显示
			checkBoxBtu.setVisibility(View.GONE);
		} else {
			checkBoxBtu.setVisibility(View.VISIBLE);
		}
		checkBoxBtu.setChecked(cVersion > 0);
		checkBoxBtu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int messageID = -1;
				if (checkBoxBtu.isChecked()) {
					if (mSerMgr.getUploadServer() == ServerManager.SERVER_BTU) {
						messageID = R.string.act_task_btu_check;
					} else {
						messageID = R.string.act_task_atu_check;
					}
					new AlertDialog.Builder(mContext).setTitle(R.string.str_tip).setMessage(messageID)
							.setCancelable(false)
							.setPositiveButton(R.string.str_return, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							checkBoxBtu.setChecked(false);
							genToolBar();
						}
					}).show();
				} else {
					if (mSerMgr.getUploadServer() == ServerManager.SERVER_BTU) {
						messageID = R.string.act_task_btu_edit;
					} else {
						messageID = R.string.act_task_atu_edit;
					}
					new AlertDialog.Builder(mContext).setTitle(R.string.str_tip).setMessage(messageID)
							.setCancelable(false)
							.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 重置测试计划版本号
							mSerMgr.setDTLogCVersion(0);
							// 清除所有业务
							String defautID = TaskListDispose.getInstance().getDefaultGroupId();
							List<TaskGroupConfig> groups = TaskListDispose.getInstance().getCurrentGroups();
							Iterator<TaskGroupConfig> it = groups.iterator();
							while (it.hasNext()) {
								TaskGroupConfig value = it.next();
								if (value.getGroupID().equals(defautID)) {
									it.remove();
								}
							}
							genToolBar();
							findView();
						}
					}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							checkBoxBtu.setChecked(true);
						}
					}).show();
				}
			}
		});
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
	/** 工具栏点击事件 */
	private OnClickListener btnListener = new OnClickListener() {
		public void onClick(View view) {
			if (view.getId() != R.id.Button02) {// 点击排序以外的按钮取消dropMode模式
				dimissDropMode();
			}
			switch (view.getId()) {
			case R.id.Button01: // 新建任务
				showNewPopView();
				dimissDropMode();
				break;
			case R.id.Button02:// 排序
				isDropMode = isDropMode ? false : true;
				setOperatorMode(isDropMode);
				break;
			case R.id.Button03:// 下载任务
				downloadTask(false);
				break;
			case R.id.Button04:// 全选
				if (btnSelectAll.getText().equals(getResources().getString(R.string.str_checkall))) {
					btnSelectAll.setText(getResources().getString(R.string.str_checknon));
					checkAllTestTask();
				} else {
					btnSelectAll.setText(getResources().getString(R.string.str_checkall));
					checkNOTTestTask();
				}
				break;
			case R.id.Button05:// 删除
				sharepushLayou.setVisibility(View.GONE);
				if (checkBoxBtu != null){
					checkBoxBtu.setVisibility(View.GONE);
				}
				displayCheck();
				break;
			case R.id.Button06:// 更多任务
				showMorePopView();
				break;
			case R.id.ButtonDelete:// 删除
				removeTask();
				break;
			case R.id.ButtonCancle:// 取消删除
				dimissCheck();
				if (checkBoxBtu != null){
					checkBoxBtu.setVisibility(View.VISIBLE);
				}
				if(WalktourApplication.isExitGroup()){
					//是否分组,分组则里面的分享按钮隐藏
					sharepushLayou.setVisibility(View.GONE);
				}else{
					sharepushLayou.setVisibility(View.VISIBLE);
				}
				break;
			}
		}
	};
	/** 生成任务列表 */
	private void genTaskList() {
		// 绑定Layout里面的ListView
		taskModelList = taskListDispose
				.getCurrentTaskList(this.isFromWorkOrder ? TaskModel.FROM_TYPE_DOWNLOAD : TaskModel.FROM_TYPE_SELF);
		taskListAdapter = new TaskAdapter(taskModelList, taskDelList, isCheckModel, isFromWorkOrder, this);
		taskListView.setAdapter(taskListAdapter);
		// 添加点击 icon_event_1
		taskListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				if (isCheckModel) {
					ImageView ItemCheckble = (ImageView) view.findViewById(R.id.ItemCheckble);
					if (!taskDelList.contains(position)) {
						ItemCheckble.setImageResource(R.drawable.btn_check_on);
						taskDelList.add(position);
						// checkDelCount++;
					} else {
						ItemCheckble.setImageResource(R.drawable.btn_check_off);
						taskDelList.remove((Integer)position);
						// checkDelCount--;
					}
					if (taskDelList.size() > 0) {
						btnDelete.setEnabled(true);
						btnDelete.setTextColor(getResources().getColor(R.color.app_main_text_color));
						btnDelete.setText(getString(R.string.delete) + "(" + taskDelList.size() + ")");
					} else {
						btnDelete.setEnabled(false);
						btnDelete.setText(getString(R.string.delete));
						btnDelete.setTextColor(getResources().getColor(R.color.gray));
					}
				} else {
					//注意这个position需要计算为实际的position
					edit(getTaskID(taskModelList,position));
				}
			}
		});
		if (mSerMgr.getDTLogCVersion() == 0) {
			taskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
					if (appModel.isTestJobIsRun()) {
						return false;
					}
					BasicDialog.Builder builder = new BasicDialog.Builder(Task.this);
					String[] items = new String[] { getResources().getString(R.string.edit),
							getResources().getString(R.string.up), getResources().getString(R.string.down),
							getResources().getString(R.string.delete),
							getResources().getString(R.string.delete_all) };
					builder.setItems(items, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							// 编辑
							case 0:
								//注意这个position需要计算为实际的position
								edit(getTaskID(taskModelList,position));
								break;
							// 上移
							case 1:
								taskListDispose.move(1, position);
								break;
							// 下移
							case 2:
								taskListDispose.move(2, position);
								break;
							// 删除
							case 3:
								taskListDispose.move(3, position);
								break;
							// 删除全部
							case 4:
								taskListDispose.move(4, position);
								break;
							default:
								break;
							}
							taskModelList.clear();
							taskModelList.addAll(taskListDispose.getCurrentTaskList(
									isFromWorkOrder ? TaskModel.FROM_TYPE_DOWNLOAD : TaskModel.FROM_TYPE_SELF));
							taskListAdapter.notifyDataSetChanged();
						}
					});
					builder.show();
					return true;
				}
			});

		}
	}

	/***
	 * 获取实际的taskID
	 * @param taskModelList
	 * @param postion
	 * @return
	 */
	private String getTaskID(List<TaskModel> taskModelList,int postion){
		//需要从position计算出实际的position
		int index=0;
		for(int i=0;i<taskModelList.size();i++){
			if(taskModelList.get(i).getEnable()!=TaskModel.TASKSTATUS_0){
				if(index==postion){
					return taskModelList.get(i).getTaskID();
				}
				index++;
			}
		}

		return "";
	}
	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				TaskModel item = (TaskModel) taskListAdapter.getItem(from);
				taskListAdapter.remove(item);
				taskListAdapter.insert(item, to);
				taskListView.moveCheckState(from, to);
				taskListAdapter.notifyDataSetChanged();
				List<TaskModel> list = taskListAdapter.getTaskModelList();
				for (int i=0;i<list.size();i++){//重新排列任务序号
					list.get(i).setTaskSequence(i);
				}
				taskListDispose.updateTaskListXmlFile(list);
			}
		}
	};
	/**
	 * 显示More Pop<BR>
	 * 点击More弹出POP展示可选项
	 */
	@SuppressWarnings("deprecation")
	public void showMorePopView() {
		if (morePopupWindow == null) {
			View morePopView = LayoutInflater.from(this).inflate(R.layout.task_more_pop, null);
			morePopupWindow = new PopupWindow(morePopView, (int) (150 * metric.density),(int) (210 * metric.density),
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
			morePopupWindow.showAsDropDown(btnMore, 10, 10);
		} else {
			morePopupWindow.showAsDropDown(btnMore, 10, 10);
		}
	}
	@SuppressWarnings("deprecation")
	public void showNewPopView() {
		if (treePopupWindow == null) {
			View treePopView = LayoutInflater.from(this).inflate(R.layout.task_tree_menu, null);
			list01 = (ListView) treePopView.findViewById(R.id.ListView02);
			list01.setVisibility(View.VISIBLE);
			list01.setFocusable(true);
			list01.requestFocus();
			list01.setFocusableInTouchMode(true);
			// 所有业务权限
			ArrayList<WalkStruct.TaskType> taskFromLicense = ApplicationModel.getInstance().getTaskList();
			if(!taskFromLicense.contains(TaskType.REBOOT)) {
				taskFromLicense.add(TaskType.REBOOT);
			}

			// 添加有权限的业务到列表中显示
			final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
			ArrayList<ShowInfoType> showInfoType = new PageManager(getApplicationContext(), false).getShowInfoList();
			for (int i = 0; i < taskFromLicense.size(); i++) {
				TaskType taskType = taskFromLicense.get(i);
				//2017-9-4添加如果存在NB模块业务测试，只保留PingTest,Ipref,UDP测试
				if(appModel.isNBTest()){
					if(taskType!= TaskType.Ping
							&&taskType!= TaskType.UDP
							&&taskType!= TaskType.Attach
							&&taskType!= TaskType.FTPDownload
							&&taskType!= TaskType.FTPUpload
							&&taskType!= TaskType.EmptyTask
//							&&taskType!= TaskType.Iperf
							
							){
						continue;
					}
				}else{//非NB测试屏蔽UDP
					if(taskType== TaskType.UDP){
						continue;
					}
				}
				if (!showInfoType.contains(WalkStruct.ShowInfoType.WLAN)) {
					// 如果没有WLan的权限,过滤掉WlanAP,EteAuth,Login
					switch(taskType){
					case WlanLogin:
					case WlanAP:
					case WlanEteAuth:
						continue;
					default:
						break;
					}
				}
				/**
				 * @data 2019/1/2
				 * 需要屏蔽的业务：
				 * WapDownload:
				  WapLogin:
				  WapRefurbish:
				 硬编码屏蔽的业务
				 */
				switch (taskType){
					case EmailSmtpAndPOP:
					case VOIP:
					case Stream:
					case WapDownload:
					case WapLogin:
					case WapRefurbish:
					case InitiativeVideoCall:
					case PassivityVideoCall:
						continue;
				}
				//通用版过滤以下需要root权限的业务
				if(ApplicationModel.getInstance().isGeneralMode()){
					switch (taskType) {
					case Stream:
					case Ping:
					case UDP:
					case SpeedTest:
					case TraceRoute:
					case Iperf:
					case PBM:
					case Attach:
					case PDP:
					case InitiativeVideoCall:
					case PassivityVideoCall:
					case WeiBo:
					case DNSLookUp:
					case Facebook:
					case WeChat:
						continue;
					default:
						break;
					}
				}
				if(android.os.Build.VERSION.SDK_INT > 22){
					if(ApplicationModel.getInstance().isGeneralMode()){
						switch (taskType) {
						case PassivityCall:
							continue;
						default:
							break;
						}
					}
				}
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemTitle", taskType.getShowName(Task.this));
				map.put("ItemKey", taskType);
				if (!taskType.getShowName(Task.this).equals("unknown")) {
					if (!taskType.name().equals(WalkStruct.TaskType.HttpRefurbish.name())) {
						listItem.add(map);
					}
				}
			}

			// 生成适配器的Item和动态数组对应的元素
			final SimpleAdapter adapter = new SimpleAdapter(this, listItem, // ListItem的数据源
																			// `1
					R.layout.listview_item_style6, // ListItem的XML实现
					// 动态数组与ImageItem对应的子项
					new String[] { "ItemTitle" }, new int[] { R.id.ItemTitle });
			// 添加并且显示
			list01.setAdapter(adapter);
			// list02添加点击 icon_event_1
			list01.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					treePopupWindow.dismiss();
					TaskType taskType = (TaskType) listItem.get(arg2).get("ItemKey");
					Intent intent = getIntentByTaskType(taskType);
					if (intent != null) {
						startActivity(intent);
						overridePendingTransition(R.anim.slide_in_right_activity, 0);
					}
				}
			});
			// 设置选择
			list01.setSelected(true);
			treePopupWindow = new PopupWindow(treePopView, (int) (170 * metric.density), (int) (350 * metric.density),
					true);
			treePopupWindow.setFocusable(true);
			treePopupWindow.setTouchable(true);
			treePopupWindow.setBackgroundDrawable(new BitmapDrawable());
			treePopupWindow.showAsDropDown(btnNew, 10, 10);
		} else {
			treePopupWindow.showAsDropDown(btnNew, 10, 10);
		}
	}
	private void downloadTask(boolean force) {
		ServerManager sm = ServerManager.getInstance(Task.this);
		// 如果有自动测试权限并且自动测试已经开启，给提示
		if (ApplicationModel.getInstance().getAppList().contains(WalkStruct.AppType.AutomatismTest)
				&& new ConfigAutoTest().isAutoTestOn()) {
			showAutoTestOnDialog();
		} else {
			if (sm.hasDownloadServerSet()) {
				showProgressDialog("", true);
				sm.downloadTestTask(force);
			} else {
				new BasicDialog.Builder(Task.this).setTitle(R.string.task_alertSetServer)
						.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent(Task.this, SysRoutineActivity.class);
								intent.setAction(SysRoutineActivity.SHOW_DATA_UPLOAD_TAB);
								startActivity(intent);
							}
						}).show();
			}
		}
	}
	private void showProgressDialog(String message, boolean cancleable) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(cancleable);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}
	private void dismissProgress() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	private void showAutoTestOnDialog() {
		new BasicDialog.Builder(Task.this).setTitle(R.string.str_tip).setMessage(R.string.main_autoison)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(Task.this, Fleet.class));
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}
	private void showMyDialog(int id, String title) {
		final LayoutInflater factory = LayoutInflater.from(Task.this);
		switch (id) {
		// 导出测试任务
		case TASK_SAVE:
			final View textEntryView = factory.inflate(R.layout.alert_dialog_edittext1, null);
			final EditText taskName = (EditText) textEntryView.findViewById(R.id.alert_textEditText);
			taskName.setText(getTaskListNames());
			new BasicDialog.Builder(Task.this).setView(textEntryView).setTitle(title)
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
								final TestPlanConfig config = TaskListDispose.getInstance().getTestPlanConfig();
								String groupID = TaskListDispose.getInstance().getGroupID();
								Iterator<TaskGroupConfig> it = config.getTestSchemas().getTestSchemaConfig()
										.getTaskGroups().iterator();
								List<TaskGroupConfig> groups = new LinkedList<>();
								while (it.hasNext()) {
									TaskGroupConfig value = it.next();
									if (!value.getGroupID().equals(groupID)) {
										groups.add(value);
										it.remove();
									}
								}
								proManager.saveTask(config, taskName.getText().toString());
								// 删除掉的对象还原回来
								config.getTestSchemas().getTestSchemaConfig().getTaskGroups().addAll(groups);
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
			final ArrayList<File> taskFileList = proManager.getTaskFileList(false);
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
			final BasicDialog alertDialog = new BasicDialog.Builder(Task.this).setTitle(title).setView(viewLoad)
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
						TestPlanConfig config = proManager.loadTask(taskFileList.get(arg2));
						if (null == config) {
							ToastUtil.showToastShort(mContext, getString(R.string.task_import_task_error));
						} else {
							List<TaskGroupConfig> groups=config.getTestSchemas().getTestSchemaConfig().getTaskGroups();
							if(null!=groups&&groups.size()>0){
								//直接取第一个就行，任务内的xml数据只有一个测试任务组
								TaskGroupConfig group = groups.get(0);
								if (loadModel == TaskXmlTools.LOADMODEL_APPEND) {// 追加覆盖
									for (TaskModel m1 : group.getTasks()) {
										boolean exist = false;
										for (TaskModel m2 : TaskListDispose.getInstance().getTaskListArray()) {
											if (m1.getTaskID().equals(m2.getTaskID())) {
												exist = true;
											}
										}
										if (!exist) {
											TaskListDispose.getInstance().addTask(m1);
											taskModelList.add(m1);
										}

									}
								} else {
									List<TaskGroupConfig> tgcs = TaskListDispose.getInstance().getTestPlanConfig().getTestSchemas()
											.getTestSchemaConfig().getTaskGroups();
									for (TaskGroupConfig g : tgcs) {
										if (g.getGroupID().equals(TaskListDispose.getInstance().getGroupID())) {
											g.getTasks().clear();
											g.getTasks().addAll(group.getTasks());
											taskModelList.clear();
											taskModelList.addAll(group.getTasks());
											ArrayList<TaskModel> lx=new ArrayList<>();
											for(TaskModel m:group.getTasks()){
												lx.add(m);
											}
											TaskListDispose.getInstance().setTaskListArray(lx);
											break;
										}
									}
								}
								findView();
							}
						}
					} else {
						ToastUtil.showToastShort(mContext, getString(R.string.sdcard_non));
					}
					alertDialog.dismiss();
				}
				/**
				 * 显示测试任务Dialog
				 */
//				private void showGroupDialog(final List<TaskGroupConfig> groups) {
//					final View viewLoad = factory.inflate(R.layout.alert_dialog_listview_task, null);
//					ListView listView = (ListView) viewLoad.findViewById(R.id.ListView);
//					ArrayList<HashMap<String, Object>> itemArrayList = new ArrayList<HashMap<String, Object>>();// checkListItemAdapter的参数
//					for (TaskGroupConfig group : groups) {
//						HashMap<String, Object> map = new HashMap<String, Object>();
//						map.put("ItemTitle", group.getGroupName());
//						itemArrayList.add(map);
//					}
//					MySimpleAdapter checkListItemAdapter = new MySimpleAdapter(Task.this, itemArrayList,
//							R.layout.listview_item_style6, new String[] { "ItemTitle" }, new int[] { R.id.ItemTitle });
//					listView.setAdapter(checkListItemAdapter);// listView用到的Adapter
//					final BasicDialog alertDialog = new BasicDialog.Builder(Task.this)
//							.setTitle(getString(R.string.total_task)).setView(viewLoad)
//							.setPositiveButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//						}
//					}).create();
//					listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//						@Override
//						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//							TaskGroupConfig group = groups.get(arg2);
//							if (loadModel == TaskListDispose.LOADMODEL_APPEND) {// 追加覆盖
//								for (TaskModel m1 : group.getTasks()) {
//									boolean exist = false;
//									for (TaskModel m2 : taskModelList) {
//										if (m1.getTaskID().equals(m2.getTaskID())) {
//											exist = true;
//										}
//									}
//									if (!exist) {
//										m1.setTaskSequence(TaskListDispose.getInstance()
//												.getCurrentTaskSequence(group.getGroupID()));
//										TaskListDispose.getInstance().getTestPlanConfig().getTestSchemas()
//												.getTestSchemaConfig().getTaskGroups().get(arg2).getTasks().add(m1);
//										TaskListDispose.getInstance().writeXml();
//									}
//								}
//							} else {
//								taskModelList.clear();
//								taskModelList.addAll(group.getTasks());
//							}
//							findView();
//							alertDialog.dismiss();
//						}
//					});
//					alertDialog.show();
//				}
			});
			alertDialog.show();
			break;
		}
	}
	/**
	 * 广播接收器:接收来广播更新界面
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(WalkMessage.NOTIFY_TESTJOBDONE)
					|| intent.getAction().equals(WalkMessage.NOTIFY_INTERRUPTJOBDONE)) {
				findView();
			}
		}
	};
	/**
	 * 注册广播接收器
	 */
	protected void regeditBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(WalkMessage.NOTIFY_TESTJOBDONE);
		filter.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE);
		this.registerReceiver(mBroadcastReceiver, filter);
	}
	/**
	 * 全选
	 */
	private void checkAll() {
		taskDelList.clear();
		for (int i = 0; i < taskModelList.size(); i++) {
			taskDelList.add(i);
		}
		taskListAdapter.notifyDataSetChanged(isCheckModel);
		btnDelete.setEnabled(true);
		btnDelete.setTextColor(getResources().getColor(R.color.app_main_text_color));
		btnDelete.setText(getString(R.string.delete) + "(" + taskModelList.size() + ")");
	}
	/**
	 * 全不选
	 */
	private void checkNon() {
		taskDelList.clear();
		taskListAdapter.notifyDataSetChanged(isCheckModel);
		btnDelete.setEnabled(false);
		btnDelete.setText(getString(R.string.delete));
		btnDelete.setTextColor(getResources().getColor(R.color.gray));
	}
	/**
	 * 全选测试任务
	 */
	private void checkAllTestTask() {
		if (taskModelList.size() == 0) {
			return;
		}
		for (int i = 0; i < taskModelList.size(); i++) {
			taskModelList.get(i).setEnable(1);
		}
		TaskListDispose.getInstance().isTestTasks(taskModelList.get(0).getTaskID());
		taskListAdapter.notifyDataSetChanged(isCheckModel);
	}
	/**
	 * 全不选任务
	 */
	private void checkNOTTestTask() {
		if (taskModelList.size() == 0) {
			return;
		}
		for (int i = 0; i < taskModelList.size(); i++) {
			taskModelList.get(i).setEnable(2);
		}
		TaskListDispose.getInstance().isTestTasks(taskModelList.get(0).getTaskID());
		taskListAdapter.notifyDataSetChanged(isCheckModel);
	}
	/**
	 * 取消拖动排序模式
	 */
	private void dimissDropMode() {
		isDropMode = false;
		setOperatorMode(isDropMode);
	}
	/**
	 * 显示可选模式
	 */
	private void displayCheck() {
		isCheckModel = true;
		/*
		 * for( int i=0;i<taskModelList.size();i++ ){ taskDelList.add(i);
		 * //taskListItemMap.get( i ).put("ItemCheckble",
		 * R.drawable.btn_check_off ); }
		 */
		taskDelList.clear();
		btnDelete.setText(getString(R.string.delete));
		checkBox.setVisibility(View.VISIBLE);
		checkBox.setChecked(false);
		txtAll.setVisibility(View.VISIBLE);
		taskListAdapter.notifyDataSetChanged(isCheckModel);
		taskListView.invalidateViews();
		findViewById(R.id.control_btn_layout).setVisibility(View.VISIBLE);
		deleteBar.setVisibility(View.VISIBLE);
		btnDelete.setEnabled(false);
		btnDelete.setTextColor(getResources().getColor(R.color.gray));
	}
	/** 取消可选模式 */
	private void dimissCheck() {
		isCheckModel = false;
		/*
		 * for( int i=0;i<taskListItemMap.size();i++ ){ taskListItemMap.get( i
		 * ).put("ItemCheckble", R.drawable.empty ); }
		 */
		taskDelList.clear();
		deleteBar.setVisibility(View.INVISIBLE);
		checkBox.setVisibility(View.INVISIBLE);
		txtAll.setVisibility(View.INVISIBLE);
		taskListAdapter.notifyDataSetChanged(isCheckModel);
		findViewById(R.id.control_btn_layout).setVisibility(View.GONE);
	}
	/**
	 * 删除选中的任务
	 */
	private void removeTask() {
		ArrayList<String> deleteList = new ArrayList<String>();
		ArrayList<TaskModel> delTasks = new ArrayList<TaskModel>();
		for (int i = taskDelList.size() - 1; i >= 0; i--) {
			deleteList.add(taskModelList.get(taskDelList.get(i)).getTaskID());
			delTasks.add(taskModelList.get(taskDelList.get(i)));
		}
		for (int i = delTasks.size() - 1; i >= 0; i--) {
			for (int j = 0; j < taskModelList.size(); j++) {
				if(taskModelList.get(j).getTaskID() == delTasks.get(i).getTaskID()){
					taskModelList.remove(j);
					break;
				}
			}
		}
//		taskModelList.removeAll(delTasks);
		// 取消可选模式
		dimissCheck();
		// 修改xml(放入历史任务)
		TaskListDispose.getInstance().removeTasks(deleteList);
		taskListAdapter.notifyDataSetChanged(isCheckModel);
	}
	/**
	 * 编辑单个任务
	 *
	 * @param taskID
	 *            任务名
	 */
	private void edit(String taskID) {
		int position = -1;
		WalkStruct.TaskType taskType = null;
		List<TaskModel> allList = taskListDispose.getTaskListArray();//.getCurrentTaskList(isFromWorkOrder ? TaskModel.FROM_TYPE_DOWNLOAD : TaskModel.FROM_TYPE_SELF);
		for (int i = 0; i < allList.size(); i++) {
			TaskModel taskModel = allList.get(i);
			if (taskModel.getTaskID().equals(taskID)) {
				position = i;
				taskType = WalkStruct.TaskType.valueOf(taskModel.getTaskType());
				break;
			}
		}
		if (position == -1) {
			return;
		}
		Intent intent = getIntentByTaskType(taskType);
		if (intent != null) {
			Bundle bundle = new Bundle();
			bundle.putInt("taskListId", position);
			intent.putExtra("fromWorkOrder", this.isFromWorkOrder);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	private Intent getIntentByTaskType(TaskType taskType) {
		Intent intent = null;
		switch (taskType) {
			case EmptyTask:
				intent = new Intent(this, TaskEmpty.class);
				break;
			case InitiativeCall:
				intent = new Intent(this, TaskMOCCall.class);
				break;
			case PassivityCall:
				intent = new Intent(this, TaskMTCCall.class);
				break;
			case InitiativeVideoCall:
				intent = new Intent(this, TaskInitiativeVideoCall.class);
				break;
			case PassivityVideoCall:
				intent = new Intent(this, TaskPassivityVideoCall.class);
				break;
			case Ping:
				intent = new Intent(this, TaskPing.class);
				break;
			case Attach:
				intent = new Intent(this, TaskAttach.class);
				break;
			case PDP:
				intent = new Intent(this, TaskPDP.class);
				break;
			case FTPUpload:
				intent = new Intent(this, TaskFtpUpload.class);
				break;
			case FTPDownload:
				intent = new Intent(this, TaskFtpDownload.class);
				break;
			case Http:
				intent = new Intent(this, TaskHttpPage.class);
				break;
			case HttpRefurbish:
				intent = new Intent(this, TaskHttpPage.class);
				break;
			case HttpDownload:
				intent = new Intent(this, TaskHttpDownload.class);
				break;
			case EmailPop3:
				intent = new Intent(this, TaskEmailPop3.class);
				break;
			case EmailSmtp:
				intent = new Intent(this, TaskEmailSmtp.class);
				break;
			case SMSIncept:
				intent = new Intent(this, TaskSmsIncept.class);
				break;
			case SMSSend:
				intent = new Intent(this, TaskSmsSend.class);
				break;
			case SMSSendReceive:
				intent = new Intent(this, TaskSmsSendReceive.class);
				break;
			case MMSIncept:
				intent = new Intent(this, TaskMmsReceive.class);
				break;
			case MMSSend:
				intent = new Intent(this, TaskMmsSend.class);
				break;
			case MMSSendReceive:
				intent = new Intent(this, TaskMmsSendReceive.class);
				break;
			case WapLogin:
				intent = new Intent(this, TaskWapLogin.class);
				break;
			case WapRefurbish:
				intent = new Intent(this, TaskWapRefurbish.class);
				break;
			case WapDownload:
				intent = new Intent(this, TaskWapDownload.class);
				break;
			case EmailSmtpAndPOP:
				intent = new Intent(this, TaskEmailSmtpAndPop3.class);
				break;
			case Stream: // 流媒体处理 Jihong Xie 2012-07-19
				intent = new Intent(this, TaskStreaming.class);
				break;
			case VOIP: // VoIP处理
				intent = new Intent(this, TaskVoIP.class);
				break;
			case MultiRAB: // 并发业务
				intent = new Intent(this, TaskMultiRAB.class);
				break;
			case DNSLookUp:// DNS
				intent = new Intent(this, TaskDNSLookup.class);
				break;
			case SpeedTest:// Speed Test Jihong.Xie
				intent = new Intent(this, TaskSpeedTest.class);
				break;
			case HttpUpload:
				intent = new Intent(this, TaskHttpUpload.class);
				break;
			case HTTPVS:
				intent = new Intent(this, TaskVideoPlay.class);
				break;
			case MultiftpUpload:
				intent = new Intent(this, TaskMultiFTPUpload.class);
				break;
			case MultiftpDownload:
				intent = new Intent(this, TaskMultiFTPDownload.class);
				break;
			case Facebook:
				intent = new Intent(this, TaskFacebookActivity.class);
				break;
			case TraceRoute:
				intent = new Intent(this, TaskTraceRouteActivity.class);
				break;
			case Iperf:
				intent = new Intent(this, TaskIperfActivity.class);
				break;
			case PBM:
				intent = new Intent(this, TaskPBMActivity.class);
				break;
			case WeiBo:
				intent = new Intent(this, TaskWeiBoActivity.class);
				break;
			case WlanAP:
				intent = new Intent(this, TaskWlanAp.class);
				break;
			case WlanEteAuth:
				intent = new Intent(this, TaskWlanEteAuth.class);
				break;
			case WlanLogin:
				intent = new Intent(this, TaskWlanLogin.class);
				break;
			case WeChat:
//				intent = new Intent(this, TaskWeChatActivity.class);
				intent = new Intent(this, TaskOttWeChatActivity.class);
				break;
			case UDP:
				intent = new Intent(this, TaskUDPActivity.class);
				break;
			case REBOOT:
				intent = new Intent(this, TaskReboot.class);
				break;
			case OpenSignal:
				intent = new Intent(this, TaskOpenSignalActivity.class);
				break;
			case MultiHttpDownload:
				intent = new Intent(this, TaskMultiHttpDownActivity.class);
				break;
			case WeCallMoc:
				intent = new Intent(this, TaskOttWxMocActivity.class);
				break;
			case WeCallMtc:
                intent = new Intent(this, TaskOttWxMtcActivity.class);
			    break;
			case SkypeChat:
                intent = new Intent(this, TaskOttSkypeActivity.class);
			    break;
            case SinaWeibo:
                intent = new Intent(this, TaskOttSinaActivity.class);
                break;
            case QQ:
                intent = new Intent(this, TaskOttQQActivity.class);
                break;
            case WhatsAppChat:
                intent = new Intent(this, TaskOttWhatsAppActivity.class);
                break;
            case WhatsAppMoc:
                intent = new Intent(this, TaskOttWhatsAppMocActivity.class);
                break;
            case WhatsAppMtc:
                intent = new Intent(this, TaskOttWhatsAppMtcActivity.class);
                break;
            case Facebook_Ott:
                intent = new Intent(this, TaskOttFacebookActivity.class);
                break;
            case Instagram_Ott:
                intent = new Intent(this, TaskOttInstagramActivity.class);
                break;
            default:
				break;
		}
		return intent;
	}
	/**
	 * 导出任务时从列表中获取要保存的列表名
	 */
	private String getTaskListNames() {
		ArrayList<TaskModel> modelList = new ArrayList<TaskModel>();
		StringBuffer name = new StringBuffer();
		List<TaskGroupConfig> groups = taskListDispose.getTestPlanConfig().getTestSchemas().getTestSchemaConfig()
				.getTaskGroups();
		for (TaskGroupConfig group : groups) {
			modelList.addAll(group.getTasks());
		}
		String[] task_key = getResources().getStringArray(R.array.task_key);
		String[] task_value = getResources().getStringArray(R.array.task_beta1);
		for (int t = 0; t < modelList.size(); t++) {
			String taskType = modelList.get(t).getTaskType();
			for (int i = 0; i < task_key.length; i++) {
				if (taskType.equals(task_key[i])) {
					if (t > 0) {
						name.append("_");
					}
					name.append(task_value[i]);
				}
			}
		}
		return name.toString();
	}
	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 *
	 * @param v
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
			} else if (taskListDispose
					.getCurrentTaskList(this.isFromWorkOrder ? TaskModel.FROM_TYPE_DOWNLOAD : TaskModel.FROM_TYPE_SELF)
					.size() == 0) {
				ToastUtil.showToastShort(mContext, R.string.task_alert_listEmpty);
			} else {
				showMyDialog(TASK_SAVE, getString(R.string.task_export_task));
			}
			morePopupWindow.dismiss();
			break;
		case R.id.History_rv:
			Bundle bundle = new Bundle();
			bundle.putBoolean("isgroup", false);
			this.jumpActivity(TaskHistory.class, bundle);
			morePopupWindow.dismiss();
			break;
		case R.id.Share_rv: {
			if (android.os.Build.VERSION.SDK_INT < 14) {
				ToastUtil.showToastShort(mContext, R.string.file_share_version_low);
			} else {
				String filename = "Task" + UtilsMethod.getSimpleDateFormat7(System.currentTimeMillis());
				proManager.saveTask(filename,false);
				Intent intent = new Intent(Task.this, WiFiDirectActivity.class);
				intent.putExtra("filename", filename + ".xml");
				startActivity(intent);
			}
			morePopupWindow.dismiss();
		}
			break;
		case R.id.pointer:
			Task.this.finish();
			break;
		case R.id.share:// 分享
			bundle=new Bundle();
			bundle.putInt("from",2);
			this.jumpActivity(ShareTaskActivity.class,bundle);
			break;
		case R.id.push:// 接收
			try {
				// 无新的共享信息,给出提示
				List<ShareFileModel> lists = ShareDataBase.getInstance(mContext).fetchAllFilesByFileStatusAndFileType(
						ShareFileModel.FILETYPE_TASK, new int[]{ShareFileModel.FILE_STATUS_INIT,ShareFileModel.FILE_STATUS_START,ShareFileModel.FILE_STATUS_ONGOING});
				if (lists.size() <= 0) {
					ToastUtil.showToastShort(Task.this, R.string.share_project_share_info_obj_receive);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 跳转到接收dialog
			Bundle bundle2 = new Bundle();
			bundle2.putInt("fileType", ShareFileModel.FILETYPE_TASK);
			this.jumpActivityForResult(ShareDialogActivity.class, bundle2, 0);
			break;
		default:
			break;
		}
	}
	@Override
	public void onCompressProgress(String filename, int progress) {
	}
	@Override
	public void onStatusChange(ServerStatus status, String info) {
		Holder holder = new Holder();
		holder.status = status;
		holder.info = info;
		btuHandler.obtainMessage(0, holder).sendToTarget();
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
				dismissProgress();
				break;
			case config:
				showDialogMessage(getString(R.string.server_config));
				break;
			case configNoNeed:
				dismissProgress();
				showForceDialog();
				break;
			case configDLFail:
				ToastUtil.showToastShort(mContext, R.string.server_config_dl_fail);
				dismissProgress();
				break;
			case configDLSuccess:
				showDialogMessage(getString(R.string.server_config_dl_success));
				break;
			case configUpdateSuccess:
				ToastUtil.showToastShort(mContext, R.string.server_config_success);
				dismissProgress();
				findView();
				break;
			case configUpdateFail:
				ToastUtil.showToastShort(mContext, R.string.server_config_fail);
				dismissProgress();
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
			new BasicDialog.Builder(Task.this).setTitle(R.string.str_tip).setMessage(R.string.server_config_noNeed)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					downloadTask(true);
				}
			}).setNegativeButton(R.string.str_cancle).show();
		}
	};
	private class ConvertTask extends AsyncTask<Integer, Integer, Integer> {
		public static final int MODE_CONVERT = 0;
		public static final int MODE_SELECT = 1;
		private ArrayList<TestScheme> schemeList = null;// 测试方案列表
		BasicDialog dialog = null;
		int position;
		@Override
		protected void onPreExecute() {
			showProgressDialog("", false);
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
			dismissProgress();
			if (mode == MODE_CONVERT) {
				showListDialog();
			} else if (mode == MODE_SELECT) {
				findView();
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


	/***
	 * 广播接收器，更新接收按钮上的数组
	 * @author weirong.fan
	 *
	 */
	private class FileReceiver extends BroadcastReceiver{

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
					ShareFileModel.FILETYPE_TASK, new int[]{ShareFileModel.FILE_STATUS_INIT});
			if (lists.size() > 0){
				badge1.setText(lists.size() +"");
				badge1.show(true);;
			}else{
				badge1.hide(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		badge1.hide();
	}
}