package com.walktour.gui.fleet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ServerMessage;
import com.walktour.Utils.TabHostUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicTabActivity;
import com.walktour.gui.R;
import com.walktour.gui.data.FileManagerFragmentActivity2;
import com.walktour.service.ApplicationInitService;


/**************************************** 
 * 系统设置 界面
 * **************************************/
@SuppressWarnings("deprecation")
public class Fleet extends BasicTabActivity implements OnTabChangeListener {
    private TabHost tabHost;
    private ApplicationModel appModel = ApplicationModel.getInstance();
    private boolean isDestroy = false;
    private int waitEvnInitTimes = 0;
    private int envInitSucc = 1;
    //BroadcastReceiver
    private static int currentTab = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        //findView();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        regedit();
        if (!appModel.getAppList().contains(WalkStruct.AppType.AutomatismTest)) {
            if (appModel.isEnvironmentInit())
                Toast.makeText(Fleet.this, R.string.main_license_autotest_faild, Toast.LENGTH_LONG).show();
            this.finish();
            return;
        }
        initTabHost();
    }


    /**
     * 初始化TabHost布局及调用逻辑<BR>
     * 初始对应Tab Acitity对象
     */
    public void initTabHost() {
        setContentView(R.layout.fleet_activity);
        (initTextView(R.id.title_txt)).setText(R.string.main_autotest);
        findViewById(R.id.pointer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fleet.this.finish();
            }
        });
        tabHost = getTabHost();
        //设置Button
        View fleetSettingBtn = TabHostUtil.createTab(this, tabHost, "tab1",
                R.string.fleet_setting, new Intent(Fleet.this, FleetSetting.class));

        //事件Button
        View fleetEvent = TabHostUtil.createTab(this, tabHost, "tab2",
                R.string.fleet_event, new Intent(Fleet.this,
                        com.walktour.gui.map.Event.class));

        //数据
        Intent intent = new Intent(this, FileManagerFragmentActivity2.class);
        intent.putExtra("from", 0);
        View dataBtn = TabHostUtil.createTab(this, tabHost, "tab3",
                R.string.info_data, intent);
        tabHost.setOnTabChangedListener(this);

    }


    /**
     * 更新字体颜色
     */
    private void updateTab(TabHost tabHost) {
        TabHostUtil.updateTab(this, tabHost);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        unregisterReceiver(mEventReceiver);//反注册事件监听
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    ;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == envInitSucc) {
                initTabHost();
            }
        }
    };

    //注册广播接收器
    private void regedit() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServerMessage.ACTION_FLEET_LOG);
        filter.addAction(ServerMessage.ACTION_FLEET_SWITCH);
        this.registerReceiver(mEventReceiver, filter);
    }


    /**
     * 广播接收器:统一接收所有操作事件和结果,再通知FleetEvent页面或者FleetSetting页面更新
     */
    private BroadcastReceiver mEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //跳转到事件页面
            if (intent.getAction().equals(ServerMessage.ACTION_FLEET_SWITCH)) {
                currentTab = 1;
                tabHost.setCurrentTab(currentTab);
                return;
            }

            //转发"事件广播"到"Fleet事件页面"
            if (intent.getAction().equals(ServerMessage.ACTION_FLEET_LOG)) {
                //接收FleetEvent页面要显示的事件
					/*String ev = intent.getExtras().getString(Fleet.KEY_EVENT);
					Event.addEvent(ev);*/
                //通知FleetEvent页面更新
                sendRefreshBroadcast();
            }

        }

    };//end inner class EventBroadcastReceiver


    /**
     * 发送广播到Fleet事件页面显示
     */
    private void sendRefreshBroadcast() {
        Intent intent = new Intent();
        //intent.putExtra(Fleet.KEY_EVENT, event);
        intent.setAction(ServerMessage.ACTION_FLEET_REFRESH);
        sendBroadcast(intent);
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        updateTab(tabHost);
    }


    @Override
    public void onTabChanged(String tabId) {
        updateTab(tabHost);
    }

}
