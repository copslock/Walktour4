package com.walktour.gui.setting.sysroutine;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicDialog.Builder;
import com.walktour.gui.R;
import com.walktour.gui.applet.ExplorerDirectory;
import com.walktour.gui.data.model.DataModel;
import com.walktour.gui.setting.sysroutine.dialog.SysRoutineDataAutoClearDialog;
import com.walktour.gui.setting.sysroutine.dialog.SysRoutineDataFormatDialog;
import com.walktour.gui.setting.sysroutine.dialog.SysRoutineDataPartitionDialog;

/**
 * 系统常规设置中的数据采集、存储和上传
 *
 * @author jianchao.wang
 */
public class SysRoutineDataActivity extends BasicActivity implements OnClickListener {
    private final String TAG = "SysRoutineDataActivity";
    /**
     * 常规设置配置文件
     */
    private ConfigRoutine configRoutine;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 操作目录
     */
    private final String ACTION_DIR = "com.walktour.SysSettingRoutine.choosePath";
    /**
     * 变量名称
     */
    private final String EXTRA_DIR = "dir";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.w(TAG, "--onCreate--");
        setContentView(R.layout.sys_routine_setting_data);
        configRoutine = ConfigRoutine.getInstance();
        this.mContext = this.getParent();
        findView();
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DIR);
        this.mContext.registerReceiver(mEventReceiver, filter);
    }
    private void findView() {
        //this.findViewById(R.id.sys_setting_data_format).setVisibility(View.GONE);
        this.findViewById(R.id.sys_setting_data_format).setOnClickListener(this);
        this.findViewById(R.id.sys_setting_data_upload).setOnClickListener(this);
        this.findViewById(R.id.sys_setting_data_filter).setOnClickListener(this);
        this.findViewById(R.id.sys_setting_data_partition).setOnClickListener(this);
        this.findViewById(R.id.sys_setting_data_storage).setOnClickListener(this);
        this.findViewById(R.id.sys_setting_data_auto_clear).setOnClickListener(this);
        this.findViewById(R.id.sys_setting_data_tcpip).setOnClickListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sys_setting_data_upload:
                Intent intent = new Intent(SysRoutineActivity.SHOW_DATA_UPLOAD_TAB);
                sendBroadcast(intent);
                break;
            case R.id.sys_setting_data_storage:
                this.createSavePathDialog();
                break;
            case R.id.sys_setting_data_filter:
                intent = new Intent(SysRoutineActivity.SHOW_DATA_FILTER_TAB);
                sendBroadcast(intent);
                break;
            case R.id.sys_setting_data_partition:
                showDialog(R.id.sys_setting_data_partition);
                break;
            case R.id.sys_setting_data_auto_clear:
                showDialog(R.id.sys_setting_data_auto_clear);
                break;
            case R.id.sys_setting_data_format:
                showDialog(R.id.sys_setting_data_format);
                break;
            case R.id.sys_setting_data_tcpip:
                showDialog(R.id.sys_setting_data_tcpip);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(SysRoutineActivity.SHOW_MAIN_TAB);
            this.sendBroadcast(intent);
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        BasicDialog.Builder builder = new BasicDialog.Builder(this.getParent());
        switch (id) {
            case R.id.sys_setting_data_partition:
                new SysRoutineDataPartitionDialog(this.getParent(), builder, configRoutine);
                break;
            case R.id.sys_setting_data_auto_clear:
                new SysRoutineDataAutoClearDialog(this.getParent(), builder, configRoutine);
                break;
            case R.id.sys_setting_data_format:
                new SysRoutineDataFormatDialog(this.getParent(), builder, configRoutine);
                break;
            case R.id.sys_setting_data_tcpip:
                createTcpIpCollectDialog(builder);
                break;
        }
        return builder.create();
    }

    /**
     * 广播接收器:接收所有操作结果以更新进度框
     */
    private BroadcastReceiver mEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_DIR)) {
                String path = intent.getExtras().getString(EXTRA_DIR);
                LogUtil.i(TAG, "change storge path to " + path);
                configRoutine.setStorgePath(path);
            }
        }
    };

    /**
     * 生成存放路径对话框
     */
    private void createSavePathDialog() {
        BasicDialog.Builder builder = new BasicDialog.Builder(this.getParent());
        String[] items;
        // 如果没有存储卡
        final boolean sdcard_exist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdcard_exist) {
            items = new String[]{this.mContext.getString(R.string.phone), this.mContext.getString(R.string.sdcard),
                    this.mContext.getString(R.string.view)};
        } else {
            items = new String[]{this.mContext.getString(R.string.phone), this.mContext.getString(R.string.sdcard_non)};
        }
        int i;
        if (configRoutine.getStorgePath().equals(AppFilePathUtil.getInstance().getAppFilesDirectory())) {
            i = 0;
        } else if (configRoutine.getStorgePath().equals(AppFilePathUtil.getInstance().getSDCardBaseDirectory())) {
            i = 1;
        } else {
            i = 2;
            items[2] = items[2] + "(" + configRoutine.getStorgePath() + ")";
        }
        final int checkItem = i;
        builder.setTitle(this.mContext.getResources().getStringArray(R.array.sys_setting_routine)[2]).setSingleChoiceItems(items,
                checkItem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                configRoutine.setStorgePath(AppFilePathUtil.getInstance().getAppFilesDirectory());
                                dialog.dismiss();
                                break;
                            case 1:
                                if (sdcard_exist) {
                                    configRoutine.setStorgePath(AppFilePathUtil.getInstance().getSDCardBaseDirectory());
                                    dialog.dismiss();
                                    break;
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.sdcard_non), Toast.LENGTH_LONG).show();
                                }
                                break;
                            case 2:
                                if (sdcard_exist) {
                                    dialog.dismiss();
                                    // 启动文件浏览器
                                    new ExplorerDirectory(mContext,
                                            DataModel.FILE_INCONTROL, ACTION_DIR, EXTRA_DIR).start();
                                    break;
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.sdcard_non), Toast.LENGTH_LONG).show();
                                }
                                break;
                        }
                    }
                });
        builder.show();
    }

    /**
     * 生成TCP/IP采集设置对话框
     */
    private void createTcpIpCollectDialog(Builder builder) {
        String[] names = this.getParent().getResources().getStringArray(R.array.tcp_ip_collect_name_array);
        final int[] values = this.getParent().getResources().getIntArray(R.array.tcp_ip_collect_value_array);
        int position = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == configRoutine.getTcpIpCollect(this.getParent())) {
                position = i;
                break;
            }
        }
        builder.setTitle(R.string.sys_setting_tcp_ip_collect).setSingleChoiceItems(names,
                position, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        configRoutine.setTcpIpCollect(getParent(), values[which]);
                        dialog.dismiss();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        this.mContext.unregisterReceiver(mEventReceiver);
        super.onDestroy();
    }

}
