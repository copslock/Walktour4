package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Message;

import com.dingli.service.test.ipc2msg;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhone;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.task.base.URLInfo;
import com.walktour.gui.task.parsedata.model.task.multihttp.download.TaskMultiHttpDownModel;
import com.walktour.service.TestService;

import java.util.HashMap;
import java.util.Map;

/**
 * MultiHttpDown
 */
@SuppressLint("SdCardPath")
public class MultiHttpDownload extends TestTaskService {
    private final int Multi_HTTP_DOWN_TEST = 93;

    private final int HTTP_DOWN_INITED = 1;//程序初始化完毕
    private final int HTTP_DOWN_START = 2;//加载开始，表示业务开始
    private final int HTTP_DOWN_FIRSTDATA = 3;//第一个数据包到达
    private final int HTTP_DOWN_COUNT_START = 4;//稳定下载开始
    private final int HTTP_DOWN_QOS = 5;//品质数据（定期）
    private final int HTTP_DOWN_FALIED = 10;//业务失败
    private final int HTTP_DOWN_DROP = 11;//业务失败
    private final int HTTP_DOWN_FINISH = 12;//业务完成
    private final int HTTP_DOWN_QUIT = 13;//程序退出
    private final int HTTP_DOWN_START_TEST = 1001;//开始业务
    private final int HTTP_DOWN_STOP_TEST = 1006;//停止业务

