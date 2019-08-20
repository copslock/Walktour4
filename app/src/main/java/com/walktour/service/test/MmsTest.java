package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Message;

import com.dingli.service.test.ipc2msg;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.RcuEventCommand.FailReason;
import com.walktour.Utils.TotalStruct.TotalAppreciation;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.DataTaskValue;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.task.mms.receive.TaskMmsReceiveModel;
import com.walktour.gui.task.parsedata.model.task.mms.send.TaskMmsSendModel;
import com.walktour.gui.task.parsedata.model.task.mms.sendreceive.TaskMmsSendReceiveModel;
import com.walktour.service.TestService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 彩信业务测试
 * 
 * @author jianchao.wang
 *
 */
@SuppressLint("SdCardPath")
public class MmsTest extends TestTaskService {
	/** 日志标识 */
	protected static final String TAG = "MmsTest";
	/** 附件接收路径 */
	private final String PATH_MMS_RECV_DIR = AppFilePathUtil.getInstance().getAppFilesDirectory() + File.separator+"temp";
	/** 附件发送路径 */
	private final String PATH_MMS_FILE = AppFilePathUtil.getInstance().getAppFilesFile("temp","mmstest").getAbsolutePath();

	/** 发送模式 */
	public static final int MODE_SEND = 0;
	/** 接收模式 */
	public static final int MODE_RECV = 1;
	/** 自发自收中的发送模式 */
	public static final int MODE_SR_SEND = 2;
	/** 自发自收中的接收模式 */
	public static final int MODE_SR_RECV = 3;
	/** 测试模式 */
	private int mode = -1;

	// private int pppDelay = 0;

	// 接收等待PUSH
	private long sendTime = System.currentTimeMillis();
	private String mmsUrl = "";
	private long startWaitPushTime = 0;
	private long recvPushTime = 0;
	private Timer pushTimer;
	private TimerTask pushTimerTask;
	private int pushTimeout = 0;// 秒

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.i(TAG, "----onStartCommand----");

		int startFlag = super.onStartCommand(intent, flags, startId);

		regedit();

		if (taskModel == null) {
			stopSelf();
		} else {
			// pppDelay = intent.getIntExtra(KEY_PDP_DELAY, 0);

			WalkStruct.TaskType taskType = WalkStruct.TaskType.valueOf(taskModel.getTaskType());

			guid = genGUID();
			TaskMmsSendModel sendModel = null;
			switch (taskType) {
			case MMSSend:
				sendModel = (TaskMmsSendModel) taskModel;
				dataTestHandler = new MmsHandler();
				((MmsHandler) dataTestHandler).setSendMode(sendModel, MODE_SEND);
				dataTestHandler.startTest();
				break;
			case MMSIncept:
				TaskMmsReceiveModel recvModel = (TaskMmsReceiveModel) taskModel;
				dataTestHandler = new MmsHandler();

				if (recvModel.isUnitTest()) {
					sendNormalMessage(MSG_UNIT_MMS_RECV_START);
				}

				((MmsHandler) dataTestHandler).setRecvMode(recvModel, MODE_RECV);
				startWaitPushTime = System.currentTimeMillis();

				pushTimeout = recvModel.getPushTimeOut();
				break;

			case MMSSendReceive:
				TaskMmsSendReceiveModel sendRecvModel = (TaskMmsSendReceiveModel) taskModel;
				sendModel = new TaskMmsSendModel();
				sendModel.setAdjunct(sendRecvModel.getAdjunct());
				sendModel.setContent(sendRecvModel.getContent());
				sendModel.setDestination(sendRecvModel.getDestination());
				sendModel.setGateway(sendRecvModel.getGateway());
				sendModel.setMediaFileSize(sendRecvModel.getFileSize());
				sendModel.setPort(sendRecvModel.getPort());
				sendModel.setServerAddress(sendRecvModel.getServerAddress());
				sendModel.setSubject(sendRecvModel.getSubject());
				sendModel.setTimeOut(sendRecvModel.getSendTimeout());

				pushTimeout = sendRecvModel.getPushTimeout();

				dataTestHandler = new MmsHandler();
				((MmsHandler) dataTestHandler).setSendMode(sendModel, MODE_SR_SEND);
				dataTestHandler.startTest();
				break;
			default:
				break;
			}

			// 启动已经实例化handler的数据业务
			if (dataTestHandler == null) {
				stopSelf();
			}
		}

