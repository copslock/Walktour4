package com.walktour.service.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;

import com.dinglicom.dataset.model.DataSetEvent;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.TotalStruct.TotalAttach;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.task.parsedata.model.task.attach.TaskAttachModel;
import com.walktour.service.TestService;

import java.util.HashMap;

/***
 * Attach业务
 */
public class AttachTest extends TestTaskService {
    private final String tag = "AttachTest";

    //测试任务相关
    private TaskAttachModel taskModel;

    private long activeTime = 0;        //开始激活网络时间
    private long attachRequestTime = 0;    /*开始Attach的时间*/
    private long attachAcceptTime = 0;
    private boolean isLteAttach = false;

    private boolean hasDetachAccept = false;//发出Detach指令后是否收到Detach Accept
    private boolean hasAttachRequest = false;//是否已经收到Attach Request
    private boolean hasAttachAccept = false;//发出Attach后是否收到Attach Accept

    boolean hasStopProcess = false;
    boolean hasFail = false;

    //是否执行了attach,防止没执行attach.
    boolean isRunAttach = false;

    long detachRequestTime = 0;
    long detachSuccessTime = 0;
    long detachFailureTime = 0;

    @Override
    public void onCreate()
    {
        super.onCreate();
        LogUtil.i(tag, "---onCreate");
        regedit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        LogUtil.i(tag, "---onStart");
        int startFlag = super.onStartCommand(intent, flags, startId);
        taskModel = (TaskAttachModel) super.taskModel;
        new Thread(new ThreadTest()).start();
        return startFlag;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //中断的情况下,如果attach未做,还需要做一次
        runAttach();
        unregisterReceiver(mEventReceiver);
        LogUtil.i(tag, "---onDestroy");
    }

