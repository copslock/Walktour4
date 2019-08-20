package com.walktour.gui.map.celllist;

import android.content.Context;
import android.util.AttributeSet;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.UnifyParaID;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.gui.R;
import com.walktour.gui.map.TraceInfoData;

/**
 * CatM网络的邻区列表显示
 * 
 * @author yi.lin
 *
 */
public class CatMCellListView extends BaseCellListView {

	public CatMCellListView(Context context) {
		super(context, "CatM", BaseStation.NETTYPE_CAT_M, -40, -141);
	}

	public CatMCellListView(Context context, AttributeSet attrs) {
		super(context, attrs, "CatM", BaseStation.NETTYPE_CAT_M, -40, -141);
	}

	@Override
	protected void parseCellValues(TraceInfoData traceData) {
		String[] neighbors = getParaValue(UnifyParaID.LTE_CELL_LIST).split(";");
		this.cellList.clear();
		for (String neighbor : neighbors) {
			if (neighbor.trim().length() == 0)
				continue;
			String[] values = neighbor.split(",", -1);
			if (values.length >= 5) {
				CellParams cellParam = new CellParams();
				cellParam.tableValues = new String[] { super.getInt(values[0]), super.getInt(values[1]), values[3], values[4] };
				cellParam.histogramValue = super.getDouble(values[2]);
				cellParam.type = this.isServerCell(cellParam) ? "S" : "N";
				this.cellList.add(cellParam);
			}
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
		String serEarfcn = getParaValue(UnifyParaID.NB_SRV_EARFCN);
		String serPci = getParaValue(UnifyParaID.NB_SRV_PCI);
		if (!StringUtil.isNullOrEmpty(serEarfcn) && !StringUtil.isNullOrEmpty(serPci)) {
			if (serEarfcn.equals(cellParam.tableValues[0]) && serPci.equals(cellParam.tableValues[1]))
				return true;
		}
		return false;
	}

	@Override
	protected String[] getHeaders() {
		return new String[] { "EARFCN", "PCI", "RSRQ", "RSSI", "RSRP" };
	}

	@Override
	protected int[] getCellParamIndexs() {
		return new int[] { 0, 1 };
	}

	@Override
	protected String[] getCellParamNames() {
		return new String[] { "eafrcn", "pci" };
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
