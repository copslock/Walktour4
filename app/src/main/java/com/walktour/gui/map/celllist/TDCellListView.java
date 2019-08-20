package com.walktour.gui.map.celllist;

import android.content.Context;
import android.util.AttributeSet;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.UnifyParaID;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.gui.R;
import com.walktour.gui.map.TraceInfoData;

/**
 * TD-SCDMA网络的邻区列表显示
 * 
 * @author jianchao.wang
 *
 */
public class TDCellListView extends BaseCellListView {

	public TDCellListView(Context context) {
		super(context, "TD-SCDMA", BaseStation.NETTYPE_TDSCDMA, -25, -115);
	}

	public TDCellListView(Context context, AttributeSet attrs) {
		super(context, attrs, "TD-SCDMA", BaseStation.NETTYPE_TDSCDMA, -25, -115);
	}

	@Override
	protected void parseCellValues(TraceInfoData traceData) {
		String[][] datas = new String[][] {
				{ getParaValue(UnifyParaID.TD_Ser_UARFCN), getParaValue(UnifyParaID.TD_Ser_CPI),
						getParaValue(UnifyParaID.TD_Ser_PCCPCHPathloss), getParaValue(UnifyParaID.TD_Ser_CarrierRSSI),
						getParaValue(UnifyParaID.TD_Served_RS), getParaValue(UnifyParaID.TD_Ser_PCCPCHISCP),
						getParaValue(UnifyParaID.TD_Ser_PCCPCHRSCP) },
				{ getParaValue(UnifyParaID.T_NCell_N6_UARFCN), getParaValue(UnifyParaID.T_NCell_N6_CPI),
						getParaValue(UnifyParaID.T_NCell_N6_PathLoss), getParaValue(UnifyParaID.T_NCell_N6_CarrierRSSI),
						getParaValue(UnifyParaID.T_NCell_N6_Rn), getParaValue(UnifyParaID.T_NCell_N6_ISCP),
						getParaValue(UnifyParaID.T_NCell_N6_RSCP) },
				{ getParaValue(UnifyParaID.T_NCell_N5_UARFCN), getParaValue(UnifyParaID.T_NCell_N5_CPI),
						getParaValue(UnifyParaID.T_NCell_N5_PathLoss), getParaValue(UnifyParaID.T_NCell_N5_CarrierRSSI),
						getParaValue(UnifyParaID.T_NCell_N5_Rn), getParaValue(UnifyParaID.T_NCell_N5_ISCP),
						getParaValue(UnifyParaID.T_NCell_N5_RSCP) },
				{ getParaValue(UnifyParaID.T_NCell_N4_UARFCN), getParaValue(UnifyParaID.T_NCell_N4_CPI),
						getParaValue(UnifyParaID.T_NCell_N4_PathLoss), getParaValue(UnifyParaID.T_NCell_N4_CarrierRSSI),
						getParaValue(UnifyParaID.T_NCell_N4_Rn), getParaValue(UnifyParaID.T_NCell_N4_ISCP),
						getParaValue(UnifyParaID.T_NCell_N4_RSCP) },
				{ getParaValue(UnifyParaID.T_NCell_N3_UARFCN), getParaValue(UnifyParaID.T_NCell_N3_CPI),
						getParaValue(UnifyParaID.T_NCell_N3_PathLoss), getParaValue(UnifyParaID.T_NCell_N3_CarrierRSSI),
						getParaValue(UnifyParaID.T_NCell_N3_Rn), getParaValue(UnifyParaID.T_NCell_N3_ISCP),
						getParaValue(UnifyParaID.T_NCell_N3_RSCP) },
				{ getParaValue(UnifyParaID.T_NCell_N2_UARFCN), getParaValue(UnifyParaID.T_NCell_N2_CPI),
						getParaValue(UnifyParaID.T_NCell_N2_PathLoss), getParaValue(UnifyParaID.T_NCell_N2_CarrierRSSI),
						getParaValue(UnifyParaID.T_NCell_N2_Rn), getParaValue(UnifyParaID.T_NCell_N2_ISCP),
						getParaValue(UnifyParaID.T_NCell_N2_RSCP) },
				{ getParaValue(UnifyParaID.T_NCell_N1_UARFCN), getParaValue(UnifyParaID.T_NCell_N1_CPI),
						getParaValue(UnifyParaID.T_NCell_N1_PathLoss), getParaValue(UnifyParaID.T_NCell_N1_CarrierRSSI),
						getParaValue(UnifyParaID.T_NCell_N1_Rn), getParaValue(UnifyParaID.T_NCell_N1_ISCP),
						getParaValue(UnifyParaID.T_NCell_N1_RSCP) } };
		this.cellList.clear();
		for (String[] values : datas) {
			if (values[0] == null || values[0].trim().length() == 0)
				continue;
			CellParams cellParam = new CellParams();
			cellParam.tableValues = new String[] { super.getInt(values[0]), super.getInt(values[1]), values[2], values[3],
					values[4], values[5] };
			cellParam.histogramValue = super.getDouble(values[6]);
			cellParam.type = this.isServerCell(cellParam) ? "S" : "N";
			this.cellList.add(cellParam);
		}
	}

	@Override
	protected float getColsTextWidthTotal() {
		return this.getWidth() * 2 / 3;
	}

	/**
	 * 是否服务小区
	 * 
	 * @param cellParam
	 *          小区参数
	 * @return
	 */
	private boolean isServerCell(CellParams cellParam) {
		String serUarfcn = getParaValue(UnifyParaID.TD_Ser_UARFCN);
		String serCpi = getParaValue(UnifyParaID.TD_Ser_CPI);
		if (!StringUtil.isNullOrEmpty(serUarfcn) && !StringUtil.isNullOrEmpty(serCpi)) {
			if (serUarfcn.equals(cellParam.tableValues[0]) && serCpi.equals(cellParam.tableValues[1]))
				return true;
		}
		return false;
	}

	@Override
	protected String[] getHeaders() {
		return new String[] { "UARFCN", "CPI", "PL", "RSSI", "RN", "ISCP", "RSCP" };
	}

	@Override
	protected int[] getCellParamIndexs() {
		return new int[] { 0, 1 };
	}

	@Override
	protected String[] getCellParamNames() {
		return new String[] { "uarfcn", "cpi" };
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
