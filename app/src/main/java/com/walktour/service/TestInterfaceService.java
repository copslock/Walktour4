package com.walktour.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.dinglicom.dataset.TotalInterface;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.ServerMessage;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct.UMPCConnectStatus;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.gui.R;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.TaskGroupConfig;
import com.walktour.service.app.AutoTestService;
import com.walktour.service.app.Killer;

import java.lang.ref.WeakReference;
import java.util.List;

public class TestInterfaceService extends Service {
	private final String tag = "TestInterfaceService";
	private ApplicationModel appModel = ApplicationModel.getInstance();

	/** 定时器,用于当前组开始测试前有指定时间,或者当前测试有持继时间时使用 */
	private AlarmManager alarmManager = null;
	/** 定义当前任务组开始执行时间或者持续指定时间到了;该intent用于发送bordercase，值会根据当前值发生变化 */
	private PendingIntent piTestTimeIn = null;

	/** 测试任务组中断,如果需要中断整个任务组测试时,收到中断消息,当前值为真 */
	private boolean testGroupInterrupt = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.w(tag, "--onCreate--");
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(WalkMessage.ACTION_WALKTOUR_START_TEST);
		filter.addAction(WalkMessage.NOTIFY_TESTJOBDONE);
		filter.addAction(WalkMessage.NOTIFY_GROUP_TESTJOBDONE);
		filter.addAction(WalkMessage.NOTIFY_GROUP_INTERRUPTJOBDONE);
		// filter.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE);
		filter.addAction(WalkMessage.redoTraceInit);
		filter.addAction(WalkMessage.InteruptTestAndRedoTraceInit);
		filter.addAction(WalkMessage.InteruptTestAndRebootDevice);
		filter.addAction(WalkMessage.PuaseTestAndRedoTraceInit);
		filter.addAction(WalkMessage.Action_Walktour_Test_Interrupt);
		filter.addAction(WalkMessage.MutilyTester_Start_UmpcServer);
		filter.addAction(WalkMessage.MutilyTester_Close_UmpcServer);
		filter.addAction(WalkMessage.MutilyTester_ReDo_ConnectServer);

		// 关于任务组调试消息
		filter.addAction(WalkMessage.ACTION_WALKTOUR_GROUP_TESTTIME_SETIN);
		filter.addAction(WalkMessage.ACTION_WALKTOUR_GROUP_KEEPTIME_SETIN);
		this.registerReceiver(testEventReceiver, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.w(tag, "--onStartCommand--" + (intent == null));
		if (intent == null) {
			startService(new Intent(getApplicationContext(), Killer.class));
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		LogUtil.w(tag, "onDestroy");
		if (serviceBinder != null) {
			unbindService(conn);
		}
		super.onDestroy();
		this.unregisterReceiver(testEventReceiver);

	}

	TestService serviceBinder;
	ServiceConnection conn = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceBinder = ((TestService.TestServiceBinder) service).getService();
		}

		public void onServiceDisconnected(ComponentName arg0) {
			serviceBinder = null;
		}
	};

	private class reInitTraceService extends Thread {
		public void run() {
			rebootTrace();
		}
	}

