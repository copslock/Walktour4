package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;

import com.dinglicom.dataset.EventManager;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.task.parsedata.model.task.reboot.TaskRebootModel;

/**
 * 自动开关机测试
 */
@SuppressLint("SdCardPath")
public class RebootTest extends TestTaskService {
    private static final String TAG = "RebootTest";
    private String time;
    private ApplicationModel appModel;

    @Override
    public void onCreate() {
        super.onCreate();
        tag = RebootTest.class.getSimpleName();
        LogUtil.i(tag, "onCreate");
        appModel = ApplicationModel.getInstance();
        SharePreferencesUtil.getInstance(this).saveBoolean(WalktourConst.IS_NEED_REBOOT,true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.w(TAG, "--Local onDestroy--");
        UtilsMethod.killProcessByPname("com.walktour.service.test.RebootTest", true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(tag, "onStart");
        int startFlag = super.onStartCommand(intent, flags, startId);
        if (taskModel instanceof TaskRebootModel) {
            time = ((TaskRebootModel) taskModel).getRebootTestConfig().getRebootTime();
            new MyThread().start();
        }
        return startFlag;
    }


    private class MyThread extends Thread {


        @Override
        public void run() {

            EventManager.getInstance().addTagEvent(mContext, System.currentTimeMillis(), "Reboot_Start");

            try {
                /**1.先休息rebootTime
                 * 2、再停止测试
                 * 3.再休息2s
                 * 4. 再重启(因为重启的广播包含停止测试，休息2秒，以上2点不需要写)
                 *
                 * */
                int rebootTime = Integer.parseInt(time);
                Thread.sleep(rebootTime*1000);
                EventManager.getInstance().addTagEvent(mContext, System.currentTimeMillis(), "Reboot_End");
//                sendBroadcast(new Intent(WalkMessage.Action_Walktour_Test_Interrupt));//停止测试
                sendBroadcast(new Intent(WalkMessage.InteruptTestAndRebootDevice));  //重启手机
            } catch (Exception e) {
                e.printStackTrace();
            }

            stopSelf();
        }
    }

}
