package com.walktour.gui.locknet.dialog;

import android.os.Bundle;

import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicActivity;

/**
 *  Created by luojun on 2018/3/20.
 *  Activity 实现部分强制功能;
 */

public class LockActivity extends BasicActivity implements OnDialogChangeListener {
    private static String TAG = "LockActivity";

    public static String STRLOCKTYPE = "LockType";

    public static String STRSETAPN  = "APN";
    public static final int LockProAPN = 0x08;

    public static String STRSCRAMBLESTATE = "ScrambleState";
    public static final int LockProScrambleState = 0x10;

    public static String STRVOLTESETTING = "Volte Setting";
    public static final int LockVolteSetting = 0x11;

    public static String STRPSMSETTING = "PSM Setting";
    public static final int LockPSMSetting = 0x12;

    public static String STREDRXSETTING = "eDRX Setting";
    public static final int LockEDRXSetting = 0x13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getIntent().getExtras();

        if (null == bundle)
            return ;

        int iLockProNo = bundle.getInt(STRLOCKTYPE);
        LogUtil.w(TAG, "LockType: " + Integer.toString(iLockProNo));
        switch (iLockProNo) {
            case LockProAPN: {
                String strAPN = bundle.getString(STRSETAPN);
                new LockProcess(this, iLockProNo, this).execute(strAPN);
                break;
            }
            case LockProScrambleState: {
                String strScrambelState = bundle.getString(STRSCRAMBLESTATE);
                new LockProcess(this, iLockProNo, this).execute(strScrambelState);
                break;
            }
            case LockVolteSetting : {
                String strVolteSetting = bundle.getString(STRVOLTESETTING);
                new LockProcess(this, iLockProNo, this).execute(strVolteSetting);
                break;
            }
            case LockPSMSetting: {
                String strPSMSetting = bundle.getString(STRPSMSETTING);
                new LockProcess(this, iLockProNo, this).execute(strPSMSetting);
                break;
            }
            case LockEDRXSetting: {
                String strEDRXSetting = bundle.getString(STREDRXSETTING);
                new LockProcess(this, iLockProNo, this).execute(strEDRXSetting);
                break;
            }
        }
    }

    @Override
    public void onPositive() {
        super.onBackPressed();
    }

    @Override
    public void onLockPositive(String lockType) {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
