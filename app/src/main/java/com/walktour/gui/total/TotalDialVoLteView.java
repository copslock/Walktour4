package com.walktour.gui.total;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.BasicTotalView;
import com.walktour.gui.R;
import com.walktour.model.TotalMeasureModel;

import java.util.HashMap;

/**
 * VoLTE语音统计
 * @author zhihui.lian
 */
public class TotalDialVoLteView extends BasicTotalView {

	private static String tag = "TotalDialView";

	private boolean isRegisterReceiver = false;

	private Paint pwfont;
	private Paint pdailfont;

	public TotalDialVoLteView(Context context) {
		super(context);
	}

	public TotalDialVoLteView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setDrawingCacheEnabled(false);

		pdailfont = new Paint();
		pdailfont.setTextSize(20);
		pdailfont.setFilterBitmap(true);
		pdailfont.setAntiAlias(true);
		pdailfont.setColor(Color.WHITE);

		pwfont = new Paint();
		pwfont.setTextSize(18);
		pwfont.setFilterBitmap(true);
		pwfont.setAntiAlias(true);
		pwfont.setColor(Color.WHITE);

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
	private int rowCount = 8;
	
	protected void CreateTable(Canvas cv) {
		int width = this.getWidth();
		float starty = 0;
		float stopx = 0;
		float stopy = 0;
		float tablewidth = width;
		int tableCols = 4; // 列数
		float colsWidth = tablewidth / tableCols; // 列宽

		cv.drawLine(1, marginSize, width - marginSize, marginSize, linePaint);//外框上线
		cv.drawLine(1, rowHeight * rowCount, width - marginSize, rowHeight * rowCount, linePaint);//下线
		cv.drawLine(1, marginSize, 1, rowHeight * rowCount, linePaint);//左线
		cv.drawLine(width - marginSize, marginSize, width - marginSize, rowHeight * rowCount,//右线
		        linePaint);
		float startx = 0;

		//行线
		for (int i = 0; i < rowCount - 1; i++) {
			startx = 1;
			starty = rowHeight * (i + 1);
			stopx = width - marginSize;
			stopy = rowHeight * (i + 1);
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
		}
		
		//竖线
		cv.drawLine( colsWidth*2, marginSize, colsWidth*2, rowHeight * 4, linePaint);
		cv.drawLine( colsWidth*3, marginSize, colsWidth*3, rowHeight * 4, linePaint);

		float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2
		String paraname;
        paraname = "VoLTE KPI";
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2,
                rowHeight * 1 - rowUpBit, fontPaint);
		
		paraname = getContext().getString(R.string.total_call_mo);
		cv.drawText(paraname,
				colsWidth * 2 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 1 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_call_mt);
		cv.drawText(paraname,
				colsWidth * 3 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 1 - rowUpBit, fontPaint);

