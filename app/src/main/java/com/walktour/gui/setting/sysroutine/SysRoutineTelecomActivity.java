package com.walktour.gui.setting.sysroutine;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.walktour.control.config.ConfigRoutine;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yi.Lin on 2018/6/12.
 * <p>
 * 高级设置-》特殊设置-》电信专项测试界面
 */

public class SysRoutineTelecomActivity extends BasicActivity {

    /**
     * 标题栏文本
     */
    @BindView(R.id.title_txt)
    TextView mTvTitle;
    /**
     * 语音业务发起网络
     */
    @BindView(R.id.tv_voice_service_network)
    TextView mTvVoiceServiceNetwork;
    /**
     * 数据业务发起网络
     */
    @BindView(R.id.tv_data_service_network)
    TextView mTvDataServiceNetwork;


    /**
     * 常规设置配置文件
     */
    private ConfigRoutine mConfigRoutine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_routine_telecom);
        ButterKnife.bind(this);
        mTvTitle.setText(R.string.sys_setting_Telecom_Setting);

        mConfigRoutine = ConfigRoutine.getInstance();
        updateItems();

    }

    /**
     * 根据保存的设置更新界面两项设置
     */
    private void updateItems() {
        mTvVoiceServiceNetwork.setText(getResources().getStringArray(R.array.cucc_setting_array)[mConfigRoutine.getTelecomVoiceNetSetting(this)]);
        mTvDataServiceNetwork.setText(getResources().getStringArray(R.array.cucc_setting_array)[mConfigRoutine.getTelecomDataNetSetting(this)]);
    }


    @OnClick(R.id.pointer)
    public void onClickBack(View view) {
        finish();
    }

    @OnClick(R.id.rl_voice_service_network)
    public void onClickRlVoiceServiceNetwork(View view) {
        BasicDialog.Builder voiceNetDlgBuilder = new BasicDialog.Builder(SysRoutineTelecomActivity.this);
        voiceNetDlgBuilder.setTitle(R.string.sys_setting_telecom_voice_net_setting)
                .setSingleChoiceItems(R.array.cucc_setting_array,
                        mConfigRoutine.getTelecomVoiceNetSetting(SysRoutineTelecomActivity.this),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mConfigRoutine.setTelecomVoiceNetSetting(SysRoutineTelecomActivity.this, which);
                                dialog.dismiss();
                                updateItems();
                            }
                        });
        voiceNetDlgBuilder.show();
    }

    @OnClick(R.id.rl_data_service_network)
    public void onClickRlDataServiceNetwork(View view) {
        BasicDialog.Builder dataNetDlgBuilder = new BasicDialog.Builder(SysRoutineTelecomActivity.this);
        dataNetDlgBuilder.setTitle(R.string.sys_setting_telecom_data_net_setting)
                .setSingleChoiceItems(R.array.cucc_setting_array,
                        mConfigRoutine.getTelecomDataNetSetting(SysRoutineTelecomActivity.this),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mConfigRoutine.setTelecomDataNetSetting(SysRoutineTelecomActivity.this, which);
                                dialog.dismiss();
                                updateItems();
                            }
                        });
        dataNetDlgBuilder.show();
    }
}