		return startFlag;
	}

	@Override
	public void onDestroy() {
		this.unregisterReceiver(mEventReceiver);
		super.onDestroy();

		// 因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
		UtilsMethod.killProcessByPname("com.walktour.service.test.MmsTest", false);
		UtilsMethod.killProcessByPname(AppFilePathUtil.getInstance().getAppLibFile("datatests_android").getAbsolutePath(), false);
	}

	/**
	 * 注册广播接收器
	 */
	private void regedit() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(WalkMessage.ACTION_MMS_PUSH_RECEIVE);
		filter.addAction(WalkMessage.ACTION_UNIT_NORMAL_RECEIVE);
		this.registerReceiver(mEventReceiver, filter);
	}

	private BroadcastReceiver mEventReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(WalkMessage.ACTION_MMS_PUSH_RECEIVE)) {
				// 此处未添加push信息判断,因为在收到 PUSH消息后，未下载彩信前，无法解析出彩信的内容
				// 所以只能一接收到PUSH消息就当成是接收PUSH成功。
				recvPushTime = System.currentTimeMillis();

				if (startWaitPushTime != 0) {

					// 存储事件:Push时延，单位：ms，为从MMS_M_Send_Config事件到本事件的毫秒数
					// 单机版，无法知道startWaitPushTime的真正时间
					int pushDelay = (int) (recvPushTime - startWaitPushTime);
					writeRcuEvent(RcuEventCommand.MMS_M_Notification_Ind, System.currentTimeMillis() * 1000, pushDelay);

					// 统计
					if (mode == MODE_SR_SEND) {
						dataTestHandler.totalResult(TotalAppreciation._MMSPushTry, 1);
						dataTestHandler.totalResult(TotalAppreciation._MMSPushSuccs, 1);
						dataTestHandler.totalResult(TotalAppreciation._MMSPushDelay, pushDelay);
					}

					// 启动监听数据库变化的线程
					new MMSReaderThread().start();
				}
			}
			// 来自服务端的常规消息
			else if (intent.getAction().equals(WalkMessage.ACTION_UNIT_NORMAL_RECEIVE)) {
				String msg = intent.getExtras().getString(WalkMessage.KEY_UNIT_MSG);
				LogUtil.i(TAG, "receive normal msg:" + msg);

				// 响应发送方的开始
				if (msg.equals(MSG_UNIT_MMS_SEND_START)) {
					// 2014.4.8 这里收到的可能是发送方下次开始之前的同步，不能作任何响应
					// sendNormalMessage( MSG_UNIT_MMS_RECV_START );
				}

				// 发送方测试完成,这里不能再无限时等下去
				else if (msg.equals(MSG_UNIT_MMS_SEND_END)) {
					startPushTimer(pushTimeout);
				}
			}
		}
	};

	@SuppressLint("HandlerLeak")
	private class MmsHandler extends DataTestHandler {
		/**
		 * mms_send_item.h
		 */
		private final int MMS_SEND_TEST = 82;

		private final int MMS_SEND_INITED = 1;
		private final int MMS_SEND_CONNECT_START = 2;
		private final int MMS_SEND_CONNECT_SUCCESS = 3;
		private final int MMS_SEND_CONNECT_FAILED = 4;
		private final int MMS_SEND_MMS_SIZE = 5;
		private final int MMS_SEND_FIRST_DATA = 6;
		private final int MMS_SEND_QOS_ARRIVED = 7;
		private final int MMS_SEND_FINISH = 10;
		private final int MMS_SEND_DROP = 9;
		private final int MMS_SEND_QUIT = 11;

		private final int MMS_SEND_START_TEST = 1001;
		private final int MMS_SEND_STOP_TEST = 1006;

		/**
		 * mms_recv_item.h
		 */
		private final int MMS_RECV_TEST = 83;

		private final int MMS_RECV_INITED = 1;
		private final int MMS_RECV_CONNECT_START = 2;
		private final int MMS_RECV_CONNECT_SUCCESS = 3;
		private final int MMS_RECV_CONNECT_FAILED = 4;
		private final int MMS_RECV_MMS_SIZE = 5;
		private final int MMS_RECV_FIRST_DATA = 6;
		private final int MMS_RECV_QOS_ARRIVED = 7;
		private final int MMS_RECV_DROP = 9;
		private final int MMS_RECV_FINISH = 10;
		private final int MMS_RECV_QUIT = 11;

		private final int MMS_RECV_START_TEST = 1001;
		private final int MMS_RECV_STOP_TEST = 1006;

		private TaskMmsSendModel sendModel = null;

		private TaskMmsReceiveModel recvModel = null;

		private void clearStatus() {
			hasFail = false;
			firstDataTime = 0;
			hasDrop = false;
			hasLastData = false;
		}

		public void setSendMode(TaskMmsSendModel sendModel, int mode) {
			construct("-m mms_send -z " + AppFilePathUtil.getInstance().getAppConfigDirectory(),
					AppFilePathUtil.getInstance().getAppLibFile(useRoot ? "datatests_android" : "libdatatests_so.so").getAbsolutePath(),
					MMS_SEND_TEST, MMS_SEND_START_TEST, MMS_SEND_STOP_TEST);
			this.sendModel = sendModel;
			MmsTest.this.mode = mode;

			clearStatus();
		}

		/**
		 * 函数功能：自发自收时切换到接收模式
		 */
		public void setRecvMode(TaskMmsReceiveModel recvModel, int mode) {
			construct("-m mms_recv -z " + getFilesDir() + "/config/",
					useRoot ? getFilesDir().getParent() + "/lib/datatests_android" // 可执行文件，绑定3G网卡需要root
							: getFilesDir().getParent() + "/lib/libdatatests_so.so", // 动态库,无法进行3G网卡绑定
					MMS_RECV_TEST, MMS_RECV_START_TEST, MMS_RECV_STOP_TEST);
			this.recvModel = recvModel;
			MmsTest.this.mode = mode;

			clearStatus();
		}

		@Override
		public void handleMessage(Message msg) {
			ipc2msg aMsg = (ipc2msg) msg.obj;

			if (aMsg.test_item == MMS_SEND_TEST) {
				handleSendMsg(aMsg);
			} else if (aMsg.test_item == MMS_RECV_TEST) {
				handleRecvMsg(aMsg);
			}
		}

		private void handleSendMsg(ipc2msg aMsg) {
			hasEventCallBack = true; // 设置业务库有事件回调

			switch (aMsg.event_id) {
			case MMS_SEND_INITED:
				LogUtil.i(TAG, "recv MMS_SEND_INITED\t" + aMsg.data);

				StringBuilder event_data = new StringBuilder();
				event_data.append("local_if::").append("").append("\n");
				event_data.append("play_time_ms::").append(sendModel.getTimeOut() * 1000).append("\n");
				event_data.append("parent_processid::").append("0").append("\n");
				event_data.append("center_url::").append(sendModel.getServerAddress()).append("\n");
				event_data.append("gateway_ip::").append(sendModel.getGateway()).append("\n");
				event_data.append("gateway_port::").append(sendModel.getPort()).append("\n");
				event_data.append("wap_protocol::").append("2").append("\n");
				event_data.append("to_phone::").append(sendModel.getDestination()).append("\n");
				event_data.append("cc_phone::").append("").append("\n");
				event_data.append("subject::").append(sendModel.getSubject()).append("\n");
				event_data.append("context::").append(sendModel.getContent()).append("\n");
				event_data.append("priority::").append("2").append("\n");
				event_data.append("send_file::").append(sendModel.getAdjunct()).append("\n");
				event_data.append("guid::").append(guid).append("\n");
				event_data.append("index::").append(repeatTimes);
				LogUtil.i(TAG, event_data.toString());

				EventBytes.Builder(mContext, RcuEventCommand.MMS_Send_Start).addStringBuffer(sendModel.getDestination())
						.addInt64(aMsg.getRealTime() / 1000).addInteger(repeatTimes).addTguid(guid).writeToRcu(aMsg.getRealTime());

				this.sendStartCommand(event_data.toString());
				sendTime = aMsg.getRealTime() / 1000;
				break;

			case MMS_SEND_QOS_ARRIVED:
				String strQos = aMsg.data;
				LogUtil.i(TAG, "QOS:\r\n" + strQos);
				lastQosTime = aMsg.getRealTime();
				// 12-18 15:33:56.441 I/MmsTest (14571): send_time_ms::1004
				// 12-18 15:33:56.441 I/MmsTest (14571): send_bytes::98304
				// 12-18 15:33:56.441 I/MmsTest (14571): send_progress::76
				// 12-18 15:33:56.441 I/MmsTest (14571): send_max_speed::783298
				// 12-18 15:33:56.441 I/MmsTest (14571): send_min_speed::783298
				// 12-18 15:33:56.441 I/MmsTest (14571): send_avg_speed::783298
				// 12-18 15:33:56.441 I/MmsTest (14571): send_cur_speed::783298
				String[] qos = aMsg.data.split("\n");
				delayTime = Long.parseLong(qos[0].split("::")[1]);
				currentBytes = Long.parseLong(qos[1].split("::")[1]) - transByte;
				transByte = Long.parseLong(qos[1].split("::")[1]);
				progress = (long) Double.parseDouble(qos[2].split("::")[1]);
				peakValue = (long) Double.parseDouble(qos[3].split("::")[1]);
				avgRate = (long) Double.parseDouble(qos[5].split("::")[1]);
				currentSpeed = (long) Double.parseDouble(qos[6].split("::")[1]);
				sendCurrentRate();
				break;

			case MMS_SEND_CONNECT_START:
				LogUtil.i(TAG, "recv MMS_SEND_CONNECT_START\r\n");
				connectTime = aMsg.getRealTime();
				EventBytes.Builder(mContext, RcuEventCommand.MMS_Send_SockConnecting).writeToRcu(aMsg.getRealTime());
				EventBytes.Builder(mContext, RcuEventCommand.MMS_Send_WSP_Connect_Request).writeToRcu(aMsg.getRealTime());
				break;

			case MMS_SEND_CONNECT_SUCCESS:
				connectedTime = aMsg.getRealTime();
				LogUtil.i(TAG, "recv MMS_SEND_CONNECT_SUCCESS\r\n");
				EventBytes.Builder(mContext, RcuEventCommand.MMS_Send_WSP_Connect_Finished).addInteger(1)
						.addInteger((int) (connectedTime - connectTime) / 1000).writeToRcu(aMsg.getRealTime());
				break;

			// 连接失败
			case MMS_SEND_CONNECT_FAILED:
				LogUtil.w(TAG, "recv MMS_SEND_CONNECT_FAILED:\r\n" + aMsg.data);
				if ((aMsg.getRealTime() - connectTime) / 1000 > sendModel.getTimeOut() * 1000) {
					fail(FailReason.CONNECT_TIMEOUT.getReasonCode(), aMsg.getRealTime());
				} else {
					fail(FailReason.UNKNOWN.getReasonCode(), aMsg.getRealTime());
				}
				break;

			case MMS_SEND_MMS_SIZE:
				LogUtil.i(TAG, "recv MMS_SEND_MMS_SIZE:\r\n" + aMsg.data);
				// 12-18 15:33:55.470 I/MmsTest (14571): recv MMS_SEND_MMS_SIZE:
				// 12-18 15:33:55.470 I/MmsTest (14571): mms_size::128767
				try {
					fileSize = Integer.parseInt(getResponseResult(aMsg.data, "mms_size"));
				} catch (Exception e) {

				}
				break;

			case MMS_SEND_FIRST_DATA:
				LogUtil.i(TAG, "recv MMS_SEND_FIRST_DATA:" + aMsg.data);
				firstDataTime = aMsg.getRealTime() / 1000;
				lastQosTime = aMsg.getRealTime();
				break;

			case MMS_SEND_FINISH:
				LogUtil.i(TAG, "recv MMS_SEND_FINISH:" + aMsg.data);
				lastDataTime = aMsg.getRealTime() / 1000;
				lastData(aMsg.getRealTime());
				break;

			case MMS_SEND_DROP:
				LogUtil.w(TAG, "recv MMS_SEND_DROP:" + aMsg.data);
				int reason = Integer.parseInt(aMsg.data.split("::")[1].trim());
				drop(reason, aMsg.getRealTime());
				break;

			case MMS_SEND_QUIT:
				LogUtil.i(TAG, "recv MMS_SEND_QUIT\r\n");
				sendCommand(MMS_SEND_STOP_TEST, "");
				break;
			}
		}

		/**
		 * 函数功能：
		 * 
		 * @param aMsg
		 */
		private void handleRecvMsg(ipc2msg aMsg) {
			switch (aMsg.event_id) {
			case MMS_RECV_INITED:
				LogUtil.i(TAG, "recv MMS_RECV_INITED\r\n");
				LogUtil.i(TAG, aMsg.data + "\r\n");

				StringBuilder event_data = new StringBuilder();
				event_data.append("local_if::").append("").append("\n");
				event_data.append("play_time_ms::").append(recvModel.getTimeOut() * 1000).append("\n");
				event_data.append("parent_processid::").append("0").append("\n");
				event_data.append("center_url::").append(recvModel.getServerAddress()).append("\n");
				event_data.append("gateway_ip::").append(recvModel.getGateway()).append("\n");
				event_data.append("gateway_port::").append(recvModel.getPort()).append("\n");
				event_data.append("wap_protocol::").append("2").append("\n");
				event_data.append("mms_url::").append(mmsUrl).append("\n");
				event_data.append("do_save::").append("1").append("\n");
				event_data.append("save_file::").append(PATH_MMS_RECV_DIR);
				this.sendStartCommand(event_data.toString());
				break;

			case MMS_RECV_QOS_ARRIVED:
				String strQos = aMsg.data;
				LogUtil.i(TAG, "QOS:\r\n" + strQos);
				lastQosTime = aMsg.getRealTime();
				String[] qos = aMsg.data.split("\n");
				delayTime = Long.parseLong(qos[0].split("::")[1]);
				currentBytes = Long.parseLong(qos[1].split("::")[1]) - transByte;
				transByte = Long.parseLong(qos[1].split("::")[1]);
				progress = (long) Double.parseDouble(qos[2].split("::")[1]);
				peakValue = (long) Double.parseDouble(qos[3].split("::")[1]);
				avgRate = (long) Double.parseDouble(qos[5].split("::")[1]);
				currentSpeed = (long) Double.parseDouble(qos[6].split("::")[1]);
				sendCurrentRate();
				break;

			case MMS_RECV_CONNECT_START:
				LogUtil.i(TAG, "recv MMS_RECV_CONNECT_START\r\n");
				connectTime = aMsg.getRealTime();
				EventBytes.Builder(mContext, RcuEventCommand.MMS_Recv_SockConnecting).writeToRcu(aMsg.getRealTime());
				EventBytes.Builder(mContext, RcuEventCommand.MMS_Recv_WSP_Connect_Request).writeToRcu(aMsg.getRealTime());
				break;

			case MMS_RECV_CONNECT_SUCCESS:
				LogUtil.i(TAG, "recv MMS_RECV_CONNECT_SUCCESS\r\n");
				EventBytes.Builder(mContext, RcuEventCommand.MMS_Recv_WSP_Connect_Finished).addInteger(1)
						.addInteger((int) (connectedTime - connectTime) / 1000).writeToRcu(aMsg.getRealTime());
				break;

			case MMS_RECV_CONNECT_FAILED:
				LogUtil.i(TAG, "recv MMS_RECV_CONNECT_FAILED\r\n");
				LogUtil.i(TAG, aMsg.data + "\r\n");
				if ((aMsg.getRealTime() - connectTime) / 1000 > recvModel.getTimeOut() * 1000) {
					fail(FailReason.CONNECT_TIMEOUT.getReasonCode(), aMsg.getRealTime());
				} else {
					fail(FailReason.UNKNOWN.getReasonCode(), aMsg.getRealTime());
				}
				break;

			case MMS_RECV_MMS_SIZE:
				LogUtil.i(TAG, "recv MMS_RECV_MMS_SIZE\r\n");
				LogUtil.i(TAG, aMsg.data + "\r\n");
				try {
					fileSize = Integer.parseInt(getResponseResult(aMsg.data, "mms_size"));
				} catch (Exception e) {
				}
				break;

			// 03-24 13:52:40.001: I/MmsTest(14376): recv MMS_RECV_FIRST_DATA
			// 03-24 13:52:40.001: I/MmsTest(14376):
			// guid::20140324115042210357657050393269
			// 03-24 13:52:40.001: I/MmsTest(14376): index::1
			// 03-24 13:52:40.001: I/MmsTest(14376): send_time::2014/03/24
			// 11:50:42.400
			case MMS_RECV_FIRST_DATA:
				LogUtil.i(TAG, "recv MMS_RECV_FIRST_DATA\r\n");
				LogUtil.i(TAG, aMsg.data + "\r\n");// 这里还没拿到时间,发送序号，GUID
				firstDataTime = aMsg.getRealTime() / 1000;

				try {
					guid = getResponseResult(aMsg.data, "guid");
					int index = Integer.parseInt(getResponseResult(aMsg.data, "index"));
					String time = getResponseResult(aMsg.data, "send_time").trim();
					time = time.replace(" ", "-");
					SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss.SSS", Locale.getDefault());
					String sendPhone = getResponseResult(aMsg.data, "send_phone");
					long sendTime = sdFormat.parse(time).getTime();
					EventBytes.Builder(mContext, RcuEventCommand.MMS_M_Recv_FirstData).addInt64(sendTime).addInteger(index)
							.addTguid(genGUID()).addStringBuffer(sendPhone).writeToRcu(aMsg.getRealTime());
				} catch (Exception e) {
					LogUtil.e(TAG, e.getMessage());
				}
				break;

			case MMS_RECV_DROP:
				LogUtil.i(TAG, "recv MMS_RECV_DROP\r\n");
				LogUtil.i(TAG, aMsg.data);
				int reason = Integer.parseInt(aMsg.data.split("::")[1].trim());
				drop(reason, aMsg.getRealTime());
				break;

			case MMS_RECV_FINISH:
				LogUtil.i(TAG, "recv MMS_RECV_FINISH\r\n");
				LogUtil.i(TAG, aMsg.data + "\r\n");
				lastDataTime = aMsg.getRealTime() / 1000;
				lastData(aMsg.getRealTime());
				break;

			case MMS_RECV_QUIT:
				LogUtil.i(TAG, "recv MMS_RECV_QUIT\r\n");
				sendCommand(MMS_RECV_STOP_TEST, "");
				break;
			}
		}

		/** 生成附近的文件 */
		private void createFile(String path, long size) {
			LogUtil.w(TAG, "---create temp file:" + size);
			try {
				RandomAccessFile raf = new RandomAccessFile(path, "rw");
				raf.setLength(size);
				raf.close();
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		@Override
		protected void prepareTest() {

			if (mode == MODE_SEND || mode == MODE_SR_SEND) {

				// 生成附件文件(限制是300K)
				File file = new File(sendModel.getAdjunct());
				if (file.exists() && file.isFile() && file.length() > 0 && file.length() / UtilsMethod.kbyteRage <= 300) {
					//
				} else {
					int fileSize = sendModel.getMediaFileSize() > 300 ? 300 : sendModel.getMediaFileSize();
					createFile(PATH_MMS_FILE, 1000 * fileSize);
					sendModel.setAdjunct(PATH_MMS_FILE); // 附件
				}
			}
		}

		@Override
		public synchronized void fail(int reason, long time) {

			// fail、drop、lastData三个事件互斥
			if (!hasFail && firstDataTime == 0 && !hasDrop && !hasLastData) {
				hasFail = true;
				int eventFlag = RcuEventCommand.MMS_Send_WSP_Connect_Finished;
				if (mode == MODE_RECV || mode == MODE_SR_RECV)
					eventFlag = RcuEventCommand.MMS_Recv_WSP_Connect_Finished;
				EventBytes.Builder(mContext, eventFlag).addInteger(reason).addInteger((int) (time - connectTime) / 1000)
						.writeToRcu(time);

				// 结束本次测试
				this.stopProcess(TestService.RESULT_FAILD);
			}

		}

		@Override
		public synchronized void drop(int reason, long time) {

			if (firstDataTime == 0) {
				fail(reason, time);
			} else if (!hasDrop && !hasFail && firstDataTime != 0 && !hasLastData) {
				hasDrop = true;

				if (mode == MODE_SEND || mode == MODE_SR_SEND) {
					sendDrop(reason, time);
				} else if (mode == MODE_RECV || mode == MODE_SR_RECV) {
					recvDrop(reason, time);
				}

				// 结束本次测试
				this.stopProcess(TestService.RESULT_SUCCESS);
			}

		}

		private void sendDrop(int reason, long time) {
			int dropDelay = (int) (System.currentTimeMillis() - firstDataTime);
			// 存储
			EventBytes.Builder(mContext, RcuEventCommand.MMS_M_Send_Config).addInteger((int) transByte).addInteger(dropDelay)
					.addInteger(reason).writeToRcu(time);

			// 统计(有Fitstdata和Drop时一次完整统计)
			if (reason == RcuEventCommand.DropReason.USER_STOP.getReasonCode()) {
				// Drop原因：User Stop，但该次下载计为下载成功
				totalResult(TotalAppreciation._MMSSendTry, 1);
				totalResult(TotalAppreciation._MMSSendSuccs, 1);
				totalResult(TotalAppreciation._MMSSendDelay, dropDelay);
			} else {
				totalResult(TotalAppreciation._MMSSendTry, 1);
				totalResult(TotalAppreciation._MMSSendSuccs, 0);
			}
		}

		private void recvDrop(int reason, long time) {
			int dropDelay = (int) (System.currentTimeMillis() - firstDataTime);

			// 存储
			EventBytes.Builder(mContext, RcuEventCommand.MMS_M_NotifyResp_Ind).addInteger(dropDelay).addInteger(reason)
					.writeToRcu(time);

			// 统计(有Fitstdata和Drop时一次完整统计)
			if (reason == RcuEventCommand.DropReason.USER_STOP.getReasonCode()) {
				// Drop原因：User Stop，但该次下载计为下载成功
				totalResult(TotalAppreciation._MMSReceiveTry, 1);
				totalResult(TotalAppreciation._MMSReceiveSuccs, 1);
				totalResult(TotalAppreciation._MMSReceiveDelay, dropDelay);
			} else {
				totalResult(TotalAppreciation._MMSReceiveTry, 1);
				totalResult(TotalAppreciation._MMSReceiveSuccs, 0);
			}
		}

		@Override
		protected void sendCurrentRate() {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			// 当前速率kbps
			dataMap.put(DataTaskValue.BordCurrentSpeed.name(),
					UtilsMethod.decFormat.format(currentSpeed / UtilsMethod.kbyteRage));
			// 平均速率kbps
			dataMap.put(DataTaskValue.BordLeftTitle.name(), getString(R.string.info_data_averagerate) + "@"
					+ UtilsMethod.decFormat.format(avgRate / UtilsMethod.kbyteRage) + getString(R.string.info_rate_kbps));

			// 峰值
			dataMap.put(WalkStruct.DataTaskValue.PeakValue.name(), UtilsMethod.decFormat.format(peakValue));
			// 进度
			dataMap.put(DataTaskValue.BordProgress.name(), UtilsMethod.decFormat.format(progress));

			callbackHandler.obtainMessage(DATA_CHANGE, dataMap).sendToTarget();

			// QOS写入到RCU
			int dirction = (mode == MODE_SEND || mode == MODE_SR_SEND) ? 0x01 : 0x00;
			UtilsMethod.sendWriteRcuFtpData(getApplicationContext(), WalkCommonPara.MsgDataFlag_U, dirction, (int) delayTime,
					transByte, 1000, (int) currentBytes);
		}

		@Override
		public synchronized void lastData(long time) {
			// fail、drop、lastData三个事件互斥
			if (!hasLastData && firstDataTime != 0 && !hasFail && !hasDrop) {
				hasLastData = true;

				if (mode == MODE_SEND || mode == MODE_SR_SEND) {
					sendLastData(time);
				} else if (mode == MODE_RECV || mode == MODE_SR_RECV) {
					recvLastData(time);
				}

				if (mode == MODE_SR_SEND) {
					startWaitPushTime = System.currentTimeMillis();

					startPushTimer(pushTimeout);
				} else {
					// 结束本次测试
					this.stopProcess(TestService.RESULT_SUCCESS);
				}
			}

			else if (firstDataTime == 0) {
				// 结束本次测试
				this.stopProcess(TestService.RESULT_SUCCESS);
			}
		}

		private void sendLastData(long time) {
			// 存储
			EventBytes.Builder(mContext, RcuEventCommand.MMS_M_Send_Config).addInteger((int) transByte)
					.addInteger((int) delayTime).addInteger(1).writeToRcu(time);

			// 统计
			totalResult(TotalAppreciation._MMSSendTry, 1);
			totalResult(TotalAppreciation._MMSSendSuccs, 1);
			totalResult(TotalAppreciation._MMSSendDelay, delayTime);
		}

		private void recvLastData(long time) {
			// 存储
			EventBytes.Builder(mContext, RcuEventCommand.MMS_M_NotifyResp_Ind).addInteger((int) delayTime).addInteger(1)
					.writeToRcu(time);

			// 统计
			totalResult(TotalAppreciation._MMSReceiveTry, 1);
			totalResult(TotalAppreciation._MMSReceiveSuccs, 1);
			totalResult(TotalAppreciation._MMSReceiveDelay, delayTime);

			if (mode == MODE_SR_RECV) {
				long delay = System.currentTimeMillis() - sendTime;
				totalResult(TotalAppreciation._MMSPtoPDelay, delay);
				totalResult(TotalAppreciation._MMSP2PCount, 1);
			}
		}

	}

	/**
	 * 监听数据库的变化
	 */
	private class MMSReaderThread extends Thread {
		@Override
		public void run() {

			LogUtil.w(TAG, "---MMS Reader start");

			Uri uriSMS = Uri.parse("content://mms/inbox");

			try {
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}

			// 2012.10.15 原来的2秒还 不足够，导致读取彩信地址失败
			boolean hasGetUrl = false;
			for (int i = 0; i < 10; i++) {
				Cursor c = MmsTest.this.getContentResolver().query(uriSMS, new String[] { "_id", "date", "sub", "ct_l" }, null,
						null, "date desc");
				c.moveToFirst();

				try {
					mmsUrl = c.getString(c.getColumnIndex("ct_l"));
				} catch (Exception e) {

				}

				if (mmsUrl != null) {
					if (mmsUrl.length() > "http:".length()) {
						hasGetUrl = true;
						break;
					}
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}

			}

			// 提示去掉自动下载
			if (!hasGetUrl) {
				UtilsMethod.showNotification(MmsTest.this, getString(R.string.notify_mms_autodownload),
						WalkMessage.ACTION_MMS_AUTODOWNLOAD);
			}

			StringBuffer sb = new StringBuffer();
			sb.append("---");

			Cursor c2 = null;
			try {
				c2 = MmsTest.this.getContentResolver().query(uriSMS, new String[] { "_id", "date", "sub", "ct_l" }, null, null,
						"date desc");
				c2.moveToFirst();

				mmsUrl = c2.getString(c2.getColumnIndex("ct_l"));

				sb.append("_id:" + c2.getString(c2.getColumnIndex("_id")) + "\t" + "date:"
						+ UtilsMethod.getSimpleDateFormat0(1000 * c2.getLong(c2.getColumnIndex("date"))) + "\t" + "sub:"
						+ c2.getString(c2.getColumnIndex("sub")) + "\t");
				sb.append("ct_l:" + c2.getString(c2.getColumnIndex("ct_l")) + "\n");
			} catch (Exception e) {
				LogUtil.w(TAG, e.toString());
				sb.append("ct_l:null");
			} finally {
				if (c2 != null) {
					c2.close();
					c2 = null;
				}
			}

			LogUtil.w(TAG, sb.toString());

			// 启动接收计时器
			if (dataTestHandler != null) {
				if (mode == MODE_RECV) {
					dataTestHandler.startTest();
				}

				else if (mode == MODE_SR_SEND) {
					setSendRecvMode();
				}
			}
		}

	}

	private void setSendRecvMode() {

		if (taskModel != null) {

			TaskMmsReceiveModel recvModel = new TaskMmsReceiveModel();
			TaskMmsSendReceiveModel sendRecvModel = (TaskMmsSendReceiveModel) taskModel;
			recvModel.setConnectTimeOut(sendRecvModel.getConnectTimeout());
			recvModel.setGateway(sendRecvModel.getGateway());
			recvModel.setPort(sendRecvModel.getPort());
			recvModel.setPushTimeOut(sendRecvModel.getPushTimeout());
			recvModel.setServerAddress(sendRecvModel.getServerAddress());
			recvModel.setTimeOut(sendRecvModel.getReceiveTimeout());

			if (dataTestHandler != null) {
				dataTestHandler.sendStopCommand();
				MmsHandler sendHandler = (MmsHandler) dataTestHandler;
				// 进入自发自收的接收状态
				sendHandler.setRecvMode(recvModel, MODE_SR_RECV);
				sendHandler.startTest();
			}
		}

	}

	/** PUSH接收计时器 */
	private void startPushTimer(int timeOut) {
		if (pushTimer != null && pushTimerTask != null) {
			pushTimer.cancel();
			pushTimerTask.cancel();
			pushTimer = null;
			pushTimerTask = null;
		}
		pushTimer = new Timer();
		pushTimerTask = new TimerTask() {
			@Override
			public void run() {

				if (recvPushTime == 0) {
					// 启动接收计时器
					if (dataTestHandler != null) {
						writeRcuEvent(RcuEventCommand.MMS_M_Notification_TimeOut, System.currentTimeMillis() * 1000);
						dataTestHandler.stopProcess(TestService.RESULT_SUCCESS);
					}
				}
			}

		};
		pushTimer.schedule(pushTimerTask, timeOut * 1000);
	}

}
