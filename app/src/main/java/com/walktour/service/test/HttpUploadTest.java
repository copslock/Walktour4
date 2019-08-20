package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.dingli.service.test.ipc2jni;
import com.dingli.service.test.ipc2msg;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.RcuEventCommand.DropReason;
import com.walktour.Utils.RcuEventCommand.FailReason;
import com.walktour.Utils.TotalStruct.TotalAppreciation;
import com.walktour.Utils.TotalStruct.TotalHttpType;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct.DataTaskEvent;
import com.walktour.Utils.WalkStruct.DataTaskValue;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.gui.task.parsedata.model.task.http.upload.TaskHttpUploadModel;
import com.walktour.model.TotalSpecialModel;
import com.walktour.service.ICallback;
import com.walktour.service.IService;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Jihong Xie HttpUpload Service
 */
// public class HttpUploadTest extends AbstractTestTaskService {
@SuppressLint({ "HandlerLeak", "SdCardPath" })
public class HttpUploadTest extends TestTaskService {
	private final String TAG = "HttpUploadTest";

	private final int TIMER_CHANG = 55; // 时间刷新计时器

	// 网络硬盘上传业务ID
	private final int HTTP_UP_TEST = 79;

	/* 对外发出事件 */
	/**
	 * 初始化完毕 事件参数: result(int) 0:init failed 1:init success
	 */
	private final int HTTP_UP_INITED = 1; // 初始化完毕

	// 登录
	/** 登录开始 */
	private final int HTTP_UP_LOGIN_START 		= 2; // 登录开始
	/** 登录成功 */
	private final int HTTP_UP_LOGIN_SUCCESS 		= 3; // 登录成功
	//事件参数：http_up_dnsresolve_start
//	private final int HTTP_UP_DNSRESOLVE_START	= 20;  //开始解析
	//事件参数：http_up_dnsresolve_info
//	private final int HTTP_UP_DNSRESOLVE_SUCCESS 	= 21;  //解析成功
	//事件参数：http_up_dnsfailed_info
//	private final int HTTP_UP_DNSRESOLVE_FAILED	= 22;  //解析失败
	/** 登录失败 */
	private final int HTTP_UP_LOGIN_FAILED = 4; // 登录失败

	// 事件参数：size(int),文件大小
	/** 开始上传 */
	private final int HTTP_UP_START = 5; // 开始上传
	/** 第1个数据包已上传 */
	private final int HTTP_UP_FIRSTDATA = 6; // 第1个数据包已上传

	/**
	 * 事件参数:http_up_qos结构体 品质数据输出
	 */
	private final int HTTP_UP_QOS_ARRIVED = 7; // 品质数据输出(定期)

	private final int HTTP_UP_FAILED = 10; // 业务失败(未收到第一个数据)

	// 事件参数:http_up_drop_info结构体
	/** 业务中断 */
	private final int HTTP_UP_DROP = 13; // 业务中断
	/** 业务完成 */
	private final int HTTP_UP_FINISH = 14; // 业务完成
	/** 业务退出 */
	private final int HTTP_UP_QUIT = 15; // 业务退出

	/* 外部发来事件 */
	// 事件参数:
	/** 开始业务(ID不可变) */
	private final int HTTP_UP_START_TEST = 1001; // 开始业务(ID不可变)
	/** 停止业务(ID不可变) */
	private final int HTTP_UP_STOP_TEST = 1006; // 停止业务(ID不可变)

	private String strQos;
	private ipc2jni aIpc2Jni;
	private boolean isRunning = Boolean.FALSE;
	private boolean hasUpload = Boolean.FALSE;
	private boolean isLogin = Boolean.FALSE;
	private TaskHttpUploadModel taskModel;
	private String filePath;
	//	private String msgStr;
	private boolean isCallbackRegister = false;
	//	private boolean testInterrupt = false;
	private boolean isStop = false;

