package com.walktour.mapextention.mif;

/**
 * Mif RoundRect类
 * Author:Zhengmin
 * Create Time:2010/4/16
 */
public class MifRoundRect extends GraphicObj{
	
	public float x1;
	public float y1;
	public float x2;
	public float y2;
	public float a;
	public MifPen pen;
	public MifBrush brush;
	public MifRoundRect()
	{
		this.type = MifConfig.roundrect;
		//pen = new Mif_Pen();
		//brush = new Mif_Brush();
	}

}
