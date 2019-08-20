package com.walktour.gui.setting.sysroutine.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.walktour.Utils.ApplicationModel;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;

/**
 * 数据格式设置对话框
 *
 * @author jianchao.wang
 */
@SuppressLint("InflateParams")
public class SysRoutineDataFormatDialog extends BasicDialog {
    /**
     * 建设类
     */
    private Builder builder;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 常规设置配置文件
     */
    private ConfigRoutine configRoutine;

    public SysRoutineDataFormatDialog(Context context, Builder builder, ConfigRoutine configRoutine) {
        super(context);
        this.mContext = context;
        this.builder = builder;
        this.configRoutine = configRoutine;
        this.init();
    }

    /**
     * 初始化
     */
    private void init() {
        ApplicationModel applicationModel = ApplicationModel.getInstance();
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.sys_routine_setting_data_format, null);
        builder.setTitle(R.string.sys_setting_data_format);
        builder.setView(layout);
        layout.findViewById(R.id.layout_rcu).setVisibility(View.GONE);
        CheckBox checkRcu = (CheckBox) layout.findViewById(R.id.toggle_rcu);
        checkRcu.setChecked(configRoutine.isGenRCU(mContext));
        checkRcu.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configRoutine.setGenRCU(mContext, isChecked);
            }
        });

        //DTLog
        layout.findViewById(R.id.layout_dtlog).setVisibility(applicationModel.isAtu() || applicationModel.isBtu() ? View.VISIBLE : View.GONE);
        CheckBox checkDtlog = (CheckBox) layout.findViewById(R.id.toggle_dtlog);
        checkDtlog.setChecked(configRoutine.isGenDTLog(mContext));
        checkDtlog.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configRoutine.setGenDtLog(mContext, isChecked);
            }
        });
        //dcf
        layout.findViewById(R.id.layout_dcf).setVisibility(applicationModel.hasDcf() ? View.VISIBLE : View.GONE);
        CheckBox checkDcf = (CheckBox) layout.findViewById(R.id.toggle_dcf);
        checkDcf.setChecked(configRoutine.isGenDCF(mContext));
        checkDcf.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configRoutine.setGenDCF(mContext, isChecked);
            }
        });

        //Org rcu
        layout.findViewById(R.id.layout_org_rcu).setVisibility(applicationModel.hasOrgRcu() ? View.VISIBLE : View.GONE);
        CheckBox checkOrgRcu = (CheckBox) layout.findViewById(R.id.toggle_org_rcu);
        checkOrgRcu.setChecked(configRoutine.isGenOrgRcu(mContext));
        checkOrgRcu.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configRoutine.setGenOrgRcu(mContext, isChecked);
            }
        });

        //设置CU数据格式
        layout.findViewById(R.id.layout_cu).setVisibility(applicationModel.showInfoTypeCu() ? View.VISIBLE : View.GONE);
        CheckBox checkcu = (CheckBox) layout.findViewById(R.id.toggle_cu);
        checkcu.setChecked(configRoutine.isGenCU(mContext));
        checkcu.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configRoutine.setGenCU(mContext, isChecked);
            }
        });


        //设置ECTI数据格式
        layout.findViewById(R.id.layout_ecti).setVisibility(applicationModel.showInfoTypeEcti() ? View.VISIBLE : View.GONE);
        CheckBox checkecti = (CheckBox) layout.findViewById(R.id.toggle_ecti);
        checkecti.setChecked(configRoutine.isGenECTI(mContext));
        checkecti.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configRoutine.setGenECTI(mContext, isChecked);
            }
        });

        //设置OTS数据格式
        layout.findViewById(R.id.layout_ots).setVisibility(applicationModel.showInfoTypeOTS() ? View.VISIBLE : View.GONE);
        layout.findViewById(R.id.layout_ots).setVisibility(View.VISIBLE);
        CheckBox checkeots = (CheckBox) layout.findViewById(R.id.toggle_ots);
        checkeots.setChecked(configRoutine.isGenOTS(mContext));
        checkeots.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configRoutine.setGenOTS(mContext, isChecked);
            }
        });

        if (ApplicationModel.getInstance().getDcfEncryptKey(mContext).length > 0) {
            layout.findViewById(R.id.layout_rcu).setVisibility(View.GONE);
            layout.findViewById(R.id.layout_dtlog).setVisibility(View.GONE);
            layout.findViewById(R.id.layout_org_rcu).setVisibility(View.GONE);
            layout.findViewById(R.id.layout_cu).setVisibility(View.GONE);
        }
    }

}
