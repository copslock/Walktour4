package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;

import com.dingli.service.test.ipc2msg;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.TotalStruct.TotalAppreciation;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigNBModuleInfo;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.task.parsedata.model.task.ping.TaskPingModel;
import com.walktour.model.TotalSpecialModel;
import com.walktour.service.TestService;

import java.util.HashMap;

import static com.android.SdkConstants.TAG;

@SuppressLint("SdCardPath")
public class PingTestNB extends TestTaskService {
    private static final int PING_TEST = 80;

    private static final int PING_INITED = 1;    //初始化完毕
    private static final int PING_RESOLV_START = 2;    //DNS解析开始(远程主机是域名才有此事件)
    private static final int PING_RESOLV_SUCCESS = 3;    //DNS解析成功(远程主机是域名才有此事件)
    private static final int PING_RESOLV_FAILED = 4;    //DNS解析失败(远程主机是域名才有此事件)
    private static final int PING_ITEM_START = 5;    //PING开始
    private static final int PING_ITEM_SUCCESS = 6;    //PING成功
    private static final int PING_ITEM_FAILED = 7;    //PING失败
    private static final int PING_ERROR = 14;    //业务开始前的参数检测(如invalid ip format(1013))
    private static final int PING_QUIT = 15;    //业务退出

    private static final int PING_START_TEST = 1001; //开始业务(ID不可变)
    private static final int PING_STOP_TEST = 1006;    //停止业务(ID不可变)
    private static long start_ping_times;
    private int lastTimeDelay = 0;        //上一次时间，如果当前是第一次进来，该值为0
    private int pingFailDelay = 0;
    private PingModel model = null;
    /**
     * 速率
     **/
    private int baudrate = 115200;

