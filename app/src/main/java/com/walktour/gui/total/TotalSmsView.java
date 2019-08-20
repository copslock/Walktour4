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

public class TotalSmsView extends BasicTotalView {
	private static String tag = "TotalSmsView";
	private boolean isRegisterReceiver = false;

	public TotalSmsView(Context context) {
		super(context);
	}

	public TotalSmsView(Context context, AttributeSet attrs) {
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
		int tableRows = 6; // 行数
		int tableCols = 3; // 列数
		float colsWidth = tablewidth / tableCols; // 列宽

		/* 画四边 */
		cv.drawLine(startx, marginSize, width - marginSize, marginSize, linePaint);
		cv.drawLine(startx, rowHeight * tableRows, width - marginSize,
				rowHeight * tableRows, linePaint);
		cv.drawLine(startx, marginSize, startx, rowHeight * tableRows, linePaint);
		cv.drawLine(width - marginSize, marginSize, width - marginSize, rowHeight
				* tableRows, linePaint);
		/* 画横线 */
		for (int i = 1; i < tableRows; i++) {
			startx = 1;
			starty = rowHeight * i;
			stopx = width - marginSize;
			stopy = rowHeight * i;
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
		}
		/* 画竖线 */
		for (int i = 0; i < tableCols - 1; i++) {
			startx = colsWidth * (i + 1);
			starty = rowHeight;
			stopx = colsWidth * (i + 1);
			stopy = rowHeight * (tableRows - (i == 0 ? 1 : 0));
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
		}

		float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2
		String paraname;
		paraname = "SMS Info";
		cv.drawText(paraname,width / 2 - fontPaint.measureText(paraname)/ 2, rowHeight * 1 - rowUpBit, fontPaint);
				
		/* 第一行 */
		paraname = getContext().getString(R.string.total_sendTime);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 1 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 2 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_sendSuccess);
		cv.drawText(paraname,
				colsWidth * 1 + (colsWidth * 1 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 2 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_sendDelay);
		cv.drawText(paraname,
				colsWidth * 2 + (colsWidth * 1 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 2 - rowUpBit, fontPaint);
		/* 第三行 */
		paraname = getContext().getString(R.string.total_receiveTime);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 1 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 4 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_receiveSuccess);
		cv.drawText(paraname,
				colsWidth * 1 + (colsWidth * 1 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 4 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_receiveDelay);
		cv.drawText(paraname,
				colsWidth * 2 + (colsWidth * 1 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 4 - rowUpBit, fontPaint);
		/* 第五行 */
		paraname = getContext().getString(R.string.total_p2pDelay);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 6 - rowUpBit, fontPaint);

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
		int tableCols = 3; // 列数
		float colsWidth = tablewidth / tableCols; // 列宽
		float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2

		String value;
		/* 第二行 */
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalAppreciation._SMSSendTry.name());// 发送尝试次数
		cv.drawText(value,
				colsWidth * 0 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalAppreciation._SMSSendSuccs.name());// 发送成功次数
		cv.drawText(value,
				colsWidth * 1 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,
						TotalStruct.TotalAppreciation._SMSSendDelay.name(),
						TotalStruct.TotalAppreciation._SMSSendSuccs.name(), 1,
						""); // 平均发送时延
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit, paramPaint);
		/* 第四行 */
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalAppreciation._SMSReceiveTry.name());// 发送尝试次数
		cv.drawText(value,
				colsWidth * 0 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalAppreciation._SMSReceiveSuccs.name());// 发送成功次数
		cv.drawText(value,
				colsWidth * 1 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,
						TotalStruct.TotalAppreciation._SMSReceiveDelay.name(),
						TotalStruct.TotalAppreciation._SMSReceiveSuccs.name(),
						1, ""); // 平均发送时延
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit, paramPaint);
		/* 七 */
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,
						TotalStruct.TotalAppreciation._SMSPtoPDelay.name(),
						TotalStruct.TotalAppreciation._SMSPtoPCount.name(), 1,
						"");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 6 - rowUpBit, paramPaint);

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
						+ "-Sms.jpg";
				LogUtil.w(tag, "--save current to file---" + path);
				TotalSmsView.this.buildDrawingCache();
				UtilsMethod.SaveBitmapToFile(
						TotalSmsView.this.getDrawingCache(), path);
			} else {
				invalidate();
			}
		}
	};

}
