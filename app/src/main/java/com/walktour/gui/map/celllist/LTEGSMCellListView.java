package com.walktour.gui.map.celllist;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UnifyStruct;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.gui.R;
import com.walktour.gui.map.TraceInfoData;
import com.walktour.model.LteGsmStructModel;

import java.text.DecimalFormat;
import java.util.List;

/**
 * LTE网络的GSM邻区列表显示
 *
 * @author Yi.Lin
 * @date 2018/7/17
 */
public class LTEGSMCellListView extends BaseCellListView {

    public LTEGSMCellListView(Context context) {
        super(context, "GSM", BaseStation.NETTYPE_LTE, 100, -50);
        this.tableRows = 25;
    }

    public LTEGSMCellListView(Context context, AttributeSet attrs) {
        super(context, attrs, "GSM", BaseStation.NETTYPE_LTE, 100, -50);
        this.tableRows = 25;
    }

    @Override
    protected void parseCellValues(TraceInfoData traceData) {

        UnifyStruct.LTE_GSM lteGsm = (UnifyStruct.LTE_GSM) TraceInfoInterface.getParaStruct(UnifyParaID.LTE_GSM_CELL_LIST);
        String lteGsmData = "";
        if (lteGsm != null) {
            lteGsmData = lteGsm.lteGsm;
        }
        this.cellList.clear();
        if (TextUtils.isEmpty(lteGsmData)) {
            return;
        }
        List<LteGsmStructModel> list = new Gson().fromJson(lteGsmData, new TypeToken<List<LteGsmStructModel>>() {
        }.getType());
        if (null == list || list.isEmpty()) {
            return;
        }
        DecimalFormat df = new DecimalFormat(".00");
        for (LteGsmStructModel model : list) {
            CellParams cellParam = new CellParams();
            cellParam.tableValues = new String[]{String.valueOf(model.getArfcn()), getGSMBandDesc(model.getGsmBand()),
                    String.valueOf(model.getLnaState()), String.valueOf(df.format(model.getRssi()))};
            cellParam.histogramValue = model.getSrxLev();
            cellParam.type = this.isServerCell(cellParam) ? "S" : "N";
            this.cellList.add(cellParam);
        }
    }

    /**
     * 获取gsmBand对应的解析字符串
     * @param gsmBand
     * @return
     */
    private String getGSMBandDesc(byte gsmBand) {
        switch (gsmBand) {
            case 1:
                return "GSM 900";
            case 2:
                return "DCS 1800";
            case 3:
                return "PCS 1900";
            case 4:
                return "GSM 850";
            case 5:
                return "GSM 450";
            default:
                return "";
        }
    }



    /**
     * 是否服务小区
     *
     * @param cellParam 小区参数
     * @return
     */
    private boolean isServerCell(CellParams cellParam) {
        String serEarfcn = getParaValue(UnifyParaID.L_SRV_EARFCN);
        String serPci = getParaValue(UnifyParaID.L_SRV_PCI);
        if (!StringUtil.isNullOrEmpty(serEarfcn) && !StringUtil.isNullOrEmpty(serPci)) {
            if (serEarfcn.equals(cellParam.tableValues[0]) && serPci.equals(cellParam.tableValues[1]))
                return true;
        }
        return false;
    }

    @Override
    protected float getColsTextWidthTotal() {
        return this.getWidth() * 2 / 3;
    }

    @Override
    protected String[] getHeaders() {
        return new String[]{"ARFCN", "GSMBand", "LNAState", "RSSI", "SrxLev"};
    }

    @Override
    protected int[] getCellParamIndexs() {
        return new int[]{0, 1};
    }

    @Override
    protected String[] getCellParamNames() {
        return new String[]{"eafrcn", "pci"};
    }

    @Override
    protected int getColor(CellParams param) {
        if (param.type.equals("S"))
            return R.color.light_orange;
        else if (param.type.equals("N"))
            return R.color.light_blue;
        return R.color.info_param_color;
    }

}
