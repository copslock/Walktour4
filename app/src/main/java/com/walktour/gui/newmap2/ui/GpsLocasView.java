package com.walktour.gui.newmap2.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.walktour.Utils.DensityUtil;

/**
 * gps 轨迹线
 *
 * @author zhicheng.chen
 * @date 2018/5/30
 */
public class GpsLocasView extends View {

    private Paint mPaint;
    private Paint mStrokePaint;
    private int mColor = Color.GRAY;
    private int mStrokeColor = Color.BLACK;
    public static final int CIRCLE = 0;
    public static final int RECT = 1;

    /**
     * 图片半径
     */
    protected int mCverlayRadius;

    private int mType = CIRCLE;

    public GpsLocasView(Context context) {
        super(context);
        init();
    }

    public GpsLocasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GpsLocasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mCverlayRadius = DensityUtil.dip2px(getContext(), 12);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);

        mStrokePaint = new Paint();
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStrokeWidth(2);
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setDither(true);
    }

    public void setType(int type) {
        if (mType != type) {
            mType = type;
            invalidate();
        }
    }

    public void setColor(int color) {
        if (mColor != color) {
            mColor = color;
            mPaint.setColor(mColor);
            invalidate();
        }
    }

    public void setStrokeColor(int color) {
        if (mStrokeColor != color) {
            mStrokeColor = color;
            mStrokePaint.setColor(mStrokeColor);
            invalidate();
        }
    }


    public void setRadius(int radius) {
        if (mCverlayRadius != radius) {
            mCverlayRadius = radius;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = mCverlayRadius * 2;
        int measuredHeight = measuredWidth;
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mType == CIRCLE) {
            canvas.drawCircle(mCverlayRadius, mCverlayRadius, mCverlayRadius - 2, mPaint);
//            canvas.drawCircle(mCverlayRadius, mCverlayRadius, mCverlayRadius - 2, mStrokePaint);
        } else if (mType == RECT) {
            RectF r = new RectF(2, 2, mCverlayRadius * 2 - 2, mCverlayRadius * 2 - 2);
            canvas.drawRect(r, mPaint);
//            canvas.drawRect(r, mStrokePaint);
        }
    }
}
