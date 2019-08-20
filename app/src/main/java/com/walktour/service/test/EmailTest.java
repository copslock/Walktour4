package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Message;

import com.dingli.service.test.ipc2msg;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.RcuEventCommand.FailReason;
import com.walktour.Utils.TotalStruct.TotalAppreciation;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.DataTaskValue;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.task.email.receive.TaskEmailPop3Model;
import com.walktour.gui.task.parsedata.model.task.email.send.TaskEmailSmtpModel;
import com.walktour.service.TestService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressLint("SdCardPath")
public class EmailTest extends TestTaskService {
	protected final String tag = "EmailTest";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.i(tag, "onStart");

		int startFlag = super.onStartCommand(intent, flags, startId);

		if (taskModel == null) {
			stopSelf();
		} else {
			WalkStruct.TaskType taskType = WalkStruct.TaskType.valueOf(taskModel.getTaskType());
			if (taskType.equals(WalkStruct.TaskType.EmailPop3)) {
				TaskEmailPop3Model popModel = (TaskEmailPop3Model) taskModel;
				dataTestHandler = new EmailReceiveHandler(popModel);
			} else if (taskType.equals(WalkStruct.TaskType.EmailSmtp)) {
				TaskEmailSmtpModel smtpModel = (TaskEmailSmtpModel) taskModel;
				dataTestHandler = new EmailSendHandler(smtpModel);
			}

			// 启动已经实例化handler的数据业务
			if (dataTestHandler != null) {
				dataTestHandler.startTest();
			}
		}