//	private void rebootTrace() {
//		appModel.setTraceInitSucc(false);
//		Intent traceService = new Intent(getApplicationContext(), TraceService.class);
//		LogUtil.w(tag, "---rebootTrace to stopService---");
//		stopService(traceService);
//		try {
//			Thread.sleep(1000 * 10);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		LogUtil.w(tag, "---rebootTrace to reStartService--");
//		startService(traceService);
//	}

	private static final int TestStopTimeout = 1;
	private int stopTimeOut = 0;

	private static class MyHandler extends Handler {
		private WeakReference<TestInterfaceService> reference;

		public MyHandler(TestInterfaceService service) {
			this.reference = new WeakReference<TestInterfaceService>(service);
		}

		@Override
		public void handleMessage(Message msg) {
			TestInterfaceService service = this.reference.get();
			super.handleMessage(msg);
			switch (msg.what) {
			case TestStopTimeout:
				service.callTestStopTimeout();
				break;
			}
		}

	}

	private Handler mHandler = new MyHandler(this);

	/** 停止业务测试时超时处理，如30未停止成功，直接StopService */
	private void callTestStopTimeout() {
		if (appModel.isTestStoping()) {
			if (stopTimeOut < 30) {
				LogUtil.w(tag, "--wait testStop:" + stopTimeOut);
				stopTimeOut++;
				mHandler.sendEmptyMessageDelayed(TestStopTimeout, 1000);
			} else {
				appModel.setTestJobIsRun(false);
				appModel.setTestStoping(false);
				LogUtil.i(tag, "--callTestStopTimeout--");
				Intent testIntent = new Intent(getApplicationContext(), TestService.class);
				stopService(testIntent);

				//发送广播通知IPACK已经停止测试
				Intent sendInterupTest = new Intent(WalkMessage.NOTIFY_TEST_IPACK_STOPED);
				sendBroadcast(sendInterupTest);
			}
		}
	}

	private final BroadcastReceiver testEventReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				LogUtil.w(tag, "--receive:" + intent.getAction());
				// 收到开始测试的广播
				if (intent.getAction().equals(WalkMessage.ACTION_WALKTOUR_START_TEST)) {
					// 当前正在测试
					LogUtil.w(tag, "---get auto test intent-" + appModel.isTestJobIsRun());
					if (appModel.isTestJobIsRun()) {
						Toast.makeText(getApplicationContext(), getString(R.string.main_testStoping),
								Toast.LENGTH_SHORT).show();
						return;
					}
					// 任务列表为空
					if (!TaskListDispose.getInstance().hasEnabledTask()
							&& (appModel.isScannerTest() ? !appModel.isScannerTestTask() : false)) {
						LogUtil.w(tag, "---test taskempty--");
						Toast.makeText(getApplicationContext(), R.string.main_testTaskEmpty, Toast.LENGTH_LONG).show();
						// 如果任务列表为空，释放GPS并且将GPS测试状态置为false
						GpsInfo.getInstance().releaseGps(getApplicationContext(), WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
						appModel.setGpsTest(false);
						return;
					}
					new Thread(new TestGroupExec(lockObj, intent)).start();

					// 中断测试
				} else if (intent.getAction().equals(WalkMessage.Action_Walktour_Test_Interrupt)) {
					testGroupInterrupt = true;
					interruptAnTestGroup();
				} else if (intent.getAction().equals(WalkMessage.NOTIFY_GROUP_TESTJOBDONE)
						|| intent.getAction().equals(WalkMessage.NOTIFY_GROUP_INTERRUPTJOBDONE)) {
					LogUtil.w(tag, "--->get Test Group Job Done--");
					continueThread(lockObj);
				} else if (intent.getAction().equals(WalkMessage.redoTraceInit)) {
					// 此处处理停止业务测试停止TraceInfo服务，并重启traceInfo服务
					LogUtil.w(tag, "---redoTraceInit--");
					new reInitTraceService().start();
				} else if (intent.getAction().equals(WalkMessage.InteruptTestAndRedoTraceInit)) {
					boolean isServiceOn = MyPhoneState.getInstance().isServiceAlive();
					// 如果当前有信号才进行重新初始化Trace
					if (isServiceOn) {
						LogUtil.w(tag, "--InteruptTestAndRedoTraceInit--");
						new InteruptTestAndRedoTraceInit().start();
					} else {
						LogUtil.w(tag, "---out of service");
					}
				} else if (intent.getAction().equals(WalkMessage.InteruptTestAndRebootDevice)) {
					new InteruptTestAndRebootDevice().start();
				} else if (intent.getAction().equals(WalkMessage.PuaseTestAndRedoTraceInit)) {
					new PuaseTestAndRedoTraceInit().start();
				} else if (intent.getAction().equals(WalkMessage.MutilyTester_Start_UmpcServer)) {
					LogUtil.w(tag, "--open UMPC service,service start!");
					startMultiTest();
				} else if (intent.getAction().equals(WalkMessage.MutilyTester_Close_UmpcServer)) {
					LogUtil.w(tag, "--close UMPC service,service stop!");
					stopMultiTest();
				} else if (intent.getAction().equals(WalkMessage.MutilyTester_Wifi_ConnectFaild)) {
					LogUtil.w(tag, "--wifi connect faild,service stop--");
					stopMultiTest();
				} else if (intent.getAction().equals(WalkMessage.ACTION_WALKTOUR_GROUP_TESTTIME_SETIN)) {
					// 测试时间到达,通知任务调度组继续
					continueThread(lockObj);
				} else if (intent.getAction().equals(WalkMessage.ACTION_WALKTOUR_GROUP_KEEPTIME_SETIN)) {
					// 当前组持续时间到达,中断当前组测试,通知任务调度组继续
					interruptAnTestGroup();
					// continueThread(lockObj);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/***
	 * 开始多网测试服务
	 */
	private void startMultiTest() {
		Intent mutilyTester = new Intent(getApplicationContext(), iPackTerminal.class);
		startService(mutilyTester);
	}

	/***
	 * 停止多网测试服务
	 */
	private void stopMultiTest() {
		appModel.setUmpcStatus(UMPCConnectStatus.Default);
		Intent mutilyTester = new Intent(getApplicationContext(), iPackTerminal.class);
		stopService(mutilyTester);
	}

	/**
	 * 测试任务组调试时线程锁 用于当前未到测试时间段或任务组下在执行中锁定调度线程 等待当前测试组测试结束,或到时候继续调度任务组
	 */
	private int[] lockObj = new int[0];

	class TestGroupExec implements Runnable {
		Object lock;
		Intent intent;

		public TestGroupExec(Object obj, Intent intent) {
			lock = obj;
			this.intent = intent;
		}

		public void run() {
			appModel.setTestJobIsRun(true);

			// 1.获得当前勾选的任务组列表
			List<TaskGroupConfig> testGroups = TaskListDispose.getInstance().getAllSelectGroup();

			// 2.循环取出列表中的任务组,需判断当前是否中断任务状态
			for (int i = 0; !testGroupInterrupt && i < testGroups.size(); i++) {
				TaskGroupConfig group = testGroups.get(i);
				/*
				 * 3.当前为分组模式,如果当前为指定时间任务,且指定时间大于当前时间, 则锁定当前线程,等待到时间继续测试
				 * 否则直接进入任务组调度
				 */
				if (WalktourApplication.isExitGroup() && group.getTimeDuration().isCheck() && group.getTimeDuration()
						.getTaskExecuteDuration().getStartTimeByLong() > System.currentTimeMillis()) {

					setAlarmSerivce(WalkMessage.ACTION_WALKTOUR_GROUP_TESTTIME_SETIN,
							group.getTimeDuration().getTaskExecuteDuration().getStartTimeByLong());

					// 锁定当前任务组调度线程,等待指定时间到来继续执行
					lockThread(lock);
				}

				// 当前非中断状态
				if (!testGroupInterrupt) {
					// 4.调度当前任务组
					intent.putExtra(WalkMessage.TEST_GROUP_INDEX, i);
					// 如果当前存在分组,将循环次数修改为组设置的次数
					if (WalktourApplication.isExitGroup()) {
						intent.putExtra(WalkMessage.Outlooptimes, group.getGroupRepeatCount());
					}
					startAnTestGroup(intent);

					// 5.当前是分组模式下,如果当前组设置了持续时间,添加持续时间闹钟,闹钟时间为:"当前时间+持续秒数"
					if (WalktourApplication.isExitGroup()
							&& group.getTimeDuration().getTaskExecuteDuration().getDurationByLong() > 0) {
						setAlarmSerivce(WalkMessage.ACTION_WALKTOUR_GROUP_KEEPTIME_SETIN, System.currentTimeMillis()
								+ group.getTimeDuration().getTaskExecuteDuration().getDurationByLong() * 1000);
					}

					// 6.等待当前任务组执行结束
					lockThread(lock);
				}

				// 7.此处取消前面设定的定义闹钟,该闹钟可能已执行过,或者未触发的测试持续时间闹钟
				cancleAlarmService();

				// 8.收到测试任务组结束的消息后,如果当前不是中断结束的,调用结束一任务的相关绑定对象
				if (!appModel.isTestInterrupt()) {
					stopAnTestGroup(intent);
				}
			}

			testJobDone();
		}
	}

	/**
	 * 发送测试结束消息
	 */
	private void testJobDone() {
		appModel.setTestJobIsRun(false);

		Intent doneIntent = null;
		if (testGroupInterrupt) {
			doneIntent = new Intent(WalkMessage.NOTIFY_INTERRUPTJOBDONE);
		} else {
			doneIntent = new Intent(WalkMessage.NOTIFY_TESTJOBDONE);
		}
		sendBroadcast(doneIntent);
		testGroupInterrupt = false;
	}

	/**
	 * 开启一个闹钟服务
	 * 
	 * @param pendingIntentStr
	 *            闹钟消息处理类型
	 * @param alarmTime
	 *            闹钟时间
	 */
	private void setAlarmSerivce(String pendingIntentStr, long alarmTime) {
		try {
			// 定义测试开始闹钟
			piTestTimeIn = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(pendingIntentStr), 0);
			// 定义一个闹钟,时间为当前组指定时间
			alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, piTestTimeIn);
		} catch (Exception e) {
			LogUtil.w(tag, "setAlarmSerivce", e);
		}
	}

	/**
	 * 取消当前定设定的闹钟通知,只有任务正常结束或正常结束时用到
	 */
	private void cancleAlarmService() {
		if (piTestTimeIn != null) {
			try {
				alarmManager.cancel(piTestTimeIn);
			} catch (Exception e) {
				LogUtil.w(tag, "stopAlarmService", e);
			}
		}
	}

	/**
	 * 锁定线程
	 * 
	 * @param lock
	 */
	private void lockThread(Object lock) {
		try {
			synchronized (lock) {
				lock.wait();
			}
		} catch (Exception e) {
			LogUtil.w(tag, "lockThread", e);
		}
	}

	/**
	 * 通知线程继续
	 * 
	 * @param lock
	 */
	private void continueThread(Object lock) {
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	/**
	 * 启动一个测试组
	 */
	private void startAnTestGroup(Intent intent) {
		// 测试开始时，记录初始测试开始时间及测试开始经纬度信息
		TraceInfoInterface.traceData.setTestStartInfo();
		TotalInterface.getInstance(getApplicationContext()).setAutoStatistic(false);
		appModel.setTestInterrupt(false);

		appModel.setRcuFileCreated(false);

		LogUtil.w(tag, "----appModel.isGyroTest=" + appModel.isGyroTest());
		// 启动测试服务
		Intent testIntent = new Intent(getApplicationContext(), TestService.class);
		testIntent.putExtras(intent);

		startService(testIntent);
		bindService(testIntent, conn, Context.BIND_AUTO_CREATE);
	}

	/**
	 * 当前任务组正常测试结束后,释放相关的绑定对象
	 * 
	 * @param intent
	 */
	private void stopAnTestGroup(Intent intent) {

		if (serviceBinder != null) {
			unbindService(conn);
			serviceBinder = null;
		}

		stopService(intent);

		// 停止陀螺仪打点
		// stopService(new Intent(getApplicationContext(),
		// RecordTraceService.class));
		// appModel.setGyroTest(false);
		// RecordTraceService.setAdjust(false);

		// 中断测试的话，GPS测试状态重置，并关闭GPS
		appModel.setGpsTest(false);
		GpsInfo.getInstance().releaseGps(getApplicationContext(), WalkCommonPara.OPEN_GPS_TYPE_JOBTEST);
		LogUtil.w(tag, "---reset gps to false--");
	}

	/** 中断一个当前任务组测试 */
	private void interruptAnTestGroup() {

		// 收到中断消息后，判断当前是否启动超时监控线程，如果未执行开启中断退出监控线程
		LogUtil.w(tag, "---start interrupt test----");
		appModel.setTestInterrupt(true);
		appModel.setTestStoping(true);

		stopTimeOut = 0;
		callTestStopTimeout();

		if (serviceBinder != null) {
			serviceBinder.puaseOrInterruptTest(appModel.isTestInterrupt(), RcuEventCommand.DROP_USERSTOP);
		} else {
			appModel.setTestStoping(false);
		}

		LogUtil.w(tag, "---test bundle release----");
		if (serviceBinder != null) {
			unbindService(conn);
			serviceBinder = null;
		}


		LogUtil.i(tag, "---Stop TestService--");

		// 2014.2.10 手工停止的时候必须等其它操作完成后stopSelf
		// Intent testIntent = new
		// Intent(getApplicationContext(),TestService.class);
		// stopService(testIntent);

		// 停止陀螺仪打点
		// stopService(new Intent(getApplicationContext(),
		// RecordTraceService.class));
		// appModel.setGyroTest(false);
		// RecordTraceService.setAdjust(false);
	}

	/** 停止当前测试，并重启设置 */
	class InteruptTestAndRebootDevice extends Thread {
		public void run() {
			try {
				if (appModel.isTestJobIsRun()) {
					LogUtil.w(tag, "--InteruptTestAndRebootDevice --");
					Intent sendInterupTest = new Intent(WalkMessage.Action_Walktour_Test_Interrupt);
					sendBroadcast(sendInterupTest);
					appModel.setTestStoping(true);

					int timeOut = 0;
					while (appModel.isTestStoping() && timeOut < 1000 * 30) {
						LogUtil.w(tag, "--test stop ing---");
						timeOut += 200;
						Thread.sleep(200);
					}

					Thread.sleep(2000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			UtilsMethod.runRootCommand("reboot");
		}
	}

	/** 停止当前测试并重新初始化Trace */
	class InteruptTestAndRedoTraceInit extends Thread {
		public void run() {
			LogUtil.w(tag, "--InteruptTestAndRedoTraceInit --Interrupt Test");
			Intent sendInterupTest = new Intent(WalkMessage.Action_Walktour_Test_Interrupt);
			sendBroadcast(sendInterupTest);

			int timeOut = 0;
			while (appModel.isTestStoping() && timeOut < 1000 * 30) {
				LogUtil.w(tag, "--test stop ing---");
				try {
					timeOut += 200;
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			LogUtil.w(tag, "--InteruptTestAndRedoTraceInit --Reboot Trace");

			rebootTrace();
			while (!appModel.isTraceInitSucc()) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			LogUtil.w(tag, "--InteruptTestAndRedoTraceInit --Redo Auto Test," + appModel.isTestJobIsRun() + "--"
					+ appModel.isTestStoping());
			Intent sendRedoAutoTest = new Intent(AutoTestService.ACTION_DOWNLOAD_AFTER_BOOT);
			sendBroadcast(sendRedoAutoTest);
		}
	}

	/**
	 * 暂停测试，重启串口服务<BR>
	 * 当前服务收到暂停重启消息后，等待5秒用于文件关闭等待 然后执行重新初始化串口的动作
	 * 
	 * @author tangwq
	 * @version [WalkTour Client V100R001C03, 2012-9-27]
	 */
	class PuaseTestAndRedoTraceInit extends Thread {
		public void run() {
			try {
				LogUtil.w(tag, "---later five mini to reinittrace---");
				sleep(1000 * 5);
				rebootTrace();

				int timeOut = 0;
				while (!appModel.isTraceInitSucc() && timeOut <= 1000 * 90) {
					try {
						Thread.sleep(200);
						timeOut += 200;

						// 如果当前初始成功后,再暂停一秒,用于处理可能又取不到值的情况
						if (appModel.isTraceInitSucc()) {
							Thread.sleep(500);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// 如果当前初始成功发送继续测试并且需要创建文件消息,否则发送中断测试消息
				if (appModel.isTraceInitSucc()) {
					sendBroadcast(new Intent(WalkMessage.Action_Walktour_Test_Continue)
							.putExtra(WalkMessage.ContinueAndCreateRcuFile, true));
				} else {
					sendBroadcast(new Intent(WalkMessage.Action_Walktour_Test_Interrupt));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 发送重启串口服务
	 */
	private void rebootTrace(){
		Intent intent = new Intent();
		intent.setAction(ServerMessage.ACTION_REBOOT_TRACE);
		sendOrderedBroadcast(intent,null);
	}
}
