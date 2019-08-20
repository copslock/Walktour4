package com.walktour.gui.analysis.csfb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.walktour.gui.R;
import com.walktour.gui.listener.OnPieChartItemSelectedLinstener;

import java.util.LinkedList;
import java.util.List;

/**
 * 自定义可旋转饼状图
 * zhihui.lian 
 */
@SuppressLint("DrawAllocation")
public class  CsfbPieChartView extends View implements Runnable,OnGestureListener {

	public static final int TO_RIGHT = 0;
	public static final int TO_BOTTOM = 1;
	public static final int TO_LEFT = 2;
	public static final int TO_TOP = 3;
	public static final int NO_ROTATE = -1;
	

	private static final String[] DEFAULT_ITEMS_COLORS = { "#ffaa24", "#ffd224", "#ff4d24", "#ff7824", "#F02900", "#F13D73" ,"#ffe624"};
	private static final String DEAFULT_BORDER_COLOR = "#000000";
	private static final int DEFAULT_STROLE_WIDTH = 2;
	private static final int DEFAULT_RADIUS = 100;
	private static final int DEFAULT_SEPARATE_DISTENCE = 10;
	private static final int TIME_HANDLER_DELY = 10;
	private static final float MIN_ANIMSPEED = (float) 0.5;
	private static final float MAX_ANIMSPEED = (float) 5.0;
	private static final float DEFAULT_ANIM_SPEED = (float) 1.7;

	private float rotateSpeed = (float) 0.5; 

	private float total;
	private float[] itemSizesTemp;
	private float[] itemsSizes;
	private String[] itemsColors;
	private float[] itemsAngle;
	private float[] itemsBeginAngle;
	private float[] itemsRate;
	private float rotateAng = 0;
	private float lastAng = 0;
	private boolean bClockWise; 
	private boolean isRotating;
	private boolean isAnimEnabled = true;
	private String radiusBorderStrokeColor;
	
	private ScrollView scrollView;
	private float strokeWidth = 0;
	private float radius;
	private int itemPostion = -1;
	private int rotateWhere = 0;
	private float separateDistence = 10;

	private Handler rotateHandler = new Handler();
	private float centerXY;
	private static final String TAG = "ParBarView";
	
	
	private String failStr = "N/A" ;
	
	private String failScaleStr = "N/A";
	private DisplayMetrics metric;
	private Canvas mCanvas;
	private GestureDetector mGestureDetector;


	public  CsfbPieChartView(Context context, String[] itemColors, float[] itemSizes, float total, int radius, int strokeWidth, String strokeColor, int rotateWhere, float separateDistence, float rotateSpeed) {
		super(context);
		mGestureDetector = new GestureDetector(context, this);
		this.rotateWhere = rotateWhere;

		if (itemSizes != null && itemSizes.length > 0) {
			this.itemSizesTemp = itemSizes;
			this.total = total;
			reSetTotal();
			refreshItemsAngs();
		}

		if (radius < 0) {
			this.radius = DEFAULT_RADIUS;
		} else {
			this.radius = radius;
		}
		if (strokeWidth < 0) {
			strokeWidth = DEFAULT_STROLE_WIDTH;
		} else {
			this.strokeWidth = strokeWidth;
		}

		this.radiusBorderStrokeColor = strokeColor;

		if (itemColors == null) {
			setDefaultColor();
		} else if (itemColors.length < itemSizes.length) {
			this.itemsColors = itemColors;
			setLeftColor();
		} else {
			this.itemsColors = itemColors;
		}

		if (separateDistence < 0) {
			this.separateDistence = DEFAULT_SEPARATE_DISTENCE;
		} else {
			this.separateDistence = separateDistence;
		}
		if (rotateSpeed < MIN_ANIMSPEED) {
			rotateSpeed = MIN_ANIMSPEED;
		}
		if (rotateSpeed > MAX_ANIMSPEED) {
			rotateSpeed = MAX_ANIMSPEED;
		}
		this.rotateSpeed = rotateSpeed;
		invalidate();
	}
	

	public  String getFailStr() {
		return failStr;
	}

	public  void setFailStr(String failStr) {
		this.failStr = failStr;
	}

	public  String getFailScaleStr() {
		return failScaleStr;
	}

	public  void setFailScaleStr(String failScaleStr) {
		this.failScaleStr = failScaleStr;
	}
	
	
	public  ScrollView getScrollView() {
		return scrollView;
	}


