package com.walktour.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.dinglicom.dataset.EventManager;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.PPPRule;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.ServerMessage;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.ServerOperateType;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigAPN;
import com.walktour.control.config.ConfigAutoTest;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.R;
import com.walktour.gui.listener.ServerStatus;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.txt.TestPlan;
import com.walktour.gui.task.parsedata.txt.TestPlan.TimeRange;
import com.walktour.model.WalktourEvent;
import com.walktour.service.app.AutoTestService;
import com.walktour.service.app.Killer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * @Service 实现连接Fleet服务器,下载测试计划,同步时间,上传RCU文件, FleetService在Fleet页面打开时启动,
 *          启动后将一直处于运行状态.
 * @author qihang.li@dinglicom.com
 */
@SuppressLint("Wakelock")
public class FleetService extends Service {
	private static String TAG = "FleetService";
	private static int CONN_TIMEOUT = 16;// 连接超时(秒)
	private static int GPS_SEND_COUNT = 10;// 间隔上传GPS时的次数

	// BroadcastReceiver
	private MyBroadcastReceiver mReceiver;
	private static boolean hasRegistedBroadcast = false;

	public static synchronized boolean hasRegistedBroadcast() {
		return hasRegistedBroadcast;
	}

	public static synchronized void setHasRegistedBroadcast(boolean hasRegistedBroadcast) {
		FleetService.hasRegistedBroadcast = hasRegistedBroadcast;
	}

	// 是否已经登录服务器
	// private static boolean hasLogin ;
	private String startDownloadTime;// 记录开始下载的时间

	// 登录Fleet服务器的线程
	private static ThreadFleeter fleeter;

	// 读取进度的线程
	private Thread progressReader;
	private static Intent intentProgress;
	private static boolean isUploading;
	private static int uploadingProgress;// 上传进度

	// 唤醒CPU
	private PowerManager.WakeLock sWakeLock = null;

	private Context mContext;

	/** 拨号规则实现类，每次任务开始前重新例化 */
	private PPPRule pppRule = null;

	private ServerManager mServerMgr = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.w(TAG, "---OnCreate--");
		mContext = this;

		pppRule = new PPPRule(this);

		mServerMgr = ServerManager.getInstance(this);

