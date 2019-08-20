package com.walktour.gui.map.celllist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UnifyStruct;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.gui.R;
import com.walktour.gui.map.TraceInfoData;
import com.walktour.model.CellInfo;
import com.walktour.model.T5GNRCellBlock;
import com.walktour.model.T5GSingleBeamInfo;

/**
 * LTE网络的邻区列表显示
 * 
 * @author jianchao.wang
 *
 */
public class ENDCCellListView extends BaseCellListView {

	public ENDCCellListView(Context context) {
		super(context, "NR", BaseStation.NETTYPE_ENDC, -40, -141);
	}

	public ENDCCellListView(Context context, AttributeSet attrs) {
		super(context, attrs, "NR", BaseStation.NETTYPE_ENDC, -40, -141);
	}

	@Override
	protected void parseCellValues(TraceInfoData traceData) {
		UnifyStruct.ENDC_CELL_LIST endc_cell_list = (UnifyStruct.ENDC_CELL_LIST) TraceInfoInterface.getParaStruct(UnifyParaID.ENDC_CELL_LIST);
		this.cellList.clear();
		if(null==endc_cell_list)
			return;
		for (T5GNRCellBlock t5GNRCellBlock : endc_cell_list.modelList) {
//			if (neighbor.trim().length() == 0)
//				continue;
//			String[] values = neighbor.split(",", -1);
//			if (values.length >= 5) {
			for(T5GSingleBeamInfo t5GSingleBeamInfo:t5GNRCellBlock.modelList) {
				CellParams cellParam = new CellParams();
				cellParam.tableValues = new String[]{t5GNRCellBlock.NRARFCN+"", t5GNRCellBlock.PCI+"",t5GSingleBeamInfo.getSSBIndex()+"", (int)t5GSingleBeamInfo.getRSRQ()+"",(int)t5GSingleBeamInfo.getSINR()+""};
				cellParam.histogramValue = super.getDouble((int)t5GSingleBeamInfo.getRSRP()+"");
				cellParam.type = this.isServerCell(cellParam) ? "S" : "N";
				this.cellList.add(cellParam);
			}
//			}
		}
	}

	protected float getColsTextWidthTotal() {
		return this.getWidth() / 6 * 4;
	}

	/**
	 * 创建表格头
	 *
	 * @param canvas
	 *          画板
	 * @return
	 */
	@Override
	protected void createTableHeader(Canvas canvas) {
		// 四周边框
//		canvas.drawLine(1, 1, this.getWidth() - 1, 1, linePaint);
//		canvas.drawLine(1, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, linePaint);
//		canvas.drawLine(1, 1, 1, this.getHeight() - 1, linePaint);
//		canvas.drawLine(this.getWidth() - 1, 1, this.getWidth() - 1, this.getHeight() - 1, linePaint);
		linePaint.setColor(Color.parseColor("#0074CC"));
		float colsTextWidthTotal = this.getColsTextWidthTotal();
		float colsTextWidth = colsTextWidthTotal / (this.getHeaders().length - 1);
		String[] headers = this.getHeaders();
		for (int i = 0; i < headers.length - 1; i++) {
			float textSize = fontPaint.measureText(headers[i]);
			canvas.drawText(headers[i], colsTextWidth * i
							+ ((colsTextWidth - textSize) / 2 <= 0 ? 0 : (colsTextWidth - textSize) / 2), rowsHeight * 3 - rowUpBit * 3,
					fontPaint);
		}
		// 绘制最后一列标题
		canvas.drawText(headers[headers.length - 1], colsTextWidthTotal, rowsHeight * 3 - rowUpBit * 3, fontPaint);

		// 画横线
		canvas.drawLine(0, (rowsHeight * 3 - rowUpBit * 2) + 5, getWidth(), (rowsHeight * 3 - rowUpBit * 2) + 5, linePaint);
		// 画中间线
		canvas.drawLine(colsTextWidthTotal, (rowsHeight * 3 - rowUpBit * 2) + 5, colsTextWidthTotal, getHeight() - 1,
				linePaint);
		canvas.save();
		canvas.restore();
	}

	/**
	 * 生成表格内容
	 *
	 * @param canvas
	 *          画图
	 * @param traceData
	 *          数据
	 */
	protected void createTableContent(Canvas canvas, TraceInfoData traceData) {
		this.parseCellValues(traceData);
		float top;
		float left;
		float right;
		float bottom;
		float x;
		float y;
		float colsTextWidthTotal = this.getColsTextWidthTotal();
		float colsTextWidth = colsTextWidthTotal / (this.getHeaders().length - 1);
		for (int row = 0; row < this.cellList.size(); row++) {
			CellParams cellParams = this.cellList.get(row);
			y = this.rowsHeight * (4 + row * 2) - rowUpBit;
			// 绘制除最后一列外的表格数据
			for (int col = 0; col < cellParams.tableValues.length; col++) {
				x = colsTextWidth * col + (colsTextWidth - paramPaint.measureText(cellParams.tableValues[col])) / 2;
				canvas.drawText(cellParams.tableValues[col], x, y, paramPaint);
			}
			// 绘制最后一列的图形
			if (cellParams.histogramValue != 0) {
				// 绘制背景灰条
				paramPaint.setColor(Color.TRANSPARENT);
				left = colsTextWidthTotal;
				top = rowsHeight * (3 + row * 2) + 5;
				bottom = rowsHeight * (4 + row * 2) - 3;
				right = this.getWidth() - super.systemScale * 10;
				canvas.drawRect(left, top, right, bottom, paramPaint);
				// 绘制数值显示条
				paramPaint.setColor(getResources().getColor(this.getColor(cellParams)));
				right = colsTextWidthTotal + this.getHistogramWidth(cellParams.histogramValue);
				canvas.drawRect(left, top, right, bottom, paramPaint);
				// 绘制数值说明文字
				paramPaint.setColor(getResources().getColor(R.color.info_param_color));
				String value = cellParams.histogramValue + cellParams.histogramUnit;
				x = colsTextWidthTotal + (this.getWidth() - colsTextWidthTotal - fontPaint.measureText(value)) / 2;
				canvas.drawText(value, x, y, fontPaint);
			}
			// 绘制小区名称
			CellInfo cell = this.getCellInfo(cellParams, traceData);
			if (cell != null) {
				y = rowsHeight * (4 + row * 2) + textSize;
				if (!StringUtil.isNullOrEmpty(cell.getCellId())) {
					String value = "CI:" + cell.getCellId();
					x = colsTextWidthTotal - 10 - fontPaint.measureText(value);
					canvas.drawText(value, x, y, fontPaint);
				}
				if (!StringUtil.isNullOrEmpty(cell.getCellName())) {
					x = colsTextWidthTotal + 10;
					canvas.drawText(cell.getCellName(), x, y, fontPaint);
				}
			}
		}
		canvas.save();
		canvas.restore();
	}

	/**
	 * 是否服务小区
	 *
	 * @param cellParam
	 *          小区参数
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
	protected String[] getHeaders() {
		return new String[] { "NARFCN", "PCI","SSB", "RSRQ", "SINR", "RSRP" };
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
