package com.walktour.externalinterface;

import android.content.Context;
import android.content.Intent;

import com.dinglicom.DataSetLib;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.StringUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.gui.BuildConfig;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * 外部测试任务控制
 *
 * @author zhicheng.chen
 * @date 2018/12/5
 */
public class AidlTestControllor {

    /**
     * log 文件数
     */
    public static final String EXTRA_FILE_COUNT = "extra_file_count";

    public static final int TYPE_XML = 1;
    public static final int TYPE_JSON = 2;


    private long mStartTestTime;
    private long mStopTestTime;
    private int mLogFileCount;

    public AidlTestControllor() {

    }

    public void loadTaskFile(int type, String filePath) {
        if (!StringUtil.isEmpty(filePath)) {
            if (TYPE_XML == type) {
                if (new File(filePath).exists()) {
                    String fileName = TaskListDispose.getInstance().getFileName();
                    try {
                        UtilsMethod.copyFile(new File(filePath), new File(fileName));
                        TaskListDispose.getInstance().reloadFromXML();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void clearTaskFile() {
        TaskListDispose.getInstance().removeAllTask();
        TaskListDispose.getInstance().reloadFromXML();
    }

    public int startTest(Context context) {
        if (TaskListDispose.getInstance().hasEnabledTask()
                && !ApplicationModel.getInstance().isTestJobIsRun()
                && ApplicationModel.getInstance().isCheckPowerSuccess()) {
            ApplicationModel appModel = ApplicationModel.getInstance();
            Intent startTestIntent = new Intent(WalkMessage.ACTION_WALKTOUR_START_TEST);
            startTestIntent.setPackage(context.getPackageName());
            startTestIntent.putExtra(WalkMessage.Outlooptimes, appModel.getOutLooptimes());
            startTestIntent.putExtra(WalkMessage.OutloopInterval, appModel.getOutLoopInterval());
            startTestIntent.putExtra(WalkMessage.OutloopDisconnetNetwork, appModel.isOutLoopDisconnetNetwork());
            startTestIntent.putExtra(WalkMessage.RcuFileLimitType, ConfigRoutine.getInstance().getSplitType());
            startTestIntent.putExtra(WalkMessage.RucFileSizeLimit, ConfigRoutine.getInstance().getFileSize());
            startTestIntent.putExtra(WalkMessage.ISNETSNIFFER, true);//是否抓包
            startTestIntent.putExtra(WalkMessage.ISCQTAUTOMARK, false);//是否自动打点
            startTestIntent.putExtra(WalkMessage.CQT_AUTO_MARK_SELECTED_GLONAVIN_MODULE, "");
            startTestIntent.putExtra(WalkMessage.KEY_TEST_DONTSAVEDATA, false); //是否不保存数据
            startTestIntent.putExtra(WalkMessage.KEY_TESTER, ""); //测试人员
            startTestIntent.putExtra(WalkMessage.KEY_TEST_ADDRESS, ""); //测试地址
            startTestIntent.putExtra(WalkMessage.KEY_TEST_INDOOR, false);
            startTestIntent.putExtra(WalkMessage.KEY_TEST_CQT_CHECK, true);
            startTestIntent.putExtra(WalkMessage.KEY_TEST_BUILDING, appModel.getBuildNodeId());// building
            startTestIntent.putExtra(WalkMessage.KEY_TEST_FLOOR, appModel.getFloorNodeId());// floor
            startTestIntent.putExtra(WalkMessage.KEY_TEST_TAG, "");
            startTestIntent.putExtra(WalkMessage.KEY_FROM_TYPE, TaskModel.FROM_TYPE_SELF); //来源：下载还是自建
            startTestIntent.putExtra(WalkMessage.KEY_FROM_SCENE, WalkStruct.SceneType.Manual);
            context.sendBroadcast(startTestIntent);
            return 0;
        }
        return -10;
    }

    public int stopTest(Context context) {
        if (ApplicationModel.getInstance().isTestJobIsRun()) {
            Intent interruptIntent = new Intent(WalkMessage.Action_Walktour_Test_Interrupt);
            context.sendBroadcast(interruptIntent);
        }
        return -10;
    }

    public boolean isTesting() {
        return ApplicationModel.getInstance().isTestJobIsRun();
    }

    public int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    public void setStartTime(long startTestTime) {
        this.mStartTestTime = startTestTime;
    }

    public void setStopTime(long stopTestTime) {
        this.mStopTestTime = stopTestTime;
    }

    public void setLogFileCount(int count) {
        this.mLogFileCount = count;
    }

    public long getLoggingTime() {
        if (isTesting()) {
            return System.currentTimeMillis() - this.mStartTestTime;
        }
        return this.mStopTestTime - this.mStartTestTime;
    }

    public int getLogFileCount() {
        return mLogFileCount;
    }

    public double getDataSize() {
        return DataSetLib.currentFileLength;
    }

    public String getLogFileDir() {
        return AppFilePathUtil.getInstance().getSDCardBaseDirectory("data", "task");
    }


    public String getLicenseInfo() {
        String info = "";
        try {
            JSONObject jo = new JSONObject();
            jo.put("LicenseDate", ApplicationModel.getInstance().getActiveDate());
            jo.put("RemainDays", ApplicationModel.getInstance().getActiveTime());
            jo.put("isVertify", ApplicationModel.getInstance().isCheckPowerSuccess() ? 1 : 0);
            info = jo.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }
}
