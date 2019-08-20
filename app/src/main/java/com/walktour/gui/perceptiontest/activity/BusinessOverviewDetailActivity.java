package com.walktour.gui.perceptiontest.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.perceptiontest.fleet.BusinessListModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BusinessOverviewDetailActivity extends BasicActivity {

    private final String TAG=BusinessOverviewDetailActivity.class.getSimpleName();

    @BindView(R.id.tv_1)
    TextView textView1;
    @BindView(R.id.tv_2)
    TextView textView2;
    @BindView(R.id.tv_3)
    TextView textView3;
    @BindView(R.id.tv_4)
    TextView textView4;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_overview_detail);
        ButterKnife.bind(this);

        Bundle bundle = this.getIntent().getExtras();
        BusinessListModel.DataBean bean = (BusinessListModel.DataBean) bundle.getSerializable
                ("model");
        if (null != bean) {
            textView1.setText(bean.getEquipment_Net_Manager_Name() + "");
            textView2.setText(bean.getAir_Download_Rate() + "");
            textView3.setText(bean.getAir_Upload_Rate() + "");
            textView4.setText(bean.getItem_Number() + "");

        } else {
            textView1.setText("");
            textView2.setText("");
            textView3.setText("");
            textView4.setText("");
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