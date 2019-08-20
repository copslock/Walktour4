package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Message;

import com.dingli.service.test.ipc2msg;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.DataTaskValue;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.task.iperf.TaskIperfModel;
import com.walktour.service.TestService;

import java.util.HashMap;
import java.util.Map;

/**
 * Iperf业务测试
 * 
 * @author jianchao.wang
 *
 */
@SuppressLint("SdCardPath")
public class IperfTest extends TestTaskService {
	private static final int IPERF_TEST = 70;

	private static final int IPERF_INITED = 1;
	private static final int IPERF_CONNECT_START = 2;
	private static final int IPERF_CONNECT_SUCCESS = 3;
	private static final int IPERF_CONNECT_FAILED = 4;
	private static final int IPERF_UP_QOS_ARRIVED = 8;
	private static final int IPERF_DOWN_QOS_ARRIVED = 9;
	private static final int IPERF_STATUS_REPORT = 10;
	private static final int IPERF_DROP = 20;
	private static final int IPERF_FINISH = 21;
	private static final int IPERF_QUIT = 22;
	private static final int IPERF_ERROR = 23;
	private static final int IPERF_START_TEST = 1002;
	private static final int IPERF_STOP_TEST = 1006;

	private long startConnectTime = 0;
	// /** 传输大小 (Byte) */
	// private long transByteUp = 0;
	/** 上行品质参数序号 */
	private int indexUp = 0;
	// /** 当前速率(bps) */
	// protected long avgRateUp = 0;
	// /** 峰值(bps) */
	// private long peakValueUp = 0;
	/** 当前上行速率 (bps) */
	private double currentSpeedUp = 0;
	/** 当前上行有效速率合计 */
	private long sumUpSpeed = 0;
	/** 当前上行有效速率次数 */
	private int upCounts = 0;
	/** 当次回调的大小(Byte) */
	private long currentBytesUp = 0;
	// /** 上行抖动 */
	// private long jitterUp = 0;
	// /** 传输大小 (Byte) */
	// private long transByteDown = 0;
	/** 下行品质参数序号 */
	private int indexDown = 0;
	// /** 当前速率(bps) */
	// private long avgRateDown = 0;
	// /** 峰值(bps) */
	// private long peakValueDown = 0;
	/** 当前速率 (bps) */
	private double currentSpeedDown = 0;
	/** 当前下行有效速率合计 */
	private long sumDownSpeed = 0;
	/** 当前下行有效速率次数 */
	private int downCounts = 0;
	/** 当次回调的大小(Byte) */
	private long currentBytesDown = 0;
	/** 下行抖动 */
	private double jitterDown = 0;
	/** 下行丢包数 */
	private int lostPkgsDown = 0;
	/** 下行包数 */
	private int pkgsDown = 0;
	/** 上行包数 */
	private int pkgsUp = 0;
	/** 记录数据的序号 */
	private int pointIndex = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		tag = "IperfTest";
		LogUtil.i(tag, "onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
		// UtilsMethod.killProcessByPname( "com.walktour.service.test.HttpDown",
		// false );
		UtilsMethod.killProcessByPname(AppFilePathUtil.getInstance().getAppLibFile("datatests_android").getAbsolutePath(), false);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.d(tag, "onStart");
		int startFlag = super.onStartCommand(intent, flags, startId);
		if (taskModel instanceof TaskIperfModel) {
			dataTestHandler = new IperfHandler((TaskIperfModel) taskModel);
		}

		// 启动已经实例化handler的数据业务
		if (dataTestHandler != null) {
			dataTestHandler.startTest();
		}

		return startFlag;
	}

	// used to update ui
	@SuppressLint("HandlerLeak")
	private class IperfHandler extends DataTestHandler {
		private TaskIperfModel iperfmodel;

		// //在非root权限下调用
		public IperfHandler(TaskIperfModel iperfmodel) {
			super("-m iperf -z " + AppFilePathUtil.getInstance().getAppConfigDirectory(),
					AppFilePathUtil.getInstance().getAppLibFile(useRoot ? "datatests_android" : "libdatatests_so.so").getAbsolutePath(),
					IPERF_TEST, IPERF_START_TEST, IPERF_STOP_TEST);
			this.iperfmodel = iperfmodel;
		}

