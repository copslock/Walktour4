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

import java.util.ArrayList;
import java.util.List;

/**
 * 扇形图
 *
 * @author zhicheng.chen
 * @date 2018/5/30
 */
public class SectorView extends View {

    private Paint mPaint;
    private Paint mStrokePaint;
    private Paint mSelectPaint;
    private Paint mHighLightPaint;
    private List<Integer> mAngle;
    /**
     * 基站方位颜色集合，方便区分不同的方向角
     */
    private int[] mColorSets = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN};
    /**
     * 图片半径
     */
    protected int mCverlayRadius;

    private boolean mIsSelected;
    private boolean mIshighlight;

    public SectorView(Context context) {
        super(context);
        init();
    }

    public SectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mCverlayRadius = dp2px(12);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mSelectPaint = new Paint();
        mSelectPaint.setAntiAlias(true);
        mSelectPaint.setStyle(Paint.Style.FILL);
        mSelectPaint.setColor(0x66157BC3);

        mHighLightPaint = new Paint();
        mHighLightPaint.setStrokeWidth(2);
        mHighLightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mHighLightPaint.setColor(0x66157BC3);
        //        mHighLightPaint.setPathEffect(new DashPathEffect(new float[]{dp2px(2), dp2px(2)}, 0));

        mStrokePaint = new Paint();
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(Color.BLACK);
        mStrokePaint.setStrokeWidth(1);
        mStrokePaint.setAntiAlias(true);

        mAngle = new ArrayList<>();
    }

    public void setAngle(List<Integer> angle) {
        if (angle == null) {
            return;
        }
        mAngle.clear();
        mAngle.addAll(angle);
        invalidate();
    }

    public void setAngle(int[] angle) {
        if (angle.length == 0) {
            return;
        }
        mAngle.clear();
        for (int i : angle) {
            if (!mAngle.contains(i)) {
                mAngle.add(i);
            }
        }
        invalidate();
    }


    public void setRadius(int radius) {
        if (mCverlayRadius != radius) {
            mCverlayRadius = radius;
            invalidate();
        }
    }

    public void setSelected(boolean isSelect) {
        if (mIsSelected != isSelect) {
            mIsSelected = isSelect;
            if (mIsSelected) {
                mCverlayRadius = DensityUtil.dip2px(getContext(), 20);
            }
            invalidate();
        }
    }

    public void setHighLight(boolean isHighLight) {
        if (mIshighlight != isHighLight) {
            mIshighlight = isHighLight;
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

        RectF r = new RectF(2, 2, mCverlayRadius * 2 - 2, mCverlayRadius * 2 - 2);

        if (mIsSelected) {
            canvas.drawCircle(r.centerX(), r.centerY(), mCverlayRadius, mSelectPaint);
        }

        if (mAngle.isEmpty()){
            mAngle.add(30);
            mAngle.add(90);
        }

        for (int i = 0; i < mAngle.size(); i++) {
            float startAngle = mAngle.get(i) - 90 - 30;
            mPaint.setColor(mColorSets[i % mColorSets.length]);
            canvas.drawArc(r, startAngle, 60, true, mPaint);
            canvas.drawArc(r, startAngle, 60, true, mStrokePaint);
        }

//        if (mAngle.isEmpty()) {
//            Rect r2 = new Rect(2, 2, mCverlayRadius * 2 - 2, mCverlayRadius * 2 - 2);
//            //            canvas.drawBitmap(mBitmap, r2, r2, mPaint);
//            mPaint.setColor(Color.BLUE);
//            canvas.drawCircle(r2.centerX(), r2.centerY(), dp2px(8), mPaint);
//        }


        if (mIshighlight) {
            mCverlayRadius = dp2px(12);
            canvas.drawCircle(r.centerX(), r.centerY(), mCverlayRadius, mHighLightPaint);
        }
    }

    private int dp2px(float dp) {
        return DensityUtil.dip2px(getContext(), dp);
    }
}
