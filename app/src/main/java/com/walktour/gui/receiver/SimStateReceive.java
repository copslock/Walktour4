package com.walktour.gui.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.dialog.BasicDialogActivity;
import com.walktour.framework.ui.dialog.SIMStausDialogActivity;
import com.walktour.gui.R;

/**
 * @author Max
 * @data 2018/12/20
 */
/*
 * 监听sim状态改变的广播，返回sim卡的状态， 有效或者无效。
 * 双卡中只要有一张卡的状态有效即返回状态为有效，两张卡都无效则返回无效。
 */
public class SimStateReceive extends BroadcastReceiver {
    private static final String TAG = "SimStateReceive";
    private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    private final static int SIM_VALID = 0;//卡有效
    private final static int SIM_INVALID = 1;//卡无效
    private int simState = SIM_INVALID;
    private String SimInfo="";
    public int getSimState() {
        return simState;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG,"SIM卡状态更换");
        if (intent.getAction().equals(ACTION_SIM_STATE_CHANGED)) {
            Log.d(TAG,"###############start");
            setSimInfoByChange(context,intent);
        }
    }

    private void setSimInfoByChange(Context context, Intent intent){
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
        int state = tm.getSimState();
        switch (state) {
            case TelephonyManager.SIM_STATE_READY :
                simState = SIM_VALID;
                SimInfo="SIM卡有效";
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN :
                SimInfo="未知SIM卡";
                simState = SIM_INVALID;
                break;
            case TelephonyManager.SIM_STATE_ABSENT ://卡已经拔出
                SimInfo="SIM卡已拔出";
                simState = SIM_INVALID;
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED :
                SimInfo="SIM_STATE_PIN_REQUIRED";
                simState = SIM_INVALID;
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED :
                SimInfo="SIM_STATE_PUK_REQUIRED";
                simState = SIM_INVALID;
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED :
                SimInfo="SIM_STATE_NETWORK_LOCKED";
                simState = SIM_INVALID;
                break;
            default:
                SimInfo="UNKNOWN";
                simState = SIM_INVALID;
                break;
        }
//        Intent dialogIntent=new Intent(context, BasicDialogActivity.class);
//        dialogIntent.putExtra("title",context.getString(R.string.str_tip));
//        dialogIntent.putExtra("message",SimInfo);
//        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(dialogIntent);
//        Log.d(TAG, "+++++SimInfo:"+SimInfo);
//        Log.d(TAG, "+++++simState:"+simState);
    }

}
