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
 * 
 * @author lzh
 *
 */
public class TotalWcdmaParamView extends BasicTotalView {
	private static String tag = "TotalParaView";
	private boolean isRegisterReceiver = false;
	DisplayMetrics metric = new DisplayMetrics();

	public TotalWcdmaParamView(Context context) {
		super(context);

		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(metric);
	}

	public TotalWcdmaParamView(Context context, AttributeSet attrs) {
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
		int tableRows = 8; // 行数
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
			stopx = width- marginSize;
			stopy = rowHeight * (i + 1);
			cv.drawLine(startx, starty+marginSize, stopx, stopy + marginSize, linePaint);
		}
		for (int i = 1; i < tableCols - 1; i++) {
			startx = colsWidth * (i + 1);
			starty = 1 + rowHeight * 1;
			stopx = colsWidth * (i + 1);
			stopy = rowHeight * tableRows;
			cv.drawLine(startx, starty+ marginSize, stopx, stopy + marginSize, linePaint);
		}
		
		float rowUpBit = (rowHeight - textSize) / 2;// 指定行上升位数,为行高-字体高度 再除2

		String paraname;
		paraname = "WCDMA Cell Info";// 表头
		cv.drawText(paraname,
				(tablewidth - fontPaint.measureText(paraname))
						/ 2, rowHeight * 1 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_para);// "参数名称";
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
		paraname = "Total RSCP"+"(dBm)";// "RxQualFull";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 3 - rowUpBit + marginSize, fontPaint);
		paraname = "Total EcIo"+"(dB)";// "RxQualSub";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 4 - rowUpBit + marginSize, fontPaint);
		paraname = "RxPower"+"(dBm)";// "RxLevFull";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 5 - rowUpBit + marginSize, fontPaint);
		paraname = "TxPower"+"(dBm)";// "RxLevBub";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 6 - rowUpBit + marginSize, fontPaint);
		paraname = "BLER"+"(%)";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 7 - rowUpBit + marginSize, fontPaint);
		paraname = "SIR"+"(dB)";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 8 - rowUpBit + marginSize, fontPaint);

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
				TotalStruct.TotalMeasurePara._wTotalRSCP.name());
		value = "" + (data.getMaxValue() != -9999 ? data.getMaxValue() : "");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit + marginSize, paramPaint);
		value = "" + (data.getMinValue() != -9999 ? data.getMinValue() : "");
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ (data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1)));
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit + marginSize, paramPaint);

		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._wTotalEclo.name());
		value = "" + (data.getMaxValue() != -9999 ? data.getMaxValue() : "");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit + marginSize, paramPaint);
		value = "" + (data.getMinValue() != -9999 ? data.getMinValue() : "");
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ (data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1)));
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit + marginSize, paramPaint);

		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._wRxPower.name());
		value = "" + (data.getMaxValue() != -9999 ? data.getMaxValue() : "");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit + marginSize, paramPaint);
		value = "" + (data.getMinValue() != -9999 ? data.getMinValue() : "");
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ (data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1)));
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit + marginSize, paramPaint);

		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._wTxPower.name());
		value = "" + (data.getMaxValue() != -9999 ? data.getMaxValue() : "");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 6 - rowUpBit + marginSize, paramPaint);
		value = "" + (data.getMinValue() != -9999 ? data.getMinValue() : "");
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 6 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ (data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1)));
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 6 - rowUpBit + marginSize, paramPaint);

		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._wBLER.name());
		value = "" + (data.getMaxValue() != -9999 ? data.getMaxValue() : "");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 7 - rowUpBit + marginSize, paramPaint);
		value = "" + (data.getMinValue() != -9999 ? data.getMinValue() : "");
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 7 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ (data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1)));
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 7 - rowUpBit + marginSize, paramPaint);

		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._wSIR.name());
		value = "" + (data.getMaxValue() != -9999 ? data.getMaxValue() : "");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 8 - rowUpBit + marginSize, paramPaint);
		value = "" + (data.getMinValue() != -9999 ? data.getMinValue() : "");
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 8 - rowUpBit + marginSize, paramPaint);
		value = ""
				+ (data.getKeySum() == -9999 ? "" : UtilsMethod.decFormat
						.format(data.getKeySum()
								* 1f
								/ (data.getKeyCounts() != 0 ? data
										.getKeyCounts() : 1)));
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 8 - rowUpBit + marginSize, paramPaint);

		cv.save();
		cv.restore();
		return null;

	}

	@Override
	protected void onAttachedToWindow() {
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
				TotalWcdmaParamView.this.buildDrawingCache();
				UtilsMethod.SaveBitmapToFile(
						TotalWcdmaParamView.this.getDrawingCache(), path);
			} else {
				invalidate();
			}
		}
	};


}
