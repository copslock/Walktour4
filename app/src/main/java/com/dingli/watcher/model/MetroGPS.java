package com.dingli.watcher.model;

public class MetroGPS {
	/** 经度 */
	public double Lon;
	/** 纬度 */
	public double Lat;
	/** 水平高度 */
	public float Altitude = 1.0f;
	/** 速度(km/h) */
	public float Speed;
	/**
	 * gps类型<BR>
	 * -9999:无加速度的报警信息,<BR>
	 * -1:不输出GPS, <BR>
	 * 0:高铁为补点GPS\地铁为两站之间打点GPS, <BR>
	 * 1..n:原始点GPS[地铁:对应站点索引号=n-1, 高铁:固定为1]
	 */
	public int Atype;

	@Override
	public String toString() {
		return "MetroGPS{" +
				"Lon=" + Lon +
				", Lat=" + Lat +
				", Altitude=" + Altitude +
				", Speed=" + Speed +
				", Atype=" + Atype +
				'}';
	}
}
