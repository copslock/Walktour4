package com.walktour.gui;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.dinglicom.data.control.BuildWhere;
import com.dinglicom.data.control.DataTableStruct.DataTableMap;
import com.dinglicom.data.control.DataTableStruct.TestRecordEnum;
import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.TestRecord;
import com.dinglicom.dataset.EventManager;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.BuildPower;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.ServerMessage;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalktourConst;
import com.walktour.Utils.WifiTools;
import com.walktour.Utils.ZipUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigAutoTest;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.instance.FileDB;
import com.walktour.gui.setting.sysroutine.SysRoutineActivity;
import com.walktour.gui.share.UpDownService;
import com.walktour.gui.share.logic.RegisterDeviceLogic;
import com.walktour.gui.share.logic.ShareHttpRequestUtil;
import com.walktour.gui.share.model.BaseResultInfoModel;
import com.walktour.gui.share.model.DeviceInfoModel;
import com.walktour.gui.share.model.DeviceInfoModel.Device;
import com.walktour.gui.share.model.GroupInfoModel;
import com.walktour.gui.share.model.UnreadModel;
import com.walktour.gui.share.upload.UploadManager;
import com.walktour.gui.weifuwu.business.model.ShareDeviceModel;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.business.model.ShareGroupModel;
import com.walktour.gui.weifuwu.business.model.ShareGroupRelationModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
import com.walktour.gui.weifuwu.view.refreshlistview.ListViewModel;
import com.walktour.service.ApplicationInitService;
import com.walktour.service.TestTaskPlanService;
import com.walktour.service.TraceService;
import com.walktour.service.app.AutoTestService;
import com.walktour.service.app.Killer;
import com.walktour.service.phoneinfo.utils.MobileUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressLint("SdCardPath")
public class WalkBroadcastReceiver extends BroadcastReceiver {
	private final static String tag = "WalktourBroadcastReceiver";
	private final static int powerSucc = 0;
	private final static int finishApp = 1;
	private static boolean bootFlag = false;// 添加初次启动标志
	public static boolean uyouReadyStart = false; // U-You是否将要启动的标志
	private ApplicationModel appModel = ApplicationModel.getInstance();
	private final static List<ScanResult> listScanResult = new LinkedList<ScanResult>();
	private String currentAP = null;
	private Context context;
	private int NOTIFICATION_FLAG = 0X097582;
	private Lock lock = new ReentrantLock();
	/***
	 * 挂载时间间隔为30秒
	 */
	private long times=30;
	/**
	 * 是否第一次启动
	 */
	public static boolean isBootFlag() {
		if (bootFlag) {
			bootFlag = false;
			return true;
		}
		return bootFlag;
	}
	/**
	 * 当用户鉴权成功时，启动监控服务
	 */
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Context context = (Context) msg.obj;
			if (msg.what == finishApp) {
				Toast.makeText(context, R.string.main_license_check_faild, Toast.LENGTH_LONG).show();
			} else if (msg.what == powerSucc) {
			}
		}
	};
	/**
	 * 当监控程序设置为开机启动，开机时通过线程先初始化用户的权限再启动相关服务
	 * 
	 * @author tangwq
	 */
	private class AutoStartThread extends Thread {
		private Context context;
//		private String appPath;
		private String deviceId;
		public AutoStartThread(Context _context, String _deviceId) {
			context = _context;
//			appPath = _appPath;
			deviceId = _deviceId;
		}
		public void run() {
			int checkPower = new BuildPower().checkUserPower(context, deviceId);
			Message msg;
			LogUtil.w(tag, "---checkPowerResult:" + checkPower);
			if (checkPower == 0) {
				msg = mHandler.obtainMessage(powerSucc);
			} else {
				msg = mHandler.obtainMessage(finishApp);
			}
			msg.obj = context;
			mHandler.sendMessage(msg);
		}
	}
	/**
	 * 从U-You的共享文件中获取U-You是否是开机启动
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	private boolean getShareValue(Context context) {
		try {
			Context con = context.createPackageContext("com.dingli.uyou", Context.CONTEXT_IGNORE_SECURITY);
			SharedPreferences p = con.getSharedPreferences("com.dingli.uyou.autostart", Context.MODE_WORLD_READABLE);
			return p.getBoolean("autostart", false);
		} catch (Exception e) {
			return false;
		}
	}
	private void wakeAndUnlock(boolean b)
	{
		PowerManager.WakeLock wl = null;
		KeyguardManager.KeyguardLock kl = null;
		if(b)
		{
			//获取电源管理器对象
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

			//获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
			wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

			//点亮屏幕
			wl.acquire();

			//得到键盘锁管理器对象
			KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
			kl = km.newKeyguardLock("unLock");

			//解锁
			kl.disableKeyguard();
		}
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			bootFlag = true;
			// 如果系统监控功能设为开机时启动，则启动监控服务，不出界面
			Boolean uyouAutostart = getShareValue(context);
			LogUtil.w(tag, "----uyouAutostart=" + uyouAutostart);
//			ToastUtil.showToastLong(context,"收到了启动广播");
			if (SharePreferencesUtil.getInstance(context).getBoolean(WalktourConst.IS_NEED_REBOOT)){
				Intent startAppIntent=new Intent(context,Main.class);
				startAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(startAppIntent);
				SharePreferencesUtil.getInstance(context).saveBoolean(WalktourConst.IS_NEED_REBOOT,false);
			}
			wakeAndUnlock(true);
			if (!uyouAutostart) {
				try {
					ConfigAutoTest config = new ConfigAutoTest();
					LogUtil.w(tag, "---c.isAutoTestOn=" + config.isAutoTestOn());
					if (config.isAutoTestOn()) {
//						String appPath = context.getFilesDir().getAbsolutePath();
						// String deviceId =
						// MyPhoneState.getInstance().getDeviceId(context);
						// 修改获取手机标识为使用自定义方法，CDMA下取蓝牙加密16位，其它取IMEI
						String deviceId = MyPhoneState.getInstance().getMyDeviceId(context);
						new AutoStartThread(context, deviceId).start();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//重启手机
		if (intent.getAction().equals(Intent.ACTION_REBOOT)) {
			try {
				LogUtil.i(tag, "---reboot");
				context.stopService(new Intent(context, TraceService.class));
			} catch (Exception e) {
				LogUtil.i(tag, e.getMessage()+"");
			}
		}
		// 去掉彩信的自动下载功能提示
		if (intent.getAction().equals(WalkMessage.ACTION_MMS_AUTODOWNLOAD)) {
			// cmp=com.android.mms/.ui.MessagingPreferenceActivity } from pid
			// 17367
			Intent intentActivity = new Intent("android.intent.action.MAIN");
			ComponentName componentName = new ComponentName("com.android.mms",
					"com.android.mms.ui.MessagingPreferenceActivity");
			intentActivity.setComponent(componentName);
			intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 清除
			context.startActivity(intentActivity);
		}
		if (intent.getAction().equals(WalkMessage.QUERY_NETWORKTYPE_ACTION)) {
			int type = intent.getExtras().getInt(WalkMessage.NETWORKTYPE_KEY, 0);
			LogUtil.w(tag, "--->receiver from WalktourPhone,NetworkType:" + type);
			MyPhoneState.getInstance().setPreferredNetworkType(type);
		}
		if (intent.getAction().equals(WalkMessage.telephonySMSReceived)) {
			LogUtil.w(tag, "-----telephonySMSReceived----");
			if(ConfigRoutine.getInstance().isSMSInfo(context)) {
				Bundle bundle = intent.getExtras();
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] msgs = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}

				if (null != msgs && msgs.length > 0) {
					String sender = "";
					String smsMsg = "";
					for (SmsMessage msg : msgs) {
						sender = msg.getOriginatingAddress();
						smsMsg += msg.getMessageBody();

					}
					if (sender.equals("10086") || sender.equals("10001") || sender.equals("10010")) {
						EventManager.getInstance().addTagEvent(context, System.currentTimeMillis(), smsMsg);
					}
				}

			}
			ApplicationModel.getInstance().setHasReceiveSMS(true);
		}
		// 数据服务的APN设置有误
		if (intent.getAction().equals(ServerMessage.ACTION_FLEET_APN)) {
			Intent intentActivity = new Intent("android.intent.action.MAIN");
			ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.ApnSettings");
			intentActivity.setComponent(componentName);
			intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 清除
			context.startActivity(intentActivity);
		}
		// 接收UYou启动的广播
		if (intent.getAction().equals(WalkMessage.START_UYOU)) {
			uyouReadyStart = true;// U-You将要启动
			LogUtil.w(tag, "---to kill walktour--");
			/*
			 * ActivityManager am =
			 * (ActivityManager)context.getSystemService(Context.
			 * ACTIVITY_SERVICE); List<RunningTaskInfo> list =
			 * am.getRunningTasks(4); //跳回到walktour首页 for(RunningTaskInfo
			 * task:list){ LogUtil.w(tag,
			 * "---r.baseactivity="+task.baseActivity.getPackageName());
			 * if(task.baseActivity.getPackageName().equals(context.
			 * getPackageName())){ LogUtil.w(tag,
			 * "---r.topActivity="+task.topActivity.getClassName()); try {
			 * Intent it = new Intent(); ComponentName component = new
			 * ComponentName(context.getPackageName(),task.baseActivity.
			 * getClassName()); it.setComponent(component);
			 * it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 * it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * context.startActivity(it); break; } catch (Exception e) { //
 			 *	}
			 * 
			 * } }
			 */
			// 如果正在测试，发送停止测试广播，先停止测试
			if (appModel.isTestJobIsRun()) {
				Intent interruptIntent = new Intent(WalkMessage.Action_Walktour_Test_Interrupt);
				context.sendBroadcast(interruptIntent);
			}
			try {
				// 通过文件目录检测Walktour已经启动
				File f = new File(context.getFilesDir().getAbsolutePath() + "/config");
				if (f.isDirectory()) {
					ConfigAutoTest config = new ConfigAutoTest();
					// 如果正在执行自动测试，那么关闭自动测试
					if (config.isAutoTestOn() || ApplicationModel.getInstance().isTesting()) {
						config.setAutoOpen(false);
						context.sendBroadcast(new Intent(ServerMessage.ACTION_FLEET_DOWNLOAD_STOP));
						context.sendBroadcast(new Intent(AutoTestService.ACTION_FLEET_TRIGGLE_AUTOTEST));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 启动子线程
			new WaitStopThread(context).start();
		}
		// 准备启动U-You，将walktour的测试状态广播给U-You
		if (intent.getAction().equals(WalkMessage.READY_UYOU)) {
			int testStatus = appModel.isTestJobIsRun() ? 1 : -1;
			Intent ie = new Intent();
			ie.setAction(WalkMessage.WALKTOUR_STATUS);
			ie.putExtra("status", testStatus);
			context.sendBroadcast(ie);
		}
		// U-You已经启动完成，将状态位置为false
		if (intent.getAction().equals(WalkMessage.UYOU_HAS_START)) {
			uyouReadyStart = false;// U-You已经启动完成
			LogUtil.w(tag, "-----uyouReadyStart2222=" + uyouReadyStart);
		}
		// 服务器等设置有误,跳到设置页面
		if (intent.getAction().equals(ServerMessage.ACTION_FLEET_SERVER_NOTSET)) {
			Intent intentActivity = new Intent(context, SysRoutineActivity.class);
			// com.walktour.gui/.setting.SystemFleetUpload: 247 ms (total 247
			// ms)
			intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intentActivity);
		}
		// 平台交互控制--》启动测试计划扫描
		if (intent.getAction().equals(ServerMessage.ACTION_PLATFORM_CONTROL_TESTPLAN_START)) {
			context.startService(new Intent(context, TestTaskPlanService.class));
		}
		// 平台交互控制--》停止测试计划扫描
		if (intent.getAction().equals(ServerMessage.ACTION_PLATFORM_CONTROL_TESTPLAN_STOP)) {
			context.stopService(new Intent(context, TestTaskPlanService.class));
		}
		// WIFI的监测
		if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
			switch (wifiState) {
			case WifiManager.WIFI_STATE_ENABLED:// 开启wifi
				// context.startService(new Intent(context,
				// WifiScanService.class));
				LogUtil.d(tag, "Wifi open");
				appModel.setWifiOpen(true);
				break;
			case WifiManager.WIFI_STATE_DISABLED:// 关闭wifi
				// context.stopService(new Intent(context,
				// WifiScanService.class));
				LogUtil.d(tag, "Wifi close");
				appModel.setWifiOpen(false);
				currentAP = null;
				break;
			}
		}
		// WIFI的监测--接入点扫描(只有在任务执行过程中才写事件)
		if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
			if (ApplicationModel.getInstance().isTestJobIsRun()) {
				doWifiApScan(context);
			}
		}
		// WIFI的监测--接入点扫描(只有在任务执行过程中才写事件)
		if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			if (ApplicationModel.getInstance().isTestJobIsRun()) {
				doWifiMain(context);
			}
		}
		// 共享信息的广播接收器
		if (intent.getAction().equals(ServerMessage.ACTION_SHARE_SEND_CONTROL)) {
			// 返回的是一串json数据
			String messageType = intent.getExtras().getString("messageType");
//			ToastUtil.showToastLong(context, messageType + "");
			try {
				lock.lock();
				JSONObject jsonObject = new JSONObject(messageType);
				String body = jsonObject.getString("body");
				int tag = jsonObject.getInt("dl_tag");
				int type = jsonObject.getInt("dl_type");
				String obj = jsonObject.getString("dl_object");
				switch (tag) {
				case 1:// 处理文件
					showNotification(this.context.getString(R.string.share_project_devices_release_relation_1), body);
					new UpdateFile().execute();
					break;
				case 2:// 处理群组
					showNotification(this.context.getString(R.string.share_project_devices_release_relation_2), body);
					new UpdateGroup(type, obj).execute();
					break;
				case 3:// 好友变更消息
					showNotification(this.context.getString(R.string.share_project_devices_release_relation_3), body);
					new ConfirmDevice(type, obj).execute();
					break;
				default:
					break;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally{
				lock.unlock();
			}
		}
		// 共享信息发送大数据
		if (intent.getAction().equals(ServerMessage.ACTION_SHARE_SEND_CONTROL_DATA)) {
			ArrayList<String> recordIDS = intent.getStringArrayListExtra("recordIDS");
			ArrayList<ListViewModel> listDevices = intent.getParcelableArrayListExtra("listDevices");
			shareDATA(recordIDS, listDevices);
		}
		rebootTrace(intent);
	}


	/**
	 * 发送重启串口服务
	 */
//	private void rebootTraceBroadcast(){
//		Intent intent = new Intent();
//		intent.setAction(ServerMessage.ACTION_REBOOT_TRACE);
//		context.sendOrderedBroadcast(intent,null);
//	}


//	private void showNormalDialog(){
//		final AlertDialog.Builder normalDialog =
//				new AlertDialog.Builder(this.context);
//		normalDialog.setIcon(R.drawable.controlbar_up);
//		normalDialog.setTitle(R.string.main_exit);
//		normalDialog.setMessage(R.string.reboot_app);
//		normalDialog.setPositiveButton(R.string.str_ok,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						//...To-do
//					}
//				});
//		normalDialog.show();
//	}
	/**
	 * 重启串口
	 */
	private void rebootTrace(Intent intent){
		if(intent.getAction().equals(ServerMessage.ACTION_REBOOT_TRACE)) {
			//重启默认串口服务
			appModel.setTraceInitSucc(false);
			Intent traceService = new Intent(context, TraceService.class);
			LogUtil.w(tag, "---rebootTrace to stopService---");
			context.stopService(traceService);
			try {
				Thread.sleep(1000 * 10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			LogUtil.w(tag, "---rebootTrace to reStartService--");
			context.startService(traceService);
		}
	}

	@SuppressWarnings("deprecation")
	private void showNotification(String title, String message) {
		NotificationManager manager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent pendingIntent2 = PendingIntent.getActivity(this.context, 0,
				new Intent(this.context, WalkTour.class), 0);
		// 通过Notification.Builder来创建通知，注意API Level
		// API11之后才支持
		Notification notify2 = new Notification.Builder(this.context).setSmallIcon(R.mipmap.walktour) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
				// icon)
				.setTicker("TickerText:" + "您有新短消息，请注意查收！")// 设置在status
															// bar上显示的提示文字
				.setContentTitle(title + "")// 设置在下拉status
											// bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
				.setContentText(message + "")// TextView中显示的详细内容
				.setContentIntent(pendingIntent2) // 关联PendingIntent
				// .setNumber(1) //
				// 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
				.getNotification(); // 需要注意build()是在API level
		notify2.flags |= Notification.FLAG_AUTO_CANCEL;
		manager.notify(NOTIFICATION_FLAG, notify2);
		NOTIFICATION_FLAG += 1;
	}
	/***
	 * 数据管理 共享文件没有历史
	 */
	private void shareDATA(ArrayList<String> recordIDS, ArrayList<ListViewModel> listDevices) {
		try {
			new SendProject(recordIDS, listDevices, ShareFileModel.FILETYPE_DATA).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/***
	 * 发送数据
	 * 
	 * @author weirong.fan
	 *
	 */
	private class SendProject extends AsyncTask<Void, Void, List<BaseResultInfoModel>> {
		private List<Long> IDS = new LinkedList<Long>();
		private List<String> recordIDS = null;
		private List<ListViewModel> listDevices;
		private StringBuffer toDeviceCodes = new StringBuffer();
		private StringBuffer toGroupCodes = new StringBuffer();
		/** 共享的文件类型 **/
		private int fileType = -1;
		private File zipFile;
		private List<BaseResultInfoModel> listResults = new LinkedList<BaseResultInfoModel>();
		private SendProject(List<String> recordIDS, List<ListViewModel> listDevices, int fileType) {
			super();
			// 获取要发送的文件数据
			this.recordIDS = recordIDS;
			this.listDevices = listDevices;
			this.fileType = fileType;
		}
		@Override
		protected void onPostExecute(List<BaseResultInfoModel> results) {
			super.onPostExecute(results);
			if (null == results)
				return;
			boolean flag = true;
			for (int k = 0; k < results.size(); k++) {
				BaseResultInfoModel result = results.get(k);
				if (result.getReasonCode() == 1) {// 判断网络
					if (result.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {// 分享成功
						try {
							ShareFileModel model = ShareDataBase.getInstance(context).fetchFile(IDS.get(k));
							model.setFileID(result.getFile_id());
							model.setFileTotalSize(zipFile.length());
							model.setFileStatus(ShareFileModel.FILE_STATUS_START);
							model.setFileRealSize(0);
							ShareDataBase.getInstance(context).updateFile(model);
							model = ShareDataBase.getInstance(context).fetchFile(IDS.get(k));
							UploadManager um = UpDownService.getUploadManager();
							um.startUpload(model);
						} catch (Exception e) {
							if (flag)
								flag = false;
							e.printStackTrace();
						}
					} else {
						if (flag)
							flag = false;
					}
				} else {
					if (flag)
						flag = false;
				}
				if (!flag) {
					ToastUtil.showToastShort(context, context.getString(R.string.share_project_failure));
				}
			}
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected List<BaseResultInfoModel> doInBackground(Void... arg0) {
			try {
				for (String recordID : recordIDS) {
					toDeviceCodes.setLength(0);
					toGroupCodes.setLength(0);
					// 发送的端
					for (int i = 0; i < listDevices.size(); i++) {
						ListViewModel m = listDevices.get(i);
						if (m.type == ListViewModel.INFO_DEVIE) {
							toDeviceCodes.append(listDevices.get(i).code + ",");
						} else {
							toGroupCodes.append(listDevices.get(i).code + ",");
						}
					}
					String toD = "";
					if (toDeviceCodes.length() > 0)
						toD = toDeviceCodes.toString();
					String toG = "";
					if (toGroupCodes.length() > 0)
						toG = toGroupCodes.toString();
					BuildWhere wheres = new BuildWhere();
					wheres.addWhere(DataTableMap.TestRecord.name(),
							"%s." + TestRecordEnum.record_id.name() + " in ('" + recordID + "')");
					ArrayList<TestRecord> dataRecords = FileDB.getInstance(context)
							.buildTestRecordList(wheres.getWhere());
					Set<File> files = new LinkedHashSet<>();
					String fileDir = AppFilePathUtil.getInstance().createSDCardBaseDirectory("sharepush", String.valueOf(System.currentTimeMillis()));
					// json文件
					File fjson = new File(fileDir + File.separator + ShareCommons.DATA_DESCRIBE_JSON);
					com.walktour.Utils.FileUtil.writeToFile(fjson, ShareCommons.changeArrayToJson(dataRecords));
					files.add(fjson);
					for (TestRecord tr : dataRecords) {
						ArrayList<RecordDetail> details = tr.getRecordDetails();
						for (RecordDetail detail : details) {
							File file = new File(detail.file_path + detail.file_name);
							files.add(file);
						}
					}
					StringBuffer sb = new StringBuffer();
					String kx = "";
					for (TestRecord tr : dataRecords) {
						if (tr.file_name.contains(".")) {
							kx = tr.file_name.substring(0, tr.file_name.lastIndexOf(".") - 1) + "_";
						} else {
							kx = tr.file_name + "_";
						}
						if (!sb.toString().contains(kx))
							sb.append(kx);
					}
					String zipFileName = System.currentTimeMillis() + ".zip";
					String fileAbsolutePath = ShareCommons.SHARE_PATH_DATA + zipFileName;
					// 1.先入库
					ShareFileModel model = new ShareFileModel();
					model.setFileType(fileType);
					model.setFilePath(fileAbsolutePath.substring(0, fileAbsolutePath.lastIndexOf("/") + 1));
					model.setFileName(zipFileName);
					model.setFileTotalSize(0);
					model.setFileRealSize(0);
					model.setFileDescribe(sb.toString().substring(0, sb.toString().length() - 1));
					model.setSendOrReceive(ShareFileModel.SEND_OR_RECEIVE_SEND);
					model.setFromDeviceCode(ShareCommons.device_code);
					model.setTargetDeviceCodes(toD);
					model.setTargetGroupCodes(toG);
					model.setFileStatus(ShareFileModel.FILE_STATUS_ZIP);
					Long ID = ShareDataBase.getInstance(context).insertFile(model);
					// 再压缩
					try {
						zipFile = new File(fileAbsolutePath);
						ZipUtil.zip(files, zipFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
					fjson.delete();
					new File(fileDir).delete();
					model = ShareDataBase.getInstance(context).fetchFile(ID);
					model.setFileStatus(ShareFileModel.FILE_STATUS_START);
					ShareDataBase.getInstance(context).updateFile(model);
					// 再发送
					BaseResultInfoModel model2 = ShareHttpRequestUtil.getInstance().send(ShareCommons.device_code, toD,
							toG, fileType + "", zipFile.getName(), zipFile.length() + "", model.getFileDescribe(),
							ShareCommons.session_id);
					if(model2.getReasonCode()==1&&model2.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)){
						RegisterDeviceLogic.getInstance(context).shareRegister();
						model2 = ShareHttpRequestUtil.getInstance().send(ShareCommons.device_code, toD,
								toG, fileType + "", zipFile.getName(), zipFile.length() + "", model.getFileDescribe(),
								ShareCommons.session_id);
					}
					listResults.add(model2);
					IDS.add(ID);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return listResults;
		}
	}
	/**
	 * 更新文件信息
	 * 
	 * @author weirong.fan
	 *
	 */
	private class UpdateFile extends AsyncTask<Void, Void, Integer> {
		List<Integer> listFileTyp = new LinkedList<Integer>();
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			// 接收文件信息通知
			for (Integer i : listFileTyp) {
				switch (i) {
				case ShareFileModel.FILETYPE_PROJECT:
					sendBroadcase(ShareCommons.SHARE_ACTION_1);
					showInfo(R.string.share_project_list);
					break;
				case ShareFileModel.FILETYPE_GROUP:
					sendBroadcase(ShareCommons.SHARE_ACTION_2);
					showInfo(R.string.droidwall_other_app_task_group_short);
					break;
				case ShareFileModel.FILETYPE_TASK:
					sendBroadcase(ShareCommons.SHARE_ACTION_3);
					showInfo(R.string.data_task);
					break;
				case ShareFileModel.FILETYPE_CQT:
					sendBroadcase(ShareCommons.SHARE_ACTION_4);
					showInfo(R.string.main_floor);
					break;
				case ShareFileModel.FILETYPE_CQI_PIC:
					sendBroadcase(ShareCommons.SHARE_ACTION_5);
					showInfo(R.string.act_sys_map);
					break;
				case ShareFileModel.FILETYPE_STATION:
					sendBroadcase(ShareCommons.SHARE_ACTION_6);
					showInfo(R.string.map_base);
					break;
				case ShareFileModel.FILETYPE_PIC_SCREENSHOT:
					sendBroadcase(ShareCommons.SHARE_ACTION_7);
					showInfo(R.string.str_snapshot);
					break;
				case ShareFileModel.FILETYPE_DATA:
					sendBroadcase(ShareCommons.SHARE_ACTION_8);
					showInfo(R.string.main_file);
					break;
				case ShareFileModel.FILETYPE_REPORT:
					sendBroadcase(ShareCommons.SHARE_ACTION_9);
					showInfo(R.string.report_type);
					break;
				}
			}
		}
		@Override
		protected Integer doInBackground(Void... params) {
			UnreadModel urm = ShareHttpRequestUtil.getInstance().queryUnreadInfo(ShareCommons.device_code,
					ShareCommons.session_id);
			if(urm.getReasonCode()==1&&urm.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)){
				RegisterDeviceLogic.getInstance(context).shareRegister();
				urm = ShareHttpRequestUtil.getInstance().queryUnreadInfo(ShareCommons.device_code,
						ShareCommons.session_id);
			}
			if (urm.getReasonCode() == 1) {
				if (urm.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {
					List<UnreadModel.ShareFile> listF = urm.getFiles();
					for (UnreadModel.ShareFile f : listF) {
						ShareFileModel model = new ShareFileModel();
						model.setFileDescribe(f.getFile_describe());
						model.setFileID(f.getFile_id());
						model.setFileType(Integer.parseInt(f.getFile_type()));
						model.setFilePath(model.getFileSDPath());
						String fileName = f.getFile_name().contains("/")
								? f.getFile_name().substring(f.getFile_name().lastIndexOf("/") + 1) : f.getFile_name();
						String name = fileName.substring(0, fileName.lastIndexOf(".")) + "_"
								+ System.currentTimeMillis() + fileName.substring(fileName.lastIndexOf("."));
						model.setFileName(name);
						fileName = null;
						name = null;
						model.setFileTotalSize(Long.parseLong(f.getFile_size()));
						if (!listFileTyp.contains(Integer.parseInt(f.getFile_type()))) {
							listFileTyp.add(Integer.parseInt(f.getFile_type()));
						}
						if (null != f.getGroup_code() && !f.getGroup_code().equals("")) {
							// 共享给组
							model.setFromGroupCode(f.getGroup_code());
						}
						model.setFromDeviceCode(f.getDevice_code());
						try {  
							// 更新设备表
							ShareDeviceModel mm = new ShareDeviceModel();
							mm.setDeviceCode(f.getDevice_code());
							mm.setDeviceName(f.getDevice_name());
							mm.setDeviceOS(f.getDevice_type().equals("0") ? 0 : 1);
							ShareDataBase.getInstance(context).insertDevice(mm);
						} catch (Exception e) {
							e.printStackTrace();
						}
						model.setSendOrReceive(ShareFileModel.SEND_OR_RECEIVE_RECEIVE);
						ShareDataBase.getInstance(context).insertFile(model);
					}
				}
			}
			return null;
		}
		private void showInfo(int ID) {
			ToastUtil.showToastShort(context, String.format(
					context.getString(R.string.share_project_devices_receive_newmessage), context.getString(ID) + ""));
		}
		private void sendBroadcase(String action) {
			Intent intent = new Intent();
			intent.setAction(action);
			context.sendBroadcast(intent);
		}
	}
	/***
	 * 更新群组关系
	 * 
	 * @author weirong.fan
	 *
	 */
	private class UpdateGroup extends AsyncTask<Void, Void, Integer> {
		// 1、你被添加到群组2、群组名称已更改3、群组成员有退出
		private int type;
		private String dl_object;
		private UpdateGroup(int type, String dl_object) {
			super();
			this.type = type;
			this.dl_object = dl_object;
		}
		@Override
		protected Integer doInBackground(Void... params) {
			try {
				DeviceInfoModel model1 = ShareHttpRequestUtil.getInstance().query_members(this.dl_object,
						ShareCommons.session_id);
				if (model1.getReasonCode()==1&&model1.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)) {
					RegisterDeviceLogic.getInstance(context).shareRegister();
					model1 = ShareHttpRequestUtil.getInstance().query_members(this.dl_object, ShareCommons.session_id);
				}
				if (model1.getReasonCode() == 1) {
					if (model1.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {
						switch (type) {
						case 1:// 1、你被添加到群组
							ShareGroupModel gm = new ShareGroupModel();
							gm.setGroupCode(model1.getGroup_code());
							gm.setGroupName(model1.getGroup_name());
							gm.setCreateDeviceCode(model1.getDevice_code());
							// 插入群组信息
							ShareDataBase.getInstance(context).saveOrUpdateGroup(gm);
							List<DeviceInfoModel.Device> listD = model1.getDevices();
							for (DeviceInfoModel.Device d : listD) {
								ShareGroupRelationModel grm = new ShareGroupRelationModel();
								grm.setGroupCode(model1.getGroup_code());
								grm.setDeviceCode(d.getDevice_code());
								// 插入群组关系信息
								ShareDataBase.getInstance(context).insertGroupRelation(grm);
								ShareDeviceModel dm = ShareDataBase.getInstance(context)
										.fetchDeviceByDeviceCode(d.getDevice_code());
								if (dm == null) {
									dm=new ShareDeviceModel();
									dm.setDeviceCode(d.getDevice_code());
									dm.setDeviceName(d.getDevice_name()); 
									dm.setDeviceOS(d.getDevice_type().equalsIgnoreCase("0") ? 0 : 1);
									// 插入设备信息
									ShareDataBase.getInstance(context).insertDevice(dm);
								}
							}
							break;
						case 2:// 2、群组名称已更改
							ShareGroupModel g = ShareDataBase.getInstance(context).fetchGroup(model1.getGroup_code());
							g.setGroupName(model1.getGroup_name());
							ShareDataBase.getInstance(context).saveOrUpdateGroup(g);
							break;
						case 3:// 3、群组成员有退出
							if (this.dl_object.equals(ShareCommons.device_code)) {// 如果this.dl_object等于本机code，那么是本机退出
								ShareDataBase.getInstance(context).deleteGroup(this.dl_object);
							} else {// 否则是别人退出
								List<ShareGroupRelationModel> relations = ShareDataBase.getInstance(context)
										.fetchGroupRelation(this.dl_object);
								Iterator<ShareGroupRelationModel> it = relations.iterator();
								boolean flag = false;
								while (it.hasNext()) {
									flag = false;
									ShareGroupRelationModel value = it.next();
									for (Device d : model1.getDevices()) {
										if (d.getDevice_code().equals(value.getDeviceCode())) {
											flag = true;
										}
									}
									if (!flag) {
										it.remove();
									}
								}
							}
							break;
						}
						Intent intent = new Intent(ShareCommons.SHARE_ACTION_MAIN_2);
						context.sendBroadcast(intent);
					}
				}
				GroupInfoModel model = ShareHttpRequestUtil.getInstance().queryGrouprelations(ShareCommons.device_code,
						ShareCommons.session_id);
				if (model.getReasonCode() == 1) {
					if (model.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {
						// ShareDataBase.getInstance(context).deleteGroup();
						for (GroupInfoModel.Group g : model.getGroups()) {
							ShareGroupModel gm = new ShareGroupModel();
							gm.setGroupCode(g.getGroup_code());
							gm.setGroupName(g.getGroup_name());
							gm.setCreateDeviceCode(g.getDevice_code());
							try {
								// 插入群组信息
								ShareDataBase.getInstance(context).saveOrUpdateGroup(gm);
								List<DeviceInfoModel.Device> listD = g.getDevices();
								for (DeviceInfoModel.Device d : listD) {
									ShareGroupRelationModel grm = new ShareGroupRelationModel();
									grm.setGroupCode(g.getGroup_code());
									grm.setDeviceCode(d.getDevice_code());
									// 插入群组关系信息
									ShareDataBase.getInstance(context).insertGroupRelation(grm);
									ShareDeviceModel dm = ShareDataBase.getInstance(context)
											.fetchDeviceByDeviceCode(d.getDevice_code());
									if (dm == null) {
										dm=new ShareDeviceModel();
										dm.setDeviceCode(d.getDevice_code());
										dm.setDeviceName(d.getDevice_name()); 
										dm.setDeviceOS(d.getDevice_type().equalsIgnoreCase("0") ? 0 : 1);
										// 插入设备信息
										ShareDataBase.getInstance(context).insertDevice(dm);
									}
								}
								Intent intent = new Intent(ShareCommons.SHARE_ACTION_MAIN_2);
								context.sendBroadcast(intent);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	/***
	 * 好友变更消息
	 * 
	 * @author weirong.fan
	 *
	 */
	private class ConfirmDevice extends AsyncTask<Void, Void, Integer> {
		private int type;
		private String obj;
		private ConfirmDevice(int type, String obj) {
			super();
			this.type = type;
			this.obj = obj;
		}
		@Override
		protected Integer doInBackground(Void... params) {
			try {
				List<ShareDeviceModel> localDeviceList = ShareDataBase.getInstance(context).fetAllDevice();
				boolean flag;
				// type类型
				switch (type) {
				case 1:// 1、接收到好友申请
					DeviceInfoModel di = ShareHttpRequestUtil.getInstance().queryConfirmDevice("0",
							ShareCommons.session_id);
					if(di.getReasonCode()==1&&di.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)){
						RegisterDeviceLogic.getInstance(context).shareRegister();
						di = ShareHttpRequestUtil.getInstance().queryConfirmDevice("0",
								ShareCommons.session_id);
					}
					for (Device dd : di.getDevices()) {
						flag = false;
						for (ShareDeviceModel ld : localDeviceList) {
							if (dd.getDevice_code().equals(ld.getDeviceCode())) {
								flag = true;
								ld.setDeviceMessage(dd.getRequest_message());
								ld.setDeviceStatus(ShareDeviceModel.STATUS_CONFIRM);
								ShareDataBase.getInstance(context).updateDevice(ld);
								break;
							}
						}
						if (!flag) {// 新增
							ShareDeviceModel smd = new ShareDeviceModel();
							smd.setDeviceCode(dd.getDevice_code());
							smd.setDeviceName(dd.getDevice_name());
							smd.setDeviceMessage(dd.getRequest_message());
							smd.setDeviceOS(dd.getDevice_type().equals(ShareDeviceModel.OS_ANDROID + "")
									? ShareDeviceModel.OS_ANDROID : ShareDeviceModel.OS_IOS);
							smd.setDeviceStatus(ShareDeviceModel.STATUS_CONFIRM);
							ShareDataBase.getInstance(context).insertDevice(smd);
						}
					}
					break;
				case 2:// 2、你的好友申请被接受
						// 申请加别别人为好友，本地端已存储
					for (ShareDeviceModel ld : localDeviceList) {
						if (obj.equals(ld.getDeviceCode())) {
							ld.setDeviceStatus(ShareDeviceModel.STATUS_ADDED);
							ShareDataBase.getInstance(context).updateDevice(ld);
							break;
						}
					}
					break;
				case 3:// 3、你的好友申请被拒绝
					for (ShareDeviceModel sdm : localDeviceList) {
						if (obj.equals(sdm.getDeviceCode())) {
							sdm.setDeviceStatus(ShareDeviceModel.STATUS_REFUSED);
							ShareDataBase.getInstance(context).updateDevice(sdm);
							break;
						}
					}
					break;
				case 4:// 4、你被别人删除好友
						// 服务器好友
					for (ShareDeviceModel ld : localDeviceList) {
						if (obj.equals(ld.getDeviceCode())) {
							ld.setDeviceStatus(ShareDeviceModel.STATUS_DELETED);
							ShareDataBase.getInstance(context).updateDevice(ld);
							break;
						}
					}
					break;
				case 5://好友备注名修改
					BaseResultInfoModel result = ShareHttpRequestUtil.getInstance().queryDevice(obj,
							ShareCommons.session_id);
					if (result.getReasonCode() == 1
							&& result.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)) {
						RegisterDeviceLogic.getInstance(context).shareRegister();
						result = ShareHttpRequestUtil.getInstance().queryDevice(obj, ShareCommons.session_id);
					}
					if (result.getReasonCode() == 1) {
						if (result.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {
							for (ShareDeviceModel sss : localDeviceList) {
								if (sss.getDeviceCode().equals(obj)) {
									sss.setDeviceName(result.getDevice_name());
									ShareDataBase.getInstance(context).updateDevice(sss);
									break;
								}
							}
						}
					}
				default:
					break;
				}
				Intent intent = new Intent(ShareCommons.SHARE_ACTION_MAIN_2);
				context.sendBroadcast(intent);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}
	}
	/**
	 * 启动子线程，如果正在测试中，那么一直等待，直到测试停止，然后停止walktour相关的所有服务，最后启动killer服务结束进程
	 * 
	 * @author Administrator
	 *
	 */
	class WaitStopThread extends Thread {
		private Context context;
		private int stopTimeOut = 0;
		public WaitStopThread(Context context) {
			this.context = context;
		}
		public void run() {
			while (stopTimeOut < 1000 * 30 && (appModel.isTestStoping() || appModel.isTestJobIsRun())) {
				try {
					Thread.sleep(500);
					stopTimeOut += 500;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			LogUtil.w(tag, "----stop timeOut:" + stopTimeOut);
			// 重置软件环境初始化设置
			ApplicationModel.getInstance().setEnvironmentInit(false);
			context.startService(new Intent(context, Killer.class));
		}
	}
	/***
	 * 每新增一个AP,写入一次,第一次将扫描到的AP全部写入,之后的都和上一次比较
	 */
	private void doWifiApScan(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (null != wifiManager && WifiManager.WIFI_STATE_ENABLED == wifiManager.getWifiState()) {
			List<ScanResult> list = wifiManager.getScanResults();
			if (null != list && list.size() > 0) {
				if (listScanResult.isEmpty()) {
					for (ScanResult sr : list) {
						EventBytes.Builder(context, RcuEventCommand.WLAN_WIFI_AP_ADDED).addStringBuffer(sr.SSID + "")
								.addStringBuffer(sr.BSSID + "")
								.addCharArray((WifiTools.getChannelByFrequency(sr.frequency) + "\0").toCharArray(), 24)
								.addInteger(0).writeToRcu(System.currentTimeMillis() * 1000);
						listScanResult.add(sr);
					}
				} else {
					List<String> ssidArray = new LinkedList<String>();
					for (ScanResult sr : listScanResult) {
						ssidArray.add(sr.BSSID);
					}
					for (ScanResult sr : list) {
						if (ssidArray.contains(sr.BSSID))
							continue;
						EventBytes.Builder(context, RcuEventCommand.WLAN_WIFI_AP_ADDED).addStringBuffer(sr.SSID + "")
								.addStringBuffer(sr.BSSID + "")
								.addCharArray((WifiTools.getChannelByFrequency(sr.frequency) + "\0").toCharArray(), 24)
								.addInteger(0).writeToRcu(System.currentTimeMillis() * 1000);
					}
					// 每次都和上一次相比较
					listScanResult.clear();
					listScanResult.addAll(list);
				}
			}
		}
	}
	/***
	 * 每连接上一个新的AP,记录下来
	 * 
	 * @param context
	 */
	private void doWifiMain(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (null != wifiManager && WifiManager.WIFI_STATE_ENABLED == wifiManager.getWifiState()) {
			WifiInfo info = wifiManager.getConnectionInfo();
			if (null != info) {
				if (null == currentAP || !currentAP.equals(info.getSSID())) {
					currentAP = info.getSSID();
					EventBytes.Builder(context, RcuEventCommand.WLAN_WIFI_AP_CONNECTED)
							.addStringBuffer(info.getSSID() + "")
							.addStringBuffer(WifiTools.getIPAddress(info.getIpAddress()) + "")
							.addStringBuffer(info.getMacAddress() + "").addInteger(info.getRssi()).addInteger(0)
							.addInteger(0).addInteger(0).addInteger(0).addInteger(0)
							.writeToRcu(System.currentTimeMillis() * 1000);
				}
			} else {
				currentAP = null;
			}
		}
	}
}