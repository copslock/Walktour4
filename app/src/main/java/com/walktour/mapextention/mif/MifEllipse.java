package com.walktour.mapextention.mif;

/**
 * Mif Eclipse类
 * Author:Zhengmin
 * Create Time:2010/4/16
 */
public class MifEllipse extends GraphicObj{
	
	public float x1;
	public float y1;
	public float x2;
	public float y2;
	public  MifPen pen;
	public MifBrush brush;
	public MifEllipse()
	{
		this.type = MifConfig.ellipse;
		//pen = new Mif_Pen();
		//brush = new Mif_Brush();
	}

}
