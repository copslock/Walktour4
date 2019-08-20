package com.walktour.gui.analysis.view.pie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class LineView extends View {

	// 提供数据源
	private List<EntyExpenses> list;
	private Paint textPaint;
	private int height;
	private int width;

	public List<EntyExpenses> getList() {
		return list;
	}

	public void setList(List<EntyExpenses> list) {
		this.list = list;
	}

	public void initi(List<EntyExpenses> list, PieView Pie) {
		this.list = list;
		this.invalidate();
	}

	@SuppressLint("WrongCall")
	public void setWidth(int width, int height) {
		this.width = width;
		this.height = height;
		this.invalidate();
	}

	public LineView(Context context, AttributeSet attrs, int width, int height) {
		super(context, attrs);
		this.height = height;
		this.width = width;

		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setAntiAlias(true);
		textPaint.setTypeface(Typeface.DEFAULT);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		textPaint.setColor(0x80000000);
		float mWidth = width * 0.45f;
		float mHeight = height * 0.45f;

		// 移动中心
		canvas.translate(width * 0.5f, height * 0.5f);

		float centerRadius = getMin(mWidth, mHeight) * 0.65f;

		// 支出种类
		if (list == null||list.size()<=0) {
			// 没有数据
			textPaint.setColor(0xff87CEFF);
//			textPaint.setTextSize(centerRadius * 0.15f);
//			canvas.drawText("没有数据", -textPaint.measureText("没有数据") / 2f, -centerRadius * 0.1f, textPaint);
			textPaint.setTextSize(centerRadius * 0.5f);
			canvas.drawText("0", -textPaint.measureText("0") / 2f, centerRadius * 0.15f, textPaint);
		} else {
			// 绘制圆圈内文字
			textPaint.setTextSize(centerRadius * 0.5f);
			String textC = list.get(0).getExpensesMainType();
			float widthTextC = textPaint.measureText(textC);
//			canvas.drawText(textC, -widthTextC / 2f, -centerRadius * 0.05f, textPaint);
			// 具体金额
			int sum=0;
			for(EntyExpenses e:list){
				sum+=e.getExpensesNum();
			}
			textC =Integer.parseInt(sum+"")+"";
			widthTextC = textPaint.measureText(textC);
			canvas.drawText(textC, -widthTextC / 2f, 0.15f * centerRadius, textPaint);
		}

	}

	

	public float getMin(float mWidth, float mHeight) {
		float min;
		if (mWidth <= mHeight) {
			min = mWidth;
		} else {
			min = mHeight;
		}
		return min;
	}
}