	public  void setScrollView(ScrollView scrollView) {
		this.scrollView = scrollView;
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public  CsfbPieChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.radiusBorderStrokeColor = DEAFULT_BORDER_COLOR;
		mGestureDetector = new GestureDetector(context, this);
		invalidate();
	}


	public void setRaduis(int radius) {
		if (radius < 0) {
			this.radius = DEFAULT_RADIUS;
		} else {
			this.radius = radius;
		}
		invalidate();
	}


	
	public float getRaduis() {
		return this.radius;
	}


	
	public void setStrokeWidth(int strokeWidth) {
		if (strokeWidth < 0) {
			strokeWidth = DEFAULT_STROLE_WIDTH;
		} else {
			this.strokeWidth = strokeWidth;
		}
		invalidate();
	}

	

	public float getStrokeWidth() {
		return strokeWidth;
	}



	
	public void setStrokeColor(String strokeColor) {

		this.radiusBorderStrokeColor = strokeColor;

		invalidate();
	}



	
	public String getStrokeColor() {

		return this.radiusBorderStrokeColor;
	}



	
	public void setItemsColors(String[] colors) {
		if (itemsSizes != null && itemsSizes.length > 0) {
			if (colors == null) {
				setDefaultColor();
			} else if (colors.length < itemsSizes.length) {
				this.itemsColors = colors;
				setLeftColor();
			} else {
				this.itemsColors = colors;
			}
		}

		invalidate();
	}

	
	
	public String[] getItemsColors() {
		return this.itemsColors;
	}


	
	public void setItemsSizes(float[] items) {
		if (items != null && items.length > 0) {
			this.itemSizesTemp = items;
			reSetTotal();
			refreshItemsAngs();
			setItemsColors(itemsColors);
		}
		invalidate();
	}


	
	public float[] getItemsSizes() {
		return this.itemSizesTemp;
	}


	
	public void setTotal(int total , float[] itemSizesTemp ) {
		this.total = total;
		this.itemSizesTemp = itemSizesTemp;
		reSetTotal();
		invalidate();
	}

	
	
	public float getTotal() {
		return this.total;
	}

	
	
	public void setAnimEnabled(boolean isAnimEnabled) {
		this.isAnimEnabled = isAnimEnabled;
		invalidate();
	}



	
	public boolean isAnimEnabled() {
		return isAnimEnabled;
	}

	
	public void setRotateSpeed(float rotateSpeed) {
		if (rotateSpeed < MIN_ANIMSPEED) {
			rotateSpeed = MIN_ANIMSPEED;
		}
		if (rotateSpeed > MAX_ANIMSPEED) {
			rotateSpeed = MAX_ANIMSPEED;
		}
		this.rotateSpeed = rotateSpeed;
	}


	
	public float getRotateSpeed() {
		if (isAnimEnabled()) {
			return rotateSpeed;
		} else {
			return 0;
		}
	}


	
	
	
	public void setShowItem(int position, boolean anim, boolean listen) {
		if (itemsSizes != null && position < itemsSizes.length && position >= 0) {

			this.itemPostion = position;


			if (listen) {
				notifySelectedListeners(position, itemsColors[position], itemsSizes[position], itemsRate[position], isPositionFree(position), getAnimTime(Math.abs(lastAng - rotateAng)));// 锟斤拷锟斤拷选锟斤拷锟斤拷目锟斤拷锟斤拷息
			}

			if (this.rotateWhere == NO_ROTATE) {

			} else {
				lastAng = getLastRotateAngle(position);
				if (anim) {
					rotateAng = 0;
					if (lastAng > 0) {
						bClockWise = true;
					} else {
						bClockWise = false;
					}
					isRotating = true;
				} else {
					rotateAng = lastAng;
				}
				rotateHandler.postDelayed(this, 1);
			}

		}
	}


	
	public int getShowItem() {
		return itemPostion;
	}


	public void setRotateWhere(int rotateWhere) {
		this.rotateWhere = rotateWhere;
	}


	public int getRotateWhere() {
		return rotateWhere;
	}


	
	public void setSeparateDistence(float separateDistence) {
		if (separateDistence < 0) {
			separateDistence = DEFAULT_SEPARATE_DISTENCE;
		}
		this.separateDistence = separateDistence;
		invalidate();
	}


