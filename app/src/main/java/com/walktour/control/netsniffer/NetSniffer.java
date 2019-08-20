package com.walktour.control.netsniffer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dingli.service.test.ipc2msg;
import com.dingli.service.test.tcpipmonitorsojni;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.packet_dissect_info;
import com.walktour.control.bean.tcpipmonitorso_start_params;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.netsniffer.packetanalysis.VideoPlayAnalysis;
import com.walktour.gui.task.parsedata.TaskListDispose;

import java.util.List;
import java.util.Locale;

/**
 * 执行或停止netsniffer脚本<BR>
 * [功能详细描述]
 *
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-8-30]
 */
public class NetSniffer extends AbsRunner {

	private static String TAG = "NetSniffer";

	private static String cmdPath;

	private static final int TCPIP_MONITOR_TEST = 92;

	private static final int TCPIP_MONITOR_INITED = 1;
	private static final int TCPIP_MONITOR_START = 2;
	private static final int TCPIP_MONITOR_FAILED = 11;
	private static final int TCPIP_MONITOR_FINISH = 12;
	private static final int TCPIP_MONITOR_QUIT = 13;

	// private static final int TCPIP_MONITOR_START_TEST = 1001;
	// private static final int TCPIP_MONITOR_STOP_TEST = 1006;

	private static tcpipmonitorsojni aIpc2Jni;

	/**
	 * 抓包文件的全路径
	 */
	private String filePath;

	private Context mContext;
	private MyThread mThread;
	/**
	 * 全局唯一实例
	 */
	private static final NetSniffer instance = new NetSniffer();;

	/**
	 * 获取实例，当实例为空时新建一个实例
	 *
	 * @return
	 */
	public static NetSniffer getInstance() {
//		LogUtil.d(TAG, "instance =" + (instance == null));
//		if (instance == null) {
//			synchronized (NetSniffer.class) {
//				if (instance == null) {
//
//				}
//			}
//		}

		return instance;
	}

	private NetSniffer() {
	}

	/**
	 * 开始抓包
	 *
	 * @return
	 */
	@Override
	protected boolean localStart(Context context, String filePath) {
		Log.i(TAG, "load tcpipmonitor lib");
		this.mContext = context;
		this.filePath = filePath;
		VideoPlayAnalysis.getInstance().init();
		VideoPlayAnalysis.getInstance().createFile(filePath);
		boolean result;
		aIpc2Jni = new tcpipmonitorsojni(new EventHandler(mContext.getMainLooper()));
		result = aIpc2Jni.init_tcpip(AppFilePathUtil.getInstance().getSDCardBaseDirectory("liblog"),
				AppFilePathUtil.getInstance().getAppLibDirectory());
		LogUtil.w(TAG, "init result = " + result);
		LogUtil.w(TAG, "start to netsniffer");

		tcpipmonitorso_start_params tcpip_params = new tcpipmonitorso_start_params();
		tcpip_params.local_if = "any";
		tcpip_params.open_type = 0;
		tcpip_params.file_path = this.filePath;
		tcpip_params.filter_mode = ConfigRoutine.getInstance().getTcpIpCollect(mContext);
		tcpip_params.decode_mode = 2;
		tcpip_params.is_append = 0;
		tcpip_params.net_diagnose = 0;
		result = aIpc2Jni.tcpip_start(tcpip_params);

		LogUtil.w(TAG,"tcpip_params="+tcpip_params.toString());
		isTcpipRun = true;
		cur_packet_count = 0;
		LogUtil.i(TAG, "start result = " + result);
		if (ApplicationModel.getInstance().isBeiJingTest() && this.mThread == null) {
			this.mThread = new MyThread();
			this.mThread.start();
		}
		return result;

		// return Command.sudo(cmd);
	}

	@SuppressLint("HandlerLeak")
	private class EventHandler extends Handler{
		public EventHandler(Looper looper){
			super(looper);
		}
		public void handleMessage(android.os.Message msg) {
			LogUtil.d(TAG, "recv callback");
			ipc2msg aMsg = (ipc2msg) msg.obj;
			if (aMsg.test_item != TCPIP_MONITOR_TEST) {
				return;
			}

			switch (aMsg.event_id) {
				case TCPIP_MONITOR_INITED:
					LogUtil.w(TAG, "recv TCPIP_MONITOR_INITED " + Deviceinfo.getInstance().getPppName());
					String event_data = "local_if::" + getNetInterface() + "\n" + "parent_processid::0\n" + "filter_mode::"
							+ ConfigRoutine.getInstance().getTcpIpCollect(mContext) + "\n" + "dump_filename::" + filePath;
					LogUtil.w(TAG, event_data);
					// aIpc2Jni.send_command(TCPIP_MONITOR_TEST, TCPIP_MONITOR_START_TEST,
					// event_data, event_data.length());
					break;
				case TCPIP_MONITOR_START:
					LogUtil.w(TAG, "recv TCPIP_MONITOR_START");
					break;
				case TCPIP_MONITOR_FAILED:
					LogUtil.w(TAG, "recv TCPIP_MONITOR_FAILED");
					LogUtil.w(TAG, "TCPIP_MONITOR_FAILED" + aMsg.data);
					break;
				case TCPIP_MONITOR_FINISH:
					LogUtil.w(TAG, "recv TCPIP_MONITOR_FINISH");
					break;
				case TCPIP_MONITOR_QUIT:
					LogUtil.w(TAG, "recv TCPIP_MONITOR_QUIT");
					break;
			}
		}
	}

