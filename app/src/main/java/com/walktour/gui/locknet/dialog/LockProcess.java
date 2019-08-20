package com.walktour.gui.locknet.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.locknet.ForceManager;

import static com.walktour.gui.locknet.dialog.LockActivity.LockEDRXSetting;
import static com.walktour.gui.locknet.dialog.LockActivity.LockPSMSetting;
import static com.walktour.gui.locknet.dialog.LockActivity.LockProAPN;
import static com.walktour.gui.locknet.dialog.LockActivity.LockProScrambleState;
import static com.walktour.gui.locknet.dialog.LockActivity.LockVolteSetting;

/**
 * Created by luojun on 2018/3/19.
 * 进程实现强制功能;
 */

public class LockProcess extends AsyncTask<String, Void, Boolean> {
    private final String TAG = "LockProcess";

    private Context mContext;
    private ProgressDialog mProgressDialog;
    private OnDialogChangeListener mCallBack;

    private int mLockProNum = -1;

    public LockProcess(Context context, int iLockProNum, OnDialogChangeListener callBack) {
        LogUtil.w(TAG, "Enter LockProcess");
        this.mContext = context;
        this.mLockProNum = iLockProNum;
        this.mCallBack = callBack;
    }

    @Override
    protected Boolean doInBackground(String... args) {
        LogUtil.w(TAG, "mLockProNum: " + Integer.toString(mLockProNum));
        switch (mLockProNum) {
            case LockProScrambleState:{
                if (args.length > 0)
                    return setScrambleState(args[0]);
            }
            break;
            case LockProAPN: {
                if (args.length > 0)
                    return setAPN(args[0]);
            }
            break;
            case LockVolteSetting: {
                if (args.length >= 0)
                    return setVolteSetting(args[0]);
            }
            break;
            case LockPSMSetting: {
                if (args.length >= 0)
                    return setPSMSetting(args[0]);
            }
            break;
            case LockEDRXSetting: {
                if (args.length >= 0)
                    return setEDRXSetting(args[0]);
            }
            break;
        }
        return false;
    }

    @Override
    protected void onPreExecute() {
        showProgressDialog(mContext.getString(R.string.exe_info), false);
    }

    @Override
    protected void onPostExecute(Boolean success) {
        dismissProgress();
        if (mCallBack != null) {
            mCallBack.onPositive();
        }
    }

    private void showProgressDialog(String message, boolean cancleable) {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(cancleable);
        mProgressDialog.show();
    }

    private void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private boolean setAPN(String strAPN) {
        ForceManager forceManager = ForceManager.getInstance();
        if (null != forceManager) {
            forceManager.init();
            return forceManager.setAPN(mContext, strAPN);
        }

        return false;
    }

    private boolean setScrambleState(String strScrambleState) {

        ForceManager forceManager = ForceManager.getInstance();
        if (null != forceManager) {
            forceManager.init();
            boolean bFlag = (strScrambleState.equals("Open") == false);
            return forceManager.setScrambleState(mContext, bFlag);
        }

        return  false;
    }

    private boolean setVolteSetting(String strVolteSetting) {
        ForceManager forceManager = ForceManager.getInstance();
        if (null == forceManager)
            return false;

        forceManager.init();
        boolean bFlag = (strVolteSetting.equals("Open") == true);
        return forceManager.setVolteSwitch(mContext, bFlag);
    }

    private boolean setPSMSetting(String strPSMSetting) {
        ForceManager forceManager = ForceManager.getInstance();
        if (null == forceManager)
            return false;

        forceManager.init();
        return forceManager.setPSMState(mContext, strPSMSetting);
    }

    private boolean setEDRXSetting(String strEDRXSetting) {
        LogUtil.w(TAG, "setEDRXSetting");
        ForceManager forceManager = ForceManager.getInstance();
        if (null == forceManager)
            return false;

        forceManager.init();
        return forceManager.setEDRXState(mContext, strEDRXSetting);
    }
}