	public float getSeparateDistence() {
		return separateDistence;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		
		metric = this.getResources().getDisplayMetrics(); 			
		
		float systemScale =  metric.densityDpi / 240.f;
		float bigRadius = radius + strokeWidth;
		
		centerXY = separateDistence + bigRadius;
		Paint paint = new Paint();
		paint.setAntiAlias(true);

		if (strokeWidth != 0) {
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.parseColor(radiusBorderStrokeColor));
			paint.setStrokeWidth(strokeWidth);
			canvas.drawCircle(centerXY, centerXY, bigRadius, paint);
			
			
			
		}

		if (itemsAngle != null && itemsBeginAngle != null) {
			float rigthBottom = 2 * (radius + strokeWidth) + separateDistence;
			float leftTop = separateDistence;
			canvas.save();
			canvas.rotate(rotateAng, centerXY, centerXY);
			
			paint.setStrokeWidth(1);
			RectF oval = new RectF(leftTop, leftTop, rigthBottom, rigthBottom);
			for (int i = 0; i < itemsAngle.length; i++) {
				if (itemPostion == i && !isRotating) {
					// Log.e(TAG, "draw last  ");
					switch (rotateWhere) {
					case TO_RIGHT:
						oval = new RectF(leftTop, leftTop, rigthBottom + separateDistence, rigthBottom);
						break;
					case TO_TOP:
						oval = new RectF(leftTop, leftTop - separateDistence, rigthBottom, rigthBottom);
						break;
					case TO_BOTTOM:
						oval = new RectF(leftTop, leftTop, rigthBottom, rigthBottom + separateDistence);
						break;
					case TO_LEFT:
						oval = new RectF(leftTop - separateDistence, leftTop, rigthBottom, rigthBottom);
						break;
					default:
						break;
					}
				} else {
					oval = new RectF(leftTop, leftTop, rigthBottom, rigthBottom);
				}
				
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(Color.parseColor(itemsColors[i]));
				canvas.drawArc(oval, itemsBeginAngle[i], itemsAngle[i], true, paint);
			}
			
			canvas.restore();
			
			
			paint.setStyle(Paint.Style.FILL);
//			paint.setColor(this.getResources().getColor(R.color.base_list_item_bg_nomal));
			paint.setColor(this.getResources().getColor(R.color.app_grey_color));
			canvas.drawCircle(centerXY, centerXY, 101 * systemScale, paint);
			
			
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(this.getResources().getColor(R.color.csfb_piechart_color));
			paint.setStrokeWidth(15 * systemScale);
			canvas.drawCircle(centerXY, centerXY, 101 * systemScale, paint);
			
			TextPaint textPaint = new TextPaint();

//			textPaint.setARGB(0xFF, 255,250,240);
			textPaint.setARGB(0xFF, 33,33,33);

			textPaint.setTextSize(25 * systemScale);

			float lnTextWidth = (textPaint.measureText(getFailScaleStr()) > 200 * systemScale) ? 200 * systemScale : textPaint.measureText(getFailScaleStr());
			
			StaticLayout layout = new StaticLayout(getFailScaleStr(),textPaint,(int)lnTextWidth,Alignment.ALIGN_CENTER,1.0F,0.0F,true);
			
			canvas.translate(centerXY - lnTextWidth / 2,  centerXY - 25 * systemScale);
			layout.draw(canvas);
			
//			Paint fontPaint = new Paint();
//			fontPaint.setColor(Color.WHITE);
//			fontPaint.setAntiAlias(true);
//			fontPaint.setTextSize(25 * systemScale);
//			float failScaleStrSize =  fontPaint.measureText(getFailScaleStr());
//			canvas.drawText(getFailScaleStr(),  centerXY - failScaleStrSize / 2,  centerXY + 25 * systemScale / 2, fontPaint);
			
		}

