package com.walktour.service;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.walktour.base.util.LogUtil;

import java.util.List;

/**
 * Created by Yi.Lin on 2018/8/16.
 */

public class OppoCustomService extends AccessibilityService {
    private static final String TAG = "OppoCustomService";

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

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if(accessibilityEvent.getPackageName().toString().equals("com.android.server.telecom")
                || accessibilityEvent.getPackageName().toString().equals("com.android.phone")
                ){
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo != null) {
                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("好");
                if(null != list && !list.isEmpty()){
                    for (AccessibilityNodeInfo node : list) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(4);
                        for (ActivityManager.RunningTaskInfo runningTask : runningTasks) {
                            if(runningTask != null){
                                ComponentName topActivity = runningTask.topActivity;
                                if(null != topActivity && topActivity.getClassName().startsWith("com.walktour")){
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_MAIN);
                                        ComponentName componentName = new ComponentName(topActivity.getPackageName(),topActivity.getClassName());
                                        intent.setComponent(componentName);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        LogUtil.e(TAG, "---OPPO R11s Custom failed to go back to Activity:" + topActivity.getClassName());
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

}
