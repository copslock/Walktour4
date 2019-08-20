package com.walktour.gui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dingli.https.HttpsUtil;
import com.walktour.Utils.APNOperate;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.BuildPower;
import com.walktour.Utils.CheckAbnormal;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TypeConver;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.LocalInfoCheck;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.framework.ui.BasicActivity;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 通过网络获取文件
 *
 * @author tangwq
 */
public class GetPowByNet extends BasicActivity {
    private static final String tag = "GetPowByNet";
    private static TextView tipTextView;
    private final int diaglogShow = 1; // 显示进度消息
    private final int diaglogDismiss = 2; // 关闭进度消息
    private final int diagDelayDismiss = 3; // 延时关闭进度消息
    private final int activityFinisy = 4; // 结束Activity
    private final int ftpGetTimeOut = 30; // 权限文件下载超时时间
    private boolean downloadFinish = false; // 下载线程结束
    private boolean checkingDownload = false; // 监控线运行中
    private Dialog progressDialog; // 进度框

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.w(tag, "--onCreate--");
        showProgressDialog(getString(R.string.main_menu_license_netloading), false);
        new doCheckPower().start(); // 启动下载线程
        if (!checkingDownload) {
            checkingDownload = true;
            new checkDownloadTimeout().start(); // 启动超时检测线程
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.w(tag, "---onNewIntent---");
        showProgressDialog(getString(R.string.main_menu_license_netloading), false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.w(tag, "---onStart---");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.w(tag, "---onDestroy---");
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                case diaglogShow:
                    showProgressDialog(msg.obj.toString(), false);
                    break;
                case diaglogDismiss:
                    dismissProgressDialog();
                    break;
                case diagDelayDismiss:
                    showProgressDialog(msg.obj.toString(), false);
                    new delyDismissDialog().start();
                    break;
                case activityFinisy:
                    finish();
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 延时关闭进度显示框
     */
    class delyDismissDialog extends Thread {
        public void run() {
            try {
                Thread.sleep(1000 * 3);
                mHandler.obtainMessage(activityFinisy).sendToTarget();
                mHandler.obtainMessage(diaglogDismiss).sendToTarget();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示网络更新进度
     */
    private void showProgressDialog(String message, boolean cancleable) {
        if (progressDialog == null) {
            progressDialog = createLoadingDialog(this);
        }
        progressDialog.setCancelable(cancleable);
        tipTextView.setText(message);// 设置加载信息
        progressDialog.show();
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @return
     */
    public Dialog createLoadingDialog(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.progress_custom, null);// 得到加载view
        // main.xml中的ImageView
        tipTextView = (TextView) v.findViewById(R.id.base_progress_showtxt);// 提示文字
        // 加载动画
        // 使用ImageView显示动画
        progressDialog = new Dialog(context, R.style.custom_transparent_dialog);// 创建自定义样式dialog

        progressDialog.setContentView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        return progressDialog;

    }

    /**
     * 关闭更新进度显示
     */
    private void dismissProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            // progressDialog = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 检测下载超时维护线程
     * @author tangwq
     *
     */
    class checkDownloadTimeout extends Thread {
        public void run() {
            try {
                for (int i = 0; i < ftpGetTimeOut && !downloadFinish; i++) {
                    Thread.sleep(1000);
                    LogUtil.w(tag, "---download waitting:" + i);
                }
                // 如果当前下载线程未结束，则强制中断FTP客户端
                if (!downloadFinish) {
                    LogUtil.w(tag, "---ftp download timeout---");
                    mHandler.obtainMessage(diagDelayDismiss, getString(R.string.main_menu_license_permissionFaild)).sendToTarget();
                }
                LogUtil.w(tag, "---download timeout end---");
            } catch (Exception e) {
                e.printStackTrace();
            }
            checkingDownload = false;
        }
    }

    /**
     * 处理下载监权文件线程
     *
     * @author tangwq
     */
    class doCheckPower extends Thread {
        public void run() {
            try {
                // 下载之前检测当前网络是否有效，如无效提示
                if (!MyPhoneState.getInstance().isNetworkAvirable(getApplicationContext())) {
                    APNOperate.getInstance(getApplicationContext()).setMobileDataEnabled(true, "", true, 1000 * 15);

                    if (!MyPhoneState.getInstance().isNetworkAvirable(getApplicationContext())) {
                        mHandler.obtainMessage(diagDelayDismiss, getString(R.string.main_menu_license_netisoff)).sendToTarget();
                        downloadFinish = true;
                        return;
                    }
                }

                String imei = MyPhoneState.getInstance().getMyDeviceId(getApplicationContext());
                String lImei = TypeConver.dIuVlic53R(imei);
                LocalInfoCheck localInfo = new LocalInfoCheck(getApplicationContext(), lImei, imei);

                if (lImei.equals("ErrorFlagFile")) {
                    mHandler.obtainMessage(diagDelayDismiss, getString(R.string.main_menu_local_info_imeifaild)).sendToTarget();
                    downloadFinish = true;
                    return;
                }
                if (!localInfo.checkLocalInfo()) {
                    mHandler.obtainMessage(diagDelayDismiss, getString(R.string.main_menu_local_info_checkfaild)).sendToTarget();
                    downloadFinish = true;
                    return;
                }

                downloadFinish = false;

                // String deviceId = MyPhoneState.getInstance().getDeviceId( GetPowByNet.this );
                // 修改获取手机标识为使用自定义方法，CDMA下取蓝牙加密16位，其它取IMEI
                String deviceId = MyPhoneState.getInstance().getMyDeviceId(GetPowByNet.this);
                String urlPath = UtilsMethod.jem(BuildPower.UL) + File.separator + UtilsMethod.jem(BuildPower.PD) + File.separator + deviceId + UtilsMethod.jem(BuildPower.PL);

                String authPaht = UtilsMethod.jem(BuildPower.UL) + File.separator + UtilsMethod.jem(BuildPower.PD) + File.separator + deviceId + File.separator + UtilsMethod.jem(BuildPower.PX);

                String powerStr = HttpsUtil.getSSLResult(GetPowByNet.this, urlPath);
                String authStr = HttpsUtil.getSSLResult(GetPowByNet.this, authPaht);
                new CheckAbnormal().checkNormalState(getApplicationContext());

                if (!StringUtil.isEmpty(powerStr)) {
                    ApplicationModel.getInstance().setPowerByNet(true);
                    ConfigRoutine.getInstance().setContext(getApplicationContext());

                    ConfigRoutine.getInstance().setAutoValue(getApplicationContext(), powerStr);
                    ConfigRoutine.getInstance().setAuthValueStr(getApplicationContext(), authStr);
                    ConfigRoutine.getInstance().setGoOnType(getApplicationContext());
                    ConfigRoutine.getInstance().setLineCheckLi(getApplicationContext());
                    mHandler.obtainMessage(diagDelayDismiss, getString(R.string.main_menu_license_downloadSuccess)).sendToTarget();
                } else {
                    mHandler.obtainMessage(diagDelayDismiss, getString(R.string.main_menu_license_downloadFaild)).sendToTarget();
                }

                LogUtil.w(tag, "--download file finish--");
                downloadFinish = true;
            } catch (FileNotFoundException e) {
                // e.printStackTrace();
                LogUtil.w(tag, "--Remote_File_Noexist--");
                downloadFinish = true;
                mHandler.obtainMessage(diagDelayDismiss, getString(R.string.main_menu_license_downloadFaild)).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
                downloadFinish = true;
                mHandler.obtainMessage(diagDelayDismiss, getString(R.string.main_menu_license_permissionFaild)).sendToTarget();
            }
        }
    }
}
