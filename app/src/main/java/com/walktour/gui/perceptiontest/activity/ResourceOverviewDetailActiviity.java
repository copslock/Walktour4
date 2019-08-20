package com.walktour.gui.perceptiontest.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.perceptiontest.fleet.ResourceListModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 资源概览详情界面
 */
public class ResourceOverviewDetailActiviity extends BasicActivity {
    private final String TAG = ResourceOverviewDetailActiviity.class.getSimpleName();
    @BindView(R.id.tvx_1)
    TextView textView1;
    @BindView(R.id.tvx_2)
    TextView textView2;
    @BindView(R.id.tvx_3)
    TextView textView3;
    @BindView(R.id.tvx_4)
    TextView textView4;
    @BindView(R.id.tvx_5)
    TextView textView5;
    @BindView(R.id.tvx_6)
    TextView textView6;
    @BindView(R.id.tvx_7)
    TextView textView7;
    @BindView(R.id.tvx_8)
    TextView textView8;
    @BindView(R.id.tvx_9)
    TextView textView9;
    @BindView(R.id.tvx_10)
    TextView textView10;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_overview_detail);
        ButterKnife.bind(this);

        Bundle bundle = this.getIntent().getExtras();
        ResourceListModel.DataBean bean = (ResourceListModel.DataBean) bundle.getSerializable
                ("model");
        if (null != bean) {
            textView1.setText(bean.getCellId() + "");
            textView2.setText(bean.getCellName() + "");
            textView3.setText(bean.getAltitude() + "");
            textView4.setText(bean.getAzimuth() + "");
            textView5.setText(bean.getElecTilt() + "");
            textView6.setText(bean.getGroundHeight() + "");
            textView7.setText(bean.getLongitude() + "");
            textView8.setText(bean.getLatitude() + "");
            textView9.setText(bean.getMechTilt() + "");
            textView10.setText(bean.getProvider() + "");

        }else{
            textView1.setText("");
            textView2.setText("");
            textView3.setText("");
            textView4.setText("");
            textView5.setText("");
            textView6.setText("");
            textView7.setText("");
            textView8.setText("");
            textView9.setText("");
            textView10.setText("");
        }

    }
    @OnClick(R.id.pointer)
    public void doBackBtn()
    {
        this.finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            this.finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
