package com.walktour.gui.analysis.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;

public class ColumnView extends View {
	private Paint xLinePaint;// 坐标轴 轴线 画笔：
	private Paint hLinePaint;// 坐标轴水平内部 虚线画笔
	private Paint titlePaint;// 绘制文本的画笔
	private Paint paint;// 矩形画笔 柱状图的样式信息
	private float[] aniProgress;// 实现动画的值
	// 坐标轴底部的星期数
	private String[] xWeeks;
	/** 默认10中颜色,超过10中，需要set颜色 **/
	private String[] DEFAULT_ITEMS_COLORS = { "#60D1D9", "#35B7E4", "#FE9C29", "#70AD47", "#B8DF72", "#E14956",
			"#f1ee83", "#37c4bc", "#3a6286", "#90214a" };
	private String[] ySteps = new String[] { "100%", "90%", "80%", "70%", "60%", "50%", "40%", "30%", "20%", "10%",
			"0%" };
	/** 汉字说明总高度 */
	private int textSumHeight = 0;

	private int height2x=dp2px(40);
	/***
	 * 图的边距
	 */
	private int bwidth=dp2px(30);
	public ColumnView(Context context, String[] xSteps, float[] values) {
		super(context);
		setWillNotDraw(false);
		this.xWeeks = xSteps;
		this.aniProgress = values;
		init();
		this.invalidate();

	}

	public String[] getDEFAULT_ITEMS_COLORS() {
		return DEFAULT_ITEMS_COLORS;
	}

	public void setDEFAULT_ITEMS_COLORS(String[] dEFAULT_ITEMS_COLORS) {
		DEFAULT_ITEMS_COLORS = dEFAULT_ITEMS_COLORS;
	}