		return startFlag;
	}

	@SuppressLint("HandlerLeak")
	private class EmailSendHandler extends DataTestHandler {
		// 临时文件
		private final String PATH_TEMP_FILE = AppFilePathUtil.getInstance().getAppFilesFile("temp","EmailSend").getAbsolutePath();

		// JNI回调
		private static final int EMAIL_SEND_TEST = 67;
		private static final int EMAIL_SEND_INITED = 1;
		private static final int EMAIL_SEND_CONNECT_START = 2;
		private static final int EMAIL_SEND_CONNECT_SUCCESS = 3;
		private static final int EMAIL_SEND_CONNECT_FAILED = 4;
		private static final int EMAIL_SEND_CMD_REQUEST = 5;
		private static final int EMAIL_SEND_CMD_RESPONSE = 6;
		private static final int EMAIL_SEND_FIRSTDATA = 7;
		private static final int EMAIL_SEND_LASTDATA = 8;
		private static final int EMAIL_SEND_DROP = 9;
		private static final int EMAIL_SEND_FINISH = 10;
		private static final int EMAIL_SEND_QOS = 11;
		private static final int EMAIL_SEND_QUIT = 12;
		private static final int EMAIL_SEND_FAILED = 13;
		private static final int EMAIL_SEND_DNSRESOLVE_START = 20;
		private static final int EMAIL_SEND_DNSRESOLVE_SUCCESS = 21;
		private static final int EMAIL_SEND_DNSRESOLVE_FAILED = 22;
		private static final int EMAIL_SEND_START_TEST = 1001;
		private static final int EMAIL_SEND_STOP_TEST = 1006;
		private static final int EMAIL_SEND_LOGIN_START = 14; // 登陆开始
		private static final int EMAIL_SEND_LOGIN_SUCCESS = 15; // 登陆成功
		private static final int EMAIL_SEND_LOGIN_FAILED = 16; // 登陆失败

		private TaskEmailSmtpModel smtpModel = null;
		/** 登录开始时间 */
		private long loginTime = 0;

		public EmailSendHandler(TaskEmailSmtpModel smptModel) {
			super("-m email_send -z " + AppFilePathUtil.getInstance().getAppConfigDirectory(),
					AppFilePathUtil.getInstance().getAppLibFile(useRoot ? "datatests_android" : "libdatatests_so.so").getAbsolutePath(),
					EMAIL_SEND_TEST, EMAIL_SEND_START_TEST, EMAIL_SEND_STOP_TEST);
			this.smtpModel = smptModel;
		}

		@Override
		public void handleMessage(Message msg) {
			ipc2msg aMsg = (ipc2msg) msg.obj;
			if (aMsg.test_item != EMAIL_SEND_TEST) {
				return;
			}
			hasEventCallBack = true;	//设置业务库有事件回调
			
			switch (aMsg.event_id) {
			case EMAIL_SEND_QOS:
				String strQos = aMsg.data;
				LogUtil.i(tag, "QOS:" + strQos);
				lastQosTime = aMsg.getRealTime();
				// 04-22 11:07:05.356: I/EmailReceive(13145): recv_time_ms::120002
				// 04-22 11:07:05.356: I/EmailReceive(13145): recv_size::720896
				// 04-22 11:07:05.356: I/EmailReceive(13145): recv_progress::51
				// 04-22 11:07:05.356: I/EmailReceive(13145): recv_max_speed::525338
				// 04-22 11:07:05.356: I/EmailReceive(13145): recv_min_speed::0
				// 04-22 11:07:05.356: I/EmailReceive(13145): recv_avg_speed::48058
				// 04-22 11:07:05.356: I/EmailReceive(13145): recv_cur_speed::524288
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

			case EMAIL_SEND_INITED:
				LogUtil.i(tag, "recv EMAIL_RECV_INITED\t" + aMsg.data);
				// 来自模板的用户名可能包含@或者没有@
				String userName = smtpModel.getAccount();
				userName = userName.contains("@") ? userName.substring(0, userName.indexOf("@")) : userName;
				StringBuilder event_data = new StringBuilder();
				event_data.append("local_if::").append("").append("\n"); // 绑定IP
				event_data.append("play_time_ms::").append(smtpModel.getTimeOut() * 1000).append("\n"); //
				event_data.append("ps_call::").append(smtpModel.getPsCall()).append("\n");
				event_data.append("connect_timeout_ms::").append("10000").append("\n"); // 连接时间
				event_data.append("nodata_timeout_ms::").append("180000").append("\n"); // 3分钟无流量将会报drop
				event_data.append("qos_inv_ms::").append("1000").append("\n"); // 刷新间隔
				event_data.append("parent_processid::").append("0").append("\n");
				event_data.append("serv_name::").append(smtpModel.getEmailServer()).append("\n");
				event_data.append("serv_port::").append(smtpModel.getPort()).append("\n");
				event_data.append("use_ssl::").append(smtpModel.getUseSSL()).append("\n");
				event_data.append("user_name::").append(userName).append("\n");
				event_data.append("user_pwd::").append(smtpModel.getPassword()).append("\n");
				event_data.append("title::").append(smtpModel.getSubject()).append("\n");
				event_data.append("text::").append(smtpModel.getBody()).append("\n");
				event_data.append("filename::").append(smtpModel.getAdjunct()).append("\n");
				event_data.append("from_user::").append(smtpModel.getAccount()).append("\n");
				event_data.append("to_user::").append(smtpModel.getTo());
				LogUtil.i(tag, event_data.toString());
				EventBytes.Builder(mContext, RcuEventCommand.EMAIL_SMTP_START).addStringBuffer(userName)
						.addStringBuffer(smtpModel.getTo()).addStringBuffer(smtpModel.getEmailServer())
						.writeToRcu(aMsg.getRealTime());
				this.sendStartCommand(event_data.toString());
				break;

			case EMAIL_SEND_CONNECT_START:
				connectTime = aMsg.getRealTime();
				// 存储和显示
				writeRcuEventWithByte(RcuEventCommand.EMAIL_SMTP_SOCK_CONNECTING, aMsg.getRealTime(),
						RcuEventCommand.EMAIL_TYPE_NORMAL);
				LogUtil.i(tag, "recv EMAIL_SEND_CONNECT_START\r\n");
				break;

			case EMAIL_SEND_CONNECT_SUCCESS:
				LogUtil.i(tag, "recv EMAIL_SEND_CONNECT_SUCCESS\r\n");
				writeRcuEvent(RcuEventCommand.EMAIL_SMTP_SOCK_CONNECT_SUCCESS, aMsg.getRealTime(),
						(int) (aMsg.getRealTime() - connectTime) / 1000);
				break;

			// 连接失败
			case EMAIL_SEND_CONNECT_FAILED:
				LogUtil.w(tag, "recv EMAIL_SEND_CONNECT_FAILED:\r\n" + aMsg.data);
				int reason = 0;
				try {
					reason = Integer.parseInt(aMsg.data.split("\n")[0].split("::")[1].trim());
				} catch (Exception e) {

				}
				writeRcuEvent(RcuEventCommand.EMAIL_SMTP_SOCK_CONNECT_FAILURE, aMsg.getRealTime(), reason);
				break;
			case EMAIL_SEND_FAILED:
				LogUtil.w(tag, "recv EMAIL_SEND_FAILED:\r\n" + aMsg.data);
				reason = 0;
				try {
					reason = Integer.parseInt(aMsg.data.split("\n")[0].split("::")[1].trim());
				} catch (Exception e) {

				}
				fail(reason, aMsg.getRealTime());
				break;
			case EMAIL_SEND_CMD_REQUEST:
				LogUtil.i(tag, "recv EMAIL_SEND_CMD_REQUEST:\r\n" + aMsg.data);
				int cmd = 0;
				try {
					cmd = Integer.parseInt(aMsg.data.split("\n")[0].split("::")[1].trim());
				} catch (Exception e) {

				}
				if (cmd == 5) {
					sendGetTime = aMsg.getRealTime();
					// 请参考基础库头文件email_send_item.h
					fileSize = Integer
							.parseInt(aMsg.data.substring(aMsg.data.indexOf("mail_size::") + 11, aMsg.data.length() - 1));
					// writeRcuEvent(RcuEventCommand.EMAIL_SMTP_SEND_MIAL_CMD, (int)
					// fileSize * 1000 * 1000);
					// twq20131209库取出来的大小值有问题
					writeRcuEvent(RcuEventCommand.EMAIL_SMTP_SEND_MIAL_CMD, aMsg.getRealTime(), (int) getFileSize(smtpModel));
				}
				break;

			case EMAIL_SEND_CMD_RESPONSE:
				LogUtil.i(tag, "recv EMAIL_SEND_CMD_RESPONSE\r\n:" + aMsg.data);
				// 账户或密码错误登录失败时的回调
				// 07-25 10:56:48.215: I/EmailTest(30932): recv EMAIL_SEND_CMD_RESPONSE
				// 07-25 10:56:48.215: I/EmailTest(30932): :cmd::2
				// 07-25 10:56:48.215: I/EmailTest(30932): err_code::26
				int errorCode = 0;
				try {
					errorCode = Integer.parseInt(aMsg.data.split("\n")[1].split("::")[1].trim());
				} catch (Exception e) {

				}
				// 这个错误码不是RCU定义的
				if (errorCode != 0) {
					if (errorCode == 26) {
						fail(FailReason.AUTHENTICATION_FAILURE.getReasonCode(), aMsg.getRealTime());
					} else {
						fail(FailReason.UNKNOWN.getReasonCode(), aMsg.getRealTime());
					}
				}
				break;

			case EMAIL_SEND_FIRSTDATA:
				LogUtil.i(tag, "recv EMAIL_SEND_FIRSTDATA:" + aMsg.data);
				firstDataTime = aMsg.getRealTime();
				lastQosTime = firstDataTime;
				writeRcuEvent(RcuEventCommand.EMAIL_SMTP_FIRST_DATA, aMsg.getRealTime());
				break;

			case EMAIL_SEND_LASTDATA:
				LogUtil.i(tag, "recv EMAIL_SEND_LASTDATA:" + aMsg.data);
				lastDataTime = aMsg.getRealTime();
				lastData(aMsg.getRealTime());
				break;

			case EMAIL_SEND_DROP:
				LogUtil.w(tag, "recv EMAIL_SEND_DROP:" + aMsg.data);
				reason = Integer.parseInt(aMsg.data.split("::")[1].trim());
				drop(reason, aMsg.getRealTime());
				break;
			case EMAIL_SEND_LOGIN_START:
				loginTime = aMsg.getRealTime();
				LogUtil.i(tag, "recv EMAIL_SEND_LOGIN_START\r\n");
				break;
			case EMAIL_SEND_LOGIN_SUCCESS:
				LogUtil.i(tag, "recv EMAIL_SEND_LOGIN_SUCCESS\r\n");
				writeRcuEvent(RcuEventCommand.EMAIL_SMTP_LOGIN_SUCCESS, aMsg.getRealTime(),
						(int) (aMsg.getRealTime() - loginTime) / 1000);
				break;
			case EMAIL_SEND_LOGIN_FAILED:
				LogUtil.i(tag, "recv EMAIL_SEND_LOGIN_FAILED\r\n");
				reason = 0;
				try {
					reason = Integer.parseInt(aMsg.data.split("\n")[0].split("::")[1].trim());
				} catch (Exception e) {

				}
				writeRcuEvent(RcuEventCommand.EMAIL_SMTP_LOGIN_FAILURE, aMsg.getRealTime(), reason);
				break;
			case EMAIL_SEND_DNSRESOLVE_START:
				LogUtil.i(tag, "recv EMAIL_SEND_DNSRESOLVE_START\r\n");
				break;
			case EMAIL_SEND_DNSRESOLVE_SUCCESS:
				LogUtil.i(tag, "recv EMAIL_SEND_DNSRESOLVE_SUCCESS\r\n");
				break;
			case EMAIL_SEND_DNSRESOLVE_FAILED:
				LogUtil.i(tag, "recv EMAIL_SEND_DNSRESOLVE_FAILED\r\n");
				break;
			case EMAIL_SEND_FINISH:
				LogUtil.i(tag, "recv EMAIL_SEND_FINISH\r\n");
				disConnect();
				break;

			case EMAIL_SEND_QUIT:
				LogUtil.i(tag, "recv EMAIL_SEND_QUIT\r\n");
				// 结束本次测试
				this.stopProcess(TestService.RESULT_SUCCESS);
				break;
            default:
                break;
			}
		}

		private void write(long fileSize) {
			FileWriter fw = null;
			try {
				// 第二个参数 true 表示写入方式是追加方式
				File file = new File(PATH_TEMP_FILE);
				if (file.length() > fileSize) {
					file.delete();
					file.createNewFile();
				}
				fw = new FileWriter(PATH_TEMP_FILE, true);
				while (file.length() < fileSize) {
					fw.write(Math.random() + "\t" + Math.random() + "\n\r");
				}

				LogUtil.w(tag, "--create file:" + file.length());
			} catch (Exception e) {
				System.out.println(e.toString());
			} finally {
				try {
					if(fw != null)
						fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		protected void prepareTest() {

			// 设置附件，优先使用路径，路径无效时使用大小。
			boolean fileOkay = false;
			try {
				File file = new File(smtpModel.getAdjunct());
				if (file.exists()) {
					if (file.length() <= TaskEmailSmtpModel.FILE_SIZE_LIMIT) {
						fileOkay = true;
					}
				}
			} catch (Exception e) {
				fileOkay = false;
			}

			if (!fileOkay) {

				long fileSize = getFileSize(smtpModel);
				if (smtpModel.getFileSize() > 0) {
					// 创建小于12M的附件
					// createFile( );

					// 2013.4.1 以上方法创建的文件传输时间仅需1秒。
					LogUtil.i(tag, "start create file:" + fileSize);
					write(fileSize);
					LogUtil.i(tag, "stop create file");

					smtpModel.setAdjunct(PATH_TEMP_FILE);
				}
			}

		}

		/** 获得文件大小 */
		private long getFileSize(TaskEmailSmtpModel smtpModel) {
			return smtpModel.getFileSize() * 1000 > TaskEmailSmtpModel.FILE_SIZE_LIMIT ? TaskEmailSmtpModel.FILE_SIZE_LIMIT
					: smtpModel.getFileSize() * 1000;
		}

		@Override
		public synchronized void fail(int reason, long time) {

			// fail、drop、lastData三个事件互斥
			if (!hasFail && firstDataTime == 0 && !hasDrop && !hasLastData) {
				hasFail = true;
				EventBytes.Builder(mContext, RcuEventCommand.EMAIL_SMTP_FAILURE).addInteger(reason).writeToRcu(time);
				disConnect();

				// 结束本次测试
				this.stopProcess(TestService.RESULT_FAILD);
			}

		}

		@Override
		public synchronized void drop(int reason, long time) {

			if (!hasDrop && !hasFail && firstDataTime != 0 && !hasLastData) {
				hasDrop = true;
				int dropDelay = (int) (time - firstDataTime) / 1000000;
				// 存储
				EventBytes.Builder(mContext, RcuEventCommand.EMAIL_SMTP_DROP).addInteger(dropDelay).addInteger((int) transByte)
						.addInteger(reason).writeToRcu(time);

				// 统计(有Fitstdata和Drop时一次完整统计)
				if (reason == RcuEventCommand.DropReason.USER_STOP.getReasonCode()) {
					// Drop原因：User Stop，但该次下载计为下载成功
					totalResult(TotalAppreciation._EmailSendTry, 1);
					totalResult(TotalAppreciation._EmailSendSuccess, 1);
					totalResult(TotalAppreciation._EmailSendSumSize, transByte);
					totalResult(TotalAppreciation._EmailSendAllTime, delayTime);
				} else {
					totalResult(TotalAppreciation._EmailSendTry, 1);
					totalResult(TotalAppreciation._EmailSendSuccess, 0);
				}

				disConnect();

				// 结束本次测试
				this.stopProcess(TestService.RESULT_SUCCESS);
			}

		}

		@Override
		protected void sendCurrentRate() {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			// 当前速率kbps
			dataMap.put(DataTaskValue.BordCurrentSpeed.name(), UtilsMethod.decFormat.format(currentSpeed / UtilsMethod.kbyteRage));
			// 平均速率kbps
			dataMap.put(DataTaskValue.BordLeftTitle.name(), getString(R.string.info_data_averagerate) + "@"
					+ UtilsMethod.decFormat.format(avgRate / UtilsMethod.kbyteRage) + getString(R.string.info_rate_kbps));

			// 峰值
			dataMap.put(WalkStruct.DataTaskValue.PeakValue.name(), UtilsMethod.decFormat.format(peakValue));
			// 进度
			dataMap.put(DataTaskValue.BordProgress.name(), UtilsMethod.decFormat.format(progress));

			callbackHandler.obtainMessage(DATA_CHANGE, dataMap).sendToTarget();

			// QOS写入到RCU
			UtilsMethod.sendWriteRcuFtpData(getApplicationContext(), WalkCommonPara.MsgDataFlag_L, 0x01, (int) delayTime,
					transByte, 1000, (int) currentBytes);
		}

		@Override
		public synchronized void lastData(long time) {
			// fail、drop、lastData三个事件互斥
			if (!hasLastData && firstDataTime != 0 && !hasFail && !hasDrop) {
				hasLastData = true;
				// 存储
				EventBytes.Builder(mContext, RcuEventCommand.EMAIL_SMTP_LAST_DATA).addInteger((int) transByte)
						.addInteger((int) delayTime).writeToRcu(time);
				// 统计
				totalResult(TotalAppreciation._EmailSendTry, 1);
				totalResult(TotalAppreciation._EmailSendSuccess, 1);
				totalResult(TotalAppreciation._EmailSendSumSize, transByte);
				totalResult(TotalAppreciation._EmailSendAllTime, delayTime);

				disConnect();

			}
		}

		private synchronized void disConnect() {
			if (connectedTime != 0) {
				writeRcuEvent(RcuEventCommand.EMAIL_SMTP_EOM_ACK, connectedTime);
			}
		}

	}

	@SuppressLint("HandlerLeak")
	private class EmailReceiveHandler extends DataTestHandler {

		// JNI回调
		private static final int EMAIL_RECV_TEST = 68;

		private static final int EMAIL_RECV_INITED = 1;
		private static final int EMAIL_RECV_CONNECT_START = 2;
		private static final int EMAIL_RECV_CONNECT_SUCCESS = 3;
		private static final int EMAIL_RECV_CONNECT_FAILED = 4;
		private static final int EMAIL_RECV_SENDRETRCMD = 5;
		private static final int EMAIL_RECV_FIRSTDATA = 6;
		private static final int EMAIL_RECV_LASTDATA = 7;
		private static final int EMAIL_RECV_DROP = 8;
		private static final int EMAIL_RECV_FINISH = 9;
		private static final int EMAIL_RECV_QOS = 10;
		private static final int EMAIL_RECV_QUIT = 11;
		private static final int EMAIL_RECV_FAILED = 13;
		private static final int EMAIL_RECV_LOGIN_START = 14; // 登陆开始
		private static final int EMAIL_RECV_LOGIN_SUCCESS = 15; // 登陆成功
		private static final int EMAIL_RECV_LOGIN_FAILED = 16; // 登陆失败
		private static final int EMAIL_SEND_DNSRESOLVE_START = 20;
		private static final int EMAIL_SEND_DNSRESOLVE_SUCCESS = 21;
		private static final int EMAIL_SEND_DNSRESOLVE_FAILED = 22;
		private static final int EMAIL_RECV_CONTENT_OK = 17; // 邮件正文接收完毕(IMAP协议才有)

		private static final int EMAIL_RECV_START_TEST = 1001;
		private static final int EMAIL_RECV_STOP_TEST = 1006;

		private TaskEmailPop3Model pop3Model = null;
		/** 登录开始时间 */
		private long loginTime = 0;

		public EmailReceiveHandler(TaskEmailPop3Model popModel) {
			super("-m email_recv -z " + getFilesDir() + "/config/", useRoot ? getFilesDir().getParent()
					+ "/lib/datatests_android" : // 可执行文件，绑定3G网卡需要root
					getFilesDir().getParent() + "/lib/libdatatests_so.so",// 动态库,无法进行3G网卡绑定
					EMAIL_RECV_TEST, EMAIL_RECV_START_TEST, EMAIL_RECV_STOP_TEST);
			this.pop3Model = popModel;
		}

		public void handleMessage(Message msg) {
			ipc2msg aMsg = (ipc2msg) msg.obj;
			if (aMsg.test_item != EMAIL_RECV_TEST) {
				return;
			}
			hasEventCallBack = true;	//设置业务库有事件回调
			
			switch (aMsg.event_id) {
			case EMAIL_RECV_QOS:
				String strQos = aMsg.data;
				LogUtil.i(tag, "QOS:" + strQos);
				lastQosTime = aMsg.getRealTime();
				// 04-22 11:07:05.356: I/EmailReceive(13145):
				// recv_time_ms::120002
				// 04-22 11:07:05.356: I/EmailReceive(13145): recv_size::720896
				// 04-22 11:07:05.356: I/EmailReceive(13145): recv_progress::51
				// 04-22 11:07:05.356: I/EmailReceive(13145):
				// recv_max_speed::525338
				// 04-22 11:07:05.356: I/EmailReceive(13145): recv_min_speed::0
				// 04-22 11:07:05.356: I/EmailReceive(13145):
				// recv_avg_speed::48058
				// 04-22 11:07:05.356: I/EmailReceive(13145):
				// recv_cur_speed::524288
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

			case EMAIL_RECV_INITED:
				LogUtil.i(tag, "recv EMAIL_RECV_INITED\t" + aMsg.data);
				// 是否要保存文件
				File file = new File(downloadPath);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				int do_save = file.exists() ? 1 : 0;
				int serverType = pop3Model.getEmailServer().toLowerCase(Locale.getDefault()).contains("imap") ? 1 : 0;
				StringBuilder event_data = new StringBuilder();
				event_data.append("local_if::").append("").append("\n");
				event_data.append("play_time_ms::").append(pop3Model.getTimeOut() * 1000).append("\n");
				event_data.append("ps_call::").append(pop3Model.getPsCall()).append("\n");
				event_data.append("connect_timeout_ms::").append(pop3Model.getTimeOut() * 1000).append("\n"); // 连接失败的设置没有生效
				event_data.append("nodata_timeout_ms::").append("180000").append("\n");
				event_data.append("qos_inv_ms::").append("1000").append("\n");
				event_data.append("parent_processid::").append("0").append("\n");
				event_data.append("serv_driver::").append(serverType).append("\n");
				event_data.append("serv_name::").append(pop3Model.getEmailServer()).append("\n");
				event_data.append("serv_port::").append(pop3Model.getPort()).append("\n");
				event_data.append("use_ssl::").append(pop3Model.getUseSSL()).append("\n");
				event_data.append("user_name::").append(pop3Model.getAccount()).append("\n");
				event_data.append("user_pwd::").append(pop3Model.getPassword()).append("\n");
				event_data.append("email_index::").append("0").append("\n");
				event_data.append("do_save::").append(do_save).append("\n");
				event_data.append("recv_folder::").append(downloadPath); // 得重新指定路径
				LogUtil.i(tag, event_data.toString());
				EventBytes.Builder(mContext, RcuEventCommand.EMAIL_POP3_START).addStringBuffer(pop3Model.getAccount())
						.addStringBuffer(pop3Model.getEmailServer()).addInteger(pop3Model.getEmailServerTye()).writeToRcu(aMsg.getRealTime());
				this.sendStartCommand(event_data.toString());
				break;

			case EMAIL_RECV_CONNECT_START:
				LogUtil.i(tag, "recv EMAIL_RECV_CONNECT_START\r\n");
				this.connectTime = aMsg.getRealTime();
				// 存储和显示
//				writeRcuEventWithByte(RcuEventCommand.EMAIL_POP3_SOCK_CONNECTING,pop3Model.getEmailServerTye(), aMsg.getRealTime(),
//						RcuEventCommand.EMAIL_TYPE_NORMAL); 
				EventBytes.Builder(mContext, RcuEventCommand.EMAIL_POP3_SOCK_CONNECTING).addInteger(pop3Model.getEmailServerTye()).writeToRcu(aMsg.getRealTime());
				break;

			case EMAIL_RECV_FAILED:
				LogUtil.i(tag, "recv EMAIL_RECV_FAILED\r" + aMsg.data);
				int reason = 0;
				try {
					reason = Integer.parseInt(aMsg.data.split("\n")[0].split("::")[1].trim());
				} catch (Exception e) {

				}
				fail(reason, aMsg.getRealTime());
				break;
			case EMAIL_RECV_LOGIN_START:
				loginTime = aMsg.getRealTime();
				LogUtil.i(tag, "recv EMAIL_RECV_LOGIN_START\r\n");
				break;
			case EMAIL_RECV_LOGIN_SUCCESS:
				LogUtil.i(tag, "recv EMAIL_RECV_LOGIN_SUCCESS\r\n");
				writeRcuEvent(RcuEventCommand.EMAIL_POP3_LOGIN_SUCCESS, aMsg.getRealTime(),
						(int) (aMsg.getRealTime() - loginTime) / 1000);
				break;
			case EMAIL_RECV_LOGIN_FAILED:
				LogUtil.i(tag, "recv EMAIL_RECV_LOGIN_FAILED\r\n");
				reason = 0;
				try {
					reason = Integer.parseInt(aMsg.data.split("\n")[0].split("::")[1].trim());
				} catch (Exception e) {

				}
				writeRcuEvent(RcuEventCommand.EMAIL_POP3_LOGIN_FAILURE, aMsg.getRealTime(), reason);
				break;
			case EMAIL_SEND_DNSRESOLVE_START:
				LogUtil.i(tag, "recv EMAIL_SEND_DNSRESOLVE_START\r\n");
				break;
			case EMAIL_SEND_DNSRESOLVE_SUCCESS:
				LogUtil.i(tag, "recv EMAIL_SEND_DNSRESOLVE_SUCCESS\r\n");
				break;
			case EMAIL_SEND_DNSRESOLVE_FAILED:
				LogUtil.i(tag, "recv EMAIL_SEND_DNSRESOLVE_FAILED\r\n");
				break;
			case EMAIL_RECV_CONNECT_SUCCESS:
				LogUtil.i(tag, "recv EMAIL_RECV_CONNECT_SUCCESS\r\n");
				writeRcuEvent(RcuEventCommand.EMAIL_POP3_SOCK_CONNECT_SUCCESS, aMsg.getRealTime(),
						(int) (aMsg.getRealTime() - connectTime) / 1000);
				break;
			case EMAIL_RECV_CONTENT_OK:
				LogUtil.i(tag, "recv EMAIL_RECV_CONTENT_OK\r\n");
				break;
			case EMAIL_RECV_CONNECT_FAILED:
				LogUtil.w(tag, "recv EMAIL_RECV_CONNECT_FAILED:\r\n" + aMsg.data);
				reason = 0;
				try {
					reason = Integer.parseInt(aMsg.data.split("\n")[0].split("::")[1].trim());
				} catch (Exception e) {

				}
				writeRcuEvent(RcuEventCommand.EMAIL_POP3_SOCK_CONNECT_FAILURE, aMsg.getRealTime(), reason);
				break;

			case EMAIL_RECV_SENDRETRCMD:
				LogUtil.i(tag, "recv EMAIL_RECV_CMD_REQUEST:\r\n" + aMsg.data);
				sendGetTime = aMsg.getRealTime();
				fileSize = Integer.parseInt(aMsg.data.split("::")[1]);
				writeRcuEvent(RcuEventCommand.EMAIL_POP3_SEND_RETR_CMD, aMsg.getRealTime(), (int) fileSize);
				break;

			case EMAIL_RECV_FIRSTDATA:
				LogUtil.i(tag, "recv EMAIL_RECV_FIRSTDATA:" + aMsg.data);
				firstDataTime = aMsg.getRealTime();
				lastQosTime = firstDataTime;
				writeRcuEvent(RcuEventCommand.EMAIL_POP3_FIRST_DATA, aMsg.getRealTime());
				break;

			case EMAIL_RECV_LASTDATA:
				LogUtil.i(tag, "recv EMAIL_RECV_LASTDATA:" + aMsg.data);
				lastDataTime = aMsg.getRealTime();
				lastData(aMsg.getRealTime());
				break;

			case EMAIL_RECV_DROP:
				LogUtil.w(tag, "recv EMAIL_RECV_DROP:" + aMsg.data);
				reason = Integer.parseInt(aMsg.data.split("::")[1].trim());
				drop(reason, aMsg.getRealTime());
				break;

			case EMAIL_RECV_FINISH:
				LogUtil.i(tag, "recv EMAIL_RECV_FINISH\r\n");
				break;

			case EMAIL_RECV_QUIT:
				LogUtil.i(tag, "recv EMAIL_RECV_QUIT\r\n");
				// 结束本次测试
				this.stopProcess(TestService.RESULT_SUCCESS);
				break;
			}
		}

		@Override
		protected void prepareTest() {

		}

		@Override
		public synchronized void fail(int reason, long time) {

			// fail、drop、lastData三个事件互斥
			if (!hasFail && firstDataTime == 0 && !hasDrop && !hasLastData) {
				hasFail = true;
				EventBytes.Builder(mContext, RcuEventCommand.EMAIL_POP3_FAILURE).addInteger(reason).writeToRcu(time);
				disConnect();

				// 结束本次测试
				this.stopProcess(TestService.RESULT_FAILD);
			}

		}

		@Override
		public synchronized void drop(int reason, long time) {

			if (!hasDrop && !hasFail && firstDataTime != 0 && !hasLastData) {
				hasDrop = true;
				int drop = (int) (time - firstDataTime);
				// 存储
				writeRcuEvent(RcuEventCommand.EMAIL_POP3_DROP, time, drop / 1000, (int) transByte, reason);

				// 统计(有Fitstdata和Drop时一次完整统计)
				if (reason == RcuEventCommand.DropReason.USER_STOP.getReasonCode()) {
					// 前后台规范文档描述：在文件未下载完成之前，人工强制停止测试，需报告Drop事件，
					// Drop原因：User Stop，但该次下载计为下载成功
					totalResult(TotalAppreciation._EmailReceiveTry, 1);
					totalResult(TotalAppreciation._EmailReceiveSuccess, 1);
					totalResult(TotalAppreciation._EmailReceSumSize, transByte);
					totalResult(TotalAppreciation._EmailReceAllTime, delayTime);
				} else {
					totalResult(TotalAppreciation._EmailReceiveTry, 1);
					totalResult(TotalAppreciation._EmailReceiveSuccess, 0);
				}

				disConnect();

				// 结束本次测试
				this.stopProcess(TestService.RESULT_SUCCESS);
			}

		}

		@Override
		protected void sendCurrentRate() {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			// 当前速率kbps(Email的瞬时速率有问题，业务库说这个改不了，这里显示平均速率 )
			dataMap.put(DataTaskValue.BordCurrentSpeed.name(), UtilsMethod.decFormat.format(currentSpeed / UtilsMethod.kbyteRage));
			// 平均速率kbps
			dataMap.put(DataTaskValue.BordLeftTitle.name(), getString(R.string.info_data_averagerate) + "@"
					+ UtilsMethod.decFormat.format(avgRate / UtilsMethod.kbyteRage) + getString(R.string.info_rate_kbps));

			// 峰值
			dataMap.put(WalkStruct.DataTaskValue.PeakValue.name(), UtilsMethod.decFormat.format(peakValue));
			// 进度
			dataMap.put(DataTaskValue.BordProgress.name(), UtilsMethod.decFormat.format(progress));

			callbackHandler.obtainMessage(DATA_CHANGE, dataMap).sendToTarget();

			// QOS写入到RCU
			UtilsMethod.sendWriteRcuFtpData(getApplicationContext(), WalkCommonPara.MsgDataFlag_L, 0x00, (int) delayTime,
					transByte, 1000, (int) currentBytes);
		}

		@Override
		public synchronized void lastData(long time) {
			// fail、drop、lastData三个事件互斥
			if (!hasLastData && firstDataTime != 0 && !hasFail && !hasDrop) {
				hasLastData = true;

				// 写事件
				EventBytes.Builder(mContext, RcuEventCommand.EMAIL_POP3_LAST_DATA).addInteger((int) transByte)
						.addInteger((int) delayTime).writeToRcu(time);

				// 统计(有Fitstdata和LastData时一次完整统计)
				totalResult(TotalAppreciation._EmailReceiveTry, 1);
				totalResult(TotalAppreciation._EmailReceiveSuccess, 1);
				totalResult(TotalAppreciation._EmailReceSumSize, transByte);
				totalResult(TotalAppreciation._EmailReceAllTime, delayTime);

				disConnect();
			}
		}

		public synchronized void disConnect() {
			if (connectedTime != 0) {
				writeRcuEvent(RcuEventCommand.EMAIL_POP3_RETR_FINISHED, connectedTime);
			}
		}

	}

}
