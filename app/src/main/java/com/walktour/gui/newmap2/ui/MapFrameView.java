package com.walktour.gui.newmap2.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.walktour.Utils.DensityUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.SafeHandler;
import com.walktour.base.util.ScreenUtils;

/**
 * 地图框选
 *
 * @author zhicheng.chen
 * @date 2018/11/18
 */
public class MapFrameView extends View implements View.OnTouchListener {

    public static final String ACTION_MAP_FINISH = "ACTION_MAP_FINISH";

    private static final int LEFT_TOP = 1;
    private static final int RIGHT_TOP = 2;
    private static final int LEFT_BOTTOM = 3;
    private static final int RIGHT_BOTTOM = 4;

    //点击中间是移动
    private static final int CENTER = 5;
    private int mDirection = CENTER;
    private boolean mIsInit = true;

    //是否是第一次移动框选框
    private boolean mIsFirstMove = true;

    private Paint mPaint;
    private Paint mBkgPaint;
    private Paint mLinePaint;
    private TextPaint mTxtPaint;

    private float lastX, lastY;
    private int width;
    private int height;
    private int left;
    private int right;
    private int bottom;
    private int top;

    private int maxBottom;
    //全局定义
    private long lastClickTime = 0L;
    // 两次点击间隔不能少于1000ms
    private static final int FAST_CLICK_DELAY_TIME = 1000;


    public MapFrameView(Context context) {
        super(context);
        init();
    }

    public MapFrameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MapFrameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mBkgPaint = new Paint();
        mBkgPaint.setColor(Color.parseColor("#223F51B5"));

