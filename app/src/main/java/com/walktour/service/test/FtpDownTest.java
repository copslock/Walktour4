package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.util.Log;

import com.dingli.service.test.ipc2msg;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.RcuEventCommand.FailReason;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TotalStruct.TotalFtp;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct.DataTaskValue;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigFtp;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.task.ftp.TaskFtpModel;
import com.walktour.model.FtpServerModel;
import com.walktour.service.TestService;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * FTP下载测试业务
 * 
 * @author jianchao.wang
 *
 */
@SuppressLint({ "SdCardPath", "HandlerLeak" })
public class FtpDownTest extends FtpBaseTest {

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(tag, "onDestroy");
		// 因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
		UtilsMethod.killProcessByPname("com.walktour.service.test.FtpDownTest", false);
		UtilsMethod.killProcessByPname(AppFilePathUtil.getInstance().getAppLibFile("datatests_android").getAbsolutePath(), false);
	}

	private class FtpDownHandler extends DataTestHandler {

		/**
		 * ftp_down_item.h
		 */
		private final int FTP_DOWN_INITED 			= 1;
		private final int FTP_DOWN_CONNECT_START 		= 2;
		private final int FTP_DOWN_CONNECT_SUCCESS 	= 3;
		private final int FTP_DOWN_CONNECT_FAILED 	= 4;
		private final int FTP_DOWN_LOGIN_SUCCESS 		= 5;
		private final int FTP_DOWN_LOGIN_FAILED 		= 6;
		private final int FTP_DOWN_DOES_SUPPORT_REST 	= 7;
		private final int FTP_DOWN_FILE_SIZE 			= 8;
		private final int FTP_DOWN_SEND_RETR 			= 9;
		private final int FTP_DOWN_FIRSTDATA 			= 10;
		private final int FTP_DOWN_QOS_ARRIVED 		= 11;
		private final int FTP_DOWN_ERROR 				= 14;
		private final int FTP_DOWN_DROP 				= 15;
		private final int FTP_DOWN_FINISH 			= 16;
		private final int FTP_DOWN_QUIT 				= 17;
		private final int FTP_DOWN_FAILED 			= 18;
		private final int FTP_DOWN_NODATA_NEED_PING 	= 23;

		private final int FTP_DOWN_DATA_CONNECT_OK	= 25;	//数据连接成功
		private final int FTP_DOWN_TCPSLOW_END		= 26;	//TCP慢启动结束
		private final int FTP_DOWN_DNSRESOLVE_START	= 27;	//DNS解析开始
		private final int FTP_DOWN_DNSRESOLVE_SUCCESS	= 28;	//DNS解析结束
		private final int FTP_DOWN_DNSRESOLVE_FAILED	= 29;	//DNS解析失败

		private final int FTP_DOWN_MSG = 101;
		private final int FTP_DOWN_TCPIP_DIAGNOSE 					= 110;//业务故障时TCPIP诊断信息

		private static final int FTP_DOWN_TEST = 65;
		private static final int FTP_DOWN_START_TEST = 1001;
		private static final int FTP_DOWN_STOP_TEST = 1006;

		private TaskFtpModel ftpModel;
		private FtpServerModel ftpServerModel;

		/**
		 * 最高下载速率(bps)
		 */
		private long downMaxValue;

		/**
		 * 最低下载速率(bps)
		 */
		private long downMinValue = -1;

		public FtpDownHandler(TaskFtpModel ftpModel) {
			super("-m ftp_down -z "+ AppFilePathUtil.getInstance().getAppConfigDirectory(),
					AppFilePathUtil.getInstance().getAppLibFile(useRoot?"datatests_android":"libdatatests_so.so").getAbsolutePath(),
					FTP_DOWN_TEST, FTP_DOWN_START_TEST, FTP_DOWN_STOP_TEST);
			this.ftpModel = ftpModel;
		}

		@Override
		public void handleMessage(android.os.Message msg) {
			ipc2msg aMsg = (ipc2msg) msg.obj;
			if (aMsg.test_item != FTP_DOWN_TEST) {
				return;
			}
			hasEventCallBack = true;	//设置业务库有事件回调
			LogUtil.w(tag, "--msgId:" + aMsg.event_id + "--msgStr:" + aMsg.data );

			switch (aMsg.event_id) {
				case FTP_DOWN_QOS_ARRIVED:
					// 05-13 01:27:19.484: I/FtpTest(2264): measure_time::88005 (ms)
					// 05-13 01:27:19.484: I/FtpTest(2264): down_bytes::4195656 (byte)
					// 05-13 01:27:19.484: I/FtpTest(2264): down_progress::0
					// 05-13 01:27:19.484: I/FtpTest(2264): down_speed_max::991936.00
					// 05-13 01:27:19.484: I/FtpTest(2264): down_speed_min::0.00
					// 05-13 01:27:19.484: I/FtpTest(2264): down_speed_avg::381401.00 (bps)
					// 05-13 01:27:19.484: I/FtpTest(2264): down_bytes_cur::95760 (byte)
					// 05-13 01:27:19.484: I/FtpTest(2264): down_speed_cur::766080.00 (bps)


					String[] qos = aMsg.data.split("\n");
					delayTime = Long.parseLong(qos[0].split("::")[1]);
					transByte = Long.parseLong(qos[1].split("::")[1]);
					progress = (long) Double.parseDouble(qos[2].split("::")[1]);
					peakValue = (long) Double.parseDouble(qos[3].split("::")[1]);
					avgRate = (long) Double.parseDouble(qos[5].split("::")[1]);
					currentBytes = Long.parseLong(qos[6].split("::")[1]);
					currentSpeed = (long) Double.parseDouble(qos[7].split("::")[1]);
					activeThreadNum = Integer.parseInt(qos[8].split("::")[1]);
					sendCurrentRate();

					break;

				case FTP_DOWN_INITED:
					StringBuilder sb = new StringBuilder();
					sb.append("local_if::").append("").append("\n");
//					sb.append("local_if::").append("rmnet_data0").append("\n");
					sb.append("play_time_ms::").append(ftpModel.getTimeOut() * 1000).append("\n");
					sb.append("ps_call::").append(ftpModel.getPsCall() == 1 ? "1" : "0").append("\n");
					sb.append("nodata_timeout_ms::").append(ftpModel.getNoAnswer() * 1000).append("\n");
					sb.append("qos_inv_ms::").append(1000).append("\n");
					sb.append("serv_type::").append(ftpModel.getTransportProtocal()==1?2:0).append("\n");//注意传输类型0-ftp服务器  2-sftp服务器
					sb.append("serv_host::").append(ftpServerModel.getIp()).append("\n");
					sb.append("serv_port::").append(ftpServerModel.getPort()).append("\n");
					sb.append("user_name::").append(ftpServerModel.getLoginUser()).append("\n");
					sb.append("password::").append(ftpServerModel.getLoginPassword()).append("\n");
					sb.append("anonymous::").append(ftpServerModel.isAnonymous() ? "1" : "0").append("\n");
					// 连接模式 1:PASSIVE 2:PORT(如连不上会自动切模式再连, 默认值1)
					sb.append("trans_mode::").append(ftpServerModel.getConnect_mode()).append("\n");
					sb.append("serv_filename::").append(ftpModel.getRemoteFile()).append("\n");
					sb.append("data_mode::").append("1").append("\n");
					sb.append("do_save::").append("0").append("\n");
					sb.append("tcpip_diagnose::").append("0").append("\n");
					sb.append("save_filepath::").append(downloadPath).append("\n");
					sb.append("thread_count::").append(ftpModel.getThreadNumber()).append("\n");
					sb.append("thread_mode::").append(ftpModel.getThreadMode()).append("\n");
					sb.append("conn_reconn_count::").append(ftpModel.getLoginTimes()).append("\n");
					sb.append("nodata_active_ms::").append(20000).append("\n");
					sb.append("lowspeed_reactive::").append(0).append("\n");
					sb.append("sendbuff_size::").append(ftpModel.getSendBuffer()).append("\n");
					sb.append("recvbuff_size::").append(ftpModel.getReceiveBuffer()).append("\n");
					sb.append("network_type::").append("8");
					LogUtil.w(tag, sb.toString());
					this.sendStartCommand(sb.toString());
					break;

				case FTP_DOWN_CONNECT_START:
					connectTime = aMsg.getRealTime();
					// 存储事件
					EventBytes.Builder(mContext, RcuEventCommand.FTP_DL_CtrlSockConnecting).addInteger(RcuEventCommand.FTP_TYPE_FTP)
							.addStringBuffer(ftpModel.getRemoteFile()).addStringBuffer(ftpServerModel.getIp())
							.writeToRcu(aMsg.getRealTime());
					// 显示事件
					showEvent("FTP Download Connect");
					break;

				case FTP_DOWN_CONNECT_SUCCESS:
					connectedTime = aMsg.getRealTime();
					// 存储事件
					EventBytes.Builder(mContext, RcuEventCommand.FTP_DL_ConnectSockSucc)
							.addInteger((int) (connectedTime - connectTime) / 1000).writeToRcu(aMsg.getRealTime());
					// 显示事件rmnet_data0
					showEvent("FTP Download Connect Success");
					break;

				case FTP_DOWN_CONNECT_FAILED:
					// 05-24 11:14:48.550: I/FtpDown(5530): FTP_DOWN_CONNECT_START
					// 05-24 11:16:20.615: I/FtpDown(5530): recv FTP_DOWN_CONNECT_FAILED
					// 05-24 11:16:20.615: I/FtpDown(5530): reason::-3
					connectFail(aMsg.getRealTime());
					break;

				case FTP_DOWN_FAILED:
					// 07-22 15:02:22.245 I/FtpTest (13755): recv MFTP_UP_FAILED
					// 07-22 15:02:22.245 I/FtpTest (13755): reason::1025
					// 07-22 15:02:22.245 I/FtpTest (13755): desc::STOR response timeout
					// 07-22 15:02:22.245 I/FtpTest (13755):
					int failCode = getReason(aMsg.data.split("\n")[0]);
					fail(failCode, aMsg.getRealTime());
					break;

				case FTP_DOWN_LOGIN_SUCCESS:
					// 存储事件
					EventBytes.Builder(mContext, RcuEventCommand.FTP_DL_LoginSuccess)
							.addInteger((int) (aMsg.getRealTime() - connectedTime) / 1000).writeToRcu(aMsg.getRealTime());
					// 显示事件
					showEvent("FTP Download Login Success");
					break;

				case FTP_DOWN_LOGIN_FAILED:
					// 07-16 11:30:03.508: I/FtpTest(12435): recv FTP_DOWN_LOGIN_FAILED
					// 07-16 11:30:03.508: I/FtpTest(12435): reason::1
					int loginFailReason = getReason(aMsg.data);
					loginFail(loginFailReason, aMsg.getRealTime());
					break;

				case FTP_DOWN_DOES_SUPPORT_REST:
					// 05-24 11:41:47.515: I/FtpDown(8109): recv FTP_DOWN_DOES_SUPPORT_REST
					// 05-24 11:41:47.515: I/FtpDown(8109): support::1
					int s = getReason(aMsg.data);
					if (s == 1) {
						// 存储事件
						EventBytes.Builder(mContext, RcuEventCommand.FTP_DL_Support_REST).writeToRcu(aMsg.getRealTime());
					}
					break;

				case FTP_DOWN_FILE_SIZE:
					try {
						fileSize = Long.parseLong(aMsg.data.split("::")[1]);
					} catch (Exception e) {
						LogUtil.e(tag, e.toString());
					}
					break;

				case FTP_DOWN_SEND_RETR:
					sendGetTime = aMsg.getRealTime();
					// 存储事件
					EventBytes.Builder(mContext, RcuEventCommand.FTP_DL_SendRetrCmd).addInt64(fileSize)
							.writeToRcu(aMsg.getRealTime());
					// 显示事件
					showEvent(String.format(Locale.getDefault(),"FTP Download Send RETR:%.2f(KByte)", fileSize / UtilsMethod.kbyteRage));
					break;

				case FTP_DOWN_FIRSTDATA:
					firstDataTime = aMsg.getRealTime();
					// 存储事件
					EventBytes.Builder(mContext, RcuEventCommand.FTP_DL_FirstData).writeToRcu(aMsg.getRealTime());

					// 设置主进程中的firstdata状态
					setMainFirstDataState(true);

					// 显示事件
					showEvent("FTP Download First Data");
					break;

				case FTP_DOWN_ERROR:
					// 05-24 11:41:47.990: I/FtpDown(8109): recv FTP_DOWN_ERROR
					// 05-24 11:41:47.990: I/FtpDown(8109): err_id::65569
					// 05-24 11:41:47.990: I/FtpDown(8109): err_desc::server file not exist
					int failReason = getReason(aMsg.data.split("\n")[0]);
					fail(failReason, aMsg.getRealTime());
					break;

				case FTP_DOWN_DROP:
					drop(getReason(aMsg.data), aMsg.getRealTime());
					break;

				case FTP_DOWN_FINISH:
					lastDataTime = aMsg.getRealTime();
					lastData(aMsg.getRealTime());
					break;

				case FTP_DOWN_NODATA_NEED_PING:
					break;

				case FTP_DOWN_MSG:
					// 05-16 10:46:09.098: I/FtpTest(6632): type::1
					// 05-16 10:46:09.098: I/FtpTest(6632): level::3
					// 05-16 10:46:09.098: I/FtpTest(6632): code::1120
					// 05-16 10:46:09.098: I/FtpTest(6632): msg::login failed
					// 05-16 10:46:09.098: I/FtpTest(6632): context::Request:PASS suuzmie
					// 05-16 10:46:09.098: I/FtpTest(6632): . Response:530 Login or password
					// incorrect!
					try {
						String context = aMsg.data.substring(aMsg.data.indexOf("context::") + 9, aMsg.data.length());
						EventBytes.Builder(mContext, RcuEventCommand.DataServiceMsg)
								.addInteger(Integer.parseInt(getResponseResult(aMsg.data, "type")))
								.addInteger(Integer.parseInt(getResponseResult(aMsg.data, "level")))
								.addInteger(Integer.parseInt(getResponseResult(aMsg.data, "code")))
								.addStringBuffer(getResponseResult(aMsg.data, "msg")).addStringBuffer(context)
								.writeToRcu(aMsg.getRealTime());
					} catch (Exception e) {
						LogUtil.w(tag, "FTP_DOWN_MSG", e);
					}
					break;

				case FTP_DOWN_QUIT:
					new Thread() {
						public void run() {

							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							stopProcess(TestService.RESULT_SUCCESS);
						}
					}.start();
					break;

				case FTP_DOWN_DATA_CONNECT_OK:		//数据连接成功
					break;
				case FTP_DOWN_TCPSLOW_END:			//TCP慢启动结束
					if(!StringUtil.isEmpty(aMsg.data)) {
						qos = aMsg.data.split("\n");
						long val1 = Long.parseLong(qos[0].split("::")[1]);
						int val2 = Integer.parseInt(qos[1].split("::")[1]);

						EventBytes.Builder(mContext, RcuEventCommand.FTP_DL_Tcpslow)
								.addInt64(val1)
								.addInteger(val2)
								.writeToRcu(aMsg.getRealTime());
					}
					break;
				case FTP_DOWN_TCPIP_DIAGNOSE:
					break;
				case FTP_DOWN_DNSRESOLVE_START:		//DNS解析开始
					EventBytes.Builder(mContext, RcuEventCommand.DNS_LOOKUP_START )
							.addInteger( RcuEventCommand.DNS_TEST_TYPE_OTHER )
							.addInteger(  RcuEventCommand.TEST_TYPE_FTP_DOWNLOAD )
							.addStringBuffer(getResponseResult(aMsg.data, "host"))
							.writeToRcu(aMsg.getRealTime());

					break;
				case FTP_DOWN_DNSRESOLVE_SUCCESS:	//DNS解析结束
					EventBytes.Builder(mContext,RcuEventCommand.DNS_LOOKUP_SUCCESS )
							.addCharArray( getResponseResult(aMsg.data, "host").toCharArray(), 256)
							.addInteger(UtilsMethod.convertIpString2Int(getResponseResult(aMsg.data, "ip")) )
							.addInteger(Integer.parseInt(getResponseResult(aMsg.data, "delay",true)))
							.addInteger(UtilsMethod.convertIpString2Int(getResponseResult(aMsg.data, "dns_server").split(",")[0]))
							.writeToRcu(aMsg.getRealTime());
					break;
				case FTP_DOWN_DNSRESOLVE_FAILED:	//DNS解析失败

					EventBytes.Builder(mContext,  RcuEventCommand.DNS_LOOKUP_FAILURE )
							.addCharArray( getResponseResult(aMsg.data, "host").toCharArray(), 256)
							.addInteger( Integer.parseInt(getResponseResult(aMsg.data, "delay",true) ))
							.addInteger( Integer.parseInt(getResponseResult(aMsg.data, "reason",true) ))
							.addInteger(UtilsMethod.convertIpString2Int(getResponseResult(aMsg.data, "dns_server").split(",")[0]))
							.writeToRcu(aMsg.getRealTime());

					break;
			}
		}

		/**
		 * 连接失败,不用写入和显示原因码
		 *
		 * @param time
		 */
		private void connectFail(long time) {
			if (!hasFail && connectTime != 0) {
				hasFail = true;

				// 存储事件
				EventBytes.Builder(mContext, RcuEventCommand.FTP_DL_ConnectSockFailed)
						.addInteger((int) (time - connectTime) / 1000).writeToRcu(time);
				// 显示事件
				showEvent("FTP Download Connect Failure");

				// 结束本次测试
				this.stopProcess(TestService.RESULT_FAILD);
			}
		}

		/**
		 * 登录失败
		 *
		 * @param reason
		 */
		protected void loginFail(int reason, long time) {
			if (!hasFail && connectTime != 0 && connectedTime != 0) {
				hasFail = true;

				// 存储事件
				EventBytes.Builder(mContext, RcuEventCommand.FTP_DL_LoginFailure).addInteger((int) (time - connectTime) / 1000)
						.addInteger(reason).writeToRcu(time);

				// 显示事件
				showEvent(String.format("FTP Download Login Failure:%s", FailReason.getFailReason(reason).getResonStr()));

				// 2014.暂时这样处理Last Data和Disconnect事件在同一采样点和同一时间写入后次序反的问题
				new Thread() {
					public void run() {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						disConnect();

						// 结束本次测试
						stopProcess(TestService.RESULT_FAILD);
					}
				}.start();

			}
		}

		@Override
		public synchronized void fail(int failReasonCode, long time) {
			// fail、drop、lastData三个事件互斥
			if (!hasFail && firstDataTime == 0 && !hasDrop && !hasLastData) {
				hasFail = true;
				// 存储事件
				EventBytes.Builder(mContext, RcuEventCommand.FTP_DL_Failure).addInteger(failReasonCode).writeToRcu(time);
				// 显示事件
				showEvent("FTP Download Failure:" + FailReason.getFailReason(failReasonCode).getResonStr());

				// 2014.暂时这样处理Last Data和Disconnect事件在同一采样点和同一时间写入后次序反的问题
				new Thread() {
					public void run() {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						disConnect();

						// 结束本次测试
						stopProcess(TestService.RESULT_FAILD);
					}
				}.start();
			}
		}

		/**
		 * FTP的drop条件是已经发送SendSETR命令 //fail、drop、lastData三个事件互斥
		 */
		@Override
		public synchronized void drop(int dropReasonCode, long time) {
			// fail、drop、lastData三个事件互斥
			if (!hasDrop && !hasFail && sendGetTime != 0 && !hasLastData) {
				hasDrop = true;

				// 存储
				EventBytes.Builder(mContext, RcuEventCommand.FTP_DL_Drop).addInteger((int) (time - sendGetTime) / 1000000)
						.addInt64(transByte).addInteger(dropReasonCode).writeToRcu(time);

				// 显示
				showEvent(String.format(Locale.getDefault(),"FTP Download Drop:Delay %d(s)," + "Mean Rate:%.2f kbps,Transmit Size:%.2f Kbytes,%s",
						delayTime / 1000, avgRate / UtilsMethod.kbyteRage, transByte / UtilsMethod.kbyteRage,
						getDropReasonString(dropReasonCode)));

				// 统计页面
				if ((dropReasonCode == RcuEventCommand.DROP_USERSTOP || dropReasonCode == RcuEventCommand.DROP_TIMEOUT) && firstDataTime != 0) {
					totalFtpResult(this, TotalFtp._downtrys, 1);
					totalFtpResult(this, TotalFtp._downSuccs, 1);
					totalFtpResult(this, TotalFtp._downDrops, 0);
					totalFtpResult(this, TotalFtp._downCurrentSize, (transByte));
					totalFtpResult(this, TotalFtp._downCurrentTimes, delayTime);
				} else {
					totalFtpResult(this, TotalFtp._downtrys, 1);
					totalFtpResult(this, TotalFtp._downSuccs, 0);
					totalFtpResult(this, TotalFtp._downDrops, 1);
				}

				// 统计参数
				if (dropReasonCode != RcuEventCommand.OUT_OF_SERVICE && dropReasonCode != RcuEventCommand.DROP_PPPDROP) {
					sendTotalFtpPara(true, (int) (lastDataTime - sendGetTime) / 1000);
				}

				// 2014.4.30 暂时这样处理Last Data和Disconnect事件在同一采样点和同一时间写入后次序反的问题
				new Thread() {
					public void run() {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						disConnect();

						// 结束本次测试
						stopProcess(TestService.RESULT_SUCCESS);
					}
				}.start();

			}
		}

		@Override
		public synchronized void lastData(long time) {
			// fail、drop、lastData三个事件互斥
			if (!hasLastData && firstDataTime != 0 && !hasFail && !hasDrop) {
				hasLastData = true;
				// 存储事件
				EventBytes.Builder(mContext, RcuEventCommand.FTP_DL_LastData).addInt64(transByte).addInteger((int) delayTime)
						.writeToRcu(time);
				// 显示事件
				showEvent(
						String.format("FTP Download Last Data:Delay %d(s)," + "Mean Rate:%.2f kbps," + "Transmit Size:%.2f Kbytes",
								delayTime / 1000, avgRate / UtilsMethod.kbyteRage, transByte / UtilsMethod.kbyteRage));

				// 统计页面
				totalFtpResult(this, TotalFtp._downtrys, 1);
				totalFtpResult(this, TotalFtp._downSuccs, 1);
				totalFtpResult(this, TotalFtp._downDrops, 0);
				totalFtpResult(this, TotalFtp._downCurrentSize, (transByte));
				totalFtpResult(this, TotalFtp._downCurrentTimes, delayTime);
				totalFtpResult(this, TotalFtp._down_max_value, downMaxValue);
				totalFtpResult(this, TotalFtp._down_min_value, downMinValue);

				// 统计参数
				sendTotalFtpPara(true, (int) (lastDataTime - sendGetTime) / 1000);

				// 设置主进程中的firstdata状态
				setMainFirstDataState(false);

				// 2014.4.30 暂时这样处理Last Data和Disconnect事件在同一采样点和同一时间写入后次序反的问题
				new Thread() {
					public void run() {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						disConnect();

						// 结束本次测试
						stopProcess(TestService.RESULT_SUCCESS);
					}
				}.start();
			}

		}

		@Override
		protected void prepareTest() {
			ConfigFtp ftp = new ConfigFtp();
			ftpServerModel = ftp.getFtpServerModel(ftpModel.getFtpServerName());
			LogUtil.i(tag, ftpServerModel.getIp());
			resetDownloadDir();
		}

		protected void disConnect() {
			if (connectedTime != 0) {
				// 写事件
				EventBytes.Builder(mContext, RcuEventCommand.FTP_DL_SocketDisconnected)
						.writeToRcu(System.currentTimeMillis() * 1000);
				// 显示
				showEvent("FTP Download Disconnect");
			}

		}

		@Override
		protected void sendCurrentRate() {
			Map<String, Object> dataMap = new HashMap<String, Object>();

			//最高下载速率
			downMaxValue = downMaxValue < currentSpeed ? currentSpeed : downMaxValue;
			//最低下载速率
			downMinValue = downMinValue == -1 ? currentSpeed : downMinValue > currentSpeed ? currentSpeed : downMinValue;
			// 当前速率kbps
			dataMap.put(DataTaskValue.FtpDlThrput.name(),
					UtilsMethod.decFormat.format(currentSpeed / UtilsMethod.kbyteRage));
			dataMap.put(DataTaskValue.BordCurrentSpeed.name(),
					UtilsMethod.decFormat.format(currentSpeed / UtilsMethod.kbyteRage));
			// 平均速率kbps
			dataMap.put(DataTaskValue.FtpDlMeanRate.name(),
					UtilsMethod.decFormat.format(avgRate / UtilsMethod.kbyteRage));
			dataMap.put(DataTaskValue.BordLeftTitle.name(), getString(R.string.info_data_averagerate) + "@"
					+ UtilsMethod.decFormat.format(avgRate / UtilsMethod.kbyteRage) + getString(R.string.info_rate_kbps));

			// 当前活动线程数
			dataMap.put(DataTaskValue.ActiveThreadNum.name(), activeThreadNum);
			// 峰值
			dataMap.put(DataTaskValue.PeakValue.name(), UtilsMethod.decFormat.format(peakValue));
			// 当前进度FtpDlProgress 如果是PS CALL 按时间比例计算
			int p = (int) (ftpModel.getPsCall() == 1 ? (delayTime * 100 / (ftpModel.getTimeOut() * 1000)) : progress);
			dataMap.put(DataTaskValue.FtpDlProgress.name(), UtilsMethod.decFormat.format(p));
			dataMap.put(DataTaskValue.BordProgress.name(), UtilsMethod.decFormat.format(p));
			// 传输大小
			dataMap.put(DataTaskValue.FtpDlCurrentSize.name(),
					UtilsMethod.decFormat.format(transByte * 8f / UtilsMethod.kbyteRage));
			dataMap.put(DataTaskValue.FtpDlAllSize.name(),
					UtilsMethod.decFormat.format(fileSize * 8f / UtilsMethod.kbyteRage));

			callbackHandler.obtainMessage(DATA_CHANGE, dataMap).sendToTarget();

			// QOS写入到RCU
			UtilsMethod.sendWriteRcuFtpData(getApplicationContext(), WalkCommonPara.MsgDataFlag_F, 0x00, (int) delayTime,
					transByte, 1000, (int) currentBytes);

		}

	}

	@Override
	protected boolean getDataTestHandler() {
		dataTestHandler = new FtpDownHandler((TaskFtpModel) taskModel);
		return true;
	}
}
