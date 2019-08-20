package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.dingli.ott.MultipleAppTestMain;
import com.dingli.ott.event.MultipleAppEvent;
import com.dingli.ott.task.service.WalktourAutoService;
import com.dingli.ott.util.OttUtil;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.SafeHandler;
import com.walktour.base.util.StringUtil;
import com.walktour.control.bean.MyPhone;
import com.walktour.gui.task.parsedata.model.task.ott.TaskMultipleAppTestModel;
import com.walktour.service.TestService;
import com.walktour.service.test.MultipleAppEvent.SkyPeEventor;
import com.walktour.service.test.MultipleAppEvent.StringSpilt;

import java.util.Map;


/**
 * Skype自动化测试服务类
 *
 * @author czc
 */
@SuppressLint("SdCardPath")
public class OttSkypeTest extends TestTaskService {
    private static final int SERVICE_READY_CODE = 1953;
    private static final String TAG = "OttSkypeTest";
    /**
     * 执行结束时的结果
     */
    private Map<String, String> resultMap;
    private MultipleAppTestMain m_multipleAppTestMain;

    static {
        OttLibsManager.loadLib();
    }

    private SafeHandler mHd = new SafeHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == SERVICE_READY_CODE) {
                TaskMultipleAppTestModel multipleAppTestModel = (TaskMultipleAppTestModel) taskModel;
                String cmd = getCmdStr(taskModel.getTaskName());
                LogUtil.w(TAG, "task name:" + taskModel.getTaskName() + ",start cmd str:" + cmd);

                dataTestHandler = new MultipleAppTestHandler(multipleAppTestModel);
                m_multipleAppTestMain = MultipleAppTestMain.getInstance(mContext);
                m_multipleAppTestMain.SetRespHandler(dataTestHandler);
                m_multipleAppTestMain.Run(cmd);

                return true;
            }
            return false;
        }
    });

    @Override
    public void onDestroy() {
        isSingleProcess = false;
        super.onDestroy();
        LogUtil.e(TAG, "onDestroy");

        if (m_multipleAppTestMain != null) {
            m_multipleAppTestMain.Stop();
        }
        new MyPhone(this).moveToHome();
        UtilsMethod.execRootCmdx("am force-stop com.skype.raider");
        // 因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
//        UtilsMethod.killProcessByPname("com.walktour.service.test.OttTest", false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.w(TAG, "----onStartCommand----");
        int startFlag = super.onStartCommand(intent, flags, startId);
        if (taskModel == null) {
            stopSelf();
        } else {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!hasAutoTestPermission()) {
                        LogUtil.w(TAG, "warrning! walktour autoservice is not connected!");
                        OttUtil.openServicePermissonCompat(OttSkypeTest.this, WalktourAutoService.class);

                        int t = 5;
                        while (!hasAutoTestPermission() && t > 0) {
                            try {
                                Thread.sleep(1000);
                                t--;
                                LogUtil.w(TAG, "Geting walktour autoservice status times, t = " + t);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if (!hasAutoTestPermission()) {
                            LogUtil.e(TAG, "can not start walktourautoservice!");
                        } else {
                            LogUtil.i(TAG, "start walktourautoservice success!");
                            mHd.sendEmptyMessage(SERVICE_READY_CODE);

                        }
                    } else {
                        LogUtil.i(TAG, "walktourautoservice is connected!");
                        mHd.sendEmptyMessage(SERVICE_READY_CODE);
                    }
                }
            }).start();
        }
        return startFlag;
    }

    private boolean hasAutoTestPermission() {
        return OttUtil.hasServicePermission(this, WalktourAutoService.class);
    }

    public class MultipleAppTestHandler extends DataTestHandler {

        public String mIp = "";
        private SkyPeEventor mEvent;
        private TaskMultipleAppTestModel mTaskModel;  //测试对象

        private MultipleAppTestHandler(TaskMultipleAppTestModel taskModel) {
            this.mTaskModel = taskModel;
            this.mEvent = new SkyPeEventor(this);
        }

        public TaskMultipleAppTestModel getTaskModel() {
            return mTaskModel;
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MultipleAppEvent.MULTIPLE_APPTEST_RESP) {
                resultMap = StringSpilt.spiltKeyValues(msg.obj.toString());
                String eventName = resultMap.get("event_name");
                if (eventName.equals("EVENT_MPAPPTEST_START")) {
                    mIp = resultMap.get("mobile_ip");
                    onAppTestStart();
                } else if (eventName.equals("EVENT_MPAPPTEST_INFO")) {
                    onAppTestProcess();
                } else if (eventName.equals("EVENT_MPAPPTEST_END")) {
                    onAppTestEnd();
                }
            }
        }

        /**
         * 应用执行开始
         */
        private void onAppTestStart() {
            LogUtil.i(TAG, "recv EVENT_MPAPPTEST_START " + this.mTaskModel.getTaskName());
            firstDataTime = System.currentTimeMillis() * 1000;
            mEvent.handleStartEvent();
            // 设置主进程中的firstdata状态
            setMainFirstDataState(true);
        }

        private void onAppTestProcess() {
            mEvent.handleProcessEvent(resultMap);
        }

        /**
         * 应用测试结束
         */
        private void onAppTestEnd() {
            LogUtil.i(TAG, "recv MULTIPLE_APPTEST_END " + this.mTaskModel.getTaskName());
            lastDataTime = System.currentTimeMillis() * 1000;

            mEvent.handleEndEvent();

            // 统计页面
            totalResult();
            // 设置主进程中的firstdata状态
            setMainFirstDataState(false);
            stopProcess(TestService.RESULT_SUCCESS);
        }


        @Override
        protected void prepareTest() {

        }

        @Override
        protected void drop(int dropReasonCode, long time) {

        }

        @Override
        protected void sendCurrentRate() {
        }

        @Override
        protected void lastData(long time) {
        }

        private void totalResult() {
            totalResult(TotalStruct.TotalAppreciation._OttTryTimes, 1);
            totalResult(TotalStruct.TotalAppreciation._OttSuccessTimes, 1);
            totalResult(TotalStruct.TotalAppreciation._OttDelayTimes, 0);
        }

        @Override
        protected void fail(int failReasonCode, long time) {
        }

        @Override
        public void sendStopCommand() {
            m_multipleAppTestMain.Stop();
            LogUtil.w(TAG, "send Stop Command app test");
        }
    }


    //获取各种业务制定命令行方式执行
    private String getCmdStr(String task_name) {

		/* WeiXin -s 0 -m how are you -p 1 -v 8000 -t 30000
			-s 		1表示开启，0表示不开启APP界面
			-m		发送消息内容
			-p		picture发送图片 1:1M 2:3M
			-v		voice 发送语音时长(s) 默认：8s
			-t      timeout 默认：30000ms
		*/
        TaskMultipleAppTestModel multipleAppTestModel = (TaskMultipleAppTestModel) taskModel;
        String contactName = StringUtil.isEmpty(multipleAppTestModel.getContactName()) ? "2" : multipleAppTestModel.getContactName();
        int pic_type = multipleAppTestModel.getSendPictureType() + 1;
        String cmd = "Skype -m " + multipleAppTestModel.getSendText()
                + " -p " + pic_type
                + " -c " + contactName
                + " -a " + multipleAppTestModel.getVoiceDuration()
                + " -t " + multipleAppTestModel.getTaskTimeout();
        return cmd;
    }
}
