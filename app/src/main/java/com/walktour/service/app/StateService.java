package com.walktour.service.app;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.APNOperate;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.CheckAbnormal;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.instance.AlertManager;
import com.walktour.control.instance.DataManagerFileList;
import com.walktour.gui.share.logic.RegisterDeviceLogic;
import com.walktour.service.StartDatasetService;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 监听系统数据变化的服务
 */
public class StateService extends Service {
	/**
	 * 日志标识
	 */
	private static final String TAG = "StateService";
	/**
	 * 状态监测
	 */
	private final String ACTION_CHECK_DATA = "com.walktor.gui.StateServer.check.data";
	/**
	 * 应用唯一实例
	 */
	private static ApplicationModel sAppModel;
	/**
	 * 界面管理器
	 */
	private ActivityManager mActivityManager;
	/**
	 * 锁屏监控定时器
	 */
	private Timer mCloseScreenTimer;
	/**
	 * 锁屏监控任务
	 */
	private TimerTask mCloseScreenTimerTask;
	/**
	 * 屏幕是否在开启
	 */
	private static boolean isScreenOn = true;
	/**
	 * 应用后台运行监控定时器
	 */
	private Timer mAppBackRunTimer;
	/**
	 * 应用后台运行监控任务
	 */
	private TimerTask mAppBackRunTimeTask;
	/**
	 * 后台运行时间
	 */
	private int mBackrunTimes = 0;
	private int mBackrunTimeOut = 60; // 程序转入后台多少秒后关半串口
	private static boolean isTraceOpen = true; // Trace口开启中

	private static final int sBatteryAlarmIntever = 1000 * 60; // 电量低告警间隔
	private long lastBatteryAlarmTime = 0; // 记录上次告警时间

	// 电池状态相关
	private int currBatteryPercent = 0;
	private int currPhoneTemperature = 0;
	private boolean isBatteryCharging = false;
	/** 计算间隔sim状态间隔时间 */
	private int times = -1;

	private static final int TRACE_CLOSE = -1;
	private static final int TRACE_OPEN = 1;
	private static final long Hour = 1000 * 60 * 60;

