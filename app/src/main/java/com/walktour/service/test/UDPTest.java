package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;

import com.dingli.service.test.ipc2msg;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.SDCardUtil;
import com.walktour.control.config.ConfigNBModuleInfo;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.task.udp.TaskUDPModel;
import com.walktour.gui.total.TotalUDPView;
import com.walktour.service.TestService;

import java.util.HashMap;
import java.util.Map;

/**
 * UDP业务测试
 */
@SuppressLint("SdCardPath")
public class UDPTest extends TestTaskService {
    private static final String TAG = "UDPTest";
    private static final int DLUDP_TEST = 201;
    /**
     * 业务测试开始
     */
    private static final int DLUDP_START_TEST = 1001;
    /**
     * 业务测试结束
     */
    private static final int DLUDP_STOP_TEST = 1006;

    private static final int DLUDP_INITED = 1;    //初始化完毕
    private static final int DLUDP_QOS = 5;
    private static final int DLUDP_CONNECT_START = 6;
    private static final int DLUDP_CONNECT_SUCCESS = 7;
    private static final int DLUDP_CONNECT_FAIL = 8;
    private static final int DLUDP_START = 10;    //业务正式开始测试(收到START_TEST命令后报出) (RCU:)
    private static final int DLUDP_END = 11;
    private static final int DLUDP_FINISH = 21; //业务异常结束 (RCU:)
    private static final int DLUDP_DISCONNECT = 23;
    private static final int DLUDP_QUIT = 25;   //程序退出

    private boolean isFinish = false;
    /**
     * 速率
     **/
    private int baudrate = 115200;

    /**
     * 是否开始业务测试
     */
    boolean isStart = false;
    /**
     * 时间总个数,判断成功与否，
     * udp connect start--》udp connect success-》udp start--》udp end--》udp finish都有就算成功，其他就是失败
     */
    private final int EVENTTOTAL = 5;
    /**
     * 统计速率的次数
     */
    int count = 0;
    int countrlc = 0;
    int countmac = 0;
    int countphy = 0;
    int countpdcp = 0;
    float udpUPValues = 0;


    float udpValue_rlc_up = 0;
    float udpValue_rlc_down = 0;
    float udpValue_mac_up = 0;
    float udpValue_mac_down = 0;
    float udpValue_phy_up = 0;
    float udpValue_phy_down = 0;
    float udpValue_pdcp_up = 0;
    float udpValue_pdcp_down = 0;

    int countEvent = 0;
    boolean isUpload = true;

    private String SDCARDPATH = "";

    private Intent intent = null;

    @Override
    public void onCreate()
    {
        super.onCreate();
        regedit();
        intent = new Intent(TotalUDPView.ACTION);
        tag = UDPTest.class.getSimpleName();
        SDCARDPATH = SDCardUtil.getSDCardPath();
        LogUtil.i(tag, "onCreate");
    }

