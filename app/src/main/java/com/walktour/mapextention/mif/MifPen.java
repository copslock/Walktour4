package com.walktour.mapextention.mif;

/**
 * Mif pen类
 * Author:Zhengmin
 * Create Time:2010/4/16
 */
public class MifPen extends GraphicObj{
	public int width;
	public int pattern;
	public int color;
	public MifPen()
	{
		this.type = MifConfig.pen;
	}
}
