package com.walktour.gui.singlestation.survey.fragment;

import android.widget.EditText;
import android.widget.TextView;

import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.model.CellInfo;
import com.walktour.gui.singlestation.dao.model.SurveyCellInfo;
import com.walktour.gui.singlestation.survey.component.DaggerSurveyCellEditFragmentComponent;
import com.walktour.gui.singlestation.survey.module.SurveyCellEditFragmentModule;
import com.walktour.gui.singlestation.survey.presenter.SurveyCellEditFragmentPresenter;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * 基站勘查基站参数编辑视图
 * Created by wangk on 2017/7/18.
 */

public class SurveyCellEditFragment extends SurveyEditBaseFragment {
    /**
     * 日志标识
     */
    private static final String TAG = "SurveyCellEditFragment";
    /**
     * 小区载波配置
     */
    @BindView(R.id.cell_carrier_configuration_old)
    TextView mCellCarrierConfigurationOld;
    /**
     * 小区载波配置
     */
    @BindView(R.id.cell_carrier_configuration)
    EditText mCellCarrierConfiguration;
    /**
     * 小区ID
     */
    @BindView(R.id.cell_id_old)
    TextView mCellIdOld;
    /**
     * 小区ID
     */
    @BindView(R.id.cell_id)
    EditText mCellId;
    /**
     * PCI
     */
    @BindView(R.id.cell_pci_old)
    TextView mCellPciOld;
    /**
     * PCI
     */
    @BindView(R.id.cell_pci)
    EditText mCellPci;
    /**
     * 频段
     */
    @BindView(R.id.cell_band_old)
    TextView mCellBandOld;
    /**
     * 频段
     */
    @BindView(R.id.cell_band)
    EditText mCellBand;
    /**
     * 主频点
     */
    @BindView(R.id.cell_frequency_old)
    TextView mCellFrequencyOld;
    /**
     * 主频点
     */
    @BindView(R.id.cell_frequency)
    EditText mCellFrequency;
    /**
     * 小区带宽
     */
    @BindView(R.id.cell_bandwidth_old)
    TextView mCellBandwidthOld;
    /**
     * 小区带宽
     */
    @BindView(R.id.cell_bandwidth)
    EditText mCellBandwidth;
    /**
     * 根序列
     */
    @BindView(R.id.cell_root_sequence_index_old)
    TextView mCellRootSequenceIndexOld;
    /**
     * 根序列
     */
    @BindView(R.id.cell_root_sequence_index)
    EditText mCellRootSequenceIndex;
    /**
     * 子帧配比
     */
    @BindView(R.id.cell_subframe_ratio_old)
    TextView mCellSubframeRatioOld;
    /**
     * 子帧配比
     */
    @BindView(R.id.cell_subframe_ratio)
    EditText mCellSubframeRatio;
    /**
     * 特殊子帧配比
     */
    @BindView(R.id.cell_special_subframe_ratio_old)
    TextView mCellSpecialSubframeRatioOld;
    /**
     * 特殊子帧配比
     */
    @BindView(R.id.cell_special_subframe_ratio)
    EditText mCellSpecialSubframeRatio;
    /**
     * RsPower(dBm)
     */
    @BindView(R.id.cell_rspower_old)
    TextView mCellRspowerOld;
    /**
     * RsPower(dBm)
     */
    @BindView(R.id.cell_rspower)
    EditText mCellRspower;
    /**
     * PDCCH符号数
     */
    @BindView(R.id.cell_pdcch_old)
    TextView mCellPdcchOld;
    /**
     * PDCCH符号数
     */
    @BindView(R.id.cell_pdcch)
    EditText mCellPdcch;
    /**
     * PA
     */
    @BindView(R.id.cell_pa_old)
    TextView mCellPaOld;
    /**
     * PA
     */
    @BindView(R.id.cell_pa)
    EditText mCellPa;
    /**
     * PB
     */
    @BindView(R.id.cell_pb_old)
    TextView mCellPbOld;
    /**
     * PB
     */
    @BindView(R.id.cell_pb)
    EditText mCellPb;
    /**
     * 天线挂高（米）
     */
    @BindView(R.id.cell_aerial_high_old)
    TextView mCellAerialHighOld;
    /**
     * 天线挂高（米）
     */
    @BindView(R.id.cell_aerial_high)
    EditText mCellAerialHigh;
    /**
     * 方位角（度）
     */
    @BindView(R.id.cell_azimuth_old)
    TextView mCellAzimuthOld;
    /**
     * 方位角（度）
     */
    @BindView(R.id.cell_azimuth)
    EditText mCellAzimuth;
    /**
     * 总下倾角（度）
     */
    @BindView(R.id.cell_down_angle_old)
    TextView mCellDownAngleOld;
    /**
     * 总下倾角（度）
     */
    @BindView(R.id.cell_down_angle)
    EditText mCellDownAngle;
    /**
     * 预制电下倾（度）
     */
    @BindView(R.id.cell_electric_down_angle_old)
    TextView mCellElectricDownAngleOld;
    /**
     * 预制电下倾（度）
     */
    @BindView(R.id.cell_electric_down_angle)
    EditText mCellElectricDownAngle;
    /**
     * 机械下倾角（度）
     */
    @BindView(R.id.cell_machine_down_angle_old)
    TextView mCellMachineDownAngleOld;
    /**
     * 机械下倾角（度）
     */
    @BindView(R.id.cell_machine_down_angle)
    EditText mCellMachineDownAngle;
    /**
     * 垂直半功率角（度）
     */
    @BindView(R.id.cell_vertical_falf_power_angle_old)
    TextView mCellVerticalFalfPowerAngleOld;
    /**
     * 垂直半功率角（度）
     */
    @BindView(R.id.cell_vertical_falf_power_angle)
    EditText mCellVerticalHalfPowerAngle;
    /**
     * 水平半功率角（度）
     */
    @BindView(R.id.cell_horizontal_falf_power_angle_old)
    TextView mCellHorizontalFalfPowerAngleOld;
    /**
     * 水平半功率角（度）
     */
    @BindView(R.id.cell_horizontal_falf_power_angle)
    EditText mCellHorizontalHalfPowerAngle;
    /**
     * 天线厂家
     */
    @BindView(R.id.cell_aerial_vender)
    EditText mCellAerialVender;
    /**
     * 天线型号
     */
    @BindView(R.id.cell_aerial_type)
    EditText mCellAerialType;

