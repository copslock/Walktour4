package com.walktour.control.netsniffer.packetanalysis;

import android.content.Context;

import com.dingli.dmplayer.sdktest.DMPlayerAPI.KpiInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.QosInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.RecvDropInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.RecvFinishInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.ReproductionStartInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.dnsstart_info;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.dnssucc_info;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.send_get_info;
import com.dinglicom.dataset.EventManager;
import com.walktour.Utils.StringUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.packet_dissect_info;
import com.walktour.service.dmplayer.VideoPlayEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 视频播放抓包信息解析类
 * 
 * @author jianchao.wang
 *
 */
public class VideoPlayAnalysis {
	/** 日志标识 */
	private final static String TAG = "VideoPlayAnalysis";
	/** 视频类型：全部 */
	private final static int VIDEO_TYPE_ALL = 0;
	/** 视频类型：优酷 */
	private final static int VIDEO_TYPE_YOUKU = 1;
	/** 视频类型：乐视 */
	private final static int VIDEO_TYPE_LETV = 2;
	/** 唯一实例 */
	private static VideoPlayAnalysis sInstance;
	/** 视频播放事件监听类 */
	private VideoPlayEventListener mListener;
	/** 第一次数据获得时间(微秒) */
	private long mFirstDataTime;
	/** 最后一次数据获得时间 (微秒) */
	private long mLastDataTime;
	/** 连接开始时间(微秒) */
	// private long mConnectStartTime;
	/** 连接成功时间(微秒) */
	// private long mConnectSuccessTime;
	/** DNS标识 */
	private String mDNSFlag;
	/** DNS解析开始时间(微秒) */
	private long mDNSStartTime;
	/** DNS解析成功时间(微秒) */
	private long mDNSSuccessTime;
	/** DNS时延(微秒) */
	private int mDNSDelay;
	/** 发送接收时间(微秒) */
	private long mSendGetTime;
	/** 请求时间(微秒) */
	private long mRequestTime;
	/** 应答开始时间(微秒) */
	private long mReproductionStartTime;
	/** 播放结束时间 (微秒) */
	private long mPlayFinishTime;
	/** KPI报告时间(微秒) */
	private long mKPIReportTime;
	/** 下载进度 */
	private int mDownloadProgress;
	/** 缓冲总时长(微秒) */
	private long mRebufferSum = 0;
	/** 缓冲最大时长 (微秒) */
	private long mRebufferMax = 0;
	/** 缓冲次数 */
	private int mRebufferCount = 0;
	/** 缓冲开始时间 (微秒) */
	private long mRebufferStartTime = 0;
	/** 连接开始源地址 */
	// private String mConnectStartSrcAddr;
	/** 连接开始目标地址 */
	// private String mConnectStartDstAddr;
	/** 发送接收源地址 */
	private String mSendGetSrcAddr;
	/** 发送接收目标地址 */
	private String mSendGetDstAddr;
	/** 生成txt的记录列表 */
	private List<Record> mRecords = new ArrayList<Record>();
	/** 保存当前记录的文件路径 */
	private String mFilePath;
	/** 日期格式 */
	private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.getDefault());
	/** 日期格式 */
	private SimpleDateFormat mFormat1 = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
	/** 上下文 */
	private Context mContext;
	/** 视频应用ID */
	private int mVideoAppID;
	/** 上一次获取流量统计的时间 */
	private long mLastRecvTime;
	/** 上一次获取的流量 */
	private long mLastRecvBytes;
	/** 第一次获取的流量 */
	private long mFirstRecvBytes;
	/** 视频类型 0：全部，1、优酷，2、乐视 */
	private int mVideoType = VIDEO_TYPE_ALL;
	/** 视频质量线程 */
	private QosThread mThread;

	private VideoPlayAnalysis() {
	}

	/**
	 * 返回唯一实例
	 * 
	 * @return
	 */
	public static VideoPlayAnalysis getInstance() {
		if (sInstance == null)
			sInstance = new VideoPlayAnalysis();
		return sInstance;
	}

	/**
	 * 初始化当前值
	 */
	public void init() {
		// this.mConnectStartDstAddr = "";
		// this.mConnectStartSrcAddr = "";
		this.mSendGetDstAddr = "";
		this.mSendGetSrcAddr = "";
		// this.mConnectStartTime = 0;
		// this.mConnectSuccessTime = 0;
		this.mDNSStartTime = 0;
		this.mDNSSuccessTime = 0;
		this.mDNSFlag = "";
		this.mDNSDelay = 0;
		this.mFirstDataTime = 0;
		this.mRequestTime = 0;
		this.mReproductionStartTime = 0;
		this.mLastDataTime = 0;
		this.mSendGetTime = 0;
		this.mPlayFinishTime = 0;
		this.mKPIReportTime = 0;
		this.mVideoType = VIDEO_TYPE_ALL;
		this.mDownloadProgress = 0;
		this.mVideoAppID = 0;
		this.mLastRecvBytes = 0;
		this.mLastRecvTime = 0;
		this.mFirstRecvBytes = 0;
		this.mRebufferCount = 0;
		this.mRebufferMax = 0;
		this.mRebufferStartTime = 0;
		this.mRebufferSum = 0;
		this.mRecords.clear();
		if (this.mThread != null) {
			this.mThread.stopThread();
			this.mThread = null;
		}
	}

	/**
	 * 生成TXT文件
	 * 
	 * @param filePath
	 *          pcap文件路径
	 */
	public void createFile(String filePath) {
		if (!StringUtil.isNullOrEmpty(filePath) && filePath.endsWith(".pcap")) {
			this.mFilePath = filePath.substring(0, filePath.length() - 5) + ".txt";
		}
		if (StringUtil.isNullOrEmpty(this.mFilePath))
			return;
		File file = new File(this.mFilePath);
		if (file.exists())
			return;

		try {
			file.createNewFile();
		} catch (IOException e) {
		}
		BufferedWriter writer = null;
		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(file, true);
			writer = new BufferedWriter(fileWriter);
			writer.write("Time,VideoType,Action");
			writer.newLine();
			writer.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (fileWriter != null) {
					fileWriter.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 初始化视频应用ID
	 * 
	 * @param context
	 */
	private void initVideoAppID(Context context) {
		if (this.mVideoAppID != 0)
			return;
		switch (this.mVideoType) {
		case VIDEO_TYPE_YOUKU:
			this.mVideoAppID = TrafficInfoUtil.getInstance(context).getUid("com.youku.phone");
			break;
		case VIDEO_TYPE_LETV:
			this.mVideoAppID = TrafficInfoUtil.getInstance(context).getUid("com.letv.android.client");
			break;
		}
	}

	/**
	 * 分析TCP/IP包中的信息，生成个事件的广播
	 * 
	 * @param context
	 * @param info
	 */
	public void analysis(Context context, packet_dissect_info info) {
		if (StringUtil.isNullOrEmpty(info.description))
			return;
		this.initVideoAppID(context);
		if (this.mListener == null)
			this.mListener = new VideoPlayEventListener(context);
		this.mContext = context;
		String protecol = info.protocol.toLowerCase(Locale.getDefault());
		if (!protecol.equals("http") && !protecol.equals("dns"))
			return;
		long realTime = this.getRealTime(info);
		LogUtil.d(TAG, "----protecol:" + info.protocol + ";description:" + info.description + ";src_addr:" + info.src_addr
				+ ";dst_addr:" + info.dst_addr + ";time:" + realTime);
		if (this.mVideoType == VIDEO_TYPE_ALL || this.mVideoType == VIDEO_TYPE_LETV)
			this.analyseLeTV(realTime, protecol, info);
		if (this.mVideoType == VIDEO_TYPE_ALL || this.mVideoType == VIDEO_TYPE_YOUKU)
			this.analyseYouku(realTime, protecol, info);
	}

	/**
	 * 分析优酷视频事件
	 * 
	 * @param realTime
	 *          实时时间
	 * @param protecol
	 *          协议类型
	 * @param info
	 *          包数据
	 */
	private void analyseYouku(long realTime, String protecol, packet_dissect_info info) {
		if (protecol.equals("dns")) {
			if (this.mDNSStartTime == 0 && info.description.contains("Standard query")
					&& (info.description.contains("down.api.mobile.youku.com") || info.description.contains("k.youku.com"))) {
				this.mVideoType = VIDEO_TYPE_YOUKU;
				this.onDNSStart(info);
			} else if (this.mDNSSuccessTime == 0 && info.description.contains("Standard query response")
					&& info.description.contains(this.mDNSFlag)) {
				this.onDNSSuccess(info.src_addr, realTime);
			}
		} else if (protecol.equals("http")) {
			// if (this.mRequestTime == 0 &&
			// info.description.contains("/player/getFlvPath/sid/")
			// && (info.description.contains("ev=2") &&
			// info.description.contains("ctype=20"))) {
			// this.mVideoType = VIDEO_TYPE_YOUKU;
			// this.onInited(realTime - 100 * 1000);
			// this.onConnectStart(info);
			// } else if (this.mConnectSuccessTime == 0
			// && (info.description.contains("HTTP/1.1 200 OK") ||
			// info.description.contains("HTTP/1.1 302"))) {
			// this.onConnectSuccess(info);
			// } else
			if (this.mSendGetTime == 0 && ((info.description.contains("/player/getFlvPath/sid/")
					&& (info.description.contains("ev=2") && info.description.contains("ctype=20")))
					|| (info.description.contains("GET /youku/")
							&& (info.description.contains(".mp4") || info.description.contains(".flv"))))) {
				this.mVideoType = VIDEO_TYPE_YOUKU;
				this.onInited(realTime - 100 * 1000);
				// this.mConnectStartTime = realTime - 200 * 1000;
				// this.mConnectSuccessTime = realTime - 100 * 1000;
				this.onSendGet(info);
			} else if (this.mSendGetTime > 0 && this.mFirstDataTime == 0) {
				this.onFirstDataArrived(info);
				if (info.description.contains("type=begin"))
					this.onReproductionStart(info.description, realTime);
			} else if (this.mReproductionStartTime == 0 && info.description.contains("type=begin")) {
				this.onReproductionStart(info.description, realTime);
			} else if (this.mLastDataTime == 0 && info.description.contains("HTTP/1.1")
					&& info.description.contains("video/mp4")) {
				this.onRecvFinish(realTime);
			} else if (this.mPlayFinishTime == 0 && this.mReproductionStartTime > 0
					&& info.description.contains("/openapi-wireless/statis") && info.description.contains("type=end")) {
				Map<String, String> map = this.analysisHttpRequest(info.description);
				if (info.description.contains("complete=0")) {
					if (map.containsKey("play_load_events")) {
						this.onKPISReport(map.get("play_load_events"), realTime);
					}
					this.onRecvDrop(realTime);
				} else {
					if (this.mLastDataTime == 0)
						this.onRecvFinish(realTime - 100 * 1000);
					if (map.containsKey("play_load_events")) {
						this.onKPISReport(map.get("play_load_events"), realTime);
					}
					this.onPlayFinish(realTime);
				}
			}
		}
	}

	/**
	 * 分析乐视视频事件
	 * 
	 * @param realTime
	 *          实时时间
	 * @param protecol
	 *          协议类型
	 * @param info
	 *          包数据
	 */
	private void analyseLeTV(long realTime, String protecol, packet_dissect_info info) {
		if (protecol.equals("dns")) {
			// if (this.mDNSStartTime == 0 && info.description.contains("Standard
			// query")
			// && (info.description.contains("down.api.mobile.youku.com") ||
			// info.description.contains("k.youku.com"))) {
			// this.onDNSStart(info);
			// } else if (this.mDNSSuccessTime == 0 &&
			// info.description.contains("Standard query response")
			// && (info.description.contains("down.api.mobile.youku.com") ||
			// info.description.contains("k.youku.com"))) {
			// this.onDNSSuccess(info.src_addr, realTime);
			// }
		} else if (protecol.equals("http")) {
			// if (this.mRequestTime == 0 && info.description.contains("ac=launch")) {
			// this.mVideoType = VIDEO_TYPE_LETV;
			// this.onInited(realTime - 100 * 1000);
			// } else if (this.mConnectStartTime == 0 &&
			// info.description.contains("ac=init")) {
			// this.onConnectStart(info);
			// } else if (this.mConnectSuccessTime == 0
			// && (info.description.contains("HTTP/1.1 200 OK") ||
			// info.description.contains("HTTP/1.1 302"))) {
			// this.onConnectSuccess(info);
			// } else
			if (this.mSendGetTime == 0 && info.description.contains("/letv-uts/") && info.description.contains(".ts")) {
				this.mVideoType = VIDEO_TYPE_LETV;
				this.onInited(realTime - 100 * 1000);
				// this.mConnectStartTime = realTime - 200 * 1000;
				// this.mConnectSuccessTime = realTime - 100 * 1000;
				this.onSendGet(info);
			} else if (this.mSendGetTime > 0 && this.mFirstDataTime == 0) {
				this.onFirstDataArrived(info);
			} else if (this.mReproductionStartTime == 0 && info.description.contains("ac=play")) {
				this.onReproductionStart(info.description, realTime);
			} else if (info.description.contains("ac=block")) {
				this.onReBufferingStart(realTime);
			} else if (info.description.contains("ac=eblock")) {
				this.onReBufferingEnd(realTime);
			} else if (this.mReproductionStartTime > 0 && this.mLastDataTime == 0 && info.description.contains("ac=end")) {
				if (info.description.contains("err=0")) {
					this.onRecvFinish(realTime - 100 * 1000);
					this.onKPISReport(realTime);
					this.onPlayFinish(realTime);
				} else {
					this.onKPISReport(realTime);
					this.onRecvDrop(realTime);
				}
			}
		}
	}

	/**
	 * 解析Http请求URL
	 * 
	 * @param url
	 *          请求URL
	 * @return
	 */
	private Map<String, String> analysisHttpRequest(String url) {
		String str = url.substring(url.indexOf("?"));
		String[] params = str.split("&");
		Map<String, String> paramMap = new HashMap<String, String>();
		for (int i = 0; i < params.length; i++) {
			String[] param = params[i].split("=");
			if (param != null && param.length == 2) {
				paramMap.put(param[0], param[1]);
			}
		}
		return paramMap;
	}

	/**
	 * 获得实际时间(微秒)
	 * 
	 * @param info
	 *          数据包
	 * @return
	 */
	private long getRealTime(packet_dissect_info info) {
		if (info.tv_sec > 0 && info.tv_usec > 0) {
			return Long.valueOf(info.tv_sec) * 1000000 + info.tv_usec; // 处理毫秒运算,去掉以前字符串处理方法
		}
		return System.currentTimeMillis() * 1000;
	}

	/**
	 * 初始化成功
	 * 
	 * @param time
	 *          包获取时间
	 */
	private void onInited(long time) {
		if (this.mRequestTime > 0)
			return;
		this.addRecord(time, "VideoPlay Request");
		this.mRequestTime = time;
		this.mListener.setRealTime(time);
		this.mListener.onInited();
	}

	/**
	 * 添加保存记录
	 * 
	 * @param time
	 *          时间(微秒)
	 * @param action
	 *          icon_event_1
	 */
	private void addRecord(long time, String action) {
		String videoType = "";
		switch (this.mVideoType) {
		case VIDEO_TYPE_YOUKU:
			videoType = "youku";
			break;
		case VIDEO_TYPE_LETV:
			videoType = "letv";
			break;
		default:
			break;
		}
		Record record = new Record(time, videoType, action);
		this.mRecords.add(record);
	}

	/**
	 * 
	 * @param packet
	 *          数据包
	 */
	private void onFirstDataArrived(packet_dissect_info packet) {
		if (this.mSendGetTime == 0 || this.mFirstDataTime > 0)
			return;
		if (this.mVideoType == VIDEO_TYPE_LETV
				&& (!this.mSendGetSrcAddr.equals(packet.dst_addr) || !this.mSendGetDstAddr.equals(packet.src_addr)))
			return;
		long time = this.getRealTime(packet);
		this.addRecord(time, "VideoPlay First Data");
		this.mFirstDataTime = time;
		this.mListener.setRealTime(time);
		this.mListener.onFirstDataArrived();
	}

	/**
	 * 
	 * @param description
	 *          包数据描述
	 * @param time
	 *          包获取时间
	 */
	private void onReproductionStart(String description, long time) {
		if (this.mFirstDataTime == 0 || this.mReproductionStartTime > 0)
			return;
		this.addRecord(time, "VideoPlay Reproduction Start");
		this.mReproductionStartTime = time;
		ReproductionStartInfo info = new ReproductionStartInfo();
		info.initBufferLatency = (int) (time - this.mSendGetTime) / 1000;
		info.RecvTotalBytes = 0;
		info.reproductionDelay = (int) (time - this.mFirstDataTime) / 1000;
		this.mListener.setRealTime(time);
		this.mListener.onReproductionStart(info);
	}

	/**
	 * 
	 * @param description
	 *          包数据描述
	 * @param time
	 *          包获取时间
	 */
	// private void onReproductionStartFailed(String description, long time) {
	// ReproductionStartFailedInfo info = new ReproductionStartFailedInfo();
	// this.mListener.onReproductionStartFailed(info);
	// }

	/**
	 * @param time
	 *          包获取时间
	 */
	private void onQosArrived(long time) {
		if (this.mVideoAppID == 0)
			return;
		if (this.mLastRecvTime == 0) {
			this.mLastRecvTime = time;
			this.mLastRecvBytes = TrafficInfoUtil.getInstance(mContext).getRcvTraffic(this.mVideoAppID);
			this.mFirstRecvBytes = this.mLastRecvBytes;
			return;
		}
		QosInfo info = new QosInfo();
		info.DownloadProgress = this.mDownloadProgress;
		long recvBytes = TrafficInfoUtil.getInstance(mContext).getRcvTraffic(this.mVideoAppID);
		info.CurRecvSpeed = (int) ((recvBytes - this.mLastRecvBytes) * 8 * 1000000 / (time - this.mLastRecvTime));
		if (info.CurRecvSpeed < 0)
			info.CurRecvSpeed = 0;
		info.RecvTotalBytes = (int) (recvBytes - this.mFirstRecvBytes);
		info.MeasureTime = (int) (time - this.mFirstDataTime) / 1000;
		this.mListener.onQosArrived(info);
		this.mLastRecvTime = time;
		this.mLastRecvBytes = recvBytes;
	}

	/**
	 * 
	 * @param time
	 *          包获取时间
	 */
	private void onRecvFinish(long time) {
		if (this.mReproductionStartTime == 0 || this.mLastDataTime > 0)
			return;
		this.mDownloadProgress = 100;
		this.onQosArrived(time);
		this.mLastDataTime = time;
		if (this.mThread != null) {
			this.mThread.stopThread();
			this.mThread = null;
		}
		this.addRecord(time, "VideoPlay Last Data");
		RecvFinishInfo info = new RecvFinishInfo();
		info.AudioSaveFile = "";
		info.AV_DeSync_Rate = 0;
		info.VideoSaveFile = "";
		info.VSNR_VMOS = 0;
		this.mListener.setRealTime(time);
		this.mListener.onRecvFinish(info);
	}

	/**
	 * 
	 * @param time
	 *          包获取时间
	 */
	private void onRecvDrop(long time) {
		RecvDropInfo info = new RecvDropInfo();
		info.reason = 3;
		this.mListener.setRealTime(time);
		this.mListener.onRecvDrop(info);
		this.saveToFile();
		this.init();
	}

	/**
	 * 
	 * @param time
	 *          包获取时间
	 */
	private void onReBufferingStart(long time) {
		if (this.mSendGetTime == 0 || this.mRebufferStartTime > 0)
			return;
		this.mRebufferStartTime = time;
		// this.mListener.setRealTime(time);
		// this.mListener.onReBufferingStart();
		this.addRecord(time, "VideoPlay Rebuffering Start");
	}

	/**
	 * 
	 * @param description
	 *          包数据描述
	 * @param time
	 *          包获取时间
	 */
	private void onReBufferingEnd(long time) {
		if (this.mRebufferStartTime == 0)
			return;
		long rebufferTime = time - this.mRebufferStartTime;
		if (rebufferTime > this.mRebufferMax)
			this.mRebufferMax = rebufferTime;
		this.mRebufferCount++;
		this.mRebufferSum += rebufferTime;
		this.mRebufferStartTime = 0;
		// this.mListener.setRealTime(time);
		// this.mListener.onReBufferingEnd();
		this.addRecord(time, "VideoPlay Rebuffering End");
	}

	/**
	 * 
	 * @param time
	 *          包获取时间
	 */
	private void onPlayFinish(long time) {
		if (this.mPlayFinishTime > 0)
			return;
		this.addRecord(time, "VideoPlay Play Finished");
		this.mPlayFinishTime = time;
		this.mListener.setRealTime(time);
		this.mListener.onPlayFinish();
		this.saveToFile();
		this.init();
	}

	/**
	 * 
	 * @param time
	 *          包获取时间
	 */
	// private void onQuit(long time) {
	// this.mListener.setRealTime(time);
	// this.mListener.onQuit();
	// }

	/**
	 * 
	 * @param description
	 *          包数据描述
	 * @param time
	 *          包获取时间
	 */
	// private void onMsg(String description, long time) {
	// MsgInfo info = new MsgInfo();
	// this.mListener.setRealTime(time);
	// this.mListener.onMsg(info);
	// }

	/**
	 * 
	 * @param description
	 *          包数据描述
	 * @param time
	 *          包获取时间
	 */
	// private void onConnectFailed(String description, long time) {
	// FailedInfo info = new FailedInfo();
	// this.mListener.setRealTime(time);
	// this.mListener.onConnectFailed(info);
	// }

	/**
	 * 
	 * @param packet
	 *          数据包
	 * @param time
	 *          包获取时间
	 */
	// private void onConnectStart(packet_dissect_info packet) {
	// if (this.mConnectStartTime > 0)
	// return;
	// long time = this.getRealTime(packet);
	// this.addRecord(time, "VideoPlay ConnectStart");
	// this.mConnectStartSrcAddr = packet.src_addr;
	// this.mConnectStartDstAddr = packet.dst_addr;
	// this.mConnectStartTime = time;
	// this.mListener.setRealTime(time);
	// this.mListener.onConnectStart();
	// }

	/**
	 * 
	 * @param packet
	 *          数据包
	 */
	// private void onConnectSuccess(packet_dissect_info packet) {
	// if (this.mConnectStartTime == 0 || this.mConnectSuccessTime > 0)
	// return;
	// if (this.mConnectStartSrcAddr.equals(packet.dst_addr) &&
	// this.mConnectStartDstAddr.equals(packet.src_addr)) {
	// long time = this.getRealTime(packet);
	// this.addRecord(time, "VideoPlay ConnectSuccess");
	// this.mConnectSuccessTime = time;
	// this.mListener.setRealTime(time);
	// this.mListener.onConnectSuccess();
	// }
	// }

	/**
	 * 
	 * @param description
	 *          包数据描述
	 * @param time
	 *          包获取时间
	 */
	// private void onDNSFailed(String description, long time) {
	// dnsfail_info info = new dnsfail_info();
	// this.mListener.setRealTime(time);
	// this.mListener.onDNSFailed(info);
	// }

	/**
	 * @param packet
	 *          数据包
	 */
	private void onDNSStart(packet_dissect_info packet) {
		if (this.mDNSStartTime > 0)
			return;
		long time = this.getRealTime(packet);
		this.addRecord(time, "DNS Lookup Start");
		this.mDNSFlag = packet.description.substring("Standard query".length(), packet.description.indexOf("A")).trim();
		this.mDNSStartTime = time;
		dnsstart_info info = new dnsstart_info();
		info.host = "down.api.mobile.youku.com";
		this.mListener.setRealTime(time);
		this.mListener.onDNSStart(info);
	}

	/**
	 * 
	 * @param src_addr
	 *          包数据描述
	 * @param time
	 *          包获取时间
	 */
	private void onDNSSuccess(String src_addr, long time) {
		if (this.mDNSStartTime == 0 || this.mDNSSuccessTime > 0)
			return;
		this.mDNSSuccessTime = time;
		dnssucc_info info = new dnssucc_info();
		this.addRecord(time, "DNS Lookup Success");
		this.mDNSDelay = (int) (time - this.mDNSStartTime);
		info.delay = this.mDNSDelay / 1000;
		info.host = "down.api.mobile.youku.com";
		info.dns_server = "";
		info.ip = src_addr;
		this.mListener.setRealTime(time);
		this.mListener.onDNSSuccess(info);
	}

	/**
	 * 
	 * @param time
	 *          包获取时间
	 */
	private void onKPISReport(long time) {
		if (this.mKPIReportTime > 0)
			return;
		this.addRecord(time, "VideoPlay VMos Report");
		this.mKPIReportTime = time;
		KpiInfo info = new KpiInfo();
		info.UrlParseLatency = this.mDNSDelay / 1000;
		info.InitBuffLatency = (int) ((this.mReproductionStartTime - this.mSendGetTime) / 1000);
		info.PlayDuration = (int) ((time - this.mReproductionStartTime - this.mRebufferSum) / 1000);
		info.ReBufferCount = this.mRebufferCount;
		info.ReBufferDuration = (int) this.mRebufferSum / 1000;
		info.MaxSingleRebuffLatency = (int) this.mRebufferMax / 1000;
		info.WholePhaseDuration = info.UrlParseLatency + info.InitBuffLatency + info.PlayDuration + info.ReBufferDuration;
		info.audio_codec = "";
		info.firstReachHopIp = "";
		info.url = "";
		info.video_codec = "";
		info.video_title = "";
		this.mListener.setRealTime(time);
		this.mListener.onKPISReport(info);
	}

	/**
	 * 
	 * @param playLoadEvents
	 *          播放加载事件
	 * @param time
	 *          包获取时间
	 */
	private void onKPISReport(String playLoadEvents, long time) {
		if (StringUtil.isNullOrEmpty(playLoadEvents) || this.mKPIReportTime > 0)
			return;
		String[] rebufferings = playLoadEvents.split("\\|");
		for (int i = 0; i < rebufferings.length; i++) {
			String[] times = rebufferings[i].split(",");
			long rebuffingStart = this.mReproductionStartTime + (long) (Float.parseFloat(times[0]) * 1000000);
			rebuffingStart += this.mRebufferSum;
			long rebuffingTime = (long) (Float.parseFloat(times[1]) * 1000);
			if (rebuffingTime == 0)
				continue;
			this.mRebufferSum += rebuffingTime;
			this.addRecord(rebuffingStart, "VideoPlay Rebuffering Start");
			this.mRebufferCount++;
			if (rebuffingTime > this.mRebufferMax)
				this.mRebufferMax = rebuffingTime;
			long rebuffingEnd = rebuffingStart + rebuffingTime;
			this.addRecord(rebuffingEnd, "VideoPlay Rebuffering End");
		}
		this.onKPISReport(time);
		// 把缓冲事件显示到界面
		for (int i = 0; i < rebufferings.length; i++) {
			String[] times = rebufferings[i].split(",");
			long rebuffingStart = this.mReproductionStartTime + (long) (Float.parseFloat(times[0]) * 1000000);
			long rebuffingTime = (long) (Float.parseFloat(times[1]) * 1000);
			if (rebuffingTime == 0)
				continue;
			String eventStr = "Rebuffer Time:" + this.mFormat1.format(new Date(rebuffingStart / 1000)) + "; Duration:"
					+ (rebuffingTime / 1000000.0);
			EventManager.getInstance().addTagEvent(this.mContext, rebuffingStart / 1000, eventStr);
		}
	}

	/**
	 * 
	 * @param packet
	 *          数据包
	 */
	private void onSendGet(packet_dissect_info packet) {
		if (this.mSendGetTime > 0)
			return;
		if (this.mThread == null) {
			this.mThread = new QosThread();
			this.mThread.start();
		}
		long time = this.getRealTime(packet);
		this.addRecord(time, "VideoPlay Send Get");
		this.mSendGetTime = time;
		this.mSendGetDstAddr = packet.dst_addr;
		this.mSendGetSrcAddr = packet.src_addr;
		send_get_info info = new send_get_info();
		this.mListener.setRealTime(time);
		this.mListener.onSendGet(info);
	}

	/**
	 * 
	 * @param description
	 *          包数据描述
	 * @param time
	 *          包获取时间
	 */
	// private void onSendGetFailed(String description, long time) {
	// FailedInfo info = new FailedInfo();
	// this.mListener.setRealTime(time);
	// this.mListener.onSendGetFailed(info);
	// }

	/**
	 * 
	 * @param description
	 *          包数据描述
	 * @param time
	 *          包获取时间
	 */
	// private void onUrlParseFailed(String description, long time) {
	// FailedInfo info = new FailedInfo();
	// this.mListener.setRealTime(time);
	// this.mListener.onUrlParseFailed(info);
	// }

	/**
	 * 
	 * @param time
	 *          包获取时间
	 */
	// private void onUrlParseStart(long time) {
	// this.mListener.setRealTime(time);
	// this.mListener.onUrlParseStart();
	// }

	/**
	 * 
	 * @param time
	 *          包获取时间
	 */
	// private void onUrlParseSuccess(long time) {
	// this.mListener.setRealTime(time);
	// this.mListener.onUrlParseSuccess();
	// }

	/**
	 * 
	 * @param description
	 *          包数据描述
	 * @param time
	 *          包获取时间
	 */
	// private void onInitFailed(String description, long time) {
	// FailedInfo info = new FailedInfo();
	// this.mListener.setRealTime(time);
	// this.mListener.onInitFailed(info);
	// }

	/**
	 * 
	 * @param description
	 *          包数据描述
	 * @param time
	 *          包获取时间
	 */
	// private void onPlayQosArrived(String description, long time) {
	// PlayQosInfo info = new PlayQosInfo();
	// this.mListener.setRealTime(time);
	// this.mListener.onPlayQosArrived(info);
	// }

	/**
	 * 保存文档记录
	 * 
	 * @author jianchao.wang
	 *
	 */
	public class Record implements Comparable<Record> {
		/** 记录时间 */
		public long mTime;
		/** 视频类型 */
		public String mVideoType;
		/** 事件类型 */
		public String mAction;

		public Record(long time, String videoType, String action) {
			this.mTime = time;
			this.mVideoType = videoType;
			this.mAction = action;
		}

		@Override
		public int compareTo(Record another) {
			if (this.mTime > another.mTime)
				return 1;
			else if (this.mTime < another.mTime)
				return -1;
			return 0;
		}

	}

	/**
	 * 把当前的记录保存到文件中
	 */
	private void saveToFile() {
		LogUtil.d(TAG, "----saveToFile:" + this.mFilePath);
		if (StringUtil.isNullOrEmpty(this.mFilePath))
			return;
		File file = new File(this.mFilePath);
		if (!file.exists())
			return;
		List<Record> list = new ArrayList<Record>(this.mRecords);
		Collections.sort(list);
		this.mRecords.clear();
		BufferedWriter writer = null;
		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(file, true);
			writer = new BufferedWriter(fileWriter);
			for (Record record : list) {
				String str = this.mFormat.format(new Date(record.mTime / 1000)) + "," + record.mVideoType + ","
						+ record.mAction;
				writer.write(str);
				writer.newLine();
				writer.flush();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (fileWriter != null) {
					fileWriter.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
			}
		}
	}

	private class QosThread extends Thread {

		private boolean isStop = false;

		@Override
		public void run() {
			while (!isStop) {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
				onQosArrived(System.currentTimeMillis() * 1000);
			}
		}

		public void stopThread() {
			this.isStop = true;
			this.interrupt();
		}
	}

}
