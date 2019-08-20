package com.walktour.framework.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.walktour.Utils.DensityUtil;
import com.walktour.gui.R;

/**
 * 统计View基类<BR>
 * 统一定义所有统计View字体大小、行高等
 *
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-7-17]
 */
public class BasicTotalView extends View {

    protected float rowHeight = 0;

    /**
     * 字体大小,所有参数View字体大小统一
     */
    protected int textSize = 18;

    /**
     * 字体离表格的间距
     */
    protected int marginSize;
    /**
     * 表格离屏幕边框的间距
     */
    protected int tableMarginSize = 4;
    /**
     * 描述文字画笔
     */
    protected Paint fontPaint;
    /**
     * 线条画笔
     */
    protected Paint linePaint;
    /**
     * 表头画笔
     */
    protected Paint titleFontPaint;
    protected DisplayMetrics metric;
    /**
     * 参数画笔
     */
    protected Paint paramPaint;
    /**
     * 参数值画笔
     */
    protected Paint valuePaint;
    /**
     * 放大标题字体大小
     */
    protected float titleTextSize = 23;

    protected Paint backgroudPaint;

    public BasicTotalView(Context context) {
//		super(context);
        this(context, null);
//		metric = new DisplayMetrics();
//		((Activity) this.getContext()).getWindowManager().getDefaultDisplay().getMetrics(metric);
//		textSize *= (metric.densityDpi / 240.f);
//		titleTextSize *= (metric.densityDpi / 240.f);
//		rowHeight = 35 * metric.densityDpi / 240.f;
//		initPaint();
    }

    public BasicTotalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        metric = new DisplayMetrics();
        ((Activity) this.getContext()).getWindowManager().getDefaultDisplay().getMetrics(metric);
        textSize *= (metric.densityDpi / 240.f);
        titleTextSize *= (metric.densityDpi / 240.f);
//		rowHeight = 35 * metric.densityDpi / 240.f;
        rowHeight = DensityUtil.dip2px(context, 30);
        marginSize = DensityUtil.dip2px(context, 8);
        initPaint();
    }

    /**
     * 初始化画笔<BR>
     */
    @SuppressWarnings("deprecation")
    public void initPaint() {
        fontPaint = new Paint();
        fontPaint.setAntiAlias(true);
        fontPaint.setStyle(Paint.Style.FILL);
        fontPaint.setColor(getResources().getColor(R.color.app_main_text_color));
        fontPaint.setTypeface(null);
        fontPaint.setTextSize(textSize);

        titleFontPaint = fontPaint;

        valuePaint = new Paint();
        valuePaint.setAntiAlias(true);
        valuePaint.setStyle(Paint.Style.FILL);
        valuePaint.setColor(getResources().getColor(R.color.app_param_color));
        valuePaint.setTypeface(null);
        valuePaint.setTextSize(textSize);

        paramPaint = valuePaint;

        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(R.color.legend));
        linePaint.setStrokeWidth(1f);

        backgroudPaint = new Paint();
        backgroudPaint.setColor(Color.LTGRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//		canvas.drawColor(getResources().getColor(R.color.app_main_bg_color));
    }
}