    /**
     * 界面交互类
     */
    @Inject
    SurveyCellEditFragmentPresenter mPresenter;
    /**
     * 关联的勘查小区信息
     */
    SurveyCellInfo mSurveyCellInfo;

    public SurveyCellEditFragment() {
        super(R.string.single_station_survey_cell_parameters, R.layout.fragment_single_station_survey_cell_edit);
    }

    @Override
    public String getLogTAG() {
        return TAG;
    }

    @Override
    public BaseFragmentPresenter getPresenter() {
        return this.mPresenter;
    }

    @Override
    protected void setupFragmentComponent() {
        DaggerSurveyCellEditFragmentComponent.builder().surveyCellEditFragmentModule(new SurveyCellEditFragmentModule(this)).build().inject(this);
    }

    @Override
    protected void onCreateView() {

    }

    public SurveyCellInfo getSurveyCellInfo() {
        return mSurveyCellInfo;
    }

    public void setSurveyCellInfo(SurveyCellInfo surveyCellInfo) {
        mSurveyCellInfo = surveyCellInfo;
    }

    /**
     * 显示勘查的小区参数
     */
    public void show() {
        CellInfo cellInfo = this.mSurveyCellInfo.getCellInfo();
        this.mCellAerialHigh.setText(String.valueOf(this.mSurveyCellInfo.getAerialHigh()));
        this.mCellAerialHighOld.setText(getOldValueShow(String.valueOf(cellInfo.getAerialHigh())));
        this.mCellAerialType.setText(this.mSurveyCellInfo.getAerialType());
        this.mCellAerialVender.setText(this.mSurveyCellInfo.getAerialVender());
        this.mCellAzimuth.setText(String.valueOf(this.mSurveyCellInfo.getAzimuth()));
        this.mCellAzimuthOld.setText(getOldValueShow(String.valueOf(cellInfo.getAzimuth())));
        this.mCellBand.setText(this.mSurveyCellInfo.getBand());
        this.mCellBandOld.setText(getOldValueShow(cellInfo.getBand()));
        this.mCellBandwidth.setText(String.valueOf(this.mSurveyCellInfo.getBandwidth()));
        this.mCellBandwidthOld.setText(getOldValueShow(String.valueOf(cellInfo.getBandwidth())));
        this.mCellCarrierConfiguration.setText(this.mSurveyCellInfo.getCarrierSetup());
        this.mCellCarrierConfigurationOld.setText(getOldValueShow(cellInfo.getCarrierSetup()));
        this.mCellDownAngle.setText(String.valueOf(this.mSurveyCellInfo.getDownAngle()));
        this.mCellDownAngleOld.setText(getOldValueShow(String.valueOf(cellInfo.getDownAngle())));
        this.mCellElectricDownAngle.setText(String.valueOf(this.mSurveyCellInfo.getElectricDownAngle()));
        this.mCellElectricDownAngleOld.setText(getOldValueShow(String.valueOf(cellInfo.getElectricDownAngle())));
        this.mCellFrequency.setText(String.valueOf(this.mSurveyCellInfo.getFrequency()));
        this.mCellFrequencyOld.setText(getOldValueShow(String.valueOf(cellInfo.getFrequency())));
        this.mCellHorizontalHalfPowerAngle.setText(String.valueOf(this.mSurveyCellInfo.getHorizontalFalfPowerAngle()));
        this.mCellHorizontalFalfPowerAngleOld.setText(getOldValueShow(String.valueOf(cellInfo.getHorizontalFalfPowerAngle())));
        this.mCellId.setText(String.valueOf(this.mSurveyCellInfo.getCellId()));
        this.mCellIdOld.setText(getOldValueShow(String.valueOf(cellInfo.getCellId())));
        this.mCellMachineDownAngle.setText(String.valueOf(this.mSurveyCellInfo.getMachineDownAngle()));
        this.mCellMachineDownAngleOld.setText(getOldValueShow(String.valueOf(cellInfo.getMachineDownAngle())));
        this.mCellPa.setText(String.valueOf(this.mSurveyCellInfo.getPA()));
        this.mCellPaOld.setText(getOldValueShow(String.valueOf(cellInfo.getPA())));
        this.mCellPb.setText(String.valueOf(this.mSurveyCellInfo.getPB()));
        this.mCellPbOld.setText(getOldValueShow(String.valueOf(cellInfo.getPB())));
        this.mCellPci.setText(String.valueOf(this.mSurveyCellInfo.getPCI()));
        this.mCellPciOld.setText(getOldValueShow(String.valueOf(cellInfo.getPCI())));
        this.mCellPdcch.setText(String.valueOf(this.mSurveyCellInfo.getPDCCH()));
        this.mCellPdcchOld.setText(getOldValueShow(String.valueOf(cellInfo.getPDCCH())));
        this.mCellRootSequenceIndex.setText(String.valueOf(this.mSurveyCellInfo.getRootSequence()));
        this.mCellRootSequenceIndexOld.setText(getOldValueShow(String.valueOf(cellInfo.getRootSequence())));
        this.mCellRspower.setText(String.valueOf(this.mSurveyCellInfo.getRsPower()));
        this.mCellRspowerOld.setText(getOldValueShow(String.valueOf(cellInfo.getRsPower())));
        this.mCellSpecialSubframeRatio.setText(String.valueOf(this.mSurveyCellInfo.getSpecialSubframeMatching()));
        this.mCellSpecialSubframeRatioOld.setText(getOldValueShow(String.valueOf(cellInfo.getSpecialSubframeMatching())));
        this.mCellSubframeRatio.setText(String.valueOf(this.mSurveyCellInfo.getSubframeMatching()));
        this.mCellSubframeRatioOld.setText(getOldValueShow(String.valueOf(cellInfo.getSubframeMatching())));
        this.mCellVerticalHalfPowerAngle.setText(String.valueOf(this.mSurveyCellInfo.getVerticalFalfPowerAngle()));
        this.mCellVerticalFalfPowerAngleOld.setText(getOldValueShow(String.valueOf(cellInfo.getVerticalFalfPowerAngle())));
    }


