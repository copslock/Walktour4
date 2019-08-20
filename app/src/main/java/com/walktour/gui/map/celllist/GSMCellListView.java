package com.walktour.gui.map.celllist;

import android.content.Context;
import android.util.AttributeSet;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.UnifyParaID;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.gui.R;
import com.walktour.gui.map.TraceInfoData;

/**
 * GSM网络的邻区列表显示
 */
public class GSMCellListView extends BaseCellListView {

	public GSMCellListView(Context context) {
		super(context, "GSM", BaseStation.NETTYPE_GSM, -47, -110);
	}

	public GSMCellListView(Context context, AttributeSet attrs) {
		super(context, attrs, "GSM", BaseStation.NETTYPE_GSM, -47, -110);
	}

	@Override
	protected void parseCellValues(TraceInfoData traceData) {
		String[][] datas = new String[][] {
				{ getParaValue(UnifyParaID.G_Ser_BCCH), getParaValue(UnifyParaID.G_Ser_BSIC),
						getParaValue(UnifyParaID.G_Ser_C1), getParaValue(UnifyParaID.G_Ser_C2),
						getParaValue(UnifyParaID.G_Ser_BCCHLev) },
				{ getParaValue(UnifyParaID.G_NCell_N1_BCCH), getParaValue(UnifyParaID.G_NCell_N1_BSIC),
						getParaValue(UnifyParaID.G_NCell_N1_C1), getParaValue(UnifyParaID.G_NCell_N1_C2),
						getParaValue(UnifyParaID.G_NCell_N1_RxLevel) },
				{ getParaValue(UnifyParaID.G_NCell_N2_BCCH), getParaValue(UnifyParaID.G_NCell_N2_BSIC),
						getParaValue(UnifyParaID.G_NCell_N2_C1), getParaValue(UnifyParaID.G_NCell_N2_C2),
						getParaValue(UnifyParaID.G_NCell_N2_RxLevel) },
				{ getParaValue(UnifyParaID.G_NCell_N3_BCCH), getParaValue(UnifyParaID.G_NCell_N3_BSIC),
						getParaValue(UnifyParaID.G_NCell_N3_C1), getParaValue(UnifyParaID.G_NCell_N3_C2),
						getParaValue(UnifyParaID.G_NCell_N3_RxLevel) },
				{ getParaValue(UnifyParaID.G_NCell_N4_BCCH), getParaValue(UnifyParaID.G_NCell_N4_BSIC),
						getParaValue(UnifyParaID.G_NCell_N4_C1), getParaValue(UnifyParaID.G_NCell_N4_C2),
						getParaValue(UnifyParaID.G_NCell_N4_RxLevel) },
				{ getParaValue(UnifyParaID.G_NCell_N5_BCCH), getParaValue(UnifyParaID.G_NCell_N5_BSIC),
						getParaValue(UnifyParaID.G_NCell_N5_C1), getParaValue(UnifyParaID.G_NCell_N5_C2),
						getParaValue(UnifyParaID.G_NCell_N5_RxLevel) },
				{ getParaValue(UnifyParaID.G_NCell_N6_BCCH), getParaValue(UnifyParaID.G_NCell_N6_BSIC),
						getParaValue(UnifyParaID.G_NCell_N6_C1), getParaValue(UnifyParaID.G_NCell_N6_C2),
						getParaValue(UnifyParaID.G_NCell_N6_RxLevel) } };
		this.cellList.clear();
		for (String[] values : datas) {
			if (values[0] == null || values[0].trim().length() == 0)
				continue;
			CellParams cellParam = new CellParams();
			cellParam.tableValues = new String[] { super.getInt(values[0]), super.getInt(values[1]), values[2], values[3] };
			cellParam.histogramValue = super.getDouble(values[4]);
			cellParam.type = this.isServerCell(cellParam) ? "S" : "N";
			this.cellList.add(cellParam);
		}
	}

	/**
	 * 是否服务小区
	 * 
	 * @param cellParam
	 *          小区参数
	 * @return
	 */
	private boolean isServerCell(CellParams cellParam) {
		String serBcch = getParaValue(UnifyParaID.G_Ser_BCCH);
		String serBsic = getParaValue(UnifyParaID.G_Ser_BSIC);
		if (!StringUtil.isNullOrEmpty(serBcch) && !StringUtil.isNullOrEmpty(serBsic)) {
			if (serBcch.equals(cellParam.tableValues[0]) && serBsic.equals(cellParam.tableValues[1]))
				return true;
		}
		return false;
	}

	@Override
	protected String[] getHeaders() {
		return new String[] { "BCCH", "BSIC", "C1", "C2", "Rxlev" };
	}

	@Override
	protected int[] getCellParamIndexs() {
		return new int[] { 0, 1 };
	}

	@Override
	protected String[] getCellParamNames() {
		return new String[] { "bcch", "bsic" };
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
