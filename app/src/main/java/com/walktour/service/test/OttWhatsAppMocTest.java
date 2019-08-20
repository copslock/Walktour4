package com.walktour.service.test;

import android.content.Intent;

import com.dingli.ott.MultipleAppTestMain;
import com.dingli.ott.event.MultipleAppEvent;
import com.walktour.Utils.TotalStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.StringUtil;
import com.walktour.control.bean.MyPhone;
import com.walktour.gui.task.parsedata.model.task.ott.TaskMultipleAppTestModel;
import com.walktour.service.TestService;
import com.walktour.service.bluetoothmos.BluetoothMOSService;
import com.walktour.service.bluetoothmos.BluetoothMTCService;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;
import com.walktour.service.test.MultipleAppEvent.StringSpilt;
import com.walktour.service.test.MultipleAppEvent.WecallMocEventor;
import com.walktour.service.test.MultipleAppEvent.WhatsAppMocEventor;

import java.util.Map;


/**
 * WhatsApp 语音拨打自动化测试服务类
 *
 * @author czc
 */
public class OttWhatsAppMocTest extends TestTaskService {

    private static final String TAG = "OttWhatsAppMocTest";
    /**
     * 执行结束时的结果
     */
    private Map<String, String> resultMap;
    private MultipleAppTestMain m_multipleAppTestMain;


    static {
        OttLibsManager.loadLib();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.e(TAG, "onDestroy");

        if (m_multipleAppTestMain != null) {
            m_multipleAppTestMain.Stop();
        }
        new MyPhone(this).moveToHome();
        // 因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.w(TAG, "----onStartCommand----");
        int startFlag = super.onStartCommand(intent, flags, startId);

        if (taskModel == null) {
            stopSelf();
        } else {
            TaskMultipleAppTestModel multipleAppTestModel = (TaskMultipleAppTestModel) taskModel;
            String cmd = getCmdStr();
            LogUtil.w(TAG, "task name:" + taskModel.getTaskName() + ",start cmd str:" + cmd);

            dataTestHandler = new MultipleAppTestHandler(multipleAppTestModel);
            this.m_multipleAppTestMain = MultipleAppTestMain.getInstance(mContext);
            this.m_multipleAppTestMain.SetRespHandler(dataTestHandler);
            this.m_multipleAppTestMain.Run(cmd);
        }
        return startFlag;
    }

    public class MultipleAppTestHandler extends DataTestHandler {
        private TaskMultipleAppTestModel mTaskModel;  //测试对象
        private WhatsAppMocEventor mEvent;
        public String mIp = "";

        private MultipleAppTestHandler(TaskMultipleAppTestModel taskModel) {
            this.mTaskModel = taskModel;
            this.mEvent = new WhatsAppMocEventor(this, taskModel);
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

        private void onAppTestEnd() {
            LogUtil.i(TAG, "recv MULTIPLE_APPTEST_END " + this.mTaskModel.getTaskName());
            lastDataTime = System.currentTimeMillis() * 1000;

            mEvent.handleEndEvent();

            // 设置主进程中的firstdata状态
            setMainFirstDataState(false);
            stopProcess(TestService.RESULT_SUCCESS);
        }


        @Override
        protected void prepareTest() {

        }

        @Override
        protected void fail(int failReasonCode, long time) {
        }

        @Override
        protected void drop(int dropReason, long time) {

        }

        @Override
        protected void sendCurrentRate() {

        }

        @Override
        protected void lastData(long time) {

        }

        @Override
        public void sendStopCommand() {
            m_multipleAppTestMain.Stop();
            LogUtil.w(TAG, "send Stop Command app test");
        }
    }

    private String getCmdStr() {
        TaskMultipleAppTestModel multipleAppTestModel = (TaskMultipleAppTestModel) taskModel;
        String contactName = StringUtil.isEmpty(multipleAppTestModel.getContactName()) ? "dingli" : multipleAppTestModel.getContactName();
        String cmd = "WhatsAppVF -u " + multipleAppTestModel.getAudioCallMode()
                + " -c " + contactName
                + " -d " + multipleAppTestModel.getDialMode()
                + " -s " + multipleAppTestModel.getAudioCallSeconds()
                + " -t " + multipleAppTestModel.getTaskTimeout() * 1000;
        return cmd;
    }
}
