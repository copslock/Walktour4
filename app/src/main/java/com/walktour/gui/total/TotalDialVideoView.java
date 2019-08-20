package com.walktour.gui.total;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.BasicTotalView;
import com.walktour.gui.R;
import com.walktour.model.XYModel;

import java.util.HashMap;

public class TotalDialVideoView extends BasicTotalView {

	private static String tag = "TotalDialVideoView";

	private boolean isRegisterReceiver = false;

	private Paint pwfont;
	private Paint pdailfont;

	public TotalDialVideoView(Context context) {
		super(context);
	}

	public TotalDialVideoView(Context context, AttributeSet attrs) {
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
	protected Bitmap CreateTable(Canvas cv) {
		int width = this.getWidth();

		float starty = 0;
		float stopx = 0;
		float stopy = 0;
		float tablewidth = width;
		int tableCols = 4; // 列数
		float colsWidth = tablewidth / tableCols; // 列宽

		cv.drawLine(1, marginSize, width - marginSize, marginSize, linePaint);//外框上线
		cv.drawLine(1, rowHeight * 7, width - marginSize, rowHeight * 7, linePaint);//下线
		cv.drawLine(1, marginSize, 1, rowHeight * 7, linePaint);//左线
		cv.drawLine(width - marginSize, marginSize, width - marginSize, rowHeight * 7,//右线
		        linePaint);
		float startx = 0;

		//行线
		for (int i = 0; i < 7 - 1; i++) {
			startx = 1;
			starty = rowHeight * (i + 1);
			stopx = width - marginSize;
			stopy = rowHeight * (i + 1);
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
		}
		
		//竖线
		cv.drawLine( colsWidth*2, marginSize, colsWidth*2, rowHeight * 7, linePaint);
		cv.drawLine( colsWidth*3, marginSize, colsWidth*3, rowHeight * 7, linePaint);

		float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2
		String paraname;
		paraname = getContext().getString(R.string.total_call_mo);// "尝试次数";
		cv.drawText(paraname,
				colsWidth * 2 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 1 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_call_mt);// "尝试次数";
		cv.drawText(paraname,
				colsWidth * 3 + (colsWidth - fontPaint.measureText(paraname)) / 2,
				rowHeight * 1 - rowUpBit, fontPaint);

		paraname = getContext().getString(R.string.total_attemptCount);// "尝试次数";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 2 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_callconnected);// "成功次数";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 3 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_DropCall);// "接通次数";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 4 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_callSuccRate);// "成功率";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 5 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_dropCallRate);// "掉话功率";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 6 - rowUpBit, fontPaint);
		paraname = getContext().getString(R.string.total_callDelay);// "呼叫时延";
		cv.drawText(paraname,
				colsWidth * 0 + (colsWidth * 2 - fontPaint.measureText(paraname))
						/ 2, rowHeight * 7 - rowUpBit, fontPaint);
			
	                        
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
			HashMap<String, Long> unifyTimes) {
		int width = this.getWidth();
		float tablewidth = width;
		int tableCols = 4; // 列数
		float colsWidth = tablewidth / tableCols; // 列宽
		float rowUpBit = (rowHeight - textSize) / 2; // 指定行上升位数,为行高-字体高度 再除2

		String value;

		value = ""
				+ TotalDataByGSM.getHashMapValue(unifyTimes,
						TotalStruct.TotalDial._video_moTrys.name()); // "尝试次数";
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 2 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(unifyTimes,
						TotalStruct.TotalDial._video_mtTrys.name()); // "尝试次数";
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 2 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(unifyTimes,
						TotalStruct.TotalDial._video_moConnects.name()); // "接通次数";
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(unifyTimes,
						TotalStruct.TotalDial._video_mtConnects.name()); // "接通次数";
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 3 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(unifyTimes,
						TotalStruct.TotalDial._video_moDropcalls.name()); // "掉话次数";
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit, paramPaint);
		value = ""
				+ TotalDataByGSM.getHashMapValue(unifyTimes,
						TotalStruct.TotalDial._video_mtDropcalls.name()); // "掉话次数";
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 4 - rowUpBit, paramPaint);
		// "接通率" 接通成功次数/尝试次数
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(unifyTimes,
						TotalStruct.TotalDial._video_moConnects.name(),
						TotalStruct.TotalDial._video_moTrys.name(), 100, "%");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit, paramPaint);
		// "接通率" 接通成功次数/尝试次数
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(unifyTimes,
						TotalStruct.TotalDial._video_mtConnects.name(),
						TotalStruct.TotalDial._video_mtTrys.name(), 100, "%");
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 5 - rowUpBit, paramPaint);
		// "掉话率" 掉话次数/尝试次数
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(unifyTimes,
						TotalStruct.TotalDial._video_moDropcalls.name(),
						TotalStruct.TotalDial._video_moConnects.name(), 100, "%");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 6 - rowUpBit, paramPaint);
		// "掉话率" 掉话次数/尝试次数
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(unifyTimes,
						TotalStruct.TotalDial._video_mtDropcalls.name(),
						TotalStruct.TotalDial._video_mtConnects.name(), 100, "%");
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 6 - rowUpBit, paramPaint);
		// 呼叫时间延：试呼成功的延累加/呼叫尝试次数
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(unifyTimes,
						TotalStruct.TotalDial._video_moCalldelay.name(),
						TotalStruct.TotalDial._video_moDelaytimes.name(), 0.001f, "");
		cv.drawText(value,
				colsWidth * 2 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 7 - rowUpBit, paramPaint);
		// 呼叫时间延：试呼成功的延累加/呼叫尝试次数
		value = ""
				+ TotalDataByGSM.getHashMapMultiple(unifyTimes,
						TotalStruct.TotalDial._video_mtCalldelay.name(),
						TotalStruct.TotalDial._video_mtDelaytimes.name(), 0.001f, "");
		cv.drawText(value,
				colsWidth * 3 + (colsWidth - paramPaint.measureText(value)) / 2,
				rowHeight * 7 - rowUpBit, paramPaint);
			
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
				TotalDialVideoView.this.buildDrawingCache();
				UtilsMethod.SaveBitmapToFile(
						TotalDialVideoView.this.getDrawingCache(), path);
			} else {
				invalidate();
			}
		}
	};

	/**
	 * 绘制饼状图<BR>
	 * [功能详细描述]
	 * 
	 * @param colors
	 *            颜色数组
	 * @param number
	 *            点数数量
	 * @param strs
	 *            显示字符
	 * @param param
	 *            参数
	 * @return 扇形图
	 */
	public Bitmap drawPieChart(int[] colors, long[] number, String[] strs,
			String param) {
		Bitmap bit_pie = Bitmap.createBitmap(480, 300, Config.ARGB_8888);
		float radians[] = new float[number.length];
		long numSum = 0;
		for (int i = 0; i < number.length; i++) {
			numSum += number[i];
		}
		for (int i = 0; i < number.length; i++) {
			radians[i] = (number[i] * 360) / numSum;
		}
		Canvas c = new Canvas(bit_pie);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize(20);
		// paint.setShadowLayer(3f, 2, 2f, Color.BLACK);
		RectF oval = new RectF(50, 50, 250, 250);
		float start_radian = 0;
		for (int i = 0; i < radians.length; i++) {

			paint.setColor(colors[i]);
			c.drawArc(oval, start_radian, radians[i], true, paint);
			c.drawRect(300, 90 + i * 30, 340, 110 + i * 30, paint);
			c.drawText(strs[i], 360, 107 + i * 30, pdailfont);
			if (radians[i] == 0) {
				continue;
			}

			// double x = (start_radian+radians[i]/2)*Math.PI / 180;
			int percentv = (int) (100 * ((float) number[i] / (float) numSum));
			if (percentv != 0) {
				float showwidth = pdailfont.measureText(percentv + "%");
				FontMetrics fm = pdailfont.getFontMetrics();// 得到系统默认字体属性
				int mFontH = (int) (Math.ceil(fm.descent - fm.top) + 2);// 获得字体高
				XYModel xymode = getXY(start_radian, start_radian + radians[i],
						100);
				double xx = (double) 150 + xymode.getX();
				double yy = (double) 150 - xymode.getY();
				c.drawText(percentv + "%", (int) (xx - (double) showwidth / 2),
						(int) (yy + mFontH / 4), pdailfont);
				// c.drawLine(150, 150, (int)xx,(int)yy,pdailfont);
				// c.drawText(percentv+"%",(float)(150+50*Math.cos(x))-showwidth/2,
				// (float)(150+50*(Math.sin(x))), pdailfont);
			}
			start_radian += radians[i];
		}
		pwfont.setTextSize(24);
		c.drawText(param, 290, 67, pdailfont);
		pwfont.setTextSize(18);
		c.drawText("Mean", 290, 117 + (colors.length) * 30, pdailfont);
		// float strwidth=pdailfont.measureText("Mean");
		String meanv = "";
		/*
		 * if(SignalAssort.signalcount!=0){
		 * meanvalue=(float)SignalAssort.signalSum
		 * /(float)SignalAssort.signalcount; DecimalFormat format = new
		 * DecimalFormat("0.00"); meanv=format.format(meanvalue); }
		 */
		c.drawText(meanv + "", 360, 117 + (colors.length) * 30, pdailfont);
		return bit_pie;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param startdu
	 *            开始角度
	 * @param enddu
	 *            结束角度
	 * @param radian
	 *            半径
	 * @return
	 */
	private XYModel getXY(float startdu, float enddu, int radian) {
		double sdu = (startdu) * Math.PI / 180;
		double edu = (enddu) * Math.PI / 180;

		double x1 = 0;
		double y1 = 0;
		double x2 = 0;
		double y2 = 0;

		if ((0 <= startdu && startdu <= 90)) {// 第一
			x1 = (double) radian * Math.cos(sdu);
			y1 = 0 - (double) radian * Math.sin(sdu);
		}

		if ((90 < startdu && startdu <= 180)) {// 第二
			x1 = (double) radian * Math.cos(sdu);
			y1 = 0 - (double) radian * Math.sin(sdu);
		}

		if ((180 < startdu && startdu <= 270)) {// 第三
			x1 = (double) radian * Math.cos(sdu);
			y1 = 0 - (double) radian * Math.sin(sdu);
		}

		if ((270 < startdu && startdu <= 360)) {// 第4
			x1 = (double) radian * Math.cos(sdu);
			y1 = 0 - (double) radian * Math.sin(sdu);
		}
		// /////////////////////////
		if ((0 <= enddu && enddu <= 90)) {// 第一
			x2 = (double) radian * Math.cos(edu);
			y2 = 0 - (double) radian * Math.sin(edu);
		}

		if ((90 < enddu && enddu <= 180)) {// 第二
			x2 = (double) radian * Math.cos(edu);
			y2 = 0 - (double) radian * Math.sin(edu);
		}

		if ((180 < enddu && enddu <= 270)) {// 第三
			x2 = (double) radian * Math.cos(edu);
			y2 = 0 - (double) radian * Math.sin(edu);
		}

		if ((270 < enddu && enddu <= 360)) {// 第三
			x2 = (double) radian * Math.cos(edu);
			y2 = 0 - (double) radian * Math.sin(edu);
		}

		double xx = (x1 + x2) / 2;
		double yy = (y1 + y2) / 2;
		if (enddu - startdu > 180) {
			xx = 0 - xx;
			yy = 0 - yy;
		}

		int sqrsum = (int) (xx * xx) + (int) (yy * yy);// 求出半径一定大于圆半径一半以上。
		float factor = 0.1f;
		while (sqrsum < ((double) radian * (double) radian / 3)) {
			xx = xx * (1 + factor);
			yy = yy * (1 + factor);
			factor = factor + 0.1f;
			sqrsum = (int) (xx * xx) + (int) (yy * yy);
		}

		factor = 0.01f;
		while (sqrsum > ((double) radian * (double) radian / 3.01)) {
			xx = xx * (1 - factor);
			yy = yy * (1 - factor);
			factor = factor + 0.01f;
			sqrsum = (int) (xx * xx) + (int) (yy * yy);
		}

		XYModel xymodel = new XYModel();
		xymodel.setX(xx);
		xymodel.setY(yy);
		return xymodel;
	}
}
