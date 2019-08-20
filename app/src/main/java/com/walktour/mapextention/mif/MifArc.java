package com.walktour.mapextention.mif;

/**
 * Mif Arc类
 * Author:Zhengmin
 * Create Time:2010/4/16
 */
public class MifArc extends GraphicObj{
	
	public float x1;
	public float y1;
	public float x2;
	public float y2;
	public float a;
	public float b;
	public MifPen pen;
	public MifArc()
	{
		this.type = MifConfig.arc;
		//pen = new Mif_Pen();
	}

}