	/**
	 * 得到当前手机绑定的网卡
	 *
	 * @return
	 */
	protected String getNetInterface() {
		String result = "";
		if (TaskListDispose.getInstance().isWlanTest()) {
			result = Deviceinfo.getInstance().getWifiDevice();
			if (result == null) {
				result = "";
			}
		} else {
			result = Deviceinfo.getInstance().getPppName();
			if (result == null) {
				result = "";
			}
		}
		return result;
	}

	/**
	 * 结束抓包
	 */
	@Override
	protected boolean localStop() {
		LogUtil.d(TAG, "stop netsniffer");
		isTcpipRun = false;
		aIpc2Jni.tcpip_stop(); // send_command(TCPIP_MONITOR_TEST,
		// TCPIP_MONITOR_STOP_TEST, "", 0);
		aIpc2Jni.uninit_tcpip();
		aIpc2Jni = null;
		this.cur_packet_count = 0;
		TraceInfoInterface.traceData.tcpipInfoList.clear();
		if (ApplicationModel.getInstance().isBeiJingTest() && this.mThread != null) {
			this.mThread.stopThread();
			this.mThread = null;
		}
		return true;
	}

	/**
	 * 发送cell信息给NetSniffer程序BR> [功能详细描述]
	 *
	 * @param cellBean
	 * @return 当实例为空或命令执行失败时返回false, 命令执行成功时返回true
	 */
	public static boolean sendCellInfo(CellBean cellBean) {
		if (instance == null) {
			return false;
		}
		String cmd = cmdPath + " cell " + cellBean.toString();
		return Command.sudo(cmd);
	}

	private boolean isTcpipRun = false;
	private packet_dissect_info packet_info = new packet_dissect_info();
	private int cur_packet_count = 0;

	/**
	 * 生成抓包详细解码相关列表信息
	 */
	public synchronized void buildTcpipSimpleInfo() {
		if (isTcpipRun) {
			int packet_count = aIpc2Jni.tcpip_get_packet_count();
			LogUtil.w(TAG, "--buildTcpipDetailInfo:" + isTcpipRun + "--packet_count:" + packet_count + "--"
					+ UtilsMethod.sdfhmsss.format(System.currentTimeMillis()));
			for (; isTcpipRun && cur_packet_count < packet_count;) {
				aIpc2Jni.tcpip_read_packet_simple_info(cur_packet_count, packet_info);
				packet_dissect_info packet = packet_dissect_info.createInstance(packet_info);
				cur_packet_count++;
				if (StringUtil.isNullOrEmpty(packet.protocol))
					continue;
				// 对于北京移动测试项目来说，需要通过判断抓包事件中的信息来判断视频播放中的所有事件
				if (ApplicationModel.getInstance().isBeiJingTest()) {
					VideoPlayAnalysis.getInstance().analysis(mContext, packet);
				}
				if (!packet.protocol.toLowerCase(Locale.getDefault()).equals("tcp")) {
					TraceInfoInterface.traceData.tcpipInfoList.add(packet);

					while (TraceInfoInterface.traceData.tcpipInfoList.size() > 30) {
						TraceInfoInterface.traceData.tcpipInfoList.remove(0);
					}
				}
			}
			LogUtil.w(TAG, "--buildTcpipDetailInfo: end:" + UtilsMethod.sdfhmsss.format(System.currentTimeMillis()));
		}
	}

    /***
     * 获取抓包界面数据
     * @return
     */
	public List<packet_dissect_info> getDatas(){
		return TraceInfoInterface.traceData.tcpipInfoList;
	}
	public packet_dissect_info buildTcpIpDetailInfo(int packet_idx) {
		packet_dissect_info detailInfo = new packet_dissect_info();
		if (aIpc2Jni != null) {
			aIpc2Jni.tcpip_read_packet_detail_info(packet_idx, detailInfo);
		}
		return detailInfo;
	}

	/**
	 * 北京测试demo启动默认线程不断的去取
	 *
	 * @author jianchao.wang
	 *
	 */
	private class MyThread extends Thread {
		/** 间隔时间 */
		private static final long INTERVAL_TIME = 1000;
		/** 是否停止线程 */
		private boolean isStop = false;

		@Override
		public void run() {
			while (isTcpipRun && !isStop) {
				try {
					sleep(INTERVAL_TIME);
				} catch (InterruptedException e) {
					break;
				}
				buildTcpipSimpleInfo();
			}
		}

		/**
		 * 停止线程
		 */
		public void stopThread() {
			this.isStop = true;
			this.interrupt();
		}
	}
}