	private void init() {
		xLinePaint = new Paint();
		hLinePaint = new Paint();
		titlePaint = new Paint();
		paint = new Paint();
		// 给画笔设置颜色
		xLinePaint.setColor(Color.DKGRAY);
		hLinePaint.setColor(Color.LTGRAY);
		titlePaint.setColor(Color.BLACK);

	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		/** 在柱状图上面画分类标注 **/
		if (null != xWeeks && xWeeks.length > 0) {
			Paint p = new Paint();
			p.setAntiAlias(true);
			p.setTextSize(dp2px(12));
			int maxLength = dp2px((int) p.measureText(xWeeks[0]));
			for (int i = 1; i < xWeeks.length; i++) {
				if (dp2px((int) p.measureText(xWeeks[i])) < maxLength) {
					maxLength = dp2px((int) p.measureText(xWeeks[i]));
				}
			}
			if (aniProgress != null && aniProgress.length > 0) {
				for (int i = 0; i < aniProgress.length; i++) {
					p.setColor(Color.parseColor(DEFAULT_ITEMS_COLORS[i]));// 设置灰色
					Rect rect = new Rect();// 柱状图的形状
					rect.left = height2x;
					rect.top = i * dp2px(16)+dp2px(4);
					rect.right = height2x + dp2px(16);
					rect.bottom = (i)* dp2px(16) + dp2px(16);
					canvas.drawRect(rect, p);
					p.setColor(Color.BLACK);
					canvas.drawText(xWeeks[i],rect.left+dp2px(20),rect.top+dp2px(10), p);
				}
				textSumHeight =  height2x;
			}
		}
		if (textSumHeight == 0) {
			textSumHeight = height2x;
		}
		int width = getWidth();
		int height = getHeight() - textSumHeight- height2x;
		float hPerHeight = (height ) / (ySteps.length-1);// 分成四部分
		// 绘制y轴
		drawAL(canvas, xLinePaint, bwidth, height + dp2px(10)+ height2x, width - bwidth, height + dp2px(10)+ height2x);
		// 绘制X轴
		drawAL(canvas, xLinePaint, bwidth, height + dp2px(10)+ height2x, bwidth, textSumHeight);

		hLinePaint.setTextAlign(Align.CENTER); 
		//画虚线
		for (int i = 0; i < ySteps.length  ; i++) {
			canvas.drawLine(bwidth, i * hPerHeight + dp2px(10) + textSumHeight, width - bwidth,
					i * hPerHeight + dp2px(10) + textSumHeight, hLinePaint);
		}

		// 绘制 Y 周坐标
		titlePaint.setTextAlign(Align.RIGHT);
		titlePaint.setTextSize(sp2px(12));
		titlePaint.setAntiAlias(true);
		titlePaint.setStyle(Paint.Style.FILL);
		// 设置左部的数字
		for (int i = 0; i < ySteps.length; i++) {
			canvas.drawText(ySteps[i], dp2px(28), i * hPerHeight + textSumHeight, titlePaint);
		}

		float sum = 0;
		if (aniProgress != null && aniProgress.length > 0) {
			for (float f : aniProgress) {
				sum += f;
			}
		}
		// 绘制 X 周 做坐标
		if (null != xWeeks && xWeeks.length > 0) {
			int xAxisLength = width - bwidth;
			int columCount = xWeeks.length + 1;
			int step = xAxisLength / columCount;

			// 绘制矩形
			if (aniProgress != null && aniProgress.length > 0) {
				Paint p = new Paint();
				for (int i = 0; i < aniProgress.length; i++) {// 循环遍历将7条柱状图形画出来
					float value = aniProgress[i];
					paint.setAntiAlias(true);// 抗锯齿效果
					paint.setStyle(Paint.Style.FILL);
					paint.setTextSize(sp2px(15));// 字体大小
					paint.setColor(Color.parseColor("#6DCAEC"));// 字体颜色
					Rect rect = new Rect();// 柱状图的形状
					rect.left = step * (i + 1);
					rect.right = bwidth + step * (i + 1);
					int rh = (int) (height - hPerHeight * value * 10 / sum - 0.5f);
					rect.top = rh +dp2px(10) + height2x;
					rect.bottom = height+dp2px(10)  + height2x;
					p.setColor(Color.parseColor(DEFAULT_ITEMS_COLORS[i]));// 设置灰色
					p.setStyle(Paint.Style.FILL);// 设置填满
					canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, p);// 长方形
				}
			}
		}

	}

	private int dp2px(int value) {
		float v = getContext().getResources().getDisplayMetrics().density;
		return (int) (v * value + 0.5f);
	}

	private int sp2px(int value) {
		float v = getContext().getResources().getDisplayMetrics().scaledDensity;
		return (int) (v * value + 0.5f);
	}

	/**
	 * 画箭头
	 * 
	 * @param sx
	 * @param sy
	 * @param ex
	 * @param ey
	 */
	public void drawAL(Canvas canvas, Paint paint, int sx, int sy, int ex, int ey) {
		double H = 8; // 箭头高度
		double L = 3.5; // 底边的一半
		int x3 = 0;
		int y3 = 0;
		int x4 = 0;
		int y4 = 0;
		double awrad = Math.atan(L / H); // 箭头角度
		double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
		double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
		double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
		double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
		double y_3 = ey - arrXY_1[1];
		double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
		double y_4 = ey - arrXY_2[1];
		Double X3 = new Double(x_3);
		x3 = X3.intValue();
		Double Y3 = new Double(y_3);
		y3 = Y3.intValue();
		Double X4 = new Double(x_4);
		x4 = X4.intValue();
		Double Y4 = new Double(y_4);
		y4 = Y4.intValue();
		// 画线
		canvas.drawLine(sx, sy, ex, ey, paint);
		Path triangle = new Path();
		triangle.moveTo(ex, ey);
		triangle.lineTo(x3, y3);
		triangle.lineTo(x4, y4);
		triangle.close();
		canvas.drawPath(triangle, paint);

	}

	// 计算
	private double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen) {
		double mathstr[] = new double[2];
		// 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
		double vx = px * Math.cos(ang) - py * Math.sin(ang);
		double vy = px * Math.sin(ang) + py * Math.cos(ang);
		if (isChLen) {
			double d = Math.sqrt(vx * vx + vy * vy);
			vx = vx / d * newLen;
			vy = vy / d * newLen;
			mathstr[0] = vx;
			mathstr[1] = vy;
		}
		return mathstr;
	}
}
