package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.dingli.dmplayer.sdktest.DMPlayerAPI;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.FailedInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.KpiInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.MsgInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.PlayQosInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.QosInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.RecvDropInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.RecvFinishInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.ReproductionStartFailedInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.ReproductionStartInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.SegInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.dnsfail_info;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.dnsstart_info;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.dnssucc_info;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.send_get_info;
import com.walktour.Utils.ApplicationModel;
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
import com.walktour.Utils.WebSiteTypeUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.gui.task.parsedata.model.task.videoplay.StringSpecialInit;
import com.walktour.gui.task.parsedata.model.task.videoplay.TaskVideoPlayModel;
import com.walktour.service.ICallback;
import com.walktour.service.IService;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Vitamio版实现视频播放测试业务
 *
 * @author jianchao.wang
 *
 */

@SuppressLint("SdCardPath")
public abstract class VideoPlayTestBase extends TestTaskService {
	private static final String TAG = "VideoPlayTestBase";

	/** 广播消息 */
	public static final String BROADCAST_INITED = "VideoPlayInited";
	public static final String BROADCAST_YOUTUBE_INIT_FAILED = "VideoPlayYouTubeInitFailed";
	public static final String BROADCAST_INIT_FAILED = "VideoPlayInitFailed";
	public static final String BROADCAST_FIRST_DATA_ARRIVED = "VideoPlayFirstDataArrived";
	public static final String BROADCAST_REPRODUCTION_START = "VideoPlayReproductionStart";
	public static final String BROADCAST_REPRODUCTION_START_FAILED = "VideoPlayReproductionStartFailed";
	public static final String BROADCAST_QOS_ARRIVED = "VideoPlayQosArrived";
	public static final String BROADCAST_RECV_FINISH = "VideoPlayRecvFinish";
	public static final String BROADCAST_RECV_DROP = "VideoPlayRecvDrop";
	public static final String BROADCAST_REBUFFERING_START = "VideoPlayReBufferingStart";
	public static final String BROADCAST_REBUFFERING_END = "VideoPlayReBufferingEnd";
	public static final String BROADCAST_PLAY_QOS_ARRIVED = "VideoPlayPlayQosArrived";
	public static final String BROADCAST_PLAY_FINISH = "VideoPlayPlayFinish";
	public static final String BROADCAST_QUIT = "VideoPlayQuit";
	public static final String BROADCAST_MSG = "VideoPlayMsg";
	public static final String BROADCAST_STOP = "VideoPlayStop";
	public static final String BROADCAST_START = "VideoPlayStart";
	public static final String BROADCAST_CONNECT_FAILED = "VideoPlayConnectFailed";
	public static final String BROADCAST_CONNECT_START = "VideoPlayConnectStart";
	public static final String BROADCAST_CONNECT_SUCCESS = "VideoPlayConnectSuccess";
	public static final String BROADCAST_DNS_FAILED = "VideoPlayDNSFailed";
	public static final String BROADCAST_DNS_START = "VideoPlayDNSStart";
	public static final String BROADCAST_DNS_SUCCESS = "VideoPlayDNSSuccess";
	public static final String BROADCAST_KPIS_REPORT = "VideoPlayKPISReport";
	public static final String BROADCAST_SENT_GET = "VideoPlaySentGet";
	public static final String BROADCAST_SENT_GET_FAILED = "VideoPlaySentGetFailed";
	public static final String BROADCAST_URL_PARSE_FAILED = "VideoPlayUrlParseFailed";
	public static final String BROADCAST_URL_PARSE_START = "VideoPlayUrlParseStart";
	public static final String BROADCAST_URL_PARSE_SUCCESS = "VideoPlayUrlParseSuccess";
	public static final String BROADCAST_SEGMENT_REPORT = "VideoPlaySegmentReport";

	protected static final int HTTPVS_INITED = 1;
	protected static final int HTTPVS_SEND_GET = 2;
	protected static final int HTTPVS_FIRSTDATA_ARRIVED = 3;
	protected static final int HTTPVS_REQUEST_FAILED = 4;
	protected static final int HTTPVS_REPRODUCTION_START = 5;
	protected static final int HTTPVS_REPRODUCTION_START_FAILED = 6;
	protected static final int HTTPVS_QOS_ARRIVED = 7;
	protected static final int HTTPVS_PLAY_QOS_ARRIVED = 8;
	protected static final int HTTPVS_RECV_FINISH = 9;
	protected static final int HTTPVS_RECV_DROP = 10;
	protected static final int HTTPVS_REBUFFERING_START = 11;
	protected static final int HTTPVS_REBUFFERING_END = 12;
	protected static final int HTTPVS_DISPLAY_VIDEO_FRAME = 13;
	protected static final int HTTPVS_SEGMENT_REPORT = 14;
	protected static final int HTTPVS_PLAY_FINISH = 15;
	protected static final int HTTPVS_QUIT = 16;

	protected static final int HTTPVS_URLPARSE_START = 30;
	protected static final int HTTPVS_URLPARSE_SUCCESS = 31;
	protected static final int HTTPVS_URLPARSE_FAILED = 32;
	protected static final int HTTPVS_KPIS_REPORT = 41;

	protected static final int HTTPVS_DNSRESOLVE_START = 20;
	protected static final int HTTPVS_DNSRESOLVE_SUCCESS = 21;
	protected static final int HTTPVS_DNSRESOLVE_FAILED = 22;
	protected static final int HTTPVS_CONNECT_START = 23;
	protected static final int HTTPVS_CONNECT_SUCCESS = 24;
	protected static final int HTTPVS_CONNECT_FAILED = 25;
	protected static final int HTTPVS_MSG = 101;
	protected static final int HTTPVS_START_TEST = 1001;
	protected static final int HTTPVS_STOP_TEST = 1006;

	private boolean isCallbackRegister = false;
	/** 视频播放对象 */
	protected TaskVideoPlayModel taskModel;
	/** 远程回调 */
	private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();
	/** mHandler: 调用回调函数 */
	protected MyHandler mHandler = new MyHandler(this);
	/** 是否测试结束 */
	protected boolean isTestFinish = false;
	/** 是否最后数据 */
	protected boolean isLastData = false;
	/** 是否中断 */
	protected boolean isDrop = false;
	/** 是否结束 */
	protected boolean isFinish = false;
	/** 当前步骤 */
	protected int currentStep;
	/** 第一数据接收时间 */
	protected long mFirstDataTime;
	/** 最后一次数据接收时间 */
	protected long mLastDataTime;
	/** 播放开始时间 */
	protected long playStartTime = System.currentTimeMillis() * 1000;
	/** 回调参数 */
	protected Map<String, Object> callBackValues;
	/** 动态VMOS值得集合 */
	private Set<Float> vmosSet = new HashSet<Float>();
	/** DNS开始解析时间 */
	private long mDNSStartTime;
	/** 连接开始时间 */
	private long mConnectStartTime;
	/***
	 *平均抖动，单位是us
	 */
	public int JitterAvg=0;
	/**
	 * 当前抖动，单位是us
	 */
	public int CurJitter=0;
	@Override
	public IBinder onBind(Intent arg0) {
		return mBind;
	}