    private void initVlaues()
    {
        isStart = false;
        count = 0;
        countrlc = 0;
        countmac = 0;
        countphy = 0;
        countpdcp = 0;
        udpValue_rlc_up = 0;
        udpValue_rlc_down = 0;
        udpValue_mac_up = 0;
        udpValue_mac_down = 0;
        udpValue_phy_up = 0;
        udpValue_phy_down = 0;
        udpValue_pdcp_up = 0;
        udpValue_pdcp_down = 0;
        udpUPValues = 0;
        countEvent = 0;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        LogUtil.w(tag, "--Local onDestroy--");
        this.unregisterReceiver(mEventReceiver);
        LogUtil.w(tag, "--kill local process--");
        if (!isFinish && null != dataTestHandler) {
            //结束datatests_android_devmodem
            dataTestHandler.sendCommand(DLUDP_TEST, DLUDP_STOP_TEST, "");
            dataTestHandler = null;
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        UtilsMethod.killProcessByPname("com.walktour.service.test.UDPTest", true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        LogUtil.i(tag, "onStart");
        initVlaues();
        useRoot = Deviceinfo.getInstance().isUseRoot();
        int startFlag = super.onStartCommand(intent, flags, startId);
        if (taskModel instanceof TaskUDPModel) {
            isUpload = ((TaskUDPModel) taskModel).getTestMode().equals("0");
            String strClientPath = useRoot ? AppFilePathUtil.getInstance().getAppLibDirectory() +
                    "datatests_android_devmodem" : AppFilePathUtil.getInstance()
                    .getAppLibDirectory() + "libdatatests_so_devmodem.so";

            dataTestHandler = new UDPHandler((TaskUDPModel) taskModel, strClientPath);
        }
        //启动已经实例化handler的数据业务
        if (dataTestHandler != null) {
            dataTestHandler.startTest();
        }

        return startFlag;
    }

    @SuppressLint("HandlerLeak")
    private class UDPHandler extends DataTestHandler {
        private TaskUDPModel udpModel;

        private Map<String, Integer> totalMap;//用于业务统计存存结构

        public UDPHandler(TaskUDPModel udpModel, String client_path)
        {

            super("-m dludp -z " + AppFilePathUtil.getInstance().getAppConfigDirectory(),
                    client_path, DLUDP_TEST, DLUDP_START_TEST,
                    DLUDP_STOP_TEST);
            this.udpModel = udpModel;
        }

        public void handleMessage(android.os.Message msg)
        {
            ipc2msg aMsg = (ipc2msg) msg.obj;
            if (aMsg.test_item != DLUDP_TEST) {
                return;
            }
            totalMap = new HashMap<String, Integer>();
            hasEventCallBack = true;    //设置业务库有事件回调
            LogUtil.w(tag, "--msgId:" + aMsg.event_id + "--msgStr:" + aMsg);

            switch (aMsg.event_id) {
                case DLUDP_INITED:
                    initVlaues();
                    LogUtil.i(tag, "recv DLUDP_INITED\r\n" + "--msgStr:" + aMsg.data);
                    String dev_params = "DevName::" + ConfigNBModuleInfo.getInstance(mContext)
                            .exterNBModuleName() +
                            "\nport::" + ConfigNBModuleInfo.getInstance(mContext).exterNBATPort() +
                            "\nBaudrate::" +
                            baudrate + "\n" +
                            "IP::" + ConfigNBModuleInfo.getInstance(mContext).exterNBSelectWifiIP() + "\n" +
                            "IPPort::8888\n" +
                            "SocketType::1\n";

                    LogUtil.i(tag, "dev_params=" + dev_params);
                    sendCommand(DLUDP_TEST, 80011, dev_params);

                    StringBuilder event_data = new StringBuilder();


                    event_data.append("SequeceNumber::" + repeatTimes + "\n");
                    event_data.append("local_if::\n");
                    event_data.append("duration_ms::" + (Integer.parseInt(udpModel
                            .getTestDuration()) + Integer.parseInt(udpModel.getSendPacketDuration
                            ())) * 1000 + "\n");
                    event_data.append("nodata_timeout_ms::" + Integer.parseInt(udpModel
                            .getNoDataTimeout()) * 1000 + "\n");
                    event_data.append("server_ip::" + udpModel.getServerIP() + "\n");
                    event_data.append("server_port::" + udpModel.getServerPort() + "\n");
                    event_data.append("up_packet_size::" + udpModel.getPacketSize() + "\n");
                    event_data.append("down_packet_size::" + udpModel.getPacketSize() + "\n");
                    event_data.append("local_port::\n");
                    event_data.append("test_mode::" + udpModel.getTestMode() + "\n");
                    event_data.append("send_packet_duration_ms::" + (Integer.parseInt(udpModel
                            .getSendPacketDuration())) * 1000 + "\n");

                    //发包间隔时长计算方法:
                    //udpModel.getSendPacketInterval() 存储的是带宽(K)
                    //(1000ms *发包大小bit)除以（ 带宽K*1000)= 发包间隔ms
                    //即:发包大小bit除以带宽= 发包间隔ms

                    int inter = (int) (Integer.parseInt(udpModel.getPacketSize()) * 8 / Integer
                            .parseInt(udpModel.getSendPacketInterval()));
                    event_data.append("send_packet_interval_ms::" + inter + "\n");

                    event_data.append("is_down_check_spnumber::0\n");//是否检查，默认为0，在外部设置，目前没处理
                    LogUtil.w(tag, "---msgId::" + aMsg.event_id + "\n" + event_data.toString());
                    sendStartCommand(event_data.toString());
                    break;

                case DLUDP_QOS:
                    LogUtil.i(tag, "recv DLUDP_QOS\r\n" + "--msgStr:" + aMsg.data);
                    break;
                case DLUDP_CONNECT_START:
                    LogUtil.i(tag, "recv DLUDP_CONNECT_START\r\n" + "--msgStr:" + aMsg.data);
                    countEvent += 1;
                    writeRcuUDPConnectStart(aMsg);

                    break;
                case DLUDP_CONNECT_SUCCESS:
                    LogUtil.i(tag, "recv DLUDP_CONNECT_SUCCESS\r\n" + "--msgStr:" + aMsg.data);
                    countEvent += 1;
                    writeRcuUDPConnectSuccess(aMsg);
                    break;
                case DLUDP_CONNECT_FAIL:
                    LogUtil.i(tag, "recv DLUDP_CONNECT_FAIL\r\n" + "--msgStr:" + aMsg.data);
                    writeRcuUDPConnectFailure(aMsg);
                    break;
                case DLUDP_START:
                    LogUtil.i(tag, "recv DLUDP_START\r\n" + "--msgStr:" + aMsg.data);
                    countEvent += 1;
                    isStart = true;
                    writeRcuUDPStart(aMsg);
                    break;
                case DLUDP_END:
                    //收到END信令后，表示UDP业务测试成功
                    LogUtil.i(tag, "recv DLUDP_END\r\n" + "--msgStr:" + aMsg.data);
                    countEvent += 1;
                    writeRcuUDPEnd(aMsg);
                    break;
                case DLUDP_FINISH:
                    LogUtil.i(tag, "recv DLUDP_FINISH\r\n" + "--msgStr:" + aMsg.data);
                    countEvent += 1;
                    writeRcuUDPFinish(aMsg);
                    break;
                case DLUDP_DISCONNECT:
                    LogUtil.i(tag, "recv DLUDP_DISCONNECT\r\n" + "--msgStr:" + aMsg.data);
                    writeRcuUDPDisconnect(aMsg);
                    break;
                case DLUDP_QUIT:
                    LogUtil.i(tag, "recv DLUDP_QUIT\r\n" + "--msgStr:" + aMsg.data);
                    isFinish = true;
                    sendCommand(DLUDP_TEST, DLUDP_STOP_TEST, "");
                    stopProcess(TestService.RESULT_FAILD);
                    break;
            }
        }

        @Override
        protected void prepareTest()
        {
            //
        }

        @Override
        protected void fail(int failReason, long time)
        {
            //
        }

        @Override
        protected void drop(int dropReason, long time)
        {
            //
        }

        @Override
        protected void sendCurrentRate()
        {
            //
        }

        @Override
        protected void lastData(long time)
        {
            //
        }

        /**
         * 统计
         *
         * @param totalType 统计项
         * @param value     值
         */
        private void totalResult(DataTestHandler handler, TotalStruct.TotalUDP totalType, long
                value)

        {
            HashMap<String, Long> map = new HashMap<String, Long>();
            map.put(totalType.name(), value);
            handler.totalResult(map);
        }

        /**
         * 根据返回的 key::value来获得值
         *
         * @param str      字符串
         * @param isString 返回值是否字符串
         * @return
         */
        private String getValue(String str, boolean isString)
        {
            String[] ls = str.split("::");
            if (ls.length == 1) {
                if (isString) {
                    return "";
                }
                return "0";
            }
            return ls[1];
        }

        /**
         * UDP连接开始
         *
         * @param aMsg
         */
        private void writeRcuUDPConnectStart(ipc2msg aMsg)
        {
            if (udpModel.getTestMode().equals("0")) {
                totalResult(this, TotalStruct.TotalUDP._upTryTimes, 1);
            } else if (udpModel.getTestMode().equals("1")) {
                totalResult(this, TotalStruct.TotalUDP._downTryTimes, 1);
            }

            LogUtil.w(tag, "writeRcuUDPConnectStart-1");
            EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.UDP_CONNECT_Start);
            eb.addInteger(Integer.parseInt(udpModel.getTestMode()));
            eb.addStringBuffer(udpModel.getServerIP());
            eb.addInteger(Integer.parseInt(udpModel.getServerPort()));
            eb.addInteger(Integer.parseInt(udpModel.getPacketSize()));
            eb.addInteger(Integer.parseInt(udpModel.getPacketSize()));
            int inter = (int) (Integer.parseInt(udpModel.getPacketSize()) * 8 / Integer.parseInt
                    (udpModel.getSendPacketInterval()));
            eb.addInteger(inter);
            eb.writeToRcu(aMsg.getRealTime());
            LogUtil.w(tag, "writeRcuUDPConnectStart-2");


        }

        /**
         * UDP连接成功
         *
         * @param aMsg
         */
        private void writeRcuUDPConnectSuccess(ipc2msg aMsg)
        {
            LogUtil.w(tag, "writeRcuUDPConnectSuccess-1");
            EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.UDP_CONNECT_Success);
            eb.writeToRcu(aMsg.getRealTime());
            LogUtil.w(tag, "writeRcuUDPConnectSuccess-2");
        }

        /**
         * UDP连接失败
         *
         * @param aMsg
         */
        private void writeRcuUDPConnectFailure(ipc2msg aMsg)
        {
            LogUtil.w(tag, "writeRcuUDPConnectFailure-1");
            String[] values = aMsg.data.split("\n");
            if (null == values || values.length < 2) {
                LogUtil.w(tag, "writeRcuUDPConnectFailure values.length<2");
                EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.UDP_CONNECT_Failure);
                eb.writeToRcu(aMsg.getRealTime());
                return;
            }
            int pos = 0;
            EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.UDP_CONNECT_Failure);
            eb.addInteger(Integer.parseInt(this.getValue(values[pos++], false)));
            eb.addStringBuffer(this.getValue(values[pos++], true));
            eb.writeToRcu(aMsg.getRealTime());
            LogUtil.w(tag, "writeRcuUDPConnectFailure-2");
        }

        /**
         * UDP开始
         *
         * @param aMsg
         */
        private void writeRcuUDPStart(ipc2msg aMsg)
        {
            LogUtil.w(tag, "writeRcuUDPStart-1");
            EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.UDP_Start);
            eb.writeToRcu(aMsg.getRealTime());
            LogUtil.w(tag, "writeRcuUDPStart-2");
        }

        /**
         * UDP结束
         *
         * @param aMsg
         */
        private void writeRcuUDPEnd(ipc2msg aMsg)
        {
            LogUtil.w(tag, "writeRcuUDPEnd-1");
            EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.UDP_End);
            eb.writeToRcu(aMsg.getRealTime());
            LogUtil.w(tag, "writeRcuUDPEnd-2");
        }

        /**
         * UDP成功
         *
         * @param aMsg
         */
        private void writeRcuUDPFinish(ipc2msg aMsg)
        {

            String[] values = aMsg.data.split("\n");
            LogUtil.w(tag, "writeRcuUDPFinish-1");
            if (null == values || values.length < 10) {
                LogUtil.w(tag, "writeRcuUDPFinish values.length<10");
                EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.UDP_Finish);
                eb.writeToRcu(aMsg.getRealTime());

                return;
            }

            int pos = 0;
            EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.UDP_Finish);
            eb.addInteger(Integer.parseInt(this.getValue(values[pos++], false)));
            eb.addStringBuffer(this.getValue(values[pos++], true));
            eb.addInteger(Integer.parseInt(this.getValue(values[pos++], false)));
            eb.addInteger(Integer.parseInt(this.getValue(values[pos++], false)));
            eb.addInteger(Integer.parseInt(this.getValue(values[pos++], false)));
            eb.addInteger(Integer.parseInt(this.getValue(values[pos++], false)));
            eb.addInteger(Integer.parseInt(this.getValue(values[pos++], false)));
            eb.addInteger(Integer.parseInt(this.getValue(values[pos++], false)));
            eb.addInteger(Integer.parseInt(this.getValue(values[pos++], false)));
            eb.addInteger(Integer.parseInt(this.getValue(values[pos++], false)));
            eb.writeToRcu(aMsg.getRealTime());
            LogUtil.w(tag, "writeRcuUDPFinish-2");
            if (udpModel.getTestMode().equals("0")) {//上行
                if (countEvent == EVENTTOTAL) {//成功
                    totalResult(this, TotalStruct.TotalUDP._upFailTimes, 0);
                    totalResult(this, TotalStruct.TotalUDP._upSuccessTimes, 1);
                    totalResult(this, TotalStruct.TotalUDP._upAverageSpeed, Integer.parseInt(this
                            .getValue(values[8], false)));

                    //累积值
                    HashMap<String, Long> hMap = TotalDataByGSM.getInstance().getUnifyTimes();
                    long valxy1 = getValue2(udpValue_rlc_up, countrlc);
                    totalResult(this, TotalStruct.TotalUDP._upAverageSpeedRLCTotal, valxy1);

                    long valxy2 = getValue2(udpValue_mac_up, countmac);
                    totalResult(this, TotalStruct.TotalUDP._upAverageSpeedMACTotal, valxy2);

                    long valxy3 = getValue2(udpValue_phy_up, countphy);
                    totalResult(this, TotalStruct.TotalUDP._upAverageSpeedPHYTotal, valxy3);

                    long valxy4 = getValue2(udpValue_pdcp_up, countpdcp);
                    totalResult(this, TotalStruct.TotalUDP._upAverageSpeedPDCPTotal, valxy4);

                    sendUDPBroadCast(0, valxy1, valxy2, valxy3, valxy4);
                } else {//失败
                    totalResult(this, TotalStruct.TotalUDP._upFailTimes, 1);
                    totalResult(this, TotalStruct.TotalUDP._upSuccessTimes, 0);
                    Float f = udpUPValues / (float) count * 1000;
                    totalResult(this, TotalStruct.TotalUDP._upAverageSpeed, 0);

                    //累积值
                    totalResult(this, TotalStruct.TotalUDP._upAverageSpeedRLCTotal, 0);

                    totalResult(this, TotalStruct.TotalUDP._upAverageSpeedMACTotal, 0);

                    totalResult(this, TotalStruct.TotalUDP._upAverageSpeedPHYTotal, 0);

                    totalResult(this, TotalStruct.TotalUDP._upAverageSpeedPDCPTotal, 0);

                    sendUDPBroadCast(0, 0, 0, 0, 0);
                }

            } else if (udpModel.getTestMode().equals("1")) {//下行
                if (countEvent == EVENTTOTAL) {//成功
                    totalResult(this, TotalStruct.TotalUDP._downSuccessTimes, 1);
                    totalResult(this, TotalStruct.TotalUDP._downFailTimes, 0);
                    totalResult(this, TotalStruct.TotalUDP._downAverageSpeed, Integer.parseInt
                            (this.getValue(values[9], false)));


                    long valxy1 = getValue2(udpValue_rlc_down, countrlc);
                    totalResult(this, TotalStruct.TotalUDP._downAverageSpeedRLCTotal, valxy1);

                    long valxy2 = getValue2(udpValue_mac_down, countmac);
                    totalResult(this, TotalStruct.TotalUDP._downAverageSpeedMACTotal, valxy2);

                    long valxy3 = getValue2(udpValue_phy_down, countphy);
                    totalResult(this, TotalStruct.TotalUDP._downAverageSpeedPHYTotal, valxy3);

                    long valxy4 = getValue2(udpValue_pdcp_down, countpdcp);
                    totalResult(this, TotalStruct.TotalUDP._downAverageSpeedPDCPTotal, valxy4);
                    sendUDPBroadCast(1, valxy1, valxy2, valxy3, valxy4);
                } else {//失败
                    Float f = udpUPValues / (float) count * 1000;
                    totalResult(this, TotalStruct.TotalUDP._downSuccessTimes, 0);
                    totalResult(this, TotalStruct.TotalUDP._downFailTimes, 1);
                    totalResult(this, TotalStruct.TotalUDP._downAverageSpeed, 0);

                    totalResult(this, TotalStruct.TotalUDP._downAverageSpeedRLCTotal, 0);

                    totalResult(this, TotalStruct.TotalUDP._downAverageSpeedMACTotal, 0);

                    totalResult(this, TotalStruct.TotalUDP._downAverageSpeedPHYTotal, 0);

                    totalResult(this, TotalStruct.TotalUDP._downAverageSpeedPDCPTotal, 0);

                    sendUDPBroadCast(1, 0, 0, 0, 0);
                }

            }
        }

        private long getValue2(float value, int count)
        {

            if (count == 0) {
                LogUtil.w(TAG, "getValue2 count=" + count);
                return 0;
            }
            Float f = value / (float) count;
//            LogUtil.w(TAG,"getValue2 value="+value+",count="+count+",f.longValue="+f.longValue());
            return f.longValue();
        }

        /**
         * UDP断开连接
         *
         * @param aMsg
         */
        private void writeRcuUDPDisconnect(ipc2msg aMsg)
        {
            LogUtil.w(tag, "writeRcuUDPDisconnect-1");
            EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.UDP_Disconnect);
            eb.writeToRcu(aMsg.getRealTime());
            LogUtil.w(tag, "writeRcuUDPDisconnect-2");
        }

    }

    /**
     * 广播接收器:接收通信过程中的信令
     */
    private BroadcastReceiver mEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals(WalkMessage.ACTION_EVENT)) {
                int rcuId = intent.getIntExtra(WalkMessage.KEY_EVENT_RCUID, 0);
                if (rcuId == UnifyParaID.L_THR_DL_UDP) {//上行
                    if (isStart && isUpload) {
                        count += 1;
                        float value = intent.getFloatExtra("nb_up_value", 0) / 1000f;
                        udpUPValues += value;
                        sendCurrentRate(udpUPValues / count, value);
                    }
                } else if (rcuId == UnifyParaID.L_THR_DL_DDP) {//下行
                    if (isStart && !isUpload) {
                        count += 1;
                        float value = intent.getFloatExtra("nb_dp_value", 0) / 1000f;
                        udpUPValues += value;
                        sendCurrentRate(udpUPValues / count, value);
                    }
                } else if (rcuId == UnifyParaID.L_Thr_UL_RLC_Thr) {
                    if (isStart && isUpload) {
                        countrlc += 1;
                        float value = intent.getFloatExtra("nb_dp_value1", 0);
                        udpValue_rlc_up += value;
                    }
                } else if (rcuId == UnifyParaID.L_Thr_DL_RLC_Thr) {
                    if (isStart && !isUpload) {
                        countrlc += 1;
                        float value = intent.getFloatExtra("nb_dp_value2", 0);
                        udpValue_rlc_down += value;
                    }
                } else if (rcuId == UnifyParaID.L_Thr_UL_MAC_Thr) {
                    if (isStart && isUpload) {
                        countmac += 1;
                        float value = intent.getFloatExtra("nb_dp_value3", 0);
                        udpValue_mac_up += value;
                    }
                } else if (rcuId == UnifyParaID.L_Thr_DL_MAC_Thr) {
                    if (isStart && !isUpload) {
                        countmac += 1;
                        float value = intent.getFloatExtra("nb_dp_value4", 0);
                        udpValue_mac_down += value;
                    }
                } else if (rcuId == UnifyParaID.L_Thr_UL_Phy_Thr) {
                    if (isStart && isUpload) {
                        countphy += 1;
                        float value = intent.getFloatExtra("nb_dp_value5", 0);
                        udpValue_phy_up += value;
                    }
                } else if (rcuId == UnifyParaID.L_Thr_DL_Phy_Thr) {
                    if (isStart && !isUpload) {
                        countphy += 1;
                        float value = intent.getFloatExtra("nb_dp_value6", 0);
                        udpValue_phy_down += value;
                    }
                } else if (rcuId == UnifyParaID.L_Thr_UL_PDCP_Thr) {
                    if (isStart && isUpload) {
                        countpdcp += 1;
                        float value = intent.getFloatExtra("nb_dp_value7", 0);
                        udpValue_pdcp_up += value;
                    }
                } else if (rcuId == UnifyParaID.L_Thr_DL_PDCP_Thr) {
                    if (isStart && !isUpload) {
                        countpdcp += 1;
                        float value = intent.getFloatExtra("nb_dp_value8", 0);
//                        LogUtil.w("wwww","wwwwpppp"+value);
                        udpValue_pdcp_down += value;
                    }
                }
            }
        }

        /**
         * 发送瞬时速率到数据页面
         *
         * @param avg     平均速率
         * @param current 当前速率
         */
        private void sendCurrentRate(float avg, float current)
        {
            LogUtil.w(TAG, "avg=" + avg + ",current=" + current);
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put(WalkStruct.DataTaskValue.BordLeftTitle.name(), getString(R.string
                    .info_data_averagerate) + "@"
                    + UtilsMethod.decFormat.format(avg) + getString(R.string.info_rate_kbps));
            dataMap.put(WalkStruct.DataTaskValue.BordCurrentSpeed.name(), UtilsMethod.decFormat
                    .format(current));
            Message msg = callbackHandler.obtainMessage(DATA_CHANGE, dataMap);
            callbackHandler.sendMessage(msg);
        }
    };


    //注册广播接收器
    private void regedit()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.ACTION_EVENT);
        registerReceiver(mEventReceiver, filter);
    }


    /***
     * 发送广播
     * @param direct 方向0-上行 1-下行
     * @param name 名称
     * @param value 数值
     */
    private void sendUDPBroadCast(int direct, long value1, long value2, long value3, long value4)
    {
        intent = new Intent(TotalUDPView.ACTION);
        intent.putExtra("direct", direct);
        intent.putExtra("Walktour/data/1.txt", value1);
        intent.putExtra("Walktour/data/2.txt", value2);
        intent.putExtra("Walktour/data/3.txt", value3);
        intent.putExtra("Walktour/data/4.txt", value4);
        mContext.sendBroadcast(intent);
        intent = null;
    }
}