    @Override
    public void onCreate() {
        super.onCreate();
        tag = "PingTestNB";
        LogUtil.i(tag, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.w(tag, "--Local onDestroy--");
        UtilsMethod.killProcessByPname("com.walktour.service.test.PingTestNB", true);

        LogUtil.w(tag, "--kill local process--");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(tag, "onStart");
        useRoot = Deviceinfo.getInstance().isUseRoot();
        int startFlag = super.onStartCommand(intent, flags, startId);
        lastTimeDelay = intent.getIntExtra(KEY_PING_DELAY, 0);
        if (taskModel instanceof TaskPingModel) {
            String strClientPath = useRoot ? AppFilePathUtil.getInstance().getAppLibDirectory()+"datatests_android_devmodem" : AppFilePathUtil.getInstance().getAppLibDirectory()+"libdatatests_so_devmodem.so";

            dataTestHandler = new PingHandler((TaskPingModel) taskModel, strClientPath);

        }

        //启动已经实例化handler的数据业务
        if (dataTestHandler != null) {
            dataTestHandler.startTest();
        }

        return startFlag;
    }

    @SuppressLint("HandlerLeak")
    private class PingHandler extends DataTestHandler {
        private TaskPingModel pingModel;

        public PingHandler(TaskPingModel pingmodel, String client_path) {

            super("-m ping -z " + AppFilePathUtil.getInstance().getAppConfigDirectory(), client_path, PING_TEST,
                    PING_START_TEST, PING_STOP_TEST);
            this.pingModel = pingmodel;
        }

        public void handleMessage(android.os.Message msg) {
            ipc2msg aMsg = (ipc2msg) msg.obj;
            if (aMsg.test_item != PING_TEST) {
                return;
            }
            hasEventCallBack = true;    //设置业务库有事件回调
            LogUtil.w(tag, "--msgId:" + aMsg.event_id + "--msgStr:" + aMsg.data);

            switch (aMsg.event_id) {
                case PING_INITED:
                    StringBuffer sb=new StringBuffer();
                    sb.append("DevName::" + ConfigNBModuleInfo.getInstance(mContext).exterNBModuleName() +"\n");
                    sb.append("port::" + ConfigNBModuleInfo.getInstance(mContext).exterNBATPort() +"\n");
                    sb.append("Baudrate::" +baudrate + "\n");
//                    if(ConfigNBModuleInfo.getInstance(mContext).isHasNBWifiTestModel()){
                        sb.append("IP::"+ConfigNBModuleInfo.getInstance(mContext).exterNBSelectWifiIP()+"\n");
                        sb.append("IPPort::8888\n");
                        sb.append("SocketType::1\n");
//                    }
                    sendCommand(PING_TEST, 80011, sb.toString());

                    callbackHandler.obtainMessage(CALL_MAINPROCESS, WalkCommonPara
                            .CallMainType_GetNetStat_ByPingStart, 0).sendToTarget();
                    StringBuilder event_data = new StringBuilder();

                    event_data.append("local_if::").append("").append("\n");
                    event_data.append("host_name::").append(pingModel.getIp()).append("\n");
                    event_data.append("packet_size::").append(pingModel.getSize()).append("\n");
                    event_data.append("ttl::").append(pingModel.getTtl()).append("\n");
                    event_data.append("ping_timeout_ms::").append(pingModel.getTimeOut() * 1000);

                    LogUtil.w(tag, "---msgId::" + event_data.toString());
                    sendStartCommand(event_data.toString());
                    break;
                case PING_RESOLV_START:
                    EventBytes.Builder(mContext, RcuEventCommand.DNS_LOOKUP_START).addInteger(RcuEventCommand
                            .DNS_TEST_TYPE_OTHER).addInteger(RcuEventCommand.TEST_TYPE_Ping).addStringBuffer
                            (pingModel.getIp()).writeToRcu(aMsg.getRealTime());

                    break;
                case PING_RESOLV_SUCCESS:
                    analysisResult(PING_RESOLV_SUCCESS, aMsg.data);
                    EventBytes.Builder(mContext, RcuEventCommand.DNS_LOOKUP_SUCCESS).addCharArray(model.host
                            .toCharArray(), 256).addInteger(UtilsMethod.convertIpString2Int(model.host_ip))
                            .addInteger(model.delay).addInteger(UtilsMethod.convertIpString2Int(model.dns_server))
                            .writeToRcu(aMsg.getRealTime());

                    break;
                case PING_RESOLV_FAILED:
                    analysisResult(PING_RESOLV_FAILED, aMsg.data);
                    EventBytes.Builder(mContext, RcuEventCommand.DNS_LOOKUP_FAILURE).addCharArray(pingModel.getIp()
                            .toCharArray(), 256).addInteger(model.delay).addInteger(model.reason).addInteger
                            (UtilsMethod.convertIpString2Int(model.dns_server)).writeToRcu(aMsg.getRealTime());
                    break;
                case PING_ITEM_START:
                    start_ping_times = aMsg.getRealTime();

                    EventBytes.Builder(PingTestNB.this, RcuEventCommand.Ping_Start).addShort((short) 1)
                            .addStringBuffer(pingModel.getIp()).writeToRcu(System.currentTimeMillis() * 1000);

                    break;
                case PING_ITEM_SUCCESS:
                    LogUtil.w(TAG, "PING_ITEM_SUCCESS=" + aMsg.data);
                    analysisResult(PING_ITEM_SUCCESS, aMsg.data);
                    showAndWritePingResult(model, pingModel);
                    break;
                case PING_ITEM_FAILED:
//                    upDownDian();//业务测试失败自动先下电再上电
                    pingFailDelay = (aMsg.getRealTime() > start_ping_times) ? (int) (aMsg.getRealTime() -
                            start_ping_times) / 1000 : 0;

                    analysisResult(PING_ITEM_FAILED, aMsg.data);
                    showAndWritePingResult(model, pingModel);
                    break;
                case PING_ERROR: //as ip=116.6.50 (invalid ip)

                    break;
                case PING_QUIT:
                    //销毁exe
                    sendCommand(PING_TEST, 1006, "");

                    stopProcess(TestService.RESULT_FAILD);
                    break;
            }
        }

        private void upDownDian(){
            sendCommand(PING_TEST, 80013, "");
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            sendCommand(PING_TEST, 80012, "");
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void prepareTest() {
            //
        }

        @Override
        protected void fail(int failReason, long time) {
            //
        }

        @Override
        protected void drop(int dropReason, long time) {
            //
        }

        @Override
        protected void sendCurrentRate() {
            //
        }

        @Override
        protected void lastData(long time) {
            //
        }
    }

    /**
     * PING结果返回对象
     *
     * @author tangwq
     */
    private class PingModel {
        private boolean isSuccess = false;
        private int ttl;
        private int time;
        private String host = "";
        private String host_ip = "";
        private int delay;
        private String dns_server = "";
        private int reason;
        private int is_timeout;
        private int icmp_type;
        private int icmp_code;
    }

    private void analysisResult(int type, String data) {
        model = new PingModel();
        String[] result = data.split("\n");
        int pos = 0;

        try {
            switch (type) {
                case PING_RESOLV_SUCCESS:
                    model.host = getValueByArray(result, pos++, true);
                    model.host_ip = getValueByArray(result, pos++, true);
                    model.delay = Integer.parseInt(getValueByArray(result, pos++, false));
                    model.dns_server = (getValueByArray(result, pos++, true)).split(",")[0];
                    break;
                case PING_RESOLV_FAILED:
                    model.host = getValueByArray(result, pos++, true);
                    model.delay = Integer.parseInt(getValueByArray(result, pos++, false));
                    model.dns_server = (getValueByArray(result, pos++, true)).split(",")[0];
                    model.reason = Integer.parseInt(getValueByArray(result, pos++, false));
                    break;
                case PING_ITEM_SUCCESS:
                    model.isSuccess = true;
                    model.ttl = Integer.parseInt(getValueByArray(result, pos++, false));
                    model.time = (int) Float.parseFloat(getValueByArray(result, pos++, false));
                    model.host_ip = getValueByArray(result, pos++, true);
                    break;
                case PING_ITEM_FAILED:
                    model.isSuccess = false;
                    model.host_ip = getValueByArray(result, pos++, true);
                    model.is_timeout = Integer.parseInt(getValueByArray(result, pos++, false));
                    model.icmp_type = Integer.parseInt(getValueByArray(result, pos++, false));
                    model.icmp_code = Integer.parseInt(getValueByArray(result, pos++, false));
                    break;
            }
        } catch (Exception e) {
            LogUtil.w(tag, "analysisResult", e);
        }
    }

    /**
     * 将PING的结果显示到事件窗口，并将事件写入RCU文件中
     *
     * @param model
     * @param pingModel
     * @author tangwq
     */
    private void showAndWritePingResult(PingModel model, TaskPingModel pingModel) {

        HashMap<String, TotalSpecialModel> totalMap = new HashMap<String, TotalSpecialModel>();
        LogUtil.w(tag, "---currentNetType:" + totalStateModel.getMainKey1() + "---state:" + totalStateModel
                .getMainKey2() + "--delay:" + model.time + "--lastdelayq:" + lastTimeDelay);
        TotalSpecialModel tmpSp = new TotalSpecialModel(totalStateModel.getMainKey1(), totalStateModel.getMainKey2(),
                TotalAppreciation._pingTry.name(), 1);
        totalMap.put(tmpSp.getKeyName(), tmpSp);
        if (model.isSuccess) {
            tmpSp = new TotalSpecialModel(totalStateModel.getMainKey1(), totalStateModel.getMainKey2(),
                    TotalAppreciation._pingSuccess.name(), 1);
            totalMap.put(tmpSp.getKeyName(), tmpSp);
            tmpSp = new TotalSpecialModel(totalStateModel.getMainKey1(), totalStateModel.getMainKey2(),
                    TotalAppreciation._pingDelay.name(), model.time);
            totalMap.put(tmpSp.getKeyName(), tmpSp);

            EventBytes.Builder(PingTestNB.this, RcuEventCommand.Ping_Success).addInteger(UtilsMethod
                    .convertIpString2Int(model.host_ip)).addInteger(pingModel.getSize()).addInteger(0).addInteger
                    (model.ttl).addInteger(model.time).addInteger(0).addInteger(0).addInteger(lastTimeDelay == 0 ? 0
                    : Math.abs(model.time - lastTimeDelay)).writeToRcu(System.currentTimeMillis() * 1000);

        } else {
            tmpSp = new TotalSpecialModel(totalStateModel.getMainKey1(), totalStateModel.getMainKey2(),
                    TotalAppreciation._pingSuccess.name(), 0);
            totalMap.put(tmpSp.getKeyName(), tmpSp);

            EventBytes.Builder(PingTestNB.this, RcuEventCommand.Ping_Failure).addInteger(UtilsMethod
                    .convertIpString2Int(model.host_ip)).addInteger(pingModel.getSize()).addInteger(model.is_timeout)
                    .addInteger(model.ttl).addInteger(pingFailDelay).addInteger(0).addInteger(0).addInteger(60)
                    .addInteger(model.icmp_type).addInteger(model.icmp_code).writeToRcu(System.currentTimeMillis() *
                    1000);
        }

        callbackHandler.obtainMessage(CHART_CHANGE, totalMap).sendToTarget();
    }
}
