package com.walktour.mapextention.tab;

import java.util.LinkedHashMap;

public class TabData {
	public String filename;
	public LinkedHashMap<GPSData,CoordData> points = new LinkedHashMap<GPSData,CoordData>();
	
	public TabData()
	{
		
	}

}
