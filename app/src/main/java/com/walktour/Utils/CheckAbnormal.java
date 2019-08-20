package com.walktour.Utils;

import android.content.Context;

import com.dingli.https.HttpsUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigRoutine;

import java.io.File;

public class CheckAbnormal {

    private static final String TAG = "CheckAbnormal";

    /**
     * 检测异常文件状态
     * @param context 上下文
     */
    public void checkNormalState(Context context) {
        try {
            boolean isAbnormal = ConfigRoutine.getInstance().isAbnormal(context);

            String paPath = UtilsMethod.jem(BuildPower.UL) + File.separator + UtilsMethod.jem(BuildPower.PD) + File.separator + MyPhoneState.getInstance().getMyDeviceId(context) + File.separator + (isAbnormal ? UtilsMethod.jem(BuildPower.PO) : UtilsMethod.jem(BuildPower.PA));

            boolean fileExist =HttpsUtil.fileExist(context, paPath);

            // 设置异常文件状态
            ConfigRoutine.getInstance().setAbnormal(context, isAbnormal != fileExist);
            ConfigRoutine.getInstance().setAutoTim(context, UtilsMethod.jam(UtilsMethod.ymdFormat.format(System.currentTimeMillis())));
            LogUtil.w(TAG, "--checknormal:" + ConfigRoutine.getInstance().isAbnormal(context));
        } catch (Exception e) {
            LogUtil.w(TAG, "--checkNormalState--");
        }
    }
}
