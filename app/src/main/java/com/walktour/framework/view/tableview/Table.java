package com.walktour.framework.view.tableview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.walktour.Utils.StringUtil;

/**
 * 表格对象
 * 
 * @author jianchao.wang
 *
 */
public class Table {
	/** 表格宽度 */
	private int mTableWidth;
	/** 行高 */
	private int mRowHeight;
	/** 列宽 */
	private int mColWidth;
	/** 总列数 */
	private int mTableCols;
	/** 总行数 */
	private int mTableRows;
	/** 表格左上角X坐标 */
	private int mStartX = 0;
	/** 表格左上角Y坐标 */
	private int mStartY = 0;
	/** 单元格列表 */
	private TableCell[][] mCells;
	/** 表格线颜色 */
	private int mLineColor;
	/** 属性名画笔 */
	private Paint mTextPaint;
	/** 单元格线画笔 */
	private Paint mLinePaint;
	/** 字体大小 */
	private float mTextSize;

	public Table(int tableRows, int tableCols, int tableWidth, int rowHeight, float textSize, int lineColor) {
		this.mTableRows = tableRows;
		this.mTableCols = tableCols;
		this.mTableWidth = tableWidth;
		this.mRowHeight = rowHeight;
		this.mTextSize = textSize;
		this.mLineColor = lineColor;
		this.initPaint();
		this.initCells();
	}

	/**
	 * 清除所有的单元格数据
	 */
	public void clearAllCellValues() {
		for (int row = 0; row < this.mTableRows; row++) {
			for (int col = 0; col < this.mTableCols; col++) {
				if (this.mCells[row][col] == null)
					continue;
				this.mCells[row][col].mCellValue = "";
				this.mCells[row][col].mAlignType = 0;
				this.mCells[row][col].hasRightSide = true;
				this.mCells[row][col].mTextSize = this.mTextSize;
			}
		}
	}

	/**
	 * 初始化表格
	 */
	private void initCells() {
		this.mColWidth = this.mTableWidth / this.mTableCols;
		this.mTableWidth = this.mColWidth * this.mTableCols;
		this.mCells = new TableCell[this.mTableRows][this.mTableCols];
		for (int row = 0; row < this.mTableRows; row++) {
			for (int col = 0; col < this.mTableCols; col++) {
				this.mCells[row][col] = new TableCell();
				this.mCells[row][col].mTextSize = this.mTextSize;
			}
		}
	}

	/**
	 * 设置标题行
	 * 
	 * @param value
	 *          标题
	 * @param textColor
	 *          文本颜色
	 */
	public void setTitle(String value, int textColor) {
		this.mergeCells(0, 0, this.mTableCols - 1);
		this.setCellValue(0, 0, value, textColor, 1, false, this.mTextSize);
	}

	/**
	 * 设置标题行
	 * 
	 * @param value
	 *          标题
	 * @param textColor
	 *          文本颜色
	 * @param alignType
	 *          单元格值对齐方式：0左对齐，1居中，2右对齐
	 * @param textSize
	 *          字体大小
	 */
	public void setTitle(String value, int textColor, int alignType, float textSize) {
		this.mergeCells(0, 0, this.mTableCols - 1);
		this.setCellValue(0, 0, value, textColor, alignType, false, textSize);
	}

	/**
	 * 把指定范围内的行中的每一列合并成一个单元格
	 * 
	 * @param startRow
	 *          起始行号
	 * @param endRow
	 *          结束行号
	 */
	public void mergeRows(int startRow, int endRow) {
		this.mergeRows(startRow, endRow, 0, this.mTableCols - 1);
	}

	/**
	 * 把指定范围内的行中的每一列合并成一个单元格
	 * 
	 * @param startRow
	 *          起始行号
	 * @param endRow
	 *          结束行号
	 * @param startCol
	 *          起始列号
	 * @param endCol
	 *          结束列号
	 */
	public void mergeRows(int startRow, int endRow, int startCol, int endCol) {
		if (endRow < startRow || endCol < startCol || startRow < 0 || startRow < 0 || endRow >= this.mTableRows
				|| endCol >= this.mTableCols)
			return;
		for (int row = startRow; row <= endRow; row++) {
			for (int col = startCol; col <= endCol; col++) {
				if (this.mCells[row][col] == null)
					continue;
				if (row == startRow) {
					this.mCells[row][col].mSumRows = endRow - startRow + 1;
				} else {
					this.mCells[row][col] = null;
				}
			}
		}
	}

	/**
	 * 把表格中起始行到结束行的每一行的指定列合并成一个单元格
	 * 
	 * @param startRow
	 *          起始行号
	 * @param startCol
	 *          起始列号
	 * @param endCol
	 *          结束列号
	 */
	public void mergeCols(int startRow, int startCol, int endCol) {
		this.mergeCols(startRow, this.mTableRows - 1, startCol, endCol);
	}

