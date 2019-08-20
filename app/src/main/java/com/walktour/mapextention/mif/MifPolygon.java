package com.walktour.mapextention.mif;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Mif Polygon类
 * Author:Zhengmin
 * Create Time:2010/4/16
 */
public class MifPolygon {
	public int numpts;
	public Queue<MifCenter> points = new LinkedBlockingQueue<MifCenter>();
}