		// 手机状态监听
		MyPhoneState.getInstance().listenPhoneState(getApplicationContext());
		// 注册事件
		regeditMyReceiver();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		LogUtil.w(TAG, "---OnStart--" + (intent == null));
		if (intent == null) {
			startService(new Intent(getApplicationContext(), Killer.class));
		}
	}

	@Override
	public void onDestroy() {
		LogUtil.w(TAG, "----onDestroy--");
		this.unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	// 注册广播接收器
	private void regeditMyReceiver() {
		IntentFilter filter = new IntentFilter();

		// 来自Fleet手工操作的广播
		filter.addAction(ServerMessage.ACTION_FLEET_DOWNLOAD_AUTOTEST);
		filter.addAction(ServerMessage.ACTION_FLEET_SYNC);
		filter.addAction(ServerMessage.ACTION_FLEET_STOPUPLD);// 停止上传
		filter.addAction(ServerMessage.ACTION_FLEET_RESULT);//
		filter.addAction(ServerMessage.ACTION_FLEET_LOGOUT);//

		// 来自自动执行的广播
		filter.addAction(ServerMessage.ACTION_FLEET_DOWNLOAD_AUTOTEST);
		filter.addAction(ServerMessage.ACTION_FLEET_DOWNLOAD_MANUAL);
		filter.addAction(ServerMessage.ACTION_FLEET_DOWNLOAD_STOP);
		filter.addAction(WalkMessage.ACTION_FLEET_SYNC);
		filter.addAction(WalkMessage.ACTION_FLEET_UPLOADGPS_ONECE);
		filter.addAction(WalkMessage.ACTION_FLEET_UPLOADGPS_ONECE_STOP);
		filter.addAction(WalkMessage.NOTIFY_TESTJOBDONE);
		filter.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE);
		filter.addAction(WalkMessage.Action_Walktour_Test_SMSTestStart);
		filter.addAction(AutoTestService.ACTION_FLEET_GPS_CONSTANTLY_START);
		filter.addAction(AutoTestService.ACTION_FLEET_GPS_CONSTANTLY_STOP);
		filter.addAction(AutoTestService.ACTION_FLEET_UPLOAD_AUTOTEST);
		filter.addAction(AutoTestService.ACTION_FLEET_UPLOAD_AUTOTEST_STOP);

		filter.addAction(ServerMessage.ACTION_FLEET_SEND_MESSAGE);

		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);

		if (mReceiver == null) {
			mReceiver = new MyBroadcastReceiver();
		}
		this.registerReceiver(mReceiver, filter);

		setHasRegistedBroadcast(true);
	}

	/**
	 * 广播接收器
	 */
	private class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			// 发送事件
			if (intent.getAction().equals(ServerMessage.ACTION_FLEET_SEND_MESSAGE)) {
				new ThreadWaiter(ServerOperateType.sendEvent).start();
			}

			// 如果是同步时间
			if (intent.getAction().equals(ServerMessage.ACTION_FLEET_SYNC)) {
				// 如果线程未实例化或者已经停止,new并start,因为服务器只允许一个fleeter登录
				new ThreadWaiter(ServerOperateType.syncTime).start();
			}

			// 如果是上传一次GPS
			if (intent.getAction().equals(WalkMessage.ACTION_FLEET_UPLOADGPS_ONECE)) {
				// 如果线程未实例化或者已经停止,new并start,因为服务器只允许一个fleeter登录
				new ThreadWaiter(ServerOperateType.uploadGPSOnce).start();
			}

			// 如果是上传一次GPS超时
			if (intent.getAction().equals(WalkMessage.ACTION_FLEET_UPLOADGPS_ONECE_STOP)) {
				if (fleeter != null) {
					if (fleeter.operateType == ServerOperateType.uploadGPSOnce) {
						fleeter.setTimeOut(true);
					}
				}
			}

			// 如果是开始连续上传GPS
			if (intent.getAction().equals(AutoTestService.ACTION_FLEET_GPS_CONSTANTLY_START)) {
				new ThreadWaiter(ServerOperateType.uploadGPSConstantly).start();
			}

			// 如果是停止连续上传GPS
			if (intent.getAction().equals(AutoTestService.ACTION_FLEET_GPS_CONSTANTLY_STOP)) {
				if (fleeter != null) {
					if (fleeter.getOperateType() == ServerOperateType.uploadGPSConstantly) {
						fleeter.setTimeOut(true);
					}
				}
			}

			// 如果是短信通知自动测试
			if (intent.getAction().equals(WalkMessage.Action_Walktour_Test_SMSTestStart)) {
				// 下载测试计划,下载完成后开始测试
				LogUtil.w(TAG, "receive auto test form sms");
				new ThreadWaiter(ServerOperateType.downAutoTest).start();
			}

			// 如果是自动测试下载测试计划
			if (intent.getAction().equals(ServerMessage.ACTION_FLEET_DOWNLOAD_AUTOTEST)) {
				// 如果线程未实例化或者已经停止,new并start,因为服务器只允许一个fleeter登录
				new ThreadWaiter(ServerOperateType.downAutoTest).start();
			}

			// 如果是在任务列表中手工下载测试计划
			if (intent.getAction().equals(ServerMessage.ACTION_FLEET_DOWNLOAD_MANUAL)) {
				// 如果线程未实例化或者已经停止,new并start,因为服务器只允许一个fleeter登录
				new ThreadWaiter(ServerOperateType.downManual).start();
			}

			// 如果是停止下载测试计划
			if (intent.getAction().equals(ServerMessage.ACTION_FLEET_DOWNLOAD_STOP)) {
				if (fleeter != null) {
					if (fleeter.getOperateType() == ServerOperateType.downAutoTest) {
						fleeter.setTimeOut(true);
					}
				}
			}

			// 如果是自动测试上传RCU数据
			if (intent.getAction().equals(AutoTestService.ACTION_FLEET_UPLOAD_AUTOTEST)) {
				new ThreadWaiter(ServerOperateType.uploadAutoTestFile).start();
			}

			// 如果是停止自动测试上传RCU数据
			if (intent.getAction().equals(AutoTestService.ACTION_FLEET_UPLOAD_AUTOTEST_STOP)) {
				if (fleeter != null) {
					if (fleeter.getOperateType() == ServerOperateType.uploadAutoTestFile) {
						if (fleeter.handle != 0) {
							fleeter.setTimeOut(true);
							setstopupld(fleeter.handle);
						}
					}
				}
			}

			// 如果是中断上传
			if (intent.getAction().equals(ServerMessage.ACTION_FLEET_STOPUPLD)) {
				// 获取停止类型
				String stopType = intent.getExtras().getString(ServerMessage.KEY_STOPTYPE);

				if (fleeter != null) {
					if (stopType.equals(fleeter.operateType.toString())) {
						// 如果已经获得句柄
						if (fleeter.handle != 0) {
							setstopupld(fleeter.handle);// 中断后，已经初始化的fleet的handle会实效，其它操作都会失败
						}

						LogUtil.w(TAG, "--->isNull?" + (fleeter == null));
						// 如果线程正在运行设置中断
						LogUtil.w(TAG, "--->isAlive?" + fleeter.isAlive());
						if (fleeter.isAlive()) {
							// 发送广播通知界面
							sendResultBroadcast(ServerMessage.KEY_STOPPING);
							fleeter.setTimeOut(true);
						}
						sendEvent("Stop Manually");
					}
				}

			} // end outside if

			// 如果是退出软件
			else if (intent.getAction().equals(ServerMessage.ACTION_FLEET_LOGOUT)) {
				new ThreadWaiter(ServerOperateType.logout).start();
			}
		}
	}

	/**
	 * 发送广播
	 * 
	 * @param result
	 *          操作结果标志
	 * @param msg
	 *          附带消息
	 */
	private void sendResultBroadcast(int result, String msg) {
		Intent intent = new Intent();
		intent.setAction(ServerMessage.ACTION_FLEET_RESULT);
		sendBroadcast(intent);
	}

	/**
	 * 发送广播
	 * 
	 * @param result
	 *          操作结果标志
	 */
	private void sendResultBroadcast(int result) {
		Intent intent = new Intent();
		intent.setAction(ServerMessage.ACTION_FLEET_RESULT);
		sendBroadcast(intent);
	}

	/**
	 * 登录线程：连接并登录到Fleet服务器,登录后可以同步时间和下载测试计划
	 * 
	 * @注意 Fleet服务器不允许重复登录,所有只能开一个线程,也就是同一时间内只允许"下载 ","上传","同步" 这几个任务中的一个在运行.
	 */
	protected class ThreadFleeter extends Thread {

		// fleet连接相关
		private int handle;// 此handle最后务必释放
		private ServerOperateType operateType;
		private boolean hasLogin = false;
		private boolean timeOut = false;

		/**
		 * @param operateType
		 *          one of the OPERATE_TYPE.name()
		 */
		public ThreadFleeter(ServerOperateType operateType) {
			this.operateType = operateType;
		}

		public ServerOperateType getOperateType() {
			return this.operateType;
		}

		@Override
		public void run() {

			// 唤醒CPU
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
			sWakeLock.acquire();

			FleetManager.getService().setFleeterAlive(true);

			// 登录3次直到成功(手工下载除外)
			for (int i = 0; i < 3 && operateType != ServerOperateType.downManual; i++) {

				if (operateType == ServerOperateType.uploadGPSConstantly) {
					if (!TestPlan.getInstance().isUploadConstantly()) {
						break;
					}
				}

				if (!isTimeOut()) {
					hasLogin = login();
					LogUtil.i(TAG, "login " + (hasLogin ? "success" : "fail"));
					if (hasLogin) {
						break;// 登录成功
					} else {
						free();// 登录失败
						// 如果是退出软件,直接退出而不重新登录
						if (operateType == ServerOperateType.logout) {
							Intent intent = new Intent(FleetService.this, Killer.class);
							startService(intent);
						}
						if (i < 2) {

							// 休眠1分钟
							sendEvent("Reconnect to server after one minute");
							for (int j = 60; j > 0 && !isTimeOut(); j--) {
								if (operateType == ServerOperateType.uploadGPSConstantly) {
									if (!TestPlan.getInstance().isUploadConstantly()) {
										break;
									}
								}
								try {
									Thread.sleep(1 * 1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}
				} else {
					LogUtil.w(TAG, "--->fleeter is stop Manual or timeout");
				}
			}

			// 如果是连续上传GPS，并且没登录成功，一直登录到成功为止
			if (fleeter != null) {
				// 连接上传
				if (!hasLogin && operateType == ServerOperateType.uploadGPSConstantly) {
					while (!isTimeOut() && TestPlan.getInstance().isUploadConstantly()) {
						hasLogin = login();
						if (hasLogin) {
							break;
						} else {
							free();// 登录失败
							for (int j = 0; j <= 30 && !isTimeOut() && TestPlan.getInstance().isUploadConstantly(); j++) {
								try {
									Thread.sleep(1 * 1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}

			// 下载测试计划
			if (fleeter != null) {
				// 自动测试下载，并且没登录成功，一直登录到成功为止
				if (!hasLogin && operateType == ServerOperateType.downAutoTest) {
					while (!isTimeOut()) {
						hasLogin = login();
						LogUtil.i(TAG, "login " + (hasLogin ? "success" : "fail"));
						if (hasLogin) {
							break;
						} else {
							free();// 登录失败
							for (int j = 0; j <= 30 && !isTimeOut(); j++) {
								try {
									Thread.sleep(1 * 1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}

				}

				// 业务测试下载
				if (!hasLogin && operateType == ServerOperateType.downManual) {
					hasLogin = login();
					if (!hasLogin) {
						mServerMgr.OnServerStatusChange(ServerStatus.loginFail, "");
					}
				}
			}

			// 登录成功后执行动作:上传、下载、GPS、logout等
			if (fleeter != null) {

				if (!isTimeOut() && hasLogin) {
					if (operateType == ServerOperateType.uploadGPSOnce) {// 一次上传GPS信息
						sendGps(GPS_SEND_COUNT);
					} else if (operateType == ServerOperateType.uploadGPSConstantly) {// 连续上传GPS
						sendGpsConstantly();
					} else if (operateType == ServerOperateType.syncTime) {// 同步时间
						sendEvent("Synchronize Time");
						boolean success = synchTime();
						if (success) {
							Intent intent = new Intent();
							intent.setAction(ServerMessage.ACTION_FLEET_SYNC_DONE);
							sendBroadcast(intent);
						}
					} else if (operateType == ServerOperateType.downManual) {// 下载测试计划
						if (downLoadTask()) {
							//下载完后解析适配计划
						} else {
							mServerMgr.OnServerStatusChange(ServerStatus.configDLFail, "");
						}
					} else if (operateType == ServerOperateType.downAutoTest) {// 下载测试计划
						sendEvent("Download Task");
						if (downLoadTask()) {
							//下载完后解析适配计划
							sendBroadcast(new Intent(ServerMessage.ACTION_FLEET_DOWNLOAD_DONE));
						} else {
							ThreadWaiter task = new ThreadWaiter(ServerOperateType.downAutoTest);
							ThreadTimer mTimer = new ThreadTimer(task, 10);
							mTimer.start();
						}
					} else if (operateType == ServerOperateType.uploadTestFile // 上传测试任务的RCU文件
							|| operateType == ServerOperateType.uploadIndoorFile
							|| operateType == ServerOperateType.uploadAutoTestFile) {
						sendEvent("Upload Test File");
						// uploadTaskFiles();
					} else if (operateType == ServerOperateType.logout) {// 退出软件
						// fleetlogout( handle );
						// 2013.6.10修改为自定义事件
						WalktourEvent event = new WalktourEvent(mContext, "logout", System.currentTimeMillis(),
								RcuEventCommand.EVENT_LOG_OUT, RcuEventCommand.EVENT_REASON_NONE);
						selfsetmsg(handle, event.getEventBytes());
					} else if (operateType == ServerOperateType.sendEvent) {// 发送自定义事件
						sendWalktourEvents(ServerManager.getInstance(mContext).getEventList());
					}
					/*
					 * else if( operateType ==FleeterType.uploadIndoorFile
					 * ){//上传室内测试的RCU文件 sendEvent("Upload Indoor File");
					 * uploadIndoorFiles(); }
					 */
				}

			}

			// 如果是上传自动测试数据
			if (operateType == ServerOperateType.uploadAutoTestFile) {
				sendBroadcast(new Intent(AutoTestService.ACTION_FLEET_UPLOAD_AUTOTEST_DONE));
			}

			// 如果是退出软件
			if (operateType == ServerOperateType.logout) {
				Intent intent = new Intent(FleetService.this, Killer.class);
				startService(intent);
			}

			// 释放连接
			free();

			// 标志位重设
			FleetManager.getService().setFleeterAlive(false);

			// 释放休眠锁
			if (sWakeLock != null) {
				sWakeLock.release();
				sWakeLock = null;
			}

			fleeter = null;
		}// end method run

		/**
		 * 登录Fleet服务器
		 */
		private boolean login() {

			// 先判断线程是否被中断
			if (isTimeOut()) {
				LogUtil.w(TAG, "--->fleeter is stop Manual or timeout");
				return false;
			}

			// 等待网络可用
			boolean isNetworkAvirable = MyPhoneState.getInstance().isNetworkAvirable(mContext);
			for (int i = 0; i < 30 && !isNetworkAvirable && !isTimeOut(); i++) {
				isNetworkAvirable = MyPhoneState.getInstance().isNetworkAvirable(mContext);
				LogUtil.e(TAG, "isNetworkAvirable:" + isNetworkAvirable);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (!isNetworkAvirable) {
				return false;
			}

			// 没有连接到wifi时进行切换到APN接入点为net类型
			if (!MyPhoneState.getInstance().hasWifiConnect(mContext)) {
				boolean pppOK = setDataNetwork();
				if (!pppOK) {
					Log.e(TAG, "ppp network unavailable");
					return false;
				}
			}

			// 第一步:初始化句柄
			handle = inithandle();

			// 读取设置
			String ip = "";
			int port = 0;
			switch (this.operateType) {
			// 如果是手工上传文件,引用Walktour->系统设置->上传设置
			case uploadTestFile:
			case uploadIndoorFile:
			case downManual:
			case logout:
			case syncTime:
			case sendEvent:
				ServerManager server = ServerManager.getInstance(FleetService.this);
				ip = server.getUploadFleetIp();
				port = server.getUploadFleetPort();
				// ip或者port设置不正确时通知用户
				if (ip.trim().toString().length() == 0 || port == 0) {
					showNotification(getString(R.string.fleet_set_notset_notify), ServerMessage.ACTION_FLEET_SERVER_NOTSET);
					return false;
				}
				break;

			case uploadAutoTestFile:
			case downAutoTest:
			case uploadGPSOnce:
			case uploadGPSConstantly:
				ConfigAutoTest auto = new ConfigAutoTest();
				ip = auto.getIp();
				try {
					port = Integer.parseInt(auto.getPort());
				} catch (Exception e) {
					e.printStackTrace();
					port = 0;
				}
				break;

			default:
				ConfigAutoTest config2 = new ConfigAutoTest();
				ip = config2.getIp();
				try {
					port = Integer.parseInt(config2.getPort());
				} catch (Exception e) {
					e.printStackTrace();
					port = 0;
				}
				break;
			}

			// 第二步:初始化Fleet
			int fleetInit = 0;
			fleetInit = fleetinit(handle, ip, port);
			// 提示正在连接服务器
			sendResultBroadcast(ServerMessage.KEY_CONNCTTING);
			LogUtil.i(TAG, "-->Initing fleet...");
			LogUtil.i(TAG, "-->Connecting to " + ip + ":" + port);

			if (fleetInit == 1) {// 如果初始化连接成功
			} else {
				LogUtil.w(TAG, "fleet init fail");
				sendEvent("Server connection fail.");
				sendResultBroadcast(ServerMessage.KEY_ERR_CONNCT);
				return false;
			}

			// 第三步:初始化Socket
			LogUtil.i(TAG, "-->initing socket...");
			int initSocket = initsocket(handle);
			if (initSocket == 1) {
			} else {
				LogUtil.w(TAG, "socket init fail");
				sendEvent("Socket init fail.");
				sendEvent("Please check your Ip and Port.");
				sendResultBroadcast(ServerMessage.KEY_ERR_CONNCT);
				return false;
			}

			// 第四步:连接服务器
			if (!isTimeOut()) {
				LogUtil.i(TAG, "-->Connecting to server...");
				sendEvent("Connect to server:" + this.operateType.toString());
				int success = 0;

				// 如果是上传文件,walktour数据和监控数据不同
				success = connect(handle, CONN_TIMEOUT);

				if (isTimeOut()) {// 如果被停止
					LogUtil.w(TAG, "--->fleeter is stop Manual or timeout");
					return false;
				}

				if (success == 1) {
					LogUtil.i(TAG, "--->Login success");
					sendEvent("Connect successful");
				} else {
					LogUtil.i(TAG, "--->Connect fail");
					sendEvent("Connect fail");
					sendResultBroadcast(ServerMessage.KEY_ERR_CONNCT);
					return false;
				}
			} // end if

			// 第五步:登录服务器
			if (!isTimeOut()) {
				LogUtil.i(TAG, "-->waiting for login...");
				sendEvent("Waiting for login...");
				int longinResult = 0;

				// 获取手机IMEI号
				String GUID = MyPhoneState.getInstance().getGUID(FleetService.this);
				longinResult = fleetlogin(handle, GUID);

				if (longinResult == 1) {
					LogUtil.i(TAG, "--->Login success");
					sendEvent("Login success.");
					sendResultBroadcast(ServerMessage.KEY_LOGIN);
					return true;
				} else {
					LogUtil.w(TAG, "--->Login in fail");
					sendEvent("Login fail");
					sendResultBroadcast(ServerMessage.KEY_ERR_LOGIN, getString(R.string.fleet_error_login));
				}
			} else {
				LogUtil.w(TAG, "--->fleeter is timeout");
			}

			return false;
		}

		/**
		 * 重新登录服务器
		 * 
		 * @param reconnectTime
		 *          重新连接的次数
		 * @param sleepTime
		 *          两次连接之间的时间(秒)
		 */
		// private void relogin(int reconnectTime,int sleepTime ){
		// if( isTimeOut() ){
		// return;
		// }
		// sleepForSecond( 3 );
		// sendEvent("Reconnect to server now");
		// LogUtil.w(TAG, "Reconnect to server now");
		//
		// //先清空句柄
		// free();
		//
		// //登录3次直到成功
		// for( int i=0; i<reconnectTime && !isTimeOut(); i++ ){
		//
		// if( !isTimeOut() ){
		// hasLogin = login();
		// if( hasLogin ) {
		// break;//登录成功
		// }else{
		// free();//登录失败
		// if( i<2 ){
		//
		// //休眠1分钟
		// for(int j=sleepTime;j>0 && !isTimeOut();j-- ){
		// sendEvent("Reconnect to server after "+j+" seconds");
		// if( operateType == ServerOperateType.uploadGPSConstantly ){
		// if( !TestPlan.getInstance().isUploadConstantly() ){
		// break;
		// }
		// }
		// try {
		// Thread.sleep(1*1000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// }
		// }else{
		// LogUtil.w( TAG, "--->fleeter is stop Manual or timeout" );
		// }
		// }
		// }

		/**
		 * 登录后同步时间
		 */
		private boolean synchTime() {
			// 先判断线程是否被中断
			if (isTimeOut()) {
				Log.e(TAG, "---fleeter is stop Manual or timeout");
				return false;
			}

			long start = System.currentTimeMillis();
			long fleetSync = sync(handle);
			int delay = (int) (System.currentTimeMillis() - start);

			if (fleetSync != 0) {
				// 平台的时间是精确到秒
				fleetSync = fleetSync * 1000 - (delay / 2);
				// 更新手机系统时间
				sendEvent("Synchronize time success," + UtilsMethod.getSimpleDateFormat1(fleetSync));
				Log.i(TAG, "Synchronize time success," + UtilsMethod.getSimpleDateFormat1(fleetSync));
				UtilsMethod.setTime(fleetSync, mContext);
				return true;
			} else {
				Log.e(TAG, "synchroniz time fail and stop now");
				sendEvent("Synchronize time fail");
				return false;
			}
		}

		/**
		 * 发送GPS信息
		 * 
		 * @param count
		 *          要发送的次数
		 */
		private void sendGps(int count) {

			int i = 0;
			while (fleeter != null && GpsInfo.getInstance().getLocation() == null) {
				i++;
				if (!isTimeOut()) {
					LogUtil.w(TAG, "--->waiting for gps info in onece upload");
					if (i >= 10) {
						sendEvent("Waiting for gps");
						i = 0;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					sendEvent("Request gps timeout");
					LogUtil.w(TAG, "Request gps timeout");
					break;
				}
			}

			if (fleeter != null) {

				LogUtil.w(TAG, "--->is fleeter interrupted?" + isTimeOut());
				if (!isTimeOut()) {
					Location location = GpsInfo.getInstance().getLocation();

					for (int c = 0; c < GPS_SEND_COUNT && !isTimeOut(); c++) {
						if (location != null) {
							float longitude = (float) location.getLongitude();
							float latitude = (float) location.getLatitude();
							LogUtil.w(TAG, "--->longitude:" + longitude + ",latitude:" + latitude);
							sendEvent("longitude:" + longitude + ",latitude:" + latitude);
							// int result = gpsmsg( handle,longitude,latitude);
							/*
							 * LogUtil.w(TAG, "--->upload gps result:"+
							 * (result!=0?"success":"fail") ); sendEvent(
							 * "Upload location result:"+ (result!=0?"success":"fail"));
							 */
						}

						try {
							Thread.sleep(3 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					sendBroadcast(new Intent(WalkMessage.ACTION_FLEET_UPLOADGPS_DONE));
				} else {
					sendEvent("Upload is stopped");
					LogUtil.w(TAG, "Upload is stooped");
				}
			}

		}

		/**
		 * 连续发送GPS信息
		 */
		private void sendGpsConstantly() {
			int resetTime = 20 * 60;// 每次上传间隔是3秒，3*20*60相当于每小时重新连接服务器一次
			int i = 0;
			while (GpsInfo.getInstance().getLocation() == null && TestPlan.getInstance().isUploadConstantly()) {
				i++;
				if (fleeter != null) {

					if (!isTimeOut()) {
						LogUtil.w(TAG, "--->waiting for gps info in constance upload");
						if (i >= 10) {
							sendEvent("Waiting for gps");
							i = 0;
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						sendEvent("Request gps timeout");
						LogUtil.w(TAG, "Request gps timeout");
						break;
					}
				}
			}

			if (fleeter != null) {
				if (!isTimeOut()) {
					sendEvent("Upload location constantly");
				}

				int c = 0;
				while (!isTimeOut() && TestPlan.getInstance().isUploadConstantly()) {
					Location location = GpsInfo.getInstance().getLocation();
					if (location != null && fleeter != null) {
						float longitude = (float) location.getLongitude();
						float latitude = (float) location.getLatitude();
						LogUtil.w(TAG, "--->longitude:" + longitude + ",latitude:" + latitude);
						sendEvent("longitude:" + longitude + ",latitude:" + latitude);
						int result = gpsmsg(handle, longitude, latitude);
						/*
						 * LogUtil.w(TAG, "--->upload gps result:"+
						 * (result!=0?"success":"fail") ); sendEvent(
						 * "Upload location result:"+ (result!=0?"success":"fail"));
						 */

						boolean needToReConnect = false;

						// 如果上传失败断开重新登录
						if (result == 0) {
							sendEvent("Send location fail,reconnect to server");
							needToReConnect = true;
						}

						// 每小时重连一次
						c++;
						if (c > resetTime) {
							sendEvent("Reconnect to server once an hour");
							needToReConnect = true;
						}

						// 网络不可用时重连
						if (!MyPhoneState.getInstance().isNetworkAvirable(FleetService.this)) {
							sendEvent("Network avirable,reconnect to server");
							needToReConnect = true;
						}

						if (needToReConnect) {
							fleeter.setTimeOut(true);
							if (TestPlan.getInstance().isUploadConstantly()) {
								ThreadWaiter task = new ThreadWaiter(ServerOperateType.uploadGPSConstantly);
								ThreadTimer mTimer = new ThreadTimer(task, 10);
								mTimer.start();
							}
						}
					} else {
						sendEvent("Location is stopped");
						LogUtil.w(TAG, "Location is stopped");
					}

					if (fleeter != null) {

						if (isTimeOut()) {
							break;
						} else {
							try {
								Thread.sleep(3 * 1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}

				}
			}

		}

		/**
		 * 登录后同步时间后开始下载测试任务
		 */
		private boolean downLoadTask() {

			// 先判断线程是否被中断
			if (isTimeOut()) {
				LogUtil.w(TAG, "--->fleeter is stop Manual or timeout");
				return false;
			}

			LogUtil.i(TAG, "task downloading...");
			sendEvent("Task downloading...");
			int getTaskSuccess = 0;
			int count = 0;

			// 获取当前时间
			SimpleDateFormat formatter = new SimpleDateFormat("yy:MM:dd:hh:mm:ss", Locale.getDefault());
			// 记录当前时间
			startDownloadTime = formatter.format(new Date());
			File file = AppFilePathUtil.getInstance().getAppConfigFile(TestPlan.FILE_NAME);
			// 开始下载
			while (getTaskSuccess == 0 && count <= 5) {
				sleep(200);

				LogUtil.i(TAG, "Time:" + count + "handle:" + handle + " path:" + file.getAbsolutePath());
				getTaskSuccess = GetConfig(handle, file.getAbsolutePath());
				count++;
			}

			// 如果成功下载
			if (getTaskSuccess == 1) {
				LogUtil.i(TAG, "testTask download successfulful");

				// Jni下载的文件改为可读
				// UtilsMethod.runCommand("chmod 400 "+TestPlan.TEMP_FILE);
				// 文件事先已经写入,不需要再给权限

				sendEvent("Download success.");
				sendResultBroadcast(ServerMessage.KEY_TASK_DLSUCCESS);
				disposeTask();
				// 下载的测试计划不为空
				LogUtil.w(TAG, "--->download test's plan succeessful");
				return true;
			} else {
				LogUtil.w(TAG, "testTask download fail and stop now");
				sendEvent("Download fail.");
				sendResultBroadcast(ServerMessage.KEY_TASK_DLFAIL);
				return false;
			}
		}

		/***
		 * 下载完后解析适配计划
		 */
		private void disposeTask() {
			boolean effect = TestPlan.getInstance().getTestPlanFromFile();
			if (effect) {
				ArrayList<TimeRange> rangeList = TestPlan.getInstance().getTimeRangeList();
				TaskListDispose.getInstance().getTestPlanConfigFromFleet(rangeList);
				mServerMgr.OnServerStatusChange(ServerStatus.configUpdateSuccess, "");
			} else {
				mServerMgr.OnServerStatusChange(ServerStatus.configUpdateFail, "");
			}
		}

		/**
		 * 上传问题投诉的数据管理页面选中上传的文件
		 */

		/**
		 * 执行命令行操作并输出流
		 * 
		 * @param command
		 * @return
		 */
		public boolean runCommand(String command) {
			Process process = null;
			InputStream inStream = null;
			try {
				process = Runtime.getRuntime().exec(command);
				inStream = process.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
				String str;
				while ((str = br.readLine()) != null) {
					LogUtil.w(TAG, "---" + str);
				}

				return true;
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
		}

		/**
		 * 上传Walktour数据管理页面选中上传的文件
		 */
		// private void uploadTaskFiles(){
		// //先判断线程是否被中断
		// if( isTimeOut() ){
		// LogUtil.w( TAG, "--->fleeter is stop Manual or timeout" );
		// return;
		// }
		//
		// boolean hasTask = true;//当前是否有上传任务
		// //记录成功上传的文件
		// ArrayList<MyFileModel> successList = new ArrayList<MyFileModel>();
		// //重新从数据库获取文件模型，因为页面不打开时未必存在这个模型
		// ArrayList<MyFileModel> modelList =
		// TaskFileList.getInstance(getApplicationContext()).getFileListInstance();
		// while( hasTask && !isTimeOut() ){
		// LogUtil.w(TAG, "Test File List's Size:"+modelList.size());
		// //遍历上传列表，上传进度为0的文件
		// for( int i=0;i<modelList.size() && !isTimeOut();i++ ){
		// MyFileModel fileModel = modelList.get(i);
		// if( fileModel.getProgress()==MyFileModel.STATUS_UPLOAD_WAITING ){
		// LogUtil.w(TAG, "--->upload file:"+fileModel.getRcuFilePath());
		// fileModel.setUPloading(true);//标识文件为正在上传
		// //修改数据库
		// TaskFileList.getInstance(getApplicationContext())
		// .updateFile(fileModel.getRcuFilePath(),fileModel );
		//
		// //上传列表中第i个文件
		// /**
		// * 上传一个RCU文件
		// * @param filePath RCU文件的绝对路径
		// * @return -1,被中断 0,上传失败 1,上传成功
		// * 2,初始化文件失败 3,文件大小有问题 5,发送命令到服务器失败
		// * */
		// int result = 0;
		// for(int u=0;u<3 && !isTimeOut() ;u++){
		// result = uploadOneFile( fileModel,false );
		// LogUtil.i(TAG, "uploadOneFile:"+result);
		// if( result ==1 ){
		// //上传成功后退出
		// break;
		// }else{
		// if( result == 3 ){
		// sendEvent("Upload file fail");
		// break;
		// }
		// if( result ==4){// 上传过程中出现错误4时，不断开登录进行续传
		// long s = System.currentTimeMillis();
		// while( result ==4 ){
		// result = uploadOneFile( fileModel,false );
		// LogUtil.i(TAG, "uploadOneFile:"+result);
		// if( System.currentTimeMillis()-s > 60*60*1000 ){
		// break;
		// }
		// }
		// }
		//
		// if( !isTimeOut() && result!=1 ){
		// sendEvent("Upload file fail");
		// //提示上传出现错误
		// sendResultBroadcast(ServerMessage.KEY_UL_ERROR);
		// //上传失败后重新连接服务器
		// relogin(3,30);
		// }
		// }
		// }
		// fileModel.setUPloading(false);
		//
		// //成功
		// if(result==1){
		//
		// //修改model值
		// LogUtil.w(TAG, "--->set model status");
		// fileModel.setProgress( MyFileModel.STATUS_UPLOAD_FINISH );
		// //更新数据库
		// TaskFileList.getInstance(getApplicationContext())
		// .updateFile( fileModel.getRcuFilePath(),fileModel );
		//
		// //通知页面更新
		// sendResultBroadcast(
		// ServerMessage.KEY_UL_SUCCESS,fileModel.getRcuFilePath() );
		// LogUtil.w(TAG, "upload successful:"+fileModel.getRcuName());
		// sendEvent( "Upload result:success");
		//
		// successList.add( fileModel );
		//
		// }
		//
		// //上传失败
		// else if( result == 0 ){
		// //修改model值
		// fileModel.setProgress( uploadingProgress );
		//
		// //更新数据库
		// TaskFileList.getInstance(getApplicationContext())
		// .updateFile( fileModel.getRcuFilePath(),fileModel );
		//
		// //通知页面更新
		// LogUtil.w(TAG, "upload fail:"+fileModel.getRcuName());
		// sendEvent( "Upload result:fail");
		// sendResultBroadcast( ServerMessage.KEY_UL_FAIL,fileModel.getRcuFilePath()
		// );
		// }
		//
		// //发送命令到服务器错误
		// else if(result ==5){
		// //修改model值
		// fileModel.setProgress( MyFileModel.STATUS_UPLOAD_INIT );
		// //更新数据库
		// TaskFileList.getInstance(getApplicationContext())
		// .updateFile( fileModel.getRcuFilePath(),fileModel );
		//
		// //通知页面更新
		// LogUtil.w(TAG, "upload fail:"+fileModel.getRcuName());
		// sendEvent( "Upload result:fail");
		// sendResultBroadcast( ServerMessage.KEY_UL_FAIL,fileModel.getRcuFilePath()
		// );
		// }
		//
		// //初始化文件失败
		// else if( result ==2 || result ==3 ){
		// //修改model值
		// fileModel.setProgress( MyFileModel.STATUS_UPLOAD_ERROR );
		// //更新数据库
		// TaskFileList.getInstance(getApplicationContext())
		// .updateFile( fileModel.getRcuFilePath(),fileModel);
		//
		// //通知页面更新
		// LogUtil.w(TAG, "upload fail:"+fileModel.getRcuName());
		// sendEvent( "Upload result:fail");
		// sendResultBroadcast( ServerMessage.KEY_UL_FAIL,fileModel.getRcuFilePath()
		// );
		// }
		//
		// }
		// }
		// hasTask = false;
		//
		// //重新遍历列表是否还有待上传的文件
		// //遍历上传列表，上传进度为0的文件为待上传
		// for( int i=0;i<modelList.size();i++ ){
		// if(modelList.get(i).getProgress()==MyFileModel.STATUS_UPLOAD_WAITING ){
		// hasTask = true;
		// break;
		// }
		// }
		//
		// }
		//
		// //告知FleetSetting所有上传动作已经完成
		// if( successList.size() > 0){
		// sendResultBroadcast( ServerMessage.KEY_UL_FINISH );
		//
		// //发邮件通知
		// SendMailReport mailReport = new SendMailReport();
		// mailReport.sendReport(successList, mContext);
		// }
		//
		// }

		/**
		 * 上传Walktour室内测试页面选中上传的文件
		 */
		/*
		 * private void uploadIndoorFiles(){
		 * 
		 * //先判断线程是否被中断 if( isTimeOut() ){ LogUtil.w( TAG,
		 * "--->fleeter is stop Manual or timeout" ); return; } boolean hasTask =
		 * true;//当前是否有上传任务 int uploaded = 0;//记录成功上传的个数 //重新从数据库获取文件模型
		 * ArrayList<MyFileModel> modelList =
		 * IndoorFileList.getInstance(getApplicationContext()).getFileListInstance()
		 * ; while( hasTask ){ LogUtil.w(TAG, "Test File List's Size:"
		 * +modelList.size()); //遍历上传列表，上传进度为0的文件 for( int
		 * i=0;i<modelList.size();i++ ){ MyFileModel fileModel = modelList.get(i);
		 * if( fileModel.getProgress()==0 ){ LogUtil.w(TAG, "--->upload file:"
		 * +fileModel.getFilePath()); fileModel.setUPloading(true);//标识文件为正在上传
		 * //修改数据库 IndoorFileList.getInstance(getApplicationContext()) .updateFile(
		 * fileModel.getFilePath(),fileModel );
		 * 
		 * //上传列表中第i个文件,上传3次直到成功 int result =0; for( int t=0;t<3;t++){ result =
		 * uploadOneFile( fileModel,false ); if( result ==1 ){ break; } }
		 * 
		 * fileModel.setUPloading(false); if(result==1){ //修改model值 LogUtil.w(TAG,
		 * "--->set model status"); fileModel.setProgress(
		 * MyFileModel.STATUS_UPLOAD_FINISH ); //更新数据库
		 * IndoorFileList.getInstance(getApplicationContext()) .updateFile(
		 * fileModel.getFilePath(),fileModel );
		 * 
		 * //通知页面更新 sendResultBroadcast(
		 * FleetMessage.KEY_UL_SUCCESS,fileModel.getName() ); LogUtil.w(TAG,
		 * "upload successful:"+fileModel.getName()); sendEvent( "Done");
		 * 
		 * uploaded ++; }else{ //修改model值 fileModel.setProgress(
		 * MyFileModel.STATUS_UPLOAD_ERROR ); //更新数据库
		 * IndoorFileList.getInstance(getApplicationContext())
		 * .updateFile(fileModel.getFilePath(),fileModel );
		 * 
		 * //通知页面更新 LogUtil.w(TAG, "upload fail:"+fileModel.getName()); sendEvent(
		 * "Fail to upload "+fileModel.getName()); sendResultBroadcast(
		 * FleetMessage.KEY_UL_FAIL,fileModel.getName() ); }
		 * 
		 * } } hasTask = false;
		 * 
		 * //重新遍历列表是否还有待上传的文件 //遍历上传列表，上传进度为0的文件为待上传 for( int
		 * i=0;i<modelList.size();i++ ){ if(modelList.get(i).getProgress()==0 ){
		 * hasTask = true; break; } }
		 * 
		 * }
		 * 
		 * //告知FleetSetting所有上传动作已经完成 if( uploaded > 0){ sendResultBroadcast(
		 * FleetMessage.KEY_UL_FINISH ); }
		 * 
		 * }
		 */

		/**
		 * 上传一个RCU文件
		 * 
		 * @return -1,被中断 0,上传失败 1,上传成功 2,初始化文件失败 3,文件大小有问题 4,网络上传错误 比如链接问题等
		 *         5,发送命令到服务器失败
		 */
		// private int uploadOneFile(MyFileModel fileModel,boolean hasFlag){
		// //先判断线程是否被中断
		// if( isTimeOut() ){
		// LogUtil.w( TAG, "--->fleeter is stop Manual or timeout" );
		// return -1;
		// }
		//
		// uploadingProgress = 0;
		// String filePath = fileModel.getRcuFilePath();
		//
		// try{
		// File file = new File(filePath);
		// if( file.isFile() ) {
		// if( file.length()<1000 ){
		// LogUtil.w(TAG, file.getAbsolutePath()+" file size is 0");
		// return 3;
		// }
		// }else{
		// LogUtil.w(TAG, file.getAbsolutePath()+" is not file");
		// return 3;
		// }
		// }catch(Exception e){
		// return 3;
		// }
		//
		// String fileName = filePath.substring(filePath.lastIndexOf("/")+1,
		// filePath.length() ) ;
		//
		// /*upldsize
		// 函数功能: 发送上传文件命令，获取到fleet服务器上文件大小信息
		// 入参: 总句柄
		// 返回值: 1表示文件已经上传完毕，请选择另外需要上传的文件
		// 4 表示获取大小成功 准备上传文件
		// 3 表示服务器返回文件大小信息有问题
		// 5 表示发送命令到fleet服务器失败
		// */
		// int upsizeResult = upldsize(handle,filePath);
		// sendEvent( "Init file:"+fileName );
		// LogUtil.w(TAG, "Init file:"+fileName );
		//
		// //5 表示发送命令到fleet服务器失败
		// if( upsizeResult ==5){
		// sendEvent( "Initial file fail:COMMAND_ERROR");
		// sendResultBroadcast( ServerMessage.KEY_UL_INITFAIL ,filePath);
		// return 5;
		// }
		//
		// //1表示文件上传过，不需要再上传
		// if( upsizeResult==1){
		// LogUtil.w(TAG, fileName +" is exist in server,will not be upload");
		// //发送广播
		// sendEvent(""+ fileName +" is exist in server");
		// sendResultBroadcast(ServerMessage.KEY_UL_INITFAIL,filePath );
		// return 1;
		// }
		//
		// //初始化文件失败
		// else if( upsizeResult==2){
		//
		// //修改model值
		// fileModel.setProgress( MyFileModel.STATUS_UPLOAD_ERROR );
		// fileModel.setUPloading(false);
		//
		// LogUtil.w(TAG, "init file fail:Init_file_ERROR");
		// sendEvent( "Initial file fail:INIT_FILE_ERROR" );
		// sendResultBroadcast(ServerMessage.KEY_UL_INITFAIL,fileName );
		// return 2;
		// }
		//
		// //3 表示服务器返回文件大小信息有问题
		// else if( upsizeResult==3){
		//
		// //修改model值
		// fileModel.setProgress( MyFileModel.STATUS_UPLOAD_ERROR );
		// fileModel.setUPloading(false);
		//
		// LogUtil.w(TAG, "init file fail:SIZE_ERROR");
		// sendEvent( "Initial file fail:SIZE_ERROR");
		// sendResultBroadcast(ServerMessage.KEY_UL_INITFAIL,filePath);
		// return 3;
		// }
		//
		// // 4 表示获取大小成功 ,准备上传文件
		// else if( upsizeResult ==4){
		//
		// //页面事件
		// LogUtil.i(TAG, "init file successful");
		// sendEvent( "Initial file success,uploading...");
		// sendResultBroadcast( ServerMessage.KEY_UL_INITSUCCESS );
		//
		// //启动读取进度的线程
		// isUploading = true;
		// progressReader = new Thread( new ThreadProgress( fileModel ) );
		// progressReader.start();
		//
		//
		// /* upldfile(无flag) phoneupldfile(有flag)
		// 函数功能: 发送上传文件命令，
		// 入参: 总句柄
		// 返回值: 1 成功，0失败
		// */
		// int uploadResult=0;
		// if(this.operateType==ServerOperateType.uploadMoniFile){
		// uploadResult = phoneupldfile(handle);
		// }else{
		// uploadResult = upldfile(handle);
		// }
		// isUploading = false;
		//
		// //等待读进度线程停止
		// for( int i=0;i<10 && progressReader!=null ;i++){
		// sleepForSecond(1);
		// }
		//
		// LogUtil.w(TAG,"--->uploadResult:"+ uploadResult );
		// return uploadResult;
		// }
		//
		// return 0;
		// }

		/**
		 * [发送Walktour自定义事件]<BR>
		 * 
		 * @param eventList
		 */
		private void sendWalktourEvents(ArrayList<WalktourEvent> eventList) {
			while (eventList.size() > 0) {
				WalktourEvent event = eventList.remove(0);
				Log.i(TAG, "send event:" + event.getName() + "," + UtilsMethod.getSimpleDateFormat0(event.getTime()));
				selfsetmsg(handle, event.getEventBytes());
			}
		}

		private void sleep(int value) {
			try {
				Thread.sleep(value);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// private void sleepForSecond(int value){
		// try {
		// Thread.sleep(value *1000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }

		/** 最后释放所有资源 */
		private void free() {
			if (this.handle != 0) {
				sendEvent("Disconnect from server");
				fleetfree(this.handle);
				freehandle(this.handle);
			}
			this.handle = 0;
		}

		public synchronized boolean isTimeOut() {
			if (fleeter == null) {
				return true;
			} else {
				return timeOut;
			}
		}

		public synchronized void setTimeOut(boolean isTimeOut) {
			this.timeOut = isTimeOut;
		}

		/**
		 * 尝试切换成数据业务的APN
		 */
		private boolean setDataNetwork() {
			pppRule.reSetConfigAPN(ConfigAPN.getInstance());
			int result = pppRule.pppDial(false, WalkStruct.TaskType.FleetConnect, WalkCommonPara.TypeProperty_Net);
			return result == PPPRule.PPP_RESULT_SUCCESS || result == PPPRule.pppNullSuccess;
		}

	}// end inner class ThreadFleeter

	/**
	 * 读取进度的线程
	 */
	// private class ThreadProgress implements Runnable{
	// private MyFileModel filePath;
	//
	// public ThreadProgress(MyFileModel filePath){
	// this.filePath = filePath;
	// }
	//
	// @Override
	// public void run(){
	//
	// while( fleeter!=null && isUploading ){
	// try {
	// Thread.sleep( 1*1000 );
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	//
	// filePath.setProgress(uploadingProgress);
	//
	// if( fleeter!=null && !fleeter.isTimeOut() && isUploading ){
	// LogUtil.i(TAG, "fleet Upload
	// progress:"+String.valueOf(uploadingProgress)+"%" );
	// sendProgressBroadcast( filePath.getModelName() , uploadingProgress);
	// }
	// }
	// progressReader = null;
	// }
	// }

	/**
	 * 等待fleeter空闲的线程,当fleeter空闲的时候，启动fleeter线程
	 **/
	private class ThreadWaiter extends Thread {

		private ServerOperateType operateType;

		public ThreadWaiter(ServerOperateType type) {
			this.operateType = type;
			LogUtil.w(TAG, "--->prepare to new ThreadFleeter:" + type.toString());
		}

		@Override
		public void run() {

			LogUtil.w(TAG, "--->fleeter==null?:" + (fleeter == null));

			// 如果是当前正在连续上传GPS，立即停止
			if (fleeter != null) {
				if (this.operateType == fleeter.getOperateType()) {
					// do nothing
					return;
				} else if (operateType == ServerOperateType.sendEvent) {
					fleeter.setTimeOut(true);
				} else if (fleeter.getOperateType() == ServerOperateType.uploadGPSConstantly) {
					fleeter.setTimeOut(true);
				} else if (fleeter.getOperateType() == ServerOperateType.logout) {
					fleeter.setTimeOut(true);
				} else if (this.operateType == ServerOperateType.logout) {
					fleeter.setTimeOut(true);
				}

			}

			// 等待之前的线程销毁
			while (fleeter != null) {
				try {
					Thread.sleep(3 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (fleeter == null) {
				fleeter = new ThreadFleeter(operateType);
				fleeter.start();
			}

		}

	}

	/** 在指定时间后运行线程 */
	private class ThreadTimer extends Thread {
		private Thread task;
		private int time;

		public ThreadTimer(Thread task, int second) {
			this.task = task;
			this.time = second;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(time * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (task != null) {
				task.start();
			}
		}

	}

	/**
	 * fleeter是否运行，自动测试过程中视为正在运行
	 */
	public static boolean isFleeterRunning() {
		if (fleeter != null) {
			return true;
		} else {
			return false;
		}
	}

	public static WalkStruct.ServerOperateType getOperateType() {
		// 为了判断控制 页面的Tip
		if (fleeter != null) {
			return fleeter.operateType;
		} else {
			return null;
		}

	}

	/***
	 * 显示通知
	 * 
	 * @param tickerText
	 *          通知显示的内容
	 * @param strBroadcast
	 *          点通知后要发的广播
	 */
	@SuppressWarnings("deprecation")
	private void showNotification(String tickerText, String strBroadcast) {
		// 生成通知管理器
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// notification //通知图标, 状态栏显示的通知文本提示,通知产生的时间
		Notification.Builder notification = new Notification.Builder(this);
		notification.setTicker(tickerText);
		notification.setSmallIcon(R.mipmap.walktour);
		notification.setWhen(System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getBroadcast(FleetService.this, 0, new Intent(strBroadcast), 0);
		// must set this for content view, or will throw a exception
		// 如果想要更新一个通知，只需要在设置好notification之后，再次调用
		// setLatestEventInfo(),然后重新发送一次通知即可，即再次调用notify()。
		notification.setAutoCancel(true);
		notification.setContentIntent(contentIntent);
		notification.setContentTitle(getString(R.string.sys_alarm));
		notification.setContentText(tickerText);
		mNotificationManager.notify(R.string.service_started, notification.build());
	}

	/** 显示通知并跳到指定的地方 */
	@SuppressWarnings("deprecation")
	protected void showNotification(String tickerText, Class<?> cls) {
		// 生成通知管理器
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// notification //通知图标, 状态栏显示的通知文本提示,通知产生的时间
		Notification.Builder notification = new Notification.Builder(this);
		notification.setTicker(tickerText);
		notification.setSmallIcon(R.mipmap.walktour);
		notification.setWhen(System.currentTimeMillis());
		// Intent 点击该通知后要跳转的Activity
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, (cls != null ? cls : TestService.class)), 0);
		// must set this for content view, or will throw a exception
		// 如果想要更新一个通知，只需要在设置好notification之后，再次调用
		// setLatestEventInfo(),然后重新发送一次通知即可，即再次调用notify()。
		notification.setAutoCancel(true);
		notification.setContentIntent(contentIntent);
		notification.setContentTitle(getString(R.string.sys_alarm));
		notification.setContentText(tickerText);
		mNotificationManager.notify(R.string.service_started, notification.build());
	}

	/**
	 * 发送当前进度的广播
	 **/
	private void sendProgressBroadcast(String filePath, int progress) {
		intentProgress = new Intent();
		String text = getString(R.string.str_uploading) + filePath + "\n" + progress + "%";
		intentProgress.putExtra(ServerMessage.KEY_MSG, text);
		intentProgress.setAction(ServerMessage.ACTION_FLEET_PROGRESS);
		sendBroadcast(intentProgress);
	}

	/**
	 * 发送显示字符到事件页面
	 */
	private void sendEvent(String event) {
		EventManager.getInstance().addEvent(mContext, event);
	}

	// JNI调用登录Fleet服务器的库
	static {
		System.loadLibrary("decoder");
	}

	/*********
	 * 以下函数按此顺序执行 一.fleet4或者fleetcloud
	 * 同步时间：inithandle->fleetinit->initsocket->connect->fleetlogin->sync->
	 * fleetfree->freehandle
	 * 下载计划：inithandle->fleetinit->initsocket->connect->fleetlogin->sync->
	 * GetConfig->fleetfree->freehandle
	 * 
	 * 二.fleet4上传walktour文件
	 * inithandle->fleetinit->initsocket->connect->fleetlogin->upldsize->upldfile-
	 * >fleetfree->freehandle
	 * 
	 * 三.fleetcloud上传监控文件（有flag）
	 * inithandle->fleetinit->initsocket->phoneconnect->phoneregester->upldsize->
	 * phoneupldfile->fleetfree->freehandle
	 */
	public native int inithandle();// 初始化库句柄，返回下面几个函数所需的handle

	public native int fleetinit(int handle, String serverIp, int port);// 初始化Fleet

	public native int initsocket(int handle);// 初始化Socket

	public native int connect(int handle, int connTime);// 连接服务器,connTime<15 ||
																											// connTime>75时为75

	public native int fleetlogin(int handle, String rcuId);// Fleet4下载上传、fleet的登录方法

	public native long sync(int handle);// 同步时间

	public native int gpsmsg(int handle, float alt, float lat);// 实时上传GPS信息到fleet4

	/**
	 * fleetcloud和Fleet4下载测试计划一样 fleetcloud上传文件要单独处理，
	 */
	public native int phoneconnect(int handle, int connTime);// FleetCloud上传时连接服务器

	public native int phoneregester(int handle, String IMSI);// FleetCloud的登录方法

	/*
	 * 获取测试计划 入参: 总句柄 返回值: 1 成功，0失败
	 */
	public native int GetConfig(int handle, String filePath);

	/**
	 * 发送上传文件命令，获取到fleet服务器上文件大小信息 入参: 总句柄 返回值: 1表示文件没有上传过，需要上传 4
	 * 表示文件上传了一部分，需要接着上传 3 表示服务器返回文件大小信息有问题 5 表示发送命令到fleet服务器失败
	 */
	public native int upldsize(int handle, String fileFullPath);// 确定上传文件大小

	/**
	 * 函数功能: 发送上传文件命令， 入参: 总句柄 返回值: #define FLEET_UPLOAD_NORMAL_ERROR 0 //一般错误
	 * 表示调用出错 回调函数错误等 内部问题 #define FLEET_UPLOAD_FILE_SUCCESS 1 //成功 #define
	 * FLEET_UPLOAD_FILE_ERROR 2 //平台对文件检测后 出现的文件错误 #define FLEET_UPLOAD_STOP 3
	 * //停止上传 #define FLEET_UPLOAD_NET_ERROR 4 //网络上传错误 比如链接问题 等
	 */
	public native int upldfile(int handle);// Fleet4的上传文件方法

	public native int phoneupldfile(int handle);// FleetCloud的上传文件方法

	/* 上传RCU文件时的进度回调函数,此函数由库decoder调用 */
	// public void fleetcallback(int size,int sent){
	// //int 类型在214M以上文件*100会越界，得用long类型
	// long sentSize = sent;
	// long fileSize = size;
	// uploadingProgress = (int)( sentSize*100 / fileSize) ;
	// //是否100%进度由平台返回的校验成功确定
	// if( uploadingProgress>=100 ){
	// uploadingProgress = MyFileModel.STATUS_UPLOAD_VERIFY;
	// }
	// LogUtil.w(TAG, "---callback progress:"+uploadingProgress+"%");
	// }

	/*
	 * 函数功能:中断上传文件命令， 入参: 总句柄 返回值: 1 成功，0失败
	 */
	public native int setstopupld(int handle);

	/* 释放资源 */
	public native int fleetfree(int handle);// 释放fleet

	public native int freehandle(int handle);// 释放库句柄

	/**
	 * fleetlogout
	 */
	public native int fleetlogout(int handle);// 退出软件

	/**
	 * 发送自定义的事件
	 * 
	 * @param handle
	 * @param msgBytes
	 *          事件的RCU结构字节
	 * @return
	 */
	public native int selfsetmsg(int handle, byte[] msgBytes);
}
