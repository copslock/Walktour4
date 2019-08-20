package com.walktour.gui.fleet;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.walktour.Utils.APNOperate;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ServerMessage;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.bean.Verify;
import com.walktour.control.config.ConfigAPN;
import com.walktour.control.config.ConfigAutoTest;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.service.app.AutoTestService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("InflateParams")
public class FleetSetting extends BasicActivity implements CompoundButton.OnCheckedChangeListener {

    public static final String tag = "FleetSetting";
    @BindView(R.id.cb_auto_test)
    CheckBox checkTest;
    @BindView(R.id.tv_server_ip)
    EditText editIp;
    @BindView(R.id.tv_port)
    EditText editPort;
    @BindView(R.id.tv_apn)
    EditText apnInternet;
    @BindView(R.id.tv_wap)
    EditText apnWap;
    @BindView(R.id.btn_start)
    Button btnStart;

    //view

    private ConfigAutoTest config = new ConfigAutoTest();
    private ConfigAPN configApn = ConfigAPN.getInstance();

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fleet);
        ButterKnife.bind(this);
        findView();
        regedit();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mEventReceiver);//反注册广播接收器
        super.onDestroy();
    }

    @SuppressWarnings("deprecation")
    private void findView() {
        config = new ConfigAutoTest();
        configApn = ConfigAPN.getInstance();
        checkTest.setChecked(config.isAutoTestOn());
        checkTest.setOnCheckedChangeListener(this);
        editIp.setText(config.getIp());
        editPort.setText(String.valueOf(config.getPort()));
        //2012.12.13 //暂时先屏蔽定位功能
        config.setLocationOn(false);
//		checkLocation.setChecked( config.isLocationOn() );
//		startTime.setSummary( config.getLocationStartTime() );
//		endTime.setSummary(  config.getLocationEndTime() );
        apnInternet.setText(configApn.getDataAPN());
        apnWap.setText(configApn.getWapAPN());
    }

    private boolean checkText() {
        String value = editIp.getText().toString();
        if (!Verify.isIp(value)) {
            Toast.makeText(FleetSetting.this.getApplicationContext(),
                    FleetSetting.this.getString(R.string.sys_ftp_alert_nullIP),
                    Toast.LENGTH_LONG).show();
            return false;
        } else {
            config.setIp(value);
        }
        value = editPort.getText().toString();
        if (!Verify.isPort(value)) {
            Toast.makeText(FleetSetting.this.getApplicationContext(),
                    FleetSetting.this.getString(R.string.sys_ftp_alert_nullPort),
                    Toast.LENGTH_LONG).show();
            return false;
        } else {
            config.setPort(value);
        }
        return true;
    }


    //注册广播接收器
    private void regedit() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.ACTION_FLEET_RECEIVE_SMS);
        this.registerReceiver(mEventReceiver, filter);
    }

    /**
     * 广播接收器:接收所有操作结果以更新进度框
     */
    private BroadcastReceiver mEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //收到更新测试计划短信
            if (intent.getAction().equals(WalkMessage.ACTION_FLEET_RECEIVE_SMS)) {
                findView();
            }
        }

    };//end inner class EventBroadcastReceiver

    @OnClick(R.id.btn_start)
    public void onViewClicked() {
        if (!checkText()) {
            return;
        }
        //先判断ip和port
        ConfigAutoTest auto = new ConfigAutoTest();
        String ip = auto.getIp();
        int port = 0;
        try {
            port = Integer.parseInt(auto.getPort());
        } catch (Exception e) {
            e.printStackTrace();
            port = 0;
        }
        if (ip.trim().length() == 0 || port == 0) {
            Toast.makeText(FleetSetting.this, getString(R.string.fleet_set_notset_notify),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        //先判断网络是否可用
        if (!MyPhoneState.getInstance().isNetworkAvirable(FleetSetting.this)) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.fleet_netIsOff),
                    Toast.LENGTH_LONG).show();
        } else {
            //发送下载测试计划广播
            sendBroadcast(new Intent(AutoTestService.ACTION_DOWNLOAD_MANUALY));

            //跳到事件页面
            sendBroadcast(new Intent(ServerMessage.ACTION_FLEET_SWITCH));
        }
    }

    @OnClick({R.id.tv_wap, R.id.tv_apn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_wap:
                wapDialog();
                break;
            case R.id.tv_apn:
                apnDialog();
                break;
        }
    }

    private void apnDialog() {
        APNOperate apnOperate = APNOperate.getInstance(FleetSetting.this);
        final String[] apn_names = apnOperate.getAPNNameListByFirstEmpty(FleetSetting.this);
        new BasicDialog.Builder(FleetSetting.this)
                .setTitle(R.string.sys_setting_internt)
                .setSingleChoiceItems(
                        apn_names,
                        apnOperate.getPositonFirstEmpty(configApn.getDataAPN()),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                configApn.setDataAPN(apn_names[which]);
                                apnInternet.setText(apn_names[which]);
                                dialog.dismiss();
                            }
                        }
                ).show();
    }

    private void wapDialog() {
        APNOperate apnOperate = APNOperate.getInstance(FleetSetting.this);
        final String[] apn_names = apnOperate.getAPNNameListByFirstEmpty(FleetSetting.this);
        new BasicDialog.Builder(FleetSetting.this)
                .setTitle(R.string.sys_setting_internt_wapAPN)
                .setSingleChoiceItems(
                        apn_names,
                        apnOperate.getPositonFirstEmpty(configApn.getDataAPN()),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                configApn.setWapAPN(apn_names[which]);
                                apnWap.setText(apn_names[which]);
                                dialog.dismiss();
                            }
                        }
                ).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
        switch (compoundButton.getId()) {
            case R.id.cb_auto_test:
                LogUtil.w(tag, "fleet_autotest:" + b);

                //打开自动测试时，如果当前正在进行业务测试,并且不是自动测试，则停止测试
                if ((Boolean) b
                        && ApplicationModel.getInstance().isTestJobIsRun()
                        && !ApplicationModel.getInstance().isTestStoping()
                        && !ApplicationModel.getInstance().isTesting()) {
                    new BasicDialog.Builder(FleetSetting.this)
                            .setTitle(R.string.str_tip)
                            .setMessage(R.string.fleet_testing_alert)
                            .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    config.setAutoOpen(b);
                                    config = new ConfigAutoTest();

                                    LogUtil.w(tag, "---stop test");
                                    sendBroadcast(new Intent(WalkMessage.Action_Walktour_Test_Interrupt));

                                    if (!b) {
                                        sendBroadcast(new Intent(ServerMessage.ACTION_FLEET_DOWNLOAD_STOP));
                                    }
                                    sendBroadcast(new Intent(AutoTestService.ACTION_FLEET_TRIGGLE_AUTOTEST));
                                }
                            }).setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                checkTest.setChecked(false);
                            }
                            return true;
                        }
                    })
                            /*.setOnCancelListener( new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    checkTest.setChecked( false );
                                }
                            })*/
                            .setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    checkTest.setChecked(false);
                                }
                            }).show();
                } else {
                    config.setAutoOpen(b);
                    config = new ConfigAutoTest();

                    if (!b) {
                        sendBroadcast(new Intent(ServerMessage.ACTION_FLEET_DOWNLOAD_STOP));
                    }
                    sendBroadcast(new Intent(AutoTestService.ACTION_FLEET_TRIGGLE_AUTOTEST));
                }
                break;
        }
    }
}