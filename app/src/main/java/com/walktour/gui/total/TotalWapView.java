package com.walktour.gui.total;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.BasicTotalView;
import com.walktour.gui.R;

import java.util.HashMap;

public class TotalWapView extends BasicTotalView {
	private static String tag = "TotalWapView";
	private boolean isRegisterReceiver = false;

	public TotalWapView(Context context) {
		super(context);
	}

	public TotalWapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		CreateTable(canvas);
		CreateTableData(canvas, TotalDataByGSM.getInstance().getUnifyTimes());
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
		float starty = 0;
		float stopx = 0;
		float stopy = 0;
		float tablewidth = width;
		int tableRows = 10; // 行数
		int tableCols = 5; // 列数
		float colsWidth = tablewidth / tableCols; // 列宽

		cv.drawLine(startx, marginSize, width - marginSize, marginSize, linePaint);
		cv.drawLine(startx, rowHeight * tableRows, width - marginSize,
				rowHeight * tableRows, linePaint);
		cv.drawLine(startx, marginSize, startx, rowHeight * tableRows, linePaint);
		cv.drawLine(width - marginSize, marginSize, width - marginSize, rowHeight
				* tableRows, linePaint);

		for (int i = 0; i < tableRows - 1; i++) {
			startx = 1;
			starty = rowHeight * (i + 1);
			stopx = width - marginSize;
			stopy = rowHeight * (i + 1);
			if(i != 1){
				cv.drawLine(startx, starty, stopx, stopy, linePaint);
			}
		}
		for (int i = 1; i < tableCols - 1; i++) {
			startx = colsWidth * (i + 1);
			starty = rowHeight;
			stopx = colsWidth * (i + 1);
			stopy = rowHeight * tableRows;
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
		}

		float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2
		String paraname;
		paraname = "WAP Info";
		cv.drawText(paraname,
				tablewidth / 2 - fontPaint.measureText(paraname)/2,
				rowHeight /2 + rowUpBit/2 + marginSize, fontPaint);
		
		paraname = getContext().getString(R.string.total_wap_login);
		cv.drawText(paraname,
				colsWidth * 2 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 2 + rowUpBit / 2, fontPaint);
		paraname = getContext().getString(R.string.total_wap_refresh);
		cv.drawText(paraname,
				colsWidth * 3 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 2 + rowUpBit / 2, fontPaint);
		paraname = getContext().getString(R.string.total_wap_down);
		cv.drawText(paraname,
				colsWidth * 4 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 2 + rowUpBit / 2, fontPaint);

		paraname = getContext().getString(R.string.total_attemptCount);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 4 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_SuccessCounts);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 5 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_SuccessRate);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 6 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_delay);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 7 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_AverageSpeed);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 8 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_downTotalSize); // 应用层平均速率
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 9 - rowUpBit, fontPaint);
		
		paraname = getContext().getString(R.string.total_downTotalTimes); // 应用层平均速率
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 10 - rowUpBit, fontPaint);
		
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
	protected Bitmap CreateTableData(Canvas cv, HashMap<String, Long> hMap) {
		int width = this.getWidth();
		float tablewidth = width;
		int tableCols = 5; // 列数
		float colsWidth = tablewidth / tableCols; // 列宽
		float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2

		String value;

		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalAppreciation._wapLoginTrys.name()); // "登录尝试次数";
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalAppreciation._wapRefreshTrys.name()); // "刷新尝试次数";
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalAppreciation._wapDownTrys.name()); // "下载尝试次数";
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit, paramPaint);

		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalAppreciation._wapLogingSuccs.name()); // "登录成功次数";
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalAppreciation._wapRefreshSuccs.name()); // "刷新成功次数";
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalAppreciation._wapDownSuccs.name()); // "下载成功次数";
		long downCount = 0;
		try {
			downCount = Long.valueOf(value);
		} catch (Exception e) {

		}
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit, paramPaint);

		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,
						TotalStruct.TotalAppreciation._wapLogingSuccs.name(),
						TotalStruct.TotalAppreciation._wapLoginTrys.name(),
						100, "%");// 登录成功率
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 6 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,
						TotalStruct.TotalAppreciation._wapRefreshSuccs.name(),
						TotalStruct.TotalAppreciation._wapRefreshTrys.name(),
						100, "%");// 刷新成功率
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 6 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,
						TotalStruct.TotalAppreciation._wapDownSuccs.name(),
						TotalStruct.TotalAppreciation._wapDownTrys.name(), 100,
						"%");// 下载成功率
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 6 - rowUpBit, paramPaint);

		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,
						TotalStruct.TotalAppreciation._wapLoginDelay.name(),
						TotalStruct.TotalAppreciation._wapLogingSuccs.name(),
						1, "");// 登录时延
		try {
			if (value.contains(".")) {
				LogUtil.w(tag, value.substring(0, value.indexOf(".")));
				value = value.substring(0, value.indexOf("."));
			}
		} catch (Exception e) {
			LogUtil.w(tag, e.toString());
		}
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 7 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,
						TotalStruct.TotalAppreciation._wapRefreshDelay.name(),
						TotalStruct.TotalAppreciation._wapRefreshSuccs.name(),
						1, "");// 刷新时延
		try {
			if (value.contains(".")) {
				LogUtil.w(tag, value.substring(0, value.indexOf(".")));
				value = value.substring(0, value.indexOf("."));
			}
		} catch (Exception e) {
			LogUtil.w(tag, e.toString());
		}
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 7 - rowUpBit, paramPaint);
		value = "/";
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 7 - rowUpBit, paramPaint);

		value = "/"; // "登录速率为空";
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 8 - rowUpBit, paramPaint);
		value = "/"; // "刷新速率为空";
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 8 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,
						TotalStruct.TotalAppreciation._wapDownTotalBytes.name(),//byte
						TotalStruct.TotalAppreciation._wapDownTotalTime.name(), //ms
						8 ,//界面显示为kbps<------byte/ms转换成 *1000f/1000f
						"");
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 8 - rowUpBit, paramPaint);
		
		value = "/";
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 9 - rowUpBit, paramPaint);
		value = "/"; // "刷新尝试次数";
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 9 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalAppreciation._wapDownTotalBytes.name(),UtilsMethod.kbyteRage); // "下载尝试次数";
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 9 - rowUpBit, paramPaint);
		value = "/"; // "登录尝试次数";
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 10 - rowUpBit, paramPaint);
		value = "/"; // "刷新尝试次数";
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 10 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalAppreciation._wapDownTotalTime.name(),UtilsMethod.kbyteRage); // "下载尝试次数";
		cv.drawText(value,
				colsWidth * 4 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 10 - rowUpBit, paramPaint);


		cv.save();
		cv.restore();
		return null;

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		IntentFilter filter = new IntentFilter(); // 注册一个消息过滤器
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
			if (intent.getAction().equals(TotalDataByGSM.TotalResultToPicture)) {
				String path = intent
						.getStringExtra(TotalDataByGSM.TotalSaveFilePath)
						+ "-Wap.jpg";
				LogUtil.w(tag, "--save current to file---" + path);
				TotalWapView.this.buildDrawingCache();
				UtilsMethod.SaveBitmapToFile(
						TotalWapView.this.getDrawingCache(), path);
			} else {
				invalidate();
			}
		}
	};

}
