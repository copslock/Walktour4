package com.walktour.gui.workorder.ah;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.AlertWakeLock;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigAutoTest;
import com.walktour.control.config.ConfigIndoor;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.ServerManager;
import com.walktour.control.instance.DataManagerFileList;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicDialog.Builder;
import com.walktour.gui.InfoTabHost;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.fleet.Fleet;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.newmap2.NewInfoTabActivity;
import com.walktour.gui.setting.sysroutine.SysRoutineActivity;
import com.walktour.gui.task.Task;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.workorder.ah.model.WorkOrderPoint;
import com.walktour.model.BuildingModel;
import com.walktour.model.FloorModel;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 安徽电信工单项目主界面->楼层
 * 
 * @author jianchao.wang
 * 
 */
public class WorkOrderFloorActivity extends BasicActivity implements OnClickListener {
	/** 日志标识 */
	public final static String TAG = "WorkOrderFloorActivity";
	/** 删除标识 */
	private static final int DELETE = 11;
	/** 删除成功标识 */
	private static final int DELETE_END = 12;
	/** 停止测试标识 */
	private static final int STOP_TEST_END = 16;
	/** 关闭结束标识 */
	private static final int CLOSE_END = 17;
	/** 关闭失败标识 */
	private static final int CLOSE_FAIL = 18;
	/** 楼层列表 */
	private ListView workOrderFloorList;
	/** 所属工单 */
	private WorkOrderPoint point;
	/** 服务器管理类 */
	private ServerManager mServer;
	/** 工单编号 */
	private String orderNo;
	/** 建筑物配置文件 */
	private ConfigIndoor config;
	/** 建筑物对象 */
	private BuildingModel builder;
	/** 楼层对象 */
	private List<FloorModel> floorList;
	/** 楼层适配器类 */
	private ArrayAdapter<String> floorAdapter;
	/** 显示的楼层列表 */
	private List<String> showFloorList = new ArrayList<String>();
	/** 删除进度框 */
	private ProgressDialog progressDialog;
	/** 应用数据 */
	private ApplicationModel appModel = ApplicationModel.getInstance();
	/** 是否设备初始化成功 */
	private static boolean isWaitInitTrace = false;
	/** 消息处理句柄 */
	private MyHandler handler = new MyHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = this.getIntent();
		this.config = ConfigIndoor.getInstance(this);
		orderNo = intent.getStringExtra(WorkOrderMainActivity.EXTRA_ORDRE_NO);
		String pointNo = intent.getStringExtra(WorkOrderMainActivity.EXTRA_POINT_NO);
		this.point = WorkOrderFactory.getInstance().getPointByNo(orderNo, pointNo);
		this.mServer = ServerManager.getInstance(WorkOrderFloorActivity.this);
		setContentView(R.layout.work_order_ah_floor_list);
		findView();
		genToolBar();
		initValue();
		// 注册接收测试完成消息
		IntentFilter broadCaseIntent = new IntentFilter();
		broadCaseIntent.addAction(WalkMessage.NOTIFY_TESTJOBDONE);
		broadCaseIntent.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE);
		broadCaseIntent.addAction(WalkMessage.ACTION_WALKTOUR_START_TEST);
		registerReceiver(testJobDoneReceiver, broadCaseIntent);
	}

	/**
	 * 视图关联设置
	 */
	private void findView() {
		this.workOrderFloorList = (ListView) this.findViewById(R.id.workOrderFloorList);
		((TextView) this.findViewById(R.id.order_no)).setText(orderNo);
		((TextView) this.findViewById(R.id.point_name)).setText(this.point.getName());
		this.findViewById(R.id.pointer).setOnClickListener(this);
		if (point.isCreate() || point.isClosed())
			this.findViewById(R.id.close_btn).setVisibility(View.GONE);
		else
			this.findViewById(R.id.close_btn).setOnClickListener(this);
	}

	/**
	 * 显示进度框
	 * @param title 标题
	 * @param message 信息
	 */
	private void showProgressDialog(String title,String message) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	/**
	 * 关闭进度框
	 */
	private void closeProgressDialog(){
		if(this.progressDialog != null){
			this.progressDialog.dismiss();
			this.progressDialog = null;
		}

	}

	/**
	 * 生成底部工具栏
	 */
	private void genToolBar() {
		ControlBar bar = (ControlBar) findViewById(R.id.ControlBar);
		boolean flag = !(point.getOrder().isClosed() || point.isClosed());
		// get button from bar
		Button btnNew = bar.getButton(0);
		btnNew.setEnabled(flag);
		Button btnRemove = bar.getButton(1);
		btnRemove.setEnabled(flag);
		Button btnEdit = bar.getButton(2);
		btnEdit.setEnabled(flag);
		Button btnRun = bar.getButton(3);
		btnRun.setEnabled(flag);
		// set text
		btnNew.setText(R.string.act_task_new);
		btnRemove.setText(R.string.delete);
		btnEdit.setText(R.string.work_order_edit_task);
		if (appModel.isTestJobIsRun() || appModel.isTestStoping()) {
			btnRun.setText(R.string.main_stop);
		} else {
			btnRun.setText(R.string.execute_test);
		}
		// set icon
		btnNew.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_new), null,
				null);
		btnRemove.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_clear),
				null, null);
		btnEdit.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_edit), null,
				null);
		if (appModel.isTestJobIsRun() || appModel.isTestStoping()) {
			btnRun.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_stop), null,
					null);
		} else {
			btnRun.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_start),
					null, null);
		}
		btnNew.setOnClickListener(this);
		btnRemove.setOnClickListener(this);
		btnEdit.setOnClickListener(this);
		btnRun.setOnClickListener(this);
	}

	/**
	 * 初始化建筑物
	 */
	private void initBuilder() {
		// 如果没有当前的建筑物，则新建建筑物
		List<BuildingModel> builders = this.config.getBuildings(this,true);
		for (BuildingModel builder : builders) {
			if (builder.getName().equals(this.point.getPointID())) {
				this.builder = builder;
				return;
			}
		}
		this.config.addBuilding(this,this.point.getPointID());
		builders = this.config.getBuildings(this,true);
		for (BuildingModel builder : builders) {
			if (builder.getName().equals(this.point.getPointID())) {
				this.builder = builder;
				break;
			}
		}
	}

	/**
	 * 初始化数据
	 */
	private void initValue() {
		this.initBuilder();
		this.floorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,
				this.showFloorList);
		this.initFloors();
		this.workOrderFloorList.setAdapter(this.floorAdapter);
		this.workOrderFloorList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	/**
	 * 初始化楼层信息
	 */
	private void initFloors() {
		this.floorList = this.config.getFloorList(this,new File(this.builder.getDirPath()));
		this.showFloorList.clear();
		for (FloorModel floor : floorList) {
			this.showFloorList.add(floor.getName());
		}
		this.workOrderFloorList.clearChoices();
		this.floorAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.Button01: // 新建楼层
			addFloor();
			break;
		case R.id.Button02: // 删除楼层
			deleteFloor();
			break;
		case R.id.Button03: // 编辑任务
			editTask();
			break;
		case R.id.Button04:// 执行测试
			excuteTest();
			break;
		case R.id.pointer:
			this.finish();
			break;
		case R.id.close_btn:// 关闭信息点
			this.closePoint();
			break;
		}
	}

	/**
	 * 关闭选中的信息点
	 */
	private void closePoint() {
		if (point.isCreate())
			return;
		String ip = this.mServer.getDownloadFleetIp();
		if (!this.mServer.getFleetServerType() || StringUtil.isNullOrEmpty(ip)) {
			Toast.makeText(getApplicationContext(), getString(R.string.work_order_fleet_ip_null), Toast.LENGTH_SHORT).show();
			return;
		}
		String account = this.mServer.getFleetAccount();
		if (account == null || account.length() == 0) {
			Toast.makeText(getApplicationContext(), getString(R.string.work_order_fleet_account_null), Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (!MyPhoneState.getInstance().isNetworkAvirable(getApplicationContext())) {
			Toast.makeText(getApplicationContext(), getString(R.string.sys_alarm_speech_neterr), Toast.LENGTH_SHORT).show();
			return;
		}
		int msgId = 0;
		int count = 0;
		for (WorkOrderPoint point1 : this.point.getOrder().getPointList()) {
			if (!point1.isCreate() && !point1.isClosed())
				count++;
		}
		if (count == 1) {
			msgId = R.string.work_order_close_last_point;
		} else {
			msgId = R.string.work_order_close_point;
		}
		new Builder(this).setMessage(msgId).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showProgressDialog(getString(R.string.work_order_close_point_title),getString(R.string.work_order_close_point_messge));
				new ClosePointThread().start();
			}
		}).setNegativeButton(R.string.str_cancle).show();
	}

	/**
	 * 关闭信息点线程
	 * 
	 * @author jianchao.wang 2014年6月20日
	 */
	private class ClosePointThread extends Thread {
		@Override
		public void run() {
			String ip = mServer.getDownloadFleetIp();
			int port = mServer.getDownloadFleetPort();
			String account = mServer.getFleetAccount();
			StringBuilder http = new StringBuilder();
			http.append("http://").append(ip).append(":").append(port);
			http.append("/Services/CQTTestWFItemService.svc/FinishCQTPointOfWFItem?");
			http.append("UserID=").append(account);
			http.append("&WorkItemCode=").append(point.getOrder().getWorkItemCode());
			http.append("&CQTPointID=").append(point.getPointID());
			LogUtil.d(TAG, http.toString());
			OkHttpClient client = new OkHttpClient();
			try {
				Request request = new Request.Builder().url(http.toString()).build();
				Response response = client.newCall(request).execute();
				if (response.isSuccessful()) {
					Message msg = handler.obtainMessage(CLOSE_END);
					handler.sendMessage(msg);
				} else {
					Message msg = handler.obtainMessage(CLOSE_FAIL);
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Message msg = handler.obtainMessage(CLOSE_FAIL);
				handler.sendMessage(msg);
			}
		}

	}

	/**
	 * 执行测试
	 */
	private void excuteTest() {
		LogUtil.w(TAG, "--stoping:" + appModel.isTestStoping() + "--run:" + appModel.isTestJobIsRun());

		List<TaskModel> modelList = this.point.getOrder().getTaskList();

		// 添加测试任务模型列表到任务列表中
		TaskListDispose taskList = TaskListDispose.getInstance();
		taskList.replaceTaskList(modelList);

		if (appModel.isTestStoping()) {
			Toast.makeText(WorkOrderFloorActivity.this, getString(R.string.main_testStoping), Toast.LENGTH_SHORT).show();
			return;
		}

		if (appModel.isTestJobIsRun()) {
			Toast.makeText(WorkOrderFloorActivity.this, getString(R.string.main_testStoping), Toast.LENGTH_SHORT).show();

			Intent interruptIntent = new Intent(WalkMessage.Action_Walktour_Test_Interrupt);
			sendBroadcast(interruptIntent);
		} else {
			if (appModel.isTraceInitSucc() && !DatasetManager.isPlayback) {
				// 如果有自动测试权限并且自动测试已经开启，给提示
				if (!TaskListDispose.getInstance().hasEnabledTask()) {
					Toast.makeText(WorkOrderFloorActivity.this, R.string.main_testTaskEmpty, Toast.LENGTH_LONG).show();
					return;
				} else if (TaskListDispose.getInstance().checkSerialDataWlan()) {
					showSerialDialog(R.string.main_hasWlanAndData);
					return;
				} else if (ConfigRoutine.getInstance().isBluetoothSync(getApplicationContext())
						&& !appModel.isBluetoothConnected()) {
					showBlueToothSetDialg();
					return;
				} else if (TaskListDispose.getInstance().checkSerialCallData()) {
					// 判断当前语音数据是否同时存在,如是检查是否有不可串行限制
					if (ConfigRoutine.getInstance().isGmccVersion()) {
						showSerialDialog(R.string.main_hasCallAndData);
						return;
					}
				}

				if (ApplicationModel.getInstance().getAppList().contains(WalkStruct.AppType.AutomatismTest)
						&& new ConfigAutoTest().isAutoTestOn()) {
					showAutoTestOnDialog();
				} else {
					AlertWakeLock.acquire(getApplicationContext());

					appModel.setIndoorTest(true);
					this.startTest(true, false, false);
				}
			} else {
				if (!appModel.isTraceInitSucc()) {
					Toast.makeText(WorkOrderFloorActivity.this, getString(R.string.main_traceIniting), Toast.LENGTH_LONG).show();
					sendBroadcast(new Intent(WalkMessage.NOTIFY_TESTTING_WAITTRACEINITSUCC));
					if (!isWaitInitTrace) {
						new WaitTraceInitSuccess().start();
					}
				} else if (DatasetManager.isPlayback) {
					Toast.makeText(WorkOrderFloorActivity.this, getString(R.string.main_test_after_playback_closed),
							Toast.LENGTH_LONG).show();
				}
				return;
			}
		}
	}

	/**
	 * 开始测试
	 * 
	 * @param isIndoortest
	 *          是否做室内测试
	 * @param isNetsniffer
	 *          是否抓包
	 * @param dontSaveData
	 *          是否不保存数据
	 */
	@SuppressLint("NewApi")
	private void startTest(boolean isIndoortest, boolean isNetsniffer, boolean dontSaveData) {
		if (this.workOrderFloorList.getCheckedItemCount() == 0) {
			Toast.makeText(getApplicationContext(), getString(R.string.str_check_non), Toast.LENGTH_SHORT).show();
			return;
		}
		LogUtil.w(TAG, "----is not general test!");
		appModel.setGerenalTest(false);
		appModel.setIndoorTest(isIndoortest);
		MapFactory.getMapData().setZoomGrade(10);
		MapFactory.getMapData().setScale(1);
		MapFactory.getMapData().setSampleSize(1);
		MapFactory.setLoadIndoor(isIndoortest);
		if (isIndoortest) {
			TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.OtherMap;
		}
		ApplicationModel.getInstance().setFloorModel(floorList.get(workOrderFloorList.getCheckedItemPosition()));
		appModel.setOutLooptimes(1);
		Intent startTestIntent = new Intent(WalkMessage.ACTION_WALKTOUR_START_TEST);
		LogUtil.w(TAG, "----looptimes=" + appModel.getOutLooptimes());
		startTestIntent.putExtra(WalkMessage.Outlooptimes, appModel.getOutLooptimes());
		startTestIntent.putExtra(WalkMessage.RcuFileLimitType, ConfigRoutine.getInstance().getSplitType());
		startTestIntent.putExtra(WalkMessage.RucFileSizeLimit, ConfigRoutine.getInstance().getFileSize());
		startTestIntent.putExtra(WalkMessage.KEY_TESTER, "");
		startTestIntent.putExtra(WalkMessage.KEY_TEST_ADDRESS, this.point.getAddress());
		startTestIntent.putExtra(WalkMessage.ISNETSNIFFER, isNetsniffer);
		startTestIntent.putExtra(WalkMessage.KEY_TEST_DONTSAVEDATA, dontSaveData);
		startTestIntent.putExtra(WalkMessage.WORK_ORDER_AH_RCU_FILE_NAME, getFileName());

		// 如果是有效的室内测试(有正确的楼层地图)
		String building = this.builder.getName();
		String floor = floorList.get(workOrderFloorList.getCheckedItemPosition()).getName();
		appModel.setFloorModel(floorList.get(workOrderFloorList.getCheckedItemPosition()));
		appModel.setBuildModel(this.builder);
		startTestIntent.putExtra(WalkMessage.KEY_TEST_INDOOR, isIndoortest);
		startTestIntent.putExtra(WalkMessage.KEY_TEST_BUILDING, building);
		startTestIntent.putExtra(WalkMessage.KEY_TEST_FLOOR, floor);
		startTestIntent.putExtra(WalkMessage.KEY_FROM_SCENE, SceneType.Anhui.getSceneTypeId());

		sendBroadcast(startTestIntent);
		// 重置时间
		TraceInfoInterface.traceData.setTestStartInfo();
		// 转到信息查看页面:Bundle内容为地图文件路径
		Intent intent = new Intent(WorkOrderFloorActivity.this, NewInfoTabActivity.class);
		if (TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Map
				|| TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Default)
			intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_MAP);
		else
			intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_OTHER);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
	}

	/**
	 * 获得执行完后的结果文件名称
	 * 
	 * @return
	 */
	private String getFileName() {
		StringBuilder fileName = new StringBuilder();
		fileName.append(this.point.getPointID()).append("_");
		fileName.append(this.point.getName()).append("_");
		fileName.append(floorList.get(workOrderFloorList.getCheckedItemPosition()).getName()).append("_");
		fileName.append(this.point.getTestTask()).append("_");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
		fileName.append(format.format(Calendar.getInstance(Locale.getDefault()).getTime()));
		return fileName.toString();
	}

	/**
	 * 等待串口初始化成功，如果一定时间未成功则重启串口
	 * 
	 * @author tangwq
	 * 
	 */
	private class WaitTraceInitSuccess extends Thread {
		public void run() {
			isWaitInitTrace = true;
			int waitTraceInitTime = 0;
			while (!appModel.isTraceInitSucc() && waitTraceInitTime < 100) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				waitTraceInitTime++;
				LogUtil.w(TAG, "--waitTraceInitTime:" + waitTraceInitTime + "---" + (waitTraceInitTime % 25 == 0));
				if (waitTraceInitTime % 25 == 0) { // 20S重新初始化一次Trace 口
					Intent reStartTraceInit = new Intent(WalkMessage.redoTraceInit);
					sendBroadcast(reStartTraceInit);
				}
			}
			isWaitInitTrace = false;
		}
	}

	/**
	 * 显示自动测试对话框
	 */
	private void showAutoTestOnDialog() {
		new BasicDialog.Builder(WorkOrderFloorActivity.this).setTitle(R.string.str_tip).setMessage(R.string.main_autoison)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(WorkOrderFloorActivity.this, Fleet.class));
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}

	/**
	 * 显示蓝牙设置对话框
	 */
	private void showBlueToothSetDialg() {
		new BasicDialog.Builder(WorkOrderFloorActivity.this).setTitle(R.string.str_tip)
				.setMessage(R.string.sys_setting_bluetooth_connect_faild)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(WorkOrderFloorActivity.this, SysRoutineActivity.class));
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}

	/**
	 * [显示任务列表的问题]<BR>
	 */
	private void showSerialDialog(int strId) {
		new BasicDialog.Builder(WorkOrderFloorActivity.this).setTitle(R.string.str_tip).setMessage(strId)
				.setNeutralButton(R.string.str_return, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	/**
	 * 编辑任务
	 */
	private void editTask() {
		List<TaskModel> modelList = this.point.getOrder().getTaskList();
		// 添加测试任务模型列表到任务列表中
		TaskListDispose taskList = TaskListDispose.getInstance();
		taskList.replaceTaskList(modelList);
		Intent intent = new Intent(WorkOrderFloorActivity.this, Task.class);
		startActivity(intent);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
	}

	/**
	 * 删除楼层
	 */
	@SuppressLint("NewApi")
	private void deleteFloor() {
		if (this.workOrderFloorList.getCheckedItemCount() > 0) {
			new BasicDialog.Builder(WorkOrderFloorActivity.this).setIcon(android.R.drawable.ic_menu_delete)
					.setTitle(R.string.delete).setMessage(R.string.main_indoor_delete)
					.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							showProgressDialog("",getString(R.string.removing));
							ArrayList<String> removeList = new ArrayList<String>();
							removeList.add(floorList.get(workOrderFloorList.getCheckedItemPosition()).getDirPath());
							new DeleteThread(removeList).start();
						}
					}).setNegativeButton(R.string.str_cancle).show();
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.str_check_non), Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 新增楼层
	 */
	@SuppressLint("InflateParams")
	private void addFloor() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.alert_dialog_edittext, null);

		new BasicDialog.Builder(WorkOrderFloorActivity.this).setIcon(android.R.drawable.ic_menu_edit)
				.setTitle(R.string.sys_indoor_newFloor).setView(textEntryView)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						EditText alert_EditText = (EditText) textEntryView.findViewById(R.id.alert_textEditText);
						String input = alert_EditText.getText().toString().trim();
						boolean exisited = false;
						for (int i = 0; i < floorList.size(); i++) {
							if (floorList.get(i).getName().equals(input)) {
								exisited = true;
								break;
							}
						}
						if (exisited) {
							Toast.makeText(getApplicationContext(),
									"\"" + input + "\"" + getString(R.string.sys_indoor_alert_existName), Toast.LENGTH_LONG).show();
						} else if (input.length() < 1) {
							Toast.makeText(getApplicationContext(), getString(R.string.sys_indoor_alert_nullName), Toast.LENGTH_LONG)
									.show();
						} else {
							if (addFloor(alert_EditText.getText().toString().trim())) {
								initFloors();
								dialog.dismiss();
							} else {
								Toast.makeText(getApplicationContext(), getString(R.string.sys_indoor_alert_errorName),
										Toast.LENGTH_LONG).show();
							}
						}
					}
				}).setNegativeButton(R.string.str_cancle).show();

	}

	/**
	 * 新增楼层信息
	 * 
	 * @param floorName
	 *          楼层信息
	 * @return
	 */
	private boolean addFloor(String floorName) {
		// xml文件中添加楼层信息
		return config.addFloor(new File(this.builder.getDirPath()), floorName);

	}

	/**
	 * 起一个线程执行删除
	 * 
	 * @author Administrator
	 * 
	 */
	private class DeleteThread extends Thread {
		private ArrayList<String> removeList = new ArrayList<String>();

		public DeleteThread(ArrayList<String> removeList) {
			this.removeList = removeList;
		}

		@Override
		public void run() {
			Message msg;
			for (int i = removeList.size() - 1; i >= 0; i--) {
				config.delete(removeList.get(i));
				msg = handler.obtainMessage(DELETE, removeList.get(i));
				handler.sendMessage(msg);
			}
			msg = handler.obtainMessage(DELETE_END);
			handler.sendMessage(msg);
		}
	}

	/**
	 * 重新从数据库中获取所有建筑信息
	 */
	private void updateDb() {
		DataManagerFileList.getInstance(getApplicationContext()).refreshFiles();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(testJobDoneReceiver);
		super.onDestroy();
	}

	/**
	 * 接收测试完成消息
	 */
	private final BroadcastReceiver testJobDoneReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			LogUtil.i(TAG, "testJobDoneReceiver" + appModel.isTestInterrupt());
			if (appModel.isTestInterrupt()) {
				new WaitStopType().start();
			} else {
				genToolBar();
			}
		}
	};

	private class WaitStopType extends Thread {
		public void run() {
			while (appModel.isTestStoping() || appModel.isTestJobIsRun()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Message msg = handler.obtainMessage(STOP_TEST_END);
			handler.sendMessage(msg);
		}
	}

	@Override
	protected void onResume() {
		this.genToolBar();
		super.onResume();
	}

	/**
	 * 处理关闭信息点过程
	 * 
	 * @author jianchao.wang
	 * 
	 */
	private static class MyHandler extends Handler {
		private WeakReference<WorkOrderFloorActivity> mActivity;

		public MyHandler(WorkOrderFloorActivity activity) {
			mActivity = new WeakReference<WorkOrderFloorActivity>(activity);
		}

		public void handleMessage(android.os.Message msg) {
			WorkOrderFloorActivity activity = mActivity.get();
			switch (msg.what) {
			// 关闭结束
			case CLOSE_END:
				activity.closeProgressDialog();
				Toast.makeText(activity, activity.getString(R.string.work_order_close_point_success), Toast.LENGTH_SHORT)
						.show();
				WorkOrderFactory.getInstance().closePoint(activity.point);
				activity.findView();
				activity.genToolBar();
				break;
			case CLOSE_FAIL:
				activity.closeProgressDialog();
				Toast.makeText(activity, activity.getString(R.string.work_order_close_point_fail), Toast.LENGTH_SHORT).show();
				break;
			// 正在删除
			case DELETE:
				String filePath = msg.obj.toString();
				activity.progressDialog.setMessage(activity.getString(R.string.removing) + filePath);
				break;
			// 删除完成
			case DELETE_END:
				activity.updateDb();
				activity.closeProgressDialog();
				activity.initFloors();
				break;
			case STOP_TEST_END:
				activity.genToolBar();
				break;
			}
		};
	}

}
