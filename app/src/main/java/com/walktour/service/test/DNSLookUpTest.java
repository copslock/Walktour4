package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.dingli.service.test.ipc2jni;
import com.dingli.service.test.ipc2msg;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.RcuEventCommand.DropReason;
import com.walktour.Utils.RcuEventCommand.FailReason;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.gui.task.parsedata.model.task.dnslookup.TaskDNSLookUpModel;
import com.walktour.service.ICallback;
import com.walktour.service.IService;
import com.walktour.service.TestService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jihong Xie DNS LookUp测试Service
 */
public class DNSLookUpTest extends TestTaskService {
    /**
     * 当前测试模型 KEY
     */
    private static final String TAG = "DNSLookUp";

    // 业务ID
    private static final int DNSLOOKUP_TEST = 85;
    /* 对外发出事件 */
    /**
     * 事件参数: result(int) 0:init failed 1:init success
     **/
    private static final int DNSLOOKUP_INITED = 1; // 程序初始化完毕
    private static final int DNSLOOKUP_RESOLV_START = 2; // 解析开始
    // 事件参数: dl_resolv_succ_info
    private static final int DNSLOOKUP_RESOLV_SUCCESS = 3; // 解析成功

    /**
     * 事件参数: reason(int) 1000:normal 1:timeout 1013:host not found
     **/
    private static final int DNSLOOKUP_RESOLV_FAILED = 4; // 解析失败
    private static final int DNSLOOKUP_RESOLV_ERROR = 5;

    private static final int DNSLOOKUP_QUIT = 8; // 程序退出

    /* 外部发来事件 */
    // 事件参数: dnslookup_start_info
    private static final int DNSLOOKUP_START_TEST = 1001; // 开始业务(ID不可变)
    private static final int DNSLOOKUP_STOP_TEST = 1006; // 停止业务(ID不可变)

    private boolean isCallbackRegister = false;
    private TaskDNSLookUpModel taskModel;
    private ipc2jni aIpc2Jni;
    private boolean isRunning;// 业务是否已经启动
    private boolean isStoped = Boolean.FALSE;

    // 测试模型
    private ApplicationModel appModel = ApplicationModel.getInstance();

    /**
     * 业务回调显示信息
     */
    private String msgStr = "";

    // 远程回调
    private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();

    // 远程回调方法绑定
    private IService.Stub mBind = new IService.Stub() {
        @Override
        public void unregisterCallback(ICallback cb) throws RemoteException {
            LogUtil.i(TAG, "unregisterCallback");
            if (cb != null) {
                mCallbacks.unregister(cb);
            }
        }

        @Override
        public void stopTask(boolean isTestInterrupt, int dropReason) throws RemoteException {
            appModel.setCurrentTask(null);

            if (!isStoped) {
                isStoped = Boolean.TRUE;
                // LogUtil.d("TTTT", isTestInterrupt + "--->" + dropReason);
                if (isTestInterrupt || (dropReason != DropReason.NORMAL.getReasonCode())) {
                    FailReason fail;

                    if (isTestInterrupt)
                        fail = FailReason.USER_STOP;
                    else
                        fail = FailReason.UNKNOWN;

                    int delay = (startTime == 0 ? 0 : (int) (System.currentTimeMillis() - startTime));
                    msgStr = String.format(WalkStruct.DataTaskEvent.DNS_LOOKUP_FAILURE.toString(), delay, fail.getResonStr());
                    displayEvent(msgStr);

                    EventBytes.Builder(getApplicationContext(), RcuEventCommand.DNS_LOOKUP_FAILURE)
                            .addCharArray(taskModel.getDnsTestConfig().getUrl().toCharArray(), 256).addInteger(delay)
                            .addInteger(fail.getReasonCode()).writeToRcu(System.currentTimeMillis());

                    // 数据统计,用户手动停止算作成功
                    if (isTestInterrupt) {
                        totalMap = new HashMap<String, Integer>();
                        totalMap.put(TotalStruct.TotalDNS._dnsSuccs.name(), 1);// 成功次数
                        mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
                    }
                }
            }
        }

        @Override
        public void registerCallback(ICallback cb) throws RemoteException {
            LogUtil.i(TAG, "registerCallback");
            if (cb != null) {
                mCallbacks.register(cb);
                isCallbackRegister = true;
            }
        }

        /**
         * 返回是否执行startCommand状态, 如果改状态需要在业务中出现某种情况时才为真, 可以在继承的业务中改写该状态
         */
        public boolean getRunState() {
            return startCommondRun;
        }
    };

    public IBinder onBind(Intent intent) {
        return mBind;
    }

