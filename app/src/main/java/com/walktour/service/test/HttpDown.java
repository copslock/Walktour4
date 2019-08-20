package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Message;

import com.dingli.service.test.ipc2msg;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.RcuEventCommand.FailReason;
import com.walktour.Utils.TotalStruct.TotalAppreciation;
import com.walktour.Utils.TotalStruct.TotalHttpType;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.DataTaskValue;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.task.http.page.TaskHttpPageModel;
import com.walktour.gui.task.parsedata.model.task.wap.TaskWapPageModel;
import com.walktour.model.TotalSpecialModel;
import com.walktour.service.TestService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("SdCardPath")
public class HttpDown extends TestTaskService {
	/**
	 * http_down_item.h
	 */
	private final int HTTP_DOWN_TEST = 72;

	private final int HTTP_DOWN_INITED 			= 1;
	private final int HTTP_DOWN_CONNECT_START 	= 2;
	private final int HTTP_DOWN_CONNECT_SUCCESS 	= 3;
	private final int HTTP_DOWN_CONNECT_FAILED 	= 4;
	private final int HTTP_DOWN_URL_REDIRECT 		= 5;
	private final int HTTP_DOWN_FILE_SIZE 		= 6;
	private final int HTTP_DOWN_SENDGETCMD 		= 7;
	private final int HTTP_DOWN_FIRSTDATA 		= 8;
	private final int HTTP_DOWN_QOS 				= 9;
	private final int HTTP_DOWN_DROP 				= 10;
	private final int HTTP_DOWN_FINISH 			= 11;
	private final int HTTP_DOWN_QUIT 				= 12;
	private final int HTTP_DOWN_FAILED 			= 13;
	
	//新增解析事件,此事件在连接事件之前进行
	//事件参数：http_down_dnsresolve_start
//	private final int HTTP_DOWN_DNSRESOLVE_START	= 15;//开始解析
	//事件参数：http_down_dnsresolve_info
//	private final int HTTP_DOWN_DNSRESOLVE_SUCCESS= 16;//解析成功
	//事件参数：http_down_dnsfailed_info
//	private final int HTTP_DOWN_DNSRESOLVE_FAILED	= 17;//解析失败


	private final int HTTP_DOWN_START_TEST = 1001;
	private final int HTTP_DOWN_STOP_TEST = 1006;