        mPaint = new Paint();
        mPaint.setStrokeWidth(4);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLUE);
        mPaint.setPathEffect(new DashPathEffect(new float[]{dp2px(5), dp2px(3)}, 0));

        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(22);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(Color.BLUE);

        mTxtPaint = new TextPaint();
        mTxtPaint.setAntiAlias(true);
        mTxtPaint.setColor(Color.GRAY);
        mTxtPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTxtPaint.setTextSize(DensityUtil.dip2px(getContext(), 16));

        setOnTouchListener(this);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        if (mIsInit) {
            super.layout(l, t, r, b);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int offset = 4;
        canvas.drawRect(offset, offset, getWidth() - offset, getHeight() - offset, mBkgPaint);
        canvas.drawRect(offset, offset, getWidth() - offset, getHeight() - offset, mPaint);

        int length = dp2px(20);
        canvas.drawLine(0, 0, length, 0, mLinePaint);
        canvas.drawLine(0, 0, 0, length, mLinePaint);

        canvas.drawLine(0, getHeight() - length, 0, getHeight(), mLinePaint);
        canvas.drawLine(0, getHeight(), length, getHeight(), mLinePaint);

        canvas.drawLine(getWidth() - length, 0, getWidth(), 0, mLinePaint);
        canvas.drawLine(getWidth(), 0, getWidth(), length, mLinePaint);

        canvas.drawLine(getWidth(), getHeight() - length, getWidth(), getHeight(), mLinePaint);
        canvas.drawLine(getWidth() - length, getHeight(), getWidth(), getHeight(), mLinePaint);

        String tips = "拖动可筛选基站";
        Rect rect = new Rect();
        mTxtPaint.getTextBounds(tips, 0, tips.length(), rect);
        if (mIsInit) {
            canvas.drawText(tips, (DensityUtil.dip2px(getContext(), 180) - rect.width()) / 2, (DensityUtil.dip2px(getContext(), 100)) / 2, mTxtPaint);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getRawX();
                lastY = event.getRawY();
                width = v.getWidth();
                height = v.getHeight();

                left = v.getLeft();
                right = v.getRight();
                top = v.getTop();
                bottom = v.getBottom();
                mDirection = getDirection(event.getX(), event.getY());

                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getRawX();
                float y = event.getRawY();

                int dx = (int) (x - lastX);
                int dy = (int) (y - lastY);

                //update view position
                updateLayout(mDirection, v, dx, dy);
                lastX = event.getRawX();
                lastY = event.getRawY();

                if (mIsFirstMove) {
                    mIsFirstMove = false;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                mDirection = CENTER;
                invokeCallback();
                break;
        }
        return true;
    }

    private int getDirection(float downX, float downY) {
        int r = dp2px(30); //四个角的空间
        if (downX < r && downY < r) {
            return LEFT_TOP;
        } else if (downX > getWidth() - r && downY < r) {
            return RIGHT_TOP;
        } else if (downY > getHeight() - r && downX < r) {
            return LEFT_BOTTOM;
        } else if (downX > getWidth() - r && downY > getHeight() - r) {
            return RIGHT_BOTTOM;
        }
        return CENTER;
    }

    public void setMaxBottom(int maxBottom) {
        this.maxBottom = maxBottom;
        if (this.maxBottom == 0) {
            this.maxBottom = ScreenUtils.getScreenHeight(getContext()) - dp2px(160);
        }
    }

    private void updateLayout(int direction, View v, int dx, int dy) {

        int minWH = dp2px(100);//最小宽高
        int maxW = ScreenUtils.getScreenWidth(getContext());
        int maxH = maxBottom;

        if (direction == LEFT_TOP) {
            left += dx;
            top += dy;
            left = Math.min(Math.max(left, 0), maxW - width);
            top = Math.min(Math.max(top, 0), maxH - height);

            if (right - left < minWH) {
                left = right - minWH;
            }

            if (bottom - top < minWH) {
                top = bottom - minWH;
            }
        } else if (direction == RIGHT_TOP) {
            right += dx;
            top += dy;
            right = Math.min(Math.max(right, left + minWH), maxW);
            top = Math.min(Math.max(top, 0), maxH - height);

            if (bottom - top < minWH) {
                top = bottom - minWH;
            }
        } else if (direction == LEFT_BOTTOM) {
            left += dx;
            bottom += dy;
            left = Math.min(Math.max(left, 0), maxW - width);
            if (right - left < minWH) {
                left = right - minWH;
            }
            bottom = Math.min(Math.max(top + minWH, bottom), maxH);
        } else if (direction == RIGHT_BOTTOM) {
            right += dx;
            bottom += dy;
            right = Math.min(Math.max(left + minWH, right), maxW);
            bottom = Math.min(Math.max(top + minWH, bottom), maxH);
        } else if (direction == CENTER) {
            left += dx;
            top += dy;
            left = Math.min(Math.max(left, 0), maxW - width);
            top = Math.min(Math.max(top, 0), maxH - height);
            right = left + v.getWidth();
            bottom = top + v.getHeight();
        }
        mIsInit = false;
        super.layout(left, top, right, bottom);
    }

    private int dp2px(float dp) {
        return DensityUtil.dip2px(getContext(), dp);
    }


    private MapFrameListener mapFrameListener;

    public void setMapFrameListener(MapFrameListener listener) {
        this.mapFrameListener = listener;
    }

    public interface MapFrameListener {
        /**
         * view 隐藏的时候执行
         *
         * @param lt
         * @param rb
         */
        void doSthMapFrameViewGone(Point lt, Point rb);

        /**
         * view 拖拽完成之后执行
         *
         * @param lt
         * @param rb
         */
        void afterMapFrameViewDrag(Point lt, Point rb);
    }

    // 回调接口
    private void invokeCallback() {
        if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME) {
            return;
        }
        lastClickTime = System.currentTimeMillis();
        if (mapFrameListener != null && getVisibility() == View.VISIBLE) {
            Point lt = new Point(getLeft(), getTop());
            Point rb = new Point(getRight(), getBottom());
            mapFrameListener.afterMapFrameViewDrag(lt, rb);
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (visibility == View.GONE) {
            if (mapFrameListener != null) {
                Point lt = new Point(getLeft(), getTop());
                Point rb = new Point(getRight(), getBottom());
                mapFrameListener.doSthMapFrameViewGone(lt, rb);
            }
        }
    }
}
