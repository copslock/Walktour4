package com.walktour.gui.setting.sysroutine;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.walktour.Utils.APNOperate;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkStruct;
import com.walktour.control.bean.Verify;
import com.walktour.control.config.ConfigAPN;
import com.walktour.control.config.ConfigNBModuleInfo;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog.Builder;
import com.walktour.gui.R;
import com.walktour.gui.setting.Sys;
import com.walktour.gui.setting.sysroutine.dialog.SysRoutineAPNDialog;
import com.walktour.gui.setting.sysroutine.dialog.SysRoutineBluetoothDialog;
import com.walktour.gui.setting.sysroutine.dialog.SysRoutineCSFBDialog;
import com.walktour.gui.setting.sysroutine.dialog.SysRoutineNogoDialog;
import com.walktour.gui.setting.sysroutine.dialog.SysRoutineTaskGroupDialog;

/**
 * 系统常规设置主页面
 *
 * @author jianchao.wang
 */
@SuppressLint("InflateParams")
public class SysRoutineMainActivity extends BasicActivity implements OnClickListener {
    /**
     * APN配置对象
     */
    private ConfigAPN configAPN;
    /**
     * 服务器管理类
     */
    private ServerManager mServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sys_routine_setting_main);
        configAPN = ConfigAPN.getInstance();
        mServer = ServerManager.getInstance(this);
        findView();
    }

    public void findView() {
        if (Deviceinfo.getInstance().getApnList()) {
            this.findViewById(R.id.sys_setting_apn).setVisibility(View.VISIBLE);
        }
        this.findViewById(R.id.sys_setting_devicetag).setOnClickListener(this);
        this.findViewById(R.id.sys_setting_apn).setOnClickListener(this);
        this.findViewById(R.id.sys_setting_data).setOnClickListener(this);
        this.findViewById(R.id.sys_setting_nogo).setOnClickListener(this);
        View lock = this.findViewById(R.id.sys_setting_lock);
        lock.setOnClickListener(this);
        if (ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.LockFrequency) || ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.LockCAFrequency)) {
            lock.setVisibility(View.VISIBLE);
        }
        this.findViewById(R.id.sys_setting_custom_window).setOnClickListener(this);
        if (ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.OTS)) {
            ((LinearLayout) this.findViewById(R.id.sys_setting_ots)).setVisibility(View.VISIBLE);
            this.findViewById(R.id.sys_setting_ots).setOnClickListener(this);
        }
        View csfb = this.findViewById(R.id.sys_setting_csfb);
        csfb.setOnClickListener(this);
        // 判断是否有权限显示CSFB异常用分析项
        if (!ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.CSFBAnalysis)) {
            csfb.setVisibility(View.GONE);
            mServer.setCsfbAnalysis(false);
        }
        this.findViewById(R.id.sys_setting_advenced).setOnClickListener(this);
        View bluetooth = this.findViewById(R.id.sys_setting_bluetooth);
        bluetooth.setOnClickListener(this);
        if (ApplicationModel.getInstance().getNetList().contains(WalkStruct.ShowInfoType.BluetoothSync)) {
            bluetooth.setVisibility(View.VISIBLE);
        }
        this.findViewById(R.id.sys_setting_other_app_network_permissions).setOnClickListener(this);
        this.findViewById(R.id.sys_setting_platform_interactive_permissions).setOnClickListener(this);
        this.findViewById(R.id.sys_setting_taskgroup).setOnClickListener(this);

        LinearLayout linearLayoutNBModule=(LinearLayout)this.findViewById(R.id.sys_setting_nbmodule);
        if(ApplicationModel.getInstance().isNBTest()){
            linearLayoutNBModule.setVisibility(View.VISIBLE);
        }else{
            linearLayoutNBModule.setVisibility(View.GONE);
        }
        linearLayoutNBModule.setOnClickListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sys_setting_devicetag:
                this.showDialog(R.id.sys_setting_devicetag);
                break;
            case R.id.sys_setting_apn:
                this.showDialog(R.id.sys_setting_apn);
                break;
            case R.id.sys_setting_data:
                Intent intent = new Intent(SysRoutineActivity.SHOW_DATA_TAB);
                sendBroadcast(intent);
                break;
            case R.id.sys_setting_nogo:
                this.showDialog(R.id.sys_setting_nogo);
                break;
            case R.id.sys_setting_lock:
                intent = new Intent(SysRoutineActivity.SHOW_LOCK_MAIN_TAB);
                sendBroadcast(intent);
                break;
            case R.id.sys_setting_custom_window:
                intent = new Intent(SysRoutineActivity.SHOW_CUSTOM_WINDOW_TAB);
                sendBroadcast(intent);
                break;
            case R.id.sys_setting_ots:
                intent = new Intent(SysRoutineActivity.SHOW_OTS_TAB);
                sendBroadcast(intent);
                break;
            case R.id.sys_setting_csfb:
                this.showDialog(R.id.sys_setting_csfb);
                break;
            case R.id.sys_setting_bluetooth:
                this.showDialog(R.id.sys_setting_bluetooth);
                break;
            case R.id.sys_setting_taskgroup:
                this.showDialog(R.id.sys_setting_taskgroup);
                break;
            case R.id.sys_setting_advenced:
                intent = new Intent(SysRoutineActivity.SHOW_ADVENCED_TAB);
                sendBroadcast(intent);
                break;
            case R.id.sys_setting_other_app_network_permissions:
                intent = new Intent(SysRoutineActivity.SHOW_NETWORK_CONTROL_TAB);
                sendBroadcast(intent);
                Sys.isExecuteNetControlSetting = true;
                break;
            case R.id.sys_setting_platform_interactive_permissions:
                intent = new Intent(SysRoutineActivity.SHOW_PLATFORM_INTERACTIVE_TAB);
                sendBroadcast(intent);
                break;
            case R.id.sys_setting_nbmodule:
                intent = new Intent(SysRoutineActivity.SHOW_NBMODULE_TAB);
                sendBroadcast(intent);
                break;

        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        APNOperate apnOperate = APNOperate.getInstance(this.getParent());
        Builder builder = new Builder(this.getParent());
        switch (id) {
            case R.id.sys_setting_devicetag:
                this.createDeviceTagDialog(builder);
                break;
            case R.id.sys_setting_apn:
                new SysRoutineAPNDialog(this.getParent(), builder, this.configAPN, apnOperate);
                break;
            case R.id.sys_setting_nogo:
                new SysRoutineNogoDialog(this.getParent(), builder);
                break;
            case R.id.sys_setting_csfb:
                new SysRoutineCSFBDialog(this.getParent(), builder, this.mServer);
                break;
            case R.id.sys_setting_bluetooth:
                new SysRoutineBluetoothDialog(this.getParent(), builder);
                break;
            case R.id.sys_setting_taskgroup:
                new SysRoutineTaskGroupDialog(this.getParent(), builder);
                break;

        }
        return builder.create();
    }

    /**
     * 生成设备标识对话框
     *
     * @param builder 建设类
     */
    private void createDeviceTagDialog(Builder builder) {
        LayoutInflater factory = LayoutInflater.from(this.getParent());
        final View view = factory.inflate(R.layout.alert_dialog_edittext, null);
        EditText alert_EditText = (EditText) view.findViewById(R.id.alert_textEditText);
        final ConfigRoutine configRoutine = ConfigRoutine.getInstance();
        alert_EditText.setText(configRoutine.getDeviceTag());
        alert_EditText.setSelectAllOnFocus(true);
        builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.sys_setting_devicetag).setView(view).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText alert_EditText = (EditText) view.findViewById(R.id.alert_textEditText);
                String tag = alert_EditText.getText().toString().trim();
                if (tag.length() == 0 || Verify.checkChar(tag)) {
                    configRoutine.setDeviceTag(tag);
                } else {
                    alert_EditText.setText(configRoutine.getDeviceTag());
                    Toast.makeText(getParent(), getString(R.string.monitor_inputPosition), Toast.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton(R.string.str_cancle);

    }
}
