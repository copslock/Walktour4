package com.walktour.mapextention.ibwave;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import com.jhlabs.map.java.Point2D;
import com.jhlabs.map.proj.Projection;


public class DrawerSite extends Drawer{
	
	private Paint mPaint;

	public DrawerSite(){
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(16);
		mPaint.setColor(0xFF000000);
	}
	
	public void Draw(Projection aProj, TabMap aMap, Canvas canvas, ViewPort aViewPort)
	{
		if (!(aMap instanceof SiteMap))
		{
			return;
		}
		
		SiteMap map = (SiteMap)aMap;
		
		CellDisplayStyle cds = map.getCellDisplayStyle();
		
		Site aSite;
		Point2D.Double src = new Point2D.Double();
		Point2D.Double dst = new Point2D.Double();
		PointF vp = new PointF();
		
		float fAntLength;
		boolean bDrawed;
		
		for(int i = 0; i < map.mSites.size(); i++){
			aSite = map.mSites.get(i);
			
			src.x = aSite.mLongitude;
			src.y = aSite.mLatitude;
			aProj.transform(src, dst);
			aViewPort.DPToVP(dst, vp);
			
			fAntLength = map.GetAntLength();
			bDrawed = false;
			
			switch (cds){
				case CILine:{
					
				}
				case CISimple:{
					
				}
				case CIPie:{
					bDrawed = DrawCellPie(aViewPort, canvas, aSite, fAntLength, vp);
				}
				case CIComplex:{
					
				}
			}
			
			if (bDrawed && map.GetDisplayLabel()){
				DrawLabel(aViewPort, canvas, map, aSite, fAntLength, vp);
			}
			
			//canvas.drawRect(vp.x - 10, vp.y - 10, vp.x + 10, vp.y + 10, mPaint);
		}
	}
	
	private boolean DrawCellPie(ViewPort aViewPort, Canvas canvas, Site aSite, float fAntLength, PointF vp){
		RectF oval = new RectF(vp.x - fAntLength, vp.y - fAntLength, vp.x + fAntLength, vp.y + fAntLength);
		if (!RectF.intersects(aViewPort.getClientRect(), oval)){
			return false;
		}
		
		Cell aCell;
		float fAzimuth;
		float sweepAngle;
		float startAngle;
		for(int i = 0; i < aSite.mCells.size(); i++){
			aCell = aSite.mCells.get(i);
			
			sweepAngle = (float)aCell.mBeamwidth;
			if(sweepAngle == -9999){
				sweepAngle = 60;
			}
			
			fAzimuth = (float)aCell.mAzimuth;
			fAzimuth = fAzimuth - 90;
			while(fAzimuth < 0){
				fAzimuth += 360;
			}
			while(fAzimuth > 360){
				fAzimuth -= 360;
			}	
			
			startAngle = fAzimuth - sweepAngle / 2;
			canvas.drawArc(oval, startAngle, sweepAngle, true, mPaint);
		}
		return true;
	}

	private void DrawLabel(ViewPort aViewPort, Canvas canvas, SiteMap aMap, Site aSite, float fAntLength, PointF vp){
		Cell aCell;
		float fAzimuth;
		float rad;
		String sLabel;
		RectF fbounds = new RectF();
	
		for(int i = 0; i < aSite.mCells.size(); i++){
			aCell = aSite.mCells.get(i);
			sLabel = aMap.GetCellLabel(aCell);
			
			fAzimuth = (float)aCell.mAzimuth;
			fAzimuth = 90 - fAzimuth;
			while(fAzimuth < 0){
				fAzimuth += 360;
			}
			while(fAzimuth > 360){
				fAzimuth -= 360;
			}
			PointF fTip = new PointF();
			
			rad = (float) Math.toRadians(fAzimuth);
			fTip.x = vp.x + fAntLength * (float)Math.cos(rad);
			fTip.y = vp.y - fAntLength * (float)Math.sin(rad);

			float fw = mPaint.measureText(sLabel);
			float fh = 16;
			
			if(fAzimuth >= 0 && fAzimuth < 90){
				
			}
			else{
				if(fAzimuth >= 90 && fAzimuth < 180){
					fTip.x -= fw;
				}
				else{
					if(fAzimuth >= 180 && fAzimuth < 270){
						fTip.x -= fw;
						fTip.y += fh;
					}
					else{
						fTip.y += fh;
					}
				}
			}
			
			fbounds.set(fTip.x, fTip.y - fh, fTip.x + fw, fTip.y);
			aViewPort.mLabelTree.query(aViewPort.mResults, fbounds);
			if(aViewPort.mResults.size() == 0){
				BoundedObject bo = new BoundedObject(fbounds);
				aViewPort.mLabelTree.insert(bo);
				canvas.drawText(sLabel, fTip.x, fTip.y, mPaint);
			}
			aViewPort.mResults.clear();
		}
	}
}
