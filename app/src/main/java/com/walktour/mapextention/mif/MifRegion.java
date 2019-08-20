package com.walktour.mapextention.mif;


/**
 * Mif Region类
 * Author:Zhengmin
 * Create Time:2010/4/16
 */
public class MifRegion extends GraphicObj{
	/*public int numploygons;
	//public Queue<Mif_Polygon> polygons = new LinkedBlockingQueue<Mif_Polygon>();
	public int numpts;
	public Queue<Mif_Center> points = new LinkedBlockingQueue<Mif_Center>();
	public Mif_Pen pen ;
	public Mif_Brush brush ;
	public Mif_Center center;*/
	public MifRegion()
	{
		this.type = MifConfig.region;
		//pen = new Mif_Pen();
		//brush = new Mif_Brush();
		//center = new Mif_Center();
	}
}