    /**
     * @return 小区载波配置
     */
    public String getEditCellCarrierConfiguration() {
        return getTextContent(mCellCarrierConfiguration);
    }

    /**
     * @return 小区ID
     */
    public String getEditCellId() {
        return getTextContent(mCellId);
    }

    /**
     * @return PCI
     */
    public String getEditCellPCI() {
        return getTextContent(mCellPci);
    }

    /**
     * @return 频段
     */
    public String getEditCellBand() {
        return getTextContent(mCellBand);
    }

    /**
     * @return 主频点
     */
    public String getEditCellFrequency() {
        return getTextContent(mCellFrequency);
    }

    /**
     * @return 小区带宽
     */
    public String getEditCellBandwidth() {
        return getTextContent(mCellBandwidth);
    }


    /**
     * @return 根序列
     */
    public String getEditCellRootSequenceIndex() {
        return getTextContent(mCellRootSequenceIndex);
    }

    /**
     * @return 子帧配比
     */
    public String getEditCellSubframeRatio() {
        return getTextContent(mCellSubframeRatio);
    }

    /**
     * @return 特殊子帧配比
     */
    public String getEditCellSpecialSubframeRatio() {
        return getTextContent(mCellSpecialSubframeRatio);
    }

    /**
     * @return RsPower
     */
    public String getEditRsPower() {
        return getTextContent(mCellRspower);
    }