    @Override
    public void onCreate() {
        super.onCreate();
        tag = "MultiHttpDownload";
        mContext=this;
        LogUtil.i(tag, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UtilsMethod.killProcessByPname("com.walktour.service.test.MultiHttpDownload", false);
        UtilsMethod.killProcessByPname(AppFilePathUtil.getInstance().getAppLibFile("datatests_android").getAbsolutePath(), false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(tag, "onStart");
        int startFlag = super.onStartCommand(intent, flags, startId);
        if (taskModel instanceof TaskMultiHttpDownModel) {
            dataTestHandler = new HttpDownHandler((TaskMultiHttpDownModel) taskModel);
        }

        if (dataTestHandler != null) {
            dataTestHandler.startTest();
        }

        return startFlag;
    }

    @SuppressLint("HandlerLeak")
    private class HttpDownHandler extends DataTestHandler {
        private TaskMultiHttpDownModel httpModel;

        private double avgSpeed=0;
        private double curSpeed=0;
        public HttpDownHandler(TaskMultiHttpDownModel httpModel) {
            super("-m mhttp_down -z " + AppFilePathUtil.getInstance().getAppConfigDirectory(),
                    AppFilePathUtil.getInstance().getAppLibFile(useRoot ? "datatests_android" : "libdatatests_so.so").getAbsolutePath(),
                    Multi_HTTP_DOWN_TEST, HTTP_DOWN_START_TEST, HTTP_DOWN_STOP_TEST);
            this.httpModel = httpModel;
        }

        @Override
        public void handleMessage(Message msg) {
            ipc2msg aMsg = (ipc2msg) msg.obj;
            if (aMsg.test_item != Multi_HTTP_DOWN_TEST) {
                LogUtil.w(tag,"aMsg.test_item is failure.");
                return;
            }
            hasEventCallBack = true;    //设置业务库有事件回调
            LogUtil.i(tag,"aMsg.event_id:"+aMsg.event_id+"\r\n"+"aMsg.data:"+aMsg.data + "\r\n");
            switch (aMsg.event_id) {
                case HTTP_DOWN_INITED:
                    StringBuilder event_data = new StringBuilder();
                    event_data.append("local_if::").append("").append("\n");
                    event_data.append("play_time_ms::").append(httpModel.getDownloadTimeout() * 1000).append("\n");
                    event_data.append("qos_inv_ms::").append(1 * 1000).append("\n");
                    event_data.append("connect_timeout_ms::").append(30 * 1000).append("\n");
                    event_data.append("nodata_timeout_ms::").append(httpModel.getNoDataTimeOut() * 1000).append("\n");
                    event_data.append("count_wait_time_ms::").append(2 * 1000).append("\n");
                    event_data.append("multitest_type::").append(httpModel.getEndCodition()==0?"1":"2").append("\n");
                    StringBuffer sbfer=new StringBuffer();

                    for(int i=0;i<httpModel.getUrlList().size();i++){
                        sbfer.append(httpModel.getUrlList().get(i).getUrl());
                        if(i<httpModel.getUrlList().size()-1){
                            sbfer.append("||");
                        }
                    }
                    event_data.append("urls::").append(sbfer.toString()).append("\n");
                    event_data.append("thread_count::").append(httpModel.getThreadCount()).append("\n");
                    LogUtil.i(tag, event_data.toString());
                    sendStartCommand(event_data.toString());
                    sbfer.setLength(0);
                    sbfer=null;
                    break;
                case HTTP_DOWN_START:
                    StringBuffer sbfer2=new StringBuffer();

                    for(int i=0;i<httpModel.getUrlList().size();i++){
                        sbfer2.append(httpModel.getUrlList().get(i).getUrl());
                        if(i<httpModel.getUrlList().size()-1){
                            sbfer2.append("||");
                        }
                    }
                    totalResult(this, TotalStruct.TotalMultiHttpDownload._TryTimes, 1);
                    EventBytes.Builder(MultiHttpDownload.this, RcuEventCommand.Multi_HTTP_Down_Start)
                            .addInteger(httpModel.getDownloadTimeout() * 1000)
                            .addInteger(httpModel.getNoDataTimeOut() * 1000)
                            .addInteger(httpModel.getNoDataTimeOut() * 1000)
                            .addInteger(httpModel.getThreadCount())
                            .addStringBuffer(sbfer2.toString())
                            .addStringBuffer(MyPhoneState.getInstance().getLocalIpv4Address())
                            .addStringBuffer(MyPhone.getApnName(mContext))
                            .addInteger(httpModel.getEndCodition()==0?1:2)
                            .writeToRcu(aMsg.getRealTime());
                    sbfer2.setLength(0);
                    sbfer2=null;
                    break;
                // 响应第一个数据
                case HTTP_DOWN_FIRSTDATA:
                    break;
                case HTTP_DOWN_COUNT_START:
                    String[] qos = aMsg.data.split("\n");
                    if(qos.length>0) {
                        EventBytes.Builder(MultiHttpDownload.this, RcuEventCommand.Multi_HTTP_Down_First_Data)
                                .addInt64(Long.parseLong(qos[0].split("::")[1]))
                                .writeToRcu(aMsg.getRealTime());
                    }else{
                        writeRcuEvent(RcuEventCommand.Multi_HTTP_Down_First_Data, aMsg.getRealTime());
                    }
                    break;
                case HTTP_DOWN_QOS:
                    qos=null;
                    qos = aMsg.data.split("\n");
                    long measTime = Long.parseLong(qos[0].split("::")[1]);
                    long transByte = Long.parseLong(qos[1].split("::")[1]);
                    avgSpeed = (long) Double.parseDouble(qos[2].split("::")[1]);
                    curSpeed = (long) Double.parseDouble(qos[3].split("::")[1]);
                    sendCurrentRate();
                    //刷新表盘
                    break;
                case HTTP_DOWN_FALIED:
                    totalResult(this, TotalStruct.TotalMultiHttpDownload._FalureTimes, 1);
                    writeRcuEvent(RcuEventCommand.Multi_HTTP_Down_Failure, aMsg.getRealTime(), getReason(aMsg.data));
                    break;
                // 下载Drop
                case HTTP_DOWN_DROP:
                    totalResult(this, TotalStruct.TotalMultiHttpDownload._SuccessTimes, 1);
                    writeRcuEvent(RcuEventCommand.Multi_HTTP_Down_Drop, aMsg.getRealTime(), getReason(aMsg.data));
                    break;
                // 最后一个数据
                case HTTP_DOWN_FINISH:
                    totalResult(this, TotalStruct.TotalMultiHttpDownload._SuccessTimes, 1);
                    qos = aMsg.data.split("\n");
                    long transbytes_from_firstdata = Long.parseLong(qos[0].split("::")[1]);
                    Long transtime_from_firstdata = Long.parseLong(qos[1].split("::")[1]);
//                    double avgspeed_from_firstdata = Double.parseDouble(qos[2].split("::")[1]);
                    double maxspeed_from_firstdata =Double.parseDouble(qos[3].split("::")[1]);
                    long transbytes_from_countstart = Long.parseLong(qos[4].split("::")[1]);
                    int transtime_from_countstart = Integer.parseInt(qos[5].split("::")[1]);
//                    double avgspeed_from_countstart = Double.parseDouble(qos[6].split("::")[1]);
                    double maxspeed_from_countstart = Double.parseDouble(qos[7].split("::")[1]);

                    EventBytes.Builder(mContext, RcuEventCommand.Multi_HTTP_Down_Last_Data)
                            .addInt64(transbytes_from_firstdata)
                            .addInteger(transtime_from_firstdata.intValue())
                            .addDouble(maxspeed_from_firstdata)
                            .addInt64(transbytes_from_countstart)
                            .addInteger(transtime_from_countstart)
                            .addDouble(maxspeed_from_countstart)
                            .writeToRcu(aMsg.getRealTime());
                    break;

                case HTTP_DOWN_QUIT:
                    stopProcess(TestService.RESULT_FAILD);
                    break;

            }
        }

        @Override
        protected void prepareTest() {

        }

        @Override
        protected void fail(int failReason, long time) {
        }

        @Override
        protected void drop(int dropReason, long time) {
        }

        @Override
        protected void sendCurrentRate() {
            LogUtil.w(tag, "avg=" + avgSpeed + ",current=" + curSpeed);
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put(WalkStruct.DataTaskValue.BordLeftTitle.name(), getString(R.string.info_data_averagerate) + "@"
                    + UtilsMethod.decFormat.format(avgSpeed) + getString(R.string.info_rate_kbps));
            dataMap.put(WalkStruct.DataTaskValue.BordCurrentSpeed.name(), UtilsMethod.decFormat.format(curSpeed));
            Message msg = callbackHandler.obtainMessage(DATA_CHANGE, dataMap);
            callbackHandler.sendMessage(msg);
        }

        @Override
        protected void lastData(long time) {


        }

        /**
         * 统计
         *
         * @param totalType 统计项
         * @param value     值
         */
        private void totalResult(DataTestHandler handler, TotalStruct.TotalMultiHttpDownload totalType, long value) {
            HashMap<String, Long> map = new HashMap<String, Long>();
            map.put(totalType.name(), value);
            handler.totalResult(map);
        }
    }
}
