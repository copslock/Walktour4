package com.walktour.gui.workorder.hw;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.AlertWakeLock;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigAutoTest;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.CaptureImg;
import com.walktour.gui.R;
import com.walktour.gui.StartDialog;
import com.walktour.gui.WalkTour;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.fleet.Fleet;
import com.walktour.gui.setting.sysroutine.SysRoutineActivity;
import com.walktour.gui.task.Task;
import com.walktour.gui.task.TaskAll;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.TaskGroupConfig;
import com.walktour.gui.task.parsedata.model.TestPlanConfig;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.xml.common.TaskXmlTools;
import com.walktour.gui.workorder.hw.model.TestSchema;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 华为工单项目主界面->工单列表
 * 
 * @author jianchao.wang
 * 
 */
public class WorkOrderMainActivity extends BasicActivity implements OnClickListener, OnCheckedChangeListener {
	/** 日志标识 */
	public final static String TAG = "WorkOrderMainActivity";
	private static final int REQ_TAKE_PHOTO = 1;
	/** 传递的工单编号参数名 */
//	public final static String EXTRA_ORDRE_NO = "order_no";
	/** 传递的信息点编号参数名 */
//	public final static String EXTRA_POINT_NO = "point_no";
	/** 下载成功标识 */
	private static final int DOWNLOAD_END = 12;
	/** 下载成功但无数据标识 */
	private static final int DOWNLOAD_END_NULL = 20;
	/** 下载失败标识 */
	private static final int DOWNLOAD_FAIL = 16;
	/** 停止测试标识 */
	private static final int STOP_TEST_END = 18;
	/** 工单列表 */
	private ListView schemaListView;
	/** 工单列表 */
	private List<TestSchema> schemaList = new ArrayList<TestSchema>();
	/** 过滤的工单列表 */
	private List<TestSchema> filterList = new ArrayList<TestSchema>();
	/** 检索文本 */
	private EditText searchText;
	/** 工单列表适配类 */
	private SchemaArrayAdapter schemaAdapter;
	/** 服务器管理类 */
	private ServerManager mServer;
	/** 下载进度条 */
	private ProgressDialog progress;
	/** 选中的工单 */
	private TestSchema selectSchema;
	/** 上下文 */
	private WorkOrderMainActivity mContext;
	/** 应用数据 */
	private ApplicationModel appModel = ApplicationModel.getInstance();
	/** 是否设备初始化成功 */
	private static boolean isWaitInitTrace = false;
	/** 处理删除过程 */
	private Handler handler = new MyHandler(this);
	/** 开始任务设置菜单 */
	private StartDialog dialog;
	/** gps设备 */
	private GpsInfo gpsInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mContext = this;
		gpsInfo = GpsInfo.getInstance();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mServer = ServerManager.getInstance(WorkOrderMainActivity.this);
		setContentView(R.layout.work_order_hw_main_list);
		findView();
		this.genToolBar();
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
		Button updateBtn = (Button) this.findViewById(R.id.pointersetting);
		updateBtn.setText(R.string.work_order_hw_update);
		updateBtn.setVisibility(View.VISIBLE);
		updateBtn.setOnClickListener(this);
		this.findViewById(R.id.pointer).setOnClickListener(this);
		this.searchText = (EditText) this.findViewById(R.id.search_content_edit);
		this.searchText.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				filterOrder();
			}

		});
		this.schemaListView = (ListView) this.findViewById(R.id.workOrderList);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_TAKE_PHOTO && resultCode == RESULT_OK && dialog != null) {
			File latestfile = new File(data.getStringExtra(CaptureImg.MAP_PATH));
			dialog.showPhoto(latestfile);
		}
	}

	/**
	 * 接收测试完成消息
	 */
	private final BroadcastReceiver testJobDoneReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(WalkMessage.NOTIFY_TESTJOBDONE)
					|| intent.getAction().equals(WalkMessage.NOTIFY_INTERRUPTJOBDONE)) {
				LogUtil.i(TAG, "testJobDoneReceiver" + appModel.isTestInterrupt());
				if (appModel.isTestInterrupt()) {
					new WaitStopType().start();
				} else {
					genToolBar();
				}
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
			gpsInfo.releaseGps(getApplicationContext(), WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
			Message msg = handler.obtainMessage(STOP_TEST_END);
			handler.sendMessage(msg);
		}
	}

	/**
	 * 初始化数据
	 */
	private void initValue() {
		WorkOrderFactory.getInstance(this.getApplicationContext()).init();
		this.schemaList = WorkOrderFactory.getInstance(this.getApplicationContext()).getSchemaList();
		this.filterList.addAll(this.schemaList);
		this.schemaAdapter = new SchemaArrayAdapter(this.getApplicationContext(), R.layout.work_order_hw_main_row,
				this.filterList);
		this.schemaListView.setAdapter(schemaAdapter);
		this.filterOrder();
	}

	/**
	 * 过滤工单
	 */
	private void filterOrder() {
		String keyword = this.searchText.getText().toString().trim();
		this.filterList.clear();
		if (keyword.length() > 0) {
			for (TestSchema schema : this.schemaList) {
				if (schema.getName().contains(keyword))
					this.filterList.add(schema);
			}
		} else {
			this.filterList.addAll(this.schemaList);
		}
		this.schemaAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(testJobDoneReceiver);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointersetting:
			updateSchemaList();
			break;
		case R.id.pointer:
			this.finish();
			break;
		case R.id.Button01:// 执行测试
			excuteTest();
			break;
		case R.id.order:
			this.showTask((TestSchema) v.getTag());
		}

	}

	/**
	 * 生成默认的群组
	 */
	private void createDefaultGroup() {
		TaskListDispose taskList = TaskListDispose.getInstance();
		String defaultGroupID = taskList.getmTaskXmlTools().getCurrentSchemasID() + "_" + TaskXmlTools.sDefaultGroupID;
		if (taskList.existGroup(TaskXmlTools.sDefaultGroupName)) {
			taskList.getmTaskXmlTools().deleteGroupTasks(defaultGroupID, TaskModel.FROM_TYPE_DOWNLOAD);
			return;
		}
		TestPlanConfig config = taskList.getTestPlanConfig();
		config.getTestSchemas().getTestSchemaConfig()
				.setSchemaID(taskList.getmTaskXmlTools().getCurrentSchemasID());
		TaskGroupConfig group = new TaskGroupConfig();
		group.setGroupID(defaultGroupID);
		group.setGroupName(TaskXmlTools.sDefaultGroupName);
		group.setGroupSequence(1);
		taskList.setGroupID(defaultGroupID);
		taskList.addGroup(group);
	}

	/**
	 * 执行前
	 */
	private void initTask(TestSchema schema) {
		if (schema.getTaskGroupList().size() == 0)
			return;
		List<TaskModel> modelList = schema.getTaskGroupList().get(0).getTaskList();
		this.createDefaultGroup();
		// 添加测试任务模型列表到任务列表中
		TaskListDispose taskList = TaskListDispose.getInstance();
		String defaultGroupID = taskList.getmTaskXmlTools().getCurrentSchemasID() + "_" + TaskXmlTools.sDefaultGroupID;
		for (TaskModel model : modelList) {
			model.setTaskID(taskList.getmTaskXmlTools().getCurrentTaskID(defaultGroupID));
			taskList.getmTaskXmlTools().addTask(TaskXmlTools.sDefaultGroupName, model);
		}
		taskList.replaceTaskList(modelList, TaskModel.FROM_TYPE_DOWNLOAD);
	}

	/**
	 * 显示关联的任务
	 * 
	 * @param schema
	 *          计划
	 */
	private void showTask(TestSchema schema) {
		if (schema.getTaskGroupList().size() == 0)
			return;
		this.initTask(schema);
		Intent intent = new Intent(WorkOrderMainActivity.this, TaskAll.class);
		intent.putExtra("fromWorkOrder", true);
		startActivity(intent);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);

	}

	/**
	 * 工单列表适配类
	 * 
	 * @author jianchao.wang 2014年6月13日
	 */
	private class SchemaArrayAdapter extends ArrayAdapter<TestSchema> {
		/**
		 * 资源ID
		 */
		private int resourceId;

		private SchemaArrayAdapter(Context context, int textViewResourceId, List<TestSchema> objectList) {
			super(context, textViewResourceId, objectList);
			this.resourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(resourceId, parent, false);
			}
			String keyword = searchText.getText().toString().trim();
			TestSchema schema = this.getItem(position);
			if (schema == null)
				return view;
			String name = schema.getName();
			SpannableStringBuilder nameStyle = new SpannableStringBuilder(name);
			int start = name.indexOf(keyword);
			if (keyword.length() > 0 && start >= 0) {
				nameStyle.setSpan(new ForegroundColorSpan(Color.RED), start, start + keyword.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			LinearLayout order = (LinearLayout) view.findViewById(R.id.order);
			order.setTag(this.getItem(position));
			order.setOnClickListener(mContext);
			TextView orderName = (TextView) view.findViewById(R.id.order_name);
			orderName.setText(nameStyle);
			TextView orderLoop = (TextView) view.findViewById(R.id.order_loop);
			orderLoop.setText(getString(R.string.main_outerLoop) + "(" + schema.getTaskGroupList().get(0).getRepeatCount() + ")");
			RadioButton button = (RadioButton) view.findViewById(R.id.order_no);
			button.setTag(this.getItem(position));
			if (schema.equals(selectSchema)) {
				button.setChecked(true);
			} else {
				button.setChecked(false);
			}
			button.setOnCheckedChangeListener(mContext);
			return view;
		}
	}


	/**
	 * 从服务器端下载工单任务
	 */
	private void updateSchemaList() {
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
		this.progress = ProgressDialog.show(WorkOrderMainActivity.this, getString(R.string.work_order_download),
				getString(R.string.work_order_download_message), true);
		new DownloadThread().start();
	}

	private static class MyHandler extends Handler {
		WeakReference<WorkOrderMainActivity> reference;

		public MyHandler(WorkOrderMainActivity activity) {
			reference = new WeakReference<WorkOrderMainActivity>(activity);
		}

		public void handleMessage(Message msg) {
			WorkOrderMainActivity activity = reference.get();

			switch (msg.what) {
			// 下载结束
			case DOWNLOAD_END:
				activity.progress.dismiss();
				Toast.makeText(activity, activity.getString(R.string.work_order_download_success), Toast.LENGTH_SHORT).show();
				activity.filterOrder();
				break;
			case DOWNLOAD_FAIL:
				activity.progress.dismiss();
				Toast.makeText(activity, activity.getString(R.string.work_order_download_fail), Toast.LENGTH_SHORT).show();
				break;
			case DOWNLOAD_END_NULL:
				activity.progress.dismiss();
				Toast.makeText(activity, activity.getString(R.string.work_order_download_success_null), Toast.LENGTH_SHORT)
						.show();
				break;
			case STOP_TEST_END:
				activity.genToolBar();
				break;
			case WalkTour.REFLESH_VIEW:
				activity.genToolBar();
				break;
				default:
					break;
			}
		};
	}

	/**
	 * 下载线程
	 * 
	 * @author jianchao.wang 2014年6月20日
	 */
	private class DownloadThread extends Thread {
		@Override
		public void run() {
			String ip = mServer.getDownloadFleetIp();
			int port = mServer.getDownloadFleetPort();
			String guid = MyPhoneState.getInstance().getGUID(WorkOrderMainActivity.this);
			StringBuilder http = new StringBuilder();
			http.append("http://").append(ip).append(":").append(port);
			http.append("/Services/TestPlanService.svc/GetTestPlan?Guid={").append(guid).append("}");
			http.append("&DeviceType=2");
			LogUtil.d(TAG, http.toString());
			OkHttpClient client = new OkHttpClient();
			try {
				Request request = new Request.Builder().url(http.toString()).build();
				Response response = client.newCall(request).execute();
				if (response.isSuccessful()) {
					InputStream input = response.body().byteStream();
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int read;
					read = input.read(buffer);
					while (read > 0) {
						output.write(buffer, 0, read);
						read = input.read(buffer);
					}
					String result = dealResult(output.toString());
					WorkOrderFactory.getInstance(WorkOrderMainActivity.this).saveXmlString(result);
					if (result == null || result.trim().length() == 0) {
						Message msg = handler.obtainMessage(DOWNLOAD_END_NULL);
						handler.sendMessage(msg);
					} else {
						Message msg = handler.obtainMessage(DOWNLOAD_END);
						handler.sendMessage(msg);
					}
				} else {
					Message msg = handler.obtainMessage(DOWNLOAD_FAIL);
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Message msg = handler.obtainMessage(DOWNLOAD_FAIL);
				handler.sendMessage(msg);
			}
		}

	}

	/**
	 * 对获得的文件字符串做特殊处理，由于平台的技术原因导致获得的文件数据格式有问题，暂时这边做处理
	 * 
	 * @param result
	 *          文件字符串
	 * @return
	 */
	private String dealResult(String result) {
		result = result.replace("\\u000d\\u000a", "");
		result = result.replace("\\", "");
		if (result.startsWith("\""))
			result = result.substring(1, result.length());
		if (result.endsWith("\""))
			result = result.substring(0, result.length() - 1);
		return result;
	}

	/**
	 * 生成底部工具栏
	 */
	private void genToolBar() {
		ControlBar bar = (ControlBar) findViewById(R.id.ControlBar);
		// get button from bar
		Button btnRun = bar.getButton(0);
		if (appModel.isTestJobIsRun() || appModel.isTestStoping()) {
			btnRun.setText(R.string.main_stop);
		} else {
			btnRun.setText(R.string.execute_test);
		}
		if (appModel.isTestJobIsRun() || appModel.isTestStoping()) {
			btnRun.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_stop), null,
					null);
		} else {
			btnRun.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.controlbar_start),
					null, null);
		}
		btnRun.setOnClickListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			this.selectSchema = (TestSchema) buttonView.getTag();
		} else if (buttonView.getTag().equals(this.selectSchema)) {
			this.selectSchema = null;
		}
		this.schemaAdapter.notifyDataSetChanged();
	}

	/**
	 * 执行测试
	 */
	private void excuteTest() {
		LogUtil.w(TAG, "--stoping:" + appModel.isTestStoping() + "--run:" + appModel.isTestJobIsRun());
		if (this.selectSchema == null || this.selectSchema.getTaskGroupList().size() == 0)
			return;
		this.initTask(this.selectSchema);
		if (appModel.isTestStoping()) {
			Toast.makeText(WorkOrderMainActivity.this, getString(R.string.main_testStoping), Toast.LENGTH_SHORT).show();
			return;
		}

		if (appModel.isTestJobIsRun()) {
			Toast.makeText(WorkOrderMainActivity.this, getString(R.string.main_testStoping), Toast.LENGTH_SHORT).show();

			Intent interruptIntent = new Intent(WalkMessage.Action_Walktour_Test_Interrupt);
			sendBroadcast(interruptIntent);
		} else {
			if (appModel.getNetList().contains(WalkStruct.ShowInfoType.WOnePro)) {
				Toast.makeText(WorkOrderMainActivity.this, getString(R.string.wone_start_toast), Toast.LENGTH_SHORT).show();
				return;
			}

			// 当前是否需要检查自动同步时间是否打开并且非电信手机
			if (Deviceinfo.getInstance().getNettype() != Deviceinfo.NETTYPE_EVDO
					&& Deviceinfo.getInstance().getNettype() != Deviceinfo.NETTYPE_CDMA
					&& UtilsMethod.isAutoTimeOpen(getContentResolver())) {
				showTipDialog(R.string.main_toClose_AutoTimes);
				return;
			}

			if (appModel.isTraceInitSucc() && !DatasetManager.isPlayback) {
				// 如果有自动测试权限并且自动测试已经开启，给提示
				if (!TaskListDispose.getInstance().hasEnabledTask()) {
					Toast.makeText(WorkOrderMainActivity.this, R.string.main_testTaskEmpty, Toast.LENGTH_LONG).show();
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
					appModel.setOutLooptimes(this.selectSchema.getTaskGroupList().get(0).getRepeatCount());
					appModel.setOutLoopInterval(this.selectSchema.getTaskGroupList().get(0).getInterval());
					appModel.setOutLoopDisconnetNetwork(this.selectSchema.getTaskGroupList().get(0).isDisconnetNetwork());
					dialog = new StartDialog(WorkOrderMainActivity.this, this.handler, true,
							this.selectSchema.getName() + '_' + this.selectSchema.getId(), TaskModel.FROM_TYPE_DOWNLOAD,
							SceneType.Huawei);
					dialog.show();
					dialog.checkDTOrCQT(true);
				}
			} else {
				if (!appModel.isTraceInitSucc()) {
					Toast.makeText(WorkOrderMainActivity.this, getString(R.string.main_traceIniting), Toast.LENGTH_LONG).show();
					sendBroadcast(new Intent(WalkMessage.NOTIFY_TESTTING_WAITTRACEINITSUCC));
					if (!isWaitInitTrace) {
						new WaitTraceInitSuccess().start();
					}
				} else if (DatasetManager.isPlayback) {
					Toast.makeText(WorkOrderMainActivity.this, getString(R.string.main_test_after_playback_closed),
							Toast.LENGTH_LONG).show();
				}
			}
		}
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
	 * 显示蓝牙设置对话框
	 */
	private void showBlueToothSetDialg() {
		new BasicDialog.Builder(WorkOrderMainActivity.this).setTitle(R.string.str_tip)
				.setMessage(R.string.sys_setting_bluetooth_connect_faild)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(WorkOrderMainActivity.this, SysRoutineActivity.class));
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}

	/**
	 * 显示确认提示窗口
	 * 
	 * @param strId
	 */
	private void showTipDialog(final int strId) {
		new BasicDialog.Builder(WorkOrderMainActivity.this).setTitle(R.string.str_tip).setMessage(strId)
				.setNeutralButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (strId) {
						case R.string.main_toClose_AutoTimes:
							startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
							break;
						}
						dialog.dismiss();
					}
				}).show();
	}

	/**
	 * [显示任务列表的问题]<BR>
	 */
	private void showSerialDialog(int strId) {
		new BasicDialog.Builder(WorkOrderMainActivity.this).setTitle(R.string.str_tip).setMessage(strId)
				.setNeutralButton(R.string.str_return, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	/**
	 * 显示自动测试对话框
	 */
	private void showAutoTestOnDialog() {
		new BasicDialog.Builder(WorkOrderMainActivity.this).setTitle(R.string.str_tip).setMessage(R.string.main_autoison)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(WorkOrderMainActivity.this, Fleet.class));
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}

}
