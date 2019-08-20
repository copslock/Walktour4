package com.walktour.gui.mos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.walktour.gui.R;
import com.walktour.gui.setting.bluetoothmos.fragment.BluetoothMOCSummaryFragment;
import com.walktour.gui.setting.bluetoothmos.fragment.BluetoothMTCSummaryFragment;
import com.walktour.gui.setting.bluetoothmos.fragment.BluetoothPhoneFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 蓝牙 Mos 头配对界面
 *
 * @author zhicheng.chen
 * @date 2019/3/19
 */
public class MosMatchActivity extends FragmentActivity {

    @BindView(R.id.title_txt)
    TextView mTvTitle;

    public static final String EXTRA_FRAGMENT_TYPE = "EXTRA_FRAGMENT_TYPE";
    public static final int EXTRA_MACTCH_MOC_MOSBOX = 0;
    //    public static final int EXTRA_MACTCH_MTC_MOSBOX = 1;
    public static final int EXTRA_MACTCH_PHONE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mos_match);

        ButterKnife.bind(this);


        int type = getIntent().getIntExtra(EXTRA_FRAGMENT_TYPE, 0);
        Fragment fragment = null;
        if (type == EXTRA_MACTCH_MOC_MOSBOX) {
            fragment = new BluetoothMOCSummaryFragment();
            mTvTitle.setText("配对MOS盒");
        }


        //        else if (type == EXTRA_MACTCH_MTC_MOSBOX) {
        //            fragment = new BluetoothMTCSummaryFragment();
        //        }

        else if (type == EXTRA_MACTCH_PHONE) {
            fragment = new BluetoothPhoneFragment();
            mTvTitle.setText("配对手机");
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fl_content, fragment).commit();
        }
    }

    @OnClick(R.id.pointer)
    void clickBackButton() {
        finish();
    }
}