		// canvas.restore();
		mCanvas = canvas;
	}

	/**
	 * View#onTouchEvent
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		if (!isRotating && itemsSizes != null && itemsSizes.length > 0) {
//			float x1 = 0;
//			float y1 = 0;
			switch (event.getAction()) {
//			case MotionEvent.ACTION_DOWN:
//				x1 = event.getX();
//				y1 = event.getY();
//				float r = radius + strokeWidth;
//				if ((x1 - r) * (x1 - r) + (y1 - r) * (y1 - r) - r * r <= 0 
//						&& (x1 - r) * (x1 - r) + (y1 - r) * (y1 - r) >= 150*150) {
//					int position = getShowItem(getTouchedPointAngle(r, r, x1, y1));
//					setShowItem(position, isAnimEnabled(), true);
//				} 
//				else if ((x1 - r) * (x1 - r) + (y1 - r) * (y1 - r) - r * r <= 0){
//					notifySelectedListeners();
//				}
//				break;
			case MotionEvent.ACTION_MOVE:
				if (!isRotating && itemsSizes != null && itemsSizes.length > 0) {
					float x1 = 0;
					float y1 = 0;
					x1 = event.getX();
					y1 = event.getY();
					float r = radius + strokeWidth;
					if ((x1 - r) * (x1 - r) + (y1 - r) * (y1 - r) - r * r <= 0 
							&& (x1 - r) * (x1 - r) + (y1 - r) * (y1 - r) >= 150*150) {
						int position = getShowItem(getTouchedPointAngle(r, r, x1, y1));
						setShowItem(position, isAnimEnabled(), true);
						scrollView.requestDisallowInterceptTouchEvent(true);
					}
					return false;
				}
//			case MotionEvent.ACTION_UP:
//
//				break;
//
//			default:
//				break;
			}
//		}
		mGestureDetector.onTouchEvent(event);
		return true;
	}
	
	
	

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		rotateHandler.removeCallbacks(this);
		// Log.e(TAG, "onDetachedFromWindow");
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// set the size of the view
		// setMeasuredDimension((int) (bitmap.getWidth() * scale), (int) (bitmap.getHeight() * scale));
		float widthHeight = 2 * (radius + strokeWidth + separateDistence);
		setMeasuredDimension((int) widthHeight, (int) widthHeight);
		Log.i("setMeasuredDimension", "widthHeight = " + widthHeight);
		Log.i("setMeasuredDimension", "radius = " + radius);
		Log.i("setMeasuredDimension", "separateDistence = " + separateDistence);
		Log.i("setMeasuredDimension", "strokeWidth = " + strokeWidth);
	}
	
	
	
	@Override
	public void run() {

		if (bClockWise) {
			rotateAng += rotateSpeed;
			invalidate();
			rotateHandler.postDelayed(this, TIME_HANDLER_DELY);
			if (rotateAng - lastAng >= 0) {
				rotateAng = 0;
				rotateHandler.removeCallbacks(this);
				resetBeginAngle(lastAng);
				// invalidate();
				isRotating = false;
			}
		} else {
			rotateAng -= rotateSpeed;
			invalidate();
			rotateHandler.postDelayed(this, TIME_HANDLER_DELY);
			if (rotateAng - lastAng <= 0) {
				rotateAng = 0;
				rotateHandler.removeCallbacks(this);
				resetBeginAngle(lastAng);
				// invalidate();
				isRotating = false;
			}
		}

	}

	private void refreshItemsAngs() {
		if (itemSizesTemp != null && itemSizesTemp.length > 0) {
			if (getTotal() > getAllSizes()) {
				itemsSizes = new float[itemSizesTemp.length + 1];
				for (int m = 0; m < itemSizesTemp.length; m++) {
					itemsSizes[m] = itemSizesTemp[m];
				}
				itemsSizes[itemsSizes.length - 1] = getTotal() - getAllSizes();
			} else {
				itemsSizes = new float[itemSizesTemp.length];
				itemsSizes = itemSizesTemp;
			}

			itemsRate = new float[itemsSizes.length];
			itemsBeginAngle = new float[itemsSizes.length];
			itemsAngle = new float[itemsSizes.length];
			float beginAngle = 0;

			for (int i = 0; i < itemsSizes.length; i++) {
				itemsRate[i] = (float) (itemsSizes[i] * 1.0 / getTotal() * 1.0);
			}

			for (int i = 0; i < itemsRate.length; i++) {
				if (i == 1) {
					beginAngle = 360 * itemsRate[i - 1];
				} else if (i > 1) {
					beginAngle = 360 * itemsRate[i - 1] + beginAngle;
				}
				itemsBeginAngle[i] = beginAngle;
				itemsAngle[i] = 360 * itemsRate[i];
				// Log.e(TAG, "itemsBeginAngle=" + beginAngle + "   itemsAngle" + 360 * bili[i]);
			}
		}

	}

	private boolean isPositionFree(int position) {
		if (position == itemsSizes.length - 1 && getTotal() > getAllSizes()) {
			return true;
		} else {
			return false;
		}
	}

	private float getAnimTime(float ang) {
		return (int) Math.floor((ang / getRotateSpeed()) * TIME_HANDLER_DELY);
	}

	private float getTouchedPointAngle(float x, float y, float x1, float y1) {

		float ax = x1 - x;
		float ay = y1 - y;

		ax = ax;
		ay = -ay;
		double a = 0;
		double t = ay / Math.sqrt((double) (ax * ax + ay * ay));
		// Log.e(TAG, "ax=" + ax + "   ay=" + ay);
		if (ax > 0) {
			if (ay > 0)
				a = Math.asin(t);
			else
				a = 2 * Math.PI + Math.asin(t);
		} else {
			if (ay > 0)
				a = Math.PI - Math.asin(t);
			else
				a = Math.PI - Math.asin(t);
		}
		return (float) (360 - (a * 180 / (Math.PI)) % (360));
	}

	private int getShowItem(float ang) {
		int position = 0;

		for (int i = 0; i < itemsBeginAngle.length; i++) {
			// Log.v(TAG, "*****itemsBeginAngle=" + itemsBeginAngle[i]);
			if (i != itemsBeginAngle.length - 1) {
				if (ang >= itemsBeginAngle[i] && ang < itemsBeginAngle[i + 1]) {
					position = i;
					break;
				}
			} else {
				if (ang > itemsBeginAngle[itemsBeginAngle.length - 1] && ang < itemsBeginAngle[0]) {
					position = itemsSizes.length - 1;
				} else if (isUpperSort(itemsBeginAngle)) {
					position = itemsSizes.length - 1;
				} else {
					position = getPointItem(itemsBeginAngle);
				}

			}
		}

		return position;
	}

	private float getLastRotateAngle(int position) {

		float result = 0;


		result = itemsBeginAngle[position];
		// Log.e(TAG, "maxAng=" + result);
		// Log.e(TAG, "ItemAng=" + itemsAngle[position]);
		result = itemsBeginAngle[position] + (itemsAngle[position]) / 2 + getRotateWhereAngle();
		if (result >= 360) {
			result -= 360;
		}
		// Log.v(TAG, "getLastRotateAngle=" + result);
		if (result <= 180) {
			result = -result;
		} else {
			result = 360 - result;
		}

		return result;
	}

	private boolean isUpperSort(float[] all) {
		boolean result = true;
		float temp = all[0];
		for (int a = 0; a < all.length - 1; a++) {
			if ((all[a + 1] - temp) > 0) {
				temp = all[a + 1];
			} else {
				return false;
			}
		}

		return result;
	}

	private int getPointItem(float[] all) {
		int item = 0;

		float temp = all[0];
		for (int a = 0; a < all.length - 1; a++) {
			if ((all[a + 1] - temp) > 0) {
				temp = all[a];
			} else {
				return a;
			}
		}

		return item;
	}

	private void resetBeginAngle(float angle) {
		for (int i = 0; i < itemsBeginAngle.length; i++) {
			float newBeginAngle = itemsBeginAngle[i] + angle;

			if (newBeginAngle < 0) {
				itemsBeginAngle[i] = newBeginAngle + 360;
			} else if (newBeginAngle > 360) {
				itemsBeginAngle[i] = newBeginAngle - 360;
			} else {
				itemsBeginAngle[i] = newBeginAngle;
			}

			// Log.v(TAG, "itemsBeginAngle  " + i + "=" + itemsBeginAngle[i]);
		}
	}

	private void setDefaultColor() {

		if (itemsSizes != null && itemsSizes.length > 0 && itemsColors == null) {
			// Log.e(TAG, "setDefaultColor");
			itemsColors = new String[itemsSizes.length];
			if (itemsColors.length <= DEFAULT_ITEMS_COLORS.length) {
				System.arraycopy(DEFAULT_ITEMS_COLORS, 0, itemsColors, 0, itemsColors.length);
			} else {
				int multiple = itemsColors.length / DEFAULT_ITEMS_COLORS.length;
				int left = itemsColors.length % DEFAULT_ITEMS_COLORS.length;

				for (int a = 0; a < multiple; a++) {
					System.arraycopy(DEFAULT_ITEMS_COLORS, 0, itemsColors, a * DEFAULT_ITEMS_COLORS.length, DEFAULT_ITEMS_COLORS.length);
				}
				if (left > 0) {
					System.arraycopy(DEFAULT_ITEMS_COLORS, 0, itemsColors, multiple * DEFAULT_ITEMS_COLORS.length, left);
				}
			}
			// Log.e(TAG, "itemsColors = " + itemsColors.length);
			// for (String a : itemsColors) {
			// Log.v(TAG, "itemsColors:" + a);
			// }
		}

	}

	private void setLeftColor() {

		if (itemsSizes != null && itemsSizes.length > itemsColors.length) {
			String[] preItemsColors = new String[itemsColors.length];
			preItemsColors = itemsColors;
			int leftall = itemsSizes.length - itemsColors.length;
			itemsColors = new String[itemsSizes.length];
			System.arraycopy(preItemsColors, 0, itemsColors, 0, preItemsColors.length);

			if (leftall <= DEFAULT_ITEMS_COLORS.length) {
				System.arraycopy(DEFAULT_ITEMS_COLORS, 0, itemsColors, preItemsColors.length, leftall);
			} else {
				int multiple = leftall / DEFAULT_ITEMS_COLORS.length;
				int left = leftall % DEFAULT_ITEMS_COLORS.length;
				for (int a = 0; a < multiple; a++) {
					System.arraycopy(DEFAULT_ITEMS_COLORS, 0, itemsColors, a * DEFAULT_ITEMS_COLORS.length, DEFAULT_ITEMS_COLORS.length);
				}
				if (left > 0) {
					System.arraycopy(DEFAULT_ITEMS_COLORS, 0, itemsColors, multiple * DEFAULT_ITEMS_COLORS.length, left);
				}
			}
			preItemsColors = null;

		}
		// for (String a : itemsColors) {
		// Log.v(TAG, "itemsColors:" + a);
		// }

	}

	private void reSetTotal() {
		float totalSizes = getAllSizes();
		if (getTotal() < totalSizes) {
			this.total = totalSizes;
		}
	}

	private float getAllSizes() {
		float tempAll = 0;
		if (itemSizesTemp != null && itemSizesTemp.length > 0) {
			for (float itemsize : itemSizesTemp) {
				tempAll += itemsize;
			}
		}

		return tempAll;
	}

	private float getRotateWhereAngle() {

		float result = 0;
		switch (rotateWhere) {
		case TO_RIGHT:
			result = 0;
			break;
		case TO_LEFT:
			result = 180;
			break;
		case TO_TOP:
			result = 90;
			break;
		case TO_BOTTOM:
			result = 270;
			break;

		default:
			break;
		}
		return result;
	}

	private List<OnPieChartItemSelectedLinstener> itemSelectedListeners = new LinkedList<OnPieChartItemSelectedLinstener>();

	public void setOnItemSelectedListener(OnPieChartItemSelectedLinstener listener) {
		itemSelectedListeners.add(listener);
	}

	public void removeItemSelectedListener(OnPieChartItemSelectedLinstener listener) {
		itemSelectedListeners.remove(listener);
	}

	/**
	 * @param position
	 */
	protected void notifySelectedListeners(int position, String colorRgb, float size, float rate, boolean isFreePart, float animTime) {
		for (OnPieChartItemSelectedLinstener listener : itemSelectedListeners) {
			listener.onPieChartItemSelected(this, position, colorRgb, size, rate, isFreePart, animTime);
		}
	}
	
	protected void notifySelectedListeners() {
		for (OnPieChartItemSelectedLinstener listener : itemSelectedListeners) {
			listener.onCircleOnClick();
		}
	}


	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		if (!isRotating && itemsSizes != null && itemsSizes.length > 0) {
			float x1 = 0;
			float y1 = 0;
			x1 = event.getX();
			y1 = event.getY();
			float r = radius + strokeWidth;
			if ((x1 - r) * (x1 - r) + (y1 - r) * (y1 - r) - r * r <= 0 
					&& (x1 - r) * (x1 - r) + (y1 - r) * (y1 - r) >= 150*150) {
				int position = getShowItem(getTouchedPointAngle(r, r, x1, y1));
				setShowItem(position, isAnimEnabled(), true);
			} 
			else if ((x1 - r) * (x1 - r) + (y1 - r) * (y1 - r) - r * r <= 0){
				notifySelectedListeners();
			}
		}
		return false;
	}


	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		
		return false;
	}


	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		return false;
	}

}
