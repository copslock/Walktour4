package com.walktour.mapextention.mif;

/**
 * Mif brush类
 * Author:Zhengmin
 * Create Time:2010/4/16
 */
public class MifBrush extends GraphicObj{
	public int pattern;
	public int forecolor;
	public int backcolor;
	public MifBrush()
	{
		this.type = MifConfig.brush;
	}
}
