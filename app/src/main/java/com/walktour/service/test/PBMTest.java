package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;

import com.dingli.service.test.ipc2msg;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.TotalStruct.TotalPBM;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.DataTaskValue;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.task.pbm.TaskPBMModel;
import com.walktour.service.TestService;

import java.util.HashMap;
import java.util.Map;

/**
 * PBM测试服务类
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("SdCardPath")
public class PBMTest extends TestTaskService {
	private final String tag = "PBMTest";
	/** 实时的品质 */
	private PBMQOS realQOS;
	/** 执行结束时汇总的品质 */
	private PBMQOS[] result;
	/** 监控当前网络状态的线程 */
	private NetTypeThread netTypeThread;
	/** 事件参数: 网络发生变化(ID不可变) */
	private static final int PBM_NETWORK_CHANGE = 1010;
	/**
	 * 发送给iPack PBM UL BANDWIDTH数据的广播
	 */
	public static final String BROADCAST_PBM_UP_BANDWIDTH = "BROADCAST_PBM_UP_BANDWIDTH";
	/**
	 * 发送给iPack PBM DL BANDWIDTH数据的广播
	 */
	public static final String BROADCAST_PBM_DOWN_BANDWIDTH = "BROADCAST_PBM_DOWN_BANDWIDTH";
	/**
	 * 发送给iPack PBM UL BANDWIDTH数据的广播key
	 */
	public static final String EXTRA_PBM_UP_BANDWIDTH = "PBM_UP_BANDWIDTH";
	/**
	 * 发送给iPack PBM DL BANDWIDTH数据的广播key
	 */
	public static final String EXTRA_PBM_DOWN_BANDWIDTH = "PBM_DOWN_BANDWIDTH";


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.i(tag, "onStart");
		useRoot = Deviceinfo.getInstance().isUseRoot();
		int startFlag = super.onStartCommand(intent, flags, startId);

		if (taskModel == null) {
			stopSelf();
		} else {
			TaskPBMModel pbmModel = (TaskPBMModel) taskModel;
			dataTestHandler = new PBMHandler(pbmModel);
			dataTestHandler.startTest();

		}
		return startFlag;
	}

	@SuppressLint("HandlerLeak")
	private class PBMHandler extends DataTestHandler {
		/** 业务ID */
		public static final int PBM_TEST = 94;
		/** 对外发出事件 */
		/** 初始化完毕 */
		public static final int PBM_INITED = 1;
		/** 业务正式开始测试(收到START_TEST命令后报出) */
		public static final int PBM_START = 10;
		/** 事件参数: 上行品质数据(定期报告) */
		public static final int PBM_UP_QOS_ARRIVED = 11;
		/** 事件参数: 下行品质数据(定期报告) */
		public static final int PBM_DOWN_QOS_ARRIVED = 12;
		/** 事件参数: 业务异常结束 (RCU:) */
		public static final int PBM_FAILED = 21;
		/** 事件参数: 业务正常结束 (RCU:) */
		public static final int PBM_FINISH = 22;
		/** 程序退出 */
		public static final int PBM_QUIT = 25;
		/** 外部发来事件 */
		/** 事件参数: 开始业务(ID不可变) */
		public static final int PBM_START_TEST = 1001;
		/** 事件参数: 停止业务(ID不可变) */
		public static final int PBM_STOP_TEST = 1006;
		/** 任务 */
		private TaskPBMModel pbmModel;
		/** 下行带宽累计 */
		private long sumBandwidth = 0;
		/** 获取有效下行带宽次数 */
		private int counts = 0;

		public PBMHandler(TaskPBMModel pbmModel) {
			super("-m dlabm -z " + AppFilePathUtil.getInstance().getAppConfigDirectory(),
					AppFilePathUtil.getInstance().getAppLibFile(false ? "datatests_android" : "libdatatests_so.so").getAbsolutePath(),
					PBM_TEST, PBM_START_TEST, PBM_STOP_TEST);
			LogUtil.i(tag, "useRoot = " + useRoot);
			this.pbmModel = pbmModel;
			super.getNetWorkType(true);
		}

		@Override
		public void handleMessage(android.os.Message msg) {
			ipc2msg aMsg = (ipc2msg) msg.obj;
			if (aMsg.test_item != PBM_TEST) {
				return;
			}
			hasEventCallBack = true; // 设置业务库有事件回调

			switch (aMsg.event_id) {
			case PBM_INITED:
				LogUtil.i(tag, "recv PBM_INITED\r\n");
				LogUtil.i(tag, aMsg.data + "\r\n");

				StringBuilder event_data = new StringBuilder();
//				event_data.append("local_if::").append(getNetInterface()).append("\n");
				event_data.append("local_if::").append("").append("\n");
				event_data.append("parent_process_id::0\n");
				event_data.append("duration_ms::").append(pbmModel.getDuration() * 1000).append("\n");
				event_data.append("sample_interval_ms::").append(pbmModel.getSampleInterval() * 1000).append("\n");
				event_data.append("network_type::").append(this.netWorkType).append("\n");
				event_data.append("nodata_timeout_ms::").append(pbmModel.getNodataTimeout() * 1000).append("\n");
				event_data.append("server_ip::").append(pbmModel.getServerIP()).append("\n");
				event_data.append("server_port::").append(pbmModel.getServerPort()).append("\n");
				event_data.append("test_mode::").append(pbmModel.getTestMode()).append("\n");
				event_data.append("up_sample_ratio::").append(pbmModel.getUpSampleRatio()).append("\n");
				event_data.append("down_sample_ratio::").append(pbmModel.getDownSampleRatio());
				LogUtil.w(tag, event_data.toString());
				this.sendStartCommand(event_data.toString());
				netTypeThread = new NetTypeThread();
				break;

			case PBM_START:
				LogUtil.i(tag, "recv PBM_START\r\n");
				firstDataTime = aMsg.getRealTime() / 1000;
				// 存储事件
				EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.PBM_Start)
						.addInteger(this.pbmModel.getDuration() * 1000).addInteger(this.pbmModel.getSampleInterval() * 1000);
				eb.addCharArray(this.pbmModel.getServerIP().toCharArray(), 48);
				eb.addInteger(this.pbmModel.getServerPort());
				eb.writeToRcu(aMsg.getRealTime());
				this.sumBandwidth = 0;
				this.counts = 0;
				if (netTypeThread != null)
					netTypeThread.start();
				// 设置主进程中的firstdata状态
				setMainFirstDataState(true);
				break;

			case PBM_UP_QOS_ARRIVED:
				LogUtil.i(tag, "recv PBM_UP_QOS_ARRIVED\r\n");
				LogUtil.i(tag, aMsg.data + "\r\n");
				String[] qos = aMsg.data.split("\n");
				realQOS = new PBMQOS();
				realQOS.direction = TaskPBMModel.DIRECTION_UP;
				int pos = 0;
				realQOS.measureTime = Integer.parseInt(this.getValue(qos[pos++], false));
				realQOS.bandwidth = Integer.parseInt(this.getValue(qos[pos++], false));
				sendPBMULBandwidth2IPack(realQOS.bandwidth);
				realQOS.delay = Integer.parseInt(this.getValue(qos[pos++], false));
				realQOS.pkgs = Integer.parseInt(this.getValue(qos[pos++], false));
				realQOS.lostPkgs = Integer.parseInt(this.getValue(qos[pos++], false));
				realQOS.lostFraction = Integer.parseInt(this.getValue(qos[pos++], false));
				realQOS.pkgGap = Integer.parseInt(this.getValue(qos[pos++], false));
				this.writeQOS();
				// sendCurrentRate();
				break;

			case PBM_DOWN_QOS_ARRIVED:
				LogUtil.i(tag, "recv PBM_DOWN_QOS_ARRIVED\r\n");
				LogUtil.i(tag, aMsg.data + "\r\n");
				qos = aMsg.data.split("\n");
				realQOS = new PBMQOS();
				realQOS.direction = TaskPBMModel.DIRECTION_DOWN;
				pos = 0;
				realQOS.measureTime = Integer.parseInt(this.getValue(qos[pos++], false));
				realQOS.bandwidth = Integer.parseInt(this.getValue(qos[pos++], false));
				sendPBMDLBandwidth2IPack(realQOS.bandwidth);
				realQOS.delay = Integer.parseInt(this.getValue(qos[pos++], false));
				realQOS.pkgs = Integer.parseInt(this.getValue(qos[pos++], false));
				realQOS.lostPkgs = Integer.parseInt(this.getValue(qos[pos++], false));
				realQOS.lostFraction = Integer.parseInt(this.getValue(qos[pos++], false));
				realQOS.pkgGap = Integer.parseInt(this.getValue(qos[pos++], false));
				realQOS.serverDelay = Integer.parseInt(this.getValue(qos[pos++], false));
				realQOS.roundTripDelay = Integer.parseInt(this.getValue(qos[pos++], false));
				if (realQOS.bandwidth > 0) {
					this.sumBandwidth += realQOS.bandwidth;
					this.counts++;
				}
				this.writeQOS();
				sendCurrentRate();
				break;

			case PBM_FAILED:
				Log.i(tag, "recv PBM_FAILED\r\n");
				Log.i(tag, aMsg.data + "\r\n");
				qos = aMsg.data.split("\n");
				Log.i(tag, "qos.length=" + qos.length);
				lastDataTime = aMsg.getRealTime() / 1000;
				int failCode = Integer.parseInt(this.getValue(qos[0], false));
				String desc = this.getValue(qos[1], true);
				result = new PBMQOS[2];
				result[0] = new PBMQOS();
				result[1] = new PBMQOS();
				if (qos.length > 2) {
					pos = 2;
					for (int i = 0; i < 2; i++) {
						PBMQOS pq = result[i];
						pq.bandwidth = Integer.parseInt(this.getValue(qos[pos++], false));
						pq.bandwidthMax = Integer.parseInt(this.getValue(qos[pos++], false));
						pq.bandwidthMin = Integer.parseInt(this.getValue(qos[pos++], false));
						pq.delay = Integer.parseInt(this.getValue(qos[pos++], false));
						pq.pkgs = Integer.parseInt(this.getValue(qos[pos++], false));
						pq.lostPkgs = Integer.parseInt(this.getValue(qos[pos++], false));
						pq.lostFraction = Integer.parseInt(this.getValue(qos[pos++], false));
						pq.pkgGap = Integer.parseInt(this.getValue(qos[pos++], false));
					}
					result[1].serverDelay = Integer.parseInt(this.getValue(qos[pos++], false));
					result[1].roundTripDelay = Integer.parseInt(this.getValue(qos[pos++], false));
				}
				if (netTypeThread != null) {
					netTypeThread.stopThread();
					netTypeThread = null;
				}
				fail(failCode, desc, aMsg.getRealTime());
				break;

			case PBM_FINISH:
				LogUtil.i(tag, "recv PBM_FINISH\r\n");
				LogUtil.i(tag, aMsg.data + "\r\n");
				lastDataTime = aMsg.getRealTime() / 1000;
				qos = aMsg.data.split("\n");
				result = new PBMQOS[2];
				pos = 0;
				result[0] = new PBMQOS();
				result[1] = new PBMQOS();
				for (int i = 0; i < 2; i++) {
					PBMQOS pq = result[i];
					pq.bandwidth = Integer.parseInt(this.getValue(qos[pos++], false));
					pq.bandwidthMax = Integer.parseInt(this.getValue(qos[pos++], false));
					pq.bandwidthMin = Integer.parseInt(this.getValue(qos[pos++], false));
					pq.delay = Integer.parseInt(this.getValue(qos[pos++], false));
					pq.pkgs = Integer.parseInt(this.getValue(qos[pos++], false));
					pq.lostPkgs = Integer.parseInt(this.getValue(qos[pos++], false));
					pq.lostFraction = Integer.parseInt(this.getValue(qos[pos++], false));
					pq.pkgGap = Integer.parseInt(this.getValue(qos[pos++], false));
				}
				result[1].serverDelay = Integer.parseInt(this.getValue(qos[pos++], false));
				result[1].roundTripDelay = Integer.parseInt(this.getValue(qos[pos++], false));
				if (netTypeThread != null) {
					netTypeThread.stopThread();
					netTypeThread = null;
				}
				lastData(aMsg.getRealTime());
				break;

			case PBM_QUIT:
				LogUtil.i(tag, "recv PBM_QUIT\r\n");
				if (netTypeThread != null) {
					netTypeThread.stopThread();
					netTypeThread = null;
				}
				break;

			}
		}

		/**
		 * 把QOS写入rcu文件
		 */
		private void writeQOS() {
			if (realQOS != null) {
				if (realQOS.direction == TaskPBMModel.DIRECTION_DOWN) {
					EventBytes.Builder(getBaseContext()).addInteger(WalkCommonPara.PBMQosDown).addInteger(realQOS.measureTime)
							.addInteger(realQOS.bandwidth).addInteger(realQOS.delay).addInteger(realQOS.pkgs)
							.addInteger(realQOS.lostPkgs).addInteger(realQOS.lostFraction).addInteger(realQOS.pkgGap)
							.addInteger(realQOS.serverDelay).addInteger(realQOS.roundTripDelay)
							.writeToRcu(WalkCommonPara.MsgDataFlag_A);
				} else {
					EventBytes.Builder(getBaseContext()).addInteger(WalkCommonPara.PBMQosUp).addInteger(realQOS.measureTime)
							.addInteger(realQOS.bandwidth).addInteger(realQOS.delay).addInteger(realQOS.pkgs)
							.addInteger(realQOS.lostPkgs).addInteger(realQOS.lostFraction).addInteger(realQOS.pkgGap)
							.writeToRcu(WalkCommonPara.MsgDataFlag_A);
				}
			}
		}

		/**
		 * 根据返回的 key::value来获得值
		 * 
		 * @param str
		 *          字符串
		 * @param isString
		 *          返回值是否字符串
		 * 
		 * @return
		 */
		private String getValue(String str, boolean isString) {
			String[] ls = str.split("::");
			if (ls.length == 1){
				if (isString)
					return "";
				return "0";
			}
			return ls[1];
		}

		@Override
		protected void prepareTest() {

		}

		/**
		 * 失败记录
		 * 
		 * @param failReason
		 *          失败原因
		 * @param desc
		 *          失败描述
		 */
		private void fail(int failReason, String desc, long time) {
			// fail、drop、lastData三个事件互斥
			LogUtil.w(tag, "--fail hasFail:" + hasFail + "-- hasDrop:" + hasDrop + "--hasLastData:" + hasLastData);
			if (!hasFail && !hasDrop && !hasLastData) {
				hasFail = true;
				int[] bandwidth = new int[2];
				int[] lostFraction = new int[2];
				int[] pkgGap = new int[2];
				// 存储事件
				EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.PBM_Failure);
				eb.addInteger(failReason).addStringBuffer(desc);
				for (int i = 0; i < 2; i++) {
					eb.addInteger(result[i].bandwidth);
					eb.addInteger(result[i].bandwidthMax);
					eb.addInteger(result[i].bandwidthMin);
					eb.addInteger(result[i].delay);
					eb.addInteger(result[i].pkgs);
					eb.addInteger(result[i].lostPkgs);
					eb.addInteger(result[i].lostFraction);
					eb.addInteger(result[i].pkgGap);
					bandwidth[i] += result[i].bandwidth;
					lostFraction[i] += result[i].lostFraction;
					pkgGap[i] += result[i].pkgGap;
				}
				eb.addInteger(result[1].serverDelay);
				eb.addInteger(result[1].roundTripDelay);
				eb.writeToRcu(time);
				// 统计页面
				totalResult(this, TotalPBM._pbmUpBandwidth, bandwidth[0]);
				totalResult(this, TotalPBM._pbmUpLostFraction, lostFraction[0] < 0 ? 0 : lostFraction[0]);
				totalResult(this, TotalPBM._pbmUpPkgGap, pkgGap[0]);
				totalResult(this, TotalPBM._pbmDownBandwidth, bandwidth[1]);
				totalResult(this, TotalPBM._pbmDownLostFraction, lostFraction[1] < 0 ? 0 : lostFraction[1]);
				totalResult(this, TotalPBM._pbmDownPkgGap, pkgGap[1]);
				totalResult(this, TotalPBM._pbmCurrentTimes, 1);

				// 统计参数
				sendTotalPBMPara(lastDataTime - firstDataTime);
				// 结束本次测试
				this.stopProcess(TestService.RESULT_FAILD);
			}
		}

		@Override
		protected void drop(int dropReason, long time) {

		}

		@Override
		protected void sendCurrentRate() {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			// 当前带宽kbps
			dataMap.put(WalkStruct.DataTaskValue.PBMDLBandwidth.name(),
					UtilsMethod.decFormat.format(realQOS.bandwidth / UtilsMethod.kbyteRage));
			dataMap.put(DataTaskValue.BordCurrentSpeed.name(),
					UtilsMethod.decFormat.format(realQOS.bandwidth / UtilsMethod.kbyteRage));
			// 平均带宽kbps
			double avg = 0;
			if (this.counts > 0)
				avg = this.sumBandwidth / this.counts;
			dataMap.put(DataTaskValue.BordLeftTitle.name(), getString(R.string.info_data_averagerate) + "@"
					+ UtilsMethod.decFormat.format(Math.round(avg) / UtilsMethod.kbyteRage) + getString(R.string.info_rate_kbps));

			callbackHandler.obtainMessage(DATA_CHANGE, dataMap).sendToTarget();
		}

		@Override
		protected void lastData(long time) {
			// fail、drop、lastData三个事件互斥
			if (!hasLastData && firstDataTime != 0 && !hasFail) {
				LogUtil.d(tag, result[0].toString() + " \r\n");
				LogUtil.d(tag, result[1].toString() + " \r\n");
				hasLastData = true;
				int[] bandwidth = new int[2];
				int[] lostFraction = new int[2];
				int[] pkgGap = new int[2];
				// 存储事件
				EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.PBM_Finished);
				for (int i = 0; i < 2; i++) {
					eb.addInteger(result[i].bandwidth);
					eb.addInteger(result[i].bandwidthMax);
					eb.addInteger(result[i].bandwidthMin);
					eb.addInteger(result[i].delay);
					eb.addInteger(result[i].pkgs);
					eb.addInteger(result[i].lostPkgs);
					eb.addInteger(result[i].lostFraction);
					eb.addInteger(result[i].pkgGap);
					bandwidth[i] += result[i].bandwidth;
					lostFraction[i] += result[i].lostFraction;
					pkgGap[i] += result[i].pkgGap;
				}
				eb.addInteger(result[1].serverDelay);
				eb.addInteger(result[1].roundTripDelay);
				eb.writeToRcu(time);

				// 统计页面
				totalResult(this, TotalPBM._pbmUpBandwidth, bandwidth[0]);
				totalResult(this, TotalPBM._pbmUpLostFraction, lostFraction[0] < 0 ? 0 : lostFraction[0]);
				totalResult(this, TotalPBM._pbmUpPkgGap, pkgGap[0]);
				totalResult(this, TotalPBM._pbmDownBandwidth, bandwidth[1]);
				totalResult(this, TotalPBM._pbmDownLostFraction, lostFraction[1] < 0 ? 0 : lostFraction[1]);
				totalResult(this, TotalPBM._pbmDownPkgGap, pkgGap[1]);
				totalResult(this, TotalPBM._pbmCurrentTimes, 1);

				// 统计参数
				sendTotalPBMPara(lastDataTime - firstDataTime);

			}

			// 设置主进程中的firstdata状态
			setMainFirstDataState(false);
			stopProcess(TestService.RESULT_SUCCESS);
		}

		/**
		 * 业务成功时写入pbm相关的参数统计
		 * 
		 * @param pbmJobTime
		 *          pbm的业务时间(last - firstDataTime)
		 */
		protected void sendTotalPBMPara(long pbmJobTime) {
			Intent pbmTotalFull = new Intent(WalkMessage.TotalByPBMIsFull);
			pbmTotalFull.putExtra("PBMJobTimes", pbmJobTime);
			sendBroadcast(pbmTotalFull);
		}

		/**
		 * 统计
		 * 
		 * @param totalType
		 *          统计项
		 * @param value
		 *          值
		 */
		private void totalResult(DataTestHandler handler, TotalPBM totalType, long value) {
			HashMap<String, Long> map = new HashMap<String, Long>();
			map.put(totalType.name(), value);
			handler.totalResult(map);
		}

		@Override
		protected void fail(int failReason, long time) {
			if (result == null) {
				result = new PBMQOS[2];
				result[0] = new PBMQOS();
				result[1] = new PBMQOS();
			}
			fail(failReason, failReason == RcuEventCommand.FailReason.NETWORK_NO_MATCH.getReasonCode()
					? RcuEventCommand.FailReason.NETWORK_NO_MATCH.getResonStr() : "Unknown", time);
		}
	}

	/**
	 * 发送PBM下行带宽给iPack端
	 * @param pbmDlBandwidth
	 */
	private void sendPBMDLBandwidth2IPack(int pbmDlBandwidth) {
		Intent intent = new Intent(BROADCAST_PBM_DOWN_BANDWIDTH);
		intent.putExtra(EXTRA_PBM_DOWN_BANDWIDTH, pbmDlBandwidth);
		sendBroadcast(intent);
	}
	/**
	 * 发送PBM上行带宽给iPack端
	 * @param pbmDlBandwidth
	 */
	private void sendPBMULBandwidth2IPack(int pbmDlBandwidth) {
		Intent intent = new Intent(BROADCAST_PBM_UP_BANDWIDTH);
		intent.putExtra(EXTRA_PBM_UP_BANDWIDTH, pbmDlBandwidth);
		sendBroadcast(intent);
	}

	/**
	 * 监控当前网络状态的线程
	 * 
	 * @author jianchao.wang
	 * 
	 */
	private class NetTypeThread extends Thread {
		/** 是否停止线程 */
		private boolean isStop = false;

		@Override
		public void run() {
			int lastNetType = dataTestHandler.netWorkType;
			while (!isStop) {
				if (dataTestHandler.netWorkType > 0 && dataTestHandler.netWorkType != lastNetType) {
					StringBuffer sb = new StringBuffer();
					sb.append("network_type::").append(dataTestHandler.netWorkType);
					dataTestHandler.sendCommand(PBM_NETWORK_CHANGE, sb.toString());
					lastNetType = dataTestHandler.netWorkType;
				}
				dataTestHandler.getNetWorkType(true);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					break;
				}
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(tag, "-----onDestroy-----");
		if (netTypeThread != null) {
			netTypeThread.stopThread();
			netTypeThread = null;
		}
		// 因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
		UtilsMethod.killProcessByPname("com.walktour.service.test.PBMTest", false);
		UtilsMethod.killProcessByPname(AppFilePathUtil.getInstance().getAppLibFile("datatests_android").getAbsolutePath(), false);
	}

	/**
	 * PBM测试品质
	 * 
	 * @author jianchao.wang
	 * 
	 */
	private class PBMQOS {
		/** 测试时间 (ms) */
		private int measureTime;
		/** 带宽(bps) */
		private int bandwidth;
		/** 最大带宽(bps) */
		private int bandwidthMax;
		/** 最小带宽(bps) */
		private int bandwidthMin;
		/** 时延 (ms) */
		private int delay;
		/** 包数 */
		private int pkgs;
		/** 丢包数 */
		private int lostPkgs;
		/** 丢包率(已经*100) */
		private int lostFraction;
		/** 包间隔(ms) */
		private int pkgGap;
		/** 服务器时延 */
		private int serverDelay;
		/** 往返时延 */
		private int roundTripDelay;
		/** 方向 */
		private int direction = TaskPBMModel.DIRECTION_DOWN;

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("measureTime =" + measureTime + "\r\n");
			sb.append("bandwidth =" + bandwidth + "\r\n");
			sb.append("bandwidthMax =" + bandwidthMax + "\r\n");
			sb.append("bandwidthMin =" + bandwidthMin + "\r\n");
			sb.append("delay =" + delay + "\r\n");
			sb.append("pkgs =" + pkgs + "\r\n");
			sb.append("lostPkgs =" + lostPkgs + "\r\n");
			sb.append("lostFraction =" + lostFraction + "\r\n");
			sb.append("pkgGap =" + pkgGap + "\r\n");
			sb.append("serverDelay =" + serverDelay + "\r\n");
			sb.append("roundTripDelay =" + roundTripDelay + "\r\n");
			sb.append("direction =" + direction + "\r\n");
			return sb.toString();
		}
	}

}
