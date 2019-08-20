package com.walktour.gui.singlestation.survey.presenter;

import android.text.TextUtils;

import com.walktour.Utils.TraceInfoInterface;
import com.walktour.base.gui.model.SimpleCallBack;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.model.SurveyCellInfo;
import com.walktour.gui.singlestation.survey.fragment.SurveyCellEditFragment;
import com.walktour.gui.singlestation.survey.service.SurveyService;

/**
 * 基站勘查小区参数编辑交互类
 * Created by wangk on 2017/7/19.
 */

public class SurveyCellEditFragmentPresenter extends SurveyEditBaseFragmentPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "SurveyCellEditFragmentPresenter";
    /**
     * 关联视图
     */
    private SurveyCellEditFragment mFragment;
    /**
     * 当前勘查的小区对象
     */
    private SurveyCellInfo mSurveyCellInfo;

    public SurveyCellEditFragmentPresenter(SurveyCellEditFragment fragment, SurveyService service) {
        super(fragment, service);
        this.mFragment = fragment;
        this.mSurveyCellInfo = this.mFragment.getSurveyCellInfo();
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    @Override
    public void saveEditingData() {
        LogUtil.i(getLogTAG(), "---saveEditingData---");

        String tac = TraceInfoInterface.getParaValue(0x7F060018);
        String pci = TraceInfoInterface.getParaValue(0x7F060002);
        String cellId = TraceInfoInterface.getParaValue(0x0A0050A4);
        String cellCarrierConfiguration = TraceInfoInterface.getParaValue(0x7F060F00);//小区载波配置
        String frequency = TraceInfoInterface.getParaValue(0x7F060016);//主频点
        String eNodeBId = TraceInfoInterface.getParaValue(0x0A0050A2);
        String band = TraceInfoInterface.getParaValue(0x7F06001C);//频段
        String bandWidth = TraceInfoInterface.getParaValue(0x7F060012);//小区带宽
        String RSPower = TraceInfoInterface.getParaValue(0x7F060D8C);
        String pa = TraceInfoInterface.getParaValue(0x7F060D8E);
        String pb = TraceInfoInterface.getParaValue(0x7F060D8D);
        String keyOfSubframeMatching = TraceInfoInterface.getParaValue(0x7F060014);//子帧配比
        String keyOfSpecialSubframeMatching = TraceInfoInterface.getParaValue(0x7F060013);//特殊子帧配比
        String pdcch = TraceInfoInterface.getParaValue(0x7F060098);//PDCCH符号数
        String rootSequence = TraceInfoInterface.getParaValue(0x7F0600A0);//根序列
        //当cellId和参数里面获取的小区id一致时，才将获取到的参数数据写进数据库
        if (!TextUtils.isEmpty(cellId) && mSurveyCellInfo.getCellId() == Integer.parseInt(cellId)) {
//            this.mSurveyCellInfo.setCellId(Integer.parseInt(cellId));
            this.mSurveyCellInfo.setCarrierSetup(cellCarrierConfiguration);//小区载波配置
            this.mSurveyCellInfo.setPCI(TextUtils.isEmpty(pci) ? 0 : Integer.parseInt(pci));
            this.mSurveyCellInfo.setBand(band);//频段
            this.mSurveyCellInfo.setFrequency(TextUtils.isEmpty(frequency) ? 0 : Integer.parseInt(frequency));//主频点
            this.mSurveyCellInfo.setBandwidth(TextUtils.isEmpty(bandWidth) ? 0 : Integer.parseInt(bandWidth));//小区带宽
            this.mSurveyCellInfo.setRootSequence(TextUtils.isEmpty(rootSequence) ? 0 : Integer.parseInt(rootSequence));
            this.mSurveyCellInfo.setSubframeMatching(getSubframeMatchValue(keyOfSubframeMatching));//子帧配比
            this.mSurveyCellInfo.setSpecialSubframeMatching(getSpecialSubframeMatchValue(keyOfSpecialSubframeMatching));//特殊子帧配比
            this.mSurveyCellInfo.setPDCCH(TextUtils.isEmpty(pdcch) ? 0 : Integer.parseInt(pdcch));
            this.mSurveyCellInfo.setRsPower(TextUtils.isEmpty(RSPower) ? 0 : Integer.parseInt(RSPower));
            this.mSurveyCellInfo.setPA(TextUtils.isEmpty(pa) ? 0 : Integer.parseInt(pa));
            this.mSurveyCellInfo.setPB(TextUtils.isEmpty(pb) ? 0 : Integer.parseInt(pb));
        }
        this.mSurveyCellInfo.setAerialHigh(TextUtils.isEmpty(mFragment.getEditCellAerialHigh()) ? 0 : Float.parseFloat(mFragment.getEditCellAerialHigh()));
        this.mSurveyCellInfo.setAzimuth(TextUtils.isEmpty(mFragment.getEditCellAzimuth()) ? 0 : Float.parseFloat(mFragment.getEditCellAzimuth()));
        this.mSurveyCellInfo.setDownAngle(TextUtils.isEmpty(mFragment.getEditCellDownAngle()) ? 0 : Float.parseFloat(mFragment.getEditCellDownAngle()));
        this.mSurveyCellInfo.setElectricDownAngle(TextUtils.isEmpty(mFragment.getEditCellElectricDownAngle()) ? 0 : Float.parseFloat(mFragment.getEditCellElectricDownAngle()));
        this.mSurveyCellInfo.setMachineDownAngle(TextUtils.isEmpty(mFragment.getEditCellMachineDownAngle()) ? 0 : Float.parseFloat(mFragment.getEditCellMachineDownAngle()));
        this.mSurveyCellInfo.setVerticalFalfPowerAngle(TextUtils.isEmpty(mFragment.getEditCellVerticalHalfPowerAngle()) ? 0 : Float.parseFloat(mFragment.getEditCellVerticalHalfPowerAngle()));
        this.mSurveyCellInfo.setHorizontalFalfPowerAngle(TextUtils.isEmpty(mFragment.getEditCellHorizontalHalfPowerAngle()) ? 0 : Float.parseFloat(mFragment.getEditCellHorizontalHalfPowerAngle()));
        this.mSurveyCellInfo.setAerialVender(mFragment.getEditCellAerialVender());
        this.mSurveyCellInfo.setAerialType(mFragment.getEditCellAerialType());

        this.mService.saveSurveyCellEdit(this.mSurveyCellInfo, new SimpleCallBack() {
            @Override
            public void onSuccess() {
                showToast(R.string.single_station_survey_save_success);
            }

            @Override
            public void onFailure(String message) {
                showToast(R.string.single_station_survey_save_failure);
            }
        });
    }

    @Override
    public void loadData() {
        this.mFragment.show();
    }
}
