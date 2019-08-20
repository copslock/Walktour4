package com.walktour.mapextention.mif;

/**
 * Mif line类
 * Author:Zhengmin
 * Create Time:2010/4/16
 */
public class MifLine extends GraphicObj{
	
	public float x1;
	public float y1;
	public float x2;
	public float y2;
	public MifPen pen;
	public MifLine()
	{
		this.type = MifConfig.line;
		//pen = new Mif_Pen();
	}

}