	@Override
	public void onCreate() {
		super.onCreate();
		aIpc2Jni = new ipc2jni(mEventHandler);
	}

	// 测试模型
	private ApplicationModel appModel = ApplicationModel.getInstance();

	// 远程回调
	private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();

	// 远程回调方法绑定
	private IService.Stub mBind = new IService.Stub() {
		@Override
		public void unregisterCallback(ICallback cb) throws RemoteException {
			LogUtil.i(TAG, "unregisterCallback");
			if (cb != null) {
				mCallbacks.unregister(cb);
			}
		}

		@Override
		public void stopTask(boolean isTestInterrupt, int dropReason)
				throws RemoteException {
			long time = System.currentTimeMillis() * 1000;
//			testInterrupt = isTestInterrupt;
			appModel.setCurrentTask(null);
			if (!isStop) {
				isStop = true;
				if (isTestInterrupt
						|| (dropReason != DropReason.NORMAL.getReasonCode())) {
					LogUtil.w(TAG, "user stop current task");
					if (!isLogin) {
						writeRcuEvent(RcuEventCommand.HTTP_UP_FAILURE,time);
						displayEvent(DataTaskEvent.HTTP_UP_LOGIN_FAILED
								.toString());
					} else if (hasUpload) {
						EventBytes
								.Builder(getBaseContext(),
										RcuEventCommand.HTTP_UP_DROP)
								.addInteger(measure_time / 1000)
								.addInt64(up_bytes / 1000L)
								.addInteger(
										isTestInterrupt ? DropReason.USER_STOP
												.getReasonCode() : dropReason)
								.writeToRcu(time);
						displayEvent(String
								.format(DataTaskEvent.HTTP_UP_DROP.toString(),
										(int) (System.currentTimeMillis() - startTime / 1000) ,
										up_speed_avg / (8 * UtilsMethod.kbyteRage),
										up_bytes / UtilsMethod.kbyteRage,
										isTestInterrupt ? DropReason.USER_STOP
												.getResonStr() : DropReason
												.getDropReason(dropReason)
												.getResonStr()));
					} else {
						writeRcuEvent(
								RcuEventCommand.HTTP_UP_FAILURE,time,
								isTestInterrupt ? DropReason.USER_STOP
										.getReasonCode() : DropReason.NORMAL
										.getReasonCode());
						displayEvent(String.format(
								DataTaskEvent.HTTP_UP_FAILED.toString(),
								isTestInterrupt ? FailReason.USER_STOP
										.getResonStr() : DropReason.NORMAL
										.getResonStr()));
					}
				}
				// 手动停止，标记为失败
				sendMsgToPioneer(",bSuccess=0");
			}

			// 如果是创建文件的，则需要把旧文件删除
			if (taskModel.getFileSource() == TaskHttpUploadModel.CREATE_FILE) {
				File data = new File(filePath);
				if (data.exists()) {
					data.delete();
				}
			}
		}

		@Override
		public void registerCallback(ICallback cb) throws RemoteException {
			LogUtil.i(TAG, "registerCallback");
			if (cb != null) {
				mCallbacks.register(cb);
				isCallbackRegister = true;
			}
		}

		/**
		 * 返回是否执行startCommand状态,
		 * 如果改状态需要在业务中出现某种情况时才为真,
		 * 可以在继承的业务中改写该状态*/
		public boolean getRunState(){
			return startCommondRun;
		}
	};

	public IBinder onBind(Intent intent) {
		return mBind;
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int startFlag = super.onStartCommand(intent, flags, startId);

		taskModel = (TaskHttpUploadModel) super.taskModel;

		// 给上传文件赋值，如果是自动创建文件，则创建一个，否则取选择的文件路径
		if (taskModel.getFileSource() == TaskHttpUploadModel.CREATE_FILE) {
			createTestFile();
		} else {
			filePath = taskModel.getFilePath();
		}

		aIpc2Jni.initServer(this.getLibLogPath());
		Thread thread = new Thread(new RunTest());
		thread.start();
		return startFlag;
	}

