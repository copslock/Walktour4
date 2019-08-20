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
import com.walktour.Utils.ConstItems;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.RcuEventCommand.DropReason;
import com.walktour.Utils.RcuEventCommand.FailReason;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct.DataTaskEvent;
import com.walktour.Utils.WalkStruct.DataTaskValue;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.gui.task.parsedata.model.task.videostreaming.TaskStreamModel;
import com.walktour.model.StreamingItem;
import com.walktour.service.ICallback;
import com.walktour.service.IService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jone 流媒体服务Service
 */
@SuppressLint("SdCardPath")
public class StreamingTest extends TestTaskService {
	private static final String TAG = "Streaming";
	public static final String ACTION = "com.walktour.service.test.streamingtest";
	public static final String IMG_CONTENT = "content";
	public static final String CONTENT_LENGTH = "content_length";
	/** 当前测试模型 KEY */
	public static final String DATA_KEY = "DATA_KEY";
	private final int TIMER_CHANG = 55; // 时间刷新计时器

	private static final int VS_INITED = 1;
	private static final int VS_REQUEST_START = 2;
	private static final int VS_FIRSTDATA_ARRIVED = 3;
	private static final int VS_REQUEST_FAILED = 4;
	private static final int VS_REPRODUCTION_START = 5; //
	private static final int VS_REPRODUCTION_START_FAILED = 6;
	private static final int VS_QOS_ARRIVED = 7;
	private static final int VS_RECV_FINISH = 9;
	private static final int VS_RECV_DROP = 10;
	private static final int VS_REBUFFERING_START = 11;
	private static final int VS_REBUFFERING_END = 12;
	private static final int VS_PLAY_FINISH = 15;
	private static final int VS_QUIT = 16;

	private static final int VS_TEST = 128;
	private static final int VS_START_TEST = 1001;
	private static final int VS_STOP_TEST = 1006;

	private long stream_start_time = System.currentTimeMillis();

	private boolean isCallbackRegister = false;

	private TaskStreamModel taskModel;

	private ipc2jni aIpc2Jni;

	private boolean isFinish = false;

	private static int CURRENT_STEP;

	// 测试模型
	// private ApplicationModel appModel = ApplicationModel.getInstance();

	static {
		LogUtil.v(TAG, "load library...");
		System.loadLibrary("crystax_shared");
		System.loadLibrary("gnustl_shared");
		System.loadLibrary("miniSDL");
		System.loadLibrary("ipc2");
		System.loadLibrary("iconv");
		System.loadLibrary("myiconv");
		System.loadLibrary("ipc2jni");
		System.loadLibrary("mysock");
		System.loadLibrary("crypto");
		System.loadLibrary("ssl");
		System.loadLibrary("mydns");
		System.loadLibrary("curl");
		System.loadLibrary("avutil");
		System.loadLibrary("avcodec");
		System.loadLibrary("swscale");
		System.loadLibrary("jpeg8d");
	}

	// 远程回调
	private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return mBind;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.d(tag, "---onStart");
		int startFlag = super.onStartCommand(intent, flags, startId);

		taskModel = (TaskStreamModel) super.taskModel;
		timerChange();

