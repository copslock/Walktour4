package com.walktour.mapextention.mif;

/**
 * Mif Point类
 * Author:Zhengmin
 * Create Time:2010/4/16
 */
public class MifPoint extends GraphicObj{
	public float x;
	public float y;
	public MifSymbol symbol;
	public MifPoint()
	{
		this.type = MifConfig.point;
		//symbol = new Mif_Symbol();
	}

}
