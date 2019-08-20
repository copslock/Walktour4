package com.walktour.service;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.dingli.ott.util.NodeUtil;
import com.walktour.Utils.ApplicationModel;
import com.walktour.base.util.LogUtil;

import java.util.List;

/**
 * Created by jinfeng.xie on 2019/1/8.
 */

public class XiaoMi8CustomService extends AccessibilityService {
    private static final String TAG = "XiaoMi8CustomService";

    @Override
    public void onCreate() {
        Log.i(TAG, "----onCreate----");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "----onStartCommand----");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "----onDestroy----");
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (!ApplicationModel.getInstance().isTestJobIsRun()){
            return;
        }
        Log.e(TAG,"accessibilityEvent.getClassName()"+accessibilityEvent.getClassName());
        if(accessibilityEvent.getClassName().toString().equals("com.android.contacts.activities.PeopleActivity")){
            Log.e(TAG,"isPeopleActivity");
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo != null) {
                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("通话记录详情");
                AccessibilityNodeInfo clickDetal=NodeUtil.findNodeByText(getRootInActiveWindow(),"联系人详情");
                if (clickDetal!=null){
                    NodeUtil.performClick(clickDetal);
                }
                Log.e(TAG,"list."+list.size());
                if(null != list && !list.isEmpty()){
                    NodeUtil.performClick(list.get(0));
                }else {
                    AccessibilityNodeInfo click=NodeUtil.findNodeByText(getRootInActiveWindow(),"发起VoLTE视频通话");
                    if (click!=null){
                        NodeUtil.performClick(click);
                    }
                }
            }
        }
        if (accessibilityEvent.getClassName().toString().equals("com.android.contacts.activities.UnknownContactActivity")||
                accessibilityEvent.getClassName().toString().equals("com.android.contacts.activities.ContactDetailActivity")){
            AccessibilityNodeInfo click=NodeUtil.findNodeByText(getRootInActiveWindow(),"VoLTE视频通话");
            if (click!=null){
                NodeUtil.performClick(click);
            }

        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onInterrupt() {

    }

}
