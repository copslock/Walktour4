package com.walktour.workorder;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.dinglicom.UnicomInterface;
import com.walktour.Utils.AlertWakeLock;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastShow;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigIndoor;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.CaptureImg;
import com.walktour.gui.InfoTabHost;
import com.walktour.gui.R;
import com.walktour.gui.indoor.TestInfoValue;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.map.PointStatus;
import com.walktour.gui.newmap2.NewInfoTabActivity;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.model.BuildingModel;
import com.walktour.model.FloorModel;
import com.walktour.model.FtpJob;
import com.walktour.workorder.bll.InitServer;
import com.walktour.workorder.bll.ManipulateWorkOrderDetail;
import com.walktour.workorder.bll.ManipulateWorkOrderDetail.OnObtainDetailListener;
import com.walktour.workorder.bll.QueryWorkOrderDetail;
import com.walktour.workorder.model.Loglabel;
import com.walktour.workorder.model.ServerInfo;
import com.walktour.workorder.model.WorkOrderDetail;
import com.walktour.workorder.model.WorkSubItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * 工单子项详细页面
 * 
 * 该功能页面子项详细工单信息，开始弹出框（GPS、拍照、楼层管理等）
 * 
 * zhihui.lian
 * 
 */
@SuppressWarnings("deprecation")
@SuppressLint({ "InflateParams", "SdCardPath", "UseSparseArrays", "HandlerLeak" })
public class WorkOrderDetailActivity extends WorkOrderBaseActivity
		implements OnItemClickListener, OnObtainDetailListener {
	private static final String TAG = "WorkOrderDetailActivity";
	private static final String KEY_WORK_ID = "WorkId";
	public static final String KEY_WORK_NAME = "WorkName";
	// 以11开头，10开头被父类占用
	public static final int OBTAIN_DATA_OK = 1100;
	public static final int OBTAIN_DATA_FAILURE = 1101;
	public static final int EXECUTE_TIMEOUT = 1102;
	private static final int DOWNLOAD_MAP_STATUS = 1103;
	private static final int DOWNLOAD_MAP_SUCCESS = 1104;
	private static final int DOWNLOAD_MAP_FAILURE = 1105;
	// private static final int DOWNLOAD_MAP_EXIST = 1106;
	public static final int DEFAULT_TIMEOUT = 60 * 5; // 默认同步的超时时长为5分钟
	private Map<Integer, Boolean> mapState = new HashMap<Integer, Boolean>(); // 存储状态
	private ApplicationModel appModel = ApplicationModel.getInstance();

	// 业务逻辑类
	private ManipulateWorkOrderDetail maniDetail = null;
	private QueryWorkOrderDetail queryDetail = null;
	// private WriteLogLabel writeLogLabel = null; // 写Log标签的业务逻辑类，写到了一个类里

	private int workId;
	private WorkOrderDetail workOrderDetail; // 工单对象，由于每次调用会很耗时，所以直接把整个工单对象一次都获取过来
	private List<WorkSubItem> workSubItems = new ArrayList<WorkSubItem>(); // 工单子项目列表

	private ListView listView;
	private ListView orderDetailDesclistView; // 显示工单详情
	// private TextView orderDetailDescView;
	private RadioAdapter adapter;
	private int selectedPosition = 0; // 工单子项中选择的位置
	private boolean isObtainFinished = false; // 是否“获取工单详情”结束，包括连接服务器（如果未连接）、同步工单、下载所有楼层地图
	private final int waitGpsTimeOut = 20; // GPS超时时间
	private GpsInfo gpsInfo;
	private final int gpsFail = 1000;
	private static final int REQ_TAKE_PHOTO = 1;
	private Timer gpsTimer = null; // GPS计时器
	private TimerTask gpsTask = null;
	private boolean IsGpsSearched = false;
	private ArrayList<FloorModel> floorList = null;
	private int loopTime = 1;
	private boolean orderDetailShow = false;
	private SimpleAdapter mAdapter;

	public LocationClient mLocationClient = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.work_order_detail);
		mapState.put(0, true); // 初始化单选框值
		gpsInfo = GpsInfo.getInstance();
		gpsTimer = new Timer();
		IntentFilter broadCaseIntent = new IntentFilter();
		broadCaseIntent.addAction(GpsInfo.gpsLocationChanged);
		// 增加这个广播是因为在子项描述里面也有执行按钮，所以加载进来了
		broadCaseIntent.addAction(WalkMessage.ACTION_EXECUTE_TASK);
		registerReceiver(testJobDoneReceiver, broadCaseIntent);

		getIntentExtra();
		findView();
		genToolBar();

		maniDetail = new ManipulateWorkOrderDetail(this, this.workId);
		maniDetail.addListener(this);
		queryDetail = new QueryWorkOrderDetail(this);

		loadData();
		fillListView();
	}

	//
	private void fillOrderDetailDescView() {
		if (workOrderDetail == null)
			return;

		// <string name="worktype">工单类型</string>
		// <string name="projectid">项目编号</string>
		// <string name="project_name">项目名称</string>
		// <string name="plan_end_time">计划结束时间</string>
		// <string name="sender_account">发送人账号</string>
		// <string name="provinceid">省份</string>
		// <string name="cityid">城市</string>
		// <string name="areaid">区域</string>
		// <string name="test_site">测试基站</string>
		// <string name="test_building">测试建筑物</string>
		// <string name="address">地址</string>
		// <string name="site_num">基站数</string>
		// <string name="building_sum">建筑物数</string>
		// <string name="net_type">网络类型</string>
		// <string name="is_received">是否接收</string>

		List<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
		final String[] title = { getString(R.string.workname), getString(R.string.workarea), getString(R.string.worktype),
				getString(R.string.projectid), getString(R.string.project_name), getString(R.string.plan_end_time),
				getString(R.string.sender_account), getString(R.string.provinceid), getString(R.string.cityid),
				getString(R.string.areaid), getString(R.string.test_site), getString(R.string.test_building),
				getString(R.string.address), getString(R.string.site_num), getString(R.string.building_sum),
				getString(R.string.net_type), getString(R.string.is_received)

		};

		final String[] content = { "" + workOrderDetail.getWorkName(), "" + workOrderDetail.getWorkArea(),
				"" + workOrderDetail.getWorkType(), "" + workOrderDetail.getProjectId(), "" + workOrderDetail.getProjectName(),
				"" + workOrderDetail.getPlanEndTime(), "" + workOrderDetail.getSenderAccount(),
				"" + workOrderDetail.getProvinceId(), "" + workOrderDetail.getCityId(), "" + workOrderDetail.getAreaId(),
				"" + workOrderDetail.getTestSite(), "" + workOrderDetail.getTestBuilding(), "" + workOrderDetail.getAddress(),
				"" + workOrderDetail.getSiteSum(), "" + workOrderDetail.getBuildingSum(),
				"" + (workOrderDetail.getNetType() == 5 ? "WCDMA" : "GSM"), // WCDMA:5
																																		// GSM=1
				"" + workOrderDetail.getIsReceived() };

		for (int i = 0; i < title.length; i++) {

			HashMap<String, String> map = new HashMap<String, String>();
			map.put(KEY_WORK_ID, title[i] + "");
			map.put(KEY_WORK_NAME, content[i]);
			listData.add(map);
		}
		mAdapter = new SimpleAdapter(this, listData, R.layout.listview_item_work_order_detail_desc, new String[] {
				KEY_WORK_ID, KEY_WORK_NAME }, new int[] { R.id.txt_title, R.id.txt_content });
		orderDetailDesclistView.setAdapter(mAdapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// UnicomInterface.free();
		unregisterReceiver(testJobDoneReceiver);
	}

	/**
	 * 获取上一个界面回传的工单ID
	 */
	private void getIntentExtra() {
		if (getIntent().getExtras() != null) {
			workId = getIntent().getExtras().getInt(KEY_WORK_ID);
		}
	}

	/**
	 * 页面控件初始化
	 */
	public void findView() {
		(initTextView(R.id.title_txt)).setText(R.string.work_order_detail);
		((ImageButton) findViewById(R.id.pointer)).setOnClickListener(this);
		// RelativeLayout
		// titleLayout=(RelativeLayout)findViewById(R.id.workordertitle);
		// titleLayout.setOnClickListener(this);
		// map.put(0, 0); //给单选列表默认勾选值

		// orderDetailDescView = initTextView(R.id.work_order_detail);
		orderDetailDesclistView = (ListView) findViewById(R.id.list_work_desc);
		listView = (ListView) findViewById(R.id.list_work_order);
		listView.setOnItemClickListener(this);

		TextView workIds = initTextView(R.id.ItemDescrition);
		workIds.setText(String.valueOf(workId));
	}

	/**
	 * 生成底部工具栏目
	 * */
	private void genToolBar() {
		Button Button1 = initButton(R.id.Button01);
		Button1.setText(R.string.get_work_order_detail);
		Button1.setOnClickListener(this);
		Button Button2 = initButton(R.id.Button02);
		Button2.setVisibility(View.VISIBLE);
		Button2.setText(R.string.execute_test);
		Button2.setOnClickListener(this);
	}

	private void loadData() {
		this.workOrderDetail = (WorkOrderDetail) maniDetail.load();
		if (workOrderDetail != null) {
			this.workSubItems = workOrderDetail.getWorkSubItems();
		}
	}

	private void fillListView() {
		adapter = new RadioAdapter(WorkOrderDetailActivity.this);
		listView.setAdapter(adapter);
	}

	/**
	 * 
	 * 子项列表适配器 列表单选框实现
	 *
	 */
	class RadioAdapter extends BaseAdapter {

		private Context context;

		private String[] bizType = new String[3];

		public RadioAdapter(Context context) {
			this.context = context;
			bizType[0] = context.getString(R.string.log_oper_cs);
			bizType[1] = context.getString(R.string.log_oper_ps);
			bizType[2] = context.getString(R.string.log_oper_all);
		}

		@Override
		public int getCount() {
			return workSubItems.size();
		}

		@Override
		public Object getItem(int arg0) {
			return workSubItems.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			RadioHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_sub_order, null);
				holder = new RadioHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (RadioHolder) convertView.getTag();
			}

			holder.radio.setChecked(mapState.get(position) == null ? false : (mapState.get(position) == true ? true : false));
			holder.radio.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					RadioButton radioCk = (RadioButton) v;
					radioCk.setChecked(true);
					mapState.put(position, radioCk.isChecked());
					/**
					 * listview 单选实现
					 */
					if (radioCk.isChecked()) {
						for (int i = 0; i < workSubItems.size(); i++) {
							// taskListDispose.getCurrentTaskList().get(i).setEnable(0);
							if (i != position) {
								mapState.put(i, false);
							}
						}
					}
					RadioAdapter.this.notifyDataSetChanged();
				}
			});

			holder.ItemDescrition
					.setText(Html.fromHtml(String.valueOf(workSubItems.get(position).getItemId())
							+ "   "
							+ (DBHelper.getInstance(context).queryWorkState(workSubItems.get(position).getItemId()) == 1 ? "<font color=red>"
									+ context.getResources().getString(R.string.order_test_stated) + "</font>"
									: "<font color = #28aae2>" + context.getResources().getString(R.string.order_test_state_un)
											+ "</font>")));
			holder.orderTime.setText(UtilsMethod.getSpecialProcesTime(Long.valueOf(workOrderDetail.getPlanEndTime())));
			holder.orderPicAdr.setText(workSubItems.get(position).getTestFloors());
			holder.orderTaskType.setText(bizType[workSubItems.get(position).getServerType() - 1]);
			if (workSubItems.get(position).getFloorMap() != null
					&& workSubItems.get(position).getFloorMap().trim().length() != 0) {
				if (UtilsMethod.existFile(getPicPath(workSubItems.get(position).getFloorMap(), workSubItems.get(position)
						.getItemId()))) {
					holder.picStatus.setBackgroundResource(R.drawable.pic_exist);
				} else {
					holder.picStatus.setBackgroundResource(R.drawable.pic_err);
				}
			} else {
				holder.picStatus.setBackgroundResource(R.drawable.pic_none);
			}
			return convertView;
		}

	}

	/**
	 * 获取下载图片路径
	 * 
	 * @return
	 */
	public String getPicPath(String floorMapPath, int subId) {
		String picName = floorMapPath.substring(floorMapPath.lastIndexOf("/") + 1, floorMapPath.length());
		String mapFile = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Walktour"
				+ File.separator + "data" + File.separator + "indoortest" + File.separator + subId + File.separator + workId
				+ subId + File.separator + picName;
		return mapFile;
	}

	class RadioHolder {
		private RadioButton radio;
		private TextView ItemDescrition;
		private TextView orderPicAdr;
		private ImageView picStatus;
		private TextView orderTime;
		private TextView orderTaskType;

		public RadioHolder(View view) {
			this.radio = (RadioButton) view.findViewById(R.id.item_radio);
			this.ItemDescrition = (TextView) view.findViewById(R.id.ItemDescrition);
			this.orderPicAdr = (TextView) view.findViewById(R.id.order_pic_adr);
			this.orderTaskType = (TextView) view.findViewById(R.id.order_task_type);
			this.orderTime = (TextView) view.findViewById(R.id.order_time);
			this.picStatus = (ImageView) view.findViewById(R.id.pic_status);
		}
	}

	// <string name="workarea">工单区域</string>
	// <string name="worktype">工单类型</string>
	// <string name="projectid">项目编号</string>
	// <string name="project_name">项目名称</string>
	// <string name="plan_end_time">计划结束时间</string>
	// <string name="sender_account">发送人账号</string>
	// <string name="provinceid">省份</string>
	// <string name="cityid">城市</string>
	// <string name="areaid">区域</string>
	// <string name="test_site">测试基站</string>
	// <string name="test_building">测试建筑物</string>
	// <string name="address">地址</string>
	// <string name="site_num">基站数</string>
	// <string name="building_sum">建筑物数</string>
	// <string name="net_type">网络类型</string>
	// <string name="is_received">是否接收</string>

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.workordertitle: // 显示工单详细描述信息
			// synchWorkOrderDetail();
			if (!orderDetailShow) {
				orderDetailDesclistView.setVisibility(View.VISIBLE);
				// orderDetailDescView.setVisibility(View.VISIBLE);
				fillOrderDetailDescView();
				// StringBuilder builder = new StringBuilder();
				// String nextLine = "\n";
				// String blank = "                  ";
				// builder.append(getString(R.string.workarea) + blank +
				// workOrderDetail.getWorkArea() + nextLine);
				// builder.append(getString(R.string.worktype) + blank +
				// workOrderDetail.getWorkType() + nextLine);
				// builder.append(getString(R.string.projectid) + blank +
				// workOrderDetail.getProjectId() + nextLine);
				// builder.append(getString(R.string.project_name) + blank +
				// workOrderDetail.getProjectName() + nextLine);
				// builder.append(getString(R.string.plan_end_time) + blank +
				// workOrderDetail.getPlanEndTime() + nextLine);
				// builder.append(getString(R.string.sender_account) + blank +
				// workOrderDetail.getSenderAccount() + nextLine);
				// builder.append(getString(R.string.provinceid) + blank +
				// workOrderDetail.getProvinceId() + nextLine);
				// builder.append(getString(R.string.cityid) + blank +
				// workOrderDetail.getCityId() + nextLine);
				// builder.append(getString(R.string.areaid) + blank +
				// workOrderDetail.getAreaId() + nextLine);
				// builder.append(getString(R.string.test_site) + blank +
				// workOrderDetail.getTestSite() + nextLine);
				// builder.append(getString(R.string.test_building) + blank +
				// workOrderDetail.getTestBuilding() + nextLine);
				// builder.append(getString(R.string.address) + blank +
				// workOrderDetail.getAddress() + nextLine);
				// builder.append(getString(R.string.site_num) + blank +
				// workOrderDetail.getSiteSum() + nextLine);
				// builder.append(getString(R.string.building_sum) + blank +
				// workOrderDetail.getBuildingSum() + nextLine);
				// int nettype = workOrderDetail.getNetType();
				// if (nettype == 5) {
				// builder.append(getString(R.string.net_type) + blank + "WCDMA" +
				// nextLine);
				// }else if (nettype == 1) {
				// builder.append(getString(R.string.net_type) + blank + "GSM" +
				// nextLine);
				// }else {
				// builder.append(getString(R.string.net_type) + blank + "UNKNOWN" +
				// nextLine);
				// }
				//
				// builder.append(getString(R.string.is_received) + blank +
				// workOrderDetail.getIsReceived() + nextLine);
				// orderDetailDescView.setText(builder.toString());
			} else {
				orderDetailDesclistView.setVisibility(View.GONE);
				// orderDetailDescView.setVisibility(View.GONE);
			}
			orderDetailShow = !orderDetailShow;
			break;

		case R.id.Button01: // 获取工单详细
			Log.i(TAG, "click button01");
			try {
				obtainDetail();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case R.id.Button02: // 执行测试
			Log.i(TAG, "click button02");
			try {
				executeTask();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case R.id.pointer:
			finish();
			break;
		case R.id.list_work_order:

			break;

		default:
			break;
		}
	}

	private void obtainDetail() {
		// 如果没有初始化服务器信息，则先初始化。否则，直接同步工单字典
		if (!InitServer.hasInit()) {
			Log.i(TAG, "init server");
			initServer(); // 调用父类的初始化方法
		} else {
			Log.i(TAG, "synchWorkOrderDetail");
			synchWorkOrderDetail();
		}
	}

	/**
	 * 获取工单选项位置
	 */

	private void getSelectedPosition() {
		for (int i = 0; i < mapState.size(); i++) {
			if (mapState.get(i) == true) {
				selectedPosition = i;
				break;
			}
		}
	}

	/**
	 * 执行任务
	 */
	private void executeTask() {

		getSelectedPosition();
		getTaskPlan();
		// int subId = queryDetail.getSubId(workOrderDetail, this.selectedPosition);
		// String floorMapName = queryDetail.getFloorMap(workOrderDetail, subId);
		if (!startDiagShow) {
			if (workOrderDetail.getWorkArea() == WorkOrderDetail.INDOOR) {
				AlertWakeLock.acquire(getApplicationContext());
				new StartDialog(WorkOrderDetailActivity.this).show();
			} else {
				AlertWakeLock.acquire(getApplicationContext());
				new StartDialog(WorkOrderDetailActivity.this).show();
			}
		}

		// 保存相关的log标签，在这里保存，是因为这里是“执行测试”按钮，点击了说明选择的是生效的
		// writeLogLabel = new WriteLogLabel(this, this.workOrderDetail,
		// this.selectedPosition);
		// writeLogLabel.write();

		// 保存相关的log标签，在这里保存，是因为这里是“执行测试”按钮，点击了说明选择的是生效的
		saveLogLabel();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		WorkOrderSubItemDetailActivity.subItem = this.workSubItems.get(position);
		Intent intent = new Intent(WorkOrderDetailActivity.this, WorkOrderSubItemDetailActivity.class);
		intent.putExtra("WorkOrderDetail", workOrderDetail);
		startActivity(intent);
	}

	private void downloadFloorMaps() {
		int subCount = this.workSubItems.size();
		showProgressDialog(getString(R.string.get_floor_map), false);
		new Thread(new DownloadMaps(subCount)).start();
	}

	private class DownloadMaps implements Runnable {
		private int subCount;
		private boolean downloaded = false;
		private ServerInfo serverInfo;
		private String account;
		private String password;

		public DownloadMaps(int subCount) {
			this.subCount = subCount;
			serverInfo = ServerManager.getInstance(WorkOrderDetailActivity.this).readUnicomServer(
					UnicomInterface.FTP_DOWNLOAD_SERVER);
			account = serverInfo.getAccount(); // 楼层的ftp下载用户名
			password = serverInfo.getPassword(); // 楼层的ftp下载密码
			Log.i(TAG, "ip:" + serverInfo.getIpAddr() + ", port:" + serverInfo.getPort() + ", account:" + account
					+ ", password:" + password);
		}

		@Override
		public void run() {
			Message msg = null;

			// 下载所有的子工单地图
			for (int i = 0; i < subCount; i++) {
				int subId = queryDetail.getSubId(workOrderDetail, i);

				String floorMapName = queryDetail.getFloorMap(workOrderDetail, subId);
				String mapFile = getPicPath(floorMapName, subId);
				Log.i(TAG, "floorMapName:" + floorMapName);
				if (floorMapName == null || "".equals(floorMapName)) {
					msg = mHandler.obtainMessage(DOWNLOAD_MAP_STATUS);
					msg.arg1 = subId;
					msg.arg2 = DOWNLOAD_MAP_FAILURE;
					mHandler.sendMessage(msg);
					continue;
				}
				FtpJob ftpJob = new FtpJob("/" + floorMapName, mapFile);
				Log.i(TAG, "start download floor map...");
				downloaded = ServerManager.getInstance(WorkOrderDetailActivity.this).singerDownLoadFile(serverInfo.getIpAddr(),
						serverInfo.getPort(), account, password, ftpJob);
				Log.i(TAG, "end download floor map...");
				msg = mHandler.obtainMessage(DOWNLOAD_MAP_STATUS);
				msg.arg1 = subId;
				msg.arg2 = downloaded ? DOWNLOAD_MAP_SUCCESS : DOWNLOAD_MAP_FAILURE;
				mHandler.sendMessage(msg);
			}
		}

	}

	/**
	 * 解析工单子项测试任务
	 */
	private void getTaskPlan() {
		// int position = getSelectedItemPosition();
		// 根据子工单的位置获取子工单的ID
		if (workSubItems.size() == 0) {
			// 已经没有工单子项了
			Toast.makeText(getApplicationContext(), getString(R.string.get_sub_work_order_first), Toast.LENGTH_SHORT).show();
			// "请先获取工单子项!"
		}
		int subId = queryDetail.getSubId(workOrderDetail, this.selectedPosition);

		if (workSubItems != null) {
			ArrayList<TaskModel> modelList = (ArrayList<TaskModel>) queryDetail.getTask(this.workOrderDetail, subId);

			if (modelList.size() > 0) {
				// 添加测试任务模型列表到任务列表中
				TaskListDispose taskList = TaskListDispose.getInstance();
				taskList.replaceTaskList(modelList);
			}
			loopTime = queryDetail.getLoopTime(this.workOrderDetail, subId); // 外循环次数

			// int loopInterval = queryDetail.getLoopInterval(this.workOrderDetail,
			// subId); // 外循环时间间隔，暂不用
			// String building = queryDetail.getBuilding(this.workOrderDetail); //
			// 建筑物名称
			// String floor = queryDetail.getFloor(this.workOrderDetail, subId); //
			// 楼层名称
			// String floorMapUrl = queryDetail.getFloorMapUrl(this.workOrderDetail,
			// subId); // 楼层的ftp下载地址
			// String floorMapName=queryDetail.getFloorMap(this.workOrderDetail,
			// subId);
		} else {
			Toast.makeText(getApplicationContext(), R.string.get_sub_work_order_first, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 资源下载失败，执行测试与否 提示
	 */
	// private void showDialog(){
	//
	// new BasicDialog.Builder(this).setTitle(R.string.str_tip)
	// .setMessage(R.string.download_map_isContinue)
	// .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// AlertWakeLock.acquire(getApplicationContext());
	// new StartDialog(WorkOrderDetailActivity.this).show();
	// }
	// })
	// .setNegativeButton(R.string.str_cancle, new
	// DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// }
	// })
	// .show();
	// }

	/**
	 * 获得选择的子工单的位置
	 * 
	 * @return 位置
	 */
	// private int getSelectedItemPosition() {
	// int position = 0;
	// if (map != null) { // 获得选中列表下标
	// Iterator iter = map.entrySet().iterator();
	// while (iter.hasNext()) {
	// Map.Entry entry = (Map.Entry) iter.next();
	// position = (Integer) entry.getKey();
	// }
	// }
	// return position;
	// }

	/*
	 * start*************************************************以上为Walktour开始测试对话框相关处理
	 * **************************************************start
	 */
	private boolean startDiagShow = false;
	private RadioButton checkGps = null;
	private RadioButton checkIndoor;
	private CheckBox checkGyro;
	private CheckBox checkPreviously;
	private TextView gpsTip = null;
	private TextView editLongitude = null;
	private TextView editLatitude = null;
	private ImageView imageOutView = null;
	private FloorModel floorModel = null;
	private Button resetGPS = null;
	private TextView imgTip = null;
	private String tester = "";// 测试人员
	private String testAddress = "";// 测试地址
	private final String defaultStr = "N/A";

	// private boolean isIndoorTest = false; /*是否有效的室内测试*/
	/**
	 * 开始测试的对话框
	 * */
	private class StartDialog {
		private final Context mContext;
		/* View */
		private LayoutInflater factory01;
		private View dialogView;
		private LinearLayout layoutIndoor;
		// private RelativeLayout relayout;
		private RelativeLayout reTextBuild;
		// private RelativeLayout reSpinnerBuild;
		private LinearLayout gpsLinearlayout;
		private LinearLayout floorOutviewlayout;

		// private CheckBox checkGps;
		// private TextView textBuild;
		// private Spinner spinnerFloor;
		private Spinner spinnerBuild;
		private TextView editLooptimes;
		private EditText editTester;
		private EditText editAddress;
		private Button btnCamera;
		private ImageView image;
		private TextView tvTip;
		// private CheckBox checkGenaral;
		private ImageAdapter adapter;
		private Gallery gallery;
		private ConfigIndoor configIndoor = ConfigIndoor.getInstance(WorkOrderDetailActivity.this); /* 室内设置 */
		private boolean isIndoorTest = false; /* 是否有效的室内测试 */
		private int ITEM_GALLARY_POSITION;
		private TextView textPreviously;
		private CheckBox netsnifferCheckBox;
		private int subId;
		private List<String> floorPicList = new ArrayList<String>();
		private List<FloorModel> floorModels;
		// private TextView txtFloor;
		private TextView txtBuild;
		private String building;
		private String floor;
		private String floorMapName;

		public MyLocationListenner myListener = new MyLocationListenner();

		public StartDialog(Context context) {
			this.mContext = context;
			factory01 = LayoutInflater.from(context);
			dialogView = factory01.inflate(R.layout.alert_dialog_wone_starttest, null);
			layoutIndoor = (LinearLayout) dialogView.findViewById(R.id.LinearLayout01);
			editLooptimes = (TextView) dialogView.findViewById(R.id.editLooptimes);
			// editLooptimes.setText(String.valueOf(appModel.getOutLooptimes()));
			editTester = (EditText) dialogView.findViewById(R.id.EditTester);
			editAddress = (EditText) dialogView.findViewById(R.id.EditAddress);
			// relayout = (RelativeLayout)dialogView.findViewById( R.id.relayout);
			gallery = (Gallery) dialogView.findViewById(R.id.gallery);
			reTextBuild = (RelativeLayout) dialogView.findViewById(R.id.reTextBuild);
			// GPS信息显示，默认隐藏
			gpsLinearlayout = (LinearLayout) dialogView.findViewById(R.id.gpsLinearlayout);
			// reSpinnerBuild = (RelativeLayout)dialogView.findViewById(
			// R.id.reSpinnerBuild);
			floorOutviewlayout = (LinearLayout) dialogView.findViewById(R.id.floorOutviewlayout);
			image = (ImageView) dialogView.findViewById(R.id.ImageView01);
			tvTip = (TextView) dialogView.findViewById(R.id.TextViewTip);
			imgTip = (TextView) dialogView.findViewById(R.id.imgTip);
			gpsTip = (TextView) dialogView.findViewById(R.id.gpsTip);
			editLongitude = (TextView) dialogView.findViewById(R.id.editLongitude);
			editLatitude = (TextView) dialogView.findViewById(R.id.editLatitude);
			resetGPS = (Button) dialogView.findViewById(R.id.btnResetGPS);
			checkIndoor = (RadioButton) dialogView.findViewById(R.id.CheckIndoor);
			resetGPS.setOnClickListener(btnListener);
			netsnifferCheckBox = (CheckBox) dialogView.findViewById(R.id.start_netsniffer);

			checkGyro = (CheckBox) dialogView.findViewById(R.id.checkGyro);
			checkGyro.setOnCheckedChangeListener(checkListener);
			checkPreviously = (CheckBox) dialogView.findViewById(R.id.checkPreviously);
			textPreviously = (TextView) dialogView.findViewById(R.id.txtPreviously);
			/******* 判断是否存在预打点，如果不存在，那么预打点勾选框选中，并且能使用，如果不存在，那么不选中并且禁用 ************/
			// 屏蔽预打点
			boolean isExistPrevious = false;
			LogUtil.d("WorkOrderDetail", "----MapFactory.getMapData().pointStatusStack.Size="
					+ MapFactory.getMapData().getPointStatusStack().size());
			if (!MapFactory.getMapData().getPointStatusStack().isEmpty()) {
				for (int i = 0; i < MapFactory.getMapData().getPointStatusStack().size(); i++) {
					int status = MapFactory.getMapData().getPointStatusStack().elementAt(i).getStatus();
					LogUtil.d("WorkOrderDetail", "---s=" + status);
					if (status == PointStatus.POINT_STATUS_PREVIOUS) {
						isExistPrevious = true;
						break;
					}
				}
			}
			if (isExistPrevious) {
				checkPreviously.setEnabled(true);
				checkPreviously.setChecked(true);
				appModel.setPreviouslyTest(true);
			} else {
				checkPreviously.setChecked(false);
				checkPreviously.setEnabled(false);
				appModel.setPreviouslyTest(false);
			}
			checkGps = (RadioButton) dialogView.findViewById(R.id.CheckGps);
			imageOutView = (ImageView) dialogView.findViewById(R.id.imageCamera);
			btnCamera = (Button) dialogView.findViewById(R.id.btnCamera);

			btnCamera.setOnClickListener(btnListener);
			// textBuild = (TextView) dialogView.findViewById(R.id.textBuild);
			// spinnerFloor = (Spinner) dialogView.findViewById(R.id.SpinnerFloor);
			spinnerBuild = (Spinner) dialogView.findViewById(R.id.SpinnerBuild);
			// txtFloor = (TextView)dialogView.findViewById(R.id.EditFloor);
			txtBuild = (TextView) dialogView.findViewById(R.id.EditBuild);
			// gpsTip.setText(R.string.main_indoor_gpsnosearch);
			// 设置经纬度默认值
			setPositon(defaultStr, defaultStr);
			if (appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)) {
				// relayout.setVisibility(View.VISIBLE);
				gpsLinearlayout.setVisibility(View.VISIBLE);
				floorOutviewlayout.setVisibility(View.GONE);
				layoutIndoor.setVisibility(View.GONE);
			} else {
				reTextBuild.setVisibility(View.GONE);
				checkIndoor.setVisibility(View.VISIBLE);
				layoutIndoor.setVisibility(checkIndoor.isChecked() ? View.VISIBLE : View.GONE);
				floorOutviewlayout.setVisibility(View.GONE);
			}
			// 室内测试的开关
			ApplicationModel.getInstance().setIndoorTest(false);

			// 是否打开室内测试
			checkIndoor.setOnCheckedChangeListener(checkListener);
			// Gps开关
			checkGps.setOnCheckedChangeListener(checkListener);

			// Gps开关
			checkPreviously.setOnCheckedChangeListener(checkListener);
			// checkPreviously.setChecked(false);

			// 若GPS已经打开
			checkGps.setChecked(gpsInfo.isJobTestGpsOpen());
			if (workOrderDetail.getWorkArea() == WorkOrderDetail.INDOOR) {
				checkIndoor.setChecked(true);
				checkGps.setChecked(false);
			} else {
				checkGps.setChecked(true);
				checkIndoor.setChecked(false);
			}

			checkGps.setChecked(gpsInfo.isJobTestGpsOpen());

			subId = queryDetail.getSubId(WorkOrderDetailActivity.this.workOrderDetail,
					WorkOrderDetailActivity.this.selectedPosition);
			loopTime = queryDetail.getLoopTime(WorkOrderDetailActivity.this.workOrderDetail, subId);
			building = queryDetail.getBuilding(WorkOrderDetailActivity.this.workOrderDetail); // 建筑物名称
			floor = queryDetail.getFloor(WorkOrderDetailActivity.this.workOrderDetail, subId); // 楼层名称
			floorMapName = queryDetail.getFloorMap(WorkOrderDetailActivity.this.workOrderDetail, subId); // 楼层名称
			txtBuild.setText(floor == null ? "" : building);

			editLooptimes.setText(loopTime == 0 ? "1" : String.valueOf(loopTime));
			// setPositon(defaultStr, defaultStr);
			// //打开GPS服务
			// gpsInfo.openGps(WorkOrderDetailActivity.this,
			// WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
			// //启动GPS计时器
			// startGpsTimer();
			// gpsTip.setText(R.string.main_indoor_waitgps);

			IsGpsSearched = false;

			initial();

		}// end constructor

		/**
		 * 楼层图管理
		 */

		private void getAllfloorPicList() {
			floorModels = configIndoor.getFloorList(WorkOrderDetailActivity.this,new File(Environment.getExternalStorageDirectory().getPath() + "/Walktour/data/indoortest/" + subId));
			for (int i = 0; i < floorModels.size(); i++) {
				String buding = floorModels.get(i).getName();
				if (buding.equals(String.valueOf(workId) + String.valueOf(subId))) {
					floorPicList = floorModels.get(i).getAllMapPaths();
					floorModel = floorModels.get(i);
					Log.i(TAG, "floor Map>>>>>>>" + floorPicList.size() + "");
					break;
				}
			}
		}

		/**
		 * 加载建筑物
		 */

		private void initial() {
			// 设定 建筑物和楼层的下拉框
			getAllfloorPicList();
			if (floorPicList.size() > 0) {
				adapter = new ImageAdapter(WorkOrderDetailActivity.this, floorPicList);
				gallery.setAdapter(adapter);
				gallery.setOnItemSelectedListener(new GallerySelectListener());
				if (!adapter.isEmpty()) {
					isIndoorTest = true; // 当楼层地图存在时设为室内测试
					tvTip.setText("");
					image.setVisibility(View.GONE);
					appModel.setBuildModel(new BuildingModel(floorMapName, Environment.getExternalStorageDirectory().getPath() + "/Walktour/data/indoortest/" + subId,
							floorModels, "", ""));
				} else {
					isIndoorTest = false;
					tvTip.setText(R.string.main_notfound);
					image.setVisibility(View.VISIBLE);
					image.setImageDrawable(getResources().getDrawable(android.R.drawable.stat_notify_error));
				}
			} else {
				btnCamera.setEnabled(false);
				adapter = new ImageAdapter(WorkOrderDetailActivity.this, new ArrayList<String>());
				// gallery.setAdapter(adapter);
				// gallery.setOnItemSelectedListener(new GallerySelectListener());
				gallery.setVisibility(View.GONE);
				imgTip.setVisibility(View.VISIBLE);
				imageOutView.setImageDrawable(getResources().getDrawable(android.R.drawable.stat_notify_error));
				imgTip.setText(R.string.main_indoor_floorNomap);
			}

			/*
			 * final ArrayList<BuildingModel> buildingList =
			 * configIndoor.getBuildings(); //如果不包含室内专项权限 //设定 建筑物和楼层的下拉框 List<String
			 * > buildingNames = new ArrayList<String >(); for(int
			 * i=0;i<buildingList.size();i++){ buildingNames.add(
			 * buildingList.get(i).getName() ); } String[] names= new
			 * String[buildingNames.size()+1];
			 * names[0]=context.getString(R.string.none); //增加无 int i=1; for(String
			 * name:buildingNames){ names[i++]=name; }
			 * buildingNames=Arrays.asList(names); if( buildingList.size()==0 ){
			 * 
			 * buildingNames.add( getString(R.string.none) ); } final
			 * ArrayAdapter<String> adptBuild = new ArrayAdapter<String>( context,
			 * R.layout.simple_spinner_custom_layout, buildingNames );
			 * adptBuild.setDropDownViewResource(R.layout.spinner_dropdown_item);
			 * 
			 * spinnerBuild.setAdapter( adptBuild ); //此时为业务测试，在建筑选择监听中再监听楼层选择
			 * spinnerBuild.setOnItemSelectedListener(new OnItemSelectedListener(){
			 * private ArrayList<FloorModel> floorList;
			 * 
			 * @Override public void onItemSelected(AdapterView<?> parent, View view,
			 * int position, long id) { //重新生成楼层Spinner下拉项 floorList = new ArrayList<
			 * FloorModel >() ; final ArrayList< String > floorNames = new ArrayList<
			 * String >(); if( buildingList.size()>0&&position>0 ){
			 * btnCamera.setEnabled(true); floorList.addAll( buildingList.get(
			 * position-1 ).getFloorList() ) ;
			 * appModel.setBuildModel(buildingList.get( position -1)); }else{
			 * btnCamera.setEnabled(false); tvTip.setText( R.string.main_buildnotfound
			 * ); imageOutView.setImageDrawable( getResources().getDrawable(
			 * android.R.drawable.stat_notify_error ) ); floorList.clear(); } for( int
			 * i=0;i<floorList.size();i++){ floorNames.add(
			 * floorList.get(i).getName()); } if( floorList.size()==0){
			 * floorNames.add(getString(R.string.none) ); isIndoorTest = false;
			 * if(!buildingList.isEmpty()){ tvTip.setText( R.string.main_floornotfound
			 * ); } image.setVisibility(View.VISIBLE); image.setImageDrawable(
			 * getResources().getDrawable( android.R.drawable.stat_notify_error ) ); }
			 * final ArrayAdapter<String> adptFloor = new ArrayAdapter<String>(
			 * WorkOrderDetailActivity.this , R.layout.simple_spinner_custom_layout,
			 * floorNames );
			 * adptFloor.setDropDownViewResource(R.layout.spinner_dropdown_item);
			 * spinnerFloor.setAdapter(adptFloor );
			 * spinnerFloor.setOnItemSelectedListener(new MyItemListenr(floorList));
			 * 
			 * }
			 * 
			 * @Override public void onNothingSelected(AdapterView<?> parent) {
			 * 
			 * } });
			 */
		}

		/**
		 * 监听楼层选择 监听室内测试楼层 监听业务测试楼层
		 * 
		 * @author Administrator
		 *
		 */
		// private class MyItemListenr implements OnItemSelectedListener {
		// private ArrayList< FloorModel > floorList;
		//
		// public MyItemListenr(ArrayList< FloorModel > floorList){
		// this.floorList = floorList;
		// }
		// @Override
		// public void onItemSelected(AdapterView<?> parent,
		// View view, int position, long id) {
		//
		// if(floorList.size()>0 ){
		// btnCamera.setEnabled(true);
		// floorModel = floorList.get(position);
		// gallery.setVisibility(View.VISIBLE);
		// ArrayList<String> mappaths = floorModel.getAllMapPaths();
		// ArrayList<String> outsideMaps = floorModel.getOutViewMapPaths();
		// //楼层有外观图
		// if(!outsideMaps.isEmpty()){
		// imgTip.setVisibility(View.GONE);
		// imgTip.setText("");
		// showImage(outsideMaps.get(0));
		// }else{//楼层没有外观图
		// imgTip.setVisibility(View.VISIBLE);
		// imageOutView.setImageDrawable(
		// getResources().getDrawable(
		// android.R.drawable.stat_notify_error ) );
		// imgTip.setText(R.string.main_indoor_floorNomap);
		// }
		// adapter = new ImageAdapter(WorkOrderDetailActivity.this, mappaths);
		// gallery.setAdapter(adapter);
		// gallery.setOnItemSelectedListener(new GallerySelectListener());
		// if(!adapter.isEmpty()){
		// isIndoorTest = true; //当楼层地图存在时设为室内测试
		// tvTip.setText("");
		// image.setVisibility(View.GONE);
		// }else{
		// isIndoorTest = false;
		// tvTip.setText( R.string.main_notfound );
		// image.setVisibility(View.VISIBLE);
		// image.setImageDrawable(
		// getResources().getDrawable(
		// android.R.drawable.stat_notify_error ) );
		// }
		//
		// }else{
		// btnCamera.setEnabled(false);
		// adapter = new ImageAdapter(WorkOrderDetailActivity.this, new
		// ArrayList<String>());
		// /*gallery.setAdapter(adapter);
		// gallery.setOnItemSelectedListener(new GallerySelectListener());*/
		// gallery.setVisibility(View.GONE);
		// imgTip.setVisibility(View.VISIBLE);
		// imageOutView.setImageDrawable(
		// getResources().getDrawable(
		// android.R.drawable.stat_notify_error ) );
		// imgTip.setText(R.string.main_indoor_floorNomap);
		// }
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> parent) {
		// }
		// };

		private class GallerySelectListener implements OnItemSelectedListener {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				ITEM_GALLARY_POSITION = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		}

		public class ImageAdapter extends BaseAdapter {
			// private int mGalleryItemBackground;
			private Context mContext;
			private List<String> floorMaps;

			public ImageAdapter(Context context, List<String> floorMaps) {
				mContext = context;
				this.floorMaps = floorMaps;
				TypedArray a = obtainStyledAttributes(R.styleable.GalleryTheme);
				// mGalleryItemBackground = a.getResourceId(
				// R.styleable.GalleryTheme_android_galleryItemBackground, 0);
				a.recycle();
			}

			@Override
			public int getCount() {
				return floorMaps.size();
			}

			@Override
			public Object getItem(int position) {
				return position;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@SuppressLint("ViewHolder")
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = LayoutInflater.from(mContext).inflate(R.layout.gallery_item, null);
				ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
				TextView tv = (TextView) view.findViewById(R.id.imageName);
				String mappath = floorMaps.get(position);
				imageView.setBackgroundResource(R.drawable.background_light);
				File file = new File(mappath);
				Bitmap bmp = null;
				if (file.isFile()) {
					tv.setText(file.getName());
					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(mappath, opts);
					opts.inSampleSize = UtilsMethod.computeSuitedSampleSize(opts, 480 * 800);
					opts.inJustDecodeBounds = false;
					bmp = BitmapFactory.decodeFile(mappath, opts);
				}
				imageView.setImageBitmap(bmp);
				return view;
			}
		}

		/**
		 * 清除GPS状态
		 */
		private void clearGpsStatus() {
			LogUtil.w(TAG, "clear All status");
			if (gpsInfo.isJobTestGpsOpen()) {
				gpsInfo.releaseGps(WorkOrderDetailActivity.this, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
				clearGpstimer();
			}
		}

		private void setTestFloorModel() {
			if (isIndoorTest) {
				File file = null;
				String path = "";
				// 要加载的室内地图,首先判断是否是ibwave地图
				if (!StringUtil.isNullOrEmpty(floorModel.tabfilePath)) {
					file = new File(floorModel.tabfilePath);
					path = floorModel.tabfilePath;
				} else {
					file = new File(floorModel.getAllMapPaths().get(ITEM_GALLARY_POSITION));
					path = floorModel.getAllMapPaths().get(ITEM_GALLARY_POSITION);
				}
				if (file.exists()) {
					floorModel.setTestMapPath(path);
					ApplicationModel.getInstance().setFloorModel(floorModel);
					LogUtil.w(TAG, "----path=" + path);
				}
			} else if ((floorList == null || floorList.size() == 0) && spinnerBuild.getSelectedItemId() > 0) {
				if (ApplicationModel.getInstance().getFloorModel() != null) {
					ApplicationModel.getInstance().getFloorModel().setTestMapPath("");
					LogUtil.w(TAG, "----path=");
				}
				MapFactory.getMapData().setMap(null);
			}
		}

		public void show() {
			DisplayMetrics metric = new DisplayMetrics();
			WorkOrderDetailActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metric);
			BasicDialog dialog = new BasicDialog.Builder(mContext).setMessage(R.string.main_start)
					.setView(dialogView, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, (int) (320 * metric.density)))
					/* .setView( dialogView ) */
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 用户选择了CQT，必须要获得经纬度才能继续
							if (checkIndoor.isChecked() && !IsGpsSearched) {
								Toast.makeText(WorkOrderDetailActivity.this, R.string.gps_fail_do_nothing, Toast.LENGTH_LONG).show();
								return;
							}
							dialog.dismiss();

							// 如果外循环输入为空或者输入0，转为1
							// boolean check =
							// editLooptimes.getText().toString().equals("")||editLooptimes.getText().toString().equals("0");
							appModel.setOutLooptimes(loopTime);
							tester = editTester.getText().toString().trim();
							testAddress = editAddress.getText().toString().trim();

							// 设置是否室内测试
							ApplicationModel.getInstance().setIndoorTest(isIndoorTest);
							LogUtil.i(TAG, "---isIndoorTest?" + isIndoorTest);
							setTestFloorModel();
							GpsInfo.getInstance().setGpsLastChangeTime(0);
							startTest(isIndoorTest, netsnifferCheckBox.isChecked());
							clearGpstimer();
						}

					}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

							// 取消时清除状态
							clearGpsStatus();
							if (mLocationClient != null) {
								mLocationClient.stop();
							}
						}
					})/*
						 * .setOnKeyListener(new OnKeyListener() {
						 * 
						 * @Override public boolean onKey(DialogInterface dialog, int
						 * keyCode, KeyEvent event) { if(keyCode == KeyEvent.KEYCODE_BACK){
						 * dialog.dismiss(); //点击返回按钮时清除状态 clearGpsStatus(); } return false;
						 * } })
						 */
					.setOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							LogUtil.w(TAG, "--diag show dismiss--");
							diagDismissClean();
						}
					}).create();
			dialog.show();

			// 判断网络的开关s
			if (checkIndoor.isChecked()) {
				// LocationManager locationManager =
				// (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
				// if(!locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER))
				// {
				// new BasicDialog.Builder(WorkOrderDetailActivity.this)
				// .setTitle( getString(R.string.main_gps) )
				// .setIcon(android.R.drawable.ic_lock_power_off)
				// .setMessage( getString(R.string.gps_no_open_network) )
				// .setNegativeButton(R.string.str_ok, new
				// DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog, int which) {
				// Intent i = new
				// Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				// startActivity(i);
				// }
				// }).show();
				//
				// }
				if (mLocationClient == null) {
					mLocationClient = new LocationClient(WorkOrderDetailActivity.this);
				}
				setLocationOption();
				mLocationClient.start();
				mLocationClient.registerLocationListener(myListener);
			}

		}// end method show

		private OnClickListener btnListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btnCamera:
					// sd卡没有挂载
					if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						Toast.makeText(WorkOrderDetailActivity.this, R.string.sdcard_unmount, Toast.LENGTH_SHORT).show();
						return;
					}
					doTakePhoto();
					break;
				case R.id.btnResetGPS:
					// 打开GPS服务
					gpsInfo.openGps(WorkOrderDetailActivity.this, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
					// 启动GPS计时器
					startGpsTimer();
					gpsTip.setText(R.string.main_indoor_waitgps);
					resetGPS.setVisibility(View.GONE);
					break;

				}
			}
		};

		/**
		 * 启动拍照intent，调用拍照程序
		 */
		protected void doTakePhoto() {
			try {
				Intent intent = new Intent(WorkOrderDetailActivity.this, CaptureImg.class);
				startActivityForResult(intent, REQ_TAKE_PHOTO);
			} catch (ActivityNotFoundException e) {

			}
		}

		private OnCheckedChangeListener checkListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				switch (buttonView.getId()) {
				case R.id.CheckIndoor:
					// 屏蔽预打点
					textPreviously.setVisibility(View.VISIBLE);
					checkPreviously.setVisibility(View.VISIBLE);
					layoutIndoor.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					floorOutviewlayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					if (!isChecked) {
						isIndoorTest = false;
						checkGps.setChecked(isChecked);
						// mLocationManager.removeUpdates(locationListener);
						// mBMapManager.stop();
						if (mLocationClient != null) {
							mLocationClient.stop();
						}
					} else {
						checkGps.setChecked(false);
						checkIndoor.setChecked(isChecked);
						setLocationOption();
						if (mLocationClient == null) {
							mLocationClient = new LocationClient(getApplicationContext());
						}
						mLocationClient.start();
						mLocationClient.registerLocationListener(myListener);
						// mLocationManager.enableProvider((int)
						// MKLocationManager.MK_GPS_PROVIDER);

					}

					break;
				case R.id.CheckGps:
					if (isChecked) {
						checkIndoor.setChecked(false);
						checkGps.setChecked(isChecked);
						checkPreviously.setChecked(false);
						// 屏蔽预打点
						checkPreviously.setVisibility(View.GONE);
						textPreviously.setVisibility(View.GONE); // 屏蔽打点标题
						appModel.setGpsTest(true);
						appModel.setPreviouslyTest(false);
						// 如果GPS没有打开则重新打开
						if (!gpsInfo.isJobTestGpsOpen()) {
							setPositon(defaultStr, defaultStr);
							gpsInfo.openGps(WorkOrderDetailActivity.this, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
							gpsTip.setText(R.string.main_indoor_waitgps);
							resetGPS.setVisibility(View.GONE);
						}
						// GPS信息可见
						if (!appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)) {
							gpsLinearlayout.setVisibility(View.VISIBLE);
						}
						// 提示正在定位
						gpsTip.setText(R.string.main_indoor_waitgps);
					} else {
						checkIndoor.setChecked(false);
						appModel.setGpsTest(false);
						textPreviously.setVisibility(View.VISIBLE);
						// 屏蔽预打点
						checkPreviously.setVisibility(View.VISIBLE);
						// 停止GPS服务
						gpsInfo.releaseGps(WorkOrderDetailActivity.this, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
						if (gpsInfo.getLocation() == null) {
							gpsTip.setText(R.string.main_indoor_gpsnosearch);
						}
						// 清除GPS计时器
						clearGpstimer();
						// 添加此判断，是当GPS搜索完成后，gps开关会关闭，会触发此事件
						if (!IsGpsSearched) {
							// //如果没有打开GPS搜索过，GPS信息不可见
							// if(!appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)){
							// gpsLinearlayout.setVisibility(View.GONE);
							// }
							gpsTip.setText(R.string.main_indoor_waitgps);
						}
					}
					/**
					 * else{ LogUtil.w(tag, "**********set gps to falses");
					 * checkIndoor.setChecked(false); appModel.setGpsTest(false);
					 * textPreviously.setVisibility(View.VISIBLE); //屏蔽预打点
					 * checkPreviously.setVisibility(View.VISIBLE); //停止GPS服务
					 * gpsInfo.releaseGps(WalkTour.this,
					 * WalkCommonPara.OPEN_GPS_TYPE_JOBTEST); if(gpsInfo.getLocation() ==
					 * null){ gpsTip.setText(R.string.main_indoor_gpsnosearch); }
					 * //清除GPS计时器 clearGpstimer(); //添加此判断，是当GPS搜索完成后，gps开关会关闭，会触发此事件
					 * if(!IsGpsSearched){ // //如果没有打开GPS搜索过，GPS信息不可见 //
					 * if(!appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)){
					 * // gpsLinearlayout.setVisibility(View.GONE); // } }
					 */

					// }
					break;
				case R.id.checkPreviously:
					appModel.setPreviouslyTest(isChecked);
					checkIndoor.setEnabled(!isChecked);
					break;
				case R.id.checkGyro:
					// appModel.setGyroTest(isChecked);
					break;
				}
			}
		};

		public class MyLocationListenner implements BDLocationListener {

			private double longitude;
			private double latitude;

			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location != null) {
					IsGpsSearched = true;
					gpsTip.setText(WorkOrderDetailActivity.this.getString(R.string.main_indoor_gpssucces));
					longitude = location.getLongitude(); // 经度
					latitude = location.getLatitude(); // 纬度
					setPositon(String.valueOf(longitude), String.valueOf(latitude));
					Location location2 = new Location(LocationManager.GPS_PROVIDER); // 由BDLocation转Location
					location2.setLongitude(longitude);
					location2.setLatitude(latitude);
					gpsInfo.setLocation(location2);
					// gpsInfo.setbDlocation(location);
				} else {
					// gpsInfo.setbDlocation(null);
					gpsInfo.setLocation(null);
				}
			}

		}

		// 设置相关参数
		public void setLocationOption() {
			LocationClientOption option = new LocationClientOption();
			option.setCoorType("wgs84"); // 设置坐标类型
			option.setScanSpan(2000);
			// option.setsetPriority(LocationClientOption.MIN_SCAN_SPAN_NETWORK); //
			// 设置网络优先
			option.setOpenGps(true); // 打开gps
			// option.disableCache(true);
			if (mLocationClient != null) {
				mLocationClient.setLocOption(option);
			}
		}
	}// end class StartDialog

	/**
	 * 设置经度和纬度
	 * 
	 * @param longitude
	 * @param latitude
	 */
	private void setPositon(String longitude, String latitude) {
		editLongitude.setText(longitude);
		editLatitude.setText(latitude);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.w(TAG, "---resultCode=" + requestCode);

		if (requestCode == REQ_TAKE_PHOTO) {
			if (resultCode != RESULT_OK) {
				return;
			}
			File latestfile = new File(data.getStringExtra(CaptureImg.MAP_PATH));
			LogUtil.w(TAG, "--latestfile path:" + latestfile.getAbsolutePath());
			imgTip.setVisibility(View.GONE);
			imgTip.setText("");
			showImage(latestfile.getAbsolutePath());
			if (floorModel != null) {
				ConfigIndoor configIndoor = ConfigIndoor.getInstance(WorkOrderDetailActivity.this);
				String newName = getString(R.string.str_lczp) + floorModel.getBuildingName() + "_" + floorModel.getName()
						+ ".jpg";
				File floorOutView = new File(floorModel.getDirPath() + "/camera");
				if (!floorOutView.exists()) {
					floorOutView.mkdirs();
				}
				String desFileFullPath = floorOutView.getAbsolutePath() + "/" + newName;
				// 删除原有楼层外观图
				configIndoor.delete(desFileFullPath);
				// 拷贝楼层外观图至对应的楼层的camera目录下
				configIndoor.setMap(latestfile.getAbsolutePath(), desFileFullPath);
			} else {
				LogUtil.w(TAG, "----floorModel is null--");
			}
		}
	}

	/**
	 * 开始测试
	 */
	private void startTest(boolean isIndoortest, boolean isNetsniffer) {

		// 添加W-One开始测试事件的标签
		Loglabel.newInstance().start_time = UtilsMethod.sdFormat.format(new Date());

		// 如果GPS开关和室内开关都未选中，并且不是有效的室内专项测试
		if (!checkGps.isChecked()) {
			// 初始设置为一般测试
			appModel.setGerenalTest(true);
			// 设置为非GPS测试
			appModel.setGpsTest(false);

			LogUtil.w(TAG, "----is general test!");
		} else {
			LogUtil.w(TAG, "----is not general test!");
			appModel.setGerenalTest(false);
		}
		appModel.setWoneTest(true);
		LogUtil.w(TAG, "----checkPreviously.isChecked=" + checkPreviously.isChecked());
		// 2012.04.19修改，不清除地图
		appModel.setNeedToCleanMap(!checkPreviously.isChecked());
		// 此逻辑解决预打点测试时，没有清空原有打点及轨迹
		if (checkPreviously.isChecked()) {
			MapFactory.getMapData().getEventQueue().clear();
			// MapFactory.getMapData().getPointStatusStack().clear();
			for (int i = MapFactory.getMapData().getPointStatusStack().size() - 1; i >= 0; i--) {
				int status = MapFactory.getMapData().getPointStatusStack().get(i).getStatus();
				if (status == PointStatus.POINT_STATUS_EFFECTIVE || status == PointStatus.POINT_STATUS_PREVIOUS_EFFECTIVE) {
					MapFactory.getMapData().getPointStatusStack().remove(i);
				}
			}
			MapFactory.getMapData().getQueueStack().clear();
			TraceInfoInterface.traceData.getGpsLocas().clear();
		} else {
			MapFactory.getMapData().getEventQueue().clear();
			MapFactory.getMapData().getPointStatusStack().clear();
			MapFactory.getMapData().getQueueStack().clear();
			TraceInfoInterface.traceData.getGpsLocas().clear();
		}
		if (checkGps.isChecked()) {
			appModel.setNeedToCleanMap(true);
			// 2012.04.19修改，不清除地图
			// appModel.setNeedToCleanMap(false);
		}
		if (appModel.isGyroTest()) {
			// 每次测试开始时都先设置为未校准状态
			// RecordTraceService.setAdjust(false);
			MapFactory.getMapData().getPointStatusStack().clear();
			appModel.setNeedToCleanMap(false);
		}
		MapFactory.getMapData().setZoomGrade(10);
		MapFactory.getMapData().setScale(1);
		MapFactory.getMapData().setSampleSize(1);
		MapFactory.setLoadIndoor(isIndoortest);
		// if(isIndoortest){ 暂时屏蔽
		// TraceInfoDispose.currentShowChildTab = WalkStruct.ShowInfoType.OtherMap;
		// }
		// SavedMapData.MapData.flag_toggle = false;
		// 开始测试时清除地图的标志
		// ApplicationModel.getInstance().setNeedToCleanMap( true );

		Intent startTestIntent = new Intent(WalkMessage.ACTION_WALKTOUR_START_TEST);
		LogUtil.w(TAG, "----looptimes=" + appModel.getOutLooptimes());
		startTestIntent.putExtra(WalkMessage.Outlooptimes, appModel.getOutLooptimes());
		startTestIntent.putExtra(WalkMessage.RcuFileLimitType, ConfigRoutine.getInstance().getSplitType());
		startTestIntent.putExtra(WalkMessage.RucFileSizeLimit, ConfigRoutine.getInstance().getFileSize());
		startTestIntent.putExtra(WalkMessage.KEY_TESTER, tester);
		startTestIntent.putExtra(WalkMessage.KEY_TEST_ADDRESS, testAddress);
		startTestIntent.putExtra(WalkMessage.ISNETSNIFFER, isNetsniffer);
		startTestIntent.putExtra(WalkMessage.KEY_FROM_SCENE, SceneType.Wone.getSceneTypeId());
		
		// 如果是有效的室内测试(有正确的楼层地图)
		String building = "";
		String floor = "";
		if (isIndoortest) {
			if (floorModel != null) {
				building = floorModel.getBuildingName();
				floor = floorModel.getName();
			}
		}
		int subId = queryDetail.getSubId(workOrderDetail, this.selectedPosition);
		DBHelper.getInstance(WorkOrderDetailActivity.this).insertData(subId, 1, subId + "");
		startTestIntent.putExtra(WalkMessage.KEY_TEST_INDOOR, isIndoortest);
		startTestIntent.putExtra(WalkMessage.KEY_TEST_BUILDING, building);
		startTestIntent.putExtra(WalkMessage.KEY_TEST_FLOOR, floor);
		startTestIntent.putExtra(WalkMessage.KEY_TEST_WORK_ORDER_ID, this.workId);
		sendBroadcast(startTestIntent);
		// new WaitStartType().start();
		// 重置时间
		TraceInfoInterface.traceData.setTestStartInfo();
		// 转到信息查看页面:Bundle内容为地图文件路径
		Intent intent = new Intent(WorkOrderDetailActivity.this, NewInfoTabActivity.class);
		if (TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Map
				|| TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Default)
			intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_MAP);
		else
			intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_OTHER);
		startActivity(intent);
		ActivityManager.finishAll();
	}

	/**
	 * 显示楼层外观图
	 * 
	 * @param imgPath
	 */
	private void showImage(String imgPath) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgPath, opts);
		opts.inSampleSize = UtilsMethod.computeSuitedSampleSize(opts, 200 * 200);
		opts.inJustDecodeBounds = false;
		File file = new File(imgPath);
		if (file.exists() && file.isFile()) {
			Bitmap bmp = BitmapFactory.decodeFile(imgPath, opts);
			imageOutView.setImageBitmap(bmp);
		}
	}

	/**
	 * 接收测试完成消息
	 */
	private final BroadcastReceiver testJobDoneReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(GpsInfo.gpsLocationChanged) && gpsInfo.getLocation() != null) {
				IsGpsSearched = true;
				if (gpsTip != null) {
					gpsTip.setText(R.string.main_indoor_gpssucces);
				}
				clearGpstimer();
				if (editLatitude != null) {
					String longitude = StringUtil.formatStr(gpsInfo.getLocation().getLongitude()+"");
					String latitude = StringUtil.formatStr(gpsInfo.getLocation().getLatitude()+"");
					TestInfoValue.latitude = Double.parseDouble(latitude);
					TestInfoValue.longtitude = Double.parseDouble(longitude);
					setPositon(StringUtil.formatStr(gpsInfo.getLocation().getLongitude()+""),
							StringUtil.formatStr(gpsInfo.getLocation().getLatitude()+""));
					if (checkIndoor.isChecked()) {
						// 定位成功以后关闭GPS
						appModel.setGpsTest(false);
						gpsInfo.releaseGps(getApplicationContext(), WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
					}
				}
			} else if (intent.getAction().equals(WalkMessage.ACTION_EXECUTE_TASK)) {
				Log.d(TAG, "get execute task broadcast");
				executeTask();
			}
		}
	};

	/**
	 * 格式化获取的经纬度数值，取到小数点后8位
	 * 
	 * @param s
	 * @return
	 */
