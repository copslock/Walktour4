/*
 * 文件名: ScaleBarDrawable.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 标尺控件
 * 创建人: 黄广府
 * 创建时间:2012-8-7
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.gui.map.googlemap.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.walktour.gui.map.googlemap.view.MapView;

/**
 * 地图UI标尺自定义Drawble<BR>
 * 控制标示图片的重绘，展现
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-8-7]
 */
public class ScaleBarDrawable extends Drawable {
    
    private Paint mPaint = new Paint();
    
    private Paint mPaint2 = new Paint();
    
    private Paint mPaint3 = new Paint();
    
    private Paint mPaint4 = new Paint();
    
    private MapView mOsmv;
    
    private int mZoomLevel = -1;
    
    private double mTouchScale = 1;
    
    private String mDist = "";
    
    private int mWidth = 100;
    
    private int mUnits;
    
    private int mWidth2 = 100;
    
    private static final int SCALE[][] = {
            { 25000000, 15000000, 8000000, 4000000, 2000000, 1000000, 500000,
                    250000, 100000, 50000, 25000, 15000, 8000, 4000, 2000,
                    1000, 500, 250, 100, 50 },
            { 15000, 8000, 4000, 2000, 1000, 500, 250, 100, 50, 25, 15, 8, 4,
                    2, 1, 3000, 1500, 500, 250, 100 } };
    
    private static int EQUATOR_M = 40075676;
    
    private static int EQUATOR_ML = 24902;
    
    private static int EQUATOR_FT = 131481877;
    
    public ScaleBarDrawable(Context ctx, MapView osmv, int units) {
        mOsmv = osmv;
        mUnits = units;
        
        mPaint.setColor(ctx.getResources().getColor(android.R.color.black));
        mPaint2.setColor(ctx.getResources().getColor(android.R.color.white));
        mPaint3.setColor(ctx.getResources().getColor(android.R.color.black));
        mPaint3.setAntiAlias(true);
        mPaint3.setTextSize(15);
        mPaint4.setColor(ctx.getResources().getColor(android.R.color.white));
        mPaint4.setAntiAlias(true);
        mPaint4.setTextSize(15);
    }
    
    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
    }
    
    @Override
    public void draw(Canvas canvas) {
        final int h = 20, h2 = 13, margin = 7;
        
        if (mZoomLevel != mOsmv.getZoomLevel()
                || mTouchScale != mOsmv.getTouchScale()) {
            mZoomLevel = mOsmv.getZoomLevel();
            mTouchScale = mOsmv.getTouchScale();
            
            final int dist = SCALE[mUnits][Math.max(0, Math.min(19, mZoomLevel
                    + 1
                    + (int) (mTouchScale > 1 ? Math.round(mTouchScale) - 1
                            : -Math.round(1 / mTouchScale) + 1)))];
            if (mUnits == 0) {
                mWidth = (int) ((double) dist * mTouchScale * 256
                        * (1 << (mZoomLevel + 1)) / EQUATOR_M);
                
                if (dist > 999)
                    mDist = "" + (dist / 1000) + " km";
                else
                    mDist = "" + dist + " m";
            } else if (mZoomLevel < 15) {
                mWidth = (int) ((double) dist * mTouchScale * 256 * 2
                        * (1 << (mZoomLevel + 1)) / EQUATOR_ML);
                mDist = "" + dist + " ml";
            } else {
                mWidth = (int) ((double) dist * mTouchScale * 256 * 2
                        * (1 << (mZoomLevel + 1)) / EQUATOR_FT);
                mDist = "" + dist + " ft";
            }
            
            mWidth2 = (int) mWidth / 2;
        }
        //mWidth change mWidth2
        canvas.drawRect(margin + 0, 17, margin + mWidth2 + 2, 20, mPaint2);
        canvas.drawRect(margin + 0, 7, margin + 4, h, mPaint2);
/*        canvas.drawRect(margin + mWidth + 2 - 4,
                7,
                margin + mWidth + 2,
                h,
                mPaint2);*/
        canvas.drawRect(margin + mWidth2 + 2 - 4,
                7,
                margin + mWidth2 + 2,
                h2 + 2,
                mPaint2);
        //mWidth change mWidth2
        canvas.drawRect(margin + 1, 18, margin + mWidth2 + 1, 20, mPaint);
        canvas.drawRect(margin + 1, 7, margin + 3, h - 1, mPaint);
/*        canvas.drawRect(margin + mWidth + 1 - 2,
                7,
                margin + mWidth + 1,
                h - 1,
                mPaint);*/
        canvas.drawRect(margin + mWidth2 + 1 - 2,
                8,
                margin + mWidth2 + 1,
                h - 2,
                mPaint);
        
        canvas.drawText(mDist,
                margin + 7 - 1,
                mPaint3.getTextSize() - 1,
                mPaint4);
        canvas.drawText(mDist,
                margin + 7 + 1,
                mPaint3.getTextSize() + 1,
                mPaint4);
        canvas.drawText(mDist, margin + 7, mPaint3.getTextSize(), mPaint3);
    }
    
    @Override
    public int getIntrinsicWidth() {
        return 350; 
    }
    
    @Override
    public int getIntrinsicHeight() {
        return 22;
    }
    
    @Override
    public int getOpacity() {
        return 0;
    }
    
    @Override
    public void setAlpha(int alpha) {
        
    }
    
    @Override
    public void setColorFilter(ColorFilter cf) {
        
    }
}
