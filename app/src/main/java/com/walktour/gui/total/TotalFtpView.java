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

public class TotalFtpView extends BasicTotalView {
	private static String tag = "TotalFtpView";
	private boolean isRegisterReceiver = false;

	public TotalFtpView(Context context) {
		super(context);
	}

	public TotalFtpView(Context context, AttributeSet attrs) {
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
		int tableRows = 17; // 行数 10
		int tableCols = 4; // 列数
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
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
		}
		for (int i = 0; i < tableCols - 2; i++) {
			startx = colsWidth * (i + 2);
			starty = marginSize;
			stopx = colsWidth * (i + 2);
			stopy = rowHeight * tableRows;
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
		}

		float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2
		
		String paraname;
        paraname = getContext().getString(R.string.total_ftp);// "尝试次数";
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 1 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_download); // UP
		cv.drawText(paraname,
				colsWidth * 2 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 1 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_upload);// DOWN
		cv.drawText(paraname,
				colsWidth * 3 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 1 - rowUpBit, fontPaint);

		paraname = getContext().getString(R.string.total_attemptCount);// "尝试次数";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 2 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_SuccessCounts);// "成功次数";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 3 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_SuccessRate);// "成功率";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
				/ 2, rowHeight * 4 - rowUpBit, fontPaint);
		
		paraname = getContext().getString(R.string.total_DropRate); // "掉线率";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 5 - rowUpBit, fontPaint);

		paraname = getContext().getString(R.string.total_AppAverageSpeed); // 应用层平均速率
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 6 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_max_vaule); // 峰值速率
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 7 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_downTotalSize); //下载总数据量
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 8 - rowUpBit, fontPaint);
		
		paraname = getContext().getString(R.string.total_downTotalTimes); // 下载时长
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 9 - rowUpBit, fontPaint);


		//MutilFTP多路并发业务
		
        paraname = getContext().getString(R.string.total_multiftp);// "尝试次数";
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 10 - rowUpBit, fontPaint);
        
        paraname = getContext().getString(R.string.total_download); // UP
        cv.drawText(paraname,
                colsWidth * 2 + (colsWidth - fontPaint.measureText(paraname)) / 2,
                rowHeight * 10 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_upload);// DOWN
        cv.drawText(paraname,
                colsWidth * 3 + (colsWidth - fontPaint.measureText(paraname)) / 2,
                rowHeight * 10 - rowUpBit, fontPaint);

        paraname = getContext().getString(R.string.total_attemptCount);// "尝试次数";
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 11 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_SuccessCounts);// "成功次数";
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 12 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_DropRate); // "掉线率";
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 13 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_AppAverageSpeed); // 应用层平均速率
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
                        / 2, rowHeight * 14 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_max_vaule); // 峰值速率
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 15 - rowUpBit, fontPaint);
        paraname = getContext().getString(R.string.total_downTotalSize); // 下载总数据量
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 16 - rowUpBit, fontPaint);
		
		paraname = getContext().getString(R.string.total_downTotalTimes); // 下载时长
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 17 - rowUpBit, fontPaint);

	        
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
		int tableCols = 4; // 列数
		float colsWidth = tablewidth / tableCols; // 列宽
		float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2

		String value;

		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalFtp._downtrys.name()); // "尝试次数";
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 2 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalFtp._uptrys.name()); // "尝试次数";
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 2 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalFtp._downSuccs.name()); // "接通次数";
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalFtp._upSuccs.name()); // "接通次数";
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit, paramPaint);
		
		
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,  
						TotalStruct.TotalFtp._downSuccs.name(),
						TotalStruct.TotalFtp._downtrys.name(), 100, "%");  //成功率
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit, paramPaint);
		
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,  
						TotalStruct.TotalFtp._upSuccs.name(),
						TotalStruct.TotalFtp._uptrys.name(), 100, "%");  //成功率
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit, paramPaint);
		
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,   // "掉话率";
						TotalStruct.TotalFtp._downDrops.name(),
						TotalStruct.TotalFtp._downtrys.name(), 100, "%");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit, paramPaint);
		// "掉话率";
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,
						TotalStruct.TotalFtp._upDrops.name(),
						TotalStruct.TotalFtp._uptrys.name(), 100, "%");
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit, paramPaint);
		// 应用层平均速率：每次平均值的累加/成功次数
		/*
		 * value = ""+TotalDataByGSM.getHashMapMultiple(hMap,
		 * TotalStruct.TotalFtp._downAverageThr.name(),
		 * TotalStruct.TotalFtp._downSuccs.name(),0.001f,"");
		 */
		/**
		 * 原公式:(当前传输大小 * 8 / ((从获得文件大小到最后一个数据包时间) / 1000f)) / kByte //0.9765625
		 * = 1000 / 1000 为乘时间的1000再除以1000得S/KByte
		 * */
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,
						TotalStruct.TotalFtp._downCurrentSize.name(),	//bit
						TotalStruct.TotalFtp._downCurrentTimes.name(),  //ms
				        8f*1000/1024,//界面显示为kbps<------bit/ms转换成 1000f/1000f
						"");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 6 - rowUpBit, paramPaint);
		// 应用层平均速率：每次平均值的累加/成功次数
		/*
		 * value = ""+TotalDataByGSM.getHashMapMultiple(hMap,
		 * TotalStruct.TotalFtp._upAverageThr.name(),
		 * TotalStruct.TotalFtp._upSuccs.name(),0.001f,"");
		 */
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(hMap,
						TotalStruct.TotalFtp._upCurrentSize.name(),//bit
						TotalStruct.TotalFtp._upCurrentTimes.name(), //ms
				        8f*1000/1024 ,//界面显示为kbps<------bit/ms转换成 1000f/1000f
						"");
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 6 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
				TotalStruct.TotalFtp._down_max_value.name(),UtilsMethod.kbyteRage ); // "峰值速率";
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 7 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
				TotalStruct.TotalFtp._up_max_value.name(),UtilsMethod.kbyteRage ); // "峰值速率";
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 7 - rowUpBit, paramPaint);

		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalFtp._downCurrentSize.name(),UtilsMethod.kbyteRage * 8f); // "下载总数据量";
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 8 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalFtp._upCurrentSize.name(),UtilsMethod.kbyteRage * 8f); // "下载总数据量";
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 8 - rowUpBit, paramPaint);
		
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalFtp._downCurrentTimes.name(),1000); // "下载时长";
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 9 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
						TotalStruct.TotalFtp._upCurrentTimes.name(),1000); // "下载时长";
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 9 - rowUpBit, paramPaint);
		
	    /****************************Multi FTP **************************************/
		
		
	      value = ""
	                + TotalDataByGSM.getHashMapValue(hMap,
	                        TotalStruct.TotalFtp.m_downtrys.name()); // "尝试次数";
	        cv.drawText(value,
	                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
	                rowHeight * 11 - rowUpBit, paramPaint);
	        value = ""
	                + TotalDataByGSM.getHashMapValue(hMap,
	                        TotalStruct.TotalFtp.m_uptrys.name()); // "尝试次数";
	        cv.drawText(value,
	                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
	                rowHeight * 11 - rowUpBit, paramPaint);
	        value = ""
	                + TotalDataByGSM.getHashMapValue(hMap,
	                        TotalStruct.TotalFtp.m_downSuccs.name()); // "接通次数";
	        cv.drawText(value,
	                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
	                rowHeight * 12 - rowUpBit, paramPaint);
	        value = ""
	                + TotalDataByGSM.getHashMapValue(hMap,
	                        TotalStruct.TotalFtp.m_upSuccs.name()); // "接通次数";
	        cv.drawText(value,
	                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
	                rowHeight * 12 - rowUpBit, paramPaint);
	        // "掉话率";
	        value = ""
	                + TotalDataByGSM.getHashMapMultiple(hMap,
	                        TotalStruct.TotalFtp.m_downDrops.name(),
	                        TotalStruct.TotalFtp.m_downtrys.name(), 100, "%");
	        cv.drawText(value,
	                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
	                rowHeight * 13 - rowUpBit, paramPaint);
	        // "掉话率";
	        value = ""
	                + TotalDataByGSM.getHashMapMultiple(hMap,
	                        TotalStruct.TotalFtp.m_upDrops.name(),
	                        TotalStruct.TotalFtp.m_uptrys.name(), 100, "%");
	        cv.drawText(value,
	                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
	                rowHeight * 13 - rowUpBit, paramPaint);
	        // 应用层平均速率：每次平均值的累加/成功次数
	        /*
	         * value = ""+TotalDataByGSM.getHashMapMultiple(hMap,
	         * TotalStruct.TotalFtp._downAverageThr.name(),
	         * TotalStruct.TotalFtp._downSuccs.name(),0.001f,"");
	         */
	        /**
	         * 原公式:(当前传输大小 * 8 / ((从获得文件大小到最后一个数据包时间) / 1000f)) / kByte //0.9765625
	         * = 1000 / 1000 为乘时间的1000再除以1000得S/KByte
	         * */
	        value = ""
	                + TotalDataByGSM.getHashMapMultiple(hMap,
	                        TotalStruct.TotalFtp.m_downCurrentSize.name(),   //bit
	                        TotalStruct.TotalFtp.m_downCurrentTimes.name(),  //ms
					        8f*1000/1024 ,//界面显示为kbps<------bit/ms转换成 1000f/1000f
	                        "");
	        cv.drawText(value,
	                colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
	                rowHeight * 14 - rowUpBit, paramPaint);
	        // 应用层平均速率：每次平均值的累加/成功次数
	        /*
	         * value = ""+TotalDataByGSM.getHashMapMultiple(hMap,
	         * TotalStruct.TotalFtp._upAverageThr.name(),
	         * TotalStruct.TotalFtp._upSuccs.name(),0.001f,"");
	         */
	        value = ""
	                + TotalDataByGSM.getHashMapMultiple(hMap,
	                        TotalStruct.TotalFtp.m_upCurrentSize.name(),//bit
	                        TotalStruct.TotalFtp.m_upCurrentTimes.name(), //ms
					        8f*1000/1024 ,//界面显示为kbps<------bit/ms转换成 1000f/1000f
	                        "");
	        cv.drawText(value,
	                colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
	                rowHeight * 14 - rowUpBit, paramPaint);

		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
				TotalStruct.TotalFtp.m_down_max_value.name(),UtilsMethod.kbyteRage ); // "峰值速率";
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 15 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(hMap,
				TotalStruct.TotalFtp.m_up_max_value.name(), UtilsMethod.kbyteRage ); // "峰值速率";
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 15 - rowUpBit, paramPaint);

	        value = ""
					+ TotalDataByGSM.getHashMapValue(hMap,
							TotalStruct.TotalFtp.m_downCurrentSize.name(),UtilsMethod.kbyteRage * 8f); // "下载总数据量";
			cv.drawText(value,
					colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
					rowHeight * 16 - rowUpBit, paramPaint);
			value = ""
					+ TotalDataByGSM.getHashMapValue(hMap,
							TotalStruct.TotalFtp.m_upCurrentSize.name(), UtilsMethod.kbyteRage * 8f); // "下载总数据量";
			cv.drawText(value,
					colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
					rowHeight * 16 - rowUpBit, paramPaint);
			
			value = ""
					+ TotalDataByGSM.getHashMapValue(hMap,
							TotalStruct.TotalFtp.m_downCurrentTimes.name(),1000); // "下载时长";
			cv.drawText(value,
					colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
					rowHeight * 17 - rowUpBit, paramPaint);
			value = ""
					+ TotalDataByGSM.getHashMapValue(hMap,
							TotalStruct.TotalFtp.m_upCurrentTimes.name(),1000); // "下载时长";
			cv.drawText(value,
					colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
					rowHeight * 17 - rowUpBit, paramPaint);
			    
		LogUtil.w(tag,
				"--_down_max_value:"
						+ TotalDataByGSM.getHashMapValue(hMap,
								TotalStruct.TotalFtp._down_max_value.name())
						+ "--_up_max_value:"
						+ TotalDataByGSM.getHashMapValue(hMap,
								TotalStruct.TotalFtp._up_max_value.name())
						+ "--m_down_max_value:"
						+ TotalDataByGSM.getHashMapValue(hMap,
								TotalStruct.TotalFtp.m_down_max_value.name())
						+ "--m_up_max_value:"
						+ TotalDataByGSM.getHashMapValue(hMap,
								TotalStruct.TotalFtp.m_up_max_value.name()));
		cv.save();
		cv.restore();
		return null;

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		LogUtil.w(tag, "----onAttachedToWindow----");
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
		LogUtil.w(tag, "----onDetachedFromWindow----");
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
						+ "-FTP.jpg";
				LogUtil.w(tag, "--save current to file---" + path);
				TotalFtpView.this.buildDrawingCache();
				UtilsMethod.SaveBitmapToFile(
						TotalFtpView.this.getDrawingCache(), path);
			} else {
				invalidate();
			}
		}
	};
}
