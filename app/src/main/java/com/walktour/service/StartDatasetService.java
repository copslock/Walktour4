package com.walktour.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.dinglicom.DiagSoLib;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigRoutine;

/**
 * 独立进程开启串口
 */
public class StartDatasetService extends Service {
    /**
     * 日志标识
     */
    private static final String TAG = "StartDatasetService";

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.w(TAG, "----onCreate----");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent)
            return super.onStartCommand(intent, flags, startId);

        LogUtil.w(TAG, "----onStartCommand----");
        int iDevDiagType = intent.getIntExtra("devdiagtype", -1);
        LogUtil.w(TAG, "----onStartCommand----devdiagtype" + Integer.toString(iDevDiagType));
        if (0 == iDevDiagType) {
            DiagSoLib.startDLDiag(AppFilePathUtil.getInstance().getAppLibFile("libDevDiag.so").getAbsolutePath(),
                    ConfigRoutine.getInstance().getAuthValueStr(this), MyPhoneState.getInstance().getDeviceId(this));
        } else if (1 == iDevDiagType){
            DiagSoLib.startDLDiag(AppFilePathUtil.getInstance().getAppLibFile("libDevDiag_s7.so").getAbsolutePath(),
                    ConfigRoutine.getInstance().getAuthValueStr(this), MyPhoneState.getInstance().getDeviceId(this));
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        LogUtil.w(TAG, "----onDestroy----");
        DiagSoLib.stopDLDiag();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
