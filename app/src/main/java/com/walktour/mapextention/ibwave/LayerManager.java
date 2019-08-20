package com.walktour.mapextention.ibwave;

import android.graphics.Canvas;

import com.jhlabs.map.java.Rectangle2D;
import com.jhlabs.map.proj.Projection;

public class LayerManager {
	private java.util.List<TabMap> mLayers = new java.util.ArrayList<TabMap>();
//	private Bitmap mBmp = Bitmap.createBitmap(0, 0, Config.ARGB_8888);
	
	private static LayerManager mLayerManager = null;
	
	public static LayerManager getInstance(){
	    if(mLayerManager == null){
	        mLayerManager = new LayerManager();
	    }
	    return mLayerManager;
	}
	public LayerManager()
	{

	}
	
	public void AddLayer(TabMap aMap)
	{
		mLayers.add(aMap);
	}
	
	public void cleanLayer()
	{
		mLayers.clear();
	}
	
	public void RemoveLayer(TabMap aMap){
		mLayers.remove(aMap);
	}
	
	public int getLayerCount()
	{
		return mLayers.size();
	}
	
	public TabMap getLayer(int idx){
		return mLayers.get(idx);
	}
	
	public TabMap getLayer(String sFileName){
		TabMap aMap;
		for(int i = 0; i < mLayers.size(); i++)
		{
			aMap = mLayers.get(i);
			String s = aMap.getLayerName();
			if (s.equalsIgnoreCase(sFileName))
			{
				return aMap;
			}
		}
		return null;
	}
	
	public void Draw(Canvas canvas, Projection aProj, ViewPort aViewPort)
	{
		TabMap aMap;
		aViewPort.BeforeDraw();
		for (int i = 0; i < mLayers.size(); i++)
		{
			aMap = mLayers.get(i);
			aMap.getmDrawer().Draw(aProj, aMap, canvas, aViewPort);
		}
		aViewPort.AfterDraw();
	}
	
	public Rectangle2D.Double getDataScope()
	{
		Rectangle2D.Double ds = new Rectangle2D.Double(0, 0, 0, 0);
		Rectangle2D.Double dsLayer;
		
		TabMap aMap;
		for (int i = 0; i < mLayers.size(); i++)
		{
			aMap = mLayers.get(i);
			dsLayer = aMap.getDataScope();
			if (ds.width == 0 || ds.height == 0)
			{
				ds.setRect(dsLayer);
			}
			else
			{
				Rectangle2D.union(ds, dsLayer, ds);
			}
		}
		
		return ds;
	}
}
