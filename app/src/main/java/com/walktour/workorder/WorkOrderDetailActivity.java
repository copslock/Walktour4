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
 * ????????????????????????
 * 
 * ????????????????????????????????????????????????????????????GPS??????????????????????????????
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
	// ???11?????????10?????????????????????
	public static final int OBTAIN_DATA_OK = 1100;
	public static final int OBTAIN_DATA_FAILURE = 1101;
	public static final int EXECUTE_TIMEOUT = 1102;
	private static final int DOWNLOAD_MAP_STATUS = 1103;
	private static final int DOWNLOAD_MAP_SUCCESS = 1104;
	private static final int DOWNLOAD_MAP_FAILURE = 1105;
	// private static final int DOWNLOAD_MAP_EXIST = 1106;
	public static final int DEFAULT_TIMEOUT = 60 * 5; // ??????????????????????????????5??????
	private Map<Integer, Boolean> mapState = new HashMap<Integer, Boolean>(); // ????????????
	private ApplicationModel appModel = ApplicationModel.getInstance();

	// ???????????????
	private ManipulateWorkOrderDetail maniDetail = null;
	private QueryWorkOrderDetail queryDetail = null;
	// private WriteLogLabel writeLogLabel = null; // ???Log????????????????????????????????????????????????

	private int workId;
	private WorkOrderDetail workOrderDetail; // ??????????????????????????????????????????????????????????????????????????????????????????????????????
	private List<WorkSubItem> workSubItems = new ArrayList<WorkSubItem>(); // ?????????????????????

	private ListView listView;
	private ListView orderDetailDesclistView; // ??????????????????
	// private TextView orderDetailDescView;
	private RadioAdapter adapter;
	private int selectedPosition = 0; // ??????????????????????????????
	private boolean isObtainFinished = false; // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	private final int waitGpsTimeOut = 20; // GPS????????????
	private GpsInfo gpsInfo;
	private final int gpsFail = 1000;
	private static final int REQ_TAKE_PHOTO = 1;
	private Timer gpsTimer = null; // GPS?????????
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
		mapState.put(0, true); // ?????????????????????
		gpsInfo = GpsInfo.getInstance();
		gpsTimer = new Timer();
		IntentFilter broadCaseIntent = new IntentFilter();
		broadCaseIntent.addAction(GpsInfo.gpsLocationChanged);
		// ??????????????????????????????????????????????????????????????????????????????????????????
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

		// <string name="worktype">????????????</string>
		// <string name="projectid">????????????</string>
		// <string name="project_name">????????????</string>
		// <string name="plan_end_time">??????????????????</string>
		// <string name="sender_account">???????????????</string>
		// <string name="provinceid">??????</string>
		// <string name="cityid">??????</string>
		// <string name="areaid">??????</string>
		// <string name="test_site">????????????</string>
		// <string name="test_building">???????????????</string>
		// <string name="address">??????</string>
		// <string name="site_num">?????????</string>
		// <string name="building_sum">????????????</string>
		// <string name="net_type">????????????</string>
		// <string name="is_received">????????????</string>

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
	 * ????????????????????????????????????ID
	 */
	private void getIntentExtra() {
		if (getIntent().getExtras() != null) {
			workId = getIntent().getExtras().getInt(KEY_WORK_ID);
		}
	}

	/**
	 * ?????????????????????
	 */
	public void findView() {
		(initTextView(R.id.title_txt)).setText(R.string.work_order_detail);
		((ImageButton) findViewById(R.id.pointer)).setOnClickListener(this);
		// RelativeLayout
		// titleLayout=(RelativeLayout)findViewById(R.id.workordertitle);
		// titleLayout.setOnClickListener(this);
		// map.put(0, 0); //??????????????????????????????

		// orderDetailDescView = initTextView(R.id.work_order_detail);
		orderDetailDesclistView = (ListView) findViewById(R.id.list_work_desc);
		listView = (ListView) findViewById(R.id.list_work_order);
		listView.setOnItemClickListener(this);

		TextView workIds = initTextView(R.id.ItemDescrition);
		workIds.setText(String.valueOf(workId));
	}

	/**
	 * ????????????????????????
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
	 * ????????????????????? ?????????????????????
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
					 * listview ????????????
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
	 * ????????????????????????
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

	// <string name="workarea">????????????</string>
	// <string name="worktype">????????????</string>
	// <string name="projectid">????????????</string>
	// <string name="project_name">????????????</string>
	// <string name="plan_end_time">??????????????????</string>
	// <string name="sender_account">???????????????</string>
	// <string name="provinceid">??????</string>
	// <string name="cityid">??????</string>
	// <string name="areaid">??????</string>
	// <string name="test_site">????????????</string>
	// <string name="test_building">???????????????</string>
	// <string name="address">??????</string>
	// <string name="site_num">?????????</string>
	// <string name="building_sum">????????????</string>
	// <string name="net_type">????????????</string>
	// <string name="is_received">????????????</string>

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.workordertitle: // ??????????????????????????????
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

		case R.id.Button01: // ??????????????????
			Log.i(TAG, "click button01");
			try {
				obtainDetail();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case R.id.Button02: // ????????????
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
		// ??????????????????????????????????????????????????????????????????????????????????????????
		if (!InitServer.hasInit()) {
			Log.i(TAG, "init server");
			initServer(); // ??????????????????????????????
		} else {
			Log.i(TAG, "synchWorkOrderDetail");
			synchWorkOrderDetail();
		}
	}

	/**
	 * ????????????????????????
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
	 * ????????????
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

		// ???????????????log????????????????????????????????????????????????????????????????????????????????????????????????????????????
		// writeLogLabel = new WriteLogLabel(this, this.workOrderDetail,
		// this.selectedPosition);
		// writeLogLabel.write();

		// ???????????????log????????????????????????????????????????????????????????????????????????????????????????????????????????????
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
			account = serverInfo.getAccount(); // ?????????ftp???????????????
			password = serverInfo.getPassword(); // ?????????ftp????????????
			Log.i(TAG, "ip:" + serverInfo.getIpAddr() + ", port:" + serverInfo.getPort() + ", account:" + account
					+ ", password:" + password);
		}

		@Override
		public void run() {
			Message msg = null;

			// ??????????????????????????????
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
	 * ??????????????????????????????
	 */
	private void getTaskPlan() {
		// int position = getSelectedItemPosition();
		// ??????????????????????????????????????????ID
		if (workSubItems.size() == 0) {
			// ???????????????????????????
			Toast.makeText(getApplicationContext(), getString(R.string.get_sub_work_order_first), Toast.LENGTH_SHORT).show();
			// "????????????????????????!"
		}
		int subId = queryDetail.getSubId(workOrderDetail, this.selectedPosition);

		if (workSubItems != null) {
			ArrayList<TaskModel> modelList = (ArrayList<TaskModel>) queryDetail.getTask(this.workOrderDetail, subId);

			if (modelList.size() > 0) {
				// ????????????????????????????????????????????????
				TaskListDispose taskList = TaskListDispose.getInstance();
				taskList.replaceTaskList(modelList);
			}
			loopTime = queryDetail.getLoopTime(this.workOrderDetail, subId); // ???????????????

			// int loopInterval = queryDetail.getLoopInterval(this.workOrderDetail,
			// subId); // ?????????????????????????????????
			// String building = queryDetail.getBuilding(this.workOrderDetail); //
			// ???????????????
			// String floor = queryDetail.getFloor(this.workOrderDetail, subId); //
			// ????????????
			// String floorMapUrl = queryDetail.getFloorMapUrl(this.workOrderDetail,
			// subId); // ?????????ftp????????????
			// String floorMapName=queryDetail.getFloorMap(this.workOrderDetail,
			// subId);
		} else {
			Toast.makeText(getApplicationContext(), R.string.get_sub_work_order_first, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * ??????????????????????????????????????? ??????
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
	 * ?????????????????????????????????
	 * 
	 * @return ??????
	 */
	// private int getSelectedItemPosition() {
	// int position = 0;
	// if (map != null) { // ????????????????????????
	// Iterator iter = map.entrySet().iterator();
	// while (iter.hasNext()) {
	// Map.Entry entry = (Map.Entry) iter.next();
	// position = (Integer) entry.getKey();
	// }
	// }
	// return position;
	// }

	/*
	 * start*************************************************?????????Walktour?????????????????????????????????
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
	private String tester = "";// ????????????
	private String testAddress = "";// ????????????
	private final String defaultStr = "N/A";

	// private boolean isIndoorTest = false; /*???????????????????????????*/
	/**
	 * ????????????????????????
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
		private ConfigIndoor configIndoor = ConfigIndoor.getInstance(WorkOrderDetailActivity.this); /* ???????????? */
		private boolean isIndoorTest = false; /* ??????????????????????????? */
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
			// GPS???????????????????????????
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
			/******* ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? ************/
			// ???????????????
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
			// ????????????????????????
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
			// ?????????????????????
			ApplicationModel.getInstance().setIndoorTest(false);

			// ????????????????????????
			checkIndoor.setOnCheckedChangeListener(checkListener);
			// Gps??????
			checkGps.setOnCheckedChangeListener(checkListener);

			// Gps??????
			checkPreviously.setOnCheckedChangeListener(checkListener);
			// checkPreviously.setChecked(false);

			// ???GPS????????????
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
			building = queryDetail.getBuilding(WorkOrderDetailActivity.this.workOrderDetail); // ???????????????
			floor = queryDetail.getFloor(WorkOrderDetailActivity.this.workOrderDetail, subId); // ????????????
			floorMapName = queryDetail.getFloorMap(WorkOrderDetailActivity.this.workOrderDetail, subId); // ????????????
			txtBuild.setText(floor == null ? "" : building);

			editLooptimes.setText(loopTime == 0 ? "1" : String.valueOf(loopTime));
			// setPositon(defaultStr, defaultStr);
			// //??????GPS??????
			// gpsInfo.openGps(WorkOrderDetailActivity.this,
			// WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
			// //??????GPS?????????
			// startGpsTimer();
			// gpsTip.setText(R.string.main_indoor_waitgps);

			IsGpsSearched = false;

			initial();

		}// end constructor

		/**
		 * ???????????????
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
		 * ???????????????
		 */

		private void initial() {
			// ?????? ??????????????????????????????
			getAllfloorPicList();
			if (floorPicList.size() > 0) {
				adapter = new ImageAdapter(WorkOrderDetailActivity.this, floorPicList);
				gallery.setAdapter(adapter);
				gallery.setOnItemSelectedListener(new GallerySelectListener());
				if (!adapter.isEmpty()) {
					isIndoorTest = true; // ??????????????????????????????????????????
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
			 * configIndoor.getBuildings(); //????????????????????????????????? //?????? ?????????????????????????????? List<String
			 * > buildingNames = new ArrayList<String >(); for(int
			 * i=0;i<buildingList.size();i++){ buildingNames.add(
			 * buildingList.get(i).getName() ); } String[] names= new
			 * String[buildingNames.size()+1];
			 * names[0]=context.getString(R.string.none); //????????? int i=1; for(String
			 * name:buildingNames){ names[i++]=name; }
			 * buildingNames=Arrays.asList(names); if( buildingList.size()==0 ){
			 * 
			 * buildingNames.add( getString(R.string.none) ); } final
			 * ArrayAdapter<String> adptBuild = new ArrayAdapter<String>( context,
			 * R.layout.simple_spinner_custom_layout, buildingNames );
			 * adptBuild.setDropDownViewResource(R.layout.spinner_dropdown_item);
			 * 
			 * spinnerBuild.setAdapter( adptBuild ); //?????????????????????????????????????????????????????????????????????
			 * spinnerBuild.setOnItemSelectedListener(new OnItemSelectedListener(){
			 * private ArrayList<FloorModel> floorList;
			 * 
			 * @Override public void onItemSelected(AdapterView<?> parent, View view,
			 * int position, long id) { //??????????????????Spinner????????? floorList = new ArrayList<
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
		 * ?????????????????? ???????????????????????? ????????????????????????
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
		// //??????????????????
		// if(!outsideMaps.isEmpty()){
		// imgTip.setVisibility(View.GONE);
		// imgTip.setText("");
		// showImage(outsideMaps.get(0));
		// }else{//?????????????????????
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
		// isIndoorTest = true; //??????????????????????????????????????????
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
		 * ??????GPS??????
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
				// ????????????????????????,?????????????????????ibwave??????
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
							// ???????????????CQT???????????????????????????????????????
							if (checkIndoor.isChecked() && !IsGpsSearched) {
								Toast.makeText(WorkOrderDetailActivity.this, R.string.gps_fail_do_nothing, Toast.LENGTH_LONG).show();
								return;
							}
							dialog.dismiss();

							// ???????????????????????????????????????0?????????1
							// boolean check =
							// editLooptimes.getText().toString().equals("")||editLooptimes.getText().toString().equals("0");
							appModel.setOutLooptimes(loopTime);
							tester = editTester.getText().toString().trim();
							testAddress = editAddress.getText().toString().trim();

							// ????????????????????????
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

							// ?????????????????????
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
						 * dialog.dismiss(); //????????????????????????????????? clearGpsStatus(); } return false;
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

			// ?????????????????????s
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
					// sd???????????????
					if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						Toast.makeText(WorkOrderDetailActivity.this, R.string.sdcard_unmount, Toast.LENGTH_SHORT).show();
						return;
					}
					doTakePhoto();
					break;
				case R.id.btnResetGPS:
					// ??????GPS??????
					gpsInfo.openGps(WorkOrderDetailActivity.this, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
					// ??????GPS?????????
					startGpsTimer();
					gpsTip.setText(R.string.main_indoor_waitgps);
					resetGPS.setVisibility(View.GONE);
					break;

				}
			}
		};

		/**
		 * ????????????intent?????????????????????
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
					// ???????????????
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
						// ???????????????
						checkPreviously.setVisibility(View.GONE);
						textPreviously.setVisibility(View.GONE); // ??????????????????
						appModel.setGpsTest(true);
						appModel.setPreviouslyTest(false);
						// ??????GPS???????????????????????????
						if (!gpsInfo.isJobTestGpsOpen()) {
							setPositon(defaultStr, defaultStr);
							gpsInfo.openGps(WorkOrderDetailActivity.this, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
							gpsTip.setText(R.string.main_indoor_waitgps);
							resetGPS.setVisibility(View.GONE);
						}
						// GPS????????????
						if (!appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)) {
							gpsLinearlayout.setVisibility(View.VISIBLE);
						}
						// ??????????????????
						gpsTip.setText(R.string.main_indoor_waitgps);
					} else {
						checkIndoor.setChecked(false);
						appModel.setGpsTest(false);
						textPreviously.setVisibility(View.VISIBLE);
						// ???????????????
						checkPreviously.setVisibility(View.VISIBLE);
						// ??????GPS??????
						gpsInfo.releaseGps(WorkOrderDetailActivity.this, WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
						if (gpsInfo.getLocation() == null) {
							gpsTip.setText(R.string.main_indoor_gpsnosearch);
						}
						// ??????GPS?????????
						clearGpstimer();
						// ????????????????????????GPS??????????????????gps????????????????????????????????????
						if (!IsGpsSearched) {
							// //??????????????????GPS????????????GPS???????????????
							// if(!appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)){
							// gpsLinearlayout.setVisibility(View.GONE);
							// }
							gpsTip.setText(R.string.main_indoor_waitgps);
						}
					}
					/**
					 * else{ LogUtil.w(tag, "**********set gps to falses");
					 * checkIndoor.setChecked(false); appModel.setGpsTest(false);
					 * textPreviously.setVisibility(View.VISIBLE); //???????????????
					 * checkPreviously.setVisibility(View.VISIBLE); //??????GPS??????
					 * gpsInfo.releaseGps(WalkTour.this,
					 * WalkCommonPara.OPEN_GPS_TYPE_JOBTEST); if(gpsInfo.getLocation() ==
					 * null){ gpsTip.setText(R.string.main_indoor_gpsnosearch); }
					 * //??????GPS????????? clearGpstimer(); //????????????????????????GPS??????????????????gps????????????????????????????????????
					 * if(!IsGpsSearched){ // //??????????????????GPS????????????GPS??????????????? //
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
					longitude = location.getLongitude(); // ??????
					latitude = location.getLatitude(); // ??????
					setPositon(String.valueOf(longitude), String.valueOf(latitude));
					Location location2 = new Location(LocationManager.GPS_PROVIDER); // ???BDLocation???Location
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

		// ??????????????????
		public void setLocationOption() {
			LocationClientOption option = new LocationClientOption();
			option.setCoorType("wgs84"); // ??????????????????
			option.setScanSpan(2000);
			// option.setsetPriority(LocationClientOption.MIN_SCAN_SPAN_NETWORK); //
			// ??????????????????
			option.setOpenGps(true); // ??????gps
			// option.disableCache(true);
			if (mLocationClient != null) {
				mLocationClient.setLocOption(option);
			}
		}
	}// end class StartDialog

	/**
	 * ?????????????????????
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
				// ???????????????????????????
				configIndoor.delete(desFileFullPath);
				// ??????????????????????????????????????????camera?????????
				configIndoor.setMap(latestfile.getAbsolutePath(), desFileFullPath);
			} else {
				LogUtil.w(TAG, "----floorModel is null--");
			}
		}
	}

	/**
	 * ????????????
	 */
	private void startTest(boolean isIndoortest, boolean isNetsniffer) {

		// ??????W-One???????????????????????????
		Loglabel.newInstance().start_time = UtilsMethod.sdFormat.format(new Date());

		// ??????GPS???????????????????????????????????????????????????????????????????????????
		if (!checkGps.isChecked()) {
			// ???????????????????????????
			appModel.setGerenalTest(true);
			// ????????????GPS??????
			appModel.setGpsTest(false);

			LogUtil.w(TAG, "----is general test!");
		} else {
			LogUtil.w(TAG, "----is not general test!");
			appModel.setGerenalTest(false);
		}
		appModel.setWoneTest(true);
		LogUtil.w(TAG, "----checkPreviously.isChecked=" + checkPreviously.isChecked());
		// 2012.04.19????????????????????????
		appModel.setNeedToCleanMap(!checkPreviously.isChecked());
		// ?????????????????????????????????????????????????????????????????????
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
			// 2012.04.19????????????????????????
			// appModel.setNeedToCleanMap(false);
		}
		if (appModel.isGyroTest()) {
			// ???????????????????????????????????????????????????
			// RecordTraceService.setAdjust(false);
			MapFactory.getMapData().getPointStatusStack().clear();
			appModel.setNeedToCleanMap(false);
		}
		MapFactory.getMapData().setZoomGrade(10);
		MapFactory.getMapData().setScale(1);
		MapFactory.getMapData().setSampleSize(1);
		MapFactory.setLoadIndoor(isIndoortest);
		// if(isIndoortest){ ????????????
		// TraceInfoDispose.currentShowChildTab = WalkStruct.ShowInfoType.OtherMap;
		// }
		// SavedMapData.MapData.flag_toggle = false;
		// ????????????????????????????????????
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
		
		// ??????????????????????????????(????????????????????????)
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
		// ????????????
		TraceInfoInterface.traceData.setTestStartInfo();
		// ????????????????????????:Bundle???????????????????????????
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
	 * ?????????????????????
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
	 * ????????????????????????
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
						// ????????????????????????GPS
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
	 * ??????????????????????????????????????????????????????8???
	 * 
	 * @param s
	 * @return
	 */
//	private String formatStr(String s) {
//		String para = "";
//		if (s.indexOf(".") > 0) {
//			String st = s.substring(s.indexOf(".") + 1, s.length());
//			if (st.length() >= 8) {// ????????????????????????8???
//				para = s.substring(0, s.indexOf(".") + 9);
//			} else {// ????????????????????????8???
//				para = s;
//			}
//		} else {
//			para = s + ".00000000";
//		}
//		return para;
//	}

	/**
	 * ??????GPS?????????
	 */
	private void startGpsTimer() {
		LogUtil.w(TAG, "---start timer");
		gpsTask = new TimerTask() {
			int time = 0;

			@Override
			public void run() {
				time++;
				LogUtil.w(TAG, "-----wait gps:" + time);
				// ???????????????????????????
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
	 * ??????GPS?????????
	 */
	private void clearGpstimer() {
		// LogUtil.w(tag, "---clear timer");
		if (gpsTask != null) {
			gpsTask.cancel();
			gpsTask = null;
		}
	}

	/**
	 * ???????????????????????????????????????????????????
	 */
	private void diagDismissClean() {
		startDiagShow = false;
		AlertWakeLock.release();
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
	}

	/*
	 * end*************************************************?????????Walktour?????????????????????????????????**
	 * ************************************************end
	 */

	@Override
	protected void handleInitOk() {
		// ?????????????????????????????????????????????????????????????????????????????????
		if (!InitServer.hasInit()) {
			Toast.makeText(WorkOrderDetailActivity.this, R.string.init_server_failed, Toast.LENGTH_LONG).show();
		} else {
			synchWorkOrderDetail();
		}
	}

	/**
	 * ??????????????????
	 */
	private void synchWorkOrderDetail() {
		showProgressDialog(getString(R.string.getting_sub_work_order_list), false); // ??????????????????????????????????????????????????????????????????
		new Thread(new SynchData()).start();
		new Thread(new MonitorTimeout(DEFAULT_TIMEOUT)).start(); // ??????????????????
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
			// ????????????????????????????????????????????????
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
				// ?????????????????????????????????????????????????????????????????????
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
	 * ?????????????????????????????????????????????????????????????????????log??????
	 */
	private void saveLogLabel() {
		// ?????????????????????????????????????????????ID?????????????????????ID???workId???????????????ID???subId??????????????????detail.xml?????????????????????
		// ????????????Log????????????
		Loglabel label = Loglabel.newInstance();
		// ??????????????????
		label.work_order_id = this.workId + ""; // ??????ID
		if (workOrderDetail != null) {
			label.produc_id = workOrderDetail.getProjectId() + ""; // ????????????ID
			label.province_code = workOrderDetail.getProvinceId() + ""; // ?????????
			label.city_code = workOrderDetail.getCityId() + ""; // ?????????
			label.scene = workOrderDetail.getWorkArea() == 1 ? "indoor" : "outdoor";

			// ??????????????????????????????????????????
			WorkSubItem sub = workOrderDetail.getWorkSubItems().get(selectedPosition);
			if (sub != null) {
				// ?????????????????????
				label.work_order_sub = sub.getItemId() + ""; // ?????????ID
				label.photo_md5 = ""; // ???????????????md5????????????????????????????????????????????????
				label.work_test_type = sub.getWorkTestType() + ""; // ??????????????????
				label.work_test_scene = sub.getTestScene() + ""; // ????????????
				label.work_test_mode = "1"; // ???????????????????????????
			}
		}

	}
}
