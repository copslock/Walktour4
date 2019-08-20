package com.walktour.gui.map;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UnifyStruct;
import com.walktour.Utils.UnifyStruct.Channel;
import com.walktour.Utils.UnifyStruct.WCDMATrCHDLConfiguration;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.UtilsMethodPara;
import com.walktour.Utils.WalktourConst;
import com.walktour.framework.view.BasicParamView;
import com.walktour.framework.view.CheckCellParamThread;
import com.walktour.framework.view.ViewSizeLinstener;
import com.walktour.gui.R;
import com.walktour.model.CellInfo;

import java.util.ArrayList;

public class WcdmaView extends BasicParamView {
	// private static final String TAG = "WcdmaView";
	private int currentPage = 1;

	private int tableCols = 6; // 列数

	private float rowsHeight;

	private float rowUpBit;

	private String[] servingNeighbor;
	/**
	 * View对象高度
	 */
	private int viewHeight;

	private ViewSizeLinstener viewSizeLinstener;

	public WcdmaView(Context context) {
		super(context);
	}

	public WcdmaView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WcdmaView(Context context, int page) {
		super(context);
		currentPage = page;
	}

	public WcdmaView(Context context, int page, ViewSizeLinstener viewSizeLinstener) {
		super(context);
		currentPage = page;
		this.viewSizeLinstener = viewSizeLinstener;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param canvas
	 * @see com.walktour.framework.view.BasicParamView#initView(android.graphics.Canvas)
	 */
	@Override
	public void initView(Canvas canvas) {
		CreateTable(canvas);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		CreateTableData(canvas, TraceInfoInterface.traceData);
	}

	/**
	 * 创建表格
	 * 
	 * @param bm
	 *          要创建表格的位图
	 * @return 输出位图
	 */
	protected void CreateTable(Canvas cv) {
		float startx = 1;
		float starty = 0;
		float stopx = 0;
		float stopy = 0;
		float colsWidth = this.getWidth() / tableCols; // 列宽
		float neiColsWith = (this.getWidth() - colsWidth) / tableCols;// 邻近单元C*宽度
		rowUpBit = (rowsHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2

		if (currentPage == 1) {
			tableRows = 19;
			if (viewHeight == 0) {
				viewHeight = this.getViewHeight() - 1;
			}
			rowsHeight = viewHeight / tableRows;
			rowUpBit = (rowsHeight - textSize) / 2;
			// 回调界面大小
			viewSizeLinstener.onViewSizeChange(viewHeight, this.getWidth());

			// 四周边框
			cv.drawLine(1, 1, this.getWidth() - 1, 1, linePaint);
			cv.drawLine(1, rowsHeight * (tableRows - 9), this.getWidth() - 1, rowsHeight * (tableRows - 9), linePaint);
			cv.drawLine(1, 1, 1, rowsHeight * (tableRows - 9), linePaint);
			cv.drawLine(this.getWidth() - 1, 1, this.getWidth() - 1, rowsHeight * (tableRows - 9), linePaint);

			// 横线
			for (int i = 0; i < (tableRows - 9); i++) {
				startx = 1;
				starty = rowsHeight * (i + 1);
				stopx = this.getWidth() - 1;
				stopy = rowsHeight * (i + 1);
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
			// Serving cell 分页竖线
			// {
			// startx = colsWidth * 5 + lableAddWidth;
			// starty = marginSize;
			// stopx = colsWidth * 5 + lableAddWidth;
			// stopy = rowsHeight ;
			// cv.drawLine(startx, starty, stopx, stopy, paint);
			// }
			// Serving cell竖线
			startx = this.getWidth() / 2;
			starty = rowsHeight * 1;
			stopx = startx;
			stopy = rowsHeight * 9;
			cv.drawLine(startx, starty, stopx, stopy, linePaint);

			/*
			 * for(int i=0;i<tableCols - 1;i++){ startx = colsWidth * (i+1) + (i%2==0
			 * ? lableAddWidth:0); starty = rowsHeight; stopx = colsWidth * (i+1) +
			 * (i%2==0 ? lableAddWidth:0); stopy = rowsHeight * 7 ;
			 * cv.drawLine(startx, starty, stopx, stopy, paint); }
			 */

			String paraname;
			paraname = getContext().getString(R.string.wcdma_serving_cell_info_1);// "Serving
																																						// Cell
																																						// Info";
			cv.drawText(paraname, (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit,
					fontPaint);

			int[] values = new int[] { R.string.wcdma_mcc_mnc, R.string.wcdma_lac, R.string.wcdma_ul_dl_uarfcn,
					R.string.wcdma_rnc_id_cell_id, R.string.wcdma_max_psc, R.string.wcdma_bler, R.string.wcdma_max_rscp,
					R.string.wcdma_total_rscp, R.string.wcdma_max_ecio, R.string.wcdma_total_ecio, R.string.wcdma_rxpower,
					R.string.wcdma_sir, R.string.wcdma_txpower, R.string.wcdma_rrc_state, R.string.dl_amr_codec,
					R.string.ul_amr_codec, R.string.wcdma_cell_name };

			for (int i = 0, j = 2; i < values.length; i += 2, j++) {
				paraname = getContext().getString(values[i]);
				cv.drawText(paraname, marginSize, rowsHeight * j - rowUpBit, fontPaint);
				if (i + 1 < values.length) {
					paraname = getContext().getString(values[i + 1]);
					cv.drawText(paraname, this.getWidth() / 2 + marginSize, rowsHeight * j - rowUpBit, fontPaint);
				}
			}

		} else if (currentPage == 2) {
			tableRows = 8;
			rowsHeight = this.getViewHeight() / tableRows;
			rowUpBit = (rowsHeight - textSize) / 2;

			// 四周边框
			cv.drawLine(1, 1, this.getWidth() - 1, 1, linePaint);
			cv.drawLine(1, rowsHeight * (tableRows - 1), this.getWidth() - 1, rowsHeight * (tableRows - 1), linePaint);
			cv.drawLine(1, 1, 1, rowsHeight * (tableRows - 1), linePaint);
			cv.drawLine(this.getWidth() - 1, 1, this.getWidth() - 1, rowsHeight * (tableRows - 1), linePaint);

			// 横线
			for (int i = 0; i < tableRows - 1; i++) {
				startx = 1;
				starty = rowsHeight * (i + 1);
				stopx = this.getWidth() - 1;
				stopy = rowsHeight * (i + 1);
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}

			// Neighbor Cell 第一竖线
			{
				startx = colsWidth;
				starty = rowsHeight * 1;
				stopx = colsWidth;
				stopy = rowsHeight * 7;
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}

			// Neighbor Cell竖线
			for (int i = 0; i < tableCols - 1; i++) {
				startx = colsWidth + neiColsWith * (i + 1);
				starty = rowsHeight * 1;
				stopx = colsWidth + neiColsWith * (i + 1);
				stopy = rowsHeight * 7;
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}

			String paraname;

			paraname = getContext().getString(R.string.wcdma_set_cell_info);
			cv.drawText(paraname, (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit,
					fontPaint);

			paraname = getContext().getString(R.string.wcdma_fre);
			cv.drawText(paraname, marginSize, rowsHeight * 2 - rowUpBit, fontPaint);

			paraname = getContext().getString(R.string.wcdma_psc);
			cv.drawText(paraname, marginSize, rowsHeight * 3 - rowUpBit, fontPaint);

			paraname = getContext().getString(R.string.wcdma_cell_id);
			cv.drawText(paraname, marginSize, rowsHeight * 4 - rowUpBit, fontPaint);

			paraname = getContext().getString(R.string.wcdma_rscp);
			cv.drawText(paraname, marginSize, rowsHeight * 5 - rowUpBit, fontPaint);

			paraname = getContext().getString(R.string.wcdma_ecio);
			cv.drawText(paraname, marginSize, rowsHeight * 6 - rowUpBit, fontPaint);

			paraname = getContext().getString(R.string.wcdma_set);
			cv.drawText(paraname, marginSize, rowsHeight * 7 - rowUpBit, fontPaint);

			/*
			 * paraname = getContext().getString(R.string.wcdma_g1);
			 * cv.drawText(paraname, marginSize , rowsHeight * 8 - rowUpBit,
			 * fontPaint);
			 */

			/*
			 * paraname = getContext().getString(R.string.wcdma_g2);
			 * cv.drawText(paraname, marginSize , rowsHeight * 9 - rowUpBit,
			 * fontPaint);
			 */

		} else if (currentPage == 3) {

		} else if (currentPage == 4) {

			rowsHeight = this.getViewHeight() / tableRows;
			rowUpBit = (rowsHeight - textSize) / 2;

			// 四周边框
			cv.drawLine(1, 1, this.getWidth() - 1, 1, linePaint);
			cv.drawLine(1, rowsHeight * 12, this.getWidth() - 1, rowsHeight * 12, linePaint);
			cv.drawLine(1, 1, 1, rowsHeight * 12, linePaint);
			cv.drawLine(this.getWidth() - 1, 1, this.getWidth() - 1, rowsHeight * 12, linePaint);
			// 横线
			for (int i = 0; i < tableRows - 7; i++) {
				startx = 0;
				starty = rowsHeight * (i + 1);
				stopx = this.getWidth() - 1;
				stopy = rowsHeight * (i + 1);
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}

			// WCDMA RACH-2 竖线
			startx = this.getWidth() / 2;
			starty = rowsHeight * 1;
			stopx = startx;
			stopy = rowsHeight * 5;
			cv.drawLine(startx, starty, stopx, stopy, linePaint);

			String paraname;
			// DownLink Trch Bler Meas.Info

			paraname = getContext().getString(R.string.wcdma_wcdma_rach);
			cv.drawText(paraname, (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 1 - rowUpBit,
					fontPaint);

			int[] values = new int[] { R.string.wcdma_message_length, R.string.wcdma_aich_status,
					R.string.wcdma_preambles_num, R.string.wcdma_access_slot, R.string.wcdma_last_preamble_signature,
					R.string.wcdma_sfn, R.string.wcdma_rf_tx_power, R.string.wcdma_aich_timing };

			for (int i = 0, j = 2; i < values.length; i += 2, j++) {
				paraname = getContext().getString(values[i]);
				cv.drawText(paraname, marginSize, rowsHeight * j - rowUpBit, fontPaint);
				if (i + 1 < values.length) {
					paraname = getContext().getString(values[i + 1]);
					cv.drawText(paraname, this.getWidth() / 2 + marginSize, rowsHeight * j - rowUpBit, fontPaint);
				}
			}

			// WCDMA PRACH-2 竖线
			startx = this.getWidth() / 2;
			starty = rowsHeight * 6;
			stopx = startx;
			stopy = rowsHeight * 10;
			cv.drawLine(startx, starty, stopx, stopy, linePaint);

			// DownLink Trch Bler Meas.Info

			paraname = getContext().getString(R.string.wcdma_wcdma_prach_2);
			cv.drawText(paraname, (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 6 - rowUpBit,
					fontPaint);

			values = new int[] { R.string.wcdma_max_txpower, R.string.wcdma_transport_chan_id, R.string.wcdma_min_sf_for_rach,
					R.string.wcdma_pwr_ramp_step, R.string.wcdma_sc_index, R.string.wcdma_max_preamble_trans,
					R.string.wcdma_ul_punctuing_limit, R.string.wcdma_ul_interference };

			for (int i = 0, j = 7; i < values.length; i += 2, j++) {
				paraname = getContext().getString(values[i]);
				cv.drawText(paraname, marginSize, rowsHeight * j - rowUpBit, fontPaint);
				if (i + 1 < values.length) {
					paraname = getContext().getString(values[i + 1]);
					cv.drawText(paraname, this.getWidth() / 2 + marginSize, rowsHeight * j - rowUpBit, fontPaint);
				}
			}

			// power control
			startx = this.getWidth() / 2;
			starty = rowsHeight * 11;
			stopx = startx;
			stopy = rowsHeight * 12;
			cv.drawLine(startx, starty, stopx, stopy, linePaint);

			// DownLink Trch Bler Meas.Info

			paraname = getContext().getString(R.string.wcdma_wcdma_rach_2);
			cv.drawText(paraname, (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 11 - rowUpBit,
					fontPaint);

			values = new int[] { R.string.wcdma_dl_power_Up, R.string.wcdma_ul_Power_Up };

			for (int i = 0, j = 12; i < values.length; i += 2, j++) {
				paraname = getContext().getString(values[i]);
				cv.drawText(paraname, marginSize, rowsHeight * j - rowUpBit, fontPaint);
				if (i + 1 < values.length) {
					paraname = getContext().getString(values[i + 1]);
					cv.drawText(paraname, this.getWidth() / 2 + marginSize, rowsHeight * j - rowUpBit, fontPaint);
				}
			}

		} else if (currentPage == 5) {
			String textSizeLongest = "PDUSixOrMoreRetxdRate";
			tableRows = 20;
			rowsHeight = this.getViewHeight() / tableRows;
			rowUpBit = (rowsHeight - textSize) / 2;
			float textColumnLen = this.getWidth() - 5 - fontPaint.measureText(textSizeLongest);
			float textLineColumnLen = this.getWidth() - textColumnLen;
			float textOtherLen = textColumnLen / 4;
			cv.drawLine(1, 1, this.getWidth() - 1, 1, linePaint);
			cv.drawLine(1, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, linePaint);
			cv.drawLine(1, 1, 1, this.getHeight() - 1, linePaint);
			cv.drawLine(this.getWidth() - 1, 1, this.getWidth() - 1, this.getHeight() - 1, linePaint);

			String[] columnStr = { "Count", "Channel", "PDU DATA ID", "PDU CTL ID", "PDU DATA NUM", "PDU CTL NUM",
					"PDU Throughput", "SDU Throughput", "PDU NAK Rate", "PDU Error Rate", "PDU One Retxd Rate",
					"PDU Two Retxd Rate", "PDU Three Retxd Rate", "PDU Four Retxd Rate", "PDU Five Retxd Rate",
					"PDUSixOrMoreRetxdRate", "Status", "TX_WIN_SIZE" };

			for (int i = 0; i < tableRows; i++) {
				if (i != 0) {
					cv.drawLine(1, rowsHeight * i, this.getWidth() - 1, rowsHeight * i, linePaint);
				}
				if (i < 18) {
					cv.drawText(columnStr[i], (textLineColumnLen - fontPaint.measureText(columnStr[i])) / 2,
							rowsHeight * 2 + (rowsHeight * (i + 1) - rowUpBit), fontPaint);
				}
			}

			cv.drawLine(textLineColumnLen, rowsHeight, textLineColumnLen, this.getHeight() - 1, linePaint);

			for (int i = 0; i < 4; i++) {
				if (i != 0) {
					cv.drawLine(textLineColumnLen + textOtherLen * i, rowsHeight, textLineColumnLen + textOtherLen * i,
							this.getHeight() - 1, linePaint);
				}
				cv.drawText(i + "", textLineColumnLen + textOtherLen * i + (textOtherLen - fontPaint.measureText(i + "")) / 2,
						rowsHeight * 2 - rowUpBit, fontPaint);
			}
			String paraname = "RLC UL Entity Info";
			cv.drawText(paraname, (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight - rowUpBit, fontPaint);

		} else if (currentPage == 6) {
			String textSizeLongest = "SDU Throughput ";
			tableRows = 19;
			rowsHeight = this.getViewHeight() / tableRows;
			rowUpBit = (rowsHeight - textSize) / 2;
			float textColumnLen = this.getWidth() - 5 - fontPaint.measureText(textSizeLongest);
			float textLineColumnLen = this.getWidth() - textColumnLen;
			float textOtherLen = textColumnLen / 4;
			cv.drawLine(1, 1, this.getWidth() - 1, 1, linePaint);
			cv.drawLine(1, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, linePaint);
			cv.drawLine(1, 1, 1, this.getHeight() - 1, linePaint);
			cv.drawLine(this.getWidth() - 1, 1, this.getWidth() - 1, this.getHeight() - 1, linePaint);

			String[] columnStr = { "Count", "Channel", "PDU DATA ID", "PDU CTL ID", "SDU Throughput", "PDU Throughput",
					"PDU NAK Rate", "PDU Retxd Rate", "PDU Error Rate", "DATA NUM", "CTL NUM", " ", " ", "Count", "ID",
					"Channel Type", "Code Rate" };

			for (int i = 0; i < tableRows; i++) {
				if (i != 0) {
					cv.drawLine(1, rowsHeight * i, this.getWidth() - 1, rowsHeight * i, linePaint);
				}
				if (i < 17) {
					cv.drawText(columnStr[i], (textLineColumnLen - fontPaint.measureText(columnStr[i])) / 2,
							rowsHeight * 2 + (rowsHeight * (i + 1) - rowUpBit), fontPaint);
				}
			}

			cv.drawLine(textLineColumnLen, rowsHeight, textLineColumnLen, rowsHeight * 13, linePaint);
			cv.drawLine(textLineColumnLen, rowsHeight * 14, textLineColumnLen, this.getHeight() - 1, linePaint);

			for (int i = 0; i < 4; i++) {
				if (i != 0) {
					cv.drawLine(textLineColumnLen + textOtherLen * i, rowsHeight, textLineColumnLen + textOtherLen * i,
							rowsHeight * 13, linePaint);
					cv.drawLine(textLineColumnLen + textOtherLen * i, rowsHeight * 14, textLineColumnLen + textOtherLen * i,
							this.getHeight() - 1, linePaint);
				}
				cv.drawText(i + "", textLineColumnLen + textOtherLen * i + (textOtherLen - fontPaint.measureText(i + "")) / 2,
						rowsHeight * 2 - rowUpBit, fontPaint);
				cv.drawText(i + "", textLineColumnLen + textOtherLen * i + (textOtherLen - fontPaint.measureText(i + "")) / 2,
						rowsHeight * 15 - rowUpBit, fontPaint);

			}
			String paraname = "RLC DL Entity Info";
			cv.drawText(paraname, (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight - rowUpBit, fontPaint);
			paraname = "DL TrCH Info";
			cv.drawText(paraname, (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight * 14 - rowUpBit,
					fontPaint);

		} else if (currentPage == 7) {
			String textSizeLongest = "AMR WB  23.85K Count ";
			tableRows = 20;
			rowsHeight = this.getViewHeight() / tableRows;
			rowUpBit = (rowsHeight - textSize) / 2;
			float textColumnLen = this.getWidth() - 5 - fontPaint.measureText(textSizeLongest);
			float textLineColumnLen = this.getWidth() - textColumnLen;
			float textOtherLen = textColumnLen / 2;
			cv.drawLine(1, 1, this.getWidth() - 1, 1, linePaint);
			cv.drawLine(1, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, linePaint);
			cv.drawLine(1, 1, 1, this.getHeight() - 1, linePaint);
			cv.drawLine(this.getWidth() - 1, 1, this.getWidth() - 1, this.getHeight() - 1, linePaint);

			String[] columnStr = { "AMR Usage %", "AMR NB 4.75k Count", "AMR NB 5.15k Count", "AMR NB 5.90k Count",
					"AMR NB 6.70k Count", "AMR NB 7.40k Count", "AMR NB 7.95k Count", "AMR NB 10.2k Count", " AMR NB 12.2k Count",
					"AMR Usage %", "AMR WB 6.6K Count", "AMR WB 8.85K Count", "AMR WB 12.65K Count", "AMR WB 14.25K Count",
					"AMR WB 15.85K Count", "AMR WB 18.25K Count", "AMR WB 19.85K Count", "AMR WB 23.25K Count",
					"AMR WB 23.85K Count" };

			for (int i = 0; i < tableRows; i++) {
				if (i != 0) {
					cv.drawLine(1, rowsHeight * i, this.getWidth() - 1, rowsHeight * i, linePaint);
				}
				if (i < 19) {
					cv.drawText(columnStr[i], (textLineColumnLen - fontPaint.measureText(columnStr[i])) / 2,
							rowsHeight * 1 + (rowsHeight * (i + 1) - rowUpBit), fontPaint);
				}
			}

			cv.drawLine(textLineColumnLen, rowsHeight, textLineColumnLen, this.getHeight() - 1, linePaint);
			String[] ulORDlStr = { "DL", "UL" };
			for (int i = 0; i < 2; i++) {
				if (i != 0) {
					cv.drawLine(textLineColumnLen + textOtherLen * i, rowsHeight, textLineColumnLen + textOtherLen * i,
							this.getHeight() - 1, linePaint);
				}
				cv.drawText(ulORDlStr[i],
						textLineColumnLen + textOtherLen * i + (textOtherLen - fontPaint.measureText(ulORDlStr[i])) / 2,
						rowsHeight * 2 - rowUpBit, fontPaint);
				cv.drawText(ulORDlStr[i] + "",
						textLineColumnLen + textOtherLen * i + (textOtherLen - fontPaint.measureText(ulORDlStr[i])) / 2,
						rowsHeight * 11 - rowUpBit, fontPaint);

			}
			String paraname = "AMR Info";
			cv.drawText(paraname, (this.getWidth() - fontPaint.measureText(paraname)) / 2, rowsHeight - rowUpBit, fontPaint);

		}

		cv.save();
		cv.restore();
	}

	/**
	 * 创建表格数据
	 * 
	 * @param bm
	 *          要创建表格的位图
	 * @param data
	 *          表格数据
	 * @return 输出位图
	 */
	protected void CreateTableData(Canvas cv, TraceInfoData traceData) {
		float rowsHeight = this.getViewHeight() / tableRows; // 行高
		float colsWidth = this.getWidth() / tableCols; // 列宽
		float neiColsWith = (this.getWidth() - colsWidth) / tableCols;// 邻近单元C*宽度
		float rowUpBit = (rowsHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2

		String value;

		if (currentPage == 1) {
			String[] datas = new String[] {
					getParaValue(UnifyParaID.W_Ser_MCC).equals("") && getParaValue(UnifyParaID.W_Ser_MNC).equals("") ? ""
							: getParaValue(UnifyParaID.W_Ser_MCC) + "/" + getParaValue(UnifyParaID.W_Ser_MNC),
					getParaValue(UnifyParaID.W_Ser_LAC),
					getParaValue(UnifyParaID.W_Ser_UL_UARFCN).equals("") ? ""
							: getParaValue(UnifyParaID.W_Ser_UL_UARFCN) + "/" + getParaValue(UnifyParaID.W_Ser_DL_UARFCN),
					UtilsMethod.getLongCellIdToRNCId(getParaValue(UnifyParaID.W_Ser_RNC_ID)) + "/"
							+ UtilsMethod.getLongTosShortCellID(getParaValue(UnifyParaID.W_Ser_Cell_ID)),
					getParaValue(UnifyParaID.W_Ser_Max_PSC), getParaValue(UnifyParaID.W_Ser_BLER),
					getParaValue(UnifyParaID.W_Ser_Max_RSCP), getParaValue(UnifyParaID.W_Ser_Total_RSCP),
					getParaValue(UnifyParaID.W_Ser_Max_EcIo), getParaValue(UnifyParaID.W_Ser_Total_EcIo),
					getParaValue(UnifyParaID.W_Ser_RxPower), getParaValue(UnifyParaID.W_Ser_SIR),
					(getParaValue(UnifyParaID.W_Ser_RRC_State).equals("0") ? "" : getParaValue(UnifyParaID.W_Ser_TxPower)),
					UtilsMethodPara.getWcdmaRRCState(getParaValue(UnifyParaID.W_Ser_RRC_State)),
					UtilsMethodPara.getWcdmaAmrStr(getParaValue(UnifyParaID.W_Ser_DL_AMR_Codec)),
					UtilsMethodPara.getWcdmaAmrStr(getParaValue(UnifyParaID.W_Ser_UL_AMR_Codec))
					/* "CellName" */ };
			for (int i = 0, j = 2; i < datas.length; i += 2, j++) {
				cv.drawText(datas[i], this.getWidth() / 2 - paramPaint.measureText(datas[i]) - marginSize,
						rowsHeight * j - rowUpBit, paramPaint);
				if (i + 1 < datas.length) {
					cv.drawText(datas[i + 1], this.getWidth() - paramPaint.measureText(datas[i + 1]) - marginSize,
							rowsHeight * j - rowUpBit, paramPaint);
				}
			}
			if (!StringUtil.isNullOrEmpty(getParaValue(UnifyParaID.W_Ser_DL_UARFCN))
					&& !StringUtil.isNullOrEmpty(getParaValue(UnifyParaID.W_Ser_Max_PSC))) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("uarfcn = '").append(getParaValue(UnifyParaID.W_Ser_DL_UARFCN)).append("'");
				buffer.append(" and psc = '").append(getParaValue(UnifyParaID.W_Ser_Max_PSC)).append("'");
				new CheckCellParamThread(this.getContext(),
						new String[] { "uarfcn", "psc", "cellName", "cellId", "longitude", "latitude" }, buffer.toString(),
						WalktourConst.NetWork.WCDMA).start();
			}
			StringBuilder cellKey = new StringBuilder();
			cellKey.append(WalktourConst.NetWork.WCDMA);
			cellKey.append("_uarfcn_").append(getParaValue(UnifyParaID.W_Ser_DL_UARFCN));
			cellKey.append("_psc_").append(getParaValue(UnifyParaID.W_Ser_Max_PSC));

			String cellnameString = traceData.getNetworkCellInfo(cellKey.toString()) == null ? ""
					: traceData.getNetworkCellInfo(cellKey.toString()).getCellName();
			cv.drawText(cellnameString, this.getWidth() - paramPaint.measureText(cellnameString) - marginSize,
					rowsHeight * 10 - rowUpBit, paramPaint);
		} else if (currentPage == 2) {
			StringBuilder buffer = new StringBuilder();
			StringBuilder buffer2 = new StringBuilder();
			servingNeighbor = getParaValue(UnifyParaID.W_TUMTSCellInfoV2).split(";");
			for (int i = 0; servingNeighbor != null && i < servingNeighbor.length - 1; i++) {
				// CdmaPilot cdmaPilot = cdmaPilotList.get(i);
				/**
				 * valueStr的值：0, -10688, 103, -68.000000, -5.267518, -73.267517
				 * ActiveSetType:0 UmtsFreq:10688 UmtsPSC;103 UmtsRssi;-68.000000
				 * UmtsEcIo;-5.267518 UmtsRSCP;-73.267517 其中ActiveSetType取值：ActiveSet
				 * 0，MonitorSet 1，NeighborSet 2，DetectedSet 3，VirtualActiveSet 4
				 */
				String[] neighbor = servingNeighbor[i + 1].split(",");
				value = neighbor[1];
				cv.drawText(value, colsWidth + neiColsWith * i + (neiColsWith - paramPaint.measureText(value)) / 2,
						rowsHeight * 2 - rowUpBit, paramPaint);
				value = neighbor[2];
				cv.drawText(value, colsWidth + neiColsWith * i + (neiColsWith - paramPaint.measureText(value)) / 2,
						rowsHeight * 3 - rowUpBit, paramPaint);
				value = ""; // cell id
				cv.drawText(value, colsWidth + neiColsWith * i + (neiColsWith - paramPaint.measureText(value)) / 2,
						rowsHeight * 4 - rowUpBit, paramPaint);
				value = UtilsMethod.numToShowDecimal2(neighbor[5]); // RSCP
				cv.drawText(value, colsWidth + neiColsWith * i + (neiColsWith - paramPaint.measureText(value)) / 2,
						rowsHeight * 5 - rowUpBit, paramPaint);
				value = UtilsMethod.numToShowDecimal2(neighbor[4]); // ECIO
				cv.drawText(value, colsWidth + neiColsWith * i + (neiColsWith - paramPaint.measureText(value)) / 2,
						rowsHeight * 6 - rowUpBit, paramPaint);
				value = UtilsMethodPara.getWcdmaSetType(neighbor[0]); // Set
				cv.drawText(value, colsWidth + neiColsWith * i + (neiColsWith - paramPaint.measureText(value)) / 2,
						rowsHeight * 7 - rowUpBit, paramPaint);

				// 查询CellID CellName 相关
				String cellidKey = WalktourConst.NetWork.WCDMA + "_" + neighbor[1] + "_" + neighbor[2];
				if (traceData.containsCellIDHmKey(cellidKey)) {
					value = traceData.getNetworkCellInfo(cellidKey).getCellId();
					cv.drawText(value, colsWidth + neiColsWith * i + (neiColsWith - paramPaint.measureText(value)) / 2,
							rowsHeight * 4 - rowUpBit, paramPaint);
				} else {
					value = "";
					traceData.setNetworkCellInfo(cellidKey, new CellInfo("", "", -1));
					if (!StringUtil.isNullOrEmpty(neighbor[1])) {
						if (buffer.length() == 0) {
							buffer.append("uarfcn in(" + neighbor[1]);
						} else {
							buffer.append("," + neighbor[1]);
						}
					}
					if (!StringUtil.isNullOrEmpty(neighbor[2])) {
						if (buffer2.length() == 0) {
							buffer2.append("psc in(" + neighbor[2]);
						} else {
							buffer2.append("," + neighbor[2]);
						}
					}

				}
			}
			if (buffer.length() > 0) {
				// UmtsServingCell model = traceData.getUmtsServingCell();
				String cellKey = new StringBuffer().append(WalktourConst.NetWork.WCDMA).append("_")
						.append(getParaValue(UnifyParaID.W_Ser_DL_UARFCN)).append(getParaValue(UnifyParaID.W_Ser_Max_PSC))
						.toString();
				if (traceData.getNetworkCellInfo(cellKey) == null
						&& !StringUtil.isNullOrEmpty(getParaValue(UnifyParaID.W_Ser_DL_UARFCN))
						&& !StringUtil.isNullOrEmpty(getParaValue(UnifyParaID.W_Ser_Max_PSC))) {
					buffer.append("," + getParaValue(UnifyParaID.W_Ser_DL_UARFCN));
					if (buffer2.length() > 0)
						buffer2.append("," + getParaValue(UnifyParaID.W_Ser_Max_PSC));
				}
				buffer.append(")");
				if(buffer2.length() > 0){
					buffer2.append(")");
					buffer.append(" and ").append(buffer2);
				}
				new CheckCellParamThread(this.getContext(),
						new String[] { "uarfcn", "psc", "cellName", "cellId", "longitude", "latitude" }, buffer.toString(),
						WalktourConst.NetWork.WCDMA).start();
				buffer = null;
				buffer2 = null;
			}

		} else if (currentPage == 3) {
			tableRows = 9;
			rowsHeight = this.getViewHeight() / tableRows; // 行高

			servingNeighbor = getParaValue(UnifyParaID.W_TUMTSCellInfoV2).split(";");
			float[] values = new float[servingNeighbor.length - 1];
			String[] params = new String[servingNeighbor.length - 1];
			float[] percentages = new float[servingNeighbor.length - 1];

			for (int i = 0; servingNeighbor != null && i < servingNeighbor.length - 1; i++) {
				String[] neighbor = servingNeighbor[i + 1].split(",");
				values[i] = Float.valueOf(UtilsMethod.numToShowDecimal2(neighbor[5]));
				params[i] = "N" + (i + 1);
				percentages[i] = 100 - ((values[i] / -125) * 100) + 25 / 125 * 100;
			}

			super.createCellHistogram("RSCP(dBm)", params, values, percentages, rowsHeight * 8, cv);

		} else if (currentPage == 4) {
			String[] datas = new String[] { getParaValue(UnifyParaID.W_RA_Message_Length),
					UtilsMethodPara.getWcdmaAICHStatus(getParaValue(UnifyParaID.W_RA_AICH_Status)),
					getParaValue(UnifyParaID.W_RA_Preambles_Num), getParaValue(UnifyParaID.W_RA_Access_Slot),
					getParaValue(UnifyParaID.W_RA_Last_Preamble_Signature), getParaValue(UnifyParaID.W_RA_SFN),
					getParaValue(UnifyParaID.W_RA_RF_TX_Power), getParaValue(UnifyParaID.W_RA_AICH_Timing) };
			for (int i = 0, j = 2; i < datas.length; i += 2, j++) {
				cv.drawText(datas[i], this.getWidth() / 2 - paramPaint.measureText(datas[i]) - marginSize,
						rowsHeight * j - rowUpBit, paramPaint);
				if (i + 1 < datas.length) {
					cv.drawText(datas[i + 1], this.getWidth() - paramPaint.measureText(datas[i + 1]) - marginSize,
							rowsHeight * j - rowUpBit, paramPaint);
				}
			}

			datas = new String[] { getParaValue(UnifyParaID.W_PRA_Max_TxPower),
					getParaValue(UnifyParaID.W_PRA_Transport_Chan_ID), getParaValue(UnifyParaID.W_PRA_Min_SF_for_RACH),
					getParaValue(UnifyParaID.W_PRA_PWR_Ramp_Step), getParaValue(UnifyParaID.W_PRA_SC_Index),
					getParaValue(UnifyParaID.W_PRA_Max_Preamble_Trans), getParaValue(UnifyParaID.W_PRA_UL_Punctuing_Limit),
					getParaValue(UnifyParaID.W_PRA_UL_Interference) };
			for (int i = 0, j = 7; i < datas.length; i += 2, j++) {
				cv.drawText(datas[i], this.getWidth() / 2 - paramPaint.measureText(datas[i]) - marginSize,
						rowsHeight * j - rowUpBit, paramPaint);
				if (i + 1 < datas.length) {
					cv.drawText(datas[i + 1], this.getWidth() - paramPaint.measureText(datas[i + 1]) - marginSize,
							rowsHeight * j - rowUpBit, paramPaint);
				}
			}

			// power control
			datas = new String[] { getParaValue(UnifyParaID.W_PC_DL_Power_Up), getParaValue(UnifyParaID.W_PC_UL_Power_Up) };
			for (int i = 0, j = 12; i < datas.length; i += 2, j++) {
				cv.drawText(datas[i], this.getWidth() / 2 - paramPaint.measureText(datas[i]) - marginSize,
						rowsHeight * j - rowUpBit, paramPaint);
				if (i + 1 < datas.length) {
					cv.drawText(datas[i + 1], this.getWidth() - paramPaint.measureText(datas[i + 1]) - marginSize,
							rowsHeight * j - rowUpBit, paramPaint);
				}
			}
		} else if (currentPage == 5) { // RLC UL Entity Info

		} else if (currentPage == 6) {
			String textSizeLongest = "PDU Throughput ";
			tableRows = 19;
			rowsHeight = this.getViewHeight() / tableRows;
			rowUpBit = (rowsHeight - textSize) / 2;
			float textColumnLen = this.getWidth() - 5 - fontPaint.measureText(textSizeLongest);
			float textLineColumnLen = this.getWidth() - textColumnLen;
			float textOtherLen = textColumnLen / 4;
			WCDMATrCHDLConfiguration wTrCHDLConfiguration = (WCDMATrCHDLConfiguration) TraceInfoInterface
					.getParaStruct(UnifyStruct.FLAG_WCDMA_TrCH_DL_Configuration);
			if (wTrCHDLConfiguration != null) {
				ArrayList<Channel> channels = wTrCHDLConfiguration.channels;
				for (int i = 0, j = 16; i < channels.size(); i++, j = 16) {
					UnifyStruct.Channel channel = channels.get(i);
					cv.drawText(channels.size() + "",
							textLineColumnLen + textOtherLen * i + (textOtherLen - fontPaint.measureText(channels.size() + "")) / 2,
							rowsHeight * j - rowUpBit, paramPaint);
					cv.drawText(channel.getChannelID() + "",
							textLineColumnLen + textOtherLen * i
									+ (textOtherLen - fontPaint.measureText(channel.getChannelID() + "")) / 2,
							rowsHeight * ++j - rowUpBit, paramPaint);
					cv.drawText(channel.getChannelType() + "",
							textLineColumnLen + textOtherLen * i
									+ (textOtherLen - fontPaint.measureText(channel.getChannelType() + "")) / 2,
							rowsHeight * ++j - rowUpBit, paramPaint);
					cv.drawText(channel.getCodingRate() + "",
							textLineColumnLen + textOtherLen * i
									+ (textOtherLen - fontPaint.measureText(channel.getCodingRate() + "")) / 2,
							rowsHeight * ++j - rowUpBit, paramPaint);
				}
			}
		} else if (currentPage == 7) {
			String textSizeLongest = "AMR WB  23.85K Count ";
			tableRows = 20;
			rowsHeight = this.getViewHeight() / tableRows;
			rowUpBit = (rowsHeight - textSize) / 2;
			float textColumnLen = this.getWidth() - 5 - fontPaint.measureText(textSizeLongest);
			float textLineColumnLen = this.getWidth() - textColumnLen;
			float textOtherLen = textColumnLen / 2;

			String[] datas = new String[] { getParaValue(UnifyParaID.W_DL_AMR_NB_475k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_NB_475k_Count), getParaValue(UnifyParaID.W_DL_AMR_NB_515k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_NB_515k_Count), getParaValue(UnifyParaID.W_DL_AMR_NB_590k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_NB_590k_Count), getParaValue(UnifyParaID.W_DL_AMR_NB_670k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_NB_670k_Count), getParaValue(UnifyParaID.W_DL_AMR_NB_740k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_NB_740k_Count), getParaValue(UnifyParaID.W_DL_AMR_NB_795k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_NB_795k_Count), getParaValue(UnifyParaID.W_DL_AMR_NB_102k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_NB_102k_Count), getParaValue(UnifyParaID.W_DL_AMR_NB_122k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_NB_122k_Count), "", "", getParaValue(UnifyParaID.W_DL_AMR_WB_66k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_WB_66k_Count), getParaValue(UnifyParaID.W_DL_AMR_WB_885k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_WB_885k_Count), getParaValue(UnifyParaID.W_DL_AMR_WB_1265k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_WB_1265k_Count), getParaValue(UnifyParaID.W_DL_AMR_WB_1425k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_WB_1425k_Count), getParaValue(UnifyParaID.W_DL_AMR_WB_1585k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_WB_1585k_Count), getParaValue(UnifyParaID.W_DL_AMR_WB_1825k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_WB_1825k_Count), getParaValue(UnifyParaID.W_DL_AMR_WB_1985k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_WB_1985k_Count), getParaValue(UnifyParaID.W_DL_AMR_WB_2325k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_WB_2325k_Count), getParaValue(UnifyParaID.W_DL_AMR_WB_2385k_Count),
					getParaValue(UnifyParaID.W_UL_AMR_WB_2385k_Count) };

			/**
			 * 求DL UL参数和
			 */
			int dCount = 0;
			int sCount = 0;

			for (int i = 0; i < datas.length; i++) {
				try {
					if (i % 2 == 0) {
						dCount += getCountInt(datas[i]);
					} else {
						sCount += getCountInt(datas[i]);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (int i = 0, j = 3; i < datas.length; i += 2, j++) {
				if (datas[i].trim().length() != 0 && !datas[i].trim().equals("0")) {
					cv.drawText(UtilsMethod.getIntMultiple(getCountInt(datas[i]), dCount), textLineColumnLen
							+ (textOtherLen - fontPaint.measureText(UtilsMethod.getIntMultiple(getCountInt(datas[i]), dCount))) / 2,
							rowsHeight * j - rowUpBit, paramPaint);
				}
				if (datas[i + 1].trim().length() != 0 && !datas[i + 1].trim().equals("0")) {
					cv.drawText(UtilsMethod.getIntMultiple(getCountInt(datas[i + 1]), sCount), textLineColumnLen + textOtherLen
							+ (textOtherLen - fontPaint.measureText(UtilsMethod.getIntMultiple(getCountInt(datas[i + 1]), sCount)))
									/ 2,
							rowsHeight * j - rowUpBit, paramPaint);
				}
			}

		}

		cv.save();
		cv.restore();
	}

	/** 获得得参数队列中指定ID的值 */
	private String getParaValue(int paraId) {
		return TraceInfoInterface.getParaValue(paraId);
	}

	private int getCountInt(String dataValue) {
		try {
			return Integer.valueOf(dataValue.trim().length() != 0 ? dataValue : "0");
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * @param viewHeight
	 *          the viewHeight to set
	 */
	public void setViewHeight(int viewHeight) {
		this.viewHeight = viewHeight;
		invalidate();
	}

	/**
	 * @param viewHeight
	 *          the viewHeight to set
	 */
	public int getViewHeight() {
		if (viewHeight == 0) {
			viewHeight = this.getHeight();
		}
		return viewHeight;
	}
}
