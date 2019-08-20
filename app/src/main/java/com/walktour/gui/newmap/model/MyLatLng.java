package com.walktour.gui.newmap.model;

/**
 * 系统使用的经纬度对象
 * 
 * @author jianchao.wang
 * 
 */
public class MyLatLng {
	/** 纬度 */
	public double latitude;
	/** 经度 */
	public double longitude;

	public MyLatLng(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public MyLatLng(double[] latlng) {
		this.latitude = latlng[0];
		this.longitude = latlng[1];
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof MyLatLng) {
			MyLatLng latlng = (MyLatLng) obj;
			if (latlng.latitude == this.latitude && latlng.longitude == this.longitude)
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("latitude:").append(this.latitude).append("longitude:").append(this.longitude);
		return sb.toString();
	}

}
