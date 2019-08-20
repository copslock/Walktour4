package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.util.Log;

import com.dingli.service.test.ipc2msg;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.RcuEventCommand.FailReason;
import com.walktour.Utils.TotalStruct.TotalFtp;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.DataTaskValue;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigFtp;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.task.multiftp.upload.TaskMultiftpUploadModel;
import com.walktour.model.FTPGroupModel;
import com.walktour.model.FtpServerModel;
import com.walktour.service.TestService;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * FTP复合上传测试业务
 *
 * @author jianchao.wang
 */
@SuppressLint({"SdCardPath", "HandlerLeak"})
public class FtpMultiUpTest extends FtpBaseTest {

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(tag, "onDestroy");
        // 因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
        UtilsMethod.killProcessByPname("com.walktour.service.test.FtpMultiUpTest", false);
        UtilsMethod.killProcessByPname(AppFilePathUtil.getInstance().getAppLibFile("datatests_android").getAbsolutePath(), false);
    }

    private class MftpUpHandler extends DataTestHandler {
        /**
         * mftp_up_item.h
         */
        private static final int MFTP_UP_TEST = 89;

        private static final int MFTP_UP_INITED = 1;
        private static final int MFTP_UP_CONNECT_START = 2;
        private static final int MFTP_UP_CONNECT_SUCCESS = 3;
        private static final int MFTP_UP_CONNECT_FAILED = 4;
        private static final int MFTP_UP_LOGIN_SUCCESS = 5;
        private static final int MFTP_UP_LOGIN_FAILED = 6;
        private static final int MFTP_UP_DOES_SUPPORT_APPE = 7;
        private static final int MFTP_UP_FILE_SIZE = 8;
        private static final int MFTP_UP_SEND_STOR = 9;
        private static final int MFTP_UP_FIRSTDATA = 10;
        private static final int MFTP_UP_QOS_ARRIVED = 11;
        private static final int MFTP_UP_COUNT_START = 12;
        private static final int MFTP_UP_DROP = 15;
        private static final int MFTP_UP_FINISH = 16;
        private static final int MFTP_UP_QUIT = 17;
        private static final int MFTP_UP_FAILED = 18;
        private static final int MFTP_UP_NODATA_NEED_PING = 23;
        // private static final int MFTP_UP_DATA_CONNECT_OK = 25;
        private static final int MFTP_UP_MSG = 101;

        private static final int MFTP_UP_START_TEST = 1001;
        private static final int MFTP_UP_STOP_TEST = 1006;

        private TaskMultiftpUploadModel ftpModel = null;

        private int serverCount = 0;
        private String ips = "";
        private String ports = "";
        private String users = "";
        private String passwords = "";
        private String serverNames = "";
        private String serverPaths = "";
        private String localFilePaths = "";
        private String localFileSizes = "";
        private String anonymous = "";
        private String trans_modes = "";
        private String data_modes = "";

        // Start Count之前的值
        private long delayTimeA = 0;
        private long transByteA = 0;
        private long startCountTime = 0;

        // 各Session的详情
        String sessionBytes;
        String sessionTime;
        String sessionResult;
        double sessionSuccessRate;

        public MftpUpHandler(TaskMultiftpUploadModel ftpModel) {
            super("-m mftp_up -z " + AppFilePathUtil.getInstance().getAppConfigDirectory(),
                    AppFilePathUtil.getInstance().getAppLibFile(useRoot ? "datatests_android" : "libdatatests_so.so").getAbsolutePath(),
                    MFTP_UP_TEST, MFTP_UP_START_TEST, MFTP_UP_STOP_TEST);
            this.ftpModel = ftpModel;
        }

        public void handleMessage(android.os.Message msg) {
            ipc2msg aMsg = (ipc2msg) msg.obj;
            if (aMsg.test_item != MFTP_UP_TEST) {
                return;
            }
            hasEventCallBack = true;    //设置业务库有事件回调
            LogUtil.w(tag, "--mftpu:" + aMsg.event_id + "--data:" + aMsg.data);
            switch (aMsg.event_id) {
                case MFTP_UP_QOS_ARRIVED:
                    String strQos = aMsg.data;
                    Log.i(tag, strQos + "\r\n\r\n-------------------------");
                    // 07-05 12:24:52.455: I/FtpTest(6018): measure_time::1009
                    // 07-05 12:24:52.455: I/FtpTest(6018): down_bytes::64296
                    // 07-05 12:24:52.455: I/FtpTest(6018): down_progress::0
                    // 07-05 12:24:52.455: I/FtpTest(6018): down_speed_max::509779.00
                    // 07-05 12:24:52.455: I/FtpTest(6018): down_speed_min::509779.00
                    // 07-05 12:24:52.455: I/FtpTest(6018): down_speed_avg::509779.00
                    // 07-05 12:24:52.455: I/FtpTest(6018): down_bytes_cur::64296
                    // 07-05 12:24:52.455: I/FtpTest(6018): down_speed_cur::509779.00

                    getSessionInfo(aMsg.data);

                    String[] qos = aMsg.data.split("\n");
                    if (startCountTime == 0) {
                        delayTimeA = Long.parseLong(qos[0].split("::")[1]);
                        transByteA = Long.parseLong(qos[1].split("::")[1]);
                        avgRate = (long) Double.parseDouble(qos[5].split("::")[1]);
                    } else {
                        // 传输时间和大小从start_Count之后算起
                        delayTime = Long.parseLong(qos[0].split("::")[1]) - delayTimeA;
                        transByte = Long.parseLong(qos[1].split("::")[1]) - transByteA;
                        // 平均速率按大小和时间来算
                        avgRate = (transByte * 8) * 1000 / delayTime;
                    }
                    progress = (long) Double.parseDouble(qos[2].split("::")[1]);
                    peakValue = (long) Double.parseDouble(qos[3].split("::")[1]);
                    currentBytes = Long.parseLong(qos[6].split("::")[1]);
                    currentSpeed = (long) Double.parseDouble(qos[7].split("::")[1]);
                    activeThreadNum = 0;// Integer.parseInt(qos[8].split("::")[1]);
                    sendCurrentRate();
                    break;

                case MFTP_UP_INITED:
                    setMainBussinessDirectType(1);
                    Log.i(tag, "recv MFTP_UP_INITED\r\n");
                    Log.i(tag, aMsg.data + "\r\n");
                    StringBuilder event_data = new StringBuilder();
                    event_data.append("local_if::").append("").append("\n");
                    event_data.append("play_time_ms::").append(ftpModel.getKeepTime() * 1000).append("\n");
                    event_data.append("ps_call::").append(ftpModel.getTestMode() == 1 ? "1" : "0").append("\n");
                    event_data.append("nodata_timeout_ms::").append(ftpModel.getNoData() * 1000).append("\n");
                    event_data.append("qos_inv_ms::").append("1000").append("\n");
                    event_data.append("current_task_count::").append(serverCount).append("\n");
                    event_data.append("conn_reconn_count::").append("3").append("\n");
                    event_data.append("count_wait_time_ms::").append(ftpModel.getWaitTime() * 1000).append("\n");
                    // 分组内容-----------------------------------
                    event_data.append("serv_host::").append(ips).append("\n");
                    event_data.append("serv_port::").append(ports).append("\n");
                    event_data.append("user_name::").append(users).append("\n");
                    event_data.append("password::").append(passwords).append("\n");
                    event_data.append("anonymous::").append(anonymous).append("\n");
                    event_data.append("serv_path::").append(serverPaths).append("\n");
                    event_data.append("serv_name::").append(serverNames).append("\n");
                    event_data.append("up_filename::").append(localFilePaths).append("\n");
                    event_data.append("up_filesize::").append(localFileSizes).append("\n");
                    event_data.append("trans_mode::").append(trans_modes).append("\n");
                    event_data.append("data_mode::").append(data_modes).append("\n");
                    // end 分组内容-----------------------------------
                    event_data.append("thread_count::").append(ftpModel.getThreadCount()).append("\n");// 建议用单线程
                    event_data.append("thread_mode::").append(ftpModel.getThreadMode()).append("\n");
                    // 2014.2.11 添加新接口V1.4.18_20140109
                    // 网络类型 1:GSM、CDMA(2G) 2:TD_SCDMA 3:CDMA2000 4:WCDMA 5:LTE
                    event_data.append("network_type::").append(8).append("\n");
                    event_data.append("sendbuff_size::").append(ftpModel.getMftpUploadTestConfig().getSendBuffer()).append("\n");
                    event_data.append("recvbuff_size::").append(ftpModel.getMftpUploadTestConfig().getReceBuffer()).append("\n");
                    event_data.append("multitest_type::").append(ftpModel.getEndCodition() + 1); // //1表示最短session方式
                    // 2:表示最长session方式（所有session完成才结束),泰国要求为2
                    Log.i(tag, event_data.toString());
                    this.sendStartCommand(event_data.toString());
                    break;

                case MFTP_UP_CONNECT_START:
                    Log.i(tag, "recv MFTP_UP_CONNECT_START\r\n");
                    connectTime = aMsg.getRealTime();
                    showEvent("Multi FTP Upload Connect");
                    EventBytes.Builder(mContext, RcuEventCommand.Multi_FTP_UL_ConnectStart)
                            .addInteger(ftpModel.getEndCodition() == 0 ? 1 : 2).addStringBuffer(serverNames)
                            .writeToRcu(aMsg.getRealTime());
                    break;

                case MFTP_UP_CONNECT_SUCCESS:
                    Log.i(tag, "recv MFTP_UP_CONNECT_SUCCESS\r\n");
                    connectedTime = aMsg.getRealTime();
                    int delayConnect = (int) (connectedTime - connectTime) / 1000;
                    showEvent(String.format("Multi FTP Upload Connect Success:Delay %d(ms)", delayConnect));
                    EventBytes.Builder(mContext, RcuEventCommand.Multi_FTP_UL_ConnectSuccess).writeToRcu(aMsg.getRealTime());
                    break;

                case MFTP_UP_CONNECT_FAILED:
                    Log.i(tag, "recv MFTP_UP_CONNECT_FAILED\r\n");
                    Log.i(tag, aMsg.data + "\r\n");
                    // 07-05 11:15:48.115: I/FtpTest(18979): recv MFTP_UP_CONNECT_FAILED
                    // 07-05 11:15:48.115: I/FtpTest(18979): task_id::1
                    // 07-05 11:15:48.115: I/FtpTest(18979): ereason::-1
                    // RCU文档没有定义单独的ConnectFail事件，当成是业务失败
                    fail(FailReason.CONNECT_TIMEOUT.getReasonCode(), aMsg.getRealTime());
                    break;

                case MFTP_UP_LOGIN_SUCCESS:

                    Log.i(tag, "recv MFTP_UP_LOGIN_SUCCESS\r\n");
                    int delayLogin = (int) (aMsg.getRealTime() - connectedTime) / 1000;
                    EventBytes.Builder(mContext, RcuEventCommand.Multi_FTP_UL_LoginSuccess).addInteger(delayLogin)
                            .writeToRcu(aMsg.getRealTime());
                    showEvent(String.format("Multi FTP Upload Login Success:Delay %d(ms)", delayLogin));
                    break;

                case MFTP_UP_LOGIN_FAILED:
                    Log.i(tag, "recv MFTP_UP_LOGIN_FAILED\r\n");
                    Log.i(tag, aMsg.data + "\r\n");
                    // 07-16 09:55:31.815: I/FtpTest(7897): recv MFTP_DOWN_LOGIN_FAILED
                    // 07-16 09:55:31.815: I/FtpTest(7897): task_id::0
                    // 07-16 09:55:31.815: I/FtpTest(7897): ereason::1
                    int loginFailCode = getReason(aMsg.data.split("\n")[1]);
                    loginFail(loginFailCode, aMsg.getRealTime());
                    break;

                case MFTP_UP_DOES_SUPPORT_APPE:
                    Log.i(tag, "recv MFTP_UP_DOES_SUPPORT_REST\r\n");
                    Log.i(tag, aMsg.data + "\r\n");
                    // 07-05 12:23:04.045: I/FtpTest(4982): recv MFTP_UP_DOES_SUPPORT_REST
                    // 07-05 12:23:04.045: I/FtpTest(4982): task 0support=1
                    // 07-05 12:23:04.045: I/FtpTest(4982): task 1support=1
                    String[] supports = aMsg.data.split("\n");
                    String supportsStr = "";
                    String supportsEvent = "";
                    for (int i = 0; i < supports.length; i++) {
                        supports[i] = supports[i].split("=")[1];
                        supportsStr += (i > 0 ? ";" : "") + supports[i];
                        supportsEvent += supports[i];
                    }
                    // 存储
                    EventBytes.Builder(mContext, RcuEventCommand.Multi_FTP_UL_SupportReset)
                            .addCharArray(supportsEvent.toCharArray(), 24).writeToRcu(aMsg.getRealTime());
                    // 显示
                    showEvent("Multi FTP Upload Reset Support:" + supportsStr);
                    break;

                case MFTP_UP_FILE_SIZE:
                    Log.i(tag, "recv MFTP_UP_FILE_SIZE\r\n");
                    Log.i(tag, aMsg.data + "\r\n");
                    // 07-05 12:23:04.175: I/FtpTest(4982): recv MFTP_UP_FILE_SIZE
                    // 07-05 12:23:04.175: I/FtpTest(4982): task 0file_size=2168797
                    // 07-05 12:23:04.175: I/FtpTest(4982): task 1file_size=1083415103
                    String fileSizesBytes = "";
                    String fileSizesStr = "";
                    String[] fileSizes = aMsg.data.split("\n");
                    for (int i = 0; i < fileSizes.length; i++) {
                        fileSizes[i] = fileSizes[i].split("=")[1];
                        fileSizesBytes += (i > 0 ? ";" : "") + fileSizes[i];
                        fileSizesStr += (i > 0 ? ";" : "") + (Integer.parseInt(fileSizes[i]) / UtilsMethod.kbyteRage) + "KB";
                    }
                    // 存储
                    EventBytes.Builder(mContext, RcuEventCommand.Multi_FTP_UL_AllSendStorCmd)
                            .addCharArray(fileSizesBytes.toCharArray(), 128).addInteger(fileSizes.length)
                            .writeToRcu(aMsg.getRealTime());
                    // 显示
                    showEvent("Multi FTP Upload Send STOR:" + fileSizesStr);
                    break;

                case MFTP_UP_SEND_STOR:
                    Log.i(tag, "recv MFTP_UP_SEND_RETR\r\n");
                    sendGetTime = aMsg.getRealTime();
                    break;

                case MFTP_UP_FAILED:
                    Log.i(tag, "recv MFTP_UP_FAILED\r\n");
                    Log.i(tag, aMsg.data + "\r\n");
                    // 07-22 15:02:22.245 I/FtpTest (13755): recv MFTP_UP_FAILED
                    // 07-22 15:02:22.245 I/FtpTest (13755): reason::1025
                    // 07-22 15:02:22.245 I/FtpTest (13755): desc::STOR response timeout
                    // 07-22 15:02:22.245 I/FtpTest (13755):
                    int failCode = getReason(aMsg.data.split("\n")[0]);
                    fail(failCode, aMsg.getRealTime());
                    break;

                case MFTP_UP_FIRSTDATA:
                    Log.i(tag, "recv MFTP_UP_FIRSTDATA\r\n");
                    firstDataTime = aMsg.getRealTime();
                    startCountTime = aMsg.getRealTime();
                    // 存储
                    EventBytes.Builder(mContext, RcuEventCommand.Multi_FTP_UL_AllFirstData).writeToRcu(aMsg.getRealTime());

                    // 设置主进程中的firstdata状态
                    setMainFirstDataState(true);

                    // 显示
                    showEvent("Multi FTP Upload First Data");
                    break;

                case MFTP_UP_COUNT_START:
                    Log.i(tag, "recv MFTP_UP_COUNT_START\r\n");
                    startCountTime = aMsg.getRealTime();
                    EventBytes.Builder(mContext, RcuEventCommand.Multi_FTP_UL_CountDownStart).writeToRcu(aMsg.getRealTime());
                    showEvent("Multi FTP Upload Count Down Start");
                    break;

                case MFTP_UP_MSG:
                    LogUtil.i(tag, aMsg.data);
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
                        LogUtil.w(tag, "MFTP_UP_MSG", e);
                    }
                    break;

                case MFTP_UP_DROP:
                    Log.i(tag, "recv MFTP_UP_DROP\r\n");
                    Log.i(tag, aMsg.data + "\r\n");
                    int dropReason = Integer.parseInt(getResponseResult(aMsg.data, "reason"));
                    getSessionInfo(aMsg.data);
                    drop(dropReason, aMsg.getRealTime());
                    break;

                case MFTP_UP_FINISH:
                    Log.i(tag, "recv MFTP_UP_FINISH\r\n");
                    Log.i(tag, aMsg.data + "\r\n");
                    // 02-13 10:09:14.161: I/FtpTest(13309): recv MFTP_DOWN_FINISH
                    // 02-13 10:09:14.161: I/FtpTest(13309):
                    // session_recv_bytes::334334;407532
                    // 02-13 10:09:14.161: I/FtpTest(13309): session_recv_time::50374;56999
                    // 02-13 10:09:14.161: I/FtpTest(13309): session_result::1;0
                    // 02-13 10:09:14.161: I/FtpTest(13309): session_success_ratio::0.50
                    lastDataTime = aMsg.getRealTime();
                    getSessionInfo(aMsg.data);
                    lastData(aMsg.getRealTime());
                    break;

                case MFTP_UP_QUIT:
                    Log.i(tag, "recv MFTP_UP_QUIT\r\n");
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

                case MFTP_UP_NODATA_NEED_PING:
                    Log.i(tag, "recv FTP_UP_NODATA_NEED_PING\r\n");
                    break;

            }
        }

        @Override
        protected void prepareTest() {
            ConfigFtp config = new ConfigFtp();
            ArrayList<FTPGroupModel> groups = ftpModel.getFtpServers();
            for (int i = 0; i < groups.size(); i++) {
                FTPGroupModel g = groups.get(i);

                if (g.getEnable() == 1) {

                    FtpServerModel server = config.getFtpServerModel(g.getFtpServerName());
                    ips += ((serverCount > 0) ? ";" : "") + server.getIp();
                    ports += ((serverCount > 0) ? ";" : "") + server.getPort();
                    users += ((serverCount > 0) ? ";" : "") + server.getLoginUser();
                    passwords += ((serverCount > 0) ? ";" : "") + server.getLoginPassword();
                    serverPaths += ((serverCount > 0) ? ";" : "") + g.getUploadFilePath();
                    anonymous += ((serverCount > 0) ? ";" : "") + 0;
                    data_modes += ((serverCount > 0) ? ";" : "") + 1;
                    trans_modes += ((serverCount > 0) ? ";" : "") + 1;

                    if (g.getFileSource() == 0) {
                        // 指定大小时，路径填空
                        localFilePaths += ((serverCount > 0) ? ";" : "") + "null";
                        localFileSizes += ((serverCount > 0) ? ";" : "") + (int) (g.getFileSize() * UtilsMethod.kbyteRage);
                        serverNames += ((serverCount > 0) ? ";" : "") + "ftpupload";
                    } else {
                        // 指定文件时，大小为0
                        File localFile = new File(g.getLocalFile());

                        // 不指定大小
                        localFilePaths += ((serverCount > 0) ? ";" : "") + localFile.getAbsolutePath();
                        localFileSizes += ((serverCount > 0) ? ";" : "") + 0;
                        serverNames += ((serverCount > 0) ? ";" : "") + localFile.getName();
                        Log.e(tag, String.format("%s,%d", localFile.getAbsolutePath(), localFile.length()));
                    }

                    serverCount++;
                }

            }

            // 确保上传路径是UFT-8编码
            try {
                serverPaths = new String(serverPaths.getBytes(), "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // try {
            // String hexutf8 = UtilsMethod.getBytesHex( serverPaths.getBytes("UTF-8")
            // );
            // String hexgbk = UtilsMethod.getBytesHex( serverPaths.getBytes("GBK") );
            // Log.i(tag,hexutf8+"--->"+hexutf8.length()/2 );
            // Log.i(tag,hexgbk+"--->"+hexgbk.length()/2 );
            //
            // if( hexutf8.length()!= hexgbk.length() ){
            // serverPaths = new String( serverPaths.getBytes(),"UTF-8" );
            // }
            // } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
            // }

        }

        /**
         * 登录失败,登录失败后不会再有业务失败
         *
         * @param loginFailCode
         */
        protected void loginFail(int loginFailCode, long time) {
            if (!hasFail) {
                hasFail = true;

                int delayLoginFail = (int) (time - connectedTime) / 1000;
                // 存储
                EventBytes.Builder(mContext, RcuEventCommand.Multi_FTP_UL_LoginFailure).addInteger(delayLoginFail)
                        .addInteger(loginFailCode).writeToRcu(time);
                // 显示
                String loginFailStr = FailReason.getFailReason(loginFailCode).getResonStr();
                showEvent(String.format("Multi FTP Upload Login Failure:%s", loginFailStr));

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
                        stopProcess(TestService.RESULT_FAILD);
                    }
                }.start();

            }

        }

        @Override
        protected synchronized void fail(int failReason, long time) {
            // fail、drop、lastData三个事件互斥
            if (!hasFail && startCountTime == 0 && !hasDrop && !hasLastData) {
                hasFail = true;

                EventBytes.Builder(mContext, RcuEventCommand.Multi_FTP_UL_Fail).addInteger(failReason).writeToRcu(time);

                String failStr = FailReason.getFailReason(failReason).getResonStr();
                showEvent(String.format("Multi FTP Upload Failure:%s", failStr));

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
                        stopProcess(TestService.RESULT_FAILD);
                    }
                }.start();

            }
        }

        /**
         * 这个drop有可能在TestTaskService中被User Stop,PPP Drop或Out of Service引用
         * ,不管是哪个，最后都得退出进程
         */
        @Override
        protected synchronized void drop(int reason, long time) {
            // fail、drop、lastData三个事件互斥
            if (!hasDrop && !hasFail && startCountTime != 0 && !hasLastData) {
                hasDrop = true;

                int dropDelay = (int) (time - startCountTime) / 1000;

                EventBytes.Builder(mContext, RcuEventCommand.Multi_FTP_UL_Drop).addInteger(dropDelay / 1000).addInt64(transByte)
                        .addInteger(reason).addCharArray(sessionBytes.toCharArray(), 512)
                        .addCharArray(sessionTime.toCharArray(), 512).addCharArray(sessionResult.toCharArray(), 128)
                        .addDouble(sessionSuccessRate).writeToRcu(time);

                showEvent(
                        String.format(
                                "Multi FTP Upload Drop:" + "Delay %d(s)," + "Mean Rate:%.2f kbps," + "Transmit Size:%.2f Kbytes,"
                                        + "%s",
                                dropDelay / 1000, avgRate / UtilsMethod.kbyteRage, transByte / UtilsMethod.kbyteRage,
                                getDropReasonString(reason)));

                // 统计页面
                if ((reason == RcuEventCommand.DROP_USERSTOP || reason == RcuEventCommand.DROP_TIMEOUT)
                        && startCountTime != 0) {
                    totalFtpResult(this, TotalFtp.m_uptrys, 1);
                    totalFtpResult(this, TotalFtp.m_upSuccs, 1);
                    totalFtpResult(this, TotalFtp.m_upDrops, 0);
                    totalFtpResult(this, TotalFtp.m_upCurrentSize, (transByte));
                    totalFtpResult(this, TotalFtp.m_upCurrentTimes, dropDelay);
                } else {
                    totalFtpResult(this, TotalFtp.m_uptrys, 1);
                    totalFtpResult(this, TotalFtp.m_upSuccs, 0);
                    totalFtpResult(this, TotalFtp.m_upDrops, 1);
                }
                    totalFtpResult(this,TotalFtp.m_up_max_value,peakValue);
                // 统计参数
                if (reason != RcuEventCommand.DROP_OUT_OF_SERVICE && reason != RcuEventCommand.DROP_PPPDROP) {
                    sendTotalFtpPara(true, (int) (lastDataTime - startCountTime) / 1000);
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
        protected void sendCurrentRate() {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            // 当前速率kbps
            dataMap.put(WalkStruct.DataTaskValue.FtpUlThrput.name(),
                    UtilsMethod.decFormat.format(currentSpeed / UtilsMethod.kbyteRage));
            dataMap.put(DataTaskValue.BordCurrentSpeed.name(),
                    UtilsMethod.decFormat.format(currentSpeed / UtilsMethod.kbyteRage));
            // 平均速率kbps
            dataMap.put(WalkStruct.DataTaskValue.FtpUlMeanRate.name(),
                    UtilsMethod.decFormat.format(avgRate / UtilsMethod.kbyteRage));
            dataMap.put(DataTaskValue.BordLeftTitle.name(), getString(R.string.info_data_averagerate) + "@"
                    + UtilsMethod.decFormat.format(avgRate / UtilsMethod.kbyteRage) + getString(R.string.info_rate_kbps));

            // 当前活动线程数
            dataMap.put(WalkStruct.DataTaskValue.ActiveThreadNum.name(), activeThreadNum);
            // 峰值
            dataMap.put(WalkStruct.DataTaskValue.PeakValue.name(), UtilsMethod.decFormat.format(peakValue));
            // 当前进度FtpDlProgress 如果是PS CALL 按时间比例计算
            int p = (int) (ftpModel.getTestMode() == 1 ? (delayTime * 100 / (ftpModel.getKeepTime() * 1000)) : progress);
            p = p > 100 ? 100 : p;// 业务库有可能上报大于100的进度
            dataMap.put(WalkStruct.DataTaskValue.FtpUlProgress.name(), UtilsMethod.decFormat.format(p));
            dataMap.put(DataTaskValue.BordProgress.name(), UtilsMethod.decFormat.format(p));
            // 传输大小
            dataMap.put(WalkStruct.DataTaskValue.FtpUlCurrentSize.name(),
                    UtilsMethod.decFormat.format(transByte * 8f / UtilsMethod.kbyteRage));
            dataMap.put(WalkStruct.DataTaskValue.FtpUlAllSize.name(),
                    UtilsMethod.decFormat.format(fileSize * 8f / UtilsMethod.kbyteRage));

            callbackHandler.obtainMessage(DATA_CHANGE, dataMap).sendToTarget();

            // QOS写入到RCU
            UtilsMethod.sendWriteRcuFtpData(getApplicationContext(), WalkCommonPara.MsgDataFlag_G, 0x01, (int) delayTime,
                    transByte, 1000, (int) currentBytes);

        }

        @Override
        protected synchronized void lastData(long time) {

            // 文件只有1K时，会未有startCoutTime 就直接FINISH
            if (!hasLastData && startCountTime != 0 && !hasFail && !hasDrop) {
                hasLastData = true;

                int finishDelay = (int) (time - startCountTime) / 1000;

                EventBytes.Builder(mContext, RcuEventCommand.Multi_FTP_UL_LastData).addInt64(transByte)
                        .addInteger((int) delayTime).addCharArray(sessionBytes.toCharArray(), 512)
                        .addCharArray(sessionTime.toCharArray(), 512).addCharArray(sessionResult.toCharArray(), 128)
                        .addDouble(sessionSuccessRate).writeToRcu(time);

                showEvent(String.format(
                        "Multi FTP Upload Last Data:" + "Delay %d(s)," + "Mean Rate:%.2f kbps," + "Transmit Size:%.2f Kbytes",
                        finishDelay / 1000, avgRate / UtilsMethod.kbyteRage, transByte / UtilsMethod.kbyteRage));

                // 统计页面
                totalFtpResult(this, TotalFtp.m_uptrys, 1);
                totalFtpResult(this, TotalFtp.m_upSuccs, 1);
                totalFtpResult(this, TotalFtp.m_upDrops, 0);
                totalFtpResult(this, TotalFtp.m_upCurrentSize, (transByte));
                totalFtpResult(this, TotalFtp.m_upCurrentTimes, finishDelay);

                // 统计参数
                sendTotalFtpPara(true, (int) (lastDataTime - sendGetTime) / 1000);

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

            // 设置主进程中的firstdata状态
            setMainFirstDataState(false);
        }

        protected synchronized void disConnect() {
            if (connectedTime != 0) {

                EventBytes.Builder(mContext, RcuEventCommand.Multi_FTP_UL_Disconnect)
                        .writeToRcu(System.currentTimeMillis() * 1000);

                showEvent("Multi FTP Upload Disconnect");
            }
        }

        private void getSessionInfo(String msg) {
            sessionBytes = getResponseResult(msg, "session_recv_bytes");
            sessionTime = getResponseResult(msg, "session_recv_time");
            sessionResult = getResponseResult(msg, "session_result");
            try {
                sessionSuccessRate = Double.parseDouble(getResponseResult(msg, "session_success_ratio"));
            } catch (Exception e) {

            }
        }

    }

    @Override
    protected boolean getDataTestHandler() {
        dataTestHandler = new MftpUpHandler((TaskMultiftpUploadModel) taskModel);
        return true;
    }
}
