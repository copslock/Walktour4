package com.walktour.mapextention.mif;


import java.util.Vector;

/**
 * Mif Points类
 * Author:Zhengmin
 * Create Time:2010/4/16
 */
public class MifPoints extends GraphicObj{
	public Vector<MifCenter>  points;
	public MifPoints()
	{
		this.type = MifConfig.points;
		points = new Vector<MifCenter>();
	}

}