    /**
     * @return PDCCH
     */
    public String getEditPDCCH() {
        return getTextContent(mCellPdcch);
    }

    /**
     * @return PA
     */
    public String getEditPA() {
        return getTextContent(mCellPa);
    }

    /**
     * @return PB
     */
    public String getEditPB() {
        return getTextContent(mCellPb);
    }

    /**
     * @return 天线挂高
     */
    public String getEditCellAerialHigh() {
        return getTextContent(mCellAerialHigh);
    }


    /**
     * @return 方位角
     */
    public String getEditCellAzimuth() {
        return getTextContent(mCellAzimuth);
    }

    /**
     * @return 总下倾角
     */
    public String getEditCellDownAngle() {
        return getTextContent(mCellDownAngle);
    }

    /**
     * @return 预制电下倾
     */
    public String getEditCellElectricDownAngle() {
        return getTextContent(mCellElectricDownAngle);
    }

    /**
     * @return 机械下倾角
     */
    public String getEditCellMachineDownAngle() {
        return getTextContent(mCellMachineDownAngle);
    }

    /**
     * @return 垂直半功率角
     */
    public String getEditCellVerticalHalfPowerAngle() {
        return getTextContent(mCellVerticalHalfPowerAngle);
    }

    /**
     * @return 水平半功率角
     */
    public String getEditCellHorizontalHalfPowerAngle() {
        return getTextContent(mCellHorizontalHalfPowerAngle);
    }

    /**
     * @return 天线厂家
     */
    public String getEditCellAerialVender() {
        return getTextContent(mCellAerialVender);
    }

    /**
     * @return 天线类型
     */
    public String getEditCellAerialType() {
        return getTextContent(mCellAerialType);
    }


}
