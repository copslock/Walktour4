package com.walktour.gui.perceptiontest.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.perceptiontest.fleet.QualityListModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QualityOverviewDetailActivity extends BasicActivity {
    private final String TAG = QualityOverviewDetailActivity.class.getSimpleName();
    @BindView(R.id.tv_1)
    TextView textView1;
    @BindView(R.id.tv_2)
    TextView textView2;
    @BindView(R.id.tv_3)
    TextView textView3;
    @BindView(R.id.tv_4)
    TextView textView4;
    @BindView(R.id.tv_5)
    TextView textView5;
    @BindView(R.id.tv_6)
    TextView textView6;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_overview_detail);
        ButterKnife.bind(this);

        Bundle bundle = this.getIntent().getExtras();
        QualityListModel.DataBean bean = (QualityListModel.DataBean) bundle.getSerializable
                ("model");
        if (null != bean) {
            textView1.setText(bean.getEquipment_Net_Manager_Name() + "");
            textView2.setText(bean.getERAB_SetupSuccessRatio() + "");
            textView3.setText(bean.getLTE_Service_DropRatio() + "");
            textView4.setText(bean.getRRC_SetupSuccessRatio() + "");
            textView5.setText(bean.getRadio_AccessRatio() + "");
            textView6.setText(bean.getItem_Number() + "");

        } else {
            textView1.setText("");
            textView2.setText("");
            textView3.setText("");
            textView4.setText("");
            textView5.setText("");
            textView6.setText("");
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

