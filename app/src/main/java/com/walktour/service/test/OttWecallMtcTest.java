package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.dingli.ott.MultipleAppTestMain;
import com.dingli.ott.event.MultipleAppEvent;
import com.dingli.ott.task.service.WalktourAutoService;
import com.dingli.ott.util.OttUtil;
import com.dingli.ott.weixin.WeiXinEvent;
import com.dingli.ott.weixinvf.WeiXinVFEvent;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.SafeHandler;
import com.walktour.base.util.StringUtil;
import com.walktour.gui.mos.CaculateModeFacade;
import com.walktour.gui.mos.TaskModelWrapper;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.gui.task.parsedata.model.task.ott.TaskWeCallModel;
import com.walktour.service.TestService;
import com.walktour.service.bluetoothmos.BluetoothMOSService;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;
import com.walktour.service.test.MultipleAppEvent.StringSpilt;
import com.walktour.service.test.MultipleAppEvent.WecallMtcEventor;

import java.util.Map;


/**
 * 微信被叫自动化测试服务类
 *
 * @author czc
 */
@SuppressLint("SdCardPath")
public class OttWecallMtcTest extends TestTaskService {

    private static final String TAG = "OttWecallMtcTest";
    private static final int SERVICE_READY_CODE = 1953;
    /**
     * 执行结束时的结果
     */
    private Map<String, String> resultMap;
    private MultipleAppTestMain m_multipleAppTestMain;
    /**
     * 当前关联的设备
     */
    private BluetoothMOSDevice mCurrMOCDevice;
    private CaculateModeFacade mCaculateModeFacade;
    private String rcuFileName = "";

    static {
        OttLibsManager.loadLib();
    }

    private SafeHandler mHd = new SafeHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == SERVICE_READY_CODE) {
                TaskWeCallModel multipleAppTestModel = (TaskWeCallModel) taskModel;
                String cmd = getCmdStr(taskModel.getTaskName());
                LogUtil.w(TAG, "task name:" + taskModel.getTaskName() + ",start cmd str:" + cmd);

                if (multipleAppTestModel.getMosTest() == TaskInitiativeCallModel.MOS_ON) {
                    mCaculateModeFacade = new CaculateModeFacade(OttWecallMtcTest.this);
                    mCaculateModeFacade.setDevice(mCurrMOCDevice);
                    mCaculateModeFacade.setRcuFile(rcuFileName);
                    mCaculateModeFacade.setModel(new TaskModelWrapper(multipleAppTestModel));
                }

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

        if (mCaculateModeFacade != null) {
            mCaculateModeFacade.stop();
        }

        //        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.w(TAG, "----onStartCommand----");
        int startFlag = super.onStartCommand(intent, flags, startId);
        rcuFileName = intent.getStringExtra(WalkMessage.TESTFILENAME);
        this.mCurrMOCDevice = intent.getParcelableExtra(BluetoothMOSService.EXTRA_KEY_BLUETOOTH_MOS);
        if (taskModel == null) {
            LogUtil.e(TAG, "taskModel is null,stop service");
            stopSelf();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!hasAutoTestPermission()) {
                        LogUtil.w(TAG, "warrning! walktour autoservice is not connected!");
                        OttUtil.openServicePermissonCompat(OttWecallMtcTest.this, WalktourAutoService.class);

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

    @SuppressLint("HandlerLeak")
    public class MultipleAppTestHandler extends DataTestHandler {
        TaskWeCallModel mTaskModel;  //测试对象
        private WecallMtcEventor mEvent;
        public String mIp = "";

        private MultipleAppTestHandler(TaskWeCallModel taskModel) {
            this.mTaskModel = taskModel;
            this.mEvent = new WecallMtcEventor(this);
            this.mEvent.setMosManager(mCaculateModeFacade);
        }

        public TaskWeCallModel getTaskModel() {
            return mTaskModel;
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what != MultipleAppEvent.MULTIPLE_APPTEST_RESP) {
                return;
            }

            resultMap = StringSpilt.spiltKeyValues(msg.obj.toString());

            String eventName = resultMap.get("event_name");
            if (eventName.equals("EVENT_MPAPPTEST_START")) {
                mIp = resultMap.get("mobile_ip");
                onAppTestStart();
            } else if (eventName.equals("EVENT_MPAPPTEST_INFO")) {
                String sub_event = resultMap.get("sub_event");
                if (sub_event.equals("SUB_EVENT_MPACTION_START")) {
                    onActionStart();
                } else if (sub_event.equals("SUB_EVENT_MPACTION_SUCCESS")) {
                    onActionSuccess();
                } else if (sub_event.equals("SUB_EVENT_MPACTION_FAILED")) {
                }
                onAppTestProcess();
            } else if (eventName.equals("EVENT_MPAPPTEST_END")) {
                onAppTestEnd();
            }
        }

        /**
         * 应用执行开始
         */
        private void onAppTestStart() {
            LogUtil.i(TAG, "onAppTestStart");
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
            totalResult(this);
            // 设置主进程中的firstdata状态
            setMainFirstDataState(false);
            stopProcess(TestService.RESULT_SUCCESS);
        }

        /**
         * 操作执行成功
         */
        private void onActionSuccess() {

        }


        /**
         * 操作执行开始
         */
        private void onActionStart() {

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

        /**
         * 统计
         *
         * @param handler 句柄
         */
        private void totalResult(DataTestHandler handler) {
            totalResult(TotalStruct.TotalAppreciation._OttTryTimes, 1);
            totalResult(TotalStruct.TotalAppreciation._OttSuccessTimes, mEvent.isSuccess() ? 1 : 0);
            totalResult(TotalStruct.TotalAppreciation._OttDelayTimes, mEvent.getDelayTime());
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
        TaskWeCallModel multipleAppTestModel = (TaskWeCallModel) taskModel;
        String cmd = "";
        String contactName = StringUtil.isEmpty(multipleAppTestModel.getContactName()) ? "2" : multipleAppTestModel.getContactName();

        if (WalkStruct.TaskType.WeCallMtc.getXmlTaskType().equals(task_name)
                || WalkStruct.TaskType.WeCallMoc.getXmlTaskType().equals(task_name)) {
            cmd = "WeiXinVF -u " + 1
                    + " -c " + contactName
                    + " -s " + multipleAppTestModel.getKeepTime()
                    + " -t " + multipleAppTestModel.getConnectTime() * 1000;
        }
        return cmd;
    }

    private boolean onStartAppAction(int action_type) {
        if (action_type == WeiXinEvent.WEIXIN_ACTION_TYPE_START_APP
                || action_type == WeiXinVFEvent.getInstace().MULTIPLE_APP_WEIXIN_ACTION_TYPE_CALLER_DIAL_AUDIO) {
            return true;
        }
        return false;
    }

}
