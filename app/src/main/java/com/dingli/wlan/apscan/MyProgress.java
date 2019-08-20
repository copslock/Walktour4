package com.dingli.wlan.apscan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;



public class MyProgress extends ProgressBar{
    String text;
    Paint mPaint;
    Paint npaint;
     
    public MyProgress(Context context) {
        super(context);
        initText(); 
    }
     
    public MyProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initText();
    }
 
 
    public MyProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        initText();
    }
     
    @Override
    public synchronized void setProgress(int progress) {
        setText(progress);
        super.setProgress(progress);
         
    }
 
    @SuppressLint("DrawAllocation")
		@Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //this.setText();
        // 给ProgressBar描边，否则进度条和背景色都是黑色的，没有边界了，郑磊修改于2012年3月26日
//        RectF localRectF = new RectF(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
//        canvas.drawRect(localRectF, npaint);
        Rect rect = new Rect();
        this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
        int x = (getWidth() / 2) - rect.centerX();  
        int y = (getHeight() / 2) - rect.centerY();  
        canvas.drawText(this.text, x, y, this.mPaint);  
    }
     
    //初始化，画笔
    private void initText(){
        this.mPaint = new Paint();
        this.npaint = new Paint();
        // 抗锯齿，郑磊修改于2012年3月26日
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(Color.WHITE);
        this.mPaint.setTextSize(24);
        
        this.npaint.setAntiAlias(true);
        this.npaint.setColor(Color.WHITE);
        // 画空心的
//        this.npaint.setStyle(Style.STROKE);
        
    }
     
//    private void setText(){
//        setText(this.getProgress());
//    }
     
    //设置文字内容
    private void setText(int progress){
        int i = progress - 110;
        this.text = String.valueOf(i) + "dBm";
    }
     
     
}
