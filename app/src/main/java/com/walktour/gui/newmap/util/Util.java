package com.walktour.gui.newmap.util;

import org.andnav.osm.util.GeoPoint;

/**
 * 地图工具类
 * 
 * @author jianchao.wang
 * 
 */
public class Util {
	/**
	 * 格式化经纬度显示
	 * 
	 * @param point
	 * @return
	 */
	public static String formatGeoPoint(GeoPoint point) {
		String lon = point.getLongitude() > 0 ? "\u00B0E" : "\u00B0W";
		String lat = point.getLatitude() > 0 ? "\u00B0N" : "\u00B0S";
		return point.getLongitude() + lon + "," + point.getLatitude() + lat;
	}

}