	/**
	 * 把表格中指定范围内的每一行的指定列合并成一个单元格
	 * 
	 * @param startRow
	 *          起始行号
	 * @param endRow
	 *          结束行号
	 * @param startCol
	 *          起始列号
	 * @param endCol
	 *          结束列号
	 */
	public void mergeCols(int startRow, int endRow, int startCol, int endCol) {
		if (endRow < startRow || endCol < startCol || startRow < 0 || startRow < 0 || endRow >= this.mTableRows
				|| endCol >= this.mTableCols)
			return;
		for (int col = startCol; col <= endCol; col++) {
			for (int row = startRow; row <= endRow; row++) {
				if (this.mCells[row][col] == null)
					continue;
				if (col == startCol) {
					this.mCells[row][col].mSumCols = endCol - startCol + 1;
				} else {
					this.mCells[row][col] = null;
				}
			}
		}
	}

	/**
	 * 合并单元格
	 * 
	 * @param row
	 *          指定行号
	 * @param startCol
	 *          起始列号
	 * @param endCol
	 *          结束列号
	 */
	public void mergeCells(int row, int startCol, int endCol) {
		this.mergeCells(row, row, startCol, endCol);
	}

	/**
	 * 合并单元格
	 * 
	 * @param startRow
	 *          起始行号
	 * @param endRow
	 *          结束行号
	 * @param startCol
	 *          起始列号
	 * @param endCol
	 *          结束列号
	 */
	public void mergeCells(int startRow, int endRow, int startCol, int endCol) {
		if (endRow < startRow || endCol < startCol || startRow < 0 || startRow < 0 || endRow >= this.mTableRows
				|| endCol >= this.mTableCols)
			return;
		for (int row = startRow; row <= endRow; row++) {
			for (int col = startCol; col <= endCol; col++) {
				if (this.mCells[row][col] == null)
					continue;
				if (row == startRow && col == startCol) {
					this.mCells[row][col].mSumRows = endRow - startRow + 1;
					this.mCells[row][col].mSumCols = endCol - startCol + 1;
				} else {
					this.mCells[row][col] = null;
				}
			}
		}
	}

	/**
	 * 初始化画笔
	 */
	private void initPaint() {
		mLinePaint = new Paint();
		mLinePaint.setColor(this.mLineColor);
		mLinePaint.setStrokeWidth(1f);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setTypeface(null);
		mTextPaint.setTextSize(mTextSize);

	}

	/**
	 * 设置表格左上角坐标
	 * 
	 * @param startX
	 *          左上角X坐标
	 * @param startY
	 *          左上角Y坐标
	 */
	public void setStartPoint(int startX, int startY) {
		this.mStartX = startX;
		this.mStartY = startY;
	}

	/**
	 * 设置单元格样式
	 * 
	 * @param rowNo
	 *          单元格行号
	 * @param colNo
	 *          单元格列号
	 * @param alignType
	 *          单元格值对齐方式：0左对齐，1居中，2右对齐
	 * @param hasRightSide
	 *          是否要画右边线
	 */
	public void setCellStyle(int rowNo, int colNo, int alignType, boolean hasRightSide) {
		if (this.mCells[rowNo][colNo] == null)
			return;
		this.mCells[rowNo][colNo].mAlignType = alignType;
		this.mCells[rowNo][colNo].hasRightSide = hasRightSide;
	}

	/**
	 * 设置单元格样式
	 * 
	 * @param rowNo
	 *          单元格行号
	 * @param colNo
	 *          单元格列号
	 * @param textColor
	 *          文本颜色
	 * @param alignType
	 *          单元格值对齐方式：0左对齐，1居中，2右对齐
	 * @param hasRightSide
	 *          是否要画右边线
	 * @param textSize
	 *          字体大小
	 */
	public void setCellStyle(int rowNo, int colNo, int textColor, int alignType, boolean hasRightSide, float textSize) {
		if (this.mCells[rowNo][colNo] == null)
			return;
		this.mCells[rowNo][colNo].mAlignType = alignType;
		this.mCells[rowNo][colNo].hasRightSide = hasRightSide;
		this.mCells[rowNo][colNo].mTextColor = textColor;
		this.mCells[rowNo][colNo].mTextSize = textSize;
	}

	/**
	 * 设置单元格值
	 * 
	 * @param rowNo
	 *          单元格行号
	 * @param colNo
	 *          单元格列号
	 * @param value
	 *          单元格值
	 * @param textColor
	 *          字体颜色
	 * @param alignType
	 *          单元格值对齐方式：0左对齐，1居中，2右对齐
	 * @param hasRightSide
	 *          是否要画右边线
	 * @param textSize
	 *          字体大小
	 */
	public void setCellValue(int rowNo, int colNo, String value, int textColor, int alignType, boolean hasRightSide,
			float textSize) {
		if (rowNo < 0 || colNo < 0 || rowNo >= this.mTableRows || colNo >= this.mTableCols)
			return;
		if (this.mCells[rowNo][colNo] == null)
			return;
		if (StringUtil.isNullOrEmpty(value))
			value = "";
		this.mCells[rowNo][colNo].mAlignType = alignType;
		this.mCells[rowNo][colNo].mCellValue = value;
		this.mCells[rowNo][colNo].hasRightSide = hasRightSide;
		this.mCells[rowNo][colNo].mTextColor = textColor;
		this.mCells[rowNo][colNo].mTextSize = textSize;
	}