		Thread thread = new Thread(new RunTest());
		thread.start();
		return startFlag;
	}

	public void onDestroy() {
		LogUtil.v(TAG, "--onDestroy--");
		super.onDestroy();

		mHandler.removeMessages(0);
		mCallbacks.kill();

		// 因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
		UtilsMethod.killProcessByPname("com.walktour.service.test.StreamingTest", false);
		UtilsMethod.killProcessByPname(AppFilePathUtil.getInstance().getAppLibFile("DMPlayerAndroid").getAbsolutePath(), false);
	};

	// 远程回调方法绑定
	private IService.Stub mBind = new IService.Stub() {
		@Override
		public void unregisterCallback(ICallback cb) throws RemoteException {
			if (cb != null) {
				mCallbacks.unregister(cb);
			}
		}

		@Override
		public void stopTask(boolean isTestInterrupt, int dropResion) throws RemoteException {
			if ((isTestInterrupt || (dropResion != DropReason.NORMAL.getReasonCode())) && !isFinish) {
				isFinish = true;
				// LogUtil.d("TTTT", isTestInterrupt + "--->" + dropResion);
				totalMap = new HashMap<String, Integer>();
				if (CURRENT_STEP < VS_FIRSTDATA_ARRIVED) {
					disMsg = String.format(DataTaskEvent.STREAMING_REQUEST_FAILURE.toString(),
							System.currentTimeMillis() - stream_start_time,
							isTestInterrupt ? FailReason.USER_STOP.getResonStr() : FailReason.UNKNOWN.getResonStr());
					displayEvent(disMsg);
					EventBytes.Builder(getBaseContext(), RcuEventCommand.Streaming_Request_Failure)
							.addInteger(isTestInterrupt ? FailReason.USER_STOP.getReasonCode() : FailReason.UNKNOWN.getReasonCode())
							.writeToRcu(System.currentTimeMillis() * 1000);
					// 通知Pioneer
					sendMsgToPioneer(",bSuccess=0");
				} else if (CURRENT_STEP < VS_REPRODUCTION_START) {
					disMsg = String.format(DataTaskEvent.STREAMING_REPRODUCTION_START_FAILURE.toString(),
							System.currentTimeMillis() - stream_start_time,
							isTestInterrupt ? FailReason.USER_STOP.getResonStr() : FailReason.UNKNOWN.getResonStr());
					displayEvent(disMsg);

					writeRcuEvent(RcuEventCommand.Streaming_Reproduction_Start_Failure, System.currentTimeMillis() * 1000,
							isTestInterrupt ? FailReason.USER_STOP.getReasonCode() : FailReason.UNKNOWN.getReasonCode());
					// 开始复制失败次数
					totalMap.put(TotalStruct.TotalVS._vsReproductionFailure.name(), 1);
					mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
					// 通知Pioneer
					sendMsgToPioneer(",bSuccess=0");
				} else {
					disMsg = String.format(DataTaskEvent.STREAMING_DROP.toString(),
							streamingItem.qos.RecvTotalBytes / UtilsMethod.kbyteRage, // 总下载数据量
							streamingItem.qos.ReBufferTimes, // 缓冲次数
							streamingItem.qos.ReBufferTimeMS, // 缓冲时间
							getVmosValue(),
							// streamingItem.qos.DVSNR_VMOS,
							streamingItem.lastData.AV_DeSync_Rate,
							streamingItem.qos.LostVideoPacket / (streamingItem.qos.RecvVideoPacket * 1f),
							streamingItem.qos.AvgVideoPacketGap, streamingItem.qos.AvgVideoPacketJitter, isTestInterrupt
									? DropReason.USER_STOP.getResonStr() : DropReason.getDropReason(dropResion).getResonStr());

					EventBytes.Builder(getBaseContext(), RcuEventCommand.Streaming_Drop)
							.addInteger(streamingItem.qos.RecvTotalBytes).addInteger(streamingItem.qos.ReBufferTimes)
							.addInteger(streamingItem.qos.ReBufferTimeMS).addSingle(getVmosValue())
							.addInteger(streamingItem.lastData.AV_DeSync_Rate).addInteger(streamingItem.qos.RecvVideoPacket)
							.addInteger(streamingItem.qos.RecvAudioPacket).addInteger(streamingItem.qos.LostVideoPacket)
							.addInteger(streamingItem.qos.LostAudioPacket).addInteger(streamingItem.qos.AvgVideoPacketGap)
							.addInteger(streamingItem.qos.AvgAudioPacketGap).addInteger(streamingItem.qos.MaxVideoPacketJitter)
							.addInteger(streamingItem.qos.MaxAudioPacketJitter).addInteger(streamingItem.qos.MinVideoPacketJitter)
							.addInteger(streamingItem.qos.MinAudioPacketJitter).addInteger(streamingItem.qos.AvgVideoPacketJitter)
							.addInteger(streamingItem.qos.AvgAudioPacketJitter)
							.addStringBuffer(streamingItem.lastData.VideoSaveFile).addInteger(isTestInterrupt
									? DropReason.USER_STOP.getReasonCode() : DropReason.getDropReason(dropResion).getReasonCode())
							.writeToRcu(System.currentTimeMillis() * 1000);

					displayEvent(disMsg);

					// 用户停止，播放完成次数+1，否则Drop+1
					if (isTestInterrupt) {
						totalMap.put(TotalStruct.TotalVS._vsPlayEnd.name(), 1);
					} else {
						totalMap.put(TotalStruct.TotalVS._vsDrop.name(), 1);
					}

					// 平均VMOS
					// totalMap.put(TotalStruct.TotalVS._vsAv_VmosTimes.name(), 1);
					// totalMap.put(TotalStruct.TotalVS._vsAv_Vmos.name(),
					// ((int)(getVmosValue()*100)));
					mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
					// 通知Pioneer
					sendMsgToPioneer(",bSuccess=1");
				}
				stopPlay();
			}
		}

		@Override
		public void registerCallback(ICallback cb) throws RemoteException {
			if (cb != null) {
				mCallbacks.register(cb);
				isCallbackRegister = true;
			}
		}

		/**
		 * 返回是否执行startCommand状态, 如果改状态需要在业务中出现某种情况时才为真, 可以在继承的业务中改写该状态
		 */
		public boolean getRunState() {
			return startCommondRun;
		}
	};

	private float getVmosValue() {
		float value = 0;
		for (Integer val : vmos) {
			value += val;
		}

		if (!vmos.isEmpty()) {
			value = value / (vmos.size() * 100);
		}
		return value;
	}

	/**
	 * 测试开始时钟消息，每秒钟刷新一次
	 */
	private void timerChange() {
		mHandler.sendEmptyMessageDelayed(TIMER_CHANG, 1000);
		// 如果当前参数传递正确进入计算环节，否则退出消息重发机制

		turnTransfersRate();
		if (firstArvTime != 0 && !isLastData) {
			// LogUtil.d(TAG, streamingItem.qos.toString());
			EventBytes.Builder(getBaseContext()).addInteger(WalkCommonPara.VideoStream)
					.addInteger(streamingItem.qos.MeasureTime).addInt64(streamingItem.qos.RecvTotalBytes)
					.addInteger(streamingItem.qos.DownloadProgress).addInteger(streamingItem.qos.ReBufferTimes)
					.addInteger(streamingItem.qos.ReBufferTimeMS).addSingle(streamingItem.qos.DVSNR_VMOS)
					.addInteger(streamingItem.qos.AV_DeSync).addInteger(streamingItem.qos.RecvVideoPacket)
					.addInteger(streamingItem.qos.RecvAudioPacket).addInteger(streamingItem.qos.LostVideoPacket)
					.addInteger(streamingItem.qos.LostAudioPacket).addInteger(streamingItem.qos.AvgVideoPacketGap)
					.addInteger(streamingItem.qos.AvgAudioPacketJitter).addInteger(streamingItem.qos.CurVideoPacketJitter)
					.addInteger(streamingItem.qos.CurAudioPacketJitter).addInteger(streamingItem.qos.MaxVideoPacketJitter)
					.addInteger(streamingItem.qos.MaxAudioPacketJitter).addInteger(streamingItem.qos.MinVideoPacketJitter)
					.addInteger(streamingItem.qos.MinAudioPacketJitter).addInteger(streamingItem.qos.AvgVideoPacketJitter)
					.addInteger(streamingItem.qos.AvgAudioPacketJitter).writeToRcu(WalkCommonPara.MsgDataFlag_A);
		}
	}

	/** mHandler: 调用回调函数 */
	@SuppressLint("HandlerLeak")
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
						Map<String, String> resultMap = TaskTestObject.getStopResultMap(taskModel);
						resultMap.put(TaskTestObject.stopResultState, (String) msg.obj);
						mCallbacks.getBroadcastItem(i).onCallTestStop(resultMap);
						break;
					case REAL_PARA:
						mCallbacks.getBroadcastItem(i).onParaChanged(WalkCommonPara.CALL_BACK_VIDEO_PLAY_REAL_PARA, (Map) msg.obj);
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
		LogUtil.w(TAG, "vs starting...");
		if (aIpc2Jni == null) {
			aIpc2Jni = new ipc2jni(mEventHandler);
		}
		aIpc2Jni.initServer(this.getLibLogPath());

		String args = "-m vs -z "+AppFilePathUtil.getInstance().getAppConfigDirectory();
		if (taskModel.getVideoQuality() == 0) {
			args += " -e " + taskModel.getmURL();
		}

		String client_path = AppFilePathUtil.getInstance().getAppLibFile(useRoot ? "DMPlayerAndroid" : "libdmplayer_so.so").getAbsolutePath();
		if (useRoot) {
			String get_root = "chmod 777 " + client_path;
			ipc2jni.runCommand(get_root);
			aIpc2Jni.set_su_file_path("su -c", "export LD_LIBRARY_PATH=" + AppFilePathUtil.getInstance().getAppLibDirectory());
		}

		aIpc2Jni.run_client(client_path, args);
	}

	/**
	 * 停止测试
	 */
	private void stopTest(boolean isTestInterrupt) {
		LogUtil.v(TAG, "--stopTest--" + isTestInterrupt);
		// 程序完成或出错停止
		Message msg = mHandler.obtainMessage(TEST_STOP, "1");
		msg.sendToTarget();

		aIpc2Jni.send_command(VS_TEST, VS_STOP_TEST, "", "".length());
		aIpc2Jni.uninit_server();
	}

	class RunTest implements Runnable {
		@Override
		public void run() {
			try {
				while (!isCallbackRegister) {
					Thread.sleep(100);
				}
			} catch (Exception e) {
			}
			startTest();
		}
	}

	/**
	 * 显示事件
	 */
	private void displayEvent(String event) {
		Message msg = mHandler.obtainMessage(EVENT_CHANGE, event);
		msg.sendToTarget();
	}

	private StringBuffer args = new StringBuffer();
	private String disMsg;
	private int reason = 0;
	private int websiteType;
	private String url = "";
	private int delay = 0;
	ArrayList<Integer> vmos = new ArrayList<Integer>();
	boolean isLastData = Boolean.FALSE;
	private long firstArvTime = 0;// 第一个包到达时间
	private StreamingItem streamingItem = StreamingItem.getInstance();
	private Map<String, Integer> totalMap;// = new HashMap<String,
																				// Integer>();//用于业务统计存存结构
	@SuppressLint("HandlerLeak")
	private Handler mEventHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ipc2msg aMsg = (ipc2msg) msg.obj;
			if (aMsg.test_item != VS_TEST || isFinish) {
				return;
			}

			CURRENT_STEP = aMsg.event_id;
			totalMap = new HashMap<String, Integer>();
			delay = 0;
			sendToPionner();
			switch (aMsg.event_id) {
			case VS_QOS_ARRIVED:
				// strQos = aMsg.data;
				LogUtil.w(TAG, "recv VS_QOS_ARRIVED");
				LogUtil.w(TAG, aMsg.data);
				streamingItem.analyseMsg(aMsg.data, StreamingItem.QOS_ARRIVED);
				vmos.add((int) (streamingItem.qos.DVSNR_VMOS * 100));
				sendItemValue();
				statisticsQos();
				break;

			case VS_INITED:
				LogUtil.w(TAG, "recv VS_INITED");
				// LogUtil.w(TAG, aMsg.data);
				args.append("local_if::").append("").append("\n");
				args.append("ps_call::").append(taskModel.isPsCall() ? "0" : "1").append("\n");
				// args.append("ps_call::1\n");
				args.append("url::").append(taskModel.getmURL()).append("\n");// 流媒体地址
				args.append("use_tcp::").append(taskModel.ismUseTCP() ? "1" : "0").append("\n");// 是否使用TCP

				args.append("media_quality::").append(taskModel.getVideoQuality() == 0 ? "4001" : "4002").append("\n");// 视频质量
				args.append("playtime_ms::").append(Integer.parseInt(taskModel.getmPlayTime()) * 1000).append("\n");// 播放时长
				args.append("connect_timeout_ms::")
						.append(taskModel.isPsCall() ? (Integer.parseInt(taskModel.getmPlayTime()) * 1000) : 60000).append("\n");// 连接超时

				args.append("nodata_timeout_ms::").append((Integer.parseInt(taskModel.getmNodataTimeout())) * 1000)
						.append("\n");// 无流量超时
				args.append("buffer_max_time_ms::").append((Integer.parseInt(taskModel.getBufferTime()) * 1000)).append("\n");// 最大缓冲时长
				// args.append("can_play_time_ms::").append((Integer.parseInt(taskModel.getBufferPlay())*1000)).append("\n");
				args.append("prebuffer_time_ms::").append((Integer.parseInt(taskModel.getBufferPlay()) * 1000)).append("\n");
				args.append("rebuffer_time_ms::").append((Integer.parseInt(taskModel.getBufferPlay()) * 1000)).append("\n");
				args.append("save_media::").append(taskModel.isSaveVideo() ? "1" : "0").append("\n");// 是否保存视频
				args.append("save_protol::").append(taskModel.isSaveprotol() ? "1" : "0").append("\n");
				args.append("save_path::").append(AppFilePathUtil.getInstance().getSDCardBaseDirectory(getString(R.string.path_video)));// 保存路径
				aIpc2Jni.send_command(VS_TEST, VS_START_TEST, args.toString(), args.length());
				LogUtil.w(TAG, args.toString());

				break;
			case VS_REQUEST_START:
				LogUtil.w(TAG, "recv VS_REQUEST_START");
				// LogUtil.w(TAG, aMsg.data);

				stream_start_time = aMsg.getRealTime();

				String values[] = aMsg.data.split("\n");
				for (String value : values) {
					String[] key_value = value.split("::");
					if (key_value.length < 2)
						continue;

					if (key_value[0].equals("website_type")) {
						websiteType = Integer.parseInt(key_value[1]);
					} else if (key_value[0].equals("url")) {
						url = key_value[1];
					}
				}

				// 显示事件
				displayEvent(DataTaskEvent.STREAMING_REQUEST.toString());
				EventBytes.Builder(getBaseContext(), RcuEventCommand.Streaming_Request).addInteger(websiteType)
						.addStringBuffer(url).writeToRcu(aMsg.getRealTime());

				// totalMap = new HashMap<String, Integer>();
				// 尝试次数
				totalMap.put(TotalStruct.TotalVS._vsTrys.name(), 1);

				// 初始化其它参数
				totalMap.put(TotalStruct.TotalVS._vsSuccs.name(), 0); // 流媒体成功次数
				totalMap.put(TotalStruct.TotalVS._vsConnectTime.name(), 0);// 接入时长
				totalMap.put(TotalStruct.TotalVS._vsReproductionStart.name(), 0);// 复制开始次数
				totalMap.put(TotalStruct.TotalVS._vsReproductionFailure.name(), 0);
				totalMap.put(TotalStruct.TotalVS._vsReproductionDaily.name(), 0);// 接入时延
				totalMap.put(TotalStruct.TotalVS._vsReproductionEnd.name(), 0);// 复制成功次数
				totalMap.put(TotalStruct.TotalVS._vsPlayEnd.name(), 0); // 播放完成次数
				totalMap.put(TotalStruct.TotalVS._vsDrop.name(), 0); // 掉线次数
				totalMap.put(TotalStruct.TotalVS._vsReBufferFailure.name(), 0); // 缓冲导制的失败次数
				totalMap.put(TotalStruct.TotalVS._vsReBuffers.name(), 0); // 缓冲次数
				totalMap.put(TotalStruct.TotalVS._vsReBufferSuccess.name(), 0); // 缓冲成功次数
				totalMap.put(TotalStruct.TotalVS._vsRebufferTime.name(), 0); // 缓冲时长
				totalMap.put(TotalStruct.TotalVS._vsTotalQosTimes.name(), 0); // QOS报告次数
				totalMap.put(TotalStruct.TotalVS._vsTotalReceivedPackage.name(), 0); // 接收保总数
				totalMap.put(TotalStruct.TotalVS._vsTotalPackgeLoss.name(), 0); // 丢包总数
				totalMap.put(TotalStruct.TotalVS._vsMeanPackedInterval.name(), 0); // 平均慢包间隔时间
				totalMap.put(TotalStruct.TotalVS._vsMeanJitter.name(), 0); // 平均包抖动
				totalMap.put(TotalStruct.TotalVS._vsTotalReceivedSize.name(), 0); // 接收数据总大小
				totalMap.put(TotalStruct.TotalVS._vsMeanRate.name(), 0); // 平均接收速率
				totalMap.put(TotalStruct.TotalVS._vsTotalBufferCount.name(), 0); // 缓冲总次数
				totalMap.put(TotalStruct.TotalVS._vsMeanAVDelay.name(), 0); // 平均A-V时延
				totalMap.put(TotalStruct.TotalVS._vsMeanAVCorrection.name(), 0); // 平均A-V纠错帧率
				totalMap.put(TotalStruct.TotalVS._vsMeanAVSync.name(), 0); // 平均A-V丢帧率
				totalMap.put(TotalStruct.TotalVS._vsMeanFPS.name(), 0); // 平均帧率
				totalMap.put(TotalStruct.TotalVS._vs_decode_frames.name(), 0); // 解码过的帧数
				totalMap.put(TotalStruct.TotalVS._vsVideoTotalRecv.name(), 0);
				totalMap.put(TotalStruct.TotalVS._vsAudio_Pkg_Recv.name(), 0);
				totalMap.put(TotalStruct.TotalVS._vsVideoTotalLost.name(), 0);
				totalMap.put(TotalStruct.TotalVS._vsAudio_Pkg_Lost.name(), 0);
				totalMap.put(TotalStruct.TotalVS._vsVideo_Interval.name(), 0);
				totalMap.put(TotalStruct.TotalVS._vsAudio_Interval.name(), 0);
				totalMap.put(TotalStruct.TotalVS._vsVideo_Jitter.name(), 0);
				totalMap.put(TotalStruct.TotalVS._vsAudio_Jitter.name(), 0);
				totalMap.put(TotalStruct.TotalVS._vsTotalBytes.name(), 0);
				totalMap.put(TotalStruct.TotalVS._vsAvgRecvSpeedKbps.name(), 0);
				totalMap.put(TotalStruct.TotalVS._vsA_v_async.name(), 0);
				totalMap.put(TotalStruct.TotalVS._vsAv_Vmos.name(), 0);
				totalMap.put(TotalStruct.TotalVS._vsAv_VmosTimes.name(), 0);
				totalMap.put(TotalStruct.TotalVS._vsTotalSample.name(), 0);
				mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
				break;
			case VS_FIRSTDATA_ARRIVED:
				LogUtil.w(TAG, "recv VS_FIRSTDATA_ARRIVED");
				LogUtil.w(TAG, aMsg.data);
				firstArvTime = aMsg.getRealTime() / 1000;
				streamingItem.analyseMsg(aMsg.data, StreamingItem.FIRSTDATA_ARRIVED);

				delay = (int) (aMsg.getRealTime() - stream_start_time) / 1000;
				disMsg = String.format(DataTaskEvent.STREAMING_FIRST_DATA.toString(), delay, // 时延
						streamingItem.firstDataItem.duration_ms / 1000, streamingItem.firstDataItem.video_width,
						streamingItem.firstDataItem.video_height, streamingItem.firstDataItem.video_fps,
						streamingItem.firstDataItem.total_bitrate
								/ UtilsMethod.kbyteRage/*
																				 * , websiteType == 0 ? "Monternet" :
																				 * "Common",
																				 * streamingItem.firstDataItem.
																				 * media_quality == 4001 ? "Low Quality"
																				 * : (streamingItem.firstDataItem.
																				 * media_quality == 4002 ?
																				 * "High Quality" : "UnKnow")
																				 */
				);

				displayEvent(disMsg);

				EventBytes.Builder(getBaseContext(), RcuEventCommand.Streaming_First_Data)
						.addInteger(streamingItem.firstDataItem.duration_ms)
						.addCharArray(streamingItem.firstDataItem.video_codec.toCharArray(), 12)
						.addCharArray(streamingItem.firstDataItem.audio_codec.toCharArray(), 12)
						.addInteger(streamingItem.firstDataItem.video_width).addInteger(streamingItem.firstDataItem.video_height)
						.addInteger(streamingItem.firstDataItem.video_fps).addInteger(streamingItem.firstDataItem.total_bitrate)
						.addInteger(streamingItem.firstDataItem.media_quality).writeToRcu(aMsg.getRealTime());

				// totalMap = new HashMap<String, Integer>();
				// 事件节点约计
				totalMap.put(TotalStruct.TotalVS._vsSuccs.name(), 1);// 接入成功次数
				totalMap.put(TotalStruct.TotalVS._vsConnectTime.name(), delay);// 接入花费时长
				mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));

				stream_start_time = aMsg.getRealTime();
				// startDownTime = System.currentTimeMillis();
				break;
			case VS_REQUEST_FAILED:
				LogUtil.w(TAG, "recv VS_REQUEST_FAILED");
				// LogUtil.w(TAG, aMsg.data);

				reason = getReason(aMsg.data);
				LogUtil.w(TAG, "Failure Reson=" + reason);
				disMsg = String.format(DataTaskEvent.STREAMING_REQUEST_FAILURE.toString(),
						(aMsg.getRealTime() - stream_start_time) / 1000, FailReason.getFailReason(reason).getResonStr());
				displayEvent(disMsg);

				EventBytes.Builder(getBaseContext(), RcuEventCommand.Streaming_Request_Failure).addInteger(reason)
						.writeToRcu(aMsg.getRealTime());
				break;
			case VS_REPRODUCTION_START:
				LogUtil.w(TAG, "recv VS_REPRODUCTION_START");

				delay = (int) (aMsg.getRealTime() - stream_start_time) / 1000;
				disMsg = String.format(DataTaskEvent.STREAMING_REPRODUCTION_START.toString(), delay);
				displayEvent(disMsg);
				writeRcuEvent(RcuEventCommand.Streaming_Reproduction_Start, aMsg.getRealTime());

				// totalMap = new HashMap<String, Integer>();
				// 开始复制次数
				totalMap.put(TotalStruct.TotalVS._vsReproductionStart.name(), 1);
				// 复制开始时延
				totalMap.put(TotalStruct.TotalVS._vsReproductionDaily.name(), delay);
				mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));

				break;
			case VS_REPRODUCTION_START_FAILED:
				LogUtil.w(TAG, "recv VS_REPRODUCTION_START_FAILED");
				// LogUtil.w(TAG, aMsg.data);

				reason = getReason(aMsg.data);
				disMsg = String.format(DataTaskEvent.STREAMING_REPRODUCTION_START_FAILURE.toString(),
						(aMsg.getRealTime() - stream_start_time) / 1000, FailReason.getFailReason(reason).getResonStr());
				displayEvent(disMsg);
				writeRcuEvent(RcuEventCommand.Streaming_Reproduction_Start_Failure, reason);

				// totalMap = new HashMap<String, Integer>();
				// 开始复制失败次数
				totalMap.put(TotalStruct.TotalVS._vsReproductionFailure.name(), 1);
				mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));

				break;
			case VS_RECV_FINISH:
				LogUtil.w(TAG, "recv VS_RECV_FINISH");
				// LogUtil.w(TAG, aMsg.data);
				streamingItem.analyseMsg(aMsg.data, StreamingItem.LAST_DATA);
				sendItemValue();
				isLastData = Boolean.TRUE;
				disMsg = String.format(DataTaskEvent.STREAMING_LAST_DATA.toString(),
						streamingItem.qos.RecvTotalBytes / UtilsMethod.kbyteRage, // 总下载数据量
						streamingItem.qos.ReBufferTimes, // 缓冲次数
						streamingItem.qos.ReBufferTimeMS, // 缓冲时间
						getVmosValue(),
						// streamingItem.qos.DVSNR_VMOS,
						streamingItem.lastData.AV_DeSync_Rate,
						streamingItem.qos.LostVideoPacket == 0 ? 0
								: streamingItem.qos.LostVideoPacket * 1f
										/ (streamingItem.qos.RecvVideoPacket + streamingItem.qos.LostVideoPacket),
						streamingItem.qos.AvgVideoPacketGap, streamingItem.qos.AvgVideoPacketJitter);
				displayEvent(disMsg);

				EventBytes.Builder(getBaseContext(), RcuEventCommand.Streaming_Last_Data)
						.addInteger(streamingItem.qos.RecvTotalBytes).addInteger(streamingItem.qos.ReBufferTimes)
						.addInteger(streamingItem.qos.ReBufferTimeMS).addSingle(getVmosValue())
						// .addSingle(streamingItem.lastData.VSNR_VMOS)
						.addInteger(streamingItem.lastData.AV_DeSync_Rate).addInteger(streamingItem.qos.RecvVideoPacket)
						.addInteger(streamingItem.qos.RecvAudioPacket).addInteger(streamingItem.qos.LostVideoPacket)
						.addInteger(streamingItem.qos.LostAudioPacket).addInteger(streamingItem.qos.AvgVideoPacketGap)
						.addInteger(streamingItem.qos.AvgAudioPacketGap).addInteger(streamingItem.qos.MaxVideoPacketJitter)
						.addInteger(streamingItem.qos.MaxAudioPacketJitter).addInteger(streamingItem.qos.MinVideoPacketJitter)
						.addInteger(streamingItem.qos.MinAudioPacketJitter).addInteger(streamingItem.qos.AvgVideoPacketJitter)
						.addInteger(streamingItem.qos.AvgAudioPacketJitter).addStringBuffer(streamingItem.lastData.VideoSaveFile)
						.writeToRcu(aMsg.getRealTime());

				// totalMap = new HashMap<String, Integer>();
				// 播放完成次数+1
				// totalMap.put(TotalStruct.TotalVS._vsPlayEnd.name(), 1);
				// 平均VMOS
				// totalMap.put(TotalStruct.TotalVS._vsAv_VmosTimes.name(), 1);
				// totalMap.put(TotalStruct.TotalVS._vsAv_Vmos.name(),
				// ((int)(getVmosValue()*100)));
				// totalMap.put(TotalStruct.TotalVS._vsA_v_async.name(),
				// streamingItem.lastData.AV_DeSync_Rate);
				mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
				break;
			case VS_RECV_DROP:
				LogUtil.w(TAG, "recv VS_RECV_DROP");
				LogUtil.w(TAG, aMsg.data);
				streamingItem.analyseMsg(aMsg.data, StreamingItem.LAST_DATA);
				disMsg = String.format(DataTaskEvent.STREAMING_DROP.toString(),
						streamingItem.qos.RecvTotalBytes / UtilsMethod.kbyteRage, // 总下载数据量
						streamingItem.qos.ReBufferTimes, // 缓冲次数
						streamingItem.qos.ReBufferTimeMS, // 缓冲时间
						getVmosValue(),
						// streamingItem.qos.DVSNR_VMOS,
						streamingItem.lastData.AV_DeSync_Rate,
						streamingItem.qos.LostVideoPacket == 0 ? 0
								: streamingItem.qos.LostVideoPacket * 1f
										/ (streamingItem.qos.RecvVideoPacket + streamingItem.qos.LostVideoPacket),
						streamingItem.qos.AvgVideoPacketGap, streamingItem.qos.AvgVideoPacketJitter,
						RcuEventCommand.DropReason.getDropReason(streamingItem.lastData.reason).getResonStr());
				displayEvent(disMsg);

				EventBytes.Builder(getBaseContext(), RcuEventCommand.Streaming_Drop)
						.addInteger(streamingItem.qos.RecvTotalBytes).addInteger(streamingItem.qos.ReBufferTimes)
						.addInteger(streamingItem.qos.ReBufferTimeMS)
						// .addDouble(streamingItem.lastData.VSNR_VMOS)
						.addSingle(getVmosValue()).addInteger(streamingItem.lastData.AV_DeSync_Rate)
						.addInteger(streamingItem.qos.RecvVideoPacket).addInteger(streamingItem.qos.RecvAudioPacket)
						.addInteger(streamingItem.qos.LostVideoPacket).addInteger(streamingItem.qos.LostAudioPacket)
						.addInteger(streamingItem.qos.AvgVideoPacketGap).addInteger(streamingItem.qos.AvgAudioPacketGap)
						.addInteger(streamingItem.qos.MaxVideoPacketJitter).addInteger(streamingItem.qos.MaxAudioPacketJitter)
						.addInteger(streamingItem.qos.MinVideoPacketJitter).addInteger(streamingItem.qos.MinAudioPacketJitter)
						.addInteger(streamingItem.qos.AvgVideoPacketJitter).addInteger(streamingItem.qos.AvgAudioPacketJitter)
						.addStringBuffer(streamingItem.lastData.VideoSaveFile).addInteger(streamingItem.lastData.reason)
						.writeToRcu(aMsg.getRealTime());

				// totalMap = new HashMap<String, Integer>();
				// 如果Drop时是User stop或Time out算一次
				if (streamingItem.lastData.reason == 1 || streamingItem.lastData.reason == -101) {
					// 播放完成次数+1
					totalMap.put(TotalStruct.TotalVS._vsPlayEnd.name(), 1);
					sendMsgToPioneer(",bSuccess=1");
				} else {
					// 播放掉线次数+1
					totalMap.put(TotalStruct.TotalVS._vsDrop.name(), 1);
					sendMsgToPioneer(",bSuccess=0");
				}

				// 如果出现这4个原因码，记重缓冲失败
				if (streamingItem.lastData.reason == 4 || streamingItem.lastData.reason == -102
						|| streamingItem.lastData.reason == -103 || streamingItem.lastData.reason == -104) {
					totalMap.put(TotalStruct.TotalVS._vsReBufferFailure.name(), 1);
				}

				// 平均VMOS
				// totalMap.put(TotalStruct.TotalVS._vsAv_VmosTimes.name(), 1);
				// totalMap.put(TotalStruct.TotalVS._vsAv_Vmos.name(),
				// ((int)(getVmosValue()*100)));
				mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
				break;
			case VS_REBUFFERING_START:
				LogUtil.w(TAG, "recv VS_REBUFFERING_START");

				displayEvent(DataTaskEvent.STREAMING_REBUFFERING_START.toString());
				writeRcuEvent(RcuEventCommand.Streaming_Rebuffering_Start, aMsg.getRealTime());

				// totalMap = new HashMap<String, Integer>();
				// 重缓冲次数
				totalMap.put(TotalStruct.TotalVS._vsReBuffers.name(), 1);
				mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));

				stream_start_time = aMsg.getRealTime();

				break;
			case VS_REBUFFERING_END:
				LogUtil.w(TAG, "recv VS_REBUFFERING_END");

				delay = (int) (aMsg.getRealTime() - stream_start_time) / 1000;
				disMsg = String.format(DataTaskEvent.STREAMING_REBUFFERING_END.toString(), delay);
				displayEvent(disMsg);
				writeRcuEvent(RcuEventCommand.Streaming_Rebuffering_End, aMsg.getRealTime());

				// totalMap = new HashMap<String, Integer>();
				// 缓冲成功次数
				totalMap.put(TotalStruct.TotalVS._vsReBufferSuccess.name(), 1);
				totalMap.put(TotalStruct.TotalVS._vsRebufferTime.name(), delay);
				mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));

				break;
			case VS_PLAY_FINISH:
				LogUtil.w(TAG, "recv VS_PLAY_FINISH");

				displayEvent(DataTaskEvent.STREAMING_PLAY_FINISHED.toString());
				writeRcuEvent(RcuEventCommand.Streaming_Play_Finished, aMsg.getRealTime());
				totalMap.put(TotalStruct.TotalVS._vsPlayEnd.name(), 1);
				mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
				sendMsgToPioneer(",bSuccess=1");
				break;
			case VS_QUIT:
				LogUtil.w(TAG, "recv VS_QUIT");
				stopPlay();
				isFinish = true;
				stopTest(false);
				break;
			}
		}

		private int getReason(String data) {
			String[] values = data.split("\n");
			for (String v : values) {
				String[] key_value = v.split("::");
				if (key_value.length < 2) {
					continue;
				}

				if (key_value[0].equals("reason")) {
					return Integer.parseInt(key_value[1]);
				}
			}
			return 0;
		}
	};

	/**
	 * 停止显示视频播放
	 */
	private void stopPlay() {
		Map<String, Object> callBackValues = new HashMap<String, Object>();
		callBackValues.put(ConstItems.VIDEO_TYPE, WalkCommonPara.CALL_BACK_VIDEO_STREAM_REAL_PARA);
		callBackValues.put(ConstItems.IS_SHOW_VIDEO, false);
		mHandler.sendMessage(mHandler.obtainMessage(REAL_PARA, callBackValues));
	}

	private int lastRecvVideoPacket = 0;
	private int lastRecvAudioPacket = 0;
	private int lastLostAudioPacket = 0;
	private int lastLostVideoPacket = 0;
	private int lastRectTotalBytes = 0;
	private int lastMeasureTime = 0;

	private void statisticsQos() {
		totalMap = new HashMap<String, Integer>();
		// 接收到Qos总次数
		totalMap.put(TotalStruct.TotalVS._vsTotalQosTimes.name(), 1);

		// 接收到视频包数
		totalMap.put(TotalStruct.TotalVS._vsVideoTotalRecv.name(), streamingItem.qos.RecvVideoPacket - lastRecvVideoPacket);
		lastRecvVideoPacket = streamingItem.qos.RecvVideoPacket;

		// 丢失的视频包数
		totalMap.put(TotalStruct.TotalVS._vsVideoTotalLost.name(), streamingItem.qos.LostVideoPacket - lastLostVideoPacket);
		lastLostVideoPacket = streamingItem.qos.LostVideoPacket;

		// 接收到音频包数
		totalMap.put(TotalStruct.TotalVS._vsAudio_Pkg_Recv.name(), streamingItem.qos.RecvAudioPacket - lastRecvAudioPacket);
		lastRecvAudioPacket = streamingItem.qos.RecvAudioPacket;

		// 丢失的音频包数
		totalMap.put(TotalStruct.TotalVS._vsAudio_Pkg_Lost.name(), streamingItem.qos.LostAudioPacket - lastLostAudioPacket);
		lastLostAudioPacket = streamingItem.qos.LostAudioPacket;

		// 平均音频包间隔
		totalMap.put(TotalStruct.TotalVS._vsAudio_Interval.name(), streamingItem.qos.AvgAudioPacketGap);
		// 平均音频包抖动
		totalMap.put(TotalStruct.TotalVS._vsAudio_Jitter.name(), streamingItem.qos.AvgAudioPacketJitter);

		// 平均视频包间隔
		totalMap.put(TotalStruct.TotalVS._vsVideo_Interval.name(), streamingItem.qos.AvgVideoPacketGap);
		// 平均视频包抖动
		totalMap.put(TotalStruct.TotalVS._vsVideo_Jitter.name(), streamingItem.qos.AvgVideoPacketJitter);

		// 平均A-V不同步
		totalMap.put(TotalStruct.TotalVS._vsA_v_async.name(), streamingItem.qos.AV_DeSync == 0 ? 1 : 0);

		// 接收总数据大小
		totalMap.put(TotalStruct.TotalVS._vsTotalBytes.name(), streamingItem.qos.RecvTotalBytes - lastRectTotalBytes);
		lastRectTotalBytes = streamingItem.qos.RecvTotalBytes;

		totalMap.put(TotalStruct.TotalVS._vsTotalTime.name(), streamingItem.qos.MeasureTime - lastMeasureTime);
		lastMeasureTime = streamingItem.qos.MeasureTime;

		totalMap.put(TotalStruct.TotalVS._vsAv_Vmos.name(), (int) (streamingItem.qos.DVSNR_VMOS * 100));
		totalMap.put(TotalStruct.TotalVS._vsTotalSample.name(), 1);
		// 平均速率
		// totalMap.put(TotalStruct.TotalVS._vsAvgRecvSpeedKbps.name(),
		// ((int)(streamingItem.qos.RecvTotalBytes*1000f/streamingItem.qos.MeasureTime)));

		mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	}

	/*
	 * 发送实时数据
	 */
	private void sendItemValue() {
		Map<String, Object> callBackValues = new HashMap<String, Object>();
		callBackValues.put(ConstItems.VIDEO_TYPE, WalkCommonPara.CALL_BACK_VIDEO_STREAM_REAL_PARA);
		callBackValues.put(ConstItems.IS_SHOW_VIDEO, taskModel.isShowVideo());
		callBackValues.put(ConstItems.TOTAL_RECV_SIZE, streamingItem.qos.RecvTotalBytes / UtilsMethod.kbyteRage);

		callBackValues.put(ConstItems.DOWN_PROCESS, streamingItem.qos.DownloadProgress);
		callBackValues.put(ConstItems.REBUF_COUNTS, streamingItem.qos.ReBufferTimes);
		callBackValues.put(ConstItems.REBUF_TIMES, streamingItem.qos.ReBufferTimeMS);
		callBackValues.put(ConstItems.VMOS, streamingItem.qos.DVSNR_VMOS);
		callBackValues.put(ConstItems.AV_SYNC, streamingItem.qos.AV_DeSync == 1 ? "out-sync" : "sync");
		callBackValues.put(ConstItems.MEAN_DL_RATE,
				streamingItem.qos.RecvTotalBytes * 8f / (streamingItem.qos.MeasureTime / 1000));
		callBackValues.put(ConstItems.MEDIA_QUALITY,
				// streamingItem.firstDataItem.media_quality == 1 ? "Low Quality" :
				// "High Quality");
				streamingItem.firstDataItem.media_quality == 4001 ? "Low Quality"
						: (streamingItem.firstDataItem.media_quality == 4002 ? "High Quality" : "UnKnow"));
		callBackValues.put(ConstItems.TOTAL_BIT_RATE, streamingItem.firstDataItem.total_bitrate);
		callBackValues.put(ConstItems.VIDEO_FPS, streamingItem.firstDataItem.video_fps);
		callBackValues.put(ConstItems.VIDEO_WITH, streamingItem.firstDataItem.video_width);
		callBackValues.put(ConstItems.VIDEO_HEIGHT, streamingItem.firstDataItem.video_height);

		callBackValues.put(ConstItems.TOTAL_VIDEO_RECV_PACKETS, streamingItem.qos.RecvVideoPacket);
		callBackValues.put(ConstItems.TOTAL_AUDIO_RECV_PACKETS, streamingItem.qos.RecvAudioPacket);
		float videoPacketsLossRate = streamingItem.qos.LostVideoPacket == 0 ? 0
				: streamingItem.qos.LostVideoPacket * 1f
						/ (streamingItem.qos.RecvVideoPacket + streamingItem.qos.LostVideoPacket) * 100;
		callBackValues.put(ConstItems.TOTAL_VIDEO_PACKETS_LOSS_RATE, String.format("%.2f", videoPacketsLossRate));
		float audioPacketsLossRate = streamingItem.qos.LostAudioPacket == 0 ? 0
				: streamingItem.qos.LostAudioPacket * 1f
						/ (streamingItem.qos.LostAudioPacket + streamingItem.qos.RecvAudioPacket) * 100;
		callBackValues.put(ConstItems.TOTAL_AUDIO_PACKETS_LOSS_RATE, String.format("%.2f", audioPacketsLossRate));
		callBackValues.put(ConstItems.VIDEO_MAX_JITTER, streamingItem.qos.MaxVideoPacketJitter);
		callBackValues.put(ConstItems.AUDIO_MAX_JITTER, streamingItem.qos.MaxAudioPacketJitter);
		callBackValues.put(ConstItems.VIDEO_MEAN_JITTER, streamingItem.qos.AvgVideoPacketJitter);
		callBackValues.put(ConstItems.AUDIO_MEAN_JITTER, streamingItem.qos.AvgAudioPacketJitter);
		callBackValues.put(ConstItems.VIDEO_INST_JITTER, streamingItem.qos.CurVideoPacketJitter);
		callBackValues.put(ConstItems.AUDIO_INST_JITTER, streamingItem.qos.CurAudioPacketJitter);

		callBackValues.put(ConstItems.CUR_RECV_SPEED, streamingItem.qos.CurRecvSpeed);
		callBackValues.put(ConstItems.CurFps, streamingItem.qos.CurFps);
		callBackValues.put(ConstItems.BufferRatio, streamingItem.qos.BufferRatio);
		callBackValues.put(ConstItems.CurVideoLostFraction, streamingItem.qos.CurVideoLostFraction);
		callBackValues.put(ConstItems.CurAudioLostFraction, streamingItem.qos.CurAudioLostFraction);
		callBackValues.put(ConstItems.MEAN_VIDEO_PACKET_INTERVAL, streamingItem.qos.AvgVideoPacketGap);
		callBackValues.put(ConstItems.MEAN_AUDIO_PACKET_INTERVAL, streamingItem.qos.AvgAudioPacketGap);
		callBackValues.put(ConstItems.MaxVideoPacketGap, streamingItem.qos.MaxVideoPacketGap);
		callBackValues.put(ConstItems.MaxAudioPacketGap, streamingItem.qos.MaxAudioPacketGap);
		callBackValues.put(ConstItems.MinVideoPacketGap, streamingItem.qos.MinVideoPacketGap);
		callBackValues.put(ConstItems.MinAudioPacketGap, streamingItem.qos.MinAudioPacketGap);

		;
		mHandler.sendMessage(mHandler.obtainMessage(REAL_PARA, callBackValues));
	}

	private Message msg;
	private float kByte = UtilsMethod.kbyteRage;
	private long totalTransSize = 0;
	private float meanRate = 0f; // 速率
	private float averageRage = 0f; // 平均速度

	/** 统计当前传输大小，时长，瞬时速率，平均速率，最大速率等相关信息 */
	private void turnTransfersRate() {
		if (streamingItem.qos.MeasureTime != 0) {
			// 计算当前每秒传输速度

			int interBytes = (int) (streamingItem.qos.RecvTotalBytes - totalTransSize); // 间隔获得数据包大小
			totalTransSize = streamingItem.qos.RecvTotalBytes; // 计算前将当前获得的总大小赋给临时总大小变量，以防计算过程中当前次总大小发生变化
			Map<String, Object> dataMap = new HashMap<String, Object>();

			// 当前平均值
			averageRage = (totalTransSize * 8 / (streamingItem.qos.MeasureTime / 1000f)) / kByte;
			dataMap.put(DataTaskValue.BordLeftTitle.name(), getString(R.string.info_data_averagerate) + "@"
					+ UtilsMethod.decFormat.format(averageRage) + getString(R.string.info_rate_kbps));
			meanRate = (interBytes * 8 / ((streamingItem.qos.MeasureTime - streamingItem.qos.prevMeasureTime) / 1000f))
					/ kByte;
			dataMap.put(DataTaskValue.BordCurrentSpeed.name(), UtilsMethod.decFormat.format(meanRate));
			dataMap.put(DataTaskValue.BordProgress.name(), streamingItem.qos.DownloadProgress);
			msg = mHandler.obtainMessage(DATA_CHANGE, dataMap);
			mHandler.sendMessage(msg);
		}
	}

	/**
	 * 住Pionner发送数据
	 */
	private void sendToPionner() {
		// 如果已经收到FirstDataArrive
		if (streamingItem.qos.MeasureTime != 0 && firstArvTime != 0 && (System.currentTimeMillis() - firstArvTime) > 1000) {
			firstArvTime = System.currentTimeMillis();
			sendMsgToPioneer(String.format(tmpPionner, streamingItem.qos.MeasureTime,
					streamingItem.qos.RecvTotalBytes * 8 / UtilsMethod.kbyteRage,
					(streamingItem.qos.RecvTotalBytes * 8 / (UtilsMethod.kbyteRage * (streamingItem.qos.MeasureTime / 1000)))));
		}
	}

	private String tmpPionner = ",DurationTime=%d,TransmitSize=%.2f,InstRate=%.2f";

	// private void writeEvent(int reason,long time) {
	// if (!isFinish) {
	// isFinish = true;
	// totalMap = new HashMap<String, Integer>();f,//总下载数据量
	// streamingItem.qos.ReBufferTimes,//缓冲次数
	// streamingItem.qos.ReBufferTimeMS,//缓冲时间
	// getVmosValue(),
	// //streamingItem.qos.DVSNR_VMOS,
	// streamingItem.lastData.AV_DeSync
	// totalMap.put(TotalStruct.TotalVS._vsDrop.name(), 1);
	// //平均VMOS
	// //totalMap.put(TotalStruct.TotalVS._vsAv_VmosTimes.name(), 1);
	// //totalMap.put(TotalStruct.TotalVS._vsAv_Vmos.name(),
	// ((int)(getVmosValue()*100)));
	// mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	//
	// disMsg = String.format(DataTaskEvent.STREAMING_DROP.toString(),
	// streamingItem.qos.RecvTotalBytes/1000_Rate,
	// streamingItem.qos.LostVideoPacket/(streamingItem.qos.RecvVideoPacket*1f),
	// streamingItem.qos.AvgVideoPacketGap,
	// streamingItem.qos.AvgVideoPacketJitter,
	// DropReason.getDropReason(reason).getResonStr()
	// );
	// displayEvent(disMsg);
	//
	// EventBytes.Builder(getBaseContext(), RcuEventCommand.Streaming_Last_Data)
	// .addInteger(streamingItem.qos.RecvTotalBytes)
	// .addInteger(streamingItem.qos.ReBufferTimes)
	// .addInteger(streamingItem.qos.ReBufferTimeMS)
	// .addSingle(getVmosValue())
	// //.addDouble(streamingItem.lastData.VSNR_VMOS)
	// .addInteger(streamingItem.lastData.AV_DeSync_Rate)
	// .addInteger(streamingItem.qos.RecvVideoPacket)
	// .addInteger(streamingItem.qos.RecvAudioPacket)
	// .addInteger(streamingItem.qos.LostVideoPacket)
	// .addInteger(streamingItem.qos.LostAudioPacket)
	// .addInteger(streamingItem.qos.AvgVideoPacketGap)
	// .addInteger(streamingItem.qos.AvgAudioPacketGap)
	// .addInteger(streamingItem.qos.MaxVideoPacketJitter)
	// .addInteger(streamingItem.qos.MaxAudioPacketJitter)
	// .addInteger(streamingItem.qos.MinVideoPacketJitter)
	// .addInteger(streamingItem.qos.MinAudioPacketJitter)
	// .addInteger(streamingItem.qos.AvgVideoPacketJitter)
	// .addInteger(streamingItem.qos.AvgAudioPacketJitter)
	// .addBytes(streamingItem.lastData.VideoSaveFile.getBytes())
	// .addInteger(reason)
	// .writeToRcu(time);
	// sendMsgToPioneer(",bSuccess=0");
	// }
	// }
}
