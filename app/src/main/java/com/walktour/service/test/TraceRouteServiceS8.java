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
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct.DataTaskEvent;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhone;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.task.parsedata.TaskTestObject;
import com.walktour.gui.task.parsedata.model.task.traceroute.TaskTraceRouteModel;
import com.walktour.service.ICallback;
import com.walktour.service.IService;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("SdCardPath")
public class TraceRouteServiceS8 extends TestTaskService {
    private static final String TAG = "TraceRouteServiceS8";

    private static final int TRACERT_TEST = 100;
    private static final int TRACERT_INITED = 4001;//初始化完毕
    private static final int TRACERT_RESOLV_START = 4002;//DNS解析开始(远程主机是域名才有此事件)
    private static final int TRACERT_RESOLV_END = 4003;//DNS解析完成(远程主机是域名才有此事件)
    private static final int TRACERT_START = 4004;//路由开始
    private static final int TRACERT_SUCCESS = 4005;//路由成功
    private static final int TRACERT_FAILURE = 4006;//路由失败
    private static final int TRACERT_ERROR = 4014;//异常错误
    private static final int TRACERT_QUIT = 4015;//路由退出
    private static final int TRACERT_START_TEST = 1001;//开始业务



    private final int TIMER_CHANG = 55;
    private TaskTraceRouteModel taskModel;
    private ipc2jni aIpc2Jni;
    private boolean isCallbackRegister = false;
    private boolean isFinish = false;

    private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();