//	private String formatStr(String s) {
//		String para = "";
//		if (s.indexOf(".") > 0) {
//			String st = s.substring(s.indexOf(".") + 1, s.length());
//			if (st.length() >= 8) {// 如果小数点后足够8位
//				para = s.substring(0, s.indexOf(".") + 9);
//			} else {// 如果小数点后不足8位
//				para = s;
//			}
//		} else {
//			para = s + ".00000000";
//		}
//		return para;
//	}

	/**
	 * 启动GPS计时器
	 */
	private void startGpsTimer() {
		LogUtil.w(TAG, "---start timer");
		gpsTask = new TimerTask() {
			int time = 0;

			@Override
			public void run() {
				time++;
				LogUtil.w(TAG, "-----wait gps:" + time);
				// 搜索超时，发送消息
				// if(time>=waitGpsTimeOut && checkIndoor.isChecked() ){
				if (time >= waitGpsTimeOut) {
					Message msg = mHandler.obtainMessage(gpsFail);
					mHandler.sendMessage(msg);
					time = 0;
					// gpsTask.cancel();
				}
			}
		};
		gpsTimer.schedule(gpsTask, 0, 1000);
	}

	/**
	 * 清除GPS计时器
	 */
	private void clearGpstimer() {
		// LogUtil.w(tag, "---clear timer");
		if (gpsTask != null) {
			gpsTask.cancel();
			gpsTask = null;
		}
	}

	/**
	 * 开测对话框消失时，一些保持状态清除
	 */
	private void diagDismissClean() {
		startDiagShow = false;
		AlertWakeLock.release();
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
	}

	/*
	 * end*************************************************以上为Walktour开始测试对话框相关处理**
	 * ************************************************end
	 */

	@Override
	protected void handleInitOk() {
		// 初始化服务器失败，则提示。否则，开始获取工单字典的线程
		if (!InitServer.hasInit()) {
			Toast.makeText(WorkOrderDetailActivity.this, R.string.init_server_failed, Toast.LENGTH_LONG).show();
		} else {
			synchWorkOrderDetail();
		}
	}

	/**
	 * 加载整个工单
	 */
	private void synchWorkOrderDetail() {
		showProgressDialog(getString(R.string.getting_sub_work_order_list), false); // 虽然提示加载工单子项，但是实际是加载整个工单
		new Thread(new SynchData()).start();
		new Thread(new MonitorTimeout(DEFAULT_TIMEOUT)).start(); // 启动监视线程
	}

	private class SynchData implements Runnable {

		@Override
		public void run() {
			isObtainFinished = false;
			maniDetail.synchronize();
		}

	}

	private class MonitorTimeout implements Runnable {
		private int timeout;

		public MonitorTimeout(int timeout) {
			this.timeout = timeout;
		}

		@Override
		public void run() {
			int counter = timeout * 1000 / 50;
			for (int i = 1; i <= counter && !isObtainFinished; i++) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// 如果超时了还没执行完，则断开连接
			if (!isObtainFinished) {
				UnicomInterface.disconnect();
				Message msg = mHandler.obtainMessage(EXECUTE_TIMEOUT);
				msg.sendToTarget();
			}
			isObtainFinished = false;
		}

	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "handle message");
			super.handleMessage(msg);
			dismissProgressDialog();
			switch (msg.what) {
			case OBTAIN_DATA_OK:
				fillListView();
				// 如果是室外，就不下载地图了。因为室外不存在地图
				if (workOrderDetail.getWorkArea() == WorkOrderDetail.INDOOR) {
					downloadFloorMaps();
				}
				break;
			case OBTAIN_DATA_FAILURE:
				Toast.makeText(WorkOrderDetailActivity.this, R.string.get_work_order_detail_failure, Toast.LENGTH_LONG).show();
				break;
			case EXECUTE_TIMEOUT:
				Toast.makeText(WorkOrderDetailActivity.this, R.string.connect_timeout, Toast.LENGTH_LONG).show();
				break;
			case DOWNLOAD_MAP_STATUS:
				Log.i(TAG, "download map status!");
				if (msg.arg2 == DOWNLOAD_MAP_SUCCESS) {
					ToastShow.showToast(WorkOrderDetailActivity.this, getString(R.string.download_map_success) + msg.arg1, 1000);
					adapter.notifyDataSetChanged();
				} else if (msg.arg2 == DOWNLOAD_MAP_FAILURE) {
					ToastShow.showToast(WorkOrderDetailActivity.this, getString(R.string.download_map_failure) + msg.arg1, 1000);
					adapter.notifyDataSetChanged();
				} else {
					ToastShow.showToast(WorkOrderDetailActivity.this, getString(R.string.download_map_exist) + msg.arg1, 1000);
					adapter.notifyDataSetChanged();
				}
				isObtainFinished = true;
				break;
			case gpsFail:
				clearGpstimer();
				if (checkIndoor.isChecked()) {
					gpsTip.setText(R.string.main_indoor_gpsfail);
					resetGPS.setVisibility(View.VISIBLE);
					gpsInfo.releaseGps(WorkOrderDetailActivity.this, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
					// Toast.makeText(WorkOrderDetailActivity.this,R.string.main_indoor_opengpsfail2,Toast.LENGTH_SHORT).show();
					new BasicDialog.Builder(WorkOrderDetailActivity.this).setTitle(R.string.main_gps)
							.setIcon(android.R.drawable.ic_lock_power_off).setMessage(R.string.main_indoor_opengpsfail2)
							.setNegativeButton(R.string.str_cancle).show();
				}
				break;
			default:
				break;
			}
		}

	};

	@Override
	public void onStartLoad() {

	}

	@Override
	public void onEndLoad(WorkOrderDetail workOrderDetail) {

	}

	@Override
	public void onStartSynchronize() {

	}

	@Override
	public void onEndSynchronize(WorkOrderDetail workOrderDetail) {
		Message msg = null;
		if (workOrderDetail != null) {
			this.workOrderDetail = workOrderDetail;
			this.workSubItems = workOrderDetail.getWorkSubItems();
			msg = mHandler.obtainMessage(OBTAIN_DATA_OK);
		} else {
			msg = mHandler.obtainMessage(OBTAIN_DATA_FAILURE);
		}
		msg.sendToTarget();
	}

	/**
	 * 保存当前工单及选择的工单子项的相关信息，用作写log标签
	 */
	private void saveLogLabel() {
		// 先获取选择位置，就能得到子工单ID，这样有了工单ID（workId）和子工单ID（subId）就可以获取detail.xml中的相关标签了
		// 开始填充Log标签的值
		Loglabel label = Loglabel.newInstance();
		// 工单相关的值
		label.work_order_id = this.workId + ""; // 工单ID
		if (workOrderDetail != null) {
			label.produc_id = workOrderDetail.getProjectId() + ""; // 项目编号ID
			label.province_code = workOrderDetail.getProvinceId() + ""; // 省编号
			label.city_code = workOrderDetail.getCityId() + ""; // 市编号
			label.scene = workOrderDetail.getWorkArea() == 1 ? "indoor" : "outdoor";

			// 获取选择位置，进而获得子工单
			WorkSubItem sub = workOrderDetail.getWorkSubItems().get(selectedPosition);
			if (sub != null) {
				// 子工单相关的值
				label.work_order_sub = sub.getItemId() + ""; // 子工单ID
				label.photo_md5 = ""; // 楼层地图的md5，等待练志辉那边提供图片的全路径
				label.work_test_type = sub.getWorkTestType() + ""; // 测试日志类型
				label.work_test_scene = sub.getTestScene() + ""; // 测试场景
				label.work_test_mode = "1"; // 测试模式，遍历测试
			}
		}

	}
}
