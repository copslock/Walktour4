package com.walktour.gui.task.activity.scannertsma.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.UtilsMethodPara;
import com.walktour.gui.R;
import com.walktour.gui.task.activity.scanner.model.RssiParseModel;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;

/**
 ** @author jinfeng.xie
 */
public class HistogramView extends SurfaceView {

    public static final int CHART_TYPE_BAR = 0;
    public static final int CHART_TYPE_LINE = 1;
    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;
    private SurfaceHolder holder;
    private int width;
    private int height;
    private float density;
    private int chartType;
    private Format valueFormat;
    private String ordinateName;
    private String abscissaName;
    private float valueMin;
    private float valueMax;
    private int canvasColor;
    private int axisColor;
    private int chartColor;
    private int gridColor;
    private int selectionLineColor;
    private int selectionValueBackgroundColor;
    private int selectionValueTextColor;
    private float originMarginLeft;
    private float originMarginBottom;
    private float arrowHeight;
    private float arrowWidth;
    private float abscissaMarginRight;
    private int abscissaNameTextColor;
    private float abscissaNameTextSize;
    private int itemNameTextColor;
    private float itemNameTextSize;
    private float itemNameTextMarginTop;
    private int itemNameTextOrientation;
    private boolean displayItemValue;
    private int itemValueTextColor;
    private float itemValueTextSize;
    private boolean itemValueTextSizeCustom = false;//是否手动设置值文字大小
    private float itemValueTextMarginTopOrBottom;
    private int itemValueTextOrientation;
    private boolean displayHorizontalGridLine;
    private boolean displayVerticalGridLine;
    private float lineChartStrokeWidth;
    private float ordinateHeight;
    private int ordinateNameTextColor;
    private float ordinateNameTextSize;
    private float ordinateNameTextMarginBottom;
    private int ordinateValueTextColor;
    private float ordinateValueTextSize;
    private float ordinateValueTextMarginRight;
    private int ordinateValueSection;
    private boolean selectionFreeMode;
    private boolean displayAbscissaAxis;
    private boolean displayOrdinateAxis;
    private boolean displayAbscissaName;
    private boolean displayOrdinateName;
    private boolean displayItemName;
    private boolean displayOrdinateValue;
    private boolean autoPositionItemValue;
    private boolean displayAbscissaScale;
    private float abscissaScaleLength;
    private int abscissaScaleColor;
    private boolean displayOrdinateScale;
    private float ordinateScaleLength;
    private int ordinateScaleColor;
    private ArrayList<RssiParseModel> items = new ArrayList<RssiParseModel>();
    //用户触屏点选中的数据位置
    private int selectionItemIndex = -1;
    //用户触屏点纵坐标方向对应的刻度值
    private float selectionPointValue = Float.NaN;
    //表名
    private String chartTitle = "";
    //第一次点击时间
    private long firstClickTime = 0;
    //handle操作
    private boolean handled;
    //是否已经激活handle操作
    private boolean isActivityHandle = false;
    //字体大小数组
    private int[] textSizeArray = new int[]{6, 8, 11, 16};
    Context context;

