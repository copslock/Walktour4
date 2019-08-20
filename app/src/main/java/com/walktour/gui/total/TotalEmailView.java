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

public class TotalEmailView extends BasicTotalView {
	private static String tag = "TotalEmailView";
	private boolean isRegisterReceiver = false;

	public TotalEmailView(Context context) {
		super(context);
	}

	public TotalEmailView(Context context, AttributeSet attrs) {
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
		int tableRows = 9; // 行数
		int tableCols = 3; // 列数
		float colsWidth = tablewidth / tableCols; // 列宽

		/* 画四边 */
		cv.drawLine(startx, marginSize, width - marginSize, marginSize, linePaint);
		cv.drawLine(startx, rowHeight * tableRows, width - marginSize,rowHeight * tableRows, linePaint);
		cv.drawLine(startx, marginSize, startx, rowHeight * tableRows, linePaint);
		cv.drawLine(width - marginSize, marginSize, width - marginSize, rowHeight * tableRows, linePaint);
		/* 画横线 */
		for (int i = 1; i < tableRows; i++) {
			startx = 1;
			starty = rowHeight * i;
			stopx = width - marginSize;
			stopy = rowHeight * i;
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
		}
		/* 画竖线 */
		// for(int i=0;i<tableCols - 1;i++){
		startx = colsWidth * 2;
		starty = rowHeight;
		stopx = colsWidth * 2;
		stopy = rowHeight * tableRows;
		cv.drawLine(startx, starty, stopx, stopy, linePaint);
		// }

		float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2
		
		String paraname;
		/* 第一行 */
		
		cv.drawText(getResources().getString(R.string.total_email_title), (this.getWidth() - 
		        fontPaint.measureText(getResources().getString(R.string.total_email_title)))/2, rowHeight * 1 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_SendAttemptCounts);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 2 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_SendSuccessCounts);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 3 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_sendSuccessRate);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 4 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_SendAverageSpeed);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 5 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_ReceiveAttemptCounts);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 6 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_ReceiveSuccessCounts);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 7 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_ReceiveSuccessRate);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 8 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_ReceiveAverageSpeed);
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 9 - rowUpBit, fontPaint);

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
						TotalStruct.TotalAppreciation._EmailSendTry.name());// 发送尝试次数
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 2 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalAppreciation._EmailSendSuccess.name());// 发送成功次数
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,
						TotalStruct.TotalAppreciation._EmailSendSuccess.name(),
						TotalStruct.TotalAppreciation._EmailSendTry.name(),
						100, "%");// 发送成功次数
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,
						TotalStruct.TotalAppreciation._EmailSendSumSize.name(),//byte
						TotalStruct.TotalAppreciation._EmailSendAllTime.name(), //ms
						8,//界面显示为kbps<------byte/ms转换成 *1000f/1000f
						"");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalAppreciation._EmailReceiveTry.name());// 发送尝试次数
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 6 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalAppreciation._EmailReceiveSuccess
								.name());// 发送成功次数
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 7 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,
						TotalStruct.TotalAppreciation._EmailReceiveSuccess
								.name(),
						TotalStruct.TotalAppreciation._EmailReceiveTry.name(),
						100, "%");// 发送成功次数
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 8 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,
						TotalStruct.TotalAppreciation._EmailReceSumSize.name(),//byte
						TotalStruct.TotalAppreciation._EmailReceAllTime.name(), //ms
						8,//界面显示为kbps<------byte/ms转换成 *1000f/1000f 
						"");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 9 - rowUpBit, paramPaint);

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
						+ "-Email.jpg";
				LogUtil.w(tag, "--save current to file---" + path);
				TotalEmailView.this.buildDrawingCache();
				UtilsMethod.SaveBitmapToFile(
						TotalEmailView.this.getDrawingCache(), path);
			} else {
				invalidate();
			}
		}
	};
}
