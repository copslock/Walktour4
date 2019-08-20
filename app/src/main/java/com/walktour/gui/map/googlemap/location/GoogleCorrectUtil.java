package com.walktour.gui.map.googlemap.location;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.andnav.osm.util.GeoPoint;

/**
 * 地理位置偏移纠正<BR>
 * [功能详细描述]
 * 
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-8-13]
 */
public class GoogleCorrectUtil {

	/**
	 * 缩放级别
	 */
	private static int zoom = 18;

	private static String[] offsetinfo = new String[] { "longitude", "latitude", "offsetx", "offsety" };

	/**
	 * 调整后的经纬度
	 * 
	 * @param lat
	 * @param lng
	 * @return
	 */
	public static double[] adjustLatLng(Context context, double lat, double lng) {
		double[] adjustedLatLng = new double[2];
		try {
			double[] offset = getXYOffset(context, (int) (lat * 100), (int) (lng * 100));
			double latPixel = Math.round(latToPixel(lat, zoom));
			double lngPixel = Math.round(lngToPixel(lng, zoom));
			latPixel += offset[1];
			lngPixel += offset[0];
			double adjustedLat = pixelToLat(latPixel, zoom);
			double adjustedLng = pixelToLng(lngPixel, zoom);
			adjustedLatLng[0] = adjustedLat;
			adjustedLatLng[1] = adjustedLng;
		} catch (Exception e) {
			adjustedLatLng[0] = lat;
			adjustedLatLng[1] = lng;
		}

		return adjustedLatLng;
	}

	/**
	 * 把GPS坐标转换成google地图坐标
	 * 
	 * @param context
	 *          上下文
	 * @param geoPoint
	 *          GPS坐标
	 * @return
	 */
	public static GeoPoint adjustLatLng(Context context, GeoPoint geoPoint) {
		if (geoPoint == null)
			return null;
		double[] latlng = adjustLatLng(context, geoPoint.getLatitude(), geoPoint.getLongitude());
		return GeoPoint.from2DoubleString(String.valueOf(latlng[0]), String.valueOf(latlng[1]));
	}

	/**
	 * 获取XY偏移量<BR>
	 * 通过查询内置数据库获取经纬度偏移量
	 * 
	 * @param lat
	 *          纬度
	 * @param lng
	 *          经度
	 * @return 偏移量数组
	 */
	public static double[] getXYOffset(Context context, double lat, double lng) {
		double[] offsetPix = new double[2];
		if (MapDBHelper.getInstance(context).isDatabaseExists()) {
			SQLiteDatabase db = MapDBHelper.getInstance(context).getWritableDatabase();
			Cursor cs = db.query("ChinaOffset", offsetinfo, "longitude=" + lng + " and latitude=" + lat, null, null, null,
					null);

			if (cs != null && cs.moveToFirst()) {
				offsetPix[0] = cs.getInt(2);
				offsetPix[1] = cs.getInt(3);
				cs.close();
				db.close();
				return offsetPix;
			} else {
				cs.close();
				db.close();
				return offsetPix;
			}
		} else {
			return offsetPix;
		}
	}

	/**
	 * 经度转化成像素值 <BR>
	 * 
	 * @param lng
	 * @param zoom
	 *          缩放级别
	 * @return
	 */
	public static double lngToPixel(double lng, int zoom) {

		return (lng + 180) * (256L << zoom) / 360;

	}

	/**
	 * 像素值转化为经度值
	 * 
	 * @param pixelX
	 * @param zoom
	 * @return
	 */

	public static double pixelToLng(double pixelX, int zoom) {

		return pixelX * 360 / (256L << zoom) - 180;

	}

	/**
	 * 纬度转化为像素值
	 * 
	 * @param lat
	 * @param zoom
	 * @return
	 */

	public static double latToPixel(double lat, int zoom) {

		double siny = Math.sin(lat * Math.PI / 180);

		double y = Math.log((1 + siny) / (1 - siny));

		return (128 << zoom) * (1 - y / (2 * Math.PI));

	}

	/**
	 * 像素值转化为纬度
	 * 
	 * @param pixelY
	 * @param zoom
	 * @return
	 */
	public static double pixelToLat(double pixelY, int zoom) {

		double y = 2 * Math.PI * (1 - pixelY / (128 << zoom));

		double z = Math.pow(Math.E, y);

		double siny = (z - 1) / (z + 1);

		return Math.asin(siny) * 180 / Math.PI;

	}

}