		public void handleMessage(android.os.Message msg) {
			ipc2msg aMsg = (ipc2msg) msg.obj;
			if (aMsg.test_item != IPERF_TEST) {
				return;
			}
			hasEventCallBack = true; // 设置业务库有事件回调
			LogUtil.d(tag, "--msgId:" + aMsg.event_id + "--msgStr:" + aMsg.data);

			switch (aMsg.event_id) {
			case IPERF_INITED:
				StringBuilder event_data = new StringBuilder();
				event_data.append("version::").append(2).append("\n");
				event_data.append("local_if::").append("").append("\n");
				event_data.append("remote_addr::").append(iperfmodel.getRemoteAddr()).append("\n");
				event_data.append("remote_port_min::").append("5001").append("\n");
				event_data.append("remote_port_max::").append("5001").append("\n");
				event_data.append("protocol::").append(iperfmodel.getProtocol()).append("\n");
				event_data.append("direction::").append(iperfmodel.getDirection()).append("\n");
				event_data.append("duration::").append(iperfmodel.getDuration()).append("\n");
				event_data.append("udp_band_width::").append(iperfmodel.getUdpBandWidth()).append("\n");
				event_data.append("udp_buff_size::").append(iperfmodel.getUdpBuffSize()).append("\n");
				event_data.append("udp_packet_size::").append(iperfmodel.getUdpPacketSize()).append("\n");
				event_data.append("auth_type::").append("1").append("\n"); // telnet
				event_data.append("auth_host_addr::").append(iperfmodel.getTelnetAddr()).append("\n");
				event_data.append("auth_host_port::").append(iperfmodel.getTelnetPort()).append("\n");
				event_data.append("user_name::").append(iperfmodel.getUserName()).append("\n");
				event_data.append("password::").append(iperfmodel.getPassword()).append("\n");
				event_data.append("iperf_path::").append("").append("\n");
				event_data.append("auth_enable::").append(1);

				LogUtil.d(tag, event_data.toString());
				sendStartCommand(event_data.toString());
				writeRcuEvent(RcuEventCommand.Iperf_Initialized, aMsg.getRealTime());
				showEvent("iPerf Initialized");
				break;
			case IPERF_CONNECT_START: // start Telent
				LogUtil.d(tag, "iperf connect start, userRoot = " + (useRoot == true ? "yes" : "No"));
				// iPerf Connect Start：TCP，Send，16.112.11.3
				String[] protocolArray = { "TCP", "UDP" };
				String[] directionArray = { "Send", "Receive", "Full Duplex" };
				showEvent("iPerf Connect Start:" + protocolArray[iperfmodel.getProtocol()] + ","
						+ directionArray[iperfmodel.getDirection()] + "," + iperfmodel.getRemoteAddr());
				// writeRcuEvent(RcuEventCommand.Iperf_Connect_Start);
				byte[] ipaddr = new byte[60];
				System.arraycopy(iperfmodel.getRemoteAddr().getBytes(), 0, ipaddr, 0, iperfmodel.getRemoteAddr().length());
				EventBytes.Builder(mContext, RcuEventCommand.Iperf_Connect_Start).addInteger(iperfmodel.getProtocol())
						.addInteger(iperfmodel.getDirection()).addBytes(ipaddr).writeToRcu(aMsg.getRealTime());
				startConnectTime = aMsg.getRealTime();
				break;
			case IPERF_CONNECT_SUCCESS: // connect Telnet success and start iperf succ
				LogUtil.d(tag, "iperf connect success");
				firstDataTime = System.currentTimeMillis();
				showEvent("iPerf Connect Success:Delay " + (aMsg.getRealTime() - startConnectTime) / 1000);
				writeRcuEvent(RcuEventCommand.Iperf_Connect_Success, aMsg.getRealTime());
				break;
			case IPERF_CONNECT_FAILED:
				LogUtil.d(tag, "iperf connect failed,msg = " + aMsg.data);
				int failReason = 0;
				try {
					failReason = getReason(aMsg.data.split("\n")[0]);
					// showEvent("Iperf Connect Failed,Reasone:" + aMsg.data);
				} catch (Exception e) {

				}
				showEvent("iPerf Connect Failure:Reason Unknown");

				fail(failReason, aMsg.getRealTime());
				break;
			case IPERF_UP_QOS_ARRIVED:
				LogUtil.d(tag, "iperf up qos arrived,msg = " + aMsg.data);
				String[] qos = aMsg.data.split("\n");
				indexUp = Integer.parseInt(qos[0].split("::")[1]);
				currentBytesUp = Long.parseLong(qos[1].split("::")[1]);
				currentSpeedUp = (long) Double.parseDouble(qos[2].split("::")[1]);
				pointIndex++;
				if (currentSpeedUp > 0) {
					sumUpSpeed += currentSpeedUp;
					upCounts++;
				}
				sendCurrentRate(true);
				break;
			case IPERF_DOWN_QOS_ARRIVED:
				LogUtil.d(tag, "iperf down qos arrived,msg = " + aMsg.data);
				qos = aMsg.data.split("\n");
				indexDown = Integer.parseInt(qos[0].split("::")[1]);
				currentBytesDown = Long.parseLong(qos[1].split("::")[1]);
				currentSpeedDown = Double.parseDouble(qos[2].split("::")[1]);
				jitterDown = Double.parseDouble(qos[3].split("::")[1]);
				lostPkgsDown = Integer.parseInt(qos[4].split("::")[1]);
				pkgsDown = Integer.parseInt(qos[5].split("::")[1]);
				pointIndex++;
				if (currentSpeedDown > 0) {
					sumDownSpeed += currentSpeedDown;
					downCounts++;
				}
				sendCurrentRate(false);
				break;
			case IPERF_STATUS_REPORT:
				LogUtil.d(tag, "iperf status report:" + aMsg.data);
				qos = aMsg.data.split("\n");
				EventBytes.Builder(getBaseContext()).addInteger(pointIndex).addCharArray(qos[0].toCharArray(), 1000)
						.writeToRcu(WalkCommonPara.MsgDataFlag_Y);
				break;
			case IPERF_DROP:
				LogUtil.d(tag, "iperf drop,msg = " + aMsg.data);
				int reason = -1;
				try {
					qos = aMsg.data.split("\n");
					reason = Integer.parseInt(qos[0].split("::")[1]);
				} catch (Exception e) {

				}
				showEvent("iPerf Drop:Reason Unknown");
				// showEvent(aMsg.data);
				drop(reason, aMsg.getRealTime());

				this.stopProcess(TestService.RESULT_FAILD);
				break;
			case IPERF_FINISH:
				LogUtil.d(tag, "iperf finish, msg = " + aMsg.data);
				if (!hasLastData) {
					hasLastData = true;

					qos = aMsg.data.split("\n");
					long upBytesTotal = Long.parseLong(qos[0].split("::")[1]);
					double upSpeedAvg = Double.parseDouble(qos[1].split("::")[1]);
					double upJitterAvg = Double.parseDouble(qos[2].split("::")[1]);
					int upLostPkgsTotal = Integer.parseInt(qos[3].split("::")[1]);
					int upPkgsTotal = Integer.parseInt(qos[4].split("::")[1]);

					long downBytesTotal = Long.parseLong(qos[5].split("::")[1]);
					double downSpeedAvg = Double.parseDouble(qos[6].split("::")[1]);
					double downJitterAvg = Double.parseDouble(qos[7].split("::")[1]);
					int downLostPkgsTotal = Integer.parseInt(qos[8].split("::")[1]);
					int downPkgsTotal = Integer.parseInt(qos[9].split("::")[1]);
					String finish = "iPerf Finished:RTT -9999ms,Send " + upBytesTotal / (UtilsMethod.kbyteRage * 8) + "KBytes,"
							+ upSpeedAvg / UtilsMethod.kbyteRage + "kbps," + " Packer Total " + upPkgsTotal + "," + " Received "
							+ downBytesTotal / (UtilsMethod.kbyteRage * 8) + "KBytes," + downSpeedAvg / UtilsMethod.kbyteRage
							+ "kbps," + " Jitter delay " + downJitterAvg + "ms," + " Packet loss " + downPkgsTotal;
					int RTT = -9999;
					// iPerf Finished：RTT 20ms，Send 21112Kbytes，211kbps，Packer Total
					// 12122，Receive 421122Kbytes，3122kbps，Jitter delay 212ms， Packet loss
					// 1221，Packet Total 4221
					EventBytes.Builder(mContext, RcuEventCommand.Iperf_Finished).addInteger(iperfmodel.getDirection())
							.addInteger(RTT).addInt64(upBytesTotal).addDouble(upSpeedAvg).addInteger(upPkgsTotal)
							.addInt64(downBytesTotal).addDouble(downSpeedAvg).addDouble(downJitterAvg).addInteger(downLostPkgsTotal)
							.addInteger(downPkgsTotal).addDouble(upJitterAvg).addInteger(upLostPkgsTotal)
							.writeToRcu(aMsg.getRealTime());

					showEvent(finish);
					LogUtil.i(tag, finish);
					// 结束本次测试
					this.stopProcess(TestService.RESULT_SUCCESS);
				}
				break;
			case IPERF_QUIT:
				LogUtil.d(tag, "iperf quit");
				showEvent("iPerf Quit");
				writeRcuEvent(RcuEventCommand.Iperf_Quit, aMsg.getRealTime());
				break;
			case IPERF_ERROR:
				LogUtil.d(tag, "iperf error,msg = " + aMsg.data);
				qos = aMsg.data.split("\n");
				try {
					reason = (int) Long.parseLong(qos[0].split("::")[1]);
					String desc = qos[1].split("::")[1];
					showEvent("iPerf Error:" + desc);
				} catch (Exception e) {
					showEvent("iPerf Error:Unkown");
				}

				this.stopProcess(TestService.RESULT_FAILD);
				break;
			}
		}