	// 远程回调方法绑定
	private IService.Stub mBind = new IService.Stub() {
		@Override
		public void unregisterCallback(ICallback cb) throws RemoteException {
			if (cb != null) {
				mCallbacks.unregister(cb);
			}
		}

		@Override
		public void stopTask(boolean isUserStop, int dropReasion) throws RemoteException {
			stopTest();
			if (!isTestFinish) {
				isTestFinish = true;

				if (isUserStop || (dropReasion != DropReason.NORMAL.getReasonCode())) {

					Map<String, Integer> totalMap = new HashMap<String, Integer>();
					if (currentStep < HTTPVS_FIRSTDATA_ARRIVED) {
						int delay = (int) (System.currentTimeMillis() - playStartTime / 1000);
						String strQos = String.format(DataTaskEvent.VIDEO_PLAY_REQUEST_FAILURE.toString(), delay,
								isUserStop ? FailReason.USER_STOP.getResonStr() : FailReason.UNKNOWN.getResonStr());
						displayEvent(strQos);
						EventBytes.Builder(getBaseContext(), RcuEventCommand.VIDEOPLAY_REQUEST_FAILURE)
								.addInteger(isUserStop ? FailReason.USER_STOP.getReasonCode() : FailReason.UNKNOWN.getReasonCode())
								.writeToRcu(System.currentTimeMillis() * 1000);
						sendMsgToPioneer(",bSuccess=0");
					} else if (currentStep < HTTPVS_REPRODUCTION_START) {
						int delay = (int) (System.currentTimeMillis() - playStartTime / 1000);
						String strQos = String.format(DataTaskEvent.VIDEO_PLAY_REPRODUCTION_START_FAILURE.toString(), delay,
								isUserStop ? FailReason.USER_STOP.getResonStr() : FailReason.UNKNOWN.getResonStr());
						displayEvent(strQos);
						EventBytes.Builder(getBaseContext(), RcuEventCommand.VIDEOPLAY_REPRODUCTION_START_FAILURE)
								.addInteger(isUserStop ? FailReason.USER_STOP.getReasonCode() : FailReason.UNKNOWN.getReasonCode())
								.writeToRcu(System.currentTimeMillis() * 1000);

						// 开始复制失败次数
						totalMap.put(TotalStruct.TotalVideoPlay._videoReproductionFailure.name(), 1);
						mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
						sendMsgToPioneer(",bSuccess=0");
					} else if (!isFinish) {

						if (isLastData) {
							if (!isFinish) {
								isFinish = Boolean.TRUE;
								EventBytes.Builder(getBaseContext(), RcuEventCommand.VIDEOPLAY_PLAY_FINISHED)
										.writeToRcu(System.currentTimeMillis() * 1000);
								sendMsgToPioneer(",bSuccess=1");
							}
						}
						if (!isFinish) {
							if (isUserStop) {// 播放完成次数+1
								totalMap.put(TotalStruct.TotalVideoPlay._videoPlayEnd.name(), 1);
							} else {// 播放掉线次数+1
								totalMap.put(TotalStruct.TotalVideoPlay._videoDrop.name(), 1);
							}
							mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));

							// 停止当前次测试,并记为成功
							String strQos = String.format(DataTaskEvent.VIDEO_PLAY_DROP.toString(),
									ItemValues.RecvTotalBytes / UtilsMethod.kbyteRage, ItemValues.ReBufferTimes,
									ItemValues.ReBufferTimeMS, ItemValues.VSNR_VMOS, ItemValues.AV_DeSync_Rate, isUserStop
											? DropReason.USER_STOP.getResonStr() : DropReason.getDropReason(dropReasion).getResonStr());
							displayEvent(strQos);
						}
						// twq20151110上面执行send_commond之后,业务库会抛退出事件,在那个地方退出业务
						// msg = mHandler.obtainMessage(TEST_STOP, "1");
						// mHandler.sendMessage(msg);
						sendMsgToPioneer(",bSuccess=1");
					}
				}

			}
			stopPlay();
		}

		@Override
		public void registerCallback(ICallback cb) throws RemoteException {
			LogUtil.i(TAG, "registerCallback");
			if (cb != null) {
				mCallbacks.register(cb);
				isCallbackRegister = true;
			}
		}

		@Override
		public boolean getRunState() throws RemoteException {
			return startCommondRun;
		}

	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.d(tag, "-----onStartCommand-----");
		int startFlag = super.onStartCommand(intent, flags, startId);
		taskModel = (TaskVideoPlayModel) super.taskModel;
		StringSpecialInit.getInstance().setmContext(this);
		return startFlag;
	}

	/**
	 * 停止业务
	 *
	 */
	protected abstract void stopTest();

	/**
	 * 停止显示视频播放
	 */
	protected void stopPlay() {
		Map<String, Object> callBackValues = new HashMap<String, Object>();
		callBackValues.put(ConstItems.VIDEO_TYPE, WalkCommonPara.CALL_BACK_VIDEO_PLAY_REAL_PARA);
		callBackValues.put(ConstItems.IS_SHOW_VIDEO, false);
		mHandler.sendMessage(mHandler.obtainMessage(REAL_PARA, callBackValues));
	}

	/**
	 * 业务执行线程
	 *
	 * @author jianchao.wang
	 *
	 */
	protected class RunTest extends Thread {
		@Override
		public void run() {
			LogUtil.i(TAG, "wait for stard test");
			try {
				Thread.sleep(2000);
				while (!isCallbackRegister) {
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			startTest();
		}
	}

	protected abstract void startTest();

	protected static class MyHandler extends Handler {
		private WeakReference<VideoPlayTestBase> reference;

		public MyHandler(VideoPlayTestBase test) {
			this.reference = new WeakReference<VideoPlayTestBase>(test);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			resultCallBack(msg);
		}

		// call back
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private void resultCallBack(Message msg) {
			VideoPlayTestBase test = this.reference.get();
			int N = test.mCallbacks.beginBroadcast();
			try {
				for (int i = 0; i < N; i++) {
					switch (msg.what) {
					case EVENT_CHANGE:
						test.mCallbacks.getBroadcastItem(i).OnEventChange(test.repeatTimes + "-" + msg.obj.toString());
						// LogUtil.w(TAG, "StreamTest:" + i + "->" + repeatTimes +"-"+
						// msg.obj.toString());
						break;
					case CHART_CHANGE:
						test.mCallbacks.getBroadcastItem(i).onChartDataChanged((Map) msg.obj);
						break;
					case DATA_CHANGE:
						Map tempMap = (Map) msg.obj;
						tempMap.put(TaskTestObject.stopResultName, test.taskModel.getTaskName());
						test.mCallbacks.getBroadcastItem(i).OnDataChanged(tempMap);
						break;
					case TEST_STOP:
						Map<String, String> resultMap = TaskTestObject.getStopResultMap(test.taskModel);
						resultMap.put(TaskTestObject.stopResultState, (String) msg.obj);
						test.mCallbacks.getBroadcastItem(i).onCallTestStop(resultMap);
						break;
					case REAL_PARA:
						test.mCallbacks.getBroadcastItem(i).onParaChanged(WalkCommonPara.CALL_BACK_VIDEO_PLAY_REAL_PARA,
								(Map) msg.obj);
						break;
					}
				}
			} catch (RemoteException e) {
				LogUtil.e(TAG, "---", e);
			}
			test.mCallbacks.finishBroadcast();
		}
	}

	/**
	 * 获取视频质量
	 *
	 * @return
	 */
	protected int getMediaQuality() {
		int mediaQuality = taskModel.getVideoQuality() + 1;
		if (mediaQuality == 8)
			return 19;
		return mediaQuality;
	}

	/**
	 * 初始化失败
	 *
	 * @param info
	 *          失败信息
	 * @param realTime
	 *          事件时间
	 */
	protected void onInitFailed(FailedInfo info, long realTime) {
		LogUtil.w(TAG, "HTTPVS_INIT_FAILED");
		int reason = info.reason;
		EventBytes.Builder(getBaseContext(), RcuEventCommand.VIDEOPLAY_REQUEST_FAILURE).addInteger(reason)
				.writeToRcu(realTime);
	}

	/**
	 * 初始化失败
	 *
	 * @param info
	 *          失败信息
	 * @param realTime
	 *          事件时间
	 */
	protected void onInitFailed(DMPlayerAPI.YoutubeInitFailedInfo info, long realTime) {
		LogUtil.w(TAG, "HTTPVS_YOUTUBE_INIT_FAILED");
		int reason = info.reason;
		EventBytes.Builder(getBaseContext(), RcuEventCommand.VIDEOPLAY_REQUEST_FAILURE).addInteger(reason)
				.writeToRcu(realTime);
	}

	/**
	 * 播放参数获取
	 *
	 * @param info
	 *          参数
	 * @param realTime
	 *          事件时间
	 */
	protected void onPlayQosArrived(PlayQosInfo info, long realTime) {
		LogUtil.w(TAG, "HTTPVS_PLAY_QOS_ARRIVED");
		this.currentStep = HTTPVS_PLAY_QOS_ARRIVED;
		// 当前事件是旧版youtube的播放控件有的事件，新版本不需要
	}

	/**
	 * 连接失败
	 *
	 * @param realTime
	 *          事件时间
	 */
	protected void onConnectFailed(long realTime) {
		LogUtil.w(TAG, "HTTPVS_CONNECT_FAILED");
		this.currentStep = HTTPVS_CONNECT_FAILED;
		int delay = (realTime > mConnectStartTime) ? (int) (realTime - mConnectStartTime) / 1000 : 0;
		EventBytes.Builder(mContext, RcuEventCommand.VIDEOPLAY_CONNECT_FAILED).addInteger(delay).writeToRcu(realTime);
	}

	/**
	 * 连接开始
	 *
	 * @param realTime
	 *          事件时间
	 */
	protected void onConnectStart(long realTime) {
		LogUtil.w(TAG, "HTTPVS_CONNECT_START");
		this.currentStep = HTTPVS_CONNECT_START;
		mConnectStartTime = realTime;
		EventBytes.Builder(mContext, RcuEventCommand.VIDEOPLAY_CONNECT_START).writeToRcu(mConnectStartTime);
	}

	/**
	 * 连接成功
	 *
	 * @param realTime
	 *          事件时间
	 */
	protected void onConnectSuccess(long realTime) {
		LogUtil.w(TAG, "HTTPVS_CONNECT_SUCCESS");
		this.currentStep = HTTPVS_CONNECT_SUCCESS;
		int delay = (realTime > mConnectStartTime) ? (int) (realTime - mConnectStartTime) / 1000 : 0;
		EventBytes.Builder(mContext, RcuEventCommand.VIDEOPLAY_CONNECT_SUCCESS).addInteger(delay).writeToRcu(realTime);
	}

	/**
	 * DNS解析失败
	 *
	 * @param info
	 *          失败信息
	 * @param realTime
	 *          事件时间
	 */
	protected void onDNSResolveFailed(dnsfail_info info, long realTime) {
		LogUtil.w(TAG, "HTTPVS_DNSRESOLVE_FAILED");
		this.currentStep = HTTPVS_DNSRESOLVE_FAILED;
		EventBytes.Builder(mContext, RcuEventCommand.DNS_LOOKUP_FAILURE).addCharArray(taskModel.getUrl().toCharArray(), 256)
				.addInteger(info.delay).addInteger(info.reason).writeToRcu(realTime);
		showEvent(String.format("Video Play DNS Lookup Failure:Delay %d(ms),%s", info.delay,
				FailReason.getFailReason(info.reason).getResonStr()));
	}

	/**
	 * DNS解析开始
	 *
	 * @param info
	 *          dns信息
	 * @param realTime
	 *          事件时间
	 */
	protected void onDNSResolveStart(dnsstart_info info, long realTime) {
		LogUtil.w(TAG, "HTTPVS_DNSRESOLVE_START");
		this.currentStep = HTTPVS_DNSRESOLVE_START;
		mDNSStartTime = realTime;
		EventBytes.Builder(mContext, RcuEventCommand.DNS_LOOKUP_START).addInteger(RcuEventCommand.NullityRcuValue)
				.addInteger(RcuEventCommand.TEST_TYPE_HTTPVS).addStringBuffer(info.host).writeToRcu(mDNSStartTime);

	}

	/**
	 * DNS解析成功
	 *
	 * @param info
	 *          成功信息
	 * @param realTime
	 *          事件时间
	 */
	protected void onDNSResolveSuccess(dnssucc_info info, long realTime) {
		LogUtil.w(TAG, "HTTPVS_DNSRESOLVE_SUCCESS");
		this.currentStep = HTTPVS_DNSRESOLVE_SUCCESS;
		EventBytes.Builder(mContext, RcuEventCommand.DNS_LOOKUP_SUCCESS).addCharArray(info.host.toCharArray(), 256)
				.addInteger(UtilsMethod.convertIpString2Int(info.ip)).addInteger(info.delay).writeToRcu(realTime);
	}

	/**
	 * 获取kpi报告信息
	 *
	 * @param info
	 *          报告信息
	 * @param realTime
	 *          事件时间
	 */
	protected void onKPIsReport(KpiInfo info, long realTime) {
		try {
			if (info != null) {
				LogUtil.w(TAG, "HTTPVS_KPIS_REPORT");
				this.currentStep = HTTPVS_KPIS_REPORT;
				int delay = (int) (realTime - playStartTime) / 1000;
				callBackValues = new HashMap<String, Object>();
				callBackValues.put(ConstItems.VIDEO_TYPE, WalkCommonPara.CALL_BACK_VIDEO_PLAY_REAL_PARA);
				String strQos = "";
				if (info.duration_ms / 1000 > 1 && info.duration_ms / 1000 < 60) {
					strQos = info.duration_ms / 1000 + "s";
				} else if (info.duration_ms / 1000 > 60 && info.duration_ms / 1000 < 60 * 60) {
					strQos = info.duration_ms / (1000 * 60) + "m";
				} else {
					strQos = info.duration_ms + "ms";
				}

				String mediaQuality = "MediaQuality:";
				switch (info.media_quality) {
				case 1:
					callBackValues.put(ConstItems.MEDIA_QUALITY, "normal");
					mediaQuality += "normal";
					break;
				case 2:
					callBackValues.put(ConstItems.MEDIA_QUALITY, "high");
					mediaQuality += "high";
					break;
				case 3:
					callBackValues.put(ConstItems.MEDIA_QUALITY, "super");
					mediaQuality += "super";
					break;
				case 4:
					callBackValues.put(ConstItems.MEDIA_QUALITY, "720P");
					mediaQuality += "720P";
					break;
				case 5:
					callBackValues.put(ConstItems.MEDIA_QUALITY, "1080P");
					mediaQuality += "1080P";
					break;
				case 6:
					callBackValues.put(ConstItems.MEDIA_QUALITY, "2K");
					mediaQuality += "2K";
					break;
				case 7:
					callBackValues.put(ConstItems.MEDIA_QUALITY, "4K");
					mediaQuality += "4K";
					break;
				case 19:
//					callBackValues.put(ConstItems.MEDIA_QUALITY, "auto-"+info.media_quality+"P");
//					mediaQuality += "auto-"+info.media_quality+"P";

					callBackValues.put(ConstItems.MEDIA_QUALITY, "auto");
					mediaQuality += "auto";
					break;
				}

				callBackValues.put(ConstItems.TOTAL_BIT_RATE, info.total_bitrate);
				callBackValues.put(ConstItems.VIDEO_FPS, info.video_fps);
				callBackValues.put(ConstItems.VIDEO_WITH, info.video_width);
				callBackValues.put(ConstItems.VIDEO_HEIGHT, info.video_height);
				callBackValues.put(ConstItems.DURATION_TIME, info.duration_ms);
				callBackValues.put(ConstItems.PLAY_DURATION, info.PlayDuration);
				callBackValues.put(ConstItems.AUDIO_CODEC, info.audio_codec);
				callBackValues.put(ConstItems.VIDEO_CODEC, info.video_codec);
				callBackValues.put(ConstItems.QUALITY_SCORE, String.format("%.2f", info.Quality_Score));
				callBackValues.put(ConstItems.LOADING_SCORE, String.format("%.2f", info.Loading_Score));
				callBackValues.put(ConstItems.STALLING_SCORE, String.format("%.2f", info.Stalling_Score));
				callBackValues.put(ConstItems.VMOS, String.format("%.2f", info.Mobile_vMos));
				callBackValues.put(ConstItems.INIT_BUFFER_LATENCY, info.InitBuffLatency);
				callBackValues.put(ConstItems.VIDEO_SERVER_IP, info.video_server_ip);
				callBackValues.put(ConstItems.VIDEO_SERVER_LOC, info.video_server_loc);
				callBackValues.put(ConstItems.VIDEO_TITLE, info.video_title);
				callBackValues.put(ConstItems.WEBSITE_TYPE, WebSiteTypeUtil.getTypeName(taskModel.getUrl()));
				callBackValues.put(ConstItems.WHOLE_PHASE_DURATION, info.WholePhaseDuration);
				callBackValues.put(ConstItems.WHOLE_PHASE_MAX_RATE, info.WholePhaseMaxRate);
				float stallingRatio = (float) info.ReBufferDuration / (float) (info.ReBufferDuration + info.PlayDuration);
				callBackValues.put(ConstItems.STALLING_RATIO, String.format("%.2f", stallingRatio));
				mHandler.sendMessage(mHandler.obtainMessage(REAL_PARA, callBackValues));
				strQos = String.format(DataTaskEvent.VIDEO_PLAY_KPI_REPORT.toString(), delay, strQos, info.video_width,
						info.video_height, info.video_fps, (float) info.total_bitrate / 1000, mediaQuality);
				displayEvent(strQos);
				EventBytes.Builder(mContext, RcuEventCommand.VIDEOPLAY_KPIS_REPORT).addSingle(info.Mobile_vMos)
						.addSingle(info.Quality_Score).addSingle(info.Loading_Score).addSingle(info.Stalling_Score)
						.addInteger(info.UrlParseLatency).addInteger(info.InitBuffLatency).addInteger(info.InitBuffRecvBytes)
						.addDouble(info.InitBuffAvgRate).addInteger(info.PlayDuration).addInteger(info.PlayPhaseRecvBytes)
						.addInteger(info.ReBufferCount).addInteger(info.ReBufferDuration).addInteger(info.MaxSingleRebuffLatency)
						.addInteger(info.WholePhaseRecvBytes).addInteger(info.WholePhaseDuration).addInteger(info.RTT)
						.addStringBuffer(info.video_server_ip).addStringBuffer(info.video_server_loc).addInteger(info.duration_ms)
						.addCharArray((info.video_codec == null ? "" : info.video_codec).toCharArray(), 12)
						.addCharArray((info.audio_codec == null ? "" : info.audio_codec).toCharArray(), 12)
						.addInteger(info.video_width).addInteger(info.video_height).addInteger(info.video_fps)
						.addInteger(info.total_bitrate).addInteger(info.media_quality).addStringBuffer(info.video_title)
						.addStringBuffer(info.url).addInteger(info.ping_loss_rate).addInteger(info.ScreenWidth)
						.addInteger(info.ScreenHeight).addInt64(info.video_size).addInt64(info.InitBuffMaxRate)
						.addInt64(info.WholePhaseMaxRate).addInteger(info.firstReachHopIpRTT).addStringBuffer(info.firstReachHopIp)
						.addInteger(info.bitrate_change_count).addStringBuffer(info.bitrate_distribution).addInteger(JitterAvg).writeToRcu(realTime);
				if (ApplicationModel.getInstance().isBeiJingTest()) {
					Map<String, Integer> totalMap = new HashMap<String, Integer>();
					totalMap.put(TotalStruct.TotalVideoPlay._videoReBuffers.name(), info.ReBufferCount);
					// if (info.ReBufferDuration > 0) {
					totalMap.put(TotalStruct.TotalVideoPlay._videoReBufferSuccess.name(), info.ReBufferCount);
					totalMap.put(TotalStruct.TotalVideoPlay._videoRebufferTime.name(), info.ReBufferDuration);
					// }
					mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
				}
			}

		} catch (Exception e) {
			LogUtil.w(TAG, "onKPIsReport", e);
		}
	}

	/**
	 * 发送接收
	 *
	 * @param info
	 *          接收信息
	 * @param realTime
	 *          事件时间
	 */
	protected void onSendGet(send_get_info info, long realTime) {
		LogUtil.w(TAG, "HTTPVS_SEND_GET");
		this.currentStep = HTTPVS_SEND_GET;
		EventBytes.Builder(mContext, RcuEventCommand.VIDEOPLAY_SEND_GET_CMD).writeToRcu(realTime);
		Map<String, Integer> totalMap = new HashMap<String, Integer>();
		totalMap.put(TotalStruct.TotalVideoPlay._videoTrys.name(), 1);
		totalMap.put(TotalStruct.TotalVideoPlay._vpTotalBytes.name(), 0);
		totalMap.put(TotalStruct.TotalVideoPlay._vpTotalSample.name(), 0);
		totalMap.put(TotalStruct.TotalVideoPlay._videoAV_DeSync.name(), 0);
		totalMap.put(TotalStruct.TotalVideoPlay._videoAv_Vmos.name(), 0);
		totalMap.put(TotalStruct.TotalVideoPlay._videoSuccs.name(), 0);
		totalMap.put(TotalStruct.TotalVideoPlay._videoConnectTime.name(), 0);
		totalMap.put(TotalStruct.TotalVideoPlay._videoReproductionStart.name(), 0);
		totalMap.put(TotalStruct.TotalVideoPlay._videoReproductionDaily.name(), 0);
		totalMap.put(TotalStruct.TotalVideoPlay._videoPlayEnd.name(), 0);
		totalMap.put(TotalStruct.TotalVideoPlay._videoDrop.name(), 0);
		totalMap.put(TotalStruct.TotalVideoPlay._videoReBuffers.name(), 0);
		totalMap.put(TotalStruct.TotalVideoPlay._videoReBufferSuccess.name(), 0);
		totalMap.put(TotalStruct.TotalVideoPlay._videoReBufferFail.name(), 0);
		totalMap.put(TotalStruct.TotalVideoPlay._videoRebufferTime.name(), 0);
		mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	}

	/**
	 * 发送接收失败
	 *
	 * @param info
	 *          失败信息
	 * @param realTime
	 *          事件时间
	 */
	protected void onSendGetFailed(FailedInfo info, long realTime) {
		LogUtil.w(TAG, "HTTPVS_SEND_GET_FAILED");
		EventBytes.Builder(mContext, RcuEventCommand.VIDEOPLAY_SEND_GET_CMD_FAILED).addInteger(info.reason)
				.addStringBuffer(info.desc).addInteger(info.http_code).writeToRcu(realTime);
	}

	/**
	 * url解析失败
	 *
	 * @param info
	 *          失败信息
	 * @param realTime
	 *          事件时间
	 */
	protected void onUrlParseFailed(FailedInfo info, long realTime) {
		LogUtil.w(TAG, "HTTPVS_URLPARSE_FAILED");
		this.currentStep = HTTPVS_URLPARSE_FAILED;
		int urlFailReason = info.reason;
		EventBytes.Builder(getBaseContext(), RcuEventCommand.VIDEOPLAY_REQUEST_FAILURE).addInteger(urlFailReason)
				.writeToRcu(realTime);
	}

	/**
	 * url解析开始
	 *
	 * @param realTime
	 *          事件时间
	 */
	protected void onUrlParseStart(long realTime) {
		LogUtil.w(TAG, "HTTPVS_URLPARSE_START");
		this.currentStep = HTTPVS_URLPARSE_START;
	}

	/**
	 * url解析成功
	 *
	 * @param realTime
	 *          事件时间
	 */
	protected void onUrlParseSuccess(long realTime) {
		LogUtil.w(TAG, "HTTPVS_URLPARSE_SUCCESS");
		this.currentStep = HTTPVS_URLPARSE_SUCCESS;
		EventBytes.Builder(getBaseContext(), RcuEventCommand.VIDEOPLAY_URLPARSE_SUCCESS).writeToRcu(realTime);
	}

	/**
	 * 初始化成功
	 *
	 * @param realTime
	 *          事件时间
	 */
	protected String onInited(long realTime) {
		LogUtil.w(TAG, "HTTPVS_INITED");
		this.currentStep = HTTPVS_INITED;
		this.mFirstDataTime = 0;
		this.mLastDataTime = 0;
		this.mConnectStartTime = 0;
		this.mDNSStartTime = 0;
		StringBuffer eventData = new StringBuffer("local_if::");
		eventData.append(getNetInterface()).append("\n");
		eventData.append("ps_call::").append(taskModel.getPlayType()).append("\n");
		eventData.append("parent_process_id::0\n");
		eventData.append("url::").append(taskModel.getUrl()).append("\n");
		eventData.append("media_quality::").append(this.getMediaQuality()).append("\n");
		// 业务时长,默认为0
		eventData.append("playtime_ms::")
				.append(taskModel.getPlayType() == TaskVideoPlayModel.PLAY_TYPE_TIME ? 0 : taskModel.getPlayTimeout() * 1000)
				.append("\n");
		// 默认连接超时为60s
		eventData.append("connect_timeout_ms::").append(30000).append("\n");
		eventData.append("reconnect_counts::3\n");// 重复连接次数，默认为0
		eventData.append("nodata_timeout_ms::").append(taskModel.getNoDataTimeout() * 1000).append("\n");
		// 播放时长,如果按文件，播放时长则没有限制
		eventData.append("media_play_time_ms::")
				.append(taskModel.getPlayType() == TaskVideoPlayModel.PLAY_TYPE_FILE ? 0 : taskModel.getPlayTimeout() * 1000)
				.append("\n");
		// 播放播百份比,
		eventData.append("media_play_percent::").append(taskModel.getPlayType() == TaskVideoPlayModel.PLAY_TYPE_FILE ? 0
				: taskModel.getPlayTimerMode() == 0 ? 0 : taskModel.getPlayTimeout()).append("\n");
		// 播放模式,是否按比例播放
		eventData.append("use_media_play_percent::").append(taskModel.getPlayType() == 0 ? 0 : taskModel.getPlayTimerMode())
				.append("\n");
		// 缓冲总时长
		eventData.append("buffering_time_ms::").append(taskModel.getPlayType() == TaskVideoPlayModel.PLAY_TYPE_FILE ? 0
				: taskModel.getBufTimerMode() == 0 ? taskModel.getMaxBufferTimeout() * 1000 : 0).append("\n");
		// 最大缓冲比例
		eventData.append("buffering_percent::").append(taskModel.getPlayType() == TaskVideoPlayModel.PLAY_TYPE_FILE ? 0
				: taskModel.getBufTimerMode() == 0 ? 0 : taskModel.getMaxBufferPercentage()).append("\n");
		// 缓冲计时方式
		eventData.append("use_buffering_percent::")
				.append(taskModel.getPlayType() == TaskVideoPlayModel.PLAY_TYPE_FILE ? 0 : taskModel.getBufTimerMode())
				.append("\n");
		// 最大缓冲次数
		eventData.append("buffering_counts::")
				.append(taskModel.getPlayType() == TaskVideoPlayModel.PLAY_TYPE_FILE ? 0 : taskModel.getMaxBufCounts())
				.append("\n");
		// 最大缓冲时长
		eventData.append("buffer_max_time_ms::").append(taskModel.getBufTime() * 1000).append("\n");
		eventData.append("prebuffer_time_ms::").append(taskModel.getBufThred() * 1000).append("\n");// 初始缓冲播放门限
		eventData.append("rebuffer_time_ms::").append(taskModel.getBufThred() * 1000).append("\n");// 重缓冲播放门限
		eventData.append("save_media::").append(taskModel.isSave() ? "1" : "0").append("\n");// 是否保存视频
		eventData.append("save_path::").append(AppFilePathUtil.getInstance().getSDCardBaseDirectory(getString(R.string.path_video)));
		LogUtil.w(TAG, eventData.toString());
		displayEvent(DataTaskEvent.VIDEO_PLAY_REQUEST.toString());
		EventBytes.Builder(VideoPlayTestBase.this, RcuEventCommand.VIDEOPLAY_REQUEST)
				.addInteger(WebSiteTypeUtil.GetType(taskModel.getUrl())).addStringBuffer(taskModel.getUrl())
				.writeToRcu(realTime);
		return eventData.toString();
	}

	/**
	 * 获取到第一次数据
	 *
	 * @param realTime
	 *          事件时间
	 */
	protected void onFirstDataArrived(long realTime) {
		LogUtil.w(TAG, "HTTPVS_FIRSTDATA_ARRIVED");
		this.currentStep = HTTPVS_FIRSTDATA_ARRIVED;
		int delay = (int) (realTime - this.mConnectStartTime) / 1000;
		playStartTime = realTime;
		mFirstDataTime = realTime;
		String strQos = String.format(DataTaskEvent.VIDEO_PLAY_FIRST_DATA.toString(), delay);
		displayEvent(strQos);
		EventBytes.Builder(VideoPlayTestBase.this, RcuEventCommand.VIDEOPLAY_FIRST_DATA).writeToRcu(realTime);

		Map<String, Integer> totalMap = new HashMap<String, Integer>();
		// 事件节点约计
		totalMap.put(TotalStruct.TotalVideoPlay._videoSuccs.name(), 1);// 接入成功次数
		totalMap.put(TotalStruct.TotalVideoPlay._videoConnectTime.name(), delay);// 接入花费时长
		mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	}

	/**
	 * 复制开始
	 *
	 * @param info
	 *          数据对象
	 * @param realTime
	 *          事件时间
	 */
	protected void onReproductionStart(ReproductionStartInfo info, long realTime) {
		LogUtil.w(TAG, "HTTPVS_REPRODUCTION_START");
		this.currentStep = HTTPVS_REPRODUCTION_START;
		int delay = (int) (realTime - playStartTime) / 1000;
		String strQos = String.format(DataTaskEvent.VIDEO_PLAY_REPRODUCTION_START.toString(), delay);
		displayEvent(strQos);

		EventBytes.Builder(getBaseContext(), RcuEventCommand.VIDEOPLAY_REPRODUCTION_START).addInt64(info.RecvTotalBytes)
				.addInteger(info.reproductionDelay).addInteger(info.initBufferLatency).writeToRcu(realTime);

		Map<String, Integer> totalMap = new HashMap<String, Integer>();
		// 开始复制次数
		totalMap.put(TotalStruct.TotalVideoPlay._videoReproductionStart.name(), 1);
		// 复制开始时延
		totalMap.put(TotalStruct.TotalVideoPlay._videoReproductionDaily.name(), delay);
		mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	}

	/**
	 * 复制开始失败
	 *
	 * @param info
	 *          失败原因
	 * @param realTime
	 *          事件时间
	 */
	protected void onReproductionStartFailed(ReproductionStartFailedInfo info, long realTime) {
		LogUtil.w(TAG, "HTTPVS_REPRODUCTION_START_FAILED");
		this.currentStep = HTTPVS_REPRODUCTION_START_FAILED;
		int delay = (int) (realTime - playStartTime) / 1000;
		analyseData(info, HTTPVS_REPRODUCTION_START_FAILED);
		String strQos = String.format(DataTaskEvent.VIDEO_PLAY_REPRODUCTION_START_FAILURE.toString(), delay,
				FailReason.getFailReason(ItemValues.reason).getResonStr());
		displayEvent(strQos);
		EventBytes.Builder(getBaseContext(), RcuEventCommand.VIDEOPLAY_REPRODUCTION_START_FAILURE)
				.addInteger(ItemValues.reason).writeToRcu(realTime);

		Map<String, Integer> totalMap = new HashMap<String, Integer>();
		// 开始复制失败次数
		totalMap.put(TotalStruct.TotalVideoPlay._videoReproductionFailure.name(), 1);
		mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
		sendMsgToPioneer(",bSuccess=0");
	}

	/**
	 * 参数获取
	 *
	 * @param info
	 *          参数对象
	 * @param realTime
	 *          事件时间
	 */
	protected void onQosArrived(QosInfo info, long realTime) {
		LogUtil.w(TAG, "HTTPVS_QOS_ARRIVED");
		this.currentStep = HTTPVS_QOS_ARRIVED;
		analyseData(info, HTTPVS_QOS_ARRIVED);
		totalQos();

		callBackValues = new HashMap<String, Object>();
		callBackValues.put(ConstItems.VIDEO_TYPE, WalkCommonPara.CALL_BACK_VIDEO_PLAY_REAL_PARA);
		callBackValues.put(ConstItems.IS_SHOW_VIDEO, taskModel.isVideoShow());
		callBackValues.put(ConstItems.TOTAL_RECV_SIZE, ItemValues.RecvTotalBytes / UtilsMethod.kbyteRage);
		callBackValues.put(ConstItems.DOWN_PROCESS, ItemValues.DownloadProgress);
		callBackValues.put(ConstItems.REBUF_COUNTS, ItemValues.ReBufferTimes);
		callBackValues.put(ConstItems.REBUF_TIMES, ItemValues.ReBufferTimeMS);
		callBackValues.put(ConstItems.VMOS, ItemValues.DVSNR_VMOS);
		callBackValues.put(ConstItems.CUR_RECV_SPEED, ItemValues.CurRecvSpeed);
		callBackValues.put(ConstItems.CUR_MEDIA_QUALITY, ItemValues.CurMediaQuality);
		if (ItemValues.DVSNR_VMOS != -9999)
			vmosSet.add(ItemValues.DVSNR_VMOS);
		callBackValues.put(ConstItems.AV_SYNC, ItemValues.AV_DeSync == 1 ? "out-sync" : "sync");
		callBackValues.put(ConstItems.MEAN_DL_RATE, ItemValues.RecvTotalBytes * 1f / (ItemValues.MeasureTime * 1000));
		mHandler.sendMessage(mHandler.obtainMessage(REAL_PARA, callBackValues));
		this.writeQos();
	}

	/**
	 * 接收结束
	 *
	 * @param info
	 *          参数对象
	 * @param realTime
	 *          事件时间
	 */
	protected void onRecvFinish(RecvFinishInfo info, long realTime) {
		LogUtil.w(TAG, "HTTPVS_RECV_FINISH");
		this.currentStep = HTTPVS_RECV_FINISH;
		analyseData(info, HTTPVS_RECV_FINISH);
		this.mLastDataTime = realTime;
		String strQos = String.format(DataTaskEvent.VIDEO_PLAY_LAST_DATA.toString(),
				ItemValues.RecvTotalBytes / UtilsMethod.kbyteRage, ItemValues.ReBufferTimes, ItemValues.ReBufferTimeMS,
				ItemValues.VSNR_VMOS, ItemValues.AV_DeSync_Rate);
		displayEvent(strQos);

		EventBytes.Builder(getBaseContext(), RcuEventCommand.VIDEOPLAY_LAST_DATA).addInteger(ItemValues.RecvTotalBytes)
				.addInteger(ItemValues.ReBufferTimes).addInteger(ItemValues.ReBufferTimeMS).addSingle(ItemValues.VSNR_VMOS)
				.addInteger(ItemValues.AV_DeSync_Rate).addStringBuffer(ItemValues.VideoSaveFile)
				.addInteger(mFirstDataTime == 0 ? 0 : (int) (realTime - mFirstDataTime) / 1000).writeToRcu(realTime);

		Map<String, Integer> totalMap = new HashMap<String, Integer>();
		// 播放完成次数+1
		totalMap.put(TotalStruct.TotalVideoPlay._videoPlayEnd.name(), 1);
		// 平均VMOS
		mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
		sendMsgToPioneer(",bSuccess=1");
	}

	/**
	 * 接收失败
	 *
	 * @param info
	 * @param realTime
	 *          事件时间
	 */
	protected void onRecvDrop(RecvDropInfo info, long realTime) {
		LogUtil.w(TAG, "HTTPVS_RECV_DROP");
		this.currentStep = HTTPVS_RECV_DROP;
		String strQos = " reason:" + info.reason + "\r\n";
		analyseData(info, HTTPVS_RECV_DROP);

		strQos = String.format(DataTaskEvent.VIDEO_PLAY_DROP.toString(), ItemValues.RecvTotalBytes / UtilsMethod.kbyteRage,
				ItemValues.ReBufferTimes, ItemValues.ReBufferTimeMS, ItemValues.VSNR_VMOS, ItemValues.AV_DeSync_Rate,
				RcuEventCommand.DropReason.getDropReason(ItemValues.reason).getResonStr());
		displayEvent(strQos);
		EventBytes.Builder(getBaseContext(), RcuEventCommand.VIDEOPLAY_DROP).addInteger(ItemValues.RecvTotalBytes)
				.addInteger(ItemValues.ReBufferTimes).addInteger(ItemValues.ReBufferTimeMS).addSingle(ItemValues.VSNR_VMOS)
				.addInteger(ItemValues.AV_DeSync_Rate).addStringBuffer(ItemValues.VideoSaveFile).addInteger(ItemValues.reason)
				.addInteger(mFirstDataTime == 0 ? 0 : (int) (realTime - mFirstDataTime) / 1000).writeToRcu(realTime);

		Map<String, Integer> totalMap = new HashMap<String, Integer>();
		// 如果Drop时是User stop或Time out算一次
		if (ItemValues.reason == DropReason.TIMEOUT.getReasonCode()
				|| ItemValues.reason == DropReason.USER_STOP.getReasonCode()) {
			// 播放完成次数+1
			totalMap.put(TotalStruct.TotalVideoPlay._videoPlayEnd.name(), 1);
			sendMsgToPioneer(",bSuccess=1");
		} else {
			// 播放掉线次数+1
			totalMap.put(TotalStruct.TotalVideoPlay._videoDrop.name(), 1);
			sendMsgToPioneer(",bSuccess=0");
		}

		// 如果出现这4个原因码，记重缓冲失败
		if (ItemValues.reason == 4 || ItemValues.reason == DropReason.PPP_DROP.getReasonCode()
				|| ItemValues.reason == DropReason.NO_DATA.getReasonCode()
				|| ItemValues.reason == DropReason.OutOfService.getReasonCode()) {
			totalMap.put(TotalStruct.TotalVideoPlay._videoReBufferFail.name(), 1);
		}

		mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	}

	/**
	 * 重缓存开始
	 *
	 * @param realTime
	 *          事件时间
	 */
	protected void onReBufferingStart(long realTime) {
		LogUtil.w(TAG, "HTTPVS_REBUFFERING_START");
		this.currentStep = HTTPVS_REBUFFERING_START;
		playStartTime = realTime;
		displayEvent(DataTaskEvent.VIDEO_PLAY_REBUFFERING_START.toString());
		EventBytes.Builder(mContext, RcuEventCommand.VIDEOPLAY_REBUFFERING_START).writeToRcu(realTime);

		Map<String, Integer> totalMap = new HashMap<String, Integer>();
		totalMap.put(TotalStruct.TotalVideoPlay._videoReBuffers.name(), 1);
		mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	}

	/**
	 * 重缓存结束
	 *
	 * @param realTime
	 *          事件时间
	 */
	protected void onReBufferingEnd(long realTime) {
		LogUtil.w(TAG, "HTTPVS_REBUFFERING_END");
		this.currentStep = HTTPVS_REBUFFERING_END;
		int delay = (int) (realTime - playStartTime) / 1000;
		String strQos = String.format(DataTaskEvent.VIDEO_PLAY_REBUFFERING_END.toString(), delay);
		displayEvent(strQos);
		EventBytes.Builder(getBaseContext(), RcuEventCommand.VIDEOPLAY_REBUFFERING_END).writeToRcu(realTime);
		Map<String, Integer> totalMap = new HashMap<String, Integer>();
		totalMap.put(TotalStruct.TotalVideoPlay._videoReBufferSuccess.name(), 1);
		totalMap.put(TotalStruct.TotalVideoPlay._videoRebufferTime.name(), delay);
		mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	}

	/**
	 * 播放结束
	 *
	 * @param realTime
	 *          事件时间
	 */
	protected void onPlayFinish(long realTime) {
		LogUtil.w(TAG, "HTTPVS_PLAY_FINISH");
		this.isFinish = true;
		this.currentStep = HTTPVS_PLAY_FINISH;
		displayEvent(DataTaskEvent.VIDEO_PLAY_PLAY_FINISHED.toString());
		EventBytes.Builder(getBaseContext(), RcuEventCommand.VIDEOPLAY_PLAY_FINISHED).writeToRcu(realTime);
		sendMsgToPioneer(",bSuccess=1");
	}

	/**
	 * 视频分片的相关参数
	 *
	 * @param info
	 *          参数信息
	 * @param realTime
	 *          事件时间
	 */
	protected void onSegmentReport(SegInfo info, long realTime) {
		LogUtil.w(TAG, "HTTPVS_SEGMENT_REPORT");
		this.currentStep = HTTPVS_SEGMENT_REPORT;
		displayEvent(DataTaskEvent.VIDEO_PLAY_SEGMENT_REPORT.toString());
		EventBytes.Builder(getBaseContext(), RcuEventCommand.VIDEOPLAY_SEGMENT_REPORT).addSingle(info.Mobile_vMos)
				.addSingle(info.Quality_Score).addSingle(info.Loading_Score).addSingle(info.Stalling_Score)
				.addInteger(info.seg_duration_ms).addInteger(info.seg_bytes).addInteger(info.video_width)
				.addInteger(info.video_height).addInteger(info.video_fps).addInteger(info.seg_bitrate)
				.addInteger(info.seg_quality).addInteger(info.play_duration_ms).addInteger(info.seg_prebuff_time_ms)
				.addInteger(info.seg_rebuff_time_ms).addInteger(info.seg_rebuff_count).writeToRcu(realTime);
	}

	/**
	 * 业务测试结束
	 */
	public void onQuit() {
		LogUtil.w(TAG, "HTTPVS_QUIT");
		this.currentStep = HTTPVS_QUIT;
		mHandler.obtainMessage(TEST_STOP, (mFirstDataTime != 0) ? "1" : "0").sendToTarget();
	}

	/**
	 * 信息接收
	 *
	 * @param info
	 *          信息对象
	 * @param realTime
	 *          事件时间
	 */
	protected void onMsg(MsgInfo info, long realTime) {
		LogUtil.w(TAG, "HTTPVS_MSG");
		this.currentStep = HTTPVS_MSG;
		String strQos = " level:" + info.level + "\r\n";
		strQos += " code:" + info.code + "\r\n";
		strQos += " msg:" + info.msg + "\r\n";
		LogUtil.w(tag, "--HTTPVS_MSG:" + strQos);
		try {
			EventBytes.Builder(mContext, RcuEventCommand.DataServiceMsg).addInteger(info.type).addInteger(info.level)
					.addInteger(info.code).addStringBuffer(info.msg).addStringBuffer(info.context).writeToRcu(realTime);
		} catch (Exception e) {
			LogUtil.w(tag, "HTTPVS_MSG", e);
		}
	}

	/**
	 * 分析数据
	 *
	 * @param infoObj
	 *          信息对象
	 * @param step
	 *          步骤
	 */
	protected void analyseData(Object infoObj, int step) {
		switch (step) {
		case HTTPVS_QOS_ARRIVED:
			QosInfo info1 = (QosInfo) infoObj;
			ItemValues.CurRecvSpeed = info1.CurRecvSpeed;
			ItemValues.MeasureTime = info1.MeasureTime;
			ItemValues.RecvTotalBytes = info1.RecvTotalBytes;
			ItemValues.DownloadProgress = info1.DownloadProgress;
			ItemValues.ReBufferTimes = info1.ReBufferTimes;
			ItemValues.ReBufferTimeMS = info1.RebufferTimeMS;
			ItemValues.DVSNR_VMOS = info1.DVSNR_VMOS;
			ItemValues.AV_DeSync = info1.AV_DeSync;
			ItemValues.CurFps = info1.CurFps;
			ItemValues.BufferRatio = info1.BufferRatio;
			ItemValues.CurMediaQuality=info1.CurMediaQuality;
			break;
		case HTTPVS_RECV_DROP:
			RecvDropInfo info3 = (RecvDropInfo) infoObj;
			ItemValues.VSNR_VMOS = info3.VSNR_VMOS;
			ItemValues.AV_DeSync_Rate = info3.AV_DeSync_Rate;
			ItemValues.VideoSaveFile = info3.VideoSaveFile == null ? "" : info3.VideoSaveFile;
			ItemValues.reason = info3.reason;
			break;
		case HTTPVS_RECV_FINISH:
			RecvFinishInfo info4 = (RecvFinishInfo) infoObj;
			ItemValues.VSNR_VMOS = info4.VSNR_VMOS;
			ItemValues.AV_DeSync_Rate = info4.AV_DeSync_Rate;
			ItemValues.VideoSaveFile = info4.VideoSaveFile == null ? "" : info4.VideoSaveFile;
			break;
		case HTTPVS_REPRODUCTION_START_FAILED:
			ReproductionStartFailedInfo info5 = (ReproductionStartFailedInfo) infoObj;
			ItemValues.reason = info5.reason;
			break;
		case HTTPVS_REQUEST_FAILED:
			FailedInfo info6 = (FailedInfo) infoObj;
			ItemValues.reason = info6.reason;
			break;
		}
	}

	private int lastRectTotalByte = 0;// 最后接收的字节数
	private int lastMeasureTime = 0;

	/**
	 * 分析视频质量
	 */
	private void statisticsQos() {
		if (mFirstDataTime != 0 && ItemValues.MeasureTime != 0) {
			sendMsgToPioneer(String.format(tmpPionner, ItemValues.MeasureTime, ItemValues.RecvTotalBytes * 8 / 1000,
					ItemValues.RecvTotalBytes * 8 / (1000 * ItemValues.MeasureTime / 1000)));

		}
		turnTransfersRate();
	}

	/**
	 * 统计视频质量
	 */
	protected void totalQos() {
		Map<String, Integer> totalMap = new HashMap<String, Integer>();
		// 接收到Qos总次数
		totalMap.put(TotalStruct.TotalVideoPlay._vpTotalSample.name(), 1);
		totalMap.put(TotalStruct.TotalVideoPlay._videoAv_Vmos.name(), (int) (getAvgVMOSValue() * 100));
		// 接收总数据大小
		totalMap.put(TotalStruct.TotalVideoPlay._vpTotalBytes.name(), ItemValues.RecvTotalBytes - lastRectTotalByte);
		lastRectTotalByte = ItemValues.RecvTotalBytes;
		totalMap.put(TotalStruct.TotalVideoPlay._vpTotalTime.name(), ItemValues.MeasureTime - lastMeasureTime);
		lastMeasureTime = ItemValues.MeasureTime;

		totalMap.put(TotalStruct.TotalVideoPlay._vpCurRecvSpeed.name(), ItemValues.CurRecvSpeed);
		totalMap.put(TotalStruct.TotalVideoPlay._vpCurRecvSpeedTimes.name(), 1);
		// 平均速率
		// totalMap.put(TotalStruct.VideoPlay._videoAvgRecvSpeedKbps.name(),
		// ((int)(ItemValues.RecvTotalBytes*1000f/ItemValues.MeasureTime)));
		// 平均A-V不同步
		totalMap.put(TotalStruct.TotalVideoPlay._videoAV_DeSync.name(), ItemValues.AV_DeSync == 0 ? 1 : 0);
		mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
	}

	private String tmpPionner = ",DurationTime=%d,TransmitSize=%d,InstRate=%d";
	private float kByte = 1000f;
	private long totalTransSize = 0;
	private float meanRate = 0f; // 速率
	private float averageRage = 0f; // 平均速度
	private int prevMeasureTime = 0;

	/** 统计当前传输大小，时长，瞬时速率，平均速率，最大速率等相关信息 */
	private void turnTransfersRate() {
		if (ItemValues.MeasureTime != 0) {
			// 计算当前每秒传输速度
			// 计算前将当前获得的总大小赋给临时总大小变量，以防计算过程中当前次总大小发生变化
			int interBytes = (int) (ItemValues.RecvTotalBytes - totalTransSize); // 间隔获得数据包大小
			totalTransSize = ItemValues.RecvTotalBytes;
			Map<String, Object> dataMap = new HashMap<String, Object>();

			// 当前平均值
			averageRage = (ItemValues.RecvTotalBytes * 8 / (ItemValues.MeasureTime / 1000f)) / kByte;
			dataMap.put(DataTaskValue.BordLeftTitle.name(), getString(R.string.info_data_averagerate) + "@"
					+ UtilsMethod.decFormat.format(averageRage) + getString(R.string.info_rate_kbps));
			meanRate = (interBytes * 8 / ((ItemValues.MeasureTime - prevMeasureTime) / 1000f)) / kByte;
			prevMeasureTime = ItemValues.MeasureTime;
			dataMap.put(DataTaskValue.BordCurrentSpeed.name(), UtilsMethod.decFormat.format(meanRate));
			dataMap.put(DataTaskValue.BordProgress.name(), ItemValues.DownloadProgress);
			Message msg = mHandler.obtainMessage(DATA_CHANGE, dataMap);
			mHandler.sendMessage(msg);
		}
	}

	/**
	 * 获取动态的VMOS值得平均值
	 *
	 * @return
	 */
	private float getAvgVMOSValue() {
		float total = 0;
		int times = 0;
		for (float value : vmosSet) {
			if (value != -9999) {
				total += value;
				times++;
			}
		}
		float avg = 0;
		if (!vmosSet.isEmpty() && times != 0) {
			avg = total / times;
		}

		return avg;
	}

	/**
	 * 显示事件
	 */
	protected void displayEvent(String event) {
		// LogUtil.w(TAG, "displayEvent:" + event);
		Message msg = mHandler.obtainMessage(EVENT_CHANGE, event);
		msg.sendToTarget();
	}

	public void onDestroy() {
		LogUtil.v(TAG, "--onDestroy--");
		super.onDestroy();
		mHandler.removeMessages(0);
		mCallbacks.kill();
	};

	/**
	 * 写QoS
	 */
	private void writeQos() {
		// 如果当前参数传递正确进入计算环节，否则退出消息重发机制
		statisticsQos();

		if (mFirstDataTime != 0 && mLastDataTime == 0) {
			LogUtil.d(TAG,
					"MeasureTime=" + ItemValues.MeasureTime + ";RecvTotalBytes=" + ItemValues.RecvTotalBytes
							+ ";DownloadProgress=" + ItemValues.DownloadProgress + ";ReBufferTimes=" + ItemValues.ReBufferTimes
							+ ";ReBufferTimeMS=" + ItemValues.ReBufferTimeMS + ";DVSNR_VMOS=" + getAvgVMOSValue() + ";AV_DeSync="
							+ ItemValues.AV_DeSync + ";CurRecvSpeed=" + ItemValues.CurRecvSpeed + ";CurFps=" + ItemValues.CurFps
							+ ";BufferRatio=" + ItemValues.BufferRatio
							+ ";CurMediaQuality=" + ItemValues.CurMediaQuality);

			EventBytes.Builder(getBaseContext()).addInteger(WalkCommonPara.VideoPlayQos).addInteger(ItemValues.MeasureTime)
					.addInt64(ItemValues.RecvTotalBytes).addInteger(ItemValues.DownloadProgress)
					.addInteger(ItemValues.ReBufferTimes).addInteger(ItemValues.ReBufferTimeMS).addSingle(getAvgVMOSValue())
					.addInteger(ItemValues.AV_DeSync).addInteger(ItemValues.CurRecvSpeed).addInteger(ItemValues.CurFps)
					.addInteger(ItemValues.BufferRatio).addInteger(ItemValues.Jetter).addInteger(ItemValues.CurMediaQuality).writeToRcu(WalkCommonPara.MsgDataFlag_A);
		}
	}

	protected static class ItemValues {
		static int initBufferLatency;
		static int reproductionDelay;
		static int MeasureTime;
		static int RecvTotalBytes;
		static int DownloadProgress;
		static int ReBufferTimes;
		static int ReBufferTimeMS;
		static float DVSNR_VMOS;
		static float VSNR_VMOS;
		static int AV_DeSync;
		static int reason;
		static int AV_DeSync_Rate; // 音视频不同步概率，已经*100, 百分比
		static String VideoSaveFile = ""; // 视频文件
		static int CurRecvSpeed;
		static int CurFps;
		static int BufferRatio;
		static int Jetter;
		static int CurMediaQuality;//当前视频质量参数
	}
}