    ;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(tag, "---onStart");
        int startFlag = super.onStartCommand(intent, flags, startId);
        taskModel = (TaskDNSLookUpModel) super.taskModel;
        Thread thread = new Thread(new RunTest());
        thread.start();
        return startFlag;
    }

    /**
     * 开启一个新线程启动任务
     *
     * @author Jihong Xie
     */
    class RunTest implements Runnable {
        @Override
        public void run() {
            LogUtil.i(TAG, "wait for stard test");
            try {
                while (!isCallbackRegister) {
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            startTest();
        }
    }

    protected void startTest() {
        try {
            LogUtil.v(TAG, "start to prep args");

            isRunning = true;
            if (aIpc2Jni == null) {
                aIpc2Jni = new ipc2jni(mEventHandler);
            }

            LogUtil.v(TAG, "start to load jni");
            System.loadLibrary("mydns");

            aIpc2Jni.initServer(this.getLibLogPath());

            String args = "-m dnslookup -z " + AppFilePathUtil.getInstance().getAppConfigDirectory();/// mnt/sdcard/leo/";
            // String client_path =
            String client_path = AppFilePathUtil.getInstance().getAppLibFile(useRoot ? "datatests_android" : "libdatatests_so.so").getAbsolutePath();
            if (useRoot) {
                aIpc2Jni.set_su_file_path("su -c", "export LD_LIBRARY_PATH=" + AppFilePathUtil.getInstance().getAppLibDirectory());
            }
            LogUtil.v(TAG, "start to run..");
            aIpc2Jni.run_client(client_path, args);
        } catch (Exception e) {
            e.printStackTrace();
            stopTest(false);
        }
    }

    ;

    /**
     * 停止测试
     *
     * @param isTestInterrupt 是否人工停止
     */
    private void stopTest(boolean isTestInterrupt) {
        if (isRunning) {
            if (isTestInterrupt) {
                aIpc2Jni.send_command(DNSLOOKUP_TEST, DNSLOOKUP_STOP_TEST, "", 0);
            } else {
                sendCallBackStop(TestService.RESULT_SUCCESS);
            }
        }

        aIpc2Jni.uninit_server();
        isRunning = false;
    }

    /**
     * mHandler: 调用回调函数
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            resultCallBack(msg);
        }

        // call back
        @SuppressWarnings("rawtypes")
        private void resultCallBack(Message msg) {
            int N = mCallbacks.beginBroadcast();
            try {
                for (int i = 0; i < N; i++) {
                    switch (msg.what) {
                        case EVENT_CHANGE:
                            mCallbacks.getBroadcastItem(i).OnEventChange(repeatTimes + "-" + msg.obj.toString());
                            break;
                        case CHART_CHANGE:
                            mCallbacks.getBroadcastItem(i).onChartDataChanged((Map) msg.obj);
                            break;
                        case DATA_CHANGE:
                            mCallbacks.getBroadcastItem(i).OnDataChanged((Map) msg.obj);
                            break;
                        case TEST_STOP:
                            if (taskModel != null) {
                                Map<String, String> resultMap = TaskTestObject.getStopResultMap(taskModel);
                                resultMap.put(TaskTestObject.stopResultState, (String) msg.obj);
                                mCallbacks.getBroadcastItem(i).onCallTestStop(resultMap);
                            } else {
                                DNSLookUpTest.this.onDestroy();
                            }
                            break;
                    }
                }
            } catch (RemoteException e) {
                LogUtil.w(TAG, "---", e);
            }
            mCallbacks.finishBroadcast();
        }
    };

    /**
     * DNS测试的开始时间
     */
    private long startTime = 0;
    private Map<String, Integer> totalMap = null;// 用于业务统计存存结构
    @SuppressLint("HandlerLeak")
    private Handler mEventHandler = new Handler() {
        int delay = 0;
        int reason = 1000;

        public void handleMessage(android.os.Message msg) {
            ipc2msg aMsg = (ipc2msg) msg.obj;
            if (aMsg.test_item != DNSLOOKUP_TEST && !isStoped) {
                return;
            }

            switch (aMsg.event_id) {
                case DNSLOOKUP_INITED:
                    LogUtil.w(TAG, "recv DNSLOOKUP_INITED");
                    // strEventList += aMsg.data + "\r\n";
                    String event_data = "local_if::" + "" + "\n" + "qos_inv_ms::\nconnect_timeout_ms::"
                            + (taskModel.getDnsTestConfig().getTimeout() * 1000) + "\n" + "hostname::"
                            + taskModel.getDnsTestConfig().getUrl();
                    LogUtil.w(TAG, event_data);
                    aIpc2Jni.send_command(DNSLOOKUP_TEST, DNSLOOKUP_START_TEST, event_data, event_data.length());
                    break;
                case DNSLOOKUP_RESOLV_START:
                    LogUtil.w(TAG, "recv DNSLOOKUP_RESOLV_START");
                    startTime = aMsg.getRealTime();
                    // 发送显示事件
                    msgStr = WalkStruct.DataTaskEvent.DNS_LOOKUP_START.toString();
                    displayEvent(msgStr);

                    EventBytes.Builder(getApplicationContext(), RcuEventCommand.DNS_LOOKUP_START).addInteger(-9999)
                            .addInteger(RcuEventCommand.TEST_TYPE_DNSLookUp).addStringBuffer(taskModel.getDnsTestConfig().getUrl())
                            .writeToRcu(aMsg.getRealTime());

                    totalMap = new HashMap<String, Integer>();
                    totalMap.put(TotalStruct.TotalDNS._dnsTotalTrys.name(), 1);
                    mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
                    break;
                case DNSLOOKUP_RESOLV_SUCCESS:
                    LogUtil.w(TAG, "recv DNSLOOKUP_RESOLV_SUCCESS");
                    LogUtil.w(TAG, aMsg.data);
                    String ipAddress = "UnKnow";
                    delay = (int) (aMsg.getRealTime() - startTime) / 1000;

                    // LogUtil.w(TAG, String.format("success, start:%s, end:%s, delay:%s",
                    // startTime, System.currentTimeMillis(), delay));
                    if (aMsg.data != null) {
                        msgStr = getValue("host_ip", aMsg.data);
                        ipAddress = !"".equals(msgStr) ? msgStr : "UnKnow";
                    }

                    msgStr = String.format(WalkStruct.DataTaskEvent.DNS_LOOKUP_SUCCESS.toString(), ipAddress, delay);
                    displayEvent(msgStr);

                    // 数据统计
                    totalMap = new HashMap<String, Integer>();
                    totalMap.put(TotalStruct.TotalDNS._dnsSuccs.name(), 1);// 成功次数
                    totalMap.put(TotalStruct.TotalDNS._dnsDelay.name(), delay);// 时延
                    mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));

                    EventBytes.Builder(getApplicationContext(), RcuEventCommand.DNS_LOOKUP_SUCCESS)
                            .addCharArray(taskModel.getDnsTestConfig().getUrl().toCharArray(), 256)
                            .addInteger(UtilsMethod.convertIpString2Int(ipAddress)).addInteger(delay).writeToRcu(aMsg.getRealTime());
                    break;
                case DNSLOOKUP_RESOLV_FAILED:
                    LogUtil.w(TAG, "recv DNSLOOKUP_RESOLV_FAILED");
                    LogUtil.w(TAG, aMsg.data);
                    // LogUtil.w(TAG, String.format("failure, start:%s, end:%s, delay:%s",
                    // startTime, System.currentTimeMillis(),
                    // (System.currentTimeMillis()-startTime)));
                    delay = (int) (aMsg.getRealTime() - startTime) / 1000;
                    // reason(int) 1000:normal 1001:timeout 1040:host not found
                    if (aMsg.data != null) {
                        msgStr = getValue("reason", aMsg.data);
                        reason = !"".equals(msgStr) ? Integer.parseInt(msgStr) : 1000;
                    }

                    msgStr = String.format(WalkStruct.DataTaskEvent.DNS_LOOKUP_FAILURE.toString(), delay,
                            FailReason.getFailReason(reason).getResonStr());
                    displayEvent(msgStr);

                    EventBytes.Builder(getApplicationContext(), RcuEventCommand.DNS_LOOKUP_FAILURE)
                            .addCharArray(taskModel.getDnsTestConfig().getUrl().toCharArray(), 256).addInteger(delay).addInteger(reason)
                            .writeToRcu(aMsg.getRealTime());
                    break;
                case DNSLOOKUP_RESOLV_ERROR:
                    LogUtil.w(TAG, "recv DNSLOOKUP_RESOLV_ERROR");

                    msgStr = String.format(WalkStruct.DataTaskEvent.DNS_LOOKUP_FAILURE.toString(), delay,
                            FailReason.UNKNOWN.getResonStr());
                    displayEvent(msgStr);

                    EventBytes.Builder(getApplicationContext(), RcuEventCommand.DNS_LOOKUP_FAILURE)
                            .addCharArray(taskModel.getDnsTestConfig().getUrl().toCharArray(), 256).addInteger(delay)
                            .addInteger(FailReason.UNKNOWN.getReasonCode()).writeToRcu(aMsg.getRealTime());
                    break;
                case DNSLOOKUP_QUIT:
                    LogUtil.w(TAG, "recv DNSLOOKUP_QUIT");
                    stopTest(false);
                    break;
            }
        }

        private String getValue(String key, String message) {
            String values[] = message.split("\n");
            String result = "";
            for (String v : values) {
                String rst[] = v.split("::");
                if (rst.length == 2 && key.equals(rst[0])) {
                    result = rst[1];
                    break;
                }
            }

            LogUtil.d(TAG, result);
            return result;
        }
    };

    /**
     * 显示事件
     */
    private void displayEvent(String event) {
        Message msg = mHandler.obtainMessage(EVENT_CHANGE, event);
        msg.sendToTarget();
    }

    /**
     * [调用停止当前业务接口]<BR>
     * [如果当前为手工停止状态不能调用当前停止接口]
     *
     * @param msg
     */
    private void sendCallBackStop(String msg) {
        if (!isInterrupted) {
            Message StopMsg = mHandler.obtainMessage(TEST_STOP, msg);
            StopMsg.sendToTarget();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(0);
        mCallbacks.kill();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
