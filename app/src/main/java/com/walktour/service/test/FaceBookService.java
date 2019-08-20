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
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.RcuEventCommand.DropReason;
import com.walktour.Utils.RcuEventCommand.FailReason;
import com.walktour.Utils.TotalStruct.TotalFaceBook;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct.DataTaskEvent;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.gui.task.parsedata.model.task.facebook.TaskFaceBookModel;
import com.walktour.service.ICallback;
import com.walktour.service.IService;

import java.util.HashMap;
import java.util.Map;

/**
 * FaceBook 测试调用Service
 * 
 * @author XieJihong
 *
 */
@SuppressLint({ "HandlerLeak", "SdCardPath" })
public class FaceBookService extends TestTaskService {
	private static final String TAG = "FaceBookService";

	private final int TIMER_CHANG = 55; // 时间刷新计时器
	private TaskFaceBookModel taskModel;
	private ipc2jni aIpc2Jni;
	private boolean isCallbackRegister = false;
	private boolean loginSucc = false;
	private boolean isFinish = false;

	/**
	 * -----------------------------------回调及业务ID定义-------------------------------
	 * ---
	 */
	private static final int FACEBOOK_TEST = 91;
	private static final int FACEBOOK_INITED = 1;
	private static final int FACEBOOK_TEST_START = 3;
	private static final int FACEBOOK_ACTION_START = 4;
	private static final int FACEBOOK_ACTION_SUCCESS = 5;
	private static final int FACEBOOK_ACTION_FAILURE = 6;
	private static final int FACEBOOK_TEST_SUCCESS = 7;
	private static final int FACEBOOK_TEST_FAILURE = 8;
	private static final int FACEBOOK_QUIT = 10;
	private static final int FACEBOOK_START_TEST = 1001;
	private static final int FACEBOOK_STOP_TEST = 1006;

	/** 业务回调显示信息 */
	private String msgStr = "";

	// 远程回调
	private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();

	// 远程回调方法绑定
	private IService.Stub mBind = new IService.Stub() {
		@Override
		public void stopTask(boolean isTestInterrupt, int dropReason) throws RemoteException {
			isFinish = true;
			if (isTestInterrupt || dropReason != DropReason.NORMAL.getReasonCode()) {// 手动停止或Drop之类事件

				if (isTestInterrupt) {// 手动停止记成功一次
					totalMap = new HashMap<String, Integer>();
					totalMap.put(TotalFaceBook._faceBookSuccesses.name(), 1);
					mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
				}

				msgStr = String.format(DataTaskEvent.FACEBOOK_TEST_FAILURE.toString(),
						isTestInterrupt ? FailReason.USER_STOP.getResonStr() : FailReason.getFailReason(dropReason).getResonStr());

				EventBytes.Builder(getBaseContext(), RcuEventCommand.Facebook_Test_Failure)
						.addInteger(isTestInterrupt ? FailReason.USER_STOP.getReasonCode() : dropReason)
						.addStringBuffer(isTestInterrupt ? FailReason.USER_STOP.getResonStr()
								: FailReason.getFailReason(dropReason).getResonStr())
						.writeToRcu(System.currentTimeMillis() * 1000);
				displayEvent(msgStr);
			}
		}

		@Override
		public void registerCallback(ICallback cb) throws RemoteException {
			if (cb != null) {
				mCallbacks.register(cb);
				isCallbackRegister = true;
			}
		}

		@Override
		public void unregisterCallback(ICallback cb) throws RemoteException {
			if (cb != null) {
				mCallbacks.unregister(cb);
			}
		}

		/**
		 * 返回是否执行startCommand状态, 如果改状态需要在业务中出现某种情况时才为真, 可以在继承的业务中改写该状态
		 */
		public boolean getRunState() {
			return startCommondRun;
		}
	};

