package com.walktour.control.netsniffer.packetanalysis;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;

import com.walktour.base.util.LogUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 应用的流量信息统计工具类
 * 
 * @author jianchao.wang
 *
 */
public class TrafficInfoUtil {
	/** 日志标识 */
	private static final String TAG = "TrafficInfoUtil";
	/** 不支持查询标识 */
	public static final int UNSUPPORTED = -1;
	/** 唯一实例 */
	private static TrafficInfoUtil sInstance;
	/** 初始接收数据大小 */
	private long preRxBytes = 0;
	/** 监控定时器 */
	private Timer mTimer = null;
	/** 上下文 */
	private Context mContext;
	/** 消息响应类 */
	private Handler mHandler;

	private TrafficInfoUtil(Context mContext) {
		this.mContext = mContext;
	}

	private TrafficInfoUtil(Context context, Handler handler) {
		this.mContext = context;
		this.mHandler = handler;
	}

	public static TrafficInfoUtil getInstance(Context context, Handler handler) {
		if (sInstance == null) {
			sInstance = new TrafficInfoUtil(context, handler);
		}
		return sInstance;
	}

	public static TrafficInfoUtil getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new TrafficInfoUtil(context);
		}
		return sInstance;
	}

	/**
	 * 获取应用总流量
	 * 
	 * @param uid
	 *          应用ID
	 * @return
	 */
	public long getTrafficInfo(int uid) {
		long rcvTraffic = UNSUPPORTED; // 下载流量
		long sndTraffic = UNSUPPORTED; // 上传流量
		rcvTraffic = getRcvTraffic(uid);
		sndTraffic = getSendTraffic(uid);
		if (rcvTraffic == UNSUPPORTED || sndTraffic == UNSUPPORTED)
			return UNSUPPORTED;
		return rcvTraffic + sndTraffic;
	}

	/**
	 * 获取下载流量 某个应用的网络流量数据保存在系统的/proc/uid_stat/$UID/tcp_rcv | tcp_snd文件中
	 * 
	 * @param uid
	 *          应用ID
	 * @return
	 */
	public long getRcvTraffic(int uid) {
		long rcvTraffic = UNSUPPORTED; // 下载流量
		rcvTraffic = TrafficStats.getUidRxBytes(uid);
		LogUtil.d(TAG, "getRcvTraffic---1:" + rcvTraffic);
		if (rcvTraffic != UNSUPPORTED) { // 支持的查询
			return rcvTraffic;
		}
		RandomAccessFile rafRcv = null, rafSnd = null; // 用于访问数据记录文件
		String rcvPath = "/proc/uid_stat/" + uid + "/tcp_rcv";
		try {
			rafRcv = new RandomAccessFile(rcvPath, "r");
			rcvTraffic = Long.parseLong(rafRcv.readLine()); // 读取流量统计
		} catch (FileNotFoundException e) {
			LogUtil.e(TAG, "FileNotFoundException: " + e.getMessage());
			rcvTraffic = UNSUPPORTED;
		} catch (IOException e) {
			LogUtil.e(TAG, "IOException: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rafRcv != null)
					rafRcv.close();
				if (rafSnd != null)
					rafSnd.close();
			} catch (IOException e) {
				LogUtil.e(TAG, "Close RandomAccessFile exception: " + e.getMessage());
			}
		}
		LogUtil.d(TAG, "getRcvTraffic---2:" + rcvTraffic);
		return rcvTraffic;
	}

	/**
	 * 获取上传流量
	 * 
	 * @param uid
	 *          应用ID
	 * 
	 * @return
	 */
	public long getSendTraffic(int uid) {
		long sndTraffic = UNSUPPORTED; // 上传流量
		sndTraffic = TrafficStats.getUidTxBytes(uid);
		LogUtil.d(TAG, "getSendTraffic---1:" + sndTraffic);
		if (sndTraffic != UNSUPPORTED) { // 支持的查询
			return sndTraffic;
		}

		RandomAccessFile rafRcv = null, rafSnd = null; // 用于访问数据记录文件
		String sndPath = "/proc/uid_stat/" + uid + "/tcp_snd";
		try {
			rafSnd = new RandomAccessFile(sndPath, "r");
			sndTraffic = Long.parseLong(rafSnd.readLine());
		} catch (FileNotFoundException e) {
			LogUtil.e(TAG, "FileNotFoundException: " + e.getMessage());
			sndTraffic = UNSUPPORTED;
		} catch (IOException e) {
			LogUtil.e(TAG, "IOException: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rafRcv != null)
					rafRcv.close();
				if (rafSnd != null)
					rafSnd.close();
			} catch (IOException e) {
				LogUtil.e(TAG, "Close RandomAccessFile exception: " + e.getMessage());
			}
		}
		LogUtil.d(TAG, "getSendTraffic---2:" + sndTraffic);
		return sndTraffic;
	}

	/**
	 * 获取当前下载流量总和
	 * 
	 * @return
	 */
	public static long getNetworkRxBytes() {
		return TrafficStats.getTotalRxBytes();
	}

	/**
	 * 获取当前上传流量总和
	 * 
	 * @return
	 */
	public static long getNetworkTxBytes() {
		return TrafficStats.getTotalTxBytes();
	}

	/**
	 * 获取当前网速
	 * 
	 * @return
	 */
	public double getNetSpeed() {
		long curRxBytes = getNetworkRxBytes();
		if (preRxBytes == 0)
			preRxBytes = curRxBytes;
		long bytes = curRxBytes - preRxBytes;
		preRxBytes = curRxBytes;
		// int kb = (int) Math.floor(bytes / 1024 + 0.5);
		double kb = (double) bytes / (double) 1024;
		BigDecimal bd = new BigDecimal(kb);

		return bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 开启流量监控
	 */
	public void startCalculateNetSpeed() {
		preRxBytes = getNetworkRxBytes();
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		if (mTimer == null) {
			mTimer = new Timer();
			mTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					Message msg = new Message();
					msg.what = 1;
					// msg.arg1 = getNetSpeed();
					msg.obj = getNetSpeed();
					mHandler.sendMessage(msg);
				}
			}, 1000, 1000);
		}
	}

	public void stopCalculateNetSpeed() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	/**
	 * 获取指定应用的uid
	 * 
	 * @param packageName
	 *          包名
	 * @return
	 */
	public int getUid(String packageName) {
		try {
			PackageManager pm = mContext.getPackageManager();
			ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_ACTIVITIES);
			return ai.uid;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}

}
