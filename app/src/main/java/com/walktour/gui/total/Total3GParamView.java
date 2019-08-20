package com.walktour.gui.total;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.BasicTotalView;
import com.walktour.gui.R;
import com.walktour.model.TotalMeasureModel;

import java.util.HashMap;


/**
 * 画参数3G数据统计
 * @author lzh
 *
 */
public class Total3GParamView extends BasicTotalView {
	private static String tag = "TotalParaView";
	private boolean isRegisterReceiver = false;
	
	DisplayMetrics metric = new DisplayMetrics();

	public Total3GParamView(Context context) {
		super(context);

		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(metric);
	}

	public Total3GParamView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setDrawingCacheEnabled(false);
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(metric);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		CreateTable(canvas);
		CreateTableData(canvas, TotalDataByGSM.getInstance().getMeasuePara());
	}

	/**
	 * 创建表格
	 * 
	 * @param bm
	 *            要创建表格的位图
	 * @return 输出位图
	 */
	protected Bitmap CreateTable(Canvas cv) {
		int width = this.getWidth();
		float startx = 1;
		float starty = 1;
		float stopx = 0;
		float stopy = 0;
		float tablewidth = width;
		int tableRows = 19; // 行数
		int tableCols = 5; // 列数
		float colsWidth = tablewidth / tableCols; // 列宽

		cv.drawLine(startx, marginSize, width - marginSize, marginSize, linePaint);
		cv.drawLine(startx, rowHeight * tableRows + marginSize, width - marginSize,
				rowHeight * tableRows + marginSize, linePaint);
		cv.drawLine(startx, marginSize, startx, rowHeight * tableRows + marginSize, linePaint);
		cv.drawLine(width - marginSize, marginSize, width - marginSize, rowHeight
				* tableRows + marginSize, linePaint);

		for (int i = 0; i < tableRows - 1; i++) {
			startx = 1;
			starty = rowHeight * (i + 1);
			stopx = width - marginSize;
			stopy = rowHeight * (i + 1);
			cv.drawLine(startx, starty+marginSize, stopx, stopy + marginSize, linePaint);
		}
		for (int i = 1; i < tableCols - 1; i++) {
			startx = colsWidth * (i + 1);
			starty = 1 + rowHeight * 1;
			stopx = colsWidth * (i + 1);
			stopy = rowHeight * 6;
			cv.drawLine(startx, starty+ marginSize, stopx, stopy + marginSize, linePaint);
		}
		
		for (int i = 1; i < tableCols - 1; i++) {
			startx = colsWidth * (i + 1);
			starty = rowHeight * 7;
			stopx = colsWidth * (i + 1);
			stopy = rowHeight * 10;
			cv.drawLine(startx, starty+ marginSize, stopx, stopy + marginSize, linePaint);
		}
		
		for (int i = 1; i < tableCols - 1; i++) {
			startx = colsWidth * (i + 1);
			starty = rowHeight * 11;
			stopx = colsWidth * (i + 1);
			stopy = rowHeight * 14;
			cv.drawLine(startx, starty+ marginSize, stopx, stopy + marginSize, linePaint);
		}
		
		for (int i = 1; i < tableCols - 1; i++) {
			startx = colsWidth * (i + 1);
			starty = rowHeight * 15;
			stopx = colsWidth * (i + 1);
			stopy = rowHeight * 19;
			cv.drawLine(startx, starty+ marginSize, stopx, stopy + marginSize, linePaint);
		}
		
		
		
		float rowUpBit = (rowHeight - textSize) / 2;// 指定行上升位数,为行高-字体高度 再除2

		String paraname;
		paraname = "HSPA Info";// 表头
		cv.drawText(paraname,
				(tablewidth - fontPaint.measureText(paraname))
						/ 2, rowHeight * 1 - rowUpBit + marginSize, fontPaint);
		paraname = "HSDPA "+getContext().getString(R.string.total_para);// "参数名称";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 2 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_max);// "最大";
		cv.drawText(paraname,
				colsWidth * 2 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 2 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_min);// "最小";
		cv.drawText(paraname,
				colsWidth * 3 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 2 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_average);// "平均";
		cv.drawText(paraname,
				colsWidth * 4 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 2 - rowUpBit + marginSize, fontPaint);
		paraname = "Phys Request Thr";// "RxQualFull";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 3 - rowUpBit + marginSize, fontPaint);
		paraname = "Phys Scheduled Thr";// "RxQualSub";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 4 - rowUpBit + marginSize, fontPaint);
		paraname = "Phys Service Thr";// "RxLevFull";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 5 - rowUpBit + marginSize, fontPaint);
		paraname = "MAC Thr";// "RxLevBub";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 6 - rowUpBit + marginSize, fontPaint);
		paraname = "HSUPA "+getContext().getString(R.string.total_para);// "参数名称";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 8 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_max);// "最大";
		cv.drawText(paraname,
				colsWidth * 2 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 8 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_min);// "最小";
		cv.drawText(paraname,
				colsWidth * 3 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 8 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_average);// "平均";
		cv.drawText(paraname,
				colsWidth * 4 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 8 - rowUpBit + marginSize, fontPaint);
		
		paraname = "Phys Service Thr";// "RxLevFull";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 9 - rowUpBit + marginSize, fontPaint);
		
		paraname = "MAC Thr";// "RxLevFull";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
				/ 2, rowHeight * 10 - rowUpBit + marginSize, fontPaint);
		paraname = "MIMO "+getContext().getString(R.string.total_para);// "参数名称";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 12 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_max);// "最大";
		cv.drawText(paraname,
				colsWidth * 2 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 12 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_min);// "最小";
		cv.drawText(paraname,
				colsWidth * 3 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 12 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_average);// "平均";
		cv.drawText(paraname,
				colsWidth * 4 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 12 - rowUpBit + marginSize, fontPaint);
		paraname = "Phys Scheduled Thr";// "RxLevFull";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
				/ 2, rowHeight * 13 - rowUpBit + marginSize, fontPaint);
		paraname = "Phys Service Thr";// "RxLevFull";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
				/ 2, rowHeight * 14 - rowUpBit + marginSize, fontPaint);
		
		paraname = "Dual Cell "+getContext().getString(R.string.total_para);// "参数名称";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 16 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_max);// "最大";
		cv.drawText(paraname,
				colsWidth * 2 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 16 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_min);// "最小";
		cv.drawText(paraname,
				colsWidth * 3 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 16 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_average);// "平均";
		cv.drawText(paraname,
				colsWidth * 4 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 16 - rowUpBit + marginSize, fontPaint);
		paraname = "Phys Request Thr";// "RxLevFull";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
				/ 2, rowHeight * 17 - rowUpBit + marginSize, fontPaint);
		paraname = "Phys Scheduled Thr";// "RxLevFull";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
				/ 2, rowHeight * 18 - rowUpBit + marginSize, fontPaint);
		paraname = "Phys Service Thr";// "RxLevFull";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
				/ 2, rowHeight * 19 - rowUpBit + marginSize, fontPaint);
		
		cv.save();
		cv.restore();
		return null;
	}

	/**
	 * 创建表格数据
	 * 
	 * @param bm
	 *            要创建表格的位图
	 * @param data
	 *            表格数据
	 * @return 输出位图
	 */
	protected Bitmap CreateTableData(Canvas cv,
			HashMap<String, TotalMeasureModel> hMap) {
		int width = this.getWidth();
		float tablewidth = width;
		int tableCols = 5; // 列数
		float colsWidth = tablewidth / tableCols; // 列宽
		float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2

		String value;
		TotalMeasureModel data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._hsdpaPhysRequestThr.name());
		value = "" + UtilsMethod.bps2Kbps((data.getMaxValue() != -9999 ? data.getMaxValue() : "").toString());
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit + marginSize, paramPaint);
		value = "" + UtilsMethod.bps2Kbps((data.getMinValue() != -9999 ? data.getMinValue() : "").toString());
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ UtilsMethod.bps2Kbps((data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1))).toString());
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit + marginSize, paramPaint);

		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._hsdpaPhysScheduledThr.name());
		value = "" + UtilsMethod.bps2Kbps((data.getMaxValue() != -9999 ? data.getMaxValue() : "").toString());
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit + marginSize, paramPaint);
		value = "" + UtilsMethod.bps2Kbps((data.getMinValue() != -9999 ? data.getMinValue() : "").toString());
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ UtilsMethod.bps2Kbps((data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1))).toString());
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit + marginSize, paramPaint);

		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._hsdpaPhysServiceThr.name());
		value = "" + UtilsMethod.bps2Kbps((data.getMaxValue() != -9999 ? data.getMaxValue() : "").toString());
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit + marginSize, paramPaint);
		value = "" + UtilsMethod.bps2Kbps((data.getMinValue() != -9999 ? data.getMinValue() : "").toString());
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ UtilsMethod.bps2Kbps((data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1))).toString());
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit + marginSize, paramPaint);

		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._hsdpaMACThr.name());
		value = "" + UtilsMethod.bps2Kbps((data.getMaxValue() != -9999 ? data.getMaxValue() : "").toString());
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 6 - rowUpBit + marginSize, paramPaint);
		value = "" + UtilsMethod.bps2Kbps((data.getMinValue() != -9999 ? data.getMinValue() : "").toString());
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 6 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ UtilsMethod.bps2Kbps((data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1))).toString());
		
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 6 - rowUpBit + marginSize, paramPaint);
		
		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._hsupaPhysServiceThr.name());
		value = "" + UtilsMethod.bps2Kbps((data.getMaxValue() != -9999 ? data.getMaxValue() : "").toString());
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 9 - rowUpBit + marginSize, paramPaint);
		value = "" + UtilsMethod.bps2Kbps((data.getMinValue() != -9999 ? data.getMinValue() : "").toString());
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 9 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ UtilsMethod.bps2Kbps((data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1))).toString());
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 9 - rowUpBit + marginSize, paramPaint);
		
		
		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._hsupaMACThr.name());
		value = "" + UtilsMethod.bps2Kbps((data.getMaxValue() != -9999 ? data.getMaxValue() : "").toString());
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 10 - rowUpBit + marginSize, paramPaint);
		value = "" + UtilsMethod.bps2Kbps((data.getMinValue() != -9999 ? data.getMinValue() : "").toString());
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 10 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ UtilsMethod.bps2Kbps((data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1))).toString());
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 10 - rowUpBit + marginSize, paramPaint);
		
		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._mimoPhysScheduledThr.name());
		value = "" + UtilsMethod.bps2Kbps((data.getMaxValue() != -9999 ? data.getMaxValue() : "").toString());
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 13 - rowUpBit + marginSize, paramPaint);
		value = "" + UtilsMethod.bps2Kbps((data.getMinValue() != -9999 ? data.getMinValue() : "").toString());
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 13 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ UtilsMethod.bps2Kbps((data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1))).toString());
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 13 - rowUpBit + marginSize, paramPaint);
		
		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._mimoPhysServiceThr.name());
		value = "" + UtilsMethod.bps2Kbps((data.getMaxValue() != -9999 ? data.getMaxValue() : "").toString());
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 14 - rowUpBit + marginSize, paramPaint);
		value = "" + UtilsMethod.bps2Kbps((data.getMinValue() != -9999 ? data.getMinValue() : "").toString());
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 14 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ UtilsMethod.bps2Kbps((data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1))).toString());
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 14 - rowUpBit + marginSize, paramPaint);
		
		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._dualPhysRequestThr.name());
		value = "" + UtilsMethod.bps2Kbps((data.getMaxValue() != -9999 ? data.getMaxValue() : "").toString());
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 17 - rowUpBit + marginSize, paramPaint);
		value = "" + UtilsMethod.bps2Kbps((data.getMinValue() != -9999 ? data.getMinValue() : "").toString());
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 17 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ UtilsMethod.bps2Kbps((data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1))).toString());
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 17 - rowUpBit + marginSize, paramPaint);
		
		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._dualPhysScheduledThr.name());
		value = "" + UtilsMethod.bps2Kbps((data.getMaxValue() != -9999 ? data.getMaxValue() : "").toString());
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 18 - rowUpBit + marginSize, paramPaint);
		value = "" + UtilsMethod.bps2Kbps((data.getMinValue() != -9999 ? data.getMinValue() : "").toString());
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 18 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ UtilsMethod.bps2Kbps((data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1))).toString());
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 18 - rowUpBit + marginSize, paramPaint);
		
		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._dualPhysServiceThr.name());
		value = "" + UtilsMethod.bps2Kbps((data.getMaxValue() != -9999 ? data.getMaxValue() : "").toString());
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 19 - rowUpBit + marginSize, paramPaint);
		value = "" + UtilsMethod.bps2Kbps((data.getMinValue() != -9999 ? data.getMinValue() : "").toString());
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 19 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ UtilsMethod.bps2Kbps((data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1))).toString());
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 19 - rowUpBit + marginSize, paramPaint);
		
		cv.save();
		cv.restore();
		return null;

	}

	@Override
	protected void onAttachedToWindow() {
		LogUtil.i("GGG888", "onAttachedToWindow ");
		super.onAttachedToWindow();
		IntentFilter filter = new IntentFilter(); // 注册一个消息过滤器
		filter.addAction(WalkMessage.TotalParaSelect);
		filter.addAction(TotalDataByGSM.TotalTaskDataChanged);
		filter.addAction(TotalDataByGSM.TotalParaDataChanged);
		filter.addAction(TotalDataByGSM.TotalResultToPicture);
		getContext().registerReceiver(mIntentReceiver, filter, null, null);
		isRegisterReceiver = true;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		try {
			if (isRegisterReceiver) {
				getContext().unregisterReceiver(mIntentReceiver); // 反注册消息过滤器
				isRegisterReceiver = false;
			}
		} catch (java.lang.IllegalArgumentException e) {
			LogUtil.w("IllegalArgumentException:", e.toString());
		}
	}

	/**
	 * 消息处理
	 */
	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			 if (intent.getAction().equals(
					TotalDataByGSM.TotalResultToPicture)) {
				String path = intent
						.getStringExtra(TotalDataByGSM.TotalSaveFilePath)
						+ "-Para.jpg";
				LogUtil.w(tag, "--save current to file---" + path);
				Total3GParamView.this.buildDrawingCache();
				UtilsMethod.SaveBitmapToFile(
						Total3GParamView.this.getDrawingCache(), path);
			} else {
				invalidate();
			}
		}
	};

	
	
	
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int heights = (int) (rowHeight * 19) + (marginSize * 2);
    	setMeasuredDimension(measureWidth(widthMeasureSpec), heights);
    }
	
	
	/**
	 * 
	 * @param 
	 * @return
	 */
    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        
        // Default size if no limits are specified.
        int result = 500;
        if (specMode == MeasureSpec.AT_MOST) {
            // Calculate the ideal size of your control
            // within this maximum size.
            // If your control fills the available space
            // return the outer bound.
            result = specSize;
        }
        
        else if (specMode == MeasureSpec.EXACTLY) {
            // If your control can fit within these bounds return that value.
            
            result = specSize;
        }
        
        return result;
    }
}