		@Override
		protected void prepareTest() {

		}

		@Override
		protected void fail(int failReason, long time) {
			// 结束本次测试
			if (!hasFail && firstDataTime == 0 && !hasDrop && !hasLastData) {
				hasFail = true;

				writeRcuEvent(RcuEventCommand.Iperf_Connect_Failure, time, failReason);
				this.stopProcess(TestService.RESULT_FAILD);
			}
		}

		@Override
		protected void drop(int dropReason, long time) {
			if (!hasDrop && !hasFail && firstDataTime != 0 && !hasLastData) {
				hasDrop = true;
				EventBytes.Builder(mContext, RcuEventCommand.Iperf_Drop).addInteger(dropReason)
						.addInteger(iperfmodel.getDirection()).addInteger(0).addInt64(0).addDouble(0).addInteger(0).addInt64(0)
						.addDouble(0).addDouble(0).addInteger(0).addInteger(0).addDouble(0).addInteger(0).writeToRcu(time);

				this.stopProcess(TestService.RESULT_SUCCESS);
			}
		}

		/**
		 * 发送当前速率到界面显示
		 * 
		 * @param isUp
		 */
		private void sendCurrentRate(boolean isUp) {
			if (firstDataTime != 0) {
				// 仪表盘的速率
				Map<String, Object> dataMap = new HashMap<String, Object>();
				double avg = 0;
				if (isUp)
					avg = sumUpSpeed / upCounts / UtilsMethod.kbyteRage;
				else
					avg = sumDownSpeed / downCounts / UtilsMethod.kbyteRage;
				if (!isUp)
					dataMap.put(WalkStruct.DataTaskValue.HttpDlThrput.name(), UtilsMethod.decFormat.format(avg));
				dataMap.put(DataTaskValue.BordLeftTitle.name(), getString(R.string.info_data_averagerate) + "@"
						+ UtilsMethod.decFormat.format(avg) + getString(R.string.info_rate_kbps) + (isUp ? "↑" : "↓"));
				if (isUp)
					dataMap.put(DataTaskValue.BordCurrentSpeed.name(),
							UtilsMethod.decFormat.format(currentSpeedUp / UtilsMethod.kbyteRage));
				else
					dataMap.put(DataTaskValue.BordCurrentSpeed.name(),
							UtilsMethod.decFormat.format(currentSpeedDown / UtilsMethod.kbyteRage));
				Message msg = callbackHandler.obtainMessage(DATA_CHANGE, dataMap);
				callbackHandler.sendMessage(msg);
				// 写入Iperf_data数据格式
				int direction = this.iperfmodel.getDirection();
				switch (direction) {
				case 0:
					direction = 2;
					break;
				case 1:
					direction = 1;
					break;
				default:
					direction = 3;
					break;
				}
				double packTime = 0;
				EventBytes.Builder(getBaseContext()).addInteger(direction).addInteger(pointIndex).addDouble(packTime)
						.addInteger(indexDown).addInt64(currentBytesDown).addDouble(currentSpeedDown).addDouble(jitterDown)
						.addInteger(lostPkgsDown).addInteger(pkgsDown).addInteger(indexUp).addInt64(currentBytesUp)
						.addDouble(currentSpeedUp).addInteger(pkgsUp).writeToRcu(WalkCommonPara.MsgDataFlag_R);
			}
		}

		@Override
		protected void sendCurrentRate() {
		}

		@Override
		protected void lastData(long time) {

		}

	}

}
