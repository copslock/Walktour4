package com.walktour.gui.task;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.dingli.ott.task.service.WalktourAutoService;
import com.dingli.ott.util.OttUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;

/**
 * @author zhicheng.chen
 * @date 2018/11/27
 */
public class OttPermissionReceiver extends BroadcastReceiver {

    private Button mBtnOpenPermission;

    public OttPermissionReceiver(final Activity activity, Button btnPermission) {
        this.mBtnOpenPermission = btnPermission;

        final Class<WalktourAutoService> serviceClass = WalktourAutoService.class;
        final boolean hasPermisson = OttUtil.hasServicePermission(activity, serviceClass);
        mBtnOpenPermission.setText(hasPermisson ? activity.getString(R.string.service_already_open) : activity.getString(R.string.setting_service));
        mBtnOpenPermission.setEnabled(!hasPermisson);

        this.mBtnOpenPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OttUtil.openServicePermissonCompat(activity, serviceClass);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // if is root
                    if (!hasPermisson && OttUtil.isRoot()) {
                        OttUtil.openServicePermissonRoot(activity, serviceClass);
                    }
                    // try again
                    if (!OttUtil.hasServicePermission(activity, serviceClass)) {
                        OttUtil.openServicePermission(activity, serviceClass);
                    }
                } catch (Exception e) {
                    LogUtil.w("OttPermissionReceiver", "open permission wrong~");
                }
            }
        }).start();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WalktourAutoService.ACTION_SERVICE_CONNECTED)) {
            mBtnOpenPermission.setText(R.string.service_already_open);
            mBtnOpenPermission.setEnabled(false);
        } else {
            mBtnOpenPermission.setText(R.string.setting_service);
            mBtnOpenPermission.setEnabled(true);
        }
    }
}
