package com.walktour.gui.setting.sysroutine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.setting.sysroutine.dialog.SysRoutineLockDialog;
/***
 * 强制功能
 *
 * @author Tangwq
 *
 */
public class SysRoutineLockMainActivity extends BasicActivity {
    private ServerManager serManager;
    private final int HANDLE_REBOOT = 1;
    private boolean isFromMultitest = false;
    private Activity context=this;
    private String[] closeCa = new String[]{
            "am broadcast -a android.provider.Telephony.SECRET_CODE -d android_secret_code://27663368378",
            "input tap 1250 600", //
            "input tap 1250 500", //
            "input tap 1250 500", //
            "input tap 1250 500", //
            "input tap 1250 600", //
            "input tap 1250 700", //
            "input tap 1250 500", //
            "input keyevent 4"};
    private String[] openCa = new String[]{
            "am broadcast -a android.provider.Telephony.SECRET_CODE -d android_secret_code://27663368378",
            "input tap 1250 600", //
            "input tap 1250 500", //
            "input tap 1250 500", //
            "input tap 1250 500", //
            "input tap 1250 600", //
            "input tap 1250 700", //
            "input tap 1250 600", //
            "input keyevent 4"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getIntent().getExtras();
        if (null != bundle) {
            isFromMultitest = bundle.getBoolean("isFromMultitest");
        }
        if(isFromMultitest){
            context=this;
        }else{
            context=this.getParent();
        }
        serManager = ServerManager.getInstance(getApplicationContext());
        findView();
    }
    private void setLockCaText() {
        try {
            String[] enableCaItem = getResources().getStringArray(R.array.lock_Enable_CA);
            int caItem = serManager.getLockLTECA();
            if (caItem >= enableCaItem.length) {
                caItem = 0;
            }
            ((TextView) findViewById(R.id.setting_lock_ca_state)).setText(enableCaItem[caItem]);
        } catch (Exception e) {
            LogUtil.w("SysRoutineLockMain", "onResume", e);
        }
    }
    /**
     * 初始化视图
     */
    private void findView() {
        setContentView(R.layout.sys_routine_setting_lockmain);
        if (ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.LockFrequency)) {
            findViewById(R.id.sys_setting_lock_net).setVisibility(View.VISIBLE);
        }
        if (ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.LockCAFrequency)) {
            findViewById(R.id.setting_lock_lteca).setVisibility(View.VISIBLE);
        }
        setLockCaText();
        findViewById(R.id.sys_setting_lock_net).setOnClickListener(this);
        findViewById(R.id.setting_lock_lteca).setOnClickListener(this);
    }
    class DoLockCaCommand implements Runnable {
        @Override
        public void run() {
            // 开关LTE上CA
            String[] commands = serManager.getLockLTECA() == 0 ? openCa : closeCa;
            for (int i = 0; commands != null && i < commands.length; i++) {
                UtilsMethod.runRootCommand(commands[i]);
                if (i == 0) {
                    UtilsMethod.ThreadSleep(1000);
                } else {
                    UtilsMethod.ThreadSleep(200);
                }
            }
            myHandle.obtainMessage(HANDLE_REBOOT).sendToTarget();
        }
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sys_setting_lock_net://锁网
                    new SysRoutineLockDialog(this.context).show();
                break;
            case R.id.setting_lock_lteca://启用CA
                this.showDialog(R.id.setting_lock_lteca);
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isFromMultitest) {
                this.finish();
            } else {
                Intent intent = new Intent(SysRoutineActivity.SHOW_MAIN_TAB);
                this.sendBroadcast(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @SuppressLint("HandlerLeak")
    Handler myHandle = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case HANDLE_REBOOT:
                    showRebootDialog();
                    break;
            }
        }
    };
    @Override
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        BasicDialog.Builder builder= new BasicDialog.Builder(context);
        switch (id) {
            case R.id.setting_lock_lteca:
                builder.setTitle(R.string.lock_lteca).setSingleChoiceItems(R.array.lock_Enable_CA, serManager.getLockLTECA(),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                serManager.setLockLTECA(which);
                                setLockCaText();
                                new Thread(new DoLockCaCommand()).start();
                            }
                        });
                break;
        }
        return builder.create();
    }
    /**
     * 显示重启对话框
     */
    private void showRebootDialog() {
        new BasicDialog.Builder(this.context).setMessage(R.string.alert_lockca_reboot)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // UtilsMethod.rebootMachine(getApplicationContext());
                        // UtilsMethod.runRootCommand("reboot");
                    }
                }).show();
    }
}