	/**
	 * 设置单元格值
	 * 
	 * @param rowNo
	 *          单元格行号
	 * @param colNo
	 *          单元格列号
	 * @param value
	 *          单元格值
	 * @param textColor
	 *          字体颜色
	 * @param alignType
	 *          单元格值对齐方式：0左对齐，1居中，2右对齐
	 * @param hasRightSide
	 *          是否要画右边线
	 */
	public void setCellValue(int rowNo, int colNo, String value, int textColor, int alignType, boolean hasRightSide) {
		this.setCellValue(rowNo, colNo, value, textColor, alignType, hasRightSide, this.mTextSize);
	}

	/**
	 * 把表格绘制到画布中
	 * 
	 * @param canvas
	 *          画布
	 */
	public void drawCanvas(Canvas canvas) {
		this.drawTableSide(canvas);
		for (int row = 0; row < this.mTableRows; row++) {
			for (int col = 0; col < this.mTableCols; col++) {
				if (this.mCells[row][col] == null)
					continue;
				this.drawTableCell(canvas, this.mCells[row][col], row, col);
			}
		}
	}

	/**
	 * 绘制单元格
	 * 
	 * @param canvas
	 *          画布
	 * @param cell
	 *          单元格
	 * @param startRow
	 *          起始行号
	 * @param startCol
	 *          起始列号
	 */
	private void drawTableCell(Canvas canvas, TableCell cell, int startRow, int startCol) {
		Rect rect = this.drawTableCellSide(canvas, cell, startRow, startCol);
		float rowUpBit = (rect.height() - this.mTextSize) / 2;
		if (StringUtil.isNullOrEmpty(cell.mCellValue))
			return;
		float textX = rect.left;
		float textY = rect.bottom - rowUpBit;
		float textLen = this.mTextPaint.measureText(cell.mCellValue);
		int margin = 4;
		switch (cell.mAlignType) {
		case 1:// 居中
			textX = rect.right - (rect.width() - textLen) / 2 - textLen;
			break;
		case 2:// 右对齐
			textX = rect.right - margin - textLen;
			break;
		default:// 左对齐
			textX = rect.left + margin;
			break;
		}
		this.mTextPaint.setColor(cell.mTextColor);
		this.mTextPaint.setTextSize(cell.mTextSize);
		canvas.drawText(cell.mCellValue, textX, textY, this.mTextPaint);

	}

	/**
	 * 绘制表格边框
	 * 
	 * @param canvas
	 *          画布
	 */
	private Rect drawTableSide(Canvas canvas) {
		Rect rect = new Rect();
		rect.left = this.mStartX;
		rect.right = rect.left + this.mTableWidth;
		rect.top = this.mStartY;
		rect.bottom = rect.top + this.mRowHeight * this.mTableRows;
		// 绘制左边线
		canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, this.mLinePaint);
		// 绘制上边线
		canvas.drawLine(rect.left, rect.top, rect.right, rect.top, this.mLinePaint);
		// 绘制右边线
		canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, this.mLinePaint);
		// 绘制下边线
		canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, this.mLinePaint);
		return rect;
	}

	/**
	 * 绘制单元格边框
	 * 
	 * @param canvas
	 *          画布
	 * @param cell
	 *          单元格
	 * @param startRow
	 *          起始行号
	 * @param startCol
	 *          起始列号
	 */
	private Rect drawTableCellSide(Canvas canvas, TableCell cell, int startRow, int startCol) {
		Rect rect = new Rect();
		rect.left = this.mStartX + this.mColWidth * startCol;
		rect.right = rect.left + this.mColWidth * cell.mSumCols;
		rect.top = this.mStartY + this.mRowHeight * startRow;
		rect.bottom = rect.top + this.mRowHeight * cell.mSumRows;
		// 绘制右边线
		if (startCol + cell.mSumCols < this.mTableCols && cell.hasRightSide)
			canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, this.mLinePaint);
		// 绘制下边线
		canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, this.mLinePaint);
		return rect;
	}

	/**
	 * 获得表格高度
	 * 
	 * @return
	 */
	public float getTableHeight() {
		return this.mRowHeight * this.mTableRows;
	}

	/**
	 * 表格单元格
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class TableCell {
		/** 占有的行数 */
		private int mSumRows = 1;
		/** 占有的列数 */
		private int mSumCols = 1;
		/** 单元格值 */
		private String mCellValue = "";
		/** 单元格值对齐方式：0左对齐，1居中，2右对齐 */
		private int mAlignType = 0;
		/** 是否要画右边线 */
		private boolean hasRightSide = true;
		/** 字体颜色 */
		private int mTextColor;
		/** 字体大小 */
		private float mTextSize;
	}

	public int getTableRows() {
		return mTableRows;
	}

	public int getStartX() {
		return mStartX;
	}

	public int getStartY() {
		return mStartY;
	}
}