		paraname = getContext().getString(R.string.total_dail_call_success);			    //
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 2 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_dail_call_duration);				//
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))          
						/ 2, rowHeight * 3 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_dail_call_ratio);					//
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 4 - rowUpBit, fontPaint);
		
		
		/*paraname = getContext().getString(R.string.total_dail_call_success_ratio);          //
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 5 - rowUpBit, fontPaint);*/
		//将原来的切换成功率替换成无线参数的最大最小平值信息
		tableCols = 5; // 列数
		colsWidth = tablewidth / tableCols; // 列宽
		
		for (int i = 1; i < tableCols - 1; i++) {
			startx = colsWidth * (i + 1);
			starty = 1 + rowHeight * 4;
			stopx = colsWidth * (i + 1);
			stopy = rowHeight * rowCount;
			cv.drawLine(startx, starty+ marginSize, stopx, stopy + marginSize, linePaint);
		}
		
		paraname = getContext().getString(R.string.total_para);// "参数名称";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 5 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_max);// "最大";
		cv.drawText(paraname,
				colsWidth * 2 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 5 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_min);// "最小";
		cv.drawText(paraname,
				colsWidth * 3 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 5 - rowUpBit + marginSize, fontPaint);
		paraname = getContext().getString(R.string.total_average);// "平均";
		cv.drawText(paraname,
				colsWidth * 4 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 5 - rowUpBit + marginSize, fontPaint);
		
		
		paraname = getContext().getString(R.string.total_dail_call_jitter);					//			
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 6 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_dail_call_latency);				//
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 7 - rowUpBit, fontPaint);
		
        paraname = getContext().getString(R.string.total_dail_call_loss);					//		
        cv.drawText(paraname,
                colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname)) / 2,
                rowHeight * 8 - rowUpBit, fontPaint);
		
		cv.save();
		cv.restore();
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
			HashMap<String, Long> unifyTimes) {
		int width = this.getWidth();
		float tablewidth = width;
		int tableCols = 4; // 列数
		float colsWidth = tablewidth / tableCols; // 列宽
		float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2

		String value;

		// "呼叫建立成功率" 接通成功次数/尝试次数
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(unifyTimes,
						TotalStruct.TotalDial._volte_moConnects.name(),
						TotalStruct.TotalDial._volte_moTrys.name(), 100, "%");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 2 - rowUpBit, paramPaint);
		// "叫建立成功率" 接通成功次数/尝试次数
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(unifyTimes,
						TotalStruct.TotalDial._volte_mtConnects.name(),
						TotalStruct.TotalDial._volte_mtTrys.name(), 100, "%");
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 2 - rowUpBit, paramPaint);
		
		// 平均呼叫建立时间：试呼成功的延累加/呼叫尝试次数
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(unifyTimes,
						TotalStruct.TotalDial._volte_moCalldelay.name(),
						TotalStruct.TotalDial._volte_moDelaytimes.name(), 0.001f, "");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit, paramPaint);
		
		// 呼叫时间延：试呼成功的延累加/呼叫尝试次数
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(unifyTimes,
						TotalStruct.TotalDial._volte_mtCalldelay.name(),
						TotalStruct.TotalDial._volte_mtDelaytimes.name(), 0.001f, "");
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit, paramPaint);
		
		// "掉话率" 掉话次数/尝试次数
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(unifyTimes,
						TotalStruct.TotalDial._volte_moDropcalls.name(),
						TotalStruct.TotalDial._volte_moConnects.name(), 100, "%");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit, paramPaint);
		// "掉话率" 掉话次数/尝试次数
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(unifyTimes,
						TotalStruct.TotalDial._volte_mtDropcalls.name(),
						TotalStruct.TotalDial._volte_mtConnects.name(), 100, "%");
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit, paramPaint);
		
		
		
		/*// "切换成功率"
		value = "";
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit, paramPaint);
		// "切换成功率"
		value = "";
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit, paramPaint);*/
		
		tableCols = 5; // 列数
		colsWidth = tablewidth / tableCols; // 列宽
		TotalMeasureModel data =  null;
		HashMap<String, TotalMeasureModel> hMap = TotalDataByGSM.getInstance().getMeasuePara();
		
		// 抖动
		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._RFC1889_Jitter.name());
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
		
		
		//包时延
		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._Packet_Delay.name());
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
		
		
		// 丢包率 总丢包数Packet LossCount/（总丢包数Packet LossCount+接收包总数SIP Rx RTP Packet Num）
		//Packet LossCount          key：7F1D401A
		//SIP Rx RTP Packet Num     key：7F1D4033
		data = TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._Packet_LossCount.name());
		TotalMeasureModel data2 =  TotalDataByGSM.getHashMapMeasure(hMap,
				TotalStruct.TotalMeasurePara._SIP_Rx_RTP_Packet_Num.name());
		
		value = "-" ;
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 8 - rowUpBit + marginSize, paramPaint);
		value = "-" ;
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
		LogUtil.w(tag, "----onDetachedFromWindow----");
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
						+ "-Dial.jpg";
				LogUtil.w(tag, "--save current to file---" + path);
				TotalDialVoLteView.this.buildDrawingCache();
				UtilsMethod.SaveBitmapToFile(
						TotalDialVoLteView.this.getDrawingCache(), path);
			} else {
				invalidate();
			}
		}
	};

	
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = (int) (rowHeight * (rowCount + 1)) + (marginSize * 2);
		setMeasuredDimension(measureWidth(widthMeasureSpec), height);
	}

	private int measureWidth(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		int result = 500;
		if (specMode == MeasureSpec.AT_MOST) {
			result = specSize;
		} else if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		}

		return result;
	}
}
