package com.walktour.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;

public class MmsReceiver extends BroadcastReceiver {
	private final static String tag = "MmsReceiver"; 
    @Override
    public void onReceive(Context context, Intent intent) {
    	LogUtil.w(tag, "---receive mms:"+intent.getAction());
    	context.sendBroadcast( new Intent(WalkMessage.ACTION_MMS_PUSH_RECEIVE) );

    }
}