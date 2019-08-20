package com.walktour.gui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.setting.sysroutine.SysRoutineNBModuleActivity;

/**
 * NB上电未成功告警确认对话框
 */
public class NbPowerOnDialog extends BasicActivity {
    private String tag = "NbPowerOnDialog";
    private Context mContext = null;
    private BasicDialog dialog = null;
    private boolean hasFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        addAlarmDialog(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        addAlarmDialog(intent);
    }

    @Override
    public void onDestroy() {
        Log.e(tag, "---onDestroy");
        if (dialog != null) {
            dialog.cancel();
        }
        hasFinish = true;
        super.onDestroy();
    }

    private void closeActicity() {
        hasFinish = true;
        finish();
    }

    private void addAlarmDialog(Intent intent) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new BasicDialog.Builder(this)
                .setTitle(R.string.sys_tab_alert)
                .setMessage(R.string.noiot_error_info)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        closeActicity();

                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(NbPowerOnDialog.this, SysRoutineNBModuleActivity.class);
                        startActivity(intent);
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        closeActicity();
                    }
                }).create();

        //如果Activity未被前一个窗口关闭时才显示
        if (!hasFinish) {
            dialog.show();
        }
    }

}
