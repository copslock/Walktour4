package com.walktour.gui.map.celllist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.framework.view.BasicParamView;
import com.walktour.framework.view.CheckCellParamThread;
import com.walktour.gui.R;
import com.walktour.gui.map.TraceInfoData;
import com.walktour.model.CellInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 小区列表显示基础类
 * 
 * @author jianchao.wang
 *
 */
public abstract class BaseCellListView extends BasicParamView {

	/** 网络名称 */
	private String paramName;
	/** 行高度 */
	protected float rowsHeight;
	/** 指定行上升位数,为行高-字体高度 再除2 */
	protected float rowUpBit;
	/** 网络类型 */
	private int networkType;
	/** 小区参数列表 */
	protected List<CellParams> cellList = new ArrayList<CellParams>();
	/** 参数柱状图的最大值 */
	protected int maxHistogramValue = 0;
	/** 参数柱状图的最小值 */
	protected int minHistogramValue = 0;

	public BaseCellListView(Context context, String paramName, int networkType, int maxHistogramValue,
			int minHistogramValue) {
		super(context);
		this.paramName = paramName;
		this.tableRows = 21;
		this.networkType = networkType;
		this.maxHistogramValue = maxHistogramValue;
		this.minHistogramValue = minHistogramValue;
	}

	public BaseCellListView(Context context, AttributeSet attrs, String paramName, int networkType,
			int maxHistogramValue, int minHistogramValue) {
		super(context, attrs);
		this.paramName = paramName;
		this.tableRows = 21;
		this.networkType = networkType;
		this.maxHistogramValue = maxHistogramValue;
		this.minHistogramValue = minHistogramValue;
	}

