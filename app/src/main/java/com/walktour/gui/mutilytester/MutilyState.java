package com.walktour.gui.mutilytester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct.UMPCConnectStatus;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

public class MutilyState extends BasicActivity implements OnClickListener {

    private boolean isApnPointChange = false; // 是否改变数据测试时APN接入点,如果是在onResume的时候刷新页面

    private TextView text_terminalsign;
    private TextView text_network;
    private TextView text_network_type;
    private TextView text_imei;
    //	private TextView text_apnname;
    private TextView text_connect_info;
    private ImageView im_ConnectState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terminal_state);

        text_terminalsign = (TextView) findViewById(R.id.text_terminal_sign);
        text_network = (TextView) findViewById(R.id.text_network);
        text_network_type = (TextView) findViewById(R.id.text_network_type);
        text_imei = (TextView) findViewById(R.id.text_imei);
//		text_apnname = (TextView) findViewById(R.id.text_apn_netname);
        text_connect_info = (TextView) findViewById(R.id.text_connectstate_info);
        im_ConnectState = (ImageView) findViewById(R.id.iv_terminal_connectstate);

        registerBroadcast();
        findView();
    }

    private void registerBroadcast() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(WalkMessage.ACTION_BLUE_CONNECT_STATE_CHANGE);

        registerReceiver(bReceiver, iFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isApnPointChange) {
            isApnPointChange = false;
            findView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }

    private void findView() {
        text_imei.setText(MyPhoneState.getInstance().getDeviceId(getApplicationContext()));
        text_network.setText(MyPhoneState.getInstance().getNetworkOperateName(getApplicationContext()));
        text_network_type.setText(MyPhoneState.getInstance().getNetworkName(getApplicationContext()));

//		text_apnname.setText(ConfigAPN.getInstance().getDataAPN());

        // 根据当前是否连接设置相应状态图
        text_terminalsign.setText(ApplicationModel.getInstance().getTerminalSign());
        if (ApplicationModel.getInstance().getUmpcStatus() == UMPCConnectStatus.TerminalConnectFaild) {
            im_ConnectState.setImageDrawable(getResources().getDrawable(R.drawable.state_disconect));
            // text_terminalsign.setTextColor(getResources().getColor(R.color.task_main_btm));
            text_connect_info.setText(R.string.mutilytester_state_connect_faild);
        } else if (ApplicationModel.getInstance().getUmpcStatus() == UMPCConnectStatus.TerminalConnected) {
            im_ConnectState.setImageDrawable(getResources().getDrawable(R.drawable.state_connectting));
            // text_terminalsign.setTextColor(getResources().getColor(R.color.private_yellow));
            text_connect_info.setText(R.string.mutilytester_state_connect_succes);
        } else if (ApplicationModel.getInstance().getUmpcStatus() == UMPCConnectStatus.TerminalLoginSucces) {
            im_ConnectState.setImageDrawable(getResources().getDrawable(R.drawable.state_connected));
            // text_terminalsign.setTextColor(getResources().getColor(R.color.private_yellow));
            text_connect_info.setText(R.string.mutilytester_state_login_succes);
        } else if (ApplicationModel.getInstance().getUmpcStatus() == UMPCConnectStatus.TerminalLoginFaild) {
            im_ConnectState.setImageDrawable(getResources().getDrawable(R.drawable.state_connectting));
            // text_terminalsign.setTextColor(getResources().getColor(R.color.task_main_btm));
            text_connect_info.setText(R.string.mutilytester_state_login_faild);
        }
    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WalkMessage.ACTION_BLUE_CONNECT_STATE_CHANGE)) {
                findView();
            }
        }
    };

}