	private DatasetManager mDatasetManager;
	/**
	 * 程序运行总时间
	 */
	private long mTotalRunTime = 0;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.i(TAG, "---onCreate");
		sAppModel = ApplicationModel.getInstance();
		mDatasetManager = DatasetManager.getInstance(this);
		// mActivityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		mTotalRunTime = ConfigRoutine.getInstance().getRunTime(this); // 计算程序运行总时间
		// 设定自动删除数据的预广播
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_CHECK_DATA), 0);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 24 * Hour, pendingIntent);
		// 注册事件监听
		regeditBroadcast();
		checkApplicationBackRun();
		if (sAppModel.isGeneralMode())
			isTraceOpen = false;
	}

	/**
	 * 监控Walktour是否在前台运行
	 */
	@SuppressWarnings("deprecation")
	private void checkApplicationBackRun() {
		mAppBackRunTimer = new Timer();
		mAppBackRunTimeTask = new TimerTask() {
			private String imsi;

			@Override
			public void run() {
				times++;
				// 当前没有在做业务测试或者空闲状态下才需要处理walktour是否为当前应用
				// && MyPhoneState.getCallState() == TelephonyManager.CALL_STATE_IDLE
				if (!sAppModel.isGeneralMode() && !sAppModel.isTestJobIsRun() && !sAppModel.isTestStoping()
						&& !sAppModel.isUmpcTest() && !ConfigRoutine.getInstance().toRunHttpServer(getApplicationContext())
						&& !DatasetManager.isPlayback) {
					// 判断walktour是否为当前活动或者后台活动
					List<RunningTaskInfo> list = mActivityManager.getRunningTasks(3);
					String myPkgName = getApplicationContext().getPackageName();
					boolean isFind = false;
					StringBuilder sb = new StringBuilder();
					for (RunningTaskInfo info : list) { 					
						if (info.topActivity.getPackageName().equals(myPkgName) || info.baseActivity.getPackageName().equals(myPkgName)) { 
							isFind = true;
							break;
						}
						sb.append("top:").append(info.topActivity.getPackageName());
						sb.append("base:").append(info.baseActivity.getPackageName());
						sb.append("/n");
					}
					if (isFind) {
						mBackrunTimes = 0;
						// 如果Trace口没有开启，则发送打开Trace口命令并将Trace口状态置为开
						if (!isTraceOpen && isScreenOn) {
							if(!Deviceinfo.getInstance().isUseRoot()||Deviceinfo.getInstance().isRunStartDatasetService()) {
								if((!Deviceinfo.getInstance().isVivo() && !Deviceinfo.getInstance().isOppoCustom())||Deviceinfo.getInstance().isRunStartDatasetService()) {
									startService(new Intent(getApplicationContext(), StartDatasetService.class));
								}
							}
							Message msg = mHandler.obtainMessage(TRACE_OPEN);
							msg.sendToTarget();
						}
					} else { // walktour不是活动应用
						// 如果当前串口为打开状态，开始计时
						if (isTraceOpen) {
							mBackrunTimes++;
							if (mBackrunTimes >= mBackrunTimeOut) {
								LogUtil.w(TAG, "--back run to close trace--" + mBackrunTimes);
								Message msg = mHandler.obtainMessage(TRACE_CLOSE);
								msg.sendToTarget();
								mBackrunTimes = 0;
							}else if(sb.length() > 0 && mBackrunTimes > mBackrunTimeOut / 2)
							LogUtil.w(TAG, "---walktour back run time:" + mBackrunTimes + "--packN:"
									+ sb.toString());
						}
						// 如果当前串口为关的状态，不做其它处理
					}
				}
				mTotalRunTime++;
				if (mTotalRunTime % 600 == 0) {
					ConfigRoutine.getInstance().setRunTime(StateService.this, mTotalRunTime);
				}
				TraceInfoInterface.traceData.setRunTimes(mTotalRunTime);
				imsi = MyPhoneState.getInstance().getIMSI(StateService.this);
				if (imsi == null || imsi.trim().length() == 0) {
					if (times % 60 == 0) { // 每隔60秒报一次没sim卡告警
						AlertManager.getInstance(StateService.this).addDeviceAlarm(WalkStruct.Alarm.DEVICE_SIMCARD, -1);
					}
				}
			}
		};

		mAppBackRunTimer.schedule(mAppBackRunTimeTask, 0, 1000);
	}

	private void cleanApplicationBackRun() {
		if (mAppBackRunTimeTask != null) {
			mAppBackRunTimeTask.cancel();
			mAppBackRunTimeTask = null;
		}
		if (mAppBackRunTimer != null) {
			mAppBackRunTimer = null;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.d(TAG, "---onStart:" + (intent == null));
		if (intent == null) {
			startService(new Intent(getApplicationContext(), Killer.class));
		}
		MyPhoneState.getInstance().listenPhoneState(this);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		LogUtil.d(TAG, "---onDestroy--");
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);// 反注册事件监听
		cleanApplicationBackRun();
	}

	/**
	 * 注册广播接收器
	 */
	protected void regeditBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(ACTION_CHECK_DATA);
		this.registerReceiver(mBroadcastReceiver, filter);
	}

	/**
	 * 广播接收器:接收来广播更新界面
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			// 屏幕开启
			if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				isScreenOn = true;
				cleanTimerTask();
				// LogUtil.i(TAG, "---received broadcast:Intent.ACTION_SCREEN_ON" );
				// context.sendBroadcast( new Intent(WalkMessage.ACTION_TRACE_ON) );
			}

			// 屏幕关闭事件
			else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				isScreenOn = false;
				LogUtil.i(TAG, "---received broadcast:" + Intent.ACTION_SCREEN_OFF);
				// 判断是否正在测试
				// ApplicationModel sAppModel = ApplicationModel.getInstance();
				if (!sAppModel.isTestJobIsRun() && !sAppModel.isTestStoping() && !sAppModel.isUmpcTest()
						&& !ConfigRoutine.getInstance().toRunHttpServer(getApplicationContext())) {
					// 如果当前非空闲状态
					LogUtil.i(TAG, "---mCallState:" + MyPhoneState.getCallState());
					if (MyPhoneState.getCallState() == TelephonyManager.CALL_STATE_IDLE && isTraceOpen) {
						closeScreen();
					}
				}

			}
			// 定时删除数据
			else if (intent.getAction().equals(ACTION_CHECK_DATA)) {
				new Thread(new ThreadDataClean()).start();
			}

			// 电池状态
			else if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction()) && isTraceOpen) {
				try {
					int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
					int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
					int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
					int oldBatteryPercent = currBatteryPercent;
					currBatteryPercent = level * 100 / scale;
					int oldPhoneTemperature = currPhoneTemperature;
					currPhoneTemperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10;
					boolean oldBatteryCharging = isBatteryCharging;
					if (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB) {
						isBatteryCharging = true;
					} else {
						isBatteryCharging = false;

						if (currBatteryPercent < AlertManager.BATTERY_LOW
								&& System.currentTimeMillis() - lastBatteryAlarmTime > sBatteryAlarmIntever) {
							lastBatteryAlarmTime = System.currentTimeMillis();

							AlertManager.getInstance(StateService.this).addDeviceAlarm(WalkStruct.Alarm.DEVICE_POWER_LOW, -1);
						}

						if (currPhoneTemperature > AlertManager.PHONE_OVERHEAT) {
							AlertManager.getInstance(StateService.this).addDeviceAlarm(WalkStruct.Alarm.DEVICE_TEMP_HIGH, -1);
						}
					}

					if (oldBatteryPercent != currBatteryPercent || oldPhoneTemperature != currPhoneTemperature
							|| oldBatteryCharging != isBatteryCharging)
						LogUtil.d(TAG, String.format("Power:%s,tem:%s,charging:%b", currBatteryPercent, currPhoneTemperature,
								isBatteryCharging));

					TraceInfoInterface.traceData.getStateInfo().setTemperature(String.valueOf(currPhoneTemperature));
				} catch (Exception e) {
					LogUtil.w(TAG, e.getMessage(), e);
				}
			} else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				LogUtil.d(TAG, "-----NetWork Change------" + APNOperate.getInstance(getApplicationContext()).checkNetWorkIsConnected());
				int state = MyPhoneState.getInstance().getDataConnectState(getApplicationContext());
				if (state == MyPhoneState.DATA_STATE_WIFI || state == MyPhoneState.DATA_STATE_MOBILE) {
					if (!ApplicationModel.getInstance().isBindXgSuccess) {
						RegisterDeviceLogic.getInstance(StateService.this).shareRegister();
					}
				}
				
				networkChangeSetWtState();
			}
		}// end onReceive

	};

	private boolean wtCheckRunState = false;
	private void networkChangeSetWtState(){
		//当前打开网络
		if(APNOperate.getInstance(getApplicationContext()).checkNetWorkIsConnected() && !wtCheckRunState 
				&& !ApplicationModel.getInstance().isTestJobIsRun()){
			wtCheckRunState = true;
			//如果当前标为异常状态,或者非今天校验的
			if(ConfigRoutine.getInstance().isAbnormal(getApplicationContext())
					|| (!UtilsMethod.jem(ConfigRoutine.getInstance().getAutoTim(getApplicationContext()))
					.equals(UtilsMethod.ymdFormat.format(System.currentTimeMillis())))){
				LogUtil.w(TAG, "--checknormal: run--");
				new Thread(new CheckAbnormalThr()).start();
			}else{
				wtCheckRunState = false;
			}
		}
	}
	
	class CheckAbnormalThr implements Runnable{
		@Override
		public void run() {
			new CheckAbnormal().checkNormalState(getApplicationContext());
			wtCheckRunState = false;
		}
	}
	
	/**
	 * 关闭屏幕器
	 */
	private void closeScreen() {
		mCloseScreenTimer = new Timer();
		mCloseScreenTimerTask = new TimerTask() {
			int i = 30;

			@Override
			public void run() {
				// 倒计时10秒关闭
				if (i == 0) {
					// 如果屏幕处于关闭状态，则通知TraceInfo服务停止读取trace口信息
					if (!isScreenOn()) {
						Message msg = mHandler.obtainMessage(TRACE_CLOSE);
						msg.sendToTarget();
					} else {
						LogUtil.i(TAG, "---screen is on");
					}
				} else {
					LogUtil.i(TAG, "shutdown trace:" + i);
				}

				i--;
			}

		};
		// 把计划任务放入计时器
		mCloseScreenTimer.schedule(mCloseScreenTimerTask, 0, 1000);
	}

	/**
	 * 清理计划 和计时器
	 */
	private void cleanTimerTask() {
		if (mCloseScreenTimerTask != null) {
			mCloseScreenTimerTask.cancel();
			mCloseScreenTimerTask = null;
		}
		if (mCloseScreenTimer != null) {
			mCloseScreenTimer.cancel();
			mCloseScreenTimer = null;
		}
	}

	private static class MyHandler extends Handler {
		private WeakReference<StateService> reference;

		public MyHandler(StateService service) {
			this.reference = new WeakReference<>(service);
		}

		@Override
		public void handleMessage(Message msg) {
			StateService service = this.reference.get();
			switch (msg.what) {
			case TRACE_CLOSE:
				// 通知TraceInfo服务关闭trace
				if (!DatasetManager.isPlayback && isTraceOpen && !sAppModel.isTestJobIsRun() && !sAppModel.isTestStoping()
						&& !sAppModel.isUmpcTest()) {
					isTraceOpen = false;
					new Thread(service.new ThreadCloseTrace()).start();
				}
				// 清除计时器
				service.cleanTimerTask();
				break;
			case TRACE_OPEN:
				isTraceOpen = true;
				service.mDatasetManager.openTrace();
				break;
			}
		}
	}

	private Handler mHandler = new MyHandler(this);

	public static synchronized boolean isScreenOn() {
		return isScreenOn;
	}

	/**
	 * ThreadCloseTrace
	 * 
	 * 2014-1-17 上午10:35:56
	 * 
	 * @version 1.0.0
	 * @author qihang.li@dinglicom.com
	 */
	private class ThreadCloseTrace implements Runnable {
		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// 2014.1.17 发现此函数会导致ANR错误，所以单独开线程
			LogUtil.w(TAG, "close trace start");
			mDatasetManager.closeTrace(DatasetManager.PORT_2);
			if(!Deviceinfo.getInstance().isUseRoot()||Deviceinfo.getInstance().isRunStartDatasetService()) {
				if((!Deviceinfo.getInstance().isVivo() && !Deviceinfo.getInstance().isOppoCustom())||Deviceinfo.getInstance().isRunStartDatasetService()) {
					stopService(new Intent(getApplicationContext(), StartDatasetService.class));
				}
            }
			LogUtil.w(TAG, "close trace end");
		}
	}

	private class ThreadDataClean implements Runnable {
		@Override
		public void run() {
			if (ConfigRoutine.getInstance().isAutoDelete(StateService.this)) {
				int day = ConfigRoutine.getInstance().getAutoDeleteDay(StateService.this);
				// 清理旧数据
				DataManagerFileList.getInstance(StateService.this).deleteOldFiles(day);
			}

		}
	}
}
