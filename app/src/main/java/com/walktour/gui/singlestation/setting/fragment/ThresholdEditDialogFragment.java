package com.walktour.gui.singlestation.setting.fragment;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.walktour.Utils.TotalStruct;
import com.walktour.base.gui.fragment.BaseDialogFragment;
import com.walktour.gui.R;
import com.walktour.gui.R2;
import com.walktour.gui.singlestation.dao.model.ThresholdSetting;

import butterknife.BindView;

/**
 * 阈值编辑对话框
 * Created by wangk on 2017/8/9.
 */

public class ThresholdEditDialogFragment extends BaseDialogFragment {
    /**
     * 日志标识
     */
    private static final String TAG = "ThresholdEditDialogFragment";
    /**
     * 任务名称
     */
    @BindView(R2.id.kpi_setting_task_id)
    TextView mTaskName;
    /**
     * 阈值key
     */
    @BindView(R2.id.kpi_setting_kpi_id)
    TextView mThresholdKey;
    /**
     * 阈值key
     */
    @BindView(R2.id.kpi_setting_formula_id)
    Spinner mFormulaSpinner;
    /**
     * 阈值单位
     */
    @BindView(R2.id.show_unit)
    TextView mThresholdUnit;
    /**
     * 阈值判断值
     */
    @BindView(R2.id.kpi_setting_threshold_id)
    EditText mThresholdValue;
    /**
     * 当前编辑的阈值对象
     */
    private ThresholdSetting mThresholdSetting;
    /**
     * 显示的判断公式选项
     */
    private static final String[] sFormulaArray = {">", ">=", "	=", "<=", "	<"};

    public ThresholdEditDialogFragment() {
        super(R.string.edit, R.layout.show_kpi_dialog);
    }

    @Override
    public String getLogTAG() {
        return TAG;
    }

    @Override
    protected void setShowValues() {
        this.mThresholdSetting = (ThresholdSetting) this.getParcelableBundle("threshold_setting");
        this.mTaskName.setText(this.getTestTaskResId(this.mThresholdSetting.getTestTask()));
        ArrayAdapter<String> formulaArrayAP = new ArrayAdapter<>(this.getContext(), R.layout.simple_spinner_custom_layout, sFormulaArray);
        formulaArrayAP.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.mFormulaSpinner.setAdapter(formulaArrayAP);
        this.mThresholdUnit.setText(String.format(this.getString(R.string.sys_setting_kpi_threshold_str), this.mThresholdSetting.getThresholdUnit()));
        this.mThresholdKey.setText(getThresholdKeyResId(this.mThresholdSetting.getThresholdKey()));
        int selection = 0;
        for (int i = 0; i < sFormulaArray.length; i++) {
            if (sFormulaArray[i].equals(this.mThresholdSetting.getOperator())) {
                selection = i;
                break;
            }
        }
        this.mFormulaSpinner.setSelection(selection);
        this.mThresholdValue.setText(String.valueOf(this.mThresholdSetting.getThresholdValue()));
        this.mThresholdValue.setSelection(String.valueOf(this.mThresholdSetting.getThresholdValue()).length());
    }

    /**
     * 获得要显示的阈值名称资源ID
     *
     * @param thresholdKey 阈值Key
     * @return 显示的阈值名称资源ID
     */
    private int getThresholdKeyResId(String thresholdKey) {
        if (thresholdKey.equals(TotalStruct.TotalSingleStation._RSCoverMileage.name())) {
            return R.string.single_station_threshold_rs_coverage_ratio;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._FTPAttemptHandoverTimes.name())) {
            return R.string.single_station_threshold_attempt_handover_times;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._FTPHandoverSuccessRate.name())) {
            return R.string.single_station_threshold_handover_succ_ratio;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._PCISample.name())) {
            return R.string.single_station_threshold_pci_sample;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._RSRPAverage.name())) {
            return R.string.single_station_threshold_rsrp;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._tryTimes.name())) {
            return R.string.single_station_threshold_attempt_times;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._successRate.name())) {
            return R.string.single_station_threshold_succ_ratio;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._SpeedAverage.name())) {
            return R.string.single_station_threshold_data_average_rate;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_tryTimes.name())) {
            return R.string.single_station_threshold_attampt_call_times_csfb;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_establishedRate.name())) {
            return R.string.single_station_threshold_establised_succ_ratio_csfb;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_successRate.name())) {
            return R.string.single_station_threshold_call_succ_ratio_csfb;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._volte_successRate.name())) {
            return R.string.single_station_threshold_call_succ_ratio_volte;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._volte_eSRVCC.name())) {
            return R.string.single_station_threshold_handover_succ_times_esrvcc;
        }else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_returnDelay.name())) {
            return R.string.single_station_threshold_csfb_return_delay;
        }else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_connectDelay.name())) {
            return R.string.single_station_threshold_csfb_connect_delay;
        }else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_returnSuccessRate.name())) {
            return R.string.single_station_threshold_csfb_return_success_rate;
        }

        return R.string.single_station_validation;
    }

    /**
     * 获取测试任务的名称资源ID
     *
     * @param testTask 测试任务
     * @return 测试任务的名称资源ID
     */
    private int getTestTaskResId(String testTask) {
        switch (testTask) {
            case "FTP_Download":
                return R.string.act_task_ftpdownload;
            case "Idle":
                return R.string.act_task_empty;
            case "Attach":
                return R.string.act_task_attach;
            case "FTP_Upload":
                return R.string.act_task_ftpupload;
            case "MOC_CSFB":
            case "MOC_VOLTE":
                return R.string.act_task_initiativecall;
        }

        return R.string.single_station_validation;
    }


    @Override
    protected Bundle setCallBackValues() {
        Bundle bundle = new Bundle();
        this.mThresholdSetting.setThresholdValue(Float.parseFloat(this.mThresholdValue.getText().toString()));
        this.mThresholdSetting.setOperator(sFormulaArray[(int) this.mFormulaSpinner.getSelectedItemId()].trim());
        bundle.putParcelable("threshold_setting", this.mThresholdSetting);
        return bundle;
    }
}
