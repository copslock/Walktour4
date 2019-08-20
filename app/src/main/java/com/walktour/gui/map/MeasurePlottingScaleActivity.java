package com.walktour.gui.map;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yi.Lin on 2018/9/12.
 * 自动计算比例尺界面
 */

public class MeasurePlottingScaleActivity extends BasicActivity {

    private static final String TAG = "MeasurePlottingScaleActivity";

    @BindView(R.id.iv_indoor_map)
    ImageView mIvIndoorMap;

    @BindView(R.id.et_scale)
    EditText mEtScale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_plotting_scale);
        ButterKnife.bind(this);
        mEtScale.setText(String.valueOf(MapFactory.getMapData().getPlottingScale()));
        mEtScale.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        String indoorMapPath = SharePreferencesUtil.getInstance(this).getString(MapView.SP_INDOOR_MAP_PATH);
        if (!TextUtils.isEmpty(indoorMapPath)) {
            mIvIndoorMap.setImageURI(Uri.parse(indoorMapPath));
        }
    }

    public static class OnPlottingScaleMeasuredEvent {
        private String plottingScaleInput;

        public OnPlottingScaleMeasuredEvent(String plottingScaleInput) {
            this.plottingScaleInput = plottingScaleInput;
        }

        public String getPlottingScaleInput() {
            return plottingScaleInput;
        }

        public void setPlottingScaleInput(String plottingScaleInput) {
            this.plottingScaleInput = plottingScaleInput;
        }
    }

    @OnClick(R.id.btn_ok)
    public void onClickOk(View view) {
        String scaleStr = mEtScale.getText().toString().trim();
        EventBus.getDefault().post(new OnPlottingScaleMeasuredEvent(scaleStr));
        finish();
    }

}