	@Override
	public void onCreate() {
		super.onCreate();
		tag = "HttpDown";
		LogUtil.i(tag, "onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
		UtilsMethod.killProcessByPname("com.walktour.service.test.HttpDown", false);
		UtilsMethod.killProcessByPname(AppFilePathUtil.getInstance().getAppLibFile("datatests_android").getAbsolutePath(), false);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.i(tag, "onStart");
		int startFlag = super.onStartCommand(intent, flags, startId);
		if (taskModel instanceof TaskWapPageModel) {
			dataTestHandler = new WapDownHandler((TaskWapPageModel) taskModel);
		} else if (taskModel instanceof TaskHttpPageModel) {
			dataTestHandler = new HttpDownHandler((TaskHttpPageModel) taskModel);
		}

		// 启动已经实例化handler的数据业务
		if (dataTestHandler != null) {
			dataTestHandler.startTest();
		}

		return startFlag;
	}

	// used to update ui
	@SuppressLint("HandlerLeak")
	private class WapDownHandler extends DataTestHandler {
		private TaskWapPageModel wapModel;

		// //在非root权限下调用
		public WapDownHandler(TaskWapPageModel wapModel) {
			super("-m http_down -z "+ AppFilePathUtil.getInstance().getAppConfigDirectory(),
					AppFilePathUtil.getInstance().getAppLibFile(useRoot?"datatests_android":"libdatatests_so.so").getAbsolutePath(),
			HTTP_DOWN_TEST, HTTP_DOWN_START_TEST,HTTP_DOWN_STOP_TEST);
			this.wapModel = wapModel;
		}

		public void handleMessage(android.os.Message msg) {
			ipc2msg aMsg = (ipc2msg) msg.obj;
			if (aMsg.test_item != HTTP_DOWN_TEST) {
				return;
			}
			hasEventCallBack = true;	//设置业务库有事件回调
			
			switch (aMsg.event_id) {
			case HTTP_DOWN_QOS:
				LogUtil.i(tag, aMsg.data + "\r\n");
				// 08-22 15:55:54.895: I/HttpDown(17403): meas_time_ms::2239
				// 08-22 15:55:54.895: I/HttpDown(17403): recv_bytes::8412
				// 08-22 15:55:54.895: I/HttpDown(17403): down_progress::0
				// 08-22 15:55:54.895: I/HttpDown(17403): recv_max_speed::288824.00
				// 08-22 15:55:54.895: I/HttpDown(17403): recv_min_speed::0.00
				// 08-22 15:55:54.895: I/HttpDown(17403): recv_avg_speed::30056.00
				// 08-22 15:55:54.895: I/HttpDown(17403): recv_bytes_cur::8412
				// 08-22 15:55:54.895: I/HttpDown(17403): recv_cur_speed::288824.00
				String[] qos = aMsg.data.split("\n");
				delayTime = Long.parseLong(qos[0].split("::")[1]);
				transByte = Long.parseLong(qos[1].split("::")[1]);
				progress = (long) Double.parseDouble(qos[2].split("::")[1]);
				peakValue = (long) Double.parseDouble(qos[3].split("::")[1]);
				avgRate = (long) Double.parseDouble(qos[5].split("::")[1]);
				currentBytes = Long.parseLong(qos[6].split("::")[1]);
				currentSpeed = (long) Double.parseDouble(qos[7].split("::")[1]);

				sendCurrentRate();

				break;

			case HTTP_DOWN_INITED:
				LogUtil.i(tag, "recv HTTP_DOWN_INITED\r\n");
				LogUtil.i(tag, aMsg.data + "\r\n");
				File file = new File(downloadPath);
				int do_save = file.exists() ? 1 : 0;
				StringBuilder event_data = new StringBuilder();
				event_data.append("local_if::").append("").append("\n");
				event_data.append("play_time_ms::").append(wapModel.getTimeOut() * 1000).append("\n");
				event_data.append("connect_timeout_ms::").append(60 * 1000).append("\n");
				event_data.append("nodata_timeout_ms::").append(60 * 1000).append("\n");
				event_data.append("qos_inv_ms::").append(1 * 1000).append("\n");
				event_data.append("url::").append(wapModel.getUrl().trim()).append("\n");
				event_data.append("protol_type::").append(PROTOL_TYPE_WAP20).append("\n");
				event_data.append("gateway_ip::").append(wapModel.getGateway()).append("\n");
				event_data.append("gateway_port::").append(wapModel.getPort()).append("\n");
				event_data.append("thread_count::").append(3).append("\n");
				event_data.append("ps_call::").append(0).append("\n");
				event_data.append("use_gateway::").append(1).append("\n");
				event_data.append("do_save::").append(do_save).append("\n");
				event_data.append("save_filepath::").append(downloadPath);
				LogUtil.i(tag, event_data.toString());
				sendStartCommand(event_data.toString());
				writeRcuEvent(RcuEventCommand.WAPDL_SockConnecting, aMsg.getRealTime());
				break;

			// 连接网关开始
			case HTTP_DOWN_CONNECT_START:
				LogUtil.i(tag, "recv HTTP_DOWN_CONNECT_START\r\n");
				connectTime = aMsg.getRealTime();
				writeRcuEvent(RcuEventCommand.WAPDL_ConnectGatewayRequest, aMsg.getRealTime());
				showEvent("WAP Download Connect Gateway Request");
				break;

			// 连接网关成功
			case HTTP_DOWN_CONNECT_SUCCESS:
				LogUtil.i(tag, "recv HTTP_DOWN_CONNECT_SUCCESS\r\n");
				connectedTime = aMsg.getRealTime();
				int delayGateway = (int) (connectedTime - connectTime) / 1000;
				writeRcuEvent(RcuEventCommand.WAPDL_ConnectGatewayFinished, aMsg.getRealTime(), 1, delayGateway);
				showEvent("WAP Download Connect Gateway Success:Delay " + delayGateway + "(ms)");
				break;

			// 连接网关结束
			case HTTP_DOWN_CONNECT_FAILED:
				LogUtil.i(tag, "recv HTTP_DOWN_CONNECT_FAILED\r\n");
				LogUtil.i(tag, aMsg.data + "\r\n");
				connectFail(aMsg.getRealTime());
				break;

			// 网站跳转
			case HTTP_DOWN_URL_REDIRECT:
				LogUtil.i(tag, "recv HTTP_DOWN_URL_REDIRECT\r\n");
				LogUtil.i(tag, aMsg.data + "\r\n");
				EventBytes.Builder(HttpDown.this, RcuEventCommand.WAPDL_UrlRedirect)
						.addCharArray(wapModel.getUrl().toCharArray(), 2048).writeToRcu(aMsg.getRealTime());
				showEvent("WAP Download Url Redirect");
				break;

			case HTTP_DOWN_FILE_SIZE:
				LogUtil.i(tag, "recv HTTP_DOWN_FILE_SIZE\r\n");
				LogUtil.i(tag, aMsg.data + "\r\n");
				break;

			// 发送下载请求
			case HTTP_DOWN_SENDGETCMD:
				LogUtil.i(tag, "recv HTTP_DOWN_SENDGETCMD\r\n");
				sendGetTime = aMsg.getRealTime();
				writeRcuEvent(RcuEventCommand.WAPDL_GetRequest, aMsg.getRealTime());
				showEvent("WAP Download Get Request");
				break;

			// 响应第一个数据
			case HTTP_DOWN_FIRSTDATA:
				LogUtil.i(tag, "recv HTTP_DOWN_FIRSTDATA\r\n");
				firstDataTime = aMsg.getRealTime();
				int delayFirstData = (int) (firstDataTime - sendGetTime) / 1000;
				writeRcuEvent(RcuEventCommand.WAPDL_FirstDataEx, aMsg.getRealTime(), delayFirstData);
				showEvent("WAP Download First Data");

				startPioneerTimer();

				// 设置主进程中的firstdata状态
				setMainFirstDataState(true);

				break;

			// 下载Drop
			case HTTP_DOWN_DROP:
				LogUtil.i(tag, "recv HTTP_DOWN_DROP\r\n");
				LogUtil.i(tag, aMsg.data);
				drop((short) getReason(aMsg.data), aMsg.getRealTime());
				break;

			// 最后一个数据
			case HTTP_DOWN_FINISH:
				LogUtil.i(tag, "recv HTTP_DOWN_FINISH\r\n");
				lastData(aMsg.getRealTime());
				break;

			case HTTP_DOWN_QUIT:
				LogUtil.i(tag, "recv HTTP_DOWN_QUIT\r\n");
				// 未知原因的退出当成是fail(URL写错时)
				if (!hasFail && !hasDrop && !hasLastData) {
					fail(FailReason.UNKNOWN.getReasonCode(), aMsg.getRealTime());
				}
				// this.stopProcess( TestService.RESULT_SUCCESS );
				break;

			}
		}

		/**
		 * Wap下载drop 文档描述如下 ? WAP下载Drop判断：在以下几种情况下，都必须插入WAP下载Drop事件，结束该次WAP登录测试流程；
		 * 1.WAP发出Get请求之后收不到WAP Down First Data时 2.WAP Down First
		 * Data之后在超时时长内收不到WAP下载最后一个数据包 3.WAP下载异常中断（拨号连接断开、Socket断开等）
		 * 以上drop原因码都在库里直接返回
		 * .;%ADK_HOME%\platform-tools;%JAVA_HOME%\bin;%JAVA_HOME%\
		 * jre\bin;%ADK_HOME%\tools;%ANT_HOME%\bin;C:\Program Files\IDM Computer
		 * Solutions\UltraEdit\
		 * */
		private void wapDrop(short reason, long time) {
			if (!hasDrop && lastDataTime == 0 && connectedTime != 0) {
				hasDrop = true;
				// 下载时长与LastData事件一样，从连接网关成功算起
				int timeCount = (int) (time - connectedTime) / 1000;
				EventBytes.Builder(HttpDown.this, RcuEventCommand.WAPDL_Drop).addInteger(timeCount).addInteger((int) transByte)
						.addShort(reason).addByte((byte) wapModel.getWapType()).writeToRcu(time);

				String fmt = "WAP Download Drop:Delay %.3f(s)," + "Mean Rate %.2f(KB/S)" + ",Transmit Size:%.2f Kbytes"
						+ ",TestMode:" + getDownType() + "," + getDropReasonString(reason);
				showEvent(String.format(fmt, delayTime / 1000f, avgRate / 8f / UtilsMethod.kbyteRage, transByte / UtilsMethod.kbyteRage));

				// 统计
				if (reason == RcuEventCommand.DROP_USERSTOP && firstDataTime != 0) {
					totalResult(TotalAppreciation._wapDownTrys, 1);
					totalResult(TotalAppreciation._wapDownSuccs, 1);
					// 成功就必须算速率，因为平均速率是 各速率和/成功次数
					totalResult(TotalAppreciation._wapDownTotalBytes, transByte);
					totalResult(TotalAppreciation._wapDownTotalTime, timeCount);
				} else {
					totalResult(TotalAppreciation._wapDownTrys, 1);
					totalResult(TotalAppreciation._wapDownSuccs, 0);
				}

				// 结束本次测试
				this.stopProcess(TestService.RESULT_SUCCESS);
			}
		}

		public String getDownType() {
			if (wapModel == null) {
				return "";
			}
			switch (wapModel.getWapType()) {
			case 0:
				return WalkStruct.DataTaskEvent.WAP_TYPE_PictureDown.toString();
			case 1:
				return WalkStruct.DataTaskEvent.WAP_TYPE_RingDown.toString();
			case 2:
				return WalkStruct.DataTaskEvent.WAP_TYPE_KjavaDown.toString();
			}
			return "";
		}

		/**
		 * 发送瞬时速率到数据页面
		 * 
		 * @param avg
		 *          平均速率
		 * @param current
		 *          当前速率
		 * */
		private void sendCurrentRate(float avg, float current) {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put(WalkStruct.DataTaskValue.WapDlThrput.name(), UtilsMethod.decFormat.format(current));
			dataMap.put(DataTaskValue.BordLeftTitle.name(), getString(R.string.info_data_averagerate) + "@"
					+ UtilsMethod.decFormat.format(avg) + getString(R.string.info_rate_kbps));
			dataMap.put(DataTaskValue.BordCurrentSpeed.name(), UtilsMethod.decFormat.format(current));
			Message msg = callbackHandler.obtainMessage(DATA_CHANGE, dataMap);
			callbackHandler.sendMessage(msg);
		}

		protected void connectFail(long time) {
			if (!hasFail && connectTime != 0) {
				hasFail = true;

				int gatewayFail = (int) (time - connectTime) / 1000;
				writeRcuEvent(RcuEventCommand.WAPDL_ConnectGatewayFinished, time, -1, gatewayFail);
				showEvent("WAP Download Connect Gateway Failure:Delay " + gatewayFail + "(ms)");

				// 结束本次测试
				this.stopProcess(TestService.RESULT_FAILD);
			}
		}

		@Override
		protected void fail(int failReason, long time) {
			// fail、drop、lastData三个事件互斥
			if (!hasFail && firstDataTime == 0 && !hasDrop && !hasLastData) {
				hasFail = true;

				showEvent("WAP Download Failure:" + FailReason.getFailReason(failReason).getResonStr());

				// 结束本次测试
				this.stopProcess(TestService.RESULT_FAILD);
			}

		}

		@Override
		protected void drop(int dropReason, long time) {
			// fail、drop、lastData三个事件互斥
			if (!hasDrop && !hasFail && sendGetTime != 0 && !hasLastData) {
				wapDrop((short) dropReason, time);

				// 结束本次测试
				this.stopProcess(TestService.RESULT_SUCCESS);
			}
		}

		@Override
		protected void lastData(long time) {
			// fail、drop、lastData三个事件互斥
			LogUtil.w(tag, "--hasLast:" + hasLastData + "--fristT:" + firstDataTime + "--hasFail:" + hasFail + "--hasDrop:"
					+ hasDrop);
			if (!hasLastData && firstDataTime != 0 && !hasFail && !hasDrop) {
				hasLastData = true;
				lastDataTime = time;
				// 文档描述:从WAPDL_ConnectGatewayFinished到本事件的毫秒数)
				// 2012.12.18但实际上后台计算平均速率用的是lastData-firstData之间的时差
				// int timeCount = (int) (lastData-gatewaySuccess ) ;
				int timeCount = (int) (lastDataTime - firstDataTime) / 1000;
				EventBytes.Builder(HttpDown.this, RcuEventCommand.WAPDL_LastData).addInteger((int) transByte)
						.addInteger(timeCount).addByte((byte) wapModel.getWapType()).writeToRcu(time);

				String fmt = "WAP Download Last Data:Delay %.3f(s)," + "Mean Rate %.2f(KB/S)" + ",Transmit Size:%.2f Kbytes"
						+ ",TestMode:" + getDownType();
				showEvent(String.format(fmt, delayTime / 1000f, avgRate / 8f / UtilsMethod.kbyteRage, transByte / UtilsMethod.kbyteRage));

				// 统计
				totalResult(TotalAppreciation._wapDownTrys, 1);
				totalResult(TotalAppreciation._wapDownSuccs, 1);
				// 成功就必须算速率，因为平均速率是 各速率和/成功次数
				totalResult(TotalAppreciation._wapDownTotalBytes, transByte);
				totalResult(TotalAppreciation._wapDownTotalTime, timeCount);

				// 结束本次测试
				this.stopProcess(TestService.RESULT_SUCCESS);
			}

			// 设置主进程中的firstdata状态
			setMainFirstDataState(false);
		}

		@Override
		protected void sendCurrentRate() {
			if (firstDataTime != 0) {
				// 仪表盘的速率
				sendCurrentRate(avgRate / UtilsMethod.kbyteRage, currentSpeed / UtilsMethod.kbyteRage);

				// 写入Ftp_data数据格式
				UtilsMethod.sendWriteRcuFtpData(getApplicationContext(), WalkCommonPara.MsgDataFlag_W, 0x00, (int) delayTime,
						transByte, 1000, (int) currentBytes);
			}
		}

		@Override
		protected void prepareTest() {
			resetDownloadDir();
		}

	};

	// used to update ui
	@SuppressLint("HandlerLeak")
	private class HttpDownHandler extends DataTestHandler {
		private TaskHttpPageModel httpModel;

		public HttpDownHandler(TaskHttpPageModel httpModel) {
			super("-m http_down -z " + AppFilePathUtil.getInstance().getAppConfigDirectory(),
					AppFilePathUtil.getInstance().getAppLibFile(useRoot ? "datatests_android" : "libdatatests_so.so").getAbsolutePath(),
					HTTP_DOWN_TEST, HTTP_DOWN_START_TEST,HTTP_DOWN_STOP_TEST);
			this.httpModel = httpModel;
		}

		public void handleMessage(android.os.Message msg) {
			ipc2msg aMsg = (ipc2msg) msg.obj;
			if (aMsg.test_item != HTTP_DOWN_TEST) {
				return;
			}
			hasEventCallBack = true;	//设置业务库有事件回调
			
			switch (aMsg.event_id) {
			case HTTP_DOWN_QOS:
				LogUtil.i(tag, aMsg.data + "\r\n");
				// 08-22 15:54:38.511: I/HttpDown(17023): meas_time_ms::6306
				// 08-22 15:54:38.511: I/HttpDown(17023): recv_bytes::662404
				// 08-22 15:54:38.511: I/HttpDown(17023): down_progress::0
				// 08-22 15:54:38.511: I/HttpDown(17023): recv_max_speed::2017731.00
				// 08-22 15:54:38.511: I/HttpDown(17023): recv_min_speed::0.00
				// 08-22 15:54:38.511: I/HttpDown(17023): recv_avg_speed::840347.00
				// 08-22 15:54:38.511: I/HttpDown(17023): recv_bytes_cur::71452
				// 08-22 15:54:38.511: I/HttpDown(17023): recv_cur_speed::1911759.00
				String[] qos = aMsg.data.split("\n");
				delayTime = Long.parseLong(qos[0].split("::")[1]);
				transByte = Long.parseLong(qos[1].split("::")[1]);
				progress = (long) Double.parseDouble(qos[2].split("::")[1]);
				peakValue = (long) Double.parseDouble(qos[3].split("::")[1]);
				avgRate = (long) Double.parseDouble(qos[5].split("::")[1]);
				currentBytes = Long.parseLong(qos[6].split("::")[1]);
				currentSpeed = (long) Double.parseDouble(qos[7].split("::")[1]);

				sendCurrentRate();
				break;

			case HTTP_DOWN_INITED:
				LogUtil.i(tag, "recv HTTP_DOWN_INITED\r\n");
				LogUtil.i(tag, aMsg.data + "\r\n");
				// 是否要保存文件
				File file = new File(downloadPath);
				int do_save = file.exists() ? 1 : 0;
				StringBuilder event_data = new StringBuilder();
				event_data.append("local_if::").append("").append("\n");
				event_data.append("play_time_ms::").append(httpModel.getTimeOut() * 1000).append("\n");
				event_data.append("connect_timeout_ms::").append(60 * 1000).append("\n");
				event_data.append("nodata_timeout_ms::").append(httpModel.getReponse() * 1000).append("\n");
				event_data.append("qos_inv_ms::").append(1 * 1000).append("\n");
				event_data.append("url::").append(httpModel.getXmlUrl().trim()).append("\n");
				event_data.append("protol_type::").append(PROTOL_TYPE_HTTP).append("\n");
				event_data.append("gateway_ip::").append("").append("\n");
				event_data.append("gateway_port::").append("").append("\n");
				event_data.append("thread_count::").append(httpModel.getThreadCount()).append("\n");
				event_data.append("ps_call::").append(httpModel.getTestMode() == 1 ? "1" : "0").append("\n");
				event_data.append("use_gateway::").append(0).append("\n");
				event_data.append("do_save::").append(do_save).append("\n");
				event_data.append("save_filepath::").append(downloadPath).append("\n");
				event_data.append("download_type::").append(httpModel.getServerType()).append("\n"); // 下载类型（0:普通下载，1:百度云下载）
				event_data.append("use_in_account::").append(httpModel.getAccountType()).append("\n"); // 使用内置的百度云开发者用户帐号（1:使用，0:不使用，使用的是外部的开发者帐号）
				event_data.append("api_key::").append(httpModel.getAccountKey()).append("\n"); // 百度云应用API
				// Key（use_in_account=0时使用）
				event_data.append("secret_key::").append(httpModel.getSecretKey()); // 百度云应用Secret
				// Key（use_in_account=0时使用）
				LogUtil.i(tag, event_data.toString());
				sendStartCommand(event_data.toString());

				// 2013.5.23添加此事件
				EventBytes.Builder(HttpDown.this, RcuEventCommand.HTTP_Down_Start).addStringBuffer(httpModel.getXmlUrl())
						.writeToRcu(aMsg.getRealTime());
				showEvent("HTTP Down Start");
				break;

			// 连接网关开始
			case HTTP_DOWN_CONNECT_START:
				LogUtil.i(tag, "recv HTTP_DOWN_CONNECT_START\r\n");
				connectTime = aMsg.getRealTime();
				writeRcuEvent(RcuEventCommand.HTTP_Down_SockConnecting, aMsg.getRealTime());
				showEvent("HTTP Download Connect");
				break;

			// 连接网关成功
			case HTTP_DOWN_CONNECT_SUCCESS:
				LogUtil.i(tag, "recv HTTP_DOWN_CONNECT_SUCCESS\r\n");
				connectedTime = aMsg.getRealTime();
				int delayGateway = (int) (connectedTime - connectTime) / 1000;
				writeRcuEvent(RcuEventCommand.HTTP_Down_ConnectSockSucc, aMsg.getRealTime(), delayGateway);
				showEvent("HTTP Download Connect Success:Delay " + delayGateway + "(ms)");
				break;

			// 连接网关结束
			case HTTP_DOWN_CONNECT_FAILED:
				LogUtil.i(tag, "recv HTTP_DOWN_CONNECT_FAILED\r\n");
				LogUtil.i(tag, aMsg.data + "\r\n");
				int reasonFail = getReason(aMsg.data);
				connectFail(reasonFail, aMsg.getRealTime());
				break;

			// 网站跳转
			case HTTP_DOWN_URL_REDIRECT:
				LogUtil.i(tag, "recv HTTP_DOWN_URL_REDIRECT\r\n");
				LogUtil.i(tag, aMsg.data + "\r\n");
				EventBytes.Builder(HttpDown.this, RcuEventCommand.HTTP_Down_UrlRedirect)
						.addCharArray(httpModel.getXmlUrl().toCharArray(), 2048).writeToRcu(aMsg.getRealTime());
				break;

			// 发送下载请求
			case HTTP_DOWN_SENDGETCMD:
				LogUtil.i(tag, "recv HTTP_DOWN_SENDGETCMD\r\n");
				sendGetTime = aMsg.getRealTime();
				showEvent("HTTP Download Send Get");
				break;
			// 文件大小
			case HTTP_DOWN_FILE_SIZE:
				LogUtil.i(tag, "recv HTTP_DOWN_FILE_SIZE\r\n");
				LogUtil.i(tag, aMsg.data + "\r\n");
				fileSize = 0;
				try {
					fileSize = Integer.parseInt(aMsg.data.trim().split("::")[1]);
				} catch (Exception e) {

				}
				EventBytes.Builder(HttpDown.this, RcuEventCommand.HTTP_Down_SendGetCmd).addInteger((int) fileSize)
						.writeToRcu(aMsg.getRealTime());

				break;

			// 2012.11.7增加此事件
			case HTTP_DOWN_FAILED:
				LogUtil.i(tag, "recv HTTP_DOWN_FAILED\r\n");
				LogUtil.i(tag, aMsg.data + "\r\n");
				// 07-22 15:46:00.636 I/HttpDown(12222): recv HTTP_DOWN_FAILED
				// 07-22 15:46:00.636 I/HttpDown(12222): reason::1000
				int failCode = getReason(aMsg.data);
				// 如果填写错误URL时，会来到这里
				fail(failCode, aMsg.getRealTime());
				break;

			// 响应第一个数据
			case HTTP_DOWN_FIRSTDATA:
				LogUtil.i(tag, "recv HTTP_DOWN_FIRSTDATA\r\n");
				firstDataTime = aMsg.getRealTime();
				writeRcuEvent(RcuEventCommand.HTTP_Down_FirstData, aMsg.getRealTime());
				showEvent("HTTP Download First Data");

				// 设置主进程中的firstdata状态
				setMainFirstDataState(true);

				startPioneerTimer();
				break;

			// 下载Drop
			case HTTP_DOWN_DROP:
				LogUtil.i(tag, "recv HTTP_DOWN_DROP\r\n");
				LogUtil.i(tag, aMsg.data);
				drop(getReason(aMsg.data), aMsg.getRealTime());
				break;

			// 最后一个数据
			case HTTP_DOWN_FINISH:
				LogUtil.i(tag, "recv HTTP_DOWN_FINISH\r\n");
				lastData(aMsg.getRealTime());
				break;

			case HTTP_DOWN_QUIT:
				LogUtil.i(tag, "recv HTTP_DOWN_QUIT\r\n");
				// 未知原因的退出当成是fail(URL写错时)
				if (!hasFail && !hasDrop && !hasLastData) {
					fail(RcuEventCommand.FAIL_NORMAL, aMsg.getRealTime());
				}
				break;

			}
		}

		/**
		 * 发送瞬时速率到数据页面
		 * 
		 * @param avg
		 *          平均速率
		 * @param current
		 *          当前速率
		 * */
		private void sendCurrentRate(float avg, float current) {

			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put(WalkStruct.DataTaskValue.HttpDlThrput.name(), UtilsMethod.decFormat.format(current));
			dataMap.put(DataTaskValue.BordLeftTitle.name(), getString(R.string.info_data_averagerate) + "@"
					+ UtilsMethod.decFormat.format(avg) + getString(R.string.info_rate_kbps));
			dataMap.put(DataTaskValue.BordCurrentSpeed.name(), UtilsMethod.decFormat.format(current));
			Message msg = callbackHandler.obtainMessage(DATA_CHANGE, dataMap);
			callbackHandler.sendMessage(msg);
		}

		protected void connectFail(int reasonFail, long time) {
			if (!hasFail && connectTime != 0) {
				hasFail = true;

				int gatewayFail = (int) (time - connectTime) / 1000;
				writeRcuEvent(RcuEventCommand.HTTP_Down_ConnectSockFailed, time, gatewayFail, reasonFail);
				showEvent("HTTP Download Connect Failure:" + FailReason.getFailReason(reasonFail).getResonStr());

				// 结束本次测试
				this.stopProcess(TestService.RESULT_FAILD);
			}
		}

		@Override
		protected void fail(int failReason, long time) {
			// fail、drop、lastData三个事件互斥
			if (!hasFail && firstDataTime == 0 && !hasDrop && !hasLastData) {
				hasFail = true;

				EventBytes.Builder(HttpDown.this, RcuEventCommand.HTTP_Down_Failure).addInteger(failReason).writeToRcu(time);

				showEvent("HTTP Download Failure:" + FailReason.getFailReason(failReason).getResonStr());

				disConnect();

				// 结束本次测试
				this.stopProcess(TestService.RESULT_FAILD);
			}
		}

		@Override
		protected void drop(int dropReason, long time) {
			// fail、drop、lastData三个事件互斥
			if (!hasDrop && !hasFail && sendGetTime != 0 && !hasLastData) {
				hasDrop = true;
				// 下载时长从连接网关成功算起
				long connectTimeCount = (time - sendGetTime) / 1000;
				EventBytes.Builder(HttpDown.this, RcuEventCommand.HTTP_Down_Drop).addInteger((int) connectTimeCount)
						.addInteger((int) transByte).addInteger(dropReason).addInteger(1)
						.addCharArray(httpModel.getXmlUrl().toCharArray(), 256).writeToRcu(time);

				String fmt = "HTTP Download Drop:Delay %.3f(s),Mean Rate:%.2f(KB/S),Transmit Size:%.3f KBytes,%s";
				showEvent(String.format(fmt, delayTime / 1000f, avgRate / 8f / UtilsMethod.kbyteRage, transByte / UtilsMethod.kbyteRage,
						getDropReasonString(dropReason)));

				// 有firstdata之后的手工停止当成是成功统计,drop时不算速率
				if (dropReason == RcuEventCommand.DROP_USERSTOP && firstDataTime != 0) {
					HashMap<String, TotalSpecialModel> map = new HashMap<String, TotalSpecialModel>();
					totalHttpResult(map, TotalHttpType.HTTPDownload, httpModel.getXmlUrl(), TotalAppreciation._HttpDownloadTry, 1);
					totalHttpResult(map, TotalHttpType.HTTPDownload, httpModel.getXmlUrl(), TotalAppreciation._HttpDownloadSuccess, 1);
					// //成功就必须算速率，因为平均速率是 各速率和/成功次数
					totalHttpResult(map, TotalHttpType.HTTPDownload, httpModel.getXmlUrl(), TotalAppreciation._HttpDownloadTotalBytes,
							transByte);
					totalHttpResult(map, TotalHttpType.HTTPDownload, httpModel.getXmlUrl(), TotalAppreciation._HttpDownloadTotalTime,
							delayTime);
					Message msgChart = callbackHandler.obtainMessage(CHART_CHANGE, map);
					callbackHandler.sendMessage(msgChart);
				} else {
					HashMap<String, TotalSpecialModel> map = new HashMap<String, TotalSpecialModel>();
					totalHttpResult(map, TotalHttpType.HTTPDownload, httpModel.getXmlUrl(), TotalAppreciation._HttpDownloadTry, 1);
					totalHttpResult(map, TotalHttpType.HTTPDownload, httpModel.getXmlUrl(), TotalAppreciation._HttpDownloadSuccess, 0);
					Message msgChart = callbackHandler.obtainMessage(CHART_CHANGE, map);
					callbackHandler.sendMessage(msgChart);
				}

				disConnect();

				// 结束本次测试
				this.stopProcess(TestService.RESULT_SUCCESS);
			}
		}

		@Override
		protected void lastData(long time) {
			// fail、drop、lastData三个事件互斥
			LogUtil.w(tag, "--hasLast:" + hasLastData + "--fristT:" + firstDataTime + "--hasFail:" + hasFail + "--hasDrop:"
					+ hasDrop);
			if (!hasLastData && firstDataTime != 0 && !hasFail && !hasDrop) {
				hasLastData = true;

				lastDataTime = time;
				int connectTimeCount = (int) (lastDataTime - sendGetTime) / 1000;
				int transmitTimeCount = (int) (lastDataTime - firstDataTime) / 1000;
				EventBytes.Builder(HttpDown.this, RcuEventCommand.HTTP_Down_LastData).addInteger((int) transByte)
						.addInteger(connectTimeCount).addInteger(transmitTimeCount).addInteger(1)
						.addCharArray(httpModel.getXmlUrl().toCharArray(), 256).writeToRcu(time);
				// 为了显示更精确,计算平均速率的时间从firstData到lastData，未按文档规定保留小数
				// int rate_KBS = (int) ( transByte*1000 / transmitTimeCount ) /1000 ;
				// rate_KBS = (rate_KBS <1 )? 1: rate_KBS;
				// int delaySecond = (transmitTimeCount/1000) > 1 ?
				// (transmitTimeCount/1000) : 1;
				// int size_Kbyte = (int) ( transByte/1000 > 1 ? (transByte/1000) : 1 );

				String fmt = "HTTP Download Last Data:Delay %.3f(s),Mean Rate:%.2f(KB/S),Transmit Size:%.3f KBytes";
				showEvent(String.format(fmt, delayTime / 1000f, avgRate / 8f / UtilsMethod.kbyteRage, transByte / UtilsMethod.kbyteRage));
				// 统计(A算法)
				HashMap<String, TotalSpecialModel> map = new HashMap<String, TotalSpecialModel>();
				totalHttpResult(map, TotalHttpType.HTTPDownload, httpModel.getXmlUrl(), TotalAppreciation._HttpDownloadTry, 1);
				totalHttpResult(map, TotalHttpType.HTTPDownload, httpModel.getXmlUrl(), TotalAppreciation._HttpDownloadSuccess, 1);
				totalHttpResult(map, TotalHttpType.HTTPDownload, httpModel.getXmlUrl(), TotalAppreciation._HttpDownloadTotalBytes,
						transByte);
				totalHttpResult(map, TotalHttpType.HTTPDownload, httpModel.getXmlUrl(), TotalAppreciation._HttpDownloadTotalTime,
						delayTime);
				Message msgChart = callbackHandler.obtainMessage(CHART_CHANGE, map);
				callbackHandler.sendMessage(msgChart);

				// 设置主进程中的firstdata状态
				setMainFirstDataState(false);

				disConnect();

				// 结束本次测试
				this.stopProcess(TestService.RESULT_SUCCESS);
			}

		}

		protected void disConnect() {
			if (connectTime != 0) {
				// 存储事件
				EventBytes.Builder(HttpDown.this, RcuEventCommand.HTTP_Down_SocketDisconnected).writeToRcu(
						System.currentTimeMillis() * 1000);
				// 显示事件
				showEvent("HTTP Page Disconnect");
			}
		}

		@Override
		protected void sendCurrentRate() {
			if (firstDataTime != 0) {
				// 仪表盘的速率
				sendCurrentRate(avgRate / UtilsMethod.kbyteRage, currentSpeed / UtilsMethod.kbyteRage);
				// 写入Ftp_data数据格式
				// int totalTime = (int) (System.currentTimeMillis() - firstDataTime /
				// 1000);
				UtilsMethod.sendWriteRcuFtpData(getApplicationContext(), WalkCommonPara.MsgDataFlag_H, 0x00, (int) delayTime,
						transByte, 1000, (int) currentBytes);
			}
		}

		@Override
		protected void prepareTest() {
			resetDownloadDir();
		}

		/**
		 * 统计HTTP业务
		 * 
		 * @param httpType
		 *          主键1，HTTP登陆或刷新
		 * @param url
		 *          url 主键2,HTTP的URL
		 * @param totalType
		 *          要统计的具体值的KEY,如http刷新时延
		 */
		protected void totalHttpResult(HashMap<String, TotalSpecialModel> map, TotalHttpType httpType, String url,
				TotalAppreciation totalType, long value) {
			TotalSpecialModel tmpSp = new TotalSpecialModel(httpType.getHttpType(), "HttpDownload",// url,
					totalType.name(), value);
			map.put(tmpSp.getKeyName(), tmpSp);
		}

	}

}
