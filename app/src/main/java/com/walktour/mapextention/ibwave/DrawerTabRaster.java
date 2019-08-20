package com.walktour.mapextention.ibwave;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.jhlabs.map.java.Point2D;
import com.jhlabs.map.java.Rectangle2D;
import com.jhlabs.map.proj.Projection;

public class DrawerTabRaster extends Drawer{
	private Paint mPaint;
	public DrawerTabRaster()
	{
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(16);
		mPaint.setColor(0xFF000000);
	}
	
	public void Draw(Projection aProj, TabMap aMap, Canvas canvas, ViewPort aViewPort)
	{
		if (!(aMap instanceof TabMapRaster))
		{
			return;
		}
		
		TabMapRaster map = (TabMapRaster)aMap;
		Rectangle2D.Double aDataScope = map.getDataScope();
		if(aDataScope.width == 0 || aDataScope.height == 0)
		{
			return;
		}
		
		Point2D.Double dp = new Point2D.Double(aDataScope.x, aDataScope.y);
		PointF pt1 = new PointF();
		aViewPort.DPToVP(dp, pt1);
		dp.x += aDataScope.width;
		dp.y -= aDataScope.height;
		PointF pt2 = new PointF();
		aViewPort.DPToVP(dp, pt2);
		Rect src = new Rect(0, 0, map.getWidth(), map.getHeight());
		RectF dst = new RectF(pt1.x, pt1.y, pt2.x, pt2.y);
		canvas.drawBitmap(map.getDrawData(), src, dst, mPaint);
	}
}