	@Override
	public void initView(Canvas canvas) {
//		this.rowsHeight = (super.getHeight() - 1) / super.tableRows;
		rowsHeight=60;//保持表格的一致性
		this.rowUpBit = (this.rowsHeight - super.textSize) / 2;
		setPadding(20,0,0,0);
//		setBackgroundResource(R.drawable.layout_white_circle);
		setBackgroundColor(Color.TRANSPARENT);
		this.createTableTitle(canvas);
		this.createTableHeader(canvas);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec);
//		width=width- DensityUtil.px2dip(getContext(),20);
//		height=height- DensityUtil.px2dip(getContext(),20);
//		setMeasuredDimension(width,height);
	}
	private int measureWidth(int measureSpec) {
		int result = 200;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		switch (specMode) {
			case MeasureSpec.UNSPECIFIED:
				result = specSize;
				break;
			case MeasureSpec.AT_MOST:
				result = Math.min(result, specSize);
				break;
			case MeasureSpec.EXACTLY:
				result = specSize;
				break;
		}
		return result;
	}

	private int measureHeight(int measureSpec) {
		int result = 200;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		switch (specMode) {
			case MeasureSpec.UNSPECIFIED:
				result = specSize;
				break;
			case MeasureSpec.AT_MOST:
				result = Math.min(result, specSize);
				break;
			case MeasureSpec.EXACTLY:
				result = specSize;
				break;
		}
		return result;
	}


	/**
	 * 创建表格头
	 * 
	 * @param canvas
	 *          画板
	 * @return
	 */
	protected void createTableHeader(Canvas canvas) {
		// 四周边框
//		canvas.drawLine(1, 1, this.getWidth() - 1, 1, linePaint);
//		canvas.drawLine(1, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, linePaint);
//		canvas.drawLine(1, 1, 1, this.getHeight() - 1, linePaint);
//		canvas.drawLine(this.getWidth() - 1, 1, this.getWidth() - 1, this.getHeight() - 1, linePaint);
		linePaint.setColor(Color.parseColor("#0074CC"));
		float colsTextWidthTotal = this.getColsTextWidthTotal();
		float colsTextWidth = colsTextWidthTotal / (this.getHeaders().length - 1);
		String[] headers = this.getHeaders();
		for (int i = 0; i < headers.length - 1; i++) {
			float textSize = fontPaint.measureText(headers[i]);
			canvas.drawText(headers[i], colsTextWidth * i
					+ ((colsTextWidth - textSize) / 2 <= 0 ? 0 : (colsTextWidth - textSize) / 2), rowsHeight * 3 - rowUpBit * 3,
					fontPaint);
		}
		// 绘制最后一列标题
		canvas.drawText(headers[headers.length - 1], colsTextWidthTotal, rowsHeight * 3 - rowUpBit * 3, fontPaint);

		// 画横线
		canvas.drawLine(0, (rowsHeight * 3 - rowUpBit * 2) + 5, getWidth(), (rowsHeight * 3 - rowUpBit * 2) + 5, linePaint);
		// 画中间线
		canvas.drawLine(colsTextWidthTotal, (rowsHeight * 3 - rowUpBit * 2) + 5, colsTextWidthTotal, getHeight() - 1,
				linePaint);
		canvas.save();
		canvas.restore();
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		createTableContent(canvas, TraceInfoInterface.traceData);
	}

	/**
	 * 获取左边列表宽度
	 * 
	 * @return
	 */
	protected float getColsTextWidthTotal() {
		return this.getWidth() / 2;
	}

	/**
	 * 获得字符串的整型值
	 * 
	 * @param value
	 *          字符串
	 * @return
	 */
	protected String getInt(String value) {
		try {
			if (UtilsMethod.isNumeric(value))
				if (!value.equals("")) {
					return String.valueOf(Double.valueOf(value).intValue());
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 生成表格内容
	 * 
	 * @param canvas
	 *          画图
	 * @param traceData
	 *          数据
	 */
	protected void createTableContent(Canvas canvas, TraceInfoData traceData) {
		this.parseCellValues(traceData);
		float top;
		float left;
		float right;
		float bottom;
		float x;
		float y;
		float colsTextWidthTotal = this.getColsTextWidthTotal();
		float colsTextWidth = colsTextWidthTotal / (this.getHeaders().length - 1);
		for (int row = 0; row < this.cellList.size(); row++) {
			CellParams cellParams = this.cellList.get(row);
			y = this.rowsHeight * (4 + row * 2) - rowUpBit;
			// 绘制除最后一列外的表格数据
			for (int col = 0; col < cellParams.tableValues.length; col++) {
				x = colsTextWidth * col + (colsTextWidth - paramPaint.measureText(cellParams.tableValues[col])) / 2;
				canvas.drawText(cellParams.tableValues[col], x, y, paramPaint);
			}
			// 绘制最后一列的图形
			if (cellParams.histogramValue != 0) {
				// 绘制背景灰条
				paramPaint.setColor(Color.TRANSPARENT);
				left = colsTextWidthTotal;
				top = rowsHeight * (3 + row * 2) + 5;
				bottom = rowsHeight * (4 + row * 2) - 3;
				right = this.getWidth() - super.systemScale * 10;
				canvas.drawRect(left, top, right, bottom, paramPaint);
				// 绘制数值显示条
				paramPaint.setColor(getResources().getColor(this.getColor(cellParams)));
				right = colsTextWidthTotal + this.getHistogramWidth(cellParams.histogramValue);
				canvas.drawRect(left, top, right, bottom, paramPaint);
				// 绘制数值说明文字
				paramPaint.setColor(getResources().getColor(R.color.info_param_color));
				String value = cellParams.histogramValue + cellParams.histogramUnit;
				x = colsTextWidthTotal + (this.getWidth() - colsTextWidthTotal - fontPaint.measureText(value)) / 2;
				canvas.drawText(value, x, y, fontPaint);
			}
			// 绘制小区名称
			CellInfo cell = this.getCellInfo(cellParams, traceData);
			if (cell != null) {
				y = rowsHeight * (4 + row * 2) + textSize;
				if (!StringUtil.isNullOrEmpty(cell.getCellId())) {
					String value = "CI:" + cell.getCellId();
					x = colsTextWidthTotal - 10 - fontPaint.measureText(value);
					canvas.drawText(value, x, y, fontPaint);
				}
				if (!StringUtil.isNullOrEmpty(cell.getCellName())) {
					x = colsTextWidthTotal + 10;
					canvas.drawText(cell.getCellName(), x, y, fontPaint);
				}
			}
		}
		canvas.save();
		canvas.restore();
	}

	/**
	 * 参数柱状图的显示宽度
	 * 
	 * @param histogramValue
	 *          参数值
	 * @return
	 */
	protected float getHistogramWidth(double histogramValue) {
		float columscale = (this.getWidth() - this.getColsTextWidthTotal() - super.systemScale * 10)
				/ (this.maxHistogramValue - this.minHistogramValue);
		return (float) ((histogramValue - this.minHistogramValue) * columscale);
	}

	/**
	 * 获得数值柱状图的颜色
	 * 
	 * @param param
	 *          参数数据
	 * @return
	 */
	protected abstract int getColor(CellParams param);

	/**
	 * 转换字符串为数值
	 * 
	 * @param str
	 *          字符串
	 * @return
	 */
	protected double getDouble(String str) {
		try {
			return Double.parseDouble(str);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 获得小区信息
	 * 
	 * @param cellParams
	 *          小区参数值
	 * @param traceData
	 *          数据
	 * @return
	 */
	protected CellInfo getCellInfo(CellParams cellParams, TraceInfoData traceData) {
		StringBuilder cellidKey = new StringBuilder();
		cellidKey.append(this.networkType);
		for (int i = 0; i < this.getCellParamIndexs().length; i++) {
			int index = this.getCellParamIndexs()[i];
			if (StringUtil.isNullOrEmpty(cellParams.tableValues[index])) {
				return null;
			}
			cellidKey.append("_").append(this.getCellParamNames()[i]).append("_").append(cellParams.tableValues[index]);
		}
		if (traceData.containsCellIDHmKey(cellidKey.toString())) {
			return traceData.getNetworkCellInfo(cellidKey.toString());
		} else {
			StringBuilder sb = new StringBuilder();
			int length = this.getCellParamNames().length;
			String[] queryParams = new String[length + 4];
			for (int i = 0; i < length; i++) {
				if (i > 0)
					sb.append(" and ");
				sb.append(this.getCellParamNames()[i]).append(" = '");
				sb.append(cellParams.tableValues[this.getCellParamIndexs()[i]]).append("' ");
				queryParams[i] = this.getCellParamNames()[i];
			}
			queryParams[length] = "cellName";
			queryParams[length + 1] = "cellId";
			queryParams[length + 2] = "longitude";
			queryParams[length + 3] = "latitude";
			new CheckCellParamThread(this.getContext(), queryParams, sb.toString(), this.networkType).start();
			return null;
		}
	}

	/**
	 * 获得判断关联小区的参数位置
	 * 
	 * @return
	 */
	protected abstract int[] getCellParamIndexs();

	/**
	 * 获得判断关联小区的参数名称
	 * 
	 * @return
	 */
	protected abstract String[] getCellParamNames();

	/**
	 * 解析小区数据
	 * 
	 * @param traceData
	 *          数据对象
	 * @return
	 */
	protected abstract void parseCellValues(TraceInfoData traceData);

	/**
	 * 绘制表格标题
	 */

	private void createTableTitle(Canvas canvas) {
		Paint titlePaint = new Paint();
		titlePaint.setAntiAlias(true);
		titlePaint.setStyle(Paint.Style.FILL);
		titlePaint.setColor(getResources().getColor(R.color.csfb_delay_color));
		titlePaint.setTypeface(null);
		rowsHeight=61;
		titlePaint.setTextSize(rowsHeight);
		String paraname = this.paramName + " Cell List";
		canvas.drawText(paraname, (this.getWidth() - titlePaint.measureText(paraname)) / 2, rowsHeight + rowsHeight / 4,
				titlePaint);
//		titlePaint.setTextSize(rowsHeight);

	}

	/**
	 * 获得参数队列中指定ID的值
	 * 
	 * @param paraId
	 *          指定ID
	 * @return
	 */
	protected String getParaValue(int paraId) {
		return TraceInfoInterface.getParaValue(paraId);
	}

	/**
	 * 获取标题项数组
	 * 
	 * @return
	 */
	protected abstract String[] getHeaders();

	/**
	 * 小区参数对象
	 * 
	 * @author jianchao.wang
	 *
	 */
	protected class CellParams {
		/** 左边表格数据 */
		protected String[] tableValues;
		/** 右边柱状图数据 */
		protected double histogramValue;
		/** 右边柱状图数据单位 */
		protected String histogramUnit = "dBm";
		/** 类型 */
		protected String type;
	}

}
