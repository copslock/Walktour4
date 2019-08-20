package com.walktour.framework.database.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 基站信息主表
 * 
 * @author jianchao.wang
 * 
 */
public class BaseStation implements Serializable, Cloneable {
	/**	 */
	private static final long serialVersionUID = 1L;
	/** 网络类型：GSM */
	public final static int NETTYPE_GSM = 1;
	/** 网络类型：WCDMA */
	public final static int NETTYPE_WCDMA = 2;
	/** 网络类型：CDMA */
	public final static int NETTYPE_CDMA = 3;
	/** 网络类型：TDSCDMA */
	public final static int NETTYPE_TDSCDMA = 4;
	/** 网络类型：LTE */
	public final static int NETTYPE_LTE = 5;
	/** 网络类型：NB-IOT */
	public final static int NETTYPE_NBIOT = 6;
	/** 网络类型：CatM */
	public final static int NETTYPE_CAT_M = 7;
	/** 网络类型：ENDC */
	public final static int NETTYPE_ENDC = 8;
	/** 地图类型：室外地图 */
	public final static int MAPTYPE_OUTDOOR = 0;
	/** 地图类型：室内地图 */
	public final static int MAPTYPE_INDOOR = 1;
	/** 数据表ID */
	public long id;
	/** 主表的Id */
	public long mainId;
	/** 原始经度 */
	public double longitude;
	/** 原始纬度 */
	public double latitude;
	/** 百度经度 */
	public double baiduLongitude;
	/** 百度纬度 */
	public double baiduLatitude;
	/** google经度 */
	public double googleLongitude;
	/** google纬度 */
	public double googleLatitude;
	/** 基站名称 */
	public String name = "";
	/** LTE */
	public String enodebId = "";
	/** 网络类型 */
	public int netType = NETTYPE_GSM;
	/** 当前的数据索引，用于显示明细使用 */
	public int detailIndex = -1;
	/** 地图类型 */
	public int mapType = MAPTYPE_OUTDOOR;
	/** 明细列表 */
	public List<BaseStationDetail> details = new ArrayList<BaseStationDetail>();

	@Override
	public BaseStation clone() {
		BaseStation o = null;
		try {
			o = (BaseStation) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * 获取指定属性名的值
	 * 
	 * @param paramName
	 *          属性名
	 * @return
	 */
	public Object getParamValue(String paramName) {
		if ("siteName".equals(paramName))
			return this.name;
		else if ("longitude".equals(paramName))
			return this.longitude;
		else if ("latitude".equals(paramName))
			return this.latitude;
		else if ("enodeb_id".equals(paramName))
			return this.enodebId;
		else
			return null;
	}

	@Override
	public boolean equals(Object o) {
		if (super.equals(o))
			return true;
		if (o == null || !(o instanceof BaseStation))
			return false;
		BaseStation station = (BaseStation) o;
		return station.id == this.id;
	}

	/**
	 * 获取基站的所有方向角
	 * 
	 * @return
	 */
	public int[] getBearings() {
		int[] bearings = new int[this.details.size()];
		for (int i = 0; i < this.details.size(); i++) {
			BaseStationDetail detail = this.details.get(i);
			bearings[i] = detail.bearing;
		}
		return bearings;
	}

	@Override
	public String toString() {
		return "BaseStation{" +
				"id=" + id +
				", mainId=" + mainId +
				", longitude=" + longitude +
				", latitude=" + latitude +
				", baiduLongitude=" + baiduLongitude +
				", baiduLatitude=" + baiduLatitude +
				", googleLongitude=" + googleLongitude +
				", googleLatitude=" + googleLatitude +
				", name='" + name + '\'' +
				", enodebId='" + enodebId + '\'' +
				", netType=" + netType +
				", detailIndex=" + detailIndex +
				", mapType=" + mapType +
				", details=" + details +
				'}';
	}
}