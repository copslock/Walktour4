package com.walktour.gui.setting.sysroutine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.walktour.control.config.ConfigDebugModel;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

/**
 * 常规设置调试模式设置
 *
 * @author jianchao.wang
 */
public class SysRoutineDebugModelActivity extends BasicActivity implements OnClickListener {
    /**
     * 是否分帧
     */
    private CheckBox divideFrame;
    /**
     * 是否解码
     */
    private CheckBox decoder;
    /**
     * 是否保存数据
     */
    private CheckBox decoderAndRead;
    /**
     * 事件分析
     */
    private CheckBox eventJudge;
    /**
     * 正常模式
     */
    private CheckBox debugNormal;
    /**
     * 对象
     */
    private ConfigDebugModel debugModel = null;
    /**
     * 上下文
     */
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.sys_routine_setting_debug_model);
        debugModel = ConfigDebugModel.getInstance(this);

        findView();
    }

    private void findView() {
        CheckBox pushDataSet = (CheckBox) findViewById(R.id.check_push_dataset);
        divideFrame = (CheckBox) findViewById(R.id.check_divide_frame);
        decoder = (CheckBox) findViewById(R.id.check_decoder);
        decoderAndRead = (CheckBox) findViewById(R.id.check_decoder_and_readdata);
        eventJudge = (CheckBox) findViewById(R.id.check_event_judeg);
        debugNormal = (CheckBox) findViewById(R.id.check_debug_normal);
        CheckBox queryEvent = (CheckBox) findViewById(R.id.check_query_event);
        CheckBox querySignal = (CheckBox) findViewById(R.id.check_query_signal);
        CheckBox queryParam = (CheckBox) findViewById(R.id.check_query_param);
        CheckBox detailDecoder = (CheckBox) findViewById(R.id.check_detail_decoder);

        pushDataSet.setChecked(debugModel.isPushDataSet());
        divideFrame.setChecked(debugModel.isOnlyFrame());
        decoder.setChecked(debugModel.isOnlyDecoder());
        decoderAndRead.setChecked(debugModel.isDecoderAndRead());
        eventJudge.setChecked(debugModel.isEventJudge());
        debugNormal.setChecked(debugModel.isDebugNormal());
        queryEvent.setChecked(debugModel.isQueryEvent());
        querySignal.setChecked(debugModel.isQuerySignal());
        queryParam.setChecked(debugModel.isQueryParam());
        detailDecoder.setChecked(debugModel.isDetailDecoder());

        pushDataSet.setOnClickListener(this);
        divideFrame.setOnClickListener(this);
        decoder.setOnClickListener(this);
        decoderAndRead.setOnClickListener(this);
        eventJudge.setOnClickListener(this);
        debugNormal.setOnClickListener(this);
        queryEvent.setOnClickListener(this);
        querySignal.setOnClickListener(this);
        queryParam.setOnClickListener(this);
        detailDecoder.setOnClickListener(this);

        // 根据当前是否打开压数据集处理界面逻辑
        datasetButtonEnable(pushDataSet);
    }

    @Override
    public void onClick(View v) {
        CheckBox button = (CheckBox) v;
        switch (button.getId()) {
            case R.id.check_push_dataset:
                datasetButtonLogic(button);
                debugModel.setPushDataSet(mContext, button.isChecked());
                break;
            case R.id.check_divide_frame:
                datasetButtonLogic(button);
                debugModel.setOnlyFrame(mContext, button.isChecked());
                break;
            case R.id.check_decoder:
                datasetButtonLogic(button);
                debugModel.setOnlyDecoder(mContext, button.isChecked());
                break;
            case R.id.check_decoder_and_readdata:
                datasetButtonLogic(button);
                debugModel.setDecoderAndRead(mContext, button.isChecked());
                break;
            case R.id.check_event_judeg:
                datasetButtonLogic(button);
                debugModel.setEventJudge(mContext, button.isChecked());
                break;
            case R.id.check_debug_normal:
                datasetButtonLogic(button);
                debugModel.setDebugNormal(mContext, button.isChecked());
                break;
            case R.id.check_query_event:
                debugModel.setQueryEvent(mContext, button.isChecked());
                break;
            case R.id.check_query_signal:
                debugModel.setQuerySignal(mContext, button.isChecked());
                break;
            case R.id.check_query_param:
                debugModel.setQueryParam(mContext, button.isChecked());
                break;
            case R.id.check_detail_decoder:
                debugModel.setDetailDecoder(mContext, button.isChecked());
                break;
            default:
                break;
        }
    }

    /**
     * 数据集设置中按钮切换时的界面逻辑处理
     */
    private void datasetButtonLogic(CheckBox cBox) {
        if (cBox.getId() != R.id.check_divide_frame) {
            divideFrame.setChecked(false);
            debugModel.setOnlyFrame(mContext, false);
        }
        if (cBox.getId() != R.id.check_decoder) {
            decoder.setChecked(false);
            debugModel.setOnlyDecoder(mContext, false);
        }
        if (cBox.getId() != R.id.check_decoder_and_readdata) {
            decoderAndRead.setChecked(false);
            debugModel.setDecoderAndRead(mContext, false);
        }
        if (cBox.getId() != R.id.check_event_judeg) {
            eventJudge.setChecked(false);
            debugModel.setEventJudge(mContext, false);
        }
        if (cBox.getId() != R.id.check_debug_normal) {
            debugNormal.setChecked(false);
            debugModel.setDebugNormal(mContext, false);
        }

        datasetButtonEnable(cBox);
    }

    private void datasetButtonEnable(CheckBox cBox) {
        if (cBox.getId() == R.id.check_push_dataset) {
            divideFrame.setEnabled(cBox.isChecked());
            decoder.setEnabled(cBox.isChecked());
            decoderAndRead.setEnabled(cBox.isChecked());
            eventJudge.setEnabled(cBox.isChecked());
            debugNormal.setEnabled(cBox.isChecked());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(SysRoutineActivity.SHOW_ADVENCED_TAB);
            this.sendBroadcast(intent);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
