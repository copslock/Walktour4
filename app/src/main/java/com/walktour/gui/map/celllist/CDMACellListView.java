package com.walktour.gui.map.celllist;

import android.content.Context;
import android.util.AttributeSet;

import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethodPara;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.gui.R;
import com.walktour.gui.map.TraceInfoData;

/**
 * CDMA网络的邻区列表显示
 * 
 * @author jianchao.wang
 *
 */
public class CDMACellListView extends BaseCellListView {

	public CDMACellListView(Context context) {
		super(context, "CDMA", BaseStation.NETTYPE_CDMA, 0, -32);
	}

	public CDMACellListView(Context context, AttributeSet attrs) {
		super(context, attrs, "CDMA", BaseStation.NETTYPE_CDMA, 0, -32);
	}

	@Override
	protected void parseCellValues(TraceInfoData traceData) {
		String[] neighbors = getParaValue(UnifyParaID.C_cdmaServingNeighbor).split(";");
		this.cellList.clear();
		for (String neighbor : neighbors) {
			if (neighbor.trim().length() == 0)
				continue;
			String[] values = neighbor.split(",", -1);
			if (values.length >= 6) {
				CellParams cellParam = new CellParams();
				cellParam.tableValues = new String[] { super.getInt(values[1]), super.getInt(values[2]), values[4],
						UtilsMethodPara.getEvdoSetType(values[0]) };
				cellParam.histogramValue = super.getDouble(values[5]);
				cellParam.type = UtilsMethodPara.getEvdoSetType(values[0]);
				this.cellList.add(cellParam);
			}
		}
	}

	@Override
	protected String[] getHeaders() {
		return new String[] { "Freq", "PN", "Ec", "Set", "Ec/Io" };
	}

	@Override
	protected int[] getCellParamIndexs() {
		return new int[] { 0, 1 };
	}

	@Override
	protected String[] getCellParamNames() {
		return new String[] { "frequency", "pn" };
	}

	@Override
	protected int getColor(CellParams param) {
		if (param.type.equals("A"))
			return R.color.light_orange;
		else if (param.type.equals("M"))
			return R.color.light_blue;
		else if (param.type.equals("N"))
			return R.color.light_red;
		else if (param.type.equals("D"))
			return R.color.light_green;
		return R.color.info_param_color;
	}

}
