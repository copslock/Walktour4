package com.walktour.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.walktour.framework.ui.BasicActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * @date on 2018/8/27
 * @describe  当成启动页
 * @author jinfeng.xie
 * @version 1.0.0
 *
 */
public class ProgressActivity extends BasicActivity {
    public static final String BASE_PROGRESS_FINISH = "com.walktour.gui.progressfinish";
    public static final String EXTRA_MESSAGE_ID = "extra_msg_id";
    @BindView(R.id.progress_wait)
    ProgressBar progressWait;
    @BindView(R.id.base_progress_showtxt)
    TextView baseProgressShowtxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent intent = getIntent();
        int msgId = intent.getIntExtra(EXTRA_MESSAGE_ID, R.string.str_waitting);
        setContentView(R.layout.base_progress);
        ButterKnife.bind(this);
        progressWait.setVisibility(View.VISIBLE);
        baseProgressShowtxt.setVisibility(View.VISIBLE);
        baseProgressShowtxt.setText(getString(msgId));

        IntentFilter filter = new IntentFilter();
        filter.addAction(BASE_PROGRESS_FINISH);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BASE_PROGRESS_FINISH)) {
               toMain();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_scale_in,R.anim.anim_scale_out);
    }

    public void toMain() {
        finish();
    }
}
