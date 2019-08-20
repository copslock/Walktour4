package com.walktour.framework.view.tableview;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 表格视图基础类
 * 
 * @author jianchao.wang
 *
 */
public abstract class BaseTableView extends View {
	/** 日志标识 */
	private static String TAG;
	/** 行高度 */
	protected float mRowHeight = 0;
	/** 字体大小,所有参数View字体大小统一 */
	private int mTextSize = 18;
	/** 表格离屏幕边框的间距 */
	private int mTableMarginSize = 4;
	/** 线条颜色 */
	private int mLineColor;
	/** 描述文字颜色 */
	protected int mTextColor;
	/** 参数值颜色 */
	protected int mValueColor;
	/** 比例尺 */
	protected DisplayMetrics mMetric;
	/** 要绘制的表格映射<表格标识，表格对象> */
	private Map<String, Table> mTableMap = new LinkedHashMap<String, Table>();
	/** 标题字体大小 */
	protected float mTitleTextSize = 23;
	/** 是否注册广播监听器 */
	private boolean isRegisterReceiver = false;
	/** 保存的图片名称 */
	private String mSavePicName;

	public BaseTableView(Context context, String tag, String savePicName) {
		super(context);
		init();
		TAG = tag;
		this.mSavePicName = savePicName;
	}

	public BaseTableView(Context context, AttributeSet attrs, String tag, String savePicName) {
		super(context, attrs);
		init();
		TAG = tag;
		this.mSavePicName = savePicName;
	}

	/**
	 * 初始化参数
	 */
	private void init() {
		this.setDrawingCacheEnabled(false);
		mMetric = new DisplayMetrics();
		((Activity) this.getContext()).getWindowManager().getDefaultDisplay().getMetrics(mMetric);
		this.mTextSize *= (mMetric.densityDpi / 240.f);
		this.mTitleTextSize *= (mMetric.densityDpi / 240.f);
		this.mRowHeight = 35 * mMetric.densityDpi / 240.f;
		this.mLineColor = getResources().getColor(R.color.legend);
		this.mTextColor = getResources().getColor(R.color.app_main_text_color);
		this.mValueColor = getResources().getColor(R.color.app_param_color);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(getResources().getColor(R.color.app_main_bg_color));
		this.mTableMap.clear();
		createTables();
		setTablesDatas();
		for (Table table : this.mTableMap.values()) {
			table.drawCanvas(canvas);
		}
		canvas.save();
		canvas.restore();
	}

	/**
	 * 生成所有表格模板
	 */
	protected abstract void createTables();

	/**
	 * 设置表格数据
	 */
	protected abstract void setTablesDatas();

	/**
	 * 设置指定表格的单元格数值
	 * 
	 * @param tag
	 *          表格对象标识
	 * @param startRowNo
	 *          起始单元格行号
	 * @param startColNo
	 *          起始单元格列号
	 * @param isText
	 *          是否文本内容，否则是数据内容
	 * @param textColor
	 *          字体颜色
	 * @param alignType
	 *          单元格值对齐方式：0左对齐，1居中，2右对齐
	 * @param hasRightSide
	 *          是否要画右边线
	 */
	protected void setTableCells(String tag, int startRowNo, int startColNo, boolean isText, int textColor, int alignType,
			boolean hasRightSide) {
		if (!this.hasTable(tag))
			return;
		Table table = this.getTable(tag);
		String[][] values;
		if (isText)
			values = this.getTableTexts(tag);
		else
			values = this.getTableValues(tag);
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[i].length; j++) {
				if (StringUtil.isNullOrEmpty(values[i][j]))
					continue;
				table.setCellValue(i + startRowNo, j + startColNo, values[i][j], textColor, alignType, hasRightSide);
			}
		}
	}

	/**
	 * 生成表格对象
	 * 
	 * @param tag
	 *          表格标识
	 * @param rows
	 *          总行数
	 * @param cols
	 *          总列数
	 * @return
	 */
	protected Table createTable(String tag, int rows, int cols) {
		Table table = new Table(rows, cols, this.getWidth() - this.mTableMarginSize * 2, (int) this.mRowHeight,
				this.mTextSize, this.mLineColor);
		if (this.mTableMap.isEmpty())
			table.setStartPoint(this.mTableMarginSize, this.mTableMarginSize);
		else {
			int startY = 0;
			for (String key : this.mTableMap.keySet()) {
				startY += (int) (this.mTableMap.get(key).getTableHeight() + this.mRowHeight);
			}
			table.setStartPoint(this.mTableMarginSize, startY);
		}
		this.mTableMap.put(tag, table);
		return table;
	}

	/**
	 * 获得表格对象
	 * 
	 * @param tag
	 *          表格标识
	 * @return
	 */
	protected Table getTable(String tag) {
		return this.mTableMap.get(tag);
	}

	/**
	 * 是否有指定标识的表格
	 * 
	 * @param tag
	 *          表格标识
	 * @return
	 */
	protected boolean hasTable(String tag) {
		return this.mTableMap.containsKey(tag);
	}

	/**
	 * 获取指定表格的数据
	 * 
	 * @param tag
	 *          表格标识
	 * @return
	 */
	protected abstract String[][] getTableValues(String tag);

	/**
	 * 获取指定表格的文本
	 * 
	 * @param tag
	 *          表格标识
	 * @return
	 */
	protected abstract String[][] getTableTexts(String tag);

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getContext().registerReceiver(mIntentReceiver, this.createIntentFilter(), null, null);
		isRegisterReceiver = true;
	}

	/**
	 * 注册广播接听器的过滤设置
	 * 
	 * @param receiver
	 *          广播接听器
	 */
	protected IntentFilter createIntentFilter() {
		IntentFilter filter = new IntentFilter(); // 注册一个消息过滤器
		filter.addAction(TotalDataByGSM.TotalTaskDataChanged);
		filter.addAction(TotalDataByGSM.TotalParaDataChanged);
		filter.addAction(TotalDataByGSM.TotalResultToPicture);
		return filter;
	}

	@Override
	protected void onDetachedFromWindow() {
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
				String path = intent.getStringExtra(TotalDataByGSM.TotalSaveFilePath) + mSavePicName;
				LogUtil.w(TAG, "--save current to file---" + path);
				BaseTableView.this.buildDrawingCache();
				UtilsMethod.SaveBitmapToFile(BaseTableView.this.getDrawingCache(), path);
			} else {
				if (!ApplicationModel.getInstance().isFreezeScreen()) {
					invalidate();
				}
			}
		}
	};

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int totalRows = 0;
		if (this.mTableMap.isEmpty())
			this.createTables();
		for (Table table : this.mTableMap.values()) {
			totalRows += table.getTableRows() + 1;
		}
		int height = 0;
		if (totalRows > 0) {
			totalRows--;
			height = (int) (this.mRowHeight * totalRows) + this.mTableMarginSize;
		} else {
			height = this.measureHeight(heightMeasureSpec);
		}
		int width = this.measureWidth(widthMeasureSpec);
//		LogUtil.d(TAG, "-----onMeasure-----width:" + width + "-----height:" + height);
		setMeasuredDimension(width, height);
	}

	/**
	 * 重算宽度
	 * 
	 * @param measureSpec
	 * @return
	 */
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

	/**
	 * 重算高度
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureHeight(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		int result = 1400;
		if (specMode == MeasureSpec.AT_MOST) {
			result = specSize;
		} else if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		}
		return result;
	}

	/**
	 * 获得资源文件中的字符串
	 * 
	 * @param resId
	 *          字符串ID
	 * @return
	 */
	protected String getString(int resId) {
		return this.getResources().getString(resId);
	}

}
