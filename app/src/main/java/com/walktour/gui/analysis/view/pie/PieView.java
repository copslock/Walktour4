package com.walktour.gui.analysis.view.pie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class PieView extends View {

	// 饼图显示区域
	private Paint showPaint;
	// 更新数据源
	private List<EntyExpenses> list;
	// 每块区域的角度范围
	private List<EntyAngle> listAngle = new ArrayList<EntyAngle>();;
	private float centerRadius;
	private LineView lineText;
	private int height;
	private int width;
	private float mWidth;
	private float mHeight;
	public final int START_ANGLE = 0;
	public final int SWEEP_ANGLE = 1;
	private String[] arcColors = new String[] { "#60D1D9", "#35B7E4", "#FE9C29", "#70AD47", "#B8DF72", "#E14956",
			"#90214A", "#F1EE83", "#37C4BC", "#3A6286" };
	private List<Integer> colorsUse = new ArrayList<Integer>();
	private int colorCenterCircle = 0xe0ffffff;
	private OnItemChangedListener mOnItemChangedListener;
	private OnCircleClickListener mOnCircleClickListener;
	private boolean isRorate = true;
	private boolean isChecked=false;
	@SuppressWarnings("unused")
	private Context context;
	public PieView(Context context, AttributeSet attrs, int width, int height) {
		super(context, attrs);
		this.context=context;
		this.height = height;
		this.width = width;
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		showPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		showPaint.setColor(0xff87CEFF);
		showPaint.setAntiAlias(true);
	}

	@SuppressLint("WrongCall")
	public void setWidth(int width, int height, boolean isRorate) {
		this.isRorate = isRorate;
		this.width = width;
		this.height = height;
		this.onMeasure(width, height);
		this.invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		showPaint.setStyle(Paint.Style.FILL);
		mWidth = width * 0.7f;
		mHeight = height * 0.7f;
		centerRadius = getMin(mWidth, mHeight) * 0.65f;
		if (list == null||list.size()<=0) { 
			showPaint.setColor(Color.parseColor(arcColors[0]));
			canvas.drawCircle(width/2f, height/2f, centerRadius,showPaint);
			showPaint.setColor(colorCenterCircle);
			canvas.drawCircle(width/2f, height/2f, centerRadius*0.45f,showPaint);
			return;
		}

		listAngle.clear();

		// 移动中心
		canvas.translate(width * 0.5f, height * 0.5f);

		// 绘制外层圆环
		float limit = centerRadius;
		RectF oval = new RectF(-limit, -limit, limit, limit);

		float startEvery = 0;
		for (int i = 0; i < list.size(); i++) {
			showPaint.setColor(colorsUse.get(i));

			float sweepAngle = list.get(i).getExpensesPersent() * 360;

			if (i == 0) {
				startEvery = 90f - sweepAngle / 2f;
			}
			if (i == 1&&isChecked) {
				limit = getMin(mWidth, mHeight) * 0.63f;
				oval = new RectF(-limit, -limit, limit, limit);
			} 
			listAngle.add(new EntyAngle(startEvery, sweepAngle));
			canvas.drawArc(oval, startEvery, sweepAngle, true, showPaint);
			startEvery = startEvery + sweepAngle;
		} 
		// 绘制内层圆
		// if(!(list.get(0).getExpensesMainType()!=null &&
		// list.get(0).getExpensesMainType().equals("没有数据(⊙o⊙)哦"))){
		showPaint.setColor(0x30000000);
		canvas.drawCircle(0, 0, centerRadius * 0.5f, showPaint);
		showPaint.setColor(colorCenterCircle);
		canvas.drawCircle(0, 0, centerRadius * 0.45f, showPaint);
		// }

	}

	private List<EntyExpenses> exchange2Persent(List<EntyExpenses> list, int position, double total) {
		double persent = list.get(position).getExpensesNum() / (double) total;
		list.get(position).setExpensesPersent(Float.valueOf(keep3AfterPoint(new BigDecimal(persent))));
		return list;
	}

	private static String keep3AfterPoint(BigDecimal money) {
		DecimalFormat df = new DecimalFormat("0.000");
		String temp = df.format(money.doubleValue());
		return temp;
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

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		float YCenter = PieView.this.getHeight() * 0.5f;
		float XCenter = PieView.this.getWidth() * 0.5f;

		if ((x - XCenter) * (x - XCenter) + (y - YCenter) * (y - YCenter) > centerRadius * 0.5 * centerRadius * 0.5
				&& (x - XCenter) * (x - XCenter) + (y - YCenter) * (y - YCenter) < centerRadius * centerRadius
				&& event.getAction() == MotionEvent.ACTION_UP) {
			if (null == listAngle || listAngle.size() <= 0)
				return true;
			if (!this.isRorate)
				return true;

			// 点击在彩色圆环上
			// 点击点与圆心之间的距离
			double distance = Math.pow((x - XCenter) * (x - XCenter) + (y - YCenter) * (y - YCenter), 1 / 2.0);
			// 获取到的角度值,第一象限和第二象限无区分,3,4同样
			double angle = (180 * Math.asin((y - YCenter) / distance)) / Math.PI;
			// 第一象限不变,区分第二象限
			if (x - XCenter < 0 && angle < 90 && angle > 0) {
				angle = 180 - angle;
			}
			// 区分第三象限
			else if (x - XCenter < 0 && angle > -90 && angle < 0) {
				angle = 180 - angle;
			}
			// 区分第四象限
			else if (x - XCenter > 0 && angle > -90 && angle < 0) {
				angle = 360 + angle;
			}

			int flag = 0;
			// 处理后判断
			if (angle < listAngle.get(0).getStartAngle())
				angle = angle + 360;
			// 判断
			for (int i = 0; i < listAngle.size(); i++) {
				if (angle > listAngle.get(i).getStartAngle()
						&& angle < listAngle.get(i).getSweepAngle() + listAngle.get(i).getStartAngle()) {
					flag = i;
				}
			}

			if (null != listAngle && listAngle.size() > 0) {
				if (this.mOnItemChangedListener != null) {
					List<EntyExpenses> listNew = new ArrayList<EntyExpenses>();
					for (int i = flag; i < list.size(); i++) {
						listNew.add(list.get(i));
					}
					for (int i = 0; i < flag; i++) {
						listNew.add(list.get(i));
					}
					isChecked=true;
					this.mOnItemChangedListener.onItemChanged(listNew);
				}
			}

			if (flag == 0){
				this.invalidate();
				return true;
			}

			// 旋转动画
			Animation animation = (getRotateAnimation(0,
					90 + 360 - (listAngle.get(flag).getStartAngle() + listAngle.get(flag).getSweepAngle() / 2f)));
			animation.setAnimationListener(new PieAnimationListener(this, lineText, true, flag, colorsUse, list));
			this.startAnimation(animation);
		}
		// 点击在中间区域,不想要该效果可以不加
		else if ((x - XCenter) * (x - XCenter) + (y - YCenter) * (y - YCenter) < centerRadius * 0.5 * centerRadius
				* 0.5) {
//			if (event.getAction() == MotionEvent.ACTION_DOWN) {
//				colorCenterCircle = 0x20000000;
//				PieView.this.invalidate();
//			} else 
				if (event.getAction() == MotionEvent.ACTION_UP) {
				colorCenterCircle = 0xe0ffffff;
				PieView.this.invalidate();
				if (this.mOnCircleClickListener != null) {
					isChecked=false;
					this.mOnCircleClickListener.onCircleOnClick(this);
				}
			}

		}

		return true;
	}

	public List<EntyExpenses> getList() {
		return list;
	}

	public void initi(List<EntyExpenses> list, LineView lineText) {
		this.list = list;
		this.lineText = lineText;

		colorsUse.clear();
		if (list == null) {
			list = new ArrayList<EntyExpenses>();
		}
		if (list.size() == 0) {
			// list.add(new EntyExpenses("没有数据(⊙o⊙)哦", 0));
			// colorsUse.add(0xff000000);
		} else {
			CompareList compare = new CompareList();
			Collections.sort(list, compare);
			// 获取比例
			int total = 0;
			for (EntyExpenses enty : list) {
				total += enty.getExpensesNum();
			}
			float totalPer = 0;
			for (int i = 0; i < list.size(); i++) {
				exchange2Persent(list, i, total);
				if (i / arcColors.length > 0 && list.size() % arcColors.length != 1) {
					colorsUse.add(Color.parseColor(arcColors[i % arcColors.length]));
				} else if (i / arcColors.length > 0 && list.size() % arcColors.length == 1) {
					colorsUse.add(Color.parseColor(arcColors[i % arcColors.length + 1]));
				} else {
					colorsUse.add(Color.parseColor(arcColors[i]));
				}
				if (i < list.size() - 1)
					totalPer += list.get(i).getExpensesPersent();
			}
			list.get(list.size() - 1)
					.setExpensesPersent(Float.valueOf(keep3AfterPoint(new BigDecimal(String.valueOf(1 - totalPer)))));
		}

		this.invalidate();
	}

	public void fresh(List<EntyExpenses> list, List<Integer> colorsUse) {
		this.list = list;
		this.colorsUse = colorsUse;
		this.invalidate();
	}

	public Animation getRotateAnimation(float startangle, float endAngle) {
		RotateAnimation rotate = new RotateAnimation(startangle, endAngle, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(600);
		return rotate;
	}

	public void setOnItemChangedListener(OnItemChangedListener listener) { 
		this.mOnItemChangedListener = listener;
	}

	public void setOnCircleClickListener(OnCircleClickListener listener) { 
		this.mOnCircleClickListener = listener;
	}

	public interface OnItemChangedListener {
		void onItemChanged(List<EntyExpenses> listNew);
	}

	public interface OnCircleClickListener {
		public void onCircleOnClick(PieView view);
	}

	private class CompareList implements Comparator<EntyExpenses> {

		@Override
		public int compare(EntyExpenses lhs, EntyExpenses rhs) {
			if (lhs.getExpensesNum() > rhs.getExpensesNum()) {
				return -1;
			} else if (lhs.getExpensesNum() < rhs.getExpensesNum()) {
				return 1;
			}
			return 0;
		}
	}
}
