package com.walktour.gui.task.activity.scanner.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.View;

import com.walktour.Utils.UtilsMethod;
import com.walktour.gui.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 扫频仪横向柱状图
 * zhihui.lian
 * */
public class HorizontalBarChart extends View{

	public static final int TYPE_TITLE = 1;//画头部
	public static final int TYPE_CHART = 2;//画图表
	private int width;
	private int height;
	private float density;
	private float layoutMarginLeft;
	private float layoutMarginRight;
	private float layoutMarginBottom;
	private float layoutMarginTop;
	private float topAreaHeight;
	private float valueMin;
	private float valueMax;
	private Paint paint = new Paint();
	private int canvasColor;
	private float itemHeight;
	private float itemValueTextSize;
	private int itemValueTextNameColor;
	private Format valueFormat;
	private int defaultBarColor;
	private float defaultBarWidth;
	private float axisWidth;
	private int drawType = 2;//1、标题栏 2、图表栏
	private String chartTitle = "";
	private String[] showColumnNameLineOne = null;
	private String[] showColumnNameLineTwo = null;
	private List<HashMap<String, Object>> itemValueLineOne = new ArrayList<HashMap<String, Object>>();
	private List<HashMap<String, Object>> itemValueLineTwo = new ArrayList<HashMap<String, Object>>();
	private final String[] tmp = new String[]{"UARFCN", "CPI", "RSCP", "RSRQ","RSRQ"};//用于计算每列数据宽度
	private  final float[] column = new float[5];
	
	public HorizontalBarChart(Context context) {
		super(context);
		init();
	}
	
	public HorizontalBarChart(Context context, int type) {
		super(context);
		drawType = type;
		init();
	}
	