    private IService.Stub mBind = new IService.Stub() {
        @Override
        public void unregisterCallback(ICallback cb) throws RemoteException {
            if (cb != null) {
                mCallbacks.unregister(cb);
            }
        }

        @Override
        public void stopTask(boolean isTestInterrupt, int dropResion) throws RemoteException {
            if ((isTestInterrupt || (dropResion != RcuEventCommand.DropReason.NORMAL.getReasonCode())) && !isFinish) {
                isFinish = true;
                displayEvent(DataTaskEvent.Route_Fail.toString());
                writeRcuEvent(RcuEventCommand.Route_Fail, System.currentTimeMillis() * 1000, isTestInterrupt ?
                        RcuEventCommand.FailReason.USER_STOP.getReasonCode() : dropResion);
            }
        }

        @Override
        public void registerCallback(ICallback cb) throws RemoteException {
            if (cb != null) {
                mCallbacks.register(cb);
                isCallbackRegister = true;
            }
        }

        /**
         * 返回是否执行startCommand状态,
         * 如果改状态需要在业务中出现某种情况时才为真,
         * 可以在继承的业务中改写该状态*/
        public boolean getRunState() {
            return startCommondRun;
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return mBind;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(tag, "---onStart");
        useRoot = Deviceinfo.getInstance().isUseRoot();
        int startFlag = super.onStartCommand(intent, flags, startId);

        taskModel = (TaskTraceRouteModel) super.taskModel;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!isCallbackRegister) {
                        Thread.sleep(100);
                    }
                } catch (Exception e) {
                }
                startTest();
            }
        });
        thread.start();
        return startFlag;
    }

    public void onDestroy() {
        LogUtil.v(TAG, "--onDestroy--");
        mHandler.removeMessages(0);
        mCallbacks.kill();


        //因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
        UtilsMethod.killProcessByPname("com.walktour.service.test.TraceRouteServiceS8", false);
        UtilsMethod.killProcessByPname("/data/data/com.dingli.service.test/lib/datatests_android", false);
        super.onDestroy();
    }

    ;

    private long time = 0;
    private int dealy = 0;
    TraceRouteInfo routeInfo = new TraceRouteInfo();
    private Map<String, Integer> totalMap;
    @SuppressLint("HandlerLeak")
    private Handler mEventHandler = new Handler() {
        public void handleMessage(Message msg) {
            ipc2msg aMsg = (ipc2msg) msg.obj;
            if (aMsg.test_item != TRACERT_TEST || isFinish) {
                return;
            }
            LogUtil.w(TAG,"aMsg.event_id="+aMsg.event_id);
            switch (aMsg.event_id) {
                case TRACERT_INITED:
                    LogUtil.w(TAG, "recv TRACERT_INITED");
                    LogUtil.w(TAG, aMsg.data + "-->");
                    String event_data = "local_if::" + getNetInterface() + "\n"
                            + "host_name::" + taskModel.getHost() + "\n"
                            + "packet_size::" + taskModel.getIpPacket() + "\n"
                            + "per_timeout_ms::" + taskModel.getHopTimeout() + "\n"
                            + "per_interval_ms::" + taskModel.getHopInterval() + "\n"
                            + "nprobes::" + taskModel.getHopProbeNum();
                    LogUtil.w(TAG, event_data);
                    aIpc2Jni.send_command(TRACERT_TEST, TRACERT_START_TEST, event_data, event_data.length());
                    break;
                case TRACERT_RESOLV_START:
                    LogUtil.w(TAG, "recv TRACERT_RESOLV_START");
                    LogUtil.w(TAG, "msg data=" + aMsg.data);
                    break;
                case TRACERT_RESOLV_END:
                    LogUtil.w(TAG, "recv TRACERT_RESOLV_END");
                    LogUtil.w(TAG, "msg data=" + aMsg.data);
                    break;
                case TRACERT_START:
                    LogUtil.w(TAG, "recv TRACERT_START");
                    LogUtil.w(TAG, aMsg.data + "-->");
                    analyseMsg(aMsg.data);

                    displayEvent(DataTaskEvent.Route_Start.toString());
                    //writeRcuEvent(RcuEventCommand.Route_Start, RcuEventCommand.TEST_TYPE_TraceRoute);
                    LogUtil.w(TAG, routeInfo.srcIp + "-->srcIp");
                    LogUtil.w(TAG, routeInfo.destIp + "-->destIp");
                    EventBytes.Builder(getApplicationContext(), RcuEventCommand.Route_Start)
                            .addCharArray(routeInfo.srcIp == null ? "".toCharArray() : routeInfo.srcIp.toCharArray(), 24)
                            .addCharArray(routeInfo.destIp == null ? "".toCharArray() : routeInfo.destIp.toCharArray(), 24)
                            .addStringBuffer(taskModel.getHost())
                            .writeToRcu(aMsg.getRealTime());

                    time = aMsg.getRealTime();
                    totalMap = new HashMap<String, Integer>();
                    totalMap.put(TotalStruct.TotalTraceRoute._traceRouteTrys.name(), 1);
                    mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));
                    LogUtil.w(TAG,"TRACERT_START finish.");
                    break;
                case TRACERT_FAILURE:
                    LogUtil.w(TAG, "recv TRACERT_FAILURE");
                    LogUtil.w(TAG, aMsg.data + "-->");
                    analyseMsg(aMsg.data);
                    displayEvent(DataTaskEvent.Route_Fail.toString());
                    writeRcuEvent(RcuEventCommand.Route_Fail, aMsg.getRealTime(), routeInfo.reason);
                    break;
                case TRACERT_ERROR:
                    LogUtil.w(TAG, "recv TRACERT_ERROR");
                    LogUtil.w(TAG, aMsg.data + "-->");
                    writeRcuEvent(RcuEventCommand.Route_ReslvError, aMsg.getRealTime());
                    break;
                case TRACERT_SUCCESS:
                    LogUtil.w(TAG, "recv TRACERT_SUCCESS");
                    LogUtil.w(TAG, aMsg.data + "-->");
                    analyseMsg(aMsg.data);
                    displayEvent(String.format(DataTaskEvent.Route_Success.toString(),
                            routeInfo.point_all_count, routeInfo.point_all_delay,
                            routeInfo.point_timeout_count, routeInfo.point_unknown_count));

                    EventBytes.Builder(getApplicationContext(), RcuEventCommand.Route_Success)
                            .addInteger(routeInfo.point_all_count)
                            .addInteger((int) routeInfo.point_all_delay)
                            .addInteger(routeInfo.point_timeout_count)
                            .addInteger((int) routeInfo.point_timeout_delay)
                            .addInteger(routeInfo.point_unknown_count)
                            .addInteger((int) routeInfo.point_unknown_delay).writeToRcu(aMsg.getRealTime());

                    totalMap = new HashMap<String, Integer>();
                    totalMap.put(TotalStruct.TotalTraceRoute._traceRouteSucc.name(), 1);
                    totalMap.put(TotalStruct.TotalTraceRoute._traceRouteDelay.name(), (int) routeInfo.point_all_delay);
                    mHandler.sendMessage(mHandler.obtainMessage(CHART_CHANGE, totalMap));

                    break;
                case TRACERT_QUIT:
                    LogUtil.w(TAG, "recv TRACERT_QUIT");
                    LogUtil.w(TAG, aMsg.data + "-->");
                    sendCallBackStop("1");
                    aIpc2Jni.uninit_server();
                    break;

            }
            totalMap = null;
        }
    };

    private void sendCallBackStop(String msg) {
        isFinish = true;
        Message StopMsg = mHandler.obtainMessage(TEST_STOP, msg);
        StopMsg.sendToTarget();
    }

    private class TraceRouteInfo {
        public String apnName;
        public String destIp;
        public String srcIp;
        public String host;
        public String ip;
        public int reason;
        public int id;
        String point_ip;
        int point_all_count;        //总节点数
        double point_all_delay;        //总时延, 单位ms
        int point_timeout_count;    //超时节点数
        double point_timeout_delay; //超时时延, 单位ms
        int point_unknown_count;    //未知节点数
        double point_unknown_delay; //未知时延, 单位ms
    }

    /**
     * 分析数据
     *
     * @param msg
     */
    private void analyseMsg(String msg) {
        if (msg != null && !"".equals(msg)) {
            String[] key_value = msg.split("\n");
            for (String v : key_value) {
                String[] res = v.split("::");
                if (res.length < 2) continue;

                if (res[0].equals("reason")) routeInfo.reason = Integer.parseInt(res[1]);
                if (res[0].equals("host")) routeInfo.host = res[1];
                if (res[0].equals("ip")) routeInfo.ip = res[1];

                if (res[0].equals("id")) routeInfo.id = Integer.parseInt(res[1]);
                if (res[0].equals("point_ip")) routeInfo.point_ip = res[1];


                if (res[0].equals("point_unknown_delay"))
                    routeInfo.point_unknown_delay = Double.parseDouble(res[1]);

                if (res[0].equals("src_ip")) //当前源IP
                    routeInfo.srcIp = res[1];
                if (res[0].equals("dest_ip")) //当前目标IP
                    routeInfo.destIp = res[1];
                if (res[0].equals("icmp_all_delay"))//往返总时延
                    routeInfo.point_all_delay = Double.parseDouble(res[1]);
                if (res[0].equals("icmp_all_packet_count"))//ICMP包总个数
                    routeInfo.point_timeout_count = Integer.parseInt(res[1]);
                if (res[0].equals("icmp_avg_delay"))//往返平均时延
                    routeInfo.point_timeout_delay = Double.parseDouble(res[1]);
                if (res[0].equals("point_all_count"))//节点总数
                    routeInfo.point_all_count = Integer.parseInt(res[1]);
                if (res[0].equals("point_unknown_count"))//未知节点数
                    routeInfo.point_unknown_count = Integer.parseInt(res[1]);
            }
            routeInfo.apnName= MyPhone.getApnName(this);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            resultCallBack(msg);
        }

        //call back
        @SuppressWarnings({"rawtypes", "unchecked"})
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
                            Map tempMap = (Map) msg.obj;
                            tempMap.put(TaskTestObject.stopResultName, taskModel.getTaskName());
                            mCallbacks.getBroadcastItem(i).OnDataChanged(tempMap);
                            break;
                        case TEST_STOP:
                            Map<String, String> resultMap = TaskTestObject.getStopResultMap(taskModel);
                            resultMap.put(TaskTestObject.stopResultState, (String) msg.obj);
                            mCallbacks.getBroadcastItem(i).onCallTestStop(resultMap);
                            break;
                        case REAL_PARA:
                            mCallbacks.getBroadcastItem(i).onParaChanged(WalkCommonPara.CALL_BACK_VIDEO_PLAY_REAL_PARA, (Map) msg.obj);
                            break;
                        case TIMER_CHANG:
                            timerChange();
                            break;
                    }
                }
            } catch (RemoteException e) {
                LogUtil.w(TAG, "---", e);
            }
            mCallbacks.finishBroadcast();
        }
    };

    private void timerChange() {
        mHandler.sendEmptyMessageDelayed(TIMER_CHANG, 1000);
    }

    protected void startTest() {
        if (aIpc2Jni == null) {
            aIpc2Jni = new ipc2jni(mEventHandler);
        }

        aIpc2Jni.initServer(this.getLibLogPath());
        String args = "-m cmd_services -c " + TRACERT_TEST + " -z " + AppFilePathUtil.getInstance().getAppConfigDirectory();
        if(Deviceinfo.getInstance().isS7()){
            args+=" -p "+ AppFilePathUtil.getInstance().getAppConfigDirectory();
        }
        String client_path = AppFilePathUtil.getInstance().getAppLibFile(useRoot ? "datatests_android" : "libdatatests_so.so").getAbsolutePath();
        if (useRoot) {
            aIpc2Jni.set_su_file_path(Deviceinfo.getInstance().getSuOrShCommand() + " -c", "export LD_LIBRARY_PATH=" + AppFilePathUtil.getInstance().getAppLibDirectory());
        }
        LogUtil.w(TAG, client_path);
        LogUtil.w(TAG, args);
        aIpc2Jni.run_client(client_path, args);
        LogUtil.w(TAG, "TraceRoute Runed");
    }

    private void displayEvent(String event) {
        Message msg = mHandler.obtainMessage(EVENT_CHANGE, event);
        msg.sendToTarget();
    }
}
