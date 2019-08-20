package com.walktour.gui.mutilytester;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TabHostUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.framework.ui.BasicTabActivity;
import com.walktour.gui.R;
import com.walktour.gui.data.FileManagerFragmentActivity2;
import com.walktour.gui.map.Event;
import com.walktour.service.ApplicationInitService;

public class MutilyTester extends BasicTabActivity implements OnTabChangeListener {
    private static int envInitSucc = 1;
    private static int currentTab = 0;
    private TabHost tabHost;
    private ApplicationModel appModel = ApplicationModel.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (!appModel.getAppList().contains(WalkStruct.AppType.MutilyTester)) {
            if (appModel.isEnvironmentInit())
                Toast.makeText(MutilyTester.this, R.string.main_license_mutilytester_faild, Toast.LENGTH_LONG).show();
            this.finish();
            return;
        }
        initTabHost();
    }

    public void initTabHost() {
        setContentView(R.layout.mutilytester_activity);
        (initTextView(R.id.title_txt)).setText(R.string.main_mutilytester);
        Button btn = this.initButton(R.id.pointersetting);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpActivity(MultilyTestSetting.class);
            }
        });
        findViewById(R.id.pointer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MutilyTester.this.finish();
            }
        });
        tabHost = getTabHost();
        tabHost.setOnTabChangedListener(this);
        // 设置Button
        View mutilyTesterSet = TabHostUtil.createTab(this, tabHost, "tab1",
                R.string.sc_main_con, new Intent(getApplicationContext(), MutilyState.class));

        // 事件Button
        View event = TabHostUtil.createTab(this, tabHost, "tab2",
                R.string.fleet_event, new Intent(getApplicationContext(), Event.class));

        // 数据
        Intent intent = new Intent(this, FileManagerFragmentActivity2.class);
        intent.putExtra("from", 1);
        View dataBtn = TabHostUtil.createTab(this, tabHost, "tab3",
                R.string.info_data, intent);




    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTab(tabHost);
    }

    ;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == envInitSucc) {
                initTabHost();
            }
        }
    };

    class WaitEnvInit extends Thread {
        public void run() {
            while (!appModel.isEnvironmentInit()) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Message msg = mHandler.obtainMessage(envInitSucc);
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    @Override
    public void onTabChanged(String tabId) {
        updateTab(tabHost);
    }

    ;

    /**
     * 更新字体颜色
     */
    private void updateTab(TabHost tabHost) {
        TabHostUtil.updateTab(this,tabHost);

    }
}