	public HorizontalBarChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public HorizontalBarChart(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	private void init() {
		width = getWidth();
		height = getHeight();
		density = getResources().getDisplayMetrics().density;
		layoutMarginLeft = 10 * density;
		layoutMarginTop = 0 * density;
		layoutMarginRight = 10 * density;
		layoutMarginBottom = 10 * density;
		canvasColor = getResources().getColor(R.color.app_main_bg_color);
		itemHeight = 50 * density;
		itemValueTextSize = 14 * density;
		itemValueTextNameColor = Color.parseColor("#0DAEF4");
		defaultBarColor = Color.parseColor("#0DAEF4");
		valueFormat = new DecimalFormat("#.#");
		defaultBarWidth = 15 * density;
		initValueRange();
		getAxisWidth();
		initColumn();
	}
	
	private void initValueRange() {
		//这里是处理没有传入valueMin、valueMax，或valueMin大于传入数据最小值，或valueMax小于传入数据最大值的情况
        //1、当没有传入valueMin、valueMax时，valueMin默认为传入数据最小值，valueMax默认为传入数据最大值
        //2、当通过上述计算得到的valueMin等于valueMax时，默认valueMin = valueMax - 1
        if (Float.isNaN(valueMin) || Float.isNaN(valueMax)) {
            if (!Float.isNaN(valueMin)) {
                valueMax = valueMin + 1;
            } else if (!Float.isNaN(valueMax)) {
                valueMin = valueMax - 1;
            } else {
                valueMin = 0;
                valueMax = 1;
            }
        } else if (Float.compare(valueMin, valueMax) == 0) {
            valueMin = valueMax - 1;
        }
        
	}
	
	public void setValueRange(float minValue, float maxValue) {
		this.valueMin = minValue;
		this.valueMax = maxValue;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		switch (drawType) {
		case TYPE_TITLE:
			drawTitle(canvas);
			break;
		case TYPE_CHART:
			drawChart(canvas);
			break;
		default:
			break;
		}
	}
	
	private void drawTitle(Canvas canvas) {
		canvas.drawColor(canvasColor);
		paint.setColor(getResources().getColor(R.color.app_main_text_color));
		paint.setTextSize(20*density);
		canvas.drawText(getChartTitle(), getWidth()/2 - getTextWidth(getChartTitle())/2, getFontHeight(20*density) + 3 * density, paint);
		paint.reset();
		//表头
		drawTopArea(canvas);
	}
	
	private void drawTopArea(Canvas canvas) {
		//TODO 画表头
		paint.setColor(itemValueTextNameColor);
		paint.setTextSize(itemValueTextSize);
	
		float yPoint = layoutMarginTop + getFontHeight(20*density) + getFontHeight(itemValueTextSize) + 15 * density;
		float yPoint2 = 0;
		float yPointLine = 0;
		if (showColumnNameLineTwo != null && showColumnNameLineTwo.length > 0) {
			yPoint2 = layoutMarginTop + getFontHeight(20*density) + getFontHeight(itemValueTextSize) * 2 + 15 * density*2;
		} else {
			yPoint = layoutMarginTop + getFontHeight(20*density) + getFontHeight(itemValueTextSize) * 2 + 15 * density*2;
		}
		yPointLine = 75 * density;
		//画第一行数据
		if (showColumnNameLineOne != null) {
			paint.setColor(itemValueTextNameColor);
			for (int i = 0; i < showColumnNameLineOne.length; i++) {
				canvas.drawText(
						showColumnNameLineOne[i],
						calculateCulomnCenterPointX(i) - getTextWidth(showColumnNameLineOne[i])/2, yPoint,paint);
			}
			
		}
		//画第二行数据
		if (showColumnNameLineTwo != null) {
			paint.setColor(Color.parseColor("#06C0F6"));
			for (int i = 0; i < showColumnNameLineTwo.length; i++) {
				canvas.drawText(showColumnNameLineTwo[i], calculateCulomnCenterPointX(i) - getTextWidth(showColumnNameLineTwo[i])/2, yPoint2,paint);
			}
		}
		//画屏幕分割线
		paint.setColor(Color.parseColor("#333333"));
		canvas.drawLine(0, yPointLine, getWidth(), yPointLine, paint);
		paint.reset();
	}
	
	private void drawChart(Canvas canvas) {
		initValueRange();
		canvas.drawColor(canvasColor);
		//计算右边区域宽度
		axisWidth = getAxisWidth();
		paint.setColor(getResources().getColor(R.color.app_main_text_color));
		canvas.drawLine(getStartXPoint(4), 0, getStartXPoint(4), calculateViewHeight(), paint);
		//画表
		//画左边区域
		//第一行数据
		paint.setTextSize(itemValueTextSize);
		paint.setColor(itemValueTextNameColor);
		for (int i = 0; i < itemValueLineOne.size(); i++) {
			float yPoint = topAreaHeight + layoutMarginTop + 18*density + i * itemHeight;
			HashMap<String, Object> item = itemValueLineOne.get(i);
			for (int j = 0; j < showColumnNameLineOne.length - 1; j++) {
				String key = showColumnNameLineOne[j];
				if (!key.equals("") && !item.get(key).equals("")) {
					String value = valueFormat.format(item.get(key));
					canvas.drawText(value, calculateCulomnCenterPointX(j) - getTextWidth(value)/2, yPoint, paint);
				}
			}
		}
		paint.reset();
		//第二行数据
		paint.setTextSize(itemValueTextSize);
		paint.setColor(Color.parseColor("#06C0F6"));
		for (int i = 0; i < itemValueLineTwo.size(); i++) {
			float yPoint = topAreaHeight + layoutMarginTop + 18*density + i * itemHeight + getFontHeight(itemValueTextSize) + 10*density;
			HashMap<String, Object> item = itemValueLineTwo.get(i);
			for (int j = 0; j < showColumnNameLineTwo.length; j++) {
				String key = showColumnNameLineTwo[j];
				if (!key.equals("") && !item.get(key).equals("")) {
					String value = valueFormat.format(item.get(key));
					canvas.drawText(value, calculateCulomnCenterPointX(j) - getTextWidth(value)/2, yPoint, paint);
					
				}
			}
		}
		paint.reset();
		//画右边区域柱状图
		for (int i = 0; i < itemValueLineOne.size(); i++) {
			float yPoint = topAreaHeight + layoutMarginTop + 18*density + i * itemHeight;
			HashMap<String, Object> item = itemValueLineOne.get(i);
			float center = yPoint - getFontHeight(itemValueTextSize)/2;
			paint.setColor(Color.WHITE);
			canvas.drawRect(getStartXPoint(4) + 1 * density, center - defaultBarWidth/2 , getStartXPoint(4) + 1 * density + axisWidth * 1, center + defaultBarWidth/2, paint);
			int color = Integer.parseInt(item.get("color").toString());
			if (color == 0)
				paint.setColor(defaultBarColor);
			else 
				paint.setColor(color);
			Object obj = item.get(showColumnNameLineOne[showColumnNameLineOne.length - 1]);
			canvas.drawRect(getStartXPoint(4) + 1 * density, center - defaultBarWidth/2 , getStartXPoint(4) + 1 * density + axisWidth * (Float.parseFloat(obj.toString()) - valueMin) / (valueMax - valueMin), center + defaultBarWidth/2, paint);
			String valueTmp = valueFormat.format(obj) + "dB";
			paint.setColor(getResources().getColor(R.color.app_main_text_color));
			paint.setTextSize(itemValueTextSize);
			canvas.drawText(valueTmp, getStartXPoint(4) + axisWidth/2 - getTextWidth(valueTmp)/2, yPoint, paint);
		}
		
	}
	
	/**
	 * 处理字符串转换相关
	 * @param value
	 * @return
	 */
	
	private static String dealValueStr(String value) {
		String valueStr = "";
		try {
			if (UtilsMethod.isNumeric(value)) {
				BigDecimal bg = new BigDecimal(value);
		        double j = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		        valueStr = UtilsMethod.subZeroAndDot(String.valueOf(j));
				float valuea = Float.parseFloat(valueStr);
				if (valuea == -9999)
					valueStr = "";
			} else {
				valueStr = value;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return valueStr;
	}
	
	
	
	//计算柱状图区域宽度
	private float getAxisWidth() {
		axisWidth = getWidth() - getStartXPoint(4) - 1 * density - layoutMarginRight;
		return axisWidth;
	}
	//获取每列x起点坐标
	private float getStartXPoint(int columnNum) {
		return column[columnNum];
	}
	
	private void initColumn () {
		for (int i = 0; i < column.length; i++) {
			column[i] = calculateXPoint(i);
		}
	}
	//计算每列数据的x起点坐标
	private float calculateXPoint (int column) {
		Paint paint = new Paint();
		paint.setTextSize(itemValueTextSize);
		float xPoint = layoutMarginLeft;
		if (column > tmp.length)
			return 0;
		for (int i = 0; i < column; i++) {
			xPoint = xPoint + paint.measureText(tmp[i]) + 10 * density;
		}
		return xPoint;
	}
	
	private float calculateCulomnCenterPointX(int column) {
		if(column == -1){
			return 0;
		}
		Paint paint = new Paint();
		paint.setTextSize(itemValueTextSize);
		float xPoint = layoutMarginLeft;
		if (column > tmp.length)
			return 0;
		for (int i = 0; i < column; i++) {
			xPoint = xPoint + paint.measureText(tmp[i]) + 10 * density;
		}
		xPoint = xPoint + getTextWidth(tmp[column])/2;
		return xPoint;
	}
	
	/**
	 * 计算文字高度
	 * @param fontSize 以多大的字体计算
	 * @return
	 */
	private float getFontHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);  
	    FontMetrics fm = paint.getFontMetrics(); 
	    return (float)Math.ceil(fm.descent - fm.ascent) - 6 * density;  
	}
	
	/**
	 * 计算字符串宽度
	 * @param text 要计算的字符串
	 * @return
	 */
	private float getTextWidth(String text) {
		return paint.measureText(text);
	}
	
	/**
	 * 计算View高度
	 * @return
	 */
	private float calculateViewHeight() {
		int result = (int) (itemValueLineOne.size() * itemHeight);
		int height = getResources().getDisplayMetrics().heightPixels;
		int[] screenLocation = new int[2];
		this.getLocationOnScreen(screenLocation);
		if (result < height - screenLocation[1])
			result = height - screenLocation[1];
		return result;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		width = getWidth();
		height = getHeight();
		itemHeight = 50 * density;
		getAxisWidth();
		if (drawType == TYPE_TITLE) {
			topAreaHeight = 70 * density;
		} else {
			topAreaHeight = 0;
		}
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}
	
	private int measureWidth(int measureSpec) {  
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = (int) paint.measureText("") + getPaddingLeft() + getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
    }  
	
	private int measureHeight(int measureSpec) {  
		int result = 0;
		if (drawType == TYPE_TITLE) {
			// We were told how big to be
			result = (int) (77 * density);
		} else {
			int specMode = MeasureSpec.getMode(measureSpec);
			int specSize = MeasureSpec.getSize(measureSpec);
			if (specMode == MeasureSpec.EXACTLY) {
				result = specSize;
			} else {
				result = (int) (itemValueLineOne.size() * itemHeight + topAreaHeight);
				int height = getResources().getDisplayMetrics().heightPixels;
				int[] screenLocation = new int[2];
				this.getLocationOnScreen(screenLocation);
				if (result < height - screenLocation[1])
					result = height - screenLocation[1];
			}
		}
		return result;
    }  
	
	public float getLayoutMarginLeft() {
		return layoutMarginLeft;
	}

	public float getItemHeight() {
		return itemHeight;
	}

	public void setItemHeight(float itemHeight) {
		this.itemHeight = itemHeight;
	}

	public void setLayoutMarginLeft(float layoutMarginLeft) {
		this.layoutMarginLeft = layoutMarginLeft;
	}

	public float getLayoutMarginRight() {
		return layoutMarginRight;
	}

	public void setLayoutMarginRight(float layoutMarginRight) {
		this.layoutMarginRight = layoutMarginRight;
	}

	public float getLayoutMarginBottom() {
		return layoutMarginBottom;
	}

	public void setLayoutMarginBottom(float layoutMarginBottom) {
		this.layoutMarginBottom = layoutMarginBottom;
	}

	public float getLayoutMarginTop() {
		return layoutMarginTop;
	}

	public void setLayoutMarginTop(float layoutMarginTop) {
		this.layoutMarginTop = layoutMarginTop;
	}
	
	public String getChartTitle() {
		return chartTitle;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}
	
	public String[] getShowColumnNameLineOne() {
		return showColumnNameLineOne;
	}

	public void setShowColumnNameLineOne(String[] showColumnNameLineOne) {
		this.showColumnNameLineOne = showColumnNameLineOne;
	}

	public String[] getShowColumnNameLineTwo() {
		return showColumnNameLineTwo;
	}

	public void setShowColumnNameLineTwo(String[] showColumnNameLineTwo) {
		this.showColumnNameLineTwo = showColumnNameLineTwo;
	}

	public List<HashMap<String, Object>> getItemValueLineOne() {
		return itemValueLineOne;
	}

	public void setItemValueLineOne(List<HashMap<String, Object>> itemValueLineOne) {
		this.invalidate();
		this.itemValueLineOne = itemValueLineOne;
	}

	public List<HashMap<String, Object>> getItemValueLineTwo() {
		return itemValueLineTwo;
	}

	public void setItemValueLineTwo(List<HashMap<String, Object>> itemValueLineTwo) {
		this.itemValueLineTwo = itemValueLineTwo;
	}
	
}