	/**
	 * 自动创建测试上传文件
	 */
	@SuppressWarnings("resource")
	private void createTestFile() {
		filePath = getFilesDir().getAbsolutePath() + "/HttpUp_Test_"
				+ new Random().nextInt(100) + ".3gp";
		MappedByteBuffer mbb = null;
		FileChannel fc = null;
		int length =(int) UtilsMethod.kbyteRage * taskModel.getFileSize();
		try {
			fc = new RandomAccessFile(filePath, "rw").getChannel();
			mbb = fc.map(FileChannel.MapMode.READ_WRITE, 0, length);
			fc.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fc != null)
					fc.close();
				if (mbb != null)
					mbb.clear();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 开启一个新线程启动任务
	 *
	 * @author Jihong Xie
	 *
	 */
	class RunTest implements Runnable {
		@Override
		public void run() {
			LogUtil.i(TAG, "wait for stard test");
			try {
				while (!isCallbackRegister) {
					LogUtil.i(TAG, "test is started");
					Thread.sleep(1000);
				}
				startTest();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/** mHandler: 调用回调函数 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			resultCallBack(msg);
		}

		// call back
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private void resultCallBack(Message msg) {
			int N = mCallbacks.beginBroadcast();
			try {
				for (int i = 0; i < N; i++) {
					switch (msg.what) {
						case EVENT_CHANGE:
							mCallbacks.getBroadcastItem(i).OnEventChange(
									repeatTimes + "-" + msg.obj.toString());
							break;
						case CHART_CHANGE:
							mCallbacks.getBroadcastItem(i).onChartDataChanged(
									(Map) msg.obj);
							break;
						case DATA_CHANGE:
							Map tempMap = (Map) msg.obj;
							tempMap.put(TaskTestObject.stopResultName,
									taskModel.getTaskName());
							mCallbacks.getBroadcastItem(i).OnDataChanged(tempMap);
							break;
						case TEST_STOP:
							LogUtil.w(TAG, "--TEST_STOP--");
							if (taskModel != null) {
								Map<String, String> resultMap = TaskTestObject
										.getStopResultMap(taskModel);
								resultMap.put(TaskTestObject.stopResultState,
										(String) msg.obj);
								mCallbacks.getBroadcastItem(i).onCallTestStop(
										resultMap);
							} else {
								HttpUploadTest.this.onDestroy();
							}
							break;
						case TIMER_CHANG:
							timerChange();
							break;
					}
				}
			} catch (RemoteException e) {
				LogUtil.w(TAG, "---", e);
			}
			mCallbacks.finishBroadcast();
		}
	};

	protected void startTest() {
		LogUtil.w(TAG, "start to prep args");
		isRunning = true;
		String args = "-m http_up -z "+ AppFilePathUtil.getInstance().getAppConfigDirectory();
		String client_path = AppFilePathUtil.getInstance().getAppLibFile(useRoot ? "datatests_android"	: "libdatatests_so.so").getAbsolutePath();
		if (useRoot) {
			aIpc2Jni.set_su_file_path("su -c", "export LD_LIBRARY_PATH="
					+ AppFilePathUtil.getInstance().getAppLibDirectory());
		}
		aIpc2Jni.run_client(client_path, args);
	}

	/**
	 * 测试开始时钟消息，每秒钟刷新一次
	 */
	private void timerChange() {
		mHandler.sendEmptyMessageDelayed(TIMER_CHANG, 1000);
		// 如果当前参数传递正确进入计算环节，否则退出消息重发机制
		if (firstDataTime != 0 && !isLastData) {
		}
	}

	private long startTime = 0;
	long fileSize = 0;
	int reason = -1;
	long firstDataTime = 0;
	boolean isLastData = Boolean.FALSE;
	private HashMap<String, TotalSpecialModel> totalMap;// 用于业务统计存存结构
	private Handler mEventHandler = new Handler() {
		long daley = 0;

		public void handleMessage(android.os.Message msg) {
			ipc2msg aMsg = (ipc2msg) msg.obj;
			if (aMsg.test_item != HTTP_UP_TEST & !isStop) {
				return;
			}

			switch (aMsg.event_id) {
				case HTTP_UP_QOS_ARRIVED:
					LogUtil.w(TAG, "recv HTTP_UP_QOS_ARRIVED");
					LogUtil.w(TAG, aMsg.data);
					strQos = aMsg.data;
					analyseQos(strQos);

					if (up_speed_avg > 0) {
						totalMap = new HashMap<String, TotalSpecialModel>();

						totalHttpResult(totalMap, TotalHttpType.HTTPUpload,
								taskModel.getUrl(),
								TotalAppreciation._HttpUploadMeanRate,
								(int) up_speed_avg);
						totalHttpResult(totalMap, TotalHttpType.HTTPUpload,
								taskModel.getUrl(),
								TotalAppreciation._HttpUploadMeanRateTimes, 1);
						mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE,
								totalMap));
						turnTransfersRate();// 发送数据到仪表盘
						sendToPionner();// 发送数据到Pioneer
					}
					break;
				case HTTP_UP_INITED:
					setMainBussinessDirectType(1);
					LogUtil.w(TAG, "recv HTTP_UP_INITED");
					LogUtil.w(TAG, aMsg.data);
					String event_data = "local_if::" +""						+ "\n"
							+ "play_time_ms::" 	+ (taskModel.getTimeout() * 1000) 			+ "\n"
							+ "nodata_timeout_ms::" + (taskModel.getNoDataTimeout() * 1000)+ "\n"
							+ "qos_inv_ms::1000"											+ "\n"
							+ "work_type::" 	+ (taskModel.getServerType()+1) 	+ "\n"	//基础业务库服务器类型1是https,2是youtube,3是baiduyun
							+ "upload_url::" 	+ taskModel.getUrl() 			+ "\n"
							+ "user_name::" 	+ taskModel.getUsername() 		+ "\n"
							+ "password::" 		+ taskModel.getPassword() 		+ "\n"
							+ "uploadfile::" 	+ filePath 						+ "\n"
							+ "up_filesize::"									+ "\n"
							+ "ps_call::" 		+ taskModel.getTestMode() 		+ "\n"
							+ "use_in_account::"+ taskModel.getAccountType()	+ "\n" 	//百度云使用内置的开发者用户帐号（1:使用，0:不使用）
							+ "api_key::" 		+ taskModel.getAccountKey()		+ "\n"	//百度云应用API Key（use_in_account=0时使用）
							+ "secret_key::"	+ taskModel.getSecretKey()		+ "\n"	//百度云应用Secret Key（use_in_account=0时使用）
							+ "server_path::"	+ taskModel.getServerPath();
					LogUtil.i(TAG, event_data);

					aIpc2Jni.send_command(HTTP_UP_TEST, HTTP_UP_START_TEST,event_data, event_data.length());
					startTime = aMsg.getRealTime();

					displayEvent(DataTaskEvent.HTTP_UP_START.toString());
					// RCU事件存储结构文档
					// 1: PrivateServer
					// 2: Youtube
					// 3: Baidu Cloud
					EventBytes.Builder(getBaseContext(), RcuEventCommand.HTTP_UP_START).addInteger(taskModel.getServerType()+1)
							.addStringBuffer(taskModel.getUrl()).writeToRcu(aMsg.getRealTime());
					break;
				case HTTP_UP_LOGIN_START:
					LogUtil.w(TAG, "recv HTTP_UP_LOGIN_START\r\n");
					displayEvent(DataTaskEvent.HTTP_UP_LOGIN_START.toString());
					writeRcuEvent(RcuEventCommand.HTTP_UP_CONNECT_START,aMsg.getRealTime());
					totalMap = new HashMap<String, TotalSpecialModel>();
					totalHttpResult(totalMap, TotalHttpType.HTTPUpload,
							taskModel.getUrl(), TotalAppreciation._HttpUploadTry, 1);
					totalHttpResult(totalMap, TotalHttpType.HTTPUpload,
							taskModel.getUrl(),
							TotalAppreciation._HttpUploadSuccess, 0);
					mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE,
							totalMap));
					break;
				case HTTP_UP_LOGIN_SUCCESS:
					LogUtil.w(TAG, "recv HTTP_UP_LOGIN_SUCCESS\r\n");
					isLogin = Boolean.TRUE;
					daley = (int)(aMsg.getRealTime() - startTime) / 1000;
					writeRcuEvent(RcuEventCommand.HTTP_UP_CONNECT_SUCCESS,aMsg.getRealTime(),
							(int) daley);
					displayEvent(String.format(
							DataTaskEvent.HTTP_UP_LOGIN_SUCCESS.toString(),
							(int) daley));
					totalMap = new HashMap<String, TotalSpecialModel>();
					totalHttpResult(totalMap, TotalHttpType.HTTPUpload,
							taskModel.getUrl(), TotalAppreciation._HttpUploadDelay,
							(int) daley);
					mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE,
							totalMap));
					break;
				case HTTP_UP_LOGIN_FAILED:
					LogUtil.w(TAG, "recv HTTP_UP_LOGIN_FAILED\r\n");
					writeRcuEvent(RcuEventCommand.HTTP_UP_FAILURE,aMsg.getRealTime());
					displayEvent(DataTaskEvent.HTTP_UP_LOGIN_FAILED.toString());
					break;
				case HTTP_UP_START:

					LogUtil.w(TAG, "recv HTTP_UP_START\r\n");
					LogUtil.w(TAG, "data=" + aMsg.data);
					hasUpload = true;
					strQos = aMsg.data;
					analyseQos(strQos);
					EventBytes
							.Builder(getBaseContext(),
									RcuEventCommand.HTTP_UP_SEND_POST_COMMAND)
							.addInt64(fileSize).writeToRcu(aMsg.getRealTime());
					displayEvent(DataTaskEvent.HTTP_UP_SEND_START.toString());
					break;
				case HTTP_UP_FIRSTDATA:
					LogUtil.w(TAG, "recv HTTP_UP_FIRSTDATA\r\n");
					firstDataTime = System.currentTimeMillis();
					startTime = aMsg.getRealTime();
					writeRcuEvent(RcuEventCommand.HTTP_UP_FIRST_DATA,aMsg.getRealTime());
					displayEvent(DataTaskEvent.HTTP_UP_FIRST_DATA.toString());
					break;
				case HTTP_UP_FAILED:
					LogUtil.w(TAG, "recv HTTP_UP_FAILED");
					strQos = aMsg.data;
					LogUtil.w(TAG, strQos);
					analyseQos(strQos);
					writeRcuEvent(RcuEventCommand.HTTP_UP_FAILURE,aMsg.getRealTime(), reason);
					displayEvent(String.format(DataTaskEvent.HTTP_UP_FAILED
							.toString(), FailReason.getFailReason(reason)
							.getResonStr()));
					sendMsgToPioneer(",bSuccess=0");
					break;
				case HTTP_UP_DROP:
					LogUtil.w(TAG, "recv HTTP_UP_DROP\r\n");
					strQos = aMsg.data;
					LogUtil.w(TAG, strQos);
					analyseQos(strQos);
					EventBytes
							.Builder(getBaseContext(), RcuEventCommand.HTTP_UP_DROP)
							.addInteger(measure_time / 1000)
							.addInt64((int)(up_bytes)).addInteger(reason)
							.writeToRcu(aMsg.getRealTime());
					displayEvent(String.format(DataTaskEvent.HTTP_UP_DROP
									.toString(),
							measure_time / 1000,// (int)(System.currentTimeMillis()-startTime)/1000,
							up_speed_avg / (8 * UtilsMethod.kbyteRage), up_bytes / UtilsMethod.kbyteRage,
							RcuEventCommand.DropReason.getDropReason(reason)
									.getResonStr()));

					if( (reason == RcuEventCommand.DROP_USERSTOP || reason == RcuEventCommand.DROP_TIMEOUT)
							&& firstDataTime !=0){
						totalHttpResult(totalMap, TotalHttpType.HTTPUpload,taskModel.getUrl(),
								TotalAppreciation._HttpUploadSuccess, 1);
						totalHttpResult(totalMap, TotalHttpType.HTTPUpload,taskModel.getUrl(),
								TotalAppreciation._HttpUploadTotalBytes, up_bytes);
						totalHttpResult(totalMap, TotalHttpType.HTTPUpload,taskModel.getUrl(),
								TotalAppreciation._HttpUploadTotalTime, measure_time);
					}
					sendMsgToPioneer(",bSuccess=0");
					break;
				case HTTP_UP_FINISH:
					LogUtil.w(TAG, "recv HTTP_UP_FINISH\r\n");
//					EventBytes
//							.Builder(getBaseContext(),
//									RcuEventCommand.HTTP_UP_LAST_DATA)
//							.addInt64(fileSize).addInteger(measure_time)
//							.writeToRcu(aMsg.getRealTime());

					EventBytes
							.Builder(getBaseContext(),
									RcuEventCommand.HTTP_UP_LAST_DATA)
							.addInt64(up_bytes).addInteger(measure_time)
							.writeToRcu(aMsg.getRealTime());

					displayEvent(String.format(
							DataTaskEvent.HTTP_UP_LAST_DATA.toString(),
							measure_time / 1000,// (System.currentTimeMillis()-startTime)/1000,
							up_speed_avg / (8 * UtilsMethod.kbyteRage), up_bytes / UtilsMethod.kbyteRage));
					totalMap = new HashMap<String, TotalSpecialModel>();
					totalHttpResult(totalMap, TotalHttpType.HTTPUpload,taskModel.getUrl(),
							TotalAppreciation._HttpUploadSuccess, 1);
					totalHttpResult(totalMap, TotalHttpType.HTTPUpload,taskModel.getUrl(),
							TotalAppreciation._HttpUploadTotalBytes, up_bytes);
					totalHttpResult(totalMap, TotalHttpType.HTTPUpload,taskModel.getUrl(),
							TotalAppreciation._HttpUploadTotalTime, measure_time);

					mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE,
							totalMap));
					sendMsgToPioneer(",bSuccess=1");
					isLastData = Boolean.TRUE;
					break;
				case HTTP_UP_QUIT:
					LogUtil.w(TAG, "recv HTTP_UP_QUIT\r\n");
					isStop = true;
					stopTest(false);
					break;
			}
		}
	};

	private float kByte = UtilsMethod.kbyteRage;
	//	private long totalTransSize = 0;
	private float meanRate = 0f; // 速率
	private float averageRage = 0f; // 平均速度
	Message msg;

	/** 统计当前传输大小，时长，瞬时速率，平均速率，最大速率等相关信息 */
	private void turnTransfersRate() {
		if (firstDataTime != 0) {
			if (measure_time != 0) {
				// 计算当前每秒传输速度
				// 计算前将当前获得的总大小赋给临时总大小变量，以防计算过程中当前次总大小发生变化
				// int interBytes = (int)(up_bytes - totalTransSize);
				// //间隔获得数据包大小
				// totalTransSize = up_bytes;
				Map<String, Object> dataMap = new HashMap<String, Object>();

				// 当前平均值
				averageRage = (float) (up_speed_avg / kByte);// (totalTransSize
				// * 8 /
				// (measure_time
				// / 1000f))
				// /kByte ;
				dataMap.put(DataTaskValue.BordLeftTitle.name(),
						getString(R.string.info_data_averagerate) + "@"
								+ UtilsMethod.decFormat.format(averageRage)
								+ getString(R.string.info_rate_kbps));
				meanRate = (float) (up_speed_cur / kByte);// (interBytes * 8 /
				// ((streamingItem.qos.MeasureTime
				// -
				// streamingItem.qos.prevMeasureTime)
				// / 1000f))/kByte;
				dataMap.put(DataTaskValue.BordCurrentSpeed.name(),
						UtilsMethod.decFormat.format(meanRate));
				dataMap.put(DataTaskValue.BordProgress.name(), up_progress);
				msg = mHandler.obtainMessage(DATA_CHANGE, dataMap);
				mHandler.sendMessage(msg);

				LogUtil.w(TAG, "--measure_time:" + measure_time + "--up_bytes:" + up_bytes + "--up_bytes_cur:" + up_bytes_cur);
				UtilsMethod.sendWriteRcuFtpData(getApplicationContext(), WalkCommonPara.MsgDataFlag_H, 0x01, measure_time,
						up_bytes, 1000, up_bytes_cur);
			}
		}
	}

	/**
	 * 住Pionner发送数据
	 */
	private void sendToPionner() {
		// 如果已经收到FirstDataArrive
		if (measure_time != 0 && firstDataTime != 0
				&& (System.currentTimeMillis() - firstDataTime) > 1000) {
			firstDataTime = System.currentTimeMillis();
			sendMsgToPioneer(String.format(tmpPionner, measure_time, up_bytes,
					(int) (up_speed_avg / kByte)));
		}
	}

	private String tmpPionner = ",DurationTime=%d,TransmitSize=%d,InstRate=%d";

	private void analyseQos(String qos) {
		if (qos != null) {
			String[] arr_info = qos.split("\n");
			try {
				for (String str : arr_info) {
					String[] key_value = str.split("::");
					if (key_value.length < 2)
						continue;
					if (key_value[0].equals("measure_time")) {
						measure_time = Integer.parseInt(key_value[1]);
					} else if (key_value[0].equals("up_bytes")) {
						up_bytes = Integer.parseInt(key_value[1]);
					} else if (key_value[0].equals("up_progress")) {
						up_progress = Integer.parseInt(key_value[1]);
					} else if (key_value[0].equals("up_speed_max")) {
//						up_speed_max = Double.parseDouble(key_value[1]);
					} else if (key_value[0].equals("up_speed_min")) {
//						up_speed_min = Double.parseDouble(key_value[1]);
					} else if (key_value[0].equals("up_speed_avg")) {
						up_speed_avg = Double.parseDouble(key_value[1]);
					} else if (key_value[0].equals("up_bytes_cur")) {
						up_bytes_cur = Integer.parseInt(key_value[1]);
					} else if (key_value[0].equals("up_speed_cur")) {
						up_speed_cur = Double.parseDouble(key_value[1]);
					} else if (key_value[0].equals("reason")) {
						reason = Integer.parseInt(key_value[1]);
					}
					if (key_value[0].equals("size")) {
						fileSize = Long.parseLong(key_value[1]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private int measure_time; // 测量时间，单位ms。从开始下载到现在经过时间。
	private int up_bytes; // 已上传字节数
	private int up_progress; // 上传进度(已经*100), 单位百分比
	//	private double up_speed_max; // 上传最大速率，单位bps
//	private double up_speed_min; // 上传最小速率，单位bps
	private double up_speed_avg; // 上传平均速率，单位bps
	private int up_bytes_cur; // 当前上传字节数(测量区间)
	private double up_speed_cur; // 当前上传速率，单位bps(测量区间)

	/**
	 * 停止测试
	 *
	 * @param isTestInterrupt
	 *            是否是人工停止
	 */
	private void stopTest(boolean isTestInterrupt) {
		if (isRunning) {
			if (isTestInterrupt) {
				aIpc2Jni.send_command(HTTP_UP_TEST, HTTP_UP_STOP_TEST, "", 0);
			} else {
				sendCallBackStop(hasUpload ? "1" : "TEST STOP");
			}
		}

		aIpc2Jni.uninit_server();
		isRunning = false;
	}

	/**
	 * 显示事件
	 * */
	private void displayEvent(String event) {
		Message msg = mHandler.obtainMessage(EVENT_CHANGE, event);
		msg.sendToTarget();
	}

	/**
	 * [调用停止当前业务接口]<BR>
	 * [如果当前为手工停止状态不能调用当前停止接口]
	 *
	 * @param msg
	 */
	private void sendCallBackStop(String msg) {
		if (!isInterrupted) {
			Message StopMsg = mHandler.obtainMessage(TEST_STOP, msg);
			StopMsg.sendToTarget();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(0);
		mCallbacks.kill();
		// 如果是创建文件的，则需要把旧文件删除
		if (taskModel.getFileSource() == TaskHttpUploadModel.CREATE_FILE) {
			File data = new File(filePath);
			if (data.exists()) {
				data.delete();
			}
		}

		//因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
		UtilsMethod.killProcessByPname( "com.walktour.service.test.HttpUploadTest", false );
		UtilsMethod.killProcessByPname( AppFilePathUtil.getInstance().getAppLibFile("datatests_android").getAbsolutePath(), false );
	}

	/*
	 * private PhoneStateListener phStateListener = new PhoneStateListener(){
	 * 
	 * @Override public void onDataConnectionStateChanged(int state) {
	 * super.onDataConnectionStateChanged(state); if(!isStop){ if(state ==
	 * TelephonyManager.DATA_DISCONNECTED) {//网络断开 LogUtil.w(TAG,
	 * "network data sdisconnected"); isStop = true; writeRCU();
	 * sendCallBackStop(isRunning?"1":"TEST STOP"); } } }
	 * 
	 * @Override public void onServiceStateChanged(ServiceState serviceState) {
	 * super.onServiceStateChanged(serviceState); if(!isStop){
	 * if(serviceState.getState() == ServiceState.STATE_OUT_OF_SERVICE){//脱网
	 * LogUtil.w(TAG, "state out of service"); isStop = true; writeRCU();
	 * sendCallBackStop(isRunning?"1":"TEST STOP"); } } }
	 * 
	 * private void writeRCU() { if (hasUpload) { EventBytes
	 * .Builder(getBaseContext(), RcuEventCommand.HTTP_UP_DROP)
	 * .addInteger(measure_time / 1000) .addInt64(up_bytes /
	 * 1000L).addInteger(RcuEventCommand
	 * .DropReason.OutOfService.getReasonCode()) .writeToRcu();
	 * displayEvent(String.format( DataTaskEvent.HTTP_UP_DROP.toString(), (int)
	 * (System.currentTimeMillis() - startTime) / 1000, up_speed_avg / (8 *
	 * 1000f), up_bytes / 1000f,
	 * RcuEventCommand.DropReason.OutOfService.getResonStr())); } else {
	 * writeRcuEvent(RcuEventCommand.HTTP_UP_FAILURE, reason);
	 * displayEvent(String.format( DataTaskEvent.HTTP_UP_FAILED.toString(),
	 * FailReason.getFailReason(reason).getResonStr())); } } };
	 */

	/**
	 * 统计HTTP业务
	 *
	 * @param httpType
	 *            主键1，HTTP登陆或刷新
	 * @param url
	 *            url 主键2,HTTP的URL
	 * @param totalType
	 *            要统计的具体值的KEY,如http刷新时延
	 */
	protected void totalHttpResult(HashMap<String, TotalSpecialModel> map,
								   TotalHttpType httpType, String url, TotalAppreciation totalType,
								   int value) {
		TotalSpecialModel tmpSp = new TotalSpecialModel(httpType.getHttpType(),
				"HttpUpload", totalType.name(), value);
		map.put(tmpSp.getKeyName(), tmpSp);
	}

}