    /**
     * 测试线程
     */
    private class ThreadTest implements Runnable {
        @Override
        public void run()
        {
            //按测试次数进行测试
            for (int i = 0; i < taskModel.getRepeat(); i++) {
                if (i > 0) {
                    addTagEvent(System.currentTimeMillis(), "Start Task:" + taskModel.getTaskName() + "-" + (i + 1));
                }
                isRunAttach = false;
                attachRequestTime = System.currentTimeMillis();//初始化数据
                repeatTimes = i + 1;
                //发送PDP Deactive指令,，测试之前先确保手机处于未激活状态
                detachRequestTime = System.currentTimeMillis();
                detachSuccessTime = detachRequestTime;
                detachFailureTime = detachRequestTime;
                deactivity();
                //间隔T/2时间，发送PDP Active指令
                try {
                    if (taskModel.getInterVal() < 5 || taskModel.getInterVal() > 60) {
                        Thread.sleep(5 * 1000);
                    } else {
                        Thread.sleep(taskModel.getInterVal() * 1000);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //发送PDP Active指令
                activate();
                //等待超时时间内返回成功指令
                boolean hasSuccess = waitforActivity();
                if (hasSuccess) {
                    try {
                        if (taskModel.getInterVal() < 5 || taskModel.getInterVal() > 60) {
                            Thread.sleep(5 * 1000);
                        } else {
                            Thread.sleep(taskModel.getInterVal() * 1000);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    fail(System.currentTimeMillis() * 1000);
                }
            }

            stopProcess(TestService.RESULT_SUCCESS);
        }

        /**
         * 发送PDP Deactive指令
         */
        private void deactivity()
        {
            hasDetachAccept = false;

            //发送Deactivity指令，连续3次直到成功返回Deactivate PDP Context Accept
            //phone.setRadioPower(false);
            callbackHandler.obtainMessage(CALL_MAINPROCESS, WalkCommonPara.CallMainType_Do_Detach, 0).sendToTarget();
            //等待5秒内收到
            for (int j = 0; j < 5 * 10 && !hasDetachAccept; j++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 发送Activate PDP Context Request
         */
        private void activate()
        {
            hasAttachRequest = false;
            hasAttachAccept = false;
            //存储PDP_Start事件　
            writeRcuEvent(RcuEventCommand.Attach_Start, System.currentTimeMillis() * 1000,
                    RcuEventCommand.TEST_TYPE_Attach);

            activeTime = System.currentTimeMillis();

            //发送Deactivity指令
            //phone.setRadioPower(true);
            runAttach();
            //等待5秒内直到Attach_Request信令返回
            while (!hasAttachRequest) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (System.currentTimeMillis() - activeTime > 30 * 1000) {
                    break;
                }
            }
        }

        /**
         * 等待Activate PDP Context Request
         *
         * @return 是否成功
         */
        private boolean waitforActivity()
        {
            for (int i = 0; i < taskModel.getAttachTestConfig().getKeepTime() &&
                    !hasAttachAccept; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //统计一次结果
            totalResult();
            return hasAttachAccept;
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
                LogUtil.w(tag, "RCUID=" + Integer.toHexString(rcuId));
                switch (rcuId) {
                    case DataSetEvent.ET_LTEMsgDetachRequest://417
                        writeRcuEvent(RcuEventCommand.DETACH_REQUEST, System.currentTimeMillis()
                                * 1000, (int) (System.currentTimeMillis() - detachRequestTime));
                        hasDetachAccept = true;
                        isLteAttach = false;
                        break;
                    case DataSetEvent.ET_DetachAcceptMsg://7D
                    case DataSetEvent.ET_LTEMsgDetachAccept://418
                        writeRcuEvent(RcuEventCommand.DETACH_SUCCESS, System.currentTimeMillis()
                                * 1000, (int) (System.currentTimeMillis() - detachSuccessTime));
                        hasDetachAccept = true;
                        isLteAttach = false;
                        break;

                    case DataSetEvent.ET_LTEMsgAttachRequest://412
                        isLteAttach = true;
                        //这里没有break;
                    case DataSetEvent.ET_AttachRequestMsg://78
                        attachRequestTime = System.currentTimeMillis();
                        //由于信令同时返回两条,这里控制只显示第一条
                        if (!hasAttachRequest) {
                            hasAttachRequest = true;
                            //存储Attach Request
                            writeRcuEvent(isLteAttach ? RcuEventCommand.Attach_LTE_Start :
                                    RcuEventCommand.Attach_Start, System.currentTimeMillis() *
                                    1000, RcuEventCommand.TEST_TYPE_Attach);
                            //显示Attach请求事件
                            showEvent((isLteAttach ? "LTE " : "") + "Attach Request");
                        }
                        break;

                    case DataSetEvent.ET_LTEMsgAttachComplete://414
                        if (!hasAttachAccept) {
                            hasAttachAccept = true;
                            attachAcceptTime = System.currentTimeMillis();
                            long delay = attachAcceptTime - attachRequestTime;
                            //存储Attach Accept事件　
                            writeRcuEvent(RcuEventCommand.Attach_Success, System
                                    .currentTimeMillis() * 1000, (int) delay);
                            //显示Attach Accept事件
                            showEvent((isLteAttach ? "LTE " : "") + "Attach Success: Delay " +
                                    delay + "(ms)");
                        }
                        break;
                    case DataSetEvent.ET_AttachAcceptMsg://79
                        if (!hasAttachAccept) {
                            hasAttachAccept = true;
                            attachAcceptTime = System.currentTimeMillis();
                            long delay = attachAcceptTime - attachRequestTime;
                            //存储Attach Accept事件　
                            writeRcuEvent(RcuEventCommand.Attach_Success, System
                                    .currentTimeMillis() * 1000, (int) delay);
                            //显示Attach Accept事件
                            showEvent((isLteAttach ? "LTE " : "") + "Attach Success: Delay " +
                                    delay + "(ms)");
                        }
                        break;

                    case DataSetEvent.ET_LTEMsgAttachReject://415
                    case DataSetEvent.ET_AttachRejectMsg:
                        LogUtil.w(tag, "ET_LTEMsgAttachReject or ET_AttachRejectMsg.");
                        hasAttachAccept = false;
                        fail(System.currentTimeMillis() * 1000);
                        break;
                }

                if (isInterrupted) {//中断业务测试要重新上电
                    runAttach();
                }
            }
        }
    };

    //注册广播接收器
    private void regedit()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.ACTION_EVENT);
        this.registerReceiver(mEventReceiver, filter);
    }

    /**
     * Attach失败
     */
    private void fail(long time)
    {
        LogUtil.w(tag, "hasFail=" + hasFail);
        if (!hasFail) {
            hasFail = true;
            int delay = (int) (System.currentTimeMillis() - attachRequestTime);

            if (isLteAttach) {
                //存储PDP Activate失败事件　
                writeRcuEvent(RcuEventCommand.Attach_LTE_Fail, time, delay);
                //显示PDP Activate失败事件
                showEvent("LTE Attach Failure：Delay " + delay + "(ms)");
            } else {
                //存储PDP Activate失败事件　
                writeRcuEvent(RcuEventCommand.Attach_Fail, time, delay);
                //显示PDP Activate失败事件
                showEvent("Attach Failure：Delay " + delay + "(ms)");
            }
        }
        runAttach();
    }

    /**
     * 统计结果
     */
    private void totalResult()
    {
        if (hasAttachRequest) {
            HashMap<String, Integer> totalMap = new HashMap<String, Integer>();
            totalMap.put(isLteAttach ? TotalAttach._lteAttachRequest.name() :
                    TotalAttach._attachRequest.name(), 1);
            if (isLteAttach) {
                totalMap.put(TotalAttach._lteSearchDelay.name(),
                        (int) (attachRequestTime - activeTime));
            }
            if (hasAttachAccept) {
                totalMap.put(isLteAttach ? TotalAttach._lteAttachSuccess.name() :
                        TotalAttach._attachSuccess.name(), 1);
                int delay = (int) (attachAcceptTime - attachRequestTime);
                totalMap.put(isLteAttach ? TotalAttach._lteAttachDelay.name()
                        : TotalAttach._attachDelay.name(), delay);

            }

            if (!totalMap.isEmpty()) {
                Message msg = callbackHandler.obtainMessage(CHART_CHANGE, totalMap);
                msg.sendToTarget();
            }
        }
    }

    /**
     * 停止当次测试(退出当前测试服务)
     *
     * @param result
     */
    private void stopProcess(String result)
    {
        if (!hasStopProcess) {
            hasStopProcess = true;
            if (!isInterrupted) {
                runAttach();
                hasAttachRequest = false;
            }
            Message msg = callbackHandler.obtainMessage(TEST_STOP, result);
            msg.sendToTarget();
        }
    }

    private void runAttach()
    {
        if (!isRunAttach) {
            callbackHandler.obtainMessage(CALL_MAINPROCESS, WalkCommonPara.CallMainType_Do_Attach, 0).sendToTarget();
            isRunAttach = true;
        }
    }
}