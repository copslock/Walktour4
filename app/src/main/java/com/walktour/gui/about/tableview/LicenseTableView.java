package com.walktour.gui.about.tableview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.TextView;

import com.walktour.gui.R;

/**
 * 自定义表格
 */
public class LicenseTableView extends TextView {
    Paint paint = new Paint();
    @RequiresApi(api = Build.VERSION_CODES.M)
    public LicenseTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        int color = context.getColor(R.color.app_main_color);
        paint.setColor(color);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0.5f, 0.5f, this.getWidth() -0.5f, 0.5f, paint);
        canvas.drawLine(0.5f, 0.5f, 0.5f, this.getHeight() -0.5f, paint);
        canvas.drawLine(this.getWidth() -0.5f, 0.5f, this.getWidth() -0.5f, this.getHeight() -0.5f, paint);
        canvas.drawLine(0.5f, this.getHeight() -0.5f, this.getWidth() -0.5f, this.getHeight() -0.5f, paint);
    }
}