    public HistogramView(Context context) {
        super(context);
        init(null, 0);
        this.context = context;
    }

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
        this.context = context;
    }

    public HistogramView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
        this.context = context;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        drawChart();
        super.dispatchDraw(canvas);
    }

    private void init(AttributeSet attrs, int defStyle) {
        initStyleable(attrs, defStyle);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //这里是处理没有传入valueMin、valueMax，或valueMin大于传入数据最小值，或valueMax小于传入数据最大值的情况
                //1、当没有传入valueMin、valueMax时，valueMin默认为传入数据最小值，valueMax默认为传入数据最大值
                //2、当通过上述计算得到的valueMin等于valueMax时，默认valueMin = valueMax - 1
                width = getWidth();
                height = getHeight();
                for (RssiParseModel item : items) {
                    float itemValue = item.getRssi();
                    valueMin = Float.isNaN(valueMin) ? itemValue : Math.min(valueMin, itemValue);
                    valueMax = Float.isNaN(valueMax) ? itemValue : Math.max(valueMax, itemValue);
                }
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
                drawChart();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    private void initStyleable(AttributeSet attrs, int defStyle) {
        density = getResources().getDisplayMetrics().density;
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Chart, defStyle, 0);
        chartType = typedArray.getInt(R.styleable.Chart_chartType, 0);
        ordinateName = typedArray.getString(R.styleable.Chart_ordinateName);
        abscissaName = typedArray.getString(R.styleable.Chart_abscissaName);
        valueMin = typedArray.getFloat(R.styleable.Chart_valueMin, Float.NaN);
        valueMax = typedArray.getFloat(R.styleable.Chart_valueMax, Float.NaN);
        canvasColor = typedArray.getColor(R.styleable.Chart_canvasColor, Color.parseColor("#197CC8"));
        axisColor = typedArray.getColor(R.styleable.Chart_axisColor, Color.CYAN);
        chartColor = typedArray.getColor(R.styleable.Chart_chartColor, Color.MAGENTA);
        gridColor = typedArray.getColor(R.styleable.Chart_gridColor, Color.DKGRAY);
        selectionLineColor = typedArray.getColor(R.styleable.Chart_selectionLineColor, Color.parseColor("#333333"));
        selectionValueBackgroundColor = typedArray.getColor(R.styleable.Chart_selectionValueBackgroundColor, Color.parseColor("#333333"));
        selectionValueTextColor = typedArray.getColor(R.styleable.Chart_selectionValueTextColor, Color.YELLOW);
        originMarginLeft = typedArray.getDimension(R.styleable.Chart_originMarginLeft, 40 * density);
        originMarginBottom = typedArray.getDimension(R.styleable.Chart_originMarginBottom, 60 * density);
        arrowHeight = typedArray.getDimension(R.styleable.Chart_arrowHeight, 10 * density);
        arrowWidth = typedArray.getDimension(R.styleable.Chart_arrowWidth, 6 * density);
        abscissaMarginRight = typedArray.getDimension(R.styleable.Chart_abscissaMarginRight, 10 * density);
        abscissaNameTextColor = typedArray.getColor(R.styleable.Chart_abscissaNameTextColor, Color.CYAN);
        abscissaNameTextSize = typedArray.getDimension(R.styleable.Chart_abscissaNameTextSize, 10 * density);
        itemNameTextColor = typedArray.getColor(R.styleable.Chart_itemNameTextColor, Color.BLUE);
        itemNameTextSize = typedArray.getDimension(R.styleable.Chart_itemNameTextSize, 14 * density);
        itemNameTextMarginTop = typedArray.getDimension(R.styleable.Chart_itemNameTextMarginTop, 3 * density);
        itemNameTextOrientation = typedArray.getInt(R.styleable.Chart_itemNameTextOrientation, 0);
        displayItemValue = typedArray.getBoolean(R.styleable.Chart_displayItemValue, true);
        itemValueTextColor = typedArray.getColor(R.styleable.Chart_itemValueTextColor, Color.parseColor("#0DAEF4"));
        itemValueTextSize = typedArray.getDimension(R.styleable.Chart_itemValueTextSize, 11 * density);
        itemValueTextMarginTopOrBottom = typedArray.getDimension(R.styleable.Chart_itemValueTextMarginTopOrBottom, 0 * density);
        itemValueTextOrientation = typedArray.getInt(R.styleable.Chart_itemValueTextOrientation, 0);
        displayHorizontalGridLine = typedArray.getBoolean(R.styleable.Chart_displayHorizontalGridLine, false);
        displayVerticalGridLine = typedArray.getBoolean(R.styleable.Chart_displayVerticalGridLine, false);
        lineChartStrokeWidth = typedArray.getDimension(R.styleable.Chart_lineChartStrokeWidth, 1 * density);
        ordinateHeight = typedArray.getDimension(R.styleable.Chart_ordinateHeight, 300 * density);
        ordinateNameTextColor = typedArray.getColor(R.styleable.Chart_ordinateNameTextColor, Color.CYAN);
        ordinateNameTextSize = typedArray.getDimension(R.styleable.Chart_ordinateNameTextSize, 14 * density);
        ordinateNameTextMarginBottom = typedArray.getDimension(R.styleable.Chart_ordinateNameTextMarginBottom, 3 * density);
        ordinateValueTextColor = typedArray.getColor(R.styleable.Chart_ordinateValueTextColor, Color.RED);
        ordinateValueTextSize = typedArray.getDimension(R.styleable.Chart_ordinateValueTextSize, 14 * density);
        ordinateValueTextMarginRight = typedArray.getDimension(R.styleable.Chart_ordinateValueTextMarginRight, 5 * density);
        ordinateValueSection = typedArray.getInt(R.styleable.Chart_ordinateValueSection, 5);
        selectionFreeMode = typedArray.getBoolean(R.styleable.Chart_selectionFreeMode, false);
        if (typedArray.hasValue(R.styleable.Chart_valueFormat)) {
            valueFormat = new DecimalFormat(typedArray.getString(R.styleable.Chart_valueFormat));
        } else {
            valueFormat = new DecimalFormat("#.#");
        }
        displayAbscissaAxis = typedArray.getBoolean(R.styleable.Chart_displayAbscissaAxis, true);
        displayOrdinateAxis = typedArray.getBoolean(R.styleable.Chart_displayOrdinateAxis, true);
        displayAbscissaName = typedArray.getBoolean(R.styleable.Chart_displayAbscissaName, true);
        displayOrdinateName = typedArray.getBoolean(R.styleable.Chart_displayOrdinateName, true);
        displayItemName = typedArray.getBoolean(R.styleable.Chart_displayItemName, true);
        displayOrdinateValue = typedArray.getBoolean(R.styleable.Chart_displayOrdinateValue, true);
        autoPositionItemValue = typedArray.getBoolean(R.styleable.Chart_autoPositionItemValue, false);
        displayAbscissaScale = typedArray.getBoolean(R.styleable.Chart_displayAbscissaScale, false);
        abscissaScaleLength = typedArray.getDimension(R.styleable.Chart_abscissaScaleLength, 3 * density);
        abscissaScaleColor = typedArray.getColor(R.styleable.Chart_abscissaScaleColor, Color.LTGRAY);
        displayOrdinateScale = typedArray.getBoolean(R.styleable.Chart_displayOrdinateScale, false);
        ordinateScaleLength = typedArray.getDimension(R.styleable.Chart_ordinateScaleLength, 3 * density);
        ordinateScaleColor = typedArray.getColor(R.styleable.Chart_ordinateScaleColor, Color.LTGRAY);
        typedArray.recycle();
    }

    private void drawChart() {
        Canvas canvas = holder.lockCanvas();
        //绘制画板背景颜色
        canvas.drawColor(canvasColor);
        float space = (width - originMarginLeft - abscissaMarginRight) / (items.size() * 3 + 1);
        Paint paint = new Paint();
        Path path = new Path();
        //TODO 画表头
        drawChartTopStructure(canvas, paint);

        //绘制纵坐标旁的数据刻度值
        if (displayOrdinateValue) {
            paint.setTextSize(ordinateValueTextSize);
            paint.setColor(ordinateValueTextColor);
            float ordinateValueTextOffset = -(paint.ascent() + paint.descent()) / 2;
            for (int i = 0; i <= ordinateValueSection; i++) {
                String valueMinText = valueFormat.format(valueMin + (valueMax - valueMin) / ordinateValueSection * i);
                canvas.drawText(valueMinText, originMarginLeft - paint.measureText(valueMinText) - ordinateValueTextMarginRight, height - originMarginBottom - ordinateHeight / ordinateValueSection * i + ordinateValueTextOffset, paint);
            }
            paint.reset();
        }
        //绘制横坐标下的数据名称
        if (displayItemName) {
            paint.setColor(itemNameTextColor);
            paint.setTextSize(itemNameTextSize);
            float itemNameTextOffset = -(paint.ascent() + paint.descent()) / 2;
            for (int i = 0; i < items.size(); i++) {
                String itemName = items.get(i).getChannel() + "";
                //判断数据名称字体方向
                switch (itemNameTextOrientation) {
                    case ORIENTATION_HORIZONTAL:
                        if (items.size() > 20) {//数据集合大于20个
                            if (i == 0) {
                                canvas.drawText(itemName, originMarginLeft + (i * 3 + 2) * space - paint.measureText(itemName) / 2, height - originMarginBottom - paint.ascent() + itemNameTextMarginTop, paint);
                            } else {
                                int flag = Math.round(items.size() / 5);
                                if ((i + 1) % flag == 0)
                                    canvas.drawText(itemName, originMarginLeft + (i * 3 + 2) * space - paint.measureText(itemName) / 2, height - originMarginBottom - paint.ascent() + itemNameTextMarginTop, paint);
                            }
                        } else {//数据集合少于20个
                            canvas.drawText(itemName, originMarginLeft + (i * 3 + 2) * space - paint.measureText(itemName) / 2, height - originMarginBottom - paint.ascent() + itemNameTextMarginTop, paint);
                        }
                        break;
                    case ORIENTATION_VERTICAL:
                        //如果是纵向文字，那就先创建垂直的引导路径
                        if (items.size() > 20) {//数据集合大于20个
                            if (i == 0) {
                                path.moveTo(originMarginLeft + (i * 3 + 2) * space, height - originMarginBottom + itemNameTextMarginTop + paint.measureText(itemName));
                                path.lineTo(originMarginLeft + (i * 3 + 2) * space, height - originMarginBottom + itemNameTextMarginTop);
                                canvas.drawTextOnPath(itemName, path, 0, itemNameTextOffset, paint);
                                path.reset();
                            } else {
                                int flag = Math.round(items.size() / 5);
                                if ((i + 1) % flag == 0) {
                                    path.moveTo(originMarginLeft + (i * 3 + 2) * space, height - originMarginBottom + itemNameTextMarginTop + paint.measureText(itemName));
                                    path.lineTo(originMarginLeft + (i * 3 + 2) * space, height - originMarginBottom + itemNameTextMarginTop);
                                    canvas.drawTextOnPath(itemName, path, 0, itemNameTextOffset, paint);
                                    path.reset();
                                }
                            }
                        } else {//数据集合少于20个
                            path.moveTo(originMarginLeft + (i * 3 + 2) * space, height - originMarginBottom + itemNameTextMarginTop + paint.measureText(itemName));
                            path.lineTo(originMarginLeft + (i * 3 + 2) * space, height - originMarginBottom + itemNameTextMarginTop);
                            canvas.drawTextOnPath(itemName, path, 0, itemNameTextOffset, paint);
                            path.reset();
                        }
                        break;
                }
            }
            paint.reset();
        }
        paint.setColor(gridColor);
        //绘制图表背景网格水平线
        if (displayHorizontalGridLine) {
            for (int i = 0; i <= ordinateValueSection; i++) {
                canvas.drawLine(originMarginLeft, height - originMarginBottom - ordinateHeight / ordinateValueSection * i, width - abscissaMarginRight - 2 * space, height - originMarginBottom - ordinateHeight / ordinateValueSection * i, paint);
            }
        }
        //绘制图表背景网格垂直线
        if (displayVerticalGridLine) {
            for (int i = 0; i < items.size(); i++) {
                canvas.drawLine(originMarginLeft + (i * 3 + 2) * space, height - originMarginBottom, originMarginLeft + (i * 3 + 2) * space, height - originMarginBottom - ordinateHeight, paint);
            }
        }
        paint.reset();
        //绘制图表形状，先判断是绘制柱状图还是折线图
        paint.setColor(chartColor);
        switch (chartType) {
            case CHART_TYPE_BAR:
                for (int i = 0; i < items.size(); i++) {
                    canvas.drawRect(originMarginLeft + (i * 3 + 1) * space, height - originMarginBottom - ordinateHeight * (items.get(i).getRssi() - valueMin) / (valueMax - valueMin), originMarginLeft + (i * 3 + 3) * space, height - originMarginBottom, paint);
                }
                break;
            case CHART_TYPE_LINE:
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeWidth(lineChartStrokeWidth);
                for (int i = 0; i < items.size(); i++) {
                    if (i == 0) {
                        path.moveTo(originMarginLeft + 2 * space, height - originMarginBottom - ordinateHeight * (items.get(0).getRssi() - valueMin) / (valueMax - valueMin));
                    } else {
                        path.lineTo(originMarginLeft + (i * 3 + 2) * space, height - originMarginBottom - ordinateHeight * (items.get(i).getRssi() - valueMin) / (valueMax - valueMin));
                    }
                }
                canvas.drawPath(path, paint);
                path.reset();
                break;
        }
        paint.reset();
        //在图表图形上绘制每个数据的值
        if (displayItemValue) {
            paint.setColor(itemValueTextColor);
            paint.setTextSize(itemValueTextSize);
            float itemValueTextOffset = -(paint.ascent() + paint.descent()) / 2;
            for (int i = 0; i < items.size(); i++) {
                float itemValue = items.get(i).getRssi();
                float itemValueTmp = itemValue - 0.05f;
                if (itemValue > 0)
                    itemValueTmp = itemValue - 0.05f;
                else
                    itemValueTmp = itemValue + 0.05f;
                String itemValueText = valueFormat.format(itemValueTmp);
                //是否合适把数据值显示在图形数据点下方
                boolean displayItemValueBelow = i == 0 ? items.size() != 1 && itemValue < items.get(1).getRssi() : i == items.size() - 1 ? itemValue < items.get(i - 1).getRssi() : itemValue < (items.get(i + 1).getRssi() + items.get(i - 1).getRssi()) / 2;
                float y = height - originMarginBottom - ordinateHeight * (itemValue - valueMin) / (valueMax - valueMin);
                //判断绘制的数据值文字方向
                //要判断是否超过坐标轴边界
                switch (itemValueTextOrientation) {
                    case ORIENTATION_HORIZONTAL:
                        canvas.drawText(itemValueText, originMarginLeft + (i * 3 + 2) * space - paint.measureText(itemValueText) / 2, autoPositionItemValue && displayItemValueBelow && y - paint.ascent() + paint.descent() + itemValueTextMarginTopOrBottom <= height - originMarginBottom ? y - paint.ascent() + itemValueTextMarginTopOrBottom : y - paint.descent() - itemValueTextMarginTopOrBottom, paint);
                        break;
                    case ORIENTATION_VERTICAL:
                        path.moveTo(originMarginLeft + (i * 3 + 2) * space, autoPositionItemValue && displayItemValueBelow && y + paint.measureText(itemValueText) + itemValueTextMarginTopOrBottom <= height - originMarginBottom ? y + paint.measureText(itemValueText) + itemValueTextMarginTopOrBottom : y - itemValueTextMarginTopOrBottom);
                        path.lineTo(originMarginLeft + (i * 3 + 2) * space, autoPositionItemValue && displayItemValueBelow && y + paint.measureText(itemValueText) + itemValueTextMarginTopOrBottom <= height - originMarginBottom ? y + itemValueTextMarginTopOrBottom : y - paint.measureText(itemValueText) - itemValueTextMarginTopOrBottom);
                        canvas.drawTextOnPath(itemValueText, path, 0, itemValueTextOffset, paint);
                        path.reset();
                        break;
                }
            }
            paint.reset();
        }
        //如果用户触屏选中了某个数据，就绘制十字线
        if (selectionItemIndex != -1 && selectionItemIndex < items.size()) {
            //固定模式下十字线只能定位在数据值上，自由模式下十字线可以跟随触屏点上下移动
            float value = selectionFreeMode ? selectionPointValue : items.get(selectionItemIndex).getRssi();
            String valueText = valueFormat.format(value);
            paint.setColor(selectionLineColor);
            PathEffect e = new DashPathEffect(new float[]{3, 5, 3, 5}, 1);
            paint.setPathEffect(e);
            canvas.drawLine(originMarginLeft, height - originMarginBottom - ordinateHeight * (value - valueMin) / (valueMax - valueMin), width - abscissaMarginRight + arrowHeight, height - originMarginBottom - ordinateHeight * (value - valueMin) / (valueMax - valueMin), paint);
            canvas.drawLine(originMarginLeft + (selectionItemIndex * 3 + 2) * space, height - originMarginBottom, originMarginLeft + (selectionItemIndex * 3 + 2) * space, height - originMarginBottom - ordinateHeight - arrowHeight, paint);
            paint.reset();
            paint.setTextSize(ordinateValueTextSize);
            float selectionValueTextOffset = -(paint.ascent() + paint.descent()) / 2;
            paint.setColor(selectionValueBackgroundColor);
            canvas.drawRect(0, height - originMarginBottom - ordinateHeight * (value - valueMin) / (valueMax - valueMin) + paint.ascent() + selectionValueTextOffset - 3 * density, originMarginLeft, height - originMarginBottom - ordinateHeight * (value - valueMin) / (valueMax - valueMin) + paint.descent() + selectionValueTextOffset + 3 * density, paint);
            paint.setColor(selectionValueTextColor);
            canvas.drawText(valueText, originMarginLeft - paint.measureText(valueText) - ordinateValueTextMarginRight, height - originMarginBottom - ordinateHeight * (value - valueMin) / (valueMax - valueMin) + selectionValueTextOffset, paint);
            paint.reset();
        }
        //绘制横坐标刻度线
        if (displayAbscissaScale) {
            paint.setColor(abscissaScaleColor);
            for (int i = 0; i < items.size(); i++) {
                if (items.size() > 20) {//数据集合大于20个
                    if (i == 0) {
                        canvas.drawLine(originMarginLeft + (i * 3 + 2) * space, height - originMarginBottom + abscissaScaleLength, originMarginLeft + (i * 3 + 2) * space, height - originMarginBottom, paint);
                    } else {
                        int flag = Math.round(items.size() / 5);
                        if ((i + 1) % flag == 0)
                            canvas.drawLine(originMarginLeft + (i * 3 + 2) * space, height - originMarginBottom + abscissaScaleLength, originMarginLeft + (i * 3 + 2) * space, height - originMarginBottom, paint);
                    }
                } else {//数据集合少于20个
                    canvas.drawLine(originMarginLeft + (i * 3 + 2) * space, height - originMarginBottom + abscissaScaleLength, originMarginLeft + (i * 3 + 2) * space, height - originMarginBottom, paint);
                }
            }
            paint.reset();
        }
        //绘制纵坐标刻度
        if (displayOrdinateScale) {
            paint.setColor(ordinateScaleColor);
            for (int i = 0; i <= ordinateValueSection; i++) {
                canvas.drawLine(originMarginLeft - ordinateScaleLength, height - originMarginBottom - ordinateHeight / ordinateValueSection * i, originMarginLeft, height - originMarginBottom - ordinateHeight / ordinateValueSection * i, paint);
            }
            paint.reset();
        }
        //绘制横坐标轴
        if (displayAbscissaAxis) {
            paint.setColor(axisColor);
            canvas.drawLine(originMarginLeft, height - originMarginBottom, width - abscissaMarginRight, height - originMarginBottom, paint);
            paint.setAntiAlias(true);
            path.moveTo(width - abscissaMarginRight + arrowHeight, height - originMarginBottom);
            path.lineTo(width - abscissaMarginRight, height - originMarginBottom - arrowWidth / 2);
            path.lineTo(width - abscissaMarginRight, height - originMarginBottom + arrowWidth / 2);
            path.close();
            canvas.drawPath(path, paint);
            path.reset();
            paint.reset();
        }
        //绘制纵坐标轴
        if (displayOrdinateAxis) {
            paint.setColor(axisColor);
            canvas.drawLine(originMarginLeft, height - originMarginBottom, originMarginLeft, height - originMarginBottom - ordinateHeight, paint);
            paint.setAntiAlias(true);
            path.moveTo(originMarginLeft, height - originMarginBottom - ordinateHeight - arrowHeight);
            path.lineTo(originMarginLeft - arrowWidth / 2, height - originMarginBottom - ordinateHeight);
            path.lineTo(originMarginLeft + arrowWidth / 2, height - originMarginBottom - ordinateHeight);
            path.close();
            canvas.drawPath(path, paint);
            path.reset();
            paint.reset();
        }
        //绘制横坐标名称
        if (displayAbscissaName && abscissaName != null) {
            paint.setTextSize(abscissaNameTextSize);
            paint.setColor(abscissaNameTextColor);
//            canvas.drawText(abscissaName, (width + originMarginLeft - abscissaMarginRight - paint.measureText(abscissaName)) / 2, height - paint.descent(), paint);
            canvas.drawText(abscissaName, 10*density, height - originMarginBottom+25*density, paint);
            paint.reset();
        }
        //绘制纵坐标名称
        if (displayOrdinateName && ordinateName != null) {
            paint.setTextSize(ordinateNameTextSize);
            paint.setColor(ordinateNameTextColor);
            canvas.drawText(ordinateName, originMarginLeft - paint.measureText(ordinateName) / 2, height - originMarginBottom - ordinateHeight - arrowHeight - ordinateNameTextMarginBottom - paint.descent(), paint);
            paint.reset();
        }
        holder.unlockCanvasAndPost(canvas);
    }


    /**
     * 画表头显示数据结构
     *
     * @param canvas
     * @param paint
     */
    @SuppressLint("NewApi")
    private void drawChartTopStructure(Canvas canvas, Paint paint) {
        paint.setColor(Color.parseColor("#ffffff"));
        //花外面边框
        RectF roundRect = new RectF(10 * density, 10 * density,
                width - 10 * density, 150 * density);
        canvas.drawRoundRect(roundRect, 6 * density, 6 * density, paint);
        // 表头结构
        String Band = "Band: ";
        String RBW = "RBW(kHz): ";
        String Channel = "Channel: ";
        String RSSI = "RSSI(dBm): ";
        //画标题
        paint.setTextSize(14 * density);
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        paint.setColor(Color.parseColor("#000000"));
        canvas.drawText(getChartTitle(), roundRect.left + 20 * density, roundRect.top + 40 * density, paint);
        paint.reset();
        //画LTE蓝框
        String networkType = "IDLE";
        if (selectionItemIndex != -1 && selectionItemIndex < items.size()) {
            networkType = UtilsMethodPara.getNetWorkStr(items.get(selectionItemIndex).getNetType());
        } else if (items.size() != 0) {
            networkType = UtilsMethodPara.getNetWorkStr(items.get(0).getNetType());
        }
        Rect networkTypeRect = new Rect();
        paint.setTextSize(14 * density);
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        paint.getTextBounds(networkType, 0, networkType.length(), networkTypeRect);
        paint.setColor(Color.parseColor("#49D7E2"));
//        paint.setStrokeWidth(30);
        Log.e("max", "" + networkTypeRect);
        canvas.drawRoundRect(roundRect.left + 50 * density + paint.measureText(getChartTitle()), roundRect.top + 25 * density
                , roundRect.left + 70 * density + paint.measureText(getChartTitle()) + networkTypeRect.right, roundRect.top + 35 * density - networkTypeRect.top,
                6 * density, 6 * density, paint);
        //画LTE
        paint.setColor(Color.WHITE);
        canvas.drawText(networkType, roundRect.left + 60 * density + paint.measureText(getChartTitle()), roundRect.top + 40 * density, paint);

        //画线
        paint.reset();
        paint.setStrokeWidth(3);
        paint.setColor(Color.parseColor("#E9E9E9"));
        canvas.drawLine(roundRect.left + 20 * density, roundRect.top + 60 * density,
                roundRect.right - 20 * density, roundRect.top + 60 * density, paint);
        //表头显示的数据列
        paint.reset();
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(14 * density);
        paint.setColor(Color.parseColor("#333333"));
        canvas.drawText(Band, roundRect.left + 20 * density, roundRect.top + 90 * density, paint);
        canvas.drawText(RBW, roundRect.left + 60 * density + paint.measureText(getChartTitle()), roundRect.top + 90 * density, paint);
        canvas.drawText(Channel, roundRect.left + 20 * density, roundRect.top + 110 * density, paint);
        canvas.drawText(RSSI, roundRect.left + 60 * density + paint.measureText(getChartTitle()), roundRect.top + 110 * density, paint);
        // 画具体数值 BEGIN
        paint.setTextSize(14 * density);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setColor(Color.parseColor("#0DAEF4"));
        if (selectionItemIndex != -1 && selectionItemIndex < items.size()) {
            RssiParseModel data = items.get(selectionItemIndex);
            canvas.drawText(data.getBandCode() + "", paint.measureText(Channel) + roundRect.left + 20 * density, roundRect.top + 90 * density, paint);
            canvas.drawText(UtilsMethod.convertValue(data.getRbw()) + "", paint.measureText(RSSI) + roundRect.left + 60 * density + paint.measureText(getChartTitle()), roundRect.top + 90 * density, paint);
            canvas.drawText(data.getChannel() + "", paint.measureText(Channel) + roundRect.left + 20 * density, 120 * density, paint);
            canvas.drawText(data.getRssi() + "", paint.measureText(RSSI) + roundRect.left + 60 * density + paint.measureText(getChartTitle()), 120 * density, paint);
        } else if (items.size() != 0) {
            RssiParseModel data = items.get(0);
            canvas.drawText(data.getBandCode() + "", paint.measureText(Channel) + roundRect.left + 20 * density, roundRect.top + 90 * density, paint);
            canvas.drawText(UtilsMethod.convertValue(data.getRbw()) + "", paint.measureText(RSSI) + roundRect.left + 60 * density + paint.measureText(getChartTitle()), roundRect.top + 90 * density, paint);
            canvas.drawText(data.getChannel() + "", paint.measureText(Channel) + roundRect.left + 20 * density, 120 * density, paint);
            canvas.drawText(data.getRssi() + "", paint.measureText(RSSI) + roundRect.left + 60 * density + paint.measureText(getChartTitle()), 120 * density, paint);
        }

        //计算纵坐标轴高度
        float yPoint = height - originMarginBottom - ordinateHeight;
        if (yPoint < 160 * density)
            ordinateHeight = height - originMarginBottom - 160 * density;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float space = (width - originMarginLeft - abscissaMarginRight) / (items.size() * 3 + 1);
        if (x >= originMarginLeft + space / 2 && x < width - abscissaMarginRight - space / 2 && y <= height - originMarginBottom && y > height - originMarginBottom - ordinateHeight) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    activityHandle();
                    break;
                case MotionEvent.ACTION_MOVE:
                    selectionItemIndex = Math.round((x - originMarginLeft - space / 2) / space / 3 - 0.5f);
                    selectionPointValue = (valueMax - valueMin) * (height - originMarginBottom - y) / ordinateHeight + valueMin;
                    handled = true;
                    break;
            }
        } else {
            selectionItemIndex = -1;
            selectionPointValue = Float.NaN;
            handled = super.onTouchEvent(event);
        }
        drawChart();
        Log.e("Bunny", "Chart_onTouchEvent_" + event.getAction() + " return: " + handled);
        return handled;
    }


    //处理手势问题
    private void activityHandle() {
        if ((System.currentTimeMillis() - firstClickTime) > 300) {
            firstClickTime = System.currentTimeMillis();
        } else {
            if (!isActivityHandle) {
                isActivityHandle = true;
                handled = true;
            } else {
                selectionItemIndex = -1;
                selectionPointValue = Float.NaN;
                isActivityHandle = false;
                handled = false;
            }

        }
    }

    /**
     * 根据传入的数据数组类别大小获取默认的字体大小
     */
    private void initDefaultItemValueTextSize() {

        displayItemValue = true;
        int size = items.size();
        if (size == 0)
            return;
        if (size <= 6)
            itemValueTextSize = textSizeArray[3] * density;
        else if (size > 6 && size <= 10)
            itemValueTextSize = textSizeArray[2] * density;
        else if (size > 10 && size <= 15)
            itemValueTextSize = textSizeArray[1] * density;
        else if (size > 15 && size <= 20)
            itemValueTextSize = textSizeArray[0] * density;
        else
            displayItemValue = false;

    }

    public int getChartType() {
        return chartType;
    }

    public void setChartType(int chartType) {
        this.chartType = chartType;
    }

    public Format getValueFormat() {
        return valueFormat;
    }

    public void setValueFormat(Format valueFormat) {
        this.valueFormat = valueFormat;
    }

    public String getOrdinateName() {
        return ordinateName;
    }

    public void setOrdinateName(String ordinateName) {
        this.ordinateName = ordinateName;
    }

    public String getAbscissaName() {
        return abscissaName;
    }

    public void setAbscissaName(String abscissaName) {
        this.abscissaName = abscissaName;
    }

    public float getValueMin() {
        return valueMin;
    }

    public void setValueMin(float valueMin) {
        this.valueMin = valueMin;
    }

    public float getValueMax() {
        return valueMax;
    }

    public void setValueMax(float valueMax) {
        this.valueMax = valueMax;
    }

    public int getCanvasColor() {
        return canvasColor;
    }

    public void setCanvasColor(int canvasColor) {
        this.canvasColor = canvasColor;
    }

    public int getAxisColor() {
        return axisColor;
    }

    public void setAxisColor(int axisColor) {
        this.axisColor = axisColor;
    }

    public int getChartColor() {
        return chartColor;
    }

    public void setChartColor(int chartColor) {
        this.chartColor = chartColor;
    }

    public int getGridColor() {
        return gridColor;
    }

    public void setGridColor(int gridColor) {
        this.gridColor = gridColor;
    }

    public int getSelectionLineColor() {
        return selectionLineColor;
    }

    public void setSelectionLineColor(int selectionLineColor) {
        this.selectionLineColor = selectionLineColor;
    }

    public int getSelectionValueBackgroundColor() {
        return selectionValueBackgroundColor;
    }

    public void setSelectionValueBackgroundColor(int selectionValueBackgroundColor) {
        this.selectionValueBackgroundColor = selectionValueBackgroundColor;
    }

    public int getSelectionValueTextColor() {
        return selectionValueTextColor;
    }

    public void setSelectionValueTextColor(int selectionValueTextColor) {
        this.selectionValueTextColor = selectionValueTextColor;
    }

    public float getOriginMarginLeft() {
        return originMarginLeft;
    }

    public void setOriginMarginLeft(float originMarginLeft) {
        this.originMarginLeft = originMarginLeft;
    }

    public float getOriginMarginBottom() {
        return originMarginBottom;
    }

    public void setOriginMarginBottom(float originMarginBottom) {
        this.originMarginBottom = originMarginBottom;
    }

    public float getArrowHeight() {
        return arrowHeight;
    }

    public void setArrowHeight(float arrowHeight) {
        this.arrowHeight = arrowHeight;
    }

    public float getArrowWidth() {
        return arrowWidth;
    }

    public void setArrowWidth(float arrowWidth) {
        this.arrowWidth = arrowWidth;
    }

    public float getAbscissaMarginRight() {
        return abscissaMarginRight;
    }

    public void setAbscissaMarginRight(float abscissaMarginRight) {
        this.abscissaMarginRight = abscissaMarginRight;
    }

    public int getAbscissaNameTextColor() {
        return abscissaNameTextColor;
    }

    public void setAbscissaNameTextColor(int abscissaNameTextColor) {
        this.abscissaNameTextColor = abscissaNameTextColor;
    }

    public float getAbscissaNameTextSize() {
        return abscissaNameTextSize;
    }

    public void setAbscissaNameTextSize(float abscissaNameTextSize) {
        this.abscissaNameTextSize = abscissaNameTextSize;
    }

    public int getItemNameTextColor() {
        return itemNameTextColor;
    }

    public void setItemNameTextColor(int itemNameTextColor) {
        this.itemNameTextColor = itemNameTextColor;
    }

    public float getItemNameTextSize() {
        return itemNameTextSize;
    }

    public void setItemNameTextSize(float itemNameTextSize) {
        this.itemNameTextSize = itemNameTextSize;
    }

    public float getItemNameTextMarginTop() {
        return itemNameTextMarginTop;
    }

    public void setItemNameTextMarginTop(float itemNameTextMarginTop) {
        this.itemNameTextMarginTop = itemNameTextMarginTop;
    }

    public int getItemNameTextOrientation() {
        return itemNameTextOrientation;
    }

    public void setItemNameTextOrientation(int itemNameTextOrientation) {
        this.itemNameTextOrientation = itemNameTextOrientation;
    }

    public boolean isDisplayItemValue() {
        return displayItemValue;
    }

    public void setDisplayItemValue(boolean displayItemValue) {
        this.displayItemValue = displayItemValue;
    }

    public int getItemValueTextColor() {
        return itemValueTextColor;
    }

    public void setItemValueTextColor(int itemValueTextColor) {
        this.itemValueTextColor = itemValueTextColor;
    }

    public float getItemValueTextSize() {
        return itemValueTextSize;
    }

    public void setItemValueTextSize(float itemValueTextSize) {
        this.itemValueTextSize = itemValueTextSize;
    }

    public float getItemValueTextMarginTopOrBottom() {
        return itemValueTextMarginTopOrBottom;
    }

    public void setItemValueTextMarginTopOrBottom(float itemValueTextMarginTopOrBottom) {
        this.itemValueTextMarginTopOrBottom = itemValueTextMarginTopOrBottom;
    }

    public int getItemValueTextOrientation() {
        return itemValueTextOrientation;
    }

    public void setItemValueTextOrientation(int itemValueTextOrientation) {
        this.itemValueTextOrientation = itemValueTextOrientation;
    }

    public boolean isDisplayHorizontalGridLine() {
        return displayHorizontalGridLine;
    }

    public void setDisplayHorizontalGridLine(boolean displayHorizontalGridLine) {
        this.displayHorizontalGridLine = displayHorizontalGridLine;
    }

    public boolean isDisplayVerticalGridLine() {
        return displayVerticalGridLine;
    }

    public void setDisplayVerticalGridLine(boolean displayVerticalGridLine) {
        this.displayVerticalGridLine = displayVerticalGridLine;
    }

    public float getLineChartStrokeWidth() {
        return lineChartStrokeWidth;
    }

    public void setLineChartStrokeWidth(float lineChartStrokeWidth) {
        this.lineChartStrokeWidth = lineChartStrokeWidth;
    }

    public float getOrdinateHeight() {
        return ordinateHeight;
    }

    public void setOrdinateHeight(float ordinateHeight) {
        this.ordinateHeight = ordinateHeight;
    }

    public int getOrdinateNameTextColor() {
        return ordinateNameTextColor;
    }

    public void setOrdinateNameTextColor(int ordinateNameTextColor) {
        this.ordinateNameTextColor = ordinateNameTextColor;
    }

    public float getOrdinateNameTextSize() {
        return ordinateNameTextSize;
    }

    public void setOrdinateNameTextSize(float ordinateNameTextSize) {
        this.ordinateNameTextSize = ordinateNameTextSize;
    }

    public float getOrdinateNameTextMarginBottom() {
        return ordinateNameTextMarginBottom;
    }

    public void setOrdinateNameTextMarginBottom(float ordinateNameTextMarginBottom) {
        this.ordinateNameTextMarginBottom = ordinateNameTextMarginBottom;
    }

    public int getOrdinateValueTextColor() {
        return ordinateValueTextColor;
    }

    public void setOrdinateValueTextColor(int ordinateValueTextColor) {
        this.ordinateValueTextColor = ordinateValueTextColor;
    }

    public float getOrdinateValueTextSize() {
        return ordinateValueTextSize;
    }

    public void setOrdinateValueTextSize(float ordinateValueTextSize) {
        this.ordinateValueTextSize = ordinateValueTextSize;
    }

    public float getOrdinateValueTextMarginRight() {
        return ordinateValueTextMarginRight;
    }

    public void setOrdinateValueTextMarginRight(float ordinateValueTextMarginRight) {
        this.ordinateValueTextMarginRight = ordinateValueTextMarginRight;
    }

    public int getOrdinateValueSection() {
        return ordinateValueSection;
    }

    public void setOrdinateValueSection(int ordinateValueSection) {
        this.ordinateValueSection = ordinateValueSection;
    }

    public boolean isSelectionFreeMode() {
        return selectionFreeMode;
    }

    public void setSelectionFreeMode(boolean selectionFreeMode) {
        this.selectionFreeMode = selectionFreeMode;
    }

    public boolean isDisplayAbscissaAxis() {
        return displayAbscissaAxis;
    }

    public void setDisplayAbscissaAxis(boolean displayAbscissaAxis) {
        this.displayAbscissaAxis = displayAbscissaAxis;
    }

    public boolean isDisplayOrdinateAxis() {
        return displayOrdinateAxis;
    }

    public void setDisplayOrdinateAxis(boolean displayOrdinateAxis) {
        this.displayOrdinateAxis = displayOrdinateAxis;
    }

    public boolean isDisplayAbscissaName() {
        return displayAbscissaName;
    }

    public void setDisplayAbscissaName(boolean displayAbscissaName) {
        this.displayAbscissaName = displayAbscissaName;
    }

    public boolean isDisplayOrdinateName() {
        return displayOrdinateName;
    }

    public void setDisplayOrdinateName(boolean displayOrdinateName) {
        this.displayOrdinateName = displayOrdinateName;
    }

    public boolean isDisplayItemName() {
        return displayItemName;
    }

    public void setDisplayItemName(boolean displayItemName) {
        this.displayItemName = displayItemName;
    }

    public boolean isDisplayOrdinateValue() {
        return displayOrdinateValue;
    }

    public void setDisplayOrdinateValue(boolean displayOrdinateValue) {
        this.displayOrdinateValue = displayOrdinateValue;
    }

    public boolean isAutoPositionItemValue() {
        return autoPositionItemValue;
    }

    public void setAutoPositionItemValue(boolean autoPositionItemValue) {
        this.autoPositionItemValue = autoPositionItemValue;
    }

    public boolean isDisplayAbscissaScale() {
        return displayAbscissaScale;
    }

    public void setDisplayAbscissaScale(boolean displayAbscissaScale) {
        this.displayAbscissaScale = displayAbscissaScale;
    }

    public float getAbscissaScaleLength() {
        return abscissaScaleLength;
    }

    public void setAbscissaScaleLength(float abscissaScaleLength) {
        this.abscissaScaleLength = abscissaScaleLength;
    }

    public int getAbscissaScaleColor() {
        return abscissaScaleColor;
    }

    public void setAbscissaScaleColor(int abscissaScaleColor) {
        this.abscissaScaleColor = abscissaScaleColor;
    }

    public boolean isDisplayOrdinateScale() {
        return displayOrdinateScale;
    }

    public void setDisplayOrdinateScale(boolean displayOrdinateScale) {
        this.displayOrdinateScale = displayOrdinateScale;
    }

    public float getOrdinateScaleLength() {
        return ordinateScaleLength;
    }

    public void setOrdinateScaleLength(float ordinateScaleLength) {
        this.ordinateScaleLength = ordinateScaleLength;
    }

    public int getOrdinateScaleColor() {
        return ordinateScaleColor;
    }

    public void setOrdinateScaleColor(int ordinateScaleColor) {
        this.ordinateScaleColor = ordinateScaleColor;
    }

    public String getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

    public boolean isItemValueTextSizeCustom() {
        return itemValueTextSizeCustom;
    }

    public void setItemValueTextSizeCustom(boolean itemValueTextSizeCustom) {
        this.itemValueTextSizeCustom = itemValueTextSizeCustom;
    }

    public ArrayList<RssiParseModel> getItems() {
        return items;
    }

    public void setItems(ArrayList<RssiParseModel> items) {
        if (items == null)
            return;
        this.items = items;
        if (!itemValueTextSizeCustom)
            initDefaultItemValueTextSize();
        if (items.size() > 20) {
            setDisplayItemValue(false);
        }
    }
}