	public IBinder onBind(Intent intent) {
		return mBind;
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.d(tag, "---onStart");
		int startFlag = super.onStartCommand(intent, flags, startId);

		taskModel = (TaskFaceBookModel) super.taskModel;
		timerChange();// 每秒进行一次调用启动，用于写入品质参数等
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!isCallbackRegister) {
					LogUtil.i(TAG, "test is started");
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
					}
				}
				startTest();
			}
		}).start();

		return startFlag;
	}

	/**
	 * 测试开始时钟消息，每秒钟刷新一次
	 */
	private void timerChange() {
		mHandler.sendEmptyMessageDelayed(TIMER_CHANG, 1000);
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
						mCallbacks.getBroadcastItem(i).OnEventChange(repeatTimes + "-" + msg.obj.toString());
						break;
					case CHART_CHANGE:
						mCallbacks.getBroadcastItem(i).onChartDataChanged((Map) msg.obj);
						break;
					case DATA_CHANGE:
						Map tempMap = (Map) msg.obj;
						tempMap.put(TaskTestObject.stopResultName, taskModel.getTaskName());
						mCallbacks.getBroadcastItem(i).OnDataChanged(tempMap);
						break;
					case TEST_STOP:
						if (taskModel != null) {
							Map<String, String> resultMap = TaskTestObject.getStopResultMap(taskModel);
							resultMap.put(TaskTestObject.stopResultState, (String) msg.obj);
							mCallbacks.getBroadcastItem(i).onCallTestStop(resultMap);
						} else {
							FaceBookService.this.onDestroy();
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

	StringBuffer messageBF = new StringBuffer();
	long startTime = 0;
	int delay = 0;
	static Map<String, Integer> totalMap;
	private Handler mEventHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ipc2msg aMsg = (ipc2msg) msg.obj;
			if (aMsg.test_item != FACEBOOK_TEST || isFinish) {
				return;
			}
			messageBF.delete(0, messageBF.length());
			totalMap = new HashMap<String, Integer>();
			switch (aMsg.event_id) {
			case FACEBOOK_INITED:
				LogUtil.w(TAG, "recv FACEBOOK_INITED");
				messageBF.append("local_if::").append("").append("\n");
				messageBF.append("parent_processid::").append("0").append("\n");
				messageBF.append("user_name::").append(taskModel.getUser()).append("\n");
				messageBF.append("password::").append(taskModel.getPassword()).append("\n");
				messageBF.append("app_id::").append(taskModel.getAppId()).append("\n");
				messageBF.append("app_secret::").append(taskModel.getAppSecret()).append("\n");
				messageBF.append("send_text::").append(taskModel.getSendContent()).append("\n");
				messageBF.append("picture_quality::").append(taskModel.getSendPicSizeLevel()).append("\n");
				aIpc2Jni.send_command(FACEBOOK_TEST, FACEBOOK_START_TEST, messageBF.toString(), messageBF.length());
				LogUtil.w(TAG, messageBF.toString());

				// 初始化所有数据
				totalMap.put(TotalFaceBook._faceBookAttempts.name(), 0);
				totalMap.put(TotalFaceBook._faceBookGetFriendListAttempts.name(), 0);
				totalMap.put(TotalFaceBook._faceBookGetFriendListDownBytes.name(), 0);
				totalMap.put(TotalFaceBook._faceBookGetFriendListMeanDelay.name(), 0);
				totalMap.put(TotalFaceBook._faceBookGetFriendListSuccesses.name(), 0);
				totalMap.put(TotalFaceBook._faceBookGetWallAttempts.name(), 0);
				totalMap.put(TotalFaceBook._faceBookGetWallDownBytes.name(), 0);
				totalMap.put(TotalFaceBook._faceBookGetWallMeanDelay.name(), 0);
				totalMap.put(TotalFaceBook._faceBookGetWallSuccesses.name(), 0);
				totalMap.put(TotalFaceBook._faceBookLoginAttempts.name(), 0);
				totalMap.put(TotalFaceBook._faceBookLoginMeanDelay.name(), 0);
				totalMap.put(TotalFaceBook._faceBookLoginSuccesses.name(), 0);
				totalMap.put(TotalFaceBook._faceBookLogoutAttempts.name(), 0);
				totalMap.put(TotalFaceBook._faceBookLogoutMeanDelay.name(), 0);
				totalMap.put(TotalFaceBook._faceBookLogoutSuccesses.name(), 0);
				totalMap.put(TotalFaceBook._faceBookPostCommentAttempts.name(), 0);
				totalMap.put(TotalFaceBook._faceBookPostCommentUpBytes.name(), 0);
				totalMap.put(TotalFaceBook._faceBookPostCommentMeanDelay.name(), 0);
				totalMap.put(TotalFaceBook._faceBookPostCommentSuccesses.name(), 0);
				totalMap.put(TotalFaceBook._faceBookPostPhotoAttempts.name(), 0);
				totalMap.put(TotalFaceBook._faceBookPostPhotoUpBytes.name(), 0);
				totalMap.put(TotalFaceBook._faceBookPostPhotoMeanDelay.name(), 0);
				totalMap.put(TotalFaceBook._faceBookPostPhotoSuccesses.name(), 0);
				totalMap.put(TotalFaceBook._faceBookPostStatusAttempts.name(), 0);
				totalMap.put(TotalFaceBook._faceBookPostStatusUpBytes.name(), 0);
				totalMap.put(TotalFaceBook._faceBookPostStatusMeanDelay.name(), 0);
				totalMap.put(TotalFaceBook._faceBookPostStatusSuccesses.name(), 0);
				totalMap.put(TotalFaceBook._faceBookSuccesses.name(), 0);
				mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));

				break;
			case FACEBOOK_TEST_START:
				LogUtil.w(TAG, "recv FACEBOOK_TEST_START");
				msgStr = String.format(DataTaskEvent.FACEBOOK_TEST_START.toString(), taskModel.getUser(),
						taskModel.getPassword(), taskModel.getSendContent(), taskModel.getSendPicSizeLevel());
				displayEvent(msgStr);
				EventBytes.Builder(getBaseContext(), RcuEventCommand.Facebook_Test_Start).addStringBuffer(taskModel.getUser())
						.addStringBuffer(taskModel.getPassword()).addStringBuffer(taskModel.getSendContent())
						.addInteger(taskModel.getSendPicSizeLevel()).writeToRcu(aMsg.getRealTime());
				startTime = aMsg.getRealTime();

				totalMap.put(TotalFaceBook._faceBookAttempts.name(), 1);
				mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
				break;
			case FACEBOOK_ACTION_START:
				LogUtil.w(TAG, "recv FACEBOOK_ACTION_START");
				analyseData(aMsg.data, FACEBOOK_ACTION_START);
				LogUtil.w(TAG, aMsg.data + "");
				EventBytes.Builder(getBaseContext(), RcuEventCommand.Facebook_Action_Start).addInteger(ItemValues.action_type)
						.addStringBuffer(ItemValues.content_text).addInteger(ItemValues.content_size)
						.writeToRcu(aMsg.getRealTime());
				switch (ItemValues.action_type) {
				case 1:
					totalMap.put(TotalFaceBook._faceBookLoginAttempts.name(), 1);
					break;
				case 2:
					totalMap.put(TotalFaceBook._faceBookGetWallAttempts.name(), 1);
					break;
				case 3:
					totalMap.put(TotalFaceBook._faceBookGetFriendListAttempts.name(), 1);
					break;
				case 4:
					totalMap.put(TotalFaceBook._faceBookPostStatusAttempts.name(), 1);
					break;
				case 5:
					totalMap.put(TotalFaceBook._faceBookPostPhotoAttempts.name(), 1);
					break;
				case 6:
					totalMap.put(TotalFaceBook._faceBookPostCommentAttempts.name(), 1);
					break;
				case 7:
					totalMap.put(TotalFaceBook._faceBookLogoutAttempts.name(), 1);
					break;
				}
				mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
				msgStr = String.format(DataTaskEvent.FACEBOOK_ACTION_START.toString(), ItemValues.action_type,
						ItemValues.content_text, ItemValues.content_size);
				displayEvent(msgStr);
				break;
			case FACEBOOK_ACTION_SUCCESS:
				LogUtil.w(TAG, "recv FACEBOOK_ACTION_SUCCESS");
				LogUtil.w(TAG, aMsg.data + "");
				analyseData(aMsg.data, FACEBOOK_ACTION_SUCCESS);
				EventBytes.Builder(getBaseContext(), RcuEventCommand.Facebook_Action_Success).addInteger(ItemValues.action_type)
						.addInteger(ItemValues.delay).addInteger(ItemValues.up_bytes).addInteger(ItemValues.down_bytes)
						.addInteger(ItemValues.click_delay).writeToRcu(aMsg.getRealTime());
				switch (ItemValues.action_type) {
				case 1:
					totalMap.put(TotalFaceBook._faceBookLoginSuccesses.name(), 1);
					totalMap.put(TotalFaceBook._faceBookLoginMeanDelay.name(), ItemValues.delay);
					break;
				case 2:
					totalMap.put(TotalFaceBook._faceBookGetWallSuccesses.name(), 1);
					totalMap.put(TotalFaceBook._faceBookGetWallDownBytes.name(), ItemValues.down_bytes);
					totalMap.put(TotalFaceBook._faceBookGetWallMeanDelay.name(), ItemValues.delay);
					break;
				case 3:
					totalMap.put(TotalFaceBook._faceBookGetFriendListSuccesses.name(), 1);
					totalMap.put(TotalFaceBook._faceBookGetFriendListDownBytes.name(), ItemValues.down_bytes);
					totalMap.put(TotalFaceBook._faceBookGetFriendListMeanDelay.name(), ItemValues.delay);
					break;
				case 4:
					totalMap.put(TotalFaceBook._faceBookPostStatusSuccesses.name(), 1);
					totalMap.put(TotalFaceBook._faceBookPostStatusUpBytes.name(), ItemValues.up_bytes);
					totalMap.put(TotalFaceBook._faceBookPostStatusMeanDelay.name(), ItemValues.delay);
					break;
				case 5:
					totalMap.put(TotalFaceBook._faceBookPostPhotoSuccesses.name(), 1);
					totalMap.put(TotalFaceBook._faceBookPostPhotoUpBytes.name(), ItemValues.up_bytes);
					totalMap.put(TotalFaceBook._faceBookPostPhotoMeanDelay.name(), ItemValues.delay);
					break;
				case 6:
					totalMap.put(TotalFaceBook._faceBookPostCommentSuccesses.name(), 1);
					totalMap.put(TotalFaceBook._faceBookPostCommentUpBytes.name(), ItemValues.up_bytes);
					totalMap.put(TotalFaceBook._faceBookPostCommentMeanDelay.name(), ItemValues.delay);
					break;
				case 7:
					totalMap.put(TotalFaceBook._faceBookLogoutSuccesses.name(), 1);
					totalMap.put(TotalFaceBook._faceBookLogoutMeanDelay.name(), ItemValues.delay);
					break;
				}
				mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
				msgStr = String.format(DataTaskEvent.FACEBOOK_ACTION_SUCCESS.toString(), ItemValues.action_type,
						ItemValues.delay, ItemValues.up_bytes, ItemValues.down_bytes, ItemValues.click_delay);
				displayEvent(msgStr);
				break;
			case FACEBOOK_ACTION_FAILURE:
				LogUtil.w(TAG, "recv FACEBOOK_ACTION_FAILURE");
				LogUtil.w(TAG, aMsg.data + "");
				analyseData(aMsg.data, FACEBOOK_ACTION_FAILURE);
				displayEvent(DataTaskEvent.FACEBOOK_ACTION_FAILURE.toString());
				EventBytes.Builder(getBaseContext(), RcuEventCommand.Facebook_Action_Failure).addInteger(ItemValues.action_type)
						.addInteger(ItemValues.delay).addInteger(ItemValues.code).addStringBuffer(ItemValues.desc)
						.writeToRcu(aMsg.getRealTime());
				startTime = aMsg.getRealTime();
				msgStr = String.format(DataTaskEvent.FACEBOOK_ACTION_FAILURE.toString(), ItemValues.action_type,
						ItemValues.delay, ItemValues.code, ItemValues.desc);
				displayEvent(msgStr);
				break;
			case FACEBOOK_TEST_SUCCESS:
				LogUtil.w(TAG, "recv FACEBOOK_TEST_SUCCESS");
				LogUtil.w(TAG, aMsg.data + "");

				displayEvent(DataTaskEvent.FACEBOOK_TEST_SUCCESS.toString());
				EventBytes.Builder(getBaseContext(), RcuEventCommand.Facebook_Test_Success).writeToRcu(aMsg.getRealTime());

				totalMap.put(TotalFaceBook._faceBookSuccesses.name(), 1);
				mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
				break;
			case FACEBOOK_TEST_FAILURE:
				LogUtil.w(TAG, "recv FACEBOOK_TEST_FAILURE");
				LogUtil.w(TAG, aMsg.data + "");
				analyseData(aMsg.data, FACEBOOK_TEST_FAILURE);
				msgStr = String.format(DataTaskEvent.FACEBOOK_TEST_FAILURE.toString(), ItemValues.code, ItemValues.desc);
				displayEvent(msgStr);
				EventBytes.Builder(getBaseContext(), RcuEventCommand.Facebook_Test_Failure).addInteger(ItemValues.code)
						.addStringBuffer(ItemValues.desc).writeToRcu(aMsg.getRealTime());
				break;
			case FACEBOOK_QUIT:
				LogUtil.w(TAG, "recv FACEBOOK_QUIT");
				finish(aMsg.getRealTime());
				break;
			}
		}

		/**
		 * 结束业务
		 * 
		 * @param time
		 */
		private void finish(long time) {
			if (!isFinish) {
				isFinish = true;
				LogUtil.w(TAG, "start to FACEBOOK_FINISH");
				aIpc2Jni.send_command(FACEBOOK_TEST, FACEBOOK_STOP_TEST, "", 0);
				mHandler.obtainMessage(TEST_STOP, loginSucc ? "1" : "0").sendToTarget();
			}
		}
	};

	/**
	 * 解析业务库返回的节点信息,组织成HashMap<String,String>的格式返回
	 * 
	 * 业务库返回的节点信息格式如下: key1::value1\nkey2::value2\nkey3::value3...
	 * 
	 * @param aData
	 * @return 注意获得解析结果使用后清除
	 */
	private Map<String, String> splitResultByLit(String aData) {
		Map<String, String> keyValues = new HashMap<String, String>();
		String[] keyList = aData.split("\n");
		for (String key : keyList) {
			String[] keyValue = key.split("::");
			if (keyValue.length == 2)
				keyValues.put(keyValue[0], keyValue[1]);
			else
				keyValues.put(keyValue[0], "");
		}
		return keyValues;
	}

	/**
	 * 分析数据
	 * 
	 * @param data
	 *          信息对象
	 * @param step
	 *          步骤
	 */
	protected void analyseData(String data, int step) {
		Map<String, String> dataMap = this.splitResultByLit(data);
		switch (step) {
		case FACEBOOK_ACTION_START:
			ItemValues.action_type = Integer.parseInt(dataMap.get("action_type"));
			ItemValues.content_text = dataMap.get("content_text");
			ItemValues.content_size = Integer.parseInt(dataMap.get("content_size"));
			break;
		case FACEBOOK_ACTION_SUCCESS:
			ItemValues.action_type = Integer.parseInt(dataMap.get("action_type"));
			ItemValues.delay = Integer.parseInt(dataMap.get("delay"));
			ItemValues.up_bytes = Integer.parseInt(dataMap.get("up_bytes"));
			ItemValues.down_bytes = Integer.parseInt(dataMap.get("down_bytes"));
			ItemValues.click_delay = Integer.parseInt(dataMap.get("click_delay"));
			break;
		case FACEBOOK_ACTION_FAILURE:
			ItemValues.action_type = Integer.parseInt(dataMap.get("action_type"));
			ItemValues.delay = Integer.parseInt(dataMap.get("delay"));
			ItemValues.code = Integer.parseInt(dataMap.get("code"));
			ItemValues.desc = dataMap.get("desc");
			break;
		case FACEBOOK_TEST_FAILURE:
			ItemValues.code = Integer.parseInt(dataMap.get("code"));
			ItemValues.desc = dataMap.get("desc");
			break;
		}
	}

	protected static class ItemValues {
		static int action_type;
		static String content_text;
		static int content_size;
		static int delay;
		static int up_bytes;
		static int down_bytes;
		static int click_delay;
		static int code;
		static String desc;
	}

	/**
	 * 显示事件
	 */
	private void displayEvent(String event) {
		Message msg = mHandler.obtainMessage(EVENT_CHANGE, event);
		msg.sendToTarget();
	}

	protected void startTest() {
		LogUtil.w(TAG, "start to run test init ipc2....");
		if (aIpc2Jni == null) {
			aIpc2Jni = new ipc2jni(mEventHandler);
		}

		aIpc2Jni.initServer(this.getLibLogPath());
		String client_path = AppFilePathUtil.getInstance().getAppLibFile(useRoot ? "datatests_android" : "libdatatests_so.so").getAbsolutePath();
		if (useRoot) {
			aIpc2Jni.set_su_file_path("su -c", "export LD_LIBRARY_PATH=" + AppFilePathUtil.getInstance().getAppLibDirectory());
			ipc2jni.runCommand("chmod 777 " + client_path);
		}
		aIpc2Jni.run_client(client_path,
				"-m facebook -z "+AppFilePathUtil.getInstance().getAppConfigDirectory());
		LogUtil.w(TAG, "ipc2 init finish, run client");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(0);
		mCallbacks.kill();
		// 因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
		UtilsMethod.killProcessByPname("com.walktour.service.test.FaceBookService", false);
		UtilsMethod.killProcessByPname(AppFilePathUtil.getInstance().getAppLibFile("datatests_android").getAbsolutePath(), false);
	}

}
